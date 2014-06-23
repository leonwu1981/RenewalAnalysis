package com.sinosoft.lis.claimanalysis.claimratio;

import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;
import com.sinosoft.lis.claimanalysis.common.FileProcessor;

public class ClaimDataDownloadThread implements Runnable {

	int threadNum; // 线程编号
	String startDate;
	String endDate;
	String minCaseNo;
	String maxCaseNo;
	String file_path;
	
	FileProcessor FileProcessor = new FileProcessor();
	
	static int SuccThreadCount = 0;  //成功线程数
	static int FailThreadCount = 0;  //失败线程数
	static int ThreadCount = 0;  //正在执行线程数
	
	ClaimDataDownloadThread() {
	} 
	ClaimDataDownloadThread( int threadNum, String startDate, String endDate, String minCaseNo, String maxCaseNo, String path ) {
		this.threadNum = threadNum;
		this.startDate = startDate;
		this.endDate = endDate;
		this.minCaseNo = minCaseNo;
		this.maxCaseNo = maxCaseNo;
		this.file_path = path;
	} 

	public void run() {
		addThreadCount();
		
		// output claim_threadNum.csv
		if( !outputClaimData()){
			addFailThreadCount();
		} else {
			addSuccThreadCount();
		}
	}
	
	public boolean outputClaimData(){
		StringBuffer sql = new StringBuffer();
		// 1 MNGCOM 分公司
		sql.append(" select (select managecom from grpcont_view where grpcontno = y.grpcontno) managecom,    ");
		// 2 GRPCONTNO 保单号
		sql.append("        y.grpcontno,                                                                     ");
		// 3 GEB 是否为 geb客户
		sql.append("        (select gebclient from grpcont_view where grpcontno = y.grpcontno) gebclient,    ");
		// 4 CCDATE 保单生效日
		sql.append("        (select CValiDate from grpcont_view where grpcontno = y.grpcontno) CValiDate,     ");
        // 5 CRDATE 保单终止日
		sql.append("nvl((select max(payenddate) - 1 from lcgrppol where grpcontno = y.grpcontno),(select edorvalidate-1 from lpgrpedoritem where grpcontno = y.grpcontno and edortype in ('CT', 'WT') and edorstate = '0')),");
		// 6 CVALIDATE 被保险人责任生效日
		sql.append("(select min(firstpaydate) from duty_view where polno = y.polno and substr(dutycode, 0, 6) = y.dutycode) dutyCValiDate,");
		// 7 ENDDATE 被保险人责任终止日
		sql.append("nvl((select max(enddate) - 1 from lcduty where polno = y.polno and contno = y.contno and substr(dutycode, 0, 6) = y.dutycode), (select m.edorvalidate-1 from lpedoritem m,lbcont n where m.edorno=n.edorno and m.contno=n.contno and m.grpcontno = y.grpcontno and m.contno = y.contno and m.edortype in ('ZT', 'CT') and m.oldedortype is null and m.edorstate = '0')) , ");
		// 8 INCURRED 事故发生日
		sql.append("        l.accidentdate,                                                                  ");
		// 9 PAYDATE 赔付日期
		sql.append("        (SELECT MAX(ConfDate)                                                            ");
		sql.append("           FROM ljaget                                                                   ");
		sql.append("          WHERE OtherNoType = '5'                                                        ");
		sql.append("            AND otherno = l.caseno                                                       ");
		sql.append("            and strikeflag is null) ConfDate,                                            ");
		// 10 RISKCODE 险种
		sql.append("        y.riskcode,                                                                      ");
        // 11 CLAIMAMT 申请理赔金额
		sql.append("        llcase_preclaimmoney(l.caseno) preclaimmoney,                                    ");
		// 12 REALPAY 实际赔付金额
		sql.append("        (case                                                                            ");
		sql.append("          when l.rgtstate in ('09', '12') then                                           ");
		sql.append("           y.realpay                                                                     ");
		sql.append("        end) realpay,                                                                    ");
		// 13 BENCO 赔付责任
		sql.append("        y.getdutycode,                                                                   ");
		// 14 DEPT 增加业绩归属部门字段
		sql.append("        (select departmentid from grpcont_view where grpcontno = y.grpcontno) gebclient,  ");
		// 15 APPNTNAME
		sql.append("        (select grpname from grpcont_view where grpcontno = y.grpcontno) appntname    ");
		// where
		sql.append("   from llclaimdetail y, llcase l                                                        ");
		sql.append("  where y.caseno = l.caseno                                                              ");
		sql.append("    and ((l.accidentdate >= date '"+startDate+"' and l.accidentdate <= date              ");
		sql.append("         '"+endDate+"') or l.accidentdate is null)                                       ");
		sql.append("    and l.caseno > '"+minCaseNo+"'                                                       ");
		sql.append("    and l.caseno <= '"+maxCaseNo+"'                                                      ");
		
		ExeSQL exeSQL = new ExeSQL();
		SSRS ssrs = exeSQL.execSQL(sql.toString());
		StringBuffer str = new StringBuffer();
		for (int i = 1; i <= ssrs.getMaxRow(); i++) {
			//APPNTNAME为前10个字为中意人寿保险有限公司的所有数据删去，其中包括中意人寿保险有限公司北京分公司等各分支公司的名称
			String appntName = ssrs.GetText(i, ssrs.getMaxCol());
			if(appntName.startsWith("中意人寿保险有限公司")){
				continue;
			}
			str.append( ssrs.GetText(i, 1) );
			for(int j = 2; j<ssrs.getMaxCol(); j++){
				//By Fang for 20120112(GEB客户标志1:非GEB客户; 2:GEB客户-Pooling; 3:GEB客户-Captive; 4:GEB客户-再保)
				String s = ssrs.GetText(i, j);
				if (j==3 && s!=null && !s.equals(""))
				{
					if (s.equals("1"))
						str.append(",").append("2");
					else 
						str.append(",").append("1");  //原1为GEB
				}else {
					str.append(",").append( s );
				}
				//str.append(",").append( ssrs.GetText(i, j) );
				//End 20120112
			}
			str.append("\r\n");
		}
		FileProcessor.outputFile(str.toString(), file_path+"data/", "claim_"+threadNum, "csv");
		return true;
	}
	
	
	
	synchronized static void addSuccThreadCount(){
		SuccThreadCount++;
		ThreadCount--;
	}
	synchronized static void addFailThreadCount(){
		FailThreadCount++;
		ThreadCount--;
	}
	synchronized static void addThreadCount(){
		ThreadCount++;
	}

}

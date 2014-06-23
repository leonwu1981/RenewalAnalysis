package com.sinosoft.lis.claimanalysis.claimratio;

import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;
import com.sinosoft.lis.claimanalysis.common.FileProcessor;

public class ClaimDataDownloadThread implements Runnable {

	int threadNum; // �̱߳��
	String startDate;
	String endDate;
	String minCaseNo;
	String maxCaseNo;
	String file_path;
	
	FileProcessor FileProcessor = new FileProcessor();
	
	static int SuccThreadCount = 0;  //�ɹ��߳���
	static int FailThreadCount = 0;  //ʧ���߳���
	static int ThreadCount = 0;  //����ִ���߳���
	
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
		// 1 MNGCOM �ֹ�˾
		sql.append(" select (select managecom from grpcont_view where grpcontno = y.grpcontno) managecom,    ");
		// 2 GRPCONTNO ������
		sql.append("        y.grpcontno,                                                                     ");
		// 3 GEB �Ƿ�Ϊ geb�ͻ�
		sql.append("        (select gebclient from grpcont_view where grpcontno = y.grpcontno) gebclient,    ");
		// 4 CCDATE ������Ч��
		sql.append("        (select CValiDate from grpcont_view where grpcontno = y.grpcontno) CValiDate,     ");
        // 5 CRDATE ������ֹ��
		sql.append("nvl((select max(payenddate) - 1 from lcgrppol where grpcontno = y.grpcontno),(select edorvalidate-1 from lpgrpedoritem where grpcontno = y.grpcontno and edortype in ('CT', 'WT') and edorstate = '0')),");
		// 6 CVALIDATE ��������������Ч��
		sql.append("(select min(firstpaydate) from duty_view where polno = y.polno and substr(dutycode, 0, 6) = y.dutycode) dutyCValiDate,");
		// 7 ENDDATE ��������������ֹ��
		sql.append("nvl((select max(enddate) - 1 from lcduty where polno = y.polno and contno = y.contno and substr(dutycode, 0, 6) = y.dutycode), (select m.edorvalidate-1 from lpedoritem m,lbcont n where m.edorno=n.edorno and m.contno=n.contno and m.grpcontno = y.grpcontno and m.contno = y.contno and m.edortype in ('ZT', 'CT') and m.oldedortype is null and m.edorstate = '0')) , ");
		// 8 INCURRED �¹ʷ�����
		sql.append("        l.accidentdate,                                                                  ");
		// 9 PAYDATE �⸶����
		sql.append("        (SELECT MAX(ConfDate)                                                            ");
		sql.append("           FROM ljaget                                                                   ");
		sql.append("          WHERE OtherNoType = '5'                                                        ");
		sql.append("            AND otherno = l.caseno                                                       ");
		sql.append("            and strikeflag is null) ConfDate,                                            ");
		// 10 RISKCODE ����
		sql.append("        y.riskcode,                                                                      ");
        // 11 CLAIMAMT ����������
		sql.append("        llcase_preclaimmoney(l.caseno) preclaimmoney,                                    ");
		// 12 REALPAY ʵ���⸶���
		sql.append("        (case                                                                            ");
		sql.append("          when l.rgtstate in ('09', '12') then                                           ");
		sql.append("           y.realpay                                                                     ");
		sql.append("        end) realpay,                                                                    ");
		// 13 BENCO �⸶����
		sql.append("        y.getdutycode,                                                                   ");
		// 14 DEPT ����ҵ�����������ֶ�
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
			//APPNTNAMEΪǰ10����Ϊ�������ٱ������޹�˾����������ɾȥ�����а����������ٱ������޹�˾�����ֹ�˾�ȸ���֧��˾������
			String appntName = ssrs.GetText(i, ssrs.getMaxCol());
			if(appntName.startsWith("�������ٱ������޹�˾")){
				continue;
			}
			str.append( ssrs.GetText(i, 1) );
			for(int j = 2; j<ssrs.getMaxCol(); j++){
				//By Fang for 20120112(GEB�ͻ���־1:��GEB�ͻ�; 2:GEB�ͻ�-Pooling; 3:GEB�ͻ�-Captive; 4:GEB�ͻ�-�ٱ�)
				String s = ssrs.GetText(i, j);
				if (j==3 && s!=null && !s.equals(""))
				{
					if (s.equals("1"))
						str.append(",").append("2");
					else 
						str.append(",").append("1");  //ԭ1ΪGEB
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

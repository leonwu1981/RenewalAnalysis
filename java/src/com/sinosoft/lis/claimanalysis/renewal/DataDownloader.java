package com.sinosoft.lis.claimanalysis.renewal;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;

import com.sinosoft.lis.claimanalysis.common.FileProcessor;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;

public class DataDownloader {
	final static int MaxThreadCount = 10;   //最大线程数
	final static int BatchNum = 100;   //每次处理的保单
	private final static int CONT_BATCH_SIZE = 2000;
	
	ExeSQL exeSQL = new ExeSQL();
	FileProcessor FileProcessor = new FileProcessor();
	private String file_path;
	
	// download claim data loop condition
	private String minContNo = "";
	private String maxContNo = "";
	private int contBatchCount = CONT_BATCH_SIZE;
	
	public void downloadData(String filePath, String grpContNo, String startDate, String endDate, String manageCom, String riskCode,String orgs){
		int threadNum = 1;
		file_path = filePath;
		HashMap lmDutyGetRelaHt = this.getLmDutyGetRelaMap();
		DataDownloadThread.FailThreadCount = 0;
		DataDownloadThread.SuccThreadCount = 0;
		DataDownloadThread.ThreadCount = 0;
		
		//如果保单太多，会导致系统慢，改为每BatchNum个保单处理一次
		String[] grpcontnos = grpContNo.split(",");
		String[] orgss = null;
		if(orgs!=null&&orgs.length()>0){
			orgss = orgs.split("\\|");
		}

		for (int i = 0; i < grpcontnos.length/BatchNum+1; i++) {
			int tmp = threadNum;
			//每次循环前个人保单号归0
			minContNo = "";
			maxContNo = "";
			int leave = grpcontnos.length-BatchNum*i;
			
			String dealingGrpContNo = "";
			String dealingOrgs = "";
			for (int j = 0; j < (leave>BatchNum?BatchNum:leave); j++) {
				dealingGrpContNo += grpcontnos[i*BatchNum+j]+",";
				if(orgss!=null){
					dealingOrgs += orgss[i*BatchNum+j]+"|";
				}
			}
			dealingGrpContNo = dealingGrpContNo.substring(0,dealingGrpContNo.length()-1);
			if(orgss!=null){
				dealingOrgs = dealingOrgs.substring(0,dealingOrgs.length()-1);
			}
			
			do {
				if( !getContBatchInfo(dealingGrpContNo, startDate, endDate, manageCom, riskCode,dealingOrgs) ){
					continue;
				}
				boolean flag = true;
				
				while(flag){
					if( DataDownloadThread.getThreadCount() < MaxThreadCount ){
//						System.out.println("线程"+threadNum+"开始运行···  共"+DataDownloadThread.getThreadCount()+"个进程");
//						Thread thread = new Thread(new DataDownloadThread(threadNum, minContNo, maxContNo, file_path+"data/", grpContNo, startDate, endDate, manageCom, riskCode, lmDutyGetRelaHt)); 
						Thread thread = new Thread(new DataDownloadThread(threadNum, minContNo, maxContNo, filePath, dealingGrpContNo, startDate, endDate, manageCom, riskCode, lmDutyGetRelaHt,dealingOrgs)); 
						thread.start(); 
						flag = false;
					} else {
						try {
							Thread.sleep(1000*10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
				// loop
				threadNum++;
				minContNo = maxContNo;

			} while( contBatchCount == CONT_BATCH_SIZE );
			while( (DataDownloadThread.FailThreadCount + DataDownloadThread.SuccThreadCount ) < (threadNum-1) )
			{
				try {
					Thread.sleep(1000*30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			System.out.println("完成第"+i+"批："+dealingGrpContNo+"，\r\n开始于"+tmp+"，结束于"+(threadNum-1));
		}
	
		


		System.out.println("fail:"+DataDownloadThread.FailThreadCount);
		System.out.println("succ:"+DataDownloadThread.SuccThreadCount);
		System.out.println("threadNum:"+threadNum);
		// head
		String claim_head = "分公司代码,团体保单号,保单生效日,保单终止日,被保险人客户号,出生年月,性别,被保险人生效日,被保险人终止日,在职或退休,与被保险人的关系,计划编码,计划名称,被保险人赔案号,出险日期,住院结束日期,住院天数,就医医院代码,就医医院名称,医院等级,索赔原因,申请赔付日期,费用项目代码,费用项目名称,费用金额,自费金额,部分自付金额,医保支付金额,索赔项目代码,索赔项目名称,实际赔付金额,赔付日期,险种代码,出险时间段,理赔时间段,理赔延迟时间,年份,险种名,门诊日限额,检查费日限额,保额,赔付比例,是否TPA标记,是否TPA赔付,行业,姓名,GEB客户,部门编码,职业编码,给付状态,理算金额,Payable,医保范围内\r\n";
		FileProcessor.outputFile(claim_head, file_path, "claim_0", "csv");
		String insured_head = "分公司代码,团体保单号,保单生效日,保单终止日,是否续保,投保单位,投保单位部门,单位性质,行业类别,主营业务,被保险人客户号,被保险人姓名,被保险人生效日,被保险人终止日,被保险人职业等级,在职或退休,与被保险人的关系,出生年月,性别,计划编码,计划名称,险种,责任,保额,保费,起付线,日药费限额,日费用限额,日床位费限额,日检查费限额,赔付比例,意外赔付比例,服务机构,年龄,年龄段,曝光数,曝光数2,费用金额,自费金额,部分自付金额,医保支付金额,实际赔付金额,门诊就诊次数,就诊次数,就诊人数,住院天数,保全类型,年份,险种名,责任名,缴至日期,行业,GEB客户,部门编码,职业编码,保单状态,理算金额,Payable,医保范围内\r\n";
		FileProcessor.outputFile(insured_head, file_path, "match_0", "csv");
		String misMatch_head = "团体保单号,被保险人客户号,个人保单号,险种,计划,赔付责任,实际赔付金额\r\n";
		FileProcessor.outputFile(misMatch_head, file_path, "misMatch_0", "csv");

		// mergeFiles
		FileProcessor.mergeFiles(file_path, "claim", 0, threadNum-1, "csv", "claim" );
		FileProcessor.mergeFiles(file_path, "match", 0, threadNum-1, "csv", "match" );
		FileProcessor.mergeFiles(file_path, "misMatch", 0, threadNum-1, "csv", "misMatch" );
		
		// 
		FileProcessor.newFolder( file_path + "Incurred-Pay" );
		FileProcessor.newFolder( file_path + "IBNR" );
	}
	
	public boolean getContBatchInfo(String grpContNo, String startDate, String endDate, String manageCom, String riskCode,String orgs){
		StringBuffer filter = new StringBuffer();
		
		// GrpContNo
		StringTokenizer st = new StringTokenizer(grpContNo, ",");
		if(st.hasMoreTokens()){
			filter.append(" and ( b.grpcontno = '").append(st.nextToken()).append("' ");
			while(st.hasMoreTokens()){
	        	filter.append(" or b.grpcontno = '").append(st.nextToken()).append("' ");
	        }
	        filter.append(" ) ");
		}
        
        // StartDate
		if( startDate != null && ! "".equals(startDate) ){
			filter.append(" and exists(SELECT 1 FROM lcgrpcont WHERE grpcontno = b.grpcontno and CValiDate>=to_date('").append(startDate).append("', 'yyyy-mm-dd')) ");
		}
		// EndDate
		if( endDate != null && ! "".equals(endDate) ){
			filter.append(" and exists(SELECT 1 FROM lcgrpcont WHERE grpcontno = b.grpcontno and CValiDate<=to_date('").append(endDate).append("', 'yyyy-mm-dd')) ");
		}
		// ManageCom
		if ( manageCom != null && !"".equals(manageCom) ) {
			filter.append(" and exists(select 1 from lcgrpcont where managecom like '").append(manageCom).append("%' and grpcontno=b.grpcontno ) ");
 		}
		
		//机构条件
		if(orgs!=null&&orgs.length()>0){
			String[] grpcontnos = grpContNo.split(",");
			String[] orgsAll = orgs.split("\\|");
			if(grpcontnos.length!=orgsAll.length){
				System.out.println("保单的个数和分支机构的组数不等！");
				return false;
			}
			String finSql = " and (";
			for (int i = 0; i < grpcontnos.length; i++) {
				if("%".equals(orgsAll[i])){
					finSql += " (b.grpcontno='"+grpcontnos[i]+"') or";
				}else{
					String[] orgArray = orgsAll[i].split(",");
					String orgsLine = "";
					for (int j = 0; j < orgArray.length; j++) {
						orgsLine += "'"+orgArray[j]+"',";
					}
					orgsLine = orgsLine.substring(0,orgsLine.length()-1);
					finSql += " (b.grpcontno='"+grpcontnos[i]+"' and b.organcomcode in ("+orgsLine+")) or";
				}
			}
			finSql = finSql.substring(0,finSql.length()-2) + ")";
			filter.append(finSql);
		}
		
//		filter.append(" and b.relationtomaininsured<>'00'");
		
		// minContNo
		if("".equals(minContNo) || minContNo==null){
			// minContNo
    		StringBuffer minContSql = new StringBuffer();
    		minContSql.append(" select min(contno) from (");
    		
    		//edit by wuk,增加了从lbinsured表里查最小个单号,否则会漏处理数据
    		minContSql.append(" select min(a.contno) contno                  ");
    		minContSql.append("   from (select b.contno                      ");
    		minContSql.append("           from lcinsured b                ");
    		minContSql.append("          where b.grpcontno > '0' ");
    		minContSql.append( filter );
    		minContSql.append("    ) a                ");
    		minContSql.append(" union               ");
    		minContSql.append(" select min(a.contno) contno                  ");
    		minContSql.append("   from (select b.contno                      ");
    		minContSql.append("           from lbinsured b                ");
    		minContSql.append("          where b.grpcontno > '0' ");
    		minContSql.append( filter );
    		minContSql.append("    ) a                ");
    		minContSql.append("    ) ");
//    		System.out.println(minContSql);
    		SSRS minContSSRS = exeSQL.execSQL(minContSql.toString());
    		if ( minContSSRS.getMaxRow() <= 0) {
    			return false;
    		}
    		
    		// minContNo < contNo <= maxContNo
    		char[] c = minContSSRS.GetText(1, 1).toCharArray();
    		if(c.length>0){
    			c[c.length-1] = (char)( c[c.length-1] - 1 );
        		minContNo = new String(c);  // minContNo 前一个
        		maxContNo = minContSSRS.GetText(1, 1);
    		}else{
    			minContNo = "0";
    			maxContNo = "1";
    		}
    		
		}
		
		
		StringBuffer sql = new StringBuffer();
		sql.append(" select max(a.contno), count(1)             ");    
		
		//edit by wuk,增加了从lbinsured表里查最大个单号,否则会漏处理数据
		sql.append("   from (select * from (select b.contno                      ");
		sql.append("           from lcinsured b                 ");
		sql.append("          where b.contno > '"+minContNo+"'    ");
		sql.append(filter);
		sql.append(" union ");
		sql.append(" select b.contno                      ");
		sql.append("           from lbinsured b                 ");
		sql.append("          where b.contno > '"+minContNo+"'    ");
		sql.append(filter);
		sql.append(" )         order by contno) a                ");
		sql.append("  where rownum <= "+CONT_BATCH_SIZE+"      ");
		SSRS ssrs = exeSQL.execSQL(sql.toString());
		if( ssrs.getMaxRow() > 0 ){
			maxContNo = ssrs.GetText(1, 1);
			contBatchCount = Integer.parseInt(ssrs.GetText(1, 2));
			return true;
		} else {
			maxContNo = "";
			contBatchCount = 0;
			return false;
		}
	}
	
	/**
	 * 理赔责任-责任  对应关系
	 * @return Hashtable - key : GetDutyCode  value : DutyCode
	 */
	public Hashtable getLmDutyGetRelaHt() {
		Hashtable ht = new Hashtable();
		StringBuffer sql = new StringBuffer();
		sql.append("select a.dutycode, b.getdutycode ");
		sql.append("from lmriskduty a, lmdutygetrela b ");
		sql.append("where a.dutycode = b.dutycode ");
//		sql.append("and a.riskcode in(select code from ldcode where codetype='filedownriskcode') ");
//		ExeSQL exeSQL = new ExeSQL();
		SSRS ssrs = exeSQL.execSQL(sql.toString());
		if ( ssrs.getMaxRow() <= 0) {
			return ht;
		}
		for( int r = 1; r < ssrs.getMaxRow() + 1 ; r++ ) {
			String dutyCode = ssrs.GetText( r, 1 );
			String getDutyCode = ssrs.GetText( r, 2 );
			if( dutyCode != null && getDutyCode != null ){
				ht.put(getDutyCode, dutyCode);
			}
		}
		return ht;
	}
	
	public HashMap getLmDutyGetRelaMap(){
		HashMap map = new HashMap();
		String sql = "select a.dutycode, b.getdutycode from lmriskduty a, lmdutygetrela b where a.dutycode = b.dutycode";
		SSRS ssrs = exeSQL.execSQL(sql);
		if ( ssrs.getMaxRow() <= 0) {
			return map;
		}
		for( int r = 1; r < ssrs.getMaxRow() + 1 ; r++ ) {
			String dutyCode = ssrs.GetText( r, 1 );
			String getDutyCode = ssrs.GetText( r, 2 );
			if( dutyCode == null || getDutyCode == null ){
				continue;
			}
			Object obj = map.get(dutyCode);
			if( obj == null ){
				map.put(dutyCode, getDutyCode);
			} else {
				String getDutyCodes = (String)obj;
				map.put(dutyCode, getDutyCodes+"_"+getDutyCode);
			}
		}
		return map;
	}

}

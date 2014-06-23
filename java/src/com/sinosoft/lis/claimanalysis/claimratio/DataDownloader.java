package com.sinosoft.lis.claimanalysis.claimratio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sinosoft.lis.claimanalysis.common.FileProcessor;
import com.sinosoft.lis.claimanalysis.renewal.DataDownloadThread;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;

public class DataDownloader {
	final static int MaxThreadCount = 10;   //最大线程数
	private final static int POL_BATCH_SIZE = 10000;
	private final static int CASE_BATCH_SIZE = 6000;
	
	ExeSQL exeSQL = new ExeSQL();
	FileProcessor FileProcessor = new FileProcessor();
	String file_path = "d:/ClaimAnal/";
	
	
	// download claim data loop condition
	String minCaseNo = "0";
	String maxCaseNo = "0";
	int caseBatchCount = CASE_BATCH_SIZE;
	
	DataDownloader( String file_path ){
		this.file_path = file_path;
	}
	
	public void downloadClaimData(String startDate, String endDate){
		int threadNum = 1;
		do {
			if( !getCaseBatchInfo(startDate, endDate) ){
				continue;
			}
			boolean flag = true;
			while(flag){
				if( ClaimDataDownloadThread.ThreadCount < MaxThreadCount ){
					System.out.println("线程"+threadNum+"开始运行···  共"+ClaimDataDownloadThread.ThreadCount+"个进程");
					Thread thread = new Thread(new ClaimDataDownloadThread(threadNum, startDate, endDate, minCaseNo, maxCaseNo, file_path)); 
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
			minCaseNo = maxCaseNo;
		} while( caseBatchCount == CASE_BATCH_SIZE );
		
		while( (ClaimDataDownloadThread.FailThreadCount + ClaimDataDownloadThread.SuccThreadCount ) < (threadNum-1) )
		{
			try {
				Thread.sleep(1000*60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// mergeFiles
		FileProcessor.mergeFiles(file_path+"data/", "claim", 1, threadNum-1, "csv", "claim");
	}
	
	/**
	 * 
	 * @output policy.csv ( 全部所需的保费信息 from pol表 )
	 */
	public void downloadPolicyData(String startDate, String endDate){
		int threadNum = 1;
		SSRS ssrs = getGrpContNos(startDate, endDate);
		for(int i=1;i<ssrs.getMaxRow();i++){
			String grpContNo = ssrs.GetText(i, 1);
			int polCount = getPolCount(grpContNo);
			
			int x = polCount/POL_BATCH_SIZE;
			int y = polCount%POL_BATCH_SIZE==0?0:1;
			for(int j=1;j<=(x+y);j++){
				int startRow = (j-1)*POL_BATCH_SIZE + 1;
				int endRow = j*POL_BATCH_SIZE;
				boolean flag = true;
				while(flag){
					if( PolicyDataDownloadThread.ThreadCount < MaxThreadCount ){
						System.out.println("线程"+threadNum+"开始运行···  共"+PolicyDataDownloadThread.ThreadCount+"个进程");
						Thread thread = new Thread(new PolicyDataDownloadThread(threadNum, startDate, endDate, grpContNo, startRow, endRow, file_path)); 
						thread.start(); 
						flag = false;
					} else {
						try {
							Thread.sleep(1000*1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				threadNum++;
			}
		}
		while((PolicyDataDownloadThread.FailThreadCount + PolicyDataDownloadThread.SuccThreadCount ) < (threadNum-1) )
		{
			try {
				Thread.sleep(1000*60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// mergeFiles
		FileProcessor.mergeFiles(file_path+"data/", "policy", 1, threadNum-1, "csv", "policy");
	}
	
	public boolean getCaseBatchInfo(String startDate, String endDate){
		StringBuffer sql = new StringBuffer();
		sql.append(" select max(a.caseno), count(1) ");
		sql.append("   from (select distinct (caseno) caseno ");
		sql.append("           from llcase ");
		sql.append("          where ((accidentdate >= date '"+startDate+"' and accidentdate <= date '"+endDate+"') or accidentdate is null) ");
		sql.append("            and caseno > '"+minCaseNo+"' ");
		sql.append("          order by caseno) a ");
		sql.append("  where rownum <= "+CASE_BATCH_SIZE+" ");
		SSRS ssrs = exeSQL.execSQL(sql.toString());
		if( ssrs.getMaxRow() > 0 ){
			maxCaseNo = ssrs.GetText(1, 1);
			caseBatchCount = Integer.parseInt(ssrs.GetText(1, 2));
			return true;
		} else {
			maxCaseNo = "";
			caseBatchCount = 0;
			return false;
		}
	}
	
	public SSRS getGrpContNos(String startDate, String endDate){
		StringBuffer sql = new StringBuffer();
		sql.append(" select distinct(b.grpcontno) ");
		sql.append(" from lcgrpcont b ");
		sql.append(" where b.grpcontno <> '99029288' ");
		sql.append(" and b.appflag = '1' ");
		sql.append(" and ((b.CValiDate >= date '"+startDate+"' and b.CValiDate <= date '"+endDate+"') ");
		sql.append(" or ((select max(payenddate) - 1 from lcgrppol where grpcontno = b.grpcontno) between date '"+startDate+"' and date '"+endDate+"')) ");
		return exeSQL.execSQL(sql.toString());
	}
	
	public int getPolCount( String grpContNo ){
		String sql_c = "select count(1) from lcpol where grpcontno='"+grpContNo+"'";
		SSRS ssrs_c = exeSQL.execSQL(sql_c);
		String sql_b = "select count(1) from lbpol where grpcontno='"+grpContNo+"'";
		SSRS ssrs_b = exeSQL.execSQL(sql_b);
		int c = Integer.parseInt( ssrs_c.GetText(1, 1) );
		int b = Integer.parseInt( ssrs_b.GetText(1, 1) );
		return b+c;
	}
	
	/**
	 * 
	 * @output claim_factor_1.csv
	 * @output claim_ibnr.csv
	 * @output claim_realpay.csv
	 */
	public void exportClaimDataSource(String startDate_factor, String endDate_factor, String startDate_ibnr, String endDate_ibnr, String startDate_realPay, String endDate_realPay){
		IbnrFactorCalculator IbnrFactorCalculator = new IbnrFactorCalculator(); 
		Date d1_factor = null;
		Date d2_factor = null;
		Date d1_ibnr = null;
		Date d2_ibnr = null;
		Date d1_realPay = null;
		Date d2_realPay = null;
		java.text.DateFormat fmt =new java.text.SimpleDateFormat("yyyy-MM-dd");            
        try {
        	d1_factor = fmt.parse(startDate_factor);
        	d2_factor = fmt.parse(endDate_factor);
        	d1_ibnr = fmt.parse(startDate_ibnr);
        	d2_ibnr = fmt.parse(endDate_ibnr);
        	d1_realPay = fmt.parse(startDate_realPay);
        	d2_realPay = fmt.parse(endDate_realPay);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		HashMap map_factor = new HashMap();
		HashMap map_ibnr = new HashMap();
		HashMap map_realPay = new HashMap();
		try {
			String line;
			// claim
			File claim_file = new File( file_path + "data/" + "claim.csv" );
			BufferedReader br = new BufferedReader(new FileReader(claim_file));
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
				String manageCom = row[0];
				String grpContNo = row[1];
				String accidentDateStr = row[7];
				String payDateStr = row[8];
				String riskCode = row[9];
				String realPayStr = row[11];
				String getDutyCode = row[12];
				String gebClient = row[2];
				String deptID = row[13];
				
				// 6.30 yaobin 其他险种不在计算范围内
				String risks = "NIK01,NIK02,NIK12,NIK03,NIK07,NIK08,NIK10,NIK11,NIK09,MIK01,MIK02,MOK01,MKK01,NMK01,NMK08,NLK03,NLK01,NMK04,NMK02,NMK03,NMK09,NMK06,NMK07,NLK02,NL05,NAK01,NAK02,NAK03,MGK02,MGK03,MGK04,MGK01,MKK02,MMK01,MMK02";
				String regex = "^.*"+riskCode+".*$";
				if( !risks.matches(regex) ){
					continue;
				}
				// realpay>0
				if("0".equals(realPayStr)||"".equals(realPayStr)){
					continue;
				}
				// incurred-pay delay
				String[] s = getDelay(accidentDateStr, payDateStr, endDate_ibnr);
				if( s == null ){
					continue;
				}
				// IBNR Type
				String ibnrType = IbnrFactorCalculator.getIbnrType(manageCom, gebClient, riskCode, getDutyCode, deptID);
				
				// incurred date & pay date
				Date accidentDate = null;
				Date payDate = null;         
		        try {
		        	accidentDate = fmt.parse(accidentDateStr);
		        	payDate = fmt.parse(payDateStr);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				// real pay
				double realPay = Double.parseDouble(realPayStr);
				// claim : ibnr factor
				if( !"".equals(ibnrType) &&  accidentDate.compareTo(d1_factor)>=0  && payDate.compareTo(d2_factor)<=0 ){
					String key = ibnrType+","+s[0]+","+s[1];
					Object o = map_factor.get(key);
					if(o==null){
						map_factor.put(key, new Double(realPay));
					} else {
						Double value = (Double)o;
						map_factor.put(key, new Double( value.doubleValue() + realPay ));
					}
					
				}
				// claim : new ibnr
				if( payDate.compareTo(d1_ibnr)>=0  && payDate.compareTo(d2_ibnr)<=0 ){
					String key = manageCom+","+grpContNo+","+riskCode+","+gebClient+","+deptID+","+ibnrType+","+s[2];
					Object o = map_ibnr.get(key);
					if(o==null){
						map_ibnr.put(key, new Double(realPay));
					} else {
						Double value = (Double)o;
						map_ibnr.put(key, new Double( value.doubleValue() + realPay ));
					}
				}
				// claim : real pay
				if( payDate.compareTo(d1_realPay)>=0  && payDate.compareTo(d2_realPay)<=0  ){
					String key = manageCom+","+grpContNo+","+riskCode;
					Object o = map_realPay.get(key);
					if(o==null){
						map_realPay.put(key, new double[]{realPay,1});
					} else {
						double[] value = (double[])o;
						value[0] += realPay;
						value[1]++;
					}
				}
			}
			// export claim_facotr.csv
			StringBuffer buffer = new StringBuffer();
			Set keySet = map_factor.keySet();
			for(Iterator i = keySet.iterator(); i.hasNext(); ) {
				String key = (String)i.next();
				Double value = (Double)map_factor.get(key);
				buffer.append(key).append(",").append(value.doubleValue()).append("\r\n");
			}
			FileProcessor.outputFile(buffer.toString(), file_path+"data/", "claim_factor_1", "csv");
			// export claim_ibnr.csv
			buffer = new StringBuffer();
			keySet = map_ibnr.keySet();
			for(Iterator i = keySet.iterator(); i.hasNext(); ) {
				String key = (String)i.next();
				Double value = (Double)map_ibnr.get(key);
				buffer.append(key).append(",").append(value.doubleValue()).append("\r\n");
			}
			FileProcessor.outputFile(buffer.toString(), file_path+"data/", "claim_ibnr", "csv");
			// export claim_realpay.csv
			buffer = new StringBuffer();
			keySet = map_realPay.keySet();
			for(Iterator i = keySet.iterator(); i.hasNext(); ) {
				String key = (String)i.next();
				double[] value = (double[])map_realPay.get(key);
				java.text.DecimalFormat df = new java.text.DecimalFormat( "#" );
				buffer.append(key).append(",").append(value[0]).append(",").append(df.format(value[1])).append("\r\n");
			}
			FileProcessor.outputFile(buffer.toString(), file_path+"data/", "claim_realpay", "csv");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 按IBNR因子的类型生成每季度预测理赔，主要用于考察因子是否合理
	 * 
	 */
	public void exportIbnrByQuarter( String startDate, String endDate ){
		String[] s = startDate.split("-");
		int startYear = Integer.parseInt( s[0] );
		int startMonth = Integer.parseInt( s[1] );
		int startQuarter = (startMonth-1) / 3 + 1;
		
		s = endDate.split("-");
		int endYear = Integer.parseInt( s[0] );
		int endMonth = Integer.parseInt( s[1] );
		int endQuarter = (endMonth-1) / 3 + 1;
		// factors
		IbnrFactorCalculator IbnrFactorCalculator = new IbnrFactorCalculator();
		HashMap ibnrMap = IbnrFactorCalculator.readIbnrFactorFile();
		// realpay
		HashMap realpayMap = new HashMap();
		File file = new File(file_path + "data/" + "claim_ibnr.csv");
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
				String type = row[5];
				int q = Integer.parseInt( row[6] );
				double realpay = Double.parseDouble( row[7] );
				//
				Object o = realpayMap.get(type);
				if( o == null ){
					double[] realpays = new double[19];
					realpays[q] = realpay;
					realpayMap.put(type, realpays);
				} else {
					double[] realpays = (double[])o;
					realpays[q] += realpay;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		StringBuffer buffer = new StringBuffer();
		
		java.text.DecimalFormat df = new java.text.DecimalFormat( "#.##" );
		int endQ = endYear*4 + endQuarter - 1;
		int startQ = startYear*4 + startQuarter - 1;
		
		String[][] fcTypes = new String[5][6];
		fcTypes[0] = new String[]{"BJ_NONGEB_MED01","BJ_NONGEB_MED02","BJ_GEB_MED01","BJ_GEB_MED02","BJ_WMP","BJ_AMR"};
		fcTypes[1] = new String[]{"SH_NONGEB_MED01","SH_NONGEB_MED02","SH_GEB_MED01","SH_GEB_MED02","SH_WMP","SH_AMR"};
		fcTypes[2] = new String[]{"GD_NONGEB_MED01","GD_NONGEB_MED02","GD_GEB_MED01","GD_GEB_MED02","","GD_AMR"};
		fcTypes[3] = new String[]{"GC_MED01","GC_MED02","","","GC_WMP","GC_AMR"};
		fcTypes[4] = new String[]{"Captive_BJ_MED","","","","",""};
		
		
		for(int row=0;row<fcTypes.length;row++){
			
			String[] lines = new String[endQ-startQ+3];
			for(int i=0;i<lines.length;i++){
				lines[i] = "";
			}
			for(int col=0;col<fcTypes[0].length;col++){
				String fcType = fcTypes[row][col];
				// line1/2
				lines[0] = lines[0] + fcType + ",,,,,";
				if("".equals(fcType)){
					lines[1] = lines[1] + fcType + ",,,,,";
				} else {
					lines[1] = lines[1] + "季度,理赔金额,完成因子,预测理赔金额,,";
				}
				
				// other lines
				for(int j=0;j<(endQ-startQ+1);j++){
//					int lineNum = j + 2;// 时间倒序
					int lineNum = endQ-startQ+2-j;// 时间正序
					
					if( "".equals(fcType) ){
						lines[lineNum] = lines[lineNum] + ",,,,,";
					} else {
						double[] factors = (double[])ibnrMap.get(fcType);
						double[] realpays = null;
						Object o = realpayMap.get(fcType);
						if( o == null ){
							realpays = new double[19];
						} else {
							realpays = (double[])o;
						}
						// quarter
						int q = endQ - j;
						int year = q/4;
						int quarter = q%4 + 1;
						// realpay
						double realpay = realpays[j];
						// factor
						double factor;
						if( factors == null ){
							factor = 1;
						} else {
							factor = factors[j];
						}
						// predict
						double predict = realpay/factor;
						lines[lineNum] = lines[lineNum] + year + "Q"+quarter+","+df.format(realpay)+","+factor+","+df.format(predict)+",,";
					}
				}
			}
			for(int i=0;i<lines.length;i++){
				buffer.append(lines[i]).append("\r\n");
			}
			buffer.append("\r\n");
		}
//		Set keySet = ibnrMap.keySet();
//		for(Iterator i = keySet.iterator(); i.hasNext(); ) {
//			String key = (String)i.next();
//			double[] factors = (double[])ibnrMap.get(key);
//			double[] realpays = null;
//			Object o = realpayMap.get(key);
//			if( o == null ){
//				realpays = new double[19];
//			} else {
//				realpays = (double[])o;
//			}
//			
//			buffer.append(key).append(",,,\r\n");
//			buffer.append("季度,理赔金额,完成因子,预测理赔金额\r\n");
//			
//			for(int j = 0; j<realpays.length; j++){
//				// quarter
//				int q = endQ - j;
//				if( q<startQ ){
//					break;
//				}
//				int year = q/4;
//				int quarter = q%4 + 1;
//				// realpay
//				double realpay = realpays[j];
//				// factor
//				double factor;
//				if( factors == null ){
//					factor = 1;
//				} else {
//					factor = factors[j];
//				}
//				// predict
//				double predict = realpay/factor;
//				
//				buffer.append(year).append("Q").append(quarter).append(",").append(df.format(realpay)).append(",").append(factor).append(",").append(df.format(predict)).append("\r\n");
//			}
//			buffer.append(",,,\r\n");
//		}
		FileProcessor.outputFile(buffer.toString(), file_path+"data/", "predictclaim", "csv");
	}
	
	public HashMap getBranchMap(){
		HashMap map = new HashMap();
		map.put("8601", "BJ");
		map.put("8602", "GD");
		map.put("8603", "SH");
		map.put("8604", "JS");
		map.put("8605", "SZ");
		map.put("8606", "LN");
		map.put("8607", "SC");
		map.put("8608", "SX");
		map.put("8609", "SD");
		map.put("8610", "HLJ");
		return map;
	}
	
	public HashMap getRiskMap(){
		HashMap map = new HashMap();
		String riskType;
		// Medical
		riskType = "MED";
		map.put("NIK01", riskType);
		map.put("NIK02", riskType);
		map.put("MKK01", riskType);
		// WMP
		riskType = "WMP";
		map.put("NIK03", riskType);
		map.put("NIK07", riskType);
		map.put("NIK08", riskType);
		map.put("NIK10", riskType);
		map.put("NIK11", riskType);
		map.put("NIK09", riskType);
		// AMR
		riskType = "AMR";
		map.put("MIK01", riskType);
		map.put("MOK01", riskType);
		map.put("MIK02", riskType); //By Fang 20121009(增加MIK02的处理)
		// Non-medical
		riskType = "NONMED";
		map.put("NMK01", riskType);
		map.put("NLK03", riskType);
		map.put("NLK01", riskType);
		map.put("NMK02", riskType);
		map.put("NMK03", riskType);
		map.put("NLK02", riskType);
		map.put("NL05", riskType);
		map.put("NAK01", riskType);
		map.put("NAK02", riskType);
		map.put("MGK01", riskType);
		map.put("MGK02", riskType);
		map.put("MGK03", riskType);
		map.put("MGK04", riskType);
		map.put("MKK02", riskType);
		map.put("MMK01", riskType);
		map.put("NMK06", riskType); //By Fang 20120112(增加NMK06的处理)
		map.put("NMK07", riskType); //By Fang 20121009(增加NMK07的处理)
		map.put("NMK08", riskType); //By Fang 20140101(增加NMK08的处理)
		map.put("MMK02", riskType); //By Fang 20140101(增加MMK02的处理)
		map.put("NMK09", riskType); //By Fang 20140101(增加NMK09的处理)
		map.put("NAK03", riskType); //By Fang 20140101(增加NAK03的处理)
		return map;
	}
	
	public String getInfoType( HashMap branchMap, HashMap riskMap, String manageCom, String gebClient, String riskCode, String dept ){
		// Captive_BJ_MED
		if( "14".equals(dept) && manageCom.startsWith("8601") && ( "NIK01".equals(riskCode) || "NIK02".equals(riskCode) )){
			return "Captive_BJ_MED";
		}
		Object o;
		o = branchMap.get(manageCom.substring(0, 4));
		if( o==null ){
			return null;
		}
		String branch = (String)o;
		
		o = riskMap.get(riskCode);
		if( o==null ){
			return null;
		}
		String riskType = (String)o;
		if( "WMP".equals(riskType) || "AMR".equals(riskType) ){
			return branch + "_" + riskType;
		} else {
			String gebFlag;
			if( "1".equals(gebClient) ){
				gebFlag = "GEB";
			} else {
				gebFlag = "NONGEB";
			}
			return branch + "_" + gebFlag + "_" + riskType;
		}
	}
	
	/**
	 * 按季度统计“暴露数”、“已赚保费”、“理赔”、“IBNR”等
	 */
	public void exportInfoByQuarter( String startDate, String endDate ){
		HashMap branchMap = this.getBranchMap();
		HashMap riskMap = this.getRiskMap();
		
		String[] s = startDate.split("-");
		int startYear = Integer.parseInt( s[0] );
		int startMonth = Integer.parseInt( s[1] );
		int startQuarter = (startMonth-1) / 3 + 1;
		
		s = endDate.split("-");
		int endYear = Integer.parseInt( s[0] );
		int endMonth = Integer.parseInt( s[1] );
		int endQuarter = (endMonth-1) / 3 + 1;
		// factors
		IbnrFactorCalculator IbnrFactorCalculator = new IbnrFactorCalculator();
		HashMap ibnrMap = IbnrFactorCalculator.readIbnrFactorFile();
		// realpay\ibnr
		HashMap claimMap = new HashMap();
		HashMap policyMap = new HashMap();
		BufferedReader br;
		try {
			File file = new File(file_path + "data/" + "claim_ibnr.csv");
			br = new BufferedReader(new FileReader(file));
			String line;
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
				String manageCom = row[0];
				String riskCode = row[2];
				String gebClient = row[3];
				String type = row[5];
				String dept = row[4];
				int q = Integer.parseInt( row[6] );
				double realpay = Double.parseDouble( row[7] );
				
				String infoType = this.getInfoType(branchMap, riskMap, manageCom, gebClient, riskCode, dept);
				Object o;
				double factor;
				o = ibnrMap.get(type);
				if( o==null ){
					factor = 1;
				} else {
					double[] factors = (double[])o;
					factor = factors[q];
				}
				String key = infoType+"#"+q;
				//
				o = claimMap.get(key);
				if( o == null ){
					double[] claimdata = new double[2];
					claimdata[0] = realpay;
					claimdata[1] = realpay/factor - realpay;
					claimMap.put(key, claimdata);
				} else {
					double[] claimdata = (double[])o;
					claimdata[0] += realpay;
					claimdata[1] += (realpay/factor - realpay);
				}
			}
			
			file = new File(file_path + "data/" + "exposure_earned.csv");
			br = new BufferedReader(new FileReader(file));
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
				String manageCom = row[0];
				String gebClient = row[1];
				String riskCode = row[3];
				String quarter = row[4];
				String dept = row[2];
				double exposure = Double.parseDouble(row[5]);
				double earnedprem = Double.parseDouble( row[6] );
				if("MKK01".equals(riskCode)){
					exposure = 0;// MKK01不用来累计暴露数
				}
				String infoType = this.getInfoType(branchMap, riskMap, manageCom, gebClient, riskCode, dept);
				Object o;

				String key = infoType+"#"+quarter;
				//
				o = policyMap.get(key);
				if( o == null ){
					double[] policydata = new double[2];
					policydata[0] = exposure;
					policydata[1] = earnedprem;
					policyMap.put(key, policydata);
				} else {
					double[] policydata = (double[])o;
					policydata[0] += exposure;
					policydata[1] += earnedprem;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		StringBuffer buffer = new StringBuffer();
		
		java.text.DecimalFormat df = new java.text.DecimalFormat( "#.##" );
		int endQ = endYear*4 + endQuarter - 1;
		int startQ = startYear*4 + startQuarter - 1;
		
		String[] branchNames = new String[]{"Captive_BJ_MED","BJ","GD","SH","JS","SZ","LN","SC","SX","SD","HLJ"};
		String[] riskTypes = new String[]{"_GEB_MED","_NONGEB_MED","_WMP","_AMR","_GEB_NONMED","_NONGEB_NONMED"};
		
		
		for(int row=0;row<branchNames.length;row++){
			
			String[] lines = new String[endQ-startQ+3];
			for(int i=0;i<lines.length;i++){
				lines[i] = "";
			}
			for(int col=0;col<riskTypes.length;col++){
				String infoType = branchNames[row]+riskTypes[col];
				// 特殊情况
				if( "Captive_BJ_MED".equals(branchNames[row]) ){
					if( col == 0 ){
						infoType = "Captive_BJ_MED";
					} else {
						infoType = "";
					}
				}
				// line1/2
				lines[0] = lines[0] + infoType + ",,,,,,";
				if("".equals(infoType)){
					lines[1] = lines[1] + infoType + ",,,,,,";
				} else {
					lines[1] = lines[1] + "季度,暴露数,已赚保费,理赔金额,IBNR,,";
				}
				
				// other lines
				for(int j=0;j<(endQ-startQ+1);j++){
//					int lineNum = j + 2;// 时间倒序
					int lineNum = endQ-startQ+2-j;// 时间正序
					if( "".equals(infoType) ){
						lines[lineNum] = lines[lineNum] + ",,,,,,";
					} else {
						// quarter
						int q = endQ - j;
						int year = q/4;
						int quarter = q%4 + 1;
						String key;
						Object o;
						double realpay = 0;
						double ibnr = 0;
						double exposure = 0;
						double earnedprem = 0;
						// claim
						key = infoType + "#" + j;
						o = claimMap.get(key);
						if( o!=null ){
							double[] claimdata = (double[])o;
							realpay = claimdata[0];
							ibnr = claimdata[1];
						}
						// policy
						key = infoType + "#" + year + "Q" + quarter;
						o = policyMap.get(key);
						if( o!=null ){
							double[] policydata = (double[])o;
							exposure = policydata[0];
							earnedprem = policydata[1];
						}
						lines[lineNum] = lines[lineNum] + year + "Q"+quarter+","+df.format(exposure)+","+df.format(earnedprem)+","+df.format(realpay)+","+df.format(ibnr)+",,";
					}
				}
			}
			for(int i=0;i<lines.length;i++){
				buffer.append(lines[i]).append("\r\n");
			}
			buffer.append("\r\n");
		}
		FileProcessor.outputFile(buffer.toString(), file_path+"data/", "infoQ", "csv");
	}
	
	/**
	 * 
	 * @output exposure_earned.csv ( 按季度的暴露数和已赚保费信息 from policy.csv )
	 * @output pol.csv ( "团单-险种"信息 from policy.csv )
	 */
	public void exportPolicyDataSource(String startDate, String endDate){
		HashMap map = new HashMap();
		HashMap map2 = new HashMap();
		HashMap mapQ = new HashMap();// 分季度已赚保费
		try {
			String line;
			// policy
			File policy_file = new File( file_path + "data/" + "policy.csv" );
			BufferedReader br = new BufferedReader(new FileReader(policy_file));
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
				String manageCom = row[0];
				String grpContNo = row[1];
				String gebClient = row[4];
				String riskCode = row[12];
				String amntStr = row[14];
				String premStr = row[15];
				String cValiDate = row[8];
				String cEndDate = row[9];
				String ccdate = row[10];
				String crdate = row[11];
				String deptID = row[16];
				if( cValiDate==null || "".equals(cValiDate) ){
					continue;
				}
				if( cEndDate==null || "".equals(cEndDate) ){
					continue;
				}
				if( ccdate==null || "".equals(ccdate) ){
					continue;
				}
				if( crdate==null || "".equals(crdate) ){
					continue;
				}
				// 6.30 yaobin 其他险种不在计算范围内
				String risks = "NIK01,NIK02,NIK12,NIK03,NIK07,NIK08,NIK10,NIK11,NIK09,MIK01,MIK02,MOK01,MKK01,NMK01,NMK08,NLK03,NLK01,NMK04,NMK02,NMK03,NMK09,NMK06,NMK07,NLK02,NL05,NAK01,NAK02,NAK03,MGK02,MGK03,MGK04,MGK01,MKK02,MMK01,MMK02";
				// 2011/10/13 +NMK06
				String regex = "^.*"+riskCode+".*$";
				if( !risks.matches(regex) ){
					continue;
				}
				// amnt
				double amnt = "".equals(amntStr)?0:Double.parseDouble(amntStr);
				// prem
				double prem = "".equals(premStr)?0:Double.parseDouble(premStr);
				// earned prem
				double earnedPrem = prem * this.getExposure(cValiDate, cEndDate, startDate, endDate);
				
				// map
				String key = manageCom+"#"+grpContNo+"#"+riskCode;
				Object o = map.get(key);
				if(o==null){
					map.put(key, new double[]{amnt,prem,earnedPrem});
				} else {
					double[] value = (double[])o;
					value[0] += amnt;
					value[1] += prem;
					value[2] += earnedPrem;
				}
				// map2
				map2.put(key, row);
				
				// earned prem by Q
//				if(earnedPrem<=0){
//					continue;
//				}
				String date1 = cValiDate;
				String date2 = null;
				java.text.DateFormat fmt =new java.text.SimpleDateFormat("yyyy-MM-dd");
				Date enddate = fmt.parse(cEndDate);
				Date d2;
				do{
					String date1Next = null;
					String[] s1 = date1.split("-");

					int y1 = Integer.parseInt(s1[0]);
					int m1 = Integer.parseInt(s1[1]);
					int q = 0;
					if( m1<=3 ){
						date1 = y1+"-1-1";
						date2 = y1+"-3-31";
						date1Next = y1+"-4-1";
						q = 1;
					} else if( m1<=6 ){
						date1 = y1+"-4-1";
						date2 = y1+"-6-30";
						date1Next = y1+"-7-1";
						q = 2;
					} else if( m1<=9 ){
						date1 = y1+"-7-1";
						date2 = y1+"-9-30";
						date1Next = y1+"-10-1";
						q = 3;
					} else if( m1<=12 ){
						date1 = y1+"-10-1";
						date2 = y1+"-12-31";
						date1Next = (y1+1)+"-1-1";
						q = 4;
					}
					
					double exposure = this.getExposure(cValiDate, cEndDate, date1, date2);

					double earned = prem * exposure;
					key = manageCom.substring(0, 4) + "," + gebClient + "," + deptID + ","+riskCode+","+y1+"Q"+q;
					System.out.println(key);
					o = mapQ.get(key);
					if(o==null){
						mapQ.put(key, new double[]{exposure, earned});
					} else {
						double[] value = (double[])o;
						value[0] += exposure;
						value[1] += earned;
					}
					date1 = date1Next;
				}while(enddate.compareTo(fmt.parse(date1))>0);
				
			}
			
			java.text.DecimalFormat df = new java.text.DecimalFormat( "#.##" );
			java.text.DecimalFormat df2 = new java.text.DecimalFormat( "#.######" );
			// export exposure_earned.csv
			StringBuffer buffer = new StringBuffer();
			Set keySet = mapQ.keySet();
			for(Iterator i = keySet.iterator(); i.hasNext(); ) {
				String key = (String)i.next();
				double[] value = (double[])mapQ.get(key);
				buffer.append(key).append(",").append( df2.format(value[0]) ).append(",").append( df2.format(value[1]) ).append("\r\n");
			}
			FileProcessor.outputFile(buffer.toString(), file_path+"data/", "exposure_earned", "csv");
			// export pol.csv
			buffer = new StringBuffer();
			keySet = map.keySet();
			for(Iterator i = keySet.iterator(); i.hasNext(); ) {
				String key = (String)i.next();
				double[] value = (double[])map.get(key);
				String[] row = (String[])map2.get(key);
				
				String manageCom = row[0];
				String branch = (manageCom != null && manageCom.length() >= 4) ?  manageCom.substring(0, 4) : "";
				String grpContNo = row[1];
				String appntName = row[3];
				String gebClient = row[4];
				
				String riskCode = row[12];
				String plan = this.getPlan(riskCode);
				String isMedical = this.getIsMedical(riskCode);
				String saleChnl = row[5];
				String agentCode = row[6];
				String deptID = row[16];
				String deptName = this.getDeptName(deptID);
				String commissionRate = row[13];
				String ccdate = row[10];
				String crdate = row[11];
				String repeatBill = row[2];
				//增加推动费率和保单套餐类型字段(ASR20123968 S-[程序修改]-理赔率分析报表) 20120917
				String tFeeRate = row[17];
				String polPackageFlag = row[18];
				
				buffer.append( manageCom ).append(",");
				buffer.append( branch ).append(",");
				buffer.append( grpContNo ).append(",");
				buffer.append( appntName ).append(",");
				buffer.append( gebClient ).append(",");
				buffer.append( plan ).append(",");
				buffer.append( riskCode ).append(",");
				buffer.append( isMedical ).append(",");
				buffer.append( saleChnl ).append(",");
				buffer.append( agentCode ).append(",");
				buffer.append( deptName ).append(",");
				buffer.append( commissionRate ).append(",");
				buffer.append( df.format(value[1]) ).append(",");
				buffer.append( df.format(value[2]) ).append(",");
				buffer.append( ccdate ).append(","); //CVALIDATE
				buffer.append( crdate ).append(","); //CENDDATE
				buffer.append( repeatBill ).append(","); //REPEATBILL
				//增加推动费率和保单套餐类型字段(ASR20123968 S-[程序修改]-理赔率分析报表) 20120917
				buffer.append( tFeeRate ).append(",");
				buffer.append( polPackageFlag );
				buffer.append("\r\n");
			}
			FileProcessor.outputFile(buffer.toString(), file_path+"data/", "pol", "csv");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public String[] getDelay( String date1, String date2, String endDate ){

		String[] s = new String[3];
		
		if( date1 == null || date2 == null || "".equals(date1) || "".equals(date2) ){
			return null;
		}
		try {
			String[] d1 = date1.split("-");
			String[] d2 = date2.split("-");
			int y1 = Integer.parseInt( d1[0] );
			int y2 = Integer.parseInt( d2[0] );
			int m1 = Integer.parseInt( d1[1] );
			int m2 = Integer.parseInt( d2[1] );
			
			int q1 = (m1-1) / 3 + 1;
			int q2 = (m2-1) / 3 + 1;
			
//			int l = (y2-y1)*4 + q2 - q1;
			String [] endD = endDate.split("-");
			
			int endYear = Integer.parseInt( endD[0] );
			int endMonth = Integer.parseInt( endD[1] );
			int endQuarter = (endMonth-1) / 3 + 1;
			int l = (endYear-y1)*4 + endQuarter - q1;
			if( l<0 ){
				return null;
			}
			if( l>18 ){
				l=18;
			}
			s[0] = d1[0] + "Q" + q1;
			s[1] = d2[0] + "Q" + q2;
			s[2] = Integer.toString(l);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return s;
	}
	
	
	
	public double getExposure( String cValiDate, String cEndDate, String startDate, String endDate ){
		
		Date cvalid;
		Date cendd;
		Date startd;
		Date endd;
		java.text.DateFormat fmt =new java.text.SimpleDateFormat("yyyy-MM-dd");
		try {
			cvalid = fmt.parse(cValiDate);
			cendd = fmt.parse(cEndDate);
			startd = fmt.parse(startDate);
			endd = fmt.parse(endDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
		double x = ((double)(((cendd.before(endd)?cendd:endd).getTime()-(cvalid.after(startd)?cvalid:startd).getTime())/(1000*60*60*24))+1);
		if(x<=0){
			return 0;
		}
		double y = (double)((cendd.getTime()-cvalid.getTime())/(1000*60*60*24)) + 1;
		if(y<=0){
			return 0;
		}
		return x/y;
	}
	
	public String getPlan( String riskCode ){
		if( "NIK01".equals(riskCode) ){
			return "CMM";
		}
		if( "NIK02".equals(riskCode) || "NIK12".equals(riskCode) ){
			return "SMM";
		}
		if( "NIK03".equals(riskCode) || "NIK07".equals(riskCode) || "NIK08".equals(riskCode) || "NIK09".equals(riskCode) || "NIK10".equals(riskCode) || "NIK11".equals(riskCode) ){
			return "WMP";
		}
		if( "MIK01".equals(riskCode) || "MOK01".equals(riskCode) || "MIK02".equals(riskCode) ){
			return "AMR";
		}
		if( "MKK01".equals(riskCode) ){
			return "HI";
		}
		if( "NMK01".equals(riskCode) || "NLK03".equals(riskCode) || "NLK01".equals(riskCode) || "NMK04".equals(riskCode) || "NMK06".equals(riskCode) || "NMK07".equals(riskCode) || "NMK08".equals(riskCode)){
			return "ADD";
		}
		if( "NMK02".equals(riskCode) || "NMK03".equals(riskCode) || "NLK02".equals(riskCode) || "NL05".equals(riskCode) || "NMK09".equals(riskCode) ){
			return "PCA";
		}
		if( "NAK01".equals(riskCode) || "NAK02".equals(riskCode) || "NAK03".equals(riskCode) ){
			return "TL";
		}
		if( "MGK02".equals(riskCode) || "MGK03".equals(riskCode) ){
			return "JDD";
		}
		if( "MGK04".equals(riskCode) ){
			return "FDD";
		}
		if( "MGK01".equals(riskCode) ){
			return "DD";
		}
		if( "MKK02".equals(riskCode) ){
			return "TPD";
		}
		if( "MMK01".equals(riskCode) || "MMK02".equals(riskCode) ){
			return "DI";
		}
		return "";
	}
	public String getIsMedical( String riskCode ){
		if( riskCode == null || "".equals(riskCode) ){
			return "";
		}
		//medical
		String mRiskCodes = "NIK01,NIK02,NIK12,NIK03,NIK07,NIK08,NIK10,NIK11,NIK09,MIK01,MIK02,MOK01,MKK01";
		//non-medical
		String nmRiskCodes = "NMK01,NMK08,NLK03,NLK01,NMK04,NMK02,NMK03,NMK09,NLK02,NL05,NAK01,NAK02,NAK03,MGK02,MGK03,MGK04,MGK01,MKK02,MMK01,MMK02,NMK06,NMK07"; //By Fang 20120112(增加NMK06的处理)
		String regex = "^.*"+riskCode+".*$";
		if( mRiskCodes.matches(regex) ){
			return "Y";
		}
		if( nmRiskCodes.matches(regex) ){
			return "N";
		}
		return "";
	}
	
	public String getDeptName( String deptID ){
		if("13".equals(deptID)){
			return "团险业务部";
		}
		if("14".equals(deptID)){
			return "关联业务部";
		}
		if("11".equals(deptID)){
			return "代理人营销部";
		}
		if("112".equals(deptID)){
			return "代理人营销部--业务行政中心";
		}
		if("12".equals(deptID)){
			return "兼业代理营销部";
		}
		if("38".equals(deptID)){
			return "人力资源部";
		}
		if("35".equals(deptID)){
			return "总经理办公室";
		}
		if("25".equals(deptID)){
			return "业务行政部 ";
		}
		return "";
	}
	
	// temp 2011/07/08 convert yaobin's PR1.csv to ds.csv
	public void convertDS(){
		ClaimReportWriter crw = new ClaimReportWriter();
		StringBuffer sb = new StringBuffer();
		String path = file_path+"data/" + "PR1.csv";
		
		sb.append(crw.COL_NAMES).append("\r\n");
		try {
        	File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line; 
			br.readLine();
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
				sb.append(row[0]).append(",");
				sb.append(row[0]).append(",");
				sb.append(row[1]).append(",");
				sb.append(row[2]).append(",");
				sb.append(row[9]).append(",");
				sb.append(row[3]).append(",");
				
				String riskCode = row[4];// riskcode
				sb.append(riskCode).append(",");
				
				sb.append(this.getIsMedical(riskCode)).append(",");// IsMedical
				sb.append(row[7]).append(",");
				sb.append("agentcode000").append(",");
				// deptname
				String deptID = row[5];
				sb.append(this.getDeptName(deptID)).append(",");
				sb.append(row[10]).append(",");
				sb.append(row[11]).append(",");
				sb.append(row[13]).append(",");
				sb.append(row[14]).append(",");
				sb.append("2011-09-01").append(",");// enddate
				sb.append("1").append(",");// repeatbill
				sb.append(row[12]).append(",");
				sb.append(row[16]).append(",");
				double newIbnr = Double.parseDouble(row[17]);
				double oldIbnr = Double.parseDouble(row[18]);
				sb.append(newIbnr-oldIbnr);
				sb.append("\r\n");
			}
			FileProcessor.outputFile(sb.toString(), file_path+"data/", "ds", "csv");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

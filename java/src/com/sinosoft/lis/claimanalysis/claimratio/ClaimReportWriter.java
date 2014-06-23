package com.sinosoft.lis.claimanalysis.claimratio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;

import com.sinosoft.lis.claimanalysis.common.FileProcessor;
import com.sinosoft.lis.claimanalysis.common.ReportWriter;

/* **************************************************************************************************************
 * Fang 20120312 
 * Change1、团险、关联拆分
 * **************************************************************************************************************
 **/
public class ClaimReportWriter {
	String file_path = "d:/ClaimAnal/";
	FileProcessor FileProcessor = new FileProcessor();
	ReportWriter ReportWriter = new ReportWriter();
	
	List sheets = new ArrayList();

	public String COL_NAMES = "MANAGECOM,BRANCH,GRPCONTNO,APPNTNAME,GEBCLIENT,PLAN,RISKCODE,ISMEDICAL,SALECHNL,AGENTCODE,DEPTNAME,COMMRATE,PREM,EARNEDPREM,CVALIDATE,CENDDATE,REPEATBILL,CLAIMTIMES,REALPAY,IBNR,OLDIBNR,NEWIBNR,TFEERATE,POLPACKAGEFLAG";
	
	String effectDate = ""; //By Fang for Change1 20120312(用于保费规模统计中的保单数统计)
	
	/**
	 * factor.csv + claim_ibnr.csv => NEW IBNR
	 * PR1.csv => OLD IBNR ( 上一评估期的IBNR )
	 * claim_realpay.csv => REALPAY
	 * pol.csv => 保费信息
	 * @output ds.csv 报表数据源
	 * @output ds_premsize.csv 用于计算保费规模
	 */
	public void exportDataSource(){
		// ibnr factor
		HashMap fcMap = new HashMap();
		String path = file_path+"data/" + "ibnr_factor.csv";
		try {
        	File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line; 
			br.readLine();
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
				if( row.length <2 ){
					continue;
				}
				double[] fc = new double[row.length-1];
				for(int i=0;i<fc.length;i++){
					fc[i] = Double.parseDouble( row[i+1] );
					System.out.println(row[0] +" (" +i+") "+fc[i]);
				}
				fcMap.put(row[0], fc);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// new ibnr
		HashMap newIbnrMap = new HashMap();
		path = file_path+"data/" + "claim_ibnr.csv";
		//By Fang 20120111
		StringBuffer grp_ibnr = new StringBuffer();
		//End 201120111
		try {
        	File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line; 
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
				String grpContNo = row[1];
				String riskCode = row[2];
				String type = row[5];
				Object o = fcMap.get( type );
				double ibnr = 0;
				if( o != null ){
					double[] fc = (double[]) o;
					int delay = Integer.parseInt( row[6] );
					double realpay = Double.parseDouble( row[7] );
					double factor = fc[delay];
					ibnr = realpay/factor - realpay;
				}
				//By Fang 20120111
				grp_ibnr.append(line).append(",").append(ibnr).append("\r\n");
				//End 201120111
				String key = grpContNo+"#"+riskCode;
				o = newIbnrMap.get(key);
				if( o == null ){
					newIbnrMap.put(key, new Double(ibnr));
				} else {
					Double value = (Double)o;
					newIbnrMap.put(key, new Double( value.doubleValue() + ibnr ));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// old ibnr 
		HashMap oldIbnrMap = new HashMap();
		path = file_path+"data/" + "PR1.csv";
		try {
        	File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			br.readLine();//first line
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
//				String grpContNo = row[1];
//				String riskCode = row[5];
//				String ibnr = row[17];
				String grpContNo = row[0];
				String riskCode = row[1];
				String ibnr = row[2];
				if( "".equals(grpContNo) || "".equals(riskCode) || "".equals(ibnr) || "0".equals(ibnr) ){
					continue;
				}
				String key = grpContNo+"#"+riskCode;
				oldIbnrMap.put(key, new Double(ibnr));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// realpay
		HashMap realpayMap = new HashMap();
		path = file_path+"data/" + "claim_realpay.csv";
		try {
        	File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line; 
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
				String grpContNo = row[1];
				String riskCode = row[2];
				String realPay = row[3];
				String times = row[4];
				if( "".equals(grpContNo) || "".equals(riskCode) ){
					continue;
				}
				String key = grpContNo+"#"+riskCode;
				realpayMap.put(key, times+","+realPay);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// match
		StringBuffer buffer = new StringBuffer();
		buffer.append(COL_NAMES).append("\r\n");
		path = file_path+"data/" + "pol.csv";
		try {
        	File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line; 
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
				String grpContNo = row[2];
				String riskCode = row[6];
				String key = grpContNo+"#"+riskCode;
				
				String realPay = "0,0"; // times,realpay
				double ibnr = 0;
				double newibnr = 0;
				double oldibnr = 0;
				// realpay
				Object o = realpayMap.get( key );
				if( o != null ) {
					realPay = (String)o;
					realpayMap.remove(key);
				}
				// new ibnr
				o = newIbnrMap.get( key );
				if( o != null ) {
					newibnr = ((Double)o).doubleValue();
					newIbnrMap.remove(key);
				}
				// old ibnr
				o = oldIbnrMap.get( key );
				if( o != null ) {
					oldibnr = ((Double)o).doubleValue();
					oldIbnrMap.remove(key);
				}
				ibnr = newibnr - oldibnr;
				//增加推动费率和保单套餐类型字段(ASR20123968 S-[程序修改]-理赔率分析报表) 20120917
				//buffer.append(line).append(",").append(realPay).append(",").append(ibnr).append(",").append(oldibnr).append(",").append(newibnr).append("\r\n");
				for(int mm=0; mm<row.length-2; mm++)
				{
					buffer.append(row[mm]).append(",");
				}
				//将新增的字段放在ds文件最后，这样不用修改后续报表模板
				buffer.append(realPay).append(",").append(ibnr).append(",").append(oldibnr).append(",").append(newibnr).append(",");
				buffer.append(row[17]).append(",").append(row[18]).append("\r\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		StringBuffer mis = new StringBuffer();
		Set keys = realpayMap.keySet();
		for(Iterator i = keys.iterator(); i.hasNext();){
			String key = (String)i.next();
//			double[] value = (double[]) map.get(key);
			mis.append(key).append("\r\n");
		}
		FileProcessor.outputFile(grp_ibnr.toString(), file_path+"data/", "grp_ibnr", "csv"); //By Fang 20120111
		FileProcessor.outputFile(buffer.toString(), file_path+"data/", "ds", "csv");
		FileProcessor.outputFile(mis.toString(), file_path+"data/", "mis", "csv");
		
		exportPremSizeDS();
	}
	
	public void exportPremSizeDS(){
		String dataSource = file_path + "data/" + "ds.csv";
		String[] columnNames = COL_NAMES.split(",");
		String jasper_premsize = file_path + "jasper/" + "PREMSIZE_DS.jasper";
		String dataSource_premsize = file_path + "data/" + "ds_premsize.csv";
		HashMap params = new HashMap();
		ReportWriter.exportCsvReport(dataSource, columnNames, jasper_premsize, params, dataSource_premsize, true);
	}
	
	public void writeReports(int effectYear, String runoffLine){
		outputGC( effectYear );
		outputRunOff(runoffLine, effectYear-3, effectYear-1);
		outputBranch( effectYear );
		
		String newFile = file_path + "data/report.xls";
		FileProcessor.mergeXls(sheets, newFile);

	}
	
	public void outputGC( int effectYear ){
		String zhName;
		String enName;
		String startDateStr;
		String endDateStr;
		String dept;
		String report;
		String sheetName;
		effectDate = (effectYear-1)+"-12-31"; 
		/**
		 * Overall
		 */
		// GC
		zhName = "中意人寿团体短期险理赔率分析－－整体分析";
		enName = "Claim Analysis of Group Short Term Business -- Overall Analysis";
		startDateStr = "2000-01-01";
		endDateStr = "3000-01-01";
		dept = "全部";
		sheetName = "GC";
		this.useGcJasper(zhName, enName, startDateStr, endDateStr, dept, sheetName);
		/**
		 * Group in 2011
		 */
		//Group 2011 - GC
		zhName = "团险－－"+effectYear+"年生效保单分析";
		enName = "Group --  Analysis For Policies Took Effect in "+effectYear;
		startDateStr = (effectYear-1)+"-12-31";
		endDateStr = "3000-01-01";
		//By Fang for Change1 20120313
		//dept = "团险业务部";
		dept = "全部";
		sheetName = "Group "+effectYear;
		
		this.useGcJasper(zhName, enName, startDateStr, endDateStr, dept, sheetName);
		/**
		 * Group before 2011
		 */
		// Group Other - GC
		zhName = "团险－－其他保单分析";
		enName = "Group -- Analysis For Policies Took Effect before "+effectYear;
		startDateStr = "2000-01-01";
		endDateStr = effectYear+"-01-01";
		//By Fang for Change1 20120313
		//dept = "团险业务部";
		dept = "全部";
		sheetName = "Group Other";
		this.useGcJasper(zhName, enName, startDateStr, endDateStr, dept, sheetName);

	}
	
	public void useGcJasper( String zhName, String enName, String startDateStr, String endDateStr, String dept, String sheetName ){
		String dataSource = file_path + "data/" + "ds.csv";
		String[] columnNames = COL_NAMES.split(",");
		String dataSource_premsize = file_path + "data/" + "ds_premsize.csv";
		String[] columnNames_premsize = new String[]{"BRANCH","DEPTNAME","GRPCONTNO","GEBCLIENT","EARNEDPREM","REALPAY","IBNR","PREM","CVALIDATE","CENDDATE"};
		//jasper
		String jasper1 = file_path + "jasper/" + "GC1.jasper";
		String jasper2 = file_path + "jasper/" + "GC2.jasper";
		String jasper3 = file_path + "jasper/" + "GC3.jasper";
		String jasper4 = file_path + "jasper/" + "GC4.jasper";
		String jasper5 = file_path + "jasper/" + "GC5.jasper"; //By Fang 总公司各险种的理赔情况(20130620)
		String jasper6 = file_path + "jasper/" + "GC6.jasper"; //By Fang 总公司保费规模理赔情况(20130620)
		String jasper7 = file_path + "jasper/" + "GC7.jasper"; //By Fang 总公司各销售渠道的理赔情况(20130621)
		//params
		HashMap params = new HashMap();

		//date
		java.text.DateFormat fmt =new java.text.SimpleDateFormat("yyyy-MM-dd");
		try {
			Date startDate = fmt.parse(startDateStr);
			Date endDate = fmt.parse(endDateStr);
			params.put("STARTDATE", startDate);
			params.put("ENDDATE", endDate);
			params.put("EFFECTDATE", fmt.parse(effectDate)); //By Fang 20130621(用于保费规模统计中的保单数统计)
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//dept
		params.put("DEPTNAME", dept);
		//title
		params.put("NAME", zhName);
		params.put("NAME_EN", enName);
		
		String report1 = file_path + "data/" + sheetName + "_1.xls";
		String report2 = file_path + "data/" + sheetName + "_2.xls";
		String report3 = file_path + "data/" + sheetName + "_3.xls";
		String report4 = file_path + "data/" + sheetName + "_4.xls";
		String report5 = file_path + "data/" + sheetName + "_5.xls"; //By Fang 总公司各险种的理赔情况(20130620)
		String report6 = file_path + "data/" + sheetName + "_6.xls"; //By Fang 总公司保费规模理赔情况(20130620)
		String report7 = file_path + "data/" + sheetName + "_7.xls"; //By Fang 总公司各销售渠道的理赔情况(20130621)
		ReportWriter.exportOneSheetXlsReport(columnNames, dataSource, params, jasper1, sheetName, report1, true);
		ReportWriter.exportOneSheetXlsReport(columnNames, dataSource, params, jasper2, sheetName, report2, true);
		ReportWriter.exportOneSheetXlsReport(columnNames, dataSource, params, jasper3, sheetName, report3, true);
		ReportWriter.exportOneSheetXlsReport(columnNames, dataSource, params, jasper4, sheetName, report4, true);
		ReportWriter.exportOneSheetXlsReport(columnNames, dataSource, params, jasper5, sheetName, report5, true); //By Fang 总公司各险种的理赔情况(20130620)
		ReportWriter.exportOneSheetXlsReport(columnNames_premsize, dataSource_premsize, params, jasper6, sheetName, report6, true); //By Fang 总公司保费规模理赔情况(20130620)
		ReportWriter.exportOneSheetXlsReport(columnNames, dataSource, params, jasper7, sheetName, report7, true); //By Fang 总公司各销售渠道的理赔情况(20130621)
		
		List files = new ArrayList();
		files.add(report1);
		files.add(report2);
		files.add(report3);
		files.add(report4);
		files.add(report5); //By Fang 总公司各险种的理赔情况(20130620)
		files.add(report6); //By Fang 总公司保费规模理赔情况(20130620)
		files.add(report7); //By Fang 总公司各销售渠道的理赔情况(20130621)

		String newFile = file_path + "data/" + sheetName + ".xls";
		FileProcessor.mergeXlsToOneSheet(files, newFile, sheetName);
		sheets.add(newFile);
	}
	
	public void useGcJasperBak( String zhName, String enName, String startDateStr, String endDateStr, String dept, String report ){
		String dataSource = file_path + "data/" + "ds.csv";
		String[] columnNames = COL_NAMES.split(",");
		//jasper
		String jasper = file_path + "jasper/" + "GC.jasper";
		//params
		HashMap params = new HashMap();
		//1.datasource
		try {
			JRDataSource ds1 = ReportWriter.getCsvDataSource(columnNames, dataSource, true);
			JRDataSource ds2 = ReportWriter.getCsvDataSource(columnNames, dataSource, true);
			JRDataSource ds3 = ReportWriter.getCsvDataSource(columnNames, dataSource, true);
			JRDataSource ds4 = ReportWriter.getCsvDataSource(columnNames, dataSource, true);
			params.put("DS1", ds1);
			params.put("DS2", ds2);
			params.put("DS3", ds3);
			params.put("DS4", ds4);
		} catch (JRException e1) {
			e1.printStackTrace();
		}

		//2.date
		java.text.DateFormat fmt =new java.text.SimpleDateFormat("yyyy-MM-dd");
		try {
			Date startDate = fmt.parse(startDateStr);
			Date endDate = fmt.parse(endDateStr);
			params.put("STARTDATE", startDate);
			params.put("ENDDATE", endDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//3.dept
		params.put("DEPTNAME", dept);
		//4.title
		params.put("NAME", zhName);
		params.put("NAME_EN", enName);
		ReportWriter.exportCsvReport(dataSource, columnNames, jasper, params, report);
	}

	public void outputBranch( int effectYear ){
		String[] branchCode = new String[]{"8601","8602","8603","8604","8605","8606","8607","8608","8609","8610"};
		String[] branchAbbName = new String[]{"BJ","GD","SH","JS","SZ","LN","SC","SX","SD","HLJ"};
		String[] branchCnName = new String[]{"北京","广东","上海","江苏","深圳","辽宁","四川","陕西","山东","黑龙江"};
		String[] branchEnName = new String[]{"Beijing","Guangdong","Shanghai","Jiangsu","Shenzhen","Liaoning","Sichuan","Shanxi","Shandong","Heilongjiang"};
		String zhName;
		String enName;
		String dept;
		String branch;
		effectDate = (effectYear-1)+"-12-31"; //BY Fang for Change1 20120312(用于保费规模统计中的保单数统计) 
		
		/**
		 * 1.StartDate~EndDate
		 */
		String zhTitle1 = "中意人寿团体短期险理赔率分析 -- ";
		String enTitle1 = "Claim Analysis of Group Short Term Business -- ";
		String startDateStr1 = "2000-01-01";
		String endDateStr1 = "3000-01-01";
		
		/**
		 * 2.in 2011
		 */
		String zhTitle2 = " -- "+effectYear+"年生效保单分析";
		String enTitle2 = " -- Analysis For Policies Took Effect in "+effectYear;
		String startDateStr2 = (effectYear-1)+"-12-31";
		String endDateStr2 = "3000-01-01";
		/**
		 * 3.before 2011
		 */
		String zhTitle3 = " -- 其他保单分析";
		String enTitle3 = " -- Analysis For Policies Took Effect before "+effectYear;
		String startDateStr3 = "2000-01-01";
		String endDateStr3 = effectYear+"-01-01";
		
		//Captive
		zhName = "关联业务";
		enName = "Captive";
		dept = "关联业务部";
		//By Fang for Change1 20120312(Captive，Captive2011和Captive other应该为北京关联)
		//branch = "86";
		branch = "8601";
		this.useBranchJasper(zhTitle1+zhName, enTitle1+enName, startDateStr1, endDateStr1, dept, branch, "Captive");
		this.useBranchJasper(zhName+zhTitle2, enName+enTitle2, startDateStr2, endDateStr2, dept, branch, "Captive "+effectYear);
		this.useBranchJasper(zhName+zhTitle3, enName+enTitle3, startDateStr3, endDateStr3, dept, branch, "Captive Other");
		//Branch
		for(int i=0;i<branchCode.length;i++){
			zhName = branchCnName[i];
			enName = branchEnName[i];
			//By Fang for Change1 20120313
			//dept = "团险业务部";
			branch = branchCode[i];
			if(branch.equals("8601"))
			{
				dept = "团险业务部";
			}else
			{
				dept = "全部";
			}
			//Ended for Change1 20120313
			this.useBranchJasper(zhTitle1+zhName, enTitle1+enName, startDateStr1, endDateStr1, dept, branch, branchAbbName[i]);
			this.useBranchJasper(zhName+zhTitle2, enName+enTitle2, startDateStr2, endDateStr2, dept, branch, branchAbbName[i]+" "+effectYear);
			this.useBranchJasper(zhName+zhTitle3, enName+enTitle3, startDateStr3, endDateStr3, dept, branch, branchAbbName[i]+" Other");
		}
		
	}
	
	public void useBranchJasper( String zhName, String enName, String startDateStr, String endDateStr, String dept, String branch, String sheetName ){
		String dataSource = file_path + "data/" + "ds.csv";
		String[] columnNames = COL_NAMES.split(",");
		String dataSource_premsize = file_path + "data/" + "ds_premsize.csv";
		String[] columnNames_premsize = new String[]{"BRANCH","DEPTNAME","GRPCONTNO","GEBCLIENT","EARNEDPREM","REALPAY","IBNR","PREM","CVALIDATE","CENDDATE"};
		
		//jasper
		String jasper1 = file_path + "jasper/" + "BRANCH1.jasper";
		if("关联业务部".equals(dept)){
			jasper1 = file_path + "jasper/" + "BRANCH1_CAPTIVE.jasper";
		}
		String jasper2 = file_path + "jasper/" + "BRANCH2.jasper";
		String jasper3 = file_path + "jasper/" + "BRANCH3.jasper";
		String jasper4 = file_path + "jasper/" + "BRANCH4.jasper";
		//params
		HashMap params = new HashMap();

		//date
		java.text.DateFormat fmt =new java.text.SimpleDateFormat("yyyy-MM-dd");
		try {
			Date startDate = fmt.parse(startDateStr);
			Date endDate = fmt.parse(endDateStr);
			params.put("STARTDATE", startDate);
			params.put("ENDDATE", endDate);
			params.put("EFFECTDATE", fmt.parse(effectDate)); //By Fang for Change1 20120312(用于保费规模统计中的保单数统计)
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//dept
		params.put("DEPTNAME", dept);
		//title
		params.put("NAME", zhName);
		params.put("NAME_EN", enName);
		params.put("BRANCH", branch);
		
		String report1 = file_path + "data/" + sheetName + "_1.xls";
		String report2 = file_path + "data/" + sheetName + "_2.xls";
		String report3 = file_path + "data/" + sheetName + "_3.xls";
		String report4 = file_path + "data/" + sheetName + "_4.xls";
		ReportWriter.exportOneSheetXlsReport(columnNames, dataSource, params, jasper1, sheetName, report1, true);
		ReportWriter.exportOneSheetXlsReport(columnNames, dataSource, params, jasper2, sheetName, report2, true);
		ReportWriter.exportOneSheetXlsReport(columnNames_premsize, dataSource_premsize, params, jasper3, sheetName, report3, true);
		ReportWriter.exportOneSheetXlsReport(columnNames, dataSource, params, jasper4, sheetName, report4, true);
		
		List files = new ArrayList();
		files.add(report1);
		files.add(report2);
		files.add(report3);
		files.add(report4);

		String newFile = file_path + "data/" + sheetName + ".xls";
		FileProcessor.mergeXlsToOneSheet(files, newFile, sheetName);
		sheets.add(newFile);
	}
	
	public void useBranchJasperBak( String zhName, String enName, String startDateStr, String endDateStr, String dept, String branch, String report ){
		String dataSource = file_path + "data/" + "ds.csv";
		String[] columnNames = COL_NAMES.split(",");
		String dataSource_premsize = file_path + "data/" + "ds_premsize.csv";
		String[] columnNames_premsize = new String[]{"BRANCH","DEPTNAME","GRPCONTNO","GEBCLIENT","EARNEDPREM","REALPAY","IBNR","PREM","CVALIDATE"};
		
		//jasper
		String jasper = file_path + "jasper/" + "BRANCH.jasper";
		//params
		HashMap params = new HashMap();
		//1.datasource
		try {
			JRDataSource ds1 = ReportWriter.getCsvDataSource(columnNames, dataSource, true);
			JRDataSource ds2 = ReportWriter.getCsvDataSource(columnNames, dataSource, true);
			JRDataSource ds3 = ReportWriter.getCsvDataSource(columnNames_premsize, dataSource_premsize, true);
			JRDataSource ds4 = ReportWriter.getCsvDataSource(columnNames, dataSource, true);
			params.put("DS1", ds1);
			params.put("DS2", ds2);
			params.put("DS3", ds3);
			params.put("DS4", ds4);
		} catch (JRException e1) {
			e1.printStackTrace();
		}
		//2.date
		java.text.DateFormat fmt =new java.text.SimpleDateFormat("yyyy-MM-dd");
		try {
			Date startDate = fmt.parse(startDateStr);
			Date endDate = fmt.parse(endDateStr);
			params.put("STARTDATE", startDate);
			params.put("ENDDATE", endDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//3.dept
		params.put("DEPTNAME", dept);
		//4.title
		params.put("NAME", zhName);
		params.put("NAME_EN", enName);
		//5.branch
		params.put("BRANCH", branch);
		ReportWriter.exportCsvReport(dataSource, columnNames, jasper, params, report);
	}
	public void overwriteGeb(){
		try {
			String line;
			File claim_file = new File( file_path + "data/" + "claim1.csv" );
			BufferedReader br = new BufferedReader(new FileReader(claim_file));
			
			StringBuffer sb = new StringBuffer();
			int i = 0;
			int j = 0;
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
				String grpContNo = row[1];
				if( "880002622".equals(grpContNo) || 
						"880005282".equals(grpContNo) || 
						"83000330800".equals(grpContNo) || 
						"86200058903".equals(grpContNo) || 
						"86200059203".equals(grpContNo) || 
						"86200060803".equals(grpContNo) || 
						"86200060804".equals(grpContNo) || 
						"88000068000".equals(grpContNo) || 
						"83000526001".equals(grpContNo) )
				{
					line = line.replaceFirst(grpContNo+",1", grpContNo+",2");
				}
				sb.append(line).append("\r\n");
				if(++i==100000){
					FileProcessor.outputFile(sb.toString(), file_path+"data/", "claim_"+j++, "csv");
					sb = new StringBuffer();
					i = 0;
				}
			}
			if( i>0 ){
				FileProcessor.outputFile(sb.toString(), file_path+"data/", "claim_"+j, "csv");
			} else {
				j--;
			}
			FileProcessor.mergeFiles(file_path+"data/", "claim", 0, j, "csv", "claim", true);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void overwriteGeb2(){
		try {
			String line;
			File policy_file = new File( file_path + "data/" + "policy1.csv" );
			BufferedReader br = new BufferedReader(new FileReader(policy_file));
			
			StringBuffer sb = new StringBuffer();
			int i = 0;
			int j = 0;
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
				String grpContNo = row[1];
				if( "880002622".equals(grpContNo) || 
						"880005282".equals(grpContNo) || 
						"83000330800".equals(grpContNo) || 
						"86200058903".equals(grpContNo) || 
						"86200059203".equals(grpContNo) || 
						"86200060803".equals(grpContNo) || 
						"86200060804".equals(grpContNo) || 
						"88000068000".equals(grpContNo) || 
						"83000526001".equals(grpContNo) )
				{
					line = line.replaceFirst(row[1]+","+row[2]+","+row[3]+","+row[4], row[1]+","+row[2]+","+row[3]+",2");
				}
				sb.append(line).append("\r\n");
				if(++i==100000){
					FileProcessor.outputFile(sb.toString(), file_path+"data/", "policy_"+j++, "csv");
					sb = new StringBuffer();
					i = 0;
				}
			}
			if( i>0 ){
				FileProcessor.outputFile(sb.toString(), file_path+"data/", "policy_"+j, "csv");
			} else {
				j--;
			}
			FileProcessor.mergeFiles(file_path+"data/", "policy", 0, j, "csv", "policy", true);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void outputRunOff( String runoffLine, int startYear, int endYear ){
//		java.text.DecimalFormat df = new java.text.DecimalFormat( "#" );
//		java.text.DecimalFormat df2 = new java.text.DecimalFormat( "#%" );
		java.text.DecimalFormat df = new java.text.DecimalFormat( "#.00" );
		java.text.DecimalFormat df2 = new java.text.DecimalFormat( "#.00%" );
		try {
			SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd");
			Date runoffDate = dateFm.parse( runoffLine );
			
			String line;
			HashMap branchMap = new HashMap();
			// claim
			HashMap claimMap = new HashMap();
			File claim_file = new File( file_path + "data/" + "claim.csv" );
			BufferedReader br = new BufferedReader(new FileReader(claim_file));
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
				String manageCom = row[0];
				String gebFlag = "1".equals(row[2]) ? "geb" : "nongeb";
				String riskCode = row[9];
				String realPayStr = row[11];
				String dept = row[13];
				String cvalidate = row[3];
				String enddate = row[4];
				//
				if( "0".equals( realPayStr ) || "".equals( realPayStr ) ){
					continue;
				}
				if( "".equals(cvalidate) || "".equals(enddate) ){
					continue;
				}
				
				int year = Integer.parseInt( cvalidate.substring(0, 4) );
				if( year < startYear || year > endYear ){
					continue;
				}
				Date endD = dateFm.parse( enddate );
				if( endD.after(runoffDate) ){
					continue;
				}
				
				String riskType = this.getRiskType(riskCode);
				if( riskType == null ){
					continue;
				}
				
				double realPay = Double.parseDouble(realPayStr);
				String key;
				if( "14".equals(dept) ){
					key = "14";
				} else {
					key = manageCom.substring(0, 4);
				}
				key = key + "#" + riskType + "#" + gebFlag + "#" + year;
				Object o = claimMap.get(key);
				if( o == null ){
					claimMap.put(key, new Double(realPay));
				} else {
					Double value = (Double)o;
					claimMap.put(key, new Double( value.doubleValue() + realPay ));
				}
			}
			// policy
			HashMap policyMap = new HashMap();
			File policy_file = new File( file_path + "data/" + "policy.csv" );
			br = new BufferedReader(new FileReader(policy_file));
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
				String manageCom = row[0];
				String gebFlag = "1".equals(row[4]) ? "geb" : "nongeb";
				String cvalidate = row[10];
				String enddate = row[11];
				String riskCode = row[12];
				String premStr = row[15];
				String dept = row[16];
				//
				if( "".equals(cvalidate) || "".equals(enddate) ){
					continue;
				}
				
				int year = Integer.parseInt( cvalidate.substring(0, 4) );
				if( year < startYear || year > endYear ){
					continue;
				}
				Date endD = dateFm.parse( enddate );
				if( endD.after(runoffDate) ){
					continue;
				}
				
				String riskType = this.getRiskType(riskCode);
				if( riskType == null ){
					continue;
				}
				
				double prem = Double.parseDouble(premStr);
				String key;
				if( "14".equals(dept) ){
					key = "14";
				} else {
					key = manageCom.substring(0, 4);
				}
				branchMap.put(key, "");
				key = key + "#" + riskType + "#" + gebFlag + "#" + year;
				Object o = policyMap.get(key);
				if( o == null ){
					policyMap.put(key, new Double(prem));
				} else {
					Double value = (Double)o;
					policyMap.put(key, new Double( value.doubleValue() + prem ));
				}
			}
			

			List branchList = new ArrayList(branchMap.keySet());
			Collections.sort(branchList);

			StringBuffer table1 = new StringBuffer();
			StringBuffer table2 = new StringBuffer();
			StringBuffer table3 = new StringBuffer();
			StringBuffer table4 = new StringBuffer(); //By Fang for ASR20125107(20121220)
			
			// table head
			table1.append(startYear).append("-01-01 至 ").append(runoffLine).append("已结束保单的理赔情况,,,,,,,\r\n");
			table1.append("claim ratios by run-off policies,,,,,,,\r\n");
			table1.append("分公司 branch,Non-Geb,,,Geb,,,合计total\r\n");
			table1.append(",保费 premium,理赔 claim,理赔率 claim ratio,保费 premium,理赔 claim,理赔率 claim ratio,\r\n");
			
			table2.append(",,,,,,,\r\n");
			table2.append(startYear).append("-01-01 至 ").append(runoffLine).append("已结束保单医疗险的理赔情况,,,,,,,\r\n");
			table2.append("claim ratios of medical products by run-off policies,,,,,,,\r\n");
			table2.append("分公司 branch,Non-Geb,,,Geb,,,合计total\r\n");
			table2.append(",保费 premium,理赔 claim,理赔率 claim ratio,保费 premium,理赔 claim,理赔率 claim ratio,\r\n");
			
			table3.append("\r\n");
			table3.append("已结束保单中各保单年度的理赔率claim ratio by policy's year\r\n");
			
			//By Fang for ASR20125107(20121220)
			table4.append("\r\n");
			table4.append("已结束保单中各保单年度的医疗险理赔率claim ratio of medical products by policy's year\r\n");
			
			String line1 = "分公司 branch";
			String line2 = "";
			for( int year=startYear; year<=endYear; year++ ){
				line1 = line1 + "," + year +",,";
				line2 = line2 + ",保费 premium,理赔 claim,理赔率 claim ratio";
			}
			table3.append(line1).append("\r\n");
			table3.append(line2).append("\r\n");
			//By Fang for ASR20125107(20121220)
			table4.append(line1).append("\r\n");
			table4.append(line2).append("\r\n");
			
			for( int i=0; i<branchList.size(); i++ ){
				String branchCode = (String)branchList.get(i);
				String branchName;
				if( "14".equals(branchCode) ){
					branchName = "关联业务部Captive";
				} else if( "8601".equals(branchCode) ){
					branchName = "北京Beijing";
				} else if( "8602".equals(branchCode) ){
					branchName = "广东Guangdong";
				} else if( "8603".equals(branchCode) ){
					branchName = "上海Shanghai";
				} else if( "8604".equals(branchCode) ){
					branchName = "江苏Jiangsu";
				} else if( "8605".equals(branchCode) ){
					branchName = "深圳Shenzhen";
				} else if( "8606".equals(branchCode) ){
					branchName = "辽宁Liaoning";
				} else if( "8607".equals(branchCode) ){
					branchName = "四川Sichuan";
				} else if( "8608".equals(branchCode) ){
					branchName = "陕西Shanxi";
				} else if( "8609".equals(branchCode) ){
					branchName = "山东Shandong";
				} else if( "8610".equals(branchCode) ){
					branchName = "黑龙江Heilongjiang";
				} else {
					continue;
				}
				double sumPrem1 = 0;
				double sumRealpay1 = 0;
				double sumPrem2 = 0;
				double sumRealpay2 = 0;
				double medPrem1 = 0;
				double medRealpay1 = 0;
				double medPrem2 = 0;
				double medRealpay2 = 0;
				
				table3.append(branchName);
				table4.append(branchName); //By Fang for ASR20125107(20121220)
				for( int year=startYear; year<=endYear; year++ ){
					double yearPrem = 0;
					double yearRealpay = 0;
					double prem;
					double realpay;
					//By Fang for ASR20125107(20121220)
					double medPrem3 = 0;
					double medRealpay3 = 0;
					String key;
					
					// medical
					key = branchCode + "#medical#geb#" + year;
					prem = this.getValue(key, policyMap);
					realpay = this.getValue(key, claimMap);
					sumPrem1 += prem;
					sumRealpay1 += realpay;
					medPrem1 += prem;
					medRealpay1 += realpay;
					yearPrem += prem;
					yearRealpay += realpay;
					//By Fang for ASR20125107(20121220)
					medPrem3 += prem;
					medRealpay3 += realpay;
					
					key = branchCode + "#medical#nongeb#" + year;
					prem = this.getValue(key, policyMap);
					realpay = this.getValue(key, claimMap);
					sumPrem2 += prem;
					sumRealpay2 += realpay;
					medPrem2 += prem;
					medRealpay2 += realpay;
					yearPrem += prem;
					yearRealpay += realpay;
					//By Fang for ASR20125107(20121220)
					medPrem3 += prem;
					medRealpay3 += realpay;
					
					// nonmedical
					key = branchCode + "#nonmedical#geb#" + year;
					prem = this.getValue(key, policyMap);
					realpay = this.getValue(key, claimMap);
					sumPrem1 += prem;
					sumRealpay1 += realpay;
					yearPrem += prem;
					yearRealpay += realpay;
					
					key = branchCode + "#nonmedical#nongeb#" + year;
					prem = this.getValue(key, policyMap);
					realpay = this.getValue(key, claimMap);
					sumPrem2 += prem;
					sumRealpay2 += realpay;
					yearPrem += prem;
					yearRealpay += realpay;
					
					//
					table3.append(",").append(df.format(yearPrem)).append(",").append(df.format(yearRealpay)).append(",").append(df2.format(yearRealpay/yearPrem));
					//By Fang for ASR20125107(20121220)
					table4.append(",").append(df.format(medPrem3)).append(",").append(df.format(medRealpay3)).append(",").append(df2.format(medRealpay3/medPrem3));
				}
				table3.append("\r\n");
				table4.append("\r\n"); //By Fang for ASR20125107(20121220)
				
				table1.append(branchName);
				table1.append(",").append(df.format(sumPrem2)).append(",").append(df.format(sumRealpay2)).append(",").append(df2.format(sumRealpay2/sumPrem2));
				table1.append(",").append(df.format(sumPrem1)).append(",").append(df.format(sumRealpay1)).append(",").append(df2.format(sumRealpay1/sumPrem1));
				table1.append(",").append(df2.format( (sumRealpay1+sumRealpay2)/(sumPrem1+sumPrem2) ));
				table1.append("\r\n");
				
				table2.append(branchName);
				table2.append(",").append(df.format(medPrem2)).append(",").append(df.format(medRealpay2)).append(",").append(df2.format(medRealpay2/medPrem2));
				table2.append(",").append(df.format(medPrem1)).append(",").append(df.format(medRealpay1)).append(",").append(df2.format(medRealpay1/medPrem1));
				table2.append(",").append(df2.format( (medRealpay1+medRealpay2)/(medPrem1+medPrem2) ));
				table2.append("\r\n");
			}
			FileProcessor.outputFile(table1.toString()+table2.toString()+table3.toString()+table4.toString(), file_path+"data/", "Run-off Analysis", "csv");
			List files = new ArrayList();
			List sheetNames = new ArrayList();
			files.add(file_path+"data/Run-off Analysis.csv");
			sheetNames.add("Run-off Analysis");
			String newFile = file_path+"data/Run-off Analysis.xls";
			FileProcessor.mergeCsvToXls(files, sheetNames, newFile);
			sheets.add(newFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	// risktype medical/nonmedical/null(不再计算范围内)
	public String getRiskType(String riskCode){
		String medicalRisk = "NIK01,NIK02,NIK12,NIK03,NIK07,NIK08,NIK10,NIK11,NIK09,MIK01,MIK02,MOK01,MKK01"; //By fang 20121009(增加险种MIK02)
		String nonmedicalRisk = "NMK01,NMK08,NLK03,NLK01,NMK04,NMK02,NMK03,NMK09,NMK06,NMK07,NLK02,NL05,NAK01,NAK03,NAK02,MGK02,MGK03,MGK04,MGK01,MKK02,MMK01,MMK02"; //By fang 20121009(增加险种NMK07)
		String regex = "^.*"+riskCode+".*$";
		if( medicalRisk.matches(regex) ){
			return "medical";
		} else if( nonmedicalRisk.matches(regex) ){
			return "nonmedical";
		} else {
			return null;
		}
	}
	
	public double getValue( String key, HashMap map ){
		Object o = map.get(key);
		if( o == null ){
			return 0;
		} else {
			return ((Double)o).doubleValue();
		}
	}
	
}

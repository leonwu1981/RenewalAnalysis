package com.sinosoft.lis.claimanalysis.claimratio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sinosoft.lis.claimanalysis.common.FileProcessor;
import com.sinosoft.lis.claimanalysis.common.ReportWriter;

public class IbnrFactorCalculator {
	String file_path = "d:/ClaimAnal/";
	String startDate = "2008-01-01";
	String endDate = "2011-06-30";
	FileProcessor FileProcessor = new FileProcessor();
	ReportWriter ReportWriter = new ReportWriter();
	
	
	String[] title;
	double[][] data;
	double[] ibnrFactor;
	double[] incurredClaims;
	double[] ibnr;
	double[] remainingRes;
	double[] ptdClaims;
	double[] restatedRes;
	double[] percents;
	int mPeriod=4;// Q.Quarter 4 M.Month 12
	HashMap ibnrMap = null;
	
	public boolean calIbnrFactors(){
		return calIbnrFactors(false);
	}
	
	public boolean recalIbnrFactors(){
		return calIbnrFactors(true);
	}
	
	public boolean calIbnrFactors( boolean isReCal ){
		String[] ibnrTypes = this.getIbnrTypes();
		if(!isReCal){
			exportIncurredPays(ibnrTypes);
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("Type\\Delay,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18\r\n");
		for(int i=0;i<ibnrTypes.length;i++){
			calIbnrFactor( ibnrTypes[i], isReCal );
			buffer.append( getIbnrFactor( ibnrTypes[i] ) );
		}
		if(!isReCal){
			FileProcessor.outputFile(buffer.toString(), file_path+"data/", "ibnr_factor", "csv");
		}
		return true;
	}
	
	public boolean calIbnrFactor( String type, boolean isReCal ){
		String ds = file_path + "data/Incurred-Pay/" + "ibnr_ds_" + type + ".csv";
		// read Incurred-Pay csv
		String[][] items = this.convertCsvToArr( ds );
		if( items == null ){
			return false;
		}
		
		// get occurred-claim csv data
		this.fill(items);
		// data accumulation
		this.cum();
		// cal IBNR
		if( !this.calFactor(type, isReCal) ){
			System.out.println("Cal IBNR error!");
			return false;
		}
		//
		String outputPath = file_path + "data/";
		// output
		if(isReCal){
//			type=type+"(2)";
			outputPath += "IBNR(2)/";
		} else {
			outputPath += "IBNR/";
		}
		FileProcessor.outputFile(this.join(), outputPath, "ibnr_" + type, "csv");
		return true;
	}
	
	/**
	 * 生成 “发生-理赔”倒三角
	 */
	public boolean exportIncurredPays( String[] ibnrTypes ){
		int startYear = Integer.parseInt(startDate.substring(0, 4));
		int startMonth = Integer.parseInt(startDate.substring(5, 7));
		int startQuarter = (startMonth-1)/3+1;
		int endYear = Integer.parseInt(endDate.substring(0, 4));
		int endMonth = Integer.parseInt(endDate.substring(5, 7));
		int endQuarter = (endMonth-1)/3+1;
		StringBuffer buffer = new StringBuffer();
		for(int i=0;i<ibnrTypes.length;i++){
			for(int year = startYear; year <= endYear; year ++){
				int startQ = 1;
				int endQ = 4;
				if( year == startYear ){
					startQ = startQuarter;
				}
				if( year == endYear ){
					endQ = endQuarter;
				}
				for(int quarter = startQ; quarter <= endQ; quarter++){
					String q = year + "Q" + quarter;
					buffer.append( ibnrTypes[i] ).append(",").append(q).append(",").append(q).append(",0\r\n");
				}
			}
		}
		
		FileProcessor.outputFile(buffer.toString(), file_path+"data/", "claim_factor_2", "csv");
		FileProcessor.mergeFiles(file_path+"data/", "claim_factor", 1, 2, "csv", "claim_factor", false);
		for(int i=0;i<ibnrTypes.length;i++){
			exportIncurredPay( ibnrTypes[i] );
		}
		return true;
	}
	
	public boolean exportIncurredPay( String type ){
		String dataSource = file_path + "data/" + "claim_factor.csv";
		String[] columnNames = new String[]{"IBNRTYPE","STARTQUARTER","ENDQUARTER","REALPAY"};
		String jasper = file_path + "jasper/" + "ibnr_ds.jasper";
		HashMap params = new HashMap();
		params.put("IBNR_TYPE", type);
		String report = file_path + "data/Incurred-Pay/" + "ibnr_ds_" + type + ".csv";
		ReportWriter.exportCsvReport(dataSource, columnNames, jasper, params, report);
		return true;
	}
	
	public String[][] convertCsvToArr( String csv ) {
		// read lines : String[]
		List lines = new ArrayList();
		int cols = 0;
        try {
        	File file = new File(csv);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
				lines.add(row);
				if( cols == 0 ){
					cols = row.length;
				} else if( cols != row.length ){
					return null;
				}
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 

		// String[][]
		if( lines.size() != cols ){
			return null;
		}
		String[][] items = new String[lines.size()][cols];
		for(int i=0;i<lines.size();i++){
			String[] row = (String[])lines.get(i);
			items[i] = row;
		}
		return items;
	}
	
	public void fill( String[][] items ){
		// title
		this.title = items[0];
		// data
		this.data = new double[items.length-1][items.length-1];
		for(int i=0;i<this.data.length;i++){
			for(int j=0;j<this.data.length;j++){
				this.data[i][j] = Double.parseDouble( items[i+1][j+1] );
			}
		}
	}
	
	// Cumulate
	public void cum(){
		for(int i=0;i<this.data.length-1;i++){
			for(int j=1;j<this.data.length-1;j++){
				double add = this.data[i][j-1];
				this.data[i][j] += add;
				this.data[data.length-1][j] += add;
			}
		}
	}
	
	// IBNR Factor
	public boolean calFactor( String type, boolean isReCal ){
		
		this.ibnrFactor = new double[this.data.length];
		this.incurredClaims = new double[this.data.length];
		this.ibnr = new double[this.data.length];
		this.remainingRes = new double[this.data.length];
		this.ptdClaims = new double[this.data.length];
		this.restatedRes = new double[this.data.length];
		this.percents = new double[this.data.length];
		
		if( data.length <= mPeriod + 1 ){
			return true;
		}
		
		double checkSum = 0;
		double incurredClaimsSum = 0;
		for(int i=0;i<mPeriod;i++){
			incurredClaims[i] = data[i][this.data.length - 1];
			checkSum += data[i][this.data.length - 1];
			incurredClaimsSum += incurredClaims[i];
		}
		
		
		if(isReCal){
			// load IBNR Factor
			readIbnrFactor( type );
		}
		for(int i=0;i<this.data.length-1;i++){
			if(i<mPeriod){
				ibnrFactor[i] = 1;
				continue;
			}
			if(!isReCal){	
				double a = 0;
				double b = 0;
				for(int j=0;j<mPeriod;j++){
					a += data[i-j-1][data.length-j-3];
					b += incurredClaims[i-j-1];
				}
				if( a == 0 || b == 0 ){
					System.out.println(type+":a,b=0");
					return false;
				}
				ibnrFactor[i] = a / b;
			}
			incurredClaims[i] = data[i][this.data.length - 1] / ibnrFactor[i];
			ibnr[i] = incurredClaims[i] - data[i][this.data.length - 1];
			checkSum += data[i][this.data.length - 1];
			remainingRes[i] = remainingRes[i-1] + ibnr[i];
			ptdClaims[i] = checkSum - data[data.length - 1][i];
			restatedRes[i] = remainingRes[i] + ptdClaims[i];
			incurredClaimsSum = incurredClaimsSum + incurredClaims[i] - incurredClaims[i-mPeriod];
			if( incurredClaimsSum == 0 ){
				System.out.println(type+":incurredClaimsSum == 0");
				return false;
			}
			percents[i] = restatedRes[i] / incurredClaimsSum;
		}
		return true;
	}
	
	public boolean readIbnrFactor( String type ){
		if(ibnrMap==null){
			ibnrMap = readIbnrFactorFile();
		}
		
		this.ibnrFactor = new double[this.data.length];
//		double[] fc = new double[19];
//		for(int i=0;i<=18;i++){
//			fc[i]=0.18+0.2*i;
//			if(fc[i]>1)fc[i]=1;
//		}
		double[] fc = (double[])ibnrMap.get(type);
		for(int i=0;i<this.data.length-1;i++){
			int j = data.length-2-i;
			if( j>18 ){
				continue;
			}
			ibnrFactor[i] = fc[j];
		}
		return true;
	}
	
	public HashMap readIbnrFactorFile(){
		HashMap ibnrMap = new HashMap();
		File file = new File(file_path + "data/" + "ibnr_factor.csv");
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			br.readLine();
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
				double[] fc = new double[row.length-1];
				for(int i=0;i<fc.length;i++){
					fc[i] = Double.parseDouble( row[i+1] );
				}
				ibnrMap.put(row[0], fc);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return ibnrMap;
		
	}
	
	public String join(){
		
		String delimiter = ",";
		// title
		StringBuffer buffer = new StringBuffer( this.joinArr(this.title, delimiter) );
		buffer.append(delimiter).append("IBNR Factor");
		buffer.append(delimiter).append("Incurred Claims");
		buffer.append(delimiter).append("IBNR");
		buffer.append(delimiter).append("Remaining Reserve");
		buffer.append(delimiter).append("PTD Claims on Res");
		buffer.append(delimiter).append("Restated Res");
		buffer.append(delimiter).append("Percent");
		buffer.append("\r\n");

		String format = "#";
		String format1 = "#.#####";
		String format2 = "#%";
		java.text.DecimalFormat df = new java.text.DecimalFormat( format );
		java.text.DecimalFormat df1 = new java.text.DecimalFormat( format1 );
		java.text.DecimalFormat df2 = new java.text.DecimalFormat( format2 );
		// lines
		for(int i=0;i<this.data.length-1;i++){
			buffer.append(this.title[i+1]).append(delimiter).append(this.joinArr(this.data[i], delimiter));
			buffer.append(delimiter).append( df1.format( ibnrFactor[i] ) );//IBNR Factor
			buffer.append(delimiter).append( df.format( incurredClaims[i] ) );//Incurred Claims
			buffer.append(delimiter).append( df.format( ibnr[i] ) );//IBNR
			buffer.append(delimiter).append( df.format( remainingRes[i] ) );//Remaining Reserve
			buffer.append(delimiter).append( df.format( ptdClaims[i] ) );//PTD Claims on Res
			buffer.append(delimiter).append( df.format( restatedRes[i] ) );//Restated Res
			buffer.append(delimiter).append( df2.format( percents[i] ) );//Percent
			buffer.append("\r\n");
		}
		return buffer.toString();
	}
	public String joinArr( String[] s, String delimiter ) {
		if( s == null){
			return "";
		}
		StringBuffer buffer = new StringBuffer(s[0]);
		for(int i=1;i<s.length;i++){
			buffer.append(delimiter).append(s[i]);
		}
		return buffer.toString();
	}
	public String joinArr( double[] s, String delimiter ) {
		String format = "#.##";
		return this.joinArr(s, delimiter, format);
	}
	/**
	 * 
	 * @param s
	 * @param delimiter
	 * @param format "#" "#,##"
	 * @return
	 */
	public String joinArr( double[] s, String delimiter, String format ) {
		if( s == null){
			return "";
		}
		java.text.DecimalFormat df = new java.text.DecimalFormat( format );
		StringBuffer buffer = new StringBuffer();
		buffer.append( df.format( s[0] ) );
		for(int i=1;i<s.length;i++){
			buffer.append(delimiter).append( df.format( s[i] ) );
		}
		return buffer.toString();
	}
	
	public String getIbnrFactor( String type ){
		StringBuffer buffer = new StringBuffer();
		buffer.append(type);
		
		String format = "#.#####";
		java.text.DecimalFormat df = new java.text.DecimalFormat( format );
		for(int i=0;i<=18;i++){
			if( i>=data.length-1 ){
				buffer.append(",1");
			} else {
				buffer.append(",").append( df.format( ibnrFactor[ data.length-2-i ] ) );
			}
		}
		buffer.append("\r\n");
		return buffer.toString();
	}
	
	public String[] getIbnrTypes(){

		String types = "";
		// 
		types += "Captive_BJ_MED";
		types += ",BJ_GEB_MED01,BJ_GEB_MED02,BJ_NONGEB_MED01,BJ_NONGEB_MED02,BJ_WMP,BJ_AMR";
		types += ",SH_GEB_MED01,SH_GEB_MED02,SH_NONGEB_MED01,SH_NONGEB_MED02,SH_WMP,SH_AMR";
		types += ",GD_GEB_MED01,GD_GEB_MED02,GD_NONGEB_MED01,GD_NONGEB_MED02,GD_AMR";
		types += ",GC_MED01,GC_MED02,GC_WMP,GC_AMR";
		return types.split(",");
	}
	public String getIbnrType( String manageCom, String gebClient, String riskCode, String getDutyCode, String deptID ){
		
		if( "14".equals(deptID) && manageCom.startsWith("8601") && ( "NIK01".equals(riskCode) || "NIK02".equals(riskCode) )){
			return "Captive_BJ_MED";
		}
		
		String branch = "GC";
		if( manageCom.startsWith("8601") ){
			branch = "BJ";
		} else if ( manageCom.startsWith("8602") ){
			branch = "GD";
		} else if ( manageCom.startsWith("8603") ){
			branch = "SH";
		}
		
		String gebFlag = "";
		if( !"GC".equals(branch) ){
			if("1".equals(gebClient)){
				gebFlag="_GEB";
			} else if("2".equals(gebClient)){
				gebFlag="_NONGEB";
			}
		}
		
		String plan = "";
		if( "NIK03".equals(riskCode) || "NIK07".equals(riskCode) || "NIK08".equals(riskCode) || "NIK09".equals(riskCode) || "NIK10".equals(riskCode) || "NIK11".equals(riskCode) ){
			plan = "_WMP";
			gebFlag = "";
			if( "GD".equals(branch) ){
				branch = "GC";
			}
		} else if( "MIK01".equals(riskCode) || "MOK01".equals(riskCode) || "MIK02".equals(riskCode) ){
			plan = "_AMR";
			gebFlag = "";
		} else if( "NIK01".equals(riskCode) ) {
			plan = "_MED01";
		} else if( "NIK02".equals(riskCode) || "MKK01".equals(riskCode) ) {
			plan = "_MED02";
		}
		return branch + gebFlag + plan;
	}
	

}

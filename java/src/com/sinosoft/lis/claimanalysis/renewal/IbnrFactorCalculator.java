package com.sinosoft.lis.claimanalysis.renewal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import com.sinosoft.lis.claimanalysis.common.FileProcessor;
import com.sinosoft.lis.claimanalysis.common.ReportWriter;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;

public class IbnrFactorCalculator {
	
	String file_path;
	String jasper_path;
	String mTaskDate;
	String mTaskUser;
	
	int startYear;
	int startMonth;
	int endYear;
	int endMonth;
	int mPeriod=12;// Q.Quarter 4 M.Month 12
	
	int months;
	
	FileProcessor FileProcessor = new FileProcessor();
	ReportWriter ReportWriter = new ReportWriter();
	
	private String id;
	private String dutyStr;
	private Hashtable lmDutyGetRelaHt;
	
	String[] title;
	double[][] data;
	double[] ibnrFactor;
	double[] incurredClaims;
	double[] ibnr;
	double[] remainingRes;
	double[] ptdClaims;
	double[] restatedRes;
	double[] percents;
	
	HashMap ibnrMap = null;
	IbnrFactorCalculator(){
	}
	
	IbnrFactorCalculator(String filePath, String jasperPath, String id, String dutyStr, int startYear, int startMonth, int endYear, int endMonth){

		this.file_path = filePath;
		this.jasper_path = jasperPath;
		this.id = id;
		this.dutyStr = dutyStr;
		this.startYear = startYear;
		this.startMonth = startMonth;
		this.endYear = endYear;
		this.endMonth = endMonth;
		this.lmDutyGetRelaHt = (new DataDownloader()).getLmDutyGetRelaHt();
		months = (endYear-startYear)*mPeriod + endMonth - startMonth;
	}
	
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
		buffer.append("Type\\Delay");
		for(int i=0;i<=months;i++){
			buffer.append(",").append(i);
		}
		buffer.append("\r\n");
		for(int i=0;i<ibnrTypes.length;i++){
			if(!calIbnrFactor( ibnrTypes[i], isReCal )){
//				return false;
			}
			buffer.append( getIbnrFactor( ibnrTypes[i] ) );
		}
		if(!isReCal){
			FileProcessor.outputFile(buffer.toString(), file_path, "cf_"+id, "csv");
		}
		return true;
	}
	
	public boolean calIbnrFactor( String type, boolean isReCal ){
		String ds = file_path + "Incurred-Pay/" + "ibnr_ds_" + type + ".csv";
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
		String outputPath = file_path;
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
	
	public boolean exportIncurredPays( String[] ibnrTypes ){
		
		//
		StringBuffer buffer = new StringBuffer();
		try {
			File claim_file = new File( file_path + "claim.csv" );
			BufferedReader br = new BufferedReader(new FileReader(claim_file));
			String line;
			br.readLine();
			FileProcessor.outputFile(buffer.toString(), file_path, "claim_factor_"+id+"_1", "csv",false);
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
				String grpContNo = row[1];
				String planCode = row[11];
				String riskCode = row[32];
				String getDutyCode = row[28];
				String realPay = row[30];
				String date1 = row[33];
				String date2 = row[34];
//				String ibnrType = this.getIbnrType(riskCode, getDutyCode);
				String ibnrType = this.getIbnrType(grpContNo, planCode, riskCode, getDutyCode);
				if( "".equals(date1) || "".equals(date2) || "".equals(ibnrType) || ibnrType == null ){
					continue;
				}
				String[] d1 = date1.split("/");
				String[] d2 = date2.split("/");
				int year1 = Integer.parseInt(d1[0]);
				int year2 = Integer.parseInt(d2[0]);
				int month1 = Integer.parseInt(d1[1]);
				int month2 = Integer.parseInt(d2[1]);
				
				if( (year1*12+month1)<(startYear*12+startMonth) || (year2*12+month2)>(endYear*12+endMonth) ){
					continue;
				}
				
				buffer.append(ibnrType).append(",");
				buffer.append(date1).append(",");
				buffer.append(date2).append(",");
				buffer.append(realPay).append("\r\n");
				
				if(buffer.length()>=1024*1024*10){
					FileProcessor.outputFile(buffer.toString(), file_path, "claim_factor_"+id+"_1", "csv",true);
					buffer = new StringBuffer();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		FileProcessor.outputFile(buffer.toString(), file_path, "claim_factor_"+id+"_1", "csv",true);
		
		buffer = new StringBuffer();
		for(int i=0;i<ibnrTypes.length;i++){
			for(int year = startYear; year <= endYear; year ++){
				int startM = 1;
				int endM = 12;
				if( year == startYear ){
					startM = startMonth;
				}
				if( year == endYear ){
					endM= endMonth;
				}
				for(int month = startM; month <= endM; month++){
					String m = year + "/" + (month<10?"0":"") + month;
					buffer.append( ibnrTypes[i] ).append(",").append(m).append(",").append(m).append(",0\r\n");
				}
			}
		}
		
		FileProcessor.outputFile(buffer.toString(), file_path, "claim_factor_"+id+"_2", "csv");
		FileProcessor.mergeFiles(file_path, "claim_factor_"+id, 1, 2, "csv", "claim_factor_"+id, false);
		for(int i=0;i<ibnrTypes.length;i++){
			exportIncurredPay( ibnrTypes[i] );
		}
		return true;
	}
	
	public boolean exportIncurredPay( String type ){
		String dataSource = file_path + "claim_factor_"+id+".csv";
		String[] columnNames = new String[]{"IBNRTYPE","STARTQUARTER","ENDQUARTER","REALPAY"};
		String jasper = jasper_path + "ibnr_ds.jasper";
		HashMap params = new HashMap();
		params.put("IBNR_TYPE", type);
		String report = file_path + "Incurred-Pay/" + "ibnr_ds_" + type + ".csv";
		ReportWriter.exportCsvReport(dataSource, columnNames, jasper, params, report, false);
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
		for(int i=0;i<data.length;i++){
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
					ibnrFactor[i] = 1;
					continue;
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
			if( j>months ){
				continue;
			}
			ibnrFactor[i] = fc[j];
		}
		return true;
	}
	
	public HashMap readIbnrFactorFile(){
		HashMap ibnrMap = new HashMap();
		File file = new File(file_path + "cf_"+id+".csv");
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
	public String[] getIbnrTypes(){
		String types = "MEDICAL,WMP,AMR";
		return types.split(",");
	}
	
	public String getIbnrType(String grpContNo, String planCode, String riskCode, String getDutyCode){
//		if( !"88000403900".equals(grpContNo) && !"88000403901".equals(grpContNo) ){
//			return null;
//		}
//		if( !"NIK01".equals(riskCode) && !"NIK02".equals(riskCode) && 
//				!"NIK03".equals(riskCode) && !"NIK07".equals(riskCode) && 
//				!"NIK08".equals(riskCode) && !"NIK09".equals(riskCode) && 
//				!"NIK12".equals(riskCode) && !"MIK01".equals(riskCode) && 
//				!"MOK01".equals(riskCode) && !"MKK01".equals(riskCode) ){
//			return null;
//		}
//		return "medical";
//		Object obj = lmDutyGetRelaHt.get(getDutyCode);
//		if(obj==null){
//			return null;
//		}
//		String dutyCode = (String)obj;
//		String regex = "^.*"+grpContNo+"_"+planCode+"_"+riskCode+"_"+dutyCode+".*$";
//		if( !dutyStr.matches(regex) ){
//			return null;
//		}
		String type = null;
		
		
		if( "NIK03".equals(riskCode) || "NIK07".equals(riskCode) || "NIK08".equals(riskCode) || "NIK09".equals(riskCode) || "NIK10".equals(riskCode) || "NIK11".equals(riskCode)){
			type = "WMP";
		}  else if( "MIK01".equals(riskCode) || "MOK01".equals(riskCode) ){
			type = "AMR";
		}  else if( "NIK01".equals(riskCode) || "NIK02".equals(riskCode) || "NIK12".equals(riskCode) || "MKK01".equals(riskCode) ){
			type = "MEDICAL";
		}  
		
		return type;
//		if( "NIK01".equals( riskCode ) || "NIK02".equals( riskCode ) || "NIK12".equals( riskCode ) || "NIK03".equals( riskCode ) || "NIK07".equals( riskCode ) || "NIK08".equals( riskCode ) || "NIK09".equals( riskCode ) || "MIK01".equals( riskCode ) || "MOK01".equals( riskCode ) || "MKK01".equals( riskCode ) ){
//			return "medical";
//		} else {
//			return null;
//		}
	}
	
	public String getIbnrFactor( String type ){
		StringBuffer buffer = new StringBuffer();
		buffer.append(type);
		
		String format = "#.#####";
		java.text.DecimalFormat df = new java.text.DecimalFormat( format );
		for(int i=0;i<=months;i++){
			if( i>=data.length-1 ){
				buffer.append(",1");
			} else {
				buffer.append(",").append( df.format( ibnrFactor[ data.length-2-i ] ) );
			}
		}
		buffer.append("\r\n");
		return buffer.toString();
	}

}

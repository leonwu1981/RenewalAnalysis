package com.sinosoft.lis.claimanalysis.renewal;

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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sinosoft.lis.claimanalysis.common.EmailSender;
import com.sinosoft.lis.claimanalysis.common.FileProcessor;
import com.sinosoft.lis.claimanalysis.common.FormatConverter;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;

public class ClaimPredictBL {
	public CErrors mErrors = new CErrors();
	public VData mResult = new VData();
	public GlobalInput mGlobalInput;
	public VData mInputData;
	public String mOperate;
	
	public String file_path;
	public String relativePath;
	public String jasperPath;
	public String mTaskDate;
	public String mTaskUser;
	
	public int startYear;
	public int startMonth;
	public int endYear;
	public int endMonth;
	public Date endDate;
	
	public boolean totalFlag = true;
	public boolean planFlag = true;
	public boolean relationFlag = true;
	public String grpContNos;
	
	public String mFileTitle;
	public String mEmail;
	
	public String id;
	
	FileProcessor FileProcessor = new FileProcessor();
	DataDownloader DataDownloader = new DataDownloader();
	
	List files = new ArrayList();
	List sheetNames = new ArrayList();
	
	public boolean getYearMonth(){
		if(grpContNos==null||grpContNos.equals("")){
			return false;
		}
		
		String sql = null;
		String[] grpContNoArr = grpContNos.split(",");
		for(int i=0;i<grpContNoArr.length;i++){
			String grpContNo = grpContNoArr[i];
			if(sql==null){
				sql = "select cvalidate from lcgrpcont where grpContNo='"+grpContNo+"' ";
			} else {
				sql = sql + " or grpContNo='"+grpContNo+"' ";
			}
		}

		if(sql==null){
			return false;
		}else{
			sql = sql + "order by cvalidate";
		}
		ExeSQL tExeSQL = new ExeSQL();
		SSRS ssrs = tExeSQL.execSQL(sql);
		String startDate;
		if (ssrs.getMaxRow() > 0) {
			startDate = ssrs.GetText(1, 1);
		} else {
			return false;
		}
		String[] sd = startDate.split("-");
		startYear = Integer.parseInt(sd[0]);
		startMonth = Integer.parseInt(sd[1]);
		
//		Date date = new Date();
		SimpleDateFormat dateFm = new SimpleDateFormat("yyyyMMdd");
		
		try {
			endDate = dateFm.parse(id);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		dateFm = new SimpleDateFormat("yyyy-MM-dd");
		String sendDate = dateFm.format(endDate);

		String[] ed = sendDate.split("-");
		endYear = Integer.parseInt(ed[0]);
		endMonth = Integer.parseInt(ed[1]);
		return true;
	}
	
	public boolean exportPredictiveClaim(){
		if( !this.getYearMonth() ){
			return false;
		}
		IbnrFactorCalculator c = new IbnrFactorCalculator(file_path, jasperPath, id, "", startYear, startMonth, endYear, endMonth);
		if( !c.calIbnrFactors()){
//			return false;
		}
		
		String[] cfTypes = c.getIbnrTypes();
		
		for(int i=0;i<cfTypes.length;i++){
			if( ! exportPredictByType(cfTypes[i]) ){
//				return false;
			}
		}
		if(files.size()>0){
			FileProcessor.mergeCsvToXls(files, sheetNames, file_path + "predictclaim.xls");
		}
		return true;
	}
	
	public boolean exportPredictByType( String cfType ){

		IbnrFactorCalculator IbnrFactorCalculator = new IbnrFactorCalculator();
		
		//1. ibnr factor
		double[] ibnrfactors = null;
		String path = file_path + "cf_"+id+".csv";
		int rownum = 0;
		try {
        	File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line; 
			br.readLine();
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
				rownum = row.length-1;
				double[] fc = new double[row.length-1];
				for(int i=0;i<fc.length;i++){
					fc[i] = Double.parseDouble( row[i+1] );
					if(fc[i]==0){
						fc = null;
						break;
					}
				}
				ibnrfactors = fc;
				if( cfType.equals(row[0]) ){
					break;
				}
				ibnrfactors = null;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//
		
		if( ibnrfactors == null ){
			ibnrfactors = new double[rownum];
			for(int i=0;i<rownum;i++){
				ibnrfactors[i] = 1;
			}
		}
		
		try {
			
			HashMap planMap = new HashMap();
			HashMap relationMap = new HashMap();
			// 2.realpay
			HashMap claimMap = new HashMap();// 放 计划的realpay
			HashMap claimMap2 = new HashMap();// 放 关系的realpay
			path = file_path + "claim.csv";
        	File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			br.readLine();
			Hashtable lmDutyGetRelaHt = DataDownloader.getLmDutyGetRelaHt();
			int readLine = 1;
			while ( (line = br.readLine()) != null ) {
				readLine++;
				if(readLine%10000==0){
					System.out.println("claim.csv read "+readLine);
				}
				
				String[] row = line.split(",");
				String grpContNo = row[1];
				String planCode = row[11].replaceAll("_", "");
				String riskCode = row[32];
				String getDutyCode = row[28];
				String relationType = row[10];
				
				String type = IbnrFactorCalculator.getIbnrType("", "", riskCode, "");
				if( !cfType.equals(type) ){
					continue;
				}
				
				Object obj = lmDutyGetRelaHt.get(getDutyCode);
				if(obj==null){
					continue;
				}
				String dutyCode = (String)obj;
				
				String duty = grpContNo + "_" +planCode + "_" + riskCode + "_" + dutyCode;
			    String plan = grpContNo + "_" +planCode;
			    //By Fang 预测理赔报表中“MEDICAL_按身份”不需要按照保单区分 20111214
			    //String relation = grpContNo + "_" + relationType;
			    String relation = relationType;
				if(!filter(duty)){
					continue;
				}

				double realPay = Double.parseDouble(row[30]);
				String incurred = row[33];
				if("".equals(incurred)){
					incurred = "0000/00";
				}
				String key = plan + "#" + incurred;
				
				Object o = claimMap.get(key);
				if(o==null){
					claimMap.put(key, new Double(realPay));
				} else {
					Double value = (Double)o;
					claimMap.put(key, new Double( value.doubleValue() + realPay ));
				}
				//
				key = relation + "#" + incurred;
				o = claimMap2.get(key);
				if(o==null){
					claimMap2.put(key, new Double(realPay));
				} else {
					Double value = (Double)o;
					claimMap2.put(key, new Double( value.doubleValue() + realPay ));
				}
			}
			
			//3.exposure
			HashMap matchMap = new HashMap();// 放 计划的exposure
			HashMap matchMap2 = new HashMap();// 放 关系的exposure
			HashMap earnedMap = new HashMap();// 放 计划的earnedprem
			HashMap earnedMap2 = new HashMap();// 放 关系的earnedprem
			path = file_path + "match.csv";
			file = new File(path);
			br = new BufferedReader(new FileReader(file));
			br.readLine();
			readLine = 1;
			while ( (line = br.readLine()) != null ) {
				readLine++;
				if(readLine%10000==0){
					System.out.println("match.csv read "+readLine);
				}
				
				String[] row = line.split(",");
				String grpContNo = row[1];
				String planCode = row[19].replaceAll("_", "");//弹性福利计划的planCode有_，会导致后面的计算有问题
				String riskCode = row[21];
				String dutyCode = row[22];
				double prem = Double.parseDouble( row[24] );
				//By Fang 预测理赔报表中“MEDICAL_按身份”不需要按照保单区分 20111214
				//String relation = grpContNo + "_" + row[16];
				String relation = row[16];
				
				String type = IbnrFactorCalculator.getIbnrType("", "", riskCode, "");
				if( !cfType.equals(type) ){
					continue;
				}
				
				String duty = grpContNo + "_" +planCode + "_" + riskCode + "_" + dutyCode;
				String plan = grpContNo + "_" +planCode;
				if(!filter(duty)){
					continue;
				}
				planMap.put(plan, ""); //
				relationMap.put(relation, "");//
				String cvalidate = row[2];
				String enddate = row[3];
				String dutyStart = row[12];
				String dutyEnd = row[13];
				String payToDate = row[50];
				if("".equals(cvalidate) || "".equals(enddate) || "".equals(dutyStart) || "".equals(dutyEnd)){
					continue;
				}
				
				Date cdate = FormatConverter.getDate(cvalidate);
				Date edate = FormatConverter.getDate(enddate);
				Date dutyStartDate = FormatConverter.getDate(dutyStart);
				Date dutyEndDate = FormatConverter.getDate(dutyEnd);
				Date ptd = FormatConverter.getDate(payToDate);
				
				//double z = (double)((dutyEndDate.getTime()-dutyStartDate.getTime())/(1000*60*60*24)) + 1;
				double z = (double)((ptd.getTime()-dutyStartDate.getTime())/(1000*60*60*24)) + 1;
				
				for(int i=(startYear*12+startMonth); i<=(endYear*12+endMonth); i++){
					int y1 = i/12;
					int m1 = i%12;
					if(m1==0){
						y1--;
						m1=12;
					}
					int y2 = (i+1)/12;
					int m2 = (i+1)%12;
					if(m2==0){
						y2--;
						m2=12;
					}
					Date d1 = FormatConverter.getDate(y1+"-"+m1+"-01");
					Date d2 = FormatConverter.getDate(y2+"-"+m2+"-01");
					// (min(评估终止日,被保险人终止日)-被保险人生效日+1)/(保单终止日-保单生效日+1)
					// 分子
					int j = 0;
					Date minEndDate = d2;
					if( dutyEndDate.before(minEndDate) ) {
						minEndDate = dutyEndDate;
						j = 1;
					}
					if( endDate.before(minEndDate) ) {
						minEndDate = endDate;
						j = 1;
					}
					Date maxStartDate = d1.after(dutyStartDate)?d1:dutyStartDate;
					double x = (double)((minEndDate.getTime()-maxStartDate.getTime())/(1000*60*60*24)) + j;
					// 分母
					j = 0;
//					if( edate.before(d2) ) {
//						d2 = edate;
//						j = 1;
//					}
//					d1 = d1.after(cdate)?d1:cdate;
					double y = (double)((d2.getTime()-d1.getTime())/(1000*60*60*24)) + j;
					double exposure = 0;
					double earnedprem = 0;
					//
					if( x>0 && y>0 ){
						
						String name = row[11];
						int people = 1;
						if("无名单".equals(name)){
							String insuredno = row[10];
							people = Integer.parseInt(new ExeSQL().getOneValue("select INSUREDPEOPLES from insured_view where insuredno='"+insuredno+"'"));
						}

						exposure = x*people/y;
						earnedprem = prem*x/z;
					}
					
					if(earnedprem>0){
						String incurred = (m1<10)?(y1+"/0"+m1):(y1+"/"+m1);
						String key = plan + "#" + incurred;
						Object o = earnedMap.get(key);
						if( o == null ){
							earnedMap.put(key, new Double(earnedprem));
						} else {
							Double value = (Double)o;
							earnedMap.put(key, new Double( value.doubleValue() + earnedprem ));
						}
						
						key = relation + "#" + incurred;
						o = earnedMap2.get(key);
						if( o == null ){
							earnedMap2.put(key, new Double(earnedprem));
						} else {
							Double value = (Double)o;
							earnedMap2.put(key, new Double( value.doubleValue() + earnedprem ));
						}
					}
					
					// 暴露数 只记 医疗险的住院责任
					if( "NIK01".equals(riskCode) && "615001".equals(dutyCode) ){
					} else if( "NIK02".equals(riskCode) && "617001".equals(dutyCode) ){
					} else if( "NIK03".equals(riskCode) && "612001".equals(dutyCode) ){
					} else if( "NIK07".equals(riskCode) && "637001".equals(dutyCode) ){
					} else if( "NIK08".equals(riskCode) && "641001".equals(dutyCode) ){
					} else if( "NIK10".equals(riskCode) && "650001".equals(dutyCode) ){
					} else if( "NIK11".equals(riskCode) && "651001".equals(dutyCode) ){
					} else if( "NIK09".equals(riskCode) && "642001".equals(dutyCode) ){
					} else if( "MIK01".equals(riskCode) || "MOK01".equals(riskCode) ){ // AMR
					} else if( "NIK12".equals(riskCode) && "602001".equals(dutyCode) ){ //By Fang 增加NIK12的暴露数计算20111214
					} else {
						continue;
					}
					
					if(exposure>0){
						String incurred = (m1<10)?(y1+"/0"+m1):(y1+"/"+m1);
						String key = plan + "#" + incurred;
						Object o = matchMap.get(key);
						if( o == null ){
							matchMap.put(key, new Double(exposure));
						} else {
							Double value = (Double)o;
							matchMap.put(key, new Double( value.doubleValue() + exposure ));
						}
						
						key = relation + "#" + incurred;
						o = matchMap2.get(key);
						if( o == null ){
							matchMap2.put(key, new Double(exposure));
						} else {
							Double value = (Double)o;
							matchMap2.put(key, new Double( value.doubleValue() + exposure ));
						}
					}
				}
			}
			
			List planList = new ArrayList(planMap.keySet());
			Collections.sort(planList);
			List relationList = new ArrayList(relationMap.keySet());
			Collections.sort(relationList);
			
			String head = "发生月份incurred month,曝露数exposure,已赚保费earned prem,实际发生金额Paid Claim,完成因子CF,预测发生金额Predicted claim,每人每月理赔金额PMPM(预测理赔金额/曝露数)";
			String note1 = "预测理赔：保单经过月份的理赔求和*12/保单经过月份*（1+医疗通胀/12)＾保单剩余月数，如保单经过8各月，预测理赔金额为1500000，则预测年理赔金额为1500000*12/8*(1+医疗通胀/12）^4=2325943（假设医疗通胀为10%），明年的理赔金额为2325943*（1+医疗通胀）/（1-佣金-推动费-费用）";
			String note2 = "预测明年的费率：每人每月理赔金额*12/保单经过月份*（1+医疗通胀/12)＾保单剩余月数，如保单经过8个月每人每月理赔金额640，则预测年费率为640*12/8*（1+0.1/12）＾4=992，明年费率为992*（1+医疗通胀）/（1-佣金-推动费-费用），该费率是适用于计算保险计划一致的情况下的费率，保单经过月份需是满一个月，否则计算的完成因子不能反映实际理赔。";

			String blank = ",,,,,,\r\n";
			
			String note = "注："+blank+note1+blank+note2+blank;
			
			
			if(totalFlag && planList.size()>0){
				StringBuffer sb = new StringBuffer();
				sb.append(head).append("\r\n");
				double sumExposure = 0;
				double sumRealPay = 0;
				double sumPredicted = 0;
				double sumEarnedprem = 0;
				for(int i=(startYear*12+startMonth); i<=(endYear*12+endMonth); i++){
					int y = i/12;
					int m = i%12;
					if(m==0){
						y--;
						m = 12;
					}
					String incurred = (m<10)?(y+"/0"+m):(y+"/"+m);
					//
					double exposure = 0;
					double realpay = 0;
					double earnedprem = 0;
					for(int j=0;j<planList.size();j++){
						String plan = (String)planList.get(j);
//						String[] s = duty.split("_");
//						String plan = s[0] + "_" + s[1];
						
						String key = plan + "#" + incurred;
						
						Object matchObj = matchMap.get(key);
						if( matchObj != null ){
							exposure += ((Double)matchObj).doubleValue();
						}
						//
						Object earnedObj = earnedMap.get(key);
						if( earnedObj != null ){
							earnedprem += ((Double)earnedObj).doubleValue();
						}
						//
						Object claimObj = claimMap.get(key);
						if( claimObj != null ){
							realpay += ((Double)claimObj).doubleValue();
						}
					}
					//
					int n =  endYear*12 + endMonth - y*12 - m;
					double cf;
					if(n>=ibnrfactors.length){
						cf = 1;
					} else {
						cf = ibnrfactors[n];
					}
					//
					double predicted = realpay / cf;
					//
					double pmpm = (exposure == 0)?0:(predicted/exposure) ;
					sb.append(incurred).append(",").append(exposure).append(",").append(earnedprem).append(",").append(realpay).append(",").append(cf).append(",").append(predicted).append(",").append(pmpm).append("\r\n");
					
					//
					sumRealPay += realpay;
					sumPredicted += predicted;
					sumExposure += exposure;
					sumEarnedprem += earnedprem;
				}

				double pmpm = (sumExposure == 0)?0:(sumPredicted/sumExposure);
				sb.append("SUM").append(",").append(sumExposure).append(",").append(sumEarnedprem).append(",").append(sumRealPay).append(",-,"+sumPredicted).append(",").append(pmpm).append("\r\n");
				sb.append("\r\n").append(note).append("\r\n");
				FileProcessor.outputFile(sb.toString(), file_path, "PredictiveClaim_total_"+cfType, "csv");
				files.add( file_path + "PredictiveClaim_total_"+cfType + ".csv" );
				sheetNames.add(cfType+"_总计");
			}
			
			if(planFlag && planList.size()>0 && (!"AMR".equals(cfType)) ){
				StringBuffer sb = new StringBuffer();
				for(int j=0;j<planList.size();j++){
					String plan = (String)planList.get(j);
					sb.append(plan).append(",,,,,\r\n");
					sb.append(head).append("\r\n");
					double sumExposure = 0;
					double sumRealPay = 0;
					double sumPredicted = 0;
					double sumEarnedprem = 0;
					for(int i=(startYear*12+startMonth); i<=(endYear*12+endMonth); i++){
						int y = i/12;
						int m = i%12;
						if(m==0){
							y--;
							m = 12;
						}
						String incurred = (m<10)?(y+"/0"+m):(y+"/"+m);
						
						String key = plan + "#" + incurred;
						//
						Object matchObj = matchMap.get(key);
						double exposure = 0;
						if( matchObj != null ){
							exposure = ((Double)matchObj).doubleValue();
						}
						//
						Object earnedObj = earnedMap.get(key);
						double earnedprem = 0;
						if( earnedObj != null ){
							earnedprem = ((Double)earnedObj).doubleValue();
						}
						//
						Object claimObj = claimMap.get(key);
						double realpay = 0;
						if( claimObj != null ){
							realpay = ((Double)claimObj).doubleValue();
						}
						int n =  endYear*12 + endMonth - y*12 - m;
						double cf;
						if(n>=ibnrfactors.length){
							cf = 1;
						} else {
							cf = ibnrfactors[n];
						}
						//
						double predicted = realpay / cf;
						//
						double pmpm = (exposure == 0)?0:(predicted/exposure) ;
						sb.append(incurred).append(",").append(exposure).append(",").append(earnedprem).append(",").append(realpay).append(",").append(cf).append(",").append(predicted).append(",").append(pmpm).append("\r\n");
						
						//
						sumRealPay += realpay;
						sumPredicted += predicted;
						sumExposure += exposure;
						sumEarnedprem += earnedprem;
					}

					double pmpm = (sumExposure == 0)?0:(sumPredicted/sumExposure);
					sb.append("SUM").append(",").append(sumExposure).append(",").append(sumEarnedprem).append(",").append(sumRealPay).append(",-,"+sumPredicted).append(",").append(pmpm).append("\r\n");
					sb.append(blank);
				}
				sb.append("\r\n").append(note).append("\r\n");
				FileProcessor.outputFile(sb.toString(), file_path, "PredictiveClaim_plan_"+cfType, "csv");
				files.add( file_path + "PredictiveClaim_plan_"+cfType + ".csv" );
				sheetNames.add(cfType+"_按计划");
			}
			
			if(relationFlag && relationList.size()>0 && (!"AMR".equals(cfType)) ){
				StringBuffer sb = new StringBuffer();
				for(int j=0;j<relationList.size();j++){
					String relation = (String)relationList.get(j);
					sb.append(relation).append(",,,,,\r\n");
					sb.append(head).append("\r\n");
					double sumExposure = 0;
					double sumRealPay = 0;
					double sumPredicted = 0;
					double sumEarnedprem = 0;
					for(int i=(startYear*12+startMonth); i<=(endYear*12+endMonth); i++){
						int y = i/12;
						int m = i%12;
						if(m==0){
							y--;
							m = 12;
						}
						String incurred = (m<10)?(y+"/0"+m):(y+"/"+m);
						
						String key = relation + "#" + incurred;
						//
						Object matchObj = matchMap2.get(key);
						double exposure = 0;
						if( matchObj != null ){
							exposure = ((Double)matchObj).doubleValue();
						}
						//
						Object earnedObj = earnedMap2.get(key);
						double earnedprem = 0;
						if( earnedObj != null ){
							earnedprem = ((Double)earnedObj).doubleValue();
						}
						//
						Object claimObj = claimMap2.get(key);
						double realpay = 0;
						if( claimObj != null ){
							realpay = ((Double)claimObj).doubleValue();
						}
						int n =  endYear*12 + endMonth - y*12 - m;
						double cf;
						if(n>=ibnrfactors.length){
							cf = 1;
						} else {
							cf = ibnrfactors[n];
						}
						//
						double predicted = realpay / cf;
						//
						double pmpm = (exposure == 0)?0:(predicted/exposure) ;
						sb.append(incurred).append(",").append(exposure).append(",").append(earnedprem).append(",").append(realpay).append(",").append(cf).append(",").append(predicted).append(",").append(pmpm).append("\r\n");
						
						//
						sumRealPay += realpay;
						sumPredicted += predicted;
						sumExposure += exposure;
						sumEarnedprem += earnedprem;
					}

					double pmpm = (sumExposure == 0)?0:(sumPredicted/sumExposure);
					sb.append("SUM").append(",").append(sumExposure).append(",").append(sumEarnedprem).append(",").append(sumRealPay).append(",-,"+sumPredicted).append(",").append(pmpm).append("\r\n");
					sb.append(blank);
				}
				sb.append("\r\n").append(note).append("\r\n");
				FileProcessor.outputFile(sb.toString(), file_path, "PredictiveClaim_relation_"+cfType, "csv");
				files.add( file_path + "PredictiveClaim_relation_"+cfType + ".csv" );
				sheetNames.add(cfType+"_按身份");
			}
			
			
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return true;
	}
	
	public boolean filter( String s ){
//		String regex = "^.*"+s+".*$";
//		return dutyStr.matches(regex);
		
		String[] info = s.split("_");
		String riskCode = info[2];
		if( "NIK01".equals(riskCode) ){
			return true;
		} else if( "NIK02".equals(riskCode) ){
			return true;
		}  else if( "NIK12".equals(riskCode) ){
			return true;
		}  else if( "NIK03".equals(riskCode) ){
			return true;
		}  else if( "NIK07".equals(riskCode) ){
			return true;
		}  else if( "NIK08".equals(riskCode) ){
			return true;
		}  else if( "NIK10".equals(riskCode) ){
			return true;
		}  else if( "NIK11".equals(riskCode) ){
			return true;
		}  else if( "NIK09".equals(riskCode) ){
			return true;
		}  else if( "MIK01".equals(riskCode) ){
			return true;
		}  else if( "MOK01".equals(riskCode) ){
			return true;
		}  else if( "MKK01".equals(riskCode) ){
			return true;
		}  else {
			return false;
		} 
		
//		String[] ss = s.split("_");
//		String grpContNo = ss[0];
//		String planCode = ss[1];
//		String riskCode = ss[2];
//		if( "NIK01".equals( riskCode ) || "NIK02".equals( riskCode ) || "NIK12".equals( riskCode ) || "NIK03".equals( riskCode ) || "NIK07".equals( riskCode ) || "NIK08".equals( riskCode ) || "NIK09".equals( riskCode ) || "MIK01".equals( riskCode ) || "MOK01".equals( riskCode ) || "MKK01".equals( riskCode ) ){
//			return true;
//		}
//		else {
//			return false;
//		}
//		if( !"88000403901".equals(grpContNo) ){
//			return false;
//		}
//		if( !"1".equals(planCode) ){
//			return false;
//		}
//		if( !"NIK01".equals(riskCode) && !"NIK02".equals(riskCode) && 
//				!"NIK03".equals(riskCode) && !"NIK07".equals(riskCode) && 
//				!"NIK08".equals(riskCode) && !"NIK09".equals(riskCode) && 
//				!"NIK12".equals(riskCode) && !"MIK01".equals(riskCode) && 
//				!"MOK01".equals(riskCode) && !"MKK01".equals(riskCode) ){
//			return false;
//		}
//		return true;
	}
	
}

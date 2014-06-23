package com.sinosoft.lis.claimanalysis.renewal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sinosoft.lis.claimanalysis.common.FileProcessor;
import com.sinosoft.lis.claimanalysis.common.ReportWriter;

public class RenewalReportWriter {
//	String file_path = "d:/renewal_analysis/";
	String file_path;
	String jasperPath;
	String relativePath;
	String mTaskDate;
	String operator;
	ReportWriter ReportWriter = new ReportWriter();
	FileDao FileDao = new FileDao();
	String DUTY_COL_NAMES = "�ֹ�˾����,���屣����,������Ч��,������ֹ��,�Ƿ�����,Ͷ����λ,Ͷ����λ����,��λ����,��ҵ���,��Ӫҵ��,�������˿ͻ���,������������,����������Ч��,����������ֹ��,��������ְҵ�ȼ�,��ְ������,�뱻�����˵Ĺ�ϵ,��������,�Ա�,�ƻ�����,�ƻ�����,����,����,����,����,����,��ҩ���޶�,�շ����޶�,�մ�λ���޶�,�ռ����޶�,�⸶����,�����⸶����,�������,����,�����,�ع���,�ع���2,���ý��,�Էѽ��,�����Ը����,ҽ��֧�����,ʵ���⸶���,����������,�������,��������,סԺ����,��ȫ����,���,������,������";
	String CLAIM_COL_NAMES = "�ֹ�˾����,���屣����,������Ч��,������ֹ��,�������˿ͻ���,��������,�Ա�,����������Ч��,����������ֹ��,��ְ������,�뱻�����˵Ĺ�ϵ,�ƻ�����,�ƻ�����,���������ⰸ��,��������,סԺ��������,סԺ����,��ҽҽԺ����,��ҽҽԺ����,ҽԺ�ȼ�,����ԭ��,�����⸶����,������Ŀ����,������Ŀ����,���ý��,�Էѽ��,�����Ը����,ҽ��֧�����,������Ŀ����,������Ŀ����,ʵ���⸶���,�⸶����,���ִ���,����ʱ���,����ʱ���,�����ӳ�ʱ��,���,������,�������޶�,�������޶�,����,�⸶����,�Ƿ�TPA���,�Ƿ�TPA�⸶";
	
	List files = new ArrayList();
	
	public void writeReports(String filePath, String jasperPath, String relativePath, String mTaskDate, String operator){
		this.file_path = filePath;
		this.jasperPath = jasperPath;
		this.relativePath = relativePath;
		this.mTaskDate = mTaskDate;
		this.operator = operator;
		
		// ����ݷָ�������ݴ��������ڴ����
		HashMap years = splitDS("match","csv",47);
		Set keySet = years.keySet();
		String[] s = new String[keySet.size()];
		int j=0;
		for(Iterator i = keySet.iterator(); i.hasNext(); ) {
			String key = (String)i.next();
			s[j++] = key;
		}
		Arrays.sort(s);

		years = splitDS("claim","csv",36);
		keySet = years.keySet();
		String[] s2 = new String[keySet.size()];
		j=0;
		for(Iterator i = keySet.iterator(); i.hasNext(); ) {
			String key = (String)i.next();
			s2[j++] = key;
		}
		Arrays.sort(s2);
		
		this.writeClaimRatioByYear(s);
		this.writeClaimRatioByGrpcont(s);
		this.writeInsuredChanges("Ա��", s);
		this.writeInsuredChanges("������������", s);
		this.getAgeDistributionDS(s);
		this.writeAgeDistribution("����������");
		this.writeAgeDistribution("������������");
		this.writeClaimProgress(s2); // claim.csv
		this.writeMedical1(s);
		this.writeMedical2(s);
		this.writeMedicalPlan1(s);
		this.writeMedicalPlan2(s);
		
		String fileName = "report.xls";
		
		FileProcessor FileProcessor = new FileProcessor();
		FileProcessor.mergeXls(files, filePath+fileName);
		
		FileDao FileDao = new FileDao();
		Date date = new Date();
		SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createDate = dateFm.format(date);
//		FileDao.addFile(relativePath, fileName, "��������", mTaskDate, operator, "report", "���ɳɹ�", createDate);
		
		for(int i=0;i<s.length;i++){
			String year = s[i];
			FileProcessor.deleteFile(file_path+"match_"+year+".csv");
		}
		for(int i=0;i<s2.length;i++){
			String year = s2[i];
			FileProcessor.deleteFile(file_path+"claim_"+year+".csv");
		}
	}
	
	// ����ݷָ�������ݴ��������ڴ����
	public HashMap splitDS(String fileName, String fileType, int keyNum){
		// match.csv 47
		// claim.csv 36
		HashMap years = new HashMap();
		String path = file_path + fileName + "." + fileType;
		try {
        	File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line; 
			String head = br.readLine();
			HashMap fileMap = new HashMap();
			FileProcessor fileProcessor = new FileProcessor();
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
				String key = row[keyNum]; // year
				Object o = fileMap.get(key);
				if( o == null ){
					StringBuffer s = new StringBuffer();
					s.append(head).append("\r\n");
					s.append(line).append("\r\n");
					fileMap.put(key, s);
				} else {
					StringBuffer s = (StringBuffer)o;
					
					if(s.length()>=1024*1024*10){
						fileProcessor.outputFile(s.toString(), file_path, fileName+"_"+key, fileType);
						s = new StringBuffer();
						fileMap.put(key, s);
					}
					
					s.append(line).append("\r\n");

				}
			}
			Set keySet = fileMap.keySet();
			for(Iterator i = keySet.iterator(); i.hasNext(); ) {
				String key = (String)i.next();
				StringBuffer s = (StringBuffer)fileMap.get(key);
//				FileProcessor FileProcessor = new FileProcessor();
				fileProcessor.outputFile(s.toString(), file_path, fileName+"_"+key, fileType);
				// years
				years.put(key, "");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return years;
	}
	
	public boolean exportXlsReport(String[] columnNames,String dataSource,HashMap params,String jasper,String sheetName,String report){
		if(ReportWriter.exportOneSheetXlsReport(columnNames, dataSource, params, jasper, sheetName, report)){
//			files.add(report);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean writeClaimRatioByYear(String[] y){
		List xlsFiles = new ArrayList();
		String newFile = file_path + "ClaimRatio_year.xls";
		String sheetName = "������";
		for(int i=0;i<y.length;i++){
			String year = y[i];
			String fileName = "ClaimRatio_year_"+year+".xls";
			String[] columnNames = DUTY_COL_NAMES.split(",");
			String dataSource = file_path + "match_"+year+".csv";
			String jasper = jasperPath + "claimratio_year.jasper";
			String report = file_path + fileName;
			HashMap params = new HashMap();
			if( !exportXlsReport(columnNames, dataSource, params, jasper, sheetName, report) ){
				return false;
			}
			xlsFiles.add(report);
		}
		
		FileProcessor FileProcessor = new FileProcessor();
		if( !FileProcessor.mergeXlsToOneSheet(xlsFiles, newFile, sheetName) ){
			return false;
		}
		
		files.add(newFile);
		return true;
	}
	
	public boolean writeClaimRatioByGrpcont(String[] y){
		List xlsFiles = new ArrayList();
		String newFile = file_path + "ClaimRatio_grpcont.xls";
		String sheetName = "������";
		for(int i=0;i<y.length;i++){
			String year = y[i];
			String fileName = "ClaimRatio_grpcont_"+year+".xls";
			String[] columnNames = DUTY_COL_NAMES.split(",");
			String dataSource = file_path + "match_"+year+".csv";
			String jasper = jasperPath + "claimratio_grpcont.jasper";
			String report = file_path + fileName;
			HashMap params = new HashMap();
			if( !exportXlsReport(columnNames, dataSource, params, jasper, sheetName, report) ){
				return false;
			}
			xlsFiles.add(report);
		}
		
		FileProcessor FileProcessor = new FileProcessor();
		if( !FileProcessor.mergeXlsToOneSheet(xlsFiles, newFile, sheetName) ){
			return false;
		}
		
		files.add(newFile);
		return true;
	}
	public boolean writeMedical1(String[] y){
		List xlsFiles = new ArrayList();
		String newFile = file_path + "medical_1.xls";
		String sheetName = "ҽ����";
		for(int i=0;i<y.length;i++){
			String year = y[i];
			String fileName = "medical_1_"+year+".xls";
			String[] columnNames = DUTY_COL_NAMES.split(",");
			String dataSource = file_path + "match_"+year+".csv";
			String jasper = jasperPath + "medical_1.jasper";
			String report = file_path + fileName;
			HashMap params = new HashMap();
			if( !exportXlsReport(columnNames, dataSource, params, jasper, sheetName, report) ){
				return false;
			}
			xlsFiles.add(report);
		}
		
		FileProcessor FileProcessor = new FileProcessor();
		if( !FileProcessor.mergeXlsToOneSheet(xlsFiles, newFile, sheetName) ){
			return false;
		}
		
		files.add(newFile);
		return true;
	}
	public boolean writeMedical2(String[] y){
		List xlsFiles = new ArrayList();
		String newFile = file_path + "medical_2.xls";
		String sheetName = "ҽ����";
		for(int i=0;i<y.length;i++){
			String year = y[i];
			String fileName = "medical_2_"+year+".xls";
			String[] columnNames = DUTY_COL_NAMES.split(",");
			String dataSource = file_path + "match_"+year+".csv";
			String jasper = jasperPath + "medical_2.jasper";
			String report = file_path + fileName;
			HashMap params = new HashMap();
			if( !exportXlsReport(columnNames, dataSource, params, jasper, sheetName, report) ){
				return false;
			}
			xlsFiles.add(report);
		}
		
		FileProcessor FileProcessor = new FileProcessor();
		if( !FileProcessor.mergeXlsToOneSheet(xlsFiles, newFile, sheetName) ){
			return false;
		}
		
		files.add(newFile);
		return true;
	}
	public boolean writeMedicalPlan1(String[] y){
		List xlsFiles = new ArrayList();
		String newFile = file_path + "medical_plan1.xls";
		String sheetName = "ҽ����";
		for(int i=0;i<y.length;i++){
			String year = y[i];
			String fileName = "medical_plan1_"+year+".xls";
			String[] columnNames = DUTY_COL_NAMES.split(",");
			String dataSource = file_path + "match_"+year+".csv";
			String jasper = jasperPath + "medical_plan1.jasper";
			String report = file_path + fileName;
			HashMap params = new HashMap();
			if( !exportXlsReport(columnNames, dataSource, params, jasper, sheetName, report) ){
				return false;
			}
			xlsFiles.add(report);
		}
		
		FileProcessor FileProcessor = new FileProcessor();
		if( !FileProcessor.mergeXlsToOneSheet(xlsFiles, newFile, sheetName) ){
			return false;
		}
		
		files.add(newFile);
		return true;
	}
	public boolean writeMedicalPlan2(String[] y){
		List xlsFiles = new ArrayList();
		String newFile = file_path + "medical_plan2.xls";
		String sheetName = "ҽ����";
		for(int i=0;i<y.length;i++){
			String year = y[i];
			String fileName = "medical_plan2_"+year+".xls";
			String[] columnNames = DUTY_COL_NAMES.split(",");
			String dataSource = file_path + "match_"+year+".csv";
			String jasper = jasperPath + "medical_plan2.jasper";
			String report = file_path + fileName;
			HashMap params = new HashMap();
			if( !exportXlsReport(columnNames, dataSource, params, jasper, sheetName, report) ){
				return false;
			}
			xlsFiles.add(report);
		}
		
		FileProcessor FileProcessor = new FileProcessor();
		if( !FileProcessor.mergeXlsToOneSheet(xlsFiles, newFile, sheetName) ){
			return false;
		}
		
		files.add(newFile);
		return true;
	}
	public boolean writeInsuredChanges( String relation, String[] y ){
		List xlsFiles = new ArrayList();
		String newFileName;
		if( "Ա��".equals(relation) ){
			newFileName = "InsuredChanges_staff";
		} else {
			newFileName = "InsuredChanges_related";
		}
		String newFile = file_path + newFileName + ".xls";
		String sheetName = relation + " ��Ա�仯";
		
		for(int i=0;i<y.length;i++){
			String year = y[i];
			String fileName = newFileName + "_" + year + ".xls";
			String[] columnNames = DUTY_COL_NAMES.split(",");
			String dataSource = file_path + "match_"+year+".csv";
			String jasper = jasperPath + "insuredchanges1.jasper";
			String report = file_path + fileName;
			HashMap params = new HashMap();
			params.put("�뱻�����˵Ĺ�ϵ", relation);
			if( !exportXlsReport(columnNames, dataSource, params, jasper, sheetName, report) ){
				return false;
			}
			xlsFiles.add(report);
		}
		
		FileProcessor FileProcessor = new FileProcessor();
		if( !FileProcessor.mergeXlsToOneSheet(xlsFiles, newFile, sheetName) ){
			return false;
		}
		
		files.add(newFile);
		return true;

	}
	
	
	public boolean getAgeDistributionDS(String[] y){
		int i;
		for(i=0;i<y.length;i++){
			String year = y[i];
			String[] columnNames = DUTY_COL_NAMES.split(",");
			String dataSource = file_path + "match_"+year+".csv";
			String jasper = jasperPath + "AgeDistribution0.jasper";
			String report = file_path + "AgeDistribution_DS_"+(i+1)+".csv";
			HashMap params = new HashMap();
			if( !ReportWriter.exportCsvReport(dataSource, columnNames, jasper, params, report, true) ){
				return false;
			}
		}
		FileProcessor FileProcessor = new FileProcessor();
		FileProcessor.outputFile("�������˿ͻ���,�Ա�,�뱻�����˵Ĺ�ϵ,��ȫ����,����,�����,���\r\n", file_path, "AgeDistribution_DS_0", "csv");
		FileProcessor.mergeFiles(file_path, "AgeDistribution_DS", 0, i, "csv", "AgeDistribution_DS");
		return true;
	}
	public boolean writeAgeDistribution( String relation ){
		String fileName;
		if( "����������".equals(relation) ){
			fileName = "AgeDistribution_staff.xls";
		} else {
			fileName = "AgeDistribution_related.xls";
		}
		
		String AGE_COL_NAMES = "�������˿ͻ���,�Ա�,�뱻�����˵Ĺ�ϵ,��ȫ����,����,�����,���";
		String[] columnNames = AGE_COL_NAMES.split(",");
		String dataSource = file_path + "AgeDistribution_DS.csv";
		String jasper = jasperPath + "AgeDistribution1.jasper";
		String report = file_path + fileName;
		HashMap params = new HashMap();
		params.put("�뱻�����˵Ĺ�ϵ", relation);
		String sheetName = relation + " ����ֲ�";
		
		if( exportXlsReport(columnNames, dataSource, params, jasper, sheetName, report) ){
			files.add(report);
		} else {
			return false;
		}
		
		return true;
	}
	public boolean writeClaimProgress(String[] y){
		List xlsFiles = new ArrayList();
		String newFile = file_path + "ClaimProgress.xls";
		String sheetName = "�����չ";
		for(int i=0;i<y.length;i++){
			String year = y[i];
			String fileName = "ClaimProgress_"+year+".xls";
			String[] columnNames = CLAIM_COL_NAMES.split(",");
			String dataSource = file_path + "claim_"+year+".csv";
			String jasper = jasperPath + "claimprogress.jasper";
			String report = file_path + fileName;
			HashMap params = new HashMap();
			if( !exportXlsReport(columnNames, dataSource, params, jasper, sheetName, report) ){
				return false;
			}
			xlsFiles.add(report);
		}
		
		FileProcessor FileProcessor = new FileProcessor();
		if( !FileProcessor.mergeXlsToOneSheet(xlsFiles, newFile, sheetName) ){
			return false;
		}
		
		files.add(newFile);
		return true;
	}
	public boolean outputJsp(String[] y){
		FileProcessor FileProcessor = new FileProcessor();
		int i;
		for(i=0;i<y.length;i++){
			String year = y[i];
			String dataSource = file_path+ "match_"+year+".csv" ;
			String[] columnNames = DUTY_COL_NAMES.split(",");
			String jasper = jasperPath+"CostPer2.jasper";
			HashMap params = new HashMap();
			boolean useFirstLine = true;
			String costPer = "CostPer2_"+year+".csv";
			String report = file_path + costPer;
			if(ReportWriter.exportCsvReport(dataSource, columnNames, jasper, params, report, useFirstLine)){
				this.createJsp(file_path, "predict", costPer, "predict_"+i);
				this.createJsp(file_path, "plan", costPer, "plan_"+i);
				FileProcessor.deleteFile(report);
			} else {
				return false;
			}
		}
		
		FileProcessor.mergeFiles(file_path, "predict", 0, i-1, "jsp", "predict");
		FileProcessor.mergeFiles(file_path, "plan", 0, i-1, "jsp", "plan");
		return true;
	}
	public boolean outputCostPer(){
		String dataSource = file_path+ "match.csv" ;
		String[] columnNames = DUTY_COL_NAMES.split(",");
		String jasper = jasperPath+"CostPer2.jasper";
		HashMap params = new HashMap();
		boolean useFirstLine = true;
		String report = file_path + "CostPer2.csv";
		return ReportWriter.exportCsvReport(dataSource, columnNames, jasper, params, report, useFirstLine);
		
	}
    
//    public void prepareMedicalDataSource(String planGroup, String dsFile){
//    	String path = this.file_path;
//    	// newPlanName1:grpContNo1_plan1,grpContNo2_plan2;newPlanName2:grpContNo3_plan3,grpContNo4_plan4;
//    	HashMap planMap;
//    	if( planGroup==null || "".equals(planGroup) ){
//    		planMap = null;
//    	}else{
//    		String[] groups = planGroup.split(";");
//    		planMap = new HashMap();
//    		for(int i=0;i<groups.length;i++){
//    			String[] contents = groups[i].split(":");
//    			String newPlanName = contents[0];
//    			String[] plans = contents[1].split(",");
//    			for(int j=0;j<plans.length;j++){
//    				String plan = plans[j];
//    				planMap.put(plan, newPlanName);
//    			}
//    		}
//    	}
//    	StringBuffer sb = new StringBuffer();
//    	sb.append(MEDICAL_COL_NAMES).append("\r\n");
//    	try {
//        	File file = new File(path+"match.csv");
//			BufferedReader br = new BufferedReader(new FileReader(file));
//			String line; 
//			br.readLine();
//			while ( (line = br.readLine()) != null ) {
//				String[] row = line.split(",");
//				// risk class
//				String riskCode = row[21];
//				String dutyCode = row[22];
//				String dutyClass = this.getMedicalDutyClass(riskCode, dutyCode);
//				if(dutyClass==null){
//					continue;
//				}
//				// plan groups
//				String grpContNo = row[1];
//				String planCode = row[19];
//				String plan = grpContNo + "_" + planCode;
//				if(planMap != null){
//					Object o = planMap.get(plan);
//					if( o == null ){
//						continue;
//					} else {
//						plan = (String)o;
//					}
//				}
//				// contents
//				// �ƻ�,���屣����,���η���,�Ա�,��¶��,ʵ���⸶���,�������,��������,��ȫ����
//				String sex = row[18];
//				String exposure = row[35];
//				String realPay = "".equals(row[41])?"0":row[41];
//				String times = "".equals(row[43])?"0":row[43];
//				String num = row[44];
//				String edorType = row[46];
//				String insuredNo = row[10];
//				sb.append(plan).append(",");
//				sb.append(grpContNo).append(",");
//				sb.append(insuredNo).append(",");
//				sb.append(dutyClass).append(",");
//				sb.append(sex).append(",");
//				sb.append(exposure).append(",");
//				sb.append(realPay).append(",");
//				sb.append(times).append(",");
//				sb.append(num).append(",");
//				sb.append(edorType).append("\r\n");
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		FileProcessor FileProcessor = new FileProcessor();
//		FileProcessor.outputFile(sb.toString(), path, dsFile, "csv");
//    }
//    // ���κ͸������ζ�Ӧ��ϵ.xls
//    public String getMedicalDutyClass( String riskCode, String dutyCode ){
//    	String op = "a ����OP";
//    	String ip = "b סԺIP";
//    	String partSelfPaid = "c �����Ը�part_self_paid";
//    	String selfPaid = "d �Է�self_paid";
//    	String maternity = "e ����maternity";
//    	String specifiedMaternity = "f �ر�Լ������specified maternity";
//    	String dentalCare = "g ���ƻ���dental care";
//    	String dentalTreatment = "h ��������dental treatment";
//    	String pp = "i ��������PP";
//    	String other = "j ����Լ��other";
//    	String tcm = "k ��ҽҽ��TCM";
//    	String dental = "l ����dental";
//    	String expandedMedical = "m ��չҽ��expanded medical";
//    	String medicalEquipment = "n �������踨������";
//    	String psychiatricCare = "o ���񼲲�סԺPsychiatric care";
//    	String aids = "p ���̲����䲢��֢AIDS/HIV treatment";
//    	String organTransplantation = "q �ش�������ֲOrgan transplantation";
//    	String nursingAtHome = "r ��ͥ����Nursing at home";
//    	String emergencyDental = "s ��������Emergency dental";
//    	String complicatedDentalCare = "t ������������ COMPLICATED DENTAL CARE";
//    	String eyeCare = "u �ۿƻ��� EYE CARE";
//    	String bodyCheckUp = "v Ԥ���Լ�� body check up";
//    	String smm = "w ���סԺ����SMM";
//    	String specialistClinic = "x ר������Specialist clinic";
//    	String physiotherapistChiropractorTreatment = "y �������ƻ�׵ָѹ����";
//    	String examinationLabTests = "z �������Examination & Lab Tests";
//    	if("MKK01".equals(riskCode)){
//    		return ip;
//    	}
//    	else if("NIK01".equals(riskCode)){
//    		if("615001".equals(dutyCode)){
//    			return ip;
//    		}
//    		else if("615002".equals(dutyCode)){
//    			return op;
//    		}
//    		else if("615003".equals(dutyCode)){
//    			return partSelfPaid;
//    		}
//    		else if("615004".equals(dutyCode)){
//    			return selfPaid;
//    		}
//    		else if("615005".equals(dutyCode)){
//    			return maternity;
//    		}
//    		else if("615006".equals(dutyCode)){
//    			return specifiedMaternity;
//    		}
//    		else if("615007".equals(dutyCode)){
//    			return dentalCare;
//    		}
//    		else if("615008".equals(dutyCode)){
//    			return dentalTreatment;
//    		}
//    		else if("615009".equals(dutyCode)){
//    			return pp;
//    		}
//    		else if("615010".equals(dutyCode)){
//    			return other;
//    		}
//    	}
//    	else if("NIK02".equals(riskCode)){
//    		if("617001".equals(dutyCode)){
//    			return ip;
//    		}
//    		else if("617002".equals(dutyCode)){
//    			return op;
//    		}
//    		else if("617003".equals(dutyCode)){
//    			return partSelfPaid;
//    		}
//    		else if("617004".equals(dutyCode)){
//    			return selfPaid;
//    		}
//    		else if("617005".equals(dutyCode)){
//    			return maternity;
//    		}
//    		else if("617006".equals(dutyCode)){
//    			return specifiedMaternity;
//    		}
//    		else if("617007".equals(dutyCode)){
//    			return dentalCare;
//    		}
//    		else if("617008".equals(dutyCode)){
//    			return dentalTreatment;
//    		}
//    		else if("617009".equals(dutyCode)){
//    			return pp;
//    		}
//    		else if("617010".equals(dutyCode)){
//    			return other;
//    		}
//    	}
//    	else if("NIK03".equals(riskCode)){
//    		if("612001".equals(dutyCode)){
//    			return ip;
//    		}
//    		else if("612002".equals(dutyCode)){
//    			return op;
//    		}
//    		else if("612003".equals(dutyCode)){
//    			return tcm;
//    		}
//    		else if("612004".equals(dutyCode)){
//    			return maternity;
//    		}
//    		else if("612005".equals(dutyCode)){
//    			return dental;
//    		}
//    		else if("612006".equals(dutyCode)){
//    			return expandedMedical;
//    		}
//    	}
//    	else if("NIK07".equals(riskCode)){
//    		if("637001".equals(dutyCode)){
//    			return ip;
//    		}
//    		else if("637002".equals(dutyCode)){
//    			return medicalEquipment;
//    		}
//    		else if("637003".equals(dutyCode)){
//    			return psychiatricCare;
//    		}
//    		else if("637004".equals(dutyCode)){
//    			return aids;
//    		}
//    		else if("637005".equals(dutyCode)){
//    			return organTransplantation;
//    		}
//    		else if("637006".equals(dutyCode)){
//    			return op;
//    		}
//    		else if("637007".equals(dutyCode)){
//    			return psychiatricCare;
//    		}
//    		else if("637008".equals(dutyCode)){
//    			return aids;
//    		}
//    		else if("637009".equals(dutyCode)){
//    			return tcm;
//    		}
//    		else if("637010".equals(dutyCode)){
//    			return nursingAtHome;
//    		}
//    		else if("637011".equals(dutyCode)){
//    			return maternity;
//    		}
//    		else if("637012".equals(dutyCode)){
//    			return emergencyDental;
//    		}
//    		else if("637013".equals(dutyCode)){
//    			return dentalCare;
//    		}
//    		else if("637014".equals(dutyCode)){
//    			return complicatedDentalCare;
//    		}
//    		else if("637015".equals(dutyCode)){
//    			return eyeCare;
//    		}
//    		else if("637016".equals(dutyCode)){
//    			return bodyCheckUp;
//    		}
//    	}
//    	else if("NIK08".equals(riskCode)){
//    		if("641001".equals(dutyCode)){
//    			return ip;
//    		}
//    		else if("641002".equals(dutyCode)){
//    			return psychiatricCare;
//    		}
//    		else if("641003".equals(dutyCode)){
//    			return aids;
//    		}
//    		else if("641004".equals(dutyCode)){
//    			return op;
//    		}
//    		else if("641005".equals(dutyCode)){
//    			return psychiatricCare;
//    		}
//    		else if("641006".equals(dutyCode)){
//    			return aids;
//    		}
//    		else if("641007".equals(dutyCode)){
//    			return tcm;
//    		}
//    		else if("641008".equals(dutyCode)){
//    			return maternity;
//    		}
//    		else if("641009".equals(dutyCode)){
//    			return dentalCare;
//    		}
//    		else if("641010".equals(dutyCode)){
//    			return bodyCheckUp;
//    		}
//    	}
//    	else if("NIK09".equals(riskCode)){
//    		if("642001".equals(dutyCode)){
//    			return ip;
//    		}
//    		else if("642002".equals(dutyCode)){
//    			return smm;
//    		}
//    		else if("642003".equals(dutyCode)){
//    			return op;
//    		}
//    		else if("642004".equals(dutyCode)){
//    			return specialistClinic;
//    		}
//    		else if("642005".equals(dutyCode)){
//    			return tcm;
//    		}
//    		else if("642006".equals(dutyCode)){
//    			return physiotherapistChiropractorTreatment;
//    		}
//    		else if("642007".equals(dutyCode)){
//    			return examinationLabTests;
//    		}
//    		else if("642008".equals(dutyCode)){
//    			return maternity;
//    		}
//    		else if("642009".equals(dutyCode)){
//    			return dentalCare;
//    		}
//    		else if("642010".equals(dutyCode)){
//    			return bodyCheckUp;
//    		}
//    	}
//    	return null;
//    }
	
	public void createJsp(String file_path, String type, String ds, String jspName){
		// type : predict / plan
		if ( !"predict".equals(type) && !"plan".equals(type) ){
			return;
		}
		String path = file_path + ds;
		try {
        	File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line; 
			br.readLine();
			String grpContNo = "";
			String planCode = "";
			String riskCode = "";
			StringBuffer content = new StringBuffer();
//			StringBuffer contPart = new StringBuffer();
			StringBuffer planPart = new StringBuffer();
			StringBuffer dutyPart = new StringBuffer();
			double amntCont = 0;
			double amntPlan = 0;
			boolean isFirstCont = true;
			while ( (line = br.readLine()) != null ) {
				String[] row = line.split(",");
				// grpcontno
				if(!"".equals(row[0])){
					if(!isFirstCont){
						planPart.append(this.getPlanPart(grpContNo, planCode, amntPlan, dutyPart));
						content.append(this.getContPart(grpContNo, amntCont, planPart));
						dutyPart = new StringBuffer();
						planPart = new StringBuffer();
						amntCont = 0;
						amntPlan = 0;
					}
					grpContNo = row[0];
					isFirstCont = false;
					continue;
				}
				// plan
				if(!"".equals(row[1])){
					if(dutyPart.length()>0){
						// type
						if ( "predict".equals(type) ){
							planPart.append(this.getPlanPart(grpContNo, planCode, amntPlan, dutyPart));
						} else {
							planPart.append(this.getPlanPart(grpContNo, planCode, amntPlan, new StringBuffer()));
						}
						dutyPart = new StringBuffer();
						amntPlan = 0;
					}
					planCode = row[1];
					continue;
				}
				// risk
				if(!"".equals(row[2])){
					riskCode = row[2];
					continue;
				}

				// duty
				String dutyCode = row[3];
				double amnt = Double.parseDouble(row[14]);
				String payRatio = row[15];
				String riskName = row[16];
				String dutyName = row[17];
				dutyPart.append(this.getDutyPart(grpContNo, planCode, riskCode, dutyCode, amnt, payRatio, riskName, dutyName));
				amntCont += amnt;
				amntPlan += amnt;
			}
			// type
			if ( "predict".equals(type) ){
				planPart.append(this.getPlanPart(grpContNo, planCode, amntPlan, dutyPart));
			} else {
				planPart.append(this.getPlanPart(grpContNo, planCode, amntPlan, new StringBuffer()));
			}
			content.append(this.getContPart(grpContNo, amntCont, planPart));
			FileProcessor FileProcessor = new FileProcessor();
			FileProcessor.outputFile(content.toString(), file_path, jspName, "jsp");
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private StringBuffer getDutyPart( String grpContNo, String planCode, String riskCode, String dutyCode, double totalamnt, String payRatio, String riskName, String dutyName ){
		DecimalFormat df=new DecimalFormat("#.00");
		String amnt = df.format(totalamnt);
		String checkboxName = grpContNo + "_" + planCode + "_duty";
		String checkboxValue = riskCode + "_" + dutyCode;
		String checkboxText = "����:" + riskName + " " + dutyName + "(���ִ��� "+riskCode+", ���δ��� "+dutyCode+",���� "+amnt+", �⸶���� "+payRatio+")"; 
		StringBuffer sb = new StringBuffer();
		sb.append("      <tr>\r\n");
		sb.append("        <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>\r\n");
		sb.append("	       <td class= titleImg><input type=checkbox name='"+checkboxName+"' value='"+checkboxValue+"' >"+checkboxText+"</td>\r\n");
		sb.append("      </tr>\r\n");
		return sb;
	}
	
	private StringBuffer getPlanPart( String grpContNo, String planCode, double totalamnt, StringBuffer dutyPart ){
		DecimalFormat df=new DecimalFormat("#.00");
		String amnt = df.format(totalamnt);
		String planDiv = "div_"+grpContNo+"_"+planCode;
		String checkboxName = grpContNo + "_plan";
		String checkboxValue = planCode;
		String checkboxText = "�ƻ�:"+planCode+"(���� "+amnt+")";
		StringBuffer sb = new StringBuffer();
		sb.append("  <table>\r\n");
		sb.append("    <tr>\r\n");
		sb.append("      <td>&nbsp;&nbsp;</td>\r\n");
		sb.append("      <td class=common>\r\n");
		sb.append("        <IMG  src= '../common/images/butExpand.gif' style= 'cursor:hand;' OnClick= 'showPage(this,"+planDiv+");'>\r\n");
		sb.append("      </td>\r\n");
		sb.append("      <td class= titleImg><input type=checkbox name='"+checkboxName+"' value='"+checkboxValue+"' onclick=\"checkPlan('"+grpContNo+"','"+planCode+"')\" >"+checkboxText+"</td>\r\n");
		sb.append("    </tr>\r\n");
		sb.append("  </table>\r\n");
		sb.append("  <Div  id= '"+planDiv+"' style= \"display: ''\">\r\n");
		sb.append("    <table>\r\n");
		sb.append(dutyPart);
		sb.append("    </table>\r\n");
		sb.append("  </Div>\r\n");
		return sb;
	}
	
	private StringBuffer getContPart( String grpContNo, double totalamnt, StringBuffer planPart ){
		DecimalFormat df=new DecimalFormat("#.00");
		String amnt = df.format(totalamnt);
		String contDiv = "div_"+grpContNo;
		String checkboxName = "cont";
		String checkboxValue = grpContNo;
		String checkboxText = "����:"+grpContNo+"(���� "+amnt+")";
		StringBuffer sb = new StringBuffer();
		sb.append("<table>\r\n");
		sb.append("  <tr>\r\n");
		sb.append("    <td class=common>\r\n");
		sb.append("      <IMG  src= '../common/images/butExpand.gif' style= 'cursor:hand;' OnClick= 'showPage(this,"+contDiv+");'>\r\n");
		sb.append("    </td>\r\n");
		sb.append("    <td class= titleImg><input type=checkbox name='"+checkboxName+"' value='"+checkboxValue+"' onclick=\"checkGrpCont('"+grpContNo+"')\" >"+checkboxText+"</td>\r\n");
		sb.append("  </tr>\r\n");
		sb.append("</table>\r\n");
		sb.append("<Div  id= '"+contDiv+"' style= \"display: ''\">\r\n");
		sb.append(planPart);
		sb.append("</Div>\r\n");
		return sb;
	}
    public static void main(String[] args) {
    	RenewalReportWriter RenewalReportWriter = new RenewalReportWriter();
    	String filePath = "D:/temp/88000403900_88000403901_88000425700_88000425701/";
    	filePath = "D:/tmp/data/new/";
    	String jasperPath = "D:/vss/lis2/ui/claimanalysis/renewal/jasper/";
    	RenewalReportWriter.file_path = filePath;
    	RenewalReportWriter.writeReports(filePath, jasperPath, "", "", "");
//    	RenewalReportWriter.writeMedicalByPlan(filePath, jasperPath, "", "", "", "");
//    	RenewalReportWriter.prepareMedicalDataSource("", "medical_test");
//    	RenewalReportWriter.writeMedicalByPlan(filePath, jasperPath, "", "", "");
    	
//    	RenewalReportWriter.prepareMedicalDataSource("1:88000403900_1,88000403901_1;1A:88000403900_1A,88000403901_1A;");
//    	RenewalReportWriter.writeReports(filePath, jasperPath);
//    	String planGroup = "";
//    	String id = "abc";
//    	RenewalReportWriter.writeMedicalByPlan(filePath, jasperPath, planGroup, id);
    }

}

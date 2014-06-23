package com.sinosoft.lis.claimanalysis.renewal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.sinosoft.lis.claimanalysis.common.FileProcessor;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;

//��Ա��Ϣ
public class InsuredInfo {
	public static HashMap hmDuty = null;
	
	private String branchCode;// �ֹ�˾����
	private String grpContNo;// ���屣����
	private Date polValiDate;// ������Ч��
	private Date polEndDate;// ������ֹ��
	private String repeatBill;//�Ƿ�����
	private String appntName;//Ͷ����λ
	private String grpNature;//��λ����
	private String businessType;//��ҵ���
	private String mainBussiness;//��Ӫҵ��
	private String customerNo;// �������˿ͻ���
	private String contno;//�������˺�ͬ��
	private Date cValiDate;// ����������Ч��
	private Date cEndDate;// ����������ֹ��
	private String occupationType;//��������ְҵ�ȼ�
	private String retireFlag;// ��ְ������
	private String relationToMainInsured;// �뱻�����˵Ĺ�ϵ
	private Date birthday;// ��������
	private String sex;// �Ա�
	private String contPlanCode;// �ƻ�
	private String contPlanName;// �ƻ�����
	private String riskCode;//����
	private String dutyCode;//����
	private double amnt;//����
	private double prem;//����
	private String payLine;// ����
	private double drugDailyLimit;// ��ҩ���޶�
	private double feeDailyLimit;// �շ����޶�
	private double bedDailyLimit;// �մ�λ���޶�
	private double checkDailyLimit;// �ռ����޶�
	private String payRatio;// �⸶����
	private double accidentPayRatio;//�����⸶����
	private String executeCom;//�������
	
	private int age;//����
	private String ageBand;//�����
	private double exposure;//�ع���
	private double exposure2;//�ع��� 2
	
	private String getDutyCode; // �⸶����
	
	// ƥ��
	private double expenseAmnt;// ���ý��
	private double ownExpenseAmnt;// �Էѽ��
	private double partExpenseAmnt;// �����Ը����
	private double ssExpenseAmnt;// ҽ��֧�����
	private double realPay;// ʵ���⸶���
	private int opip;// ����������
	private int visitTimes; //�������
	private int hospStayLength;// סԺ����
	
	// added
	private String appntDept; // �ͻ���֧��������
	private String customerName; // �ͻ�����
	private String posFlag; // ��ȫ��ʶ
	private Date payToDate; //��������
	
	private String businesstypein;
	private String gebclient;
	private String departmentid;
	private String occupationcode;
	private String grpState;
	private double claimmoney;// ������
	private double payable;// payable
	private double ssScope;// ҽ����Χ��
	
	public void setPayable(double payable){
		this.payable = payable;
	}
	
	public double getPayable(){
		return payable;
	}
	
	public void setSsScope(double ssScope){
		this.ssScope = ssScope;
	}
	
	public double getSsScope(){
		return ssScope;
	}
	
	public double getClaimmoney() {
		return claimmoney;
	}
	public void setClaimmoney(double claimmoney) {
		this.claimmoney = claimmoney;
	}
	
	public String getGrpState() {
		return grpState;
	}
	public void setGrpState(String grpState) {
		this.grpState = grpState;
	}
	public String getOccupationcode() {
		return occupationcode;
	}
	public void setOccupationcode(String occupationcode) {
		this.occupationcode = occupationcode;
	}
	public String getGebclient() {
		return gebclient;
	}
	public void setGebclient(String gebclient) {
		this.gebclient = gebclient;
	}
	public String getDepartmentid() {
		return departmentid;
	}
	public void setDepartmentid(String departmentid) {
		this.departmentid = departmentid;
	}
	
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getGrpContNo() {
		return grpContNo;
	}
	public void setGrpContNo(String grpContNo) {
		this.grpContNo = grpContNo;
	}
	public Date getPolValiDate() {
		return polValiDate;
	}
	public void setPolValiDate(Date polValiDate) {
		this.polValiDate = polValiDate;
	}
	public Date getPolEndDate() {
		return polEndDate;
	}
	public void setPolEndDate(Date polEndDate) {
		this.polEndDate = polEndDate;
	}
	public String getRepeatBill() {
		return repeatBill;
	}
	public void setRepeatBill(String repeatBill) {
		this.repeatBill = repeatBill;
	}
	public String getAppntName() {
		return appntName;
	}
	public void setAppntName(String appntName) {
		this.appntName = appntName;
	}
	public String getGrpNature() {
		return grpNature;
	}
	public void setGrpNature(String grpNature) {
		this.grpNature = grpNature;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public String getMainBussiness() {
		return mainBussiness;
	}
	public void setMainBussiness(String mainBussiness) {
		this.mainBussiness = mainBussiness;
	}
	public String getCustomerNo() {
		return customerNo;
	}
	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}
	public String getContno() {
		return contno;
	}
	public void setContno(String contno) {
		this.contno = contno;
	}
	public Date getCValiDate() {
		return cValiDate;
	}
	public void setCValiDate(Date valiDate) {
		cValiDate = valiDate;
	}
	public Date getCEndDate() {
		return cEndDate;
	}
	public void setCEndDate(Date endDate) {
		cEndDate = endDate;
	}
	public String getOccupationType() {
		return occupationType;
	}
	public void setOccupationType(String occupationType) {
		this.occupationType = occupationType;
	}
	public String getRetireFlag() {
		return retireFlag;
	}
	public void setRetireFlag(String retireFlag) {
		this.retireFlag = retireFlag;
	}
	public String getRelationToMainInsured() {
		return relationToMainInsured;
	}
	public void setRelationToMainInsured(String relationToMainInsured) {
		this.relationToMainInsured = relationToMainInsured;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getContPlanCode() {
		return contPlanCode;
	}
	public void setContPlanCode(String contPlanCode) {
		this.contPlanCode = contPlanCode;
	}
	public String getContPlanName() {
		return contPlanName;
	}
	public void setContPlanName(String contPlanName) {
		this.contPlanName = contPlanName;
	}
	public String getRiskCode() {
		return riskCode;
	}
	public void setRiskCode(String riskCode) {
		this.riskCode = riskCode;
	}
	public String getDutyCode() {
		return dutyCode;
	}
	public void setDutyCode(String dutyCode) {
		this.dutyCode = dutyCode;
	}
	public double getAmnt() {
		return amnt;
	}
	public void setAmnt(double amnt) {
		this.amnt = amnt;
	}
	public double getPrem() {
		return prem;
	}
	public void setPrem(double prem) {
		this.prem = prem;
	}
	public String getPayLine() {
		return payLine;
	}
	public void setPayLine(String payLine) {
		this.payLine = payLine;
	}
	public double getDrugDailyLimit() {
		return drugDailyLimit;
	}
	public void setDrugDailyLimit(double drugDailyLimit) {
		this.drugDailyLimit = drugDailyLimit;
	}
	public double getFeeDailyLimit() {
		return feeDailyLimit;
	}
	public void setFeeDailyLimit(double feeDailyLimit) {
		this.feeDailyLimit = feeDailyLimit;
	}
	public double getBedDailyLimit() {
		return bedDailyLimit;
	}
	public void setBedDailyLimit(double bedDailyLimit) {
		this.bedDailyLimit = bedDailyLimit;
	}
	public double getCheckDailyLimit() {
		return checkDailyLimit;
	}
	public void setCheckDailyLimit(double checkDailyLimit) {
		this.checkDailyLimit = checkDailyLimit;
	}
	public String getPayRatio() {
		return payRatio;
	}
	public void setPayRatio(String payRatio) {
		this.payRatio = payRatio;
	}
	public double getAccidentPayRatio() {
		return accidentPayRatio;
	}
	public void setAccidentPayRatio(double accidentPayRatio) {
		this.accidentPayRatio = accidentPayRatio;
	}
	public String getExecuteCom() {
		return executeCom;
	}
	public void setExecuteCom(String executeCom) {
		this.executeCom = executeCom;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getAgeBand() {
		return ageBand;
	}
	public void setAgeBand(String ageBand) {
		this.ageBand = ageBand;
	}
	public double getExposure() {
		return exposure;
	}
	public void setExposure(double exposure) {
		this.exposure = exposure;
	}
	public double getExposure2() {
		return exposure2;
	}
	public void setExposure2(double exposure2) {
		this.exposure2 = exposure2;
	}
	public void setGetDutyCode(String getDutyCode) {
		this.getDutyCode = getDutyCode;
	}
	public String getGetDutyCode() {
		return getDutyCode;
	}
	public double getExpenseAmnt() {
		return expenseAmnt;
	}
	public void setExpenseAmnt(double expenseAmnt) {
		this.expenseAmnt = expenseAmnt;
	}
	public double getOwnExpenseAmnt() {
		return ownExpenseAmnt;
	}
	public void setOwnExpenseAmnt(double ownExpenseAmnt) {
		this.ownExpenseAmnt = ownExpenseAmnt;
	}
	public double getPartExpenseAmnt() {
		return partExpenseAmnt;
	}
	public void setPartExpenseAmnt(double partExpenseAmnt) {
		this.partExpenseAmnt = partExpenseAmnt;
	}
	public double getSsExpenseAmnt() {
		return ssExpenseAmnt;
	}
	public void setSsExpenseAmnt(double ssExpenseAmnt) {
		this.ssExpenseAmnt = ssExpenseAmnt;
	}
	public double getRealPay() {
		return realPay;
	}
	public void setRealPay(double realPay) {
		this.realPay = realPay;
	}
	public int getOpip() {
		return opip;
	}
	public void setOpip(int opip) {
		this.opip = opip;
	}
	public void setHospStayLength(int hospStayLength) {
		this.hospStayLength = hospStayLength;
	}
	public int getHospStayLength() {
		return hospStayLength;
	}
	public void setVisitTimes(int visitTimes) {
		this.visitTimes = visitTimes;
	}
	public int getVisitTimes() {
		return visitTimes;
	}
	
	public String getAppntDept() {
		return appntDept;
	}
	public void setAppntDept(String appntDept) {
		this.appntDept = appntDept;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getPosFlag() {
		return posFlag;
	}
	public void setPosFlag(String posFlag) {
		this.posFlag = posFlag;
	}
	public Date getPayToDate() {
		return payToDate;
	}
	public void setPayToDate(Date payToDate) {
		this.payToDate = payToDate;
	}
	
	public String getBusinesstypein() {
		return businesstypein;
	}
	public void setBusinesstypein(String businesstypein) {
		this.businesstypein = businesstypein;
	}
	public static boolean outputInsuredInfoCsv( List list, String file_path, String file_name ){
    	
		StringBuffer strBuff = new StringBuffer();
	    SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd");
	    java.text.DecimalFormat decimalFm = new java.text.DecimalFormat( "#.##");
	    for(Iterator i = list.iterator(); i.hasNext(); ) {
	    	InsuredInfo insuredInfo = (InsuredInfo)i.next();
	    	
	    	strBuff.append(insuredInfo.getBranchCode());// �ֹ�˾����
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getGrpContNo());// ���屣����
	    	strBuff.append(",");
	    	String cValidate = dateFm.format(insuredInfo.getPolValiDate());
	    	strBuff.append(cValidate);// ������Ч��
	    	strBuff.append(",");
	    	strBuff.append(dateFm.format(insuredInfo.getPolEndDate()));// ������ֹ��
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getRepeatBill());// �Ƿ�����
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getAppntName());// Ͷ����λ
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getAppntDept());// Ͷ����λ-����
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getGrpNature());// ��λ����
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getBusinessType());// ��ҵ���
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getMainBussiness());// ��Ӫҵ��
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getCustomerNo());// �������˿ͻ���
	    	strBuff.append(",");
	    	
	    	strBuff.append(insuredInfo.getCustomerName().replaceAll(",", "��"));// ������������
	    	strBuff.append(",");
//	    	strBuff.append(insuredInfo.getContno());// �������˺�ͬ��
//	    	strBuff.append(",");
	    	strBuff.append(dateFm.format(insuredInfo.getCValiDate()));// ����������Ч��
	    	strBuff.append(",");
	    	strBuff.append(dateFm.format(insuredInfo.getCEndDate()));// ����������ֹ��
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getOccupationType());// ��������ְҵ�ȼ�
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getRetireFlag());// ��ְ������
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getRelationToMainInsured());// �뱻�����˵Ĺ�ϵ
	    	strBuff.append(",");
	    	strBuff.append(dateFm.format(insuredInfo.getBirthday()));// ��������
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getSex());// �Ա�
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getContPlanCode());// �ƻ�
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getContPlanName());// �ƻ�����
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getRiskCode());// ����
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getDutyCode());// ����
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getAmnt());// ����
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getPrem());// ����
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getPayLine());// ����
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getDrugDailyLimit());// ��ҩ���޶�
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getFeeDailyLimit());// �շ����޶�
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getBedDailyLimit());// �մ�λ���޶�
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getCheckDailyLimit());// �ռ����޶�
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getPayRatio());// �⸶����
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getAccidentPayRatio());// �����⸶����
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getExecuteCom());// �������
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getAge());// ����
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getAgeBand());// �����
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getExposure());// �ع���
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getExposure2());// �ع���
	    	strBuff.append(",");
//	    	str.append(this.format(insuredInfo.getGetDutyCode()));// �⸶����
			
	    	strBuff.append(insuredInfo.getExpenseAmnt());// ���ý��
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getOwnExpenseAmnt());// �Էѽ��
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getPartExpenseAmnt());// �����Ը����
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getSsExpenseAmnt());// ҽ��֧�����
	    	strBuff.append(",");
	    	/**
	    	 * 2011-5-4 ������ �绰���󣬽�RealPay Opip VisitTimes �����ֶΣ��Կմ���0
	    	 */
	    	if( insuredInfo.getRealPay() != 0 )
	    	strBuff.append(insuredInfo.getRealPay());// ʵ���⸶���
	    	strBuff.append(",");
	    	if( insuredInfo.getOpip() != 0 )
	    	strBuff.append(insuredInfo.getOpip());// ����������
	    	strBuff.append(",");
	    	if( insuredInfo.getVisitTimes() != 0 )
	    	strBuff.append(insuredInfo.getVisitTimes());// ������� 
	    	/**
	    	 * 2011-5-5 ������ �������󣬵�RealPay > 0 ʱ������Ϊ1 ������Ϊ0������ͳ��  ��������
	    	 * isVisit
	    	 */
	    	strBuff.append(",");
	    	if( insuredInfo.getRealPay() > 0 ){
	    		strBuff.append(1);
	    	} else {
	    		strBuff.append(0);
	    	}

	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getHospStayLength());// סԺ����
	    	//�����ʣ�������/���ѣ�
	    	//�����ʣ���������/��¶����
	    	//����������������/��������ı�¶����
	    	// �������
	    	//ÿ�ξ���������ã�������/���������
	    	//�˾������������/��¶����
	    	//�����ʣ��������/��¶���� 
	    	//ÿ�ξ��ﱣ�շ�Χ�ڷ��ã����շ�Χ�ڷ���/�������
	    	
	    	strBuff.append(",");
	    	String edortype = insuredInfo.getPosFlag();
	    	if( !"NI".equals(edortype) && !"ZT".equals(edortype) ){
	    		edortype = "-"; // last col
	    	}
	    	strBuff.append(edortype);// ��ȫ
	    	strBuff.append(",").append(cValidate.substring(0, 4)); // ���
	    	
	    	// ������
			strBuff.append(",").append( getRiskName( insuredInfo.getRiskCode() ) );
			// ������
			strBuff.append(",").append( getDutyName( insuredInfo.getDutyCode() ) );
			//�������ڣ�������������ڱ�������ֹ��֮����ȡ��������ֹ��
			if(insuredInfo.getPayToDate().after(insuredInfo.getCEndDate()))
			{
				strBuff.append(",").append(dateFm.format(insuredInfo.getCEndDate()));
			}else {
				strBuff.append(",").append(dateFm.format(insuredInfo.getPayToDate()));	
			}
			strBuff.append(",").append(insuredInfo.getBusinesstypein());
			strBuff.append(",").append(insuredInfo.getGebclient());
			strBuff.append(",").append(insuredInfo.getDepartmentid());
			strBuff.append(",").append(insuredInfo.getOccupationcode());
			strBuff.append(",").append(insuredInfo.getGrpState());
			strBuff.append(",").append(insuredInfo.getClaimmoney());
			strBuff.append(",").append(insuredInfo.getPayable());
			strBuff.append(",").append(insuredInfo.getSsScope());
	    	strBuff.append("\r\n");// ����
	    }
//	    if( !outputFile(strBuff.toString(), report_path, reqID + "_Match" + ++insSubFileNum, "csv") ){
//	    	return false;
//	    }
	    
	    FileProcessor FileProcessor = new FileProcessor();
	    if( !FileProcessor.outputFile(strBuff.toString(), file_path, file_name, "csv") ){
	    	return false;
	    }
    	
    	return true;
    }
	
	public static String getRiskName( String riskCode ){
		if( "MGK01".equals(riskCode) ) return "DD";
		else if( "MGK02".equals(riskCode) ) return "JDD";
		else if( "MGK03".equals(riskCode) ) return "JDD";
		else if( "MGK04".equals(riskCode) ) return "FDD";
		else if( "MIK01".equals(riskCode) ) return "AMR";
		else if( "MKK01".equals(riskCode) ) return "HI";
		else if( "MKK02".equals(riskCode) ) return "TPDI";
		else if( "MMK01".equals(riskCode) ) return "DI";
		else if( "MOK01".equals(riskCode) ) return "STAMR";
		else if( "MOK02".equals(riskCode) ) return "OEA";
		else if( "NAK01".equals(riskCode) ) return "TL";
		else if( "NAK02".equals(riskCode) ) return "TL";
		else if( "NHK01".equals(riskCode) ) return "AKM";
		else if( "NIK01".equals(riskCode) ) return "CMM";
		else if( "NIK02".equals(riskCode) ) return "SMM";
		else if( "NIK03".equals(riskCode) ) return "WMP";
		else if( "NIK04".equals(riskCode) ) return "FMM";
		else if( "NIK05".equals(riskCode) ) return "OEA-B";
		else if( "NIK06".equals(riskCode) ) return "OEA-C";
		else if( "NIK07".equals(riskCode) ) return "WMP-Exp";
		else if( "NIK08".equals(riskCode) ) return "WMP-HL1";
		else if( "NIK09".equals(riskCode) ) return "WMP-HL2";
		else if( "NIK10".equals(riskCode) ) return "WMP-Exp";
		else if( "NIK11".equals(riskCode) ) return "WMP-HL1";
		else if( "NIK12".equals(riskCode) ) return "SMM-BJ";
		else if( "NL05".equals(riskCode) ) return "STPCA";
		else if( "NLK01".equals(riskCode) ) return "STADD";
		else if( "NLK02".equals(riskCode) ) return "PCA";
		else if( "NLK03".equals(riskCode) ) return "OTADD";
		else if( "NMK01".equals(riskCode) ) return "ADD";
		else if( "NMK02".equals(riskCode) ) return "PAC-A";
		else if( "NMK03".equals(riskCode) ) return "PCA";
		else if( "NMK04".equals(riskCode) ) return "SWC";
		else if( "NMK05".equals(riskCode) ) return "ADD_loan";
		else if( "NMK06".equals(riskCode) ) return "NWRADD";
		else if( "NOK01".equals(riskCode) ) return "OEA";
		else if( "NOK02".equals(riskCode) ) return "OEA-A";
		return riskCode;
	}
	
	public static String getDutyName( String dutyCode ){
/*		if( "601001".equals(dutyCode) ) return "��������ս�";
		else if( "602001".equals(dutyCode) ) return "סԺ����";
		else if( "602002".equals(dutyCode) ) return "��������";
		else if( "602003".equals(dutyCode) ) return "�����Ը���Ŀ����";
		else if( "602004".equals(dutyCode) ) return "��ȫ�Է���Ŀ����";
		else if( "602005".equals(dutyCode) ) return "��ͨ��������";
		else if( "602006".equals(dutyCode) ) return "���ƻ�������";
		else if( "602007".equals(dutyCode) ) return "�ر�Լ����������";
		else if( "602008".equals(dutyCode) ) return "������������";
		else if( "602009".equals(dutyCode) ) return "������������";
		else if( "602010".equals(dutyCode) ) return "����Լ��ҽ�Ʒ�������1";
		else if( "602011".equals(dutyCode) ) return "����Լ��ҽ�Ʒ�������2";
		else if( "603001".equals(dutyCode) ) return "סԺ����";
		else if( "603002".equals(dutyCode) ) return "��������";
		else if( "603003".equals(dutyCode) ) return "��������";
		else if( "603004".equals(dutyCode) ) return "��������";
		else if( "603005".equals(dutyCode) ) return "�����������";
		else if( "603006".equals(dutyCode) ) return "����Լ������";
		else if( "604001".equals(dutyCode) ) return "��������";
		else if( "604002".equals(dutyCode) ) return "����м�������";
		else if( "604003".equals(dutyCode) ) return "��������������м�������";
		else if( "605001".equals(dutyCode) ) return "�������";
		else if( "605002".equals(dutyCode) ) return "������ʺ��������";
		else if( "605003".equals(dutyCode) ) return "ȫ��";
		else if( "605004".equals(dutyCode) ) return "������ʡ�������ʺ�ȫ��";
		else if( "606001".equals(dutyCode) ) return "�ɻ�����";
		else if( "606002".equals(dutyCode) ) return "�𳵼��ִ�����";
		else if( "606003".equals(dutyCode) ) return "��������";
		else if( "607001".equals(dutyCode) ) return "�ض������¹��������м�����������";
		else if( "608001".equals(dutyCode) ) return "ȫ������";
		else if( "609001".equals(dutyCode) ) return "סԺ����";
		else if( "609002".equals(dutyCode) ) return "�����˺�סԺ";
		else if( "610001".equals(dutyCode) ) return "����ҽҩ��������";
		else if( "611001".equals(dutyCode) ) return "������Ԯ(C��)����������";
		else if( "611002".equals(dutyCode) ) return "������Ԯ(C��)������������";
		else if( "612001".equals(dutyCode) ) return "סԺҽ�Ʊ��ս�";
		else if( "612002".equals(dutyCode) ) return "����ҽ�Ʊ��ս�";
		else if( "612003".equals(dutyCode) ) return "��ҽҽ�Ʊ��ս�";
		else if( "612004".equals(dutyCode) ) return "����ҽ�Ʊ��ս�";
		else if( "612005".equals(dutyCode) ) return "����ҽ�Ʊ��ս�";
		else if( "612006".equals(dutyCode) ) return "��չҽ�Ʊ��ս�";
		else if( "613001".equals(dutyCode) ) return "�ش󼲲�(10��)���ս�����";
		else if( "613002".equals(dutyCode) ) return "�ش󼲲�(20��)���ս�����";
		else if( "613003".equals(dutyCode) ) return "�ش󼲲�(30��)���ս�����";
		else if( "614001".equals(dutyCode) ) return "������Ԯ(B��)����������";
		else if( "614002".equals(dutyCode) ) return "������Ԯ(B��)������������";
		else if( "615001".equals(dutyCode) ) return "סԺ�������ض���Ŀ����";
		else if( "615002".equals(dutyCode) ) return "��������";
		else if( "615003".equals(dutyCode) ) return "�����Ը���Ŀ����";
		else if( "615004".equals(dutyCode) ) return "��ȫ�Է���Ŀ����";
		else if( "615005".equals(dutyCode) ) return "��ͨ��������";
		else if( "615006".equals(dutyCode) ) return "�ر�Լ����������";
		else if( "615007".equals(dutyCode) ) return "���ƻ�������";
		else if( "615008".equals(dutyCode) ) return "������������";
		else if( "615009".equals(dutyCode) ) return "������������";
		else if( "615010".equals(dutyCode) ) return "����Լ��ҽ�Ʒ�������1";
		else if( "615011".equals(dutyCode) ) return "����Լ��ҽ�Ʒ�������2";
		else if( "616001".equals(dutyCode) ) return "�ٶ��ؼ�����";
		else if( "617001".equals(dutyCode) ) return "סԺ�������ض���Ŀ����";
		else if( "617002".equals(dutyCode) ) return "��������";
		else if( "617003".equals(dutyCode) ) return "�����Ը���Ŀ����";
		else if( "617004".equals(dutyCode) ) return "��ȫ�Է���Ŀ����";
		else if( "617005".equals(dutyCode) ) return "��ͨ��������";
		else if( "617006".equals(dutyCode) ) return "�ر�Լ����������";
		else if( "617007".equals(dutyCode) ) return "���ƻ�������";
		else if( "617008".equals(dutyCode) ) return "������������";
		else if( "617009".equals(dutyCode) ) return "������������";
		else if( "617010".equals(dutyCode) ) return "����Լ��ҽ�Ʒ�������1";
		else if( "617011".equals(dutyCode) ) return "����Լ��ҽ�Ʒ�������2";
		else if( "622001".equals(dutyCode) ) return "�ɻ�����";
		else if( "622002".equals(dutyCode) ) return "������";
		else if( "622003".equals(dutyCode) ) return "�ִ�����";
		else if( "622004".equals(dutyCode) ) return "��������";
		else if( "623001".equals(dutyCode) ) return "�ٶ��ؼ�����";
		else if( "624001".equals(dutyCode) ) return "�����¹ʲм��������¹����";
		else if( "625001".equals(dutyCode) ) return "���⾳�������Ԯ����ҽ�Ʊ�������";
		else if( "626001".equals(dutyCode) ) return "���⾳�������Ԯ����ҽ�Ʊ��գ�A�����";
		else if( "627001".equals(dutyCode) ) return "�����񺽰������";
		else if( "627002".equals(dutyCode) ) return "�����г�����";
		else if( "627003".equals(dutyCode) ) return "�����ִ�����";
		else if( "627004".equals(dutyCode) ) return "����������λ�೵����";
		else if( "627005".equals(dutyCode) ) return "�������ʻһ�㽻ͨ��������";
		else if( "628001".equals(dutyCode) ) return "�����񺽰������";
		else if( "628002".equals(dutyCode) ) return "�����г�����";
		else if( "628003".equals(dutyCode) ) return "�����ִ�����";
		else if( "628004".equals(dutyCode) ) return "����������λ�೵����";
		else if( "628005".equals(dutyCode) ) return "�������ʻһ�㽻ͨ��������";
		else if( "629001".equals(dutyCode) ) return "����ҽҩ��������";
		else if( "632001".equals(dutyCode) ) return "һ�����˲о�ҵ�������һ���Թ���ҽ�Ʋ�����";
		else if( "632002".equals(dutyCode) ) return "һ�����˲о�ҵ������";
		else if( "632003".equals(dutyCode) ) return "һ���Թ���ҽ�Ʋ�����";
		else if( "632004".equals(dutyCode) ) return "һ�����˲о�ҵ������(�����ڽ���)";
		else if( "632005".equals(dutyCode) ) return "һ���Թ���ҽ�Ʋ�����(�����ڽ���)";
		else if( "634001".equals(dutyCode) ) return "Ů�Լ������ս�";
		else if( "635001".equals(dutyCode) ) return "ר�������";
		else if( "635002".equals(dutyCode) ) return "סԺ����";
		else if( "635003".equals(dutyCode) ) return "��������";
		else if( "635004".equals(dutyCode) ) return "��������";
		else if( "635005".equals(dutyCode) ) return "����Լ������";
		else if( "636001".equals(dutyCode) ) return "�����¹ʲм��������¹����";
		else if( "637001".equals(dutyCode) ) return "סԺ����";
		else if( "637002".equals(dutyCode) ) return "�������踨����������";
		else if( "637003".equals(dutyCode) ) return "���񼲲�סԺ����";
		else if( "637004".equals(dutyCode) ) return "���̲����䲢��֢סԺ����";
		else if( "637005".equals(dutyCode) ) return "�ش�������ֲ����";
		else if( "637006".equals(dutyCode) ) return "��������";
		else if( "637007".equals(dutyCode) ) return "���񼲲���������";
		else if( "637008".equals(dutyCode) ) return "���̲����䲢��֢��������";
		else if( "637009".equals(dutyCode) ) return "��ҽ����";
		else if( "637010".equals(dutyCode) ) return "��ͥ��������";
		else if( "637011".equals(dutyCode) ) return "����ҽ������";
		else if( "637012".equals(dutyCode) ) return "������������";
		else if( "637013".equals(dutyCode) ) return "�������Ƽ�Ԥ������������";
		else if( "637014".equals(dutyCode) ) return "������������";
		else if( "637015".equals(dutyCode) ) return "�ۿƻ�������";
		else if( "637016".equals(dutyCode) ) return "Ԥ���Լ������";
		else if( "638001".equals(dutyCode) ) return "���⸽�Ӿ��������Ԯ����ҽ�Ʊ�������";
		else if( "640001".equals(dutyCode) ) return "��ʺ�ȫ������";
		else if( "641001".equals(dutyCode) ) return "סԺ����";
		else if( "641002".equals(dutyCode) ) return "���񼲲�סԺ����";
		else if( "641003".equals(dutyCode) ) return "���̲����䲢��֢סԺ����";
		else if( "641004".equals(dutyCode) ) return "��������";
		else if( "641005".equals(dutyCode) ) return "���񼲲���������";
		else if( "641006".equals(dutyCode) ) return "���̲����䲢��֢��������";
		else if( "641007".equals(dutyCode) ) return "��ҽ��������";
		else if( "641008".equals(dutyCode) ) return "����ҽ������";
		else if( "641009".equals(dutyCode) ) return "����ҽ������";
		else if( "641010".equals(dutyCode) ) return "Ԥ���Լ������";
		else if( "642001".equals(dutyCode) ) return "סԺ����";
		else if( "642002".equals(dutyCode) ) return "���סԺ����ҽ������";
		else if( "642003".equals(dutyCode) ) return "��������";
		else if( "642004".equals(dutyCode) ) return "ר����������";
		else if( "642005".equals(dutyCode) ) return "��ҽ��������";
		else if( "642006".equals(dutyCode) ) return "�����׵ָѹ��������";
		else if( "642007".equals(dutyCode) ) return "��������������";
		else if( "642008".equals(dutyCode) ) return "����ҽ������";
		else if( "642009".equals(dutyCode) ) return "����ҽ������";
		else if( "642010".equals(dutyCode) ) return "Ԥ���Լ������";
		else if( "643001".equals(dutyCode) ) return "������ʡ��м�������";
		else if( "644001".equals(dutyCode) ) return "�����������";
		else if( "644002".equals(dutyCode) ) return "��������м�������";
		else if( "644003".equals(dutyCode) ) return "����������ʡ��м�������";
		else if( "646001".equals(dutyCode) ) return "��ͯ��������";
		else if( "691002".equals(dutyCode) ) return "��ʱ��ս�";
		return dutyCode;
*/
		if(hmDuty==null){
			hmDuty = new HashMap();
			ExeSQL mExeSQL = new ExeSQL();
			String sql = "select dutycode,dutyname from lmduty";
			SSRS aSSRS = mExeSQL.execSQL(sql);
			for (int i = 1; i <= aSSRS.getMaxRow(); i++) {
				hmDuty.put(aSSRS.GetText(i, 1), aSSRS.GetText(i, 2));
			}
		}
		return (String) hmDuty.get(dutyCode);
	}
	
}

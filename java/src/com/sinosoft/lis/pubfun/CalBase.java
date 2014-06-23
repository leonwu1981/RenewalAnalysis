/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

/**
 * <p>
 * ClassName: CalBase
 * </p>
 * <p>
 * Description: �������Ҫ�����ļ�
 * </p>
 * <p>
 * Description: ���з�������Ϊ�ڲ���Ա������ȡֵ��set��ͷΪ�棬get��ͷΪȡ
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: sinosoft
 * </p>
 * 
 * @Database: HST
 * @CreateDate��2002-07-30
 */
public class CalBase {
	// @Field
	/** ���� */
	private double Prem;

	/** ���� */
	private double Get;

	/** ���� */
	private double Mult;

	/** �ɷѼ�� */
	private int PayIntv;

	/** ��ȡ��� */
	private int GetIntv;

	/** �ɷ���ֹ���ڻ����� */
	private int PayEndYear;

	/** �ɷ���ֹ���ڻ������� */
	private String PayEndYearFlag;

	/** ��ȡ��ʼ���ڻ����� */
	private int GetYear;

	/** ��ȡ��ʼ���ڻ������� */
	private String GetYearFlag;

	/** ������ȡ���� */
	private String GetDutyKind;

	/** �������ڼ������ */
	private String StartDateCalRef;

	/** �����ڼ� */
	private int Years;

	/** �����ڼ� */
	private int InsuYear;

	/** �����ڼ��� */
	private String InsuYearFlag;

	/** ������Ͷ������ */
	private int AppAge;

	/** �������Ա� */
	private String Sex;

	/** �����˹��� */
	private String Job;

	/** ���θ������� */
	private String GDuty;

	/** Ͷ������ */
	private int Count;

	/** �������� */
	private int RnewFlag;

	/** ������ */
	private double AddRate;

	/** ������ͬ�� */
	private String ContNo;

	/** ������ */
	private String PolNo;

	/** ԭ���� */
	private double Amnt;

	/** �������� */
	private double FloatRate;

	/** ���ֱ��� */
	private String RiskCode;

	/** ���˱����������������ݼ�������Ĺ���Ԫ��--������ҵ���䱣���д�������ѡ�����ϴ��� */
	private String StandbyFlag1;

	/** ���� */
	private String StandbyFlag2;

	/** ������ҵ���䱣���д�����ְ�������ֶ� */
	private String StandbyFlag3;

	/** ����йص������ģ */
	private String StandbyFlag4;

	/** ���岹�乤��ҽ����ʱ���������洢�⸶�ı�����StandbyFlag1~StandbyFlag6�ֱ����˲��弶��ʮ�����⸶������ */
	private String StandbyFlag5;

	/** ���岹�乤��ҽ����ʱ���������洢�⸶�ı�����StandbyFlag1~StandbyFlag6�ֱ����˲��弶��ʮ�����⸶������ */
	private String StandbyFlag6;

	/** ��������������𷢷ű�׼ֵ */
	private double SendMoney;

	/** ������������������� */
	private double Float;

	/** �����������������ʼ���������뱣�ѽ����յĲ�� */
	private String BetweenDay;

	/** �����ͬ�� */
	private String GrpContNo;

	/** ���屣���� */
	private String GrpPolNo;

	private String EdorNo;

	/** �������� */
	private String CalType;

	/** ����* */
	private double GetLimit;

	/** �⸶����* */
	private double GetRate;

	/** �籣���* */
	private String SSFlag;

	/** �ⶥ��* */
	private double PeakLine;

	/** ������Ч��* */
	private String CValiDate;

	/** ���ϼƻ�����* */
	private String ContPlanCode;

	/** �����˿ͻ���* */
	private String InsuredNo;
	/** ���������˿ͻ���* */
	private String MainInsuredNo;

	/** ���α���* */
	private String DutyCode;

	/** ���Ѽ������* */
	private String CalRule;

	/** ��������н* */
	private double Salary;
	
	/** �������ͱ��* */
	private String PolTypeFlag;
	
	

	// @Constructor
	public CalBase() {
	}

	// @Method
	public void setCalRule(String tCalRule) {
		CalRule = tCalRule;
	}

	public String getCalRule() {
		return CalRule;
	}

	public void setDutyCode(String tDutyCode) {
		DutyCode = tDutyCode;
	}

	public String getDutyCode() {
		return DutyCode;
	}

	public void setContPlanCode(String tContPlanCode) {
		ContPlanCode = tContPlanCode;
	}

	public String getContPlanCode() {
		return ContPlanCode;
	}

	public void setInsuredNo(String tInsuredNo) {
		InsuredNo = tInsuredNo;
	}

	public String getInsuredNo() {
		return InsuredNo;
	}
	
	public void setMainInsuredNo(String tMainInsuredNo) {
		MainInsuredNo = tMainInsuredNo;
	}

	public String getMainInsuredNo() {
		return MainInsuredNo;
	}

	public void setContNo(String tContNo) {
		ContNo = tContNo;
	}

	public void setGrpContNo(String tGrpContNo) {
		GrpContNo = tGrpContNo;
	}

	public void setPrem(double tPrem) {
		Prem = tPrem;
	}

	public String getPrem() {
		return String.valueOf(Prem);
	}

	public void setGet(double tGet) {
		Get = tGet;
	}

	public String getGet() {
		return String.valueOf(Get);
	}

	public void setAmnt(double tAmnt) {
		Amnt = tAmnt;
	}

	public String getAmnt() {
		return String.valueOf(Amnt);
	}

	public void setMult(double tMult) {
		Mult = tMult;
	}

	public String getMult() {
		return String.valueOf(Mult);
	}

	public void setFloatRate(double tFloatRate) {
		FloatRate = tFloatRate;
	}

	public String getFloatRate() {
		return String.valueOf(FloatRate);
	}

	public void setAddRate(double tAddRate) {
		AddRate = tAddRate;
	}

	public String getContNo() {
		return ContNo;
	}

	public String getGrpContNo() {
		return GrpContNo;
	}

	public String getAddRate() {
		return String.valueOf(AddRate);
	}

	public void setPayIntv(int tPayIntv) {
		PayIntv = tPayIntv;
	}

	public String getPayIntv() {
		return String.valueOf(PayIntv);
	}

	public void setGetIntv(int tGetIntv) {
		GetIntv = tGetIntv;
	}

	public String getGetIntv() {
		return String.valueOf(GetIntv);
	}

	public void setPayEndYear(int tPayEndYear) {
		PayEndYear = tPayEndYear;
	}

	public String getPayEndYear() {
		return String.valueOf(PayEndYear);
	}

	public void setGetYear(int tGetYear) {
		GetYear = tGetYear;
	}

	public String getGetYear() {
		return String.valueOf(GetYear);
	}

	public void setYears(int tYears) {
		Years = tYears;
	}

	public String getYears() {
		return String.valueOf(Years);
	}

	public void setInsuYear(int tInsuYear) {
		InsuYear = tInsuYear;
	}

	public String getInsuYear() {
		return String.valueOf(InsuYear);
	}

	public void setAppAge(int tAppAge) {
		AppAge = tAppAge;
	}

	public String getAppAge() {
		return String.valueOf(AppAge);
	}

	public void setCount(int tCount) {
		Count = tCount;
	}

	public String getCount() {
		return String.valueOf(Count);
	}

	public void setRnewFlag(int tRnewFlag) {
		RnewFlag = tRnewFlag;
	}

	public String getRnewFlag() {
		return String.valueOf(RnewFlag);
	}

	public void setSex(String tSex) {
		Sex = tSex;
	}

	public String getSex() {
		return Sex;
	}

	public void setInsuYearFlag(String tInsuYearFlag) {
		InsuYearFlag = tInsuYearFlag;
	}

	public String getInsuYearFlag() {
		return InsuYearFlag;
	}

	public void setPayEndYearFlag(String tPayEndYearFlag) {
		PayEndYearFlag = tPayEndYearFlag;
	}

	public String getPayEndYearFlag() {
		return PayEndYearFlag;
	}

	public void setGetYearFlag(String tGetYearFlag) {
		GetYearFlag = tGetYearFlag;
	}

	public String getGetYearFlag() {
		return GetYearFlag;
	}

	public void setGetDutyKind(String tGetDutyKind) {
		GetDutyKind = tGetDutyKind;
	}

	public String getGetDutyKind() {
		return GetDutyKind;
	}

	public void setStartDateCalRef(String tStartDateCalRef) {
		StartDateCalRef = tStartDateCalRef;
	}

	public String getStartDateCalRef() {
		return StartDateCalRef;
	}

	public void setJob(String tJob) {
		Job = tJob;
	}

	public String getJob() {
		return Job;
	}

	public void setGDuty(String tGDuty) {
		GDuty = tGDuty;
	}

	public String getGDuty() {
		return GDuty;
	}

	public void setPolNo(String tPolNo) {
		PolNo = tPolNo;
	}

	public String getPolNo() {
		return PolNo;
	}

	public void setRiskCode(String tRiskCode) {
		RiskCode = tRiskCode;
	}

	public String getRiskCode() {
		return RiskCode;
	}

	public void setStandbyFlag1(String tStandbyFlag1) {
		StandbyFlag1 = tStandbyFlag1;
	}

	public String getStandbyFlag1() {
		return StandbyFlag1;
	}

	public void setStandbyFlag2(String tStandbyFlag2) {
		StandbyFlag2 = tStandbyFlag2;
	}

	public String getStandbyFlag2() {
		return StandbyFlag2;
	}

	public void setStandbyFlag3(String tStandbyFlag3) {
		StandbyFlag3 = tStandbyFlag3;
	}

	public String getStandbyFlag3() {
		return StandbyFlag3;
	}

	public String getStandbyFlag4() {
		return StandbyFlag4;
	}

	public void setStandbyFlag4(String tStandbyFlag4) {
		StandbyFlag4 = tStandbyFlag4;
	}

	public String getStandbyFlag5() {
		return StandbyFlag5;
	}

	public double getFloat() {
		return Float;
	}

	public void setFloat(double tFloat) {
		Float = tFloat;
	}

	public String getBetweenDay() {
		return BetweenDay;
	}

	public void setBetweenDay(String tBetweenDay) {
		BetweenDay = tBetweenDay;
	}

	public double getSendMoney() {
		return SendMoney;
	}

	public void setSendMoney(double tSendMoney) {
		SendMoney = tSendMoney;
	}

	public void setStandbyFlag5(String tStandbyFlag5) {
		StandbyFlag5 = tStandbyFlag5;
	}

	public String getStandbyFlag6() {
		return StandbyFlag6;
	}

	public void setStandbyFlag6(String tStandbyFlag6) {
		StandbyFlag6 = tStandbyFlag6;
	}

	public void setGrpPolNo(String tGrpPolNo) {
		GrpPolNo = tGrpPolNo;
	}

	public String getGrpPolNo() {
		return GrpPolNo;
	}

	public void setEdorNo(String tEdorNo) {
		EdorNo = tEdorNo;
	}

	public String getEdorNo() {
		return EdorNo;
	}

	public void setCalType(String tCalType) {
		CalType = tCalType;
	}

	public String getCalType() {
		return CalType;
	}

	public void setGetLimit(double tGetLimit) {
		GetLimit = tGetLimit;
	}

	public String getGetLimit() {
		return String.valueOf(GetLimit);
	}

	public void setGetRate(double tGetRate) {
		GetRate = tGetRate;
	}

	public String getGetRate() {
		return String.valueOf(GetRate);
	}

	public void setSSFlag(String tSSFlag) {
		SSFlag = tSSFlag;
	}

	public String getSSFlag() {
		return SSFlag;
	}

	public void setPeakLine(double tPeakLine) {
		PeakLine = tPeakLine;
	}

	public String getPeakLine() {
		return String.valueOf(PeakLine);
	}

	public void setCValiDate(String tCValiDate) {
		CValiDate = tCValiDate;
	}

	public String getCValiDate() {
		return CValiDate;
	}

	public void setSalary(double tSalary) {
		Salary = tSalary;
	}

	public String getSalary() {
		return String.valueOf(Salary);
	}
	
	public void setPolTypeFlag(String tPolTypeFlag) {
		PolTypeFlag = tPolTypeFlag;
	}

	public String getPolTypeFlag() {
		return PolTypeFlag;
	}

}

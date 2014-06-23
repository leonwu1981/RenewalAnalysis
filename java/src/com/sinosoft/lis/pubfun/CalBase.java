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
 * Description: 计算基础要素类文件
 * </p>
 * <p>
 * Description: 所有方法均是为内部成员变量存取值，set开头为存，get开头为取
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: sinosoft
 * </p>
 * 
 * @Database: HST
 * @CreateDate：2002-07-30
 */
public class CalBase {
	// @Field
	/** 保费 */
	private double Prem;

	/** 保额 */
	private double Get;

	/** 份数 */
	private double Mult;

	/** 缴费间隔 */
	private int PayIntv;

	/** 领取间隔 */
	private int GetIntv;

	/** 缴费终止年期或年龄 */
	private int PayEndYear;

	/** 缴费终止年期或年龄标记 */
	private String PayEndYearFlag;

	/** 领取开始年期或年龄 */
	private int GetYear;

	/** 领取开始年期或年龄标记 */
	private String GetYearFlag;

	/** 责任领取类型 */
	private String GetDutyKind;

	/** 起领日期计算参照 */
	private String StartDateCalRef;

	/** 保险期间 */
	private int Years;

	/** 保险期间 */
	private int InsuYear;

	/** 保险期间标记 */
	private String InsuYearFlag;

	/** 被保人投保年龄 */
	private int AppAge;

	/** 被保人性别 */
	private String Sex;

	/** 被保人工种 */
	private String Job;

	/** 责任给付编码 */
	private String GDuty;

	/** 投保人数 */
	private int Count;

	/** 续保次数 */
	private int RnewFlag;

	/** 递增率 */
	private double AddRate;

	/** 个单合同号 */
	private String ContNo;

	/** 保单号 */
	private String PolNo;

	/** 原保额 */
	private double Amnt;

	/** 浮动费率 */
	private double FloatRate;

	/** 险种编码 */
	private String RiskCode;

	/** 个人保单表中用来做传递计算变量的公用元素--团体商业补充保险中传递责任选择的组合代码 */
	private String StandbyFlag1;

	/** 待用 */
	private String StandbyFlag2;

	/** 团体商业补充保险中传递在职或退休字段 */
	private String StandbyFlag3;

	/** 存放有关的团体规模 */
	private String StandbyFlag4;

	/** 定义补充工伤医疗险时新增用作存储赔付的倍数（StandbyFlag1~StandbyFlag6分别是伤残五级到十级的赔付倍数） */
	private String StandbyFlag5;

	/** 定义补充工伤医疗险时新增用作存储赔付的倍数（StandbyFlag1~StandbyFlag6分别是伤残五级到十级的赔付倍数） */
	private String StandbyFlag6;

	/** 定义中意阳光年金发放标准值 */
	private double SendMoney;

	/** 定义中意阳光年金利率 */
	private double Float;

	/** 定义中意阳光年金起始发放日期与保费缴纳日的差额 */
	private String BetweenDay;

	/** 集体合同号 */
	private String GrpContNo;

	/** 集体保单号 */
	private String GrpPolNo;

	private String EdorNo;

	/** 计算类型 */
	private String CalType;

	/** 起付线* */
	private double GetLimit;

	/** 赔付比例* */
	private double GetRate;

	/** 社保标记* */
	private String SSFlag;

	/** 封顶线* */
	private double PeakLine;

	/** 保单生效日* */
	private String CValiDate;

	/** 保障计划编码* */
	private String ContPlanCode;

	/** 被保人客户号* */
	private String InsuredNo;
	/** 主被保险人客户号* */
	private String MainInsuredNo;

	/** 责任编码* */
	private String DutyCode;

	/** 保费计算规则* */
	private String CalRule;

	/** 被保人月薪* */
	private double Salary;
	
	/** 保单类型标记* */
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

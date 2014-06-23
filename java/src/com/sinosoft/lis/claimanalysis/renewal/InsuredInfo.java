package com.sinosoft.lis.claimanalysis.renewal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.sinosoft.lis.claimanalysis.common.FileProcessor;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;

//人员信息
public class InsuredInfo {
	public static HashMap hmDuty = null;
	
	private String branchCode;// 分公司代码
	private String grpContNo;// 团体保单号
	private Date polValiDate;// 保单生效日
	private Date polEndDate;// 保单终止日
	private String repeatBill;//是否续保
	private String appntName;//投保单位
	private String grpNature;//单位性质
	private String businessType;//行业类别
	private String mainBussiness;//主营业务
	private String customerNo;// 被保险人客户号
	private String contno;//被保险人合同号
	private Date cValiDate;// 被保险人生效日
	private Date cEndDate;// 被保险人终止日
	private String occupationType;//被保险人职业等级
	private String retireFlag;// 在职或退休
	private String relationToMainInsured;// 与被保险人的关系
	private Date birthday;// 出生年月
	private String sex;// 性别
	private String contPlanCode;// 计划
	private String contPlanName;// 计划名称
	private String riskCode;//险种
	private String dutyCode;//责任
	private double amnt;//保额
	private double prem;//保费
	private String payLine;// 起付线
	private double drugDailyLimit;// 日药费限额
	private double feeDailyLimit;// 日费用限额
	private double bedDailyLimit;// 日床位费限额
	private double checkDailyLimit;// 日检查费限额
	private String payRatio;// 赔付比例
	private double accidentPayRatio;//意外赔付比例
	private String executeCom;//服务机构
	
	private int age;//年龄
	private String ageBand;//年龄段
	private double exposure;//曝光数
	private double exposure2;//曝光数 2
	
	private String getDutyCode; // 赔付责任
	
	// 匹配
	private double expenseAmnt;// 费用金额
	private double ownExpenseAmnt;// 自费金额
	private double partExpenseAmnt;// 部分自付金额
	private double ssExpenseAmnt;// 医保支付金额
	private double realPay;// 实际赔付金额
	private int opip;// 门诊就诊次数
	private int visitTimes; //就诊次数
	private int hospStayLength;// 住院天数
	
	// added
	private String appntDept; // 客户分支机构名称
	private String customerName; // 客户姓名
	private String posFlag; // 保全标识
	private Date payToDate; //缴至日期
	
	private String businesstypein;
	private String gebclient;
	private String departmentid;
	private String occupationcode;
	private String grpState;
	private double claimmoney;// 理算金额
	private double payable;// payable
	private double ssScope;// 医保范围内
	
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
	    	
	    	strBuff.append(insuredInfo.getBranchCode());// 分公司代码
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getGrpContNo());// 团体保单号
	    	strBuff.append(",");
	    	String cValidate = dateFm.format(insuredInfo.getPolValiDate());
	    	strBuff.append(cValidate);// 保单生效日
	    	strBuff.append(",");
	    	strBuff.append(dateFm.format(insuredInfo.getPolEndDate()));// 保单终止日
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getRepeatBill());// 是否续保
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getAppntName());// 投保单位
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getAppntDept());// 投保单位-部门
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getGrpNature());// 单位性质
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getBusinessType());// 行业类别
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getMainBussiness());// 主营业务
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getCustomerNo());// 被保险人客户号
	    	strBuff.append(",");
	    	
	    	strBuff.append(insuredInfo.getCustomerName().replaceAll(",", "，"));// 被保险人姓名
	    	strBuff.append(",");
//	    	strBuff.append(insuredInfo.getContno());// 被保险人合同号
//	    	strBuff.append(",");
	    	strBuff.append(dateFm.format(insuredInfo.getCValiDate()));// 被保险人生效日
	    	strBuff.append(",");
	    	strBuff.append(dateFm.format(insuredInfo.getCEndDate()));// 被保险人终止日
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getOccupationType());// 被保险人职业等级
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getRetireFlag());// 在职或退休
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getRelationToMainInsured());// 与被保险人的关系
	    	strBuff.append(",");
	    	strBuff.append(dateFm.format(insuredInfo.getBirthday()));// 出生年月
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getSex());// 性别
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getContPlanCode());// 计划
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getContPlanName());// 计划名称
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getRiskCode());// 险种
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getDutyCode());// 责任
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getAmnt());// 保额
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getPrem());// 保费
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getPayLine());// 起付线
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getDrugDailyLimit());// 日药费限额
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getFeeDailyLimit());// 日费用限额
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getBedDailyLimit());// 日床位费限额
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getCheckDailyLimit());// 日检查费限额
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getPayRatio());// 赔付比例
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getAccidentPayRatio());// 意外赔付比例
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getExecuteCom());// 服务机构
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getAge());// 年龄
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getAgeBand());// 年龄段
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getExposure());// 曝光数
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getExposure2());// 曝光数
	    	strBuff.append(",");
//	    	str.append(this.format(insuredInfo.getGetDutyCode()));// 赔付责任
			
	    	strBuff.append(insuredInfo.getExpenseAmnt());// 费用金额
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getOwnExpenseAmnt());// 自费金额
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getPartExpenseAmnt());// 部分自付金额
	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getSsExpenseAmnt());// 医保支付金额
	    	strBuff.append(",");
	    	/**
	    	 * 2011-5-4 刘建新 电话需求，将RealPay Opip VisitTimes 三个字段，以空代替0
	    	 */
	    	if( insuredInfo.getRealPay() != 0 )
	    	strBuff.append(insuredInfo.getRealPay());// 实际赔付金额
	    	strBuff.append(",");
	    	if( insuredInfo.getOpip() != 0 )
	    	strBuff.append(insuredInfo.getOpip());// 门诊就诊次数
	    	strBuff.append(",");
	    	if( insuredInfo.getVisitTimes() != 0 )
	    	strBuff.append(insuredInfo.getVisitTimes());// 就诊次数 
	    	/**
	    	 * 2011-5-5 刘建新 会议需求，当RealPay > 0 时，设置为1 ，否则为0，用于统计  就诊人数
	    	 * isVisit
	    	 */
	    	strBuff.append(",");
	    	if( insuredInfo.getRealPay() > 0 ){
	    		strBuff.append(1);
	    	} else {
	    		strBuff.append(0);
	    	}

	    	strBuff.append(",");
	    	strBuff.append(insuredInfo.getHospStayLength());// 住院天数
	    	//理赔率＝理赔金额/保费，
	    	//发生率＝发生人数/暴露数，
	    	//就诊次数＝就诊次数/发生就诊的暴露数，
	    	// 就诊次数
	    	//每次就诊理赔费用＝理赔金额/就诊次数，
	    	//人均理赔金额＝理赔金额/暴露数，
	    	//利用率＝就诊次数/暴露数， 
	    	//每次就诊保险范围内费用＝保险范围内费用/就诊次数
	    	
	    	strBuff.append(",");
	    	String edortype = insuredInfo.getPosFlag();
	    	if( !"NI".equals(edortype) && !"ZT".equals(edortype) ){
	    		edortype = "-"; // last col
	    	}
	    	strBuff.append(edortype);// 保全
	    	strBuff.append(",").append(cValidate.substring(0, 4)); // 年份
	    	
	    	// 险种名
			strBuff.append(",").append( getRiskName( insuredInfo.getRiskCode() ) );
			// 责任名
			strBuff.append(",").append( getDutyName( insuredInfo.getDutyCode() ) );
			//缴至日期：如果缴至日期在被保人终止日之后则取被保人终止日
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
	    	strBuff.append("\r\n");// 换行
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
/*		if( "601001".equals(dutyCode) ) return "生活补贴保险金";
		else if( "602001".equals(dutyCode) ) return "住院责任";
		else if( "602002".equals(dutyCode) ) return "门诊责任";
		else if( "602003".equals(dutyCode) ) return "部分自付项目责任";
		else if( "602004".equals(dutyCode) ) return "完全自费项目责任";
		else if( "602005".equals(dutyCode) ) return "普通生育责任";
		else if( "602006".equals(dutyCode) ) return "牙科护理责任";
		else if( "602007".equals(dutyCode) ) return "特别约定生育责任";
		else if( "602008".equals(dutyCode) ) return "牙科治疗责任";
		else if( "602009".equals(dutyCode) ) return "公共保额责任";
		else if( "602010".equals(dutyCode) ) return "其它约定医疗费用责任1";
		else if( "602011".equals(dutyCode) ) return "其它约定医疗费用责任2";
		else if( "603001".equals(dutyCode) ) return "住院责任";
		else if( "603002".equals(dutyCode) ) return "门诊责任";
		else if( "603003".equals(dutyCode) ) return "生育责任";
		else if( "603004".equals(dutyCode) ) return "牙科责任";
		else if( "603005".equals(dutyCode) ) return "基金基本责任";
		else if( "603006".equals(dutyCode) ) return "其他约定责任";
		else if( "604001".equals(dutyCode) ) return "意外死亡";
		else if( "604002".equals(dutyCode) ) return "意外残疾和烧伤";
		else if( "604003".equals(dutyCode) ) return "意外死亡、意外残疾和烧伤";
		else if( "605001".equals(dutyCode) ) return "疾病身故";
		else if( "605002".equals(dutyCode) ) return "疾病身故和意外身故";
		else if( "605003".equals(dutyCode) ) return "全残";
		else if( "605004".equals(dutyCode) ) return "疾病身故、意外身故和全残";
		else if( "606001".equals(dutyCode) ) return "飞机责任";
		else if( "606002".equals(dutyCode) ) return "火车及轮船责任";
		else if( "606003".equals(dutyCode) ) return "汽车责任";
		else if( "607001".equals(dutyCode) ) return "特定意外事故死亡、残疾和烧伤责任";
		else if( "608001".equals(dutyCode) ) return "全残责任";
		else if( "609001".equals(dutyCode) ) return "住院治疗";
		else if( "609002".equals(dutyCode) ) return "意外伤害住院";
		else if( "610001".equals(dutyCode) ) return "意外医药补偿责任";
		else if( "611001".equals(dutyCode) ) return "紧急救援(C款)带门诊责任";
		else if( "611002".equals(dutyCode) ) return "紧急救援(C款)不带门诊责任";
		else if( "612001".equals(dutyCode) ) return "住院医疗保险金";
		else if( "612002".equals(dutyCode) ) return "门诊医疗保险金";
		else if( "612003".equals(dutyCode) ) return "中医医疗保险金";
		else if( "612004".equals(dutyCode) ) return "生育医疗保险金";
		else if( "612005".equals(dutyCode) ) return "牙科医疗保险金";
		else if( "612006".equals(dutyCode) ) return "扩展医疗保险金";
		else if( "613001".equals(dutyCode) ) return "重大疾病(10种)保险金责任";
		else if( "613002".equals(dutyCode) ) return "重大疾病(20种)保险金责任";
		else if( "613003".equals(dutyCode) ) return "重大疾病(30种)保险金责任";
		else if( "614001".equals(dutyCode) ) return "紧急救援(B款)带门诊责任";
		else if( "614002".equals(dutyCode) ) return "紧急救援(B款)不带门诊责任";
		else if( "615001".equals(dutyCode) ) return "住院和门诊特定项目责任";
		else if( "615002".equals(dutyCode) ) return "门诊责任";
		else if( "615003".equals(dutyCode) ) return "部分自付项目责任";
		else if( "615004".equals(dutyCode) ) return "完全自费项目责任";
		else if( "615005".equals(dutyCode) ) return "普通生育责任";
		else if( "615006".equals(dutyCode) ) return "特别约定生育责任";
		else if( "615007".equals(dutyCode) ) return "牙科护理责任";
		else if( "615008".equals(dutyCode) ) return "牙科治疗责任";
		else if( "615009".equals(dutyCode) ) return "公共保额责任";
		else if( "615010".equals(dutyCode) ) return "其它约定医疗费用责任1";
		else if( "615011".equals(dutyCode) ) return "其它约定医疗费用责任2";
		else if( "616001".equals(dutyCode) ) return "少儿重疾责任";
		else if( "617001".equals(dutyCode) ) return "住院和门诊特定项目责任";
		else if( "617002".equals(dutyCode) ) return "门诊责任";
		else if( "617003".equals(dutyCode) ) return "部分自付项目责任";
		else if( "617004".equals(dutyCode) ) return "完全自费项目责任";
		else if( "617005".equals(dutyCode) ) return "普通生育责任";
		else if( "617006".equals(dutyCode) ) return "特别约定生育责任";
		else if( "617007".equals(dutyCode) ) return "牙科护理责任";
		else if( "617008".equals(dutyCode) ) return "牙科治疗责任";
		else if( "617009".equals(dutyCode) ) return "公共保额责任";
		else if( "617010".equals(dutyCode) ) return "其它约定医疗费用责任1";
		else if( "617011".equals(dutyCode) ) return "其它约定医疗费用责任2";
		else if( "622001".equals(dutyCode) ) return "飞机责任";
		else if( "622002".equals(dutyCode) ) return "火车责任";
		else if( "622003".equals(dutyCode) ) return "轮船责任";
		else if( "622004".equals(dutyCode) ) return "汽车责任";
		else if( "623001".equals(dutyCode) ) return "少儿重疾责任";
		else if( "624001".equals(dutyCode) ) return "意外事故残疾和意外事故身故";
		else if( "625001".equals(dutyCode) ) return "中意境外紧急救援团体医疗保险责任";
		else if( "626001".equals(dutyCode) ) return "中意境外紧急救援团体医疗保险（A款）责任";
		else if( "627001".equals(dutyCode) ) return "乘坐民航班机责任";
		else if( "627002".equals(dutyCode) ) return "乘坐列车责任";
		else if( "627003".equals(dutyCode) ) return "乘坐轮船责任";
		else if( "627004".equals(dutyCode) ) return "乘坐汽车或单位班车责任";
		else if( "627005".equals(dutyCode) ) return "乘坐或驾驶一般交通工具责任";
		else if( "628001".equals(dutyCode) ) return "乘坐民航班机责任";
		else if( "628002".equals(dutyCode) ) return "乘坐列车责任";
		else if( "628003".equals(dutyCode) ) return "乘坐轮船责任";
		else if( "628004".equals(dutyCode) ) return "乘坐汽车或单位班车责任";
		else if( "628005".equals(dutyCode) ) return "乘坐或驾驶一般交通工具责任";
		else if( "629001".equals(dutyCode) ) return "意外医药补偿责任";
		else if( "632001".equals(dutyCode) ) return "一次性伤残就业补助金和一次性工伤医疗补助金";
		else if( "632002".equals(dutyCode) ) return "一次性伤残就业补助金";
		else if( "632003".equals(dutyCode) ) return "一次性工伤医疗补助金";
		else if( "632004".equals(dutyCode) ) return "一次性伤残就业补助金(适用于江苏)";
		else if( "632005".equals(dutyCode) ) return "一次性工伤医疗补助金(适用于江苏)";
		else if( "634001".equals(dutyCode) ) return "女性疾病保险金";
		else if( "635001".equals(dutyCode) ) return "专项疾病责任";
		else if( "635002".equals(dutyCode) ) return "住院责任";
		else if( "635003".equals(dutyCode) ) return "门诊责任";
		else if( "635004".equals(dutyCode) ) return "生育责任";
		else if( "635005".equals(dutyCode) ) return "其他约定责任";
		else if( "636001".equals(dutyCode) ) return "意外事故残疾和意外事故身故";
		else if( "637001".equals(dutyCode) ) return "住院责任";
		else if( "637002".equals(dutyCode) ) return "购买或租借辅助器材责任";
		else if( "637003".equals(dutyCode) ) return "精神疾病住院责任";
		else if( "637004".equals(dutyCode) ) return "艾滋病及其并发症住院责任";
		else if( "637005".equals(dutyCode) ) return "重大器官移植责任";
		else if( "637006".equals(dutyCode) ) return "门诊责任";
		else if( "637007".equals(dutyCode) ) return "精神疾病门诊责任";
		else if( "637008".equals(dutyCode) ) return "艾滋病及其并发症门诊责任";
		else if( "637009".equals(dutyCode) ) return "中医责任";
		else if( "637010".equals(dutyCode) ) return "家庭护理责任";
		else if( "637011".equals(dutyCode) ) return "生育医疗责任";
		else if( "637012".equals(dutyCode) ) return "紧急牙科责任";
		else if( "637013".equals(dutyCode) ) return "牙科诊疗及预防性治疗责任";
		else if( "637014".equals(dutyCode) ) return "复杂牙科责任";
		else if( "637015".equals(dutyCode) ) return "眼科护理责任";
		else if( "637016".equals(dutyCode) ) return "预防性检查责任";
		else if( "638001".equals(dutyCode) ) return "中意附加境外紧急救援团体医疗保险责任";
		else if( "640001".equals(dutyCode) ) return "身故和全残责任";
		else if( "641001".equals(dutyCode) ) return "住院责任";
		else if( "641002".equals(dutyCode) ) return "精神疾病住院责任";
		else if( "641003".equals(dutyCode) ) return "艾滋病及其并发症住院责任";
		else if( "641004".equals(dutyCode) ) return "门诊责任";
		else if( "641005".equals(dutyCode) ) return "精神疾病门诊责任";
		else if( "641006".equals(dutyCode) ) return "艾滋病及其并发症门诊责任";
		else if( "641007".equals(dutyCode) ) return "中医门诊责任";
		else if( "641008".equals(dutyCode) ) return "生育医疗责任";
		else if( "641009".equals(dutyCode) ) return "牙科医疗责任";
		else if( "641010".equals(dutyCode) ) return "预防性检查责任";
		else if( "642001".equals(dutyCode) ) return "住院责任";
		else if( "642002".equals(dutyCode) ) return "大额住院补充医疗责任";
		else if( "642003".equals(dutyCode) ) return "门诊责任";
		else if( "642004".equals(dutyCode) ) return "专科门诊责任";
		else if( "642005".equals(dutyCode) ) return "中医门诊责任";
		else if( "642006".equals(dutyCode) ) return "物理或脊椎指压门诊责任";
		else if( "642007".equals(dutyCode) ) return "检查检验门诊责任";
		else if( "642008".equals(dutyCode) ) return "生育医疗责任";
		else if( "642009".equals(dutyCode) ) return "牙科医疗责任";
		else if( "642010".equals(dutyCode) ) return "预防性检查责任";
		else if( "643001".equals(dutyCode) ) return "意外身故、残疾和烧伤";
		else if( "644001".equals(dutyCode) ) return "非因工意外身故";
		else if( "644002".equals(dutyCode) ) return "非因工意外残疾和烧伤";
		else if( "644003".equals(dutyCode) ) return "非因工意外身故、残疾和烧伤";
		else if( "646001".equals(dutyCode) ) return "儿童疾病责任";
		else if( "691002".equals(dutyCode) ) return "身故保险金";
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

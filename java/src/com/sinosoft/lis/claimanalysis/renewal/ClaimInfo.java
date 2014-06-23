package com.sinosoft.lis.claimanalysis.renewal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.sinosoft.lis.claimanalysis.common.FileProcessor;
import com.sinosoft.lis.claimanalysis.common.FormatConverter;

//理赔信息
public class ClaimInfo {
	private String branchCode;// 分公司代码
	private String grpContNo;// 团体保单号
	private Date polValiDate;// 保单生效日
	private Date polEndDate;// 保单终止日
	private String customerNo;// 被保险人客户号
	private Date birthday;// 出生年月
	private String sex;// 性别
	private Date cValiDate;// 被保险人生效日
	private Date cEndDate;// 被保险人终止日
	private String retireFlag;// 在职或退休
	private String relationToMainInsured;// 与被保险人的关系
	private String contPlanCode;// 计划
	private String contPlanName;// 计划名称
	private double payLine;// 起付线
	private double drugDailyLimit;// 日药费限额
	private double feeDailyLimit;// 日费用限额
	private double bedDailyLimit;// 日床位费限额
	private double checkDailyLimit;// 日检查费限额

	private String payRatio;// 赔付比例
	private String caseNo;// 被保险人赔案号
	private Date accidentDate;// 就诊（住院）发生日
	private Date leaveHospDate;// 住院结束日期
	private int hospStayLength;// 住院天数
	private String hospCode;// 就医医院代码
	private String hospName;// 就医医院代码
	private String hospClass;// 医院等级
	private String diseaseCode;// 索赔原因（icd10名字）
	private Date applyDate;// 申请赔付日期
	private String expenseCode;// 费用项目代码
	private String expenseName;// 费用项目名称
	private double expenseAmnt;// 费用金额
	private double ownExpenseAmnt;// 自费金额
	private double partExpenseAmnt;// 部分自付金额
	private double ssExpenseAmnt;// 医保支付金额
	private String getDutyCode;// 索赔项目代码
	private String getDutyName;// 索赔项目名称
	private double realPay;// 实际赔付金额
	private Date payDate;// 赔付日期
	private String riskCode;// 险种代码
	// 出险类型
	// 索赔金额
	private double clinicMaxPayLimit;// 日门诊限额
	private double amnt;//保额
	private String tpaflag;//是否TPA标记
	private String innetwork;//是否TPA赔付
	
	private int opip;// 门诊就诊次数
	private int visitTimes; // 就诊次数
	
	
	private String contNo;// 个单号
	
	private String businesstypein;
	private String customername;
	private String gebclient;
	private String departmentid;
	private String occupationcode;
	private String giveTypeDesc;
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
	
	public String getGiveTypeDesc() {
		return giveTypeDesc;
	}
	public void setGiveTypeDesc(String giveTypeDesc) {
		this.giveTypeDesc = giveTypeDesc;
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
	
	public String getCustomername() {
		return customername;
	}
	public void setCustomername(String customername) {
		this.customername = customername;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getBranchCode() {
		return branchCode;
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
	public String getCustomerNo() {
		return customerNo;
	}
	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
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
	public double getPayLine() {
		return payLine;
	}
	public void setPayLine(double payLine) {
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
	public double getClinicMaxPayLimit() {
		return clinicMaxPayLimit;
	}
	public void setClinicMaxPayLimit(double clinicMaxPayLimit) {
		this.clinicMaxPayLimit = clinicMaxPayLimit;
	}
	public double getAmnt() {
		return amnt;
	}
	public void setAmnt(double amnt) {
		this.amnt = amnt;
	}
	public String getTpaflag() {
		return tpaflag;
	}
	public void setTpaflag(String tpaflag) {
		this.tpaflag = tpaflag;
	}
	public String getInnetwork() {
		return innetwork;
	}
	public void setInnetwork(String innetwork) {
		this.innetwork = innetwork;
	}
	public String getPayRatio() {
		return payRatio;
	}
	public void setPayRatio(String payRatio) {
		this.payRatio = payRatio;
	}
	public String getCaseNo() {
		return caseNo;
	}
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}
	public Date getAccidentDate() {
		return accidentDate;
	}
	public void setAccidentDate(Date accidentDate) {
		this.accidentDate = accidentDate;
	}
	public Date getLeaveHospDate() {
		return leaveHospDate;
	}
	public void setLeaveHospDate(Date leaveHospDate) {
		this.leaveHospDate = leaveHospDate;
	}
	public int getHospStayLength() {
		return hospStayLength;
	}
	public void setHospStayLength(int hospStayLength) {
		this.hospStayLength = hospStayLength;
	}
	public String getHospCode() {
		return hospCode;
	}
	public void setHospCode(String hospCode) {
		this.hospCode = hospCode;
	}
	public String getHospName() {
		return hospName;
	}
	public void sethospName(String hospName) {
		this.hospName = hospName;
	}
	public String getHospClass() {
		return hospClass;
	}
	public void setHospClass(String hospClass) {
		this.hospClass = hospClass;
	}
	public String getDiseaseCode() {
		return diseaseCode;
	}
	public void setDiseaseCode(String diseaseCode) {
		this.diseaseCode = diseaseCode;
	}
	public Date getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}
	public String getExpenseCode() {
		return expenseCode;
	}
	public void setExpenseCode(String expenseCode) {
		this.expenseCode = expenseCode;
	}
	public String getExpenseName() {
		return expenseName;
	}
	public void setExpenseName(String expenseName) {
		this.expenseName = expenseName;
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
	public String getGetDutyCode() {
		return getDutyCode;
	}
	public void setGetDutyCode(String getDutyCode) {
		this.getDutyCode = getDutyCode;
	}
	public String getGetDutyName() {
		return getDutyName;
	}
	public void setGetDutyName(String getDutyName) {
		this.getDutyName = getDutyName;
	}
	public double getRealPay() {
		return realPay;
	}
	public void setRealPay(double realPay) {
		this.realPay = realPay;
	}
	public Date getPayDate() {
		return payDate;
	}
	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}
	public String getRiskCode() {
		return riskCode;
	}
	public void setRiskCode(String riskCode) {
		this.riskCode = riskCode;
	}
	public void setOpip(int opip) {
		this.opip = opip;
	}
	public int getOpip() {
		return opip;
	}
	public void setContNo(String contNo) {
		this.contNo = contNo;
	}
	public String getContNo() {
		return contNo;
	}
	public void setVisitTimes(int visitTimes) {
		this.visitTimes = visitTimes;
	}
	public int getVisitTimes() {
		return visitTimes;
	}
	
	public String getBusinesstypein() {
		return businesstypein;
	}
	public void setBusinesstypein(String businesstypein) {
		this.businesstypein = businesstypein;
	}
	public static boolean outputClaimInfoCsv( List list, String file_path, String file_name, char mPeriodFlag ){
    	
    	StringBuffer strBuff = new StringBuffer();
//	    SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd");
//	    java.text.DecimalFormat decimalFm = new java.text.DecimalFormat( "#.##");
    	for(Iterator i = list.iterator(); i.hasNext(); ) {
			ClaimInfo claimInfo = (ClaimInfo)i.next();
			//1分公司代码
			strBuff.append( claimInfo.getBranchCode() ).append(",");
			//2团体保单号
			strBuff.append( claimInfo.getGrpContNo() ).append(",");
			//3保单生效日
			String cValidate = FormatConverter.dateFm(claimInfo.getPolValiDate());
			strBuff.append( cValidate ).append(",");
			//4保单终止日
			strBuff.append( FormatConverter.dateFm(claimInfo.getPolEndDate()) ).append(",");
			//5被保险人客户号
			strBuff.append( claimInfo.getCustomerNo() ).append(",");
			//6出生年月
			strBuff.append( FormatConverter.dateFm(claimInfo.getBirthday()) ).append(",");
			//7性别
			strBuff.append( claimInfo.getSex() ).append(",");
			//8被保险人生效日
			strBuff.append( FormatConverter.dateFm(claimInfo.getCValiDate()) ).append(",");
			//9被保险人终止日
			strBuff.append( FormatConverter.dateFm(claimInfo.getCEndDate()) ).append(",");
			//10在职或退休
			strBuff.append( claimInfo.getRetireFlag() ).append(",");
			//11与被保险人的关系
			strBuff.append( claimInfo.getRelationToMainInsured() ).append(",");
			//12计划 : 后需调整
			strBuff.append( claimInfo.getContPlanCode() ).append(",");
			//13计划名称
			strBuff.append( claimInfo.getContPlanName() ).append(",");
			//14起付线、15日药费限额、16日费用限额、17日床位费限额、18日检查费限额、19赔付比例 
			//20被保险人赔案号
			strBuff.append( claimInfo.getCaseNo() ).append(",");
			//21就诊（住院）发生日(旧系统移植过来的案件,就诊日期修改为 出险日期)
			strBuff.append( FormatConverter.dateFm(claimInfo.getAccidentDate()) ).append(",");
			//22住院结束日期
			strBuff.append( FormatConverter.dateFm(claimInfo.getLeaveHospDate()) ).append(",");
			//23住院天数
			strBuff.append( claimInfo.getHospStayLength() ).append(",");
			//24就医医院代码
			strBuff.append( claimInfo.getHospCode() ).append(",");
			//24.5 （添加）就医医院名称
			strBuff.append( claimInfo.getHospName() ).append(",");
			//25医院等级
			strBuff.append( claimInfo.getHospClass() ).append(",");
			
			//26索赔原因（icd10名字）
			strBuff.append( claimInfo.getDiseaseCode() ).append(",");
			//27申请赔付日期
			strBuff.append( FormatConverter.dateFm(claimInfo.getApplyDate()) ).append(",");
			//28费用项目代码、29费用项目名称、30费用金额、31自费金额、32部分自付金额、33医保支付金额
			strBuff.append( claimInfo.getExpenseCode() ).append(",");
			strBuff.append( claimInfo.getExpenseName() ).append(",");
			strBuff.append( claimInfo.getExpenseAmnt() ).append(",");
			strBuff.append( claimInfo.getOwnExpenseAmnt() ).append(",");
			strBuff.append( claimInfo.getPartExpenseAmnt() ).append(",");
			strBuff.append( claimInfo.getSsExpenseAmnt() ).append(",");
			//34索赔项目代码
			strBuff.append( claimInfo.getGetDutyCode() ).append(",");
			//35索赔项目名称
			strBuff.append( claimInfo.getGetDutyName() ).append(",");
			//36实际赔付金额(同一责任第二条起置0)
			strBuff.append( claimInfo.getRealPay() ).append(",");
			//37赔付日期
			strBuff.append( FormatConverter.dateFm(claimInfo.getPayDate()) ).append(",");
			//38险种代码
			strBuff.append( claimInfo.getRiskCode() );
			//39出险类型
			//40索赔金额
			
			// 发生-理赔 日期
			String[] s = ClaimInfo.getPeriodInfo(claimInfo.getAccidentDate(), claimInfo.getPayDate(), mPeriodFlag);
			if( s == null ) {
				strBuff.append(",,,0");
			} else {
				strBuff.append(",");
				//acc date
				strBuff.append( s[0] );
				strBuff.append(",");
				//pay date
				strBuff.append( s[1] );
				strBuff.append(",");
				//length
				strBuff.append( s[2] );
			}
			
			// 年份
			strBuff.append(",").append( cValidate.substring(0, 4) );
			// 险种名
			strBuff.append(",").append( getRiskName( claimInfo.getRiskCode() ) ).append(",");
			
			//39门诊日限额
			strBuff.append( claimInfo.getClinicMaxPayLimit() ).append(",");
			//40检查费日限额
			strBuff.append( claimInfo.getCheckDailyLimit() ).append(",");
			//41保额
			strBuff.append( claimInfo.getAmnt() ).append(",");
			//42赔付比例
			strBuff.append( claimInfo.getPayRatio() ).append(",");
			//43是否TPA标记
			strBuff.append( claimInfo.getTpaflag() ).append(",");
			//44是否TPA赔付
			strBuff.append( claimInfo.getInnetwork() ).append(",");
			
			strBuff.append( claimInfo.getBusinesstypein() ).append(",");
			strBuff.append( claimInfo.getCustomername().replaceAll(",", "，") ).append(",");
			strBuff.append( claimInfo.getGebclient() ).append(",");
			strBuff.append( claimInfo.getDepartmentid() ).append(",");;
			strBuff.append( claimInfo.getOccupationcode() ).append(",");
			strBuff.append( claimInfo.getGiveTypeDesc() ).append(",");
			strBuff.append( claimInfo.getClaimmoney() ).append(",");
			strBuff.append( claimInfo.getPayable() ).append(",");
			strBuff.append( claimInfo.getSsScope() );
			strBuff.append("\r\n");// 换行
    	}
    	FileProcessor FileProcessor = new FileProcessor();
    	if( !FileProcessor.outputFile(strBuff.toString(), file_path, file_name, "csv") ){
	    	return false;
	    }
    	return true;
    }
	
	public static String[] getPeriodInfo( Date date1, Date date2, char mPeriodFlag ){
		if(date1==null){
			return null;
		}
		String[] r = new String[3];
		SimpleDateFormat dateFm = new SimpleDateFormat("yyyy/MM");
		if (date2==null) {
			String s1 = dateFm.format(date1);
			String[] dateTime1 = s1.split("/");
			int m1 = Integer.parseInt( dateTime1[1] );

			if( mPeriodFlag == 'M' ){
				r[0] = s1;
				r[1] = "";
				r[2] = "0";
			} else if( mPeriodFlag == 'Y' ){
				r[0] = dateTime1[0];
				r[1] = "";
				r[2] = "0";
			} else if( mPeriodFlag == 'Q' ){
				int q1 = (m1-1) / 3 + 1;
				r[0] = dateTime1[0]+"Q"+q1;
				r[1] = "";
				r[2] = "0";
			} else {
				return null;
			}
		}else {
			String s1 = dateFm.format(date1);
			String s2 = dateFm.format(date2);
			String[] dateTime1 = s1.split("/");
			String[] dateTime2 = s2.split("/");
			int y1 = Integer.parseInt( dateTime1[0] );
			int y2 = Integer.parseInt( dateTime2[0] );
			int m1 = Integer.parseInt( dateTime1[1] );
			int m2 = Integer.parseInt( dateTime2[1] );

			if( mPeriodFlag == 'M' ){
				r[0] = s1;
				r[1] = s2;
				int l = (y2-y1)*12 + m2 - m1;
				if(l<0){
					l=0;
				}
				r[2] = Integer.toString(l);
			} else if( mPeriodFlag == 'Y' ){
				r[0] = dateTime1[0];
				r[1] = dateTime2[0];
				int l = y2-y1;
				if(l<0){
					l=0;
				}
				r[2] = Integer.toString(l);
			} else if( mPeriodFlag == 'Q' ){
				int q1 = (m1-1) / 3 + 1;
				int q2 = (m2-1) / 3 + 1;
				r[0] = dateTime1[0]+"Q"+q1;
				r[1] = dateTime2[0]+"Q"+q2;
				int l = (y2-y1)*4 + q2 - q1;
				r[2] = Integer.toString(l);
			} else {
				return null;
			}
		}
		return r;
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
		if( "613001".equals(dutyCode) ) return "重大疾病(10种)给付";
		else if( "613002".equals(dutyCode) ) return "重大疾病(20种)给付";
		else if( "613003".equals(dutyCode) ) return "重大疾病(30种)给付";
		else if( "616001".equals(dutyCode) ) return "重大疾病保险金";
		else if( "623001".equals(dutyCode) ) return "重大疾病保险金";
		else if( "634001".equals(dutyCode) ) return "女性特定手术保险金2给付";
		else if( "634001".equals(dutyCode) ) return "女性特定手术保险金1给付";
		else if( "634001".equals(dutyCode) ) return "女性癌症保险金给付";
		else if( "634001".equals(dutyCode) ) return "女性原位癌保险金给付";
		else if( "610001".equals(dutyCode) ) return "意外医药补偿保险金";
		else if( "609001".equals(dutyCode) ) return "医院治疗责任给付";
		else if( "609002".equals(dutyCode) ) return "意外伤害重症监护病房治疗";
		else if( "608001".equals(dutyCode) ) return "全残责任给付";
		else if( "607001".equals(dutyCode) ) return "特定意外事故死亡给付";
		else if( "605001".equals(dutyCode) ) return "疾病身故责任给付";
		else if( "605002".equals(dutyCode) ) return "意外身故责任给付";
		else if( "605002".equals(dutyCode) ) return "疾病身故责任给付";
		else if( "605003".equals(dutyCode) ) return "全残责任给付";
		else if( "605004".equals(dutyCode) ) return "疾病身故责任给付";
		else if( "605004".equals(dutyCode) ) return "全残责任给付";
		else if( "605004".equals(dutyCode) ) return "意外身故责任给付";
		else if( "640001".equals(dutyCode) ) return "身故责任给付";
		else if( "640001".equals(dutyCode) ) return "全残责任给付";
		else if( "615001".equals(dutyCode) ) return "住院和门诊特定项目责任保险金";
		else if( "615002".equals(dutyCode) ) return "门诊责任保险金";
		else if( "615003".equals(dutyCode) ) return "部分自付项目责任保险金";
		else if( "615004".equals(dutyCode) ) return "完全自费项目责任保险金";
		else if( "615005".equals(dutyCode) ) return "普通生育责任保险金";
		else if( "615006".equals(dutyCode) ) return "特别约定生育责任保险金";
		else if( "615007".equals(dutyCode) ) return "牙科护理责任保险金";
		else if( "615008".equals(dutyCode) ) return "牙科治疗责任保险金";
		else if( "615009".equals(dutyCode) ) return "公共保额责任保险金";
		else if( "615010".equals(dutyCode) ) return "其它约定医疗费用责任保险金";
		else if( "617001".equals(dutyCode) ) return "住院和门诊特定项目责任保险金";
		else if( "617002".equals(dutyCode) ) return "门诊责任保险金";
		else if( "617003".equals(dutyCode) ) return "部分自付项目责任保险金";
		else if( "617004".equals(dutyCode) ) return "完全自费项目责任保险金";
		else if( "617005".equals(dutyCode) ) return "普通生育责任保险金";
		else if( "617006".equals(dutyCode) ) return "特别约定生育责任保险金";
		else if( "617007".equals(dutyCode) ) return "牙科护理责任保险金";
		else if( "617008".equals(dutyCode) ) return "牙科治疗责任保险金";
		else if( "617009".equals(dutyCode) ) return "公共保额责任保险金";
		else if( "617010".equals(dutyCode) ) return "其它约定医疗费用责任保险金";
		else if( "612001".equals(dutyCode) ) return "住院医疗保险金给付";
		else if( "612002".equals(dutyCode) ) return "门诊医疗保险金给付";
		else if( "612003".equals(dutyCode) ) return "中医医疗保险金给付";
		else if( "612004".equals(dutyCode) ) return "生育医疗保险金给付";
		else if( "612005".equals(dutyCode) ) return "牙科医疗保险金给付";
		else if( "612006".equals(dutyCode) ) return "扩展医疗保险金给付";
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
		else if( "622001".equals(dutyCode) ) return "飞机事故残疾给付";
		else if( "622001".equals(dutyCode) ) return "飞机事故死亡给付";
		else if( "622002".equals(dutyCode) ) return "火车事故残疾给付";
		else if( "622002".equals(dutyCode) ) return "火车事故死亡给付";
		else if( "622003".equals(dutyCode) ) return "轮船事故残疾给付";
		else if( "622003".equals(dutyCode) ) return "轮船事故死亡给付";
		else if( "622004".equals(dutyCode) ) return "汽车事故死亡给付";
		else if( "622004".equals(dutyCode) ) return "汽车事故残疾给付";
		else if( "624001".equals(dutyCode) ) return "意外事故身故保险金";
		else if( "624001".equals(dutyCode) ) return "意外事故残疾保险金";
		else if( "627001".equals(dutyCode) ) return "交通工具残疾给付";
		else if( "627001".equals(dutyCode) ) return "交通工具身故给付";
		else if( "627002".equals(dutyCode) ) return "交通工具残疾给付";
		else if( "627002".equals(dutyCode) ) return "交通工具身故给付";
		else if( "627003".equals(dutyCode) ) return "交通工具残疾给付";
		else if( "627003".equals(dutyCode) ) return "交通工具身故给付";
		else if( "627004".equals(dutyCode) ) return "交通工具身故给付";
		else if( "627004".equals(dutyCode) ) return "交通工具残疾给付";
		else if( "627005".equals(dutyCode) ) return "交通工具身故给付";
		else if( "627005".equals(dutyCode) ) return "交通工具残疾给付";
		else if( "636001".equals(dutyCode) ) return "意外事故身故保险金";
		else if( "636001".equals(dutyCode) ) return "意外事故残疾保险金";
		else if( "604001".equals(dutyCode) ) return "意外身故保险金";
		else if( "604002".equals(dutyCode) ) return "意外残疾和烧伤保险金";
		else if( "604003".equals(dutyCode) ) return "意外身故保险金";
		else if( "604003".equals(dutyCode) ) return "意外残疾和烧伤保险金";
		else if( "606001".equals(dutyCode) ) return "飞机事故残疾给付";
		else if( "606001".equals(dutyCode) ) return "飞机事故死亡给付";
		else if( "606002".equals(dutyCode) ) return "火车及轮船事故死亡给付";
		else if( "606002".equals(dutyCode) ) return "火车及轮船事故残疾给付";
		else if( "606003".equals(dutyCode) ) return "汽车事故残疾给付";
		else if( "606003".equals(dutyCode) ) return "汽车事故死亡给付";
		else if( "628001".equals(dutyCode) ) return "交通工具身故给付";
		else if( "628001".equals(dutyCode) ) return "交通工具残疾给付";
		else if( "628002".equals(dutyCode) ) return "交通工具残疾给付";
		else if( "628002".equals(dutyCode) ) return "交通工具身故给付";
		else if( "628003".equals(dutyCode) ) return "交通工具身故给付";
		else if( "628003".equals(dutyCode) ) return "交通工具残疾给付";
		else if( "628004".equals(dutyCode) ) return "交通工具残疾给付";
		else if( "628004".equals(dutyCode) ) return "交通工具身故给付";
		else if( "628005".equals(dutyCode) ) return "交通工具身故给付";
		else if( "628005".equals(dutyCode) ) return "交通工具残疾给付";
		else if( "632001".equals(dutyCode) ) return "一次性伤残就业补助和一次性工伤医疗补助金";
		else if( "632002".equals(dutyCode) ) return "一次性伤残就业补助金";
		else if( "632003".equals(dutyCode) ) return "一次性工伤医疗补助金";
		else if( "632004".equals(dutyCode) ) return "一次性伤残就业补助金(适用于江苏)";
		else if( "632005".equals(dutyCode) ) return "一次性工伤医疗补助金(适用于江苏)";
		return dutyCode;
	}
	
}

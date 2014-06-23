/*
 * <p>ClassName: BqCalBase </p>
 * <p>Description: 保全计算基础要素类文件 </p>
 * <p>Description: 所有方法均是为内部成员变量存取值，set开头为存，get开头为取,方式源于CalBase,要素描述属于保全 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft </p>
 * @Database: TJJ
 * @CreateDate：2002-10-02
 */
package com.sinosoft.lis.pubfun;

public class BqCalBase
{
    // @Field
    /** 保费 */
    private double Prem;


    /** 累计保费 */
    private double SumPrem;


    /** 保额 */
    private double Get;


    /** 份数 */
    private double Mult;


    /** 缴费间隔 */
    private int PayIntv;


    /** 领取间隔 */
    private int GetIntv;


    /**领取开始年龄年期标志*/
    private String GetStartFlag;


    /**交费止期年龄年期标志*/
    private String PayEndYearFlag;


    /** 缴费终止年期或年龄 */
    private int PayEndYear;


    /** 领取开始年期 */
    private int GetStartYear;


    /** 领取开始年龄 */
    private int GetStartAge;


    /** 领取时投保年期 */
    private int GetAppYear;


    /** 领取时领取年期 */
    private int GetYear;


    /** 领取时年龄 */
    private int GetAge;


    /** 领取次数 */
    private int GetTimes;


    /** 保险期间 */
    private int Years;


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


    /** 递增率 */
    private double AddRate;


    /** 险种保单号 */
    private String PolNo;


    /** 集体险种保单号 */
    private String GrpPolNo;


    /** 合同保单号 */
    private String ContNo;


    /** 集体合同保单号 */
    private String GrpContNo;


    /**时间间隔*/
    private int Interval;


    /**限制时间间隔*/
    private int LimitDay;


    /**保单失效标志*/
    private int PolValiFlag; //1无效　；0有效


    /**借款金额*/
    private double LoanMoney;


    /**限制时间间隔*/
    private double TrayMoney;


    /**操作员编码**/
    private String Operator;


    /**保全申请号*/
    private String EdorAcceptNo;


    /**保全批单号*/
    private String EdorNo;


    /**保全类型*/
    private String EdorType;


    /**批改生效日期*/
    private String EdorValiDate;


    /**交退费金额*/
    private double GetMoney;


    /**起领日期*/
    private String GetStartDate;


    /**起保日期*/
    private String CValiDate;


    /**帐户转年金金额*/
    private double GetBalance;


    /**险种编码*/
    private String RiskCode;


    /**浮动费率*/
    private double FloatRate;


    /**保险期间*/
    private int InsuYear;


    /**保险期间标志（单位）*/
    private String InsuYearFlag;


    /**保单通用字段*/
    private String StandByFlag1;


    /**舍弃法保单年度**/
    private String DownPolYears;


    /**约进法保单年度**/
    private String UpPolYears;


    /** 最近一期保费 */
    private double LastPrem;


    /**退保点**/
    private String ZTPoint;


    /**交至日期**/
    private String PayToDate;


    /**保全申请日期**/
    private String EdorAppDate;


    /**保全重算后保费**/
    private double NewPrem;


    /**计算类型*/
    private String CalType;


    /** 责任领取类型 */
    private String GetDutyKind;


    /** 年龄间隔 */
    private int AgeInterval;

    /** 被保人客户号 */
    private String InsuredNo;

    /** 已经理赔标识 */
    private String HavePay;

    /**责任编码 */
    private String DutyCode;
    
    
    /** 累计赔付金额 */
	private String LJPFJT;
	
	/** 算法类型 */
    private String ArithType;

	/** 定义中意阳光年金发放标准值 */
	private double SendMoney;

	// @Constructor
	public BqCalBase() {
	}

	public double getSendMoney() {
		return SendMoney;
	}

	public void setSendMoney(double tSendMoney) {
		SendMoney = tSendMoney;
	}

	public void setDutyCode(String tDutyCode) {
		DutyCode = tDutyCode;
	}
    
    public void setLJPFJT(String aLJPFJT)
    {
    	LJPFJT = aLJPFJT;
    }
    public void setArithType(String aArithType)
    {
    	ArithType = aArithType;
    }
    
    public String getDutyCode()
    {
        return DutyCode;
    }
    public void setHavePay(String mHavePay)
        {
            HavePay = mHavePay;
        }

        public String getHavePay()
        {
            return HavePay;
        }

    public void setEdorAppDate(String tEdorAppDate)
    {
        EdorAppDate = tEdorAppDate;
    }

    public String getEdorAppDate()
    {
        return EdorAppDate;
    }

    public void setInsuredNo(String tInsuredNo)
    {
        InsuredNo = tInsuredNo;
    }

    public String getInsuredNo()
    {
        return InsuredNo;
    }


    public void setAgeInterval(int tAgeInterval)
    {
        AgeInterval = tAgeInterval;
    }

    public String getAgeInterval()
    {
        return String.valueOf(AgeInterval);
    }


    public void setDownPolYears(String tDownPolYears)
    {
        DownPolYears = tDownPolYears;
    }

    public String getDownPolYears()
    {
        return DownPolYears;
    }

    public void setUpPolYears(String tUpPolYears)
    {
        UpPolYears = tUpPolYears;
    }

    public String getUpPolYears()
    {
        return UpPolYears;
    }

    public void setZTPoint(String tZTPoint)
    {
        ZTPoint = tZTPoint;
    }

    public String getZTPoint()
    {
        return ZTPoint;
    }

    public void setPayToDate(String tPayToDate)
    {
        PayToDate = tPayToDate;
    }

    public String getPayToDate()
    {
        return PayToDate;
    }


    // @Method
    public void setInsuYear(int tInsuYear)
    {
        InsuYear = tInsuYear;
    }

    public String getInsuYear()
    {
        return String.valueOf(InsuYear);
    }

    public void setInsuYearFlag(String tInsuYearFlag)
    {
        InsuYearFlag = tInsuYearFlag;
    }

    public String getInsuYearFlag()
    {
        return InsuYearFlag;
    }

    public void setInterval(int tInterval)
    {
        Interval = tInterval;
    }

    public String getInterval()
    {
        return String.valueOf(Interval);
    }

    public void setLimitDay(int tLimitDay)
    {
        LimitDay = tLimitDay;
    }

    public String getLimitDay()
    {
        return String.valueOf(LimitDay);
    }
    
    public String getLJPFJT()
    {
        return LJPFJT;
    }
    public String getArithType()
    {
        return ArithType;
    }
    public void setPolValiFlag(int tPolValiFlag)
    {
        PolValiFlag = tPolValiFlag;
    }

    public String getPolValiFlag()
    {
        return String.valueOf(PolValiFlag);
    }


    public String getLoanMoney()
    {
        return String.valueOf(LoanMoney);
    }

    public void setLoanMoney(double tLoanMoney)
    {
        LoanMoney = tLoanMoney;
    }

    public String getTrayMoney()
    {
        return String.valueOf(TrayMoney);
    }

    public void setTrayMoney(double tTrayMoney)
    {
        TrayMoney = tTrayMoney;
    }

    public String getOperator()
    {
        return String.valueOf(Operator);
    }

    public void setOperator(String tOperator)
    {
        Operator = tOperator;
    }


    public void setPrem(double tPrem)
    {
        Prem = tPrem;
    }

    public String getPrem()
    {
        return String.valueOf(Prem);
    }

    public void setSumPrem(double tSumPrem)
    {
        SumPrem = tSumPrem;
    }

    public String getSumPrem()
    {
        return String.valueOf(SumPrem);
    }

    public void setGet(double tGet)
    {
        Get = tGet;
    }

    public String getGet()
    {
        return String.valueOf(Get);
    }

    public void setMult(double tMult)
    {
        Mult = tMult;
    }

    public String getMult()
    {
        return String.valueOf(Mult);
    }

    public void setAddRate(double tAddRate)
    {
        AddRate = tAddRate;
    }

    public String getAddRate()
    {
        return String.valueOf(AddRate);
    }

    public void setPayIntv(int tPayIntv)
    {
        PayIntv = tPayIntv;
    }

    public String getPayIntv()
    {
        return String.valueOf(PayIntv);
    }

    public void setGetIntv(int tGetIntv)
    {
        GetIntv = tGetIntv;
    }

    public String getGetIntv()
    {
        return String.valueOf(GetIntv);
    }

    public void setPayEndYear(int tPayEndYear)
    {
        PayEndYear = tPayEndYear;
    }

    public String getPayEndYear()
    {
        return String.valueOf(PayEndYear);
    }

    public void setGetYear(int tGetYear)
    {
        GetYear = tGetYear;
    }

    public String getGetYear()
    {
        return String.valueOf(GetYear);
    }

    public void setGetAppYear(int tGetAppYear)
    {
        GetAppYear = tGetAppYear;
    }

    public String getGetAppYear()
    {
        return String.valueOf(GetAppYear);
    }

    public void setGetAge(int tGetAge)
    {
        GetAge = tGetAge;
    }

    public String getGetAge()
    {
        return String.valueOf(GetAge);
    }

    public void setGetStartYear(int tGetStartYear)
    {
        GetStartYear = tGetStartYear;
    }

    public String getGetStartYear()
    {
        return String.valueOf(GetStartYear);
    }

    public void setGetStartAge(int tGetStartAge)
    {
        GetStartAge = tGetStartAge;
    }

    public String getGetStartAge()
    {
        return String.valueOf(GetStartAge);
    }

    public void setGetTimes(int tGetTimes)
    {
        GetTimes = tGetTimes;
    }

    public String getGetTimes()
    {
        return String.valueOf(GetTimes);
    }

    public void setYears(int tYears)
    {
        Years = tYears;
    }

    public String getYears()
    {
        return String.valueOf(Years);
    }

    public void setAppAge(int tAppAge)
    {
        AppAge = tAppAge;
    }

    public String getAppAge()
    {
        return String.valueOf(AppAge);
    }

    public void setCount(int tCount)
    {
        Count = tCount;
    }

    public String getCount()
    {
        return String.valueOf(Count);
    }

    public void setSex(String tSex)
    {
        Sex = tSex;
    }

    public String getSex()
    {
        return Sex;
    }

    public void setJob(String tJob)
    {
        Job = tJob;
    }

    public String getJob()
    {
        return Job;
    }

    public void setGDuty(String tGDuty)
    {
        GDuty = tGDuty;
    }

    public String getGDuty()
    {
        return GDuty;
    }

    public void setPolNo(String tPolNo)
    {
        PolNo = tPolNo;
    }

    public String getPolNo()
    {
        return PolNo;
    }

    public void setGrpPolNo(String tGrpPolNo)
    {
        GrpPolNo = tGrpPolNo;
    }

    public String getGrpPolNo()
    {
        return GrpPolNo;
    }

    public void setContNo(String tContNo)
    {
        ContNo = tContNo;
    }

    public String getContNo()
    {
        return ContNo;
    }

    public void setGrpContNo(String tGrpContNo)
    {
        GrpContNo = tGrpContNo;
    }

    public String getGrpContNo()
    {
        return GrpContNo;
    }

    public void setEdorAcceptNo(String tEdorAcceptNo)
    {
        EdorAcceptNo = tEdorAcceptNo;
    }

    public String getEdorAcceptNo()
    {
        return EdorAcceptNo;
    }

    public void setEdorNo(String tEdorNo)
    {
        EdorNo = tEdorNo;
    }

    public String getEdorNo()
    {
        return EdorNo;
    }

    public void setEdorType(String tEdorType)
    {
        EdorType = tEdorType;
    }


    public String getEdorType()
    {
        return EdorType;
    }

    public void setGetMoney(double tGetMoney)
    {
        GetMoney = tGetMoney;
    }

    public String getGetMoney()
    {
        return String.valueOf(GetMoney);
    }

    public void setCValiDate(String tCValiDate)
    {
        CValiDate = tCValiDate;
    }

    public String getCValiDate()
    {
        return CValiDate;
    }

    public void setEdorValiDate(String tEdorValiDate)
    {
        EdorValiDate = tEdorValiDate;
    }

    public String getEdorValiDate()
    {
        return EdorValiDate;
    }

    public void setGetStartDate(String tGetStartDate)
    {
        GetStartDate = tGetStartDate;
    }

    public String getGetStartDate()
    {
        return GetStartDate;
    }

    public void setGetBalance(double tGetBalance)
    {
        GetBalance = tGetBalance;
    }

    public String getGetBalance()
    {
        return String.valueOf(GetBalance);
    }

    public void setRiskCode(String tRiskCode)
    {
        RiskCode = tRiskCode;
    }

    public String getRiskCode()
    {
        return RiskCode;
    }

    public void setPayEndYearFlag(String tPayEndYearFlag)
    {
        PayEndYearFlag = tPayEndYearFlag;
    }

    public String getPayEndYearFlag()
    {
        return PayEndYearFlag;
    }

    public void setGetStartFlag(String tGetStartFlag)
    {
        GetStartFlag = tGetStartFlag;
    }

    public String getGetStartFlag()
    {
        return GetStartFlag;
    }

    public void setFloatRate(double tFloatRate)
    {
        FloatRate = tFloatRate;
    }

    public String getFloatRate()
    {
        return String.valueOf(FloatRate);
    }

    public void setStandByFlag1(String tStandByFlag1)
    {
        StandByFlag1 = tStandByFlag1;
    }

    public String getStandByFlag1()
    {
        return StandByFlag1;
    }

    public void setNewPrem(double tNewPrem)
    {
        NewPrem = tNewPrem;
    }

    public String getNewPrem()
    {
        return String.valueOf(NewPrem);
    }

    public void setCalType(String tCalType)
    {
        CalType = tCalType;
    }

    public String getCalType()
    {
        return CalType;
    }

    public void setGetDutyKind(String tGetDutyKind)
    {
        GetDutyKind = tGetDutyKind;
    }

    public String getGetDutyKind()
    {
        return GetDutyKind;
    }


}

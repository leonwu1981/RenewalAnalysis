/*
 * <p>ClassName: BqCalBase </p>
 * <p>Description: ��ȫ�������Ҫ�����ļ� </p>
 * <p>Description: ���з�������Ϊ�ڲ���Ա������ȡֵ��set��ͷΪ�棬get��ͷΪȡ,��ʽԴ��CalBase,Ҫ���������ڱ�ȫ </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft </p>
 * @Database: TJJ
 * @CreateDate��2002-10-02
 */
package com.sinosoft.lis.pubfun;

public class BqCalBase
{
    // @Field
    /** ���� */
    private double Prem;


    /** �ۼƱ��� */
    private double SumPrem;


    /** ���� */
    private double Get;


    /** ���� */
    private double Mult;


    /** �ɷѼ�� */
    private int PayIntv;


    /** ��ȡ��� */
    private int GetIntv;


    /**��ȡ��ʼ�������ڱ�־*/
    private String GetStartFlag;


    /**����ֹ���������ڱ�־*/
    private String PayEndYearFlag;


    /** �ɷ���ֹ���ڻ����� */
    private int PayEndYear;


    /** ��ȡ��ʼ���� */
    private int GetStartYear;


    /** ��ȡ��ʼ���� */
    private int GetStartAge;


    /** ��ȡʱͶ������ */
    private int GetAppYear;


    /** ��ȡʱ��ȡ���� */
    private int GetYear;


    /** ��ȡʱ���� */
    private int GetAge;


    /** ��ȡ���� */
    private int GetTimes;


    /** �����ڼ� */
    private int Years;


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


    /** ������ */
    private double AddRate;


    /** ���ֱ����� */
    private String PolNo;


    /** �������ֱ����� */
    private String GrpPolNo;


    /** ��ͬ������ */
    private String ContNo;


    /** �����ͬ������ */
    private String GrpContNo;


    /**ʱ����*/
    private int Interval;


    /**����ʱ����*/
    private int LimitDay;


    /**����ʧЧ��־*/
    private int PolValiFlag; //1��Ч����0��Ч


    /**�����*/
    private double LoanMoney;


    /**����ʱ����*/
    private double TrayMoney;


    /**����Ա����**/
    private String Operator;


    /**��ȫ�����*/
    private String EdorAcceptNo;


    /**��ȫ������*/
    private String EdorNo;


    /**��ȫ����*/
    private String EdorType;


    /**������Ч����*/
    private String EdorValiDate;


    /**���˷ѽ��*/
    private double GetMoney;


    /**��������*/
    private String GetStartDate;


    /**������*/
    private String CValiDate;


    /**�ʻ�ת�����*/
    private double GetBalance;


    /**���ֱ���*/
    private String RiskCode;


    /**��������*/
    private double FloatRate;


    /**�����ڼ�*/
    private int InsuYear;


    /**�����ڼ��־����λ��*/
    private String InsuYearFlag;


    /**����ͨ���ֶ�*/
    private String StandByFlag1;


    /**�������������**/
    private String DownPolYears;


    /**Լ�����������**/
    private String UpPolYears;


    /** ���һ�ڱ��� */
    private double LastPrem;


    /**�˱���**/
    private String ZTPoint;


    /**��������**/
    private String PayToDate;


    /**��ȫ��������**/
    private String EdorAppDate;


    /**��ȫ����󱣷�**/
    private double NewPrem;


    /**��������*/
    private String CalType;


    /** ������ȡ���� */
    private String GetDutyKind;


    /** ������ */
    private int AgeInterval;

    /** �����˿ͻ��� */
    private String InsuredNo;

    /** �Ѿ������ʶ */
    private String HavePay;

    /**���α��� */
    private String DutyCode;
    
    
    /** �ۼ��⸶��� */
	private String LJPFJT;
	
	/** �㷨���� */
    private String ArithType;

	/** ��������������𷢷ű�׼ֵ */
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

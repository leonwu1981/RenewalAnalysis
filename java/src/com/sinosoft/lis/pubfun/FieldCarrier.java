package com.sinosoft.lis.pubfun;


/**
 * <p>ClassName: CalBase </p>
 * <p>Description: �������Ҫ�����ļ� </p>
 * <p>Description: ���з�������Ϊ�ڲ���Ա������ȡֵ��set��ͷΪ�棬get��ͷΪȡ </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft </p>
 * @Database:
 * @CreateDate��2002-07-30
 */
public class FieldCarrier
{
    // @Field

    /*---������Ϣ--*/

    /** ���� */
    private double Prem;

    /** ���� */
    private double Get;

    /** ���� */
    private double Mult;

    /** ������Ͷ������ */
    private int AppAge;

    /** �������Ա� */
    private String Sex;

    /** �����˳������� */
    private String InsuredBirthday;

    /** �����˹��� */
    private String Job;

    /** ���θ������� */
    private String GDuty;

    /** ���α��� */


    private String DutyCode;

    /** Ͷ������ */
    private int Count;

    /** �������� */
    private int RnewFlag;

    /** ������ */
    private double AddRate;

    /** ��ͬ�� */
    private String ContNo;

    /** ������ */
    private String PolNo;

    /** ԭ���� */
    private double Amnt;

    /** �������� */
    private double FloatRate;

    /**���ֱ���*/
    private String RiskCode;

    /**���պ���*/
    private String MainPolNo;

    /**���ձ���*/
    private String MainRiskCode;

    /**��������ְҵ���*/
    private String OccupationType;

    /**�����ֶ�1*/
    private String StandbyFlag1;

    /**�����ֶ�2*/
    private String StandbyFlag2;

    /**�����ֶ�3*/
    private String StandbyFlag3;

    /**�����ֶ�4*/
    private String StandbyFlag4;

    /**��ֹ����*/
    private String EndDate;

    /*--������Ϣ--*/

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

    /**��������*/
    private int PayYears;

    /*��������*/
    private String PolTypeFlag;

    /*����ѱ���*/
    private double ManageFeeRate;
    /*---��ȫ��Ϣ--*/

    /** ���屣���� */
    private String GrpPolNo;

    /**ʱ����*/
    private int Interval;

    /**��ȫ�����*/
    private String EdorNo;

    /**��ȫ����*/
    private String EdorType;

    /**������Ч����*/
    private String EdorValiDate;

    /**���˷ѽ��*/
    private double GetMoney;

    /**��ȡ��ʼ�������ڱ�־*/
    private String GetStartFlag;

    /**��������*/
    private String GetStartDate;

    /**������*/
    private String CValiDate;

    /**�ʻ�ת�����*/
    private double GetBalance;

    /*--��������Ϣ--*/

    /**����*/
    private String InsuredNo;

    /**����*/
    private String InsuredName;

    /**֤������*/
    private String IDNo;

    /**֤������*/
    private String IDType;

    /**ְҵ����*/
    private String WorkType;

    /**��λ����*/
    private String GrpName;


    // @Constructor
    public FieldCarrier()
    {
    }

    // @Method
    public void setPrem(double tPrem)
    {
        Prem = tPrem;
    }

    public String getPrem()
    {
        return String.valueOf(Prem);
    }

    public void setGet(double tGet)
    {
        Get = tGet;
    }

    public String getGet()
    {
        return String.valueOf(Get);
    }

    public void setAmnt(double tAmnt)
    {
        Amnt = tAmnt;
    }

    public String getAmnt()
    {
        return String.valueOf(Amnt);
    }

    public void setMult(double tMult)
    {
        Mult = tMult;
    }

    public String getMult()
    {
        return String.valueOf(Mult);
    }

    public void setFloatRate(double tFloatRate)
    {
        FloatRate = tFloatRate;
    }

    public String getFloatRate()
    {
        return String.valueOf(FloatRate);
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
    public void setPolTypeFlag(String tPolTypeFlag)
    {
        PolTypeFlag = tPolTypeFlag;
    }
    public void setManageFeeRate(double tManageFeeRate)
    {
        ManageFeeRate = tManageFeeRate;
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

    public void setYears(int tYears)
    {
        Years = tYears;
    }

    public String getYears()
    {
        return String.valueOf(Years);
    }

    public void setInsuYear(int tInsuYear)
    {
        InsuYear = tInsuYear;
    }

    public String getInsuYear()
    {
        return String.valueOf(InsuYear);
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

    public void setRnewFlag(int tRnewFlag)
    {
        RnewFlag = tRnewFlag;
    }

    public String getRnewFlag()
    {
        return String.valueOf(RnewFlag);
    }

    public void setInsuredNo(String tInsuredNo)
    {
        InsuredNo = tInsuredNo;
    }

    public String getInsuredNo()
    {
        return InsuredNo;
    }

    public void setSex(String tSex)
    {
        Sex = tSex;
    }

    public String getSex()
    {
        return Sex;
    }

    public void setInsuredBirthday(String tInsuredBirthday)
    {
        InsuredBirthday = tInsuredBirthday;
    }

    public String getInsuredBirthday()
    {
        return InsuredBirthday;
    }

    public void setInsuYearFlag(String tInsuYearFlag)
    {
        InsuYearFlag = tInsuYearFlag;
    }

    public String getInsuYearFlag()
    {
        return InsuYearFlag;
    }

    public void setPayYears(int tPayYears)
    {
        PayYears = tPayYears;
    }

    public int getPayYears()
    {
        return PayYears;
    }

    public void setPayEndYearFlag(String tPayEndYearFlag)
    {
        PayEndYearFlag = tPayEndYearFlag;
    }

    public String getPayEndYearFlag()
    {
        return PayEndYearFlag;
    }

    public void setGetYearFlag(String tGetYearFlag)
    {
        GetYearFlag = tGetYearFlag;
    }

    public String getGetYearFlag()
    {
        return GetYearFlag;
    }

    public void setGetDutyKind(String tGetDutyKind)
    {
        GetDutyKind = tGetDutyKind;
    }

    public String getGetDutyKind()
    {
        return GetDutyKind;
    }

    public void setStartDateCalRef(String tStartDateCalRef)
    {
        StartDateCalRef = tStartDateCalRef;
    }

    public String getStartDateCalRef()
    {
        return StartDateCalRef;
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
    public void setDutyCode(String tDutyCode)
    {
          DutyCode = tDutyCode;
    }
    public String getGDuty()
    {
        return GDuty;
    }

    public void setContNo(String tContNo)
    {
        ContNo = tContNo;
    }

    public void setPolNo(String tPolNo)
    {
        PolNo = tPolNo;
    }

    public String getContNo()
    {
        return ContNo;
    }

    public String getPolNo()
    {
        return PolNo;
    }

    public void setRiskCode(String tRiskCode)
    {
        RiskCode = tRiskCode;
    }

    public String getRiskCode()
    {
        return RiskCode;
    }

    public void setMainRiskCode(String tMainRiskCode)
    {
        MainRiskCode = tMainRiskCode;
    }

    public String getMainRiskCode()
    {
        return MainRiskCode;
    }

    public void setMainPolNo(String tMainPolNo)
    {
        MainPolNo = tMainPolNo;
    }

    public String getMainPolNo()
    {
        return MainPolNo;
    }

    public void setOccupationType(String tOccupationType)
    {
        OccupationType = tOccupationType;
    }

    public String getOccupationType()
    {
        return OccupationType;
    }

    public void setStandbyFlag1(String tStandbyFlag1)
    {
        StandbyFlag1 = tStandbyFlag1;
    }

    public String getStandbyFlag1()
    {
        return StandbyFlag1;
    }

    public void setStandbyFlag2(String tStandbyFlag2)
    {
        StandbyFlag2 = tStandbyFlag2;
    }

    public String getStandbyFlag2()
    {
        return StandbyFlag2;
    }

    public void setStandbyFlag3(String tStandbyFlag3)
    {
        StandbyFlag3 = tStandbyFlag3;
    }

    public String getStandbyFlag3()
    {
        return StandbyFlag3;
    }

    public void setStandbyFlag4(String tStandbyFlag4)
    {
        StandbyFlag4 = tStandbyFlag4;
    }

    public String getStandbyFlag4()
    {
        return StandbyFlag4;
    }

    public void setEndDate(String tEndDate)
    {
        EndDate = tEndDate;
    }

    public String getEndDate()
    {
        return EndDate;
    }

    public void setGrpPolNo(String tGrpPolNo)
    {
        GrpPolNo = tGrpPolNo;
    }

    public String getGrpPolNo()
    {
        return GrpPolNo;
    }

    public void setInterval(int tInterval)
    {
        Interval = tInterval;
    }

    public String getInterval()
    {
        return String.valueOf(Interval);
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

    public void setGetStartFlag(String tGetStartFlag)
    {
        GetStartFlag = tGetStartFlag;
    }

    public String getGetStartFlag()
    {
        return GetStartFlag;
    }

    public void setIDNo(String tIDNo)
    {
        IDNo = tIDNo;
    }

    public String getIDNo()
    {
        return IDNo;
    }

    public void setIDType(String tIDType)
    {
        IDType = tIDType;
    }

    public String getIDType()
    {
        return IDType;
    }

    public void setWorkType(String tWorkType)
    {
        WorkType = tWorkType;
    }

    public String getWorkType()
    {
        return WorkType;
    }

    public void setGrpName(String tGrpName)
    {
        GrpName = tGrpName;
    }

    public String getGrpName()
    {
        return GrpName;
    }

    public void setInsuredName(String tInsuredName)
    {
        InsuredName = tInsuredName;
    }

    public String getInsuredName()
    {
        return InsuredName;
    }



    public String getPolTypeFlag()
    {
        return PolTypeFlag;
    }


    public double getManageFeeRate()
    {
        return ManageFeeRate;
    }


    public String getDutyCode()
    {
      return DutyCode;
    }
}

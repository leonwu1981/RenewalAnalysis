package com.sinosoft.lis.pubfun;


/**
 * <p>ClassName: CalBase </p>
 * <p>Description: 计算基础要素类文件 </p>
 * <p>Description: 所有方法均是为内部成员变量存取值，set开头为存，get开头为取 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft </p>
 * @Database:
 * @CreateDate：2002-07-30
 */
public class FieldCarrier
{
    // @Field

    /*---保单信息--*/

    /** 保费 */
    private double Prem;

    /** 保额 */
    private double Get;

    /** 份数 */
    private double Mult;

    /** 被保人投保年龄 */
    private int AppAge;

    /** 被保人性别 */
    private String Sex;

    /** 被保人出生日期 */
    private String InsuredBirthday;

    /** 被保人工种 */
    private String Job;

    /** 责任给付编码 */
    private String GDuty;

    /** 责任编码 */


    private String DutyCode;

    /** 投保人数 */
    private int Count;

    /** 续保次数 */
    private int RnewFlag;

    /** 递增率 */
    private double AddRate;

    /** 合同号 */
    private String ContNo;

    /** 保单号 */
    private String PolNo;

    /** 原保额 */
    private double Amnt;

    /** 浮动费率 */
    private double FloatRate;

    /**险种编码*/
    private String RiskCode;

    /**主险号码*/
    private String MainPolNo;

    /**主险编码*/
    private String MainRiskCode;

    /**被保险人职业类别*/
    private String OccupationType;

    /**补充字段1*/
    private String StandbyFlag1;

    /**补充字段2*/
    private String StandbyFlag2;

    /**补充字段3*/
    private String StandbyFlag3;

    /**补充字段4*/
    private String StandbyFlag4;

    /**终止日期*/
    private String EndDate;

    /*--责任信息--*/

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

    /**交费年期*/
    private int PayYears;

    /*保单类型*/
    private String PolTypeFlag;

    /*管理费比例*/
    private double ManageFeeRate;
    /*---保全信息--*/

    /** 集体保单号 */
    private String GrpPolNo;

    /**时间间隔*/
    private int Interval;

    /**保全申请号*/
    private String EdorNo;

    /**保全类型*/
    private String EdorType;

    /**批改生效日期*/
    private String EdorValiDate;

    /**交退费金额*/
    private double GetMoney;

    /**领取开始年龄年期标志*/
    private String GetStartFlag;

    /**起领日期*/
    private String GetStartDate;

    /**起保日期*/
    private String CValiDate;

    /**帐户转年金金额*/
    private double GetBalance;

    /*--被保人信息--*/

    /**号码*/
    private String InsuredNo;

    /**姓名*/
    private String InsuredName;

    /**证件号码*/
    private String IDNo;

    /**证件类型*/
    private String IDType;

    /**职业工种*/
    private String WorkType;

    /**单位名称*/
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

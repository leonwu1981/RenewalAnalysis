/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.text.DecimalFormat;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;


/**
 * <p>����Ĺ��ú���</p>
 * <p>Description: ����ͳб�Ҫ�õ���ȡ������</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: sinosoft</p>
 * @author guoxiang
 * @version 1.0
 */
public class ReportPubFun
{
    public static CErrors mErrors = new CErrors();

    public ReportPubFun()
    {
    }


    /**
     * �õ����ȴ�������
     * @param ys double
     * @param jd String
     * @return String
     */
    public static String functionJD(double ys, String jd)
    {
        try
        {
            if (ys == 0)
            {
                return "0";
            }

            String tt = new DecimalFormat(jd).format(ys);

            return tt;
        }
        catch (Exception ex)
        {
            System.out.println("ԭ��Ϊ0");
            ex.getMessage();

            return "0";
        }
    }


    /**
     * �õ�doublex��
     * @param ys String
     * @return double
     */
    public static double functionDouble(String ys)
    {
        try
        {
            return Double.parseDouble(ys);
        }
        catch (Exception ex)
        {
            System.out.println("ԭ��Ϊ0");
            ex.getMessage();

            return 0;
        }
    }


    /**
     * �õ����
     * @param cs String
     * @param bcs String
     * @param jd String
     * @return String
     */
    public static String functionDivision(String cs, String bcs, String jd)
    {
        try
        {
            if (0 == functionDouble(bcs))
            {
                return "0";
            }

            return functionJD(functionDouble(cs) / functionDouble(bcs), jd);
        }
        catch (Exception ex)
        {
            ex.getMessage();

            return "0";
        }
    }


    /**
     * �õ����
     * @param cs double
     * @param bcs double
     * @param jd String
     * @return String
     */
    public static String functionDivision(double cs, double bcs, String jd)
    {
        try
        {
            if (0 == bcs)
            {
                return "0";
            }

            return functionJD(cs / bcs, jd);
        }
        catch (Exception ex)
        {
            System.out.println("����Ϊ0");
            ex.getMessage();

            return "0";
        }
    }


    /**
     * �ɼ���set�õ���������
     * @param tLCPolSet LCPolSet
     * @param mManageCom String
     * @return String
     */
    public static String getCountSign(LCPolSet tLCPolSet, String mManageCom)
    {
        LCPolSet mLCPolSet = new LCPolSet();

        for (int i = 1; i <= tLCPolSet.size(); i++)
        {
            LCPolSchema tLCPolSchema = tLCPolSet.get(i);

            if ((tLCPolSchema.getManageCom().substring(0, 4)).equals(mManageCom
                    .substring(0,
                               4)))
            {
                mLCPolSet.add(tLCPolSchema);
            }
        }

        return String.valueOf(mLCPolSet.size());
    }


    /**
     * �����䷶Χ�õ��б�����
     * @param age int
     * @param mStart String
     * @param mEnd String
     * @return int
     */
    public static int getCount(int age, String mStart, String mEnd)
    {
        if (mEnd.equals("����"))
        {
            //60���ϣ�>60��
            if (age > Integer.parseInt(mStart))
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
        else if ((age >= Integer.parseInt(mStart))
                 && (age < Integer.parseInt(mEnd)))
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }


    /**
     * �����䷶Χ�õ��б�����
     * @param age int
     * @param prem String
     * @param mStart String
     * @param mEnd String
     * @return double
     */
    public static double getPrem(int age, String prem, String mStart,
                                 String mEnd)
    {

        //60���ϣ�>60��
        if (mEnd.equals("����"))
        {
            if (age > Integer.parseInt(mStart))
            {
                return Double.parseDouble(prem);
            }
            else
            {
                return 0;
            }
        }
        else if ((age >= Integer.parseInt(mStart))
                 && (age <= Integer.parseInt(mEnd)))
        {
            return Double.parseDouble(prem);
        }
        else
        {
            return 0;
        }
    }


    /**
     * �����䷶Χ�õ��б�����
     * @param age String
     * @param prem String
     * @param mStart String
     * @param mEnd String
     * @return double
     */
    public static double getPrem(String age, String prem, String mStart,
                                 String mEnd)
    {

        //60���ϣ�>60��
        if (mEnd.equals("����"))
        {
            if (Integer.parseInt(age) > Integer.parseInt(mStart))
            {
                return Double.parseDouble(prem);
            }
            else
            {
                return 0;
            }
        }
        else if ((Integer.parseInt(age) >= Integer.parseInt(mStart))
                 && (Integer.parseInt(age) <= Integer.parseInt(mEnd)))
        {
            return Double.parseDouble(prem);
        }
        else
        {
            return 0;
        }
    }


    /**
     * �����䷶Χ�õ��б�����
     * @param age String
     * @param mStart String
     * @param mEnd String
     * @return int
     */
    public static int getCount(String age, String mStart, String mEnd)
    {

        //60���ϣ�>60��
        if (mEnd.equals("����"))
        {
            if (Integer.parseInt(age) > Integer.parseInt(mStart))
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
        else if ((Integer.parseInt(age) >= Integer.parseInt(mStart))
                 && (Integer.parseInt(age) <= Integer.parseInt(mEnd)))
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }


    /**
     * �Ӱ�������ð��������еı����Ľ���
     * @param rgtNo String
     * @param sTime String
     * @param eTime String
     * @param state String
     * @return String
     */
    public static String getPayMoney(String rgtNo, String sTime, String eTime,
                                     String state)
    {

        String tSql = "select * from llclaimunderwrite where rgtno='" + rgtNo
                      + "'"
                      + ReportPubFun.getWherePart("makedate", sTime, eTime, 1);

        if (state.equals("0"))
        {
            tSql += " and ClmDecision='0'";
        }

        if (state.equals("1"))
        {
            tSql += " and ClmDecision in('1','2')";
        }

        System.out.println("���Ĳ�ѯsql��" + tSql);

        LLClaimUnderwriteDB tLLClaimUnderwriteDB = new LLClaimUnderwriteDB();
        LLClaimUnderwriteSet tLLClaimUnderwriteSet = tLLClaimUnderwriteDB
                .executeQuery(tSql);
        double sumactupaymoney = 0;

        if (tLLClaimUnderwriteSet.size() == 0)
        {
            System.out.println("��ѯ�Ľ����0");
        }

        for (int i = 1; i <= tLLClaimUnderwriteSet.size(); i++)
        {
            LLClaimUnderwriteSchema tLLClaimUnderwriteSchema =
                    tLLClaimUnderwriteSet
                    .get(i);
            sumactupaymoney += tLLClaimUnderwriteSchema.getRealPay();
        }

        return String.valueOf(sumactupaymoney);
    }


    /**
     * ��Ͷ�����ŵõ�����
     * @param proposalno String
     * @param sTime String
     * @param eTime String
     * @param mManageCom String
     * @param mRiskCode String
     * @return double
     */
    public static double getPayMoney(String proposalno, String sTime,
                                     String eTime, String mManageCom,
                                     String mRiskCode)
    {

        double money = 0.0;
        String pfl_lc = "select * from lcpol where PolNo='" + proposalno
                        + "' and appflag='0' and makedate>'" + sTime
                        + "' and makedate<'" + eTime + "'"
                        + " and managecom like'" + mManageCom + "%'";

        if (mRiskCode.equals(""))
        {
//            pfl_lc = pfl_lc;
        }
        else
        {
            pfl_lc = pfl_lc + " and riskcode='" + mRiskCode + "'";
        }

        System.out.println("����" + pfl_lc);

        LCPolDB tLCPolDB = new LCPolDB();
        LCPolSet tLCPolSet = tLCPolDB.executeQuery(pfl_lc);

        if (tLCPolSet.size() > 1)
        {
            System.out.println("��ѯ�Ľ������һ��");

            return 0.0;
        }
        else if (tLCPolSet.size() == 0)
        {
            return 0.0;
        }
        else
        {
            LCPolSchema tLCPolSchema = tLCPolSet.get(1);
            money = tLCPolSchema.getPrem();
        }

        return money;
    }


    /**
     * ��Ͷ�����ŵõ�����
     * @param proposalno String
     * @return double
     */
    public static double getPayMoney(String proposalno)
    {

        double money = 0.0;
        String pfl_lc = "select * from lcpol and appflag='0' where PolNo='" +
                        proposalno + "'";
        System.out.println("����" + pfl_lc);

        LCPolDB tLCPolDB = new LCPolDB();
        LCPolSet tLCPolSet = tLCPolDB.executeQuery(pfl_lc);

        if (tLCPolSet.size() > 1)
        {
            System.out.println("��ѯ�Ľ������һ��");

            return 0.0;
        }

        if (tLCPolSet.size() == 0)
        {
            money = 0.0;
        }

        LCPolSchema tLCPolSchema = tLCPolSet.get(1);
        money = tLCPolSchema.getPrem();

        return money;
    }


    /**
     * �ɸ߶Χ�õ��б�����
     * @param mAmnt String
     * @param mStart double
     * @param mEnd double
     * @return String
     */
    public static String getHighAmnt(String mAmnt, double mStart, double mEnd)
    {

        if ((Double.parseDouble(mAmnt) >= mStart)
            && (Double.parseDouble(mAmnt) < mEnd))
        {
            return "1";
        }

        return "0";
    }


    /**
     * �����ִ���ĵõ���������
     * @param riskcode String
     * @param mLMRiskAppSet LMRiskAppSet
     * @return String
     */
    public static String getRiskName(String riskcode,
                                     LMRiskAppSet mLMRiskAppSet)
    {

        String tRet = "";

        for (int i = 1; i <= mLMRiskAppSet.size(); i++)
        {
            LMRiskAppSchema tLMRiskAppSchema = mLMRiskAppSet.get(i);

            if (tLMRiskAppSchema.getRiskCode().equals(riskcode))
            {
                tRet = tLMRiskAppSchema.getRiskName();
            }
        }

        return tRet;
    }

    /**
     * �Ӱ�������ð�����ǩ����
     * @param CaseNo String
     * @return String
     */
    public static String getLLClaimUWMainInfo(String CaseNo)
    {

        LLClaimUWMainDB tLLClaimUWMainDB = new LLClaimUWMainDB();
        tLLClaimUWMainDB.setClmNo(CaseNo);

        if (!tLLClaimUWMainDB.getInfo())
        {
            return null;
        }

        return tLLClaimUWMainDB.getClmUWer();
    }


    /**
     * �Ӱ��������������
     * @param rgtNO String
     * @return String
     */
    public static String getLLClaimInfo(String rgtNO)
    {

        LLClaimDB tLLClaimDB = new LLClaimDB();
        tLLClaimDB.setRgtNo(rgtNO);

        LLClaimSet tLLClaimSet = tLLClaimDB.query();

        if (tLLClaimSet.size() != 1)
        {
            buildError("getLLCaseInfo", "��ѯ�Ľ������һ����Ϊ0");

            return "";
        }

        if (tLLClaimSet.size() == 0)
        {
            return "";
        }

        LLClaimSchema tLLClaimSchema = tLLClaimSet.get(1);

        return tLLClaimSchema.getGetDutyKind();
    }


    /**
     * �Ӱ��������¹�������
     * @param rgtNO String
     * @return String
     */
    public static String getLLCaseInfo(String rgtNO)
    {

        LLCaseDB tLLCaseDB = new LLCaseDB(); //�¹�������
        tLLCaseDB.setRgtNo(rgtNO);

        LLCaseSet tLLCaseSet = tLLCaseDB.query();

        if (tLLCaseSet.size() != 1)
        {
            buildError("getLLCaseInfo", "��ѯ�Ľ������һ����Ϊ0");
        }

        if (tLLCaseSet.size() == 0)
        {
            return "";
        }

        LLCaseSchema tLLCaseSchema = tLLCaseSet.get(1);

        return tLLCaseSchema.getCustomerName();
    }


    /**
     * ���û��ŷ����û�����
     * @param usercode String
     * @return LDUserDB
     */
    public static LDUserDB getLdUserInfo(String usercode)
    {

        LDUserDB tLDUserDB = new LDUserDB();
        tLDUserDB.setUserCode(usercode);

        if (!tLDUserDB.getInfo())
        {
            return tLDUserDB;
        }

        return tLDUserDB;
    }


    /**
     * �õ�LDPersonDB
     * @param CustomerNo String
     * @param otherContion String
     * @return LDPersonDB
     */
    public static LDPersonDB getLdPersonInfo(String CustomerNo,
                                             String otherContion)

    {
        LDPersonDB tLDPersonDB = new LDPersonDB();
        tLDPersonDB.setCustomerNo(CustomerNo);

        if (otherContion.equals("1"))
        {
            tLDPersonDB.setIDType("0");
        }

        if (!tLDPersonDB.getInfo())
        {
            return tLDPersonDB;
        }

        return tLDPersonDB;
    }


    /**
     * �����ʹ���ĵõ���������
     * @param pst int
     * @param pot int
     * @param mSSRS SSRS
     * @param mStr String
     * @return String
     */
    public static String getColSSRS(int pst, int pot, SSRS mSSRS, String mStr)
    {

        for (int i = 1; i <= mSSRS.MaxRow; i++)
        {
            if (mSSRS.GetText(i, pst).equals(mStr))
            {
                return mSSRS.GetText(i, pot);
            }
        }

        return "";
    }


    /**
     * �����ʹ���ĵõ���������
     * @param TypeCode String
     * @return String
     */
    public static String getTypeName(String TypeCode)
    {

        if (TypeCode.equals("1"))
        {
            return "�ܱ�";
        }

        if (TypeCode.equals("2"))
        {
            return "����";
        }

        return "";
    }


    /**
     * ���Ա����ĵõ��Ա�����
     * @param SexType String
     * @return String
     */
    public static String getSexName(String SexType)
    {

        if (SexType.equals("0"))
        {
            return "Ů";
        }

        if (SexType.equals("1"))
        {
            return "��";
        }

        if (SexType.equals("2"))
        {
            return "����";
        }

        return "";
    }


    /**
     * ��Ͷ���˺ŵĵõ�ְҵ����
     * @param mInsuredNo String
     * @param mLDPersonSet LDPersonSet
     * @return String
     */
    public static String getWorkName(String mInsuredNo,
                                     LDPersonSet mLDPersonSet)
    {

        String WorkName = "";

        for (int i = 1; i <= mLDPersonSet.size(); i++)
        {
            LDPersonSchema tLDPersonSchema = mLDPersonSet.get(i);

            if (tLDPersonSchema.getCustomerNo().equals(mInsuredNo))
            {
                WorkName = tLDPersonSchema.getWorkType();
            }
        }

        return WorkName;
    }


    /**
     * ��Ͷ���˺ŵĵõ�ְҵ����
     * @param mInsuredNo String
     * @param mLDPersonSet LDPersonSet
     * @return String
     */
    public static String getOccupationCode(String mInsuredNo,
                                           LDPersonSet mLDPersonSet)
    {

        String Occupaion = "";

        for (int i = 1; i <= mLDPersonSet.size(); i++)
        {
            LDPersonSchema tLDPersonSchema = mLDPersonSet.get(i);

            if (tLDPersonSchema.getCustomerNo().equals(mInsuredNo))
            {
                Occupaion = tLDPersonSchema.getOccupationCode();
            }
        }

        return Occupaion;
    }


    /**
     * �ɿͻ��ŵõ���Ӧ��schema
     * @param mInsuredNo String
     * @param mLDPersonSet LDPersonSet
     * @return LDPersonSchema
     */
    public static LDPersonSchema getLdPersonInfo(String mInsuredNo,
                                                 LDPersonSet mLDPersonSet)
    {

        for (int i = 1; i <= mLDPersonSet.size(); i++)
        {
            LDPersonSchema tLDPersonSchema = mLDPersonSet.get(i);

            if (tLDPersonSchema.getCustomerNo().equals(mInsuredNo))
            {
                return tLDPersonSchema;
            }
        }

        return null;
    }


    /**
     * ��Ͷ���˺ŵĵõ�ְҵ����
     * @param mOccupationCode String
     * @param mLDOccupationSet LDOccupationSet
     * @return String
     */
    public static String getOccupationName(String mOccupationCode,
                                           LDOccupationSet mLDOccupationSet)
    {

        String Occupaion = "";

        for (int i = 1; i <= mLDOccupationSet.size(); i++)
        {
            LDOccupationSchema tLDOccupationSchema = mLDOccupationSet.get(i);

            if (tLDOccupationSchema.getOccupationCode().equals(mOccupationCode))
            {
                Occupaion = tLDOccupationSchema.getOccupationName();
            }
        }

        return Occupaion;
    }


    /**
     * �������ִ�����С���ϵĴ�С
     * @param mSSRS SSRS
     * @param mRiskCode String
     * @return String
     */
    public static String getCountSumUW(SSRS mSSRS, String mRiskCode)
    {

        int CountJS = 0;

        for (int i = 1; i <= mSSRS.MaxRow; i++)
        {
            if (mSSRS.GetText(i, 1).equals(mRiskCode))
            {
                CountJS++;
            }
        }

        System.out.println("the total Class:" + CountJS);

        return String.valueOf(CountJS);
    }


    /**
     * �������˴���õ�������
     * @param mSSRS SSRS
     * @param mHandler String
     * @return String
     */
    public static String getCaseCount(SSRS mSSRS, String mHandler)
    {

        int CountJS = 0;

        for (int i = 1; i <= mSSRS.MaxRow; i++)
        {
            if (mSSRS.GetText(i, 1).equals(mHandler))
            {
                CountJS++;
            }
        }

        return String.valueOf(CountJS);
    }


    /**
     * ��ְҵ����õ��ܳб����� "" Ϊ 0 ��
     * @param mSSRS SSRS
     * @param mRiskCode String
     * @param mClassCode String
     * @return String
     */
    public static String getClassCount(SSRS mSSRS, String mRiskCode,
                                       String mClassCode)
    {

        int CountJS = 0;

        for (int i = 1; i <= mSSRS.MaxRow; i++)
        {
            if (mSSRS.GetText(i, 1).equals(mRiskCode))
            {
                if ("0".equals(mClassCode) && (mSSRS.GetText(i, 2).equals("")))
                {
                    CountJS++;
                }
                else if (mSSRS.GetText(i, 2).equals(mClassCode))
                {
                    CountJS++;
                }
            }
        }

        System.out.println("the " + mClassCode + " Class:" + CountJS);

        return String.valueOf(CountJS);
    }


    /**
     * �ɹ�������ź�ҵ������SSRS�õ�����
     * @param specSSRS SSRS
     * @param mManageCom String
     * @return String
     */
    public static String getCount(SSRS specSSRS, String mManageCom)
    {

        int ZCCDCountJS = 0;

        for (int i = 1; i <= specSSRS.MaxRow; i++)
        {
            if ((specSSRS.GetText(i, 1).substring(0, 4)).equals(mManageCom
                    .substring(0,
                               4)))
            {
                ZCCDCountJS++;
            }
        }

        return String.valueOf(ZCCDCountJS);
    }


    /**
     * �ɹ�������ź�ҵ������SSRS�õ�����
     * @param specSSRS SSRS
     * @param mManageCom String
     * @return double
     */
    public static double getCounts(SSRS specSSRS, String mManageCom)
    {

        double ZCCDCountJS = 0;

        for (int i = 1; i <= specSSRS.MaxRow; i++)
        {
            if (specSSRS.GetText(i, 1).equals(mManageCom))
            {
                ZCCDCountJS++;
            }
        }

        return ZCCDCountJS;
    }


    /**
     * �ɴ���ı���Ͷ�Ӧ����źʹ����ҵ������SSRS�õ�����
     * @param mInputCode String
     * @param mSSRS SSRS
     * @param colNUM int
     * @return double
     */
    public static double getCounts(String mInputCode, SSRS mSSRS, int colNUM)
    {

        double ReturnJS = 0;

        for (int i = 1; i <= mSSRS.MaxRow; i++)
        {
            if (mSSRS.GetText(i, colNUM).equals(mInputCode))
            {
                ReturnJS++;
            }
        }

        return ReturnJS;
    }


    /**
     * �ɹ�������ź�ҵ������SSRS�õ�ʱ��
     * @param specSSRS SSRS
     * @param mManageCom String
     * @return String
     */
    public static String getTime(SSRS specSSRS, String mManageCom)
    {

        double sumtime = 0;

        for (int i = 1; i <= specSSRS.MaxRow; i++)
        {
            if ((specSSRS.GetText(i, 2).substring(0, 4)).equals(mManageCom
                    .substring(0,
                               4)))
            {
                double getTime = Double.parseDouble(specSSRS.GetText(i, 1));
                sumtime += getTime;
            }
        }

        System.out.println("Time:" + sumtime);

        return String.valueOf(sumtime);
    }


    /**
     * �ӻ������������
     * @param mngcom String
     * @return String
     */
    public static String getMngName(String mngcom)
    {

        String msql = "select Name from LDCom where ComCode='" + mngcom + "'";
        ExeSQL mExeSQL = new ExeSQL();
        SSRS mSSRS = new SSRS();
        mSSRS = mExeSQL.execSQL(msql);

        if (mSSRS.MaxRow > 1)
        {
            System.out.println("��ѯ�Ľ������һ��");

            return "";
        }

        String mMangName = "";

        if (mSSRS.MaxRow == 0)
        {
            mMangName = "";
        }

        mMangName = mSSRS.GetText(1, 1);

        return mMangName;
    }


    /**
     * �������ִ���õ��⸶���
     * @param riskcode String
     * @param sTime String
     * @param eTime String
     * @param mManageCom String
     * @return String
     */
    public static String getPolmoney(String riskcode, String sTime,
                                     String eTime, String mManageCom)
    {

        String tSql = "select * from LJAPayPerson where ConfDate>='" + sTime
                      + "' and ConfDate<='" + eTime
                      + "' and PayAimClass='1' and ManageCom like'"
                      + mManageCom + "%' and paytype='ZC' AND riskcode='"
                      + riskcode + "'";
        System.out.println("sql" + tSql);

        LJAPayPersonDB tLJAPayPersonDB = new LJAPayPersonDB();
        LJAPayPersonSet tLJAPayPersonSet = tLJAPayPersonDB.executeQuery(tSql);
        double sumactupaymoney = 0;

        for (int i = 1; i <= tLJAPayPersonSet.size(); i++)
        {
            LJAPayPersonSchema tLJAPayPersonSchema = tLJAPayPersonSet.get(i);
            sumactupaymoney += tLJAPayPersonSchema.getSumActuPayMoney();
        }

        return String.valueOf(sumactupaymoney);
    }

    public static String getSurveyClmcaseCount(String sTime, String eTime,
                                               String mManageCom)
    {
        SSRS nSSRS = new SSRS();
        ExeSQL nExeSQL = new ExeSQL();
        String nSql = "select distinct(clmcaseno) from llsurvey where mngcom='" +
                      mManageCom + "'" + " and makedate>='" + sTime +
                      "' and ModifyDate<='" + eTime + "'";
        System.out.println("��ѯ�û����������Ϣnsql:" + nSql);
        nSSRS = nExeSQL.execSQL(nSql);
        System.out.println("������" + nSSRS.MaxRow);

        return String.valueOf(nSSRS.MaxRow);
    }


    /**
     * �����ִ���ĵõ���ɨ����е����ֱ�־
     * @param classname String
     * @return String
     */
    public static String getRiskClass(String classname)
    {

        if (classname.equals("0"))
        {
            return "11"; //����=0
        }

        if (classname.equals("1"))
        {
            return "12"; //����=1
        }

        if (classname.equals("2"))
        {
            return "15"; //����=2
        }

        if (classname.equals("3"))
        {
            return "16"; //���ױ���=3
        }

        return "";
    }


    /**
     * edortypecode1Ϊ���մ��룬edortypecodeΪ���պ�����
     * @param BQType String
     * @return String
     */
    public static String getBQCode(String BQType)
    {

        if (BQType.equals("bq3") || BQType.equals("bq4")
            || BQType.equals("bq7") || BQType.equals("bq8"))
        {
            return "edortypecode1";
        }
        else
        {
            return "edortypecode";
        }
    }


    /**
     * edortypecode1Ϊ���մ��룬edortypecodeΪ���պ�����
     * @param BQType String
     * @return String
     */
    public static String getBQCodeTypeSql(String BQType)
    {

        if (BQType.equals("bq3") || BQType.equals("bq4")
            || BQType.equals("bq7") || BQType.equals("bq8"))
        {
            return " and ldcode.codetype ='edortypecode1'";
        }
        else
        {
            return " and ldcode.codetype ='edortypecode'";
        }
    }

    public static String getBQSaleChnlSql(String BQType)
    {
        if (BQType.equals("bq1") || BQType.equals("bq2"))
        {
            return " and lcpol.SaleChnl='02' ";
        }

        if (BQType.equals("bq5") || BQType.equals("bq6"))
        {
            return " and lcpol.SaleChnl='03' ";
        }
        if (BQType.equals("bq3") || BQType.equals("bq4")
            || BQType.equals("bq7") || BQType.equals("bq8"))
        {
            return " and lcpol.SaleChnl='01' ";
        }

        return " and 1=1 ";
    }


    /**
     * ��ȫ�������
     * @param BQType String
     * @param DBName String
     * @return String
     */
    public static String getBQChnlSql(String BQType, String DBName)
    {

        if (BQType.equals("bq1"))
        {
            return " and " + DBName
                    + ".SaleChnl='02' and ldcode.othersign='1' "; //����������
        }

        if (BQType.equals("bq2"))
        {
            return " and " + DBName + ".SaleChnl='02' "; //�����ۺ���
        }

        if (BQType.equals("bq3") || BQType.equals("bq7"))
        {
            return " and " + DBName
                    + ".SaleChnl='01' and ldcode.othersign='1' "; //�ŵ�������
        }

        if (BQType.equals("bq4") || BQType.equals("bq8"))
        {
            return " and " + DBName + ".SaleChnl='01' "; //�ŵ��ۺ���
        }

        if (BQType.equals("bq5"))
        {
            return " and " + DBName
                    + ".SaleChnl='03' and ldcode.othersign='1' "; //����������
        }

        if (BQType.equals("bq6"))
        {
            return " and " + DBName + ".SaleChnl='03' "; //�����ۺ���
        }

        return "and 1=1 ";
    }


    /**
     * �ɱ�ȫ����ϵͳ����õ��ĵõ���Ӧ��sql���
     * ���ڱ�ȫ��������Ŀ������/���汣�ո�����LG���ͺ�����ȡ��BG��
     * �����ı���û��������Ŀ��¼ �����Ǳ�ȫ������Ҫͳ����������Ŀ
     * �ڸ��˷����������������Ҫ��������������Դ
     * @param BQCode String
     * @param BQType String
     * @param sTime String
     * @param eTime String
     * @param mManageCom String
     * @return String
     */
    public static String getBQSql(String BQCode, String BQType, String sTime,
                                  String eTime, String mManageCom)
    {

        String add_lg = "";
        String add_bg = "";
        if (BQCode.equals("LG") || BQCode.equals(""))
        {
            add_lg = " union all (select 'LG' as edortype, ljagetdraw.polno,'' as edorno, lcpol.AppntName,lcpol.InsuredName,ljagetdraw.getmoney,lcpol.agentcode,ljagetdraw.Operator,ljagetdraw.ModifyDate,substr(lcpol.Managecom,1,4),lcpol.Managecom,lcpol.riskcode from ljagetdraw,lcpol where ljagetdraw.polno=lcpol.polno and lcpol.appflag='1'" +
                     getBQSaleChnlSql(BQType) +
                     getWherePartLike("ljagetdraw.ManageCom", mManageCom) +
                     getWherePart("ljagetdraw.ModifyDate", sTime, eTime, 1) +
                     ")";
        }
        if (BQCode.equals("BG") || BQCode.equals(""))
        {
            add_bg = "union all (select 'BG' as edortype, LJABonusGet.OtherNo,'' as edorno, lcpol.AppntName,lcpol.InsuredName,LJABonusGet.getmoney,lcpol.agentcode,LJABonusGet.Operator,LJABonusGet.ModifyDate,substr(lcpol.Managecom,1,4),lcpol.Managecom,lcpol.riskcode from LJABonusGet,lcpol where LJABonusGet.OtherNo=lcpol.polno and lcpol.appflag='1'" +
                     getBQSaleChnlSql(BQType) +
                     getWherePartLike("LJABonusGet.ManageCom", mManageCom) +
                     getWherePart("LJABonusGet.ModifyDate", sTime, eTime, 1) +
                     ")";
        }

        String add_sql = add_lg + add_bg;

        return add_sql;
    }


    /**
     * �ɱ�ȫ����ϵͳ����õ��ĵõ���Ӧ��sql���
     * ���ڱ�ȫ��������Ŀ������/���汣�ո�����LG���ͺ�����ȡ��BG��
     * �����ı���û��������Ŀ��¼ �����Ǳ�ȫ������Ҫͳ����������Ŀ
     * �ڸ��˷����������������Ҫ��������������Դ
     * @param BQCode String
     * @param BQType String
     * @param sTime String
     * @param eTime String
     * @param mManageCom String
     * @return String
     */
    public static String getBQtSql(String BQCode, String BQType, String sTime,
                                   String eTime, String mManageCom)
    {

        String add_lg = "";
        String add_bg = "";
        if (BQCode.equals("LG") || BQCode.equals(""))
        {
            add_lg = " union all (select 'LG' as edortype, ljagetdraw.grppolno,'' as edorno, lcpol.AppntName,sum(abs(ljagetdraw.getmoney)),lcpol.agentcode,ljagetdraw.Operator,ljagetdraw.ModifyDate,substr(lcpol.Managecom,1,4),lcpol.Managecom,lcpol.riskcode from ljagetdraw,lcpol where ljagetdraw.grppolno=lcpol.grppolno and lcpol.appflag='1'" +
                     getBQSaleChnlSql(BQType) +
                     getWherePartLike("ljagetdraw.ManageCom", mManageCom) +
                     getWherePart("ljagetdraw.ModifyDate", sTime, eTime, 1) + " group by  ljagetdraw.grppolno,lcpol.AppntName,lcpol.agentcode,ljagetdraw.Operator,ljagetdraw.ModifyDate,substr(lcpol.Managecom,1,4),lcpol.Managecom,lcpol.riskcode)";
        }
        if (BQCode.equals("BG") || BQCode.equals(""))
        {
            add_bg = " union all (select 'BG' as edortype, LJABonusGet.OtherNo,'' as edorno, lcpol.AppntName,sum(abs(LJABonusGet.getmoney)),lcpol.agentcode,LJABonusGet.Operator,LJABonusGet.ModifyDate,substr(lcpol.Managecom,1,4),lcpol.Managecom,lcpol.riskcode from LJABonusGet,lcpol where LJABonusGet.OtherNo=lcpol.grppolno and lcpol.appflag='1'" +
                     getBQSaleChnlSql(BQType) +
                     getWherePartLike("LJABonusGet.ManageCom", mManageCom) +
                     getWherePart("LJABonusGet.ModifyDate", sTime, eTime, 1) + " group by  LJABonusGet.OtherNo,lcpol.AppntName,lcpol.agentcode,LJABonusGet.Operator,LJABonusGet.ModifyDate,substr(lcpol.Managecom,1,4),lcpol.Managecom,lcpol.riskcode)";
        }

        String add_sql = add_lg + add_bg;

        return add_sql;
    }


    /**
     * ��mCode�õ�����
     * @param mCode String
     * @param BQType String
     * @param mLDCodeSet LDCodeSet
     * @return String
     */
    public static String getBqItemName(String mCode, String BQType,
                                       LDCodeSet mLDCodeSet)
    {

        String tRet = "";

        for (int i = 1; i <= mLDCodeSet.size(); i++)
        {
            LDCodeSchema tLDCodeSchema = mLDCodeSet.get(i);

            if (tLDCodeSchema.getCode().equals(mCode)
                && tLDCodeSchema.getCodeType().equals(getBQCode(BQType)))
            {
                tRet = tLDCodeSchema.getCodeName();
            }
        }

        return tRet;
    }


    /**
     * ��mCode�õ�����
     * @param mCode String
     * @param mType String
     * @param mLDCodeSet LDCodeSet
     * @return String
     */
    public static String getDutyKindName(String mCode, String mType,
                                         LDCodeSet mLDCodeSet)
    {

        String tRet = "";

        for (int i = 1; i <= mLDCodeSet.size(); i++)
        {
            LDCodeSchema tLDCodeSchema = mLDCodeSet.get(i);

            if (tLDCodeSchema.getCode().equals(mCode)
                && tLDCodeSchema.getCodeType().equals(mType))
            {
                tRet = tLDCodeSchema.getCodeName();
            }
        }

        return tRet;
    }


    /**
     * �������ִ���õ��⸶���
     * @param tLJAPayPersonSet LJAPayPersonSet
     * @param riskcode String
     * @return String
     */
    public static String getPolmoney(LJAPayPersonSet tLJAPayPersonSet,
                                     String riskcode)
    {

        double sumactupaymoney = 0;

        for (int i = 1; i <= tLJAPayPersonSet.size(); i++)
        {
            LJAPayPersonSchema tLJAPayPersonSchema = tLJAPayPersonSet.get(i);

            if (tLJAPayPersonSchema.getRiskCode().equals(riskcode))
            {
                sumactupaymoney += tLJAPayPersonSchema.getSumActuPayMoney();
            }
        }

        return String.valueOf(sumactupaymoney);
    }


    /**
     * ���ݱ�������õ���������
     * @param strCertifyCode String
     * @return String
     * @throws Exception
     */
    public static String getCertifyName(String strCertifyCode) throws Exception

    {
        LMCertifyDesDB tLMCertifyDesDB = new LMCertifyDesDB();

        tLMCertifyDesDB.setCertifyCode(strCertifyCode);

        if (!tLMCertifyDesDB.getInfo())
        {
            mErrors.copyAllErrors(tLMCertifyDesDB.mErrors);
            throw new Exception("��ȡ��LMCertifyDes������ʱ��������");
        }

        return tLMCertifyDesDB.getCertifyName();
    }


    /**
     * where ����
     * @auto:zhouping ,I move this method from class com.sinosoft.lis.certify.CerStartBL
     * @param strColName String
     * @param strColValue String
     * @return String
     */
    public static String getWherePartLike(String strColName, String strColValue)

    {
        if ((strColValue == null) || strColValue.equals(""))
        {
            return "";
        }

        return " AND " + strColName + " Like '" + strColValue + "%'";
    }

    public static String getWherePart(String strColName, String strColValue)
    {
        if ((strColValue == null) || strColValue.equals(""))
        {
            return "";
        }

        return " AND " + strColName + " = '" + strColValue + "'";
    }

    public static String getWherePartInRiskCode(String strColName,
                                                String strColValue,
                                                String nFlag)
    {
        if ((strColValue == null) || strColValue.equals(""))
        {
            if (nFlag.equals("on") || (nFlag.equals("on")))
            {
                return " AND " + strColName
                        +
                        "  In ( select riskcode from lmriskapp where subRiskFlag='M')";
            }
            else
            {
                return "";
            }
        }
        else
        {
            return " AND " + strColName + " In('" + strColValue + "') ";
        }
    }

    public static String getWherePart(String strCol1, String strCol2,
                                      String strCol3, int nFlag)
    {
        if (nFlag == 0)
        {
            if ((strCol3 == null) || strCol3.equals(""))
            {
                return "";
            }

            return " AND " + strCol1 + " <= '" + strCol3 + "' AND " + strCol2
                    + " >= '" + strCol3 + "'";
        }
        else
        {
            String str = "";

            if ((strCol2 == null) || strCol2.equals(""))
            {
                str += "";
            }
            else
            {
                str += (" AND " + strCol1 + " >= '" + strCol2 + "'");
            }

            if ((strCol3 == null) || strCol3.equals(""))
            {
                str += "";
            }
            else
            {
                str += (" AND " + strCol1 + " <= '" + strCol3 + "'");
            }

            return str;
        }
    }


    /**
     *
     * @param dInsuredAPPage String
     * @param mFlag String[][]
     * @return String
     */
    public static String getAgeGroup(String dInsuredAPPage, String[][] mFlag)
    {

        for (int i = 0; i < mFlag.length; i++)
        {
            if (mFlag[i][1].equals("����"))
            {
                return mFlag[i][0] + mFlag[i][1];
            }
            else if ((Integer.parseInt(dInsuredAPPage) >=
                      Integer.parseInt(mFlag[i][0]))
                     && (Integer.parseInt(dInsuredAPPage) <= Integer
                         .parseInt(mFlag[i][1])))
            {
                return mFlag[i][0] + "-" + mFlag[i][1];
            }
        }

        return "";
    }

    public static void buildError(String szFunc, String szErrMsg)
    {
        CError cError = new CError();
        cError.moduleName = "RiskClaimBL";
        cError.functionName = szFunc;
        cError.errorMessage = szErrMsg;
        mErrors.addOneError(cError);
    }

    public static void main(String[] args)
    {
        LDPersonDB tLDPersonDB = new LDPersonDB();
        LDPersonSet tLDPersonSet = tLDPersonDB.query();
        getLdPersonInfo("0000039606", tLDPersonSet);
    }
}

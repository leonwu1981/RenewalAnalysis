package com.sinosoft.lis.pubfun;


/**
 * <p>Title:���۹���ϵͳ</p>
 * <p>Description:���۹���Ĺ���ҵ������
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author
 * @version 1.0
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;

public class AgentPubFun
{

    public AgentPubFun()
    {
    }

    /**
     * ��ѯ�����˵Ļ�����Ϣ author: xijh
     *
     * @param cAgentCode String
     * @return String
     */
    public static String getBranchCode(String cAgentCode)
    {
        if (cAgentCode == null || cAgentCode.equals(""))
        {
            return null;
        }
        String tSQL = "Select BranchCode from LAAgent where AgentCode = '" +
                      cAgentCode + "'";
        ExeSQL tExeSQL = new ExeSQL();
        String tBranchCode = tExeSQL.getOneValue(tSQL);
        if (tExeSQL.mErrors.needDealError())
        {
            return null;
        }
        return tBranchCode;
    }

    /**
     * ��ѯ�����˵Ļ�����Ϣ author: xijh
     *
     * @param cAgentCode String
     * @return String
     */
    public static String getAgentGroup(String cAgentCode)
    {
        if (cAgentCode == null || cAgentCode.equals(""))
        {
            return null;
        }
        String tSQL = "Select AgentGroup from LAAgent where AgentCode = '" +
                      cAgentCode + "'";
        ExeSQL tExeSQL = new ExeSQL();
        String tAgentGroup = tExeSQL.getOneValue(tSQL);
        if (tExeSQL.mErrors.needDealError())
        {
            return null;
        }
        return tAgentGroup;
    }


    /**
     * ��ѯ�����˵Ĺ�Ӷ���� author: xijh
     *
     * @param cAgentCode String
     * @return String
     */
    public static String getEmployDate(String cAgentCode)
    {
        if (cAgentCode == null || cAgentCode.equals(""))
        {
            return null;
        }
        String tSQL = "Select EmployDate from LAAgent where AgentCode = '" +
                      cAgentCode + "'";
        ExeSQL tExeSQL = new ExeSQL();
        String tEmployDate = tExeSQL.getOneValue(tSQL);
        if (tExeSQL.mErrors.needDealError())
        {
            return null;
        }
        return tEmployDate;
    }

    /**
     * parseTime author: xijh
     * �����ص�����
     * @param Time String
     * @param Type int//�������ͣ���1����2����3����4������5����6��
     * @param MonthDeflection int//��ƫ��
     * @param DayDeflection int//��ƫ�ƣ�����������˵�����䣬��5���ڵ�ʱ��Σ�
     * @return String[]
     */
    public static String[] parseTime(String Time, int Type, int MonthDeflection,
                                     int DayDeflection)
    {
        //��ǰʱ����ʽ,yyyy-MM-dd
//      String sourcePattern=getSessionPool().getConfig().getDefaultDateFormat();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //�û�������ʽ
        SimpleDateFormat targetDF = new SimpleDateFormat("yyyy-MM-dd"); //���ݿ�Ҫ����ʽ

        String StartTime = Time;
        String EndTime = Time;

        try
        {
            Date tDate = sdf.parse(Time);
            Calendar cal = Calendar.getInstance();
            switch (Type)
            {
                case 1: //��
                    cal.setTime(tDate);
                    cal.add(Calendar.MONTH, MonthDeflection);
                    if (DayDeflection > 0)
                    {
                        StartTime = targetDF.format(cal.getTime());
                        cal.add(Calendar.DATE, DayDeflection - 1);
                        EndTime = targetDF.format(cal.getTime());
                    }
                    else if (DayDeflection < 0)
                    {
                        EndTime = targetDF.format(cal.getTime());
                        cal.add(Calendar.DATE, DayDeflection + 1);
                        StartTime = targetDF.format(cal.getTime());
                    }
                    break;
                case 2: //��
                    cal.setTime(tDate);
                    cal.add(Calendar.MONTH, MonthDeflection);
                    int DayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
                    cal.add(Calendar.DATE, DayDeflection);
                    cal.add(Calendar.DATE, -DayOfWeek + 1);
                    StartTime = targetDF.format(cal.getTime());
                    cal.clear();
                    cal.setTime(tDate);
                    cal.add(Calendar.MONTH, MonthDeflection);
                    cal.add(Calendar.DATE, 7 - DayOfWeek);
                    cal.add(Calendar.DATE, DayDeflection);
                    EndTime = targetDF.format(cal.getTime());
                    break;
                case 3: //��
                    cal.setTime(tDate);
                    cal.add(Calendar.MONTH, MonthDeflection);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    cal.add(Calendar.DATE, DayDeflection);
                    StartTime = targetDF.format(cal.getTime());
                    cal.clear();
                    cal.setTime(tDate);
                    cal.add(Calendar.MONTH, MonthDeflection);
                    cal.add(Calendar.MONTH, 1);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                    cal.add(Calendar.DATE, DayDeflection);
                    EndTime = targetDF.format(cal.getTime());
                    break;
                case 4: //��
                    cal.setTime(tDate);
                    int Quarter = cal.get(Calendar.MONTH) / 3; //���ڼ���,0-spring;1-summer
                    cal.set(Calendar.MONTH, Quarter * 3);
                    cal.add(Calendar.MONTH, MonthDeflection);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    cal.add(Calendar.DATE, DayDeflection);
                    StartTime = targetDF.format(cal.getTime());
                    cal.setTime(tDate);
                    cal.set(Calendar.MONTH, (Quarter + 1) * 3);
                    cal.add(Calendar.MONTH, MonthDeflection);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                    cal.add(Calendar.DATE, DayDeflection);
                    EndTime = targetDF.format(cal.getTime());
                    break;
                case 5: //����
                    cal.setTime(tDate);
                    int HalfYear = cal.get(Calendar.MONTH) / 6; //���ڰ����,0-�ϰ���;1-�°���
                    cal.set(Calendar.MONTH, HalfYear * 6);
                    cal.add(Calendar.MONTH, MonthDeflection);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    cal.add(Calendar.DATE, DayDeflection);
                    StartTime = targetDF.format(cal.getTime());
                    cal.setTime(tDate);
                    cal.set(Calendar.MONTH, (HalfYear + 1) * 6);
                    cal.add(Calendar.MONTH, MonthDeflection);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                    cal.add(Calendar.DATE, DayDeflection);
                    EndTime = targetDF.format(cal.getTime());
                    break;
                case 6: //��
                    cal.setTime(tDate);
                    cal.set(Calendar.MONTH, Calendar.JANUARY);
                    cal.set(Calendar.DATE, 1); //�赽���
                    cal.add(Calendar.MONTH, MonthDeflection);
                    cal.add(Calendar.DATE, DayDeflection);
                    StartTime = targetDF.format(cal.getTime());
                    cal.clear();
                    cal.setTime(tDate);
                    cal.add(Calendar.YEAR, 1); //��һ��
                    cal.set(Calendar.MONTH, Calendar.JANUARY);
                    cal.add(Calendar.MONTH, MonthDeflection);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                    cal.add(Calendar.DATE, DayDeflection);
                    EndTime = targetDF.format(cal.getTime());
                    break;
                default:
                    break;
            }
        }
        catch (ParseException ex)
        {

        }
        String[] result = new String[2];
        result[0] = StartTime;
        result[1] = EndTime;
        return result;
    }

    /**
     * 15Ϊ�����֤ת��Ϊ18λ author: xijh
     * @param id String
     * @return String
     */
    public static final String getNewId(String id)
    {
        if (id.length() != 15)
        {
            return null;
        }
        try
        {
            double a = Double.parseDouble(id);
        }
        catch (Exception ex)
        {
            return null;
        }
        final int[] W =
                {
                7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1};
        //��Ȩ����
        final String[] A =
                {
                "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
        //У����
        int i, j, s = 0;
        String newid;
        newid = id;
        newid = newid.substring(0, 6) + "19" + newid.substring(6, id.length());
        for (i = 0; i < newid.length(); i++)
        {
            j = Integer.parseInt(newid.substring(i, i + 1)) * W[i];
            s += j;
        }
        s %= 11;
        newid += A[s];
        return newid;
    }


    /**
     * ת��ҵ��ϵͳ����������Ϊ����ϵͳ��չҵ���͵ĺ��� author: liujw
     * @param cSalChnl ҵ��ϵͳ����������
     * @return String ����ϵͳ��չҵ����(BranchType)
     */
    public static String switchSalChnltoBranchType(String cSalChnl)
    {
        String tBranchType = "";

        cSalChnl = cSalChnl.trim();
        if (cSalChnl.equals("01"))
        { //����
            tBranchType = "2";
        }
        else if (cSalChnl.equals("02"))
        { //����
            tBranchType = "1";
        }
        else if (cSalChnl.equals("03") || cSalChnl.equals("04")
                 || cSalChnl.equals("05") || cSalChnl.equals("06"))
        //���б��� ��ҵ���� רҵ���� ���͹�˾
        {
            tBranchType = "3";
        }

        return tBranchType;
    }

//    //xjh Add 2005/04/01
//    public static String[] switchSalChnl(String cSalChnl)
//    {
//        String[] result = new String[2];
//        LDCodeRelaSet tLDCodeRelaSet = new LDCodeRelaSet();
//        LDCodeRelaDB tLDCodeRelaDB = new LDCodeRelaDB();
//        tLDCodeRelaDB.setRelaType("salechnlvsbranchtype");
//        tLDCodeRelaDB.setCode3(cSalChnl);
//        tLDCodeRelaSet = tLDCodeRelaDB.query();
//        if (tLDCodeRelaSet.size() == 0)
//            return null;
//        else
//        {
//            result[0] = tLDCodeRelaSet.get(1).getCode1();
//            result[1] = tLDCodeRelaSet.get(1).getCode2();
//        }
//        return result;
//    }

    /**
     * parseTime
     *
     * @param endTime String
     * @param reportType String
     * @param reportCycle String
     * @return String[]
     */
    public static String[] parseTime(String endTime, int reportType,
                                     int reportCycle)
    {
        //��ǰʱ����ʽ,yyyy-MM-dd
//      String sourcePattern=getSessionPool().getConfig().getDefaultDateFormat();

//    int reportCycle=1;
        String sourcePattern = "yyyy-MM-dd";
        String targetPattern = "yyyy-MM-dd";

        SimpleDateFormat sdf = new SimpleDateFormat(sourcePattern); //�û�������ʽ
        SimpleDateFormat targetDF = new SimpleDateFormat(targetPattern); //���ݿ�Ҫ����ʽ

        String reportStartTime = endTime;
        String reportEndTime = endTime;

        String settedEnd = endTime;

        Date endDate;
//        Date startDate;
        try
        {
            endDate = sdf.parse(settedEnd);

            //��������
            int type = reportType;
            //��������
            int cycle = reportCycle;

            Calendar cal = Calendar.getInstance();
            cal.setTime(endDate);
            switch (type)
            {
                case 1: //�ձ�
                    reportEndTime = targetDF.format(endDate);
                    cal.add(Calendar.DATE, ( -1) * cycle + 1); //��ʼʱ��=����ʱ��-����+1
                    reportStartTime = targetDF.format(cal.getTime());
                    break;
                case 2: //�±�
                    cal.set(Calendar.DAY_OF_MONTH, 1); //�³�
                    cal.add(Calendar.MONTH, ( -1) * cycle + 1); //�����ڵ��³�
                    reportStartTime = targetDF.format(cal.getTime()); //��ʼʱ��
                    cal.add(Calendar.MONTH, cycle); //����ĩ�����³�
                    cal.add(Calendar.DAY_OF_MONTH, -1); //����ĩ
                    reportEndTime = targetDF.format(cal.getTime());
                    break;
                case 3: //����

                    // int month=cal.get(Calendar.MONTH)+1;  //�¶�,��Ҫ+1
                    int month = cal.get(Calendar.MONTH);
                    int quarter = month / 3; //���ڼ���,0-spring;1-summer
                    cal.set(Calendar.MONTH,
                            quarter * 3 + (( -1) * cycle + 1) * 3); //����-���ڳ���ʱ��
                    cal.set(Calendar.DATE, 1); //�赽1��
                    reportStartTime = targetDF.format(cal.getTime());

                    ////////////////////end time
                    cal.add(Calendar.MONTH, cycle * 3); //����һ��
                    cal.add(Calendar.DATE, -1);
                    reportEndTime = targetDF.format(cal.getTime());
                    break;
                case 4: //�걨
                    cal.add(Calendar.YEAR, ( -1) * cycle + 1);
                    cal.set(Calendar.MONTH, Calendar.JANUARY);
                    cal.set(Calendar.DATE, 1); //�赽���
                    reportStartTime = targetDF.format(cal.getTime());

                    /////////
                    cal.add(Calendar.YEAR, cycle); //��һ��Ԫ��,����һ��
                    cal.add(Calendar.DATE, -1); //��ĩ
                    reportEndTime = targetDF.format(cal.getTime());
                    break;
                default:
                    break;
            }
        }
        catch (ParseException ex)
        {

        }

        String[] result = new String[2];
        result[0] = reportStartTime;
        result[1] = reportEndTime;
        return result;
    }

    /**
     * �����ڸ�ʽ���ɡ�YYYY��MM��DD�족�ĸ�ʽ�������ʽΪ��YYYY/MM/DD������YYYY-MM-DD��
     *
     * @param cDate String
     * @return String
     */
    public static String formatDatex(String cDate)
    {
        if (cDate.indexOf("/") != -1)
        {
            cDate = cDate.substring(0, cDate.indexOf("/")) + "��"
                    +
                    cDate.substring(cDate.indexOf("/") + 1,
                                    cDate.lastIndexOf("/")) +
                    "��"
                    + cDate.substring(cDate.lastIndexOf("/") + 1) + "��";
        }
        else if (cDate.indexOf("-") != -1)
        {
            cDate = cDate.substring(0, cDate.indexOf("-")) + "��"
                    +
                    cDate.substring(cDate.indexOf("-") + 1,
                                    cDate.lastIndexOf("-")) +
                    "��"
                    + cDate.substring(cDate.lastIndexOf("-") + 1) + "��";
        }
        return cDate;
    }

//    /**
//     * ת��ҵ��ϵͳ����������Ϊ����ϵͳ��չҵ���͵ĺ��� author: liujw
//     *
//     * @param cBranchType ����ϵͳ��չҵ����(BranchType)
//     * @return String ҵ��ϵͳ����������
//     */
//    public static String switchBranchTypetoSalChnl(String cBranchType)
//    {
//        String tSalChnl = "";
////        cBranchType = cBranchType.trim();
////        if (cBranchType.equals("2"))
////        { //����
////            tSalChnl = "01";
////        }
////        else if (cBranchType.equals("1"))
////        { //����
////            tSalChnl = "02";
////        }
////        else if (cBranchType.equals("3"))
////        //���б��� ��ҵ���� רҵ���� ���͹�˾
////        {
////            tSalChnl = "03";
////        }
//        LDCodeRelaDB tLDCodeRelaDB = new LDCodeRelaDB();
//        LDCodeRelaSet tLDCodeRelaSet = new LDCodeRelaSet();
//        tLDCodeRelaDB.setRelaType("salechnlvsbranchtype");
//        tLDCodeRelaDB.setCode1(cBranchType);
//        tLDCodeRelaSet = tLDCodeRelaDB.query();
//        for (int i = 1; i <= tLDCodeRelaSet.size(); i++)
//        {
//            tSalChnl += tLDCodeRelaSet.get(i).getCode3();
//            if(i != tLDCodeRelaSet.size())
//            {
//                tSalChnl+= ",";
//            }
//        }
//        return tSalChnl;
//    }

    //xjh Add 2005/04/13
    //չҵ���͵�����������ת��
    public static String[] switchBranchTypetoSalChnl(String cBranchType,
            String cBranchType2)
    {
        String[] result = new String[2];
        //BranchType 1 ����,2 ����,3 ����
        //SaleChnl 01 ��������,02 ��������,03 ���д���
        if (cBranchType == null || cBranchType.equals(""))
        {
            result[0] = "01,02,03";
        }
        else if (cBranchType.equals("2"))
        {
            result[0] = "01";
        }
        else if (cBranchType.equals("1"))
        {
            result[0] = "02";
        }
        else if (cBranchType.equals("3"))
        {
            result[0] = "03";
        }
        //BranchType2 01 ֱ��,02 �н�,03 ����,04 ����
        //SaleChnlDetail 01 ֱ���Ŷ�,02 ���͹�˾,03 ����˾,04 ��ҵ����˾,05 �������ۣ�06 ���д���
        if (cBranchType2 == null || cBranchType2.equals(""))
        {
            result[1] = "01,02,03,04,05,06";
        }
        else if (cBranchType2.equals("01"))
        {
            result[1] = "01";
        }
        else if (cBranchType2.equals("02"))
        {
            result[1] = "02,03,04";
        }
        else if (cBranchType2.equals("03"))
        {
            result[1] = "05";
        }
        else if (cBranchType2.equals("04"))
        {
            result[1] = "06";
        }
        return result;
    }

    //xjh Add 2005/04/14
    //����������չҵ���͵�ת��
    public static String[] switchSalChnl(String cSaleChnl,
                                         String cSaleChnlDetail)
    {
        String[] result = new String[2];
        //BranchType 1 ����,2 ����,3 ����
        //SaleChnl 01 ��������,02 ��������,03 ���д���
        if (cSaleChnl == null || cSaleChnl.equals(""))
        {
            result[0] = "";
        }
        else if (cSaleChnl.equals("01"))
        {
            result[0] = "2";
        }
        else if (cSaleChnl.equals("02"))
        {
            result[0] = "1";
        }
        else if (cSaleChnl.equals("03"))
        {
            result[0] = "3";
        }
        //BranchType2 01 ֱ��,02 �н�,03 ����,04 ����
        //SaleChnlDetail 01 ֱ���Ŷ�,02 ���͹�˾,03 ����˾,04 ��ҵ����˾,05 �������ۣ�06 ���д���
        if (cSaleChnlDetail == null || cSaleChnlDetail.equals(""))
        {
            result[1] = "";
        }
        else if (cSaleChnlDetail.equals("01"))
        {
            result[1] = "01";
        }
        else if (cSaleChnlDetail.equals("02"))
        {
            result[1] = "02";
        }
        else if (cSaleChnlDetail.equals("03"))
        {
            result[1] = "02";
        }
        else if (cSaleChnlDetail.equals("04"))
        {
            result[1] = "02";
        }
        else if (cSaleChnlDetail.equals("05"))
        {
            result[1] = "03";
        }
        else if (cSaleChnlDetail.equals("06"))
        {
            result[1] = "04";
        }
        return result;
    }

    /**
     * �õ�����2468λ�ĺ��� author: zy
     *
     * @param cManagecom ����ϵͳ�Ĺ������
     * @param mManagecom ��½�������
     * @return SSRS ������
     */
    public static SSRS getbranch(String cManagecom, String mManagecom)
    {
//     GlobalInput mGlobalInput =new GlobalInput() ;
        SSRS tSSRS = new SSRS();
        int n = cManagecom.trim().length();
        System.out.println("¼�����������" + cManagecom);
        System.out.println("¼��������ȣ�����" + n);
        String MM = "";
        switch (n)
        {
            case 2:
            {
                MM = " and length(a)=4 ";
                break;
            }
            case 4:
            {
                MM = " and length(a)=6 ";
                break;
            }
            case 6:
            {
                MM = " and length(a)=8 ";
                break;
            }
            case 8:
            {
                MM = " and length(a)=12 ";
                break;
            }
            case 12:
            {
                MM = " and length(a)=15 ";
                break;
            }
            case 15:
            {
                MM = " and length(a)=18 ";
                break;
            }
        }
        String nsql =
                "select a,b from (select trim(comcode) a,shortname b from ldcom union"
                +
                " select trim(branchattr) a,name b From labranchgroup where branchtype='1'"
                +
                " and (state<>1 or state is null) and length(trim(branchattr))<>8)"
                + " where a like '" + cManagecom + "%' and a like '" +
                mManagecom +
                "%' "
                + MM + "order by a";
//      SSRS ySSRS = new SSRS();
        ExeSQL aExeSQL = new ExeSQL();
        tSSRS = aExeSQL.execSQL(nsql);
        return tSSRS;
    }


    /**
     * ���ݴ�����ְ����ѯ������ϵ�У�ֻ���ø��գ�
     * @param cAgentGrade ���յĴ�����ְ��
     * @return String ְ����Ӧ�Ĵ�����ϵ��
     */
    public static String getAgentSeries(String cAgentGrade)
    {
        if (cAgentGrade == null || cAgentGrade.equals(""))
        {
            return null;
        }
        //xjh Modify 2005/3/22
//        String tSQL =
//                "select Trim(code2) from ldcodeRela where relaType = 'gradeserieslevel' "
//                + "and code1 = '" + cAgentGrade + "' ";
        String tSQL =
                "Select GradeProperty2 from LAAgentGrade where GradeCode = '" +
                cAgentGrade + "'";
        ExeSQL tExeSQL = new ExeSQL();
        String tAgentSeries = tExeSQL.getOneValue(tSQL);
        if (tExeSQL.mErrors.needDealError())
        {
            return null;
        }
        return tAgentSeries;
    }


    /**
     * ���ݴ����˱����ѯ��������˾ְ��
     * @param cAgentCode �����˱���
     * @return String ��������˾����
     */
    public static String getAgentGrade(String cAgentCode)
    {
        if (cAgentCode == null || cAgentCode.equals(""))
        {
            return null;
        }
        String tAgentGrade = "";
        String tSQL = "select agentgrade from latreeb where agentcode='" +
                      cAgentCode + "' order by makedate,maketime";
        ExeSQL tExeSQL = new ExeSQL();
        tAgentGrade = tExeSQL.getOneValue(tSQL);
        if (tAgentGrade == null || tAgentGrade.equals(""))
        {
            String aSQL = "select agentgrade from latree where agentcode='" +
                          cAgentCode + "' ";
            ExeSQL aExeSQL = new ExeSQL();
            tAgentGrade = aExeSQL.getOneValue(aSQL);
        }
        return tAgentGrade;
    }

    public static String AdjustCommCheck(String tAgentCode, String tStartDate)
    {
        if (!tStartDate.endsWith("01")) //�����ʼʱ�䲻�Ǵ�1�ſ�ʼ���򷵻ش���
        {
            return "�������ڱ����Ǵ�һ�ſ�ʼ";
        }
        String sql = "select max(indexcalno) from lawage where AgentCode='" +
                     tAgentCode + "'";
        ExeSQL tExeSQL = new ExeSQL();
        String maxIndexCalNo = tExeSQL.getOneValue(sql);
        if (maxIndexCalNo != null && !maxIndexCalNo.equals(""))
        {
            String lastDate = PubFun.calDate(tStartDate, -1, "M", null);
            lastDate = AgentPubFun.formatDate(lastDate, "yyyyMM");
            if (maxIndexCalNo.trim().compareTo(lastDate.trim()) > 0)
            {
                return ("�ϴη�������" + maxIndexCalNo.substring(0, 4) + "��" +
                        maxIndexCalNo.substring(4).trim() +
                        "�£���˵������ڱ��������µ���һ����1��");
            }
        }
        return "00";

    }


    /**
     * ��ѯ��������������ⲿ����
     *
     * @param cAgentCode �����˴���
     * @return String ��������������ⲿ����
     */
    public static String getAgentBranchAttr(String cAgentCode)
    {
        String tBranchAttr = "";
        if (cAgentCode == null || cAgentCode.equals(""))
        {
            return null;
        }
        String tSQL =
                "Select Trim(BranchAttr) From LABranchGroup Where AgentGroup = ("
                + "Select BranchCode From LAAgent Where AgentCode = '" +
                cAgentCode +
                "') ";
        ExeSQL tExeSQL = new ExeSQL();
        tBranchAttr = tExeSQL.getOneValue(tSQL);
        if (tExeSQL.mErrors.needDealError())
        {
            return null;
        }
        return tBranchAttr;
    }


    /**
     * ��cFormat�ĸ�ʽ ��ʽ���������� �����յı�ʾ��ĸΪ y M d
     *
     * @param cDate String
     * @param cFormat String
     * @return String
     */
    public static String formatDate(String cDate, String cFormat)
    {
        String FormatDate = "";
        Date tDate;
        SimpleDateFormat sfd = new SimpleDateFormat(cFormat);
        FDate fDate = new FDate();
        tDate = fDate.getDate(cDate.trim());
        FormatDate = sfd.format(tDate);
//       System.out.println("[--formatedate--]:"+FormatDate);
        return FormatDate;
    }
    
    /*
     * ���ظ�ʽ������
     * ��cFormat�ĸ�ʽ ��ʽ���������� �����յı�ʾ��ĸΪ y M d
     * @param cDate Date
     * @param cFormat String
     * @return String
     */
    public static String formatDate(Date cDate, String cFormat)
    {
        SimpleDateFormat sfd = new SimpleDateFormat(cFormat);
        String FormatDate = sfd.format(cDate);
        return FormatDate;
    }

    /**
     * ��LDCodeRela���в��ManageCom��Ӧ�ĵ�������
     *
     * @param cManageCom String
     * @return String
     */
    public static String getAreaType(String cManageCom)
    {
        //AreaType//������ֵ
        String tSql =
                "Select trim(code2) From LDCodeRela Where RelaType = 'comtoareatype' and trim(code1) = '" +
                cManageCom + "' and othersign='1'";
        ExeSQL tExeSQL = new ExeSQL();
        String tAreaType = tExeSQL.getOneValue(tSql);
        if (tExeSQL.mErrors.needDealError())
        {
            return null;
        }
        if (tAreaType == null || tAreaType.equals(""))
        {
            return null;
        }
        return tAreaType;
    }

    public static String getWageAreaType(String cManageCom)
    {
        //AreaType
        String tSql =
                "Select trim(code2) From LDCodeRela Where RelaType = 'wageareatype' and trim(code1) = '" +
                cManageCom + "'";
        ExeSQL tExeSQL = new ExeSQL();
        String tAreaType = tExeSQL.getOneValue(tSql);
        if (tExeSQL.mErrors.needDealError())
        {
            return null;
        }
        if (tAreaType == null || tAreaType.equals(""))
        {
            return null;
        }
        return tAreaType;
    }


    /**
     * ת����������26��---����25����������
     *
     * @Return :��ʽΪYYYYMM
     * @param cDate String
     * @return String
     */
    public static String ConverttoYM(String cDate)
    {
        String sYearMonth = "";
        ExeSQL tExeSQL = new ExeSQL();
        String tSql =
                "Select YearMonth From LAStatSegment Where StatType = '5' "
                + "And StartDate <= '" + cDate + "' And EndDate >= '" + cDate +
                "'";
        sYearMonth = tExeSQL.getOneValue(tSql);
        if (tExeSQL.mErrors.needDealError())
        {
            return null;
        }
        if (sYearMonth == null || sYearMonth.equals(""))
        {
            return null;
        }
        return sYearMonth;
    }

    /**
     * getManagecom ��LABranchGroup ������AgentGroupȡ����������
     *
     * @param cAgentGroup String
     * @return String
     */
    public static String getManagecom(String cAgentGroup)
    {
        if (cAgentGroup == null || cAgentGroup.equals(""))
        {
            return null;
        }
        String tSQL =
                "Select Trim(Managecom) From LABranchGroup Where AgentGroup = '" +
                cAgentGroup + "'";
        ExeSQL tExeSQL = new ExeSQL();
        String tManagecom = tExeSQL.getOneValue(tSQL);
        if (tExeSQL.mErrors.needDealError())
        {
            return null;
        }
        return tManagecom;
    }


    /**
     * ת����������26��---����25����������
     *
     * @Return :��ʽΪYYYYMMDD
     * @param cDate String
     * @return String
     */
    public static String ConverttoYMD(String cDate)
    {
        String sYearMonth = "";
        sYearMonth = ConverttoYM(cDate);
        if (sYearMonth != null && !sYearMonth.equals(""))
        {
            sYearMonth = sYearMonth.substring(0, 4) + "-" +
                         sYearMonth.substring(4) +
                         "-01";
        }
        return sYearMonth;
    }


    /**
     * ��ѯ����ϵ�к�
     * @param cAgentGroup �����ڲ�����
     * @return String ����ϵ�к�
     */
    public static String getBranchSeries(String cAgentGroup)
    {
        String tBranchSeries = cAgentGroup;
        if (cAgentGroup == null || cAgentGroup.equals(""))
        {
            return "";
        }
        String tSQL =
                "Select Trim(UpBranch) From LABranchGroup Where AgentGroup = '" +
                cAgentGroup + "'";
        ExeSQL tExeSQL = new ExeSQL();
        String tUpBranch = tExeSQL.getOneValue(tSQL);
        if (tExeSQL.mErrors.needDealError())
        {
            return "";
        }
        if (tUpBranch == null || tUpBranch.compareTo("") == 0)
        {
            System.out.println("�������");
        }
        else
        {
            //������ָû��������ϼ����������ѯ�ϼ�������Ϣ
            //xjh Modify �� 2005/02/17��������ʹ�á������ָ���
//            tBranchSeries = getBranchSeries(tUpBranch) + tBranchSeries;
            tBranchSeries = getBranchSeries(tUpBranch) + ":" + tBranchSeries;

//          System.out.println("��������" + tBranchSeries);
        }
        return tBranchSeries;
    }

    public static void main(String args[])
    {
//        String Agentcode = "000000000473";
//        System.out.println(getBranchSeries(Agentcode));
    }
}

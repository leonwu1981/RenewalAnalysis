/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.math.BigInteger;

//import com.sinosoft.lis.db.LABranchGroupDB;
import com.sinosoft.lis.db.LMCalModeDB;
//import com.sinosoft.lis.schema.LABranchGroupSchema;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;
import com.sinosoft.utility.VData;

/**
 * <p>Title: Webҵ��ϵͳ</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author YT
 * @version 1.0
 */
public class PubFun1
{
    public PubFun1()
    {
    }

    /**
     *<p>������ˮ�ŵĺ���<p>
     *<p>������򣺻�������  ������  У��λ   ����    ��ˮ��<p>
     *<p>          1-6     7-10   11     12-13   14-20<p>
     * @param cNoType Ϊ��Ҫ���ɺ��������
     * @param cNoLimit Ϊ��Ҫ���ɺ������������
     * @return ���ɵķ�����������ˮ�ţ��������ʧ�ܣ����ؿ��ַ���""
     */
    public static String CreateMaxNo(String cNoType, String cNoLimit)
    {
        try
        {
            //��̬������
//            System.out.println("sysmaxnotype:" + com.sinosoft.utility.SysConst.MAXNOTYPE);
//            String className = "com.sinosoft.lis.pubfun.SysMaxNo"
//                    + com.sinosoft.utility.SysConst.MAXNOTYPE;

            StringBuffer className = new StringBuffer(32);
            className.append("com.sinosoft.lis.pubfun.SysMaxNo");
            className.append(com.sinosoft.utility.SysConst.MAXNOTYPE);

            Class cc = Class.forName(className.toString());

            com.sinosoft.lis.pubfun.SysMaxNo tSysMaxNo = (com.sinosoft.lis.pubfun.SysMaxNo) (cc.
                    newInstance());

            return tSysMaxNo.CreateMaxNo(cNoType, cNoLimit);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * ������ˮ�ŵĺ���
     * ������򣺻�������  ������  У��λ   ����    ��ˮ��
     *           1-6     7-10   11     12-13   14-20
     * @param cNoType String Ϊ��Ҫ���ɺ��������
     * @param cNoLimit String Ϊ��Ҫ���ɺ������������
     * @param tVData VData
     * @return String ���ɵķ�����������ˮ�ţ��������ʧ�ܣ����ؿ��ַ���""
     */
    public static String CreateMaxNo(String cNoType, String cNoLimit, VData tVData)
    {
        try
        {
            //��̬������
//            System.out.println("sysmaxnotype:" + com.sinosoft.utility.SysConst.MAXNOTYPE);
//            String className = "com.sinosoft.lis.pubfun.SysMaxNo"
//                    + com.sinosoft.utility.SysConst.MAXNOTYPE;

            StringBuffer className = new StringBuffer(32);
            className.append("com.sinosoft.lis.pubfun.SysMaxNo");
            className.append(com.sinosoft.utility.SysConst.MAXNOTYPE);

            Class cc = Class.forName(className.toString());

            com.sinosoft.lis.pubfun.SysMaxNo tSysMaxNo = (com.sinosoft.lis.pubfun.SysMaxNo) (cc.
                    newInstance());

            return tSysMaxNo.CreateMaxNo(cNoType, cNoLimit, tVData);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * ���ܣ�����ָ�����ȵ���ˮ�ţ�һ����������һ����ˮ
     * @param cNoType String ��ˮ�ŵ�����
     * @param cNoLength int ��ˮ�ŵĳ���
     * @return String ���ز�������ˮ����
     */
    public static String CreateMaxNo(String cNoType, int cNoLength)
    {
        try
        {
            //��̬������
//            String className = "com.sinosoft.lis.pubfun.SysMaxNo" +
//                    com.sinosoft.utility.SysConst.MAXNOTYPE;

            StringBuffer className = new StringBuffer(32);
            className.append("com.sinosoft.lis.pubfun.SysMaxNo");
            className.append(com.sinosoft.utility.SysConst.MAXNOTYPE);

            Class cc = Class.forName(className.toString());

            com.sinosoft.lis.pubfun.SysMaxNo tSysMaxNo = (com.sinosoft.lis.pubfun.SysMaxNo) (cc.
                    newInstance());

            return tSysMaxNo.CreateMaxNo(cNoType, cNoLength);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * �õ�calculator��������SQL��䡣ԭ����calculator��ֻ�ܷ���aCalCode�������SQL���
     * ��һ��ִ�н������������õ�����aCalCode�������ʵ�ʵ�SQL��䡣
     * @param strSql String ��Ҫ������SQL���
     * @param calculator Calculator �Ѿ������˻�������Ԫ�ص�calculator����
     * @return String �������SQL��䡣��������κδ��󣬽����׳��쳣
     * @throws Exception
     */
    public static String getSQL(String strSql, Calculator calculator)
            throws Exception
    {
        LMCalModeDB tLMCalModeDB = new LMCalModeDB();

//        // �����Ž�������Ĵ���
//        String strSQL = strSql.replace('\'', '@');
//        //��ѯ��ʱ�����Ǻܺõ�ѡ��
//        strSQL = "select ''" + strSQL + "'' from dual";

        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select ''");
        tSBql.append(strSql.replace('\'', '@'));
        tSBql.append("'' from dual");

        tLMCalModeDB.setCalCode("XXX");
        tLMCalModeDB.setRiskCode("0");
        tLMCalModeDB.setType("F");
        tLMCalModeDB.setCalSQL(tSBql.toString());
//        tLMCalModeDB.setCalSQL(strSQL);

        if (!tLMCalModeDB.update())
        {
            throw new Exception("���ü��㹫ʽʱ����");
        }

        calculator.setCalCode("XXX");
//        strSQL = calculator.calculate();
        tSBql = new StringBuffer(calculator.calculate());

        if (tSBql.toString().equals(""))
        {
            throw new Exception("����SQL���ʱ����");
        }

        return tSBql.toString().replace('@', '\'');
    }

    /**
     * ͨ����������ڿ��Եõ������µļ����һ��ͼ������һ������� author: LH
     * <p><b>Example: </b><p>
     * <p>calFLDate("2003-5-8") returns 2003-4-26 2003-5-25<p>
     * @param tDate String ��ʼ���ڣ�(String,��ʽ��"YYYY-MM-DD")
     * @return String[] ���¿�ʼ�ͽ������ڣ�����String[2]
     */
    public static String[] calFLDate(String tDate)
    {
        String[] MonDate = new String[2];
        StringBuffer asql = new StringBuffer(128);
        asql.append(
                "select startdate,enddate from LAStatSegment where stattype='5' and startdate<='");
        asql.append(tDate);
        asql.append("' and enddate>='");
        asql.append(tDate);
        asql.append("'");
//        String asql =
//                "select startdate,enddate from LAStatSegment where stattype='5' and startdate<='" +
//                tDate + "' and enddate>='" + tDate + "'";
        ExeSQL aExeSQL = new ExeSQL();
        SSRS aSSRS = new SSRS();
        aSSRS = aExeSQL.execSQL(asql.toString());
        MonDate[0] = aSSRS.GetText(1, 1);
        MonDate[1] = aSSRS.GetText(1, 2);

        return MonDate;
    }

    /**
     * ���ɱ��������³����Ļ������룺BranchAttr
     * @param tUpAttr String �½��������ϼ�������BranchAttr����
     * @param tLength int ���볤��(10λ-���򶽵�����12����������15��Ӫ�����񲿡�18��Ӫҵ�飩
     * @return String �½������ı���BranchAttr
     */
    public static String CreateBranchAttr(String tUpAttr, int tLength)
    {
//        LABranchGroupDB tDB = new LABranchGroupDB();
//        LABranchGroupSchema tSch = new LABranchGroupSchema();
        ExeSQL tExeSql = new ExeSQL();

//        String aNewAttr = "";
        StringBuffer tSQL = new StringBuffer(128);
        tSQL.append("select max(branchattr) from labranchgroup where trim(branchattr) like '");
        tSQL.append(tUpAttr.trim());
        tSQL.append(
                "%' and (trim(state) <> '1' or state is null) and branchtype = '1' and length(trim(branchattr)) = ");
        tSQL.append(tLength);
//        String tSQL = "select max(branchattr) from labranchgroup where trim(branchattr) like '"
//                + tUpAttr.trim()
//                + "%' and (trim(state) <> '1' or state is null) and branchtype = '1' and length(trim(branchattr)) = "
//                + tLength;

        String aNewAttr = tExeSql.getOneValue(tSQL.toString());
//        System.out.println("---maximum = " + aNewAttr);

        BigInteger tInt = null;
        BigInteger tAdd = null;

        try
        {
            tInt = new BigInteger(aNewAttr.trim());
            tAdd = new BigInteger("1");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return "";
        }

        tInt = tInt.add(tAdd);

        aNewAttr = tInt.toString();
//        System.out.println("---aNewAttr = " + aNewAttr);

        return aNewAttr;
    }

    /**
     * ���Ժ���
     * @param args String[]
     */
    public static void main(String[] args)
    {
//    PubFun1 tPF1 = new PubFun1();
//    System.out.println(PubFun1.CreateMaxNo("TAKEBACKNO", 2));
    }
}

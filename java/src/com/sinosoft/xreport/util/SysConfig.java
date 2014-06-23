//Source file: D:\\ireport\\src\\com\\sinosoft\\ireport\\util\\SysConfig.java

/******************************************************************************************
 * �����ƣ� SysConfig
 * ��������ϵͳ�����������
 * ��������ˣ���ΰ
 * ����������ڣ�2002-04-24
 ******************************************************************************************/

package com.sinosoft.xreport.util;


/**
 * <p>Title: ����ϵͳ</p>
 * <p>Description: ������ṹ��xml��Ӧ��</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: SinoSoft Co. Ltd,</p>
 * @author unascribed
 * @version 1.0
 */
public class SysConfig
{
    /**************************************************
     * ·��������׼��: ����·��ĩβ�����ļ��ָ���.
     * Added by Yang on 2003/03/04
     public static final String HOST = "http://localhost:8080/applet/";
     */
//   public static  String TRUEHOST = "http://localhost:8080/";
//   public static  String FILEPATH = "D:\\xreport_data\\";
    public static final String ENDOFLINE = "\n";
//   public static  String FUNCTIONFILE = FILEPATH + "conf\\function.bsh";

    public static String TRUEHOST = "http://localhost:8080/ui/xreport/jsp/";
    public static String FILEPATH = "D:/xreport_data/";
    public static String FUNCTIONFILE = "D:/xreport_data/conf/function.bsh";

//   public static  String TRUEHOST = "http://10.0.22.129:7001/xreport/jsp/";
//   public static  String FILEPATH = "/export/home/bea/wlserver6.1/config/mydomain/applications/xreport_data/";
//   public static  String FUNCTIONFILE ="/export/home/bea/wlserver6.1/config/mydomain/applications/xreport_data/conf/function.bsh";

    /**
     * XML �ļ�ͷ����
     */
    public static final String HEADOFXML =
            "<?xml version=\"1.0\" encoding=\"GB2312\"?>";

    /**
     * \\\\\\\\\\\\\  yang added 02-05-23     \\\\\\\\\\\\\\\\\
     * һ���ָ���
     */
    public static final String SEPARATORONE = "|";

    /**
     * �����ָ���
     */
    public static final String SEPARATORTWO = "^";

    /**
     * �����ָ���
     */
    public static final String SEPARATORTREE = ";";

    /**
     * Ҫ�طָ���(����ϵͳר��)
     */
    public static final String SEPARATORITEM = "/";

    /**
     * У���ϵʽ�ָ���
     */
    public static final String SEPARATORCHECK = "&";

    /**
     * У���ϵ������Ϣ�ָ�
     */
    public static final String SEPARATORERROR = ";";

    /**
     * ���ݱ�����Դ���
     */
    public static final String DSTABLE = "DSTable";

    /**
     * ���ݱ�����Դ��ʼ���
     */
    public static final String HEADOFDSTABLE = "<" + DSTABLE + ">";

    /**
     * ���ݱ�����Դ�������
     */
    public static final String ENDOFDSTABLE = "</" + DSTABLE + ">";

    /**
     * ��������Դ���
     */
    public static final String DSREPORT = "DSReport";

    /**
     * ��������Դ��ʼ���
     */
    public static final String HEADOFDSREPORT = "<" + DSREPORT + ">";

    /**
     * ��������Դ�������
     */
    public static final String ENDOFDSREPORT = "</" + DSREPORT + ">";

    /**
     * ��������Դ��־
     */
    public static final String DSREPORTFLAG = "$";

    /**
     * ���������
     */
    public static final String FUNCTION = "Function";

    /**
     * ��������Դ��ʼ���
     */
    public static final String HEADOFFUNCTION = "<" + FUNCTION + ">";

    /**
     * ��������Դ�������
     */
    public static final String ENDOFFUNCTION = "</" + FUNCTION + ">";

    /**
     * ��������־
     */
    public static final String FUNCTIONFLAG = "@";

    /**
     * ����������
     */
    public static final String VARIABLE = "Var";

    /**
     * ���������ʼ���
     */
    public static final String HEADOFVARIABLE = "<" + VARIABLE + ">";

    /**
     * ��������������
     */
    public static final String ENDOFVARIABLE = "</" + VARIABLE + ">";

    /**
     * ���������־
     */
    public static final String VARIABLEFLAG = "&";

    /**
     * ������Ϣ���ӷ�
     */
    public static final String REPORTJOINCHAR = "_";

    /**
     * �ļ��ָ���
     */
    public static final String FILESEPARATOR = System.getProperty(
            "file.separator");

    /**
     * added 2002-09-29
     * ϵͳ����:���ȡ������report
     */
    public static final String FUNCREPORT = "report";

    /**
     * ϵͳ����:���ȡ������report
     */
    public static final String FUNCPLUGINS = "plugins";

    /**
     * added 2002-10-10
     * Session Pool��־
     */
    public static final String SESSIONPOOLSIGN = "SESSIONPOOL";

    /**
     * ////////////2002-11-04; �嵥����(ListReport)���ñ���
     */
    public static final String LRSUBJECT = "Subject";

    /**
     * �����
     */
    public static final String LRSTARTCELL = "StartCell";

    /**
     * ���忪ʼ��Ԫ��
     */
    public static final String LRCOLUMNVIEWS = "ColumnViews";

    /**
     * ��ʾ�ֶ�
     */
    public static final String LRCOLUMNREPLACE = "ColumnReplace";

    /**
     * ��Ҫ�滻����
     */
    public static final String LRSUMCOLUMNS = "SumColumns";

    /**
     * С��,�ϼ���
     */
    public static final String LRSUMDEPEND = "SumDepend";

    /**
     * С��������
     */
    public static final String LRSTRWHERE = "StrWhere";

    /**
     * �����ȡ������
     */
    public static final String LRSTRAPPEND = "StrAppend";

    /**
     * ��������Դsql
     * ////////////
     */
    public static final boolean NEEDCONVERT = false;

    //////////lixy add/////////////
    public static final String KEYSEPARATOR = ".";

    /**
     * ����ϵͳ����ģʽ
     */
    public static final String SYSTEMDATEPATTERN = "yyyy-MM-dd";

    /**
     * [?����Դ?],��̬����Դ
     */
    public static final String DYNAMICDS = "[?����Դ?]";

    /**
     * �������ݵ�����ģʽ, 0.00-������λ,#.##-û�б㲻����.
     */
    public static final String DECIMALPATTERN = "0.00";

    /**
     * ��ȡ����ϵͳ����ģʽ
     * @return ����ϵͳ����ģʽ
     */
    public static String getSystemDatePattern()
    {
        return SYSTEMDATEPATTERN;
    }

    /**
     * /////////////   end yang added        //////////////////
     */
    public SysConfig()
    {

    }

    static
    {
        if (null != System.getProperty("XR.TRUEHOST") &&
            !"".equals(System.getProperty("XR.TRUEHOST")))
        {
            SysConfig.TRUEHOST = System.getProperty("XR.TRUEHOST");
        }

        if (null != System.getProperty("XR.FILEPATH") &&
            !"".equals(System.getProperty("XR.FILEPATH")))
        {
            SysConfig.FILEPATH = System.getProperty("XR.FILEPATH");

            SysConfig.FUNCTIONFILE = SysConfig.FILEPATH + "conf/function.bsh";
        }

        System.err.println(SysConfig.FUNCTIONFILE);
    }


    /**
     * @param args
     */
    public static void main(String[] args)
    {
        SysConfig sysConfig1 = new SysConfig();
    }
}

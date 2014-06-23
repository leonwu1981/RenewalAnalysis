//Source file: D:\\ireport\\src\\com\\sinosoft\\ireport\\util\\SysConfig.java

/******************************************************************************************
 * 类名称： SysConfig
 * 类描述：系统参数定义对象
 * 最近更新人：方伟
 * 最近更新日期：2002-04-24
 ******************************************************************************************/

package com.sinosoft.xreport.util;


/**
 * <p>Title: 财务系统</p>
 * <p>Description: 满足多层结构和xml的应用</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: SinoSoft Co. Ltd,</p>
 * @author unascribed
 * @version 1.0
 */
public class SysConfig
{
    /**************************************************
     * 路径名常量准则: 所有路径末尾包括文件分隔符.
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
     * XML 文件头定义
     */
    public static final String HEADOFXML =
            "<?xml version=\"1.0\" encoding=\"GB2312\"?>";

    /**
     * \\\\\\\\\\\\\  yang added 02-05-23     \\\\\\\\\\\\\\\\\
     * 一级分隔符
     */
    public static final String SEPARATORONE = "|";

    /**
     * 二级分隔符
     */
    public static final String SEPARATORTWO = "^";

    /**
     * 三级分隔符
     */
    public static final String SEPARATORTREE = ";";

    /**
     * 要素分隔符(财务系统专用)
     */
    public static final String SEPARATORITEM = "/";

    /**
     * 校验关系式分隔符
     */
    public static final String SEPARATORCHECK = "&";

    /**
     * 校验关系错误信息分隔
     */
    public static final String SEPARATORERROR = ";";

    /**
     * 数据表数据源标记
     */
    public static final String DSTABLE = "DSTable";

    /**
     * 数据表数据源开始标记
     */
    public static final String HEADOFDSTABLE = "<" + DSTABLE + ">";

    /**
     * 数据表数据源结束标记
     */
    public static final String ENDOFDSTABLE = "</" + DSTABLE + ">";

    /**
     * 报表数据源标记
     */
    public static final String DSREPORT = "DSReport";

    /**
     * 报表数据源开始标记
     */
    public static final String HEADOFDSREPORT = "<" + DSREPORT + ">";

    /**
     * 报表数据源结束标记
     */
    public static final String ENDOFDSREPORT = "</" + DSREPORT + ">";

    /**
     * 报表数据源标志
     */
    public static final String DSREPORTFLAG = "$";

    /**
     * 报表函数标记
     */
    public static final String FUNCTION = "Function";

    /**
     * 报表数据源开始标记
     */
    public static final String HEADOFFUNCTION = "<" + FUNCTION + ">";

    /**
     * 报表数据源结束标记
     */
    public static final String ENDOFFUNCTION = "</" + FUNCTION + ">";

    /**
     * 报表函数标志
     */
    public static final String FUNCTIONFLAG = "@";

    /**
     * 报表变量标记
     */
    public static final String VARIABLE = "Var";

    /**
     * 报表变量开始标记
     */
    public static final String HEADOFVARIABLE = "<" + VARIABLE + ">";

    /**
     * 报表变量结束标记
     */
    public static final String ENDOFVARIABLE = "</" + VARIABLE + ">";

    /**
     * 报表变量标志
     */
    public static final String VARIABLEFLAG = "&";

    /**
     * 报表信息连接符
     */
    public static final String REPORTJOINCHAR = "_";

    /**
     * 文件分隔符
     */
    public static final String FILESEPARATOR = System.getProperty(
            "file.separator");

    /**
     * added 2002-09-29
     * 系统函数:表间取数函数report
     */
    public static final String FUNCREPORT = "report";

    /**
     * 系统函数:表间取数函数report
     */
    public static final String FUNCPLUGINS = "plugins";

    /**
     * added 2002-10-10
     * Session Pool标志
     */
    public static final String SESSIONPOOLSIGN = "SESSIONPOOL";

    /**
     * ////////////2002-11-04; 清单报表(ListReport)常用变量
     */
    public static final String LRSUBJECT = "Subject";

    /**
     * 主题表
     */
    public static final String LRSTARTCELL = "StartCell";

    /**
     * 定义开始单元格
     */
    public static final String LRCOLUMNVIEWS = "ColumnViews";

    /**
     * 显示字段
     */
    public static final String LRCOLUMNREPLACE = "ColumnReplace";

    /**
     * 需要替换的列
     */
    public static final String LRSUMCOLUMNS = "SumColumns";

    /**
     * 小计,合计列
     */
    public static final String LRSUMDEPEND = "SumDepend";

    /**
     * 小计依据列
     */
    public static final String LRSTRWHERE = "StrWhere";

    /**
     * 主题表取数条件
     */
    public static final String LRSTRAPPEND = "StrAppend";

    /**
     * 附加数据源sql
     * ////////////
     */
    public static final boolean NEEDCONVERT = false;

    //////////lixy add/////////////
    public static final String KEYSEPARATOR = ".";

    /**
     * 报表系统日期模式
     */
    public static final String SYSTEMDATEPATTERN = "yyyy-MM-dd";

    /**
     * [?数据源?],动态数据源
     */
    public static final String DYNAMICDS = "[?数据源?]";

    /**
     * 报表数据的数字模式, 0.00-保留两位,#.##-没有便不保留.
     */
    public static final String DECIMALPATTERN = "0.00";

    /**
     * 获取报表系统日期模式
     * @return 报表系统日期模式
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

/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Vector;

/*****************************************************************
 *               Program NAME: 读取配置文件类
 *                 programmer: hubo
 *                Create DATE: 2002.04.15
 *             Create address: Beijing
 *                Modify DATE: 2002.09.25
 *             Modify address: Beijing
 *****************************************************************
 *
 *          读取配置 AppConfig.properties 文件中的配置信息。
 *
 *****************************************************************
 */
public class ConfigInfo
{
    private static String ErrorString = "";
    private static String ConfigFilePath = "AppConfig.properties";
    //"AppConfig.properties";

    /**********************************************************************
     *                       构造方法(default)
     *      参数  : 无
     *      返回值：无
     **********************************************************************
     */
    public ConfigInfo()
    {}

    /**
     * 构造方法(以Config路径初始化实例)
     * @param XmlFilePath String
     */
    public ConfigInfo(String XmlFilePath)
    {
        ConfigFilePath = XmlFilePath;
    }

    /**
     * 得到Config路径字符串
     * @return String
     */
    public static String GetConfigPath()
    {
        return ConfigFilePath;
    }

    /**
     * 设置Config路径字符串
     * @param newpath String
     */
    public static void SetConfigPath(String newpath)
    {
        ConfigFilePath = newpath;
    }

    /**
     * 得到错误信息字符串
     * @return String
     */
    public static String GetErrorString()
    {
        return ErrorString;
    }

    /**
     * 通过名称得到字段配置信息
     * @param inpfieldname String
     * @return String
     */
    public static String GetValuebyName(String inpfieldname)
    {
        String ConfigValue = "";

        try
        {
            FileInputStream readconfig = new FileInputStream(ConfigFilePath); //文件对象
            byte tb[] = new byte[256]; //临时byte数组
            int len = 0; //读取信息
            int i = 0; //循环变量
            String fieldname = ""; //域名
            String fieldvalue = ""; //域值

            while ((len = readconfig.read()) != -1) //读配置信息
            {
                String tempStr = null;

                if (len == '\n') //若到行末则将该行转化为string
                {
                    tempStr = new String(tb); //转化为string
                    tempStr = tempStr.trim();
                    fieldname = StrTool.decodeStr(tempStr, "=", 1); //解析出配置域名

                    if (fieldname.equals(inpfieldname)) //是否与查找的域名匹配
                    {
                        fieldvalue = tempStr.substring(fieldname.length() + 1); //得到域值
                        break;
                    }
                    else
                    {
                        i = 0; //重置参数以便取下一行
                        tb = new byte[256];
                    }
                }
                else
                {
                    Integer reallen = new Integer(len); //将读取的int值转化为byte
                    tb[i] = reallen.byteValue();
                    i += 1;
                }
            }

            readconfig.close(); //关闭文件
            ConfigValue = fieldvalue.trim();

        }
        catch (Exception exception)
        {
            ErrorString = "<Conf.class> Parsing config file error:" +
                    exception.toString();
            UserLog.printException(ErrorString);
        }

        return ConfigValue;
    }

    /**
     * 通过名称范围得到字段配置信息
     * @param inpfieldname String
     * @return String
     */
    public static String GetValuebyArea(String inpfieldname)
    {
        String ConfigValue = "";

        try
        {
            File tFile = new File(ConfigFilePath);
            System.out.println("AppConfig.properties的绝对路径" + tFile.getAbsolutePath());
            FileInputStream readconfig = new FileInputStream(ConfigFilePath); //文件对象

            byte tb[] = new byte[256]; //临时byte数组
            int len = 0; //读取信息
            int i = 0; //循环变量
            String fieldname = ""; //域名
            String fieldvalue = ""; //域值

            while ((len = readconfig.read()) != -1) //读配置信息
            {
                String tempStr = null;

                if (len == '\n') //若到行末则将该行转化为string
                {
                    tempStr = new String(tb); //转化为string
                    tempStr = tempStr.trim();

                    fieldname = StrTool.decodeStr(tempStr, "=", 1); //解析出配置域名

                    if ((tempStr.length() == 0) && (fieldname.length() == 0))
                    {
                        i = 0; //重置参数以便取下一行
                        tb = new byte[256];
                        continue;
                    }

                    if (cmpFieldValue(fieldname, inpfieldname)) //是否与查找的域名匹配
                    {
                        fieldvalue = tempStr.substring(fieldname.length() + 1); //得到域值
                        break;
                    }
                    else
                    {
                        i = 0; //重置参数以便取下一行
                        tb = new byte[256];
                    }
                }
                else
                {
                    Integer reallen = new Integer(len); //将读取的int值转化为byte
                    tb[i] = reallen.byteValue();
                    i += 1;
                }

            }

            readconfig.close(); //关闭文件
            ConfigValue = fieldvalue.trim();

        }
        catch (Exception exception)
        {
            ErrorString = "<Conf.class> Parsing config file error:" +
                    exception.toString();
            UserLog.printException(ErrorString);
        }

        return ConfigValue;
    }

    /**
     * 通过名称范围得到字段配置信息
     * @param srcFieldName String
     * @param tagFieldName String
     * @return boolean
     */
    public static boolean cmpFieldValue(String srcFieldName,
            String tagFieldName)
    {
        String tmpStr[] = new String[5];
        String tmpValue[] = new String[5];
//        int strPos = 0;
        int i = 0;

        if (tagFieldName.length() == 0 || srcFieldName.indexOf(".") == -1)
        {
            return false;
        }

        try
        {
            srcFieldName = srcFieldName.trim() + ".";
            tagFieldName = tagFieldName.trim() + ".";

            tmpStr[0] = StrTool.decodeStr(srcFieldName, ".", 1);
            tmpStr[1] = StrTool.decodeStr(srcFieldName, ".", 2);
            tmpStr[2] = StrTool.decodeStr(srcFieldName, ".", 3);
            tmpStr[3] = StrTool.decodeStr(srcFieldName, ".", 4);
            tmpStr[4] = StrTool.decodeStr(srcFieldName, ".", 5);

            tmpValue[0] = StrTool.decodeStr(tagFieldName, ".", 1);
            tmpValue[1] = StrTool.decodeStr(tagFieldName, ".", 2);
            tmpValue[2] = StrTool.decodeStr(tagFieldName, ".", 3);
            tmpValue[3] = StrTool.decodeStr(tagFieldName, ".", 4);
            tmpValue[4] = StrTool.decodeStr(tagFieldName, ".", 5);

            for (i = 0; i < tmpStr.length; i++)
            {
                if (cmp2Value(tmpStr[i], tmpValue[i]))
                {
                }
                else
                {
                    return false;
                }
            }
            return true;
        }
        catch (Exception exception)
        {
            //UserLog.println("lalala" + exception.toString());
            return false;
        }
    }

    public static boolean cmp2Value(String strSource, String strTarget)
    {
        String tmpStrValue = "";
        String tmpStrArray[] = new String[2];
        int strPos = strSource.indexOf("-");

        if (strPos == -1)
        {
            if (strTarget.equals(strSource.substring(1, strSource.length() - 1)) ||
                    strSource.substring(1, strSource.length() - 1).equals("*"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            tmpStrValue = strSource.substring(1, strSource.length() - 1) + "-";
            tmpStrArray[0] = StrTool.decodeStr(tmpStrValue, "-", 1);
            tmpStrArray[1] = StrTool.decodeStr(tmpStrValue, "-", 2);

            int intCmp = new Integer(ChgData.chgNumericStr(strTarget)).intValue();
            int intAreaS = new Integer(ChgData.chgNumericStr(tmpStrArray[0])).
                    intValue();
            int intAreaE = new Integer(ChgData.chgNumericStr(tmpStrArray[1])).
                    intValue();

            if (intCmp >= intAreaS && intCmp <= intAreaE)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * 通过ip得到打印配置信息
     * @param inpip String
     * @return Vector
     */
    public static Vector GetIniByIp(String inpip)
    {
        String fieldvalue = "";
        Vector Strvector = new Vector();
        try
        {
            FileInputStream readconfig = new FileInputStream(ConfigFilePath); //文件对象
            byte tb[] = new byte[256]; //临时byte数组
            int len = 0; //读取信息
            int i = 0;
//            int intIndex = 0;
            //循环变量
            String fieldname = ""; //域名

            String inpfieldname = "(" + StrTool.decodeStr(inpip, ".", 1) + ")" +
                    "." + "(" + StrTool.decodeStr(inpip, ".", 2) +
                    ")";

            while ((len = readconfig.read()) != -1)
            {
                String tempStr = null;
                if (len == '\n') //若到行末则将该行转化为string
                {
                    tempStr = new String(tb); //转化为string
                    tempStr = tempStr.trim();
                    //解析出配置域名
                    fieldname = StrTool.decodeStr(tempStr, ".", 1) + "." +
                            StrTool.decodeStr(tempStr, ".", 2); //解析出配置域名
                    //System.out.println("haha"+fieldname);
                    if (fieldname.equals(inpfieldname)) //是否与查找的域名匹配
                    {
                        fieldvalue = tempStr; //得到域值
                        Strvector.addElement(fieldvalue);
                        i = 0; //重置参数以便取下一行
                        tb = new byte[256];
                    }
                    else
                    {
                        i = 0; //重置参数以便取下一行
                        tb = new byte[256];
                    }
                }
                else
                {
                    Integer reallen = new Integer(len); //将读取的int值转化为byte
                    tb[i] = reallen.byteValue();
                    i += 1;
                }

            }
            readconfig.close();
        }
        catch (Exception exception)
        {
            ErrorString = "<Conf.class> Parsing config file error:" + exception.toString();
            UserLog.printException(ErrorString);
        }

        for (int i = 0; i < Strvector.size(); i++)
        {
            System.out.println((String) Strvector.get(i));
        }

        return Strvector;

    }

    /**
     * 通过ip得到打印配置信息
     * @param inputStr String
     * @return boolean
     */
    public static boolean DeleteByStr(String inputStr)
    {

        try
        {
            File output = new File("AppConfig.properties.tmp");
            output.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(output.
                    getPath(), true));
            File input = new File(ConfigFilePath);
            FileInputStream readconfig = new FileInputStream(input); //文件对象
            byte tb[] = new byte[256]; //临时byte数组
            int len = 0; //读取信息
            int i = 0;
//            int intIndex = 0;
            while ((len = readconfig.read()) != -1)
            {
                String tempStr = null;
                if (len == '\n') //若到行末则将该行转化为string
                {

                    tempStr = new String(tb);
                    if (!tempStr.substring(0,
                            inputStr.length()).equals(inputStr))
                    {
                        tempStr = tempStr.trim();
                        System.out.println(tempStr);
                        out.write(tempStr);
                        out.write('\n');
                    }

                    i = 0;
                    tb = new byte[256];
                }

                else
                {
                    if (len != 10)
                    {
                        Integer reallen = new Integer(len); //将读取的int值转化为byte
                        tb[i] = reallen.byteValue();
                        i += 1;
                    }
                }
            }
            readconfig.close();
            input.delete();
            out.close();
            File tempfile = new File(ConfigFilePath);
            //tempfile.createNewFile();
            output.renameTo(tempfile);
            return true;

        }
        catch (Exception exception)
        {
            ErrorString = "<Conf.class> Parsing config file error:" + exception.toString();
            UserLog.printException(ErrorString);
            return false;
        }
    }

    /**
     * 读取配置文件对于特殊字符转换的信息
     * @return SSRS
     */
    public static SSRS GetValuebyCon()
    {
        SSRS tSSRS = new SSRS(2);

        try
        {
            FileInputStream readconfig = new FileInputStream(ConfigFilePath); //文件对象
            byte tb[] = new byte[256]; //临时byte数组
            int len = 0; //读取信息
            int i = 0; //循环变量
            int position;
            String tempStr = null;

            while ((len = readconfig.read()) != -1) //读配置信息
            {

                if (len == '!') //若到行末则将该行转化为string
                {
                    tempStr = new String(tb); //转化为string

                    position = tempStr.indexOf('|');
                    tSSRS.SetText(tempStr.substring(0, position));
                    if (tempStr.substring(0, position).equals("\r\n"))
                    {
                        tempStr = tempStr.trim();
                        tSSRS.SetText(tempStr.substring(position - 1,
                                tempStr.length()));
                    }
                    else
                    {
                        tempStr = tempStr.trim();
                        tSSRS.SetText(tempStr.substring(position + 1,
                                tempStr.length()));
                    }
                    i = 0; //重置参数以便取下一行
                    tb = new byte[256];
                }
                else
                {
                    Integer reallen = new Integer(len); //将读取的int值转化为byte
                    tb[i] = reallen.byteValue();
                    i += 1;
                }

            }

            readconfig.close(); //关闭文件

        }
        catch (Exception exception)
        {
            ErrorString = "<Conf.class> Parsing config file error:" +
                    exception.toString();
            UserLog.printException(ErrorString);
        }

        return tSSRS;
    }

    /**
     * 调试函数
     * @param args String[]
     */
    public static void main(String args[])
    {
//        ConfigInfo test = new ConfigInfo();
//        System.out.println(test.GetValuebyName(SysConst.USERLOGPATH));
//        System.out.println(System.getProperty("user.home"));
    }
}

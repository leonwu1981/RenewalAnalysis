/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.text.DecimalFormat;

/**
 ****************************************************************
 *               Program NAME: 字符串处理工具类
 *                 programmer: Ouyangsheng (Modify)
 *                Create DATE: 2002.05.21
 *             Create address: Beijing
 *                Modify DATE:
 *             Modify address:
 *****************************************************************
 * 定义数据类型相关处理工具类,多个静态的数字转换成字符串的重载函数
 *                  对字符串操作的工具类。
 *
 *****************************************************************
 */
public class ChgData 
{
    /**
     * 将byte值转换成字符串，当数值为0时返回""，否则返回数值字符串
     * @param byteValue byte
     * @return String 转换后的字符串
     */
    public static String chgData(byte byteValue)
    {
        String strReturn = null;
        if (byteValue == 0)
        {
            strReturn = "0";
        }
        else
        {
            strReturn = String.valueOf(byteValue);
        }
        return strReturn;
    }

    /**
     * 将short值转换成字符串，当数值为0时返回""，否则返回数值字符串
     * @param shortValue short
     * @return String 转换后的字符串
     */
    public static String chgData(short shortValue)
    {
        String strReturn = null;
        if (shortValue == 0)
        {
            strReturn = "0";
        }
        else
        {
            strReturn = String.valueOf(shortValue);
        }
        return strReturn;
    }

    /**
     * 将int值转换成字符串，当数值为0时返回""，否则返回数值字符串
     * @param intValue int
     * @return String 转换后的字符串
     */
    public static String chgData(int intValue)
    {
        String strReturn = null;
        if (intValue == 0)
        {
            strReturn = "0";
        }
        else
        {
            strReturn = String.valueOf(intValue);
        }
        return strReturn;
    }

    /**
     * 将long值转换成字符串，当数值为0时返回""，否则返回数值字符串
     * @param longValue long
     * @return String 转换后的字符串
     */
    public static String chgData(long longValue)
    {
        String strReturn = null;
        if (longValue == 0)
        {
            strReturn = "0";
        }
        else
        {
            strReturn = String.valueOf(longValue);
        }
        return strReturn;
    }

    /**
     * 将float值转换成字符串，当数值为0时返回""，否则返回数值字符串
     * @param floatValue float
     * @return String 转换后的字符串
     */
    public static String chgData(float floatValue)
    {
        String strReturn = null;

        if (floatValue == 0)
        {
            strReturn = "0";
        }
        else
        {
            //格式化浮点数据，防止返回的时候呈现科学计数法
            DecimalFormat df = new DecimalFormat("#.###");
//            strReturn = String.valueOf(floatValue);
            strReturn = String.valueOf(df.format(floatValue));
        }
        return strReturn;
    }

    /**
     * 将double转换成字符串，当数值为0时返回""，否则返回数值字符串
     * @param doubleValue double
     * @return String 转换后的字符串
     */
    public static String chgData(double doubleValue)
    {
        String strReturn = null;
        if (doubleValue == 0)
        {
            strReturn = "0";
        }
        else
        {
            //格式化浮点数据，防止返回的时候呈现科学计数法
            DecimalFormat df = new DecimalFormat("#.##################");
//            strReturn = String.valueOf(doubleValue);
            strReturn = String.valueOf(df.format(doubleValue));
        }
        return strReturn;
    }

    /**
     * 转换数值字符串，当字符串变量的值为""或者为空时,将其转换为字符串"0"
     * @param strValue String
     * @return String 转换后的字符串
     */
    public static String chgNumericStr(String strValue)
    {
        if (strValue == null)
        {
            return "0";
        }
        else if (strValue.trim().equals("") || strValue.length() == 0)
        {
            return "0";
        }
        else
        {
            return strValue;
        }
    }

    /**
     * 转换布尔类型值
     * @param strValue String
     * @return String 布尔变量对应的中文描述
     */
    public static String getBooleanDescribe(String strValue)
    {
        String strReturn = strValue;
        if (strValue.equals("Y") || strValue.equals("y"))
        {
            strReturn = "是";
        }
        else if (strValue.equals("N") || strValue.equals("n"))
        {
            strReturn = "否";
        }
        return strReturn;
    }

    /**
     * 测试函数
     * @param args String[]
     */
    public static void main(String[] args)
    {
        double a = 12.4445;
        System.out.println(chgData(a));
    }
}

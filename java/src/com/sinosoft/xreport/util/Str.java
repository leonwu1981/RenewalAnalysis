//Source file: D:\\ireport\\src\\com\\sinosoft\\ireport\\util\\Str.java

/******************************************************************************************
 * 类名称：Str
 * 类描述：字符串相关处理工具类
 * 最近更新人：郭妍
 * 最近更新日期: 2002-01-13
 ******************************************************************************************/
package com.sinosoft.xreport.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
/**
 * 声明Str类所属类包
 */
import java.util.Calendar;

/**
 * 数据类型相关处理工具类
 */
public class Str
{
    public static final String EQUAL = "=";
    public static final String PACKAGESPILTER = "|";
    public static final String GREATER = ">";
    public static final String GREATGE_EQUAL = ">=";
    public static final String LESS = "<";
    public static final String LESS_EQUAL = "<=";
    public static final String NOT_EQUAL = "!=";
    public static final String CONTAIN = "*";
    public static final String BETWEEN = ":";
    public static final String DATEDELIMITER = "-";
    public static final String VISADATEDELIMITER = "-";
    public static final String TIMEDELIMITER = ":";
    public static final String ADDRESSDELIMITER = "$$";
    public static final String DELIMITER = "^";
    public static final String OR = "~!";
    public static final int LENGTH_OR = 2;
    public static final String BETWEEN_AND = ":";
    public static final String BLANK = "?";

    /**
     * 将字符串按照指定的分隔字符进行拆分，返回从指定序号的分隔符到前一个分隔符之间的字
     * 符串
     * @param  strMain 主字符串
     * @param  strDelimiters 分隔符
     * @param  intSerialNo 分隔符序号
     * @return 指定序号的分隔符到前一个分隔符之间的字符串,如果没有找到则返回""
     */
    public static String decodeStr(String strMain, String strDelimiters,
                                   int intSerialNo)
    {
        int intIndex = 0; //分隔符出现在主字符串中的起始位置
        int intCount = 0; //在扫描主字符串的过程中,第几次遇到分隔符字符串
        String strReturn = ""; //作为返回值的字符串

        if (strMain.length() < strDelimiters.length())
        {
            return ""; //若主字符串比分隔符串还要短，则返回空字符串
        }

        intIndex = strMain.indexOf(strDelimiters);
        if (intIndex == -1)
        {
            return ""; //若主字符串中不存在分隔符，则返回空字符串
        }

        while (intIndex != -1) //未找到分隔符时退出循环，并返回空字符串
        {
            strReturn = strMain.substring(0, intIndex);
            intCount++;
            if (intCount == intSerialNo)
            {
                if (intIndex == 0)
                {
                    return "";
                }
                else
                {
                    return strReturn.trim();
                }
            }
            strMain = strMain.substring(intIndex + 1);
            intIndex = strMain.indexOf(strDelimiters);
        }
        ;
        return "";
    }

    /**
     * 将字符串按照指定的分隔字符进行拆分，返回从指定序号的分隔符到前一个分隔符之间的字
     * 符串
     * @param  strMain 主字符串
     * @param  strDelimiters 分隔符
     * @param  intSerialNo 分隔符序号
     * @return 指定序号的分隔符到前一个分隔符之间的字符串,如果没有找到则返回""
     */
    public static String decodeBuf(StringBuffer strMain, String strDelimiters,
                                   int intSerialNo)
    {
        String strReturn = "";
        char str[] = new char[100];
        int intIndex = 0; //分隔符出现在主字符串中的起始位置
        int intCount = 0; //在扫描主字符串的过程中,第几次遇到分隔符字符串
        StringBuffer sb = new StringBuffer();

        if (strMain.length() < strDelimiters.length())
        {
            return ""; //若主字符串比分隔符串还要短，则返回空字符串
        }

        intIndex = strMain.toString().indexOf(strDelimiters);
        if (intIndex == -1)
        {
            return ""; //若主字符串中不存在分隔符，则返回空字符串
        }

//    while (intIndex !=-1) //未找到分隔符时退出循环，并返回空字符串
//    {
//      intCount++;
//      if (intCount == 1)
//      {
        if (intIndex == 0)
        {
            strMain = strMain.delete(0, intIndex + 1);
            return "";
        }
        else
        {
            //瓶颈begin
            strReturn = strMain.substring(0, intIndex);
//          strMain.getChars(0,intIndex,str,0);
//          strReturn=String.copyValueOf(str,0,intIndex);
            //瓶颈end
            strMain = strMain.delete(0, intIndex + 1);
            return strReturn;
        }
//      }
//    };
//    return "";
    }

    /**
     * 获取子串在主串中出现 n 次的位置
     * @param strMain：主字符串
     * @param strSub：子字符串
     * @param intTimes：出现次数
     * @param strMain
     * @param strSub
     * @param intTimes
     * @return  位置值，如果子串在主串中没有出现指定次数，则返回-1
     */
    public static int getPos(String strMain, String strSub, int intTimes)
    {
        int intCounter = 0; //循环记数
        int intPosition = 0; //位置记录
        int intLength = strSub.length(); //子串长度
        String strTemp = "";
        if (intTimes <= 0)
        {
            return -1;
        }
        while (intCounter < intTimes)
        {
            intPosition = strMain.indexOf(strSub, intPosition);
            if (intPosition == -1)
            {
                return -1;
            }
            intCounter++;
            intPosition += intLength;
            strTemp = strMain.substring(intPosition);
        }
        return intPosition - 1;
    }

    /**
     * 获取从指定位置开始子串在主串中出现 n 次的位置
     * @param strMain：主字符串
     * @param strSub：子字符串
     * @param intStartIndex：起始位置
     * @param intTimes：出现次数
     * @param strMain
     * @param strSub
     * @param intStartIndex
     * @param intTimes
     * @return  位置值,如果从起始位置起子串在主串中没有出现指定次数,则返回-1
     */
    public static int getPos(String strMain, String strSub, int intStartIndex,
                             int intTimes)
    {
        if (strMain.length() - 1 < intStartIndex)
        {
            return -1;
        }
        int intPosition = getPos(strMain.substring(intStartIndex), strSub,
                                 intTimes);
        if (intPosition != -1)
        {
            intPosition += intStartIndex;
        }
        return intPosition;
    }

    /**
     * 将字符串转换为GBK字符串
     * @param   strOriginal:原串
     * @param strOriginal
     * @return  将原串由ISO8859_1(Unicode)编码转换为GBK编码
     */
    public static String unicodeToGBK(String strOriginal)
    {
        if (!SysConfig.NEEDCONVERT)
        {
            return strOriginal;
        }

        if (strOriginal != null)
        {
            try
            {
                return new String(strOriginal.getBytes("ISO8859_1"), "GBK");
            }
            catch (Exception exception)
            {
                return strOriginal;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * 将字符串转换为Unicode字符串
     * @param strOriginal:原串
     * @param strOriginal
     * @return  将原串由GBK编码转换为ISO8859_1(Unicode)编码
     */
    public static String GBKToUnicode(String strOriginal)
    {
        if (strOriginal != null)
        {
            try
            {
                return new String(strOriginal.getBytes("GBK"), "ISO8859_1");
            }
            catch (Exception exception)
            {
                return strOriginal;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * 解包给定字符串
     * @param   strMessage：字符串
     * @param   intCount：解包次数
     * @param   cl：解包的类型
     * @return  将每个解包数据生成对应的类实例,并将这些实例作为返回Vector的元素
     * @throws  Exception
     * 使用指定类中的decode()方法解包给定字符串
     * @param strMessage:字符串，intCount:解包次数，cl:包含decode()方法的类
     * @return  将每个解包数据生成对应的类实例,并将这些实例作为返回数组
     * @throws  Exception 如果查找decode方法出错、或者解包出错，方法抛出异常
     * 字符串替换函数
     * @param strMain：原串，strFind：查找字符串，strReplaceWith：替换字符串
     * @param strMain
     * @param strFind
     * @param strReplaceWith
     * @return 替换后的字符串，如果原串为空或者为""，则返回""
     */
    public static String replace(String strMain, String strFind,
                                 String strReplaceWith)
    {
        String strReturn = "";
        int intStartIndex = 0,
                            intEndIndex = 0;

        if (strMain == null || strMain.equals(""))
        {
            return "";
        }

        while ((intEndIndex = strMain.indexOf(strFind, intStartIndex)) > -1)
        {
            strReturn = strReturn +
                        strMain.substring(intStartIndex, intEndIndex) +
                        strReplaceWith;
            intStartIndex = intEndIndex + strFind.length();
        }
        strReturn = strReturn + strMain.substring(intStartIndex, strMain.length());
        return strReturn;
    }

    /**
     * 字符串替换函数(只替换第一次）
     * @param strMain：原串，strFind：查找字符串，strReplaceWith：替换字符串
     * @param strMain
     * @param strFind
     * @param strReplaceWith
     * @return 替换后的字符串，如果原串为空或者为""，则返回""
     */
    public static String replaceone(String strMain, String strFind,
                                    String strReplaceWith)
    {
        String strReturn = "";
        int intStartIndex = 0,
                            intEndIndex = 0;

        if (strMain == null || strMain.equals(""))
        {
            return "";
        }
        intEndIndex = strMain.indexOf(strFind, intStartIndex);
        if (intEndIndex > -1)
        {
            //intStartIndex = intEndIndex + strFind.length();
            strReturn =
                    strReturn + strMain.substring(intStartIndex, intEndIndex) +
                    strReplaceWith +
                    strMain.substring((intEndIndex + strFind.length()),
                                      strMain.length());

        }
        //strReturn = strReturn + strMain.substring(intStartIndex,strMain.length());
        return strReturn;
    }

    /**
     * 由给定日期字符串获取格式日期(年/月/日)
     * @param   strYear：年，strMonth：月，strDay：日
     * @param strYear
     * @param strMonth
     * @param strDay
     * @return  格式日期：年/月/日
     */
    public static String getDate(String strYear, String strMonth, String strDay)
    {
        String strReturn = "";
        int intYear = 0;
        int intMonth = 0;
        int intDay = 0;
        if ((strYear != null) && (strMonth != null) && (strDay != null) &&
            (strYear.trim().length() > 0) && (strMonth.trim().length() > 0) &&
            (strDay.trim().length() > 0))
        {
            intYear = new Integer(strYear).intValue();
            intMonth = new Integer(strMonth).intValue();
            intDay = new Integer(strDay).intValue();
            if ((intYear <= 0) || (intMonth <= 0) || (intMonth > 12) ||
                (intDay <= 0) || (intDay > 31))
            {
                strReturn = "";
            }
            else
            {
                strReturn = "" + intYear;

                if (intMonth < 10)
                {
                    strReturn += DATEDELIMITER + "0" + intMonth;
                }
                else
                {
                    strReturn += DATEDELIMITER + intMonth;
                }

                if (intDay < 10)
                {
                    strReturn += DATEDELIMITER + "0" + intDay;
                }
                else
                {
                    strReturn += DATEDELIMITER + intDay;
                }
            }
        }
        return strReturn;
    }

    /**
     * 获得由格式的系统日期(年/月/日)
     * @param   无
     * @return  格式日期：年/月/日
     */
    public static String getDate()
    {
        String strReturn = "";
        int intYear = Calendar.getInstance().get(Calendar.YEAR);
        int intMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int intDate = Calendar.getInstance().get(Calendar.DATE);
        strReturn = "" + intYear;

        if (intMonth < 10)
        {
            strReturn += DATEDELIMITER + "0" + intMonth;
        }
        else
        {
            strReturn += DATEDELIMITER + intMonth;
        }

        if (intDate < 10)
        {
            strReturn += DATEDELIMITER + "0" + intDate;
        }
        else
        {
            strReturn += DATEDELIMITER + intDate;
        }
        return strReturn;
    }

    /**
     * 由给定日期字符串获取格式日期(年/月)
     * @param   strYear：年，strMonth：月
     * @param strYear
     * @param strMonth
     * @return  格式日期：年/月
     */
    public static String getDate(String strYear, String strMonth)
    {
        String strReturn = "";
        int intYear = 0;
        int intMonth = 0;
        if ((strYear != null) && (strMonth != null) &&
            (strYear.trim().length() > 0) && (strMonth.trim().length() > 0))
        {
            intYear = new Integer(strYear).intValue();
            intMonth = new Integer(strMonth).intValue();
            if ((intYear <= 0) || (intMonth <= 0) || (intMonth > 12))
            {
                strReturn = "";
            }
            else
            {
                strReturn = "" + intYear + DATEDELIMITER + intMonth;
            }
        }
        return strReturn;
    }

    /**
     * 获取日期值中的年份
     * @param    strDate：日期
     * @param strDate
     * @return   年
     */
    public static String getYear(String strDate)
    {
        int intPosition = 0;
        String strReturn = "";
        int intYear = 0;

        if ((strDate != null) && (strDate.trim().length() > 0))
        {
            intPosition = Str.getPos(strDate, DATEDELIMITER, 1);
            if (intPosition > 0)
            {
                strReturn = strDate.substring(0, intPosition);
                intYear = new Integer(strReturn).intValue();
                if ((intYear <= 0))
                {
                    strReturn = "";
                }
                else
                {
                    strReturn = "" + intYear;
                }

                if ((intYear < 10) && (strReturn != ""))
                {
                    strReturn = "0" + strReturn;
                }
            }
        }
        return strReturn;
    }

    /**
     * 获取系统日期中的年份
     * @param    无
     * @return   年
     */
    public static String getYear()
    {
        String strReturn = "";
        int intYear = Calendar.getInstance().get(Calendar.YEAR);
        strReturn = "" + intYear;
        return strReturn;
    }

    /**
     * 获取日期值中的月份
     * @param    strDate：日期
     * @param strDate
     * @return   月
     */
    public static String getMonth(String strDate)
    {
        int intPosition1 = 0, intPosition2 = 0;
        String strReturn = "";
        int intMonth = 0;
        if ((strDate != null) && (strDate.trim().length() > 0))
        {
            intPosition1 = Str.getPos(strDate, DATEDELIMITER, 1);
            intPosition2 = Str.getPos(strDate, DATEDELIMITER, 2);
            if ((intPosition1 > 0) && intPosition2 > intPosition1)
            {

                strReturn = strDate.substring(intPosition1 + 1, intPosition2);

                intMonth = new Integer(strReturn).intValue();
                if ((intMonth <= 0) || (intMonth > 12))
                {
                    strReturn = "";
                }
                else
                {
                    strReturn = "" + intMonth;
                }

                if ((intMonth < 10) && (strReturn != ""))
                {
                    strReturn = "0" + strReturn;
                }
            }
        }
        return strReturn;
    }

    /**
     * 获取系统日期中的月份
     * @param    strDate：日期
     * @return   月
     */
    public static String getMonth()
    {
        String strReturn = "";
        int intMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        if (intMonth < 10)
        {
            strReturn = "0" + intMonth;
        }
        else
        {
            strReturn = "" + intMonth;
        }
        return strReturn;
    }

    /**
     * 获取给定日期值中的天
     * @param    strDate：日期
     * @param strDate
     * @return   天
     */
    public static String getDay(String strDate)
    {
        int intPosition = 0;
        String strReturn = "";
        int intDay = 0;
        if ((strDate != null) && (strDate.trim().length() > 0))
        {
            intPosition = Str.getPos(strDate, DATEDELIMITER, 2);
            if (intPosition > 0)
            {

                strReturn = strDate.substring(intPosition + 1);

                intDay = new Integer(strReturn).intValue();

                if ((intDay <= 0) || (intDay > 31))
                {
                    strReturn = "";
                }
                else
                {
                    strReturn = "" + intDay;
                }

                if ((intDay < 10) && (strReturn != ""))
                {
                    strReturn = "0" + strReturn;
                }
            }
        }
        return strReturn;
    }

    /**
     * 获取系统日期值中的天
     * @param    无
     * @return   天
     */
    public static String getDay()
    {
        String strReturn = "";
        int intDate = Calendar.getInstance().get(Calendar.DATE);
        if (intDate < 10)
        {
            strReturn = "0" + intDate;
        }
        else
        {
            strReturn = "" + intDate;
        }

        return strReturn;
    }

    /**
     * 获取系统时间值中的小时
     * @param    无
     * @return   小时
     */
    public static String getHour()
    {
        String strReturn = "";
        int intHour = Calendar.getInstance().get(Calendar.HOUR) +
                      (Calendar.HOUR_OF_DAY + 1) *
                      Calendar.getInstance().get(Calendar.AM_PM);
        if (intHour < 10)
        {
            strReturn = "0" + intHour;
        }
        else
        {
            strReturn = "" + intHour;
        }

        return strReturn;
    }

    /**
     * 获取系统时间值中的分钟
     * @param    无
     * @return   分钟
     */
    public static String getMinute()
    {
        String strReturn = "";
        int intMinute = Calendar.getInstance().get(Calendar.MINUTE);
        if (intMinute < 10)
        {
            strReturn = "0" + intMinute;
        }
        else
        {
            strReturn = "" + intMinute;
        }

        return strReturn;
    }

    /**
     * 获取系统时间值中秒数
     * @param    无
     * @return   秒数
     */
    public static String getSecond()
    {
        String strReturn = "";
        int intSecond = Calendar.getInstance().get(Calendar.SECOND);
        if (intSecond < 10)
        {
            strReturn = "0" + intSecond;
        }
        else
        {
            strReturn = "" + intSecond;
        }

        return strReturn;
    }

    /**
     * 获取日期值中的年份
     * @param    strDate：日期
     * @param strDate
     * @return   年
     */
    public static String getVisaYear(String strDate)
    {
        int intPosition = 0;
        String strReturn = "";
        int intYear = 0;

        if ((strDate != null) && (strDate.trim().length() > 0))
        {
            intPosition = Str.getPos(strDate, VISADATEDELIMITER, 1);
            if (intPosition > 0)
            {
                strReturn = strDate.substring(0, intPosition);
                intYear = new Integer(strReturn).intValue();
                if ((intYear <= 0))
                {
                    strReturn = "";
                }
                else
                {
                    strReturn = "" + intYear;
                }

                if ((intYear < 10) && (strReturn != ""))
                {
                    strReturn = "0" + strReturn;
                }
            }
        }
        return strReturn;
    }

    /**
     * 获取单证日期值中的月份
     * @param    strDate：日期
     * @param strDate
     * @return   月
     */
    public static String getVisaMonth(String strDate)
    {
        int intPosition1 = 0, intPosition2 = 0;
        String strReturn = "";
        int intMonth = 0;
        if ((strDate != null) && (strDate.trim().length() > 0))
        {
            intPosition1 = Str.getPos(strDate, VISADATEDELIMITER, 1);
            intPosition2 = Str.getPos(strDate, VISADATEDELIMITER, 2);
            if ((intPosition1 > 0) && intPosition2 > intPosition1)
            {

                strReturn = strDate.substring(intPosition1 + 1, intPosition2);

                intMonth = new Integer(strReturn).intValue();
                if ((intMonth <= 0) || (intMonth > 12))
                {
                    strReturn = "";
                }
                else
                {
                    strReturn = "" + intMonth;
                }

                if ((intMonth < 10) && (strReturn != ""))
                {
                    strReturn = "0" + strReturn;
                }
            }
        }
        return strReturn;
    }

    /**
     * 获取单证给定日期值中的天
     * @param    strDate：日期
     * @param strDate
     * @return   天
     */
    public static String getVisaDay(String strDate)
    {
        int intPosition = 0;
        String strReturn = "";
        int intDay = 0;
        if ((strDate != null) && (strDate.trim().length() > 0))
        {
            intPosition = Str.getPos(strDate, VISADATEDELIMITER, 2);
            if (intPosition > 0)
            {

                strReturn = strDate.substring(intPosition + 1);

                intDay = new Integer(strReturn).intValue();

                if ((intDay <= 0) || (intDay > 31))
                {
                    strReturn = "";
                }
                else
                {
                    strReturn = "" + intDay;
                }

                if ((intDay < 10) && (strReturn != ""))
                {
                    strReturn = "0" + strReturn;
                }
            }
        }
        return strReturn;
    }

    /**
     * 字符串 转换成HTML格式
     * @param    strInValue：传入字符串
     * @param strInValue
     * @return   strOutValue:传入字符串
     */
    public static String toHTMLFormat(String strInValue)
    {
        String strOutValue = "";
        char c;

        for (int i = 0; i < strInValue.length(); i++)
        {
            c = strInValue.charAt(i);
            switch (c)
            {
                case '<':
                    strOutValue += "&lt;";
                    break;
                case '>':
                    strOutValue += "&gt;";
                    break;
                case '\n':
                    strOutValue += "<br>";
                    break;
                case '\r':
                    break;
                case ' ':
                    strOutValue += "&nbsp;";
                    break;
                default:
                    strOutValue += c;
                    break;
            }
        }
        return strOutValue;
    }

    /**
     * 字符串打包
     * @param    strInValue：传入字符串
     * @param strInValue
     * @return   strOutValue:传入字符串
     */
    public static String encode(String strInValue)
    {
        String strOutValue = "";
        char c;

        for (int i = 0; i < strInValue.length(); i++)
        {
            c = strInValue.charAt(i);
            switch (c)
            {
                case ':': //hardcode 同Common.js中 NAMEVALUEDELIMITER   //域名与域值的分隔符
                    strOutValue += "："; //hardcode
                    break;
                case '|': //hardcode 同Common.js中 FIELDDELIMITER       //域之间的分隔符
                    strOutValue += "┃";
                    break;
                case '\n':
                    strOutValue += "\\n";
                    break;
                case '\r':
                    strOutValue += "\\r";
                    break;
                case '\"':
                    strOutValue += "\\\"";
                    break;
                case '\'':
                    strOutValue += "\\\'";
                    break;
                case '\b':
                    strOutValue += "\\b";
                    break;
                case '\t':
                    strOutValue += "\\t";
                    break;
                case '\f':
                    strOutValue += "\\f";
                    break;
                case '\\':
                    strOutValue += "\\\\";
                    break;
                case '<':
                    strOutValue += "\\<";
                    break;
                case '>':
                    strOutValue += "\\>";
                    break;
                default:
                    strOutValue += c;
                    break;
            }
        }
        return strOutValue;
    }

    /**
     * 生成给定长度的字符串
     * @param intLength 字符串长度
     * @return 字符串
     */
    public static String space(int intLength)
    {
        StringBuffer strReturn = new StringBuffer();
        for (int i = 0; i < intLength; i++)
        {
            strReturn.append(" ");
        }
        return new String(strReturn);
    }

    /**
     * 得到转化为字节数组的长度
     * @param strSource:原串
     * @param strSource
     * @return 长度
     */
    public static int getLength(String strSource)
    {
        return strSource.getBytes().length;
    }

    /**
     * 复制文件
     * @param fromFile 源文件
     * @param toFile   目的文件@throws java.io.FileNotFoundException
     * @throws java.io.IOException
     * @throws java.lang.Exception
     */
    public static void copyFile(String fromFile, String toFile) throws
            FileNotFoundException, IOException, Exception
            {
            FileInputStream in = new FileInputStream(fromFile);
            FileOutputStream out = new FileOutputStream(toFile);
            byte b[] = new byte[1024];
            int len; while ((len = in.read(b)) != -1)
    {
        out.write(b, 0, len);
    }
    out.close();
            in.close();
    }
            /**
             * 数字大写
             * @param    dblInValue：传入数字
             * @param intValue
             * @return   strOutValue:传出字符串
             */
            public static String toUpper(int intValue)
    {
        String strOutValue = "";
        String[] strTemp =
                           {
                           "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        try
        {
            strOutValue = strTemp[intValue];
        }
        catch (Exception ex)
        {
            strOutValue = "";
        }
        return strOutValue;
    }

    /**
     * 得到单位
     * @param    dblInValue：传入数字
     * @param intValue
     * @return   strOutValue:传出字符串
     */
    public static String getUnit(int intValue)
    {
        String strOutValue = "";
        String[] strTemp =
                           {
                           "仟", "佰", "拾", "亿", "仟", "佰", "拾", "万", "仟", "佰",
                           "拾", "", "", ""};
        try
        {
            strOutValue = strTemp[intValue];
        }
        catch (Exception ex)
        {
            strOutValue = "";
        }
        return strOutValue;
    }

    /**
     * 数字转换为大写
     * @param    dblInValue：传入数字
     * @param dblInValue
     * @return   strOutValue:传出字符串
     */
    public static String toChinese(double dblInValue)
    {
        String strOutValue = "";
        String strValue = new DecimalFormat("0").format(dblInValue * 100);
        String strTemp = "                 ";
        String strThat = "";
        int i = 0;
        int j = 0;
        int k = 0;

        k = strValue.length();
        if (k > 14)
        {
            return "";
        }

        strValue = strTemp.substring(0, 14 - k) + strValue;

        for (i = 14 - k; i < 14; i++)
        {

            j = new Integer(strValue.substring(i, i + 1)).intValue();
            if (j > 0)
            {
                strOutValue = strOutValue + strThat + toUpper(j) + getUnit(i);
                strThat = "";
            }
            else
            {
                if (i == 11)
                {
                    strOutValue = strOutValue + getUnit(i);
                }

                if (i == 7 && !strValue.substring(4, 8).equals("0000"))
                {
                    strOutValue = strOutValue + getUnit(i);
                }

                if (i == 3 && !strValue.substring(0, 4).equals("0000"))
                {
                    strOutValue = strOutValue + getUnit(i);
                }

                if (i < 11)
                {
                    strThat = toUpper(0);
                }

                if (i == 12)
                {
                    strThat = toUpper(0);
                }
            }
        }
        return strOutValue;
    }

    /**
     * 数字转换为大写
     * @param    intInValue：传入数字
     * @param intInValue
     * @return   strOutValue:传出字符串
     */
    public static String toChinese(int intInValue)
    {
        return toChinese((double) intInValue);
    }

    /**
     * 数字转换为大写
     * @param    longInValue：传入数字
     * @param longInValue
     * @return   strOutValue:传出字符串
     */
    public static String toChinese(long longInValue)
    {
        return toChinese((double) longInValue);
    }

    /**
     * 计算字符串出现的次数
     * @param strMain：原串，strFind：查找字符串
     * @param strMain
     * @param strFind
     * @return 返回字符串出现的次数
     */
    public static int getTimes(String strMain, String strFind)
    {
        String strReturn = "";
        int intStartIndex = 0,
                            intEndIndex = 0;
        int intTimes = 0;

        if (strMain == null || strMain.equals(""))
        {
            return intTimes;
        }

        while ((intEndIndex = strMain.indexOf(strFind, intStartIndex)) > -1)
        {
            intTimes = intTimes + 1;
            intStartIndex = intEndIndex + strFind.length();
        }
        return intTimes;
    }

    /**
     * @param str
     * @return boolean
     */
    public static boolean isEmpty(String str)
    {
        if (str == null || str.length() == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}

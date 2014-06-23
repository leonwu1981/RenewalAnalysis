//Source file: D:\\ireport\\src\\com\\sinosoft\\ireport\\util\\StringUtility.java

package com.sinosoft.xreport.util;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
//import com.sinosoft.xreport.table.db.*;
//import com.sinosoft.xreport.table.schema.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * <p>Title: ireport Java Report Solution</p>
 * <p>Description: ireport is a report solution based on J2EE architecture.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft Inc.</p>
 * @author Yang Yalin
 * @version 1.0
 * 常用字符串处理函数
 * <br> 1.从F1格式取行列  e.g.B5->(5,2)
 * <br> 2.从行列号得到F1格式 e.g. (5,2)->B5
 */
public class StringUtility
{

    public StringUtility()
    {

    }

    /**
     * 将行列号转换为F1格式
     * <br /><b>caution 行列号从1开始</b>
     * @param row 行号
     * @param col 列号
     * @return F1格式,e.g. B5
     */
    public static String rowCol2Cell(int row, int col)
    {
        //changed 02/07/16, to accord with F1!
        row++;
        col++;

        int irow = col;
        int i, j;
        char[] c = new char[1];
        String s = "";

        while ((int) irow > 0)
        {
            i = (int) (irow) % 26;
            if (i == 0 && irow >= 26)
            {
                irow -= 26;
                i = 26;
            }
            c[0] = (char) ('A' + i - 1);
            s = new String(c) + s;
            irow = (int) (irow / 26);
        }
        return s + row;
    }

    /**
     * 从F1格式取行号 e.g. B5->5
     * @param cell F1单元格号
     * @return 行号
     */
    public static int Cell2Row(String cell)
    {
        String cellCap = cell.toUpperCase();
        int i = cellCap.length();
        char c;
        while (--i >= 0)
        {
            c = cellCap.charAt(i);
            if (!(c >= '0' && c <= '9'))
            {
                break;
            }
        }
        return Integer.parseInt(cellCap.substring(i + 1)) - 1; //changed 02/07/16, to accord with F1!
    }

    /**
     * 从F1格式取列号 e.g. AB5->28
     * @param cell F1单元格号
     * @return 列号
     */
    public static int Cell2Col(String cell)
    {
        String cellCap = cell.toUpperCase();
        int i = -1;
        char c;
        int j = 0;
        while (++i < cellCap.length())
        {
            c = cellCap.charAt(i);
            if (!(c >= 'A' && c <= 'Z'))
            {
                break;
            }
            j = (int) (j * 26 + c - 'A' + 1);
        }
        return j - 1; //changed 02/07/16, to accord with F1!
    }

    /**
     * 从F1格式取列号 e.g. AB5->AB
     * @param cell F1单元格号
     * @return 列号
     */
    public static String getCol(String cell)
    {
        String cellCap = cell.toUpperCase();
        int i = -1;
        char c;
        while (++i < cellCap.length())
        {
            c = cellCap.charAt(i);
            if (!(c >= 'A' && c <= 'Z'))
            {
                break;
            }
        }
        return cellCap.substring(0, i);
    }

    /**
     * 从F1格式取行号 e.g. AB5->5
     * @param cell F1单元格号
     * @return 列号
     */
    public static String getRow(String cell)
    {
        String cellCap = cell.toUpperCase();
        int i = -1;
        char c;
        while (++i < cellCap.length())
        {
            c = cellCap.charAt(i);
            if (!(c >= 'A' && c <= 'Z'))
            {
                break;
            }
        }
        return cellCap.substring(i);
    }


    /**
     * 字符串替换函数
     * @param strMain 原串
     * @param strFind 查找字符串
     * @param strReplaceWith 替换字符串
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
     * 字符串替换函数(只替换整个单词)
     * @param strMain 原串
     * @param strFind 查找字符串
     * @param strReplaceWith 替换字符串
     * @return 替换后的字符串，如果原串为空或者为""，则返回""
     */
    public static String replaceWord(String strMain, String strFind,
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
            if (intEndIndex == 0 ||
                intEndIndex + strFind.length() == strMain.length() ||
                !StringUtility.isCharacter(strMain.substring(intEndIndex - 1,
                    intEndIndex)) &&
                !StringUtility.isCharacter(strMain.substring(intEndIndex +
                    strFind.length(), intEndIndex + strFind.length() + 1)))
            {
                strReturn = strReturn +
                            strMain.substring(intStartIndex, intEndIndex) +
                            strReplaceWith;
                intStartIndex = intEndIndex + strFind.length();
            }
            else
            {
                strReturn = strReturn +
                            strMain.substring(intStartIndex, intEndIndex) +
                            strFind;
                intStartIndex = intEndIndex + strFind.length();
            }
        }
        strReturn = strReturn + strMain.substring(intStartIndex, strMain.length());
        return strReturn;
    }

    /**
     * 判断operator是否运算符
     * @param operator
     * @return boolean
     */
    public static boolean isOperator(char operator)
    {
        if (operator == '+' ||
            operator == '-' ||
            operator == '*' ||
            operator == '/' ||
            operator == '$' ||
            operator == '@')
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 检查表达式是否正确
     * @param strExpression
     * @return boolean
     */
    public static boolean check(String strExpression)
    {
        boolean flag = true;
        int intLeft = 0;
        int intRight = 0;
        //左右括号个数相等
        for (int i = 0; i < strExpression.length(); i++)
        {
            char ch = strExpression.charAt(i);
            if (ch == '(')
            {
                intLeft++;
            }
            if (ch == ')')
            {
                intRight++;
            }
        }
        if (intLeft != intRight)
        {
            return false;
        }

        for (int i = 0; i < strExpression.length(); i++)
        {
            char ch = strExpression.charAt(i);
            if (ch == '(')
            {
                //左括号的左边只能是null、(、op
                if (i != 0 && strExpression.charAt(i - 1) != '(' &&
                    !isOperator(strExpression.charAt(i - 1)))
                {
                    return false;
                }
                //左括号的右边不能是null、)、op
                if (i == strExpression.length() - 1 ||
                    isOperator(strExpression.charAt(i + 1)) ||
                    strExpression.charAt(i + 1) == ')')
                {
                    return false;
                }
            }
            if (ch == ')')
            {
                //右括号的左边不能是null、(、op
                if (i == 0 || isOperator(strExpression.charAt(i - 1)) ||
                    strExpression.charAt(i - 1) == '(')
                {
                    return false;
                }
                //右括号的右边只能是null、)、op
                if (i != strExpression.length() - 1 &&
                    !isOperator(strExpression.charAt(i + 1)) &&
                    strExpression.charAt(i + 1) != ')')
                {
                    return false;
                }
            }
            if (isOperator(ch))
            {
                //操作符左边不能是null、(、op
                if (i == 0 || strExpression.charAt(i - 1) == '(' ||
                    isOperator(strExpression.charAt(i - 1)))
                {
                    return false;
                }
                //操作符的右边不能是null、)、op
                if (i == strExpression.length() - 1 ||
                    strExpression.charAt(i + 1) == ')' ||
                    isOperator(strExpression.charAt(i + 1)))
                {
                    return false;
                }
            }
        }
        return flag;
    }

    /**
     * @param strURL
     * @return String
     */
    public static String readJSP(String strURL)
    {
        URL url = null;
        URLConnection uc = null;
        InputStream is = null;
        String strTemp = "";
        byte[] b;
        int i;

        try
        {
            url = new URL(strURL);
            uc = url.openConnection();
            is = uc.getInputStream();
            b = new byte[4096];
            while ((i = is.read(b)) >= 0)
            {
                strTemp = strTemp + new String(b, 0, i);
            }
            is.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return strTemp;
    }

    /**
     * @param strURL
     */
    public static void WriteJSP(String strURL)
    {
        URL url = null;
        URLConnection uc = null;
        InputStream is = null;
        String strTemp = "";
        byte[] b;
        int i;

        try
        {
            url = new URL(strURL);
            uc = url.openConnection();
            is = uc.getInputStream();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 将向量中的内容拼制成字符串，以“/”为分隔
     * @param vector
     * @return String
     */
    public static String vectorTostring(java.util.Vector vector)
    {
        String str;
        int i;
        str = "";
        for (i = 0; i < vector.size(); i++)
        {
            str = str + SysConfig.SEPARATORITEM + vector.get(i).toString();
        }
        str = str + SysConfig.SEPARATORITEM;
        return str;
    }

    /**
     * 将以“/”为分隔的字符串拆分成向量
     * @param str
     * @return Vector
     */
    public static Vector stringTovector(String str)
    {
        int indexStart, indexEnd;
        Vector vector = new Vector();

        while (str.indexOf(SysConfig.SEPARATORITEM) != -1)
        {
            if (!str.equals(""))
            {
                indexStart = str.indexOf(SysConfig.SEPARATORITEM);
                str = str.substring(indexStart + 1);
                indexEnd = str.indexOf(SysConfig.SEPARATORITEM);
                if (indexEnd != -1)
                {
                    vector.add(str.substring(indexStart, indexEnd));
                    str = str.substring(indexEnd);
                }
            }
            else
            {
                indexStart = -1;
            }
        }
        return vector;
    }

    /**
     * 将三个字符串拼制成一个字符串中间用“|”隔开
     * @param str1
     * @param str2
     * @return String
     */
    public static String stringTostringSet(String str1, String str2)
    {
        String strSet;
        strSet = "";
        strSet = str1 + SysConfig.SEPARATORONE + str2;
        return strSet;
    }

    /**
     * 将以“|”为分隔符的字符串拆分为三个字符串
     * @param strSet
     * @return String[]
     */
    public static String[] stringSetTostring(String strSet)
    {
        int separator;
        String str[] = new String[2];
        str[0] = new String();
        str[1] = new String();
        separator = strSet.indexOf(SysConfig.SEPARATORONE);
        if (separator >= 1)
        {
            str[0] = strSet.substring(0, separator);
        }
        str[1] = strSet.substring(separator + 1, strSet.length());
        return str;
    }

    /**
     * 判断字符串是否符合项目单元格的格式
     * @param strCellItems
     * @return boolean
     */
    public static boolean isCellItems(String strCellItems)
    {
        if (strCellItems.equals(""))
        {
            return false;
        }
        if (strCellItems.indexOf(SysConfig.SEPARATORONE) != 0
            && !StringUtility.isCellData(strCellItems))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 判断字符串是否符合数据单元格的格式
     * @param strCellData
     * @return boolean
     */
    public static boolean isCellData(String strCellData)
    {
        if (strCellData.equals(""))
        {
            return false;
        }
        if (strCellData.indexOf(SysConfig.HEADOFDSTABLE) != -1 ||
            strCellData.indexOf(SysConfig.HEADOFDSREPORT) != -1 ||
            strCellData.indexOf(SysConfig.HEADOFFUNCTION) != -1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 判断字符串是否符合要素的形式
     * @param strItem
     * @return boolean
     */
    public static boolean isItem(String strItem)
    {
        if (strItem.equals(""))
        {
            return false;
        }
        if (strItem.indexOf(SysConfig.SEPARATORITEM) == -1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 判断字符串是否符合要素组的格式
     * @param strItems
     * @return boolean
     */
    public static boolean isItems(String strItems)
    {
        if (strItems.equals(""))
        {
            return false;
        }
        if (strItems.indexOf(SysConfig.SEPARATORITEM) >= 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 获得校验规则的校验公式
     * @param strCheckRule 校验规则
     * @return 校验公式
     */
    public static String getCheckFormula(String strCheckRule)
    {
        String strReturn = "";
        int intSep = 0;
        if (strCheckRule.equals(""))
        {
            return "";
        }
        intSep = strCheckRule.indexOf(SysConfig.SEPARATORERROR);
        if (intSep == -1)
        {
            return "";
        }
        strReturn = strCheckRule.substring(0, intSep);
        return strReturn;
    }

    /**
     * 获得校验规则的错误信息
     * @param strCheckRule 校验规则
     * @return 校验公式
     */
    public static String getErrorInfo(String strCheckRule)
    {
        String strReturn = "";
        int intSep = 0;
        if (strCheckRule.equals(""))
        {
            return "";
        }
        intSep = strCheckRule.indexOf(SysConfig.SEPARATORERROR);
        if (intSep == -1)
        {
            return "";
        }
        strReturn = strCheckRule.substring(intSep + 1);
        return strReturn;
    }

    /**
     * 拆分多条校验规则
     * @param strCheckRule 校验公式或错误信息
     * @return Vector
     */
    public static Vector getCheckRule(String strCheckRule)
    {
        Vector vecReturn = new Vector();
        if (strCheckRule.equals(""))
        {
            return vecReturn;
        }
        StringTokenizer strToken = new StringTokenizer(strCheckRule,
                SysConfig.SEPARATORCHECK);
        while (strToken.hasMoreTokens())
        {
            vecReturn.add(strToken.nextElement());
        }
        return vecReturn;
    }

    /**
     * 组合多条校验规则
     * @param vecCheckRule
     * @return 校验规则
     */
    public static String setCheckRule(java.util.Vector vecCheckRule)
    {
        String strReturn = "";
        if (vecCheckRule.size() == 0)
        {
            return strReturn;
        }
        for (int i = 0; i < vecCheckRule.size(); i++)
        {
            strReturn = strReturn + "&" + vecCheckRule.elementAt(i).toString();
        }
        return strReturn.substring(1);
    }

    /**
     * 后台字符串－>前台字符串
     * @param strCellTxt
     * @return String
     */
    public static String convert(String strCellTxt)
    {
        String strReturn = "";
        int intStart = strCellTxt.indexOf("<DSReport>");
        int intEnd = strCellTxt.indexOf("</DSReport>", intStart);
        while (intStart != -1 && intEnd != -1)
        {
            strReturn = strReturn + strCellTxt.substring(0, intStart);
            strReturn = strReturn + strCellTxt.substring(intStart + 10, intEnd);
            strCellTxt = strCellTxt.substring(intEnd + 11);
            intStart = strCellTxt.indexOf("<DSReport>");
            intEnd = strCellTxt.indexOf("</DSReport>", intStart);
        }
        return strReturn;
    }

    /**
     * 转化XML字符串(防止解析特殊字符错误 '<'、'>'、'&'、'"')
     * @param strXML XML字符串
     * @return XML字符串
     */
    public static String convertXML(String strXML)
    {
        String strReturn = "";
        StringBuffer sb = new StringBuffer();
        if (strXML == null)
        {
            return strReturn;
        }
        char[] ch = strXML.toCharArray();
        int intLen = ch.length;
        for (int i = 0; i < intLen; i++)
        {
            switch (ch[i])
            {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\t':
                case '\r':
                case '\n':
                    break;
                default:
                    sb.append(ch[i]);
            }
        }
        return sb.toString();
    }

    /**
     * segment_define.segment_namec处理函数
     * 去掉最后的半个字符,替换/为"－",并转换为GBK编码.
     * caution:iso编码汉字为2字节,gbk汉字为1字节.what a pain!
     * @param in 输入的segment_name
     * @param maxLen 最大长度,byte数
     * @return 处理结果
     */
    public static String dealSegmentName(String in, int maxLen)
    {
        //先将"/"转换成ISO
        String bias = Str.GBKToUnicode("/");

        try
        {
//        if (in.length()>=maxLen/2)  error
            if (in.length() >= maxLen) //iso编码,String.length()就是字节数
            {
                if (in.indexOf(bias) > 0) //长度满足,又有斜线
                {
                    String s = new String(in.getBytes("ISO8859_1"), 0,
                                          maxLen - 1, "GBK");
                    return Str.replace(s, "/", "－");
                }
                else //没有斜线
                {
                    return Str.GBKToUnicode(in);
                }
            }
            else //没有截掉半个字符的可能,需要替换斜线,为全角"－"
            {
                return Str.replace(Str.unicodeToGBK(in), "/", "－");
            }

//          String result=
        }
        catch (Exception ex)
        {
            return in;
        }
    }

    /**
     * 打印Map
     * @param map
     */
    public static void listMap(Map map)
    {
        Logger logger = XTLogger.getLogger(StringUtility.class);
        logger.debug("\n--------map content-----------");
        if (map == null)
        {
            logger.debug("null");
            return;
        }

        Iterator it = map.keySet().iterator();
        while (it.hasNext())
        {
            Object o = it.next();
            logger.debug("\t" + o + ":" + map.get(o));
        }
        logger.debug("--------------------end----------------");
    }

    /**
     * 时间处理函数.根据给定结束时间,报表类型报表周期,时间格式,得到报表开始,结束时间.
     * 这个函数主要解决报表开始,结束时间的算法.支持任意时间格式,任意周期长度,
     * 支持日报,月报,季报,年报.
     * 时间格式可以为"yyyyMMdd","yyyy-MM-dd","yyyy/MM/dd"等
     * 注意:月的模式为M,非m!!!
     * @param endTime 给定参考时间,应当为sourcePattern确定的形式
     * @param reportType 报表类型, 1-日报,2-月报,3-季报,4-年报,5-周报(unimplemented)
     * @param reportCycle 报表周期,从1开始,2+表示开始时间往前移相应时间
     * @param sourcePattern 用户的时间形式; 注意月份是M,m是分的模式
     * @param targetPattern 数据库要求的形式
     * @return [0]-开始时间;[1]-结束时间
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public static String[] parseTime(String endTime, int reportType,
                                     int reportCycle, String sourcePattern,
                                     String targetPattern) throws
            ParseException
    {
        //当前时间形式,yyyy-MM-dd
//      String sourcePattern=getSessionPool().getConfig().getDefaultDateFormat();
        SimpleDateFormat sdf = new SimpleDateFormat(sourcePattern); //用户定义形式
        SimpleDateFormat targetDF = new SimpleDateFormat(targetPattern); //数据库要求形式

        String reportStartTime = endTime;
        String reportEndTime = endTime;

        String settedEnd = endTime;

        Date endDate = sdf.parse(settedEnd);
        Date startDate;

        //报表类型
        int type = reportType;
        //报表周期
        int cycle = reportCycle;

        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        switch (type)
        {
            case 1: //日报
                reportEndTime = targetDF.format(endDate);
                cal.add(Calendar.DATE, ( -1) * cycle + 1); //开始时间=结束时间-周期+1
                reportStartTime = targetDF.format(cal.getTime());
                break;
            case 2: //月报
                cal.set(Calendar.DAY_OF_MONTH, 1); //月初
                cal.add(Calendar.MONTH, ( -1) * cycle + 1); //到周期的月初
                reportStartTime = targetDF.format(cal.getTime()); //开始时间
                cal.add(Calendar.MONTH, cycle); //周期末的下月初
                cal.add(Calendar.DAY_OF_MONTH, -1); //周期末
                reportEndTime = targetDF.format(cal.getTime());
                break;
            case 3: //季报
                int month = cal.get(Calendar.MONTH) + 1; //月度,需要+1
                int quarter = (int) month / 3; //所在季度,0-spring;1-summer
                cal.set(Calendar.MONTH, quarter * 3 + (( -1) * cycle + 1) * 3); //季初-周期持续时间
                cal.set(Calendar.DATE, 1); //设到1号
                reportStartTime = targetDF.format(cal.getTime());

                ////////////////////end time
                cal.add(Calendar.MONTH, cycle * 3); //多了一天
                cal.add(Calendar.DATE, -1);
                reportEndTime = targetDF.format(cal.getTime());
                break;
            case 4: //年报
                cal.add(Calendar.YEAR, ( -1) * cycle + 1);
                cal.set(Calendar.MONTH, Calendar.JANUARY);
                cal.set(Calendar.DATE, 1); //设到年初
                reportStartTime = targetDF.format(cal.getTime());

                /////////
                cal.add(Calendar.YEAR, cycle); //下一年元旦,多了一天
                cal.add(Calendar.DATE, -1); //年末
                reportEndTime = targetDF.format(cal.getTime());
                break;
            default:
                break;
        }

        String[] result = new String[2];
        result[0] = reportStartTime;
        result[1] = reportEndTime;
        return result;
    }

    public static String getMonthStart(String strDate)
    {
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(strDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            return sdf.format(cal.getTime());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public static String getMonthEnd(String strDate)
    {
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(strDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.MONTH, 1);
            cal.set(Calendar.DAY_OF_MONTH, 0);
            return sdf.format(cal.getTime());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFirstDay(String strDate)
    {
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(strDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.YEAR, 0);
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.DATE, 1);
            return sdf.format(cal.getTime());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 在字符串中找出关键字所在位置.
     * 要求关键字左右都是非字母.
     * @param content 被查找的字符串
     * @param key 要查找的字符串
     * @return 要查找字符串的位置
     */
    public static int keyPos(String content, String key)
    {
        String all =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";
        int idx = 0;
        int oldIdx = 0;
        while (true)
        {
            idx = content.indexOf(key, oldIdx);
            if (idx < 0)
            {
                return -1;
            }
            if (idx == 0)
            {
                if (content.length() == key.length())
                {
                    return 0;
                }
                else //判断右侧是否是字母和数字
                {
                    String rightChara = content.substring(key.length(),
                            key.length() + 1);
                    if (all.indexOf(rightChara) < 0)
                    {
                        return idx;
                    }
                }
            }
            else
            {
                String leftChar = content.substring(idx - 1, idx);
                boolean leftCorrect = (all.indexOf(leftChar) < 0) ? true : false;
                if (leftCorrect)
                {
                    int rightPos = idx + key.length();
                    if (rightPos >= content.length()) //以关键字结束,满足
                    {
                        return idx;
                    }
                    else
                    {
                        String rightChar = content.substring(rightPos,
                                rightPos + 1);
                        if (all.indexOf(rightChar) < 0)
                        {
                            return idx;
                        }
                    }
                }
            }

            oldIdx = idx + key.length();
        }
    }

    /**
     * 是否是字母或数字
     * @param ch
     * @return
     */
    public static boolean isCharacter(String ch)
    {
        if (ch.length() > 1 || ch.length() < 1)
        {
            return false;
        }
        String all =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_"; //yang add "_" on 2003.04.01
        return (all.indexOf(ch) >= 0);
    }

    /**
     * 得到左边相邻第一个单词
     * e.g.由表名.字段 得到表名
     * @param content
     * @param pos
     * @return
     */
    public static String getLeftWord(String content, int pos)
    {
        for (int startPos = pos - 1; startPos > -1; startPos--)
        {
            String ch = content.substring(startPos, startPos + 1);
            if (!isCharacter(ch))
            {
                return content.substring(startPos + 1, pos);
            }
        }
        return content.substring(0, pos);
    }

    /**
     * 得到右边第一个单词
     * e.g.由表名.字段 得到字段
     * @param content
     * @param pos
     * @return
     */
    public static String getRightWord(String content, int pos)
    {
        for (int endPos = pos + 1; endPos < content.length(); endPos++)
        {
            String ch = content.substring(endPos, endPos + 1);
            if (!isCharacter(ch))
            {
                return content.substring(pos + 1, endPos);
            }
        }
        return content.substring(pos + 1);
    }

    /**
     * 日期转换函数,将日期格式由sourcePattern指定的转换为目标格式
     * @param source 日期
     * @param sourcePattern 源格式
     * @param targetPattern 目标格式
     * @return 转换后的日期
     * @throws Exception 转换时错误
     */
    public static String convertDate(String source, String sourcePattern,
                                     String targetPattern) throws Exception
    {

        return parseTime(source, 1, 1, sourcePattern, targetPattern)[1];

    }


    /**
     * 去掉所有空白字符.
     * @param content 待去的字符串
     * @return 去掉后的结果
     */
    public static String trimAll(String content)
    {
        if (null == content || "".equals(content))
        {
            return "";
        }

        char c[] = content.toCharArray();

        char target[] = new char[c.length];

        int length = 0;
        for (int i = 0; i < c.length; i++)
        {
            //it's not a whiteSpace
            if (!isWhiteSpace(c[i]))
            {
                target[length++] = c[i];
            }
        }

        return new String(target, 0, length);

    }

    /**
     * 判断是否是空白字符
     * @param c 字符
     * @return true-是,false-fou
     */
    public static boolean isWhiteSpace(char c)
    {
        int j;
        char[] whiteSpace =
                            {
                            ' ', '\t', '\n'};
        for (j = 0; j < whiteSpace.length && c != whiteSpace[j]; j++)
        {}
        //no,it's not a whiteSpace
        if (j >= whiteSpace.length)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * 判断字符串是否是全为数字
     * @param txt 字符串
     * @return ture/false
     */
    public static boolean isNumber(String txt)
    {
        try
        {
            Double.parseDouble(txt);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }

    }

    /**
     * 测试
     * @param args 控制台参数@throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception
    {
        System.out.println(StringUtility.getFirstDay("2004-04-01"));
//       String strMain="boy";
//       String strFind="boy";
//       String strReplace="b";
//       System.out.println(StringUtility.replaceWord(strMain,strFind,strReplace));
    }
}
/*
 StringUtility.isNumber(String){
      if(ch==null)
        return false;
      String all="0123456789.,";
      String s=ch.trim();
      if(s.startsWith("-"))
      {
        s=s.substring(1);
      }
      if(s.length()<1)
      {
        return false;
      }
      for(int i=0;i<s.length();i++)
      {
        String c=s.substring(i,i+1);
        if (all.indexOf(c)<0)
        {
          return false;
        }
      }
      return true;
    }
 */

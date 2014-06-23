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
 * �����ַ���������
 * <br> 1.��F1��ʽȡ����  e.g.B5->(5,2)
 * <br> 2.�����кŵõ�F1��ʽ e.g. (5,2)->B5
 */
public class StringUtility
{

    public StringUtility()
    {

    }

    /**
     * �����к�ת��ΪF1��ʽ
     * <br /><b>caution ���кŴ�1��ʼ</b>
     * @param row �к�
     * @param col �к�
     * @return F1��ʽ,e.g. B5
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
     * ��F1��ʽȡ�к� e.g. B5->5
     * @param cell F1��Ԫ���
     * @return �к�
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
     * ��F1��ʽȡ�к� e.g. AB5->28
     * @param cell F1��Ԫ���
     * @return �к�
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
     * ��F1��ʽȡ�к� e.g. AB5->AB
     * @param cell F1��Ԫ���
     * @return �к�
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
     * ��F1��ʽȡ�к� e.g. AB5->5
     * @param cell F1��Ԫ���
     * @return �к�
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
     * �ַ����滻����
     * @param strMain ԭ��
     * @param strFind �����ַ���
     * @param strReplaceWith �滻�ַ���
     * @return �滻����ַ��������ԭ��Ϊ�ջ���Ϊ""���򷵻�""
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
     * �ַ����滻����(ֻ�滻��������)
     * @param strMain ԭ��
     * @param strFind �����ַ���
     * @param strReplaceWith �滻�ַ���
     * @return �滻����ַ��������ԭ��Ϊ�ջ���Ϊ""���򷵻�""
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
     * �ж�operator�Ƿ������
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
     * �����ʽ�Ƿ���ȷ
     * @param strExpression
     * @return boolean
     */
    public static boolean check(String strExpression)
    {
        boolean flag = true;
        int intLeft = 0;
        int intRight = 0;
        //�������Ÿ������
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
                //�����ŵ����ֻ����null��(��op
                if (i != 0 && strExpression.charAt(i - 1) != '(' &&
                    !isOperator(strExpression.charAt(i - 1)))
                {
                    return false;
                }
                //�����ŵ��ұ߲�����null��)��op
                if (i == strExpression.length() - 1 ||
                    isOperator(strExpression.charAt(i + 1)) ||
                    strExpression.charAt(i + 1) == ')')
                {
                    return false;
                }
            }
            if (ch == ')')
            {
                //�����ŵ���߲�����null��(��op
                if (i == 0 || isOperator(strExpression.charAt(i - 1)) ||
                    strExpression.charAt(i - 1) == '(')
                {
                    return false;
                }
                //�����ŵ��ұ�ֻ����null��)��op
                if (i != strExpression.length() - 1 &&
                    !isOperator(strExpression.charAt(i + 1)) &&
                    strExpression.charAt(i + 1) != ')')
                {
                    return false;
                }
            }
            if (isOperator(ch))
            {
                //��������߲�����null��(��op
                if (i == 0 || strExpression.charAt(i - 1) == '(' ||
                    isOperator(strExpression.charAt(i - 1)))
                {
                    return false;
                }
                //���������ұ߲�����null��)��op
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
     * �������е�����ƴ�Ƴ��ַ������ԡ�/��Ϊ�ָ�
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
     * ���ԡ�/��Ϊ�ָ����ַ�����ֳ�����
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
     * �������ַ���ƴ�Ƴ�һ���ַ����м��á�|������
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
     * ���ԡ�|��Ϊ�ָ������ַ������Ϊ�����ַ���
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
     * �ж��ַ����Ƿ������Ŀ��Ԫ��ĸ�ʽ
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
     * �ж��ַ����Ƿ�������ݵ�Ԫ��ĸ�ʽ
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
     * �ж��ַ����Ƿ����Ҫ�ص���ʽ
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
     * �ж��ַ����Ƿ����Ҫ����ĸ�ʽ
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
     * ���У������У�鹫ʽ
     * @param strCheckRule У�����
     * @return У�鹫ʽ
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
     * ���У�����Ĵ�����Ϣ
     * @param strCheckRule У�����
     * @return У�鹫ʽ
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
     * ��ֶ���У�����
     * @param strCheckRule У�鹫ʽ�������Ϣ
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
     * ��϶���У�����
     * @param vecCheckRule
     * @return У�����
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
     * ��̨�ַ�����>ǰ̨�ַ���
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
     * ת��XML�ַ���(��ֹ���������ַ����� '<'��'>'��'&'��'"')
     * @param strXML XML�ַ���
     * @return XML�ַ���
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
     * segment_define.segment_namec������
     * ȥ�����İ���ַ�,�滻/Ϊ"��",��ת��ΪGBK����.
     * caution:iso���뺺��Ϊ2�ֽ�,gbk����Ϊ1�ֽ�.what a pain!
     * @param in �����segment_name
     * @param maxLen ��󳤶�,byte��
     * @return ������
     */
    public static String dealSegmentName(String in, int maxLen)
    {
        //�Ƚ�"/"ת����ISO
        String bias = Str.GBKToUnicode("/");

        try
        {
//        if (in.length()>=maxLen/2)  error
            if (in.length() >= maxLen) //iso����,String.length()�����ֽ���
            {
                if (in.indexOf(bias) > 0) //��������,����б��
                {
                    String s = new String(in.getBytes("ISO8859_1"), 0,
                                          maxLen - 1, "GBK");
                    return Str.replace(s, "/", "��");
                }
                else //û��б��
                {
                    return Str.GBKToUnicode(in);
                }
            }
            else //û�нص�����ַ��Ŀ���,��Ҫ�滻б��,Ϊȫ��"��"
            {
                return Str.replace(Str.unicodeToGBK(in), "/", "��");
            }

//          String result=
        }
        catch (Exception ex)
        {
            return in;
        }
    }

    /**
     * ��ӡMap
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
     * ʱ�䴦����.���ݸ�������ʱ��,�������ͱ�������,ʱ���ʽ,�õ�����ʼ,����ʱ��.
     * ���������Ҫ�������ʼ,����ʱ����㷨.֧������ʱ���ʽ,�������ڳ���,
     * ֧���ձ�,�±�,����,�걨.
     * ʱ���ʽ����Ϊ"yyyyMMdd","yyyy-MM-dd","yyyy/MM/dd"��
     * ע��:�µ�ģʽΪM,��m!!!
     * @param endTime �����ο�ʱ��,Ӧ��ΪsourcePatternȷ������ʽ
     * @param reportType ��������, 1-�ձ�,2-�±�,3-����,4-�걨,5-�ܱ�(unimplemented)
     * @param reportCycle ��������,��1��ʼ,2+��ʾ��ʼʱ����ǰ����Ӧʱ��
     * @param sourcePattern �û���ʱ����ʽ; ע���·���M,m�Ƿֵ�ģʽ
     * @param targetPattern ���ݿ�Ҫ�����ʽ
     * @return [0]-��ʼʱ��;[1]-����ʱ��
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public static String[] parseTime(String endTime, int reportType,
                                     int reportCycle, String sourcePattern,
                                     String targetPattern) throws
            ParseException
    {
        //��ǰʱ����ʽ,yyyy-MM-dd
//      String sourcePattern=getSessionPool().getConfig().getDefaultDateFormat();
        SimpleDateFormat sdf = new SimpleDateFormat(sourcePattern); //�û�������ʽ
        SimpleDateFormat targetDF = new SimpleDateFormat(targetPattern); //���ݿ�Ҫ����ʽ

        String reportStartTime = endTime;
        String reportEndTime = endTime;

        String settedEnd = endTime;

        Date endDate = sdf.parse(settedEnd);
        Date startDate;

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
                int month = cal.get(Calendar.MONTH) + 1; //�¶�,��Ҫ+1
                int quarter = (int) month / 3; //���ڼ���,0-spring;1-summer
                cal.set(Calendar.MONTH, quarter * 3 + (( -1) * cycle + 1) * 3); //����-���ڳ���ʱ��
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
     * ���ַ������ҳ��ؼ�������λ��.
     * Ҫ��ؼ������Ҷ��Ƿ���ĸ.
     * @param content �����ҵ��ַ���
     * @param key Ҫ���ҵ��ַ���
     * @return Ҫ�����ַ�����λ��
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
                else //�ж��Ҳ��Ƿ�����ĸ������
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
                    if (rightPos >= content.length()) //�Թؼ��ֽ���,����
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
     * �Ƿ�����ĸ������
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
     * �õ�������ڵ�һ������
     * e.g.�ɱ���.�ֶ� �õ�����
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
     * �õ��ұߵ�һ������
     * e.g.�ɱ���.�ֶ� �õ��ֶ�
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
     * ����ת������,�����ڸ�ʽ��sourcePatternָ����ת��ΪĿ���ʽ
     * @param source ����
     * @param sourcePattern Դ��ʽ
     * @param targetPattern Ŀ���ʽ
     * @return ת���������
     * @throws Exception ת��ʱ����
     */
    public static String convertDate(String source, String sourcePattern,
                                     String targetPattern) throws Exception
    {

        return parseTime(source, 1, 1, sourcePattern, targetPattern)[1];

    }


    /**
     * ȥ�����пհ��ַ�.
     * @param content ��ȥ���ַ���
     * @return ȥ����Ľ��
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
     * �ж��Ƿ��ǿհ��ַ�
     * @param c �ַ�
     * @return true-��,false-fou
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
     * �ж��ַ����Ƿ���ȫΪ����
     * @param txt �ַ���
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
     * ����
     * @param args ����̨����@throws java.lang.Exception
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

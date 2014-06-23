//Source file: D:\\ireport\\src\\com\\sinosoft\\ireport\\util\\Str.java

/******************************************************************************************
 * �����ƣ�Str
 * ���������ַ�����ش�������
 * ��������ˣ�����
 * �����������: 2002-01-13
 ******************************************************************************************/
package com.sinosoft.xreport.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
/**
 * ����Str���������
 */
import java.util.Calendar;

/**
 * ����������ش�������
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
     * ���ַ�������ָ���ķָ��ַ����в�֣����ش�ָ����ŵķָ�����ǰһ���ָ���֮�����
     * ����
     * @param  strMain ���ַ���
     * @param  strDelimiters �ָ���
     * @param  intSerialNo �ָ������
     * @return ָ����ŵķָ�����ǰһ���ָ���֮����ַ���,���û���ҵ��򷵻�""
     */
    public static String decodeStr(String strMain, String strDelimiters,
                                   int intSerialNo)
    {
        int intIndex = 0; //�ָ������������ַ����е���ʼλ��
        int intCount = 0; //��ɨ�����ַ����Ĺ�����,�ڼ��������ָ����ַ���
        String strReturn = ""; //��Ϊ����ֵ���ַ���

        if (strMain.length() < strDelimiters.length())
        {
            return ""; //�����ַ����ȷָ�������Ҫ�̣��򷵻ؿ��ַ���
        }

        intIndex = strMain.indexOf(strDelimiters);
        if (intIndex == -1)
        {
            return ""; //�����ַ����в����ڷָ������򷵻ؿ��ַ���
        }

        while (intIndex != -1) //δ�ҵ��ָ���ʱ�˳�ѭ���������ؿ��ַ���
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
     * ���ַ�������ָ���ķָ��ַ����в�֣����ش�ָ����ŵķָ�����ǰһ���ָ���֮�����
     * ����
     * @param  strMain ���ַ���
     * @param  strDelimiters �ָ���
     * @param  intSerialNo �ָ������
     * @return ָ����ŵķָ�����ǰһ���ָ���֮����ַ���,���û���ҵ��򷵻�""
     */
    public static String decodeBuf(StringBuffer strMain, String strDelimiters,
                                   int intSerialNo)
    {
        String strReturn = "";
        char str[] = new char[100];
        int intIndex = 0; //�ָ������������ַ����е���ʼλ��
        int intCount = 0; //��ɨ�����ַ����Ĺ�����,�ڼ��������ָ����ַ���
        StringBuffer sb = new StringBuffer();

        if (strMain.length() < strDelimiters.length())
        {
            return ""; //�����ַ����ȷָ�������Ҫ�̣��򷵻ؿ��ַ���
        }

        intIndex = strMain.toString().indexOf(strDelimiters);
        if (intIndex == -1)
        {
            return ""; //�����ַ����в����ڷָ������򷵻ؿ��ַ���
        }

//    while (intIndex !=-1) //δ�ҵ��ָ���ʱ�˳�ѭ���������ؿ��ַ���
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
            //ƿ��begin
            strReturn = strMain.substring(0, intIndex);
//          strMain.getChars(0,intIndex,str,0);
//          strReturn=String.copyValueOf(str,0,intIndex);
            //ƿ��end
            strMain = strMain.delete(0, intIndex + 1);
            return strReturn;
        }
//      }
//    };
//    return "";
    }

    /**
     * ��ȡ�Ӵ��������г��� n �ε�λ��
     * @param strMain�����ַ���
     * @param strSub�����ַ���
     * @param intTimes�����ִ���
     * @param strMain
     * @param strSub
     * @param intTimes
     * @return  λ��ֵ������Ӵ���������û�г���ָ���������򷵻�-1
     */
    public static int getPos(String strMain, String strSub, int intTimes)
    {
        int intCounter = 0; //ѭ������
        int intPosition = 0; //λ�ü�¼
        int intLength = strSub.length(); //�Ӵ�����
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
     * ��ȡ��ָ��λ�ÿ�ʼ�Ӵ��������г��� n �ε�λ��
     * @param strMain�����ַ���
     * @param strSub�����ַ���
     * @param intStartIndex����ʼλ��
     * @param intTimes�����ִ���
     * @param strMain
     * @param strSub
     * @param intStartIndex
     * @param intTimes
     * @return  λ��ֵ,�������ʼλ�����Ӵ���������û�г���ָ������,�򷵻�-1
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
     * ���ַ���ת��ΪGBK�ַ���
     * @param   strOriginal:ԭ��
     * @param strOriginal
     * @return  ��ԭ����ISO8859_1(Unicode)����ת��ΪGBK����
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
     * ���ַ���ת��ΪUnicode�ַ���
     * @param strOriginal:ԭ��
     * @param strOriginal
     * @return  ��ԭ����GBK����ת��ΪISO8859_1(Unicode)����
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
     * ��������ַ���
     * @param   strMessage���ַ���
     * @param   intCount���������
     * @param   cl�����������
     * @return  ��ÿ������������ɶ�Ӧ����ʵ��,������Щʵ����Ϊ����Vector��Ԫ��
     * @throws  Exception
     * ʹ��ָ�����е�decode()������������ַ���
     * @param strMessage:�ַ�����intCount:���������cl:����decode()��������
     * @return  ��ÿ������������ɶ�Ӧ����ʵ��,������Щʵ����Ϊ��������
     * @throws  Exception �������decode�����������߽�����������׳��쳣
     * �ַ����滻����
     * @param strMain��ԭ����strFind�������ַ�����strReplaceWith���滻�ַ���
     * @param strMain
     * @param strFind
     * @param strReplaceWith
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
     * �ַ����滻����(ֻ�滻��һ�Σ�
     * @param strMain��ԭ����strFind�������ַ�����strReplaceWith���滻�ַ���
     * @param strMain
     * @param strFind
     * @param strReplaceWith
     * @return �滻����ַ��������ԭ��Ϊ�ջ���Ϊ""���򷵻�""
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
     * �ɸ��������ַ�����ȡ��ʽ����(��/��/��)
     * @param   strYear���꣬strMonth���£�strDay����
     * @param strYear
     * @param strMonth
     * @param strDay
     * @return  ��ʽ���ڣ���/��/��
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
     * ����ɸ�ʽ��ϵͳ����(��/��/��)
     * @param   ��
     * @return  ��ʽ���ڣ���/��/��
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
     * �ɸ��������ַ�����ȡ��ʽ����(��/��)
     * @param   strYear���꣬strMonth����
     * @param strYear
     * @param strMonth
     * @return  ��ʽ���ڣ���/��
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
     * ��ȡ����ֵ�е����
     * @param    strDate������
     * @param strDate
     * @return   ��
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
     * ��ȡϵͳ�����е����
     * @param    ��
     * @return   ��
     */
    public static String getYear()
    {
        String strReturn = "";
        int intYear = Calendar.getInstance().get(Calendar.YEAR);
        strReturn = "" + intYear;
        return strReturn;
    }

    /**
     * ��ȡ����ֵ�е��·�
     * @param    strDate������
     * @param strDate
     * @return   ��
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
     * ��ȡϵͳ�����е��·�
     * @param    strDate������
     * @return   ��
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
     * ��ȡ��������ֵ�е���
     * @param    strDate������
     * @param strDate
     * @return   ��
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
     * ��ȡϵͳ����ֵ�е���
     * @param    ��
     * @return   ��
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
     * ��ȡϵͳʱ��ֵ�е�Сʱ
     * @param    ��
     * @return   Сʱ
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
     * ��ȡϵͳʱ��ֵ�еķ���
     * @param    ��
     * @return   ����
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
     * ��ȡϵͳʱ��ֵ������
     * @param    ��
     * @return   ����
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
     * ��ȡ����ֵ�е����
     * @param    strDate������
     * @param strDate
     * @return   ��
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
     * ��ȡ��֤����ֵ�е��·�
     * @param    strDate������
     * @param strDate
     * @return   ��
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
     * ��ȡ��֤��������ֵ�е���
     * @param    strDate������
     * @param strDate
     * @return   ��
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
     * �ַ��� ת����HTML��ʽ
     * @param    strInValue�������ַ���
     * @param strInValue
     * @return   strOutValue:�����ַ���
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
     * �ַ������
     * @param    strInValue�������ַ���
     * @param strInValue
     * @return   strOutValue:�����ַ���
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
                case ':': //hardcode ͬCommon.js�� NAMEVALUEDELIMITER   //��������ֵ�ķָ���
                    strOutValue += "��"; //hardcode
                    break;
                case '|': //hardcode ͬCommon.js�� FIELDDELIMITER       //��֮��ķָ���
                    strOutValue += "��";
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
     * ���ɸ������ȵ��ַ���
     * @param intLength �ַ�������
     * @return �ַ���
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
     * �õ�ת��Ϊ�ֽ�����ĳ���
     * @param strSource:ԭ��
     * @param strSource
     * @return ����
     */
    public static int getLength(String strSource)
    {
        return strSource.getBytes().length;
    }

    /**
     * �����ļ�
     * @param fromFile Դ�ļ�
     * @param toFile   Ŀ���ļ�@throws java.io.FileNotFoundException
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
             * ���ִ�д
             * @param    dblInValue����������
             * @param intValue
             * @return   strOutValue:�����ַ���
             */
            public static String toUpper(int intValue)
    {
        String strOutValue = "";
        String[] strTemp =
                           {
                           "��", "Ҽ", "��", "��", "��", "��", "½", "��", "��", "��"};
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
     * �õ���λ
     * @param    dblInValue����������
     * @param intValue
     * @return   strOutValue:�����ַ���
     */
    public static String getUnit(int intValue)
    {
        String strOutValue = "";
        String[] strTemp =
                           {
                           "Ǫ", "��", "ʰ", "��", "Ǫ", "��", "ʰ", "��", "Ǫ", "��",
                           "ʰ", "", "", ""};
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
     * ����ת��Ϊ��д
     * @param    dblInValue����������
     * @param dblInValue
     * @return   strOutValue:�����ַ���
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
     * ����ת��Ϊ��д
     * @param    intInValue����������
     * @param intInValue
     * @return   strOutValue:�����ַ���
     */
    public static String toChinese(int intInValue)
    {
        return toChinese((double) intInValue);
    }

    /**
     * ����ת��Ϊ��д
     * @param    longInValue����������
     * @param longInValue
     * @return   strOutValue:�����ַ���
     */
    public static String toChinese(long longInValue)
    {
        return toChinese((double) longInValue);
    }

    /**
     * �����ַ������ֵĴ���
     * @param strMain��ԭ����strFind�������ַ���
     * @param strMain
     * @param strFind
     * @return �����ַ������ֵĴ���
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

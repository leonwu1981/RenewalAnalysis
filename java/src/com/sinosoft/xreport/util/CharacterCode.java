/**
 * Title:        characterCode
 * Description:  �ַ�����ת����
 * Copyright:    Copyright (c) 2002
 * Company:      sinosoft
 * @author       wuxiao
 * @version 1.0
 */
package com.sinosoft.xreport.util;

public class CharacterCode
{
    public CharacterCode()
    {
    }

    /**
     * ��GBK�����ַ���ת��ΪISO8859_1�����ַ���
     * @param strOriginal GBK�����ַ���
     * @return  ISO8859_1�����ַ���
     */
    public static String GBKToISO8859_1(String strOriginal)
    {
        if (!SysConfig.NEEDCONVERT)
        {
            return strOriginal;
        }
        if (strOriginal == null)
        {
            return "";
        }
        try
        {
            return new String(strOriginal.getBytes("GB2312"), "ISO8859_1");
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            return strOriginal;
        }
    }

    /**
     * ��ISO8859_1�����ַ���ת��ΪGBK�����ַ���
     * @param   strOriginal ISO8859_1�����ַ���
     * @return  GBK�����ַ���
     */
    public static String ISO8859_1ToGBK(String strOriginal)
    {
        if (!SysConfig.NEEDCONVERT)
        {
            return strOriginal;
        }
        if (strOriginal == null)
        {
            return "";
        }
        try
        {
            return new String(strOriginal.getBytes("ISO8859_1"), "GB2312");
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            return strOriginal;
        }
    }

    public static String toConvertStr(String original, String fromCode,
                                      String toCode)
    {
        if (original != null)
        {
            try
            {
                if (fromCode != null && toCode != null)
                {
                    return new String(original.getBytes(fromCode), toCode);
                }
                else
                {
                    return original;
                }
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
                return original;
            }
        }
        else
        {
            return null;
        }
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
}
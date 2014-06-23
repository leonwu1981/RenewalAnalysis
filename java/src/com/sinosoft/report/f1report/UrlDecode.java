/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.report.f1report;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class UrlDecode
{
    public static String urlEncode(String s)
    {
        byte[] ba = s.getBytes();
        final int caseDiff = ('a' - 'A');
        StringBuffer out = new StringBuffer(s.length());
        for (int j = 0; j < ba.length; j++)
        {
            out.append('%');
            char ch = forDigit((ba[j] >> 4) & 0xF, 16);
            if (!Character.isDigit(ch))
            {
                ch -= caseDiff;
            }
            out.append(ch);
            ch = forDigit(ba[j] & 0xF, 16);
            if (!Character.isDigit(ch))
            {
                ch -= caseDiff;
            }
            out.append(ch);
        }
        return out.toString();
    }

    private static char forDigit(int digit, int radix)
    {
        if ((digit >= radix) || (digit < 0))
        {
            return '\0';
        }
        if ((radix < 2) || (radix > 36))
        {
            return '\0';
        }
        if (digit < 10)
        {
            return (char) ('0' + digit);
        }
        return (char) ('a' - 10 + digit);
    }

    public static final String utfEncode(String myString)
    {
        try
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeUTF(myString);
            dos.close();
            byte[] bb = bos.toByteArray();
            StringBuffer buf = new StringBuffer();
            for (int i = 2; i < bb.length; i++)
            {
                buf.append("%" + Integer.toHexString(0xff & bb[i]).toUpperCase());
            }
            return buf.toString();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public static String GBK_TO_UTF8_URL(String sGBKUrl)
    {
        String sRtn = "";
        String s = "";
        String sTmp = "";
        if (sGBKUrl == null)
        {
            return null;
        }
        if (sGBKUrl.equals(""))
        {
            return "";
        }
        StringBuffer bf = new StringBuffer();
        for (int i = 0; i < sGBKUrl.length(); i++)
        {
            s = sGBKUrl.substring(i, i + 1);
            byte[] ba = s.getBytes();
            if (ba.length > 1)
            {

                sTmp = UrlDecode.utfEncode(s);

            }
            else
            {
                if (s.equals(" "))
                {
                    sTmp = "+";
                }
                else
                {
                    sTmp = s;
                }
            }

            bf.append(sTmp);
        }
        sRtn = bf.toString();
        return sRtn;
    }

    public UrlDecode()
    {

    }

    public static void main(String[] args)
    {
//        String ss = "hahaEÎÄÖÐ F E";
//        System.out.println(GBK_TO_UTF8_URL(ss));
    }
}

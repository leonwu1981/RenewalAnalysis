package com.sinosoft.report.f1report;

import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;

import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;
import java.util.GregorianCalendar;

public class JRptUtility
{
    /**
     * �ú����õ�c_Str�еĵ�c_i����c_Split�ָ���ַ���
     * @param c_Str ���봮
     * @param c_i ����λ��
     * @param c_Split �ָ��ַ���
     * @return ���صõ��Ĵ�
     */
    public static String Get_Str(String c_Str, int c_i, String c_Split)
    {
        String t_Str1 = "", t_Str2 = "", t_strOld = "";
        int i = 0, i_Start = 0, j_End = 0;
        t_Str1 = c_Str;
        t_Str2 = c_Split;
        i = 0;
        try
        {
            while (i < c_i)
            {
                i_Start = t_Str1.indexOf(t_Str2, 0);
                if (i_Start >= 0)
                {
                    i += 1;
                    t_strOld = t_Str1;
                    t_Str1 = t_Str1.substring(i_Start + t_Str2.length(),
                                              t_Str1.length());
                }
                else
                {
                    if (i != c_i - 1)
                    {
                        t_Str1 = "";
                    }
                    break;
                }
            }

            if (i_Start >= 0)
            {
                t_Str1 = t_strOld.substring(0, i_Start);
            }
        }
        catch (Exception ex)
        {
            t_Str1 = "";
        }
        return t_Str1;
    }

    /**
     * �õ���ǰ���ڵ��ַ���
     * @return ���ڵ��ַ���
     */
    public static String getDate()
    {
        java.util.Date tDate;
        tDate = new Date(System.currentTimeMillis());
        //�����ڲ�����ʱ�򣬽������Calendar����
        GregorianCalendar tCalendar = new GregorianCalendar();
        tCalendar.setTime(tDate);
        return String.valueOf(tCalendar.get(Calendar.YEAR)) + "-"
                + String.valueOf(tCalendar.get(Calendar.MONTH) + 1) + "-"
                + String.valueOf(tCalendar.get(Calendar.DATE));

//        return String.valueOf(tDate.getYear() + 1900) + "-"
//                + String.valueOf(tDate.getMonth() + 1) + "-"
//                + String.valueOf(tDate.getDate());

    }

    /**
     * �õ���ǰ���ں�ʱ����ַ���
     * @return ���ں�ʱ��
     */
    public static String getNow()
    {
        return getDate() + " " + getTime();
    }


    /**
     * �õ���ǰʱ����ַ���
     * @return ��ǰʱ��
     */
    public static String getTime()
    {
        java.util.Date tDate;
        tDate = new Date(System.currentTimeMillis());
        //�����ڲ�����ʱ�򣬽������Calendar����
        GregorianCalendar tCalendar = new GregorianCalendar();
        tCalendar.setTime(tDate);
        return String.valueOf(tCalendar.get(Calendar.HOUR)) + ":"
                + String.valueOf(tCalendar.get(Calendar.MINUTE)) + ":"
                + String.valueOf(tCalendar.get(Calendar.SECOND));
//        java.util.Date d;
//        d = Calendar.getInstance().getTime();
//        return String.valueOf(d.getHours()) + ":"
//                + String.valueOf(d.getMinutes()) + ":"
//                + String.valueOf(d.getSeconds());

    }

    /**
     * �����ֶη��صĽ�����д�����Ҫ��Null�Ĵ���
     * @param fd ���˵��ַ���
     * @return �ַ���
     */
    public static String ChgValue(String fd)
    {
        if (fd == null)
        {
            return "";
        }

        return fd.trim();
    }

    /**
     * �����ļ����õ��ļ��������������ַ���,�磺c:\temp\abc.vts���򷵻�abc
     * @param c_FileName �ļ���
     * @return �������������ļ����ַ���
     */
    public static String Get_OnlyFileNameEx(String c_FileName)
    {
        File f;
        f = new File(c_FileName);
        return f.getName().substring(0, f.getName().length() - 4);
    }

    /**
     * ���룺 cSqlΪ����SQL���
     * @param cSQL Ϊ����SQL���
     * @return ����ֵ
     */
    public static String GetOneValueBySQL(String cSQL)
    {
        SSRS tSSRS = new SSRS();
        ExeSQL tExeSQL = new ExeSQL();

        String tR = "";

        tSSRS = tExeSQL.execSQL(cSQL);
        tR = ChgValue(tSSRS.GetText(1, 1));
        // tR = JRptUtility.ChgValue(cConn.Execute(cSQL).Fields(0));
        return tR;
    }

    /**
     * ��ȡֵ
     * @param cVarString ���ʽ
     * @return doubleֵ
     */

    public static double GetValue(String cVarString)
    {
        double tR;
//        String tStr;
        String tCh, tOldOp;
        String tStr1 = "";
        int i, iMax, tIndex;
        System.out.println(" GetValue===== :" + cVarString);
        tR = 0.0;
        iMax = cVarString.length();
        tIndex = 0;
        tOldOp = "+";
        for (i = 0; i < iMax; i++)
        {
            tCh = cVarString.substring(i, i + 1);

            if (tCh.equals("+") || tCh.equals("-"))
            {
                tStr1 = cVarString.substring(tIndex, i);

                if (tStr1.equals(""))
                {
                    tStr1 = "0";
                }

                if (tOldOp.equals("+"))
                {
                    tR += Double.parseDouble(tStr1);
                }
                else
                {
                    tR -= Double.parseDouble(tStr1);
                }
                tOldOp = tCh;
                tIndex = i + 1;
            }
        }

        tStr1 = cVarString.substring(tIndex, i);
        if (tStr1.equals(""))
        {
            tStr1 = "0";
        }
        if (tOldOp.equals("+"))
        {
            tR += Double.parseDouble(tStr1);
        }
        else
        {
            tR -= Double.parseDouble(tStr1);
        }

        return tR;
    }

    /**
     * �ж��Ƿ�Ϊ����
     * @param sVar �����ַ�
     * @return ����true or false
     */
    public static boolean IsNumeric(String sVar)
    {
        boolean b = true;
        try
        {
            Integer.parseInt(sVar);
        }
        catch (NumberFormatException e)
        {
            b = false;
        }
        return b;
    }


    /**
     * �������Ĺ�����ɾ��һ���ַ����ж���Ŀո�ֻ����һ���ո�
     * @param Pro_Str �������
     * @return �ַ���
     */
    public static String Kill_Blank(String Pro_Str)
    {
        String t_killchr = "";
        Pro_Str = Pro_Str.trim();
        while (Pro_Str.indexOf("  ") >= 0)
        {
            Pro_Str = Pro_Str.replaceAll("  ", " ");
        }
        return Pro_Str;
    }

    /**
     * ����ո�
     * @param Pro_Str �����ַ���
     * @return ���ز���ո����ַ���
     */
    public static String Last_Pro_Str(String Pro_Str)
    {
        Pro_Str = Pro_Str.replaceAll(",", " , ");
        Pro_Str = Pro_Str.replaceAll(",", " , ");
        Pro_Str = Pro_Str.replaceAll("\\(", " \\( ");
        Pro_Str = Pro_Str.replaceAll("\\)", " \\)");
        Pro_Str = Pro_Str.replaceAll(" =", "=");
        Pro_Str = Pro_Str.replaceAll("= ", "=");
        Pro_Str = Pro_Str.replaceAll(" OR ", "  OR ");

        return Pro_Str;
    }

    /**
     * ��һ��ʵ�����и�ʽ�������c_JD=0��ԭ����ֵ���أ����򷵻���λ���ȵ�����
     * @param c_Num ʵ��
     * @param c_JD ����
     * @return �ַ���
     */
    public static String My_Format(double c_Num, int c_JD)
    {
        String t_Return = "";
        String t_Format;
        int i;
        if (c_JD == 0)
        {
            t_Return = String.valueOf(c_Num);
        }
        else
        {
            t_Format = "0.";
            for (i = 1; i <= c_JD; i++)
            {
                t_Format += "0";
            }
            //t_Return = Format(String.valueOf(c_Num), t_Format);

            long l_1 = 1, l_2;

            for (i = 1; i <= c_JD; i++)
            {
                l_1 *= 10;
            }

            c_Num *= l_1;
            l_2 = java.lang.Math.round(c_Num);

            c_Num = ((double) l_2) / l_1;

            t_Return = String.valueOf(c_Num);
        }

        return t_Return;
    }

    //��һ��ͼƬURL�з���һ��GIF�ֽ���
    public static byte[] getGifBytes(java.net.URL picURL) throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Image image = ImageIO.read(picURL);
        GifEncoder gif = new GifEncoder(image, bos);
        gif.encode();
        return bos.toByteArray();
    }

    /**
     * ��������������
     * @param args String[]
     */
    public static void main(String[] args)
    {
//        Date tDate = new Date(System.currentTimeMillis());
//
//        GregorianCalendar tCalendar = new GregorianCalendar();
//        tCalendar.setTime(tDate);
//        System.out.println(String.valueOf(tCalendar.get(Calendar.YEAR)) + "-"
//                           + String.valueOf(tCalendar.get(Calendar.MONTH) + 1) +
//                           "-"
//                           + String.valueOf(tCalendar.get(Calendar.DATE)));
//
//        System.out.println(JRptUtility.getTime());
//        System.out.println(JRptUtility.getDate());
    }
}

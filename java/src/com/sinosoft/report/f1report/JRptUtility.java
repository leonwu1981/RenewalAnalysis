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
     * 该函数得到c_Str中的第c_i个以c_Split分割的字符串
     * @param c_Str 输入串
     * @param c_i 查找位置
     * @param c_Split 分割字符串
     * @return 返回得到的串
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
     * 得到当前日期的字符串
     * @return 日期的字符串
     */
    public static String getDate()
    {
        java.util.Date tDate;
        tDate = new Date(System.currentTimeMillis());
        //对日期操作的时候，建议采用Calendar方法
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
     * 得到当前日期和时间的字符串
     * @return 日期和时间
     */
    public static String getNow()
    {
        return getDate() + " " + getTime();
    }


    /**
     * 得到当前时间的字符串
     * @return 当前时间
     */
    public static String getTime()
    {
        java.util.Date tDate;
        tDate = new Date(System.currentTimeMillis());
        //对日期操作的时候，建议采用Calendar方法
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
     * 将以字段返回的结果进行处理，主要是Null的处理
     * @param fd 传人的字符串
     * @return 字符串
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
     * 根据文件名得到文件不含附加名的字符串,如：c:\temp\abc.vts，则返回abc
     * @param c_FileName 文件名
     * @return 不含附加名的文件名字符串
     */
    public static String Get_OnlyFileNameEx(String c_FileName)
    {
        File f;
        f = new File(c_FileName);
        return f.getName().substring(0, f.getName().length() - 4);
    }

    /**
     * 输入： cSql为输入SQL语句
     * @param cSQL 为输入SQL语句
     * @return 返回值
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
     * 获取值
     * @param cVarString 表达式
     * @return double值
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
     * 判断是否为数字
     * @param sVar 传人字符
     * @return 返回true or false
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
     * 本函数的功能是删除一个字符串中多余的空格，只保留一个空格
     * @param Pro_Str 传入参数
     * @return 字符串
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
     * 插入空格
     * @param Pro_Str 传入字符串
     * @return 返回插入空格后的字符串
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
     * 对一个实数进行格式化，如果c_JD=0则按原来的值返回，否则返回两位精度的数据
     * @param c_Num 实数
     * @param c_JD 精度
     * @return 字符串
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

    //从一个图片URL中返回一个GIF字节流
    public static byte[] getGifBytes(java.net.URL picURL) throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Image image = ImageIO.read(picURL);
        GifEncoder gif = new GifEncoder(image, bos);
        gif.encode();
        return bos.toByteArray();
    }

    /**
     * 主函数，测试用
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

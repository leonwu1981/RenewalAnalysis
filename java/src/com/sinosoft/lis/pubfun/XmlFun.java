/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

//import org.jdom.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sinosoft.lis.db.LBPolDB;
import com.sinosoft.lis.db.LCPolDB;
import com.sinosoft.lis.db.LJAGetEndorseDB;
import com.sinosoft.lis.db.LJTempFeeDB;
import com.sinosoft.lis.vschema.LJAGetEndorseSet;
import com.sinosoft.lis.vschema.LJTempFeeSet;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlFun
{
    private static boolean firstLineFlag = true;

    public XmlFun()
    {
    }

    /**
     * 返回年份加月份信息
     * @param value String
     * @return String
     */
    public static String getYearMonth(String value)
    {
        String tYearMonth = "";
        tYearMonth = value.substring(2, 6);
        return tYearMonth;
    }

    /**
     * 判断中文字符的个数，用于银行xml转换
     * @param value String
     * @param len String
     * @return String
     */
    public static String getChinaLen(String value, String len)
    {
        int n = Integer.parseInt(len);
        char[] arr = value.toCharArray();
        int result = 0;

        for (int i = 0; i < n; i++)
        {
            if (arr[i] > 511)
            {
                result++;
            }
        }

        return result + "";
    }

    /**
     * 判断中文字符的个数，用于银行xml转换
     * @param value String
     * @param len String
     * @return String
     */
    public static String getNameLen(String value, String len)
    {
        int n = Integer.parseInt(len);
        char[] arr = value.toCharArray();
        int result = 0;

        for (int i = 0; i < n; i++)
        {
            if (arr[i] > 511)
            {
                result++;
                n--;
            }
            else
            {
                result++;
            }
        }

        return result + "";
    }

    /**
     * 判断中文字符的个数，用于银行xml转换
     * @param value String
     * @return String
     */
    public static String getChinaLen(String value)
    {
        char[] arr = value.toCharArray();
        int result = 0;

        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i] > 511)
            {
                System.out.println(arr[i]);
                result++;
            }
        }

        return result + "";
    }

    /**
     * 判断中文字符的个数，用于银行xml转换
     * @param value String
     * @return int
     */
    public static int getChinaLength(String value)
    {
        char[] arr = value.toCharArray();
        int result = 0;

        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i] > 511)
            {
//        System.out.print(arr[i]);
                result = result + 2;
            }
            else
            {
//        System.out.print(arr[i]);
                result = result + 1;
            }
        }

        return result;
    }

    /**
     * 得到当前系统日期 author: YT
     * @return 当前日期的格式字符串,日期格式为"yyyy-MM-dd"
     */
    public static String getCurrentDate()
    {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        Date today = new Date();
        return df.format(today);
    }

    /**
     * 得到当前系统日期 author: YT
     * @return 当前日期的格式字符串,日期格式为"yyyy-MM-dd"
     */
    public static String getStandDate(String sendate,String Format)
    {
        FDate tD=new FDate();
        Date baseDate =tD.getDate(sendate);
        //目前的格式为MMddyyyy,MM-dd-yyyy
        SimpleDateFormat df = new SimpleDateFormat(Format);
        df.format(baseDate);
        return df.format(baseDate);
    }
    
    
    /**
     * 得到当前系统日期 author: Tracy
     * @return 当前日期的格式字符串,日期格式为"yyyy-MM-dd"
     */
    public static String getCurrentDate1(String sendate,String Format)
    {
        String pattern = "yy-MM-dd";
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        Date today = new Date();
        
        String FormatDate = "";
        SimpleDateFormat sfd = new SimpleDateFormat(Format);
        FormatDate = sfd.format(today);
        String CalNextDate = PubFun.calDate(sendate, 0, "M", null);
        return FormatDate;
        
        
//        sendate=AgentPubFun.formatDate(sendate);
//        return df.format(sendate);
    }
    
    /**
     * 得到当前系统时间 author: YT
     * @return 当前时间的格式字符串，时间格式为"HH:mm:ss"
     */
    public static String getCurrentTime()
    {
        String pattern = "HH:mm:ss";
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        Date today = new Date();
        return df.format(today);
    }

    /**
     * 标志第一行开始
     */
    public static void setFirstLine()
    {
        firstLineFlag = true;
    }

    /**
     * 判断是否是第一行
     * @return boolean
     */
    public static boolean isFirstLine()
    {
        if (firstLineFlag)
        {
            firstLineFlag = false;
            return true;
        }

        return false;
    }

    /**
     * 根据投保单号获取险种、交费年期和交费间隔类型
     * @param polNo String
     * @return String
     */
    public static String getRiskInfo(String polNo)
    {
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setPolNo(polNo);
        tLCPolDB.setProposalNo(polNo);

        //如果保单表有数据，一定是扣款，直接根据销售渠道判断银代即可
        if (!tLCPolDB.getInfo())
        {
            //如果保单表无数据，对应代付的情况
            System.out.println("可能是代付数据！");

            //从暂交费表中查找，对应投保退费
            LJTempFeeDB tLJTempFeeDB = new LJTempFeeDB();
            tLJTempFeeDB.setTempFeeNo(polNo);
            LJTempFeeSet tLJTempFeeSet = tLJTempFeeDB.query();

            if (tLJTempFeeSet.size() > 0)
            {
                tLCPolDB.setPolNo(tLJTempFeeSet.get(1).getOtherNo());
                if (!tLCPolDB.getInfo())
                {
                    System.out.println("从保单表中获取险种信息失败！");
                    return "0000000";
                }
            }
            //如果暂交费表中无数据，去批改补退费表(实收/实付)查找，对应犹豫期撤单
            else
            {
                LJAGetEndorseDB tLJAGetEndorseDB = new LJAGetEndorseDB();
                tLJAGetEndorseDB.setEndorsementNo(polNo);
                //犹豫期撤单标记
                tLJAGetEndorseDB.setFeeOperationType("WT");
                LJAGetEndorseSet tLJAGetEndorseSet = tLJAGetEndorseDB.query();
                if (tLJAGetEndorseSet.size() == 0)
                {
                    return "0000000";
                }

                //任意取一个保单号
                LBPolDB tLBPolDB = new LBPolDB();
                tLBPolDB.setPolNo(tLJAGetEndorseSet.get(1).getPolNo());
                if (!tLBPolDB.getInfo())
                {
                    System.out.println("从保单B表中获取险种信息失败！");
                    return "0000000";
                }
                //获取该保单号对应的主险保单号
                tLBPolDB.setPolNo(tLBPolDB.getMainPolNo());
                if (!tLBPolDB.getInfo())
                {
                    System.out.println("从保单B表中获取险种信息失败！");
                    return "0000000";
                }
                else
                {
                    tLCPolDB.setSaleChnl(tLBPolDB.getSaleChnl());
                    tLCPolDB.setRiskCode(tLBPolDB.getRiskCode());
                    tLCPolDB.setPayIntv(tLBPolDB.getPayIntv());
                    tLCPolDB.setPayYears(tLBPolDB.getPayYears());
                }
            }
        }

        System.out.println("SaleChnl:" + tLCPolDB.getSaleChnl() +
                " | RiskCode:" +
                tLCPolDB.getRiskCode() + " | PayIntv:" +
                tLCPolDB.getPayIntv() + " | PayYears:" +
                tLCPolDB.getPayYears());

        //根据销售渠道、交费年期和交费间隔返回类型值
        if (tLCPolDB.getSaleChnl().equals("03") && tLCPolDB.getPayIntv() == 0 &&
                tLCPolDB.getPayYears() == 5)
        {
            return tLCPolDB.getRiskCode() + "0";
        }

        if (tLCPolDB.getSaleChnl().equals("03") && tLCPolDB.getPayIntv() == 0 &&
                tLCPolDB.getPayYears() == 8)
        {
            return tLCPolDB.getRiskCode() + "1";
        }

        if (tLCPolDB.getSaleChnl().equals("03") && tLCPolDB.getPayIntv() == 0 &&
                tLCPolDB.getPayYears() == 12)
        {
            return tLCPolDB.getRiskCode() + "1";
        }

        return "0000000";
    }

    public static int num = 0;

    /**
     * 显示W3C的DOM模型对象，显示JDOM的函数在BQ目录下的changeXml类中
     * @param d Node
     */
    public static void displayDocument(Node d)
    {
        num += 2;

        if (d.hasChildNodes())
        {
            NodeList nl = d.getChildNodes();

            for (int i = 0; i < nl.getLength(); i++)
            {
                Node n = nl.item(i);

                for (int j = 0; j < num; j++)
                {
                    System.out.print(" ");
                }
                if (n.getNodeValue() == null)
                {
                    System.out.println("<" + n.getNodeName() + ">");
                }
                else
                {
                    System.out.println(n.getNodeValue());
                }

                displayDocument(n);

                num -= 2;
//        System.out.println("num:" + num);

                if (n.getNodeValue() == null)
                {
                    for (int j = 0; j < num; j++)
                    {
                        System.out.print(" ");
                    }
                    System.out.println("</" + n.getNodeName() + ">");
                }

            }

        }
    }

    /**
     * 显示InputStream流对象
     * @param in InputStream
     */
    public static void displayStream(InputStream in)
    {
        try
        {
//      DataInputStream din = new DataInputStream(in);
            BufferedReader brin = new BufferedReader(new InputStreamReader(in));
            String s = "";

            System.out.println("");
            while ((s = brin.readLine()) != null)
            {
                System.out.println(s);
            }
            System.out.println("");

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * 显示Blob的内容
     * @param blob Blob
     */
    public static void displayBlob(Blob blob)
    {
        try
        {
            InputStream in = blob.getBinaryStream();

            displayStream(in);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        FDate tD=new FDate();
        Date baseDate =tD.getDate("2006-06-01");
        
//        String pattern = Format;
        SimpleDateFormat df = new SimpleDateFormat("MMddyyyy");
        SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd");
        String FormatDate = AgentPubFun.formatDate(baseDate, "yyyymmdd");
        
        String FormatDate1 = AgentPubFun.formatDate("2006-06-01", "yyyymmdd");
        Date CalNextDate = PubFun.calDate(baseDate, 0, "M", null);
        df.format(baseDate);
        df1.format(baseDate);
        AgentPubFun.formatDate(CalNextDate, "yyyymmdd");

//        AgentPubFun.formatDate(mDate, "yyyymmdd");
        System.out.println(getStandDate("2006-07-01","yyyymmdd"));
        System.out.println(getStandDate("2006-01-01","yyyy-mm-dd"));
        System.out.println(getStandDate("2006-01-01","yyyymm"));
        System.out.println(getStandDate("2006-01-01","yyyy-mm"));
        //86110020030310003825
    }
}

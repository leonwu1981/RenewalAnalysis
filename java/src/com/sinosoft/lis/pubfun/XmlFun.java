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
     * ������ݼ��·���Ϣ
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
     * �ж������ַ��ĸ�������������xmlת��
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
     * �ж������ַ��ĸ�������������xmlת��
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
     * �ж������ַ��ĸ�������������xmlת��
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
     * �ж������ַ��ĸ�������������xmlת��
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
     * �õ���ǰϵͳ���� author: YT
     * @return ��ǰ���ڵĸ�ʽ�ַ���,���ڸ�ʽΪ"yyyy-MM-dd"
     */
    public static String getCurrentDate()
    {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        Date today = new Date();
        return df.format(today);
    }

    /**
     * �õ���ǰϵͳ���� author: YT
     * @return ��ǰ���ڵĸ�ʽ�ַ���,���ڸ�ʽΪ"yyyy-MM-dd"
     */
    public static String getStandDate(String sendate,String Format)
    {
        FDate tD=new FDate();
        Date baseDate =tD.getDate(sendate);
        //Ŀǰ�ĸ�ʽΪMMddyyyy,MM-dd-yyyy
        SimpleDateFormat df = new SimpleDateFormat(Format);
        df.format(baseDate);
        return df.format(baseDate);
    }
    
    
    /**
     * �õ���ǰϵͳ���� author: Tracy
     * @return ��ǰ���ڵĸ�ʽ�ַ���,���ڸ�ʽΪ"yyyy-MM-dd"
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
     * �õ���ǰϵͳʱ�� author: YT
     * @return ��ǰʱ��ĸ�ʽ�ַ�����ʱ���ʽΪ"HH:mm:ss"
     */
    public static String getCurrentTime()
    {
        String pattern = "HH:mm:ss";
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        Date today = new Date();
        return df.format(today);
    }

    /**
     * ��־��һ�п�ʼ
     */
    public static void setFirstLine()
    {
        firstLineFlag = true;
    }

    /**
     * �ж��Ƿ��ǵ�һ��
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
     * ����Ͷ�����Ż�ȡ���֡��������ںͽ��Ѽ������
     * @param polNo String
     * @return String
     */
    public static String getRiskInfo(String polNo)
    {
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setPolNo(polNo);
        tLCPolDB.setProposalNo(polNo);

        //��������������ݣ�һ���ǿۿֱ�Ӹ������������ж���������
        if (!tLCPolDB.getInfo())
        {
            //��������������ݣ���Ӧ���������
            System.out.println("�����Ǵ������ݣ�");

            //���ݽ��ѱ��в��ң���ӦͶ���˷�
            LJTempFeeDB tLJTempFeeDB = new LJTempFeeDB();
            tLJTempFeeDB.setTempFeeNo(polNo);
            LJTempFeeSet tLJTempFeeSet = tLJTempFeeDB.query();

            if (tLJTempFeeSet.size() > 0)
            {
                tLCPolDB.setPolNo(tLJTempFeeSet.get(1).getOtherNo());
                if (!tLCPolDB.getInfo())
                {
                    System.out.println("�ӱ������л�ȡ������Ϣʧ�ܣ�");
                    return "0000000";
                }
            }
            //����ݽ��ѱ��������ݣ�ȥ���Ĳ��˷ѱ�(ʵ��/ʵ��)���ң���Ӧ��ԥ�ڳ���
            else
            {
                LJAGetEndorseDB tLJAGetEndorseDB = new LJAGetEndorseDB();
                tLJAGetEndorseDB.setEndorsementNo(polNo);
                //��ԥ�ڳ������
                tLJAGetEndorseDB.setFeeOperationType("WT");
                LJAGetEndorseSet tLJAGetEndorseSet = tLJAGetEndorseDB.query();
                if (tLJAGetEndorseSet.size() == 0)
                {
                    return "0000000";
                }

                //����ȡһ��������
                LBPolDB tLBPolDB = new LBPolDB();
                tLBPolDB.setPolNo(tLJAGetEndorseSet.get(1).getPolNo());
                if (!tLBPolDB.getInfo())
                {
                    System.out.println("�ӱ���B���л�ȡ������Ϣʧ�ܣ�");
                    return "0000000";
                }
                //��ȡ�ñ����Ŷ�Ӧ�����ձ�����
                tLBPolDB.setPolNo(tLBPolDB.getMainPolNo());
                if (!tLBPolDB.getInfo())
                {
                    System.out.println("�ӱ���B���л�ȡ������Ϣʧ�ܣ�");
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

        //���������������������ںͽ��Ѽ����������ֵ
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
     * ��ʾW3C��DOMģ�Ͷ�����ʾJDOM�ĺ�����BQĿ¼�µ�changeXml����
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
     * ��ʾInputStream������
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
     * ��ʾBlob������
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

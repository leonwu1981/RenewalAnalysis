package com.sinosoft.xreport.util;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import com.sinosoft.xreport.bl.ReportMain;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author unascribed
 * @version 1.0
 */

public class XReader
{

    public XReader()
    {
    }

    /**
     * 读取报表定义
     * @param file 报表定义文件名
     * @return 报表定义字符串
     */
    public static String readDefine(String file)
    {
        String stream = "";
        try
        {
            URL url = new URL(SysConfig.TRUEHOST + "down.jsp?type=define&file=" +
                              file);
            URLConnection uc = url.openConnection();
            InputStream in = uc.getInputStream();
            byte[] b = new byte[4096];
            int len = 0;
            while ((len = in.read(b)) >= 0)
            {
                stream = stream + new String(b, 0, len);
            }
            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return stream;
    }

    /**
     * 读取报表数据
     * @param file 报表数据文件名
     * @return 报表数据字符串
     */
    public static String readData(String file)
    {
        String stream = "";
        try
        {
            URL url = new URL(SysConfig.TRUEHOST + "down.jsp?type=data&file=" +
                              file);
            URLConnection uc = url.openConnection();
            InputStream in = uc.getInputStream();
            byte[] b = new byte[4096];
            int len = 0;
            while ((len = in.read(b)) >= 0)
            {
                stream = stream + new String(b, 0, len);
            }
            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return stream;
    }

    /**
     * 读取配置文件
     * @param file 配置文件文件名
     * @return 配置文件字符串
     */
    public static String readConf(String file)
    {
        String stream = "";
        try
        {
            URL url = new URL(SysConfig.TRUEHOST + "down.jsp?type=conf&file=" +
                              file);
            System.out.println(url);
            URLConnection uc = url.openConnection();
            InputStream in = uc.getInputStream();
            byte[] b = new byte[4096];
            int len = 0;
            while ((len = in.read(b)) >= 0)
            {
                stream = stream + new String(b, 0, len);
            }
            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return stream;
    }

    public static String readStyle(String file)
    {
        String stream = "";
        try
        {
            URL url = new URL(SysConfig.TRUEHOST + "down.jsp?type=style&file=" +
                              file);
            URLConnection uc = url.openConnection();
            InputStream in = uc.getInputStream();
            byte[] b = new byte[4096];
            int len = 0;
            while ((len = in.read(b)) >= 0)
            {
                stream = stream + new String(b, 0, len);
            }
            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return stream;
    }


    public static String readPrecal(ReportMain report)
    {
        String strBranchId = report.getBranchId();
        String strReportId = report.getReportId();
        String strReportEdition = report.getReportEdition();
        String stream = "";
        try
        {
            URL url = new URL(SysConfig.TRUEHOST + "precal.jsp?branch=" +
                              strBranchId +
                              "&report=" + strReportId + "&edition=" +
                              strReportEdition);
            URLConnection uc = url.openConnection();
            InputStream in = uc.getInputStream();
            byte[] b = new byte[4096];
            int len = 0;
            while ((len = in.read(b)) >= 0)
            {
                stream = stream + new String(b, 0, len);
            }
            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return stream;
    }

    public static String readCalculate(Map map)
    {
        String strUrl = SysConfig.TRUEHOST + "calculate.jsp";
        String strKey = "", strValue = "";
        String stream = "";
        URL url;
        try
        {
            /**没有计算参数*/
            if (map.size() == 0)
            {
                url = new URL(strUrl);
            }
            /**有计算参数*/
            else
            {
                /**生成带参数的url字符串*/
                Object[] key = map.keySet().toArray();
                strUrl = strUrl + "?";
                for (int i = 0; i < key.length; i++)
                {
                    strKey = (String) key[i];
                    strValue = (String) map.get(strKey);
                    strUrl = strUrl + strKey + "=" + strValue + "&";
                }
                strUrl = strUrl.substring(0, strUrl.length() - 1);
                url = new URL(strUrl);
            }
            URLConnection uc = url.openConnection();
            InputStream in = uc.getInputStream();
            byte[] b = new byte[4096];
            int len = 0;
            while ((len = in.read(b)) >= 0)
            {
                stream = stream + new String(b, 0, len);
            }
            in.close();
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
        return stream;
    }

    public static void main(String[] args)
    {
        System.out.println(XReader.readDefine("000000_month4_20030401.xml"));
//        XMLPathTool xpath=new XMLPathTool(read.readConf("ReportMain.xml"));
//        NodeList list=xpath.parseN("/ReportMains/ReportMain/ReportName");
//        for(int i=0;i<list.getLength();i++)
//        {
//            try
//            {
//                Node node=list.item(i);
//                System.out.println(xpath.getTextContents(node));
//            }
//            catch(Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
    }

}

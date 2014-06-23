package com.sinosoft.xreport.bl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.sinosoft.xreport.util.SysConfig;

/**
 * <p>Title: XReport</p>
 * <p>Description: 报表定义文件中参数的读取</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author lixy
 * @version 1.0
 */

public class ParamsReader
{
    /**单位代码*/
    private String branch = "";
    /**报表代码代码*/
    private String report = "";
    /**报表版别代码*/
    private String edition = "";

    public ParamsReader()
    {
    }

    ///////////////////////JavaBean Method//////////////////////////////////
    public String getBranch()
    {
        return branch;
    }

    public void setBranch(String branch)
    {
        this.branch = branch;
    }

    public void setEdition(String edition)
    {
        this.edition = edition;
    }

    public void setReport(String report)
    {
        this.report = report;
    }

    public String getReport()
    {
        return report;
    }

    public String getEdition()
    {
        return edition;
    }

    ///////////////////////JavaBean End///////////////////////////////////////

    public String getParams()
    {
        /**报表定义读取*/
        DefineReader reader;
        /**完整文件名*/
        String filePath;
        Map map = new HashMap();
        try
        {
            /**读取文件名*/
            filePath = getFileName();
            /**读取报表定义文件中的参数*/
            reader = new DefineReader(filePath);
            map = reader.getParams();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Collection values = map.values();
        Object[] obj = values.toArray();
        String strReturn = "";
        for (int i = 0; i < obj.length; i++)
        {
            Map value = (Map) obj[i];
            Object[] keys = value.keySet().toArray();
            String strText = "";
            for (int j = 0; j < keys.length; j++)
            {
                String strKey = (String) keys[j];
                String strValue = (String) value.get(strKey);
                strText = strText + strKey + SysConfig.SEPARATORTREE + strValue +
                          SysConfig.SEPARATORTWO;
            }
            strReturn = strReturn + strText + SysConfig.SEPARATORONE;
        }
        return strReturn;
    }

    private String getFileName()
    {
        String file = "";
        file = SysConfig.FILEPATH +
               "define\\" +
               branch + "\\" +
               branch + "_" +
               report + "_" +
               edition + ".xml";
        return file;
    }

    public static void main(String[] args)
    {
        ParamsReader param = new ParamsReader();
        param.setBranch("330700");
        param.setReport("js01");
        param.setEdition("200201");
        System.out.println(param.getParams());
    }

}
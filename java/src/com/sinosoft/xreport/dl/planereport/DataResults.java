package com.sinosoft.xreport.dl.planereport;

/**
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author wuxiao
 * @version 1.0
 */
public class DataResults
{

    private int resultCount;
    private Data resultData[];

    public DataResults()
    {}

    /**
     * 取得结果个数
     * @return 结果个数
     */
    public int getResultCount()
    {
        return resultCount;
    }

    /**
     * 取得结果集
     * @return 结果集
     */
    public Data[] getResultData()
    {
        return resultData;
    }

    /**
     * 设置结果个数
     * @param param 结果个数
     */
    public void setResultCount(int param)
    {
        resultCount = param;
    }

    /**
     * 设置结果集
     * @param param 结果集
     */
    public void setResultData(Data[] param)
    {
        resultData = param;
    }

    public String toXML()
    {
        String sXML = "<dataResults>\r\n";
        for (int i = 0; i < resultCount; i++)
        {
            sXML = sXML + resultData[i].toXML();
            System.out.print(i + ",");
        }
        sXML = sXML + "</dataResults>\r\n";
        return sXML;
    }
}
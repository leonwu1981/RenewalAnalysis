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
     * ȡ�ý������
     * @return �������
     */
    public int getResultCount()
    {
        return resultCount;
    }

    /**
     * ȡ�ý����
     * @return �����
     */
    public Data[] getResultData()
    {
        return resultData;
    }

    /**
     * ���ý������
     * @param param �������
     */
    public void setResultCount(int param)
    {
        resultCount = param;
    }

    /**
     * ���ý����
     * @param param �����
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
package com.sinosoft.xreport.dl.planereport;


public class GlobalConditions
{

    private int gConditionCount;
    private GCondition gCondition[];

    public GlobalConditions()
    {}

    /**
     * 取得全局查询条件个数
     * @return 全局查询条件个数
     */
    public int getConditionCount()
    {
        return gConditionCount;
    }

    /**
     * 取得全局查询条件信息
     * @return 全局查询条件信息
     */
    public GCondition[] getConditions()
    {
        return gCondition;
    }

    /**
     * 设置全局查询条件个数
     * @param param 全局查询条件个数
     */
    public void setConditionCount(int param)
    {
        gConditionCount = param;
        gCondition = new GCondition[param];
    }

    /**
     * 设置全局查询条件信息
     * @param param 全局查询条件信息
     */
    public void setConditions(GCondition[] param)
    {
        gCondition = param;
    }

    /**
     * 返回XML字符串
     * @return XML字符串
     */
    public String toXML()
    {
        String sXML = "<global>\r\n";
        for (int i = 0; i < gConditionCount; i++)
        {
            sXML = sXML + gCondition[i].toXML();
        }
        sXML = sXML + "</global>\r\n";
        return sXML;
    }
}
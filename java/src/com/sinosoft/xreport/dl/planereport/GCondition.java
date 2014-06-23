package com.sinosoft.xreport.dl.planereport;

public class GCondition
{

    private String type;
    private String value;

    public GCondition()
    {}

    /**
     * 取得全局查询条件类型
     * @return 全局查询条件类型
     */
    public String getType()
    {
        return type;
    }

    /**
     * 取得全局查询条件取值
     * @return 全局查询条件取值
     */
    public String getValue()
    {
        return value;
    }

    /**
     * 设置全局查询条件类型
     * @param param 全局查询条件类型
     */
    public void setType(String param)
    {
        type = param;
    }

    /**
     * 设置全局查询条件取值
     * @param param 全局查询条件取值
     */
    public void setValue(String param)
    {
        value = param;
    }

    /**
     * 返回XML字符串
     * @return XML字符串
     */
    public String toXML()
    {
        String sXML = "<condition type=\"" + type
                      + "\" value=\"" + value + "\"/>\r\n";
        return sXML;
    }
}
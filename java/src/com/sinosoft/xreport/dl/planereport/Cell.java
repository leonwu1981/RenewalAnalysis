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
public class Cell
{

    private String location;
    private String text;

    public Cell()
    {}

    /**
     * 取得单元格位置
     * @return 单元格位置
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * 取得单元格内容
     * @return 单元格内容
     */
    public String getText()
    {
        return text;
    }

    /**
     * 设置单元格位置
     * @param param 单元格位置
     */
    public void setLocation(String param)
    {
        location = param;
    }

    /**
     * 设置单元格内容
     * @param param 单元格内容
     */
    public void setText(String param)
    {
        text = param;
    }

    /**
     * 返回XML字符串
     * @return XML字符串
     */
    public String toXML()
    {
        String sXML = "<cell location=\"" + location + "\">"
                      + text + "</cell>\r\n";
        return sXML;
    }
}
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
public class Col
{

    private String condition;
    private String formula;
    private String location;
    private String name;
    private String text;

    public Col()
    {}

    /**
     * 取得列头条件
     * @return 列头条件
     */
    public String getCondition()
    {
        return condition;
    }

    /**
     * 取得列头公式
     * @return 列头公式
     */
    public String getFormula()
    {
        return formula;
    }

    /**
     * 取得列头位置
     * @return 列头位置
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * 取得列头内容
     * @return 列头内容
     */
    public String getText()
    {
        return text;
    }

    /**
     * 取得列头名称
     * @return 列头名称
     */
    public String getName()
    {
        return name;
    }

    /**
     * 设置列头条件
     * @param param 列头条件
     */
    public void setCondition(String param)
    {
        condition = param;
    }

    /**
     * 设置列头公式
     * @param param 列头公式
     */
    public void setFormula(String param)
    {
        formula = param;
    }

    /**
     * 设置列头位置
     * @param param 列头位置
     */
    public void setLocation(String param)
    {
        location = param;
    }

    /**
     * 设置列头内容
     * @param param 列头内容
     */
    public void setText(String param)
    {
        text = param;
    }

    /**
     * 设置列头名称
     * @param param 列头名称
     */
    public void setName(String param)
    {
        name = param;
    }

    /**
     * 返回XML字符串
     * @return XML字符串
     */
    public String toXML()
    {
        String sXML = "<col condition=\"" + condition
                      + "\" formula=\"" + formula
                      + "\" location=\"" + location
                      + "\" name=\"" + name
                      + "\">" + text + "</col>\r\n";
        return sXML;
    }
}
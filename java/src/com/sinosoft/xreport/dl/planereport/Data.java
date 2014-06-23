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
public class Data
{

    private String value;
    private int row;
    private int col;
    private String name;

    public Data()
    {}

    /**
     * 取得计算结果
     * @return 计算结果
     */
    public String getValue()
    {
        return value;
    }

    /**
     * 取得计算结果所在行
     * @return 计算结果所在行
     */
    public int getRow()
    {
        return row;
    }

    /**
     * 取得计算结果所在列
     * @return 计算结果所在列
     */
    public int getCol()
    {
        return col;
    }

    /**
     * 取得计算结果所在列的名称
     * @return 计算结果所在列的名称
     */
    public String getName()
    {
        return name;
    }

    /**
     * 设置计算结果所在列的名称
     * @param param 计算结果所在列的名称
     */
    public void setName(String param)
    {
        name = param;
    }

    /**
     * 设置计算结果
     * @param param 计算结果
     */
    public void setValue(String param)
    {
        value = param;
    }

    /**
     * 设置计算结果所在行
     * @param param 计算结果所在行
     */
    public void setRow(int param)
    {
        row = param;
    }

    /**
     * 设置计算结果所在列
     * @param param 计算结果所在列
     */
    public void setCol(int param)
    {
        col = param;
    }

    /**
     * 返回XML字符串
     * @return XML字符串
     */
    public String toXML()
    {
        String sXML = "<data value=\"" + value + "\""
                      + "row=\"" + String.valueOf(row) + "\""
                      + "name=\"" + name + "\""
                      + "col=\"" + String.valueOf(col)
                      + "\"/>\r\n";
        return sXML;
    }
}
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
public class ReplaceCol
{

    private String col;
    private String src;
    private String dataCol;
    private String displayCol;

    public ReplaceCol()
    {}

    /**
     * 取得代码替换列的名称
     * @return 代码替换列的名称
     */
    public String getColumn()
    {
        return col;
    }

    /**
     * 取得代码替换列的代码源查询SQL
     * @return 代码源查询SQL
     */
    public String getSource()
    {
        return src;
    }

    /**
     * 取得代码替换列的数据对应名称
     * @return 数据对应名称
     */
    public String getDataColumn()
    {
        return dataCol;
    }

    /**
     * 取得代码替换列的数据显示列名
     * @return 数据显示列名
     */
    public String getDisplayColumn()
    {
        return displayCol;
    }

    /**
     * 设置代码替换列的列号
     * @param param 代码替换列的列号
     */
    public void setColumn(String param)
    {
        col = param;
    }

    /**
     * 设置代码替换列的代码源查询SQL
     * @param param 代码源查询SQL
     */
    public void setSource(String param)
    {
        src = param;
    }

    /**
     * 设置代码替换列的数据对应列名
     * @param param 数据对应列名
     */
    public void setDataColumn(String param)
    {
        dataCol = param;
    }

    /**
     * 设置代码替换列的数据显示列名
     * @param param 数据显示列名
     */
    public void setDisplayColumn(String param)
    {
        displayCol = param;
    }

    /**
     * 返回XML字符串
     * @return XML字符串
     */
    public String toXML()
    {
        String sXML = "<replaceCol col=\"" + col
                      + "\" src=\"" + src
                      + "\" dataCol=\"" + dataCol
                      + "\" dispCol=\"" + displayCol + "\"/>\r\n";
        return sXML;
    }
}
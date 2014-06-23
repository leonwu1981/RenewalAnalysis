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
public class Append
{

    private String source;
    private AppendCol appendCol[];
    private int appendColCount;
    private int col = 0;

    public Append()
    {}

    /**
     * 获取附加数据源查询SQL
     * @return 附加数据源查询SQL
     */
    public String getSource()
    {
        return source;
    }

    /**
     * 获取附加数据源显示依赖列
     * @return 附加数据源显示依赖列
     */
    public AppendCol[] getAppendCol()
    {
        return appendCol;
    }

    /**
     * 获取附加数据源显示依赖列个数
     * @return 附加数据源显示依赖列个数
     */
    public int getAppendColCount()
    {
        return appendColCount;
    }

    /**
     * 设置附加数据源查询SQL
     * @param param 附加数据源查询SQL
     */
    public void setSource(String param)
    {
        source = param;
    }

    /**
     * 获取附加数据源依赖列对应的数据库名
     * @return 附加数据源依赖列对应的数据库名
     */
    public int getCol()
    {
        return col;
    }

    /**
     * 设置附加数据源依赖列对应的数据库名
     * @param param 附加数据源依赖列对应的数据库名
     */

    public void setCol(int param)
    {
        col = param;
    }

    /**
     * 设置附加数据源显示依赖列
     * @param param 附加数据源显示依赖列
     */
    public void setAppendCol(AppendCol[] param)
    {
        appendCol = param;
    }

    /**
     * 设置附加数据源显示依赖列个数
     * @param param 附加数据源显示依赖列个数
     */
    public void setAppendColCount(int param)
    {
        appendColCount = param;
        appendCol = new AppendCol[param];
    }

    /**
     * 获取附加数据源的xml串
     * @return 附加数据源的xml串
     */
    public String toXML()
    {
        String xml = "";
        xml = xml + "<append src=\"" + source + "\">\r\n";
        for (int i = 0; i < appendColCount; i++)
        {
            xml = xml + appendCol[i].toXML();
        }
        xml = xml + "</append>\r\n";
        return xml;
    }
}
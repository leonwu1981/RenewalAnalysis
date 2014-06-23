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
public class ColumnViews
{

    private int colCount;
    private ViewCol col[];

    public ColumnViews()
    {}

    /**
     * 取得数据列个数
     * @return 数据列个数
     */
    public int getViewColCount()
    {
        return colCount;
    }

    /**
     * 取得数据列信息
     * @return 数据列信息
     */
    public ViewCol[] getViewCol()
    {
        return col;
    }

    /**
     * 设置数据列个数
     * @param param 数据列个数
     */
    public void setViewColCount(int param)
    {
        colCount = param;
        col = new ViewCol[param];
    }

    /**
     * 设置数据列信息
     * @param param 数据列信息
     */
    public void setViewCol(ViewCol[] param)
    {
        col = param;
    }

    /**
     * 返回XML字符串
     * @return XML字符串
     */
    public String toXML()
    {
        String sXML = "<columnViews>\r\n";
        for (int i = 0; i < colCount; i++)
        {
            sXML = sXML + col[i].toXML();
        }
        sXML = sXML + "</columnViews>\r\n";
        return sXML;
    }

    /**
     * 返回SQL字符串
     * @return SQL字符串
     */
    public String toSQL()
    {
        String sql = "";
        for (int i = 0; i < colCount; i++)
        {
            if (col[i].getSource().equalsIgnoreCase("db"))
            {
                if (!sql.equals(""))
                {
                    sql = sql + ",";
                }
                sql = sql + col[i].getDbName();
            }
        }
        return sql;
    }
}
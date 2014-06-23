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
public class SumColumns
{

    private int colCount;
    private SumCol col[];

    public SumColumns()
    {}

    /**
     * 取得小计、合计列个数
     * @return 小计、合计列个数
     */
    public int getSumColCount()
    {
        return colCount;
    }

    /**
     * 取得小计、合计列信息
     * @return 小计、合计列信息
     */
    public SumCol[] getSumCol()
    {
        return col;
    }

    /**
     * 设置小计、合计列个数
     * @param param 小计、合计列个数
     */
    public void setSumColCount(int param)
    {
        colCount = param;
        col = new SumCol[param];
    }

    /**
     * 设置小计、合计列信息
     * @param param 小计、合计列信息
     */
    public void setSumCol(SumCol[] param)
    {
        col = param;
    }

    /**
     * 返回XML字符串
     * @return XML字符串
     */
    public String toXML()
    {
        String sXML = "<sumColumns>\r\n";
        for (int i = 0; i < colCount; i++)
        {
            sXML = sXML + col[i].toXML();
        }
        sXML = sXML + "</sumColumns>\r\n";
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
            if (!sql.equals(""))
            {
                sql = sql + ",";
            }
            sql = sql + "sum(" + col[i].getDbName() + ") " + col[i].getDbName();
        }
        return sql;
    }
}
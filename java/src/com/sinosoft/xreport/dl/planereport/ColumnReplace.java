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
public class ColumnReplace
{
    private int colCount;
    private ReplaceCol col[];

    /**
     * 取得代码转换列个数
     * @return 代码转换列个数
     */
    public int getReplaceColCount()
    {
        return colCount;
    }

    /**
     * 取得代码转换列信息
     * @return 代码转换列信息
     */
    public ReplaceCol[] getReplaceCol()
    {
        return col;
    }

    /**
     * 设置代码转换列个数
     * @param param 代码转换列个数
     */
    public void setReplaceColCount(int param)
    {
        colCount = param;
        col = new ReplaceCol[param];
    }

    /**
     * 设置代码转换列信息
     * @param param 代码转换列信息
     */
    public void setReplaceCol(ReplaceCol[] param)
    {
        col = param;
    }

    /**
     * 返回XML字符串
     * @return XML字符串
     */
    public String toXML()
    {
        String sXML = "<columnReplace>\r\n";
        for (int i = 0; i < colCount; i++)
        {
            sXML = sXML + col[i].toXML();
        }
        sXML = sXML + "</columnReplace>\r\n";
        return sXML;
    }
}
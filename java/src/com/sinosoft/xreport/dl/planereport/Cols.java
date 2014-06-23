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
public class Cols
{

    private int colCount;
    private Col col[];

    public Cols()
    {}

    /**
     * 取得显示列头个数
     * @return 显示列头个数
     */
    public int getColCount()
    {
        return colCount;
    }

    /**
     * 取得显示列头信息
     * @return 显示列头信息
     */
    public Col[] getCol()
    {
        return col;
    }

    /**
     * 设置显示列头个数
     * @param param 显示列头个数
     */
    public void setColCount(int param)
    {
        colCount = param;
        col = new Col[param];
    }

    /**
     * 设置显示列头信息
     * @param param 显示列头信息
     */
    public void setCol(Col[] param)
    {
        col = param;
    }

    /**
     * 返回XML字符串
     * @return XML字符串
     */
    public String toXML()
    {
        String sXML = "<cols>\r\n";
        for (int i = 0; i < colCount; i++)
        {
            sXML = sXML + col[i].toXML();
        }
        sXML = sXML + "</cols>\r\n";
        return sXML;
    }
}
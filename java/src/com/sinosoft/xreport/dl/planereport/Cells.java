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
public class Cells
{

    private int cellCount;
    private Cell cell[];

    public Cells()
    {}

    /**
     * 取得单元格个数
     * @return 显示单元格个数
     */
    public int getCellCount()
    {
        return cellCount;
    }

    /**
     * 取得单元格信息
     * @return 显示单元格信息
     */
    public Cell[] getCell()
    {
        return cell;
    }

    /**
     * 设置单元格个数
     * @param param 单元格个数
     */
    public void setCellCount(int param)
    {
        cellCount = param;
        cell = new Cell[param];
    }

    /**
     * 设置单元格信息
     * @param param 单元格信息
     */
    public void setCell(Cell[] param)
    {
        cell = param;
    }

    /**
     * 返回XML字符串
     * @return XML字符串
     */
    public String toXML()
    {
        String sXML = "<cells>\r\n";
        for (int i = 0; i < cellCount; i++)
        {
            sXML = sXML + cell[i].toXML();
        }
        sXML = sXML + "</cells>\r\n";
        return sXML;
    }
}
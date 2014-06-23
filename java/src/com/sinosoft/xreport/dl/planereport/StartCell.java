package com.sinosoft.xreport.dl.planereport;

public class StartCell
{

    private int row;
    private int col;

    public StartCell()
    {}

    /**
     * 取得起始单元格的行号
     * @return 起始单元格的行号
     */
    public int getRow()
    {
        return row;
    }

    /**
     * 取得起始单元格的列号
     * @return 起始单元格的列号
     */
    public int getCol()
    {
        return col;
    }

    /**
     * 设置起始单元格的行号
     * @param param 起始单元格的行号
     */
    public void setRow(int param)
    {
        row = param;
    }

    /**
     * 设置起始单元格的列号
     * @param param 起始单元格的列号
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
        String sXML = "<startCell col=\"" + col
                      + "\" row=\"" + row
                      + "\"/>\r\n";
        return sXML;
    }
}
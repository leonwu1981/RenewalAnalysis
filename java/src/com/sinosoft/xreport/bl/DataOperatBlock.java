package com.sinosoft.xreport.bl;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author unascribed
 * @version 1.0
 */

import java.util.Map;

public class DataOperatBlock
{


    /**全局变量*/
    private Map global;

    /**格式对象*/
    private Format format;

    /**列头集合,数据格式:定义位置->定义的列头.e.g. "B2"->a colheader object */
    private Map cols;

    /**行头集合*/
    private Map rows;

    /**清单信息集合*/
    private Map planeInfoMap;


    /**特殊定义单元格集合*/
    private Map cells;

    public DataOperatBlock()
    {
    }

    /**
     * 得到所有定义列的列头
     * @return 所有定义列的列头
     */
    public Map getCols()
    {
        return cols;
    }

    public void setCols(Map cols)
    {
        this.cols = cols;
    }

    public void setCells(Map cells)
    {
        this.cells = cells;
    }

    public Map getCells()
    {
        return cells;
    }

    public Format getFormat()
    {
        return format;
    }

    public void setFormat(Format format)
    {
        this.format = format;
    }

    public Map getRows()
    {
        return rows;
    }

    public void setRows(Map rows)
    {
        this.rows = rows;
    }

    private void parseDefine()
    {

    }

    public Map getGlobal()
    {
        return global;
    }

    public void setGlobal(Map global)
    {
        this.global = global;
    }

    public Map getPlaneInfoMap()
    {
        return planeInfoMap;
    }

    public void setPlaneInfoMap(Map planeInfoMap)
    {
        this.planeInfoMap = planeInfoMap;
    }


    /**@todo 根据行号,列号,单元格号取相应的定义*/


    public static void main(String[] args)
    {
        DataOperatBlock dataOperatBlock1 = new DataOperatBlock();
    }
}
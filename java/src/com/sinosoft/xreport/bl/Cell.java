//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\Cell.java

package com.sinosoft.xreport.bl;

import java.util.Map;

import com.sinosoft.xreport.util.StringUtility;

/**
 * 数据单元格类.
 * 数据单元格可以由行,列确定,也可以由单元格定义覆盖原来的定义.
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */
public class Cell
{
    private Formula theFormula;
    private Condition theCondition;

    /**列属性*/
    private ColData colData;

    /**行属性*/
    private RowData rowData;

    /**特殊单元格属性*/
    private CellData cellData;

    /**定义内容,只针对特殊单元格*/
    private Map defineContentMap;

    /**数值*/
    private String value;

    /**位置,格式等属性*/
    private Format format;


    public Cell()
    {

    }

    public CellData getCellData()
    {
        return cellData;
    }

    public void setCellData(CellData cellData)
    {
        this.cellData = cellData;
    }

    public ColData getColData()
    {
        return colData;
    }

    public void setColData(ColData colData)
    {
        this.colData = colData;
    }

    public RowData getRowData()
    {
        return rowData;
    }

    public void setRowData(RowData rowData)
    {
        this.rowData = rowData;
    }

    public Condition getTheCondition()
    {
        return theCondition;
    }

    public void setTheCondition(Condition theCondition)
    {
        this.theCondition = theCondition;
    }

    public Formula getTheFormula()
    {
        return theFormula;
    }

    public void setTheFormula(Formula theFormula)
    {
        this.theFormula = theFormula;
    }

    public Format getFormat()
    {
        return format;
    }

    public void setFormat(Format format)
    {
        this.format = format;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public Map getDefineContentMap()
    {
        return defineContentMap;
    }

    public void setDefineContentMap(Map defineContentMap)
    {
        this.defineContentMap = defineContentMap;
    }

    ////////////////////////////////////////
    //该类最实际的方法.yang
    ////////////////////////////////////////

    private String location;

    /**
     * 设置位置.
     * 主要用于从数据文件读出位置.
     * @param location 位置
     */
    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getLocation() throws Exception
    {
        //位置显式设置过,如从数据文件读取时.
        if (location != null)
        {
            return location;
        }

        //////////////////////////////
        //计算时,单元格的位置由计算获取
        //非特殊单元格
        if (getRowData() != null && getColData() != null)
        {
            String rowLocation = (String) getRowData().getDefineContentMap().
                                 get(BlockElement.LOCATION);
            String colLocation = (String) getColData().getDefineContentMap().
                                 get(BlockElement.LOCATION);

//      System.err.println("rowLocation:"+rowLocation+",colLocation:"+colLocation+",rowDefineMap:"+getRowData().getDefineContentMap());

            return StringUtility.getCol(colLocation) +
                    StringUtility.getRow(rowLocation);
        }
        else //特殊单元格
        {
            String loc = (String) getDefineContentMap().get(BlockElement.
                    LOCATION);
            return loc;
        }
    }

    public String toString()
    {
        return "cellValue:" + getValue();
    }

}

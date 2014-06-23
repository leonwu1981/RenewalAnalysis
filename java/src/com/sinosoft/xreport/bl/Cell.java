//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\Cell.java

package com.sinosoft.xreport.bl;

import java.util.Map;

import com.sinosoft.xreport.util.StringUtility;

/**
 * ���ݵ�Ԫ����.
 * ���ݵ�Ԫ���������,��ȷ��,Ҳ�����ɵ�Ԫ���帲��ԭ���Ķ���.
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

    /**������*/
    private ColData colData;

    /**������*/
    private RowData rowData;

    /**���ⵥԪ������*/
    private CellData cellData;

    /**��������,ֻ������ⵥԪ��*/
    private Map defineContentMap;

    /**��ֵ*/
    private String value;

    /**λ��,��ʽ������*/
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
    //������ʵ�ʵķ���.yang
    ////////////////////////////////////////

    private String location;

    /**
     * ����λ��.
     * ��Ҫ���ڴ������ļ�����λ��.
     * @param location λ��
     */
    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getLocation() throws Exception
    {
        //λ����ʽ���ù�,��������ļ���ȡʱ.
        if (location != null)
        {
            return location;
        }

        //////////////////////////////
        //����ʱ,��Ԫ���λ���ɼ����ȡ
        //�����ⵥԪ��
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
        else //���ⵥԪ��
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

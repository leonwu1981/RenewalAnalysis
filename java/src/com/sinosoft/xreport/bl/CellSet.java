//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\CellSet.java

package com.sinosoft.xreport.bl;

import java.util.HashMap;
import java.util.Map;

/**
 * ��/��ģ�͵ĳ���.
 * ͨ������λ����ĵ�Ԫ��.�������ģ��,����������,�������ģ��,����������.
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */
public abstract class CellSet
{

    /**����*/
    protected String name;
    protected String formula;
    protected String condition;
    protected String type;
    protected String location;


    protected Map defineContentMap;

    /**cell����: ��/�е�����->Cell*/
    protected Map cells;

    public CellSet()
    {
        cells = new HashMap();
    }

    /**
     * ����ֵ����ѯ
     * �����û��Ĳ�ѯ���õ�����(��Ԫ������)����
     * @param rql ��ѯ���,Ʃ�� [ĳ����].[ĳ��] ���� [ĳ����].[ĳ��] ���� [��].[��].[��]
     * ����Ƕ����ݿ�:[��].[��].[��].[��],��������ʡ��[��]
     * @return ���ݼ�,�������ݵ�Ԫ��
     * @throws Exception
     */
    public Cell getCell(String rql) throws Exception
    {
        /**@todo ʵ��*/
        return (Cell) cells.get(rql);
    }

    /**
     * �趨ָ��������ֵ
     * @param rql ָ��������
     * @param cell ֵ
     */
    public void setCell(String rql, Cell cell)
    {
        cells.put(rql, cell);
    }

    /**
     * �õ�����ģ�͵�����.
     * @return ��������
     */
    public String getType()
    {
        return type;
    }

    /**
     * �趨��������.
     * @param type ��������.ͬBlockElement.TYPE
     */
    protected void setType(String type)
    {
        this.type = type;
    }

    /**
     * ����CellSetʵ��,���ؾ�����������
     * @param type ��������
     * @return �����ʵ��,��������,��,���ⵥԪ��.
     */
    public static CellSet getInstance(String type)
    {
        CellSet cs = null;

        if (BlockElement.TYPE_ROWH.equals(type))
        {
            cs = new RowData();
        }
        else if (BlockElement.TYPE_COLH.equals(type))
        {
            cs = new ColData();
        }
        else if (BlockElement.TYPE_CELL.equals(type))
        {
            cs = new CellData();
        }

        //�趨����
        cs.setType(type);
        return cs;
    }

    public Map getCells()
    {
        return cells;
    }

    public String getCondition()
    {
        return condition;
    }

    public String getFormula()
    {
        return formula;
    }

    public String getName()
    {
        return name;
    }

    public void setCells(Map cells)
    {
        this.cells = cells;
    }

    public void setCondition(String condition)
    {
        this.condition = condition;
    }

    public void setFormula(String formula)
    {
        this.formula = formula;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Map getDefineContentMap()
    {
        return defineContentMap;
    }

    public void setDefineContentMap(Map defineContentMap)
    {
        this.defineContentMap = defineContentMap;
    }
}

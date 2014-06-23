//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\CellSet.java

package com.sinosoft.xreport.bl;

import java.util.HashMap;
import java.util.Map;

/**
 * 行/列模型的超类.
 * 通过它定位具体的单元格.如果是行模型,传入列命名,如果是列模型,传入行命名.
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */
public abstract class CellSet
{

    /**命名*/
    protected String name;
    protected String formula;
    protected String condition;
    protected String type;
    protected String location;


    protected Map defineContentMap;

    /**cell集合: 行/列的命名->Cell*/
    protected Map cells;

    public CellSet()
    {
        cells = new HashMap();
    }

    /**
     * 数据值级查询
     * 根据用户的查询语句得到数据(单元格数据)集合
     * @param rql 查询语句,譬如 [某报表].[某列] 或者 [某报表].[某行] 或者 [表].[行].[列]
     * 如果是多数据块:[表].[块].[列].[行],单块数据省略[块]
     * @return 数据集,或者数据单元格
     * @throws Exception
     */
    public Cell getCell(String rql) throws Exception
    {
        /**@todo 实现*/
        return (Cell) cells.get(rql);
    }

    /**
     * 设定指定命名的值
     * @param rql 指定的命名
     * @param cell 值
     */
    public void setCell(String rql, Cell cell)
    {
        cells.put(rql, cell);
    }

    /**
     * 得到行列模型的类型.
     * @return 行列类型
     */
    public String getType()
    {
        return type;
    }

    /**
     * 设定集合类型.
     * @param type 集合类型.同BlockElement.TYPE
     */
    protected void setType(String type)
    {
        this.type = type;
    }

    /**
     * 创建CellSet实例,返回具体的子类对象
     * @param type 行列类型
     * @return 具体的实例,可能是行,列,特殊单元格.
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

        //设定类型
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

//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\DefineBlock.java

package com.sinosoft.xreport.bl;

import java.util.Map;

/**
 * 报表定义块.
 * 它是行头,列头和单独定义单元格的集合.定义中有位置信息.
 * 包含Format对象,描述该块的格式信息.
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */
public class DefineBlock
{
    /**命名*/
    private String name = "default";
    /**定义块类型*/
    private String blockType = "cross";


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


    /**计算时关联的数据块*/
    private DataBlock dataBlock;

    /**特殊定义单元格集合*/
    private Map cells;

    protected DefineBlock()
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

    public DataBlock getDataBlock()
    {
        return dataBlock;
    }

    public void setDataBlock(DataBlock dataBlock)
    {
        this.dataBlock = dataBlock;
    }

    public String getBlockType()
    {
        return blockType;
    }

    public void setBlockType(String blockType)
    {
        this.blockType = blockType;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }


    /**@todo 根据行号,列号,单元格号取相应的定义*/

}
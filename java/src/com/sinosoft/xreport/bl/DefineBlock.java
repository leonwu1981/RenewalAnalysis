//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\DefineBlock.java

package com.sinosoft.xreport.bl;

import java.util.Map;

/**
 * �������.
 * ������ͷ,��ͷ�͵������嵥Ԫ��ļ���.��������λ����Ϣ.
 * ����Format����,�����ÿ�ĸ�ʽ��Ϣ.
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
    /**����*/
    private String name = "default";
    /**���������*/
    private String blockType = "cross";


    /**ȫ�ֱ���*/
    private Map global;

    /**��ʽ����*/
    private Format format;

    /**��ͷ����,���ݸ�ʽ:����λ��->�������ͷ.e.g. "B2"->a colheader object */
    private Map cols;

    /**��ͷ����*/
    private Map rows;

    /**�嵥��Ϣ����*/
    private Map planeInfoMap;


    /**����ʱ���������ݿ�*/
    private DataBlock dataBlock;

    /**���ⶨ�嵥Ԫ�񼯺�*/
    private Map cells;

    protected DefineBlock()
    {

    }

    /**
     * �õ����ж����е���ͷ
     * @return ���ж����е���ͷ
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


    /**@todo �����к�,�к�,��Ԫ���ȡ��Ӧ�Ķ���*/

}
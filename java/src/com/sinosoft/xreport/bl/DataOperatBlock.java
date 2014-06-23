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


    /**���ⶨ�嵥Ԫ�񼯺�*/
    private Map cells;

    public DataOperatBlock()
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


    /**@todo �����к�,�к�,��Ԫ���ȡ��Ӧ�Ķ���*/


    public static void main(String[] args)
    {
        DataOperatBlock dataOperatBlock1 = new DataOperatBlock();
    }
}
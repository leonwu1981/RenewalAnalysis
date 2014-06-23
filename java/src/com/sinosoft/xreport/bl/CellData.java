package com.sinosoft.xreport.bl;

/**
 * 特殊定义单元格数据.
 * 与定义DefineCell相对应.
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */

public class CellData extends CellSet
{

    public CellData()
    {
        setType(BlockElement.TYPE_CELL);

    }

    public static void main(String[] args)
    {
        CellData cellData1 = new CellData();
    }
}
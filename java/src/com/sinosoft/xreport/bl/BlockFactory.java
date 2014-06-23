package com.sinosoft.xreport.bl;

import java.util.Map;

/**
 * 通过定义读取的内容,分别创建RowHeader,ColHeader,DefineCell
 * 不需要指明具体类型,是为Factory
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */

public class BlockFactory
{

    public BlockFactory()
    {
    }

    /**
     * 通过读取内容创建具体的ColHeader,RowHeader,DefineCell
     * @param content 从定义读取的具体内容
     * @return 具体的BlockElement的实现类
     */
    public static BlockElement buildBlockElement(Map content)
    {
        BlockElement be = null;
        if (BlockElement.TYPE_ROWH.equals(content.get(BlockElement.TYPE)))
        {
            be = new RowHeader(content);
        }
        else if (BlockElement.TYPE_COLH.equals(content.get(BlockElement.TYPE)))
        {
            be = new ColHeader(content);
        }
        else if (BlockElement.TYPE_CELL.equals(content.get(BlockElement.TYPE)))
        {
            be = new DefineCell(content);
        }
        return be;
    }


    public static void main(String[] args)
    {
        BlockFactory blockFactory1 = new BlockFactory();
    }
}
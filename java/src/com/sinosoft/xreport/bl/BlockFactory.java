package com.sinosoft.xreport.bl;

import java.util.Map;

/**
 * ͨ�������ȡ������,�ֱ𴴽�RowHeader,ColHeader,DefineCell
 * ����Ҫָ����������,��ΪFactory
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
     * ͨ����ȡ���ݴ��������ColHeader,RowHeader,DefineCell
     * @param content �Ӷ����ȡ�ľ�������
     * @return �����BlockElement��ʵ����
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
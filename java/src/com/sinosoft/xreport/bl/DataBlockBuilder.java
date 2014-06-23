package com.sinosoft.xreport.bl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 将报表定义创建为一个空的数据矩阵.
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */

public class DataBlockBuilder
{
//  Report reportHandler;


    public DataBlockBuilder()
    {
    }

    /**
     * 根据定义创建一个空数据块,支持rql查询,方便放数,取数.
     * @return 数据块
     * @throws Exception 异常
     */
    public DataBlock createDataBlock(DefineBlock defineBlock) throws Exception
    {
        DataBlock dataBlock = new DataBlock();

        Map rows = defineBlock.getRows();
        Map cols = defineBlock.getCols();
        Map cells = defineBlock.getCells();

        Map rowsDataMap = new HashMap(); //行对象集合
        Map colsDataMap = new HashMap(); //列对象集合
        //特殊单元格集合
        Map cellsDataMap = new HashMap();

        //read finish
        ///////////////

        //创建行对象
        Iterator itRowsKey = rows.keySet().iterator();
        while (itRowsKey.hasNext())
        {
            //行命名
            String rowName = (String) itRowsKey.next();
            Map rowHeaderContent = (Map) rows.get(rowName);

            //创建一列数据
            RowData rowData = new RowData();
            rowData.setName(rowName);
            rowData.setDefineContentMap(rowHeaderContent);

            rowsDataMap.put(rowName, rowData);
        }

        //根据列创建所有的DataCell
        Iterator itColsKey = cols.keySet().iterator();
        while (itColsKey.hasNext())
        {
            //列命名
            String colName = (String) itColsKey.next();
            //创建一列数据
            ColData colData = new ColData();
            colData.setName(colName);
            colData.setDefineContentMap((Map) cols.get(colName));

            colsDataMap.put(colName, colData); //放到block的Map中

            Iterator itKey = rows.keySet().iterator();
            while (itKey.hasNext())
            {
                //行命名
                String rowName = (String) itKey.next();
                //创建一列数据
//        RowData rowData=new RowData();
//        rowData.setName(rowName);

                RowData rowData = (RowData) rowsDataMap.get(rowName);

                Cell cell = new Cell();

                cell.setRowData(rowData);
                cell.setColData(colData);

                rowData.getCells().put(colName, cell); //双向指针,分别放入
                colData.getCells().put(rowName, cell); //在行中以列名表示cell,在列中以行名表示cell
            }
        }

        //特殊单元格的处理...
        CellData cellData = new CellData();
        Map spCellsMap = new HashMap();

        Iterator itCellsKey = cells.keySet().iterator();
        while (itCellsKey.hasNext())
        {
            String cellName = (String) itCellsKey.next();
            Map cellsContentMap = (Map) cells.get(cellName);

            Cell cell = new Cell();

            cell.setDefineContentMap(cellsContentMap);

            cell.setCellData(cellData);
            spCellsMap.put(cellName, cell);
        }

        cellData.setCells(spCellsMap);

        dataBlock.setRowDataMap(rowsDataMap);
        dataBlock.setColDataMap(colsDataMap);
        dataBlock.setCellData(cellData);

        //关联定义块,数据块
        dataBlock.setDefineBlock(defineBlock);
        defineBlock.setDataBlock(dataBlock);

        //name
        dataBlock.setName(defineBlock.getName());
        //type
        dataBlock.setBlockType(defineBlock.getBlockType());

        return dataBlock;
    }


    public static void main(String[] args)
    {
        DataBlockBuilder dataBlockBuilder1 = new DataBlockBuilder();
    }
}
package com.sinosoft.xreport.bl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * �������崴��Ϊһ���յ����ݾ���.
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
     * ���ݶ��崴��һ�������ݿ�,֧��rql��ѯ,�������,ȡ��.
     * @return ���ݿ�
     * @throws Exception �쳣
     */
    public DataBlock createDataBlock(DefineBlock defineBlock) throws Exception
    {
        DataBlock dataBlock = new DataBlock();

        Map rows = defineBlock.getRows();
        Map cols = defineBlock.getCols();
        Map cells = defineBlock.getCells();

        Map rowsDataMap = new HashMap(); //�ж��󼯺�
        Map colsDataMap = new HashMap(); //�ж��󼯺�
        //���ⵥԪ�񼯺�
        Map cellsDataMap = new HashMap();

        //read finish
        ///////////////

        //�����ж���
        Iterator itRowsKey = rows.keySet().iterator();
        while (itRowsKey.hasNext())
        {
            //������
            String rowName = (String) itRowsKey.next();
            Map rowHeaderContent = (Map) rows.get(rowName);

            //����һ������
            RowData rowData = new RowData();
            rowData.setName(rowName);
            rowData.setDefineContentMap(rowHeaderContent);

            rowsDataMap.put(rowName, rowData);
        }

        //�����д������е�DataCell
        Iterator itColsKey = cols.keySet().iterator();
        while (itColsKey.hasNext())
        {
            //������
            String colName = (String) itColsKey.next();
            //����һ������
            ColData colData = new ColData();
            colData.setName(colName);
            colData.setDefineContentMap((Map) cols.get(colName));

            colsDataMap.put(colName, colData); //�ŵ�block��Map��

            Iterator itKey = rows.keySet().iterator();
            while (itKey.hasNext())
            {
                //������
                String rowName = (String) itKey.next();
                //����һ������
//        RowData rowData=new RowData();
//        rowData.setName(rowName);

                RowData rowData = (RowData) rowsDataMap.get(rowName);

                Cell cell = new Cell();

                cell.setRowData(rowData);
                cell.setColData(colData);

                rowData.getCells().put(colName, cell); //˫��ָ��,�ֱ����
                colData.getCells().put(rowName, cell); //��������������ʾcell,��������������ʾcell
            }
        }

        //���ⵥԪ��Ĵ���...
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

        //���������,���ݿ�
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
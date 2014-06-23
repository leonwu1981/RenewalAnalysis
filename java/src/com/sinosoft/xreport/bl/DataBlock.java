//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\DataBlock.java

package com.sinosoft.xreport.bl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sinosoft.xreport.util.StringUtility;
import com.sinosoft.xreport.util.XTLogger;
import org.apache.log4j.Logger;
import org.apache.regexp.RE;

/**
 * ���ݿ����
 * �����ļ��������ߴӱ��������ļ�����Ľ��.
 * ����Ŀ��:���ȡ��ʱֻ��ָ�� [ĳ����].[ĳ��] ���� [ĳ����].[ĳ��] (��Ҫ�� bq,tq,sq)
 * �Ϳɵõ���Ӧ��ֵ.
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */
public class DataBlock
{

    Logger log = XTLogger.getLogger(this.getClass());

    /**
     * �������
     */
    private String name;

    /**
     * �������
     */
    private String blockType;

    private Map rowDataMap;
    private Map colDataMap;
    private CellData cellData;


    /**�����ڵ���������,��������->���ж���*/
    private Map rowColMap;

    /**ȫ������,ȡֵ��*/
    private Map gcMap;

    private DefineBlock defineBlock;

    /////////////////////////////
    // ����λ������
    /////////////////////////////
    private Map locationMap;

    private void buildLocationIndex() throws Exception
    {
        if (locationMap != null)
        {
            return;
        }

        locationMap = new HashMap();

//ûɶ��˼
        /*
            //����������ͷ
            Iterator itRowKey=getRowDataMap().keySet().iterator();
            while(itRowKey.hasNext())
            {
              String rowName=(String)itRowKey.next();
              RowData rowData=(RowData) getRowDataMap().get(rowName);
         String loc=(String)rowData.getDefineContentMap().get(BlockElement.LOCATION);

              //��ͷ����
              locationMap.put(loc,rowData);
              //ͬʱ�����ж��� e.g. ["5"->rowData]
              locationMap.put(StringUtility.getRow(loc),rowData);
            }
         */
        //����������ͷ
        Iterator itColKey = getColDataMap().keySet().iterator();
        while (itColKey.hasNext())
        {
            String colName = (String) itColKey.next();
            ColData colData = (ColData) getColDataMap().get(colName);
//      String loc=(String)colData.getDefineContentMap().get(BlockElement.LOCATION);
//
//      //l��ͷ����
//      locationMap.put(loc,colData);
//      //ͬʱ�����ж��� e.g. [A->colData]
//      locationMap.put(StringUtility.getCol(loc),colData);

            //put cells
            Iterator itColRowKey = getRowDataMap().keySet().iterator();
            while (itColRowKey.hasNext())
            {
                String colRowName = (String) itColRowKey.next();
                Cell cell = (Cell) colData.getCells().get(colRowName);
                String cellLoc = cell.getLocation();

                locationMap.put(cellLoc, cell);
            }
        }

        //ok,���ж�������.
        //�������,�Ḳ��һЩ��Ԫ��,Ҫ�ľ�������
        Iterator itSpKey = getCellData().getCells().keySet().iterator();
        while (itSpKey.hasNext())
        {
            String cellName = (String) itSpKey.next();
            Cell cell = (Cell) getCellData().getCells().get(cellName);
            String loc = (String) cell.getLocation();

            locationMap.put(loc, cell);
        }
    }

    public DataBlock()
    {
        rowColMap = new HashMap();
    }

    /**
     * ���ݿ鼶��ѯ
     * �����û��Ĳ�ѯ���õ�����(��Ԫ������)����
     * @param rql ��ѯ���,Ʃ�� [ĳ����].[ĳ��] ���� [ĳ����].[ĳ��] ���� [��].[��].[��]
     * ����Ƕ����ݿ�:[��].[��].[��].[��],��������ʡ��[��]
     * @return ���ݼ�,�������ݵ�Ԫ��
     * @throws Exception
     */
    public CellSet getCellSet(String rql) throws Exception
    {
        /**@todo ʵ��*/
        return (CellSet) rowColMap.get(rql);
    }

    /**
     * @todo: ��getCellByLocation()���ܲ�һ��,�ڼ���ʱ,"�������"--�Ѿ����������滻��
     * ʱ��,���ܳ��ִ���..���ǿ����ڼ���ʱͨ���ű����������,I do that........���ȡ���������������.
     * ����ǰ����ʹ����,���ȡ���ն���ʱҲ������.
     * �ڼ������ ����ʱ,��getCellByLocation()
     * ������������ȡ��.
     * @param rql [��].[��]
     * @return value
     * @throws Exception
     */
    public Cell getCell(String rql) throws Exception
    {
        //����rql
        RE re = new RE("\\[(.*?)\\]\\.\\s*\\[(.*?)\\]"); //\[(.*)\]\.\s*\[(.*)\]
        boolean matched = re.match(rql);
        String colName = null;
        String rowName = null;

        if (matched) //����ȷ����[��].[��]��ʽ
        {
            colName = re.getParen(1);
            rowName = re.getParen(2);
        }
        else
        {
            log.error("*****null rql:" + rql);
            return null;
        }

        /////////////////////////////////
        //
        log.debug("rrql:" + rql + ":colName:" + colName + ":rowName:" + rowName);
        CellSet cs = (CellSet) getColDataMap().get(colName);
        return cs.getCell(rowName);

    }

    /**
     * ����λ��ȡ��Ԫ��.
     * @param location λ��
     * @return ��Ԫ��
     * @throws Exception
     */
    public Cell getCellByLocation(String location) throws Exception
    {
        buildLocationIndex();
        Object o = locationMap.get(location);
        if (o == null || !(o instanceof Cell))
        {
            return null;
        }
        else
        {
            return (Cell) o;
        }
    }


    public String getValue(String rql) throws Exception
    {
        return getCell(rql).getValue();
    }

    public void setValue(String rql, String value) throws Exception
    {
        getCell(rql).setValue(value);
    }

    public void setObject(String rql, Object object) throws Exception
    {}


    /**
     * ���ӻ��޸����ݿ������.
     * @param rql ���е�����.
     * @param cellSet ��������.
     */
    public void setCellSet(String rql, CellSet cellSet)
    {
        rowColMap.put(rql, cellSet);
    }

    public CellData getCellData()
    {
        return cellData;
    }

    public void setCellData(CellData cellData)
    {
        this.cellData = cellData;
    }

    public Map getColDataMap()
    {
        return colDataMap;
    }

    public void setColDataMap(Map colDataMap)
    {
        this.colDataMap = colDataMap;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Map getRowColMap()
    {
        return rowColMap;
    }

    public void setRowColMap(Map rowColMap)
    {
        this.rowColMap = rowColMap;
    }

    public Map getRowDataMap()
    {
        return rowDataMap;
    }

    public void setRowDataMap(Map rowDataMap)
    {
        this.rowDataMap = rowDataMap;
    }

    public Map getLocationMap() throws Exception
    {
        buildLocationIndex();
        return locationMap;
    }

    public void setLocationMap(Map locationMap)
    {
        this.locationMap = locationMap;
    }

    public String toXMLString() throws Exception
    {
        StringBuffer sb = new StringBuffer();

        sb.append("\t<data name=\"")
                .append(getName())
                .append("\" type=\"")
                .append(getBlockType())
                .append("\">\n")
                .append("\t\t<global>\n");

        //ȫ������
        Iterator itGCKey = getGcMap().keySet().iterator();
        while (itGCKey.hasNext())
        {
            String left = (String) itGCKey.next();
            String value = (String) gcMap.get(left);

            sb.append("\t\t\t"); //����
            sb.append("<condition type=\"")
                    .append(left)
                    .append("\" value=\"")
                    .append(value)
                    .append("\" />\n");
        }

        sb.append("\t\t</global>\n");

        //ȷ��������,������������ڵ�,ʹ��������ֵ,���ǲ�ʹ�������Ķ���
        //�������������,����д��������,ʹ��������ֵ.
        Map allData = new HashMap();
        allData.putAll(getLocationMap());

        sb.append("\t\t<cols>\n");

        //������֯����
        Iterator itColKey = getColDataMap().keySet().iterator();
        while (itColKey.hasNext())
        {
            String colName = (String) itColKey.next();
            ColData colData = (ColData) getColDataMap().get(colName);
            String colHeaderLoc = (String) colData.getDefineContentMap().get(
                    BlockElement.LOCATION);

            sb.append("\t\t\t")
                    .append("<col name=\"")
                    .append(colName)
                    .append("\">\n");

            sb.append("\t\t\t\t")
                    .append("<rows>\n");

            Iterator itRowKey = getRowDataMap().keySet().iterator();
            while (itRowKey.hasNext())
            {
                String rowName = (String) itRowKey.next();
                RowData rowData = (RowData) getRowDataMap().get(rowName);
                String rowHeaderLoc = (String) rowData.getDefineContentMap().
                                      get(BlockElement.LOCATION);

                //cell
                //caution: ����Ҫ��locationMap��ȡ,why? �и��ǵĶ�����,��locationMapΪ׼.
                String location = StringUtility.getCol(colHeaderLoc) +
                                  StringUtility.getRow(rowHeaderLoc);

                //�ڼ������ ����ʱ,��getCellByLocation()
                Cell cell = getCellByLocation(location);

                sb.append("\t\t\t\t\t")
                        .append("<row name=\"")
                        .append(rowName)
                        .append("\" location=\"")
                        .append(location)
                        .append("\">") //row
                        .append(cell.getValue())
                        .append("</row>\n");

                allData.remove(location); //ȥ�������ڵĵ�Ԫ��,ֻʣ���������.
            }

            sb.append("\t\t\t\t")
                    .append("</rows>\n");

            sb.append("\t\t\t")
                    .append("</col>\n");

        }

        sb.append("\t\t</cols>\n");

        //cells
        sb.append("\t\t<cells>\n");

        Iterator itSPCellKey = allData.keySet().iterator();
        while (itSPCellKey.hasNext())
        {
            String spLocation = (String) itSPCellKey.next();
            Object o = allData.get(spLocation);
            //caution: ������RowData,ColData,��Щ��,������
            if (!(o instanceof Cell))
            {
                continue;
            }

            Cell spCell = (Cell) o;
            String name = (String) spCell.getDefineContentMap().get(
                    BlockElement.NAME);
            name = (name == null) ? "" : name;

            sb.append("\t\t\t")
                    .append("<cell name=\"")
                    .append(name)
                    .append("\" location=\"")
                    .append(spLocation)
                    .append("\">")
                    .append(spCell.getValue())
                    .append("</cell>\n");

        } //while ���ⵥԪ�� over

        sb.append("\t\t</cells>\n");

        sb.append("\t</data>\n");

        return sb.toString();
    }


    public Map getGcMap()
    {
        if (gcMap == null)
        {
            gcMap = new HashMap();
        }

        return gcMap;
    }

    public void setGcMap(Map gcMap)
    {
        this.gcMap = gcMap;
    }

    public DefineBlock getDefineBlock()
    {
        return defineBlock;
    }

    public void setDefineBlock(DefineBlock defineBlock)
    {
        this.defineBlock = defineBlock;
    }

    public String getBlockType()
    {
        return blockType;
    }

    public void setBlockType(String blockType)
    {
        this.blockType = blockType;
    }


}

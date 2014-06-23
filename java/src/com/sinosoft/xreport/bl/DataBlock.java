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
 * 数据块对象
 * 定义块的计算结果或者从报表数据文件读入的结果.
 * 期望目标:表间取数时只需指明 [某报表].[某列] 或者 [某报表].[某行] (主要是 bq,tq,sq)
 * 就可得到相应数值.
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
     * 块的命名
     */
    private String name;

    /**
     * 块的类型
     */
    private String blockType;

    private Map rowDataMap;
    private Map colDataMap;
    private CellData cellData;


    /**本块内的所有行列,行列命名->行列对象*/
    private Map rowColMap;

    /**全局条件,取值后*/
    private Map gcMap;

    private DefineBlock defineBlock;

    /////////////////////////////
    // 建立位置索引
    /////////////////////////////
    private Map locationMap;

    private void buildLocationIndex() throws Exception
    {
        if (locationMap != null)
        {
            return;
        }

        locationMap = new HashMap();

//没啥意思
        /*
            //放入所有行头
            Iterator itRowKey=getRowDataMap().keySet().iterator();
            while(itRowKey.hasNext())
            {
              String rowName=(String)itRowKey.next();
              RowData rowData=(RowData) getRowDataMap().get(rowName);
         String loc=(String)rowData.getDefineContentMap().get(BlockElement.LOCATION);

              //行头放入
              locationMap.put(loc,rowData);
              //同时放入行对象 e.g. ["5"->rowData]
              locationMap.put(StringUtility.getRow(loc),rowData);
            }
         */
        //放入所有列头
        Iterator itColKey = getColDataMap().keySet().iterator();
        while (itColKey.hasNext())
        {
            String colName = (String) itColKey.next();
            ColData colData = (ColData) getColDataMap().get(colName);
//      String loc=(String)colData.getDefineContentMap().get(BlockElement.LOCATION);
//
//      //l列头放入
//      locationMap.put(loc,colData);
//      //同时放入列对象 e.g. [A->colData]
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

        //ok,行列都放完了.
        //放特殊格,会覆盖一些单元格,要的就是它了
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
     * 数据块级查询
     * 根据用户的查询语句得到数据(单元格数据)集合
     * @param rql 查询语句,譬如 [某报表].[某列] 或者 [某报表].[某行] 或者 [表].[行].[列]
     * 如果是多数据块:[表].[块].[列].[行],单块数据省略[块]
     * @return 数据集,或者数据单元格
     * @throws Exception
     */
    public CellSet getCellSet(String rql) throws Exception
    {
        /**@todo 实现*/
        return (CellSet) rowColMap.get(rql);
    }

    /**
     * @todo: 与getCellByLocation()可能不一致,在计算时,"过晚参照"--已经过了行列替换的
     * 时间,可能出现错误..但是可以在计算时通过脚本覆盖来解决,I do that........表间取数不存在这个问题.
     * 计算前可以使用它,表间取数刚读出时也可用它.
     * 在计算完后 参照时,用getCellByLocation()
     * 根据命名块内取数.
     * @param rql [列].[行]
     * @return value
     * @throws Exception
     */
    public Cell getCell(String rql) throws Exception
    {
        //解析rql
        RE re = new RE("\\[(.*?)\\]\\.\\s*\\[(.*?)\\]"); //\[(.*)\]\.\s*\[(.*)\]
        boolean matched = re.match(rql);
        String colName = null;
        String rowName = null;

        if (matched) //可以确定是[列].[行]格式
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
     * 根据位置取单元格.
     * @param location 位置
     * @return 单元格
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
     * 增加或修改数据块的内容.
     * @param rql 行列的命名.
     * @param cellSet 行列内容.
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

        //全局条件
        Iterator itGCKey = getGcMap().keySet().iterator();
        while (itGCKey.hasNext())
        {
            String left = (String) itGCKey.next();
            String value = (String) gcMap.get(left);

            sb.append("\t\t\t"); //缩进
            sb.append("<condition type=\"")
                    .append(left)
                    .append("\" value=\"")
                    .append(value)
                    .append("\" />\n");
        }

        sb.append("\t\t</global>\n");

        //确定块区域,特殊格在区域内的,使用特殊格的值,但是不使用特殊格的定义
        //特殊格在区域外,定义写到数据区,使用特殊格的值.
        Map allData = new HashMap();
        allData.putAll(getLocationMap());

        sb.append("\t\t<cols>\n");

        //按列组织数据
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
                //caution: 这里要从locationMap中取,why? 有覆盖的定义嘛,以locationMap为准.
                String location = StringUtility.getCol(colHeaderLoc) +
                                  StringUtility.getRow(rowHeaderLoc);

                //在计算完后 参照时,用getCellByLocation()
                Cell cell = getCellByLocation(location);

                sb.append("\t\t\t\t\t")
                        .append("<row name=\"")
                        .append(rowName)
                        .append("\" location=\"")
                        .append(location)
                        .append("\">") //row
                        .append(cell.getValue())
                        .append("</row>\n");

                allData.remove(location); //去掉区域内的单元格,只剩下区域外的.
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
            //caution: 可能是RowData,ColData,有些乱,管它呢
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

        } //while 特殊单元格 over

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

//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\ReportData.java

package com.sinosoft.xreport.bl;

import java.util.Iterator;
import java.util.Map;


public class ReportData
{
    /**报表信息*/
    private DataInfo dataInfo;

    /**数据块*/
    private Map dataBlocks;

    private Format format;


    public ReportData()
    {

    }


    public Map getDataBlocks()
    {
        return dataBlocks;
    }

    public void setDataBlocks(Map dataBlocks)
    {
        this.dataBlocks = dataBlocks;
    }

    /**
     * 报表级数据查询
     * 根据用户的查询语句得到数据(单元格数据)集合
     * @param name 查询语句,譬如 [某报表].[某列] 或者 [某报表].[某行] 或者 [表].[行].[列]
     * 如果是多数据块:[表].[块].[列].[行],单块数据省略[块]
     * @return 数据集
     * @throws Exception
     */
    public DataBlock getDataBlock(String name) throws Exception
    {
        return (DataBlock) dataBlocks.get(name);
    }

    public DataInfo getDataInfo()
    {
        return dataInfo;
    }

    public void setDataInfo(DataInfo dataInfo)
    {
        this.dataInfo = dataInfo;
    }

    /**
     * 查询报表数据的值.
     * 仅对单块数据有效.
     * @todo: 多块数据的实现.
     * @param rql
     * @return
     * @throws Exception
     */
    public String getValue(String rql) throws Exception
    {
        Iterator it = getDataBlocks().keySet().iterator();
        return getDataBlock((String) it.next()).getValue(rql);
    }

    /**
     * 得到指定的单元格.
     * @param rql
     * @return
     * @throws Exception
     */
    public Cell getCell(String rql) throws Exception
    {
        Iterator it = getDataBlocks().keySet().iterator();
        return getDataBlock((String) it.next()).getCell(rql);
    }

    public String toXMLString() throws Exception
    {
        StringBuffer sb = new StringBuffer();

        //process instruction
        sb.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n");
        //start tag
        sb.append("<" + DataReader.START_TAG + ">\n");
        //data info
        sb.append(getDataInfo().toXMLString());

        //blocks' xmlString
        Object[] dataBlockArr = dataBlocks.values().toArray();
        for (int i = 0; i < dataBlockArr.length; i++)
        {
            DataBlock dataBlock = (DataBlock) dataBlockArr[i];

            sb.append(dataBlock.toXMLString());
        }

        //format
        sb.append(getFormat().toXMLString());

        //end report_data tag
        sb.append("</" + DataReader.START_TAG + ">");

        return sb.toString();
    }


    public Format getFormat()
    {
        return format;
    }

    public void setFormat(Format format)
    {
        this.format = format;
    }

}


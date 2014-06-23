package com.sinosoft.xreport.bl;

/**
 * 轻量数据读取.
 * caution:这里读取出的内容只可用于表间取数,如果要更改数据,并写出,请使用"重量"读取器.
 * 轻量 vs.重量 区别:轻量不加载相关的定义信息,不可更改写出数据,重量加载定义信息,可更改,写出
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang
 * @version 1.0
 */

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.sinosoft.xreport.dl.Source;
import com.sinosoft.xreport.dl.SourceImpl;
import com.sinosoft.xreport.util.XMLPathTool;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FlyDataReader
{

    public final static String START_TAG = "report_data";
    //根节点
    public final static String REPORT_DATA = "/report_data";
    //报表信息
    public final static String REPORT_DATA_INFO = REPORT_DATA + "/info";
    //数据块信息
    public final static String REPORT_DATA_DATA = REPORT_DATA + "/data";

    public final static String REPORT_DATA_PARAMETERS = REPORT_DATA +
            "/parameters";

    private String dataFile;
    private StringBuffer sb;
    private Node root;
    private Document document;
    //最后更新时间
    private long lastMotified;

    public FlyDataReader()
    {
    }

    public FlyDataReader(String filePath) throws Exception
    {
        this.dataFile = filePath;
    }

    public FlyDataReader(StringBuffer buffer) throws Exception
    {
        this.sb = buffer;
    }

    private DataInfo reportInfo;

    /**
     * 解析信息部分
     * @throws Exception 解析出错
     */
    private void parseReportInfo() throws Exception
    {
        reportInfo = new DataInfo();

        reportInfo.setBranch(XMLPathTool.getValue(getRoot(),
                                                  REPORT_DATA_INFO + "/branch"));
        reportInfo.setCode(XMLPathTool.getValue(getRoot(),
                                                REPORT_DATA_INFO + "/code"));
        reportInfo.setName(XMLPathTool.getValue(getRoot(),
                                                REPORT_DATA_INFO + "/name"));
        reportInfo.setEdition(XMLPathTool.getValue(getRoot(),
                REPORT_DATA_INFO + "/edition"));
        reportInfo.setOperator(XMLPathTool.getValue(getRoot(),
                REPORT_DATA_INFO + "/operator"));

        reportInfo.setType(XMLPathTool.getValue(getRoot(),
                                                REPORT_DATA_INFO + "/type"));
        reportInfo.setCycle(XMLPathTool.getValue(getRoot(),
                                                 REPORT_DATA_INFO + "/cycle"));
        reportInfo.setFeature(XMLPathTool.getValue(getRoot(),
                REPORT_DATA_INFO + "/feature"));
        reportInfo.setCurrency(XMLPathTool.getValue(getRoot(),
                REPORT_DATA_INFO + "/currency"));
        reportInfo.setRemark(XMLPathTool.getValue(getRoot(),
                                                  REPORT_DATA_INFO + "/remark"));
        reportInfo.setStartDate(XMLPathTool.getValue(getRoot(),
                REPORT_DATA_INFO + "/start_date"));
        reportInfo.setEndDate(XMLPathTool.getValue(getRoot(),
                REPORT_DATA_INFO + "/end_date"));
        reportInfo.setCalculateBranch(XMLPathTool.getValue(getRoot(),
                REPORT_DATA_INFO + "/calculate_branch"));

    }

    private Node getRoot() throws Exception
    {
        if (root == null)
        {
            root = parseRoot();
        }
        return root;
    }

    private Node parseRoot() throws Exception
    {
        return getDoc().getDocumentElement();
    }


    private Document getDoc() throws Exception
    {
        if (null == document)
        {
            parse();
        }
        return document;
    }

    private void parse() throws Exception
    {
        ////////////////lixy begin//////////////
        if (sb != null)
        {
            document = XMLPathTool.parseText(sb.toString());
        }

        if (dataFile != null)
        {
            File file = new File(this.dataFile);
            //记录文件修改时间
            lastMotified = file.lastModified();

            if (!file.exists())
            {
                throw new Exception("找不到数据文件[" + this.dataFile + "]");
            }

            document = XMLPathTool.parseFile(file);
        }
        //////////////////lixy end///////////////////////
    }

    /**
     * 判断文件是否更新.如果更新,重新解析
     * @throws IOException 文件不存在
     */
    public void refresh() throws IOException
    {
        File file = new File(dataFile);
        long l = file.lastModified();
        if (l > lastMotified)
        {
            lastMotified = l;

            //clear, so that recall parse operate.
            this.dataList = null;
            this.dataBlockArr = null;
            this.document = null;
            this.params = null;
            this.root = null;
            this.reportInfo = null;
        }
    }


    ///////////////////////////////////////
    // 数据块
    ///////////////////////////////////////

    //数据块集合
    private NodeList dataList;


    private void parseData() throws Exception
    {
        dataList = XPathAPI.selectNodeList(getRoot(), REPORT_DATA_DATA);
    }

    private NodeList getDataList() throws Exception
    {
        if (dataList == null)
        {
            parseData();
        }
        return dataList;
    }

    /**
     * 报表取数定义块
     */
    private DataBlock[] dataBlockArr;

    /**
     * 解析报表取数定义块defineBlock
     * @throws Exception 解析错误.
     */
    private void parseDataBlock() throws Exception
    {
        NodeList nl = getDataList();

        dataBlockArr = new DataBlock[nl.getLength()];
        for (int i = 0; i < nl.getLength(); i++)
        {
            Node nodeData = nl.item(i);

            dataBlockArr[i] = new DataBlock();

            //block info
            String blockName = XMLPathTool.getValue(nodeData, "@name");
            String blockType = XMLPathTool.getValue(nodeData, "@type");

            dataBlockArr[i].setName(blockName);
            dataBlockArr[i].setBlockType(blockType);

            //parse data/global section,A data block have only one global section.
            Map globalCondition = new HashMap();
            NodeList nlGlobalCondition = XPathAPI.selectNodeList(nodeData,
                    "global/condition");
            for (int j = 0; j < nlGlobalCondition.getLength(); j++)
            {
                Node nodeGlobalCondition = nlGlobalCondition.item(j);
                //条件列
                String type = XMLPathTool.getValue(nodeGlobalCondition, "@type");
                //值.Caution: value中可能有比较操作符...>,<,=
                String value = XMLPathTool.getValue(nodeGlobalCondition,
                        "@value");

                globalCondition.put(type, value);
            }

            ////////////////lixy begin//////////////////
            NodeList nlGlobalUser = XPathAPI.selectNodeList(nodeData,
                    "global/user_sql_where");
            for (int j = 0; j < nlGlobalUser.getLength(); j++)
            {
                Node nodeGlobalUser = nlGlobalUser.item(j);
                //条件列
                String type = "user_sql_where";
                //值.Caution: value中可能有比较操作符...>,<,=
                String value = XMLPathTool.getValue(nodeGlobalUser, "@where");

                globalCondition.put(type, value);
            }
            ////////////////lixy end//////////////////////

            dataBlockArr[i].setGcMap(globalCondition);

            // global condition parse completed.
            /////////////////////////////////////

            /**
             * @todo 清单块处理的接口!!!!!!!!!!!!!!!!!!
             * must be implemented
             */

            Map colDataMap = new HashMap();
            Map rowDataMap = new HashMap();
            CellData spCellData = new CellData();

            //逐列
            NodeList nlColumn = XPathAPI.selectNodeList(nodeData, "cols/col");
            for (int k = 0; k < nlColumn.getLength(); k++)
            {
                Node nodeColumn = nlColumn.item(k);
                ColData colData = new ColData();
                String colName = XMLPathTool.getValue(nodeColumn, "@name");
                colData.setName(colName);
                colDataMap.put(colName, colData);

                //行内的单元格
                NodeList nlRowCell = XPathAPI.selectNodeList(nodeColumn,
                        "rows/row");
                for (int l = 0; l < nlRowCell.getLength(); l++)
                {
                    Node rowCell = nlRowCell.item(l);
                    String rowName = XMLPathTool.getValue(rowCell, "@name");
                    String cellLocation = XMLPathTool.getValue(rowCell,
                            "@location");
                    String value = XMLPathTool.getValue(rowCell);

                    RowData rowData = (RowData) rowDataMap.get(rowName);
                    if (rowData == null)
                    {
                        rowData = new RowData();
                        rowData.setName(rowName);
                        rowDataMap.put(rowName, rowData);
                    }

                    Cell cell = new Cell();
                    cell.setLocation(cellLocation);
                    cell.setValue(value);

                    //指向行列集合指针
                    cell.setRowData(rowData);
                    cell.setColData(colData);

                    rowData.getCells().put(colName, cell);
                    colData.getCells().put(rowName, cell);
                }
            }
            ///////////////////////////////////
            //special cells
            NodeList nlSPCell = XPathAPI.selectNodeList(nodeData, "cells/cell");
            for (int m = 0; m < nlSPCell.getLength(); m++)
            {
                Node spCellNode = nlSPCell.item(m);
                String name = XMLPathTool.getValue(spCellNode, "@name");
                String location = XMLPathTool.getValue(spCellNode, "@location");
                String value = XMLPathTool.getValue(spCellNode);

                Cell cell = new Cell();
                cell.setLocation(location);
                cell.setValue(value);
                cell.setCellData(spCellData);

                spCellData.getCells().put(name, cell);
            }

            //构造dataBlock的数据信息
            dataBlockArr[i].setColDataMap(colDataMap);
            dataBlockArr[i].setRowDataMap(rowDataMap);
            dataBlockArr[i].setCellData(spCellData);
        }
    }


    /**
     * 得到所有数据块定义.
     * @return 数据块定义数组.
     * @throws Exception 解析错误.
     */
    public DataBlock[] getDataBlocks() throws Exception
    {
        if (dataBlockArr == null)
        {
            parseDataBlock();
        }

        return dataBlockArr;
    }


    ///////////////////////lixy begin////////////////////////
    /**数据源信息*/
    private Vector vecSource;
    /**数据源信息*/
    public final static String SOURCE = REPORT_DATA + "/sources/source";
    /**代码表*/
    private Code code = new Code();

    public Vector getSource() throws Exception
    {
        if (vecSource == null)
        {
            parseSource();
        }
        return vecSource;
    }

    private void parseSource()
    {
        vecSource = new Vector();
        try
        {
            NodeList list = XPathAPI.selectNodeList(getRoot(), SOURCE);
            for (int i = 0; i < list.getLength(); i++)
            {
                Source source = new SourceImpl();
                source.setDataSourceId(XMLPathTool.getValue(list.item(i)));
                source.setDataSourceName(code.getDataSource(
                        XMLPathTool.getValue(list.item(i))).getDataSourceName());
                vecSource.addElement(source);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

///////////////////////////lixy end////////////////////////////

    ///////////////////////////////////////
    // 可传入参数的解析
    ///////////////////////////////////////
    /**
     * @todo: 可传入参数的解析
     */

    /**
     * 参数集合,[name->[name->value,tips->value,showMode->value]]
     */
    private Map params;

    /**
     * 解析用户参数.
     * @throws Exception 解析错误.
     */
    private void parseParams() throws Exception
    {
        params = new HashMap();

        NodeList nl = XPathAPI.selectNodeList(getRoot(),
                                              REPORT_DATA_PARAMETERS + "/parameter");

        for (int i = 0; i < nl.getLength(); i++)
        {
            Node node = nl.item(i);
            Map param = new HashMap();

            String name = XMLPathTool.getAttrValue(node, Parameter.NAME);
            String tips = XMLPathTool.getAttrValue(node, Parameter.TIPS);
            String showMode = XMLPathTool.getAttrValue(node, Parameter.SHOWMODE);

            param.put(Parameter.NAME, name);
            param.put(Parameter.TIPS, tips);
            param.put(Parameter.SHOWMODE, showMode);

            params.put(name, param);
        }
    }

    /**
     * 得到用户传入参数的集合
     * @return 报表定义参数
     * @throws Exception 解析错误
     */
    public Map getParams() throws Exception
    {
        if (params == null)
        {
            parseParams();
        }
        return params;
    }

    public DataInfo getDataInfo() throws Exception
    {
        if (reportInfo == null)
        {
            parseReportInfo();
        }
        return reportInfo;
    }

    public Format getFormat() throws Exception
    {
        Format format = new Format();
        String file = XMLPathTool.getValue(getRoot(),
                                           REPORT_DATA + "/format/file");
        format.setFile(file);
        return format;
    }

    public static void main(String[] args) throws Exception
    {

    }
}
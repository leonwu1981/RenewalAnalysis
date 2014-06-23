package com.sinosoft.xreport.bl;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author houzw
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

public class DataReader
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

    public DataReader()
    {
    }

    public DataReader(String filePath) throws Exception
    {
        this.dataFile = filePath;
    }

    public DataReader(StringBuffer buffer) throws Exception
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
    private DataOperatBlock[] dataBlockArr;

    /**
     * 解析报表取数定义块defineBlock
     * @throws Exception 解析错误.
     */
    private void parseDataBlock() throws Exception
    {
        NodeList nl = getDataList();

        dataBlockArr = new DataOperatBlock[nl.getLength()];
        for (int i = 0; i < nl.getLength(); i++)
        {
            Node nodeData = nl.item(i);

            dataBlockArr[i] = new DataOperatBlock();

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

            dataBlockArr[i].setGlobal(globalCondition);

            // global condition parse completed.
            /////////////////////////////////////

            /**
             * @todo 清单块处理的接口!!!!!!!!!!!!!!!!!!
             * must be implemented
             */



            NodeList nlColumn = XPathAPI.selectNodeList(nodeData, "cols/col");
            Map colMap = doParseCols(nlColumn, BlockElement.TYPE_COLH);
            dataBlockArr[i].setCols(colMap);

            NodeList nlCell = XPathAPI.selectNodeList(nodeData, "cells/cell");
            Map cellMap = this.parseRowColCell(nlCell, BlockElement.TYPE_CELL);
            dataBlockArr[i].setCells(cellMap);

//            //parse data/rows section
//            NodeList nlRow=XPathAPI.selectNodeList(nodeData,"rows/row");
//            Map rowhMap=parseRowColCell(nlRow,BlockElement.TYPE_ROWH);
//            dataBlockArr[i].setRows(rowhMap);
//
//            //parse data/cols section
//            NodeList nlCol=XPathAPI.selectNodeList(nodeData,"cols/col");
//            Map colhMap=parseRowColCell(nlCol,BlockElement.TYPE_COLH);
//            dataBlockArr[i].setCols(colhMap);
//
//            //parse data/cells section
//            NodeList nlCell=XPathAPI.selectNodeList(nodeData,"cells/cell");
//            Map cellsMap=parseRowColCell(nlCell,BlockElement.TYPE_CELL);
//            dataBlockArr[i].setCells(cellsMap);

            //data tag parse complete
            ///////////////////////////////////

            //format
            /**
             * @todo: format implement
             */

        }
    }


    private Map doParseCols(NodeList nl, String type) throws Exception
    {
        Map result = new HashMap();
        for (int i = 0; i < nl.getLength(); i++)
        {
            Node node = nl.item(i);
            Map keyItemMap = new HashMap();
            String value = null;

            keyItemMap.put(BlockElement.TYPE, type);

            value = XMLPathTool.getAttrValue(node, BlockElement.CONDITION);
            if (!value.equals("") && value != null)
            {
                keyItemMap.put(BlockElement.CONDITION, value);
            }

            value = XMLPathTool.getAttrValue(node, BlockElement.FORMULA);
            if (!value.equals("") && value != null)
            {
                keyItemMap.put(BlockElement.FORMULA, value);
            }

            value = XMLPathTool.getAttrValue(node, BlockElement.NAME);
            if (!value.equals("") && value != null)
            {
                keyItemMap.put(BlockElement.NAME, value);
            }

            Map keyMap = new HashMap();
            keyMap.put(value, keyItemMap);

            Map valueMap = new HashMap();
            NodeList nlRow = XPathAPI.selectNodeList(node, "rows/row");
            for (int j = 0; j < nlRow.getLength(); j++)
            {
                Node rowNode = nlRow.item(j);
                String dataValue;
                Map valueItemMap = new HashMap();

                valueItemMap.put(BlockElement.TYPE, BlockElement.TYPE_ROWH);

                dataValue = XMLPathTool.getAttrValue(rowNode,
                        BlockElement.CONDITION);
                if (!dataValue.equals("") && dataValue != null)
                {
                    valueItemMap.put(BlockElement.CONDITION, dataValue);
                }

                dataValue = XMLPathTool.getAttrValue(rowNode,
                        BlockElement.FORMULA);
                if (!dataValue.equals("") && dataValue != null)
                {
                    valueItemMap.put(BlockElement.FORMULA, dataValue);
                }

//          dataValue = XMLPathTool.getAttrValue(rowNode,BlockElement.CONTEXT);
//          if(!dataValue.equals("") && dataValue != null)
//            valueItemMap.put(BlockElement.CONTEXT,dataValue);

                dataValue = XMLPathTool.getValue(rowNode, ".");
                if (!dataValue.equals("") && dataValue != null)
                {
                    valueItemMap.put(BlockElement.CONTEXT, dataValue);
                }

                dataValue = XMLPathTool.getAttrValue(rowNode, BlockElement.NAME);
                if (!dataValue.equals("") && dataValue != null)
                {
                    valueItemMap.put(BlockElement.NAME, dataValue);
                }

                valueMap.put(dataValue, valueItemMap);
            }
            result.put(keyMap, valueMap);
        }

        return result;
    }

    /**
     * 解析行列单元格等.
     * @param nl 行/列/单元格的定义节点集
     * @param type 指定行/列/单元格类型,{@link BlockElement.TYPE}
     * @return 以位置为key的定义.
     * @throws Exception 解析错误
     */
    private Map parseRowColCell(NodeList nl, String type) throws Exception
    {

        Map result = new HashMap();

        for (int j = 0; j < nl.getLength(); j++)
        {
            Node node = nl.item(j);
            Map itemDefine = new HashMap();
            //item header type
            itemDefine.put(BlockElement.TYPE, type);

            String definedValue;

            //get condition...
            definedValue = XMLPathTool.getAttrValue(node,
                    BlockElement.CONDITION);
            if (null != definedValue && !"".equals(definedValue))
            {
                itemDefine.put(BlockElement.CONDITION, definedValue);
            }

            //get formula
            definedValue = XMLPathTool.getAttrValue(node, BlockElement.FORMULA);
            if (null != definedValue && !"".equals(definedValue))
            {
                itemDefine.put(BlockElement.FORMULA, definedValue);
            }

            //get context/text value
            definedValue = XMLPathTool.getValue(node, ".");
            if (null != definedValue && !"".equals(definedValue))
            {
                itemDefine.put(BlockElement.CONTEXT, definedValue);
            }

            //get location
            definedValue = XMLPathTool.getAttrValue(node, BlockElement.LOCATION);
            if (null != definedValue && !"".equals(definedValue))
            {
                itemDefine.put(BlockElement.LOCATION, definedValue);
            }

            //yang changed on 2003/03/18 增加"命名"属性,用来标识行/列/单元格
            //get name
            definedValue = XMLPathTool.getAttrValue(node, BlockElement.NAME);

            //如果没有定义name属性,用描述作为命名.
            if (null != definedValue && !"".equals(definedValue))
            {
                definedValue = (String) itemDefine.get(BlockElement.CONTEXT);
            }
            itemDefine.put(BlockElement.NAME, definedValue);

            //放入
            result.put(definedValue, itemDefine);

        }

        return result;
    }


    /**
     * 得到所有数据块定义.
     * @return 数据块定义数组.
     * @throws Exception 解析错误.
     */
    public DataOperatBlock[] getDataBlocks() throws Exception
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


    public static void main(String[] args) throws Exception
    {
        DataReader dataReader1 = new DataReader(
                "D:\\xreport_data\\data\\330700\\111111\\js0207h_20020101_20030401_20030430.xml");
        NodeList nl = dataReader1.getDataList();
        DataOperatBlock[] b = dataReader1.getDataBlocks();
        Map m = b[0].getCols();
        System.out.println(m.size());
        //Map mm = dataReader1.get
        dataReader1.parseReportInfo();
        DataInfo di = dataReader1.reportInfo;

        System.out.println(di.getCalculateBranch());
        //System.out.println(di.getDataFile());
        System.out.println(di.getBranch());
        System.out.println(di.getName());
        System.out.println(di.getCode());
        System.out.println(di.getEdition());
    }
}
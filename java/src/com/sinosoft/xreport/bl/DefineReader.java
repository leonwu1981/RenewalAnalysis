//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\DefineReader.java

package com.sinosoft.xreport.bl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.sinosoft.xreport.dl.Source;
import com.sinosoft.xreport.dl.SourceImpl;
import com.sinosoft.xreport.util.XMLPathTool;
import com.sinosoft.xreport.util.XTLogger;
import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 定义文件的XPath相关处理类.
 * 可以使用几个公用方法得到所有的定义读取结果.
 *
 * @todo: 将set,write操作都放在这里.
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */
public class DefineReader
{
    /**定义的根*/
//  public final static String ROOT="/NewDataSet";

    /**定义开始*/
    public final static String REPORT_DEFINE = "/report_define";

    /**报表信息*/
    public final static String INFO = REPORT_DEFINE + "/info";

    /**参数信息*/
    public final static String PARAMETERS = REPORT_DEFINE + "/parameters";

    /**数据块信息*/
    public final static String DATA = REPORT_DEFINE + "/data";

    /**Format信息*/
    public final static String FORMAT = REPORT_DEFINE + "/format";

    /**Logger*/
    Logger log = XTLogger.getLogger(this.getClass());


//   public DefineReader()
//   {
//
//   }
//
//   public DefineReader(InputStream is)
//   {
//     this.is=is;
//   }

    private String defineFile;
    public DefineReader(String filePath) throws IOException
    {
        this.defineFile = filePath;
    }

    private StringBuffer buffer;
    public DefineReader(StringBuffer buffer)
    {
        this.buffer = buffer;
    }

    /////////////////////////////////////////
    // 解析报表信息部分
    /////////////////////////////////////////

    //报表信息对象
    private DefineInfo reportInfo;

    /**
     * 解析信息部分
     * @throws Exception 解析出错
     */
    private void parseReportInfo() throws Exception
    {
        reportInfo = new DefineInfo();

        reportInfo.setBranch(XMLPathTool.getValue(getRoot(), INFO + "/branch"));
        reportInfo.setCode(XMLPathTool.getValue(getRoot(), INFO + "/code"));
        reportInfo.setName(XMLPathTool.getValue(getRoot(), INFO + "/name"));
        reportInfo.setEdition(XMLPathTool.getValue(getRoot(), INFO + "/edition"));
        reportInfo.setOperator(XMLPathTool.getValue(getRoot(),
                INFO + "/operator"));
        reportInfo.setType(XMLPathTool.getValue(getRoot(), INFO + "/type"));
        reportInfo.setCycle(XMLPathTool.getValue(getRoot(), INFO + "/cycle"));
        reportInfo.setFeature(XMLPathTool.getValue(getRoot(), INFO + "/feature"));
        reportInfo.setCurrency(XMLPathTool.getValue(getRoot(),
                INFO + "/currency"));
        reportInfo.setRemark(XMLPathTool.getValue(getRoot(), INFO + "/remark"));

//     return reportInfo;
    }

    /**
     * 得到报表信息部分解析结果
     * @return 解析结果
     * @throws Exception 解析结果
     */
    public DefineInfo getReportInfo() throws Exception
    {
        if (reportInfo == null)
        {
            parseReportInfo();
        }
        return reportInfo;
    }

    /**报表信息*/
    private ReportMain tReportMain;

    public ReportMain getReportMain() throws Exception
    {
        if (tReportMain == null)
        {
            parseReportMain();
        }
        return tReportMain;
    }

    private void parseReportMain() throws Exception
    {
        tReportMain = new ReportMain();
        tReportMain.setBranchId(XMLPathTool.getValue(getRoot(),
                INFO + "/branch"));
        tReportMain.setReportId(XMLPathTool.getValue(getRoot(), INFO + "/code"));
        tReportMain.setReportEdition(XMLPathTool.getValue(getRoot(),
                INFO + "/edition"));
        tReportMain.setReportName(XMLPathTool.getValue(getRoot(),
                INFO + "/name"));
        tReportMain.setReportType(XMLPathTool.getValue(getRoot(),
                INFO + "/type"));
        tReportMain.setReportCycle(XMLPathTool.getValue(getRoot(),
                INFO + "/cycle"));
        tReportMain.setReportAtt(XMLPathTool.getValue(getRoot(),
                INFO + "/feature"));
        tReportMain.setCurrency(XMLPathTool.getValue(getRoot(),
                INFO + "/currency"));

    }

    ///////////////////////lixy begin////////////////////////
    /**数据源信息*/
    private Vector vecSource;
    /**数据源信息*/
    public final static String SOURCE = REPORT_DEFINE + "/sources/source";
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
                                              PARAMETERS + "/parameter");

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

    private void parseFormat() throws Exception
    {
        format = new Format();

        format.setFile(XMLPathTool.getValue(getRoot(), FORMAT + "/file"));

        /**todo:更多实现*/
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

    private Format format;

    public Format getFormat() throws Exception
    {
        if (format == null)
        {
            parseFormat();
        }
        return format;
    }

    ///////////////////////////////////////
    // 数据块
    ///////////////////////////////////////

    //数据块集合
    private NodeList dataList;


    private void parseData() throws Exception
    {
        dataList = XPathAPI.selectNodeList(getRoot(), DATA);
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
    private DefineBlock[] defineBlockArr;

    /**
     * 解析报表取数定义块defineBlock
     * @throws Exception 解析错误.
     */
    private void parseDefineBlock() throws Exception
    {
        NodeList nl = getDataList();

        defineBlockArr = new DefineBlock[nl.getLength()];
        for (int i = 0; i < nl.getLength(); i++)
        {
            Node nodeData = nl.item(i);

            defineBlockArr[i] = new DefineBlock();

            ///////////name and block's type by yang at 2003-4-3 9:15
            String name = XMLPathTool.getValue(nodeData, "@name");
            name = (name == null || "".equals(name)) ? "unNamedBlock" + i :
                   name; //name

            String blockType = XMLPathTool.getValue(nodeData, "@type");
            blockType = (blockType == null || "".equals(blockType)) ? "cross" :
                        blockType; //name

            defineBlockArr[i].setName(name);
            defineBlockArr[i].setBlockType(blockType);

            ///////////end yang

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

            defineBlockArr[i].setGlobal(globalCondition);

            // global condition parse completed.
            /////////////////////////////////////

            /**
             * @todo 清单块处理的接口!!!!!!!!!!!!!!!!!!
             * must be implemented
             */


            //parse data/rows section
            NodeList nlRow = XPathAPI.selectNodeList(nodeData, "rows/row");
            Map rowhMap = parseRowColCell(nlRow, BlockElement.TYPE_ROWH);
            defineBlockArr[i].setRows(rowhMap);

            //parse data/cols section
            NodeList nlCol = XPathAPI.selectNodeList(nodeData, "cols/col");
            Map colhMap = parseRowColCell(nlCol, BlockElement.TYPE_COLH);
            defineBlockArr[i].setCols(colhMap);

            //parse data/cells section
            NodeList nlCell = XPathAPI.selectNodeList(nodeData, "cells/cell");
            Map cellsMap = parseRowColCell(nlCell, BlockElement.TYPE_CELL);
            defineBlockArr[i].setCells(cellsMap);

            //data tag parse complete
            ///////////////////////////////////

            //format
            /**
             * @todo: format implement
             */

        }
    }

    /**
     * 得到所有数据块定义.
     * @return 数据块定义数组.
     * @throws Exception 解析错误.
     */
    public DefineBlock[] getDefineBlocks() throws Exception
    {
        if (defineBlockArr == null)
        {
            parseDefineBlock();
        }

        return defineBlockArr;
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
            if (null == definedValue || "".equals(definedValue))
            {
                definedValue = (String) itemDefine.get(BlockElement.CONTEXT);
            }
            itemDefine.put(BlockElement.NAME, definedValue);

            //放入
            result.put(definedValue, itemDefine);

        }

        return result;
    }


    //////////////////////////////////////////////
    // 解析文件,以下为使用XPath的准备部分
    //////////////////////////////////////////////

    private Document document;
    private void parse() throws Exception
    {
        ////////////////lixy begin//////////////
        if (buffer != null)
        {
            document = XMLPathTool.parseText(buffer.toString());
        }

        if (defineFile != null)
        {
            File file = new File(this.defineFile);
            //记录文件修改时间
            lastMotified = file.lastModified();
            document = XMLPathTool.parseFile(file);
        }
        //////////////////lixy end///////////////////////

        log.debug("defineFile=" + defineFile);
    }

    private Document getDoc() throws Exception
    {
        if (null == document)
        {
            parse();
        }
        return document;
    }

    private Node root;
    private Node parseRoot() throws Exception
    {
        return getDoc().getDocumentElement();
    }

    private Node getRoot() throws Exception
    {
        if (root == null)
        {
            root = parseRoot();
        }
        return root;
    }

    //最后更新时间
    private long lastMotified;

    /**
     * 判断文件是否更新.如果更新,重新解析
     * @throws IOException 文件不存在
     */
    public void refresh() throws IOException
    {
        File file = new File(defineFile);
        long l = file.lastModified();
        if (l > lastMotified)
        {
            lastMotified = l;

            //clear, so that recall parse operate.
            this.dataList = null;
            this.defineBlockArr = null;
            this.document = null;
            this.params = null;
            this.root = null;
            this.reportInfo = null;
        }
    }


    public static void main(String[] args) throws Exception
    {
        String myDefine =
                "D:\\xreport_data\\define\\330700\\330700_js0207h_20020101.xml";
        DefineReader dr = new DefineReader(myDefine);
        Vector vec = dr.getSource();
        for (int i = 0; i < vec.size(); i++)
        {
            System.out.println(vec.elementAt(i));
        }
    }


}
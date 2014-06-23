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
 * �����ļ���XPath��ش�����.
 * ����ʹ�ü������÷����õ����еĶ����ȡ���.
 *
 * @todo: ��set,write��������������.
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
    /**����ĸ�*/
//  public final static String ROOT="/NewDataSet";

    /**���忪ʼ*/
    public final static String REPORT_DEFINE = "/report_define";

    /**������Ϣ*/
    public final static String INFO = REPORT_DEFINE + "/info";

    /**������Ϣ*/
    public final static String PARAMETERS = REPORT_DEFINE + "/parameters";

    /**���ݿ���Ϣ*/
    public final static String DATA = REPORT_DEFINE + "/data";

    /**Format��Ϣ*/
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
    // ����������Ϣ����
    /////////////////////////////////////////

    //������Ϣ����
    private DefineInfo reportInfo;

    /**
     * ������Ϣ����
     * @throws Exception ��������
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
     * �õ�������Ϣ���ֽ������
     * @return �������
     * @throws Exception �������
     */
    public DefineInfo getReportInfo() throws Exception
    {
        if (reportInfo == null)
        {
            parseReportInfo();
        }
        return reportInfo;
    }

    /**������Ϣ*/
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
    /**����Դ��Ϣ*/
    private Vector vecSource;
    /**����Դ��Ϣ*/
    public final static String SOURCE = REPORT_DEFINE + "/sources/source";
    /**�����*/
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
    // �ɴ�������Ľ���
    ///////////////////////////////////////
    /**
     * @todo: �ɴ�������Ľ���
     */

    /**
     * ��������,[name->[name->value,tips->value,showMode->value]]
     */
    private Map params;

    /**
     * �����û�����.
     * @throws Exception ��������.
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

        /**todo:����ʵ��*/
    }

    /**
     * �õ��û���������ļ���
     * @return ���������
     * @throws Exception ��������
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
    // ���ݿ�
    ///////////////////////////////////////

    //���ݿ鼯��
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
     * ����ȡ�������
     */
    private DefineBlock[] defineBlockArr;

    /**
     * ��������ȡ�������defineBlock
     * @throws Exception ��������.
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
                //������
                String type = XMLPathTool.getValue(nodeGlobalCondition, "@type");
                //ֵ.Caution: value�п����бȽϲ�����...>,<,=
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
                //������
                String type = "user_sql_where";
                //ֵ.Caution: value�п����бȽϲ�����...>,<,=
                String value = XMLPathTool.getValue(nodeGlobalUser, "@where");

                globalCondition.put(type, value);
            }
            ////////////////lixy end//////////////////////

            defineBlockArr[i].setGlobal(globalCondition);

            // global condition parse completed.
            /////////////////////////////////////

            /**
             * @todo �嵥�鴦��Ľӿ�!!!!!!!!!!!!!!!!!!
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
     * �õ��������ݿ鶨��.
     * @return ���ݿ鶨������.
     * @throws Exception ��������.
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
     * �������е�Ԫ���.
     * @param nl ��/��/��Ԫ��Ķ���ڵ㼯
     * @param type ָ����/��/��Ԫ������,{@link BlockElement.TYPE}
     * @return ��λ��Ϊkey�Ķ���.
     * @throws Exception ��������
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

            //yang changed on 2003/03/18 ����"����"����,������ʶ��/��/��Ԫ��
            //get name
            definedValue = XMLPathTool.getAttrValue(node, BlockElement.NAME);

            //���û�ж���name����,��������Ϊ����.
            if (null == definedValue || "".equals(definedValue))
            {
                definedValue = (String) itemDefine.get(BlockElement.CONTEXT);
            }
            itemDefine.put(BlockElement.NAME, definedValue);

            //����
            result.put(definedValue, itemDefine);

        }

        return result;
    }


    //////////////////////////////////////////////
    // �����ļ�,����Ϊʹ��XPath��׼������
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
            //��¼�ļ��޸�ʱ��
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

    //������ʱ��
    private long lastMotified;

    /**
     * �ж��ļ��Ƿ����.�������,���½���
     * @throws IOException �ļ�������
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
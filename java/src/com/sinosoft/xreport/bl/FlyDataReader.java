package com.sinosoft.xreport.bl;

/**
 * �������ݶ�ȡ.
 * caution:�����ȡ��������ֻ�����ڱ��ȡ��,���Ҫ��������,��д��,��ʹ��"����"��ȡ��.
 * ���� vs.���� ����:������������صĶ�����Ϣ,���ɸ���д������,�������ض�����Ϣ,�ɸ���,д��
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
    //���ڵ�
    public final static String REPORT_DATA = "/report_data";
    //������Ϣ
    public final static String REPORT_DATA_INFO = REPORT_DATA + "/info";
    //���ݿ���Ϣ
    public final static String REPORT_DATA_DATA = REPORT_DATA + "/data";

    public final static String REPORT_DATA_PARAMETERS = REPORT_DATA +
            "/parameters";

    private String dataFile;
    private StringBuffer sb;
    private Node root;
    private Document document;
    //������ʱ��
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
     * ������Ϣ����
     * @throws Exception ��������
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
            //��¼�ļ��޸�ʱ��
            lastMotified = file.lastModified();

            if (!file.exists())
            {
                throw new Exception("�Ҳ��������ļ�[" + this.dataFile + "]");
            }

            document = XMLPathTool.parseFile(file);
        }
        //////////////////lixy end///////////////////////
    }

    /**
     * �ж��ļ��Ƿ����.�������,���½���
     * @throws IOException �ļ�������
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
    // ���ݿ�
    ///////////////////////////////////////

    //���ݿ鼯��
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
     * ����ȡ�������
     */
    private DataBlock[] dataBlockArr;

    /**
     * ��������ȡ�������defineBlock
     * @throws Exception ��������.
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

            dataBlockArr[i].setGcMap(globalCondition);

            // global condition parse completed.
            /////////////////////////////////////

            /**
             * @todo �嵥�鴦��Ľӿ�!!!!!!!!!!!!!!!!!!
             * must be implemented
             */

            Map colDataMap = new HashMap();
            Map rowDataMap = new HashMap();
            CellData spCellData = new CellData();

            //����
            NodeList nlColumn = XPathAPI.selectNodeList(nodeData, "cols/col");
            for (int k = 0; k < nlColumn.getLength(); k++)
            {
                Node nodeColumn = nlColumn.item(k);
                ColData colData = new ColData();
                String colName = XMLPathTool.getValue(nodeColumn, "@name");
                colData.setName(colName);
                colDataMap.put(colName, colData);

                //���ڵĵ�Ԫ��
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

                    //ָ�����м���ָ��
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

            //����dataBlock��������Ϣ
            dataBlockArr[i].setColDataMap(colDataMap);
            dataBlockArr[i].setRowDataMap(rowDataMap);
            dataBlockArr[i].setCellData(spCellData);
        }
    }


    /**
     * �õ��������ݿ鶨��.
     * @return ���ݿ鶨������.
     * @throws Exception ��������.
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
    /**����Դ��Ϣ*/
    private Vector vecSource;
    /**����Դ��Ϣ*/
    public final static String SOURCE = REPORT_DATA + "/sources/source";
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
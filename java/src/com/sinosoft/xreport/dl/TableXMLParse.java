package com.sinosoft.xreport.dl;

/**
 * �������ݿ��������ļ�
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author houzw
 * @version 1.0
 */
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.sinosoft.xreport.util.XMLPathTool;
import com.sinosoft.xreport.util.XReader;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class TableXMLParse
{

    //����ڵ�
    private static final String XQLROOT = "/Models";
    private static final String XQLMODEL = XQLROOT + "/Model";
    private static final String XQLCOLUMN = XQLMODEL + "/Column";
    private static final String XQLRELATIONS = XQLROOT + "/Relations";
    private static final String XQLRELATION = XQLRELATIONS + "Relation";
    //�ļ���λ��
    private String filePath =
            "D:\\xreport\\src\\com\\sinosoft\\xreport\\dl\\DataSourceSchema.xml";
    private Document document;
    private Node root;

    private InputSource in;


    /**
     * ���캯��
     * @param null
     * @return null
     */
    public TableXMLParse()
    {

    }

    /**
     * ָ���ļ�·��
     * @param filepath �ļ�·��
     * @return null
     */
    public TableXMLParse(String filepath)
    {
        this.filePath = filepath;
    }


    /**
     * �������ݱ�������ļ�
     * @param null
     * @return null
     */
    public void doParseXML() throws Exception
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        StringReader sr = new StringReader(XReader.readConf(
                "DataSourceSchema.xml"));

        InputSource insour = new InputSource(sr);
        //document = db.parse(filePath);
        document = db.parse(insour);
        //��ø��ڵ�
        root = document.getDocumentElement();
    }

    public Node getRoot() throws Exception
    {
        return this.root;
    }

    /**
     * ������еı�ÿ������Ϊһ���ڵ�
     * @param null
     * @return һ���ڵ㼯�����������е�Model�ڵ�
     */
    public NodeList getAllModels() throws Exception
    {
        NodeList nl = XPathAPI.selectNodeList(root, XQLMODEL);
        return nl;
    }

    /**
     * ������еĹ�����ϵ
     * @param null
     * @return һ���ڵ㼯���������еĹ�����ϵ
     */
    public NodeList getAllRelations() throws Exception
    {
        NodeList nl = XPathAPI.selectNodeList(root, XQLRELATIONS);
        return nl;
    }

    /**
     * ������
     * @param args[]
     * @return null
     */
    public static void main(String[] args) throws Exception
    {

        //TableXMLParse test = new TableXMLParse("D:\\xreport\\src\\com\\sinosoft\\xreport\\dl\\DataSourceSchema.xml");
        TableXMLParse test = new TableXMLParse();
        test.doParseXML();
        //TableXMLParse test = parse();
        NodeList nl = test.getAllModels();

        for (int i = 0; i < nl.getLength(); i++)
        {
            Node tmpNode = nl.item(i);
            Model m = new Model(tmpNode);

            NodeList nll = m.getAllColumns();
            System.out.println("i get " + nll.getLength() + "columns");
            for (int j = 0; j < nll.getLength(); j++)
            {
                Column c = new Column(nll.item(j));
            }
        }

        NodeList nlrelation = test.getAllRelations();
        for (int ii = 0; ii < nlrelation.getLength(); ii++)
        {
            Relations r = new Relations(nlrelation.item(ii));
            NodeList nlchild = r.getAllChildRelations();
            for (int jj = 0; jj < nlchild.getLength(); jj++)
            {
                Relation rr = new Relation(nlchild.item(jj));
            }
        }
    }
}


/////////////////////////////////////////////////////////////////////////
//�ڵ���Ϊ����ʵ��
//�ڵ�Model
class Model
{
    Node model;

    public Model()
    {
    }

    public Model(Node n)
    {
        this.model = n;
    }

    public NodeList getAllColumns() throws Exception
    {
        //NodeList nl = XPathAPI.selectNodeList(model,"/Models/Model/Column");//����д��Ҳ����
        NodeList nl = XPathAPI.selectNodeList(model, "Column");
        return nl;
    }

    private String getModelProValue(String relativeXQL) throws Exception
    {
        String result = XMLPathTool.getValue(model, relativeXQL);
        return (result == null) ? "" : result;
    }

    public String getModelID() throws Exception
    {
        return getModelProValue("@id");
    }

    public String getModelName() throws Exception
    {
        return getModelProValue("@name");
    }

    public String getModelSource() throws Exception
    {
        return getModelProValue("@source");
    }

}


///////////////////////////////////////////////////////////////////////////////
//�ڵ�Column

class Column
{

    Node column;

    public Column()
    {
    }

    public Column(Node n)
    {
        this.column = n;
    }

    private String getColumnProValue(String s) throws Exception
    {
        String result = XMLPathTool.getValue(column, s);
        return (result == null) ? "" : result;
    }

    public String getColumnName() throws Exception
    {
        return getColumnProValue("@name");
    }

    public String getColumnDesc() throws Exception
    {
        return getColumnProValue("@desc");
    }

    public String getColumnTreeMode() throws Exception
    {
        return getColumnProValue("@treeMode");
    }

}


////////////////////////////////////////////////////////////////////////////
//�ڵ�Relations
class Relations
{
    Node relations;

    public Relations()
    {}

    public Relations(Node n)
    {
        this.relations = n;
    }

    public NodeList getAllChildRelations() throws Exception
    {
        NodeList nl = XPathAPI.selectNodeList(relations, "Relation");
        return nl;
    }
}


////////////////////////////////////////////////////////////////////////////
//�ڵ�Relation
class Relation
{
    Node relation;

    public Relation()
    {}

    public Relation(Node n)
    {
        this.relation = n;
    }

    private String getRelationProValue(String s) throws Exception
    {
        String result = XMLPathTool.getValue(relation, s);
        return (result == null) ? "" : result;
    }

    public String getRelationValue() throws Exception
    {
        return XMLPathTool.getTextContents(relation);
    }
}

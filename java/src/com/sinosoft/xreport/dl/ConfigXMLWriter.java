package com.sinosoft.xreport.dl;

/**
 * 报表计算时间配置描述文件的存储
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author houzw
 * @version 1.0
 */

import java.io.FileWriter;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.sinosoft.xreport.util.XMLPathTool;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ConfigXMLWriter
{


    String template = "<?xml version=\"1.0\" encoding=\"GB2312\"?>"
                      + "\n" + "<Config>"
                      + "\n" + "<System>"
                      + "\n" + "</System>"
                      + "\n" + "<Users>"
                      + "\n" + "</Users>"
                      + "\n" + "</Config>";

    //定义节点
    private static final String XQLRoot = "/Config";

    private static final String XQLSystem = XQLRoot + "/System";
    private static final String XQLDefaultDateFormat = XQLSystem +
            "/DefaultDateFormat";
    private static final String XQLDBDateFormat = XQLSystem + "/DBDateFormat";
    private static final String XQLReportDate = XQLSystem + "/ReportDate";
    private static final String XQLFAFlag = XQLSystem + "/FAFlag";
    private static final String XQLReportTables = XQLSystem + "/ReportTables";
    private static final String XQLTable = XQLReportTables + "/Table";

    private static final String XQLUsers = XQLRoot + "/Users";
    private static final String XQLUser = XQLUsers + "/User";
    private static final String XQLId = XQLUser + "/ID";
    private static final String XQLQueryStartDate = XQLUser + "/QueryStartDate";
    private static final String XQLQueryEndDate = XQLUser + "/QueryEndDate";
    private static final String XQLHobby = XQLUser + "/Hobby";
    private static final String XQLThemes = XQLHobby + "/Themes";


    //文件的位置
    //private String filePath = "D:\\xreport\\src\\com\\sinosoft\\xreport\\dl\\Config.xml";
    private String filePath = "";
    private Document document;
    private Node root;
    private Node ConfigRoot;
    private Node SystemRoot;
    private Node UsersRoot;
    private Node TablesRoot;

    private InputSource in;


    public ConfigXMLWriter()
    {
    }

    public ConfigXMLWriter(String filepath)
    {
        this.filePath = filepath;
    }

    public void parseXML() throws Exception
    {
        StringReader sr = new StringReader(this.template);
        InputSource inputSource = new InputSource(sr);

        // Parse the file
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        //    document = db.parse(filePath);
        document = db.parse(inputSource);
        root = document.getDocumentElement();
        ConfigRoot = XPathAPI.selectSingleNode(root, XQLRoot);
        SystemRoot = XPathAPI.selectSingleNode(root, XQLSystem);
        UsersRoot = XPathAPI.selectSingleNode(root, XQLUsers);
    }

    /**
     * 加一个新节点,并分行
     * @param start 开始节点
     * @param name 新节点名
     * @return 新节点
     */
    private Node addNodeNewLine(Node start, String name)
    {
        Document document = start.getOwnerDocument();
        Node newLine = document.createTextNode("\n");
        //    Node newLine2=document.createTextNode("\n");
        Node newNode = document.createElement(name);

        //    start.appendChild(newLine);
        start.appendChild(newNode);
        start.appendChild(newLine);

        return newNode;
    }

    public Node getRootNode() throws Exception
    {
        return this.root;
    }

    public Node getSystemNode() throws Exception
    {
        return SystemRoot;
    }

    public Node getTablesNode() throws Exception
    {
        if (this.TablesRoot == null)
        {
            TablesRoot = addNodeNewLine(SystemRoot, "ReportTables");
        }
        return TablesRoot;
    }

    public Node getUsersNode() throws Exception
    {
        if (this.UsersRoot == null)
        {
            UsersRoot = addNodeNewLine(ConfigRoot, "Users");
        }
        return UsersRoot;
    }

    public void addSetSystemValue(String newNodeName, String value) throws
            Exception
    {
        Node sysNode = getSystemNode();
//
//    Node newNode=addNodeNewLine(sysNode,newNodeName);
//    setNodeValue(newNode,value);
//

        if (XPathAPI.selectSingleNode(sysNode, newNodeName) != null)
        {
            Node tempNode = XPathAPI.selectSingleNode(sysNode, newNodeName);
            sysNode.removeChild(tempNode);
            //setNodeValue(tempNode,value);
            Node newNode = addNodeNewLine(sysNode, newNodeName);
            setNodeValue(newNode, value);
        }
        else
        {
            Node newNode = addNodeNewLine(sysNode, newNodeName);
            setNodeValue(newNode, value);
        }

        //Node newNode=addNodeNewLine(sysNode,newNodeName);
        //setNodeValue(newNode,value);

    }

    public void addSetTable(String newNodeName, String value) throws Exception
    {
        Node tablesNode = getTablesNode();
        Node newNode = addNodeNewLine(tablesNode, newNodeName);
        setNodeValue(newNode, value);
    }

    public void setNodeValue(Node node, String value) throws Exception
    {
        Node textNode = document.createTextNode(value);
        node.appendChild(textNode);
    }

    public void setDefaultDateFormat(String value) throws Exception
    {
        addSetSystemValue("DefaultDateFormat", value);
    }

    public void setDBDateFormat(String value) throws Exception
    {
        addSetSystemValue("DBDateFormat", value);
    }

    public void setReportDate(String value) throws Exception
    {
        addSetSystemValue("ReportDate", value);
    }

    public void setFAFlag(String value) throws Exception
    {
        addSetSystemValue("FAFlag", value);
    }

    public void setTable(String value) throws Exception
    {
        Node node = getTablesNode();
        if (node == null)
        {
            TablesRoot = addNodeNewLine(SystemRoot, "ReportTables");
        }
        addSetTable("Table", value);
    }

    public NodeList getAllUser() throws Exception
    {
        NodeList nl = XPathAPI.selectNodeList(root, XQLUser);
        return nl;
    }


    public void save(String userId, String queryStartDate, String queryEndDate) throws
            Exception
    {
        ConfigXMLWriter parse = new ConfigXMLWriter();
        parse.parseXML();
        NodeList nl = parse.getAllUser();
        Node get = null;
        for (int i = 0; i < nl.getLength(); i++)
        {
            User u = new User(nl.item(i));
            if (u.getID().equals(userId))
            {
                get = nl.item(i);
                break;
            }
        }

        if (get == null)
        {
            UserWriter uw = new UserWriter(this.getUsersNode());
            uw.setUserID(userId);
            uw.setUserQueryStartDate(queryStartDate);
            uw.setUserQueryEndDate(queryEndDate);
        }
        else
        {
            UserWriter uw = new UserWriter(getUsersNode(), get);
            uw.setUserID(userId);
            System.out.println();
            uw.setUserQueryStartDate(queryStartDate);
            uw.setUserQueryEndDate(queryEndDate);
        }

    }

    //////////////////////////
    public static void main(String args[]) throws Exception
    {
        ConfigXMLWriter writer = new ConfigXMLWriter();
        writer.parseXML();
        writer.setDBDateFormat("aaa");
        //writer.setDBDateFormat("ddd");
        writer.setDefaultDateFormat("bbb");
        //writer.setDefaultDateFormat("ccc");
        writer.setFAFlag("flag");
        //writer.setFAFlag("ga");
        writer.setReportDate("date");
        writer.setTable("表1");
        writer.setTable("tables");
        writer.setTable("table2");

        System.out.println(XMLPathTool.toString(writer.root));
        System.out.println("--------------------");

        writer.save("houzw", "20030329", "20030331");

        //System.out.println(writer.template);
        Node user = writer.getUsersNode();
        System.out.println(XMLPathTool.toString(writer.root));
        System.out.println("--------------------");
        for (int i = 0; i < 3; i++)
        {
            UserWriter uw = new UserWriter(user);
            uw.setUserID("id" + i);
            uw.setUserQueryStartDate("start" + i);
            uw.setUserQueryEndDate("end" + i);
            uw.setUserThemes("theme" + i);
        }
        //uw.setUserID("id1");
        FileWriter fw = new FileWriter("d:\\Config.xml");
        fw.write(XMLPathTool.toString(writer.root));
        fw.close();
    }

}


///////////////////////////////////////////////////

class UserWriter
{
    private Node UsersNode;
    private Node UserNode;
    private Document document;
    private Node HobbyNode;

    public UserWriter(Node node) throws Exception
    {
        this.UsersNode = node;
        document = UsersNode.getOwnerDocument();
        UserNode = addNodeNewLine(UsersNode, "User");
    }

    public UserWriter(Node usersNode, Node userNode) throws Exception
    {
        this.UsersNode = usersNode;
        this.UserNode = userNode;
        document = UserNode.getOwnerDocument();
    }

    public void setUserID(String value) throws Exception
    {
        addSetUserValue("ID", value);
    }

    public void setUserQueryStartDate(String value) throws Exception
    {
        addSetUserValue("QueryStartDate", value);
    }

    public void setUserQueryEndDate(String value) throws Exception
    {
        addSetUserValue("QueryEndDate", value);
    }

    public void setUserThemes(String value) throws Exception
    {
        addSetHobbyValue("Themes", value);
    }

    public void setNodeValue(Node node, String value) throws Exception
    {
        Node textNode = document.createTextNode(value);
        node.appendChild(textNode);
    }

    //add by hou
    private void addSetUserValue(String newNodeName, String value) throws
            Exception
    {
        //System.out.println("得到 newNodeName ：" + newNodeName);
        if (XPathAPI.selectSingleNode(UserNode, newNodeName) != null)
        {
            Node tempNode = XPathAPI.selectSingleNode(UserNode, newNodeName);
            setNodeValue(tempNode, value);
        }
        else
        {
            Node newNode = addNodeNewLine(UserNode, newNodeName);
            setNodeValue(newNode, value);
        }
    }

    private void addSetHobbySubValue(String newNodeName, String value) throws
            Exception
    {
        //System.out.println("得到 newNodeName ：" + newNodeName);
        if (XPathAPI.selectSingleNode(HobbyNode, newNodeName) != null)
        {
            Node tempNode = XPathAPI.selectSingleNode(HobbyNode, newNodeName);
            setNodeValue(tempNode, value);
        }
        else
        {
            Node newNode = addNodeNewLine(HobbyNode, newNodeName);
            setNodeValue(newNode, value);
        }
    }

    private void addSetHobbyValue(String newNodeName, String value) throws
            Exception
    {
        Node user = XPathAPI.selectSingleNode(UserNode, "Hobby");
        if (user == null)
        {
            HobbyNode = addNodeNewLine(UserNode, "Hobby");
        }
        addSetHobbySubValue(newNodeName, value);
    }

    private Node addNodeNewLine(Node start, String name)
    {
        Node newLine = document.createTextNode("\n");
        //    Node newLine2=document.createTextNode("\n");
        Node newNode = document.createElement(name);

        //    start.appendChild(newLine);
        start.appendChild(newNode);
        start.appendChild(newLine);

        return newNode;
    }

}

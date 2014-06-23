package com.sinosoft.xreport.dl;

/**
 * �������������û����ñ������ʱ���XML�Ľ���
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
import com.sinosoft.xreport.util.XTLogger;
import com.sinosoft.xreport.util.XWriter;
import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class ConfigXMLParse
{

    //����ڵ�
    private static final String XQLRoot = "/Config";

    private static final String XQLSystem = XQLRoot + "/System";
    private static final String XQLDefaultDateFormat = XQLSystem +
            "/DefaultDateFormat";
    private static final String XQLDBDateFormat = XQLSystem + "/DBDateFormat";
    private static final String XQLReportDate = XQLSystem + "/ReportDate";
    private static final String XQLFAFlag = XQLSystem + "/FAFlag";
    private static final String XQLReportTables = XQLSystem + "/ReportTables";
    private static final String XQLTable = XQLReportTables + "/Table";
    private static final String XQLAuthorize = XQLSystem + "/Authorize";
    private static final String XQLManageCom = XQLAuthorize + "/ManageCom";
    private static final String XQLPerson = XQLAuthorize + "/Person";
    private static final String XQLPerview = XQLAuthorize + "/Purview";


    private static final String XQLUsers = XQLRoot + "/Users";
    private static final String XQLUser = XQLUsers + "/User";
    private static final String XQLId = XQLUser + "/ID";
    private static final String XQLQueryStartDate = XQLUser + "/QueryStartDate";
    private static final String XQLQueryEndDate = XQLUser + "/QueryEndDate";
    private static final String XQLHobby = XQLUser + "/Hobby";
    private static final String XQLThemes = XQLHobby + "/Themes";


    //�ļ���λ��
    //private String filePath = "D:\\xreport\\src\\com\\sinosoft\\xreport\\dl\\Config.xml";
    private String filePath = "d:\\Config.xml"; //for test
    private Document document;
    private Node root;

    private InputSource in;

    private int flag = 0; //��־λ�������ж�XML�ļ��Ƿ��Ѿ�������
    static ConfigXMLParse cxp = null; //����������Ķ���


    /**
     * �ж��Ƿ��Ѿ�������
     * @param null
     * @return boolean
     */
    private boolean isParse()
    {
        if (flag == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * ��������
     * @param null
     * @return ���ؽ�������
     */
    public ConfigXMLParse parse() throws Exception
    {
        if (flag == 1)
        {
            return cxp;
        }
        else
        {
            cxp = new ConfigXMLParse();
            cxp.doParse();
            flag = 1;
            return cxp;
        }
    }


    public ConfigXMLParse()
    {
    }

    /**
     * ָ���ļ�·��
     * @param filepath �ļ�·��
     * @return null
     */
    public ConfigXMLParse(String filepath)
    {
        this.filePath = filepath;
    }

    /**
     * �������ݱ�������ļ�
     * @param null
     * @return null
     */
    public void doParse() throws Exception
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        StringReader sr = new StringReader(XReader.readConf("Config.xml"));
        document = db.parse(new InputSource(sr));
        //document = db.parse(filePath);
        root = document.getDocumentElement();

    }

    /**
     * �õ����е�User�ڵ�
     * @param null
     * @return ��NodeList�������е�User�ڵ�
     */
    public NodeList getAllUser() throws Exception
    {
        NodeList nl = XPathAPI.selectNodeList(root, XQLUser);
        return nl;
    }

    /**
     * �õ�ָ����ֵ
     * @param ָ����TAG
     * @return String
     */
    private String getSystemValue(String relation) throws Exception
    {
        String result = XMLPathTool.getValue(root, relation);
        return (result == null) ? "" : result;
    }

    /**
     * �õ�DefaultDateFormatֵ
     * @param null
     * @return String
     */
    public String getDefaultDateFormat() throws Exception
    {
        return getSystemValue("System/DefaultDateFormat");
    }

    /**
     * �õ�DBDateFormatֵ
     * @param null
     * @return String
     */
    public String getDBDateFormat() throws Exception
    {
        return getSystemValue("System/DBDateFormat");
    }

    /**
     * �õ�ReportDateֵ
     * @param null
     * @return String
     */
    public String getReportDate() throws Exception
    {
        return getSystemValue("System/ReportDate");
    }

    /**
     * �õ�FAFlagֵ
     * @param null
     * @return String
     */
    public String getFAFlag() throws Exception
    {
        return getSystemValue("System/FAFlag");
    }

    /**
     * �õ�ManageComֵ
     * @param null
     * @return String
     */
    public String getManageCom() throws Exception
    {
        return getSystemValue("System/Authorize/ManageCom");
    }

    /**
     * �õ�Personֵ
     * @param null
     * @return String
     */
    public String getPerson() throws Exception
    {
        return getSystemValue("System/Authorize/Person");
    }

    /**
     * ����û�Ȩ��
     * @param PersonID �û���
     * @param ModuleID ģ����
     * @return ����Ȩ�� boolean
     * @throws null
     */

    public boolean getPurview(String personID, String moduleID) throws
            Exception
    {
        Object ob = new Object();
        return ((Purview) ob).checkPurview(personID, moduleID);
    }


    /**
     * �õ����е�Table�ڵ�
     * @param null
     * @return NodeList
     */
    public NodeList getAllTables() throws Exception
    {
        NodeList nl = XPathAPI.selectNodeList(root, "System/ReportTables/Table");
        return nl;
    }

    /**
     * �õ�ָ����Tableֵ
     * @param �ڵ� table
     * @return String
     */
    private String getTable(Node table) throws Exception
    {
        return XMLPathTool.getValue(table);
    }

    /**
     * ��ָ���Ľڵ㸳ֵ
     * @param Node ָ���Ľڵ�
     * @param String ָ���ڵ��ֵ
     * @return null
     */
    public void setNodeValue(Node node, String value) throws Exception
    {
        Node textNode = document.createTextNode(value);
        node.appendChild(textNode);
    }

    /**
     * ��һ���½ڵ�,������
     * @param start ��ʼ�ڵ�
     * @param name �½ڵ���
     * @return �½ڵ�
     */
    private Node addNodeNewLine(Node start, String name)
    {
        Document document = start.getOwnerDocument();
        Node newLine = document.createTextNode("\n");
        //    Node newLine2=document.createTextNode("\n");
        Node newNode = document.createElement(name);

        //start.appendChild(newLine);
        start.appendChild(newNode);
        start.appendChild(newLine);

        return newNode;
    }

    /**
     * �����û����ã��û�ID��������㿪ʼʱ�䣬����������ʱ�䣩
     * @param String QueryStartDate ������㿪ʼʱ��
     * @param String QueryEndDate ����������ʱ��
     * @return null
     */
    public void save(String userId, String queryStartDate, String queryEndDate) throws
            Exception
    {
//      ConfigXMLParse parse = new ConfigXMLParse();
//      parse.doParse();
        ConfigXMLParse parse = parse();
        NodeList nl = parse.getAllUser();
        Node userNode = null;
        for (int i = 0; i < nl.getLength(); i++)
        {
            String idvalue = XMLPathTool.getValue(nl.item(i), "ID");
            if (idvalue.equals(userId))
            {
                userNode = nl.item(i);
                break;
            }
        }
        Node usersNode = XPathAPI.selectSingleNode(root, XQLUsers);
        Node userIdNode = null;
        Node hobbyNode = null;
        Node themesNode = null;
        Node queryStartDateNode = null;
        Node queryEndDateNode = null;
        if (userNode == null) //����������û��ڵ�
        {
            userNode = addNodeNewLine(usersNode, "User");
            userIdNode = addNodeNewLine(userNode, "ID");
            queryStartDateNode = addNodeNewLine(userNode, "QueryStartDate");
            queryEndDateNode = addNodeNewLine(userNode, "QueryEndDate");
            setNodeValue(userIdNode, userId);
            setNodeValue(queryStartDateNode, queryStartDate);
            setNodeValue(queryEndDateNode, queryEndDate);
        }
        else //��������û��ڵ�
        {
            queryStartDateNode = XPathAPI.selectSingleNode(userNode,
                    "QueryStartDate");
            queryEndDateNode = XPathAPI.selectSingleNode(userNode,
                    "QueryEndDate");
            //���㿪ʼʱ��
            if (queryStartDateNode == null) //�����ڼ��㿪ʼʱ��ڵ�
            {
                queryStartDateNode = addNodeNewLine(userNode, "QueryStartDate");
                setNodeValue(queryStartDateNode, queryStartDate);
            }
            else //���ڼ��㿪ʼʱ��ڵ�
            {
                XMLPathTool.setValue(queryStartDateNode, queryStartDate);
            }
            //�������ʱ��
            if (queryEndDateNode == null) //�����ڼ������ʱ��ڵ�
            {
                queryEndDateNode = addNodeNewLine(userNode, "QueryEndDate");
                setNodeValue(queryEndDateNode, queryEndDate);
            }
            else //���ڼ������ʱ��ڵ�
            {
                XMLPathTool.setValue(queryEndDateNode, queryEndDate);
            }
        }
    }

    /**
     * �趨DefaultDateFormatֵ
     * @param String value ָ����ֵ
     * @return null
     */
    public void setDefaultDateFormat(String value) throws Exception
    {
//      ConfigXMLParse parse = new ConfigXMLParse();
//      parse.doParse();
        ConfigXMLParse parse = parse();
        Node sysnode = XPathAPI.selectSingleNode(root, XQLSystem);
        Node node = XPathAPI.selectSingleNode(root, XQLDefaultDateFormat);
        if (node != null)
        {
            XMLPathTool.setValue(node, value);
        }
        else
        {
            Node newNode = addNodeNewLine(sysnode, "DefaultDateFormat");
            setNodeValue(node, value);
        }
    }

    /**
     * �趨DBDateFormatֵ
     * @param String value ָ����ֵ
     * @return null
     */
    public void setDBDateFormat(String value) throws Exception
    {
//          ConfigXMLParse parse = new ConfigXMLParse();
//          parse.doParse();
        ConfigXMLParse parse = parse();
        Node sysnode = XPathAPI.selectSingleNode(root, XQLSystem);
        Node node = XPathAPI.selectSingleNode(root, XQLDBDateFormat);
        if (node != null)
        {
            XMLPathTool.setValue(node, value);
        }
        else
        {
            Node newNode = addNodeNewLine(sysnode, "DBDateFormat");
            setNodeValue(node, value);
        }
    }

    /**
     * �趨ReportDateֵ
     * @param String value ָ����ֵ
     * @return null
     */
    public void setReportDate(String value) throws Exception
    {
//      ConfigXMLParse parse = new ConfigXMLParse();
//      parse.doParse();
        ConfigXMLParse parse = parse();
        Node sysnode = XPathAPI.selectSingleNode(root, XQLSystem);
        Node node = XPathAPI.selectSingleNode(root, XQLReportDate);
        if (node != null)
        {
            XMLPathTool.setValue(node, value);
        }
        else
        {
            Node newNode = addNodeNewLine(sysnode, "ReportDate");
            setNodeValue(node, value);
        }
    }

    /**
     * �趨FAFlagֵ
     * @param String value ָ����ֵ
     * @return null
     */
    public void setFAFlag(String value) throws Exception
    {
//      ConfigXMLParse parse = new ConfigXMLParse();
//      parse.doParse();
        ConfigXMLParse parse = parse();
        Node sysnode = XPathAPI.selectSingleNode(root, XQLSystem);
        Node node = XPathAPI.selectSingleNode(root, XQLFAFlag);
        if (node != null)
        {
            XMLPathTool.setValue(node, value);
        }
        else
        {
            Node newNode = addNodeNewLine(sysnode, "FAFlag");
            setNodeValue(node, value);
        }
    }

    /**
     * �趨Themesֵ
     * @param String userid �û���id
     * @param String value ָ����ֵ
     * @return null
     */
    public void setUserThemes(String userid, String value) throws Exception
    {
        ConfigXMLParse parse = parse();
//      ConfigXMLParse parse = new ConfigXMLParse();
//      parse.doParse();
        Node usersNode = XPathAPI.selectSingleNode(root, XQLUsers);
        NodeList nl = parse.getAllUser();

        Node userNode = null;
        for (int i = 0; i < nl.getLength(); i++)
        {
            String idvalue = XMLPathTool.getValue(nl.item(i), "ID");
            if (idvalue.equals(userid))
            {
                userNode = nl.item(i);
                break;
            }
        }
        Node hobbyNode = null;
        Node themesNode = null;
        if (userNode == null) //�����ڴ��û�
        {
            userNode = addNodeNewLine(usersNode, "User");
            Node idNode = addNodeNewLine(userNode, "ID");
            setNodeValue(idNode, userid);
            hobbyNode = addNodeNewLine(userNode, "Hobby");
            themesNode = addNodeNewLine(hobbyNode, "Themes");
            setNodeValue(themesNode, value);
        }
        else //�Ѿ����ڴ��û�
        {
            hobbyNode = XPathAPI.selectSingleNode(userNode, "Hobby");
            if (hobbyNode == null) //�������<Hobby>
            {
                hobbyNode = addNodeNewLine(userNode, "Hobby");
                themesNode = addNodeNewLine(hobbyNode, "Themes");
                setNodeValue(themesNode, value);
            }
            else //����Ѿ�����<Hobby>
            {
                themesNode = XPathAPI.selectSingleNode(hobbyNode, "Themes");
                if (themesNode != null) //����<Themes>
                {
                    XMLPathTool.setValue(themesNode, value);
                }
                else //������<Themes>
                {
                    themesNode = addNodeNewLine(hobbyNode, "Themes");
                    setNodeValue(themesNode, value);
                }
            }
        }
    }

    /**
     * �����û�������
     * @param Node ��Ҫ����Ķ���ĸ��ڵ�
     * @return null
     */
    public void saveToFile(Node test) throws Exception
    {
        XWriter.writeConf(XMLPathTool.toString(test), "Config.xml");
    }


    /**
     * �趨Tableֵ
     * @param String Table ָ����Table��
     * @return null
     */
    public void addTable(String tabName) throws Exception
    {
//      ConfigXMLParse parse = new ConfigXMLParse();
//      parse.doParse();
        ConfigXMLParse parse = parse();
        Node tables = XPathAPI.selectSingleNode(root, XQLReportTables);
        NodeList nl = parse.getAllTables();
        int flag = 0;
        for (int i = 0; i < nl.getLength(); i++)
        {
            String tabname = XMLPathTool.getValue(nl.item(i));
            if (tabname.equals(tabName)) //����Ѿ��������־Ϊ1
            {
                flag = 1;
            }
        }
        if (flag == 0) //��������������
        {
            Node table = addNodeNewLine(tables, "Table");
            setNodeValue(table, tabName);
        }
    }

    /**
     * ɾ���û�User userId
     * @param String ָ�����û�ID
     * @return boolean ɾ���Ƿ�ɹ���true��ʾɾ���ɹ� false��ʾɾ��ʧ��
     */
    public boolean delUser(String userId) throws Exception
    {
        Logger logger = XTLogger.getLogger(ConfigXMLParse.class);
        ConfigXMLParse parse = parse();
        boolean flag = true;
        Node usersNode = XPathAPI.selectSingleNode(root, XQLUsers);
        Node userNode = null;
        NodeList nl = parse.getAllUser();
        for (int i = 0; i < nl.getLength(); i++)
        {
            String userid = XMLPathTool.getValue(nl.item(i), "ID");
            if (userid.equals(userId))
            {
                userNode = nl.item(i);
                break;
            }
        }
        if (userNode != null)
        {
            usersNode.removeChild(userNode);
            logger.info("delete user " + userId + " successfully.");
            flag = true;
        }
        else
        {
            logger.info("the user you want to delete " + userId +
                        " is not exist.");
            flag = false;
        }
        return flag;
    }

    /**
     * ����û�User�Ƿ��Ѿ�������XML��
     * @param String ָ����UserID
     * @return boolean true��ʾ�û����ڣ�false��ʾ�û�������
     */
    public boolean checkUser(String userId) throws Exception
    {
        ConfigXMLParse parse = parse();
        Node usersNode = XPathAPI.selectSingleNode(root, "Users");
        NodeList nl = parse.getAllUser();
        Node userNode = null;

        for (int i = 0; i < nl.getLength(); i++)
        {
            String userid = XMLPathTool.getValue(nl.item(i), "ID");
            if (userid.equals(userId))
            {
                userNode = nl.item(i);
                break;
            }
        }
        if (userNode == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * ����û�
     * @param String ָ�����û�UserID
     * @return boolean true��ʾ��ӳɹ���false��ʾ���ʧ��
     */
    public boolean addUser(String userId) throws Exception
    {
        Logger logger = XTLogger.getLogger(ConfigXMLParse.class);
        ConfigXMLParse parse = parse();
        if (checkUser(userId)) //�û��Ѿ�����
        {
            logger.info("the user " + userId + " is exist.");
            return false;
        }
        else //�û������ڣ�����һ���µ��û��ڵ㣬����ֵ
        {
            Node usersNode = XPathAPI.selectSingleNode(root, "Users");
            Node userNode = addNodeNewLine(usersNode, "User");
            Node idNode = addNodeNewLine(userNode, "ID");
            setNodeValue(idNode, userId);
            logger.info("the user " + userId + " add successfully.");
            return true;
        }
    }


    /**
     * ����������������������
     * @param args[]
     * @return null
     */
    public static void main(String args[]) throws Exception
    {
//        Logger logger = XTLogger.getLogger(ConfigXMLParse.class);
//        ConfigXMLParse test = new ConfigXMLParse();
//        test.doParse();
//        test.cxp = test;
//        test.flag = 1;
//        logger.info(test.getManageCom());
//        logger.info(test.getPerson());

//    NodeList nl = test.getAllTables();
//
//    test.setDefaultDateFormat("1");
//    test.setDBDateFormat("bbb");
//    test.setFAFlag("ccc");
//    test.setReportDate("ddd");
//    test.setReportDate("changed it again");
//    test.save("test","��ʼʱ��","����ʱ��");
//    //test.delUser("lala");
//    test.addTable("test");
//    test.setUserThemes("houzw","change");
//    //test.delUser("new");
//    test.addUser("new");
//    test.save("new","new start time","new end time");
//    test.setUserThemes("mm","mmmmmmmm");
//    //test.save("new","haha","hehe");
//    test.saveToFile(test.root);
        //XWriter.writeConf(XMLPathTool.toString(test.root),"Config.xml");
    }
}


//////////////////////////////////////////////////////////////////////////////
class User
{
    Node user;
    Document document;

    public User()
    {
    }

    public User(Node node)
    {
        this.user = node;
        document = user.getOwnerDocument();
    }

    /**
     * �õ��û��Ĳ���
     * @param ָ���Ĳ���
     * @return ��Ӧ�Ĳ�����ȡֵ
     */
    private String getUserValue(String relativeXQL) throws Exception
    {
        String result = XMLPathTool.getValue(user, relativeXQL);
        return (result == null) ? "" : result;
    }

    /**
     * �õ��û���id
     * @param null
     * @return String
     */
    public String getID() throws Exception
    {
        return getUserValue("ID");
    }

    /**
     * �õ��û��ļ��㿪ʼʱ��
     * @param null
     * @return String
     */
    public String getStartDate() throws Exception
    {
        return getUserValue("QueryStartDate");
    }

    /**
     * �õ��û��ļ������ʱ��
     * @param null
     * @return String
     */
    public String getEndDate() throws Exception
    {
        return getUserValue("QueryEndDate");
    }

    /**
     * �õ��û���Themes
     * @param null
     * @return String
     */
    public String getThemes() throws Exception
    {
        return getUserValue("Hobby/Themes");
    }

}

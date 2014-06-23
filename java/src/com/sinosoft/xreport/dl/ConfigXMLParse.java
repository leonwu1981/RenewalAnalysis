package com.sinosoft.xreport.dl;

/**
 * 这个类用来完成用户配置报表计算时间的XML的解析
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


    //文件的位置
    //private String filePath = "D:\\xreport\\src\\com\\sinosoft\\xreport\\dl\\Config.xml";
    private String filePath = "d:\\Config.xml"; //for test
    private Document document;
    private Node root;

    private InputSource in;

    private int flag = 0; //标志位，用来判断XML文件是否已经解析过
    static ConfigXMLParse cxp = null; //保存解析过的对象


    /**
     * 判断是否已经解析过
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
     * 解析函数
     * @param null
     * @return 返回解析对象
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
     * 指定文件路径
     * @param filepath 文件路径
     * @return null
     */
    public ConfigXMLParse(String filepath)
    {
        this.filePath = filepath;
    }

    /**
     * 解析数据表的描述文件
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
     * 得到所有的User节点
     * @param null
     * @return 以NodeList返回所有的User节点
     */
    public NodeList getAllUser() throws Exception
    {
        NodeList nl = XPathAPI.selectNodeList(root, XQLUser);
        return nl;
    }

    /**
     * 得到指定的值
     * @param 指定的TAG
     * @return String
     */
    private String getSystemValue(String relation) throws Exception
    {
        String result = XMLPathTool.getValue(root, relation);
        return (result == null) ? "" : result;
    }

    /**
     * 得到DefaultDateFormat值
     * @param null
     * @return String
     */
    public String getDefaultDateFormat() throws Exception
    {
        return getSystemValue("System/DefaultDateFormat");
    }

    /**
     * 得到DBDateFormat值
     * @param null
     * @return String
     */
    public String getDBDateFormat() throws Exception
    {
        return getSystemValue("System/DBDateFormat");
    }

    /**
     * 得到ReportDate值
     * @param null
     * @return String
     */
    public String getReportDate() throws Exception
    {
        return getSystemValue("System/ReportDate");
    }

    /**
     * 得到FAFlag值
     * @param null
     * @return String
     */
    public String getFAFlag() throws Exception
    {
        return getSystemValue("System/FAFlag");
    }

    /**
     * 得到ManageCom值
     * @param null
     * @return String
     */
    public String getManageCom() throws Exception
    {
        return getSystemValue("System/Authorize/ManageCom");
    }

    /**
     * 得到Person值
     * @param null
     * @return String
     */
    public String getPerson() throws Exception
    {
        return getSystemValue("System/Authorize/Person");
    }

    /**
     * 检查用户权限
     * @param PersonID 用户名
     * @param ModuleID 模块名
     * @return 返回权限 boolean
     * @throws null
     */

    public boolean getPurview(String personID, String moduleID) throws
            Exception
    {
        Object ob = new Object();
        return ((Purview) ob).checkPurview(personID, moduleID);
    }


    /**
     * 得到所有的Table节点
     * @param null
     * @return NodeList
     */
    public NodeList getAllTables() throws Exception
    {
        NodeList nl = XPathAPI.selectNodeList(root, "System/ReportTables/Table");
        return nl;
    }

    /**
     * 得到指定的Table值
     * @param 节点 table
     * @return String
     */
    private String getTable(Node table) throws Exception
    {
        return XMLPathTool.getValue(table);
    }

    /**
     * 给指定的节点赋值
     * @param Node 指定的节点
     * @param String 指定节点的值
     * @return null
     */
    public void setNodeValue(Node node, String value) throws Exception
    {
        Node textNode = document.createTextNode(value);
        node.appendChild(textNode);
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

        //start.appendChild(newLine);
        start.appendChild(newNode);
        start.appendChild(newLine);

        return newNode;
    }

    /**
     * 保存用户配置（用户ID，报表计算开始时间，报表计算结束时间）
     * @param String QueryStartDate 报表计算开始时间
     * @param String QueryEndDate 报表计算结束时间
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
        if (userNode == null) //不存在这个用户节点
        {
            userNode = addNodeNewLine(usersNode, "User");
            userIdNode = addNodeNewLine(userNode, "ID");
            queryStartDateNode = addNodeNewLine(userNode, "QueryStartDate");
            queryEndDateNode = addNodeNewLine(userNode, "QueryEndDate");
            setNodeValue(userIdNode, userId);
            setNodeValue(queryStartDateNode, queryStartDate);
            setNodeValue(queryEndDateNode, queryEndDate);
        }
        else //存在这个用户节点
        {
            queryStartDateNode = XPathAPI.selectSingleNode(userNode,
                    "QueryStartDate");
            queryEndDateNode = XPathAPI.selectSingleNode(userNode,
                    "QueryEndDate");
            //计算开始时间
            if (queryStartDateNode == null) //不存在计算开始时间节点
            {
                queryStartDateNode = addNodeNewLine(userNode, "QueryStartDate");
                setNodeValue(queryStartDateNode, queryStartDate);
            }
            else //存在计算开始时间节点
            {
                XMLPathTool.setValue(queryStartDateNode, queryStartDate);
            }
            //计算结束时间
            if (queryEndDateNode == null) //不存在计算结束时间节点
            {
                queryEndDateNode = addNodeNewLine(userNode, "QueryEndDate");
                setNodeValue(queryEndDateNode, queryEndDate);
            }
            else //存在计算结束时间节点
            {
                XMLPathTool.setValue(queryEndDateNode, queryEndDate);
            }
        }
    }

    /**
     * 设定DefaultDateFormat值
     * @param String value 指定的值
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
     * 设定DBDateFormat值
     * @param String value 指定的值
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
     * 设定ReportDate值
     * @param String value 指定的值
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
     * 设定FAFlag值
     * @param String value 指定的值
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
     * 设定Themes值
     * @param String userid 用户的id
     * @param String value 指定的值
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
        if (userNode == null) //不存在此用户
        {
            userNode = addNodeNewLine(usersNode, "User");
            Node idNode = addNodeNewLine(userNode, "ID");
            setNodeValue(idNode, userid);
            hobbyNode = addNodeNewLine(userNode, "Hobby");
            themesNode = addNodeNewLine(hobbyNode, "Themes");
            setNodeValue(themesNode, value);
        }
        else //已经存在此用户
        {
            hobbyNode = XPathAPI.selectSingleNode(userNode, "Hobby");
            if (hobbyNode == null) //如果不含<Hobby>
            {
                hobbyNode = addNodeNewLine(userNode, "Hobby");
                themesNode = addNodeNewLine(hobbyNode, "Themes");
                setNodeValue(themesNode, value);
            }
            else //如果已经含有<Hobby>
            {
                themesNode = XPathAPI.selectSingleNode(hobbyNode, "Themes");
                if (themesNode != null) //存在<Themes>
                {
                    XMLPathTool.setValue(themesNode, value);
                }
                else //不存在<Themes>
                {
                    themesNode = addNodeNewLine(hobbyNode, "Themes");
                    setNodeValue(themesNode, value);
                }
            }
        }
    }

    /**
     * 保存用户的配置
     * @param Node 所要保存的对象的根节点
     * @return null
     */
    public void saveToFile(Node test) throws Exception
    {
        XWriter.writeConf(XMLPathTool.toString(test), "Config.xml");
    }


    /**
     * 设定Table值
     * @param String Table 指定的Table名
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
            if (tabname.equals(tabName)) //如果已经存在则标志为1
            {
                flag = 1;
            }
        }
        if (flag == 0) //如果不存在则加上
        {
            Node table = addNodeNewLine(tables, "Table");
            setNodeValue(table, tabName);
        }
    }

    /**
     * 删除用户User userId
     * @param String 指定的用户ID
     * @return boolean 删除是否成功，true表示删除成功 false表示删除失败
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
     * 检查用户User是否已经存在在XML中
     * @param String 指定的UserID
     * @return boolean true表示用户存在，false表示用户不存在
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
     * 添加用户
     * @param String 指定的用户UserID
     * @return boolean true表示添加成功，false表示添加失败
     */
    public boolean addUser(String userId) throws Exception
    {
        Logger logger = XTLogger.getLogger(ConfigXMLParse.class);
        ConfigXMLParse parse = parse();
        if (checkUser(userId)) //用户已经存在
        {
            logger.info("the user " + userId + " is exist.");
            return false;
        }
        else //用户不存在，建立一个新的用户节点，并赋值
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
     * 主函数－－测试其他函数
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
//    test.save("test","开始时间","结束时间");
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
     * 得到用户的参数
     * @param 指定的参数
     * @return 相应的参数的取值
     */
    private String getUserValue(String relativeXQL) throws Exception
    {
        String result = XMLPathTool.getValue(user, relativeXQL);
        return (result == null) ? "" : result;
    }

    /**
     * 得到用户的id
     * @param null
     * @return String
     */
    public String getID() throws Exception
    {
        return getUserValue("ID");
    }

    /**
     * 得到用户的计算开始时间
     * @param null
     * @return String
     */
    public String getStartDate() throws Exception
    {
        return getUserValue("QueryStartDate");
    }

    /**
     * 得到用户的计算结束时间
     * @param null
     * @return String
     */
    public String getEndDate() throws Exception
    {
        return getUserValue("QueryEndDate");
    }

    /**
     * 得到用户的Themes
     * @param null
     * @return String
     */
    public String getThemes() throws Exception
    {
        return getUserValue("Hobby/Themes");
    }

}

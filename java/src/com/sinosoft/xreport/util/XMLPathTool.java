//*******************************************************************
 // Java  XMLPATHTool:
 //	Name        :
 //      Test content:
 //	Comment	    :
 //	Date        :
 //********************************************************************
  //1.得到document 对象;2.传入xpath表达式3.返回结点集
package com.sinosoft.xreport.util;

// Imported Serializer classes
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

// Imported JAVA API for XML Parsing 1.0 classes
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
//import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**XPATH应用类
 * @author  zhuzhen
 * @modified by wuxiao
 * @version 1.0
 **/
public class XMLPathTool
{

    /**
     * 对应的Dom树
     **/
    private Document sourceDom;

    /**
     * Url资源
     **/
    private String Url;

    public XMLPathTool(String Url)
    {
        this.Url = Url;
        try
        {
            if (sourceDom == null)
            {
                sourceDom = getDocument(Url);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public XMLPathTool(File aFile)
    {
        try
        {
            if (sourceDom == null)
            {
                sourceDom = getDocument(aFile);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 获取文档的document对象。
     * @param  Url 输入的文件名。
     * @return 文件名对应的document对象。
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */

    ////////////////lixy 20030310////////////////
    private Document getDocument(String Url) throws
            ParserConfigurationException, SAXException, IOException
            {
//        InputSource in = new InputSource(new FileInputStream(fileName));
            DocumentBuilderFactory dfactory = DocumentBuilderFactory.
                                              newInstance();
            dfactory.setNamespaceAware(true);
            /////////lixy 20030312 使用解析二进制流的方式//////////////
            InputSource in = new InputSource(new StringReader(Url));
            Document doc = dfactory.newDocumentBuilder().parse(in);
            return doc;
    }

            private Document getDocument(File aFile) throws
            ParserConfigurationException, SAXException, IOException
            {
            Url = aFile.getAbsolutePath();
            InputSource in = new InputSource(new FileInputStream(Url));
            DocumentBuilderFactory dfactory = DocumentBuilderFactory.
                                              newInstance();
            dfactory.setNamespaceAware(true);
            Document doc = dfactory.newDocumentBuilder().parse(in);

            return doc;
    }
            /**
             * 取得当前Document
             * @return 当前Document
             */
            public Document getDocument()
    {
        return sourceDom;
    }

    /**
     * 设置当前Document
     * @param doc Document对象
     */
    public void setDocument(Document doc)
    {
        sourceDom = doc;
    }

    /**
     *分析xpath串。
     *@param xpathString  要进行分析的xpath表达式。
     *@return 结果集。
     */
    public XObject parseX(String xpathString)
    {
        try
        {
            if (sourceDom == null)
            {
                sourceDom = getDocument(Url);
            }
            Element root = sourceDom.getDocumentElement();
            XObject xobj = XPathAPI.eval(root, xpathString);
            return xobj;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *分析xpath串。
     *@param xpathString  要进行分析的xpath表达式。
     *@return 节点。
     */
    public Node getNode(String xpathString)
    {
        try
        {
            if (sourceDom == null)
            {
                sourceDom = getDocument(Url);
            }
            Element root = sourceDom.getDocumentElement();
            Node node = XPathAPI.selectSingleNode(root, xpathString);
            return node;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 分析xpath串。
     * @param xpathString  要进行分析的xpath表达式。
     * @return 结果集。
     **/
    public NodeList parseN(String xpathString)
    {
        try
        {
            if (sourceDom == null)
            {
                sourceDom = getDocument(Url);
            }
            Element root = sourceDom.getDocumentElement();
            return XPathAPI.selectNodeList(root, xpathString);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 分析xpath串。
     * @param xpathString  要进行分析的xpath表达式。
     * @param node 开始节点
     * @return 结果集。
     **/
    public NodeList parseN(Node node, String xpathString)
    {
        Element root;
        try
        {
            if (sourceDom == null)
            {
                sourceDom = getDocument(Url);
            }
            if (node == null)
            {
                root = sourceDom.getDocumentElement();
            }
            else
            {
                root = (Element) node;
            }
            return XPathAPI.selectNodeList(root, xpathString);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获得根节点
     * @return 根节点
     */
    public Node getRootNode()
    {
        Element root = sourceDom.getDocumentElement();
        return (Node) root;
    }

    /**
     * 返回包含在文本节点中的连续文本值，可以删除空白字符
     * <p>
     * Takes a node as input and merges all its immediate text nodes into a
     * string.  If the strip whitespace flag is set, whitespace at the beggining
     * and end of each merged text node will be removed
     * @param node                  需要求值节点
     * @return                      包含在节点中的连续文本值
     **/
    public static String getTextContents(Node node)
    {
        NodeList childNodes;
        StringBuffer contents = new StringBuffer();

        childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++)
        {
            if (childNodes.item(i).getNodeType() == Node.TEXT_NODE)
            {
                contents.append(childNodes.item(i).getNodeValue());
            }
        }
        return contents.toString();
    }

    /**
     * 返回符合XPath查询表达式条件的第一个节点的文本值
     * <p>
     * Takes a context node and an xpath expression and finds a matching
     * node. The text contents of this node are returned as a string
     *
     * @param node    起始节点, 如果此参数等于null，则从跟节点开始
     * @param xql     XPath查询表达式
     * @return        符合XPath查询表达式条件的第一个节点的文本值
     * @throws Exception
     **/
    public static String getValue(Node node, String xql) throws Exception
    {
        if ((xql == null) || (xql.length() == 0))
        {
            throw new Exception("findValue called with empty xql statement");
        }
        if (node == null)
        {
//            Element root = sourceDom.getDocumentElement();
//            node = (Node)root;
            throw new Exception("findValue called with null node");
        }
        return getTextContents(XPathAPI.selectSingleNode(node, xql));
    }

    /**
     * 返回符合XPath查询表达式条件的第一个节点的文本值
     * <p>
     * Takes a context node and an xpath expression and finds a matching
     * node. The text contents of this node are returned as a string
     *
     * @param node    起始节点, 如果此参数等于null，则从跟节点开始
     * @return        符合XPath查询表达式条件的第一个节点的文本值
     * @throws Exception
     **/
    public static String getValue(Node node) throws Exception
    {
        return getTextContents(node);
    }

    /**
     * 返回符合XPath查询表达式条件的第一个节点的属性值
     * <p>
     * Takes a context node and an xpath expression and finds a matching
     * node. The text contents of this node are returned as a string
     *
     * @param node     起始节点, 如果此参数等于null，则从跟节点开始
     * @param attrName 属性名称
     * @param xql      XPath查询表达式
     * @return         符合XPath查询表达式条件的第一个节点的属性值
     * @throws Exception
     **/
    public static String getAttrValue(Node node, String attrName, String xql) throws
            Exception
    {
        if ((xql == null) || (xql.length() == 0))
        {
            throw new Exception("findValue called with empty xql statement");
        }
        if (node == null)
        {
//            Element root = sourceDom.getDocumentElement();
//            node = (Node)root;
            throw new Exception("findValue called with null node");
        }
        Node targetNode = XPathAPI.selectSingleNode(node, xql);
        Element targetElem;

        targetElem = (Element) targetNode;
        return targetElem.getAttribute(attrName);
    }

    /**
     * 返回节点的属性值
     * <p>
     * Takes a context node and an xpath expression and finds a matching
     * node. The text contents of this node are returned as a string
     *
     * @param node     起始节点
     * @param attrName 属性名称
     * @return         节点的属性值
     * @throws Exception
     **/
    public static String getAttrValue(Node node, String attrName) throws
            Exception
    {
        if (node == null)
        {
            throw new Exception("findValue called with null node");
        }
        Element targetElem;

        targetElem = (Element) node;
        return targetElem.getAttribute(attrName);
    }

    /**
     * 查找符合XPath查询表达式条件的第一个节点，并设值文本值
     * <p>
     * Takes a context node and an XPath expression.  The matching node gets a
     * text node appending containing the contents of the value string.  The
     * node matching the XPath expression is returned
     *
     * @param startNode  起始节点
     * @param value      文本值
     * @param xql        XPath表达式
     * @return           修改后的节点
     * @throws Exception
     **/
    public static Node setValue(Node startNode, String value, String xql) throws
            Exception
    {
        Node targetNode = XPathAPI.selectSingleNode(startNode, xql);
        NodeList children = targetNode.getChildNodes();
        int index = 0;
        int length = children.getLength();

        // Remove all of the current contents
        for (index = 0; index < length; index++)
        {
            targetNode.removeChild(children.item(index));
        }

        // Add in the new value
        Document doc = startNode.getOwnerDocument();
        targetNode.appendChild(doc.createTextNode(value));

        return targetNode;
    }

    /**
     * 查找符合XPath查询表达式条件的第一个节点，并设置文本值
     * <p>
     * Takes a context node and an XPath expression.  The matching node gets a
     * text node appending containing the contents of the value string.  The
     * node matching the XPath expression is returned
     *
     * @param startNode  起始节点
     * @param value      文本值
     * @return           修改后的节点
     * @throws Exception
     **/
    public static Node setValue(Node startNode, String value) throws Exception
    {
//        Node targetNode = XPathAPI.selectSingleNode( startNode, xql );
        NodeList children = startNode.getChildNodes();
        int index = 0;
        int length = children.getLength();

        // Remove all of the current contents
        for (index = 0; index < length; index++)
        {
            startNode.removeChild(children.item(index));
        }

        // Add in the new value
        Document doc = startNode.getOwnerDocument();
        startNode.appendChild(doc.createTextNode(value));

        return startNode;
    }

    /**
     * 查找符合XPath查询表达式条件的第一个节点，并设置属性值
     * <p>
     * Takes a context node and an XPath expression.  The matching node gets a
     * text node appending containing the contents of the value string.  The
     * node matching the XPath expression is returned
     *
     * @param startNode  起始节点
     * @param attrName   文本值
     * @param attrValue  文本值
     * @param xql        XPath表达式
     * @return           修改后的节点
     * @throws Exception
     **/
    public static Node setAttrValue(Node startNode, String attrName,
                                    String attrValue, String xql) throws
            Exception
    {
        Node targetNode = XPathAPI.selectSingleNode(startNode, xql);
        NodeList children = targetNode.getChildNodes();
        int index = 0;
        int length = children.getLength();
        Element targetElem;

        targetElem = (Element) targetNode;
        targetElem.setAttribute(attrName, attrValue);
        return (Node) targetElem;
    }

    /**
     * 查找符合XPath查询表达式条件的第一个节点，并设置属性值
     * <p>
     * Takes a context node and an XPath expression.  The matching node gets a
     * text node appending containing the contents of the value string.  The
     * node matching the XPath expression is returned
     *
     * @param startNode  起始节点
     * @param attrName   文本值
     * @param attrValue  文本值
     * @return           修改后的节点
     * @throws Exception
     **/
    public static Node setAttrValue(Node startNode, String attrName,
                                    String attrValue) throws Exception
    {
        Element targetElem;

        targetElem = (Element) startNode;
        targetElem.setAttribute(attrName, attrValue);
        return (Node) targetElem;
    }

    /**
     * 在文档指定位置添加一个新节点
     * <p>
     * Takes a context node, the name of the new node, and an XPath expression.
     * The new node is appended to the document at the point specified by the
     * context node and the XPath statement
     *
     * @param startNode   起始节点
     * @param name        新节点名（值）
     * @param xql         XPath表达式
     * @return            新建的节点
     * @throws Exception
     **/
    public static Node appendNode(Node startNode, String name, String xql) throws
            Exception
    {
        Node targetNode = XPathAPI.selectSingleNode(startNode, xql);
        Document doc = startNode.getOwnerDocument();

        Element newElement = doc.createElement(name);
        targetNode.appendChild((Node) newElement);
        return ((Node) newElement);
    }

    /**
     * 在文档指定位置添加一个新节点
     * <p>
     * Takes a context node, the name of the new node, and an XPath expression.
     * The new node is appended to the document at the point specified by the
     * context node and the XPath statement
     *
     * @param startNode   起始节点
     * @param name        新节点名（值）
     * @return            新建的节点
     * @throws Exception
     **/
    public static Node appendNode(Node startNode, String name) throws Exception
    {
        //Node targetNode = XPathAPI.selectSingleNode( startNode,xql );
        Document doc = startNode.getOwnerDocument();

        Element newElement = doc.createElement(name);
        startNode.appendChild((Node) newElement);
        return ((Node) newElement);
    }

    /**
     * 在文档指定位置删除一个节点
     * <p>
     * @param startNode   起始节点
     * @param xql         XPath表达式
     * @return            新建的节点
     * @throws Exception
     **/
    public static Node deleteNode(Node startNode, String xql) throws Exception
    {
        Node targetNode = XPathAPI.selectSingleNode(startNode, xql);
        Node parentNode = targetNode.getParentNode();
        return (parentNode.removeChild(targetNode));
    }

    /**
     * 在文档指定位置删除一个节点
     * <p>
     * @param startNode   起始节点
     * @return            新建的节点
     * @throws Exception
     **/
    public static Node deleteNode(Node startNode) throws Exception
    {
//        Node targetNode = XPathAPI.selectSingleNode( startNode,xql );
        Node parentNode = startNode.getParentNode();
        return (parentNode.removeChild(startNode));
    }

    /**
     * 把从指定节点开始内容转换为xml格式的字符串
     * <p>
     * Converts either the SubTree designated by an Element node or an
     * entire tree, specified by a Document object into an XML Text
     * representation.
     * @param node    DOM节点（文档或元素）
     * @return        xml格式的字符串
     * @throws Exception
     **/
    public static String toString(Node node) throws Exception
    {
        StringWriter writer = new StringWriter();
        XMLSerializer serial = new XMLSerializer();

        serial.setOutputCharStream(writer);
        OutputFormat format = new OutputFormat();
        format.setPreserveSpace(true);
        format.setEncoding("GB2312");
        serial.setOutputFormat(format);
        if (node.getNodeType() == Node.DOCUMENT_NODE)
        {
            serial.serialize((Document) node);
        }
        else
        {
            serial.serialize((Element) node);
        }
        return writer.toString();
    }

    /**
     * 把从指定节点开始内容保存到指定xml文件中
     * <p>
     * Converts either the SubTree designated by an Element node or an
     * entire tree, specified by a Document object into an XML Text
     * representation.
     * @param node    DOM节点（文档或元素）
     * @param fName   XML文件名
     * @return        xml格式的字符串
     * @throws Exception
     **/
    public static String toFile(Node node, String fName) throws Exception
    {
        FileWriter writer = new FileWriter(fName);
        XMLSerializer serial = new XMLSerializer();

        serial.setOutputCharStream(writer);
        OutputFormat format = new OutputFormat();
        format.setPreserveSpace(true);
        format.setEncoding("GB2312");
        serial.setOutputFormat(format);
        if (node.getNodeType() == Node.DOCUMENT_NODE)
        {
            serial.serialize((Document) node);
        }
        else
        {
            serial.serialize((Element) node);
        }
        return writer.toString();
    }

    /**
     * 把从指定节点开始内容保存到指定xml文件中
     * <p>
     * Converts either the SubTree designated by an Element node or an
     * entire tree, specified by a Document object into an XML Text
     * representation.
     * @param node    DOM节点（文档或元素）
     * @param fName   XML文件名
     * @return        xml格式的字符串
     * @throws Exception
     **/
    ////////////////lixy 20030310 向服务器写XML文件////////////////////////////
    public static String toServer(Node node, String fName) throws Exception
    {
        URL url = new URL(SysConfig.TRUEHOST + "WriteConf.jsp?file=" + fName);
        URLConnection uc = url.openConnection();
        uc.setDoOutput(true);
        OutputStream out = uc.getOutputStream();
        XMLSerializer serial = new XMLSerializer();
        serial.setOutputByteStream(out);
        OutputFormat format = new OutputFormat();
        format.setPreserveSpace(true);
        format.setEncoding("GB2312");
        serial.setOutputFormat(format);
        if (node.getNodeType() == Node.DOCUMENT_NODE)
        {
            serial.serialize((Document) node);
        }
        else
        {
            serial.serialize((Element) node);
        }
        InputStream in = uc.getInputStream();
        out.close();
        in.close();
        return null;
    }


    /**
     * 解析xml文件成为Dom树
     * @author Yang Yalin on 2003.3.14
     * @param file xml文件
     * @return 解析后的XML DOM树
     * @throws Exception 解析错误
     */
    public static Document parseFile(File file) throws Exception
    {

        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        Document doc = dfactory.newDocumentBuilder().parse(file);
        return doc;
    }

    /**
     * 解析xml文件成为Dom树
     * @author lixy on 2003.3.20
     * @param file xml文件
     * @return 解析后的XML DOM树
     * @throws Exception 解析错误
     */
    public static Document parseText(String content) throws Exception
    {

        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        InputSource in = new InputSource(new StringReader(content));
        Document doc = dfactory.newDocumentBuilder().parse(in);
        return doc;
    }


    public static void main(String args[])
    {
//        XMLPathTool xpath = new XMLPathTool(SysConfig.TRUEHOST +
//                                            "ReadReportMain.jsp");
//        try
//        {
//            System.out.println(xpath.toString(xpath.getNode("/")));
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
    }
}

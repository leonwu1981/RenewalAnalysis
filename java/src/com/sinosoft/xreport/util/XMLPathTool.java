//*******************************************************************
 // Java  XMLPATHTool:
 //	Name        :
 //      Test content:
 //	Comment	    :
 //	Date        :
 //********************************************************************
  //1.�õ�document ����;2.����xpath���ʽ3.���ؽ�㼯
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

/**XPATHӦ����
 * @author  zhuzhen
 * @modified by wuxiao
 * @version 1.0
 **/
public class XMLPathTool
{

    /**
     * ��Ӧ��Dom��
     **/
    private Document sourceDom;

    /**
     * Url��Դ
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
     * ��ȡ�ĵ���document����
     * @param  Url ������ļ�����
     * @return �ļ�����Ӧ��document����
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
            /////////lixy 20030312 ʹ�ý������������ķ�ʽ//////////////
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
             * ȡ�õ�ǰDocument
             * @return ��ǰDocument
             */
            public Document getDocument()
    {
        return sourceDom;
    }

    /**
     * ���õ�ǰDocument
     * @param doc Document����
     */
    public void setDocument(Document doc)
    {
        sourceDom = doc;
    }

    /**
     *����xpath����
     *@param xpathString  Ҫ���з�����xpath���ʽ��
     *@return �������
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
     *����xpath����
     *@param xpathString  Ҫ���з�����xpath���ʽ��
     *@return �ڵ㡣
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
     * ����xpath����
     * @param xpathString  Ҫ���з�����xpath���ʽ��
     * @return �������
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
     * ����xpath����
     * @param xpathString  Ҫ���з�����xpath���ʽ��
     * @param node ��ʼ�ڵ�
     * @return �������
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
     * ��ø��ڵ�
     * @return ���ڵ�
     */
    public Node getRootNode()
    {
        Element root = sourceDom.getDocumentElement();
        return (Node) root;
    }

    /**
     * ���ذ������ı��ڵ��е������ı�ֵ������ɾ���հ��ַ�
     * <p>
     * Takes a node as input and merges all its immediate text nodes into a
     * string.  If the strip whitespace flag is set, whitespace at the beggining
     * and end of each merged text node will be removed
     * @param node                  ��Ҫ��ֵ�ڵ�
     * @return                      �����ڽڵ��е������ı�ֵ
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
     * ���ط���XPath��ѯ���ʽ�����ĵ�һ���ڵ���ı�ֵ
     * <p>
     * Takes a context node and an xpath expression and finds a matching
     * node. The text contents of this node are returned as a string
     *
     * @param node    ��ʼ�ڵ�, ����˲�������null����Ӹ��ڵ㿪ʼ
     * @param xql     XPath��ѯ���ʽ
     * @return        ����XPath��ѯ���ʽ�����ĵ�һ���ڵ���ı�ֵ
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
     * ���ط���XPath��ѯ���ʽ�����ĵ�һ���ڵ���ı�ֵ
     * <p>
     * Takes a context node and an xpath expression and finds a matching
     * node. The text contents of this node are returned as a string
     *
     * @param node    ��ʼ�ڵ�, ����˲�������null����Ӹ��ڵ㿪ʼ
     * @return        ����XPath��ѯ���ʽ�����ĵ�һ���ڵ���ı�ֵ
     * @throws Exception
     **/
    public static String getValue(Node node) throws Exception
    {
        return getTextContents(node);
    }

    /**
     * ���ط���XPath��ѯ���ʽ�����ĵ�һ���ڵ������ֵ
     * <p>
     * Takes a context node and an xpath expression and finds a matching
     * node. The text contents of this node are returned as a string
     *
     * @param node     ��ʼ�ڵ�, ����˲�������null����Ӹ��ڵ㿪ʼ
     * @param attrName ��������
     * @param xql      XPath��ѯ���ʽ
     * @return         ����XPath��ѯ���ʽ�����ĵ�һ���ڵ������ֵ
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
     * ���ؽڵ������ֵ
     * <p>
     * Takes a context node and an xpath expression and finds a matching
     * node. The text contents of this node are returned as a string
     *
     * @param node     ��ʼ�ڵ�
     * @param attrName ��������
     * @return         �ڵ������ֵ
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
     * ���ҷ���XPath��ѯ���ʽ�����ĵ�һ���ڵ㣬����ֵ�ı�ֵ
     * <p>
     * Takes a context node and an XPath expression.  The matching node gets a
     * text node appending containing the contents of the value string.  The
     * node matching the XPath expression is returned
     *
     * @param startNode  ��ʼ�ڵ�
     * @param value      �ı�ֵ
     * @param xql        XPath���ʽ
     * @return           �޸ĺ�Ľڵ�
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
     * ���ҷ���XPath��ѯ���ʽ�����ĵ�һ���ڵ㣬�������ı�ֵ
     * <p>
     * Takes a context node and an XPath expression.  The matching node gets a
     * text node appending containing the contents of the value string.  The
     * node matching the XPath expression is returned
     *
     * @param startNode  ��ʼ�ڵ�
     * @param value      �ı�ֵ
     * @return           �޸ĺ�Ľڵ�
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
     * ���ҷ���XPath��ѯ���ʽ�����ĵ�һ���ڵ㣬����������ֵ
     * <p>
     * Takes a context node and an XPath expression.  The matching node gets a
     * text node appending containing the contents of the value string.  The
     * node matching the XPath expression is returned
     *
     * @param startNode  ��ʼ�ڵ�
     * @param attrName   �ı�ֵ
     * @param attrValue  �ı�ֵ
     * @param xql        XPath���ʽ
     * @return           �޸ĺ�Ľڵ�
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
     * ���ҷ���XPath��ѯ���ʽ�����ĵ�һ���ڵ㣬����������ֵ
     * <p>
     * Takes a context node and an XPath expression.  The matching node gets a
     * text node appending containing the contents of the value string.  The
     * node matching the XPath expression is returned
     *
     * @param startNode  ��ʼ�ڵ�
     * @param attrName   �ı�ֵ
     * @param attrValue  �ı�ֵ
     * @return           �޸ĺ�Ľڵ�
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
     * ���ĵ�ָ��λ�����һ���½ڵ�
     * <p>
     * Takes a context node, the name of the new node, and an XPath expression.
     * The new node is appended to the document at the point specified by the
     * context node and the XPath statement
     *
     * @param startNode   ��ʼ�ڵ�
     * @param name        �½ڵ�����ֵ��
     * @param xql         XPath���ʽ
     * @return            �½��Ľڵ�
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
     * ���ĵ�ָ��λ�����һ���½ڵ�
     * <p>
     * Takes a context node, the name of the new node, and an XPath expression.
     * The new node is appended to the document at the point specified by the
     * context node and the XPath statement
     *
     * @param startNode   ��ʼ�ڵ�
     * @param name        �½ڵ�����ֵ��
     * @return            �½��Ľڵ�
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
     * ���ĵ�ָ��λ��ɾ��һ���ڵ�
     * <p>
     * @param startNode   ��ʼ�ڵ�
     * @param xql         XPath���ʽ
     * @return            �½��Ľڵ�
     * @throws Exception
     **/
    public static Node deleteNode(Node startNode, String xql) throws Exception
    {
        Node targetNode = XPathAPI.selectSingleNode(startNode, xql);
        Node parentNode = targetNode.getParentNode();
        return (parentNode.removeChild(targetNode));
    }

    /**
     * ���ĵ�ָ��λ��ɾ��һ���ڵ�
     * <p>
     * @param startNode   ��ʼ�ڵ�
     * @return            �½��Ľڵ�
     * @throws Exception
     **/
    public static Node deleteNode(Node startNode) throws Exception
    {
//        Node targetNode = XPathAPI.selectSingleNode( startNode,xql );
        Node parentNode = startNode.getParentNode();
        return (parentNode.removeChild(startNode));
    }

    /**
     * �Ѵ�ָ���ڵ㿪ʼ����ת��Ϊxml��ʽ���ַ���
     * <p>
     * Converts either the SubTree designated by an Element node or an
     * entire tree, specified by a Document object into an XML Text
     * representation.
     * @param node    DOM�ڵ㣨�ĵ���Ԫ�أ�
     * @return        xml��ʽ���ַ���
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
     * �Ѵ�ָ���ڵ㿪ʼ���ݱ��浽ָ��xml�ļ���
     * <p>
     * Converts either the SubTree designated by an Element node or an
     * entire tree, specified by a Document object into an XML Text
     * representation.
     * @param node    DOM�ڵ㣨�ĵ���Ԫ�أ�
     * @param fName   XML�ļ���
     * @return        xml��ʽ���ַ���
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
     * �Ѵ�ָ���ڵ㿪ʼ���ݱ��浽ָ��xml�ļ���
     * <p>
     * Converts either the SubTree designated by an Element node or an
     * entire tree, specified by a Document object into an XML Text
     * representation.
     * @param node    DOM�ڵ㣨�ĵ���Ԫ�أ�
     * @param fName   XML�ļ���
     * @return        xml��ʽ���ַ���
     * @throws Exception
     **/
    ////////////////lixy 20030310 �������дXML�ļ�////////////////////////////
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
     * ����xml�ļ���ΪDom��
     * @author Yang Yalin on 2003.3.14
     * @param file xml�ļ�
     * @return �������XML DOM��
     * @throws Exception ��������
     */
    public static Document parseFile(File file) throws Exception
    {

        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        Document doc = dfactory.newDocumentBuilder().parse(file);
        return doc;
    }

    /**
     * ����xml�ļ���ΪDom��
     * @author lixy on 2003.3.20
     * @param file xml�ļ�
     * @return �������XML DOM��
     * @throws Exception ��������
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

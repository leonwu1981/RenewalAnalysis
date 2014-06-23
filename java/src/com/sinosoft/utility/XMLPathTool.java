/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
//*******************************************************************
 // Java  XMLPATHTool:
 //	Name        :
 //      Test content:
 //	Comment	    :
 //	Date        :
 //********************************************************************
  //1.得到document 对象;2.传入xpath表达式3.返回结点集
package com.sinosoft.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * XPathAPI应用类
 * <p>Title: Life Information System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Kevin
 * @version 1.0
 */
public class XMLPathTool
{

    /*对应的Dom树*/
    private Document sourceDom;


    /*对应的文件名*/
    private String fileName;

    public XMLPathTool(String fileName)
    {
        this.fileName = fileName;
        try
        {
            if (sourceDom == null)
            {
                sourceDom = getDocument(fileName);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * 从一个StringReader对象中产生一个Document
     * @param in InputStream 输入流
     */
    public XMLPathTool(InputStream in)
    {
        this.fileName = "";
        try
        {
            DocumentBuilderFactory dfactory = DocumentBuilderFactory.
                    newInstance();
            dfactory.setNamespaceAware(true);
            sourceDom = dfactory.newDocumentBuilder().parse(new InputSource(in));

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * 获取文档的document对象。
     * @param fileName String 输入的文件名。
     * @return Document 文件名对应的document对象。
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private static Document getDocument(String fileName)
            throws
            ParserConfigurationException, SAXException, IOException
            {
            InputSource in = new InputSource(new FileInputStream(fileName));
            DocumentBuilderFactory dfactory = DocumentBuilderFactory.
            newInstance();
            dfactory.setNamespaceAware(true);
            Document doc = dfactory.newDocumentBuilder().parse(in);

            return doc;
    }
            /**
             *分析xpath串。
             *@param xpathString  要进行分析的xpath表达式。
             *@return 结果集。
             */
            public Node parseX(String xpathString)
    {
        try
        {
            if (sourceDom == null)
            {
                sourceDom = getDocument(fileName);
            }

            return XPathAPI.selectSingleNode(sourceDom.getDocumentElement(),
                    xpathString);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }


    /**
     *分析xpath串。
     *@param xpathString  要进行分析的xpath表达式。
     *@return 结果集。
     */
    public NodeList parseN(String xpathString)
    {
        try
        {
            if (sourceDom == null)
            {
                sourceDom = getDocument(fileName);
            }

            return XPathAPI.selectNodeList(sourceDom.getDocumentElement(),
                    xpathString);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public static void main(String args[])
    {
        XMLPathTool xmlPathTool = new XMLPathTool("2.xml");

        Node node = xmlPathTool.parseX("/DATASET/CONTROL/TEMPLATE");

        System.out.println(node.getFirstChild().getNodeValue());

        NodeList nodeList = xmlPathTool.parseN("/DATASET/TABLE1/ROW/COL1");

        String nVal[] = null;
        if (nodeList == null)
        {
            nVal = new String[0];
        }
        else
        {
            nVal = new String[nodeList.getLength()];
        }

        for (int i = 0; i < nVal.length; i++)
        {
            nVal[i] = nodeList.item(i).getFirstChild().getNodeValue();
        }

        for (int i = 0; i < nVal.length; i++)
        {
            System.out.println(nVal[i]);
        }
        return;
    }
}

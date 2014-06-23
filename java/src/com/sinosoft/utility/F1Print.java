/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

// Imported Serializer classes
import java.io.InputStream;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 打印类
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: sinosoft</p>
 * @author zhuxf
 * @version 1.0
 */
public class F1Print
{
    private String strXMLFileName;
    private XMLPathTool aTest;
//    private String strF1FileName;

    // 改变获取多行数据的方式
    private Node m_nodeMultiRow = null;
    private boolean m_bBOF = false;

    /**
     * 缺省的构造函数
     */
    public F1Print()
    {}

    /**
     *带参数的构造函数
     * @param argXMLFile xml文件名
     */
    public F1Print(String argXMLFile)
    {
        setFileName(argXMLFile);
    }

    /**
     * 从一个输入流中构造一个F1Print对象
     * @param in 输入流，其中包含的内容是一个xml文件
     */
    public F1Print(InputStream in)
    {
        this.strXMLFileName = "";
        aTest = new XMLPathTool(in);
    }

    /**
     * 设置xml文件名
     * @param argFileName xml文件名
     */
    public void setFileName(String argFileName)
    {
        this.strXMLFileName = argFileName;
        aTest = new XMLPathTool(strXMLFileName);
    }

    public String getTemplate()
    {
        Node node = aTest.parseX("/DATASET/CONTROL/TEMPLATE");
        return getNodeValue(node);
    }

    public String getNodeValue(String argXPath)
    {
        Node node = aTest.parseX(argXPath);
        return getNodeValue(node);
    }

    public String getDisplayControl(String strName)
    {
        Node node = aTest.parseX("/DATASET/CONTROL/DISPLAY" + strName);
        return getNodeValue(node);
    }

    public String[] getNodeListValue(String argXPath, String argChildPath)
    {
        NodeList nodeList = aTest.parseN(argXPath + "/" + argChildPath);
        String nVal[] = null;

        if (nodeList == null)
        {
            nVal = new String[0];
        }
        else
        {
            nVal = new String[nodeList.getLength()];
            for (int i = 0; i < nodeList.getLength(); i++)
            {
//                nVal[i] = nodeList.item(i).getFirstChild() == null ? "" :
//                          nodeList.item(i).getFirstChild().getNodeValue();
                if (nodeList.item(i).getFirstChild().equals("null"))
                {
                    nVal[i] = "";
                }
                else
                {
                    nVal[i] = nodeList.item(i).getFirstChild().getNodeValue();
                }

            }
        }
        return nVal;
    }

    /**
     * Kevin 2003-06-06
     * @param argXPath String
     */
    public void query(String argXPath)
    {
        m_nodeMultiRow = aTest.parseX(argXPath);
        m_bBOF = true;
    }

    public boolean next()
    {
        boolean bReturn = true;

        if (m_nodeMultiRow == null)
        {
            bReturn = false;
        }
        else
        {
            if (m_bBOF)
            {
                // 如果是第一条记录
                bReturn = true;
                m_bBOF = false;
            }
            else
            {
                bReturn = false;
                m_nodeMultiRow = m_nodeMultiRow.getNextSibling();
                while (m_nodeMultiRow != null)
                {
                    if (m_nodeMultiRow.getNodeType() == Node.ELEMENT_NODE)
                    {
                        bReturn = true;
                        break;
                    }
                    m_nodeMultiRow = m_nodeMultiRow.getNextSibling();
                }
            }
        }

        return bReturn;
    }

    /**
     * 按照列的名字取得一个值
     * @param strChildPath String
     * @return String
     */
    public String getString(String strChildPath)
    {
        String strReturn = "";

        if (m_nodeMultiRow == null)
        {
            return "";
        }

        Node node = null;
        NodeList nodeList = m_nodeMultiRow.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++)
        {
            node = nodeList.item(i);
            if (node.getNodeName().equals(strChildPath))
            {
                node = node.getFirstChild();
                if (node != null)
                {
                    strReturn = node.getNodeValue();
                }
                else
                {
                    strReturn = "";
                }
                break;
            }
        }

        return strReturn;
    }

    /**
     * 按照列索引取得某一个值
     * @param nIndex int
     * @return String
     */
    public String getString(int nIndex)
    {
        String strReturn = "";

        if (nIndex < 0 || m_nodeMultiRow == null)
        {
            return "";
        }

        Node node = m_nodeMultiRow.getChildNodes().item(nIndex);
        NodeList nodeList = m_nodeMultiRow.getChildNodes();

        int nItem = 0;
        for (int nCount = 0; nCount < nodeList.getLength(); nCount++)
        {
            node = nodeList.item(nCount);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                if (nItem == nIndex)
                {
                    break;
                }
                else
                {
                    nItem++;
                }
            }
        }

        node = node.getFirstChild();

        if (node != null)
        {
            strReturn = node.getNodeValue();
        }
        else
        {
            strReturn = "";
        }

        return strReturn;
    }

    /**
     * 得到某一行数据的列数
     * @return int
     */
    public int getColCount()
    {
        if (m_nodeMultiRow == null)
        {
            return -1;
        }

//        Node node = null;
        NodeList nodeList = m_nodeMultiRow.getChildNodes();

        if (nodeList == null)
        {
            return -1;
        }

        int nCount = 0;

        for (int nIndex = 0; nIndex < nodeList.getLength(); nIndex++)
        {
            if (nodeList.item(nIndex).getNodeType() == Node.ELEMENT_NODE)
            {
                nCount++;
            }
        }

        return nCount;
    }

    /**
     * 获得某一个列的索引值
     * @param strChildPath String
     * @return int
     */
    public int getColIndex(String strChildPath)
    {
        if (m_nodeMultiRow == null)
        {
            return -1;
        }

        Node node = null;
        NodeList nodeList = m_nodeMultiRow.getChildNodes();

        boolean bFound = false;
        int nIndex = -1;

        for (int i = 0; i < nodeList.getLength(); i++)
        {
            node = nodeList.item(i);

            if (node.getNodeType() != Node.ELEMENT_NODE)
            {
                continue;
            }

            nIndex++;

            if (node.getNodeName().equals(strChildPath))
            {
                bFound = true;
                break;
            }
        }

        if (!bFound)
        {
            nIndex = -1;
        }

        return nIndex;
    }

    public String getNodeValue(Node node)
    {
        if (node == null)
        {
            return "";
        }

        if (node.getFirstChild() == null)
        {
            return "";
        }

        return node.getFirstChild().getNodeValue();
    }

    /**
     * 调试函数
     * @param args String[]
     */
    public static void main(String args[])
    {
//    F1Print aTest = new F1Print("2.xml");
//    String s = aTest.getTemplate();
//    System.out.println(s);
//    System.out.println(aTest.getNodeValue("/DATASET/f1"));
//    System.out.println(aTest.getNodeValue("/DATASET/f2"));
//    System.out.println(aTest.getNodeValue("/DATASET/f3"));
//    aTest.query("/DATASET/TABLE1/ROW");
//    int nIndex = 0;
//    int nColIndex = aTest.getColIndex("COL1");
//    if( nColIndex != -1 ) {
//      while( aTest.next() ) {
//        System.out.print( nIndex ++ );
//        System.out.print( " : ");
//        System.out.println( aTest.getString(nColIndex) );
//      }
//    }
    }
}

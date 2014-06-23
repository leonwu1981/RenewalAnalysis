package com.sinosoft.xreport.dl;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */

import java.io.FileWriter;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.sinosoft.xreport.util.SysConfig;
import com.sinosoft.xreport.util.XMLPathTool;
import com.sinosoft.xreport.util.XReader;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class ReadDataFile
{

    private static final String XQLROOT = "/DataFile";
    private static final String XQLFILE = XQLROOT + "/file";


    private Document document;
    Node root;
    private String filepath = "d:\\xslt\\xml\\DataFile.xml";
    ReadDataFile rdf = null;

    public ReadDataFile()
    {
    }

    public ReadDataFile(String path)
    {
        this.filepath = path;
    }

    public ReadDataFile doparse() throws Exception
    {
        if (rdf == null)
        {
            rdf = new ReadDataFile();
            rdf.Parse();
        }
        return rdf;
    }

    public void Parse() throws Exception
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        StringReader sr = new StringReader(XReader.readStyle("DataFile.xml"));
        InputSource insour = new InputSource(sr);
        //document = db.parse(filepath);
        document = db.parse(insour);
        //获得根节点
        root = document.getDocumentElement();
    }

    public void setFile(String str) throws Exception
    {
        ReadDataFile file = doparse();
        Node node = XPathAPI.selectSingleNode(root, XQLFILE);
        System.out.println(XMLPathTool.getValue(node));
        XMLPathTool.setValue(node, str);

    }

    public void saveToFile(Node nd) throws Exception
    {
        //XWriter.writeStyle(XMLPathTool.toString(nd),"DataFile.xml");
        FileWriter fw = new FileWriter(SysConfig.FILEPATH +
                                       "style/DataFile.xml");
        fw.write(XMLPathTool.toString(nd));
        fw.close();
    }

    public static void main(String[] args) throws Exception
    {
        ReadDataFile test = new ReadDataFile();
        test = test.doparse();

        test.setFile("sdfsd");
        System.out.println(XMLPathTool.toString(test.root));
        test.saveToFile(test.root);
    }
}
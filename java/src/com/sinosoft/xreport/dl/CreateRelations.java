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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class CreateRelations
{

    private static final String XQLROOT = "/Models";
    private static final String XQLMODEL = XQLROOT + "/Model";
    private static final String XQLCOLUMN = XQLMODEL + "/Column";
    private static final String XQLRELATIONS = XQLROOT + "/Relations";
    private static final String XQLRELATION = XQLRELATIONS + "Relation";
    //文件的位置
    private String filePath =
            "D:\\xreport\\src\\com\\sinosoft\\xreport\\dl\\DataSourceSchema.xml";
    private Document document;
    private Node root;

    private InputSource in;

    public CreateRelations()
    {

    }

    public CreateRelations(String filepath)
    {
        this.filePath = filepath;
    }


    public void doParseXML() throws Exception
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

//    StringReader sr = new StringReader(XReader.readConf("DataSourceSchema.xml"));
//
//    InputSource insour = new InputSource(sr);
        document = db.parse("d:\\xreport_data\\conf\\DataSourceSchema.xml");
        // document = db.parse(insour);
        //获得根节点
        root = document.getDocumentElement();
    }

    public NodeList getAllModels() throws Exception
    {
        NodeList nl = XPathAPI.selectNodeList(root, XQLMODEL);
        return nl;
    }


    public static void main(String[] args) throws Exception
    {
        CreateRelations test = new CreateRelations();
        test.doParseXML();
        String str = "";
        NodeList nl = test.getAllModels();
        for (int i = 0; i < nl.getLength(); i++)
        {
            Model m = new Model(nl.item(i));
            String s = m.getModelID();
            NodeList collist = m.getAllColumns();
            for (int j = 0; j < collist.getLength(); j++)
            {
                Column c = new Column(collist.item(j));
                String colID = c.getColumnName();
                ///////
                int flag = colID.indexOf("RiskCode");
                if (flag >= 0)
                {
                    System.out.println("i get RiskCode at :" + s + "." + colID);
                    for (int ii = 0; ii < nl.getLength(); ii++)
                    {
                        Model mm = new Model(nl.item(ii));
                        String ss = mm.getModelID();
                        NodeList collist1 = mm.getAllColumns();
                        for (int jj = 0; jj < collist1.getLength(); jj++)
                        {
                            Column cc = new Column(collist1.item(jj));
                            String colID1 = cc.getColumnName();

                            if (colID1.indexOf("RiskCode") >= 0 && !s.equals(ss))
                            {
                                str = str + "\n<Relations>\n" + "<Relation>" +
                                      s + "." + colID + " = " + ss + "." +
                                      colID1 + "</Relation>\n" + "</Relations>";
                            }
                        }

                    }
                }
                /////

                ///////
                int flag1 = colID.indexOf("SaleChnl");
                if (flag1 >= 0)
                {
                    System.out.println("i get SaleChnl at :" + s + "." + colID);
                    for (int ii = 0; ii < nl.getLength(); ii++)
                    {
                        Model mm = new Model(nl.item(ii));
                        String ss = mm.getModelID();
                        NodeList collist1 = mm.getAllColumns();
                        for (int jj = 0; jj < collist1.getLength(); jj++)
                        {
                            Column cc = new Column(collist1.item(jj));
                            String colID1 = cc.getColumnName();

                            if (colID1.indexOf("SaleChnl") >= 0 && !s.equals(ss))
                            {
                                str = str + "\n<Relations>\n" + "<Relation>" +
                                      s + "." + colID + " = " + ss + "." +
                                      colID1 + "</Relation>\n" + "</Relations>";
                            }
                        }

                    }
                }
                /////
/////

                int flag2 = colID.indexOf("ManageCom");
                if (flag2 >= 0)
                {
                    System.out.println("i get ManageCom at :" + s + "." + colID);
                    for (int ii = 0; ii < nl.getLength(); ii++)
                    {
                        Model mm = new Model(nl.item(ii));
                        String ss = mm.getModelID();
                        NodeList collist1 = mm.getAllColumns();
                        for (int jj = 0; jj < collist1.getLength(); jj++)
                        {
                            Column cc = new Column(collist1.item(jj));
                            String colID1 = cc.getColumnName();

                            if (colID1.indexOf("ManageCom") >= 0 &&
                                !s.equals(ss))
                            {
                                str = str + "\n<Relations>\n" + "<Relation>" +
                                      s + "." + colID + " = " + ss + "." +
                                      colID1 + "</Relation>\n" + "</Relations>";
                            }
                        }

                    }
                }
                /////
///////

                int flag3 = colID.indexOf("RiskType");
                if (flag3 >= 0)
                {
                    System.out.println("i get RiskType at :" + s + "." + colID);
                    for (int ii = 0; ii < nl.getLength(); ii++)
                    {
                        Model mm = new Model(nl.item(ii));
                        String ss = mm.getModelID();
                        NodeList collist1 = mm.getAllColumns();
                        for (int jj = 0; jj < collist1.getLength(); jj++)
                        {
                            Column cc = new Column(collist1.item(jj));
                            String colID1 = cc.getColumnName();

                            if (colID1.indexOf("RiskType") >= 0 && !s.equals(ss))
                            {
                                str = str + "\n<Relations>\n" + "<Relation>" +
                                      s + "." + colID + " = " + ss + "." +
                                      colID1 + "</Relation>\n" + "</Relations>";
                            }
                        }

                    }
                }
                /////




            }

        }
        FileWriter fw = new FileWriter("d:\\test.xml");
        fw.write(str);
        fw.close();
        System.out.println(str);
    }
}


//////////////////////////////

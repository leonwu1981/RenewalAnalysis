/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.io.*;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

public class XmlExport
{
    private Document myDocument;
    private int col;


    // @Method
    //��ʼ���ļ�������Ϊģ��������ӡ����
    public Document createDocument(String templatename, String printername)
    {
        // Create the root element
        // TemplateName=templatename;
        Element DataSetElement = new Element("DATASET");
        //create the document
        this.myDocument = new Document(DataSetElement);
        //add some child elements

        //  Note that this is the first approach to adding an element and
        // textual content.  The second approach is commented out.

        Element CONTROL = new Element("CONTROL");
        DataSetElement.addContent(CONTROL);
        Element TEMPLATE = new Element("TEMPLATE");
        Element PRINTER = new Element("PRINTER");
        PRINTER.addContent(printername);
        TEMPLATE.addContent(templatename);
        CONTROL.addContent(TEMPLATE);
        CONTROL.addContent(PRINTER);

        CONTROL.addContent(new Element("DISPLAY"));
        return myDocument;
    }

    //���xml�ļ�������Ϊ·�����ļ���
    public void outputDocumentToFile(String pathname, String filename)
    {
        //setup this like outputDocument
        try
        {
            XMLOutputter outputter = new XMLOutputter("  ", true, "GBK");
            //output to a file
            String str = pathname + filename + ".xml";
            FileWriter writer = new FileWriter(str);
            outputter.output(myDocument, writer);
            writer.close();
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
        }
    }

    //���һ���б�����ΪListTag�Ͷ�̬�б�ı�ͷ����
    public Document addListTable(ListTable listtable, String[] colvalue)
    {
        this.col = colvalue.length;
        Element DataSetElement = this.myDocument.getRootElement();
        Element table = new Element(listtable.getName());
        DataSetElement.addContent(table);
        Element head = new Element("HEAD");
        table.addContent(head);
        //������ͷ��
        for (int m = 0; m < colvalue.length; m++)
        {
            int n = m + 1;
            String colnum = "COL" + n;
            head.addContent(new Element(colnum).addContent(colvalue[m]));
        }

        //��������table

        int tablesize = listtable.size();

        for (int i = 0; i <= tablesize - 1; i++)
        {
            String[] temparray = new String[this.col];
            temparray = listtable.get(i);
            Element row = new Element("ROW");
            table.addContent(row);
            for (int m = 0; m < temparray.length; m++)
            {
                int n = m + 1;
                String colnum = "COL" + n;
                row.addContent(new Element(colnum).addContent(temparray[m]));
            }

        }
        return myDocument;
    }


    //��Ӷ�̬�ı���ǩ�����飬����Ϊһ��TextTag
    public Document addTextTag(TextTag texttag)
    {
        Element DataSetElement = this.myDocument.getRootElement();
        int tagsize = texttag.size();
        for (int i = 0; i <= tagsize - 1; i++)
        {
            String[] temparray = new String[2];
            temparray = (String[]) texttag.get(i);
            if (temparray[1].length() > 0)
            {
                DataSetElement.addContent(new Element(temparray[0]).addContent(
                        temparray[1]));
            }

            else
            {
                DataSetElement.addContent(new Element(temparray[0]).addContent(
                        " "));
            }

        }
        return myDocument;
    }


    /*
     * ֱ�Ӵ��ĵ��в���һ�����������󣬶���������һ����ʱ�ļ�
     * �����
     *     һ������������
     */
    public InputStream getInputStream()
    {
        try
        {
            XMLOutputter outputter = new XMLOutputter("  ", true, "GBK");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            outputter.output(myDocument, baos);
            baos.close();

            return new ByteArrayInputStream(baos.toByteArray());
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }
    /*
     * ֱ�Ӵ��ĵ��в���һ�����������󣬶���������һ����ʱ�ļ�
     * �����
     *     һ������������
     */
    public InputStream getInputStream(String Encoding)
    {
    	if(Encoding==null||"".equals(Encoding)){
    		return getInputStream();
    	}
        try
        {
            XMLOutputter outputter = new XMLOutputter("  ", true, Encoding);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            outputter.output(myDocument, baos);
            baos.close();

            return new ByteArrayInputStream(baos.toByteArray());
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 2002-11-11 kevin
     * ������xml�ļ��м���һ����ʾ��������
     * @param strName String
     * @return Document
     */
    public Document addDisplayControl(String strName)
    {
        Element elementControl = this.myDocument.getRootElement().getChild(
                "CONTROL").getChild("DISPLAY");
        elementControl.addContent(new Element(strName).addContent("1"));

        return myDocument;
    }
}

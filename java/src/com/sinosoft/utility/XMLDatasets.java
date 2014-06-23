/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.io.*;

import com.sinosoft.lis.schema.LMRiskDutySchema;
import com.sinosoft.lis.vschema.LMRiskDutySet;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;


/**
 * 为保单打印所做的XML文件生成类
 * <p>Title: Life Information System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft</p>
 * @author kevin
 * @version 1.0
 */
public class XMLDatasets
{
    private Document _Document;

    public XMLDatasets()
    {
        _Document = null;
    }

    public boolean createDocument()
    {
        Element elementDataSets = new Element("DATASETS");

        _Document = new Document(elementDataSets);

        return true;
    }

    public Document getDocument()
    {
        return (Document) (_Document.clone());
    }


    /**
     * 将XML文件的内容输出到一个文件中
     * @param strFileName 指定的文件名
     * @return boolean
     */
    public boolean output(String strFileName)
    {
        try
        {
            FileWriter fileWriter = new FileWriter(strFileName);
            boolean bRet = output(fileWriter);
            fileWriter.close();
            return bRet;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }


    /**
     * 将XML文件的内容输出到一个输出流中
     * @param writer 指定的输出流
     * @return boolean
     */
    public boolean output(Writer writer)
    {
        if (writer == null)
        {
            return false;
        }

        try
        {
            XMLOutputter outputter = new XMLOutputter("  ", true, "UTF-8");
            outputter.output(_Document, writer);
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }


    /**
     * 将XML文件的内容输出到一个输出流中
     * @param outputStream 指定的输出流
     * @return boolean
     */
    public boolean output(OutputStream outputStream)
    {
        if (outputStream == null)
        {
            return false;
        }

        try
        {
            XMLOutputter outputter = new XMLOutputter("  ", true, "UTF-8");
            outputter.output(_Document, outputStream);
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    public XMLDataset createDataset()
    {
        if (_Document == null)
        {
            if (!createDocument())
            {
                return null;
            }
        }

        Element elementDataset = new Element("DATASET");

        _Document.getRootElement().addContent(elementDataset);

        return new XMLDataset(elementDataset);
    }

    public XMLDatasetP createDatasetP()
    {
        if (_Document == null)
        {
            if (!createDocument())
            {
                return null;
            }
        }

        Element elementDataset = new Element("DATASET");

        _Document.getRootElement().addContent(elementDataset);

        return new XMLDatasetP(elementDataset);
    }


    /**
     * 直接从文档中产生一个输入流对象，而不是生成一个临时文件
     * 输出：
     *     一个输入流对象
     * @return InputStream
     */
    public InputStream getInputStream()
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (!this.output(baos))
            {
                baos.close();
                return null;
            }
            baos.close();

            return new ByteArrayInputStream(baos.toByteArray());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public static void main(String args[])
    {
        XMLDatasets xmlDatasets = new XMLDatasets();

        xmlDatasets.createDocument();

        XMLDataset xmlDataset = xmlDatasets.createDataset();

        xmlDataset.addDataObject(new XMLDataTag("one", "value"));

        XMLDataList xmlDataList = new XMLDataList();

        xmlDataList.setDataObjectID("CashValue");
        xmlDataList.addColHead("Age");
        xmlDataList.addColHead("Value");

        xmlDataList.buildColHead();

        xmlDataList.setColValue("Age", "0");
        xmlDataList.setColValue("Value", "100");

        xmlDataList.insertRow(0);

        xmlDataList.setColValue("Age", "1");
        xmlDataList.setColValue("Value", "101");

        xmlDataList.insertRow(0);

        xmlDataset = xmlDatasets.createDataset();
        xmlDataset.addDataObject(xmlDataList);

        LMRiskDutySchema tLMRiskDutySchema = new LMRiskDutySchema();

        tLMRiskDutySchema.setChoFlag("ChoFlag");
        tLMRiskDutySchema.setDutyCode("DutyCode");
        tLMRiskDutySchema.setRiskCode("00");
        tLMRiskDutySchema.setRiskVer("11");

        xmlDataset.addSchema(tLMRiskDutySchema);

        LMRiskDutySet tLMRiskDutySet = new LMRiskDutySet();

        tLMRiskDutySet.add(tLMRiskDutySchema);
        tLMRiskDutySet.add(tLMRiskDutySchema);

        xmlDataset.addSchemaSet(tLMRiskDutySet, "");

        xmlDatasets.output("data.xml");
    }
}

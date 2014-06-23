/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.lang.reflect.Field;
import java.util.Vector;

import org.jdom.Element;


/**
 * Ϊ������ӡ������XML�ļ�������
 * <p>Title: Life Information System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft</p>
 * @author kevin
 * @version 1.0
 */
public class XMLDatasetP
{
    private Element _Element;
    private CErrors mErrors = new CErrors();

    protected XMLDatasetP()
    {
        _Element = new Element("DATASET");
        if (!createControlInfo())
        {
            _Element = null;
        }
    }


    /**
     * �ڴ����DATASETԪ�����洴�����ǵ�Ԫ��
     * @param element Element
     */
    protected XMLDatasetP(Element element)
    {
        _Element = element;
        if (!createControlInfo())
        {
            _Element = null;
        }
    }

    private static boolean createControlInfo()
    {
//    if( _Element == null ) {
//      return false;
//    }
//
//    Element elementControl = new Element("CONTROL");
//
//    _Element.addContent(elementControl);
//
//    elementControl.addContent(new Element("TEMPLATE").addContent(""));
//    elementControl.addContent(new Element("PRINTER").addContent(""));
//    elementControl.addContent(new Element("DISPLAY").addContent(""));

        return true;
    }

    public boolean createFileListName(String FilePath)
    {
        if (_Element == null)
        {
            return false;
        }

        Element elementFile = new Element("file");

        _Element.addContent(elementFile);

        FileViewer f = new FileViewer();
        f.setPath(FilePath);
        f.refreshList();
        while (f.nextFile())
        {
            Element elementRow = new Element("row");
            elementFile.addContent(elementRow);
            Element elementName = new Element("name");
            elementRow.addContent(elementName.addContent(f.getFileName()));
//      System.out.print(f.getFileName());
//      if (!f.getFileType())
//        System.out.print("  " + f.getFileSize());
//      else
//        System.out.print("  ");
//      System.out.print(f.getFileTimeStamp() + "\n");
        }

        return true;
    }


    /**
     * �õ��ڲ�Ԫ�صĿ�¡
     * @return Element
     */
    public Element getElement()
    {
        if (_Element == null)
        {
            return null;
        }

        return (Element) (_Element.clone());
    }


    /**
     * ��XML�ļ��м���һ����ʾ��������
     * @param strName String ���Ʊ�ǩ������
     * @return XMLDatasetP
     */
    public XMLDatasetP addDisplayControl(String strName)
    {
        if (_Element == null)
        {
            return null;
        }

        Element elementControl = _Element.getChild("CONTROL").getChild(
                "DISPLAY");
        elementControl.addContent(new Element(strName).addContent("1"));

        return this;
    }


    /**
     * �������Dataset���õ�ģ����
     * @param strTemplate String
     * @return XMLDatasetP
     */
    public XMLDatasetP setTemplate(String strTemplate)
    {
        if (_Element == null)
        {
            return null;
        }

        _Element.getChild("CONTROL").getChild("TEMPLATE").setText(strTemplate);
        return this;
    }


    /**
     * �������Dataset���õĴ�ӡ����
     * @param strPrinter String
     * @return XMLDatasetP
     */
    public XMLDatasetP setPrinter(String strPrinter)
    {
        if (_Element == null)
        {
            return null;
        }

        _Element.getChild("CONTROL").getChild("PRINTER").setText(strPrinter);
        return this;
    }


    /**
     *
     * @param xmlDataObject XMLDataObject
     * @return boolean
     */
    public boolean addDataObject(XMLDataObject xmlDataObject)
    {
        // �������ı�ʶΪ�գ���XMLDataset�Զ�����һ�������ʶ
        if (xmlDataObject.getDataObjectID().equals(""))
        {
            xmlDataObject.setDataObjectID("");
        }

        return xmlDataObject.addDataTo(_Element);
    }


    /**
     * Get data from a schema object
     * @param schema Schema
     * @return boolean
     */
    public boolean addSchema(Schema schema)
    {
        mErrors.clearErrors();

        if (schema == null)
        {
            buildError("addSchema", "����Ϊ��");
            return false;
        }

        if (schema.getClass().getSuperclass().getName().equals(
                "com.sinosoft.lis.Schema"))
        {
            buildError("addSchema", "��������Schema��ֱ������");
            return false;
        }

        String strClassName = schema.getClass().getName();

        // get rid of package name : com.sinosoft.lis.schema.
        strClassName = strClassName.substring(strClassName.lastIndexOf(".") + 1);

        // get rid of "schema" : LAAgentSchema -> LAAgent
        strClassName = strClassName.substring(0, strClassName.indexOf("Schema"));

        Field fields[] = schema.getClass().getDeclaredFields();

        for (int nIndex = 0; nIndex < fields.length; nIndex++)
        {
            String strFieldName = fields[nIndex].getName();

            if (strFieldName.equals("FIELDNUM") || strFieldName.equals("PK")
                    || strFieldName.equals("mErrors") ||
                    strFieldName.equals("fDate"))
            {
                continue;
            }

            String strFieldType = fields[nIndex].getType().getName();
            String strFieldValue = schema.getV(strFieldName);

            if (strFieldType.equals("float") && !strFieldValue.equals("-100.0"))
            {
                _Element.addContent(new Element(strClassName + "." +
                        strFieldName).
                        addContent(strFieldValue));

            }
            else if (strFieldType.equals("int") && !strFieldValue.equals("-100"))
            {
                _Element.addContent(new Element(strClassName + "." +
                        strFieldName).
                        addContent(strFieldValue));

            }
            else if (strFieldType.equals("double") &&
                    !strFieldValue.equals("-100"))
            {
                _Element.addContent(new Element(strClassName + "." +
                        strFieldName).
                        addContent(strFieldValue));

            }
            else if (strFieldType.equals("java.lang.String") &&
                    !strFieldValue.equals("null"))
            {
                _Element.addContent(new Element(strClassName + "." +
                        strFieldName).
                        addContent(strFieldValue));

            }
            else if (strFieldType.equals("java.util.Date") &&
                    !strFieldValue.equals("null"))
            {
                if (strFieldValue.indexOf('-') != -1)
                {
                    strFieldValue = strFieldValue.substring(0, 4) + "��" +
                            strFieldValue.substring(5, 7) + "��" +
                            strFieldValue.substring(8, 10) + "��";
                }
                _Element.addContent(new Element(strClassName + "." +
                        strFieldName).
                        addContent(strFieldValue));

            }
            else
            {
                _Element.addContent(new Element(strClassName + "." +
                        strFieldName).
                        addContent(""));

            }
        }
        return true;
    }


    /**
     * Get data from a SchemaSet object
     * @param schemaSet SchemaSet
     * @param strID String
     * @return boolean
     */
    public boolean addSchemaSet(SchemaSet schemaSet, String strID)
    {
        mErrors.clearErrors();

        if (schemaSet == null || strID == null)
        {
            buildError("addSchemaSet", "�����д�");
            return false;
        }

        if (schemaSet.getClass().getSuperclass().getName().equals(
                "com.sinosoft.lis.SchemaSet"))
        {
            buildError("addSchemaSet", "��������SchemaSet��ֱ������");
            return false;
        }

        String strClassName = schemaSet.getClass().getName();

        // get rid of package name : com.sinosoft.lis.vschema.
        strClassName = strClassName.substring(strClassName.lastIndexOf(".") + 1);

        // get rid of "Set" : LAAgentSet -> LAAgent
        strClassName = strClassName.substring(0, strClassName.indexOf("Set"));

        Element eleList = null;

        if (strID.equals(""))
        {
            eleList = new Element(strClassName);
        }
        else
        {
            eleList = new Element(strID);
        }

        _Element.addContent(eleList);

        // get the columns' name
        strClassName = "com.sinosoft.lis.schema." + strClassName + "Schema";
        Field fields[] = null;

        Vector vFields = new Vector();

        try
        {
            fields = Class.forName(strClassName).getDeclaredFields();
        }
        catch (ClassNotFoundException ex)
        {
            buildError("addSchemaSet", "�Ҳ�����Ӧ��Schema��");
            return false;
        }

        Element eleHead = new Element("HEAD");
        eleList.addContent(eleHead);

        String strColName = "";
        int nColNum = 0;

        // first, we get col needed
        for (int nIndex = 0; nIndex < fields.length; nIndex++)
        {
            String strFieldName = fields[nIndex].getName();

            if (strFieldName.equals("FIELDNUM") || strFieldName.equals("PK")
                    || strFieldName.equals("mErrors") ||
                    strFieldName.equals("fDate"))
            {
                continue;
            }
            else
            {
                strColName = "COL" + String.valueOf(nColNum++);
                eleHead.addContent(new Element(strColName).addContent(
                        strFieldName));
                vFields.add(fields[nIndex]);
            }
        }

        // then, we get row value
        for (int nIndex = 0; nIndex < schemaSet.size(); nIndex++)
        {

            Element eleRow = new Element("ROW");
            eleList.addContent(eleRow);

            Schema schema = (Schema) schemaSet.getObj(nIndex + 1);

            nColNum = 0;
            for (int nCols = 0; nCols < vFields.size(); nCols++)
            {
                String strFieldName = ((Field) vFields.get(nCols)).getName();
                String strFieldType = ((Field) vFields.get(nCols)).getType().
                        getName();
                String strFieldValue = schema.getV(strFieldName);

                strColName = "COL" + String.valueOf(nColNum++);
                if (strFieldType.equals("float") &&
                        !strFieldValue.equals("-100.0"))
                {
                    eleRow.addContent(new Element(strColName).addContent(
                            strFieldValue));
                }
                else if (strFieldType.equals("double") &&
                        !strFieldValue.equals("-100.0"))
                {
                    eleRow.addContent(new Element(strColName).addContent(
                            strFieldValue));
                }
                else if (strFieldType.equals("int") &&
                        !strFieldValue.equals("-100"))
                {
                    eleRow.addContent(new Element(strColName).addContent(
                            strFieldValue));
                }
                else if (strFieldType.equals("java.lang.String") &&
                        !strFieldValue.equals("null"))
                {
                    eleRow.addContent(new Element(strColName).addContent(
                            strFieldValue));
                }
                else if (strFieldType.equals("java.util.Date") &&
                        !strFieldValue.equals("null"))
                {
                    if (strFieldValue.indexOf('-') != -1)
                    {
                        strFieldValue = strFieldValue.substring(0, 4) + "��" +
                                strFieldValue.substring(5, 7) + "��" +
                                strFieldValue.substring(8, 10) + "��";
                    }
                    eleRow.addContent(new Element(strColName).addContent(
                            strFieldValue));
                }
                else
                {
                    eleRow.addContent(new Element(strColName).addContent(""));
                }

            } // end of for(int nCols = 0; ...
        } // end of for(int nIndex = 0; ...

        return true;
    }


    /**
     * �����е�XMLDataset���������һ���µ�XMLDataset����
     * @return Object
     */
    public Object clone()
    {
        if (_Element == null)
        {
            return null;
        }

        return new XMLDatasetP(this.getElement());
    }

    private void buildError(String szFunc, String szErrMsg)
    {
        CError cError = new CError();

        cError.moduleName = "XmlExportHelper";
        cError.functionName = szFunc;
        cError.errorMessage = szErrMsg;
        mErrors.addOneError(cError);
    }
}
/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.jdom.Element;


/**
 * 多行数据所对应的List类
 * <p>Title: Life Information System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class XMLDataList extends XMLDataObject
{

    /**
     * _VCols 用来对表头进行排序
     * _HCols 表示一行记录或者表头，记录表头和数据的对应
     * _VData 数据存储区，其中的每一个元素都是一个Hashtable
     * _bAutoCol 自动产生列名标志。如果这个值为false，则使用用户指定的列名；否则，使用自动产生的列名。
     */
    private Vector _VCols = new Vector();
    private Hashtable _HCols = new Hashtable();
    private Vector _VData = new Vector();
    private boolean _bAutoCol = true;

    public XMLDataList()
    {
        _VData.clear();
        setDataObjectID("");
    }

    public XMLDataList(String strDataObjectID)
    {
        this();
        setDataObjectID(strDataObjectID);
    }


    /**
     * 添加列表的表头
     * @param strColHead String
     * @return boolean
     */
    public boolean addColHead(String strColHead)
    {
        if (_VData.size() != 0)
        {
            // Have call buildColHead() already
            _VData.clear();
            _HCols.clear();
            _VCols.clear();
        }

        if (_HCols.containsKey(strColHead))
        {
            return false;
        }
        else
        {
            _HCols.put(strColHead, "");
            _VCols.add(strColHead);
            return true;
        }
    }


    /**
     * 设置列表的表头。列表的表头信息放在Vector的第一个位置
     * @return boolean
     */
    public boolean buildColHead()
    {
        _VData.clear();
        return true;
    }


    /**
     * 设置列中的值
     * @param strColName String
     * @param strValue String
     * @return boolean
     */
    public boolean setColValue(String strColName, String strValue)
    {
        if (!_HCols.containsKey(strColName))
        {
            return false;
        }

        if (strValue == null || strValue.equals(""))
        {
            _HCols.put(strColName, "");
        }
        else
        {
            _HCols.put(strColName, strValue);
        }
        return true;
    }


    /**
     * 设置列中的值
     * @param strColName String
     * @param nValue int
     * @return boolean
     */
    public boolean setColValue(String strColName, int nValue)
    {
        if (!_HCols.containsKey(strColName))
        {
            return false;
        }

        _HCols.put(strColName, String.valueOf(nValue));
        return true;
    }


    /**
     * 设置列中的值
     * @param strColName String
     * @param fValue float
     * @return boolean
     */
    public boolean setColValue(String strColName, float fValue)
    {
        if (!_HCols.containsKey(strColName))
        {
            return false;
        }

        _HCols.put(strColName, String.valueOf(fValue));
        return true;
    }


    /**
     * 设置列中的值
     * @param strColName String
     * @param dValue double
     * @return boolean
     */
    public boolean setColValue(String strColName, double dValue)
    {
        if (!_HCols.containsKey(strColName))
        {
            return false;
        }

        _HCols.put(strColName, String.valueOf(dValue));
        return true;
    }


    /**
     * 将列中准备好的值添加到行集中
     * @param nFlag int
     * @return boolean
     */
    public boolean insertRow(int nFlag)
    {
        _VData.add(_HCols.clone());

        for (Enumeration e = _HCols.keys(); e.hasMoreElements(); )
        {
            _HCols.put(e.nextElement(), "");
        }

        return true;
    }


    /**
     * 设置自动产生列名标志
     * @param bAutoCol 自动产生列名标志
     * @return 新的自动产生列名标志的值
     */
    public boolean setAutoCol(boolean bAutoCol)
    {
        _bAutoCol = bAutoCol;
        return _bAutoCol;
    }

    public int getDataObjectType()
    {
        return XMLDataObject.TYPE_LIST;
    }

    public boolean addDataTo(Element element)
    {
        Enumeration tEnumeration = null;

        String str = "";
        String strColName = "";
        int nColNum = 0;

        Element eleID = new Element(_ID);
        element.addContent(eleID);

        Element eleHead = new Element("HEAD");
        eleID.addContent(eleHead);

        nColNum = 0;
        for (tEnumeration = _VCols.elements(); tEnumeration.hasMoreElements(); )
        {
            str = (String) tEnumeration.nextElement();
            strColName = _bAutoCol ? "COL0" + String.valueOf(nColNum++) : str;
            eleHead.addContent(new Element(strColName).addContent(str));
        }

        Hashtable hash = null;

        for (int nIndex = 0; nIndex < _VData.size(); nIndex++)
        {
            Element eleRow = new Element("ROW");
            eleID.addContent(eleRow);
            hash = (Hashtable) (_VData.get(nIndex));

            nColNum = 0;
            for (tEnumeration = _VCols.elements(); tEnumeration.hasMoreElements(); )
            {
                str = (String) tEnumeration.nextElement();
                strColName = _bAutoCol ? "COL0" + String.valueOf(nColNum++) :
                        str;
                eleRow.addContent(new Element(strColName).addContent((String)
                        hash.get(str)));
            }
        }
        return true;
    }


    /**
     * Create a XMLDataList object from a SchemaSet object
     * @param schemaSet SchemaSet
     * @return XMLDataList
     */
    public static XMLDataList fromSchemaSet(SchemaSet schemaSet)
    {

        XMLDataList xmlDataList = new XMLDataList();

        if (schemaSet == null)
        {
            return null;
        }

        if (schemaSet.getClass().getSuperclass().getName().equals(
                "com.sinosoft.lis.SchemaSet"))
        {
            return null;
        }

        String strClassName = schemaSet.getClass().getName();

        // get rid of package name : com.sinosoft.lis.vschema.
        strClassName = strClassName.substring(strClassName.lastIndexOf(".") + 1);

        // get rid of "Set" : LAAgentSet -> LAAgent
        strClassName = strClassName.substring(0, strClassName.indexOf("Set"));

        xmlDataList.setDataObjectID(strClassName);

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
            return null;
        }

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
                xmlDataList.addColHead(strFieldName);
                vFields.add(fields[nIndex]);
            }
        }

        // then, we get row value
        for (int nIndex = 0; nIndex < schemaSet.size(); nIndex++)
        {
            Schema schema = (Schema) schemaSet.getObj(nIndex + 1);

            for (int nCols = 0; nCols < vFields.size(); nCols++)
            {
                String strFieldName = ((Field) vFields.get(nCols)).getName();
                String strFieldType = ((Field) vFields.get(nCols)).getType().
                        getName();
                String strFieldValue = schema.getV(strFieldName);

                if (strFieldType.equals("float") &&
                        !strFieldValue.equals("-100.0"))
                {
                    xmlDataList.setColValue(strFieldName, strFieldValue);
                }
                else if (strFieldType.equals("int") &&
                        !strFieldValue.equals("-100"))
                {
                    xmlDataList.setColValue(strFieldName, strFieldValue);
                }
                else if (strFieldType.equals("java.lang.String") &&
                        !strFieldValue.equals("null"))
                {
                    xmlDataList.setColValue(strFieldName, strFieldValue);
                }
                else if (strFieldType.equals("java.util.Date") &&
                        !strFieldValue.equals("null"))
                {
                    if (strFieldValue.indexOf('-') != -1)
                    {
                        strFieldValue = strFieldValue.substring(0, 4) + "年" +
                                strFieldValue.substring(5, 7) + "月" +
                                strFieldValue.substring(8, 10) + "日";
                    }
                    xmlDataList.setColValue(strFieldName, strFieldValue);
                }
                else
                {
                    xmlDataList.setColValue(strFieldName, "");
                }

            }
            // end of for(int nCols = 0; ...
            xmlDataList.insertRow(0);
        }
        // end of for(int nIndex = 0; ...
        return xmlDataList;
    }
}

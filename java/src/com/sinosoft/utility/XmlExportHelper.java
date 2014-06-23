/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.lang.reflect.Field;
import java.util.Vector;


/*
 * <p>ClassName: XmlExportHelper </p>
 * <p>Description: XmlExport的助手类，可以从Schema类或者Set类中生成 </p>
 * <p>包含表名和字段信息的XML查询结果数据文件 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft </p>
 * @Database: Kevin
 * @CreateDate：2002-11-05
 */
public class XmlExportHelper
{
    // @Field
    public static final CErrors mErrors = new CErrors(); // 错误信息


    // @Constructor
    private XmlExportHelper()
    {
        // do nothing
    }


    // @Method
    public static boolean genTextTag(TextTag textTag, Schema s)
    {
        mErrors.clearErrors();

        if (s == null || textTag == null)
        {
            buildError("genTextTag", "参数为空");
            return false;
        }

        if (s.getClass().getSuperclass().getName().equals(
                "com.sinosoft.lis.Schema"))
        {
            buildError("genTextTag", "参数不是Schema的直接子类");
            return false;
        }

        String strClassName = s.getClass().getName();

        // get rid of package name : com.sinosoft.lis.schema.
        strClassName = strClassName.substring(strClassName.lastIndexOf(".") + 1);

        // get rid of "schema" : LAAgentSchema -> LAAgent
        strClassName = strClassName.substring(0, strClassName.indexOf("Schema"));

        Field fields[] = s.getClass().getDeclaredFields();

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
            String strFieldValue = s.getV(strFieldName);

            if (strFieldType.equals("float") && !strFieldValue.equals("0.0"))
            {
                textTag.add(strClassName + "." + strFieldName, strFieldValue);

            }
            else if ((strFieldType.equals("int")) && !strFieldValue.equals("0"))
            {
                textTag.add(strClassName + "." + strFieldName, strFieldValue);

            }
            else if ((strFieldType.equals("java.lang.String") ||
                    strFieldType.equals("java.util.Date")) &&
                    !strFieldValue.equals("null"))
            {
                textTag.add(strClassName + "." + strFieldName, strFieldValue);

            }
            else
            {
                textTag.add(strClassName + "." + strFieldName, "");

            }
        }

        return true;
    }

    public static boolean genListTable(XmlExport xmlExport, ListTable listTable,
            SchemaSet schemaSet)
    {
        mErrors.clearErrors();

        if (listTable == null || schemaSet == null)
        {
            buildError("genListTable", "参数有错");
            return false;
        }

        if (schemaSet.getClass().getSuperclass().getName().equals(
                "com.sinosoft.lis.SchemaSet"))
        {
            buildError("genTextTag", "参数不是SchemaSet的直接子类");
            return false;
        }

        String strClassName = schemaSet.getClass().getName();

        // get rid of package name : com.sinosoft.lis.vschema.
        strClassName = strClassName.substring(strClassName.lastIndexOf(".") + 1);

        // get rid of "Set" : LAAgentSet -> LAAgent
        strClassName = strClassName.substring(0, strClassName.indexOf("Set"));
        listTable.setName(strClassName);

        // get the columns' name
        strClassName = "com.sinosoft.lis.schema." + strClassName + "Schema";
        Field fields[] = null;
        String strFields[] = null;

        Vector vFields = new Vector();

        try
        {
            fields = Class.forName(strClassName).getDeclaredFields();
        }
        catch (ClassNotFoundException ex)
        {
            buildError("genListTable", "找不到对应的Schema类");
            return false;
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
                vFields.add(fields[nIndex]);
            }
        }

        // then, we get row value
        for (int nIndex = 0; nIndex < schemaSet.size(); nIndex++)
        {
            strFields = new String[vFields.size()];
            Schema schema = (Schema) schemaSet.getObj(nIndex + 1);

            for (int nCols = 0; nCols < vFields.size(); nCols++)
            {
                String strFieldName = ((Field) vFields.get(nCols)).getName();
                String strFieldType = ((Field) vFields.get(nCols)).getType().
                        getName();
                String strFieldValue = schema.getV(strFieldName);

                if (strFieldType.equals("float") && !strFieldValue.equals("0.0"))
                {
                    strFields[nCols] = strFieldValue;
                }
                else if ((strFieldType.equals("int")) &&
                        !strFieldValue.equals("0"))
                {
                    strFields[nCols] = strFieldValue;
                }
                else if ((strFieldType.equals("java.lang.String") ||
                        strFieldType.equals("java.util.Date")) &&
                        !strFieldValue.equals("null"))
                {
                    strFields[nCols] = strFieldValue;
                }
                else
                {
                    strFields[nCols] = "";
                }

            } // end of for(int nCols = 0; ...
            listTable.add(strFields);
        } // end of for(int nIndex = 0; ...

        // now, we get col name
        strFields = new String[vFields.size()];
        for (int nIndex = 0; nIndex < vFields.size(); nIndex++)
        {
            strFields[nIndex] = ((Field) vFields.get(nIndex)).getName();
        }

        xmlExport.addListTable(listTable, strFields);

        return true;
    }

    private static void buildError(String szFunc, String szErrMsg)
    {
        CError cError = new CError();

        cError.moduleName = "XmlExportHelper";
        cError.functionName = szFunc;
        cError.errorMessage = szErrMsg;
        mErrors.addOneError(cError);
    }
}

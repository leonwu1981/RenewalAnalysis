/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import org.jdom.Element;


/**
 * 单行数据所对应的Tag类
 * <p>Title: Life Information System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author kevin
 * @version 1.0
 */

public class XMLDataTag extends XMLDataObject
{

    private String _Value;

    public XMLDataTag(String strName, String strValue)
    {
        setDataObjectID(strName);

        _Value = strValue;
    }

    public XMLDataTag(String strName, int iValue)
    {
        setDataObjectID(strName);

        _Value = String.valueOf(iValue);
    }

    public void setName(String strName)
    {
        setDataObjectID(strName);
    }

    public void setValue(String strValue)
    {
        _Value = strValue;
    }

    public void setValue(int iValue)
    {
        _Value = String.valueOf(iValue);
    }

    public int getDataObjectType()
    {
        return XMLDataObject.TYPE_TAG;
    }

    public boolean addDataTo(Element element)
    {
        element.addContent(new Element(_ID).addContent(_Value));
        return true;
    }
}

/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import org.jdom.Element;


/**
 * XML数据的基类
 * <p>Title: Life Information System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft</p>
 * @author kevin
 * @version 1.0
 */
public abstract class XMLDataObject
{
    public final static int TYPE_TAG = 0;
    public final static int TYPE_LIST = 1;
    public final static int TYPE_MINE = 2;
    public final static int TYPE_BLOB = 3;

    protected String _ID;

    private XMLDataFormater m_xdf = null;

    public XMLDataObject()
    {
    }

    public final void setDataObjectID(String strID)
    {
        if (strID.equals("null"))
        {
            strID = "";
        }
        //        _ID = strID == null ? "" : strID;
        _ID = strID;
    }

    public final String getDataObjectID()
    {
        if (_ID == null)
        {
            return null;
        }
        else
        {
            return _ID;
        }
    }

    public final void setDataFormater(XMLDataFormater xdf)
    {
        m_xdf = xdf;
    }

    public final XMLDataFormater getDataFormater()
    {
        return m_xdf;
    }


    // The following are abstract functions
    public abstract boolean addDataTo(Element element);

    public abstract int getDataObjectType();
}

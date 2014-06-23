/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.io.InputStream;
import java.util.Hashtable;

import com.sinosoft.lis.f1print.DMParser;

import org.jdom.Element;


/**
 * <p>Title: Life Information System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class XMLDataMine extends XMLDataObject
{

    private DMParser dmParser = new DMParser();


    /**
     * Construct a XMLDataMine
     * @param ins InputStream
     * @param hashData Hashtable
     */
    public XMLDataMine(InputStream ins, Hashtable hashData)
    {
        try
        {
            dmParser.setInputer(ins, hashData);
            _ID = "";
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public int getDataObjectType()
    {
        return XMLDataObject.TYPE_MINE;
    }

    public boolean addDataTo(Element element)
    {
        try
        {
            dmParser.addDataTo(element);

            XMLDataFormater xdf = this.getDataFormater();

            if (xdf != null)
            {
                return xdf.format(element);
            }

            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
}

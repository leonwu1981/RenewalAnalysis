/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.io.InputStream;
import java.util.List;

import org.jdom.Element;
import org.jdom.input.DOMBuilder;


/**
 * <p>Title: Life Information System</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class XMLDataBlob extends XMLDataObject
{

    private Element _ele = null;


    /**
     * Construct a XMLDataBlob from a stream object
     * @param ins InputStream
     */
    public XMLDataBlob(InputStream ins)
    {
        DOMBuilder domBuilder = new DOMBuilder();

        try
        {
            super._ID = "";
            _ele = domBuilder.build(ins).getRootElement();
//      _ele = new Element("DATASET").setMixedContent( _ele.getMixedContent() );
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            _ele = null;
        }
    }

    public int getDataObjectType()
    {
        return XMLDataObject.TYPE_BLOB;
    }

    public boolean addDataTo(Element element)
    {
        try
        {
            if (_ele == null)
            {
                return false;
            }

            _ele.removeChild("CONTROL");

            List list = _ele.getChildren();

            for (int nIndex = 0; nIndex < list.size(); nIndex++)
            {
                Element ele = (Element) list.get(nIndex);
                _ele.removeChild(ele.getName()); // Detach this element from document

                element.addContent(ele);
            }
//      transfer(_ele, element);

            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }


//  private void transfer(Element eleSrc, Element eleDest)
//      throws Exception
//  {
//    List list = eleSrc.getChildren();
//
//    for(int nIndex = 0; nIndex < list.size(); nIndex ++) {
//      Element eleSource = (Element)list.get(nIndex);
//      Element eleDestination = new Element(eleSource.getName());
//
//      eleDestination.setText(eleSource.getTextTrim());
//
//      transfer(eleSource, eleDestination);
//
//      eleDest.addContent(eleDestination);
//    }
//  }
}

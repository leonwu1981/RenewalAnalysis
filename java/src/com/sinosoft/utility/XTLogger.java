/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


/**
 * log4j��������.
 * ��static���ָ��������ļ�.
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */

public class XTLogger
{

    static
    {
//    DOMConfigurator.configure(SysConfig.FILEPATH+"conf/log4j.xml");

        DOMConfigurator.configure(XTLogger.class.getResource(
                "../../../AutoPayLog4j.xml"));
    }

    public XTLogger()
    {
    }


    /**
     * �õ���¼��
     * @param className ����
     * @return ��¼��
     */
    public static Logger getLogger(String className)
    {
        return Logger.getLogger(className);
    }


    /**
     * �õ���¼��
     * @param className ��
     * @return ��¼��
     */
    public static Logger getLogger(Class className)
    {
        return Logger.getLogger(className);
    }


    public static void main(String[] args)
    {
        XTLogger XTLogger1 = new XTLogger();
        Logger log = XTLogger.getLogger(XTLogger.class);
        log.info(".......");
        log.info("/////////");
    }
}

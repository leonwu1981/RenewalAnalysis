package com.sinosoft.xreport.bl;

import java.util.Map;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */

public interface Parameter
{

    public static final String NAME = "name";
    public static final String TIPS = "tips";
    public static final String SHOWMODE = "showMode";

    public Map getDefine();

    public void setDefine(Map map);

    public String toXMLString();

}
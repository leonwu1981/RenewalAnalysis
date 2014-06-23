package com.sinosoft.xreport.bl;

/**
 * 计算所需的准备信息.
 * 操作员,登录单位,系统时间...
 * 以用户为单位,每个session共享一些变量...
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */

public interface Environment
{

    /**
     * 操作员ID
     */
    public static final String OPERATORID = "operatorID";

    /**
     * 计算单位.
     */
    public static final String CALCBRANCH = "calculateBranch";

    /**
     * 计算时间
     */
    public static final String CALCDATE = "calculateDate";

    /**
     * 计算时间,END
     */
    public static final String CALCDATEEND = "calculateDateEnd";

    /**
     * 是否显示0值,由DISPLAYZERO_YES/DISPLAYZERO_NO确定.
     */
    public static final String DISPLAYZERO = "displayZero";
    public static final String DISPLAYZERO_YES = "1";
    public static final String DISPLAYZERO_NO = "0";

    public String getEnv(String envName) throws Exception;

    public void setEnv(String envName, Object value);

}
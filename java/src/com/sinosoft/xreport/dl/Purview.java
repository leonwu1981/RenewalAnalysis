package com.sinosoft.xreport.dl;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author houzw
 * @version 1.0
 */

public interface Purview
{

    /**
     * 检查用户权限
     * @param PersonID 用户名
     * @param ModuleID 模块名
     * @return 返回权限 boolean
     * @throws null
     */
    public boolean checkPurview(String PersonID, String ModuleID);

}
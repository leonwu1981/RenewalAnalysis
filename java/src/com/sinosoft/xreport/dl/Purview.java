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
     * ����û�Ȩ��
     * @param PersonID �û���
     * @param ModuleID ģ����
     * @return ����Ȩ�� boolean
     * @throws null
     */
    public boolean checkPurview(String PersonID, String ModuleID);

}
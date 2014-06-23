package com.sinosoft.xreport.bl;

/**
 * ���������׼����Ϣ.
 * ����Ա,��¼��λ,ϵͳʱ��...
 * ���û�Ϊ��λ,ÿ��session����һЩ����...
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
     * ����ԱID
     */
    public static final String OPERATORID = "operatorID";

    /**
     * ���㵥λ.
     */
    public static final String CALCBRANCH = "calculateBranch";

    /**
     * ����ʱ��
     */
    public static final String CALCDATE = "calculateDate";

    /**
     * ����ʱ��,END
     */
    public static final String CALCDATEEND = "calculateDateEnd";

    /**
     * �Ƿ���ʾ0ֵ,��DISPLAYZERO_YES/DISPLAYZERO_NOȷ��.
     */
    public static final String DISPLAYZERO = "displayZero";
    public static final String DISPLAYZERO_YES = "1";
    public static final String DISPLAYZERO_NO = "0";

    public String getEnv(String envName) throws Exception;

    public void setEnv(String envName, Object value);

}
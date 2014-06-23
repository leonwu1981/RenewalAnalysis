/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

/**
 * <p>Title: Life Information System</p>
 * <p>Description: ���ݿ�ͬ������</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author Kevin
 * @version 1.0
 */
public class DBSemaphore
{

    private static volatile boolean m_bInUse = false;
    private static String m_szObject = "DBSemaphore";

    public DBSemaphore()
    {
    }

    /**
     * ����"ʹ�ñ�־"��
     * ����true��ʾ����ʹ�ñ�־��������false��ʾ�ͷš�ʹ�ñ�־����
     * @param bNewValue boolean
     * @return boolean
     */
    protected static synchronized boolean setInUseFlag(boolean bNewValue)
    {
        if (bNewValue == true)
        { // ����ʹ�ñ�־��
            if (m_bInUse == true)
            { // ��ʹ�ñ�־���Ѿ���ռ��
                return false;
            }
            else
            {
                m_bInUse = true;
                return true;
            }
        }
        else
        { // �ͷš�ʹ�ñ�־��
            m_bInUse = false;
            return true;
        }
    }

    protected static void Lock() throws Exception
    {
        Lock(1000);
    }

    protected static void Lock(int nSeconds) throws Exception
    {
        if (nSeconds <= 0)
        {
            while (!setInUseFlag(true))
            {
                Thread.sleep(100);
            }
        }
        else
        {
            while (!setInUseFlag(true) && nSeconds-- > 0)
            {
                Thread.sleep(100);
            }

            if (nSeconds == 0)
            {
                throw new Exception("Lock time out");
            }
        }
    }

    protected static void UnLock()
    {
        setInUseFlag(false);
    }
}

/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

/**
 * <p>Title: Life Information System</p>
 * <p>Description: 数据库同步对象</p>
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
     * 设置"使用标志"。
     * 传入true表示请求“使用标志”，传入false表示释放“使用标志”。
     * @param bNewValue boolean
     * @return boolean
     */
    protected static synchronized boolean setInUseFlag(boolean bNewValue)
    {
        if (bNewValue == true)
        { // 请求“使用标志”
            if (m_bInUse == true)
            { // “使用标志”已经被占用
                return false;
            }
            else
            {
                m_bInUse = true;
                return true;
            }
        }
        else
        { // 释放“使用标志”
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

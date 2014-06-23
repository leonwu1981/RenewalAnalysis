/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.util.Date;

/*
 * <p>ClassName: DBConn </p>
 * <p>Description: 连接数据库类文件 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft </p>
 * @Database: LIS
 * @CreateDate：2002-08-09
 */
public class DBConnDaemon extends Thread
{
    // @Field
    public CErrors mErrors = new CErrors(); // 错误信息

    // @Method
    public void run()
    {
        int nStep = 0;

        while (true)
        {
            try
            {
                Thread.sleep(30000);
                nStep = 0;

                DBSemaphore.Lock();
                nStep = 1;

                daemonFunc();

                DBSemaphore.UnLock();
                nStep = 2;

            }
            catch (Exception ex)
            {
                if (nStep == 1)
                {
                    DBSemaphore.UnLock();
                }
            }
        }
    }

    private void daemonFunc() throws Exception
    {
        DBConn[] dbConns = DBConnPool.getDBConns();

        if (dbConns == null)
        {
            return;
        }

        int nIndex = 0;
        long lCurTime = new Date().getTime();

        for (nIndex = 0; nIndex < dbConns.length; nIndex++)
        {
            Date lastestAccess = dbConns[nIndex].getLastestAccess();

            if (lastestAccess == null)
            {
                continue;
            }

            long lLastestAccess = lastestAccess.getTime();

            // if current dbconn is in use now
            if (dbConns[nIndex].isInUse())
            {
                // and keep dbconn in use for 10 minute
                if (lCurTime - lLastestAccess > 600000)
                {
                    dbConns[nIndex].close();
                }
                // if current dbconn is not in use now
            }
            else
            {
                // and inner conn idle for 30 minutes
                if (lCurTime - lLastestAccess > 1800000)
                {
                    dbConns[nIndex].innerClose();
                }
            }
        }
    }
}

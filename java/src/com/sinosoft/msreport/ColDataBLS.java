/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.ListIterator;

import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.ExeSQL;

/**
 * <p>Title: lis</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: sinosoft</p>
 * @author lh
 * @version 1.0
 */

public class ColDataBLS
{

    //错误处理类，每个需要错误处理的类中都放置该类
    public CErrors mErrors = new CErrors();
    private LinkedList mLinkedList = new LinkedList();

    public ColDataBLS()
    {
    }

    //传输数据的公共方法
    public boolean submitData(LinkedList cLinkedList)
    {

        //首先将数据在本类中做一个备份
        mLinkedList = (LinkedList) cLinkedList.clone();

        System.out.println("Start ColData BLS Submit...");

        boolean tReturn = false;
//对数据库进行操作，把相关数据插入到数据库中
        tReturn = save();
        if (tReturn)
        {
            System.out.println("Save sucessful");
        }
        else
        {
            System.out.println("Save failed");
        }

        System.out.println("End ColData BLS Submit...");

        return tReturn;
    }

    /**
     * 根据前面的输入数据，进行BLS逻辑处理，保存数据
     * 如果在处理过程中出错，则返回false,否则返回true
     */
    private boolean save()
    {
        boolean tReturn = true;
        System.out.println("Start Save...");

//获得连接池的一个连接
        Connection conn = DBConnPool.getConnection();
        if (conn == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "ColDataBLS";
            tError.functionName = "save";
            tError.errorMessage = "数据库连接失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
//循环遍历每一个将要执行的SQL语句
            ListIterator tIterator = mLinkedList.listIterator();
            while (tIterator.hasNext())
            {
                String tSQL = (String) tIterator.next();
                ExeSQL tExeSQL = new ExeSQL(conn);
//执行取得的SQL语句
                if (!tExeSQL.execUpdateSQL(tSQL))
                {
                    // @@错误处理
                    this.mErrors.copyAllErrors(tExeSQL.mErrors);
                    CError tError = new CError();
                    tError.moduleName = "ColDataBLS";
                    tError.functionName = "save";
                    tError.errorMessage = "保存数据出错!";
                    this.mErrors.addOneError(tError);
                    return false;
                }
            }
            conn.commit();
            conn.close();
        }
        catch (Exception ex)
        {
            System.out.println("Exception in BLS");
            ex.printStackTrace();
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "ColDataBLS";
            tError.functionName = "save";
            tError.errorMessage = ex.toString();
            this.mErrors.addOneError(tError);
            try
            {
                conn.rollback();
                conn.close();
            }
            catch (Exception e)
            {}
            tReturn = false;
        }

        return tReturn;
    }

    public static void main(String[] args)
    {
        ColDataBLS colDataBLS1 = new ColDataBLS();
    }
}

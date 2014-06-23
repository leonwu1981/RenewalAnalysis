/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.io.OutputStream;

/*
 * <p>ClassName: dbConnsPool </p>
 * <p>Description: 数据库连接池 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft </p>
 * @Database: LIS
 * @CreateDate：2002-10-04
 */
public class DBConnPool
{
    //虽然没有调用该对象，但是没有的话，无法连接到数据库，采用一种很古怪的方式来实现类的加载
//    private static DBConnPool dbconnpool = new DBConnPool();

    //连接池对象
    private static DBConn dbConns[];
    //设置应用程序的最大连接数，可相对扩充，但是会常驻内存，占用空间，因此大小需要适度
    private static final int nConnCount = 20;

    // @Constructor
    static
    {
        dbConns = new DBConn[nConnCount];
        for (int nIndex = 0; nIndex < nConnCount; nIndex++)
        {
            dbConns[nIndex] = new DBConn();
        }
    }

    //构建函数
    private DBConnPool()
    {}

    /**
     * 获取连接
     * @return DBConn
     */
    static public DBConn getConnection()
    {
        JdbcUrl JUrl = new JdbcUrl();
        //update by wangzw,为了支持WebLogic、apache、WebSphere的连接池，在得到连接的最前面判断
        if (JUrl.getDBType().toUpperCase().equals("WEBLOGICPOOL")
                || JUrl.getDBType().toUpperCase().equals("COMMONSDBCP")
                || JUrl.getDBType().toUpperCase().equals("WEBSPHERE"))
        {
            DBConn tDBConn = new DBConn();
            if (tDBConn.createConnection())
            {
                return tDBConn;
            }
            else
            {
            	System.out.println("1");
                return null;
            }
        }

        try
        {
            DBSemaphore.Lock();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        	System.out.println("2");
            return null;
        }
        int index = 0;
        while(true){
        	//如果没有返回，则表示使用的是自己的连接池
	        for (int nIndex = 0; nIndex < nConnCount; nIndex++)
	        {
	        	index++;
	        	if(index%100==0){
	        		System.out.println(index+"次还没有获得连接!");
	        	}
	            DBConn dbConn = dbConns[nIndex];
	            //判定连接是否被使用
	            if (dbConn.isInUse())
	            {
	                continue;
	            }
	            
	            while(!dbConn.createConnection()){
	            	
	            }
	//            if (!dbConn.createConnection())
	//            {
	//                //如果创建连接失败
	//                DBSemaphore.UnLock();
	//            	System.out.println("3");
	//                return null;
	//            }
	            //如果连接数超过1的话，才输出，否则输出太多，很烦人
	            if (nIndex > 1)
	            {
	//                System.out.println("DBConnPool : get connection, index is " +
	//                        String.valueOf(nIndex));
	            }
	            try
	            {
	                //特殊处理连接的AutoCommit是否已经被设置
	                dbConn.setAutoCommit(true);
	                dbConn.setInUse();
	                DBSemaphore.UnLock();
	                return dbConn;
	            }
	            catch (Exception ex)
	            {
	                ex.printStackTrace();
	
	                DBSemaphore.UnLock();
	            	System.out.println("4");
	                return null;
	            }
	        }
        }
//        //如果全部的连接数都被占用的话，应用程序出错
//        System.out.println("DBConnPool : All connections are in use");
//        //如果为了应用正常流转，可以在这里强制释放掉全部的应用连接数
//        DBSemaphore.UnLock();
//    	System.out.println("5");
//        return null;
    }

    /**
     * 连接查看
     * @param os OutputStream
     */
    public static void dumpConnInfo(OutputStream os)
    {
        try
        {
            if (dbConns == null)
            {
                os.write("all connections are free".getBytes());
                return;
            }

            for (int nIndex = 0; nIndex < nConnCount; nIndex++)
            {
                DBConn dbConn = dbConns[nIndex];

                os.write((String.valueOf(nIndex) +
                        "------------------------------------\r\n\r").
                        getBytes());
                if (dbConn != null && dbConn.isInUse())
                {
                    dbConn.dumpConnInfo(os);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    protected static DBConn[] getDBConns()
    {
        return dbConns;
    }
}

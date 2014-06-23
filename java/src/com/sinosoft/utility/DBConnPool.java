/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.io.OutputStream;

/*
 * <p>ClassName: dbConnsPool </p>
 * <p>Description: ���ݿ����ӳ� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft </p>
 * @Database: LIS
 * @CreateDate��2002-10-04
 */
public class DBConnPool
{
    //��Ȼû�е��øö��󣬵���û�еĻ����޷����ӵ����ݿ⣬����һ�ֺܹŹֵķ�ʽ��ʵ����ļ���
//    private static DBConnPool dbconnpool = new DBConnPool();

    //���ӳض���
    private static DBConn dbConns[];
    //����Ӧ�ó�����������������������䣬���ǻ᳣פ�ڴ棬ռ�ÿռ䣬��˴�С��Ҫ�ʶ�
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

    //��������
    private DBConnPool()
    {}

    /**
     * ��ȡ����
     * @return DBConn
     */
    static public DBConn getConnection()
    {
        JdbcUrl JUrl = new JdbcUrl();
        //update by wangzw,Ϊ��֧��WebLogic��apache��WebSphere�����ӳأ��ڵõ����ӵ���ǰ���ж�
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
        	//���û�з��أ����ʾʹ�õ����Լ������ӳ�
	        for (int nIndex = 0; nIndex < nConnCount; nIndex++)
	        {
	        	index++;
	        	if(index%100==0){
	        		System.out.println(index+"�λ�û�л������!");
	        	}
	            DBConn dbConn = dbConns[nIndex];
	            //�ж������Ƿ�ʹ��
	            if (dbConn.isInUse())
	            {
	                continue;
	            }
	            
	            while(!dbConn.createConnection()){
	            	
	            }
	//            if (!dbConn.createConnection())
	//            {
	//                //�����������ʧ��
	//                DBSemaphore.UnLock();
	//            	System.out.println("3");
	//                return null;
	//            }
	            //�������������1�Ļ�����������������̫�࣬�ܷ���
	            if (nIndex > 1)
	            {
	//                System.out.println("DBConnPool : get connection, index is " +
	//                        String.valueOf(nIndex));
	            }
	            try
	            {
	                //���⴦�����ӵ�AutoCommit�Ƿ��Ѿ�������
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
//        //���ȫ��������������ռ�õĻ���Ӧ�ó������
//        System.out.println("DBConnPool : All connections are in use");
//        //���Ϊ��Ӧ��������ת������������ǿ���ͷŵ�ȫ����Ӧ��������
//        DBSemaphore.UnLock();
//    	System.out.println("5");
//        return null;
    }

    /**
     * ���Ӳ鿴
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

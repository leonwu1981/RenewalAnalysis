package com.sinosoft.xreport.dl;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author houzw
 * @version 1.0
 */

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DBTransaction
{

    private static Statement sm;
    private static Connection conn;


    public DBTransaction()
    {
    }

    public Connection getConnection()
    {
        return conn;
    }

    static void open() throws Exception
    {
        DBConnParam db = new DBConnParam();
        ConnectionFactory.bind("pool", db);
        DataSourceInf ds = ConnectionFactory.lookup("pool");
        try
        {
            conn = ds.getConnection();
            sm = conn.createStatement();
            ResultSet rs = sm.executeQuery("select * from summary_balance");
            while (rs.next())
            {
                System.out.println(rs.getString("name"));
            }

        } finally
        {
            sm.close();
            conn.close();
        }

        ConnectionFactory.unbind("pool");

    }

    public ResultSet execSQL(String sql) throws SQLException
    {
        return sm.executeQuery(sql);
    }

    public int update(String sql) throws SQLException
    {
        int flag = sm.executeUpdate(sql);
        return flag;
    }

    //以下三个方法为游标加速时使用，即成批的更新或插入数据时使用
    public void addBatch(String sql) throws SQLException
    {
        sm.addBatch(sql);
    }

    public boolean commitBatch() throws SQLException //提交游标加速区的数据
    {
        int[] updateCounts = sm.executeBatch();
        if (updateCounts.length >= 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void clearBatch() throws SQLException
    {
        sm.clearBatch();
    }

    public void startTrans() throws SQLException
    {
        conn.setAutoCommit(false);
    }

    public void commitTrans() throws SQLException
    {
        conn.commit();
        conn.setAutoCommit(true);
    }

    public void rollbackTrans() throws SQLException
    {
        conn.rollback();
        conn.setAutoCommit(true);
    }

    public void close() throws SQLException
    {
        if (conn != null)
        {
            conn.close();
            sm.close();
        }
    }


    public static void main(String[] args) throws Exception
    {
        DBTransaction DBTransaction1 = new DBTransaction();
        open();
    }
}
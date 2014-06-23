package com.sinosoft.xreport.dl;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;

import com.sinosoft.xreport.util.Str;

public class TestPool
{


    public TestPool()
    {
    }


    private static DataSourceInf ds = null;
    public static void main(String[] args) throws Exception
    {
        //TestPool testPool1 = new TestPool();
        DBConnParam db = null;
        try
        {
            db = new DBConnParam();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("url is : |" + db.getUrl() + "|");
        System.out.println("user is : |" + db.getUser() + "|");
        System.out.println("password is : |" + db.getPassword() + "|");

        Hashtable ht = ConnectionFactory.connectionPools;
        if (!ht.containsKey("pool"))
        {
            ConnectionFactory.bind("pool", db);
            System.out.println("bind ok.");
        }
        ds = ConnectionFactory.lookup("pool");
        Collection v = new Vector();
        Statement st = null;
        //ConnectionFactory.bind("pool",db);
        //DataSourceInf ds = ConnectionFactory.lookup("pool");
        for (int i = 0; i < 100; i++)
        {
            try
            {
                Connection conn = ds.getConnection();
                try
                {

                    st = conn.createStatement();
                    //System.out.println("Run execute the SQL : " + sql);
                    String sql = "select * from LCPol";
                    ResultSet rs = st.executeQuery(Str.GBKToUnicode(sql));
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int clmNum = rsmd.getColumnCount();
                    //保存字段类型
                    String[] rowStr = new String[clmNum];
                    for (int j = 0; j < clmNum; j++)
                    {
                        rowStr[j] = String.valueOf(rsmd.getColumnName(j + 1));
                    }

                    //保存数据信息

                } finally
                {
                    try
                    {
                        st.close();
                        conn.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            } finally
            {
                System.out.println("search one time");
//        ConnectionFactory.unbind("pool");
//        System.out.println("unbind datasource ok.");
            }
        }
        ConnectionFactory.unbind("pool");
        System.out.println("unbind datasource ok.");
    }
}
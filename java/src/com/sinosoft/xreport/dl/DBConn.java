package com.sinosoft.xreport.dl;

/**
 * 该类用于连接数据库，使用连接池等操作
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author unascribed
 * @version 1.0
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class DBConn
{

    private static String Driver;
    private static String url;
    private static String user;
    private static String password;

    /*
      public DBConn() {
      }
     */
    public DBConn() throws Exception
    {
        DBConnParam dbcon = new DBConnParam();
        DBConn.Driver = dbcon.getDriver();
        DBConn.url = dbcon.getUrl();
        DBConn.user = dbcon.getUser();
        DBConn.password = dbcon.getPassword();
        //Connection con =
        System.out.println("driver is : " + Driver);
        System.out.println("url is : " + url);
        System.out.println("user is : " + user);
        System.out.println("password is : " + password);

    }

    public static void main(String[] args) throws Exception
    {
        DBConn DBConn1 = new DBConn();

        for (int h = 0; h < 100; h++)
        {
            System.out.println(" ========== new connection " + h +
                               "============");
            try
            {
                Class.forName(Driver);
                Connection con = DriverManager.getConnection(url, user,
                        password);
                Statement st = con.createStatement();
                System.out.println("connect success!");
                //ResultSet rs = st.executeQuery("select * from lcpol");
//        ResultSetMetaData rsmd = rs.getMetaData();

//        for(int i = 0 ; i < rsmd.getColumnCount();i++ )
//          System.out.print(rsmd.getColumnName(i+1)+"    ");

            }
            catch (SQLException e)
            {
                System.out.println(e.toString());
            }
            System.out.println(" ========== end connection " + h +
                               "============");
        }

    }


}

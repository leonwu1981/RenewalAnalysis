package com.sinosoft.xreport.dl;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author unascribed
 * @version 1.0
 */

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class DBQuery
{

    private static Statement sm;
    private static Connection conn;

    public DBQuery()
    {

    }

    public DBQuery(Connection conn) throws SQLException
    {
        DBQuery.conn = conn;
        DBQuery.sm = conn.createStatement();
    }

    public Vector execSQL(String sql) throws SQLException
    {
        ResultSet rs = sm.executeQuery(sql);
        Vector v = new Vector();
        while (rs.next())
        {
            v.addElement(rs);
        }

        return v;
    }

    public int updateSQL(String sql) throws SQLException
    {
        int flag = sm.executeUpdate(sql);
        return flag;
    }

    public static void main(String[] args)
    {
        DBQuery DBQuery1 = new DBQuery();
    }

}

/*
 * 创建日期 2005-7-15
 * 作者：王育春
 * 邮箱:wangyc@sinosoft.com.cn
 */

package com.sinosoft.utility.treetable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SysConst;

public class DataAccess {
    private Connection conn;

    public DataAccess() {
        this.conn = DBConnPool.getConnection();
    }

    public DataAccess(Connection conn) {
        this.conn = conn;
    }

    public Connection getConnection() {
        return conn;
    }

    public void setAutoCommit(boolean bCommit) throws SQLException {
        this.conn.setAutoCommit(bCommit);
    }

    public void commit() throws SQLException {
        this.conn.commit();
    }

    public void rollback() throws SQLException {
        this.conn.rollback();
    }

    public void close() throws SQLException {
        this.conn.close();
    }

    public DataTable executeDataTable(String sql) {
   
        Statement stmt = null;
        ResultSet rs = null;
        DataTable dt = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            //if (rs.next() ) {
                dt = new DataTable(rs);
           // }
        } catch (SQLException e) {
            System.out
            .println("------------DataAccess.executeDataTable:Err-------------"
                    + sql);
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                stmt.close();
                rs = null;
                stmt = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return dt;
    }

    /**
     * @param sql
     * @param pageSize 每页记录数
     * @param pageIndex 页数，从0开始
     * @return
     */
    public DataTable executePagedDataTable(String sql, int pageSize,
            int pageIndex) {
        if (pageSize < 1) {
            throw new RuntimeException(
                "DataAccess.executePagedDataTable：每页记录数不能小于1！");
        }
        if (pageIndex < 0) {
            throw new RuntimeException(
                "DataAccess.executePagedDataTable：页数不能小于1！");
        }
        String dbName = null;
        try {
            dbName = SysConst.DBTYPE.toLowerCase();//conn.getMetaData().getDatabaseProductName().toLowerCase();
        } catch (Exception e1) {
            e1.printStackTrace();
            throw new RuntimeException("发生错误，未得到数据库的产品名!");
        }
        String tSQL = "";
        if (dbName.indexOf("db2") > -1) {
            tSQL = "select * from (select temp.*,ROW_NUMBER() over() as row___Num "
                    + "from ("
                    + sql
                    + ") as temp) as t where row___Num>"
                    + (pageIndex * pageSize)
                    + " and row___Num<="
                    + ((pageIndex + 1) * pageSize);
        } else if (dbName.indexOf("mysql") > -1) {
            tSQL = tSQL + " limit " + (pageIndex * pageSize) + ","
                    + ((pageIndex + 1) * pageSize);
        } else if (dbName.indexOf("oracle") > -1) {
            tSQL = "select * from (select temp.*,rownum row___Num from  ("
                    + sql + ") temp) where row___Num >"
                    + (pageIndex * pageSize) + " and row___Num<="
                    + ((pageIndex + 1) * pageSize);
        }
        // 其它数据库暂没有实现
        System.out.println("------------DataAccess.executePagedDataTable-------------"+tSQL);
        Statement stmt = null;
        ResultSet rs = null;
        DataTable dt = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(tSQL);
            dt = new DataTable(rs);
        } catch (SQLException e) {
        	System.out
            .println("------------DataAccess.executeDataTable:Err-------------"
                    + sql);
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                stmt.close();
                rs = null;
                stmt = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (dbName.indexOf("db2") > -1 || dbName.indexOf("oracle") > -1) {
            dt.deleteColumn("row___Num");
        }
        return dt;
    }

    public Object excuteOneValue(String sql) throws SQLException {

        Statement stmt = null;
        ResultSet rs = null;
        Object t = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                t = rs.getObject(1);
            }
        } catch (SQLException e) {
          System.out.println("------------DataAccess.excuteOneValue:Err-------------"
          + sql);
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                stmt.close();
                rs = null;
                stmt = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public int executeNoQuery(String sql) throws SQLException {
       
        System.out.println(sql);
        Statement stmt = null;
        int t = -1;
        try {
            stmt = conn.createStatement();
            t = stmt.executeUpdate(sql);
        } catch (SQLException e) {
        	 System.out.println("------------DataAccess.executeNoQuery:Err-------------"
                     + sql);
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
                stmt = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public static void main(String[] args) {
        try {
            DataAccess da = new DataAccess();
            ExeSQL eSQL = new ExeSQL();
            long t = System.currentTimeMillis();
            for (int i = 0; i < 1; i++) {
                //String sql = "select nodecode from ldmenu where nodecode='1001'";
                DataTable dt = da.executeDataTable("select * from lcpol");
                //				for(int j=0;j<dt.getRowCount();j++){
                //					for(int k=0;k<dt.getColCount();k++){
                //						System.out.print(dt.getString(j, k)+"\t");
                //					}
                //					System.out.print("\n");
                //				}
                //				SSRS rs = eSQL.execSQL("select * from lcpol");
                //				for(int j=1;j<=rs.getMaxRow();j++){
                //					for(int k=1;k<=rs.getMaxCol();k++){
                //						System.out.print(rs.GetText(j, k)+"\t");
                //					}
                //					System.out.print("\n");
                //				}
                System.out.println(i + 1);
            }
            System.out.println("------------------------"
                    + (System.currentTimeMillis() - t));
            da.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

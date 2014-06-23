package com.sinosoft.utility.treetable;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBUtil {
	
	/**
	 * @param conn 数据库连接
	 * @param tableName 表名
	 * @return 主键名字符串数组
	 */
	public static String[] getPrimaryKey(Connection conn,ResultSet rs){
		DatabaseMetaData dbmd = null;
		ResultSetMetaData rsmd = null;
		try {
			dbmd = conn.getMetaData();
			rsmd = rs.getMetaData();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getPrimaryKey(dbmd,rsmd);
	}	
	
	/**
	 * @param dbmd 数据库元数据
	 * @param tableName 表名
	 * @return 主键名字符串数组
	 */
	public static String[] getPrimaryKey(DatabaseMetaData dbmd,ResultSetMetaData rsmd){
		try {
			long s = System.currentTimeMillis();
			ResultSet rs = dbmd.getPrimaryKeys(null,null, rsmd.getTableName(1));
			System.out.println("=================================="+(System.currentTimeMillis()-s));
			StringBuffer sb = new StringBuffer();
			ArrayList al = new ArrayList();
			while(rs.next()){
				//System.out.println(rs.getObject(4));
				al.add(rs.getString(4));
			}
			String[] t = new String[al.size()];
			for(int i=0;i<al.size();i++){
				t[i] = (String)al.get(i);
			}

			return t;
		} catch (SQLException e) {
			System.out.println("驱动程序不支持getPrimaryKey。");
			return null;
		}
	}
	
	public static DataTable executeDataTable(String sql){
		DataAccess da = new DataAccess();
		DataTable dt = null;
		try {
			dt = da.executeDataTable(sql);
			da.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				da.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dt;
	}
	
	public static DataTable executePagedDataTable(String sql, int pageSize,
			int pageIndex){
		DataAccess da = new DataAccess();
		DataTable dt = null;
		try {
			dt = da.executePagedDataTable(sql,pageSize,pageIndex);
			da.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				da.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dt;
	}
	
	public static Object excuteOneValue(String sql){
		System.out.println(sql);
		DataAccess da = new DataAccess();
		Object t = null;
		try {
			t = da.excuteOneValue(sql);
			da.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				da.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return t;
	}
	
	public static int executeNoQuery(String sql){
		DataAccess da = new DataAccess();
		int t = -1;
		try {
			t = da.executeNoQuery(sql);
			da.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				da.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return t;
	}
}

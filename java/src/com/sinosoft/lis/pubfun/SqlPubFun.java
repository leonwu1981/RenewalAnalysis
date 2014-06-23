package com.sinosoft.lis.pubfun;

public class SqlPubFun {
	//创建 (col in ('item1','item2','item3'...))的语句,如果超过1000,换成(col in ('item1','item2','item3'...) or col in ('item1001','item1002'...))
	public static String createInSql(String col,String[] items){
		
		String sql = "(";
		for (int i = 0; i < items.length; i++) {
			if(i%1000==0){
				sql = sql + col + " in (";
			}
			sql += "'" + items[i] + "',";
			if(i%1000==999&&i!=items.length-1){
				sql = sql.substring(0, sql.length() - 1);//去掉最后的,
				sql += ") or ";
			}
		}
		sql = sql.substring(0, sql.length() - 1);//去掉最后的,
		sql += "))";
		return sql;

	}

}

/*
 * 创建日期 2005-7-15
 * 作者：王育春
 * 邮箱:wangyc@sinosoft.com.cn
 */
package com.sinosoft.utility.treetable;

public interface IDataCollection {
	public abstract DataColumn[] getDataColumns();
	public abstract Object[][] getDataValues();
}

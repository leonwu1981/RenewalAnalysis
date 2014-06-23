/*
 * �������� 2005-7-15
 * ���ߣ�������
 * ����:wangyc@sinosoft.com.cn
 */
package com.sinosoft.utility.treetable;

public class DataColumnValues extends DataColumn implements IDataCollection {
	
	private Object[] columnValues;
	
	//���½��������캯������Ϊfriendly���Է�ֹ�ӳ���������ı�ĵط�����ʵ��
	DataColumnValues(){}
	DataColumnValues(String columnName,int columnType,boolean PrimaryKeyPart,boolean allowNull){
		this.ColumnName = columnName;
		this.ColumnType = columnType;
		this.isAllowNull = allowNull;
	}
	
	public void setValues(Object[] values){
		columnValues = values;
	}
	
	public int getRowCount(){
		if(columnValues==null){
			return 0;
		}else{
			return columnValues.length;
		}
	}
	
	public Object[][] getDataValues(){
		Object[][] t = new Object[columnValues.length][1];
		for(int i=0;i<columnValues.length;i++){
			t[i][0] = columnValues[i];
 		}
		return t;
	}
	
	public DataColumn[] getDataColumns(){
		DataColumn[]  t = {new DataColumn(ColumnName,ColumnType,isAllowNull)};
		return t;
	}
}

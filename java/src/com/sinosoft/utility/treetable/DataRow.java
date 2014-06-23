/*
 * �������� 2005-7-15
 * ���ߣ�������
 * ����:wangyc@sinosoft.com.cn
 */
package com.sinosoft.utility.treetable; 

public class DataRow implements IDataCollection {
	protected DataColumn[] columnTypes;
	protected Object[] columnValues;
	
	public DataRow(DataColumn[] types,Object[] values){
		columnTypes = types;
		columnValues = values;
	}
	
	public Object get(int index){
		if(columnValues==null){
			return null;
		}
		if(index<0||index>=columnValues.length){
			throw new RuntimeException("DataRow��û��ָ�����У�"+index);			
		}
		return columnValues[index];
	}
	
	
	public String getString(int index){
		if(columnValues[index]!=null){
			return String.valueOf(columnValues[index]).trim();
		}else{
			return null;
		}
	}
	
	public Object get(String columnName){
		if(columnName==null||columnName.equals("")){
			throw new RuntimeException("���ܴ�ȡ����Ϊ�յ���");			
		}
		for(int i=0;i<columnValues.length;i++){
			if(columnTypes[i].getColumnName().equalsIgnoreCase(columnName)){
				return columnValues[i];
			}
		}
		throw new RuntimeException("ָ��������û���ҵ�");			
	}
	
	public void set(int index,Object value){
		if(columnValues==null){
			return;
		}
		if(index<0||index>=columnValues.length){
			throw new RuntimeException("DataRow��û��ָ�����У�"+index);			
		}
		columnValues[index] = value;
	}

	
	public void set(String columnName,Object value){
		if(columnName==null||columnName.equals("")){
			throw new RuntimeException("���ܴ�ȡ����Ϊ�յ���");			
		}
		boolean flag = false;
		for(int i=0;i<columnTypes.length;i++){
			if(columnTypes[i].getColumnName().equalsIgnoreCase(columnName)){
				columnValues[i] = value;
				flag = true;
			}
		}
		if(!flag){
			throw new RuntimeException("ָ��������û���ҵ�");	
		}
					

	}
	
	public String getString(String columnName){
		Object o = get(columnName);
		if(o!=null){
			return String.valueOf(o);
		}else{
			return null;
		}
	}

	
	public DataColumn getColumnType(int index){
		if(index<0||index>=columnTypes.length){
			throw new RuntimeException("DataRow��û��ָ�����У�"+index);			
		}
		return columnTypes[index];
	}
	
	public DataColumn getColumnType(String columnName){
		if(columnName==null||columnName.equals("")){
			throw new RuntimeException("���ܴ�ȡ����Ϊ�յ���");			
		}
		for(int i=0;i<columnValues.length;i++){
			if(columnTypes[i].getColumnName().equals(columnName)){
				return columnTypes[i];
			}
		}
		throw new RuntimeException("ָ��������û���ҵ�");			
	}
	
	public int getColumnCount(){
		return columnTypes.length;
	}
	
	public Object[][] getDataValues(){
		Object[][] t = new Object[columnValues.length][1];
		for(int i=0;i<columnValues.length;i++){
			t[i][0] = columnValues[i];
 		}
		return t;
	}
	
	
	public DataColumn[] getDataColumns(){
		return columnTypes;
	}
}

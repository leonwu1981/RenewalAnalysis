/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.util.Comparator;
import java.util.List;

import java.util.Vector;

public class ListTable
{
    private Vector myVector = new Vector();
    private int col; //�����
    private String name; //����


    // @Constructor
    public ListTable()
    {
    }

    // @Method
    //�½�һ��ListTable������Ϊһ���ַ������飬���������ֵ
    public void add(String[] temparray)
    {
        myVector.addElement(temparray);
    }


    //�õ�ָ�����е�ֵ
    public String getValue(int column, int row)
    {
        String[] temparray = new String[column];
        temparray = (String[]) myVector.get(row);
        return temparray[column];
    }
    
    //�޸�ָ�����е�ֵ
    public void setValue(int column, int row,String Value)
    {
        String[] temparray = new String[column];
        temparray = (String[]) myVector.get(row);
        temparray[column] = Value;
        myVector.set(row, temparray);

    }

    //ȡ�õ�i�е��б������ֵ
    public String[] get(int i)
    {
        return (String[]) myVector.get(i);
    }

    //�õ������б�Ĵ�С
    public int size()
    {
        return myVector.size();
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }


    public int getCol()
    {
        return this.col;
    }
    
    //����ASR20082334����Ҫ������ֶ�������Ӵ˱�������ListTable����е�����ֱ�ӽ�������20081008
    public Vector getVector(){
        return this.myVector;
   }
    //col ������ĵڼ������򣬴�0��ʼ
    //ascendOrDescend ������� 1 ���� 0 ����
    public  boolean sortListTable (int col, int   ascendOrDescend)
    {
    	if(myVector.isEmpty())
    	{
    		System.out.println("Vector����Ϊ�գ�");
    		return  false;
    	}
    	else
    	{
    		SortListTable   tSortListTable=new   SortListTable();  
    		Vector   v=new   Vector();   
    		v=tSortListTable.returnVBysortElementInVector(myVector,col,ascendOrDescend);
    		myVector.clear();
    		myVector = v;
    		return true;
    	}
    }
}

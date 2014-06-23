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
    private int col; //表的行
    private String name; //表名


    // @Constructor
    public ListTable()
    {
    }

    // @Method
    //新建一个ListTable，参数为一个字符串数组，包含所需的值
    public void add(String[] temparray)
    {
        myVector.addElement(temparray);
    }


    //得到指定行列的值
    public String getValue(int column, int row)
    {
        String[] temparray = new String[column];
        temparray = (String[]) myVector.get(row);
        return temparray[column];
    }
    
    //修改指定行列的值
    public void setValue(int column, int row,String Value)
    {
        String[] temparray = new String[column];
        temparray = (String[]) myVector.get(row);
        temparray[column] = Value;
        myVector.set(row, temparray);

    }

    //取得第i行的列表的所有值
    public String[] get(int i)
    {
        return (String[]) myVector.get(i);
    }

    //得到整个列表的大小
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
    
    //财务ASR20082334需求要按相关字段排序，添加此变量，将ListTable结果中的内容直接进行排序20081008
    public Vector getVector(){
        return this.myVector;
   }
    //col 按数组的第几列排序，从0开始
    //ascendOrDescend 升序或降序 1 升序 0 降序
    public  boolean sortListTable (int col, int   ascendOrDescend)
    {
    	if(myVector.isEmpty())
    	{
    		System.out.println("Vector容器为空！");
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

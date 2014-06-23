/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.util.Vector;


/**类的名称：TextTag
 *类的描述：包含打印模板中动态标签的字段名和值
 *
 *
 *
 *
 *
 */

public class TextTag
{

    private Vector myVector = new Vector();

    public TextTag()
    {

    }

    // @Method
    //增加一个动态标签，参数为标签名，值
    public Vector add(String name, String value)
    {
        if (value != null)
        {
            String[] temparray =
                                 {
                                 name, value};
            myVector.addElement(temparray);
            return myVector;
        }
        else
        {
            String[] temparray =
                                 {
                                 name, " "};
            myVector.addElement(temparray);
            return myVector;
        }
    }

    public Vector add(String name, int value)
    {
        Integer ivalue = new Integer(value);
        String svalue = ivalue.toString();
        String[] temparray =
                             {
                             name, svalue};
        myVector.addElement(temparray);
        return myVector;

    }

    public Vector add(String name, long value)
    {
        Long ivalue = new Long(value);
        String svalue = ivalue.toString();
        String[] temparray =
                             {
                             name, svalue};
        myVector.addElement(temparray);
        return myVector;

    }

    public Vector add(String name, float value)
    {
        Float fvalue = new Float(value);
        String svalue = fvalue.toString();
        String[] temparray =
                             {
                             name, svalue};
        myVector.addElement(temparray);
        return myVector;
    }

    public Vector add(String name, double value)
    {
        Double fvalue = new Double(value);
        String svalue = fvalue.toString();
        String[] temparray =
                             {
                             name, svalue};
        myVector.addElement(temparray);
        return myVector;
    }


    //取得第i个动态标签
    public Object get(int i)
    {
        return myVector.get(i);

    }

    //得到整个动态标签列表的大小
    public int size()
    {
        return myVector.size();

    }

    //查找name对应的value,成功返回值，不成功返回null
    /****************************************************
     *public String getValue(String name){
     *	int tsize=this.size();
     *	int flag=0;
     *	String[] temparray=new String[2];
     *	for (int i=0;i<=tsize-1;i++){
     *		 temparray=(String[])this.get(i);
     *		 if (temparray[0]==name){
     *		 	flag=1;
     *		   return temparray[1];
     *		   }
     *	      }
     *	  if (flag==0)
     *	     return  "null";
     *    }
     */
   public Vector getMyVector()
   {
	   return this.myVector;
   }

}

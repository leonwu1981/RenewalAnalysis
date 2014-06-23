/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.util.Vector;


/**������ƣ�TextTag
 *���������������ӡģ���ж�̬��ǩ���ֶ�����ֵ
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
    //����һ����̬��ǩ������Ϊ��ǩ����ֵ
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


    //ȡ�õ�i����̬��ǩ
    public Object get(int i)
    {
        return myVector.get(i);

    }

    //�õ�������̬��ǩ�б�Ĵ�С
    public int size()
    {
        return myVector.size();

    }

    //����name��Ӧ��value,�ɹ�����ֵ�����ɹ�����null
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

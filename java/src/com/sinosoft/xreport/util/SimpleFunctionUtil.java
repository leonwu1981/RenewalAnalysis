package com.sinosoft.xreport.util;


/**
 * �򵥺�����������.
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */

public class SimpleFunctionUtil
{

    public SimpleFunctionUtil()
    {
    }

//  public void getAllFunction(String context,Stack target) throws NullPointerException
//  {
//
//    context=StringUtility.trimAll(context); //trim whiteSpace
//    Stack s=new Stack();
//    int[][] branchketPos=new int[20][2];
//    char leftBracket='(';
//    Vector v=new Vector();
//
//
//    //ƥ�����Ŷ�...
//    for(int i=0;i<context.length();i++)
//    {
//      char c=context.charAt(i);
//      if(c==leftBracket)
//      {
//
//      }
//
//    }
//
//
//  }

    /**���Ŷ�*/
    class BracketG
    {
        /**������λ��*/
        int left = -1;
        /**������λ��*/
        int right = Integer.MAX_VALUE;

        /**λ��������֮��*/
        boolean isInner(int i)
        {
            return i > left && i < right;
        }
    }


    public static void main(String[] args)
    {
        SimpleFunctionUtil simpleFunctionUtil1 = new SimpleFunctionUtil();
    }
}
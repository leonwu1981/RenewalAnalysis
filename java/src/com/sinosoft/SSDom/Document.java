package com.sinosoft.SSDom;

import java.io.*;
import java.util.*;
/**
 * <p>Title: lis</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: sinosoft</p>
 * @author lh
 * @version 1.0
 */

public class Document {

  private PrintWriter mOut = null;
  private String mFileName = null;
  private Stack mStack = new Stack();
  private boolean mFlag=false;

  public Document() {
  }

  public Document(String tFileName) {
    mFileName = tFileName;
  }

  protected Stack getStack()
  {
    return mStack;
  }

  public void startOutput(String aEncoding)
      throws Exception
  {
    mOut = new PrintWriter(new FileOutputStream(mFileName));
    mOut.println("<?xml version=\"1.0\" encoding=\"" + aEncoding + "\" ?>");
  }

  public void addRoot(Element aElement)
      throws Exception
  {
    if (mFlag==true)
    {
      Exception tEx = new Exception("XML 文档只能有一个顶层元素！");
      throw tEx;
    }
    mFlag = true;
    String tEleName = aElement.getEleName();
    String tEleByname = tEleName + Common.mDivSign + "1";
    aElement.setEleByname(tEleByname);
//      mStack.push(tEleName);
    mStack.push(tEleByname);
    aElement.setStack(mStack);
    mOut.print("<"+tEleName+">");
    String tEleText = aElement.getEleText();
    if (tEleText!=null)
      mOut.print(tEleText);
    aElement.setPrintWriter(mOut);
    mOut.flush();
  }

  public void endOutput()
      throws Exception
  {
    int tNum = mStack.size();
    for(int i=1;i<=tNum;i++)
    {
      if(i!=1)
        Common.printBlank(tNum-i,mOut);
      String tEleByname = (String)mStack.pop();
      String tEleName = Common.parseEleName(tEleByname);
      mOut.print("</"+tEleName+">");
      if(i!=tNum)
        mOut.print("\r\n");
    }
    mOut.flush();
    mOut.close();
  }

  public static void main(String[] args) {
    System.out.println("------start---:"+Common.getCurrentTime());
    Document doc = new Document("E:/aa.xml");
    try
    {
      doc.startOutput("gb2312");
      Element A = new Element("A");
      doc.addRoot(A);
//      for(int i=1;i<=1000000;i++)
//      {
      Element B = new Element("B");
      B.setEleText("1000");
      A.addContent(B);
      Element C = new Element("C");
      A.addContent(C);
      Element D = new Element("C");
      C.addContent(D);
      Element E = new Element("E");
      E.setEleText("2000");
      D.addContent(E);
      Element F = new Element("F");
      F.setEleText("3000");
      C.addContent(F);
//      }
      doc.endOutput();
      System.out.println("------end---:"+Common.getCurrentTime());
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }


//    String aa="aaaa||bb";
//    int i = aa.indexOf("||");
//    String bb = aa.substring(i);
//    String cc = aa.substring(0,i);
//    System.out.println(bb);
//    System.out.println(cc);


//    Stack a = new Stack();
//    a.push("bb");
//    a.push("aa");
//    a.push("aa");
//    a.push("ss");
//    System.out.println(a.size());
//    System.out.println(a.search("bb"));
//    System.out.println(a.search("aa"));
//    System.out.println(a.peek());

//    try
//    {
//      PrintWriter out = new PrintWriter(new FileOutputStream("E://aa.txt"));
//      out.print("asdfdsaf\r\nadf");
//      out.close();
//    }
//    catch(Exception ex)
//    {
//      ex.printStackTrace();
//    }

  }
}
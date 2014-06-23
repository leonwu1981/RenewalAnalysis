package com.sinosoft.SSDom;

import java.util.*;
import java.io.*;
/**
 * <p>Title: lis</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: sinosoft</p>
 * @author lh
 * @version 1.0
 */

public class Element {

  private String mEleName = null;
  private String mEleByname = null;//堆栈中的元素名
  private String mEleText = null;
  private Stack mStack = null;
  private PrintWriter mOut = null;

  public Element() {
  }

  public Element(String aEleName) {
    mEleName = aEleName;
  }

  protected String getEleName()
  {
    return mEleName;
  }

  public void setEleByname(String aEleByname)
  {
    mEleByname = aEleByname;
  }

  public void setEleText(String aEleText)
  {
    mEleText = aEleText;
  }

  protected String getEleText()
  {
    return mEleText;
  }

  protected void setStack(Stack aStack)
  {
    mStack = aStack;
  }

  protected void setPrintWriter(PrintWriter aOut)
  {
    mOut = aOut;
  }

  public void addContent(Element aChildEle)
      throws Exception
  {
    int tSize = mStack.size();
//      int tSer = mStack.search(mEleName);
    int tSer = mStack.search(mEleByname);
    if (tSer==-1)
    {
      Exception tEx = new Exception("元素父子结点关系错误！");
      throw tEx;
    }
    if (tSer==1){
      mOut.print("\r\n");
    }
    else {
      for(int i=1;i<tSer;i++)
      {
        if(i!=1)
          Common.printBlank(tSize-i,mOut);
        String tEleByname = (String)mStack.pop();
        String tEleName = Common.parseEleName(tEleByname);
        mOut.print("</"+tEleName+">");
        mOut.print("\r\n");
      }
    }

    String tChildEleName = aChildEle.getEleName();
    int tNo = tSize - tSer + 2;
    String tChildEleByname = tChildEleName + Common.mDivSign + tNo;
    aChildEle.setEleByname(tChildEleByname);

    mStack.push(tChildEleByname);
    aChildEle.setStack(mStack);
    int tCurSize = mStack.size();
    Common.printBlank(tCurSize-1,mOut);
    mOut.print("<"+tChildEleName+">");
    String tEleText = aChildEle.getEleText();
    if (tEleText!=null)
      mOut.print(tEleText);
    aChildEle.setPrintWriter(mOut);
    mOut.flush();
  }

  public static void main(String[] args) {
    Element element1 = new Element();
  }
}
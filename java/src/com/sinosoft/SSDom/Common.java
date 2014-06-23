package com.sinosoft.SSDom;

import java.io.*;
import java.text.*;
import java.util.*;
/**
 * <p>Title: lis</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: sinosoft</p>
 * @author lh
 * @version 1.0
 */

public class Common {

  protected static final String mDivSign = "||";

  public Common() {
  }

  protected static void printBlank(int aNum,PrintWriter aOut)
  {
    for(int i=0;i<aNum;i++)
    {
      aOut.print("  ");
    }
  }

  protected static String parseEleName(String aEleByname)
  {
    int i = aEleByname.indexOf(Common.mDivSign);
    String tEleName = aEleByname.substring(0,i);
    return tEleName;
  }

  /**
   * 得到当前系统时间
   */
  protected static String getCurrentTime()
  {
    String pattern="HH:mm:ss";
    SimpleDateFormat df = new SimpleDateFormat(pattern);
    Date today=new Date();
    String tString = df.format(today);
    return tString;
  }

  public static void main(String[] args) {
    Common common1 = new Common();
  }
}
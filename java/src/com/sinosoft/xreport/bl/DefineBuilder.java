//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\DefineBuilder.java

package com.sinosoft.xreport.bl;

/**
 * 报表定义缓存.
 * 保证定义读取后不需重复解析,报表级数据重用.
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */
public class DefineBuilder
{
    public DefineReader theDefineReader;
    public ReportDefine theReportDefine;

    public DefineBuilder()
    {

    }


}

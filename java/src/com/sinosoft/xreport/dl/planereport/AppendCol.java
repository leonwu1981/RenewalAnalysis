package com.sinosoft.xreport.dl.planereport;

/**
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author wuxiao
 * @version 1.0
 */
public class AppendCol
{

    private String colName = "";
    private String dbName = "";

    public AppendCol()
    {}

    /**
     * 获取附加数据源依赖列名
     * @return 附加数据源依赖列名
     */
    public String getColName()
    {
        return colName;
    }

    /**
     * 设置附加数据源依赖列名
     * @param param 附加数据源依赖列名
     */
    public void setColName(String param)
    {
        colName = param;
    }

    /**
     * 获取附加数据源依赖列对应的数据库名
     * @return 附加数据源依赖列对应的数据库名
     */
    public String getDbName()
    {
        return dbName;
    }

    /**
     * 设置附加数据源依赖列对应的数据库名
     * @param param 附加数据源依赖列对应的数据库名
     */
    public void setDbName(String param)
    {
        dbName = param;
    }

    /**
     * 获取附加数据源依赖列的xml串
     * @return 附加数据源依赖列的xml串
     */
    public String toXML()
    {
        return "<appendCol name=\"" + colName
                + "\" dbName=\"" + dbName
                + "\"/>\r\n";
    }
}
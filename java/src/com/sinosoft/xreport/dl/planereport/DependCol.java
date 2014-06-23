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
public class DependCol
{

    private String col;
    private String dbName;

    public DependCol()
    {}

    /**
     * 取得小计、合计依据列名称
     * @return 列名称
     */
    public String getCol()
    {
        return col;
    }

    /**
     * 取得小计、合计依据列数据库列名
     * @return 数据库列名
     */
    public String getDbName()
    {
        return dbName;
    }

    /**
     * 设置小计、合计依据列名称
     * @param param 列名称
     */
    public void setCol(String param)
    {
        col = param;
    }

    /**
     * 设置小计、合计依据列数据库列名
     * @param param 数据库列名
     */
    public void setDbName(String param)
    {
        dbName = param;
    }

    /**
     * 返回XML字符串
     * @return XML字符串
     */
    public String toXML()
    {
        String sXML = "<dependCol col=\"" + col
                      + "\" dbname=\"" + dbName
                      + "\"/>\r\n";
        return sXML;
    }
}
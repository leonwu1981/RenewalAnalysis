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
public class ViewCol
{

    private String col;
    private String dbName;
    private boolean view;
    private String src;
    private String formula;
    private String location;

    public ViewCol()
    {}

    /**
     * 取得数据列名称
     * @return 数据列名称
     */
    public String getCol()
    {
        return col;
    }

    /**
     * 取得数据列的数据库表名
     * @return 数据库表名
     */
    public String getDbName()
    {
        return dbName;
    }

    /**
     * 取得数据列是否显示
     * @return 是否显示
     */
    public boolean isView()
    {
        return view;
    }

    /**
     * 取得数据列的来源，例如从数据库取数=“DB”
     * @return 来源
     */
    public String getSource()
    {
        return src;
    }

    /**
     * 取得数据列的计算公式
     * @return 计算公式
     */
    public String getFormula()
    {
        return formula;
    }

    /**
     * 取得数据列的显示位置
     * @return 显示位置
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * 设置数据列的名称
     * @param param 名称
     */
    public void setCol(String param)
    {
        col = param;
    }

    /**
     * 设置数据列的数据库表名
     * @param param 数据库表名
     */
    public void setDbName(String param)
    {
        dbName = param;
    }

    /**
     * 设置数据列的显示属性
     * @param param 是否显示
     */
    public void setView(boolean param)
    {
        view = param;
    }

    /**
     * 设置数据列的来源
     * @param param 来源
     */
    public void setSource(String param)
    {
        src = param;
    }

    /**
     * 设置数据列的计算公式
     * @param param 计算公式
     */
    public void setFormula(String param)
    {
        formula = param;
    }

    /**
     * 设置数据列的显示位置
     * @param param 显示位置
     */
    public void setLocation(String param)
    {
        location = param;
    }

    /**
     * 返回XML字符串
     * @return XML字符串
     */
    public String toXML()
    {
        String sXML = "<viewCol col=\"" + col
                      + "\" dbname=\"" + dbName
                      + "\" view=\"" + (view ? 1 : 0)
                      + "\" src=\"" + src
                      + "\" formula=\"" + formula
                      + "\" location=\"" + location + "\"/>\r\n";
        return sXML;
    }
}
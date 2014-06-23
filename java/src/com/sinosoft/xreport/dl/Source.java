package com.sinosoft.xreport.dl;


public interface Source
{

    /**
     * 读取数据源的id号
     * @return 数据源id号
     */
    public String getDataSourceId();

    /**
     * 读取数据源的名称
     * @return 数据源名称
     */
    public String getDataSourceName();


    /**
     * 设置数据源的id号
     * @param strDataSourceId 数据源id号
     */
    public void setDataSourceId(String strDataSourceId);

    /**
     * 设置数据源的名称
     * @param strDataSourceName 数据源名称
     */
    public void setDataSourceName(String strDataSourceName);

    public String toString();

    public int hashCode();

    public boolean equals(Object obj);
}
package com.sinosoft.xreport.dl;


public interface Dim
{

    public void setSuperDimenId(String SuperDimenId);


    /**
     * 读取当前维的上一级维的代码
     * @param 当前维的上一级维的代码
     */
    public String getSuperDimenId();


    public void setDimenId(String DimenId);


    /**
     * 读取当前维的代码
     * @param 当前维的代码
     */
    public String getDimenId();


    /**
     * 读取当前维的名称
     * @param 当前维的名称
     */
    public String getDimenName();


    public void setDimenName(String DimenName);


    /**
     * 读取当前维的所属数据源的代码
     * @param 当前维的所属数据源的代码
     */
    public String getDataSourceId();


    public void setDataSourceId(String DataSourceId);

    public String toString();

    public int hashCode();

    public boolean equals(Object obj);

}
package com.sinosoft.xreport.dl;


public interface Dim
{

    public void setSuperDimenId(String SuperDimenId);


    /**
     * ��ȡ��ǰά����һ��ά�Ĵ���
     * @param ��ǰά����һ��ά�Ĵ���
     */
    public String getSuperDimenId();


    public void setDimenId(String DimenId);


    /**
     * ��ȡ��ǰά�Ĵ���
     * @param ��ǰά�Ĵ���
     */
    public String getDimenId();


    /**
     * ��ȡ��ǰά������
     * @param ��ǰά������
     */
    public String getDimenName();


    public void setDimenName(String DimenName);


    /**
     * ��ȡ��ǰά����������Դ�Ĵ���
     * @param ��ǰά����������Դ�Ĵ���
     */
    public String getDataSourceId();


    public void setDataSourceId(String DataSourceId);

    public String toString();

    public int hashCode();

    public boolean equals(Object obj);

}
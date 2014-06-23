package com.sinosoft.xreport.dl;


public interface Source
{

    /**
     * ��ȡ����Դ��id��
     * @return ����Դid��
     */
    public String getDataSourceId();

    /**
     * ��ȡ����Դ������
     * @return ����Դ����
     */
    public String getDataSourceName();


    /**
     * ��������Դ��id��
     * @param strDataSourceId ����Դid��
     */
    public void setDataSourceId(String strDataSourceId);

    /**
     * ��������Դ������
     * @param strDataSourceName ����Դ����
     */
    public void setDataSourceName(String strDataSourceName);

    public String toString();

    public int hashCode();

    public boolean equals(Object obj);
}
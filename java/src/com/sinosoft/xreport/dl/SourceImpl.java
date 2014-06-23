package com.sinosoft.xreport.dl;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author houzw
 * @version 1.0
 */


public class SourceImpl extends TreeNodeImpl implements Source
{

    private String dataSourceID = "";
    private String dataSourceName = "";

    //public SourceImpl() {
    //}

    /**
     * ��ȡ����Դ��id��
     * @return ����Դid��
     */
    public String getDataSourceId()
    {
        return this.dataSourceID;
    }

    /**
     * ��ȡ����Դ������
     * @return ����Դ����
     */
    public String getDataSourceName()
    {
        return this.dataSourceName;
    }


    /**
     * ��������Դ��id��
     * @param strDataSourceId ����Դid��
     */
    public void setDataSourceId(String strDataSourceId)
    {
        this.dataSourceID = strDataSourceId;
    }

    /**
     * ��������Դ������
     * @param strDataSourceName ����Դ����
     */
    public void setDataSourceName(String strDataSourceName)
    {
        this.dataSourceName = strDataSourceName;
    }

    public String toString()
    {
        return this.dataSourceName;
    }

    public int hashCode()
    {
        return dataSourceID.hashCode();
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        SourceImpl other = (SourceImpl) obj;
        return
                dataSourceID.equals(other.getDataSourceId())
                && dataSourceName.equals(other.getDataSourceName());
    }
}
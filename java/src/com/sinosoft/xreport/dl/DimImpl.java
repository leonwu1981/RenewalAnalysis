package com.sinosoft.xreport.dl;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author unascribed
 * @version 1.0
 */

public class DimImpl implements Dim
{

    private String supperDimenID = "";
    private String dimenID = "";
    private String dimenName = "";
    private String dataSourceID = "";

    public DimImpl()
    {
    }

    public void setSuperDimenId(String SuperDimenId)
    {
        this.supperDimenID = SuperDimenId;
    }


    /**
     * ��ȡ��ǰά����һ��ά�Ĵ���
     * @param ��ǰά����һ��ά�Ĵ���
     */
    public String getSuperDimenId()
    {
        return this.supperDimenID;
    }


    public void setDimenId(String DimenId)
    {
        this.dimenID = DimenId;
    }


    /**
     * ��ȡ��ǰά�Ĵ���
     * @param ��ǰά�Ĵ���
     */
    public String getDimenId()
    {
        return this.dimenID;
    }


    /**
     * ��ȡ��ǰά������
     * @param ��ǰά������
     */
    public String getDimenName()
    {
        return this.dimenName;
    }


    public void setDimenName(String DimenName)
    {
        this.dimenName = DimenName;
    }


    /**
     * ��ȡ��ǰά����������Դ�Ĵ���
     * @param ��ǰά����������Դ�Ĵ���
     */
    public String getDataSourceId()
    {
        return this.dataSourceID;
    }


    public void setDataSourceId(String DataSourceId)
    {
        this.dataSourceID = DataSourceId;
    }

    public String toString()
    {
        return this.dimenName;
    }

    public int hashCode()
    {
        return (dataSourceID + dimenID).hashCode();
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
        DimImpl other = (DimImpl) obj;
        return
                dataSourceID.equals(other.getDataSourceId())
                && dimenID.equals(other.getDimenId())
                && dimenName.equals(other.getDimenName())
                && supperDimenID.equals(other.getSuperDimenId());
    }


}
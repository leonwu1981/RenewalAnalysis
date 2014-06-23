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
     * 读取当前维的上一级维的代码
     * @param 当前维的上一级维的代码
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
     * 读取当前维的代码
     * @param 当前维的代码
     */
    public String getDimenId()
    {
        return this.dimenID;
    }


    /**
     * 读取当前维的名称
     * @param 当前维的名称
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
     * 读取当前维的所属数据源的代码
     * @param 当前维的所属数据源的代码
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
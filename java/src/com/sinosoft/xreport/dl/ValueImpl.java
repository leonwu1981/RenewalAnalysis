package com.sinosoft.xreport.dl;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author houzw
 * @version 1.0
 */


import javax.swing.tree.TreeNode;

public class ValueImpl extends TreeNodeImpl implements Value
{

    private String valueID = "";
    private String valueName = "";
    private String dimensionID = "";
    //private Vector v = new Vector();
//  private Vector children = new

//  private String

    public ValueImpl()
    {
    }


    /**
     * 读取值的id号
     * @return 值的id号
     */
    public String getValueId()
    {
        return this.valueID;
    }


    /**
     * 设置值的id号
     * @param 值的id号
     */
    public void setValueId(String ValueId)
    {
        this.valueID = ValueId;
    }


    /**
     * 读取值的名称
     * @return 值的名称
     */
    public String getValueName()
    {
        return this.valueName;
    }


    /**
     * 设置值的名称
     * @param 值的名称
     */
    public void setValueName(String ValueName)
    {
        this.valueName = ValueName;
    }


    /**
     * 读取值所属的维的id号
     * @return 值所属的维的id号
     */
    public String getDimensionId()
    {
        return this.dimensionID;
    }


    /**
     * 设置值所属的维的id号
     * @param 值所属的维的id号
     */
    public void setDimensionId(String DimensionId)
    {
        this.dimensionID = DimensionId;
    }


    /**
     * 添加子结点
     * @param 子结点对象
     * @return true成功 false失败
     */
    public boolean addChildren(TreeNode node) throws Exception
    {
        if (node == null)
        {
            throw new Exception("node null!");
        }
        else
        {
            return children.add(node);
        }
    }

    public int hashCode()
    {
        return (dimensionID + valueID).hashCode();
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
        ValueImpl other = (ValueImpl) obj;
        return
                dimensionID.equals(other.getDimensionId())
                && valueID.equals(other.getValueId())
                && valueName.equals(other.getValueName());
    }

    public String toString()
    {
        return this.valueName;
    }


}
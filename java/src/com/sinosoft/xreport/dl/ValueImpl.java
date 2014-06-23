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
     * ��ȡֵ��id��
     * @return ֵ��id��
     */
    public String getValueId()
    {
        return this.valueID;
    }


    /**
     * ����ֵ��id��
     * @param ֵ��id��
     */
    public void setValueId(String ValueId)
    {
        this.valueID = ValueId;
    }


    /**
     * ��ȡֵ������
     * @return ֵ������
     */
    public String getValueName()
    {
        return this.valueName;
    }


    /**
     * ����ֵ������
     * @param ֵ������
     */
    public void setValueName(String ValueName)
    {
        this.valueName = ValueName;
    }


    /**
     * ��ȡֵ������ά��id��
     * @return ֵ������ά��id��
     */
    public String getDimensionId()
    {
        return this.dimensionID;
    }


    /**
     * ����ֵ������ά��id��
     * @param ֵ������ά��id��
     */
    public void setDimensionId(String DimensionId)
    {
        this.dimensionID = DimensionId;
    }


    /**
     * ����ӽ��
     * @param �ӽ�����
     * @return true�ɹ� falseʧ��
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
package com.sinosoft.xreport.dl;

import javax.swing.tree.TreeNode;


public interface Value
{


    /**
     * ��ȡֵ��id��
     * @return ֵ��id��
     */
    public String getValueId();


    /**
     * ����ֵ��id��
     * @param ֵ��id��
     */
    public void setValueId(String ValueId);


    /**
     * ��ȡֵ������
     * @return ֵ������
     */
    public String getValueName();


    /**
     * ����ֵ������
     * @param ֵ������
     */
    public void setValueName(String ValueName);


    /**
     * ��ȡֵ������ά��id��
     * @return ֵ������ά��id��
     */
    public String getDimensionId();


    /**
     * ����ֵ������ά��id��
     * @param ֵ������ά��id��
     */
    public void setDimensionId(String DimensionId);


    /**
     * ����ӽ��
     * @param �ӽ�����
     * @return true�ɹ� falseʧ��
     */
    public boolean addChildren(TreeNode node) throws Exception;

    public String toString();

    public int hashCode();

    public boolean equals(Object obj);

}

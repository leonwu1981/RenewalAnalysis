package com.sinosoft.xreport.dl;

import javax.swing.tree.TreeNode;


public interface Value
{


    /**
     * 读取值的id号
     * @return 值的id号
     */
    public String getValueId();


    /**
     * 设置值的id号
     * @param 值的id号
     */
    public void setValueId(String ValueId);


    /**
     * 读取值的名称
     * @return 值的名称
     */
    public String getValueName();


    /**
     * 设置值的名称
     * @param 值的名称
     */
    public void setValueName(String ValueName);


    /**
     * 读取值所属的维的id号
     * @return 值所属的维的id号
     */
    public String getDimensionId();


    /**
     * 设置值所属的维的id号
     * @param 值所属的维的id号
     */
    public void setDimensionId(String DimensionId);


    /**
     * 添加子结点
     * @param 子结点对象
     * @return true成功 false失败
     */
    public boolean addChildren(TreeNode node) throws Exception;

    public String toString();

    public int hashCode();

    public boolean equals(Object obj);

}

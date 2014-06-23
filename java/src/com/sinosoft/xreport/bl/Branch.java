/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.xreport.bl;

import java.util.Vector;

import com.sinosoft.xreport.util.XMLPathTool;
import com.sinosoft.xreport.util.XReader;
//import com.sinosoft.xreport.util.XTLogger;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>Title: XReport</p>
 * <p>Description: 单位信息类</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author lixy
 * @version 1.0
 * 功能描述：记录单位的基本信息
 */

public class Branch
{
    /**报表代码*/
    private String BranchId;
    /**报表名称*/
    private String BranchName;
    /**上级报表代码*/
    private String SuperBranchId;
    /**备注*/
    private String Remark;

    //程序日志
    private static Logger log = Logger.getLogger(Branch.class.getName());

    /**
     * 构造函数
     */
    public Branch()
    {
        BranchId = "";
        BranchName = "";
        SuperBranchId = "";
        Remark = "";

        PropertyConfigurator.configure("d:\\xreport_lib\\my.property");
        //定义日志信息级别
        log.setLevel(Level.ALL);
    }

    ///////////////////////////JavaBean Method///////////////////////////////

    public String getBranchId()
    {
        return BranchId;
    }

    public void setBranchId(String BranchId)
    {
        this.BranchId = BranchId;
    }

    public void setBranchName(String BranchName)
    {
        this.BranchName = BranchName;
    }

    public String getBranchName()
    {
        return BranchName;
    }

    public String getRemark()
    {
        return Remark;
    }

    public void setRemark(String Remark)
    {
        this.Remark = Remark;
    }

    public void setSuperBranchId(String SuperBranchId)
    {
        this.SuperBranchId = SuperBranchId;
    }

    public String getSuperBranchId()
    {
        return SuperBranchId;
    }

    ///////////////////////////End JavaBean////////////////////////////////////

    ///////////////////////////User Method////////////////////////////////////

    /**
     * 返回单位名称
     * @return 单位名称
     */
    public String toString()
    {
        return BranchName;
    }

    /**
     * 判断otherObject是否和当前对象相等
     * @param otherObject 与当前对象比较的对象
     * @return true:相等，false:不等
     */
    public boolean equals(Object otherObject)
    {
        if (this == otherObject)
        {
            return true;
        }
        if (otherObject == null)
        {
            return false;
        }
        if (getClass() != otherObject.getClass())
        {
            return false;
        }
        Branch other = (Branch) otherObject;
        //判断数据项是否相等
        return
                BranchId.equals(other.getBranchId())
                && BranchName.equals(other.getBranchName())
                && SuperBranchId.equals(other.getSuperBranchId())
                && Remark.equals(other.getRemark());
    }

    /**
     * 返回对象的Hash值
     * @return 对象的Hash值
     */
    public int hashcode()
    {
        int intReturn = 0;
        /**当前对象的Hash值取BranchId字段的Hash值*/
        intReturn = BranchId.hashCode();
        return intReturn;
    }

    /**
     * 复制当前对象
     * @return 复制结果
     */
    public Branch copy()
    {
        Branch branch = new Branch();
        /**复制当前对象的数据项*/
        branch.setBranchId(this.getBranchId());
        branch.setBranchName(this.getBranchName());
        branch.setSuperBranchId(this.getSuperBranchId());
        branch.setRemark(this.getRemark());
        /**静态数据项无需复制 PK[]、FIELDNUM*/
        return branch;
    }

    /**
     * 读取所有的单位信息
     * @return 单位信息向量
     */
    public Vector query()
    {
        /**返回值*/
        Vector vecBranch = new Vector();
        try
        {
            Document doc = XMLPathTool.parseText(XReader.readConf("Branch.xml"));
            Node root = doc.getDocumentElement();
            /**branch属性*/
            String strBranchId = "";
            String strBranchName = "";
            String strSuperBranchId = "";
            String strRemark;
            /**读取Branch结点集合*/
            NodeList list = XPathAPI.selectNodeList(root, "/Branchs/Branch");
            for (int i = 0; i < list.getLength(); i++)
            {
                /**处理单个结点*/
                Node node = list.item(i);
                Branch branch = new Branch();
                /**处理属性*/
                strBranchId = XMLPathTool.getValue(node, "BranchId");
                branch.setBranchId(strBranchId);
                strBranchName = XMLPathTool.getValue(node, "BranchName");
                branch.setBranchName(strBranchName);
                strSuperBranchId = XMLPathTool.getValue(node, "SuperBranchId");
                branch.setSuperBranchId(strSuperBranchId);
                strRemark = XMLPathTool.getValue(node, "Remark");
                branch.setRemark(strRemark);

                /**添加branch对象*/
                vecBranch.addElement(branch);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return vecBranch;
    }

    /**
     * 查询单位信息
     * @param qryBranch 查询条件
     * @return 符合条件的单位信息
     */
    public Vector query(Branch qryBranch)
    {
        Vector vecBranch = query();
        Vector vecReturn = new Vector();
        /**如果没有查询条件则返回所有的单位信息*/
        if (qryBranch == null)
        {
            return vecBranch;
        }
        /**从所有的单位信息中筛选符合条件的单位*/
        for (int i = 0; i < vecBranch.size(); i++)
        {
            Branch branch = (Branch) vecBranch.elementAt(i);
            if ((qryBranch.getBranchId().equals("") ||
                 qryBranch.getBranchId().equals(branch.getBranchId())) &&
                (qryBranch.getBranchName().equals("") ||
                 qryBranch.getBranchName().equals(branch.getBranchName())) &&
                (qryBranch.getSuperBranchId().equals("") ||
                 qryBranch.getSuperBranchId().equals(branch.getSuperBranchId())) &&
                (qryBranch.getRemark().equals("") ||
                 qryBranch.getRemark().equals(branch.getRemark())))
            {
                vecReturn.addElement(branch);
            }
        }
        return vecReturn;
    }

    public void toServer()
    {
        XMLPathTool xpath = new XMLPathTool(XReader.readConf("Branch.xml"));
        Node node = xpath.getNode("/Branchs");
        try
        {
            XMLPathTool.toServer(node, "1.xml");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
//        Branch branch = new Branch();
//        branch.setBranchId("330700");
//        Vector vec = branch.query();
//        for (int i = 0; i < vec.size(); i++)
//        {
//            XTLogger.getLogger("Branch").info(vec.elementAt(i));
//        }
    }
}

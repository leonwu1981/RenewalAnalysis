/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.xreport.bl;

import java.util.Vector;

import com.sinosoft.xreport.util.XMLPathTool;
import com.sinosoft.xreport.util.XReader;
import com.sinosoft.xreport.util.XWriter;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * <p>Title: XReport</p>
 * <p>Description: 报表信息类</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author lixy
 * @version 1.0
 * 功能描述：记录报表信息
 */

public class ReportMain
{
    /** 报表代码 */
    private String ReportId = "";
    /** 报表版别 */
    private String ReportEdition = "";
    /** 单位代码 */
    private String BranchId = "";
    /** 报表名称 */
    private String ReportName = "";
    /** 报表类型 */
    private String ReportType = "";
    /** 报表周期 */
    private String ReportCycle = "";
    /** 报表形式 */
    private String ReportAtt = "";
    /** 币别 */
    private String Currency = "";
    /** 备注 */
    private String Remark = "";

    public ReportMain()
    {
    }

    ///////////////////////////JavaBean Method///////////////////////////

    public String getReportType()
    {
        return ReportType;
    }

    public void setReportType(String ReportType)
    {
        this.ReportType = ReportType;
    }

    public String getReportName()
    {
        return ReportName;
    }

    public void setReportName(String ReportName)
    {
        this.ReportName = ReportName;
    }

    public String getReportId()
    {
        return ReportId;
    }

    public void setReportId(String ReportId)
    {
        this.ReportId = ReportId;
    }

    public String getReportEdition()
    {
        return ReportEdition;
    }

    public void setReportEdition(String ReportEdition)
    {
        this.ReportEdition = ReportEdition;
    }

    public String getReportCycle()
    {
        return ReportCycle;
    }

    public void setReportCycle(String ReportCycle)
    {
        this.ReportCycle = ReportCycle;
    }

    public String getReportAtt()
    {
        return ReportAtt;
    }

    public void setReportAtt(String ReportAtt)
    {
        this.ReportAtt = ReportAtt;
    }

    public String getRemark()
    {
        return Remark;
    }

    public void setRemark(String Remark)
    {
        this.Remark = Remark;
    }

    public String getCurrency()
    {
        return Currency;
    }

    public void setCurrency(String Currency)
    {
        this.Currency = Currency;
    }

    public String getBranchId()
    {
        return BranchId;
    }

    public void setBranchId(String BranchId)
    {
        this.BranchId = BranchId;
    }

    ///////////////////////////End JavaBean////////////////////////////////////

    ///////////////////////////User Method////////////////////////////////////

    /**
     * 返回单位名称
     * @return 单位名称
     */
    public String toString()
    {
        return ReportName;
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
        ReportMain other = (ReportMain) otherObject;
        //判断数据项是否相等
        return
                BranchId.equals(other.getBranchId())
                && ReportId.equals(other.getReportId())
                && ReportEdition.equals(other.getReportEdition())
                && ReportName.equals(other.getReportName())
                && ReportType.equals(other.getReportType())
                && ReportAtt.equals(other.getReportAtt())
                && ReportCycle.equals(other.getReportCycle())
                && Currency.equals(other.getCurrency())

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
        intReturn = (BranchId + ReportId + ReportEdition).hashCode();
        return intReturn;
    }

    /**
     * 复制当前对象
     * @return 复制结果
     */
    public ReportMain copy()
    {
        ReportMain report = new ReportMain();
        /**复制当前对象的数据项*/
        report.setBranchId(this.getBranchId());
        report.setReportId(this.getReportId());
        report.setReportEdition(this.getReportEdition());
        report.setReportName(this.getReportName());
        report.setReportAtt(this.getReportAtt());
        report.setReportType(this.getReportType());
        report.setReportCycle(this.getReportCycle());
        report.setRemark(this.getRemark());
        /**静态数据项无需复制 PK[]、FIELDNUM*/
        return report;
    }

    /**
     * 查询报表信息
     * @return 报表信息集合
     */
    public Vector query()
    {
        /**返回值*/
        Vector vecReportMain = new Vector();
        /**ReportMain属性*/
        String strBranchId = "";
        String strReportId = "";
        String strReportEdition = "";
        String strReportName = "";
        String strReportType = "";
        String strReportCycle = "";
        String strReportAtt = "";
        String strCurrency = "";
        String strRemark = "";
        try
        {
            Document doc = XMLPathTool.parseText(XReader.readConf(
                    "ReportMain.xml"));
            Node root = doc.getDocumentElement();
            /**读取ReportMain结点集合*/
            NodeList list = XPathAPI.selectNodeList(root,
                    "/ReportMains/ReportMain");
            for (int i = 0; i < list.getLength(); i++)
            {
                /**处理单个结点*/
                Node node = list.item(i);
                ReportMain report = new ReportMain();
                /**处理属性*/
                strBranchId = XMLPathTool.getValue(node, "BranchId");
                report.setBranchId(strBranchId);
                strReportId = XMLPathTool.getValue(node, "ReportId");
                report.setReportId(strReportId);
                strReportEdition = XMLPathTool.getValue(node, "ReportEdition");
                report.setReportEdition(strReportEdition);
                strReportName = XMLPathTool.getValue(node, "ReportName");
                report.setReportName(strReportName);
                strReportType = XMLPathTool.getValue(node, "ReportType");
                report.setReportType(strReportType);
                strReportCycle = XMLPathTool.getValue(node, "ReportCycle");
                report.setReportCycle(strReportCycle);
                strReportAtt = XMLPathTool.getValue(node, "ReportAtt");
                report.setReportAtt(strReportAtt);
                strCurrency = XMLPathTool.getValue(node, "Currency");
                report.setCurrency(strCurrency);
                strRemark = XMLPathTool.getValue(node, "Remark");
                report.setRemark(strRemark);
                /**添加ReportMain对象*/
                vecReportMain.addElement(report);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return vecReportMain;
    }

    /**
     * 查询报表信息
     * @param qryReportMain 查询条件
     * @return 报表信息集合
     */
    public Vector query(ReportMain qryReportMain)
    {
        Vector vecReportMain = query();
        Vector vecReturn = new Vector();
        /**如果没有查询条件则返回所有的报表信息*/
        if (qryReportMain == null)
        {
            return vecReportMain;
        }
        /**从所有的单位信息中筛选符合条件的单位*/
        for (int i = 0; i < vecReportMain.size(); i++)
        {
            ReportMain report = (ReportMain) vecReportMain.elementAt(i);
            if ((qryReportMain.getBranchId().equals("") ||
                 qryReportMain.getBranchId().equals(report.getBranchId())) &&
                (qryReportMain.getReportId().equals("") ||
                 qryReportMain.getReportId().equals(report.getReportId())) &&
                (qryReportMain.getReportEdition().equals("") ||
                 qryReportMain.getReportEdition().equals(report.
                    getReportEdition())) &&
                (qryReportMain.getReportName().equals("") ||
                 qryReportMain.getReportName().equals(report.getReportName())) &&
                (qryReportMain.getReportAtt().equals("") ||
                 qryReportMain.getReportAtt().equals(report.getReportAtt())) &&
                (qryReportMain.getReportCycle().equals("") ||
                 qryReportMain.getReportCycle().equals(report.getReportCycle())) &&
                (qryReportMain.getReportType().equals("") ||
                 qryReportMain.getReportType().equals(report.getReportType())) &&
                (qryReportMain.getCurrency().equals("") ||
                 qryReportMain.getCurrency().equals(report.getCurrency())) &&
                (qryReportMain.getRemark().equals("") ||
                 qryReportMain.getRemark().equals(report.getRemark())))
            {
                vecReturn.addElement(report);
            }
        }
        return vecReturn;
    }

    /**
     * 从XML文件中删除当前报表对象
     */
    public void delete()
    {
        /**查询报表信息*/
        Vector vecReport = query(this);
        /**xpath工具*/
        XMLPathTool xpath = new XMLPathTool(XReader.readConf("ReportMain.xml"));
        /**获得结点集合*/
        NodeList list = xpath.parseN("/ReportMains/ReportMain");
        /**查找符合条件的结点*/
        try
        {
            for (int i = 0; i < vecReport.size(); i++)
            {
                for (int j = 0; j < list.getLength(); j++)
                {
                    ReportMain report = (ReportMain) vecReport.elementAt(i);
                    Node node = list.item(j);
                    if ((report.getBranchId().equals("") ||
                         report.getBranchId().equals(XMLPathTool.getValue(node,
                            "BranchId"))) &&
                        (report.getReportId().equals("") ||
                         report.getReportId().equals(XMLPathTool.getValue(node,
                            "ReportId"))) &&
                        (report.getReportEdition().equals("") ||
                         report.getReportEdition().equals(XMLPathTool.getValue(node,
                            "ReportEdition")))
                            )
                    {
                        /**删除符合条件的结点*/
                        XMLPathTool.deleteNode(node);
                    }
                }
            }
            /**保存操作结果*/
            Node root = xpath.getNode("/ReportMains");
            XWriter.writeConf(XMLPathTool.toString(root), "ReportMain.xml");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void insert()
    {
        /**在XML文件中清除当前报表的信息*/
        delete();
        /**xpath工具*/
        XMLPathTool xpath = new XMLPathTool(XReader.readConf("ReportMain.xml"));
        try
        {
            /**添加一个报表结点(暂时没有任何属性)*/
            Node root = xpath.getNode("/ReportMains");
            Node node = XMLPathTool.appendNode(root, "ReportMain");
            /**添加报表属性*/
            Node newNode = XMLPathTool.appendNode(node, "BranchId");
            XMLPathTool.setValue(newNode, BranchId);
            newNode = XMLPathTool.appendNode(node, "ReportId");
            XMLPathTool.setValue(newNode, ReportId);
            newNode = XMLPathTool.appendNode(node, "ReportEdition");
            XMLPathTool.setValue(newNode, ReportEdition);
            newNode = XMLPathTool.appendNode(node, "ReportName");
            XMLPathTool.setValue(newNode, ReportName);
            newNode = XMLPathTool.appendNode(node, "ReportType");
            XMLPathTool.setValue(newNode, ReportType);
            newNode = XMLPathTool.appendNode(node, "ReportCycle");
            Node node2 = XMLPathTool.setValue(newNode, ReportCycle);
            newNode = XMLPathTool.appendNode(node, "ReportAtt");
            XMLPathTool.setValue(newNode, ReportAtt);
            newNode = XMLPathTool.appendNode(node, "Currency");
            XMLPathTool.setValue(newNode, Currency);
            newNode = XMLPathTool.appendNode(node, "Remark");
            XMLPathTool.setValue(newNode, Remark);
            /**保存操作结果*/
            XWriter.writeConf(XMLPathTool.toString(root), "ReportMain.xml");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
//        ReportMain report = new ReportMain();
//        report.setBranchId("330700");
//        report.setReportId("js01");
//        report.setReportEdition("200201");
//        report.delete();
    }
}

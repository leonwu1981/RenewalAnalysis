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
 * <p>Description: ������Ϣ��</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author lixy
 * @version 1.0
 * ������������¼������Ϣ
 */

public class ReportMain
{
    /** ������� */
    private String ReportId = "";
    /** ������ */
    private String ReportEdition = "";
    /** ��λ���� */
    private String BranchId = "";
    /** �������� */
    private String ReportName = "";
    /** �������� */
    private String ReportType = "";
    /** �������� */
    private String ReportCycle = "";
    /** ������ʽ */
    private String ReportAtt = "";
    /** �ұ� */
    private String Currency = "";
    /** ��ע */
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
     * ���ص�λ����
     * @return ��λ����
     */
    public String toString()
    {
        return ReportName;
    }

    /**
     * �ж�otherObject�Ƿ�͵�ǰ�������
     * @param otherObject �뵱ǰ����ȽϵĶ���
     * @return true:��ȣ�false:����
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
        //�ж��������Ƿ����
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
     * ���ض����Hashֵ
     * @return �����Hashֵ
     */
    public int hashcode()
    {
        int intReturn = 0;
        /**��ǰ�����HashֵȡBranchId�ֶε�Hashֵ*/
        intReturn = (BranchId + ReportId + ReportEdition).hashCode();
        return intReturn;
    }

    /**
     * ���Ƶ�ǰ����
     * @return ���ƽ��
     */
    public ReportMain copy()
    {
        ReportMain report = new ReportMain();
        /**���Ƶ�ǰ�����������*/
        report.setBranchId(this.getBranchId());
        report.setReportId(this.getReportId());
        report.setReportEdition(this.getReportEdition());
        report.setReportName(this.getReportName());
        report.setReportAtt(this.getReportAtt());
        report.setReportType(this.getReportType());
        report.setReportCycle(this.getReportCycle());
        report.setRemark(this.getRemark());
        /**��̬���������踴�� PK[]��FIELDNUM*/
        return report;
    }

    /**
     * ��ѯ������Ϣ
     * @return ������Ϣ����
     */
    public Vector query()
    {
        /**����ֵ*/
        Vector vecReportMain = new Vector();
        /**ReportMain����*/
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
            /**��ȡReportMain��㼯��*/
            NodeList list = XPathAPI.selectNodeList(root,
                    "/ReportMains/ReportMain");
            for (int i = 0; i < list.getLength(); i++)
            {
                /**���������*/
                Node node = list.item(i);
                ReportMain report = new ReportMain();
                /**��������*/
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
                /**���ReportMain����*/
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
     * ��ѯ������Ϣ
     * @param qryReportMain ��ѯ����
     * @return ������Ϣ����
     */
    public Vector query(ReportMain qryReportMain)
    {
        Vector vecReportMain = query();
        Vector vecReturn = new Vector();
        /**���û�в�ѯ�����򷵻����еı�����Ϣ*/
        if (qryReportMain == null)
        {
            return vecReportMain;
        }
        /**�����еĵ�λ��Ϣ��ɸѡ���������ĵ�λ*/
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
     * ��XML�ļ���ɾ����ǰ�������
     */
    public void delete()
    {
        /**��ѯ������Ϣ*/
        Vector vecReport = query(this);
        /**xpath����*/
        XMLPathTool xpath = new XMLPathTool(XReader.readConf("ReportMain.xml"));
        /**��ý�㼯��*/
        NodeList list = xpath.parseN("/ReportMains/ReportMain");
        /**���ҷ��������Ľ��*/
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
                        /**ɾ�����������Ľ��*/
                        XMLPathTool.deleteNode(node);
                    }
                }
            }
            /**����������*/
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
        /**��XML�ļ��������ǰ�������Ϣ*/
        delete();
        /**xpath����*/
        XMLPathTool xpath = new XMLPathTool(XReader.readConf("ReportMain.xml"));
        try
        {
            /**���һ��������(��ʱû���κ�����)*/
            Node root = xpath.getNode("/ReportMains");
            Node node = XMLPathTool.appendNode(root, "ReportMain");
            /**��ӱ�������*/
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
            /**����������*/
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

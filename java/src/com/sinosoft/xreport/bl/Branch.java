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
 * <p>Description: ��λ��Ϣ��</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author lixy
 * @version 1.0
 * ������������¼��λ�Ļ�����Ϣ
 */

public class Branch
{
    /**�������*/
    private String BranchId;
    /**��������*/
    private String BranchName;
    /**�ϼ��������*/
    private String SuperBranchId;
    /**��ע*/
    private String Remark;

    //������־
    private static Logger log = Logger.getLogger(Branch.class.getName());

    /**
     * ���캯��
     */
    public Branch()
    {
        BranchId = "";
        BranchName = "";
        SuperBranchId = "";
        Remark = "";

        PropertyConfigurator.configure("d:\\xreport_lib\\my.property");
        //������־��Ϣ����
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
     * ���ص�λ����
     * @return ��λ����
     */
    public String toString()
    {
        return BranchName;
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
        Branch other = (Branch) otherObject;
        //�ж��������Ƿ����
        return
                BranchId.equals(other.getBranchId())
                && BranchName.equals(other.getBranchName())
                && SuperBranchId.equals(other.getSuperBranchId())
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
        intReturn = BranchId.hashCode();
        return intReturn;
    }

    /**
     * ���Ƶ�ǰ����
     * @return ���ƽ��
     */
    public Branch copy()
    {
        Branch branch = new Branch();
        /**���Ƶ�ǰ�����������*/
        branch.setBranchId(this.getBranchId());
        branch.setBranchName(this.getBranchName());
        branch.setSuperBranchId(this.getSuperBranchId());
        branch.setRemark(this.getRemark());
        /**��̬���������踴�� PK[]��FIELDNUM*/
        return branch;
    }

    /**
     * ��ȡ���еĵ�λ��Ϣ
     * @return ��λ��Ϣ����
     */
    public Vector query()
    {
        /**����ֵ*/
        Vector vecBranch = new Vector();
        try
        {
            Document doc = XMLPathTool.parseText(XReader.readConf("Branch.xml"));
            Node root = doc.getDocumentElement();
            /**branch����*/
            String strBranchId = "";
            String strBranchName = "";
            String strSuperBranchId = "";
            String strRemark;
            /**��ȡBranch��㼯��*/
            NodeList list = XPathAPI.selectNodeList(root, "/Branchs/Branch");
            for (int i = 0; i < list.getLength(); i++)
            {
                /**���������*/
                Node node = list.item(i);
                Branch branch = new Branch();
                /**��������*/
                strBranchId = XMLPathTool.getValue(node, "BranchId");
                branch.setBranchId(strBranchId);
                strBranchName = XMLPathTool.getValue(node, "BranchName");
                branch.setBranchName(strBranchName);
                strSuperBranchId = XMLPathTool.getValue(node, "SuperBranchId");
                branch.setSuperBranchId(strSuperBranchId);
                strRemark = XMLPathTool.getValue(node, "Remark");
                branch.setRemark(strRemark);

                /**���branch����*/
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
     * ��ѯ��λ��Ϣ
     * @param qryBranch ��ѯ����
     * @return ���������ĵ�λ��Ϣ
     */
    public Vector query(Branch qryBranch)
    {
        Vector vecBranch = query();
        Vector vecReturn = new Vector();
        /**���û�в�ѯ�����򷵻����еĵ�λ��Ϣ*/
        if (qryBranch == null)
        {
            return vecBranch;
        }
        /**�����еĵ�λ��Ϣ��ɸѡ���������ĵ�λ*/
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

package com.sinosoft.xreport.pl;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import com.sinosoft.xreport.bl.ReportMain;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author lixy
 * @version 1.0
 */

public class FrameNewReport extends FrameEditReport
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public FrameNewReport()
    {
        super();
    }

    public FrameNewReport(FrameManageReport frmParent)
    {
        super(frmParent);
        this.report = frmParent.getReport();
    }

    /**
     * ��ʾ����
     * @param report �������
     */
    public void open(ReportMain report)
    {
        initData();
        initInterface();
        this.show();
    }

    /**
     * ��ʼ������
     */
    private void initInterface()
    {
        initOne();
        initTwo();
        initThree();
    }

    /**
     * ��ʼ����һҳ
     */
    private void initOne()
    {
        /**����������Ͽ�*/
        jComboReportType.removeAllItems();
        jComboReportType.addItem("�ձ�");
        jComboReportType.addItem("�±�");
        jComboReportType.addItem("����");
        jComboReportType.addItem("�걨");
        jComboReportType.addItem("����ʱ��α���");
        jComboReportType.setSelectedIndex(0);
        /**����������Ͽ�*/
        jComboReportCycle.removeAllItems();
        jComboReportCycle.addItem("1");
        jComboReportCycle.addItem("2");
        jComboReportCycle.addItem("3");
        jComboReportCycle.addItem("4");
        jComboReportCycle.addItem("5");
        jComboReportCycle.addItem("6");
        jComboReportCycle.setSelectedIndex(0);
        /**������Ͽ�*/
        jComboCurrency.removeAllItems();
        jComboCurrency.addItem("�����");
        jComboCurrency.setSelectedIndex(0);
        /**������ʽ��Ͽ�*/
        jComboAtt.removeAllItems();
        jComboAtt.addItem("�̶���ʽ");
        jComboAtt.addItem("�嵥��ʽ");
        jComboAtt.setSelectedIndex(0);
        /**��������ı���*/
        jTextReportCode.setText("");
        /**���������ı���*/
        jTextReportName.setText("");
        /**�������ı���*/
        jTextReportEdition.setText("");
        /**����������λ��Ͽ�*/
        jComboBranch.removeAllItems();
        for (int i = 0; i < code.getBranch().size(); i++)
        {
            jComboBranch.addItem(code.getBranch().elementAt(i));
        }
        if (jComboBranch.getItemCount() != 0)
        {
            jComboBranch.setSelectedIndex(0);
        }
    }

    /**
     * ��ʼ���ڶ�ҳ
     */
    private void initTwo()
    {

    }

    /**
     * ��ʼ������ҳ
     */
    private void initThree()
    {
        dataSource = code.getDataSources();
        jListResource.setListData(dataSource);
        dataDest.removeAllElements();
        jListResResult.setListData(dataDest);
    }

    /**
     * ��ʼ������
     */
    private void initData()
    {
        code.getBranchMap();
        code.getReportMain();
        code.getTableFields();
    }

    /**
     * �ύ�Ի������
     */
    protected void commit()
    {
        commitReport();
        commitParent();
        this.dispose();
    }

    /**
     * �ύ�Ա�����޸�
     */
    protected void commitReport()
    {
        /**�½�����*/
        report.newReport(tReportMain, dataDest);
        /**���´����*/
        code.getReportMainMap();
    }

    /**
     * �ύ�Ը����ڽ�����޸�
     */
    protected void commitParent()
    {
        /**���½��ı�����ӵ���������ڵ�jTable�ؼ���*/
        Vector vecReport = new Vector();
        vecReport.addElement(code.getBranch(tReportMain.getBranchId()));
        vecReport.addElement(tReportMain.getReportId());
        vecReport.addElement(tReportMain.getReportName());
        vecReport.addElement(tReportMain.getReportEdition());
        vecReport.addElement(tReportMain.getReportType());
        vecReport.addElement(tReportMain.getReportCycle());
        vecReport.addElement(tReportMain.getReportAtt());
        DefaultTableModel model = frmParent.getModel();
        model.addRow(vecReport);
        frmParent.jTableReport.setModel(model);
    }

    public static void main(String[] args)
    {
        FrameNewReport newReport = new FrameNewReport();
        newReport.setSize(570, 360);
        newReport.open(new ReportMain());
    }
}
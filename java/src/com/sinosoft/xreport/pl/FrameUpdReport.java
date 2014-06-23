package com.sinosoft.xreport.pl;

import com.sinosoft.xreport.bl.ReportMain;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author lixy
 * @version 1.0
 */

public class FrameUpdReport extends FrameEditReport
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public FrameUpdReport()
    {
        super();
    }

    public FrameUpdReport(FrameManageReport frmParent)
    {
        super(frmParent);
        report = frmParent.getReport();
    }

    /**
     * ��ʾ����
     * @param tReportMain ������Ϣ����
     */
    public void open(ReportMain tReportMain)
    {
        initData(tReportMain);
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
        jComboReportType.setSelectedItem(report.
                                         getReport().getReportType());
        /**����������Ͽ�*/
        jComboReportCycle.removeAllItems();
        jComboReportCycle.addItem("1");
        jComboReportCycle.addItem("2");
        jComboReportCycle.addItem("3");
        jComboReportCycle.addItem("4");
        jComboReportCycle.addItem("5");
        jComboReportCycle.addItem("6");
        jComboReportCycle.setSelectedItem(report.
                                          getReport().getReportCycle());
        /**������Ͽ�*/
        jComboCurrency.removeAllItems();
        jComboCurrency.addItem("�����");
        jComboCurrency.setSelectedItem(report.
                                       getReport().getCurrency());
        /**������ʽ��Ͽ�*/
        jComboAtt.removeAllItems();
        jComboAtt.addItem("�̶���ʽ");
        jComboAtt.addItem("�嵥��ʽ");
        jComboAtt.setSelectedItem(report.
                                  getReport().getReportAtt());
        /**��������ı���*/
        jTextReportCode.setText(report.getReport().getReportId());
        /**���������ı���*/
        jTextReportName.setText(report.getReport().getReportName());
        /**�������ı���*/
        jTextReportEdition.setText(report.getReport().getReportEdition());
        /**����������λ��Ͽ�*/
        jComboBranch.removeAllItems();
        for (int i = 0; i < code.getBranch().size(); i++)
        {
            jComboBranch.addItem(code.getBranch().elementAt(i));
        }
        jComboBranch.setSelectedItem(code.getBranch(
                report.getReport().getBranchId()));
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
        for (int i = 0; i < report.getSource().size(); i++)
        {
            dataDest.addElement(report.getSource().elementAt(i));
        }
        jListResResult.setListData(dataDest);
    }

    /**
     * ��ʼ������
     * @param tReportMain ������Ϣ����
     */
    private void initData(ReportMain tReportMain)
    {
        report.readDefine(tReportMain);
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
     * �ύ�Ա����ʽ���޸�
     */
    protected void commitReport()
    {
        /**���汨����Ϣ*/
        report.updReport(tReportMain, dataDest);
        /**���´����*/
        code.getReportMainMap();
    }

    /**
     * �ύ�Ը����ڽ�����޸�
     */
    protected void commitParent()
    {
        /**�޸ı���������б��޸ı������Ϣ*/
        int intRowNum = frmParent.jTableReport.getSelectedRow();
        frmParent.jTableReport.setValueAt(code.getBranch(
                tReportMain.getBranchId()), intRowNum, 0);
        frmParent.jTableReport.setValueAt(
                tReportMain.getReportId(), intRowNum, 1);
        frmParent.jTableReport.setValueAt(
                tReportMain.getReportName(), intRowNum, 2);
        frmParent.jTableReport.setValueAt(
                tReportMain.getReportEdition(), intRowNum, 3);
        frmParent.jTableReport.setValueAt(
                tReportMain.getReportType(), intRowNum, 4);
        frmParent.jTableReport.setValueAt(
                tReportMain.getReportCycle(), intRowNum, 5);
        frmParent.jTableReport.setValueAt(
                tReportMain.getReportAtt(), intRowNum, 6);
    }

    public static void main(String[] args)
    {
        FrameUpdReport updReport = new FrameUpdReport();
        updReport.setSize(570, 360);
        updReport.open(new ReportMain());
    }
}
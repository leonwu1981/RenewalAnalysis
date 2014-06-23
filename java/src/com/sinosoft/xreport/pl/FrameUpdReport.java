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
     * 显示窗口
     * @param tReportMain 报表信息对象
     */
    public void open(ReportMain tReportMain)
    {
        initData(tReportMain);
        initInterface();
        this.show();
    }

    /**
     * 初始化界面
     */
    private void initInterface()
    {
        initOne();
        initTwo();
        initThree();
    }

    /**
     * 初始化第一页
     */
    private void initOne()
    {
        /**报表类型组合框*/
        jComboReportType.removeAllItems();
        jComboReportType.addItem("日报");
        jComboReportType.addItem("月报");
        jComboReportType.addItem("季报");
        jComboReportType.addItem("年报");
        jComboReportType.addItem("任意时间段报表");
        jComboReportType.setSelectedItem(report.
                                         getReport().getReportType());
        /**报表周期组合框*/
        jComboReportCycle.removeAllItems();
        jComboReportCycle.addItem("1");
        jComboReportCycle.addItem("2");
        jComboReportCycle.addItem("3");
        jComboReportCycle.addItem("4");
        jComboReportCycle.addItem("5");
        jComboReportCycle.addItem("6");
        jComboReportCycle.setSelectedItem(report.
                                          getReport().getReportCycle());
        /**币种组合框*/
        jComboCurrency.removeAllItems();
        jComboCurrency.addItem("人民币");
        jComboCurrency.setSelectedItem(report.
                                       getReport().getCurrency());
        /**报表形式组合框*/
        jComboAtt.removeAllItems();
        jComboAtt.addItem("固定格式");
        jComboAtt.addItem("清单格式");
        jComboAtt.setSelectedItem(report.
                                  getReport().getReportAtt());
        /**报表代码文本框*/
        jTextReportCode.setText(report.getReport().getReportId());
        /**报表名称文本框*/
        jTextReportName.setText(report.getReport().getReportName());
        /**报表版别文本框*/
        jTextReportEdition.setText(report.getReport().getReportEdition());
        /**报表所属单位组合框*/
        jComboBranch.removeAllItems();
        for (int i = 0; i < code.getBranch().size(); i++)
        {
            jComboBranch.addItem(code.getBranch().elementAt(i));
        }
        jComboBranch.setSelectedItem(code.getBranch(
                report.getReport().getBranchId()));
    }

    /**
     * 初始化第二页
     */
    private void initTwo()
    {

    }

    /**
     * 初始化第三页
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
     * 初始化数据
     * @param tReportMain 报表信息对象
     */
    private void initData(ReportMain tReportMain)
    {
        report.readDefine(tReportMain);
    }

    /**
     * 提交对话框操作
     */
    protected void commit()
    {
        commitReport();
        commitParent();
        this.dispose();
    }


    /**
     * 提交对报表格式的修改
     */
    protected void commitReport()
    {
        /**保存报表信息*/
        report.updReport(tReportMain, dataDest);
        /**更新代码表*/
        code.getReportMainMap();
    }

    /**
     * 提交对父窗口界面的修改
     */
    protected void commitParent()
    {
        /**修改报表管理窗口中被修改报表的信息*/
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
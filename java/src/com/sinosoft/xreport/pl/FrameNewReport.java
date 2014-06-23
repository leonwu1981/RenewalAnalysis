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
     * 显示窗口
     * @param report 报表对象
     */
    public void open(ReportMain report)
    {
        initData();
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
        jComboReportType.setSelectedIndex(0);
        /**报表周期组合框*/
        jComboReportCycle.removeAllItems();
        jComboReportCycle.addItem("1");
        jComboReportCycle.addItem("2");
        jComboReportCycle.addItem("3");
        jComboReportCycle.addItem("4");
        jComboReportCycle.addItem("5");
        jComboReportCycle.addItem("6");
        jComboReportCycle.setSelectedIndex(0);
        /**币种组合框*/
        jComboCurrency.removeAllItems();
        jComboCurrency.addItem("人民币");
        jComboCurrency.setSelectedIndex(0);
        /**报表形式组合框*/
        jComboAtt.removeAllItems();
        jComboAtt.addItem("固定格式");
        jComboAtt.addItem("清单格式");
        jComboAtt.setSelectedIndex(0);
        /**报表代码文本框*/
        jTextReportCode.setText("");
        /**报表名称文本框*/
        jTextReportName.setText("");
        /**报表版别文本框*/
        jTextReportEdition.setText("");
        /**报表所属单位组合框*/
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
        jListResResult.setListData(dataDest);
    }

    /**
     * 初始化数据
     */
    private void initData()
    {
        code.getBranchMap();
        code.getReportMain();
        code.getTableFields();
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
     * 提交对报表的修改
     */
    protected void commitReport()
    {
        /**新建报表*/
        report.newReport(tReportMain, dataDest);
        /**更新代码表*/
        code.getReportMainMap();
    }

    /**
     * 提交对父窗口界面的修改
     */
    protected void commitParent()
    {
        /**将新建的报表添加到报表管理窗口的jTable控件中*/
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
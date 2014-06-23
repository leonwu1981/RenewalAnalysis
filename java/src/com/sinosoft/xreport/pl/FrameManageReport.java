package com.sinosoft.xreport.pl;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.sinosoft.xreport.bl.Branch;
import com.sinosoft.xreport.bl.Code;
import com.sinosoft.xreport.bl.ReportMain;
import com.sinosoft.xreport.util.SysConfig;
import com.sinosoft.xreport.util.XWriter;

/**
 * <p>Title: XReport 1.0 (c)Sinosoft 2003</p>
 * <p>Description: 报表管理主界面</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft</p>
 * @author lixy
 * @version 1.0
 */

public class FrameManageReport extends JDialog
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**父窗口句柄*/
    private XReportMain frmParent;
    /**报表对象*/
    private Report report;
    /**代码表*/
    private Code code = new Code();
    /**jTable控件模型*/
    private DefaultTableModel model = new DefaultTableModel();

    /**自动生成*/
    JScrollPane jScrollPaneReport = new JScrollPane();
    JTable jTableReport = new JTable();
    JButton jButtonQuery = new JButton();
    JButton jButtonNew = new JButton();
    JButton jButtonOpen = new JButton();
    JButton jButtonUpdate = new JButton();
    JButton jButtonDelete = new JButton();
    JButton jButtonQuit = new JButton();
    private JButton jButtonCal = new JButton();

    /**
     * 构造函数
     */
    public FrameManageReport()
    {
        try
        {
            jbInit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception
    {
        this.getContentPane().setLayout(null);
        jScrollPaneReport.setFont(new java.awt.Font("DialogInput", 0, 12));
        jScrollPaneReport.setBounds(new Rectangle(37, 42, 455, 194));
        jTableReport.setFont(new java.awt.Font("DialogInput", 0, 12));
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setFont(new java.awt.Font("DialogInput", 0, 12));
        this.setModal(true);
        this.setResizable(false);
        jButtonQuery.setBounds(new Rectangle(45, 251, 63, 27));
        jButtonQuery.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonQuery.setText("查询");
        jButtonQuery.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                queryReport();
            }
        });
        jButtonNew.setBounds(new Rectangle(108, 251, 63, 27));
        jButtonNew.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonNew.setText("新建");
        jButtonNew.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                newReport();
            }
        });
        jButtonOpen.setBounds(new Rectangle(171, 251, 63, 27));
        jButtonOpen.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonOpen.setText("打开");
        jButtonOpen.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                openReport();
            }
        });
        jButtonUpdate.setBounds(new Rectangle(234, 251, 63, 27));
        jButtonUpdate.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonUpdate.setText("修改");
        jButtonUpdate.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                updateReport();
            }
        });
        jButtonDelete.setBounds(new Rectangle(297, 251, 63, 27));
        jButtonDelete.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonDelete.setText("删除");
        jButtonDelete.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                deleteReport();
            }
        });
        jButtonQuit.setBounds(new Rectangle(423, 251, 63, 27));
        jButtonQuit.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonQuit.setText("退出");
        jButtonQuit.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                quit();
            }
        });
        jButtonCal.setBounds(new Rectangle(360, 251, 63, 27));
        jButtonCal.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonCal.setText("计算");
        jButtonCal.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                calculate();
            }
        });
        this.getContentPane().add(jScrollPaneReport, null);
        this.getContentPane().add(jButtonQuery, null);
        this.getContentPane().add(jButtonNew, null);
        this.getContentPane().add(jButtonOpen, null);
        this.getContentPane().add(jButtonUpdate, null);
        this.getContentPane().add(jButtonDelete, null);
        this.getContentPane().add(jButtonCal, null);
        this.getContentPane().add(jButtonQuit, null);
        jScrollPaneReport.getViewport().add(jTableReport, null);
    }

    /**
     * 构造函数
     * @param frmParent 父窗口句柄
     */
    public FrameManageReport(XReportMain frmParent)
    {
        super(frmParent);
        report = frmParent.getReport();
        try
        {
            jbInit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Report getReport()
    {
        return report;
    }

    /**
     * 显示窗口
     */
    public void open()
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
        Vector vecReportInfo = new Vector();
        /**设置标题*/
        setTitle("报表管理");
        /**添加列*/
        model = new DefaultTableModel();
        model.addColumn("报表单位");
        model.addColumn("报表代码");
        model.addColumn("报表名称");
        model.addColumn("报表版别");
        model.addColumn("报表类型");
        model.addColumn("报表周期");
        model.addColumn("报表形式");
        Vector vecReport = code.getReportMain();
        /**添加行*/
        for (int i = 0; i < vecReport.size(); i++)
        {
            ReportMain report = (ReportMain) vecReport.elementAt(i);
            vecReportInfo = new Vector();
            vecReportInfo.addElement(code.getBranch(report.getBranchId()));
            vecReportInfo.addElement(report.getReportId());
            vecReportInfo.addElement(report.getReportName());
            vecReportInfo.addElement(report.getReportEdition());
            vecReportInfo.addElement(report.getReportType());
            vecReportInfo.addElement(report.getReportCycle());
            vecReportInfo.addElement(report.getReportAtt());
            model.addRow(vecReportInfo);
        }
        jTableReport.setRowHeight(30);
        jTableReport.setModel(model);
        jTableReport.setSelectionMode(0);
        System.out.println(jTableReport.getCellEditor(0, 0));
    }

    /**
     * 初始化数据
     */
    private void initData()
    {
        code.getReportMainMap();
        code.getBranchMap();
    }

    public static void main(String[] args)
    {
        FrameManageReport manage = new FrameManageReport();
        manage.setSize(550, 350);
        manage.open();
    }

    /**
     * 查询报表
     * */
    public void queryReport()
    {
        FrameQueryReport query = new FrameQueryReport(this);
        query.setSize(510, 350);
        query.open();
    }

    /**
     * 新建报表
     */
    public void newReport()
    {
        openFrame(new FrameNewReport(this));
    }

    /**
     * 修改报表
     */
    public void updateReport()
    {
        openFrame(new FrameUpdReport(this));
    }

    /**
     * 打开子窗口
     * @param frmEditReport 子窗口句柄
     */
    private void openFrame(FrameEditReport frmEditReport)
    {
        frmEditReport.setSize(570, 360);
        ReportMain tReportMain = new ReportMain();
        if (frmEditReport instanceof FrameUpdReport)
        {
            int intRow = jTableReport.getSelectedRow();
            String strBranchId = ((Branch) jTableReport.getValueAt(intRow, 0)).
                                 getBranchId();
            String strReportId = (String) jTableReport.getValueAt(intRow, 1);
            String strReportEdition = (String) jTableReport.getValueAt(intRow,
                    3);
            String strReportAtt = (String) jTableReport.getValueAt(intRow, 6);
            tReportMain = code.getReportMain(strBranchId, strReportId,
                                             strReportEdition);
        }
        frmEditReport.open(tReportMain);
    }

    /**
     * 删除选定的报表
     */
    public void deleteReport()
    {
        /**被选定的报表在jTable中的id号*/
        int intRowNum = -1;
        /**报表所属单位*/
        String strBranchId = "";
        /**报表代码*/
        String strReportId = "";
        /**报表版别*/
        String strReportEdition = "";
        /**读取被选定报表在jTable中的id号*/
        intRowNum = jTableReport.getSelectedRow();
        /**如果没有选中报表则退出*/
        if (intRowNum == -1)
        {
            return;
        }
        if (JOptionPane.showConfirmDialog
            (null, "真的要删除吗?", "删除报表",
             JOptionPane.OK_CANCEL_OPTION,
             JOptionPane.QUESTION_MESSAGE)
            == JOptionPane.CANCEL_OPTION)
        {
            return;
        }
        /**读取报表所属单位信息*/
        Branch branch = (Branch) (jTableReport.getValueAt(intRowNum, 0));
        strBranchId = branch.getBranchId();
        /**读取报表代码*/
        strReportId = (String) (jTableReport.getValueAt(intRowNum, 1));
        /**读取报表版别*/
        strReportEdition = (String) (jTableReport.getValueAt(intRowNum, 3));
        /**在jTable控件中删除选定的报表*/
        model.removeRow(intRowNum);
        jTableReport.setModel(model);
        /**删除报表信息*/
        ReportMain report = new ReportMain();
        report.setBranchId(strBranchId);
        report.setReportId(strReportId);
        report.setReportEdition(strReportEdition);
        report.delete();
        /**todo删除报表定义*/
        String strFileName = strBranchId + SysConfig.REPORTJOINCHAR +
                             strReportId + SysConfig.REPORTJOINCHAR +
                             strReportEdition;
        //删除定义数据文件
        XWriter.deleteFile("define", strFileName + ".xml");
        //删除定义格式文件
        XWriter.deleteFile("define", strFileName + ".xls");
        //更新代码表 lixy 20030224
        code.getReportMainMap();
    }

    /**
     * 打开选定的报表
     */
    public void openReport()
    {
        String strBranchId = "";
        String strReportId = "";
        String strReportEdition = "";
        int intRowNum = -1;
        intRowNum = jTableReport.getSelectedRow();
        if (intRowNum == -1)
        {
            return;
        }
        /**读取被选定报表的主健*/
        strBranchId = ((Branch) (jTableReport.getValueAt(intRowNum, 0))).
                      getBranchId();
        strReportId = (String) (jTableReport.getValueAt(intRowNum, 1));
        strReportEdition = (String) (jTableReport.getValueAt(intRowNum, 3));
        /**设置当前报表对象*/
        ReportMain tReportMain = code.getReportMain(strBranchId, strReportId,
                strReportEdition);
        /**打开当前报表*/
        report.open(tReportMain);
        this.dispose();
    }

    /**
     * 退出报表管理程序
     */
    public void quit()
    {
        this.dispose();
    }

    public DefaultTableModel getModel()
    {
        return model;
    }

    public void setModel(DefaultTableModel model)
    {
        this.model = model;
    }

    void calculate()
    {
        String strBranchId = "";
        String strReportId = "";
        String strReportEdition = "";
        int intRowNum = -1;
        intRowNum = jTableReport.getSelectedRow();
        if (intRowNum == -1)
        {
            return;
        }
        /**读取被选定报表的主健*/
        strBranchId = ((Branch) (jTableReport.getValueAt(intRowNum, 0))).
                      getBranchId();
        strReportId = (String) (jTableReport.getValueAt(intRowNum, 1));
        strReportEdition = (String) (jTableReport.getValueAt(intRowNum, 3));
        /**设置当前报表对象*/
        ReportMain tReportMain = code.getReportMain(strBranchId, strReportId,
                strReportEdition);
        /**计算当前报表*/
        report.calculate(tReportMain);
    }
}
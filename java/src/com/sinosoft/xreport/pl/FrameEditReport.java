package com.sinosoft.xreport.pl;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.sinosoft.xreport.bl.Branch;
import com.sinosoft.xreport.bl.Code;
import com.sinosoft.xreport.bl.ReportMain;
import com.sinosoft.xreport.dl.Source;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Cop                                                                                                                                   yright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author lixy
 * @version 1.0
 * 功能描述：处理控件事件
 */

public abstract class FrameEditReport extends JDialog
{
    /**父窗口句柄*/
    protected FrameManageReport frmParent;
    /**报表对象*/
    protected Report report;
    /**报表信息*/
    protected ReportMain tReportMain = new ReportMain();
    /**数据源*/
    protected Vector dataSource = new Vector();
    /**数据源*/
    protected Vector dataDest = new Vector();
    /**代码表*/
    protected Code code = new Code();

    JTabbedPane jTab = new JTabbedPane();
    JPanel jPanelAttrib = new JPanel();
    JPanel jPanelResource = new JPanel();
    JScrollPane jScrollPaneResource = new JScrollPane();
    JScrollPane jScrollPaneResResult = new JScrollPane();
    JList jListResource = new JList();
    JList jListResResult = new JList();
    JLabel jLabelBranchCode = new JLabel();
    JComboBox jComboBranch = new JComboBox();
    JLabel jLabelReportCode = new JLabel();
    JTextField jTextReportCode = new JTextField();
    JLabel jLabelReportName = new JLabel();
    JTextField jTextReportName = new JTextField();
    JLabel jLabelReportEdition = new JLabel();
    JTextField jTextReportEdition = new JTextField();
    JLabel jLabelReportType = new JLabel();
    JComboBox jComboReportType = new JComboBox();
    JLabel jLabelReportCycle = new JLabel();
    JComboBox jComboReportCycle = new JComboBox();
    JLabel jLabelCurrency = new JLabel();
    JComboBox jComboCurrency = new JComboBox();
    JLabel jLabelAtt = new JLabel();
    JComboBox jComboAtt = new JComboBox();
    JLabel jLabelResource = new JLabel();
    JLabel jLabelResResult = new JLabel();
    JPanel jPanelModel = new JPanel();
    JScrollPane jScrollPaneReport = new JScrollPane();
    JList jListReportModel = new JList();
    JLabel jLabelReportModel = new JLabel();
    JButton jPurview = new JButton();
    JButton jClear = new JButton();
    JButton jNext = new JButton();
    JButton jOK = new JButton();
    JButton jCancel = new JButton();
    JLabel jLabelIcon = new JLabel();
    private JLabel jLabelModel = new JLabel();
    private JScrollPane jScrollPaneModel = new JScrollPane();
    private JList jListModel = new JList();

    /**
     * 构造函数
     */
    public FrameEditReport()
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

    /**
     * 构造函数
     * @param frmParent 父窗口句柄
     */
    public FrameEditReport(FrameManageReport frmParent)
    {
        super(frmParent);
        this.frmParent = frmParent;
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
        jTab.setFont(new java.awt.Font("DialogInput", 0, 12));
        jTab.setBounds(new Rectangle(169, 24, 385, 238));
        jPanelAttrib.setLayout(null);
        jPanelAttrib.setFont(new java.awt.Font("DialogInput", 0, 12));
        jPanelResource.setFont(new java.awt.Font("DialogInput", 0, 12));
        jPanelResource.setLayout(null);
        this.setDefaultCloseOperation(3);
        this.setFont(new java.awt.Font("DialogInput", 0, 12));
        this.setModal(true);
        this.setResizable(false);
        jScrollPaneResource.setBounds(new Rectangle(34, 36, 132, 127));
        jScrollPaneResResult.setBounds(new Rectangle(203, 36, 132, 127));
        jLabelBranchCode.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelBranchCode.setText("所属单位");
        jLabelBranchCode.setBounds(new Rectangle(15, 17, 51, 23));
        jComboBranch.setBounds(new Rectangle(76, 17, 101, 23));
        jLabelReportCode.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelReportCode.setText("报表代码");
        jLabelReportCode.setBounds(new Rectangle(15, 52, 51, 23));
        jTextReportCode.setBounds(new Rectangle(76, 52, 101, 23));
        jLabelReportName.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelReportName.setText("报表名称");
        jLabelReportName.setBounds(new Rectangle(15, 86, 51, 23));
        jTextReportName.setBounds(new Rectangle(76, 86, 101, 23));
        jLabelReportEdition.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelReportEdition.setText("报表版别");
        jLabelReportEdition.setBounds(new Rectangle(15, 122, 51, 23));
        jTextReportEdition.setToolTipText("");
        jTextReportEdition.setBounds(new Rectangle(75, 122, 101, 23));
        jLabelReportType.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelReportType.setText("报表类型");
        jLabelReportType.setBounds(new Rectangle(195, 17, 51, 23));
        jComboReportType.setBounds(new Rectangle(256, 17, 101, 23));
        jLabelReportCycle.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelReportCycle.setText("报表周期");
        jLabelReportCycle.setBounds(new Rectangle(195, 52, 51, 23));
        jComboReportCycle.setBounds(new Rectangle(256, 52, 101, 23));
        jLabelCurrency.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelCurrency.setText("币   别");
        jLabelCurrency.setBounds(new Rectangle(195, 86, 51, 23));
        jComboCurrency.setBounds(new Rectangle(256, 86, 101, 23));
        jLabelAtt.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelAtt.setText("形   式");
        jLabelAtt.setBounds(new Rectangle(195, 122, 51, 23));
        jComboAtt.setBounds(new Rectangle(256, 122, 101, 23));
        jLabelResource.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelResource.setText("数据源");
        jLabelResource.setBounds(new Rectangle(36, 14, 63, 20));
        jLabelResResult.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelResResult.setText("结果");
        jLabelResResult.setBounds(new Rectangle(204, 14, 63, 20));
        jPanelModel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jPanelModel.setLayout(null);
        jScrollPaneReport.setBounds(new Rectangle(34, 36, 132, 127));
        jLabelReportModel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelReportModel.setText("报表模板");
        jLabelReportModel.setBounds(new Rectangle(38, 11, 69, 23));
        jPurview.setBounds(new Rectangle(185, 279, 72, 30));
        jPurview.setFont(new java.awt.Font("DialogInput", 0, 12));
        jPurview.setText("上一步");
        jPurview.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                purview();
            }
        });
        jClear.setBounds(new Rectangle(328, 279, 72, 30));
        jClear.setFont(new java.awt.Font("DialogInput", 0, 12));
        jClear.setText("清 除");
        jNext.setBounds(new Rectangle(256, 279, 72, 30));
        jNext.setFont(new java.awt.Font("DialogInput", 0, 12));
        jNext.setText("下一步");
        jNext.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                next();
            }
        });
        jOK.setBounds(new Rectangle(400, 279, 72, 30));
        jOK.setFont(new java.awt.Font("DialogInput", 0, 12));
        jOK.setText("完 成");
        jOK.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                commit();
            }
        });
        jCancel.setBounds(new Rectangle(470, 279, 72, 30));
        jCancel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jCancel.setText("取 消");
        jCancel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                cancel();
            }
        });
        jListResource.setFont(new java.awt.Font("DialogInput", 0, 12));
        jListResource.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseReleased(MouseEvent e)
            {
                addResource(e);
            }
        });
        jListResResult.setFont(new java.awt.Font("DialogInput", 0, 12));
        jListResResult.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseReleased(MouseEvent e)
            {
                removeResource(e);
            }
        });
        jListReportModel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelIcon.setIcon(new ImageIcon(FrameEditReport.class.getResource(
                "..\\resource\\ApplicationWizardLarge.gif")));
        jLabelIcon.setBounds(new Rectangle(23, 26, 137, 283));
        jLabelModel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelModel.setText("结果");
        jLabelModel.setBounds(new Rectangle(217, 11, 63, 19));
        jScrollPaneModel.setBounds(new Rectangle(203, 36, 132, 127));
        jPanelModel.add(jScrollPaneReport, null);
        jPanelModel.add(jLabelReportModel, null);
        jPanelModel.add(jLabelModel, null);
        jScrollPaneReport.getViewport().add(jListReportModel, null);
        this.getContentPane().add(jLabelIcon, null);
        this.getContentPane().add(jNext, null);
        this.getContentPane().add(jClear, null);
        this.getContentPane().add(jPurview, null);
        this.getContentPane().add(jOK, null);
        this.getContentPane().add(jTab, null);
        jTab.add(jPanelAttrib, "报表属性");
        jTab.add(jPanelModel, "报表模板");
        jTab.add(jPanelResource, "报表数据源");
        jPanelResource.add(jScrollPaneResource, null);
        jPanelResource.add(jScrollPaneResResult, null);
        jPanelResource.add(jLabelResource, null);
        jPanelResource.add(jLabelResResult, null);
        jScrollPaneResResult.getViewport().add(jListResResult, null);
        jScrollPaneResource.getViewport().add(jListResource, null);
        jPanelAttrib.add(jLabelBranchCode, null);
        jPanelAttrib.add(jComboBranch, null);
        jPanelAttrib.add(jLabelReportCode, null);
        jPanelAttrib.add(jLabelReportName, null);
        jPanelAttrib.add(jTextReportName, null);
        jPanelAttrib.add(jTextReportCode, null);
        jPanelAttrib.add(jLabelReportEdition, null);
        jPanelAttrib.add(jTextReportEdition, null);
        jPanelAttrib.add(jLabelReportType, null);
        jPanelAttrib.add(jComboReportType, null);
        jPanelAttrib.add(jLabelReportCycle, null);
        jPanelAttrib.add(jComboReportCycle, null);
        jPanelAttrib.add(jLabelCurrency, null);
        jPanelAttrib.add(jComboCurrency, null);
        jPanelAttrib.add(jLabelAtt, null);
        jPanelAttrib.add(jComboAtt, null);
        this.getContentPane().add(jCancel, null);
        jPanelModel.add(jScrollPaneModel, null);
        jScrollPaneModel.getViewport().add(jListModel, null);

        /**总体*/
        jTab.setSelectedIndex(0);
        jTab.setEnabledAt(0, true);
        jTab.setEnabledAt(1, false);
        jTab.setEnabledAt(2, false);
        jPurview.setEnabled(false);
        jOK.setEnabled(false);
        jClear.setEnabled(true);
        jNext.setEnabled(true);
        this.setTitle("新建报表－－第一步");
    }

    /**
     * 关闭窗口
     */
    public void cancel()
    {
        dispose();
    }

    /**
     * 打开窗口
     * @param report 报表对象
     */
    public abstract void open(ReportMain report);

    /**
     * 下一步
     */
    public void next()
    {
        int intTab = jTab.getSelectedIndex();
        switch (intTab)
        {
            case 0:
                nextSheet0();
                break;
            case 1:
                nextSheet1();
                break;
        }
    }

    /**
     * 确认第一页
     */
    private void nextSheet0()
    {
        //用户输入有效性检验
        if (jTextReportCode.getText().equals("") ||
            jTextReportName.getText().equals("") ||
            jTextReportEdition.getText().equals(""))
        {
            JOptionPane.showMessageDialog(
                    null,
                    "报表信息不完整(报表代码、报表名称、报表版别不能置空)!",
                    "Warning!",
                    JOptionPane.WARNING_MESSAGE
                    );
            return;
        }
        //根据选择的单位名称查找单位代码
        Branch branch = (Branch) (jComboBranch.getSelectedItem());
        String strBranchId = branch.getBranchId();
        //确定报表基本属性
        tReportMain = new ReportMain();
        tReportMain.setBranchId(strBranchId);
        tReportMain.setReportId(jTextReportCode.getText());
        tReportMain.setReportName(jTextReportName.getText());
        tReportMain.setReportEdition(jTextReportEdition.getText());
        tReportMain.setReportCycle((String) (jComboReportCycle.getSelectedItem()));
        tReportMain.setReportType((String) jComboReportType.getSelectedItem());
        tReportMain.setReportAtt((String) jComboAtt.getSelectedItem());
        tReportMain.setCurrency((String) jComboCurrency.getSelectedItem());
        //调整对话框显示内容
        jTab.setEnabledAt(0, false);
        jTab.setEnabledAt(1, true);
        jTab.setEnabledAt(2, false);
        jTab.setSelectedIndex(1);
        setTitle("新建报表－－第二步");
        jPurview.setEnabled(true);
    }

    /**
     * 确认第二页
     */
    private void nextSheet1()
    {
        /**调整对话框显示*/
        jTab.setEnabledAt(0, false);
        jTab.setEnabledAt(1, false);
        jTab.setEnabledAt(2, true);
        jTab.setSelectedIndex(2);
        this.setTitle("新建报表－－第三步");
        jNext.setEnabled(false);
        jOK.setEnabled(true);
    }

    /**
     * 上一步
     */
    public void purview()
    {
        int intTab = jTab.getSelectedIndex();
        switch (intTab)
        {
            case 0:

            case 1:
                purviewSheet1();
                break;
            case 2:
                purviewSheet2();
                break;
        }
    }

    /**
     * 第二页
     */
    private void purviewSheet1()
    {
        jTab.setEnabledAt(0, true);
        jTab.setEnabledAt(1, false);
        jTab.setEnabledAt(2, false);
        jTab.setSelectedIndex(0);
        this.setTitle("新建报表－－第一步");
        jPurview.setEnabled(false);
    }

    /**
     * 第三页
     */
    private void purviewSheet2()
    {
        jTab.setEnabledAt(0, false);
        jTab.setEnabledAt(1, true);
        jTab.setEnabledAt(2, false);
        jTab.setSelectedIndex(1);
        this.setTitle("新建报表－－第二步");
    }

    public void addResource(MouseEvent e)
    {
        if (dataSource.size() == 0)
        {
            return;
        }
        Source source = (Source) jListResource.getSelectedValue();
        /**双击列表框*/
        if (e.getClickCount() == 2)
        {
            /**判重*/
            if (!dataDest.contains(source))
            {
                /**添加*/
                dataDest.addElement(source);
                jListResResult.setListData(dataDest);
            }
        }
    }

    public void removeResource(MouseEvent e)
    {
        int intSelected;
        intSelected = jListResResult.getSelectedIndex();
        if (e.getClickCount() == 2 && dataDest.size() > 0)
        {
            dataDest.remove(intSelected);
            jListResResult.setListData(dataDest);
        }
    }

    /**
     * 提交对话框操作
     */
    protected abstract void commit();


    /**
     * 提交对报表格式的修改
     */
    protected abstract void commitReport();


    /**
     * 提交对父窗口界面的修改
     */
    protected abstract void commitParent();

}
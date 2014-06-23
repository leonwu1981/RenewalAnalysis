package com.sinosoft.xreport.pl;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.sinosoft.xreport.bl.Branch;
import com.sinosoft.xreport.bl.Code;
import com.sinosoft.xreport.bl.ReportMain;

/**
 * <p>Title: XReport 1.0 (c)Sinosoft 2003</p>
 * <p>Description: �����ѯ������</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft</p>
 * @author lixy
 * @version 1.0
 */

public class FrameQueryReport extends JDialog
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**�����ھ��*/
    private FrameManageReport manage = null;
    /**�����*/
    private Code code = new Code();

    /**�Զ�����*/
    private JPanel jPanel1 = new JPanel();
    private Border border1;
    private TitledBorder titledBorder1;
    private JLabel jLabelBranch = new JLabel();
    private JComboBox jComboBranch = new JComboBox();
    private JLabel jLabelReportId = new JLabel();
    private JComboBox jComboReportId = new JComboBox();
    private JLabel jLabelReportName = new JLabel();
    private JComboBox jComboReportName = new JComboBox();
    private JLabel jLabelReportEdition = new JLabel();
    private JComboBox jComboReportEdition = new JComboBox();
    private JLabel jLabelReportType = new JLabel();
    private JLabel jLabelReportCycle = new JLabel();
    private JLabel jLabelReportAtt = new JLabel();
    private JLabel jLabelCurrency = new JLabel();
    private JComboBox jComboReportType = new JComboBox();
    private JComboBox jComboReportCycle = new JComboBox();
    private JComboBox jComboReportAtt = new JComboBox();
    private JComboBox jComboCurrency = new JComboBox();
    private JPanel jPanel2 = new JPanel();
    private JButton jButtonOK = new JButton();
    private JButton jButtonClear = new JButton();
    private JButton jButtonCancel = new JButton();

    /**
     * ���캯��
     */
    public FrameQueryReport()
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
     * ���캯��
     * @param manage �����ھ��
     */
    public FrameQueryReport(FrameManageReport manage)
    {
        super(manage);
        this.manage = manage;
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
        border1 = BorderFactory.createEtchedBorder(Color.white,
                new Color(148, 145, 140));
        titledBorder1 = new TitledBorder(border1, "��ѯ����");
        this.getContentPane().setLayout(null);
        jPanel1.setFont(new java.awt.Font("DialogInput", 0, 12));
        jPanel1.setBorder(titledBorder1);
        jPanel1.setBounds(new Rectangle(39, 29, 430, 222));
        jPanel1.setLayout(null);
        jLabelBranch.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelBranch.setText("������λ");
        jLabelBranch.setBounds(new Rectangle(30, 41, 67, 26));
        jLabelReportId.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelReportId.setText("�������");
        jLabelReportId.setBounds(new Rectangle(29, 83, 67, 26));
        jLabelReportName.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelReportName.setText("��������");
        jLabelReportName.setBounds(new Rectangle(29, 124, 67, 26));
        jLabelReportEdition.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelReportEdition.setText("������");
        jLabelReportEdition.setBounds(new Rectangle(29, 166, 67, 26));
        jLabelReportType.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelReportType.setText("��������");
        jLabelReportType.setBounds(new Rectangle(225, 41, 72, 23));
        jLabelReportCycle.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelReportCycle.setText("��������");
        jLabelReportCycle.setBounds(new Rectangle(225, 83, 72, 23));
        jLabelReportAtt.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelReportAtt.setText("������ʽ");
        jLabelReportAtt.setBounds(new Rectangle(225, 124, 72, 23));
        jLabelCurrency.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelCurrency.setText("��   ��");
        jLabelCurrency.setBounds(new Rectangle(225, 166, 72, 23));
        jPanel2.setBounds(new Rectangle(43, 262, 426, 49));
        jPanel2.setLayout(null);
        jButtonOK.setBounds(new Rectangle(46, 5, 69, 29));
        jButtonOK.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonOK.setText("ȷ��");
        jButtonOK.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                commit();
            }
        });
        jButtonClear.setBounds(new Rectangle(176, 5, 69, 29));
        jButtonClear.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonClear.setText("���");
        jButtonClear.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                clear();
            }
        });
        jButtonCancel.setBounds(new Rectangle(306, 5, 69, 29));
        jButtonCancel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonCancel.setText("ȡ��");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                quit();
            }
        });
        jComboCurrency.setBounds(new Rectangle(288, 166, 102, 24));
        jComboReportEdition.setBounds(new Rectangle(90, 166, 102, 24));
        jComboReportAtt.setBounds(new Rectangle(288, 124, 102, 24));
        jComboReportName.setBounds(new Rectangle(90, 124, 102, 24));
        jComboReportCycle.setBounds(new Rectangle(289, 83, 102, 24));
        jComboReportId.setBounds(new Rectangle(90, 83, 102, 24));
        jComboReportType.setBounds(new Rectangle(289, 41, 102, 24));
        jComboBranch.setBounds(new Rectangle(90, 41, 102, 24));
        this.setModal(true);
        this.setResizable(false);
        this.getContentPane().add(jPanel1, null);
        jPanel1.add(jLabelBranch, null);
        jPanel1.add(jComboBranch, null);
        jPanel1.add(jLabelReportId, null);
        jPanel1.add(jComboReportId, null);
        jPanel1.add(jLabelReportName, null);
        jPanel1.add(jComboReportName, null);
        jPanel1.add(jLabelReportEdition, null);
        jPanel1.add(jComboReportEdition, null);
        jPanel1.add(jLabelReportType, null);
        jPanel1.add(jLabelReportCycle, null);
        jPanel1.add(jLabelReportAtt, null);
        jPanel1.add(jLabelCurrency, null);
        jPanel1.add(jComboReportType, null);
        jPanel1.add(jComboReportCycle, null);
        jPanel1.add(jComboReportAtt, null);
        jPanel1.add(jComboCurrency, null);
        this.getContentPane().add(jPanel2, null);
        jPanel2.add(jButtonOK, null);
        jPanel2.add(jButtonClear, null);
        jPanel2.add(jButtonCancel, null);
    }

    /**
     * ��ʾ����
     */
    public void open()
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
        /**��ʼ������*/
        this.setTitle("�����ѯ");
        /**����������λ��Ͽ�*/
        jComboBranch.removeAllItems();
        jComboBranch.addItem("");
        Vector vecBranch = code.getBranch();
        for (int i = 0; i < vecBranch.size(); i++)
        {
            jComboBranch.addItem(vecBranch.elementAt(i));
        }
        if (jComboBranch.getItemCount() != 0)
        {
            jComboBranch.setSelectedIndex(0);
        }
        /**���������Ͽ�*/
        jComboReportId.removeAllItems();
        jComboReportId.addItem("");
        Vector vecReportMain = code.getReportMain();
        for (int i = 0; i < vecReportMain.size(); i++)
        {
            jComboReportId.addItem(((ReportMain) vecReportMain.elementAt(i))
                                   .getReportId());
        }
        if (jComboReportId.getItemCount() != 0)
        {
            jComboReportId.setSelectedIndex(0);
        }
        /**����������Ͽ�*/
        jComboReportName.removeAllItems();
        jComboReportName.addItem("");
        for (int i = 0; i < vecReportMain.size(); i++)
        {
            jComboReportName.addItem(((ReportMain) vecReportMain.elementAt(i))
                                     .getReportName());
        }
        if (jComboReportName.getItemCount() != 0)
        {
            jComboReportName.setSelectedIndex(0);
        }
        /**��������Ͽ�*/
        jComboReportEdition.removeAllItems();
        jComboReportEdition.addItem("");
        for (int i = 0; i < vecReportMain.size(); i++)
        {
            jComboReportEdition.addItem(((ReportMain) vecReportMain.elementAt(i))
                                        .getReportEdition());
        }
        if (jComboReportEdition.getItemCount() != 0)
        {
            jComboReportEdition.setSelectedIndex(0);
        }
        /**����������Ͽ�*/
        jComboReportType.removeAllItems();
        jComboReportType.addItem("");
        jComboReportType.addItem("�ձ�");
        jComboReportType.addItem("�±�");
        jComboReportType.addItem("����");
        jComboReportType.addItem("�걨");
        jComboReportType.addItem("����ʱ��α���");
        jComboReportType.setSelectedIndex(0);
        /**����������Ͽ�*/
        jComboReportCycle.removeAllItems();
        jComboReportCycle.addItem("");
        jComboReportCycle.addItem("1");
        jComboReportCycle.addItem("2");
        jComboReportCycle.addItem("3");
        jComboReportCycle.addItem("4");
        jComboReportCycle.addItem("5");
        jComboReportCycle.addItem("6");
        jComboReportCycle.setSelectedIndex(0);
        /**������Ͽ�*/
        jComboCurrency.removeAllItems();
        jComboCurrency.addItem("");
        jComboCurrency.addItem("�����");
        jComboCurrency.setSelectedIndex(0);
        /**������ʽ��Ͽ�*/
        jComboReportAtt.removeAllItems();
        jComboReportAtt.addItem("");
        jComboReportAtt.addItem("�̶���ʽ");
        jComboReportAtt.addItem("�嵥��ʽ");
        jComboReportAtt.setSelectedIndex(0);
    }

    /**
     * ��ʼ������
     */
    private void initData()
    {
        code.getReportMainMap();
        code.getBranchMap();
    }

    /**
     * �ύ�Ի������
     */
    private void commit()
    {
        commitParent();
        commitReportFormat();
        commitReportModel();
    }

    /**
     * �ύ�Ա������ݵ��޸�
     */
    private void commitReportModel()
    {

    }

    /**
     * �ύ�Ա����ʽ���޸�
     */
    private void commitReportFormat()
    {

    }

    /**
     * �ύ�Ը����ڽ�����޸�
     */
    private void commitParent()
    {
        /**��������*/
        String strBranchId = "";
        String strReportId = "";
        String strReportName = "";
        String strReportEdition = "";
        String strReportType = "";
        String strReportCycle = "";
        String strReportAtt = "";
        String strCurrency = "";
        /**��ȡ��ѯ����*/
        Object obj = jComboBranch.getSelectedItem();
        if (obj instanceof Branch)
        {
            strBranchId = ((Branch) obj).getBranchId();
        }
        strReportId = (String) (jComboReportId.getSelectedItem());
        strReportName = (String) (jComboReportName.getSelectedItem());
        strReportEdition = (String) (jComboReportEdition.getSelectedItem());
        int intReportType = jComboReportType.getSelectedIndex();
        if (intReportType != 0)
        {
            strReportType = "" + intReportType;
        }
        int intReportCycle = jComboReportCycle.getSelectedIndex();
        if (intReportCycle != 0)
        {
            strReportCycle = "" + intReportCycle;
        }
        int intReportAtt = jComboReportAtt.getSelectedIndex();
        if (intReportAtt != 0)
        {
            strReportAtt = "" + intReportAtt;
        }
        int intCurrency = jComboCurrency.getSelectedIndex();
        if (intCurrency != 0)
        {
            strCurrency = "" + intCurrency;
        }
        /**���ݲ�ѯ������ѯ������Ϣ*/
        ReportMain report = new ReportMain();
        report.setBranchId(strBranchId);
        report.setReportId(strReportId);
        report.setReportName(strReportName);
        report.setReportEdition(strReportEdition);
        report.setReportType(strReportType);
        report.setReportCycle(strReportCycle);
        report.setReportAtt(strReportAtt);
        report.setCurrency(strCurrency);
        Vector vecReportMain = report.query(report);
        /**���±��������*/
        DefaultTableModel model = manage.getModel();
        int intRowCount = model.getRowCount();
        for (int i = 0; i < intRowCount; i++)
        {
            model.removeRow(0);
        }
        for (int i = 0; i < vecReportMain.size(); i++)
        {
            Vector vecReport = new Vector();
            vecReport.addElement(code.getBranch(((ReportMain)
                                                 vecReportMain.elementAt(i)).
                                                getBranchId()));
            vecReport.addElement(((ReportMain)
                                  vecReportMain.elementAt(i)).getReportId());
            vecReport.addElement(((ReportMain)
                                  vecReportMain.elementAt(i)).getReportName());
            vecReport.addElement(((ReportMain)
                                  vecReportMain.elementAt(i)).getReportEdition());
            vecReport.addElement(((ReportMain)
                                  vecReportMain.elementAt(i)).getReportType());
            vecReport.addElement(((ReportMain)
                                  vecReportMain.elementAt(i)).getReportCycle());
            vecReport.addElement(((ReportMain)
                                  vecReportMain.elementAt(i)).getReportAtt());
            model.addRow(vecReport);
        }
        manage.jTableReport.setModel(model);
        this.dispose();
    }

    public static void main(String[] args)
    {
        FrameQueryReport frmQueryReport = new FrameQueryReport();
        frmQueryReport.setSize(510, 350);
        frmQueryReport.setModal(true);
        frmQueryReport.open();
    }

    /**
     * �˳������ѯ����
     */
    public void quit()
    {
        this.dispose();
    }

    /**
     * �����ѯ����
     */
    public void clear()
    {
        jComboBranch.setSelectedIndex(0);
        jComboReportId.setSelectedIndex(0);
        jComboReportName.setSelectedIndex(0);
        jComboReportEdition.setSelectedIndex(0);
        jComboReportType.setSelectedIndex(0);
        jComboReportCycle.setSelectedIndex(0);
        jComboReportAtt.setSelectedIndex(0);
        jComboCurrency.setSelectedIndex(0);
    }

}

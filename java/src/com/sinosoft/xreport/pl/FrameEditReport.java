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
 * ��������������ؼ��¼�
 */

public abstract class FrameEditReport extends JDialog
{
    /**�����ھ��*/
    protected FrameManageReport frmParent;
    /**�������*/
    protected Report report;
    /**������Ϣ*/
    protected ReportMain tReportMain = new ReportMain();
    /**����Դ*/
    protected Vector dataSource = new Vector();
    /**����Դ*/
    protected Vector dataDest = new Vector();
    /**�����*/
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
     * ���캯��
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
     * ���캯��
     * @param frmParent �����ھ��
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
        jLabelBranchCode.setText("������λ");
        jLabelBranchCode.setBounds(new Rectangle(15, 17, 51, 23));
        jComboBranch.setBounds(new Rectangle(76, 17, 101, 23));
        jLabelReportCode.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelReportCode.setText("�������");
        jLabelReportCode.setBounds(new Rectangle(15, 52, 51, 23));
        jTextReportCode.setBounds(new Rectangle(76, 52, 101, 23));
        jLabelReportName.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelReportName.setText("��������");
        jLabelReportName.setBounds(new Rectangle(15, 86, 51, 23));
        jTextReportName.setBounds(new Rectangle(76, 86, 101, 23));
        jLabelReportEdition.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelReportEdition.setText("������");
        jLabelReportEdition.setBounds(new Rectangle(15, 122, 51, 23));
        jTextReportEdition.setToolTipText("");
        jTextReportEdition.setBounds(new Rectangle(75, 122, 101, 23));
        jLabelReportType.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelReportType.setText("��������");
        jLabelReportType.setBounds(new Rectangle(195, 17, 51, 23));
        jComboReportType.setBounds(new Rectangle(256, 17, 101, 23));
        jLabelReportCycle.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelReportCycle.setText("��������");
        jLabelReportCycle.setBounds(new Rectangle(195, 52, 51, 23));
        jComboReportCycle.setBounds(new Rectangle(256, 52, 101, 23));
        jLabelCurrency.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelCurrency.setText("��   ��");
        jLabelCurrency.setBounds(new Rectangle(195, 86, 51, 23));
        jComboCurrency.setBounds(new Rectangle(256, 86, 101, 23));
        jLabelAtt.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelAtt.setText("��   ʽ");
        jLabelAtt.setBounds(new Rectangle(195, 122, 51, 23));
        jComboAtt.setBounds(new Rectangle(256, 122, 101, 23));
        jLabelResource.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelResource.setText("����Դ");
        jLabelResource.setBounds(new Rectangle(36, 14, 63, 20));
        jLabelResResult.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelResResult.setText("���");
        jLabelResResult.setBounds(new Rectangle(204, 14, 63, 20));
        jPanelModel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jPanelModel.setLayout(null);
        jScrollPaneReport.setBounds(new Rectangle(34, 36, 132, 127));
        jLabelReportModel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelReportModel.setText("����ģ��");
        jLabelReportModel.setBounds(new Rectangle(38, 11, 69, 23));
        jPurview.setBounds(new Rectangle(185, 279, 72, 30));
        jPurview.setFont(new java.awt.Font("DialogInput", 0, 12));
        jPurview.setText("��һ��");
        jPurview.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                purview();
            }
        });
        jClear.setBounds(new Rectangle(328, 279, 72, 30));
        jClear.setFont(new java.awt.Font("DialogInput", 0, 12));
        jClear.setText("�� ��");
        jNext.setBounds(new Rectangle(256, 279, 72, 30));
        jNext.setFont(new java.awt.Font("DialogInput", 0, 12));
        jNext.setText("��һ��");
        jNext.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                next();
            }
        });
        jOK.setBounds(new Rectangle(400, 279, 72, 30));
        jOK.setFont(new java.awt.Font("DialogInput", 0, 12));
        jOK.setText("�� ��");
        jOK.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                commit();
            }
        });
        jCancel.setBounds(new Rectangle(470, 279, 72, 30));
        jCancel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jCancel.setText("ȡ ��");
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
        jLabelModel.setText("���");
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
        jTab.add(jPanelAttrib, "��������");
        jTab.add(jPanelModel, "����ģ��");
        jTab.add(jPanelResource, "��������Դ");
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

        /**����*/
        jTab.setSelectedIndex(0);
        jTab.setEnabledAt(0, true);
        jTab.setEnabledAt(1, false);
        jTab.setEnabledAt(2, false);
        jPurview.setEnabled(false);
        jOK.setEnabled(false);
        jClear.setEnabled(true);
        jNext.setEnabled(true);
        this.setTitle("�½���������һ��");
    }

    /**
     * �رմ���
     */
    public void cancel()
    {
        dispose();
    }

    /**
     * �򿪴���
     * @param report �������
     */
    public abstract void open(ReportMain report);

    /**
     * ��һ��
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
     * ȷ�ϵ�һҳ
     */
    private void nextSheet0()
    {
        //�û�������Ч�Լ���
        if (jTextReportCode.getText().equals("") ||
            jTextReportName.getText().equals("") ||
            jTextReportEdition.getText().equals(""))
        {
            JOptionPane.showMessageDialog(
                    null,
                    "������Ϣ������(������롢�������ơ����������ÿ�)!",
                    "Warning!",
                    JOptionPane.WARNING_MESSAGE
                    );
            return;
        }
        //����ѡ��ĵ�λ���Ʋ��ҵ�λ����
        Branch branch = (Branch) (jComboBranch.getSelectedItem());
        String strBranchId = branch.getBranchId();
        //ȷ�������������
        tReportMain = new ReportMain();
        tReportMain.setBranchId(strBranchId);
        tReportMain.setReportId(jTextReportCode.getText());
        tReportMain.setReportName(jTextReportName.getText());
        tReportMain.setReportEdition(jTextReportEdition.getText());
        tReportMain.setReportCycle((String) (jComboReportCycle.getSelectedItem()));
        tReportMain.setReportType((String) jComboReportType.getSelectedItem());
        tReportMain.setReportAtt((String) jComboAtt.getSelectedItem());
        tReportMain.setCurrency((String) jComboCurrency.getSelectedItem());
        //�����Ի�����ʾ����
        jTab.setEnabledAt(0, false);
        jTab.setEnabledAt(1, true);
        jTab.setEnabledAt(2, false);
        jTab.setSelectedIndex(1);
        setTitle("�½��������ڶ���");
        jPurview.setEnabled(true);
    }

    /**
     * ȷ�ϵڶ�ҳ
     */
    private void nextSheet1()
    {
        /**�����Ի�����ʾ*/
        jTab.setEnabledAt(0, false);
        jTab.setEnabledAt(1, false);
        jTab.setEnabledAt(2, true);
        jTab.setSelectedIndex(2);
        this.setTitle("�½�������������");
        jNext.setEnabled(false);
        jOK.setEnabled(true);
    }

    /**
     * ��һ��
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
     * �ڶ�ҳ
     */
    private void purviewSheet1()
    {
        jTab.setEnabledAt(0, true);
        jTab.setEnabledAt(1, false);
        jTab.setEnabledAt(2, false);
        jTab.setSelectedIndex(0);
        this.setTitle("�½���������һ��");
        jPurview.setEnabled(false);
    }

    /**
     * ����ҳ
     */
    private void purviewSheet2()
    {
        jTab.setEnabledAt(0, false);
        jTab.setEnabledAt(1, true);
        jTab.setEnabledAt(2, false);
        jTab.setSelectedIndex(1);
        this.setTitle("�½��������ڶ���");
    }

    public void addResource(MouseEvent e)
    {
        if (dataSource.size() == 0)
        {
            return;
        }
        Source source = (Source) jListResource.getSelectedValue();
        /**˫���б��*/
        if (e.getClickCount() == 2)
        {
            /**����*/
            if (!dataDest.contains(source))
            {
                /**���*/
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
     * �ύ�Ի������
     */
    protected abstract void commit();


    /**
     * �ύ�Ա����ʽ���޸�
     */
    protected abstract void commitReport();


    /**
     * �ύ�Ը����ڽ�����޸�
     */
    protected abstract void commitParent();

}
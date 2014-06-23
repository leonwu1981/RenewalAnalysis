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
 * <p>Description: �������������</p>
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
    /**�����ھ��*/
    private XReportMain frmParent;
    /**�������*/
    private Report report;
    /**�����*/
    private Code code = new Code();
    /**jTable�ؼ�ģ��*/
    private DefaultTableModel model = new DefaultTableModel();

    /**�Զ�����*/
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
     * ���캯��
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
        jButtonQuery.setText("��ѯ");
        jButtonQuery.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                queryReport();
            }
        });
        jButtonNew.setBounds(new Rectangle(108, 251, 63, 27));
        jButtonNew.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonNew.setText("�½�");
        jButtonNew.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                newReport();
            }
        });
        jButtonOpen.setBounds(new Rectangle(171, 251, 63, 27));
        jButtonOpen.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonOpen.setText("��");
        jButtonOpen.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                openReport();
            }
        });
        jButtonUpdate.setBounds(new Rectangle(234, 251, 63, 27));
        jButtonUpdate.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonUpdate.setText("�޸�");
        jButtonUpdate.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                updateReport();
            }
        });
        jButtonDelete.setBounds(new Rectangle(297, 251, 63, 27));
        jButtonDelete.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonDelete.setText("ɾ��");
        jButtonDelete.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                deleteReport();
            }
        });
        jButtonQuit.setBounds(new Rectangle(423, 251, 63, 27));
        jButtonQuit.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonQuit.setText("�˳�");
        jButtonQuit.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                quit();
            }
        });
        jButtonCal.setBounds(new Rectangle(360, 251, 63, 27));
        jButtonCal.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonCal.setText("����");
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
     * ���캯��
     * @param frmParent �����ھ��
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
        Vector vecReportInfo = new Vector();
        /**���ñ���*/
        setTitle("�������");
        /**�����*/
        model = new DefaultTableModel();
        model.addColumn("����λ");
        model.addColumn("�������");
        model.addColumn("��������");
        model.addColumn("������");
        model.addColumn("��������");
        model.addColumn("��������");
        model.addColumn("������ʽ");
        Vector vecReport = code.getReportMain();
        /**�����*/
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
     * ��ʼ������
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
     * ��ѯ����
     * */
    public void queryReport()
    {
        FrameQueryReport query = new FrameQueryReport(this);
        query.setSize(510, 350);
        query.open();
    }

    /**
     * �½�����
     */
    public void newReport()
    {
        openFrame(new FrameNewReport(this));
    }

    /**
     * �޸ı���
     */
    public void updateReport()
    {
        openFrame(new FrameUpdReport(this));
    }

    /**
     * ���Ӵ���
     * @param frmEditReport �Ӵ��ھ��
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
     * ɾ��ѡ���ı���
     */
    public void deleteReport()
    {
        /**��ѡ���ı�����jTable�е�id��*/
        int intRowNum = -1;
        /**����������λ*/
        String strBranchId = "";
        /**�������*/
        String strReportId = "";
        /**������*/
        String strReportEdition = "";
        /**��ȡ��ѡ��������jTable�е�id��*/
        intRowNum = jTableReport.getSelectedRow();
        /**���û��ѡ�б������˳�*/
        if (intRowNum == -1)
        {
            return;
        }
        if (JOptionPane.showConfirmDialog
            (null, "���Ҫɾ����?", "ɾ������",
             JOptionPane.OK_CANCEL_OPTION,
             JOptionPane.QUESTION_MESSAGE)
            == JOptionPane.CANCEL_OPTION)
        {
            return;
        }
        /**��ȡ����������λ��Ϣ*/
        Branch branch = (Branch) (jTableReport.getValueAt(intRowNum, 0));
        strBranchId = branch.getBranchId();
        /**��ȡ�������*/
        strReportId = (String) (jTableReport.getValueAt(intRowNum, 1));
        /**��ȡ������*/
        strReportEdition = (String) (jTableReport.getValueAt(intRowNum, 3));
        /**��jTable�ؼ���ɾ��ѡ���ı���*/
        model.removeRow(intRowNum);
        jTableReport.setModel(model);
        /**ɾ��������Ϣ*/
        ReportMain report = new ReportMain();
        report.setBranchId(strBranchId);
        report.setReportId(strReportId);
        report.setReportEdition(strReportEdition);
        report.delete();
        /**todoɾ��������*/
        String strFileName = strBranchId + SysConfig.REPORTJOINCHAR +
                             strReportId + SysConfig.REPORTJOINCHAR +
                             strReportEdition;
        //ɾ�����������ļ�
        XWriter.deleteFile("define", strFileName + ".xml");
        //ɾ�������ʽ�ļ�
        XWriter.deleteFile("define", strFileName + ".xls");
        //���´���� lixy 20030224
        code.getReportMainMap();
    }

    /**
     * ��ѡ���ı���
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
        /**��ȡ��ѡ�����������*/
        strBranchId = ((Branch) (jTableReport.getValueAt(intRowNum, 0))).
                      getBranchId();
        strReportId = (String) (jTableReport.getValueAt(intRowNum, 1));
        strReportEdition = (String) (jTableReport.getValueAt(intRowNum, 3));
        /**���õ�ǰ�������*/
        ReportMain tReportMain = code.getReportMain(strBranchId, strReportId,
                strReportEdition);
        /**�򿪵�ǰ����*/
        report.open(tReportMain);
        this.dispose();
    }

    /**
     * �˳�����������
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
        /**��ȡ��ѡ�����������*/
        strBranchId = ((Branch) (jTableReport.getValueAt(intRowNum, 0))).
                      getBranchId();
        strReportId = (String) (jTableReport.getValueAt(intRowNum, 1));
        strReportEdition = (String) (jTableReport.getValueAt(intRowNum, 3));
        /**���õ�ǰ�������*/
        ReportMain tReportMain = code.getReportMain(strBranchId, strReportId,
                strReportEdition);
        /**���㵱ǰ����*/
        report.calculate(tReportMain);
    }
}
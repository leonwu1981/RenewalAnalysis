package com.sinosoft.xreport.pl;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.sinosoft.xreport.bl.BlockGlobal;
import com.sinosoft.xreport.bl.Code;
import com.sinosoft.xreport.util.SysConfig;


/**
 * <p>Title: test</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft</p>
 * @author lixy
 * @version 1.0
 */

public class FrameGlobalInfo extends JDialog
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**�����ھ��*/
    private XReportMain frmParent;
    /**�������*/
    private Report report;
    /**��jTable������TableModel*/
    private DefaultTableModel model = new DefaultTableModel();
    /**�����*/
    private Code code = new Code();
    /**��ϵ��Ͽ�*/
    private String[] strRelation =
            {
            "=", ">", "<"};
    private JComboBox boxRelation = new JComboBox(strRelation);

    private JButton jButtonAdd = new JButton();
    private JScrollPane jScrollPaneInfo = new JScrollPane();
    private JTable jTableInfo = new JTable();
    private JButton jButtonEdit = new JButton();
    private JButton jButtonRemove = new JButton();
    private JButton jButtonOk = new JButton();
    private JButton jButtonCancel = new JButton();
    private JLabel jLabelGlobalInfo = new JLabel();
    private JLabel jLabelGlobalSql = new JLabel();
    private JScrollPane jScrollPaneGlobal = new JScrollPane();
    private JTextArea jTextGlobalInfo = new JTextArea();

    public FrameGlobalInfo()
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

    public FrameGlobalInfo(XReportMain frmParent)
    {
        super(frmParent);
        this.frmParent = frmParent;
        this.report = frmParent.getReport();
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
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setFont(new java.awt.Font("DialogInput", 0, 12));
        this.setResizable(false);
        this.setTitle("ȫ����Ϣ");
        this.getContentPane().setLayout(null);
        jButtonAdd.setBounds(new Rectangle(372, 66, 63, 27));
        jButtonAdd.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonAdd.setText("���");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                addInfo();
            }
        });
        jScrollPaneInfo.setFont(new java.awt.Font("DialogInput", 0, 12));
        jScrollPaneInfo.setBounds(new Rectangle(32, 65, 319, 137));
        jTableInfo.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonEdit.setBounds(new Rectangle(372, 115, 63, 27));
        jButtonEdit.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonEdit.setText("�༭");
        jButtonEdit.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                editInfo();
            }
        });
        jButtonRemove.setBounds(new Rectangle(372, 163, 63, 27));
        jButtonRemove.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonRemove.setText("ɾ��");
        jButtonRemove.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                removeInfo();
            }
        });
        jButtonOk.setBounds(new Rectangle(372, 250, 63, 27));
        jButtonOk.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonOk.setText("ȷ��");
        jButtonOk.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                commit();
            }
        });
        jButtonCancel.setBounds(new Rectangle(372, 298, 63, 27));
        jButtonCancel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonCancel.setText("ȡ��");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                cancel();
            }
        });
        jLabelGlobalInfo.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelGlobalInfo.setText("ȫ����Ϣ");
        jLabelGlobalInfo.setBounds(new Rectangle(32, 28, 110, 31));
        jLabelGlobalSql.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelGlobalSql.setText("ȫ����Ϣ");
        jLabelGlobalSql.setBounds(new Rectangle(32, 210, 88, 30));
        jScrollPaneGlobal.setFont(new java.awt.Font("DialogInput", 0, 12));
        jScrollPaneGlobal.setBounds(new Rectangle(32, 249, 321, 107));
        this.getContentPane().add(boxRelation, null);
        this.getContentPane().add(jButtonAdd, null);
        this.getContentPane().add(jScrollPaneInfo, null);
        this.getContentPane().add(jButtonEdit, null);
        this.getContentPane().add(jLabelGlobalInfo, null);
        jScrollPaneInfo.getViewport().add(jTableInfo, null);
        this.getContentPane().add(jScrollPaneGlobal, null);
        jScrollPaneGlobal.getViewport().add(jTextGlobalInfo, null);
        this.getContentPane().add(jLabelGlobalSql, null);
        this.getContentPane().add(jButtonOk, null);
        this.getContentPane().add(jButtonRemove, null);
        this.getContentPane().add(jButtonCancel, null);
    }

    public void init()
    {
        /**��ʼ��jTable�ؼ�*/
        model = new DefaultTableModel();
        /**�����*/
        model.addColumn("����");
        model.addColumn("��ϵ");
        model.addColumn("ȡֵ");
        jTableInfo.setModel(model);
        jTableInfo.setRowHeight(30);
        /**�ڶ���ʹ��jComboBox*/
        TableColumn relationColumn = jTableInfo.getColumnModel().getColumn(1);
        relationColumn.setCellEditor(new DefaultCellEditor(boxRelation));
    }

    public static void main(String[] args)
    {
        FrameGlobalInfo panel = new FrameGlobalInfo();
        panel.setSize(470, 450);
        panel.init();
        panel.show();
    }

    void addInfo()
    {
        Vector vecRow = new Vector();
        vecRow.addElement("");
        vecRow.addElement("");
        vecRow.addElement("");
        model.addRow(vecRow);
    }

    void editInfo()
    {

    }

    void removeInfo()
    {
        int intRow = jTableInfo.getSelectedRow();
        jTableInfo.remove(intRow);
    }

    void commit()
    {
        /**��jTable��jTextArea�ؼ����ռ�ȫ����Ϣ*/
        int intRow = jTableInfo.getRowCount();
        String info = "", type = "", operator = "", value = "";
        /**�ռ������ȫ����Ϣ*/
        for (int i = 0; i < intRow; i++)
        {
            //��ȡ���� ˵����������ʱʹ��String�ͣ��Ժ���ܸ�άDim��
            type = (String) jTableInfo.getValueAt(i, 0);
            //��ȡ������
            operator = (String) jTableInfo.getValueAt(i, 1);
            //��ȡȡֵ
            value = (String) jTableInfo.getValueAt(i, 2) +
                    SysConfig.SEPARATORONE;
            //ƴ��ȫ����Ϣ�ַ���
            info = info + type + SysConfig.SEPARATORTWO +
                   operator + SysConfig.SEPARATORTREE + value;
        }
        /**�ռ��û��Զ����ȫ����Ϣ*/
        info = info + BlockGlobal.USER + SysConfig.SEPARATORTWO
               + jTextGlobalInfo.getText();
        report.setGlobal(info);
        this.dispose();
    }

    void cancel()
    {
        this.dispose();
    }

    /**
     * �򿪴���
     * @param text ��ʼ������
     */
    void open(String text)
    {
        initData();
        initInterface(text);
        this.show();
    }

    /**
     * ��ʼ������
     */
    private void initData()
    {

    }

    /**
     * ��ʼ������
     * @param text ��ʼ������
     */
    private void initInterface(String text)
    {
        /**��ʼ��jTable�ؼ�*/
        model = new DefaultTableModel();
        /**�����*/
        model.addColumn("����");
        model.addColumn("��ϵ");
        model.addColumn("ȡֵ");
        jTableInfo.setModel(model);
        jTableInfo.setRowHeight(30);
        /**�ڶ���ʹ��jComboBox*/
        TableColumn relationColumn = jTableInfo.getColumnModel().getColumn(1);
        relationColumn.setCellEditor(new DefaultCellEditor(boxRelation));
        /**���û��ȫ����Ϣ���˳�*/
        if (text.equals(""))
        {
            return;
        }
        /**���ȫ����Ϣ�ַ���*/
        StringTokenizer token = new StringTokenizer(text,
                SysConfig.SEPARATORONE);
        Vector vecGlobal = new Vector();
        while (token.hasMoreTokens())
        {
            vecGlobal.addElement(token.nextElement());
        }
        /**���type��value*/
        for (int i = 0; i < vecGlobal.size(); i++)
        {
            String global = (String) vecGlobal.elementAt(i);
            token = new StringTokenizer(global, SysConfig.SEPARATORTWO);
            String type = (String) token.nextElement();
            String value = (String) token.nextElement();
            //�û��Զ�������
            if (type.equals(BlockGlobal.USER))
            {
                jTextGlobalInfo.setText(value);
            }
            //��׼����
            else
            {
                //���operator��value
                token = new StringTokenizer(value, SysConfig.SEPARATORTREE);
                String operator = (String) token.nextElement();
                String val = (String) token.nextElement();
                //��jTable������һ��
                Vector vecRow = new Vector();
                vecRow.addElement(type);
                vecRow.addElement(operator);
                vecRow.addElement(val);
                model.addRow(vecRow);
            }
        }
    }
}
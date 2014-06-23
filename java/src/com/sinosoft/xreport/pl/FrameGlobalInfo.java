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
    /**父窗口句柄*/
    private XReportMain frmParent;
    /**报表对象*/
    private Report report;
    /**与jTable关联的TableModel*/
    private DefaultTableModel model = new DefaultTableModel();
    /**代码表*/
    private Code code = new Code();
    /**关系组合框*/
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
        this.setTitle("全局信息");
        this.getContentPane().setLayout(null);
        jButtonAdd.setBounds(new Rectangle(372, 66, 63, 27));
        jButtonAdd.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonAdd.setText("添加");
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
        jButtonEdit.setText("编辑");
        jButtonEdit.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                editInfo();
            }
        });
        jButtonRemove.setBounds(new Rectangle(372, 163, 63, 27));
        jButtonRemove.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonRemove.setText("删除");
        jButtonRemove.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                removeInfo();
            }
        });
        jButtonOk.setBounds(new Rectangle(372, 250, 63, 27));
        jButtonOk.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonOk.setText("确定");
        jButtonOk.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                commit();
            }
        });
        jButtonCancel.setBounds(new Rectangle(372, 298, 63, 27));
        jButtonCancel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonCancel.setText("取消");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                cancel();
            }
        });
        jLabelGlobalInfo.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelGlobalInfo.setText("全局信息");
        jLabelGlobalInfo.setBounds(new Rectangle(32, 28, 110, 31));
        jLabelGlobalSql.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelGlobalSql.setText("全局信息");
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
        /**初始化jTable控件*/
        model = new DefaultTableModel();
        /**添加列*/
        model.addColumn("类型");
        model.addColumn("关系");
        model.addColumn("取值");
        jTableInfo.setModel(model);
        jTableInfo.setRowHeight(30);
        /**第二列使用jComboBox*/
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
        /**从jTable和jTextArea控件上收集全局信息*/
        int intRow = jTableInfo.getRowCount();
        String info = "", type = "", operator = "", value = "";
        /**收集规则的全局信息*/
        for (int i = 0; i < intRow; i++)
        {
            //读取类型 说明：类型暂时使用String型，以后可能该维Dim型
            type = (String) jTableInfo.getValueAt(i, 0);
            //读取操作符
            operator = (String) jTableInfo.getValueAt(i, 1);
            //读取取值
            value = (String) jTableInfo.getValueAt(i, 2) +
                    SysConfig.SEPARATORONE;
            //拼制全局信息字符串
            info = info + type + SysConfig.SEPARATORTWO +
                   operator + SysConfig.SEPARATORTREE + value;
        }
        /**收集用户自定义的全局信息*/
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
     * 打开窗口
     * @param text 初始化参数
     */
    void open(String text)
    {
        initData();
        initInterface(text);
        this.show();
    }

    /**
     * 初始化数据
     */
    private void initData()
    {

    }

    /**
     * 初始化界面
     * @param text 初始化参数
     */
    private void initInterface(String text)
    {
        /**初始化jTable控件*/
        model = new DefaultTableModel();
        /**添加列*/
        model.addColumn("类型");
        model.addColumn("关系");
        model.addColumn("取值");
        jTableInfo.setModel(model);
        jTableInfo.setRowHeight(30);
        /**第二列使用jComboBox*/
        TableColumn relationColumn = jTableInfo.getColumnModel().getColumn(1);
        relationColumn.setCellEditor(new DefaultCellEditor(boxRelation));
        /**如果没有全局信息，退出*/
        if (text.equals(""))
        {
            return;
        }
        /**拆分全局信息字符串*/
        StringTokenizer token = new StringTokenizer(text,
                SysConfig.SEPARATORONE);
        Vector vecGlobal = new Vector();
        while (token.hasMoreTokens())
        {
            vecGlobal.addElement(token.nextElement());
        }
        /**拆分type、value*/
        for (int i = 0; i < vecGlobal.size(); i++)
        {
            String global = (String) vecGlobal.elementAt(i);
            token = new StringTokenizer(global, SysConfig.SEPARATORTWO);
            String type = (String) token.nextElement();
            String value = (String) token.nextElement();
            //用户自定义类型
            if (type.equals(BlockGlobal.USER))
            {
                jTextGlobalInfo.setText(value);
            }
            //标准类型
            else
            {
                //拆分operator、value
                token = new StringTokenizer(value, SysConfig.SEPARATORTREE);
                String operator = (String) token.nextElement();
                String val = (String) token.nextElement();
                //在jTable中增加一行
                Vector vecRow = new Vector();
                vecRow.addElement(type);
                vecRow.addElement(operator);
                vecRow.addElement(val);
                model.addRow(vecRow);
            }
        }
    }
}
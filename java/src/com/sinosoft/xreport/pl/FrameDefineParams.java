package com.sinosoft.xreport.pl;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.sinosoft.xreport.bl.BlockParams;
import com.sinosoft.xreport.util.SysConfig;
import com.sinosoft.xreport.util.XTLogger;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author lixy
 * @version 1.0
 */

public class FrameDefineParams extends JDialog
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private XReportMain frmParent;
    private Report report;
    private DefaultTableModel model = new DefaultTableModel();

    private JLabel jLabelParams = new JLabel();
    private JScrollPane jScrollPaneParams = new JScrollPane();
    private JTable jTableParams = new JTable();
    private JButton jButtonAdd = new JButton();
    private JButton jButtonEdit = new JButton();
    private JButton jButtonRemove = new JButton();
    private JButton jButtonOk = new JButton();
    private JButton jButtonCancel = new JButton();

    public FrameDefineParams()
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

    public FrameDefineParams(XReportMain frmParent)
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
        jLabelParams.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelParams.setText("计算参数");
        jLabelParams.setBounds(new Rectangle(32, 20, 73, 30));
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setFont(new java.awt.Font("DialogInput", 0, 12));
        this.setModal(true);
        this.setResizable(false);
        this.getContentPane().setLayout(null);
        jScrollPaneParams.setFont(new java.awt.Font("DialogInput", 0, 12));
        jScrollPaneParams.setBounds(new Rectangle(31, 55, 293, 174));
        jTableParams.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonAdd.setBounds(new Rectangle(349, 56, 63, 27));
        jButtonAdd.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonAdd.setText("添加");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonAdd();
            }
        });
        jButtonEdit.setBounds(new Rectangle(349, 92, 63, 27));
        jButtonEdit.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonEdit.setText("编辑");
        jButtonEdit.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonEdit();
            }
        });
        jButtonRemove.setBounds(new Rectangle(349, 127, 63, 27));
        jButtonRemove.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonRemove.setText("删除");
        jButtonRemove.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonRemove();
            }
        });
        jButtonOk.setBounds(new Rectangle(349, 163, 63, 27));
        jButtonOk.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonOk.setText("确定");
        jButtonOk.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonOk();
            }
        });
        jButtonCancel.setBounds(new Rectangle(349, 198, 63, 27));
        jButtonCancel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonCancel.setText("取消");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonCancel();
            }
        });
        this.getContentPane().add(jLabelParams, null);
        this.getContentPane().add(jScrollPaneParams, null);
        this.getContentPane().add(jButtonEdit, null);
        this.getContentPane().add(jButtonRemove, null);
        this.getContentPane().add(jButtonOk, null);
        this.getContentPane().add(jButtonAdd, null);
        this.getContentPane().add(jButtonCancel, null);
        jScrollPaneParams.getViewport().add(jTableParams, null);
    }

    void buttonAdd()
    {
        Vector vecRow = new Vector();
        vecRow.addElement("");
        vecRow.addElement("");
        vecRow.addElement("");
        model.addRow(vecRow);
    }

    void buttonEdit()
    {

    }

    void buttonRemove()
    {
        int intRow = jTableParams.getSelectedRow();
        jTableParams.remove(intRow);
    }

    void buttonOk()
    {
        /**从jTable和jTextArea控件上收集全局信息*/
        int intRow = jTableParams.getRowCount();
        String params = "", name = "", tips = "", showMode = "";
        /**收集规则的全局信息*/
        for (int i = 0; i < intRow; i++)
        {
            //读取类型 说明：类型暂时使用String型，以后可能该维Dim型
            name = (String) jTableParams.getValueAt(i, 0);
            //读取操作符
            tips = (String) jTableParams.getValueAt(i, 1);
            //读取取值
            showMode = (String) jTableParams.getValueAt(i, 2);
            //拼制全局信息字符串
            params = params + "name" + SysConfig.SEPARATORTREE + name +
                     SysConfig.SEPARATORTWO +
                     "tips" + SysConfig.SEPARATORTREE + tips +
                     SysConfig.SEPARATORTWO +
                     "showMode" + SysConfig.SEPARATORTREE + showMode +
                     SysConfig.SEPARATORONE;
        }
//        report.setParams(params);
        XTLogger.getLogger(this.getClass()).info(params);
        this.dispose();
    }

    void buttonCancel()
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

    private void initData()
    {

    }

    private void initInterface(String text)
    {
        /**初始化jTable控件*/
        model = new DefaultTableModel();
        /**添加列*/
        model.addColumn("name");
        model.addColumn("tips");
        model.addColumn("showMode");
        jTableParams.setModel(model);
        jTableParams.setRowHeight(30);
        /**如果没有全局信息，退出*/
        if (text.equals(""))
        {
            return;
        }
        Vector vecParams = BlockParams.getParams(text);
        Vector vecRow = new Vector();
        for (int i = 0; i < vecParams.size(); i++)
        {
            Map map = (Map) vecParams.elementAt(i);
            vecRow.addElement(map.get("name"));
            vecRow.addElement(map.get("tips"));
            vecRow.addElement(map.get("showMode"));
            model.addRow(vecRow);
        }
    }

    public static void main(String[] args)
    {
        FrameDefineParams frmParams = new FrameDefineParams();
        frmParams.setSize(440, 300);
        frmParams.open("");
    }
}
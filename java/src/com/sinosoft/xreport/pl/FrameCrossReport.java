package com.sinosoft.xreport.pl;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.sinosoft.xreport.bl.Code;
import com.sinosoft.xreport.util.SysConfig;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author lixy
 * @version 1.0
 */

public class FrameCrossReport extends JDialog
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

    private JLabel jLabelCell = new JLabel();
    private JLabel jLabelName = new JLabel();
    private JLabel jLabelLocation = new JLabel();
    private JLabel jLabelContext = new JLabel();
    private JLabel jLabelCondition = new JLabel();
    private JLabel jLabelFormula = new JLabel();
    private JTextField jTextName = new JTextField();
    private JTextField jTextContext = new JTextField();
    private JTextField jTextCondition = new JTextField();
    private JTextField jTextFormula = new JTextField();
    private JTextField jTextLocation = new JTextField();
    private JButton jButtonCondition = new JButton();
    private JButton jButtonFormula = new JButton();
    private JButton jButtonOk = new JButton();
    private JButton jButtonCancel = new JButton();

    /**
     * 构造函数
     */
    public FrameCrossReport()
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
    public FrameCrossReport(XReportMain frmParent)
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
        jLabelCell.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelCell.setText("单元格属性");
        jLabelCell.setBounds(new Rectangle(36, 25, 72, 31));
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setFont(new java.awt.Font("DialogInput", 0, 12));
        this.setModal(true);
        this.setResizable(false);
        this.setTitle("单元格定义");
        this.getContentPane().setLayout(null);
        jLabelName.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelName.setText("命名");
        jLabelName.setBounds(new Rectangle(36, 110, 38, 31));
        jLabelLocation.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelLocation.setText("位置");
        jLabelLocation.setBounds(new Rectangle(36, 64, 38, 31));
        jLabelContext.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelContext.setText("内容");
        jLabelContext.setBounds(new Rectangle(36, 153, 38, 31));
        jLabelCondition.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelCondition.setText("条件");
        jLabelCondition.setBounds(new Rectangle(36, 196, 38, 31));
        jLabelFormula.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelFormula.setText("公式");
        jLabelFormula.setBounds(new Rectangle(36, 240, 38, 31));
        jTextName.setBounds(new Rectangle(83, 108, 206, 27));
        jTextContext.setBounds(new Rectangle(83, 152, 206, 27));
        jTextCondition.setBounds(new Rectangle(83, 196, 206, 27));
        jTextFormula.setBounds(new Rectangle(83, 240, 206, 27));
        jTextLocation.setBounds(new Rectangle(83, 64, 206, 27));
        jButtonCondition.setBounds(new Rectangle(291, 195, 35, 31));
        jButtonCondition.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonCondition.setText("...");
        jButtonCondition.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                addCondition();
            }
        });
        jButtonFormula.setBounds(new Rectangle(290, 238, 37, 31));
        jButtonFormula.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonFormula.setText("...");
        jButtonFormula.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                addFormula();
            }
        });
        jButtonOk.setBounds(new Rectangle(93, 289, 71, 28));
        jButtonOk.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonOk.setText("确定");
        jButtonOk.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                commit();
            }
        });
        jButtonCancel.setBounds(new Rectangle(211, 289, 71, 28));
        jButtonCancel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonCancel.setText("取消");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                cancel();
            }
        });
        this.getContentPane().add(jLabelCell, null);
        this.getContentPane().add(jLabelContext, null);
        this.getContentPane().add(jLabelCondition, null);
        this.getContentPane().add(jLabelFormula, null);
        this.getContentPane().add(jLabelLocation, null);
        this.getContentPane().add(jLabelName, null);
        this.getContentPane().add(jTextName, null);
        this.getContentPane().add(jTextContext, null);
        this.getContentPane().add(jTextCondition, null);
        this.getContentPane().add(jTextFormula, null);
        this.getContentPane().add(jTextLocation, null);
        this.getContentPane().add(jButtonCondition, null);
        this.getContentPane().add(jButtonFormula, null);
        this.getContentPane().add(jButtonOk, null);
        this.getContentPane().add(jButtonCancel, null);
    }

    /**
     * 打开窗口
     * @param text 初始化参数
     */
    public void open(String text)
    {
        initData();
        initInterface(text);
        this.show();
    }

    public static void main(String[] args)
    {
        FrameCrossReport frm = new FrameCrossReport();
        frm.setSize(360, 380);
        frm.show();
    }

    private void cancel()
    {
        this.dispose();
    }

    /**
     * 提交定义内容
     */
    private void commit()
    {
        String text, name, condition, formula, context, location;
        if ((name = jTextName.getText()).equals(""))
        {
            name = "null";
        }
        if ((condition = jTextCondition.getText()).equals(""))
        {
            condition = "null";
        }
        if ((formula = jTextFormula.getText()).equals(""))
        {
            formula = "null";
        }
        if ((context = jTextContext.getText()).equals(""))
        {
            context = "null";
        }
        if ((location = jTextLocation.getText()).equals(""))
        {
            location = "null";
        }
        text = name + SysConfig.SEPARATORTWO +
               condition + SysConfig.SEPARATORTWO +
               formula + SysConfig.SEPARATORTWO +
               location + SysConfig.SEPARATORTWO +
               context;
        report.setText(text);
        this.dispose();
    }

    /**
     * 添加条件
     */
    private void addCondition()
    {

    }

    /**
     * 添加公式
     */
    private void addFormula()
    {

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
        /**该单元格没有定义，只设置位置文本框*/
        if (text.indexOf(SysConfig.SEPARATORTWO) == -1)
        {
            jTextLocation.setText(text);
        }
        /**该单元格有定义，拆分定义字符串*/
        else
        {
            StringTokenizer token = new StringTokenizer(text,
                    SysConfig.SEPARATORTWO);
            //假设字符串是按照name^condition^formula^location^context的格式保存
            jTextName.setText((String) token.nextElement());
            jTextCondition.setText((String) token.nextElement());
            jTextFormula.setText((String) token.nextElement());
            jTextLocation.setText((String) token.nextElement());
            jTextContext.setText((String) token.nextElement());
        }
    }
}
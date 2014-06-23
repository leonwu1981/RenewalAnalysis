package com.sinosoft.xreport.pl;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import com.f1j.ss.CellFormat;
import com.f1j.swing.JBook;


/****************************************************
 * Description:    调整单元格对齐方式
 * Author:         李旭英
 * CreatDate:      2002-3-11
 * UpdateLog:      Name      Date     Reason/Contents
 *
 ****************************************************/
public class FrameCellAlign extends JDialog
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //可视化编程生成代码开始
    ButtonGroup buttonGroupAlign = new ButtonGroup();
    JButton jOK = new JButton();
    JButton jCancel = new JButton();
    JPanel jPanel1 = new JPanel();
    TitledBorder titledBorder1;
    JRadioButton jRadioAlignLeft = new JRadioButton();
    JRadioButton jRadioAlignRight = new JRadioButton();
    JRadioButton jRadioCrossCenter = new JRadioButton();
    JRadioButton jRadioAlignCenter = new JRadioButton();
    //可视化编程生成代码结束
    /**该窗口的上一级窗口句柄*/
    JBook jReport = null;

    /**
     *
     * 构造函数
     *
     * */
    public FrameCellAlign()
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
     *
     * 构造函数
     * @param jReport 与报表关联的控件
     *
     * */
    public FrameCellAlign(JBook jReport)
    {
        this.jReport = jReport;
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
        //可视化编程生成代码开始
        titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(Color.
                white, new Color(148, 145, 140)), "对齐方式");
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setFont(new java.awt.Font("DialogInput", 0, 12));
        this.setModal(true);
        this.setTitle("对齐方式");
        this.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowOpened(WindowEvent e)
            {
                open();
            }
        });
        this.getContentPane().setLayout(null);
        jOK.setFont(new java.awt.Font("DialogInput", 0, 12));
        jOK.setText("确定");
        jOK.setBounds(new Rectangle(46, 192, 60, 29));
        jOK.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jOK_actionPerformed(e);
            }
        });
        jCancel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jCancel.setText("取消");
        jCancel.setBounds(new Rectangle(126, 192, 60, 29));
        jCancel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jCancel_actionPerformed(e);
            }
        });
        jPanel1.setFont(new java.awt.Font("DialogInput", 0, 12));
        jPanel1.setBorder(titledBorder1);
        jPanel1.setBounds(new Rectangle(53, 28, 129, 153));
        jPanel1.setLayout(null);
        jRadioAlignLeft.setFont(new java.awt.Font("DialogInput", 0, 12));
        jRadioAlignLeft.setText("左对齐");
        jRadioAlignLeft.setBounds(new Rectangle(20, 22, 101, 28));
        jRadioAlignRight.setFont(new java.awt.Font("DialogInput", 0, 12));
        jRadioAlignRight.setText("右对齐");
        jRadioAlignRight.setBounds(new Rectangle(20, 49, 101, 28));
        jRadioCrossCenter.setFont(new java.awt.Font("DialogInput", 0, 12));
        jRadioCrossCenter.setText("跨列居中");
        jRadioCrossCenter.setBounds(new Rectangle(20, 103, 101, 28));
        jRadioAlignCenter.setFont(new java.awt.Font("DialogInput", 0, 12));
        jRadioAlignCenter.setText("居中");
        jRadioAlignCenter.setBounds(new Rectangle(20, 76, 101, 28));
        this.getContentPane().add(jOK, null);
        this.getContentPane().add(jCancel, null);
        this.getContentPane().add(jPanel1, null);
        jPanel1.add(jRadioAlignLeft, null);
        jPanel1.add(jRadioAlignCenter, null);
        jPanel1.add(jRadioCrossCenter, null);
        jPanel1.add(jRadioAlignRight, null);
        buttonGroupAlign.add(jRadioAlignLeft);
        buttonGroupAlign.add(jRadioAlignRight);
        buttonGroupAlign.add(jRadioAlignCenter);
        buttonGroupAlign.add(jRadioCrossCenter);
        //可视化编程生成代码结束
    }

    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        FrameCellAlign setAlign = new FrameCellAlign();
        setAlign.setSize(250, 300);
        setAlign.show();
    }

    /**
     *
     * 处理OK按钮单击事件
     * @param e 鼠标事件句柄
     *
     * */
    void jOK_actionPerformed(ActionEvent e)
    {
        try
        {
//            jReport.setSheet(2*id);
            //读取选定单元格的原有对齐方式
            CellFormat cellFormat = jReport.getCellFormat();
            //根据单选框的情况设置新的对齐方式
            if (jRadioAlignLeft.isSelected())
            {
                //左对齐
                cellFormat.setHorizontalAlignment((short) 1);
            }
            if (jRadioAlignCenter.isSelected())
            {
                //右对齐
                cellFormat.setHorizontalAlignment((short) 2);
            }
            if (jRadioAlignRight.isSelected())
            {
                //居中
                cellFormat.setHorizontalAlignment((short) 3);
            }
            if (jRadioCrossCenter.isSelected())
            {
                //跨列居中
                cellFormat.setHorizontalAlignment((short) 6);
            }
            jReport.setCellFormat(cellFormat);
        }
        catch (Exception eSetAlign)
        {
            eSetAlign.printStackTrace();
        }
        //关闭窗口
        this.dispose();
    }

    /**
     *
     * 处理Cancel按钮单击事件
     * @param e 单击事件句柄
     *
     * */
    void jCancel_actionPerformed(ActionEvent e)
    {
        //关闭窗口
        this.dispose();
    }

    void open()
    {
        //对齐方式标志
        short align;
        try
        {
//            jReport.setSheet(2*id);
            //读取选定单元格的格式
            CellFormat cellFormat = jReport.getCellFormat();
            //读取选定单元格原有的对齐方式
            align = cellFormat.getHorizontalAlignment();
            //利用当前对齐方式初始化对齐方式对话框
            if (align == (short) 1 || align == (short) 0)
            {
                this.jRadioAlignLeft.setSelected(true);
            }
            if (align == (short) 2)
            {
                this.jRadioAlignCenter.setSelected(true);
            }
            if (align == (short) 3)
            {
                this.jRadioAlignRight.setSelected(true);
            }
            if (align == (short) 6)
            {
                this.jRadioCrossCenter.setSelected(true);
            }
        }
        catch (Exception eOpen)
        {
            eOpen.printStackTrace();
        }
        this.show();
    }
}

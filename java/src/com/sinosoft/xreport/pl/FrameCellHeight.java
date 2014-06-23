package com.sinosoft.xreport.pl;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.f1j.swing.JBook;

/****************************************************
 * Description:    调整单元格行高列宽
 * Author:             李旭英
 * CreatDate:        2002-3-11
 * UpdateLog:        Name            Date            Reason/Contents
 *
 ****************************************************/
public class FrameCellHeight extends JDialog
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //可视化编程生成代码开始
    JLabel jRowHeight = new JLabel();
    JLabel ColWidth = new JLabel();
    JTextField jTextRowHeigh = new JTextField();
    JTextField jTextColWidth = new JTextField();
    JButton jOK = new JButton();
    JButton jCancel = new JButton();
    //可视化编程生成代码结束
    /**该窗口的上一级窗口句柄*/
    JBook jReport = null;

    /**构造函数
     *
     * */
    public FrameCellHeight()
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

    /**构造函数
     *
     * @param  jReport  与报表关联的控件
     *
     * */
    public FrameCellHeight(JBook jReport)
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
        jRowHeight.setFont(new java.awt.Font("DialogInput", 0, 12));
        jRowHeight.setText("行高");
        jRowHeight.setBounds(new Rectangle(40, 48, 47, 26));
        this.getContentPane().setLayout(null);
        ColWidth.setFont(new java.awt.Font("DialogInput", 0, 12));
        ColWidth.setText("列宽");
        ColWidth.setBounds(new Rectangle(40, 100, 47, 26));
        jTextColWidth.setFont(new java.awt.Font("DialogInput", 0, 12));
        jTextColWidth.setToolTipText("");
        jTextColWidth.setBounds(new Rectangle(86, 100, 85, 25));
        jOK.setFont(new java.awt.Font("DialogInput", 0, 12));
        jOK.setText("确定");
        jOK.setBounds(new Rectangle(39, 160, 60, 28));
        jOK.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                jOK_mouseClicked(e);
            }
        });
        jTextRowHeigh.setFont(new java.awt.Font("DialogInput", 0, 12));
        jTextRowHeigh.setBounds(new Rectangle(86, 48, 85, 25));
        jCancel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jCancel.setText("取消");
        jCancel.setBounds(new Rectangle(121, 160, 60, 28));
        jCancel.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                jCancel_mouseClicked(e);
            }
        });
        this.setTitle("设置行高列宽");
        this.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowOpened(WindowEvent e)
            {
                open();
            }
        });
        this.getContentPane().add(jRowHeight, null);
        this.getContentPane().add(ColWidth, null);
        this.getContentPane().add(jTextRowHeigh, null);
        this.getContentPane().add(jTextColWidth, null);
        this.getContentPane().add(jOK, null);
        this.getContentPane().add(jCancel, null);
        //可视化编程生成代码结束
    }


    public static void main(String[] args)
    {
        FrameCellHeight heightwidth = new FrameCellHeight();
        heightwidth.setSize(230, 270);
        heightwidth.show();
    }

    /**
     *
     * 处理OK按钮的单击事件
     * @param e 鼠标事件句柄
     *
     * */
    void jOK_mouseClicked(MouseEvent e)
    {
        //选定区域的范围
        int startRow, startCol, endRow, endCol;
        //行高列宽的值
        int height, width;
        //行号列号标志
        int i, j;
        //读取设定的行高列宽值
        height = Integer.parseInt(jTextRowHeigh.getText().trim());
        width = Integer.parseInt(jTextColWidth.getText().trim());
        try
        {
//            jReport.setSheet(2*id);
            //读取当前的选定范围
            startRow = jReport.getSelStartRow();
            endRow = jReport.getSelEndRow();
            startCol = jReport.getSelStartCol();
            endCol = jReport.getSelEndCol();
            //设置行高列宽
            for (i = startRow; i <= endRow; i++)
            {
                jReport.setRowHeight(i, height);
            }
            for (j = startCol; j <= endCol; j++)
            {
                jReport.setColWidth(j, width);
            }
        }
        catch (Exception eHeight)
        {
            eHeight.printStackTrace();
        }
        //关闭窗口
        this.dispose();
    }

    /**
     *
     * 处理Cancel按钮的单击事件
     * @param e 鼠标事件句柄
     *
     * */
    void jCancel_mouseClicked(MouseEvent e)
    {
        //关闭窗口
        this.dispose();
    }

    /**
     *
     * 处理窗口打开事件
     *
     * */
    void open()
    {
        //选定范围
        int startRow, endRow, startCol, endCol;
        //行标列标
        int i, j;
        //行高列宽值
        int height, width;
        try
        {
            //首先读取被选定单元格的行高和列宽
            startRow = jReport.getSelStartRow();
            height = jReport.getRowHeight(startRow);
            startCol = jReport.getSelStartCol();
            width = jReport.getColWidth(startCol);
            //将行高列宽的信息显示在弹出的对话框中
            this.jTextRowHeigh.setText("" + height);
            this.jTextColWidth.setText("" + width);
        }
        catch (Exception eOpen)
        {
            eOpen.printStackTrace();
        }
        this.show();
    }
}

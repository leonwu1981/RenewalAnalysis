/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.report.f1report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import com.f1j.ss.GRObject;
import com.f1j.swing.JBook;
import com.f1j.swing.ss.PageSetupDlg;
import com.f1j.util.F1Exception;
import netscape.javascript.JSObject;


public class AppletControl extends JApplet
{
    private static final long serialVersionUID = 1L;
    public JBook book;

    boolean isStandalone = false;
    String var0;
    String var1;
    JToolBar jToolBar1 = new JToolBar();
    JButton jButton2 = new JButton();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JButton jButton3 = new JButton();
    JButton jButton4 = new JButton();
    JButton jButton5 = new JButton();
    JLabel jLabel3 = new JLabel();
    JLabel jLabel4 = new JLabel();
    //Get a parameter value
    public String getParameter(String key, String def)
    {
        return isStandalone ? System.getProperty(key, def) :
                (getParameter(key) != null ? getParameter(key) : def);
    }

    //Construct the applet
    public AppletControl()
    {
    }

    //Initialize the applet
    public void init()
    {
        try
        {
            var0 = this.getParameter("ReportURL", "");
            var1 = this.getParameter("IsNeedExcel", "");
            book = new JBook();
            book.readURL(var0);
            book.setAllowDesigner(false);
            book.setAllowEditHeaders(false);
            book.setShowEditBar(false);
            book.setAllowInCellEditing(false);
            book.setAllowDelete(false);
            book.setAllowSelections(false);
            book.setAllowResize(false);
            book.setPrintGridLines(false);
            GRObject gr = null;
            book.setSelection(gr);
            getContentPane().setLayout(new BorderLayout(0, 0));
            getContentPane().add(BorderLayout.CENTER, book);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            jbInit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //Component initialization
    private void jbInit()
            throws Exception
    {
        jButton2.setBackground(Color.white);
        jButton2.setFont(new java.awt.Font("新宋体", Font.PLAIN, 12));
        jButton2.setAlignmentX((float) 1.0);
        jButton2.setText("   设置   ");
        jButton2.addActionListener(new Applet1_jButton2_actionAdapter(this));
        jLabel1.setText("      ");
        jLabel2.setText("      ");
        jButton3.setBackground(Color.white);
        jButton3.setFont(new java.awt.Font("新宋体", Font.PLAIN, 12));
        jButton3.setText("   打印   ");
        jButton3.addActionListener(new Applet1_jButton3_actionAdapter(this));
        jButton4.setBackground(Color.white);
        jButton4.setFont(new java.awt.Font("新宋体", Font.PLAIN, 12));
        jButton4.setText("   关闭   ");
        jButton4.addActionListener(new Applet1_jButton4_actionAdapter(this));
        jButton5.setBackground(Color.white);
        jButton5.setFont(new java.awt.Font("新宋体", Font.PLAIN, 12));
        jButton5.setText("   下载   ");
        jButton5.addActionListener(new Applet1_jButton5_actionAdapter(this));
        jLabel3.setText("      ");
        jLabel4.setText("      ");
        jToolBar1.setBackground(Color.white);
        jToolBar1.setForeground(Color.white);
        this.setFont(new java.awt.Font("新宋体", Font.PLAIN, 10));
        jToolBar1.add(jButton3);
        jToolBar1.add(jLabel3);
        jToolBar1.add(jButton5);
        jToolBar1.add(jLabel2);
        jToolBar1.add(jButton2);
        jToolBar1.add(jLabel1);
        jToolBar1.add(jButton4);
        this.getContentPane().add(jToolBar1, java.awt.BorderLayout.NORTH);

    }

    //Get Applet information
    public String getAppletInfo()
    {
        return "Applet Information";
    }

    //Get parameter info
    public String[][] getParameterInfo()
    {
        java.lang.String[][] pinfo =
                {
                {"ReportURL", "String", ""},
        };
        return pinfo;
    }

    //响应打印按钮
    public void jButton3_actionPerformed(ActionEvent e)
    {
        try
        {
            book.filePrint(true);
        }
        catch (Exception ex)
        {
        }
    }

    //响应设置按钮
    public void jButton2_actionPerformed(ActionEvent e)
    {
        try
        {
            PageSetupDlg pDlg = new PageSetupDlg(book);
            pDlg.show();
        }
        catch (F1Exception ex)
        {
        }
    }

    //下载
    public void jButton5_actionPerformed(ActionEvent e)
    {
        try
        {
            JSObject.getWindow(this).eval("javascript:fnDownLoad()");
        }
        catch (Exception ex)
        {
        }
//        try
//        {
//            if (this.var1.equalsIgnoreCase("true"))
//            {
//                String strExcelURL = var0.substring(0, var0.length() - 4) +
//                                     ".xls";
//                this.getAppletContext().showDocument(new java.net.URL(
//                        strExcelURL),
//                        "_blank");
//            }
//        }
//        catch (MalformedURLException ex)
//        {
//        }
    }

    //关闭窗口
    public void jButton4_actionPerformed(ActionEvent e)
    {
        JSObject.getWindow(this).eval("javascript:window.close()");
    }
}


class Applet1_jButton4_actionAdapter implements ActionListener
{
    private AppletControl adaptee;
    Applet1_jButton4_actionAdapter(AppletControl adaptee)
    {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e)
    {
        adaptee.jButton4_actionPerformed(e);
    }
}


class Applet1_jButton5_actionAdapter implements ActionListener
{
    private AppletControl adaptee;
    Applet1_jButton5_actionAdapter(AppletControl adaptee)
    {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e)
    {
        adaptee.jButton5_actionPerformed(e);
    }
}


class Applet1_jButton2_actionAdapter implements ActionListener
{
    private AppletControl adaptee;
    Applet1_jButton2_actionAdapter(AppletControl adaptee)
    {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e)
    {
        adaptee.jButton2_actionPerformed(e);
    }
}


class Applet1_jButton3_actionAdapter implements ActionListener
{
    private AppletControl adaptee;
    Applet1_jButton3_actionAdapter(AppletControl adaptee)
    {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e)
    {
        adaptee.jButton3_actionPerformed(e);
    }
}

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
 * Description:    ������Ԫ����뷽ʽ
 * Author:         ����Ӣ
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
    //���ӻ�������ɴ��뿪ʼ
    ButtonGroup buttonGroupAlign = new ButtonGroup();
    JButton jOK = new JButton();
    JButton jCancel = new JButton();
    JPanel jPanel1 = new JPanel();
    TitledBorder titledBorder1;
    JRadioButton jRadioAlignLeft = new JRadioButton();
    JRadioButton jRadioAlignRight = new JRadioButton();
    JRadioButton jRadioCrossCenter = new JRadioButton();
    JRadioButton jRadioAlignCenter = new JRadioButton();
    //���ӻ�������ɴ������
    /**�ô��ڵ���һ�����ھ��*/
    JBook jReport = null;

    /**
     *
     * ���캯��
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
     * ���캯��
     * @param jReport �뱨������Ŀؼ�
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
        //���ӻ�������ɴ��뿪ʼ
        titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(Color.
                white, new Color(148, 145, 140)), "���뷽ʽ");
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setFont(new java.awt.Font("DialogInput", 0, 12));
        this.setModal(true);
        this.setTitle("���뷽ʽ");
        this.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowOpened(WindowEvent e)
            {
                open();
            }
        });
        this.getContentPane().setLayout(null);
        jOK.setFont(new java.awt.Font("DialogInput", 0, 12));
        jOK.setText("ȷ��");
        jOK.setBounds(new Rectangle(46, 192, 60, 29));
        jOK.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jOK_actionPerformed(e);
            }
        });
        jCancel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jCancel.setText("ȡ��");
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
        jRadioAlignLeft.setText("�����");
        jRadioAlignLeft.setBounds(new Rectangle(20, 22, 101, 28));
        jRadioAlignRight.setFont(new java.awt.Font("DialogInput", 0, 12));
        jRadioAlignRight.setText("�Ҷ���");
        jRadioAlignRight.setBounds(new Rectangle(20, 49, 101, 28));
        jRadioCrossCenter.setFont(new java.awt.Font("DialogInput", 0, 12));
        jRadioCrossCenter.setText("���о���");
        jRadioCrossCenter.setBounds(new Rectangle(20, 103, 101, 28));
        jRadioAlignCenter.setFont(new java.awt.Font("DialogInput", 0, 12));
        jRadioAlignCenter.setText("����");
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
        //���ӻ�������ɴ������
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
     * ����OK��ť�����¼�
     * @param e ����¼����
     *
     * */
    void jOK_actionPerformed(ActionEvent e)
    {
        try
        {
//            jReport.setSheet(2*id);
            //��ȡѡ����Ԫ���ԭ�ж��뷽ʽ
            CellFormat cellFormat = jReport.getCellFormat();
            //���ݵ�ѡ�����������µĶ��뷽ʽ
            if (jRadioAlignLeft.isSelected())
            {
                //�����
                cellFormat.setHorizontalAlignment((short) 1);
            }
            if (jRadioAlignCenter.isSelected())
            {
                //�Ҷ���
                cellFormat.setHorizontalAlignment((short) 2);
            }
            if (jRadioAlignRight.isSelected())
            {
                //����
                cellFormat.setHorizontalAlignment((short) 3);
            }
            if (jRadioCrossCenter.isSelected())
            {
                //���о���
                cellFormat.setHorizontalAlignment((short) 6);
            }
            jReport.setCellFormat(cellFormat);
        }
        catch (Exception eSetAlign)
        {
            eSetAlign.printStackTrace();
        }
        //�رմ���
        this.dispose();
    }

    /**
     *
     * ����Cancel��ť�����¼�
     * @param e �����¼����
     *
     * */
    void jCancel_actionPerformed(ActionEvent e)
    {
        //�رմ���
        this.dispose();
    }

    void open()
    {
        //���뷽ʽ��־
        short align;
        try
        {
//            jReport.setSheet(2*id);
            //��ȡѡ����Ԫ��ĸ�ʽ
            CellFormat cellFormat = jReport.getCellFormat();
            //��ȡѡ����Ԫ��ԭ�еĶ��뷽ʽ
            align = cellFormat.getHorizontalAlignment();
            //���õ�ǰ���뷽ʽ��ʼ�����뷽ʽ�Ի���
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

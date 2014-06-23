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
 * Description:    ������Ԫ���и��п�
 * Author:             ����Ӣ
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
    //���ӻ�������ɴ��뿪ʼ
    JLabel jRowHeight = new JLabel();
    JLabel ColWidth = new JLabel();
    JTextField jTextRowHeigh = new JTextField();
    JTextField jTextColWidth = new JTextField();
    JButton jOK = new JButton();
    JButton jCancel = new JButton();
    //���ӻ�������ɴ������
    /**�ô��ڵ���һ�����ھ��*/
    JBook jReport = null;

    /**���캯��
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

    /**���캯��
     *
     * @param  jReport  �뱨������Ŀؼ�
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
        //���ӻ�������ɴ��뿪ʼ
        jRowHeight.setFont(new java.awt.Font("DialogInput", 0, 12));
        jRowHeight.setText("�и�");
        jRowHeight.setBounds(new Rectangle(40, 48, 47, 26));
        this.getContentPane().setLayout(null);
        ColWidth.setFont(new java.awt.Font("DialogInput", 0, 12));
        ColWidth.setText("�п�");
        ColWidth.setBounds(new Rectangle(40, 100, 47, 26));
        jTextColWidth.setFont(new java.awt.Font("DialogInput", 0, 12));
        jTextColWidth.setToolTipText("");
        jTextColWidth.setBounds(new Rectangle(86, 100, 85, 25));
        jOK.setFont(new java.awt.Font("DialogInput", 0, 12));
        jOK.setText("ȷ��");
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
        jCancel.setText("ȡ��");
        jCancel.setBounds(new Rectangle(121, 160, 60, 28));
        jCancel.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                jCancel_mouseClicked(e);
            }
        });
        this.setTitle("�����и��п�");
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
        //���ӻ�������ɴ������
    }


    public static void main(String[] args)
    {
        FrameCellHeight heightwidth = new FrameCellHeight();
        heightwidth.setSize(230, 270);
        heightwidth.show();
    }

    /**
     *
     * ����OK��ť�ĵ����¼�
     * @param e ����¼����
     *
     * */
    void jOK_mouseClicked(MouseEvent e)
    {
        //ѡ������ķ�Χ
        int startRow, startCol, endRow, endCol;
        //�и��п��ֵ
        int height, width;
        //�к��кű�־
        int i, j;
        //��ȡ�趨���и��п�ֵ
        height = Integer.parseInt(jTextRowHeigh.getText().trim());
        width = Integer.parseInt(jTextColWidth.getText().trim());
        try
        {
//            jReport.setSheet(2*id);
            //��ȡ��ǰ��ѡ����Χ
            startRow = jReport.getSelStartRow();
            endRow = jReport.getSelEndRow();
            startCol = jReport.getSelStartCol();
            endCol = jReport.getSelEndCol();
            //�����и��п�
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
        //�رմ���
        this.dispose();
    }

    /**
     *
     * ����Cancel��ť�ĵ����¼�
     * @param e ����¼����
     *
     * */
    void jCancel_mouseClicked(MouseEvent e)
    {
        //�رմ���
        this.dispose();
    }

    /**
     *
     * �����ڴ��¼�
     *
     * */
    void open()
    {
        //ѡ����Χ
        int startRow, endRow, startCol, endCol;
        //�б��б�
        int i, j;
        //�и��п�ֵ
        int height, width;
        try
        {
            //���ȶ�ȡ��ѡ����Ԫ����иߺ��п�
            startRow = jReport.getSelStartRow();
            height = jReport.getRowHeight(startRow);
            startCol = jReport.getSelStartCol();
            width = jReport.getColWidth(startCol);
            //���и��п����Ϣ��ʾ�ڵ����ĶԻ�����
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

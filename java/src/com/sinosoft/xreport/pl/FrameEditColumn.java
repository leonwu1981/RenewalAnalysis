package com.sinosoft.xreport.pl;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;

/**
 * <p>Title: test</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft</p>
 * @author unascribed
 * @version 1.0
 */

/***
 * ˵����(1)�������ڵ�HashMap�������ص�HashMap
 *      (2)�������ڵ���ʾ�ֶ������������ص���ʾ�ֶ�����
 *      (3)��ʼ������
 *      (4)ȷ��ʱ�����ص�HashMap��Ϣ�ύ�������ڵ�HashMap��
 *      (5)ѡ�в�ͬ����ʾ�ֶ�ʱ���ֶζ�Ӧ����Ϣ�浽���ص�HashMap�У���ˢ�½���
 */

public class FrameEditColumn extends JDialog
{
//    /**������*/
//    FramePlaneReport frmParent=null;
//    /**�����*/
//    Code code=new Code();
//    /***/
//    Vector vecFields=new Vector();
//    /**�б����һ��ѡ����*/
//    String strPreSelected="";
//    /**����*/
//    HashMap mapSubject=new HashMap();
//    /**������ʾ*/
//    HashMap mapDisplay=new HashMap();
//    /**Լ������*/
//    HashMap mapRestrict=new HashMap();
//    /**�滻��*/
//    HashMap mapReplace=new HashMap();
//    /**���ܹ���*/
//    HashMap mapSumRule=new HashMap();
//    /**��������*/
//    HashMap mapSumDepend=new HashMap();
//    /**����*/
//    HashMap mapWhere=new HashMap();
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private JPanel jPanelColumn = new JPanel();
    private JScrollPane jScrollPaneColumn = new JScrollPane();
    private Border border1;
    private TitledBorder titledBorder1;
    private JList jListColumn = new JList();
    private JTabbedPane jTabbedPaneAtt = new JTabbedPane();
    private JPanel jPanelGerenral = new JPanel();
    private JPanel jPanelReplace = new JPanel();
    private JPanel jPanelSum = new JPanel();
    private JPanel jPanelWhere = new JPanel();
    private JPanel jPanelButton = new JPanel();
    private JButton jButtonOK = new JButton();
    private JButton jButtonCancel = new JButton();
    JLabel jLabelDisplay = new JLabel();
    JTextField jTextDisplay = new JTextField();
    JLabel jLabelRestrict = new JLabel();
    JComboBox jComboRestrict = new JComboBox();
    JLabel jLabelReplace = new JLabel();
    JPanel jPanel2 = new JPanel();
    Border border2;
    TitledBorder titledBorder2;
    JLabel jLabelSumRule = new JLabel();
    JCheckBox jCheckSum1 = new JCheckBox();
    JCheckBox jCheckSum2 = new JCheckBox();
    JLabel jLabelSumDep = new JLabel();
    JScrollPane jScrollPaneSumDep = new JScrollPane();
    JList jListSum = new JList();
    JTextField jTextColumn = new JTextField();
    JTextField jTextValue = new JTextField();
    JComboBox jComboRelation = new JComboBox();
    JButton jButtonSql = new JButton();
    JScrollPane jScrollPane1 = new JScrollPane();
    JTextArea jTextAreaSql = new JTextArea();
    JButton jButtonAdd1 = new JButton();
    JButton jButtonAdd2 = new JButton();
    JLabel jLabelColumn1 = new JLabel();
    JLabel jLabelColumn2 = new JLabel();
    JLabel jLabelRelation = new JLabel();
    JLabel jLabelSql = new JLabel();
    JTextField jTextReplace = new JTextField();
    private JButton jButtonAdd3 = new JButton();

    public FrameEditColumn()
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

    public FrameEditColumn(FramePlaneReport frmParent)
    {
//        super(frmParent);
//        this.frmParent=frmParent;
//        try
//        {
//            jbInit();
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
    }

    private void jbInit() throws Exception
    {
        border1 = BorderFactory.createEmptyBorder();
        titledBorder1 = new TitledBorder(border1, "��ʾ�ֶ�");
        border2 = BorderFactory.createEmptyBorder();
        titledBorder2 = new TitledBorder(BorderFactory.createEmptyBorder(), "");
        this.getContentPane().setLayout(null);
        jPanelColumn.setFont(new java.awt.Font("DialogInput", 0, 12));
        jPanelColumn.setBorder(titledBorder1);
        jPanelColumn.setBounds(new Rectangle(19, 37, 225, 306));
        jPanelColumn.setLayout(null);
        jScrollPaneColumn.setBounds(new Rectangle(5, 24, 194, 287));
        jTabbedPaneAtt.setFont(new java.awt.Font("DialogInput", 0, 12));
        jTabbedPaneAtt.setBounds(new Rectangle(245, 50, 348, 294));
        jPanelGerenral.setFont(new java.awt.Font("DialogInput", 0, 12));
        jPanelGerenral.setLayout(null);
        jPanelButton.setFont(new java.awt.Font("DialogInput", 0, 12));
        jPanelButton.setBounds(new Rectangle(342, 349, 251, 43));
        jPanelButton.setLayout(null);
        jButtonOK.setBounds(new Rectangle(44, 8, 80, 32));
        jButtonOK.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonOK.setText("ȷ��");
        jButtonOK.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButtonOK_actionPerformed(e);
            }
        });
        jButtonCancel.setBounds(new Rectangle(142, 8, 80, 32));
        jButtonCancel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonCancel.setText("ȡ��");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButtonCancel_actionPerformed(e);
            }
        });
        jPanelReplace.setFont(new java.awt.Font("DialogInput", 0, 12));
        jPanelReplace.setLayout(null);
        jPanelSum.setFont(new java.awt.Font("DialogInput", 0, 12));
        jPanelSum.setLayout(null);
        jPanelWhere.setFont(new java.awt.Font("DialogInput", 0, 12));
        jPanelWhere.setLayout(null);
        jLabelDisplay.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelDisplay.setText("������ʾ");
        jLabelDisplay.setBounds(new Rectangle(34, 11, 75, 22));
        jTextDisplay.setBounds(new Rectangle(34, 35, 163, 26));
        jLabelRestrict.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelRestrict.setText("Լ������");
        jLabelRestrict.setBounds(new Rectangle(34, 67, 75, 25));
        jComboRestrict.setBounds(new Rectangle(34, 95, 164, 26));
        jLabelReplace.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelReplace.setText("�ֶ��滻");
        jLabelReplace.setBounds(new Rectangle(33, 129, 75, 23));
        jPanel2.setFont(new java.awt.Font("DialogInput", 0, 12));
        jPanel2.setBorder(titledBorder2);
        jPanel2.setBounds(new Rectangle(13, 11, 297, 217));
        jPanel2.setLayout(null);
        jLabelSumRule.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelSumRule.setText("���ܹ���");
        jLabelSumRule.setBounds(new Rectangle(27, 18, 67, 25));
        jCheckSum1.setFont(new java.awt.Font("DialogInput", 0, 12));
        jCheckSum1.setText("С��");
        jCheckSum1.setBounds(new Rectangle(42, 51, 86, 29));
        jCheckSum2.setFont(new java.awt.Font("DialogInput", 0, 12));
        jCheckSum2.setText("�ϼ�");
        jCheckSum2.setBounds(new Rectangle(144, 51, 86, 29));
        jLabelSumDep.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelSumDep.setText("��������");
        jLabelSumDep.setBounds(new Rectangle(27, 87, 61, 27));
        jScrollPaneSumDep.setFont(new java.awt.Font("DialogInput", 0, 12));
        jScrollPaneSumDep.setBounds(new Rectangle(28, 122, 220, 113));
        jListSum.setFont(new java.awt.Font("DialogInput", 0, 12));
        jTextColumn.setBounds(new Rectangle(75, 33, 166, 29));
        jTextValue.setBounds(new Rectangle(75, 76, 168, 29));
        jComboRelation.setBounds(new Rectangle(74, 120, 69, 28));
        jButtonSql.setBounds(new Rectangle(175, 120, 70, 30));
        jButtonSql.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonSql.setText("ȷ��");
        jButtonSql.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButtonSql_actionPerformed(e);
            }
        });
        jScrollPane1.setBounds(new Rectangle(73, 163, 221, 82));
        jButtonAdd1.setBounds(new Rectangle(259, 33, 38, 29));
        jButtonAdd1.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonAdd1.setText("...");
        jButtonAdd1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButtonAdd1_actionPerformed(e);
            }
        });
        jButtonAdd2.setBounds(new Rectangle(259, 77, 38, 30));
        jButtonAdd2.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonAdd2.setText("...");
        jButtonAdd2.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButtonAdd2_actionPerformed(e);
            }
        });
        jTextAreaSql.setText("where 1��1");
        jLabelColumn1.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelColumn1.setText("�ֶ�1");
        jLabelColumn1.setBounds(new Rectangle(25, 34, 40, 27));
        jLabelColumn2.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelColumn2.setText("�ֶ�2");
        jLabelColumn2.setBounds(new Rectangle(25, 77, 40, 27));
        jLabelRelation.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelRelation.setText("��ϵ");
        jLabelRelation.setBounds(new Rectangle(25, 121, 40, 27));
        jLabelSql.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelSql.setText("SQL");
        jLabelSql.setBounds(new Rectangle(25, 164, 40, 27));
        jListColumn.addListSelectionListener(new javax.swing.event.
                                             ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                jListColumn_valueChanged(e);
            }
        });
        jListColumn.setFont(new java.awt.Font("DialogInput", 0, 12));
        jTextReplace.setBounds(new Rectangle(34, 156, 164, 26));
        jButtonAdd3.setBounds(new Rectangle(209, 156, 32, 26));
        jButtonAdd3.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonAdd3.setText("...");
        jButtonAdd3.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButtonAdd3_actionPerformed(e);
            }
        });
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setModal(true);
        this.getContentPane().add(jPanelColumn, null);
        jPanelColumn.add(jScrollPaneColumn, null);
        jScrollPaneColumn.getViewport().add(jListColumn, null);
        this.getContentPane().add(jTabbedPaneAtt, null);
        jTabbedPaneAtt.add(jPanelGerenral, "����");
//        jTabbedPaneAtt.add(jPanelReplace,  "�滻");
        jPanelReplace.add(jPanel2, null);
        jTabbedPaneAtt.add(jPanelSum, "����");
        jTabbedPaneAtt.add(jPanelWhere, "����");
        this.getContentPane().add(jPanelButton, null);
        jPanelButton.add(jButtonOK, null);
        jPanelButton.add(jButtonCancel, null);
        jPanelGerenral.add(jLabelDisplay, null);
        jPanelGerenral.add(jTextDisplay, null);
        jPanelGerenral.add(jLabelRestrict, null);
        jPanelGerenral.add(jComboRestrict, null);
        jPanelGerenral.add(jLabelReplace, null);
        jPanelSum.add(jLabelSumRule, null);
        jPanelSum.add(jCheckSum1, null);
        jPanelSum.add(jCheckSum2, null);
        jPanelSum.add(jLabelSumDep, null);
        jPanelSum.add(jScrollPaneSumDep, null);
        jScrollPaneSumDep.getViewport().add(jListSum, null);
        jPanelWhere.add(jTextColumn, null);
        jPanelWhere.add(jTextValue, null);
        jPanelWhere.add(jComboRelation, null);
        jPanelWhere.add(jButtonSql, null);
        jPanelWhere.add(jScrollPane1, null);
        jPanelWhere.add(jButtonAdd1, null);
        jPanelWhere.add(jButtonAdd2, null);
        jPanelWhere.add(jLabelColumn1, null);
        jPanelWhere.add(jLabelColumn2, null);
        jPanelWhere.add(jLabelRelation, null);
        jPanelWhere.add(jLabelSql, null);
        jScrollPane1.getViewport().add(jTextAreaSql, null);
        jPanelGerenral.add(jTextReplace, null);
        jPanelGerenral.add(jButtonAdd3, null);
    }

    public void init()
    {
//        /**��ʼ�������*/
//        if(code.getFieldDefine().size()==0||
//           code.getTableDefine().size()==0)
//        {
//            code.getTableFields();
//        }
//        /**��ʼ����ű�����Ϣ��HashMap*/
//        /**todo initial HashMap*/
//        LoadEdit();
//        vecFields=frmParent.vecFields;
//        jListColumn.setListData(vecFields);
//        int intSelected=frmParent.jListColumn.getSelectedIndex();
//        jListColumn.setSelectedIndex(intSelected);
//        /**��ʼ������*/
//        jComboRestrict.removeAllItems();
//        jComboRestrict.addItem("");
//        jComboRestrict.addItem("Count");
//        jComboRestrict.addItem("Sum");
//        jComboRestrict.setSelectedIndex(0);
//        jComboRelation.removeAllItems();
//        jComboRelation.addItem("");
//        jComboRelation.addItem("=");
//        jComboRelation.addItem(">");
//        jComboRelation.addItem("<");
//        jComboRelation.setSelectedIndex(0);
//        /**��ҳ���г�ʼ��*/
//        init1();
//        init2();
//        init3();
//        init4();
    }

    private void init1()
    {
//        String strSelected=jListColumn.getSelectedValue().toString();
//        //������ʾ
//        Object obj=mapDisplay.get(strSelected);
//        if(obj!=null&&!obj.equals(""))
//            jTextDisplay.setText((String)obj);
//        //��ѡ�����ֶλ�û�ж���������ʾʱ��ȡѡ������ֶε��������
//        else
//        {
//            //��ֱ�id���ֶ�id
//            String strTableId=(String)mapSubject.get(strSelected);
//            String strFieldId=strSelected;
//            String strText=code.getFieldDefineScm(strTableId,strFieldId).getFieldName();
//            jTextDisplay.setText(strText);
//        }
//        //Լ������
//        obj=mapRestrict.get(strSelected);
//        if(obj!=null&&!obj.equals(""))
//            jComboRestrict.setSelectedItem((String)obj);
//        else
//            jComboRestrict.setSelectedIndex(0);
//        //���滻
//        obj=mapReplace.get(strSelected);
//        if(obj!=null&&!obj.equals(""))
//            jTextReplace.setText((String)obj);
//        else
//            jTextReplace.setText("");
    }

    private void init2()
    {
    }

    private void init3()
    {
//        /**��ʼ���ؼ�*/
//        jCheckSum1.setSelected(false);
//        jCheckSum2.setSelected(false);
//        jListSum.setListData(frmParent.vecFields);
//        /**�����û�ѡ����ֶ��޸Ŀؼ�������*/
//        String strSelected=jListColumn.getSelectedValue().toString();
//        //�޸ĸ�ѡ��״̬
//        Object obj=mapSumRule.get(strSelected);
//        if(obj!=null&&!obj.equals(""))
//        {
//            String str=(String)obj;
//            if(str.equals("00"))
//            {
//                jCheckSum1.setSelected(false);
//                jCheckSum2.setSelected(false);
//            }
//            if(str.equals("01"))
//            {
//                jCheckSum1.setSelected(false);
//                jCheckSum2.setSelected(true);
//            }
//            if(str.equals("10"))
//            {
//                jCheckSum1.setSelected(true);
//                jCheckSum2.setSelected(false);
//            }
//            if(str.equals("11"))
//            {
//                jCheckSum1.setSelected(true);
//                jCheckSum2.setSelected(true);
//            }
//        }
//        //�޸Ļ��������ֶ�
//        obj=mapSumDepend.get(strSelected);
//        if(obj!=null&&!obj.equals(""))
//        {
//            jListSum.setSelectedIndex(Integer.parseInt((String)obj));
//        }
    }

    private void init4()
    {
//        /**��ʼ���ؼ�*/
//        String strSelected=jListColumn.getSelectedValue().toString();
//        jTextColumn.setText("");
//        jTextValue.setText("");
//        jComboRelation.setSelectedIndex(0);
//        jTextAreaSql.setText("where 1=1");
//        /**�޸�ȡ������*/
//        Object obj=mapWhere.get(strSelected);
//        if(obj!=null&&!obj.equals(""))
//        {
//            jTextAreaSql.setText((String)obj);
//        }
    }

    public static void main(String args[])
    {
        FrameEditColumn frame = new FrameEditColumn();
        frame.setSize(630, 450);
        frame.init();
        frame.show();
    }

    void jButtonOK_actionPerformed(ActionEvent e)
    {
//        /**�ڴ����ڲ������������޸�*/
//        if(!strPreSelected.equals(""))
//        {
//            saveLocalEdit();
//        }
//        /**����һ�������ύ�������޸�*/
//        ConfirmEdit();
//        this.dispose();
    }

    void jButtonCancel_actionPerformed(ActionEvent e)
    {
        this.dispose();
    }

    void jListColumn_valueChanged(ListSelectionEvent e)
    {
//        /**�����û�ѡ��Ĳ�ͬ�ֶ������½���*/
//        if(!strPreSelected.equals(""))
//        {
//            saveLocalEdit();
//            init1();
//            init2();
//            init3();
//        }
//        strPreSelected=(String)jListColumn.getSelectedValue();
    }

    private void saveLocalEdit()
    {
//        String strDisplay=jTextDisplay.getText();
//        String strRestrict=(String)jComboRestrict.getSelectedItem();
//        String strReplace=jTextReplace.getText();
//        String strSumRule="";
//        if(!jCheckSum1.isSelected()&&!jCheckSum2.isSelected())
//            strSumRule="00";
//        if(!jCheckSum1.isSelected()&&jCheckSum2.isSelected())
//            strSumRule="01";
//        if(jCheckSum1.isSelected()&&!jCheckSum2.isSelected())
//            strSumRule="10";
//        if(jCheckSum1.isSelected()&&jCheckSum2.isSelected())
//            strSumRule="11";
//        String strSumDepend=""+jListSum.getSelectedIndex();
//        String strWhere=jTextAreaSql.getText();
//        mapDisplay.put(strPreSelected,strDisplay);
//        mapRestrict.put(strPreSelected,strRestrict);
//        mapReplace.put(strPreSelected,strReplace);
//        //�����ֶζ�ʹ��ͬһ�����ܹ���
//        if(!strSumRule.equals("00"))
//            for(int i=0;i<vecFields.size();i++)
//            {
//                mapSumRule.put(vecFields.elementAt(i).toString(),strSumRule);
//            }
//        mapSumDepend.put(strPreSelected,strSumDepend);
//        //������������е��ֶι���
//        for(int i=0;i<vecFields.size();i++)
//        {
//            mapWhere.put(vecFields.elementAt(i).toString(),strWhere);
//        }
    }

    private void ConfirmEdit()
    {
//        /**���޸Ľ���ύ��������*/
//        frmParent.mapSubject=(HashMap)this.mapSubject.clone();
//        frmParent.mapDisplay=(HashMap)this.mapDisplay.clone();
//        frmParent.mapRestrict=(HashMap)this.mapRestrict.clone();
//        frmParent.mapReplace=(HashMap)this.mapReplace.clone();
//        frmParent.mapSumRule=(HashMap)this.mapSumRule.clone();
//        frmParent.mapSumDepend=(HashMap)this.mapSumDepend.clone();
//        frmParent.mapWhere=(HashMap)this.mapWhere.clone();
    }

    private void LoadEdit()
    {
//        /**��ȡ�������е���Ϣ*/
//        this.mapSubject=(HashMap)frmParent.mapSubject.clone();
//        this.mapDisplay=(HashMap)frmParent.mapDisplay.clone();
//        this.mapRestrict=(HashMap)frmParent.mapRestrict.clone();
//        this.mapReplace=(HashMap)frmParent.mapReplace.clone();
//        this.mapSumDepend=(HashMap)frmParent.mapSumDepend.clone();
//        this.mapSumRule=(HashMap)frmParent.mapSumRule.clone();
//        this.mapWhere=(HashMap)frmParent.mapWhere.clone();
    }

    void jButtonAdd1_actionPerformed(ActionEvent e)
    {
        FrameAddColumn1 frame = new FrameAddColumn1(this);
        frame.setSize(350, 500);
        frame.init();
        frame.show();
    }

    void jButtonAdd2_actionPerformed(ActionEvent e)
    {
        FrameAddColumn2 frame = new FrameAddColumn2(this);
        frame.setSize(350, 500);
        frame.init();
        frame.show();
    }

    void jButtonSql_actionPerformed(ActionEvent e)
    {
        if (jTextColumn.getText().equals("") ||
            jTextValue.getText().equals("") ||
            jComboRelation.getSelectedIndex() == 0)
        {
            return;
        }
        /**ƴ�Ʊ����ȡ������*/
        String strColumn = jTextColumn.getText();
        String strValue = jTextValue.getText();
        String strRelation = jComboRelation.getSelectedItem().toString();
        String strText = jTextAreaSql.getText();
        String strSql = "\nand " + strColumn + strRelation + strValue;
        jTextAreaSql.setText(strText + strSql);
    }

    void jButtonAdd3_actionPerformed(ActionEvent e)
    {
        FrameAddColumn3 frame = new FrameAddColumn3(this);
        frame.setSize(350, 500);
        frame.init();
        frame.show();
    }
}

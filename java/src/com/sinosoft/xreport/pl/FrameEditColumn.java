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
 * 说明：(1)将父窗口的HashMap读到本地的HashMap
 *      (2)将父窗口的显示字段向量读到本地的显示字段向量
 *      (3)初始化界面
 *      (4)确定时将本地的HashMap信息提交到父窗口的HashMap中
 *      (5)选中不同的显示字段时将字段对应的信息存到本地的HashMap中，并刷新界面
 */

public class FrameEditColumn extends JDialog
{
//    /**父窗口*/
//    FramePlaneReport frmParent=null;
//    /**代码表*/
//    Code code=new Code();
//    /***/
//    Vector vecFields=new Vector();
//    /**列表框上一次选择结果*/
//    String strPreSelected="";
//    /**主题*/
//    HashMap mapSubject=new HashMap();
//    /**中文显示*/
//    HashMap mapDisplay=new HashMap();
//    /**约束条件*/
//    HashMap mapRestrict=new HashMap();
//    /**替换列*/
//    HashMap mapReplace=new HashMap();
//    /**汇总规则*/
//    HashMap mapSumRule=new HashMap();
//    /**汇总依据*/
//    HashMap mapSumDepend=new HashMap();
//    /**条件*/
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
        titledBorder1 = new TitledBorder(border1, "显示字段");
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
        jButtonOK.setText("确定");
        jButtonOK.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButtonOK_actionPerformed(e);
            }
        });
        jButtonCancel.setBounds(new Rectangle(142, 8, 80, 32));
        jButtonCancel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonCancel.setText("取消");
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
        jLabelDisplay.setText("中文显示");
        jLabelDisplay.setBounds(new Rectangle(34, 11, 75, 22));
        jTextDisplay.setBounds(new Rectangle(34, 35, 163, 26));
        jLabelRestrict.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelRestrict.setText("约束条件");
        jLabelRestrict.setBounds(new Rectangle(34, 67, 75, 25));
        jComboRestrict.setBounds(new Rectangle(34, 95, 164, 26));
        jLabelReplace.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelReplace.setText("字段替换");
        jLabelReplace.setBounds(new Rectangle(33, 129, 75, 23));
        jPanel2.setFont(new java.awt.Font("DialogInput", 0, 12));
        jPanel2.setBorder(titledBorder2);
        jPanel2.setBounds(new Rectangle(13, 11, 297, 217));
        jPanel2.setLayout(null);
        jLabelSumRule.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelSumRule.setText("汇总规则");
        jLabelSumRule.setBounds(new Rectangle(27, 18, 67, 25));
        jCheckSum1.setFont(new java.awt.Font("DialogInput", 0, 12));
        jCheckSum1.setText("小计");
        jCheckSum1.setBounds(new Rectangle(42, 51, 86, 29));
        jCheckSum2.setFont(new java.awt.Font("DialogInput", 0, 12));
        jCheckSum2.setText("合计");
        jCheckSum2.setBounds(new Rectangle(144, 51, 86, 29));
        jLabelSumDep.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelSumDep.setText("汇总依据");
        jLabelSumDep.setBounds(new Rectangle(27, 87, 61, 27));
        jScrollPaneSumDep.setFont(new java.awt.Font("DialogInput", 0, 12));
        jScrollPaneSumDep.setBounds(new Rectangle(28, 122, 220, 113));
        jListSum.setFont(new java.awt.Font("DialogInput", 0, 12));
        jTextColumn.setBounds(new Rectangle(75, 33, 166, 29));
        jTextValue.setBounds(new Rectangle(75, 76, 168, 29));
        jComboRelation.setBounds(new Rectangle(74, 120, 69, 28));
        jButtonSql.setBounds(new Rectangle(175, 120, 70, 30));
        jButtonSql.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonSql.setText("确定");
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
        jTextAreaSql.setText("where 1＝1");
        jLabelColumn1.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelColumn1.setText("字段1");
        jLabelColumn1.setBounds(new Rectangle(25, 34, 40, 27));
        jLabelColumn2.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelColumn2.setText("字段2");
        jLabelColumn2.setBounds(new Rectangle(25, 77, 40, 27));
        jLabelRelation.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelRelation.setText("关系");
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
        jTabbedPaneAtt.add(jPanelGerenral, "常规");
//        jTabbedPaneAtt.add(jPanelReplace,  "替换");
        jPanelReplace.add(jPanel2, null);
        jTabbedPaneAtt.add(jPanelSum, "汇总");
        jTabbedPaneAtt.add(jPanelWhere, "条件");
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
//        /**初始化代码表*/
//        if(code.getFieldDefine().size()==0||
//           code.getTableDefine().size()==0)
//        {
//            code.getTableFields();
//        }
//        /**初始化存放报表信息的HashMap*/
//        /**todo initial HashMap*/
//        LoadEdit();
//        vecFields=frmParent.vecFields;
//        jListColumn.setListData(vecFields);
//        int intSelected=frmParent.jListColumn.getSelectedIndex();
//        jListColumn.setSelectedIndex(intSelected);
//        /**初始化界面*/
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
//        /**分页进行初始化*/
//        init1();
//        init2();
//        init3();
//        init4();
    }

    private void init1()
    {
//        String strSelected=jListColumn.getSelectedValue().toString();
//        //中文显示
//        Object obj=mapDisplay.get(strSelected);
//        if(obj!=null&&!obj.equals(""))
//            jTextDisplay.setText((String)obj);
//        //当选定的字段还没有定义中文显示时，取选定表和字段的中文组合
//        else
//        {
//            //拆分表id和字段id
//            String strTableId=(String)mapSubject.get(strSelected);
//            String strFieldId=strSelected;
//            String strText=code.getFieldDefineScm(strTableId,strFieldId).getFieldName();
//            jTextDisplay.setText(strText);
//        }
//        //约束条件
//        obj=mapRestrict.get(strSelected);
//        if(obj!=null&&!obj.equals(""))
//            jComboRestrict.setSelectedItem((String)obj);
//        else
//            jComboRestrict.setSelectedIndex(0);
//        //列替换
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
//        /**初始化控件*/
//        jCheckSum1.setSelected(false);
//        jCheckSum2.setSelected(false);
//        jListSum.setListData(frmParent.vecFields);
//        /**根据用户选择的字段修改控件的内容*/
//        String strSelected=jListColumn.getSelectedValue().toString();
//        //修改复选框状态
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
//        //修改汇总依据字段
//        obj=mapSumDepend.get(strSelected);
//        if(obj!=null&&!obj.equals(""))
//        {
//            jListSum.setSelectedIndex(Integer.parseInt((String)obj));
//        }
    }

    private void init4()
    {
//        /**初始化控件*/
//        String strSelected=jListColumn.getSelectedValue().toString();
//        jTextColumn.setText("");
//        jTextValue.setText("");
//        jComboRelation.setSelectedIndex(0);
//        jTextAreaSql.setText("where 1=1");
//        /**修改取数条件*/
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
//        /**在窗口内部保存所做的修改*/
//        if(!strPreSelected.equals(""))
//        {
//            saveLocalEdit();
//        }
//        /**向上一级窗口提交所做的修改*/
//        ConfirmEdit();
//        this.dispose();
    }

    void jButtonCancel_actionPerformed(ActionEvent e)
    {
        this.dispose();
    }

    void jListColumn_valueChanged(ListSelectionEvent e)
    {
//        /**根据用户选择的不同字段来更新界面*/
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
//        //所有字段都使用同一个汇总规则
//        if(!strSumRule.equals("00"))
//            for(int i=0;i<vecFields.size();i++)
//            {
//                mapSumRule.put(vecFields.elementAt(i).toString(),strSumRule);
//            }
//        mapSumDepend.put(strPreSelected,strSumDepend);
//        //将条件语句所有的字段关联
//        for(int i=0;i<vecFields.size();i++)
//        {
//            mapWhere.put(vecFields.elementAt(i).toString(),strWhere);
//        }
    }

    private void ConfirmEdit()
    {
//        /**将修改结果提交到父窗口*/
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
//        /**读取父窗口中的信息*/
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
        /**拼制报表的取数条件*/
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

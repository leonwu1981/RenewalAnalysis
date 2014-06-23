package com.sinosoft.xreport.pl;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import com.sinosoft.xreport.bl.Code;


/**
 * <p>Title: test</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft</p>
 * @author unascribed
 * @version 1.0
 */

public class FrameAddColumn extends JDialog
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**�����ھ��*/
    private FramePlaneReport frmParent = null;
    /**�����*/
    private Code code = new Code();
    /**���ؼ�*/
    private DefaultMutableTreeNode root = null;
    private DefaultMutableTreeNode nodTable = null;
    private DefaultMutableTreeNode nodField = null;
    private TreeModel model = null;

    private JPanel jPanelColumn = new JPanel();
    private Border border1;
    private TitledBorder titledBorder1;
    private JPanel jPanelButton = new JPanel();
    private JButton jButtonOk = new JButton();
    private JButton jButtonCancel = new JButton();
    private JScrollPane jScrollPaneTree = new JScrollPane();
    public JTree jTreeColumn = new JTree();
    public FrameAddColumn()
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

    public FrameAddColumn(FramePlaneReport frmParent)
    {
        super(frmParent);
        this.frmParent = frmParent;
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
        border1 = BorderFactory.createEmptyBorder();
        titledBorder1 = new TitledBorder(border1, "����ֶ�");
        this.getContentPane().setLayout(null);
        jPanelColumn.setFont(new java.awt.Font("DialogInput", 0, 12));
        jPanelColumn.setBorder(titledBorder1);
        jPanelColumn.setBounds(new Rectangle(41, 31, 265, 360));
        jPanelColumn.setLayout(null);
        jPanelButton.setFont(new java.awt.Font("DialogInput", 0, 12));
        jPanelButton.setBounds(new Rectangle(40, 396, 265, 46));
        jPanelButton.setLayout(null);
        jButtonOk.setBounds(new Rectangle(33, 8, 80, 32));
        jButtonOk.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonOk.setText("ȷ��");
        jButtonOk.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButtonOk_actionPerformed(e);
            }
        });
        jButtonCancel.setBounds(new Rectangle(155, 8, 80, 32));
        jButtonCancel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonCancel.setText("ȡ��");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButtonCancel_actionPerformed(e);
            }
        });
        jScrollPaneTree.setBounds(new Rectangle(6, 25, 250, 319));
        jTreeColumn.setFont(new java.awt.Font("DialogInput", 0, 12));
        this.setModal(true);
        this.getContentPane().add(jPanelColumn, null);
        jPanelColumn.add(jScrollPaneTree, null);
        jScrollPaneTree.getViewport().add(jTreeColumn, null);
        this.getContentPane().add(jPanelButton, null);
        jPanelButton.add(jButtonOk, null);
        jPanelButton.add(jButtonCancel, null);
    }

    public void init()
    {
//        /**��ʼ������*/
//        if(code.getFieldDefine().size()==0||
//           code.getTableDefine().size()==0)
//        {
//            code.getTableFields();
//        }
//        root=new DefaultMutableTreeNode("ROOT");
//        /**��ʼ����*/
//        TableDefine dbTableDefine=code.getTableDefine();
//        //todo:���ݵ�ǰ�������ϢɸѡTableDefine
//        for(int i=0;i<dbTableDefine.size();i++)
//        {
//            nodTable=new DefaultMutableTreeNode(dbTableDefine.at(i));
//            root.add(nodTable);
//            FieldDefine dbFieldDefine=code.getFieldDefine(dbTableDefine.at(i).getTableId());
//            for(int j=0;j<dbFieldDefine.size();j++)
//            {
//                nodField=new DefaultMutableTreeNode(dbFieldDefine.at(j));
//                nodTable.add(nodField);
//            }
//        }
//        model=new DefaultTreeModel(root);
//        jTreeColumn.setModel(model);
    }

    public static void main(String args[])
    {
        FrameAddColumn frame = new FrameAddColumn();
        frame.setSize(350, 500);
        frame.init();
        frame.show();
    }

    void jButtonOk_actionPerformed(ActionEvent e)
    {
//        /**��ȡ��ѡ��ı��ֶ�*/
//        TreePath[] path=jTreeColumn.getSelectionPaths();
//        int intLength=path.length;
//        for(int i=0;i<intLength;i++)
//        {
//            if(!((DefaultMutableTreeNode)path[i].getLastPathComponent()).isLeaf())
//                continue;
//            /**��ȡ����Ϣ*/
//            TableDefineScm scmTableDefine=(TableDefineScm)((DefaultMutableTreeNode)
//                      path[i].getPathComponent(1)).getUserObject();
//            /**��ȡ�ֶ���Ϣ*/
//            FieldDefineScm scmFieldDefine=(FieldDefineScm)((DefaultMutableTreeNode)
//                      path[i].getPathComponent(2)).getUserObject();
//            /**�򸸴��������ѡ����*/
//            String strId=scmFieldDefine.getFieldId();
//            frmParent.vecFields.addElement(strId);
//            frmParent.strSubject=scmTableDefine.getTableId();
//            /**��ʼ�������ڵ�HashMap*/
//            frmParent.mapSubject.put(strId,frmParent.strSubject);
//            frmParent.mapDisplay.put(strId,"");
//            frmParent.mapRestrict.put(strId,"");
//            frmParent.mapReplace.put(strId,"");
//            frmParent.mapSumRule.put(strId,"00");
//            frmParent.mapSumDepend.put(strId,"-1");
//            frmParent.mapWhere.put(strId,"where 1=1 ");
//        }
//        frmParent.jListColumn.setListData(frmParent.vecFields);
//        this.dispose();
    }

    void jButtonCancel_actionPerformed(ActionEvent e)
    {
        this.dispose();
    }
}

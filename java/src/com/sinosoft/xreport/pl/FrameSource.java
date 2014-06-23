package com.sinosoft.xreport.pl;

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.sinosoft.xreport.bl.Code;
import com.sinosoft.xreport.dl.Dim;
import com.sinosoft.xreport.dl.Source;
import com.sinosoft.xreport.dl.Value;
import com.sinosoft.xreport.dl.ValueImpl;
import com.sinosoft.xreport.util.SysConfig;


/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author lixy
 * @version 1.0
 */

public class FrameSource extends JDialog
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**�����ھ��*/
    private XReportMain frmParent;
    /**������*/
    private Report report;
    /**�����*/
    private Code code = new Code();

    //////////////temp////////////////////
    private Map map = new HashMap();
    /////////////temp/////////////////////

    private JScrollPane jScrollResult = new JScrollPane();
    private JTextArea jTextResult = new JTextArea();
    private JButton jButtonOk = new JButton();
    private JButton jButtonCancel = new JButton();
    private JButton jButtonUndo = new JButton();
    private JScrollPane jScrollPaneSource = new JScrollPane();
    private JScrollPane jScrollPaneDim = new JScrollPane();
    private JScrollPane jScrollPaneValue = new JScrollPane();
    private JList jListDimension = new JList();
    private JTree jTreeSource = new JTree();
    private JButton jButtonAdd = new JButton();
    private JButton jButtonSub = new JButton();
    private JButton jButtonMul = new JButton();
    private JButton jButtonDiv = new JButton();
    private JButton jButtonAbove = new JButton();
    private JButton jButtonLow = new JButton();
    private JButton jButtonEqual = new JButton();
    private JButton jButtonNotEqual = new JButton();
    private JButton jButtonAnd = new JButton();
    private JButton jButtonOr = new JButton();
    private JButton jButtonNot = new JButton();
    private JButton jButtonLeft = new JButton();
    private JButton jButtonRight = new JButton();
    private JButton jButtonLike = new JButton();
    private JLabel jLabelGen = new JLabel();
    private JPanel jPanelValue = new JPanel();
    private GridLayout gridLayoutValue = new GridLayout(10, 2);
    private JButton jButtonCommit = new JButton();
    private JComboBox[] jComboBox;
    private JLabel[] jLabel1;

    public FrameSource()
    {
        ////////////////////temp/////////////////
        map.put("center_code", "330700");
        map.put("acc_book_type", "01");
        map.put("acc_book_code", "11");
        /////////////////temp//////////////////

        try
        {
            jbInit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public FrameSource(XReportMain frmParent)
    {
        super(frmParent);
        report = frmParent.getReport();
        try
        {
            jbInit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        FrameSource frmSource = new FrameSource();
        frmSource.setSize(595, 490);
        frmSource.open("");
//        System.out.println(frmSource.getDimension("summary_balance.acc_book_type"));
    }

    private void jbInit() throws Exception
    {
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setFont(new java.awt.Font("DialogInput", 0, 12));
        this.setModal(true);
        this.setResizable(false);
        this.getContentPane().setLayout(null);
        jScrollResult.setFont(new java.awt.Font("DialogInput", 0, 12));
        jScrollResult.setBounds(new Rectangle(25, 57, 434, 94));
        jButtonOk.setBounds(new Rectangle(487, 58, 78, 29));
        jButtonOk.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonOk.setText("ȷ��");
        jButtonOk.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonOK();
            }
        });
        jButtonCancel.setBounds(new Rectangle(487, 88, 78, 29));
        jButtonCancel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonCancel.setText("ȡ��");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonCancel();
            }
        });
        jButtonUndo.setBounds(new Rectangle(487, 118, 78, 29));
        jButtonUndo.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonUndo.setText("����");
        jButtonUndo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonUndo();
            }
        });
        jScrollPaneSource.setFont(new java.awt.Font("DialogInput", 0, 12));
        jScrollPaneSource.setBounds(new Rectangle(25, 200, 182, 219));
        jScrollPaneDim.setFont(new java.awt.Font("DialogInput", 0, 12));
        jScrollPaneDim.setBounds(new Rectangle(207, 200, 182, 219));
        jScrollPaneValue.setFont(new java.awt.Font("DialogInput", 0, 12));
        jScrollPaneValue.setBounds(new Rectangle(389, 200, 182, 219));
        jTreeSource.setFont(new java.awt.Font("DialogInput", 0, 12));
        jTreeSource.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseReleased(MouseEvent e)
            {
                jTreeSourceMouseReleased();
            }
        });
        jListDimension.setFont(new java.awt.Font("DialogInput", 0, 12));
        jListDimension.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseReleased(MouseEvent e)
            {
                jListDimensionMouseReleased();
            }
        });
        jButtonAdd.setBounds(new Rectangle(24, 162, 22, 29));
        jButtonAdd.setFont(new java.awt.Font("DialogInput", 0, 10));
        jButtonAdd.setMargin(new Insets(2, 4, 2, 4));
        jButtonAdd.setText("+");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonAnd();
            }
        });
        jButtonSub.setBounds(new Rectangle(49, 162, 22, 29));
        jButtonSub.setFont(new java.awt.Font("DialogInput", 0, 10));
        jButtonSub.setMargin(new Insets(2, 4, 2, 4));
        jButtonSub.setText("-");
        jButtonSub.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonSub();
            }
        });
        jButtonMul.setBounds(new Rectangle(74, 162, 22, 29));
        jButtonMul.setFont(new java.awt.Font("DialogInput", 0, 10));
        jButtonMul.setMargin(new Insets(2, 4, 2, 4));
        jButtonMul.setText("*");
        jButtonMul.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonMul();
            }
        });
        jButtonDiv.setBounds(new Rectangle(98, 162, 22, 29));
        jButtonDiv.setFont(new java.awt.Font("DialogInput", 0, 10));
        jButtonDiv.setMargin(new Insets(2, 4, 2, 4));
        jButtonDiv.setText("/");
        jButtonDiv.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonDiv();
            }
        });
        jButtonAbove.setBounds(new Rectangle(123, 162, 22, 29));
        jButtonAbove.setFont(new java.awt.Font("DialogInput", 0, 10));
        jButtonAbove.setMargin(new Insets(2, 4, 2, 4));
        jButtonAbove.setText(">");
        jButtonAbove.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonAbove();
            }
        });
        jButtonLow.setBounds(new Rectangle(148, 162, 22, 29));
        jButtonLow.setFont(new java.awt.Font("DialogInput", 0, 10));
        jButtonLow.setMargin(new Insets(2, 4, 2, 4));
        jButtonLow.setText("<");
        jButtonLow.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonLow();
            }
        });
        jButtonEqual.setBounds(new Rectangle(173, 162, 22, 29));
        jButtonEqual.setFont(new java.awt.Font("DialogInput", 0, 10));
        jButtonEqual.setMargin(new Insets(2, 4, 2, 4));
        jButtonEqual.setText("=");
        jButtonEqual.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonEqual();
            }
        });
        jButtonNotEqual.setBounds(new Rectangle(197, 162, 22, 29));
        jButtonNotEqual.setFont(new java.awt.Font("DialogInput", 0, 10));
        jButtonNotEqual.setMargin(new Insets(2, 2, 2, 2));
        jButtonNotEqual.setText("!=");
        jButtonNotEqual.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonNotEqual();
            }
        });
        jButtonAnd.setBounds(new Rectangle(222, 162, 22, 29));
        jButtonAnd.setFont(new java.awt.Font("DialogInput", 0, 10));
        jButtonAnd.setMargin(new Insets(2, 4, 2, 4));
        jButtonAnd.setText("&");
        jButtonAnd.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonAnd();
            }
        });
        jButtonOr.setBounds(new Rectangle(247, 162, 22, 29));
        jButtonOr.setFont(new java.awt.Font("DialogInput", 0, 10));
        jButtonOr.setMargin(new Insets(2, 4, 2, 4));
        jButtonOr.setText("|");
        jButtonOr.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonOr();
            }
        });
        jButtonNot.setBounds(new Rectangle(272, 162, 22, 29));
        jButtonNot.setFont(new java.awt.Font("DialogInput", 0, 10));
        jButtonNot.setMargin(new Insets(2, 4, 2, 4));
        jButtonNot.setText("!");
        jButtonNot.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonNot();
            }
        });
        jButtonLeft.setBounds(new Rectangle(296, 162, 22, 29));
        jButtonLeft.setFont(new java.awt.Font("DialogInput", 0, 10));
        jButtonLeft.setMargin(new Insets(2, 4, 2, 4));
        jButtonLeft.setText("(");
        jButtonLeft.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonLeft();
            }
        });
        jButtonRight.setBounds(new Rectangle(321, 162, 22, 29));
        jButtonRight.setFont(new java.awt.Font("DialogInput", 0, 10));
        jButtonRight.setMargin(new Insets(2, 4, 2, 4));
        jButtonRight.setText(")");
        jButtonRight.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonRight();
            }
        });
        jButtonLike.setBounds(new Rectangle(346, 162, 36, 29));
        jButtonLike.setFont(new java.awt.Font("DialogInput", 0, 10));
        jButtonLike.setMargin(new Insets(2, 2, 2, 2));
        jButtonLike.setText("Like");
        jButtonLike.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonLike();
            }
        });
        jLabelGen.setFont(new java.awt.Font("DialogInput", 0, 12));
        jLabelGen.setText("���ʽ������");
        jLabelGen.setBounds(new Rectangle(26, 16, 113, 32));
        jPanelValue.setLayout(gridLayoutValue);
        jPanelValue.setFont(new java.awt.Font("DialogInput", 0, 12));
        gridLayoutValue.setColumns(1);
        jButtonCommit.setBounds(new Rectangle(393, 162, 78, 29));
        jButtonCommit.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonCommit.setText("ȷ��");
        jButtonCommit.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                commit();
            }
        });
        this.getContentPane().add(jButtonOk, null);
        this.getContentPane().add(jScrollResult, null);
        this.getContentPane().add(jButtonCancel, null);
        this.getContentPane().add(jButtonUndo, null);
        this.getContentPane().add(jScrollPaneSource, null);
        this.getContentPane().add(jScrollPaneDim, null);
        jScrollPaneDim.getViewport().add(jListDimension, null);
        this.getContentPane().add(jScrollPaneValue, null);
        jScrollPaneValue.getViewport().add(jPanelValue, null);
        this.getContentPane().add(jButtonAdd, null);
        this.getContentPane().add(jButtonSub, null);
        this.getContentPane().add(jButtonMul, null);
        this.getContentPane().add(jButtonDiv, null);
        this.getContentPane().add(jButtonAbove, null);
        this.getContentPane().add(jButtonLow, null);
        this.getContentPane().add(jButtonEqual, null);
        this.getContentPane().add(jButtonNotEqual, null);
        this.getContentPane().add(jButtonAnd, null);
        this.getContentPane().add(jButtonOr, null);
        this.getContentPane().add(jButtonNot, null);
        this.getContentPane().add(jButtonLeft, null);
        this.getContentPane().add(jButtonRight, null);
        this.getContentPane().add(jButtonLike, null);
        this.getContentPane().add(jLabelGen, null);
        this.getContentPane().add(jButtonCommit, null);
        jScrollPaneSource.getViewport().add(jTreeSource, null);
        jScrollResult.getViewport().add(jTextResult, null);
    }

    /**
     * ��ʾ����
     * @param text ��Ԫ�����ַ���
     */
    public void open(String text)
    {
        initData();
        initInterface(text);
        this.show();
    }

    /**
     * ��ʼ������
     * @param text ��Ԫ�����ַ���
     */
    private void initInterface(String text)
    {
        /**��ʼ������ı���*/
        jTextResult.setText(text);
        /**��ʼ�����ؼ�*/
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
        DefaultMutableTreeNode table = new DefaultMutableTreeNode("��");
        DefaultMutableTreeNode report = new DefaultMutableTreeNode("����");
        root.add(table);
        root.add(report);
        /**���ֱ������Դ*/
        Vector vecSource = code.getDataSources();
        for (int i = 0; i < vecSource.size(); i++)
        {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(vecSource.
                    elementAt(i));
            table.add(node);
        }
        /**��ӱ�������Դ*/
        Vector vecReport = code.getReportMain();
        for (int i = 0; i < vecReport.size(); i++)
        {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(vecReport.
                    elementAt(i));
            report.add(node);
        }
        /**�������ؼ�*/
        jTreeSource.setModel(new DefaultTreeModel(root));
    }

    /**
     * ��ʼ������
     */
    private void initData()
    {

        code.getTableFields();
        code.getReportMainMap();
    }

    void jTreeSourceMouseReleased()
    {
        /**��ȡ�û�ѡ����*/
        TreePath path = jTreeSource.getSelectionPath();
        if (path == null)
        {
            return;
        }
        /**��������*/
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.
                                      getLastPathComponent();
        /**����û�ѡ��Ľ�㲻��Ҷ�ӽ���򷵻�*/
        if (!node.isLeaf())
        {
            return;
        }
        /**����û�����*/
        Object obj = node.getUserObject();
        /**�û�ѡ����ֱ������Դ*/
        if (obj instanceof Source)
        {
            Source source = (Source) node.getUserObject();
            Vector vecDimension = code.getDimensions(source.getDataSourceId());
            jListDimension.setListData(vecDimension);
        }
    }

    void jListDimensionMouseReleased()
    {
        /**��ȡ�û�ѡ����ά*/
        Dim dimension = (Dim) jListDimension.getSelectedValue();
        createCombox(dimension);
    }

    private void createCombox(Dim dimension)
    {
        String type = "", value = "";
        /**Ѱ��ѡ��ά�������ϼ�(����)*/
        Vector parent = code.getSuperDimensions(dimension);
        /**����Combox�ĸ���*/
        int intCount = parent.size() + 1;
        /**��ʼ����ǩ����Ͽ�*/
        jComboBox = new JComboBox[intCount];
        jLabel1 = new JLabel[intCount];
        /**��ʼ���ؼ�����*/
        jPanelValue.removeAll();
        /**���������Ĳ���*/
        if (intCount * 2 > 10)
        {
            gridLayoutValue.setRows(2 * intCount);
        }
        /**���������ϼ�ά�ı�ǩ����Ͽ�*/
        for (int i = intCount - 1; i >= 1; i--)
        {
            Dim dim = (Dim) parent.elementAt(i - 1);
            /**��ǩ����ʾά������*/
            jLabel1[i] = new JLabel(dim.getDimenName());
            /**����һ������Ͽ�����¼�������*/
            jComboBox[i] = new JComboBox();
            jComboBox[i].setEditable(true);
            jComboBox[i].setName(dim.getDataSourceId() + SysConfig.KEYSEPARATOR +
                                 dim.getDimenId());
            jComboBox[i].addItemListener(new java.awt.event.ItemListener()
            {
                public void itemStateChanged(ItemEvent e)
                {
                    itemStateChanged1(e);
                }
            });
            /**����ǩ����Ͽ���뵽������*/
            jPanelValue.add(jLabel1[i], null);
            jPanelValue.add(jComboBox[i], null);
            /**����ȫ�ֱ����������޸�ά����Ϣ*/
            type = dim.getDimenId();
            value = (String) map.get(type);
            Map condition = code.getCondition(dim, map);
            Vector vecValue = code.getValue(dim, condition);
            //û���ҵ�ά��ֵ
            if (value == null || value.equals(""))
            {
                //����ȡ����ֵ��д����ά��Ӧ����Ͽ���
                //���һ������ֵ
                Value valNull = new ValueImpl();
                valNull.setValueId("*");
                valNull.setValueName("*");
                vecValue.insertElementAt(valNull, 0);
                jComboBox[i].setModel(new DefaultComboBoxModel(vecValue));
                jComboBox[i].setSelectedItem(valNull);
            }
            //�Ѿ�������һ��ά��ֵ
            else
            {
                //���ø�ά��Ӧ����Ͽ������
                jComboBox[i].setModel(new DefaultComboBoxModel(vecValue));
                jComboBox[i].setSelectedItem(code.getValue(type, value));
            }
        }
        //���ɵ�ǰά��jComboBox
        jLabel1[0] = new JLabel(dimension.getDimenName());
        jComboBox[0] = new JComboBox();
        jComboBox[0].setEditable(true);
        jComboBox[0].setName(dimension.getDataSourceId() +
                             SysConfig.KEYSEPARATOR + dimension.getDimenId());
        jComboBox[0].addItemListener(new java.awt.event.ItemListener()
        {
            public void itemStateChanged(ItemEvent e)
            {
                itemStateChanged1(e);
            }
        });
        jPanelValue.add(jLabel1[0], null);
        jPanelValue.add(jComboBox[0], null);
        type = dimension.getDimenId();
        value = (String) map.get(type);
        Map condition = code.getCondition(dimension, map);
        Vector vecValue = code.getValue(dimension, condition);
        //û���ҵ�ά��ֵ
        if (value == null || value.equals(""))
        {
            //����ȡ����ֵ��д����ά��Ӧ����Ͽ���
            //���һ������ֵ
            Value valNull = new ValueImpl();
            valNull.setValueId("*");
            valNull.setValueName("*");
            vecValue.insertElementAt(valNull, 0);
            jComboBox[0].setModel(new DefaultComboBoxModel(vecValue));
            jComboBox[0].setSelectedItem(valNull);
        }
        //�Ѿ�������һ��ά��ֵ
        else
        {
            //���ø�ά��Ӧ����Ͽ������
            jComboBox[0].setModel(new DefaultComboBoxModel(vecValue));
            jComboBox[0].setSelectedItem(code.getValue(type, value));
        }
        /**������ͼ*/
        jPanelValue.updateUI();
    }

    private Dim getDimension(String name)
    {
        StringTokenizer token = new StringTokenizer(name,
                SysConfig.KEYSEPARATOR);
        Dim dim = code.getDimension((String) token.nextElement(),
                                    (String) token.nextElement());
        return dim;
    }

    public void itemStateChanged1(ItemEvent e)
    {
        /**ɸѡ�¼�*/
        if (e.getStateChange() != ItemEvent.SELECTED)
        {
            return;
        }
        //��õ�ǰ��Ͽ��Ӧ��ά��ѡ��ֵ
        JComboBox box = (JComboBox) e.getSource();
        String strName = box.getName();
        if (!(box.getSelectedItem() instanceof Value))
        {
            return;
        }
        Value value = (Value) box.getSelectedItem();
        Dim dimension = getDimension(strName);
        //ɨ��������Ͽ򣬲��ҵ���Ͽ��Ӧ��ά����
        for (int i = jComboBox.length - 1; i >= 0; i--)
        {
            if (jComboBox[i] == null)
            {
                continue;
            }
            strName = jComboBox[i].getName();
            Dim dim = getDimension(strName);
            if (dim.getSuperDimenId() == null)
            {
                continue;
            }
            Map condition = code.getCondition(dim, map);
            if (code.getSuperDimensions(dim).contains(dimension))
            {
                condition.put(dimension.getDimenId(), value.getValueId());
                Vector vecValue = code.getValue(dim, condition);
                //���һ������ֵ
                Value valNull = new ValueImpl();
                valNull.setValueId("*");
                valNull.setValueName("*");
                vecValue.insertElementAt(valNull, 0);
                jComboBox[i].setModel(new DefaultComboBoxModel(vecValue));
                jComboBox[i].setSelectedItem(valNull);
            }
        }
        //��ȫ�ֱ������ж�ȡ��ǰά��ȡ������
        //�жϵ�ǰ���в�����ά�Ƿ�Ӱ�쵽ɨ�赽��ά
        //���δӰ�첻���κβ���
        //���Ӱ�����Ƚ���ǰ������ά��ֵ��ӵ�ȡ�������У��ٶ�ȡɨ�赽ά��ֵ
    }

    void commit()
    {
        if (jComboBox == null)
        {
            return;
        }
        if (jComboBox[0] == null)
        {
            return;
        }
        TreePath path = jTreeSource.getSelectionPath();
        DefaultMutableTreeNode leaf = (DefaultMutableTreeNode) path.
                                      getLastPathComponent();
        Source source = (Source) leaf.getUserObject();
        Dim dimension = (Dim) jListDimension.getSelectedValue();
        Object obj = jComboBox[0].getSelectedItem();
        if (obj instanceof Value)
        {
            Value value = (Value) obj;
            String text = source.getDataSourceId() + SysConfig.KEYSEPARATOR +
                          dimension.getDimenId() + "<��ϵ��>" + "'" +
                          value.getValueId() + "'" + "\n";
            setText(text);
        }
        if (obj instanceof String)
        {
            String value = (String) obj;
            String text = source.getDataSourceId() + SysConfig.KEYSEPARATOR +
                          dimension.getDimenId() + "<��ϵ��>" + "'" + value + "'" +
                          "\n";
            setText(text);
        }
    }

    private void setText(String text)
    {
        int start = jTextResult.getSelectionStart();
        int end = jTextResult.getSelectionEnd();
        jTextResult.replaceRange(text, start, end);
    }

    void buttonAbove()
    {
        setText(">");
    }

    void buttonLow()
    {
        setText("<");
    }

    void buttonEqual()
    {
        setText("=");
    }

    void buttonAnd()
    {
        setText(" and ");
    }

    void buttonOr()
    {
        setText(" or ");
    }

    void buttonAdd()
    {
        setText(" + ");
    }

    void buttonSub()
    {
        setText(" - ");
    }

    void buttonMul()
    {
        setText(" * ");
    }

    void buttonDiv()
    {
        setText(" / ");
    }

    void buttonNotEqual()
    {
        setText(" != ");
    }

    void buttonNot()
    {
        setText(" ! ");
    }

    void buttonLeft()
    {
        setText(" ( ");
    }

    void buttonRight()
    {
        setText(" ) ");
    }

    void buttonLike()
    {
        setText(" Like ");
    }

    void buttonOK()
    {
        this.dispose();
    }

    void buttonCancel()
    {
        this.dispose();
    }

    void buttonUndo()
    {

    }
}
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
    /**父窗口句柄*/
    private XReportMain frmParent;
    /**报表句柄*/
    private Report report;
    /**代码表*/
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
        jButtonOk.setText("确定");
        jButtonOk.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonOK();
            }
        });
        jButtonCancel.setBounds(new Rectangle(487, 88, 78, 29));
        jButtonCancel.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonCancel.setText("取消");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonCancel();
            }
        });
        jButtonUndo.setBounds(new Rectangle(487, 118, 78, 29));
        jButtonUndo.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonUndo.setText("撤销");
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
        jLabelGen.setText("表达式生成器");
        jLabelGen.setBounds(new Rectangle(26, 16, 113, 32));
        jPanelValue.setLayout(gridLayoutValue);
        jPanelValue.setFont(new java.awt.Font("DialogInput", 0, 12));
        gridLayoutValue.setColumns(1);
        jButtonCommit.setBounds(new Rectangle(393, 162, 78, 29));
        jButtonCommit.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonCommit.setText("确认");
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
     * 显示窗口
     * @param text 单元格定义字符串
     */
    public void open(String text)
    {
        initData();
        initInterface(text);
        this.show();
    }

    /**
     * 初始化界面
     * @param text 单元格定义字符串
     */
    private void initInterface(String text)
    {
        /**初始化结果文本框*/
        jTextResult.setText(text);
        /**初始化树控件*/
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
        DefaultMutableTreeNode table = new DefaultMutableTreeNode("表");
        DefaultMutableTreeNode report = new DefaultMutableTreeNode("报表");
        root.add(table);
        root.add(report);
        /**添加直接数据源*/
        Vector vecSource = code.getDataSources();
        for (int i = 0; i < vecSource.size(); i++)
        {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(vecSource.
                    elementAt(i));
            table.add(node);
        }
        /**添加报表数据源*/
        Vector vecReport = code.getReportMain();
        for (int i = 0; i < vecReport.size(); i++)
        {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(vecReport.
                    elementAt(i));
            report.add(node);
        }
        /**更新树控件*/
        jTreeSource.setModel(new DefaultTreeModel(root));
    }

    /**
     * 初始化数据
     */
    private void initData()
    {

        code.getTableFields();
        code.getReportMainMap();
    }

    void jTreeSourceMouseReleased()
    {
        /**读取用户选择结果*/
        TreePath path = jTreeSource.getSelectionPath();
        if (path == null)
        {
            return;
        }
        /**获得树结点*/
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.
                                      getLastPathComponent();
        /**如果用户选择的结点不是叶子结点则返回*/
        if (!node.isLeaf())
        {
            return;
        }
        /**获得用户对象*/
        Object obj = node.getUserObject();
        /**用户选中了直接数据源*/
        if (obj instanceof Source)
        {
            Source source = (Source) node.getUserObject();
            Vector vecDimension = code.getDimensions(source.getDataSourceId());
            jListDimension.setListData(vecDimension);
        }
    }

    void jListDimensionMouseReleased()
    {
        /**读取用户选定的维*/
        Dim dimension = (Dim) jListDimension.getSelectedValue();
        createCombox(dimension);
    }

    private void createCombox(Dim dimension)
    {
        String type = "", value = "";
        /**寻找选定维的所有上级(有序)*/
        Vector parent = code.getSuperDimensions(dimension);
        /**生成Combox的个数*/
        int intCount = parent.size() + 1;
        /**初始化标签和组合框*/
        jComboBox = new JComboBox[intCount];
        jLabel1 = new JLabel[intCount];
        /**初始化控件容器*/
        jPanelValue.removeAll();
        /**调整容器的布局*/
        if (intCount * 2 > 10)
        {
            gridLayoutValue.setRows(2 * intCount);
        }
        /**生成所有上级维的标签和组合框*/
        for (int i = intCount - 1; i >= 1; i--)
        {
            Dim dim = (Dim) parent.elementAt(i - 1);
            /**标签上显示维的名称*/
            jLabel1[i] = new JLabel(dim.getDimenName());
            /**生成一个新组合框并添加事件处理函数*/
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
            /**将标签和组合框加入到容器中*/
            jPanelValue.add(jLabel1[i], null);
            jPanelValue.add(jComboBox[i], null);
            /**查找全局变量表中有无该维的信息*/
            type = dim.getDimenId();
            value = (String) map.get(type);
            Map condition = code.getCondition(dim, map);
            Vector vecValue = code.getValue(dim, condition);
            //没有找到维的值
            if (value == null || value.equals(""))
            {
                //将读取到的值填写到该维对应的组合框中
                //添加一个任意值
                Value valNull = new ValueImpl();
                valNull.setValueId("*");
                valNull.setValueName("*");
                vecValue.insertElementAt(valNull, 0);
                jComboBox[i].setModel(new DefaultComboBoxModel(vecValue));
                jComboBox[i].setSelectedItem(valNull);
            }
            //已经有了上一级维的值
            else
            {
                //设置该维对应的组合框的内容
                jComboBox[i].setModel(new DefaultComboBoxModel(vecValue));
                jComboBox[i].setSelectedItem(code.getValue(type, value));
            }
        }
        //生成当前维的jComboBox
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
        //没有找到维的值
        if (value == null || value.equals(""))
        {
            //将读取到的值填写到该维对应的组合框中
            //添加一个任意值
            Value valNull = new ValueImpl();
            valNull.setValueId("*");
            valNull.setValueName("*");
            vecValue.insertElementAt(valNull, 0);
            jComboBox[0].setModel(new DefaultComboBoxModel(vecValue));
            jComboBox[0].setSelectedItem(valNull);
        }
        //已经有了上一级维的值
        else
        {
            //设置该维对应的组合框的内容
            jComboBox[0].setModel(new DefaultComboBoxModel(vecValue));
            jComboBox[0].setSelectedItem(code.getValue(type, value));
        }
        /**更新视图*/
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
        /**筛选事件*/
        if (e.getStateChange() != ItemEvent.SELECTED)
        {
            return;
        }
        //获得当前组合框对应的维和选定值
        JComboBox box = (JComboBox) e.getSource();
        String strName = box.getName();
        if (!(box.getSelectedItem() instanceof Value))
        {
            return;
        }
        Value value = (Value) box.getSelectedItem();
        Dim dimension = getDimension(strName);
        //扫描所有组合框，并找到组合框对应的维代码
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
                //添加一个任意值
                Value valNull = new ValueImpl();
                valNull.setValueId("*");
                valNull.setValueName("*");
                vecValue.insertElementAt(valNull, 0);
                jComboBox[i].setModel(new DefaultComboBoxModel(vecValue));
                jComboBox[i].setSelectedItem(valNull);
            }
        }
        //从全局变量表中读取当前维的取数条件
        //判断当前进行操作的维是否影响到扫描到的维
        //如果未影响不做任何操作
        //如果影响则先将当前操作的维和值添加到取数条件中，再读取扫描到维的值
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
                          dimension.getDimenId() + "<关系符>" + "'" +
                          value.getValueId() + "'" + "\n";
            setText(text);
        }
        if (obj instanceof String)
        {
            String value = (String) obj;
            String text = source.getDataSourceId() + SysConfig.KEYSEPARATOR +
                          dimension.getDimenId() + "<关系符>" + "'" + value + "'" +
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
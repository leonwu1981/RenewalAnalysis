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
 * 说明：(1)从数据库中读取某张报表的CellItems信息
 *      (2)将CellItems中的字段信息读入到本地相应的字符串中
 *         strSubject、strViews等。
 *      (3)拆分本地字符串，将每个字段的相关信息保存到本地的HashMap中
 *         字段->约束条件
 *         字段->列替换
 *         字段->汇总规则
 *      (4)确定时，再将HashMap中的信息保存到数据库中
 */

public class FramePlaneReport extends JDialog
{
//    /***/
//    Vector vecFields=new Vector();
//    /***/
//    /**以下这些HashMap用来存放每个字段的属性，如：中文显示、约束条件、列替换等*/
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
//    /**报表对象*/
//    private Report report=null;
//    /**代码表*/
//    private Code code=new Code();
//    /***/
//    private CellItems dbCellItems=new CellItems();
//    /***/
//    private String strRowCol="";
//    String strSubject="";
//    private String strViews="";
//    private String strReplace="";
//    private String strSumRule="";
//    private String strSumDepend="";
//    private String strWhere="";
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**控件*/
    private JPanel jPanelList = new JPanel();
    private Border border1;
    private JPanel jPanelButton = new JPanel();
    private JButton jButtonAdd = new JButton();
    private JButton jButtonEdit = new JButton();
    private JButton jButtonRemove = new JButton();
    private JButton jButtonUp = new JButton();
    private JButton jButtonDown = new JButton();
    private JScrollPane jScrollPaneDisp = new JScrollPane();
    JList jListColumn = new JList();
    private JButton jButtonOK = new JButton();


    public FramePlaneReport()
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

    public FramePlaneReport(Report report)
    {
//        super(report.mainFrame);
//        this.report=report;
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
        border1 = new TitledBorder(BorderFactory.createEmptyBorder(), "显示字段");
        this.getContentPane().setLayout(null);
        jPanelList.setFont(new java.awt.Font("DialogInput", 0, 12));
        jPanelList.setBorder(border1);
        jPanelList.setBounds(new Rectangle(23, 23, 209, 249));
        jPanelList.setLayout(null);
        jPanelButton.setBounds(new Rectangle(230, 23, 92, 249));
        jPanelButton.setLayout(null);
        jButtonAdd.setBounds(new Rectangle(6, 30, 80, 32));
        jButtonAdd.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonAdd.setActionCommand("添加");
        jButtonAdd.setText("添加...");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButtonAdd_actionPerformed(e);
            }
        });
        jButtonEdit.setBounds(new Rectangle(6, 67, 80, 32));
        jButtonEdit.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonEdit.setText("编辑...");
        jButtonEdit.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButtonEdit_actionPerformed(e);
            }
        });
        jButtonRemove.setBounds(new Rectangle(6, 103, 80, 32));
        jButtonRemove.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonRemove.setText("删除");
        jButtonRemove.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButtonRemove_actionPerformed(e);
            }
        });
        jButtonUp.setBounds(new Rectangle(6, 140, 80, 32));
        jButtonUp.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonUp.setText("上移");
        jButtonUp.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButtonUp_actionPerformed(e);
            }
        });
        jButtonDown.setBounds(new Rectangle(6, 176, 80, 32));
        jButtonDown.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonDown.setText("下移");
        jButtonDown.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButtonDown_actionPerformed(e);
            }
        });
        jScrollPaneDisp.setBounds(new Rectangle(7, 29, 190, 219));
        jListColumn.setFont(new java.awt.Font("DialogInput", 0, 12));
        jListColumn.addListSelectionListener(new javax.swing.event.
                                             ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                jListColumn_valueChanged(e);
            }
        });
        jButtonOK.setBounds(new Rectangle(6, 213, 80, 32));
        jButtonOK.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonOK.setText("确定");
        jButtonOK.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButtonOK_actionPerformed(e);
            }
        });
        this.getContentPane().add(jPanelList, null);
        jPanelList.add(jScrollPaneDisp, null);
        jScrollPaneDisp.getViewport().add(jListColumn, null);
        this.getContentPane().add(jPanelButton, null);
        jPanelButton.add(jButtonAdd, null);
        jPanelButton.add(jButtonOK, null);
        jPanelButton.add(jButtonDown, null);
        jPanelButton.add(jButtonUp, null);
        jPanelButton.add(jButtonRemove, null);
        jPanelButton.add(jButtonEdit, null);
    }

    public void init()
    {
//        /**初始化代码表*/
//        if(code.getFieldDefine().size()==0||
//           code.getTableDefine().size()==0)
//        {
//            code.getTableFields();
//        }
//        /**初始化界面*/
//        jListColumn.setListData(vecFields);
//        //只有当选中某个字段时，Edit、Remove、Up、Down按钮才可用
//        jButtonEdit.setEnabled(false);
//        jButtonDown.setEnabled(false);
//        jButtonUp.setEnabled(false);
//        jButtonRemove.setEnabled(false);
//        /**初始化数据*/
//        dbCellItems=report.dbCellItems;
//        /**将cellitems中的字段读到本地的字符串中*/
//        for(int i=0;i<dbCellItems.size();i++)
//        {
//            if(dbCellItems.at(i).getItemsId().equals(SysConfig.LRSTARTCELL))
//                strRowCol=dbCellItems.at(i).getItemsValue();
//            if(dbCellItems.at(i).getItemsId().equals(SysConfig.LRSUBJECT))
//                strSubject=dbCellItems.at(i).getItemsValue();
//            if(dbCellItems.at(i).getItemsId().equals(SysConfig.LRCOLUMNVIEWS))
//                strViews=dbCellItems.at(i).getItemsValue();
//            if(dbCellItems.at(i).getItemsId().equals(SysConfig.LRCOLUMNREPLACE))
//                strReplace=dbCellItems.at(i).getItemsValue();
//            if(dbCellItems.at(i).getItemsId().equals(SysConfig.LRSUMCOLUMNS))
//                strSumRule=dbCellItems.at(i).getItemsValue();
//            if(dbCellItems.at(i).getItemsId().equals(SysConfig.LRSUMDEPEND))
//                strSumDepend=dbCellItems.at(i).getItemsValue();
//            if(dbCellItems.at(i).getItemsId().equals(SysConfig.LRSTRWHERE))
//                strWhere=dbCellItems.at(i).getItemsValue();
//        }
//        //todo:initial HashMap
//        initRestrict();
//        initSubject();
//        initDisplay();
//        initReplace();
//        initSumRule();
//        initSumDepend();
//        initWhere();
    }

    private void initDisplay()
    {
//        for(int i=0;i<vecFields.size();i++)
//        {
//            String strTableId=strSubject;
//            String strFieldId=vecFields.elementAt(i).toString();
//            String strText=code.getFieldDefineScm(strTableId,strFieldId).getFieldName();
//            mapDisplay.put(strFieldId,strText);
//        }
    }

    /**
     * 获得所有的显示字段以及它的约束条件
     * 并初始化所有的HashMap
     */
    private void initRestrict()
    {
//        /**初始化显示字段列表框和显示字段的约束条件:字段->约束条件*/
//        if(strViews==null||strViews.equals(""))
//            return;
//        StringTokenizer token=new StringTokenizer(strViews,",");
//        /**拆分显示字段字符串*/
//        //初始化显示字段向量
//        vecFields=new Vector();
//        while(token.hasMoreTokens())
//        {
//            String strField=(String)(token.nextElement());
//            /**拆分约束条件*/
//            int intIndexLeft=strField.indexOf("(");
//            int intIndexRight=strField.indexOf(")");
//            if(intIndexLeft==-1)
//            //没有约束条件
//            {
//                vecFields.addElement(strField);
//                mapRestrict.put(strField,"");
//            }
//            else
//            //有约束条件
//            {
//                vecFields.addElement(strField.substring(intIndexLeft+1,intIndexRight));
//                mapRestrict.put(strField.substring(intIndexLeft+1,intIndexRight),
//                                strField.substring(0,intIndexLeft));
//            }
//        }
//        //刷新界面
//        jListColumn.setListData(vecFields);
//        /**初始化其他HashMap*/
//        for(int i=0;i<vecFields.size();i++)
//        {
//            mapDisplay.put(vecFields.elementAt(i).toString(),"");
//            mapReplace.put(vecFields.elementAt(i).toString(),"");
//            mapSumRule.put(vecFields.elementAt(i).toString(),"00");
//            mapSumDepend.put(vecFields.elementAt(i).toString(),"-1");
//            mapWhere.put(vecFields.elementAt(i).toString(),"where 1=1 ");
//        }
//        System.out.println(mapRestrict);
    }

    /**
     * 获得列替换字符串，并初始化存放列替换信息的HashMap
     */
    private void initReplace()
    {
//        /**初始化列替换HashMap:字段->替换列*/
//        strReplace="@0@select bm_la from ds_indemnity where segment_col=?";
//        StringTokenizer token=new StringTokenizer(strReplace,"|");
//        if(strReplace==null||strReplace.equals(""))
//            return;
//        /**拆分列替换字符串*/
//        while(token.hasMoreTokens())
//        {
//            String strReplaceRule=(String)(token.nextElement());
//            //拆分被替换字段
//            int intBegin=strReplace.indexOf("@",0);
//            int intEnd=strReplace.indexOf("@",1);
//            String strIndex=strReplace.substring(intBegin+1,intEnd);
//            String strReplaced=vecFields.elementAt(Integer.parseInt(strIndex)).toString();
//            //拆分替换的表
//            int intSelect=strReplace.indexOf("select");
//            int intFrom=strReplace.indexOf("from");
//            String strFieldId=strReplace.substring(intSelect+6,intFrom).trim();
//            //拆分替换的字段
//            int intWhere=strReplace.indexOf("where");
//            //形成HashMap
//            String strTableId=strReplace.substring(intFrom+4,intWhere).trim();
//            String strTableField=strTableId+"."+strFieldId;
//            mapReplace.put(strReplaced,strTableField);
//        }
//        System.out.println(mapReplace);
    }

    /**
     * 获得汇总规则字符串，并初始化存放汇总规则信息的HashMap
     */
    private void initSumRule()
    {
//        /**初始化汇总规则HashMap:字段->汇总规则*/
//        strSumRule="01,2|1|0";
//        if(strSumRule==null||strSumRule.equals(""))
//            return;
//        int intIndex=strSumRule.indexOf(",");
//        String strRule=strSumRule.substring(0,intIndex);
//        String strField=strSumRule.substring(intIndex+1);
//        StringTokenizer token=new StringTokenizer(strField,"|");
//        /**拆分汇总规则字符串，分别关联每个字段的汇总规则*/
//        Vector vecField=new Vector();
//        while(token.hasMoreTokens())
//            vecField.addElement(token.nextElement());
//        //根据汇总规则字符串的格式可知vecSumRule的第一个元素是汇总规则
//        for(int i=0;i<vecField.size();i++)
//        {
//            String strSum=(String)(vecField.elementAt(i));
//            int intColumn=Integer.parseInt(strSum);
//            String strColumn=(String)(vecFields.elementAt(intColumn));
//            mapSumRule.put(strColumn,strRule);
//        }
//        System.out.println(mapSumRule);
    }

    /**
     * 获得汇总依据字符串，并初始化存放汇总依据信息的HashMap
     */
    private void initSumDepend()
    {
//        /**初始化汇总依据HashMap:字段->汇总依据*/
//        strSumDepend="1,2,-1";
//        StringTokenizer token=new StringTokenizer(strSumDepend,",");
//        if(strSumDepend==null||strSumDepend.equals(""))
//            return;
//        /**拆分汇总依据字符串，分别关联每个字段的汇总依据*/
//        int intIndex=0;
//        while(token.hasMoreTokens())
//        {
//            String strDepend=(String)(token.nextElement());
//            String strColumn=(String)(vecFields.elementAt(intIndex));
//            mapSumDepend.put(strColumn,strDepend);
//            intIndex++;
//        }
//
//        System.out.println(mapSumDepend);
    }

    /**
     * 获得取数条件字符串，并初始化存放取数条件信息的HashMap
     */
    private void initWhere()
    {
//        /**初始化条件HashMap:字段->条件语句*/
//        strWhere="where 1=1\nand segment_value.segment_col=1";
//        if(strWhere==null||strWhere.equals(""))
//            return;
//        /**将本张报表的条件语句与所有的字段关联*/
//        for(int i=0;i<vecFields.size();i++)
//        {
//            String strColumn=(String)(vecFields.elementAt(i));
//            mapWhere.put(strColumn,strWhere);
//        }
//        System.out.println(mapWhere);
    }

    private void initSubject()
    {
//        /**初始化主题HashMap:字段->主题*/
//        if(strSubject==null||strSubject.equals(""))
//            return;
//        /**将本张报表的主题于所有字段关联*/
//        for(int i=0;i<vecFields.size();i++)
//        {
//            String strColumn=(String)(vecFields.elementAt(i));
//            mapSubject.put(strColumn,strSubject);
//        }
//        System.out.println(mapSubject);
    }

    public static void main(String args[])
    {
//        FrameDealList frame=new FrameDealList(new Report());
//        frame.setSize(350,350);
//        frame.init();
//        frame.show();

    }

    void jButtonRemove_actionPerformed(ActionEvent e)
    {
//        /**删除被选择的项目*/
//        int intSelected=jListColumn.getSelectedIndex();
//        if(intSelected==-1)
//            return;
//        /**删除显示字段时顺便在HashMap删除该字段的信息*/
//        String strKey=(String)(vecFields.elementAt(intSelected));
//        mapDisplay.remove(strKey);
//        mapRestrict.remove(strKey);
//        mapReplace.remove(strKey);
//        mapSumRule.remove(strKey);
//        mapSumDepend.remove(strKey);
//        mapWhere.remove(strKey);
//        /**更新列表框*/
//        vecFields.remove(intSelected);
//        jListColumn.setListData(vecFields);
//        /**设置控件状态*/
//        if(vecFields.size()==0)
//        {
//            jButtonEdit.setEnabled(false);
//            jButtonDown.setEnabled(false);
//            jButtonUp.setEnabled(false);
//            jButtonRemove.setEnabled(false);
//        }
    }

    void jButtonUp_actionPerformed(ActionEvent e)
    {
//        int intSelected=jListColumn.getSelectedIndex();
//        /**将被选择的项目上移*/
//        if(intSelected==-1||intSelected==0)
//            return;
//        Object obj=vecFields.remove(intSelected);
//        vecFields.insertElementAt(obj,intSelected-1);
//        jListColumn.setListData(vecFields);
//        jListColumn.setSelectedValue(obj,true);
    }

    void jButtonDown_actionPerformed(ActionEvent e)
    {
//        int intSelected=jListColumn.getSelectedIndex();
//        /**将被选择的项目下移*/
//        if(intSelected==-1||intSelected==vecFields.size()-1)
//            return;
//        Object obj=vecFields.remove(intSelected);
//        vecFields.insertElementAt(obj,intSelected+1);
//        jListColumn.setListData(vecFields);
//        jListColumn.setSelectedValue(obj,true);
    }

    void jButtonAdd_actionPerformed(ActionEvent e)
    {
//        /**添加新字段*/
//        FrameAddColumn frmAddColumn=new FrameAddColumn(this);
//        frmAddColumn.setSize(350,500);
//        frmAddColumn.init();
//        frmAddColumn.show();
    }

    void jButtonEdit_actionPerformed(ActionEvent e)
    {
//        if(vecFields.size()==0)
//            return;
//        if(jListColumn.getSelectedIndex()==-1)
//            return;
//        /**编辑字段属性*/
//        FrameEditColumn frmEditColumn=new FrameEditColumn(this);
//        frmEditColumn.setSize(630,450);
//        frmEditColumn.init();
//        frmEditColumn.show();
    }

    void jListColumn_valueChanged(ListSelectionEvent e)
    {
        /**当选中了某个字段时调整控件的状态*/
        if (jListColumn.getSelectedIndex() == -1)
        {
            return;
        }
        jButtonEdit.setEnabled(true);
        jButtonRemove.setEnabled(true);
        jButtonDown.setEnabled(true);
        jButtonUp.setEnabled(true);
    }

    private void setViews()
    {
//        /**约束条件*/
//        /**显示字段*/
//        String strViews="";
//        for(int i=0;i<vecFields.size();i++)
//        {
//            String strView=vecFields.elementAt(i).toString();
//            //读取每个字段的约束条件，并用它来修饰要显示的列
//            Object valueRestrict=mapRestrict.get(strView);
//            if(valueRestrict==null||valueRestrict.equals(""))
//                strViews=strViews+","+strView;
//            else
//                strViews=strViews+","+valueRestrict.toString()+"("+strView+")";
//            Object valueSubject=mapSubject.get(strView);
//            if(valueSubject!=null)
//                strSubject=valueSubject.toString();
//        }
//        System.out.println(strSubject);
//        System.out.println(strViews.substring(1));
//        //保存显示字段
//        CellItemsScm scmCellItems=new CellItemsScm();
//        scmCellItems.setBranchId(report.scmReportMain.getBranchId());
//        scmCellItems.setReportId(report.scmReportMain.getReportId());
//        scmCellItems.setReportEdition(report.scmReportMain.getReportEdition());
//        scmCellItems.setRowPosition("-1");
//        scmCellItems.setColPosition("-1");
//        scmCellItems.setItemsId(SysConfig.LRCOLUMNVIEWS);
//        scmCellItems.setItemsValue(strViews.substring(1));
//        dbCellItems.add(scmCellItems);
//        //保存主题
//        scmCellItems=new CellItemsScm();
//        scmCellItems.setBranchId(report.scmReportMain.getBranchId());
//        scmCellItems.setReportId(report.scmReportMain.getReportId());
//        scmCellItems.setReportEdition(report.scmReportMain.getReportEdition());
//        scmCellItems.setRowPosition("-1");
//        scmCellItems.setColPosition("-1");
//        scmCellItems.setItemsId(SysConfig.LRSUBJECT);
//        scmCellItems.setItemsValue(strSubject);
//        dbCellItems.add(scmCellItems);
//        //保存起始行列
//        scmCellItems=new CellItemsScm();
//        scmCellItems.setBranchId(report.scmReportMain.getBranchId());
//        scmCellItems.setReportId(report.scmReportMain.getReportId());
//        scmCellItems.setReportEdition(report.scmReportMain.getReportEdition());
//        scmCellItems.setRowPosition("-1");
//        scmCellItems.setColPosition("-1");
//        scmCellItems.setItemsId(SysConfig.LRSTARTCELL);
//        scmCellItems.setItemsValue("A1");
//        report.dbCellItems.add(scmCellItems);
    }

    private void setReplace()
    {
//        CellItemsScm scmCellItems=new CellItemsScm();
        /**列替换语句*/
//        String strReplaceSql="";
//        Object[] keyReplace=mapReplace.keySet().toArray();
//        for(int i=0;i<keyReplace.length;i++)
//        {
//            //读取每个字段的列替换字段，并拼制列替换语句
//            Object valueReplace=mapReplace.get(keyReplace[i].toString());
//            if(valueReplace==null||valueReplace.equals(""))
//               continue;
//            int intIndex=keyReplace[i].toString().indexOf(".");
//            String strReplaced=keyReplace[i].toString().substring(intIndex+1);
//            intIndex=valueReplace.toString().indexOf(".");
//            String strTable=valueReplace.toString().substring(0,intIndex);
//            String strReplace=valueReplace.toString().substring(intIndex+1);
//            strReplaceSql=strReplaceSql+"|"+"@"+vecFields.indexOf(keyReplace[i].toString())+"@"+
//                          "select "+strReplace+" from "+strTable+
//                          " where "+strReplaced+"=?";
//        }
//        if(!strReplaceSql.equals(""))
//        {
//            System.out.println(strReplaceSql.substring(1));
//            //保存列替换规则
//            scmCellItems=new CellItemsScm();
//            scmCellItems.setBranchId(report.scmReportMain.getBranchId());
//            scmCellItems.setReportId(report.scmReportMain.getReportId());
//            scmCellItems.setReportEdition(report.scmReportMain.getReportEdition());
//            scmCellItems.setRowPosition("-1");
//            scmCellItems.setColPosition("-1");
//            scmCellItems.setItemsId(SysConfig.LRCOLUMNREPLACE);
//            scmCellItems.setItemsValue(strReplaceSql.substring(1));
//            dbCellItems.add(scmCellItems);
//        }
    }

    private void setSumRule()
    {
//        CellItemsScm scmCellItems=new CellItemsScm();
//        /**汇总字段*/
//        //格式：汇总规则|要汇总的列|要汇总的列|……
//        String strSum="";
//        Object[] keySum=mapSumRule.keySet().toArray();
//        for(int i=0;i<keySum.length;i++)
//        {
//            //读取每个字段的汇总规则
//            Object valueSum=mapSumRule.get(keySum[i].toString());
//            if(valueSum==null||valueSum.equals(""))
//                continue;
//            String strSumField=keySum[i].toString();
//            strSum=strSum+"|"+vecFields.indexOf(strSumField);
//            if(i==keySum.length-1)
//                strSum=valueSum.toString()+","+strSum.substring(1);
//        }
//        if(!strSum.equals(""))
//        {
//            System.out.println(strSum);
//            //保存汇总规则
//            scmCellItems=new CellItemsScm();
//            scmCellItems.setBranchId(report.scmReportMain.getBranchId());
//            scmCellItems.setReportId(report.scmReportMain.getReportId());
//            scmCellItems.setReportEdition(report.scmReportMain.getReportEdition());
//            scmCellItems.setRowPosition("-1");
//            scmCellItems.setColPosition("-1");
//            scmCellItems.setItemsId(SysConfig.LRSUMCOLUMNS);
//            scmCellItems.setItemsValue(strSum);
//            dbCellItems.add(scmCellItems);
//        }
    }

    private void setSumDepend()
    {
//        CellItemsScm scmCellItems=new CellItemsScm();
//        /**汇总依据字段*/
//        String strSumDepend="";
//        Object[] keyDepend=mapSumDepend.keySet().toArray();
//        for(int i=0;i<vecFields.size();i++)
//            for(int j=0;j<keyDepend.length;j++)
//        {
//            if(vecFields.elementAt(i).toString().equals(keyDepend[j].toString()))
//            {
//                //读取每个字段的汇总依据字段,并拼制汇总依据字符串
//                Object valueDepend=mapSumDepend.get(keyDepend[j].toString());
//                if(valueDepend==null||valueDepend.equals(""))
//                    continue;
//                String strSumField=keyDepend[j].toString();
//                strSumDepend=strSumDepend+","+valueDepend.toString();
//            }
//        }
//        if(!strSumDepend.equals(""))
//        {
//            System.out.println(strSumDepend.substring(1));
//            //保存汇总依据
//            scmCellItems=new CellItemsScm();
//            scmCellItems.setBranchId(report.scmReportMain.getBranchId());
//            scmCellItems.setReportId(report.scmReportMain.getReportId());
//            scmCellItems.setReportEdition(report.scmReportMain.getReportEdition());
//            scmCellItems.setRowPosition("-1");
//            scmCellItems.setColPosition("-1");
//            scmCellItems.setItemsId(SysConfig.LRSUMDEPEND);
//            scmCellItems.setItemsValue(strSumDepend.substring(1));
//            dbCellItems.add(scmCellItems);
//        }
    }

    private void setWhere()
    {
//        CellItemsScm scmCellItems=new CellItemsScm();
//        /**条件语句*/
//        String strWhere="";
//        Object[] keyWhere=mapWhere.keySet().toArray();
//        for(int i=0;i<keyWhere.length;i++)
//        {
//            //读取条件语句并显示
//            Object valueWhere=mapWhere.get(keyWhere[i].toString());
//            if(valueWhere==null||valueWhere.equals(""))
//                continue;
//            strWhere=valueWhere.toString();
//            if(!strWhere.equals(""))
//                break;
//        }
//        System.out.println(strWhere);
//        //保存条件语句
//        scmCellItems=new CellItemsScm();
//        scmCellItems.setBranchId(report.scmReportMain.getBranchId());
//        scmCellItems.setReportId(report.scmReportMain.getReportId());
//        scmCellItems.setReportEdition(report.scmReportMain.getReportEdition());
//        scmCellItems.setRowPosition("-1");
//        scmCellItems.setColPosition("-1");
//        scmCellItems.setItemsId(SysConfig.LRSTRWHERE);
//        scmCellItems.setItemsValue(strWhere);
//        dbCellItems.add(scmCellItems);
    }

    private void setFormat()
    {
//        try
//        {
//            int intStartRow=report.mainFrame.jReport.getActiveRow();
//            int intStartCol=report.mainFrame.jReport.getActiveCol();
//            for(int i=0;i<vecFields.size();i++)
//            {
//                String strViewId=vecFields.elementAt(i).toString();
//                Object valueDisplay=mapDisplay.get(strViewId);
//                if(valueDisplay!=null&&!valueDisplay.equals(""))
//                {
//                    report.mainFrame.jReport.setText(0,intStartRow,intStartCol+i,
//                            (String)valueDisplay);
//                    report.mainFrame.jReport.setText(1,intStartRow,intStartCol+i,
//                            strViewId);
//                }
//            }
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
    }

    void jButtonOK_actionPerformed(ActionEvent e)
    {
//        if(vecFields.size()==0)
//            return;
//        dbCellItems.clear();
//        setViews();
//        setReplace();
//        setSumRule();
//        setSumDepend();
//        setWhere();
//        report.dbCellItems=dbCellItems;
//        setFormat();
//        this.dispose();
    }
}

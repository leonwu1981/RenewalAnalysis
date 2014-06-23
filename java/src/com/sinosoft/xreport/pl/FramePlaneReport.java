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
 * ˵����(1)�����ݿ��ж�ȡĳ�ű����CellItems��Ϣ
 *      (2)��CellItems�е��ֶ���Ϣ���뵽������Ӧ���ַ�����
 *         strSubject��strViews�ȡ�
 *      (3)��ֱ����ַ�������ÿ���ֶε������Ϣ���浽���ص�HashMap��
 *         �ֶ�->Լ������
 *         �ֶ�->���滻
 *         �ֶ�->���ܹ���
 *      (4)ȷ��ʱ���ٽ�HashMap�е���Ϣ���浽���ݿ���
 */

public class FramePlaneReport extends JDialog
{
//    /***/
//    Vector vecFields=new Vector();
//    /***/
//    /**������ЩHashMap�������ÿ���ֶε����ԣ��磺������ʾ��Լ�����������滻��*/
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
//    /**�������*/
//    private Report report=null;
//    /**�����*/
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
    /**�ؼ�*/
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
        border1 = new TitledBorder(BorderFactory.createEmptyBorder(), "��ʾ�ֶ�");
        this.getContentPane().setLayout(null);
        jPanelList.setFont(new java.awt.Font("DialogInput", 0, 12));
        jPanelList.setBorder(border1);
        jPanelList.setBounds(new Rectangle(23, 23, 209, 249));
        jPanelList.setLayout(null);
        jPanelButton.setBounds(new Rectangle(230, 23, 92, 249));
        jPanelButton.setLayout(null);
        jButtonAdd.setBounds(new Rectangle(6, 30, 80, 32));
        jButtonAdd.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonAdd.setActionCommand("���");
        jButtonAdd.setText("���...");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButtonAdd_actionPerformed(e);
            }
        });
        jButtonEdit.setBounds(new Rectangle(6, 67, 80, 32));
        jButtonEdit.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonEdit.setText("�༭...");
        jButtonEdit.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButtonEdit_actionPerformed(e);
            }
        });
        jButtonRemove.setBounds(new Rectangle(6, 103, 80, 32));
        jButtonRemove.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonRemove.setText("ɾ��");
        jButtonRemove.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButtonRemove_actionPerformed(e);
            }
        });
        jButtonUp.setBounds(new Rectangle(6, 140, 80, 32));
        jButtonUp.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonUp.setText("����");
        jButtonUp.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButtonUp_actionPerformed(e);
            }
        });
        jButtonDown.setBounds(new Rectangle(6, 176, 80, 32));
        jButtonDown.setFont(new java.awt.Font("DialogInput", 0, 12));
        jButtonDown.setText("����");
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
        jButtonOK.setText("ȷ��");
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
//        /**��ʼ�������*/
//        if(code.getFieldDefine().size()==0||
//           code.getTableDefine().size()==0)
//        {
//            code.getTableFields();
//        }
//        /**��ʼ������*/
//        jListColumn.setListData(vecFields);
//        //ֻ�е�ѡ��ĳ���ֶ�ʱ��Edit��Remove��Up��Down��ť�ſ���
//        jButtonEdit.setEnabled(false);
//        jButtonDown.setEnabled(false);
//        jButtonUp.setEnabled(false);
//        jButtonRemove.setEnabled(false);
//        /**��ʼ������*/
//        dbCellItems=report.dbCellItems;
//        /**��cellitems�е��ֶζ������ص��ַ�����*/
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
     * ������е���ʾ�ֶ��Լ�����Լ������
     * ����ʼ�����е�HashMap
     */
    private void initRestrict()
    {
//        /**��ʼ����ʾ�ֶ��б�����ʾ�ֶε�Լ������:�ֶ�->Լ������*/
//        if(strViews==null||strViews.equals(""))
//            return;
//        StringTokenizer token=new StringTokenizer(strViews,",");
//        /**�����ʾ�ֶ��ַ���*/
//        //��ʼ����ʾ�ֶ�����
//        vecFields=new Vector();
//        while(token.hasMoreTokens())
//        {
//            String strField=(String)(token.nextElement());
//            /**���Լ������*/
//            int intIndexLeft=strField.indexOf("(");
//            int intIndexRight=strField.indexOf(")");
//            if(intIndexLeft==-1)
//            //û��Լ������
//            {
//                vecFields.addElement(strField);
//                mapRestrict.put(strField,"");
//            }
//            else
//            //��Լ������
//            {
//                vecFields.addElement(strField.substring(intIndexLeft+1,intIndexRight));
//                mapRestrict.put(strField.substring(intIndexLeft+1,intIndexRight),
//                                strField.substring(0,intIndexLeft));
//            }
//        }
//        //ˢ�½���
//        jListColumn.setListData(vecFields);
//        /**��ʼ������HashMap*/
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
     * ������滻�ַ���������ʼ��������滻��Ϣ��HashMap
     */
    private void initReplace()
    {
//        /**��ʼ�����滻HashMap:�ֶ�->�滻��*/
//        strReplace="@0@select bm_la from ds_indemnity where segment_col=?";
//        StringTokenizer token=new StringTokenizer(strReplace,"|");
//        if(strReplace==null||strReplace.equals(""))
//            return;
//        /**������滻�ַ���*/
//        while(token.hasMoreTokens())
//        {
//            String strReplaceRule=(String)(token.nextElement());
//            //��ֱ��滻�ֶ�
//            int intBegin=strReplace.indexOf("@",0);
//            int intEnd=strReplace.indexOf("@",1);
//            String strIndex=strReplace.substring(intBegin+1,intEnd);
//            String strReplaced=vecFields.elementAt(Integer.parseInt(strIndex)).toString();
//            //����滻�ı�
//            int intSelect=strReplace.indexOf("select");
//            int intFrom=strReplace.indexOf("from");
//            String strFieldId=strReplace.substring(intSelect+6,intFrom).trim();
//            //����滻���ֶ�
//            int intWhere=strReplace.indexOf("where");
//            //�γ�HashMap
//            String strTableId=strReplace.substring(intFrom+4,intWhere).trim();
//            String strTableField=strTableId+"."+strFieldId;
//            mapReplace.put(strReplaced,strTableField);
//        }
//        System.out.println(mapReplace);
    }

    /**
     * ��û��ܹ����ַ���������ʼ����Ż��ܹ�����Ϣ��HashMap
     */
    private void initSumRule()
    {
//        /**��ʼ�����ܹ���HashMap:�ֶ�->���ܹ���*/
//        strSumRule="01,2|1|0";
//        if(strSumRule==null||strSumRule.equals(""))
//            return;
//        int intIndex=strSumRule.indexOf(",");
//        String strRule=strSumRule.substring(0,intIndex);
//        String strField=strSumRule.substring(intIndex+1);
//        StringTokenizer token=new StringTokenizer(strField,"|");
//        /**��ֻ��ܹ����ַ������ֱ����ÿ���ֶεĻ��ܹ���*/
//        Vector vecField=new Vector();
//        while(token.hasMoreTokens())
//            vecField.addElement(token.nextElement());
//        //���ݻ��ܹ����ַ����ĸ�ʽ��֪vecSumRule�ĵ�һ��Ԫ���ǻ��ܹ���
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
     * ��û��������ַ���������ʼ����Ż���������Ϣ��HashMap
     */
    private void initSumDepend()
    {
//        /**��ʼ����������HashMap:�ֶ�->��������*/
//        strSumDepend="1,2,-1";
//        StringTokenizer token=new StringTokenizer(strSumDepend,",");
//        if(strSumDepend==null||strSumDepend.equals(""))
//            return;
//        /**��ֻ��������ַ������ֱ����ÿ���ֶεĻ�������*/
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
     * ���ȡ�������ַ���������ʼ�����ȡ��������Ϣ��HashMap
     */
    private void initWhere()
    {
//        /**��ʼ������HashMap:�ֶ�->�������*/
//        strWhere="where 1=1\nand segment_value.segment_col=1";
//        if(strWhere==null||strWhere.equals(""))
//            return;
//        /**�����ű����������������е��ֶι���*/
//        for(int i=0;i<vecFields.size();i++)
//        {
//            String strColumn=(String)(vecFields.elementAt(i));
//            mapWhere.put(strColumn,strWhere);
//        }
//        System.out.println(mapWhere);
    }

    private void initSubject()
    {
//        /**��ʼ������HashMap:�ֶ�->����*/
//        if(strSubject==null||strSubject.equals(""))
//            return;
//        /**�����ű���������������ֶι���*/
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
//        /**ɾ����ѡ�����Ŀ*/
//        int intSelected=jListColumn.getSelectedIndex();
//        if(intSelected==-1)
//            return;
//        /**ɾ����ʾ�ֶ�ʱ˳����HashMapɾ�����ֶε���Ϣ*/
//        String strKey=(String)(vecFields.elementAt(intSelected));
//        mapDisplay.remove(strKey);
//        mapRestrict.remove(strKey);
//        mapReplace.remove(strKey);
//        mapSumRule.remove(strKey);
//        mapSumDepend.remove(strKey);
//        mapWhere.remove(strKey);
//        /**�����б��*/
//        vecFields.remove(intSelected);
//        jListColumn.setListData(vecFields);
//        /**���ÿؼ�״̬*/
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
//        /**����ѡ�����Ŀ����*/
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
//        /**����ѡ�����Ŀ����*/
//        if(intSelected==-1||intSelected==vecFields.size()-1)
//            return;
//        Object obj=vecFields.remove(intSelected);
//        vecFields.insertElementAt(obj,intSelected+1);
//        jListColumn.setListData(vecFields);
//        jListColumn.setSelectedValue(obj,true);
    }

    void jButtonAdd_actionPerformed(ActionEvent e)
    {
//        /**������ֶ�*/
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
//        /**�༭�ֶ�����*/
//        FrameEditColumn frmEditColumn=new FrameEditColumn(this);
//        frmEditColumn.setSize(630,450);
//        frmEditColumn.init();
//        frmEditColumn.show();
    }

    void jListColumn_valueChanged(ListSelectionEvent e)
    {
        /**��ѡ����ĳ���ֶ�ʱ�����ؼ���״̬*/
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
//        /**Լ������*/
//        /**��ʾ�ֶ�*/
//        String strViews="";
//        for(int i=0;i<vecFields.size();i++)
//        {
//            String strView=vecFields.elementAt(i).toString();
//            //��ȡÿ���ֶε�Լ��������������������Ҫ��ʾ����
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
//        //������ʾ�ֶ�
//        CellItemsScm scmCellItems=new CellItemsScm();
//        scmCellItems.setBranchId(report.scmReportMain.getBranchId());
//        scmCellItems.setReportId(report.scmReportMain.getReportId());
//        scmCellItems.setReportEdition(report.scmReportMain.getReportEdition());
//        scmCellItems.setRowPosition("-1");
//        scmCellItems.setColPosition("-1");
//        scmCellItems.setItemsId(SysConfig.LRCOLUMNVIEWS);
//        scmCellItems.setItemsValue(strViews.substring(1));
//        dbCellItems.add(scmCellItems);
//        //��������
//        scmCellItems=new CellItemsScm();
//        scmCellItems.setBranchId(report.scmReportMain.getBranchId());
//        scmCellItems.setReportId(report.scmReportMain.getReportId());
//        scmCellItems.setReportEdition(report.scmReportMain.getReportEdition());
//        scmCellItems.setRowPosition("-1");
//        scmCellItems.setColPosition("-1");
//        scmCellItems.setItemsId(SysConfig.LRSUBJECT);
//        scmCellItems.setItemsValue(strSubject);
//        dbCellItems.add(scmCellItems);
//        //������ʼ����
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
        /**���滻���*/
//        String strReplaceSql="";
//        Object[] keyReplace=mapReplace.keySet().toArray();
//        for(int i=0;i<keyReplace.length;i++)
//        {
//            //��ȡÿ���ֶε����滻�ֶΣ���ƴ�����滻���
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
//            //�������滻����
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
//        /**�����ֶ�*/
//        //��ʽ�����ܹ���|Ҫ���ܵ���|Ҫ���ܵ���|����
//        String strSum="";
//        Object[] keySum=mapSumRule.keySet().toArray();
//        for(int i=0;i<keySum.length;i++)
//        {
//            //��ȡÿ���ֶεĻ��ܹ���
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
//            //������ܹ���
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
//        /**���������ֶ�*/
//        String strSumDepend="";
//        Object[] keyDepend=mapSumDepend.keySet().toArray();
//        for(int i=0;i<vecFields.size();i++)
//            for(int j=0;j<keyDepend.length;j++)
//        {
//            if(vecFields.elementAt(i).toString().equals(keyDepend[j].toString()))
//            {
//                //��ȡÿ���ֶεĻ��������ֶ�,��ƴ�ƻ��������ַ���
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
//            //�����������
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
//        /**�������*/
//        String strWhere="";
//        Object[] keyWhere=mapWhere.keySet().toArray();
//        for(int i=0;i<keyWhere.length;i++)
//        {
//            //��ȡ������䲢��ʾ
//            Object valueWhere=mapWhere.get(keyWhere[i].toString());
//            if(valueWhere==null||valueWhere.equals(""))
//                continue;
//            strWhere=valueWhere.toString();
//            if(!strWhere.equals(""))
//                break;
//        }
//        System.out.println(strWhere);
//        //�����������
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

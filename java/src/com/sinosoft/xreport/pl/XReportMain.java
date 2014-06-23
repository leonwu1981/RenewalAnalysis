package com.sinosoft.xreport.pl;

import java.awt.BorderLayout;
import java.awt.Dimension;
//import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.*;

//import javax.swing.UIManager;
import com.f1j.swing.EndEditEvent;
import com.f1j.swing.JBook;
import com.sinosoft.xreport.util.IconBuilder;

/**
 * <p>Title: XReport 1.0 (c)Sinosoft 2003</p>
 * <p>Description: XReport������</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: sinosoft</p>
 * @author lixy
 * @version 1.0
 */

public class XReportMain extends JFrame
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**����ؼ�*/
    private JBook jReport = new Report();
    /**�������*/
    private Report report;

    private IconBuilder ib = IconBuilder.getInstance();
    //���ӻ�����Զ����ɴ��뿪ʼ
    private JMenuBar jMenuDefine = new JMenuBar();
    private JMenu jMenuDefineReport = new JMenu();
    private JMenuItem jMenuCloseDef = new JMenuItem();
    private JMenuItem jMenuSaveDef = new JMenuItem();
    private JMenuItem jMenuManageReport = new JMenuItem();
    private JMenuItem jMenuExitDef = new JMenuItem();
    private JMenu jMenuFormat = new JMenu();
    private JMenuItem jMenuDataFormat = new JMenuItem();
    private JMenuItem jMenuHeight = new JMenuItem();
    private JMenuItem jMenuAlign = new JMenuItem();
    private JMenuItem jMenuBorder = new JMenuItem();
    private JMenuItem jMenuFont = new JMenuItem();
    private JMenuItem jMenuCancelHead = new JMenuItem();
    private JMenuItem jMenuHead = new JMenuItem();
    private JMenu jMenuEdit = new JMenu();
    private JMenuItem jMenuCanelFix = new JMenuItem();
    private JMenuItem jMenuFix = new JMenuItem();
    private JMenuItem jMenuPaste = new JMenuItem();
    private JMenuItem jMenuCut = new JMenuItem();
    private JMenuItem jMenuCopy = new JMenuItem();
    private JMenu jMenuInsert = new JMenu();
    private JMenuItem jMenuDown = new JMenuItem();
    private JMenuItem jMenuRight = new JMenuItem();
    private JMenuItem jMenuInsertCol = new JMenuItem();
    private JMenuItem jMenuInsertRow = new JMenuItem();
    private JMenu jMenuDelete = new JMenu();
    private JMenuItem jMenuUp = new JMenuItem();
    private JMenuItem jMenuLeft = new JMenuItem();
    private JMenuItem jMenuDeleteCol = new JMenuItem();
    private JMenuItem jMenuDeleteRow = new JMenuItem();
    private JMenu jMenuInsertPage = new JMenu();
    private JMenuItem jMenuColPage = new JMenuItem();
    private JMenuItem jMenuRowPage = new JMenuItem();
    private JMenu jMenuDeletePage = new JMenu();
    private JMenuItem jMenuItem6 = new JMenuItem();
    private JMenuItem jMenuItem7 = new JMenuItem();
    private JMenu jMenuMyDefine = new JMenu();
    private JMenuItem jMenuFun = new JMenuItem();
    private JMenuItem jMenuReplace = new JMenuItem();
    private JMenu jMenuPrint = new JMenu();
    private JMenuItem jMenuPrintSum = new JMenuItem();
    private JMenuItem jMenuPrintThis = new JMenuItem();
    private JMenu jMenuReportDisp = new JMenu();
    private JMenuItem jMenuOpenSumData = new JMenuItem();
    private JMenuItem jMenuOpenSubData = new JMenuItem();
    private JMenuItem jMenuOpenReportData = new JMenuItem();
    private JMenuItem jMenuCloseThis = new JMenuItem();
    private JMenuItem jMenuCloseAll = new JMenuItem();
    private JPopupMenu pmuCell = new JPopupMenu();
    private JMenuItem jMenuItemPopCopy = new JMenuItem();
    private JMenuItem jMenuItemPopCut = new JMenuItem();
    private JMenuItem jMenuItemPopPaste = new JMenuItem();
    private JMenuItem jMenuItemPopFormat = new JMenuItem();
    private JMenuItem jMenuSaveThis = new JMenuItem();
    private JMenuItem jMenuSaveAll = new JMenuItem();
    private JMenuItem jMenuPageSet = new JMenuItem();
    private JMenuItem jMenuItemMerge = new JMenuItem();
    private JMenu jMenuFormula = new JMenu();
    private JMenuItem jMenuItemSum = new JMenuItem();
    private JMenuItem jMenuItemSum2 = new JMenuItem();
    private JMenu jMenuPopInsert = new JMenu();
    private JMenuItem jMenuPopInsertRow = new JMenuItem();
    private JMenuItem jMenuPopInsertCol = new JMenuItem();
    private JMenu jMenuPopDelete = new JMenu();
    private JMenuItem jMenuPopDeleteCol = new JMenuItem();
    private JMenuItem jMenuPopDeleteRow = new JMenuItem();
    private JToolBar jToolBarMain = new JToolBar();
    private JButton jBCut = new JButton();
    private JButton jBSaveDef = new JButton();
    private JButton jBCopy = new JButton();
    private JButton jBNewReport = new JButton();
    private JButton jBCloseDef = new JButton();
    private JButton jBOpenDef = new JButton();
    private JMenuItem jMenuDispSet = new JMenuItem();
    private JMenuItem jMenuItemRc = new JMenuItem();
    private BorderLayout borderLayout = new BorderLayout();
    private JMenuItem jMenuSelectSource = new JMenuItem();
    private JMenuItem jMenuDefineRc = new JMenuItem();
    private JButton jSep1 = new JButton();
    private JButton jBPaste = new JButton();
    private JButton jSep3 = new JButton();
    private JButton jSep2 = new JButton();
    private JButton jBBorder = new JButton();
    private JButton jBJoin = new JButton();
    private JButton jBRight = new JButton();
    private JButton jBCenter = new JButton();
    private JButton jBLeft = new JButton();
    private JMenuItem jMenuCalcu = new JMenuItem();
    private JLabel jStatus = new JLabel();
    private JButton jBSource = new JButton();
    private JButton jSep4 = new JButton();
    private JMenuItem jMenuGlobal = new JMenuItem();
    private JButton jBDelete = new JButton();
    private JMenuItem jMenuParams = new JMenuItem();
    private JMenuItem jMenuHideRow = new JMenuItem();
    private JMenuItem jMenuHideCol = new JMenuItem();
    private JMenu jMenuWin = new JMenu();
    private JMenuItem jMenuChangeWin = new JMenuItem();
    private JMenu jMenuHelp = new JMenu();
    private JMenuItem jMenuAbout = new JMenuItem();
    private JMenuItem jMenuSubject = new JMenuItem();
    private JMenuItem jMenuVar = new JMenuItem();
    private JMenuItem jMenuDisp = new JMenuItem();
    //���ӻ�������ɴ������


    public XReportMain()
    {
        try
        {
            /**�����������ͱ���ؼ�*/
            report = (Report) jReport;
            /**�����ʼ��*/
            jbInit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public Report getReport()
    {
        return report;
    }

    private void jbInit() throws Exception
    {
        //���ӻ�����Զ����ɴ��뿪ʼ
        jSep4.setBorder(null);
        jSep4.setMaximumSize(new Dimension(5, 5));
        jSep4.setMinimumSize(new Dimension(5, 5));
        jSep4.setPreferredSize(new Dimension(5, 5));
        this.setFont(new java.awt.Font("DialogInput", 0, 12));
        this.setJMenuBar(jMenuDefine);
        this.getContentPane().setLayout(borderLayout);
        jMenuDefineReport.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuDefineReport.setText("������");
        jMenuManageReport.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuManageReport.setText("�������");
        jMenuManageReport.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                manageReport();
            }
        });
        jMenuSaveDef.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuSaveDef.setText("���汨����");
        jMenuSaveDef.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuSaveDefine();
            }
        });
        jMenuCloseDef.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuCloseDef.setText("�رձ���");
        jMenuCloseDef.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuColseReport();
            }
        });
        jMenuExitDef.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuExitDef.setText("����");
        jMenuFormat.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuFormat.setText("��ʽ");
        jMenuFont.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuFont.setText("����");
        jMenuFont.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuFont();
            }
        });
        jMenuBorder.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuBorder.setText("�߿�");
        jMenuBorder.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuBorder();
            }
        });
        jMenuAlign.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuAlign.setText("����");
        jMenuAlign.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuAlign();
            }
        });
        jMenuHeight.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuHeight.setText("�����и��п�");
        jMenuHeight.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuHeight();
            }
        });
        jMenuDataFormat.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuDataFormat.setText("����������ʾ��ʽ");
        jMenuDataFormat.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuDataFormat();
            }
        });
        jMenuHead.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuHead.setText("���ñ�ͷ");
        jMenuCancelHead.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuCancelHead.setText("ȡ����ͷ");
        jMenuEdit.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuEdit.setText("�༭");
        jMenuCopy.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuCopy.setText("����");
        jMenuCopy.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuCopy();
            }
        });
        jMenuCut.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuCut.setText("����");
        jMenuCut.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuCut();
            }
        });
        jMenuPaste.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPaste.setText("ճ��");
        jMenuPaste.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuPaste();
            }
        });
        jMenuFix.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuFix.setText("�̶�����");
        jMenuCanelFix.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuCanelFix.setText("�����̶�");
        jMenuInsert.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuInsert.setText("����");
        jMenuDelete.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuDelete.setText("ɾ��");
        jMenuInsertRow.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuInsertRow.setText("����һ��");
        jMenuInsertRow.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                insertOneRow();
            }
        });
        jMenuInsertCol.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuInsertCol.setText("����һ��");
        jMenuInsertCol.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                insertOneCol();
            }
        });
        jMenuRight.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuRight.setText("��Ԫ����");
        jMenuRight.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                insertOneCellR();
            }
        });
        jMenuDown.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuDown.setText("��Ԫ����");
        jMenuDown.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                insertOneCellD();
            }
        });
        jMenuDeleteRow.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuDeleteRow.setText("ɾ��һ��");
        jMenuDeleteRow.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                deleteOneRow();
            }
        });
        jMenuDeleteCol.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuDeleteCol.setText("ɾ��һ��");
        jMenuDeleteCol.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                deleteOneCol();
            }
        });
        jMenuLeft.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuLeft.setText("��Ԫ����");
        jMenuLeft.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                deleteOneCellL();
            }
        });
        jMenuUp.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuUp.setText("��Ԫ����");
        jMenuUp.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                deleteOneCellU();
            }
        });
        jMenuInsertPage.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuInsertPage.setText("�����ҳ��");
        jMenuDeletePage.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuDeletePage.setText("ɾ����ҳ��");
        jMenuRowPage.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuRowPage.setText("�з�ҳ��");
        jMenuColPage.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuColPage.setText("�з�ҳ��");
        jMenuItem7.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuItem7.setText("�з�ҳ��");
        jMenuItem6.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuItem6.setText("�з�ҳ��");
        jMenuMyDefine.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuMyDefine.setText("�Զ���");
        jMenuFun.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuFun.setText("ѡ������Դ");
        jMenuFun.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuSource();
            }
        });
        jMenuReplace.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuReplace.setText("�ַ�������/�滻");
        jMenuPrint.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPrint.setText("��ӡ");
        jMenuPrintThis.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPrintThis.setText("��ӡ��ǰ����");
        jMenuPrintSum.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPrintSum.setText("��ӡ���ű���");
        jMenuReportDisp.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuReportDisp.setText("������ʾ");
        jMenuOpenReportData.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuOpenReportData.setText("�򿪱���");
        jMenuOpenSubData.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuOpenSubData.setText("���¼���λ����");
        jMenuOpenSumData.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuOpenSumData.setText("�򿪶��ű���");
        jMenuCloseThis.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuCloseThis.setText("�رյ�ǰ����");
        jMenuCloseAll.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuCloseAll.setText("�ر����б���");
        jMenuItemPopCopy.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuItemPopCopy.setMnemonic('C');
        jMenuItemPopCopy.setText("����(C)");
        jMenuItemPopCut.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuItemPopCut.setMnemonic('X');
        jMenuItemPopCut.setText("����(X)");
        jMenuItemPopPaste.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuItemPopPaste.setMnemonic('P');
        jMenuItemPopPaste.setText("ճ��(P)");
        jMenuItemPopFormat.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuItemPopFormat.setMnemonic('F');
        jMenuItemPopFormat.setText("��ʽ(F)...");
        jMenuSaveThis.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuSaveThis.setText("���浱ǰ����");
        jMenuSaveAll.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuSaveAll.setText("�������б���");
        jMenuPageSet.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPageSet.setText("ҳ������");
        jMenuItemMerge.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuItemMerge.setMnemonic('M');
        jMenuItemMerge.setText("�ϲ���Ԫ��(M)");
        jMenuFormula.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuFormula.setText("���ڼ��㹫ʽ");
        jMenuItemSum.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuItemSum.setText("����ϼ�");
        jMenuItemSum2.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuItemSum2.setText("����ϼ�");
        jMenuPopInsert.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPopInsert.setText("����");
        jMenuPopDelete.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPopDelete.setText("ɾ��");
        jMenuPopInsertRow.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPopInsertRow.setText("����һ��");
        jMenuPopInsertCol.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPopInsertCol.setText("����һ��");
        jMenuPopDeleteRow.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPopDeleteRow.setText("ɾ��һ��");
        jMenuPopDeleteCol.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPopDeleteCol.setText("ɾ��һ��");
        pmuCell.setFont(new java.awt.Font("DialogInput", 0, 12));
        pmuCell.setDoubleBuffered(true);
        pmuCell.setToolTipText("");
        jToolBarMain.setFont(new java.awt.Font("DialogInput", 0, 12));
        jBOpenDef.setMaximumSize(new Dimension(25, 25));
        jBOpenDef.setMinimumSize(new Dimension(25, 25));
        jBOpenDef.setPreferredSize(new Dimension(25, 25));
        jBOpenDef.setToolTipText("�򿪱���");
        jBOpenDef.setIcon(ib.getImageIcon(jBOpenDef, IconBuilder.OPEN));
        jBOpenDef.setMnemonic('0');
        jMenuDispSet.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuDispSet.setText("��ʾ����");
        jMenuItemRc.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuItemRc.setText("��������");
        jReport.setAllowFillRange(false);
        jBSaveDef.setIcon(ib.getImageIcon(jBSaveDef, IconBuilder.SAVE));
        jBSaveDef.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonSaveDefine();
            }
        });
        jBSaveDef.setMaximumSize(new Dimension(25, 25));
        jBSaveDef.setMinimumSize(new Dimension(25, 25));
        jBSaveDef.setPreferredSize(new Dimension(25, 25));
        jBSaveDef.setToolTipText("���汨��");
        jMenuSelectSource.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuSelectSource.setText("ѡ������Դ");
        jMenuDefineRc.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuDefineRc.setText("���幫ʽ");
        jBNewReport.setMaximumSize(new Dimension(25, 25));
        jBNewReport.setMinimumSize(new Dimension(25, 25));
        jBNewReport.setPreferredSize(new Dimension(25, 25));
        jBNewReport.setToolTipText("�½�����");
        jBNewReport.setIcon(ib.getImageIcon(jBNewReport, IconBuilder.NEW));
        jBNewReport.setMnemonic('0');
        jBCloseDef.setMaximumSize(new Dimension(25, 25));
        jBCloseDef.setMinimumSize(new Dimension(25, 25));
        jBCloseDef.setPreferredSize(new Dimension(25, 25));
        jBCloseDef.setToolTipText("�رձ���");
        jBCloseDef.setIcon(ib.getImageIcon(jBCloseDef, IconBuilder.CLOSE));
        jBCloseDef.setMnemonic('0');
        this.jToolBarMain.addSeparator();
        jBCopy.setMaximumSize(new Dimension(25, 25));
        jBCopy.setMinimumSize(new Dimension(25, 25));
        jBCopy.setPreferredSize(new Dimension(25, 25));
        jBCopy.setToolTipText("�򿪶��ű���");
        jBCopy.setIcon(ib.getImageIcon(jBCopy, IconBuilder.COPY));
        jBCopy.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonCopy();
            }
        });
        jBCut.setMaximumSize(new Dimension(25, 25));
        jBCut.setMinimumSize(new Dimension(25, 25));
        jBCut.setPreferredSize(new Dimension(25, 25));
        jBCut.setIcon(ib.getImageIcon(jBCut, IconBuilder.CUT));
        jBCut.setMnemonic('0');
        jBCut.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonCut();
            }
        });
        jSep1.setBorder(null);
        jSep1.setMaximumSize(new Dimension(5, 5));
        jSep1.setMinimumSize(new Dimension(5, 5));
        jSep1.setPreferredSize(new Dimension(5, 5));
        jSep1.setMnemonic('0');
        jBPaste.setMaximumSize(new Dimension(25, 25));
        jBPaste.setMinimumSize(new Dimension(25, 25));
        jBPaste.setPreferredSize(new Dimension(25, 25));
        jBPaste.setIcon(ib.getImageIcon(jBPaste, IconBuilder.PASTE));
        jBPaste.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonPaste();
            }
        });
        jBRight.setMaximumSize(new Dimension(25, 25));
        jBRight.setMinimumSize(new Dimension(25, 25));
        jBRight.setPreferredSize(new Dimension(25, 25));
        jBRight.setToolTipText("�Ҷ���");
        jBRight.setIcon(ib.getImageIcon(jBRight, IconBuilder.RIGHT));
        jBRight.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonRightAlign();
            }
        });
        jBJoin.setMaximumSize(new Dimension(25, 25));
        jBJoin.setMinimumSize(new Dimension(25, 25));
        jBJoin.setPreferredSize(new Dimension(25, 25));
        jBJoin.setToolTipText("���о���");
        jBJoin.setMnemonic('0');
        jBJoin.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonJoinAlign();
            }
        });
        jBJoin.setIcon(ib.getImageIcon(jBJoin, IconBuilder.JOIN));
        jBBorder.setMaximumSize(new Dimension(25, 25));
        jBBorder.setMinimumSize(new Dimension(25, 25));
        jBBorder.setPreferredSize(new Dimension(25, 25));
        jBBorder.setToolTipText("�߿�");
        jBBorder.setIcon(ib.getImageIcon(jBBorder, IconBuilder.BORDER));
        jBBorder.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonBorder();
            }
        });
        jSep2.setBorder(null);
        jSep2.setMaximumSize(new Dimension(5, 5));
        jSep2.setMinimumSize(new Dimension(5, 5));
        jSep2.setPreferredSize(new Dimension(5, 5));
        jSep2.setToolTipText("");
        jSep3.setBorder(null);
        jSep3.setMaximumSize(new Dimension(5, 5));
        jSep3.setMinimumSize(new Dimension(5, 5));
        jSep3.setPreferredSize(new Dimension(5, 5));
        jSep3.setToolTipText("");
        jBLeft.setMaximumSize(new Dimension(25, 25));
        jBLeft.setMinimumSize(new Dimension(25, 25));
        jBLeft.setPreferredSize(new Dimension(25, 25));
        jBLeft.setToolTipText("�����");
        jBLeft.setIcon(ib.getImageIcon(jBLeft, IconBuilder.LEFT));
        jBLeft.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonLeftAlign();
            }
        });
        jBCenter.setMaximumSize(new Dimension(25, 25));
        jBCenter.setMinimumSize(new Dimension(25, 25));
        jBCenter.setPreferredSize(new Dimension(25, 25));
        jBCenter.setToolTipText("����");
        jBCenter.setIcon(ib.getImageIcon(jBCenter, IconBuilder.CENTER));
        jBCenter.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonCenterAlign();
            }
        });
        jMenuCalcu.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuCalcu.setText("�������");
        jMenuCalcu.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuCalculate();
            }
        });
        jStatus.setFont(new java.awt.Font("DialogInput", 0, 12));
        jStatus.setText(" ");
        jBSource.setMaximumSize(new Dimension(25, 25));
        jBSource.setMinimumSize(new Dimension(25, 25));
        jBSource.setPreferredSize(new Dimension(25, 25));
        jBSource.setIcon(ib.getImageIcon(jBSource, IconBuilder.SOURCE));
        jMenuGlobal.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuGlobal.setText("ȫ����Ϣ");
        jMenuGlobal.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuGlobal();
            }
        });
        jBDelete.setMaximumSize(new Dimension(25, 25));
        jBDelete.setMinimumSize(new Dimension(25, 25));
        jBDelete.setPreferredSize(new Dimension(25, 25));
        jBDelete.setToolTipText("ɾ��");
        jBDelete.setIcon(ib.getImageIcon(jBDelete, IconBuilder.DELETE));
        jBDelete.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonDelete();
            }
        });
        jMenuParams.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuParams.setText("�������");
        jMenuParams.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuParams();
            }
        });
        jMenuHideRow.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuHideRow.setText("������");
        jMenuHideCol.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuHideCol.setText("������");
        jMenuWin.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuWin.setText("����");
        jMenuChangeWin.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuChangeWin.setText("�л�����");
        jMenuHelp.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuHelp.setText("����");
        jMenuSubject.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuSubject.setText("��������");
        jMenuAbout.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuAbout.setText("����IReport");
        jMenuVar.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuVar.setText("ϵͳ������");
        jMenuDisp.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuDisp.setText("��ԭ");
        jReport.addKeyListener(new java.awt.event.KeyAdapter()
        {
            //��������ȷ����������
            public void keyPressed(KeyEvent e)
            {
                jReportKeyPressed(e);
            }
        });
        jReport.addEndEditListener(new com.f1j.swing.EndEditListener()
        {
            public void endEdit(EndEditEvent e)
            {
                jReportEndEdit(e);
            }
        });
        this.getContentPane().add(jReport, BorderLayout.CENTER);
        this.getContentPane().add(jToolBarMain, BorderLayout.NORTH);
        jToolBarMain.add(jBNewReport, null);
        jToolBarMain.add(jBOpenDef, null);
        jToolBarMain.add(jBSaveDef, null);
        jToolBarMain.add(jBCloseDef, null);
        jToolBarMain.add(jSep1, null);
        jToolBarMain.add(jBCut, null);
        jToolBarMain.add(jBCopy, null);
        jToolBarMain.add(jBPaste, null);
        jToolBarMain.add(jBDelete, null);
        jToolBarMain.add(jSep2, null);
        jToolBarMain.add(jBLeft, null);
        jToolBarMain.add(jBCenter, null);
        jToolBarMain.add(jBRight, null);
        jToolBarMain.add(jBJoin, null);
        jToolBarMain.add(jBBorder, null);
        jToolBarMain.add(jSep3, null);
        jToolBarMain.add(jBSource, null);
        jToolBarMain.add(jSep4, null);
        this.getContentPane().add(jStatus, BorderLayout.SOUTH);
        jMenuDefine.add(jMenuDefineReport);
        jMenuDefine.add(jMenuReportDisp);
        jMenuDefine.add(jMenuFormat);
        jMenuDefine.add(jMenuEdit);
        jMenuDefine.add(jMenuMyDefine);
        jMenuDefine.add(jMenuPrint);
        jMenuDefine.add(jMenuWin);
        jMenuDefine.add(jMenuHelp);
        jMenuDefineReport.add(jMenuManageReport);
        jMenuDefineReport.add(jMenuSaveDef);
        jMenuDefineReport.add(jMenuCalcu);
        jMenuDefineReport.add(jMenuCloseDef);
        jMenuDefineReport.addSeparator();
        jMenuDefineReport.add(jMenuExitDef);
        jMenuFormat.add(jMenuFont);
        jMenuFormat.add(jMenuBorder);
        jMenuFormat.add(jMenuAlign);
        jMenuFormat.add(jMenuHeight);
        jMenuFormat.addSeparator();
        jMenuFormat.add(jMenuDataFormat);
        jMenuFormat.addSeparator();
        jMenuFormat.add(jMenuHead);
        jMenuFormat.add(jMenuCancelHead);
        jMenuEdit.add(jMenuCopy);
        jMenuEdit.add(jMenuCut);
        jMenuEdit.add(jMenuPaste);
        jMenuEdit.addSeparator();
        jMenuEdit.add(jMenuInsert);
        jMenuEdit.add(jMenuDelete);
        jMenuEdit.addSeparator();
        jMenuEdit.add(jMenuInsertPage);
        jMenuEdit.add(jMenuDeletePage);
        jMenuEdit.addSeparator();
        jMenuEdit.add(jMenuFix);
        jMenuEdit.add(jMenuCanelFix);
        jMenuEdit.addSeparator();
        jMenuEdit.add(jMenuReplace);
        jMenuEdit.addSeparator();
        jMenuEdit.add(jMenuHideRow);
        jMenuEdit.add(jMenuHideCol);
        jMenuEdit.add(jMenuDisp);
        jMenuInsert.add(jMenuInsertRow);
        jMenuInsert.add(jMenuInsertCol);
        jMenuInsert.add(jMenuRight);
        jMenuInsert.add(jMenuDown);
        jMenuDelete.add(jMenuDeleteRow);
        jMenuDelete.add(jMenuDeleteCol);
        jMenuDelete.add(jMenuLeft);
        jMenuDelete.add(jMenuUp);
        jMenuInsertPage.add(jMenuRowPage);
        jMenuInsertPage.add(jMenuColPage);
        jMenuDeletePage.add(jMenuItem7);
        jMenuDeletePage.add(jMenuItem6);
        jMenuMyDefine.add(jMenuVar);
        jMenuMyDefine.add(jMenuFun);
        jMenuMyDefine.add(jMenuParams);
        jMenuMyDefine.add(jMenuGlobal);
        jMenuMyDefine.add(jMenuFormula);
        jMenuPrint.add(jMenuPrintThis);
        jMenuPrint.add(jMenuPrintSum);
        jMenuPrint.add(jMenuPageSet);
        jMenuReportDisp.add(jMenuOpenReportData);
        jMenuReportDisp.add(jMenuOpenSubData);
        jMenuReportDisp.add(jMenuOpenSumData);
        jMenuReportDisp.addSeparator();
        jMenuReportDisp.add(jMenuSaveThis);
        jMenuReportDisp.add(jMenuSaveAll);
        jMenuReportDisp.addSeparator();
        jMenuReportDisp.add(jMenuCloseThis);
        jMenuReportDisp.add(jMenuCloseAll);
        jMenuReportDisp.addSeparator();
        jMenuReportDisp.add(jMenuDispSet);
        pmuCell.add(jMenuItemPopCut);
        pmuCell.add(jMenuItemPopCopy);
        pmuCell.add(jMenuItemPopPaste);
        pmuCell.addSeparator();
        pmuCell.add(jMenuPopInsert);
        pmuCell.add(jMenuPopDelete);
        pmuCell.addSeparator();
        pmuCell.add(jMenuItemPopFormat);
        pmuCell.addSeparator();
        pmuCell.add(jMenuSelectSource);
        pmuCell.add(jMenuDefineRc);
        pmuCell.addSeparator();
        pmuCell.add(jMenuItemMerge);
        jMenuFormula.add(jMenuItemSum);
        jMenuFormula.add(jMenuItemSum2);
        jMenuFormula.add(jMenuItemRc);
        jMenuPopInsert.add(jMenuPopInsertRow);
        jMenuPopInsert.add(jMenuPopInsertCol);
        jMenuPopDelete.add(jMenuPopDeleteRow);
        jMenuPopDelete.add(jMenuPopDeleteCol);
        jToolBarMain.add(jSep4, null);
        jMenuWin.add(jMenuChangeWin);
        jMenuHelp.add(jMenuSubject);
        jMenuHelp.addSeparator();
        jMenuHelp.add(jMenuAbout);
        this.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                close();
            }

            public void windowOpened(WindowEvent e)
            {
                open();
            }
        });
        this.getContentPane().add(jReport, BorderLayout.CENTER);

        /**�ؼ���ʼ��*/
        jReport.initWorkbook();
        /**ʹ�ؼ�������*/
        //jReport.setAllowSelections(false);
        jReport.insertSheets(1, 1);
        jReport.setSheet(0);
        /**����Ĭ������*/
        jReport.setDefaultFontName("����");
        /**��ֹ�϶�*/
        jReport.setAllowFillRange(false);
        jReport.setAllowMoveRange(false);
        jReport.setShowTabs(JBook.eTabsBottom);
    }

    public static void main(String[] args)
    {
//        /**�ı������ʾ���������ʹ��Windows���*/
//        try
//        {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        XReportMain xReport = new XReportMain();
//        /**�����ڴ�С����Ϊ��Ļ�Ĵ�С*/
//        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
//        xReport.setSize(d);
//        /**��ʾ����*/
//        xReport.setTitle("XReport 1.0 (c)Sinosoft 2003");
//        xReport.setLocation((d.width - xReport.getSize().width) / 2,
//                            (d.height - xReport.getSize().height) / 2);
//        xReport.setVisible(true);
    }

    void close()
    {
        System.exit(0);
    }

    void open()
    {
    }

    void menuCopy()
    {
        report.copyUnit();
    }

    void menuCut()
    {
        report.cutUnit();
    }

    void menuPaste()
    {
        report.pasteUnit();
    }

    void jReportKeyPressed(KeyEvent e)
    {
        //�û������ˡ�Delete����ʱ�������Ԫ�������
        if (KeyEvent.getKeyText(e.getKeyCode()).equals("Delete"))
        {
            report.clearUnit("Delete");
        }
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_C)
        {
            report.copyUnit();
        }
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_X)
        {
            report.cutUnit();
        }
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V)
        {
            report.pasteUnit();
        }
    }

    void manageReport()
    {
        FrameManageReport manage = new FrameManageReport(this);
        manage.setSize(550, 350);
        manage.open();
    }

    void buttonCut()
    {
        menuCut();
    }

    void buttonCopy()
    {
        menuCopy();
    }

    void buttonPaste()
    {
        menuPaste();
    }

    void buttonDelete()
    {
        report.clearUnit("Delete");
    }

    void insertOneRow()
    {
        report.insertUnit((short) 3);
    }

    void insertOneCol()
    {
        report.insertUnit((short) 4);
    }

    void insertOneCellR()
    {
        report.insertUnit((short) 1);
    }

    void insertOneCellD()
    {
        report.insertUnit((short) 2);
    }

    void deleteOneRow()
    {
        report.deleteUnit((short) 3);
    }

    void deleteOneCol()
    {
        report.deleteUnit((short) 4);
    }

    void deleteOneCellL()
    {
        report.deleteUnit((short) 1);
    }

    void deleteOneCellU()
    {
        report.deleteUnit((short) 2);
    }

    void jReportEndEdit(EndEditEvent e)
    {
        //ɾ���˸õ�Ԫ���е�����
        if (e.getEditString().equals(""))
        {
            report.clearUnit("BackSpace");
        }
    }

    void menuFont()
    {
        report.setFont();
    }

    void menuBorder()
    {
        report.setBorder();
    }

    void menuAlign()
    {
        report.setAlign();
    }

    void menuHeight()
    {
        report.setHeightWidth();
    }

    void menuDataFormat()
    {
        report.setNumberFormat();
    }

    void buttonBorder()
    {
        report.setBorder();
    }

    void buttonLeftAlign()
    {
        report.leftAlign();
    }

    void buttonCenterAlign()
    {
        report.centerAlign();
    }

    void buttonRightAlign()
    {
        report.rightAlign();
    }

    void buttonJoinAlign()
    {
        report.joinAlign();
    }

    void menuSaveDefine()
    {
        report.saveDefine();
    }

    void buttonSaveDefine()
    {
        menuSaveDefine();
    }

    void menuSource()
    {
        FrameCrossReport frmCrossReport = new FrameCrossReport(this);
        frmCrossReport.setSize(360, 380);
        frmCrossReport.open(report.getText());
    }

    void menuGlobal()
    {
        FrameGlobalInfo frmGlobalInfo = new FrameGlobalInfo(this);
        frmGlobalInfo.setSize(470, 450);
        frmGlobalInfo.open(report.getGlobal());
    }

    void menuColseReport()
    {
        report.closeReport();
    }

    void menuCalculate()
    {

    }

    void menuParams()
    {
        FrameDefineParams frmParams = new FrameDefineParams(this);
        frmParams.setSize(440, 300);
        frmParams.open(report.getParams());
    }
}

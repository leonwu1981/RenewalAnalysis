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
 * <p>Description: XReport主界面</p>
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
    /**报表控件*/
    private JBook jReport = new Report();
    /**报表对象*/
    private Report report;

    private IconBuilder ib = IconBuilder.getInstance();
    //可视化编程自动生成代码开始
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
    //可视化编程生成代码结束


    public XReportMain()
    {
        try
        {
            /**关联报表对象和报表控件*/
            report = (Report) jReport;
            /**界面初始化*/
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
        //可视化编程自动生成代码开始
        jSep4.setBorder(null);
        jSep4.setMaximumSize(new Dimension(5, 5));
        jSep4.setMinimumSize(new Dimension(5, 5));
        jSep4.setPreferredSize(new Dimension(5, 5));
        this.setFont(new java.awt.Font("DialogInput", 0, 12));
        this.setJMenuBar(jMenuDefine);
        this.getContentPane().setLayout(borderLayout);
        jMenuDefineReport.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuDefineReport.setText("报表定义");
        jMenuManageReport.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuManageReport.setText("报表管理");
        jMenuManageReport.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                manageReport();
            }
        });
        jMenuSaveDef.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuSaveDef.setText("保存报表定义");
        jMenuSaveDef.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuSaveDefine();
            }
        });
        jMenuCloseDef.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuCloseDef.setText("关闭报表");
        jMenuCloseDef.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuColseReport();
            }
        });
        jMenuExitDef.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuExitDef.setText("返回");
        jMenuFormat.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuFormat.setText("格式");
        jMenuFont.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuFont.setText("字体");
        jMenuFont.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuFont();
            }
        });
        jMenuBorder.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuBorder.setText("边框");
        jMenuBorder.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuBorder();
            }
        });
        jMenuAlign.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuAlign.setText("对齐");
        jMenuAlign.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuAlign();
            }
        });
        jMenuHeight.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuHeight.setText("调整行高列宽");
        jMenuHeight.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuHeight();
            }
        });
        jMenuDataFormat.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuDataFormat.setText("设置数据显示格式");
        jMenuDataFormat.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuDataFormat();
            }
        });
        jMenuHead.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuHead.setText("设置表头");
        jMenuCancelHead.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuCancelHead.setText("取消表头");
        jMenuEdit.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuEdit.setText("编辑");
        jMenuCopy.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuCopy.setText("复制");
        jMenuCopy.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuCopy();
            }
        });
        jMenuCut.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuCut.setText("剪切");
        jMenuCut.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuCut();
            }
        });
        jMenuPaste.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPaste.setText("粘贴");
        jMenuPaste.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuPaste();
            }
        });
        jMenuFix.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuFix.setText("固定行列");
        jMenuCanelFix.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuCanelFix.setText("撤销固定");
        jMenuInsert.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuInsert.setText("插入");
        jMenuDelete.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuDelete.setText("删除");
        jMenuInsertRow.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuInsertRow.setText("插入一行");
        jMenuInsertRow.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                insertOneRow();
            }
        });
        jMenuInsertCol.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuInsertCol.setText("插入一列");
        jMenuInsertCol.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                insertOneCol();
            }
        });
        jMenuRight.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuRight.setText("单元右移");
        jMenuRight.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                insertOneCellR();
            }
        });
        jMenuDown.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuDown.setText("单元下移");
        jMenuDown.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                insertOneCellD();
            }
        });
        jMenuDeleteRow.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuDeleteRow.setText("删除一行");
        jMenuDeleteRow.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                deleteOneRow();
            }
        });
        jMenuDeleteCol.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuDeleteCol.setText("删除一列");
        jMenuDeleteCol.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                deleteOneCol();
            }
        });
        jMenuLeft.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuLeft.setText("单元左移");
        jMenuLeft.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                deleteOneCellL();
            }
        });
        jMenuUp.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuUp.setText("单元上移");
        jMenuUp.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                deleteOneCellU();
            }
        });
        jMenuInsertPage.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuInsertPage.setText("插入分页符");
        jMenuDeletePage.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuDeletePage.setText("删除分页符");
        jMenuRowPage.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuRowPage.setText("行分页符");
        jMenuColPage.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuColPage.setText("列分页符");
        jMenuItem7.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuItem7.setText("行分页符");
        jMenuItem6.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuItem6.setText("列分页符");
        jMenuMyDefine.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuMyDefine.setText("自定义");
        jMenuFun.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuFun.setText("选择数据源");
        jMenuFun.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuSource();
            }
        });
        jMenuReplace.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuReplace.setText("字符串查找/替换");
        jMenuPrint.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPrint.setText("打印");
        jMenuPrintThis.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPrintThis.setText("打印当前报表");
        jMenuPrintSum.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPrintSum.setText("打印多张报表");
        jMenuReportDisp.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuReportDisp.setText("报表显示");
        jMenuOpenReportData.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuOpenReportData.setText("打开报表");
        jMenuOpenSubData.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuOpenSubData.setText("打开下级单位报表");
        jMenuOpenSumData.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuOpenSumData.setText("打开多张报表");
        jMenuCloseThis.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuCloseThis.setText("关闭当前报表");
        jMenuCloseAll.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuCloseAll.setText("关闭所有报表");
        jMenuItemPopCopy.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuItemPopCopy.setMnemonic('C');
        jMenuItemPopCopy.setText("复制(C)");
        jMenuItemPopCut.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuItemPopCut.setMnemonic('X');
        jMenuItemPopCut.setText("剪切(X)");
        jMenuItemPopPaste.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuItemPopPaste.setMnemonic('P');
        jMenuItemPopPaste.setText("粘贴(P)");
        jMenuItemPopFormat.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuItemPopFormat.setMnemonic('F');
        jMenuItemPopFormat.setText("格式(F)...");
        jMenuSaveThis.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuSaveThis.setText("保存当前报表");
        jMenuSaveAll.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuSaveAll.setText("保存所有报表");
        jMenuPageSet.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPageSet.setText("页面设置");
        jMenuItemMerge.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuItemMerge.setMnemonic('M');
        jMenuItemMerge.setText("合并单元格(M)");
        jMenuFormula.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuFormula.setText("表内计算公式");
        jMenuItemSum.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuItemSum.setText("纵向合计");
        jMenuItemSum2.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuItemSum2.setText("横向合计");
        jMenuPopInsert.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPopInsert.setText("插入");
        jMenuPopDelete.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPopDelete.setText("删除");
        jMenuPopInsertRow.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPopInsertRow.setText("插入一行");
        jMenuPopInsertCol.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPopInsertCol.setText("插入一列");
        jMenuPopDeleteRow.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPopDeleteRow.setText("删除一行");
        jMenuPopDeleteCol.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuPopDeleteCol.setText("删除一列");
        pmuCell.setFont(new java.awt.Font("DialogInput", 0, 12));
        pmuCell.setDoubleBuffered(true);
        pmuCell.setToolTipText("");
        jToolBarMain.setFont(new java.awt.Font("DialogInput", 0, 12));
        jBOpenDef.setMaximumSize(new Dimension(25, 25));
        jBOpenDef.setMinimumSize(new Dimension(25, 25));
        jBOpenDef.setPreferredSize(new Dimension(25, 25));
        jBOpenDef.setToolTipText("打开报表");
        jBOpenDef.setIcon(ib.getImageIcon(jBOpenDef, IconBuilder.OPEN));
        jBOpenDef.setMnemonic('0');
        jMenuDispSet.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuDispSet.setText("显示设置");
        jMenuItemRc.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuItemRc.setText("整行整列");
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
        jBSaveDef.setToolTipText("保存报表");
        jMenuSelectSource.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuSelectSource.setText("选择数据源");
        jMenuDefineRc.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuDefineRc.setText("定义公式");
        jBNewReport.setMaximumSize(new Dimension(25, 25));
        jBNewReport.setMinimumSize(new Dimension(25, 25));
        jBNewReport.setPreferredSize(new Dimension(25, 25));
        jBNewReport.setToolTipText("新建报表");
        jBNewReport.setIcon(ib.getImageIcon(jBNewReport, IconBuilder.NEW));
        jBNewReport.setMnemonic('0');
        jBCloseDef.setMaximumSize(new Dimension(25, 25));
        jBCloseDef.setMinimumSize(new Dimension(25, 25));
        jBCloseDef.setPreferredSize(new Dimension(25, 25));
        jBCloseDef.setToolTipText("关闭报表");
        jBCloseDef.setIcon(ib.getImageIcon(jBCloseDef, IconBuilder.CLOSE));
        jBCloseDef.setMnemonic('0');
        this.jToolBarMain.addSeparator();
        jBCopy.setMaximumSize(new Dimension(25, 25));
        jBCopy.setMinimumSize(new Dimension(25, 25));
        jBCopy.setPreferredSize(new Dimension(25, 25));
        jBCopy.setToolTipText("打开多张报表");
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
        jBRight.setToolTipText("右对齐");
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
        jBJoin.setToolTipText("跨列居中");
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
        jBBorder.setToolTipText("边框");
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
        jBLeft.setToolTipText("左对齐");
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
        jBCenter.setToolTipText("居中");
        jBCenter.setIcon(ib.getImageIcon(jBCenter, IconBuilder.CENTER));
        jBCenter.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonCenterAlign();
            }
        });
        jMenuCalcu.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuCalcu.setText("报表计算");
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
        jMenuGlobal.setText("全局信息");
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
        jBDelete.setToolTipText("删除");
        jBDelete.setIcon(ib.getImageIcon(jBDelete, IconBuilder.DELETE));
        jBDelete.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                buttonDelete();
            }
        });
        jMenuParams.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuParams.setText("计算参数");
        jMenuParams.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                menuParams();
            }
        });
        jMenuHideRow.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuHideRow.setText("隐藏行");
        jMenuHideCol.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuHideCol.setText("隐藏列");
        jMenuWin.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuWin.setText("窗口");
        jMenuChangeWin.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuChangeWin.setText("切换窗口");
        jMenuHelp.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuHelp.setText("帮助");
        jMenuSubject.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuSubject.setText("帮助主题");
        jMenuAbout.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuAbout.setText("关于IReport");
        jMenuVar.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuVar.setText("系统变量表");
        jMenuDisp.setFont(new java.awt.Font("DialogInput", 0, 12));
        jMenuDisp.setText("还原");
        jReport.addKeyListener(new java.awt.event.KeyAdapter()
        {
            //函数名的确定重名问题
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

        /**控件初始化*/
        jReport.initWorkbook();
        /**使控件不可用*/
        //jReport.setAllowSelections(false);
        jReport.insertSheets(1, 1);
        jReport.setSheet(0);
        /**设置默认字体*/
        jReport.setDefaultFontName("宋体");
        /**禁止拖动*/
        jReport.setAllowFillRange(false);
        jReport.setAllowMoveRange(false);
        jReport.setShowTabs(JBook.eTabsBottom);
    }

    public static void main(String[] args)
    {
//        /**改变界面显示风格，在这里使用Windows风格*/
//        try
//        {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        XReportMain xReport = new XReportMain();
//        /**将窗口大小设置为屏幕的大小*/
//        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
//        xReport.setSize(d);
//        /**显示窗口*/
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
        //用户按下了“Delete”键时，清除单元格的内容
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
        //删除了该单元格中的内容
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

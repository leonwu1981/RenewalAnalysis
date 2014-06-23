package com.sinosoft.xreport.pl;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.f1j.ss.CellFormat;
import com.f1j.swing.JBook;
import com.f1j.swing.ss.FormatCellsDlg;
import com.sinosoft.xreport.bl.*;
import com.sinosoft.xreport.util.StringUtility;
import com.sinosoft.xreport.util.SysConfig;
import com.sinosoft.xreport.util.XReader;

/**
 * <p>Title: XReport 1.0 (c)Sinosoft 2003</p>
 * <p>Description: 报表对象</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: sinosoft</p>
 * @author lixy
 * @version 1.0
 */

public class Report extends JBook implements Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**报表数据*/
    private ReportModel model;
    /**剪贴板*/
    private HashMap mapSheetOne = new HashMap();
    private HashMap mapSheetTwo = new HashMap();

    public Report()
    {
        super();
    }

    /**
     * 读取报表格式
     * @return 报表格式对象
     */
    public JBook getFormat()
    {
        return (JBook)this;
    }

    /**
     * 读取报表信息
     * @return 报表数据信息
     */
    public ReportMain getReport()
    {
        if (model == null)
        {
            return null;
        }
        return model.getReport();
    }

    public boolean setReport(ReportMain report)
    {
        if (model == null)
        {
            return false;
        }
        model.setReport(report);
        return true;
    }

    public Vector getSource()
    {
        if (model == null)
        {
            return null;
        }
        return model.getSource();
    }

    public void setModel(ReportModel model)
    {
        this.model = model;
    }


/////////////////////////报表格式操作////////////////////////////////

    /**
     * 单元格复制
     * */
    public void copyUnit()
    {
        /**被复制区域的范围*/
        int intStartRow, intStartCol, intEndRow, intEndCol;
        /**清空剪贴板*/
        mapSheetOne.clear();
        mapSheetTwo.clear();
        try
        {
            /**读取被复制区域的范围*/
            intStartRow = getSelStartRow();
            intStartCol = getSelStartCol();
            intEndRow = getSelEndRow();
            intEndCol = getSelEndCol();
            /**将被复制区域的内容copy到剪贴板中*/
            for (int i = intStartRow; i <= intEndRow; i++)
            {
                for (int j = intStartCol; j <= intEndCol; j++)
                {
                    //复制第一张sheet的内容
                    String strText = getText(0, i, j);
                    String strRowCol = (i - intStartRow) + "^" +
                                       (j - intStartCol);
                    if (!strText.equals(""))
                    {
                        mapSheetOne.put(strRowCol, strText);
                    }
                    //复制第二张sheet的内容
                    strText = getText(1, i, j);
                    if (!strText.equals(""))
                    {
                        mapSheetTwo.put(strRowCol, strText);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 单元格剪切
     */
    public void cutUnit()
    {
        /**被复制区域的范围*/
        int intStartRow, intStartCol, intEndRow, intEndCol;
        /**清空剪贴板*/
        mapSheetOne.clear();
        mapSheetTwo.clear();
        try
        {
            /**读取被剪切区域的范围*/
            intStartRow = getSelStartRow();
            intStartCol = getSelStartCol();
            intEndRow = getSelEndRow();
            intEndCol = getSelEndCol();
            /**将被剪切区域的内容cut到剪贴板中*/
            for (int i = intStartRow; i <= intEndRow; i++)
            {
                for (int j = intStartCol; j <= intEndCol; j++)
                {
                    //剪切第一张sheet的内容
                    String strText = getText(0, i, j);
                    String strRowCol = (i - intStartRow) + "^" +
                                       (j - intStartCol);
                    if (!strText.equals(""))
                    {
                        mapSheetOne.put(strRowCol, strText);
                    }
                    setText(0, i, j, "");
                    //剪切第二张sheet的内容
                    strText = getText(1, i, j);
                    if (!strText.equals(""))
                    {
                        mapSheetTwo.put(strRowCol, strText);
                    }
                    setText(1, i, j, "");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 单元格粘贴
     * */
    public void pasteUnit()
    {
        /**被粘贴的位置*/
        int intRow = 0, intCol = 0;
        int intRelativeRow = 0, intRelativeCol = 0;
        String strText = "";
        try
        {
            /**读取被粘贴的位置*/
            intRow = getActiveRow();
            intCol = getActiveCol();
            /**粘贴第一张sheet的内容*/
            Object obj[] = mapSheetOne.keySet().toArray();
            for (int i = 0; i < obj.length; i++)
            {
                /**读取剪贴板的内容*/
                String strRowCol = obj[i].toString();
                strText = mapSheetOne.get(strRowCol).toString();
                int intIndex = strRowCol.indexOf("^");
                intRelativeRow = Integer.parseInt(strRowCol.substring(0,
                        intIndex));
                intRelativeCol = Integer.parseInt(strRowCol.substring(intIndex +
                        1));
                /**粘贴操作*/
                setText(0, intRow + intRelativeRow,
                        intCol + intRelativeCol,
                        strText);
            }
            /**粘贴第二张sheet的内容*/
            obj = mapSheetTwo.keySet().toArray();
            for (int i = 0; i < obj.length; i++)
            {
                /**读取剪贴板的内容*/
                String strRowCol = obj[i].toString();
                strText = mapSheetTwo.get(strRowCol).toString();
                int intIndex = strRowCol.indexOf("^");
                intRelativeRow = Integer.parseInt(strRowCol.substring(0,
                        intIndex));
                intRelativeCol = Integer.parseInt(strRowCol.substring(intIndex +
                        1));
                /**粘贴操作*/
                setText(1, intRow + intRelativeRow,
                        intCol + intRelativeCol,
                        strText);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 清除单元格内容
     * @param clearType 清除方式
     */
    public void clearUnit(String clearType)
    {
        int intStartRow, intStartCol, intEndRow, intEndCol;
        int i, j;
        try
        {
            //读取sheet1的选定范围
            intStartRow = getSelStartRow();
            intStartCol = getSelStartCol();
            intEndRow = getSelEndRow();
            intEndCol = getSelEndCol();
            //用Delete键删除
            if (clearType.equals("Delete"))
            {
                //清除sheet2中相应的范围
                for (i = intStartRow; i <= intEndRow; i++)
                {
                    for (j = intStartCol; j <= intEndCol; j++)
                    {
                        setText(0, i, j, "");
                        setText(1, i, j, "");
                    }
                }
            }
            //用BackSpace键删除
            else if (clearType.equals("BackSpace"))
            {
                setText(1, intStartRow, intStartCol, "");
            }
        }
        catch (Exception eClear)
        {
            eClear.printStackTrace();
        }
    }

    /**
     * 报表单元格插入
     * @param shiftType 插入方式
     */
    public void insertUnit(short shiftType)
    {
        int intStartRow, intStartCol, intEndRow, intEndCol;
        try
        {
            //在sheet1中进行插入操作
            editInsert(shiftType);
            //读取进行插入操作的位置
            intStartRow = getSelStartRow();
            intStartCol = getSelStartCol();
            intEndRow = getSelEndRow();
            intEndCol = getSelEndCol();
            //在sheet2中相应的位置进行插入操作
            setSheet(1);
            setSelection(intStartRow, intStartCol, intEndRow, intEndCol);
            editInsert(shiftType);
            setSheet(0);
        }
        catch (Exception eInsert)
        {
            eInsert.printStackTrace();
        }
    }

    /**
     * 报表单元格
     * @param shiftType 删除方式
     */
    public void deleteUnit(short shiftType)
    {
        int intStartRow, intStartCol, intEndRow, intEndCol;
        try
        {
            //在sheet1中进行删除操作
            editDelete(shiftType);
            //读取进行删除操作的位置
            intStartRow = getSelStartRow();
            intStartCol = getSelStartCol();
            intEndRow = getSelEndRow();
            intEndCol = getSelEndCol();
            //在sheet2中相应的位置进行删除操作
            setSheet(1);
            setSelection(intStartRow, intStartCol, intEndRow, intEndCol);
            editDelete(shiftType);
            setSheet(0);
        }
        catch (Exception eDelete)
        {
            eDelete.printStackTrace();
        }
    }

    /**
     * 设置单元格字体
     * */
    public void setFont()
    {
        try
        {
            //创建字体对话框
            FormatCellsDlg dlg = new FormatCellsDlg((JBook)this,
                    FormatCellsDlg.kFontPage);
            //显示字体对话框
            dlg.show();
        }
        catch (Exception ef)
        {
            ef.printStackTrace();
        }
    }

    /**
     * 设置单元格边框
     * */
    public void setBorder()
    {
        try
        {
            /**创建边框对话框*/
            FormatCellsDlg dlg = new FormatCellsDlg((JBook)this,
                    FormatCellsDlg.kBorderPage);
            /**显示边框对话框*/
            dlg.show();
        }
        catch (Exception eb)
        {
            eb.printStackTrace();
        }
    }

    /**
     * 设置数据显示格式
     * */
    public void setNumberFormat()
    {
        try
        {
            //创建数据显示格式对话框
            FormatCellsDlg dlg = new FormatCellsDlg((JBook)this,
                    FormatCellsDlg.kNumberPage);
            //显示数据显示格式对话框
            dlg.show();
        }
        catch (Exception ed)
        {
            ed.printStackTrace();
        }
    }

    /**
     * 设置单元格对齐方式
     * */
    public void setAlign()
    {
        try
        {
            FrameCellAlign align = new FrameCellAlign((JBook)this);
            //调整对话框的大小和位置
            align.setSize(250, 300);
            align.setModal(true);
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            align.setLocation((d.width - align.getSize().width) / 2,
                              (d.height - align.getSize().height) / 2);
            //显示对齐方式对话框
            align.open();
        }
        catch (Exception eAlign)
        {
            eAlign.printStackTrace();
        }
    }

    /**
     * 调整单元格行高列宽
     * */
    public void setHeightWidth()
    {
        try
        {
            FrameCellHeight height = new FrameCellHeight((JBook)this);
            //调整对话框的大小和位置
            height.setSize(230, 270);
            height.setModal(true);
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            height.setLocation((d.width - height.getSize().width) / 2,
                               (d.height - height.getSize().height) / 2);
            //显示对话框
            height.open();
        }
        catch (Exception eHeigh)
        {
            eHeigh.printStackTrace();
        }
    }

    /**
     * 报表选定单元格左对齐
     */
    public void leftAlign()
    {
        try
        {
            CellFormat cellFormat = getCellFormat();
            cellFormat.setHorizontalAlignment((short) 1);
            setCellFormat(cellFormat);
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    /**
     * 报表选定单元格居中
     */
    public void centerAlign()
    {
        try
        {
            CellFormat cellFormat = getCellFormat();
            cellFormat.setHorizontalAlignment((short) 2);
            setCellFormat(cellFormat);
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    /**
     * 报表选定单元格右对齐
     */
    public void rightAlign()
    {
        try
        {
            CellFormat cellFormat = getCellFormat();
            cellFormat.setHorizontalAlignment((short) 3);
            setCellFormat(cellFormat);
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    /**
     * 报表选定单元格跨列居中
     */
    public void joinAlign()
    {
        try
        {
            CellFormat cellFormat = getCellFormat();
            cellFormat.setHorizontalAlignment((short) 6);
            setCellFormat(cellFormat);
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    /**
     * 读取选定单元格的内容
     * @return 选定单元格的内容
     */
    public String getText()
    {
        String text = "";
        int intRow = getActiveRow();
        int intCol = getActiveCol();
        try
        {
            text = getText(1, intRow, intCol);
            //如果选定的单元格没有内容则返回该单元格的位置信息
            if (text == null || text.equals(""))
            {
                text = StringUtility.rowCol2Cell(intRow, intCol);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return text;
    }

    /**
     * 设置选定单元格的内容
     * @param text 选定单元格的内容
     */
    public void setText(String text)
    {
        int intRow = getActiveRow();
        int intCol = getActiveCol();
        try
        {
            StringTokenizer token = new StringTokenizer(text,
                    SysConfig.SEPARATORTWO);
            token.nextElement();
            token.nextElement();
            token.nextElement();
            token.nextElement();
            setText(0, intRow, intCol, (String) token.nextElement());
            setText(1, intRow, intCol, text);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 读取报表的全局信息
     * @return 报表的全局信息
     */
    public String getGlobal()
    {
        String text = "";
        try
        {
            text = getText(1, 0, 0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return text;
    }

    public String getParams()
    {
        String text = "";
        try
        {
            text = getText(1, 0, 2);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return text;
    }

    /**
     * 设置报表的全局信息
     * @param text 报表的全局信息
     */
    public void setGlobal(String text)
    {
        try
        {
            setText(1, 0, 0, text);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setParams(String text)
    {
        try
        {
            setText(1, 0, 2, text);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

//////////////////////报表数据操作///////////////////////////////

    /**
     * 打开指定报表定义
     * @param report 报表信息
     */
    public void open(ReportMain report)
    {
        /**根据新建报表的信息初始化model*/
        if (report.getReportAtt().equals("固定格式"))
        {
            model = new CrossReport();
        }
        if (report.getReportAtt().equals("清单格式"))
        {
            model = new PlaneReport();
        }
        model.setReport(report);
        readFormat(report);
        readDefine(report);
    }

    /**
     * 打开报表数据
     * @param file 报表数据文件名
     */
    public void open(String file)
    {
        try
        {
            /**读取报表格式*/
            initWorkbook();
            readURL(new URL(SysConfig.TRUEHOST + "down.jsp?type=data&file=" +
                            file)
                    , new com.f1j.ss.ReadParams());
            /**初始化界面*/
            setDefaultFontName("宋体");
            setSheet(0);
            setAllowFillRange(false);
            setAllowMoveRange(false);
            setShowTabs(JBook.eTabsBottom);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "打开文件失败!");
        }
    }

    /**
     * 读取报表格式
     * @param report 报表信息
     */
    private void readFormat(ReportMain report)
    {
        /**拼制文件名*/
        String file = report.getBranchId() +
                      "_" + report.getReportId() +
                      "_" + report.getReportEdition() +
                      ".xls";
        try
        {
            /**读取报表格式*/
            initWorkbook();
            readURL(new URL(SysConfig.TRUEHOST + "down.jsp?type=define&file=" +
                            file)
                    , new com.f1j.ss.ReadParams());
            /**初始化界面*/
            setDefaultFontName("宋体");
            setSheet(0);
            setAllowFillRange(false);
            setAllowMoveRange(false);
            setShowTabs(JBook.eTabsBottom);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "打开文件失败!");
        }
    }

    /**
     * 读取报表数据(数据源)
     * @param report 报表信息
     */
    public void readDefine(ReportMain report)
    {
        String strReportAtt = report.getReportAtt();
        if (strReportAtt.equals("固定格式"))
        {
            setModel(new CrossReport());
        }
        if (strReportAtt.equals("清单格式"))
        {
            setModel(new PlaneReport());
        }
        model.readDefine(report);
    }

    /**
     * 新建一张报表
     * @param newReport 报表信息
     * @param newSource 报表数据源
     */
    public void newReport(ReportMain newReport, Vector newSource)
    {
        /**根据新建报表的信息初始化model*/
        if (newReport.getReportAtt().equals("固定格式"))
        {
            model = new CrossReport();
        }
        if (newReport.getReportAtt().equals("清单格式"))
        {
            model = new PlaneReport();
        }
        model.setReport(newReport);
        model.setSource(newSource);
        /**保存报表信息*/
        saveReportInfo();
        /**新建格式文件*/
        newFormat();
        /**新建定义文件*/
        newDefine();
    }

    /**
     * 修改当前报表信息(基本信息、数据源信息)
     * @param updReport 新的报表信息
     * @param updSource 新的数据源信息
     */
    public void updReport(ReportMain updReport, Vector updSource)
    {
        /**根据新建报表的信息初始化model*/
        if (updReport.getReportAtt().equals("固定格式"))
        {
            model = new CrossReport();
        }
        if (updReport.getReportAtt().equals("清单格式"))
        {
            model = new PlaneReport();
        }
        /**设置新的报表信息*/
        model.setReport(updReport);
        /**读取报表定义*/
        model.readDefine(updReport);
        /**设置新的数据源信息*/
        model.setSource(updSource);
        /**保存报表定义*/
        model.saveDefine();
    }

    /**
     * 保存当前报表格式(新建)
     */
    private void newFormat()
    {
        ReportMain tReportMain = model.getReport();
        try
        {
            /**保存报表格式*/
            String strFileName = tReportMain.getBranchId().trim() + "_" +
                                 tReportMain.getReportId().trim() + "_" +
                                 tReportMain.getReportEdition().trim() +
                                 ".xls";
            URL url = new URL(SysConfig.TRUEHOST + "up.jsp?type=define&file=" +
                              strFileName);
            initWorkbook();
            insertSheets(1, 1);
            setSheet(0);
            setShowTabs(JBook.eTabsBottom);
            writeURL(url, new com.f1j.ss.WriteParams(JBook.eFileExcel97));
        }
        catch (Exception eOK)
        {
            JOptionPane.showMessageDialog(null, "格式文件保存失败!");
            eOK.printStackTrace();
        }
    }

    /**
     * 保存当前报表格式
     */
    private void saveFormat()
    {
        ReportMain tReportMain = model.getReport();
        /**拼制文件名*/
        String strFileName = tReportMain.getBranchId().trim() + "_" +
                             tReportMain.getReportId().trim() + "_" +
                             tReportMain.getReportEdition().trim() +
                             ".xls";
        try
        {
            /**保存报表格式*/
            URL url = new URL(SysConfig.TRUEHOST + "up.jsp?type=define&file=" +
                              strFileName);
            writeURL(url, new com.f1j.ss.WriteParams(JBook.eFileExcel97));
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "格式文件保存失败!");
            e.printStackTrace();
        }
    }

    /**
     * 保存当前报表信息
     */
    private void saveReportInfo()
    {
        model.getReport().insert();
    }

    /**
     * 保存当前报表的定义(新建)
     */
    private void newDefine()
    {
        model.newDefine();
    }

    /**
     * 保存当前报表的定义
     */
    public void saveDefine()
    {
        if (model == null)
        {
            return;
        }
        /**保存报表格式*/
        saveFormat();
        //从Formula One 控件上收集Row、Col、Cell的信息并设置到ReportModel中
        DefineCreater creater = new DefineCreater(getFormat());
        BlockParams params = creater.getParams();
        DefineBlock[] block = creater.getDefineBlock();
        if (model instanceof CrossReport)
        {
            ((CrossReport) model).setDefineBlock(block);
            ((CrossReport) model).setParams(params);
        }
        model.saveDefine();
    }

    /**
     * 关闭当前报表
     */
    public void closeReport()
    {
        if (JOptionPane.showConfirmDialog
            (null, "保存所做的修改吗?", "关闭报表",
             JOptionPane.OK_CANCEL_OPTION,
             JOptionPane.QUESTION_MESSAGE)
            == JOptionPane.OK_OPTION)
        {
            saveFormat();
            saveReportInfo();
            saveDefine();
        }
        /**清除报表数据*/
        model = null;
        mapSheetOne = new HashMap();
        mapSheetTwo = new HashMap();
        /**清除报表格式*/
        try
        {
            initWorkbook();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void calculate(ReportMain report)
    {
        String strParams = XReader.readPrecal(report);
        FrameCommitParams frmParams = new FrameCommitParams(this);
        frmParams.open(strParams);
    }
}
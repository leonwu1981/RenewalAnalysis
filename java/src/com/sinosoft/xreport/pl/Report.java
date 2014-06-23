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
 * <p>Description: �������</p>
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
    /**��������*/
    private ReportModel model;
    /**������*/
    private HashMap mapSheetOne = new HashMap();
    private HashMap mapSheetTwo = new HashMap();

    public Report()
    {
        super();
    }

    /**
     * ��ȡ�����ʽ
     * @return �����ʽ����
     */
    public JBook getFormat()
    {
        return (JBook)this;
    }

    /**
     * ��ȡ������Ϣ
     * @return ����������Ϣ
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


/////////////////////////�����ʽ����////////////////////////////////

    /**
     * ��Ԫ����
     * */
    public void copyUnit()
    {
        /**����������ķ�Χ*/
        int intStartRow, intStartCol, intEndRow, intEndCol;
        /**��ռ�����*/
        mapSheetOne.clear();
        mapSheetTwo.clear();
        try
        {
            /**��ȡ����������ķ�Χ*/
            intStartRow = getSelStartRow();
            intStartCol = getSelStartCol();
            intEndRow = getSelEndRow();
            intEndCol = getSelEndCol();
            /**�����������������copy����������*/
            for (int i = intStartRow; i <= intEndRow; i++)
            {
                for (int j = intStartCol; j <= intEndCol; j++)
                {
                    //���Ƶ�һ��sheet������
                    String strText = getText(0, i, j);
                    String strRowCol = (i - intStartRow) + "^" +
                                       (j - intStartCol);
                    if (!strText.equals(""))
                    {
                        mapSheetOne.put(strRowCol, strText);
                    }
                    //���Ƶڶ���sheet������
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
     * ��Ԫ�����
     */
    public void cutUnit()
    {
        /**����������ķ�Χ*/
        int intStartRow, intStartCol, intEndRow, intEndCol;
        /**��ռ�����*/
        mapSheetOne.clear();
        mapSheetTwo.clear();
        try
        {
            /**��ȡ����������ķ�Χ*/
            intStartRow = getSelStartRow();
            intStartCol = getSelStartCol();
            intEndRow = getSelEndRow();
            intEndCol = getSelEndCol();
            /**�����������������cut����������*/
            for (int i = intStartRow; i <= intEndRow; i++)
            {
                for (int j = intStartCol; j <= intEndCol; j++)
                {
                    //���е�һ��sheet������
                    String strText = getText(0, i, j);
                    String strRowCol = (i - intStartRow) + "^" +
                                       (j - intStartCol);
                    if (!strText.equals(""))
                    {
                        mapSheetOne.put(strRowCol, strText);
                    }
                    setText(0, i, j, "");
                    //���еڶ���sheet������
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
     * ��Ԫ��ճ��
     * */
    public void pasteUnit()
    {
        /**��ճ����λ��*/
        int intRow = 0, intCol = 0;
        int intRelativeRow = 0, intRelativeCol = 0;
        String strText = "";
        try
        {
            /**��ȡ��ճ����λ��*/
            intRow = getActiveRow();
            intCol = getActiveCol();
            /**ճ����һ��sheet������*/
            Object obj[] = mapSheetOne.keySet().toArray();
            for (int i = 0; i < obj.length; i++)
            {
                /**��ȡ�����������*/
                String strRowCol = obj[i].toString();
                strText = mapSheetOne.get(strRowCol).toString();
                int intIndex = strRowCol.indexOf("^");
                intRelativeRow = Integer.parseInt(strRowCol.substring(0,
                        intIndex));
                intRelativeCol = Integer.parseInt(strRowCol.substring(intIndex +
                        1));
                /**ճ������*/
                setText(0, intRow + intRelativeRow,
                        intCol + intRelativeCol,
                        strText);
            }
            /**ճ���ڶ���sheet������*/
            obj = mapSheetTwo.keySet().toArray();
            for (int i = 0; i < obj.length; i++)
            {
                /**��ȡ�����������*/
                String strRowCol = obj[i].toString();
                strText = mapSheetTwo.get(strRowCol).toString();
                int intIndex = strRowCol.indexOf("^");
                intRelativeRow = Integer.parseInt(strRowCol.substring(0,
                        intIndex));
                intRelativeCol = Integer.parseInt(strRowCol.substring(intIndex +
                        1));
                /**ճ������*/
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
     * �����Ԫ������
     * @param clearType �����ʽ
     */
    public void clearUnit(String clearType)
    {
        int intStartRow, intStartCol, intEndRow, intEndCol;
        int i, j;
        try
        {
            //��ȡsheet1��ѡ����Χ
            intStartRow = getSelStartRow();
            intStartCol = getSelStartCol();
            intEndRow = getSelEndRow();
            intEndCol = getSelEndCol();
            //��Delete��ɾ��
            if (clearType.equals("Delete"))
            {
                //���sheet2����Ӧ�ķ�Χ
                for (i = intStartRow; i <= intEndRow; i++)
                {
                    for (j = intStartCol; j <= intEndCol; j++)
                    {
                        setText(0, i, j, "");
                        setText(1, i, j, "");
                    }
                }
            }
            //��BackSpace��ɾ��
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
     * ����Ԫ�����
     * @param shiftType ���뷽ʽ
     */
    public void insertUnit(short shiftType)
    {
        int intStartRow, intStartCol, intEndRow, intEndCol;
        try
        {
            //��sheet1�н��в������
            editInsert(shiftType);
            //��ȡ���в��������λ��
            intStartRow = getSelStartRow();
            intStartCol = getSelStartCol();
            intEndRow = getSelEndRow();
            intEndCol = getSelEndCol();
            //��sheet2����Ӧ��λ�ý��в������
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
     * ����Ԫ��
     * @param shiftType ɾ����ʽ
     */
    public void deleteUnit(short shiftType)
    {
        int intStartRow, intStartCol, intEndRow, intEndCol;
        try
        {
            //��sheet1�н���ɾ������
            editDelete(shiftType);
            //��ȡ����ɾ��������λ��
            intStartRow = getSelStartRow();
            intStartCol = getSelStartCol();
            intEndRow = getSelEndRow();
            intEndCol = getSelEndCol();
            //��sheet2����Ӧ��λ�ý���ɾ������
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
     * ���õ�Ԫ������
     * */
    public void setFont()
    {
        try
        {
            //��������Ի���
            FormatCellsDlg dlg = new FormatCellsDlg((JBook)this,
                    FormatCellsDlg.kFontPage);
            //��ʾ����Ի���
            dlg.show();
        }
        catch (Exception ef)
        {
            ef.printStackTrace();
        }
    }

    /**
     * ���õ�Ԫ��߿�
     * */
    public void setBorder()
    {
        try
        {
            /**�����߿�Ի���*/
            FormatCellsDlg dlg = new FormatCellsDlg((JBook)this,
                    FormatCellsDlg.kBorderPage);
            /**��ʾ�߿�Ի���*/
            dlg.show();
        }
        catch (Exception eb)
        {
            eb.printStackTrace();
        }
    }

    /**
     * ����������ʾ��ʽ
     * */
    public void setNumberFormat()
    {
        try
        {
            //����������ʾ��ʽ�Ի���
            FormatCellsDlg dlg = new FormatCellsDlg((JBook)this,
                    FormatCellsDlg.kNumberPage);
            //��ʾ������ʾ��ʽ�Ի���
            dlg.show();
        }
        catch (Exception ed)
        {
            ed.printStackTrace();
        }
    }

    /**
     * ���õ�Ԫ����뷽ʽ
     * */
    public void setAlign()
    {
        try
        {
            FrameCellAlign align = new FrameCellAlign((JBook)this);
            //�����Ի���Ĵ�С��λ��
            align.setSize(250, 300);
            align.setModal(true);
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            align.setLocation((d.width - align.getSize().width) / 2,
                              (d.height - align.getSize().height) / 2);
            //��ʾ���뷽ʽ�Ի���
            align.open();
        }
        catch (Exception eAlign)
        {
            eAlign.printStackTrace();
        }
    }

    /**
     * ������Ԫ���и��п�
     * */
    public void setHeightWidth()
    {
        try
        {
            FrameCellHeight height = new FrameCellHeight((JBook)this);
            //�����Ի���Ĵ�С��λ��
            height.setSize(230, 270);
            height.setModal(true);
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            height.setLocation((d.width - height.getSize().width) / 2,
                               (d.height - height.getSize().height) / 2);
            //��ʾ�Ի���
            height.open();
        }
        catch (Exception eHeigh)
        {
            eHeigh.printStackTrace();
        }
    }

    /**
     * ����ѡ����Ԫ�������
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
     * ����ѡ����Ԫ�����
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
     * ����ѡ����Ԫ���Ҷ���
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
     * ����ѡ����Ԫ����о���
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
     * ��ȡѡ����Ԫ�������
     * @return ѡ����Ԫ�������
     */
    public String getText()
    {
        String text = "";
        int intRow = getActiveRow();
        int intCol = getActiveCol();
        try
        {
            text = getText(1, intRow, intCol);
            //���ѡ���ĵ�Ԫ��û�������򷵻ظõ�Ԫ���λ����Ϣ
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
     * ����ѡ����Ԫ�������
     * @param text ѡ����Ԫ�������
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
     * ��ȡ�����ȫ����Ϣ
     * @return �����ȫ����Ϣ
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
     * ���ñ����ȫ����Ϣ
     * @param text �����ȫ����Ϣ
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

//////////////////////�������ݲ���///////////////////////////////

    /**
     * ��ָ��������
     * @param report ������Ϣ
     */
    public void open(ReportMain report)
    {
        /**�����½��������Ϣ��ʼ��model*/
        if (report.getReportAtt().equals("�̶���ʽ"))
        {
            model = new CrossReport();
        }
        if (report.getReportAtt().equals("�嵥��ʽ"))
        {
            model = new PlaneReport();
        }
        model.setReport(report);
        readFormat(report);
        readDefine(report);
    }

    /**
     * �򿪱�������
     * @param file ���������ļ���
     */
    public void open(String file)
    {
        try
        {
            /**��ȡ�����ʽ*/
            initWorkbook();
            readURL(new URL(SysConfig.TRUEHOST + "down.jsp?type=data&file=" +
                            file)
                    , new com.f1j.ss.ReadParams());
            /**��ʼ������*/
            setDefaultFontName("����");
            setSheet(0);
            setAllowFillRange(false);
            setAllowMoveRange(false);
            setShowTabs(JBook.eTabsBottom);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "���ļ�ʧ��!");
        }
    }

    /**
     * ��ȡ�����ʽ
     * @param report ������Ϣ
     */
    private void readFormat(ReportMain report)
    {
        /**ƴ���ļ���*/
        String file = report.getBranchId() +
                      "_" + report.getReportId() +
                      "_" + report.getReportEdition() +
                      ".xls";
        try
        {
            /**��ȡ�����ʽ*/
            initWorkbook();
            readURL(new URL(SysConfig.TRUEHOST + "down.jsp?type=define&file=" +
                            file)
                    , new com.f1j.ss.ReadParams());
            /**��ʼ������*/
            setDefaultFontName("����");
            setSheet(0);
            setAllowFillRange(false);
            setAllowMoveRange(false);
            setShowTabs(JBook.eTabsBottom);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "���ļ�ʧ��!");
        }
    }

    /**
     * ��ȡ��������(����Դ)
     * @param report ������Ϣ
     */
    public void readDefine(ReportMain report)
    {
        String strReportAtt = report.getReportAtt();
        if (strReportAtt.equals("�̶���ʽ"))
        {
            setModel(new CrossReport());
        }
        if (strReportAtt.equals("�嵥��ʽ"))
        {
            setModel(new PlaneReport());
        }
        model.readDefine(report);
    }

    /**
     * �½�һ�ű���
     * @param newReport ������Ϣ
     * @param newSource ��������Դ
     */
    public void newReport(ReportMain newReport, Vector newSource)
    {
        /**�����½��������Ϣ��ʼ��model*/
        if (newReport.getReportAtt().equals("�̶���ʽ"))
        {
            model = new CrossReport();
        }
        if (newReport.getReportAtt().equals("�嵥��ʽ"))
        {
            model = new PlaneReport();
        }
        model.setReport(newReport);
        model.setSource(newSource);
        /**���汨����Ϣ*/
        saveReportInfo();
        /**�½���ʽ�ļ�*/
        newFormat();
        /**�½������ļ�*/
        newDefine();
    }

    /**
     * �޸ĵ�ǰ������Ϣ(������Ϣ������Դ��Ϣ)
     * @param updReport �µı�����Ϣ
     * @param updSource �µ�����Դ��Ϣ
     */
    public void updReport(ReportMain updReport, Vector updSource)
    {
        /**�����½��������Ϣ��ʼ��model*/
        if (updReport.getReportAtt().equals("�̶���ʽ"))
        {
            model = new CrossReport();
        }
        if (updReport.getReportAtt().equals("�嵥��ʽ"))
        {
            model = new PlaneReport();
        }
        /**�����µı�����Ϣ*/
        model.setReport(updReport);
        /**��ȡ������*/
        model.readDefine(updReport);
        /**�����µ�����Դ��Ϣ*/
        model.setSource(updSource);
        /**���汨����*/
        model.saveDefine();
    }

    /**
     * ���浱ǰ�����ʽ(�½�)
     */
    private void newFormat()
    {
        ReportMain tReportMain = model.getReport();
        try
        {
            /**���汨���ʽ*/
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
            JOptionPane.showMessageDialog(null, "��ʽ�ļ�����ʧ��!");
            eOK.printStackTrace();
        }
    }

    /**
     * ���浱ǰ�����ʽ
     */
    private void saveFormat()
    {
        ReportMain tReportMain = model.getReport();
        /**ƴ���ļ���*/
        String strFileName = tReportMain.getBranchId().trim() + "_" +
                             tReportMain.getReportId().trim() + "_" +
                             tReportMain.getReportEdition().trim() +
                             ".xls";
        try
        {
            /**���汨���ʽ*/
            URL url = new URL(SysConfig.TRUEHOST + "up.jsp?type=define&file=" +
                              strFileName);
            writeURL(url, new com.f1j.ss.WriteParams(JBook.eFileExcel97));
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "��ʽ�ļ�����ʧ��!");
            e.printStackTrace();
        }
    }

    /**
     * ���浱ǰ������Ϣ
     */
    private void saveReportInfo()
    {
        model.getReport().insert();
    }

    /**
     * ���浱ǰ����Ķ���(�½�)
     */
    private void newDefine()
    {
        model.newDefine();
    }

    /**
     * ���浱ǰ����Ķ���
     */
    public void saveDefine()
    {
        if (model == null)
        {
            return;
        }
        /**���汨���ʽ*/
        saveFormat();
        //��Formula One �ؼ����ռ�Row��Col��Cell����Ϣ�����õ�ReportModel��
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
     * �رյ�ǰ����
     */
    public void closeReport()
    {
        if (JOptionPane.showConfirmDialog
            (null, "�����������޸���?", "�رձ���",
             JOptionPane.OK_CANCEL_OPTION,
             JOptionPane.QUESTION_MESSAGE)
            == JOptionPane.OK_OPTION)
        {
            saveFormat();
            saveReportInfo();
            saveDefine();
        }
        /**�����������*/
        model = null;
        mapSheetOne = new HashMap();
        mapSheetTwo = new HashMap();
        /**��������ʽ*/
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
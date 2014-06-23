/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;

import com.f1j.ss.Constants;

/**
 * <p>Title: lis</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: sinosoft</p>
 * @author lh
 * @version 1.0
 */

public class F1PrintVTS
{
    private String m_strTemplatePath = "";
    private com.f1j.ss.BookModelImpl bmTemplate = new com.f1j.ss.BookModelImpl();
    private int row = 0;
    private int col = 0;
    private int maxCol = 0;
    private int maxRow = 0;

    public F1PrintVTS(String strTemplatePath, int cMaxCol)
    {
        m_strTemplatePath = strTemplatePath;
        maxCol = cMaxCol;
        try
        {
            bmTemplate.read(new FileInputStream(m_strTemplatePath),
                            new com.f1j.ss.ReadParams());
//      this.deleteRange(bmTemplate,0,maxCol)
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * ����Ӧ��addrow�����ӵڼ��в���
     * @param cRow int
     */
    public void setRunRow(int cRow)
    {
        this.row = cRow;
        this.maxRow = cRow;
    }

    /**
     * ���������
     * @param StrArr String[]
     */
    public void addRow(String[] StrArr)
    {
        for (col = 0; col < this.maxCol; col++)
        {
            try
            {
                bmTemplate.setText(row, col, StrArr[col]);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        row += 1;
        maxRow = row;
    }

    public boolean setOne(int i, int j, String cData)
    {
        try
        {
            if (j < maxCol)
            {
                bmTemplate.setText(i, j, cData);
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Exception ex)
        {
//      System.out.println("errorline:"+i);
            ex.printStackTrace();
            return false;
        }
    }

    public void addOne(String cData)
    {
        try
        {
            bmTemplate.setText(row, col, cData);
            col += 1;
            if (col == maxCol)
            {
                row += 1;
                maxRow = row;
                col = 0;
            }
        }
        catch (Exception ex)
        {
            System.out.println("errorline:" + row);
            ex.printStackTrace();
        }
    }

    public void addAll(SSRS cSSRS)
    {
        this.maxRow = cSSRS.getMaxRow();
        this.maxCol = cSSRS.getMaxCol();
        for (row = 0; row < this.maxRow; row++)
        {
            for (col = 0; col <= this.maxCol; col++)
            {
                try
                {
                    bmTemplate.setText(row, col, cSSRS.GetText(row + 1, col + 1));
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * �����������������
     * @return OutputStream
     */
    public OutputStream getInputStream()
    {
        try
        {
            bmTemplate.setMaxRow(maxRow - 1);
            bmTemplate.setMaxCol(maxCol - 1);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bmTemplate.setSelection(0, 0, maxRow - 1, maxCol - 1);
            bmTemplate.setPrintArea();

            bmTemplate.setAllowSelections(false); //��ӡ��ʾ���治��ѡ��
            bmTemplate.setAllowObjectSelections(false); //��ӡ��ʾ���治��ѡ��
            bmTemplate.setShowGridLines(false); //ȥ����ʾ�е�����
            bmTemplate.setShowColHeading(false); //ȥ����ʾ����ͷ
            bmTemplate.setShowRowHeading(false); //ȥ����ʾ����ͷ
            bmTemplate.setShowEditBar(false); //ȥ����ʾ��bar
            bmTemplate.setShowEditBarCellRef(false); //ȥ����ʾ��bar
            bmTemplate.setShowTabs(Constants.eTabsOff); //ȥ����ʾ��sheet
//bmTemplate.setShowVScrollBar(com.f1j.ss.BookModelImpl.eShowOff);
//bmTemplate.setShowHScrollBar(com.f1j.ss.BookModelImpl.eShowOff);

            bmTemplate.setPrintGridLines(false); //�ڴ�ӡʱȥ������
            bmTemplate.setPrintHeader(""); //�ڴ�ӡʱȥ��ͷ����
//            bmTemplate.setPrintFooter(bmTemplate.getPrintFooter());
            bmTemplate.setPrintFooter(""); //�ڴ�ӡʱȥ��ҳ��

            bmTemplate.saveViewInfo();
            bmTemplate.write(out,
                             new com.f1j.ss.WriteParams(Constants.
                    eFileFormulaOne6));
            return out;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * ���Ժ���
     * @param args String[]
     */
    public static void main(String[] args)
    {
//        String strTemplatePath = "E:\\ui\\f1print\\template\\test.VTS";
//        F1PrintVTS a = new F1PrintVTS(strTemplatePath, 2);
//        String b[] =
//                     {
//                     "1", "2"};
//        a.addRow(b);
//        a.addRow(b);
//        a.addRow(b);
    }
}

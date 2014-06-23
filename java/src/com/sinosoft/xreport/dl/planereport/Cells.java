package com.sinosoft.xreport.dl.planereport;


/**
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author wuxiao
 * @version 1.0
 */
public class Cells
{

    private int cellCount;
    private Cell cell[];

    public Cells()
    {}

    /**
     * ȡ�õ�Ԫ�����
     * @return ��ʾ��Ԫ�����
     */
    public int getCellCount()
    {
        return cellCount;
    }

    /**
     * ȡ�õ�Ԫ����Ϣ
     * @return ��ʾ��Ԫ����Ϣ
     */
    public Cell[] getCell()
    {
        return cell;
    }

    /**
     * ���õ�Ԫ�����
     * @param param ��Ԫ�����
     */
    public void setCellCount(int param)
    {
        cellCount = param;
        cell = new Cell[param];
    }

    /**
     * ���õ�Ԫ����Ϣ
     * @param param ��Ԫ����Ϣ
     */
    public void setCell(Cell[] param)
    {
        cell = param;
    }

    /**
     * ����XML�ַ���
     * @return XML�ַ���
     */
    public String toXML()
    {
        String sXML = "<cells>\r\n";
        for (int i = 0; i < cellCount; i++)
        {
            sXML = sXML + cell[i].toXML();
        }
        sXML = sXML + "</cells>\r\n";
        return sXML;
    }
}
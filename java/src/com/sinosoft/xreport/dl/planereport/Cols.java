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
public class Cols
{

    private int colCount;
    private Col col[];

    public Cols()
    {}

    /**
     * ȡ����ʾ��ͷ����
     * @return ��ʾ��ͷ����
     */
    public int getColCount()
    {
        return colCount;
    }

    /**
     * ȡ����ʾ��ͷ��Ϣ
     * @return ��ʾ��ͷ��Ϣ
     */
    public Col[] getCol()
    {
        return col;
    }

    /**
     * ������ʾ��ͷ����
     * @param param ��ʾ��ͷ����
     */
    public void setColCount(int param)
    {
        colCount = param;
        col = new Col[param];
    }

    /**
     * ������ʾ��ͷ��Ϣ
     * @param param ��ʾ��ͷ��Ϣ
     */
    public void setCol(Col[] param)
    {
        col = param;
    }

    /**
     * ����XML�ַ���
     * @return XML�ַ���
     */
    public String toXML()
    {
        String sXML = "<cols>\r\n";
        for (int i = 0; i < colCount; i++)
        {
            sXML = sXML + col[i].toXML();
        }
        sXML = sXML + "</cols>\r\n";
        return sXML;
    }
}
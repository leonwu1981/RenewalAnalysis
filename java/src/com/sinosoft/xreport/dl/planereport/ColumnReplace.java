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
public class ColumnReplace
{
    private int colCount;
    private ReplaceCol col[];

    /**
     * ȡ�ô���ת���и���
     * @return ����ת���и���
     */
    public int getReplaceColCount()
    {
        return colCount;
    }

    /**
     * ȡ�ô���ת������Ϣ
     * @return ����ת������Ϣ
     */
    public ReplaceCol[] getReplaceCol()
    {
        return col;
    }

    /**
     * ���ô���ת���и���
     * @param param ����ת���и���
     */
    public void setReplaceColCount(int param)
    {
        colCount = param;
        col = new ReplaceCol[param];
    }

    /**
     * ���ô���ת������Ϣ
     * @param param ����ת������Ϣ
     */
    public void setReplaceCol(ReplaceCol[] param)
    {
        col = param;
    }

    /**
     * ����XML�ַ���
     * @return XML�ַ���
     */
    public String toXML()
    {
        String sXML = "<columnReplace>\r\n";
        for (int i = 0; i < colCount; i++)
        {
            sXML = sXML + col[i].toXML();
        }
        sXML = sXML + "</columnReplace>\r\n";
        return sXML;
    }
}
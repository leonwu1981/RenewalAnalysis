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
public class SumColumns
{

    private int colCount;
    private SumCol col[];

    public SumColumns()
    {}

    /**
     * ȡ��С�ơ��ϼ��и���
     * @return С�ơ��ϼ��и���
     */
    public int getSumColCount()
    {
        return colCount;
    }

    /**
     * ȡ��С�ơ��ϼ�����Ϣ
     * @return С�ơ��ϼ�����Ϣ
     */
    public SumCol[] getSumCol()
    {
        return col;
    }

    /**
     * ����С�ơ��ϼ��и���
     * @param param С�ơ��ϼ��и���
     */
    public void setSumColCount(int param)
    {
        colCount = param;
        col = new SumCol[param];
    }

    /**
     * ����С�ơ��ϼ�����Ϣ
     * @param param С�ơ��ϼ�����Ϣ
     */
    public void setSumCol(SumCol[] param)
    {
        col = param;
    }

    /**
     * ����XML�ַ���
     * @return XML�ַ���
     */
    public String toXML()
    {
        String sXML = "<sumColumns>\r\n";
        for (int i = 0; i < colCount; i++)
        {
            sXML = sXML + col[i].toXML();
        }
        sXML = sXML + "</sumColumns>\r\n";
        return sXML;
    }

    /**
     * ����SQL�ַ���
     * @return SQL�ַ���
     */
    public String toSQL()
    {
        String sql = "";
        for (int i = 0; i < colCount; i++)
        {
            if (!sql.equals(""))
            {
                sql = sql + ",";
            }
            sql = sql + "sum(" + col[i].getDbName() + ") " + col[i].getDbName();
        }
        return sql;
    }
}
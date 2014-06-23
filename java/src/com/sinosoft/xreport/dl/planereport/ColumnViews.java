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
public class ColumnViews
{

    private int colCount;
    private ViewCol col[];

    public ColumnViews()
    {}

    /**
     * ȡ�������и���
     * @return �����и���
     */
    public int getViewColCount()
    {
        return colCount;
    }

    /**
     * ȡ����������Ϣ
     * @return ��������Ϣ
     */
    public ViewCol[] getViewCol()
    {
        return col;
    }

    /**
     * ���������и���
     * @param param �����и���
     */
    public void setViewColCount(int param)
    {
        colCount = param;
        col = new ViewCol[param];
    }

    /**
     * ������������Ϣ
     * @param param ��������Ϣ
     */
    public void setViewCol(ViewCol[] param)
    {
        col = param;
    }

    /**
     * ����XML�ַ���
     * @return XML�ַ���
     */
    public String toXML()
    {
        String sXML = "<columnViews>\r\n";
        for (int i = 0; i < colCount; i++)
        {
            sXML = sXML + col[i].toXML();
        }
        sXML = sXML + "</columnViews>\r\n";
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
            if (col[i].getSource().equalsIgnoreCase("db"))
            {
                if (!sql.equals(""))
                {
                    sql = sql + ",";
                }
                sql = sql + col[i].getDbName();
            }
        }
        return sql;
    }
}
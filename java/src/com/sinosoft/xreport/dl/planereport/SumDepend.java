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
public class SumDepend
{
    private int colCount;
    private DependCol col[];

    public SumDepend()
    {}

    /**
     * ȡ��С�ơ��ϼ������и���
     * @return С�ơ��ϼ������и���
     */
    public int getSumDependColCount()
    {
        return colCount;
    }

    /**
     * ȡ��С�ơ��ϼ���������Ϣ
     * @return С�ơ��ϼ���������Ϣ
     */
    public DependCol[] getSumDependCol()
    {
        return col;
    }

    /**
     * ����С�ơ��ϼ������и���
     * @param param С�ơ��ϼ������и���
     */
    public void setSumDependColCount(int param)
    {
        colCount = param;
        col = new DependCol[param];
    }

    /**
     * ����С�ơ��ϼ���������Ϣ
     * @param param С�ơ��ϼ���������Ϣ
     */
    public void setSumDependCol(DependCol[] param)
    {
        col = param;
    }

    /**
     * ����XML�ַ���
     * @return XML�ַ���
     */
    public String toXML()
    {
        String sXML = "<sumDepend>\r\n";
        for (int i = 0; i < colCount; i++)
        {
            sXML = sXML + col[i].toXML();
        }
        sXML = sXML + "</sumDepend>\r\n";
        return sXML;
    }

    /**
     * �����������SQL�ַ���
     * @return �������SQL�ַ���
     */
    public String[] toSumSQL()
    {
        String sql[] = new String[colCount];
        for (int i = 0; i < colCount; i++)
        {
            sql[i] = "";
            for (int j = 0; j < i + 1; j++)
            {
                if (!sql[i].equals(""))
                {
                    sql[i] = sql[i] + ",";
                }
                sql[i] = sql[i] + col[j].getDbName();
            }
        }
        return sql;
    }

    /**
     * ��������SQL�ַ���
     * @return ����SQL�ַ���
     */
    public String toOrderBySQL()
    {
        String sql = "";
        for (int i = 0; i < colCount; i++)
        {
            if (!sql.equals(""))
            {
                sql = sql + ",";
            }
            sql = sql + col[i].getDbName();
        }
        return sql;
    }
}
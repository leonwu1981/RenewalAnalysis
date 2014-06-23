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
     * 取得小计、合计依据列个数
     * @return 小计、合计依据列个数
     */
    public int getSumDependColCount()
    {
        return colCount;
    }

    /**
     * 取得小计、合计依据列信息
     * @return 小计、合计依据列信息
     */
    public DependCol[] getSumDependCol()
    {
        return col;
    }

    /**
     * 设置小计、合计依据列个数
     * @param param 小计、合计依据列个数
     */
    public void setSumDependColCount(int param)
    {
        colCount = param;
        col = new DependCol[param];
    }

    /**
     * 设置小计、合计依据列信息
     * @param param 小计、合计依据列信息
     */
    public void setSumDependCol(DependCol[] param)
    {
        col = param;
    }

    /**
     * 返回XML字符串
     * @return XML字符串
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
     * 返回求和依赖SQL字符串
     * @return 求和依赖SQL字符串
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
     * 返回排序SQL字符串
     * @return 排序SQL字符串
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
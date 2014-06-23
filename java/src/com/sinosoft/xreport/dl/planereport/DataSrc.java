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
public class DataSrc
{

    private String subject = "";
    private ColumnViews colViews;
    private ColumnReplace colReplaces;
    private SumColumns colSums;
    private SumDepend colSumDepends;
    private StartCell startCell;
    private String strWhere = "";
    private int appendCount;
    private Append aAppend[];

    public DataSrc()
    {}

    /**
     * 取得数据列信息
     * @return 数据列信息
     */
    public ColumnViews getColViews()
    {
        return colViews;
    }

    /**
     * 取得代码转换列信息
     * @return 代码转换列信息
     */
    public ColumnReplace getColReplaces()
    {
        return colReplaces;
    }

    /**
     * 取得小计合计列信息
     * @return 小计合计列信息
     */
    public SumColumns getColSums()
    {
        return colSums;
    }

    /**
     * 取得小计合计依据列信息
     * @return 小计合计依据列信息
     */
    public SumDepend getColSumDepends()
    {
        return colSumDepends;
    }

    /**
     * 取得起始单元格信息
     * @return 起始单元格信息
     */
    public StartCell getStartCell()
    {
        return startCell;
    }

    /**
     * 取得主题表取数条件
     * @return 主题表取数条件
     */
    public String getStrWhere()
    {
        return strWhere;
    }

    /**
     * 取得主题表
     * @return 主题表
     */
    public String getSubject()
    {
        return subject;
    }

    /**
     * 取得附加数据源SQL
     * @return 附加数据源SQL
     */
    public Append[] getAppend()
    {
        return aAppend;
    }

    /**
     * 设置数据列信息
     * @param param 数据列信息
     */
    public void setColViews(ColumnViews param)
    {
        colViews = param;
    }

    /**
     * 设置代码转换列信息
     * @param param 代码转换列信息
     */
    public void setColReplaces(ColumnReplace param)
    {
        colReplaces = param;
    }

    /**
     * 设置小计合计列信息
     * @param param 小计合计列信息
     */
    public void setColSums(SumColumns param)
    {
        colSums = param;
    }

    /**
     * 设置小计合计依据列信息
     * @param param 小计合计依据列信息
     */
    public void setColSumDepends(SumDepend param)
    {
        colSumDepends = param;
    }

    /**
     * 设置起始单元格信息
     * @param param 起始单元格信息
     */
    public void setStartCell(StartCell param)
    {
        startCell = param;
    }

    /**
     * 设置主题表取数条件
     * @param param 主题表取数条件
     */
    public void setStrWhere(String param)
    {
        strWhere = param;
    }

    /**
     * 设置主题表
     * @param param 主题表
     */
    public void setSubject(String param)
    {
        subject = param;
    }

    /**
     * 设置附加数据源SQL
     * @param param 附加数据源SQL
     */
    public void setAppend(Append param[])
    {
        aAppend = param;
    }

    /**
     * 获取附加数据源个数
     * @return 附加数据源个数
     */
    public int getAppendCount()
    {
        return appendCount;
    }

    /**
     * 设置附加数据源个数
     * @param param 附加数据源个数
     */
    public void setAppendCount(int param)
    {
        appendCount = param;
        aAppend = new Append[param];
    }

    /**
     * 返回XML字符串
     * @return XML字符串
     */
    public String toXML()
    {
        String sXML = "<dataSrc>\r\n";
        if (subject == null)
        {
            sXML = sXML + "<subject>" + subject + "</subject>\r\n";
        }
        else
        {
            sXML = sXML + "<subject/>";
        }
        if (colViews == null)
        {
            sXML = sXML + colViews.toXML();
        }
        else
        {
            sXML = sXML + "<columnViews/>";
        }
        if (colReplaces == null)
        {
            sXML = sXML + colReplaces.toXML();
        }
        else
        {
            sXML = sXML + "<subject/>";
        }
        if (colSums == null)
        {
            sXML = sXML + colSums.toXML();
        }
        else
        {
            sXML = sXML + "<sumColumns/>";
        }
        if (colSumDepends == null)
        {
            sXML = sXML + colSumDepends.toXML();
        }
        else
        {
            sXML = sXML + "<sumDepend/>";
        }
        if (startCell == null)
        {
            sXML = sXML + startCell.toXML();
        }
        else
        {
            sXML = sXML + "<startCell/>";
        }
        if (colViews == null)
        {
            sXML = sXML + "<strWhere>" + strWhere + "</strWhere>\r\n";
        }
        else
        {
            sXML = sXML + "<strWhere/>";
        }
        if (aAppend.length >= 0)
        {
            for (int i = 0; i < aAppend.length; i++)
            {
                if (aAppend[i] != null)
                {
                    sXML = sXML + aAppend[i].toXML();
                }
                else
                {
                    sXML = sXML + "";
                }
            }
        }
        else
        {
            sXML = sXML + "<append/>";
        }
        sXML = sXML + "</dataSrc>\r\n";
        return sXML;
    }

    /**
     * 取得主题表SQL
     * @return 主题表SQL
     */
    public String getSubjectSQL()
    {
        String sql = "";
        String selList = colViews.toSQL();
        String orderList = colSumDepends.toOrderBySQL();
        if (!selList.equals(""))
        {
            sql = "select " + selList
                  + " from " + subject
                  + " " + strWhere;
            if (!orderList.equals(""))
            {
                sql = sql + " order by " + orderList;
            }
        }
        return sql;
    }

    /**
     * 取得小计计算SQL
     * @return 小计计算SQL
     */
    public String[] getSumSQL()
    {
        String selList = colSums.toSQL();
        String orderList[] = colSumDepends.toSumSQL();
        String sql[] = new String[orderList.length];
        for (int i = 0; i < orderList.length; i++)
        {
            if (!selList.equals(""))
            {
                sql[i] = selList;
                if (!orderList[i].equals(""))
                {
                    sql[i] = sql[i] + ",";
                    sql[i] = sql[i] + orderList[i];
                }
                sql[i] = "select " + sql[i]
                         + " from " + subject
                         + " " + strWhere;
                if (!orderList[i].equals(""))
                {
                    sql[i] = sql[i] + " group by " + orderList[i];
                    sql[i] = sql[i] + " order by " + orderList[i];
                }
            }
        }
        return sql;
    }

    /**
     * 取得合计计算SQL
     * @return 合计计算SQL
     */
    public String getTotalSQL()
    {
        String selList = colSums.toSQL();
        String sql = "";
        if (!selList.equals(""))
        {
            sql = "select " + selList
                  + " from " + subject
                  + " " + strWhere;
        }
        return sql;
    }

    /**
     * 取得附加数据源SQL
     * @return 附加数据源SQL
     */
    public String[] getAppendSQL()
    {
        String sql[] = new String[aAppend.length];
        for (int i = 0; i < aAppend.length; i++)
        {
            sql[i] = aAppend[i].getSource();
        }
//        int orderByIdx = sql.toLowerCase().indexOf("order by");
//        int groupByIdx = sql.toLowerCase().indexOf("group by");
//        String orderby = "";
//        if (orderByIdx >= 0 && groupByIdx >= 0 && groupByIdx > orderByIdx){
//            orderby = sql.substring(orderByIdx,groupByIdx);
//        }
//        if (orderByIdx >= 0 && groupByIdx >= 0 && groupByIdx < orderByIdx){
//            orderby = sql.substring(orderByIdx);
//        }
//        if (orderByIdx >= 0 && groupByIdx == 0){
//            orderby = sql.substring(orderByIdx);
//        }
//        String orderList = "";
//        for (int i = 0; i < aAppend.getAppendColCount(); i++){
//            if (!orderList.equals("")) orderList = orderList + ",";
//            orderList = orderList + aAppend.getAppendCol()[i].getDbName();
//        }
//        if (orderby.equals("")
        return sql;
    }
}
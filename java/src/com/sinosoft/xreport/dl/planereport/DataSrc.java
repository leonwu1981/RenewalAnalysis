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
     * ȡ����������Ϣ
     * @return ��������Ϣ
     */
    public ColumnViews getColViews()
    {
        return colViews;
    }

    /**
     * ȡ�ô���ת������Ϣ
     * @return ����ת������Ϣ
     */
    public ColumnReplace getColReplaces()
    {
        return colReplaces;
    }

    /**
     * ȡ��С�ƺϼ�����Ϣ
     * @return С�ƺϼ�����Ϣ
     */
    public SumColumns getColSums()
    {
        return colSums;
    }

    /**
     * ȡ��С�ƺϼ���������Ϣ
     * @return С�ƺϼ���������Ϣ
     */
    public SumDepend getColSumDepends()
    {
        return colSumDepends;
    }

    /**
     * ȡ����ʼ��Ԫ����Ϣ
     * @return ��ʼ��Ԫ����Ϣ
     */
    public StartCell getStartCell()
    {
        return startCell;
    }

    /**
     * ȡ�������ȡ������
     * @return �����ȡ������
     */
    public String getStrWhere()
    {
        return strWhere;
    }

    /**
     * ȡ�������
     * @return �����
     */
    public String getSubject()
    {
        return subject;
    }

    /**
     * ȡ�ø�������ԴSQL
     * @return ��������ԴSQL
     */
    public Append[] getAppend()
    {
        return aAppend;
    }

    /**
     * ������������Ϣ
     * @param param ��������Ϣ
     */
    public void setColViews(ColumnViews param)
    {
        colViews = param;
    }

    /**
     * ���ô���ת������Ϣ
     * @param param ����ת������Ϣ
     */
    public void setColReplaces(ColumnReplace param)
    {
        colReplaces = param;
    }

    /**
     * ����С�ƺϼ�����Ϣ
     * @param param С�ƺϼ�����Ϣ
     */
    public void setColSums(SumColumns param)
    {
        colSums = param;
    }

    /**
     * ����С�ƺϼ���������Ϣ
     * @param param С�ƺϼ���������Ϣ
     */
    public void setColSumDepends(SumDepend param)
    {
        colSumDepends = param;
    }

    /**
     * ������ʼ��Ԫ����Ϣ
     * @param param ��ʼ��Ԫ����Ϣ
     */
    public void setStartCell(StartCell param)
    {
        startCell = param;
    }

    /**
     * ���������ȡ������
     * @param param �����ȡ������
     */
    public void setStrWhere(String param)
    {
        strWhere = param;
    }

    /**
     * ���������
     * @param param �����
     */
    public void setSubject(String param)
    {
        subject = param;
    }

    /**
     * ���ø�������ԴSQL
     * @param param ��������ԴSQL
     */
    public void setAppend(Append param[])
    {
        aAppend = param;
    }

    /**
     * ��ȡ��������Դ����
     * @return ��������Դ����
     */
    public int getAppendCount()
    {
        return appendCount;
    }

    /**
     * ���ø�������Դ����
     * @param param ��������Դ����
     */
    public void setAppendCount(int param)
    {
        appendCount = param;
        aAppend = new Append[param];
    }

    /**
     * ����XML�ַ���
     * @return XML�ַ���
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
     * ȡ�������SQL
     * @return �����SQL
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
     * ȡ��С�Ƽ���SQL
     * @return С�Ƽ���SQL
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
     * ȡ�úϼƼ���SQL
     * @return �ϼƼ���SQL
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
     * ȡ�ø�������ԴSQL
     * @return ��������ԴSQL
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
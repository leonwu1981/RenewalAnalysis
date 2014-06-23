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
public class AppendCol
{

    private String colName = "";
    private String dbName = "";

    public AppendCol()
    {}

    /**
     * ��ȡ��������Դ��������
     * @return ��������Դ��������
     */
    public String getColName()
    {
        return colName;
    }

    /**
     * ���ø�������Դ��������
     * @param param ��������Դ��������
     */
    public void setColName(String param)
    {
        colName = param;
    }

    /**
     * ��ȡ��������Դ�����ж�Ӧ�����ݿ���
     * @return ��������Դ�����ж�Ӧ�����ݿ���
     */
    public String getDbName()
    {
        return dbName;
    }

    /**
     * ���ø�������Դ�����ж�Ӧ�����ݿ���
     * @param param ��������Դ�����ж�Ӧ�����ݿ���
     */
    public void setDbName(String param)
    {
        dbName = param;
    }

    /**
     * ��ȡ��������Դ�����е�xml��
     * @return ��������Դ�����е�xml��
     */
    public String toXML()
    {
        return "<appendCol name=\"" + colName
                + "\" dbName=\"" + dbName
                + "\"/>\r\n";
    }
}
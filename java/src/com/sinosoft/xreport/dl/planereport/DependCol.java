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
public class DependCol
{

    private String col;
    private String dbName;

    public DependCol()
    {}

    /**
     * ȡ��С�ơ��ϼ�����������
     * @return ������
     */
    public String getCol()
    {
        return col;
    }

    /**
     * ȡ��С�ơ��ϼ����������ݿ�����
     * @return ���ݿ�����
     */
    public String getDbName()
    {
        return dbName;
    }

    /**
     * ����С�ơ��ϼ�����������
     * @param param ������
     */
    public void setCol(String param)
    {
        col = param;
    }

    /**
     * ����С�ơ��ϼ����������ݿ�����
     * @param param ���ݿ�����
     */
    public void setDbName(String param)
    {
        dbName = param;
    }

    /**
     * ����XML�ַ���
     * @return XML�ַ���
     */
    public String toXML()
    {
        String sXML = "<dependCol col=\"" + col
                      + "\" dbname=\"" + dbName
                      + "\"/>\r\n";
        return sXML;
    }
}
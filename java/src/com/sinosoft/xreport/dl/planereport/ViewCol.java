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
public class ViewCol
{

    private String col;
    private String dbName;
    private boolean view;
    private String src;
    private String formula;
    private String location;

    public ViewCol()
    {}

    /**
     * ȡ������������
     * @return ����������
     */
    public String getCol()
    {
        return col;
    }

    /**
     * ȡ�������е����ݿ����
     * @return ���ݿ����
     */
    public String getDbName()
    {
        return dbName;
    }

    /**
     * ȡ���������Ƿ���ʾ
     * @return �Ƿ���ʾ
     */
    public boolean isView()
    {
        return view;
    }

    /**
     * ȡ�������е���Դ����������ݿ�ȡ��=��DB��
     * @return ��Դ
     */
    public String getSource()
    {
        return src;
    }

    /**
     * ȡ�������еļ��㹫ʽ
     * @return ���㹫ʽ
     */
    public String getFormula()
    {
        return formula;
    }

    /**
     * ȡ�������е���ʾλ��
     * @return ��ʾλ��
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * ���������е�����
     * @param param ����
     */
    public void setCol(String param)
    {
        col = param;
    }

    /**
     * ���������е����ݿ����
     * @param param ���ݿ����
     */
    public void setDbName(String param)
    {
        dbName = param;
    }

    /**
     * ���������е���ʾ����
     * @param param �Ƿ���ʾ
     */
    public void setView(boolean param)
    {
        view = param;
    }

    /**
     * ���������е���Դ
     * @param param ��Դ
     */
    public void setSource(String param)
    {
        src = param;
    }

    /**
     * ���������еļ��㹫ʽ
     * @param param ���㹫ʽ
     */
    public void setFormula(String param)
    {
        formula = param;
    }

    /**
     * ���������е���ʾλ��
     * @param param ��ʾλ��
     */
    public void setLocation(String param)
    {
        location = param;
    }

    /**
     * ����XML�ַ���
     * @return XML�ַ���
     */
    public String toXML()
    {
        String sXML = "<viewCol col=\"" + col
                      + "\" dbname=\"" + dbName
                      + "\" view=\"" + (view ? 1 : 0)
                      + "\" src=\"" + src
                      + "\" formula=\"" + formula
                      + "\" location=\"" + location + "\"/>\r\n";
        return sXML;
    }
}
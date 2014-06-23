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
public class Data
{

    private String value;
    private int row;
    private int col;
    private String name;

    public Data()
    {}

    /**
     * ȡ�ü�����
     * @return ������
     */
    public String getValue()
    {
        return value;
    }

    /**
     * ȡ�ü�����������
     * @return ������������
     */
    public int getRow()
    {
        return row;
    }

    /**
     * ȡ�ü�����������
     * @return ������������
     */
    public int getCol()
    {
        return col;
    }

    /**
     * ȡ�ü����������е�����
     * @return �����������е�����
     */
    public String getName()
    {
        return name;
    }

    /**
     * ���ü����������е�����
     * @param param �����������е�����
     */
    public void setName(String param)
    {
        name = param;
    }

    /**
     * ���ü�����
     * @param param ������
     */
    public void setValue(String param)
    {
        value = param;
    }

    /**
     * ���ü�����������
     * @param param ������������
     */
    public void setRow(int param)
    {
        row = param;
    }

    /**
     * ���ü�����������
     * @param param ������������
     */
    public void setCol(int param)
    {
        col = param;
    }

    /**
     * ����XML�ַ���
     * @return XML�ַ���
     */
    public String toXML()
    {
        String sXML = "<data value=\"" + value + "\""
                      + "row=\"" + String.valueOf(row) + "\""
                      + "name=\"" + name + "\""
                      + "col=\"" + String.valueOf(col)
                      + "\"/>\r\n";
        return sXML;
    }
}
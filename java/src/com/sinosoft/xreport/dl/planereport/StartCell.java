package com.sinosoft.xreport.dl.planereport;

public class StartCell
{

    private int row;
    private int col;

    public StartCell()
    {}

    /**
     * ȡ����ʼ��Ԫ����к�
     * @return ��ʼ��Ԫ����к�
     */
    public int getRow()
    {
        return row;
    }

    /**
     * ȡ����ʼ��Ԫ����к�
     * @return ��ʼ��Ԫ����к�
     */
    public int getCol()
    {
        return col;
    }

    /**
     * ������ʼ��Ԫ����к�
     * @param param ��ʼ��Ԫ����к�
     */
    public void setRow(int param)
    {
        row = param;
    }

    /**
     * ������ʼ��Ԫ����к�
     * @param param ��ʼ��Ԫ����к�
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
        String sXML = "<startCell col=\"" + col
                      + "\" row=\"" + row
                      + "\"/>\r\n";
        return sXML;
    }
}
package com.sinosoft.xreport.dl.planereport;

public class GCondition
{

    private String type;
    private String value;

    public GCondition()
    {}

    /**
     * ȡ��ȫ�ֲ�ѯ��������
     * @return ȫ�ֲ�ѯ��������
     */
    public String getType()
    {
        return type;
    }

    /**
     * ȡ��ȫ�ֲ�ѯ����ȡֵ
     * @return ȫ�ֲ�ѯ����ȡֵ
     */
    public String getValue()
    {
        return value;
    }

    /**
     * ����ȫ�ֲ�ѯ��������
     * @param param ȫ�ֲ�ѯ��������
     */
    public void setType(String param)
    {
        type = param;
    }

    /**
     * ����ȫ�ֲ�ѯ����ȡֵ
     * @param param ȫ�ֲ�ѯ����ȡֵ
     */
    public void setValue(String param)
    {
        value = param;
    }

    /**
     * ����XML�ַ���
     * @return XML�ַ���
     */
    public String toXML()
    {
        String sXML = "<condition type=\"" + type
                      + "\" value=\"" + value + "\"/>\r\n";
        return sXML;
    }
}
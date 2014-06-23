package com.sinosoft.xreport.dl.planereport;


public class GlobalConditions
{

    private int gConditionCount;
    private GCondition gCondition[];

    public GlobalConditions()
    {}

    /**
     * ȡ��ȫ�ֲ�ѯ��������
     * @return ȫ�ֲ�ѯ��������
     */
    public int getConditionCount()
    {
        return gConditionCount;
    }

    /**
     * ȡ��ȫ�ֲ�ѯ������Ϣ
     * @return ȫ�ֲ�ѯ������Ϣ
     */
    public GCondition[] getConditions()
    {
        return gCondition;
    }

    /**
     * ����ȫ�ֲ�ѯ��������
     * @param param ȫ�ֲ�ѯ��������
     */
    public void setConditionCount(int param)
    {
        gConditionCount = param;
        gCondition = new GCondition[param];
    }

    /**
     * ����ȫ�ֲ�ѯ������Ϣ
     * @param param ȫ�ֲ�ѯ������Ϣ
     */
    public void setConditions(GCondition[] param)
    {
        gCondition = param;
    }

    /**
     * ����XML�ַ���
     * @return XML�ַ���
     */
    public String toXML()
    {
        String sXML = "<global>\r\n";
        for (int i = 0; i < gConditionCount; i++)
        {
            sXML = sXML + gCondition[i].toXML();
        }
        sXML = sXML + "</global>\r\n";
        return sXML;
    }
}
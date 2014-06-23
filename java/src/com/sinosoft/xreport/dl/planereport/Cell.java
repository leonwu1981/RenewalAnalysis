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
public class Cell
{

    private String location;
    private String text;

    public Cell()
    {}

    /**
     * ȡ�õ�Ԫ��λ��
     * @return ��Ԫ��λ��
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * ȡ�õ�Ԫ������
     * @return ��Ԫ������
     */
    public String getText()
    {
        return text;
    }

    /**
     * ���õ�Ԫ��λ��
     * @param param ��Ԫ��λ��
     */
    public void setLocation(String param)
    {
        location = param;
    }

    /**
     * ���õ�Ԫ������
     * @param param ��Ԫ������
     */
    public void setText(String param)
    {
        text = param;
    }

    /**
     * ����XML�ַ���
     * @return XML�ַ���
     */
    public String toXML()
    {
        String sXML = "<cell location=\"" + location + "\">"
                      + text + "</cell>\r\n";
        return sXML;
    }
}
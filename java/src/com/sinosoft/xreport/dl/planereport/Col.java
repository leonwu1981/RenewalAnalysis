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
public class Col
{

    private String condition;
    private String formula;
    private String location;
    private String name;
    private String text;

    public Col()
    {}

    /**
     * ȡ����ͷ����
     * @return ��ͷ����
     */
    public String getCondition()
    {
        return condition;
    }

    /**
     * ȡ����ͷ��ʽ
     * @return ��ͷ��ʽ
     */
    public String getFormula()
    {
        return formula;
    }

    /**
     * ȡ����ͷλ��
     * @return ��ͷλ��
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * ȡ����ͷ����
     * @return ��ͷ����
     */
    public String getText()
    {
        return text;
    }

    /**
     * ȡ����ͷ����
     * @return ��ͷ����
     */
    public String getName()
    {
        return name;
    }

    /**
     * ������ͷ����
     * @param param ��ͷ����
     */
    public void setCondition(String param)
    {
        condition = param;
    }

    /**
     * ������ͷ��ʽ
     * @param param ��ͷ��ʽ
     */
    public void setFormula(String param)
    {
        formula = param;
    }

    /**
     * ������ͷλ��
     * @param param ��ͷλ��
     */
    public void setLocation(String param)
    {
        location = param;
    }

    /**
     * ������ͷ����
     * @param param ��ͷ����
     */
    public void setText(String param)
    {
        text = param;
    }

    /**
     * ������ͷ����
     * @param param ��ͷ����
     */
    public void setName(String param)
    {
        name = param;
    }

    /**
     * ����XML�ַ���
     * @return XML�ַ���
     */
    public String toXML()
    {
        String sXML = "<col condition=\"" + condition
                      + "\" formula=\"" + formula
                      + "\" location=\"" + location
                      + "\" name=\"" + name
                      + "\">" + text + "</col>\r\n";
        return sXML;
    }
}
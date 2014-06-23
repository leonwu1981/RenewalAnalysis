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
public class ReplaceCol
{

    private String col;
    private String src;
    private String dataCol;
    private String displayCol;

    public ReplaceCol()
    {}

    /**
     * ȡ�ô����滻�е�����
     * @return �����滻�е�����
     */
    public String getColumn()
    {
        return col;
    }

    /**
     * ȡ�ô����滻�еĴ���Դ��ѯSQL
     * @return ����Դ��ѯSQL
     */
    public String getSource()
    {
        return src;
    }

    /**
     * ȡ�ô����滻�е����ݶ�Ӧ����
     * @return ���ݶ�Ӧ����
     */
    public String getDataColumn()
    {
        return dataCol;
    }

    /**
     * ȡ�ô����滻�е�������ʾ����
     * @return ������ʾ����
     */
    public String getDisplayColumn()
    {
        return displayCol;
    }

    /**
     * ���ô����滻�е��к�
     * @param param �����滻�е��к�
     */
    public void setColumn(String param)
    {
        col = param;
    }

    /**
     * ���ô����滻�еĴ���Դ��ѯSQL
     * @param param ����Դ��ѯSQL
     */
    public void setSource(String param)
    {
        src = param;
    }

    /**
     * ���ô����滻�е����ݶ�Ӧ����
     * @param param ���ݶ�Ӧ����
     */
    public void setDataColumn(String param)
    {
        dataCol = param;
    }

    /**
     * ���ô����滻�е�������ʾ����
     * @param param ������ʾ����
     */
    public void setDisplayColumn(String param)
    {
        displayCol = param;
    }

    /**
     * ����XML�ַ���
     * @return XML�ַ���
     */
    public String toXML()
    {
        String sXML = "<replaceCol col=\"" + col
                      + "\" src=\"" + src
                      + "\" dataCol=\"" + dataCol
                      + "\" dispCol=\"" + displayCol + "\"/>\r\n";
        return sXML;
    }
}
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
public class Append
{

    private String source;
    private AppendCol appendCol[];
    private int appendColCount;
    private int col = 0;

    public Append()
    {}

    /**
     * ��ȡ��������Դ��ѯSQL
     * @return ��������Դ��ѯSQL
     */
    public String getSource()
    {
        return source;
    }

    /**
     * ��ȡ��������Դ��ʾ������
     * @return ��������Դ��ʾ������
     */
    public AppendCol[] getAppendCol()
    {
        return appendCol;
    }

    /**
     * ��ȡ��������Դ��ʾ�����и���
     * @return ��������Դ��ʾ�����и���
     */
    public int getAppendColCount()
    {
        return appendColCount;
    }

    /**
     * ���ø�������Դ��ѯSQL
     * @param param ��������Դ��ѯSQL
     */
    public void setSource(String param)
    {
        source = param;
    }

    /**
     * ��ȡ��������Դ�����ж�Ӧ�����ݿ���
     * @return ��������Դ�����ж�Ӧ�����ݿ���
     */
    public int getCol()
    {
        return col;
    }

    /**
     * ���ø�������Դ�����ж�Ӧ�����ݿ���
     * @param param ��������Դ�����ж�Ӧ�����ݿ���
     */

    public void setCol(int param)
    {
        col = param;
    }

    /**
     * ���ø�������Դ��ʾ������
     * @param param ��������Դ��ʾ������
     */
    public void setAppendCol(AppendCol[] param)
    {
        appendCol = param;
    }

    /**
     * ���ø�������Դ��ʾ�����и���
     * @param param ��������Դ��ʾ�����и���
     */
    public void setAppendColCount(int param)
    {
        appendColCount = param;
        appendCol = new AppendCol[param];
    }

    /**
     * ��ȡ��������Դ��xml��
     * @return ��������Դ��xml��
     */
    public String toXML()
    {
        String xml = "";
        xml = xml + "<append src=\"" + source + "\">\r\n";
        for (int i = 0; i < appendColCount; i++)
        {
            xml = xml + appendCol[i].toXML();
        }
        xml = xml + "</append>\r\n";
        return xml;
    }
}
//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\Location.java

package com.sinosoft.xreport.bl;

/**
 * ���ݿ��ʽ��.
 * ��Ϊ���F1�ķ���,Format���������ݿ��λ��,��С,Border����Ϣ.��ǰֻʵ�ּ�
 * ����,��������һ�㱨��ĸ�ʽ������,���ʵ�ָ���ĸ�ʽ,����F1�����Format��Ϣ.
 * ʹ��Decoratorģʽ,�Ľ����ݿ��չ��
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */
public class Format
{

    private String context;
    private String file;
    private String displayZero; //�Ƿ���ʾ


    public Format()
    {

    }


    public String toXMLString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("\t<format>\n");

        sb.append("\t\t<file>")
                .append(getFile())
                .append("</file>\n");

        sb.append("\t</format>\n");
        return sb.toString();

    }

    public String getContext()
    {
        return context;
    }

    public void setContext(String context)
    {
        this.context = context;
    }

    public String getFile()
    {
        return file;
    }

    public void setFile(String file)
    {
        this.file = file;
    }
}

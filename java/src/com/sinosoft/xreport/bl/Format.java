//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\Location.java

package com.sinosoft.xreport.bl;

/**
 * 数据块格式类.
 * 作为替代F1的方案,Format类描述数据块的位置,大小,Border等信息.当前只实现简单
 * 描述,可以满足一般报表的格式化需求,如果实现更多的格式,参照F1的相关Format信息.
 * 使用Decorator模式,改进数据块的展现
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
    private String displayZero; //是否显示


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

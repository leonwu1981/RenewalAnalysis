//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\RowHeader.java

package com.sinosoft.xreport.bl;

import java.util.Map;

public class RowHeader implements BlockElement
{
    /**行头定义(单个)*/
    private Map map;

    protected RowHeader()
    {

    }

    public RowHeader(Map content)
    {
        setCompleteDefine(content);
    }

    public Map getCompleteDefine()
    {
        return map;
    }

    public String toXMLString()
    {
        StringBuffer bufRows = new StringBuffer();
        bufRows.append("<row ");
        bufRows.append("name=\"");
        bufRows.append(map.get("name"));
        bufRows.append("\" condition=\"");
        bufRows.append(map.get("condition"));
        bufRows.append("\" formula=\"");
        bufRows.append(map.get("formula"));
        bufRows.append("\" location=\"");
        bufRows.append(map.get("location"));
        bufRows.append("\">");
        bufRows.append(map.get("context"));
        bufRows.append("</row>\n");
        return bufRows.toString();
    }

    public void setCompleteDefine(Map define)
    {
        map = define;
    }
}
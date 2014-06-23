//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\DefineCell.java

package com.sinosoft.xreport.bl;

import java.util.Map;


public class DefineCell implements BlockElement
{
    /**单元格定义(单个)*/
    private Map map;

    protected DefineCell()
    {

    }

    public DefineCell(Map content)
    {
        setCompleteDefine(content);
    }

    public Map getCompleteDefine()
    {
        return map;
    }

    public void setCompleteDefine(Map define)
    {
        map = define;
    }

    public String toXMLString()
    {
        StringBuffer bufCells = new StringBuffer();
        bufCells.append("<cell ");
        bufCells.append("name=\"");
        bufCells.append(map.get("name"));
        bufCells.append("\" condition=\"");
        bufCells.append(map.get("condition"));
        bufCells.append("\" formula=\"");
        bufCells.append(map.get("formula"));
        bufCells.append("\" location=\"");
        bufCells.append(map.get("location"));
        bufCells.append("\">");
        bufCells.append(map.get("context"));
        bufCells.append("</cell>\n");
        return bufCells.toString();
    }
}
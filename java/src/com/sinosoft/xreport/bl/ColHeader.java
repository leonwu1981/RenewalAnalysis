//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\ColHeader.java

package com.sinosoft.xreport.bl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ColHeader implements BlockElement
{
    /**列头定义(单个)*/
    private Map map;

    protected ColHeader()
    {

    }

    public ColHeader(Map content)
    {
        setCompleteDefine(content);
    }

    public Map getCompleteDefine()
    {
        return map;
    }

    public String toXMLString()
    {
        StringBuffer bufCols = new StringBuffer();
        bufCols.append("<col ");
        bufCols.append("name=\"");
        bufCols.append(map.get("name"));
        bufCols.append("\" condition=\"");
        bufCols.append(map.get("condition"));
        bufCols.append("\" formula=\"");
        bufCols.append(map.get("formula"));
        bufCols.append("\" location=\"");
        bufCols.append(map.get("location"));
        bufCols.append("\">");
        bufCols.append(map.get("context"));
        bufCols.append("</col>\n");
        return bufCols.toString();
    }

    public void setCompleteDefine(Map define)
    {
        map = define;
    }

    /**
     * 列头所包含的所有'数据源', 如: summary_balance.balance_dest
     * @return 数据源集合.
     */
    public Set getDS()
    {
        Set set = new HashSet();
        try
        {
            String formula = (String) getCompleteDefine().get(BlockElement.
                    FORMULA);

            XRRegExp.getAllDS(formula, set);

            return set;

        }
        catch (Exception ex)
        {
            return null;
        }

    }

    /**
     * 列头所包含的条件信息.
     * @return 列头的where信息.
     */
    public String[] getConditions()
    {
        return null;
    }

}
//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\ColHeader.java

package com.sinosoft.xreport.bl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ColHeader implements BlockElement
{
    /**��ͷ����(����)*/
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
     * ��ͷ������������'����Դ', ��: summary_balance.balance_dest
     * @return ����Դ����.
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
     * ��ͷ��������������Ϣ.
     * @return ��ͷ��where��Ϣ.
     */
    public String[] getConditions()
    {
        return null;
    }

}
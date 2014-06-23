package com.sinosoft.xreport.bl;

import java.util.Map;

import com.sinosoft.xreport.util.StringUtility;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author lixy
 * @version 1.0
 */

public class BlockGlobal implements BlockElement
{
    /**块的全局信息(单个)*/
    private Map map;

    public static final String USER = "user_sql_where";

    protected BlockGlobal()
    {
    }

    public BlockGlobal(Map map)
    {
        setCompleteDefine(map);
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
        StringBuffer bufGlobal = new StringBuffer();
        bufGlobal.append("<global>\n");
        Object[] key = map.keySet().toArray();
        for (int i = 0; i < key.length; i++)
        {
            if (!key[i].equals(USER))
            {
                bufGlobal.append("<condition type=\"");
                bufGlobal.append(key[i]);
                bufGlobal.append("\" value=\"");
                //处理操作符
                bufGlobal.append(StringUtility.convertXML(
                        (String) map.get(key[i])));
                bufGlobal.append("\"/>\n");
            }
            else
            {
                bufGlobal.append("<user_sql_where where=\"");
                bufGlobal.append(map.get(key[i]));
                bufGlobal.append("\"/>\n");
            }
        }
        bufGlobal.append("</global>\n");
        return bufGlobal.toString();
    }
}
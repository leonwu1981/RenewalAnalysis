package com.sinosoft.xreport.bl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import com.sinosoft.xreport.util.SysConfig;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author lixy
 * @version 1.0
 */

public class BlockParams implements Parameter
{
    Map map;

    public BlockParams()
    {
    }

    public BlockParams(Map map)
    {
        this.map = map;
    }

    public Map getDefine()
    {
        return map;
    }

    public void setDefine(Map map)
    {
        this.map = map;
    }

    public String toXMLString()
    {
        StringBuffer bufReturn = new StringBuffer();
        bufReturn.append("<parameters>\n");
        if (map.size() != 0)
        {
            Collection values = map.values();
            Iterator iterator = values.iterator();
            while (iterator.hasNext())
            {
                Map mapParam = (Map) iterator.next();
                String name = (String) mapParam.get(NAME);
                String tips = (String) mapParam.get(TIPS);
                String showMode = (String) mapParam.get(SHOWMODE);
                bufReturn.append("<parameter");
                bufReturn.append(" name=\"" + name + "\"");
                bufReturn.append(" tips=\"" + tips + "\"");
                bufReturn.append(" showMode=\"" + showMode + "\" />\n");
            }
        }
        bufReturn.append("</parameters>\n");
        return bufReturn.toString();
    }

    public static Vector getParams(String params)
    {
        StringTokenizer token = new StringTokenizer(params,
                SysConfig.SEPARATORONE);
        Vector vecParams = new Vector();
        String strOneParams = "";
        String strOneParam = "";
        /**拆分第一级分隔符"|"*/
        while (token.hasMoreTokens())
        {
            strOneParams = (String) token.nextElement();
            StringTokenizer tokenOne = new StringTokenizer(
                    strOneParams, SysConfig.SEPARATORTWO);
            Map map = new HashMap();
            /**拆分第二级分隔符"^"*/
            while (tokenOne.hasMoreTokens())
            {
                strOneParam = (String) tokenOne.nextElement();
                int index = strOneParam.indexOf(SysConfig.SEPARATORTREE);
                /**拆分第三级分隔符";"并形成HashMap*/
                map.put(strOneParam.substring(0, index),
                        strOneParam.substring(index + 1));
            }
            vecParams.addElement(map);
        }
        return vecParams;
    }

    public static void main(String[] args)
    {
        Map mapBlock = new HashMap();
        Map map = new HashMap();
        map.put("name", "name1");
        map.put("tips", "tips1");
        map.put("showMode", "showMode1");
        mapBlock.put("name1", map);
        map = new HashMap();
        map.put("name", "name2");
        map.put("tips", "tips2");
        map.put("showMode", "showMode2");
        mapBlock.put("name2", map);
        BlockParams block = new BlockParams(mapBlock);
        System.out.print(block.toXMLString());
    }
}
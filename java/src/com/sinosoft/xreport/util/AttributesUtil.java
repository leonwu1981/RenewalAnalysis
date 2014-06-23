package com.sinosoft.xreport.util;

import org.xml.sax.Attributes;

public class AttributesUtil
{
    /**
     * 返回指定名称的属性的值
     * @param attrs 属性对象
     * @param names 属性名称列表数组
     * @return 属性值数组
     */
    public static String[] getValues(Attributes attrs, String names[])
    {
        if (attrs == null || names == null)
        {
            return null;
        }
        String values[] = new String[names.length];
        for (int i = 0; i < values.length; i++)
        {
            values[i] = attrs.getValue(names[i]);
            if (values[i] == null && names[i].indexOf(":") == -1)
            {
                int n = attrs.getLength();
                for (int j = 0; j < n; j++)
                {
                    if (attrs.getLocalName(j).equals(names[i]))
                    {
                        values[i] = attrs.getValue(j);
                        break;
                    }
                }
            }
        }
        return values;
    }

    public static Object[] convertValues(String values[], Class types[]) throws
            NumberFormatException
    {
        if (values == null || types == null)
        {
            return null;
        }
        int n = values.length;
        Object wrappers[] = new Object[n];
        for (int i = 0; i < n; i++)
        {
            if (values[i] == null)
            {
                continue;
            }
            if (types[i] == String.class)
            {
                wrappers[i] = values[i];
            }
            else if (types[i] == byte.class)
            {
                wrappers[i] = new Byte(values[i]);
            }
            else if (types[i] == short.class)
            {
                wrappers[i] = new Short(values[i]);
            }
            else if (types[i] == int.class)
            {
                wrappers[i] = new Integer(values[i]);
            }
            else if (types[i] == long.class)
            {
                wrappers[i] = new Long(values[i]);
            }
            else if (types[i] == float.class)
            {
                wrappers[i] = new Float(values[i]);
            }
            else if (types[i] == double.class)
            {
                wrappers[i] = new Double(values[i]);
            }
            else if (types[i] == boolean.class)
            {
                wrappers[i] = new Boolean(values[i]);
            }
            else if (types[i] == char.class && values.length > 0)
            {
                wrappers[i] = new Character(values[i].charAt(0));
            }
        }
        return wrappers;
    }
}
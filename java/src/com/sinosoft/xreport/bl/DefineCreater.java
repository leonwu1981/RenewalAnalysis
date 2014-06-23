package com.sinosoft.xreport.bl;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import com.f1j.swing.JBook;
import com.sinosoft.xreport.util.SysConfig;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author lixy
 * @version 1.0
 */

/*
 * 说明：1、暂时只考虑一个数据块的情况
 *      2、行头和列头的数据格式为：name^condition^formula^location^context
 *      3、HashMap的格式为：[name：[name:name,condition:condition,formula:formula,
 *                                location:location,context:context]]
 * 问题：全局信息存在控件的什么部分?如果不存在控件上，则需要加入一个表示全局信息的参数。
 */

public class DefineCreater
{
    /**报表控件*/
    private JBook jbook;
    /**报表定义*/
    private DefineBlock[] block;
    /**HashMap的键*/
    private String[] key =
            {
            "name", "condition", "formula", "location", "context"};


    public DefineCreater(JBook jbook)
    {
        this.jbook = jbook;
    }

    public DefineBlock[] getDefineBlock()
    {
        if (block == null)
        {
            parseDefineBlock();
        }
        return block;
    }

    private void parseDefineBlock()
    {
        /**暂时只考虑一个数据块*/
        block = new DefineBlock[1];
        block[0] = new DefineBlock();
        try
        {
            jbook.setSheet(1);
            /**读取行头*/
            int intRow = getRowHeader();
            /**读取列头*/
            int intCol = getColHeader();
            /**读取行头定义*/
            Map mapRow = getRows(intRow, intCol);
            /**读取列头定义*/
            Map mapCol = getCols(intRow, intCol);
            /**读取单元格定义*/
            Map mapCell = getCells(intRow, intCol);
            /**取全局信息*/
            Map mapGlobal = getGlobal(intRow, intCol);
            jbook.setSheet(0);
            /**设置block*/
            block[0].setRows(mapRow);
            block[0].setCols(mapCol);
            block[0].setCells(mapCell);
            block[0].setGlobal(mapGlobal);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 获得报表定义的行头所在的行号
     * @return 报表定义的行头所在的行号
     */
    private int getRowHeader()
    {
        /**单元格定义字符串*/
        String strTextOne = "";
        String strTextTwo = "";
        try
        {
            /**扫描报表控件*/
            for (int i = 0; i <= jbook.getLastRow(); i++)
            {
                for (int j = 0; j < jbook.getLastCol(); j++)
                {
                    /**如果连续两个单元格不为空则认为该行为行头*/
                    strTextOne = jbook.getText(1, i, j);
                    strTextTwo = jbook.getText(1, i, j + 1);
                    if (!strTextOne.equals("") && !strTextTwo.equals(""))
                    {
                        return i;
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获得报表定义的列头所在的列号
     * @return 报表定义的列头所在的列号
     */
    private int getColHeader()
    {
        /**单元格定义字符串*/
        String strTextOne = "";
        String strTextTwo = "";
        try
        {
            /**扫描报表控件*/
            for (int j = 0; j <= jbook.getLastCol(); j++)
            {
                for (int i = 0; i < jbook.getLastRow(); i++)
                {
                    /**如果连续两个单元格不为空则认为该列为列头*/
                    strTextOne = jbook.getText(1, i, j);
                    strTextTwo = jbook.getText(1, i + 1, j);
                    if (!strTextOne.equals("") && !strTextTwo.equals(""))
                    {
                        return j;
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 读取报表行头定义
     * @param intRow 报表行头的行号
     * @param intCol 报表列头的行号
     * @return 报表行头定义
     */
    private Map getRows(int intRow, int intCol)
    {
        /**报表行定义内容*/
        String strText = "";
        /**返回值*/
        Map mapReturn = new HashMap();
        try
        {
            /**扫描报表的行头*/
            for (int j = intCol + 1; j <= jbook.getLastCol(); j++)
            {
                /**读取单元格定义*/
                Map map = getCell(intRow, j);
                /**形成二级HashMap*/
                if (!map.isEmpty())
                {
                    mapReturn.put((String) map.get(key[0]), map);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return mapReturn;
    }

    private Map getCell(int intRow, int intCol)
    {
        /**报表单元格内容*/
        String strText = "";
        /**返回值*/
        Map mapRow = new HashMap();
        try
        {
            /**读取单元格定义*/
            strText = jbook.getText(1, intRow, intCol);
            if (strText == null || strText.equals(""))
            {
                return new HashMap();
            }
            StringTokenizer token = new StringTokenizer(strText,
                    SysConfig.SEPARATORTWO);
            Vector vecInfo = new Vector();
            /**拆分单元格定义*/
            while (token.hasMoreTokens())
            {
                vecInfo.addElement(token.nextElement());
            }
            /**将行定义的信息保存到HashMap中*/
            for (int i = 0; i < key.length; i++)
            {
                mapRow.put(key[i], (String) vecInfo.elementAt(i));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return mapRow;
    }

    /**
     * 读取报表列头定义
     * @param intRow 报表行头的行号
     * @param intCol 报表列头的列号
     * @return 报表列头定义
     */
    private Map getCols(int intRow, int intCol)
    {
        /**报表列定义内容*/
        String strText = "";
        /**返回值*/
        Map mapReturn = new HashMap();
        try
        {
            /**扫描报表的列头*/
            for (int i = intRow + 1; i <= jbook.getLastRow(); i++)
            {
                /**读取单元格定义*/
                Map map = getCell(i, intCol);
                /**形成二级HashMap*/
                if (!map.isEmpty())
                {
                    mapReturn.put((String) map.get(key[0]), map);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return mapReturn;
    }

    /**
     * 读取报表单元格定义
     * @param intRow 报表行头的行号
     * @param intCol 报表列头的列号
     * @return 报表单元格定义
     */
    private Map getCells(int intRow, int intCol)
    {
        /**报表单元格定义内容*/
        String strText = "";
        /**返回值*/
        Map mapReturn = new HashMap();
        try
        {
            /**扫描报表的单元格*/
            for (int i = intRow + 1; i <= jbook.getLastRow(); i++)
            {
                for (int j = intCol + 1; j <= jbook.getLastCol(); j++)
                {
                    /**读取单元格定义*/
                    Map map = getCell(i, j);
                    /**形成二级HashMap*/
                    if (!map.isEmpty())
                    {
                        mapReturn.put((String) map.get(key[0]), map);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return mapReturn;
    }

    /**
     * 读取报表全局信息定义
     * @param intRow 报表行头的行号
     * @param intCol 报表列头的列号
     * @return 报表全局信息定义
     * 说明：全局信息的格式：type^value|type^value|……
     *      全局信息存放在行头和列头的交点处
     */
    public Map getGlobal(int intRow, int intCol)
    {
        /**报表单元格内容*/
        String strText = "";
        /**返回值*/
        Map mapGlobal = new HashMap();
        try
        {
            /**读取单元格定义，在[0,0]单元格上读取全局信息*/
            strText = jbook.getText(1, 0, 0);
            if (strText == null || strText.equals(""))
            {
                return new HashMap();
            }
            StringTokenizer token = new StringTokenizer(strText,
                    SysConfig.SEPARATORONE);
            Vector vecGlobal = new Vector();
            /**拆分全局信息定义*/
            while (token.hasMoreTokens())
            {
                vecGlobal.addElement(token.nextElement());
            }
            /**将行定义的信息保存到HashMap中*/
            for (int i = 0; i < vecGlobal.size(); i++)
            {
                String global = (String) vecGlobal.elementAt(i);
                token = new StringTokenizer(global, SysConfig.SEPARATORTWO);
                mapGlobal.put((String) token.nextElement(),
                              (String) token.nextElement());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return mapGlobal;
    }

    public BlockParams getParams()
    {
        /**报表单元格内容*/
        String strText = "";
        /**返回值*/
        Map mapParams = new HashMap();
        try
        {
            /**读取单元格定义，在[0,1]单元格上读取全局信息*/
            strText = jbook.getText(1, 0, 2);
            if (strText == null || strText.equals(""))
            {
                return new BlockParams(mapParams);
            }
            Vector vecParams = BlockParams.getParams(strText);
            for (int i = 0; i < vecParams.size(); i++)
            {
                Map map = (Map) vecParams.elementAt(i);
                String name = (String) map.get("name");
                mapParams.put(name, map);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new BlockParams(mapParams);
    }

    public static void main(String[] args)
    {
        JBook jbook = new JBook();
        try
        {
            jbook.insertSheets(1, 1);
            jbook.setText(1, 0, 1, "tips;请输入单位代码^showMode;select branch_name,branch_code from branch ^name;branch^|");
            jbook.setText(1, 1, 2,
                          "name12^condition12^formula12^location12^context12");
            jbook.setText(1, 1, 3,
                          "name13^condition13^formula13^location13^context13");
            jbook.setText(1, 2, 1,
                          "name21^condition21^formula21^location21^context21");
            jbook.setText(1, 3, 1,
                          "name31^condition31^formula31^location31^context31");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        DefineCreater creater = new DefineCreater(jbook);
        creater.getParams();
    }

}
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
 * ˵����1����ʱֻ����һ�����ݿ�����
 *      2����ͷ����ͷ�����ݸ�ʽΪ��name^condition^formula^location^context
 *      3��HashMap�ĸ�ʽΪ��[name��[name:name,condition:condition,formula:formula,
 *                                location:location,context:context]]
 * ���⣺ȫ����Ϣ���ڿؼ���ʲô����?��������ڿؼ��ϣ�����Ҫ����һ����ʾȫ����Ϣ�Ĳ�����
 */

public class DefineCreater
{
    /**����ؼ�*/
    private JBook jbook;
    /**������*/
    private DefineBlock[] block;
    /**HashMap�ļ�*/
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
        /**��ʱֻ����һ�����ݿ�*/
        block = new DefineBlock[1];
        block[0] = new DefineBlock();
        try
        {
            jbook.setSheet(1);
            /**��ȡ��ͷ*/
            int intRow = getRowHeader();
            /**��ȡ��ͷ*/
            int intCol = getColHeader();
            /**��ȡ��ͷ����*/
            Map mapRow = getRows(intRow, intCol);
            /**��ȡ��ͷ����*/
            Map mapCol = getCols(intRow, intCol);
            /**��ȡ��Ԫ����*/
            Map mapCell = getCells(intRow, intCol);
            /**ȡȫ����Ϣ*/
            Map mapGlobal = getGlobal(intRow, intCol);
            jbook.setSheet(0);
            /**����block*/
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
     * ��ñ��������ͷ���ڵ��к�
     * @return ���������ͷ���ڵ��к�
     */
    private int getRowHeader()
    {
        /**��Ԫ�����ַ���*/
        String strTextOne = "";
        String strTextTwo = "";
        try
        {
            /**ɨ�豨��ؼ�*/
            for (int i = 0; i <= jbook.getLastRow(); i++)
            {
                for (int j = 0; j < jbook.getLastCol(); j++)
                {
                    /**�������������Ԫ��Ϊ������Ϊ����Ϊ��ͷ*/
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
     * ��ñ��������ͷ���ڵ��к�
     * @return ���������ͷ���ڵ��к�
     */
    private int getColHeader()
    {
        /**��Ԫ�����ַ���*/
        String strTextOne = "";
        String strTextTwo = "";
        try
        {
            /**ɨ�豨��ؼ�*/
            for (int j = 0; j <= jbook.getLastCol(); j++)
            {
                for (int i = 0; i < jbook.getLastRow(); i++)
                {
                    /**�������������Ԫ��Ϊ������Ϊ����Ϊ��ͷ*/
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
     * ��ȡ������ͷ����
     * @param intRow ������ͷ���к�
     * @param intCol ������ͷ���к�
     * @return ������ͷ����
     */
    private Map getRows(int intRow, int intCol)
    {
        /**�����ж�������*/
        String strText = "";
        /**����ֵ*/
        Map mapReturn = new HashMap();
        try
        {
            /**ɨ�豨�����ͷ*/
            for (int j = intCol + 1; j <= jbook.getLastCol(); j++)
            {
                /**��ȡ��Ԫ����*/
                Map map = getCell(intRow, j);
                /**�γɶ���HashMap*/
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
        /**����Ԫ������*/
        String strText = "";
        /**����ֵ*/
        Map mapRow = new HashMap();
        try
        {
            /**��ȡ��Ԫ����*/
            strText = jbook.getText(1, intRow, intCol);
            if (strText == null || strText.equals(""))
            {
                return new HashMap();
            }
            StringTokenizer token = new StringTokenizer(strText,
                    SysConfig.SEPARATORTWO);
            Vector vecInfo = new Vector();
            /**��ֵ�Ԫ����*/
            while (token.hasMoreTokens())
            {
                vecInfo.addElement(token.nextElement());
            }
            /**���ж������Ϣ���浽HashMap��*/
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
     * ��ȡ������ͷ����
     * @param intRow ������ͷ���к�
     * @param intCol ������ͷ���к�
     * @return ������ͷ����
     */
    private Map getCols(int intRow, int intCol)
    {
        /**�����ж�������*/
        String strText = "";
        /**����ֵ*/
        Map mapReturn = new HashMap();
        try
        {
            /**ɨ�豨�����ͷ*/
            for (int i = intRow + 1; i <= jbook.getLastRow(); i++)
            {
                /**��ȡ��Ԫ����*/
                Map map = getCell(i, intCol);
                /**�γɶ���HashMap*/
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
     * ��ȡ����Ԫ����
     * @param intRow ������ͷ���к�
     * @param intCol ������ͷ���к�
     * @return ����Ԫ����
     */
    private Map getCells(int intRow, int intCol)
    {
        /**����Ԫ��������*/
        String strText = "";
        /**����ֵ*/
        Map mapReturn = new HashMap();
        try
        {
            /**ɨ�豨��ĵ�Ԫ��*/
            for (int i = intRow + 1; i <= jbook.getLastRow(); i++)
            {
                for (int j = intCol + 1; j <= jbook.getLastCol(); j++)
                {
                    /**��ȡ��Ԫ����*/
                    Map map = getCell(i, j);
                    /**�γɶ���HashMap*/
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
     * ��ȡ����ȫ����Ϣ����
     * @param intRow ������ͷ���к�
     * @param intCol ������ͷ���к�
     * @return ����ȫ����Ϣ����
     * ˵����ȫ����Ϣ�ĸ�ʽ��type^value|type^value|����
     *      ȫ����Ϣ�������ͷ����ͷ�Ľ��㴦
     */
    public Map getGlobal(int intRow, int intCol)
    {
        /**����Ԫ������*/
        String strText = "";
        /**����ֵ*/
        Map mapGlobal = new HashMap();
        try
        {
            /**��ȡ��Ԫ���壬��[0,0]��Ԫ���϶�ȡȫ����Ϣ*/
            strText = jbook.getText(1, 0, 0);
            if (strText == null || strText.equals(""))
            {
                return new HashMap();
            }
            StringTokenizer token = new StringTokenizer(strText,
                    SysConfig.SEPARATORONE);
            Vector vecGlobal = new Vector();
            /**���ȫ����Ϣ����*/
            while (token.hasMoreTokens())
            {
                vecGlobal.addElement(token.nextElement());
            }
            /**���ж������Ϣ���浽HashMap��*/
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
        /**����Ԫ������*/
        String strText = "";
        /**����ֵ*/
        Map mapParams = new HashMap();
        try
        {
            /**��ȡ��Ԫ���壬��[0,1]��Ԫ���϶�ȡȫ����Ϣ*/
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
            jbook.setText(1, 0, 1, "tips;�����뵥λ����^showMode;select branch_name,branch_code from branch ^name;branch^|");
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
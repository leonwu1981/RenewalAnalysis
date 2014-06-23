package com.sinosoft.xreport.dl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import com.sinosoft.xreport.util.CharacterCode;
import com.sinosoft.xreport.util.SysConfig;

/**
 * <p>Title: XReport</p>
 * <p>Description: 核算方向处理类</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author lixy
 * @version 1.0
 */

public class DirectionIdx
{
    /**核算方向的查询条件*/
    private String strCenterCode = "";
    private String strAccBookType = "";
    private String strAccBookCode = "";
    private String strItemCode = "";

    /**segment_value的数据*/
    private Map map = new HashMap();

    public DirectionIdx()
    {

    }

    public DirectionIdx(Map condition)
    {
        strCenterCode = (String) condition.get("center_code");
        strAccBookType = (String) condition.get("acc_book_type");
        strAccBookCode = (String) condition.get("acc_book_code");
        strItemCode = (String) condition.get("item_code");
    }

    public TreeModel getTree()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(strItemCode);
        Vector vecValue = getDirectionIdxValue();
        Vector vecNode = new Vector();
        String strIdx = getDirectionIdx();
        String strValue = "";
        for (int i = 0; i < vecValue.size(); i++)
        {
            strValue = (String) vecValue.elementAt(i);
            vecNode = getValue(strIdx, strValue);
            createTree(root, vecNode);
        }
        return new DefaultTreeModel(root);
    }

    /**
     * 获得strItemCode科目的核算方向代码
     * @return 核算方向代码
     */
    private String getDirectionIdx()
    {
        String strReturn = "";
        try
        {
            DBConnParam db = new DBConnParam();
            ConnectionFactory.bind("pool", db);
            DataSourceInf ds = ConnectionFactory.lookup("pool");
            System.out.println("bind  ok.");
            Connection conn = ds.getConnection();
            Statement st = conn.createStatement();
            String sql = "select direction_idx from " +
                         "summary_direction where center_code=" + strCenterCode +
                         " and acc_book_type=" + strAccBookType +
                         " and acc_book_code=" + strAccBookCode +
                         " and item_code=" + strItemCode;
            ResultSet rs = st.executeQuery(sql);
            if (rs.next())
            {
                strReturn = (" " + rs.getString("direction_idx")).trim();
            }
            //断开连接
            conn.close();
            ConnectionFactory.unbind("pool");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return strReturn;
    }

    /**
     * 获得strItemCode科目的核算方向值的代码
     * @return 核算方向值的代码
     */
    private Vector getDirectionIdxValue()
    {
        Vector vecReturn = new Vector();
        String strReturn = "";
        try
        {
            DBConnParam db = new DBConnParam();
            ConnectionFactory.bind("pool", db);
            DataSourceInf ds = ConnectionFactory.lookup("pool");
            System.out.println("bind  ok.");
            Connection conn = ds.getConnection();
            Statement st = conn.createStatement();
            String sql = "select direction_idx from " +
                         "validate_item where center_code=" + strCenterCode +
                         " and acc_book_type=" + strAccBookType +
                         " and acc_book_code=" + strAccBookCode +
                         " and item_code=" + strItemCode;
            ResultSet rs = st.executeQuery(sql);
            while (rs.next())
            {
                vecReturn.addElement((" " + rs.getString("direction_idx")).trim());
            }
            //断开连接
            conn.close();
            ConnectionFactory.unbind("pool");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return vecReturn;
    }

    /**
     * 根据核算方向代码和核算方向值代码生成Value对象
     * @param idx 核算方向代码
     * @param idxValue 核算方向值代码
     * @return 容纳Value对象的向量
     */
    private Vector getValue(String idx, String idxValue)
    {
        Vector vecIdx = getVector(idx);
        Vector vecValue = getVector(idxValue);
        Vector vecReturn = new Vector();
        String strItemId = "";
        String strValueCode = "";
        String strValueName = "";
        String strKey = "";
        if (map.size() == 0)
        {
            getSegmentValueMap();
        }
        for (int i = 0; i < vecIdx.size(); i++)
        {
            strItemId = (String) vecIdx.elementAt(i);
            strValueCode = (String) vecValue.elementAt(i);
            strKey = strItemId + SysConfig.KEYSEPARATOR + strValueCode;
            strValueName = (String) map.get(strKey);
            if (strValueName == null ||
                strValueName.equals("null") ||
                strValueName.equals(""))
            {
                continue;
            }
            ValueImpl value = new ValueImpl();
            value.setDimensionId(strItemId);
            value.setValueId(strValueCode);
            value.setValueName(strValueName);
            vecReturn.addElement(value);
        }
        return vecReturn;
    }

    /**
     * 读取segment_value表的数据，存放到map中
     */
    private void getSegmentValueMap()
    {
        String strItemId = "";
        String strItemValue = "";
        String strValueName = "";
        String strKey = "";
        try
        {
            DBConnParam db = new DBConnParam();
            ConnectionFactory.bind("pool", db);
            DataSourceInf ds = ConnectionFactory.lookup("pool");
            System.out.println("bind  ok.");
            Connection conn = ds.getConnection();
            Statement st = conn.createStatement();
            String sql = "select segment_col,value_code,value_name from " +
                         "segment_value where center_code=" + strCenterCode +
                         " and acc_book_type=" + strAccBookType;
            ResultSet rs = st.executeQuery(sql);
            while (rs.next())
            {
                strItemId = (" " + rs.getString("segment_col")).trim();
                strItemValue = (" " + rs.getString("value_code")).trim();
                strValueName = (" " + rs.getString("value_name")).trim();
                strValueName = CharacterCode.ISO8859_1ToGBK(strValueName);
                strKey = strItemId + SysConfig.KEYSEPARATOR + strItemValue;
                map.put(strKey, strValueName);
            }
            //断开连接
            conn.close();
            ConnectionFactory.unbind("pool");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 拆分direction_idx字段
     * @param idx direction_idx字段值
     * @return 向量
     */
    private Vector getVector(String idx)
    {
        /**以"/"为分隔符*/
        StringTokenizer token = new StringTokenizer(idx,
                SysConfig.SEPARATORITEM);
        Vector vecReturn = new Vector();
        while (token.hasMoreTokens())
        {
            vecReturn.addElement(token.nextElement());
        }
        return vecReturn;
    }

    /**
     * 构造核算方向数
     * @param root 根结点
     * @param vecNode 子结点
     */
    private void createTree(DefaultMutableTreeNode root, Vector vecNode)
    {
        DefaultMutableTreeNode temp = root;
        DefaultMutableTreeNode node = root;
        for (int i = 0; i < vecNode.size(); i++)
        {
            //temp是叶子
            if (temp.isLeaf())
            {
                //直接将当前节点作为temp的下一级节点
                node = new DefaultMutableTreeNode(vecNode.elementAt(i));
                temp.add(node);
                //调整temp
                temp = (DefaultMutableTreeNode) temp.getLastChild();
            }
            //temp不是叶子
            else
            {
                boolean flag = false;
                //查找temp的所有下一级子节点
                for (int k = 0; k < temp.getChildCount(); k++)
                {
                    //找到了与当前节点相同的节点
                    if (((DefaultMutableTreeNode) temp.getChildAt(k)).
                        getUserObject()
                        .equals(vecNode.elementAt(i)))
                    {
                        //将找到的节点作为temp
                        temp = (DefaultMutableTreeNode) temp.getChildAt(k);
                        flag = true;
                    }
                }
                //在temp的子节点中没有找到与当前节点相同的节点
                if (!flag)
                {
                    //将当前节点作为temp的下一级子节点
                    node = new DefaultMutableTreeNode(vecNode.elementAt(i));
                    temp.add(node);
                    //调整temp
                    temp = (DefaultMutableTreeNode) temp.getLastChild();
                }
            }
        }
    }

    public static void main(String[] args)
    {
//        Map map=new HashMap();
//        map.put("center_code","000000");
//        map.put("acc_book_type","01");
//        map.put("acc_book_code","11");
//        map.put("item_code","4101");
//        DirectionIdx idx=new DirectionIdx(map);
//        Vector vec=idx.getValue("f01/f02/f03/f04/f05/","1/1/1/1/1111001/");
//        for(int i=0;i<vec.size();i++)
//        {
//            System.out.println(vec.elementAt(i));
//        }
        System.out.print((String)null);
    }
}
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
 * <p>Description: ���㷽������</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author lixy
 * @version 1.0
 */

public class DirectionIdx
{
    /**���㷽��Ĳ�ѯ����*/
    private String strCenterCode = "";
    private String strAccBookType = "";
    private String strAccBookCode = "";
    private String strItemCode = "";

    /**segment_value������*/
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
     * ���strItemCode��Ŀ�ĺ��㷽�����
     * @return ���㷽�����
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
            //�Ͽ�����
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
     * ���strItemCode��Ŀ�ĺ��㷽��ֵ�Ĵ���
     * @return ���㷽��ֵ�Ĵ���
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
            //�Ͽ�����
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
     * ���ݺ��㷽�����ͺ��㷽��ֵ��������Value����
     * @param idx ���㷽�����
     * @param idxValue ���㷽��ֵ����
     * @return ����Value���������
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
     * ��ȡsegment_value������ݣ���ŵ�map��
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
            //�Ͽ�����
            conn.close();
            ConnectionFactory.unbind("pool");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * ���direction_idx�ֶ�
     * @param idx direction_idx�ֶ�ֵ
     * @return ����
     */
    private Vector getVector(String idx)
    {
        /**��"/"Ϊ�ָ���*/
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
     * ������㷽����
     * @param root �����
     * @param vecNode �ӽ��
     */
    private void createTree(DefaultMutableTreeNode root, Vector vecNode)
    {
        DefaultMutableTreeNode temp = root;
        DefaultMutableTreeNode node = root;
        for (int i = 0; i < vecNode.size(); i++)
        {
            //temp��Ҷ��
            if (temp.isLeaf())
            {
                //ֱ�ӽ���ǰ�ڵ���Ϊtemp����һ���ڵ�
                node = new DefaultMutableTreeNode(vecNode.elementAt(i));
                temp.add(node);
                //����temp
                temp = (DefaultMutableTreeNode) temp.getLastChild();
            }
            //temp����Ҷ��
            else
            {
                boolean flag = false;
                //����temp��������һ���ӽڵ�
                for (int k = 0; k < temp.getChildCount(); k++)
                {
                    //�ҵ����뵱ǰ�ڵ���ͬ�Ľڵ�
                    if (((DefaultMutableTreeNode) temp.getChildAt(k)).
                        getUserObject()
                        .equals(vecNode.elementAt(i)))
                    {
                        //���ҵ��Ľڵ���Ϊtemp
                        temp = (DefaultMutableTreeNode) temp.getChildAt(k);
                        flag = true;
                    }
                }
                //��temp���ӽڵ���û���ҵ��뵱ǰ�ڵ���ͬ�Ľڵ�
                if (!flag)
                {
                    //����ǰ�ڵ���Ϊtemp����һ���ӽڵ�
                    node = new DefaultMutableTreeNode(vecNode.elementAt(i));
                    temp.add(node);
                    //����temp
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
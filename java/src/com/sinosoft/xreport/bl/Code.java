package com.sinosoft.xreport.bl;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import com.sinosoft.xreport.dl.DataSource;
import com.sinosoft.xreport.dl.DataSourceImpl;
import com.sinosoft.xreport.dl.Dim;
import com.sinosoft.xreport.dl.DimImpl;
import com.sinosoft.xreport.dl.Source;
import com.sinosoft.xreport.dl.SourceImpl;
import com.sinosoft.xreport.dl.Value;
import com.sinosoft.xreport.dl.ValueImpl;
import com.sinosoft.xreport.util.SysConfig;

/**
 * <p>Title: Code</p>
 * <p>Description: 代码处理类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft</p>
 * @author lixy
 * @version 1.0
 * 功能简介：
 * 1、代码信息的读取
 * 2、代码id号和代码对象的对应
 * 3、代码的展现
 */

public class Code
{

    /**数据源信息*/
    private static HashMap mapDataSource = new HashMap();
    /**数据源与字段的对应关系*/
    private static HashMap mapTableFields = new HashMap();
    /**报表信息*/
    private static HashMap mapReportMain = new HashMap();
    /**单位信息*/
    private static HashMap mapBranch = new HashMap();
    /**值信息*/
    private static HashMap mapValue = new HashMap();

    public Code()
    {
    }

    public static void main(String[] args)
    {
        Code code = new Code();
        code.getReportMainMap();
        Vector vecReport = code.getReportMain();
        for (int i = 0; i < vecReport.size(); i++)
        {
            System.out.println(((ReportMain) vecReport.elementAt(i)).
                               getBranchId());
        }
    }

    ////////////////////////DataSource、Dimension、Value//////////////////////

    /**
     * 读取数据源信息
     */
    public void getTableFields()
    {
        /**获得数据源定义列表*/
        try
        {
            /***/
            DataSource data = new DataSourceImpl();
            Collection list = data.getDataSourceDefine();
            Vector vecDimension = new Vector();
            Object[] source = list.toArray();
            /**将数据源的信息写入HashMap*/
            for (int i = 0; i < source.length; i++)
            {
                String strDataSourceId = ((Source) source[i]).getDataSourceId();
                mapDataSource.put(strDataSourceId, (Source) source[i]);
                /**读取当前数据源包含的维*/
                list = data.getDimensionDefine(strDataSourceId);
                Object[] dim = list.toArray();
                vecDimension.clear();
                for (int j = 0; j < dim.length; j++)
                {
                    vecDimension.addElement((Dim) dim[j]);
                }
                /**将数据源和维的对应关系写入HashMap*/
                mapTableFields.put(strDataSourceId, vecDimension);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 返回数据源信息
     * @return 数据源信息
     */
    public Vector getDataSources()
    {
        /**初始化数据*/
        if (mapDataSource.size() == 0)
        {
            getTableFields();
        }
        Vector vecDataSource = new Vector();
        /**从HashMap中读取数据源对象*/
        Iterator iterator = mapDataSource.values().iterator();
        while (iterator.hasNext())
        {
            Source source = (Source) (iterator.next());
            vecDataSource.add(source);
        }
        return vecDataSource;
    }

    /**
     * 返回数据源表信息
     * @param strDataSourceId 数据表代码
     * @return 数据源表信息
     */
    public Source getDataSource(String strDataSourceId)
    {
        /**初始化数据*/
        if (mapDataSource.size() == 0)
        {
            getTableFields();
        }
        /**从HashMap中读取给定代码的数据源对象*/
        Source source = (Source) (mapDataSource.get(strDataSourceId));
        if (source == null)
        {
            source = new SourceImpl();
        }
        return source;
    }

    /**
     * 获得所有的维定义
     * @return Vector 向量
     * */
    public Vector getDimensions()
    {
        /**返回值*/
        Vector vecReturn = new Vector();
        try
        {
            DataSource data = new DataSourceImpl();
            Collection c = data.getDimensionDefine();
            Object[] obj = c.toArray();
            for (int i = 0; i < obj.length; i++)
            {
                vecReturn.addElement(obj[i]);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return vecReturn;
    }

    /**
     * 返回数据源字段信息
     * @param strDataSourceId 数据源表代码
     * @return 数据源字段信息
     */
    public Vector getDimensions(String strDataSourceId)
    {
        /**初始化数据*/
        if (mapTableFields.size() == 0)
        {
            getTableFields();
        }
        /**从HashMap中读取给定数据源的维*/
        Vector vecDimension = (Vector) (mapTableFields.get(strDataSourceId));
        if (vecDimension == null)
        {
            return new Vector();
        }
        return vecDimension;
    }

    /**
     * 返回数据源字段信息
     * @param strDataSourceId 数据源表代码
     * @param strDimenId 数据源字段代码
     * @return 数据源字段信息
     */
    public Dim getDimension(String strDataSourceId, String strDimenId)
    {
        /**读取给定数据源的所有维*/
        Vector vecDimension = getDimensions(strDataSourceId);
        Dim dimension = null;
        /**读取给定维代码的维对象*/
        for (int i = 0; i < vecDimension.size(); i++)
        {
            dimension = (Dim) vecDimension.elementAt(i);
            if (strDimenId.equals(dimension.getDimenId()))
            {
                break;
            }
        }
        return dimension;
    }

    /**
     * 返回当前维的直接上级
     * @param dimension 当前维
     * @return 上级维数组
     */
    public Dim getSuperDimension(Dim dimension)
    {
        Dim dim;
        /**数据源id*/
        String strDataSourceId = dimension.getDataSourceId();
        /**上级维id*/
        String strSuperDimsId = dimension.getSuperDimenId();
        /**如果没有上级维，则返回空*/
        if (strSuperDimsId == null || strSuperDimsId.equals(""))
        {
            return null;
        }
        /**拆分上级维id*/
        StringTokenizer token = new StringTokenizer
                                (strSuperDimsId, SysConfig.SEPARATORTWO);
        Vector vecSuperDimension = new Vector();
        while (token.hasMoreTokens())
        {
            vecSuperDimension.addElement(token.nextElement());
        }
        /**共有多少个上级维*/
        String strSuperDimId = "";
        int intIndex = 1;
        /**形成返回值，并寻找直接上级*/
        for (int i = 0; i < vecSuperDimension.size(); i++)
        {
            strSuperDimId = (String) vecSuperDimension.elementAt(i);
            //直接上级
            if (strSuperDimId.indexOf("[") != -1)
            {
                int intLeft = strSuperDimId.indexOf("[");
                int intRight = strSuperDimId.indexOf("]");
                strSuperDimId = strSuperDimId.substring(intLeft + 1, intRight);
                dim = getDimension(strDataSourceId, strSuperDimId);
                return dim;
            }
        }
        return null;
    }

    /**
     * 获得当前维的所有上级(有序)
     * @param dimension 当前维
     * @return 当前维的所有上级
     */
    public Vector getSuperDimensions(Dim dimension)
    {
        Vector vecReturn = new Vector();
        Dim dimNew = getSuperDimension(dimension);
        while (dimNew != null)
        {
            vecReturn.addElement(dimNew);
            Dim dimOld = dimNew;
            dimNew = new DimImpl();
            dimNew = getSuperDimension(dimOld);
        }
        return vecReturn;
    }

    /**
     * 从全局变量表中获得当前维的取数条件
     * @param dimension 当前维
     * @param global 全局变量表
     * @return 当前维的取数条件
     */
    public Map getCondition(Dim dimension, Map global)
    {
        Map map = new HashMap();
        Vector vecSuperDim = getSuperDimensions(dimension);
        for (int i = 0; i < vecSuperDim.size(); i++)
        {
            Dim dim = (Dim) vecSuperDim.elementAt(i);
            String value = (String) global.get(dim.getDimenId());
            if (value == null || value.equals(""))
            {
                continue;
            }
            map.put(dim.getDimenId(), value);
        }
        return map;
    }

    /**
     * 形成mapValue
     */
    public void getValue()
    {
        System.out.println("getValue" + Calendar.getInstance().getTime() +
                           ">>>>>>>>>>>>>>>>>>>>>");
        /**读取所有的维定义*/
        Vector vecDimension = getDimensions();
        String strDimensionId = "", strValueId = "", strKey = "";
        for (int i = 0; i < vecDimension.size(); i++)
        {
            Dim dimension = (Dim) vecDimension.elementAt(i);
            /**读取维的所有值*/
            Vector vecValue = getValue(dimension, new HashMap());
            for (int j = 0; j < vecValue.size(); j++)
            {
                /**形成HashMap*/
                Value value = (Value) vecValue.elementAt(j);
                strDimensionId = value.getDimensionId();
                strValueId = value.getValueId();
                strKey = strDimensionId + SysConfig.KEYSEPARATOR + strValueId;
                mapValue.put(strKey, value);
            }
        }
        System.out.println("getValue" + Calendar.getInstance().getTime() +
                           ">>>>>>>>>>>>>>>>>>>>>");
    }

    /**
     * 读取给定维的取值
     * @param dimension 维
     * @param parent 取值条件
     * @return 取值的Collection或TreeNode
     */
    public Vector getValue(Dim dimension, Map parent)
    {
        System.out.println("getValue2" + Calendar.getInstance().getTime());
        Vector vecReturn = new Vector();
        try
        {
            DataSource data = new DataSourceImpl();
            /**拼写表名.字段名*/
            String strDataSourceId = dimension.getDataSourceId();
            String strDimensionId = dimension.getDimenId();
            String strTableColumn = strDataSourceId + SysConfig.KEYSEPARATOR +
                                    strDimensionId;
            /**不考虑核算方向的情况*/
            if (strDimensionId.equals("direction_idx"))
            {
                return vecReturn;
            }
            /**读取dimension的所有值*/
            Collection c = (Collection) data.getValue(strTableColumn, parent);
            if (c == null)
            {
                return vecReturn;
            }
            Object[] obj = c.toArray();
            for (int i = 0; i < obj.length; i++)
            {
                Value value = (Value) obj[i];
                /**形成返回值*/
                vecReturn.addElement(value);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("getValue2" + Calendar.getInstance().getTime());
        return vecReturn;
    }

    /**
     * 读取值定义
     * @param strDimensionId 维代码
     * @param strValueId 值代码
     * @return 值定义
     */
    public Value getValue(String strDimensionId, String strValueId)
    {
        if (mapValue.size() == 0)
        {
            getValue();
        }
        String strKey = strDimensionId + SysConfig.KEYSEPARATOR + strValueId;
        Value value = (Value) mapValue.get(strKey);
        if (value == null)
        {
            return new ValueImpl();
        }
        return value;
    }

    /**
     * 以树的形式展现所有数据源
     * @return 数据源树
     */
    public TreeModel getDataSourceTree()
    {
        /**初始化数据*/
        if (mapDataSource.size() == 0 ||
            mapTableFields.size() == 0)
        {
            getTableFields();
        }
        /**建立根元素*/
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        root.setUserObject("数据源");
        /**读取所有的数据源*/
        Vector vecSource = getDataSources();
        for (int i = 0; i < vecSource.size(); i++)
        {
            /**添加一个数据源结点*/
            Source source = (Source) vecSource.elementAt(i);
            DefaultMutableTreeNode nodeSource = new DefaultMutableTreeNode(
                    source);
            /**读取给定数据源包含的维*/
            Vector vecDimension = getDimensions(source.getDataSourceId());
            for (int j = 0; j < vecDimension.size(); j++)
            {
                /**给当前数据源添加一个维结点*/
                Dim dimension = (Dim) vecDimension.elementAt(j);
                DefaultMutableTreeNode nodeDim = new DefaultMutableTreeNode(
                        dimension);
                nodeSource.add(nodeDim);
            }
        }
        /**够造TreeModel并返回*/
        return new DefaultTreeModel(root);
    }

    /**
     * 以树的形式展现给定的数据源
     * @param strDataSourceId 给定数据源的id
     * @return 给定数据源的树
     */
    public TreeModel getDataSourceTree(String strDataSourceId)
    {
        /**初始化数据*/
        if (mapDataSource.size() == 0 ||
            mapTableFields.size() == 0)
        {
            getTableFields();
        }
        /**读取给定的数据源的对象*/
        Source source = getDataSource(strDataSourceId);
        if (source == null)
        {
            return new DefaultTreeModel(new DefaultMutableTreeNode());
        }
        /**建立根元素*/
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(source);
        /**读取给定数据源的维*/
        Vector vecDimension = getDimensions(strDataSourceId);
        for (int i = 0; i < vecDimension.size(); i++)
        {
            /**添加一个维结点*/
            Dim dimension = (Dim) vecDimension.elementAt(i);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(dimension);
            root.add(node);
        }
        return new DefaultTreeModel(root);
    }

    /**
     * 以列表的形式展现所有数据源
     * @return 数据源列表
     */
    public ListModel getDataSourceList()
    {
        /**初始化数据*/
        if (mapDataSource.size() == 0 ||
            mapTableFields.size() == 0)
        {
            getTableFields();
        }
        /**读取所有的数据源*/
        Vector vecDataSource = getDataSources();
        DefaultListModel model = new DefaultListModel();
        /**向列表中添加数据源元素*/
        for (int i = 0; i < vecDataSource.size(); i++)
        {
            Source source = (Source) vecDataSource.elementAt(i);
            model.addElement(source);
        }
        return model;
    }

    /**
     * 以列表的形式展现给定的数据源
     * @param strDataSourceId 给定的数据源id
     * @return 给定数据源的维列表
     */
    public ListModel getDimensionList(String strDataSourceId)
    {
        /**初始化数据*/
        if (mapDataSource.size() == 0 ||
            mapTableFields.size() == 0)
        {
            getTableFields();
        }
        /**读取给定数据源的对象*/
        Source source = getDataSource(strDataSourceId);
        if (source == null)
        {
            return new DefaultListModel();
        }
        DefaultListModel model = new DefaultListModel();
        /**读取给定数据源的维*/
        Vector vecDimension = getDimensions(strDataSourceId);
        for (int i = 0; i < vecDimension.size(); i++)
        {
            /**向列表中添加维元素*/
            Dim dimension = (Dim) vecDimension.elementAt(i);
            model.addElement(dimension);
        }
        return model;
    }

    /**
     * 读取给定维的取值
     * @param dimension 给定维的对象
     * @return 给定为的取值
     */
//    public Vector getDimensionValue(Dim dimension)
//    {
//        /**读取给定维的基本信息*/
//        String strCodeTable=dimension.getCodeTable();
//        String strIdField=dimension.getIdField();
//        String strNameField=dimension.getNameField();
//        String strSuperDimenId=dimension.getSuperDimenId();
//        String strParameter=dimension.getParameter();
//        /**读取给定维的所有值*/
//        Vector vecValue=getValue(strCodeTable,strIdField,strNameField,
//                                 strSuperDimenId,strParameter);
//        /**给所有的值对象设置维代码信息*/
//        for(int i=0;i<vecValue.size();i++)
//        {
//            Value value=(Value)vecValue.elementAt(i);
//            value.setDimenId(dimension.getDimenId());
//        }
//        return vecValue;
//    }

    /**
     * 读取给定代码表的值
     * @param strCodeTable 给定代码表的id
     * @param strIdField 代码表的id列
     * @param strNameField 代码表的name列
     * @param strSuperDimenId 参数类型
     * @param strParameter 参数值
     * @return 给定代码表的值
     */
//    public Vector getValue(String strCodeTable,String strIdField,String strNameField,
//                           String strSuperDimenId,String strParameter)
//    {
//        return new Vector();
//    }

    /**
     * 以树的方式展现维的取值
     * @param vecDimension 需要展现的维
     * @return 维的所有取值构成的树
     */
//    public TreeModel getDimensionTree(Vector vecDimension)
//    {
//        /**构造根结点*/
//        Dimen root=new Dimen();
//        root.setDimenName("ROOT");
//        /***/
//        for(int i=0;i<vecDimension.size();i++)
//        {
//            Dimen dimension=(Dimen)vecDimension.elementAt(i);
//            Vector vecValue=getDimensionValue(dimension);
//            for(int j=0;j<vecValue.size();j++)
//            {
//                Value value=(Value)vecValue.elementAt(j);
//                root.addChildren(value);
//            }
//        }
//        return null;
//    }

    ////////////////////////////End DataSource、Dimension、Value///////////////

    ///////////////////////////ReportMain///////////////////////////////////

    /**
     * 读取报表信息->HashMap
     */
    public void getReportMainMap()
    {
        ReportMain report = new ReportMain();
        /**查询所有的报表信息*/
        Vector vecReportMain = report.query();
        for (int i = 0; i < vecReportMain.size(); i++)
        {
            /**向HashMap中添加记录*/
            report = (ReportMain) vecReportMain.elementAt(i);
            mapReportMain.put(report.getBranchId() + SysConfig.KEYSEPARATOR +
                              report.getReportId() + SysConfig.KEYSEPARATOR +
                              report.getReportEdition(), report);
        }
    }

    /**
     * 返回报表信息
     * @return ReportMain 报表信息
     */
    public Vector getReportMain()
    {
        Vector vecReport = new Vector();
        if (mapReportMain.size() == 0)
        {
            getReportMainMap();
        }
        Iterator iterator = mapReportMain.values().iterator();
        while (iterator.hasNext())
        {
            ReportMain report = (ReportMain) (iterator.next());
            vecReport.addElement(report);
        }
        return vecReport;
    }

    /**
     * 返回报表信息
     * @param strBranchId 单位代码
     * @param strReportId 报表代码
     * @param strReportEdition 报表版别
     * @return 报表信息
     */
    public ReportMain getReportMain(String strBranchId, String strReportId,
                                    String strReportEdition)
    {
        ReportMain report = new ReportMain();
        /**拼写查询条件*/
        String strKey = strBranchId + SysConfig.KEYSEPARATOR +
                        strReportId + SysConfig.KEYSEPARATOR +
                        strReportEdition;
        /**初始化HashMap*/
        if (mapReportMain.size() == 0)
        {
            getReportMainMap();
        }
        /**从HashMap中读取报表信息*/
        report = (ReportMain) (mapReportMain.get(strKey));
        if (report == null)
        {
            return new ReportMain();
        }
        return report;
    }

    ////////////////////////////End ReportMain/////////////////////////////

    ///////////////////////////Branch/////////////////////////////////////

    /**
     * 读取单位信息->HashMap
     */
    public void getBranchMap()
    {
        Branch branch = new Branch();
        /**读取所有的报表信息*/
        Vector vecBranch = branch.query();
        for (int i = 0; i < vecBranch.size(); i++)
        {
            /**向HashMap中添加记录*/
            branch = (Branch) vecBranch.elementAt(i);
            mapBranch.put(branch.getBranchId(), branch);
        }
    }

    /**
     * 返回单位信息
     * @return Branch 单位信息
     */
    public Vector getBranch()
    {
        Vector vecBranch = new Vector();
        if (mapBranch.size() == 0)
        {
            getBranchMap();
        }
        Iterator iterator = mapBranch.values().iterator();
        while (iterator.hasNext())
        {
            Branch branch = (Branch) (iterator.next());
            vecBranch.addElement(branch);
        }
        return vecBranch;
    }

    /**
     * 返回单位信息
     * @param strBranchId 查询条件
     * @return 单位信息
     */
    public Branch getBranch(String strBranchId)
    {
        Branch branch = new Branch();
        /**初始化数据*/
        if (mapBranch.size() == 0)
        {
            getBranchMap();
        }
        /**从HashMap中读取单位信息*/
        branch = (Branch) (mapBranch.get(strBranchId));
        if (branch == null)
        {
            return new Branch();
        }
        return branch;
    }
    ///////////////////////////End Branch/////////////////////////////////
}
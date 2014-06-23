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
 * <p>Description: ���봦����</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft</p>
 * @author lixy
 * @version 1.0
 * ���ܼ�飺
 * 1��������Ϣ�Ķ�ȡ
 * 2������id�źʹ������Ķ�Ӧ
 * 3�������չ��
 */

public class Code
{

    /**����Դ��Ϣ*/
    private static HashMap mapDataSource = new HashMap();
    /**����Դ���ֶεĶ�Ӧ��ϵ*/
    private static HashMap mapTableFields = new HashMap();
    /**������Ϣ*/
    private static HashMap mapReportMain = new HashMap();
    /**��λ��Ϣ*/
    private static HashMap mapBranch = new HashMap();
    /**ֵ��Ϣ*/
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

    ////////////////////////DataSource��Dimension��Value//////////////////////

    /**
     * ��ȡ����Դ��Ϣ
     */
    public void getTableFields()
    {
        /**�������Դ�����б�*/
        try
        {
            /***/
            DataSource data = new DataSourceImpl();
            Collection list = data.getDataSourceDefine();
            Vector vecDimension = new Vector();
            Object[] source = list.toArray();
            /**������Դ����Ϣд��HashMap*/
            for (int i = 0; i < source.length; i++)
            {
                String strDataSourceId = ((Source) source[i]).getDataSourceId();
                mapDataSource.put(strDataSourceId, (Source) source[i]);
                /**��ȡ��ǰ����Դ������ά*/
                list = data.getDimensionDefine(strDataSourceId);
                Object[] dim = list.toArray();
                vecDimension.clear();
                for (int j = 0; j < dim.length; j++)
                {
                    vecDimension.addElement((Dim) dim[j]);
                }
                /**������Դ��ά�Ķ�Ӧ��ϵд��HashMap*/
                mapTableFields.put(strDataSourceId, vecDimension);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * ��������Դ��Ϣ
     * @return ����Դ��Ϣ
     */
    public Vector getDataSources()
    {
        /**��ʼ������*/
        if (mapDataSource.size() == 0)
        {
            getTableFields();
        }
        Vector vecDataSource = new Vector();
        /**��HashMap�ж�ȡ����Դ����*/
        Iterator iterator = mapDataSource.values().iterator();
        while (iterator.hasNext())
        {
            Source source = (Source) (iterator.next());
            vecDataSource.add(source);
        }
        return vecDataSource;
    }

    /**
     * ��������Դ����Ϣ
     * @param strDataSourceId ���ݱ����
     * @return ����Դ����Ϣ
     */
    public Source getDataSource(String strDataSourceId)
    {
        /**��ʼ������*/
        if (mapDataSource.size() == 0)
        {
            getTableFields();
        }
        /**��HashMap�ж�ȡ�������������Դ����*/
        Source source = (Source) (mapDataSource.get(strDataSourceId));
        if (source == null)
        {
            source = new SourceImpl();
        }
        return source;
    }

    /**
     * ������е�ά����
     * @return Vector ����
     * */
    public Vector getDimensions()
    {
        /**����ֵ*/
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
     * ��������Դ�ֶ���Ϣ
     * @param strDataSourceId ����Դ�����
     * @return ����Դ�ֶ���Ϣ
     */
    public Vector getDimensions(String strDataSourceId)
    {
        /**��ʼ������*/
        if (mapTableFields.size() == 0)
        {
            getTableFields();
        }
        /**��HashMap�ж�ȡ��������Դ��ά*/
        Vector vecDimension = (Vector) (mapTableFields.get(strDataSourceId));
        if (vecDimension == null)
        {
            return new Vector();
        }
        return vecDimension;
    }

    /**
     * ��������Դ�ֶ���Ϣ
     * @param strDataSourceId ����Դ�����
     * @param strDimenId ����Դ�ֶδ���
     * @return ����Դ�ֶ���Ϣ
     */
    public Dim getDimension(String strDataSourceId, String strDimenId)
    {
        /**��ȡ��������Դ������ά*/
        Vector vecDimension = getDimensions(strDataSourceId);
        Dim dimension = null;
        /**��ȡ����ά�����ά����*/
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
     * ���ص�ǰά��ֱ���ϼ�
     * @param dimension ��ǰά
     * @return �ϼ�ά����
     */
    public Dim getSuperDimension(Dim dimension)
    {
        Dim dim;
        /**����Դid*/
        String strDataSourceId = dimension.getDataSourceId();
        /**�ϼ�άid*/
        String strSuperDimsId = dimension.getSuperDimenId();
        /**���û���ϼ�ά���򷵻ؿ�*/
        if (strSuperDimsId == null || strSuperDimsId.equals(""))
        {
            return null;
        }
        /**����ϼ�άid*/
        StringTokenizer token = new StringTokenizer
                                (strSuperDimsId, SysConfig.SEPARATORTWO);
        Vector vecSuperDimension = new Vector();
        while (token.hasMoreTokens())
        {
            vecSuperDimension.addElement(token.nextElement());
        }
        /**���ж��ٸ��ϼ�ά*/
        String strSuperDimId = "";
        int intIndex = 1;
        /**�γɷ���ֵ����Ѱ��ֱ���ϼ�*/
        for (int i = 0; i < vecSuperDimension.size(); i++)
        {
            strSuperDimId = (String) vecSuperDimension.elementAt(i);
            //ֱ���ϼ�
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
     * ��õ�ǰά�������ϼ�(����)
     * @param dimension ��ǰά
     * @return ��ǰά�������ϼ�
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
     * ��ȫ�ֱ������л�õ�ǰά��ȡ������
     * @param dimension ��ǰά
     * @param global ȫ�ֱ�����
     * @return ��ǰά��ȡ������
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
     * �γ�mapValue
     */
    public void getValue()
    {
        System.out.println("getValue" + Calendar.getInstance().getTime() +
                           ">>>>>>>>>>>>>>>>>>>>>");
        /**��ȡ���е�ά����*/
        Vector vecDimension = getDimensions();
        String strDimensionId = "", strValueId = "", strKey = "";
        for (int i = 0; i < vecDimension.size(); i++)
        {
            Dim dimension = (Dim) vecDimension.elementAt(i);
            /**��ȡά������ֵ*/
            Vector vecValue = getValue(dimension, new HashMap());
            for (int j = 0; j < vecValue.size(); j++)
            {
                /**�γ�HashMap*/
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
     * ��ȡ����ά��ȡֵ
     * @param dimension ά
     * @param parent ȡֵ����
     * @return ȡֵ��Collection��TreeNode
     */
    public Vector getValue(Dim dimension, Map parent)
    {
        System.out.println("getValue2" + Calendar.getInstance().getTime());
        Vector vecReturn = new Vector();
        try
        {
            DataSource data = new DataSourceImpl();
            /**ƴд����.�ֶ���*/
            String strDataSourceId = dimension.getDataSourceId();
            String strDimensionId = dimension.getDimenId();
            String strTableColumn = strDataSourceId + SysConfig.KEYSEPARATOR +
                                    strDimensionId;
            /**�����Ǻ��㷽������*/
            if (strDimensionId.equals("direction_idx"))
            {
                return vecReturn;
            }
            /**��ȡdimension������ֵ*/
            Collection c = (Collection) data.getValue(strTableColumn, parent);
            if (c == null)
            {
                return vecReturn;
            }
            Object[] obj = c.toArray();
            for (int i = 0; i < obj.length; i++)
            {
                Value value = (Value) obj[i];
                /**�γɷ���ֵ*/
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
     * ��ȡֵ����
     * @param strDimensionId ά����
     * @param strValueId ֵ����
     * @return ֵ����
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
     * ��������ʽչ����������Դ
     * @return ����Դ��
     */
    public TreeModel getDataSourceTree()
    {
        /**��ʼ������*/
        if (mapDataSource.size() == 0 ||
            mapTableFields.size() == 0)
        {
            getTableFields();
        }
        /**������Ԫ��*/
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        root.setUserObject("����Դ");
        /**��ȡ���е�����Դ*/
        Vector vecSource = getDataSources();
        for (int i = 0; i < vecSource.size(); i++)
        {
            /**���һ������Դ���*/
            Source source = (Source) vecSource.elementAt(i);
            DefaultMutableTreeNode nodeSource = new DefaultMutableTreeNode(
                    source);
            /**��ȡ��������Դ������ά*/
            Vector vecDimension = getDimensions(source.getDataSourceId());
            for (int j = 0; j < vecDimension.size(); j++)
            {
                /**����ǰ����Դ���һ��ά���*/
                Dim dimension = (Dim) vecDimension.elementAt(j);
                DefaultMutableTreeNode nodeDim = new DefaultMutableTreeNode(
                        dimension);
                nodeSource.add(nodeDim);
            }
        }
        /**����TreeModel������*/
        return new DefaultTreeModel(root);
    }

    /**
     * ��������ʽչ�ָ���������Դ
     * @param strDataSourceId ��������Դ��id
     * @return ��������Դ����
     */
    public TreeModel getDataSourceTree(String strDataSourceId)
    {
        /**��ʼ������*/
        if (mapDataSource.size() == 0 ||
            mapTableFields.size() == 0)
        {
            getTableFields();
        }
        /**��ȡ����������Դ�Ķ���*/
        Source source = getDataSource(strDataSourceId);
        if (source == null)
        {
            return new DefaultTreeModel(new DefaultMutableTreeNode());
        }
        /**������Ԫ��*/
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(source);
        /**��ȡ��������Դ��ά*/
        Vector vecDimension = getDimensions(strDataSourceId);
        for (int i = 0; i < vecDimension.size(); i++)
        {
            /**���һ��ά���*/
            Dim dimension = (Dim) vecDimension.elementAt(i);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(dimension);
            root.add(node);
        }
        return new DefaultTreeModel(root);
    }

    /**
     * ���б����ʽչ����������Դ
     * @return ����Դ�б�
     */
    public ListModel getDataSourceList()
    {
        /**��ʼ������*/
        if (mapDataSource.size() == 0 ||
            mapTableFields.size() == 0)
        {
            getTableFields();
        }
        /**��ȡ���е�����Դ*/
        Vector vecDataSource = getDataSources();
        DefaultListModel model = new DefaultListModel();
        /**���б����������ԴԪ��*/
        for (int i = 0; i < vecDataSource.size(); i++)
        {
            Source source = (Source) vecDataSource.elementAt(i);
            model.addElement(source);
        }
        return model;
    }

    /**
     * ���б����ʽչ�ָ���������Դ
     * @param strDataSourceId ����������Դid
     * @return ��������Դ��ά�б�
     */
    public ListModel getDimensionList(String strDataSourceId)
    {
        /**��ʼ������*/
        if (mapDataSource.size() == 0 ||
            mapTableFields.size() == 0)
        {
            getTableFields();
        }
        /**��ȡ��������Դ�Ķ���*/
        Source source = getDataSource(strDataSourceId);
        if (source == null)
        {
            return new DefaultListModel();
        }
        DefaultListModel model = new DefaultListModel();
        /**��ȡ��������Դ��ά*/
        Vector vecDimension = getDimensions(strDataSourceId);
        for (int i = 0; i < vecDimension.size(); i++)
        {
            /**���б������άԪ��*/
            Dim dimension = (Dim) vecDimension.elementAt(i);
            model.addElement(dimension);
        }
        return model;
    }

    /**
     * ��ȡ����ά��ȡֵ
     * @param dimension ����ά�Ķ���
     * @return ����Ϊ��ȡֵ
     */
//    public Vector getDimensionValue(Dim dimension)
//    {
//        /**��ȡ����ά�Ļ�����Ϣ*/
//        String strCodeTable=dimension.getCodeTable();
//        String strIdField=dimension.getIdField();
//        String strNameField=dimension.getNameField();
//        String strSuperDimenId=dimension.getSuperDimenId();
//        String strParameter=dimension.getParameter();
//        /**��ȡ����ά������ֵ*/
//        Vector vecValue=getValue(strCodeTable,strIdField,strNameField,
//                                 strSuperDimenId,strParameter);
//        /**�����е�ֵ��������ά������Ϣ*/
//        for(int i=0;i<vecValue.size();i++)
//        {
//            Value value=(Value)vecValue.elementAt(i);
//            value.setDimenId(dimension.getDimenId());
//        }
//        return vecValue;
//    }

    /**
     * ��ȡ����������ֵ
     * @param strCodeTable ����������id
     * @param strIdField ������id��
     * @param strNameField ������name��
     * @param strSuperDimenId ��������
     * @param strParameter ����ֵ
     * @return ����������ֵ
     */
//    public Vector getValue(String strCodeTable,String strIdField,String strNameField,
//                           String strSuperDimenId,String strParameter)
//    {
//        return new Vector();
//    }

    /**
     * �����ķ�ʽչ��ά��ȡֵ
     * @param vecDimension ��Ҫչ�ֵ�ά
     * @return ά������ȡֵ���ɵ���
     */
//    public TreeModel getDimensionTree(Vector vecDimension)
//    {
//        /**��������*/
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

    ////////////////////////////End DataSource��Dimension��Value///////////////

    ///////////////////////////ReportMain///////////////////////////////////

    /**
     * ��ȡ������Ϣ->HashMap
     */
    public void getReportMainMap()
    {
        ReportMain report = new ReportMain();
        /**��ѯ���еı�����Ϣ*/
        Vector vecReportMain = report.query();
        for (int i = 0; i < vecReportMain.size(); i++)
        {
            /**��HashMap����Ӽ�¼*/
            report = (ReportMain) vecReportMain.elementAt(i);
            mapReportMain.put(report.getBranchId() + SysConfig.KEYSEPARATOR +
                              report.getReportId() + SysConfig.KEYSEPARATOR +
                              report.getReportEdition(), report);
        }
    }

    /**
     * ���ر�����Ϣ
     * @return ReportMain ������Ϣ
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
     * ���ر�����Ϣ
     * @param strBranchId ��λ����
     * @param strReportId �������
     * @param strReportEdition ������
     * @return ������Ϣ
     */
    public ReportMain getReportMain(String strBranchId, String strReportId,
                                    String strReportEdition)
    {
        ReportMain report = new ReportMain();
        /**ƴд��ѯ����*/
        String strKey = strBranchId + SysConfig.KEYSEPARATOR +
                        strReportId + SysConfig.KEYSEPARATOR +
                        strReportEdition;
        /**��ʼ��HashMap*/
        if (mapReportMain.size() == 0)
        {
            getReportMainMap();
        }
        /**��HashMap�ж�ȡ������Ϣ*/
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
     * ��ȡ��λ��Ϣ->HashMap
     */
    public void getBranchMap()
    {
        Branch branch = new Branch();
        /**��ȡ���еı�����Ϣ*/
        Vector vecBranch = branch.query();
        for (int i = 0; i < vecBranch.size(); i++)
        {
            /**��HashMap����Ӽ�¼*/
            branch = (Branch) vecBranch.elementAt(i);
            mapBranch.put(branch.getBranchId(), branch);
        }
    }

    /**
     * ���ص�λ��Ϣ
     * @return Branch ��λ��Ϣ
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
     * ���ص�λ��Ϣ
     * @param strBranchId ��ѯ����
     * @return ��λ��Ϣ
     */
    public Branch getBranch(String strBranchId)
    {
        Branch branch = new Branch();
        /**��ʼ������*/
        if (mapBranch.size() == 0)
        {
            getBranchMap();
        }
        /**��HashMap�ж�ȡ��λ��Ϣ*/
        branch = (Branch) (mapBranch.get(strBranchId));
        if (branch == null)
        {
            return new Branch();
        }
        return branch;
    }
    ///////////////////////////End Branch/////////////////////////////////
}
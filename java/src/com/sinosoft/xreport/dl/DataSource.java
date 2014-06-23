package com.sinosoft.xreport.dl;

import java.util.Collection;
import java.util.Map;

public interface DataSource
{

    /**
     * ִ��sql�������ݼ�.ע��ʵ��Ҫȡ��ȫ�����ݺ��ͷ�����
     * @param sql sql��ѯ���
     * @return �����
     * @throws DBExecption ������Ϣ
     */
    public Collection getDataSet(String sql) throws DBExecption, Exception;


    /**
     * �õ����е�����Դ����
     * @return ��Collection�η������е�����Դ Collection��Source��
     * @throws DBExecption ��ȡʱ�����Ĵ���
     */
    public Collection getDataSourceDefine() throws DBExecption, Exception;

    /**
     * �õ����е�ά����
     * @return ��Collection�η������е�ά Collection��Dim��
     * @throws DBExecption ��ȡʱ�����Ĵ���
     */
    public Collection getDimensionDefine() throws Exception, DBExecption;

    /**
     * �õ���strDataSourceIdΪid�ŵ�����Դ�����е�ά����
     * @return ��Collection�η�������������ά Collection��Dim��
     * @throws DBExecption ��ȡʱ�����Ĵ���
     */
    public Collection getDimensionDefine(String strDataSourceId) throws
            DBExecption, Exception;

    /**
     * �õ�ĳ������Դ�ֶε�����ȡֵ.Collection or TreeNode
     * @param tableColumn ����.�ֶ���
     * @param parent ����,�������=null,����ʾ�û���ѡ��ά����һ��ε�ֵ
     * @return ��������ȡֵ Vector�ͻ�TreeNode��
     * @throws DBExecption DB����
     */
    public Object getValue(String tableColumn, Map parent) throws DBExecption,
            Exception;

    /**
     * �õ����б���ֶεĹ�����ϵ Collection
     * @return �������й��� Collection
     * @throws DBExecption DB����
     */
    public Collection getRelations() throws DBExecption, Exception;
}

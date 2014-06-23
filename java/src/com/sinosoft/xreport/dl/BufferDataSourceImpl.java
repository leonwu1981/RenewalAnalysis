package com.sinosoft.xreport.dl;

/**
 * ���崦��ִ��SQL���
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author houzw
 * @version 1.0
 */

import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;


public class BufferDataSourceImpl extends DataSourceImpl
{
    //���SQL������ִ�еĽ��
    Hashtable htResult = new Hashtable();
    DataSourceInf ds = null;

    public BufferDataSourceImpl() throws Exception
    {

    }

    /**
     * ���sql�Ƿ��Ѿ�ִ�У�����Ѿ�ִ�У��ӹ�ϣ����ȡ�����������ִ�У�Ȼ������ϣ��
     * @param sql ������ַ���
     * @return Collection
     * @throws Exception �쳣����
     */
    public Collection execSQL(String sql)
    {
        Collection collection = new Vector();
        if (htResult.containsKey(sql))
        {
            collection = (Vector) htResult.get(sql);
        }
        else
        {
            try
            {
                collection = this.getDataSet(sql);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            htResult.put(sql, collection);
        }
        return collection;
    }


    /**
     * ����DataSourceImpl�е�getDataSet()����
     * ���sql�Ƿ��Ѿ�ִ�У�����Ѿ�ִ�У��ӹ�ϣ����ȡ�����������ִ�У�Ȼ������ϣ��
     * @param sql ������ַ���
     * @return Collection
     * @throws Exception �쳣����
     */
    public Collection getDataSet(String sql) throws DBExecption, Exception
            {
            if (htResult.containsKey(sql))
    {
        return (Collection) htResult.get(sql);
    }
    else
    {
        Collection c = super.getDataSet(sql);
        htResult.put(sql, c);
        return c;
    }
    }
            /**
             * ������
             * @param args[]
             * @throws Exception �쳣����
             */
            public static void main(String[] args) throws Exception
    {
        BufferDataSourceImpl sh = new BufferDataSourceImpl();

    }
}
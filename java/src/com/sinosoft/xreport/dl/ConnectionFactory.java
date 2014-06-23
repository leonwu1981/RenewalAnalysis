package com.sinosoft.xreport.dl;

/**
 * ���ӳ��೧�����೤��������������Դ���ƺ����ݿ����ӳض�Ӧ�Ĺ�ϣ
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author houzw
 * @version 1.0
 */
import java.sql.SQLException;
import java.util.Hashtable;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

public class ConnectionFactory
{
    //�ù�ϣ��������������Դ�������ӳض���Ĺ�ϵ��
    static Hashtable connectionPools = null;
    static
    {
        connectionPools = new Hashtable(5, 0.75F);
    }

    /**
     * �����ӳع����л�ȡָ�����ƶ�Ӧ�����ӳض���
     * @param dataSource	���ӳض����Ӧ������
     * @return DataSource	�������ƶ�Ӧ�����ӳض���
     * @throws NameNotFoundException	�޷��ҵ�ָ�������ӳ�
     */
    public static DataSourceInf lookup(String dataSource) throws
            NameNotFoundException
    {
        Object ds = null;
        ds = connectionPools.get(dataSource);
        if (ds == null || !(ds instanceof DataSourceInf))
        {
            throw new NameNotFoundException(dataSource);
        }
        return (DataSourceInf) ds;
    }

    /**
     * ��ָ�������ֺ����ݿ��������ð���һ�𲢳�ʼ�����ݿ����ӳ�
     * @param name		��Ӧ���ӳص�����
     * @param param	���ӳص����ò��������������ConnectionParam
     * @return DataSource	����󶨳ɹ��󷵻����ӳض���
     * @throws NameAlreadyBoundException	һ������name�Ѿ������׳����쳣
     * @throws ClassNotFoundException		�޷��ҵ����ӳص������е�����������
     * @throws IllegalAccessException		���ӳ������е���������������
     * @throws InstantiationException		�޷�ʵ��������������
     * @throws SQLException				�޷���������ָ�������ݿ�
     */
    public static DataSourceInf bind(String name, DBConnParam param) throws
            NameAlreadyBoundException, ClassNotFoundException,
    IllegalAccessException, InstantiationException, SQLException
            {
            DataSourceImp source = null;
            try
    {
        lookup(name);
        throw new NameAlreadyBoundException(name);
    }
    catch (NameNotFoundException e)
    {
        source = new DataSourceImp(param);
        source.initConnection();
        connectionPools.put(name, source);
    }
    return source;
    }
            /**
             * ���°����ݿ����ӳ�
             * @param name		��Ӧ���ӳص�����
             * @param param	���ӳص����ò��������������ConnectionParam
             * @return DataSource	����󶨳ɹ��󷵻����ӳض���
             * @throws NameAlreadyBoundException	һ������name�Ѿ������׳����쳣
             * @throws ClassNotFoundException		�޷��ҵ����ӳص������е�����������
             * @throws IllegalAccessException		���ӳ������е���������������
             * @throws InstantiationException		�޷�ʵ��������������
             * @throws SQLException				�޷���������ָ�������ݿ�
             */
            public static DataSourceInf rebind(String name, DBConnParam param) throws
            NameAlreadyBoundException, ClassNotFoundException,
    IllegalAccessException, InstantiationException, SQLException
            {
            try
    {
        unbind(name);
    }
    catch (Exception e)
    {}
    return bind(name, param);
    }
            /**
             * ɾ��һ�����ݿ����ӳض���
             * @param name
             * @throws NameNotFoundException
             */
            public static void unbind(String name) throws NameNotFoundException
    {
        DataSourceInf dataSource = lookup(name);
        if (dataSource instanceof DataSourceImp)
        {
            DataSourceImp dsi = (DataSourceImp) dataSource;
            try
            {
                dsi.stop();
            }
            catch (Exception e)
            {}
            try
            {
                dsi.close();
            }
            catch (Exception e)
            {}
            dsi = null;
        }
        connectionPools.remove(name);
    }

}
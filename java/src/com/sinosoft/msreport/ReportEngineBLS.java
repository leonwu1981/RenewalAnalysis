/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Set;

import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.VData;

/**
 * <p>Title: Webҵ��ϵͳ</p>
 * <p>Description: �Զ�BLS</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author Minim
 * @version 1.0
 */
public class ReportEngineBLS
{
    /** �������ݵ����� */
    private VData mInputData = new VData();

    /** �������ݵ����� */
    private VData mResult = new VData();

    /** ���ݲ����ַ��� */
    private String mOperate;

    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** ���ݿ�����  **/
    private Connection conn = null;

    /** �����ύ��־ **/
    private boolean commitFlag = true;

    public ReportEngineBLS()
    {
    }

    /**
     * �����ύ�Ĺ����������ύ�ɹ��󽫷��ؽ���������ڲ�VData������
     * @param cInputData ���������,VData����
     * @param cOperate ���ݲ����ַ�������Ҫ����"INSERT"
     * @return ����ֵ��true--�ύ�ɹ�, false--�ύʧ�ܣ�
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //���������ݿ�����������
        mInputData = (VData) cInputData.clone();
        mOperate = cOperate;

        //������Ϣ
        System.out.println("\n---Start Save---");
        if (!saveData())
        {
            return false;
        }
        System.out.println("---End saveData---\n");

        return true;
    }

    /**
     * �������ݿ����ӣ���Ҫͳһ����ʱʹ��
     * @param c
     */
    public void setConnection(Connection c)
    {
        this.conn = c;
    }

    /**
     * �������ݿ����ӣ���Ҫͳһ����ʱʹ��
     * @return
     */
    public Connection getConnection()
    {
        return this.conn;
    }

    /**
     * �������ݿ�������Ƿ������ύ
     * @param b ����ֵ��true--�����ύ, false--���ύ��
     */
    public void setCommitStatus(boolean b)
    {
        this.commitFlag = b;
    }

    /**
     * ���ݿ����
     * @return: boolean
     */
    private boolean saveData()
    {
        //�������ݿ�����
        if (conn == null)
        {
            conn = DBConnPool.getConnection();
        }

        if (conn == null)
        {
            CError.buildErr(this, "���ݿ�����ʧ��");
            return false;
        }

        try
        {
            //��ʼ��������
            conn.setAutoCommit(false);

            String action = ""; //������ʽ��INSERT\UPDATE\DELETE
            String className = ""; //����
            Object o = null; //Schema��Set����
            Object DBObject = null; //DB��DBSet����
            Method m = null; //����
            Constructor constructor = null; //���캯��
            Class[] parameterC = new Class[1]; //���÷����Ĳ�������
            Object[] parameterO = new Object[1]; //���÷����Ķ�������

            for (int j = 0; j < mInputData.size(); j++)
            {
                if (!(mInputData.get(j) instanceof MMap))
                {
                    continue;
                }

                MMap map = (MMap) mInputData.get(j);
                Set set = map.keySet();

                for (int i = 0; i < set.size(); i++)
                {
                    //��ȡ��������Schema��Set��String��ֱ��ִ��Sql��
                    o = map.getOrder().get(String.valueOf(i + 1));
                    //System.out.println("gxtest"+o);
                    //��ȡ������ʽ
                    action = (String) map.get(o);

                    if ((action == null) || action.equals(""))
                    {
                        continue;
                    }
                    System.out.println("\n" + o.getClass().getName() +
                                       " Operate DB: " + action);

                    //������Ӧ��DB����
                    className = o.getClass().getName();
                    if (className.endsWith("Schema"))
                    {
                        className = "com.sinosoft.lis.db."
                                    +
                                    className.substring(className.lastIndexOf(
                                ".") +
                                1,
                                className.lastIndexOf("S")) + "DB";
                    }
                    else if (className.endsWith("Set"))
                    {
                        className = "com.sinosoft.lis.vdb."
                                    +
                                    className.substring(className.lastIndexOf(
                                ".") +
                                1,
                                className.lastIndexOf("S")) + "DBSet";
                    }
                    else if (className.endsWith("String"))
                    {
                        className = "com.sinosoft.utility.ExeSQL";
                    }
                    Class DBClass = Class.forName(className);

                    //�������캯����������ͬ�����DB��DBSet������ͬ���񼴴������ӣ�
                    parameterC[0] = Connection.class;
                    constructor = DBClass.getConstructor(parameterC);
                    parameterO[0] = conn;
                    DBObject = constructor.newInstance(parameterO);

                    //��DB����ֵ���������Schema��Set��������ݸ��Ƶ�DB��
                    parameterC[0] = o.getClass();

                    //ΪDBSchema��DBSet��������
                    if (o.getClass().getName().endsWith("Schema"))
                    {
                        m = DBObject.getClass().getMethod("setSchema",
                                parameterC);
                    }
                    else if (o.getClass().getName().endsWith("Set"))
                    {
                        m = DBObject.getClass().getMethod("set", parameterC);
                    }

                    if (!o.getClass().getName().endsWith("String"))
                    {
                        parameterO[0] = o;
                        m.invoke(DBObject, parameterO);
                    }

                    //����action�������ݿ�
                    this.switch_DB_Action(DBObject, action, o);
                }
            }

            if (commitFlag)
            {
                conn.commit();
                conn.close();
                System.out.println("---End Committed---");
            }
            else
            {
                System.out.println(
                        "---End Datebase Operation, but not Commit in AutoBLS---");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            CError.buildErr(this, e.toString());

            try
            {
                conn.rollback();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                CError.buildErr(this, ex.toString());
            }
            return false;
        }

        return true;
    }

    /**
     * ����Map��ÿ���������󣩶�Ӧ��ֵ��Action����ѡ�����ݿ������ʽ
     * @param DBObject
     * @param action
     * @throws Exception
     */
    private void switch_DB_Action(Object DBObject, String action, Object o) throws
            Exception
    {
        //ǿ��ת��������ʽΪ��д
        action = action.toUpperCase();

        //����
        if (action.equals("INSERT"))
        {
            this.operateDB(DBObject, "insert");
        }
        //����
        else if (action.equals("UPDATE"))
        {
            this.operateDB(DBObject, "update");
        }
        //ɾ��
        else if (action.equals("DELETE"))
        {
            this.operateDB(DBObject, "delete");
        }
        //��ɾ�������
        else if (action.equals("DELETE&INSERT"))
        {
            //first DELETE
            this.operateDB(DBObject, "delete");

            //then INSERT
            this.operateDB(DBObject, "insert");
        }
        //�������ʧ�ܣ������
        else if (action.equals("UPDATE|INSERT"))
        {
            try
            {
                //if UPDATE fail
                this.operateDB(DBObject, "update");
            }
            catch (Exception ex)
            {
                //then INSERT
                this.operateDB(DBObject, "insert");
            }
        }
        //ֱ��ִ��Sql��䣬��Ҫ���ڸ��¹ؼ��ֵ����
        else if (action.equals("EXESQL"))
        {

            this.operateDB_execSql(DBObject, "execUpdateSQL", o);
        }
    }

    /**
     * �������ݿ⣬ʧ����ر����Ӳ��׳�����
     * @param DBObject
     * @param action
     * @throws Exception
     */
    private void operateDB(Object DBObject, String action) throws Exception
    {
        Method m = DBObject.getClass().getMethod(action, null);
        Boolean b = (Boolean) m.invoke(DBObject, null);

        if (!b.booleanValue())
        {
            conn.rollback();
            conn.close();
            throw new Exception(DBObject.getClass().getName() + " " + action +
                                " Failed");
        }
    }

    /**
     * �������ݿ⣬ֱ��ִ��Sql��䣬ʧ����ر����Ӳ��׳�����
     * @param DBObject
     * @param action
     * @throws Exception
     */
    private void operateDB_execSql(Object DBObject, String action, Object o) throws
            Exception
    {
        Class[] parameterC = new Class[1]; //���÷����Ĳ�������
        Object[] parameterO = new Object[1]; //���÷����Ķ�������

        parameterC[0] = o.getClass();
        parameterO[0] = o;

        Method m = DBObject.getClass().getMethod(action, parameterC);
        //System.out.println("gx1");
        Boolean b = (Boolean) m.invoke(DBObject, parameterO);
        //System.out.println("gx2");
        if (!b.booleanValue())
        {
            conn.rollback();
            conn.close();
            throw new Exception(DBObject.getClass().getName() + " " + action +
                                " Failed");
        }
    }

    /**
     * �������������������ȡ���ݴ�����
     * @return ���������ݲ�ѯ����ַ�����VData����
     */
    public VData getResult()
    {
        return mResult;
    }

    /**
     * ��������������
     */
    public static void main(String[] args)
    {
        ReportEngineBLS ReportEngineBLS1 = new ReportEngineBLS();
    }
}

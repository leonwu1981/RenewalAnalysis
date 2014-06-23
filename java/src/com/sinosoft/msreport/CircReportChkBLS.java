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
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.VData;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class CircReportChkBLS
{
    //����������
    private VData mInputData;
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** ���ݿ�����  **/
    private Connection conn = null;
    /** �����ύ��־ **/
    private boolean commitFlag = true;

    public CircReportChkBLS()
    {
    }

    /**
     * �������ݵĹ�������
     * @param: cInputData ���������
     *         cOperate ���ݲ���
     * @return:
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //���Ƚ������ڱ�������һ������
        mInputData = (VData) cInputData.clone();

        System.out.println("Start CircReportChkBLS Submit...");
        if (!this.saveData())
        {
            return false;
        }
        System.out.println("End CircReportChkBLS Submit...");

        mInputData = null;
        return true;
    }

    /**
     * ���ݿ����
     * @return: boolean
     */
    private boolean saveData()
    {
        System.out.println("---Start Save---");

        //�������ݿ�����
        if (conn == null)
        {
            conn = DBConnPool.getConnection();
        }

        if (conn == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "CircAutoBatchChkBLS";
            tError.functionName = "saveData";
            tError.errorMessage = "���ݿ�����ʧ��!";
            this.mErrors.addOneError(tError);
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
            System.out.println("mInputData.size():" + mInputData.size());
            System.out.println("mInputData :" + mInputData);
            //ͨ��MMap������ÿ��Schema��Set�����ݿ������ʽ��Լ��ʹ��
            MMap map = (MMap) mInputData.getObjectByObjectName("MMap", 0);
            if (map != null && map.keySet().size() != 0)
            {
                Set set = map.keySet();
                //Iterator iterator = map.keySet().iterator();
                //while (iterator.hasNext()) {
                for (int j = 0; j < set.size(); j++)
                {
                    //��ȡ��������Schema��Set��SQL
                    //o = iterator.next();
                    o = map.getOrder().get(String.valueOf(j + 1));
                    //��ȡ������ʽ
                    action = (String) map.get(o);
                    if (action == null)
                    {
                        continue;
                    }
                    System.out.println("\n" + o.getClass().getName() +
                                       " Operate DB: " + action);

                    //������Ӧ��DB����
                    className = o.getClass().getName();
                    System.out.println("className :" + className);
                    System.out.println("action :" + action);

                    if (className.endsWith("String"))
                    {
                        if (action.equals("UPDATE"))
                        {
                            className = "com.sinosoft.lis.db." +
                                        className.
                                        substring(className.lastIndexOf(".") +
                                                  1, className.lastIndexOf("S")) +
                                        "DB";
                            String tSQL = (String) o;
                            ExeSQL tExeSQL = new ExeSQL(conn);
                            System.out.println("ִ��SQL���:" + tSQL);
                            if (!tExeSQL.execUpdateSQL(tSQL))
                            {
                                CError tError = new CError();
                                tError.moduleName = "CircAutoBatchChkBLS";
                                tError.functionName = "saveData";
                                tError.errorMessage = "ִ�и������ʧ��!";
                                this.mErrors.addOneError(tError);
                                conn.rollback();
                                conn.close();
                                return false;
                            }
                            continue;
                        }
                        if (action.equals("DELETE"))
                        {
                            className = "com.sinosoft.lis.db." +
                                        className.
                                        substring(className.lastIndexOf(".") +
                                                  1, className.lastIndexOf("S")) +
                                        "DB";
                            String tSQL = (String) o;
                            ExeSQL tExeSQL = new ExeSQL(conn);
                            System.out.println("ִ��SQL���:" + tSQL);
                            if (!tExeSQL.execUpdateSQL(tSQL))
                            {
                                CError tError = new CError();
                                tError.moduleName = "CircAutoBatchChkBLS";
                                tError.functionName = "saveData";
                                tError.errorMessage = "ִ��ɾ�����ʧ��!";
                                this.mErrors.addOneError(tError);
                                conn.rollback();
                                conn.close();
                                return false;
                            }
                            continue;
                        }
                    }
                    else if (className.endsWith("Schema"))
                    {
                        className = "com.sinosoft.lis.db." +
                                    className.
                                    substring(className.lastIndexOf(".") + 1,
                                              className.lastIndexOf("S")) +
                                    "DB";
                    }
                    else if (className.endsWith("Set"))
                    {
                        className = "com.sinosoft.lis.vdb." +
                                    className.
                                    substring(className.lastIndexOf(".") + 1,
                                              className.lastIndexOf("S")) +
                                    "DBSet";
                    }
                    Class DBClass = Class.forName(className);

                    //ѡ���캯����������ͬ�����DB��DBSet����
                    parameterC[0] = Connection.class;
                    constructor = DBClass.getConstructor(parameterC);
                    parameterO[0] = conn;
                    DBObject = constructor.newInstance(parameterO);

                    //��DB����ֵ���������Schema��Set��������ݸ��Ƶ�DB��
                    parameterC[0] = o.getClass();
                    if (o.getClass().getName().endsWith("Schema"))
                    {
                        m = DBObject.getClass().getMethod("setSchema",
                                parameterC);
                    }
                    else if (o.getClass().getName().endsWith("Set"))
                    {
                        m = DBObject.getClass().getMethod("set", parameterC);
                    }
                    parameterO[0] = o;
                    m.invoke(DBObject, parameterO);

                    //�������ݿ����
                    if (action.equals("INSERT"))
                    {
                        m = DBObject.getClass().getMethod("insert", null);
                        Boolean b = (Boolean) m.invoke(DBObject, null);

                        if (!b.booleanValue())
                        {
                            try
                            {
                                conn.rollback();
                            }
                            catch (Exception e)
                            {}
                            conn.close();
                            System.out.println(DBObject.getClass().getName() +
                                               " " + action + " Failed");
                            return false;
                        }
                    }
                    else if (action.equals("UPDATE"))
                    {
                        m = DBObject.getClass().getMethod("update", null);
                        Boolean b = (Boolean) m.invoke(DBObject, null);

                        if (!b.booleanValue())
                        {
                            try
                            {
                                conn.rollback();
                            }
                            catch (Exception e)
                            {}
                            conn.close();
                            System.out.println(DBObject.getClass().getName() +
                                               " " + action + " Failed");
                            return false;
                        }
                    }
                    else if (action.equals("DELETE"))
                    {
                        m = DBObject.getClass().getMethod("delete", null);
                        Boolean b = (Boolean) m.invoke(DBObject, null);

                        if (!b.booleanValue())
                        {
                            try
                            {
                                conn.rollback();
                            }
                            catch (Exception e)
                            {}
                            conn.close();
                            System.out.println(DBObject.getClass().getName() +
                                               " " + action + " Failed");
                            return false;
                        }
                    }
                    else if (action.equals("DELETE&INSERT"))
                    {
                        //DELETE
                        m = DBObject.getClass().getMethod("delete", null);
                        Boolean b = (Boolean) m.invoke(DBObject, null);

                        if (!b.booleanValue())
                        {
                            try
                            {
                                conn.rollback();
                            }
                            catch (Exception e)
                            {}
                            conn.close();
                            System.out.println(DBObject.getClass().getName() +
                                               " " + action + " Failed");
                            return false;
                        }

                        //INSERT
                        m = DBObject.getClass().getMethod("insert", null);
                        b = (Boolean) m.invoke(DBObject, null);

                        if (!b.booleanValue())
                        {
                            try
                            {
                                conn.rollback();
                            }
                            catch (Exception e)
                            {}
                            conn.close();
                            System.out.println(DBObject.getClass().getName() +
                                               " " + action + " Failed");
                            return false;
                        }
                    }
                } //end of while
            }

            //�����ύ:Ϊ��������һ������������׼����Ϻ�һ�����ύ.
            if (commitFlag)
            {
                conn.commit();
                conn.close();
                System.out.println("---End CircAutoBatchChkBLS Committed---");
            }
            else
            {
                System.out.println(
                        "---End Datebase Operation, but not Commit in AutoBLS---");
            }
        }
        catch (Exception e)
        {
            // @@������
            e.printStackTrace();
            CError tError = new CError();
            tError.moduleName = "PRnewManualDunBLS";
            tError.functionName = "savaData";
            tError.errorMessage = e.toString();
            this.mErrors.addOneError(tError);
            try
            {
                conn.rollback();
            }
            catch (Exception ex)
            {}
            return false;
        }

        return true;
    }

}

/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.atb;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Set;

import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.utility.*;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class GrpATbWorkFlowBLS
{
    //传输数据类
    private VData mInputData;

    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 数据库连接  **/
    private Connection conn = null;

    /** 立即提交标志 **/
    private final boolean commitFlag = true;

    public GrpATbWorkFlowBLS()
    {
    }


    /**
     * 传输数据的公共方法
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //首先将数据在本类中做一个备份
        mInputData = (VData) cInputData.clone();

        System.out.println("Start GrpTbWorkFlowBLS Submit...");
        if (this.saveData())
        {
            System.out.println("End GrpTbWorkFlowBLS Submit...");
            mInputData = null;
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * 数据库操作
     * @return: boolean
     */
    private boolean saveData()
    {
//        System.out.println("---Start Save---");

        //建立数据库连接
        if (conn == null)
        {
            conn = DBConnPool.getConnection();
        }

        if (conn == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "GrpTbWorkFlowBLS";
            tError.functionName = "saveData";
            tError.errorMessage = "数据库连接失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            //开始事务，锁表
            conn.setAutoCommit(false);

            String action = ""; //操作方式，INSERT\UPDATE\DELETE
            String className = ""; //类名
            Object o = null; //Schema或Set对象
            Object DBObject = null; //DB或DBSet对象
            Method m = null; //方法
            Constructor constructor = null; //构造函数
            Class[] parameterC = new Class[1]; //调用方法的参数数组
            Object[] parameterO = new Object[1]; //调用方法的对象数组
            StringBuffer tSBql = null;

//            System.out.println("mInputData.size():" + mInputData.size());
//            System.out.println("mInputData :" + mInputData);

            //通过MMap来传递每个Schema或Set的数据库操作方式，约定使用
            for (int i = 0; i < mInputData.size(); i++)
            {
                VData tData = new VData();
                tData = (VData) mInputData.get(i);
                MMap map = (MMap) tData.getObjectByObjectName("MMap", 0);
                if (map != null && map.keySet().size() != 0)
                {
//                    System.out.println("map.keySet().size():" + map.keySet().size());
                    Set set = map.keySet();
                    //Iterator iterator = map.keySet().iterator();
                    //while (iterator.hasNext()) {
                    for (int j = 0; j < set.size(); j++)
                    {
                        //获取操作对象Schema或Set或SQL
                        //o = iterator.next();
                        o = map.getOrder().get(String.valueOf(j + 1));
                        //获取操作方式
                        action = (String) map.get(o);
                        if (action == null)
                        {
                            continue;
                        }
//                        System.out.println("\n" + o.getClass().getName() + " Operate DB: " + action);

                        //构造相应的DB类名
                        className = o.getClass().getName();
//                        System.out.println("className :" + className);
//                        System.out.println("action :" + action);

                        if (className.endsWith("String"))
                        {
//                            className = "com.sinosoft.lis.db."
//                                    + className.substring(className.lastIndexOf(".") + 1
//                                    , className.lastIndexOf("S")) + "DB";
                            tSBql = new StringBuffer(50);
                            tSBql.append("com.sinosoft.lis.db.");
                            tSBql.append(className.substring(className.lastIndexOf(".") + 1
                                    , className.lastIndexOf("S")));
                            tSBql.append("DB");
                            className = tSBql.toString();

                            String tSQL = (String) o;
                            ExeSQL tExeSQL = new ExeSQL(conn);
//                            System.out.println("执行SQL语句:" + tSQL);
                            if (tExeSQL.execUpdateSQL(tSQL))
                            {
                                continue;
                            }
                            else
                            {
                                this.mErrors.copyAllErrors(tExeSQL.mErrors);
                                CError tError = new CError();
                                tError.moduleName = "GrpTbWorkFlowBLS";
                                tError.functionName = "saveLJAGetserials";
                                tError.errorMessage = "执行DML语句失败!";
                                this.mErrors.addOneError(tError);
                                conn.rollback();
                                conn.close();
                                return false;
                            }
                        }
                        else if (className.endsWith("Schema"))
                        {
//                            className = "com.sinosoft.lis.db." +
//                                    className.
//                                    substring(className.lastIndexOf(".") +
//                                    1, className.lastIndexOf("S")) +
//                                    "DB";
                            tSBql = new StringBuffer(50);
                            tSBql.append("com.sinosoft.lis.db.");
                            tSBql.append(className.substring(className.lastIndexOf(".") + 1
                                    , className.lastIndexOf("S")));
                            tSBql.append("DB");
                            className = tSBql.toString();
                        }
                        else if (className.endsWith("Set"))
                        {
//                            className = "com.sinosoft.lis.vdb." +
//                                    className.
//                                    substring(className.lastIndexOf(".") +
//                                    1, className.lastIndexOf("S")) +
//                                    "DBSet";
                            tSBql = new StringBuffer(50);
                            tSBql.append("com.sinosoft.lis.vdb.");
                            tSBql.append(className.substring(className.lastIndexOf(".") + 1
                                    , className.lastIndexOf("S")));
                            tSBql.append("DBSet");
                            className = tSBql.toString();
                        }
                        Class DBClass = Class.forName(className);

                        //选择构造函数，构造相同事务的DB或DBSet对象
                        parameterC[0] = Connection.class;
                        constructor = DBClass.getConstructor(parameterC);
                        parameterO[0] = conn;
                        DBObject = constructor.newInstance(parameterO);

                        //给DB对象付值，将传入的Schema或Set对象的内容复制到DB中
                        parameterC[0] = o.getClass();
                        if (o.getClass().getName().endsWith("Schema"))
                        {
                            m = DBObject.getClass().getMethod("setSchema", parameterC);
                        }
                        else if (o.getClass().getName().endsWith("Set"))
                        {
                            m = DBObject.getClass().getMethod("set", parameterC);
                        }
                        parameterO[0] = o;
                        m.invoke(DBObject, parameterO);

                        //进行数据库操作
                        if (action.equals("INSERT"))
                        {
                            m = DBObject.getClass().getMethod("insert", null);
                            Boolean b = (Boolean) m.invoke(DBObject, null);

                            if (b.booleanValue())
                            {
                                continue;
                            }
                            else
                            {
                                try
                                {
                                    conn.rollback();
                                }
                                catch (Exception e)
                                {}
                                conn.close();
                                System.out.println(DBObject.getClass().getName() + " " + action
                                        + " Failed");
                                return false;
                            }
                        }
                        else if (action.equals("UPDATE"))
                        {
                            m = DBObject.getClass().getMethod("update", null);
                            Boolean b = (Boolean) m.invoke(DBObject, null);

                            if (b.booleanValue())
                            {
                                continue;
                            }
                            else
                            {
                                try
                                {
                                    conn.rollback();
                                }
                                catch (Exception e)
                                {}
                                conn.close();
                                System.out.println(DBObject.getClass().getName() + " " + action
                                        + " Failed");
                                return false;
                            }
                        }
                        else if (action.equals("DELETE"))
                        {
                            m = DBObject.getClass().getMethod("delete", null);
                            Boolean b = (Boolean) m.invoke(DBObject, null);

                            if (b.booleanValue())
                            {
                                continue;
                            }
                            else
                            {
                                try
                                {
                                    conn.rollback();
                                }
                                catch (Exception e)
                                {}
                                conn.close();
                                System.out.println(DBObject.getClass().getName() + " " + action
                                        + " Failed");
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
                                System.out.println(DBObject.getClass().getName() + " " + action
                                        + " Failed");
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
                                System.out.println(DBObject.getClass().getName() + " " + action
                                        + " Failed");
                                return false;
                            }
                        }
                    } //end of while
                }
            }
            //数据提交:为保正事务一致性所有数据准备完毕后一次性提交.
            if (commitFlag)
            {
                conn.commit();
                conn.close();
                System.out.println("---End Committed---");
            }
            else
            {
                System.out.println("---End Datebase Operation, but not Commit in AutoBLS---");
            }
        }
        catch (Exception e)
        {
            // @@错误处理
            e.printStackTrace();
            CError tError = new CError();
            tError.moduleName = "GrpTbWorkFlowBLS";
            tError.functionName = "savaData";
            tError.errorMessage = e.toString();
            this.mErrors.addOneError(tError);
            try
            {
                //回滚事务，关闭连接
                conn.rollback();
                conn.close();
            }
            catch (Exception ex)
            {}
            return false;
        }
        return true;
    }
}

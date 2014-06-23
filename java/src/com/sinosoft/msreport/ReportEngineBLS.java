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
 * <p>Title: Web业务系统</p>
 * <p>Description: 自动BLS</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author Minim
 * @version 1.0
 */
public class ReportEngineBLS
{
    /** 传入数据的容器 */
    private VData mInputData = new VData();

    /** 传出数据的容器 */
    private VData mResult = new VData();

    /** 数据操作字符串 */
    private String mOperate;

    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 数据库连接  **/
    private Connection conn = null;

    /** 立即提交标志 **/
    private boolean commitFlag = true;

    public ReportEngineBLS()
    {
    }

    /**
     * 数据提交的公共方法，提交成功后将返回结果保存入内部VData对象中
     * @param cInputData 传入的数据,VData对象
     * @param cOperate 数据操作字符串，主要包括"INSERT"
     * @return 布尔值（true--提交成功, false--提交失败）
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //将操作数据拷贝到本类中
        mInputData = (VData) cInputData.clone();
        mOperate = cOperate;

        //保存信息
        System.out.println("\n---Start Save---");
        if (!saveData())
        {
            return false;
        }
        System.out.println("---End saveData---\n");

        return true;
    }

    /**
     * 共享数据库连接，需要统一事务时使用
     * @param c
     */
    public void setConnection(Connection c)
    {
        this.conn = c;
    }

    /**
     * 共享数据库连接，需要统一事务时使用
     * @return
     */
    public Connection getConnection()
    {
        return this.conn;
    }

    /**
     * 设置数据库操作后是否马上提交
     * @param b 布尔值（true--马上提交, false--不提交）
     */
    public void setCommitStatus(boolean b)
    {
        this.commitFlag = b;
    }

    /**
     * 数据库操作
     * @return: boolean
     */
    private boolean saveData()
    {
        //建立数据库连接
        if (conn == null)
        {
            conn = DBConnPool.getConnection();
        }

        if (conn == null)
        {
            CError.buildErr(this, "数据库连接失败");
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
                    //获取操作对象Schema或Set或String（直接执行Sql）
                    o = map.getOrder().get(String.valueOf(i + 1));
                    //System.out.println("gxtest"+o);
                    //获取操作方式
                    action = (String) map.get(o);

                    if ((action == null) || action.equals(""))
                    {
                        continue;
                    }
                    System.out.println("\n" + o.getClass().getName() +
                                       " Operate DB: " + action);

                    //构造相应的DB类名
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

                    //建立构造函数，构造相同事务的DB或DBSet对象（相同事务即传入连接）
                    parameterC[0] = Connection.class;
                    constructor = DBClass.getConstructor(parameterC);
                    parameterO[0] = conn;
                    DBObject = constructor.newInstance(parameterO);

                    //给DB对象付值，将传入的Schema或Set对象的内容复制到DB中
                    parameterC[0] = o.getClass();

                    //为DBSchema或DBSet传入内容
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

                    //根据action操作数据库
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
     * 根据Map中每个键（对象）对应的值（Action），选择数据库操作方式
     * @param DBObject
     * @param action
     * @throws Exception
     */
    private void switch_DB_Action(Object DBObject, String action, Object o) throws
            Exception
    {
        //强制转换操作方式为大写
        action = action.toUpperCase();

        //插入
        if (action.equals("INSERT"))
        {
            this.operateDB(DBObject, "insert");
        }
        //更新
        else if (action.equals("UPDATE"))
        {
            this.operateDB(DBObject, "update");
        }
        //删除
        else if (action.equals("DELETE"))
        {
            this.operateDB(DBObject, "delete");
        }
        //先删除后插入
        else if (action.equals("DELETE&INSERT"))
        {
            //first DELETE
            this.operateDB(DBObject, "delete");

            //then INSERT
            this.operateDB(DBObject, "insert");
        }
        //如果更新失败，则插入
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
        //直接执行Sql语句，主要用于更新关键字的情况
        else if (action.equals("EXESQL"))
        {

            this.operateDB_execSql(DBObject, "execUpdateSQL", o);
        }
    }

    /**
     * 操作数据库，失败则关闭连接并抛出错误
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
     * 操作数据库，直接执行Sql语句，失败则关闭连接并抛出错误
     * @param DBObject
     * @param action
     * @throws Exception
     */
    private void operateDB_execSql(Object DBObject, String action, Object o) throws
            Exception
    {
        Class[] parameterC = new Class[1]; //调用方法的参数数组
        Object[] parameterO = new Object[1]; //调用方法的对象数组

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
     * 数据输出方法，供外界获取数据处理结果
     * @return 包含有数据查询结果字符串的VData对象
     */
    public VData getResult()
    {
        return mResult;
    }

    /**
     * 主函数，测试用
     */
    public static void main(String[] args)
    {
        ReportEngineBLS ReportEngineBLS1 = new ReportEngineBLS();
    }
}

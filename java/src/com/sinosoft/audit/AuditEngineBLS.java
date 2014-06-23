/**
 * Copyright (c) 2006 sinosoft Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.audit;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vdb.*;
import com.sinosoft.lis.vschema.*;

import com.sinosoft.utility.*;

import java.lang.reflect.*;

import java.sql.*;

import java.util.*;

/**
 * <p>
 * ClassName: AuditEngineBLS
 * </p>
 * <p>
 * Company: Sinosoft Co. Ltd.
 * </p>
 * @author Alex
 * @version 1.0
 */
public class AuditEngineBLS
{
    private VData mInputData = new VData();

    private VData mResult = new VData();

    private String mOperate;

    public CErrors mErrors = new CErrors();

    private Connection conn = null;

    private boolean commitFlag = true;

	/**
	 * AuditEngineBLS
	 */
    public AuditEngineBLS()
    {
    }

	/**
	 * submitData
	 * 通用接口：接收传入数据，并对数据进行处理
	 * @param VData cInputData
	 * @param String cOperate
	 */
    public boolean submitData(VData cInputData, String cOperate)
    {
        mInputData = (VData) cInputData.clone();
        mOperate = cOperate;

        System.out.println("\n---Start Save---");

        if (!saveData())
        {
            return false;
        }

        System.out.println("---End saveData---\n");

        return true;
    }

	/**
	 * setConnection
	 * @param Connection c
	 */
    public void setConnection(Connection c)
    {
        this.conn = c;
    }

	/**
	 * getConnection
	 */
    public Connection getConnection()
    {
        return this.conn;
    }

	/**
	 * setCommitStatus
	 * @param boolean b
	 */
    public void setCommitStatus(boolean b)
    {
        this.commitFlag = b;
    }

	/**
	 * saveData
	 */
    private boolean saveData()
    {
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
            conn.setAutoCommit(false);

            String action = ""; 
            String className = ""; 
            Object o = null; 
            Object DBObject = null; 
            Method m = null; 
            Constructor constructor = null; 
            Class[] parameterC = new Class[1]; 
            Object[] parameterO = new Object[1]; 

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
                    o = map.getOrder().get(String.valueOf(i + 1));

                    //System.out.println("gxtest"+o);
                    action = (String) map.get(o);

                    if ((action == null) || action.equals(""))
                    {
                        continue;
                    }

                    System.out.println("\n" + o.getClass().getName() +
                                       " Operate DB: " + action);

                    className = o.getClass().getName();

                    if (className.endsWith("Schema"))
                    {
                        className = "com.sinosoft.lis.db." +
                                    className.substring(className.lastIndexOf(".") +
                                                        1,
                                                        className.lastIndexOf("S")) +
                                    "DB";
                    }
                    else if (className.endsWith("Set"))
                    {
                        className = "com.sinosoft.lis.vdb." +
                                    className.substring(className.lastIndexOf(".") +
                                                        1,
                                                        className.lastIndexOf("S")) +
                                    "DBSet";
                    }
                    else if (className.endsWith("String"))
                    {
                        className = "com.sinosoft.utility.ExeSQL";
                    }

                    Class DBClass = Class.forName(className);

                    parameterC[0] = Connection.class;
                    constructor = DBClass.getConstructor(parameterC);
                    parameterO[0] = conn;
                    DBObject = constructor.newInstance(parameterO);

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

                    if (!o.getClass().getName().endsWith("String"))
                    {
                        parameterO[0] = o;
                        m.invoke(DBObject, parameterO);
                    }

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
                System.out.println("---End Datebase Operation, but not Commit in AutoBLS---");
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
	 * switch_DB_Action
	 * @param Object DBObject
	 * @param String action
	 * @param Object o
	 */
    private void switch_DB_Action(Object DBObject, String action, Object o)
                           throws Exception
    {
        action = action.toUpperCase();

        if (action.equals("INSERT"))
        {
            this.operateDB(DBObject, "insert");
        }

        else if (action.equals("UPDATE"))
        {
            this.operateDB(DBObject, "update");
        }

        else if (action.equals("DELETE"))
        {
            this.operateDB(DBObject, "delete");
        }

        else if (action.equals("DELETE&INSERT"))
        {
            this.operateDB(DBObject, "delete");

            this.operateDB(DBObject, "insert");
        }

        else if (action.equals("UPDATE|INSERT"))
        {
            try
            {
                this.operateDB(DBObject, "update");
            }
            catch (Exception ex)
            {
                this.operateDB(DBObject, "insert");
            }
        }

        else if (action.equals("EXESQL"))
        {
            this.operateDB_execSql(DBObject, "execUpdateSQL", o);
        }
    }

	/**
	 * operateDB
	 * @param Object DBObject
	 * @param String action
	 */
    private void operateDB(Object DBObject, String action)
                    throws Exception
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
	 * operateDB_execSql
	 * @param Object DBObject
	 * @param String action
	 * @param Object o
	 */
    private void operateDB_execSql(Object DBObject, String action, Object o)
                            throws Exception
    {
        Class[] parameterC = new Class[1]; 
        Object[] parameterO = new Object[1]; 

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
	 * getResult
	 * 数据返回
	 */
    public VData getResult()
    {
        return mResult;
    }

	/**
	 * main
	 * 应用测试
	 * @param String[] args
	 */
    public static void main(String[] args)
    {
        AuditEngineBLS ReportEngineBLS1 = new AuditEngineBLS();
    }
}

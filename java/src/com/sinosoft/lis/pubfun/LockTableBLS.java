/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.sql.Connection;

import com.sinosoft.lis.vdb.LDSysTraceDBSet;
import com.sinosoft.lis.vschema.LDSysTraceSet;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.VData;

/**
 * <p>Title: Web业务系统</p>
 * <p>Description: 锁表类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author Minim
 * @version 1.0
 */
public class LockTableBLS
{
    /** 传入数据的容器 */
    private VData mInputData = new VData();
    /** 传出数据的容器 */
    private VData mResult = new VData();
    /** 数据操作字符串 */
    private String mOperate;
    //错误处理类，每个需要错误处理的类中都放置该类
    public CErrors mErrors = new CErrors();

    /** 传入的业务数据 */
    private LDSysTraceSet inLDSysTraceSet = new LDSysTraceSet();

    public LockTableBLS()
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
        this.mInputData = (VData) cInputData.clone();
        this.mOperate = cOperate;

        //得到外部传入的数据,将数据备份到本类中
        if (!getInputData())
        {
            return false;
        }
        System.out.println("---End getInputData---");

        //保存信息
        if (!saveData())
        {
            return false;
        }
        System.out.println("---End saveData---");

        return true;
    }

    /**
     * 将外部传入的数据分解到本类的属性中
     * @return boolean
     */
    private boolean getInputData()
    {
        try
        {
            inLDSysTraceSet = (LDSysTraceSet) mInputData.getObjectByObjectName(
                    "LDSysTraceSet", 0);
        }
        catch (Exception e)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "LockTableBLS";
            tError.functionName = "getInputData";
            tError.errorMessage = "接收数据失败!!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * 数据库操作
     * @return: boolean
     */
    private boolean saveData()
    {
        System.out.println("---Start Save---");

        //建立数据库连接
        Connection conn = DBConnPool.getConnection();
        if (conn == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "LockTableBLS";
            tError.functionName = "saveData";
            tError.errorMessage = "数据库连接失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            //开始事务，锁表
            conn.setAutoCommit(false);

            if (mOperate.equals("INSERT"))
            {
                //数据库操作说明
                //System.out.println("inLDSysTraceSet:" + inLDSysTraceSet.size() + " : " + inLDSysTraceSet.encode());
                LDSysTraceDBSet tLDSysTraceDBSet = new LDSysTraceDBSet(conn);
                tLDSysTraceDBSet.set(inLDSysTraceSet);
                if (!tLDSysTraceDBSet.insert())
                {
                    try
                    {
                        conn.rollback();
                    }
                    catch (Exception e)
                    {}
                    conn.close();
                    System.out.println("LDSysTrace Insert Failed");
                    return false;
                }
            }

            if (mOperate.equals("DELETE"))
            {
                //数据库操作说明
                //System.out.println("inLDSysTraceSet:" + inLDSysTraceSet.size() + " : " + inLDSysTraceSet.encode());
                LDSysTraceDBSet tLDSysTraceDBSet = new LDSysTraceDBSet(conn);
                tLDSysTraceDBSet.set(inLDSysTraceSet);
                if (!tLDSysTraceDBSet.delete())
                {
                    try
                    {
                        conn.rollback();
                    }
                    catch (Exception e)
                    {}
                    conn.close();
                    System.out.println("LDSysTrace Delete Failed");
                    return false;
                }
            }

            conn.commit();
            conn.close();
            System.out.println("---End Committed---");
        }
        catch (Exception ex)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "LockTableBLS";
            tError.functionName = "submitData";
            tError.errorMessage = ex.toString();
            this.mErrors.addOneError(tError);
            try
            {
                conn.rollback();
            }
            catch (Exception e)
            {}
            return false;
        }

        return true;
    }

    /**
     * 数据输出方法，供外界获取数据处理结果
     * @return VData 包含有数据查询结果字符串的VData对象
     */
    public VData getResult()
    {
        return mResult;
    }

    /**
     * 主函数，测试用
     * @param args String[]
     */
    public static void main(String[] args)
    {
//        LockTableBLS LockTableBLS1 = new LockTableBLS();
    }
}

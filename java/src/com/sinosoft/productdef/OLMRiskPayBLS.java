/*
 * <p>ClassName: OLMRiskPayBLS </p>
 * <p>Description: OLMRiskPayBLS类文件 </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: testcompany </p>
 * @Database:
 * @CreateDate：2004-12-13 14:18:00
 */
package com.sinosoft.productdef;

import java.sql.Connection;

import com.sinosoft.lis.db.LMRiskPayDB;
import com.sinosoft.lis.schema.LMRiskPaySchema;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.VData;

public class OLMRiskPayBLS
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
//传输数据类
    private VData mInputData;
    /** 数据操作字符串 */
    private String mOperate;
    public OLMRiskPayBLS()
    {
    }

    public static void main(String[] args)
    {
    }

    /**
      传输数据的公共方法
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //将操作数据拷贝到本类中
        this.mOperate = cOperate;
        mInputData = (VData) cInputData.clone();
        if (this.mOperate.equals("INSERT||MAIN"))
        {
            if (!saveLMRiskPay())
            {
                return false;
            }
        }
        if (this.mOperate.equals("DELETE||MAIN"))
        {
            if (!deleteLMRiskPay())
            {
                return false;
            }
        }
        if (this.mOperate.equals("UPDATE||MAIN"))
        {
            if (!updateLMRiskPay())
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 保存函数
     */
    private boolean saveLMRiskPay()
    {
        LMRiskPaySchema tLMRiskPaySchema = new LMRiskPaySchema();
        tLMRiskPaySchema = (LMRiskPaySchema) mInputData.getObjectByObjectName(
                "LMRiskPaySchema", 0);
        Connection conn;
        conn = null;
        conn = DBConnPool.getConnection();
        if (conn == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "OLMRiskPayBLS";
            tError.functionName = "saveData";
            tError.errorMessage = "数据库连接失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        try
        {
            conn.setAutoCommit(false);
            LMRiskPayDB tLMRiskPayDB = new LMRiskPayDB(conn);
            tLMRiskPayDB.setSchema(tLMRiskPaySchema);
            if (!tLMRiskPayDB.insert())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tLMRiskPayDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMRiskPayBLS";
                tError.functionName = "saveData";
                tError.errorMessage = "数据保存失败!";
                this.mErrors.addOneError(tError);
                conn.rollback();
                conn.close();
                return false;
            }
            conn.commit();
            conn.close();
        }
        catch (Exception ex)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "OLMRiskPayBLS";
            tError.functionName = "submitData";
            tError.errorMessage = ex.toString();
            this.mErrors.addOneError(tError);
            try
            {
                conn.rollback();
                conn.close();
            }
            catch (Exception e)
            {}
            return false;
        }
        return true;
    }

    /**
     * 保存函数
     */
    private boolean deleteLMRiskPay()
    {
        LMRiskPaySchema tLMRiskPaySchema = new LMRiskPaySchema();
        tLMRiskPaySchema = (LMRiskPaySchema) mInputData.getObjectByObjectName(
                "LMRiskPaySchema", 0);
        System.out.println("Start Save...");
        Connection conn;
        conn = null;
        conn = DBConnPool.getConnection();
        if (conn == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "OLMRiskPayBLS";
            tError.functionName = "saveData";
            tError.errorMessage = "数据库连接失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        try
        {
            conn.setAutoCommit(false);
            System.out.println("Start 保存...");
            LMRiskPayDB tLMRiskPayDB = new LMRiskPayDB(conn);
            tLMRiskPayDB.setSchema(tLMRiskPaySchema);
            if (!tLMRiskPayDB.delete())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tLMRiskPayDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMRiskPayBLS";
                tError.functionName = "saveData";
                tError.errorMessage = "数据删除失败!";
                this.mErrors.addOneError(tError);
                conn.rollback();
                conn.close();
                return false;
            }
            conn.commit();
            conn.close();
        }
        catch (Exception ex)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "OLMRiskPayBLS";
            tError.functionName = "submitData";
            tError.errorMessage = ex.toString();
            this.mErrors.addOneError(tError);
            try
            {
                conn.rollback();
                conn.close();
            }
            catch (Exception e)
            {}
            return false;
        }
        return true;
    }

    /**
     * 保存函数
     */
    private boolean updateLMRiskPay()
    {
        LMRiskPaySchema tLMRiskPaySchema = new LMRiskPaySchema();
        tLMRiskPaySchema = (LMRiskPaySchema) mInputData.getObjectByObjectName(
                "LMRiskPaySchema", 0);
        System.out.println("Start Save...");
        Connection conn;
        conn = null;
        conn = DBConnPool.getConnection();
        if (conn == null)
        {
            CError tError = new CError();
            tError.moduleName = "OLMRiskPayBLS";
            tError.functionName = "updateData";
            tError.errorMessage = "数据库连接失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        try
        {
            conn.setAutoCommit(false);
            System.out.println("Start 保存...");
            LMRiskPayDB tLMRiskPayDB = new LMRiskPayDB(conn);
            tLMRiskPayDB.setSchema(tLMRiskPaySchema);
            if (!tLMRiskPayDB.update())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tLMRiskPayDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMRiskPayBLS";
                tError.functionName = "saveData";
                tError.errorMessage = "数据保存失败!";
                this.mErrors.addOneError(tError);
                conn.rollback();
                conn.close();
                return false;
            }
            conn.commit();
            conn.close();
        }
        catch (Exception ex)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "LMRiskPayBLS";
            tError.functionName = "submitData";
            tError.errorMessage = ex.toString();
            this.mErrors.addOneError(tError);
            try
            {
                conn.rollback();
                conn.close();
            }
            catch (Exception e)
            {}
            return false;
        }
        return true;
    }
}

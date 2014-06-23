package com.sinosoft.productdef;

import java.sql.Connection;

import com.sinosoft.lis.db.LMRiskDB;
import com.sinosoft.lis.schema.LMRiskSchema;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.VData;

public class OLMRiskBLS
{
    public CErrors mErrors = new CErrors();
    //传输数据类
    private VData mInputData;
    /** 数据操作字符串 */
    private String mOperate;

    public OLMRiskBLS()
    {
    }

    public boolean submitData(VData cInputData, String cOperate)
    {
        //将操作数据拷贝到本类中
        this.mOperate = cOperate;
        mInputData = (VData) cInputData.clone();
        if (this.mOperate.equals("INSERT||MAIN"))
        {
            if (!saveLMRisk())
            {
                return false;
            }
        }
        if (this.mOperate.equals("DELETE||MAIN"))
        {
            if (!deleteLMRisk())
            {
                return false;
            }
        }
        if (this.mOperate.equals("UPDATE||MAIN"))
        {
            if (!updateLMRisk())
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 保存函数
     */
    private boolean saveLMRisk()
    {
        LMRiskSchema tLMRiskSchema = new LMRiskSchema();
        tLMRiskSchema = (LMRiskSchema) mInputData.getObjectByObjectName(
                "LMRiskSchema", 0);
        Connection conn;
        conn = null;
        conn = DBConnPool.getConnection();
        if (conn == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "OLMRiskBLS";
            tError.functionName = "saveData";
            tError.errorMessage = "数据库连接失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        try
        {
            conn.setAutoCommit(false);
            LMRiskDB tLMRiskDB = new LMRiskDB(conn);
            tLMRiskDB.setSchema(tLMRiskSchema);
            if (!tLMRiskDB.insert())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tLMRiskDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMRiskBLS";
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
            tError.moduleName = "OLMRiskBLS";
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
    private boolean deleteLMRisk()
    {
        LMRiskSchema tLMRiskSchema = new LMRiskSchema();
        tLMRiskSchema = (LMRiskSchema) mInputData.getObjectByObjectName(
                "LMRiskSchema", 0);
        System.out.println("Start Save...");
        Connection conn;
        conn = null;
        conn = DBConnPool.getConnection();
        if (conn == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "OLMRiskBLS";
            tError.functionName = "saveData";
            tError.errorMessage = "数据库连接失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        try
        {
            conn.setAutoCommit(false);
            System.out.println("Start 保存...");
            LMRiskDB tLMRiskDB = new LMRiskDB(conn);
            tLMRiskDB.setSchema(tLMRiskSchema);
            if (!tLMRiskDB.delete())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tLMRiskDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMRiskBLS";
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
            tError.moduleName = "OLMRiskBLS";
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
    private boolean updateLMRisk()
    {
        LMRiskSchema tLMRiskSchema = new LMRiskSchema();
        tLMRiskSchema = (LMRiskSchema) mInputData.getObjectByObjectName(
                "LMRiskSchema", 0);
        System.out.println("Start Save...");
        Connection conn;
        conn = null;
        conn = DBConnPool.getConnection();
        if (conn == null)
        {
            CError tError = new CError();
            tError.moduleName = "OLMRiskBLS";
            tError.functionName = "updateData";
            tError.errorMessage = "数据库连接失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        try
        {
            conn.setAutoCommit(false);
            System.out.println("Start 保存...");
            LMRiskDB tLMRiskDB = new LMRiskDB(conn);
            tLMRiskDB.setSchema(tLMRiskSchema);
            if (!tLMRiskDB.update())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tLMRiskDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMRiskBLS";
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
            tError.moduleName = "LMRiskBLS";
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

/*
 * <p>ClassName: OLMDutyBLS </p>
 * <p>Description: OLMDutyBLS类文件 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: testcompany </p>
 * @Database:
 * @CreateDate：2003-10-28 15:15:24
 */
package com.sinosoft.productdef;

import java.sql.Connection;

import com.sinosoft.lis.db.LMDutyDB;
import com.sinosoft.lis.schema.LMDutySchema;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.VData;

public class OLMDutyBLS
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
//传输数据类
    private VData mInputData;
    /** 数据操作字符串 */
    private String mOperate;
    public OLMDutyBLS()
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
            if (!saveLMDuty())
            {
                return false;
            }
        }
        if (this.mOperate.equals("DELETE||MAIN"))
        {
            if (!deleteLMDuty())
            {
                return false;
            }
        }
        if (this.mOperate.equals("UPDATE||MAIN"))
        {
            if (!updateLMDuty())
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 保存函数
     */
    private boolean saveLMDuty()
    {
        LMDutySchema tLMDutySchema = new LMDutySchema();
        tLMDutySchema = (LMDutySchema) mInputData.getObjectByObjectName(
                "LMDutySchema", 0);
        Connection conn;
        conn = null;
        conn = DBConnPool.getConnection();
        if (conn == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "OLMDutyBLS";
            tError.functionName = "saveData";
            tError.errorMessage = "数据库连接失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        try
        {
            conn.setAutoCommit(false);
            LMDutyDB tLMDutyDB = new LMDutyDB(conn);
            tLMDutyDB.setSchema(tLMDutySchema);
            if (!tLMDutyDB.insert())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tLMDutyDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMDutyBLS";
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
            tError.moduleName = "OLMDutyBLS";
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
    private boolean deleteLMDuty()
    {
        LMDutySchema tLMDutySchema = new LMDutySchema();
        tLMDutySchema = (LMDutySchema) mInputData.getObjectByObjectName(
                "LMDutySchema", 0);
        System.out.println("Start Save...");
        Connection conn;
        conn = null;
        conn = DBConnPool.getConnection();
        if (conn == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "OLMDutyBLS";
            tError.functionName = "saveData";
            tError.errorMessage = "数据库连接失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        try
        {
            conn.setAutoCommit(false);
            System.out.println("Start 保存...");
            LMDutyDB tLMDutyDB = new LMDutyDB(conn);
            tLMDutyDB.setSchema(tLMDutySchema);
            if (!tLMDutyDB.delete())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tLMDutyDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMDutyBLS";
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
            tError.moduleName = "OLMDutyBLS";
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
    private boolean updateLMDuty()
    {
        LMDutySchema tLMDutySchema = new LMDutySchema();
        tLMDutySchema = (LMDutySchema) mInputData.getObjectByObjectName(
                "LMDutySchema", 0);
        System.out.println("Start Save...");
        Connection conn;
        conn = null;
        conn = DBConnPool.getConnection();
        if (conn == null)
        {
            CError tError = new CError();
            tError.moduleName = "OLMDutyBLS";
            tError.functionName = "updateData";
            tError.errorMessage = "数据库连接失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        try
        {
            conn.setAutoCommit(false);
            System.out.println("Start 保存...");
            LMDutyDB tLMDutyDB = new LMDutyDB(conn);
            tLMDutyDB.setSchema(tLMDutySchema);
            if (!tLMDutyDB.update())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tLMDutyDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMDutyBLS";
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
            tError.moduleName = "LMDutyBLS";
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

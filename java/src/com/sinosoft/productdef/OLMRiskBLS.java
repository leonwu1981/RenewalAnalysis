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
    //����������
    private VData mInputData;
    /** ���ݲ����ַ��� */
    private String mOperate;

    public OLMRiskBLS()
    {
    }

    public boolean submitData(VData cInputData, String cOperate)
    {
        //���������ݿ�����������
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
     * ���溯��
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
            // @@������
            CError tError = new CError();
            tError.moduleName = "OLMRiskBLS";
            tError.functionName = "saveData";
            tError.errorMessage = "���ݿ�����ʧ��!";
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
                // @@������
                this.mErrors.copyAllErrors(tLMRiskDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMRiskBLS";
                tError.functionName = "saveData";
                tError.errorMessage = "���ݱ���ʧ��!";
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
            // @@������
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
     * ���溯��
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
            // @@������
            CError tError = new CError();
            tError.moduleName = "OLMRiskBLS";
            tError.functionName = "saveData";
            tError.errorMessage = "���ݿ�����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        try
        {
            conn.setAutoCommit(false);
            System.out.println("Start ����...");
            LMRiskDB tLMRiskDB = new LMRiskDB(conn);
            tLMRiskDB.setSchema(tLMRiskSchema);
            if (!tLMRiskDB.delete())
            {
                // @@������
                this.mErrors.copyAllErrors(tLMRiskDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMRiskBLS";
                tError.functionName = "saveData";
                tError.errorMessage = "����ɾ��ʧ��!";
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
            // @@������
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
     * ���溯��
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
            tError.errorMessage = "���ݿ�����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        try
        {
            conn.setAutoCommit(false);
            System.out.println("Start ����...");
            LMRiskDB tLMRiskDB = new LMRiskDB(conn);
            tLMRiskDB.setSchema(tLMRiskSchema);
            if (!tLMRiskDB.update())
            {
                // @@������
                this.mErrors.copyAllErrors(tLMRiskDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMRiskBLS";
                tError.functionName = "saveData";
                tError.errorMessage = "���ݱ���ʧ��!";
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
            // @@������
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

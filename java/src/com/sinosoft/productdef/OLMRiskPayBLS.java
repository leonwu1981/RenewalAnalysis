/*
 * <p>ClassName: OLMRiskPayBLS </p>
 * <p>Description: OLMRiskPayBLS���ļ� </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: testcompany </p>
 * @Database:
 * @CreateDate��2004-12-13 14:18:00
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
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
//����������
    private VData mInputData;
    /** ���ݲ����ַ��� */
    private String mOperate;
    public OLMRiskPayBLS()
    {
    }

    public static void main(String[] args)
    {
    }

    /**
      �������ݵĹ�������
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //���������ݿ�����������
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
     * ���溯��
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
            // @@������
            CError tError = new CError();
            tError.moduleName = "OLMRiskPayBLS";
            tError.functionName = "saveData";
            tError.errorMessage = "���ݿ�����ʧ��!";
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
                // @@������
                this.mErrors.copyAllErrors(tLMRiskPayDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMRiskPayBLS";
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
     * ���溯��
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
            // @@������
            CError tError = new CError();
            tError.moduleName = "OLMRiskPayBLS";
            tError.functionName = "saveData";
            tError.errorMessage = "���ݿ�����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        try
        {
            conn.setAutoCommit(false);
            System.out.println("Start ����...");
            LMRiskPayDB tLMRiskPayDB = new LMRiskPayDB(conn);
            tLMRiskPayDB.setSchema(tLMRiskPaySchema);
            if (!tLMRiskPayDB.delete())
            {
                // @@������
                this.mErrors.copyAllErrors(tLMRiskPayDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMRiskPayBLS";
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
     * ���溯��
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
            tError.errorMessage = "���ݿ�����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        try
        {
            conn.setAutoCommit(false);
            System.out.println("Start ����...");
            LMRiskPayDB tLMRiskPayDB = new LMRiskPayDB(conn);
            tLMRiskPayDB.setSchema(tLMRiskPaySchema);
            if (!tLMRiskPayDB.update())
            {
                // @@������
                this.mErrors.copyAllErrors(tLMRiskPayDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMRiskPayBLS";
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

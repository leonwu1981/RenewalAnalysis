/*
 * <p>ClassName: OLMDutyBLS </p>
 * <p>Description: OLMDutyBLS���ļ� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: testcompany </p>
 * @Database:
 * @CreateDate��2003-10-28 15:15:24
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
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
//����������
    private VData mInputData;
    /** ���ݲ����ַ��� */
    private String mOperate;
    public OLMDutyBLS()
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
     * ���溯��
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
            // @@������
            CError tError = new CError();
            tError.moduleName = "OLMDutyBLS";
            tError.functionName = "saveData";
            tError.errorMessage = "���ݿ�����ʧ��!";
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
                // @@������
                this.mErrors.copyAllErrors(tLMDutyDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMDutyBLS";
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
     * ���溯��
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
            // @@������
            CError tError = new CError();
            tError.moduleName = "OLMDutyBLS";
            tError.functionName = "saveData";
            tError.errorMessage = "���ݿ�����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        try
        {
            conn.setAutoCommit(false);
            System.out.println("Start ����...");
            LMDutyDB tLMDutyDB = new LMDutyDB(conn);
            tLMDutyDB.setSchema(tLMDutySchema);
            if (!tLMDutyDB.delete())
            {
                // @@������
                this.mErrors.copyAllErrors(tLMDutyDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMDutyBLS";
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
     * ���溯��
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
            tError.errorMessage = "���ݿ�����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        try
        {
            conn.setAutoCommit(false);
            System.out.println("Start ����...");
            LMDutyDB tLMDutyDB = new LMDutyDB(conn);
            tLMDutyDB.setSchema(tLMDutySchema);
            if (!tLMDutyDB.update())
            {
                // @@������
                this.mErrors.copyAllErrors(tLMDutyDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMDutyBLS";
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

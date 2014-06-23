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
 * <p>Title: Webҵ��ϵͳ</p>
 * <p>Description: ������</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author Minim
 * @version 1.0
 */
public class LockTableBLS
{
    /** �������ݵ����� */
    private VData mInputData = new VData();
    /** �������ݵ����� */
    private VData mResult = new VData();
    /** ���ݲ����ַ��� */
    private String mOperate;
    //�������࣬ÿ����Ҫ����������ж����ø���
    public CErrors mErrors = new CErrors();

    /** �����ҵ������ */
    private LDSysTraceSet inLDSysTraceSet = new LDSysTraceSet();

    public LockTableBLS()
    {
    }

    /**
     * �����ύ�Ĺ����������ύ�ɹ��󽫷��ؽ���������ڲ�VData������
     * @param cInputData ���������,VData����
     * @param cOperate ���ݲ����ַ�������Ҫ����"INSERT"
     * @return ����ֵ��true--�ύ�ɹ�, false--�ύʧ�ܣ�
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //���������ݿ�����������
        this.mInputData = (VData) cInputData.clone();
        this.mOperate = cOperate;

        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData())
        {
            return false;
        }
        System.out.println("---End getInputData---");

        //������Ϣ
        if (!saveData())
        {
            return false;
        }
        System.out.println("---End saveData---");

        return true;
    }

    /**
     * ���ⲿ��������ݷֽ⵽�����������
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
            // @@������
            CError tError = new CError();
            tError.moduleName = "LockTableBLS";
            tError.functionName = "getInputData";
            tError.errorMessage = "��������ʧ��!!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * ���ݿ����
     * @return: boolean
     */
    private boolean saveData()
    {
        System.out.println("---Start Save---");

        //�������ݿ�����
        Connection conn = DBConnPool.getConnection();
        if (conn == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "LockTableBLS";
            tError.functionName = "saveData";
            tError.errorMessage = "���ݿ�����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            //��ʼ��������
            conn.setAutoCommit(false);

            if (mOperate.equals("INSERT"))
            {
                //���ݿ����˵��
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
                //���ݿ����˵��
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
            // @@������
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
     * �������������������ȡ���ݴ�����
     * @return VData ���������ݲ�ѯ����ַ�����VData����
     */
    public VData getResult()
    {
        return mResult;
    }

    /**
     * ��������������
     * @param args String[]
     */
    public static void main(String[] args)
    {
//        LockTableBLS LockTableBLS1 = new LockTableBLS();
    }
}

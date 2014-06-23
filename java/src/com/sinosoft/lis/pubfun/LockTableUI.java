/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;

/**
 * <p>Title: Webҵ��ϵͳ</p>
 * <p>Description: ������</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author Minim
 * @version 1.0
 */
public class LockTableUI
{
    /** �������ݵ����� */
    private VData mInputData = new VData();
    /** �������ݵ����� */
    private VData mResult = new VData();
    /** ���ݲ����ַ��� */
    private String mOperate;
    /** �������� */
    public CErrors mErrors = new CErrors();

    public LockTableUI()
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
        // ���ݲ����ַ���������������
        this.mInputData = (VData) cInputData.clone();
        this.mOperate = cOperate;

        System.out.println("---LockTable BL BEGIN---");
        LockTableBL tLockTableBL = new LockTableBL();

        if (tLockTableBL.submitData(cInputData, cOperate) == false)
        {
            // @@������
            this.mErrors.copyAllErrors(tLockTableBL.mErrors);
            mResult.clear();
            mResult.add(mErrors.getFirstError());
            return false;
        }
        else
        {
            mResult = tLockTableBL.getResult();
        }
        System.out.println("---LockTable BL END---");

        return true;
    }

    /**
     * �������������������ȡ���ݴ�����
     * @return ���������ݲ�ѯ����ַ�����VData����
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
        LockTableUI LockTableUI1 = new LockTableUI();
        GlobalInput tGlobalInput = new GlobalInput();
        tGlobalInput.Operator = "001";
        LDSysTraceSchema tLDSysTraceSchema = new LDSysTraceSchema();
        tLDSysTraceSchema.setPolNo("98765423");
        tLDSysTraceSchema.setCreatePos("�б�¼��");
        tLDSysTraceSchema.setPolState("1002");
        LDSysTraceSet inLDSysTraceSet = new LDSysTraceSet();
        inLDSysTraceSet.add(tLDSysTraceSchema);
        VData tVData = new VData();
        tVData.add(tGlobalInput);
        tVData.add(inLDSysTraceSet);
        if (!LockTableUI1.submitData(tVData, "INSERT"))
        {
            VData rVData = LockTableUI1.getResult();
            System.out.println("Submit Failed! " + (String) rVData.get(0));
        }
        else
        {
            System.out.println("Submit Succed!");
        }
    }
}

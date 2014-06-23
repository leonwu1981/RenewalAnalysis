/*
 * <p>ClassName: OLMDutyUI </p>
 * <p>Description: OLMDutyUI���ļ� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: testcompany </p>
 * @Database:
 * @CreateDate��2003-10-28 15:15:24
 */
package com.sinosoft.productdef;

import com.sinosoft.lis.schema.LMDutySchema;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

public class OLMDutyUI
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    private VData mResult = new VData();
    /** �����洫�����ݵ����� */
    private VData mInputData = new VData();
    /** ���ݲ����ַ��� */
    private String mOperate;
//ҵ������ر���
    /** ȫ������ */
    private LMDutySchema mLMDutySchema = new LMDutySchema();
    public OLMDutyUI()
    {
    }

    /**
     �������ݵĹ�������
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //���������ݿ�����������
        this.mOperate = cOperate;

        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData(cInputData))
        {
            return false;
        }

        //����ҵ����
        if (!dealData())
        {
            return false;
        }

        //׼������̨������
        if (!prepareOutputData())
        {
            return false;
        }

        OLMDutyBL tOLMDutyBL = new OLMDutyBL();

        System.out.println("Start OLMDuty UI Submit...");
        tOLMDutyBL.submitData(mInputData, mOperate);
        System.out.println("End OLMDuty UI Submit...");
        //�������Ҫ����Ĵ����򷵻�
        if (tOLMDutyBL.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tOLMDutyBL.mErrors);
            CError tError = new CError();
            tError.moduleName = "OLMDutyUI";
            tError.functionName = "submitData";
            tError.errorMessage = "�����ύʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (mOperate.equals("INSERT||MAIN"))
        {
            this.mResult.clear();
            this.mResult = tOLMDutyBL.getResult();
        }
        mInputData = null;
        return true;
    }

    public static void main(String[] args)
    {
    }

    /**
     * ׼��������������Ҫ������
     * ��������׼������ʱ���������򷵻�false,���򷵻�true
     */
    private boolean prepareOutputData()
    {
        try
        {
            mInputData.clear();
            mInputData.add(this.mLMDutySchema);
        }
        catch (Exception ex)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "LDutyUI";
            tError.functionName = "prepareData";
            tError.errorMessage = "��׼������㴦������Ҫ������ʱ����";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * ����ǰ����������ݣ�����UI�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {
        boolean tReturn = false;
        //�˴�����һЩУ�����
        tReturn = true;
        return tReturn;
    }

    /**
     * �����������еõ����ж���
     *��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean getInputData(VData cInputData)
    {
        //ȫ�ֱ���
        this.mLMDutySchema.setSchema((LMDutySchema) cInputData.
                                     getObjectByObjectName("LMDutySchema", 0));
        return true;
    }

    public VData getResult()
    {
        return this.mResult;
    }
}

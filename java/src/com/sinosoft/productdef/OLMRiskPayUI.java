/*
 * <p>ClassName: OLMRiskPayUI </p>
 * <p>Description: OLMRiskPayUI���ļ� </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: testcompany </p>
 * @Database:
 * @CreateDate��2004-12-13 14:18:00
 */
package com.sinosoft.productdef;

import com.sinosoft.lis.schema.LMRiskPaySchema;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

public class OLMRiskPayUI
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
    private LMRiskPaySchema mLMRiskPaySchema = new LMRiskPaySchema();
    public OLMRiskPayUI()
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

        OLMRiskPayBL tOLMRiskPayBL = new OLMRiskPayBL();

        System.out.println("Start OLMRiskPay UI Submit...");
        tOLMRiskPayBL.submitData(mInputData, mOperate);
        System.out.println("End OLMRiskPay UI Submit...");
        //�������Ҫ����Ĵ����򷵻�
        if (tOLMRiskPayBL.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tOLMRiskPayBL.mErrors);
            CError tError = new CError();
            tError.moduleName = "OLMRiskPayUI";
            tError.functionName = "submitData";
            tError.errorMessage = "�����ύʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (mOperate.equals("INSERT||MAIN"))
        {
            this.mResult.clear();
            this.mResult = tOLMRiskPayBL.getResult();
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
            mInputData.add(this.mLMRiskPaySchema);
        }
        catch (Exception ex)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "LRiskPayUI";
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
        this.mLMRiskPaySchema.setSchema((LMRiskPaySchema) cInputData.
                                        getObjectByObjectName("LMRiskPaySchema",
                0));
        return true;
    }

    public VData getResult()
    {
        return this.mResult;
    }
}

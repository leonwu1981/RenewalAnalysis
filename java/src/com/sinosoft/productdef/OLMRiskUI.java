package com.sinosoft.productdef;

import com.sinosoft.lis.schema.LMRiskSchema;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

public class OLMRiskUI
{
    public CErrors mErrors = new CErrors();
    private VData mResult = new VData();
    private VData mInputData = new VData();
    private String mOperate;

    private LMRiskSchema mLMRiskSchema = new LMRiskSchema();

    public OLMRiskUI()
    {
    }

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

        OLMRiskBL tOLMRiskBL = new OLMRiskBL();
        System.out.println("Start OLMRisk UI Submit...");
        tOLMRiskBL.submitData(mInputData, mOperate);
        System.out.println("End OLMRisk UI Submit...");
        //�������Ҫ����Ĵ����򷵻�
        if (tOLMRiskBL.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tOLMRiskBL.mErrors);
            CError tError = new CError();
            tError.moduleName = "OLMRiskUI";
            tError.functionName = "submitData";
            tError.errorMessage = "�����ύʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (mOperate.equals("INSERT||MAIN"))
        {
            this.mResult.clear();
            this.mResult = tOLMRiskBL.getResult();
        }
        mInputData = null;
        return true;
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
            mInputData.add(this.mLMRiskSchema);
        }
        catch (Exception ex)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "LMRiskUI";
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
        this.mLMRiskSchema.setSchema((LMRiskSchema) cInputData.
                                     getObjectByObjectName("LMRiskSchema", 0));
        return true;
    }

    public VData getResult()
    {
        return this.mResult;
    }
}

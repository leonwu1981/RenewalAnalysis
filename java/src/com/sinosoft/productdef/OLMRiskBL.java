package com.sinosoft.productdef;

import com.sinosoft.lis.db.LMRiskDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LMRiskSchema;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

public class OLMRiskBL
{
    public CErrors mErrors = new CErrors();
    private VData mResult = new VData();
    /** �����洫�����ݵ����� */
    private VData mInputData = new VData();
    /** ȫ������ */
    private GlobalInput mGlobalInput = new GlobalInput();
    /** ���ݲ����ַ��� */
    private String mOperate;
    /** ҵ������ر��� */
    private LMRiskSchema mLMRiskSchema = new LMRiskSchema();

    public OLMRiskBL()
    {
    }

    public boolean submitData(VData cInputData, String cOperate)
    {
        //���������ݿ�����������
        this.mOperate = cOperate;
        System.out.println("this.mOperate " + this.mOperate);
        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData(cInputData))
        {
            return false;
        }
        //����ҵ����
        if (!dealData())
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "OLMRiskBL";
            tError.functionName = "submitData";
            tError.errorMessage = "���ݴ���ʧ��OLMRiskBL-->dealData!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //׼������̨������
        if (!prepareOutputData())
        {
            return false;
        }
        if (this.mOperate.equals("QUERY||MAIN"))
        {
            this.submitquery();
        }
        else
        {
            System.out.println("Start OLMRiskBL Submit...");
            OLMRiskBLS tOLMRiskBLS = new OLMRiskBLS();
            tOLMRiskBLS.submitData(mInputData, mOperate);
            System.out.println("End OLMRiskBL Submit...");
            //�������Ҫ����Ĵ����򷵻�
            if (tOLMRiskBLS.mErrors.needDealError())
            {
                // @@������
                this.mErrors.copyAllErrors(tOLMRiskBLS.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMRiskBL";
                tError.functionName = "submitDat";
                tError.errorMessage = "�����ύʧ��!";
                this.mErrors.addOneError(tError);
                return false;
            }
        }
        mInputData = null;
        return true;
    }

    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {
        return true;
    }

    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean updateData()
    {
        return true;
    }

    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean deleteData()
    {
        return true;
    }

    /**
     * �����������еõ����ж���
     *��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean getInputData(VData cInputData)
    {
        this.mLMRiskSchema.setSchema((LMRiskSchema) cInputData.
                                     getObjectByObjectName("LMRiskSchema", 0));
        return true;
    }

    /**
     * ׼��������������Ҫ������
     * ��������׼������ʱ���������򷵻�false,���򷵻�true
     */
    private boolean submitquery()
    {
        this.mResult.clear();
        LMRiskDB tLMRiskDB = new LMRiskDB();
        tLMRiskDB.setSchema(this.mLMRiskSchema);
        //�������Ҫ����Ĵ����򷵻�
        if (tLMRiskDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "LDRiskBL";
            tError.functionName = "submitData";
            tError.errorMessage = "�����ύʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mInputData = null;
        return true;
    }

    private boolean prepareOutputData()
    {
        try
        {
            this.mInputData.clear();
            this.mInputData.add(this.mLMRiskSchema);
            mResult.clear();
            mResult.add(this.mLMRiskSchema);
        }
        catch (Exception ex)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "LMRiskBL";
            tError.functionName = "prepareData";
            tError.errorMessage = "��׼������㴦������Ҫ������ʱ����";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    public VData getResult()
    {
        return this.mResult;
    }
}

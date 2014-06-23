/*
 * <p>ClassName: OLMRiskPayBL </p>
 * <p>Description: OLMRiskPayBL���ļ� </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: testcompany </p>
 * @Database:
 * @CreateDate��2004-12-13 14:18:00
 */
package com.sinosoft.productdef;

import com.sinosoft.lis.db.LMRiskPayDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LMRiskPaySchema;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

public class OLMRiskPayBL
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    private VData mResult = new VData();
    /** �����洫�����ݵ����� */
    private VData mInputData = new VData();
    /** ȫ������ */
    private GlobalInput mGlobalInput = new GlobalInput();
    /** ���ݲ����ַ��� */
    private String mOperate;
    /** ҵ������ر��� */
    private LMRiskPaySchema mLMRiskPaySchema = new LMRiskPaySchema();
//private LRiskPaySet mLRiskPaySet=new LRiskPaySet();
    public OLMRiskPayBL()
    {
    }

    public static void main(String[] args)
    {
    }

    /**
     * �������ݵĹ�������
     * @param: cInputData ���������
     *         cOperate ���ݲ���
     * @return:
     */
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
            tError.moduleName = "OLMRiskPayBL";
            tError.functionName = "submitData";
            tError.errorMessage = "���ݴ���ʧ��OLMRiskPayBL-->dealData!";
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
            System.out.println("Start OLMRiskPayBL Submit...");
            OLMRiskPayBLS tOLMRiskPayBLS = new OLMRiskPayBLS();

            tOLMRiskPayBLS.submitData(mInputData, mOperate);
            System.out.println("End OLMRiskPayBL Submit...");
            //�������Ҫ����Ĵ����򷵻�
            if (tOLMRiskPayBLS.mErrors.needDealError())
            {
                // @@������
                this.mErrors.copyAllErrors(tOLMRiskPayBLS.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMRiskPayBL";
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
        this.mLMRiskPaySchema.setSchema((LMRiskPaySchema) cInputData.
                                        getObjectByObjectName("LMRiskPaySchema",
                0));
        //this.mGlobalInput.setSchema((GlobalInput)cInputData.getObjectByObjectName("GlobalInput",0));
        return true;
    }

    /**
     * ׼��������������Ҫ������
     * ��������׼������ʱ���������򷵻�false,���򷵻�true
     */
    private boolean submitquery()
    {
        this.mResult.clear();
        LMRiskPayDB tLRiskPayDB = new LMRiskPayDB();
        tLRiskPayDB.setSchema(this.mLMRiskPaySchema);
        //�������Ҫ����Ĵ����򷵻�
        if (tLRiskPayDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLRiskPayDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "LRiskPayBL";
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
            this.mInputData.add(this.mLMRiskPaySchema);
            mResult.clear();
            mResult.add(this.mLMRiskPaySchema);
        }
        catch (Exception ex)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "LRiskPayBL";
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

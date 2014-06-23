/*
 * <p>ClassName: OLMDutyBL </p>
 * <p>Description: OLMDutyBL���ļ� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: testcompany </p>
 * @Database:
 * @CreateDate��2003-10-28 15:15:24
 */
package com.sinosoft.productdef;

import com.sinosoft.lis.db.LMDutyDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LMDutySchema;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

public class OLMDutyBL
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
    private LMDutySchema mLMDutySchema = new LMDutySchema();
//private LDutySet mLDutySet=new LDutySet();
    public OLMDutyBL()
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
            tError.moduleName = "OLMDutyBL";
            tError.functionName = "submitData";
            tError.errorMessage = "���ݴ���ʧ��OLMDutyBL-->dealData!";
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
            System.out.println("Start OLMDutyBL Submit...");
            OLMDutyBLS tOLMDutyBLS = new OLMDutyBLS();
            tOLMDutyBLS.submitData(mInputData, mOperate);
            System.out.println("End OLMDutyBL Submit...");
            //�������Ҫ����Ĵ����򷵻�
            if (tOLMDutyBLS.mErrors.needDealError())
            {
                // @@������
                this.mErrors.copyAllErrors(tOLMDutyBLS.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMDutyBL";
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
        this.mLMDutySchema.setSchema((LMDutySchema) cInputData.
                                     getObjectByObjectName("LMDutySchema", 0));
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
        LMDutyDB tLDutyDB = new LMDutyDB();
        tLDutyDB.setSchema(this.mLMDutySchema);
        //�������Ҫ����Ĵ����򷵻�
        if (tLDutyDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLDutyDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "LDutyBL";
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
            this.mInputData.add(this.mLMDutySchema);
            mResult.clear();
            mResult.add(this.mLMDutySchema);
        }
        catch (Exception ex)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "LDutyBL";
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

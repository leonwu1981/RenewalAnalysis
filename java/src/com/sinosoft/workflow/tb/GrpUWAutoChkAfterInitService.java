/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.cbcheck.GrpUWAutoChkBL;
import com.sinosoft.lis.db.LCGrpContDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LCGrpContSchema;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: ������������:��������Լ�Զ��˱� </p>
 * <p>Description: �Զ��˱�������AfterInit������</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class GrpUWAutoChkAfterInitService implements AfterInitService
{
    /** �������࣬ÿ����Ҫ�����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
//    private MMap mMap = new MMap();
    private VData mInputData = new VData();
    private TransferData mTransferData = new TransferData();
    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();

    /** ���ݲ����ַ��� */
//    private String mGrpContNo = "";

    public GrpUWAutoChkAfterInitService()
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
        System.out.println(">>>>>>submitData");
        //���������ݿ�����������
        mInputData = (VData) cInputData.clone();

        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData(cInputData))
        {
            return false;
        }

        GrpUWAutoChkBL tGrpUWAutoChkBL = new GrpUWAutoChkBL();
        boolean tUWResult = tGrpUWAutoChkBL.submitData(mInputData, null);

        if (!tUWResult)
        {
            if (!tGrpUWAutoChkBL.mErrors.needDealError())
            {
                CError.buildErr(this, "�Զ��˱�ʧ�ܣ�");
            }
            else
            {
                this.mErrors.copyAllErrors(tGrpUWAutoChkBL.mErrors);
            }
            mTransferData.setNameAndValue("FinishFlag", "0");
            // return false;
        }
        else
        {
            mTransferData.setNameAndValue("FinishFlag", "1");
        }

        if (!prepareTransferData())
        {
            return false;
        }

        return tUWResult;
        //return true;
    }

    /**
     * �����������еõ����ж���
     *��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean getInputData(VData cInputData)
    {
        GlobalInput tGlobalInput = new GlobalInput();
        tGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);

        LCGrpContSchema tLCGrpContSchema = (LCGrpContSchema) cInputData.
                getObjectByObjectName(
                "LCGrpContSchema", 0); //�����������л�ȡ��ͬ��¼������
        LCGrpContDB tLCGrpContDB = new LCGrpContDB();
        tLCGrpContDB.setGrpContNo(tLCGrpContSchema.getGrpContNo());
        if (!tLCGrpContDB.getInfo()) //��֤LCGrpCont�����Ƿ���ڸú�ͬ���¼
        {
            CError tError = new CError();
            tError.moduleName = "GrpUWAutoChkBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "�����ͬ��Ϊ" + tLCGrpContSchema.getGrpContNo() +
                    "�ĺ�ͬ��Ϣδ��ѯ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCGrpContSchema = tLCGrpContDB.getSchema();

        return true;
    }


    /**
     * Ϊ�����������ݼ��������ӹ�������һ�ڵ������ֶ�����
     * @return
     */
    private boolean prepareTransferData()
    {

        mTransferData.setNameAndValue("GrpContNo", mLCGrpContSchema.getGrpContNo());
        mTransferData.setNameAndValue("PrtNo", mLCGrpContSchema.getPrtNo());
        mTransferData.setNameAndValue("AgentCode",
                mLCGrpContSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup",
                mLCGrpContSchema.getAgentGroup());
        mTransferData.setNameAndValue("SaleChnl", mLCGrpContSchema.getSaleChnl());
        mTransferData.setNameAndValue("ManageCom",
                mLCGrpContSchema.getManageCom());
        mTransferData.setNameAndValue("GrpNo", mLCGrpContSchema.getAppntNo());
        mTransferData.setNameAndValue("GrpName", mLCGrpContSchema.getGrpName());
        mTransferData.setNameAndValue("CValiDate",
                mLCGrpContSchema.getCValiDate());
        LCGrpContDB tLCGrpContDB = new LCGrpContDB();
        tLCGrpContDB.setGrpContNo(mLCGrpContSchema.getGrpContNo());
        if (!tLCGrpContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "GrpUWAutoChkBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "��ѯLCGrpCont��ʧ�ܣ�";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCGrpContSchema = tLCGrpContDB.getSchema();
        mTransferData.setNameAndValue("UWFlag", mLCGrpContSchema.getUWFlag());

        return true;
    }


    /**
     * ���ش�����Ľ��
     * @return VData
     */
    public VData getResult()
    {
        return mResult;
    }


    /**
     * ���ع������е�Lwfieldmap��������ֵ
     * @return TransferData
     */
    public TransferData getReturnTransferData()
    {
        return mTransferData;
    }


    /**
     * ���ش������
     * @return CErrors
     */
    public CErrors getErrors()
    {
        return mErrors;
    }
}
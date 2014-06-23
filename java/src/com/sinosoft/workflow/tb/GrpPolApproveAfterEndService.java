/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.cbcheck.GrpUWAutoChkByRuleBL;
import com.sinosoft.lis.db.LCGrpContDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LCGrpContSchema;
import com.sinosoft.lis.schema.LMUWSchema;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterEndService;


/**
 * <p>Title: ������������:��������Լ�Զ��˱� </p>
 * <p>Description: �Զ��˱�������AfterInit������</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class GrpPolApproveAfterEndService implements AfterEndService
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();


    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
//    private MMap mMap = new MMap();
    private VData mInputData = new VData();
    private TransferData mTransferData = new TransferData();
    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();
    private LMUWSchema mLMUWSchema = new LMUWSchema();  //����ǰ�洫��������˱���ʾ��Ϣ


    /** ���ݲ����ַ��� */
    private String mGrpContNo = "";

    public GrpPolApproveAfterEndService()
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

        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData(cInputData))
        {
            return false;
        }

        GrpUWAutoChkByRuleBL tGrpUWAutoChkByRuleBL = new GrpUWAutoChkByRuleBL();
        boolean tUWResult = tGrpUWAutoChkByRuleBL.submitData(mInputData, null);

        if (tUWResult)
        {
            mTransferData.setNameAndValue("FinishFlag", "1");
        }
        else
        {
            if (tGrpUWAutoChkByRuleBL.mErrors.needDealError())
            {
                this.mErrors.copyAllErrors(tGrpUWAutoChkByRuleBL.mErrors);
            }
            else
            {
                CError.buildErr(this, "�Զ��˱�ʧ�ܣ�");
            }
            mTransferData.setNameAndValue("FinishFlag", "0");
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
        mLMUWSchema = (LMUWSchema) cInputData.getObjectByObjectName(
                "LMUWSchema", 0);

        //��õ�ǰ���������GrpContNo
        mGrpContNo = (String) mTransferData.getValueByName("GrpContNo");
        if (mGrpContNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������GrpContNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        LCGrpContDB tLCGrpContDB = new LCGrpContDB();
        tLCGrpContDB.setGrpContNo(mGrpContNo);
        if (!tLCGrpContDB.getInfo()) //��֤LCGrpCont�����Ƿ���ڸú�ͬ���¼
        {
            CError tError = new CError();
            tError.moduleName = "GrpUWAutoChkBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "�����ͬ��Ϊ" + mGrpContNo +
                    "�ĺ�ͬ��Ϣδ��ѯ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCGrpContSchema = tLCGrpContDB.getSchema();

        mInputData.clear();
        mInputData.add(tGlobalInput);
        mInputData.add(mLCGrpContSchema);
        mInputData.add(mLMUWSchema);

        return true;
    }


    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return
     */
    private boolean prepareTransferData()
    {

        mTransferData.setNameAndValue("GrpContNo",
                mLCGrpContSchema.getGrpContNo());
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
     * ���ش����Ľ��
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

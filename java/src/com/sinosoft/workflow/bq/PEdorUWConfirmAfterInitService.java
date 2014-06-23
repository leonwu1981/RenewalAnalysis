package com.sinosoft.workflow.bq;

import com.sinosoft.lis.bq.PEdorManuUWUI;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.schema.LPUWMasterMainSchema;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: </p>
 * <p>Description: ������������:���˱�ȫ�˹��˱������˱�����</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * ReWrite ZhangRong
 * @version 1.0
 */

public class PEdorUWConfirmAfterInitService implements AfterInitService
{

    /** ȫ������ */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** �����洫�����ݵ����� */
    private VData mInputData;
    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
    private MMap mResultMap = new MMap();

    public PEdorUWConfirmAfterInitService()
    {
    }

    public VData getResult()
    {
        return mResult;
    }

    public TransferData getReturnTransferData()
    {
        return mTransferData;
    }

    public CErrors getErrors()
    {
        return mErrors;
    }

    /**
     * �������ݵĹ�������
     * @param: cInputData ���������
     *         cOperate ���ݲ���
     * @return:
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        mInputData = (VData) cInputData.clone();

        if (!getInputData(cInputData))
        {
            return false;
        }

        if (!dealData())
        {
            return false;
        }

        if (!prepareTransferData())
        {
            return false;
        }
        if (!prepareOutputData())
        {
            return false;
        }

        return true;
    }


    /**
     * �����������еõ����ж���
     *��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean getInputData(VData cInputData)
    {
        //�����������еõ����ж���
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);
        LPUWMasterMainSchema tLPUWMasterMainSchema = (LPUWMasterMainSchema) mTransferData.
                getValueByName("LPUWMasterMainSchema");
        if (mGlobalInput == null || mTransferData == null || tLPUWMasterMainSchema == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "�������ݲ���ȫ!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {
        //���������ݿ�����������
        PEdorManuUWUI tPEdorManuUWUI = new PEdorManuUWUI();
        boolean tUWResult = tPEdorManuUWUI.submitData(mInputData, "");

        if (!tUWResult)
        {
            if (!tPEdorManuUWUI.mErrors.needDealError())
            {
                CError.buildErr(this, "�˹��˱�ʧ�ܣ�");
            }
            else
            {
                this.mErrors.copyAllErrors(tPEdorManuUWUI.mErrors);
            }
            mTransferData.setNameAndValue("FinishFlag", "0");
        }
        else
        {
            VData tVData = (VData) tPEdorManuUWUI.getResult();
            if (tVData == null)
            {
                CError.buildErr(this, "��ȡ�˹��˱����ʧ�ܣ�");
                return false;
            }
            mResultMap = (MMap) tVData.getObjectByObjectName("MMap", 0);
            if (mResultMap == null)
            {
                CError.buildErr(this, "��ȡ�˹��˱����ʧ�ܣ�");
                return false;
            }
            mTransferData.setNameAndValue("FinishFlag", "1");
        }

        return tUWResult;
    }

    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return
     */
    private boolean prepareTransferData()
    {
        return true;
    }

    /**
     * ׼������ǰ̨ͳһ�洢����
     * �����������������򷵻�false,���򷵻�true
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        mResult.add(mResultMap);
        return true;
    }

}

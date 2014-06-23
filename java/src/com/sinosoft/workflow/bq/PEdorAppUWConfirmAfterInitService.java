package com.sinosoft.workflow.bq;

import java.util.*;
import java.lang.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.bq.*;
import com.sinosoft.lis.vdb.*;
import com.sinosoft.utility.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.workflowengine.*;

/**
 * <p>Title: </p>
 * <p>Description: ������������:���˱�ȫ�˹��˱������˱�����</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author FanX
 * @version 1.0
 */

public class PEdorAppUWConfirmAfterInitService implements AfterInitService
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

    public PEdorAppUWConfirmAfterInitService()
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
     *�����������еõ����ж���
     *��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @param cInputData
     * @return
     */
    private boolean getInputData(VData cInputData)
    {
        //�����������еõ����ж���
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);
        LPUWMasterMainSchema tLPUWMasterMainSchema = (LPUWMasterMainSchema) mTransferData.getValueByName("LPUWMasterMainSchema");
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
        PEdorAppManuUWUI tPEdorAppManuUWUI = new PEdorAppManuUWUI();
        boolean tUWResult = tPEdorAppManuUWUI.submitData(mInputData, "");

        if (!tUWResult)
        {
            if (!tPEdorAppManuUWUI.mErrors.needDealError())
            {
                CError.buildErr(this, "�˹��˱�ʧ�ܣ�");
            }
            else
            {
                this.mErrors.copyAllErrors(tPEdorAppManuUWUI.mErrors);
            }
            mTransferData.setNameAndValue("FinishFlag", "0");
        }
        else
        {
            VData tVData = (VData) tPEdorAppManuUWUI.getResult();
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

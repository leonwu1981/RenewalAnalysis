/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.tb.LCGrpContSignBL;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;


/**
 * <p>Title: Webҵ��ϵͳ</p>
 * <p>Description: �ŵ���ͬǩ��������</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author wujs
 * @version 6.0
 */

public class GrpContSignAfterInitService implements AfterInitService
{
    public GrpContSignAfterInitService()
    {
    }

    /**��Ž��*/
    private VData mVResult = new VData();


    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();


    private TransferData mTransferData = new TransferData();


    /** �����洫�����ݵ����� */
    private VData mInputData;


    /** ȫ������ */
//    private GlobalInput mGlobalInput = new GlobalInput();


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
        //  mInputData = (VData) cInputData.clone();
        if (!getInputData(cInputData, cOperate))
        {
            return false;
        }

        LCGrpContSignBL tLCGrpContSignBL = new LCGrpContSignBL();
        boolean tSignResult = tLCGrpContSignBL.submitData(mInputData, null);
        if (tSignResult)
        {
            mTransferData.setNameAndValue("FinishFlag", "1");
        }
        else
        {
            if (tLCGrpContSignBL.mErrors.needDealError())
            {
                this.mErrors.copyAllErrors(tLCGrpContSignBL.mErrors);
            }
            else
            {
                CError.buildErr(this, "����ǩ�����");
            }
            mTransferData.setNameAndValue("FinishFlag", "0");
        }
        this.mVResult.add(mTransferData);
        return tSignResult;
    }


    public VData getResult()
    {
        return this.mVResult;
    }

    public TransferData getReturnTransferData()
    {
        return this.mTransferData;
    }

    public CErrors getErrors()
    {
        return this.mErrors;
    }

    private boolean getInputData(VData cInputData, String cOperate)
    {
        this.mInputData = cInputData;
        this.mTransferData = (TransferData) mInputData.getObjectByObjectName("TransferData", 0);
        if (mTransferData == null)
        {
            return false;
        }
        return true;
    }
}

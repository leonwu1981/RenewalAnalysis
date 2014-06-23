/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.tb.LCContSignBL;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: Webҵ��ϵͳ</p>
 * <p>Description: ��ͬǩ��������</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author wujs
 * @version 6.0
 */
public class ProposalSignAfterInitService implements AfterInitService
{
    /**��Ž��*/
    private VData mVResult = new VData();

    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    //private VData mIputData = new VData();
    private TransferData mTransferData = new TransferData();

    /** �����洫�����ݵ����� */
    private VData mInputData;

    public ProposalSignAfterInitService()
    {}

    /**
     * �������ݵĹ�������
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
//        System.out.println(">>>>>>submitData");
        //���������ݿ�����������
        mInputData = (VData) cInputData.clone();

        LCContSignBL tLCContSignBL = new LCContSignBL();
        boolean tSignResult = tLCContSignBL.submitData(mInputData, null);
        if (!tSignResult)
        {
            if (!tLCContSignBL.mErrors.needDealError())
            {
                CError.buildErr(this, "ǩ��û��ͨ��");
            }
            else
            {
                this.mErrors.copyAllErrors(tLCContSignBL.mErrors);
            }
            return false;
        }

        if (tLCContSignBL.mErrors.needDealError())
        {
            this.mErrors.copyAllErrors(tLCContSignBL.mErrors);
            return false;
        }

        this.mVResult = tLCContSignBL.mVResult;

        return true;
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
}

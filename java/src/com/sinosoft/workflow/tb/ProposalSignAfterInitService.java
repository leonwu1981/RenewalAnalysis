/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.tb.LCContSignBL;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: Web业务系统</p>
 * <p>Description: 合同签单处理类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author wujs
 * @version 6.0
 */
public class ProposalSignAfterInitService implements AfterInitService
{
    /**存放结果*/
    private VData mVResult = new VData();

    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    //private VData mIputData = new VData();
    private TransferData mTransferData = new TransferData();

    /** 往后面传输数据的容器 */
    private VData mInputData;

    public ProposalSignAfterInitService()
    {}

    /**
     * 传输数据的公共方法
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
//        System.out.println(">>>>>>submitData");
        //将操作数据拷贝到本类中
        mInputData = (VData) cInputData.clone();

        LCContSignBL tLCContSignBL = new LCContSignBL();
        boolean tSignResult = tLCContSignBL.submitData(mInputData, null);
        if (!tSignResult)
        {
            if (!tLCContSignBL.mErrors.needDealError())
            {
                CError.buildErr(this, "签单没有通过");
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

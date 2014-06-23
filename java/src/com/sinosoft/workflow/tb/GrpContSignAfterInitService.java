/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.tb.LCGrpContSignBL;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;


/**
 * <p>Title: Web业务系统</p>
 * <p>Description: 团单合同签单处理类</p>
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

    /**存放结果*/
    private VData mVResult = new VData();


    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();


    private TransferData mTransferData = new TransferData();


    /** 往后面传输数据的容器 */
    private VData mInputData;


    /** 全局数据 */
//    private GlobalInput mGlobalInput = new GlobalInput();


    /**
     * 传输数据的公共方法
     * @param: cInputData 输入的数据
     *         cOperate 数据操作
     * @return:
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        System.out.println(">>>>>>submitData");
        //将操作数据拷贝到本类中
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
                CError.buildErr(this, "部分签单完成");
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

package com.sinosoft.workflow.bq;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.bq.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PEdorAppManuUWAfterInitService implements AfterInitService
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 往界面传输数据的容器 */
    private VData mResult = new VData();
    private MMap mMap = new MMap();
    private VData mInputData = new VData();
    private TransferData mTransferData = new TransferData();
    private String mContNo;

    public PEdorAppManuUWAfterInitService()
    {
    }

    public boolean submitData(VData cInputData, String cOperate)
    {
        if (!getInputData(cInputData))
        {
            return false;
        }

        if (!dealData())
        {
            return false;
        }
        return true;
    }

    /**
     * 返回处理后的结果
     * @return VData
     */
    public VData getResult()
    {
        return mResult;
    }

    /**
     * 返回工作流中的Lwfieldmap所描述的值
     * @return TransferData
     */
    public TransferData getReturnTransferData()
    {
        return mTransferData;
    }

    /**
     * 返回错误对象
     * @return CErrors
     */
    public CErrors getErrors()
    {
        return mErrors;
    }

    private boolean getInputData(VData cInputData)
    {
		if (cInputData == null)
		{
			CError tError = new CError();
			tError.moduleName = "PEdorManuUWAfterInitService";
			tError.functionName = "dealData";
			tError.errorMessage = "传入数据不完全！";
			this.mErrors.addOneError(tError);
			return false;
		}
		mInputData = (VData) cInputData.clone();
		return true;
    }

    private boolean dealData()
    {
		PEdorAppUWManuApplyChkUI tPEdorAppUWManuApplyChkUI   = new PEdorAppUWManuApplyChkUI();
		if (tPEdorAppUWManuApplyChkUI.submitData(mInputData,"INSERT") == false)
		{
			mErrors.copyAllErrors(tPEdorAppUWManuApplyChkUI.mErrors);
			CError tError = new CError();
			tError.moduleName = "PEdorAppManuUWAfterInitService";
			tError.functionName = "dealData";
			tError.errorMessage = "个人保全人工核保申请失败！";
			this.mErrors.addOneError(tError);
			return false;
		}
        return true;
    }
}

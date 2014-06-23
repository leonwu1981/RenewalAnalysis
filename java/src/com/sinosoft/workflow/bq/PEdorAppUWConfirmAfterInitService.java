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
 * <p>Description: 工作流服务类:个人保全人工核保批单核保结论</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author FanX
 * @version 1.0
 */

public class PEdorAppUWConfirmAfterInitService implements AfterInitService
{

    /** 全局数据 */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往后面传输数据的容器 */
    private VData mInputData;
    /** 往界面传输数据的容器 */
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
     * 传输数据的公共方法
     * @param: cInputData 输入的数据
     *         cOperate 数据操作
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
     *从输入数据中得到所有对象
     *输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @param cInputData
     * @return
     */
    private boolean getInputData(VData cInputData)
    {
        //从输入数据中得到所有对象
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);
        LPUWMasterMainSchema tLPUWMasterMainSchema = (LPUWMasterMainSchema) mTransferData.getValueByName("LPUWMasterMainSchema");
        if (mGlobalInput == null || mTransferData == null || tLPUWMasterMainSchema == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "传输数据不完全!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     */
    private boolean dealData()
    {
        //将操作数据拷贝到本类中
        PEdorAppManuUWUI tPEdorAppManuUWUI = new PEdorAppManuUWUI();
        boolean tUWResult = tPEdorAppManuUWUI.submitData(mInputData, "");

        if (!tUWResult)
        {
            if (!tPEdorAppManuUWUI.mErrors.needDealError())
            {
                CError.buildErr(this, "人工核保失败！");
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
                CError.buildErr(this, "获取人工核保结果失败！");
                return false;
            }
            mResultMap = (MMap) tVData.getObjectByObjectName("MMap", 0);
            if (mResultMap == null)
            {
                CError.buildErr(this, "获取人工核保结果失败！");
                return false;
            }
            mTransferData.setNameAndValue("FinishFlag", "1");
        }

        return tUWResult;
    }

    /**
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
     * @return
     */
    private boolean prepareTransferData()
    {
        return true;
    }

    /**
     * 准备返回前台统一存储数据
     * 输出：如果发生错误则返回false,否则返回true
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        mResult.add(mResultMap);
        return true;
    }
    

}

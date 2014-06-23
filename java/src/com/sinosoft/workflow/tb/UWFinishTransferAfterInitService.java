/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.cbcheck.UWTakeBackFinishTransferBL;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: 工作流节点任务:新契约回收划帐失败函，处理完毕服务类 </p>
 * <p>Description: 回收回收划帐失败函，处理完毕工作流服务类 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: SinoSoft</p>
 * @author Yuanaq
 * @version 1.0
 */

public class UWFinishTransferAfterInitService implements AfterInitService
{

    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 往界面传输数据的容器 */
    private VData mResult = new VData();

    /** 往界面传输数据的容器 */
    private VData mInputData = new VData();


    /** 往工作流引擎中传输数据的容器 */
//    private GlobalInput mGlobalInput = new GlobalInput();

    private TransferData mTransferData = new TransferData();

    public UWFinishTransferAfterInitService()
    {
    }

    /**
     * 传输数据的公共方法
     * @param cInputData VData 输入的数据
     * @param cOperate String 数据操作
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //得到外部传入的数据,将数据备份到本类中
        if (!getInputData(cInputData, cOperate))
        {
            return false;
        }
        //进行业务验证
        if (!checkData())
        {
            return false;
        }
        //进行业务处理
        if (!dealData())
        {
            return false;
        }

        //准备往后台的数据
        if (!prepareOutputData())
        {
            return false;
        }

        return true;
    }

    /**
     * 准备返回前台统一存储数据
     * 输出：如果发生错误则返回false,否则返回true
     * @return boolean
     */
    private static boolean prepareOutputData()
    {
        return true;
    }

    /**
     * 校验业务数据
     * @return boolean
     */
    private static boolean checkData()
    {
        return true;
    }


    /**
     * 从输入数据中得到所有对象
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    private boolean getInputData(VData cInputData, String cOperate)
    {
        mInputData = (VData) cInputData.clone();
        return true;
    }


    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     * @return boolean
     */
    private boolean dealData()
    {
        UWTakeBackFinishTransferBL tUWTakeBackFinishTransferBL = new UWTakeBackFinishTransferBL();
        if (!tUWTakeBackFinishTransferBL.submitData(mInputData, ""))
        {
            CError tError = new CError();
            tError.moduleName = "UWPrintTakeBackAutoIssueAfterInitService";
            tError.functionName = "dealdata";
            tError.errorMessage = "回收问题件，处理出错";
            this.mErrors.addOneError(tError);
            return false;
        }
        mResult = tUWTakeBackFinishTransferBL.getResult();
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
}

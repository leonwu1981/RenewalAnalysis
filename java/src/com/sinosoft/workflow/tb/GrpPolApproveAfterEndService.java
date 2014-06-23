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
 * <p>Title: 工作流服务类:团体新契约自动核保 </p>
 * <p>Description: 自动核保工作流AfterInit服务类</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class GrpPolApproveAfterEndService implements AfterEndService
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();


    /** 往界面传输数据的容器 */
    private VData mResult = new VData();
//    private MMap mMap = new MMap();
    private VData mInputData = new VData();
    private TransferData mTransferData = new TransferData();
    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();
    private LMUWSchema mLMUWSchema = new LMUWSchema();  //保存前面传过来的需核保提示信息


    /** 数据操作字符串 */
    private String mGrpContNo = "";

    public GrpPolApproveAfterEndService()
    {
    }


    /**
     * 传输数据的公共方法
     * @param: cInputData 输入的数据
     *         cOperate 数据操作
     * @return:
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        System.out.println(">>>>>>submitData");

        //得到外部传入的数据,将数据备份到本类中
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
                CError.buildErr(this, "自动核保失败！");
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
     * 从输入数据中得到所有对象
     *输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
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

        //获得当前工作任务的GrpContNo
        mGrpContNo = (String) mTransferData.getValueByName("GrpContNo");
        if (mGrpContNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中GrpContNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        LCGrpContDB tLCGrpContDB = new LCGrpContDB();
        tLCGrpContDB.setGrpContNo(mGrpContNo);
        if (!tLCGrpContDB.getInfo()) //验证LCGrpCont表中是否存在该合同项记录
        {
            CError tError = new CError();
            tError.moduleName = "GrpUWAutoChkBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "集体合同号为" + mGrpContNo +
                    "的合同信息未查询到!";
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
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
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
            tError.errorMessage = "查询LCGrpCont表失败！";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCGrpContSchema = tLCGrpContDB.getSchema();
        mTransferData.setNameAndValue("UWFlag", mLCGrpContSchema.getUWFlag());

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

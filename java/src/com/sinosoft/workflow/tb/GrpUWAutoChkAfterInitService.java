/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.cbcheck.GrpUWAutoChkBL;
import com.sinosoft.lis.db.LCGrpContDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LCGrpContSchema;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: 工作流服务类:团体新契约自动核保 </p>
 * <p>Description: 自动核保工作流AfterInit服务类</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class GrpUWAutoChkAfterInitService implements AfterInitService
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 往界面传输数据的容器 */
    private VData mResult = new VData();
//    private MMap mMap = new MMap();
    private VData mInputData = new VData();
    private TransferData mTransferData = new TransferData();
    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();

    /** 数据操作字符串 */
//    private String mGrpContNo = "";

    public GrpUWAutoChkAfterInitService()
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
        //将操作数据拷贝到本类中
        mInputData = (VData) cInputData.clone();

        //得到外部传入的数据,将数据备份到本类中
        if (!getInputData(cInputData))
        {
            return false;
        }

        GrpUWAutoChkBL tGrpUWAutoChkBL = new GrpUWAutoChkBL();
        boolean tUWResult = tGrpUWAutoChkBL.submitData(mInputData, null);

        if (!tUWResult)
        {
            if (!tGrpUWAutoChkBL.mErrors.needDealError())
            {
                CError.buildErr(this, "自动核保失败！");
            }
            else
            {
                this.mErrors.copyAllErrors(tGrpUWAutoChkBL.mErrors);
            }
            mTransferData.setNameAndValue("FinishFlag", "0");
            // return false;
        }
        else
        {
            mTransferData.setNameAndValue("FinishFlag", "1");
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

        LCGrpContSchema tLCGrpContSchema = (LCGrpContSchema) cInputData.
                getObjectByObjectName(
                "LCGrpContSchema", 0); //从输入数据中获取合同记录的数据
        LCGrpContDB tLCGrpContDB = new LCGrpContDB();
        tLCGrpContDB.setGrpContNo(tLCGrpContSchema.getGrpContNo());
        if (!tLCGrpContDB.getInfo()) //验证LCGrpCont表中是否存在该合同项记录
        {
            CError tError = new CError();
            tError.moduleName = "GrpUWAutoChkBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "集体合同号为" + tLCGrpContSchema.getGrpContNo() +
                    "的合同信息未查询到!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCGrpContSchema = tLCGrpContDB.getSchema();

        return true;
    }


    /**
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
     * @return
     */
    private boolean prepareTransferData()
    {

        mTransferData.setNameAndValue("GrpContNo", mLCGrpContSchema.getGrpContNo());
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

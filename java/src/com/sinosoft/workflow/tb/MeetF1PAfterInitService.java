/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LOPRTManagerDB;
import com.sinosoft.lis.f1print.MeetF1PBLS;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.LOPRTManagerSchema;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;


/**
 * <p>Title: 工作流服务类:新契约打印生调通知书 </p>
 * <p>Description:打印生调通知书工作流AfterInit服务类 </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class MeetF1PAfterInitService implements AfterInitService
{
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();

    private String mOperate = "";
    private String CurrentDate = PubFun.getCurrentDate();

    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 往界面传输数据的容器 */
    private VData mResult = new VData();

    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();

    private TransferData mTransferData = new TransferData();

    /** 数据操作字符串 */
    private String mOperater;
    private String mManageCom;

    /** 业务数据操作字符串 */
    private String mMissionID;

    public MeetF1PAfterInitService()
    {
    }


    /**
     * 传输数据的公共方法
     * @param cInputData
     * @param cOperate
     * @return
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        mOperate = cOperate;
        try
        {
            // 得到外部传入的数据，将数据备份到本类中
            if (!getInputData(cInputData, mOperate))
            {
                return false;
            }

            if (!saveData(cInputData))
            {
                return false;
            }

            return true;

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            buildError("submitData", ex.toString());
            return false;
        }
    }

    /**
     * 保存打印状态数据
     * @param mInputData VData
     * @return boolean
     */
    private boolean saveData(VData mInputData)
    {

        //根据印刷号查询打印队列中的纪录
        //mLOPRTManagerSchema
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();
        tLOPRTManagerDB.setSchema(mLOPRTManagerSchema);
        if (!tLOPRTManagerDB.getInfo())
        {
            mErrors.copyAllErrors(tLOPRTManagerDB.mErrors);
            buildError("outputXML", "在取得打印队列中数据时发生错误");
            return false;
        }
        mLOPRTManagerSchema = tLOPRTManagerDB.getSchema();
        mLOPRTManagerSchema.setStateFlag("1");
        mLOPRTManagerSchema.setDoneDate(CurrentDate);
        mLOPRTManagerSchema.setExeOperator(mGlobalInput.Operator);
        tLOPRTManagerDB.setSchema((LOPRTManagerSchema) mInputData.
                getObjectByObjectName("LOPRTManagerSchema", 0));

        mResult.add(mLOPRTManagerSchema);
        mResult.add(tLOPRTManagerDB);
        MeetF1PBLS tMeetF1PBLS = new MeetF1PBLS();
        tMeetF1PBLS.submitData(mResult, mOperate);
        if (tMeetF1PBLS.mErrors.needDealError())
        {
            mErrors.copyAllErrors(tMeetF1PBLS.mErrors);
            buildError("saveData", "提交数据库出错！");
            return false;
        }
        return true;
    }

    /**
     * 从输入数据中得到所有对象
     *输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean getInputData(VData cInputData, String cOperate)
    {
        //从输入数据中得到所有对象
        //获得全局公共数据
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);

        if (mGlobalInput == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintSendNoticeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得操作员编码
        mOperater = mGlobalInput.Operator;
        if (mOperater == null || mOperater.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintSendNoticeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据Operate失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得登陆机构编码
        mManageCom = mGlobalInput.ManageCom;
        if (mManageCom == null || mManageCom.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWSendNoticeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mOperate = cOperate;

        //获得当前工作任务的任务ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintSendNoticeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //全局变量
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mLOPRTManagerSchema.setSchema((LOPRTManagerSchema) cInputData.
                getObjectByObjectName(
                "LOPRTManagerSchema", 0));

        if (mGlobalInput == null)
        {
            buildError("getInputData", "没有得到足够的信息！");
            return false;
        }

        return true;
    }

    private void buildError(String szFunc, String szErrMsg)
    {
        CError cError = new CError();

        cError.moduleName = "RefuseAppF1PBL";
        cError.functionName = szFunc;
        cError.errorMessage = szErrMsg;
        this.mErrors.addOneError(cError);
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

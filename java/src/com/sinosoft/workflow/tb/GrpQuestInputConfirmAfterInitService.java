/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LCGrpContDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.schema.LCGrpContSchema;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;


/**
 * <p>Title: 工作流服务类模版 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author heyq
 * @version 1.0
 */

public class GrpQuestInputConfirmAfterInitService implements AfterInitService
{

    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();


    /** 往后面传输数据的容器 */
    private VData mInputData;


    /** 往界面传输数据的容器 */
    private VData mResult = new VData();


    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();


    //private VData mIputData = new VData();
    private TransferData mTransferData = new TransferData();


    /** 数据操作字符串 */
    private String mOperater;
//    private String mManageCom;
    private String mOperate;
    private String mMissionID;
    private String mSubMissionID;
    private String mGrpContNo;
    private String mLCGrpPolSql = "";
    private String mLCContSql = "";
    private String mLCPolSql = "";
    private String mLCGCUWMasterSql = "";
    private String mLCGUWMasterSql = "";
    private String mLCCUWMasterSql = "";
    private String mLCUWMasterSql = "";
    private String mLCGCUWSubSql = "";
    private String mLCGUWSubSql = "";
    private String mLCCUWSubSql = "";
    private String mLCUWSubSql = "";
    private String mLCGCUWErrorSql = "";
    private String mLCGUWErrorSql = "";
    private String mLCCUWErrorSql = "";
    private String mLCUWErrorSql = "";


    /** 业务操作类 */
    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();

    public GrpQuestInputConfirmAfterInitService()
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
        //得到外部传入的数据,将数据备份到本类中
        if (!getInputData(cInputData, cOperate))
        {
            return false;
        }

        //校验是否有未打印的体检通知书
        if (!checkData())
        {
            return false;
        }

        System.out.println("Start  dealData...");

        //进行业务处理
        if (!dealData())
        {
            return false;
        }

        System.out.println("dealData successful!");

        //为工作流下一节点属性字段准备数据
        if (!prepareTransferData())
        {
            return false;
        }

        //准备往后台的数据
        if (!prepareOutputData())
        {
            return false;
        }

        System.out.println("Start  Submit...");

        return true;
    }


    /**
     * 准备返回前台统一存储数据
     * 输出：如果发生错误则返回false,否则返回true
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();

        map.put(mLCGrpContSchema, "UPDATE");
        map.put(mLCGrpPolSql, "UPDATE");
        map.put(mLCContSql, "UPDATE");
        map.put(mLCPolSql, "UPDATE");
        map.put(mLCGCUWMasterSql, "DELETE");
        map.put(mLCGUWMasterSql, "DELETE");
        map.put(mLCCUWMasterSql, "DELETE");
        map.put(mLCUWMasterSql, "DELETE");
        map.put(mLCGCUWSubSql, "DELETE");
        map.put(mLCGUWSubSql, "DELETE");
        map.put(mLCCUWSubSql, "DELETE");
        map.put(mLCUWSubSql, "DELETE");
        map.put(mLCGCUWErrorSql, "DELETE");
        map.put(mLCGUWErrorSql, "DELETE");
        map.put(mLCCUWErrorSql, "DELETE");
        map.put(mLCUWErrorSql, "DELETE");

        mResult.add(map);
        return true;
    }


    /**
     * 校验业务数据
     * @return
     */
    private boolean checkData()
    {
        LCGrpContDB tLCGrpContDB = new LCGrpContDB();
        tLCGrpContDB.setGrpContNo(mGrpContNo);
        if (!tLCGrpContDB.getInfo())
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpQuestInputConfirmAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "查询集体保单失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCGrpContSchema = tLCGrpContDB.getSchema();

        ExeSQL tExeSQL = new ExeSQL();
        String tSql = "select count(1) from lcgrpissuepol where ProposalGrpContNo = '" + mGrpContNo
                + "' and BackObjType = '1' and  ReplyMan is null ";
        String rs = tExeSQL.getOneValue(tSql);
        if (Integer.parseInt(rs) <= 0)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpQuestInputConfirmAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "未录入问题件!";
            this.mErrors.addOneError(tError);
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
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpQuestInputConfirmAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpQuestInputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据Operate失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mOperate = cOperate;
        //获得当前工作任务的任务ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpQuestInputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的任务ID
        mSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
        if (mSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpQuestInputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的GrpContNo
        mGrpContNo = (String) mTransferData.getValueByName("GrpContNo");
        if (mGrpContNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpQuestInputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中GrpContNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
      /**
      * 根据前面的输入数据，进行BL逻辑处理
      * 如果在处理过程中出错，则返回false,否则返回true
      */
     private boolean dealData()
     {
         //将状态置为未核保状态

         //准备lcgrpcont表数据
         mLCGrpContSchema.setUWFlag("");
         mLCGrpContSchema.setUWDate("");
         mLCGrpContSchema.setUWOperator("");
         mLCGrpContSchema.setUWTime("");

         //准备lcgrppol表数据
         mLCGrpPolSql =
                 "update lcgrppol set uwflag='',UWOperator='',UWDate=null,UWTime=null where GrpContNo = '"
                 + mGrpContNo + "'";

         //准备lccont表数据
         mLCContSql =
                 "update lccont set uwflag='',UWOperator='',UWDate=null,UWTime=null where GrpContNo = '"
                 + mGrpContNo + "'";

         //准备lcpol表数据
         mLCPolSql =
                 "update lcpol set uwflag='',uwcode='',UWDate=null,UWTime=null where GrpContNo = '"
                 + mGrpContNo + "'";

         //准备LCGCUWMaster数据
         mLCGCUWMasterSql = "delete from LCGCUWMaster where ProposalGrpContNo = '" + mGrpContNo
                 + "'";

         //准备LCGUWMaster数据
         mLCGUWMasterSql = "delete from LCGUWMaster where GrpContNo = '" + mGrpContNo + "'";

         //准备lccuwmaster数据
         mLCCUWMasterSql = "delete from lccuwmaster where GrpContNo = '" + mGrpContNo + "'";

         //准备lcuwmaster数据
         mLCUWMasterSql = "delete from lcuwmaster where GrpContNo = '" + mGrpContNo + "'";

         //准备LCGCUWSub数据
         mLCGCUWSubSql = "delete from LCGCUWSub where ProposalGrpContNo = '" + mGrpContNo + "'";

         //准备LCGUWSub数据
         mLCGUWSubSql = "delete from LCGUWSub where GrpContNo = '" + mGrpContNo + "'";

         //准备lccuwSub数据
         mLCCUWSubSql = "delete from lccuwSub where GrpContNo = '" + mGrpContNo + "'";

         //准备lcuwSub数据
         mLCUWSubSql = "delete from lcuwSub where GrpContNo = '" + mGrpContNo + "'";

         //准备LCGCUWError数据
         mLCGCUWErrorSql = "delete from LCGCUWError where ProposalGrpContNo = '" + mGrpContNo + "'";

         //准备LCGUWError数据
         mLCGUWErrorSql = "delete from LCGUWError where GrpContNo = '" + mGrpContNo + "'";

         //准备lccuwError数据
         mLCCUWErrorSql = "delete from lccuwError where GrpContNo = '" + mGrpContNo + "'";

         //准备lcuwError数据
         mLCUWErrorSql = "delete from lcuwError where GrpContNo = '" + mGrpContNo + "'";

         return true;
     }


    /**
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
     * @return
     */
    private boolean prepareTransferData()
    {
        mTransferData.setNameAndValue("GrpContNo", mGrpContNo);
        mTransferData.setNameAndValue("PrtNo", mLCGrpContSchema.getPrtNo());
        mTransferData.setNameAndValue("SaleChnl", mLCGrpContSchema.getSaleChnl());
        mTransferData.setNameAndValue("ManageCom",
                mLCGrpContSchema.getManageCom());
        mTransferData.setNameAndValue("AgentCode",
                mLCGrpContSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup",
                mLCGrpContSchema.getAgentGroup());
        mTransferData.setNameAndValue("GrpNo", mLCGrpContSchema.getAppntNo());
        mTransferData.setNameAndValue("GrpName", mLCGrpContSchema.getGrpName());
        mTransferData.setNameAndValue("CValiDate",
                mLCGrpContSchema.getCValiDate());

        return true;
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

}

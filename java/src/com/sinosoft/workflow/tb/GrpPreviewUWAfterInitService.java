/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LCGrpContDB;
import com.sinosoft.lis.db.LCGrpPolDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.LCGrpPolSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;
import com.sinosoft.lis.vschema.LCGrpContSet;


/**
 * <p>Title: 团体新契约新单初核 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Liuliang
 * @version 1.0
 */

public class GrpPreviewUWAfterInitService  implements AfterInitService
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
    private LCProtocolRelaContSchema mLCProtocolRelaContSchema   = new LCProtocolRelaContSchema();
    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();


    /** 数据操作字符串 */
    private String mOperater;
//    private String mManageCom;
    private String mOperate;
    private String mMissionID;
//    private String mSubMissionID;
    //协议关联标记
    private String mProtocolRelaFlag;
    //特殊件标记
    private String mSpecialFlag;
    private String mProtocolPrtNo;
    private String mPrtNo;

    public GrpPreviewUWAfterInitService()
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
        //如果合同关联了协议，则将关联信息写入到协议与合同关联表里面
        if(mProtocolRelaFlag.equals("Y"))
        {
          map.put(mLCProtocolRelaContSchema, "INSERT");
        }
        mResult.add(map);
        return true;
    }


    /**
     * 校验业务数据
     * @return
     */
    private boolean checkData()
    {


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
            tError.moduleName = "GrpInputConfirmAfterInitService";
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
            tError.moduleName = "GrpInputConfirmAfterInitService";
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
            tError.moduleName = "GrpInputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

//        //获得当前工作任务的任务ID
//        mGrpContNo = (String) mTransferData.getValueByName("GrpContNo");
//        if (mGrpContNo == null)
//        {
//            // @@错误处理
//            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
//            CError tError = new CError();
//            tError.moduleName = "GrpInputConfirmAfterInitService";
//            tError.functionName = "getInputData";
//            tError.errorMessage = "前台传输业务数据中mContNo失败!";
//            this.mErrors.addOneError(tError);
//            return false;
//        }

        return true;
    }

    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     */
    private boolean dealData()
    {

        mPrtNo = (String) mTransferData.getValueByName("PrtNo");
        mProtocolRelaFlag = (String) mTransferData.getValueByName("ProtocolRelaFlag");
        mSpecialFlag = (String) mTransferData.getValueByName("SpecialFlag");
        mProtocolPrtNo = (String) mTransferData.getValueByName("ProtocolPrtNo");


        LCGrpContDB tLCGrpContDB = new LCGrpContDB();
        String strSQL = "SELECT * FROM LCGrpCont WHERE PrtNo = '" +
            mPrtNo + "'";
        LCGrpContSet tempLCGrpContSet = tLCGrpContDB.executeQuery(strSQL);
        System.out.println("$$$$$$$$" + strSQL);
        if (tempLCGrpContSet.size() == 0) {
          mErrors.copyAllErrors(tLCGrpContDB.mErrors);
          CError tError = new CError();
          tError.moduleName = "GrpPreviewUWAfterInitService";
          tError.functionName = "dealdata";
          tError.errorMessage = "查询LCGrpCont表，获取数据失败";
          this.mErrors.addOneError(tError);

          return false;
        }

        tLCGrpContDB.setSchema(tempLCGrpContSet.get(1));
        mLCGrpContSchema = tLCGrpContDB.getSchema();

      //为团体合同表存入录入人、录入时间
        //mLCGrpContSchema.setInputOperator(mOperater);
        mLCGrpContSchema.setModifyDate(PubFun.getCurrentDate());
        mLCGrpContSchema.setModifyTime(PubFun.getCurrentTime());

        mLCGrpContSchema.setSpecialFlag(mSpecialFlag);
        mLCGrpContSchema.setProtocolRelaFlag(mProtocolRelaFlag);



        //如果合同关联了协议，则将关联信息写入到协议与合同关联表里面
        if(mProtocolRelaFlag.equals("Y"))
        {
          mLCProtocolRelaContSchema.setProtocolNo(mProtocolPrtNo);
          mLCProtocolRelaContSchema.setPrtNo(mPrtNo);
          mLCProtocolRelaContSchema.setTypeCode("2");

          mLCProtocolRelaContSchema.setOperator(mOperater);
          mLCProtocolRelaContSchema.setMakeDate(PubFun.getCurrentDate());
          mLCProtocolRelaContSchema.setMakeTime(PubFun.getCurrentTime());
          mLCProtocolRelaContSchema.setModifyDate(PubFun.getCurrentDate());
          mLCProtocolRelaContSchema.setModifyTime(PubFun.getCurrentTime());
        }
        return true;

    }



    /**
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
     * @return
     */
    private boolean prepareTransferData()
    {
//        mTransferData.setNameAndValue("ProposalGrpContNo", mLCGrpContSchema.getProposalGrpContNo());
//        mTransferData.setNameAndValue("PrtNo", mLCGrpContSchema.getPrtNo());
//        mTransferData.setNameAndValue("SaleChnl", mLCGrpContSchema.getSaleChnl());
//        mTransferData.setNameAndValue("ManageCom", mLCGrpContSchema.getManageCom());
//        mTransferData.setNameAndValue("AgentCode", mLCGrpContSchema.getAgentCode());
//        mTransferData.setNameAndValue("AgentGroup", mLCGrpContSchema.getAgentGroup());
//        mTransferData.setNameAndValue("GrpName", mLCGrpContSchema.getGrpName());
//        mTransferData.setNameAndValue("CValiDate", mLCGrpContSchema.getCValiDate());

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

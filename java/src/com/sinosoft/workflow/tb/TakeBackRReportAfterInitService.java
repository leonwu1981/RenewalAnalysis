/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LCContDB;
import com.sinosoft.lis.db.LCRReportDB;
import com.sinosoft.lis.db.LOPRTManagerDB;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.LCContSchema;
import com.sinosoft.lis.schema.LCRReportSchema;
import com.sinosoft.lis.schema.LOPRTManagerSchema;
import com.sinosoft.lis.vschema.LCRReportSet;
import com.sinosoft.lis.vschema.LOPRTManagerSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title:工作流节点任务:新契约回收生调通知书 </p>
 * <p>Description:人工核保生调通知书回收AfterInit服务类 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: SinoSoft </p>
 * @author HYQ
 * @version 1.0
 */

public class TakeBackRReportAfterInitService implements AfterInitService
{
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
    private String mContNo;
    private String mMissionID;
    private String mPrtSeq;
    private boolean mPatchFlag;
    private String mReplyContente;
    private String mOldPrtSeq; //如果该单证是补打单证,则同时将遗失原单证也回收.反之如果回收原单证,但其已补发过,则同时也要把补发的单证回收掉

    /**执行保全工作流特约活动表任务0000000013*/
    /**保单表*/
    private LCContSchema mLCContSchema = new LCContSchema();

    /** 保全核保主表 */
//    private LCCUWMasterSchema mLCCUWMasterSchema = new LCCUWMasterSchema();

    /** 打印管理表 */
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();
    private LOPRTManagerSet mLOPRTManagerSet = new LOPRTManagerSet(); //如果该单证是补打单证,则同时将遗失原单证也回收.反之如果回收原单证,但其已补发过,则同时也要把补发的单证回收掉

    /** 生存调查表 */
    private LCRReportSchema mLCRReportSchema = new LCRReportSchema();

    public TakeBackRReportAfterInitService()
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
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();

        //添加核保通知书打印管理表数据
        if (mLOPRTManagerSet != null && mLOPRTManagerSet.size() > 0)
        {
            map.put(mLOPRTManagerSet, "UPDATE");
        }

//        //添加保全批单核保主表数据
//        if (mLCCUWMasterSchema != null)
//        {
//            map.put(mLCCUWMasterSchema, "UPDATE");
//        }

        //添加保全生调表数据
        if (mLCRReportSchema != null)
        {
            map.put(mLCRReportSchema, "UPDATE");
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
        //校验保单信息
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mContNo);
        if (!tLCContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mContNo + "信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);

        //校验保全批单核保主表
        //校验保单信息
//        LCCUWMasterDB tLCCUWMasterDB = new LCCUWMasterDB();
//        tLCCUWMasterDB.setContNo(mContNo);
//        if (!tLCCUWMasterDB.getInfo())
//        {
//            CError tError = new CError();
//            tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
//            tError.functionName = "checkData";
//            tError.errorMessage = "保单" + mContNo + "保全批单核保主表信息查询失败!";
//            this.mErrors.addOneError(tError);
//            return false;
//        }
//
//        mLCCUWMasterSchema.setSchema(tLCCUWMasterDB);

        // 处于未打印状态的核保通知书在打印队列中只能有一个
        // 条件：同一个单据类型，同一个其它号码，同一个其它号码类型
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

        tLOPRTManagerDB.setCode(PrintManagerBL.CODE_MEET); //
        tLOPRTManagerDB.setPrtSeq(mPrtSeq);
        tLOPRTManagerDB.setOtherNo(mContNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_CONT); //保单号
        tLOPRTManagerDB.setStateFlag("1");

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "查询打印管理表信息出错!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLOPRTManagerSet.size() != 1)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "在打印队列中没有处于已打印待回收状态的生调通知书!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLOPRTManagerSchema = tLOPRTManagerSet.get(1);
        if (mLOPRTManagerSchema.getPatchFlag() == null)
        {
            mPatchFlag = false;
        }
        else if (mLOPRTManagerSchema.getPatchFlag().equals("0"))
        {
            mPatchFlag = false;
        }
        else if (mLOPRTManagerSchema.getPatchFlag().equals("1"))
        {
            mPatchFlag = true;
        }

        //如果该单证是补打单证,则同时将遗失原单证也回收.反之如果回收原单证,但其已补发过,则同时也要把补发的单证回收掉
        if (mPatchFlag)
        {
            LOPRTManagerDB tempLOPRTManagerDB = new LOPRTManagerDB();
            mOldPrtSeq = mLOPRTManagerSchema.getOldPrtSeq();
            String tStr = "select * from LOPRTManager where (PrtSeq = '" +
                    mOldPrtSeq + "' or OldPrtSeq = '" + mOldPrtSeq + "')";
            LOPRTManagerSet tempLOPRTManagerSet = tempLOPRTManagerDB.
                    executeQuery(tStr); ;
            if (tempLOPRTManagerSet.size() == 1)
            {
                // @@错误处理
                CError tError = new CError();
                tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
                tError.functionName = "preparePrint";
                tError.errorMessage = "查询在打印队列中没有该补打生调通知书的原通知书记录信息出错!";
                this.mErrors.addOneError(tError);
                return false;
            }

            for (int i = 1; i <= tempLOPRTManagerSet.size(); i++)
            {
                mLOPRTManagerSet.add(tempLOPRTManagerSet.get(i));
            }
        }
        else
        {
            LOPRTManagerDB tempLOPRTManagerDB = new LOPRTManagerDB();
            mOldPrtSeq = mLOPRTManagerSchema.getPrtSeq();
            if (mOldPrtSeq != null && !mOldPrtSeq.equals(""))
            {
                tempLOPRTManagerDB.setOldPrtSeq(mOldPrtSeq);
                LOPRTManagerSet tempLOPRTManagerSet = tempLOPRTManagerDB.query();
                if (tempLOPRTManagerSet != null &&
                        tempLOPRTManagerSet.size() > 0)
                {
                    for (int i = 1; i <= tempLOPRTManagerSet.size(); i++)
                    {
                        mLOPRTManagerSet.add(tempLOPRTManagerSet.get(i));
                    }
                }
            }
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
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
            tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得业务数据
        if (mTransferData == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mContNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中ContNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的任务ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得业务处理数据
        mPrtSeq = (String) mTransferData.getValueByName("CertifyNo");
        if (mPrtSeq == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输单证号出错!";
            this.mErrors.addOneError(tError);
            return false;
        }

//        //获得业务处理数据
//        mLCRReportSchema = (LCRReportSchema) mTransferData.getValueByName(
//                "LCRReportSchema");
//        if (mLCRReportSchema == null)
//        {
//            // @@错误处理
//            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
//            CError tError = new CError();
//            tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
//            tError.functionName = "getInputData";
//            tError.errorMessage = "前台传输业务数据中LPRReportSchema失败!";
//            this.mErrors.addOneError(tError);
//            return false;
//        }
//
//        mReplyContente = mLCRReportSchema.getReplyContente();
//        mPrtSeq = mLCRReportSchema.getPrtSeq();
//        if (mPrtSeq == null || mPrtSeq.trim().equals(""))
//        {
//            // @@错误处理
//            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
//            CError tError = new CError();
//            tError.moduleName = "PrintTakeBackRReportAfterInitService";
//            tError.functionName = "getInputData";
//            tError.errorMessage = "前台传输业务数据中LPRReportSchema中的PrtSeq失败!";
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
        // 核保生调信息
        if (!prepareRReport())
        {
            return false;
        }

        // 打印队列
        if (!preparePrint())
        {
            return false;
        }

        return true;
    }

    /**
     * 准备核保资料信息
     * 输出：如果发生错误则返回false,否则返回true
     */
    private boolean prepareRReport()
    {
//        ////准备核保主表信息
//        mLCCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
//        mLCCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());
//        mLCCUWMasterSchema.setReportFlag("3"); //回收生调通知书

        //准备生调表信息
        LCRReportDB tLCRReportDB = new LCRReportDB();
        LCRReportSet tLCRReportSet = new LCRReportSet();
        tLCRReportDB.setContNo(mContNo);
        tLCRReportDB.setPrtSeq(mPrtSeq);
        tLCRReportSet = tLCRReportDB.query();
        if (tLCRReportSet == null || tLCRReportSet.size() != 1)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PrintTakeBackRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "查询续保生调表LPRReportSchema失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCRReportSchema = tLCRReportSet.get(1);
        mLCRReportSchema.setReplyFlag("0");  //修改
        if (mReplyContente != null)
        {
            mLCRReportSchema.setReplyContente(mReplyContente);
        }
        mLCRReportSchema.setReplyOperator(mOperater);
        mLCRReportSchema.setReplyDate(PubFun.getCurrentDate());
        mLCRReportSchema.setReplyTime(PubFun.getCurrentTime());
        mLCRReportSchema.setModifyDate(PubFun.getCurrentDate());
        mLCRReportSchema.setModifyTime(PubFun.getCurrentTime());
        return true;
    }


    /**
     * 准备打印信息表
     * @return
     */
    private boolean preparePrint()
    {
        //准备打印管理表数据
        for (int i = 1; i <= mLOPRTManagerSet.size(); i++)
        {
            mLOPRTManagerSet.get(i).setStateFlag("2");
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
}

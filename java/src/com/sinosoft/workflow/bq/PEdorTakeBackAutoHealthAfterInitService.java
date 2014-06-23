package com.sinosoft.workflow.bq;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.LOPRTManagerSet;
import com.sinosoft.lis.vschema.LZSysCertifySet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: </p>
 * <p>Description:工作流节点任务:保全人工核保体检通知书回收服务类 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PEdorTakeBackAutoHealthAfterInitService implements AfterInitService
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
    private String mManageCom;
    private String mOperate;
    /** 业务数据操作字符串 */
    private String mEdorNo;
    private String mPolNo;
    private String mMissionID;
    private String mCertifyNo;
    private String mCertifyCode;
    private boolean mPatchFlag;
    private String mTakeBackOperator;
    private String mTakeBackMakeDate;
    private String mOldPrtSeq; //如果该单证是补打单证,则同时将遗失原单证也回收.反之如果回收原单证,但其已补发过,则同时也要把补发的单证回收掉
//    private boolean mAutoSysCertSendOutFlag = true;
//    private Reflections mReflections = new Reflections();

    /**执行保全工作流特约活动表任务0000000011*/
    /**保单表*/
    private LCPolSchema mLCPolSchema = new LCPolSchema();
    /** 保全核保主表 */
    private LPUWMasterMainSchema mLPUWMasterMainSchema = new LPUWMasterMainSchema();
    /** 打印管理表 */
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();
    private LOPRTManagerSet mLOPRTManagerSet = new LOPRTManagerSet(); //如果该单证是补打单证,则同时将遗失原单证也回收.反之如果回收原单证,但其已补发过,则同时也要把补发的单证回收掉
    /** 体检主表*/
    private LPPENoticeSchema mLPPENoticeSchema = new LPPENoticeSchema();
    /** 单证发放表*/
//    private LZSysCertifySchema mLZSysCertifySchema = new LZSysCertifySchema();
    private LZSysCertifySet mLZSysCertifySet = new LZSysCertifySet();
    public PEdorTakeBackAutoHealthAfterInitService()
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

        //添加保全批单核保主表数据
        if (mLPUWMasterMainSchema != null)
        {
            map.put(mLPUWMasterMainSchema, "UPDATE");
        }

        //添加体检表数据
        if (mLPPENoticeSchema != null)
        {
            map.put(mLPPENoticeSchema, "UPDATE");
        }

        //添加保全体检通知书自动发放表数据
        if (mLZSysCertifySet != null && mLZSysCertifySet.size() > 0)
        {
            map.put(mLZSysCertifySet, "UPDATE");
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
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setPolNo(mPolNo);
        if (!tLCPolDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mPolNo + "信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCPolSchema.setSchema(tLCPolDB);

        //校验保全批单核保主表
        //校验保单信息
        LPUWMasterMainDB tLPUWMasterMainDB = new LPUWMasterMainDB();
        tLPUWMasterMainDB.setEdorNo(mEdorNo);
//   tLPUWMasterMainDB.setPolNo(mPolNo) ;
        if (!tLPUWMasterMainDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mPolNo + "保全批单核保主表信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLPUWMasterMainSchema.setSchema(tLPUWMasterMainDB);

        // 处于未打印状态的核保通知书在打印队列中只能有一个
        // 条件：同一个单据类型，同一个其它号码，同一个其它号码类型
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

        tLOPRTManagerDB.setCode("23"); //
        tLOPRTManagerDB.setPrtSeq(mCertifyNo);
        tLOPRTManagerDB.setOtherNo(mPolNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_INDPOL); //保单号
        tLOPRTManagerDB.setStandbyFlag2(mEdorNo);
        tLOPRTManagerDB.setStateFlag("1");

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "查询打印管理表信息出错!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLOPRTManagerSet.size() != 1)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "在打印队列中没有处于已打印待回收状态的体检通知书!";
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
            String tStr = "select * from LOPRTManager where (PrtSeq = '" + mOldPrtSeq + "'"
                    + "or OldPrtSeq = '" + mOldPrtSeq + "')";
            LOPRTManagerSet tempLOPRTManagerSet = tempLOPRTManagerDB.executeQuery(tStr);
            if (tempLOPRTManagerSet.size() == 1)
            {
                // @@错误处理
                CError tError = new CError();
                tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
                tError.functionName = "preparePrint";
                tError.errorMessage = "查询在打印队列中没有该补打体检通知书的原通知书记录信息出错!";
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
                if (tempLOPRTManagerSet != null && tempLOPRTManagerSet.size() > 0)
                {
                    for (int i = 1; i <= tempLOPRTManagerSet.size(); i++)
                    {
                        mLOPRTManagerSet.add(tempLOPRTManagerSet.get(i));
                    }
                }
            }
        }

        //查询系统单证回收队列表
        for (int i = 1; i <= mLOPRTManagerSet.size(); i++)
        {
            if (mLOPRTManagerSet.get(i).getStateFlag() != null
                    && mLOPRTManagerSet.get(i).getStateFlag().trim().equals("1"))
            {
                LZSysCertifyDB tLZSysCertifyDB = new LZSysCertifyDB();
                LZSysCertifySet tLZSysCertifySet = new LZSysCertifySet();
                tLZSysCertifyDB.setCertifyCode("7773"); //体检通知书标识
                tLZSysCertifyDB.setCertifyNo(mLOPRTManagerSet.get(i).getPrtSeq());
                tLZSysCertifySet = tLZSysCertifyDB.query();
                if (tLZSysCertifySet == null || tLZSysCertifySet.size() != 1)
                {
                    // @@错误处理
                    CError tError = new CError();
                    tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
                    tError.functionName = "preparePrint";
                    tError.errorMessage = "回收体检通知书时,LZSysCertifySchema表信息查询失败!";
                    this.mErrors.addOneError(tError);
                    return false;
                }
                mLZSysCertifySet.add(tLZSysCertifySet.get(1));
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
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
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
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
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
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mOperate = cOperate;

        //获得业务数据
        if (mTransferData == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mEdorNo = (String) mTransferData.getValueByName("EdorNo");
        if (mEdorNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中EdorNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mPolNo = (String) mTransferData.getValueByName("PolNo");
        if (mPolNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中PolNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的任务ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得业务处理数据
        mCertifyNo = (String) mTransferData.getValueByName("CertifyNo");
        if (mCertifyNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得业务处理数据
        mCertifyCode = (String) mTransferData.getValueByName("CertifyCode");
        if (mCertifyCode == null || mCertifyCode.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得业务处理数据
        LZSysCertifySchema tLZSysCertifySchema = new LZSysCertifySchema();
        tLZSysCertifySchema = (LZSysCertifySchema) mTransferData.getValueByName(
                "LZSysCertifySchema");
        if (tLZSysCertifySchema == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中LZSysCertifySchema失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mTakeBackOperator = tLZSysCertifySchema.getTakeBackOperator();
        if (mTakeBackOperator == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中TakeBackOperator失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mTakeBackMakeDate = tLZSysCertifySchema.getTakeBackMakeDate();
        if (mTakeBackMakeDate == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中mTakeBackMakeDate失败!";
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
        // 核保生调信息
        if (!prepareAutoHealth())
        {
            return false;
        }

        //打印队列
        if (!preparePrint())
        {
            return false;
        }

        //发放系统单证打印队列
        if (!prepareAutoSysCertSendOut())
        {
            return false;
        }

        return true;

    }

    /**
     * 准备核保资料信息
     * 输出：如果发生错误则返回false,否则返回true
     */
    private boolean prepareAutoHealth()
    {

        ////准备核保主表信息
        mLPUWMasterMainSchema.setModifyDate(PubFun.getCurrentDate());
        mLPUWMasterMainSchema.setModifyTime(PubFun.getCurrentTime());
        mLPUWMasterMainSchema.setHealthFlag("3"); //发送核保通知书

        //更新体检通知书
        LPPENoticeDB tLPPENoticeDB = new LPPENoticeDB();
        tLPPENoticeDB.setEdorNo(mEdorNo);
//	tLPPENoticeDB.setPolNo(mPolNo);
        tLPPENoticeDB.setPrintFlag("1");
//	tLPPENoticeDB.setInsuredNo(mLOPRTManagerSchema.getStandbyFlag1());//备用字段1存放客户号
        if (!tLPPENoticeDB.getInfo())
        {
            mErrors.copyAllErrors(tLPPENoticeDB.mErrors);
            return false;
        }
        mLPPENoticeSchema = tLPPENoticeDB.getSchema();
        mLPPENoticeSchema.setPrintFlag("2");
        mLPPENoticeSchema.setModifyDate(PubFun.getCurrentDate());
        mLPPENoticeSchema.setModifyTime(PubFun.getCurrentTime());

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
     * 准备核保资料信息
     * 输出：如果发生错误则返回false,否则返回true
     */
    private boolean prepareAutoSysCertSendOut()
    {
        //准备单证回收管理表数据
        for (int i = 1; i <= mLZSysCertifySet.size(); i++)
        {
            mLZSysCertifySet.get(i).setTakeBackMakeDate(PubFun.getCurrentDate());
            mLZSysCertifySet.get(i).setTakeBackMakeTime(PubFun.getCurrentTime());
            mLZSysCertifySet.get(i).setModifyDate(PubFun.getCurrentDate());
            mLZSysCertifySet.get(i).setModifyTime(PubFun.getCurrentTime());
            mLZSysCertifySet.get(i).setTakeBackOperator(mTakeBackOperator);
            mLZSysCertifySet.get(i).setStateFlag("1");
        }
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

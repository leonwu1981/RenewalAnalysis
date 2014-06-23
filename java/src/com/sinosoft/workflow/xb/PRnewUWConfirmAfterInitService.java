/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.xb;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.LCRnewStateLogSet;
import com.sinosoft.lis.vschema.LDUserSet;
import com.sinosoft.lis.vschema.LOPRTManagerSet;
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

public class PRnewUWConfirmAfterInitService implements AfterInitService
{

    /** 全局数据 */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往后面传输数据的容器 */
    private VData mInputData;
//    private VData pInputData;
    /** 往界面传输数据的容器 */
    private VData mResult = new VData();
    //**个人集体入口标志*/
//    private String mContType;
    //** 数据操作字符串 */
    private String mManageCom;
    private String mOperate;
//    private String mLJSPayOperateFlag;
    private String mOperator;
    private String mMissionID;
    /** 业务数据操作字符串 */
    private String mPrtNo;
    private String mPolNo;
    private String mInsuredNo;
    private String mUWState;
    private String mUWIdea;
    private String mPostponeDay;
    private String mAppGrade; //上保核保师级别
    private String mAppUser; //上报核保师编码
    private String mAgentGroup;
    private String mAgentCode;
//    private Reflections mReflections = new Reflections();
    private MMap mMMap = new MMap();
    /**执行保全核保工作流核保确认活动表任务0000000010*/
    /**保单表*/
    private LCPolSchema mLCPolSchema = new LCPolSchema();
    /**保单批改核保主表备份表  */
    private LCUWSubSchema mLCUWSubSchema = new LCUWSubSchema();
//    private LCUWSubSet mLCUWSubSet = new LCUWSubSet();
    /**保单批单核保主表 */
    private LCUWMasterSchema mLCUWMasterSchema = new LCUWMasterSchema();
    private LCRnewStateLogSchema mLCRnewStateLogSchema = new LCRnewStateLogSchema();

//    private String mFlag;

    //private  LJSPaySet mLJSPaySet  = new LJSPaySet();
    /** 打印管理表 */
    private LOPRTManagerSet mLOPRTManagerSet = new LOPRTManagerSet();
    private String CurrentDate = PubFun.getCurrentDate();
//    private String CurrentTime = PubFun.getCurrentTime();

    public PRnewUWConfirmAfterInitService()
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

        //将操作数据拷贝到本类中
        mInputData = (VData) cInputData.clone();

        //得到外部传入的数据,将数据备份到本类中
        if (!getInputData(cInputData, cOperate))
        {
            return false;
        }

        //数据校验操作（checkdata)
        if (!checkData())
        {
            return false;
        }

        //进行业务处理
        if (!dealData())
        {
            return false;
        }

        //数据准备操作（preparedata())
        if (!prepareOutputData())
        {
            return false;
        }

//	if (mOperate.equals("UPDATE||MANUUWENDORSE"))
//	{
//	  PEdorManuUWBLS tPEdorManuUWBLS = new PEdorManuUWBLS();
//	  if (!tPEdorManuUWBLS.submitData(mInputData,mOperate))
//		return false;
//	  if (mContType.equals("I"))
//	  {
//		System.out.println("------mConttype"+mContType);
//		this.checkFinaProduce();
//		if (this.getUWFlag().equals("9"))
//		{
//		  EdorFinaProduce tEdorFinaProduce = new EdorFinaProduce(mLPEdorMainSchema.getEdorNo());
//		  tEdorFinaProduce.setLimit(PubFun.getNoLimit(mLPEdorMainSchema.getManageCom()));
//		  tEdorFinaProduce.setOperator(mGlobalInput.Operator);
//		  if (!tEdorFinaProduce.submitData())
//			return false;
//		  //生成打印数据.
//		  VData tVData = new VData();
//		  tVData.add(mGlobalInput);
//		  tVData.add(mLPEdorMainSchema);
//		  PrtEndorsementBL tPrtEndorsementBL = new PrtEndorsementBL(mLPEdorMainSchema.getEdorNo(),mLPEdorMainSchema.getPolNo());
//		  if (!tPrtEndorsementBL.submitData(tVData,""))
//			return false;
//		}
//	  }
//	}
//	System.out.println("---updateDataPEdorManuUWBL---");

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
        if (mMMap != null && mMMap.keySet().size() > 0)
        {
            map = mMMap;
        }

        //添加批单核保主表信息
        map.put(mLCUWMasterSchema, "UPDATE");

        //添加投保单表信息
        map.put(mLCPolSchema, "UPDATE");

        //添加批单下项目核保主表备份表信息
        if (mLCUWSubSchema != null)
        {
            map.put(mLCUWSubSchema, "INSERT");
        }

        //添加批单下项目核保主表备份表信息
        if (mLCRnewStateLogSchema != null && !mLCRnewStateLogSchema.getProposalNo().equals(""))
        {
            map.put(mLCRnewStateLogSchema, "UPDATE");
        }

        //添加批单主表信息
        if (mLOPRTManagerSet != null && mLOPRTManagerSet.size() > 0)
        {
            map.put(mLOPRTManagerSet, "INSERT");
        }

        mResult.add(map);
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
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得操作员编码
        mOperator = mGlobalInput.Operator;
        if (mOperator == null || mOperator.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWConfirmAfterInitService";
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
            tError.moduleName = "PEdorUWConfirmAfterInitService";
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
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据失败!";
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
            tError.moduleName = "PRnewUWRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mPolNo = (String) mTransferData.getValueByName("PolNo");
        if (mPolNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中PolNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得业务保全核保确认数据
        //mContType = (String)cInputData.get(0);
        LCUWMasterSchema tLCUWMasterSchema = new LCUWMasterSchema();
        tLCUWMasterSchema = (LCUWMasterSchema) mTransferData.getValueByName("LCUWMasterSchema");
        if (tLCUWMasterSchema == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输获得业务批单核保主表数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLCUWMasterSchema.getPassFlag() == null
                || tLCUWMasterSchema.getPassFlag().trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输获得业务批单核保主表中的核保结论数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mUWState = tLCUWMasterSchema.getPassFlag();
        mUWIdea = tLCUWMasterSchema.getUWIdea();
        mPostponeDay = tLCUWMasterSchema.getPostponeDay();
        mAppUser = (String) mTransferData.getValueByName("AppUser");
        if (mUWState.trim().equals("6") && (mAppUser == null || mAppUser.trim().equals("")))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中上报核保师编码失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (mUWState.trim().equals("2") && (mPostponeDay == null || mPostponeDay.trim().equals("")))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中的延期日期数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;

    }


    /**
     * 校验传入的数据的合法性
     * 输出：如果发生错误则返回false,否则返回true
     */
    private boolean checkData()
    {

        //校验保单信息
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setPolNo(mPolNo);
        if (!tLCPolDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mPolNo + "信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCPolSchema.setSchema(tLCPolDB);

        mAgentCode = mLCPolSchema.getAgentCode();
        mAgentGroup = mLCPolSchema.getAgentGroup();
        mPrtNo = mLCPolSchema.getPrtNo();
        //校验保单被保人编码信息
        mInsuredNo = mLCPolSchema.getInsuredNo();
        if (mInsuredNo == null)
        {
            CError tError = new CError();
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mPolNo + "的被保人编码信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //校验保全批单核保主表
        //校验保单信息
        LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
        tLCUWMasterDB.setProposalNo(mPolNo);
        if (!tLCUWMasterDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mPolNo + "保全批单核保主表信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCUWMasterSchema.setSchema(tLCUWMasterDB);

        if (mUWState.trim().equals("6"))
        {
            //保全核保结论为上报核保,获得上保核保师级别
            LDUserDB tLDUserDB = new LDUserDB();
            LDUserSet tLDUserSet = new LDUserSet();
            LDUserSchema tLDUserSchema = new LDUserSchema();
            tLDUserDB.setUserCode(mAppUser);
            tLDUserSet = tLDUserDB.query();
            if (tLDUserSet == null || tLDUserSet.size() != 1)
            {
                CError tError = new CError();
                tError.moduleName = "PEdorUWConfirmAfterInitService";
                tError.functionName = "prepareData";
                tError.errorMessage = "上报核保师" + mAppGrade + "的信息查询失败!";
                this.mErrors.addOneError(tError);
                return false;
            }
            tLDUserSchema = tLDUserSet.get(1);
            mAppGrade = tLDUserSchema.getEdorPopedom();
        }

        LCRnewStateLogDB tLCRnewStateLogDB = new LCRnewStateLogDB();
        tLCRnewStateLogDB.setProposalNo(mPolNo);
        LCRnewStateLogSet tLCRnewStateLogSet = tLCRnewStateLogDB.query();
        if (tLCRnewStateLogSet == null || tLCRnewStateLogSet.size() != 1)
        {
            CError tError = new CError();
            tError.moduleName = "PRnewManualDunBL";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mPrtNo + "续保状态表信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCRnewStateLogSchema = tLCRnewStateLogSet.get(1);

        return true;
    }


    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     */
    private boolean dealData()
    {
        // 核保结论信息
        if (!prepareData())
        {
            return false;
        }
        // 核保结论为１和２时要打印出相应的通知书信息
        if (!preparePrint())
        {
            return false;
        }
        return true;

    }


    /**
     * 准备需要保存的数据
     */
    private boolean prepareData()
    {

        //准备续保核保主表信息
        if (mUWIdea != null && !mUWIdea.trim().equals(""))
        {
            mLCUWMasterSchema.setUWIdea(mUWIdea);
        }
        if (mUWState.trim().equals("6"))
        {
            mLCUWMasterSchema.setAppGrade(mAppGrade);
        }
        if (mUWState.trim().equals("2"))
        {
            mLCUWMasterSchema.setPostponeDay(mPostponeDay);
        }
        mLCUWMasterSchema.setUWNo(mLCUWMasterSchema.getUWNo() + 1);
        mLCUWMasterSchema.setPassFlag(mUWState);
        mLCUWMasterSchema.setState(mUWState);
        mLCUWMasterSchema.setOperator(mOperator);
        mLCUWMasterSchema.setManageCom(mManageCom);
        mLCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
        mLCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());

        //准备续保核保主表轨迹信息
        mLCUWSubSchema = new LCUWSubSchema();
        //mReflections.transFields(mLCUWSubSchema,mLCUWMasterSchema );
        mLCUWSubSchema.setUWNo(mLCUWMasterSchema.getUWNo()); //第几次核保
        mLCUWSubSchema.setProposalNo(mLCUWMasterSchema.getProposalNo());
        mLCUWSubSchema.setPolNo(mLCUWMasterSchema.getPolNo());
//	 mLCUWSubSchema.setUWFlag(mLCUWMasterSchema.getState()); //核保意见
        mLCUWSubSchema.setUWGrade(mLCUWMasterSchema.getUWGrade()); //核保级别
        mLCUWSubSchema.setAppGrade(mLCUWMasterSchema.getAppGrade()); //申请级别
        mLCUWSubSchema.setAutoUWFlag(mLCUWMasterSchema.getAutoUWFlag());
        mLCUWSubSchema.setState(mLCUWMasterSchema.getState());
        mLCUWSubSchema.setUWIdea(mLCUWMasterSchema.getUWIdea());
        mLCUWSubSchema.setOperator(mLCUWMasterSchema.getOperator()); //操作员
        mLCUWSubSchema.setManageCom(mLCUWMasterSchema.getManageCom());
        if (mLCUWMasterSchema.getSpecReason() != null)
        {
            mLCUWSubSchema.setSpecReason(mLCUWMasterSchema.getSpecReason()); //特约原因
        }
        if (mLCUWMasterSchema.getAddPremReason() != null)
        {
            mLCUWSubSchema.setAddPremReason(mLCUWMasterSchema.getAddPremReason());
        }
        if (mLCUWMasterSchema.getChangePolReason() != null)
        {
            mLCUWSubSchema.setChangePolReason(mLCUWMasterSchema.getChangePolReason());
        }
        if (mLCUWMasterSchema.getUpReportContent() != null)
        {
            mLCUWSubSchema.setUpReportContent(mLCUWMasterSchema.getUpReportContent());
        }
        mLCUWSubSchema.setMakeDate(PubFun.getCurrentDate());
        mLCUWSubSchema.setMakeTime(PubFun.getCurrentTime());
        mLCUWSubSchema.setModifyDate(PubFun.getCurrentDate());
        mLCUWSubSchema.setModifyTime(PubFun.getCurrentTime());

        //准备保单信息
        if (mUWState != null && !mUWState.trim().equals(""))
        {
            mLCPolSchema.setUWFlag(mUWState);
            mLCPolSchema.setUWCode(mOperator);
            mLCPolSchema.setUWDate(PubFun.getCurrentDate());
        }

        //准备续保状态信息
        if (mUWState.trim().equals("9") || mUWState.trim().equals("4"))
        {
            mLCRnewStateLogSchema.setModifyDate(CurrentDate);
            mLCRnewStateLogSchema.setState("3");
        }

        return true;

    }

    /**
     * 打印信息表
     * @return
     */
    private boolean preparePrint()
    {

        //拒保通知书
        if (mUWState.trim().equals("1"))
        {
            //通知书号
            String tLimit = PubFun.getNoLimit(mManageCom);
            String mGetNoticeNo = PubFun1.CreateMaxNo("PRTSEQNO", tLimit); //产生即付通知书号
            LOPRTManagerSchema tLOPRTManagerSchema;
            tLOPRTManagerSchema = new LOPRTManagerSchema();
            tLOPRTManagerSchema.setPrtSeq(mGetNoticeNo);
            tLOPRTManagerSchema.setReqCom(mGlobalInput.ComCode);
            tLOPRTManagerSchema.setReqOperator(mGlobalInput.Operator);
            tLOPRTManagerSchema.setMakeDate(PubFun.getCurrentDate());
            tLOPRTManagerSchema.setMakeTime(PubFun.getCurrentTime());
            tLOPRTManagerSchema.setPrtSeq(mGetNoticeNo);
            tLOPRTManagerSchema.setManageCom(mLCPolSchema.getManageCom());
            tLOPRTManagerSchema.setPrtType(PrintManagerBL.PT_FRONT);
            tLOPRTManagerSchema.setAgentCode(mLCPolSchema.getAgentCode());
            tLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_INDPOL);

            tLOPRTManagerSchema.setCode(PrintManagerBL.CODE_PRnewDECLINE);
            tLOPRTManagerSchema.setOtherNo(mPolNo);
            tLOPRTManagerSchema.setStandbyFlag1(mLCPolSchema.getPrtNo());
            tLOPRTManagerSchema.setStandbyFlag3(mMissionID);
            tLOPRTManagerSchema.setStandbyFlag2(mLCPolSchema.getPrtNo());
            tLOPRTManagerSchema.setStateFlag("0");
            mLOPRTManagerSet.add(tLOPRTManagerSchema);
        }

        //延期通知书
        if (mUWState.trim().equals("2"))
        {
            String tLimit = PubFun.getNoLimit(mManageCom);
            String mGetNoticeNo = PubFun1.CreateMaxNo("PRTSEQNO", tLimit); //产生即付通知书号
            LOPRTManagerSchema tLOPRTManagerSchema;
            tLOPRTManagerSchema = new LOPRTManagerSchema();
            tLOPRTManagerSchema.setPrtSeq(mGetNoticeNo);
            tLOPRTManagerSchema.setReqCom(mGlobalInput.ComCode);
            tLOPRTManagerSchema.setReqOperator(mGlobalInput.Operator);
            tLOPRTManagerSchema.setMakeDate(PubFun.getCurrentDate());
            tLOPRTManagerSchema.setMakeTime(PubFun.getCurrentTime());
            tLOPRTManagerSchema.setManageCom(mLCPolSchema.getManageCom());
            tLOPRTManagerSchema.setPrtType(PrintManagerBL.PT_FRONT);
            tLOPRTManagerSchema.setAgentCode(mLCPolSchema.getAgentCode());
            tLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_INDPOL);

            tLOPRTManagerSchema.setCode(PrintManagerBL.CODE_PRnewDEFER);
            tLOPRTManagerSchema.setOtherNo(mPolNo);
            tLOPRTManagerSchema.setStandbyFlag1(mLCPolSchema.getPrtNo());
            tLOPRTManagerSchema.setStandbyFlag3(mMissionID);
            tLOPRTManagerSchema.setStandbyFlag2(mLCPolSchema.getPrtNo());
            tLOPRTManagerSchema.setStateFlag("0");
            mLOPRTManagerSet.add(tLOPRTManagerSchema);
        }

        return true;
    }

//    private void checkFinaProduce()
//    {
//        String sql = "select * from LPEdorMain where edorno='" + mLPEdorMainSchema.getEdorNo()
//                + "' and uwstate in ('0','5','6')";
//        LPEdorMainDB tLPEdorMainDB = new LPEdorMainDB();
//        LPEdorMainSet tLPEdorMainSet = new LPEdorMainSet();
//        tLPEdorMainSet = tLPEdorMainDB.executeQuery(sql);
//
//        if (tLPEdorMainSet.size() > 0)
//        {
//            this.setUWFlag("5");
//        }
//        else
//        {
//            this.setUWFlag("9");
//        }
//    }

    public static void main(String[] args)
    {
//        VData tInputData = new VData();
//        GlobalInput tGlobalInput = new GlobalInput();
//        LPEdorMainSchema tLPEdorMainSchema = new LPEdorMainSchema();
//        LPUWMasterSchema tLPUWMasterSchema = new LPUWMasterSchema();
//        tGlobalInput.ManageCom = "001";
//        tGlobalInput.Operator = "Admin";
//        tLPEdorMainSchema.setEdorNo("00000120020420000067");
//        tLPUWMasterSchema.setUWIdea("tjj temp test");
//        tLPUWMasterSchema.setPassFlag("9");
//        tInputData.addElement(tLPEdorMainSchema);
//        tInputData.addElement(tLPUWMasterSchema);
//        tInputData.addElement(tGlobalInput);
    }

}

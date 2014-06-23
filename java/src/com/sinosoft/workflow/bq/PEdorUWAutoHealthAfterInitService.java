package com.sinosoft.workflow.bq;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author lh
 * @version 1.0
 */

public class PEdorUWAutoHealthAfterInitService implements AfterInitService
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
    private TransferData mReturnTransferData = new TransferData();
    /**工作流引擎 */
    ActivityOperator mActivityOperator = new ActivityOperator();
    /** 数据操作字符串 */
    private String mOperater;
    private String mManageCom;
    private String mOperate;
    /** 业务数据操作字符串 */
    private String mEdorNo;
    private String mContNo;
    private String mInsuredNo;
    private String mMissionID;
    private String mPrtSeq;

    /**执行保全工作流发体检通知书活动表任务0000000001*/
    /**保单表*/
    private LCContSchema mLCContSchema = new LCContSchema();
    /** 保全核保主表 */
    private LPUWMasterMainSchema mLPUWMasterMainSchema = new
            LPUWMasterMainSchema();
    /** 体检资料主表 */
    private LPPENoticeSet mLPPENoticeSet = new LPPENoticeSet();
    private LPPENoticeSchema mLPPENoticeSchema = new LPPENoticeSchema();
    private LPPENoticeSet mOldLPPENoticeSet = new LPPENoticeSet();
    private LPPENoticeItemSet mOldLPPENoticeItemSet = new LPPENoticeItemSet();
    /** 体检资料项目表 */
    private LPPENoticeItemSet mLPPENoticeItemSet = new LPPENoticeItemSet();
    /** 打印管理表 */
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();

    public PEdorUWAutoHealthAfterInitService()
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
        System.out.println("Start PEdorUWAutoHealthAfterInitService Submit...");
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

        //mResult.clear();
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
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单合同" + mContNo + "信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);

        //校验是否有未打印的体检通知书
        LPPENoticeDB tLPPENoticeDB = new LPPENoticeDB();
        tLPPENoticeDB.setEdorNo(mEdorNo);
        tLPPENoticeDB.setContNo(mContNo);
        tLPPENoticeDB.setCustomerNo(mInsuredNo);
        LPPENoticeSet tLPPENoticeSet = new LPPENoticeSet();
        tLPPENoticeSet = tLPPENoticeDB.query();

        if (tLPPENoticeSet.size() > 0)
        {

            if (tLPPENoticeSet.get(1).getPrintFlag().equals("0"))
            {
                CError tError = new CError();
                tError.moduleName = "PEdorUWAutoHealthAfterInitService";
                tError.functionName = "checkData";
                tError.errorMessage = "体检通知已经录入,但未打印，不能录入新体检资料!";
                this.mErrors.addOneError(tError);
                return false;
            }
        }

        //校验保全批单核保主表
        //校验保单信息
        LPUWMasterMainDB tLPUWMasterMainDB = new LPUWMasterMainDB();
        tLPUWMasterMainDB.setEdorNo(mEdorNo);
        tLPUWMasterMainDB.setContNo(mContNo);
        if (!tLPUWMasterMainDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorUWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单合同" + mContNo + "保全批单核保主表信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLPUWMasterMainSchema.setSchema(tLPUWMasterMainDB);

        return true;
    }

    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     */
    private boolean dealData()
    {
        String strNoLimit = PubFun.getNoLimit(mGlobalInput.ComCode);
        mPrtSeq = PubFun1.CreateMaxNo("PRTSEQNO", strNoLimit);

        // 体检信息
        if (!prepareHealth())
        {
            return false;
        }

        //打印队列
        if (!preparePrint())
        {
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
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
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
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
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
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
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
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
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
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中EdorNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mContNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中PolNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mInsuredNo = (String) mTransferData.getValueByName("InsuredNo");
        if (mInsuredNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中InsuredNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得业务体检通知数据
        mLPPENoticeSchema = (LPPENoticeSchema) mTransferData.getValueByName(
                "LPPENoticeSchema");
        if (mLPPENoticeSchema == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输获得业务体检通知数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //获得业务体检通知对应的体检项目
        mLPPENoticeItemSet = (LPPENoticeItemSet) mTransferData.getValueByName(
                "LPPENoticeItemSet");
        if (mLPPENoticeItemSet == null || mLPPENoticeItemSet.size() == 0)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输获得业务体检通知对应的体检项目数据失败!";
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
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 准备体检资料信息
     * 输出：如果发生错误则返回false,否则返回true
     */
    private boolean prepareHealth()
    {

        //取代理人姓名
        LAAgentDB tLAAgentDB = new LAAgentDB();
        tLAAgentDB.setAgentCode(mLCContSchema.getAgentCode());
        if (!tLAAgentDB.getInfo())
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "prepareHealth";
            tError.errorMessage = "取代理人姓名失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

//	//删除旧的体检信息
//	LPPENoticeDB tLPPENoticeDB = new LPPENoticeDB();
//	tLPPENoticeDB.setEdorNo(mLPPENoticeSchema.getEdorNo()) ;
//	tLPPENoticeDB.setContNo(mLPPENoticeSchema.getPolNo()) ;
//	tLPPENoticeDB.setInsuredNo(mLPPENoticeSchema.getInsuredNo()) ;
//    mOldLPPENoticeSet = tLPPENoticeDB.query() ;
//	if(mOldLPPENoticeSet == null)
//	{
//	  // @@错误处理
//	  CError tError = new CError();
//	  tError.moduleName = "PEdorUWAutoHealthAfterInitService";
//	  tError.functionName = "prepareHealth";
//	  tError.errorMessage = "查询旧的体检信息出错!";
//	  this.mErrors .addOneError(tError) ;
//	  return false;
//	}
//
//	//准备检资料项目信息
//	for (int i = 1;i <= mLPPENoticeItemSet.size();i++)
//	{
//	  //mLPPENoticeItemSet.get(i).setEdorNo( mEdorNo );
//	  //mLPPENoticeItemSet.get(i).setPolNo( mContNo );
//	  //mLPPENoticeItemSet.get(i).setPEItemCode(); //核保规则编码
//	  //mLPPENoticeItemSet.get(i).setPEItemName(); //核保出错信息
//	  //mLPPENoticeItemSet.get(i).setInsuredNo(mInsuredNo);
//	  mLPPENoticeItemSet.get(i).setModifyDate(PubFun.getCurrentDate()); //当前值
//	  mLPPENoticeItemSet.get(i).setModifyTime(PubFun.getCurrentTime());
//	  //mLPPENoticeItemSet.get(i).setFreePE() ;
//	}
//
//
//	//删除旧的体检信息
//
//	String tSQL = "select * from LPPENoticeItem where edorno = '"+mEdorNo +"'"
//	              +" and ContNo = '" + mContNo +"'"
//	              +" and insuredno = '" + mInsuredNo + "'";
//	LPPENoticeItemDB tLPPENoticeItemDB = new LPPENoticeItemDB();
//	mOldLPPENoticeItemSet = tLPPENoticeItemDB.executeQuery(tSQL);
//	if(mOldLPPENoticeItemSet == null)
//	{
//	  // @@错误处理
//	  CError tError = new CError();
//	  tError.moduleName = "PEdorUWAutoHealthAfterInitService";
//	  tError.functionName = "prepareHealth";
//	  tError.errorMessage = "查询旧的体检项目信息出错!";
//	  this.mErrors .addOneError(tError) ;
//	  return false;
//	}

        //取体检人姓名
        LDPersonDB tLDPersonDB = new LDPersonDB();
        tLDPersonDB.setCustomerNo(mInsuredNo);
        if (!tLDPersonDB.getInfo())
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "UWAutoHealthAfterInitService";
            tError.functionName = "prepareHealth";
            tError.errorMessage = "取被体检客户姓名失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLPPENoticeSchema.setGrpContNo(mLCContSchema.getGrpContNo());
        mLPPENoticeSchema.setProposalContNo(mLCContSchema.getProposalContNo());
        mLPPENoticeSchema.setName(tLDPersonDB.getName());
        mLPPENoticeSchema.setPrtSeq(mPrtSeq);
        //mLCPENoticeSchema.setPEDate(mLCPENoticeSchema.getPEDate());
        //mLCPENoticeSchema.setPEAddress(mLCPENoticeSchema.getPEAddress());
        mLPPENoticeSchema.setPrintFlag("0");
        mLPPENoticeSchema.setAppName(mLCContSchema.getAppntName());
        mLPPENoticeSchema.setAgentCode(mLCContSchema.getAgentCode());
        mLPPENoticeSchema.setAgentName(tLAAgentDB.getName());
        mLPPENoticeSchema.setManageCom(mLCContSchema.getManageCom());
        //mLCPENoticeSchema.setPEBeforeCond(mLCPENoticeSchema.getPEBeforeCond());
        mLPPENoticeSchema.setOperator(mOperater); //操作员
        mLPPENoticeSchema.setMakeDate(PubFun.getCurrentDate());
        mLPPENoticeSchema.setMakeTime(PubFun.getCurrentTime());
        mLPPENoticeSchema.setModifyDate(PubFun.getCurrentDate());
        mLPPENoticeSchema.setModifyTime(PubFun.getCurrentTime());
        //mLCPENoticeSchema.setRemark(mLCPENoticeSchema.getRemark());

        //准备检资料项目信息
        for (int i = 1; i <= mLPPENoticeItemSet.size(); i++)
        {
            mLPPENoticeItemSet.get(i).setGrpContNo(mLCContSchema.getGrpContNo());
            mLPPENoticeItemSet.get(i).setProposalContNo(mLCContSchema.
                    getProposalContNo());
            mLPPENoticeItemSet.get(i).setPrtSeq(mPrtSeq);
            //mLCPENoticeItemSet.get(i).setContNo( mContNo );
            //mLCPENoticeItemSet.get(i).setPEItemCode(); //核保规则编码
            //mLCPENoticeItemSet.get(i).setPEItemName(); //核保出错信息
            //mLCPENoticeItemSet.get(i).setCustomerNo(mCustomerNo);
            mLPPENoticeItemSet.get(i).setModifyDate(PubFun.getCurrentDate()); //当前值
            mLPPENoticeItemSet.get(i).setModifyTime(PubFun.getCurrentTime());
            //mLCPENoticeItemSet.get(i).setFreePE() ;
        }
        //准备核保主表信息
        mLPUWMasterMainSchema.setOperator(mOperater);
        mLPUWMasterMainSchema.setManageCom(mManageCom);
        mLPUWMasterMainSchema.setModifyDate(PubFun.getCurrentDate());
        mLPUWMasterMainSchema.setModifyTime(PubFun.getCurrentTime());
        mLPUWMasterMainSchema.setHealthFlag("1");

        return true;
    }


    /**
     * 打印信息表
     * @return
     */
    private boolean preparePrint()
    {
        // 处于未打印状态的通知书在打印队列中只能有一个
        // 条件：同一个单据类型，同一个其它号码，同一个其它号码类型
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

        tLOPRTManagerDB.setCode(PrintManagerBL.CODE_PEdorPE); //体检
        tLOPRTManagerDB.setOtherNo(mContNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_CONT); //保单号
        tLOPRTManagerDB.setStandbyFlag1(mInsuredNo);
        tLOPRTManagerDB.setStandbyFlag2(mEdorNo);
        tLOPRTManagerDB.setStateFlag("0");

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "查询打印管理表信息出错!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLOPRTManagerSet.size() != 0)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "在打印队列中已有一个处于未打印状态的体检通知书!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //准备打印管理表数据
//	 String strNoLimit = PubFun.getNoLimit( mGlobalInput.ComCode );
//	 String tPrtSeq = PubFun1.CreateMaxNo("PRTSEQNO", strNoLimit);
        mLOPRTManagerSchema.setPrtSeq(mPrtSeq);
        mLOPRTManagerSchema.setOtherNo(mContNo);
        mLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_CONT); //保单号
        mLOPRTManagerSchema.setCode(PrintManagerBL.CODE_PEdorPE); //体检
        mLOPRTManagerSchema.setManageCom(mLCContSchema.getManageCom());
        mLOPRTManagerSchema.setAgentCode(mLCContSchema.getAgentCode());
        mLOPRTManagerSchema.setReqCom(mGlobalInput.ComCode);
        mLOPRTManagerSchema.setReqOperator(mGlobalInput.Operator);
        //mLOPRTManagerSchema.setExeCom();
        //mLOPRTManagerSchema.setExeOperator();
        mLOPRTManagerSchema.setPrtType(PrintManagerBL.PT_FRONT); //前台打印
        mLOPRTManagerSchema.setStateFlag("0");
        mLOPRTManagerSchema.setPatchFlag("0");
        mLOPRTManagerSchema.setMakeDate(PubFun.getCurrentDate());
        mLOPRTManagerSchema.setMakeTime(PubFun.getCurrentTime());
        //mLOPRTManagerSchema.setDoneDate() ;
        //mLOPRTManagerSchema.setDoneTime();
        mLOPRTManagerSchema.setStandbyFlag1(mInsuredNo); //被保险人编码
        mLOPRTManagerSchema.setStandbyFlag2(mEdorNo); //保全批单号
        mLOPRTManagerSchema.setStandbyFlag3(mMissionID);
        mLOPRTManagerSchema.setOldPrtSeq(mPrtSeq);

        return true;
    }

    /**
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
     * @return
     */
    private boolean prepareTransferData()
    {
        LAAgentDB tLAAgentDB = new LAAgentDB();
        LAAgentSet tLAAgentSet = new LAAgentSet();
        tLAAgentDB.setAgentCode(mLCContSchema.getAgentCode());
        tLAAgentSet = tLAAgentDB.query();
        if (tLAAgentSet == null || tLAAgentSet.size() != 1)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人表LAAgent查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLAAgentSet.get(1).getAgentGroup() == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人表LAAgent中的代理机构数据丢失!";
            this.mErrors.addOneError(tError);
            return false;
        }

        LABranchGroupDB tLABranchGroupDB = new LABranchGroupDB();
        LABranchGroupSet tLABranchGroupSet = new LABranchGroupSet();
        tLABranchGroupDB.setAgentGroup(tLAAgentSet.get(1).getAgentGroup());
        tLABranchGroupSet = tLABranchGroupDB.query();
        if (tLABranchGroupSet == null || tLABranchGroupSet.size() != 1)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人展业机构表LABranchGroup查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLABranchGroupSet.get(1).getBranchAttr() == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人展业机构表LABranchGroup中展业机构信息丢失!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mTransferData.setNameAndValue("PrtSeq", mLOPRTManagerSchema.getPrtSeq());
        mTransferData.setNameAndValue("OldPrtSeq",
                mLOPRTManagerSchema.getPrtSeq());
        mTransferData.setNameAndValue("AgentCode", mLCContSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup",
                tLAAgentSet.get(1).getAgentGroup());
        mTransferData.setNameAndValue("BranchAttr",
                tLABranchGroupSet.get(1).getBranchAttr());
        mTransferData.setNameAndValue("ManageCom", mLCContSchema.getManageCom());
        return true;
    }

    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();

        //删除旧体检通知数据
        if (mOldLPPENoticeSet != null && mOldLPPENoticeSet.size() > 0)
        {
            map.put(mOldLPPENoticeSet, "DELETE");
        }

        //添加体检通知数据
        map.put(mLPPENoticeSchema, "INSERT");

        //删除旧体检项目通知数据
        if (mOldLPPENoticeItemSet != null && mOldLPPENoticeItemSet.size() > 0)
        {
            map.put(mOldLPPENoticeItemSet, "DELETE");
        }

        //添加体检项目数据
        map.put(mLPPENoticeItemSet, "INSERT");

        //添加体检通知书打印管理表数据
        map.put(mLOPRTManagerSchema, "INSERT");

        //添加保全批单核保主表通知书打印管理表数据
        map.put(mLPUWMasterMainSchema, "UPDATE");

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

    public static void main(String[] args)
    {
        //SysUWNoticeBL sysUWNoticeBL1 = new SysUWNoticeBL();
    }
}

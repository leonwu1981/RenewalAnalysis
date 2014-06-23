/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.xb;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.LAAgentSet;
import com.sinosoft.lis.vschema.LABranchGroupSet;
import com.sinosoft.lis.vschema.LOPRTManagerSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: </p>
 * <p>Description:工作流节点任务:续保人工核保生调录入服务类 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PRnewUWRReportAfterInitService implements AfterInitService
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
    /**工作流引擎 */
    ActivityOperator mActivityOperator = new ActivityOperator();
    /** 数据操作字符串 */
    private String mOperater;
    private String mManageCom;
    private String mOperate;
    /** 业务数据操作字符串 */
    private String mPolNo;
    private String mInsuredNo;
    private String mMissionID;
//    private Reflections mReflections = new Reflections();

    /**执行续保工作流生调通知书活动表任务0000000004*/
    /**保单表*/
    private LCPolSchema mLCPolSchema = new LCPolSchema();
    /** 续保核保主表 */
    private LCUWMasterSchema mLCUWMasterSchema = new LCUWMasterSchema();
    /** 生存调查表 */
    private LCRReportSchema mLCRReportSchema = new LCRReportSchema();
    /** 打印管理表 */
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();

    public PRnewUWRReportAfterInitService()
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

        System.out.println("After PRnewUWRReportAfterInitService Submit...");

        //mResult.clear();
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

        //添加生调数据
        if (mLCRReportSchema != null)
        {
            map.put(mLCRReportSchema, "INSERT");
        }

        //添加体检通知书打印管理表数据
        map.put(mLOPRTManagerSchema, "INSERT");

        //添加续保批单核保主表通知书打印管理表数据
        map.put(mLCUWMasterSchema, "UPDATE");

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
            tError.moduleName = "PEdorUWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mPolNo + "信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCPolSchema.setSchema(tLCPolDB);

        //校验保单被保人编码信息
        mInsuredNo = mLCPolSchema.getInsuredNo();
        if (mInsuredNo == null)
        {
            CError tError = new CError();
            tError.moduleName = "PEdorUWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mPolNo + "的被保人编码信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //校验续保批单核保主表
        //校验保单信息
        LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
        tLCUWMasterDB.setProposalNo(mPolNo);
        if (!tLCUWMasterDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorUWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mPolNo + "续保批单核保主表信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCUWMasterSchema.setSchema(tLCUWMasterDB);

//   if (mLPUWMasterMainSchema != null && mLPUWMasterMainSchema.getReportFlag().equals("1"))
//	 {
//	   CError tError = new CError();
//	   tError.moduleName = "PEdorUWRReportAfterInitService";
//	   tError.functionName = "checkData";
//	   tError.errorMessage = "已经发生调通知书,还未打印,不可再次进行生调录入!";
//	   this.mErrors .addOneError(tError) ;
//	   return false;
//	 }


        // 处于未打印状态的核保通知书在打印队列中只能有一个
        // 条件：同一个单据类型，同一个其它号码，同一个其它号码类型
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

        tLOPRTManagerDB.setCode(PrintManagerBL.CODE_PRnewMEET); //生调通知书
        tLOPRTManagerDB.setOtherNo(mPolNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_INDPOL); //保单号
        tLOPRTManagerDB.setStateFlag("0");

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PRnewUWRReportAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "查询打印管理表信息出错!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLOPRTManagerSet.size() != 0)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PRnewUWRReportAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "在打印队列中已有一个处于未打印状态的生调通知书!";
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
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewUWRReportAfterInitService";
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
            tError.moduleName = "PRnewUWRReportAfterInitService";
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
            tError.moduleName = "PRnewUWRReportAfterInitService";
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
            tError.moduleName = "PRnewUWRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mPolNo = (String) mTransferData.getValueByName("PolNo");
        if (mPolNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewUWRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中PolNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得业务生调通知数据
        mLCRReportSchema = (LCRReportSchema) mTransferData.getValueByName("LCRReportSchema");
        if (mLCRReportSchema == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewUWRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输获得业务生调数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (mLCRReportSchema.getContente() == null
                || mLCRReportSchema.getContente().trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewUWRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输获得业务生调内容数据失败!";
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

        return true;
    }

    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     */
    private boolean dealData()
    {
        // 核保生调信息
        if (!prepareReport())
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
     * 准备生调资料信息
     * 输出：如果发生错误则返回false,否则返回true
     */
    private boolean prepareReport()
    {

        //准备生调
        //生成流水号
        String tsql = "select max(serialno) from lcrreport where polno = '" + mPolNo + "'";

        ExeSQL tExeSQL = new ExeSQL();
        String tSerialno = tExeSQL.getOneValue(tsql);

        if (tSerialno == null)
        {
            tSerialno = "0";
        }
        else
        {
            Integer ttno = Integer.valueOf(tSerialno.trim());
            int tno = ttno.intValue();
            tno = +1;
            tSerialno = String.valueOf(tno);
        }

//	 mLCRReportSchema.setSerialNo(tSerialno);
        mLCRReportSchema.setContNo(mLCPolSchema.getContNo());
//	 mLCRReportSchema.setGrpPolNo(mLCPolSchema.getGrpPolNo());
        mLCRReportSchema.setAppntNo(mLCPolSchema.getAppntNo());
        mLCRReportSchema.setAppntName(mLCPolSchema.getAppntName());
//	 mLCRReportSchema.setInsuredNo(mLCPolSchema.getInsuredNo());
//	 mLCRReportSchema.setInsuredName(mLCPolSchema.getInsuredName());
        mLCRReportSchema.setManageCom(mManageCom);
        mLCRReportSchema.setReplyContente("");
        mLCRReportSchema.setReplyFlag("0");
        mLCRReportSchema.setOperator(mOperater);
        mLCRReportSchema.setMakeDate(PubFun.getCurrentDate());
        mLCRReportSchema.setMakeTime(PubFun.getCurrentTime());
        mLCRReportSchema.setReplyOperator("");
        mLCRReportSchema.setReplyDate("");
        mLCRReportSchema.setReplyTime("");
        mLCRReportSchema.setModifyDate(PubFun.getCurrentDate());
        mLCRReportSchema.setModifyTime(PubFun.getCurrentTime());

        ////准备核保主表信息

        mLCUWMasterSchema.setOperator(mOperater);
        mLCUWMasterSchema.setManageCom(mManageCom);
        mLCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
        mLCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());

        mLCUWMasterSchema.setReportFlag("1");

        return true;
    }

    /**
     * 打印信息表
     * @return
     */
    private boolean preparePrint()
    {

        //准备打印管理表数据
        String strNoLimit = PubFun.getNoLimit(mGlobalInput.ComCode);
        String tPrtSeq = PubFun1.CreateMaxNo("PRTSEQNO", strNoLimit);
        mLOPRTManagerSchema.setPrtSeq(tPrtSeq);
        mLOPRTManagerSchema.setOtherNo(mPolNo);
        mLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_INDPOL); //保单号
        mLOPRTManagerSchema.setCode(PrintManagerBL.CODE_PRnewMEET); //生调
        mLOPRTManagerSchema.setManageCom(mLCPolSchema.getManageCom());
        mLOPRTManagerSchema.setAgentCode(mLCPolSchema.getAgentCode());
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
        mLOPRTManagerSchema.setStandbyFlag2(mLCPolSchema.getPrtNo()); //被保险人编码
        mLOPRTManagerSchema.setStandbyFlag3(mMissionID);
        mLOPRTManagerSchema.setOldPrtSeq(tPrtSeq);
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
        tLAAgentDB.setAgentCode(mLCPolSchema.getAgentCode());
        tLAAgentSet = tLAAgentDB.query();
        if (tLAAgentSet == null || tLAAgentSet.size() != 1)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PRnewUWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人表LAAgent查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLAAgentSet.get(1).getAgentGroup() == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PRnewUWRReportAfterInitService";
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
            tError.moduleName = "PRnewUWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人展业机构表LABranchGroup查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLABranchGroupSet.get(1).getBranchAttr() == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PRnewUWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人展业机构表LABranchGroup中展业机构信息丢失!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mTransferData.setNameAndValue("PrtSeq", mLOPRTManagerSchema.getPrtSeq());
        mTransferData.setNameAndValue("AgentCode", mLCPolSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup", tLAAgentSet.get(1).getAgentGroup());
        mTransferData.setNameAndValue("BranchAttr", tLABranchGroupSet.get(1).getBranchAttr());
        mTransferData.setNameAndValue("ManageCom", mLCPolSchema.getManageCom());
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

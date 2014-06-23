/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import java.util.Date;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;
import com.sinosoft.workflowengine.AfterInitService;


/**
 * <p>Title:工作流节点任务:新契约发生调通知书 </p>
 * <p>Description: 发生调通知书工作流AfterInit服务类 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: SinoSoft </p>
 * @author HYQ
 * @version 1.0
 */

public class UWRReportAfterInitService implements AfterInitService
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 往界面传输数据的容器 */
    private VData mResult = new VData();

    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();

    /**工作流引擎 */
    ActivityOperator mActivityOperator = new ActivityOperator();

    /** 数据操作字符串 */
    private String mOperater;
    private String mManageCom;

    /** 业务数据操作字符串 */
    private String mContNo;
    private String mCustomerNo;
    private String mInsuredNo;
    private String mName;
    private String mMissionID;
    String mPrtSeq = "";

    /**保单表*/
    private LCContSchema mLCContSchema = new LCContSchema();

//    /** 续保核保主表 */
//    private LCCUWMasterSchema mLCCUWMasterSchema = new LCCUWMasterSchema();

    /** 生存调查表 */
    private LCRReportSchema mLCRReportSchema = new LCRReportSchema();
    private LCRReportItemSet mLCRReportItemSet = new LCRReportItemSet();
    private LCRReportItemSet mNewLCRReportItemSet = new LCRReportItemSet();
    /** 打印管理表 */
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();

    /**自动标志**/
//    private boolean mAutoSysCertSendOutFlag = true;

    public UWRReportAfterInitService()
    {
    }


    /**
     * 传输数据的公共方法
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
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

        System.out.println("After UWRReportAfterInitService Submit...");

        return true;
    }

    /**
     * 准备返回前台统一存储数据
     * 输出：如果发生错误则返回false,否则返回true
     * @return boolean
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
//        map.put(mLCCUWMasterSchema, "UPDATE");
        map.put(mNewLCRReportItemSet, "INSERT");
        mResult.add(map);
        return true;
    }

    /**
     * 校验业务数据
     * @return boolean
     */
    private boolean checkData()
    {
        //校验保单信息
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mContNo);
        if (!tLCContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mContNo + "信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);

        //校验保单被保人编码信息
        mInsuredNo = mLCContSchema.getInsuredNo();
        if (mInsuredNo == null)
        {
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mContNo + "的被保人编码信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

//        //校验续保批单核保主表
//        //校验保单信息
//        LCCUWMasterDB tLCCUWMasterDB = new LCCUWMasterDB();
//        tLCCUWMasterDB.setContNo(mContNo);
//        if (!tLCCUWMasterDB.getInfo())
//        {
//            CError tError = new CError();
//            tError.moduleName = "UWRReportAfterInitService";
//            tError.functionName = "checkData";
//            tError.errorMessage = "保单" + mContNo + "续保批单核保主表信息查询失败!";
//            this.mErrors.addOneError(tError);
//            return false;
//        }
//
//        mLCCUWMasterSchema.setSchema(tLCCUWMasterDB);

        // 处于未打印状态的核保通知书在打印队列中只能有一个
        // 条件：同一个单据类型，同一个其它号码，同一个其它号码类型
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

        tLOPRTManagerDB.setCode(PrintManagerBL.CODE_MEET); //生调通知书
        tLOPRTManagerDB.setOtherNo(mContNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_CONT); //保单号
        tLOPRTManagerDB.setStateFlag("0"); //通知书未打印

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "查询打印管理表信息出错!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //取生调人姓名
        LDPersonDB tLDPersonDB = new LDPersonDB();
        tLDPersonDB.setCustomerNo(mCustomerNo);
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
        mName = tLDPersonDB.getName();

        return true;
    }

    /**
     * 从输入数据中得到所有对象
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
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
            tError.moduleName = "UWRReportAfterInitService";
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
            tError.moduleName = "UWRReportAfterInitService";
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
            tError.moduleName = "UWRReportAfterInitService";
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
            tError.moduleName = "UWRReportAfterInitService";
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
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中ContNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mCustomerNo = (String) mTransferData.getValueByName("CustomerNo");
        if (mCustomerNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中CustomerNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得业务生调通知数据
        mLCRReportSchema = (LCRReportSchema) mTransferData.getValueByName(
                "LCRReportSchema");
        if (mLCRReportSchema == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输获得业务生调数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCRReportItemSet = (LCRReportItemSet) mTransferData.getValueByName("LCRReportItemSet");
        if (mLCRReportItemSet == null || mLCRReportItemSet.size() <= 0)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输获得业务生调项目内容数据失败!";
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
            tError.moduleName = "UWRReportAfterInitService";
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
     * @return boolean
     */
    private boolean dealData()
    {
        VData tVData = new VData();
        String strNoLimit = PubFun.getNoLimit(mGlobalInput.ComCode);
        //mPrtSeq = PubFun1.CreateMaxNo("PRTSEQNO", strNoLimit);
        mPrtSeq = PubFun1.CreateMaxNo("QIDIAO", strNoLimit, tVData);
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
     * @return boolean
     */
    private boolean prepareReport()
    {
        LCRReportItemSchema tLCRReportItemSchema;

        mLCRReportSchema.setPrtSeq(mPrtSeq);
        mLCRReportSchema.setContNo(mLCContSchema.getContNo());
        mLCRReportSchema.setGrpContNo(mLCContSchema.getGrpContNo());
        mLCRReportSchema.setProposalContNo(mLCContSchema.getProposalContNo());
        mLCRReportSchema.setAppntNo(mLCContSchema.getAppntNo());
        mLCRReportSchema.setAppntName(mLCContSchema.getAppntName());
        mLCRReportSchema.setCustomerNo(mCustomerNo);
        mLCRReportSchema.setName(mName);
        mLCRReportSchema.setManageCom(mManageCom);
        mLCRReportSchema.setReplyContente("");
        mLCRReportSchema.setContente(mLCRReportSchema.getContente());
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

//        mLCCUWMasterSchema.setOperator(mOperater);
//        mLCCUWMasterSchema.setManageCom(mManageCom);
//        mLCCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
//        mLCCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());
//        mLCCUWMasterSchema.setReportFlag("1");          //发生调通知书并等待回复

        for (int i = 1; i <= mLCRReportItemSet.size(); i++)
        {
            tLCRReportItemSchema = new LCRReportItemSchema();

            tLCRReportItemSchema.setContNo(mLCContSchema.getContNo());
            tLCRReportItemSchema.setGrpContNo(mLCContSchema.getGrpContNo());
            tLCRReportItemSchema.setProposalContNo(mLCContSchema.
                    getProposalContNo());
            tLCRReportItemSchema.setPrtSeq(mPrtSeq);
            tLCRReportItemSchema.setRReportItemCode(mLCRReportItemSet.get(i).
                    getRReportItemCode());
            tLCRReportItemSchema.setRReportItemName(mLCRReportItemSet.get(i).
                    getRReportItemName());
            tLCRReportItemSchema.setModifyDate(PubFun.getCurrentDate());
            tLCRReportItemSchema.setModifyTime(PubFun.getCurrentTime());

            mNewLCRReportItemSet.add(tLCRReportItemSchema);
        }
        return true;
    }


    /**
     * 打印信息表
     * @return boolean
     */
    private boolean preparePrint()
    {

        LDSysVarDB tLDSysVarDB = new LDSysVarDB();
        tLDSysVarDB.setSysVar("URGEInterval");

        if (!tLDSysVarDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "UWSendPrintBL";
            tError.functionName = "prepareURGE";
            tError.errorMessage = "没有描述催发间隔!";
            this.mErrors.addOneError(tError);
            return false;
        }
        FDate tFDate = new FDate();
        int tInterval = Integer.parseInt(tLDSysVarDB.getSysVarValue());
        System.out.println(tInterval);

        Date tDate = PubFun.calDate(tFDate.getDate(PubFun.getCurrentDate()),
                tInterval, "D", null);
        System.out.println(tDate); //取预计催办日期
        //准备打印管理表数据
        mLOPRTManagerSchema.setPrtSeq(mPrtSeq);
        mLOPRTManagerSchema.setOtherNo(mContNo);
        mLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_CONT); //保单号
        mLOPRTManagerSchema.setCode(PrintManagerBL.CODE_MEET); //生调
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
        mLOPRTManagerSchema.setStandbyFlag1(mCustomerNo); //体检人编码
        mLOPRTManagerSchema.setStandbyFlag2(mLCContSchema.getPrtNo()); //被保险人编码
        mLOPRTManagerSchema.setStandbyFlag3(mMissionID);
        mLOPRTManagerSchema.setOldPrtSeq(mPrtSeq);
        mLOPRTManagerSchema.setForMakeDate(tDate);
        return true;
    }

    /**
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
     * @return boolean
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
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人表LAAgent查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLAAgentSet.get(1).getAgentGroup() == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
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
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人展业机构表LABranchGroup查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLABranchGroupSet.get(1).getBranchAttr() == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
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
        mTransferData.setNameAndValue("CustomerNo", mCustomerNo);

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

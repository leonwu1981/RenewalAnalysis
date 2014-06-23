package com.sinosoft.workflow.bq;

import com.sinosoft.lis.db.*;
import java.util.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;
import com.sinosoft.workflowengine.AfterInitService;
import com.sinosoft.lis.vdb.LPContDBSet;

/**
 * <p>Title: 工作流节点任务:保全发体检通知书 </p>
 * <p>Description: 工作流发体检通知书AfterInit服务类</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: SinoSoft</p>
 * @author Lanjun
 * @version 1.0
 */

public class PEdorAppUWRReportAfterInitService
    implements AfterInitService {
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
  private String mMissionID;

  /**保单表*/
  private LPContSchema mLPContSchema = new LPContSchema();
  private String mPrtSeq;

  /** 保全核保主表 */
  private LPCUWMasterSchema mLPCUWMasterSchema = new LPCUWMasterSchema();

  /** 体检资料主表 */
  private LPAppRReportSchema mLPAppRReportSchema = new LPAppRReportSchema();

  /** 体检资料项目表 */
  private LPAppRReportItemSet mLPAppRReportItemSet = new LPAppRReportItemSet();

  /** 打印管理表 */
  private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();

  public PEdorAppUWRReportAfterInitService() {
  }

  /**
   * 传输数据的公共方法
   * @param: cInputData 输入的数据
   *         cOperate 数据操作
   * @return:
   */
  public boolean submitData(VData cInputData, String cOperate) {
    //得到外部传入的数据,将数据备份到本类中
    if (!getInputData(cInputData, cOperate))
      return false;

    //校验是否有未打印的体检通知书
    if (!checkData())
      return false;

    //进行业务处理
    if (!dealData())
      return false;

    //为工作流下一节点属性字段准备数据
    if (!prepareTransferData())
      return false;

    //准备往后台的数据
    if (!prepareOutputData())
      return false;

    System.out.println("Start SysUWNoticeBL Submit...");

    //mResult.clear();
    return true;
  }

  /**
   * 校验业务数据
   * @return
   */
  private boolean checkData() {
    //校验保单信息
        LPContDB tLPContDB = new LPContDB();
        tLPContDB.setContNo(mContNo);

        LPContSet tLPContSet = new LPContSet();
        tLPContSet = tLPContDB.query();
        if (tLPContSet.size() < 1 || tLPContSet == null)
        {
            CError tError = new CError();
            tError.moduleName = "PEdorAppUWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mContNo + "信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLPContSchema.setSchema(tLPContSet.get(1));

       //校验是否有未打印的体检通知书
        LPAppRReportDB tLPAppRReportDB = new LPAppRReportDB();
        tLPAppRReportDB.setContNo(mContNo);
        tLPAppRReportDB.setCustomerNo(mCustomerNo);



        //校验保全批单核保主表
        //校验保单信息
        LPCUWMasterDB tLPCUWMasterDB = new LPCUWMasterDB();
        tLPCUWMasterDB.setContNo(mContNo);
        String sql = "select * from LPCUWMaster where ContNo='" + mContNo
            + "' and edorno in (select edorno from lpedormain where edoracceptno = '"
            +(String) mTransferData.getValueByName("EdorAcceptNo") + "')";
        LPCUWMasterSet tLPCUWMasterSet = new LPCUWMasterSet();
        tLPCUWMasterSet = tLPCUWMasterDB.executeQuery(sql);
        if (tLPCUWMasterSet.size()<1 || tLPCUWMasterSet == null)
        {
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mContNo + "保全批单核保主表信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLPCUWMasterSchema.setSchema(tLPCUWMasterSet.get(1));

    return true;
  }

  /**
   * 根据前面的输入数据，进行BL逻辑处理
   * 如果在处理过程中出错，则返回false,否则返回true
   */
  private boolean dealData() {

    String strNoLimit = PubFun.getNoLimit(mGlobalInput.ComCode);

    mPrtSeq = PubFun1.CreateMaxNo("PRTSEQNO", strNoLimit);

    // 体检信息
    if (prepareReport() == false)
      return false;

    //打印队列
    if (preparePrint() == false)
      return false;

    return true;
  }

  /**
   * 从输入数据中得到所有对象
   *输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
   */
  private boolean getInputData(VData cInputData, String cOperate) {
    //从输入数据中得到所有对象
    //获得全局公共数据
    mGlobalInput.setSchema( (GlobalInput) cInputData.getObjectByObjectName(
        "GlobalInput", 0));
    mTransferData = (TransferData) cInputData.getObjectByObjectName(
        "TransferData", 0);

    if (mGlobalInput == null) {
      // @@错误处理
      //this.mErrors.copyAllErrors( tLPContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "PEdorAppUWRReportAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "前台传输全局公共数据失败!";
      this.mErrors.addOneError(tError);
      return false;
    }

    //获得操作员编码
    mOperater = mGlobalInput.Operator;
    if (mOperater == null || mOperater.trim().equals("")) {
      // @@错误处理
      //this.mErrors.copyAllErrors( tLPContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "PEdorAppUWRReportAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "前台传输全局公共数据Operate失败!";
      this.mErrors.addOneError(tError);
      return false;
    }

    //获得登陆机构编码
    mManageCom = mGlobalInput.ManageCom;
    if (mManageCom == null || mManageCom.trim().equals("")) {
      // @@错误处理
      //this.mErrors.copyAllErrors( tLPContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "PEdorAppUWRReportAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
      this.mErrors.addOneError(tError);
      return false;
    }

    //获得业务数据
    if (mTransferData == null) {
      // @@错误处理
      //this.mErrors.copyAllErrors( tLPContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "PEdorAppUWRReportAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "前台传输业务数据失败!";
      this.mErrors.addOneError(tError);
      return false;
    }

        mContNo = (String) mTransferData.getValueByName("ContNo");
        System.out.println("mContNo="+mContNo);
        if (mContNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLPContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorAppUWRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中ContNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

    mCustomerNo = (String) mTransferData.getValueByName("CustomerNo");
    if (mCustomerNo == null) {
      // @@错误处理
      //this.mErrors.copyAllErrors( tLPContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "PEdorAppUWRReportAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "前台传输业务数据中CustomerNo失败!";
      this.mErrors.addOneError(tError);
      return false;
    }

    //获得业务体检通知数据
    mLPAppRReportSchema = (LPAppRReportSchema) mTransferData.getValueByName(
        "LPAppRReportSchema");
    if (mLPAppRReportSchema == null) {
      // @@错误处理
      //this.mErrors.copyAllErrors( tLPContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "PEdorAppUWRReportAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "前台传输获得业务体检通知数据失败!";
      this.mErrors.addOneError(tError);
      return false;
    }
    //获得业务体检通知对应的体检项目
    mLPAppRReportItemSet = (LPAppRReportItemSet) mTransferData.getValueByName(
        "LPAppRReportItemSet");
    if (mLPAppRReportItemSet == null || mLPAppRReportItemSet.size() == 0) {
      // @@错误处理
      //this.mErrors.copyAllErrors( tLPContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "PEdorAppUWRReportAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "前台传输获得业务体检通知对应的体检项目数据失败!";
      this.mErrors.addOneError(tError);
      return false;
    }

    //获得当前工作任务的任务ID
    mMissionID = (String) mTransferData.getValueByName("MissionID");
    if (mMissionID == null) {
      // @@错误处理
      //this.mErrors.copyAllErrors( tLPContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "PEdorAppUWRReportAfterInitService";
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
  private boolean prepareReport() {

   //取代理人姓名
        LAAgentDB tLAAgentDB = new LAAgentDB();
        tLAAgentDB.setAgentCode(mLPContSchema.getAgentCode());
        if (!tLAAgentDB.getInfo())
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PEdorAppUWRReportAfterInitService";
            tError.functionName = "prepareHealth";
            tError.errorMessage = "取代理人姓名失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //取体检人姓名
    LDPersonDB tLDPersonDB = new LDPersonDB();
    tLDPersonDB.setCustomerNo(mCustomerNo);
    if (!tLDPersonDB.getInfo()) {
      // @@错误处理
      CError tError = new CError();
      tError.moduleName = "PEdorAppUWRReportAfterInitService";
      tError.functionName = "prepareHealth";
      tError.errorMessage = "取被体检客户姓名失败!";
      this.mErrors.addOneError(tError);
      return false;
    }

    mLPAppRReportSchema.setGrpContNo(mLPContSchema.getGrpContNo());
    mLPAppRReportSchema.setContNo(mLPContSchema.getContNo());
    mLPAppRReportSchema.setEdorAcceptNo( (String) mTransferData.
          getValueByName("EdorAcceptNo"));
   // mLPAppRReportSchema.setProposaLPontNo(mLPContSchema.getProposaLPontNo());
    mLPAppRReportSchema.setCustomerNo(mCustomerNo);
    mLPAppRReportSchema.setName(tLDPersonDB.getName());
    mLPAppRReportSchema.setPrtSeq(mPrtSeq);
    System.out.println("SEQNO=" + mPrtSeq);
   // mLPAppRReportSchema.setPEDate(mLPAppRReportSchema.getPEDate());
  //  mLPAppRReportSchema.set(mLPAppRReportSchema.getPEAddress());
    //mLPAppRReportSchema.setPrintFlag("0");
    //mLPAppRReportSchema.setAppName(mLPContSchema.getAppntName());
  //  mLPAppRReportSchema.setAgentCode(mLPContSchema.getAgentCode());
  //  mLPAppRReportSchema.setAgentName(tLAAgentDB.getName());
    mLPAppRReportSchema.setManageCom(mLPContSchema.getManageCom());
   // mLPAppRReportSchema.setPEBeforeCond(mLPAppRReportSchema.getPEBeforeCond());
    mLPAppRReportSchema.setOperator(mOperater); //操作员
    mLPAppRReportSchema.setMakeDate(PubFun.getCurrentDate());
    mLPAppRReportSchema.setMakeTime(PubFun.getCurrentTime());
    mLPAppRReportSchema.setModifyDate(PubFun.getCurrentDate());
    mLPAppRReportSchema.setModifyTime(PubFun.getCurrentTime());
    mLPAppRReportSchema.setReplyContent(mLPAppRReportSchema.getReplyContent());

    //准备检资料项目信息
    for (int i = 1; i <= mLPAppRReportItemSet.size(); i++) {

      mLPAppRReportItemSet.get(i).setGrpContNo(mLPContSchema.getGrpContNo());
      mLPAppRReportItemSet.get(i).setContNo(mLPContSchema.getContNo());
      mLPAppRReportItemSet.get(i).setEdorAcceptNo( (String) mTransferData.
          getValueByName("EdorAcceptNo"));
      System.out.println( (String) mTransferData.getValueByName("EdorAcceptNo"));
      //  mLPAppRReportItemSet.get(i).setProposaLPontNo(mLPContSchema.getProposaLPontNo());
      mLPAppRReportItemSet.get(i).setPrtSeq(mPrtSeq);
      //mLPAppRReportItemSet.get(i).setPEItemCode(); //核保规则编码
      //mLPAppRReportItemSet.get(i).setPEItemName(); //核保出错信息
      //mLPAppRReportItemSet.get(i).setCustomerNo(mCustomerNo);
      mLPAppRReportItemSet.get(i).setModifyDate(PubFun.getCurrentDate()); //当前值
      mLPAppRReportItemSet.get(i).setModifyTime(PubFun.getCurrentTime());
      //mLPAppRReportItemSet.get(i).setFreePE() ;
    }
    //准备核保主表信息
    mLPCUWMasterSchema.setOperator(mOperater);
    mLPCUWMasterSchema.setManageCom(mManageCom);
    mLPCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
    mLPCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());

    return true;
  }

  /**
   * 打印信息表
   * @return
   */
  private boolean preparePrint() {
    // 处于未打印状态的通知书在打印队列中只能有一个
    // 条件：同一个单据类型，同一个其它号码，同一个其它号码类型
    LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

    tLOPRTManagerDB.setCode(PrintManagerBL.CODE_PE); //体检
    //tLOPRTManagerDB.setOtherNo(mContNo);
    tLOPRTManagerDB.setOtherNo( (String) mTransferData.getValueByName(
        "EdorAcceptNo"));
    tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_CONT); //保单号
    tLOPRTManagerDB.setStandbyFlag1(mCustomerNo);
    tLOPRTManagerDB.setStateFlag("0");

    LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
    if (tLOPRTManagerSet == null) {
      // @@错误处理
      CError tError = new CError();
      tError.moduleName = "PEdorAppUWRReportAfterInitService";
      tError.functionName = "preparePrint";
      tError.errorMessage = "查询打印管理表信息出错!";
      this.mErrors.addOneError(tError);
      return false;
    }

    LDSysVarDB tLDSysVarDB = new LDSysVarDB();
    tLDSysVarDB.setSysVar("URGEInterval");

    if (tLDSysVarDB.getInfo() == false) {
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
    mLOPRTManagerSchema.setOtherNo( (String) mTransferData.getValueByName(
        "EdorAcceptNo"));
    mLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_EDORACCEPT); //保单号
    mLOPRTManagerSchema.setCode(PrintManagerBL.CODE_PEdorMEET); //生调
    mLOPRTManagerSchema.setManageCom(mLPContSchema.getManageCom());
    mLOPRTManagerSchema.setAgentCode(mLPContSchema.getAgentCode());
    mLOPRTManagerSchema.setReqCom(mGlobalInput.ComCode);
    mLOPRTManagerSchema.setReqOperator(mGlobalInput.Operator);
    //mLOPRTManagerSchema.setExeCom();
    //mLOPRTManagerSchema.setExeOperator();
    mLOPRTManagerSchema.setPrtType(PrintManagerBL.PT_FRONT); //前台打印
    mLOPRTManagerSchema.setStateFlag("0");
    mLOPRTManagerSchema.setPatchFlag("0");
    mLOPRTManagerSchema.setMakeDate(PubFun.getCurrentDate());
    mLOPRTManagerSchema.setMakeTime(PubFun.getCurrentTime());
    // mLOPRTManagerSchema.setDoneDate() ;
    // mLOPRTManagerSchema.setDoneTime();
    mLOPRTManagerSchema.setStandbyFlag1(mCustomerNo); //被保险人编码
    mLOPRTManagerSchema.setStandbyFlag3(mMissionID);
    mLOPRTManagerSchema.setOldPrtSeq(mPrtSeq);
    mLOPRTManagerSchema.setForMakeDate(tDate);

    return true;
  }

  /**
   * 为公共传输数据集合中添加工作流下一节点属性字段数据
   * @return
   */
  private boolean prepareTransferData() {
        LAAgentDB tLAAgentDB = new LAAgentDB();
        LAAgentSet tLAAgentSet = new LAAgentSet();
        tLAAgentDB.setAgentCode(mLPContSchema.getAgentCode());
        System.out.println("mLPContSchema.getAgentCode()"+mLPContSchema.getAgentCode());
        tLAAgentSet = tLAAgentDB.query();
        if (tLAAgentSet == null || tLAAgentSet.size() != 1)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PEdorAppUWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人表LAAgent查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLAAgentSet.get(1).getAgentGroup() == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PEdorAppUWRReportAfterInitService";
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
            tError.moduleName = "PEdorAppUWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人展业机构表LABranchGroup查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLABranchGroupSet.get(1).getBranchAttr() == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PEdorAppUWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人展业机构表LABranchGroup中展业机构信息丢失!";
            this.mErrors.addOneError(tError);
            return false;
        }


    mTransferData.setNameAndValue("EdorAcceptNo",mLOPRTManagerSchema.getOtherNo());
    mTransferData.setNameAndValue("PrtSeq", mLOPRTManagerSchema.getPrtSeq());
    mTransferData.setNameAndValue("OldPrtSeq", mLOPRTManagerSchema.getPrtSeq());
    mTransferData.setNameAndValue("AgentCode", mLPContSchema.getAgentCode());
    mTransferData.setNameAndValue("AgentGroup",
                                      tLAAgentSet.get(1).getAgentGroup());
    mTransferData.setNameAndValue("BranchAttr",
                                      tLABranchGroupSet.get(1).getBranchAttr());
    mTransferData.setNameAndValue("ManageCom", mLPContSchema.getManageCom());
    mTransferData.setNameAndValue("ContNo",mLPContSchema.getContNo());
    System.out.println("class信息：edorAcceptNo="+mTransferData.getValueByName("EdorAcceptNo"));

    return true;
  }

  /**
   * 工作流准备后台提交数据
   * @return boolean
   */
  private boolean prepareOutputData() {
    mResult.clear();
    MMap map = new MMap();

    //添加体检通知数据
    map.put(mLPAppRReportSchema, "INSERT");

    //添加体检项目数据
    map.put(mLPAppRReportItemSet, "INSERT");

    //添加体检通知书打印管理表数据
    map.put(mLOPRTManagerSchema, "INSERT");

    //添加保全批单核保主表通知书打印管理表数据
     map.put(mLPCUWMasterSchema, "UPDATE");

    mResult.add(map);
    return true;
  }

  /**
   * 返回处理后的结果
   * @return VData
   */
  public VData getResult() {
    return mResult;
  }

  /**
   * 返回工作流中的Lwfieldmap所描述的值
   * @return TransferData
   */
  public TransferData getReturnTransferData() {
    return mTransferData;
  }

  /**
   * 返回错误对象
   * @return CErrors
   */
  public CErrors getErrors() {
    return mErrors;
  }

  public static void main(String[] args) {
    //SysUWNoticeBL sysUWNoticeBL1 = new SysUWNoticeBL();
  }
}

package com.sinosoft.workflow.ask;

import com.sinosoft.workflowengine.*;
import java.lang.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.utility.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.cbcheck.*;
import com.sinosoft.utility.VData;
/**
 * <p>Title: 工作流服务类模版 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author heyq
 * @version 1.0
 */

public class AskUWTrackAfterInitService implements AfterInitService{

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
  private LCGCUWMasterSchema mLCGCUWMasterSchema = new LCGCUWMasterSchema();
  private MMap mMap = new MMap();

  /** 数据操作字符串 */
  private String mOperater;
  private String mManageCom;
  private String mOperate;
  private String mMissionID;
  private String mSubMissionID;
  private String mGrpContNo;
  private String mUWFlag;

  public AskUWTrackAfterInitService() {
  }

  /**
   * 传输数据的公共方法
   * @param: cInputData 输入的数据
   *         cOperate 数据操作
   * @return:
   */
  public boolean submitData(VData cInputData, String cOperate) {

    mInputData = (VData) cInputData.clone();
    //得到外部传入的数据,将数据备份到本类中
    if (!getInputData(cInputData, cOperate))
      return false;

    //校验是否有未打印的体检通知书
    if (!checkData())
      return false;

    System.out.println("Start  dealData...");

    //进行业务处理
    if (!dealData())
      return false;

    System.out.println("dealData successful!");

    //为工作流下一节点属性字段准备数据
    if (!prepareTransferData())
      return false;

    //准备往后台的数据
    if (!prepareOutputData())
      return false;

    System.out.println("Start  Submit...");

    return true;
  }

  /**
   * 准备返回前台统一存储数据
   * 输出：如果发生错误则返回false,否则返回true
   */
  private boolean prepareOutputData() {
    mResult.clear();
    MMap map = new MMap();

    map.add(mMap);

    mResult.add(map);
    return true;
  }

  /**
   * 校验业务数据
   * @return
   */
  private boolean checkData() {

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
    mInputData = cInputData;
    if (mGlobalInput == null) {
      // @@错误处理
      //this.mErrors.copyAllErrors( tLCContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "AskUWConfirmAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "前台传输全局公共数据失败!";
      this.mErrors.addOneError(tError);
      return false;
    }

    //获得操作员编码
    mOperater = mGlobalInput.Operator;
    if (mOperater == null || mOperater.trim().equals("")) {
      // @@错误处理
      //this.mErrors.copyAllErrors( tLCContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "AskUWConfirmAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "前台传输全局公共数据Operate失败!";
      this.mErrors.addOneError(tError);
      return false;
    }

    mOperate = cOperate;
    //获得当前工作任务的任务ID
    mMissionID = (String) mTransferData.getValueByName("MissionID");
    if (mMissionID == null) {
      // @@错误处理
      //this.mErrors.copyAllErrors( tLCContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "AskUWConfirmAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "前台传输业务数据中MissionID失败!";
      this.mErrors.addOneError(tError);
      return false;
    }

    //获得当集体合同号
    mGrpContNo = (String) mTransferData.getValueByName("GrpContNo");
    if (mGrpContNo == null) {
      // @@错误处理
      //this.mErrors.copyAllErrors( tLCContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "AskUWConfirmAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "前台传输业务数据中GrpContNo失败!";
      this.mErrors.addOneError(tError);
      return false;
    }

    mLCGCUWMasterSchema = (LCGCUWMasterSchema)mTransferData.getValueByName("LCGCUWMasterSchema");
    if (mLCGCUWMasterSchema == null) {
      // @@错误处理
      //this.mErrors.copyAllErrors( tLCContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "AskUWConfirmAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "前台传输业务数据LCGCUWMasterSchema失败!";
      this.mErrors.addOneError(tError);
      return false;
    }

    return true;
  }

  /**
   * 根据前面的输入数据，进行BL逻辑处理
   * 如果在处理过程中出错，则返回false,否则返回true
   */
  private boolean dealData() {


      AskUWTrackConfirmBL tAskUWConfirmBL = new AskUWTrackConfirmBL();
      boolean tResult = tAskUWConfirmBL.submitData(mInputData,"");
      if(tResult)
      {
          mMap = (MMap)tAskUWConfirmBL.getResult().getObjectByObjectName("MMap",0);
      }
      else
      {
          this.mErrors.copyAllErrors(tAskUWConfirmBL.getErrors());
      }

    return true;
  }

  /**
   * 为公共传输数据集合中添加工作流下一节点属性字段数据
   * @return
   */
  private boolean prepareTransferData() {

    mTransferData.setNameAndValue("UWFlag",mUWFlag) ;

    return true;
  }

  public VData getResult() {
    return mResult;
  }

  public TransferData getReturnTransferData() {
    return mTransferData;
  }

  public CErrors getErrors() {
    return mErrors;
  }

}

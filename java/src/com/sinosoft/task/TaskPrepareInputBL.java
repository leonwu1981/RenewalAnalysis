package com.sinosoft.task;

import com.sinosoft.utility.*;
import com.sinosoft.lis.bl.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;

/**
 * <p>Title: 工单管理系统</p>
 * <p>Description: 工单录入BL层业务逻辑处理类 </p>
 * <p>Copyright: Copyright (c) 2005 </p>
 * <p>Company: Sinosoft </p>
 * @author QiuYang
 * @version 1.0
 * @date 2005-01-17
 */

public class TaskPrepareInputBL {
  /** 错误处理类 */
  public CErrors mErrors = new CErrors();

  /** 输入数据的容器 */
  private VData mInputData = new VData();

  /** 输出数据的容器 */
  private VData mResult = new VData();

  /** 数据操作字符串 */
  private String mOperate;

  private LGWorkSchema mLGWorkSchema = new LGWorkSchema();

  private LGWorkTraceSchema mLGWorkTraceSchema = new LGWorkTraceSchema();

  private MMap map = new MMap();

  /** 全局参数 */
  private GlobalInput mGlobalInput = new GlobalInput();

  /** 统一更新日期 */
  private String mCurrentDate = PubFun.getCurrentDate();

  /** 统一更新时间 */
  private String mCurrentTime = PubFun.getCurrentTime();

  public TaskPrepareInputBL() {
  }

  /**
   * 数据提交的公共方法
   * @param: cInputData 传入的数据
   * @param: cOperate   数据操作字符串
   * @return: boolean
   */
  public boolean submitData(VData cInputData, String cOperate) {
    // 将传入的数据拷贝到本类中
    mInputData = (VData) cInputData.clone();
    this.mOperate = cOperate;
    System.out.println("now in TaskInputBL submit");
    // 将外部传入的数据分解到本类的属性中，准备处理
    if (this.getInputData() == false) {
      return false;
    }
    System.out.println("---getInputData---");

    // 根据业务逻辑对数据进行处理
    if (this.dealData() == false) {
      return false;
    }
    System.out.println("---dealDate---");

    // 装配处理好的数据，准备给后台进行保存
    this.prepareOutputData();
    System.out.println("---prepareOutputData---");

    PubSubmit tPubSubmit = new PubSubmit();
    System.out.println("Start tPRnewManualDunBLS Submit...");

    if (!tPubSubmit.submitData(mInputData, mOperate)) {
      // @@错误处理
      this.mErrors.copyAllErrors(tPubSubmit.mErrors);

      CError tError = new CError();
      tError.moduleName = "TaskInputBL";
      tError.functionName = "submitData";
      tError.errorMessage = "数据提交失败!";

      this.mErrors.addOneError(tError);
      return false;
    }
    return true;
  }

  /**
   * 将外部传入的数据分解到本类的属性中
   * @param: 无
   * @return: boolean
   */
  private boolean getInputData() {
    mGlobalInput.setSchema( (GlobalInput) mInputData.
                           getObjectByObjectName("GlobalInput", 0));
    mLGWorkSchema.setSchema( (LGWorkSchema) mInputData.
                            getObjectByObjectName("LGWorkSchema", 0));
    return true;
  }

  /**
   * 校验传入的数据
   * @param: 无
   * @return: boolean
   */
  private boolean checkData() {
    return true;
  }

  /**
   * 根据业务逻辑对数据进行处理
   * @param: 无
   * @return: boolean
   */
  private boolean dealData() {
    String sql;
    SSRS tSSRS = new SSRS();
    ExeSQL tExeSQL = new ExeSQL();
    String tWorkNo;
    String tWorkBoxNo;
    String tAcceptorNo = mGlobalInput.Operator;
    String tAcceptCom;

    //得到受理机构
    sql = "select GroupNo from LGGroupMember " +
          "where  MemberNo = '" + tAcceptorNo + "' ";
    tSSRS = tExeSQL.execSQL(sql);
    tAcceptCom = tSSRS.GetText(1, 1);

    //产生作业号和受理号,作业号和受理号相同,都为日期加上流水号
    String tDate = mLGWorkSchema.getAcceptDate();
    tWorkNo = tDate.substring(0, 4) + tDate.substring(5, 7) + tDate.substring(8, 10)
        + PubFun1.CreateMaxNo("TASK" + tDate, 6);

    //得到用户信箱号
    sql = "select WorkBoxNo from LGWorkBox " +
          "where  OwnerTypeNo = '2' " + //个人信箱
          "and    OwnerNo = '" + mGlobalInput.Operator + "'";
    tSSRS = tExeSQL.execSQL(sql);
    tWorkBoxNo = tSSRS.GetText(1, 1);

    //设置数据
    mLGWorkSchema.setWorkNo(tWorkNo);
    mLGWorkSchema.setAcceptNo(tWorkNo);
    mLGWorkSchema.setNodeNo("0"); //当前是起始结点
    mLGWorkSchema.setStatusNo("2"); //设置为未经办状态
    mLGWorkSchema.setAcceptorNo(tAcceptorNo);
    mLGWorkSchema.setAcceptCom(tAcceptCom);
    mLGWorkSchema.setMakeDate(mCurrentDate);
    mLGWorkSchema.setMakeTime(mCurrentTime);
    mLGWorkSchema.setModifyDate(mCurrentDate);
    mLGWorkSchema.setModifyTime(mCurrentTime);
    mLGWorkSchema.setOperator(mGlobalInput.Operator);
    map.put(mLGWorkSchema, "INSERT"); //插入

    mLGWorkTraceSchema.setWorkNo(tWorkNo);
    mLGWorkTraceSchema.setNodeNo("0"); //起始结点
    mLGWorkTraceSchema.setWorkBoxNo(tWorkBoxNo); //信箱编号
    mLGWorkTraceSchema.setInMethodNo("0"); //新单录入
    mLGWorkTraceSchema.setInDate(mCurrentDate);
    mLGWorkTraceSchema.setInTime(mCurrentTime);
    mLGWorkTraceSchema.setSendComNo("");
    mLGWorkTraceSchema.setSendPersonNo("");
    mLGWorkTraceSchema.setMakeDate(mCurrentDate);
    mLGWorkTraceSchema.setMakeTime(mCurrentTime);
    mLGWorkTraceSchema.setModifyDate(mCurrentDate);
    mLGWorkTraceSchema.setModifyTime(mCurrentTime);
    mLGWorkTraceSchema.setOperator(mGlobalInput.Operator);
    map.put(mLGWorkTraceSchema, "INSERT"); //插入

    return true;
  }

  /**
   * 根据业务逻辑对数据进行处理
   * @param: 无
   * @return: void
   */
  private void prepareOutputData() {
    mInputData.clear();
    mResult.clear();
    mResult.add(map);
  }

  /**
   * 得到处理后的结果集
   * @return 结果集
   */
  public VData getResult() {
    return mResult;
  }
}

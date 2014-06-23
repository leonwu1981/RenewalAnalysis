/**
 * Copyright (c) 2005 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.*;

/**
 * <p>Title: 工作流团单返回复核服务类 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author chenhq
 * @version 1.0
 */

public class GrpUWBackApproveAfterInitService implements AfterInitService
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 往界面传输数据的容器 */
    private VData mResult = new VData();

    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();

    private TransferData mTransferData = new TransferData();
    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();

    /** 数据操作字符串 */
    private String mOperator;

    /** 业务数据操作字符串 */
    private String mGrpContNo;
    private String mMissionID;

    /** 修改复核修改的表 */
    private String mPolSql;
    private String mContSql;
    private String mGrpPolSql;
    private String mGrpContSql;
    private String mGCUWMasterSql;
    private String mGUWMasterSql;
    private String mGCUWErrorSql;
    private String mGUWErrorSql;
    private String mGCUWSubSql;
    private String mGUWSubSql;
    private String mCUWMasterSql;
    private String mUWMasterSql;
    private String mCUWErrorSql;
    private String mUWErrorSql;
    private String mCUWSubSql;
    private String mUWSubSql;
    private String mDelWorkFlowSql;

    private LCGCUWErrorTraceSet mLCGCUWErrorTraceSet = new LCGCUWErrorTraceSet();
    private LCGUWErrorTraceSet mLCGUWErrorTraceSet = new LCGUWErrorTraceSet();
    private LCCUWErrorTraceSet mLCCUWErrorTraceSet = new LCCUWErrorTraceSet();
    private LCUWErrorTraceSet mLCUWErrorTraceSet = new LCUWErrorTraceSet();
    
    public GrpUWBackApproveAfterInitService()
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

        //校验数据
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

        System.out.println("Start  Submit...");
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

        map.put(mPolSql, "UPDATE");
        map.put(mContSql, "UPDATE");
        map.put(mGrpPolSql, "UPDATE");
        map.put(mGrpContSql, "UPDATE");
        map.put(mGCUWMasterSql, "DELETE");
        map.put(mGUWMasterSql, "DELETE");
        map.put(mGCUWErrorSql, "DELETE");
        map.put(mGUWErrorSql, "DELETE");
        map.put(mGCUWSubSql, "DELETE");
        map.put(mGUWSubSql, "DELETE");
        map.put(mCUWMasterSql, "DELETE");
        map.put(mUWMasterSql, "DELETE");
        map.put(mCUWErrorSql, "DELETE");
        map.put(mUWErrorSql, "DELETE");
        map.put(mCUWSubSql, "DELETE");
        map.put(mUWSubSql, "DELETE");
        map.put(mDelWorkFlowSql, "DELETE");
        map.put(mLCGCUWErrorTraceSet, "DELETE&INSERT");
        map.put(mLCGUWErrorTraceSet, "DELETE&INSERT");
        map.put(mLCCUWErrorTraceSet, "DELETE&INSERT");
        map.put(mLCUWErrorTraceSet, "DELETE&INSERT");

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
        LCGrpContDB tLCGrpContDB = new LCGrpContDB();
        tLCGrpContDB.setGrpContNo(mGrpContNo);
        if (!tLCGrpContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mGrpContNo + "信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCGrpContSchema.setSchema(tLCGrpContDB);

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
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据Operate失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的任务ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的GrpContNo
        mGrpContNo = (String) mTransferData.getValueByName("GrpContNo");
        if (mGrpContNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中GrpContNo失败!";
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
      String tCurrentDate = PubFun.getCurrentDate();
      String tCurrentTime = PubFun.getCurrentTime();

//修改险种保单表 mPolSql
      StringBuffer tSBql = new StringBuffer(128);
      tSBql.append("update LCPol set ApproveCode = '',");
      tSBql.append("ApproveDate = '',");
      tSBql.append("ApproveTime = '',");
      tSBql.append("ApproveFlag = '0',");
      tSBql.append("uwcode = '',");
      tSBql.append("uwflag = '0',");
      tSBql.append("uwdate = '',");
      tSBql.append("uwtime = '',");
      tSBql.append("ModifyDate = '");
      tSBql.append(tCurrentDate);
      tSBql.append("',ModifyTime = '");
      tSBql.append(tCurrentTime);
      tSBql.append("' where GrpContNo = '");
      tSBql.append(mGrpContNo);
      tSBql.append("'");
      mPolSql = tSBql.toString();

//修改合同表 mContSql
      tSBql = new StringBuffer(128);
      tSBql.append("update LCCont set ApproveCode = '',");
      tSBql.append("ApproveDate = '',");
      tSBql.append("ApproveTime = '',");
      tSBql.append("ApproveFlag = '0',");
      tSBql.append("uwoperator = '',");
      tSBql.append("uwflag = '0',");
      tSBql.append("uwdate = '',");
      tSBql.append("uwtime = '',");
      tSBql.append("ModifyDate = '");
      tSBql.append(tCurrentDate);
      tSBql.append("',ModifyTime = '");
      tSBql.append(tCurrentTime);
      tSBql.append("' where GrpContNo = '");
      tSBql.append(mGrpContNo);
      tSBql.append("'");
      mContSql = tSBql.toString();

//修改集体险种表 mGrpPolSql
      tSBql = new StringBuffer(128);
      tSBql.append("update LCGrpPol set ApproveCode = '',");
      tSBql.append("ApproveDate = '',");
      tSBql.append("ApproveTime = '',");
      tSBql.append("ApproveFlag = '0',");
      tSBql.append("uwoperator = '',");
      tSBql.append("uwflag = '0',");
      tSBql.append("uwdate = '',");
      tSBql.append("uwtime = '',");
      tSBql.append("ModifyDate = '");
      tSBql.append(tCurrentDate);
      tSBql.append("',ModifyTime = '");
      tSBql.append(tCurrentTime);
      tSBql.append("' where GrpContNo = '");
      tSBql.append(mGrpContNo);
      tSBql.append("'");
      mGrpPolSql = tSBql.toString();

//修改集体合同表 mGrpContSql
      tSBql = new StringBuffer(128);
      tSBql.append("update LCGrpCont set ApproveCode = '',");
      tSBql.append("ApproveDate = '',");
      tSBql.append("ApproveTime = '',");
      tSBql.append("ApproveFlag = '0',");
      tSBql.append("uwoperator = '',");
      tSBql.append("uwflag = '0',");
      tSBql.append("uwdate = '',");
      tSBql.append("uwtime = '',");
      tSBql.append("ModifyDate = '");
      tSBql.append(tCurrentDate);
      tSBql.append("',ModifyTime = '");
      tSBql.append(tCurrentTime);
      tSBql.append("' where GrpContNo = '");
      tSBql.append(mGrpContNo);
      tSBql.append("'");
      mGrpContSql = tSBql.toString();

//团体核保最近结果表 mGCUWMasterSql
      tSBql = new StringBuffer(128);
      tSBql.append("delete LCGCUWMaster where GrpContNo='");
      tSBql.append(mGrpContNo);
      tSBql.append("'");
      mGCUWMasterSql = tSBql.toString();

//集体核保最近结果表 mGUWMasterSql
      tSBql = new StringBuffer(128);
      tSBql.append("delete LCGUWMaster where GrpContNo='");
      tSBql.append(mGrpContNo);
      tSBql.append("'");
      mGUWMasterSql = tSBql.toString();

//团体核保错误表 mGCUWErrorSql
      tSBql = new StringBuffer(128);
      tSBql.append("delete LCGCUWError where GrpContNo='");
      tSBql.append(mGrpContNo);
      tSBql.append("'");
      mGCUWErrorSql = tSBql.toString();

//集体核保错误表 mGUWErrorSql
      tSBql = new StringBuffer(128);
      tSBql.append("delete LCGUWError where GrpContNo='");
      tSBql.append(mGrpContNo);
      tSBql.append("'");
      mGUWErrorSql = tSBql.toString();

//团体核保轨迹表 mGCUWSubSql
      tSBql = new StringBuffer(128);
      tSBql.append("delete LCGCUWSub where GrpContNo='");
      tSBql.append(mGrpContNo);
      tSBql.append("'");
      mGCUWSubSql = tSBql.toString();

//集体核保轨迹表 mGUWSubSql
      tSBql = new StringBuffer(128);
      tSBql.append("delete LCGUWSub where GrpContNo='");
      tSBql.append(mGrpContNo);
      tSBql.append("'");
      mGUWSubSql = tSBql.toString();

//核保最近结果表 mCUWMasterSql
            tSBql = new StringBuffer(128);
            tSBql.append("delete LCCUWMaster where GrpContNo='");
            tSBql.append(mGrpContNo);
            tSBql.append("'");
            mCUWMasterSql = tSBql.toString();

//险种核保最近结果表 mUWMasterSql
            tSBql = new StringBuffer(128);
            tSBql.append("delete LCUWMaster where GrpContNo='");
            tSBql.append(mGrpContNo);
            tSBql.append("'");
            mUWMasterSql = tSBql.toString();

//核保错误表 mCUWErrorSql
            tSBql = new StringBuffer(128);
            tSBql.append("delete LCCUWError where GrpContNo='");
            tSBql.append(mGrpContNo);
            tSBql.append("'");
            mCUWErrorSql = tSBql.toString();

//险种核保错误表 mUWErrorSql
            tSBql = new StringBuffer(128);
            tSBql.append("delete LCUWError where GrpContNo='");
            tSBql.append(mGrpContNo);
            tSBql.append("'");
            mUWErrorSql = tSBql.toString();

//核保轨迹表 mCUWSubSql
            tSBql = new StringBuffer(128);
            tSBql.append("delete LCCUWSub where GrpContNo='");
            tSBql.append(mGrpContNo);
            tSBql.append("'");
            mCUWSubSql = tSBql.toString();

//险种核保轨迹表 mUWSubSql
            tSBql = new StringBuffer(128);
            tSBql.append("delete LCUWSub where GrpContNo='");
            tSBql.append(mGrpContNo);
            tSBql.append("'");
            mUWSubSql = tSBql.toString();

//删除人工核保的节点      mDelWorkFlowSql
      tSBql = new StringBuffer(128);
      tSBql.append("delete lwmission where activityid='0000002004' and missionid='");
      tSBql.append(mMissionID);
      tSBql.append("'");
      mDelWorkFlowSql = tSBql.toString();
      
      //ASR20105425-见费出单系统修改（核保部分）  存储之前下过通过核保结论(uwpassflag为4，9，B)的信息 add by frost
      String tSql1 = "select * from lcgcuwerror where grpcontno='"+ mGrpContNo +"' and uwpassflag in ('4','9','B')";
      String tSql2 = "select * from lcguwerror where grpcontno='"+ mGrpContNo +"' and uwpassflag in ('4','9','B')";
      String tSql3 = "select * from Lccuwerror where grpcontno='"+ mGrpContNo +"' and uwpassflag in ('4','9','B')";
      String tSql4 = "select * from Lcuwerror where grpcontno='"+ mGrpContNo +"' and uwpassflag in ('4','9','B')";
      
      String tMaxUwnoSql =  "select nvl(max(uwno),0)+1 from uwerrortrace_view where grpcontno='"+ mGrpContNo +"'";
      ExeSQL tExeSQL = new ExeSQL();
      String tMaxUwno = tExeSQL.getOneValue(tMaxUwnoSql);
      
      LCGCUWErrorDB tLCGCUWErrorDB = new LCGCUWErrorDB();
      LCGUWErrorDB tLCGUWErroreDB = new LCGUWErrorDB();
      LCCUWErrorDB tLCCUWErrorDB = new LCCUWErrorDB();
      LCUWErrorDB tLCUWErrorDB = new LCUWErrorDB();

      LCGCUWErrorSet tLCGCUWErrorSet = tLCGCUWErrorDB.executeQuery(tSql1);
      LCGUWErrorSet tLCGUWErroreSet = tLCGUWErroreDB.executeQuery(tSql2);
      LCCUWErrorSet tLCCUWErrorSet = tLCCUWErrorDB.executeQuery(tSql3);
      LCUWErrorSet tLCUWErrorSet = tLCUWErrorDB.executeQuery(tSql4);
      
      Reflections tReflections = new Reflections();
      for (int i = 1; i <= tLCGCUWErrorSet.size(); i++)
      {
    	  LCGCUWErrorTraceSchema aLCGCUWErrorTraceSchema = new LCGCUWErrorTraceSchema();
          LCGCUWErrorSchema aLCGCUWErrorSchema = tLCGCUWErrorSet.get(i);          
          tReflections.transFields(aLCGCUWErrorTraceSchema, aLCGCUWErrorSchema);
          aLCGCUWErrorTraceSchema.setUWNo(tMaxUwno);
          mLCGCUWErrorTraceSet.add(aLCGCUWErrorTraceSchema);
      }
      
      for (int i = 1; i <= tLCGUWErroreSet.size(); i++)
      {
    	  LCGUWErrorTraceSchema aLCGUWErrorTraceSchema = new LCGUWErrorTraceSchema();
    	  LCGUWErrorSchema aLCGUWErrorSchema = tLCGUWErroreSet.get(i);          
          tReflections.transFields(aLCGUWErrorTraceSchema, aLCGUWErrorSchema);
          aLCGUWErrorTraceSchema.setUWNo(tMaxUwno);
          mLCGUWErrorTraceSet.add(aLCGUWErrorTraceSchema);
      }
      
      for (int i = 1; i <= tLCCUWErrorSet.size(); i++)
      {
    	  LCCUWErrorTraceSchema aLCCUWErrorTraceSchema = new LCCUWErrorTraceSchema();
          LCCUWErrorSchema aLCCUWErrorSchema = tLCCUWErrorSet.get(i);          
          tReflections.transFields(aLCCUWErrorTraceSchema, aLCCUWErrorSchema);
          aLCCUWErrorTraceSchema.setUWNo(tMaxUwno);
          mLCCUWErrorTraceSet.add(aLCCUWErrorTraceSchema);
      }
      
      for (int i = 1; i <= tLCUWErrorSet.size(); i++)
      {
    	  LCUWErrorTraceSchema aLCUWErrorTraceSchema = new LCUWErrorTraceSchema();
          LCUWErrorSchema aLCUWErrorSchema = tLCUWErrorSet.get(i);          
          tReflections.transFields(aLCUWErrorTraceSchema, aLCUWErrorSchema);
          aLCUWErrorTraceSchema.setUWNo(tMaxUwno);
          mLCUWErrorTraceSet.add(aLCUWErrorTraceSchema);
      }

      return true;
    }

    /**
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
     * @return
     */
    private boolean prepareTransferData()
    {
        mTransferData.setNameAndValue("ProposalGrpContNo", mGrpContNo);
        mTransferData.setNameAndValue("PrtNo", mLCGrpContSchema.getPrtNo());
        mTransferData.setNameAndValue("SaleChnl", mLCGrpContSchema.getSaleChnl());
        mTransferData.setNameAndValue("ManageCom",mLCGrpContSchema.getManageCom());
        mTransferData.setNameAndValue("AgentCode", mLCGrpContSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup", mLCGrpContSchema.getAgentGroup());
        mTransferData.setNameAndValue("GrpName", mLCGrpContSchema.getGrpName());
        mTransferData.setNameAndValue("CValiDate",mLCGrpContSchema.getCValiDate());

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

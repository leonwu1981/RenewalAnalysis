package com.sinosoft.workflow.ca;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;
import com.sinosoft.lis.pubfun.PubFun1;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.llcase.ClientRegisterBackBL;
import com.sinosoft.lis.schema.LLCaseSchema;
import com.sinosoft.lis.schema.LLSurveySchema;
import com.sinosoft.workflow.ca.LLRgtSurveyInputjsService;


/**
 * <p>Title: 理赔工作流 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author pd
 * @version 1.0
 */
public class CaWorkFlowBL {
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();


    /** 往后面传输数据的容器 */
    private VData mInputData;

    VData tVData = new VData();


    /** 往界面传输数据的容器 */
    private VData mResult = new VData();
    private VData tResult = new VData();

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
    private String mSaveFlag;// 如果mSaveFlag为null或者值等于""或者"0",表示本次调用需要保存结果,否则,不保存,只返回结果

    public CaWorkFlowBL() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    
    
    public VData getmResult()
    {
    	return mResult;
    }
    /**
     * 传输数据的公共方法
     * @param: cInputData 输入的数据
     *         cOperate 数据操作
     * @return:
     */
    public boolean submitData(VData cInputData, String cOperate) {
        //得到外部传入的数据,将数据备份到本类中
        if (!getInputData(cInputData, cOperate)) {
            return false;
        }
        System.out.println("---CaWorkFlowBL getInputData---");
        // 数据操作业务处理
        if (!dealData()) {
            return false;
        }
        System.out.println("---CaWorkFlowBL dealData---");

        //准备给后台的数据
        if (!prepareOutputData()) {
            return false;
        }
        
        if(mSaveFlag==null || mSaveFlag.trim().equals("") || mSaveFlag.trim().equals("0"))
        {
        //数据提交
	        CaWorkFlowBLS tCaWorkFlowBLS = new CaWorkFlowBLS();
	        System.out.println("Start CaWorkFlowBL Submit...");
	
	        if (!tCaWorkFlowBLS.submitData(mResult, mOperate)) {
	            // @@错误处理
	            this.mErrors.copyAllErrors(tCaWorkFlowBLS.mErrors);
	
	            CError tError = new CError();
	            tError.moduleName = "CaWorkFlowBL";
	            tError.functionName = "submitData";
	            tError.errorMessage = "数据提交失败!";
	            this.mErrors.addOneError(tError);
	
	            return false;
	        }
        }
        System.out.println("---CaWorkFlowBLS commitData End ---");

        return true;
    }

    
    
    /**
     * 从输入数据中得到所有对象
     *输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean getInputData(VData cInputData, String cOperate) {
        //从输入数据中得到所有对象
        //获得全局公共数据
        mGlobalInput = ((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput",
                0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData",
                0);
        mInputData = cInputData;
        if (mGlobalInput == null) {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据失败!";
            this.mErrors.addOneError(tError);

            return false;
        }

        //获得操作员编码
        mOperater = mGlobalInput.Operator;
        if ((mOperater == null) || mOperater.trim().equals("")) {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据Operate失败!";
            this.mErrors.addOneError(tError);

            return false;
        }

        //获得登陆机构编码
        mManageCom = mGlobalInput.ManageCom;
        if ((mManageCom == null) || mManageCom.trim().equals("")) {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
            this.mErrors.addOneError(tError);

            return false;
        }

        mOperate = cOperate;
        if ((mOperate == null) || mOperate.trim().equals("")) {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据Operate任务节点编码失败!";
            this.mErrors.addOneError(tError);

            return false;
        }
        mSaveFlag=(String)mTransferData.getValueByName("SaveFlag");
        return true;
    }


    /**
     * 数据操作类业务处理
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean dealData() {

        if (mOperate.trim().equals("7699999999")) { //执行工作流待人工核保活动表任务
            if (!Execute7699999999()) {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("7899999999")) {
            if (!Execute7899999999()) {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("7799999999")) {
            if (!Execute7799999999()) {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("7599999999")) {
            if (!Execute7599999999()) {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("7499999999")) {
            if (!Execute7499999999()) {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("7999999999")) {
            if (!Execute7999999999()) {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("8099999999")) {
            if (!Execute8099999999()) {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("8199999999")) {
            if (!Execute8199999999()) {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("8299999999")) {
            if (!Execute8299999999()) {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("6999999999")) {
            if (!Execute6999999999()) {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("7299999999")) {
            if (!Execute7299999999()) {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("9099999999")) {
            if (!Execute9099999999()) {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("9199999999")) {
            if (!Execute9199999999()) {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else {
            if (!Execute()) {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        return true;
    }


    /**
     * 执行承保工作流待人工核保活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute() {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();

        //获得当前工作任务的任务ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        System.out.println("CAFlag===" + mTransferData.getValueByName("CAFlag"));
        System.out.println("CaseNo===" + mTransferData.getValueByName("CaseNo"));
        System.out.println("RptorName===" +
                           mTransferData.getValueByName("RptorName"));
        System.out.println("MissionID===" +
                           mTransferData.getValueByName("MissionID"));
        System.out.println("SubMissionID===" +
                           mTransferData.getValueByName("SubMissionID"));
        System.out.println("ManageCom===" +
                           mTransferData.getValueByName("ManageCom"));
        System.out.println("Operator===" +
                           mTransferData.getValueByName("Operator"));
        System.out.println("RgtState===" +
                           mTransferData.getValueByName("RgtState"));
        System.out.println("InputDate===" +
                           mTransferData.getValueByName("InputDate"));

        System.out.println(
                "-------------------------------------------------------------");
        if (tMissionID == null) {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute0000000000";
            tError.errorMessage = "前台传输数据TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);

            return false;
        }

        if (tSubMissionID == null) {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute0000000000";
            tError.errorMessage = "前台传输数据TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);

            return false;
        }

        try {
            //调用Service
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  mOperate, mInputData)) {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);

                return false;
            }
            //获得执行理赔保活动表任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData != null) {
                for (int i = 0; i < tVData.size(); i++) {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    mResult.add(tempVData);
                }
            }
        } catch (Exception ex) {
            // @@错误处理

            this.mErrors.copyAllErrors(mActivityOperator.mErrors);

            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎执行理赔活动表ExecuteMission任务出错!";
            this.mErrors.addOneError(tError);

            return false;
        }
        try {
            //产生执行完理赔保活动表任务后的任务节点
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    mOperate, mInputData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if ((tempVData != null) && (tempVData.size() > 0)) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            } else {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                System.out.println("Mission Test");
                return false;
            }
        } catch (Exception ex) {
            // @@错误处理

            this.mErrors.copyAllErrors(mActivityOperator.mErrors);

            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎执行理赔活动表CreateNextMission任务出错!";
            this.mErrors.addOneError(tError);

            return false;
        }
        try {
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                mOperate, mInputData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if ((tempVData != null) && (tempVData.size() > 0)) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            } else {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);

                return false;
            }
        } catch (Exception ex) {
            // @@错误处理

            this.mErrors.copyAllErrors(mActivityOperator.mErrors);

            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎执行理赔活动表DeleteMission任务出错!";
            this.mErrors.addOneError(tError);

            return false;
        }

        //*/
        return true;
    }

    /**
     * 执行理赔工作流理赔保活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * 生成团体受理节点
     */
    private boolean Execute7499999999() { //*
        mResult.clear();

        ActivityOperator tActivityOperator = new ActivityOperator();

        try {
            if (!preparedRptNo()) {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);

                CError tError = new CError();
                tError.moduleName = "CaWorkFlowBL";
                tError.functionName = "preparedCaseNO";
                tError.errorMessage = "生成团体批次号出错!";
                this.mErrors.addOneError(tError);
            }
            //产生执行完理赔工作流理赔活动表任务后的任务节点
            if (tActivityOperator.CreateStartMission("0000000005", "0000005012",
                    tVData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        } catch (Exception ex) {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute7499999999";
            tError.errorMessage = "工作流引擎执行理赔工作流报案任务出错!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/

        return true;

    }

    /**
     * 执行理赔工作流理赔保活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * 条件判断 1 生成帐单录入  2 生成检录
     */
    private boolean Execute7599999999() { //*
        mResult.clear();
        ActivityOperator tActivityOperator = new ActivityOperator();
        try {
            //产生执行完理赔工作流理赔活动表任务后的任务节点
            String CAFlag = (String) mTransferData.getValueByName("CAFlag");
            if (CAFlag.equals("1")) {
                if (tActivityOperator.CreateStartMission("0000000005",
                        "0000005004", mInputData)) {
                    VData tempVData = new VData();
                    tempVData = tActivityOperator.getResult();
                    if (tempVData != null && tempVData.size() > 0) {
                        mResult.add(tempVData);
                        tempVData = null;
                    }
                }
            } else if (CAFlag.equals("2")) {
                if (tActivityOperator.CreateStartMission("0000000005",
                        "0000005005", mInputData)) {
                    VData tempVData = new VData();
                    tempVData = tActivityOperator.getResult();
                    if (tempVData != null && tempVData.size() > 0) {
                        mResult.add(tempVData);
                        tempVData = null;
                    }
                }
            }
        } catch (Exception ex) {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute7599999999";
            tError.errorMessage = "工作流引擎执行理赔工作流报案任务出错!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/

        return true;

    }

    /**
     * 执行理赔工作流理赔保活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * 生成报案节点
     */
    private boolean Execute7699999999() { //*
        mResult.clear();

        ActivityOperator tActivityOperator = new ActivityOperator();

        try {
            if (!preparedCaseNO()) {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);

                CError tError = new CError();
                tError.moduleName = "CaWorkFlowBL";
                tError.functionName = "preparedCaseNO";
                tError.errorMessage = "生成案件号出错!";
                this.mErrors.addOneError(tError);
            }
            //产生执行完理赔工作流理赔活动表任务后的任务节点

            if (tActivityOperator.CreateStartMission("0000000005", "0000005001",
                    tVData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        } catch (Exception ex) {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute7699999999";
            tError.errorMessage = "工作流引擎执行理赔工作流报案任务出错!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/

        return true;

    }

    /**
     * 执行理赔工作流调查保活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * 生成调查节点
     */
    private boolean Execute7899999999() { //*
        mResult.clear();

        ActivityOperator tActivityOperator = new ActivityOperator();
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        String tActivityID = (String) mTransferData.getValueByName("ActivityID");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("CAFlag===" + mTransferData.getValueByName("CAFlag"));
        System.out.println("ActivityID===" + mTransferData.getValueByName("ActivityID"));
        System.out.println("CaseNo===" + mTransferData.getValueByName("CaseNo"));
        System.out.println("RptorName===" +
                           mTransferData.getValueByName("RptorName"));
        System.out.println("MissionID===" +
                           mTransferData.getValueByName("MissionID"));
        System.out.println("SubMissionID===" +
                           mTransferData.getValueByName("SubMissionID"));
        System.out.println("ManageCom===" +
                           mTransferData.getValueByName("ManageCom"));
        System.out.println("Operator===" +
                           mTransferData.getValueByName("Operator"));
        System.out.println("RgtState===" +
                           mTransferData.getValueByName("RgtState"));
        System.out.println("InputDate===" +
                           mTransferData.getValueByName("InputDate"));
        System.out.println("SurveyNo===" +
                           mTransferData.getValueByName("SurveyNo"));
        System.out.println("SurveyReturn===" +
                           mTransferData.getValueByName("SurveyReturn"));
        try {
            //调用Service
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  tActivityID, mInputData)) {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);

                return false;
            }
            //获得执行理赔保活动表任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData != null) {
                for (int i = 0; i < tVData.size(); i++) {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    mResult.add(tempVData);
                }
            }
        } catch (Exception ex) {
            // @@错误处理

            this.mErrors.copyAllErrors(mActivityOperator.mErrors);

            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎执行理赔活动表ExecuteMission任务出错!";
            this.mErrors.addOneError(tError);

            return false;
        }
        VData aVData = new VData();
        //LLRgtSurveyInputjsService tLLRgtSurveyInputjsService = new LLRgtSurveyInputjsService();
        //tVData = LLRgtSurveyInputjsService.getResult();
        //String SurveyNo = LLRgtSurveyInputjsService.getSurveyNo;
        LLSurveySchema mLLSurveySchema = new LLSurveySchema();
        // tVData.getObjectByObjectName("VDate",1).get
        aVData = (VData) tVData.get(0);
        aVData = (VData) aVData.get(0);
        mLLSurveySchema = (LLSurveySchema) aVData.getObjectByObjectName(
                "LLSurveySchema", 0);
        String SurveyNo = mLLSurveySchema.getSurveyNo();
        //mLLSurveySchema.setSchema(( LLSurveySchema )tVData.getObjectByObjectName( "LLSurveySchema", 0 ));
        System.out.println("SurveyNo3===" + SurveyNo);

        //VData bVData = new VData();
        //LLSurveySchema mLLSurveySchema = new LLSurveySchema();
        //mLLSurveySchema = (LLSurveySchema) aVData.getObjectByObjectName(
        //        "LLSurveySchema", 0);
        //mTransferData = (TransferData)SurveyNo.subSequence(0, 15);
        mTransferData.setNameAndValue("SurveyNo",SurveyNo);
        System.out.println("SurveyNo4===" + SurveyNo);
        mInputData.add(mTransferData);

        try {
            //产生执行完理赔工作流调查活动表任务后的任务节点

            if (tActivityOperator.CreateStartMission("0000000005", "0000005010",
                    mInputData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        } catch (Exception ex) {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute7899999999";
            tError.errorMessage = "工作流引擎执行理赔工作流报案任务出错!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/
        return true;
    }

//删除调查结束后的节点
    private boolean Execute7799999999() { //*
        mResult.clear();

        ActivityOperator tActivityOperator = new ActivityOperator();
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");

        try {
            //调用Service
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000005011", mInputData)) {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);

                return false;
            }
            //获得执行理赔保活动表任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData != null) {
                for (int i = 0; i < tVData.size(); i++) {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    mResult.add(tempVData);
                }
            }
        } catch (Exception ex) {
            // @@错误处理

            this.mErrors.copyAllErrors(mActivityOperator.mErrors);

            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎执行理赔活动表ExecuteMission任务出错!";
            this.mErrors.addOneError(tError);

            return false;
        }

        try {

            //删除执行完调查工作流调查活动表任务后的任务节点
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000005011", mInputData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        } catch (Exception ex) {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute7799999999";
            tError.errorMessage = "工作流引擎执行理赔工作流报案任务出错!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/
        return true;
    }

    //个人给付确认后的删除
    private boolean Execute7999999999() { //*
        mResult.clear();

        ActivityOperator tActivityOperator = new ActivityOperator();
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        try {

            //删除执行完调查工作流调查活动表任务后的任务节点
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000005009", mInputData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        } catch (Exception ex) {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute7999999999";
            tError.errorMessage = "工作流引擎执行理赔工作流报案任务出错!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/
        return true;
    }

    //团体受理确认后的删除
    private boolean Execute8099999999() { //*
        mResult.clear();

        ActivityOperator tActivityOperator = new ActivityOperator();
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        try {

            //删除执行完调查工作流调查活动表任务后的任务节点
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000005012", mInputData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        } catch (Exception ex) {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute8099999999";
            tError.errorMessage = "工作流引擎执行理赔工作流报案任务出错!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/
        return true;
    }

    //生成团体给付节点
    private boolean Execute8199999999() { //*
        mResult.clear();

        ActivityOperator tActivityOperator = new ActivityOperator();

        try {

            //产生执行完理赔工作流调查活动表任务后的任务节点

            if (tActivityOperator.CreateStartMission("0000000005", "0000005013",
                    mInputData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        } catch (Exception ex) {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute8199999999";
            tError.errorMessage = "工作流引擎执行理赔工作流报案任务出错!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/
        return true;
    }

    //团体给付确认后的删除
    private boolean Execute8299999999() { //*
        mResult.clear();

        ActivityOperator tActivityOperator = new ActivityOperator();
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        try {

            //删除执行完调查工作流调查活动表任务后的任务节点
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000005013", mInputData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        } catch (Exception ex) {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute8299999999";
            tError.errorMessage = "工作流引擎执行理赔工作流报案任务出错!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/
        return true;
    }

    private boolean Execute6999999999() {
        tResult.clear();
        VData tVData = new VData();
        //错误申诉完毕校验
        ClientRegisterBackBL tClientRegisterBackBL = new ClientRegisterBackBL();
        String strOperate = (String) mTransferData.getValueByName("strOperate");
        if (tClientRegisterBackBL.submitData(mInputData, strOperate)) {
            tVData = tClientRegisterBackBL.getResult();
            tResult.add(tVData);
        } else {
            this.mErrors.copyAllErrors(tClientRegisterBackBL.mErrors);
            return false;
        }
        ActivityOperator tActivityOperator = new ActivityOperator();
        LLCaseSchema mLLCaseSchema = new LLCaseSchema();
        mLLCaseSchema.setSchema((LLCaseSchema) tVData.getObjectByObjectName(
                "LLCaseSchema", 0));
        System.out.println("CaseNo ==== " + mLLCaseSchema.getCaseNo());
        mTransferData.setNameAndValue("CaseNo", mLLCaseSchema.getCaseNo());
        tResult.add(mLLCaseSchema);
        mInputData.add(mTransferData);
        //查询是否需要帐单录入的数据
        String sql = "select count(*) from LLAffix where caseno = '" +
                     mLLCaseSchema.getCaseNo() +
                     "' and affixcode in('1304','1305','1314','1316','1317')";
        ExeSQL exesql = new ExeSQL();
        SSRS ssrs = exesql.execSQL(sql);
        String CAFlag = "";
        if (ssrs.getMaxRow() > 0) {
            CAFlag = "1";
        } else {
            CAFlag = "2";
        }
        try {
            if (CAFlag.equals("1")) {
                if (tActivityOperator.CreateStartMission("0000000005",
                        "0000005004", mInputData)) {
                    VData tempVData = new VData();
                    tempVData = tActivityOperator.getResult();
                    if (tempVData != null && tempVData.size() > 0) {
                        mResult.add(tempVData);
                        tempVData = null;
                    }
                }
            } else if (CAFlag.equals("2")) {
                if (tActivityOperator.CreateStartMission("0000000005",
                        "0000005005", mInputData)) {
                    VData tempVData = new VData();
                    tempVData = tActivityOperator.getResult();
                    if (tempVData != null && tempVData.size() > 0) {
                        mResult.add(tempVData);
                        tempVData = null;
                    }
                }
            }
        } catch (Exception ex) {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "GrpTbWorkFlowBL";
            tError.functionName = "Execute6999999999";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

   //理赔直接立案创建工作流节点
    private boolean Execute7299999999() {
         mResult.clear();
         ActivityOperator tActivityOperator = new ActivityOperator();
         tVData.add(mGlobalInput);
         tVData.add(mTransferData);

         try {
         //产生执行完理赔工作流理赔活动表任务后的任务节点
            if (tActivityOperator.CreateStartMission("0000000005",
                            "0000005002",tVData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
         }
         catch (Exception ex)
         {
             // @@错误处理
             this.mErrors.copyAllErrors(mActivityOperator.mErrors);
             CError tError = new CError();
             tError.moduleName = "CaWorkFlowBL";
             tError.functionName = "Execute7699999999";
             tError.errorMessage = "工作流引擎执行理赔工作流报案任务出错!";
             this.mErrors.addOneError(tError);
             return false;
          }
          return true;
    }

    //团体批量理赔立案创建工作流节点
    private boolean Execute9099999999() {
        mResult.clear();
        ActivityOperator tActivityOperator = new ActivityOperator();
        tVData.add(mGlobalInput);
        tVData.add(mTransferData);

        try {
         //产生执行完理赔工作流理赔活动表任务后的任务节点
            if (tActivityOperator.CreateStartMission("0000000005",
                            "0000005101",tVData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        }
        catch (Exception ex)
        {
             // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute9099999999";
            tError.errorMessage = "工作流引擎执行团体理赔工作流立案任务出错!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }    
    
    //团体理赔结案后删除节点
    private boolean Execute9199999999() { 
        mResult.clear();

        ActivityOperator tActivityOperator = new ActivityOperator();
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");

        try {
            //调用Service
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000005105", mInputData)) 
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            //获得执行理赔保活动表任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData != null) {
                for (int i = 0; i < tVData.size(); i++) {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    mResult.add(tempVData);
                }
            }
        } catch (Exception ex) {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎执行理赔活动表ExecuteMission任务出错!";
            this.mErrors.addOneError(tError);
            return false;
        }
        try {

            //删除执行完调查工作流调查活动表任务后的任务节点
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000005105", mInputData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        } catch (Exception ex) {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute9199999999";
            tError.errorMessage = "工作流引擎执行理赔工作流报案任务出错!";
            this.mErrors.addOneError(tError);
            return false;
        } 
        return true;
    }   

    /**
     * 准备需要保存的数据
     */
    private boolean prepareOutputData() {
        //mInputData.add( mGlobalInput );
        return true;
    }

    //生成case号码
    private boolean preparedCaseNO() {
        String tManageCom = mGlobalInput.ManageCom;
        String tRgtNo = PubFun1.CreateMaxNo("RgtNo", tManageCom);
 
        String tLimit = PubFun.getNoLimit(mGlobalInput.ManageCom);
        TransferData tempTransferData = new TransferData();
        VData tempVDate = new VData();
        tempTransferData.setNameAndValue("RGTNO", tRgtNo);//总案立案号
        tempVDate.add(tempTransferData);
        String tCaseNo = PubFun1.CreateMaxNo("CASENO", tLimit, tempVDate);//个人赔案号
        
        System.out.println("报案号" + tCaseNo);
        tVData.add(mGlobalInput);
        mTransferData.setNameAndValue("CaseNo", tCaseNo);
        tVData.add(mTransferData);
        return true;
    }

    //生成RptNo号码
    private boolean preparedRptNo() {
        String tLimit = PubFun.getNoLimit(this.mGlobalInput.ManageCom);
        System.out.println("管理机构代码是 : " + tLimit);
        
        //String RGTNO = PubFun1.CreateMaxNo("RGTNO", tLimit);
        //中意用
        String RGTNO = PubFun1.CreateMaxNo("RGTNO", this.mGlobalInput.ManageCom);
        System.out.println("团体批次号 :" + RGTNO);
        tVData.add(mGlobalInput);
        mTransferData.setNameAndValue("RptNo", RGTNO);
        tVData.add(mTransferData);
        return true;
    }

    public String getCaseNo() {
        String tCaseNo = (String) mTransferData.getValueByName("CaseNo");
        return tCaseNo;
    }

    public String getRptNo() {
        String tRptNo = (String) mTransferData.getValueByName("RptNo");
        return tRptNo;
    }

    public VData getResult() {
        return tResult;
    }

    private void jbInit() throws Exception {
    }
}

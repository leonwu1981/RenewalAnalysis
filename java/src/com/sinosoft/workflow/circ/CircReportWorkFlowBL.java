/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.circ;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;
import com.sinosoft.workflowengine.ActivityOperator;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class CircReportWorkFlowBL
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

    public CircReportWorkFlowBL()
    {
    }

    /**
     * 传输数据的公共方法
     * @param cInputData VData 输入的数据
     * @param cOperate String 数据操作
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //得到外部传入的数据,将数据备份到本类中
        if (!getInputData(cInputData, cOperate))
        {
            return false;
        }

        // 数据操作业务处理
        if (!dealData())
        {
            return false;
        }

        System.out.println("---CircReportWorkFlowBL dealData---");

        //准备给后台的数据
        if (!prepareOutputData())
        {
            return false;
        }

        System.out.println("---CircReportWorkFlowBL prepareOutputData---");

        //数据提交
        CircReportWorkFlowBLS tCircReportWorkFlowBLS = new
                CircReportWorkFlowBLS();
        System.out.println("Start CircReportWorkFlowBL Submit...");

        if (!tCircReportWorkFlowBLS.submitData(mResult, mOperate))
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tCircReportWorkFlowBLS.mErrors);

            CError tError = new CError();
            tError.moduleName = "CircReportWorkFlowBL";
            tError.functionName = "submitData";
            tError.errorMessage = "数据提交失败!";
            this.mErrors.addOneError(tError);

            return false;
        }

        System.out.println("---CircReportWorkFlowBLS commitData End ---");

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
        mGlobalInput = ((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput",
                0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData",
                0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CircReportWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据失败!";
            this.mErrors.addOneError(tError);

            return false;
        }

        //获得操作员编码
        mOperater = mGlobalInput.Operator;
        if ((mOperater == null) || mOperater.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CircReportWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据Operate失败!";
            this.mErrors.addOneError(tError);

            return false;
        }

        //获得登陆机构编码
        mManageCom = mGlobalInput.ManageCom;
        if ((mManageCom == null) || mManageCom.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CircReportWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
            this.mErrors.addOneError(tError);

            return false;
        }

        mOperate = cOperate;
        if ((mOperate == null) || mOperate.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CircReportWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据Operate任务节点编码失败!";
            this.mErrors.addOneError(tError);

            return false;
        }

        return true;
    }

    /**
     * 数据操作类业务处理
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @return boolean
     */
    private boolean dealData()
    {
        //执行保全工作流发核保通知书活动表任务
        if (!Execute())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            return false;
        }

        return true;
    }

    /**
     * 执行保全工作流待人工核保活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @return boolean
     */
    private boolean Execute()
    {
        mResult.clear();

        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();

        //获得当前工作任务的任务ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        if (tMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "Execute0000000000";
            tError.errorMessage = "前台传输数据TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);

            return false;
        }

        if (tSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "Execute0000000000";
            tError.errorMessage = "前台传输数据TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);

            return false;
        }

        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  mOperate, mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);

                return false;
            }

            //获得执行保全工作流待人工核保活动表任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    mResult.add(tempVData);
                }
            }

            //产生执行完保全工作流待人工核保活动表任务后的任务节点
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    mOperate, mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if ((tempVData != null) && (tempVData.size() > 0))
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);

                return false;
            }

            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                mOperate, mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if ((tempVData != null) && (tempVData.size() > 0))
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);

                return false;
            }
        }
        catch (Exception ex)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);

            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎执行保监会报表活动表任务出错!";
            this.mErrors.addOneError(tError);

            return false;
        }
        return true;
    }

    /**
     * 准备需要保存的数据
     * @return boolean
     */
    private static boolean prepareOutputData()
    {
        return true;
    }
}

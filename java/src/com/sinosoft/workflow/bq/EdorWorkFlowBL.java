package com.sinosoft.workflow.bq;

import java.lang.*;
import java.util.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.vdb.*;
import com.sinosoft.utility.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.workflow.bq.*;
import com.sinosoft.workflowengine.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * ReWrite ZhangRong,FanX
 * @version 1.0
 */

public class EdorWorkFlowBL
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 往后面传输数据的容器 */
    private VData mInputData;

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
    private String mOperate;


    /** 体检资料主表 */
    private LPPENoticeSet mLPPENoticeSet = new LPPENoticeSet();
    private LPPENoticeSet mAllLPPENoticeSet = new LPPENoticeSet();
    private LPPENoticeSchema mLPPENoticeSchema = new LPPENoticeSchema();

    /** 体检资料项目表 */
    private LPPENoticeItemSet mLPPENoticeItemSet = new LPPENoticeItemSet();
    private LPPENoticeItemSet mmLPPENoticeItemSet = new LPPENoticeItemSet();
    private LPPENoticeItemSet mAllLPPENoticeItemSet = new LPPENoticeItemSet();

    public EdorWorkFlowBL()
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

        // 数据操作业务处理
        if (!dealData())
        {
            return false;
        }

        //准备给后台的数据
        if (!prepareOutputData())
        {
            return false;
        }

        //数据提交
        PubSubmit tps = new PubSubmit();

//        EdorWorkFlowBLS tEdorWorkFlowBLS = new EdorWorkFlowBLS();
//        System.out.println("Start EdorWorkFlowBLS Submit...");

        EdorWorkFlowBLS tEdorWorkFlowBLS = new EdorWorkFlowBLS();

        if (!tEdorWorkFlowBLS.submitData(mResult, mOperate))
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tEdorWorkFlowBLS.mErrors);

            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "submitData";
            tError.errorMessage = "数据提交失败!";
            this.mErrors.addOneError(tError);

            return false;
        }

//        if (! tps.submitData(mResult,mOperate))
//        {
//            // @@错误处理
//            this.mErrors.copyAllErrors(tps.mErrors);
//            CError tError = new CError();
//            tError.moduleName = "EdorWorkFlowBLS";
//            tError.functionName = "submitData";
//            tError.errorMessage = "数据提交失败!";
//            this.mErrors.addOneError(tError);
//            return false;
//        }

        System.out.println("---EdorWorkFlowBLS commitData End ---");
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
        mGlobalInput = ((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
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
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
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
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mOperate = cOperate;
        if (mOperate == null || mOperate.trim().equals(""))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
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
     */
    private boolean dealData()
    {
        //创建起始任务节点(保全申请级)
        if (mOperate.trim().equals("9999999998"))
        {
            if (!Execute9999999998())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }

        //保全工作流打印核保通知书活动表
        if (mOperate.trim().equals("9999999999"))
        {
            if (!Execute9999999999())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }
        //执行保全工作流待人工核保活动表任务
        else if (mOperate.trim().equals("0000000000"))
        {
            if (!Execute0000000000())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }

        //执行保全工作流发体检通知书活动表任务
        else if (mOperate.trim().equals("0000000001"))
        {
            if (!Execute0000000001())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }
        //执行保全工作流加费活动表任务
        else if (mOperate.trim().equals("0000000002"))
        {
            if (!Execute0000000002())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }

            return true;

        }
        //执行保全工作流特约活动表任务
        else if (mOperate.trim().equals("0000000003"))
        {
            if (!Execute0000000003())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }
        //执行保全工作流发生调通知书活动表任务
        else if (mOperate.trim().equals("0000000004"))
        {
            if (!Execute0000000004())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }
        //执行保全工作流发核保通知书活动表任务
        else if (mOperate.trim().equals("0000000005"))
        {
            if (!Execute0000000005())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }
        //执行保全工作流打印体检通知书活动表任务
        else if (mOperate.trim().equals("0000000006"))
        {
            if (!Execute0000000006())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }

        //保全工作流打印核保通知书活动表
        else if (mOperate.trim().equals("0000000007"))
        {
            if (!Execute0000000007())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }

        //保全工作流打印生调通知书活动表
        else if (mOperate.trim().equals("0000000008"))
        {
            if (!Execute0000000008())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }

        //执行保全工作流核保确认活动表任务
//        else if (mOperate.trim().equals("0000000010"))
//        {
//            if (!Execute0000000010())
//            {
//                // @@错误处理
//                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//                return false;
//            }
//            return true;
//
//        }

        //执行保全工作流回收体检通知书活动表任务
        else if (mOperate.trim().equals("0000000011"))
        {
            if (!Execute0000000011())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }

        //保全工作流打印核保通知书活动表
        else if (mOperate.trim().equals("0000000012"))
        {
            if (!Execute0000000012())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;
        }
        //保全工作流回收打印生调通知书活动表
        else if (mOperate.trim().equals("0000000013"))
        {
            if (!Execute0000000013())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;
        }
        //执行保全工作流补打体检通知书活动表任务
        else if (mOperate.trim().equals("0000000014"))
        {
            if (!Execute0000000014())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }

        //保全工作流补打核保通知书活动表
        else if (mOperate.trim().equals("0000000015"))
        {
            if (!Execute0000000015())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }

        //保全工作流补打生调通知书活动表
        else if (mOperate.trim().equals("0000000016"))
        {
            if (!Execute0000000016())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;
        }
        //保全工作批单申请起始活动表
        else if (mOperate.trim().equals("0000000017"))
        {
            if (!Execute0000000017())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;
        }
        else if (mOperate.trim().equals("0000000019"))
        {
            if (!Execute0000000019())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                System.out.println(
                        "class信息: EdorWorkFloeBL -->执行Execute0000000019()出错");
                return false;
            }
            return true;

        }

        else
        {
            if (!Execute())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;
        }
    }


    /**
     * 执行保全工作流待人工核保活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute0000000000()
    { //*
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
            tError.moduleName = "EdorWorkFlowBL";
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "Execute0000000000";
            tError.errorMessage = "前台传输数据TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000000", mInputData))
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
                    for (int j = 0; j < tempVData.size(); j++)
                    {
                        mResult.add(tempVData.get(i)); //取出Map值
                    }
                }
            }

            //产生执行完保全工作流待人工核保活动表任务后的任务节点

            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000000", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        }
        catch (Exception ex)
        {
            // @@错误处理
            System.out.println(ex.getStackTrace());
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎执行保全工作流待人工核保活动表任务出错!";
            this.mErrors.addOneError(tError);
            return false;

        } //*/

        return true;

    }


    /**
     * 执行保全工作流发体检通知书活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute0000000001()
    {
        mResult.clear();
        VData tVData = new VData();
        //获得当前工作任务的任务ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        if (tMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //执行发放体检通知书虚拟任务(当产生任务与执行任务为同一事务时,采用执行虚拟任务模式工作)
        try
        {
            System.out.println("ActivityOperator name:" +
                               mActivityOperator.getClass());
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000001", mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }

            //获得执行发放体检通知书任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "工作流引擎执行保全人工核保处体检通知书录入任务出错!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    mResult.add(tempVData); //取出Map值
                }
            }

            //产生执行完发放体检通知书任务后的下一打印体检通知书任务节点
            ActivityOperator tActivityOperator = new ActivityOperator();
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000001", mInputData))
            {
                VData tmpVData = new VData();
                tmpVData = tActivityOperator.getResult();
                if (tmpVData != null && tmpVData.size() > 0)
                {
                    mResult.add(tmpVData);
                    tmpVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "工作流引擎执行保全人工核保处体检通知书录入任务后,产生下一工作任务节点出错!";
                this.mErrors.addOneError(tError);
                return false;

            }

            //删除发放体检通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000001", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "工作流引擎执行保全人工核保处体检通知书录入任务后,删除该任务节点出错!";
                this.mErrors.addOneError(tError);
                return false;

            }
        }
        catch (Exception ex)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 执行保全工作流加费录入活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute0000000002()
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数tSubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //执行人工核保处加费录入活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000002", mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }

            //获得执行人工核保处特约录入任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    mResult.add(tempVData); //取出Map值
                }
            }

            //产生执行人工核保处加费录入任务后等待发放核保通知书任务节点
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000002", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 执行保全工作流人工核保处特约录入活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute0000000003()
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //执行人工核保处特约活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000003", mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }

            //获得执行人工核保处特约录入任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "工作流引擎执行保全工作流人工核保处特约录入任务出错!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    mResult.add(tempVData); //取出Map值
                }
            }

            //产生执行人工核保处特约录入任务后等待发放核保通知书任务节点
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000003", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 执行保全工作流发发生调通知书活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute0000000004()
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //执行人工核保处特约活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000004", mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "EdorWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "工作流引擎执行人工核保处发生调通知书任务出错!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }

            //获得执行人工核保处特约录入任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "工作流引擎执行保全工作流人工核保处发生调通知书任务出错!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //取出Map值
                    //}
                }
            }

            //产生执行人工核保处特约录入任务后等待发放核保通知书任务节点
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000004", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                // tError.moduleName = "EdorWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "工作流引擎执行保全人工核保处发生调通知书任务后,产生下一工作任务节点出错!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

            //删除发生调通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000004", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "EdorWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "工作流引擎执行保全人工核保处发生调通知书任务后,删除该任务节点出错!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 执行保全工作流发核保通知书活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute0000000005()
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //执行人工核保处发核保通知书活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000005", mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "EdorWorkFlowBL";
                // tError.functionName = "dealData";
                //tError.errorMessage = "工作流引擎执行人工核保处发核保通知书任务出错!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }

            //获得执行人工核保处特约录入任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "工作流引擎执行保全工作流人工核保处发核保通知书任务出错!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //取出Map值
                    //}
                }
            }

            //产生执行人工核保处特约录入任务后等待发放核保通知书任务节点
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000005", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "EdorWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "工作流引擎执行保全人工核保处发生调通知书任务后,产生下一工作任务节点出错!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000005", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "EdorWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "工作流引擎执行保全人工核保处发核保通知书任务后,删除该任务节点出错!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 执行保全工作流打印体检通知书活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute0000000006()
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //执行人工核保处打印体检通知书
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000006", mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "EdorWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "工作流引擎执行人工核保处打印体检通知书任务出错!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }

            //获得执行人工核保处特约录入任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "工作流引擎执行保全工作流人工核保处打印体检通知书任务出错!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //取出Map值
                    //}
                }
            }

            //产生执行人工核保处特约录入任务后等待发放核保通知书任务节点
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000006", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "EdorWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "工作流引擎执行保全人工核保处打印体检通知书任务后,产生下一工作任务节点出错!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000006", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "EdorWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "工作流引擎执行保全人工核保处打印体检通知书任务后,删除该任务节点出错!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 执行保全工作流打印核保通知书活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute0000000007()
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //执行人工核保处特约活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000007", mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "EdorWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "工作流引擎执行执行打印核保通知书活动任务出错!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //获得执行人工核保处打印核保通知书活动任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "工作流引擎执行打印核保通知书活动任务出错!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //取出Map值
                    //}
                }
            }

            //产生执行人工核保处特约录入任务后等待发放核保通知书任务节点
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000007", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "EdorWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "工作流引擎执行保全人工核保处发生调通知书任务后,产生下一工作任务节点出错!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000007", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "EdorWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "工作流引擎执行保全人工核保处发核保通知书任务后,删除该任务节点出错!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 执行保全工作流打印面见通知书活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute0000000008()
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //执行打印面见通知书活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000008", mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "EdorWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "工作流引擎执行执行打印面见通知书活动任务出错!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //获得执行人工核保处打印面见通知书活动任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "工作流引擎执行打印面见通知书活动任务出错!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //取出Map值
                    //}
                }
            }

            //产生执行人工核保处特约录入任务后等待发放核保通知书任务节点
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000008", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "EdorWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "工作流引擎执行保全人工核保处发生调通知书任务后,产生下一工作任务节点出错!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000008", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "EdorWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "工作流引擎执行保全人工核保处发核保通知书任务后,删除该任务节点出错!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 执行保全工作流核保确认活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute0000000010()
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //执行人工核保处特约活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000010", mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "EdorWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "工作流引擎执行人工保全工作流核保确认任务出错!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //获得执行人工核保处特约录入任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "工作流引擎执行保全工作流人工保全工作流核保确认任务出错!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //取出Map值
                    //}
                }
            }

            //产生执行人工核保处特约录入任务后等待发放核保通知书任务节点
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000010", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "EdorWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "工作流引擎保全工作流人工保全工作流核保确认任务后获得下一个工作节点出错!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000010", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//	   CError tError = new CError();
//	   tError.moduleName = "EdorWorkFlowBL";
//	   tError.functionName = "dealData";
//	   tError.errorMessage = "工作流引擎执行保全人工核保处发核保通知书任务后,删除该任务节点出错!";
//	   this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 执行保全工作流体检通知书回收活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute0000000011()
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //执行人工核保处特约活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000011", mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			 CError tError = new CError();
//			 tError.moduleName = "EdorWorkFlowBL";
//			 tError.functionName = "dealData";
//			 tError.errorMessage = "工作流引擎执行人工保全工作流体检通知书回收任务出错!";
//			 this.mErrors .addOneError(tError) ;
                return false;
            }

            //获得执行人工核保处特约录入任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "工作流引擎执行保全工作流人工保全工作流体检通知书回收任务出错!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //取出Map值
                    //}
                }
            }

            //产生执行人工核保处特约录入任务后等待发放核保通知书任务节点
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000011", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			  CError tError = new CError();
//			  tError.moduleName = "EdorWorkFlowBL";
//			  tError.functionName = "dealData";
//			  tError.errorMessage = "工作流引擎保全工作流人工保全工作流体检通知书回收任务后获得下一个工作节点出错!";
//			  this.mErrors .addOneError(tError) ;
                return false;
            }

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000011", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			  CError tError = new CError();
//			  tError.moduleName = "EdorWorkFlowBL";
//			  tError.functionName = "dealData";
//			  tError.errorMessage = "工作流引擎执行保全人工核保处发核保通知书任务后,删除该任务节点出错!";
//			  this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 执行保全工作流核保通知书回收活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute0000000012()
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //执行人工核保处特约活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000012", mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			  CError tError = new CError();
//			  tError.moduleName = "EdorWorkFlowBL";
//			  tError.functionName = "dealData";
//			  tError.errorMessage = "工作流引擎执行人工保全工作流核保通知书回收任务出错!";
//			  this.mErrors .addOneError(tError) ;
                return false;
            }

            //获得执行人工核保处特约录入任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "工作流引擎执行保全工作流人工保全工作流核保通知书回收任务出错!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //取出Map值
                    //}
                }
            }

            //产生执行人工核保处特约录入任务后等待发放核保通知书任务节点
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000012", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			  CError tError = new CError();
//			  tError.moduleName = "EdorWorkFlowBL";
//			  tError.functionName = "dealData";
//			  tError.errorMessage = "工作流引擎保全工作流人工保全工作流核保通知书回收任务后获得下一个工作节点出错!";
//			  this.mErrors .addOneError(tError) ;
                return false;
            }

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000012", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			  CError tError = new CError();
//			  tError.moduleName = "EdorWorkFlowBL";
//			  tError.functionName = "dealData";
//			  tError.errorMessage = "工作流引擎执行保全人工核保处发核保通知书任务后,删除该任务节点出错!";
//			  this.mErrors .addOneError(tError) ;
                return false;

            }
        }
        catch (Exception ex)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 执行保全工作流回复面见通知书活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute0000000013()
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //执行回复面见通知书活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000013", mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "EdorWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "工作流引擎执行执行回复面见通知书活动任务出错!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //获得执行人工核保处回复面见通知书活动任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "工作流引擎执行打印面见通知书活动任务出错!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //取出Map值
                    //}
                }
            }

            //产生执行人工核保处特约录入任务后等待发放核保通知书任务节点
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000013", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "EdorWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "工作流引擎执行保全人工核保处回复生调通知书任务后,产生下一工作任务节点出错!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000013", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "EdorWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "工作流引擎执行保全人工核保处回复生调通知书任务后,删除该任务节点出错!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 执行保全工作流补打体检通知书活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute0000000014()
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //执行人工核保处打印体检通知书
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000014", mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			 CError tError = new CError();
//			 tError.moduleName = "EdorWorkFlowBL";
//			 tError.functionName = "dealData";
//			 tError.errorMessage = "工作流引擎执行人工核保处打印体检通知书任务出错!";
//			 this.mErrors .addOneError(tError) ;
                return false;
            }

            //获得执行人工核保处特约录入任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "工作流引擎执行保全工作流人工核保处打印体检通知书任务出错!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //取出Map值
                    //}
                }
            }

            //产生执行人工核保处特约录入任务后等待发放核保通知书任务节点
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000014", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			CError tError = new CError();
//			tError.moduleName = "EdorWorkFlowBL";
//			tError.functionName = "dealData";
//			tError.errorMessage = "工作流引擎执行保全人工核保处打印体检通知书任务后,产生下一工作任务节点出错!";
//			this.mErrors .addOneError(tError) ;
                return false;

            }

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000014", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			CError tError = new CError();
//			tError.moduleName = "EdorWorkFlowBL";
//			tError.functionName = "dealData";
//			tError.errorMessage = "工作流引擎执行保全人工核保处打印体检通知书任务后,删除该任务节点出错!";
//			this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 执行保全工作流补打核保通知书活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute0000000015()
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //执行人工核保处特约活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000015", mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "EdorWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "工作流引擎执行执行补打核保通知书活动任务出错!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //获得执行人工核保处打印核保通知书活动任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "工作流引擎执行补打核保通知书活动任务出错!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //取出Map值
                    //}
                }
            }

            //产生执行人工核保处特约录入任务后等待发放核保通知书任务节点
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000015", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "EdorWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "工作流引擎执行保全人工核保处补打核保通知书任务后,产生下一工作任务节点出错!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000015", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "EdorWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "工作流引擎执行保全人工核保处发核保通知书任务后,删除该任务节点出错!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 执行保全工作流补打面见通知书活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute0000000016()
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //执行打印面见通知书活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000016", mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "EdorWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "工作流引擎执行执行补打面见通知书活动任务出错!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //获得执行人工核保处打印面见通知书活动任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "工作流引擎执行补打面见通知书活动任务出错!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //取出Map值
                    //}
                }
            }

            //产生执行人工核保处特约录入任务后等待发放核保通知书任务节点
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000016", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
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

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000016", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    private boolean Execute0000000017()
    { //*
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "Execute0000000017";
            tError.errorMessage = "前台传输数据TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "Execute0000000017";
            tError.errorMessage = "前台传输数据TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000017", mInputData))
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
                    for (int j = 0; j < tempVData.size(); j++)
                    {
                        mResult.add(tempVData.get(i)); //取出Map值
                    }
                }
            }

            //产生执行完保全工作流待人工核保活动表任务后的任务节点

            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000017", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎执行保全工作流待人工核保活动表任务出错!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/

        return true;

    }


    /**
     * 保全工作流申请级体检通知录入
     * @return boolean
     */
    private boolean Execute0000000019()
    { //*
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "Execute0000000019";
            tError.errorMessage = "前台传输数据TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "Execute0000000019";
            tError.errorMessage = "前台传输数据TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        System.out.println("class信息：EdorWorkFlowBL --->MissionID=" + tMissionID);
        System.out.println("class信息：EdorWorkFlowBL --->SubMissionID=" +
                           tSubMissionID);
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000019", mInputData))
            {
                System.out.println("class信息：EdorWorkFlowBL-->Result = false");
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            else
            {
                System.out.println("class信息：EdorWorkFlowBL-->Result = true");
            }

            //获得执行保全工作流体检通知录入活动表任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    System.out.println("tempVData.size()=" + tempVData.size());
                    for (int j = 0; j < tempVData.size(); j++)
                    {
                        mResult.add(tempVData.get(j)); //取出Map值
                        // tempVData.get(i).toString()
                        System.out.println(tempVData.get(j).toString());
                    }
                }
            }

            //产生执行完保全工作流体检通知录入活动表任务后的任务节点

            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000019", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                System.out.println("dfdfdfdfd");
                if (tempVData != null && tempVData.size() > 0)
                {
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎执行保全工作流体检通知录入保活动表任务出错!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/

        return true;

    }


    /**
     * 创建起始任务节点(保全申请级)
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute9999999998()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        try
        {
            //产生执行保全申请起始任务节点和核保结论节点
            if (tActivityOperator.CreateStartMission("0000000000", "0000000017",
                    mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                mResult.add(tempVData);
                tempVData = null;
            }
            else
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        catch (Exception ex)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "Execute9999999998";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 创建起始任务节点
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute9999999999()
    {
        mResult.clear();
        ActivityOperator tActivityOperator = new ActivityOperator();
        try
        {
            //产生执行完发放体检通知书任务后的下一打印体检通知书任务节点
            if (tActivityOperator.CreateStartMission("0000000000", "0000000000",
                    mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                mResult.add(tempVData);
                tempVData = null;
            }
            else
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        catch (Exception ex)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "Execute9999999999";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

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
            tError.moduleName = "EdorWorkFlowBL";
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
            tError.moduleName = "EdorWorkFlowBL";
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
            //获得执行承保工作流待人工核保活动表任务的结果
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

            //产生执行完承保工作流待人工核保活动表任务后的任务节点
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎执行新契约活动表任务出错!";
            this.mErrors.addOneError(tError);

            return false;
        }
        return true;
    }

    public VData getResult()
    {
      return this.mResult;
    }



    /**
     * 准备需要保存的数据
     */
    private boolean prepareOutputData()
    {
        //mInputData.add( mGlobalInput );
        return true;
    }


}

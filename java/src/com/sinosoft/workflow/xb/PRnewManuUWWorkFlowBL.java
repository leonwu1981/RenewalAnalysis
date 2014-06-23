package com.sinosoft.workflow.xb;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LWMissionSchema;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PRnewManuUWWorkFlowBL
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

    /**执行保全工作流发体检通知书活动表任务0000000001
         /** 体检资料主表 */
//     private LPPENoticeSet mLPPENoticeSet = new LPPENoticeSet();
//    private LPPENoticeSet mAllLPPENoticeSet = new LPPENoticeSet();
//    private LPPENoticeSchema mLPPENoticeSchema = new LPPENoticeSchema();
     /** 体检资料项目表 */
//    private LPPENoticeItemSet mLPPENoticeItemSet = new LPPENoticeItemSet();
//    private LPPENoticeItemSet mmLPPENoticeItemSet = new LPPENoticeItemSet();
//    private LPPENoticeItemSet mAllLPPENoticeItemSet = new LPPENoticeItemSet();

     public PRnewManuUWWorkFlowBL()
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

        System.out.println("---PRnewManuUWWorkFlowBL dealData---");

        //准备给后台的数据
        if (!prepareOutputData())
        {
            return false;
        }

        System.out.println("---PRnewManuUWWorkFlowBL prepareOutputData---");

        //数据提交
        PRnewManuUWWorkFlowBLS tPRnewManuUWWorkFlowBLS = new PRnewManuUWWorkFlowBLS();
        System.out.println("Start PRnewManuUWWorkFlowBLS Submit...");

        if (!tPRnewManuUWWorkFlowBLS.submitData(mResult, mOperate))
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tPRnewManuUWWorkFlowBLS.mErrors);
            CError tError = new CError();
            tError.moduleName = "PRnewManuUWWorkFlowBLS";
            tError.functionName = "submitData";
            tError.errorMessage = "数据提交失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        System.out.println("---PRnewManuUWWorkFlowBLS commitData End ---");
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
        mGlobalInput = ((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewManuUWWorkFlowBL";
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
            tError.moduleName = "PRnewManuUWWorkFlowBL";
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
            tError.moduleName = "PRnewManuUWWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mOperate = cOperate;
        if (mOperate == null || mOperate.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewManuUWWorkFlowBL";
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
        //保全工作流打印核保通知书活动表
        if (mOperate.trim().equals("8999999999"))
        {
            if (!Execute8999999999())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "产生保全工作流待人工核保活动表起始任务失败!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;
        }

        //执行保全工作流待人工核保活动表任务
        if (mOperate.trim().equals("0000000100"))
        {
            if (!Execute0000000100())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "执行保全工作流待人工核保活动表任务失败!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }

        //执行保全工作流发体检通知书活动表任务
        if (mOperate.trim().equals("0000000101"))
        {
            if (!Execute0000000101())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "执行保全工作流发体检通知书活动表任务失败!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }
        //执行保全工作流加费活动表任务
        if (mOperate.trim().equals("0000000102"))
        {
            if (!Execute0000000102())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "执行保全工作流发体检通知书活动表任务失败!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }

            return true;

        }
        //执行保全工作流特约活动表任务
        if (mOperate.trim().equals("0000000103"))
        {
            if (!Execute0000000103())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "执行保全工作流发体检通知书活动表任务失败!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }
        //执行保全工作流发生调通知书活动表任务
        if (mOperate.trim().equals("0000000104"))
        {
            if (!Execute0000000104())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "执行保全工作流发体检通知书活动表任务失败!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }
        //执行保全工作流发核保通知书活动表任务
        if (mOperate.trim().equals("0000000105"))
        {
            if (!Execute0000000105())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "执行保全工作流发体检通知书活动表任务失败!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }
        //执行保全工作流打印体检通知书活动表任务
        if (mOperate.trim().equals("0000000106"))
        {
            if (!Execute0000000106())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "执行保全工作流发体检通知书活动表任务失败!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }

        //保全工作流打印核保通知书活动表
        if (mOperate.trim().equals("0000000107"))
        {
            if (!Execute0000000107())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "执行保全工作流打印核保通知书活动表任务失败!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }

        //保全工作流打印生调通知书活动表
        if (mOperate.trim().equals("0000000108"))
        {
            if (!Execute0000000108())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "执行保全工作流发体检通知书活动表任务失败!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }

        //执行保全工作流核保确认活动表任务
        if (mOperate.trim().equals("0000000110"))
        {
            if (!Execute0000000110())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "执行保全工作流发体检通知书活动表任务失败!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }

        //执行保全工作流回收体检通知书活动表任务
        if (mOperate.trim().equals("0000000111"))
        {
            if (!Execute0000000111())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "执行保全工作流发体检通知书活动表任务失败!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }

        //保全工作流打印核保通知书活动表
        if (mOperate.trim().equals("0000000112"))
        {
            if (!Execute0000000112())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "执行保全工作流打印核保通知书活动表任务失败!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;
        }
        //保全工作流回收打印生调通知书活动表
        if (mOperate.trim().equals("0000000113"))
        {
            if (!Execute0000000113())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//	   CError tError = new CError();
//	   tError.moduleName = "PEdorManuUWWorkFlowBL";
//	   tError.functionName = "dealData";
//	   tError.errorMessage = "执行保全工作流补打生调通知书活动表任务失败!";
//	   this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;
        }
        //执行保全工作流补打体检通知书活动表任务
        if (mOperate.trim().equals("0000000114"))
        {
            if (!Execute0000000114())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "执行保全工作流补打体检通知书活动表任务失败!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }

        //保全工作流补打核保通知书活动表
        if (mOperate.trim().equals("0000000115"))
        {
            if (!Execute0000000115())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "执行保全工作流补打核保通知书活动表任务失败!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }

        //保全工作流补打生调通知书活动表
        if (mOperate.trim().equals("0000000116"))
        {
            if (!Execute0000000116())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "执行保全工作流补打生调通知书活动表任务失败!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;
        }

        return true;

    }

    /**
     * 执行保全工作流待人工核保活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute0000000100()
    { //*
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //获得当前工作任务的任务ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
        if (tMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "Execute0000000100";
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
            tError.functionName = "Execute0000000100";
            tError.errorMessage = "前台传输数据TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000100"
                    , mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "Execute0000000000";
                //tError.errorMessage = "工作流引擎执行保全工作流待人工核保活动表任务出错!";
                //this.mErrors .addOneError(tError) ;
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

            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000100"
                    , mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }

            }

            //获得转存任务的数据
//		LBMissionSchema tLBMissionSchema = new LBMissionSchema ();
//		tLBMissionSchema = mActivityOperator.TranSaveDummyMission(tLWMissionSchema,mInputData);
//		if(tLBMissionSchema == null)
//		{
//		  // @@错误处理
//		 this.mErrors.copyAllErrors( mActivityOperator.mErrors );
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
//		 tError.functionName = "Execute0000000100";
//		 tError.errorMessage = "工作流引擎执行执行完保全工作流待人工核保活动表任务,获得转存任务数据出错!";
//		 this.mErrors .addOneError(tError) ;
//		 return false;
//		}
//		else
//		{
//		  MMap map = new MMap();
//		  map.put(tLBMissionSchema, "INSERT");
//		  mResult.add(map) ;
//		  tLBMissionSchema = null ;
//		}

        }
        catch (Exception ex)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
    private boolean Execute0000000101()
    {
        mResult.clear();
        VData tVData = new VData();
        //获得当前工作任务的任务ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
        if (tMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //执行发放体检通知书虚拟任务(当产生任务与执行任务为同一事务时,采用执行虚拟任务模式工作)
        try
        {
            System.out.println("ActivityOperator name:" + mActivityOperator.getClass());
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000101"
                    , mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "工作流引擎执行保全人工核保处体检通知书录入任务出错!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }

            //获得执行发放体检通知书任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            //mInputData.add() ;
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000101"
                    , mInputData))
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
                tError.moduleName = "PEdorManuUWWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "工作流引擎执行保全人工核保处体检通知书录入任务后,产生下一工作任务节点出错!";
                this.mErrors.addOneError(tError);
                return false;

            }

            //删除发放体检通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000101", mInputData))
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
                tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
    private boolean Execute0000000102()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //获得当前工作任务的任务ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
        if (tMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数tSubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //执行人工核保处加费录入活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000102"
                    , mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "工作流引擎执行人工核保处加费录入任务出错!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }

            //获得执行人工核保处特约录入任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "工作流引擎执行保全工作流人工核保处加费录入任务出错!";
                //this.mErrors .addOneError(tError) ;
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

            //产生执行人工核保处加费录入任务后等待发放核保通知书任务节点
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000102"
                    , mInputData))
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
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "工作流引擎执行保全人工核保处加费录入任务后,产生下一工作任务节点出错!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

//		//删除发放加费录入任务任务节点
//		tActivityOperator = new ActivityOperator();
//		if(tActivityOperator.DeleteMission(tMissionID,tSubMissionID,"0000000002",mInputData) )
//		{
//		  VData tempVData = new VData();
//		  tempVData = tActivityOperator.getResult();
//		  if(tempVData != null && tempVData.size() >0)
//		  {
//			mResult.add(tempVData) ;
//			tempVData = null ;
//		  }
//		}
//		else
//		{
//		  this.mErrors.copyAllErrors( mActivityOperator.mErrors );
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "工作流引擎执行保全人工核保处加费录入任务后,删除该任务节点出错!";
//		 this.mErrors .addOneError(tError) ;
//		 return false;
//
//		}//未发核保通知书以前仍可以修改加费信息

        }
        catch (Exception ex)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
    private boolean Execute0000000103()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //获得当前工作任务的任务ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
        if (tMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //执行人工核保处特约活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000103"
                    , mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "工作流引擎执行人工核保处特约录入任务出错!";
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
                tError.moduleName = "PEdorManuUWWorkFlowBL";
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
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //取出Map值
                    //}
                }
            }

            //产生执行人工核保处特约录入任务后等待发放核保通知书任务节点
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000103"
                    , mInputData))
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
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "工作流引擎执行保全人工核保处特约录入任务后,产生下一工作任务节点出错!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

//		//删除发放特约录入任务任务节点
//		tActivityOperator = new ActivityOperator();
//		if(tActivityOperator.DeleteMission(tMissionID,tSubMissionID,"0000000003",mInputData) )
//		{
//		  VData tempVData = new VData();
//		  tempVData = tActivityOperator.getResult();
//		  if(tempVData != null && tempVData.size() >0)
//		  {
//			mResult.add(tempVData) ;
//			tempVData = null ;
//		  }
//		}
//		else
//		{
//		  this.mErrors.copyAllErrors( mActivityOperator.mErrors );
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "工作流引擎执行保全人工核保处特约录入任务后,删除该任务节点出错!";
//		 this.mErrors .addOneError(tError) ;
//		 return false;
//
//		}//未发核保通知书以前仍可以修改特约信息


        }
        catch (Exception ex)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
    private boolean Execute0000000104()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //获得当前工作任务的任务ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");

        if (tMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //执行人工核保处特约活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000104"
                    , mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "工作流引擎执行人工核保处发生调通知书任务出错!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }
            System.out.println("Exception:1");
            //获得执行人工核保处特约录入任务的结果
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            System.out.println("Exception:2");
            //产生执行人工核保处特约录入任务后等待发放核保通知书任务节点
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000104"
                    , mInputData))
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
                this.mErrors.copyAllErrors(tActivityOperator.mErrors);
                //CError tError = new CError();
                // tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "工作流引擎执行保全人工核保处发生调通知书任务后,产生下一工作任务节点出错!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }
            System.out.println("Exception:3");
            //删除发生调通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000104", mInputData))
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
                this.mErrors.copyAllErrors(tActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "工作流引擎执行保全人工核保处发生调通知书任务后,删除该任务节点出错!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
//		 // @@错误处理
//		 if(tActivityOperator.mErrors != null )
            System.out.println("Exception:" + ex.toString());
//		 if(mActivityOperator.mErrors != null )
//         System.out.println("mActivityOperator.mErrors:"+mActivityOperator.mErrors.getErrorCount()) ;

//		 this.mErrors.copyAllErrors( tActivityOperator.mErrors );
//		 this.mErrors.copyAllErrors( mActivityOperator.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = ex.toString();
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * 执行保全工作流发核保通知书活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute0000000105()
    {

        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //获得当前工作任务的任务ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");

        if (tMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //执行人工核保处发核保通知书活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000105"
                    , mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
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
                tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000105"
                    , mInputData))
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
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "工作流引擎执行保全人工核保处发生调通知书任务后,产生下一工作任务节点出错!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000105", mInputData))
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
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
    private boolean Execute0000000106()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //获得当前工作任务的任务ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");

        if (tMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //执行人工核保处打印体检通知书
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000106"
                    , mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
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
                tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000106"
                    , mInputData))
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
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "工作流引擎执行保全人工核保处打印体检通知书任务后,产生下一工作任务节点出错!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000106", mInputData))
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
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
    private boolean Execute0000000107()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //获得当前工作任务的任务ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");

        if (tMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //执行人工核保处特约活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000107"
                    , mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
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
                tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000107"
                    , mInputData))
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
//		tError.moduleName = "PEdorManuUWWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "工作流引擎执行保全人工核保处发生调通知书任务后,产生下一工作任务节点出错!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000107", mInputData))
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
//		tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
    private boolean Execute0000000108()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //获得当前工作任务的任务ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");

        if (tMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //执行打印面见通知书活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000108"
                    , mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
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
                tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000108"
                    , mInputData))
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
//		tError.moduleName = "PEdorManuUWWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "工作流引擎执行保全人工核保处发生调通知书任务后,产生下一工作任务节点出错!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000108", mInputData))
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
//		tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
    private boolean Execute0000000110()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //获得当前工作任务的任务ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");

        if (tMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //执行人工核保处特约活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000110"
                    , mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
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
                tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000110"
                    , mInputData))
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
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "工作流引擎保全工作流人工保全工作流核保确认任务后获得下一个工作节点出错!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000110", mInputData))
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
//	   tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
    private boolean Execute0000000111()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //获得当前工作任务的任务ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");

        if (tMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //执行人工核保处特约活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000111"
                    , mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			 CError tError = new CError();
//			 tError.moduleName = "PEdorManuUWWorkFlowBL";
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
                tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000111"
                    , mInputData))
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
//			  tError.moduleName = "PEdorManuUWWorkFlowBL";
//			  tError.functionName = "dealData";
//			  tError.errorMessage = "工作流引擎保全工作流人工保全工作流体检通知书回收任务后获得下一个工作节点出错!";
//			  this.mErrors .addOneError(tError) ;
                return false;
            }

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000111", mInputData))
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
//			  tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
    private boolean Execute0000000112()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //获得当前工作任务的任务ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");

        if (tMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //执行人工核保处特约活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000112"
                    , mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			  CError tError = new CError();
//			  tError.moduleName = "PEdorManuUWWorkFlowBL";
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
                tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000112"
                    , mInputData))
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
//			  tError.moduleName = "PEdorManuUWWorkFlowBL";
//			  tError.functionName = "dealData";
//			  tError.errorMessage = "工作流引擎保全工作流人工保全工作流核保通知书回收任务后获得下一个工作节点出错!";
//			  this.mErrors .addOneError(tError) ;
                return false;
            }

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000112", mInputData))
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
//			  tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
    private boolean Execute0000000113()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //获得当前工作任务的任务ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");

        if (tMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //执行回复面见通知书活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000113"
                    , mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
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
                tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000113"
                    , mInputData))
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
//		tError.moduleName = "PEdorManuUWWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "工作流引擎执行保全人工核保处回复生调通知书任务后,产生下一工作任务节点出错!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000113", mInputData))
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
//		tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
    private boolean Execute0000000114()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //获得当前工作任务的任务ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");

        if (tMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //执行人工核保处打印体检通知书
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000114"
                    , mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			 CError tError = new CError();
//			 tError.moduleName = "PEdorManuUWWorkFlowBL";
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
                tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000114"
                    , mInputData))
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
//			tError.moduleName = "PEdorManuUWWorkFlowBL";
//			tError.functionName = "dealData";
//			tError.errorMessage = "工作流引擎执行保全人工核保处打印体检通知书任务后,产生下一工作任务节点出错!";
//			this.mErrors .addOneError(tError) ;
                return false;

            }

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000114", mInputData))
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
//			tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
    private boolean Execute0000000115()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //获得当前工作任务的任务ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");

        if (tMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //执行人工核保处特约活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000115"
                    , mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
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
                tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000115"
                    , mInputData))
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
//		tError.moduleName = "PEdorManuUWWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "工作流引擎执行保全人工核保处补打核保通知书任务后,产生下一工作任务节点出错!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000115", mInputData))
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
//		tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
    private boolean Execute0000000116()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //获得当前工作任务的任务ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");

        if (tMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "保单数据前台传输TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //执行打印面见通知书活动表
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000116"
                    , mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
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
                tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000116"
                    , mInputData))
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
//		tError.moduleName = "PEdorManuUWWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "工作流引擎执行保全人工核保处发生调通知书任务后,产生下一工作任务节点出错!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

            //删除发核保通知书任务节点
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000116", mInputData))
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
//		tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
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
    private boolean Execute8999999999()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //执行发放体检通知书虚拟任务(当产生任务与执行任务为同一事务时,采用执行虚拟任务模式工作)
        try
        {
            System.out.println("ActivityOperator name:" + mActivityOperator.getClass());

            //产生执行完发放体检通知书任务后的下一打印体检通知书任务节点
            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
            if (tActivityOperator.CreateStartMission("0000000001", "0000000100", mInputData))
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
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "Execute9999999999";
                //tError.errorMessage = "工作流引擎工作出现异常!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }
        }
        catch (Exception ex)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "Execute9999999999";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 准备需要保存的数据
     */
    private static boolean prepareOutputData()
    {
        //mInputData.add( mGlobalInput );
        return true;
    }


}

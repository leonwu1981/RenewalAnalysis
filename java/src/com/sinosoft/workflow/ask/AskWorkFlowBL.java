package com.sinosoft.workflow.ask;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LWMissionSchema;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;


/**
 * <p>Title: 新契约工作流 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */
public class AskWorkFlowBL
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

    public AskWorkFlowBL()
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
        System.out.println("---AskWorkFlowBL getInputData---");
        // 数据操作业务处理
        if (!dealData())
        {
            return false;
        }
        System.out.println("---AskWorkFlowBL dealData---");

        //准备给后台的数据
        if (!prepareOutputData())
        {
            return false;
        }

        System.out.println("---AskWorkFlowBL prepareOutputData---");

        //数据提交
        AskWorkFlowBLS tAskWorkFlowBLS = new AskWorkFlowBLS();
        System.out.println("Start AskWorkFlowBL Submit...");

        if (!tAskWorkFlowBLS.submitData(mResult, mOperate))
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tAskWorkFlowBLS.mErrors);

            CError tError = new CError();
            tError.moduleName = "AskWorkFlowBL";
            tError.functionName = "submitData";
            tError.errorMessage = "数据提交失败!";
            this.mErrors.addOneError(tError);

            return false;
        }

        System.out.println("---AskWorkFlowBLS commitData End ---");

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
            tError.moduleName = "AskWorkFlowBL";
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
            tError.moduleName = "AskWorkFlowBL";
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
            tError.moduleName = "AskWorkFlowBL";
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
            tError.moduleName = "AskWorkFlowBL";
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
      //承保工作流打印核保通知书活动表
        if (mOperate.trim().equals("7999999999"))
        {
            if (!Execute7999999999())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        else if (mOperate.trim().equals("0000006004")) //执行工作流待人工核保活动表任务
        {
            if (!Execute0000006004())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        else if(mOperate.trim().equals("0000006000")) //生成无扫描的第一个节点
        {
          if(!Execute0000006000())
          {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            return false;

          }
        }else if(mOperate.trim().equals("0000006020"))
        {
          if(!Execute0000006020())
          {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            return false;
          }

        }else if(mOperate.trim().equals("0000006006"))
        {
          if(!Execute0000006006())
          {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            return false;
          }
        }
        else //执行承保工作流发核保通知书活动表任务
        {
            if (!Execute())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }

        return true;
    }
    //产生询价工作流的第一个节点
    private boolean Execute0000006000() {
      mResult.clear();
      VData tVData = new VData();
      ActivityOperator tActivityOperator = new ActivityOperator();
      try {
        System.out.println("ActivityOperator name:" +
                           mActivityOperator.getClass());
        //产生第一个节点
        if (tActivityOperator.CreateStartMission("0000000006", "0000006002",
                                                 mInputData)) {
          mGlobalInput = ((GlobalInput) mInputData.getObjectByObjectName(
                  "GlobalInput",
                  0));
          VData tempVData = new VData();
          tempVData = tActivityOperator.getResult();
          mResult.add(tempVData);
          tempVData = null;
        }
        else {
          // @@错误处理
          this.mErrors.copyAllErrors(mActivityOperator.mErrors);
          //CError tError = new CError();
          //tError.moduleName = "AskWorkFlowBL";
          //tError.functionName = "Execute0000006002";
          //tError.errorMessage = "工作流引擎工作出现异常!";
          //this.mErrors .addOneError(tError) ;
          return false;
        }
      }
      catch (Exception ex) {
        // @@错误处理
        this.mErrors.copyAllErrors(mActivityOperator.mErrors);
        CError tError = new CError();
        tError.moduleName = "AskWorkFlowBL";
        tError.functionName = "Execute0000006002";
        tError.errorMessage = "工作流引擎工作出现异常!";
        this.mErrors.addOneError(tError);
        return false;
      }
      return true;
    }

    /**
     * Execute0000001100
     *
     * @return boolean
     */
    private boolean Execute0000006004()
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
            tError.moduleName = "AskWorkFlowBL";
            tError.functionName = "Execute0000006004";
            tError.errorMessage = "前台传输数据TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "AskWorkFlowBL";
            tError.functionName = "Execute0000006004";
            tError.errorMessage = "前台传输数据TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000006004", mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "TbWorkFlowBL";
                //tError.functionName = "Execute0000000000";
                //tError.errorMessage = "工作流引擎执行承保工作流待人工核保活动表任务出错!";
                //this.mErrors .addOneError(tError) ;
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
                    for (int j = 0; j < tempVData.size(); j++)
                    {
                        mResult.add(tempVData.get(i)); //取出Map值
                    }
                }
            }

            //产生执行完承保工作流待人工核保活动表任务后的任务节点

            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000006004", mInputData))
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
            tError.moduleName = "AskWorkFlowBL";
            tError.functionName = "Execute0000006004";
            tError.errorMessage = "工作流引擎执行承保工作流待人工核保活动表任务出错!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/

        return true;
    }

  private boolean Execute0000006006()
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
            tError.moduleName = "AskWorkFlowBL";
            tError.functionName = "Execute0000006004";
            tError.errorMessage = "前台传输数据TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "AskWorkFlowBL";
            tError.functionName = "Execute0000006004";
            tError.errorMessage = "前台传输数据TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000006006", mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "TbWorkFlowBL";
                //tError.functionName = "Execute0000000000";
                //tError.errorMessage = "工作流引擎执行承保工作流待人工核保活动表任务出错!";
                //this.mErrors .addOneError(tError) ;
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
                    for (int j = 0; j < tempVData.size(); j++)
                    {
                        mResult.add(tempVData.get(i)); //取出Map值
                    }
                }
            }

            //产生执行完承保工作流待人工核保活动表任务后的任务节点

            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000006006", mInputData))
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
            tError.moduleName = "AskWorkFlowBL";
            tError.functionName = "Execute0000006004";
            tError.errorMessage = "工作流引擎执行承保工作流待人工核保活动表任务出错!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/

        return true;
    }

    /**
     * 执行承保工作流待人工核保活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
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
            tError.moduleName = "AskWorkFlowBL";
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
            tError.moduleName = "AskWorkFlowBL";
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
            tError.moduleName = "AskWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎执行新契约活动表任务出错!";
            this.mErrors.addOneError(tError);

            return false;
        }

        //*/
        return true;
    }

   private boolean Execute0000006020()
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
         tError.moduleName = "AskWorkFlowBL";
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
         tError.moduleName = "AskWorkFlowBL";
         tError.functionName = "Execute0000000000";
         tError.errorMessage = "前台传输数据TransferData中的必要参数SubMissionID失败!";
         this.mErrors.addOneError(tError);

         return false;
     }
     try
     {

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
     }
     catch (Exception ex)
     {
         // @@错误处理
         ex.printStackTrace();
         this.mErrors.copyAllErrors(mActivityOperator.mErrors);

         CError tError = new CError();
         tError.moduleName = "AskWorkFlowBL";
         tError.functionName = "dealData";
         tError.errorMessage = "工作流引擎执行新契约活动表任务出错!";
         this.mErrors.addOneError(tError);

         return false;
     }

     //*/
     return true;
   }
    /**
     * 创建起始任务节点
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean Execute7999999999()
    {
        mResult.clear();
        VData tVData = new VData();
        //团单录入完毕校验
        AskFirstActivityCheck tAskFirstActivityCheck = new
                                                 AskFirstActivityCheck();

        if (tAskFirstActivityCheck.submitData(mInputData, ""))
        {
            tVData = tAskFirstActivityCheck.getResult();
//            mResult.add(tVData);
        }
        else
        {
            this.mErrors.copyAllErrors(tAskFirstActivityCheck.mErrors);
            return false;
        }

        ActivityOperator tActivityOperator = new ActivityOperator();
        //执行发放体检通知书虚拟任务(当产生任务与执行任务为同一事务时,采用执行虚拟任务模式工作)
        try
        {
            System.out.println("ActivityOperator name:" +
                               mActivityOperator.getClass());

            //产生执行完发放体检通知书任务后的下一打印体检通知书任务节点
            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
            if (tActivityOperator.CreateStartMission("0000000006", "0000006004",
                                                     mInputData))                 //对于录入完毕的第一个节点
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
            tError.moduleName = "AskWorkFlowBL";
            tError.functionName = "Execute7999999999";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
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

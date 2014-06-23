/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LWMissionDB;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.vschema.LDSpotTrackSet;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;
import com.sinosoft.lis.sms.*;
/**
 * <p>Title: 新契约工作流 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */
public class TbWorkFlowBL
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 往后面传输数据的容器 */
    private VData mInputData;

    /** 往界面传输数据的容器 */
    private VData mResult = new VData();

    /** 往前台传输数据的容器 */
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

    /**是否提交标志**/
    private String flag;
    private boolean mFlag = true;

    public TbWorkFlowBL()
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
//        System.out.println("---TbWorkFlowBL getInputData---");
        // 数据操作业务处理
        if (!dealData())
        {
            return false;
        }
//        System.out.println("---TbWorkFlowBL dealData---");

        //准备给后台的数据
        if (!prepareOutputData())
        {
            return false;
        }

//        System.out.println("---TbWorkFlowBL prepareOutputData---");

        if (mFlag)
        {
            //如果置相应的标志位，不提交
            //数据提交
            TbWorkFlowBLS tTbWorkFlowBLS = new TbWorkFlowBLS();
//            System.out.println("Start TbWorkFlowBL Submit...");
            boolean a=true;
            if (!tTbWorkFlowBLS.submitData(mResult, mOperate))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tTbWorkFlowBLS.mErrors);

                CError tError = new CError();
                tError.moduleName = "TbWorkFlowBL";
                tError.functionName = "submitData";
                tError.errorMessage = "数据提交失败!";
                this.mErrors.addOneError(tError);

                return false;
            }
            else
            {
                if(mOperate.equals("0000001150"))
                {
                  LCContSet toneLCContSet=new LCContSet();
                  LCContSet ttwoLCContSet=new LCContSet();
                  toneLCContSet = (LCContSet) cInputData.getObjectByObjectName("LCContSet", 0);
                  LCContDB tLCContDB=new LCContDB();
                  tLCContDB.setProposalContNo(toneLCContSet.get(1).getContNo());
                  ttwoLCContSet=tLCContDB.query();
                  LXCalculator lxcalculator = new LXCalculator();
                  VData tVData = new VData();
                  TransferData tTransferData = new TransferData();
                  tTransferData.setNameAndValue("ItemCode", "TBGX0001");
                  tTransferData.setNameAndValue("SendDate", PubFun.getCurrentDate());
                  tTransferData.setNameAndValue("ContNo", ttwoLCContSet.get(1).getContNo());
                  tVData.add(mGlobalInput);
                  tVData.add(tTransferData);
                  lxcalculator.submitData(tVData, "send");
                }
                /*
                if(mOperate.equals("0000001104"))
                {
                  LCContSet toneLCContSet=new LCContSet();
                  LCContSet ttwoLCContSet=new LCContSet();
                  toneLCContSet = (LCContSet) cInputData.getObjectByObjectName("LCContSet", 0);
                  LCContDB tLCContDB=new LCContDB();
                  tLCContDB.setProposalContNo(toneLCContSet.get(1).getContNo());
                  ttwoLCContSet=tLCContDB.query();
                  LXCalculator lxcalculator = new LXCalculator();
                  VData tVData = new VData();
                  TransferData tTransferData = new TransferData();
                  tTransferData.setNameAndValue("ItemCode", "LIS00020005");
                  tTransferData.setNameAndValue("SendDate", PubFun.getCurrentDate()+1);
                  tTransferData.setNameAndValue("ContNo", ttwoLCContSet.get(1).getContNo());
                  tVData.add(mGlobalInput);
                  tVData.add(tTransferData);
                  lxcalculator.submitData(tVData, "send");
                }
                */
            }
        }
        /*
                 if (!CheckDraw(mTransferData)) {
          TbWorkFlowUI tTbWorkFlowUI = new TbWorkFlowUI();
          if (!tTbWorkFlowUI.submitData(cInputData, "0000001001")) {
            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "submitData";
            tError.errorMessage = "进行非抽检直接复核失败!";
            this.mErrors.addOneError(tError);

          }

                 }
         */
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
        mGlobalInput = ((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
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
            tError.moduleName = "TbWorkFlowBL";
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
            tError.moduleName = "TbWorkFlowBL";
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
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据Operate任务节点编码失败!";
            this.mErrors.addOneError(tError);

            return false;
        }
        flag = (String) mTransferData.getValueByName("flag");
        if (flag != null)
        {
            if (flag.equals("N"))
            {
                mFlag = false;
            }
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
        else if (mOperate.trim().equals("0000001100"))
        {
            //执行工作流待人工核保活动表任务
            if (!Execute0000001100())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        else if (mOperate.trim().equals("7599999999"))
        {
            //执行工作流待人工核保活动表任务
            if (!Execute7599999999())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }

        else if (mOperate.trim().equals("7899999999"))
        {
            if (!Execute7899999999())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        else if (mOperate.trim().equals("7799999999"))
        {
            if (!Execute7799999999())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        else if (mOperate.trim().equals("7099999999"))
        {
            //执行初审申请节点
            if (!Execute7099999999())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        else if (mOperate.trim().equals("9999991061"))
        {
            if (!Execute9999991061())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        /*
        else if (mOperate.trim().equals("0000001123") || mOperate.trim().equals("0000001124")|| mOperate.trim().equals("0000001125"))
        {
            if (!Execute000000112345(mOperate))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        */
        else
        {
            //执行承保工作流发核保通知书活动表任务
            if (!Execute())
            {
                // @@错误处理
                //this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }

        return true;
    }

    /**
     * 执行承保工作流待人工核保活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @return boolean
     */
    private boolean Execute()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();

        //获得当前工作任务的任务ID，这里没有清空数据导致的问题，我估计是
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
        if (tMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
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
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "Execute0000000000";
            tError.errorMessage = "前台传输数据TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);

            return false;
        }

        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, mOperate, mInputData))
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
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, mOperate, mInputData))
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
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, mOperate, mInputData))
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
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎执行新契约活动表任务出错!";
            this.mErrors.addOneError(tError);

            return false;
        }
        return true;
    }

    /**
     * 执行承保工作流待人工核保活动表任务
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @return boolean
     */
    private boolean Execute0000001100()
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
            tError.moduleName = "TbWorkFlowBL";
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
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "Execute0000001100";
            tError.errorMessage = "前台传输数据TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000001100"
                    , mInputData))
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

            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000001100"
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

        }
        catch (Exception ex)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎执行承保工作流待人工核保活动表任务出错!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * 创建起始任务节点
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @return boolean
     */
    private boolean Execute7999999999()
    {
        mResult.clear();
        VData tVData = new VData();
        //团单录入完毕校验
        FirstWorkFlowCheck tFirstWorkFlowCheck = new
                FirstWorkFlowCheck();

        if (tFirstWorkFlowCheck.submitData(mInputData, ""))
        {
            tVData = tFirstWorkFlowCheck.getResult();
            mResult.add(tVData);
        }
        else
        {
            this.mErrors.copyAllErrors(tFirstWorkFlowCheck.mErrors);
            return false;
        }

        ActivityOperator tActivityOperator = new ActivityOperator();
        //执行发放体检通知书虚拟任务(当产生任务与执行任务为同一事务时,采用执行虚拟任务模式工作)
        try
        {
//            System.out.println("ActivityOperator name:" +                    mActivityOperator.getClass());

            //产生执行完发放体检通知书任务后的下一打印体检通知书任务节点
//            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
            if (tActivityOperator.CreateStartMission("0000000003", "0000001001",
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
                //CError tError = new CError();
                //tError.moduleName = "TbWorkFlowBL";
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
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "Execute7999999999";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * 创建起始任务节点
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @return boolean
     */
    private boolean Execute7599999999()
    {
        mResult.clear();
//        VData tVData = new VData();
//        System.out.println("Execute 7599999999");
        ActivityOperator tActivityOperator = new ActivityOperator();
        //执行发放体检通知书虚拟任务(当产生任务与执行任务为同一事务时,采用执行虚拟任务模式工作)
        try
        {
//            System.out.println("ActivityOperator name:" +                    mActivityOperator.getClass());

            //产生执行完发放体检通知书任务后的下一打印体检通知书任务节点
//            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
            if (tActivityOperator.CreateStartMission("0000000003", "0000001061", mInputData))
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
                //tError.moduleName = "TbWorkFlowBL";
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
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "Execute7899999999";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;

    }

    /**
     * 创建起始任务节点
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @return boolean
     */
    private boolean Execute7899999999()
    {
        mResult.clear();
//        VData tVData = new VData();
//        System.out.println("Excute 78999999999");
        ActivityOperator tActivityOperator = new ActivityOperator();
        //执行发放体检通知书虚拟任务(当产生任务与执行任务为同一事务时,采用执行虚拟任务模式工作)
        try
        {
//            System.out.println("ActivityOperator name:" +                    mActivityOperator.getClass());

            //产生执行完发放体检通知书任务后的下一打印体检通知书任务节点
//            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
//            tLWMissionSchema = tActivityOperator.CreateOneMission("0000000003",                    "0000001099", mInputData);
            tActivityOperator.CreateOneMission("0000000003", "0000001099", mInputData);
//            System.out.println("prtno ==" + tLWMissionSchema.getMissionProp1());

//            if (tActivityOperator.CreateStartMission("0000000003", "0000001099",
//                    mInputData))
//            {
//                VData tempVData = new VData();
//                tempVData = tActivityOperator.getResult();
//                mResult.add(tempVData);
//                tempVData = null;
//            }
//            else
//            {
//                // @@错误处理
//                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//                //CError tError = new CError();
//                //tError.moduleName = "TbWorkFlowBL";
//                //tError.functionName = "Execute9999999999";
//                //tError.errorMessage = "工作流引擎工作出现异常!";
//                //this.mErrors .addOneError(tError) ;
//                return false;
//            }
        }
        catch (Exception ex)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "Execute7899999999";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * 创建起始任务节点
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @return boolean
     */
    private boolean Execute7799999999()
    {
        mResult.clear();
//        VData tVData = new VData();
//        System.out.println("Excute 77999999999");
        ActivityOperator tActivityOperator = new ActivityOperator();
//        LWMissionSchema tLWMissionSchema = new LWMissionSchema();
        //执行发放体检通知书虚拟任务(当产生任务与执行任务为同一事务时,采用执行虚拟任务模式工作)
        try
        {
//            System.out.println("ActivityOperator name:" +                    mActivityOperator.getClass());
            if (CheckFirstTrial())
            {
                return true;
            }
            if (tActivityOperator.CreateStartMission("0000000003", "0000001098", mInputData))
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
                //tError.moduleName = "TbWorkFlowBL";
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
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "Execute7899999999";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * 创建初审起始任务节点
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @return boolean
     */
    private boolean Execute7099999999()
    {
        mResult.clear();
//        VData tVData = new VData();
//        System.out.println("Excute Execute7099999999");
        ActivityOperator tActivityOperator = new ActivityOperator();
        //执行发放体检通知书虚拟任务(当产生任务与执行任务为同一事务时,采用执行虚拟任务模式工作)
        try
        {
//            System.out.println("ActivityOperator name:" +                    mActivityOperator.getClass());

//            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
            if (tActivityOperator.CreateStartMission("0000000003", "0000001061", mInputData))
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
                //tError.moduleName = "TbWorkFlowBL";
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
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "Execute7899999999";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * 自动初始化体检,生调节点
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @return boolean
     */
    private boolean Execute9999991061()
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
        LWMissionSchema t1LWMissionSchema = new LWMissionSchema();
        LWMissionSchema t2LWMissionSchema = new LWMissionSchema();

        t1LWMissionSchema = tActivityOperator.CreateOnlyOneMission("0000000003",
                "0000001101", mInputData);
        t2LWMissionSchema = tActivityOperator.CreateOnlyOneMission("0000000003",
                "0000001104", mInputData);

        MMap map = new MMap();
        if (t1LWMissionSchema != null)
        {
            map.put(t1LWMissionSchema, "INSERT");
        }
        if (t1LWMissionSchema != null)
        {
            map.put(t2LWMissionSchema, "INSERT");
        }
        tVData.add(map);
        PubSubmit tPubSubmit = new PubSubmit();
        if (!tPubSubmit.submitData(tVData, ""))
        {
            CError tError = new CError();
            tError.moduleName = "UWSendTraceBL";
            tError.functionName = "submitData";
            tError.errorMessage = "数据库提交失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * 自动结束问题件回销节点
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @param mOperate String
     * @return boolean
     */
    private boolean Execute000000112345(String mOperate)
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
            tError.moduleName = "TBWorkFlowBL";
            tError.functionName = "Execute0000001123";
            tError.errorMessage = "前台传输数据TransferData中的必要参数MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (tSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "TBWorkFlowBL";
            tError.functionName = "Execute0000001123";
            tError.errorMessage = "前台传输数据TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        tActivityOperator.DeleteMission(tMissionID, tSubMissionID, mOperate, mInputData);
        MMap map = new MMap();
        map = ((MMap) tActivityOperator.getResult().getObjectByObjectName("MMap", 0));
        tVData.add(map);
        PubSubmit tPubSubmit = new PubSubmit();
        if (!tPubSubmit.submitData(tVData, ""))
        {
            CError tError = new CError();
            tError.moduleName = "TBWorkFlowBL";
            tError.functionName = "submitData";
            tError.errorMessage = "数据库提交失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * 准备需要保存的数据
     * @return boolean
     */
    private boolean prepareOutputData()
    {
        try
        {
            MMap tmap = new MMap();
            for (int i = 0; i < mResult.size(); i++)
            {
                VData tData = new VData();
                tData = (VData) mResult.get(i);
                MMap map = (MMap) tData.getObjectByObjectName("MMap", 0);
                tmap.add(map);
            }
            tResult.add(tmap);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断是否有过初审
     * @return boolean
     */
    private boolean CheckFirstTrial()
    {

        VData tVData = new VData();
        LWMissionSet tLWMissionSet = new LWMissionSet();
        LWMissionSchema tLWMissionSchema = new LWMissionSchema();
        LWMissionDB tLWMissionDB = new LWMissionDB();
        tLWMissionDB.setActivityID("0000001062");
        tLWMissionDB.setProcessID("0000000003");
        tLWMissionDB.setMissionProp1((String) mTransferData.getValueByName("PrtNo"));
        tLWMissionSet = tLWMissionDB.query();
        if (tLWMissionSet.size() == 0)
        {
            return false;
        }
        MMap map = new MMap();

        tLWMissionSchema = tLWMissionSet.get(1);
        map.put("delete from lwmission where missionid='" +
                tLWMissionSchema.getMissionID() + "' and activityid = '0000001062'",
                "DELETE"); //删除以前的节点
        tLWMissionSchema.setActivityID("0000001098");
        if (mTransferData.getValueByName("SubType") != null)
        {
            tLWMissionSchema.setMissionProp5((String) mTransferData.getValueByName("SubType"));
        }
        tLWMissionSchema.setLastOperator(mGlobalInput.Operator);
        tLWMissionSchema.setModifyDate(PubFun.getCurrentDate());
        tLWMissionSchema.setModifyTime(PubFun.getCurrentTime());
        tLWMissionSchema.setMissionProp2(PubFun.getCurrentDate());
        map.put(tLWMissionSchema, "INSERT"); //生成新的节点
        tVData.add(map);
        mResult.add(tVData);
        return true;
    }

    public VData getResult()
    {
        return tResult;
    }

    /**
     * 判断是否需要抽检
     * @param tTransferData TransferData
     * @return boolean
     */
    private boolean CheckDraw(TransferData tTransferData)
    {
        if (mOperate.equals("0000001098") || mOperate.equals("0000001099"))
        {
            String mContNo = (String) tTransferData.getValueByName("ContNo");
            LDSpotTrackSet tLDSpotTrackSet = new LDSpotTrackSet();
            LDSpotTrackDB tLDSpotTrackDB = new LDSpotTrackDB();
            tLDSpotTrackDB.setOtherNo(mContNo);
            tLDSpotTrackDB.setOtherType("spotbargain");
            tLDSpotTrackSet = tLDSpotTrackDB.query();
            if (tLDSpotTrackSet.size() >= 1)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 测试函数
     * @param args String[]
     */
    public static void main(String[] args)
    {
        VData cInputData = new VData();
        String cOperate = "";
        boolean expectedReturn = true;
        GlobalInput tGlobalInput = new GlobalInput();
        tGlobalInput.Operator = "001";
        tGlobalInput.ManageCom = "861100";
        tGlobalInput.ComCode = "001";
        cInputData.add(tGlobalInput);
        LCContSet tLCContSet = new LCContSet();
        LCContSchema tLCContSchema = new LCContSchema();
        tLCContSchema.setContNo( "9015000000747788");
        tLCContSchema.setPrtNo( "1001010512300188");
        //重新初始化Set对象
        tLCContSet = new LCContSet();
        tLCContSet.add( tLCContSchema );

        TransferData tTransferData = new TransferData();
        tTransferData.setNameAndValue("MissionID", "00000000000000003276");
        tTransferData.setNameAndValue("SubMissionID", "1");
        cInputData.add(tLCContSet);
        cInputData.add(tTransferData);
        TbWorkFlowBL ttTbWorkFlowBL = new TbWorkFlowBL();
        boolean actualReturn = ttTbWorkFlowBL.submitData(cInputData, "0000001150");
    }
}

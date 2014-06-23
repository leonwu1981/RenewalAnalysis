/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;
import com.sinosoft.lis.db.LWMissionDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LWMissionSchema;
import com.sinosoft.lis.vschema.LWMissionSet;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */
public class GrpTbWorkFlowBL
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
    String tPrtNo;//印刷号
    String lockKey = "";//锁值为：管理机构加印刷号
    String lockuser = "";//加锁用户
    String lockinfo ="";//锁信息
    ExeSQL mExeSQL = new ExeSQL();
    public GrpTbWorkFlowBL()
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
        if(!submitAddLock())//调用加锁内容
        {
        	 return false;
        }
        // 数据操作业务处理
        if (!dealData())
        {
        	if(!submitUnLock())//调用解锁内容
        	{
        		 return false;
        	}
            return false;
        }

        System.out.println("---GrpTbWorkFlowBL dealData---");

        //准备给后台的数据
        if (!prepareOutputData())
        {
        	if(!submitUnLock())//调用解锁内容
        	{
        		 return false;
        	}
            return false;
        }

        System.out.println("---GrpTbWorkFlowBL prepareOutputData---");

        //数据提交
        GrpTbWorkFlowBLS tGrpTbWorkFlowBLS = new GrpTbWorkFlowBLS();
        System.out.println("Start GrpTbWorkFlowBL Submit...");

        if (tGrpTbWorkFlowBLS.submitData(mResult, mOperate))
        {
        	if(!submitUnLock())//调用解锁内容
        	{
        		 return false;
        	}
            System.out.println("---GrpTbWorkFlowBLS commitData End ---");
            return true;
        }
        else
        {
        	if(!submitUnLock())//调用解锁内容
        	{
        		 return false;
        	}
            // @@错误处理
            this.mErrors.copyAllErrors(tGrpTbWorkFlowBLS.mErrors);

            CError tError = new CError();
            tError.moduleName = "GrpTbWorkFlowBL";
            tError.functionName = "submitData";
            tError.errorMessage = "数据提交失败!";
            this.mErrors.addOneError(tError);
            System.out.println("---GrpTbWorkFlowBLS commitData End ---");
            return false;
        }
    }

    //guoly-2009-03-24-TASK000024--契约加锁（录入完毕、复核、人工核保、核保订正）/进行并发加锁操作控制
    public boolean submitAddLock()//对其提交内容加锁
    {
        lockuser = mGlobalInput.Operator;
        lockinfo ="";
        //为防止和调用此类的其他内容有冲突，所以暂只判断此需求内容-契约加锁（录入完毕、复核、人工核保、核保订正）进行加锁
        //经核实契约（录入完毕、新单复核、人工核保、核保订正）均会调用此工作流处理类，固将其锁加在此类
        if(mOperate.equals("0000002005")||mOperate.equals("0000002098")//0000002005	团单核保订正 0000002098	团单新契约无扫描待录入
          //0000002001 ,团单新单复核  0000002002 ,团单复核修改  0000002003 团单自动核保//0000001001 工作流新契约复核确认
          ||mOperate.equals("0000002001")||mOperate.equals("0000002002")||mOperate.equals("0000002003")
          ||mOperate.equals("0000002008")//团单返回新单复核
          ||mOperate.equals("0000002004"))//0000002004 团单人工核保
          
        {
		     if(mOperate.equals("0000002098"))//团单新契约无扫描待录入，状态流程(用任务节点判断)
		     {
		         tPrtNo=(String) mTransferData.getValueByName("PrtNo");//无扫描录入可直接接收传入的印刷号
		         lockinfo="用户："+lockuser+"，正在进行契约‘录入完毕’操作。";
		     }
		     if(mOperate.equals("0000002005"))//团单核保订正，状态流程(用任务节点判断)
		     {
		    	 String sqlPrtNo="select n.missionprop2 from lwmission n where n.activityid='0000002005'  "
 		                        +" and n.missionid='"+(String) mTransferData.getValueByName("MissionID")+"'"
 		                        +" and n.submissionid='"+(String) mTransferData.getValueByName("SubMissionID")+"'"
 		                        ;
                 tPrtNo=mExeSQL.getOneValue(sqlPrtNo);//核保订正，要根据传入的MissionID&SubMissionID，获取其印刷号
		         lockinfo="用户："+lockuser+"，正在进行契约‘核保订正’操作。";
		     }
		     //0000002001 ,团单新单复核  0000002002 ,团单复核修改  0000002003 团单自动核保
		     if(mOperate.equals("0000002001")||mOperate.equals("0000002002")||mOperate.equals("0000002003"))
		     {
		    	 tPrtNo=(String) mTransferData.getValueByName("PrtNo");//新单复核’可直接接收传入的印刷号
			     lockinfo="用户："+lockuser+"，正在进行契约‘新单复核’操作。";
		     }
		     //0000002004 团单人工核保
		     if(mOperate.equals("0000002004"))
		     {
		    	 String sqlPrtNo="select n.missionprop2 from lwmission n where n.activityid='0000002004' and "
                                +" n.missionid='"+(String) mTransferData.getValueByName("MissionID")+"'"
                                +" and n.submissionid='"+(String) mTransferData.getValueByName("SubMissionID")+"'"
                                ;
		    	 tPrtNo=mExeSQL.getOneValue(sqlPrtNo);//人工核保’从工作流表中获取印刷号
			     lockinfo="用户："+lockuser+"，正在进行契约‘人工核保’操作。";
		     }
		     //0000002008 团单返回新单复核
		     if(mOperate.equals("0000002008"))
		     {
		    	 tPrtNo=(String) mTransferData.getValueByName("PrtNo");//返回新单复核可直接接收传入的印刷号
			     lockinfo="用户："+lockuser+"，正在进行契约‘团单返回新单复核’操作。";
		     }
		     if(tPrtNo==null||tPrtNo.equals(""))
		     {
		    	 CError.buildErr(this, "获取印刷号失败，请核实数据无误后再进行业务操作！");
		         return false;
		     }
		     lockKey = mManageCom +tPrtNo;//锁值为：管理机构加印刷号
		     if(!LockUtil.lock(SysConst.QY_LOCK_TYPE, lockKey, lockuser ,lockinfo))
		     {
		        	String info = " select information from ldlock where locktype='QY' and lockvalue='"+lockKey+"'";
			        String tinfo = mExeSQL.getOneValue(info);
		        	CError.buildErr(this, "管理机构："+mManageCom+"下"+" 印刷号: "+tPrtNo +" 己被锁定，锁值为： "+lockKey+"，不能进行并发操作！ 错误信息为:"+tinfo+"");
		        	return false;
		     }
        }
    	return true;
    }
    //guoly-2009-03-24-TASK000024--契约加锁（录入完毕、复核、人工核保、核保订正）/进行解锁
    public boolean submitUnLock()//对其加锁内容解锁
    {
        //为防止和调用此类的其他内容有冲突，所以暂只判断此需求内容-契约加锁（录入完毕、核保订正）进行加锁
        if(mOperate.equals("0000002005")||mOperate.equals("0000002098")//0000002005	团单核保订正 0000002098	团单新契约无扫描待录入
    	   //0000002001 ,团单新单复核  0000002002 ,团单复核修改  0000002003 团单自动核保//0000001001 工作流新契约复核确认
    	   ||mOperate.equals("0000002001")||mOperate.equals("0000002002")||mOperate.equals("0000002003")
    	   ||mOperate.equals("0000002008")//团单返回新单复核
    	   ||mOperate.equals("0000002004"))//0000002004 团单人工核保
        {
            //对上面的契约（录入完毕、新单复核、人工核保、核保订正）解锁
	        if(!LockUtil.unlock(SysConst.QY_LOCK_TYPE, lockKey, this)){
	        	CError.buildErr(this, "管理机构"+mManageCom+"下,"+" 印刷号: "+tPrtNo +" 解锁失败！");
				return false;
	        }
        }
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
            tError.moduleName = "GrpTbWorkFlowBL";
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
            tError.moduleName = "GrpTbWorkFlowBL";
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
            tError.moduleName = "GrpTbWorkFlowBL";
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
            tError.moduleName = "GrpTbWorkFlowBL";
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
        //承保工作流打印核保通知书活动表
        if (mOperate.trim().equals("6999999999"))
        {
            if (!Execute6999999999())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "GrpTbWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "产生承保工作流待人工核保活动表起始任务失败!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
        }
        else if (mOperate.trim().equals("7699999999"))
        {
            if (!Execute7699999999())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;

            }
            return true;
        }
        else if (mOperate.trim().equals("7899999999"))
        {
            if (!Execute7899999999())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;

            }
            return true;
        }

        else
        {
            if (!Execute())
            {
                // @@错误处理
//                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
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

        //获得当前工作任务的任务ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        if (tMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpTbWorkFlowBL";
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
            tError.moduleName = "GrpTbWorkFlowBL";
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
            tError.moduleName = "GrpTbWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎执行新契约活动表任务出错!";
            this.mErrors.addOneError(tError);

            return false;
        }

        //*/
        return true;
    }

    private boolean Execute7699999999()
    {
        mResult.clear();
//        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //执行发放体检通知书虚拟任务(当产生任务与执行任务为同一事务时,采用执行虚拟任务模式工作)
        try
        {
            System.out.println("ActivityOperator name:" + mActivityOperator.getClass());
            if (CheckGrpFirstTrial())
            {
                return true;
            }
            //产生执行完发放体检通知书任务后的下一打印体检通知书任务节点
//            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
            if (tActivityOperator.CreateStartMission("0000000004", "0000002098",
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
            tError.functionName = "Execute7899999999";
            tError.errorMessage = "工作流引擎工作出现异常!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    private boolean Execute7899999999()
      {
          mResult.clear();
//        VData tVData = new VData();
          ActivityOperator tActivityOperator = new ActivityOperator();
          //执行发放体检通知书虚拟任务(当产生任务与执行任务为同一事务时,采用执行虚拟任务模式工作)
          try
          {
              System.out.println("ActivityOperator name:" + mActivityOperator.getClass());

              //产生执行完发放体检通知书任务后的下一打印体检通知书任务节点
//            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
              if (tActivityOperator.CreateStartMission("0000000004", "0000002095",
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
              tError.functionName = "Execute7899999999";
              tError.errorMessage = "工作流引擎工作出现异常!";
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
            tError.moduleName = "GrpTbWorkFlowBL";
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
            tError.moduleName = "GrpTbWorkFlowBL";
            tError.functionName = "Execute0000001100";
            tError.errorMessage = "前台传输数据TransferData中的必要参数SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                    "0000001100", mInputData))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "GrpTbWorkFlowBL";
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
                    "0000001100", mInputData))
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
            tError.moduleName = "GrpTbWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "工作流引擎执行承保工作流待人工核保活动表任务出错!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/
        return true;
    }

    /**
     * 创建起始任务节点
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @return boolean
     */
    private boolean Execute6999999999()
    {
        mResult.clear();
        VData tVData = new VData();

        //团单录入完毕校验
        GrpFirstWorkFlowCheck tGrpFirstWorkFlowCheck = new GrpFirstWorkFlowCheck();

        if (tGrpFirstWorkFlowCheck.submitData(mInputData, ""))
        {
            tVData = tGrpFirstWorkFlowCheck.getResult();
            mResult.add(tVData);
        }
        else
        {
            this.mErrors.copyAllErrors(tGrpFirstWorkFlowCheck.mErrors);
            return false;
        }
        //执行发放体检通知书虚拟任务(当产生任务与执行任务为同一事务时,采用执行虚拟任务模式工作)
        try
        {
            System.out.println("ActivityOperator name:" + mActivityOperator.getClass());

            //产生执行完发放体检通知书任务后的下一打印体检通知书任务节点
//            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
            if (mActivityOperator.CreateStartMission("0000000004", "0000002001",
                    mInputData))
            {
                VData tempVData = new VData();
                tempVData = mActivityOperator.getResult();
                mResult.add(tempVData);
                tempVData = null;
            }
            else
            {
                // @@错误处理
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "GrpTbWorkFlowBL";
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
            tError.moduleName = "GrpTbWorkFlowBL";
            tError.functionName = "Execute7999999999";
            tError.errorMessage = "工作流引擎工作出现异常!";
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
        //mInputData.add( mGlobalInput );
        return true;
    }

    private boolean CheckGrpFirstTrial()
{

    VData tVData = new VData();
    LWMissionSet tLWMissionSet = new LWMissionSet();
    LWMissionSchema tLWMissionSchema = new LWMissionSchema();
    LWMissionDB tLWMissionDB = new LWMissionDB();
    tLWMissionDB.setActivityID("0000002096");
    tLWMissionDB.setProcessID("0000000004");
    tLWMissionDB.setMissionProp1((String) mTransferData.getValueByName("PrtNo"));
    tLWMissionSet = tLWMissionDB.query();
    if (tLWMissionSet.size() == 0)
    {
        return false;
    }
    MMap map = new MMap();

    tLWMissionSchema = tLWMissionSet.get(1);
    map.put("delete from lwmission where missionid='" +
            tLWMissionSchema.getMissionID() + "' and activityid = '0000002096'",
            "DELETE"); //删除以前的节点
    tLWMissionSchema.setActivityID("0000002098");
    if (mTransferData.getValueByName("SubType") != null)
    {
        tLWMissionSchema.setMissionProp5((String) mTransferData.getValueByName("SubType"));
    }
    tLWMissionSchema.setLastOperator(mGlobalInput.Operator);
    //chenwm080125 记录无扫描录入时候的申请人,以前沿用的是初审录入的申请人.
    tLWMissionSchema.setMissionProp4(mGlobalInput.Operator);
    tLWMissionSchema.setModifyDate(PubFun.getCurrentDate());
    tLWMissionSchema.setModifyTime(PubFun.getCurrentTime());
    tLWMissionSchema.setMissionProp2(PubFun.getCurrentDate());
    map.put(tLWMissionSchema, "INSERT"); //生成新的节点
    tVData.add(map);
    mResult.add(tVData);
    return true;
}

}


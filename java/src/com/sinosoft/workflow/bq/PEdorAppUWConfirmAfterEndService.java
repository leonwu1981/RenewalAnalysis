package com.sinosoft.workflow.bq;

import com.sinosoft.workflowengine.*;
import java.lang.*;
import java.util.*;
import com.sinosoft.lis.tb.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.vdb.*;
import com.sinosoft.lis.bl.*;
import com.sinosoft.lis.vbl.*;
import com.sinosoft.utility.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.cbcheck.*;
import com.sinosoft.lis.f1print.*;
import com.sinosoft.lis.bq.PEdorAppCancelBL;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PEdorAppUWConfirmAfterEndService implements AfterEndService{

  /** 错误处理类，每个需要错误处理的类中都放置该类 */
	public  CErrors mErrors = new CErrors();
	/** 往后面传输数据的容器 */
	private VData mInputData ;
	/** 往界面传输数据的容器 */
	private VData mResult = new VData();
	/** 往工作流引擎中传输数据的容器 */
	private GlobalInput mGlobalInput = new GlobalInput();
	//private VData mIputData = new VData();
	private TransferData mTransferData = new TransferData();
	/** 数据操作字符串 */
	private String mOperater;
	private String mManageCom;
	private String mOperate;
	private String mString ;
	/** 业务数据操作字符串 */
	private String mEdorNo;
	private String mPolNo;
	private String mMissionID ;
	private String mSubMissionID ;
	private String mInsuredNo;
	private String mUWState;
	private String mUWIdea;
	private String mAppGrade;//上保核保师级别
	private String mAppUser;//上报核保师编码
	private Reflections mReflections = new Reflections();
        private MMap map = new MMap();

   /**执行保全工作流特约活动表任务0000000001*/
   /**保单表*/
   private LCPolSchema mLCPolSchema = new LCPolSchema();
   /** 工作流任务节点表*/
   private LWMissionSchema mInitLWMissionSchema = new LWMissionSchema();//保全人工核保工作流起始节点
   /** 工作流任务节点备份表*/
   private LWMissionSet mLWMissionSet = new LWMissionSet();
   private LBMissionSet mLBMissionSet = new LBMissionSet();

  public PEdorAppUWConfirmAfterEndService() {
  }

  /**
   * 传输数据的公共方法
   * @param: cInputData 输入的数据
   *         cOperate 数据操作
   * @return:
   */
  public boolean submitData(VData cInputData,String cOperate)
  {
	  //得到外部传入的数据,将数据备份到本类中
	 if (!getInputData(cInputData,cOperate))
	   return false;


	 //进行业务处理
	 if (!dealData())
		 return false;

	 //准备往后台的数据
	 if (!prepareOutputData())
	   return false;

	return true;
  }


  /**
   * 从输入数据中得到所有对象
   *输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
   */
  private boolean getInputData(VData cInputData,String cOperate)
  {
	//从输入数据中得到所有对象
	//获得全局公共数据
	mGlobalInput.setSchema((GlobalInput)cInputData.getObjectByObjectName("GlobalInput",0));
	mTransferData = (TransferData)cInputData.getObjectByObjectName("TransferData",0);
	mInputData = cInputData ;
	if ( mGlobalInput == null  )
	{
	  // @@错误处理
	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
	  CError tError = new CError();
	  tError.moduleName = "PEdorUWConfirmAfterEndService";
	  tError.functionName = "getInputData";
	  tError.errorMessage = "前台传输全局公共数据失败!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}

	//获得操作员编码
	mOperater = mGlobalInput.Operator;
	if ( mOperater == null || mOperater.trim().equals("") )
	{
	  // @@错误处理
	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
	  CError tError = new CError();
	  tError.moduleName = "PEdorUWConfirmAfterEndService";
	  tError.functionName = "getInputData";
	  tError.errorMessage = "前台传输全局公共数据Operate失败!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}

	//获得登陆机构编码
	mManageCom = mGlobalInput.ManageCom;
	if ( mManageCom == null || mManageCom.trim().equals("") )
	{
	  // @@错误处理
	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
	  CError tError = new CError();
	  tError.moduleName = "PEdorUWConfirmAfterEndService";
	  tError.functionName = "getInputData";
	  tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}

	mOperate = cOperate;

	//获得业务数据
	if ( mTransferData == null  )
	{
	  // @@错误处理
	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
	  CError tError = new CError();
	  tError.moduleName = "PEdorUWConfirmAfterEndService";
	  tError.functionName = "getInputData";
	  tError.errorMessage = "前台传输业务数据失败!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}

	mEdorNo = (String)mTransferData.getValueByName("EdorNo");
	if ( mEdorNo == null  )
	{
	  // @@错误处理
	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
	  CError tError = new CError();
	  tError.moduleName = "PEdorUWConfirmAfterEndService";
	  tError.functionName = "getInputData";
	  tError.errorMessage = "前台传输业务数据中EdorNo失败!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}

	mPolNo = (String)mTransferData.getValueByName("PolNo");
	if ( mPolNo == null  )
	{
	  // @@错误处理
	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
	  CError tError = new CError();
	  tError.moduleName = "PEdorUWConfirmAfterEndService";
	  tError.functionName = "getInputData";
	  tError.errorMessage = "前台传输业务数据中PolNo失败!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}



	//获得业务体检通知数据

	//获得当前工作任务的任务ID
	  mMissionID = (String)mTransferData.getValueByName("MissionID");
	   if ( mMissionID == null  )
	  {
		// @@错误处理
		//this.mErrors.copyAllErrors( tLCPolDB.mErrors );
		CError tError = new CError();
		tError.moduleName = "PEdorUWConfirmAfterEndService";
		tError.functionName = "getInputData";
		tError.errorMessage = "前台传输业务数据中MissionID失败!";
		this.mErrors .addOneError(tError) ;
		return false;
	 }

	 //获得当前工作任务的任务ID
	  mSubMissionID = (String)mTransferData.getValueByName("SubMissionID");
	   if ( mMissionID == null  )
	  {
		// @@错误处理
		//this.mErrors.copyAllErrors( tLCPolDB.mErrors );
		CError tError = new CError();
		tError.moduleName = "PEdorUWConfirmAfterEndService";
		tError.functionName = "getInputData";
		tError.errorMessage = "前台传输业务数据中SubMissionID失败!";
		this.mErrors .addOneError(tError) ;
		return false;
	 }

	 //获得业务保全核保确认数据
	//mContType = (String)cInputData.get(0);
	LPUWMasterMainSchema tLPUWMasterMainSchema = new LPUWMasterMainSchema();
	tLPUWMasterMainSchema = (LPUWMasterMainSchema)mTransferData.getValueByName("LPUWMasterMainSchema");
	if ( tLPUWMasterMainSchema == null   )
	{
	  // @@错误处理
	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
	  CError tError = new CError();
	  tError.moduleName = "PEdorUWConfirmAfterEndService";
	  tError.functionName = "getInputData";
	  tError.errorMessage = "前台传输获得业务批单核保主表数据失败!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}

	if(tLPUWMasterMainSchema.getPassFlag()==null ||tLPUWMasterMainSchema.getPassFlag().trim().equals(""))
	{
	  // @@错误处理
	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
	  CError tError = new CError();
	  tError.moduleName = "PEdorUWConfirmAfterEndService";
	  tError.functionName = "getInputData";
	  tError.errorMessage = "前台传输获得业务批单核保主表中的核保结论数据失败!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}

	mUWState = tLPUWMasterMainSchema.getPassFlag();
	mUWIdea = tLPUWMasterMainSchema.getUWIdea();

	mAppUser = (String)mTransferData.getValueByName("AppUser");
	if( mUWState.trim().equals("6") && (mAppUser == null || mAppUser.trim().equals("")))
	{
	  // @@错误处理
	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
	  CError tError = new CError();
	  tError.moduleName = "PEdorUWConfirmAfterEndService";
	  tError.functionName = "getInputData";
	  tError.errorMessage = "前台传输业务数据中上报核保师编码失败!";
	  this.mErrors .addOneError(tError) ;
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
	//保全核保工作流起始节点状态改变
	if (prepareMission() == false)
		return false;

	 return true;

  }

  /**
	* 准备打印信息表
	* @return
	*/
   private boolean prepareMission()
   {

	 //上报核保修改保全人工核保的起始节点默认操作员为上保核保师
	 if(mUWState.equals("6"))
	 {
	   LWMissionDB tLWMissionDB = new LWMissionDB();
	   LWMissionSet tLWMissionSet = new LWMissionSet();
	   String tStr = "Select * from LWMission where MissionID = '"+mMissionID+"'"
				+ "and ActivityID = '0000000000'";
	   tLWMissionSet = tLWMissionDB.executeQuery(tStr);
	   if(tLWMissionSet == null || tLWMissionSet.size() !=1)
	   {
		 // @@错误处理
		 this.mErrors.copyAllErrors(tLWMissionSet.mErrors);
		 CError tError =new CError();
		 tError.moduleName="PEdorUWConfirmAfterEndService";
		 tError.functionName="prepareMission";
		 tError.errorMessage="查询工作流保全人工核保的起始任务节点失败!";
		 this.mErrors .addOneError(tError) ;
		 return false;
	   }
	   mInitLWMissionSchema = tLWMissionSet.get(1) ;
	   mInitLWMissionSchema.setDefaultOperator(mAppUser);


	 }

     //下保全核保最终结论
        if (mUWState.equals("1") || mUWState.equals("9") || mUWState.equals("8")||mUWState.equals("4")||mUWState.equals("E")||mUWState.equals("L")||mUWState.equals("a"))
	 {
	   mInitLWMissionSchema = null;

	   LWMissionDB tLWMissionDB = new LWMissionDB();
	   String tStr = "Select * from LWMission where MissionID = '"+mMissionID+"' and ActivityID <> '0000000010'";

	   mLWMissionSet = tLWMissionDB.executeQuery(tStr);
	   if(mLWMissionSet == null )
	   {
		 // @@错误处理
		 this.mErrors.copyAllErrors(mLWMissionSet.mErrors);
		 CError tError =new CError();
		 tError.moduleName="PEdorUWConfirmAfterEndService";
		 tError.functionName="prepareMission";
		 tError.errorMessage="查询工作流保全人工核保的任务节点失败!";
		 this.mErrors .addOneError(tError) ;
		 return false;
	   }

	   for(int i = 1 ;i<=mLWMissionSet.size();i++)
	   {
		 LWMissionSchema tLWMissionSchema = new LWMissionSchema();
		 LBMissionSchema tLBMissionSchema = new LBMissionSchema();

		 tLWMissionSchema = mLWMissionSet.get(i);
		 String tSerielNo = PubFun1.CreateMaxNo("MissionSerielNo", 10);
		 mReflections.transFields(tLBMissionSchema,tLWMissionSchema);
		 tLBMissionSchema.setSerialNo(tSerielNo);
		 tLBMissionSchema.setActivityStatus("4");//节点任务执行强制移除
		 tLBMissionSchema.setLastOperator(mOperater);
		 tLBMissionSchema.setMakeDate(PubFun.getCurrentDate());
		 tLBMissionSchema.setMakeTime(PubFun.getCurrentTime());
		 mLBMissionSet.add(tLBMissionSchema) ;
	   }

           //如果客户不同意，直接撤销本次申请。
           //add by Lanjun 2005/12/18
           if(mUWState.equals("a")||mUWState.equals("1"))
           {
             LPEdorAppSchema tLPEdorAppSchema  = new LPEdorAppSchema();

             String sqlEdorAcceptNo =
                 "select edoracceptno from lpedormain where edorno='"
                 + mEdorNo + "'";
             ExeSQL tExeSQL = new ExeSQL();
             String edorAcceptNo = tExeSQL.getOneValue(sqlEdorAcceptNo);

             tLPEdorAppSchema.setEdorAcceptNo(edorAcceptNo);

             String delReason = "核保不通过";
             String reasonCode = "6";
             TransferData tTransferData = new TransferData();
             tTransferData.setNameAndValue("DelReason", delReason);
             tTransferData.setNameAndValue("ReasonCode", reasonCode);

             // 准备传输数据 VData
             VData tVData = new VData();
             tVData.addElement(mGlobalInput);
             tVData.addElement(tLPEdorAppSchema);
             tVData.addElement(tTransferData);

             //调用保全申请删除类，和本次工作流过程执行同一个事务提交。
             PEdorAppCancelBL tPEdorAppCancelBL = new PEdorAppCancelBL();
             if (!tPEdorAppCancelBL.submitData(tVData, "EDORAPP")) {
               this.mErrors.copyAllErrors(tPEdorAppCancelBL.mErrors);
               return false;
             }
             else {
               map.add(tPEdorAppCancelBL.getMap());
             }
           }



	 }
	 return true;
   }


  private boolean prepareOutputData()
 {
	 mResult.clear();
	 //添加保全工作流起始任务节点表数据
	 if( mUWState.equals("6") && mInitLWMissionSchema != null  )
	 {
	   map.put(mInitLWMissionSchema, "UPDATE");
	 }

	 //添加相关工作流同步强制执行完毕的任务节点表数据
	 if(mLWMissionSet != null && mLWMissionSet.size()>0)
	 {
	   map.put(mLWMissionSet, "DELETE");
	 }

	 //添加相关工作流同步强制执行完毕的任务节点备份表数据
	 if(mLBMissionSet != null && mLBMissionSet.size()>0)
	 {
	   map.put(mLBMissionSet, "INSERT");
	 }


	mResult.add(map);
   return true;
  }
  public VData getResult()
  {
	return mResult;
  }

  public TransferData getReturnTransferData()
  {
	return mTransferData;
  }
  public CErrors getErrors() {
	return mErrors;
  }
}

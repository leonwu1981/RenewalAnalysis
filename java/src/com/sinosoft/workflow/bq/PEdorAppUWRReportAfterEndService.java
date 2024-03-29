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
/**
 * <p>Title: 工作流节点任务:保全申请发体检通知书</p>
 * <p>Description: 工作流发体检通知书AfterEnd服务类 </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Lanjun
 * @version 1.0
 */

public class PEdorAppUWRReportAfterEndService implements AfterEndService{

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
        private String mEdorAcceptNo;
        private String mContNo;
        private String mInsuredNo;
        private String mMissionID ;
        private String mSubMissionID ;
        private Reflections mReflections = new Reflections();

   /**执行保全工作流特约活动表任务0000000019*/
   /**保单表*/
   private LCPolSchema mLCPolSchema = new LCPolSchema();
   /** 工作流任务节点表*/
   private LWMissionSchema mInitLWMissionSchema = new LWMissionSchema();//保全人工核保工作流起始节点
   /** 工作流任务节点备份表*/
   private LBMissionSet mLBMissionSet = new LBMissionSet();

  public PEdorAppUWRReportAfterEndService() {
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
          tError.moduleName = "PEdorUWAutoHealthAfterEndService";
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
          tError.moduleName = "PEdorUWAutoHealthAfterEndService";
          tError.functionName = "getInputData";
          tError.errorMessage = "前台传输全局公共数据Operate失败!";
          this.mErrors .addOneError(tError) ;
          return false;
        }

        //获得登陆机构编码
        mManageCom = mGlobalInput.ManageCom;
        System.out.println("class 信息:ManageCom"+mManageCom);
        if ( mManageCom == null || mManageCom.trim().equals("") )
        {
          // @@错误处理
          //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
          CError tError = new CError();
          tError.moduleName = "PEdorUWAutoHealthAfterEndService";
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
          tError.moduleName = "PEdorUWAutoHealthAfterEndService";
          tError.functionName = "getInputData";
          tError.errorMessage = "前台传输业务数据失败!";
          this.mErrors .addOneError(tError) ;
          return false;
        }

        mEdorAcceptNo = (String)mTransferData.getValueByName("EdorAcceptNo");
        if ( mEdorAcceptNo == null  )
        {
          // @@错误处理
          //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
          CError tError = new CError();
          tError.moduleName = "PEdorUWAutoHealthAfterEndService";
          tError.functionName = "getInputData";
          tError.errorMessage = "前台传输业务数据中EdorAcceptNo失败!";
          this.mErrors .addOneError(tError) ;
          return false;
        }
//
//	mContNo = (String)mTransferData.getValueByName("ContNo");
//	if ( mContNo == null  )
//	{
//	  // @@错误处理
//	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
//	  CError tError = new CError();
//	  tError.moduleName = "PEdorUWAutoHealthAfterEndService";
//	  tError.functionName = "getInputData";
//	  tError.errorMessage = "前台传输业务数据中PolNo失败!";
//	  this.mErrors .addOneError(tError) ;
//	  return false;
//	}
//
//
//
//	//获得业务体检通知数据
//
        //获得当前工作任务的任务ID
          mMissionID = (String)mTransferData.getValueByName("MissionID");
         System.out.println("class信息：PEdorAppUWRReportAfterEndService --->mMissionID="+mMissionID);
           if ( mMissionID == null  )
          {
                // @@错误处理
                //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                CError tError = new CError();
                tError.moduleName = "PEdorUWAutoHealthAfterEndService";
                tError.functionName = "getInputData";
                tError.errorMessage = "前台传输业务数据中MissionID失败!";
                this.mErrors .addOneError(tError) ;
                return false;
         }

         //获得当前工作任务的任务ID
          mSubMissionID = (String)mTransferData.getValueByName("SubMissionID");
         System.out.println("class信息：PEdorAppUWRReportAfterEndService --->mSubMissionID="+mSubMissionID);
           if ( mMissionID == null  )
          {
                // @@错误处理
                //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                CError tError = new CError();
                tError.moduleName = "PEdorUWAutoHealthAfterEndService";
                tError.functionName = "getInputData";
                tError.errorMessage = "前台传输业务数据中SubMissionID失败!";
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

         //修改保全人工核保的起始节点状态为未回复
         LWMissionDB tLWMissionDB = new LWMissionDB();
         LWMissionSet tLWMissionSet = new LWMissionSet();
         String tStr = "Select * from LWMission where MissionID = '"+mMissionID+"'"
                          + "  and ActivityID = '0000000022'";
        System.out.println(tStr);

         tLWMissionSet = tLWMissionDB.executeQuery(tStr);
         if(tLWMissionSet == null || tLWMissionSet.size() !=1)
         {
           // @@错误处理
           this.mErrors.copyAllErrors(tLWMissionSet.mErrors);
           CError tError =new CError();
           tError.moduleName="PEdorUWAutoHealthAfterEndService";
           tError.functionName="prepareMission";
           tError.errorMessage="查询工作流保全人工核保的起始任务节点失败!";
           this.mErrors .addOneError(tError) ;
           return false;
         }
         mInitLWMissionSchema = tLWMissionSet.get(1) ;
         mInitLWMissionSchema.setActivityStatus("2");
         mInitLWMissionSchema.setDefaultOperator(mOperater);

         return true;
   }


  private boolean prepareOutputData()
 {
         mResult.clear();
         MMap map = new MMap();

         //添加保全工作流起始任务节点表数据
         if(mInitLWMissionSchema != null  )
         {
           map.put(mInitLWMissionSchema, "UPDATE");
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

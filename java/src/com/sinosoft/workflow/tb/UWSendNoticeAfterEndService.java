/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LWMissionDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.schema.LWMissionSchema;
import com.sinosoft.lis.vschema.LBMissionSet;
import com.sinosoft.lis.vschema.LWMissionSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterEndService;


/**
 * <p>Title: 工作流节点任务:新契约发核保通知书</p>
 * <p>Description: 发核保通知书工作流AfterEnd服务类 </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class UWSendNoticeAfterEndService implements AfterEndService
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 往界面传输数据的容器 */
    private VData mResult = new VData();

    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();

    //private VData mIputData = new VData();
    private TransferData mTransferData = new TransferData();

    /** 数据操作字符串 */
    private String mOperater;
    private String mManageCom;

    /** 业务数据操作字符串 */
    private String mContNo;
    private String mMissionID;

    /** 工作流任务节点表*/
    private LWMissionSchema mInitLWMissionSchema = new LWMissionSchema(); //人工核保工作流起始节点
    private LWMissionSet mLWMissionSet = new LWMissionSet();

    /** 工作流任务节点备份表*/
    private LBMissionSet mLBMissionSet = new LBMissionSet();

    public UWSendNoticeAfterEndService()
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

        //进行业务处理
        if (!dealData())
        {
            return false;
        }

        //准备往后台的数据
        if (!prepareOutputData())
        {
            return false;
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
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);

        if (mGlobalInput == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWSendNoticeAfterEndService";
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWSendNoticeAfterEndService";
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWSendNoticeAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得业务数据
        if (mTransferData == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWSendNoticeAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mContNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWSendNoticeAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中ContNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得业务体检通知数据

        //获得当前工作任务的任务ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWSendNoticeAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     * @return boolean
     */
    private boolean dealData()
    {
        //保全核保工作流起始节点状态改变
        if (prepareMission())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 准备打印信息表
     * @return boolean
     */
    private boolean prepareMission()
    {
        //修改新契约人工核保的起始节点状态为未回复
        LWMissionDB tLWMissionDB = new LWMissionDB();
        LWMissionSet tLWMissionSet = new LWMissionSet();
        String tStr = "Select * from LWMission where MissionID = '" +
                mMissionID + "' and ActivityID = '0000001100'";
        System.out.println("tStr==" + tStr);
        tLWMissionSet = tLWMissionDB.executeQuery(tStr);
        if (tLWMissionSet == null || tLWMissionSet.size() != 1)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLWMissionSet.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWSendNoticeAfterEndService";
            tError.functionName = "prepareMission";
            tError.errorMessage = "查询工作流保全人工核保的起始任务节点失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mInitLWMissionSchema = tLWMissionSet.get(1);
        mInitLWMissionSchema.setActivityStatus("2");
        mInitLWMissionSchema.setDefaultOperator(mOperater);

//
//         //准备要删除的加费和特约工作流任务节点
//          tStr = "Select * from LWMission where MissionID = '"+mMissionID+"'"
//                          + "and ActivityID in( '0000000102','0000000103')";
//           mLWMissionSet = tLWMissionDB.executeQuery(tStr);
//         if( mLWMissionSet == null || mLWMissionSet.size() <0)
//         {
//           // @@错误处理
//           this.mErrors.copyAllErrors(tLWMissionSet.mErrors);
//           CError tError =new CError();
//           tError.moduleName="UWSendNoticeAfterEndService";
//           tError.functionName="prepareMission";
//           tError.errorMessage="查询工作流续保人工核保的加费和特约任务节点失败!";
//           this.mErrors .addOneError(tError) ;
//           return false;
//         }
//
//         for(int i = 1 ;i<=mLWMissionSet.size();i++)
//          {
//                LWMissionSchema tLWMissionSchema = new LWMissionSchema();
//                LBMissionSchema tLBMissionSchema = new LBMissionSchema();
//
//                tLWMissionSchema = mLWMissionSet.get(i);
//                String tSerielNo = PubFun1.CreateMaxNo("MissionSerielNo", 10);
//                mReflections.transFields(tLBMissionSchema,tLWMissionSchema);
//                tLBMissionSchema.setSerialNo(tSerielNo);
//                tLBMissionSchema.setActivityStatus("3");//节点任务执行完毕
//                tLBMissionSchema.setLastOperator(mOperater);
//                tLBMissionSchema.setMakeDate(PubFun.getCurrentDate());
//                tLBMissionSchema.setMakeTime(PubFun.getCurrentTime());
//                mLBMissionSet.add(tLBMissionSchema) ;
//          }
//
        return true;
    }

    /**
     * 准备
     * @return boolean
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();

        //添加相关工作流同步执行完毕的任务节点表数据
        if (mLWMissionSet != null && mLWMissionSet.size() > 0)
        {
            map.put(mLWMissionSet, "DELETE");
        }

        //添加相关工作流同步执行完毕的任务节点备份表数据
        if (mLBMissionSet != null && mLBMissionSet.size() > 0)
        {
            map.put(mLBMissionSet, "INSERT");
        }

        //添加修改续保工作流起始任务节点表数据
        if (mInitLWMissionSchema != null)
        {
            map.put(mInitLWMissionSchema, "UPDATE");
        }

        mResult.add(map);
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

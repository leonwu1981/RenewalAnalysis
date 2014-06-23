/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LWMissionDB;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.LBMissionSchema;
import com.sinosoft.lis.schema.LWMissionSchema;
import com.sinosoft.lis.vschema.LBMissionSet;
import com.sinosoft.lis.vschema.LWMissionSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterEndService;


/**
 * <p>Title: 工作流节点任务:新契约人工核保回收补费通知书工作流服务类 </p>
 * <p>Description:新契约人工核保补费通知书回收工作流AfterEnd服务类
 *                对回收成功后工作流节点的流转进行控制</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: SinoSoft </p>
 * @author HYQ
 * @version 1.0
 */

public class UWTakeBackAutoAddFeeAfterEndService implements AfterEndService
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 往界面传输数据的容器 */
    private VData mResult = new VData();

    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();

    /** 数据操作字符串 */
    private String mOperater;
    private String mManageCom;

    /** 业务数据操作字符串 */
    private String mContNo;
    private String mMissionID;
    private String mSubMissionID;
    private Reflections mReflections = new Reflections();

    /**执行保全工作流特约活动表任务0000001011*/
    /** 工作流任务节点表*/
    private LWMissionSchema mLWMissionSchema = new LWMissionSchema();
    private LWMissionSchema mInitLWMissionSchema = new LWMissionSchema(); //保全人工核保工作流起始节点
    private LWMissionSet mLWMissionSet = new LWMissionSet();

    /**初审标志位**/
    private boolean FirstTrialFlaog = false;
    /** 工作流任务节点备份表*/
    private LBMissionSet mLBMissionSet = new LBMissionSet();

    public UWTakeBackAutoAddFeeAfterEndService()
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

        //校验是否有未打印的体检通知书
        if (!checkData())
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
     * 准备返回前台统一存储数据
     * 输出：如果发生错误则返回false,否则返回true
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

        //添加保全工作流起始任务节点表数据
        if (mInitLWMissionSchema != null)
        {
            if (!FirstTrialFlaog) //added by tuqiang for FirstTrial
            {
                map.put(mInitLWMissionSchema, "UPDATE");
            }
        }

        mResult.add(map);
        return true;
    }


    /**
     * 校验业务数据
     * @return boolean
     */
    private boolean checkData()
    {
        //查询工作流当前任务轨迹表
        LWMissionDB tLWMissionDB = new LWMissionDB();
        LWMissionSet tLWMissionSet = new LWMissionSet();
        tLWMissionDB.setMissionID(mMissionID);
        tLWMissionDB.setActivityID("0000001130");
        tLWMissionDB.setSubMissionID(mSubMissionID);
        tLWMissionSet = tLWMissionDB.query();
        if (tLWMissionSet == null || tLWMissionSet.size() != 1)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
            tError.functionName = "checkData";
            tError.errorMessage = "查询工作流轨迹表LWMission失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLWMissionSchema = tLWMissionSet.get(1);
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
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
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
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
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
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
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
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
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
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中ContNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的任务ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的子任务ID
        mSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
        if (mSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中SubMissionID失败!";
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
        //核保工作流起始节点状态改变
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
        //查询出同一体检通知书(原打印流水号相同者)待回收的任务节点,并回收
        StringBuffer tSBql = new StringBuffer(256);
        tSBql.append("Select * from LWMission where MissionID = '");
        tSBql.append(mMissionID);
        tSBql.append("' and ActivityID = '0000001130' and SubMissionID <> '");
        tSBql.append(mSubMissionID);
        tSBql.append("' and MissionProp14 = '");
        tSBql.append(mLWMissionSchema.getMissionProp14());
        tSBql.append("' union Select * from LWMission where MissionID = '");
        tSBql.append(mMissionID);
        tSBql.append("' and ActivityID = '0000001131' and MissionProp14 = '");
        tSBql.append(mLWMissionSchema.getMissionProp14());
        tSBql.append("' union Select * from LWMission where MissionID = '");
        tSBql.append(mMissionID);
        tSBql.append("' and ActivityID = '0000001109' and MissionProp14 = '");
        tSBql.append(mLWMissionSchema.getMissionProp14());
        tSBql.append("'");
        LWMissionDB tLWMissionDB = new LWMissionDB();
        mLWMissionSet = tLWMissionDB.executeQuery(tSBql.toString());
        if (mLWMissionSet == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
            tError.functionName = "prepareMission";
            tError.errorMessage = "承保工作流起始任务节点查询出错!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (mLWMissionSet.size() < 0)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
            tError.functionName = "prepareMission";
            tError.errorMessage = "承保工作流起始任务节点LWMission查询出错!";
            this.mErrors.addOneError(tError);
            return false;
        }

        for (int i = 1; i <= mLWMissionSet.size(); i++)
        {
            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
            LBMissionSchema tLBMissionSchema = new LBMissionSchema();

            tLWMissionSchema = mLWMissionSet.get(i);
            String tSerielNo = PubFun1.CreateMaxNo("MissionSerielNo", 10);
            mReflections.transFields(tLBMissionSchema, tLWMissionSchema);
            tLBMissionSchema.setSerialNo(tSerielNo);
            tLBMissionSchema.setActivityStatus("3"); //节点任务执行完毕
            tLBMissionSchema.setLastOperator(mOperater);
            tLBMissionSchema.setMakeDate(PubFun.getCurrentDate());
            tLBMissionSchema.setMakeTime(PubFun.getCurrentTime());
            mLBMissionSet.add(tLBMissionSchema);
        }

        //判段该通知书回收后,是否该承保申请已处于人工核保已回复状态.
        tSBql = new StringBuffer(256);
        tSBql.append("Select count(1) from LWMission where MissionID = '");
        tSBql.append(mMissionID);
        tSBql.append("' and ActivityID in ('0000001111','0000001112','0000001113','0000001106','0000001107','0000001108','0000001109','0000001114','0000001115','0000001116','0000001017','0000001018','0000001019','0000001020','0000001021','0000001002')");
        String tReSult = new String();
        ExeSQL tExeSQL = new ExeSQL();
        tReSult = tExeSQL.getOneValue(tSBql.toString());
        if (tExeSQL.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tExeSQL.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterEndService";
            tError.functionName = "prepareMission";
            tError.errorMessage = "执行SQL语句失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (tReSult == null || tReSult.equals(""))
        {
            return false;
        }
        int tCount = 0;
        tCount = Integer.parseInt(tReSult); //已包括了本次节点及相关同步节点
        if (tCount > (mLWMissionSet.size() + 1))
        { //处于核保未回复状态,不用修改承保人工核保的起始节点状
            mInitLWMissionSchema = null;
        }
        else
        {
            //处于核保已回复状态,修改续保人工核保的起始节点状态为已回复
            LWMissionSet tLWMissionSet = new LWMissionSet();
            tSBql = new StringBuffer(128);
            tSBql.append("Select * from LWMission where MissionID = '");
            tSBql.append(mMissionID);
            tSBql.append("' and ActivityID = '0000001100'");
            tLWMissionSet = tLWMissionDB.executeQuery(tSBql.toString());
            if (tLWMissionSet == null || tLWMissionSet.size() != 1)
            {
                FirstTrialFlaog = true;
//                // @@错误处理
//                this.mErrors.copyAllErrors(tExeSQL.mErrors);
//                CError tError = new CError();
//                tError.moduleName = "UWTakeBackAutoHealthAfterEndService";
//                tError.functionName = "prepareMission";
//                tError.errorMessage = "查询工作流续保人工核保的起始任务节点失败!";
//                this.mErrors.addOneError(tError);
//                return false;
            }
            else
            {
                mInitLWMissionSchema = tLWMissionSet.get(1);
                mInitLWMissionSchema.setActivityStatus("3");
            }
        }
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

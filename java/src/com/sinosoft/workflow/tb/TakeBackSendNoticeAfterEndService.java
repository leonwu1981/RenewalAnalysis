/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterEndService;

/**
 * <p>Title: 工作流节点任务:新契约人工核保回收核保通知书</p>
 * <p>Description:人工核保核保通知书回收AfterEnd服务类 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class TakeBackSendNoticeAfterEndService implements AfterEndService
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
    private String mSubMissionID;
    private String mUpWorkFlowSql;
    private Reflections mReflections = new Reflections();

    /** 工作流任务节点表*/
    private LWMissionSchema mLWMissionSchema = new LWMissionSchema();
    private LWMissionSet mLWMissionSet = new LWMissionSet();

    /** 工作流任务节点备份表*/
    private LBMissionSet mLBMissionSet = new LBMissionSet();
    public TakeBackSendNoticeAfterEndService()
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
        map.put(mUpWorkFlowSql, "UPDATE");

        mResult.add(map);
        return true;
    }

    /**
     * 校验业务数据
     * @return
     */
    private boolean checkData()
    {
        //查询工作流当前任务轨迹表
        LWMissionDB tLWMissionDB = new LWMissionDB();
        LWMissionSet tLWMissionSet = new LWMissionSet();
        tLWMissionDB.setMissionID(mMissionID);
        tLWMissionDB.setActivityID("0000001112");
        tLWMissionDB.setSubMissionID(mSubMissionID);
        tLWMissionSet = tLWMissionDB.query();
        if (tLWMissionSet == null || tLWMissionSet.size() != 1)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PrintTakeBackSendNoticeAfterEndService";
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
     *输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
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
            tError.moduleName = "PrintTakeBackSendNoticeAfterEndService";
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
            tError.moduleName = "PrintTakeBackSendNoticeAfterEndService";
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
            tError.moduleName = "PrintTakeBackSendNoticeAfterEndService";
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
            tError.moduleName = "PrintTakeBackSendNoticeAfterEndService";
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
            tError.moduleName = "PrintTakeBackSendNoticeAfterEndService";
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
            tError.moduleName = "PrintTakeBackSendNoticeAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的任务ID
        mSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
        if (mSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PrintTakeBackSendNoticeAfterEndService";
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
     */
    private boolean dealData()
    {
        //承保核保工作流起始节点状态改变
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
     * @return
     */
    private boolean prepareMission()
    {
        /*
          1.若有业务员通知书节点
          2.是否有除己之外的核保通知书
          3.时否有机构问题件为回复
         */
        //查询出同一体检通知书(原打印流水号相同者)待回收的任务节点,并回收
//        boolean flag = false;

        String tStr = "Select * from LWMission where MissionID = '" +
                mMissionID + "' and ActivityID = '0000001112' and SubMissionID <> '"
                + mSubMissionID + "' and MissionProp14 = '" +
                mLWMissionSchema.getMissionProp14()
                + "' union Select * from LWMission where MissionID = '" +
                mMissionID + "' and ActivityID = '0000001115' and MissionProp14 = '" +
                mLWMissionSchema.getMissionProp14()
                + "' union Select * from LWMission where MissionID = '" +
                mMissionID + "' and ActivityID = '0000001107' and MissionProp14 = '" +
                mLWMissionSchema.getMissionProp14() + "'";

        LWMissionDB tLWMissionDB = new LWMissionDB();
        mLWMissionSet = tLWMissionDB.executeQuery(tStr);
        if (mLWMissionSet == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PrintTakeBackAutoHealthAfterEndService";
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
            tError.moduleName = "PrintTakeBackAutoHealthAfterEndService";
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

//判段该函件所在的状态 0000001112
        tStr = "Select count(*) from LWMission where MissionID = '" +
                mMissionID + "' and ActivityID in ('0000001107','0000001108','0000001023','0000001017','0000001106','0000001113','0000001025','0000001019','0000001111')";
        String tReSult = new String();
        ExeSQL tExeSQL = new ExeSQL();
        tReSult = tExeSQL.getOneValue(tStr);
        if (tReSult == null || tReSult.equals(""))
        {
            return false;
        }
        int tCount = 0;
        tCount = Integer.parseInt(tReSult); //已包括了本次节点及相关同步节点
        StringBuffer tSBql = new StringBuffer(128);
        if(tCount > 0)
        {
          tSBql.append("update lwmission set activitystatus='2' where missionid='");
          tSBql.append(mMissionID);
          tSBql.append("' and activityid='0000001100'");
          mUpWorkFlowSql = tSBql.toString();
        }
        else
        {
          tSBql.append("update lwmission set activitystatus='3' where missionid='");
          tSBql.append(mMissionID);
          tSBql.append("' and activityid='0000001100'");
          mUpWorkFlowSql = tSBql.toString();
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

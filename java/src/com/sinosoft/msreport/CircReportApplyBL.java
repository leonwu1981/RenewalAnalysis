/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import com.sinosoft.lis.db.LBMissionDB;
import com.sinosoft.lis.db.LWMissionDB;
import com.sinosoft.lis.pubfun.CalBase;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.pubfun.PubSubmit;
import com.sinosoft.lis.schema.LWMissionSchema;
import com.sinosoft.lis.vschema.LBMissionSet;
import com.sinosoft.lis.vschema.LWMissionSet;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.Reflections;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;
import com.sinosoft.workflowengine.ActivityOperator;


/**
 * <p>Title: Web业务系统承保个人单自动核保部分</p>
 * <p>Description: 逻辑处理类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author WHN
 * @version 1.0
 */
public class CircReportApplyBL
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往后面传输数据的容器 */
    private VData mInputData;
    /** 往界面传输数据的容器 */
    private VData mResult = new VData();
    /** 数据操作字符串 */
    private String mOperate;
    private String mManageCom;
    private String mOperater;
    private String mStatYear; //通过标记
    private String mStatMonth = "";

    /**续保人工核保工作流数据*/
    private VData tWorkFlowVData = new VData();
    private TransferData tWorkFlowTransferData = new TransferData();
    private LWMissionSet mLWMissionSet = new LWMissionSet();
    private LWMissionSchema mLWMissionSchema = new LWMissionSchema();
    /** 全局数据 */
    private GlobalInput mGlobalInput = new GlobalInput();
    private Reflections mReflections = new Reflections();
    private String CurrentDate = PubFun.getCurrentDate();
    private String CurrentTime = PubFun.getCurrentTime();
    private TransferData mTransferData = new TransferData();

    private CalBase mCalBase = new CalBase();

    public CircReportApplyBL()
    {}

    /**
     * 传输数据的公共方法
     * @param: cInputData 输入的数据
     *         cOperate 数据操作
     * @return:
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        int flag = 0; //判断是不是所有数据都不成功
        int j = 0; //符合条件数据个数

        //将操作数据拷贝到本类中
        mInputData = (VData) cInputData.clone();

        System.out.println("---1---");
        //得到外部传入的数据,将数据备份到本类中
        if (!getInputData(cInputData))
        {
            return false;
        }

        if (!CheckData())
        {
            return false;
        }

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
        PubSubmit tPubSubmit = new PubSubmit();
        System.out.println("Start CircReportApplyBL Submit...");
        if (!tPubSubmit.submitData(mInputData, mOperate))
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tPubSubmit.mErrors);
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "submitData";
            tError.errorMessage = "数据提交失败!";
            //this.mErrors .addOneError(tError) ;
            return false;
        }
        System.out.println("---CircReportApplyBL commitData---");
        return true;
    }

    /**
     * 数据操作类业务处理
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean dealData()
    {
        if (!prepareWorkFlowData())
        {
            return false;
        }
        return true;
    }

    /**
     * 数据操作类业务处理
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean CheckData()
    {
        LWMissionDB tLWMissionDB = new LWMissionDB();
        LWMissionSet tLWMissionSet = new LWMissionSet();
        String tStr = "Select * from LWMission where ProcessID = '0000000002'"
                      + "and MissionProp2 = '" + mStatYear + "'"
                      + "and MissionProp3 = '" + mStatMonth + "'";
        tLWMissionSet = tLWMissionDB.executeQuery(tStr);
        if (tLWMissionSet == null)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLWMissionSet.mErrors);
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "prepareMission";
            tError.errorMessage = "查询保监会报表工作流任务节点失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (tLWMissionSet.size() > 0)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLWMissionSet.mErrors);
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "prepareMission";
            tError.errorMessage = "已申请" + mStatYear + "年" + mStatMonth +
                                  "月的保监会报表，正在处理状态，无需再次申请!";
            this.mErrors.addOneError(tError);
            return false;
        }

        LBMissionDB tLBMissionDB = new LBMissionDB();
        LBMissionSet tLBMissionSet = new LBMissionSet();
        tStr = "Select * from LBMission where ProcessID = '0000000002'"
               + "and MissionProp2 = '" + mStatYear + "'"
               + "and MissionProp3 = '" + mStatMonth + "'";
        tLBMissionSet = tLBMissionDB.executeQuery(tStr);
        if (tLBMissionSet == null)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLWMissionSet.mErrors);
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "prepareMission";
            tError.errorMessage = "查询保监会报表工作流备份任务节点失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (tLWMissionSet.size() == 0 && tLBMissionSet.size() > 0)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLWMissionSet.mErrors);
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "prepareMission";
            tError.errorMessage = "已申请" + mStatYear + "年" + mStatMonth +
                                  "月的保监会报表，已处理结束状态，不能再次申请!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }


    private boolean getInputData(VData cInputData)
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
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
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
            tError.moduleName = "CircReportApplyBL";
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
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得业务数据
        if (mTransferData == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mStatYear = (String) mTransferData.getValueByName("StatYear");
        if (mStatYear == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中StatYear失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mStatMonth = (String) mTransferData.getValueByName("StatMonth");
        if (mStatMonth == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中StatMonth失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 为自动核保后,需进入人工核保的保全准备转入工作流的数据
     */
    private boolean prepareWorkFlowData()
    {

        // 准备传输工作流数据 VData

        tWorkFlowTransferData = new TransferData();
        tWorkFlowTransferData.setNameAndValue("StatYear", mStatYear);
        tWorkFlowTransferData.setNameAndValue("StatMon", mStatMonth);

        tWorkFlowVData = new VData();
        tWorkFlowVData.add(mGlobalInput);
        tWorkFlowVData.add(tWorkFlowTransferData);
        System.out.println("ok-prepareWorkFlowData");

        ActivityOperator tActivityOperator = new ActivityOperator();
        //执行发放体检通知书虚拟任务(当产生任务与执行任务为同一事务时,采用执行虚拟任务模式工作)
        try
        {
            //产生执行完发放体检通知书任务后的下一打印体检通知书任务节点
            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
            tLWMissionSchema = tActivityOperator.CreateStartMission(
                    "0000000002", tWorkFlowVData);
            if (tLWMissionSchema != null)
            {
                mLWMissionSet.add(tLWMissionSchema);
            }
            else
            {
                return false;
            }
        }
        catch (Exception ex)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( mActivityOperator.mErrors );
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "CreateStartMission";
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
        mInputData = new VData();
        MMap tMMap = new MMap();
        if (mLWMissionSet != null && mLWMissionSet.size() > 0)
        {
            tMMap.put(mLWMissionSet, "INSERT");
            mInputData.add(tMMap);

            return true;

        }

        return false;

    }

}

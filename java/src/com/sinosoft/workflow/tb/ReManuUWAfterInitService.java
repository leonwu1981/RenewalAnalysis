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
import com.sinosoft.workflowengine.AfterInitService;


/**
 * <p>Title: 工作流服务类:新契约核保订正</p>
 * <p>Description: 核保订正</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class ReManuUWAfterInitService implements AfterInitService
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();


    /** 往界面传输数据的容器 */
    private VData mResult = new VData();


    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();

    private TransferData mTransferData = new TransferData();

    private LCCUWMasterSet mLCCUWMasterSet = new LCCUWMasterSet();
    private LCCUWSubSet mLCCUWSubSet = new LCCUWSubSet();
    private LCUWMasterSet mLCUWMasterSet = new LCUWMasterSet();
    private LCUWSubSet mLCUWSubSet = new LCUWSubSet();


    /** 数据操作字符串 */
    private String mOperater;
    private String mManageCom;


    /** 业务数据操作字符串 */
    private String mContNo;
    private String mUWFlag;
    private String mBackUWGrade;
    private String mBackAppGrade;
    private String mOperator;
    private String mUWPopedom; //核保级别
    private String mAppGrade; //申请级别
    private String mMissionID;


    /**保单表*/
    private LCContSchema mLCContSchema = new LCContSchema();
    private LCPolSet mLCPolSet = new LCPolSet();
    public ReManuUWAfterInitService()
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

        //为工作流下一节点属性字段准备数据
        if (!prepareTransferData())
        {
            return false;
        }

        //准备往后台的数据
        if (!prepareOutputData())
        {
            return false;
        }

        System.out.println("Start  Submit...");

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

        map.put(mLCContSchema, "UPDATE");
        map.put(mLCPolSet, "UPDATE");
        map.put(mLCCUWMasterSet, "UPDATE");
        map.put(mLCCUWSubSet, "INSERT");
        map.put(mLCUWMasterSet, "UPDATE");
        map.put(mLCUWSubSet, "INSERT");
        mResult.add(map);
        return true;
    }


    /**
     * 校验业务数据
     * @return boolean
     */
    private boolean checkData()
    {
        //校验核保员级别
        LDUserDB tLDUserDB = new LDUserDB();
        tLDUserDB.setUserCode(mOperater);
        System.out.println("mOperate" + mOperater);
        if (!tLDUserDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "ReManuUWAfterInitService";
            tError.functionName = "checkUWGrade";
            tError.errorMessage = "无此操作员信息，不能核保!（操作员：" + mOperater + "）";
            this.mErrors.addOneError(tError);
            return false;
        }

        LDUWUserDB tLDUWUserDB = new LDUWUserDB();
       tLDUWUserDB.setUserCode(mOperater);
       if (!tLDUWUserDB.getInfo())
       {
           CError tError = new CError();
           tError.moduleName = "UWManuNormChkBL";
           tError.functionName = "checkUWGrade";
           tError.errorMessage = "无此核保师信息，不能核保订正!（操作员：" + mOperater + "）";
           this.mErrors.addOneError(tError);
           return false;
       }
       String tUWPopedom = tLDUWUserDB.getUWPopedom();
       if (tUWPopedom == null || tUWPopedom.trim().equals("")) {
         CError tError = new CError();
         tError.moduleName = "ReManuUWAfterInitService";
         tError.functionName = "CheckDate";
         tError.errorMessage = "该核保师没有定义权限!";
         this.mErrors.addOneError(tError);
         return false;
       }

        //校验保单信息
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mContNo);
        if (!tLCContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "ReManuUWAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mContNo + "信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);

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
            tError.moduleName = "ReManuUWAfterInitService";
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
            tError.moduleName = "ReManuUWAfterInitService";
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
            tError.moduleName = "ReManuUWAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
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
            tError.moduleName = "ReManuUWAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的mCont
        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mContNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReManuUWAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mUWFlag = (String) mTransferData.getValueByName("UWFlag");
        if (mUWFlag == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReManuUWAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中UWFlag失败!";
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

        mUWFlag = "z"; //核保订正标志
        //准备保单的复核标志
        mLCContSchema.setUWFlag(mUWFlag);

//因为在核保时，对于附加险拒保延期的情况LCCont的保费进行了从新计算，故订正回去的单子也要计算一次保费    chenhq
        String tsql = "select sum(prem) from lcpol where contno='"+ mContNo +"'";
        ExeSQL tExeSQL = new ExeSQL();
        String UWprem = tExeSQL.getOneValue(tsql);
        mLCContSchema.setPrem(Double.parseDouble(UWprem));

        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setContNo(mLCContSchema.getContNo());
        mLCPolSet = tLCPolDB.query();
        //准备险种保单的复核标志
        for (int i = 1; i <= mLCPolSet.size(); i++)
        {
            mLCPolSet.get(i).setUWFlag(mUWFlag);
        }

        //准备合同复核表数据
        if (!prepareContUW())
        {
            return false;
        }

        //准备险种复核表数据
        if (!prepareAllUW())
        {
            return false;
        }

        return true;
    }


    /**
     * 准备主附险核保信息
     * 输出：如果发生错误则返回false,否则返回true
     * @return boolean
     */
    private boolean prepareAllUW()
    {
        mLCUWMasterSet.clear();
        mLCUWSubSet.clear();

        for (int i = 1; i <= mLCPolSet.size(); i++)
        {
            LCPolSchema tLCPolSchema = new LCPolSchema();

            tLCPolSchema = mLCPolSet.get(i);

            LCUWMasterSchema tLCUWMasterSchema = new LCUWMasterSchema();
            LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
            tLCUWMasterDB.setPolNo(tLCPolSchema.getPolNo());
            LCUWMasterSet tLCUWMasterSet = new LCUWMasterSet();
            tLCUWMasterSet = tLCUWMasterDB.query();
            if (tLCUWMasterDB.mErrors.needDealError())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tLCUWMasterDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "UWManuNormChkBL";
                tError.functionName = "prepareAllUW";
                tError.errorMessage = "LCUWMaster表取数失败!";
                this.mErrors.addOneError(tError);
                return false;
            }

            int n = tLCUWMasterSet.size();
            System.out.println("该投保单的核保主表当前记录条数:  " + n);
            if (n == 1)
            {
                tLCUWMasterSchema = tLCUWMasterSet.get(1);

                //为核保订正回退保存核保级别和核保人
                mBackUWGrade = tLCUWMasterSchema.getUWGrade();
                mBackAppGrade = tLCUWMasterSchema.getAppGrade();
                mOperator = tLCUWMasterSchema.getOperator();

                //tLCUWMasterSchema.setUWNo(tLCUWMasterSchema.getUWNo()+1);核保主表中的UWNo表示该投保单经过几次人工核保(等价于经过几次自动核保次数),而不是人工核保结论(包括核保通知书,上报等)下过几次.所以将其注释.sxy-2003-09-19
                tLCUWMasterSchema.setPassFlag(mUWFlag); //通过标志
                tLCUWMasterSchema.setState(mUWFlag);
                tLCUWMasterSchema.setAutoUWFlag("2"); // 1 自动核保 2 人工核保
                tLCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
                tLCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());

                //恢复核保级别和核保员
                tLCUWMasterSchema.setUWGrade(mBackUWGrade);
                tLCUWMasterSchema.setAppGrade(mBackAppGrade);
                tLCUWMasterSchema.setOperator(mOperator);
                //解锁
                LDSysTraceSchema tLDSysTraceSchema = new LDSysTraceSchema();
                tLDSysTraceSchema.setPolNo(mContNo);
                tLDSysTraceSchema.setCreatePos("人工核保");
                tLDSysTraceSchema.setPolState("1001");
                LDSysTraceSet inLDSysTraceSet = new LDSysTraceSet();
                inLDSysTraceSet.add(tLDSysTraceSchema);

                VData tVData = new VData();
                tVData.add(mGlobalInput);
                tVData.add(inLDSysTraceSet);

                LockTableBL LockTableBL1 = new LockTableBL();
                if (!LockTableBL1.submitData(tVData, "DELETE"))
                {
                    System.out.println("解锁失败！");
                }

            }
            else
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tLCUWMasterDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "UWManuNormChkBL";
                tError.functionName = "prepareAllUW";
                tError.errorMessage = "LCUWMaster表取数据不唯一!";
                this.mErrors.addOneError(tError);
                return false;
            }

            mLCUWMasterSet.add(tLCUWMasterSchema);

            // 核保轨迹表
            LCUWSubSchema tLCUWSubSchema = new LCUWSubSchema();
            LCUWSubDB tLCUWSubDB = new LCUWSubDB();
            tLCUWSubDB.setContNo(tLCPolSchema.getContNo());
            tLCUWSubDB.setPolNo(tLCPolSchema.getPolNo());
            LCUWSubSet tLCUWSubSet = new LCUWSubSet();
            tLCUWSubSet = tLCUWSubDB.query();
            if (tLCUWSubDB.mErrors.needDealError())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tLCUWSubDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "UWManuNormChkBL";
                tError.functionName = "prepareAllUW";
                tError.errorMessage = "LCUWSub表取数失败!";
                this.mErrors.addOneError(tError);
                return false;
            }

            int m = tLCUWSubSet.size();
            System.out.println("subcount=" + m);
            if (m > 0)
            {
                m++; //核保次数
                tLCUWSubSchema = new LCUWSubSchema();
                tLCUWSubSchema.setUWNo(m); //第几次核保
                tLCUWSubSchema.setProposalNo(tLCUWMasterSchema.getProposalNo());
                tLCUWSubSchema.setContNo(tLCUWMasterSchema.getContNo());
                tLCUWSubSchema.setGrpContNo(tLCUWMasterSchema.getGrpContNo());
                tLCUWSubSchema.setProposalContNo(tLCUWMasterSchema.
                        getProposalContNo());
                tLCUWSubSchema.setPolNo(tLCUWMasterSchema.getPolNo());
                tLCUWSubSchema.setOperator(tLCUWMasterSchema.getOperator());
                tLCUWSubSchema.setProposalNo(tLCUWMasterSchema.getProposalNo());

                tLCUWSubSchema.setPassFlag(mUWFlag); //核保意见
                tLCUWSubSchema.setUWGrade(mUWPopedom); //核保级别
                tLCUWSubSchema.setAppGrade(mAppGrade); //申请级别
                tLCUWSubSchema.setAutoUWFlag("2");
                tLCUWSubSchema.setState(mUWFlag);
                tLCUWSubSchema.setOperator(mOperater); //操作员

                tLCUWSubSchema.setManageCom(tLCUWMasterSchema.getManageCom());
                tLCUWSubSchema.setMakeDate(PubFun.getCurrentDate());
                tLCUWSubSchema.setMakeTime(PubFun.getCurrentTime());
                tLCUWSubSchema.setModifyDate(PubFun.getCurrentDate());
                tLCUWSubSchema.setModifyTime(PubFun.getCurrentTime());

            }
            else
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tLCUWSubDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "UWManuNormChkBL";
                tError.functionName = "prepareAllUW";
                tError.errorMessage = "LCUWSub表取数失败!";
                this.mErrors.addOneError(tError);
                return false;
            }
            mLCUWSubSet.add(tLCUWSubSchema);
        }
        return true;
    }


    /**
     * 准备主附险核保信息
     * @return boolean
     */
    private boolean prepareContUW()
    {
        mLCCUWMasterSet.clear();
        mLCCUWSubSet.clear();

        LCCUWMasterSchema tLCCUWMasterSchema = new LCCUWMasterSchema();
        LCCUWMasterDB tLCCUWMasterDB = new LCCUWMasterDB();
        tLCCUWMasterDB.setContNo(mLCContSchema.getContNo());
        LCCUWMasterSet tLCCUWMasterSet = new LCCUWMasterSet();
        tLCCUWMasterSet = tLCCUWMasterDB.query();
        if (tLCCUWMasterDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCCUWMasterDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWManuNormChkBL";
            tError.functionName = "prepareAllUW";
            tError.errorMessage = "LCCUWMaster表取数失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        int n = tLCCUWMasterSet.size();
        System.out.println("该投保单的核保主表当前记录条数:  " + n);
        if (n == 1)
        {
            tLCCUWMasterSchema = tLCCUWMasterSet.get(1);

            //为核保订正回退保存核保级别和核保人
            mBackUWGrade = tLCCUWMasterSchema.getUWGrade();
            mBackAppGrade = tLCCUWMasterSchema.getAppGrade();
            mOperator = tLCCUWMasterSchema.getOperator();

            //tLCCUWMasterSchema.setUWNo(tLCCUWMasterSchema.getUWNo()+1);核保主表中的UWNo表示该投保单经过几次人工核保(等价于经过几次自动核保次数),而不是人工核保结论(包括核保通知书,上报等)下过几次.所以将其注释.sxy-2003-09-19
            tLCCUWMasterSchema.setPassFlag(mUWFlag); //通过标志
            tLCCUWMasterSchema.setState(mUWFlag);
            tLCCUWMasterSchema.setAutoUWFlag("2"); // 1 自动核保 2 人工核保
            tLCCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
            tLCCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());

            //恢复核保级别和核保员
            tLCCUWMasterSchema.setUWGrade(mBackUWGrade);
            tLCCUWMasterSchema.setAppGrade(mBackAppGrade);
            tLCCUWMasterSchema.setOperator(mOperator);
            //解锁
            LDSysTraceSchema tLDSysTraceSchema = new LDSysTraceSchema();
            tLDSysTraceSchema.setPolNo(mContNo);
            tLDSysTraceSchema.setCreatePos("人工核保");
            tLDSysTraceSchema.setPolState("1001");
            LDSysTraceSet inLDSysTraceSet = new LDSysTraceSet();
            inLDSysTraceSet.add(tLDSysTraceSchema);

            VData tVData = new VData();
            tVData.add(mGlobalInput);
            tVData.add(inLDSysTraceSet);

            LockTableBL LockTableBL1 = new LockTableBL();
            if (!LockTableBL1.submitData(tVData, "DELETE"))
            {
                System.out.println("解锁失败！");
            }

        }
        else
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCCUWMasterDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWManuNormChkBL";
            tError.functionName = "prepareAllUW";
            tError.errorMessage = "LCCUWMaster表取数据不唯一!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCCUWMasterSet.add(tLCCUWMasterSchema);

        // 核保轨迹表
        LCCUWSubSchema tLCCUWSubSchema = new LCCUWSubSchema();
        LCCUWSubDB tLCCUWSubDB = new LCCUWSubDB();
        tLCCUWSubDB.setContNo(mLCContSchema.getContNo());
        LCCUWSubSet tLCCUWSubSet = new LCCUWSubSet();
        tLCCUWSubSet = tLCCUWSubDB.query();
        if (tLCCUWSubDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCCUWSubDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWManuNormChkBL";
            tError.functionName = "prepareAllUW";
            tError.errorMessage = "LCCUWSub表取数失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        int m = tLCCUWSubSet.size();
        System.out.println("subcount=" + m);
        if (m > 0)
        {
            m++; //核保次数
            tLCCUWSubSchema = new LCCUWSubSchema();
            tLCCUWSubSchema.setUWNo(m); //第几次核保
            tLCCUWSubSchema.setContNo(tLCCUWMasterSchema.getContNo());
            tLCCUWSubSchema.setGrpContNo(tLCCUWMasterSchema.getGrpContNo());
            tLCCUWSubSchema.setProposalContNo(tLCCUWMasterSchema.getProposalContNo());
            tLCCUWSubSchema.setOperator(mOperater);

            tLCCUWSubSchema.setPassFlag(mUWFlag); //核保意见
            tLCCUWSubSchema.setUWGrade(mUWPopedom); //核保级别
            tLCCUWSubSchema.setAppGrade(mAppGrade); //申请级别
            tLCCUWSubSchema.setAutoUWFlag("2");
            tLCCUWSubSchema.setState(mUWFlag);
            tLCCUWSubSchema.setOperator(mOperater); //操作员

            tLCCUWSubSchema.setManageCom(tLCCUWMasterSchema.getManageCom());
            tLCCUWSubSchema.setMakeDate(PubFun.getCurrentDate());
            tLCCUWSubSchema.setMakeTime(PubFun.getCurrentTime());
            tLCCUWSubSchema.setModifyDate(PubFun.getCurrentDate());
            tLCCUWSubSchema.setModifyTime(PubFun.getCurrentTime());
        }
        else
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCCUWSubDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWManuNormChkBL";
            tError.functionName = "prepareAllUW";
            tError.errorMessage = "LCCUWSub表取数失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCCUWSubSet.add(tLCCUWSubSchema);

        return true;
    }


    /**
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
     * @return boolean
     */
    private boolean prepareTransferData()
    {
        LAAgentDB tLAAgentDB = new LAAgentDB();
        LAAgentSet tLAAgentSet = new LAAgentSet();
        tLAAgentDB.setAgentCode(mLCContSchema.getAgentCode());
        tLAAgentSet = tLAAgentDB.query();
        if (tLAAgentSet == null || tLAAgentSet.size() != 1)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "ReManuUWAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人表LAAgent查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLAAgentSet.get(1).getAgentGroup() == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "ReManuUWAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人表LAAgent中的代理机构数据丢失!";
            this.mErrors.addOneError(tError);
            return false;
        }

        LABranchGroupDB tLABranchGroupDB = new LABranchGroupDB();
        LABranchGroupSet tLABranchGroupSet = new LABranchGroupSet();
        tLABranchGroupDB.setAgentGroup(tLAAgentSet.get(1).getAgentGroup());
        tLABranchGroupSet = tLABranchGroupDB.query();
        if (tLABranchGroupSet == null || tLABranchGroupSet.size() != 1)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "ReManuUWAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人展业机构表LABranchGroup查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLABranchGroupSet.get(1).getBranchAttr() == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "ReManuUWAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人展业机构表LABranchGroup中展业机构信息丢失!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mTransferData.setNameAndValue("AgentCode", mLCContSchema.getAgentCode());
//        System.out.println("agentcode==" + mLCContSchema.getAgentCode());
//        mTransferData.setNameAndValue("ManageCom", mManageCom);
        mTransferData.setNameAndValue("ManageCom", mLCContSchema.getManageCom());
//        System.out.println("manageCom=" + mManageCom);
        mTransferData.setNameAndValue("PrtNo", mLCContSchema.getPrtNo());
//        System.out.println("prtNo==" + mLCContSchema.getPrtNo());
        mTransferData.setNameAndValue("ContNo", mLCContSchema.getContNo());
//        System.out.println("ContNo==" + mLCContSchema.getContNo());
        mTransferData.setNameAndValue("AgentGroup", tLAAgentSet.get(1).getAgentGroup());
        mTransferData.setNameAndValue("AgentName", tLAAgentSet.get(1).getName());
        mTransferData.setNameAndValue("AppntCode", mLCContSchema.getAppntNo());
//        System.out.println("AppntName = " + mLCContSchema.getAppntName());
        mTransferData.setNameAndValue("AppntName", mLCContSchema.getAppntName());
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

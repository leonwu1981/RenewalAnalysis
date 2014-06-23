/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import java.util.Date;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;


/**
 * <p>Title: 工作流节点任务:新契约发核保通知书</p>
 * <p>Description: 发核保通知书工作流AfterInit服务类</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class UWSendNoticeAfterInitService implements AfterInitService
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();


    /** 往界面传输数据的容器 */
    private VData mResult = new VData();


    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();


    /** 核保主表 */
    private LCCUWMasterSchema mLCCUWMasterSchema = new LCCUWMasterSchema();
    private LCUWMasterSet mLCUWMasterSet = new LCUWMasterSet();


    /** 核保子表 */
    private LCUWSubSet mLCUWSubSet = new LCUWSubSet();
    private LCCUWSubSet mLCCUWSubSet = new LCCUWSubSet();


    /** 打印管理表 */
    private LOPRTManagerSet mLOPRTManagerSet = new LOPRTManagerSet();
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();
    private LCIssuePolSet mLCIssuePolSet = new LCIssuePolSet();


    /** 数据操作字符串 */
    private String mOperater;
    private String mManageCom;


    /** 业务数据操作字符串 */
    private String mContNo;
    private String mMissionID;
    private String mPrtSeq;
    private String mPrtSeqUW = "0";
    private String mPrtSeqOper = "0";


    /**保单表*/
    private LCContSchema mLCContSchema = new LCContSchema();
    private LCPolSet mLCPolSet = new LCPolSet();
    private String mUWFlag = ""; //核保标志


    /**工作流扭转标志*/
    private String mUWSendFlag = "";
    private String mSendOperFlag = "";
    private String mQuesOrgFlag = "";
    private String mApproveModifyFlag = "";

    public UWSendNoticeAfterInitService()
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

        //对是否能发核保通知书进行校验
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

        if (!prepareTransferData())
        {
            return false;
        }
        return true;
    }


    /**
     * checkData
     *
     * @return boolean
     */
    private boolean checkData()
    {
        int flag = 1;  //　暂时不对发核保函件作任何检验，因为现在各种情况都要可以发送核保函
        //没有未回复的问题件则不能发核保通知书
//        String strSql = "select count(1) from lcissuepol where 1=1"
//                        + " and Contno = '" + mContNo
//                        +
//                        "' and backobjtype = '3' and replyresult is null and needprint = 'Y'"
//                        ;
//
//         ExeSQL tExeSQL = new ExeSQL();
//         int rs = Integer.parseInt(tExeSQL.getOneValue(strSql));
//         if (rs > 0)
//         {
//              flag = 1;
//         }  //change by tuqiang 问题件模块已经单独提出来解决
//
//        LCCUWMasterDB tLCCUWMasterDB = new LCCUWMasterDB();
//        LCCUWMasterSchema tLCCUWMasterSchema = new LCCUWMasterSchema();
//        tLCCUWMasterDB.setContNo(mContNo);
//        if (!tLCCUWMasterDB.getInfo())
//        {
//            // @@错误处理
//            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
//            CError tError = new CError();
//            tError.moduleName = "UWSendNoticeAfterEndService";
//            tError.functionName = "checkData";
//            tError.errorMessage = "查询核保主表失败!";
//            this.mErrors.addOneError(tError);
//            return false;
//
//        }
//        tLCCUWMasterSchema = tLCCUWMasterDB.getSchema();
//        //没有承保计划变更，不能发核保通知书
//        if (tLCCUWMasterSchema.getChangePolFlag() != null &&
//                tLCCUWMasterSchema.getChangePolFlag().length() > 0 &&
//                tLCCUWMasterSchema.getChangePolFlag().equals("1"))
//        {
//            flag = 1;
//        }

//        //没有特约，不能发核保通知书
//        LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
//        tLCUWMasterDB.setContNo(mContNo);
//        LCUWMasterSet tLCUWMasterSet = tLCUWMasterDB.query();
//        if (tLCUWMasterSet == null || tLCUWMasterSet.size() <= 0)
//        {
//            // @@错误处理
//            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
//            CError tError = new CError();
//            tError.moduleName = "UWSendNoticeAfterEndService";
//            tError.functionName = "checkData";
//            tError.errorMessage = "查询险种单核保主表失败!";
//            this.mErrors.addOneError(tError);
//            return false;
//        }
//        for (int i = 1; i <= tLCUWMasterSet.size(); i++)
//        {
//            if (tLCUWMasterSet.get(i).getSpecFlag() != null &&
//                    tLCUWMasterSet.get(i).getSpecFlag().length() > 0 &&
//                    tLCUWMasterSet.get(i).getSpecFlag().equals("1"))
//            {
//                flag = 1;
//            }
//        }

//        //没有加费，不能发核保通知书
//        if (tLCCUWMasterSchema.getAddPremFlag() != null &&
//                tLCCUWMasterSchema.getAddPremFlag().length() > 0 &&
//                tLCCUWMasterSchema.getAddPremFlag().equals("1"))
//        {
//            flag = 1;
//        }

//        if (flag == 0)
//        {
//            // @@错误处理
//            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
//            CError tError = new CError();
//            tError.moduleName = "UWSendNoticeAfterEndService";
//            tError.functionName = "checkData";
//            tError.errorMessage = "没有录入问题件、没有承保计划变更、没有特约、没有加费，不能发核保通知书!";
//            this.mErrors.addOneError(tError);
//            return false;
//
//        }
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

        //获得业务核保通知数据

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
        //准备险种信息
        if (!prepareCont())
        {
            return false;
        }
        //准备合同核保表信息
        if (!prepareContUW())
        {
            return false;
        }
        //准备险种核保表信息
        if (!preparePolUW())
        {
            return false;
        }
        //选择工作流流转活动
        if (!chooseActivity())
        {
            return false;
        }
        if (mUWSendFlag.equals("1") || mSendOperFlag.equals("1"))
        {
            //准备打印管理表信息
            if (!preparePrt())
            {
                return false;
            }
            if (!prepareIssue())
            {
                return false;
            }
        }
        return true;
    }


    /**
     * prepareIssue
     *  准备lcissuepol表数据，生成prtseq
     * @return boolean
     */
    private boolean prepareIssue()
    {
        LCIssuePolSet tLCIssuePolSet = new LCIssuePolSet();
        LCIssuePolDB tLCIssuePolDB = new LCIssuePolDB();
        LCIssuePolSchema tLCIssuePolSchema = new LCIssuePolSchema();
        tLCIssuePolDB.setContNo(mContNo);
        tLCIssuePolSet = tLCIssuePolDB.query();

        for (int i = 1; i <= tLCIssuePolSet.size(); i++)
        {
            tLCIssuePolSchema = new LCIssuePolSchema();
            tLCIssuePolSchema = tLCIssuePolSet.get(i);
            if (tLCIssuePolSchema.getPrtSeq() == null || tLCIssuePolSchema.getPrtSeq().equals(""))
            {
                if (tLCIssuePolSchema.getBackObjType().equals("2"))
                {
                    tLCIssuePolSchema.setPrtSeq(mPrtSeqOper);
                }
                else
                {
                    tLCIssuePolSchema.setPrtSeq(mPrtSeqUW);
                }
            }
            mLCIssuePolSet.add(tLCIssuePolSchema);
        }

        return true;
    }


    /**
     * preparePrt
     *  准备合同表数据,并置UWFlag=8,表示生成核保通知书
     * @return boolean
     */
    private boolean prepareCont()
    {
        //准备合同表数据
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mContNo);
        if (!tLCContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "合同" + mContNo + "信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);

//        mUWFlag = "8"; //核保订正标志
        //准备保单的复核标志
//        mLCContSchema.setUWFlag(mUWFlag);
        //准备险种合同表数据
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setContNo(mLCContSchema.getContNo());
        mLCPolSet = tLCPolDB.query();
        mUWFlag = mLCPolSet.get(1).getUWFlag();

        //准备险种保单的复核标志
//        for (int i = 1; i < mLCPolSet.size(); i++)
//        {
//            mLCPolSet.get(i).setUWFlag(mUWFlag);
//        }
        return true;
    }


    /**
     * preparePrt
     *  准备核保主表数据
     * @return boolean
     */
    private boolean prepareContUW()
    {
        mLCCUWSubSet.clear();

        LCCUWMasterDB tLCCUWMasterDB = new LCCUWMasterDB();
        tLCCUWMasterDB.setContNo(mContNo);
        if (!tLCCUWMasterDB.getInfo())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCCUWMasterDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWSendNoticeAfterInitService";
            tError.functionName = "prepareContUW";
            tError.errorMessage = "LCCUWMaster表取数失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCCUWMasterSchema.setSchema(tLCCUWMasterDB);
        mLCCUWMasterSchema.setPassFlag(mUWFlag);

        //每次进行核保相关操作时，向核保轨迹表插一条数据
        LCCUWSubSchema tLCCUWSubSchema = new LCCUWSubSchema();
        LCCUWSubDB tLCCUWSubDB = new LCCUWSubDB();
        tLCCUWSubDB.setContNo(mContNo);
        LCCUWSubSet tLCCUWSubSet = new LCCUWSubSet();
        tLCCUWSubSet = tLCCUWSubDB.query();

        if (tLCCUWSubDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCCUWSubDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWSendNoticeAfterInitService";
            tError.functionName = "prepareContUW";
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
            tLCCUWSubSchema.setContNo(mContNo);
            tLCCUWSubSchema.setGrpContNo(mLCCUWMasterSchema.getGrpContNo());
            tLCCUWSubSchema.setProposalContNo(mLCCUWMasterSchema.
                    getProposalContNo());
            tLCCUWSubSchema.setPassFlag(mUWFlag); //核保意见
            tLCCUWSubSchema.setPrintFlag("1");
            tLCCUWSubSchema.setAutoUWFlag(mLCCUWMasterSchema.getAutoUWFlag());
            tLCCUWSubSchema.setState(mLCCUWMasterSchema.getState());
            tLCCUWSubSchema.setOperator(mLCCUWMasterSchema.getOperator()); //操作员
            tLCCUWSubSchema.setManageCom(mLCCUWMasterSchema.getManageCom());
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
            tError.moduleName = "UWSendNoticeAfterInitService";
            tError.functionName = "prepareAllUW";
            tError.errorMessage = "LCCUWSub表取数失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCCUWSubSet.add(tLCCUWSubSchema);
        return true;
    }


    /**
     * 准备险种核保表信息
     * 输出：如果发生错误则返回false,否则返回true
     * @return boolean
     */
    private boolean preparePolUW()
    {
        mLCUWMasterSet.clear();
        mLCUWSubSet.clear();
        LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
        LCPolSchema tLCPolSchema = new LCPolSchema();
//        LCUWMasterSet tLCUWMasterSet = new LCUWMasterSet();
        LCUWMasterSchema tLCUWMasterSchema = new LCUWMasterSchema();
        LCUWSubSchema tLCUWSubSchema = new LCUWSubSchema();
        LCUWSubDB tLCUWSubDB = new LCUWSubDB();
        LCUWSubSet tLCUWSubSet = new LCUWSubSet();

        for (int i = 1; i <= mLCPolSet.size(); i++)
        {
            tLCUWMasterSchema = new LCUWMasterSchema();
            tLCUWMasterDB = new LCUWMasterDB();
            tLCPolSchema = mLCPolSet.get(i);
            tLCUWMasterDB.setProposalNo(tLCPolSchema.getProposalNo());

            if (!tLCUWMasterDB.getInfo())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tLCUWMasterDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "UWSendNoticeAfterInitService";
                tError.functionName = "prepareAllUW";
                tError.errorMessage = "LCUWMaster表取数失败!";
                this.mErrors.addOneError(tError);
                return false;
            }
            tLCUWMasterSchema.setSchema(tLCUWMasterDB);

            //tLCUWMasterSchema.setUWNo(tLCUWMasterSchema.getUWNo()+1);核保主表中的UWNo表示该投保单经过几次人工核保(等价于经过几次自动核保次数),而不是人工核保结论(包括核保通知书,上报等)下过几次.所以将其注释.sxy-2003-09-19
            tLCUWMasterSchema.setPassFlag(tLCPolSchema.getUWFlag()); //通过标志
            tLCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
            tLCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());

            mLCUWMasterSet.add(tLCUWMasterSchema);

            // 核保轨迹表
            tLCUWSubSchema = new LCUWSubSchema();
            tLCUWSubDB = new LCUWSubDB();
            tLCUWSubSet = new LCUWSubSet();
            tLCUWSubDB.setContNo(tLCPolSchema.getContNo());
            tLCUWSubDB.setPolNo(tLCPolSchema.getPolNo());
            tLCUWSubSet = tLCUWSubDB.query();
            if (tLCUWSubDB.mErrors.needDealError())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tLCUWSubDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "UWSendNoticeAfterInitService";
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
                tLCUWSubSchema.setGrpContNo(tLCUWMasterSchema.getGrpContNo());
                tLCUWSubSchema.setProposalNo(tLCUWMasterSchema.getProposalNo());
                tLCUWSubSchema.setContNo(tLCUWMasterSchema.getContNo());
                tLCUWSubSchema.setProposalContNo(tLCUWMasterSchema.
                        getProposalContNo());
                tLCUWSubSchema.setPolNo(tLCUWMasterSchema.getPolNo());
                tLCUWSubSchema.setPassFlag(mUWFlag); //核保意见
                tLCUWSubSchema.setUWGrade(tLCUWMasterSchema.getUWGrade()); //核保级别
                tLCUWSubSchema.setAppGrade(tLCUWMasterSchema.getAppGrade()); //申请级别
                tLCUWSubSchema.setAutoUWFlag(tLCUWMasterSchema.getAutoUWFlag());
                tLCUWSubSchema.setPrintFlag("1");
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
                tError.moduleName = "UWSendNoticeAfterInitService";
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
     * preparePrt
     *  准备打印表
     * @return boolean
     */
    private boolean chooseActivity()
    {
//        String tsql = "";
        LCIssuePolDB tLCIssuePolDB = new LCIssuePolDB();
        LCIssuePolSet tLCIssuePolSet = new LCIssuePolSet();
///////////////////////////////  长城:所有的都要求可以发送核保通知书  ////////////////////////////////////////////////////////
        mUWSendFlag = "1"; //打印核保通知书标志
        mSendOperFlag = "0"; //打印业务员通知书标志
        mQuesOrgFlag = "0"; //机构问题件标志
        mApproveModifyFlag = "0"; //复核修改标志
//        for (int i = 1; i <= mLCUWMasterSet.size(); i++)
//        {
//            if (mLCUWMasterSet.get(i).getAddPremFlag() != null &&
//                    mLCUWMasterSet.get(i).getAddPremFlag().equals("1"))
//            {
//                mUWSendFlag = "1";
//            }
//            if (mLCUWMasterSet.get(i).getSpecFlag() != null &&
//                    mLCUWMasterSet.get(i).getSpecFlag().equals("1"))
//            {
//                mUWSendFlag = "1";
//            }
//        }
//        if (mLCCUWMasterSchema.getChangePolFlag() != null &&
//                mLCCUWMasterSchema.getChangePolFlag().equals("1"))
//        {
//            mUWSendFlag = "1";
//        }

        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from lcissuepol where Contno = '");
        tSBql.append(mContNo);
        tSBql.append("' and backobjtype = '3' and replyresult is null and needprint = 'Y'");
        tLCIssuePolSet = tLCIssuePolDB.executeQuery(tSBql.toString());
        if (tLCIssuePolSet.size() > 0)
        {
            mUWSendFlag = "1";
        }
        tLCIssuePolSet.clear();
        tSBql = new StringBuffer(128);
        tSBql.append("select * from lcissuepol where Contno = '");
        tSBql.append(mContNo);
        tSBql.append("' and backobjtype = '2' and prtseq is null");
        tLCIssuePolSet = tLCIssuePolDB.executeQuery(tSBql.toString());
        if (tLCIssuePolSet.size() > 0)
        {
            mSendOperFlag = "1";
        }
        tLCIssuePolSet.clear();
        tSBql = new StringBuffer(128);
        tSBql.append("select * from lcissuepol where Contno = '");
        tSBql.append(mContNo);
        tSBql.append("' and backobjtype = '4' and replyresult is null");
        tLCIssuePolSet = tLCIssuePolDB.executeQuery(tSBql.toString());
        if (tLCIssuePolSet.size() > 0)
        {
            mQuesOrgFlag = "1";
        }
        tLCIssuePolSet.clear();
        tSBql = new StringBuffer(128);
        tSBql.append("select * from lcissuepol where Contno = '");
        tSBql.append(mContNo);
        tSBql.append("' and backobjtype = '1'");
        tLCIssuePolSet = tLCIssuePolDB.executeQuery(tSBql.toString());
        if (tLCIssuePolSet.size() > 0 && mSendOperFlag.equals("0") &&
                mUWSendFlag.equals("0") && mQuesOrgFlag.equals("0"))
        {
            mApproveModifyFlag = "1";
        }

        return true;
    }


    /**
     * preparePrt
     *  准备打印表
     * @return boolean
     */
    private boolean preparePrt()
    {
        // 处于未打印状态的通知书在打印队列中只能有一个
        // 条件：同一个单据类型，同一个其它号码，同一个其它号码类型
        //生成给付通知书号
        LDSysVarDB tLDSysVarDB = new LDSysVarDB();
        tLDSysVarDB.setSysVar("URGEInterval");

        if (!tLDSysVarDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "UWSendPrintBL";
            tError.functionName = "prepareURGE";
            tError.errorMessage = "没有描述催发间隔!";
            this.mErrors.addOneError(tError);
            return false;
        }
        FDate tFDate = new FDate();
        int tInterval = Integer.parseInt(tLDSysVarDB.getSysVarValue());
        Date tDate = PubFun.calDate(tFDate.getDate(PubFun.getCurrentDate()),
                tInterval, "D", null);
        if (mUWSendFlag.equals("1"))
        {
            String tLimit = PubFun.getNoLimit(mManageCom);
            VData tVData = new VData();
            mPrtSeqUW = PubFun1.CreateMaxNo("HEBAOHAN", "SN",tVData);
            System.out.println("---tLimit---" + tLimit);

            //准备打印管理表数据
            mLOPRTManagerSchema.setPrtSeq(mPrtSeqUW);
            mLOPRTManagerSchema.setOtherNo(mContNo);
            mLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_CONT); //保单号
            mLOPRTManagerSchema.setCode(PrintManagerBL.CODE_UW); //核保
            mLOPRTManagerSchema.setManageCom(mLCContSchema.getManageCom());
            mLOPRTManagerSchema.setAgentCode(mLCContSchema.getAgentCode());
            mLOPRTManagerSchema.setReqCom(mGlobalInput.ComCode);
            mLOPRTManagerSchema.setReqOperator(mGlobalInput.Operator);
            //mLOPRTManagerSchema.setExeCom();
            //mLOPRTManagerSchema.setExeOperator();
            mLOPRTManagerSchema.setPrtType(PrintManagerBL.PT_FRONT); //前台打印
            mLOPRTManagerSchema.setStateFlag("0");
            mLOPRTManagerSchema.setPatchFlag("0");
            mLOPRTManagerSchema.setMakeDate(PubFun.getCurrentDate());
            mLOPRTManagerSchema.setMakeTime(PubFun.getCurrentTime());
            //mLOPRTManagerSchema.setDoneDate() ;
            //mLOPRTManagerSchema.setDoneTime();
            mLOPRTManagerSchema.setStandbyFlag1(mLCContSchema.getAppntNo()); //投保人编码
            mLOPRTManagerSchema.setStandbyFlag3(mMissionID);
            mLOPRTManagerSchema.setOldPrtSeq(mPrtSeq);
            mLOPRTManagerSchema.setForMakeDate(tDate);

            mLOPRTManagerSet.add(mLOPRTManagerSchema);
        }
        if (mSendOperFlag.equals("1"))
        {
            String tLimit = PubFun.getNoLimit(mManageCom);
            mPrtSeqOper = PubFun1.CreateMaxNo("PRTSEQNO", tLimit);
            //准备打印管理表数据
            mLOPRTManagerSchema.setPrtSeq(mPrtSeqOper);
            mLOPRTManagerSchema.setOtherNo(mContNo);
            mLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_CONT); //保单号
            mLOPRTManagerSchema.setCode(PrintManagerBL.CODE_AGEN_QUEST); //业务员通知书
            mLOPRTManagerSchema.setManageCom(mLCContSchema.getManageCom());
            mLOPRTManagerSchema.setAgentCode(mLCContSchema.getAgentCode());
            mLOPRTManagerSchema.setReqCom(mGlobalInput.ComCode);
            mLOPRTManagerSchema.setReqOperator(mGlobalInput.Operator);
            //mLOPRTManagerSchema.setExeCom();
            //mLOPRTManagerSchema.setExeOperator();
            mLOPRTManagerSchema.setPrtType(PrintManagerBL.PT_FRONT); //前台打印
            mLOPRTManagerSchema.setStateFlag("0");
            mLOPRTManagerSchema.setPatchFlag("0");
            mLOPRTManagerSchema.setMakeDate(PubFun.getCurrentDate());
            mLOPRTManagerSchema.setMakeTime(PubFun.getCurrentTime());
            //mLOPRTManagerSchema.setDoneDate() ;
            //mLOPRTManagerSchema.setDoneTime();
            mLOPRTManagerSchema.setStandbyFlag1(mLCContSchema.getAppntNo()); //投保人编码
            mLOPRTManagerSchema.setStandbyFlag3(mMissionID);
            mLOPRTManagerSchema.setOldPrtSeq(mPrtSeq);

            mLOPRTManagerSet.add(mLOPRTManagerSchema);
        }

        return true;
    }


    /**
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
     * @return boolean
     */
    private boolean prepareTransferData()
    {
        mTransferData.setNameAndValue("SendOperFlag", mSendOperFlag);
        mTransferData.setNameAndValue("UWSendFlag", mUWSendFlag); //打印核保通知书标志
        mTransferData.setNameAndValue("QuesOrgFlag", mQuesOrgFlag);
        mTransferData.setNameAndValue("ApproveModifyFlag", mApproveModifyFlag);

        LAAgentDB tLAAgentDB = new LAAgentDB();
        LAAgentSet tLAAgentSet = new LAAgentSet();
        tLAAgentDB.setAgentCode(mLCContSchema.getAgentCode());
        tLAAgentSet = tLAAgentDB.query();
        if (tLAAgentSet == null || tLAAgentSet.size() != 1)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人表LAAgent查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLAAgentSet.get(1).getAgentGroup() == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
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
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人展业机构表LABranchGroup查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLABranchGroupSet.get(1).getBranchAttr() == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人展业机构表LABranchGroup中展业机构信息丢失!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mTransferData.setNameAndValue("AgentCode", mLCContSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup",
                tLAAgentSet.get(1).getAgentGroup());
        mTransferData.setNameAndValue("BranchAttr",
                tLABranchGroupSet.get(1).getBranchAttr());
        mTransferData.setNameAndValue("ManageCom", mLCContSchema.getManageCom());
        mTransferData.setNameAndValue("PrtNo", mLCContSchema.getPrtNo());
        mTransferData.setNameAndValue("PrtSeqUW", mPrtSeqUW);
        mTransferData.setNameAndValue("PrtSeqOper", mPrtSeqOper);
        mTransferData.setNameAndValue("AppntName", mLCContSchema.getAppntName());

        return true;
    }


    /**
     * 向工作流引擎提交数据
     * @return boolean
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();

        map.put(mLCContSchema, "UPDATE");
        map.put(mLCPolSet, "UPDATE");
        map.put(mLCCUWMasterSchema, "UPDATE");
        map.put(mLCIssuePolSet, "UPDATE");
        map.put(mLCCUWSubSet, "INSERT");
        map.put(mLCUWMasterSet, "UPDATE");
        map.put(mLCUWSubSet, "INSERT");
        map.put(mLOPRTManagerSet, "INSERT");

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

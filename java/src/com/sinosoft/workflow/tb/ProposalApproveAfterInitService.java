/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LCContDB;
import com.sinosoft.lis.db.LCIssuePolDB;
import com.sinosoft.lis.db.LCPolDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.LCContSchema;
import com.sinosoft.lis.tb.CustomerdRelaInfoUI;
import com.sinosoft.lis.tb.GetLCFamilyRelaInfo;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title:工作流服务类:新契约新单复核 </p>
 * <p>Description: 新单复核工作流AfterInit服务类 </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft </p>
 * @author HYQ
 * @version 1.0
 */

public class ProposalApproveAfterInitService implements AfterInitService
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
    private String mOperate;

    /** 业务数据操作字符串 */
    private String mContNo;
    private String mApproveFlag;
    private String mApproveDate;
    private String mApproveTime;
    private String mMissionID;
    private boolean checkfamilyr = true;

    /**保单表*/
    private LCContSchema mLCContSchema = new LCContSchema();
    public LCFamilyRelaInfoSet tLCFamilyRelaInfoSet = new LCFamilyRelaInfoSet();
    public LCFamilyInfoSet tLCFamilyInfoSet = new LCFamilyInfoSet();
    private LCPolSet mLCPolSet = new LCPolSet();
    public ProposalApproveAfterInitService()
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
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();
        if (checkfamilyr)
        {
            VData frdata = new VData();
            TransferData frTransferData = new TransferData();
            frTransferData.setNameAndValue("ContNo", mContNo);
            frdata.add(frTransferData);
            GetLCFamilyRelaInfo frGetLCFamilyRelaInfo = new GetLCFamilyRelaInfo();
            if (!frGetLCFamilyRelaInfo.GetFamilyRelaInfo(frdata, "INSERT"))
            {
                CError tError = new CError();
                tError.moduleName = "UWRReportAfterInitService";
                tError.functionName = "checkData";
                tError.errorMessage = "保单" + mContNo + "生成家庭关系时失败";
                this.mErrors.addOneError(tError);
                return false;
            }
            VData fresultdata = new VData();
            fresultdata = frGetLCFamilyRelaInfo.getResult();
            System.out.println("asdasdasd");
            tLCFamilyRelaInfoSet = (LCFamilyRelaInfoSet) fresultdata.
                    getObjectByObjectName("LCFamilyRelaInfoSet", 0);
            tLCFamilyInfoSet = (LCFamilyInfoSet) fresultdata.getObjectByObjectName(
                    "LCFamilyInfoSet", 0);
            if (tLCFamilyRelaInfoSet.size() > 0)
            {
                map.put(tLCFamilyRelaInfoSet, "INSERT");
            }
            if (tLCFamilyInfoSet.size() > 0)
            {
                map.put(tLCFamilyInfoSet, "INSERT");
            }
        }
        map.put(mLCContSchema, "UPDATE");
        map.put(mLCPolSet, "UPDATE");
        mResult.add(map);
        return true;
    }

    /**
     * 校验业务数据
     * @return
     */
    private boolean checkData()
    {
        //校验保单信息
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mContNo);
        if (!tLCContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mContNo + "信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);
        //个人单无需处理家庭关系
        if (mLCContSchema.getFamilyType().equals("0"))
        {
            checkfamilyr = false;
        }
        else
        {
            checkfamilyr = true;
        }

        //校验合同单下是否有险种单
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setContNo(mContNo);
        mLCPolSet = tLCPolDB.query();
        if (mLCPolSet == null || mLCPolSet.size() <= 0)
        {
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "险种保单" + mContNo + "信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

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
            tError.moduleName = "ProposalApproveAfterInitService";
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
            tError.moduleName = "ProposalApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据Operate失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mOperate = cOperate;
        //获得当前工作任务的任务ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ProposalApproveAfterInitService";
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
            tError.moduleName = "ProposalApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (mLCContSchema.getAppFlag() != null &&
                !mLCContSchema.getAppFlag().trim().equals("0"))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "ProposalApproveBL";
            tError.functionName = "checkData";
            tError.errorMessage = "此单不是投保单，不能进行复核操作!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (mLCContSchema.getUWFlag() != null &&
                !mLCContSchema.getUWFlag().trim().equals("0"))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "ProposalApproveBL";
            tError.functionName = "checkData";
            tError.errorMessage = "此投保单已经开始核保，不能进行复核操作!";
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
        if (checkfamilyr)
        {
            VData urdata = new VData();
            TransferData urTransferData = new TransferData();
            urTransferData.setNameAndValue("ContNo", mContNo);
            urdata.add(urTransferData);
            CustomerdRelaInfoUI urCustomerdRelaInfoUI = new CustomerdRelaInfoUI();
            if (!urCustomerdRelaInfoUI.submitData(urdata, "INSERT"))
            {
                // @@错误处理
                CError tError = new CError();
                tError.moduleName = "CustomerdRelaInfoBL";
                tError.functionName = "submitData";
                tError.errorMessage = "生成客户关系时出错";
                this.mErrors.addOneError(tError);
                return false;
            }
        }

        mApproveDate = PubFun.getCurrentDate();
        mApproveTime = PubFun.getCurrentTime();
        LCIssuePolDB tLCIssuePolDB = new LCIssuePolDB();
        LCIssuePolSet tLCIssuePolSet = new LCIssuePolSet();
        String tSql = "Select * from lcissuepol where ContNo = '" + mContNo
                + "' and backobjtype = '1' and REPLYMAN is null ";
        tLCIssuePolSet = tLCIssuePolDB.executeQuery(tSql);
        if (tLCIssuePolSet == null)
        {
            CError tError = new CError();
            tError.moduleName = "ProposalApproveAfterInitService";
            tError.functionName = "dealData";
            tError.errorMessage = "查询问题件失败!";
            this.mErrors.addOneError(tError);
        }
        System.out.println("size==" + tLCIssuePolSet.size());
        if (tLCIssuePolSet.size() > 0)
        {
            System.out.println("ApproveFlag = 1");
            mApproveFlag = "1"; //有已发的操作员问题件,则须复核修改
        }
        else
        {
            System.out.println("ApproveFlag = 9");
            mApproveFlag = "9";
        }

        //准备保单的复核标志
        mLCContSchema.setApproveFlag(mApproveFlag);
        mLCContSchema.setApproveDate(mApproveDate);
        mLCContSchema.setApproveTime(mApproveTime);
        mLCContSchema.setApproveCode(mOperate);
        mLCContSchema.setModifyDate(mApproveDate);
        mLCContSchema.setModifyTime(mApproveTime);

        //准备险种保单的复核标志
        for (int i = 1; i <= mLCPolSet.size(); i++)
        {
            mLCPolSet.get(i).setApproveFlag(mApproveFlag);
            mLCPolSet.get(i).setApproveDate(mApproveDate);
            mLCPolSet.get(i).setApproveTime(mApproveTime);
            mLCPolSet.get(i).setApproveCode(mOperate);
            mLCPolSet.get(i).setModifyDate(mApproveDate);
            mLCPolSet.get(i).setModifyTime(mApproveTime);
        }
        return true;
    }

    /**
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
     * @return
     */
    private boolean prepareTransferData()
    {
        System.out.println("-----ApproveFlag==" + mApproveFlag);
        mTransferData.setNameAndValue("ApproveFlag", mApproveFlag);

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

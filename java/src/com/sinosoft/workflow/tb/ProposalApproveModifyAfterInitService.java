/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LCContDB;
import com.sinosoft.lis.db.LCInsuredDB;
import com.sinosoft.lis.db.LCPolDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.schema.LCContSchema;
import com.sinosoft.lis.tb.RewritePolInfo;
import com.sinosoft.lis.vschema.LCInsuredSet;
import com.sinosoft.lis.vschema.LCPolSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: 工作流服务类:新契约复核修改</p>
 * <p>Description: 复核修改工作流AfterInit服务类</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author: HYQ
 * @version 1.0
 */

public class ProposalApproveModifyAfterInitService implements AfterInitService
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 往后面传输数据的容器 */
    private VData mInputData = new VData();

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
    private String mApproveFlag;
    private String mMissionID;

    /**保单表*/
    private LCContSchema mLCContSchema = new LCContSchema();
    private LCInsuredSet mLCInsuredSet = new LCInsuredSet();
    private LCPolSet mLCPolSet = new LCPolSet();
    public ProposalApproveModifyAfterInitService()
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

        //校验业务数据
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

        //mResult.clear();
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
            tError.moduleName = "UWRReportModifyAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mContNo + "信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);

        //校验合同单下是否有被保人
        LCInsuredDB tLCInsuredDB = new LCInsuredDB();
        tLCInsuredDB.setContNo(mContNo);
        mLCInsuredSet = tLCInsuredDB.query();
        if (mLCInsuredSet == null || mLCInsuredSet.size() == 0)
        {
            CError tError = new CError();
            tError.moduleName = "UWRReportModifyAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单号" + mContNo + "下没有被保人信息!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //校验合同单下是否有险种单
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setContNo(mContNo);
        mLCPolSet = tLCPolDB.query();
        if (mLCPolSet == null || mLCPolSet.size() < 0)
        {

            CError tError = new CError();
            tError.moduleName = "UWRReportModifyAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "险种保单" + mContNo + "信息查询失败!";
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
        //mInputData = cInputData;
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

        //获得登陆机构编码
        mManageCom = mGlobalInput.ManageCom;
        if (mManageCom == null || mManageCom.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ProposalApproveAfterInitService";
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
            tError.errorMessage = "前台传输业务数据中ContNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 具体操作为从TransferData取出投保单ID，检索此投保单下所有被保人和险种信息；
     * 如果被保人（投保人）重要信息与此被保人险种中的信息存在变化则重新计算险种的保额和保费；
     * 如果在处理过程中出错，则返回false,否则返回true
     */
    private boolean dealData()
    {
        RewritePolInfo dealpol = new RewritePolInfo();
        if (!dealpol.updatePolInfo(mLCContSchema))
        {
            mErrors.copyAllErrors(dealpol.mErrors);
            return false;
        }
        mInputData.clear();
        System.out.println(dealpol.getResult().size());
        mInputData = dealpol.getResult();
        mApproveFlag = "0"; //复核确认标志
        //准备保单的复核标志
        mLCContSchema.setApproveFlag(mApproveFlag);
        for (int i = 1; i < mLCPolSet.size(); i++)
        {
            mLCPolSet.get(i).setApproveFlag(mApproveFlag);
        }

        return true;
    }

    /**
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
     * @return
     */
    private boolean prepareTransferData()
    {

        mTransferData.setNameAndValue("PrtSeq", mLCContSchema.getPrtNo());
        mTransferData.setNameAndValue("AgentCode", mLCContSchema.getAgentCode());
        mTransferData.setNameAndValue("AppntNo", mLCContSchema.getAppntNo());
        mTransferData.setNameAndValue("AppntName", mLCContSchema.getAppntName());
        mTransferData.setNameAndValue("ManageCom", mLCContSchema.getManageCom());
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

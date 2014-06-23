/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.tb.TBPrepareIssueBL;
import com.sinosoft.lis.db.LAAgentDB;
import com.sinosoft.lis.db.LABranchGroupDB;
import com.sinosoft.lis.db.LCContDB;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.schema.LCContSchema;
import com.sinosoft.lis.schema.LOPRTManagerSchema;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: 工作流节点任务:新契约发特殊件问题通知书 </p>
 * <p>Description: 工作流发特殊件问题通知书AfterInit服务类</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class UWSpecIssueAfterInitService implements AfterInitService
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 往界面传输数据的容器 */
    private VData mResult = new VData();

    /** 从前台得到的数据 */
    private VData mInputData = new VData();

    /** 往工作流引擎中传输数据的容器 */
//    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();

    /** 业务数据操作字符串 */
    private String mContNo;

    /**保单表*/
    private LCContSchema mLCContSchema = new LCContSchema();

    /** 问题见表 */
    private LCIssuePolSet tLCIssuePolSet = new LCIssuePolSet();

    /** 打印管理表 */
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();

    public UWSpecIssueAfterInitService()
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
        //得到外部传入的数据,将数据备份到本类中
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

        System.out.println("Start SysUWNoticeBL Submit...");

        //mResult.clear();
        return true;
    }

    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     */
    private boolean dealData()
    {
        TBPrepareIssueBL tTBPrepareIssueBL = new TBPrepareIssueBL();
        if (!tTBPrepareIssueBL.submitData(mInputData, ""))
        {
            CError tError = new CError();
            tError.moduleName = "UWSpecIssueAfterInitService";
            tError.functionName = "delaData";
            tError.errorMessage = "向TBPrepareIssueBL传输数据出错!";
            this.mErrors.checkErrors(tTBPrepareIssueBL.mErrors);
            return false;
        }
        try
        {
            mLOPRTManagerSchema = (LOPRTManagerSchema) tTBPrepareIssueBL.getResult().
                    get(0);
            tLCIssuePolSet = (LCIssuePolSet) tTBPrepareIssueBL.getResult().get(1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            CError tError = new CError();
            tError.moduleName = "UWSpecIssueAfterInitService";
            tError.functionName = "delaData";
            tError.errorMessage = "向TBPrepareIssueBL传输数据出错!";
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
        mInputData = (VData) cInputData.clone();

        mTransferData = (TransferData) mInputData.getObjectByObjectName(
                "TransferData", 0);

        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mContNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWSpecIssueAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中ContNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
     * @return
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
            tError.moduleName = "UWSpecIssueAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人表LAAgent查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLAAgentSet.get(1).getAgentGroup() == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "UWSpecIssueAfterInitService";
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
            tError.moduleName = "UWSpecIssueAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人展业机构表LABranchGroup查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLABranchGroupSet.get(1).getBranchAttr() == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "UWSpecIssueAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人展业机构表LABranchGroup中展业机构信息丢失!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mTransferData.setNameAndValue("PrtSeq", mLOPRTManagerSchema.getPrtSeq());
        mTransferData.setNameAndValue("OldPrtSeq",
                mLOPRTManagerSchema.getPrtSeq());
        mTransferData.setNameAndValue("AgentCode", mLCContSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup",
                tLAAgentSet.get(1).getAgentGroup());
        mTransferData.setNameAndValue("BranchAttr",
                tLABranchGroupSet.get(1).getBranchAttr());
        mTransferData.setNameAndValue("ManageCom", mLCContSchema.getManageCom());
        return true;
    }

    /**
     * 工作流准备后台提交数据
     * @return boolean
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();

        if (mLOPRTManagerSchema != null)
        {
            map.put(mLOPRTManagerSchema, "INSERT");
        }
        if (tLCIssuePolSet.size() != 0)
        {
            map.put(tLCIssuePolSet, "UPDATE");
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
     * 返回处理后的结果
     * @return VData
     */
    public boolean checkData()
    {
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mContNo);
        LCContSet tLCContSet = new LCContSet();
        tLCContSet = tLCContDB.query();
        if (tLCContSet.size() == 0)
        {
            CError tError = new CError();
            tError.moduleName = "UWSpecIssueAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "合同信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema = tLCContSet.get(1);
        return true;
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

    public static void main(String[] args)
    {
        //SysUWNoticeBL sysUWNoticeBL1 = new SysUWNoticeBL();
    }
}

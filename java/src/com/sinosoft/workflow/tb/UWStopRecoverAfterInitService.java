/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LAAgentDB;
import com.sinosoft.lis.db.LCContDB;
import com.sinosoft.lis.db.LCStopInsuredDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.schema.LCContSchema;
import com.sinosoft.lis.vschema.LAAgentSet;
import com.sinosoft.lis.vschema.LCContSet;
import com.sinosoft.lis.vschema.LCStopInsuredSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;


/**
 * <p>Title: 被保人暂停恢复 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @modify by Yuanaq
 * @version 1.1
 */

public class UWStopRecoverAfterInitService implements AfterInitService
{

    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();


    /** 往后面传输数据的容器 */
    private VData mInputData;


    /** 往界面传输数据的容器 */
    private VData mResult = new VData();


    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();
    private LCStopInsuredSet tLCStopInsuredSet = new LCStopInsuredSet();
    private LCContSchema mLCContSchema = new LCContSchema();

    /** 数据操作字符串 */
    private String mOperater;
//    private String mManageCom;
//    private String mOperate;
    private String mMissionID;
    private String mSubMissionID;

    /** 业务操作字符串 */
    private String mPrtNo;
    private String mContNo;

    public UWStopRecoverAfterInitService()
    {}


    /**
     * submitData
     *
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

//        System.out.println("Start  dealData...");

        //进行业务处理
        if (!dealData())
        {
            return false;
        }

//        System.out.println("dealData successful!");

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

//        System.out.println("Start  Submit...");

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
        if (tLCStopInsuredSet.size() > 0)
        {
            StringBuffer tSBql = new StringBuffer();
            map.put(tLCStopInsuredSet, "DELETE");
            for (int i = 1; i <= tLCStopInsuredSet.size(); i++)
            {
                tSBql = new StringBuffer(128);
                tSBql.append("Update LCInsured set InsuredStat='0' where Insuredno='");
                tSBql.append(tLCStopInsuredSet.get(i).getInsuredNo());
                tSBql.append("'");
//                map.put("Update LCInsured set InsuredStat='0' where Insuredno='" +
//                        tLCStopInsuredSet.get(i).getInsuredNo() + "'",
//                        "UPDATE");
                map.put(tSBql.toString(), "UPDATE");
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
        LCStopInsuredDB tLCStopInsuredDB = new LCStopInsuredDB();

        tLCStopInsuredDB.setPrtNo(mPrtNo);
        tLCStopInsuredSet = tLCStopInsuredDB.query();

        if (tLCStopInsuredSet == null || tLCStopInsuredSet.size() <= 0)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWStopRecoverAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "未查到被保人暂停信息!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //加入校验，限制其只能在签单后做暂停恢复，以后可能会放开
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setPrtNo(mPrtNo);
        LCContSet tLCContSet = new LCContSet();
        tLCContSet = tLCContDB.query();
        if (tLCContSet == null || tLCContSet.size() <= 0)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWStopRecoverAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "合同信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        int tflag = 0;
        for (int i = 1; i <= tLCContSet.size(); i++)
        {
            if (tLCContSet.get(i).getAppFlag().equals("1"))
            {
                tflag = 1;
            }
        }
        if (tflag == 0)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWStopRecoverAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "暂时不支持签单前的暂停恢复!";
            this.mErrors.addOneError(tError);
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
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWStopRecoverAfterInitService";
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
            tError.moduleName = "UWStopRecoverAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据Operate失败!";
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
            tError.moduleName = "UWStopRecoverAfterInitService";
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
            tError.moduleName = "UWStopRecoverAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的任务ID
        mPrtNo = (String) mTransferData.getValueByName("PrtNo");
        if (mPrtNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWStopRecoverAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中PrtNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的任务ID
        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mContNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWStopRecoverAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中ContNo失败!";
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
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mContNo);
        if (!tLCContDB.getInfo())
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWStopRecoverAfterInitService";
            tError.functionName = "dealData";
            tError.errorMessage = "查询合同信息失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema = tLCContDB.getSchema();

        return true;
    }


    /**
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
     * @return boolean
     */
    private boolean prepareTransferData()
    {

        mTransferData.setNameAndValue("PrtNo", mPrtNo);
        mTransferData.setNameAndValue("ContNo", mContNo);
        mTransferData.setNameAndValue("ProposalContNo", mLCContSchema.getProposalContNo());
        mTransferData.setNameAndValue("AgentCode", mLCContSchema.getAgentCode());

        mTransferData.setNameAndValue("AppntNo", mLCContSchema.getAppntNo());
        mTransferData.setNameAndValue("AppntName", mLCContSchema.getAppntName());
        mTransferData.setNameAndValue("ManageCom", mLCContSchema.getManageCom());

        LAAgentDB tLAAgentDB = new LAAgentDB();
        LAAgentSet tLAAgentSet = new LAAgentSet();
        tLAAgentDB.setAgentCode(mLCContSchema.getAgentCode());
        tLAAgentSet = tLAAgentDB.query();
        if (tLAAgentSet == null || tLAAgentSet.size() != 1)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "ProposalApproveAfterEndService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人表LAAgent查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mTransferData.setNameAndValue("AgentName", tLAAgentSet.get(1).getName());

        return true;
    }


    /**
     * getResult
     *
     * @return VData
     */
    public VData getResult()
    {
        return mResult;
    }


    /**
     * getReturnTransferData
     *
     * @return TransferData
     */

    public TransferData getReturnTransferData()
    {
        return mTransferData;
    }


    /**
     * getErrors
     *
     * @return CErrors
     */
    public CErrors getErrors()
    {
        return mErrors;
    }

}

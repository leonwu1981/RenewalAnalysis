/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.schema.LZSysCertifySchema;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: 工作流节点任务:新契约回收面见通知书工作流服务类 </p>
 * <p>Description: 回收新契约免检通知书工作流AfterInit服务类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class UWTakeBackRReportAfterInitService implements AfterInitService
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 往界面传输数据的容器 */
    private VData mResult = new VData();

    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();

    /** 业务相关处理类 */
    private LZSysCertifySchema mLZSysCertifySchema = new LZSysCertifySchema();

    /** 数据操作字符串 */
    private String mOperater;
    private String mManageCom;

    /** 业务数据操作字符串 */
    private String mMissionID;
    private String mPRTManagerSQL;
    private String mUWMasterSQL;

    /**执行新契约工作流特约活动表任务ID：0000000003*/
    public UWTakeBackRReportAfterInitService()
    {}

    /**
     * 传输数据的公共方法
     * @param cInputData VData 输入的数据
     * @param cOperate String 数据操作
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
        map.put(mPRTManagerSQL, "UPDATE");
        map.put(mUWMasterSQL, "UPDATE");
        mResult.add(map);
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
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackRReportAfterInitService";
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
            tError.moduleName = "UWTakeBackRReportAfterInitService";
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
            tError.moduleName = "UWTakeBackRReportAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackRReportAfterInitService";
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
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("UPDATE LOPRTManager SET StateFlag = ''2'' WHERE PrtSeq = '");
        tSBql.append(mLZSysCertifySchema.getCertifyNo());
        tSBql.append("'");
//        mPRTManagerSQL = "UPDATE LOPRTManager SET StateFlag = ''2'' WHERE 1=1 and PrtSeq = '"
//                + mLZSysCertifySchema.getCertifyNo() + "' ";
        mPRTManagerSQL = tSBql.toString();

        tSBql = new StringBuffer(256);
        tSBql.append("UPDATE LCUWMaster SET ReportFlag = ''2'' WHERE ProposalNo = ( SELECT OtherNo FROM LOPrtManager WHERE PrtSeq ='");
        tSBql.append(mLZSysCertifySchema.getCertifyNo());
        tSBql.append("')");
//        mUWMasterSQL = "UPDATE LCUWMaster SET ReportFlag = ''2'' WHERE ProposalNo = ( SELECT OtherNo FROM LOPrtManager WHERE PrtSeq ='"
//                + mLZSysCertifySchema.getCertifyNo() + "') ";
        mUWMasterSQL = tSBql.toString();
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

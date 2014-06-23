/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.xb;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: </p>
 * <p>Description:工作流节点任务:续保人工核保特约录入服务类 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PRnewUWSpecAfterInitService implements AfterInitService
{

    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往后面传输数据的容器 */
    private VData mInputData;
    /** 往界面传输数据的容器 */
    private VData mResult = new VData();
    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();
    //private VData mIputData = new VData();
    private TransferData mTransferData = new TransferData();
    /**工作流引擎 */
    ActivityOperator mActivityOperator = new ActivityOperator();
    /** 数据操作字符串 */
    private String mOperater;
    private String mManageCom;
    private String mOperate;
    /** 业务数据操作字符串 */
    private String mPolNo;
    private String mPrtNo;
//    private String mInsuredNo;
    private String mSpecReason;
    private String mRemark;
    private Reflections mReflections = new Reflections();

    /**执行续保工作流特约活动表任务0000000003*/
    /**保单表*/
    private LCPolSchema mLCPolSchema = new LCPolSchema();
    /** 续保核保主表 */
    private LCUWMasterSchema mLCUWMasterSchema = new LCUWMasterSchema();
    /** 特约表 */
    private LCSpecSchema mLCSpecSchema = new LCSpecSchema();
    private LCSpecSet mLCSpecSet = new LCSpecSet();
    private LBSpecSet mLBSpecSet = new LBSpecSet();
    /** 续保备注表 */
    private LCRemarkSchema mLCRemarkSchema = new LCRemarkSchema();
//    private LBRemarkSchema mLBRemarkSchema = new LBRemarkSchema();
    private LBRemarkSet mLBRemarkSet = new LBRemarkSet();
    private LCRemarkSet mLCRemarkSet = new LCRemarkSet();

    public PRnewUWSpecAfterInitService()
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
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     */
    private boolean dealData()
    {
        // 核保特约信息
        if (!prepareSpec())
        {
            return false;
        }
        // 核保备注信息
        if (!prepareRemark())
        {
            return false;
        }

        return true;

    }


    /**
     * 校验业务数据
     * @return
     */
    private boolean checkData()
    {
        //校验保单信息
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setPolNo(mPolNo);
        if (!tLCPolDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PRnewSpecAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mPolNo + "信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCPolSchema.setSchema(tLCPolDB);
        mPrtNo = mLCPolSchema.getPrtNo();

        //校验续保批单核保主表
        //校验保单信息
        LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
        tLCUWMasterDB.setProposalNo(mPolNo);
        if (!tLCUWMasterDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PRnewSpecAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mPolNo + "续保批单核保主表信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCUWMasterSchema.setSchema(tLCUWMasterDB);

        // 处于未打印状态的核保通知书在打印队列中只能有一个
        // 条件：同一个单据类型，同一个其它号码，同一个其它号码类型
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

        tLOPRTManagerDB.setCode(PrintManagerBL.CODE_UW); //核保通知书
        tLOPRTManagerDB.setOtherNo(mPolNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_INDPOL); //保单号
        tLOPRTManagerDB.setStandbyFlag2(mLCPolSchema.getPrtNo());
        tLOPRTManagerDB.setStateFlag("0");

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "查询打印管理表信息出错!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLOPRTManagerSet.size() != 0)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "在打印队列中已有一个处于未打印状态的核保通知书!";
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
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewUWSpecAfterInitService";
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
            tError.moduleName = "PRnewUWSpecAfterInitService";
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
            tError.moduleName = "PRnewUWSpecAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mOperate = cOperate;

        //获得业务数据
        if (mTransferData == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewUWSpecAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mPolNo = (String) mTransferData.getValueByName("PolNo");
        if (mPolNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewUWSpecAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中PolNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mSpecReason = (String) mTransferData.getValueByName("SpecReason");
//	if ( mSpecReason == null  )
//	{
//	  // @@错误处理
//	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
//	  CError tError = new CError();
//	  tError.moduleName = "PRnewUWSpecAfterInitService";
//	  tError.functionName = "getInputData";
//	  tError.errorMessage = "前台传输业务数据中SpecReason失败!";
//	  this.mErrors .addOneError(tError) ;
//	  return false;
//	}  //注销考虑到:只录入特约内容,而不录入特约原因时处理



        //获得业务特约通知书数据
        mLCSpecSchema = (LCSpecSchema) mTransferData.getValueByName("LCSpecSchema");
        if (mLCSpecSchema == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewUWSpecAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输获得业务特约数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mRemark = (String) mTransferData.getValueByName("Remark");
//	if ( mRemark == null  )
//	{
//	  // @@错误处理
//	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
//	  CError tError = new CError();
//	  tError.moduleName = "PRnewUWSpecAfterInitService";
//	  tError.functionName = "getInputData";
//	  tError.errorMessage = "前台传输业务数据中mRemark失败!";
//	  this.mErrors .addOneError(tError) ;
//	  return false;
//	}  //注销考虑到:只录入特约内容,而不录入续保备注时处理


        return true;
    }


    /**
     * 准备特约资料信息
     * 输出：如果发生错误则返回false,否则返回true
     */
    private boolean prepareSpec()
    {

        //取险种名称
        LMRiskSchema tLMRiskSchema = new LMRiskSchema();
        LMRiskDB tLMRiskDB = new LMRiskDB();
        tLMRiskDB.setRiskCode(mLCPolSchema.getRiskCode());
        //tLMRiskDB.setRiskVer(mLCPolSchema.getRiskVersion());
        if (!tLMRiskDB.getInfo())
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PRnewUWSpecAfterInitService";
            tError.functionName = "prepareHealth";
            tError.errorMessage = "取险种名称失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //取代理人姓名
        LAAgentDB tLAAgentDB = new LAAgentDB();
        tLAAgentDB.setAgentCode(mLCPolSchema.getAgentCode());
        if (!tLAAgentDB.getInfo())
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PRnewUWSpecAfterInitService";
            tError.functionName = "prepareHealth";
            tError.errorMessage = "取代理人姓名失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //准备特约的数据
        if (mLCSpecSchema != null && !mLCSpecSchema.getSpecContent().trim().equals(""))
        {
//		mLCSpecSchema.setSpecNo(PubFun1.CreateMaxNo("SpecNo",PubFun.getNoLimit(mGlobalInput.ComCode)));
            //mLCSpecSchema.setPolNo(mPolNo);
            //mLCSpecSchema.setPolType("1");
            //mLCSpecSchema.setEndorsementNo("");
            //mLCSpecSchema.setSpecType();
            //mLCSpecSchema.setSpecCode();
            //mLCSpecSchema.setSpecContent();//前台已准备
            mLCSpecSchema.setPrtFlag("1");
            mLCSpecSchema.setBackupType("");
            //mLCSpecSchema.setState("0") ;
            mLCSpecSchema.setOperator(mOperater);
            mLCSpecSchema.setMakeDate(PubFun.getCurrentDate());
            mLCSpecSchema.setMakeTime(PubFun.getCurrentTime());
            mLCSpecSchema.setModifyDate(PubFun.getCurrentDate());
            mLCSpecSchema.setModifyTime(PubFun.getCurrentTime());
        }
        else
        {
            mLCSpecSchema = null;
        }

        //准备备份上一次的特约信息
        LCSpecDB tLCSpecDB = new LCSpecDB();

        tLCSpecDB.setPolNo(mPolNo);
        mLCSpecSet = tLCSpecDB.query();
        System.out.println("LCSpecSet.size()" + mLCSpecSet.size());
        for (int i = 1; i <= mLCSpecSet.size(); i++)
        {
            LBSpecSchema tLBSpecSchema = new LBSpecSchema();
            mReflections.transFields(tLBSpecSchema, mLCSpecSet.get(i));
            tLBSpecSchema.setMakeDate(PubFun.getCurrentDate());
            tLBSpecSchema.setMakeTime(PubFun.getCurrentTime());
            mLBSpecSet.add(tLBSpecSchema);
        }

        //准备续保核保主表信息
        LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
        tLCUWMasterDB.setProposalNo(mPolNo);
        if (!tLCUWMasterDB.getInfo())
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PRnewUWSpecAfterInitService";
            tError.functionName = "prepareSpec";
            tError.errorMessage = "无续保批单核保主表信息!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCUWMasterSchema.setSchema(tLCUWMasterDB);
        String tSpecReason = (String) mTransferData.getValueByName("SpecReason");
        if (tSpecReason != null && !tSpecReason.trim().equals(""))
        {
            mLCUWMasterSchema.setSpecReason(tSpecReason);
        }
        else
        {
            mLCUWMasterSchema.setSpecReason("");
        }

        if (mLCSpecSchema != null)
        {
            mLCUWMasterSchema.setSpecFlag("1"); //特约标识
        }
        else
        {
            mLCUWMasterSchema.setSpecFlag("0"); //特约标识
        }

        mLCUWMasterSchema.setOperator(mOperater);
        mLCUWMasterSchema.setManageCom(mManageCom);
        mLCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
        mLCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());

        return true;
    }


    /**
     * 准备特约资料信息
     * 输出：如果发生错误则返回false,否则返回true
     */
    private boolean prepareRemark()
    {

        //准备续保备注的数据
        if (mRemark != null && !mRemark.trim().equals(""))
        {
            String tSerialNo = PubFun1.CreateMaxNo("RemarkNo", 20);
            mLCRemarkSchema.setSerialNo(tSerialNo);
            mLCRemarkSchema.setPolNo(mPolNo);
            mLCRemarkSchema.setPrtNo(mPrtNo);
            mLCRemarkSchema.setOperatePos("1");
            mLCRemarkSchema.setRemarkCont(mRemark);
            mLCRemarkSchema.setManageCom(mManageCom);
            mLCRemarkSchema.setOperator(mOperater);
            mLCRemarkSchema.setMakeDate(PubFun.getCurrentDate());
            mLCRemarkSchema.setMakeTime(PubFun.getCurrentTime());
            mLCRemarkSchema.setModifyDate(PubFun.getCurrentDate());
            mLCRemarkSchema.setModifyTime(PubFun.getCurrentTime());
        }
        else
        {
            mLCRemarkSchema = null;
        }

        //准备备份上一次的续保备注信息
        LCRemarkDB tLCRemarkDB = new LCRemarkDB();
        tLCRemarkDB.setPolNo(mPolNo);
        tLCRemarkDB.setPrtNo(mPrtNo);
        tLCRemarkDB.setOperatePos("1");
        tLCRemarkDB.setPolNo(mPolNo);
        mLCRemarkSet = tLCRemarkDB.query();
        System.out.println("tLCRemarkSet.size()" + mLCRemarkSet.size());
        for (int i = 1; i <= mLCRemarkSet.size(); i++)
        {
            LBRemarkSchema tLBRemarkSchema = new LBRemarkSchema();
            mReflections.transFields(tLBRemarkSchema, mLCRemarkSet.get(i));
            tLBRemarkSchema.setEdorNo("99999999999999999999");
            tLBRemarkSchema.setMakeDate(PubFun.getCurrentDate());
            tLBRemarkSchema.setMakeTime(PubFun.getCurrentTime());
            mLBRemarkSet.add(tLBRemarkSchema);
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

        //添加删除上一个特约数据
        if (mLCSpecSet != null && mLCSpecSet.size() > 0)
        {
            map.put(mLCSpecSet, "DELETE");
        }
        //添加本次续保特约数据
        if (mLCSpecSchema != null)
        {
            map.put(mLCSpecSchema, "INSERT");
        }

        //添加删除上一个续保备注数据
        if (mLCRemarkSet != null && mLCRemarkSet.size() > 0)
        {
            map.put(mLCRemarkSet, "DELETE");
        }
        //添加续保特约备份数据
        if (mLBSpecSet != null && mLBSpecSet.size() > 0)
        {
            map.put(mLBSpecSet, "INSERT");
        }

        //添加本次续保备注数据
        if (mLCRemarkSchema != null)
        {
            map.put(mLCRemarkSchema, "INSERT");
        }

        //添加续保备注备份数据
        if (mLBRemarkSet != null && mLBRemarkSet.size() > 0)
        {
            map.put(mLBRemarkSet, "INSERT");
        }

        //添加续保批单核保主表通知书打印管理表数据
        map.put(mLCUWMasterSchema, "UPDATE");

        mResult.add(map);
        return true;
    }

    public VData getResult()
    {
        return mResult;
    }

    public TransferData getReturnTransferData()
    {
        return mTransferData;
    }

    public CErrors getErrors()
    {
        return mErrors;
    }
}

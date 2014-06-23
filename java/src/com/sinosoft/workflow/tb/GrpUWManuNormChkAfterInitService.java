/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import java.util.Date;
import java.util.Hashtable;

import com.sinosoft.lis.bl.LCDutyBL;
import com.sinosoft.lis.bl.LCPolBL;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.tb.CalBL;
import com.sinosoft.lis.vbl.LCDutyBLSet;
import com.sinosoft.lis.vbl.LCGetBLSet;
import com.sinosoft.lis.vbl.LCPremBLSet;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.finfee.GrpNewTempFeeWithdrawBL;

/**
 * <p>
 * Title: 工作流服务类:团体新契约人工核保
 * </p>
 * <p>
 * Description: 人工核保工作流AfterInit服务类
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: SinoSoft
 * </p>
 * @author HYQ
 * @version 1.0
 */

public class GrpUWManuNormChkAfterInitService implements AfterInitService
{

	/** 错误处理类，每个需要错误处理的类中都放置该类 */
	public CErrors mErrors = new CErrors();

	/** 往界面传输数据的容器 */
	private VData mResult = new VData();
	
	private VData mReturnFeeData;

	// MMap mMap = new MMap();
	MMap mmMap = new MMap();

	/** 往工作流引擎中传输数据的容器 */
	private GlobalInput mGlobalInput = new GlobalInput();

	private TransferData mTransferData = new TransferData();

	/** 业务处理相关变量 */
	private String mGrpContNo = "";

	private String mPrtNo = "";

	private String mPolNo = "";

	private String mUWFlag = ""; // 核保标志

	private Date mvalidate = null;

	private String mUWIdea = ""; // 核保结论

	private int mpostday; // 延长天数

	private String mUWPopedom = ""; // 操作员核保级别

	private String mAppGrade = ""; // 上报级别

	private String mManageCom = ""; // 操作员机构

	private String mPrtSeqNo = "";

	private String mGetNoticeNo = "";

	private Reflections mReflections = new Reflections();

	/** 团体合同表 */
	private LCGrpContSet mLCGrpContSet = new LCGrpContSet();

	private LCGrpContSet mAllLCGrpContSet = new LCGrpContSet();

	private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();

	/** 团体保单表 */
	private LCGrpPolSet mLCGrpPolSet = new LCGrpPolSet();

	private LCGrpPolSet mAllLCGrpPolSet = new LCGrpPolSet();

	/** 合同表 */
	private LCContSet mLCContSet = new LCContSet();

	private LCContSet mAllLCContSet = new LCContSet();

	// private LCContSchema mLCContSchema = new LCContSchema();

	/** 打印管理表 */
	private LOPRTManagerSet mLOPRTManagerSet = new LOPRTManagerSet();

	/** 保单表 */
	private LCPolSet mLCPolSet = new LCPolSet();
	
	private LCPolSet mAllLCPolSet = new LCPolSet(); 

	private LCPolSchema mLCPolSchema = new LCPolSchema();

	/** 保费项表 */
	private LCPremSet mLCPremSet = new LCPremSet();

	private LCPremSet mAllLCPremSet = new LCPremSet();

	private LCPremSet mmLCPremSet = new LCPremSet();

	/** 特别约定表 */
	private LCSpecSet mLCSpecSet = new LCSpecSet();

	private LCSpecSet mAllLCSpecSet = new LCSpecSet();

	/** 核保主表 */
	private LCUWMasterSet mLCUWMasterSet = new LCUWMasterSet();

	private LCUWMasterSet mAllLCUWMasterSet = new LCUWMasterSet();

	private LCCUWMasterSet mLCCUWMasterSet = new LCCUWMasterSet();

	private LCCUWMasterSet mAllLCCUWMasterSet = new LCCUWMasterSet();

	private LCGUWMasterSet mLCGUWMasterSet = new LCGUWMasterSet();

	private LCGUWMasterSet mAllLCGUWMasterSet = new LCGUWMasterSet();

	private LCGCUWMasterSet mLCGCUWMasterSet = new LCGCUWMasterSet();

	private LCGCUWMasterSet mAllLCGCUWMasterSet = new LCGCUWMasterSet();

	/** 核保子表 */
	private LCUWSubSet mLCUWSubSet = new LCUWSubSet();

	private LCUWSubSet mAllLCUWSubSet = new LCUWSubSet();

	private LCCUWSubSet mLCCUWSubSet = new LCCUWSubSet();

	private LCCUWSubSet mAllLCCUWSubSet = new LCCUWSubSet();

	private LCGUWSubSet mLCGUWSubSet = new LCGUWSubSet();

	private LCGUWSubSet mAllLCGUWSubSet = new LCGUWSubSet();

	private LCGCUWSubSet mLCGCUWSubSet = new LCGCUWSubSet();

	private LCGCUWSubSet mAllLCGCUWSubSet = new LCGCUWSubSet();

	/** 暂交费表 */
	private LJTempFeeSet outLJTempFeeSet = new LJTempFeeSet();

	private LBTempFeeSet outLBTempFeeSet = new LBTempFeeSet();

	private LJTempFeeClassSet outLJTempFeeClassSet = new LJTempFeeClassSet();

	private LBTempFeeClassSet outLBTempFeeClassSet = new LBTempFeeClassSet();

	/** 数据操作字符串 */
	private String mOperator;
	
	/****新增加的信息*/
	private LCPolSet tLCPolSet = new LCPolSet();  //一个人
//	private LCContSet tLCContSet = new LCContSet();
	
	private LCGetSet tLCGetSet = new LCGetSet();
	
	private LCDutySet tLCDutySet = new LCDutySet();  //一个人
	
	private LCPremSet tLCPremSet = new LCPremSet();  //一个人
	
	private  LCPolSchema tLCPolSchema = new LCPolSchema();

	private boolean tsign ;

	private ExeSQL mExeSQL = new ExeSQL();

	private String SQL;
	
	// [ 2011/01/06 inserted by qsnp099
	private Hashtable lmriskappTable;
	private Hashtable gilTable;
	private String grpcontnoFlag;
	// 2011/01/06 inserted by qsnp099 ]
	
	public GrpUWManuNormChkAfterInitService(){}
	
	/**
	 * 传输数据的公共方法
	 * @param cInputData VData
	 * @param cOperate String
	 * @return boolean
	 */
	public boolean submitData(VData cInputData, String cOperate)
	{
		// 得到外部传入的数据,将数据备份到本类中
		if (!getInputData(cInputData))
		{
			return false;
		}

		System.out.println("---GrpUWManuNormChkBL getInputData OK---");
		// mod by heyq 20041220 由于核保是可以再录入险种，所以去掉对是否复核通过的校验
		// 校验数据
		// if (!checkApprove(mLCGrpContSchema))
		// {
		// return false;
		// }

		// 判断是不是整单已经确认过
		if (!checkUWGrpPol(mLCGrpContSchema))
		{
			return false;
		}

		// 判断核保级别
		if (!checkUWGrade(mLCGrpContSchema))
		{
			return false;
		}

		/*
		 * //如果发核保通知书校验是不是主险 if(!checkMain()) { return false; }
		 */
		// commented by zhr 2004.11
		// 判断个单是不是全部通过(当核保结论为正常通过或通融承保时,要确保该团体单下的所有个单均已通过核保)
		if (!mUWFlag.equals("1") && !mUWFlag.equals("6") && !mUWFlag.equals("a") && !mUWFlag.equals("7")
				&& !mUWFlag.equals("8"))
		{
			if (!checkUWPol(mLCGrpContSchema))
			{
				return false;
			}
		}

		if (!dealData())
		{
			return false;
		}

		System.out.println("dealData successful!");

		// 为工作流下一节点属性字段准备数据
		if (!prepareTransferData())
		{
			return false;
		}
        //备份lcpol.subgil>0部分lcduty,lcprem,lcget表数据
		if(!bakLCTable())
		{
			return false;
		}
		//先将LCDuty表中的保额大于GIL标准的修改为GIl值
		if(!updateLCDuty()) {
			return false;
		}

		//重新计算保费
		if(!RecalculationPrem()) {
			return false;
		}
		
		//根据契约录入的GIL处理信息LCGilTrace表对保费保额进行调整
		if(!GilByLCGilTrace())
		{
			return false;
		}
		
		// 准备往后台的数据
		if (!prepareOutputData())
		{
			return false;
		}
		
		System.out.println("Start  Submit...");

		return true;
	}

	/**
	 * 数据操作类业务处理 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
	 * @return boolean
	 */
	private boolean dealData()
	{
		if (dealOnePol())
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * 操作一张保单的业务处理 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
	 * @return boolean
	 */
	private boolean dealOnePol()
	{
		mLCGrpContSchema.setUWFlag(mUWFlag);
		mLCGrpContSchema.setUWOperator(mOperator);
		mLCGrpContSchema.setUWDate(PubFun.getCurrentDate());
		mLCGrpContSchema.setOperator(mOperator);
		mLCGrpContSchema.setModifyDate(PubFun.getCurrentDate());
		mLCGrpContSchema.setModifyTime(PubFun.getCurrentTime());
		mLCGrpContSet.clear();
		mLCGrpContSet.add(mLCGrpContSchema);

		LCGrpPolDB tLCGrpPolDB = new LCGrpPolDB();
		tLCGrpPolDB.setGrpContNo(mGrpContNo);
		LCGrpPolSet tLCGrpPolSet = tLCGrpPolDB.query();
		if (tLCGrpPolSet == null || tLCGrpPolSet.size() <= 0)
		{
			// @@错误处理
			this.mErrors.copyAllErrors(tLCGrpPolDB.mErrors);
			CError tError = new CError();
			tError.moduleName = "GrpUWManuNormChkBL";
			tError.functionName = "dealOnePol";
			tError.errorMessage = "集体险种保单查询失败!";
			this.mErrors.addOneError(tError);
			return false;
		}
		int n = tLCGrpPolSet.size();
		int i = 0;
		LCGrpPolSchema tLCGrpPolSchema;
		for (i = 1; i <= n; i++)
		{
			tLCGrpPolSchema = tLCGrpPolSet.get(i);
			tLCGrpPolSchema.setUWFlag(mUWFlag);
			tLCGrpPolSchema.setUWOperator(mOperator);
			tLCGrpPolSchema.setUWDate(PubFun.getCurrentDate());
			tLCGrpPolSchema.setOperator(mOperator);
			tLCGrpPolSchema.setModifyDate(PubFun.getCurrentDate());
			tLCGrpPolSchema.setModifyTime(PubFun.getCurrentTime());
//			
//			System.out.println("++++++++++++++++++++++++++++++++++"+tLCGrpPolSchema.getGIL());
			mLCGrpPolSet.add(tLCGrpPolSchema);
		}

		LCGCUWMasterSchema tLCGCUWMasterSchema = new LCGCUWMasterSchema();
		LCGCUWMasterDB tLCGCUWMasterDB = new LCGCUWMasterDB();
		tLCGCUWMasterDB.setGrpContNo(mGrpContNo);
		LCGCUWMasterSet tLCGCUWMasterSet = new LCGCUWMasterSet();
		tLCGCUWMasterSet = tLCGCUWMasterDB.query();
		if (tLCGCUWMasterDB.mErrors.needDealError())
		{
			// @@错误处理
			this.mErrors.copyAllErrors(tLCGCUWMasterDB.mErrors);
			CError tError = new CError();
			tError.moduleName = "GrpUWManuNormChkBL";
			tError.functionName = "prepareUW";
			tError.errorMessage = "集体核保总表取数失败!";
			this.mErrors.addOneError(tError);
			return false;
		}

		n = tLCGCUWMasterSet.size();

		System.out.println("mastercount=" + n);

		if (n == 1)
		{
			tLCGCUWMasterSchema = tLCGCUWMasterSet.get(1);
			int uwno = tLCGCUWMasterSet.get(1).getUWNo();
			uwno++;
			tLCGCUWMasterSchema.setUWNo(uwno);

			tLCGCUWMasterSchema.setPassFlag(mUWFlag); // 通过标志
			tLCGCUWMasterSchema.setAutoUWFlag("2"); // 1 自动核保 2 人工核保
			tLCGCUWMasterSchema.setUWGrade(mUWPopedom);
			tLCGCUWMasterSchema.setAppGrade(mAppGrade);
			tLCGCUWMasterSchema.setState(mUWFlag);
			tLCGCUWMasterSchema.setUWIdea(mUWIdea);
			tLCGCUWMasterSchema.setOperator(mOperator); // 操作员
			tLCGCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
			tLCGCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());
		}
		else
		{
			// @@错误处理
			this.mErrors.copyAllErrors(tLCGCUWMasterDB.mErrors);
			CError tError = new CError();
			tError.moduleName = "GrpUWManuNormChkBL";
			tError.functionName = "prepareUW";
			tError.errorMessage = "集体核保总表取数据不唯一!";
			this.mErrors.addOneError(tError);
			return false;
		}

		mLCGCUWMasterSet.clear();
		mLCGCUWMasterSet.add(tLCGCUWMasterSchema);

		// 核保轨迹表
		LCGCUWSubSchema tLCGCUWSubSchema = new LCGCUWSubSchema();
		LCGCUWSubDB tLCGCUWSubDB = new LCGCUWSubDB();
		tLCGCUWSubDB.setGrpContNo(mGrpContNo);
		LCGCUWSubSet tLCGCUWSubSet = new LCGCUWSubSet();
		tLCGCUWSubSet = tLCGCUWSubDB.query();
		if (tLCGCUWSubDB.mErrors.needDealError())
		{
			// @@错误处理
			this.mErrors.copyAllErrors(tLCGCUWSubDB.mErrors);
			CError tError = new CError();
			tError.moduleName = "GrpUWManuNormChkBl";
			tError.functionName = "prepareUW";
			tError.errorMessage = "集体核保轨迹表查询失败!";
			this.mErrors.addOneError(tError);
			return false;
		}

		int m = tLCGCUWSubSet.size();
		if (m >= 0)
		{
			m++; // 核保次数
			tLCGCUWSubSchema = new LCGCUWSubSchema(); // tLCGCUWSubSet.get(1);

			//Amended By Fang(20110524)
			//tLCGCUWSubSchema.setUWNo(m); // 第几次核保
			tLCGCUWSubSchema.setUWNo(tLCGCUWMasterSchema.getUWNo());
			//End(20110524)
			tLCGCUWSubSchema.setGrpContNo(tLCGCUWMasterSchema.getGrpContNo());
			tLCGCUWSubSchema.setProposalGrpContNo(tLCGCUWMasterSchema.getProposalGrpContNo());
			tLCGCUWSubSchema.setAgentCode(tLCGCUWMasterSchema.getAgentCode());
			tLCGCUWSubSchema.setAgentGroup(tLCGCUWMasterSchema.getAgentGroup());
			tLCGCUWSubSchema.setUWGrade(tLCGCUWMasterSchema.getUWGrade()); // 核保级别
			tLCGCUWSubSchema.setAppGrade(tLCGCUWMasterSchema.getAppGrade()); // 申请级别
			tLCGCUWSubSchema.setAutoUWFlag(tLCGCUWMasterSchema.getAutoUWFlag());
			tLCGCUWSubSchema.setState(tLCGCUWMasterSchema.getState());
			tLCGCUWSubSchema.setPassFlag(tLCGCUWMasterSchema.getState());
			tLCGCUWSubSchema.setPostponeDay(tLCGCUWMasterSchema.getPostponeDay());
			tLCGCUWSubSchema.setPostponeDate(tLCGCUWMasterSchema.getPostponeDate());
			tLCGCUWSubSchema.setUpReportContent(tLCGCUWMasterSchema.getUpReportContent());
			tLCGCUWSubSchema.setHealthFlag(tLCGCUWMasterSchema.getHealthFlag());
			tLCGCUWSubSchema.setSpecFlag(tLCGCUWMasterSchema.getSpecFlag());
			tLCGCUWSubSchema.setSpecReason(tLCGCUWMasterSchema.getSpecReason());
			tLCGCUWSubSchema.setQuesFlag(tLCGCUWMasterSchema.getQuesFlag());
			tLCGCUWSubSchema.setReportFlag(tLCGCUWMasterSchema.getReportFlag());
			tLCGCUWSubSchema.setChangePolFlag(tLCGCUWMasterSchema.getChangePolFlag());
			tLCGCUWSubSchema.setChangePolReason(tLCGCUWMasterSchema.getChangePolReason());
			tLCGCUWSubSchema.setAddPremReason(tLCGCUWMasterSchema.getAddPremReason());
			tLCGCUWSubSchema.setPrintFlag(tLCGCUWMasterSchema.getPrintFlag());
			tLCGCUWSubSchema.setPrintFlag2(tLCGCUWMasterSchema.getPrintFlag2());
			tLCGCUWSubSchema.setUWIdea(tLCGCUWMasterSchema.getUWIdea());
			tLCGCUWSubSchema.setOperator(tLCGCUWMasterSchema.getOperator()); // 操作员
			tLCGCUWSubSchema.setManageCom(tLCGCUWMasterSchema.getManageCom());
			tLCGCUWSubSchema.setMakeDate(PubFun.getCurrentDate());
			tLCGCUWSubSchema.setMakeTime(PubFun.getCurrentTime());
			tLCGCUWSubSchema.setModifyDate(PubFun.getCurrentDate());
			tLCGCUWSubSchema.setModifyTime(PubFun.getCurrentTime());
		}
		else
		{
			// @@错误处理
			this.mErrors.copyAllErrors(tLCGCUWSubDB.mErrors);
			CError tError = new CError();
			tError.moduleName = "GrpUWManuNormChkBL";
			tError.functionName = "prepareUW";
			tError.errorMessage = "集体核保轨迹表查询失败!";
			this.mErrors.addOneError(tError);
			return false;
		}
		mLCGCUWSubSet.clear();
		mLCGCUWSubSet.add(tLCGCUWSubSchema);

		LCGUWMasterSchema tLCGUWMasterSchema = new LCGUWMasterSchema();
		LCGUWMasterDB tLCGUWMasterDB = new LCGUWMasterDB();
		tLCGUWMasterDB.setGrpContNo(mGrpContNo);
		LCGUWMasterSet tLCGUWMasterSet = new LCGUWMasterSet();
		tLCGUWMasterSet = tLCGUWMasterDB.query();
		if (tLCGUWMasterDB.mErrors.needDealError())
		{
			// @@错误处理
			this.mErrors.copyAllErrors(tLCGUWMasterDB.mErrors);
			CError tError = new CError();
			tError.moduleName = "GrpUWManuNormChkBL";
			tError.functionName = "prepareUW";
			tError.errorMessage = "集体核保总表取数失败!";
			this.mErrors.addOneError(tError);
			return false;
		}

		n = tLCGUWMasterSet.size();
		System.out.println("mastercount=" + n);

		if (n <= 0)
		{
			// @@错误处理
			this.mErrors.copyAllErrors(tLCGUWMasterDB.mErrors);
			CError tError = new CError();
			tError.moduleName = "GrpUWManuNormChkBL";
			tError.functionName = "prepareUW";
			tError.errorMessage = "集体核保总表取数据不唯一!";
			this.mErrors.addOneError(tError);
			return false;
		}
		mLCGUWMasterSet.clear();
		mLCGUWSubSet.clear();
		for (i = 1; i <= n; i++)
		{
			tLCGUWMasterSchema = tLCGUWMasterSet.get(i);
			int uwno = tLCGUWMasterSet.get(i).getUWNo();
			uwno++;
			System.out.println("uwno==" + uwno);
			tLCGUWMasterSchema.setUWNo(uwno);
			tLCGUWMasterSchema.setPassFlag(mUWFlag); // 通过标志
			tLCGUWMasterSchema.setAutoUWFlag("2"); // 1 自动核保 2 人工核保
			tLCGUWMasterSchema.setUWGrade(mUWPopedom);
			tLCGUWMasterSchema.setAppGrade(mAppGrade);
			tLCGUWMasterSchema.setState(mUWFlag);
			tLCGUWMasterSchema.setUWIdea(mUWIdea);
			tLCGUWMasterSchema.setOperator(mOperator); // 操作员
			tLCGUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
			tLCGUWMasterSchema.setModifyTime(PubFun.getCurrentTime());
			mLCGUWMasterSet.add(tLCGUWMasterSchema);

			// 核保轨迹表
			LCGUWSubSchema tLCGUWSubSchema = new LCGUWSubSchema();
			LCGUWSubDB tLCGUWSubDB = new LCGUWSubDB();
			tLCGUWSubDB.setGrpPolNo(tLCGUWMasterSchema.getGrpPolNo());
			LCGUWSubSet tLCGUWSubSet = new LCGUWSubSet();
			tLCGUWSubSet = tLCGUWSubDB.query();
			if (tLCGUWSubDB.mErrors.needDealError())
			{
				// @@错误处理
				this.mErrors.copyAllErrors(tLCGUWSubDB.mErrors);
				CError tError = new CError();
				tError.moduleName = "GrpUWManuNormChkBl";
				tError.functionName = "prepareUW";
				tError.errorMessage = "集体核保轨迹表查询失败!";
				this.mErrors.addOneError(tError);
				return false;
			}

			m = tLCGUWSubSet.size();
			if (m >= 0)
			{
				m++; // 核保次数
				tLCGUWSubSchema = new LCGUWSubSchema(); // tLCGUWSubSet.get(1);

				//Amedned By Fang(20110524)
				//tLCGUWSubSchema.setUWNo(m); // 第几次核保
				tLCGUWSubSchema.setUWNo(tLCGUWMasterSchema.getUWNo());
				//End(20110524)
				tLCGUWSubSchema.setGrpContNo(tLCGUWMasterSchema.getGrpContNo());
				tLCGUWSubSchema.setGrpPolNo(tLCGUWMasterSchema.getGrpPolNo());
				tLCGUWSubSchema.setProposalGrpContNo(tLCGUWMasterSchema.getProposalGrpContNo());
				tLCGUWSubSchema.setGrpProposalNo(tLCGUWMasterSchema.getGrpProposalNo());
				tLCGUWSubSchema.setAgentCode(tLCGUWMasterSchema.getAgentCode());
				tLCGUWSubSchema.setAgentGroup(tLCGUWMasterSchema.getAgentGroup());
				tLCGUWSubSchema.setUWGrade(tLCGUWMasterSchema.getUWGrade()); // 核保级别
				tLCGUWSubSchema.setAppGrade(tLCGUWMasterSchema.getAppGrade()); // 申请级别
				tLCGUWSubSchema.setAutoUWFlag(tLCGUWMasterSchema.getAutoUWFlag());
				tLCGUWSubSchema.setState(tLCGUWMasterSchema.getState());
				tLCGUWSubSchema.setPassFlag(tLCGUWMasterSchema.getState());
				tLCGUWSubSchema.setPostponeDay(tLCGUWMasterSchema.getPostponeDay());
				tLCGUWSubSchema.setPostponeDate(tLCGUWMasterSchema.getPostponeDate());
				tLCGUWSubSchema.setUpReportContent(tLCGUWMasterSchema.getUpReportContent());
				tLCGUWSubSchema.setHealthFlag(tLCGUWMasterSchema.getHealthFlag());
				tLCGUWSubSchema.setSpecFlag(tLCGUWMasterSchema.getSpecFlag());
				tLCGUWSubSchema.setSpecReason(tLCGUWMasterSchema.getSpecReason());
				tLCGUWSubSchema.setQuesFlag(tLCGUWMasterSchema.getQuesFlag());
				tLCGUWSubSchema.setReportFlag(tLCGUWMasterSchema.getReportFlag());
				tLCGUWSubSchema.setChangePolFlag(tLCGUWMasterSchema.getChangePolFlag());
				tLCGUWSubSchema.setChangePolReason(tLCGUWMasterSchema.getChangePolReason());
				tLCGUWSubSchema.setAddPremReason(tLCGUWMasterSchema.getAddPremReason());
				tLCGUWSubSchema.setPrintFlag(tLCGUWMasterSchema.getPrintFlag());
				tLCGUWSubSchema.setPrintFlag2(tLCGUWMasterSchema.getPrintFlag2());
				tLCGUWSubSchema.setUWIdea(tLCGUWMasterSchema.getUWIdea());
				tLCGUWSubSchema.setOperator(tLCGUWMasterSchema.getOperator()); // 操作员
				tLCGUWSubSchema.setManageCom(tLCGUWMasterSchema.getManageCom());
				tLCGUWSubSchema.setMakeDate(PubFun.getCurrentDate());
				tLCGUWSubSchema.setMakeTime(PubFun.getCurrentTime());
				tLCGUWSubSchema.setModifyDate(PubFun.getCurrentDate());
				tLCGUWSubSchema.setModifyTime(PubFun.getCurrentTime());
			}
			else
			{
				// @@错误处理
				this.mErrors.copyAllErrors(tLCGUWSubDB.mErrors);
				CError tError = new CError();
				tError.moduleName = "GrpUWManuNormChkBL";
				tError.functionName = "prepareUW";
				tError.errorMessage = "集体核保轨迹表查询失败!";
				this.mErrors.addOneError(tError);
				return false;
			}
			mLCGUWSubSet.add(tLCGUWSubSchema);
		}

		// 上级核保
		if (mUWFlag.equals("6"))
		{
			uplevel();
		}
		
		if (mUWFlag.equals("1") || mUWFlag.equals("6") || mUWFlag.equals("a"))
		{
			// 保单
			if (!preparePol())
			{
				return false;
			}

			// 核保信息
			if (!prepareUW())
			{
				return false;
			}
			// 如果是拒保在此生成打印管理表数据
			if (mUWFlag.equals("1"))
			{
				// 生成拒保打印数据
				if (!setLOPRTManager())
				{
					// @@错误处理
					CError tError = new CError();
					tError.moduleName = "GrpQuestInputChkBL";
					tError.functionName = "prepareQuest";
					tError.errorMessage = "生成拒保打印数据!";
					this.mErrors.addOneError(tError);

					return false;
				}
				// 生成新契约退费打印数据
				if (!setBackTempFeeLOPRTManager())
				{
					// @@错误处理
					CError tError = new CError();
					tError.moduleName = "GrpQuestInputChkBL";
					tError.functionName = "prepareQuest";
					tError.errorMessage = "生成新契约退费打印数据!";
					this.mErrors.addOneError(tError);

					return false;
				}

			}

		}else {
			preparePPol();
		}

		if (mUWFlag.equals("2"))
		{
			TimeAccept();
		}

		if (mUWFlag.equals("3"))
		{
			CondAccept();

			LCPremSet tLCPremSet = new LCPremSet();
			tLCPremSet.set(mmLCPremSet);
			mAllLCPremSet.add(tLCPremSet);

			LCSpecSet tLCSpecSet = new LCSpecSet();
			tLCSpecSet.set(mLCSpecSet);
			mAllLCSpecSet.add(tLCSpecSet);
		}

		LCPolSet tLCPolSet = new LCPolSet();
		tLCPolSet.set(mLCPolSet);
		mAllLCPolSet.add(tLCPolSet);

		LCContSet tLCContSet = new LCContSet();
		tLCContSet.set(mLCContSet);
		mAllLCContSet.add(tLCContSet);

		LCGrpPolSet tLCGrpPolSet1 = new LCGrpPolSet();
		tLCGrpPolSet1.set(mLCGrpPolSet);
		mAllLCGrpPolSet.add(tLCGrpPolSet1);

		LCGrpContSet tLCGrpContSet = new LCGrpContSet();
		tLCGrpContSet.set(mLCGrpContSet);
		mAllLCGrpContSet.add(tLCGrpContSet);

		LCUWMasterSet tLCUWMasterSet = new LCUWMasterSet();
		tLCUWMasterSet.set(mLCUWMasterSet);
		mAllLCUWMasterSet.add(tLCUWMasterSet);

		LCCUWMasterSet tLCCUWMasterSet = new LCCUWMasterSet();
		tLCCUWMasterSet.set(mLCCUWMasterSet);
		mAllLCCUWMasterSet.add(tLCCUWMasterSet);

		LCGUWMasterSet tLCGUWMasterSet1 = new LCGUWMasterSet();
		tLCGUWMasterSet1.set(mLCGUWMasterSet);
		mAllLCGUWMasterSet.add(tLCGUWMasterSet1);

		LCGCUWMasterSet tLCGCUWMasterSet1 = new LCGCUWMasterSet();
		tLCGCUWMasterSet1.set(mLCGCUWMasterSet);
		mAllLCGCUWMasterSet.add(tLCGCUWMasterSet1);

		LCUWSubSet tLCUWSubSet = new LCUWSubSet();
		tLCUWSubSet.set(mLCUWSubSet);
		mAllLCUWSubSet.add(tLCUWSubSet);

		LCCUWSubSet tLCCUWSubSet = new LCCUWSubSet();
		tLCCUWSubSet.set(mLCCUWSubSet);
		mAllLCCUWSubSet.add(tLCCUWSubSet);

		LCGUWSubSet tLCGUWSubSet1 = new LCGUWSubSet();
		tLCGUWSubSet1.set(mLCGUWSubSet);
		mAllLCGUWSubSet.add(tLCGUWSubSet1);

		LCGCUWSubSet tLCGCUWSubSet1 = new LCGCUWSubSet();
		tLCGCUWSubSet1.set(mLCGCUWSubSet);
		mAllLCGCUWSubSet.add(tLCGCUWSubSet1);

		return true;
	}

	/**
	 * 生成新契约退费打印数据
	 * @param tLCGrpIssuePolSchema LCGrpIssuePolSchema
	 * @return boolean
	 */
	private boolean setBackTempFeeLOPRTManager()
	{
		// 判断是否已经收取了暂交费,如果没有只生成打印数据，不生成实付表数据
		if (!checkTempFee(mPrtNo))
		{
			System.out.println("----------setLOPRTManager----------");
			// 3-准备打印数据,生成印刷流水号

			String tLimit = PubFun.getNoLimit(mManageCom);
			String prtSeqNo = "";
			String mCurrentDate = PubFun.getCurrentDate();
			String mCurrentTime = PubFun.getCurrentTime();

			LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();
			prtSeqNo = PubFun1.CreateMaxNo("PRTSEQNO", tLimit);
			mLOPRTManagerSchema.setPrtSeq(prtSeqNo);
			mLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_CONT);
			// 没有暂交费的时候,打印表里面的OtherNo存的是合同号,查询的时候也要做区分.
			mLOPRTManagerSchema.setOtherNo(mLCGrpContSchema.getGrpContNo());
			System.out.println("otherno:" + mLCGrpContSchema.getGrpContNo());
			mLOPRTManagerSchema.setMakeDate(mCurrentDate);
			mLOPRTManagerSchema.setMakeTime(mCurrentTime);
			mLOPRTManagerSchema.setManageCom(mLCGrpContSchema.getManageCom());
			mLOPRTManagerSchema.setAgentCode(mLCGrpContSchema.getAgentCode());
			mLOPRTManagerSchema.setCode(PrintManagerBL.CODE_REFUND);
			mLOPRTManagerSchema.setReqCom(mManageCom);
			mLOPRTManagerSchema.setReqOperator(mOperator);
			mLOPRTManagerSchema.setPrtType("0");
			mLOPRTManagerSchema.setStateFlag("0");
			mLOPRTManagerSchema.setStandbyFlag1(mLCGrpContSchema.getPrtNo());
			// 为了后期打印的时候区分是初核的拒保还是人工核保阶段的拒保在此处作一个标记
			mLOPRTManagerSchema.setStandbyFlag2("PreUW");

			mPrtSeqNo = prtSeqNo;
			mLOPRTManagerSet.add(mLOPRTManagerSchema);
			return true;

		}
		else
		{
			// 生成给付通知书号
			String tLimit = PubFun.getNoLimit(mManageCom);
			mGetNoticeNo = PubFun1.CreateMaxNo("GETNOTICENO", tLimit); // 产生即付通知书号
			mPrtNo = mLCGrpContSchema.getPrtNo();

			StringBuffer tSBql = new StringBuffer(128);
			tSBql.append("select * from ljtempfee where EnterAccDate is not null and trim(otherno) in (select trim(grpcontno) from lcgrpcont where prtno='");
			tSBql.append(mPrtNo);
			tSBql.append("' union select trim(proposalgrpcontno) from lcgrpcont where prtno='");
			tSBql.append(mPrtNo);
			tSBql.append("' union select trim(prtno) from lcgrpcont where prtno='");
			tSBql.append(mPrtNo);
			tSBql.append("' )");
			// System.out.println("strSql=" + strSql);
			outLJTempFeeSet = (new LJTempFeeDB()).executeQuery(tSBql.toString());

			if (outLJTempFeeSet.size() > 0)
			{
				for (int i = 0; i < outLJTempFeeSet.size(); i++)
				{
					LBTempFeeSchema tLBTempFeeSchema = new LBTempFeeSchema();
					mReflections.transFields(tLBTempFeeSchema, outLJTempFeeSet.get(i + 1));
					tLBTempFeeSchema.setBackUpSerialNo(PubFun1.CreateMaxNo("LBTempFee", 20));
					outLBTempFeeSet.add(tLBTempFeeSchema);

					LJTempFeeClassDB tLJTempFeeClassDB = new LJTempFeeClassDB();
					tLJTempFeeClassDB.setTempFeeNo(outLJTempFeeSet.get(i + 1).getTempFeeNo());
					outLJTempFeeClassSet.add(tLJTempFeeClassDB.query());
				}

				for (int i = 0; i < outLJTempFeeClassSet.size(); i++)
				{
					LBTempFeeClassSchema tLBTempFeeClassSchema = new LBTempFeeClassSchema();
					mReflections.transFields(tLBTempFeeClassSchema, outLJTempFeeClassSet.get(i + 1));
					tLBTempFeeClassSchema.setBackUpSerialNo(PubFun1.CreateMaxNo("LBTFClass", 20));
					outLBTempFeeClassSet.add(tLBTempFeeClassSchema);
				}
			}

			if (!returnFee())
			{
				return false;
			}
			return true;
		}

	}

	/**
	 * 数据操作撤单业务处理 输出：如果出错，则返回false,否则返回true
	 * @return boolean
	 */
	private boolean returnFee()
	{
		// System.out.println("============In ReturnFee");

		String payMode = ""; // 交费方式
		String BankCode = ""; // 银行编码
		String BankAccNo = ""; // 银行账号
		String AccName = ""; // 户名

		// 准备TransferData数据

		StringBuffer tSBql = new StringBuffer(128);
		tSBql.append("select * from ljtempfee where trim(otherno) in (select '");
		tSBql.append(mPrtNo);
		tSBql.append("' from dual ) and EnterAccDate is not null and confdate is  null");

		System.out.println(tSBql);
		LJTempFeeDB sLJTempFeeDB = new LJTempFeeDB();
		LJTempFeeSet sLJTempFeeSet = new LJTempFeeSet();
		sLJTempFeeSet = sLJTempFeeDB.executeQuery(tSBql.toString());
		System.out.println("暂交费数量:  " + sLJTempFeeSet.size());
		if (sLJTempFeeSet.size() == 0)
		{
			System.out.println("Out ReturnFee");
			// return true;
		}

		// 如果通知书号不为空，找出退费方式（优先级依次为支票，银行，现金）
		String sql = "select * from ljtempfeeclass where tempfeeno in ";
		sql = sql + "(select tempfeeno from ljtempfee where EnterAccDate is not null and otherno ='" + mPrtNo + "'";
		sql = sql + ")";
		System.out.println(sql);
		LJTempFeeClassDB tLJTempFeeClassDB = new LJTempFeeClassDB();
		LJTempFeeClassSet tLJTempFeeClassSet = tLJTempFeeClassDB.executeQuery(sql);
		if (tLJTempFeeClassSet == null || tLJTempFeeClassSet.size() == 0)
		{
			// mErrors.addOneError("没有找到对应的暂交费分类纪录");
			// return false;
		}
		for (int i = 1; i <= tLJTempFeeClassSet.size(); i++)
		{
			if (tLJTempFeeClassSet.get(i).getPayMode().equals("2")
					|| tLJTempFeeClassSet.get(i).getPayMode().equals("3"))
			{
				payMode = tLJTempFeeClassSet.get(i).getPayMode();
				BankCode = tLJTempFeeClassSet.get(i).getBankCode();
				BankAccNo = tLJTempFeeClassSet.get(i).getChequeNo();
				break;
			}
			if (tLJTempFeeClassSet.get(i).getPayMode().equals("4"))
			{
				payMode = tLJTempFeeClassSet.get(i).getPayMode();
				BankCode = tLJTempFeeClassSet.get(i).getBankCode();
				BankAccNo = tLJTempFeeClassSet.get(i).getBankAccNo();
				AccName = tLJTempFeeClassSet.get(i).getAccName();
			}
			else
			{
				payMode = "1";
			}
		}

		TransferData sTansferData = new TransferData();
		sTansferData.setNameAndValue("PayMode", payMode);
		sTansferData.setNameAndValue("NotBLS", "1");
		if (payMode.equals("1"))
		{
			sTansferData.setNameAndValue("BankFlag", "0");
		}
		else
		{
			sTansferData.setNameAndValue("BankCode", BankCode);
			sTansferData.setNameAndValue("AccNo", BankAccNo);
			sTansferData.setNameAndValue("AccName", AccName);
			sTansferData.setNameAndValue("BankFlag", "1");
		}
		sTansferData.setNameAndValue("GetNoticeNo", mGetNoticeNo);
		// 传输印刷号和拒保函件的条码到GrpNewTempFeeWithdrawBL
		sTansferData.setNameAndValue("PrtNo", mPrtNo);
		sTansferData.setNameAndValue("PrtSeqNo", mPrtSeqNo);

		LJTempFeeSet tLJTempFeeSet = new LJTempFeeSet();
		LJAGetTempFeeSet tLJAGetTempFeeSet = new LJAGetTempFeeSet();

		for (int index = 1; index <= sLJTempFeeSet.size(); index++)
		{
			System.out.println("HaveDate In Second1");
			LJTempFeeSchema tLJTempFeeSchema = new LJTempFeeSchema();
			tLJTempFeeSchema.setTempFeeNo(sLJTempFeeSet.get(index).getTempFeeNo());
			tLJTempFeeSchema.setTempFeeType(sLJTempFeeSet.get(index).getTempFeeType());
			tLJTempFeeSchema.setRiskCode(sLJTempFeeSet.get(index).getRiskCode());
			tLJTempFeeSet.add(tLJTempFeeSchema);

			LJAGetTempFeeSchema tLJAGetTempFeeSchema = new LJAGetTempFeeSchema();
			// tLJAGetTempFeeSchema.setGetReasonCode(mAllLCUWMasterSet.get(1).getUWIdea());
			tLJAGetTempFeeSchema.setGetReasonCode("99");

			tLJAGetTempFeeSet.add(tLJAGetTempFeeSchema);

		}

		// 准备传输数据 VData
		VData tVData = new VData();
		GlobalInput tGlobalInput = new GlobalInput();
		tGlobalInput = this.mGlobalInput;
		tVData.add(tGlobalInput);
		tVData.add(tLJTempFeeSet);
		tVData.add(mLCGrpContSchema);
		tVData.add(tLJAGetTempFeeSet);
		tVData.add(sTansferData);

		// 数据传输
		System.out.println("--------开始传输数据---------");
		GrpNewTempFeeWithdrawBL tGrpNewTempFeeWithdrawBL = new GrpNewTempFeeWithdrawBL();
		tGrpNewTempFeeWithdrawBL.submitData(tVData, "INSERT");

		if (tGrpNewTempFeeWithdrawBL.mErrors.needDealError())
		{
			// @@错误处理
			this.mErrors.copyAllErrors(tGrpNewTempFeeWithdrawBL.mErrors);
			CError tError = new CError();
			tError.moduleName = "GrpQuestInputChkBL";
			tError.functionName = "submitData";
			tError.errorMessage = "生成退费数据失败!";
			this.mErrors.addOneError(tError);
			return false;
		}
		mReturnFeeData = tGrpNewTempFeeWithdrawBL.getResult();

		mmMap = (MMap) mReturnFeeData.getObjectByObjectName("MMap", 0);

		System.out.println("----------------------Out ReturnFee--------------------");
		return true;
	}

	/**
	 * 检查是否有暂交费 输出：如果没有，则返回false,否则返回true
	 * @return boolean
	 */

	private boolean checkTempFee(String tPrtNo)
	{
		StringBuffer tSBql = new StringBuffer(128);
		tSBql.append("select * from ljtempfee where trim(otherno) in (select '");
		tSBql.append(tPrtNo);
		tSBql.append("' from dual ) and EnterAccDate is not null and confdate is  null");

		System.out.println(tSBql);
		LJTempFeeDB sLJTempFeeDB = new LJTempFeeDB();
		LJTempFeeSet sLJTempFeeSet = new LJTempFeeSet();
		sLJTempFeeSet = sLJTempFeeDB.executeQuery(tSBql.toString());
		System.out.println("暂交费数量:  " + sLJTempFeeSet.size());
		if (sLJTempFeeSet.size() == 0)
		{
			System.out.println("----：No ReturnFee");
			return false;
		}
		else
		{
			return true;
		}

	}

	/**
	 * 生成拒保打印数据
	 * @param tLCGrpIssuePolSchema LCGrpIssuePolSchema
	 * @return boolean
	 */
	private boolean setLOPRTManager()
	{
		System.out.println("----------setLOPRTManager----------");
		// 3-准备打印数据,生成印刷流水号
		VData tVData = new VData();
		String tLimit = PubFun.getNoLimit(mManageCom);
		String prtSeqNo = PubFun1.CreateMaxNo("HEBAOHAN", tLimit, tVData);

		String mCurrentDate = PubFun.getCurrentDate();
		String mCurrentTime = PubFun.getCurrentTime();

		LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();

		mLOPRTManagerSchema.setPrtSeq(prtSeqNo);
		mLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_CONT);
		mLOPRTManagerSchema.setOtherNo(mLCGrpContSchema.getGrpContNo());
		System.out.println("otherno:" + mLCGrpContSchema.getGrpContNo());
		mLOPRTManagerSchema.setMakeDate(mCurrentDate);
		mLOPRTManagerSchema.setMakeTime(mCurrentTime);
		mLOPRTManagerSchema.setManageCom(mLCGrpContSchema.getManageCom());
		mLOPRTManagerSchema.setAgentCode(mLCGrpContSchema.getAgentCode());
		mLOPRTManagerSchema.setCode(PrintManagerBL.CODE_GRP_DECLINE);
		mLOPRTManagerSchema.setReqCom(mManageCom);
		mLOPRTManagerSchema.setReqOperator(mOperator);
		mLOPRTManagerSchema.setPrtType("0");
		mLOPRTManagerSchema.setStateFlag("0");
		mLOPRTManagerSchema.setStandbyFlag1(mLCGrpContSchema.getPrtNo());
		// 为了后期打印的时候区分是初核的拒保还是人工核保阶段的拒保在此处作一个标记
		mLOPRTManagerSchema.setStandbyFlag2("UW");

		mLOPRTManagerSet.add(mLOPRTManagerSchema);
		return true;
	}

	/**
	 * 准备核保信息 输出：如果发生错误则返回false,否则返回true
	 * @return boolean
	 */
	private boolean TimeAccept()
	{
		Date temp;
		String temp1 = "D";
		Date temp2;

		FDate tFDate = new FDate();
		temp = null;
		temp2 = tFDate.getDate(mLCPolSchema.getCValiDate());

		mvalidate = PubFun.calDate(temp2, mpostday, temp1, temp);

		System.out.println("---TimeAccept -- 延期 ---");
		mLCPolSchema.setCValiDate(mvalidate);
		System.out.println("---mvalidate---" + mvalidate);

		return true;
	}

	/**
	 * 准备核保信息 输出：如果发生错误则返回false,否则返回true
	 * @return boolean
	 */
	private boolean prepareUW()
	{
		LCCUWMasterSchema tLCCUWMasterSchema = new LCCUWMasterSchema();
		LCCUWMasterDB tLCCUWMasterDB = new LCCUWMasterDB();
		tLCCUWMasterDB.setGrpContNo(mGrpContNo);
		LCCUWMasterSet tLCCUWMasterSet = new LCCUWMasterSet();
		tLCCUWMasterSet = tLCCUWMasterDB.query();
		if (tLCCUWMasterDB.mErrors.needDealError())
		{
			// @@错误处理
			this.mErrors.copyAllErrors(tLCCUWMasterDB.mErrors);
			CError tError = new CError();
			tError.moduleName = "GrpUWManuNormChkBL";
			tError.functionName = "prepareUW";
			tError.errorMessage = "核保总表取数失败!";
			this.mErrors.addOneError(tError);
			return false;
		}

		int n = tLCCUWMasterSet.size();
		int i = 0;
		System.out.println("mastercount=" + n);

		if (n <= 0)
		{
			// @@错误处理
			this.mErrors.copyAllErrors(tLCCUWMasterDB.mErrors);
			CError tError = new CError();
			tError.moduleName = "GrpUWManuNormChkBL";
			tError.functionName = "prepareUW";
			tError.errorMessage = "合同单核保总表取数据失败!";
			this.mErrors.addOneError(tError);
			return false;
		}
		for (i = 1; i <= n; i++)
		{
			tLCCUWMasterSchema = tLCCUWMasterSet.get(i);
			int uwno = tLCCUWMasterSet.get(i).getUWNo();
			uwno++;
			tLCCUWMasterSchema.setPassFlag(mUWFlag); // 通过标志
			tLCCUWMasterSchema.setAutoUWFlag("2"); // 1 自动核保 2 人工核保
			tLCCUWMasterSchema.setUWGrade(mUWPopedom);
			tLCCUWMasterSchema.setAppGrade(mAppGrade);
			tLCCUWMasterSchema.setUWNo(uwno);
			tLCCUWMasterSchema.setState(mUWFlag);
			tLCCUWMasterSchema.setUWIdea(mUWIdea);
			tLCCUWMasterSchema.setOperator(mOperator); // 操作员
			tLCCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
			tLCCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());

			mLCCUWMasterSet.clear();
			mLCCUWMasterSet.add(tLCCUWMasterSchema);

			// 核保轨迹表
			LCCUWSubSchema tLCCUWSubSchema = new LCCUWSubSchema();
			LCCUWSubDB tLCCUWSubDB = new LCCUWSubDB();
			tLCCUWSubDB.setContNo(tLCCUWMasterSchema.getContNo());
			LCCUWSubSet tLCCUWSubSet = new LCCUWSubSet();
			tLCCUWSubSet = tLCCUWSubDB.query();
			if (tLCCUWSubDB.mErrors.needDealError())
			{
				// @@错误处理
				this.mErrors.copyAllErrors(tLCCUWSubDB.mErrors);
				CError tError = new CError();
				tError.moduleName = "GrpUWManuNormChkBl";
				tError.functionName = "prepareUW";
				tError.errorMessage = "核保轨迹表查询失败!";
				this.mErrors.addOneError(tError);
				return false;
			}

			int m = tLCCUWSubSet.size();
			if (m >= 0)
			{
				m++; // 核保次数
				tLCCUWSubSchema = new LCCUWSubSchema(); // tLCCUWSubSet.get(1);

				//Amended By Fang(20110524)
				//tLCCUWSubSchema.setUWNo(m); // 第几次核保
				tLCCUWSubSchema.setUWNo(tLCCUWMasterSchema.getUWNo());
				//Ended(20110524)
				tLCCUWSubSchema.setContNo(tLCCUWMasterSchema.getContNo());
				tLCCUWSubSchema.setGrpContNo(tLCCUWMasterSchema.getGrpContNo());
				tLCCUWSubSchema.setProposalContNo(tLCCUWMasterSchema.getProposalContNo());
				tLCCUWSubSchema.setInsuredNo(tLCCUWMasterSchema.getInsuredNo());
				tLCCUWSubSchema.setInsuredName(tLCCUWMasterSchema.getInsuredName());
				tLCCUWSubSchema.setAppntNo(tLCCUWMasterSchema.getAppntNo());
				tLCCUWSubSchema.setAppntName(tLCCUWMasterSchema.getAppntName());
				tLCCUWSubSchema.setAgentCode(tLCCUWMasterSchema.getAgentCode());
				tLCCUWSubSchema.setAgentGroup(tLCCUWMasterSchema.getAgentGroup());
				tLCCUWSubSchema.setUWGrade(tLCCUWMasterSchema.getUWGrade()); // 核保级别
				tLCCUWSubSchema.setAppGrade(tLCCUWMasterSchema.getAppGrade()); // 申请级别
				tLCCUWSubSchema.setAutoUWFlag(tLCCUWMasterSchema.getAutoUWFlag());
				tLCCUWSubSchema.setState(tLCCUWMasterSchema.getState());
				tLCCUWSubSchema.setPassFlag(tLCCUWMasterSchema.getState());
				tLCCUWSubSchema.setPostponeDay(tLCCUWMasterSchema.getPostponeDay());
				tLCCUWSubSchema.setPostponeDate(tLCCUWMasterSchema.getPostponeDate());
				tLCCUWSubSchema.setUpReportContent(tLCCUWMasterSchema.getUpReportContent());
				tLCCUWSubSchema.setHealthFlag(tLCCUWMasterSchema.getHealthFlag());
				tLCCUWSubSchema.setSpecFlag(tLCCUWMasterSchema.getSpecFlag());
				tLCCUWSubSchema.setSpecReason(tLCCUWMasterSchema.getSpecReason());
				tLCCUWSubSchema.setQuesFlag(tLCCUWMasterSchema.getQuesFlag());
				tLCCUWSubSchema.setReportFlag(tLCCUWMasterSchema.getReportFlag());
				tLCCUWSubSchema.setChangePolFlag(tLCCUWMasterSchema.getChangePolFlag());
				tLCCUWSubSchema.setChangePolReason(tLCCUWMasterSchema.getChangePolReason());
				tLCCUWSubSchema.setAddPremReason(tLCCUWMasterSchema.getAddPremReason());
				tLCCUWSubSchema.setPrintFlag(tLCCUWMasterSchema.getPrintFlag());
				tLCCUWSubSchema.setPrintFlag2(tLCCUWMasterSchema.getPrintFlag2());
				tLCCUWSubSchema.setUWIdea(tLCCUWMasterSchema.getUWIdea());
				tLCCUWSubSchema.setOperator(tLCCUWMasterSchema.getOperator()); // 操作员
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
				tError.moduleName = "GrpUWManuNormChkBL";
				tError.functionName = "prepareUW";
				tError.errorMessage = "核保轨迹表查询失败!";
				this.mErrors.addOneError(tError);
				return false;
			}
			mLCCUWSubSet.clear();
			mLCCUWSubSet.add(tLCCUWSubSchema);
		}

		LCUWMasterSchema tLCUWMasterSchema = new LCUWMasterSchema();
		LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
		tLCUWMasterDB.setGrpContNo(mGrpContNo);
		LCUWMasterSet tLCUWMasterSet = new LCUWMasterSet();
		tLCUWMasterSet = tLCUWMasterDB.query();
		if (tLCUWMasterDB.mErrors.needDealError())
		{
			// @@错误处理
			this.mErrors.copyAllErrors(tLCUWMasterDB.mErrors);
			CError tError = new CError();
			tError.moduleName = "GrpUWManuNormChkBL";
			tError.functionName = "prepareUW";
			tError.errorMessage = "核保总表取数失败!";
			this.mErrors.addOneError(tError);
			return false;
		}

		n = tLCUWMasterSet.size();
		i = 0;
		System.out.println("mastercount=" + n);

		if (n <= 0)
		{
			// @@错误处理
			this.mErrors.copyAllErrors(tLCUWMasterDB.mErrors);
			CError tError = new CError();
			tError.moduleName = "GrpUWManuNormChkBL";
			tError.functionName = "prepareUW";
			tError.errorMessage = "核保总表取数据不唯一!";
			this.mErrors.addOneError(tError);
			return false;
		}
		mLCUWMasterSet.clear();
		mLCUWSubSet.clear();
		for (i = 1; i <= n; i++)
		{
			tLCUWMasterSchema = tLCUWMasterSet.get(i);
			int uwno = tLCUWMasterSet.get(i).getUWNo();
			uwno++;
			tLCUWMasterSchema.setPassFlag(mUWFlag); // 通过标志
			tLCUWMasterSchema.setAutoUWFlag("2"); // 1 自动核保 2 人工核保
			tLCUWMasterSchema.setUWGrade(mUWPopedom);
			tLCUWMasterSchema.setAppGrade(mAppGrade);
			tLCUWMasterSchema.setUWNo(uwno);
			tLCUWMasterSchema.setState(mUWFlag);
			tLCUWMasterSchema.setUWIdea(mUWIdea);
			tLCUWMasterSchema.setOperator(mOperator); // 操作员
			tLCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
			tLCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());
			mLCUWMasterSet.add(tLCUWMasterSchema);

			// 核保轨迹表
			LCUWSubSchema tLCUWSubSchema = new LCUWSubSchema();
			LCUWSubDB tLCUWSubDB = new LCUWSubDB();
			tLCUWSubDB.setPolNo(tLCUWMasterSchema.getPolNo());
			LCUWSubSet tLCUWSubSet = new LCUWSubSet();
			tLCUWSubSet = tLCUWSubDB.query();
			if (tLCUWSubDB.mErrors.needDealError())
			{
				// @@错误处理
				this.mErrors.copyAllErrors(tLCUWSubDB.mErrors);
				CError tError = new CError();
				tError.moduleName = "GrpUWManuNormChkBl";
				tError.functionName = "prepareUW";
				tError.errorMessage = "核保轨迹表查询失败!";
				this.mErrors.addOneError(tError);
				return false;
			}

			int m = tLCUWSubSet.size();
			if (m >= 0)
			{
				m++; // 核保次数
				tLCUWSubSchema = new LCUWSubSchema(); // tLCUWSubSet.get(1);

				//Amended By Fang(20110524)
				//tLCUWSubSchema.setUWNo(m); // 第几次核保
				tLCUWSubSchema.setUWNo(tLCUWMasterSchema.getUWNo());
				//End(20110524)
				tLCUWSubSchema.setContNo(tLCUWMasterSchema.getContNo());
				tLCUWSubSchema.setPolNo(tLCUWMasterSchema.getPolNo());
				tLCUWSubSchema.setGrpContNo(tLCUWMasterSchema.getGrpContNo());
				tLCUWSubSchema.setProposalContNo(tLCUWMasterSchema.getProposalContNo());
				tLCUWSubSchema.setProposalNo(tLCUWMasterSchema.getProposalNo());
				tLCUWSubSchema.setInsuredNo(tLCUWMasterSchema.getInsuredNo());
				tLCUWSubSchema.setInsuredName(tLCUWMasterSchema.getInsuredName());
				tLCUWSubSchema.setAppntNo(tLCUWMasterSchema.getAppntNo());
				tLCUWSubSchema.setAppntName(tLCUWMasterSchema.getAppntName());
				tLCUWSubSchema.setAgentCode(tLCUWMasterSchema.getAgentCode());
				tLCUWSubSchema.setAgentGroup(tLCUWMasterSchema.getAgentGroup());
				tLCUWSubSchema.setUWGrade(tLCUWMasterSchema.getUWGrade()); // 核保级别
				tLCUWSubSchema.setAppGrade(tLCUWMasterSchema.getAppGrade()); // 申请级别
				tLCUWSubSchema.setAutoUWFlag(tLCUWMasterSchema.getAutoUWFlag());
				tLCUWSubSchema.setState(tLCUWMasterSchema.getState());
				tLCUWSubSchema.setPassFlag(tLCUWMasterSchema.getState());
				tLCUWSubSchema.setPostponeDay(tLCUWMasterSchema.getPostponeDay());
				tLCUWSubSchema.setPostponeDate(tLCUWMasterSchema.getPostponeDate());
				tLCUWSubSchema.setUpReportContent(tLCUWMasterSchema.getUpReportContent());
				tLCUWSubSchema.setHealthFlag(tLCUWMasterSchema.getHealthFlag());
				tLCUWSubSchema.setSpecFlag(tLCUWMasterSchema.getSpecFlag());
				tLCUWSubSchema.setSpecReason(tLCUWMasterSchema.getSpecReason());
				tLCUWSubSchema.setQuesFlag(tLCUWMasterSchema.getQuesFlag());
				tLCUWSubSchema.setReportFlag(tLCUWMasterSchema.getReportFlag());
				tLCUWSubSchema.setChangePolFlag(tLCUWMasterSchema.getChangePolFlag());
				tLCUWSubSchema.setChangePolReason(tLCUWMasterSchema.getChangePolReason());
				tLCUWSubSchema.setAddPremReason(tLCUWMasterSchema.getAddPremReason());
				tLCUWSubSchema.setPrintFlag(tLCUWMasterSchema.getPrintFlag());
				tLCUWSubSchema.setPrintFlag2(tLCUWMasterSchema.getPrintFlag2());
				tLCUWSubSchema.setUWIdea(tLCUWMasterSchema.getUWIdea());
				tLCUWSubSchema.setOperator(tLCUWMasterSchema.getOperator()); // 操作员
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
				tError.moduleName = "GrpUWManuNormChkBL";
				tError.functionName = "prepareUW";
				tError.errorMessage = "核保轨迹表查询失败!";
				this.mErrors.addOneError(tError);
				return false;
			}
			mLCUWSubSet.add(tLCUWSubSchema);
		}

		return true;
	}

	/**
	 * 准备团体保单信息 输出：如果发生错误则返回false,否则返回true
	 * @return boolean
	 */
	private boolean preparePol()
	{
		/*
		 * if(mUWFlag.equals("7"))//(sxy-add
		 * 2003-08-19)-判断下核保通知书时该团体单下的主险团体单下是否有问题件针对各种问题件置该团体单的状态 {
		 * //发放问题件时,只有操作员的问题件,该团体单将处于复核修改处(即置ApproveFlag=1) String tStrSql =
		 * "select * from lcissuepol where proposalno='" + mmGrpContNo +"'" + "
		 * and replyman is null " + " and backobjtype='1' " + " and (select
		 * count(*) from lcissuepol where proposalno='" +mmGrpContNo+"' and
		 * replyman is null and backobjtype not in ('1'))=0 "; LCIssuePolDB
		 * tLCIssuePolDB = new LCIssuePolDB(); LCIssuePolSet tLCIssuePolSet =
		 * new LCIssuePolSet(); tLCIssuePolSet =
		 * tLCIssuePolDB.executeQuery(tStrSql); if(tLCIssuePolSet.size()>0) {
		 * mLCGrpContSchema.setApproveFlag("1"); }
		 * //发放问题件时,不只有操作员的问题件,该团体单将处于问题修改或管理中心处(即置ApproveFlag=2) tStrSql =
		 * "select * from lcissuepol where proposalno='" + mmGrpContNo +"'" + "
		 * and replyman is null " + " and (select count(*) from lcissuepol where
		 * proposalno='" + mmGrpContNo +"' and replyman is null and backobjtype
		 * not in ('1'))>0 "; tLCIssuePolSet =
		 * tLCIssuePolDB.executeQuery(tStrSql); if(tLCIssuePolSet.size()>0) {
		 * mLCGrpContSchema.setApproveFlag("2"); } }
		 */

		if (mUWFlag.equals("1") || mUWFlag.equals("6") || mUWFlag.equals("a") || mUWFlag.equals("7")
				|| mUWFlag.equals("8"))
		{
			preparePPol();
		}

		return true;
	}

	/**
	 * 个人保单
	 * @return boolean
	 */
	private boolean preparePPol()
	{
		//ASR20107109 大单跳过人工核保  
        LDCodeDB codeDB = new LDCodeDB();
        LDCodeSet codeSet = new LDCodeSet();
        codeDB.setCodeType("specialpolicy");
        codeDB.setCode(mLCGrpContSchema.getPrtNo());
        codeSet = codeDB.query();
        if (codeSet.size() > 0){
      	  	return true;
        }
		
		LCContDB tLCContDB = new LCContDB();
		LCContSet tLCContSet = new LCContSet();

		tLCContDB.setGrpContNo(mGrpContNo);
		tLCContSet = tLCContDB.query();
		int n = 0;
		int i = 0;
		if (tLCContSet != null)
		{
			n = tLCContSet.size();
			LCContSchema tLCContSchema = null;
			for (i = 1; i <= n; i++)
			{
				tLCContSchema = tLCContSet.get(i);
//				tLCContSchema.setUWFlag(mUWFlag);
				tLCContSchema.setUWOperator(mOperator);
				tLCContSchema.setUWDate(PubFun.getCurrentDate());
				tLCContSchema.setOperator(mOperator);
				tLCContSchema.setModifyDate(PubFun.getCurrentDate());
				tLCContSchema.setModifyTime(PubFun.getCurrentTime());
				mLCContSet.add(tLCContSchema);
			}
		}

		LCPolDB tLCPolDB = new LCPolDB();
		LCPolSet tLCPolSet = new LCPolSet();
		tLCPolDB.setGrpContNo(mLCGrpContSchema.getGrpContNo());
		tLCPolSet = tLCPolDB.query();

		if (tLCPolSet.size() > 0)
		{
			n = tLCPolSet.size();
			LCPolSchema tLCPolSchema = null;
			
			//只对核保结论为4或9时，才存储GIL和保额差值字段
//			2007.3.16中意新增加的对GIL值的处理
			
			if(mUWFlag.equals("4")||mUWFlag.equals("9")) {
				            	
				for (i = 1; i <= n; i++){
					tLCPolSchema = tLCPolSet.get(i);
					
					LCGrpPolSchema tLCGrpPolSchema = new LCGrpPolSchema();
					
					for(int j = 1;j<=mLCGrpPolSet.size();j++){
						//对有GIL的才回去算差额
						if(getIfORGIL(mLCGrpPolSet.get(j).getRiskCode())){
							
						}else{
							tLCGrpPolSchema = mLCGrpPolSet.get(j);
							if(tLCPolSchema.getGrpPolNo().equals(tLCGrpPolSchema.getGrpPolNo())) {
								//对于只有GIL的值存在才进行算差值
								if(tLCGrpPolSchema.getGIL()>0) {
									//chenwm 20070817 以前算法不准确 没有考虑到无名单的人数 和 GIL调小后再调大的问题
									double subGIL = tLCPolSchema.getAmnt()/tLCPolSchema.getInsuredPeoples()+tLCPolSchema.getSubGIL()
													-tLCGrpPolSchema.getGIL();
									//如果差值小于0的话,则存入0
									if(subGIL>0) {
										tLCPolSchema.setSubGIL(subGIL); //只对有关有GIL值的个人险种来保存GIL和保额差额字段
									}else {
										tLCPolSchema.setSubGIL(0); 
									}
								}else {
									tLCPolSchema.setSubGIL(0); 
								}
							}
						}
					}						

//					tLCPolSchema.setUWFlag(mUWFlag);
//					tLCPolSchema.setUWCode(mOperator);
					tLCPolSchema.setUWDate(PubFun.getCurrentDate());
					tLCPolSchema.setOperator(mOperator);
					tLCPolSchema.setModifyDate(PubFun.getCurrentDate());
					tLCPolSchema.setModifyTime(PubFun.getCurrentTime());
					
//					System.out.println("--------------------"+tLCPolSchema.getSubGIL());
					mLCPolSet.add(tLCPolSchema);
				}
			}else {
				for (i = 1; i <= n; i++){
					tLCPolSchema = tLCPolSet.get(i);
//					tLCPolSchema.setUWFlag(mUWFlag);
//					tLCPolSchema.setUWCode(mOperator);
					tLCPolSchema.setUWDate(PubFun.getCurrentDate());
					tLCPolSchema.setOperator(mOperator);
					tLCPolSchema.setModifyDate(PubFun.getCurrentDate());
					tLCPolSchema.setModifyTime(PubFun.getCurrentTime());
					mLCPolSet.add(tLCPolSchema);
				}
			}
			
		}
		return true;
	}

	/**
	 * 准备核保信息 输出：如果发生错误则返回false,否则返回true
	 * @return boolean
	 */
	private boolean CondAccept()
	{
		int n;
		int i;
		int max;
		String sql;

		System.out.println("---CondAccept---");

		n = 0;
		n = mLCSpecSet.size();
		if (n > 0)
		{
			LCSpecSchema tLCSpecSchema = new LCSpecSchema();
			LCSpecDB tLCSpecDB = new LCSpecDB();

			sql = "select * from LCSpec where specno = (select max(specno) from LCSpec)";
			LCSpecSet tLCSpecSet = tLCSpecDB.executeQuery(sql);
			tLCSpecSchema = tLCSpecSet.get(1);

			// 生成流水号码
			// specno = tLCSpecSchema.getSpecNo();
			// max = String.valueOf(specno);
			// max = max + 1;
			// specno =

			tLCSpecSchema = mLCSpecSet.get(1);

			// tLCSpecSchema.setSpecNo(PubFun1.CreateMaxNo("SpecNo",PubFun.getNoLimit(mGlobalInput.ComCode)));
			tLCSpecSchema.setPolNo(mPolNo);
			System.out.println("specpolno=" + mPolNo);
			// tLCSpecSchema.setPolType("1");
			tLCSpecSchema.setEndorsementNo("");
			tLCSpecSchema.setSpecType("");
			tLCSpecSchema.setSpecCode("");
			// tLCSpecSchema.setSpecContent();
			tLCSpecSchema.setPrtFlag("1");
			tLCSpecSchema.setBackupType("");
			tLCSpecSchema.setOperator(mOperator);
			tLCSpecSchema.setMakeDate(PubFun.getCurrentDate());
			tLCSpecSchema.setMakeTime(PubFun.getCurrentTime());
			tLCSpecSchema.setModifyDate(PubFun.getCurrentDate());
			tLCSpecSchema.setModifyTime(PubFun.getCurrentTime());

			mLCSpecSet.clear();
			mLCSpecSet.add(tLCSpecSchema);
		}

		if ((n = mLCPremSet.size()) > 0)
		{
			System.out.println("premsize=" + n);
			for (i = 1; i < n; i++)
			{
				;
			}
			{
				LCPremSchema ttLCPremSchema = mLCPremSet.get(i);
				LCPremSchema tLCPremSchema = new LCPremSchema();
				LCPremDB tLCPremDB = new LCPremDB();
				double tPrem;

				tLCPremDB.setPolNo(mLCPolSchema.getPolNo());
				tLCPremDB.setDutyCode(tLCPremSchema.getDutyCode());

				LCPremSet tLCPremSet = tLCPremDB.query();
				tLCPremSchema = tLCPremSet.get(1);

				sql = "select * from lcprem where payplancode = (select max(payplancode) from lcprem where payplancode like '000000%') and polno = "
						+ mLCPolSchema.getPolNo().trim();
				LCPremSet ttLCPremSet = tLCPremDB.executeQuery(sql);
				// String tPayPlanCode = "";
				String PayPlanCode = "";

				if (ttLCPremSet.size() > 0)
				{
					LCPremSchema tttLCPremSchema = ttLCPremSet.get(1);

					// 生成流水号码

					PayPlanCode = tttLCPremSchema.getPayPlanCode();

					if (PayPlanCode.length() > 0)
					{
						int j = 0;
						max = Integer.parseInt(PayPlanCode);
						max += 1;
						PayPlanCode = String.valueOf(max);
						for (j = PayPlanCode.length(); j < 8; j++)
						{
							PayPlanCode = "0" + PayPlanCode;
						}
					}
				}
				else
				{
					PayPlanCode = "00000001";
				}

				System.out.println("payplancode" + PayPlanCode);
				// 保单总保费
				tPrem = mLCPolSchema.getPrem() + ttLCPremSchema.getPrem();
				// tLCPremSchema.setPolNo(mLCPolSchema.getPolNo());
				// tLCPremSchema.setDutyCode(mmaxDutyCode);
				tLCPremSchema.setPayPlanCode(PayPlanCode);
				// tLCPremSchema.setGrpContNo(mLCPolSchema.get);
				// tLCPremSchema.setPayPlanType();
				// tLCPremSchema.setPayTimes();
				// tLCPremSchema.setPayIntv();
				// tLCPremSchema.setMult();
				// tLCPremSchema.setStandPrem();
				tLCPremSchema.setPrem(ttLCPremSchema.getPrem());
				// tLCPremSchema.setSumPrem();
				// tLCPremSchema.setRate();
				tLCPremSchema.setPayStartDate(ttLCPremSchema.getPayStartDate());
				tLCPremSchema.setPayEndDate(ttLCPremSchema.getPayEndDate());
				// tLCPremSchema.setPaytoDate();
				// tLCPremSchema.setState();
				// tLCPremSchema.setBankCode();
				// tLCPremSchema.setBankAccNo();
				// tLCPremSchema.setAppntNo();
				// tLCPremSchema.setAppntType("1"); //投保人类型
				tLCPremSchema.setModifyDate(PubFun.getCurrentDate());
				tLCPremSchema.setModifyTime(PubFun.getCurrentTime());

				mmLCPremSet.add(tLCPremSchema);

				// 更新保单数据
				mLCPolSchema.setPrem(tPrem);

			}
		}
		return true;
	}

	/**
	 * 待上级核保 输出：如果发生错误则返回false,否则返回true
	 */
	private void uplevel()
	{
		LCGCUWErrorDB tLCGCUWErrorDB = new LCGCUWErrorDB();
		LCGCUWMasterDB tLCGCUWMasterDB = new LCGCUWMasterDB();
		LCGCUWMasterSchema tLCGCUWMasterSchema = new LCGCUWMasterSchema();

		tLCGCUWErrorDB.setGrpContNo(mLCGrpContSchema.getGrpContNo());
		tLCGCUWMasterDB.setProposalGrpContNo(mLCGrpContSchema.getGrpContNo());

		if (tLCGCUWMasterDB.getInfo())
		{
			tLCGCUWMasterSchema = tLCGCUWMasterDB.getSchema();
		}

		String tcurrgrade = "";

		if (tLCGCUWMasterSchema.getAppGrade() == null)
		{
			tcurrgrade = "A";
		}
		else
		{
			tcurrgrade = tLCGCUWMasterSchema.getAppGrade();
		}

		String tpolno = mLCGrpContSchema.getGrpContNo();
		String tsql = "select * from LCGCUWerror where GrpContNo = '" + tpolno.trim()
				+ "' and uwno = (select max(uwno) from LCGCUWerror where GrpContNo = '" + tpolno.trim() + "')";
		LCGCUWErrorSet tLCGCUWErrorSet = tLCGCUWErrorDB.executeQuery(tsql);

		int errno = tLCGCUWErrorSet.size();
		if (errno > 0)
		{
			for (int i = 1; i <= errno; i++)
			{
				LCGCUWErrorSchema tLCGCUWErrorSchema = new LCGCUWErrorSchema();
				tLCGCUWErrorSchema = tLCGCUWErrorSet.get(i);
				String terrgrade = tLCGCUWErrorSchema.getUWGrade();
				if (terrgrade.compareTo(tcurrgrade) > 0)
				{
					tcurrgrade = terrgrade;
				}
			}
		}

		mAppGrade = tcurrgrade;

		// 与当前核保员级别校验
		if ((mUWPopedom.compareTo(mAppGrade) >= 0 && mUWPopedom.compareTo("L") < 0))
		{
			char temp[];
			char tempgrade;
			temp = mUWPopedom.toCharArray();
			tempgrade = (char) ((int) temp[0] + 1);
			System.out.println("上报级别:" + tempgrade);
			mAppGrade = String.valueOf(tempgrade);
		}
	}

	/**
	 * 从输入数据中得到所有对象 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
	 * @param cInputData VData
	 * @return boolean
	 */
	private boolean getInputData(VData cInputData)
	{
		mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
		mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);
		mOperator = mGlobalInput.Operator;
		mManageCom = mGlobalInput.ManageCom;

		// 取投保单
		mLCGrpContSet.set((LCGrpContSet) cInputData.getObjectByObjectName("LCGrpContSet", 0));

		int n = mLCGrpContSet.size();
		if (n == 1)
		{
			LCGrpContSchema tLCGrpContSchema = mLCGrpContSet.get(1);
			LCGrpContDB tLCGrpContDB = new LCGrpContDB();

			mGrpContNo = tLCGrpContSchema.getGrpContNo();
			// mPrtNo = tLCGrpContSchema.getPrtNo();
			mUWIdea = tLCGrpContSchema.getRemark();
			mUWFlag = tLCGrpContSchema.getUWFlag();
			System.out.println("muwflag=" + mUWFlag);

			// 校验是不是以下核保结论
			if (mUWFlag == null)
			{
				// @@错误处理
				CError tError = new CError();
				tError.moduleName = "GrpUWManuNormChkBL";
				tError.functionName = "getInputData";
				tError.errorMessage = "没有选择核保结论";
				this.mErrors.addOneError(tError);
				return false;
			}

			if (mUWFlag.equals(""))
			{
				// @@错误处理
				CError tError = new CError();
				tError.moduleName = "GrpUWManuNormChkBL";
				tError.functionName = "getInputData";
				tError.errorMessage = "没有选择核保结论";
				this.mErrors.addOneError(tError);
				return false;
			}

			tLCGrpContDB.setGrpContNo(mGrpContNo);
			if (tLCGrpContDB.getInfo())
			{
				tLCGrpContSchema.setSchema(tLCGrpContDB);
				mLCGrpContSchema.setSchema(tLCGrpContDB);
				mPrtNo = tLCGrpContSchema.getPrtNo();
				// mLCGrpContSet.add(tLCGrpContSchema);
			}
			else
			{
				// @@错误处理
				this.mErrors.copyAllErrors(tLCGrpContDB.mErrors);
				CError tError = new CError();
				tError.moduleName = "GrpUWManuNormChkBL";
				tError.functionName = "getInputData";
				tError.errorMessage = mGrpContNo + "集体合同单查询失败!";
				this.mErrors.addOneError(tError);
				return false;
			}
			/*
			 * if (mUWFlag.equals("3")) {
			 * mLCPremSet.set((LCPremSet)cInputData.getObjectByObjectName("LCPremSet",0));
			 * n = mLCPremSet.size(); if (n > 0) { }
			 * mLCSpecSet.set((LCSpecSet)cInputData.getObjectByObjectName("LCSpecSet",0));
			 * n = mLCSpecSet.size(); if (n == 1) { } if
			 * (getflag.equals("false")) { CError tError = new CError();
			 * tError.moduleName = "GrpUWManuNormChkBL"; tError.functionName =
			 * "getInputData"; tError.errorMessage = "条件承保数据传输失败"; this.mErrors
			 * .addOneError(tError); return false; } }
			 */

			LCGCUWMasterDB tLCGCUWMasterDB = new LCGCUWMasterDB();
			tLCGCUWMasterDB.setGrpContNo(tLCGrpContSchema.getGrpContNo());
			tLCGCUWMasterDB.setProposalGrpContNo(tLCGrpContSchema.getGrpContNo());
			System.out.println("--BL--Master--" + tLCGrpContSchema.getGrpContNo());
			if (!tLCGCUWMasterDB.getInfo())
			{
				// @@错误处理
				this.mErrors.copyAllErrors(tLCGCUWMasterDB.mErrors);
				CError tError = new CError();
				tError.moduleName = "GrpUWManuNormChkBL";
				tError.functionName = "getInputData";
				tError.errorMessage = mGrpContNo + "集体核保总表查询失败!";
				this.mErrors.addOneError(tError);
				return false;
			}
		}
		else
		{
			return false;
		}
		return true;
	}

	/**
	 * 准备返回前台统一存储数据 输出：如果发生错误则返回false,否则返回true
	 * @return boolean
	 */
	private boolean prepareOutputData()
	{
		mResult.clear();
		MMap map = new MMap();
		
		if(!tsign ) {
			map.put(mAllLCPolSet, "UPDATE");
			map.put(mAllLCContSet, "UPDATE");
			map.put(mAllLCGrpContSet, "UPDATE");
			map.put(mAllLCGrpPolSet, "UPDATE");
		}else {
			ExeSQL mExeSQL = new ExeSQL();
			String SQL = "";
			SQL = "update lcgrpcont set uwflag ='"+mLCGrpContSchema.getUWFlag()+"' where grpcontno='"+mLCGrpContSchema.getGrpContNo()+"'";
			if(!mExeSQL.execUpdateSQL(SQL)){
				CError tError = new CError();
				tError.moduleName = "GrpUWManuNormChkAfterInitService";
				tError.functionName = "prepareOutputData";
				tError.errorMessage = "执行更新语句失败！";
				this.mErrors.addOneError(tError);
				return false;
			}
			for(int i=1;i<=mLCPolSet.size();i++) {
				SQL = "update lcpol set uwflag='"+mLCPolSet.get(i).getUWFlag()+"' where polno='"+mLCPolSet.get(i).getPolNo()+"'";
				if(!mExeSQL.execUpdateSQL(SQL)){
					CError tError = new CError();
					tError.moduleName = "GrpUWManuNormChkAfterInitService";
					tError.functionName = "prepareOutputData";
					tError.errorMessage = "执行更新语句失败！";
					this.mErrors.addOneError(tError);
					return false;
				}
			}
			
			for(int i =1;i<=mLCContSet.size();i++) {
				SQL = "update lccont set uwflag='"+mLCContSet.get(i).getUWFlag()+"' where contno='"+mLCContSet.get(i).getContNo()+"'";
				if(!mExeSQL.execUpdateSQL(SQL)){
					CError tError = new CError();
					tError.moduleName = "GrpUWManuNormChkAfterInitService";
					tError.functionName = "prepareOutputData";
					tError.errorMessage = "执行更新语句失败！";
					this.mErrors.addOneError(tError);
					return false;
				}
			}
				
			for(int i =1;i<=mLCGrpPolSet.size();i++) {
				SQL = "update lcgrppol set uwflag='"+mLCGrpPolSet.get(i).getUWFlag()+"' where grppolno='"+mLCGrpPolSet.get(i).getGrpPolNo()+"'";
				if(!mExeSQL.execUpdateSQL(SQL)){
					CError tError = new CError();
					tError.moduleName = "GrpUWManuNormChkAfterInitService";
					tError.functionName = "prepareOutputData";
					tError.errorMessage = "执行更新语句失败！";
					this.mErrors.addOneError(tError);
					return false;
				}
			}
		}
		
		map.put(mAllLCUWMasterSet, "DELETE&INSERT");
		map.put(mAllLCUWSubSet, "INSERT");
		map.put(mAllLCCUWMasterSet, "DELETE&INSERT");
		map.put(mAllLCCUWSubSet, "INSERT");
		map.put(mAllLCGUWMasterSet, "DELETE&INSERT");
		map.put(mAllLCGUWSubSet, "INSERT");
		map.put(mAllLCGCUWMasterSet, "DELETE&INSERT");
		map.put(mAllLCGCUWSubSet, "INSERT");
		// 如果是拒保在此生成打印管理表数据
		if (mUWFlag.equals("1"))
		{
			map.put(mLOPRTManagerSet, "INSERT");
			map.add(mmMap);
		}
		mResult.add(map);
		return true;
	}
	
	/**
	 * 如果有GIL值的话,在人工核保确认的时候需要重新计算保费
	 * 2007.3.16
	 * @return boolean
	 */
	private boolean RecalculationPrem(){
		LCPolBL tLCPolBL = new LCPolBL();
		ExeSQL mExeSQL = new ExeSQL();
        SSRS aSSRS = new SSRS();
        boolean tag = false;
    
        	
		for(int i=1;i<=mAllLCPolSet.size();i++) {
			tLCPolBL.setSchema(mAllLCPolSet.get(i).getSchema());
			//只有有GIL值且GIL的差值大于0的才会回去重算保费
			//chenwm20071031 subGIL=0也要重算,因为如果GIL调小后又调大到和原保额一样,subGIL就等于0了.
			
			String strCalRule = " select SysContPlanCode from lccontplandutyparam where contplancode=("
				+ "select contplancode from lcinsured where contno = '"+tLCPolBL.getContNo()+"'"
				+ ") and grpcontno='"
				+ tLCPolBL.getGrpContNo()
				+ "' and riskcode='"
				+ tLCPolBL.getRiskCode()
				+ "' "
				+ "and plantype='1' and calfactor='CalRule' and calfactorvalue='0'";
		    mExeSQL = new ExeSQL();
		    SSRS arrSSRS = mExeSQL.execSQL(strCalRule);
			
			if( getIfORGIL(tLCPolBL.getRiskCode())) {
				
			}else if( tLCPolBL.getSubGIL()>=0&&ZeroGIL(tLCPolBL.getRiskCode(), tLCPolBL.getGrpContNo()) && arrSSRS.getMaxRow() == 0 ) {
				LCDutyBLSet tLCDutyBLSet = new LCDutyBLSet();			
				
				//获取责任表的信息
				SQL = "select * from lcduty where polno ='"+tLCPolBL.getPolNo()+"'";
				aSSRS = mExeSQL.execSQL(SQL);
				
	            if(aSSRS.getMaxRow()>0) {
	            	for(int j = 1;j<=aSSRS.getMaxRow();j++) {
	            		LCDutyDB tLCDutyDB = new LCDutyDB();
	            		LCDutyBL tLCDutyBL = new LCDutyBL();
	            		
	            		tLCDutyDB.setPolNo(aSSRS.GetText(j, 1));
	            		tLCDutyDB.setDutyCode(aSSRS.GetText(j,2));
	            		
	            		//根据关键信息获取新的Schema 
	            		if(tLCDutyDB.getInfo()) {
	            			tLCDutyBL.setSchema(tLCDutyDB.getSchema());
	            		}
	            		tLCDutyBL.setPrem(0);//由于CalBL中处理浮动费率的原因，现在置Prem为零
	            		//缺少再次计算如果责任的最大保额超过GIL的话,责任的保额修改为GIL
	            		tLCDutyBLSet.add(tLCDutyBL);
	            	}
	            }			
				//如果是统一费率 则直接计算保费=保额*费率
	            for(int m=0;m<tLCDutyBLSet.size();m++){
	            	String CalRule=tLCDutyBLSet.get(m+1).getCalRule();
	            	//统一费率,或者约定保费保额  期交要乘个比例
                    SSRS bSSRS = new SSRS();
                    String  strSQL = "select rateintv from ratepayintv where payintv='"+tLCDutyBLSet.get(m+1).getPayIntv()+"'";
                    bSSRS = mExeSQL.execSQL(strSQL);
                    double RateIntv=1;
                    if(bSSRS.getMaxRow()>0) {
                    	RateIntv = Double.parseDouble(bSSRS.GetText(1,1)); //保费在乘以比例
                    }
	            	if("3".equals(CalRule)){
	            		//tLCDutyBLSet.get(m+1).setPrem(tLCDutyBLSet.get(m+1).getPrem()*RateIntv);
	        			CError tError = new CError();
	        			tError.moduleName = "GrpUWManuNormChkAfterInitService";
	        			tError.functionName = "RecalculationPrem";
	        			tError.errorMessage = "计算规则是'3-约定保费'的计划不能做GIL调整.";
	        			this.mErrors.addOneError(tError);
	        			return false;
	            	}
	            	if("1".equals(CalRule)){
	            		double tprem = tLCDutyBLSet.get(m+1).getAmnt()*tLCDutyBLSet.get(m+1).getFloatRate()*RateIntv;
	            		tLCDutyBLSet.get(m+1).setPrem(Arith.round(tprem,2));
	            	}
	            }	            
	            CalBL tC = new CalBL(tLCPolBL, tLCDutyBLSet,"",true);
//				tC.setNoCalFalg(false); //将是否需要计算的标记传入计算类中
//				boolean flag = tC.calPol();
//				if(!flag) {
//	                    return false;
//				}
				
	            //得到计算结果
	            getCalResult(tC.getLCPol(), tC.getLCDuty(),tC.getLCPrem());
	            tag = true;
	            boolean returnflag2 = tC.getreturnflag();
	            if(!returnflag2){
	            	CError tError = new CError();
	    			tError.moduleName = "GrpUWManuNormChkAfterInitService";
	    			tError.functionName = "prepareOutputData2";
	    			tError.errorMessage = "保费计算失败！";
	    			this.mErrors.addOneError(tError);
	    			return false;
	            }
			}
		}
		
		//只有有GIl的情况才会取重新更新保费和保额
		if(tag) {
			//更新LCGrpPOl,LCGRPCont,LCCont表
			if(!getLCGrp()) {
				return false;
			}
		}
		
		return true;
	}
	//备份lcpol.subgil>0部分lcduty,lcprem,lcget表数据
	private boolean bakLCTable()
	{
        MMap map = new MMap();
        VData mInputData = new VData();
		LCPolBL tLCPolBL = new LCPolBL();
		for(int i=1;i<=mAllLCPolSet.size();i++) {
			tLCPolBL.setSchema(mAllLCPolSet.get(i).getSchema());
			if(tLCPolBL.getSubGIL()>=0)
			{
				//LCDuty-->LCBDuty
				LCBDutySet tLCBDutySet = new LCBDutySet();
				LCDutyDB tLCDutyDB = new LCDutyDB();
				LCDutySet tLCDutySet = new LCDutySet();
				tLCDutyDB.setPolNo(tLCPolBL.getPolNo());
				tLCDutySet = tLCDutyDB.query();
				if(tLCDutySet != null && tLCDutySet.size() >= 1)
				{
					tLCBDutySet = new LCBDutySet();
					for(int j=1;j<=tLCDutySet.size();j++)
					{
						LCBDutySchema tLCBDutySchema = new LCBDutySchema();
						mReflections.transFields(tLCBDutySchema, tLCDutySet.get(j).getSchema());
						tLCBDutySchema.setSerialNo("Q");
						tLCBDutySet.add(tLCBDutySchema);
					}
					

				}
				//LCPrem-->LCBPrem
				LCBPremSet tLCBPremSet = new LCBPremSet();
				LCPremDB tLCPremDB = new LCPremDB();
				LCPremSet tLCPremSet = new LCPremSet();
				tLCPremDB.setPolNo(tLCPolBL.getPolNo());
				tLCPremSet = tLCPremDB.query();
				if(tLCPremSet != null && tLCPremSet.size() >= 1)
				{
					tLCBPremSet = new LCBPremSet();
					for(int j=1;j<=tLCPremSet.size();j++)
					{
						LCBPremSchema tLCBPremSchema = new LCBPremSchema();
						mReflections.transFields(tLCBPremSchema, tLCPremSet.get(j).getSchema());
						tLCBPremSchema.setSerialNo("Q");
						tLCBPremSet.add(tLCBPremSchema);
					}

				}
				//LCGet-->LCBGet
				LCBGetSet tLCBGetSet = new LCBGetSet();
				LCGetDB tLCGetDB = new LCGetDB();
				LCGetSet tLCGetSet = new LCGetSet();
				tLCGetDB.setPolNo(tLCPolBL.getPolNo());
				tLCGetSet = tLCGetDB.query();
				if(tLCGetSet != null && tLCGetSet.size() >= 1)
				{
				    tLCBGetSet = new LCBGetSet();
				    for(int j=1;j<=tLCGetSet.size();j++)
					{
				    	LCBGetSchema tLCBGetSchema = new LCBGetSchema();
				    	mReflections.transFields(tLCBGetSchema, tLCGetSet.get(j).getSchema());
				    	tLCBGetSchema.setSerialNo("Q");
				    	tLCBGetSet.add(tLCBGetSchema);
					}
					
				}
				map = new MMap();
				//备份
				map.put(tLCBDutySet, "DELETE&INSERT");
				map.put(tLCBPremSet, "DELETE&INSERT");
				map.put(tLCBGetSet, "DELETE&INSERT");
				
				
				mInputData.clear();
	            mInputData.add(map);
		        //提交数据
	            PubSubmit tPubSubmit = new PubSubmit();
	            if (!tPubSubmit.submitData(mInputData, "")) 
	            {
	            	CError tError = new CError();
	    			tError.moduleName = "GrpUWManuNormChkAfterInitService";
	    			tError.functionName = "bakLCTable";
	    			tError.errorMessage = "数据提交失败！";
	    			this.mErrors.addOneError(tError);
	            	return false;
	            } 
			}
		}
		return true;
	}
	
	
//	根据契约录入的GIL处理信息LCGilTrace表对保费保额进行调整
	private boolean GilByLCGilTrace()
	{
        MMap map = new MMap();
        VData mInputData = new VData();
        
		LCPolBL tLCPolBL = new LCPolBL();
		ExeSQL mExeSQL = new ExeSQL();
        boolean tag = false;
		for(int i=1;i<=mAllLCPolSet.size();i++) 
		{
			tLCPolBL.setSchema(mAllLCPolSet.get(i).getSchema());
			//套餐不进行GIL保费重算
			String strCalRule = " select SysContPlanCode from lccontplandutyparam where contplancode=("
				+ "select contplancode from lcinsured where contno = '"+tLCPolBL.getContNo()+"'"
				+ ") and grpcontno='"
				+ tLCPolBL.getGrpContNo()
				+ "' and riskcode='"
				+ tLCPolBL.getRiskCode()
				+ "' "
				+ "and plantype='1' and calfactor='CalRule' and calfactorvalue='0'";
		    mExeSQL = new ExeSQL();
		    SSRS arrSSRS = mExeSQL.execSQL(strCalRule);
		    
		    //只有有GIL值且GIL的差值大于0的才会回去重算保费
			if( getIfORGIL(tLCPolBL.getRiskCode()))   //是GIL值的险种的话,返回false
			{ }
			else if( tLCPolBL.getSubGIL()>=0&&ZeroGIL(tLCPolBL.getRiskCode(), tLCPolBL.getGrpContNo()) && arrSSRS.getMaxRow() == 0 ) 
			{
				//在LCGILTrace存在GIL调整信息的修改保费保额信息
				String sqlStr = " select * from lcgiltrace where polno='"+tLCPolBL.getPolNo()+"' and serialno='Q' ";
				LCGILTraceDB tLCGILTraceDB = new LCGILTraceDB();
				LCGILTraceSet tLCGILTraceSet = new LCGILTraceSet();
				tLCGILTraceSet = tLCGILTraceDB.executeQuery(sqlStr);
				
				if(tLCGILTraceSet != null && tLCGILTraceSet.size() >= 1)  
				{
					tag = true;
					
					//LCDuty
					LCDutyDB tLCDutyDB = new LCDutyDB();
					LCDutySet tLCDutySet = new LCDutySet();
					tLCDutyDB.setPolNo(tLCPolBL.getPolNo());
					tLCDutySet = tLCDutyDB.query();

					//LCPrem
					LCPremDB tLCPremDB = new LCPremDB();
					LCPremSet tLCPremSet = new LCPremSet();
					tLCPremDB.setPolNo(tLCPolBL.getPolNo());
					tLCPremSet = tLCPremDB.query();
	
					//LCGet
					LCGetDB tLCGetDB = new LCGetDB();
					LCGetSet tLCGetSet = new LCGetSet();
					tLCGetDB.setPolNo(tLCPolBL.getPolNo());
					tLCGetSet = tLCGetDB.query();
					//修改保费保额
					LCGILTraceSchema tLCGILTraceSchema = new LCGILTraceSchema();
					for(int j=1;j<=tLCGILTraceSet.size();j++) 
					{
						tLCGILTraceSchema = tLCGILTraceSet.get(j).getSchema();
						String tPolNo =tLCGILTraceSchema.getPolNo();
						String tDutyCode = tLCGILTraceSchema.getDutyCode();
						double tOldAmnt = 0;  //记录GIL处理前的基本保额
						for(int k=1;k<=tLCDutySet.size();k++) //1:1
						{
							if(tLCDutySet.get(k).getPolNo().equals(tPolNo) && tLCDutySet.get(k).getDutyCode().equals(tDutyCode))
							{
								LCDutySchema tLCDutySchema =tLCDutySet.get(k);
								tOldAmnt = tLCDutySchema.getAmnt();
//								tLCDutySchema.setSumPrem(tLCDutySchema.getSumPrem()+tLCGILTraceSchema.getAddSumPrem());
								tLCDutySchema.setPrem(Arith.add(tLCDutySchema.getPrem(),tLCGILTraceSchema.getAddPrem()));
								tLCDutySchema.setStandPrem(Arith.add(tLCDutySchema.getStandPrem(),tLCGILTraceSchema.getAddStandPrem()));
								tLCDutySchema.setAmnt(Arith.add(tLCDutySchema.getAmnt(),tLCGILTraceSchema.getAddAmnt()));
								tLCDutySchema.setRiskAmnt(Arith.add(tLCDutySchema.getRiskAmnt(),tLCGILTraceSchema.getAddRiskAmnt()));
								tLCDutySchema.setSubGIL(Arith.sub(tLCDutySchema.getSubGIL(),tLCGILTraceSchema.getApprovalAmnt()));
								break;
							}
						}
						for(int k=1;k<=tLCPremSet.size();k++) //1:1
						{
							if(tLCPremSet.get(k).getPolNo().equals(tPolNo) && tLCPremSet.get(k).getDutyCode().equals(tDutyCode))
							{
								LCPremSchema tLCPremSchema =tLCPremSet.get(k);
//								tLCPremSchema.setSumPrem(tLCPremSchema.getSumPrem()+tLCGILTraceSchema.getAddSumPrem());
								tLCPremSchema.setPrem(Arith.add(tLCPremSchema.getPrem(),tLCGILTraceSchema.getAddPrem()));
								tLCPremSchema.setStandPrem(Arith.add(tLCPremSchema.getStandPrem(),tLCGILTraceSchema.getAddStandPrem()));
								break;
							}
						}
						for(int k=1;k<=tLCGetSet.size();k++)  // 1:n
						{
							if(tLCGetSet.get(k).getPolNo().equals(tPolNo) && tLCGetSet.get(k).getDutyCode().equals(tDutyCode))
							{
								LCGetSchema tLCGetSchema =tLCGetSet.get(k);
								if(tOldAmnt>0)
								{
									tLCGetSchema.setStandMoney(Arith.round(Arith.add(tLCGetSchema.getStandMoney(),Arith.mul(tLCGILTraceSchema.getAddAmnt(),Arith.div(tLCGetSchema.getStandMoney(), tOldAmnt))),2));
									tLCGetSchema.setActuGet(Arith.round(Arith.add(tLCGetSchema.getActuGet(),Arith.mul(tLCGILTraceSchema.getAddAmnt(),Arith.div(tLCGetSchema.getActuGet(), tOldAmnt))),2));
								}
								else
								{
									tLCGetSchema.setStandMoney(0);
									tLCGetSchema.setActuGet(0);
								}
							}
						}
					}
					
					map = new MMap();
					//修改
					map.put(tLCDutySet, "DELETE&INSERT");
					map.put(tLCPremSet, "DELETE&INSERT");
					map.put(tLCGetSet, "DELETE&INSERT");
					
					mInputData.clear();
		            mInputData.add(map);
			        //提交数据
		            PubSubmit tPubSubmit = new PubSubmit();
		            if (!tPubSubmit.submitData(mInputData, "")) 
		            {
		            	CError tError = new CError();
		    			tError.moduleName = "GrpUWManuNormChkAfterInitService";
		    			tError.functionName = "GilByLCGilTrace";
		    			tError.errorMessage = "数据提交失败！";
		    			this.mErrors.addOneError(tError);
		            	return false;
		            } 
				}
				//修改lcpol的subgil
			    sqlStr = " select nvl(max(subgil),0) from lcduty where polno='"+tLCPolBL.getPolNo()+"' ";
				String tSubGil = mExeSQL.getOneValue(sqlStr);
				System.out.println("LCPOL.SubGil="+tSubGil);
                if(tSubGil!=null && !tSubGil.equals(""))
                {
                	sqlStr = " update lcpol set subgil="+tSubGil+" where polno='"+tLCPolBL.getPolNo()+"' ";
                	if(!mExeSQL.execUpdateSQL(sqlStr)){
        				CError tError = new CError();
        				tError.moduleName = "GrpUWManuNormChkAfterInitService";
        				tError.functionName = "GilByLCGilTrace";
        				tError.errorMessage = "LCPol更新SubGil语句失败！";
        				this.mErrors.addOneError(tError);
        				return false;
        			}
                }
			}
		}
       //只有有GIl的情况才会取重新更新保费和保额
		if(tag) {
			//更新LCGrpPOl,LCGRPCont,LCCont,lcpol表
	    	for(int i = 1;i<=mLCGrpPolSet.size();i++) 
	    	{
	        	double GIL = mLCGrpPolSet.get(i).getGIL();
	        	String tGrpContNo = mLCGrpPolSet.get(i).getGrpContNo();
	        	if(GIL>0)
	        	{
	        		String tGrpPolNo = mLCGrpPolSet.get(i).getGrpPolNo();
	        		
	        		String sqlStr = " update lcpol a set " 
//	        			+ " a.sumprem=(select nvl(sum(sumprem),0) from lcduty where polno=a.polno), "
	        			+ " a.prem=(select nvl(sum(prem),0) from lcduty where polno=a.polno), "
	        			+ " a.standprem=(select nvl(sum(standprem),0) from lcduty where polno=a.polno), "
	        			+ " a.amnt=(select nvl(max(amnt),0) from lcduty where polno=a.polno), "
	        			+ " a.riskamnt=(select nvl(max(riskamnt),0) from lcduty where polno=a.polno) "
	        			+ " where grppolno='"+tGrpPolNo+"' ";
	        		if(!mExeSQL.execUpdateSQL(sqlStr)){
	    				CError tError = new CError();
	    				tError.moduleName = "GrpUWManuNormChkAfterInitService";
	    				tError.functionName = "GilByLCGilTrace";
	    				tError.errorMessage = "LCPol执行更新语句失败！";
	    				this.mErrors.addOneError(tError);
	    				return false;
	    			}
	        		
	        	}
	    	}
	    	String sqlStr = "  update lccont a set " 
//	    		+ " a.sumprem=(select nvl(sum(sumprem),0) from lcpol where contno=a.contno), "
    			+ " a.prem=(select nvl(sum(prem),0) from lcpol where contno=a.contno), "
    			+ " a.amnt=(select nvl(sum(amnt),0) from lcpol where contno=a.contno) "
    			+ " where grpcontno='"+mGrpContNo+"' ";
	    	if(!mExeSQL.execUpdateSQL(sqlStr)){
				CError tError = new CError();
				tError.moduleName = "GrpUWManuNormChkAfterInitService";
				tError.functionName = "GilByLCGilTrace";
				tError.errorMessage = "LCCont执行更新语句失败！";
				this.mErrors.addOneError(tError);
				return false;
			}
	    		
	    	sqlStr = " update lcgrppol a set a.amnt= (select nvl(sum(amnt),0) from lcpol where grppolno=a.grppolno), "
	    		+ " a.prem=(select nvl(sum(prem),0) from lcpol where grppolno=a.grppolno) "
//	    		+ " a.sumprem=(select nvl(sum(sumprem),0) from lcpol where grppolno=a.grppolno) "
	    		+ " where  grpcontno='"+mGrpContNo+"' ";
	    	if(!mExeSQL.execUpdateSQL(sqlStr)){
				CError tError = new CError();
				tError.moduleName = "GrpUWManuNormChkAfterInitService";
				tError.functionName = "GilByLCGilTrace";
				tError.errorMessage = "LCGrpPol执行更新语句失败！";
				this.mErrors.addOneError(tError);
				return false;
			}
	    	
	    	sqlStr = "  update lcgrpcont a set a.amnt= (select nvl(sum(amnt),0) from lcpol where grpcontno=a.grpcontno), "
	    		+ " a.prem=(select nvl(sum(prem),0) from lcpol where grpcontno=a.grpcontno) "
//	    		+ " a.sumprem=(select nvl(sum(sumprem),0) from lcpol where grpcontno=a.grpcontno) "
	    		+ " where  grpcontno='"+mGrpContNo+"' ";
	    	if(!mExeSQL.execUpdateSQL(sqlStr)){
				CError tError = new CError();
				tError.moduleName = "GrpUWManuNormChkAfterInitService";
				tError.functionName = "GilByLCGilTrace";
				tError.errorMessage = "LCGrpCont执行更新语句失败！";
				this.mErrors.addOneError(tError);
				return false;
			}	
	    	
	    	sqlStr = "  update lcgiltrace a set "
	    		+ " a.newstandprem=(select standprem from lcduty where polno=a.polno and dutycode=a.dutycode), "
	    		+ " a.newprem=(select prem from lcduty where polno=a.polno and dutycode=a.dutycode), "
	    		+ " a.newsumprem=(select sumprem from lcduty where polno=a.polno and dutycode=a.dutycode), "
	    		+ " a.newamnt=(select amnt from lcduty where polno=a.polno and dutycode=a.dutycode), "
	    		+ " a.newriskamnt=(select riskamnt from lcduty where polno=a.polno and dutycode=a.dutycode), "
	    		+ " a.newsubgil=(select subgil from lcduty where polno=a.polno and dutycode=a.dutycode) "
	    		+ " where a.serialno='Q' and a.grpcontno='"+mGrpContNo+"' ";
	    	if(!mExeSQL.execUpdateSQL(sqlStr)){
				CError tError = new CError();
				tError.moduleName = "GrpUWManuNormChkAfterInitService";
				tError.functionName = "GilByLCGilTrace";
				tError.errorMessage = "LCGILTrace执行更新语句失败！";
				this.mErrors.addOneError(tError);
				return false;
			}	
		}
		return true;
	}
	//判断是否在LCGILTrace存在人工核保GIL调整信息,如果是的话，返回true
	private boolean hasLCGILTrace(String PolNo)
	{
		ExeSQL mExeSQL = new ExeSQL();
		String sqlStr = " select count(1) from lcgiltrace where polno='"+PolNo+"' and serialno='Q' ";
		if(Integer.parseInt(mExeSQL.getOneValue(sqlStr))>0)
		{
			return true;
		}
		return false;
	}
	
	//判断是否是GIL值的险种 ,如果是的话,返回false
	private boolean getIfORGIL(String riskcode) {
		// [ 2011/01/06 inserted by qsnp099
		if(lmriskappTable == null){
			lmriskappTable = new Hashtable();
			ExeSQL exeSQL = new ExeSQL();
			SSRS ssrs = new SSRS();
			String sql = "select riskcode from lmriskapp where risktype8 = '1' ";
			ssrs = exeSQL.execSQL(sql);
			if(ssrs.getMaxRow()>0){
				for (int i=1;i<=ssrs.getMaxRow();i++) {
					String key = ssrs.GetText(i, 1);
					lmriskappTable.put(key, "1");
				}
			}
		}
		if(lmriskappTable.get(riskcode) != null)
			return false;
		// 2011/01/06 inserted by qsnp099 ] 

		// [ 2011/01/06 deleted by qsnp099
//		ExeSQL mExeSQL = new ExeSQL();
//	   SQL = " select 1 from lmriskapp where risktype8 = '1' and riskcode='"+riskcode+"'";
//       if("1".equals(mExeSQL.getOneValue(SQL))){
//					return false;
//		}
		// 2011/01/06 deleted by qsnp099 ]
       return true;
	}
	
	private boolean ZeroGIL(String RiskCode,String GrpContNo) {
		// [ 2011/01/06 inserted by qsnp099
		if(GrpContNo == null){
			return false;
		}		
		if(!GrpContNo.equals(grpcontnoFlag) || gilTable == null ){
			grpcontnoFlag = GrpContNo;
			gilTable = new Hashtable();
			ExeSQL exeSQL = new ExeSQL();
			SSRS ssrs = new SSRS();
			String sql = "select riskcode,gil from lcgrppol where grpcontno = '"+GrpContNo+"' ";
			ssrs = exeSQL.execSQL(sql);
			if(ssrs.getMaxRow()>0){
				for (int i=1;i<=ssrs.getMaxRow();i++) {
					String key = ssrs.GetText(i, 1);
					String value = ssrs.GetText(i, 2);
					gilTable.put(key, value);
				}
			}
		}		
		if(gilTable.get(RiskCode) != null && Double.parseDouble((String)gilTable.get(RiskCode))>0){
			return true;
		}
		// 2011/01/06 inserted by qsnp099 ] 

		// [ 2011/01/06 deleted by qsnp099
//	   ExeSQL mExeSQL = new ExeSQL();
//       SSRS bSSRS = new SSRS();
//	   SQL = " select gil from lcgrppol where grpcontno = '"+GrpContNo+"' and riskcode = '"+RiskCode+"'";
//       bSSRS = mExeSQL.execSQL(SQL);
//       
//       if(bSSRS!=null&&bSSRS.getMaxRow()>0&&Double.parseDouble(bSSRS.GetText(1, 1))>0) 
//       {
//	      return true;						
//	   }
		// 2011/01/06 deleted by qsnp099 ]
       
       return false;
	}
	
	 /**
     * 处理重算结果
     * @param pLCPolBL
     * @param pLCDutyBLSet
     * @param pLCPremBLSet
     * @param pLCGetBLSet
     * @return
     * 
     */
    private boolean getCalResult(LCPolBL pLCPolBL, LCDutyBLSet pLCDutyBLSet,
                                 LCPremBLSet pLCPremBLSet)
    {
        //获取重算后保单表数据，并重置原来的的保单表未变更信息
//        LCPolSchema tLCPolSchema = new LCPolSchema();
        mReflections.transFields(tLCPolSchema, pLCPolBL.getSchema());

        tLCPremSet = pLCPremBLSet; //得到的保费项集合不包括加费的保费项，所以在后面处理
//        tLCGetSet = pLCGetBLSet;
        tLCDutySet = pLCDutyBLSet;
       
        tLCPolSet.add(pLCPolBL.getSchema());
       if(!prepareOutputData2()) {
    	   return false;
       }
       
    	return  true;
    }

	/**
	 * 准备返回前台统一存储数据 输出：如果发生错误则返回false,否则返回true
	 * @return boolean
	 */
	private boolean prepareOutputData2()
	{
		
		if(tLCPolSchema.getRiskCode()!=null && tLCPolSchema.getRiskCode().equals("NHK01"))  //安康保费不变
		{
			//更新LCpol表
			SQL="update lcpol set amnt='"+tLCPolSchema.getAmnt()+"',riskamnt='"+tLCPolSchema.getRiskAmnt()+"'" +
	    					" where polno='"+tLCPolSchema.getPolNo()+"'";
	    	if(!mExeSQL.execUpdateSQL(SQL)){
	    		CError tError = new CError();
				tError.moduleName = "GrpUWManuNormChkAfterInitService";
				tError.functionName = "prepareOutputData2";
				tError.errorMessage = "执行更新语句失败！";
				this.mErrors.addOneError(tError);
				return false;
	    	}
		}
		else
		{
			//更新LCpol表
			SQL="update lcpol set amnt='"+tLCPolSchema.getAmnt()+"',riskamnt='"+tLCPolSchema.getRiskAmnt()+"'" +
	    			" ,prem='"+tLCPolSchema.getPrem()+"' ,standprem='"+tLCPolSchema.getStandPrem()+"'" +
	    					" where polno='"+tLCPolSchema.getPolNo()+"'";
	    	if(!mExeSQL.execUpdateSQL(SQL)){
	    		CError tError = new CError();
				tError.moduleName = "GrpUWManuNormChkAfterInitService";
				tError.functionName = "prepareOutputData2";
				tError.errorMessage = "执行更新语句失败！";
				this.mErrors.addOneError(tError);
				return false;
	    	}
	
	        
	//		//更新LCGet表standmoney 和actuget
	//		for(int i = 1;i<=tLCGetSet.size();i++) {
	//			strSQL = "update LCGet set standmoney='"+tLCGetSet.get(i).getStandMoney()+"',actuget='" +
	//					""+tLCGetSet.get(i).getActuGet()+"' where polno='"+tLCGetSet.get(i).getPolNo()+"' " +
	//					"and dutycode='"+tLCGetSet.get(i).getDutyCode()+"' and getdutycode='"+tLCGetSet.get(i).getGetDutyCode()+"'";
	//			tExeSQL.execUpdateSQL(strSQL);
	//		}
			
			//更新LCDuty表的保费
			
			for(int i=1;i<=tLCDutySet.size();i++) {
				SQL="update LCDuty set standprem='"+tLCDutySet.get(i).getStandPrem()+"',prem='"+tLCDutySet.get(i).getPrem()+"'" +
						//" ,amnt='"+tLCDutySet.get(i).getAmnt()+"' ,riskamnt ='"+tLCDutySet.get(i).getAmnt()+"'"+
				 		" where polno='"+tLCDutySet.get(i).getPolNo()+"' and dutycode='"+tLCDutySet.get(i).getDutyCode()+"' ";
				if(!mExeSQL.execUpdateSQL(SQL)){
					CError tError = new CError();
					tError.moduleName = "GrpUWManuNormChkAfterInitService";
					tError.functionName = "prepareOutputData2";
					tError.errorMessage = "执行更新语句失败！";
					this.mErrors.addOneError(tError);
					return false;
				}
			}
			
			//更新LCPrem表的保费
			for(int i=1;i<=tLCPremSet.size();i++) {
				SQL = "update LCPrem set standprem='"+tLCPremSet.get(i).getStandPrem()+"' ,prem='"+tLCPremSet.get(i).getPrem()+"'" +
						" where polno='"+tLCPremSet.get(i).getPolNo()+"' and dutycode='"+tLCPremSet.get(i).getDutyCode()+"' and " +
								"payplancode='"+tLCPremSet.get(i).getPayPlanCode()+"'";
				if(!mExeSQL.execUpdateSQL(SQL)){
					CError tError = new CError();
					tError.moduleName = "GrpUWManuNormChkAfterInitService";
					tError.functionName = "prepareOutputData2";
					tError.errorMessage = "执行更新语句失败！";
					this.mErrors.addOneError(tError);
					return false;
				}
			}
		}
		tsign = true; 
		return true;
	}
    
	/*
     * 更新重算保费后的相应的表
     * @return boolean
     */
    
    private boolean getLCGrp() {
        String tgrpcontno = tLCPolSet.get(1).getGrpContNo();

        //存LCPol表中的保额与GIL差值
        //chenwm20070820 取消注释,在lcpol表中存上SubGIL,解决人工核保时,GIL调小后不能调大的问题.
        for(int i=1;i<=mLCPolSet.size();i++) {
        	SQL="update lcpol set SubGIL='"+mLCPolSet.get(i).getSubGIL()+"' where PolNo='"+mLCPolSet.get(i).getPolNo()+"'";
        	if(!mExeSQL.execUpdateSQL(SQL)){
        		CError tError = new CError();
    			tError.moduleName = "GrpUWManuNormChkAfterInitService";
    			tError.functionName = "getLCGrp";
    			tError.errorMessage = "执行更新语句失败！";
    			this.mErrors.addOneError(tError);
    			return false;
        	}
        }
        
        //先更新LCPol表
//        for(int j=1;j<=tLCPolSet.size();j++) {
//        	SQL="update lcpol set amnt='"+tLCPolSet.get(j).getAmnt()+"',riskamnt='"+tLCPolSet.get(j).getRiskAmnt()+"'" +
//        			" ,prem='"+tLCPolSet.get(j).getPrem()+"' ,standprem='"+tLCPolSet.get(j).getStandPrem()+"'" +
//        					" where polno='"+tLCPolSet.get(j).getPolNo()+"'";
//        	mExeSQL.execUpdateSQL(SQL);
//        }
        
        //更新LCCont表的保额和保费
        for(int i=1;i<=tLCPolSet.size();i++) {
        	SQL = "update LCCont set amnt =(select sum(amnt) from lcpol where contno='"+tLCPolSet.get(i).getContNo()+"') , " +
        			" prem=(select sum(prem) from lcpol where contno='"+tLCPolSet.get(i).getContNo()+"')" +
        			" where contno='"+tLCPolSet.get(i).getContNo()+"'";
        	if(!mExeSQL.execUpdateSQL(SQL)){
        		CError tError = new CError();
    			tError.moduleName = "GrpUWManuNormChkAfterInitService";
    			tError.functionName = "getLCGrp";
    			tError.errorMessage = "执行更新语句失败！";
    			this.mErrors.addOneError(tError);
    			return false;
        	}
        }
        
        //更新LCGrpPol表的保额和保费
        for(int j = 1;j<=mLCGrpPolSet.size();j++){
        	String tgrppolno = mLCGrpPolSet.get(j).getGrpPolNo();
        	SQL = "update lcgrppol set amnt =(select sum(amnt) from lcpol where grppolno='"+tgrppolno+"') ,"+
              		"prem=(select sum(prem) from lcpol where grppolno='"+tgrppolno+"')  where grppolno='"+tgrppolno+"'";
        	if(!mExeSQL.execUpdateSQL(SQL)){
        		CError tError = new CError();
    			tError.moduleName = "GrpUWManuNormChkAfterInitService";
    			tError.functionName = "getLCGrp";
    			tError.errorMessage = "执行更新语句失败！";
    			this.mErrors.addOneError(tError);
    			return false;
        	}
        }
		//更新LCGRPCont表的保费和保额
		SQL = "update lcgrpcont set amnt=(select sum(amnt) from lcpol where grpcontno='"+tgrpcontno+"'),"+
				"prem=(select sum(prem) from lcpol where grpcontno='"+tgrpcontno+"') where grpcontno='"+tgrpcontno+"'";
		if(!mExeSQL.execUpdateSQL(SQL)){
			CError tError = new CError();
			tError.moduleName = "GrpUWManuNormChkAfterInitService";
			tError.functionName = "getLCGrp";
			tError.errorMessage = "执行更新语句失败！";
			this.mErrors.addOneError(tError);
			return false;
		}
		
    	return true;
    }
    
    /*
     * 初时更新LCDuty表,如果LCDuty表中的保额超过GIL值,则将LCDuty表中的保额修改为GIL值
     * @return boolean
     */
    private boolean updateLCDuty() {
    	for(int i = 1;i<=mLCGrpPolSet.size();i++) {
    		//以前的有问题 对于同一个合同下既有无名单 又有 有名单的, 无名单要乘以人数 chenweiming 20070802
        	SSRS bSSRS = new SSRS();
        	SQL ="select polno,insuredpeoples,contno from lcpol where grppolno = '"+mLCGrpPolSet.get(i).getGrpPolNo()+"'";	
        	bSSRS = mExeSQL.execSQL(SQL);
        	double GIL = mLCGrpPolSet.get(i).getGIL();
        	if(bSSRS.getMaxRow()>0) {
        		for (int j=1;j<=bSSRS.getMaxRow();j++) {
        			String tPolNo = bSSRS.GetText(j, 1);
		    		double tPeopoles=Double.parseDouble(bSSRS.GetText(j, 2));
		    		//对于是GIL的险种并且GIL值大于0		    		
		    		String strCalRule = " select SysContPlanCode from lccontplandutyparam where contplancode=("
						+ "select contplancode from lcinsured where contno = '"+bSSRS.GetText(j, 3)+"'"
						+ ") and grpcontno='"
						+ mLCGrpPolSet.get(i).getGrpContNo()
						+ "' and riskcode='"
						+ mLCGrpPolSet.get(i).getRiskCode()
						+ "' "
						+ "and plantype='1' and calfactor='CalRule' and calfactorvalue='0'";
				    mExeSQL = new ExeSQL();
				    SSRS arrSSRS = mExeSQL.execSQL(strCalRule);	    		
		    		if(!getIfORGIL(mLCGrpPolSet.get(i).getRiskCode())&& GIL>0 && arrSSRS.getMaxRow()==0) {
		            	SSRS cSSRS = new SSRS();
		            	SQL = "select dutycode,amnt,subgil from lcduty where polno = '" + tPolNo + "'";	
		            	cSSRS = mExeSQL.execSQL(SQL);
		            	int cMaxRow = cSSRS.getMaxRow();
		            	if(cMaxRow > 0) {
		            		for (int k = 1;k <= cMaxRow;k++) {
		            			String tDutycode = cSSRS.GetText(k, 1);
		            			double tAmnt = Double.parseDouble(cSSRS.GetText(k, 2));
		            			double tSubgil = Double.parseDouble(cSSRS.GetText(k, 3));
				    			//chenwm20070820  添加对无名单多人的支持.以前没有考虑到人数因素
					    		SQL = "UPDATE LCDuty set amnt = " + (GIL * tPeopoles) + ",riskamnt = (" + (GIL * tPeopoles) + " * riskamnt) / " + tAmnt
				    		        + ",SubGIL = (" + (tAmnt + tSubgil * tPeopoles) + " - " + (GIL * tPeopoles) + ") / " + tPeopoles
				    		        + " where " + (tAmnt + tSubgil * tPeopoles) + " >= " + (GIL * tPeopoles) + " and polno = '" + tPolNo + "' and dutycode = '" + tDutycode + "'";
				    		    System.out.println("1:" + SQL);
					    		if(!mExeSQL.execUpdateSQL(SQL)){
				    			    CError tError = new CError();
				    			    tError.moduleName = "GrpUWManuNormChkAfterInitService";
				    			    tError.functionName = "updateLCDuty";
				    			    tError.errorMessage = "执行更新语句失败！";
				    			    this.mErrors.addOneError(tError);
				    			    return false;
				    		    }

		            			SQL = "UPDATE LCGet set standmoney = (" + (GIL * tPeopoles) + " * standmoney) / " + tAmnt + ",actuget = (" + (GIL * tPeopoles) + " * actuget) / " + tAmnt
		            			    + " where " + (tAmnt + tSubgil * tPeopoles) + " >= " + (GIL * tPeopoles) + " and polno = '" + tPolNo + "' and dutycode = '" + tDutycode + "'";
				    		    System.out.println("2:" + SQL);
		            			if(!mExeSQL.execUpdateSQL(SQL)){
				    			    CError tError = new CError();
				    			    tError.moduleName = "GrpUWManuNormChkAfterInitService";
				    			    tError.functionName = "updateLCDuty";
				    			    tError.errorMessage = "执行更新语句失败！";
				    			    this.mErrors.addOneError(tError);
				    			    return false;
					    		}
		            		}
		            	}
		    		}
        		}
        	}
    	}
    	
    	return true;
    }
    
    private String getPolNo(String tGrpPolNo) {
    	String tPolNo="(";
    	SSRS bSSRS = new SSRS();
    	SQL ="select polno from lcpol where grppolno = '"+tGrpPolNo+"'";	
    	bSSRS = mExeSQL.execSQL(SQL);
    	
    	if(bSSRS.getMaxRow()>0) {
    		for (int i=1;i<=bSSRS.getMaxRow();i++) {
    			tPolNo += "'"+bSSRS.GetText(i,1)+"',";
    		}
    		
    		tPolNo = tPolNo.substring(0,tPolNo.length()-1) + ")" ;
    	}
    	
    	return tPolNo;
    } 
	/**
	 * 校验投保单是否复核 输出：如果发生错误则返回false,否则返回true
	 */
	// private boolean checkApprove(LCGrpContSchema tLCGrpContSchema)
	// {
	// if (!tLCGrpContSchema.getApproveFlag().equals("9"))
	// {
	// // @@错误处理
	// CError tError = new CError();
	// tError.moduleName = "GrpUWManuNormChkBL";
	// tError.functionName = "checkApprove";
	// tError.errorMessage = "投保单尚未进行复核操作，不能核保!（投保单号：" +
	// tLCGrpContSchema.getGrpContNo().trim() + "）";
	// this.mErrors.addOneError(tError);
	// return false;
	// }
	// return true;
	// }
	/**
	 * 校验核保员级别 输出：如果发生错误则返回false,否则返回true
	 * @param tLCGrpContSchema LCGrpContSchema
	 * @return boolean
	 */
	private boolean checkUWGrade(LCGrpContSchema tLCGrpContSchema)
	{
		LDUWUserDB tLDUWUserDB = new LDUWUserDB();
		tLDUWUserDB.setUserCode(mOperator);
		if (!tLDUWUserDB.getInfo())
		{
			CError tError = new CError();
			tError.moduleName = "GrpUWManuNormChkAfterInitService";
			tError.functionName = "checkUWGrade";
			tError.errorMessage = "无此操作员信息，不能核保!（操作员：" + mOperator + "）";
			this.mErrors.addOneError(tError);
			return false;
		}
		String tUWPopedom = tLDUWUserDB.getUWPopedom();
		mUWPopedom = tLDUWUserDB.getUWPopedom();
		mAppGrade = mUWPopedom;
		// 有可能没有描述核保级别，这样会导致空指针错误
		if (tUWPopedom == null || tUWPopedom.equals(""))
		{
			CError tError = new CError();
			tError.moduleName = "GrpUWManuNormChkBL";
			tError.functionName = "checkUWGrade";
			tError.errorMessage = "操作员无核保权限，不能核保!（操作员：" + mOperator + "）";
			this.mErrors.addOneError(tError);
			return false;
		}
		LCGCUWMasterDB tLCGCUWMasterDB = new LCGCUWMasterDB();
		tLCGCUWMasterDB.setProposalGrpContNo(mGrpContNo);
		if (!tLCGCUWMasterDB.getInfo())
		{
			CError tError = new CError();
			tError.moduleName = "GrpUWManuNormChkBL";
			tError.functionName = "checkUWGrade";
			tError.errorMessage = "没有核保信息，不能核保!（操作员：" + mOperator + "）";
			this.mErrors.addOneError(tError);
			return false;
		}
		else
		{
			String tappgrade = tLCGCUWMasterDB.getAppGrade();
//			if (tUWPopedom.compareTo(tappgrade) < 0)
//			{
//				CError tError = new CError();
//				tError.moduleName = "GrpUWManuNormChkBL";
//				tError.functionName = "checkUWGrade";
//				tError.errorMessage = "核保权限不足，不能核保!（操作员：" + mOperator + "）";
//				this.mErrors.addOneError(tError);
//				return false;
//			}
		}

		// 某些级别核保师有处理保单的权限，但是没有下拒保/延期结论的权限。------yeshu，20060824
		if ("1".equals(mUWFlag))
		{
			String tsql = "select * from lduwgrade_greatwall where delayflag='0' and uwgrade='" + tUWPopedom + "'";
			ExeSQL tExeSql = new ExeSQL();
			SSRS tSSRS = new SSRS();
			tSSRS = tExeSql.execSQL(tsql);
			if (tSSRS != null && tSSRS.getMaxRow() > 0)
			{
				CError tError = new CError();
				tError.moduleName = "GrpUWManuNormChkBL";
				tError.functionName = "checkUWGrade";
				tError.errorMessage = "操作员：" + mOperator + " 无权限下拒保结论！";
				this.mErrors.addOneError(tError);
				return false;
			}
		}
		/*
		 * LCGCUWErrorDB tLCGCUWErrorDB = new LCGCUWErrorDB();
		 * tLCGCUWErrorDB.setGrpContNo(tLCGrpContSchema.getGrpContNo()); String
		 * tpolno = tLCGrpContSchema.getGrpContNo(); String tsql = "select *
		 * from LCGCUWerror where GrpContNo = '"+tpolno.trim()+"' and uwno =
		 * (select max(uwno) from LCGCUWerror where GrpContNo =
		 * '"+tpolno.trim()+"')"; LCGCUWErrorSet tLCGCUWErrorSet =
		 * tLCGCUWErrorDB.executeQuery(tsql); int errno =
		 * tLCGCUWErrorSet.size(); if (errno > 0) { for( int i = 1; i <= errno;
		 * i++) { LCGCUWErrorSchema tLCGCUWErrorSchema = new
		 * LCGCUWErrorSchema(); tLCGCUWErrorSchema = tLCGCUWErrorSet.get(i);
		 * String terrgrade = tLCGCUWErrorSchema.getUWGrade();
		 * if(tUWPopedom.compareTo(terrgrade) < 0 && !mUWFlag.equals("6")) {
		 * CError tError = new CError(); tError.moduleName =
		 * "GrpUWManuNormChkBL"; tError.functionName = "checkUWGrade";
		 * tError.errorMessage = "核保级别不够，请录入核保意见，申请待上级核保!（操作员：" + mOperator +
		 * "）"; this.mErrors .addOneError(tError) ; return false; } } }
		 */
		return true;
	}

	/**
	 * 校验团体保单下个单是不是全部通过核保 输出：如果发生错误则返回false,否则返回true
	 * @param tLCGrpContSchema LCGrpContSchema
	 * @return boolean
	 */
	private boolean checkUWPol(LCGrpContSchema tLCGrpContSchema)
	{
		String tsql = "select count(*) from LCCUWMaster where grpcontno='" + tLCGrpContSchema.getGrpContNo()
				+ "' and PassFlag in ('0','5')";
		ExeSQL tExeSQL = new ExeSQL();
		int tcount = Integer.parseInt(tExeSQL.getOneValue(tsql));

		if (tExeSQL.mErrors.needDealError() || tcount >= 1)
		{
			CError.buildErr(this, "集体单下有未通过核保的个人投保单!");
			return false;
		}
		return true;

//		LCCUWMasterDB tLCCUWMasterDB = new LCCUWMasterDB();
//		tLCCUWMasterDB.setGrpContNo(tLCGrpContSchema.getGrpContNo());
//
//		LCCUWMasterSet tLCCUWMasterSet = new LCCUWMasterSet();
//		tLCCUWMasterSet = tLCCUWMasterDB.query();
//
//		int n = tLCCUWMasterSet.size();
//		if (n > 0)
//		{
//			for (int i = 1; i <= n; i++)
//			{
//				LCCUWMasterSchema tLCCUWMasterSchema = new LCCUWMasterSchema();
//				tLCCUWMasterSchema = tLCCUWMasterSet.get(i);
//
//				String tUWFlag = tLCCUWMasterSchema.getPassFlag();
//				if (tUWFlag.equals("0") || tUWFlag.equals("2") || tUWFlag.equals("5") || tUWFlag.equals("6")
//						|| tUWFlag.equals("8"))
//				{
//					CError tError = new CError();
//					tError.moduleName = "GrpUWManuNormChkBL";
//					tError.functionName = "checkUWPol";
//					tError.errorMessage = "集体单下有未通过核保的个人投保单!";
//					this.mErrors.addOneError(tError);
//					return false;
//				}
//			}
//		}
//		return true;
	}

	/**
	 * 校验团体保单下个单是不是全部通过核保 输出：如果发生错误则返回false,否则返回true
	 * @param tLCGrpContSchema LCGrpContSchema
	 * @return boolean
	 */
	private boolean checkUWGrpPol(LCGrpContSchema tLCGrpContSchema)
	{
		LCGCUWMasterDB tLCGCUWMasterDB = new LCGCUWMasterDB();
		tLCGCUWMasterDB.setGrpContNo(mGrpContNo);

		LCGCUWMasterSet tLCGCUWMasterSet = new LCGCUWMasterSet();
		tLCGCUWMasterSet = tLCGCUWMasterDB.query();

		int n = tLCGCUWMasterSet.size();
		if (n > 0)
		{
			for (int i = 1; i <= n; i++)
			{
				LCGCUWMasterSchema tLCGCUWMasterSchema = new LCGCUWMasterSchema();
				tLCGCUWMasterSchema = tLCGCUWMasterSet.get(i);

				String tUWFlag = tLCGCUWMasterSchema.getPassFlag();
				if (tUWFlag.equals("4") || tUWFlag.equals("9"))
				{
					CError tError = new CError();
					tError.moduleName = "GrpUWManuNormChkBL";
					tError.functionName = "checkUWGrpPol";
					tError.errorMessage = "已经整单确认!";
					this.mErrors.addOneError(tError);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 为公共传输数据集合中添加工作流下一节点属性字段数据
	 * @return boolean
	 */
	private boolean prepareTransferData()
	{
		mTransferData.setNameAndValue("UWFlag", mLCGrpContSchema.getUWFlag());
		mTransferData.setNameAndValue("GrpContNo", mGrpContNo);
		mTransferData.setNameAndValue("PrtNo", mLCGrpContSchema.getPrtNo());
		mTransferData.setNameAndValue("AgentCode", mLCGrpContSchema.getAgentCode());
		mTransferData.setNameAndValue("AgentGroup", mLCGrpContSchema.getAgentGroup());
		mTransferData.setNameAndValue("SaleChnl", mLCGrpContSchema.getSaleChnl());
		mTransferData.setNameAndValue("ManageCom", mLCGrpContSchema.getManageCom());
		mTransferData.setNameAndValue("GrpNo", mLCGrpContSchema.getAppntNo());
		mTransferData.setNameAndValue("GrpName", mLCGrpContSchema.getGrpName());
		mTransferData.setNameAndValue("CValiDate", mLCGrpContSchema.getCValiDate());
		mTransferData.setNameAndValue("UWDate", mLCGrpContSchema.getUWDate());
		mTransferData.setNameAndValue("UWTime", PubFun.getCurrentTime());
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

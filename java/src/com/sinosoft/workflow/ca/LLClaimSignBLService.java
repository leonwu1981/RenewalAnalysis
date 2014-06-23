package com.sinosoft.workflow.ca;

import com.sinosoft.lis.db.LCInsureAccClassDB;
import com.sinosoft.lis.db.LCInsureAccDB;
import com.sinosoft.lis.db.LLCaseDB;
import com.sinosoft.lis.db.LLCasePolicyDB;
import com.sinosoft.lis.db.LLClaimDB;
import com.sinosoft.lis.db.LLClaimDetailDB;
import com.sinosoft.lis.db.LLClaimUWMainDB;
import com.sinosoft.lis.db.LLInfoDB;
import com.sinosoft.lis.db.LMRiskDB;
import com.sinosoft.lis.db.LWMissionDB;
import com.sinosoft.lis.pubfun.AccountManage;
import com.sinosoft.lis.pubfun.Arith;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.LCInsureAccClassSchema;
import com.sinosoft.lis.schema.LCInsureAccSchema;
import com.sinosoft.lis.schema.LLCasePolicySchema;
import com.sinosoft.lis.schema.LLCaseSchema;
import com.sinosoft.lis.schema.LLClaimSchema;
import com.sinosoft.lis.schema.LLClaimUWMDetailSchema;
import com.sinosoft.lis.schema.LLClaimUWMainSchema;
import com.sinosoft.lis.schema.LLInfoSchema;
import com.sinosoft.lis.schema.LWMissionSchema;
import com.sinosoft.lis.vschema.LCInsureAccClassSet;
import com.sinosoft.lis.vschema.LCInsureAccSet;
import com.sinosoft.lis.vschema.LCInsureAccTraceSet;
import com.sinosoft.lis.vschema.LLCasePolicySet;
import com.sinosoft.lis.vschema.LLClaimDetailSet;
import com.sinosoft.lis.vschema.LLClaimSet;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;
import com.sinosoft.workflowengine.BeforeInitService;

public class LLClaimSignBLService implements BeforeInitService {
	/** 错误处理类，每个需要错误处理的类中都放置该类 */
	public CErrors mErrors = new CErrors();

	private TransferData mTransferData = new TransferData();

	private GlobalInput mG = new GlobalInput();

	private MMap mMMap = new MMap();

	private VData mResult = new VData();

	private VData mInputData = new VData();

	private String mRgtNo = "";

	private String mCaseNo = "";

	private String mMngCom = "";

	private String mOperate = "";// 操作标志

	private String mOperator = "";// 操作人员

	private String mMissionID = "";

	private String mSubMissionID = "";

	private String mActivityID = "";

	private String mAuditConclusion = "";// 签批结论

	private String mAuditIdea = "";// 签批意见

	private String mSpecialRemark1 = "";// 备注
	
	private String mUpFlag = "";  //1从上报队列进入
	
	private String mGrpContNo ="";
	
	private String mOutUserMngCom ="";   //保单管理机构（外包处理用）

	private LLCaseSchema mLLCaseSchema = new LLCaseSchema();

	private LLClaimUWMDetailSchema mLLClaimUWMDetailSchema = new LLClaimUWMDetailSchema();
	
    private AccountManage mAccountManage = new AccountManage();    
    
    private LLInfoSchema  mLLInfoSchema = new LLInfoSchema();

	public LLClaimSignBLService() {
		super();
		// TODO 自动生成构造函数存根
	}

	public boolean submitData(VData cInputData, String cOperate) {
		if (!getInputData(cInputData)) {
			return false;
		}
		if (!dealData()) {
			return false;
		}
		if (!prepareTransferData()) {
			return false;
		}
		return true;
	}

	public boolean getInputData(VData cInputData) {
		mG = (GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0);
		mTransferData = (TransferData) cInputData.getObjectByObjectName(
				"TransferData", 0);

		mMissionID = (String) mTransferData.getValueByName("MissionID");
		mSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
		mActivityID = (String) mTransferData.getValueByName("ActivityID");

		mCaseNo = (String) mTransferData.getValueByName("CaseNo");
		mRgtNo = (String) mTransferData.getValueByName("RgtNo");
		mOperate = (String) mTransferData.getValueByName("Operate");
		mAuditConclusion = (String) mTransferData.getValueByName("CAFlag");// 总案结论
		mAuditIdea = (String) mTransferData.getValueByName("AuditIdea");
		mSpecialRemark1 = (String) mTransferData
				.getValueByName("SpecialRemark1");
		mMngCom = (String) mTransferData.getValueByName("ManageCom");
		mOperator = (String) mTransferData.getValueByName("Operator");
		mUpFlag = (String)mTransferData.getValueByName("UpFlag");   //1从上报队列进入
		mGrpContNo = (String) mTransferData.getValueByName("GrpContNo");
		if("".equals(mGrpContNo))
		{
			CError tError = new CError();
			tError.moduleName = "LLClaimGrpSignBL";
			tError.functionName = "checkData";
			tError.errorMessage = "复核签批获取保单号失败!";
			this.mErrors.addOneError(tError);
			return false;
		}
		if("".equals(mAuditConclusion))
		{
			CError tError = new CError();
			tError.moduleName = "LLClaimGrpSignBL";
			tError.functionName = "checkData";
			tError.errorMessage = "复核签批获取签批结论失败!";
			this.mErrors.addOneError(tError);
			return false;
		}
		System.out.println("案件保单号："+mGrpContNo);
		if(mG.OutUserFlag.equals("1"))  //外包用户
		{
//			String str = " select managecom from lcgrpcont where grpcontno='"+mGrpContNo+"'";
//			ExeSQL mExeSQL = new ExeSQL();
//			SSRS mSSRS = mExeSQL.execSQL(str);
//			if(mSSRS.getMaxRow()>0)
//			{
//				mOutUserMngCom =mSSRS.GetText(1, 1) ;
//			}
//			else
//			{
//				buildError("getInputData", "查询外包用户保单管理机构信息失败！");
//				return false;
//			}
//			if(mG.ManageCom.length()==8)
//			{
			  mOutUserMngCom=mG.ManageCom;
//			}
//			else
//			{
//				CError tError = new CError();
//				tError.moduleName = "LLClaimSignBLServer";
//				tError.functionName = "getInputData";
//				tError.errorMessage = "请使用八位机构登录！";
//				this.mErrors.addOneError(tError);
//				return false;
//			}
		}
		return true;
	}

	public boolean dealData() {
		if (mOperate != null && mOperate.equals("SIGNCASE")) {
			// 签批意见：同意
			if (mAuditConclusion != null && mAuditConclusion.equals("1")) {
				if (!dealSignPass()) {
					return false;
				}
			}

			// 签批意见：不同意
			if (mAuditConclusion != null && mAuditConclusion.equals("2")) {
				if (!dealSignBack()) {
					return false;
				}

			}
			// 签批意见：请示上级
			if (mAuditConclusion != null && mAuditConclusion.equals("3")) {
				if (!dealSignUp()) {
					return false;
				}
			}
            //  签批意见：上报
			if (mAuditConclusion != null && mAuditConclusion.equals("4")) {
				if (!dealSignReport()) {
					return false;
				}
			}
			mResult.add(mMMap);
		}
		if (mOperate != null && mOperate.equals("WB_SIGNCASE")) {
			// 签批意见：不同意
			if (mAuditConclusion != null && mAuditConclusion.equals("2")) {
				if (!dealSignBack()) {
					return false;
				}

			}
			mResult.add(mMMap);
		}
		return true;
	}
	
	
	/**
	 * 签批通过处理
	 * @return
	 */
	private boolean dealSignPass() {
		// 新分案信息
		LLCaseDB mLLCaseDB = new LLCaseDB();
		mLLCaseDB.setCaseNo(mCaseNo);
		if (!mLLCaseDB.getInfo()) {
			this.mErrors.copyAllErrors(mLLCaseDB.mErrors);
			return false;
		}
		mLLCaseSchema = mLLCaseDB.getSchema();
		mLLCaseSchema.setSigner(mG.Operator);// 签批人
		mLLCaseSchema.setSignerDate(PubFun.getCurrentDate());// 签批日期
		mLLCaseSchema.setRgtState("09");// 签批通过
		
		//add start by ASR20094866 系统邮件、短信和打印通知书触发规则的修改 
		if (mLLCaseSchema.getUWState().equals("3")){
			mLLCaseSchema.setNoticeWay("2");
		}
		//add end by ASR20094866 系统邮件、短信和打印通知书触发规则的修改 
		
		if(mG.OutUserFlag.equals("1"))   //外包用户
		{
			mLLCaseSchema.setCaseProp4("1");// 外包案件标志
		}
		else
		{
			mLLCaseSchema.setCaseProp4("");// 回退案件再签批情况
		}
		mMMap.put(mLLCaseSchema, "UPDATE");

		// 更新赔案表信息
		LLClaimDB tLLClaimDB = new LLClaimDB();
		tLLClaimDB.setCaseNo(mCaseNo);
		LLClaimSet tLLClaimSet = new LLClaimSet();
		tLLClaimSet = tLLClaimDB.query();
		if (tLLClaimSet == null || tLLClaimSet.size() < 1) {
			CError tError = new CError();
			tError.moduleName = "LLClaimSignBL";
			tError.functionName = "dealData";
			tError.errorMessage = "未查询到相应的赔付明细表数据!";
			this.mErrors.addOneError(tError);
		}

		LLClaimSchema tLLClaimSchema = new LLClaimSchema();
		tLLClaimSchema = tLLClaimSet.get(1);// 目前，llcase和llclaim是一一对应关系
		if ("1".equals(tLLClaimSchema.getGiveType())) {
			tLLClaimSchema.setClmState("2");
		} else if ("3".equals(tLLClaimSchema.getGiveType())) {
			tLLClaimSchema.setClmState("4");
		}
		tLLClaimSchema.setModifyDate(PubFun.getCurrentDate());
		tLLClaimSchema.setModifyTime(PubFun.getCurrentTime());
		tLLClaimSchema.setOperator(mG.Operator);
		tLLClaimSchema.setClmUWer(mG.Operator);
		mMMap.put(tLLClaimSchema, "UPDATE");

		// 更新案件核赔表llclaimuwmain
		LLClaimUWMainDB tLLClaimUWMainDB = new LLClaimUWMainDB();
		tLLClaimUWMainDB.setClmNo(mLLCaseDB.getCaseNo());
		boolean hasExists = false;
		hasExists = tLLClaimUWMainDB.getInfo();

		LLClaimUWMainSchema tLLClaimUWMainSchema = new LLClaimUWMainSchema();
		// 存在该案件的核赔信息
		if (hasExists) {
			tLLClaimUWMainSchema = tLLClaimUWMainDB.getSchema();
		} else {
			tLLClaimUWMainSchema.setClmNo(mLLCaseDB.getCaseNo());
			tLLClaimUWMainSchema.setRgtNo(mLLCaseDB.getRgtNo());
			tLLClaimUWMainSchema.setCaseNo(mLLCaseDB.getCaseNo());
			tLLClaimUWMainSchema.setMakeDate(PubFun.getCurrentDate());
			tLLClaimUWMainSchema.setMakeTime(PubFun.getCurrentTime());
		}
		tLLClaimUWMainSchema.setClmUWer(mG.Operator);
		tLLClaimUWMainSchema.setAppPhase("1");// 申请阶段 复核
		tLLClaimUWMainSchema.setAppActionType("1");// 申请动作 确认
		tLLClaimUWMainSchema.setcheckDecision2(mAuditConclusion);// 签批结论
		tLLClaimUWMainSchema.setRemark2(mAuditIdea);// 签批意见
		// tLLClaimUWMainSchema.setRemark(mSpecialRemark1);//备注
		tLLClaimUWMainSchema.setOperator(mG.Operator);
		if(mG.OutUserFlag.equals("1"))   //外包用户
		{
			tLLClaimUWMainSchema.setMngCom(mOutUserMngCom);
		}
		else
		{
			tLLClaimUWMainSchema.setMngCom(mG.ManageCom);
		}
		tLLClaimUWMainSchema.setModifyDate(PubFun.getCurrentDate());
		tLLClaimUWMainSchema.setModifyTime(PubFun.getCurrentTime());

		mMMap.put(tLLClaimUWMainSchema, "DELETE&INSERT");

		// 更新核赔轨迹表

		// 查询LLClaimUWMDetail核赔次数
		String strSQL = "";
		strSQL = " select Max(to_number(ClmUWNo)) from LLClaimUWMDetail where "
				+ " ClmNo='" + tLLClaimUWMainSchema.getClmNo() + "'";
		ExeSQL execsql = new ExeSQL();
		String tMaxNo = execsql.getOneValue(strSQL);
		if (tMaxNo.length() == 0) {
			tMaxNo = "1";
		} else {
			int tInt = Integer.parseInt(tMaxNo);
			tInt = tInt + 1;
			tMaxNo = String.valueOf(tInt);
		}

		mLLClaimUWMDetailSchema.setClmUWNo(tMaxNo);
		mLLClaimUWMDetailSchema.setRgtNo(tLLClaimUWMainSchema.getRgtNo());
		mLLClaimUWMDetailSchema.setCaseNo(tLLClaimUWMainSchema.getCaseNo());
		mLLClaimUWMDetailSchema.setClmNo(tLLClaimUWMainSchema.getClmNo());
		mLLClaimUWMDetailSchema.setClmUWer(tLLClaimUWMainSchema.getClmUWer());
		mLLClaimUWMDetailSchema.setClmUWGrade(tLLClaimUWMainSchema
				.getClmUWGrade());
		mLLClaimUWMDetailSchema.setClmDecision(tLLClaimUWMainSchema
				.getcheckDecision2());
		mLLClaimUWMDetailSchema.setRemark(tLLClaimUWMainSchema.getRemark2());
		mLLClaimUWMDetailSchema.setCheckType(tLLClaimUWMainSchema
				.getCheckType());
		mLLClaimUWMDetailSchema.setAppPhase(tLLClaimUWMainSchema.getAppPhase());// 申请阶段
																				// 复核
		mLLClaimUWMDetailSchema.setAppActionType(tLLClaimUWMainSchema
				.getAppActionType());
		mLLClaimUWMDetailSchema.setOperator(mG.Operator);
		
		
		if(mG.OutUserFlag.equals("1"))   //外包用户
		{
			mLLClaimUWMDetailSchema.setMngCom(mOutUserMngCom);
		}
		else
		{
			mLLClaimUWMDetailSchema.setMngCom(mG.ManageCom);
		}
		mLLClaimUWMDetailSchema.setMakeDate(PubFun.getCurrentDate());
		mLLClaimUWMDetailSchema.setMakeTime(PubFun.getCurrentTime());
		mLLClaimUWMDetailSchema.setModifyDate(PubFun.getCurrentDate());
		mLLClaimUWMDetailSchema.setModifyTime(PubFun.getCurrentTime());

		mMMap.put(mLLClaimUWMDetailSchema, "INSERT");
		return true;
	}

	/**
	 * 签批退回处理
	 * @return
	 */
	private boolean dealSignBack() {
		// 更新分案信息
		LLCaseDB mLLCaseDB = new LLCaseDB();
		mLLCaseDB.setCaseNo(mCaseNo);
		if (!mLLCaseDB.getInfo()) {
			this.mErrors.copyAllErrors(mLLCaseDB.mErrors);
			return false;
		}
		mLLCaseSchema = mLLCaseDB.getSchema();
		
		System.out.println("上报队列进入标志:"+mUpFlag+"1".equals(mUpFlag));
		if("1".equals(mUpFlag)){         //从上报队列进入的，签批结论为不同意时
			mLLCaseSchema.setSigner("");
		}
		else{
		    mLLCaseSchema.setSigner(mG.Operator);// 签批人
		}
		mLLCaseSchema.setSignerDate(PubFun.getCurrentDate());// 签批日期
		mLLCaseSchema.setRgtState("05");// 退回审核公共队列
		mMMap.put(mLLCaseSchema, "UPDATE");

		// 更新案件核赔表llclaimuwmain
		LLClaimUWMainDB tLLClaimUWMainDB = new LLClaimUWMainDB();
		tLLClaimUWMainDB.setClmNo(mLLCaseDB.getCaseNo());
		boolean hasExists = false;
		hasExists = tLLClaimUWMainDB.getInfo();

		LLClaimUWMainSchema tLLClaimUWMainSchema = new LLClaimUWMainSchema();
		// 存在该案件的核赔信息
		if (hasExists) {
			tLLClaimUWMainSchema = tLLClaimUWMainDB.getSchema();
		} else {
			tLLClaimUWMainSchema.setClmNo(mLLCaseDB.getCaseNo());
			tLLClaimUWMainSchema.setRgtNo(mLLCaseDB.getRgtNo());
			tLLClaimUWMainSchema.setCaseNo(mLLCaseDB.getCaseNo());
			tLLClaimUWMainSchema.setMakeDate(PubFun.getCurrentDate());
			tLLClaimUWMainSchema.setMakeTime(PubFun.getCurrentTime());
		}
		tLLClaimUWMainSchema.setClmUWer(mG.Operator);
		tLLClaimUWMainSchema.setAppPhase("1");// 申请阶段 复核
		tLLClaimUWMainSchema.setAppActionType("2");// 申请动作 退回
		tLLClaimUWMainSchema.setcheckDecision2(mAuditConclusion);// 签批结论
		tLLClaimUWMainSchema.setRemark2(mAuditIdea);// 签批意见
		// tLLClaimUWMainSchema.setRemark(mSpecialRemark1);//备注
		tLLClaimUWMainSchema.setOperator(mG.Operator);
		
		if(mG.OutUserFlag.equals("1"))   //外包用户
		{
			tLLClaimUWMainSchema.setMngCom(mOutUserMngCom);
		}
		else
		{
			tLLClaimUWMainSchema.setMngCom(mG.ManageCom);
		}
		
		tLLClaimUWMainSchema.setModifyDate(PubFun.getCurrentDate());
		tLLClaimUWMainSchema.setModifyTime(PubFun.getCurrentTime());

		mMMap.put(tLLClaimUWMainSchema, "DELETE&INSERT");

		// 更新核赔轨迹表

		// 查询LLClaimUWMDetail核赔次数
		String strSQL = "";
		strSQL = " select Max(to_number(ClmUWNo)) from LLClaimUWMDetail where "
				+ " ClmNo='" + tLLClaimUWMainSchema.getClmNo() + "'";
		ExeSQL execsql = new ExeSQL();
		String tMaxNo = execsql.getOneValue(strSQL);
		if (tMaxNo.length() == 0) {
			tMaxNo = "1";
		} else {
			int tInt = Integer.parseInt(tMaxNo);
			tInt = tInt + 1;
			tMaxNo = String.valueOf(tInt);
		}

		mLLClaimUWMDetailSchema.setClmUWNo(tMaxNo);
		mLLClaimUWMDetailSchema.setRgtNo(tLLClaimUWMainSchema.getRgtNo());
		mLLClaimUWMDetailSchema.setCaseNo(tLLClaimUWMainSchema.getCaseNo());
		mLLClaimUWMDetailSchema.setClmNo(tLLClaimUWMainSchema.getClmNo());
		mLLClaimUWMDetailSchema.setClmUWer(tLLClaimUWMainSchema.getClmUWer());
		mLLClaimUWMDetailSchema.setClmUWGrade(tLLClaimUWMainSchema
				.getClmUWGrade());
		mLLClaimUWMDetailSchema.setClmDecision(tLLClaimUWMainSchema
				.getcheckDecision2());
		mLLClaimUWMDetailSchema.setRemark(tLLClaimUWMainSchema.getRemark2());
		mLLClaimUWMDetailSchema.setCheckType(tLLClaimUWMainSchema
				.getCheckType());
		mLLClaimUWMDetailSchema.setAppPhase(tLLClaimUWMainSchema.getAppPhase());// 申请阶段
																				// 复核
		mLLClaimUWMDetailSchema.setAppActionType(tLLClaimUWMainSchema
				.getAppActionType());
		mLLClaimUWMDetailSchema.setOperator(mG.Operator);
		
		if(mG.OutUserFlag.equals("1"))   //外包用户
		{
			mLLClaimUWMDetailSchema.setMngCom(mOutUserMngCom);
		}
		else
		{
			mLLClaimUWMDetailSchema.setMngCom(mG.ManageCom);
		}
		mLLClaimUWMDetailSchema.setMakeDate(PubFun.getCurrentDate());
		mLLClaimUWMDetailSchema.setMakeTime(PubFun.getCurrentTime());
		mLLClaimUWMDetailSchema.setModifyDate(PubFun.getCurrentDate());
		mLLClaimUWMDetailSchema.setModifyTime(PubFun.getCurrentTime());

		mMMap.put(mLLClaimUWMDetailSchema, "INSERT");

		return true;
	}

	/**
	 * 签批请示上级处理
	 * @return
	 */
	private boolean dealSignUp() {
		
	    String comCode = ""; //上报的机构
	    if(mG.OutUserFlag.equals("1"))   //外包用户
		{
			 comCode = mOutUserMngCom;
		}
		else
		{
			 comCode = mG.ManageCom;
		}
	    
	    boolean flag = false; 
		String strSQL = "";
		strSQL ="SELECT usercode FROM llclaimuser WHERE isleader='1' AND comcode='"+ comCode +"'";
		ExeSQL execsql = new ExeSQL();
		SSRS tSSRS = new SSRS();  
		tSSRS = execsql.execSQL(strSQL);
		System.out.println("ssrs======"+tSSRS.getMaxRow()+"==="+(tSSRS.getMaxRow() <= 0));
		if(tSSRS.getMaxRow() <= 0)      //本级机构没找到部门主管
		{
			System.out.println("找上级机构领导============");
			flag = false;
			int strlength = comCode.length();
		    // 确保不是总公司层级
		    if (strlength > 2) {
		    	for (; strlength>2; strlength-=2) 
		    	{
		    		  if(mG.OutUserFlag.equals("1"))   //外包用户
		    		  {
		    			   comCode = mOutUserMngCom.substring(0, strlength - 2);
		    		  }
		    		  else
		    		  {
		    			   comCode = mG.ManageCom.substring(0, strlength - 2);
		    		  }
		    		  strSQL ="SELECT usercode FROM llclaimuser WHERE isleader='1' AND comcode='"+ comCode +"'";
		    		  tSSRS = execsql.execSQL(strSQL);
		    		  if(tSSRS.getMaxRow() <= 0)      //本级机构没找到部门主管
		    		  {
		    			  flag = false;
		    			  continue;
		    		  }
		    		  else
		    		  {
		    			  flag = true;
		    			  break; 
		    		  }	  
		    	}	
		    	
		    } 
		    else
		    {
		    	flag = false;
		    }	
		}
		else
		{
			 flag = true;
		}


		if(flag)
		{
		
			String tLeaderName = tSSRS.GetText(1, 1);
			System.out.println("com======="+comCode+"head======="+tLeaderName);
			mLLInfoSchema.setCaseNo(mCaseNo);
			
			if(mG.OutUserFlag.equals("1"))   //外包用户
  		  	{
  			    mLLInfoSchema.setSubComCode(mOutUserMngCom);
  		  	}
  		  	else
  		  	{
  		  	    mLLInfoSchema.setSubComCode(mG.ComCode);
  		  	}
			mLLInfoSchema.setName(mG.Operator);
			
			mLLInfoSchema.setSuperComCode(comCode);
			mLLInfoSchema.setLeaderName(tLeaderName);
			
			mLLInfoSchema.setCaseInfo(mAuditIdea);
			mLLInfoSchema.setIsRead("1");
			mLLInfoSchema.setMakeDate(PubFun.getCurrentDate());
			mMMap.put(mLLInfoSchema, "INSERT");
			return true;
		}
		else
		{
			 // @@错误处理
			  CError tError = new CError();
			  tError.moduleName = "LLClaimAuditBL";
			  tError.functionName = "dealSignUp";
			  tError.errorMessage = "未查询到相应的部门主管!";
			  this.mErrors.addOneError(tError);
			  return false;
			
		}
		
	}
	
	/**
	 * 签批上报处理
	 * @return
	 */
	private boolean dealSignReport() {
		//从私人队列返回到公共队列
		LWMissionDB tLWMissionDB = new LWMissionDB();
		LWMissionSchema tLWMissionSchema=new LWMissionSchema();
		tLWMissionDB.setMissionID(mMissionID);
		tLWMissionDB.setSubMissionID(mSubMissionID);
		tLWMissionDB.setActivityID(mActivityID);
		if (!tLWMissionDB.getInfo())
		{ 
			this.mErrors.copyAllErrors(tLWMissionDB.mErrors);
			CError tError = new CError();
	        tError.moduleName = "LLClaimSignBLService";
	        tError.functionName = "dealSignReport";
	        tError.errorMessage = "查询工作流节点完整信息时失败!";
	        this.mErrors.addOneError(tError);
	        return false;
		}
		tLWMissionSchema=tLWMissionDB.getSchema();
		tLWMissionSchema.setMissionProp6("");
		tLWMissionSchema.setModifyDate(PubFun.getCurrentDate());
		tLWMissionSchema.setModifyTime(PubFun.getCurrentTime());
		mMMap.put(tLWMissionSchema, "UPDATE");
		
		//更新签批意见和签批结论
		LLClaimUWMainDB tLLClaimUWMainDB = new LLClaimUWMainDB();
		LLClaimUWMainSchema tLLClaimUWMainSchema = new LLClaimUWMainSchema();
		tLLClaimUWMainDB.setClmNo(mCaseNo);  // 目前，llcase和llclaim是一一对应关系
		boolean hasExists = false;
		hasExists = tLLClaimUWMainDB.getInfo();
		if (!hasExists) {                      // 不存在该案件的核赔信息
			this.mErrors.copyAllErrors(tLLClaimUWMainDB.mErrors);
			CError tError = new CError();
	        tError.moduleName = "LLClaimSignBLService";
	        tError.functionName = "dealSignReport";
	        tError.errorMessage = "查询案件核赔表信息时失败!";
	        this.mErrors.addOneError(tError);
	        return false;
		} 
		tLLClaimUWMainSchema = tLLClaimUWMainDB.getSchema();
		tLLClaimUWMainSchema.setcheckDecision2(mAuditConclusion);// 签批结论
		tLLClaimUWMainSchema.setRemark2(mAuditIdea);// 签批意见
		// tLLClaimUWMainSchema.setRemark(mSpecialRemark1);//备注
		tLLClaimUWMainSchema.setOperator(mG.Operator);
		
		if(mG.OutUserFlag.equals("1"))   //外包用户
		{
			   tLLClaimUWMainSchema.setMngCom(mOutUserMngCom);
		}
		else
		{
			   tLLClaimUWMainSchema.setMngCom(mG.ManageCom);
		}
		tLLClaimUWMainSchema.setModifyDate(PubFun.getCurrentDate());
		tLLClaimUWMainSchema.setModifyTime(PubFun.getCurrentTime());
		mMMap.put(tLLClaimUWMainSchema, "UPDATE");
		
//		在llcase表中置上报标志
		LLCaseDB tLLCaseDB = new LLCaseDB();
		LLCaseSchema tLLCaseSchema = new LLCaseSchema();
		tLLCaseDB.setCaseNo(mCaseNo);
		if (!tLLCaseDB.getInfo())
		{ 
			this.mErrors.copyAllErrors(tLLCaseDB.mErrors);
			CError tError = new CError();
	        tError.moduleName = "LLClaimSignBLService";
	        tError.functionName = "dealSignReport";
	        tError.errorMessage = "查询分案信息时失败!";
	        this.mErrors.addOneError(tError);
	        return false;
		}
		tLLCaseSchema=tLLCaseDB.getSchema();
		tLLCaseSchema.setCaseProp3("1");      //上报标志
		mMMap.put(tLLCaseSchema, "UPDATE");
		return true;		
	}

    /**
     * 生成错误信息
     * @param szFunc
     * @param szErrMsg
     */
    private void buildError(String szFunc, String szErrMsg) {
        CError cError = new CError();
        cError.moduleName = "LLClaimGrpSignBL";
        cError.functionName = szFunc;
        cError.errorMessage = szErrMsg;
        this.mErrors.addOneError(cError);
      }

	public boolean prepareTransferData() {
		System.out.println("dddddddddd" + mAuditConclusion);
		mTransferData.setNameAndValue("CAFlag", mAuditConclusion);// 传往工作流的标志“1
																	// 2”
		mTransferData.setNameAndValue("RgtNo", mRgtNo);
		mTransferData.setNameAndValue("CaseNo", mCaseNo);
		mTransferData.setNameAndValue("MissionID", mMissionID);
		mTransferData.setNameAndValue("SubMissionID", mSubMissionID);
		mTransferData.setNameAndValue("ActivityID", mActivityID);
		
		if(mG.OutUserFlag.equals("1"))   //外包用户
		{
			  mTransferData.setNameAndValue("ManageCom", mOutUserMngCom);
		}
		else
		{
			  mTransferData.setNameAndValue("ManageCom", mMngCom);
		}
		mTransferData.setNameAndValue("Operator", mOperator);
		mTransferData.setNameAndValue("InputDate", PubFun.getCurrentDate());
		mTransferData.setNameAndValue("CustomerNo", mLLCaseSchema
				.getCustomerNo());
		mTransferData.setNameAndValue("CustomerName", mLLCaseSchema
				.getCustomerName());

		return true;
	}	
		
	public TransferData getReturnTransferData() {
		return mTransferData;
	}

	public CErrors getErrors() {
		return mErrors;
	}

	public VData getResult() {
		return mResult;
	}
}
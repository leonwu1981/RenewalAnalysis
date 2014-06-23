package com.sinosoft.workflow.ca;

import com.sinosoft.lis.claim.LLClaimNoRgtMailBL;
import com.sinosoft.lis.db.LDCodeDB;
import com.sinosoft.lis.db.LLCaseDB;
import com.sinosoft.lis.db.LLClaimDB;
import com.sinosoft.lis.db.LLRegisterDB;
import com.sinosoft.lis.db.LLClaimDetailDB;
import com.sinosoft.lis.db.LLClaimPolicyDB;
import com.sinosoft.lis.db.LLClaimUWMainDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.LLCaseSchema;
import com.sinosoft.lis.schema.LLClaimDetailSchema;
import com.sinosoft.lis.schema.LLClaimPolicySchema;
import com.sinosoft.lis.schema.LLClaimSchema;
import com.sinosoft.lis.schema.LLClaimUWMDetailSchema;
import com.sinosoft.lis.schema.LLClaimUWMainSchema;
import com.sinosoft.lis.schema.LLRegisterSchema;
import com.sinosoft.lis.vschema.LLClaimDetailSet;
import com.sinosoft.lis.vschema.LLClaimPolicySet;
import com.sinosoft.lis.vschema.LLClaimSet;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;
import com.sinosoft.workflowengine.BeforeInitService;

public class LLGrpClaimAuditBLService implements BeforeInitService {

	public CErrors mErrors = new CErrors();

	private String mOperate = "";

	private String mMissionID = "";

	private String mSubMissionID = "";

	private String mActivityID = "";

	private TransferData mTransferData = new TransferData();

	private GlobalInput mGlobalInput = new GlobalInput();

	private LLCaseSchema mInsLLCaseSchema = new LLCaseSchema();

	private LLClaimSchema mInsLLClaimSchema = new LLClaimSchema();

	private LLClaimDetailSet mInsLLClaimDetailSet = new LLClaimDetailSet();

	private LLRegisterSchema mInsLLRegisterSchema = new LLRegisterSchema();

	private String mRgtNo = "";

	private String mCaseNo = "";
	
	private String mOutUserMngCom="";

	private MMap mMMap = new MMap();

	private VData mResult = new VData();
	
	private ExeSQL mExeSQL = new ExeSQL();

	public LLGrpClaimAuditBLService() {
		super();
		// TODO 自动生成构造函数存根
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自动生成方法存根

	}

	public boolean submitData(VData cInputData, String cOperate) {
		if (!getInputData(cInputData)) {
			return false;
		}
		if (!dealData()) {
			return false;
		}
		return true;
	}

	public boolean getInputData(VData cInputData) {
		mTransferData = (TransferData) cInputData.getObjectByObjectName(
				"TransferData", 0);
		mGlobalInput = (GlobalInput) cInputData.getObjectByObjectName(
				"GlobalInput", 0);
		mMissionID = (String) mTransferData.getValueByName("MissionID");
		mSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
		mActivityID = (String) mTransferData.getValueByName("ActivityID");
		mOperate = (String) mTransferData.getValueByName("Operate");
		mRgtNo = (String) mTransferData.getValueByName("RgtNo");
		mCaseNo = (String) mTransferData.getValueByName("CaseNo");
		if(mGlobalInput.OutUserFlag.equals("1"))  //外包用户
		{
//			String str = " select managecom from lcgrpcont where grpcontno=(select grpcontno from llregister where rgtno='"+mRgtNo+"')";
//			ExeSQL mExeSQL = new ExeSQL();
//			SSRS mSSRS = mExeSQL.execSQL(str);
//			if(mSSRS.getMaxRow()>0)
//			{
//				mOutUserMngCom =mSSRS.GetText(1, 1) ;
//			}
//			else
//			{
//				CError tError = new CError();
//				tError.moduleName = "LLGrpClaimAuditBLService";
//				tError.functionName = "getInputData";
//				tError.errorMessage = "查询外包用户保单管理机构信息失败!";
//				this.mErrors.addOneError(tError);
//				return false;
//			}
//			if(mGlobalInput.ManageCom.length()==8)
//			{
			  mOutUserMngCom=mGlobalInput.ManageCom;
//			}
//			else
//			{
//				CError tError = new CError();
//				tError.moduleName = "LLGrpClaimAuditBLService";
//				tError.functionName = "getInputData";
//				tError.errorMessage = "请使用八位机构登录！";
//				this.mErrors.addOneError(tError);
//				return false;
//			}
		}
		return true;
	}

	public boolean dealData() {
		//如果是对一个分案下结论
		if (mOperate != null && mOperate.equals("AUDITCASE")) {
			String tRgtRemark = (String) mTransferData
			.getValueByName("RgtRemark");
			String tRgtConclusion = (String) mTransferData
					.getValueByName("RgtConclusion");
			String tNoRgtReason = (String) mTransferData
					.getValueByName("NoRgtReason");
			String tDeclineReasonType=(String) mTransferData.getValueByName("DeclineReasonType");
			String tCancleReason = (String) mTransferData.getValueByName("CancleReason");
		    
			System.out.println("分案撤案原因:"+tCancleReason);
			LLCaseDB tLLCaseDB = new LLCaseDB();
			tLLCaseDB.setCaseNo(mCaseNo);
			if (!tLLCaseDB.getInfo()) {
				this.mErrors.copyAllErrors(tLLCaseDB.mErrors);
				return false;
			}
			mInsLLCaseSchema = tLLCaseDB.getSchema();
			mInsLLCaseSchema.setUWer(mGlobalInput.Operator);      //审核人
			mInsLLCaseSchema.setUWDate(PubFun.getCurrentDate());   //审核日期
			mInsLLCaseSchema.setUWState(tRgtConclusion);
			if(tRgtConclusion != null && tRgtConclusion.equals("6"))//退回受理
			{
				mInsLLCaseSchema.setRgtState("01");
			}
			else if(tRgtConclusion != null 
					&& (tRgtConclusion.equals("1")||tRgtConclusion.equals("3")))//建议给付、建议拒付
			{
				mInsLLCaseSchema.setRgtState("06");
			}			
//			else if(tRgtConclusion != null && tRgtConclusion.equals("7"))//撤案
			else if(tRgtConclusion != null && (tRgtConclusion.equals("7") || "8".equals(tRgtConclusion)) )//撤案
			{
				if ( "8".equals(tRgtConclusion) ){
					mInsLLCaseSchema.setRgtState("16");
					//如果该总案下其他所有分案都是撤案状态，则将其总案的状态也置为撤案状态
					String strSQL = "select count(*) from llcase where rgtno='"
							+ mInsLLCaseSchema.getRgtNo() + "' and caseno<>'"
							+ mInsLLCaseSchema.getCaseNo() + "' and rgtstate<>'16'";	
					String tCount = mExeSQL.getOneValue(strSQL);
					if("0".equals(tCount))
					{
						LLRegisterDB tLLRegisterDB = new LLRegisterDB();
						tLLRegisterDB.setRgtNo(mInsLLCaseSchema.getRgtNo());
						if(!tLLRegisterDB.getInfo())
						{
							CError tError = new CError();
							tError.moduleName = "LLGrpClaimAuditBLService";
							tError.functionName = "dealData";
							tError.errorMessage = "查询立案总表失败!";
							this.mErrors.addOneError(tError);
						}
						tLLRegisterDB.setRgtState("16");
						mMMap.put(tLLRegisterDB.getSchema(), "UPDATE");	
					}				
				}else{
					mInsLLCaseSchema.setRgtState("14");
					//如果该总案下其他所有分案都是撤案状态，则将其总案的状态也置为撤案状态
					String strSQL = "select count(*) from llcase where rgtno='"
							+ mInsLLCaseSchema.getRgtNo() + "' and caseno<>'"
							+ mInsLLCaseSchema.getCaseNo() + "' and rgtstate<>'14'";	
					String tCount = mExeSQL.getOneValue(strSQL);
					if("0".equals(tCount))
					{
						LLRegisterDB tLLRegisterDB = new LLRegisterDB();
						tLLRegisterDB.setRgtNo(mInsLLCaseSchema.getRgtNo());
						if(!tLLRegisterDB.getInfo())
						{
							CError tError = new CError();
							tError.moduleName = "LLGrpClaimAuditBLService";
							tError.functionName = "dealData";
							tError.errorMessage = "查询立案总表失败!";
							this.mErrors.addOneError(tError);
						}
						tLLRegisterDB.setRgtState("14");
						mMMap.put(tLLRegisterDB.getSchema(), "UPDATE");	
					}
				}
			}
			if ( "8".equals(tRgtConclusion) ){
				LLClaimNoRgtMailBL lLClaimNoRgtMailBL = new LLClaimNoRgtMailBL();
				if ( lLClaimNoRgtMailBL.submitData(mCaseNo, tRgtRemark) ){
					mInsLLCaseSchema.setNREamil("y");
				}else{
					mInsLLCaseSchema.setNREamil("n");
				}
			}

			mMMap.put(mInsLLCaseSchema, "UPDATE");

			LLClaimDB tLLClaimDB = new LLClaimDB();
			tLLClaimDB.setCaseNo(mCaseNo);
			LLClaimSet tLLClaimSet = new LLClaimSet();
			tLLClaimSet = tLLClaimDB.query();
			if (tLLClaimSet == null || tLLClaimSet.size() < 1) {
				CError tError = new CError();
				tError.moduleName = "LLGrpClaimAuditBL";
				tError.functionName = "dealData";
				tError.errorMessage = "未查询到相应的赔付明细表数据!";
				this.mErrors.addOneError(tError);
			}

			LLClaimSchema tLLClaimSchema = new LLClaimSchema();
			tLLClaimSchema = tLLClaimSet.get(1);//目前，llcase和llclaim是一一对应关系
			tLLClaimSchema.setClmState("1");
			if(tLLClaimSchema.getGiveType() != null && !tLLClaimSchema.getGiveType().equals(""))
			{
				tLLClaimSchema.setCheckType("1");
			}
			else
			{
				tLLClaimSchema.setCheckType("0");
			}
			
			tLLClaimSchema.setModifyDate(PubFun.getCurrentDate());
			tLLClaimSchema.setModifyTime(PubFun.getCurrentTime());
			tLLClaimSchema.setOperator(mGlobalInput.Operator);
			LDCodeDB tLDCodeDB = new LDCodeDB();
			tLDCodeDB.setCodeType("llgrpclaimdecision");
			tLDCodeDB.setCode(tRgtConclusion);
			tLDCodeDB.getInfo();			
			tLLClaimSchema.setGiveType(tRgtConclusion);
			tLLClaimSchema.setGiveTypeDesc(tLDCodeDB.getCodeName());
			tLLClaimSchema.setClmUWer(mGlobalInput.Operator);
			// 拒付时把实付金额置为0
			if (tRgtConclusion != null && tRgtConclusion.equals("3")) {
				tLLClaimSchema.setRealPay(0);
			}
			mMMap.put(tLLClaimSchema, "UPDATE");

			
			//更新案件核赔表llclaimuwmain
			LLClaimUWMainDB tLLClaimUWMainDB = new LLClaimUWMainDB();
			tLLClaimUWMainDB.setClmNo(tLLCaseDB.getCaseNo());
			boolean hasExists = false;
			hasExists = tLLClaimUWMainDB.getInfo();			
			
			LLClaimUWMainSchema tLLClaimUWMainSchema = new LLClaimUWMainSchema();
			//存在该案件的核赔信息
			if(hasExists)
			{
				tLLClaimUWMainSchema = tLLClaimUWMainDB.getSchema();		
			}
			else
			{
				tLLClaimUWMainSchema.setClmNo(tLLCaseDB.getCaseNo());
				tLLClaimUWMainSchema.setRgtNo(tLLCaseDB.getRgtNo());
				tLLClaimUWMainSchema.setCaseNo(tLLCaseDB.getCaseNo());
				tLLClaimUWMainSchema.setMakeDate(PubFun.getCurrentDate());
				tLLClaimUWMainSchema.setMakeTime(PubFun.getCurrentTime());
			}				
			tLLClaimUWMainSchema.setClmUWer(mGlobalInput.Operator);
			tLLClaimUWMainSchema.setAppPhase("0");//申请阶段 审核
			tLLClaimUWMainSchema.setCheckType(tLLClaimSchema.getCheckType());
			tLLClaimUWMainSchema.setClmDecision(tRgtConclusion);
			tLLClaimUWMainSchema.setRemark(tNoRgtReason);
			tLLClaimUWMainSchema.setRemark1(tRgtRemark);//审核意见
			tLLClaimUWMainSchema.setDeclineReasonType(tDeclineReasonType);
			tLLClaimUWMainSchema.setCancleReason(tCancleReason); //撤案原因
				
			tLLClaimUWMainSchema.setOperator(mGlobalInput.Operator);
			
			if(mGlobalInput.OutUserFlag.equals("1"))   //外包用户
			{
				tLLClaimUWMainSchema.setMngCom(mOutUserMngCom);
			}
			else
			{
				tLLClaimUWMainSchema.setMngCom(mGlobalInput.ManageCom);
			}
			
			tLLClaimUWMainSchema.setModifyDate(PubFun.getCurrentDate());
			tLLClaimUWMainSchema.setModifyTime(PubFun.getCurrentTime());

			mMMap.put(tLLClaimUWMainSchema, "DELETE&INSERT");

			//更新核赔轨迹表

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
			
			LLClaimUWMDetailSchema tLLClaimUWMDetailSchema = new LLClaimUWMDetailSchema();
			tLLClaimUWMDetailSchema.setClmUWNo(tMaxNo);
			tLLClaimUWMDetailSchema.setRgtNo(tLLClaimUWMainSchema.getRgtNo());
			tLLClaimUWMDetailSchema.setCaseNo(tLLClaimUWMainSchema.getCaseNo());
			tLLClaimUWMDetailSchema.setClmNo(tLLClaimUWMainSchema.getClmNo());
			tLLClaimUWMDetailSchema.setClmUWer(tLLClaimUWMainSchema.getClmUWer());
			tLLClaimUWMDetailSchema.setClmUWGrade(tLLClaimUWMainSchema.getClmUWGrade());
			tLLClaimUWMDetailSchema.setClmDecision(tLLClaimUWMainSchema.getClmDecision());
			tLLClaimUWMDetailSchema.setClmType(tLLClaimUWMainSchema.getRemark());//暂借该字段存拒付原因
			tLLClaimUWMDetailSchema.setCheckType(tLLClaimUWMainSchema.getCheckType());
			tLLClaimUWMDetailSchema.setAppPhase(tLLClaimUWMainSchema.getAppPhase());
			tLLClaimUWMDetailSchema.setAppActionType(tLLClaimUWMainSchema.getAppActionType());
			tLLClaimUWMDetailSchema.setRemark(tLLClaimUWMainSchema.getRemark1());
			tLLClaimUWMDetailSchema.setDeclineReasonType(tLLClaimUWMainSchema.getDeclineReasonType());
			tLLClaimUWMDetailSchema.setOperator(mGlobalInput.Operator);
			
			if(mGlobalInput.OutUserFlag.equals("1"))   //外包用户
			{
				tLLClaimUWMDetailSchema.setMngCom(mOutUserMngCom);
			}
			else
			{
				tLLClaimUWMDetailSchema.setMngCom(mGlobalInput.ManageCom);
			}
			
			tLLClaimUWMDetailSchema.setMakeDate(PubFun.getCurrentDate());
			tLLClaimUWMDetailSchema.setMakeTime(PubFun.getCurrentTime());
			tLLClaimUWMDetailSchema.setModifyDate(PubFun.getCurrentDate());
			tLLClaimUWMDetailSchema.setModifyTime(PubFun.getCurrentTime());

			mMMap.put(tLLClaimUWMDetailSchema, "INSERT");
			

			if (tRgtConclusion != null && tRgtConclusion.equals("3")) {
				LLClaimDetailDB tLLClaimDetailDB = new LLClaimDetailDB();
				LLClaimDetailSet tLLClaimDetailSet = new LLClaimDetailSet();
				tLLClaimDetailDB.setCaseNo(tLLClaimDB.getCaseNo());
				tLLClaimDetailSet = tLLClaimDetailDB.query();
				for (int i = 1; i <= tLLClaimDetailSet.size(); i++) {
					LLClaimDetailSchema tLLClaimDetailSchema = new LLClaimDetailSchema();
					tLLClaimDetailSchema = tLLClaimDetailSet.get(i);
					tLLClaimDetailSchema.setDeclineAmnt(tLLClaimDetailSchema
							.getRealPay());
					tLLClaimDetailSchema.setRealPay(0);
					mMMap.put(tLLClaimDetailSchema, "DELETE&INSERT");
				}

				LLClaimPolicyDB tLLClaimPolicyDB = new LLClaimPolicyDB();
				LLClaimPolicySet tLLClaimPolicySet = new LLClaimPolicySet();
				tLLClaimPolicyDB.setCaseNo(tLLClaimDB.getCaseNo());
				tLLClaimPolicySet = tLLClaimPolicyDB.query();
				for (int i = 1; i <= tLLClaimPolicySet.size(); i++) {
					LLClaimPolicySchema tLLClaimPolicySchema = new LLClaimPolicySchema();
					tLLClaimPolicySchema = tLLClaimPolicySet.get(i);
					tLLClaimPolicySchema.setRealPay(0);
					tLLClaimPolicySchema.setPayType("3");
					tLLClaimPolicySchema.setGiveType("3");
					mMMap.put(tLLClaimPolicySchema, "DELETE&INSERT");
				}

			}
		}

		mResult.add(mMMap);
		return true;
	}

	public TransferData getReturnTransferData() {
		return this.mTransferData;
	}

	public CErrors getErrors() {
		return mErrors;
	}

	public VData getResult() {
		return mResult;
	}
}

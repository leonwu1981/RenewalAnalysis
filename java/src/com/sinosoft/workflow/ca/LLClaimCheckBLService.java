package com.sinosoft.workflow.ca;

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
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;
import com.sinosoft.workflowengine.BeforeInitService;

public class LLClaimCheckBLService implements BeforeInitService {

	public CErrors mErrors = new CErrors();

	private String mOperate = "";

	private TransferData mTransferData = new TransferData();

	private GlobalInput mGlobalInput = new GlobalInput();

	private LLCaseSchema mInsLLCaseSchema = new LLCaseSchema();
	
	private LLRegisterSchema mInsLLRegisterSchema = new LLRegisterSchema();

	private String mCaseNo = "";

	private MMap mMMap = new MMap();

	private VData mResult = new VData();
	
	private ExeSQL mExeSQL = new ExeSQL();

	public LLClaimCheckBLService() {
		super();
		// TODO �Զ����ɹ��캯�����
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO �Զ����ɷ������

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
		mOperate = (String) mTransferData.getValueByName("Operate");
		mCaseNo = (String) mTransferData.getValueByName("CaseNo");
		return true;
	}

	public boolean dealData() {
		//����Ƕ�һ���ְ��½���
		if (mOperate != null && mOperate.equals("AUDITCASE")) {
			String tRgtRemark = (String) mTransferData.getValueByName("RgtRemark");
			String tRgtConclusion = (String) mTransferData.getValueByName("RgtConclusion");
			String tNoRgtReason = (String) mTransferData.getValueByName("NoRgtReason");
			String tDeclineReasonType = (String) mTransferData.getValueByName("DeclineReasonType");
			String tCancleReason = (String) mTransferData.getValueByName("CancleReason");  //����ԭ��
			System.out.println("����ԭ��:"+tCancleReason);
			LLCaseDB tLLCaseDB = new LLCaseDB();
			tLLCaseDB.setCaseNo(mCaseNo);
			if (!tLLCaseDB.getInfo()) {
				this.mErrors.copyAllErrors(tLLCaseDB.mErrors);
				return false;
			}			
			mInsLLCaseSchema = tLLCaseDB.getSchema();
			mInsLLCaseSchema.setUWer(mGlobalInput.Operator);     //�������
			mInsLLCaseSchema.setUWDate(PubFun.getCurrentDate());   //�������
			mInsLLCaseSchema.setUWState(tRgtConclusion);
			if(tRgtConclusion != null && tRgtConclusion.equals("6"))//�˻�����
			{
				mInsLLCaseSchema.setRgtState("01");
			}
			else if(tRgtConclusion != null 
					&& (tRgtConclusion.equals("1")||tRgtConclusion.equals("3")))//�������������ܸ�
			{
				mInsLLCaseSchema.setRgtState("06");
			}			
			else if(tRgtConclusion != null && tRgtConclusion.equals("7"))//����
			{
				mInsLLCaseSchema.setRgtState("14");	
			}
			
			LLRegisterDB tLLRegisterDB = new LLRegisterDB();
			tLLRegisterDB.setRgtNo(mInsLLCaseSchema.getRgtNo());
			if(!tLLRegisterDB.getInfo())
			{
				this.mErrors.copyAllErrors(tLLRegisterDB.mErrors);
				return false;
			}
			mInsLLRegisterSchema = tLLRegisterDB.getSchema();
			mInsLLRegisterSchema.setRgtState(mInsLLCaseSchema.getRgtState());
			mMMap.put(mInsLLRegisterSchema, "UPDATE");
			mMMap.put(mInsLLCaseSchema, "UPDATE");

			LLClaimDB tLLClaimDB = new LLClaimDB();
			tLLClaimDB.setCaseNo(mCaseNo);
			LLClaimSet tLLClaimSet = new LLClaimSet();
			tLLClaimSet = tLLClaimDB.query();
			if (tLLClaimSet == null || tLLClaimSet.size() < 1) {
				CError tError = new CError();
				tError.moduleName = "LLClaimCheckBLService";
				tError.functionName = "dealData";
				tError.errorMessage = "δ��ѯ����Ӧ���⸶��ϸ������!";
				this.mErrors.addOneError(tError);
			}

			LLClaimSchema tLLClaimSchema = new LLClaimSchema();
			tLLClaimSchema = tLLClaimSet.get(1);//Ŀǰ��llcase��llclaim��һһ��Ӧ��ϵ
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
			// �ܸ�ʱ��ʵ�������Ϊ0
			if (tRgtConclusion != null && tRgtConclusion.equals("3")) {
				tLLClaimSchema.setRealPay(0);
			}
			mMMap.put(tLLClaimSchema, "UPDATE");

			
			//���°��������llclaimuwmain
			LLClaimUWMainDB tLLClaimUWMainDB = new LLClaimUWMainDB();
			tLLClaimUWMainDB.setClmNo(tLLCaseDB.getCaseNo());
			boolean hasExists = false;
			hasExists = tLLClaimUWMainDB.getInfo();			
			
			LLClaimUWMainSchema tLLClaimUWMainSchema = new LLClaimUWMainSchema();
			//���ڸð����ĺ�����Ϣ
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
			tLLClaimUWMainSchema.setAppPhase("0");//����׶� ���
			tLLClaimUWMainSchema.setCheckType(tLLClaimSchema.getCheckType());
			tLLClaimUWMainSchema.setClmDecision(tRgtConclusion);
			tLLClaimUWMainSchema.setRemark(tNoRgtReason);
			tLLClaimUWMainSchema.setDeclineReasonType(tDeclineReasonType);
			tLLClaimUWMainSchema.setRemark1(tRgtRemark);//������
			tLLClaimUWMainSchema.setCancleReason(tCancleReason);   //����ԭ��
				
			tLLClaimUWMainSchema.setOperator(mGlobalInput.Operator);
			tLLClaimUWMainSchema.setMngCom(mGlobalInput.ManageCom);
			tLLClaimUWMainSchema.setModifyDate(PubFun.getCurrentDate());
			tLLClaimUWMainSchema.setModifyTime(PubFun.getCurrentTime());

			mMMap.put(tLLClaimUWMainSchema, "DELETE&INSERT");

			//���º���켣��

			// ��ѯLLClaimUWMDetail�������
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
			tLLClaimUWMDetailSchema.setClmType(tLLClaimUWMainSchema.getRemark());//�ݽ���ֶδ�ܸ�ԭ��
			tLLClaimUWMDetailSchema.setCheckType(tLLClaimUWMainSchema.getCheckType());
			tLLClaimUWMDetailSchema.setAppPhase(tLLClaimUWMainSchema.getAppPhase());
			tLLClaimUWMDetailSchema.setAppActionType(tLLClaimUWMainSchema.getAppActionType());
			tLLClaimUWMDetailSchema.setRemark(tLLClaimUWMainSchema.getRemark1());
			tLLClaimUWMDetailSchema.setDeclineReasonType(tLLClaimUWMainSchema.getDeclineReasonType());
			tLLClaimUWMDetailSchema.setOperator(mGlobalInput.Operator);
			tLLClaimUWMDetailSchema.setMngCom(mGlobalInput.ManageCom);
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

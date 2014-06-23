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
	/** �������࣬ÿ����Ҫ����������ж����ø��� */
	public CErrors mErrors = new CErrors();

	private TransferData mTransferData = new TransferData();

	private GlobalInput mG = new GlobalInput();

	private MMap mMMap = new MMap();

	private VData mResult = new VData();

	private VData mInputData = new VData();

	private String mRgtNo = "";

	private String mCaseNo = "";

	private String mMngCom = "";

	private String mOperate = "";// ������־

	private String mOperator = "";// ������Ա

	private String mMissionID = "";

	private String mSubMissionID = "";

	private String mActivityID = "";

	private String mAuditConclusion = "";// ǩ������

	private String mAuditIdea = "";// ǩ�����

	private String mSpecialRemark1 = "";// ��ע
	
	private String mUpFlag = "";  //1���ϱ����н���
	
	private String mGrpContNo ="";
	
	private String mOutUserMngCom ="";   //���������������������ã�

	private LLCaseSchema mLLCaseSchema = new LLCaseSchema();

	private LLClaimUWMDetailSchema mLLClaimUWMDetailSchema = new LLClaimUWMDetailSchema();
	
    private AccountManage mAccountManage = new AccountManage();    
    
    private LLInfoSchema  mLLInfoSchema = new LLInfoSchema();

	public LLClaimSignBLService() {
		super();
		// TODO �Զ����ɹ��캯�����
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
		mAuditConclusion = (String) mTransferData.getValueByName("CAFlag");// �ܰ�����
		mAuditIdea = (String) mTransferData.getValueByName("AuditIdea");
		mSpecialRemark1 = (String) mTransferData
				.getValueByName("SpecialRemark1");
		mMngCom = (String) mTransferData.getValueByName("ManageCom");
		mOperator = (String) mTransferData.getValueByName("Operator");
		mUpFlag = (String)mTransferData.getValueByName("UpFlag");   //1���ϱ����н���
		mGrpContNo = (String) mTransferData.getValueByName("GrpContNo");
		if("".equals(mGrpContNo))
		{
			CError tError = new CError();
			tError.moduleName = "LLClaimGrpSignBL";
			tError.functionName = "checkData";
			tError.errorMessage = "����ǩ����ȡ������ʧ��!";
			this.mErrors.addOneError(tError);
			return false;
		}
		if("".equals(mAuditConclusion))
		{
			CError tError = new CError();
			tError.moduleName = "LLClaimGrpSignBL";
			tError.functionName = "checkData";
			tError.errorMessage = "����ǩ����ȡǩ������ʧ��!";
			this.mErrors.addOneError(tError);
			return false;
		}
		System.out.println("���������ţ�"+mGrpContNo);
		if(mG.OutUserFlag.equals("1"))  //����û�
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
//				buildError("getInputData", "��ѯ����û��������������Ϣʧ�ܣ�");
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
//				tError.errorMessage = "��ʹ�ð�λ������¼��";
//				this.mErrors.addOneError(tError);
//				return false;
//			}
		}
		return true;
	}

	public boolean dealData() {
		if (mOperate != null && mOperate.equals("SIGNCASE")) {
			// ǩ�������ͬ��
			if (mAuditConclusion != null && mAuditConclusion.equals("1")) {
				if (!dealSignPass()) {
					return false;
				}
			}

			// ǩ���������ͬ��
			if (mAuditConclusion != null && mAuditConclusion.equals("2")) {
				if (!dealSignBack()) {
					return false;
				}

			}
			// ǩ���������ʾ�ϼ�
			if (mAuditConclusion != null && mAuditConclusion.equals("3")) {
				if (!dealSignUp()) {
					return false;
				}
			}
            //  ǩ��������ϱ�
			if (mAuditConclusion != null && mAuditConclusion.equals("4")) {
				if (!dealSignReport()) {
					return false;
				}
			}
			mResult.add(mMMap);
		}
		if (mOperate != null && mOperate.equals("WB_SIGNCASE")) {
			// ǩ���������ͬ��
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
	 * ǩ��ͨ������
	 * @return
	 */
	private boolean dealSignPass() {
		// �·ְ���Ϣ
		LLCaseDB mLLCaseDB = new LLCaseDB();
		mLLCaseDB.setCaseNo(mCaseNo);
		if (!mLLCaseDB.getInfo()) {
			this.mErrors.copyAllErrors(mLLCaseDB.mErrors);
			return false;
		}
		mLLCaseSchema = mLLCaseDB.getSchema();
		mLLCaseSchema.setSigner(mG.Operator);// ǩ����
		mLLCaseSchema.setSignerDate(PubFun.getCurrentDate());// ǩ������
		mLLCaseSchema.setRgtState("09");// ǩ��ͨ��
		
		//add start by ASR20094866 ϵͳ�ʼ������źʹ�ӡ֪ͨ�鴥��������޸� 
		if (mLLCaseSchema.getUWState().equals("3")){
			mLLCaseSchema.setNoticeWay("2");
		}
		//add end by ASR20094866 ϵͳ�ʼ������źʹ�ӡ֪ͨ�鴥��������޸� 
		
		if(mG.OutUserFlag.equals("1"))   //����û�
		{
			mLLCaseSchema.setCaseProp4("1");// ���������־
		}
		else
		{
			mLLCaseSchema.setCaseProp4("");// ���˰�����ǩ�����
		}
		mMMap.put(mLLCaseSchema, "UPDATE");

		// �����ⰸ����Ϣ
		LLClaimDB tLLClaimDB = new LLClaimDB();
		tLLClaimDB.setCaseNo(mCaseNo);
		LLClaimSet tLLClaimSet = new LLClaimSet();
		tLLClaimSet = tLLClaimDB.query();
		if (tLLClaimSet == null || tLLClaimSet.size() < 1) {
			CError tError = new CError();
			tError.moduleName = "LLClaimSignBL";
			tError.functionName = "dealData";
			tError.errorMessage = "δ��ѯ����Ӧ���⸶��ϸ������!";
			this.mErrors.addOneError(tError);
		}

		LLClaimSchema tLLClaimSchema = new LLClaimSchema();
		tLLClaimSchema = tLLClaimSet.get(1);// Ŀǰ��llcase��llclaim��һһ��Ӧ��ϵ
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

		// ���°��������llclaimuwmain
		LLClaimUWMainDB tLLClaimUWMainDB = new LLClaimUWMainDB();
		tLLClaimUWMainDB.setClmNo(mLLCaseDB.getCaseNo());
		boolean hasExists = false;
		hasExists = tLLClaimUWMainDB.getInfo();

		LLClaimUWMainSchema tLLClaimUWMainSchema = new LLClaimUWMainSchema();
		// ���ڸð����ĺ�����Ϣ
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
		tLLClaimUWMainSchema.setAppPhase("1");// ����׶� ����
		tLLClaimUWMainSchema.setAppActionType("1");// ���붯�� ȷ��
		tLLClaimUWMainSchema.setcheckDecision2(mAuditConclusion);// ǩ������
		tLLClaimUWMainSchema.setRemark2(mAuditIdea);// ǩ�����
		// tLLClaimUWMainSchema.setRemark(mSpecialRemark1);//��ע
		tLLClaimUWMainSchema.setOperator(mG.Operator);
		if(mG.OutUserFlag.equals("1"))   //����û�
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

		// ���º���켣��

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
		mLLClaimUWMDetailSchema.setAppPhase(tLLClaimUWMainSchema.getAppPhase());// ����׶�
																				// ����
		mLLClaimUWMDetailSchema.setAppActionType(tLLClaimUWMainSchema
				.getAppActionType());
		mLLClaimUWMDetailSchema.setOperator(mG.Operator);
		
		
		if(mG.OutUserFlag.equals("1"))   //����û�
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
	 * ǩ���˻ش���
	 * @return
	 */
	private boolean dealSignBack() {
		// ���·ְ���Ϣ
		LLCaseDB mLLCaseDB = new LLCaseDB();
		mLLCaseDB.setCaseNo(mCaseNo);
		if (!mLLCaseDB.getInfo()) {
			this.mErrors.copyAllErrors(mLLCaseDB.mErrors);
			return false;
		}
		mLLCaseSchema = mLLCaseDB.getSchema();
		
		System.out.println("�ϱ����н����־:"+mUpFlag+"1".equals(mUpFlag));
		if("1".equals(mUpFlag)){         //���ϱ����н���ģ�ǩ������Ϊ��ͬ��ʱ
			mLLCaseSchema.setSigner("");
		}
		else{
		    mLLCaseSchema.setSigner(mG.Operator);// ǩ����
		}
		mLLCaseSchema.setSignerDate(PubFun.getCurrentDate());// ǩ������
		mLLCaseSchema.setRgtState("05");// �˻���˹�������
		mMMap.put(mLLCaseSchema, "UPDATE");

		// ���°��������llclaimuwmain
		LLClaimUWMainDB tLLClaimUWMainDB = new LLClaimUWMainDB();
		tLLClaimUWMainDB.setClmNo(mLLCaseDB.getCaseNo());
		boolean hasExists = false;
		hasExists = tLLClaimUWMainDB.getInfo();

		LLClaimUWMainSchema tLLClaimUWMainSchema = new LLClaimUWMainSchema();
		// ���ڸð����ĺ�����Ϣ
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
		tLLClaimUWMainSchema.setAppPhase("1");// ����׶� ����
		tLLClaimUWMainSchema.setAppActionType("2");// ���붯�� �˻�
		tLLClaimUWMainSchema.setcheckDecision2(mAuditConclusion);// ǩ������
		tLLClaimUWMainSchema.setRemark2(mAuditIdea);// ǩ�����
		// tLLClaimUWMainSchema.setRemark(mSpecialRemark1);//��ע
		tLLClaimUWMainSchema.setOperator(mG.Operator);
		
		if(mG.OutUserFlag.equals("1"))   //����û�
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

		// ���º���켣��

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
		mLLClaimUWMDetailSchema.setAppPhase(tLLClaimUWMainSchema.getAppPhase());// ����׶�
																				// ����
		mLLClaimUWMDetailSchema.setAppActionType(tLLClaimUWMainSchema
				.getAppActionType());
		mLLClaimUWMDetailSchema.setOperator(mG.Operator);
		
		if(mG.OutUserFlag.equals("1"))   //����û�
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
	 * ǩ����ʾ�ϼ�����
	 * @return
	 */
	private boolean dealSignUp() {
		
	    String comCode = ""; //�ϱ��Ļ���
	    if(mG.OutUserFlag.equals("1"))   //����û�
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
		if(tSSRS.getMaxRow() <= 0)      //��������û�ҵ���������
		{
			System.out.println("���ϼ������쵼============");
			flag = false;
			int strlength = comCode.length();
		    // ȷ�������ܹ�˾�㼶
		    if (strlength > 2) {
		    	for (; strlength>2; strlength-=2) 
		    	{
		    		  if(mG.OutUserFlag.equals("1"))   //����û�
		    		  {
		    			   comCode = mOutUserMngCom.substring(0, strlength - 2);
		    		  }
		    		  else
		    		  {
		    			   comCode = mG.ManageCom.substring(0, strlength - 2);
		    		  }
		    		  strSQL ="SELECT usercode FROM llclaimuser WHERE isleader='1' AND comcode='"+ comCode +"'";
		    		  tSSRS = execsql.execSQL(strSQL);
		    		  if(tSSRS.getMaxRow() <= 0)      //��������û�ҵ���������
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
			
			if(mG.OutUserFlag.equals("1"))   //����û�
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
			 // @@������
			  CError tError = new CError();
			  tError.moduleName = "LLClaimAuditBL";
			  tError.functionName = "dealSignUp";
			  tError.errorMessage = "δ��ѯ����Ӧ�Ĳ�������!";
			  this.mErrors.addOneError(tError);
			  return false;
			
		}
		
	}
	
	/**
	 * ǩ���ϱ�����
	 * @return
	 */
	private boolean dealSignReport() {
		//��˽�˶��з��ص���������
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
	        tError.errorMessage = "��ѯ�������ڵ�������Ϣʱʧ��!";
	        this.mErrors.addOneError(tError);
	        return false;
		}
		tLWMissionSchema=tLWMissionDB.getSchema();
		tLWMissionSchema.setMissionProp6("");
		tLWMissionSchema.setModifyDate(PubFun.getCurrentDate());
		tLWMissionSchema.setModifyTime(PubFun.getCurrentTime());
		mMMap.put(tLWMissionSchema, "UPDATE");
		
		//����ǩ�������ǩ������
		LLClaimUWMainDB tLLClaimUWMainDB = new LLClaimUWMainDB();
		LLClaimUWMainSchema tLLClaimUWMainSchema = new LLClaimUWMainSchema();
		tLLClaimUWMainDB.setClmNo(mCaseNo);  // Ŀǰ��llcase��llclaim��һһ��Ӧ��ϵ
		boolean hasExists = false;
		hasExists = tLLClaimUWMainDB.getInfo();
		if (!hasExists) {                      // �����ڸð����ĺ�����Ϣ
			this.mErrors.copyAllErrors(tLLClaimUWMainDB.mErrors);
			CError tError = new CError();
	        tError.moduleName = "LLClaimSignBLService";
	        tError.functionName = "dealSignReport";
	        tError.errorMessage = "��ѯ�����������Ϣʱʧ��!";
	        this.mErrors.addOneError(tError);
	        return false;
		} 
		tLLClaimUWMainSchema = tLLClaimUWMainDB.getSchema();
		tLLClaimUWMainSchema.setcheckDecision2(mAuditConclusion);// ǩ������
		tLLClaimUWMainSchema.setRemark2(mAuditIdea);// ǩ�����
		// tLLClaimUWMainSchema.setRemark(mSpecialRemark1);//��ע
		tLLClaimUWMainSchema.setOperator(mG.Operator);
		
		if(mG.OutUserFlag.equals("1"))   //����û�
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
		
//		��llcase�������ϱ���־
		LLCaseDB tLLCaseDB = new LLCaseDB();
		LLCaseSchema tLLCaseSchema = new LLCaseSchema();
		tLLCaseDB.setCaseNo(mCaseNo);
		if (!tLLCaseDB.getInfo())
		{ 
			this.mErrors.copyAllErrors(tLLCaseDB.mErrors);
			CError tError = new CError();
	        tError.moduleName = "LLClaimSignBLService";
	        tError.functionName = "dealSignReport";
	        tError.errorMessage = "��ѯ�ְ���Ϣʱʧ��!";
	        this.mErrors.addOneError(tError);
	        return false;
		}
		tLLCaseSchema=tLLCaseDB.getSchema();
		tLLCaseSchema.setCaseProp3("1");      //�ϱ���־
		mMMap.put(tLLCaseSchema, "UPDATE");
		return true;		
	}

    /**
     * ���ɴ�����Ϣ
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
		mTransferData.setNameAndValue("CAFlag", mAuditConclusion);// �����������ı�־��1
																	// 2��
		mTransferData.setNameAndValue("RgtNo", mRgtNo);
		mTransferData.setNameAndValue("CaseNo", mCaseNo);
		mTransferData.setNameAndValue("MissionID", mMissionID);
		mTransferData.setNameAndValue("SubMissionID", mSubMissionID);
		mTransferData.setNameAndValue("ActivityID", mActivityID);
		
		if(mG.OutUserFlag.equals("1"))   //����û�
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
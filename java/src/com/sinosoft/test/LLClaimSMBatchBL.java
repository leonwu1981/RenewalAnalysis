package com.sinosoft.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.sinosoft.lis.bl.LCInsuredBL;
import com.sinosoft.lis.claim.ClaimNoticeBean;
import com.sinosoft.lis.claim.SynLCLBDutyBL;
import com.sinosoft.lis.claim.SynLCLBInsuredBL;
import com.sinosoft.lis.db.LCAddressDB;
import com.sinosoft.lis.db.LCClaimNoticeTemplateDB;
import com.sinosoft.lis.db.LCContPlanFactoryDB;
import com.sinosoft.lis.db.LCGrpContDB;
import com.sinosoft.lis.db.LDCodeDB;
import com.sinosoft.lis.db.LDComDB;
import com.sinosoft.lis.db.LLCaseDB;
import com.sinosoft.lis.db.LLCasePolicyDB;
import com.sinosoft.lis.db.LLClaimDB;
import com.sinosoft.lis.db.LLClaimDetailDB;
import com.sinosoft.lis.db.LLClaimUWMainDB;
import com.sinosoft.lis.db.LLRegisterDB;
import com.sinosoft.lis.db.LMDutyGetClmDB;
import com.sinosoft.lis.db.LMRiskDB;
import com.sinosoft.lis.db.LPInsuredDB;
import com.sinosoft.lis.pubfun.FDate;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubCalculator;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.pubfun.PubFun1;
import com.sinosoft.lis.pubfun.PubSubmit;
import com.sinosoft.lis.schema.LCAddressSchema;
import com.sinosoft.lis.schema.LCClaimNoticeTemplateSchema;
import com.sinosoft.lis.schema.LCContPlanFactorySchema;
import com.sinosoft.lis.schema.LCGrpContSchema;
import com.sinosoft.lis.schema.LCInsuredSchema;
import com.sinosoft.lis.schema.LJAGetSchema;
import com.sinosoft.lis.schema.LLCasePolicySchema;
import com.sinosoft.lis.schema.LLCaseSchema;
import com.sinosoft.lis.schema.LLClaimDetailSchema;
import com.sinosoft.lis.schema.LLClaimSchema;
import com.sinosoft.lis.schema.LLMailListSchema;
import com.sinosoft.lis.schema.LLRegisterSchema;
import com.sinosoft.lis.schema.LLSMListSchema;
import com.sinosoft.lis.vschema.LCContPlanFactorySet;
import com.sinosoft.lis.vschema.LCInsuredSet;
import com.sinosoft.lis.vschema.LDComSet;
import com.sinosoft.lis.vschema.LJAGetSet;
import com.sinosoft.lis.vschema.LLCasePolicySet;
import com.sinosoft.lis.vschema.LLCaseSet;
import com.sinosoft.lis.vschema.LLClaimDetailSet;
import com.sinosoft.lis.vschema.LLClaimSet;
import com.sinosoft.lis.vschema.LLClaimUWMainSet;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.ListTable;
import com.sinosoft.utility.SSRS;
import com.sinosoft.utility.StrTool;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;
import com.sinosoft.xreport.util.StringUtility;

/* ******************************************************************************************
 * Fang 2009-10-15 ASR20094568 ��ȫ��������ͻָ�������չ
 * Change1: ���ڲ��ֶ���ı������ⰸ�������ʼ��Ͷ��������޸�
 * ******************************************************************************************
 * Fang 2010-02-22 ASR20105662 S-�����޸�-�붳��״̬�������������޸�
 * Change2: �ʼ����Ÿ��ݹ㶫��ʽ�޸�
 * ******************************************************************************************
 * Fang 2010-03-03 ASR20105662 S-�����޸�-�붳��״̬�������������޸�
 * Change3: ���ݱ����˲��ֶ���ʱ��ȫѡ������������������ʾ
 * ******************************************************************************************
 * Fang 2011-06-14 ASR20118337 S-��������-�޸�Ƿ�ѱ��������ⶳ��Ĺ��� 
 * Change4: ���ֶ��� �����ʼ�������ģ��ͳһ
 * ******************************************************************************************
 */

public class LLClaimSMBatchBL {
	public CErrors mErrors = new CErrors();

	private TransferData mTransferData = new TransferData();

	private GlobalInput mGlobalInput = new GlobalInput();

	private LLCaseSchema mLLCaseSchema = new LLCaseSchema();

	private VData mResult = new VData();

	public MMap map = new MMap();

	private String mCurrentDate = PubFun.getCurrentDate();

	private String mSaveFlag;// ���mSaveFlagΪnull����ֵ����""����"0",��ʾ���ε�����Ҫ������,����,������,ֻ���ؽ��

	private String mOutUserMngCom = "";

	private String mGiveType = ""; // �潨�����1 ����ܸ�3

	private VData mInputData = new VData();

	private LLCaseDB mLLCaseDB = new LLCaseDB();

	private LCAddressDB mLCAddressDB = new LCAddressDB();

	private LLRegisterDB mLLRegisterDB = new LLRegisterDB();

	private LCInsuredBL mLCInsuredBL = new LCInsuredBL();

	private LJAGetSet mLJAGetSet = new LJAGetSet();

	private String templateMode;
	private boolean tFEFlag = false; // Added By Fang for Change1
	private String mailSubject = "�����������֪ͨ��";
	//add by winnie ASR20107519 
	private String logopath = "http://www.generalichina.com/image/emaillogo.jpg";
	//add by winnie �Ż�
	private ClaimNoticeBean tClaimNoticeBean=null;
	
	public LLClaimSMBatchBL() {
		// TODO �Զ����ɹ��캯�����
	}

	private boolean getInputData(VData cInputData) {
		mTransferData = (TransferData) cInputData.getObjectByObjectName(
				"TransferData", 0);

		mGlobalInput = (GlobalInput) cInputData.getObjectByObjectName(
				"GlobalInput", 0);

		mLLCaseSchema = (LLCaseSchema) cInputData.getObjectByObjectName(
				"LLCaseSchema", 0);

		mLJAGetSet = (LJAGetSet) cInputData.getObjectByObjectName("LJAGetSet",
				0);

		mSaveFlag = (String) mTransferData.getValueByName("SaveFlag");

		mOutUserMngCom = (String) mTransferData.getValueByName("OutUserMngCom"); // ����û������������

		mGiveType = (String) mTransferData.getValueByName("GiveType");

		if (mLLCaseSchema == null || mLLCaseSchema.getCaseNo() == null
				|| mLLCaseSchema.getCaseNo().equals("")) {
			buildError("getInputData", "���밸����Ϣ�İ����Ų���Ϊ��");
			return false;
		}
		return true;
	}

	public boolean submitData(VData cInputData, String cOperate) {
		mInputData = (VData) cInputData.clone();
		if (!getInputData(cInputData)) {
			return false;
		}

		if (!dealData()) {
			return false;
		}

		// ׼���������
		if (!prepareOutputData()) {
			return false;
		}

		if (mSaveFlag == null || mSaveFlag.trim().equals("")
				|| mSaveFlag.trim().equals("0")) {
			// �ύ����
			PubSubmit tPubSubmit = new PubSubmit();
			if (tPubSubmit.submitData(mInputData, cOperate)) {
				return true;
			} else {
				this.mErrors.copyAllErrors(tPubSubmit.mErrors);
				return false;
			}
		}
		return true;

	}

	private boolean dealData() {
		mLLCaseDB.setCaseNo(mLLCaseSchema.getCaseNo());
		if (!mLLCaseDB.getInfo()) {
			buildError("dealData", "��ѯ���˰�����ʧ�ܣ�");
			return false;
		}
		LLCasePolicyDB tLLCasePolicyDB = new LLCasePolicyDB();
		LLCasePolicySet tLLCasePolicySet = new LLCasePolicySet();
		LLCasePolicySchema tLLCasePolicySchema = new LLCasePolicySchema();
		tLLCasePolicyDB.setCaseNo(mLLCaseSchema.getCaseNo());
		tLLCasePolicySet = tLLCasePolicyDB.query();
		if (tLLCasePolicySet == null || tLLCasePolicySet.size() == 0) {
			buildError("dealData", "��ѯ�ְ�������ϸ��ʧ�ܣ�");
			return false;
		}
		tLLCasePolicySchema = tLLCasePolicySet.get(1);

		mLCInsuredBL.setInsuredNo(tLLCasePolicySchema.getInsuredNo());
		mLCInsuredBL.setContNo(tLLCasePolicySchema.getContNo());
		if (!mLCInsuredBL.getInfo()) {
			buildError("dealData", "��ѯ���˱������˱�ʧ�ܣ�");
			return false;
		}

		mLCAddressDB.setCustomerNo(mLCInsuredBL.getInsuredNo());
		mLCAddressDB.setAddressNo(mLCInsuredBL.getAddressNo());
		if (!mLCAddressDB.getInfo()) { // û�в�ѯ���������˵���Ϣ
			// buildError("dealData", "��ѯ���˿ͻ���ַ��ʧ�ܣ�");
			// return false;
			// ��������������Ϣ
			SynLCLBInsuredBL tSynLCLBInsuredBL = new SynLCLBInsuredBL();

			tSynLCLBInsuredBL.setGrpContNo(mLCInsuredBL.getGrpContNo());
			tSynLCLBInsuredBL.setInsuredNo(mLCInsuredBL.getMainInsuredNo());
			LCInsuredSet tLCInsuredSet = tSynLCLBInsuredBL.query();
			if (tLCInsuredSet == null || tLCInsuredSet.size() == 0) {
				buildError("dealData", "��ѯ������������Ϣʧ�ܣ�");
				return false;
			}
			mLCAddressDB = new LCAddressDB();
			mLCAddressDB.setCustomerNo(tLCInsuredSet.get(1).getInsuredNo());
			mLCAddressDB.setAddressNo(tLCInsuredSet.get(1).getAddressNo());
			if (!mLCAddressDB.getInfo()) {
				buildError("dealData", "��ѯ���˿ͻ���ַ��ʧ�ܣ�");
				return false;
			}

		}

		mLLRegisterDB.setRgtNo(mLLCaseDB.getRgtNo());
		if (!mLLRegisterDB.getInfo()) {
			buildError("dealData", "��ѯ����������ʧ�ܣ�");
			return false;
		}
		// Added By Fang for Change1
		ExeSQL mmExeSQL = new ExeSQL();
		String tFETypeSQL = "select (select count(1) from lccont where grpcontno='"
				+ mLCInsuredBL.getGrpContNo()
				+ "' and contno='"
				+ mLCInsuredBL.getContNo()
				+ "' and state = '0206')+(select count(1) from lbcont where grpcontno='"
				+ mLCInsuredBL.getGrpContNo()
				+ "' and contno='"
				+ mLCInsuredBL.getContNo() + "' and state = '0206') from dual";
		if (Integer.parseInt(mmExeSQL.getOneValue(tFETypeSQL)) > 0) { // �Ѿ������ֶ���
			tFEFlag = true;
		}
		// End for Change1

		if (!caseMailSMControl(mLLCaseSchema)) {
			System.out.println("û�����ɶ�����Ϣ");
		}

		return true;
	}

	/**
	 * ����mail��sm�߼�У�鷽�� У��ְ���״̬����������-05����������ʼ��Ĵ����������ǩ�����ʼ������Ŵ���
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	private boolean caseMailSMControl(LLCaseSchema mLLCaseSchema) {
		ExeSQL mExeSQL = new ExeSQL();
		String dieDateSql = "select 1 from llcase a, llsubreport b where a.caseno = '"
				+ mLLCaseSchema.getCaseNo()
				+ "' and a.customerno = b.customerno and b.diedate is not null";
		String dieDate = mExeSQL.getOneValue(dieDateSql);
		// �������ʱ�����ɶ��Žӿ���Ϣ
		if ("1".equals(mGiveType)) {
			if (null != dieDate && !"".equals(dieDate)) {
				System.out.println("����" + mLLCaseSchema.getCaseNo()
						+ "��������ʲ��������ʼ����ֻ����š�");
				return false;
			}
		}

		LLRegisterDB registerDB = new LLRegisterDB();
		registerDB.setRgtNo(mLLCaseSchema.getRgtNo());
		LLRegisterSchema registerSchema = registerDB.query().get(1);
		String templatePath = "";
		if ("05".equals(mLLCaseSchema.getRgtState())) {
			// ����֪ͨ�ʼ��Ĵ���
			mailSubject = "���������ʼ�֪ͨ";
			templatePath = readTemplate(registerSchema, mLLCaseSchema, "05",
					"email");
			if ("false".equals(templatePath)) {
				System.out.println("δ��ѯ�������ʼ������������ˣ�");
				return false;
			}
			if ("templateError".equals(templatePath)) {
				System.out.println("δ��ѯ����Ӧ��ģ����Ϣ��");
				return false;
			}
			try {
				InputStream is = new FileInputStream(templatePath);
				StringBuffer buffer = new StringBuffer();
				readToBuffer(buffer, is);
				System.out.println(buffer); // ������ buffer �е�����д����
				is.close();
				templateMode = buffer.toString();
			} catch (IOException e) {
				System.out.println(e + "-��ȡģ���ļ�ʧ�ܣ�");
				buildError("caseMailSMControl", "��ȡģ���ļ�ʧ�ܣ�");
				return false;
			}
			if (!dealMailList()) {
				System.out.println("û�������ʼ���Ϣ");
			}
		} else {
			if ("1".equals(registerSchema.getSingerSMFlag())) {
				templatePath = readTemplate(registerSchema, mLLCaseSchema,
						"09", "sm");
				if ("false".equals(templatePath)) {
					System.out.println("δ��ѯ�������ʼ������������ˣ�");
					return false;
				}
				if ("templateError".equals(templatePath)) {
					System.out.println("δ��ѯ����Ӧ��ģ����Ϣ��");
					return false;
				}
				try {
					InputStream is = new FileInputStream(templatePath);
					StringBuffer buffer = new StringBuffer();
					readToBuffer(buffer, is);
					//System.out.println(buffer); // ������ buffer �е�����д����
					is.close();
					templateMode = buffer.toString();
				} catch (IOException e) {
					System.out.println(e + "-��ȡģ���ļ�ʧ�ܣ�");
					buildError("caseMailSMControl", "��ȡģ���ļ�ʧ�ܣ�");
					return false;
				}
				if ("1".equals(mGiveType)) {
					if (!dealSMList()) {
						System.out.println("û�����ɶ�����Ϣ");
					}
				}
			}

			// �������������ܸ������ʼ��ӿ���Ϣ // ��ѯ����֪ͨ��ʽ���Ƿ���Ҫ�����ʼ� 1 ��only E-mail 2 ��E-mail
			// and Hard copy
//			String strNoticeWay = " select count(1) from dual where casenoticeway('"
//					+ mLLCaseSchema.getCaseNo() + "') in ('1','2') ";
//			String tNoticeWay = mExeSQL.getOneValue(strNoticeWay);
//			if (tNoticeWay != null && !tNoticeWay.equals("")
//					&& tNoticeWay.equals("1")) {
				mailSubject = "�����������֪ͨ��";
				templatePath = readTemplate(registerSchema, mLLCaseSchema,
						"09", "email");
				if ("false".equals(templatePath)) {
					System.out.println("δ��ѯ�������ʼ������������ˣ�");
					return false;
				}
				if ("templateError".equals(templatePath)) {
					System.out.println("δ��ѯ����Ӧ��ģ����Ϣ��");
					return false;
				}
				try {
					InputStream is = new FileInputStream(templatePath);
					StringBuffer buffer = new StringBuffer();
					readToBuffer(buffer, is);
					//System.out.println(buffer); // ������ buffer �е�����д����
					is.close();
					templateMode = buffer.toString();
				} catch (IOException e) {
					System.out.println(e + "-��ȡģ���ļ�ʧ�ܣ�");
					buildError("caseMailSMControl", "��ȡģ���ļ�ʧ�ܣ�");
					return false;
				}
				if (!dealMailList()) {
					System.out.println("û�������ʼ���Ϣ");
				}

//			} else {
//				System.out.println("����" + mLLCaseSchema.getCaseNo()
//						+ "����֪ͨ��ʽΪ:" + tNoticeWay + ",���������ʼ���Ϣ");
//			}

		}

		return true;
	}

	public String readTemplate(LLRegisterSchema registerSchema,
			LLCaseSchema mLLCaseSchema, String rgtState, String sendMethod) {
		LCClaimNoticeTemplateDB claimNoticeTemplateDB = new LCClaimNoticeTemplateDB();
		LCClaimNoticeTemplateSchema claimNoticeTemplateSchema = new LCClaimNoticeTemplateSchema();
		String path = getRealPath();

		SynLCLBInsuredBL tSynLCLBInsuredBL = new SynLCLBInsuredBL();
		tSynLCLBInsuredBL.setGrpContNo(registerSchema.getGrpContNo());
		// tSynLCLBInsuredBL.setInsuredNo("000168477588");

		tSynLCLBInsuredBL.setInsuredNo(mLLCaseSchema.getCustomerNo());

		LCInsuredSet tLCInsuredSet = tSynLCLBInsuredBL.query();
		if (tLCInsuredSet == null || tLCInsuredSet.size() == 0) {
			System.out.println("δ��ѯ�������ʼ������������ˣ�");
			return "false";
		}
		if (mLLCaseSchema.getRgtType().equals("1")){
			claimNoticeTemplateDB.setGrpContNo("86");			
		}else{
			claimNoticeTemplateDB.setGrpContNo(registerSchema.getGrpContNo());			
		}
		claimNoticeTemplateDB.setOrganComCode(tLCInsuredSet.get(1)
				.getOrganComCode());
		if ("05".equals(rgtState) && "email".equals(sendMethod)) {
			claimNoticeTemplateDB.setCaseStatus("R");
			claimNoticeTemplateDB.setEMFlag("1");
		} else if ("email".equals(sendMethod)) {
			claimNoticeTemplateDB.setCaseStatus("S");
			claimNoticeTemplateDB.setEMFlag("1");
		} else if ("sm".equals(sendMethod)) {
			claimNoticeTemplateDB.setCaseStatus("S");
			claimNoticeTemplateDB.setSMFlag("1");
		} else {
			return "templateError";
		}

		// �����֧������û�����ѯ�����ϵ�
		if (null == claimNoticeTemplateDB.query().get(1)) {
			claimNoticeTemplateDB.setOrganComCode("86");
		}
		// ���������Ҳû����ʹ��ϵͳĬ��ģ��
		if (null == claimNoticeTemplateDB.query().get(1)) {
			claimNoticeTemplateDB.setGrpContNo("86");
			claimNoticeTemplateDB.setOrganComCode("86");
		}
		if (null == claimNoticeTemplateDB.query().get(1)){
			return "templateError";
		}
		claimNoticeTemplateSchema = claimNoticeTemplateDB.query().get(1);
		return path + claimNoticeTemplateSchema.getTemplateFile();
	}

	public void readToBuffer(StringBuffer buffer, InputStream is)
			throws IOException {
		String line; // ��������ÿ�ж�ȡ������
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		line = reader.readLine(); // ��ȡ��һ��
		while (line != null) { // ��� line Ϊ��˵��������
			buffer.append(line); // ��������������ӵ� buffer ��
			buffer.append("\n"); // ��ӻ��з�
			line = reader.readLine(); // ��ȡ��һ��
		}
	}

	/**
	 * �����ʼ��ӿ���Ϣ
	 * 
	 * @return
	 */
	private boolean dealMailList() {
		String tGlphone = "";
		String tEMail = "";
		LCAddressSchema tLCAddressSchema = new LCAddressSchema();
		SynLCLBInsuredBL tSynLCLBInsuredBL = new SynLCLBInsuredBL();
		LCInsuredSchema tMainLCInsuredSchema = new LCInsuredSchema();// ����������
		tLCAddressSchema = mLCAddressDB.getSchema();
		if (tLCAddressSchema.getEMail() == null
				|| "".equals(tLCAddressSchema.getEMail())) {
			if ((mLCInsuredBL.getMainInsuredNo() != null)
					&& (!"".equals(mLCInsuredBL.getMainInsuredNo()))) {
				// �õ����������������ַ
				LCAddressDB tLCAddressDB = new LCAddressDB();
				tSynLCLBInsuredBL.setGrpContNo(mLCInsuredBL.getGrpContNo());
				tSynLCLBInsuredBL.setInsuredNo(mLCInsuredBL.getMainInsuredNo());
				LCInsuredSet tLCInsuredSet = tSynLCLBInsuredBL.query();
				if (tLCInsuredSet == null || tLCInsuredSet.size() == 0) {
					System.out.println("δ��ѯ�������ʼ������������ˣ�");
					return false;
				}
				tMainLCInsuredSchema = tLCInsuredSet.get(1);
				tLCAddressDB.setCustomerNo(tLCInsuredSet.get(1).getInsuredNo());
				tLCAddressDB.setAddressNo(tLCInsuredSet.get(1).getAddressNo());
				if (!tLCAddressDB.getInfo()) {
					System.out.println("��ѯ�����ʼ������������˵ĵ�ַ��ʧ�ܣ�");
					return false;
				}
				tLCAddressSchema = tLCAddressDB.getSchema();
				tEMail = tLCAddressSchema.getEMail();
				if ( tEMail == null ||"".equals(tEMail) ) {
					System.out.println("��ѯ�����ʼ������������˵ĵ�ַ��ʧ�ܣ�");
					return false;
				}
				if ((tLCAddressSchema.getPhone() != null)
						&& (!"".equals(tLCAddressSchema.getPhone()))) {
					tGlphone = tLCAddressSchema.getPhone();
				} else if ((tLCAddressSchema.getMobile() != null)
						&& (!"".equals(tLCAddressSchema.getMobile()))) {
					tGlphone = tLCAddressSchema.getMobile();
				}
			} else {
				System.out.println("�����ʼ���û�����������˵���Ϣ��");
				return false;
			}
		} else {
			tEMail = tLCAddressSchema.getEMail();
			if (tEMail == null ||"".equals(tEMail) ) {
				System.out.println("��ѯ�����ʼ������������˵ĵ�ַ��ʧ�ܣ�");
				return false;
			}
			tMainLCInsuredSchema = mLCInsuredBL.getSchema();
			// ��ѯ������������Ϣ
			tSynLCLBInsuredBL.setGrpContNo(mLCInsuredBL.getGrpContNo());
			tSynLCLBInsuredBL.setInsuredNo(mLCInsuredBL.getMainInsuredNo());
			LCInsuredSet tLCInsuredSet = tSynLCLBInsuredBL.query();
			if (tLCInsuredSet.size() > 0) {
				tMainLCInsuredSchema = tLCInsuredSet.get(1).getSchema();
			}

			if ((tLCAddressSchema.getPhone() != null)
					&& (!"".equals(tLCAddressSchema.getPhone()))) {
				tGlphone = tLCAddressSchema.getPhone();
			} else if ((tLCAddressSchema.getMobile() != null)
					&& (!"".equals(tLCAddressSchema.getMobile()))) {
				tGlphone = tLCAddressSchema.getMobile();
			}
		}
		Date today = new Date();
		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		ExeSQL mExeSQL = new ExeSQL();
		String tGlsrvbrch = "";
		double sumPay = 0.00;
		String tPreClaimMoney = "0";// ����������			
		LDComDB tLDComDB = new LDComDB();
		tLDComDB.setComCode(mLLCaseDB.getMngCom());
		if (!tLDComDB.getInfo()) {
			buildError("dealMailList", "��ѯldcom��ʧ�ܣ�");
			return false;
		}
		if ((tLDComDB.getInnerComCode() != null)
				&& (!tLDComDB.getInnerComCode().equals(""))) {
			tGlsrvbrch = tLDComDB.getInnerComCode().substring(1, 3);
		}

		String tGetDate = StringUtility.replace(mLLRegisterDB.getGetDate(),
				"-", "");
		double tGlrecvdate = Double.parseDouble(tGetDate);

		FDate fDate = new FDate();
		GregorianCalendar sCalendar = new GregorianCalendar();
		sCalendar.setTime(fDate.getDate(PubFun.getCurrentDate()));
		String tCurrentDate = sCalendar.get(Calendar.YEAR) + "��"
				+ (sCalendar.get(Calendar.MONTH) + 1) + "��"
				+ sCalendar.get(Calendar.DAY_OF_MONTH) + "��";
		String tRgtType=mLLCaseDB.getRgtType();
		if (tRgtType == null || tRgtType.equals("")) {
			System.out.println("�޷������������ⰸ������ͨ�ⰸ����������");
			return false;
		}
		if(tRgtType.equals("1")){
			//��ͨ�ⰸ
			LLClaimDetailSet tLLClaimDetailSet = new LLClaimDetailSet();
			LLClaimDetailDB tLLClaimDetailDB = new LLClaimDetailDB();
			tLLClaimDetailDB.setCaseNo(mLLCaseDB.getCaseNo());
			tLLClaimDetailSet = tLLClaimDetailDB.query();
			int detailNum = 0;
			String tDetailPay = "";
			if (tLLClaimDetailSet != null) {
				detailNum = tLLClaimDetailSet.size();
			}
			for (int i = 1; i <= detailNum; i++) {
				LLClaimDetailSchema tLLClaimDetailSchema = tLLClaimDetailSet.get(i);
				sumPay += tLLClaimDetailSchema.getRealPay();
				String tRiskName = "";
				String tGetDutyName = "";
				if (tLLClaimDetailSchema.getRealPay() > 0.00) {
					LMRiskDB tLMRiskDB = new LMRiskDB();
					tLMRiskDB.setRiskCode(tLLClaimDetailSchema.getRiskCode());
					if (tLMRiskDB.getInfo()) {
						tRiskName = tLMRiskDB.getRiskName();
					}

					LMDutyGetClmDB tLMDutyGetClmDB = new LMDutyGetClmDB();
					tLMDutyGetClmDB.setGetDutyCode(tLLClaimDetailSchema
							.getGetDutyCode());
					tLMDutyGetClmDB.setGetDutyKind(tLLClaimDetailSchema
							.getGetDutyKind());
					if (tLMDutyGetClmDB.getInfo()) {
						tGetDutyName = tLMDutyGetClmDB.getGetDutyName();
					}
					//modify by winnie ASR20107519 �ʼ�ģ����:�⸶���� �⸶����  �⸶���
					double getRate = 0;//�⸶����-
					String getRateIn="";//���⸶������ʾΪ�ٷֱȵķ�ʽ
					getRate = getPayRate(tLLClaimDetailSchema , mLLCaseSchema);
					ExeSQL tExeSQL=new ExeSQL();
					getRateIn=tExeSQL.getOneValue(" select '"+getRate+"' *100||'%' from dual ");
					tDetailPay += "<tr>" + "<td></td>"
					+ "<td>" + tGetDutyName
					+ "</td>" +"<td>" + getRateIn
					+ "</td>" +"<td>" + tLLClaimDetailSchema.getRealPay()
					+ "</td>" + "</tr>";
					//end modify 20101123
				}
			}

			int getPersonNum = 0;
			String tGetDetail = "";

			if (mLJAGetSet != null) {
				getPersonNum = mLJAGetSet.size();
			}
			for (int i = 1; i <= getPersonNum; i++) {
				LJAGetSchema tLJAGetSchema = mLJAGetSet.get(i);
				String tGetName = tLJAGetSchema.getDrawer();
				if (tGetName == null || tGetName.equals("")) {
					tGetName = tLJAGetSchema.getAccName();
				}
				String tGetMode = "";
				LDCodeDB tLDCodeDB = new LDCodeDB();
				tLDCodeDB.setCodeType("paymode");
				tLDCodeDB.setCode(tLJAGetSchema.getPayMode());
				if (tLDCodeDB.getInfo()) {
					tGetMode = tLDCodeDB.getCodeName();
				}
				String tBankInfo = "";
				String tAccNo = "";
				String tAccName = "";
				if ("4".equals(tLJAGetSchema.getPayMode())) { // ����ת��
					tBankInfo = tLJAGetSchema.getBankInfo();
					if(tBankInfo==null){
						tBankInfo="";
					}
					tAccNo = tLJAGetSchema.getBankAccNo();
					//add by winnie  ASR20106910 ���������˺�/PIR
					if(tAccNo!=null&&!"".equals(tAccNo)){
						int lenBA=tAccNo.length();
						if(lenBA>=8){
							tAccNo=tAccNo.substring(0,lenBA-8)+"******"+tAccNo.substring(lenBA-2, lenBA);
						}else{
							tAccNo="******";
						}
					}
					//end add 20100910
					tAccName = tLJAGetSchema.getAccName();
				}
				tGetDetail += "<tr>" + "<td></td>" + "<td>" + tGetName + "</td>"
						+ "<td>" + tGetMode + "</td>" + "<td>" + tBankInfo
						+ "</td>" + "<td>" + tAccNo + "</td>" + "<td>" + tAccName
						+ "</td>" + "</tr>";
			}

			String tRemark1 = "";
			LLClaimUWMainSet tLLClaimUWMainSet = new LLClaimUWMainSet();
			LLClaimUWMainDB tLLClaimUWMainDB = new LLClaimUWMainDB();
			tLLClaimUWMainDB.setCaseNo(mLLCaseDB.getCaseNo());
			tLLClaimUWMainSet = tLLClaimUWMainDB.query();
			if (tLLClaimUWMainSet != null && tLLClaimUWMainSet.size() > 0) {
				if (tLLClaimUWMainSet.get(1).getRemark1() != null) {
					tRemark1 += tLLClaimUWMainSet.get(1).getRemark1();
				}
			}
			LLClaimSet tLLClaimSet = new LLClaimSet();
			LLClaimDB tLLClaimDB = new LLClaimDB();
			tLLClaimDB.setCaseNo(mLLCaseDB.getCaseNo());
			tLLClaimSet = tLLClaimDB.query();
			if (tLLClaimSet != null && tLLClaimSet.size() > 0) {
				if (tLLClaimSet.get(1).getCalRule() != null) {
					tRemark1 += tLLClaimSet.get(1).getCalRule();
				}
			}
			// ��������
			LLRegisterDB registerDB = new LLRegisterDB();
			registerDB.setRgtNo(mLLCaseDB.getRgtNo());
			String applyDate = registerDB.query().get(1).getApplyDate();
			if (null == applyDate || "".equals(applyDate)) {
				applyDate = tCurrentDate;
			}
			LLRegisterSchema tLLRegisterSchema= registerDB.query().get(1);
			
			LCGrpContDB tLCGrpContDB = new LCGrpContDB();
			tLCGrpContDB.setGrpContNo(mLCInsuredBL.getGrpContNo());
			LCGrpContSchema tLCGrpContSchema=tLCGrpContDB.query().get(1);
			
			// ����������
			String SQL = "select llcase_preclaimmoney('" + mLLCaseDB.getCaseNo()
					+ "') from llcase where caseno='" + mLLCaseDB.getCaseNo() + "'";
			tPreClaimMoney = mExeSQL.getOneValue(SQL);

			// �ʵ��Էѣ��Ը���Ϣ,��ͨ�ⰸ���⴦��
			String feeDetail = getFeeDetail2();
			
			//ģ�������Ϣ
			//add by winnie ASR20107519 ����ģ����Ϣ
			String tActuGetNo="";
			if(getPersonNum>0){
				LJAGetSchema tLJAGetSchema = mLJAGetSet.get(1);
				tActuGetNo=tLJAGetSchema.getGetNoticeNo();
			}

	        String tOrganComCode=mLCInsuredBL.getOrganComCode();

	        String orgSQL="select GrpName from LCOrgan where grpcontno='"+mLCInsuredBL.getGrpContNo()+"' and OrganComCode='"+tOrganComCode+"'";
	        SSRS orgSSRS = mExeSQL.execSQL(orgSQL);
	        String tOrganComCodeName="";
	        if (orgSSRS != null && orgSSRS.getMaxRow() > 0) {
	            tOrganComCodeName=orgSSRS.GetText(1, 1);
	        }
	        
			templateMode = StringUtility.replace(templateMode, "$logopath", logopath);//logo��ַ
			templateMode=StringUtility.replace(templateMode, "$GrpContNo", mLCInsuredBL.getGrpContNo());//������ 
			String beginDate="";//�����ڼ���ʼ����
			String endDate="";//�����ڼ���ֹ����
	        String SQLDate =
	            "select cvalidate,enddate-1 from lcpol where polno=(select polno from"
	            + " llcasepolicy where caseno='" + mLLCaseDB.getCaseNo() + "')";
	        SSRS aSSRS = mExeSQL.execSQL(SQLDate);
	        if (aSSRS.getMaxRow() > 0) {
	        	beginDate = aSSRS.GetText(1, 1);
	        	endDate = aSSRS.GetText(1, 2);
	        }else{
	        	//ȡB�����Ч�գ�ʧЧ��
	        	String strSQL1 = "select EdorNo,contno,insuredno,polno,cvalidate from lbpol where polno in (select polno from LLCasePolicy where caseno='"+ mLLCaseDB.getCaseNo()+"')";
	        	aSSRS = mExeSQL.execSQL(strSQL1);
	        	if(aSSRS.getMaxRow() > 0)
	        	{
	        		beginDate = aSSRS.GetText(1, 5);
	        		String strSQL2 = "select EdorValiDate from LPEdorItem where edorno='"+aSSRS.GetText(1, 1)+"' and contno='"+aSSRS.GetText(1, 2)+"' and insuredno='"+aSSRS.GetText(1, 3)+"'";
	        		aSSRS = mExeSQL.execSQL(strSQL2);
	        		if(aSSRS.getMaxRow() > 0)
	        		{
	        			endDate = aSSRS.GetText(1, 1);
	        		}
	        	}
	        }
	        beginDate= getDate(beginDate, false);
	        templateMode = StringUtility.replace(templateMode, "$BeginDate",
	        		beginDate);
	        endDate= getDate(endDate, false);
	        templateMode = StringUtility.replace(templateMode, "$EndDate",
	        		endDate);//�����ڼ� $BeginDate - $EndDate
	        
	        templateMode = StringUtility.replace(templateMode, "$GrpName",
	        		tLCGrpContSchema.getGrpName());//����������
	        templateMode = StringUtility.replace(templateMode, "$RCustomerNo",
	        		tLCGrpContSchema.getAppntNo());//��λ�ͻ���
	        templateMode = StringUtility.replace(templateMode, "$OrganComCode",
	        		tOrganComCode);//��֧��������(ȡ�����˵ķ�֧����)
	        
	        templateMode = StringUtility.replace(templateMode, "$OrganComName",
	        		tOrganComCodeName);//��֧��������
	        
	        templateMode = StringUtility.replace(templateMode, "$ActuGetNo", tActuGetNo);//֧������ 
	        if(getPersonNum>0){
		        templateMode = StringUtility.replace(templateMode, "$BankInfo",mLJAGetSet.get(1).getBankInfo());//��������
		        String tBankAccNo=mLJAGetSet.get(1).getBankAccNo();
		        if(tBankAccNo!=null&&!tBankAccNo.equals("")){
		        	int lenBAN=tBankAccNo.length();
		  			if(lenBAN>=8){
		  				tBankAccNo=tBankAccNo.substring(0,lenBAN-8)+"******"+tBankAccNo.substring(lenBAN-2, lenBAN);
		  			}else{
		  				tBankAccNo="******";
		  			}
		        }
		        templateMode = StringUtility.replace(templateMode, "$BankAccNo",tBankAccNo);//ת�˺��� 
		        templateMode = StringUtility.replace(templateMode, "$AccName",mLJAGetSet.get(1).getAccName());//�ʻ������� 
	        }else{
	        	 templateMode = StringUtility.replace(templateMode, "$BankInfo","");//��������
			     templateMode = StringUtility.replace(templateMode, "$BankAccNo","");//ת�˺��� 
			     templateMode = StringUtility.replace(templateMode, "$AccName","");//�ʻ������� 
	        }

			//end 20101123
			templateMode = StringUtility.replace(templateMode, "$CaseNo", mLLCaseDB
					.getCaseNo());//�����
			templateMode = StringUtility.replace(templateMode, "$PreClaimMoney",
					tPreClaimMoney);//�ϼ�������
			templateMode = StringUtility.replace(templateMode, "$MainCustomerName",
					tMainLCInsuredSchema.getName());//��������������
			templateMode = StringUtility.replace(templateMode, "$MainCustomerNo",
					tMainLCInsuredSchema.getInsuredNo());
			//modify by winnie ASR20106910 �����������˵����֤��
			String tMIDNo=tMainLCInsuredSchema.getIDNo();
			if(tMainLCInsuredSchema.getIDType().equals("0")){
				if(tMIDNo!=null&&!"".equals(tMIDNo)){
					int lenMID=tMIDNo.length();
					if(lenMID>=8){
						tMIDNo=tMIDNo.substring(0,lenMID-8)+"******"+tMIDNo.substring(lenMID-2, lenMID);
					}else{
						tMIDNo="******";
					}
				}
			}
			templateMode = StringUtility.replace(templateMode, "$MainIDNo",
					tMIDNo);//��������
			//end modify 20100910
			templateMode = StringUtility.replace(templateMode, "$CustomerName",
					mLLCaseDB.getCustomerName());
			templateMode = StringUtility.replace(templateMode, "$CustomerNo",
					mLLCaseDB.getCustomerNo());
			//modify by winnie ASR20106910 ���γ����˵����֤��
			String tDIDNo=mLLCaseDB.getIDNo();
			if(mLLCaseDB.getIDType().equals("0")){
				if(tDIDNo!=null&&!tDIDNo.equals("")){
					int lenDID=tDIDNo.length();
					if(lenDID>=8){
						tDIDNo=tDIDNo.substring(0,lenDID-8)+"******"+tDIDNo.substring(lenDID-2, lenDID);
					}else{
						tDIDNo="******";
					}
				}
			}
			templateMode = StringUtility.replace(templateMode, "$IDNo", tDIDNo);
			//end modify 20100910
			templateMode = StringUtility.replace(templateMode, "$SumPay", String
					.valueOf(sumPay));
			templateMode = StringUtility.replace(templateMode, "$DetailPay",
					tDetailPay);
			templateMode = StringUtility
					.replace(templateMode, "$Remark1", tRemark1);
			templateMode = StringUtility.replace(templateMode, "$CurrentDate",
					tCurrentDate);
			templateMode = StringUtility.replace(templateMode, "$GetDetail",
					tGetDetail);
			templateMode = StringUtility.replace(templateMode, "$FeeDetail",
					feeDetail);
			templateMode = StringUtility.replace(templateMode, "$ApplyDate",
					applyDate);
			// Added By Fang for Change1
			String tRemark = "1������2-5�������պ���գ�2����ת�˳ɹ������Ӹ������յ��ñ�������3���������ʣ�����ϵ�������ٿͷ���Ա��";
			if (tFEFlag) {
				String mEdorReasonno2 = "";
//				String sqlContState = "select distinct aa.contno,aa.edorno,aa.edortype,bb.confdate,bb.conftime,cc.edorreasonno2,"
//						+ "(select codealias from ldcode where codetype='claimtemplat' and code=cc.edorreasonno2)"
//						+ " from lpcont aa,lpgrpedormain bb,lpgrpedoritem cc where aa.edorno=bb.edorno and bb.edorno=cc.edorno"
//						+ " and aa.edortype=cc.edortype and bb.edorstate='0' and aa.edortype='FE' and aa.contno='"
//						+ mLCInsuredBL.getContNo()
//						+ "' and bb.confdate<='"
//						+ mCurrentDate
//						+ "' order by bb.confdate desc, bb.conftime desc";
//				SSRS mSSRS_EdorReasonno2 = new SSRS();
//				mSSRS_EdorReasonno2 = mExeSQL.execSQL(sqlContState);
//				if (mSSRS_EdorReasonno2.getMaxRow() > 0) {
//					mEdorReasonno2 = mSSRS_EdorReasonno2.GetText(1, 7);
//				}
//				if (mEdorReasonno2 == null||mEdorReasonno2.equals("")  ) {
//					mEdorReasonno2 = mExeSQL
//							.getOneValue("select codealias from ldcode where codetype='claimtemplat' and comcode = substr('"
//									+ mLLCaseDB.getMngCom() + "',0,4)");
//				}
				//Amended By Fang for Change4(ͳһģ��)
				mEdorReasonno2 = mExeSQL.getOneValue("select codealias from ldcode where codetype='claimtemplat' and comcode='86'");
				tRemark = mEdorReasonno2;
			}
			templateMode = StringUtility.replace(templateMode, "$Remark", tRemark);
			// End
		}
		
		if(tRgtType.equals("2")){
			//�����ⰸ
			//	add by winnie �����������֪ͨ���Ż�
			tClaimNoticeBean=new ClaimNoticeBean(mLLCaseDB.getCaseNo());
			
			String tDetailPay = "";
			//�⸶��Ŀ
			String[][] tLLClaimDetailList=tClaimNoticeBean.getClaimDetailList();
			for (int i = 0; i < tLLClaimDetailList.length; i++) {
				String tGetDutyName = "";//�⸶����
				String getRateIn="";//���⸶������ʾΪ�ٷֱȵķ�ʽ
				String realpay="";//�⸶���
				tGetDutyName=tLLClaimDetailList[i][0];
				getRateIn=tLLClaimDetailList[i][1];
				realpay=tLLClaimDetailList[i][2];
				if(Double.parseDouble(realpay)>0.00){
					tDetailPay += "<tr>" + "<td></td>"
					+ "<td>" + tGetDutyName
					+ "</td>" +"<td>" + getRateIn
					+ "</td>" +"<td>" + realpay
					+ "</td>" + "</tr>";
				}
			}
			sumPay=Double.parseDouble(tClaimNoticeBean.getSumRealPay());//ʵ����
			int getPersonNum = 0;
			if (mLJAGetSet != null) {
				getPersonNum = mLJAGetSet.size();
			}

			String tRemark1 = "";
			
			//�������
			if(tClaimNoticeBean.getRemark1()!=null){
				tRemark1=tClaimNoticeBean.getRemark1();
			}
			if(tClaimNoticeBean.getCalRule()!=null){
				tRemark1+=tClaimNoticeBean.getCalRule();
			}

			// ����������
			tPreClaimMoney=tClaimNoticeBean.getSumFee();
			// �ʵ��Էѣ��Ը���Ϣ
			String feeDetail = getFeeDetail();
			
			//ģ�������Ϣ
			//add by winnie ASR20107519 ����ģ����Ϣ
			String tActuGetNo="";
			if(getPersonNum>0){
				LJAGetSchema tLJAGetSchema = mLJAGetSet.get(1);
				tActuGetNo=tLJAGetSchema.getGetNoticeNo();//֧������
			}

			String tOrganComCode=tClaimNoticeBean.getOrganComCode();//��֧��������
			String tOrganComCodeName=tClaimNoticeBean.getOrganComName();//��֧��������
			
			templateMode = StringUtility.replace(templateMode, "$logopath", logopath);//logo��ַ
		
			templateMode=StringUtility.replace(templateMode, "$GrpContNo", tClaimNoticeBean.getGrpContNo());//������ 
			String beginDate="";//�����ڼ���ʼ����
			String endDate="";//�����ڼ���ֹ����

	        beginDate= tClaimNoticeBean.getBeginDate();//��ʼ����
	        templateMode = StringUtility.replace(templateMode, "$BeginDate",
	        		beginDate);
	        endDate= tClaimNoticeBean.getEndDate();//��ֹ����
	        templateMode = StringUtility.replace(templateMode, "$EndDate",
	        		endDate);//�����ڼ� $BeginDate -- $EndDate
	        
	        templateMode = StringUtility.replace(templateMode, "$GrpName",
	        		tClaimNoticeBean.getGrpName());//����������

	        templateMode = StringUtility.replace(templateMode, "$RCustomerNo",
	        		tClaimNoticeBean.getAppntNo());//��λ�ͻ���
	        
	        templateMode = StringUtility.replace(templateMode, "$OrganComCode",
	        		tOrganComCode);//��֧��������(ȡ�����˵ķ�֧����)
	        
	        templateMode = StringUtility.replace(templateMode, "$OrganComName",
	        		tOrganComCodeName);//��֧��������
	        
	        templateMode = StringUtility.replace(templateMode, "$ActuGetNo", tActuGetNo);//֧������ 
	        
	        templateMode = StringUtility.replace(templateMode, "$BankInfo",tClaimNoticeBean.getBankInfo());//��������

	        String tBankAccNo=tClaimNoticeBean.getBankAccNo();
	        if(tBankAccNo!=null&&!tBankAccNo.equals("")){
		        int lenBAN=tBankAccNo.length();
				if(lenBAN>=8){
					tBankAccNo=tBankAccNo.substring(0,lenBAN-8)+"******"+tBankAccNo.substring(lenBAN-2, lenBAN);
				}else{
					tBankAccNo="******";
				}	        	
	        }

	        templateMode = StringUtility.replace(templateMode, "$BankAccNo",tBankAccNo);//ת�˺��� 

	        templateMode = StringUtility.replace(templateMode, "$AccName",tClaimNoticeBean.getAccName());//�ʻ������� 
			//end 20101123

			templateMode = StringUtility.replace(templateMode, "$CaseNo", tClaimNoticeBean
					.getCaseNo());//�����
			templateMode = StringUtility.replace(templateMode, "$PreClaimMoney",
					tPreClaimMoney);//�ϼ�������

			templateMode = StringUtility.replace(templateMode, "$MainCustomerName",
					tClaimNoticeBean.getMainName());//��������������

			templateMode = StringUtility.replace(templateMode, "$MainCustomerNo",
					tClaimNoticeBean.getCustomerNo());//��Ա����
			//modify by winnie ASR20106910 �����������˵����֤��

			String tMIDNo=tClaimNoticeBean.getIDNo();
			if(tMainLCInsuredSchema.getIDType().equals("0")){
				if(tMIDNo!=null&&!tMIDNo.equals("")){
					int lenMID=tMIDNo.length();
					if(lenMID>=8){
						tMIDNo=tMIDNo.substring(0,lenMID-8)+"******"+tMIDNo.substring(lenMID-2, lenMID);
					}else{
						tMIDNo="******";
					}					
				}
			}

			templateMode = StringUtility.replace(templateMode, "$MainIDNo",
					tMIDNo);//��������
			//end modify 20100910

			templateMode = StringUtility.replace(templateMode, "$CustomerName",
					tClaimNoticeBean.getCustomerName());
			//�ѷ���ʹ��
//			templateMode = StringUtility.replace(templateMode, "$CustomerNo",
//					mLLCaseDB.getCustomerNo());
			//modify by winnie ASR20106910 ���γ����˵����֤��

			String tDIDNo=tClaimNoticeBean.getIDNo();
			if(mLLCaseDB.getIDType().equals("0")){
				if(tDIDNo!=null&&!tDIDNo.equals("")){
					int lenDID=tDIDNo.length();
					if(lenDID>=8){
						tDIDNo=tDIDNo.substring(0,lenDID-8)+"******"+tDIDNo.substring(lenDID-2, lenDID);
					}else{
						tDIDNo="******";
					}
				}
			}

			templateMode = StringUtility.replace(templateMode, "$IDNo", tDIDNo);
			//end modify 20100910
			templateMode = StringUtility.replace(templateMode, "$SumPay", String
					.valueOf(sumPay));
			templateMode = StringUtility.replace(templateMode, "$DetailPay",
					tDetailPay);
			templateMode = StringUtility
					.replace(templateMode, "$Remark1", tRemark1);
			templateMode = StringUtility.replace(templateMode, "$CurrentDate",
					tCurrentDate);
//			templateMode = StringUtility.replace(templateMode, "$GetDetail",
//					tGetDetail);//��ȡ��  Ŀǰ�ѷ���
			templateMode = StringUtility.replace(templateMode, "$FeeDetail",
					feeDetail);
//			templateMode = StringUtility.replace(templateMode, "$ApplyDate",
//					applyDate);
			// Added By Fang for Change1
			//String tRemark = "1.�������������պ���գ�2.��ת�˳ɹ�������Ϊ�������յ��ñ�������";//modify by winnie ASR20097519
			String tRemark = "1������2-5�������պ���գ�2����ת�˳ɹ������Ӹ������յ��ñ�������3���������ʣ�����ϵ�������ٿͷ���Ա��";
			if (tFEFlag) {
				// Amended By Fang for Change3
				// //Amended By Fang for Change2
				// //tRemark="���������Ѿ���������������ϣ������ڹ�˾�ı���δ���ɣ������������ʱδ�ܻ����������½⡣";
				// tRemark="���������Ѿ���������������ϣ����˾�����ҹ�˾���б��Ѻ��㣬�����ڱ��Ѻ�����Ϻ󾡿컮�����������Ⲣ�½⡣";
				// //End for Change2
				String mEdorReasonno2 = "";
//				String sqlContState = "select distinct aa.contno,aa.edorno,aa.edortype,bb.confdate,bb.conftime,cc.edorreasonno2,"
//						+ "(select codealias from ldcode where codetype='claimtemplat' and code=cc.edorreasonno2)"
//						+ " from lpcont aa,lpgrpedormain bb,lpgrpedoritem cc where aa.edorno=bb.edorno and bb.edorno=cc.edorno"
//						+ " and aa.edortype=cc.edortype and bb.edorstate='0' and aa.edortype='FE' and aa.contno='"
//						+ mLCInsuredBL.getContNo()
//						+ "' and bb.confdate<='"
//						+ mCurrentDate
//						+ "' order by bb.confdate desc, bb.conftime desc";
//				SSRS mSSRS_EdorReasonno2 = new SSRS();
//				mSSRS_EdorReasonno2 = mExeSQL.execSQL(sqlContState);
//				if (mSSRS_EdorReasonno2.getMaxRow() > 0) {
//					mEdorReasonno2 = mSSRS_EdorReasonno2.GetText(1, 7);
//				}
//				if (mEdorReasonno2 == null || mEdorReasonno2.equals("")) {
//					mEdorReasonno2 = mExeSQL
//							.getOneValue("select codealias from ldcode where codetype='claimtemplat' and comcode = substr('"
//									+ mLLCaseDB.getMngCom() + "',0,4)");
//				}
				//Amended By Fang for Change4(ͳһģ��)
				mEdorReasonno2 = mExeSQL.getOneValue("select codealias from ldcode where codetype='claimtemplat' and comcode='86'");
				
				tRemark = mEdorReasonno2;
				// End for Change3
			}
			templateMode = StringUtility.replace(templateMode, "$Remark", tRemark);
			// End

		}
		InputStream inputStream = new ByteArrayInputStream(templateMode
				.getBytes());
		LLMailListSchema tLLMailListSchema = new LLMailListSchema();

		String tLimit = "";
		if (mGlobalInput.OutUserFlag.equals("1")) // ����û�
		{
			tLimit = PubFun.getNoLimit(mOutUserMngCom);
		} else {
			tLimit = PubFun.getNoLimit(mGlobalInput.ManageCom);
		}

		String tSerialNo = PubFun1.CreateMaxNo("SERIALNO", tLimit);
		tLLMailListSchema.setGLPOLPFX("CH");
		tLLMailListSchema.setGLPOLCOY("3");
		tLLMailListSchema.setGLPOLNUM(mLCInsuredBL.getGrpContNo());// ������
		tLLMailListSchema.setGLRENNO(0);
		tLLMailListSchema.setGLCERT(mLLCaseDB.getCustomerNo());// �ͻ���
		tLLMailListSchema.setGLCERTPFIX("0");
		tLLMailListSchema.setGLCERTSEQ("1");
		tLLMailListSchema.setGlemail(tEMail);
		tLLMailListSchema.setGlphone(tGlphone);
		tLLMailListSchema.setGlmessage(inputStream);
		tLLMailListSchema.setGlstatus("0");
		tLLMailListSchema.setGltype(0);
		tLLMailListSchema.setGLCSEQNO("1");
		tLLMailListSchema.setGLCLAIM(mLLCaseDB.getCaseNo());// �����

		if (mGlobalInput.OutUserFlag.equals("1")) // ����û�,��������ձ�û�����ù����գ��������ɶ���
		{
			String pattern1 = "HH:mm:ss";
			SimpleDateFormat df1 = new SimpleDateFormat(pattern1);
			String pattern2 = "yyyy-MM-dd";
			SimpleDateFormat df2 = new SimpleDateFormat(pattern2);

			String strWorkDate = " select min(workdate) from ldworktime where f01='0' and workdate>sysdate ";
			String tWorkDate = mExeSQL.getOneValue(strWorkDate);

			if (!tWorkDate.equals("") && tWorkDate != null) {
				FDate tFDate = new FDate();
				Date dWorkDate = tFDate.getDate(tWorkDate);
				tLLMailListSchema.setGlmessageDate(df2.format(dWorkDate) + " "
						+ df1.format(today));
			} else {
				System.out.println("�����ʼ�ʱ����ѯ��һ����������Ϊ�գ�");
				return false;
			}
		} else {
			tLLMailListSchema.setGlmessageDate(df.format(today));
		}
		tLLMailListSchema.setGlsubject(mailSubject);
		tLLMailListSchema.setGlsurname(mLLCaseDB.getCustomerName());// ������������
		tLLMailListSchema.setGlsrvbrch(tGlsrvbrch);
		tLLMailListSchema.setGlrecvdate(tGlrecvdate);
		tLLMailListSchema.setGltotclamt(tPreClaimMoney);
		tLLMailListSchema.setGltotpyamt(sumPay);
		tLLMailListSchema.setRECORDERNUM(tSerialNo);
		tLLMailListSchema.setMakeDate(PubFun.getCurrentDate());
		tLLMailListSchema.setMakeTime(PubFun.getCurrentTime());
		map.put(tLLMailListSchema, "BLOBINSERT");
		return true;
	}

	/**
	 * ��ȡ�ʵ��Էѣ��Ը���Ϣ
	 * 
	 * @return
	 */
	private String getFeeDetail() {
		String feeDetail = "";
		//modify by winnie ASR20107039  ��ͷ�ֶΣ��������ڣ�����ҽԺ����𣬵������������ݽ��۳����۳�˵��    	
		ListTable tOtherListTable=tClaimNoticeBean.getTOtherListTable();//�⸶��ϸ���۳�������˵��
		for(int i=0;i<tOtherListTable.size();i++){
			String[]temp= tOtherListTable.get(i);
			feeDetail += "<tr>" + "<td >" + temp[0] + "</td>"
			+ " <td>" + temp[1] + "</td>" + " <td>"+temp[2]+"</td>"
			+ " <td>" + temp[3]+ "</td>"+ " <td>"+temp[4]+"</td>"
			+ " <td>" + temp[5]+ "</td>" + "<td>"
			+ temp[6] + "&nbsp;</td>"
			+ "</tr>";
		}
		//end modify 20101020
		return feeDetail;
	}
	/**
	 * ��ȡ�ʵ��Էѣ��Ը���Ϣ
	 * ��ͨ�ⰸ
	 * @return
	 */
	private String getFeeDetail2() {
		String feeDetail = "";
//		modify by winnie ASR20107039  ��ͷ�ֶΣ��������ڣ�����ҽԺ����𣬵������������ݽ��۳����۳�˵��
		//�ֶδ���:1 ����������
		ExeSQL  mExeSQL=new ExeSQL();
		String otherGetSql="select '','','����������',count(*),sum(nvl(sumfee,0)),sum(nvl(sumfee,0)) from llfeemain m where m.caseno='"+mLLCaseDB.getCaseNo()+"' and m.feetype='4' group by '','','����������' having sum(nvl(sumfee, 0))>0";
		SSRS otherGetSSRS=new SSRS();
		otherGetSSRS = mExeSQL.execSQL(otherGetSql);
		if(otherGetSSRS!=null&&otherGetSSRS.getMaxRow()>0){
			for(int g=0;g<otherGetSSRS.getMaxRow();g++){
    			double sumMoney=0.00;
				String other_Info[]=new String[7];
    			other_Info[0]="";//��������
    			other_Info[1]="";//����ҽԺ
    			other_Info[2]="����������";//���
    			other_Info[3]=otherGetSSRS.GetText(g+1, 4);//��֤����
    			other_Info[4]=otherGetSSRS.GetText(g+1, 5);//���ݽ��
    			other_Info[5]=otherGetSSRS.GetText(g+1, 6);//�۳����
    			String gRemarkSQL="select otherorganname from llfeemain m where m.caseno='"+mLLCaseDB.getCaseNo()+"' and m.feetype='4'";//�۳�˵��
    			String gRemark="";
    			sumMoney=Double.parseDouble(other_Info[4]);
    			SSRS rmSSRS0=new SSRS();
    			rmSSRS0=mExeSQL.execSQL(gRemarkSQL);
    			for(int i=0;i<rmSSRS0.getMaxRow();i++){
    				if(rmSSRS0.GetText(i+1, 1).endsWith(";")){
    					gRemark=gRemark+rmSSRS0.GetText(i+1, 1);	
    				}else{
        				gRemark=gRemark+rmSSRS0.GetText(i+1, 1)+";";
    				}
    			}
    			other_Info[6]=gRemark;//�۳�˵��
    			if(sumMoney!=0.00&&sumMoney!=0.0&&sumMoney!=0){
    				feeDetail += "<tr>" + "<td>" + other_Info[0] + "</td>"
					+ " <td>" + other_Info[1] + "</td>" + " <td>"+other_Info[2]+"</td>"
					+ " <td>" + other_Info[3]+ "</td>"+ " <td>"+other_Info[4]+"</td>"
					+ " <td>" + other_Info[5]+ "</td>" + "<td>"
					+ other_Info[6] + "&nbsp;</td>"
					+ "</tr>";
    			}
			}
		}
//		2 ��������������֪ͨ��
		//����Ϊ1���������ݣ������Ľ��   
		String OtherSql="select to_char(a.Feedate,'yyyy/mm/dd'),HospitalCode,HospitalName,FeeType,count(*),sum(nvl(SumFee,0)) as sm,sum(nvl(SelfAmnt,0) +nvl(RefuseAmnt,0) + nvl(SPayAmnt,0)) as kcprem"
			+" from llfeemain a where a.CaseNo = '"+mLLCaseSchema.getCaseNo()+"' and a.feetype = '1'"
			+" group by a.feedate,a.HospitalCode,HospitalName,a.FeeType  having  sum(nvl(SumFee, 0))>0 "
			+" order by a.feedate,a.HospitalCode  "; 
		 System.out.println("�������1: "+OtherSql);
         SSRS  OtherSSRS = mExeSQL.execSQL(OtherSql);
			if (OtherSSRS.getMaxRow() > 0) {
				SSRS rmSSRS1=new SSRS(); //��ſ۳�˵���Ľ����
				int len=OtherSSRS.getMaxRow();
				for (int a = 0; a < len; a++) {
    				String temp_O[]=new String[7];
					temp_O[0]=OtherSSRS.GetText(a+1, 1);//��������
					temp_O[1]=OtherSSRS.GetText(a+1, 3);//����ҽԺ
					temp_O[2]="����";
					temp_O[3]=OtherSSRS.GetText(a+1, 5);//��������
					temp_O[4]=OtherSSRS.GetText(a+1, 6);//���ݽ��
					temp_O[5]=OtherSSRS.GetText(a+1, 7);//�۳����
					//�۳�˵��
					String mRemark1="";
	        		String feeSql = "select to_char(a.Feedate,'yyyy/mm/dd'),HospitalCode,"
	        		       +"nvl(a.selfamntstatement,''),nvl(a.refuseamntstatement,''),nvl(a.spayamntstatement,'')"
	        		       +" from llfeemain a where a.CaseNo = '"+mLLCaseDB.getCaseNo()+"' and a.feetype='1'"
	        		       +" and a.FeeDate=to_Date('"+temp_O[0]+"','yyyy-MM-dd') and a.HospitalCode='"+OtherSSRS.GetText(a+1, 2)+"'";
	        		rmSSRS1=mExeSQL.execSQL(feeSql);
	        		if(rmSSRS1.getMaxRow()>0){
	        			for(int r=0;r<rmSSRS1.getMaxRow();r++){
	        				//�Է�
	        				if(rmSSRS1.GetText(r+1, 3)!=null&&!rmSSRS1.GetText(r+1, 3).equals("")){
	        					if(rmSSRS1.GetText(r+1, 3).endsWith(";")){
	        						mRemark1=mRemark1+rmSSRS1.GetText(r+1, 3);
	        					}else{
	        						mRemark1=mRemark1+rmSSRS1.GetText(r+1, 3)+";";
	        					}
	        				}
	        				//�����Ը�
	        				if(rmSSRS1.GetText(r+1, 4)!=null&&!rmSSRS1.GetText(r+1, 4).equals("")){
	        					if(rmSSRS1.GetText(r+1, 4).endsWith(";")){
	        						mRemark1=mRemark1+rmSSRS1.GetText(r+1, 4);
	        					}else{
	        						mRemark1=mRemark1+rmSSRS1.GetText(r+1, 4)+";";
	        					}	
	        				}
	        				//ҽ��֧��
	        				if(rmSSRS1.GetText(r+1, 5)!=null&&!rmSSRS1.GetText(r+1, 5).equals("")){
	        					if(rmSSRS1.GetText(r+1, 5).endsWith(";")){
	        						mRemark1=mRemark1+rmSSRS1.GetText(r+1, 5);
	        					}else{
	        						mRemark1=mRemark1+rmSSRS1.GetText(r+1, 5)+";";
	        					}	
	        				}
	        			}
	        		}
					temp_O[6]=mRemark1;
					if(Double.parseDouble(OtherSSRS.GetText(a+1, 6))!=0.00&&Double.parseDouble(OtherSSRS.GetText(a+1, 6))!=0.0&&Double.parseDouble(OtherSSRS.GetText(a+1, 6))!=0){
	    				feeDetail += "<tr>" + "<td>" + temp_O[0] + "</td>"
						+ " <td>" + temp_O[1] + "</td>" + " <td>"+temp_O[2]+"</td>"
						+ " <td>" + temp_O[3]+ "</td>"+ " <td>"+temp_O[4]+"</td>"
						+ " <td>" + temp_O[5]+ "</td>" + "<td>"
						+ temp_O[6] + "&nbsp;</td>"
						+ "</tr>";
					}
				}
			}
			//סԺ�������������֪ͨ�� 
    		//����Ϊ2���������ݣ������Ľ��	
        	String OtherSql2="select to_char(a.HospStartDate,'yyyy/mm/dd'),to_char(a.HospEndDate,'yyyy/mm/dd'),HospitalCode,HospitalName,FeeType,count(*),sum(nvl(SumFee,0)) as sm,sum(nvl(SelfAmnt,0) +nvl(RefuseAmnt,0) + nvl(SPayAmnt,0)) as kcprem"
        			+" from llfeemain a where a.CaseNo = '"+mLLCaseDB.getCaseNo()+"' and a.feetype = '2'"
        			+" group by a.HospStartDate,a.HospEndDate,a.HospitalCode,HospitalName,a.FeeType having sum(nvl(SumFee, 0))>0 "
        			+" order by a.HospStartDate,a.HospEndDate,a.HospitalCode  "; 
            System.out.println("סԺ����1: "+OtherSql2);
            SSRS OtherSSRS2 = mExeSQL.execSQL(OtherSql2);
        	if (OtherSSRS2.getMaxRow() > 0) {
        		SSRS rmSSRS2=new SSRS(); //��ſ۳�˵���Ľ����
        		for (int a = 0; a < OtherSSRS2.getMaxRow(); a++) {
        					String temp_O_Z[]=new String[7];
        					temp_O_Z[0]=OtherSSRS2.GetText(a+1, 1)+" -- "+OtherSSRS2.GetText(a+1, 2);//��������
        					temp_O_Z[1]=OtherSSRS2.GetText(a+1, 4);//����ҽԺ
        					temp_O_Z[2]="סԺ";
        					temp_O_Z[3]=OtherSSRS2.GetText(a+1, 6);//��������
        					temp_O_Z[4]=OtherSSRS2.GetText(a+1, 7);//���ݽ��
        					temp_O_Z[5]=OtherSSRS2.GetText(a+1, 8);//�۳����
        					//�۳�˵��
        					String mRemark2="";
        	        String feeSql = "select HospStartDate,HospEndDate,HospitalCode,"
        	               +"nvl(a.selfamntstatement,''),nvl(a.refuseamntstatement,''),nvl(a.spayamntstatement,'')"
        	               +" from llfeemain a where a.CaseNo = '"+mLLCaseDB.getCaseNo()+"' and a.feetype='2'"
        	               +" and a.HospStartDate=to_Date('"+OtherSSRS2.GetText(a+1, 1)+"','yyyy-MM-dd') and a.HospEndDate=to_Date('"+OtherSSRS2.GetText(a+1, 2)+"','yyyy-MM-dd') and a.HospitalCode='"+OtherSSRS2.GetText(a+1, 3)+"'";
        	        rmSSRS2=mExeSQL.execSQL(feeSql);
        	        if(rmSSRS2.getMaxRow()>0){
        	        	for(int r=0;r<rmSSRS2.getMaxRow();r++){
        	        		//�Է�
        	        		if(rmSSRS2.GetText(r+1, 4)!=null&&!rmSSRS2.GetText(r+1, 4).equals("")){
        	        			if(rmSSRS2.GetText(r+1, 4).endsWith(";")){
        	        				mRemark2=mRemark2+rmSSRS2.GetText(r+1, 4);
        	        			}else{
        	        				mRemark2=mRemark2+rmSSRS2.GetText(r+1, 4)+";";
        	        			}
        	        		} 
        	        		//�����Ը�
        	        		if(rmSSRS2.GetText(r+1, 5)!=null&&!rmSSRS2.GetText(r+1, 5).equals("")){
        	        			if(rmSSRS2.GetText(r+1, 5).endsWith(";")){
        	        				mRemark2=mRemark2+rmSSRS2.GetText(r+1, 5);
        	        			}else{
        	        				mRemark2=mRemark2+rmSSRS2.GetText(r+1, 5)+";";
        	        			}	
        	        		} 
        	        		//ҽ��֧��
        	        		if(rmSSRS2.GetText(r+1, 6)!=null&&!rmSSRS2.GetText(r+1, 6).equals("")){
        	        			if(rmSSRS2.GetText(r+1, 6).endsWith(";")){
        	        				mRemark2=mRemark2+rmSSRS2.GetText(r+1, 6);
        	        			}else{
        	        				mRemark2=mRemark2+rmSSRS2.GetText(r+1, 6)+";";
        	        			}	
        	        		} 
        	        	}   
        	        }    
        	        temp_O_Z[6]=mRemark2;
        	        if(Double.parseDouble(OtherSSRS2.GetText(a+1, 7))!=0.00&&Double.parseDouble(OtherSSRS2.GetText(a+1, 7))!=0.0&&Double.parseDouble(OtherSSRS2.GetText(a+1, 7))!=0){
	    				feeDetail += "<tr>" + "<td >" + temp_O_Z[0] + "</td>"
						+ " <td>" + temp_O_Z[1] + "</td>" + " <td>"+temp_O_Z[2]+"</td>"
						+ " <td>" + temp_O_Z[3]+ "</td>"+ " <td>"+temp_O_Z[4]+"</td>"
						+ " <td>" + temp_O_Z[5]+ "</td>" + "<td>"
						+ temp_O_Z[6] + "&nbsp;</td>"
						+ "</tr>";
        			} 
        		}   
        	}	 
		return feeDetail;
	}

	/**
	 * ���ɶ��Žӿ���Ϣ
	 * 
	 * @return
	 */
	private boolean dealSMList() {
		// haoxh:2007-07-25:��������������˵��ֻ��ź����������˵��ֻ��Ŷ�Ϊ�յĻ��Ͳ����Ͷ���
		String tMoblie = "";
		if (mLCAddressDB.getMobile() == null
				|| "".equals(mLCAddressDB.getMobile())) {
			if ((mLCInsuredBL.getMainInsuredNo() != null)
					&& (!"".equals(mLCInsuredBL.getMainInsuredNo()))
					&& !"0".equals(mLCAddressDB.getBP2())) {
				// �õ������������ֻ�����
				SynLCLBInsuredBL tSynLCLBInsuredBL = new SynLCLBInsuredBL();
				LCAddressDB tLCAddressDB = new LCAddressDB();

				tSynLCLBInsuredBL.setGrpContNo(mLCInsuredBL.getGrpContNo());
				tSynLCLBInsuredBL.setInsuredNo(mLCInsuredBL.getMainInsuredNo());
				LCInsuredSet tLCInsuredSet = tSynLCLBInsuredBL.query();
				if (tLCInsuredSet == null || tLCInsuredSet.size() == 0) {
					System.out.println("δ��ѯ�����������ˣ�");
					return false;
				}
				tLCAddressDB.setCustomerNo(tLCInsuredSet.get(1).getInsuredNo());
				tLCAddressDB.setAddressNo(tLCInsuredSet.get(1).getAddressNo());
				if (!tLCAddressDB.getInfo()) {
					System.out.println("��ѯ���������˵�ַ��ʧ�ܣ�");
					return false;
				}
				tMoblie = tLCAddressDB.getSchema().getMobile();
				if ("".equals(tMoblie) || tMoblie == null) {
					System.out.println("��ѯ���������˵��ֻ�����Ϊ�գ�");
					return false;
				}
			} else {
				return false;
			}
		} else {
			tMoblie = mLCAddressDB.getMobile();
			if ("".equals(tMoblie) || tMoblie == null
					|| "0".equals(mLCAddressDB.getBP2())) {
				System.out.println("��ѯ�������˻����������˵��ֻ�����Ϊ�ջ�رն���֪ͨ����");
				return false;
			}
		}
		// ���ֻ��ŵ�У��(�Ƿ�Ϊ���֣������Ƿ�С��20)
		if (!tMoblie.equals("") || tMoblie != null) {
			if (!PubFun.isNumeric(tMoblie) || (tMoblie.length() >= 20)) {
				System.out.println("��ѯ�������˻����������˵��ֻ���������Ϊ" + tMoblie + "��");
				return false;
			}
		}
		LLSMListSchema tLLSMListSchema = new LLSMListSchema();

		String tLimit = "";
		if (mGlobalInput.OutUserFlag.equals("1")) // ����û�
		{
			tLimit = PubFun.getNoLimit(mOutUserMngCom);
		} else {
			tLimit = PubFun.getNoLimit(mGlobalInput.ManageCom);
		}

		String tSerialNo = PubFun1.CreateMaxNo("SERIALNO", tLimit);
		FDate fDate = new FDate();
		Date tApplyDate = fDate.getDate(mLLRegisterDB.getApplyDate());
		GregorianCalendar sCalendar = new GregorianCalendar();
		sCalendar.setTime(tApplyDate);
		String tNewApplyDate = sCalendar.get(Calendar.YEAR) + "��"
				+ (sCalendar.get(Calendar.MONTH) + 1) + "��"
				+ sCalendar.get(Calendar.DAY_OF_MONTH) + "��";

		String tRealPaySQL = "select nvl(sum(realpay),0) from llclaimdetail where caseno='"
				+ mLLCaseSchema.getCaseNo() + "'";
		ExeSQL tExeSQL = new ExeSQL();
		String tSumRealPay = tExeSQL.getOneValue(tRealPaySQL);

		// Amended By Fang For Change1
		// String tSMContent = "�𾴵�" + mLLCaseDB.getCustomerName() + "������"
		// + tNewApplyDate + "�ύ�����������Ѵ�����ϣ������" + tSumRealPay +
		// "Ԫ������3�������պ���ա�";
		String tSMContent = "";
		if (tFEFlag) {
			// Amended By Fang for Change3
			// //Amended By Fang for Change2
			// //tSMContent = "�𾴵�" + mLLCaseDB.getCustomerName() + "������"
			// //+ tNewApplyDate + "�ύ�����������Ѵ�����ϣ������" + tSumRealPay +
			// "Ԫ�������ڹ�˾����δ���ɣ������δ������";
			// tSMContent = "�𾴵�" + mLLCaseDB.getCustomerName() + "������"
			// + tNewApplyDate + "�ύ�����������Ѵ�����ϣ������" + tSumRealPay +
			// "Ԫ��������˾�����˾���㱣�ѣ������ڱ��Ѻ�����Ϻ󾡿컮�����������Ⲣ�½⡣";
			// //End for Change2
			String mEdorReasonno2 = "";
//			String sqlContState = "select distinct aa.contno,aa.edorno,aa.edortype,bb.confdate,bb.conftime,cc.edorreasonno2,"
//					+ "(select codealias from ldcode where codetype='claimphtemplat' and code=cc.edorreasonno2)"
//					+ " from lpcont aa,lpgrpedormain bb,lpgrpedoritem cc where aa.edorno=bb.edorno and bb.edorno=cc.edorno"
//					+ " and aa.edortype=cc.edortype and bb.edorstate='0' and aa.edortype='FE' and aa.contno='"
//					+ mLCInsuredBL.getContNo()
//					+ "' and bb.confdate<='"
//					+ mCurrentDate
//					+ "' order by bb.confdate desc, bb.conftime desc";
//			SSRS mSSRS_EdorReasonno2 = new SSRS();
//			mSSRS_EdorReasonno2 = tExeSQL.execSQL(sqlContState);
//			if (mSSRS_EdorReasonno2.getMaxRow() > 0) {
//				mEdorReasonno2 = mSSRS_EdorReasonno2.GetText(1, 7);
//			}
//			if (mEdorReasonno2.equals("") || mEdorReasonno2 == null) {
//				mEdorReasonno2 = tExeSQL
//						.getOneValue("select codealias from ldcode where codetype='claimphtemplat' and comcode = substr('"
//								+ mLLCaseDB.getMngCom() + "',0,4)");
//			}
			//Amended By Fang for Change4(ͳһģ��)
			mEdorReasonno2 = tExeSQL.getOneValue("select codealias from ldcode where codetype='claimphtemplat' and comcode='86'");
			
			mEdorReasonno2 = StringUtility.replace(mEdorReasonno2,
					"$CustomerName$", mLLCaseDB.getCustomerName());
			mEdorReasonno2 = StringUtility.replace(mEdorReasonno2,
					"$NewApplyDate$", tNewApplyDate);
			mEdorReasonno2 = StringUtility.replace(mEdorReasonno2, "$RealPay$",
					tSumRealPay);
			tSMContent = mEdorReasonno2;
			// End for Change3
		} else {
			templateMode = StringUtility.replace(templateMode, "$CustomerName",
					mLLCaseDB.getCustomerName());
			templateMode = StringUtility.replace(templateMode, "$ApplyDate",
					tNewApplyDate);
			templateMode = StringUtility.replace(templateMode, "$SumRealPay",
					tSumRealPay);

			// tSMContent = "�𾴵�" + mLLCaseDB.getCustomerName() + "������"
			// + tNewApplyDate + "�ύ�����������Ѵ�����ϣ������" + tSumRealPay
			// + "Ԫ������3�������պ���ա�";
			if ("0".equals(tSumRealPay)) {
				tSMContent = "�𾴵�" + mLLCaseDB.getCustomerName()
						+ "�����������Ѵ�������������������֪ͨ��������վ����ϵ�ͷ���Ա����������";
			}else{
				tSMContent = templateMode;				
			}
		}
		// End
		String tBranchCode = "";
		String tSMStionSql = "select * from ldcom where comcode = '"
				+ mLLCaseDB.getMngCom() + "' and signid is not null";
		LDComDB tLDComDB = new LDComDB();
		LDComSet tLDComSet = new LDComSet();
		tLDComSet = tLDComDB.executeQuery(tSMStionSql);
		if (tLDComSet != null && tLDComSet.size() > 0) {
			tBranchCode = tLDComSet.get(1).getInnerComCode().substring(1,3);
		} else {
			System.out.println("û�в�ѯ��Carriercode����������������д���Ӧ�Ĵ���");
		}
		String tSendTargetDesc = "UnSendBack|UGSCLAIM|" + tBranchCode;

		Date today = new Date();
		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		String pattern1 = "HH:mm:ss";
		SimpleDateFormat df1 = new SimpleDateFormat(pattern1);
		String pattern2 = "yyyy-MM-dd";
		SimpleDateFormat df2 = new SimpleDateFormat(pattern2);

		tLLSMListSchema.setSerialNo(tSerialNo);
		tLLSMListSchema.setServiceID("SMCS");
		tLLSMListSchema.setSendTarget(tMoblie);
		tLLSMListSchema.setSMContent(tSMContent);
		tLLSMListSchema.setPriority(0);

		if (mGlobalInput.OutUserFlag.equals("1")) // ����û�,��������ձ�û�����ù����գ��������ɶ���
		{
			String strWorkDate = " select min(workdate) from ldworktime where f01='0' and workdate>sysdate ";
			String tWorkDate = tExeSQL.getOneValue(strWorkDate);

			if (!tWorkDate.equals("") && tWorkDate != null) {
				FDate tFDate = new FDate();
				Date dWorkDate = tFDate.getDate(tWorkDate);

				tLLSMListSchema.setRCompleteTimeBegin(df2.format(dWorkDate)
						+ " " + df1.format(today));
				tLLSMListSchema.setRCompleteTimeEnd(PubFun.calDate(df
						.format(dWorkDate), 1, "D", null)
						+ " " + df1.format(today));
				tLLSMListSchema.setRequestTime(df2.format(dWorkDate) + " "
						+ df1.format(today));
			} else {
				System.out.println("���ɶ���ʱ����ѯ��һ����������Ϊ�գ�");
				return false;
			}
		} else {
			tLLSMListSchema.setRCompleteTimeBegin(df.format(today));
			tLLSMListSchema.setRCompleteTimeEnd(PubFun.calDate(
					df.format(today), 1, "D", null)
					+ " " + df1.format(today));
			tLLSMListSchema.setRequestTime(df.format(today));
		}
		tLLSMListSchema.setRCompleteHourBegin(0);
		tLLSMListSchema.setRCompleteHourEnd(1439);
		tLLSMListSchema.setRoadBy(0);
		tLLSMListSchema.setSendTargetDesc(tSendTargetDesc);
		tLLSMListSchema.setPAD2(mLLCaseSchema.getCaseNo());// ������
		tLLSMListSchema.setMakeDate(PubFun.getCurrentDate());
		tLLSMListSchema.setMakeTime(PubFun.getCurrentTime());
		map.put(tLLSMListSchema, "INSERT");
		return true;
	}

	private boolean prepareOutputData() {
		try {
			mInputData.clear();
			mInputData.add(map);
			mResult.clear();
			mResult.add(map);
		} catch (Exception ex) {
			// @@������
			buildError("prepareOutputData", "��׼������㴦������Ҫ������ʱ����");
			return false;
		}
		return true;
	}

	/**
	 * ���ɴ�����Ϣ
	 * 
	 * @param szFunc
	 * @param szErrMsg
	 */
	private void buildError(String szFunc, String szErrMsg) {
		CError cError = new CError();
		cError.moduleName = "LLClaimSMBL";
		cError.functionName = szFunc;
		cError.errorMessage = szErrMsg;
		this.mErrors.addOneError(cError);
	}

	public String getRealPath() {
		String strRealPath = System.getProperty("uicontextpath");
		if (StrTool.cTrim(strRealPath).equals("")) {// ���û�ҵ�����ͨ��class�ļ���
			File path = new File(LLClaimSMBatchBL.class.getResource("/").getFile());
			strRealPath = path.getParentFile().getParentFile().toString();
		}

		return strRealPath;
	}
	 /**
     * ��ȡ���ϼƻ����� add by winnie ASR20107519
     */
    private String getContPlanCode(LLClaimDetailSchema tLLClaimDetailSchema , LLCaseSchema tLLCaseSchema){
        String tContPlanCode = "";
        //���ϼƻ��������Ҫ������ǰ�ı��ϼƻ�
        String strSQContPlanCode = "select edorno from LPEdorItem where EdorType='LC' " +
        		" and ContNo='"+tLLClaimDetailSchema.getContNo()+"' and InsuredNo='"+tLLCaseSchema.getCustomerNo()+"' " +
        		" and EdorValiDate > '"+ tLLCaseSchema.getAccidentDate()+"' order by EdorValiDate ";
        SSRS planSSRS = new SSRS();
        ExeSQL planExeSQL = new ExeSQL();
        planSSRS = planExeSQL.execSQL(strSQContPlanCode);
        if(planSSRS != null && planSSRS.getMaxRow() > 0){
        	String tEdorNo = planSSRS.GetText(1, 1);
        	LPInsuredDB tLPInsuredDB = new LPInsuredDB();
        	tLPInsuredDB.setEdorNo(tEdorNo);
        	tLPInsuredDB.setEdorType("LC");
        	tLPInsuredDB.setContNo(tLLClaimDetailSchema.getContNo());
        	tLPInsuredDB.setInsuredNo(tLLCaseSchema.getCustomerNo());
        	if(!tLPInsuredDB.getInfo()){
        		System.out.println("���ұ��ϼƻ������ȫ��LPInsured��ʧ�ܣ�");
        	}else{
        		tContPlanCode = tLPInsuredDB.getContPlanCode();
        	}
        }
        if("".equals(tContPlanCode)){
        	LCInsuredBL tLCInsuredBL = new LCInsuredBL();
        	tLCInsuredBL.setInsuredNo(tLLCaseSchema.getCustomerNo());
    		tLCInsuredBL.setContNo(tLLClaimDetailSchema.getContNo());
    		if (!tLCInsuredBL.getInfo()) {
    			System.out.println("û���ҵ����ϼƻ���");
    		}
    		tContPlanCode = tLCInsuredBL.getContPlanCode();
        }
        return tContPlanCode;
    }
    /**
     * add by winnie ASR20107519
     * @param tLLClaimDetailSchema
     * @param tLLCaseSchema
     * @return
     */
    public double getPayRate(LLClaimDetailSchema tLLClaimDetailSchema , LLCaseSchema tLLCaseSchema){
        //��ȡ���ϼƻ�����
        String tContPlanCode = getContPlanCode(tLLClaimDetailSchema , tLLCaseSchema);
      
        //ȡ����ҪԼҪ�� ��������������㷨
	    LCContPlanFactoryDB tLCContPlanFactoryDB = new LCContPlanFactoryDB();
	    LCContPlanFactorySet tLCContPlanFactorySet = new LCContPlanFactorySet();
	        
	    String strSQL = "select * from LCContPlanFactory where grpcontno='"+tLLClaimDetailSchema.getGrpContNo()+"' " +
	        	" and riskcode='"+tLLClaimDetailSchema.getRiskCode()+"' " +
	        	" and ContPlanCode = '"+tContPlanCode+"' " +
	        	" and FactoryType='000004' and OtherNo='"+tLLClaimDetailSchema.getGetDutyCode()+"' and FactoryCode<>'000007'";
	    tLCContPlanFactorySet = tLCContPlanFactoryDB.executeQuery(strSQL);
	    TransferData tTransferData = new TransferData();
	    PubCalculator tempPubCalculator = new PubCalculator();
	    boolean DiseasePayPercentFlag = false;//�����⸶����
	    boolean SuddennessPayPercentFlag = false;//�����⸶����
	    boolean PayPercentFlag = false; //NIK12���⸶����
	    for (int m = 1; m <= tLCContPlanFactorySet.size(); m++) {
	        LCContPlanFactorySchema tLCContPlanFactorySchema = new LCContPlanFactorySchema();
			tLCContPlanFactorySchema = tLCContPlanFactorySet.get(m);
			tempPubCalculator.setCalSql(tLCContPlanFactorySchema.getCalSql());
			String tResult = tempPubCalculator.calculate();
			if (tResult != null && !tResult.trim().equals("")) {
				if (tTransferData.findIndexByName(tLCContPlanFactorySchema
						.getFactoryName()) == -1) {
					tTransferData.setNameAndValue(tLCContPlanFactorySchema
							.getFactoryName(), tResult);
				
					if(tLCContPlanFactorySchema.getFactoryName().equals("DiseasePayPercent")){
						DiseasePayPercentFlag = true;
					}
					if(tLCContPlanFactorySchema.getFactoryName().equals("SuddennessPayPercent")){
						SuddennessPayPercentFlag = true;
					}
					if(tLCContPlanFactorySchema.getFactoryName().equals("PayPercent")){
						PayPercentFlag = true;
					}
				}
			}
		}
	    
	    
    	//�õ��⸶����
	    double GetRate = 0 ;
        
	    String tKind = tLLClaimDetailSchema.getGetDutyKind().substring(2, 3);
	    if(tKind != null && tKind.equals("1")){
	    	if(DiseasePayPercentFlag){
	    		GetRate = Double.parseDouble((String)tTransferData.getValueByName("DiseasePayPercent"));
	    	}
	    }else if(tKind != null && tKind.equals("2")){
	    	if(SuddennessPayPercentFlag){
	    		GetRate = Double.parseDouble((String)tTransferData.getValueByName("SuddennessPayPercent"));
	    	}
	    }
	    if(PayPercentFlag){
	    	GetRate = Double.parseDouble((String)tTransferData.getValueByName("PayPercent"));
	    }
	    if(GetRate == 0){//ȡ�����ϵĸ�������
	    	
		    SynLCLBDutyBL tSynLCLBDutyBL = new SynLCLBDutyBL();
	        //��ѯ������
	        boolean dutyflag = tSynLCLBDutyBL.Query(tLLClaimDetailSchema.
	                getPolNo(), tLLClaimDetailSchema.getDutyCode(),
	                tLLCaseSchema.getAccidentDate(), tLLCaseSchema.getAccidentDate());

	        if (dutyflag) {
	        	GetRate = tSynLCLBDutyBL.getGetRate();
	        }
	    	
	    }
	    if(GetRate == 0){
	    	GetRate = 1;
	    }
    	return GetRate;
    	
    }
	
	/**
	 * // ת�����ڸ�ʽ add by winnie ASR20107519
	 * @param date
	 * @param tag
	 * @return
	 */
	public String getDate(String date, boolean tag) {
		String tDate = "";
		if (tag) {
			tDate = date.substring(0, 4) + "��" + date.substring(5, 7) + "��"
					+ date.substring(8, 10) + "��";
		} else {
			tDate = date.substring(0, 4) + "/" + date.substring(5, 7) + "/"
					+ date.substring(8, 10);
		}
		return tDate;
	}    

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO �Զ����ɷ������
		LLClaimSMBatchBL claimSMBL = new LLClaimSMBatchBL();
		// LLCaseSchema caseSchema = new LLCaseSchema();
		// claimSMBL.caseMailSMControl(caseSchema);
		//claimSMBL.getRealPath();
		claimSMBL.autoRun();
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
	
	public void autoRun() {
		String rgtNo = "";
		//String[] rgtNoStr = {"201130095816001","201130095815001","201130095817001","201180095777"};  //��������1
		//String[] rgtNoStr = {"201130095724","201130095744","201130095770", "201152095766"}; //��������2
		String[] rgtNoStr = {"201130535393","201130535395","201130535396","201130535398","201130535517",
				"201130535518","201130535753","201130535943","201130535951","201130536167","201130536243",
				"201130536921","201130536957","201130536972","201130538393","201130538427","201130538447",
				"201130538461","201130538464","201130538467","201130538488","201130539350","201130539429",
				"201130539717","201130539722","201130539738","201130539739","201130540452","201130540579",
				"201130540836","201130541330","201130541359","201130541403","201130541416","201130541484",
				"201130541573","201130541631","201130541679","201130541684","201130541727","201130541734",
				"201130541808","201130541811","201130541843","201130541861","201130541864","201130542216",
				"201130542273","201130542418","201130542460","201130542472","201130542491","201130542526",
				"201130543006","201130543046","201130543085","201130543129","201130543196","201130543377",
				"201130543852","201130543865","201130543940","201130543981","201130543994","201130544123",
				"201130544792","201130544860","201130544905","201130545370","201130545504","201130545659",
				"201130546195","201130546229","201130546314","201130546380","201130546407","201130546599",
				"201130547223","201130547230","201130547430","201130547434","201130547436","201130548410",
				"201130548413","201130548499","201130548553","201130548795","201130548819","201130549296",
				"201130549297","201130549319","201130549794","201130549872","201130549874","201130550966",
				"201130550968","201130552092","201130552094","201130552569","201130553369"};
		for (int i = 0; i < rgtNoStr.length; i++) {
			rgtNo = rgtNoStr[i];
			System.out.println(i+1+" rgtNo:"+rgtNo);
			String caseNoSQL = "select * from llcase where rgtno = '"+rgtNo+"' and rgtstate in ('09','12')";
			LLCaseDB tLLCaseDB = new LLCaseDB();
			LLCaseSet tLLCaseSet = new LLCaseSet();
			
			LLClaimDB tLLClaimDB;
			LLClaimSet tLLClaimSet;
			LLClaimSchema tLLClaimSchema = new LLClaimSchema();
			
			//tLLCaseDB.setRgtNo(rgtNo);
			//tLLCaseSet = tLLCaseDB.query();
			tLLCaseSet = tLLCaseDB.executeQuery(caseNoSQL);
			for (int j = 1; j <= tLLCaseSet.size(); j++) {
				LLCaseSchema tLLCaseSchema = new LLCaseSchema();
				tLLCaseSchema = tLLCaseSet.get(j).getSchema();
				tLLClaimDB = new LLClaimDB();
				tLLClaimSet = new LLClaimSet();
				tLLClaimDB.setCaseNo(tLLCaseSchema.getCaseNo());
				tLLClaimSet = tLLClaimDB.query();
				if (tLLClaimSet.size() == 1)
				{
					tLLClaimSchema = tLLClaimSet.get(1).getSchema();
					if ("1".equals(tLLClaimSchema.getGiveType()) || "3".equals(tLLClaimSchema.getGiveType()))
					{
						VData tSMInputData = new VData();
						GlobalInput mG = new GlobalInput();
						TransferData tSMTransferData = new TransferData();
						mG.Operator = "batch";
						mG.OutUserFlag = "";
						mG.ManageCom = tLLCaseSchema.getMngCom();
						tSMTransferData.setNameAndValue("SaveFlag", "0"); //0Ϊ��LLClaimSMBL���ύ���ݿ�
						//tSMTransferData.setNameAndValue("SaveFlag", "1"); // ��LLClaimSMBL�಻�ύ���ݿ�
						// tSMTransferData.setNameAndValue("OutUserMngCom",mOutUserMngCom); //����û������������
						tSMTransferData.setNameAndValue("GiveType",tLLClaimSchema.getGiveType());//�������1������ܸ�3
						tSMInputData.add(mG);
						tSMInputData.add(tSMTransferData);
						tSMInputData.add(tLLCaseSchema);

						LLClaimSMBatchBL tLLClaimSMBL = new LLClaimSMBatchBL();
						if (!tLLClaimSMBL.submitData(tSMInputData, "INSERT")) {
							System.out.println(tLLClaimSMBL.mErrors.getFirstError());
						}
					}
				}
			}
		}
		System.out.println("&&&&&&&&&&&&&&&&&& �ʼ������������ &&&&&&&&&&&&&&&&&&");
	}

}

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
 * Fang 2009-10-15 ASR20094568 保全保单冻结和恢复功能扩展
 * Change1: 对于部分冻结的被保人赔案，理赔邮件和短信内容修改
 * ******************************************************************************************
 * Fang 2010-02-22 ASR20105662 S-程序修改-半冻结状态保单若干问题修改
 * Change2: 邮件短信根据广东格式修改
 * ******************************************************************************************
 * Fang 2010-03-03 ASR20105662 S-程序修改-半冻结状态保单若干问题修改
 * Change3: 根据被保人部分冻结时保全选定的理赔描述类型显示
 * ******************************************************************************************
 * Fang 2011-06-14 ASR20118337 S-其他问题-修改欠费保单的理赔冻结的功能 
 * Change4: 部分冻结 理赔邮件、短信模板统一
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

	private String mSaveFlag;// 如果mSaveFlag为null或者值等于""或者"0",表示本次调用需要保存结果,否则,不保存,只返回结果

	private String mOutUserMngCom = "";

	private String mGiveType = ""; // 存建议给付1 或建议拒付3

	private VData mInputData = new VData();

	private LLCaseDB mLLCaseDB = new LLCaseDB();

	private LCAddressDB mLCAddressDB = new LCAddressDB();

	private LLRegisterDB mLLRegisterDB = new LLRegisterDB();

	private LCInsuredBL mLCInsuredBL = new LCInsuredBL();

	private LJAGetSet mLJAGetSet = new LJAGetSet();

	private String templateMode;
	private boolean tFEFlag = false; // Added By Fang for Change1
	private String mailSubject = "团险理赔决定通知书";
	//add by winnie ASR20107519 
	private String logopath = "http://www.generalichina.com/image/emaillogo.jpg";
	//add by winnie 优化
	private ClaimNoticeBean tClaimNoticeBean=null;
	
	public LLClaimSMBatchBL() {
		// TODO 自动生成构造函数存根
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

		mOutUserMngCom = (String) mTransferData.getValueByName("OutUserMngCom"); // 外包用户保单管理机构

		mGiveType = (String) mTransferData.getValueByName("GiveType");

		if (mLLCaseSchema == null || mLLCaseSchema.getCaseNo() == null
				|| mLLCaseSchema.getCaseNo().equals("")) {
			buildError("getInputData", "传入案件信息的案件号不能为空");
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

		// 准备输出数据
		if (!prepareOutputData()) {
			return false;
		}

		if (mSaveFlag == null || mSaveFlag.trim().equals("")
				|| mSaveFlag.trim().equals("0")) {
			// 提交数据
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
			buildError("dealData", "查询个人案件表失败！");
			return false;
		}
		LLCasePolicyDB tLLCasePolicyDB = new LLCasePolicyDB();
		LLCasePolicySet tLLCasePolicySet = new LLCasePolicySet();
		LLCasePolicySchema tLLCasePolicySchema = new LLCasePolicySchema();
		tLLCasePolicyDB.setCaseNo(mLLCaseSchema.getCaseNo());
		tLLCasePolicySet = tLLCasePolicyDB.query();
		if (tLLCasePolicySet == null || tLLCasePolicySet.size() == 0) {
			buildError("dealData", "查询分案保单明细表失败！");
			return false;
		}
		tLLCasePolicySchema = tLLCasePolicySet.get(1);

		mLCInsuredBL.setInsuredNo(tLLCasePolicySchema.getInsuredNo());
		mLCInsuredBL.setContNo(tLLCasePolicySchema.getContNo());
		if (!mLCInsuredBL.getInfo()) {
			buildError("dealData", "查询个人被保险人表失败！");
			return false;
		}

		mLCAddressDB.setCustomerNo(mLCInsuredBL.getInsuredNo());
		mLCAddressDB.setAddressNo(mLCInsuredBL.getAddressNo());
		if (!mLCAddressDB.getInfo()) { // 没有查询到被保险人的信息
			// buildError("dealData", "查询个人客户地址表失败！");
			// return false;
			// 查主被保险人信息
			SynLCLBInsuredBL tSynLCLBInsuredBL = new SynLCLBInsuredBL();

			tSynLCLBInsuredBL.setGrpContNo(mLCInsuredBL.getGrpContNo());
			tSynLCLBInsuredBL.setInsuredNo(mLCInsuredBL.getMainInsuredNo());
			LCInsuredSet tLCInsuredSet = tSynLCLBInsuredBL.query();
			if (tLCInsuredSet == null || tLCInsuredSet.size() == 0) {
				buildError("dealData", "查询主被保险人信息失败！");
				return false;
			}
			mLCAddressDB = new LCAddressDB();
			mLCAddressDB.setCustomerNo(tLCInsuredSet.get(1).getInsuredNo());
			mLCAddressDB.setAddressNo(tLCInsuredSet.get(1).getAddressNo());
			if (!mLCAddressDB.getInfo()) {
				buildError("dealData", "查询个人客户地址表失败！");
				return false;
			}

		}

		mLLRegisterDB.setRgtNo(mLLCaseDB.getRgtNo());
		if (!mLLRegisterDB.getInfo()) {
			buildError("dealData", "查询个人立案表失败！");
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
		if (Integer.parseInt(mmExeSQL.getOneValue(tFETypeSQL)) > 0) { // 已经被部分冻结
			tFEFlag = true;
		}
		// End for Change1

		if (!caseMailSMControl(mLLCaseSchema)) {
			System.out.println("没有生成短信信息");
		}

		return true;
	}

	/**
	 * 生成mail及sm逻辑校验方法 校验分案的状态，如果是审核-05则进行立案邮件的处理，否则进行签批的邮件及短信处理。
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
		// 建议给付时，生成短信接口信息
		if ("1".equals(mGiveType)) {
			if (null != dieDate && !"".equals(dieDate)) {
				System.out.println("案件" + mLLCaseSchema.getCaseNo()
						+ "被保人身故不需生成邮件及手机短信。");
				return false;
			}
		}

		LLRegisterDB registerDB = new LLRegisterDB();
		registerDB.setRgtNo(mLLCaseSchema.getRgtNo());
		LLRegisterSchema registerSchema = registerDB.query().get(1);
		String templatePath = "";
		if ("05".equals(mLLCaseSchema.getRgtState())) {
			// 立案通知邮件的处理
			mailSubject = "理赔受理邮件通知";
			templatePath = readTemplate(registerSchema, mLLCaseSchema, "05",
					"email");
			if ("false".equals(templatePath)) {
				System.out.println("未查询到发送邮件的主被保险人！");
				return false;
			}
			if ("templateError".equals(templatePath)) {
				System.out.println("未查询到相应的模板信息！");
				return false;
			}
			try {
				InputStream is = new FileInputStream(templatePath);
				StringBuffer buffer = new StringBuffer();
				readToBuffer(buffer, is);
				System.out.println(buffer); // 将读到 buffer 中的内容写出来
				is.close();
				templateMode = buffer.toString();
			} catch (IOException e) {
				System.out.println(e + "-读取模板文件失败！");
				buildError("caseMailSMControl", "读取模板文件失败！");
				return false;
			}
			if (!dealMailList()) {
				System.out.println("没有生成邮件信息");
			}
		} else {
			if ("1".equals(registerSchema.getSingerSMFlag())) {
				templatePath = readTemplate(registerSchema, mLLCaseSchema,
						"09", "sm");
				if ("false".equals(templatePath)) {
					System.out.println("未查询到发送邮件的主被保险人！");
					return false;
				}
				if ("templateError".equals(templatePath)) {
					System.out.println("未查询到相应的模板信息！");
					return false;
				}
				try {
					InputStream is = new FileInputStream(templatePath);
					StringBuffer buffer = new StringBuffer();
					readToBuffer(buffer, is);
					//System.out.println(buffer); // 将读到 buffer 中的内容写出来
					is.close();
					templateMode = buffer.toString();
				} catch (IOException e) {
					System.out.println(e + "-读取模板文件失败！");
					buildError("caseMailSMControl", "读取模板文件失败！");
					return false;
				}
				if ("1".equals(mGiveType)) {
					if (!dealSMList()) {
						System.out.println("没有生成短信信息");
					}
				}
			}

			// 建议给付，建议拒付生成邮件接口信息 // 查询理赔通知方式，是否需要发送邮件 1 ：only E-mail 2 ：E-mail
			// and Hard copy
//			String strNoticeWay = " select count(1) from dual where casenoticeway('"
//					+ mLLCaseSchema.getCaseNo() + "') in ('1','2') ";
//			String tNoticeWay = mExeSQL.getOneValue(strNoticeWay);
//			if (tNoticeWay != null && !tNoticeWay.equals("")
//					&& tNoticeWay.equals("1")) {
				mailSubject = "团险理赔决定通知书";
				templatePath = readTemplate(registerSchema, mLLCaseSchema,
						"09", "email");
				if ("false".equals(templatePath)) {
					System.out.println("未查询到发送邮件的主被保险人！");
					return false;
				}
				if ("templateError".equals(templatePath)) {
					System.out.println("未查询到相应的模板信息！");
					return false;
				}
				try {
					InputStream is = new FileInputStream(templatePath);
					StringBuffer buffer = new StringBuffer();
					readToBuffer(buffer, is);
					//System.out.println(buffer); // 将读到 buffer 中的内容写出来
					is.close();
					templateMode = buffer.toString();
				} catch (IOException e) {
					System.out.println(e + "-读取模板文件失败！");
					buildError("caseMailSMControl", "读取模板文件失败！");
					return false;
				}
				if (!dealMailList()) {
					System.out.println("没有生成邮件信息");
				}

//			} else {
//				System.out.println("案件" + mLLCaseSchema.getCaseNo()
//						+ "理赔通知方式为:" + tNoticeWay + ",不需生成邮件信息");
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
			System.out.println("未查询到发送邮件的主被保险人！");
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

		// 如果分支机构上没有则查询保单上的
		if (null == claimNoticeTemplateDB.query().get(1)) {
			claimNoticeTemplateDB.setOrganComCode("86");
		}
		// 如果保单上也没有则使用系统默认模板
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
		String line; // 用来保存每行读取的内容
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		line = reader.readLine(); // 读取第一行
		while (line != null) { // 如果 line 为空说明读完了
			buffer.append(line); // 将读到的内容添加到 buffer 中
			buffer.append("\n"); // 添加换行符
			line = reader.readLine(); // 读取下一行
		}
	}

	/**
	 * 生成邮件接口信息
	 * 
	 * @return
	 */
	private boolean dealMailList() {
		String tGlphone = "";
		String tEMail = "";
		LCAddressSchema tLCAddressSchema = new LCAddressSchema();
		SynLCLBInsuredBL tSynLCLBInsuredBL = new SynLCLBInsuredBL();
		LCInsuredSchema tMainLCInsuredSchema = new LCInsuredSchema();// 主被保险人
		tLCAddressSchema = mLCAddressDB.getSchema();
		if (tLCAddressSchema.getEMail() == null
				|| "".equals(tLCAddressSchema.getEMail())) {
			if ((mLCInsuredBL.getMainInsuredNo() != null)
					&& (!"".equals(mLCInsuredBL.getMainInsuredNo()))) {
				// 得到主被保险人邮箱地址
				LCAddressDB tLCAddressDB = new LCAddressDB();
				tSynLCLBInsuredBL.setGrpContNo(mLCInsuredBL.getGrpContNo());
				tSynLCLBInsuredBL.setInsuredNo(mLCInsuredBL.getMainInsuredNo());
				LCInsuredSet tLCInsuredSet = tSynLCLBInsuredBL.query();
				if (tLCInsuredSet == null || tLCInsuredSet.size() == 0) {
					System.out.println("未查询到发送邮件的主被保险人！");
					return false;
				}
				tMainLCInsuredSchema = tLCInsuredSet.get(1);
				tLCAddressDB.setCustomerNo(tLCInsuredSet.get(1).getInsuredNo());
				tLCAddressDB.setAddressNo(tLCInsuredSet.get(1).getAddressNo());
				if (!tLCAddressDB.getInfo()) {
					System.out.println("查询发送邮件的主被保险人的地址表失败！");
					return false;
				}
				tLCAddressSchema = tLCAddressDB.getSchema();
				tEMail = tLCAddressSchema.getEMail();
				if ( tEMail == null ||"".equals(tEMail) ) {
					System.out.println("查询发送邮件的主被保险人的地址表失败！");
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
				System.out.println("发送邮件中没有主被保险人的信息！");
				return false;
			}
		} else {
			tEMail = tLCAddressSchema.getEMail();
			if (tEMail == null ||"".equals(tEMail) ) {
				System.out.println("查询发送邮件的主被保险人的地址表失败！");
				return false;
			}
			tMainLCInsuredSchema = mLCInsuredBL.getSchema();
			// 查询主被保险人信息
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
		String tPreClaimMoney = "0";// 计算索赔金额			
		LDComDB tLDComDB = new LDComDB();
		tLDComDB.setComCode(mLLCaseDB.getMngCom());
		if (!tLDComDB.getInfo()) {
			buildError("dealMailList", "查询ldcom表失败！");
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
		String tCurrentDate = sCalendar.get(Calendar.YEAR) + "年"
				+ (sCalendar.get(Calendar.MONTH) + 1) + "月"
				+ sCalendar.get(Calendar.DAY_OF_MONTH) + "日";
		String tRgtType=mLLCaseDB.getRgtType();
		if (tRgtType == null || tRgtType.equals("")) {
			System.out.println("无法区分是团体赔案还是普通赔案，数据有误！");
			return false;
		}
		if(tRgtType.equals("1")){
			//普通赔案
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
					//modify by winnie ASR20107519 邮件模版变更:赔付责任 赔付比例  赔付金额
					double getRate = 0;//赔付比例-
					String getRateIn="";//将赔付比例显示为百分比的方式
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
				if ("4".equals(tLJAGetSchema.getPayMode())) { // 银行转帐
					tBankInfo = tLJAGetSchema.getBankInfo();
					if(tBankInfo==null){
						tBankInfo="";
					}
					tAccNo = tLJAGetSchema.getBankAccNo();
					//add by winnie  ASR20106910 屏蔽银行账号/PIR
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
			// 申请日期
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
			
			// 计算索赔金额
			String SQL = "select llcase_preclaimmoney('" + mLLCaseDB.getCaseNo()
					+ "') from llcase where caseno='" + mLLCaseDB.getCaseNo() + "'";
			tPreClaimMoney = mExeSQL.getOneValue(SQL);

			// 帐单自费，自付信息,普通赔案特殊处理
			String feeDetail = getFeeDetail2();
			
			//模版基础信息
			//add by winnie ASR20107519 增加模版信息
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
	        
			templateMode = StringUtility.replace(templateMode, "$logopath", logopath);//logo地址
			templateMode=StringUtility.replace(templateMode, "$GrpContNo", mLCInsuredBL.getGrpContNo());//保单号 
			String beginDate="";//保险期间起始日期
			String endDate="";//保险期间终止日期
	        String SQLDate =
	            "select cvalidate,enddate-1 from lcpol where polno=(select polno from"
	            + " llcasepolicy where caseno='" + mLLCaseDB.getCaseNo() + "')";
	        SSRS aSSRS = mExeSQL.execSQL(SQLDate);
	        if (aSSRS.getMaxRow() > 0) {
	        	beginDate = aSSRS.GetText(1, 1);
	        	endDate = aSSRS.GetText(1, 2);
	        }else{
	        	//取B表的生效日，失效日
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
	        		endDate);//保险期间 $BeginDate - $EndDate
	        
	        templateMode = StringUtility.replace(templateMode, "$GrpName",
	        		tLCGrpContSchema.getGrpName());//保单持有人
	        templateMode = StringUtility.replace(templateMode, "$RCustomerNo",
	        		tLCGrpContSchema.getAppntNo());//单位客户号
	        templateMode = StringUtility.replace(templateMode, "$OrganComCode",
	        		tOrganComCode);//分支机构代码(取出险人的分支机构)
	        
	        templateMode = StringUtility.replace(templateMode, "$OrganComName",
	        		tOrganComCodeName);//分支机构名称
	        
	        templateMode = StringUtility.replace(templateMode, "$ActuGetNo", tActuGetNo);//支付号码 
	        if(getPersonNum>0){
		        templateMode = StringUtility.replace(templateMode, "$BankInfo",mLJAGetSet.get(1).getBankInfo());//银行名称
		        String tBankAccNo=mLJAGetSet.get(1).getBankAccNo();
		        if(tBankAccNo!=null&&!tBankAccNo.equals("")){
		        	int lenBAN=tBankAccNo.length();
		  			if(lenBAN>=8){
		  				tBankAccNo=tBankAccNo.substring(0,lenBAN-8)+"******"+tBankAccNo.substring(lenBAN-2, lenBAN);
		  			}else{
		  				tBankAccNo="******";
		  			}
		        }
		        templateMode = StringUtility.replace(templateMode, "$BankAccNo",tBankAccNo);//转账号码 
		        templateMode = StringUtility.replace(templateMode, "$AccName",mLJAGetSet.get(1).getAccName());//帐户持有人 
	        }else{
	        	 templateMode = StringUtility.replace(templateMode, "$BankInfo","");//银行名称
			     templateMode = StringUtility.replace(templateMode, "$BankAccNo","");//转账号码 
			     templateMode = StringUtility.replace(templateMode, "$AccName","");//帐户持有人 
	        }

			//end 20101123
			templateMode = StringUtility.replace(templateMode, "$CaseNo", mLLCaseDB
					.getCaseNo());//理赔号
			templateMode = StringUtility.replace(templateMode, "$PreClaimMoney",
					tPreClaimMoney);//合计索赔金额
			templateMode = StringUtility.replace(templateMode, "$MainCustomerName",
					tMainLCInsuredSchema.getName());//主被保险人姓名
			templateMode = StringUtility.replace(templateMode, "$MainCustomerNo",
					tMainLCInsuredSchema.getInsuredNo());
			//modify by winnie ASR20106910 屏蔽主被保人的身份证号
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
					tMIDNo);//主被保人
			//end modify 20100910
			templateMode = StringUtility.replace(templateMode, "$CustomerName",
					mLLCaseDB.getCustomerName());
			templateMode = StringUtility.replace(templateMode, "$CustomerNo",
					mLLCaseDB.getCustomerNo());
			//modify by winnie ASR20106910 屏蔽出险人的身份证号
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
			String tRemark = "1、请于2-5个工作日后查收；2、如转账成功，将视阁下已收到该笔理赔款项；3、如有疑问，请联系中意人寿客服人员。";
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
				//Amended By Fang for Change4(统一模板)
				mEdorReasonno2 = mExeSQL.getOneValue("select codealias from ldcode where codetype='claimtemplat' and comcode='86'");
				tRemark = mEdorReasonno2;
			}
			templateMode = StringUtility.replace(templateMode, "$Remark", tRemark);
			// End
		}
		
		if(tRgtType.equals("2")){
			//团体赔案
			//	add by winnie 团体理赔决定通知书优化
			tClaimNoticeBean=new ClaimNoticeBean(mLLCaseDB.getCaseNo());
			
			String tDetailPay = "";
			//赔付项目
			String[][] tLLClaimDetailList=tClaimNoticeBean.getClaimDetailList();
			for (int i = 0; i < tLLClaimDetailList.length; i++) {
				String tGetDutyName = "";//赔付责任
				String getRateIn="";//将赔付比例显示为百分比的方式
				String realpay="";//赔付金额
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
			sumPay=Double.parseDouble(tClaimNoticeBean.getSumRealPay());//实赔金额
			int getPersonNum = 0;
			if (mLJAGetSet != null) {
				getPersonNum = mLJAGetSet.size();
			}

			String tRemark1 = "";
			
			//评审意见
			if(tClaimNoticeBean.getRemark1()!=null){
				tRemark1=tClaimNoticeBean.getRemark1();
			}
			if(tClaimNoticeBean.getCalRule()!=null){
				tRemark1+=tClaimNoticeBean.getCalRule();
			}

			// 计算索赔金额
			tPreClaimMoney=tClaimNoticeBean.getSumFee();
			// 帐单自费，自付信息
			String feeDetail = getFeeDetail();
			
			//模版基础信息
			//add by winnie ASR20107519 增加模版信息
			String tActuGetNo="";
			if(getPersonNum>0){
				LJAGetSchema tLJAGetSchema = mLJAGetSet.get(1);
				tActuGetNo=tLJAGetSchema.getGetNoticeNo();//支付号码
			}

			String tOrganComCode=tClaimNoticeBean.getOrganComCode();//分支机构代码
			String tOrganComCodeName=tClaimNoticeBean.getOrganComName();//分支机构名称
			
			templateMode = StringUtility.replace(templateMode, "$logopath", logopath);//logo地址
		
			templateMode=StringUtility.replace(templateMode, "$GrpContNo", tClaimNoticeBean.getGrpContNo());//保单号 
			String beginDate="";//保险期间起始日期
			String endDate="";//保险期间终止日期

	        beginDate= tClaimNoticeBean.getBeginDate();//起始日期
	        templateMode = StringUtility.replace(templateMode, "$BeginDate",
	        		beginDate);
	        endDate= tClaimNoticeBean.getEndDate();//终止日期
	        templateMode = StringUtility.replace(templateMode, "$EndDate",
	        		endDate);//保险期间 $BeginDate -- $EndDate
	        
	        templateMode = StringUtility.replace(templateMode, "$GrpName",
	        		tClaimNoticeBean.getGrpName());//保单持有人

	        templateMode = StringUtility.replace(templateMode, "$RCustomerNo",
	        		tClaimNoticeBean.getAppntNo());//单位客户号
	        
	        templateMode = StringUtility.replace(templateMode, "$OrganComCode",
	        		tOrganComCode);//分支机构代码(取出险人的分支机构)
	        
	        templateMode = StringUtility.replace(templateMode, "$OrganComName",
	        		tOrganComCodeName);//分支机构名称
	        
	        templateMode = StringUtility.replace(templateMode, "$ActuGetNo", tActuGetNo);//支付号码 
	        
	        templateMode = StringUtility.replace(templateMode, "$BankInfo",tClaimNoticeBean.getBankInfo());//银行名称

	        String tBankAccNo=tClaimNoticeBean.getBankAccNo();
	        if(tBankAccNo!=null&&!tBankAccNo.equals("")){
		        int lenBAN=tBankAccNo.length();
				if(lenBAN>=8){
					tBankAccNo=tBankAccNo.substring(0,lenBAN-8)+"******"+tBankAccNo.substring(lenBAN-2, lenBAN);
				}else{
					tBankAccNo="******";
				}	        	
	        }

	        templateMode = StringUtility.replace(templateMode, "$BankAccNo",tBankAccNo);//转账号码 

	        templateMode = StringUtility.replace(templateMode, "$AccName",tClaimNoticeBean.getAccName());//帐户持有人 
			//end 20101123

			templateMode = StringUtility.replace(templateMode, "$CaseNo", tClaimNoticeBean
					.getCaseNo());//理赔号
			templateMode = StringUtility.replace(templateMode, "$PreClaimMoney",
					tPreClaimMoney);//合计索赔金额

			templateMode = StringUtility.replace(templateMode, "$MainCustomerName",
					tClaimNoticeBean.getMainName());//主被保险人姓名

			templateMode = StringUtility.replace(templateMode, "$MainCustomerNo",
					tClaimNoticeBean.getCustomerNo());//成员号码
			//modify by winnie ASR20106910 屏蔽主被保人的身份证号

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
					tMIDNo);//主被保人
			//end modify 20100910

			templateMode = StringUtility.replace(templateMode, "$CustomerName",
					tClaimNoticeBean.getCustomerName());
			//已废弃使用
//			templateMode = StringUtility.replace(templateMode, "$CustomerNo",
//					mLLCaseDB.getCustomerNo());
			//modify by winnie ASR20106910 屏蔽出险人的身份证号

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
//					tGetDetail);//领取表  目前已废弃
			templateMode = StringUtility.replace(templateMode, "$FeeDetail",
					feeDetail);
//			templateMode = StringUtility.replace(templateMode, "$ApplyDate",
//					applyDate);
			// Added By Fang for Change1
			//String tRemark = "1.请于三个工作日后查收；2.如转账成功，将视为阁下已收到该笔理赔款项。";//modify by winnie ASR20097519
			String tRemark = "1、请于2-5个工作日后查收；2、如转账成功，将视阁下已收到该笔理赔款项；3、如有疑问，请联系中意人寿客服人员。";
			if (tFEFlag) {
				// Amended By Fang for Change3
				// //Amended By Fang for Change2
				// //tRemark="您的理赔已经正常受理并理赔完毕，但由于贵司的保费未缴纳，您的理赔款暂时未能划出，敬请谅解。";
				// tRemark="您的理赔已经正常受理并理赔完毕，因贵公司正与我公司进行保费核算，理赔款将在保费核算完毕后尽快划出，敬请留意并谅解。";
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
				//Amended By Fang for Change4(统一模板)
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
		if (mGlobalInput.OutUserFlag.equals("1")) // 外包用户
		{
			tLimit = PubFun.getNoLimit(mOutUserMngCom);
		} else {
			tLimit = PubFun.getNoLimit(mGlobalInput.ManageCom);
		}

		String tSerialNo = PubFun1.CreateMaxNo("SERIALNO", tLimit);
		tLLMailListSchema.setGLPOLPFX("CH");
		tLLMailListSchema.setGLPOLCOY("3");
		tLLMailListSchema.setGLPOLNUM(mLCInsuredBL.getGrpContNo());// 保单号
		tLLMailListSchema.setGLRENNO(0);
		tLLMailListSchema.setGLCERT(mLLCaseDB.getCustomerNo());// 客户号
		tLLMailListSchema.setGLCERTPFIX("0");
		tLLMailListSchema.setGLCERTSEQ("1");
		tLLMailListSchema.setGlemail(tEMail);
		tLLMailListSchema.setGlphone(tGlphone);
		tLLMailListSchema.setGlmessage(inputStream);
		tLLMailListSchema.setGlstatus("0");
		tLLMailListSchema.setGltype(0);
		tLLMailListSchema.setGLCSEQNO("1");
		tLLMailListSchema.setGLCLAIM(mLLCaseDB.getCaseNo());// 理赔号

		if (mGlobalInput.OutUserFlag.equals("1")) // 外包用户,如果工作日表没有设置工作日，不会生成短信
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
				System.out.println("生成邮件时，查询下一工作日设置为空！");
				return false;
			}
		} else {
			tLLMailListSchema.setGlmessageDate(df.format(today));
		}
		tLLMailListSchema.setGlsubject(mailSubject);
		tLLMailListSchema.setGlsurname(mLLCaseDB.getCustomerName());// 被保险人姓名
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
	 * 获取帐单自费，自付信息
	 * 
	 * @return
	 */
	private String getFeeDetail() {
		String feeDetail = "";
		//modify by winnie ASR20107039  表头字段：就诊日期，就诊医院，类别，单据数量，单据金额，扣除金额，扣除说明    	
		ListTable tOtherListTable=tClaimNoticeBean.getTOtherListTable();//赔付明细、扣除手续费说明
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
	 * 获取帐单自费，自付信息
	 * 普通赔案
	 * @return
	 */
	private String getFeeDetail2() {
		String feeDetail = "";
//		modify by winnie ASR20107039  表头字段：就诊日期，就诊医院，类别，单据数量，单据金额，扣除金额，扣除说明
		//分段处理:1 第三方给付
		ExeSQL  mExeSQL=new ExeSQL();
		String otherGetSql="select '','','第三方给付',count(*),sum(nvl(sumfee,0)),sum(nvl(sumfee,0)) from llfeemain m where m.caseno='"+mLLCaseDB.getCaseNo()+"' and m.feetype='4' group by '','','第三方给付' having sum(nvl(sumfee, 0))>0";
		SSRS otherGetSSRS=new SSRS();
		otherGetSSRS = mExeSQL.execSQL(otherGetSql);
		if(otherGetSSRS!=null&&otherGetSSRS.getMaxRow()>0){
			for(int g=0;g<otherGetSSRS.getMaxRow();g++){
    			double sumMoney=0.00;
				String other_Info[]=new String[7];
    			other_Info[0]="";//就诊日期
    			other_Info[1]="";//就诊医院
    			other_Info[2]="第三方给付";//类别
    			other_Info[3]=otherGetSSRS.GetText(g+1, 4);//单证数量
    			other_Info[4]=otherGetSSRS.GetText(g+1, 5);//单据金额
    			other_Info[5]=otherGetSSRS.GetText(g+1, 6);//扣除金额
    			String gRemarkSQL="select otherorganname from llfeemain m where m.caseno='"+mLLCaseDB.getCaseNo()+"' and m.feetype='4'";//扣除说明
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
    			other_Info[6]=gRemark;//扣除说明
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
//		2 门诊：团体理赔决定通知书
		//类型为1的门诊数据，分组后的结果   
		String OtherSql="select to_char(a.Feedate,'yyyy/mm/dd'),HospitalCode,HospitalName,FeeType,count(*),sum(nvl(SumFee,0)) as sm,sum(nvl(SelfAmnt,0) +nvl(RefuseAmnt,0) + nvl(SPayAmnt,0)) as kcprem"
			+" from llfeemain a where a.CaseNo = '"+mLLCaseSchema.getCaseNo()+"' and a.feetype = '1'"
			+" group by a.feedate,a.HospitalCode,HospitalName,a.FeeType  having  sum(nvl(SumFee, 0))>0 "
			+" order by a.feedate,a.HospitalCode  "; 
		 System.out.println("门诊费用1: "+OtherSql);
         SSRS  OtherSSRS = mExeSQL.execSQL(OtherSql);
			if (OtherSSRS.getMaxRow() > 0) {
				SSRS rmSSRS1=new SSRS(); //存放扣除说明的结果集
				int len=OtherSSRS.getMaxRow();
				for (int a = 0; a < len; a++) {
    				String temp_O[]=new String[7];
					temp_O[0]=OtherSSRS.GetText(a+1, 1);//就诊日期
					temp_O[1]=OtherSSRS.GetText(a+1, 3);//就诊医院
					temp_O[2]="门诊";
					temp_O[3]=OtherSSRS.GetText(a+1, 5);//单据数量
					temp_O[4]=OtherSSRS.GetText(a+1, 6);//单据金额
					temp_O[5]=OtherSSRS.GetText(a+1, 7);//扣除金额
					//扣除说明
					String mRemark1="";
	        		String feeSql = "select to_char(a.Feedate,'yyyy/mm/dd'),HospitalCode,"
	        		       +"nvl(a.selfamntstatement,''),nvl(a.refuseamntstatement,''),nvl(a.spayamntstatement,'')"
	        		       +" from llfeemain a where a.CaseNo = '"+mLLCaseDB.getCaseNo()+"' and a.feetype='1'"
	        		       +" and a.FeeDate=to_Date('"+temp_O[0]+"','yyyy-MM-dd') and a.HospitalCode='"+OtherSSRS.GetText(a+1, 2)+"'";
	        		rmSSRS1=mExeSQL.execSQL(feeSql);
	        		if(rmSSRS1.getMaxRow()>0){
	        			for(int r=0;r<rmSSRS1.getMaxRow();r++){
	        				//自费
	        				if(rmSSRS1.GetText(r+1, 3)!=null&&!rmSSRS1.GetText(r+1, 3).equals("")){
	        					if(rmSSRS1.GetText(r+1, 3).endsWith(";")){
	        						mRemark1=mRemark1+rmSSRS1.GetText(r+1, 3);
	        					}else{
	        						mRemark1=mRemark1+rmSSRS1.GetText(r+1, 3)+";";
	        					}
	        				}
	        				//部分自付
	        				if(rmSSRS1.GetText(r+1, 4)!=null&&!rmSSRS1.GetText(r+1, 4).equals("")){
	        					if(rmSSRS1.GetText(r+1, 4).endsWith(";")){
	        						mRemark1=mRemark1+rmSSRS1.GetText(r+1, 4);
	        					}else{
	        						mRemark1=mRemark1+rmSSRS1.GetText(r+1, 4)+";";
	        					}	
	        				}
	        				//医保支付
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
			//住院：团体理赔决定通知书 
    		//类型为2的门诊数据，分组后的结果	
        	String OtherSql2="select to_char(a.HospStartDate,'yyyy/mm/dd'),to_char(a.HospEndDate,'yyyy/mm/dd'),HospitalCode,HospitalName,FeeType,count(*),sum(nvl(SumFee,0)) as sm,sum(nvl(SelfAmnt,0) +nvl(RefuseAmnt,0) + nvl(SPayAmnt,0)) as kcprem"
        			+" from llfeemain a where a.CaseNo = '"+mLLCaseDB.getCaseNo()+"' and a.feetype = '2'"
        			+" group by a.HospStartDate,a.HospEndDate,a.HospitalCode,HospitalName,a.FeeType having sum(nvl(SumFee, 0))>0 "
        			+" order by a.HospStartDate,a.HospEndDate,a.HospitalCode  "; 
            System.out.println("住院费用1: "+OtherSql2);
            SSRS OtherSSRS2 = mExeSQL.execSQL(OtherSql2);
        	if (OtherSSRS2.getMaxRow() > 0) {
        		SSRS rmSSRS2=new SSRS(); //存放扣除说明的结果集
        		for (int a = 0; a < OtherSSRS2.getMaxRow(); a++) {
        					String temp_O_Z[]=new String[7];
        					temp_O_Z[0]=OtherSSRS2.GetText(a+1, 1)+" -- "+OtherSSRS2.GetText(a+1, 2);//就诊日期
        					temp_O_Z[1]=OtherSSRS2.GetText(a+1, 4);//就诊医院
        					temp_O_Z[2]="住院";
        					temp_O_Z[3]=OtherSSRS2.GetText(a+1, 6);//单据数量
        					temp_O_Z[4]=OtherSSRS2.GetText(a+1, 7);//单据金额
        					temp_O_Z[5]=OtherSSRS2.GetText(a+1, 8);//扣除金额
        					//扣除说明
        					String mRemark2="";
        	        String feeSql = "select HospStartDate,HospEndDate,HospitalCode,"
        	               +"nvl(a.selfamntstatement,''),nvl(a.refuseamntstatement,''),nvl(a.spayamntstatement,'')"
        	               +" from llfeemain a where a.CaseNo = '"+mLLCaseDB.getCaseNo()+"' and a.feetype='2'"
        	               +" and a.HospStartDate=to_Date('"+OtherSSRS2.GetText(a+1, 1)+"','yyyy-MM-dd') and a.HospEndDate=to_Date('"+OtherSSRS2.GetText(a+1, 2)+"','yyyy-MM-dd') and a.HospitalCode='"+OtherSSRS2.GetText(a+1, 3)+"'";
        	        rmSSRS2=mExeSQL.execSQL(feeSql);
        	        if(rmSSRS2.getMaxRow()>0){
        	        	for(int r=0;r<rmSSRS2.getMaxRow();r++){
        	        		//自费
        	        		if(rmSSRS2.GetText(r+1, 4)!=null&&!rmSSRS2.GetText(r+1, 4).equals("")){
        	        			if(rmSSRS2.GetText(r+1, 4).endsWith(";")){
        	        				mRemark2=mRemark2+rmSSRS2.GetText(r+1, 4);
        	        			}else{
        	        				mRemark2=mRemark2+rmSSRS2.GetText(r+1, 4)+";";
        	        			}
        	        		} 
        	        		//部分自付
        	        		if(rmSSRS2.GetText(r+1, 5)!=null&&!rmSSRS2.GetText(r+1, 5).equals("")){
        	        			if(rmSSRS2.GetText(r+1, 5).endsWith(";")){
        	        				mRemark2=mRemark2+rmSSRS2.GetText(r+1, 5);
        	        			}else{
        	        				mRemark2=mRemark2+rmSSRS2.GetText(r+1, 5)+";";
        	        			}	
        	        		} 
        	        		//医保支付
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
	 * 生成短信接口信息
	 * 
	 * @return
	 */
	private boolean dealSMList() {
		// haoxh:2007-07-25:如果附带被保险人的手机号和主被保险人的手机号都为空的话就不发送短信
		String tMoblie = "";
		if (mLCAddressDB.getMobile() == null
				|| "".equals(mLCAddressDB.getMobile())) {
			if ((mLCInsuredBL.getMainInsuredNo() != null)
					&& (!"".equals(mLCInsuredBL.getMainInsuredNo()))
					&& !"0".equals(mLCAddressDB.getBP2())) {
				// 得到主被保险人手机号码
				SynLCLBInsuredBL tSynLCLBInsuredBL = new SynLCLBInsuredBL();
				LCAddressDB tLCAddressDB = new LCAddressDB();

				tSynLCLBInsuredBL.setGrpContNo(mLCInsuredBL.getGrpContNo());
				tSynLCLBInsuredBL.setInsuredNo(mLCInsuredBL.getMainInsuredNo());
				LCInsuredSet tLCInsuredSet = tSynLCLBInsuredBL.query();
				if (tLCInsuredSet == null || tLCInsuredSet.size() == 0) {
					System.out.println("未查询到主被保险人！");
					return false;
				}
				tLCAddressDB.setCustomerNo(tLCInsuredSet.get(1).getInsuredNo());
				tLCAddressDB.setAddressNo(tLCInsuredSet.get(1).getAddressNo());
				if (!tLCAddressDB.getInfo()) {
					System.out.println("查询主被保险人地址表失败！");
					return false;
				}
				tMoblie = tLCAddressDB.getSchema().getMobile();
				if ("".equals(tMoblie) || tMoblie == null) {
					System.out.println("查询主被保险人的手机号码为空！");
					return false;
				}
			} else {
				return false;
			}
		} else {
			tMoblie = mLCAddressDB.getMobile();
			if ("".equals(tMoblie) || tMoblie == null
					|| "0".equals(mLCAddressDB.getBP2())) {
				System.out.println("查询被保险人或主被保险人的手机号码为空或关闭短信通知服务！");
				return false;
			}
		}
		// 对手机号的校验(是否为数字，长度是否小于20)
		if (!tMoblie.equals("") || tMoblie != null) {
			if (!PubFun.isNumeric(tMoblie) || (tMoblie.length() >= 20)) {
				System.out.println("查询被保险人或主被保险人的手机号码有误为" + tMoblie + "！");
				return false;
			}
		}
		LLSMListSchema tLLSMListSchema = new LLSMListSchema();

		String tLimit = "";
		if (mGlobalInput.OutUserFlag.equals("1")) // 外包用户
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
		String tNewApplyDate = sCalendar.get(Calendar.YEAR) + "年"
				+ (sCalendar.get(Calendar.MONTH) + 1) + "月"
				+ sCalendar.get(Calendar.DAY_OF_MONTH) + "日";

		String tRealPaySQL = "select nvl(sum(realpay),0) from llclaimdetail where caseno='"
				+ mLLCaseSchema.getCaseNo() + "'";
		ExeSQL tExeSQL = new ExeSQL();
		String tSumRealPay = tExeSQL.getOneValue(tRealPaySQL);

		// Amended By Fang For Change1
		// String tSMContent = "尊敬的" + mLLCaseDB.getCustomerName() + "：您于"
		// + tNewApplyDate + "提交的理赔申请已处理完毕，赔款金额" + tSumRealPay +
		// "元，请于3个工作日后查收。";
		String tSMContent = "";
		if (tFEFlag) {
			// Amended By Fang for Change3
			// //Amended By Fang for Change2
			// //tSMContent = "尊敬的" + mLLCaseDB.getCustomerName() + "：您于"
			// //+ tNewApplyDate + "提交的理赔申请已处理完毕，赔款金额" + tSumRealPay +
			// "元，但由于贵司保费未缴纳，理赔款未划出。";
			// tSMContent = "尊敬的" + mLLCaseDB.getCustomerName() + "：您于"
			// + tNewApplyDate + "提交的理赔申请已处理完毕，赔款金额" + tSumRealPay +
			// "元，由于我司正与贵司核算保费，理赔款将在保费核算完毕后尽快划出，敬请留意并谅解。";
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
			//Amended By Fang for Change4(统一模板)
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

			// tSMContent = "尊敬的" + mLLCaseDB.getCustomerName() + "：您于"
			// + tNewApplyDate + "提交的理赔申请已处理完毕，赔款金额" + tSumRealPay
			// + "元，请于3个工作日后查收。";
			if ("0".equals(tSumRealPay)) {
				tSMContent = "尊敬的" + mLLCaseDB.getCustomerName()
						+ "：您的索赔已处理结束，详情请见理赔通知、中意网站或联系客服人员。中意人寿";
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
			System.out.println("没有查询到Carriercode按照立案机构编码写入对应的代码");
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

		if (mGlobalInput.OutUserFlag.equals("1")) // 外包用户,如果工作日表没有设置工作日，不会生成短信
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
				System.out.println("生成短信时，查询下一工作日设置为空！");
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
		tLLSMListSchema.setPAD2(mLLCaseSchema.getCaseNo());// 案件号
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
			// @@错误处理
			buildError("prepareOutputData", "在准备往后层处理所需要的数据时出错");
			return false;
		}
		return true;
	}

	/**
	 * 生成错误信息
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
		if (StrTool.cTrim(strRealPath).equals("")) {// 如果没找到，再通过class文件找
			File path = new File(LLClaimSMBatchBL.class.getResource("/").getFile());
			strRealPath = path.getParentFile().getParentFile().toString();
		}

		return strRealPath;
	}
	 /**
     * 获取保障计划编码 add by winnie ASR20107519
     */
    private String getContPlanCode(LLClaimDetailSchema tLLClaimDetailSchema , LLCaseSchema tLLCaseSchema){
        String tContPlanCode = "";
        //保障计划变更后，需要查找以前的保障计划
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
        		System.out.println("查找保障计划变更保全表LPInsured表失败！");
        	}else{
        		tContPlanCode = tLPInsuredDB.getContPlanCode();
        	}
        }
        if("".equals(tContPlanCode)){
        	LCInsuredBL tLCInsuredBL = new LCInsuredBL();
        	tLCInsuredBL.setInsuredNo(tLLCaseSchema.getCustomerNo());
    		tLCInsuredBL.setContNo(tLLClaimDetailSchema.getContNo());
    		if (!tLCInsuredBL.getInfo()) {
    			System.out.println("没查找到保障计划！");
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
        //获取保障计划编码
        String tContPlanCode = getContPlanCode(tLLClaimDetailSchema , tLLCaseSchema);
      
        //取理赔要约要素 除了理算给付金算法
	    LCContPlanFactoryDB tLCContPlanFactoryDB = new LCContPlanFactoryDB();
	    LCContPlanFactorySet tLCContPlanFactorySet = new LCContPlanFactorySet();
	        
	    String strSQL = "select * from LCContPlanFactory where grpcontno='"+tLLClaimDetailSchema.getGrpContNo()+"' " +
	        	" and riskcode='"+tLLClaimDetailSchema.getRiskCode()+"' " +
	        	" and ContPlanCode = '"+tContPlanCode+"' " +
	        	" and FactoryType='000004' and OtherNo='"+tLLClaimDetailSchema.getGetDutyCode()+"' and FactoryCode<>'000007'";
	    tLCContPlanFactorySet = tLCContPlanFactoryDB.executeQuery(strSQL);
	    TransferData tTransferData = new TransferData();
	    PubCalculator tempPubCalculator = new PubCalculator();
	    boolean DiseasePayPercentFlag = false;//疾病赔付比例
	    boolean SuddennessPayPercentFlag = false;//意外赔付比例
	    boolean PayPercentFlag = false; //NIK12的赔付比例
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
	    
	    
    	//得到赔付比例
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
	    if(GetRate == 0){//取责任上的给付比例
	    	
		    SynLCLBDutyBL tSynLCLBDutyBL = new SynLCLBDutyBL();
	        //查询责任项
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
	 * // 转换日期格式 add by winnie ASR20107519
	 * @param date
	 * @param tag
	 * @return
	 */
	public String getDate(String date, boolean tag) {
		String tDate = "";
		if (tag) {
			tDate = date.substring(0, 4) + "年" + date.substring(5, 7) + "月"
					+ date.substring(8, 10) + "日";
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
		// TODO 自动生成方法存根
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
		//String[] rgtNoStr = {"201130095816001","201130095815001","201130095817001","201180095777"};  //测试数据1
		//String[] rgtNoStr = {"201130095724","201130095744","201130095770", "201152095766"}; //测试数据2
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
						tSMTransferData.setNameAndValue("SaveFlag", "0"); //0为在LLClaimSMBL类提交数据库
						//tSMTransferData.setNameAndValue("SaveFlag", "1"); // 在LLClaimSMBL类不提交数据库
						// tSMTransferData.setNameAndValue("OutUserMngCom",mOutUserMngCom); //外包用户保单管理机构
						tSMTransferData.setNameAndValue("GiveType",tLLClaimSchema.getGiveType());//建议给付1，或建议拒付3
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
		System.out.println("&&&&&&&&&&&&&&&&&& 邮件短信生成完毕 &&&&&&&&&&&&&&&&&&");
	}

}

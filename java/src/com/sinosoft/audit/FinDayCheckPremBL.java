package com.sinosoft.lis.f1print;

import com.sinosoft.lis.pubfun.*;
import com.sinosoft.utility.*;
import com.sinosoft.lis.schema.LJEveryBalanceSchema;
import com.sinosoft.lis.vschema.LJEveryBalanceSet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Calendar;

public class FinDayCheckPremBL {

	/** �������࣬ÿ����Ҫ����������ж����ø��� */
	public CErrors mErrors = new CErrors();

	private VData mResult = new VData();

	private String mGrpContNo = "";

	private String mGrpName = "";

	private String mStartDate = "";

	private String mEndDate = "";

	private String mPrtNo = "";

	private XmlExport xmlexport = new XmlExport();

	// private TextTag mTextTag = new TextTag();

	private String mCurrentDate = PubFun.getCurrentDate();

	ExeSQL tExeSQL = new ExeSQL();

	private String folderPath;// �����ļ�·��

	private String ASFlag;// ��������ҳ�滹���������������ɱ���
	String DMFlag="";//������ս���½�ı��
	String strRealPath="";//��ȡUI��Ŀ¼·��
	// ҵ������ر���
	/** ȫ������ */
	private GlobalInput mGlobalInput = new GlobalInput();

	public FinDayCheckPremBL() {
	}

	/**
	 * �������ݵĹ�������
	 * 
	 * @param cInputData
	 *            VData
	 * @param cOperate
	 *            String
	 * @return boolean
	 */
	public boolean submitData(VData cInputData, String cOperate) {
		// mOperate = cOperate;
		// �õ��ⲿ��������ݣ������ݱ��ݵ�������
		if (!getInputData(cInputData)) {
			return false;
		}
		mResult.clear();
		// ׼������Ҫ��ӡ������
		if (!getPrintData()) {
			return false;
		}
		return true;
	}

	/**
	 * �����������еõ����ж��� ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
	 * 
	 * @param cInputData
	 *            VData
	 * @return boolean
	 */
	private boolean getInputData(VData cInputData) {
		// ȫ�ֱ���
		String[] mDay = (String[]) cInputData.get(0);
		mStartDate = mDay[0];
		mEndDate = mDay[1];
		mGrpName = mDay[2];
		mGrpContNo = mDay[3];
		mPrtNo = mDay[4];
		folderPath = mDay[5];
		ASFlag = mDay[6];
		DMFlag=mDay[7];//��ȡ��/����������
		strRealPath=mDay[8];//��ȡUI��Ŀ¼·��
		this.mGlobalInput = ((GlobalInput) cInputData.getObjectByObjectName(
				"GlobalInput", 0));
		// mManageCom = mGlobalInput.ManageCom;
		if (mStartDate == null || mStartDate.equals("")) {
			buildError("submitData", "��ʼ���ڲ���Ϊ�գ�");
			return false;
		}
		if (mEndDate == null || mEndDate.equals("")) {
			buildError("submitData", "��ֹ���ڲ���Ϊ�գ�");
			return false;
		}

		if (mStartDate.compareTo(mEndDate) > 0) {
			buildError("submitData", "��ʼ���ڲ��ܴ�����ֹ���ڣ�");
			return false;
		}
		return true;
	}

	/**
	 * ���ش�����Ϣ
	 * 
	 * @return VData
	 */
	public VData getResult() {
		return mResult;
	}

	/**
	 * ���ش�����Ϣ
	 * 
	 * @return CErrors
	 */
	public CErrors getErrors() {
		return mErrors;
	}

	/**
	 * ���󹹽�
	 * 
	 * @param szFunc
	 *            String
	 * @param szErrMsg
	 *            String
	 */
	private void buildError(String szFunc, String szErrMsg) {
		CError cError = new CError();
		cError.moduleName = "PayGetNotice";
		cError.functionName = szFunc;
		cError.errorMessage = szErrMsg;
		this.mErrors.addOneError(cError);
	}

	/**
	 * ��ӡ����
	 * 
	 * @return boolean
	 */
	private boolean getPrintData() {
		if (!getPrintDataOUT()) {
			return false;
		}

		return true;
	}

	// �����ս�
	private boolean getPrintDataOUT() {
		if(ASFlag.equals("A"))//������������������ɵı����������ļ����̶�Ŀ¼��
		{
			xmlexport.createDocument("FinDayCheckPremOut.htm", "");
		}
		else
		{
		xmlexport.createDocument("FinDayCheckPremOut.vts", "");
		}
		TextTag mTextTag = new TextTag();
		mTextTag.add("CurrentDate", mCurrentDate); // ��ӡ����
		System.out.println("ͳ�����ɱ������ʼ���ڣ�" + mStartDate + " �������ڣ�" + mEndDate);
		SSRS mSSRS = new SSRS();
		SSRS tSSRS = new SSRS();
		String SQL[] = new String[30];
		String tSQL1 = "";
		String tSQL2 = "";
		String tSQL3 = "";
		String tSQL4 = "";
		String tSQL5 = "";
		String tSQL6 = "";
		String tSQL7 = "";
		String tSQL8 = "";
		String tSQL9 = "";
		String tSQL10 = "";
		String tSQL11 = "";
		String tSQL12 = "";
		String tSQL13 = "";
		String tSQL14 = "";
		String tSQL15 = "";
		String tSQL16 = "";
		String tSQL17 = "";
		String tSQL18 = "";
		String tSQL19 = "";
		String tSQL20 = "";
		String tSQL21 = "";
		String tSQL22 = "";
		String tSQL23 = "";
		String tSQL24 = "";
		String tSQL25 = "";
		String tSQL26 = "";
		String tSQL27 = "";
		String tSQL28 = "";
		String tSQLA="";
		ExeSQL tExeSQLA = new ExeSQL();
		SSRS aSSRS = new SSRS();
		ListTable tlistTable = new ListTable();
		String strArr[] = null;
		tlistTable.setName("PayReportPIT"); // ��ģ��ı������Pay,������ģ������һ��

		// ���ڽ������ȷ��
		tSQL6 = "  select c.modifydate,decode(b.branchtype,'1','KA','2','DB','3','GA'),b.entryno,c.otherno,d.grpname ,"
				+ "a.riskcode,(select riskname from lmrisk where riskcode = a.riskcode),"
				+ "-a.money,c.tempfeeno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//�����������
				+" From ljtempfee c,lcgrpcont d,ljpbalancerelasub a,laagent b "
				+ "where c.tempfeetype ='8' and c.otherno=d.grpcontno and b.agentcode=d.agentcode "
				+ " and c.tempfeeno=a.balancerelano and c.modifydate >= '"
				+ mStartDate
				+ "' and not exists(select 'x' from ljtempfeeclass where "
				+ "tempfeeno=c.tempfeeno and paymode in ('B','J'))  and c.modifydate <= '"
				+ mEndDate
		//		+ "' and a.riskcode<>'NIK04' "  //ASR20118405_NIK04��Ʒ����ӿ�
				+"'  and c.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%' and c.financedate is not null  "
				+ "and not exists(select 'x' from lbgrpcont where grpcontno=d.grpcontno)";
		// ��ȫ����ȷ�� ---����ȷ�ϣ�ת��ljagetendorse����(����)
		tSQL1 = " select c.modifydate,decode(d.branchtype,'1','KA','2','DB','3','GA'), d.entryno,b.grpcontno,b.grpname,"
				+ "a.riskcode,(select riskname from lmrisk where riskcode = a.riskcode),"
				+ "sum(-a.getmoney),c.tempfeeno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = b.salechnl)"
				+" ,b.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=b.managecom)"//�����������
				+" From ljtempfee c ,lcgrpcont b,ljagetendorse a,laagent d where "
				+ "c.tempfeetype ='4' and b.agentcode=d.agentcode and a.GetMoney<>0 "
				+ " and c.tempfeeno=a.getnoticeno and b.grpcontno=a.grpcontno "
				+ " and not exists(select 'x' from ljtempfeeclass where "
				+ "tempfeeno=c.tempfeeno and paymode in ('B','J'))  and c.modifydate >= '"
				+ mStartDate
				+ "' and c.modifydate <= '"
				+ mEndDate
				+ "' and a.riskcode<>'NIK04' and c.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%'  and c.financedate is not null  and not exists(select 'x' from lbgrpcont where grpcontno=b.grpcontno)";
		// ��ȫ����ȷ�� ---����ȷ�ϣ�ת��ljapayperson���� (����)
		tSQL10 = " select c.modifydate,decode(d.branchtype,'1','KA','2','DB','3','GA'), d.entryno,b.grpcontno,b.grpname,"
				+ "a.riskcode,(select riskname from lmrisk where riskcode = a.riskcode),"
				+ "sum(-a.sumduepaymoney),c.tempfeeno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = b.salechnl)"
				+" ,b.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=b.managecom)"//�����������
				+" From ljtempfee c ,lcgrpcont b,ljapayperson a,"
				+ "laagent d,ljapay e  where e.payno=a.payno and a.sumduepaymoney<>0 and "
				+ "c.tempfeetype ='4' and b.agentcode=d.agentcode "
				+ " and c.tempfeeno=e.getnoticeno and b.grpcontno=a.grpcontno "
				+ " and not exists(select 'x' from ljtempfeeclass where "
				+ "tempfeeno=c.tempfeeno and paymode in ('B','J'))  and c.modifydate >= '"
				+ mStartDate
				+ "' and c.modifydate <= '"
				+ mEndDate
				+ "' and a.riskcode<>'NIK04' and c.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%'  and c.financedate is not null  and not exists(select 'x' from lbgrpcont where grpcontno=b.grpcontno)";
		// ��ȫ����ȷ�� �����ǵ���ȷ�ϣ���ljsgetendorse�� (����)
		tSQL7 = " select c.modifydate,decode(d.branchtype,'1','KA','2','DB','3','GA'), d.entryno,b.grpcontno,b.grpname,a.riskcode,(select "
				+ "riskname from lmrisk where riskcode = a.riskcode),sum(-a.getmoney),c.tempfeeno"
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = b.salechnl)"
				+" ,b.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=b.managecom)"//�����������
				+ " From ljtempfee c ,lcgrpcont b,ljsgetendorse a,laagent d where c.tempfeetype ='4' "
				+ " and c.tempfeeno=a.getnoticeno and b.grpcontno=a.grpcontno "
				+ " and d.agentcode=b.agentcode and a.GetMoney<>0 and c.modifydate >= '"
				+ mStartDate
				+ "' and not exists(select 'x' from ljtempfeeclass where "
				+ "tempfeeno=c.tempfeeno and paymode in ('B','J'))  and c.modifydate <= '"
				+ mEndDate
				+ "' and a.riskcode<>'NIK04' and c.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%'  and c.financedate is not null "
				+ " and not exists(select 'x' from lbgrpcont where grpcontno=b.grpcontno)";
		// ��Ȩǩ��
		tSQL2 = " select a.makedate ,decode(d.branchtype,'1','KA','2','DB','3','GA'), d.entryno,c.grpcontno,c.grpname,"
				+ "b.riskcode,(select riskname from lmrisk  where riskcode = b.riskcode), "
				+ "sum(b.sumactupaymoney),a.payno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//�����������
				+" from ljapay a,lcgrpcont c,laagent d,ljapaygrp b where a.incomeno=c.grpcontno and  "
				+ "incometype in ('B','J') and b.payno=a.payno and c.grpgroupno is null  "
				+ " and d.agentcode=c.agentcode "
		//		+" and b.riskcode<> 'NIK04'"  //ASR20118405_NIK04��Ʒ����ӿ�
				+" and a.makedate >='"
				+ mStartDate
				+ "'"
				+ " and a.makedate <='"
				+ mEndDate
				+ "' and a.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%'  and not exists(select 'x' from lbgrpcont where grpcontno=c.grpcontno)";

		// ��ȫ�˷� ����ȷ��  (����,����Ԥ�շ�Ӧ��)
		tSQL3 = " select a.modifydate ,decode(c.branchtype,'1','KA','2',"
				+ "'DB','3','GA'),c.entryno, d.grpcontno,d.grpname,"
				+ "b.riskcode,f.riskname ,sum(-b.getmoney),a.actugetno  "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//�����������
				+" From LJAGet a,"
				+ "laagent c,lcgrpcont d, ljagetendorse b,lmriskapp f where "
				+ "b.feefinatype not in ('TQ', 'SX') and b.riskcode<>'NIK04' and " //fengyan PIR20092040
				+ "b.feeoperationtype<>'WT'  and a.paymode<>'A' and ( f.riskperiod<>'L') "//ֻ�ų����գ���������Ϊ and  f.risktype<>'L'
				+ " and f.riskcode=b.riskcode and not exists  (select 'x' from  "
				+ " ljpbalancerela where payno=a.actugetno) and d.grpcontno=b.grpcontno "
				+ " and a.actugetno=b.actugetno and c.agentcode=b.agentcode and a.modifydate >= '"
				+ mStartDate
				+ "' and a.modifydate <= '"
				+ mEndDate
				+ "' and b.GetMoney<>0 and a.financedate is not null  and a.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%'  and not exists(select 'x' from lbgrpcont where grpcontno=d.grpcontno)";
		// ��ȫ�˷� ����ȷ��
		// tSQL20 = " select a.modifydate ,decode(c.branchtype,'1','KA','2',"
		// + "'DB','3','GA'),c.entryno, d.grpcontno,d.grpname,"
		// + "b.riskcode,(select riskname from lmrisk where riskcode =
		// b.riskcode),"
		// + "sum(-b.sumactupaymoney),a.actugetno From LJAGet a,laagent
		// c,lcgrpcont d, "
		// + "ljapayperson b where not exists (select 'x' from "
		// + " ljpbalancerela where payno=a.actugetno) and
		// d.grpcontno=b.grpcontno "
		// + " and a.actugetno=b.payno and c.agentcode=b.agentcode and
		// a.modifydate >= '"
		// + mStartDate
		// + "' and a.modifydate <= '"
		// + mEndDate
		// + "' and b.sumactupaymoney<>0 and a.financedate is not null and
		// a.managecom like '"
		// + mGlobalInput.ManageCom
		// + "%' and not exists(select 'x' from lbgrpcont where
		// grpcontno=d.grpcontno)";
		// ���ڽ����˷� ����ȷ��
		tSQL12 = " select a.modifydate ,decode(c.branchtype,'1','KA','2',"
				+ "'DB','3','GA'),c.entryno, d.grpcontno,d.grpname,"
				+ "e.riskcode,(select riskname from lmrisk  where riskcode = e.riskcode),"
				+ "sum(-e.money),a.actugetno  "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//�����������
				+" From LJAGet a,laagent c,lcgrpcont d, "
				+ "ljpbalancerelasub e where e.balancerelano=a.actugetno "
				+" and exists "
				+" (select r.grpcontno from ljpbalancerela r where r.balancerelano = e.balancerelano and r.grpcontno = d.grpcontno)"
			//	+ "and  e.riskcode<>'NIK04' "  //ASR20118405_NIK04��Ʒ����ӿ�
				+ "  and c.agentcode=d.agentcode and a.modifydate >= '"
				+ mStartDate
				+ "' and a.modifydate <= '"
				+ mEndDate
				+ "' and a.financedate is not null  and a.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%'  and not exists(select 'x' from lbgrpcont where grpcontno=d.grpcontno)";

		// ���ڽ�������󻻺�
		tSQL4 = " select c.modifydate,decode(b.branchtype,'1','KA','2','DB','3','GA'),"
				+ "b.entryno,c.otherno,d.grpname ,a.riskcode,(select riskname from lmrisk"
				+ " where riskcode = a.riskcode),-a.money,c.tempfeeno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//�����������
				+" From ljtempfee c,lcgrpcont d,"
				+ "ljpbalancerelasub a,laagent b,ljapay e where c.tempfeetype ='8' and"
				+ " c.otherno=d.grpcontno and b.agentcode=d.agentcode "
				+ "and c.tempfeeno=e.getnoticeno "
			//	+"  and a.riskcode<>'NIK04' "  //ASR20118405_NIK04��Ʒ����ӿ�
				+ " and e.payno=a.balancerelano and c.modifydate >= '"
				+ mStartDate
				+ "' and not exists(select 'x' from ljtempfeeclass where "
				+ "tempfeeno=c.tempfeeno and paymode in ('B','J'))  and c.modifydate <= '"
				+ mEndDate
				+ "' and c.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%' and c.financedate is not null  "
				+ "and not exists(select 'x' from lbgrpcont where grpcontno=d.grpcontno)";
		// ��ȫ����ǩ��+���ڽ���===��ljapayperson��ȡ��
		tSQL9 = " select a.makedate ,decode(d.branchtype,'1','KA','2','DB','3','GA'), d.entryno,c.grpcontno,c.grpname,"
				+ "b.riskcode,(select riskname from lmrisk  where riskcode = b.riskcode), "
				+ " sum(b.sumactupaymoney),a.payno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//�����������
				+" from ljapay a,lcgrpcont c,laagent d,ljapayperson b "
				+ "where b.grpcontno = c.grpcontno and exists(select 'x' from lpedormain"
				+ " where edorno=a.incomeno) and a.payno=b.payno and c.grpgroupno is null"
				//+ " and d.agentcode=c.agentcode and b.riskcode<> 'NIK04' and a.makedate >='"
				+ " and d.agentcode=c.agentcode and a.makedate >='"
				+ mStartDate
				+ "'"
				+ " and a.makedate <='"
				+ mEndDate
				+ "' and b.sumactupaymoney<>0 and a.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%'  and not exists(select 'x' from lbgrpcont where grpcontno=b.grpcontno)"
				+" and a.incometype in ('B','J')"//��Ȩ��ȫ�շ������������Ŀ
				;//�շ������г��սӿ�
		// ��ȫ����ǩ��+���ڽ���===��ljagetendorse��ȡ��
		tSQL5 = " select b.makedate,decode(c.branchtype,'1','KA','2','DB','3','GA'),c.entryno,d.grpcontno,d.grpname,"
				+ "a.riskcode,(select riskname from lmrisk  where riskcode = a.riskcode), "
				+ "sum(a.getmoney),a.actugetno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//�����������
				+" from ljagetendorse a,lcgrpcont d,ljapay b,laagent c "
				+ "where b.payno=a.actugetno and d.grpgroupno is null"
				+ " and c.agentcode=d.agentcode and d.grpcontno= a.grpcontno "
				+" and b.incometype in ('B','J')"//��Ȩ��ȫ�շ������������Ŀ
				//+ " and a.GetMoney<>0 and a.riskcode<> 'NIK04' and b.makedate >='"
				+ " and a.GetMoney<>0 and b.makedate >='"
				+ mStartDate
				+ "'"
				+ " and b.makedate <='"
				+ mEndDate
				+ "' and a.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%'  and not exists(select 'x' from lbgrpcont where grpcontno=d.grpcontno)";
		// ��ȫ����ǩ�� ,��ȥ��������
		tSQL8 = " select b.makedate,decode(c.branchtype,'1','KA','2','DB','3','GA'),c.entryno,d.grpcontno,d.grpname,"
				+ "a.riskcode,(select riskname from lmrisk  where riskcode = a.riskcode), "
				+ "sum(a.getmoney),a.actugetno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//�����������
				+" from ljagetendorse a,lcgrpcont d,ljaget b,laagent c "
				+ "where b.actugetno=a.actugetno and not exists(select 'x' from  "
				+ " ljpbalancerela where balancerelano=b.actugetno)"
				//+ "  and d.grpgroupno is null and a.feeoperationtype <> 'ZH' and a.riskcode<> 'NIK04' "
				+ "  and d.grpgroupno is null and a.feeoperationtype <> 'ZH' "
//				+" and not exists (select k.riskcode from lmriskapp k where k.riskcode=a.riskcode and "
//				+" (k.riskperiod='L' ) ) "//ֻ�ų����գ���������Ϊ or k.risktype='L'
				+" and b.tqflag ='Y'"//��Ȩ��ȫ�շ������������Ŀ
				+ " and c.agentcode=d.agentcode and d.grpcontno= a.grpcontno  and b.makedate >='"
				+ mStartDate
				+ "' and b.makedate <='"
				+ mEndDate
				+ "'  and a.GetMoney<>0 and b.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%'  and not exists(select 'x' from lbgrpcont where grpcontno=a.grpcontno)";
		// ��ȫ����ǩ��
		tSQL11 = " union all select b.makedate,decode(c.branchtype,'1','KA','2','DB','3','GA'),c.entryno,d.grpcontno,d.grpname,"
				+ "a.riskcode,(select riskname from lmrisk  where riskcode = a.riskcode), "
				+ "sum(a.getmoney),a.actugetno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//�����������
				+" from ljagetendorse a,lbgrpcont d,ljaget b,laagent c "
				+ "where b.actugetno=a.actugetno and not exists(select 'x' from  "
				+ " ljpbalancerela where balancerelano=b.actugetno)"
				//+ "  and d.grpgroupno is null and a.riskcode<> 'NIK04'"
				+ "  and d.grpgroupno is null "
//				+" and not exists (select k.riskcode from lmriskapp k where k.riskcode=a.riskcode and "    ���ų�����
//				+" (k.riskperiod='L') ) "//ֻ�ų����գ��������� or k.risktype='L'
				+" and b.tqflag ='Y'"//��Ȩ��ȫ�շ������������Ŀ
				+ " and c.agentcode=d.agentcode and d.grpcontno= a.grpcontno  and b.makedate >='"
				+ mStartDate
				+ "' and a.feeoperationtype <> 'ZH' and b.makedate <='"
				+ mEndDate
				+ "' and a.feeoperationtype<>'WT'  and a.feeoperationtype <> 'ZH' and a.GetMoney<>0 and b.managecom like '"
				+ mGlobalInput.ManageCom + "%' ";
		// ���Ӽ��ˣ����Ǵ����������
		tSQL13 = " select  a.makedate,decode(d.branchtype,'1','KA','2','DB',"
				+ "'3','GA'),d.entryno,c.grpcontno,c.grpname,b.riskcode,"
				+ "(select riskname from lmrisk  where riskcode = b.riskcode),"
				+ " sum(b.sumactupaymoney),a.actugetno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//�����������
				+ "from ljaget a,lcgrpcont c,lmrisk m ,"
				+ "ljapayperson b,laagent d where b.payno=a.actugetno and "
				+ "c.grpcontno=b.grpcontno and exists(select 'x' from lpedormain where "
				+ "edorno=a.otherno) and c.grpgroupno is null and c.agentcode=d.agentcode"
				+ " and m.riskcode=b.riskcode "
			//	+" and b.riskcode<> 'NIK04' " //ASR20118405_NIK04��Ʒ����ӿ�
				+" and a.tqflag ='Y'"//��Ȩ��ȫ�շ������������Ŀ
				+ " and b.sumactupayMoney<>0  and a.makedate >='"
				+ mStartDate
				+ "'"
				+ " and a.makedate <='"
				+ mEndDate
				+ "' and a.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%'  and not exists(select 'x' from lbgrpcont where grpcontno=c.grpcontno)";
		// ���ڲ���ȷ��
		/** huangkai--2008-09-09--����ASR20082078���ݣ������н�ʵ���ܱ���ӱ��getnoticeno�ó��ݽ��ѱ��tempfeeno����
		���ڲ����տ��ѯֱ�����ݽɷѱ��tempfeeno��ʵ���ܱ�ʵ���ӱ��getnoticeno������ѯ */
		// Tracy modify 2008-03-18 ���ڲ�����ܴ���һ���ն�ηѵ��������Ҫ�ж�
//		ExeSQL tExeSQL = new ExeSQL();
//		String ljtempfee = "select paydate,paymoney+difpaymoney,otherno from ljtempfee c where c.tempfeetype='2' "
//			+ "and not exists(select 'x' from ljtempfeeclass where tempfeeno=c.tempfeeno "
//			+ "and paymode in ('B','J'))  and c.modifydate >= '"
//			+ mStartDate
//			+ "' and c.modifydate <= '"
//			+ mEndDate
//			+ "' and c.managecom like '"
//			+ mGlobalInput.ManageCom
//			+ "%' and c.financedate is not null  ";
//		if (mGrpContNo != null && !mGrpContNo.equals("")) {
//			ljtempfee += " and otherno = '" + mGrpContNo + "'";
//		}
//		SSRS aSSRS = tExeSQL.execSQL(ljtempfee);
//		if (aSSRS.getMaxRow() > 0) {
//			String Payno = "''";
//			for (int i = 1; i <= aSSRS.getMaxRow(); i++) {
//				String money = aSSRS.GetText(i, 2);
//				if(aSSRS.GetText(i, 2)==null||"".equals(aSSRS.GetText(i, 2))){
//					money = "0";
//				}
//				double m = Double.parseDouble(money);
//				SSRS bSSRS = tExeSQL
//						.execSQL("select sum(sumactupaymoney) money, payno from ljapay where incometype='2'"
//								+ " and incomeno='"
//								+ aSSRS.GetText(i, 3)
//								+ "' and paydate <= '"
//								+ aSSRS.GetText(i, 1)
//								+ "'  and modifydate >='"
//								+ mStartDate
//								+ "'  and modifydate<= '"
//								+ mEndDate
//								+ "' group by payno order by payno desc ");
//				if (bSSRS.getMaxRow() > 0) {
//					for (int t = 1; t <= bSSRS.getMaxRow(); t++) {
//						m = Double.parseDouble(PubFun.format(m - Double.parseDouble(bSSRS.GetText(t, 1))));
//						if (m < 0) {
//							break;
//						} else {
//							Payno += ",'" + bSSRS.GetText(t, 2) + "'";
//						}
//					}
//				}
//			}
			tSQL14 = " select c.modifydate,decode(b.branchtype,'1','KA','2','DB','3',"
					+ "'GA'),b.entryno,c.otherno,d.grpname ,a.riskcode,m.riskname,"
					+ "sum(-a.sumduepaymoney),c.tempfeeno "
					+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
					+" ,d.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
	                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//�����������
					+" From ljtempfee c,lcgrpcont d,"
					+ "ljapaygrp a,laagent b,lmrisk m where "
					+ " a.paycount=2  and d.agentcode=b.agentcode  and  c.tempfeetype='2'  "
					+ "and c.otherno=d.grpcontno  and c.tempfeeno=a.getnoticeno and m.riskcode=a.riskcode and not exists(select 'x' from ljtempfeeclass where "
					+ "tempfeeno=c.tempfeeno and paymode in ('B','J'))  and c.modifydate >= '"
					+ mStartDate
					+ "'    and a.grpcontno=d.grpcontno and c.modifydate <= '"
					+ mEndDate
					+ "'  and a.sumduepaymoney>0 and c.managecom like '"
					+ mGlobalInput.ManageCom
					+ "%' and c.financedate is not null  and not exists(select 'x' "
					+ "from lbgrpcont where grpcontno=d.grpcontno)";
			tSQL22 = " select c.modifydate,decode(b.branchtype,'1','KA','2','DB','3',"
					+ "'GA'),b.entryno,c.otherno,d.grpname ,a.riskcode,m.riskname,"
					+ "sum(-a.sumduepaymoney),c.tempfeeno "
					+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
					+" ,d.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
	                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//�����������
					+" From ljtempfee c,lbgrpcont d,"
					+ "ljapaygrp a,laagent b,lmrisk m where "
					+ " a.paycount=2  and d.agentcode=b.agentcode  and  c.tempfeetype='2'  "
					+ "and c.otherno=d.grpcontno and c.tempfeeno=a.getnoticeno and m.riskcode=a.riskcode and not exists(select 'x' from ljtempfeeclass where "
					+ "tempfeeno=c.tempfeeno and paymode in ('B','J'))  and c.modifydate >= '"
					+ mStartDate
					+ "' and a.grpcontno=d.grpcontno and c.modifydate <= '"
					+ mEndDate
					+ "'  and a.sumduepaymoney>0 and c.managecom like '"
					+ mGlobalInput.ManageCom
					+ "%' and c.financedate is not null  ";
			if (mGrpName != null && !mGrpName.equals("")) {
				tSQL14 += " and d.grpname like '%" + mGrpName + "%'";
				tSQL22 += " and d.grpname like '%" + mGrpName + "%'";
			}
			if (mGrpContNo != null && !mGrpContNo.equals("")) {
				tSQL14 += " and d.grpcontno = '" + mGrpContNo + "'";
				tSQL22 += " and d.grpcontno = '" + mGrpContNo + "'";
			}
			if (mPrtNo != null && !mPrtNo.equals("")) {
				tSQL14 += " and d.prtno = '" + mPrtNo + "'";
				tSQL22 += " and d.prtno = '" + mPrtNo + "'";
			}
			tSQL14 += " group by c.modifydate,b.branchtype,b.entryno,c.otherno,d.grpname ,"
					+ "a.riskcode,m.riskname,c.tempfeeno ,d.salechnl,d.managecom";
			tSQL22 += " group by c.modifydate,b.branchtype,b.entryno,c.otherno,d.grpname ,"
					+ "a.riskcode,m.riskname,c.tempfeeno ,d.salechnl,d.managecom";
//		}

		// ��ȫ�˷�תԤ��   (����)
		tSQL15 = " select a.confmakedate ,decode(c.branchtype,'1','KA','2',"
				+ "'DB','3','GA'),c.entryno, d.grpcontno,d.grpname ,"
				+ "b.riskcode,m.riskname ,sum(-b.getmoney),a.actugetno  "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//�����������
				+ "From ljfiget a,ljagetendorse b,laagent c,lcgrpcont d,lmrisk m "
				+ "where a.paymode = 'A'  and a.actugetno=b.actugetno and a.othernotype='3' "
				+ "and b.grpcontno=d.grpcontno and b.GetMoney<>0 "
				+ " and m.riskcode=b.riskcode "
				+" and b.riskcode <> 'NIK04' "  
				+" and not exists (select k.riskcode from lmriskapp k where k.riskcode=b.riskcode and "
				+" (k.riskperiod='L' ) ) "//ֻ�ų�����,��������or k.risktype='L'
				+ " and b.agentcode=c.agentcode and a.confmakedate >= '"
				+ mStartDate
				+ "' and a.confmakedate <= '"
				+ mEndDate
				+ "' and a.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%' and not exists(select 'x' from lbgrpcont where grpcontno=d.grpcontno)";
		// ��ȫ�˷�תԤ��  (����)
		tSQL21 = " select a.confmakedate ,decode(c.branchtype,'1','KA','2',"
				+ "'DB','3','GA'),c.entryno, d.grpcontno,d.grpname ,"
				+ "b.riskcode,m.riskname ,sum(-b.sumduepaymoney),a.actugetno  "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//�����������
				+ "From ljfiget a,ljapayperson b,laagent c,lcgrpcont d,lmrisk m "
				+ "where a.paymode = 'A'  and a.actugetno=b.payno and a.othernotype='3' "
				+ "and b.grpcontno=d.grpcontno and b.sumduepaymoney<>0 "
				+ " and m.riskcode=b.riskcode "
				+"  and b.riskcode <> 'NIK04' " 
				+" and not exists (select k.riskcode from lmriskapp k where k.riskcode=b.riskcode and "
				+" (k.riskperiod='L' ) ) "//ֻ�ų����պͶ�������,or k.risktype='L'
				+ " and b.agentcode=c.agentcode and a.confmakedate >= '"
				+ mStartDate
				+ "' and a.confmakedate <= '"
				+ mEndDate
				+ "' and a.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%' and not exists(select 'x' from lbgrpcont where grpcontno=d.grpcontno)";
		// ����ǩ��
//		huagnkai--2008-09-09--PIR20081308--�����п��ܴ����ܱ���ӱ��getnoticenoһ��������ҵ�����ڲ�һ�µ������Ӧ����serialno����
		tSQL16 = "  select a.makedate,decode(d.branchtype,'1','KA','2','DB','3',"
				+ "'GA'),d.entryno,c.grpcontno,c.grpname,b.riskcode,m.riskname,"
				+ "sum(b.sumduepaymoney) "
				+" ,a.Getnoticeno"
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//�����������
				+" from ljspay a,lcgrpcont c,laagent d,lmrisk m,"
				+ "ljspaygrp b where a.otherno=c.grpcontno and b.getnoticeno=a.getnoticeno"
				+ " and b.serialno=a.serialno and c.agentcode=d.agentcode and a.OtherNoType = '1' "
				+ "and m.riskcode=b.riskcode and c.grpgroupno is null  "
				+ "and b.sumduepaymoney>=0  and a.makedate >='"
				+ mStartDate
				+ "' and a.makedate <='"
				+ mEndDate
				+ "' and b.riskcode<>'NIK04' "  
				+"  and c.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%' and not exists(select 'x' from lbgrpcont where grpcontno=c.grpcontno)";
		// ����ǩ����תʵ��
//		huagnkai--2008-09-09--PIR20081308--�����п��ܴ����ܱ���ӱ��getnoticenoһ��������ҵ�����ڲ�һ�µ������Ӧ�������ǵ�����payno����
		tSQL20 = "  select a.makedate,decode(d.branchtype,'1','KA','2','DB','3',"
				+ "'GA'),d.entryno,c.grpcontno,c.grpname,b.riskcode,m.riskname,"
				+ "sum(b.sumduepaymoney) "
				+" ,a.Getnoticeno"
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//�����������
				+" from ljapay a,lcgrpcont c,laagent d,lmrisk m,"
				+ "ljapaygrp b where a.incomeno=c.grpcontno and b.getnoticeno=a.getnoticeno"
				+ " and b.payno=a.payno and c.agentcode=d.agentcode and a.incometype = '2' "
				+ "and m.riskcode=b.riskcode and c.grpgroupno is null  "
				+ "and b.sumactupaymoney>=0  and a.makedate >='"
				+ mStartDate
				+ "' and a.makedate <='"
				+ mEndDate
				+ "' and b.riskcode<>'NIK04' "  
				+"  and c.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%'  and not exists(select 'x' from lbgrpcont where grpcontno=c.grpcontno)";
		// ���ڽ���ཻ�����˷ѳ�ر��ѷ�¼
		tSQL17 = " select distinct b.modifydate,decode(f.branchtype, '1', 'KA', '2', 'DB', '3', 'GA'),"
                +" f.entryno,e.grpcontno,e.grpname,d.riskcode,m.riskname,"
                +" nvl(d.money, 0) + nvl(sum(-n.sumactupaymoney), 0),a.nunmber,"
                +" (select codename from ldcode l where l.codetype = 'agenttype' and l.code = e.salechnl),"
                +" e.managecom,(select s.name from ldcom s where s.comcode = e.managecom) "
			    +" from (select t.balancerelano nunmber, t.payno payno, t.grpcontno  "
			    +" from (select balancerelano,sum(balancerelasum) money,payno,grpcontno "
			    +" from ljpbalancerela where BalanceRelaState = '1' group by balancerelano, payno, grpcontno) t, "
                +" (select balancerelano, nvl(sum(money), 0) money from ljpbalancerelasub "
//                +" group by balancerelano) n where t.balancerelano = n.balancerelano and n.money > t.money) a, "
                +" group by balancerelano) n where t.balancerelano = n.balancerelano ) a, "
                +" (select '1' PayCount,polno,dutycode,payplancode,sumactupaymoney,payno,grpcontno,riskcode "
                +" from ljapayperson union all select '1' PayCount,polno,dutycode,payplancode, "
                +" getmoney as sumactupaymoney,actugetno as payno,grpcontno,riskcode from ljagetendorse) n,"
			    +" ljapay b,  ljpbalancerelasub d, lcgrpcont e, laagent f, lmriskapp m"
			    +" where n.payno = a.payno and a.nunmber = b.payno and n.grpcontno = e.grpcontno "
			    +" and n.riskcode = d.riskcode "
			    +" and d.balancerelano = a.nunmber"
			    +" and (select nvl(sum(money),0) from ljpbalancerelasub "
				+" where balancerelano = d.balancerelano)>(select nvl(sum(balancerelasum),0) "
				+" from ljpbalancerela "
				+" where balancerelano = d.balancerelano) "
			    +" and m.riskcode = d.riskcode and e.agentcode = f.agentcode"
			    +" and a.grpcontno = e.grpcontno "
			    +" and b.incometype='8' "
			  //  +" and d.riskcode <> 'NIK04' "  //ASR20118405_NIK04��Ʒ����ӿ�
			    + " and m.riskperiod<>'L'"
			    + " and b.modifydate >='" + mStartDate
			    + "' and b.modifydate <='" + mEndDate
			    +"' and b.managecom like '"+ mGlobalInput.ManageCom+ "%' "
			    +" and not exists (select 'x' from lbgrpcont where grpcontno = e.grpcontno) "
			    +" group by b.modifydate,f.branchtype,f.entryno,e.grpcontno,e.grpname,"
				+ "d.riskcode,m.riskname,d.money,a.nunmber,e.salechnl,e.managecom"
				+" union all"//��Ӷ��ڶึ���
				+" select distinct b.modifydate,decode(f.branchtype, '1', 'KA', '2', 'DB', '3', 'GA'),"
                +" f.entryno,e.grpcontno,e.grpname,d.riskcode,m.riskname,"
                +" nvl(d.money, 0) + nvl(sum(-n.sumactupaymoney), 0),a.nunmber,"
                +" (select codename from ldcode l where l.codetype = 'agenttype' and l.code = e.salechnl),"
                +" e.managecom,(select s.name from ldcom s where s.comcode = e.managecom) "
				+" from (select t.balancerelano nunmber, t.payno payno, t.grpcontno  "
				+" from (select balancerelano,sum(balancerelasum) money,payno,grpcontno "
				+" from ljpbalancerela where BalanceRelaState = '1' group by balancerelano, payno, grpcontno) t, "
	            +" (select balancerelano, nvl(sum(money), 0) money from ljpbalancerelasub "
//	            +" group by balancerelano) n where t.balancerelano = n.balancerelano and n.money < t.money) a, "
	            +" group by balancerelano) n where t.balancerelano = n.balancerelano ) a, "
	            +" (select '1' PayCount,polno,dutycode,payplancode,sumactupaymoney,payno,grpcontno,riskcode "
	            +" from ljapayperson union all select '1' PayCount,polno,dutycode,payplancode, "
	            +" getmoney as sumactupaymoney,actugetno as payno,grpcontno,riskcode from ljagetendorse) n,"
				+" ljaget b,  ljpbalancerelasub d, lcgrpcont e, laagent f, lmriskapp m"
				+" where n.payno = a.payno and a.nunmber = b.actugetno and n.grpcontno = e.grpcontno "
				+" and n.riskcode = d.riskcode "
				+" and d.balancerelano = a.nunmber"
				+" and (select nvl(sum(money),0) from ljpbalancerelasub "
				+" where balancerelano = d.balancerelano)<(select nvl(sum(balancerelasum),0) "
				+" from ljpbalancerela "
				+" where balancerelano = d.balancerelano) "
				+" and m.riskcode = d.riskcode and e.agentcode = f.agentcode"
				+" and a.grpcontno = e.grpcontno "
			//	+" and d.riskcode <> 'NIK04' "  //ASR20118405_NIK04��Ʒ����ӿ�
				+" and m.riskperiod<>'L' and b.financedate is not null "
				+ " and b.modifydate >='" + mStartDate
			    + "' and b.modifydate <='" + mEndDate
				+"' and b.managecom like '"+ mGlobalInput.ManageCom+ "%' "
				+" and not exists (select 'x' from lbgrpcont where grpcontno = e.grpcontno) "
				+" group by b.modifydate,f.branchtype,f.entryno,e.grpcontno,e.grpname,"
				+ "d.riskcode,m.riskname,d.money,a.nunmber,e.salechnl,e.managecom"
				;
		//���ڽ����ٽ������˷ѳ�ر��ѷ�¼
		tSQL24 = " select distinct b.modifydate,decode(f.branchtype, '1', 'KA', '2', 'DB', '3', 'GA'),"
            +" f.entryno,e.grpcontno,e.grpname,d.riskcode,m.riskname,"
            +" nvl(d.money, 0) + nvl(sum(-n.sumactupaymoney), 0),a.nunmber,"
            +" (select codename from ldcode l where l.codetype = 'agenttype' and l.code = e.salechnl),"
            +" e.managecom,(select s.name from ldcom s where s.comcode = e.managecom) "
		    +" from (select t.balancerelano nunmber, t.payno payno, t.grpcontno  "
		    +" from (select balancerelano,sum(balancerelasum) money,payno,grpcontno "
		    +" from ljpbalancerela where BalanceRelaState = '1' group by balancerelano, payno, grpcontno) t, "
            +" (select balancerelano, nvl(sum(money), 0) money from ljpbalancerelasub "
//            +" group by balancerelano) n where t.balancerelano = n.balancerelano and n.money < t.money) a, "
            +" group by balancerelano) n where t.balancerelano = n.balancerelano ) a, "
            +" (select '1' PayCount,polno,dutycode,payplancode,sumactupaymoney,payno,grpcontno,riskcode "
            +" from ljapayperson union all select '1' PayCount,polno,dutycode,payplancode, "
            +" getmoney as sumactupaymoney,actugetno as payno,grpcontno,riskcode from ljagetendorse) n,"
		    +" ljapay b,  ljpbalancerelasub d, lcgrpcont e, laagent f, lmriskapp m"
		    +" where n.payno = a.payno and a.nunmber = b.payno and n.grpcontno = e.grpcontno "
		    +" and n.riskcode = d.riskcode "
		    +" and d.balancerelano = a.nunmber"
		    +" and (select nvl(sum(money),0) from ljpbalancerelasub "
			+" where balancerelano = d.balancerelano)<(select nvl(sum(balancerelasum),0) "
			+" from ljpbalancerela "
			+" where balancerelano = d.balancerelano) "
		    +" and m.riskcode = d.riskcode and e.agentcode = f.agentcode"
		    +" and a.grpcontno = e.grpcontno "
		    +" and b.incometype='8' "
		   // +" and d.riskcode <> 'NIK04'"   //ASR20118405_NIK04��Ʒ����ӿ�
		    +" and m.riskperiod<>'L'"
		    + " and b.modifydate >='" + mStartDate
		    + "' and b.modifydate <='" + mEndDate
		    +"' and b.managecom like '"+ mGlobalInput.ManageCom+ "%' "
		    +" and not exists (select 'x' from lbgrpcont where grpcontno = e.grpcontno) "
		    +" group by b.modifydate,f.branchtype,f.entryno,e.grpcontno,e.grpname,"
			+ "d.riskcode,m.riskname,d.money,a.nunmber,e.salechnl,e.managecom"
			+" union all"//��Ӷ��ڶึ���
			+" select distinct b.modifydate,decode(f.branchtype, '1', 'KA', '2', 'DB', '3', 'GA'),"
            +" f.entryno,e.grpcontno,e.grpname,d.riskcode,m.riskname,"
            +" nvl(d.money, 0) + nvl(sum(-n.sumactupaymoney), 0),a.nunmber,"
            +" (select codename from ldcode l where l.codetype = 'agenttype' and l.code = e.salechnl),"
            +" e.managecom,(select s.name from ldcom s where s.comcode = e.managecom) "
			+" from (select t.balancerelano nunmber, t.payno payno, t.grpcontno  "
			+" from (select balancerelano,sum(balancerelasum) money,payno,grpcontno "
			+" from ljpbalancerela where BalanceRelaState = '1' group by balancerelano, payno, grpcontno) t, "
            +" (select balancerelano, nvl(sum(money), 0) money from ljpbalancerelasub "
//            +" group by balancerelano) n where t.balancerelano = n.balancerelano and n.money > t.money) a, "
            +" group by balancerelano) n where t.balancerelano = n.balancerelano ) a, "
            +" (select '1' PayCount,polno,dutycode,payplancode,sumactupaymoney,payno,grpcontno,riskcode "
            +" from ljapayperson union all select '1' PayCount,polno,dutycode,payplancode, "
            +" getmoney as sumactupaymoney,actugetno as payno,grpcontno,riskcode from ljagetendorse) n,"
			+" ljaget b,  ljpbalancerelasub d, lcgrpcont e, laagent f, lmriskapp m"
			+" where n.payno = a.payno and a.nunmber = b.actugetno and n.grpcontno = e.grpcontno "
			+" and n.riskcode = d.riskcode "
			+" and d.balancerelano = a.nunmber"
			+" and (select nvl(sum(money),0) from ljpbalancerelasub "
			+" where balancerelano = d.balancerelano)>(select nvl(sum(balancerelasum),0) "
			+" from ljpbalancerela "
			+" where balancerelano = d.balancerelano) "
			+" and m.riskcode = d.riskcode and e.agentcode = f.agentcode"
			+" and a.grpcontno = e.grpcontno "
			//+" and d.riskcode <> 'NIK04' "  //ASR20118405_NIK04��Ʒ����ӿ�
			+" and m.riskperiod<>'L' and b.financedate is not null "
			+ " and b.modifydate >='" + mStartDate
		    + "' and b.modifydate <='" + mEndDate
			+"' and b.managecom like '"+ mGlobalInput.ManageCom+ "%' "
			+" and not exists (select 'x' from lbgrpcont where grpcontno = e.grpcontno) "
			+" group by b.modifydate,f.branchtype,f.entryno,e.grpcontno,e.grpname,"
			+ "d.riskcode,m.riskname,d.money,a.nunmber,e.salechnl,e.managecom"
			;
		tSQL18 = " select a.modifydate ,decode(c.branchtype,'1','KA','2',"
				+ "'DB','3','GA'),c.entryno, d.grpcontno,d.grpname,"
				+ "b.riskcode,(select riskname from lmrisk  where riskcode = b.riskcode),"
				+ "sum(-b.sumduepaymoney),a.actugetno  "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//�����������
				+" From LJAGet a,laagent c,lcgrpcont d, "
				+ "ljapayperson b where not exists (select 'x' from  "
				+ " ljpbalancerela where payno=a.actugetno) and d.grpcontno=b.grpcontno "
				+ " and a.actugetno=b.payno and c.agentcode=b.agentcode and a.modifydate >= '"
				+ mStartDate
				+ "' and a.paymode <> 'A' "
				//+" and b.riskcode<>'NIK04' "  //ASR20118405_NIK04��Ʒ����ӿ�
				+" and a.modifydate <= '"
				+ mEndDate
				+ "' and a.financedate is not null  and a.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%'  and not exists(select 'x' from lbgrpcont where grpcontno=d.grpcontno)";
		// 0929���Ӷ����������ָ���ȷ�ϵĲ����¼����
		// �������Ȩǩ��δ���� �裺�������룬����Ӧ�ձ��ѣ��������ͨ�������裺�������룻�������д��
		// ASR20082804�����շѱ�����Ƹ���(�����˷�),��:Ӧ�գ��������� �Ͳ���ӿ�ͬ����20081202
		//PIR20113143 ��������ӿ��޸ģ����շѶ��ڽ���� �裺�������� ����Ӧ�ձ��� modify by Bright
		tSQL19 = "  select a.modifydate,decode(d.branchtype, '1', 'KA', '2', "
				+ "'DB', '3', 'GA'),d.entryno,c.grpcontno,c.grpname,b.riskcode,m.riskname,"
				+ "sum(-b.getmoney),0 "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//�����������
				+" From LJAGet a,ljagetendorse b, lbgrpcont c, "
				+ "laagent d, lmrisk m where  a.actugetno=b.actugetno "
				+ " and b.grpcontno = c.grpcontno and c.agentcode = d.agentcode "
				+ "and b.riskcode = m.riskcode and b.feeoperationtype='WT' "
				+ "and b.feefinatype in ('TF', 'TB', 'TQ', 'SX') "
			//	+" and b.riskcode<>'NIK04'"  //ASR20118405_NIK04��Ʒ����ӿ�
				+" and a.modifydate >='"+ mStartDate+ "' "
				+" and a.modifydate <= '"+ mEndDate+ "' "
				+" and a.financedate is not null  "//��Ƹ���(�����˷�)
				+" and a.managecom like '"+ mGlobalInput.ManageCom+ "%' "
				+" and not exists (select 'x' from lcgrpcont where grpcontno=b.grpcontno"
				+" and grpgroupno is not null ) "
				+" and (c.PayFlag='1' "//�������շѱ������
				+" or ( c.PayFlag='0' and c.balancemode='2')) ";//�������շѡ����ڽ��㱣�����
		//ASR20082804 ���շѱ���������ȫȷ�Ͻ裺��������,����Ӧ�ձ���,�Ͳ���ӿ�ͬ����20081202
       // PIR20113143 ��������ӿ��޸ģ����շѶ��ڽ���� �裺�������� ����Ӧ�ձ��� modify by Bright
		tSQL23 = "  select a.modifydate,decode(d.branchtype, '1', 'KA', '2', "
				+" 'DB', '3', 'GA'),d.entryno,c.grpcontno,c.grpname,b.riskcode,m.riskname,"
				+" sum(b.getmoney),0 "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
	            +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//�����������
				+" From LJAGet a,ljagetendorse b, lbgrpcont c, "
				+" laagent d, lmrisk m where  a.actugetno=b.actugetno "
				+" and b.grpcontno = c.grpcontno and c.agentcode = d.agentcode "
				+" and b.riskcode = m.riskcode and b.feeoperationtype='WT' "
				+" and b.feefinatype in ('TF', 'TB', 'TQ', 'SX') "
			//	+" and b.riskcode<>'NIK04'"  //ASR20118405_NIK04��Ʒ����ӿ�
				+" and a.modifydate >='"+ mStartDate+ "' "
				+" and a.modifydate <= '"+ mEndDate+ "' "
				+"  "//������ȫȷ�ϲ����κβ�������
				+" and a.managecom like '"+ mGlobalInput.ManageCom+ "%' "
				+" and not exists (select 'x' from lcgrpcont where grpcontno=b.grpcontno"
				+" and grpgroupno is not null ) "
				+" and (c.PayFlag='1' "//�������շѱ������
		        +" or ( c.PayFlag='0' and c.balancemode='2'))";//�������շѡ����ڽ��㱣�����
		//���ڽ���������������ȷ�ϣ��շ�ȷ�� ��:Ӧ�ձ���-�����ݼ�10403������Ӧ�ձ���10401
		tSQL25=" select r.makedate,decode(a.branchtype, '1', 'KA', '2','DB', '3', 'GA')"
			   +" ,a.entryno,t.grpcontno,t.grpname,s.riskcode,p.riskname,"
			   +" -nvl(sum(s.money),0),0 "
			   +" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = t.salechnl)"
			   +" ,t.managecom"
	           +" ,(select s.name from ldcom s where s.comcode=t.managecom)"
			   +" from ljapay  r, ljpbalancerelasub s, lcgrpcont t,lmriskapp  p, laagent  a  "
			   +" where r.payno=s.balancerelano  "
			   +" and exists (select r.balancerelano from ljpbalancerela r where r.balancerelano="
			   +" s.balancerelano and r.grpcontno=t.grpcontno)"
			   +" and t.agentcode = a.agentcode and s.riskcode = p.riskcode"
			   +" and t.grpgroupno is null "
			   +" and r.makedate >='"+ mStartDate+ "' "
			   +" and r.makedate <='"+ mEndDate+ "' "
			   +" and r.managecom like '"+ mGlobalInput.ManageCom+ "%' "
			   +" and not exists (select 'x' from lbgrpcont where grpcontno = t.grpcontno) "
			   +" group by  r.makedate,a.branchtype,a.entryno,t.grpcontno,t.grpname,s.riskcode,p.riskname"
			   +" ,t.salechnl,t.managecom"
			   ;
		//���ڽ���������������ȷ�ϣ�ʹ���罻���֣��շѣ������գ���NIK04���裺Ԥ�ձ��ѣ�20501������Ӧ�ձ���10401
		tSQL26=" select r.makedate,decode(a.branchtype, '1', 'KA', '2','DB', '3', 'GA')"
			   +" ,a.entryno,t.grpcontno,t.grpname,s.riskcode,p.riskname,"
			   +" -(select sum(f.difpaymoney) from ljtempfee f where f.associateno=r.getnoticeno and f.tempfeetype='8'),0 "
			   +" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = t.salechnl)"
			   +" ,t.managecom"
	           +" ,(select s.name from ldcom s where s.comcode=t.managecom)"
			   +" from ljapay  r, ljpbalancerelasub s, lcgrpcont t,lmriskapp  p, laagent  a  "
			   +" where r.payno=s.balancerelano  "
			   +" and exists (select r.balancerelano from ljpbalancerela r where r.balancerelano="
			   +" s.balancerelano and r.grpcontno=t.grpcontno) "
			//   + " and s.riskcode<>'NIK04'"  //ASR20118405_NIK04��Ʒ����ӿ�
			   +" and t.agentcode = a.agentcode and s.riskcode = p.riskcode"
			   +" and t.grpgroupno is null "
			   +" and exists (select f.associateno from ljtempfee f where f.associateno=r.getnoticeno"
			   +" and f.difpaymoney>0 and f.tempfeetype='8')"
			   +" and r.makedate >='"+ mStartDate+ "' "
			   +" and r.makedate <='"+ mEndDate+ "' "
			   +" and r.managecom like '"+ mGlobalInput.ManageCom+ "%' "
			   +" and not exists (select 'x' from lbgrpcont where grpcontno = t.grpcontno) "
			   +" group by  r.makedate,a.branchtype,a.entryno,t.grpcontno,t.grpname,s.riskcode,p.riskname"
			   +" ,r.getnoticeno,t.salechnl,t.managecom"
			   ;
		//��ɻ���տ�ȷ��(�տ�) �裺���д�� ����Ӧ�ձ���-�����ݼ�10403
		tSQL27 = "select r.makedate,decode(a.branchtype, '1', 'KA', '2','DB', '3', 'GA'),a.entryno, "
	           +" t.grpcontno,t.grpname,'','',c.paymoney,0,"
	           +" (select codename from ldcode l where l.codetype = 'agenttype' and l.code = t.salechnl), "
	           +" t.managecom,(select s.name from ldcom s where s.comcode=t.managecom) "
	           +" From ljtempfee c,ljapay r,laagent a,lcgrpcont t "
	           +" where c.tempfeetype in ('8') and r.getnoticeno=c.associateno and r.incomeno=c.otherno "
	           +" and not exists(select 'x' from ljtempfeeclass where "
	           +" tempfeeno=c.tempfeeno and paymode in ('B','J')) "
	           +" and r.makedate >='"+ mStartDate+ "' "
			   +" and r.makedate <='"+ mEndDate+ "' "
	           +" and r.managecom like '"+ mGlobalInput.ManageCom+ "%' "
	           +" and c.financedate is not null "
	           +" and c.paymoney<>0 "
	           +" and a.agentcode = c.agentcode "
	           +" and c.otherno = t.grpcontno "
	           +" union all "
	           +" select c.modifydate,decode(a.branchtype, '1', 'KA', '2','DB', '3', 'GA'),a.entryno, "
	           +" t.grpcontno,t.grpname,'','',-c.paymoney,0,"
	           +" (select codename from ldcode l where l.codetype = 'agenttype' and l.code = t.salechnl), "
	           +" t.managecom,(select s.name from ldcom s where s.comcode=t.managecom) "
	           +" From ljtempfee c,laagent a,lcgrpcont t "
	           +" where c.tempfeetype in ('8') "
	           +" and not exists(select 'x' from ljtempfeeclass where "
	           +" tempfeeno=c.tempfeeno and paymode in ('B','J')) "
	           +" and c.modifydate >='"+ mStartDate+ "' "
			   +" and c.modifydate <='"+ mEndDate+ "' "
	           +" and c.managecom like '"+ mGlobalInput.ManageCom+ "%' "
	           +" and c.financedate is not null "
	           +" and c.paymoney<>0 "
	           +" and a.agentcode = c.agentcode "
	           +" and c.otherno = t.grpcontno " 
	           +" union all "
	           +" select b.modifydate,decode(a.branchtype, '1', 'KA', '2','DB', '3', 'GA'),a.entryno, "
	           +" t.grpcontno,t.grpname,'','',-b.paymoney,0, "
	           +" (select codename from ldcode l where l.codetype = 'agenttype' and l.code = t.salechnl), "
	           +" t.managecom,(select s.name from ldcom s where s.comcode=t.managecom) "
	           +" From lbtempfee b,laagent a,lcgrpcont t"
	           +" where b.tempfeetype in ('8') "
	           +" and not exists(select 'x' from ljtempfeeclass where "
	           +" tempfeeno=b.tempfeeno and paymode in ('B','J')) "
	           +" and b.modifydate >='"+ mStartDate+ "' "
			   +" and b.modifydate <='"+ mEndDate+ "' "
	           +" and b.managecom like '"+ mGlobalInput.ManageCom+ "%' "
	           +" and b.confmakedate is not null " 
	           +" and b.paymoney<>0 "
	           +" and a.agentcode = b.agentcode "
	           +" and b.otherno = t.grpcontno ";
		//���ڽ��㳷��(���ڽ������շѳ���),�裺Ӧ�ձ���-�����ݼ�10403,����Ԥ�ձ��ѣ�20501��
		tSQL28 = "select a.makedate,decode(f.branchtype, '1', 'KA', '2','DB', '3', 'GA'),f.entryno, "
			   +" t.grpcontno,t.grpname,'','',-b.getmoney,0, "
	           +" (select codename from ldcode l where l.codetype = 'agenttype' and l.code = t.salechnl), "
	           +" t.managecom,(select s.name from ldcom s where s.comcode=t.managecom) "
	           +" from ljaget a,ljagettempfee b ,ljgrpdiftrace g,lcgrpcont t,laagent f "
			   +" where a.actugetno=b.actugetno and g.otherno=b.tempfeeno  "
			   +" and t.agentcode=f.agentcode and g.grpcontno=t.grpcontno"
			   +" and b.tempfeetype='8'"//������������������,�ų���ȫ�������(��ȫ����Ϊ,Ԥ�յ�Ԥ��,���Կɲ�����ʾ)
			   +" and a.makedate >='"+ mStartDate+ "' "
			   +" and a.makedate <='"+ mEndDate+ "' "
			   +" and a.managecom like '"+ mGlobalInput.ManageCom+ "%' "
			   +" and not exists (select 'x' from lbgrpcont where grpcontno = t.grpcontno) ";
			
		if (mGrpName != null && !mGrpName.equals("")) {
			tSQL10 += " and b.grpname like '%" + mGrpName + "%'";
			tSQL1 += " and b.grpname like '%" + mGrpName + "%'";
			tSQL2 += " and c.grpname like '%" + mGrpName + "%'";
			tSQL3 += " and d.grpname like '%" + mGrpName + "%'";
			tSQL4 += " and d.grpname like '%" + mGrpName + "%'";
			tSQL5 += " and d.grpname like '%" + mGrpName + "%'";
			tSQL6 += " and d.grpname like '%" + mGrpName + "%'";
			tSQL7 += " and b.grpname like '%" + mGrpName + "%'";
			tSQL8 += " and d.grpname like '%" + mGrpName + "%'";
			tSQL9 += " and c.grpname like '%" + mGrpName + "%'";
			tSQL11 += " and d.grpname like '%" + mGrpName + "%'";
			tSQL12 += " and d.grpname like '%" + mGrpName + "%'";
			tSQL13 += " and c.grpname like '%" + mGrpName + "%'";
			// tSQL14 += " and d.grpname like '%" + mGrpName + "%'";
			tSQL15 += " and d.grpname like '%" + mGrpName + "%'";
			tSQL16 += " and c.grpname like '%" + mGrpName + "%'";
			tSQL17 += " and e.grpname like '%" + mGrpName + "%'";
			tSQL24 += " and e.grpname like '%" + mGrpName + "%'";
			tSQL18 += " and d.grpname like '%" + mGrpName + "%'";
			tSQL19 += " and c.grpname like '%" + mGrpName + "%'";
			tSQL23 += " and c.grpname like '%" + mGrpName + "%'";
			tSQL20 += " and c.grpname like '%" + mGrpName + "%'";
			tSQL21 += " and d.grpname like '%" + mGrpName + "%'";
			tSQL25 += " and t.grpname like '%" + mGrpName + "%'";
			tSQL26 += " and t.grpname like '%" + mGrpName + "%'";
			tSQL27 += " and t.grpname like '%" + mGrpName + "%'";
			tSQL28 += " and t.grpname like '%" + mGrpName + "%'";
		}
		if (mGrpContNo != null && !mGrpContNo.equals("")) {
			tSQL10 += " and b.grpcontno = '" + mGrpContNo + "'";
			tSQL1 += " and b.grpcontno = '" + mGrpContNo + "'";
			tSQL2 += " and c.grpcontno = '" + mGrpContNo + "'";
			tSQL3 += " and d.grpcontno = '" + mGrpContNo + "'";
			tSQL4 += " and d.grpcontno = '" + mGrpContNo + "'";
			tSQL5 += " and d.grpcontno = '" + mGrpContNo + "'";
			tSQL6 += " and d.grpcontno = '" + mGrpContNo + "'";
			tSQL7 += " and b.grpcontno = '" + mGrpContNo + "'";
			tSQL8 += " and d.grpcontno = '" + mGrpContNo + "'";
			tSQL9 += " and c.grpcontno = '" + mGrpContNo + "'";
			tSQL11 += " and d.grpcontno = '" + mGrpContNo + "'";
			tSQL12 += " and d.grpcontno = '" + mGrpContNo + "'";
			tSQL13 += " and c.grpcontno = '" + mGrpContNo + "'";
			// tSQL14 += " and d.grpcontno = '" + mGrpContNo + "'";
			tSQL15 += " and d.grpcontno = '" + mGrpContNo + "'";
			tSQL16 += " and c.grpcontno = '" + mGrpContNo + "'";
			tSQL17 += " and e.grpcontno = '" + mGrpContNo + "'";
			tSQL24 += " and e.grpcontno = '" + mGrpContNo + "'";
			tSQL18 += " and d.grpcontno = '" + mGrpContNo + "'";
			tSQL19 += " and c.grpcontno = '" + mGrpContNo + "'";
			tSQL23 += " and c.grpcontno = '" + mGrpContNo + "'";
			tSQL20 += " and c.grpcontno = '" + mGrpContNo + "'";
			tSQL21 += " and d.grpcontno = '" + mGrpContNo + "'";
			tSQL25 += " and t.grpcontno = '" + mGrpContNo + "'";
			tSQL26 += " and t.grpcontno = '" + mGrpContNo + "'";
			tSQL27 += " and t.grpcontno = '" + mGrpContNo + "'";
			tSQL28 += " and t.grpcontno = '" + mGrpContNo + "'";
		}
		if (mPrtNo != null && !mPrtNo.equals("")) {
			tSQL10 += " and b.prtno = '" + mPrtNo + "'";
			tSQL1 += " and b.prtno = '" + mPrtNo + "'";
			tSQL2 += " and c.prtno = '" + mPrtNo + "'";
			tSQL3 += " and d.prtno = '" + mPrtNo + "'";
			tSQL4 += " and d.prtno = '" + mPrtNo + "'";
			tSQL5 += " and d.prtno = '" + mPrtNo + "'";
			tSQL6 += " and d.prtno = '" + mPrtNo + "'";
			tSQL7 += " and b.prtno = '" + mPrtNo + "'";
			tSQL8 += " and d.prtno = '" + mPrtNo + "'";
			tSQL9 += " and c.prtno = '" + mPrtNo + "'";
			tSQL11 += " and d.prtno = '" + mPrtNo + "'";
			tSQL12 += " and d.prtno = '" + mPrtNo + "'";
			tSQL13 += " and c.prtno = '" + mPrtNo + "'";
			// tSQL14 += " and d.prtno = '" + mPrtNo + "'";
			tSQL15 += " and d.prtno = '" + mPrtNo + "'";
			tSQL16 += " and c.prtno = '" + mPrtNo + "'";
			tSQL17 += " and e.prtno = '" + mPrtNo + "'";
			tSQL24 += " and e.prtno = '" + mPrtNo + "'";
			tSQL18 += " and d.prtno = '" + mPrtNo + "'";
			tSQL19 += " and c.prtno = '" + mPrtNo + "'";
			tSQL23 += " and c.prtno = '" + mPrtNo + "'";
			tSQL20 += " and c.prtno = '" + mPrtNo + "'";
			tSQL21 += " and d.prtno = '" + mPrtNo + "'";
			tSQL25 += " and t.prtno = '" + mPrtNo + "'";
			tSQL26 += " and t.prtno = '" + mPrtNo + "'";
			tSQL27 += " and t.prtno = '" + mPrtNo + "'";
			tSQL28 += " and t.prtno = '" + mPrtNo + "'";
		}
		tSQL1 += " group by c.modifydate,d.branchtype,d.entryno,b.grpcontno,b.grpname,"
				+ "a.riskcode,c.tempfeeno,b.salechnl,b.managecom";
		tSQL3 += "  group by a.modifydate,f.riskname, c.branchtype, c.entryno, d.grpcontno, "
				+ "d.grpname,b.riskcode,a.actugetno,d.salechnl ,d.managecom";
		tSQL9 += "  group by a.makedate, d.branchtype, d.entryno,"
				+ " c.grpcontno, c.grpname, b.riskcode,a.payno,c.salechnl,c.managecom";
		tSQL10 += " group by c.modifydate, d.branchtype,d.entryno,"
				+ "b.grpcontno, b.grpname,   a.riskcode,c.tempfeeno,b.salechnl,b.managecom";
		tSQL13 += " group by a.makedate,d.branchtype,d.entryno,c.grpcontno,c.grpname,"
				+ "b.riskcode,a.actugetno ,c.salechnl,c.managecom";
		tSQL15 += " group by a.confmakedate ,c.branchtype,c.entryno, d.grpcontno,"
				+ "d.grpname ,b.riskcode,m.riskname ,a.actugetno ,d.salechnl,d.managecom";
		tSQL16 += " group by  a.makedate, d.branchtype,d.entryno,c.grpcontno,c.grpname,"
				+ "b.riskcode, m.riskname ,a.Getnoticeno,c.salechnl,c.managecom";
		tSQL18 += " group by a.modifydate,c.branchtype,c.entryno,d.grpcontno,d.grpname,"
				+ "b.riskcode,a.actugetno ,d.salechnl,d.managecom";
		tSQL19 += " group by d.branchtype,a.modifydate,d.entryno,c.grpcontno,"
				+ "c.grpname,b.riskcode,m.riskname ,c.salechnl,c.managecom";
		tSQL23 += " group by d.branchtype,a.modifydate,d.entryno,c.grpcontno,"
				+ "c.grpname,b.riskcode,m.riskname ,c.salechnl,c.managecom";
		tSQL20 += "  group by  a.makedate, d.branchtype,d.entryno,c.grpcontno,c.grpname,"
				+ "b.riskcode, m.riskname ,a.Getnoticeno,c.salechnl,c.managecom";
		tSQL2 += " group by a.makedate ,d.branchtype, d.entryno,c.grpcontno,c.grpname,"
				+ "b.riskcode,a.payno,c.salechnl,c.managecom";
		tSQL12 += " group by a.modifydate ,c.branchtype,c.entryno, d.grpcontno,d.grpname,"
				+ "e.riskcode,a.actugetno,d.salechnl,d.managecom";
		tSQL5 += " group by b.makedate,c.branchtype,c.entryno,d.grpcontno,d.grpname,"
				+ "a.riskcode,a.actugetno,d.salechnl,d.managecom";
		//huangkai--2008-09-26--��ȫ����ȷ�ϣ��ǵ���ȷ��ҲӦ�ð�������������
		tSQL7 += " group by c.modifydate,d.branchtype,d.entryno,b.grpcontno,b.grpname,"
				+ "a.riskcode,c.tempfeeno,b.salechnl,b.managecom";
		tSQL8 += " group by b.makedate,c.branchtype,c.entryno,d.grpcontno,d.grpname,"
				+ "a.riskcode,a.actugetno,d.salechnl,d.managecom";
		tSQL11 += " group by b.makedate,c.branchtype,c.entryno,d.grpcontno,d.grpname,"
				+ "a.riskcode,a.actugetno,d.salechnl,d.managecom";
		tSQL21 += " group by a.confmakedate ,c.branchtype,c.entryno, d.grpcontno,"
				+ "d.grpname ,b.riskcode,m.riskname ,a.actugetno ,d.salechnl,d.managecom";
		tSQL28 += "group by a.makedate,f.branchtype,f.entryno,t.grpcontno,t.grpname,"
			    + "b.getmoney,t.salechnl,t.managecom,a.actugetno";
		
		//ASR20082836--����ӿڣ�����շѣ�����ȫ���ȫ���Ƶ�Ԥ�ձ��ѱ�����
		//SQL[0] = tSQL1;
		SQL[1] = tSQL2;
		//SQL[2] = tSQL3;
		//SQL[3] = tSQL4;
		SQL[4] = tSQL5;
		//SQL[5] = tSQL6;//ԭ���ڽ��㣬�����к�Ӧ�տ�Ŀ������Ӧ���ݼǿ�Ŀ
		//SQL[6] = tSQL7;
		SQL[7] = tSQL8 + tSQL11;
		SQL[8] = tSQL9;
		//SQL[9] = tSQL10;
		SQL[10] = tSQL12;
		SQL[11] = tSQL13;
		SQL[12] = tSQL14;
		//SQL[13] = tSQL15;
		SQL[14] = tSQL16;
		SQL[15] = tSQL17;
		//SQL[16] = tSQL18;
		SQL[17] = tSQL19;
		SQL[18] = tSQL20;
		//SQL[19] = tSQL21;
		SQL[20] = tSQL22;
		SQL[21] = tSQL23;
		SQL[22] = tSQL24;
		SQL[23] = tSQL25;
		//SQL[24] = tSQL26;
		SQL[25] = tSQL27;
		SQL[26] = tSQL28;
		double SumMoney = 0;
		int SumAccount = 0;
        
		for (int a = 0; a <= SQL.length - 1; a++) {
			if (SQL[a] != null && !SQL[a].equals("")) {
				tSSRS = tExeSQL.execSQL(SQL[a]);
				SumAccount = SumAccount + tSSRS.MaxRow;
				if (SumAccount > 20000) {
					CError tError = new CError();
					tError.moduleName = "GetCounterInBL";
					tError.functionName = "dealData";
					tError.errorMessage = "��ǰ��ӡ����������20000���������Ӳ�ѯ�����Լ��ٴ�ӡ��¼����Ȼ�����´�ӡ!";
					this.mErrors.addOneError(tError);
					return false;
				}
				if (tSSRS.MaxRow > 0) {
					System.out.println(SQL[a]);
					for (int i = 1; i <= tSSRS.MaxRow; i++) {
						strArr = new String[15];
						String tSum = tSSRS.GetText(i, 8);
						if (tSum == null || tSum.equals("")
								|| tSum.equals("null")) {
							tSum = "0.00";
						} else {
							SumMoney = SumMoney + Double.parseDouble(tSum);
						}

						String No = String.valueOf(SumAccount - tSSRS.MaxRow
								+ i);
						strArr[0] = No; // ���
						strArr[1] = tSSRS.GetText(i, 1); // ҵ������
						strArr[2] = tSSRS.GetText(i, 2); // ����
						strArr[3] = tSSRS.GetText(i, 3); // Ӫ�ܴ�
						strArr[4] = tSSRS.GetText(i, 4); // Ͷ����������)
						strArr[5] = tSSRS.GetText(i, 5); // Ͷ����λ
						strArr[6] = tSSRS.GetText(i, 6); // ���ֱ���
						strArr[7] = tSSRS.GetText(i, 7); // ��������
						strArr[8] = tSum; // Ӧ�ձ��շ�
						strArr[9] = tSSRS.GetText(i, 10); // ��������
						strArr[10] = tSSRS.GetText(i, 12); // ASR20082492:������������Ʒ������(�����������Ϊ11��)
                        //����������ˡ�������Ч�ա�������ֹ����Ϣ  add by Bright
						tSQLA="select b.name,a.cValidate,(select distinct (payenddate-1) from lcgrppol where grpcontno=a.grpcontno and rownum=1 ) "
							+ "from lcgrpcont a,laagent b where a.agentcode=b.agentcode and a.grpcontno ='"+tSSRS.GetText(i, 4)+"'";
						aSSRS = tExeSQLA.execSQL(tSQLA);
						if (aSSRS.MaxRow > 0) {
							strArr[11]=aSSRS.GetText(1,1);
							strArr[12]=aSSRS.GetText(1,2);
							strArr[13]=aSSRS.GetText(1,3);
						}
						
						tlistTable.add(strArr);
					}
				}
			}
		}

		// ------����lbgrpcont��õ�����
		// ���ڽ������ȷ��
		tSQL6 = "  select c.modifydate,decode(b.branchtype,'1','KA','2','DB','3','GA'),b.entryno,c.otherno,d.grpname ,"
				+ "a.riskcode,(select riskname from lmrisk where riskcode = a.riskcode),"
				+ "-a.money,c.tempfeeno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//�����������
				+" From ljtempfee c,lbgrpcont d,ljpbalancerelasub a,laagent b "
				+ "where c.tempfeetype ='8' and c.otherno=d.grpcontno and b.agentcode=d.agentcode "
				+ " and c.tempfeeno=a.balancerelano and c.modifydate >= '"
				+ mStartDate
				+ "' and not exists(select 'x' from ljtempfeeclass where "
				+ "tempfeeno=c.tempfeeno and paymode in ('B','J'))  and c.modifydate <= '"
				+ mEndDate
			//	+ " and a.riskcode<>'NIK04' " //ASR20118405_NIK04��Ʒ����ӿ�
				+"'and c.managecom like '"
				+ mGlobalInput.ManageCom + "%' and c.financedate is not null ";
		//huangkai--2008-09-23--PIR20081392--���ڽ����˷Ѳ���ӿ����д�b���ѯ��Ӧ�ձ���û�в�ѯ��ʹ���߲�һ��
		//���ڽ����˷� ����ȷ��
		tSQL12 = " select a.modifydate ,decode(c.branchtype,'1','KA','2',"
				+ "'DB','3','GA'),c.entryno, d.grpcontno,d.grpname,"
				+ "e.riskcode,(select riskname from lmrisk  where riskcode = e.riskcode),"
				+ "sum(-e.money),a.actugetno  "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//�����������
				+ " From LJAGet a,laagent c,lbgrpcont d, "
				+ "ljpbalancerelasub e where e.balancerelano=a.actugetno "
				+" and exists "
				+" (select r.grpcontno from ljpbalancerela r where r.balancerelano = e.balancerelano and r.grpcontno = d.grpcontno)"
				//+ "and  e.riskcode<>'NIK04' "   //ASR20118405_NIK04��Ʒ����ӿ�
				+ "  and c.agentcode=d.agentcode and a.modifydate >= '"
				+ mStartDate
				+ "' and a.modifydate <= '"
				+ mEndDate
				+ "' and a.financedate is not null  and a.managecom like '"
				+ mGlobalInput.ManageCom+ "%'";
		// ��ȫ����ȷ�� ---����ȷ�ϣ�ת��ljagetendorse����
		tSQL1 = " select c.modifydate,decode(d.branchtype,'1','KA','2','DB','3','GA'), d.entryno,b.grpcontno,b.grpname,"
				+ "a.riskcode,(select riskname from lmrisk where riskcode = a.riskcode),"
				+ "sum(-a.getmoney),c.tempfeeno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = b.salechnl)"
				+" ,b.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=b.managecom)"//�����������
				+" From ljtempfee c ,lbgrpcont b,ljagetendorse a,laagent d where "
				+ "c.tempfeetype ='4' and b.agentcode=d.agentcode and a.GetMoney<>0 "
				+ " and c.tempfeeno=a.getnoticeno and b.grpcontno=a.grpcontno "
				+ " and not exists(select 'x' from ljtempfeeclass where "
				+ "tempfeeno=c.tempfeeno and paymode in ('B','J'))  and c.modifydate >= '"
				+ mStartDate
				+ "' and c.modifydate <= '"
				+ mEndDate
				+ "' and a.riskcode<>'NIK04' and c.managecom like '"
				+ mGlobalInput.ManageCom + "%'  and c.financedate is not null";
		// ��ȫ����ȷ�� ---����ȷ�ϣ�ת��ljapayperson����  (����)
		tSQL10 = " select c.modifydate,decode(d.branchtype,'1','KA','2','DB','3','GA'), d.entryno,b.grpcontno,b.grpname,"
				+ "a.riskcode,(select riskname from lmrisk where riskcode = a.riskcode),"
				+ "sum(-a.sumduepaymoney),c.tempfeeno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = b.salechnl)"
				+" ,b.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=b.managecom)"//�����������
				+" From ljtempfee c ,lbgrpcont b,ljapayperson a,"
				+ "laagent d,ljapay e  where e.payno=a.payno and a.sumduepaymoney<>0 and "
				+ "c.tempfeetype ='4' and b.agentcode=d.agentcode "
				+ " and c.tempfeeno=e.getnoticeno and b.grpcontno=a.grpcontno "
				+ " and not exists(select 'x' from ljtempfeeclass where "
				+ "tempfeeno=c.tempfeeno and paymode in ('B','J'))  and c.modifydate >= '"
				+ mStartDate
				+ "' and c.modifydate <= '"
				+ mEndDate
				+ "' and a.riskcode<>'NIK04' and c.managecom like '"
				+ mGlobalInput.ManageCom + "%'  and c.financedate is not null";
		// ��ȫ����ȷ�� �����ǵ���ȷ�ϣ���ljsgetendorse�� �����ϣ�
		tSQL7 = " select c.modifydate,decode(d.branchtype,'1','KA','2','DB','3','GA'), d.entryno,b.grpcontno,b.grpname,a.riskcode,(select "
				+ "riskname from lmrisk where riskcode = a.riskcode),sum(-a.getmoney),c.tempfeeno"
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = b.salechnl)"
				+" ,b.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=b.managecom)"//�����������
				+ " From ljtempfee c ,lbgrpcont b,ljsgetendorse a,laagent d where c.tempfeetype ='4' "
				+ " and c.tempfeeno=a.getnoticeno and b.grpcontno=a.grpcontno "
				+ " and d.agentcode=b.agentcode and a.GetMoney<>0 and c.modifydate >= '"
				+ mStartDate
				+ "' and not exists(select 'x' from ljtempfeeclass where "
				+ "tempfeeno=c.tempfeeno and paymode in ('B','J'))  and c.modifydate <= '"
				+ mEndDate
				+ "' and a.riskcode<>'NIK04' and c.managecom like '"
				+ mGlobalInput.ManageCom + "%'  and c.financedate is not null";
		// ��Ȩǩ��
		tSQL2 = " select a.makedate ,decode(d.branchtype,'1','KA','2','DB','3','GA'), d.entryno,c.grpcontno,c.grpname,"
				+ "b.riskcode,(select riskname from lmrisk  where riskcode = b.riskcode), "
				+ "sum(b.sumactupaymoney),a.payno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//�����������
				+" from ljapay a,lbgrpcont c,laagent d,ljapaygrp b where a.incomeno=c.grpcontno and  "
				+ "incometype in ('B','J') and b.payno=a.payno and c.grpgroupno is null  "
				+ " and d.agentcode=c.agentcode"
			//	+" and b.riskcode<> 'NIK04'"  //ASR20118405_NIK04��Ʒ����ӿ�
				+" and a.makedate >='"
				+ mStartDate
				+ "' and a.makedate <='"
				+ mEndDate
				+ "' and a.managecom like '" + mGlobalInput.ManageCom + "%' ";

		// ��ȫ�˷� ����ȷ��(����)
		tSQL3 = " select a.modifydate ,decode(c.branchtype,'1','KA','2',"
				+ "'DB','3','GA'),c.entryno, d.grpcontno,d.grpname,"
				+ "b.riskcode,f.riskname ,sum(-b.getmoney),a.actugetno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//�����������
				+" From LJAGet a,"
				+ "laagent c,lbgrpcont d, ljagetendorse b,lmriskapp f where "
				+ "b.feefinatype not in ('TQ', 'SX') and "    //fengyan PIR20092040
				+ "b.feeoperationtype<>'WT'  and a.paymode<>'A' and ( f.riskperiod<>'L' )  "//ֻ�ų����գ���������Ϊand  f.risktype<>'L'
				+ " and f.riskcode=b.riskcode and not exists  (select 'x' from  "
				+ " ljpbalancerela where payno=a.actugetno) and d.grpcontno=b.grpcontno "
				+ " and a.actugetno=b.actugetno and c.agentcode=b.agentcode and a.modifydate >= '"
				+ mStartDate
				+ "' and a.modifydate <= '"
				+ mEndDate
				+ "' and b.GetMoney<>0 and a.financedate is not null  and a.managecom like '"
				+ mGlobalInput.ManageCom + "%'" 
				+" and b.riskcode<>'NIK04' ";  
		// ��ȫ�˷� ����ȷ��
		// tSQL20 = " select a.modifydate ,decode(c.branchtype,'1','KA','2',"
		// + "'DB','3','GA'),c.entryno, d.grpcontno,d.grpname,"
		// + "b.riskcode,(select riskname from lmrisk where riskcode =
		// b.riskcode),"
		// + "sum(-b.sumactupaymoney),a.actugetno From LJAGet a,laagent
		// c,lbgrpcont d, "
		// + "ljapayperson b where not exists (select 'x' from "
		// + " ljpbalancerela where payno=a.actugetno) and
		// d.grpcontno=b.grpcontno "
		// + " and a.actugetno=b.payno and c.agentcode=b.agentcode and
		// a.modifydate >= '"
		// + mStartDate
		// + "' and a.modifydate <= '"
		// + mEndDate
		// + "' and b.sumactupaymoney<>0 and a.financedate is not null and
		// a.managecom like '"
		// + mGlobalInput.ManageCom + "%' ";
		// ���ڽ�������󻻺�
		tSQL4 = " select c.modifydate,decode(b.branchtype,'1','KA','2','DB','3','GA'),"
				+ "b.entryno,c.otherno,d.grpname ,a.riskcode,(select riskname from lmrisk"
				+ " where riskcode = a.riskcode),-a.money,c.tempfeeno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//�����������
				+" From ljtempfee c,lbgrpcont d,"
				+ "ljpbalancerelasub a,laagent b,ljapay e where c.tempfeetype ='8' and"
				+ " c.otherno=d.grpcontno and b.agentcode=d.agentcode "
				+ "and c.tempfeeno=e.getnoticeno "
				//+ " and a.riskcode<> 'NIK04'" //ASR20118405_NIK04��Ʒ����ӿ�
				+ " and e.payno=a.balancerelano and c.modifydate >= '"
				+ mStartDate
				+ "' and not exists(select 'x' from ljtempfeeclass where "
				+ "tempfeeno=c.tempfeeno and paymode in ('B','J'))  and c.modifydate <= '"
				+ mEndDate
				+ "' and c.managecom like '"
				+ mGlobalInput.ManageCom + "%' and c.financedate is not null ";
		// ��ȫ����ǩ��+���ڽ���===��ljapayperson��ȡ��
		tSQL9 = " select a.makedate ,decode(d.branchtype,'1','KA','2','DB','3','GA'), d.entryno,c.grpcontno,c.grpname,"
				+ "b.riskcode,(select riskname from lmrisk  where riskcode = b.riskcode), "
				+ " sum(b.sumactupaymoney),a.payno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//�����������
				+" from ljapay a,lbgrpcont c,laagent d,ljapayperson b "
				+ "where b.grpcontno = c.grpcontno and exists(select 'x' from lpedormain"
				+ " where edorno=a.incomeno) and a.payno=b.payno and c.grpgroupno is null"
				//+ " and d.agentcode=c.agentcode and b.riskcode<> 'NIK04' and a.makedate >='"
				+ " and d.agentcode=c.agentcode and a.makedate >='"
				+ mStartDate
				+ "'"
				+ " and a.makedate <='"
				+ mEndDate
				+ "' and b.sumactupaymoney<>0 and a.managecom like '"
				+ mGlobalInput.ManageCom + "%' "
				+" and a.incometype in ('B','J')"//��Ȩ��ȫ�շ������������Ŀ
				;//fengyan PIR20092040
		// ��ȫ����ǩ��+���ڽ���===��ljagetendorse��ȡ��
		tSQL5 = "  select b.makedate,decode(c.branchtype,'1','KA','2','DB','3','GA'),c.entryno,d.grpcontno,d.grpname,"
				+ "a.riskcode,(select riskname from lmrisk  where riskcode = a.riskcode), "
				+ "sum(a.getmoney),a.actugetno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//�����������
				+" from ljagetendorse a,lbgrpcont d,ljapay b,laagent c "
				+ "where b.payno=a.actugetno and d.grpgroupno is null"
				+ " and c.agentcode=d.agentcode and d.grpcontno= a.grpcontno "
				+" and b.incometype in ('B','J')"//��Ȩ��ȫ�շ������������Ŀ
				//+ " and a.GetMoney<>0 and a.riskcode<> 'NIK04' and b.makedate >='"
				+ " and a.GetMoney<>0 and b.makedate >='"
				+ mStartDate
				+ "' and b.makedate <='"
				+ mEndDate
				+ "' and a.managecom like '" + mGlobalInput.ManageCom + "%' ";

		// ���ӱ�ȫ���������Ϊ�շѣ����Ǵ����������
		tSQL11 = "  select  a.makedate,decode(d.branchtype,'1','KA','2','DB',"
				+ "'3','GA'),d.entryno,c.grpcontno,c.grpname,b.riskcode,"
				+ "(select riskname from lmrisk  where riskcode = b.riskcode),"
				+ " sum(b.sumactupaymoney),a.actugetno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//�����������
				+ "from ljaget a,lbgrpcont c,lmrisk m ,"
				+ "ljapayperson b,laagent d where b.payno=a.actugetno and "
				+ "c.grpcontno=b.grpcontno and exists(select 'x' from lpedormain where "
				+ "edorno=a.otherno) and c.grpgroupno is null and c.agentcode=d.agentcode"
				+ " and m.riskcode=b.riskcode "
			//	+ " and b.riskcode<> 'NIK04' " //ASR20118405_NIK04��Ʒ����ӿ�
				+" and a.tqflag ='Y'"//��Ȩ��ȫ�շ������������Ŀ
				+ " and b.sumactupayMoney<>0  and a.makedate >='" + mStartDate
				+ "'" + " and a.makedate <='" + mEndDate
				+ "' and a.managecom like '" + mGlobalInput.ManageCom + "%' ";
		// // ���ڲ���ȷ��
		// tSQL14 = " select
		// c.modifydate,decode(b.branchtype,'1','KA','2','DB','3',"
		// + "'GA'),b.entryno,c.otherno,d.grpname ,a.riskcode,m.riskname,"
		// + "sum(-a.sumduepaymoney),c.tempfeeno From ljtempfee c,lbgrpcont d,"
		// + "ljapaygrp a,laagent b,lmrisk m,ljapay n where "
		// + " a.paycount=2 and d.agentcode=b.agentcode "
		// + "and c.tempfeetype='2' and c.otherno=d.grpcontno "
		// + " and n.payno=a.payno and n.incomeno=d.grpcontno and
		// m.riskcode=a.riskcode "
		// + " and not exists(select 'x' from ljtempfeeclass where "
		// + "tempfeeno=c.tempfeeno and paymode in ('B','J')) and c.modifydate
		// >= '"
		// + mStartDate
		// + "' and c.modifydate <= '"
		// + mEndDate
		// + "' and c.managecom like '"
		// + mGlobalInput.ManageCom
		// + "%' and c.financedate is not null ";

		// ��ȫ�˷�תԤ�գ����ϣ�
		tSQL15 = " select a.confmakedate ,decode(c.branchtype,'1','KA','2',"
				+ "'DB','3','GA'),c.entryno, d.grpcontno,d.grpname ,"
				+ "b.riskcode,m.riskname ,sum(b.getmoney),a.actugetno  "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//�����������
				+ "From ljfiget a,ljagetendorse b,laagent c,lbgrpcont d,lmrisk m "
				+ "where a.paymode = 'A'  and a.actugetno=b.actugetno and a.othernotype='3' "
				+ "and b.grpcontno=d.grpcontno and b.GetMoney<>0 "
				+ " and m.riskcode=b.riskcode "
				+ " and b.riskcode<> 'NIK04' "  
				+" and not exists (select k.riskcode from lmriskapp k where k.riskcode=b.riskcode and "
				+" (k.riskperiod='L' ) ) "//ֻ�ų����պͶ�������or k.risktype='L'
				+ " and b.agentcode=c.agentcode and a.confmakedate >= '"
				+ mStartDate + "' and a.confmakedate <= '" + mEndDate
				+ "' and a.managecom like '" + mGlobalInput.ManageCom + "%' ";
		// ��ȫ�˷�תԤ�� �����ϣ�
		tSQL21 = " select a.confmakedate ,decode(c.branchtype,'1','KA','2',"
				+ "'DB','3','GA'),c.entryno, d.grpcontno,d.grpname ,"
				+ "b.riskcode,m.riskname ,sum(-b.sumduepaymoney),a.actugetno  "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//�����������
				+ "From ljfiget a,ljapayperson b,laagent c,lbgrpcont d,lmrisk m "
				+ "where a.paymode = 'A'  and a.actugetno=b.payno and a.othernotype='3' "
				+ "and b.grpcontno=d.grpcontno and b.sumduepaymoney<>0 "
				+ " and m.riskcode=b.riskcode "
				+ " and b.riskcode <> 'NIK04' " 
				+" and not exists (select k.riskcode  from lmriskapp k where k.riskcode=b.riskcode and "
				+" (k.riskperiod='L' ) ) "//ֻ�ų����պͶ�������or k.risktype='L'
				+ " and b.agentcode=c.agentcode and a.confmakedate >= '"
				+ mStartDate + "' and a.confmakedate <= '" + mEndDate
				+ "' and a.managecom like '" + mGlobalInput.ManageCom + "%' ";

		// ����ǩ��
//		huagnkai--2008-09-09--PIR20081308--�����п��ܴ����ܱ���ӱ��getnoticenoһ��������ҵ�����ڲ�һ�µ������Ӧ����serialno����
		tSQL16 = "  select a.makedate,decode(d.branchtype,'1','KA','2','DB','3',"
				+ "'GA'),d.entryno,c.grpcontno,c.grpname,b.riskcode,m.riskname,"
				+ "sum(b.sumduepaymoney) "
				+" ,a.Getnoticeno"
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//�����������
				+" from ljspay a,lbgrpcont c,laagent d,lmrisk m,"
				+ "ljspaygrp b where a.otherno=c.grpcontno and b.getnoticeno=a.getnoticeno"
				+ " and b.serialno=a.serialno and c.agentcode=d.agentcode and a.OtherNoType = '1' "
				+ "and m.riskcode=b.riskcode and c.grpgroupno is null  "
				+ "and a.sumduepaymoney>=0  and a.makedate >='"
				+ mStartDate
				+ "' and a.makedate <='"
				+ mEndDate
				+ "' and b.riskcode<>'NIK04' "  
				+" and c.managecom like '"
				+ mGlobalInput.ManageCom + "%' ";
		// ����ǩ����תʵ��
//		huagnkai--2008-09-09--PIR20081308--�����п��ܴ����ܱ���ӱ��getnoticenoһ��������ҵ�����ڲ�һ�µ������Ӧ�������ǵ�����payno����
		tSQL20 = "  select a.makedate,decode(d.branchtype,'1','KA','2','DB','3',"
				+ "'GA'),d.entryno,c.grpcontno,c.grpname,b.riskcode,m.riskname,"
				+ "sum(b.sumduepaymoney) "
				+" ,a.Getnoticeno"
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//�����������
				+" from ljapay a,lbgrpcont c,laagent d,lmrisk m,"
				+ "ljapaygrp b where a.incomeno=c.grpcontno and b.getnoticeno=a.getnoticeno"
				+ " and b.payno=a.payno and c.agentcode=d.agentcode and a.incometype = '2' "
				+ "and m.riskcode=b.riskcode and c.grpgroupno is null  "
				+ "and a.sumactupaymoney>=0  and a.makedate >='"
				+ mStartDate
				+ "' and a.makedate <='"
				+ mEndDate
				+ "' and b.riskcode<>'NIK04' "  
				+" and c.managecom like '"
				+ mGlobalInput.ManageCom + "%' ";
		//���ڽ���ཻ�����˷ѳ�ر��ѷ�¼
		tSQL17 = " select distinct b.modifydate,decode(f.branchtype, '1', 'KA', '2', 'DB', '3', 'GA'),"
            +" f.entryno,e.grpcontno,e.grpname,d.riskcode,m.riskname,"
            +" nvl(d.money, 0) + nvl(sum(-n.sumactupaymoney), 0),a.nunmber,"
            +" (select codename from ldcode l where l.codetype = 'agenttype' and l.code = e.salechnl),"
            +" e.managecom,(select s.name from ldcom s where s.comcode = e.managecom) "
		    +" from (select t.balancerelano nunmber, t.payno payno, t.grpcontno  "
		    +" from (select balancerelano,sum(balancerelasum) money,payno,grpcontno "
		    +" from ljpbalancerela where BalanceRelaState = '1' group by balancerelano, payno, grpcontno) t, "
            +" (select balancerelano, nvl(sum(money), 0) money from ljpbalancerelasub "
//            +" group by balancerelano) n where t.balancerelano = n.balancerelano and n.money > t.money) a, "
            +" group by balancerelano) n where t.balancerelano = n.balancerelano ) a, "
            +" (select '1' PayCount,polno,dutycode,payplancode,sumactupaymoney,payno,grpcontno,riskcode "
            +" from ljapayperson union all select '1' PayCount,polno,dutycode,payplancode, "
            +" getmoney as sumactupaymoney,actugetno as payno,grpcontno,riskcode from ljagetendorse) n,"
		    +" ljapay b,  ljpbalancerelasub d, lbgrpcont e, laagent f, lmriskapp m"
		    +" where n.payno = a.payno and a.nunmber = b.payno and n.grpcontno = e.grpcontno "
		    +" and n.riskcode = d.riskcode "
		    +" and d.balancerelano = a.nunmber"
		    +" and (select nvl(sum(money),0) from ljpbalancerelasub "
			+" where balancerelano = d.balancerelano)>(select nvl(sum(balancerelasum),0) "
			+" from ljpbalancerela "
			+" where balancerelano = d.balancerelano) "
		    +" and m.riskcode = d.riskcode and e.agentcode = f.agentcode"
		    +" and a.grpcontno = e.grpcontno "
		    +" and b.incometype='8' "
		//    +" and d.riskcode <> 'NIK04' "  //ASR20118405_NIK04��Ʒ����ӿ�
		    +" and m.riskperiod<>'L' "
		    + " and b.modifydate >='" + mStartDate
		    + "' and b.modifydate <='" + mEndDate
		    +"' and b.managecom like '"+ mGlobalInput.ManageCom+ "%' "
		    +" and not exists (select 'x' from lcgrpcont where grpcontno = e.grpcontno) "
		    +" group by b.modifydate,f.branchtype,f.entryno,e.grpcontno,e.grpname,"
			+ "d.riskcode,m.riskname,d.money,a.nunmber,e.salechnl,e.managecom"
			+" union all"//��Ӷ��ڶึ���
			+" select distinct b.modifydate,decode(f.branchtype, '1', 'KA', '2', 'DB', '3', 'GA'),"
            +" f.entryno,e.grpcontno,e.grpname,d.riskcode,m.riskname,"
            +" nvl(d.money, 0) + nvl(sum(-n.sumactupaymoney), 0),a.nunmber,"
            +" (select codename from ldcode l where l.codetype = 'agenttype' and l.code = e.salechnl),"
            +" e.managecom,(select s.name from ldcom s where s.comcode = e.managecom) "
			+" from (select t.balancerelano nunmber, t.payno payno, t.grpcontno  "
			+" from (select balancerelano,sum(balancerelasum) money,payno,grpcontno "
			+" from ljpbalancerela where BalanceRelaState = '1' group by balancerelano, payno, grpcontno) t, "
            +" (select balancerelano, nvl(sum(money), 0) money from ljpbalancerelasub "
//            +" group by balancerelano) n where t.balancerelano = n.balancerelano and n.money < t.money) a, "
            +" group by balancerelano) n where t.balancerelano = n.balancerelano ) a, "
            +" (select '1' PayCount,polno,dutycode,payplancode,sumactupaymoney,payno,grpcontno,riskcode "
            +" from ljapayperson union all select '1' PayCount,polno,dutycode,payplancode, "
            +" getmoney as sumactupaymoney,actugetno as payno,grpcontno,riskcode from ljagetendorse) n,"
			+" ljaget b,  ljpbalancerelasub d, lbgrpcont e, laagent f, lmriskapp m"
			+" where n.payno = a.payno and a.nunmber = b.actugetno and n.grpcontno = e.grpcontno "
			+" and n.riskcode = d.riskcode "
			+" and d.balancerelano = a.nunmber"
			+" and (select nvl(sum(money),0) from ljpbalancerelasub "
			+" where balancerelano = d.balancerelano)<(select nvl(sum(balancerelasum),0) "
			+" from ljpbalancerela "
			+" where balancerelano = d.balancerelano) "
			+" and m.riskcode = d.riskcode and e.agentcode = f.agentcode"
			+" and a.grpcontno = e.grpcontno "
		//	+" and d.riskcode <> 'NIK04'"  //ASR20118405_NIK04��Ʒ����ӿ�
			+" and m.riskperiod<>'L' and b.financedate is not null "
			+ " and b.modifydate >='" + mStartDate
		    + "' and b.modifydate <='" + mEndDate
			+"' and b.managecom like '"+ mGlobalInput.ManageCom+ "%' "
			+" and not exists (select 'x' from lcgrpcont where grpcontno = e.grpcontno) "
			+" group by b.modifydate,f.branchtype,f.entryno,e.grpcontno,e.grpname,"
			+ "d.riskcode,m.riskname,d.money,a.nunmber,e.salechnl,e.managecom"
			;
	//���ڽ����ٽ������˷ѳ�ر��ѷ�¼
	tSQL24 = " select distinct b.modifydate,decode(f.branchtype, '1', 'KA', '2', 'DB', '3', 'GA'),"
        +" f.entryno,e.grpcontno,e.grpname,d.riskcode,m.riskname,"
        +" nvl(d.money, 0) + nvl(sum(-n.sumactupaymoney), 0),a.nunmber,"
        +" (select codename from ldcode l where l.codetype = 'agenttype' and l.code = e.salechnl),"
        +" e.managecom,(select s.name from ldcom s where s.comcode = e.managecom) "
	    +" from (select t.balancerelano nunmber, t.payno payno, t.grpcontno  "
	    +" from (select balancerelano,sum(balancerelasum) money,payno,grpcontno "
	    +" from ljpbalancerela where BalanceRelaState = '1' group by balancerelano, payno, grpcontno) t, "
        +" (select balancerelano, nvl(sum(money), 0) money from ljpbalancerelasub "
//        +" group by balancerelano) n where t.balancerelano = n.balancerelano and n.money < t.money) a, "
        +" group by balancerelano) n where t.balancerelano = n.balancerelano ) a, "
        +" (select '1' PayCount,polno,dutycode,payplancode,sumactupaymoney,payno,grpcontno,riskcode "
        +" from ljapayperson union all select '1' PayCount,polno,dutycode,payplancode, "
        +" getmoney as sumactupaymoney,actugetno as payno,grpcontno,riskcode from ljagetendorse) n,"
	    +" ljapay b,  ljpbalancerelasub d, lbgrpcont e, laagent f, lmriskapp m"
	    +" where n.payno = a.payno and a.nunmber = b.payno and n.grpcontno = e.grpcontno "
	    +" and n.riskcode = d.riskcode "
	    +" and d.balancerelano = a.nunmber"
	    +" and (select nvl(sum(money),0) from ljpbalancerelasub "
		+" where balancerelano = d.balancerelano)<(select nvl(sum(balancerelasum),0) "
		+" from ljpbalancerela "
		+" where balancerelano = d.balancerelano) "
	    +" and m.riskcode = d.riskcode and e.agentcode = f.agentcode"
	    +" and a.grpcontno = e.grpcontno "
	    +" and b.incometype='8' "
	  //  +" and d.riskcode <> 'NIK04' "  //ASR20118405_NIK04��Ʒ����ӿ�
	    +" and m.riskperiod<>'L' "
	    + " and b.modifydate >='" + mStartDate
	    + "' and b.modifydate <='" + mEndDate
	    +"' and b.managecom like '"+ mGlobalInput.ManageCom+ "%' "
	    +" and not exists (select 'x' from lcgrpcont where grpcontno = e.grpcontno) "
	    +" group by b.modifydate,f.branchtype,f.entryno,e.grpcontno,e.grpname,"
		+ "d.riskcode,m.riskname,d.money,a.nunmber,e.salechnl,e.managecom"
		+" union all"//��Ӷ��ڶึ���
		+" select distinct b.modifydate,decode(f.branchtype, '1', 'KA', '2', 'DB', '3', 'GA'),"
        +" f.entryno,e.grpcontno,e.grpname,d.riskcode,m.riskname,"
        +" nvl(d.money, 0) + nvl(sum(-n.sumactupaymoney), 0),a.nunmber,"
        +" (select codename from ldcode l where l.codetype = 'agenttype' and l.code = e.salechnl),"
        +" e.managecom,(select s.name from ldcom s where s.comcode = e.managecom) "
		+" from (select t.balancerelano nunmber, t.payno payno, t.grpcontno  "
		+" from (select balancerelano,sum(balancerelasum) money,payno,grpcontno "
		+" from ljpbalancerela where BalanceRelaState = '1' group by balancerelano, payno, grpcontno) t, "
        +" (select balancerelano, nvl(sum(money), 0) money from ljpbalancerelasub "
//        +" group by balancerelano) n where t.balancerelano = n.balancerelano and n.money > t.money) a, "
        +" group by balancerelano) n where t.balancerelano = n.balancerelano ) a, "
        +" (select '1' PayCount,polno,dutycode,payplancode,sumactupaymoney,payno,grpcontno,riskcode "
        +" from ljapayperson union all select '1' PayCount,polno,dutycode,payplancode, "
        +" getmoney as sumactupaymoney,actugetno as payno,grpcontno,riskcode from ljagetendorse) n,"
		+" ljaget b,  ljpbalancerelasub d, lbgrpcont e, laagent f, lmriskapp m"
		+" where n.payno = a.payno and a.nunmber = b.actugetno and n.grpcontno = e.grpcontno "
		+" and n.riskcode = d.riskcode "
		+" and d.balancerelano = a.nunmber"
		+" and (select nvl(sum(money),0) from ljpbalancerelasub "
		+" where balancerelano = d.balancerelano)>(select nvl(sum(balancerelasum),0) "
		+" from ljpbalancerela "
		+" where balancerelano = d.balancerelano) "
		+" and m.riskcode = d.riskcode and e.agentcode = f.agentcode"
		+" and a.grpcontno = e.grpcontno "
	//	+" and d.riskcode <> 'NIK04' "  //ASR20118405_NIK04��Ʒ����ӿ�
		+" and m.riskperiod<>'L' and b.financedate is not null "
		+ " and b.modifydate >='" + mStartDate
	    + "' and b.modifydate <='" + mEndDate
		+"' and b.managecom like '"+ mGlobalInput.ManageCom+ "%' "
		+" and not exists (select 'x' from lcgrpcont where grpcontno = e.grpcontno) "
		+" group by b.modifydate,f.branchtype,f.entryno,e.grpcontno,e.grpname,"
		+ "d.riskcode,m.riskname,d.money,a.nunmber,e.salechnl,e.managecom"
		;
		tSQL18 = " select a.modifydate ,decode(c.branchtype,'1','KA','2',"
				+ "'DB','3','GA'),c.entryno, d.grpcontno,d.grpname,"
				+ "b.riskcode,(select riskname from lmrisk  where riskcode = b.riskcode),"
				+ "sum(-b.sumduepaymoney),a.actugetno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:�����������սᡢӦ�ձ����սᡢԤ�ձ����սᡢ���֧���ս�����һ�л�����(�����������)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//�����������
				+" From LJAGet a,laagent c,lbgrpcont d, "
				+ "ljapayperson b where not exists (select 'x' from  "
				+ " ljpbalancerela where payno=a.actugetno) and d.grpcontno=b.grpcontno "
				+ " and a.actugetno=b.payno and c.agentcode=b.agentcode and a.modifydate >= '"
				+ mStartDate + "' and a.modifydate <= '" + mEndDate
				+ "' and a.financedate is not null and a.paymode <> 'A' "
			//	+ " and b.riskcode<>'NIK04' " //ASR20118405_NIK04��Ʒ����ӿ�
				+" and a.managecom like '"
				+ mGlobalInput.ManageCom + "%'";
		//���ڽ���������������ȷ�ϣ��շ�ȷ�� ��:Ӧ�ձ���-�����ݼ�10403������Ӧ�ձ���10401
		tSQL25=" select r.makedate,decode(a.branchtype, '1', 'KA', '2','DB', '3', 'GA')"
			   +" ,a.entryno,t.grpcontno,t.grpname,s.riskcode,p.riskname,"
			   +" -nvl(sum(s.money),0),0 "
			   +" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = t.salechnl)"
			   +" ,t.managecom"
	           +" ,(select s.name from ldcom s where s.comcode=t.managecom)"
			   +" from ljapay  r, ljpbalancerelasub s, lbgrpcont t,lmriskapp  p, laagent  a  "
			   +" where r.payno=s.balancerelano  "
			   +" and exists (select r.balancerelano from ljpbalancerela r where r.balancerelano="
			   +" s.balancerelano and r.grpcontno=t.grpcontno)"
			   +" and t.agentcode = a.agentcode and s.riskcode = p.riskcode"
			   +" and t.grpgroupno is null "
			   +" and r.makedate >='"+ mStartDate+ "' "
			   +" and r.makedate <='"+ mEndDate+ "' "
			   +" and r.managecom like '"+ mGlobalInput.ManageCom+ "%' "
			   +" group by  r.makedate,a.branchtype,a.entryno,t.grpcontno,t.grpname,s.riskcode,p.riskname"
			   +" ,t.salechnl,t.managecom"
			   ;
		//���ڽ���������������ȷ�ϣ�ʹ���罻���֣��շѣ������գ���NIK04���裺Ԥ�ձ��ѣ�20501������Ӧ�ձ���10401
		tSQL26=" select r.makedate,decode(a.branchtype, '1', 'KA', '2','DB', '3', 'GA')"
			   +" ,a.entryno,t.grpcontno,t.grpname,s.riskcode,p.riskname,"
			   +" -(select sum(f.difpaymoney) from ljtempfee f where f.associateno=r.getnoticeno and f.tempfeetype='8'),0 "
			   +" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = t.salechnl)"
			   +" ,t.managecom"
	           +" ,(select s.name from ldcom s where s.comcode=t.managecom)"
			   +" from ljapay  r, ljpbalancerelasub s, lbgrpcont t,lmriskapp  p, laagent  a  "
			   +" where r.payno=s.balancerelano  "
			   +" and exists (select r.balancerelano from ljpbalancerela r where r.balancerelano="
			   +" s.balancerelano and r.grpcontno=t.grpcontno)"
			  // +" and s.riskcode<>'NIK04'"  //ASR20118405_NIK04��Ʒ����ӿ�
			   +" and t.agentcode = a.agentcode and s.riskcode = p.riskcode"
			   +" and t.grpgroupno is null "
			   +" and exists (select f.associateno from ljtempfee f where f.associateno=r.getnoticeno"
			   +" and f.difpaymoney>0 and f.tempfeetype='8')"
			   +" and r.makedate >='"+ mStartDate+ "' "
			   +" and r.makedate <='"+ mEndDate+ "' "
			   +" and r.managecom like '"+ mGlobalInput.ManageCom+ "%' "
			   +" group by  r.makedate,a.branchtype,a.entryno,t.grpcontno,t.grpname,s.riskcode,p.riskname"
			   +" ,r.getnoticeno,t.salechnl,t.managecom"
			   ;
//		��ɻ���տ�ȷ��(�տ�) �裺���д�� ����Ӧ�ձ���-�����ݼ�10403
		tSQL27 = "select r.makedate,decode(a.branchtype, '1', 'KA', '2','DB', '3', 'GA'),a.entryno, "
	           +" t.grpcontno,t.grpname,'','',c.paymoney,0,"
	           +" (select codename from ldcode l where l.codetype = 'agenttype' and l.code = t.salechnl), "
	           +" t.managecom,(select s.name from ldcom s where s.comcode=t.managecom) "
	           +" From ljtempfee c,ljapay r,laagent a,lbgrpcont t "
	           +" where c.tempfeetype in ('8') and r.getnoticeno=c.associateno and r.incomeno=c.otherno "
	           +" and not exists(select 'x' from ljtempfeeclass where "
	           +" tempfeeno=c.tempfeeno and paymode in ('B','J')) "
	           +" and r.makedate >='"+ mStartDate+ "' "
			   +" and r.makedate <='"+ mEndDate+ "' "
	           +" and r.managecom like '"+ mGlobalInput.ManageCom+ "%' "
	           +" and c.financedate is not null "
	           +" and c.paymoney<>0 "
	           +" and a.agentcode = c.agentcode "
	           +" and c.otherno = t.grpcontno "
	           +" union all "
	           +" select c.modifydate,decode(a.branchtype, '1', 'KA', '2','DB', '3', 'GA'),a.entryno, "
	           +" t.grpcontno,t.grpname,'','',-c.paymoney,0,"
	           +" (select codename from ldcode l where l.codetype = 'agenttype' and l.code = t.salechnl), "
	           +" t.managecom,(select s.name from ldcom s where s.comcode=t.managecom) "
	           +" From ljtempfee c,laagent a,lbgrpcont t "
	           +" where c.tempfeetype in ('8') "
	           +" and not exists(select 'x' from ljtempfeeclass where "
	           +" tempfeeno=c.tempfeeno and paymode in ('B','J')) "
	           +" and c.modifydate >='"+ mStartDate+ "' "
			   +" and c.modifydate <='"+ mEndDate+ "' "
	           +" and c.managecom like '"+ mGlobalInput.ManageCom+ "%' "
	           +" and c.financedate is not null "
	           +" and c.paymoney<>0 "
	           +" and a.agentcode = c.agentcode "
	           +" and c.otherno = t.grpcontno " 
	           +" union all "
	           +" select b.modifydate,decode(a.branchtype, '1', 'KA', '2','DB', '3', 'GA'),a.entryno, "
	           +" t.grpcontno,t.grpname,'','',-b.paymoney,0, "
	           +" (select codename from ldcode l where l.codetype = 'agenttype' and l.code = t.salechnl), "
	           +" t.managecom,(select s.name from ldcom s where s.comcode=t.managecom) "
	           +" From lbtempfee b,laagent a,lbgrpcont t"
	           +" where b.tempfeetype in ('8') "
	           +" and not exists(select 'x' from ljtempfeeclass where "
	           +" tempfeeno=b.tempfeeno and paymode in ('B','J')) "
	           +" and b.modifydate >='"+ mStartDate+ "' "
			   +" and b.modifydate <='"+ mEndDate+ "' "
	           +" and b.managecom like '"+ mGlobalInput.ManageCom+ "%' "
	           +" and b.confmakedate is not null " 
	           +" and b.paymoney<>0 "
	           +" and a.agentcode = b.agentcode "
	           +" and b.otherno = t.grpcontno ";
//		���ڽ��㳷��(���ڽ������շѳ���),�裺Ӧ�ձ���-�����ݼ�10403,����Ԥ�ձ��ѣ�20501��
		tSQL28 = "select a.makedate,decode(f.branchtype, '1', 'KA', '2','DB', '3', 'GA'),f.entryno, "
			   +" t.grpcontno,t.grpname,'','',-b.getmoney,0, "
	           +" (select codename from ldcode l where l.codetype = 'agenttype' and l.code = t.salechnl), "
	           +" t.managecom,(select s.name from ldcom s where s.comcode=t.managecom) "
	           +" from ljaget a,ljagettempfee b ,ljgrpdiftrace g,lbgrpcont t,laagent f  "
			   +" where a.actugetno=b.actugetno and g.otherno=b.tempfeeno  "
			   +" and t.agentcode=f.agentcode and g.grpcontno=t.grpcontno"
			   +" and b.tempfeetype='8'"//������������������,�ų���ȫ�������(��ȫ����Ϊ,Ԥ�յ�Ԥ��,���Կɲ�����ʾ)
			   +" and a.makedate >='"+ mStartDate+ "' "
			   +" and a.makedate <='"+ mEndDate+ "' "
			   +" and a.managecom like '"+ mGlobalInput.ManageCom+ "%' "
			   +" and not exists (select 'x' from lcgrpcont where grpcontno = t.grpcontno) ";
		if (mGrpName != null && !mGrpName.equals("")) {
			tSQL10 += " and b.grpname like '%" + mGrpName + "%'";
			tSQL1 += " and b.grpname like '%" + mGrpName + "%'";
			tSQL2 += " and c.grpname like '%" + mGrpName + "%'";
			tSQL3 += " and d.grpname like '%" + mGrpName + "%'";
			tSQL4 += " and d.grpname like '%" + mGrpName + "%'";
			tSQL5 += " and d.grpname like '%" + mGrpName + "%'";
			tSQL6 += " and d.grpname like '%" + mGrpName + "%'";
			tSQL7 += " and b.grpname like '%" + mGrpName + "%'";
			tSQL8 += " and d.grpname like '%" + mGrpName + "%'";
			tSQL9 += " and c.grpname like '%" + mGrpName + "%'";
			tSQL11 += " and c.grpname like '%" + mGrpName + "%'";
			tSQL12 += " and d.grpname like '%" + mGrpName + "%'";
			// tSQL14 += " and d.grpname = '" + mGrpName + "'";
			tSQL15 += " and d.grpname = '" + mGrpName + "'";
			tSQL16 += " and c.grpname like '%" + mGrpName + "%'";
			tSQL17 += " and e.grpname like '%" + mGrpName + "%'";
			tSQL24 += " and e.grpname like '%" + mGrpName + "%'";
			tSQL18 += " and d.grpname like '%" + mGrpName + "%'";
			tSQL20 += " and c.grpname like '%" + mGrpName + "%'";
			tSQL21 += " and d.grpname = '" + mGrpName + "'";
			tSQL25 += " and t.grpname like '%" + mGrpName + "%'";
			tSQL26 += " and t.grpname like '%" + mGrpName + "%'";
			tSQL27 += " and t.grpname like '%" + mGrpName + "%'";
			tSQL28 += " and t.grpname like '%" + mGrpName + "%'";
		}
		if (mGrpContNo != null && !mGrpContNo.equals("")) {
			tSQL10 += " and b.grpcontno = '" + mGrpContNo + "'";
			tSQL1 += " and b.grpcontno = '" + mGrpContNo + "'";
			tSQL2 += " and c.grpcontno = '" + mGrpContNo + "'";
			tSQL3 += " and d.grpcontno = '" + mGrpContNo + "'";
			tSQL4 += " and d.grpcontno = '" + mGrpContNo + "'";
			tSQL5 += " and d.grpcontno = '" + mGrpContNo + "'";
			tSQL6 += " and d.grpcontno = '" + mGrpContNo + "'";
			tSQL7 += " and b.grpcontno = '" + mGrpContNo + "'";
			tSQL8 += " and d.grpcontno = '" + mGrpContNo + "'";
			tSQL9 += " and c.grpcontno = '" + mGrpContNo + "'";
			tSQL11 += " and c.grpcontno = '" + mGrpContNo + "'";
			tSQL12 += " and d.grpcontno = '" + mGrpContNo + "'";
			// tSQL14 += " and d.grpcontno = '" + mGrpContNo + "'";
			tSQL15 += " and d.grpcontno = '" + mGrpContNo + "'";
			tSQL16 += " and c.grpcontno = '" + mGrpContNo + "'";
			tSQL17 += " and e.grpcontno = '" + mGrpContNo + "'";
			tSQL24 += " and e.grpcontno = '" + mGrpContNo + "'";
			tSQL18 += " and d.grpcontno = '" + mGrpContNo + "'";
			tSQL20 += " and c.grpcontno = '" + mGrpContNo + "'";
			tSQL21 += " and d.grpcontno = '" + mGrpContNo + "'";
			tSQL25 += " and t.grpcontno = '" + mGrpContNo + "'";
			tSQL26 += " and t.grpcontno = '" + mGrpContNo + "'";
			tSQL27 += " and t.grpcontno = '" + mGrpContNo + "'";
			tSQL28 += " and t.grpcontno = '" + mGrpContNo + "'";
		}
		if (mPrtNo != null && !mPrtNo.equals("")) {
			tSQL10 += " and b.prtno = '" + mPrtNo + "'";
			tSQL1 += " and b.prtno = '" + mPrtNo + "'";
			tSQL2 += " and c.prtno = '" + mPrtNo + "'";
			tSQL3 += " and d.prtno = '" + mPrtNo + "'";
			tSQL4 += " and d.prtno = '" + mPrtNo + "'";
			tSQL5 += " and d.prtno = '" + mPrtNo + "'";
			tSQL6 += " and d.prtno = '" + mPrtNo + "'";
			tSQL7 += " and b.prtno = '" + mPrtNo + "'";
			tSQL8 += " and d.prtno = '" + mPrtNo + "'";
			tSQL9 += " and c.prtno = '" + mPrtNo + "'";
			tSQL11 += " and c.prtno = '" + mPrtNo + "'";
			tSQL12 += " and d.prtno = '" + mPrtNo + "'";
			// tSQL14 += " and d.prtno = '" + mPrtNo + "'";
			tSQL15 += " and d.prtno = '" + mPrtNo + "'";
			tSQL16 += " and c.prtno = '" + mPrtNo + "'";
			tSQL17 += " and e.prtno = '" + mPrtNo + "'";
			tSQL24 += " and e.prtno = '" + mPrtNo + "'";
			tSQL18 += " and d.prtno = '" + mPrtNo + "'";
			tSQL20 += " and c.prtno = '" + mPrtNo + "'";
			tSQL21 += " and d.prtno = '" + mPrtNo + "'";
			tSQL25 += " and t.prtno = '" + mPrtNo + "'";
			tSQL26 += " and t.prtno = '" + mPrtNo + "'";
			tSQL27 += " and t.prtno = '" + mPrtNo + "'";
			tSQL28 += " and t.prtno = '" + mPrtNo + "'";
		}
		tSQL1 += " group by c.modifydate,d.branchtype,d.entryno,b.grpcontno,b.grpname,"
				+ "a.riskcode,c.tempfeeno,b.salechnl,b.managecom";
		tSQL3 += "  group by a.modifydate, c.branchtype, c.entryno, d.grpcontno, "
				+ "d.grpname,b.riskcode,f.riskname,a.actugetno ,d.salechnl,d.managecom";
		tSQL9 += "  group by a.makedate, d.branchtype, d.entryno,"
				+ " c.grpcontno, c.grpname, b.riskcode,a.payno,c.salechnl,c.managecom";
		tSQL10 += " group by c.modifydate, d.branchtype,d.entryno,"
				+ "b.grpcontno, b.grpname,   a.riskcode,c.tempfeeno,b.salechnl,b.managecom";
		tSQL11 += " group by a.makedate,d.branchtype,d.entryno,c.grpcontno,c.grpname,"
				+ "b.riskcode,a.actugetno ,c.salechnl,c.managecom";
		tSQL12 += " group by a.modifydate ,c.branchtype,c.entryno, d.grpcontno,d.grpname,"
			+ "e.riskcode,a.actugetno,d.salechnl,d.managecom";
		// tSQL14 += " group by
		// c.modifydate,b.branchtype,b.entryno,c.otherno,d.grpname ,"
		// + "a.riskcode,m.riskname,c.tempfeeno ";
		tSQL15 += " group by a.confmakedate ,c.branchtype,c.entryno, d.grpcontno,"
				+ "d.grpname ,b.riskcode,m.riskname ,a.actugetno ,d.salechnl,d.managecom";
		tSQL16 += " group by  a.makedate, d.branchtype,d.entryno,c.grpcontno,c.grpname,"
				+ "b.riskcode, m.riskname ,c.salechnl,a.Getnoticeno,c.managecom";
		tSQL18 += " group by a.modifydate,c.branchtype,c.entryno,d.grpcontno,d.grpname,"
				+ "b.riskcode,a.actugetno ,d.salechnl,d.managecom";
		tSQL20 += "  group by  a.makedate, d.branchtype,d.entryno,c.grpcontno,c.grpname,"
				+ "b.riskcode, m.riskname ,c.salechnl,a.Getnoticeno,c.managecom";
		tSQL2 += " group by a.makedate ,d.branchtype, d.entryno,c.grpcontno,c.grpname,"
				+ "b.riskcode,a.payno,c.salechnl,c.managecom";
		tSQL5 += " group by  b.makedate,c.branchtype,c.entryno,d.grpcontno,d.grpname,"
				+ "a.riskcode,a.actugetno,d.salechnl,d.managecom";
		//huangkai--2008-09-26--b���ѯ�����������������
		tSQL7 +=" group by c.modifydate,d.branchtype,d.entryno,b.grpcontno,b.grpname,"
				+ "a.riskcode,c.tempfeeno,b.salechnl,b.managecom";
		tSQL21 += " group by a.confmakedate ,c.branchtype,c.entryno, d.grpcontno,"
				+ "d.grpname ,b.riskcode,m.riskname ,a.actugetno ,d.salechnl,d.managecom";
		tSQL28 += "group by a.makedate,f.branchtype,f.entryno,t.grpcontno,t.grpname,"
		    	+ "b.getmoney,t.salechnl,t.managecom,a.actugetno";
		SQL = new String[30];
		//SQL[0] = tSQL1;
		SQL[1] = tSQL2;
		//SQL[2] = tSQL3;
		//SQL[3] = tSQL4;
		SQL[4] = tSQL5;
		//SQL[5] = tSQL6;
		//SQL[6] = tSQL7;
		// SQL[7] = tSQL8;
		SQL[8] = tSQL9;
		//SQL[9] = tSQL10;
		SQL[10] = tSQL11;
		// SQL[11] = tSQL14;
		//SQL[12] = tSQL15;
		SQL[13] = tSQL16;
		SQL[14] = tSQL17;
		//SQL[15] = tSQL18;
		SQL[16] = tSQL20;
		//SQL[17] = tSQL21;
		SQL[18] = tSQL12;
		SQL[19] = tSQL24;
		SQL[20] = tSQL25;
		//SQL[21] = tSQL26;
		SQL[22] = tSQL27;
		SQL[23] = tSQL28;
		for (int b = 0; b <= SQL.length - 1; b++) {
			if (SQL[b] != null && !SQL[b].equals("")) {
				mSSRS = tExeSQL.execSQL(SQL[b]);
				SumAccount = SumAccount + mSSRS.MaxRow;
				if (SumAccount > 20000) {
					CError tError = new CError();
					tError.moduleName = "GetCounterInBL";
					tError.functionName = "dealData";
					tError.errorMessage = "��ǰ��ӡ����������20000���������Ӳ�ѯ�����Լ��ٴ�ӡ��¼����Ȼ�����´�ӡ!";
					this.mErrors.addOneError(tError);
					return false;
				}
				if (mSSRS.MaxRow > 0) {
					System.out.println(SQL[b]);
					for (int i = 1; i <= mSSRS.MaxRow; i++) {
						strArr = new String[15];
						String tSum = mSSRS.GetText(i, 8);

						if (tSum == null || tSum.equals("")
								|| tSum.equals("null")) {
							tSum = "0.00";
						} else {
							SumMoney = SumMoney + Double.parseDouble(tSum);
						}

						String No = String.valueOf(SumAccount - mSSRS.MaxRow
								+ i);
						strArr[0] = No; // ���
						strArr[1] = mSSRS.GetText(i, 1); // ҵ������
						strArr[2] = mSSRS.GetText(i, 2); // ����
						strArr[3] = mSSRS.GetText(i, 3); // Ӫ�ܴ�
						strArr[4] = mSSRS.GetText(i, 4); // Ͷ����������)
						strArr[5] = mSSRS.GetText(i, 5); // Ͷ����λ
						strArr[6] = mSSRS.GetText(i, 6); // ���ֱ���
						strArr[7] = mSSRS.GetText(i, 7); // ��������
						strArr[8] = tSum; // Ӧ�ձ��շ�
						strArr[9] = mSSRS.GetText(i, 10); // ��������
						strArr[10] = mSSRS.GetText(i, 12); // ASR20082492:������������Ʒ������(�����������Ϊ11��)
                      //����������ˡ�������Ч�ա�������ֹ����Ϣ  add by Bright
						tSQLA="select b.name,a.cValidate,(select distinct (payenddate-1) from lbgrppol where grpcontno=a.grpcontno and rownum=1 ) "
							+ "from lBgrpcont a,laagent b where a.agentcode=b.agentcode and a.grpcontno ='"+tSSRS.GetText(i, 4)+"'";
						aSSRS = tExeSQLA.execSQL(tSQLA);
						if (aSSRS.MaxRow > 0) {
							strArr[11]=aSSRS.GetText(1,1);
							strArr[12]=aSSRS.GetText(1,2);
							strArr[13]=aSSRS.GetText(1,3);
						}
						tlistTable.add(strArr);
					}
				}
			}
		}

		// �õ���������
		String tSQL = "select name From ldcom where comcode = '"
				+ mGlobalInput.ManageCom + "'";
		String mName = tExeSQL.getOneValue(tSQL);
		mTextTag.add("Name", mName);
		// �ܱ����ϼ�

		String tSumAccount = String.valueOf(SumAccount);
		tSumAccount = new DecimalFormat("0").format(Float.valueOf(tSumAccount));
		mTextTag.add("Account", new DecimalFormat("0").format(SumAccount));
		// �ܽ��ϼ�
		String tSumMoney = String.valueOf(SumMoney);
		tSumMoney = new DecimalFormat("0.00").format(Double.valueOf(tSumMoney));
		mTextTag.add("Money", new DecimalFormat("0.00").format(SumMoney));

		strArr = new String[1];
		strArr[0] = "PayReport";
		xmlexport.addListTable(tlistTable, strArr);
		xmlexport.addTextTag(mTextTag);

		mResult.addElement(xmlexport);
		//ASR20082836�������շ�----�洢�����սᲿ��
		if(ASFlag.equals("A"))//�����������ʱ������������ݴ洢�����ݿ��� 
		{
			if(DMFlag.equals("D"))//ֻ���ս����������е�ʱ����������ݵ������ݿ��У��½�ֻ����xls
			{
 				System.out.println(mCurrentDate+ " ��������� "+mGlobalInput.ManageCom +" : ��ȡ��Ӧ�ձ��Ѳ�ѯ���Ϊ :"+tlistTable.size() +" ��!");
				VData mInputData = new VData();
				MMap map= new MMap();
				LJEveryBalanceSet mLJEveryBalanceSet = new LJEveryBalanceSet();
				for (int i = 0; i <  tlistTable.size(); i++) {
					LJEveryBalanceSchema mLJEveryBalanceSchema = new LJEveryBalanceSchema();
					String tLimit = PubFun.getNoLimit(mGlobalInput.ManageCom);
		            String tCureNo = PubFun1.CreateMaxNo("PRTSEQNO", tLimit); //��ţ�����
		            mLJEveryBalanceSchema.setSerialeNo(tCureNo); //��ˮ��
		            mLJEveryBalanceSchema.setOperationType("YSBF10401"); //ҵ������(YSBF��ΪӦ�ձ��ѵ���д)
		            mLJEveryBalanceSchema.setGrpContNo(tlistTable.getValue(4,i)); //�����ͬ����
		            mLJEveryBalanceSchema.setOperationDate(tlistTable.getValue(1,i)); //ҵ������
		            String tBTSQL = "select t.code from ldcode  t where t.codetype='branchtype' "
		            	          +" and t.othersign='"+tlistTable.getValue(2,i)+"'";
		    		String mBranchType = tExeSQL.getOneValue(tBTSQL);//��ѯҵ��Ա��������
		            mLJEveryBalanceSchema.setBranchType(mBranchType); //ҵ��Ա����
		            mLJEveryBalanceSchema.setBranchTypeName(tlistTable.getValue(2,i)); //ҵ��Ա��������
		            mLJEveryBalanceSchema.setEntryNo(tlistTable.getValue(3,i)); //ҵ��Ӫ�ܴ�
		            mLJEveryBalanceSchema.setRiskCode(tlistTable.getValue(6,i)); //���ֱ���
		            mLJEveryBalanceSchema.setRiskName(tlistTable.getValue(7,i)); //��������
		            String tSCSQL = "select d.code from ldcode d where d.codetype = 'agenttype'  "
          	                      +" and d.codename ='"+tlistTable.getValue(9,i)+"'";
  		            String mSaleChnl = tExeSQL.getOneValue(tSCSQL);//��ѯ������������
		            mLJEveryBalanceSchema.setSaleChnl(mSaleChnl); //��������
		            mLJEveryBalanceSchema.setSaleChnlName(tlistTable.getValue(9,i)); //������������
		            String tMCSQL = " select m.comcode from ldcom m where  "
	                              +" m.name='"+tlistTable.getValue(10,i)+"'";
                    String mManageCom = tExeSQL.getOneValue(tMCSQL);//��ѯ�����������
		            mLJEveryBalanceSchema.setManageCom(mManageCom); //�������
		            mLJEveryBalanceSchema.setManageComName(tlistTable.getValue(10,i)); //�����������
		            mLJEveryBalanceSchema.setGrpName(tlistTable.getValue(5,i)); //��λ����
		            mLJEveryBalanceSchema.setPrem(tlistTable.getValue(8,i)); //���շ�
		            String tANSQL = "select t.agentcode from laagent t where  "
   	                              +" t.name='"+tlistTable.getValue(11,i)+"'";
	                String mAgentCode= tExeSQL.getOneValue(tANSQL);//��ѯ�����˱������
		            mLJEveryBalanceSchema.setAgentCode(mAgentCode); //�����˱���
		            mLJEveryBalanceSchema.setAgentName(tlistTable.getValue(11,i)); //����������
		            mLJEveryBalanceSchema.setCValiDate(tlistTable.getValue(12,i)); //������Ч����
		            mLJEveryBalanceSchema.setPayEndDate(tlistTable.getValue(13,i)); //������ֹ����
		            mLJEveryBalanceSchema.setOperator("001"); //����Ա
		            mLJEveryBalanceSchema.setMakeDate(PubFun.getCurrentDate()); //��¼ʱ��
		            mLJEveryBalanceSchema.setMakeTime(PubFun.getCurrentTime());
		            mLJEveryBalanceSchema.setModifyDate(PubFun.getCurrentDate());
		            mLJEveryBalanceSchema.setModifyTime(PubFun.getCurrentTime());
		            mLJEveryBalanceSet.add(mLJEveryBalanceSchema);
				}
				map.put(mLJEveryBalanceSet, "INSERT");
				mInputData.clear();
				mInputData.add(map);
				PubSubmit tPubSubmit = new PubSubmit();
				if (!tPubSubmit.submitData(mInputData, "INSERT")) {
					return false;
				}
			}
		}

		if (ASFlag.equals("A"))// ������������������ɵı����������ļ����̶�Ŀ¼��
		{
			// System.out.println("���·�� "+strRealPath);
			System.out.println("Strart Ӧ�ձ��ѱ������ݵ�����ʼ.......");
			String sqlComName = " select a.shortname  from LDCom a where a.comcode='"
					+ mGlobalInput.ManageCom + "'";
			String aComName = tExeSQL.getOneValue(sqlComName); // �������� ����ƣ�

			// System.out.println("�����ļ��е�·����"+folderPath);
			File folder = new File(folderPath);
			// �ж��ļ����Ƿ���ڣ�û���򴴽�
			if (!folder.exists()) {
				folder.mkdirs();
			}
			String floderDate = "";
			if(DMFlag.equals("D"))//�ս���������
			{
				floderDate = folderPath + "/" + "Ӧ�ձ����ս�";// �ļ�����(Ӧ�ձ��ѱ���)-->���ļ�����(��������)-->�ļ���(Ӧ�ձ��ѱ�������)
			} 
			if(DMFlag.equals("M"))//�½���������
			{
				floderDate = folderPath + "/" + "Ӧ�ձ����½�";
			}
			File folder2 = new File(floderDate);
			// �ж��ļ����Ƿ���ڣ�û���򴴽�
			if (!folder2.exists()) {
				folder2.mkdirs();
			}
			String strManaCom = "";// ������/�½ᱨ��-->Ӧ�ձ��ѱ���-->��������-->���ѣ���������д������
			strManaCom = floderDate + "/" + mName;
			File folder3 = new File(strManaCom);
			// �ж��ļ����Ƿ���ڣ�û���򴴽�
			if (!folder3.exists()) {
				folder3.mkdirs();
			}
			System.out.println("Ӧ�ձ��ѱ��������ļ��е�·��Ϊ��" + strManaCom);

			/** ��xmlexport��ӵ�mResult�У��Թ��Ժ���� */
			// ��ָ��·���Ͻ����ļ�
			String time = Calendar.getInstance().getTime().toString();
			String date = "";
			if(DMFlag.equals("D"))//�ս���������// �ս��ļ�����Ӧ�գ���������д������
			{
				date = mCurrentDate.substring(0, 4)
						+ mCurrentDate.substring(5, 7)
						+ mCurrentDate.substring(8, 10) + "_"
						+ time.substring(11, 13) + time.substring(14, 16)
						+ time.substring(17, 19);
			}
			if(DMFlag.equals("M"))//�½���������// �½��ļ�����Ӧ�գ���������д������
			{
				date = mCurrentDate.substring(0, 4)
						+ mCurrentDate.substring(5, 7) + "_"
						+ time.substring(11, 13) + time.substring(14, 16)
						+ time.substring(17, 19);
			}
			String filePath = strManaCom + "/" + "Ӧ��" + "_" + aComName + "_"
					+ "_" + date + ".xls";// �ļ���:Ӧ�գ���������д������
			File file = new File(filePath);
			// �ж��ļ��Ƿ���ڣ�û���򴴽�
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO �Զ����� catch ��
					e.printStackTrace();
				}
			}
//			CombineVts tcombineVts = null;
//			String tComSQL = "select SysVarValue from ldsysvar where Sysvar='VTSFilePath'";
//			String strFilePath = tExeSQL.getOneValue(tComSQL);
//			// ���.vts���ļ�
//			String strVFFileName = strFilePath + "001" + "_"
//					+ FileQueue.getFileName() + ".vts";
//			String strVFPathName = folder.getParentFile().getParentFile()
//					+ strVFFileName;
//			// �ϲ�VTS�ļ�
//			String strTemplatePath = folder.getParentFile().getParentFile()
//					+ "/f1print/yihetemplate/";
//			// XmlExport txmlExport = new XmlExport();
//			// txmlExport = (XmlExport)
//			// mResult.getObjectByObjectName("XmlExport", 0);
//			tcombineVts = new CombineVts(xmlexport.getInputStream("UTF-8"),
//					strTemplatePath);
//			ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
//			// System.out.println("�洢�ļ���" + strTemplatePath);
//			tcombineVts.output(dataStream);
//			// ��dataStream�洢�������ļ�
//			AccessVtsFile.saveToFile(dataStream, strVFPathName);
//			// System.out.println("==> Write VTS file to disk ");
//			// �Ѳ�ѯ����洢��Execl�ļ���
//			try {
//				dataStream = new ByteArrayOutputStream();
//				AccessVtsFile.loadToBuffer(dataStream, strVFPathName);
//				byte[] bArr = dataStream.toByteArray();
//				InputStream ins = new ByteArrayInputStream(bArr);
//				com.f1j.ss.BookModelImpl bm = new com.f1j.ss.BookModelImpl();
//				if (ins != null) {
//					// Now, reload data file from mem
//					bm.read(ins, new com.f1j.ss.ReadParams());
//					FileOutputStream fos = new FileOutputStream(filePath);
//					// ��.vtת��Ϊ.xls�ļ��������ļ�����ʵ�ֵ�
//					bm.write(fos, new com.f1j.ss.WriteParams(
//							com.f1j.ss.BookModelImpl.eFileExcel97));
//					fos.flush();
//					fos.close();
//				} else {
//					System.out.println("There is not any data stream!");
//				}
//				ins.close();
//				dataStream.close();
//				bm.destroy();
//			} catch (java.net.MalformedURLException urlEx) {
//				urlEx.printStackTrace();
//			} catch (java.io.IOException ioEx) {
//				ioEx.printStackTrace();
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
			//�޸������������ļ��ķ�ʽ�������ڴ���Ļ�ȡhtm�ļ����ɡ�
			 readhtml rh=new readhtml();
	    	 rh.XmlParse(xmlexport.getInputStream("UTF-8"));
	    	 String tPath=folderPath;
	    	 String outPath=strManaCom;
	    	 String ReadFileNameAndPath="";
	    	 String WriteFileNameAndPath="";
	    	 String filename=rh.getTempLateName();
	    	 tPath=strRealPath;
	    	 ReadFileNameAndPath=tPath+"/f1print/template/htmltemplate/"+filename;
	    	 WriteFileNameAndPath=filePath;
	    	 CreatDirAndFile(outPath,WriteFileNameAndPath);
	    	 rh.setReadFileAddress(ReadFileNameAndPath);
	    	 rh.setWriteFileAddress(WriteFileNameAndPath);
	    	 //System.out.println("ReadFileNameAndPath="+ReadFileNameAndPath);
	    	 //this.mSystemOutlis.add("ReadFileNameAndPath="+ReadFileNameAndPath);//�����־
            //System.out.println("WriteFileNameAndPath="+WriteFileNameAndPath);
            //this.mSystemOutlis.add("WriteFileNameAndPath="+WriteFileNameAndPath);
	    	 rh.start("vts");
			System.out.println("����Ӧ�ձ��ѱ���xls�ļ��ɹ�!");
		}
		return true;
	}
	 private void CreatDirAndFile(String outPath, String writeFileNameAndPath) 
	 {
	// TODO �Զ����ɷ������
	     try
	     {
	    	 File dirf = new File(outPath);
	    	 if(!dirf.exists())
		     {
		    	 dirf.mkdirs();
		     }
	    	 File file = new File(writeFileNameAndPath);
	    	 if(!file.exists())
	    	 {
	    		 file.createNewFile();
	    	 }
	     }
	     catch(Exception e)
	     {
	    	 e.printStackTrace();
	     }
	     
     }
	public FinDayCheckPremBL(String Calendar) {
		super();
		// TODO �Զ����ɹ��캯�����
		this.mCurrentDate = Calendar;
	}

	public static void main(String[] args) {
		// TODO �Զ����ɷ������
		PubFun tPubFun = new PubFun();
		String tDate = PubFun.getCurrentDate();
		String tEndDate = PubFun.getCurrentDate();//��ֹ����
		String DMFlag = "";// ������ս���½�ı��
		String strArr[] = new String[2];// ���뵱�����ڵ������·ݵĵ�һ������һ��
//		tDate="2011-03-18";
//		DMFlag="D";
//		tEndDate="2010-10-12";
		if (args.length > 0) {
			System.out.println("��ȡ�Ĳ����ĸ����ǣ�--> " + args.length);
			if (args.length == 1) {
				if (args[0].equals("D") || args[0].equals("M")) {
					DMFlag = args[0];
				} else {
					System.out.println("����Ĳ�����Ч�������ս���½�ı��(���½���Ϊ��D/M)!");
					return;
				}
			}
			if (args.length == 2) {
				if (args[0].equals("D") || args[0].equals("M")) {
					DMFlag = args[0];
					tDate = args[1];
				}
				if (args[1].equals("D") || args[1].equals("M")) {
					tDate = args[0];
					DMFlag = args[1];// ����ÿ�����У����ڹ̶�����ֵ����D��
				}
				 tEndDate="";//ֻ��һ�����ڲ���ʱ��Ĭ����ʼ����ֹ���ھ�Ϊ������
		    }
		    if(args.length==3)//����Ĳ�������������ʼ���ڣ���ֹ���ں���/�½��ǣ�
		    {
			    if(args[0].equals("D")||args[0].equals("M"))
			    {
			    	DMFlag= args[0];
			    	tDate = args[1];
			    	tEndDate = args[2];
			    }
			    if(args[1].equals("D")||args[1].equals("M"))
			    {
			    	tDate = args[0];
			    	DMFlag= args[1];//����ÿ�����У����ڹ̶�����ֵ����D��
			    	tEndDate = args[2];
			    }
			    if(args[2].equals("D")||args[2].equals("M"))
			    {
			    	tDate = args[0];
			    	tEndDate = args[1];
			    	DMFlag= args[2];//����ÿ�����У����ڹ̶�����ֵ����D��
			    }
			}
			if (!(tDate.length() == 8 || tDate.length() == 10)) {
				System.out.println("�������ʼ���ڲ�����ʽ����ȷ,�밴�ա�YYYY-MM-DD����ʽ�������ڲ���!");
				return;
			} else if (tDate.length() == 8) {
				tDate = tDate.substring(0, 4) + "-" + tDate.substring(4, 6)
						+ "-" + tDate.substring(6, 8);
			} else if (tDate
					.matches("^2[0-2]\\d{2}((-|/)|\\\\)((0?[1-9])|(1[0-2]))((-|/)|\\\\)(((0?[1-9])|([1-2]\\d))|(3[0-1]))$")) {
				tDate = tDate.replaceAll("/|\\\\", "-");
			}
			if(tEndDate!=null && !tEndDate.equals(""))
	    	{
		    	if(!(tEndDate.length()==8||tEndDate.length()==10)){
			    	System.out.println("�������ֹ���ڲ�����ʽ����ȷ,�밴�ա�YYYY-MM-DD����ʽ�������ڲ���!");
			    	return;
			    }else if(tEndDate.length()==8){
			    	tEndDate = tEndDate.substring(0,4)+"-"+tEndDate.substring(4,6)+"-"+tEndDate.substring(6,8);
			    }else if(tEndDate.matches("^2[0-2]\\d{2}((-|/)|\\\\)((0?[1-9])|(1[0-2]))((-|/)|\\\\)(((0?[1-9])|([1-2]\\d))|(3[0-1]))$")){
			    	tEndDate = tEndDate.replaceAll("/|\\\\","-");
			    }
	    	}
		} else {
			System.out.println("��������������Ҫ����һ�����������½���(D/M)!");
			return;
		}
		if (DMFlag == null || DMFlag.equals("")) {
			System.out.println("�������/�½��ǲ���Ϊ��!");
			return;
		}
		System.out.println("��ȡ����ʼ����.�ǣ� "+tDate+" ��ֹ�����ǣ� "+ tEndDate +" ��/�½����ǣ�"+DMFlag);
		// Ŀǰ��Ϊÿ������00:10:00�Զ�����
		FinDayCheckPremBL tFinDayCheckPremBL = new FinDayCheckPremBL(tDate);
		String mDay[] = new String[10];
		ExeSQL tExeSQL = new ExeSQL();
		String tQuerySQL = "  select to_date('" + tDate
				+ "','yyyy-mm-dd')-1 from dual";
		String mStartDate = tExeSQL.getOneValue(tQuerySQL);
		mStartDate = mStartDate.substring(0, 10);
		String mEndDate = "";// ��������
		if (DMFlag.equals("D"))// �����ս�
		{
			if(tEndDate==null || tEndDate.equals(""))//��ֹ����Ϊ��
			{
			tQuerySQL = "  select to_date('" + tDate
					+ "','yyyy-mm-dd') from dual";// �ս᲻�����һ�죬ֱ�Ӿ��Ǵ�������ĵ���,�ձ�����00:00��ǰ����
			mStartDate = tExeSQL.getOneValue(tQuerySQL);
			mStartDate = mStartDate.substring(0, 10);
			mDay[0] = mStartDate;
			mEndDate = mStartDate;
			mDay[1] = mEndDate;
			}
			else
			{				 
				 tQuerySQL = "  select to_date('"+tDate+"','yyyy-mm-dd') from dual";//�ս᲻�����һ�죬ֱ�Ӿ��Ǵ�������ĵ���,�ձ�����00:00��ǰ����
				 mStartDate = tExeSQL.getOneValue(tQuerySQL);
				 mStartDate=mStartDate.substring(0,10);
				 mDay[0]=mStartDate;
				 //����������ֹ���ڲ�Ϊ�գ������������ȡֵ
				 tQuerySQL = "  select to_date('"+tEndDate+"','yyyy-mm-dd') from dual";//�ս᲻�����һ�죬ֱ�Ӿ��Ǵ�������ĵ���,�ձ�����00:00��ǰ����
				 mEndDate = tExeSQL.getOneValue(tQuerySQL);
				 mEndDate=mEndDate.substring(0,10);
				 mDay[1]=mEndDate;
			}
		}
		if (DMFlag.equals("M"))// �����½�
		{
			strArr = tPubFun.calFLDate(mStartDate);
			mStartDate = strArr[0];
			mEndDate = strArr[1];
			mDay[0] = mStartDate;
			mDay[1] = mEndDate;
		}
		 if (mStartDate.compareTo(mEndDate) > 0) {
			    System.out.println("�����������ʼ���ڲ��ܴ�����ֹ���ڣ�");
				return ;
		 }
		String folderPath = "";// �����ļ�·��
		File path = new File(FinDayCheckPremBL.class.getResource("/").getFile());
		String strRealPath = path.getParentFile().getParentFile().toString();
		if(DMFlag.equals("D"))//�ս���������
		{
			folderPath = strRealPath + "/batchprint/�����սᱨ��";
		} 
		if(DMFlag.equals("M"))//�ս���������
		{
			folderPath = strRealPath + "/batchprint/�����½ᱨ��";
		}
		mDay[5] = folderPath;
		mDay[6] = "A";// ��ʾ�������������ɵı���
		mDay[7]=DMFlag;//���½���������
 	   	mDay[8]=strRealPath;//��ȡui��Ŀ¼·��
		SSRS aSSRS = new SSRS();
		String comCodeSql = "  select m.comcode from ldcom m where m.comgrade='4' order by comcode ";// ��ѯĩ���������
		aSSRS = tExeSQL.execSQL(comCodeSql);
		int ComCont = aSSRS.getMaxRow();
		String strManageCom[];
		if (ComCont > 0) {
			for (int a = 1; a <= ComCont; a++) {
				for (int b = 1; b <= aSSRS.getMaxCol(); b++) {
					VData tVData = new VData();
					GlobalInput tG = new GlobalInput();
					strManageCom = new String[ComCont];
					strManageCom[b] = aSSRS.GetText(a, b);
					tG.ManageCom = strManageCom[b];
					tVData.addElement(mDay);
					String tGrpContNo = "";// Ϊ�˱�����ҳ��һ�£��ֲ�Ӱ��ҳ�棬���Զ���˱���ֵ
					String tGrpName = "";
					String tPrtNo = "";
					tVData.addElement(tGrpContNo);
					tVData.addElement(tGrpName);
					tVData.addElement(tPrtNo);
					tVData.addElement(tG);
					tFinDayCheckPremBL.submitData(tVData, "");
				}
			}
		}
	}
}

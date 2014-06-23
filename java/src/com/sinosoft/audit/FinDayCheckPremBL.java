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

	/** 错误处理类，每个需要错误处理的类中都放置该类 */
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

	private String folderPath;// 生成文件路径

	private String ASFlag;// 屈分是走页面还是运行批处理生成报表
	String DMFlag="";//传入的日结或月结的标记
	String strRealPath="";//获取UI根目录路径
	// 业务处理相关变量
	/** 全局数据 */
	private GlobalInput mGlobalInput = new GlobalInput();

	public FinDayCheckPremBL() {
	}

	/**
	 * 传输数据的公共方法
	 * 
	 * @param cInputData
	 *            VData
	 * @param cOperate
	 *            String
	 * @return boolean
	 */
	public boolean submitData(VData cInputData, String cOperate) {
		// mOperate = cOperate;
		// 得到外部传入的数据，将数据备份到本类中
		if (!getInputData(cInputData)) {
			return false;
		}
		mResult.clear();
		// 准备所有要打印的数据
		if (!getPrintData()) {
			return false;
		}
		return true;
	}

	/**
	 * 从输入数据中得到所有对象 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
	 * 
	 * @param cInputData
	 *            VData
	 * @return boolean
	 */
	private boolean getInputData(VData cInputData) {
		// 全局变量
		String[] mDay = (String[]) cInputData.get(0);
		mStartDate = mDay[0];
		mEndDate = mDay[1];
		mGrpName = mDay[2];
		mGrpContNo = mDay[3];
		mPrtNo = mDay[4];
		folderPath = mDay[5];
		ASFlag = mDay[6];
		DMFlag=mDay[7];//获取日/月批处理标记
		strRealPath=mDay[8];//获取UI根目录路径
		this.mGlobalInput = ((GlobalInput) cInputData.getObjectByObjectName(
				"GlobalInput", 0));
		// mManageCom = mGlobalInput.ManageCom;
		if (mStartDate == null || mStartDate.equals("")) {
			buildError("submitData", "起始日期不能为空！");
			return false;
		}
		if (mEndDate == null || mEndDate.equals("")) {
			buildError("submitData", "终止日期不能为空！");
			return false;
		}

		if (mStartDate.compareTo(mEndDate) > 0) {
			buildError("submitData", "起始日期不能大于终止日期！");
			return false;
		}
		return true;
	}

	/**
	 * 返回处理信息
	 * 
	 * @return VData
	 */
	public VData getResult() {
		return mResult;
	}

	/**
	 * 返回错误信息
	 * 
	 * @return CErrors
	 */
	public CErrors getErrors() {
		return mErrors;
	}

	/**
	 * 错误构建
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
	 * 打印处理
	 * 
	 * @return boolean
	 */
	private boolean getPrintData() {
		if (!getPrintDataOUT()) {
			return false;
		}

		return true;
	}

	// 付费日结
	private boolean getPrintDataOUT() {
		if(ASFlag.equals("A"))//如果是运行批处理生成的报表，才生成文件到固定目录下
		{
			xmlexport.createDocument("FinDayCheckPremOut.htm", "");
		}
		else
		{
		xmlexport.createDocument("FinDayCheckPremOut.vts", "");
		}
		TextTag mTextTag = new TextTag();
		mTextTag.add("CurrentDate", mCurrentDate); // 打印日期
		System.out.println("统计生成报表的起始日期：" + mStartDate + " 结束日期：" + mEndDate);
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
		tlistTable.setName("PayReportPIT"); // 给模板的表格命名Pay,保持与模板命名一致

		// 定期结算财务确认
		tSQL6 = "  select c.modifydate,decode(b.branchtype,'1','KA','2','DB','3','GA'),b.entryno,c.otherno,d.grpname ,"
				+ "a.riskcode,(select riskname from lmrisk where riskcode = a.riskcode),"
				+ "-a.money,c.tempfeeno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//管理机构名称
				+" From ljtempfee c,lcgrpcont d,ljpbalancerelasub a,laagent b "
				+ "where c.tempfeetype ='8' and c.otherno=d.grpcontno and b.agentcode=d.agentcode "
				+ " and c.tempfeeno=a.balancerelano and c.modifydate >= '"
				+ mStartDate
				+ "' and not exists(select 'x' from ljtempfeeclass where "
				+ "tempfeeno=c.tempfeeno and paymode in ('B','J'))  and c.modifydate <= '"
				+ mEndDate
		//		+ "' and a.riskcode<>'NIK04' "  //ASR20118405_NIK04产品财务接口
				+"'  and c.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%' and c.financedate is not null  "
				+ "and not exists(select 'x' from lbgrpcont where grpcontno=d.grpcontno)";
		// 保全财务确认 ---当天确认，转到ljagetendorse表中(作废)
		tSQL1 = " select c.modifydate,decode(d.branchtype,'1','KA','2','DB','3','GA'), d.entryno,b.grpcontno,b.grpname,"
				+ "a.riskcode,(select riskname from lmrisk where riskcode = a.riskcode),"
				+ "sum(-a.getmoney),c.tempfeeno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = b.salechnl)"
				+" ,b.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=b.managecom)"//管理机构名称
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
		// 保全财务确认 ---当天确认，转到ljapayperson表中 (作废)
		tSQL10 = " select c.modifydate,decode(d.branchtype,'1','KA','2','DB','3','GA'), d.entryno,b.grpcontno,b.grpname,"
				+ "a.riskcode,(select riskname from lmrisk where riskcode = a.riskcode),"
				+ "sum(-a.sumduepaymoney),c.tempfeeno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = b.salechnl)"
				+" ,b.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=b.managecom)"//管理机构名称
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
		// 保全财务确认 －－非当天确认，在ljsgetendorse中 (作废)
		tSQL7 = " select c.modifydate,decode(d.branchtype,'1','KA','2','DB','3','GA'), d.entryno,b.grpcontno,b.grpname,a.riskcode,(select "
				+ "riskname from lmrisk where riskcode = a.riskcode),sum(-a.getmoney),c.tempfeeno"
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = b.salechnl)"
				+" ,b.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=b.managecom)"//管理机构名称
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
		// 特权签单
		tSQL2 = " select a.makedate ,decode(d.branchtype,'1','KA','2','DB','3','GA'), d.entryno,c.grpcontno,c.grpname,"
				+ "b.riskcode,(select riskname from lmrisk  where riskcode = b.riskcode), "
				+ "sum(b.sumactupaymoney),a.payno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//管理机构名称
				+" from ljapay a,lcgrpcont c,laagent d,ljapaygrp b where a.incomeno=c.grpcontno and  "
				+ "incometype in ('B','J') and b.payno=a.payno and c.grpgroupno is null  "
				+ " and d.agentcode=c.agentcode "
		//		+" and b.riskcode<> 'NIK04'"  //ASR20118405_NIK04产品财务接口
				+" and a.makedate >='"
				+ mStartDate
				+ "'"
				+ " and a.makedate <='"
				+ mEndDate
				+ "' and a.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%'  and not exists(select 'x' from lbgrpcont where grpcontno=c.grpcontno)";

		// 保全退费 财务确认  (做废,生成预收非应收)
		tSQL3 = " select a.modifydate ,decode(c.branchtype,'1','KA','2',"
				+ "'DB','3','GA'),c.entryno, d.grpcontno,d.grpname,"
				+ "b.riskcode,f.riskname ,sum(-b.getmoney),a.actugetno  "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//管理机构名称
				+" From LJAGet a,"
				+ "laagent c,lcgrpcont d, ljagetendorse b,lmriskapp f where "
				+ "b.feefinatype not in ('TQ', 'SX') and b.riskcode<>'NIK04' and " //fengyan PIR20092040
				+ "b.feeoperationtype<>'WT'  and a.paymode<>'A' and ( f.riskperiod<>'L') "//只排除长险，定期寿险为 and  f.risktype<>'L'
				+ " and f.riskcode=b.riskcode and not exists  (select 'x' from  "
				+ " ljpbalancerela where payno=a.actugetno) and d.grpcontno=b.grpcontno "
				+ " and a.actugetno=b.actugetno and c.agentcode=b.agentcode and a.modifydate >= '"
				+ mStartDate
				+ "' and a.modifydate <= '"
				+ mEndDate
				+ "' and b.GetMoney<>0 and a.financedate is not null  and a.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%'  and not exists(select 'x' from lbgrpcont where grpcontno=d.grpcontno)";
		// 保全退费 财务确认
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
		// 定期结算退费 财务确认
		tSQL12 = " select a.modifydate ,decode(c.branchtype,'1','KA','2',"
				+ "'DB','3','GA'),c.entryno, d.grpcontno,d.grpname,"
				+ "e.riskcode,(select riskname from lmrisk  where riskcode = e.riskcode),"
				+ "sum(-e.money),a.actugetno  "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//管理机构名称
				+" From LJAGet a,laagent c,lcgrpcont d, "
				+ "ljpbalancerelasub e where e.balancerelano=a.actugetno "
				+" and exists "
				+" (select r.grpcontno from ljpbalancerela r where r.balancerelano = e.balancerelano and r.grpcontno = d.grpcontno)"
			//	+ "and  e.riskcode<>'NIK04' "  //ASR20118405_NIK04产品财务接口
				+ "  and c.agentcode=d.agentcode and a.modifydate >= '"
				+ mStartDate
				+ "' and a.modifydate <= '"
				+ mEndDate
				+ "' and a.financedate is not null  and a.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%'  and not exists(select 'x' from lbgrpcont where grpcontno=d.grpcontno)";

		// 定期结算核销后换号
		tSQL4 = " select c.modifydate,decode(b.branchtype,'1','KA','2','DB','3','GA'),"
				+ "b.entryno,c.otherno,d.grpname ,a.riskcode,(select riskname from lmrisk"
				+ " where riskcode = a.riskcode),-a.money,c.tempfeeno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//管理机构名称
				+" From ljtempfee c,lcgrpcont d,"
				+ "ljpbalancerelasub a,laagent b,ljapay e where c.tempfeetype ='8' and"
				+ " c.otherno=d.grpcontno and b.agentcode=d.agentcode "
				+ "and c.tempfeeno=e.getnoticeno "
			//	+"  and a.riskcode<>'NIK04' "  //ASR20118405_NIK04产品财务接口
				+ " and e.payno=a.balancerelano and c.modifydate >= '"
				+ mStartDate
				+ "' and not exists(select 'x' from ljtempfeeclass where "
				+ "tempfeeno=c.tempfeeno and paymode in ('B','J'))  and c.modifydate <= '"
				+ mEndDate
				+ "' and c.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%' and c.financedate is not null  "
				+ "and not exists(select 'x' from lbgrpcont where grpcontno=d.grpcontno)";
		// 保全加人签单+定期结算===从ljapayperson中取数
		tSQL9 = " select a.makedate ,decode(d.branchtype,'1','KA','2','DB','3','GA'), d.entryno,c.grpcontno,c.grpname,"
				+ "b.riskcode,(select riskname from lmrisk  where riskcode = b.riskcode), "
				+ " sum(b.sumactupaymoney),a.payno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//管理机构名称
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
				+" and a.incometype in ('B','J')"//特权保全收费情况锁定此项目
				;//收费审批有长险接口
		// 保全加人签单+定期结算===从ljagetendorse中取数
		tSQL5 = " select b.makedate,decode(c.branchtype,'1','KA','2','DB','3','GA'),c.entryno,d.grpcontno,d.grpname,"
				+ "a.riskcode,(select riskname from lmrisk  where riskcode = a.riskcode), "
				+ "sum(a.getmoney),a.actugetno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//管理机构名称
				+" from ljagetendorse a,lcgrpcont d,ljapay b,laagent c "
				+ "where b.payno=a.actugetno and d.grpgroupno is null"
				+ " and c.agentcode=d.agentcode and d.grpcontno= a.grpcontno "
				+" and b.incometype in ('B','J')"//特权保全收费情况锁定此项目
				//+ " and a.GetMoney<>0 and a.riskcode<> 'NIK04' and b.makedate >='"
				+ " and a.GetMoney<>0 and b.makedate >='"
				+ mStartDate
				+ "'"
				+ " and b.makedate <='"
				+ mEndDate
				+ "' and a.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%'  and not exists(select 'x' from lbgrpcont where grpcontno=d.grpcontno)";
		// 保全减人签单 ,除去契撤部分
		tSQL8 = " select b.makedate,decode(c.branchtype,'1','KA','2','DB','3','GA'),c.entryno,d.grpcontno,d.grpname,"
				+ "a.riskcode,(select riskname from lmrisk  where riskcode = a.riskcode), "
				+ "sum(a.getmoney),a.actugetno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//管理机构名称
				+" from ljagetendorse a,lcgrpcont d,ljaget b,laagent c "
				+ "where b.actugetno=a.actugetno and not exists(select 'x' from  "
				+ " ljpbalancerela where balancerelano=b.actugetno)"
				//+ "  and d.grpgroupno is null and a.feeoperationtype <> 'ZH' and a.riskcode<> 'NIK04' "
				+ "  and d.grpgroupno is null and a.feeoperationtype <> 'ZH' "
//				+" and not exists (select k.riskcode from lmriskapp k where k.riskcode=a.riskcode and "
//				+" (k.riskperiod='L' ) ) "//只排除长险，定期寿险为 or k.risktype='L'
				+" and b.tqflag ='Y'"//特权保全收费情况锁定此项目
				+ " and c.agentcode=d.agentcode and d.grpcontno= a.grpcontno  and b.makedate >='"
				+ mStartDate
				+ "' and b.makedate <='"
				+ mEndDate
				+ "'  and a.GetMoney<>0 and b.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%'  and not exists(select 'x' from lbgrpcont where grpcontno=a.grpcontno)";
		// 保全减人签单
		tSQL11 = " union all select b.makedate,decode(c.branchtype,'1','KA','2','DB','3','GA'),c.entryno,d.grpcontno,d.grpname,"
				+ "a.riskcode,(select riskname from lmrisk  where riskcode = a.riskcode), "
				+ "sum(a.getmoney),a.actugetno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//管理机构名称
				+" from ljagetendorse a,lbgrpcont d,ljaget b,laagent c "
				+ "where b.actugetno=a.actugetno and not exists(select 'x' from  "
				+ " ljpbalancerela where balancerelano=b.actugetno)"
				//+ "  and d.grpgroupno is null and a.riskcode<> 'NIK04'"
				+ "  and d.grpgroupno is null "
//				+" and not exists (select k.riskcode from lmriskapp k where k.riskcode=a.riskcode and "    不排除长险
//				+" (k.riskperiod='L') ) "//只排除长险，定期寿险 or k.risktype='L'
				+" and b.tqflag ='Y'"//特权保全收费情况锁定此项目
				+ " and c.agentcode=d.agentcode and d.grpcontno= a.grpcontno  and b.makedate >='"
				+ mStartDate
				+ "' and a.feeoperationtype <> 'ZH' and b.makedate <='"
				+ mEndDate
				+ "' and a.feeoperationtype<>'WT'  and a.feeoperationtype <> 'ZH' and a.GetMoney<>0 and b.managecom like '"
				+ mGlobalInput.ManageCom + "%' ";
		// 增加减人，但是存在正数金额
		tSQL13 = " select  a.makedate,decode(d.branchtype,'1','KA','2','DB',"
				+ "'3','GA'),d.entryno,c.grpcontno,c.grpname,b.riskcode,"
				+ "(select riskname from lmrisk  where riskcode = b.riskcode),"
				+ " sum(b.sumactupaymoney),a.actugetno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//管理机构名称
				+ "from ljaget a,lcgrpcont c,lmrisk m ,"
				+ "ljapayperson b,laagent d where b.payno=a.actugetno and "
				+ "c.grpcontno=b.grpcontno and exists(select 'x' from lpedormain where "
				+ "edorno=a.otherno) and c.grpgroupno is null and c.agentcode=d.agentcode"
				+ " and m.riskcode=b.riskcode "
			//	+" and b.riskcode<> 'NIK04' " //ASR20118405_NIK04产品财务接口
				+" and a.tqflag ='Y'"//特权保全收费情况锁定此项目
				+ " and b.sumactupayMoney<>0  and a.makedate >='"
				+ mStartDate
				+ "'"
				+ " and a.makedate <='"
				+ mEndDate
				+ "' and a.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%'  and not exists(select 'x' from lbgrpcont where grpcontno=c.grpcontno)";
		// 续期财务确认
		/** huangkai--2008-09-09--更新ASR20082078数据（续期中将实收总表和子表的getnoticeno置成暂交费表的tempfeeno）后，
		续期财务收款查询直接用暂缴费表的tempfeeno和实收总表、实收子表的getnoticeno关联查询 */
		// Tracy modify 2008-03-18 续期财务可能存在一次收多次费的情况，需要判断
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
					+" ,d.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
	                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//管理机构名称
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
					+" ,d.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
	                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//管理机构名称
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

		// 保全退费转预收   (做废)
		tSQL15 = " select a.confmakedate ,decode(c.branchtype,'1','KA','2',"
				+ "'DB','3','GA'),c.entryno, d.grpcontno,d.grpname ,"
				+ "b.riskcode,m.riskname ,sum(-b.getmoney),a.actugetno  "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//管理机构名称
				+ "From ljfiget a,ljagetendorse b,laagent c,lcgrpcont d,lmrisk m "
				+ "where a.paymode = 'A'  and a.actugetno=b.actugetno and a.othernotype='3' "
				+ "and b.grpcontno=d.grpcontno and b.GetMoney<>0 "
				+ " and m.riskcode=b.riskcode "
				+" and b.riskcode <> 'NIK04' "  
				+" and not exists (select k.riskcode from lmriskapp k where k.riskcode=b.riskcode and "
				+" (k.riskperiod='L' ) ) "//只排除长险,定期寿险or k.risktype='L'
				+ " and b.agentcode=c.agentcode and a.confmakedate >= '"
				+ mStartDate
				+ "' and a.confmakedate <= '"
				+ mEndDate
				+ "' and a.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%' and not exists(select 'x' from lbgrpcont where grpcontno=d.grpcontno)";
		// 保全退费转预收  (做废)
		tSQL21 = " select a.confmakedate ,decode(c.branchtype,'1','KA','2',"
				+ "'DB','3','GA'),c.entryno, d.grpcontno,d.grpname ,"
				+ "b.riskcode,m.riskname ,sum(-b.sumduepaymoney),a.actugetno  "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//管理机构名称
				+ "From ljfiget a,ljapayperson b,laagent c,lcgrpcont d,lmrisk m "
				+ "where a.paymode = 'A'  and a.actugetno=b.payno and a.othernotype='3' "
				+ "and b.grpcontno=d.grpcontno and b.sumduepaymoney<>0 "
				+ " and m.riskcode=b.riskcode "
				+"  and b.riskcode <> 'NIK04' " 
				+" and not exists (select k.riskcode from lmriskapp k where k.riskcode=b.riskcode and "
				+" (k.riskperiod='L' ) ) "//只排除长险和定期寿险,or k.risktype='L'
				+ " and b.agentcode=c.agentcode and a.confmakedate >= '"
				+ mStartDate
				+ "' and a.confmakedate <= '"
				+ mEndDate
				+ "' and a.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%' and not exists(select 'x' from lbgrpcont where grpcontno=d.grpcontno)";
		// 续期签单
//		huagnkai--2008-09-09--PIR20081308--续期中可能存在总表和子表的getnoticeno一样，但是业务日期不一致的情况，应再用serialno控制
		tSQL16 = "  select a.makedate,decode(d.branchtype,'1','KA','2','DB','3',"
				+ "'GA'),d.entryno,c.grpcontno,c.grpname,b.riskcode,m.riskname,"
				+ "sum(b.sumduepaymoney) "
				+" ,a.Getnoticeno"
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//管理机构名称
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
		// 续期签单，转实收
//		huagnkai--2008-09-09--PIR20081308--续期中可能存在总表和子表的getnoticeno一样，但是业务日期不一致的情况，应再用它们的主键payno控制
		tSQL20 = "  select a.makedate,decode(d.branchtype,'1','KA','2','DB','3',"
				+ "'GA'),d.entryno,c.grpcontno,c.grpname,b.riskcode,m.riskname,"
				+ "sum(b.sumduepaymoney) "
				+" ,a.Getnoticeno"
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//管理机构名称
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
		// 定期结算多交部分退费冲回保费分录
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
			  //  +" and d.riskcode <> 'NIK04' "  //ASR20118405_NIK04产品财务接口
			    + " and m.riskperiod<>'L'"
			    + " and b.modifydate >='" + mStartDate
			    + "' and b.modifydate <='" + mEndDate
			    +"' and b.managecom like '"+ mGlobalInput.ManageCom+ "%' "
			    +" and not exists (select 'x' from lbgrpcont where grpcontno = e.grpcontno) "
			    +" group by b.modifydate,f.branchtype,f.entryno,e.grpcontno,e.grpname,"
				+ "d.riskcode,m.riskname,d.money,a.nunmber,e.salechnl,e.managecom"
				+" union all"//添加定期多付情况
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
			//	+" and d.riskcode <> 'NIK04' "  //ASR20118405_NIK04产品财务接口
				+" and m.riskperiod<>'L' and b.financedate is not null "
				+ " and b.modifydate >='" + mStartDate
			    + "' and b.modifydate <='" + mEndDate
				+"' and b.managecom like '"+ mGlobalInput.ManageCom+ "%' "
				+" and not exists (select 'x' from lbgrpcont where grpcontno = e.grpcontno) "
				+" group by b.modifydate,f.branchtype,f.entryno,e.grpcontno,e.grpname,"
				+ "d.riskcode,m.riskname,d.money,a.nunmber,e.salechnl,e.managecom"
				;
		//定期结算少交部分退费冲回保费分录
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
		   // +" and d.riskcode <> 'NIK04'"   //ASR20118405_NIK04产品财务接口
		    +" and m.riskperiod<>'L'"
		    + " and b.modifydate >='" + mStartDate
		    + "' and b.modifydate <='" + mEndDate
		    +"' and b.managecom like '"+ mGlobalInput.ManageCom+ "%' "
		    +" and not exists (select 'x' from lbgrpcont where grpcontno = e.grpcontno) "
		    +" group by b.modifydate,f.branchtype,f.entryno,e.grpcontno,e.grpname,"
			+ "d.riskcode,m.riskname,d.money,a.nunmber,e.salechnl,e.managecom"
			+" union all"//添加定期多付情况
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
			//+" and d.riskcode <> 'NIK04' "  //ASR20118405_NIK04产品财务接口
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
				+" ,d.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//管理机构名称
				+" From LJAGet a,laagent c,lcgrpcont d, "
				+ "ljapayperson b where not exists (select 'x' from  "
				+ " ljpbalancerela where payno=a.actugetno) and d.grpcontno=b.grpcontno "
				+ " and a.actugetno=b.payno and c.agentcode=b.agentcode and a.modifydate >= '"
				+ mStartDate
				+ "' and a.paymode <> 'A' "
				//+" and b.riskcode<>'NIK04' "  //ASR20118405_NIK04产品财务接口
				+" and a.modifydate <= '"
				+ mEndDate
				+ "' and a.financedate is not null  and a.managecom like '"
				+ mGlobalInput.ManageCom
				+ "%'  and not exists(select 'x' from lbgrpcont where grpcontno=d.grpcontno)";
		// 0929增加对于契撤部分付费确认的财务分录处理
		// 如果是特权签单未交费 借：保费收入，贷：应收保费；如果是普通契撤，借：保费收入；贷：银行存款
		// ASR20082804，后收费保单会计付款(契撤退费),借:应收，贷：银行 和财务接口同步，20081202
		//PIR20113143 契撤财务接口修改，先收费定期结算出 借：保费收入 贷：应收保费 modify by Bright
		tSQL19 = "  select a.modifydate,decode(d.branchtype, '1', 'KA', '2', "
				+ "'DB', '3', 'GA'),d.entryno,c.grpcontno,c.grpname,b.riskcode,m.riskname,"
				+ "sum(-b.getmoney),0 "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//管理机构名称
				+" From LJAGet a,ljagetendorse b, lbgrpcont c, "
				+ "laagent d, lmrisk m where  a.actugetno=b.actugetno "
				+ " and b.grpcontno = c.grpcontno and c.agentcode = d.agentcode "
				+ "and b.riskcode = m.riskcode and b.feeoperationtype='WT' "
				+ "and b.feefinatype in ('TF', 'TB', 'TQ', 'SX') "
			//	+" and b.riskcode<>'NIK04'"  //ASR20118405_NIK04产品财务接口
				+" and a.modifydate >='"+ mStartDate+ "' "
				+" and a.modifydate <= '"+ mEndDate+ "' "
				+" and a.financedate is not null  "//会计付款(契撤退费)
				+" and a.managecom like '"+ mGlobalInput.ManageCom+ "%' "
				+" and not exists (select 'x' from lcgrpcont where grpcontno=b.grpcontno"
				+" and grpgroupno is not null ) "
				+" and (c.PayFlag='1' "//锁定后收费保单情况
				+" or ( c.PayFlag='0' and c.balancemode='2')) ";//锁定先收费、定期结算保单情况
		//ASR20082804 后收费保单契撤保全确认借：保费收入,贷：应收保费,和财务接口同步，20081202
       // PIR20113143 契撤财务接口修改，先收费定期结算出 借：保费收入 贷：应收保费 modify by Bright
		tSQL23 = "  select a.modifydate,decode(d.branchtype, '1', 'KA', '2', "
				+" 'DB', '3', 'GA'),d.entryno,c.grpcontno,c.grpname,b.riskcode,m.riskname,"
				+" sum(b.getmoney),0 "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
	            +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//管理机构名称
				+" From LJAGet a,ljagetendorse b, lbgrpcont c, "
				+" laagent d, lmrisk m where  a.actugetno=b.actugetno "
				+" and b.grpcontno = c.grpcontno and c.agentcode = d.agentcode "
				+" and b.riskcode = m.riskcode and b.feeoperationtype='WT' "
				+" and b.feefinatype in ('TF', 'TB', 'TQ', 'SX') "
			//	+" and b.riskcode<>'NIK04'"  //ASR20118405_NIK04产品财务接口
				+" and a.modifydate >='"+ mStartDate+ "' "
				+" and a.modifydate <= '"+ mEndDate+ "' "
				+"  "//契撤保全确认不加任何财务日期
				+" and a.managecom like '"+ mGlobalInput.ManageCom+ "%' "
				+" and not exists (select 'x' from lcgrpcont where grpcontno=b.grpcontno"
				+" and grpgroupno is not null ) "
				+" and (c.PayFlag='1' "//锁定后收费保单情况
		        +" or ( c.PayFlag='0' and c.balancemode='2'))";//锁定先收费、定期结算保单情况
		//定期结算运作部完成足额确认，收费确认 借:应收保费-团险暂记10403，贷：应收保费10401
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
		//定期结算运作部完成足额确认，使用溢交部分（收费）短期险（除NIK04）借：预收保费（20501）贷：应收保费10401
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
			//   + " and s.riskcode<>'NIK04'"  //ASR20118405_NIK04产品财务接口
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
		//完成会计收款确认(收款) 借：银行存款 贷：应收保费-团险暂记10403
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
		//定期结算撤销(定期结算已收费撤销),借：应收保费-团险暂记10403,贷：预收保费（20501）
		tSQL28 = "select a.makedate,decode(f.branchtype, '1', 'KA', '2','DB', '3', 'GA'),f.entryno, "
			   +" t.grpcontno,t.grpname,'','',-b.getmoney,0, "
	           +" (select codename from ldcode l where l.codetype = 'agenttype' and l.code = t.salechnl), "
	           +" t.managecom,(select s.name from ldcom s where s.comcode=t.managecom) "
	           +" from ljaget a,ljagettempfee b ,ljgrpdiftrace g,lcgrpcont t,laagent f "
			   +" where a.actugetno=b.actugetno and g.otherno=b.tempfeeno  "
			   +" and t.agentcode=f.agentcode and g.grpcontno=t.grpcontno"
			   +" and b.tempfeetype='8'"//将撤销操作锁定类型,排除保全撤销情况(保全撤销为,预收到预收,所以可不用显示)
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
		//huangkai--2008-09-26--保全财务确认－非当天确认也应该按下列条件分组
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
		
		//ASR20082836--财务接口，多次收费，将保全情况全部移到预收保费报表中
		//SQL[0] = tSQL1;
		SQL[1] = tSQL2;
		//SQL[2] = tSQL3;
		//SQL[3] = tSQL4;
		SQL[4] = tSQL5;
		//SQL[5] = tSQL6;//原定期结算，走银行和应收科目，现走应收暂记科目
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
					tError.errorMessage = "当前打印数据量超过20000条，请增加查询条件以减少打印记录数，然后重新打印!";
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
						strArr[0] = No; // 序号
						strArr[1] = tSSRS.GetText(i, 1); // 业务日期
						strArr[2] = tSSRS.GetText(i, 2); // 渠道
						strArr[3] = tSSRS.GetText(i, 3); // 营管处
						strArr[4] = tSSRS.GetText(i, 4); // 投保单（保单)
						strArr[5] = tSSRS.GetText(i, 5); // 投保单位
						strArr[6] = tSSRS.GetText(i, 6); // 险种编码
						strArr[7] = tSSRS.GetText(i, 7); // 险种名称
						strArr[8] = tSum; // 应收保险费
						strArr[9] = tSSRS.GetText(i, 10); // 销售渠道
						strArr[10] = tSSRS.GetText(i, 12); // ASR20082492:将管理机构名称放入此列(管理机构代码为11列)
                        //填加主服务人、保单生效日、保单终止日信息  add by Bright
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

		// ------关联lbgrpcont表得到数据
		// 定期结算财务确认
		tSQL6 = "  select c.modifydate,decode(b.branchtype,'1','KA','2','DB','3','GA'),b.entryno,c.otherno,d.grpname ,"
				+ "a.riskcode,(select riskname from lmrisk where riskcode = a.riskcode),"
				+ "-a.money,c.tempfeeno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//管理机构名称
				+" From ljtempfee c,lbgrpcont d,ljpbalancerelasub a,laagent b "
				+ "where c.tempfeetype ='8' and c.otherno=d.grpcontno and b.agentcode=d.agentcode "
				+ " and c.tempfeeno=a.balancerelano and c.modifydate >= '"
				+ mStartDate
				+ "' and not exists(select 'x' from ljtempfeeclass where "
				+ "tempfeeno=c.tempfeeno and paymode in ('B','J'))  and c.modifydate <= '"
				+ mEndDate
			//	+ " and a.riskcode<>'NIK04' " //ASR20118405_NIK04产品财务接口
				+"'and c.managecom like '"
				+ mGlobalInput.ManageCom + "%' and c.financedate is not null ";
		//huangkai--2008-09-23--PIR20081392--定期结算退费财务接口中有从b表查询而应收保费没有查询，使两者不一致
		//定期结算退费 财务确认
		tSQL12 = " select a.modifydate ,decode(c.branchtype,'1','KA','2',"
				+ "'DB','3','GA'),c.entryno, d.grpcontno,d.grpname,"
				+ "e.riskcode,(select riskname from lmrisk  where riskcode = e.riskcode),"
				+ "sum(-e.money),a.actugetno  "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//管理机构名称
				+ " From LJAGet a,laagent c,lbgrpcont d, "
				+ "ljpbalancerelasub e where e.balancerelano=a.actugetno "
				+" and exists "
				+" (select r.grpcontno from ljpbalancerela r where r.balancerelano = e.balancerelano and r.grpcontno = d.grpcontno)"
				//+ "and  e.riskcode<>'NIK04' "   //ASR20118405_NIK04产品财务接口
				+ "  and c.agentcode=d.agentcode and a.modifydate >= '"
				+ mStartDate
				+ "' and a.modifydate <= '"
				+ mEndDate
				+ "' and a.financedate is not null  and a.managecom like '"
				+ mGlobalInput.ManageCom+ "%'";
		// 保全财务确认 ---当天确认，转到ljagetendorse表中
		tSQL1 = " select c.modifydate,decode(d.branchtype,'1','KA','2','DB','3','GA'), d.entryno,b.grpcontno,b.grpname,"
				+ "a.riskcode,(select riskname from lmrisk where riskcode = a.riskcode),"
				+ "sum(-a.getmoney),c.tempfeeno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = b.salechnl)"
				+" ,b.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=b.managecom)"//管理机构名称
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
		// 保全财务确认 ---当天确认，转到ljapayperson表中  (做废)
		tSQL10 = " select c.modifydate,decode(d.branchtype,'1','KA','2','DB','3','GA'), d.entryno,b.grpcontno,b.grpname,"
				+ "a.riskcode,(select riskname from lmrisk where riskcode = a.riskcode),"
				+ "sum(-a.sumduepaymoney),c.tempfeeno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = b.salechnl)"
				+" ,b.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=b.managecom)"//管理机构名称
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
		// 保全财务确认 －－非当天确认，在ljsgetendorse中 （做废）
		tSQL7 = " select c.modifydate,decode(d.branchtype,'1','KA','2','DB','3','GA'), d.entryno,b.grpcontno,b.grpname,a.riskcode,(select "
				+ "riskname from lmrisk where riskcode = a.riskcode),sum(-a.getmoney),c.tempfeeno"
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = b.salechnl)"
				+" ,b.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=b.managecom)"//管理机构名称
				+ " From ljtempfee c ,lbgrpcont b,ljsgetendorse a,laagent d where c.tempfeetype ='4' "
				+ " and c.tempfeeno=a.getnoticeno and b.grpcontno=a.grpcontno "
				+ " and d.agentcode=b.agentcode and a.GetMoney<>0 and c.modifydate >= '"
				+ mStartDate
				+ "' and not exists(select 'x' from ljtempfeeclass where "
				+ "tempfeeno=c.tempfeeno and paymode in ('B','J'))  and c.modifydate <= '"
				+ mEndDate
				+ "' and a.riskcode<>'NIK04' and c.managecom like '"
				+ mGlobalInput.ManageCom + "%'  and c.financedate is not null";
		// 特权签单
		tSQL2 = " select a.makedate ,decode(d.branchtype,'1','KA','2','DB','3','GA'), d.entryno,c.grpcontno,c.grpname,"
				+ "b.riskcode,(select riskname from lmrisk  where riskcode = b.riskcode), "
				+ "sum(b.sumactupaymoney),a.payno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//管理机构名称
				+" from ljapay a,lbgrpcont c,laagent d,ljapaygrp b where a.incomeno=c.grpcontno and  "
				+ "incometype in ('B','J') and b.payno=a.payno and c.grpgroupno is null  "
				+ " and d.agentcode=c.agentcode"
			//	+" and b.riskcode<> 'NIK04'"  //ASR20118405_NIK04产品财务接口
				+" and a.makedate >='"
				+ mStartDate
				+ "' and a.makedate <='"
				+ mEndDate
				+ "' and a.managecom like '" + mGlobalInput.ManageCom + "%' ";

		// 保全退费 财务确认(做废)
		tSQL3 = " select a.modifydate ,decode(c.branchtype,'1','KA','2',"
				+ "'DB','3','GA'),c.entryno, d.grpcontno,d.grpname,"
				+ "b.riskcode,f.riskname ,sum(-b.getmoney),a.actugetno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//管理机构名称
				+" From LJAGet a,"
				+ "laagent c,lbgrpcont d, ljagetendorse b,lmriskapp f where "
				+ "b.feefinatype not in ('TQ', 'SX') and "    //fengyan PIR20092040
				+ "b.feeoperationtype<>'WT'  and a.paymode<>'A' and ( f.riskperiod<>'L' )  "//只排除长险，定期寿险为and  f.risktype<>'L'
				+ " and f.riskcode=b.riskcode and not exists  (select 'x' from  "
				+ " ljpbalancerela where payno=a.actugetno) and d.grpcontno=b.grpcontno "
				+ " and a.actugetno=b.actugetno and c.agentcode=b.agentcode and a.modifydate >= '"
				+ mStartDate
				+ "' and a.modifydate <= '"
				+ mEndDate
				+ "' and b.GetMoney<>0 and a.financedate is not null  and a.managecom like '"
				+ mGlobalInput.ManageCom + "%'" 
				+" and b.riskcode<>'NIK04' ";  
		// 保全退费 财务确认
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
		// 定期结算核销后换号
		tSQL4 = " select c.modifydate,decode(b.branchtype,'1','KA','2','DB','3','GA'),"
				+ "b.entryno,c.otherno,d.grpname ,a.riskcode,(select riskname from lmrisk"
				+ " where riskcode = a.riskcode),-a.money,c.tempfeeno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//管理机构名称
				+" From ljtempfee c,lbgrpcont d,"
				+ "ljpbalancerelasub a,laagent b,ljapay e where c.tempfeetype ='8' and"
				+ " c.otherno=d.grpcontno and b.agentcode=d.agentcode "
				+ "and c.tempfeeno=e.getnoticeno "
				//+ " and a.riskcode<> 'NIK04'" //ASR20118405_NIK04产品财务接口
				+ " and e.payno=a.balancerelano and c.modifydate >= '"
				+ mStartDate
				+ "' and not exists(select 'x' from ljtempfeeclass where "
				+ "tempfeeno=c.tempfeeno and paymode in ('B','J'))  and c.modifydate <= '"
				+ mEndDate
				+ "' and c.managecom like '"
				+ mGlobalInput.ManageCom + "%' and c.financedate is not null ";
		// 保全加人签单+定期结算===从ljapayperson中取数
		tSQL9 = " select a.makedate ,decode(d.branchtype,'1','KA','2','DB','3','GA'), d.entryno,c.grpcontno,c.grpname,"
				+ "b.riskcode,(select riskname from lmrisk  where riskcode = b.riskcode), "
				+ " sum(b.sumactupaymoney),a.payno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//管理机构名称
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
				+" and a.incometype in ('B','J')"//特权保全收费情况锁定此项目
				;//fengyan PIR20092040
		// 保全加人签单+定期结算===从ljagetendorse中取数
		tSQL5 = "  select b.makedate,decode(c.branchtype,'1','KA','2','DB','3','GA'),c.entryno,d.grpcontno,d.grpname,"
				+ "a.riskcode,(select riskname from lmrisk  where riskcode = a.riskcode), "
				+ "sum(a.getmoney),a.actugetno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//管理机构名称
				+" from ljagetendorse a,lbgrpcont d,ljapay b,laagent c "
				+ "where b.payno=a.actugetno and d.grpgroupno is null"
				+ " and c.agentcode=d.agentcode and d.grpcontno= a.grpcontno "
				+" and b.incometype in ('B','J')"//特权保全收费情况锁定此项目
				//+ " and a.GetMoney<>0 and a.riskcode<> 'NIK04' and b.makedate >='"
				+ " and a.GetMoney<>0 and b.makedate >='"
				+ mStartDate
				+ "' and b.makedate <='"
				+ mEndDate
				+ "' and a.managecom like '" + mGlobalInput.ManageCom + "%' ";

		// 增加保全增减人最后为收费，但是存在正数金额
		tSQL11 = "  select  a.makedate,decode(d.branchtype,'1','KA','2','DB',"
				+ "'3','GA'),d.entryno,c.grpcontno,c.grpname,b.riskcode,"
				+ "(select riskname from lmrisk  where riskcode = b.riskcode),"
				+ " sum(b.sumactupaymoney),a.actugetno "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//管理机构名称
				+ "from ljaget a,lbgrpcont c,lmrisk m ,"
				+ "ljapayperson b,laagent d where b.payno=a.actugetno and "
				+ "c.grpcontno=b.grpcontno and exists(select 'x' from lpedormain where "
				+ "edorno=a.otherno) and c.grpgroupno is null and c.agentcode=d.agentcode"
				+ " and m.riskcode=b.riskcode "
			//	+ " and b.riskcode<> 'NIK04' " //ASR20118405_NIK04产品财务接口
				+" and a.tqflag ='Y'"//特权保全收费情况锁定此项目
				+ " and b.sumactupayMoney<>0  and a.makedate >='" + mStartDate
				+ "'" + " and a.makedate <='" + mEndDate
				+ "' and a.managecom like '" + mGlobalInput.ManageCom + "%' ";
		// // 续期财务确认
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

		// 保全退费转预收（做废）
		tSQL15 = " select a.confmakedate ,decode(c.branchtype,'1','KA','2',"
				+ "'DB','3','GA'),c.entryno, d.grpcontno,d.grpname ,"
				+ "b.riskcode,m.riskname ,sum(b.getmoney),a.actugetno  "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//管理机构名称
				+ "From ljfiget a,ljagetendorse b,laagent c,lbgrpcont d,lmrisk m "
				+ "where a.paymode = 'A'  and a.actugetno=b.actugetno and a.othernotype='3' "
				+ "and b.grpcontno=d.grpcontno and b.GetMoney<>0 "
				+ " and m.riskcode=b.riskcode "
				+ " and b.riskcode<> 'NIK04' "  
				+" and not exists (select k.riskcode from lmriskapp k where k.riskcode=b.riskcode and "
				+" (k.riskperiod='L' ) ) "//只排除长险和定期寿险or k.risktype='L'
				+ " and b.agentcode=c.agentcode and a.confmakedate >= '"
				+ mStartDate + "' and a.confmakedate <= '" + mEndDate
				+ "' and a.managecom like '" + mGlobalInput.ManageCom + "%' ";
		// 保全退费转预收 （做废）
		tSQL21 = " select a.confmakedate ,decode(c.branchtype,'1','KA','2',"
				+ "'DB','3','GA'),c.entryno, d.grpcontno,d.grpname ,"
				+ "b.riskcode,m.riskname ,sum(-b.sumduepaymoney),a.actugetno  "
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = d.salechnl)"
				+" ,d.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//管理机构名称
				+ "From ljfiget a,ljapayperson b,laagent c,lbgrpcont d,lmrisk m "
				+ "where a.paymode = 'A'  and a.actugetno=b.payno and a.othernotype='3' "
				+ "and b.grpcontno=d.grpcontno and b.sumduepaymoney<>0 "
				+ " and m.riskcode=b.riskcode "
				+ " and b.riskcode <> 'NIK04' " 
				+" and not exists (select k.riskcode  from lmriskapp k where k.riskcode=b.riskcode and "
				+" (k.riskperiod='L' ) ) "//只排除长险和定期寿险or k.risktype='L'
				+ " and b.agentcode=c.agentcode and a.confmakedate >= '"
				+ mStartDate + "' and a.confmakedate <= '" + mEndDate
				+ "' and a.managecom like '" + mGlobalInput.ManageCom + "%' ";

		// 续期签单
//		huagnkai--2008-09-09--PIR20081308--续期中可能存在总表和子表的getnoticeno一样，但是业务日期不一致的情况，应再用serialno控制
		tSQL16 = "  select a.makedate,decode(d.branchtype,'1','KA','2','DB','3',"
				+ "'GA'),d.entryno,c.grpcontno,c.grpname,b.riskcode,m.riskname,"
				+ "sum(b.sumduepaymoney) "
				+" ,a.Getnoticeno"
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//管理机构名称
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
		// 续期签单，转实收
//		huagnkai--2008-09-09--PIR20081308--续期中可能存在总表和子表的getnoticeno一样，但是业务日期不一致的情况，应再用它们的主键payno控制
		tSQL20 = "  select a.makedate,decode(d.branchtype,'1','KA','2','DB','3',"
				+ "'GA'),d.entryno,c.grpcontno,c.grpname,b.riskcode,m.riskname,"
				+ "sum(b.sumduepaymoney) "
				+" ,a.Getnoticeno"
				+" ,(select codename from ldcode l where l.codetype = 'agenttype' and l.code = c.salechnl)"
				+" ,c.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=c.managecom)"//管理机构名称
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
		//定期结算多交部分退费冲回保费分录
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
		//    +" and d.riskcode <> 'NIK04' "  //ASR20118405_NIK04产品财务接口
		    +" and m.riskperiod<>'L' "
		    + " and b.modifydate >='" + mStartDate
		    + "' and b.modifydate <='" + mEndDate
		    +"' and b.managecom like '"+ mGlobalInput.ManageCom+ "%' "
		    +" and not exists (select 'x' from lcgrpcont where grpcontno = e.grpcontno) "
		    +" group by b.modifydate,f.branchtype,f.entryno,e.grpcontno,e.grpname,"
			+ "d.riskcode,m.riskname,d.money,a.nunmber,e.salechnl,e.managecom"
			+" union all"//添加定期多付情况
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
		//	+" and d.riskcode <> 'NIK04'"  //ASR20118405_NIK04产品财务接口
			+" and m.riskperiod<>'L' and b.financedate is not null "
			+ " and b.modifydate >='" + mStartDate
		    + "' and b.modifydate <='" + mEndDate
			+"' and b.managecom like '"+ mGlobalInput.ManageCom+ "%' "
			+" and not exists (select 'x' from lcgrpcont where grpcontno = e.grpcontno) "
			+" group by b.modifydate,f.branchtype,f.entryno,e.grpcontno,e.grpname,"
			+ "d.riskcode,m.riskname,d.money,a.nunmber,e.salechnl,e.managecom"
			;
	//定期结算少交部分退费冲回保费分录
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
	  //  +" and d.riskcode <> 'NIK04' "  //ASR20118405_NIK04产品财务接口
	    +" and m.riskperiod<>'L' "
	    + " and b.modifydate >='" + mStartDate
	    + "' and b.modifydate <='" + mEndDate
	    +"' and b.managecom like '"+ mGlobalInput.ManageCom+ "%' "
	    +" and not exists (select 'x' from lcgrpcont where grpcontno = e.grpcontno) "
	    +" group by b.modifydate,f.branchtype,f.entryno,e.grpcontno,e.grpname,"
		+ "d.riskcode,m.riskname,d.money,a.nunmber,e.salechnl,e.managecom"
		+" union all"//添加定期多付情况
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
	//	+" and d.riskcode <> 'NIK04' "  //ASR20118405_NIK04产品财务接口
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
				+" ,d.managecom"//ASR20082492:将保费收入日结、应收保费日结、预收保费日结、赔款支出日结增加一列机构列(管理机构代码)
                +" ,(select s.name from ldcom s where s.comcode=d.managecom)"//管理机构名称
				+" From LJAGet a,laagent c,lbgrpcont d, "
				+ "ljapayperson b where not exists (select 'x' from  "
				+ " ljpbalancerela where payno=a.actugetno) and d.grpcontno=b.grpcontno "
				+ " and a.actugetno=b.payno and c.agentcode=b.agentcode and a.modifydate >= '"
				+ mStartDate + "' and a.modifydate <= '" + mEndDate
				+ "' and a.financedate is not null and a.paymode <> 'A' "
			//	+ " and b.riskcode<>'NIK04' " //ASR20118405_NIK04产品财务接口
				+" and a.managecom like '"
				+ mGlobalInput.ManageCom + "%'";
		//定期结算运作部完成足额确认，收费确认 借:应收保费-团险暂记10403，贷：应收保费10401
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
		//定期结算运作部完成足额确认，使用溢交部分（收费）短期险（除NIK04）借：预收保费（20501）贷：应收保费10401
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
			  // +" and s.riskcode<>'NIK04'"  //ASR20118405_NIK04产品财务接口
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
//		完成会计收款确认(收款) 借：银行存款 贷：应收保费-团险暂记10403
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
//		定期结算撤销(定期结算已收费撤销),借：应收保费-团险暂记10403,贷：预收保费（20501）
		tSQL28 = "select a.makedate,decode(f.branchtype, '1', 'KA', '2','DB', '3', 'GA'),f.entryno, "
			   +" t.grpcontno,t.grpname,'','',-b.getmoney,0, "
	           +" (select codename from ldcode l where l.codetype = 'agenttype' and l.code = t.salechnl), "
	           +" t.managecom,(select s.name from ldcom s where s.comcode=t.managecom) "
	           +" from ljaget a,ljagettempfee b ,ljgrpdiftrace g,lbgrpcont t,laagent f  "
			   +" where a.actugetno=b.actugetno and g.otherno=b.tempfeeno  "
			   +" and t.agentcode=f.agentcode and g.grpcontno=t.grpcontno"
			   +" and b.tempfeetype='8'"//将撤销操作锁定类型,排除保全撤销情况(保全撤销为,预收到预收,所以可不用显示)
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
		//huangkai--2008-09-26--b表查询结果按下列条件分组
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
					tError.errorMessage = "当前打印数据量超过20000条，请增加查询条件以减少打印记录数，然后重新打印!";
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
						strArr[0] = No; // 序号
						strArr[1] = mSSRS.GetText(i, 1); // 业务日期
						strArr[2] = mSSRS.GetText(i, 2); // 渠道
						strArr[3] = mSSRS.GetText(i, 3); // 营管处
						strArr[4] = mSSRS.GetText(i, 4); // 投保单（保单)
						strArr[5] = mSSRS.GetText(i, 5); // 投保单位
						strArr[6] = mSSRS.GetText(i, 6); // 险种编码
						strArr[7] = mSSRS.GetText(i, 7); // 险种名称
						strArr[8] = tSum; // 应收保险费
						strArr[9] = mSSRS.GetText(i, 10); // 销售渠道
						strArr[10] = mSSRS.GetText(i, 12); // ASR20082492:将管理机构名称放入此列(管理机构代码为11列)
                      //填加主服务人、保单生效日、保单终止日信息  add by Bright
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

		// 得到机构名称
		String tSQL = "select name From ldcom where comcode = '"
				+ mGlobalInput.ManageCom + "'";
		String mName = tExeSQL.getOneValue(tSQL);
		mTextTag.add("Name", mName);
		// 总笔数合计

		String tSumAccount = String.valueOf(SumAccount);
		tSumAccount = new DecimalFormat("0").format(Float.valueOf(tSumAccount));
		mTextTag.add("Account", new DecimalFormat("0").format(SumAccount));
		// 总金额合计
		String tSumMoney = String.valueOf(SumMoney);
		tSumMoney = new DecimalFormat("0.00").format(Double.valueOf(tSumMoney));
		mTextTag.add("Money", new DecimalFormat("0.00").format(SumMoney));

		strArr = new String[1];
		strArr[0] = "PayReport";
		xmlexport.addListTable(tlistTable, strArr);
		xmlexport.addTextTag(mTextTag);

		mResult.addElement(xmlexport);
		//ASR20082836财务多次收费----存储财务日结部分
		if(ASFlag.equals("A"))//运行批处理的时候生成相关数据存储到数据库中 
		{
			if(DMFlag.equals("D"))//只有日结批处理运行的时候才生成数据到期数据库中，月结只生成xls
			{
 				System.out.println(mCurrentDate+ " 管理机构： "+mGlobalInput.ManageCom +" : 获取的应收保费查询结果为 :"+tlistTable.size() +" 条!");
				VData mInputData = new VData();
				MMap map= new MMap();
				LJEveryBalanceSet mLJEveryBalanceSet = new LJEveryBalanceSet();
				for (int i = 0; i <  tlistTable.size(); i++) {
					LJEveryBalanceSchema mLJEveryBalanceSchema = new LJEveryBalanceSchema();
					String tLimit = PubFun.getNoLimit(mGlobalInput.ManageCom);
		            String tCureNo = PubFun1.CreateMaxNo("PRTSEQNO", tLimit); //序号，主键
		            mLJEveryBalanceSchema.setSerialeNo(tCureNo); //流水号
		            mLJEveryBalanceSchema.setOperationType("YSBF10401"); //业务类型(YSBF定为应收保费的缩写)
		            mLJEveryBalanceSchema.setGrpContNo(tlistTable.getValue(4,i)); //集体合同号码
		            mLJEveryBalanceSchema.setOperationDate(tlistTable.getValue(1,i)); //业务日期
		            String tBTSQL = "select t.code from ldcode  t where t.codetype='branchtype' "
		            	          +" and t.othersign='"+tlistTable.getValue(2,i)+"'";
		    		String mBranchType = tExeSQL.getOneValue(tBTSQL);//查询业务员渠道代码
		            mLJEveryBalanceSchema.setBranchType(mBranchType); //业务员渠道
		            mLJEveryBalanceSchema.setBranchTypeName(tlistTable.getValue(2,i)); //业务员渠道名称
		            mLJEveryBalanceSchema.setEntryNo(tlistTable.getValue(3,i)); //业务营管处
		            mLJEveryBalanceSchema.setRiskCode(tlistTable.getValue(6,i)); //险种编码
		            mLJEveryBalanceSchema.setRiskName(tlistTable.getValue(7,i)); //险种名称
		            String tSCSQL = "select d.code from ldcode d where d.codetype = 'agenttype'  "
          	                      +" and d.codename ='"+tlistTable.getValue(9,i)+"'";
  		            String mSaleChnl = tExeSQL.getOneValue(tSCSQL);//查询销售渠道代码
		            mLJEveryBalanceSchema.setSaleChnl(mSaleChnl); //销售渠道
		            mLJEveryBalanceSchema.setSaleChnlName(tlistTable.getValue(9,i)); //销售渠道名称
		            String tMCSQL = " select m.comcode from ldcom m where  "
	                              +" m.name='"+tlistTable.getValue(10,i)+"'";
                    String mManageCom = tExeSQL.getOneValue(tMCSQL);//查询管理机构代码
		            mLJEveryBalanceSchema.setManageCom(mManageCom); //管理机构
		            mLJEveryBalanceSchema.setManageComName(tlistTable.getValue(10,i)); //管理机构名称
		            mLJEveryBalanceSchema.setGrpName(tlistTable.getValue(5,i)); //单位名称
		            mLJEveryBalanceSchema.setPrem(tlistTable.getValue(8,i)); //保险费
		            String tANSQL = "select t.agentcode from laagent t where  "
   	                              +" t.name='"+tlistTable.getValue(11,i)+"'";
	                String mAgentCode= tExeSQL.getOneValue(tANSQL);//查询代理人编码代码
		            mLJEveryBalanceSchema.setAgentCode(mAgentCode); //代理人编码
		            mLJEveryBalanceSchema.setAgentName(tlistTable.getValue(11,i)); //代理人名称
		            mLJEveryBalanceSchema.setCValiDate(tlistTable.getValue(12,i)); //保单生效日期
		            mLJEveryBalanceSchema.setPayEndDate(tlistTable.getValue(13,i)); //保单终止日期
		            mLJEveryBalanceSchema.setOperator("001"); //操作员
		            mLJEveryBalanceSchema.setMakeDate(PubFun.getCurrentDate()); //记录时间
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

		if (ASFlag.equals("A"))// 如果是运行批处理生成的报表，才生成文件到固定目录下
		{
			// System.out.println("存放路径 "+strRealPath);
			System.out.println("Strart 应收保费报表数据导出开始.......");
			String sqlComName = " select a.shortname  from LDCom a where a.comcode='"
					+ mGlobalInput.ManageCom + "'";
			String aComName = tExeSQL.getOneValue(sqlComName); // 机构名称 （简称）

			// System.out.println("生成文件夹的路径："+folderPath);
			File folder = new File(folderPath);
			// 判断文件夹是否存在，没有则创建
			if (!folder.exists()) {
				folder.mkdirs();
			}
			String floderDate = "";
			if(DMFlag.equals("D"))//日结批处理标记
			{
				floderDate = folderPath + "/" + "应收保费日结";// 文件夹名(应收保费报表)-->子文件夹名(机构名称)-->文件名(应收保费报表＋日期)
			} 
			if(DMFlag.equals("M"))//月结批处理标记
			{
				floderDate = folderPath + "/" + "应收保费月结";
			}
			File folder2 = new File(floderDate);
			// 判断文件夹是否存在，没有则创建
			if (!folder2.exists()) {
				folder2.mkdirs();
			}
			String strManaCom = "";// 财务日/月结报表-->应收保费报表-->机构名称-->保费＋机构名简写＋日期
			strManaCom = floderDate + "/" + mName;
			File folder3 = new File(strManaCom);
			// 判断文件夹是否存在，没有则创建
			if (!folder3.exists()) {
				folder3.mkdirs();
			}
			System.out.println("应收保费报表生成文件夹的路径为：" + strManaCom);

			/** 将xmlexport添加到mResult中，以供以后调用 */
			// 在指定路径上建立文件
			String time = Calendar.getInstance().getTime().toString();
			String date = "";
			if(DMFlag.equals("D"))//日结批处理标记// 日结文件名：应收＋机构名简写＋日期
			{
				date = mCurrentDate.substring(0, 4)
						+ mCurrentDate.substring(5, 7)
						+ mCurrentDate.substring(8, 10) + "_"
						+ time.substring(11, 13) + time.substring(14, 16)
						+ time.substring(17, 19);
			}
			if(DMFlag.equals("M"))//月结批处理标记// 月结文件名：应收＋机构名简写＋年月
			{
				date = mCurrentDate.substring(0, 4)
						+ mCurrentDate.substring(5, 7) + "_"
						+ time.substring(11, 13) + time.substring(14, 16)
						+ time.substring(17, 19);
			}
			String filePath = strManaCom + "/" + "应收" + "_" + aComName + "_"
					+ "_" + date + ".xls";// 文件名:应收＋机构名简写＋日期
			File file = new File(filePath);
			// 判断文件是否存在，没有则创建
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO 自动生成 catch 块
					e.printStackTrace();
				}
			}
//			CombineVts tcombineVts = null;
//			String tComSQL = "select SysVarValue from ldsysvar where Sysvar='VTSFilePath'";
//			String strFilePath = tExeSQL.getOneValue(tComSQL);
//			// 存放.vts的文件
//			String strVFFileName = strFilePath + "001" + "_"
//					+ FileQueue.getFileName() + ".vts";
//			String strVFPathName = folder.getParentFile().getParentFile()
//					+ strVFFileName;
//			// 合并VTS文件
//			String strTemplatePath = folder.getParentFile().getParentFile()
//					+ "/f1print/yihetemplate/";
//			// XmlExport txmlExport = new XmlExport();
//			// txmlExport = (XmlExport)
//			// mResult.getObjectByObjectName("XmlExport", 0);
//			tcombineVts = new CombineVts(xmlexport.getInputStream("UTF-8"),
//					strTemplatePath);
//			ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
//			// System.out.println("存储文件到" + strTemplatePath);
//			tcombineVts.output(dataStream);
//			// 把dataStream存储到磁盘文件
//			AccessVtsFile.saveToFile(dataStream, strVFPathName);
//			// System.out.println("==> Write VTS file to disk ");
//			// 把查询结果存储到Execl文件中
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
//					// 把.vt转换为.xls文件，是用文件流来实现的
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
			//修改批处理生成文件的方式，按现在处理的获取htm文件生成。
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
	    	 //this.mSystemOutlis.add("ReadFileNameAndPath="+ReadFileNameAndPath);//输出日志
            //System.out.println("WriteFileNameAndPath="+WriteFileNameAndPath);
            //this.mSystemOutlis.add("WriteFileNameAndPath="+WriteFileNameAndPath);
	    	 rh.start("vts");
			System.out.println("生成应收保费报表xls文件成功!");
		}
		return true;
	}
	 private void CreatDirAndFile(String outPath, String writeFileNameAndPath) 
	 {
	// TODO 自动生成方法存根
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
		// TODO 自动生成构造函数存根
		this.mCurrentDate = Calendar;
	}

	public static void main(String[] args) {
		// TODO 自动生成方法存根
		PubFun tPubFun = new PubFun();
		String tDate = PubFun.getCurrentDate();
		String tEndDate = PubFun.getCurrentDate();//终止日期
		String DMFlag = "";// 传入的日结或月结的标记
		String strArr[] = new String[2];// 存入当天日期的所在月份的第一天和最后一天
//		tDate="2011-03-18";
//		DMFlag="D";
//		tEndDate="2010-10-12";
		if (args.length > 0) {
			System.out.println("获取的参数的个数是：--> " + args.length);
			if (args.length == 1) {
				if (args[0].equals("D") || args[0].equals("M")) {
					DMFlag = args[0];
				} else {
					System.out.println("输入的参数无效，不是日结或月结的标记(日月结标记为：D/M)!");
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
					DMFlag = args[1];// 现日每日运行，所在固定传入值：“D”
				}
				 tEndDate="";//只有一个日期参数时。默认起始和终止日期均为此日期
		    }
		    if(args.length==3)//传入的参数有三个（起始日期，终止日期和日/月结标记）
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
			    	DMFlag= args[1];//现日每日运行，所在固定传入值：“D”
			    	tEndDate = args[2];
			    }
			    if(args[2].equals("D")||args[2].equals("M"))
			    {
			    	tDate = args[0];
			    	tEndDate = args[1];
			    	DMFlag= args[2];//现日每日运行，所在固定传入值：“D”
			    }
			}
			if (!(tDate.length() == 8 || tDate.length() == 10)) {
				System.out.println("输入的起始日期参数格式不正确,请按照“YYYY-MM-DD”格式输入日期参数!");
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
			    	System.out.println("输入的终止日期参数格式不正确,请按照“YYYY-MM-DD”格式输入日期参数!");
			    	return;
			    }else if(tEndDate.length()==8){
			    	tEndDate = tEndDate.substring(0,4)+"-"+tEndDate.substring(4,6)+"-"+tEndDate.substring(6,8);
			    }else if(tEndDate.matches("^2[0-2]\\d{2}((-|/)|\\\\)((0?[1-9])|(1[0-2]))((-|/)|\\\\)(((0?[1-9])|([1-2]\\d))|(3[0-1]))$")){
			    	tEndDate = tEndDate.replaceAll("/|\\\\","-");
			    }
	    	}
		} else {
			System.out.println("运行批处理至少要输入一个参数，日月结标记(D/M)!");
			return;
		}
		if (DMFlag == null || DMFlag.equals("")) {
			System.out.println("传入的日/月结标记不能为空!");
			return;
		}
		System.out.println("获取的起始日期.是： "+tDate+" 终止日期是： "+ tEndDate +" 日/月结标记是："+DMFlag);
		// 目前定为每天上午00:10:00自动运行
		FinDayCheckPremBL tFinDayCheckPremBL = new FinDayCheckPremBL(tDate);
		String mDay[] = new String[10];
		ExeSQL tExeSQL = new ExeSQL();
		String tQuerySQL = "  select to_date('" + tDate
				+ "','yyyy-mm-dd')-1 from dual";
		String mStartDate = tExeSQL.getOneValue(tQuerySQL);
		mStartDate = mStartDate.substring(0, 10);
		String mEndDate = "";// 结束日期
		if (DMFlag.equals("D"))// 运行日结
		{
			if(tEndDate==null || tEndDate.equals(""))//终止日期为空
			{
			tQuerySQL = "  select to_date('" + tDate
					+ "','yyyy-mm-dd') from dual";// 日结不需求减一天，直接就是传入参数的当天,日报表是00:00以前运行
			mStartDate = tExeSQL.getOneValue(tQuerySQL);
			mStartDate = mStartDate.substring(0, 10);
			mDay[0] = mStartDate;
			mEndDate = mStartDate;
			mDay[1] = mEndDate;
			}
			else
			{				 
				 tQuerySQL = "  select to_date('"+tDate+"','yyyy-mm-dd') from dual";//日结不需求减一天，直接就是传入参数的当天,日报表是00:00以前运行
				 mStartDate = tExeSQL.getOneValue(tQuerySQL);
				 mStartDate=mStartDate.substring(0,10);
				 mDay[0]=mStartDate;
				 //如果输入的终止日期不为空，则按输入的日期取值
				 tQuerySQL = "  select to_date('"+tEndDate+"','yyyy-mm-dd') from dual";//日结不需求减一天，直接就是传入参数的当天,日报表是00:00以前运行
				 mEndDate = tExeSQL.getOneValue(tQuerySQL);
				 mEndDate=mEndDate.substring(0,10);
				 mDay[1]=mEndDate;
			}
		}
		if (DMFlag.equals("M"))// 运行月结
		{
			strArr = tPubFun.calFLDate(mStartDate);
			mStartDate = strArr[0];
			mEndDate = strArr[1];
			mDay[0] = mStartDate;
			mDay[1] = mEndDate;
		}
		 if (mStartDate.compareTo(mEndDate) > 0) {
			    System.out.println("输入参数的起始日期不能大于终止日期！");
				return ;
		 }
		String folderPath = "";// 生成文件路径
		File path = new File(FinDayCheckPremBL.class.getResource("/").getFile());
		String strRealPath = path.getParentFile().getParentFile().toString();
		if(DMFlag.equals("D"))//日结批处理标记
		{
			folderPath = strRealPath + "/batchprint/财务日结报表";
		} 
		if(DMFlag.equals("M"))//日结批处理标记
		{
			folderPath = strRealPath + "/batchprint/财务月结报表";
		}
		mDay[5] = folderPath;
		mDay[6] = "A";// 表示是走批处理生成的报表
		mDay[7]=DMFlag;//日月结批处理标记
 	   	mDay[8]=strRealPath;//获取ui根目录路径
		SSRS aSSRS = new SSRS();
		String comCodeSql = "  select m.comcode from ldcom m where m.comgrade='4' order by comcode ";// 查询末级管理机构
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
					String tGrpContNo = "";// 为了保持与页面一致，又不影响页面，所以定义此变量值
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

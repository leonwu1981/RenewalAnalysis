package com.sinosoft.lis.batch;

import com.sinosoft.utility.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.pubfun.PubSubmit;
import com.sinosoft.lis.vschema.*;

import java.sql.*;
import java.util.Properties;

public class DownData {

	private Connection conn_Production; // 需要进行数据检查的数据库连接

	// private Connection conn_Test; // 存储结果的数据连接

	private ExeSQL tExeSQL_Pro = null;

	private SSRS tSSRS = null;

	// private Statement stmtTest;

	private VData mResult = new VData();

	private MMap aMMap = new MMap();

	private String SendInnerCode = "";

	private int submitNumber = 10000;

	public DownData() {
		try {
			if (!InitConn())
				System.out.println("错误::创建数据库连接失败!!");
			tExeSQL_Pro = new ExeSQL(conn_Production);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// 获取数据库连接
	private boolean InitConn() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			// 创建第一个连接
			Properties props = new Properties();
			props.setProperty("user", "liscnpcquery");
			props.setProperty("password", "dbreader60!");

			String sUrl = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST = (ADDRESS = (PROTOCOL = TCP)(HOST=10.0.60.117)(PORT =1521)))(CONNECT_DATA = (SERVICE_NAME =lis)))";
			conn_Production = DriverManager.getConnection(sUrl, props);

			Statement stmt = conn_Production
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
			stmt.execute("alter session set nls_date_format = 'YYYY-MM-DD'");
			stmt.close();

			// 创建第二个连接
			// props = new Properties();
			// props.setProperty("user", "liscnpc");
			// props.setProperty("password", "liscnpc");
			// conn_Test = DriverManager.getConnection(
			// "jdbc:oracle:thin:@10.10.0.32:1521:lis", props);
			//
			// stmtTest = conn_Test
			// .createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			// ResultSet.CONCUR_UPDATABLE);
			// stmtTest
			// .execute("alter session set nls_date_format = 'YYYY-MM-DD'");
		} catch (Exception ex) {
			conn_Production = null;
			// conn_Test = null;
			ex.printStackTrace();
			writeLog("out", "数据库连接错误。");
			return false;
		}
		return true;
	}

	public void getPublicData() {
		writeLog("out", "公共部分数据提取。");

		// 处理公共数据信息
		getOtherPData();

		// 处理发放表的中石油架构
		getSendOrganPData();

		// 处理生活补贴工作单位
		getOrganPData();

		// 处理过度年金工作单位
		getCNPCOrganPData();

		// 处理用户信息
		getUserPData();

		// 保全流程信息
		getEdorPData();
	}

	public void getData() {
		writeLog("out", "单独数据提取。");

		// 处理发放机构信息
		getSendOrganData();

		// 处理用户信息
		getUserData();

		// 处理用户信息
		getInsuredData();

		// 处理保全信息
		getEdorData();

		// 处理发放信息
		getAnnualData();

		// 过度年金工行上传信息
		getAnnuityData();
	}

	public void getAnnuityData() {
		// select * from LDFirstImport where sendinnercode like '%'
		// select * from LPFirstImport where sendinnercode like '%'
		// select * from LDSecondImport where sendinnercode like '%'
		// select * from LPSecondImport where sendinnercode like '%'
		// select * from LDSameImport where sendinnercode like '%'
		// select * from LCGrpImportLog a where grpcontno='99029288' and
		// exists(select 'x' from LDFirstImport b where b.annuityno=a.insuredno
		// and b.sendinnercode like '%')
		// select * from LPGrpImportLog a where grpcontno='99029288' and
		// exists(select 'x' from LDFirstImport b where b.annuityno=a.insuredno
		// and b.sendinnercode like '%')
		// select * from LDRepeatAV a where exists(select 'x' from LDFirstImport
		// b where b.annuityno=a.annuityno and b.sendinnercode like '%')
		// select * from LPRepeatAV a where exists(select 'x' from LDFirstImport
		// b where b.annuityno=a.annuityno and b.sendinnercode like '%')
		// select * from LDUPLoadImport a where exists(select 'x' from
		// LDFirstImport b where b.annuityno=a.annuityno and b.sendinnercode
		// like '%')
	}

	public void getAnnualData() {
		int index = 0;
		writeLog("out", "LCAnnualGetLog");
		deleteData("DELETE LCAnnualGetLog a where grpcontno='99029288' and sendinnercode like '"
				+ SendInnerCode + "%'");
		LCAnnualGetLogDB pLCAnnualGetLogDB = new LCAnnualGetLogDB(
				conn_Production);
		LCAnnualGetLogSet pLCAnnualGetLogSet = pLCAnnualGetLogDB
				.executeQuery("select * from LCAnnualGetLog a where grpcontno='99029288' and sendinnercode like '"
						+ SendInnerCode + "%'");
		index = pLCAnnualGetLogSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCAnnualGetLogSet sSet = new LCAnnualGetLogSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCAnnualGetLogSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCAnnualGetLogSet();
				}
			}
		} else {
			writeLog("noData", "LCAnnualGetLog");
		}

		writeLog("out", "LCAnnualMonthApply");
		deleteData("DELETE LCAnnualMonthApply a where grpcontno='99029288' and sendinnercode like '"
				+ SendInnerCode + "%'");

		LCAnnualMonthApplyDB pLCAnnualMonthApplyDB = new LCAnnualMonthApplyDB(
				conn_Production);
		LCAnnualMonthApplySet pLCAnnualMonthApplySet = pLCAnnualMonthApplyDB
				.executeQuery("select * from LCAnnualMonthApply a where grpcontno='99029288' and sendinnercode like '"
						+ SendInnerCode + "%'");
		index = pLCAnnualMonthApplySet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCAnnualMonthApplySet sSet = new LCAnnualMonthApplySet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCAnnualMonthApplySet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCAnnualMonthApplySet();
				}
			}
		} else {
			writeLog("noData", "LCAnnualMonthApply");
		}

		writeLog("out", "LCAnnualDebtInfo");
		deleteData("DELETE LCAnnualDebtInfo a where grpcontno='99029288' and sendinnercode like '"
				+ SendInnerCode + "%'");

		LCAnnualDebtInfoDB pLCAnnualDebtInfoDB = new LCAnnualDebtInfoDB(
				conn_Production);
		LCAnnualDebtInfoSet pLCAnnualDebtInfoSet = pLCAnnualDebtInfoDB
				.executeQuery("select * from LCAnnualDebtInfo a where grpcontno='99029288' and sendinnercode like '"
						+ SendInnerCode + "%'");
		index = pLCAnnualDebtInfoSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCAnnualDebtInfoSet sSet = new LCAnnualDebtInfoSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCAnnualDebtInfoSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCAnnualDebtInfoSet();
				}
			}
		} else {
			writeLog("noData", "LCAnnualDebtInfo");
		}

		writeLog("out", "LCAnnualDebtSub");
		deleteData("DELETE LCAnnualDebtSub a where grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
				+ SendInnerCode + "%')");

		LCAnnualDebtSubDB pLCAnnualDebtSubDB = new LCAnnualDebtSubDB(
				conn_Production);
		LCAnnualDebtSubSet pLCAnnualDebtSubSet = pLCAnnualDebtSubDB
				.executeQuery("select * from LCAnnualDebtSub a where grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLCAnnualDebtSubSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCAnnualDebtSubSet sSet = new LCAnnualDebtSubSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCAnnualDebtSubSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCAnnualDebtSubSet();
				}
			}
		} else {
			writeLog("noData", "LCAnnualDebtSub");
		}

		writeLog("out", "LCAnnualPaySub");
		deleteData("DELETE LCAnnualPaySub a where grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
				+ SendInnerCode + "%')");

		LCAnnualPaySubDB pLCAnnualPaySubDB = new LCAnnualPaySubDB(
				conn_Production);
		LCAnnualPaySubSet pLCAnnualPaySubSet = pLCAnnualPaySubDB
				.executeQuery("select * from LCAnnualPaySub a where grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLCAnnualPaySubSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCAnnualPaySubSet sSet = new LCAnnualPaySubSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCAnnualPaySubSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCAnnualPaySubSet();
				}
			}
		} else {
			writeLog("noData", "LCAnnualPaySub");
		}

		writeLog("out", "LCAnnualRedressal");
		deleteData("DELETE LCAnnualRedressal a where grpcontno='99029288' and sendinnercode like '"
				+ SendInnerCode + "%'");

		LCAnnualRedressalDB pLCAnnualRedressalDB = new LCAnnualRedressalDB(
				conn_Production);
		LCAnnualRedressalSet pLCAnnualRedressalSet = pLCAnnualRedressalDB
				.executeQuery("select * from LCAnnualRedressal a where grpcontno='99029288' and sendinnercode like '"
						+ SendInnerCode + "%'");
		index = pLCAnnualRedressalSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCAnnualRedressalSet sSet = new LCAnnualRedressalSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCAnnualRedressalSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCAnnualRedressalSet();
				}
			}
		} else {
			writeLog("noData", "LCAnnualRedressal");
		}

		writeLog("out", "LCComPersonForSend");
		deleteData("DELETE LCComPersonForSend a where grpcontno='99029288' and sendinnercode like '"
				+ SendInnerCode + "%'");

		LCComPersonForSendDB pLCComPersonForSendDB = new LCComPersonForSendDB(
				conn_Production);
		LCComPersonForSendSet pLCComPersonForSendSet = pLCComPersonForSendDB
				.executeQuery("select * from LCComPersonForSend a where grpcontno='99029288' and sendinnercode like '"
						+ SendInnerCode + "%'");
		index = pLCComPersonForSendSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCComPersonForSendSet sSet = new LCComPersonForSendSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCComPersonForSendSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCComPersonForSendSet();
				}
			}
		} else {
			writeLog("noData", "LCComPersonForSend");
		}

		writeLog("out", "LCComPersonForOrgan");
		deleteData("DELETE LCComPersonForOrgan a where grpcontno='99029288' and sendinnercode like '"
				+ SendInnerCode + "%'");

		LCComPersonForOrganDB pLCComPersonForOrganDB = new LCComPersonForOrganDB(
				conn_Production);
		LCComPersonForOrganSet pLCComPersonForOrganSet = pLCComPersonForOrganDB
				.executeQuery("select * from LCComPersonForOrgan a where grpcontno='99029288' and sendinnercode like '"
						+ SendInnerCode + "%'");
		index = pLCComPersonForOrganSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCComPersonForOrganSet sSet = new LCComPersonForOrganSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCComPersonForOrganSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCComPersonForOrganSet();
				}
			}
		} else {
			writeLog("noData", "LCComPersonForOrgan");
		}

		writeLog("out", "LCTable6Data");
		deleteData("DELETE LCTable6Data a where sendinnercode like '"
				+ SendInnerCode + "%'");

		LCTable6DataDB pLCTable6DataDB = new LCTable6DataDB(conn_Production);
		LCTable6DataSet pLCTable6DataSet = pLCTable6DataDB
				.executeQuery("select * from LCTable6Data a where sendinnercode like '"
						+ SendInnerCode + "%'");
		index = pLCTable6DataSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCTable6DataSet sSet = new LCTable6DataSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCTable6DataSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCTable6DataSet();
				}
			}
		} else {
			writeLog("noData", "LCTable6Data");
		}

//		writeLog("out", "LCAnnualGet");
//		deleteData("DELETE LCAnnualGet a where grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
//				+ SendInnerCode + "%')");
//
//		LCAnnualGetDB pLCAnnualGetDB = new LCAnnualGetDB(conn_Production);
//		for (int a = 2013; a >= 2004; a--) {
//			LCAnnualGetSet pLCAnnualGetSet = pLCAnnualGetDB
//					.executeQuery("select * from LCAnnualGet a where a.grpcontno='99029288' and a.actugetmonth like '"+a+"%' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
//				+ SendInnerCode + "%')");
//			index = pLCAnnualGetSet.size();
//			writeLog("out", a + "年共" + index + "条记录");
//			if (index > 0) {
//				LCAnnualGetSet sSet = new LCAnnualGetSet();
//				for (int i = 1; i <= index; i++) {
//					sSet.add(pLCAnnualGetSet.get(i));
//					if (i % submitNumber == 0 || i == index) {
//						writeLog("out", i + "条记录,提交");
//						aMMap.put(sSet, "INSERT");
//						pubSubmit();
//						sSet = new LCAnnualGetSet();
//					}
//				}
//			}
//		}
//
//		writeLog("out", "LCAnnualGetSub");
//		deleteData("DELETE LCAnnualGetSub a where grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
//				+ SendInnerCode + "%')");
//
//		SSRS aSSRS = tExeSQL_Pro
//				.execSQL("select insuredno from lcinsured where grpcontno='99029288' and sendinnercode like '"
//						+ SendInnerCode + "%'");
//		writeLog("out", "共" + aSSRS.getMaxRow() + "人");
//		LCAnnualGetSubDB pLCAnnualGetSubDB = new LCAnnualGetSubDB(
//				conn_Production);
//		for (int a = 1; a <= aSSRS.getMaxRow(); a++) {
//			LCAnnualGetSubSet pLCAnnualGetSubSet = pLCAnnualGetSubDB
//					.executeQuery("select * from LCAnnualGetSub a where grpcontno='99029288' and insuredno='"
//							+ aSSRS.GetText(a, 1) + "'");
//			index = pLCAnnualGetSubSet.size();
//			if (index > 0) {
//				LCAnnualGetSubSet sSet = new LCAnnualGetSubSet();
//				for (int i = 1; i <= index; i++) {
//					sSet.add(pLCAnnualGetSubSet.get(i));
//				}
//				aMMap.put(sSet, "INSERT");
//				pubSubmit();
//			}
//			if (a % 100 == 0) {// 100人提交一次
//				pubSubmit();
//			}
//		}

	}

	public void getEdorData() {
		int index = 0;
		writeLog("out", "LPEdorItem");
		deleteData("DELETE LPEdorItem a where exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LPEdorItemDB pLPEdorItemDB = new LPEdorItemDB(conn_Production);
		LPEdorItemSet pLPEdorItemSet = pLPEdorItemDB
				.executeQuery("select * from LPEdorItem a where exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLPEdorItemSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPEdorItemSet sSet = new LPEdorItemSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPEdorItemSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPEdorItemSet();
				}
			}
		} else {
			writeLog("noData", "LPEdorItem");
		}

		writeLog("out", "LPEdorApp");
		deleteData("DELETE LPEdorApp a where exists(select 'x' from lpedoritem b,lcinsured c where b.grpcontno='99029288' and b.edorno=a.edoracceptno and b.insuredno=c.insuredno and b.contno=c.contno and c.sendinnercode like '"
				+ SendInnerCode + "%')");
		LPEdorAppDB pLPEdorAppDB = new LPEdorAppDB(conn_Production);
		LPEdorAppSet pLPEdorAppSet = pLPEdorAppDB
				.executeQuery("select * from LPEdorApp a where exists(select 'x' from lpedoritem b,lcinsured c where b.grpcontno='99029288' and b.edorno=a.edoracceptno and b.insuredno=c.insuredno and b.contno=c.contno and c.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLPEdorAppSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPEdorAppSet sSet = new LPEdorAppSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPEdorAppSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPEdorAppSet();
				}
			}
		} else {
			writeLog("noData", "LPEdorApp");
		}

		writeLog("out", "LPGrpEdorMain");
		deleteData("DELETE LPGrpEdorMain a where exists(select 'x' from lpedoritem b,lcinsured c where b.grpcontno='99029288' and b.edorno=a.edorno and b.insuredno=c.insuredno and b.contno=c.contno and c.sendinnercode like '"
				+ SendInnerCode + "%')");
		LPGrpEdorMainDB pLPGrpEdorMainDB = new LPGrpEdorMainDB(conn_Production);
		LPGrpEdorMainSet pLPGrpEdorMainSet = pLPGrpEdorMainDB
				.executeQuery("select * from LPGrpEdorMain a where exists(select 'x' from lpedoritem b,lcinsured c where b.grpcontno='99029288' and b.edorno=a.edorno and b.insuredno=c.insuredno and b.contno=c.contno and c.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLPGrpEdorMainSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPGrpEdorMainSet sSet = new LPGrpEdorMainSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPGrpEdorMainSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPGrpEdorMainSet();
				}
			}
		} else {
			writeLog("noData", "LPGrpEdorMain");
		}

		writeLog("out", "LPGrpEdorItem");
		deleteData("DELETE LPGrpEdorItem a where exists(select 'x' from lpedoritem b,lcinsured c where b.grpcontno='99029288' and b.edorno=a.edorno and b.insuredno=c.insuredno and b.contno=c.contno and c.sendinnercode like '"
				+ SendInnerCode + "%')");
		LPGrpEdorItemDB pLPGrpEdorItemDB = new LPGrpEdorItemDB(conn_Production);
		LPGrpEdorItemSet pLPGrpEdorItemSet = pLPGrpEdorItemDB
				.executeQuery("select * from LPGrpEdorItem a where exists(select 'x' from lpedoritem b,lcinsured c where b.grpcontno='99029288' and b.edorno=a.edorno and b.insuredno=c.insuredno and b.contno=c.contno and c.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLPGrpEdorItemSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPGrpEdorItemSet sSet = new LPGrpEdorItemSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPGrpEdorItemSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPGrpEdorItemSet();
				}
			}
		} else {
			writeLog("noData", "LPGrpEdorItem");
		}

		writeLog("out", "LPEdorMain");
		deleteData("DELETE LPEdorMain a where exists(select 'x' from lpedoritem b,lcinsured c where b.grpcontno='99029288' and b.edorno=a.edorno and b.insuredno=c.insuredno and b.contno=c.contno and c.sendinnercode like '"
				+ SendInnerCode + "%')");
		LPEdorMainDB pLPEdorMainDB = new LPEdorMainDB(conn_Production);
		LPEdorMainSet pLPEdorMainSet = pLPEdorMainDB
				.executeQuery("select * from LPEdorMain a where exists(select 'x' from lpedoritem b,lcinsured c where b.grpcontno='99029288' and b.edorno=a.edorno and b.insuredno=c.insuredno and b.contno=c.contno and c.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLPEdorMainSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPEdorMainSet sSet = new LPEdorMainSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPEdorMainSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPEdorMainSet();
				}
			}
		} else {
			writeLog("noData", "LPEdorMain");
		}

		writeLog("out", "LPEdorAnnualConfig");
		deleteData("DELETE LPEdorAnnualConfig a where exists(select 'x' from lpedoritem b,lcinsured c where b.grpcontno='99029288' and b.edorno=a.edorno and b.insuredno=c.insuredno and b.contno=c.contno and c.sendinnercode like '"
				+ SendInnerCode + "%')");
		LPEdorAnnualConfigDB pLPEdorAnnualConfigDB = new LPEdorAnnualConfigDB(
				conn_Production);
		LPEdorAnnualConfigSet pLPEdorAnnualConfigSet = pLPEdorAnnualConfigDB
				.executeQuery("select * from LPEdorAnnualConfig a where exists(select 'x' from lpedoritem b,lcinsured c where b.grpcontno='99029288' and b.edorno=a.edorno and b.insuredno=c.insuredno and b.contno=c.contno and c.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLPEdorAnnualConfigSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPEdorAnnualConfigSet sSet = new LPEdorAnnualConfigSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPEdorAnnualConfigSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPEdorAnnualConfigSet();
				}
			}
		} else {
			writeLog("noData", "LPEdorAnnualConfig");
		}

		writeLog("out", "LCAnnualEdorInfo");
		deleteData("DELETE LCAnnualEdorInfo a where exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LCAnnualEdorInfoDB pLCAnnualEdorInfoDB = new LCAnnualEdorInfoDB(
				conn_Production);
		LCAnnualEdorInfoSet pLCAnnualEdorInfoSet = pLCAnnualEdorInfoDB
				.executeQuery("select * from LCAnnualEdorInfo a where exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLCAnnualEdorInfoSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCAnnualEdorInfoSet sSet = new LCAnnualEdorInfoSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCAnnualEdorInfoSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCAnnualEdorInfoSet();
				}
			}
		} else {
			writeLog("noData", "LCAnnualEdorInfo");
		}

		writeLog("out", "LCAnnualIPInfo");
		deleteData("DELETE LCAnnualIPInfo a where exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LCAnnualIPInfoDB pLCAnnualIPInfoDB = new LCAnnualIPInfoDB(
				conn_Production);
		LCAnnualIPInfoSet pLCAnnualIPInfoSet = pLCAnnualIPInfoDB
				.executeQuery("select * from LCAnnualIPInfo a where exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLCAnnualIPInfoSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCAnnualIPInfoSet sSet = new LCAnnualIPInfoSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCAnnualIPInfoSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCAnnualIPInfoSet();
				}
			}
		} else {
			writeLog("noData", "LCAnnualIPInfo");
		}

		writeLog("out", "LPApproveSub");
		deleteData("DELETE LPApproveSub a where exists(select 'x' from lpedoritem b,lcinsured c where b.grpcontno='99029288' and b.edorno=a.edorno and b.insuredno=c.insuredno and c.sendinnercode like '"
				+ SendInnerCode + "%')");
		LPApproveSubDB pLPApproveSubDB = new LPApproveSubDB(conn_Production);
		LPApproveSubSet pLPApproveSubSet = pLPApproveSubDB
				.executeQuery("select * from LPApproveSub a where exists(select 'x' from lpedoritem b,lcinsured c where b.grpcontno='99029288' and b.edorno=a.edorno and b.insuredno=c.insuredno and c.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLPApproveSubSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPApproveSubSet sSet = new LPApproveSubSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPApproveSubSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPApproveSubSet();
				}
			}
		} else {
			writeLog("noData", "LPApproveSub");
		}

		writeLog("out", "LPApproveInfo");
		deleteData("DELETE LPApproveInfo a where exists(select 'x' from lpedoritem b,lcinsured c where b.grpcontno='99029288' and b.edorno=a.edorno and b.insuredno=c.insuredno and c.sendinnercode like '"
				+ SendInnerCode + "%')");
		LPApproveInfoDB pLPApproveInfoDB = new LPApproveInfoDB(conn_Production);
		LPApproveInfoSet pLPApproveInfoSet = pLPApproveInfoDB
				.executeQuery("select * from LPApproveInfo a where exists(select 'x' from lpedoritem b,lcinsured c where b.grpcontno='99029288' and b.edorno=a.edorno and b.insuredno=c.insuredno and c.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLPApproveInfoSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPApproveInfoSet sSet = new LPApproveInfoSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPApproveInfoSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPApproveInfoSet();
				}
			}
		} else {
			writeLog("noData", "LPApproveInfo");
		}

		writeLog("out", "LDBank");
		deleteData("DELETE LDBank a where comcode in (select organcomcode from lcsendorgan b where b.grpcontno='99029288' and b.organinnercode like '"
				+ SendInnerCode + "%')");
		LDBankDB pLDBankDB = new LDBankDB(conn_Production);
		LDBankSet pLDBankSet = pLDBankDB
				.executeQuery("select * from LDBank a where comcode in (select organcomcode from lcsendorgan b where b.grpcontno='99029288' and b.organinnercode like '"
						+ SendInnerCode + "%')");
		index = pLDBankSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LDBankSet sSet = new LDBankSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLDBankSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LDBankSet();
				}
			}
		} else {
			writeLog("noData", "LDBank");
		}
	}

	public void getInsuredData() {
		int index = 0;
		writeLog("out", "LCInsured");
		deleteData("DELETE LCInsured where grpcontno='99029288' and sendinnercode like '"
				+ SendInnerCode + "%'");
		LCInsuredDB pLCInsuredDB = new LCInsuredDB(conn_Production);
		LCInsuredSet pLCInsuredSet = pLCInsuredDB
				.executeQuery("select * from LCInsured where grpcontno='99029288' and sendinnercode like '"
						+ SendInnerCode + "%'");
		index = pLCInsuredSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCInsuredSet sSet = new LCInsuredSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCInsuredSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCInsuredSet();
				}
			}
		} else {
			writeLog("noData", "LCInsured");
		}

		writeLog("out", "LPInsured");
		deleteData("DELETE LPInsured a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LPInsuredDB pLPInsuredDB = new LPInsuredDB(conn_Production);
		LPInsuredSet pLPInsuredSet = pLPInsuredDB
				.executeQuery("select * from LPInsured a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLPInsuredSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPInsuredSet sSet = new LPInsuredSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPInsuredSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPInsuredSet();
				}
			}
		} else {
			writeLog("noData", "LPInsured");
		}

		writeLog("out", "LBInsured");
		deleteData("DELETE LBInsured a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LBInsuredDB pLBInsuredDB = new LBInsuredDB(conn_Production);
		LBInsuredSet pLBInsuredSet = pLBInsuredDB
				.executeQuery("select * from LBInsured a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLBInsuredSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LBInsuredSet sSet = new LBInsuredSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLBInsuredSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LBInsuredSet();
				}
			}
		} else {
			writeLog("noData", "LBInsured");
		}

		writeLog("out", "LOBInsured");
		deleteData("DELETE LOBInsured a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LOBInsuredDB pLOBInsuredDB = new LOBInsuredDB(conn_Production);
		LOBInsuredSet pLOBInsuredSet = pLOBInsuredDB
				.executeQuery("select * from LOBInsured a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLOBInsuredSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LOBInsuredSet sSet = new LOBInsuredSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLOBInsuredSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LOBInsuredSet();
				}
			}
		} else {
			writeLog("noData", "LOBInsured");
		}

		writeLog("out", "LCPol");
		deleteData("DELETE LCPol a where a.grpcontno='99029288' and a.sendinnercode like '"
				+ SendInnerCode + "%'");
		LCPolDB pLCPolDB = new LCPolDB(conn_Production);
		LCPolSet pLCPolSet = pLCPolDB
				.executeQuery("select * from LCPol a where a.grpcontno='99029288' and a.sendinnercode like '"
						+ SendInnerCode + "%'");
		index = pLCPolSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCPolSet sSet = new LCPolSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCPolSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCPolSet();
				}
			}
		} else {
			writeLog("noData", "LCPol");
		}

		writeLog("out", "LPPol");
		deleteData("DELETE LPPol a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LPPolDB pLPPolDB = new LPPolDB(conn_Production);
		LPPolSet pLPPolSet = pLPPolDB
				.executeQuery("select * from LPPol a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLPPolSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPPolSet sSet = new LPPolSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPPolSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPPolSet();
				}
			}
		} else {
			writeLog("noData", "LPPol");
		}

		writeLog("out", "LBPol");
		deleteData("DELETE LBPol a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LBPolDB pLBPolDB = new LBPolDB(conn_Production);
		LBPolSet pLBPolSet = pLBPolDB
				.executeQuery("select * from LBPol a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLBPolSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LBPolSet sSet = new LBPolSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLBPolSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LBPolSet();
				}
			}
		} else {
			writeLog("noData", "LBPol");
		}

		writeLog("out", "LOBPol");
		deleteData("DELETE LOBPol a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LOBPolDB pLOBPolDB = new LOBPolDB(conn_Production);
		LOBPolSet pLOBPolSet = pLOBPolDB
				.executeQuery("select * from LOBPol a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLOBPolSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LOBPolSet sSet = new LOBPolSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLOBPolSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LOBPolSet();
				}
			}
		} else {
			writeLog("noData", "LOBPol");
		}

		writeLog("out", "LDPerson");
		deleteData("DELETE LDPerson a where exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.customerno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LDPersonDB pLDPersonDB = new LDPersonDB(conn_Production);
		LDPersonSet pLDPersonSet = pLDPersonDB
				.executeQuery("select * from LDPerson a where exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.customerno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLDPersonSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LDPersonSet sSet = new LDPersonSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLDPersonSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LDPersonSet();
				}
			}
		} else {
			writeLog("noData", "LDPerson");
		}

		writeLog("out", "LPPerson");
		deleteData("DELETE LPPerson a where exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.customerno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LPPersonDB pLPPersonDB = new LPPersonDB(conn_Production);
		LPPersonSet pLPPersonSet = pLPPersonDB
				.executeQuery("select * from LPPerson a where exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.customerno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLPPersonSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPPersonSet sSet = new LPPersonSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPPersonSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPPersonSet();
				}
			}
		} else {
			writeLog("noData", "LPPerson");
		}

		writeLog("out", "LBPerson");
		deleteData("DELETE LBPerson a where exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.customerno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LBPersonDB pLBPersonDB = new LBPersonDB(conn_Production);
		LBPersonSet pLBPersonSet = pLBPersonDB
				.executeQuery("select * from LBPerson a where exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.customerno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLBPersonSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LBPersonSet sSet = new LBPersonSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLBPersonSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LBPersonSet();
				}
			}
		} else {
			writeLog("noData", "LBPerson");
		}

		writeLog("out", "LCCont");
		deleteData("DELETE LCCont a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LCContDB pLCContDB = new LCContDB(conn_Production);
		LCContSet pLCContSet = pLCContDB
				.executeQuery("select * from LCCont a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLCContSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCContSet sSet = new LCContSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCContSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCContSet();
				}
			}
		} else {
			writeLog("noData", "LCCont");
		}

		writeLog("out", "LPCont");
		deleteData("DELETE LPCont a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LPContDB pLPContDB = new LPContDB(conn_Production);
		LPContSet pLPContSet = pLPContDB
				.executeQuery("select * from LPCont a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLPContSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPContSet sSet = new LPContSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPContSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPContSet();
				}
			}
		} else {
			writeLog("noData", "LPCont");
		}

		writeLog("out", "LBCont");
		deleteData("DELETE LBCont a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LBContDB pLBContDB = new LBContDB(conn_Production);
		LBContSet pLBContSet = pLBContDB
				.executeQuery("select * from LBCont a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLBContSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LBContSet sSet = new LBContSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLBContSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LBContSet();
				}
			}
		} else {
			writeLog("noData", "LBCont");
		}

		writeLog("out", "LOBCont");
		deleteData("DELETE LOBCont a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LOBContDB pLOBContDB = new LOBContDB(conn_Production);
		LOBContSet pLOBContSet = pLOBContDB
				.executeQuery("select * from LOBCont a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLOBContSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LOBContSet sSet = new LOBContSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLOBContSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LOBContSet();
				}
			}
		} else {
			writeLog("noData", "LOBCont");
		}

		writeLog("out", "LCDuty");
		deleteData("DELETE LCDuty a where exists(select 'x' from lcpol b where b.grpcontno='99029288' and b.contno=a.contno and b.polno=a.polno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LCDutyDB pLCDutyDB = new LCDutyDB(conn_Production);
		LCDutySet pLCDutySet = pLCDutyDB
				.executeQuery("select * from LCDuty a where exists(select 'x' from lcpol b where b.grpcontno='99029288' and b.contno=a.contno and b.polno=a.polno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLCDutySet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCDutySet sSet = new LCDutySet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCDutySet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCDutySet();
				}
			}
		} else {
			writeLog("noData", "LCDuty");
		}

		writeLog("out", "LPDuty");
		deleteData("DELETE LPDuty a where exists(select 'x' from lcpol b where b.grpcontno='99029288' and b.contno=a.contno and b.polno=a.polno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LPDutyDB pLPDutyDB = new LPDutyDB(conn_Production);
		LPDutySet pLPDutySet = pLPDutyDB
				.executeQuery("select * from LPDuty a where exists(select 'x' from lcpol b where b.grpcontno='99029288' and b.contno=a.contno and b.polno=a.polno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLPDutySet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPDutySet sSet = new LPDutySet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPDutySet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPDutySet();
				}
			}
		} else {
			writeLog("noData", "LPDuty");
		}

		writeLog("out", "LBDuty");
		deleteData("DELETE LBDuty a where exists(select 'x' from lcpol b where b.grpcontno='99029288' and b.contno=a.contno and b.polno=a.polno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LBDutyDB pLBDutyDB = new LBDutyDB(conn_Production);
		LBDutySet pLBDutySet = pLBDutyDB
				.executeQuery("select * from LBDuty a where exists(select 'x' from lcpol b where b.grpcontno='99029288' and b.contno=a.contno and b.polno=a.polno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLBDutySet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LBDutySet sSet = new LBDutySet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLBDutySet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LBDutySet();
				}
			}
		} else {
			writeLog("noData", "LBDuty");
		}

		writeLog("out", "LOBDuty");
		deleteData("DELETE LOBDuty a where exists(select 'x' from lcpol b where b.grpcontno='99029288' and b.contno=a.contno and b.polno=a.polno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LOBDutyDB pLOBDutyDB = new LOBDutyDB(conn_Production);
		LOBDutySet pLOBDutySet = pLOBDutyDB
				.executeQuery("select * from LOBDuty a where exists(select 'x' from lcpol b where b.grpcontno='99029288' and b.contno=a.contno and b.polno=a.polno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLOBDutySet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LOBDutySet sSet = new LOBDutySet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLOBDutySet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LOBDutySet();
				}
			}
		} else {
			writeLog("noData", "LOBDuty");
		}

		writeLog("out", "LCPrem");
		deleteData("DELETE LCPrem a where exists(select 'x' from lcpol b where b.grpcontno='99029288' and b.contno=a.contno and b.polno=a.polno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LCPremDB pLCPremDB = new LCPremDB(conn_Production);
		LCPremSet pLCPremSet = pLCPremDB
				.executeQuery("select * from LCPrem a where exists(select 'x' from lcpol b where b.grpcontno='99029288' and b.contno=a.contno and b.polno=a.polno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLCPremSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCPremSet sSet = new LCPremSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCPremSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCPremSet();
				}
			}
		} else {
			writeLog("noData", "LCPrem");
		}

		writeLog("out", "LPPrem");
		deleteData("DELETE LPPrem a where exists(select 'x' from lcpol b where b.grpcontno='99029288' and b.contno=a.contno and b.polno=a.polno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LPPremDB pLPPremDB = new LPPremDB(conn_Production);
		LPPremSet pLPPremSet = pLPPremDB
				.executeQuery("select * from LPPrem a where exists(select 'x' from lcpol b where b.grpcontno='99029288' and b.contno=a.contno and b.polno=a.polno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLPPremSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPPremSet sSet = new LPPremSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPPremSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPPremSet();
				}
			}
		} else {
			writeLog("noData", "LPPrem");
		}

		writeLog("out", "LBPrem");
		deleteData("DELETE LBPrem a where exists(select 'x' from lcpol b where b.grpcontno='99029288' and b.contno=a.contno and b.polno=a.polno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LBPremDB pLBPremDB = new LBPremDB(conn_Production);
		LBPremSet pLBPremSet = pLBPremDB
				.executeQuery("select * from LBPrem a where exists(select 'x' from lcpol b where b.grpcontno='99029288' and b.contno=a.contno and b.polno=a.polno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLBPremSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LBPremSet sSet = new LBPremSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLBPremSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LBPremSet();
				}
			}
		} else {
			writeLog("noData", "LBPrem");
		}

		writeLog("out", "LOBPrem");
		deleteData("DELETE LOBPrem a where exists(select 'x' from lcpol b where b.grpcontno='99029288' and b.contno=a.contno and b.polno=a.polno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LOBPremDB pLOBPremDB = new LOBPremDB(conn_Production);
		LOBPremSet pLOBPremSet = pLOBPremDB
				.executeQuery("select * from LOBPrem a where exists(select 'x' from lcpol b where b.grpcontno='99029288' and b.contno=a.contno and b.polno=a.polno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLOBPremSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LOBPremSet sSet = new LOBPremSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLOBPremSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LOBPremSet();
				}
			}
		} else {
			writeLog("noData", "LOBPrem");
		}

		writeLog("out", "LCGet");
		deleteData("DELETE LCGet a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LCGetDB pLCGetDB = new LCGetDB(conn_Production);
		LCGetSet pLCGetSet = pLCGetDB
				.executeQuery("select * from LCGet a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLCGetSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCGetSet sSet = new LCGetSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCGetSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCGetSet();
				}
			}
		} else {
			writeLog("noData", "LCGet");
		}

		writeLog("out", "LPGet");
		deleteData("DELETE LPGet a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LPGetDB pLPGetDB = new LPGetDB(conn_Production);
		LPGetSet pLPGetSet = pLPGetDB
				.executeQuery("select * from LPGet a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLPGetSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPGetSet sSet = new LPGetSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPGetSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPGetSet();
				}
			}
		} else {
			writeLog("noData", "LPGet");
		}

		writeLog("out", "LBGet");
		deleteData("DELETE LBGet a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LBGetDB pLBGetDB = new LBGetDB(conn_Production);
		LBGetSet pLBGetSet = pLBGetDB
				.executeQuery("select * from LBGet a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLBGetSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LBGetSet sSet = new LBGetSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLBGetSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LBGetSet();
				}
			}
		} else {
			writeLog("noData", "LBGet");
		}

		writeLog("out", "LOBGet");
		deleteData("DELETE LOBGet a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LOBGetDB pLOBGetDB = new LOBGetDB(conn_Production);
		LOBGetSet pLOBGetSet = pLOBGetDB
				.executeQuery("select * from LOBGet a where a.grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLOBGetSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LOBGetSet sSet = new LOBGetSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLOBGetSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LOBGetSet();
				}
			}
		} else {
			writeLog("noData", "LOBGet");
		}

		writeLog("out", "LCAddress");
		deleteData("DELETE LCAddress a where exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.customerno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LCAddressDB pLCAddressDB = new LCAddressDB(conn_Production);
		LCAddressSet pLCAddressSet = pLCAddressDB
				.executeQuery("select * from LCAddress a where exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.customerno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLCAddressSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCAddressSet sSet = new LCAddressSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCAddressSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCAddressSet();
				}
			}
		} else {
			writeLog("noData", "LCAddress");
		}

		writeLog("out", "LPAddress");
		deleteData("DELETE LPAddress a where exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.customerno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
		LPAddressDB pLPAddressDB = new LPAddressDB(conn_Production);
		LPAddressSet pLPAddressSet = pLPAddressDB
				.executeQuery("select * from LPAddress a where exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.customerno and b.sendinnercode like '"
						+ SendInnerCode + "%')");
		index = pLPAddressSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPAddressSet sSet = new LPAddressSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPAddressSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPAddressSet();
				}
			}
		} else {
			writeLog("noData", "LPAddress");
		}
	}

	public void getUserData() {
		int index = 0;
		writeLog("out", "LDUser");
		deleteData("DELETE LDUser where organinnercode like '"
				+ SendInnerCode
				+ "%' and insurerusertype='1' and usertype='3' and cropusertype='2'");
		LDUserDB pLDUserDB = new LDUserDB(conn_Production);
		LDUserSet pLDUserSet = pLDUserDB
				.executeQuery("select * from LDUser where organinnercode like '"
						+ SendInnerCode
						+ "%' and insurerusertype='1' and usertype='3' and cropusertype='2'");
		index = pLDUserSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LDUserSet sSet = new LDUserSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLDUserSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LDUserSet();
				}
			}
		} else {
			writeLog("noData", "LDUser");
		}

		writeLog("out", "LPUser");
		deleteData("DELETE LPUser where organinnercode like '"
				+ SendInnerCode
				+ "%' and insurerusertype='1' and usertype='3' and cropusertype='2'");
		LPUserDB pLPUserDB = new LPUserDB(conn_Production);
		LPUserSet pLPUserSet = pLPUserDB
				.executeQuery("select * from LPUser where organinnercode like '"
						+ SendInnerCode
						+ "%' and insurerusertype='1' and usertype='3' and cropusertype='2'");
		index = pLPUserSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPUserSet sSet = new LPUserSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPUserSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPUserSet();
				}
			}
		} else {
			writeLog("noData", "LPUser");
		}

		writeLog("out", "LCUserCont");
		deleteData("DELETE LCUserCont a where exists(select 'x' from lduser b where b.usercode=a.usercode and b.organinnercode like '"
				+ SendInnerCode
				+ "%' and b.insurerusertype='1' and b.usertype='3' and b.cropusertype='2')");
		LCUserContDB pLCUserContDB = new LCUserContDB(conn_Production);
		LCUserContSet pLCUserContSet = pLCUserContDB
				.executeQuery("select * from LCUserCont a where exists(select 'x' from lduser b where b.usercode=a.usercode and b.organinnercode like '"
						+ SendInnerCode
						+ "%' and b.insurerusertype='1' and b.usertype='3' and b.cropusertype='2')");
		index = pLCUserContSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCUserContSet sSet = new LCUserContSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCUserContSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCUserContSet();
				}
			}
		} else {
			writeLog("noData", "LCUserCont");
		}

		writeLog("out", "LCUserOrganRela");
		deleteData("DELETE LCUserOrganRela a where grpcontno='99029288' and exists(select 'x' from lduser b where b.usercode=a.usercode and b.organinnercode like '"
				+ SendInnerCode
				+ "%' and b.insurerusertype='1' and b.usertype='3' and b.cropusertype='2')");
		LCUserOrganRelaDB pLCUserOrganRelaDB = new LCUserOrganRelaDB(
				conn_Production);
		LCUserOrganRelaSet pLCUserOrganRelaSet = pLCUserOrganRelaDB
				.executeQuery("select * from LCUserOrganRela a where grpcontno='99029288' and exists(select 'x' from lduser b where b.usercode=a.usercode and b.organinnercode like '"
						+ SendInnerCode
						+ "%' and b.insurerusertype='1' and b.usertype='3' and b.cropusertype='2')");
		index = pLCUserOrganRelaSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCUserOrganRelaSet sSet = new LCUserOrganRelaSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCUserOrganRelaSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCUserOrganRelaSet();
				}
			}
		} else {
			writeLog("noData", "LCUserOrganRela");
		}

		writeLog("out", "LPUserOrganRela");
		deleteData("DELETE LPUserOrganRela a where grpcontno='99029288' and exists(select 'x' from lduser b where b.usercode=a.usercode and b.organinnercode like '"
				+ SendInnerCode
				+ "%' and b.insurerusertype='1' and b.usertype='3' and b.cropusertype='2')");
		LPUserOrganRelaDB pLPUserOrganRelaDB = new LPUserOrganRelaDB(
				conn_Production);
		LPUserOrganRelaSet pLPUserOrganRelaSet = pLPUserOrganRelaDB
				.executeQuery("select * from LPUserOrganRela a where grpcontno='99029288' and exists(select 'x' from lduser b where b.usercode=a.usercode and b.organinnercode like '"
						+ SendInnerCode
						+ "%' and b.insurerusertype='1' and b.usertype='3' and b.cropusertype='2')");
		index = pLPUserOrganRelaSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPUserOrganRelaSet sSet = new LPUserOrganRelaSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPUserOrganRelaSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPUserOrganRelaSet();
				}
			}
		} else {
			writeLog("noData", "LPUserOrganRela");
		}

		writeLog("out", "LDUserToMenuGrp");
		deleteData("DELETE LDUserToMenuGrp a where exists(select 'x' from lduser b where b.usercode=a.usercode and b.organinnercode like '"
				+ SendInnerCode
				+ "%' and b.insurerusertype='1' and b.usertype='3' and b.cropusertype='2')");
		LDUserTOMenuGrpDB pLDUserTOMenuGrpDB = new LDUserTOMenuGrpDB(
				conn_Production);
		LDUserTOMenuGrpSet pLDUserTOMenuGrpSet = pLDUserTOMenuGrpDB
				.executeQuery("select * from LDUserToMenuGrp a where exists(select 'x' from lduser b where b.usercode=a.usercode and b.organinnercode like '"
						+ SendInnerCode
						+ "%' and b.insurerusertype='1' and b.usertype='3' and b.cropusertype='2')");
		index = pLDUserTOMenuGrpSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LDUserTOMenuGrpSet sSet = new LDUserTOMenuGrpSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLDUserTOMenuGrpSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LDUserTOMenuGrpSet();
				}
			}
		} else {
			writeLog("noData", "LDUserToMenuGrp");
		}

		writeLog("out", "LPUserTOMenuGrp");
		deleteData("DELETE LPUserTOMenuGrp a where exists(select 'x' from lduser b where b.usercode=a.usercode and b.organinnercode like '"
				+ SendInnerCode
				+ "%' and b.insurerusertype='1' and b.usertype='3' and b.cropusertype='2')");
		LPUserTOMenuGrpDB pLPUserTOMenuGrpDB = new LPUserTOMenuGrpDB(
				conn_Production);
		LPUserTOMenuGrpSet pLPUserTOMenuGrpSet = pLPUserTOMenuGrpDB
				.executeQuery("select * from LPUserTOMenuGrp a where exists(select 'x' from lduser b where b.usercode=a.usercode and b.organinnercode like '"
						+ SendInnerCode
						+ "%' and b.insurerusertype='1' and b.usertype='3' and b.cropusertype='2')");
		index = pLPUserTOMenuGrpSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPUserTOMenuGrpSet sSet = new LPUserTOMenuGrpSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPUserTOMenuGrpSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPUserTOMenuGrpSet();
				}
			}
		} else {
			writeLog("noData", "LPUserTOMenuGrp");
		}

	}

	public void getUserPData() {
		int index = 0;
		// 公共部分只取保险公司和集团公司用户
		writeLog("out", "LDUser");
		deleteData("DELETE LDUser where usercode='001' or (insurerusertype='1' and (usertype in ('1','2') or (usertype='3' and cropusertype='1')))");

		LDUserDB pLDUserDB = new LDUserDB(conn_Production);
		LDUserSet pLDUserSet = pLDUserDB
				.executeQuery("select * from LDUser where usercode='001' or (insurerusertype='1' and (usertype in ('1','2') or (usertype='3' and cropusertype='1')))");
		index = pLDUserSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LDUserSet sSet = new LDUserSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLDUserSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LDUserSet();
				}
			}
		} else {
			writeLog("noData", "LDUser");
		}

		writeLog("out", "LPUser");
		deleteData("DELETE LPUser where usercode='001' or (insurerusertype='1' and (usertype in ('1','2') or (usertype='3' and cropusertype='1')))");

		LPUserDB pLPUserDB = new LPUserDB(conn_Production);
		LPUserSet pLPUserSet = pLPUserDB
				.executeQuery("select * from LPUser where usercode='001' or (insurerusertype='1' and (usertype in ('1','2') or (usertype='3' and cropusertype='1')))");
		index = pLPUserSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPUserSet sSet = new LPUserSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPUserSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPUserSet();
				}
			}
		} else {
			writeLog("noData", "LPUser");
		}

		writeLog("out", "LCUserCont");
		deleteData("DELETE LCUserCont a where exists(select 'x' from lduser b where b.usercode=a.usercode and (b.usercode='001' or (b.insurerusertype='1' and (b.usertype in ('1','2') or (b.usertype='3' and b.cropusertype='1')))))");

		LCUserContDB pLCUserContDB = new LCUserContDB(conn_Production);
		LCUserContSet pLCUserContSet = pLCUserContDB
				.executeQuery("select * from LCUserCont a where exists(select 'x' from lduser b where b.usercode=a.usercode and (b.usercode='001' or (b.insurerusertype='1' and (b.usertype in ('1','2') or (b.usertype='3' and b.cropusertype='1')))))");
		index = pLCUserContSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCUserContSet sSet = new LCUserContSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCUserContSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCUserContSet();
				}
			}
		} else {
			writeLog("noData", "LCUserCont");
		}

		writeLog("out", "LCUserOrganRela");
		deleteData("DELETE LCUserOrganRela a where grpcontno='99029288' and exists(select 'x' from lduser b where b.usercode=a.usercode and (b.usercode='001' or (b.insurerusertype='1' and (b.usertype in ('1','2') or (b.usertype='3' and b.cropusertype='1')))))");

		LCUserOrganRelaDB pLCUserOrganRelaDB = new LCUserOrganRelaDB(
				conn_Production);
		LCUserOrganRelaSet pLCUserOrganRelaSet = pLCUserOrganRelaDB
				.executeQuery("select * from LCUserOrganRela a where grpcontno='99029288' and exists(select 'x' from lduser b where b.usercode=a.usercode and (b.usercode='001' or (b.insurerusertype='1' and (b.usertype in ('1','2') or (b.usertype='3' and b.cropusertype='1')))))");
		index = pLCUserOrganRelaSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCUserOrganRelaSet sSet = new LCUserOrganRelaSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCUserOrganRelaSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCUserOrganRelaSet();
				}
			}
		} else {
			writeLog("noData", "LCUserOrganRela");
		}

		writeLog("out", "LPUserOrganRela");
		deleteData("DELETE LPUserOrganRela a where grpcontno='99029288' and exists(select 'x' from lduser b where b.usercode=a.usercode and (b.usercode='001' or (b.insurerusertype='1' and (b.usertype in ('1','2') or (b.usertype='3' and b.cropusertype='1')))))");

		LPUserOrganRelaDB pLPUserOrganRelaDB = new LPUserOrganRelaDB(
				conn_Production);
		LPUserOrganRelaSet pLPUserOrganRelaSet = pLPUserOrganRelaDB
				.executeQuery("select * from LPUserOrganRela a where grpcontno='99029288' and exists(select 'x' from lduser b where b.usercode=a.usercode and (b.usercode='001' or (b.insurerusertype='1' and (b.usertype in ('1','2') or (b.usertype='3' and b.cropusertype='1')))))");
		index = pLPUserOrganRelaSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPUserOrganRelaSet sSet = new LPUserOrganRelaSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPUserOrganRelaSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPUserOrganRelaSet();
				}
			}
		} else {
			writeLog("noData", "LPUserOrganRela");
		}

		writeLog("out", "LDUserToMenuGrp");
		deleteData("DELETE LDUserToMenuGrp a where exists(select 'x' from lduser b where b.usercode=a.usercode and (b.usercode='001' or (b.insurerusertype='1' and (b.usertype in ('1','2') or (b.usertype='3' and b.cropusertype='1')))))");

		LDUserTOMenuGrpDB pLDUserTOMenuGrpDB = new LDUserTOMenuGrpDB(
				conn_Production);
		LDUserTOMenuGrpSet pLDUserTOMenuGrpSet = pLDUserTOMenuGrpDB
				.executeQuery("select * from LDUserToMenuGrp a where exists(select 'x' from lduser b where b.usercode=a.usercode and (b.usercode='001' or (b.insurerusertype='1' and (b.usertype in ('1','2') or (b.usertype='3' and b.cropusertype='1')))))");
		index = pLDUserTOMenuGrpSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LDUserTOMenuGrpSet sSet = new LDUserTOMenuGrpSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLDUserTOMenuGrpSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LDUserTOMenuGrpSet();
				}
			}
		} else {
			writeLog("noData", "LDUserToMenuGrp");
		}

		writeLog("out", "LPUserTOMenuGrp");
		deleteData("DELETE LPUserTOMenuGrp a where exists(select 'x' from lduser b where b.usercode=a.usercode and (b.usercode='001' or (b.insurerusertype='1' and (b.usertype in ('1','2') or (b.usertype='3' and b.cropusertype='1')))))");

		LPUserTOMenuGrpDB pLPUserTOMenuGrpDB = new LPUserTOMenuGrpDB(
				conn_Production);
		LPUserTOMenuGrpSet pLPUserTOMenuGrpSet = pLPUserTOMenuGrpDB
				.executeQuery("select * from LPUserTOMenuGrp a where exists(select 'x' from lduser b where b.usercode=a.usercode and (b.usercode='001' or (b.insurerusertype='1' and (b.usertype in ('1','2') or (b.usertype='3' and b.cropusertype='1')))))");
		index = pLPUserTOMenuGrpSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPUserTOMenuGrpSet sSet = new LPUserTOMenuGrpSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPUserTOMenuGrpSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPUserTOMenuGrpSet();
				}
			}
		} else {
			writeLog("noData", "LPUserTOMenuGrp");
		}
	}

	public void getSendOrganData() {
		int index = 0;

		writeLog("out", "LCSendOrgan");
		deleteData("DELETE LCSendOrgan where organinnercode like '"
				+ SendInnerCode + "%'");

		LCSendOrganDB pLCSendOrganDB = new LCSendOrganDB(conn_Production);
		LCSendOrganSet pLCSendOrganSet = pLCSendOrganDB
				.executeQuery("select GRPCONTNO, PRTNO, ORGANCOMCODE, ORGANINNERCODE, POSTALADDRESS, ZIPCODE, GRPNAME, MANAGECOM, UPINNERCODE, UPCOMCODE, GROUPLEVEL, ORGANTYPE, ADDRESSNO, LINKMAN, LINKMANMAIL, FAX, '', FOUNDDATE, BANKCODE, '', ACCNAME, SENDFLAG, STATE, OPERATOR, MAKEDATE, MAKETIME, MODIFYDATE, MODIFYTIME, GETMODE, USE, CHILDFLAG, BQFLAG, ANNUALBANKNAME, INTERVALDV, ACCNOFLAG from LCSendOrgan where grpcontno='99029288' and organinnercode like '"
						+ SendInnerCode + "%'");
		index = pLCSendOrganSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCSendOrganSet sSet = new LCSendOrganSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCSendOrganSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCSendOrganSet();
				}
			}
		} else {
			writeLog("noData", "LCSendOrgan");
		}

		writeLog("out", "LTSendOrgan");
		deleteData("DELETE LTSendOrgan where organinnercode like '"
				+ SendInnerCode + "%'");

		LTSendOrganDB pLTSendOrganDB = new LTSendOrganDB(conn_Production);
		LTSendOrganSet pLTSendOrganSet = pLTSendOrganDB
				.executeQuery("select CALMONTH, GRPCONTNO, PRTNO, ORGANCOMCODE, ORGANINNERCODE, POSTALADDRESS, ZIPCODE, GRPNAME, MANAGECOM, UPINNERCODE, UPCOMCODE, GROUPLEVEL, ORGANTYPE, ADDRESSNO, LINKMAN, LINKMANMAIL, '', '', FOUNDDATE, BANKCODE, '', ACCNAME, SENDFLAG, STATE, OPERATOR, MAKEDATE, MAKETIME, MODIFYDATE, MODIFYTIME, GETMODE, USE, CHILDFLAG, BQFLAG, ANNUALBANKNAME, INTERVALDV, ACCNOFLAG from LTSendOrgan where grpcontno='99029288' and organinnercode like '"
						+ SendInnerCode + "%'");
		index = pLTSendOrganSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LTSendOrganSet sSet = new LTSendOrganSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLTSendOrganSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LTSendOrganSet();
				}
			}
		} else {
			writeLog("noData", "LTSendOrgan");
		}

		writeLog("out", "LPSendOrgan");
		deleteData("DELETE LPSendOrgan where organinnercode like '"
				+ SendInnerCode + "%'");

		LPSendOrganDB pLPSendOrganDB = new LPSendOrganDB(conn_Production);
		LPSendOrganSet pLPSendOrganSet = pLPSendOrganDB
				.executeQuery("select EDORNO, EDORTYPE, GRPCONTNO, PRTNO, ORGANCOMCODE, ORGANINNERCODE, POSTALADDRESS, ZIPCODE, GRPNAME, MANAGECOM, UPINNERCODE, UPCOMCODE, GROUPLEVEL, ORGANTYPE, ADDRESSNO, LINKMAN, LINKMANMAIL, FAX, '', FOUNDDATE, BANKCODE, '', ACCNAME, SENDFLAG, STATE, OPERATOR, MAKEDATE, MAKETIME, MODIFYDATE, MODIFYTIME, GETMODE, USE, CHILDFLAG, BQFLAG, ANNUALBANKNAME, INTERVALDV, ACCNOFLAG from LPSendOrgan where grpcontno='99029288' and organinnercode like '"
						+ SendInnerCode + "%'");
		index = pLPSendOrganSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPSendOrganSet sSet = new LPSendOrganSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPSendOrganSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPSendOrganSet();
				}
			}
		} else {
			writeLog("noData", "LPSendOrgan");
		}

		writeLog("out", "LBSendOrgan");
		deleteData("DELETE LBSendOrgan where organinnercode like '"
				+ SendInnerCode + "%'");

		LBSendOrganDB pLBSendOrganDB = new LBSendOrganDB(conn_Production);
		LBSendOrganSet pLBSendOrganSet = pLBSendOrganDB
				.executeQuery("select EDORNO, GRPCONTNO, PRTNO, ORGANCOMCODE, ORGANINNERCODE, '', ZIPCODE, GRPNAME, MANAGECOM, UPINNERCODE, UPCOMCODE, GROUPLEVEL, ORGANTYPE, ADDRESSNO, LINKMAN, '', FAX, '', FOUNDDATE, BANKCODE, '', ACCNAME, SENDFLAG, STATE, OPERATOR, MAKEDATE, MAKETIME, MODIFYDATE, MODIFYTIME, GETMODE, USE, CHILDFLAG, BQFLAG, ANNUALBANKNAME, INTERVALDV, ACCNOFLAG from LBSendOrgan where grpcontno='99029288' and organinnercode like '"
						+ SendInnerCode + "%'");
		index = pLBSendOrganSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LBSendOrganSet sSet = new LBSendOrganSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLBSendOrganSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LBSendOrganSet();
				}
			}
		} else {
			writeLog("noData", "LBSendOrgan");
		}

		writeLog("out", "LOBSendOrgan");
		deleteData("DELETE LOBSendOrgan where organinnercode like '"
				+ SendInnerCode + "%'");

		LOBSendOrganDB pLOBSendOrganDB = new LOBSendOrganDB(conn_Production);
		LOBSendOrganSet pLOBSendOrganSet = pLOBSendOrganDB
				.executeQuery("select GRPCONTNO, PRTNO, ORGANCOMCODE, ORGANINNERCODE, POSTALADDRESS, ZIPCODE, GRPNAME, MANAGECOM, UPINNERCODE, UPCOMCODE, GROUPLEVEL, ORGANTYPE, ADDRESSNO, LINKMAN, LINKMANMAIL, FAX, PHONE, FOUNDDATE, BANKCODE, BANKACCNO, ACCNAME, SENDFLAG, STATE, OPERATOR, MAKEDATE, MAKETIME, MODIFYDATE, MODIFYTIME, GETMODE, USE, CHILDFLAG, BQFLAG, ANNUALBANKNAME, INTERVALDV, ACCNOFLAG from LOBSendOrgan where grpcontno='99029288' and organinnercode like '"
						+ SendInnerCode + "%'");
		index = pLOBSendOrganSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LOBSendOrganSet sSet = new LOBSendOrganSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLOBSendOrganSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LOBSendOrganSet();
				}
			}
		} else {
			writeLog("noData", "LOBSendOrgan");
		}
	}

	public void getSendOrganPData() {
		int index = 0;

		// 发放部门:公共部分只取中石油
		writeLog("out", "LCSendOrgan");
		deleteData("DELETE LCSendOrgan where organinnercode='86'");

		LCSendOrganDB pLCSendOrganDB = new LCSendOrganDB(conn_Production);
		LCSendOrganSet pLCSendOrganSet = pLCSendOrganDB
				.executeQuery("select GRPCONTNO, PRTNO, ORGANCOMCODE, ORGANINNERCODE, POSTALADDRESS, ZIPCODE, GRPNAME, MANAGECOM, UPINNERCODE, UPCOMCODE, GROUPLEVEL, ORGANTYPE, ADDRESSNO, LINKMAN, LINKMANMAIL, FAX, '', FOUNDDATE, BANKCODE, '', ACCNAME, SENDFLAG, STATE, OPERATOR, MAKEDATE, MAKETIME, MODIFYDATE, MODIFYTIME, GETMODE, USE, CHILDFLAG, BQFLAG, ANNUALBANKNAME, INTERVALDV, ACCNOFLAG from LCSendOrgan where grpcontno='99029288' and organinnercode='86'");
		index = pLCSendOrganSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCSendOrganSet sSet = new LCSendOrganSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCSendOrganSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCSendOrganSet();
				}
			}
		} else {
			writeLog("noData", "LCSendOrgan");
		}

		writeLog("out", "LTSendOrgan");
		deleteData("DELETE LTSendOrgan where organinnercode='86'");

		LTSendOrganDB pLTSendOrganDB = new LTSendOrganDB(conn_Production);
		LTSendOrganSet pLTSendOrganSet = pLTSendOrganDB
				.executeQuery("select CALMONTH, GRPCONTNO, PRTNO, ORGANCOMCODE, ORGANINNERCODE, POSTALADDRESS, ZIPCODE, GRPNAME, MANAGECOM, UPINNERCODE, UPCOMCODE, GROUPLEVEL, ORGANTYPE, ADDRESSNO, LINKMAN, LINKMANMAIL, '', '', FOUNDDATE, BANKCODE, '', ACCNAME, SENDFLAG, STATE, OPERATOR, MAKEDATE, MAKETIME, MODIFYDATE, MODIFYTIME, GETMODE, USE, CHILDFLAG, BQFLAG, ANNUALBANKNAME, INTERVALDV, ACCNOFLAG from LTSendOrgan where grpcontno='99029288' and organinnercode='86'");
		index = pLTSendOrganSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LTSendOrganSet sSet = new LTSendOrganSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLTSendOrganSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LTSendOrganSet();
				}
			}
		} else {
			writeLog("noData", "LTSendOrgan");
		}

		writeLog("out", "LPSendOrgan");
		deleteData("DELETE LPSendOrgan where organinnercode='86'");

		LPSendOrganDB pLPSendOrganDB = new LPSendOrganDB(conn_Production);
		LPSendOrganSet pLPSendOrganSet = pLPSendOrganDB
				.executeQuery("select EDORNO, EDORTYPE, GRPCONTNO, PRTNO, ORGANCOMCODE, ORGANINNERCODE, POSTALADDRESS, ZIPCODE, GRPNAME, MANAGECOM, UPINNERCODE, UPCOMCODE, GROUPLEVEL, ORGANTYPE, ADDRESSNO, LINKMAN, LINKMANMAIL, FAX, '', FOUNDDATE, BANKCODE, '', ACCNAME, SENDFLAG, STATE, OPERATOR, MAKEDATE, MAKETIME, MODIFYDATE, MODIFYTIME, GETMODE, USE, CHILDFLAG, BQFLAG, ANNUALBANKNAME, INTERVALDV, ACCNOFLAG from LPSendOrgan where grpcontno='99029288' and organinnercode='86'");
		index = pLPSendOrganSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPSendOrganSet sSet = new LPSendOrganSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPSendOrganSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPSendOrganSet();
				}
			}
		} else {
			writeLog("noData", "LPSendOrgan");
		}

		writeLog("out", "LBSendOrgan");
		deleteData("DELETE LBSendOrgan where organinnercode='86'");

		LBSendOrganDB pLBSendOrganDB = new LBSendOrganDB(conn_Production);
		LBSendOrganSet pLBSendOrganSet = pLBSendOrganDB
				.executeQuery("select EDORNO, GRPCONTNO, PRTNO, ORGANCOMCODE, ORGANINNERCODE, '', ZIPCODE, GRPNAME, MANAGECOM, UPINNERCODE, UPCOMCODE, GROUPLEVEL, ORGANTYPE, ADDRESSNO, LINKMAN, '', FAX, '', FOUNDDATE, BANKCODE, '', ACCNAME, SENDFLAG, STATE, OPERATOR, MAKEDATE, MAKETIME, MODIFYDATE, MODIFYTIME, GETMODE, USE, CHILDFLAG, BQFLAG, ANNUALBANKNAME, INTERVALDV, ACCNOFLAG from LBSendOrgan where grpcontno='99029288' and organinnercode='86'");
		index = pLBSendOrganSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LBSendOrganSet sSet = new LBSendOrganSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLBSendOrganSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LBSendOrganSet();
				}
			}
		} else {
			writeLog("noData", "LBSendOrgan");
		}

		writeLog("out", "LOBSendOrgan");
		deleteData("DELETE LOBSendOrgan where organinnercode='86'");

		LOBSendOrganDB pLOBSendOrganDB = new LOBSendOrganDB(conn_Production);
		LOBSendOrganSet pLOBSendOrganSet = pLOBSendOrganDB
				.executeQuery("select GRPCONTNO, PRTNO, ORGANCOMCODE, ORGANINNERCODE, POSTALADDRESS, ZIPCODE, GRPNAME, MANAGECOM, UPINNERCODE, UPCOMCODE, GROUPLEVEL, ORGANTYPE, ADDRESSNO, LINKMAN, LINKMANMAIL, FAX, PHONE, FOUNDDATE, BANKCODE, BANKACCNO, ACCNAME, SENDFLAG, STATE, OPERATOR, MAKEDATE, MAKETIME, MODIFYDATE, MODIFYTIME, GETMODE, USE, CHILDFLAG, BQFLAG, ANNUALBANKNAME, INTERVALDV, ACCNOFLAG from LOBSendOrgan where grpcontno='99029288' and organinnercode='86'");
		index = pLOBSendOrganSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LOBSendOrganSet sSet = new LOBSendOrganSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLOBSendOrganSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LOBSendOrganSet();
				}
			}
		} else {
			writeLog("noData", "LOBSendOrgan");
		}
	}

	public void getOrganPData() {
		int index = 0;
		// 生活补贴工作单位

		writeLog("out", "LCOrgan");
		deleteData("DELETE LCOrgan");

		LCOrganDB pLCOrganDB = new LCOrganDB(conn_Production);
		LCOrganSet pLCOrganSet = pLCOrganDB
				.executeQuery("select * from lcorgan where grpcontno='99029288' and grouplevel<'2'");
		index = pLCOrganSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCOrganSet sSet = new LCOrganSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCOrganSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCOrganSet();
				}
			}
		} else {
			writeLog("noData", "LCOrgan");
		}

		writeLog("out", "LTOrgan");
		deleteData("DELETE LTOrgan");

		LTOrganDB pLTOrganDB = new LTOrganDB(conn_Production);
		LTOrganSet pLTOrganSet = pLTOrganDB
				.executeQuery("select * from ltorgan where grpcontno='99029288' and grouplevel<'2'");
		index = pLTOrganSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LTOrganSet sSet = new LTOrganSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLTOrganSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LTOrganSet();
				}
			}
		} else {
			writeLog("noData", "LTOrgan");
		}

		writeLog("out", "LPOrgan");
		deleteData("DELETE LPOrgan");

		LPOrganDB pLPOrganDB = new LPOrganDB(conn_Production);
		LPOrganSet pLPOrganSet = pLPOrganDB
				.executeQuery("select * from lporgan where grpcontno='99029288' and grouplevel<'2'");
		index = pLPOrganSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPOrganSet sSet = new LPOrganSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPOrganSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPOrganSet();
				}
			}
		} else {
			writeLog("noData", "LPOrgan");
		}

		writeLog("out", "LBOrgan");
		deleteData("DELETE LBOrgan");

		LBOrganDB pLBOrganDB = new LBOrganDB(conn_Production);
		LBOrganSet pLBOrganSet = pLBOrganDB
				.executeQuery("select * from lborgan where grpcontno='99029288' and grouplevel<'2'");
		index = pLBOrganSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LBOrganSet sSet = new LBOrganSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLBOrganSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LBOrganSet();
				}
			}
		} else {
			writeLog("noData", "LBOrgan");
		}

		writeLog("out", "LOBOrgan");
		deleteData("DELETE LOBOrgan");

		LOBOrganDB pLOBOrganDB = new LOBOrganDB(conn_Production);
		LOBOrganSet pLOBOrganSet = pLOBOrganDB
				.executeQuery("select * from loborgan where grpcontno='99029288' and grouplevel<'2'");
		index = pLOBOrganSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LOBOrganSet sSet = new LOBOrganSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLOBOrganSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LOBOrganSet();
				}
			}
		} else {
			writeLog("noData", "LOBOrgan");
		}
	}

	public void getCNPCOrganPData() {
		int index = 0;
		writeLog("out", "LCCNPCOrgan");
		deleteData("DELETE LCCNPCOrgan");

		LCCNPCOrganDB pLCCNPCOrganDB = new LCCNPCOrganDB(conn_Production);
		LCCNPCOrganSet pLCCNPCOrganSet = pLCCNPCOrganDB
				.executeQuery(" select GRPCONTNO, PRTNO, ORGANCOMCODE, ORGANINNERCODE, GRPNAME, ABBREVIATEDNAME, UPCOMCODE, UPGRPNAME, MANAGECOM, CNPCINNERCODE, INCNPCDATE, POSTALADDRESS, PROVICE, ZIPCODE, LINKMAN, '', '', '', ORGANTYPE, USERFLAG, CNPCFLAG, STOCKFLAG, GROUPLEVEL, UPINNERCODE, ADDRESSNO, FOUNDDATE, BANKCODE, '', ACCNAME, STATE, OPERATOR, MAKEDATE, MAKETIME, MODIFYDATE, MODIFYTIME, CHILDFLAG, BQFLAG, PASSWORD, BANKPARTICULAR, BANKPRV, BANKCITY from lccnpcorgan");
		index = pLCCNPCOrganSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCCNPCOrganSet sSet = new LCCNPCOrganSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCCNPCOrganSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCCNPCOrganSet();
				}
			}
		} else {
			writeLog("noData", "LCCNPCOrgan");
		}

		writeLog("out", "LTCNPCOrgan");
		deleteData("DELETE LTCNPCOrgan");

		LTCNPCOrganDB pLTCNPCOrganDB = new LTCNPCOrganDB(conn_Production);
		LTCNPCOrganSet pLTCNPCOrganSet = pLTCNPCOrganDB
				.executeQuery("select * from LTCNPCOrgan where sn like '2013%'");
		index = pLTCNPCOrganSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LTCNPCOrganSet sSet = new LTCNPCOrganSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLTCNPCOrganSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LTCNPCOrganSet();
				}
			}
		} else {
			writeLog("noData", "LTCNPCOrgan");
		}

		writeLog("out", "LPCNPCOrgan");
		deleteData("DELETE liscnpc.LPCNPCOrgan");

		LPCNPCOrganDB pLPCNPCOrganDB = new LPCNPCOrganDB(conn_Production);
		LPCNPCOrganSet pLPCNPCOrganSet = pLPCNPCOrganDB
				.executeQuery("select * from liscnpc.LPCNPCOrgan");
		index = pLPCNPCOrganSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LPCNPCOrganSet sSet = new LPCNPCOrganSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLPCNPCOrganSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LPCNPCOrganSet();
				}
			}
		} else {
			writeLog("noData", "LPCNPCOrgan");
		}

		writeLog("out", "LBCNPCOrgan");
		deleteData("DELETE liscnpc.LBCNPCOrgan");

		LBCNPCOrganDB pLBCNPCOrganDB = new LBCNPCOrganDB(conn_Production);
		LBCNPCOrganSet pLBCNPCOrganSet = pLBCNPCOrganDB
				.executeQuery("select * from liscnpc.LBCNPCOrgan");
		index = pLBCNPCOrganSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LBCNPCOrganSet sSet = new LBCNPCOrganSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLBCNPCOrganSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LBCNPCOrganSet();
				}
			}
		} else {
			writeLog("noData", "LBCNPCOrgan");
		}

		writeLog("out", "LOBCNPCOrgan");
		deleteData("DELETE liscnpc.LOBCNPCOrgan");

		LOBCNPCOrganDB pLOBCNPCOrganDB = new LOBCNPCOrganDB(conn_Production);
		LOBCNPCOrganSet pLOBCNPCOrganSet = pLOBCNPCOrganDB
				.executeQuery("select * from liscnpc.LOBCNPCOrgan");
		index = pLOBCNPCOrganSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LOBCNPCOrganSet sSet = new LOBCNPCOrganSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLOBCNPCOrganSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LOBCNPCOrganSet();
				}
			}
		} else {
			writeLog("noData", "LOBCNPCOrgan");
		}
	}

	public void getOtherPData() {
		int index = 0;
		writeLog("out", "LDMenu");
		deleteData("DELETE LDMenu");

		LDMenuDB pLDMenuDB = new LDMenuDB(conn_Production);
		LDMenuSet pLDMenuSet = pLDMenuDB.executeQuery("select * from LDMenu");
		index = pLDMenuSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LDMenuSet sSet = new LDMenuSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLDMenuSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LDMenuSet();
				}
			}
		} else {
			writeLog("noData", "LDMenu");
		}

		writeLog("out", "LDMenuGrp");
		deleteData("DELETE LDMenuGrp");

		LDMenuGrpDB pLDMenuGrpDB = new LDMenuGrpDB(conn_Production);
		LDMenuGrpSet pLDMenuGrpSet = pLDMenuGrpDB
				.executeQuery("select * from LDMenuGrp");
		index = pLDMenuGrpSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LDMenuGrpSet sSet = new LDMenuGrpSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLDMenuGrpSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LDMenuGrpSet();
				}
			}
		} else {
			writeLog("noData", "LDMenuGrp");
		}

		writeLog("out", "LDMenuGrpToMenu");
		deleteData("DELETE LDMenuGrpToMenu");

		LDMenuGrpToMenuDB pLDMenuGrpToMenuDB = new LDMenuGrpToMenuDB(
				conn_Production);
		LDMenuGrpToMenuSet pLDMenuGrpToMenuSet = pLDMenuGrpToMenuDB
				.executeQuery("select * from LDMenuGrpToMenu");
		index = pLDMenuGrpToMenuSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LDMenuGrpToMenuSet sSet = new LDMenuGrpToMenuSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLDMenuGrpToMenuSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LDMenuGrpToMenuSet();
				}
			}
		} else {
			writeLog("noData", "LDMenuGrpToMenu");
		}

		writeLog("out", "LDCode");
		deleteData("DELETE LDCode");

		LDCodeDB pLDCodeDB = new LDCodeDB(conn_Production);
		LDCodeSet pLDCodeSet = pLDCodeDB.executeQuery("select * from LDCode");
		index = pLDCodeSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LDCodeSet sSet = new LDCodeSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLDCodeSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LDCodeSet();
				}
			}
		} else {
			writeLog("noData", "LDCode");
		}

		writeLog("out", "LMEdorItem");
		deleteData("DELETE LMEdorItem where appobj in ('A','B','T')");

		LMEdorItemDB pLMEdorItemDB = new LMEdorItemDB(conn_Production);
		LMEdorItemSet pLMEdorItemSet = pLMEdorItemDB
				.executeQuery("select * from LMEdorItem where appobj in ('A','B','T')");
		index = pLMEdorItemSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LMEdorItemSet sSet = new LMEdorItemSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLMEdorItemSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LMEdorItemSet();
				}
			}
		} else {
			writeLog("noData", "LMEdorItem");
		}

		writeLog("out", "LCAnnualPayMentDate");
		deleteData("DELETE LCAnnualPayMentDate where grpcontno='99029288'");

		LCAnnualPayMentDateDB pLCAnnualPayMentDateDB = new LCAnnualPayMentDateDB(
				conn_Production);
		LCAnnualPayMentDateSet pLCAnnualPayMentDateSet = pLCAnnualPayMentDateDB
				.executeQuery("select * from LCAnnualPayMentDate where grpcontno='99029288'");
		index = pLCAnnualPayMentDateSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCAnnualPayMentDateSet sSet = new LCAnnualPayMentDateSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCAnnualPayMentDateSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCAnnualPayMentDateSet();
				}
			}
		} else {
			writeLog("noData", "LCAnnualPayMentDate");
		}

		writeLog("out", "LCAnnualRiskSub");
		deleteData("DELETE LCAnnualRiskSub where grpcontno='99029288'");

		LCAnnualRiskSubDB pLCAnnualRiskSubDB = new LCAnnualRiskSubDB(
				conn_Production);
		LCAnnualRiskSubSet pLCAnnualRiskSubSet = pLCAnnualRiskSubDB
				.executeQuery("select * from LCAnnualRiskSub where grpcontno='99029288'");
		index = pLCAnnualRiskSubSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCAnnualRiskSubSet sSet = new LCAnnualRiskSubSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCAnnualRiskSubSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCAnnualRiskSubSet();
				}
			}
		} else {
			writeLog("noData", "LCAnnualRiskSub");
		}

		writeLog("out", "LCAnnualGrpContSub");
		deleteData("DELETE LCAnnualGrpContSub where grpcontno='99029288'");

		LCAnnualGrpContSubDB pLCAnnualGrpContSubDB = new LCAnnualGrpContSubDB(
				conn_Production);
		LCAnnualGrpContSubSet pLCAnnualGrpContSubSet = pLCAnnualGrpContSubDB
				.executeQuery("select * from LCAnnualGrpContSub where grpcontno='99029288'");
		index = pLCAnnualGrpContSubSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCAnnualGrpContSubSet sSet = new LCAnnualGrpContSubSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCAnnualGrpContSubSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCAnnualGrpContSubSet();
				}
			}
		} else {
			writeLog("noData", "LCAnnualGrpContSub");
		}

		writeLog("out", "LCContPlan");
		deleteData("DELETE LCContPlan where grpcontno='99029288'");

		LCContPlanDB pLCContPlanDB = new LCContPlanDB(conn_Production);
		LCContPlanSet pLCContPlanSet = pLCContPlanDB
				.executeQuery("select * from LCContPlan where grpcontno='99029288'");
		index = pLCContPlanSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCContPlanSet sSet = new LCContPlanSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCContPlanSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCContPlanSet();
				}
			}
		} else {
			writeLog("noData", "LCContPlan");
		}

		writeLog("out", "LCContPlanDutyParam");
		deleteData("DELETE LCContPlanDutyParam where grpcontno='99029288'");

		LCContPlanDutyParamDB pLCContPlanDutyParamDB = new LCContPlanDutyParamDB(
				conn_Production);
		LCContPlanDutyParamSet pLCContPlanDutyParamSet = pLCContPlanDutyParamDB
				.executeQuery("select * from LCContPlanDutyParam where grpcontno='99029288'");
		index = pLCContPlanDutyParamSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCContPlanDutyParamSet sSet = new LCContPlanDutyParamSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCContPlanDutyParamSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCContPlanDutyParamSet();
				}
			}
		} else {
			writeLog("noData", "LCContPlanDutyParam");
		}

		writeLog("out", "LCGrpCont");
		deleteData("DELETE LCGrpCont where grpcontno='99029288'");

		LCGrpContDB pLCGrpContDB = new LCGrpContDB(conn_Production);
		LCGrpContSet pLCGrpContSet = pLCGrpContDB
				.executeQuery("select * from LCGrpCont where grpcontno='99029288'");
		index = pLCGrpContSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCGrpContSet sSet = new LCGrpContSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCGrpContSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCGrpContSet();
				}
			}
		} else {
			writeLog("noData", "LCGrpCont");
		}

		writeLog("out", "LDMaxNo");
		deleteData("DELETE LDMaxNo");

		LDMaxNoDB pLDMaxNoDB = new LDMaxNoDB(conn_Production);
		LDMaxNoSet pLDMaxNoSet = pLDMaxNoDB
				.executeQuery("select * from LDMaxNo");
		index = pLDMaxNoSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LDMaxNoSet sSet = new LDMaxNoSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLDMaxNoSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LDMaxNoSet();
				}
			}
		} else {
			writeLog("noData", "LDMaxNo");
		}

		writeLog("out", "LDPremRate");
		deleteData("DELETE LDPremRate");

		LDPremRateDB pLDPremRateDB = new LDPremRateDB(conn_Production);
		LDPremRateSet pLDPremRateSet = pLDPremRateDB
				.executeQuery("select * from LDPremRate");
		index = pLDPremRateSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LDPremRateSet sSet = new LDPremRateSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLDPremRateSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LDPremRateSet();
				}
			}
		} else {
			writeLog("noData", "LDPremRate");
		}

		writeLog("out", "LDRate");
		deleteData("DELETE LDRate");

		LDRateDB pLDRateDB = new LDRateDB(conn_Production);
		LDRateSet pLDRateSet = pLDRateDB.executeQuery("select * from LDRate");
		index = pLDRateSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LDRateSet sSet = new LDRateSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLDRateSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LDRateSet();
				}
			}
		} else {
			writeLog("noData", "LDRate");
		}

		writeLog("out", "LDBillType");
		deleteData("DELETE LDBillType");

		LDBillTypeDB pLDBillTypeDB = new LDBillTypeDB(conn_Production);
		LDBillTypeSet pLDBillTypeSet = pLDBillTypeDB
				.executeQuery("select * from LDBillType");
		index = pLDBillTypeSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LDBillTypeSet sSet = new LDBillTypeSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLDBillTypeSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LDBillTypeSet();
				}
			}
		} else {
			writeLog("noData", "LDBillType");
		}

		writeLog("out", "cashvalue103");
		deleteData("DELETE cashvalue103");
		SSRS aSSRS = tExeSQL_Pro.execSQL("select * from cashvalue103");
		for (int i = 1; i <= aSSRS.getMaxRow(); i++) {
			aMMap.put("insert cashvalue103 values('" + aSSRS.GetText(i, 1)
					+ "','" + aSSRS.GetText(i, 2) + "','" + aSSRS.GetText(i, 3)
					+ "','" + aSSRS.GetText(i, 4) + "')", "INSERT");
		}
		pubSubmit();
	}

	public boolean getEdorPData() {
		int index = 0;

		writeLog("out", "LCEdorItemFlow");
		deleteData("DELETE LCEdorItemFlow");
		LCEdorItemFlowDB pLCEdorItemFlowDB = new LCEdorItemFlowDB(
				conn_Production);
		LCEdorItemFlowSet pLCEdorItemFlowSet = pLCEdorItemFlowDB
				.executeQuery("select * from LCEdorItemflow");
		index = pLCEdorItemFlowSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCEdorItemFlowSet sSet = new LCEdorItemFlowSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCEdorItemFlowSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCEdorItemFlowSet();
				}
			}
		} else {
			writeLog("noData", "LCEdorItemFlow");
		}

		writeLog("out", "LCEdorFlow");
		deleteData("DELETE LCEdorFlow");
		LCEdorFlowDB pLCEdorFlowDB = new LCEdorFlowDB(conn_Production);
		LCEdorFlowSet pLCEdorFlowSet = pLCEdorFlowDB
				.executeQuery("select * from LCEdorFlow");
		index = pLCEdorFlowSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCEdorFlowSet sSet = new LCEdorFlowSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCEdorFlowSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCEdorFlowSet();
				}
			}
		} else {
			writeLog("noData", "LCEdorFlow");
		}

		writeLog("out", "LCEdorFlowSub");
		deleteData("DELETE LCEdorFlowSub");
		LCEdorFlowSubDB pLCEdorFlowSubDB = new LCEdorFlowSubDB(conn_Production);
		LCEdorFlowSubSet pLCEdorFlowSubSet = pLCEdorFlowSubDB
				.executeQuery("select * from LCEdorFlowSub");
		index = pLCEdorFlowSubSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCEdorFlowSubSet sSet = new LCEdorFlowSubSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCEdorFlowSubSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCEdorFlowSubSet();
				}
			}
		} else {
			writeLog("noData", "LCEdorFlowSub");
		}

		writeLog("out", "LCEdorFlowUserRela");
		deleteData("DELETE LCEdorFlowUserRela a where exists(select 'x' from lduser b where b.usercode=a.usercode)");
		LCEdorFlowUserRelaDB pLCEdorFlowUserRelaDB = new LCEdorFlowUserRelaDB(
				conn_Production);
		LCEdorFlowUserRelaSet pLCEdorFlowUserRelaSet = pLCEdorFlowUserRelaDB
				.executeQuery("select * from LCEdorFlowUserRela a where exists(select 'x' from lduser b where b.usercode=a.usercode)");
		index = pLCEdorFlowUserRelaSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCEdorFlowUserRelaSet sSet = new LCEdorFlowUserRelaSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCEdorFlowUserRelaSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCEdorFlowUserRelaSet();
				}
			}
		} else {
			writeLog("noData", "LCEdorFlowUserRela");
		}

		writeLog("out", "LCEdorFlowOrganRela");
		deleteData("DELETE LCEdorFlowOrganRela");
		LCEdorFlowOrganRelaDB pLCEdorFlowOrganRelaDB = new LCEdorFlowOrganRelaDB(
				conn_Production);
		LCEdorFlowOrganRelaSet pLCEdorFlowOrganRelaSet = pLCEdorFlowOrganRelaDB
				.executeQuery("select * from LCEdorFlowOrganRela");
		index = pLCEdorFlowOrganRelaSet.size();
		writeLog("out", index + "条记录");
		if (index > 0) {
			LCEdorFlowOrganRelaSet sSet = new LCEdorFlowOrganRelaSet();
			for (int i = 1; i <= index; i++) {
				sSet.add(pLCEdorFlowOrganRelaSet.get(i));
				if (i % submitNumber == 0 || i == index) {
					writeLog("out", i + "条记录,提交");
					aMMap.put(sSet, "INSERT");
					pubSubmit();
					sSet = new LCEdorFlowOrganRelaSet();
				}
			}
		} else {
			writeLog("noData", "LCEdorFlowOrganRela");
		}
		return true;
	}

	public boolean testData(String SendComCode) {
		int index = 0;

		writeLog("out", "LCAnnualGet");
		deleteData("DELETE LCAnnualGet a where grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
				+ SendInnerCode + "%')");

		LCAnnualGetDB pLCAnnualGetDB = new LCAnnualGetDB(conn_Production);
		for (int a = 2013; a >= 2004; a--) {
			LCAnnualGetSet pLCAnnualGetSet = pLCAnnualGetDB
					.executeQuery("select * from LCAnnualGet a where a.grpcontno='99029288' and a.actugetmonth like '"+a+"%' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
				+ SendInnerCode + "%')");
			index = pLCAnnualGetSet.size();
			writeLog("out", a + "年共" + index + "条记录");
			if (index > 0) {
				LCAnnualGetSet sSet = new LCAnnualGetSet();
				for (int i = 1; i <= index; i++) {
					sSet.add(pLCAnnualGetSet.get(i));
					if (i % submitNumber == 0 || i == index) {
						writeLog("out", i + "条记录,提交");
						aMMap.put(sSet, "INSERT");
						pubSubmit();
						sSet = new LCAnnualGetSet();
					}
				}
			}
		}

//		writeLog("out", "LCAnnualGetSub");
//		deleteData("DELETE LCAnnualGetSub a where grpcontno='99029288' and exists(select 'x' from lcinsured b where b.grpcontno='99029288' and b.insuredno=a.insuredno and b.contno=a.contno and b.sendinnercode like '"
//				+ SendInnerCode + "%')");
//
//		SSRS aSSRS = tExeSQL_Pro
//				.execSQL("select insuredno from lcinsured where grpcontno='99029288' and sendinnercode like '"
//						+ SendInnerCode + "%'");
//		writeLog("out", "共" + aSSRS.getMaxRow() + "人");
//		LCAnnualGetSubDB pLCAnnualGetSubDB = new LCAnnualGetSubDB(
//				conn_Production);
//		for (int a = 1; a <= aSSRS.getMaxRow(); a++) {
//			LCAnnualGetSubSet pLCAnnualGetSubSet = pLCAnnualGetSubDB
//					.executeQuery("select * from LCAnnualGetSub a where grpcontno='99029288' and insuredno='"
//							+ aSSRS.GetText(a, 1) + "'");
//			index = pLCAnnualGetSubSet.size();
//			if (index > 0) {
//				LCAnnualGetSubSet sSet = new LCAnnualGetSubSet();
//				for (int i = 1; i <= index; i++) {
//					sSet.add(pLCAnnualGetSubSet.get(i));
//				}
//				aMMap.put(sSet, "INSERT");
//				pubSubmit();
//			}
//			if (a % 100 == 0) {// 100人提交一次
//				pubSubmit();
//			}
//		}
		return true;
	}

	public void updateDate() {
		aMMap.put("UPDATE LDUser set password='BF0AADF2ED34AC65'", "UPDATE");
		aMMap.put("UPDATE LPUser set password='BF0AADF2ED34AC65'", "UPDATE");
		mResult.add(aMMap);
		PubSubmit tPubSubmit = new PubSubmit();

		if (!tPubSubmit.submitData(mResult, "UPDATE")) {
			// @@错误处理
			CError.buildErr(this, "PubSubmit提交数据失败");
		}
		aMMap = new MMap();
		mResult = new VData();
		;
	}

	public void updateData(String sql) {
		aMMap.put(sql, "UPDATE");
		mResult.add(aMMap);
		PubSubmit tPubSubmit = new PubSubmit();

		if (!tPubSubmit.submitData(mResult, "UPDATE")) {
			// @@错误处理
			CError.buildErr(this, "PubSubmit提交数据失败");
		}
		aMMap = new MMap();
		mResult = new VData();
		;
	}

	public void deleteData(String sql) {
		aMMap.put(sql, "DELETE");
		mResult.add(aMMap);
		PubSubmit tPubSubmit = new PubSubmit();

		if (!tPubSubmit.submitData(mResult, "DELETE&INSERT")) {
			writeLog("out", "PubSubmit提交数据失败");
		}
		aMMap = new MMap();
		mResult = new VData();
		;
	}

	public void pubSubmit() {
		mResult.add(aMMap);
		PubSubmit tPubSubmit = new PubSubmit();

		if (!tPubSubmit.submitData(mResult, "DELETE&INSERT")) {
			writeLog("out", "PubSubmit提交数据失败");
		}
		aMMap = new MMap();
		mResult = new VData();
		;
	}

	public static void writeLog(String type, String table) {
		String content = "＝＝＝ " + PubFun.getCurrentDate() + " "
				+ PubFun.getCurrentTime() + " ... " + table + " ... ";
		if ("noData".equals(type)) {
			content += "没有数据。";
		} else if ("error delete".equals(type)) {
			content += " = = = = = = " + type + " = = = = = = 删除原表数据失败。";
		}
		System.out.println(content);
	}

	public void connColse() {
		// try {
		// stmtTest.close();
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }

		// 关闭数据库连接
		try {
			conn_Production.close();
			conn_Production = null;
			// conn_Test.close();
			// conn_Test = null;
		} catch (Exception ex) {
			conn_Production = null;
			// conn_Test = null;
			ex.printStackTrace();
		}
	}

	public boolean initData(String SendComCode) {
		if (conn_Production == null) {
			writeLog("error", "数据库连接出错！");
			return false;
		}
		SSRS tSSRS = new SSRS();
		tSSRS = tExeSQL_Pro
				.execSQL("select organcomcode,organinnercode from lcsendorgan where grpcontno='99029288' and organcomcode='"
						+ SendComCode + "'");
		if (tSSRS.getMaxRow() > 0) {
			SendInnerCode = tSSRS.GetText(1, 2);
		} else {
			writeLog("error", "获取参数信息失败！");
			return false;
		}
		writeLog("out", SendComCode + " *** " + SendInnerCode);
		return true;
	}

	public static void main(String args[]) {
		// 21081707
		// 21031703
		// 2209
		// 1204
		//少get和getsub表
		// 2401
		// 不能查看
		// 0114
		// 1114
		// 4014
		writeLog("out", "start");
		DownData tDownData = new DownData();
		String sendComCode = "2401";
		if (tDownData.initData(sendComCode)) {
			// tDownData.getPublicData();
//			 tDownData.getData();
//			 tDownData.updateDate();
			tDownData.testData(sendComCode);
		}
//		sendComCode = "1204";
//		if (tDownData.initData(sendComCode)) {
//			tDownData.testData(sendComCode);
//		}
//		sendComCode = "2209";
//		if (tDownData.initData(sendComCode)) {
//			tDownData.testData(sendComCode);
//		}
//		sendComCode = "21031703";
//		if (tDownData.initData(sendComCode)) {
//			tDownData.testData(sendComCode);
//		}
//		sendComCode = "21081707";
//		if (tDownData.initData(sendComCode)) {
//			tDownData.testData(sendComCode);
//		}
		tDownData.connColse();
		writeLog("out", "end");
	}
}

/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

/**
 * <p>
 * Title: Web业务系统
 * </p>
 * <p>
 * Description:系统号码管理（长城人寿核心业务系统）生成系统号码
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: Sinosoft
 * </p>
 * 
 * @author Fanym
 * @version 1.0
 */

import java.sql.Connection;
import java.math.BigInteger;
import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.*;
import com.sinosoft.utility.treetable.DBUtil;
import com.sinosoft.utility.TransferData;

public class SysMaxNoZhongYi implements com.sinosoft.lis.pubfun.SysMaxNo {

	public SysMaxNoZhongYi() {
	}

	/**
	 * <p>
	 * 生成流水号的函数
	 * <p>
	 * <p>
	 * 号码规则：机构编码 日期年 校验位 类型 流水号
	 * <p>
	 * <p>
	 * 1-6 7-10 11 12-13 14-20
	 * <p>
	 * 
	 * @param cNoType
	 *            为需要生成号码的类型
	 * @param cNoLimit
	 *            为需要生成号码的限制条件（要么是SN，要么是机构编码）
	 * @param cVData
	 *            为需要生成号码的业务相关限制条件
	 * @return 生成的符合条件的流水号，如果生成失败，返回空字符串""
	 *         针对颐和的单证类型，需要一单证一版本流水的规则，LDMaxNo中的字段含义如下： NoType:单证编码 NoLimit:单证版本
	 */
	public String CreateMaxNo(String cNoType, String cNoLimit, VData cVData) {
		// 单证编码的总位数

		int serialLen = 16;
		String tReturn = null;
		if (cNoType == null || cNoType.equalsIgnoreCase("")) {
			return null;
		}
		// 划帐失败通知书
		else if (cNoType.equalsIgnoreCase("TRANSFER")) {
			tReturn = "1019";
		}
		// 客户问题件通知书
		else if (cNoType.equalsIgnoreCase("CUSISSUE")) {
			tReturn = "1022";
		}
		// 核保通知书
		else if (cNoType.equalsIgnoreCase("HEBAOHAN")) {
			tReturn = "1023";
		}
		// 体检通知书
		else if (cNoType.equalsIgnoreCase("TIJIAN")) {
			tReturn = "1028";
		}
		// 契调通知书
		else if (cNoType.equalsIgnoreCase("QIDIAO")) {
			tReturn = "1045";
		}
		// 补费通知书
		else if (cNoType.equalsIgnoreCase("BUFEI")) {
			tReturn = "1048";
		}
		// 业务员问题件
		else if (cNoType.equalsIgnoreCase("AGENTISSUE")) {
			tReturn = "1066";
		}
		// 特殊件问题件
		else if (cNoType.equalsIgnoreCase("SPECISSUE")) {
			tReturn = "1067";
		}
		// 集体保单号
		else if (cNoType.equalsIgnoreCase("GRPCONTNO")) {
			String grpcontno = (String) ((TransferData) cVData
					.getObjectByObjectName("TransferData", 0))
					.getValueByName("GRPCONTNO");
			System.out.println("++++++++" + grpcontno);
			if (grpcontno != null) {
				BigInteger grp = new BigInteger(grpcontno);
				BigInteger t = BigInteger.valueOf(1);
				grp = grp.add(t);
				System.out.println("tttttt " + grp);
				grpcontno = grp + "";
				return grpcontno;
			}
		}
		// 立案分案编号
		else if (cNoType.equalsIgnoreCase("CASENO")) {
			TransferData tTransferData = new TransferData();
			tTransferData = (TransferData) cVData.getObjectByObjectName(
					"TransferData", 0);
			String tNoLimit = (String) tTransferData.getValueByName("RGTNO");

			// String prgino = (String)((TransferData)
			// cVData.getObjectByObjectName("TransferData",
			// 0)).getValueByName("RGTNO");
			System.out.println("+++++++prgino= " + tNoLimit);
			if (tNoLimit != null) {
				tReturn = tNoLimit;

				Connection conn = DBConnPool.getConnection();
				if (conn == null) {
					System.out
							.println("CreateMaxNo : fail to get db connection");
					return null;
				}
				// int tMaxNo = 0;
				BigInteger tMaxNo = new BigInteger("0");
				try {
					// 开始事务
					// 查询结果有3个： -- added by Fanym
					// 全部采用直接执行SQL语句，只要有其他事务锁定了本行，立即返回NULL
					// 如果没有锁定，则本事务锁定，查询得到结果则UPDATE，没有则INSERT
					conn.setAutoCommit(false);

					String strSQL = "select MaxNo from LDZhongYiMaxNo where notype='"
							+ cNoType
							+ "' and nolimit='"
							+ tNoLimit
							+ "' for update";
					ExeSQL exeSQL = new ExeSQL(conn);
					String result = null;
					result = exeSQL.getOneValue(strSQL);

					// 测试返回错误
					if (exeSQL.mErrors.needDealError()) {
						System.out.println("查询LDZhongYiMaxNo出错，请稍后!");
						conn.rollback();
						conn.close();
						return null;
					}

					if ((result == null) || result.equals("")) {
						strSQL = "insert into ldzhongyimaxno(notype, nolimit, maxno) values('"
								+ cNoType + "', '" + tNoLimit + "', 1)";
						exeSQL = new ExeSQL(conn);
						if (!exeSQL.execUpdateSQL(strSQL)) {
							System.out.println("CreateMaxNo 插入失败，请重试!");
							conn.rollback();
							conn.close();
							return null;
						} else {
							// tMaxNo = 1;
							tMaxNo = new BigInteger("1");
						}
					} else {
						strSQL = "update ldzhongyimaxno set maxno = maxno + 1 where notype = '"
								+ cNoType
								+ "' and nolimit = '"
								+ tNoLimit
								+ "'";
						exeSQL = new ExeSQL(conn);
						if (!exeSQL.execUpdateSQL(strSQL)) {
							System.out.println("CreateMaxNo 更新失败，请重试!");
							conn.rollback();
							conn.close();
							return null;
						} else {
							// tMaxNo = Integer.parseInt(result) + 1;
							tMaxNo = new BigInteger(result).add(new BigInteger(
									"1"));
						}
					}
					conn.commit();
					conn.close();
				} catch (Exception Ex) {
					try {
						conn.rollback();
						conn.close();
						return null;
					} catch (Exception e1) {
						e1.printStackTrace();
						return null;
					}
				}
				// 长度为3位，不足左补0
				String s = PubFun.LCh(tMaxNo.toString(), "0", 3);
				tReturn = tReturn + s; // 总案号+3位分案号
				return tReturn;
			}

		}
		// 调查编号
		else if (cNoType.equalsIgnoreCase("SURVEYNO")) {
			TransferData tTransferData = new TransferData();
			tTransferData = (TransferData) cVData.getObjectByObjectName(
					"TransferData", 0);
			String tNoLimit = (String) tTransferData.getValueByName("SURVEYNO");

			// String prgino = (String)((TransferData)
			// cVData.getObjectByObjectName("TransferData",
			// 0)).getValueByName("RGTNO");
			System.out.println("+++++++prgino= " + tNoLimit);
			if (tNoLimit != null) {
				tReturn = tNoLimit;

				Connection conn = DBConnPool.getConnection();
				if (conn == null) {
					System.out
							.println("CreateMaxNo : fail to get db connection");
					return null;
				}
				// int tMaxNo = 0;
				BigInteger tMaxNo = new BigInteger("0");
				try {
					// 开始事务
					// 查询结果有3个： -- added by Fanym
					// 全部采用直接执行SQL语句，只要有其他事务锁定了本行，立即返回NULL
					// 如果没有锁定，则本事务锁定，查询得到结果则UPDATE，没有则INSERT
					conn.setAutoCommit(false);

					String strSQL = "select MaxNo from LDZhongYiMaxNo where notype='"
							+ cNoType
							+ "' and nolimit='"
							+ tNoLimit
							+ "' for update";
					ExeSQL exeSQL = new ExeSQL(conn);
					String result = null;
					result = exeSQL.getOneValue(strSQL);

					// 测试返回错误
					if (exeSQL.mErrors.needDealError()) {
						System.out.println("查询LDZhongYiMaxNo出错，请稍后!");
						conn.rollback();
						conn.close();
						return null;
					}

					if ((result == null) || result.equals("")) {
						strSQL = "insert into ldzhongyimaxno(notype, nolimit, maxno) values('"
								+ cNoType + "', '" + tNoLimit + "', 1)";
						exeSQL = new ExeSQL(conn);
						if (!exeSQL.execUpdateSQL(strSQL)) {
							System.out.println("CreateMaxNo 插入失败，请重试!");
							conn.rollback();
							conn.close();
							return null;
						} else {
							// tMaxNo = 1;
							tMaxNo = new BigInteger("1");
						}
					} else {
						strSQL = "update ldzhongyimaxno set maxno = maxno + 1 where notype = '"
								+ cNoType
								+ "' and nolimit = '"
								+ tNoLimit
								+ "'";
						exeSQL = new ExeSQL(conn);
						if (!exeSQL.execUpdateSQL(strSQL)) {
							System.out.println("CreateMaxNo 更新失败，请重试!");
							conn.rollback();
							conn.close();
							return null;
						} else {
							// tMaxNo = Integer.parseInt(result) + 1;
							tMaxNo = new BigInteger(result).add(new BigInteger(
									"1"));
						}
					}
					conn.commit();
					conn.close();
				} catch (Exception Ex) {
					try {
						conn.rollback();
						conn.close();
						return null;
					} catch (Exception e1) {
						e1.printStackTrace();
						return null;
					}
				}
				// 长度为2位，不足左补0
				String s = PubFun.LCh(tMaxNo.toString(), "0", 2);
				tReturn = tReturn + s; // 总案号+3位分案号
				return tReturn;
			}

		} else {
			return null;
		}

		Connection conn = DBConnPool.getConnection();
		if (conn == null) {
			System.out.println("CreateMaxNo : fail to get db connection");
			return null;
		}
		// int tMaxNo = 0;
		BigInteger tMaxNo = new BigInteger("0");
		try {
			// 开始事务
			// 查询结果有3个： -- added by Fanym
			// 全部采用直接执行SQL语句，只要有其他事务锁定了本行，立即返回NULL
			// 如果没有锁定，则本事务锁定，查询得到结果则UPDATE，没有则INSERT
			conn.setAutoCommit(false);
			// 查询单证描述表，获取该单证类型有效的最大版本号
			String strSQL = "select max(certifycode) from lmcertifydes where state = '0' and Certifycode like '"
					+ tReturn + "%'";
			ExeSQL exeSQL = new ExeSQL(conn);
			String tVersion = exeSQL.getOneValue(strSQL).substring(4, 6);

			// 如果版本号不存在，则证明没有此单证类型描述
			if (tVersion == null || tVersion.equals("")) {
				System.out.println("没有该" + cNoType + "单证类型描述！");
				conn.rollback();
				conn.close();
				return null;
			}
			// 把版本作为限制条件，长度为2位，不足左补0
			cNoLimit = PubFun.LCh(tVersion, "0", 2);
			strSQL = "select MaxNo from LDMaxNo where notype='" + cNoType
					+ "' and nolimit='" + cNoLimit + "' for update";
			exeSQL = new ExeSQL(conn);
			String result = null;
			result = exeSQL.getOneValue(strSQL);

			// 测试返回错误
			if (exeSQL.mErrors.needDealError()) {
				System.out.println("查询LDMaxNo出错，请稍后!");
				conn.rollback();
				conn.close();
				return null;
			}

			if ((result == null) || result.equals("")) {
				strSQL = "insert into ldmaxno(notype, nolimit, maxno) values('"
						+ cNoType + "', '" + cNoLimit + "', 1)";
				exeSQL = new ExeSQL(conn);
				if (!exeSQL.execUpdateSQL(strSQL)) {
					System.out.println("CreateMaxNo 插入失败，请重试!");
					conn.rollback();
					conn.close();
					return null;
				} else {
					// tMaxNo = 1;
					tMaxNo = new BigInteger("1");
				}
			} else {
				strSQL = "update ldmaxno set maxno = maxno + 1 where notype = '"
						+ cNoType + "' and nolimit = '" + cNoLimit + "'";
				exeSQL = new ExeSQL(conn);
				if (!exeSQL.execUpdateSQL(strSQL)) {
					System.out.println("CreateMaxNo 更新失败，请重试!");
					conn.rollback();
					conn.close();
					return null;
				} else {
					// tMaxNo = Integer.parseInt(result) + 1;
					tMaxNo = new BigInteger(result).add(new BigInteger("1"));
				}
			}
			conn.commit();
			conn.close();
		} catch (Exception Ex) {
			try {
				conn.rollback();
				conn.close();
				return null;
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
		}
		// 获取流水号，总位数-单证类型长度-版本位数-最后两位88
		String tStr = PubFun.LCh(tMaxNo.toString(), "0", serialLen
				- tReturn.length() - cNoLimit.length() - 2);

		tReturn = tReturn + cNoLimit + tStr + "88";
		// 返回单证流水号
		return tReturn;
	}

	/**
	 * <p>
	 * 生成流水号的函数
	 * <p>
	 * <p>
	 * 号码规则：机构编码 日期年 校验位 类型 流水号
	 * <p>
	 * <p>
	 * 1-6 7-10 11 12-13 14-20
	 * <p>
	 * 
	 * @param cNoType
	 *            为需要生成号码的类型
	 * @param cNoLimit
	 *            为需要生成号码的限制条件（要么是SN，要么是机构编码）
	 * @return 生成的符合条件的流水号，如果生成失败，返回空字符串""
	 */
	public String CreateMaxNo(String cNoType, String cNoLimit) {

		String copyNolMit = ""; // 中意 ，用来暂时存放cNoLimit
		// 传入的参数不能为空，如果为空，则直接返回
		if ((cNoType == null) || (cNoType.trim().length() <= 0)
				|| (cNoLimit == null)) {
			System.out.println("NoType长度错误或者NoLimit为空");
			return null;
		}

		int serialLen = 16; // 长城号码总长度是16位
		String tReturn = null; // 返回字符串
		cNoType = cNoType.toUpperCase(); // 流水类型
		// String tBit = "0"; //校验位

		// cNoLimit如果是SN类型，则cNoType只能是下面类型中的一个
		if (cNoLimit.trim().equalsIgnoreCase("SN")) {
			// modify by yt 2002-11-04
			if (cNoType.equalsIgnoreCase("CUSTOMERNO")
					|| cNoType.equalsIgnoreCase("GRPNO")
					|| cNoType.equalsIgnoreCase("VCUSTOMERNO")) {
				serialLen = 12; // 长城默认客户号是12位
			} else if (cNoType.equalsIgnoreCase("COMMISIONSN")) {
				serialLen = 20; // 扎帐表流水号默认是20位
			} 
			//liuwd 20070927
			else if (cNoType.equalsIgnoreCase("EDITUSER")) {
				serialLen = 10; // 
			} 
			
			else {
				System.out.println("错误的NoLimit");
				return null;
			}
		}
		//added by zhangjq for 年金子保单
		else if (cNoType.toUpperCase().startsWith("GRPCONTNOSUB")) {

		}
		else if (!cNoType.equalsIgnoreCase("ORGCOMCODE")
				&& !cNoType.equalsIgnoreCase("SENDCOMCODE")) {
			copyNolMit = cNoLimit;
			cNoLimit = "SYS";

		}

		// 中意团体年金机构内部编码,传入的cNoLimit必须为上级机构的内部编码
		if (cNoType.equalsIgnoreCase("ORGCOMCODE")
				|| cNoType.equalsIgnoreCase("SENDCOMCODE")) {
			serialLen = 4;
		}

		// 个人客户号
		if (cNoType.equalsIgnoreCase("CUSTOMERNO")) {
			tReturn = "1"; // 1＋9位流水＋88
			serialLen = 12;
		}
		// 团体客户号
		else if (cNoType.equalsIgnoreCase("GRPNO")) {
			serialLen = 12;
			tReturn = "9"; // 9＋9位流水＋88
		}
		// 虚拟客户：无名单、公共帐户
		else if (cNoType.equalsIgnoreCase("VCUSTOMERNO")) {
			tReturn = "0";
		}
		// 个险投保单
		else if (cNoType.equalsIgnoreCase("PROPOSALNO")) {
			tReturn = "9010";
		}
		// 团体下个险投保单
		else if (cNoType.equalsIgnoreCase("GPROPOSALNO")) {
			tReturn = "9011";
		}
		// 团体投保单
		else if (cNoType.equalsIgnoreCase("GRPPROPOSALNO")) {
			tReturn = "9012";
		}
		// 总单投保单号码，系统类型
		else if (cNoType.equalsIgnoreCase("PROPOSALCONTNO")) {
			tReturn = "9015";
		}
		// 团体下合同投保单号码
		else if (cNoType.equalsIgnoreCase("GPROPOSALCONTNO")) {
			tReturn = "9016";
		}
		// 集体投保单号码
		else if (cNoType.equalsIgnoreCase("PROGRPCONTNO")) {
			if (copyNolMit.length() != 8) {
				return "所传号码错误,长度应该为8位";
			}
			Object o = DBUtil
					.excuteOneValue("select InnerComCode from ldcom where comcode='"
							+ copyNolMit + "'");
			if (o != null) {
				tReturn = o.toString(); // 4位branch code+5位流水号
				serialLen = 9;
			} else {
				return null;
			}
		}
		// 保单险种号码
		else if (cNoType.equalsIgnoreCase("POLNO")) {
			tReturn = "9020";
		}
		// 团体下保单险种号码
		else if (cNoType.equalsIgnoreCase("GPOLNO")) {
			tReturn = "9021";
		}
		// 集体保单险种号码
		else if (cNoType.equalsIgnoreCase("GRPPOLNO")) {
			tReturn = "9022";
		}
		// 个人保单号码
		else if (cNoType.equalsIgnoreCase("CONTNO")) {
			tReturn = "9025";
		}
		// 团体下个人保单号码
		else if (cNoType.equalsIgnoreCase("GCONTNO")) {
			serialLen = 12;
			tReturn = "6"; // 6＋9位流水＋88
		}
		// 集体保单号
		else if (cNoType.equalsIgnoreCase("GRPCONTNO")) {
			if (copyNolMit.length() != 8) {
				return "所传号码错误,长度应该为8位";
			}

			Object o = DBUtil
					.excuteOneValue("select InnerComCode from ldcom where comcode='"
							+ copyNolMit + "'");
			if (o != null) {
				tReturn = "8" + o.toString().substring(1, 3); // 8＋2位branchcode+6位流水号＋2位续保号码
				serialLen = 11;
			} else {
				return null;
			}
		}
		// 交通意外险保单号
		else if (cNoType.equalsIgnoreCase("AIRPOLNO")) {
			tReturn = "9029";
		}
		// 核保通知书号
		// else if (cNoType.equalsIgnoreCase("UWNOTICENO"))
		// {
		// tReturn = "102301";
		// }
		// 交费通知书号码
		else if (cNoType.equalsIgnoreCase("PAYNOTICENO")) {
			tReturn = "102101";
		}
		// 交费收据号码
		else if (cNoType.equalsIgnoreCase("PAYNO")) {
			tReturn = "9032";
		}
		// 给付通知书号码
		else if (cNoType.equalsIgnoreCase("GETNOTICENO")) {
			tReturn = "9036";
		}
		// 实付号码
		else if (cNoType.equalsIgnoreCase("GETNO")) {
			tReturn = "9038";
		}
		// 批改申请号码
		else if (cNoType.equalsIgnoreCase("EDORAPPNO")) {
			tReturn = "9041";
		}
		// 批单号码
		else if (cNoType.equalsIgnoreCase("EDORNO")) {
			tReturn = "9046";
		}
		// 集体批单申请号码
		else if (cNoType.equalsIgnoreCase("EDORGRPAPPNO")) {
			tReturn = "9043";
		}
		// 集体批单号码
		else if (cNoType.equalsIgnoreCase("EDORGRPNO")) {
			serialLen = 12;
			tReturn = "5"; // 5＋9位流水＋88
		}
		// 保全受理号码
		else if (cNoType.equalsIgnoreCase("EDORACCEPTNO")) {
			tReturn = "9048";
		}
		// 报案编号
		else if (cNoType.equalsIgnoreCase("RPTNO")) {
			tReturn = "9050";
		}
		// 立案编号
		else if (cNoType.equalsIgnoreCase("RGTNO")) {
			if (copyNolMit.length() != 8) {
				return "所传号码错误,长度应该为8位";
			}

			Object o = DBUtil
					.excuteOneValue("select InnerComCode from ldcom where comcode='"
							+ copyNolMit + "'");
			serialLen = 12;
			String date = PubFun.getCurrentDate();
			if (o != null) {
				tReturn = date.substring(0, 4) + o.toString().substring(1, 3); // 四位年＋2位branch
				// code＋6位流水
				// //
			} else {
				return null;
			}

		}
		// 赔案编号
		else if (cNoType.equalsIgnoreCase("CLMNO")) {
			tReturn = "9052";
		}
		// 拒案编号
		else if (cNoType.equalsIgnoreCase("DECLINENO")) {
			tReturn = "9053";
		}
		// 报案分案编号
		else if (cNoType.equalsIgnoreCase("SUBRPTNO")) {
			tReturn = "9054";
		}
		// 立案分案编号
		// else if (cNoType.equalsIgnoreCase("CASENO"))
		// {
		// serialLen = 15;
		// //////////////////总案号＋3位分案号
		// tReturn = "9055";
		// }
		// 事件编号
		else if (cNoType.equalsIgnoreCase("CASERELANO")) {
			tReturn = "9056";
		}
		// 打印管理流水号
		else if (cNoType.equalsIgnoreCase("PRTSEQNO")) {
			tReturn = "9066";
		}
		// 打印管理流水号
		else if (cNoType.equalsIgnoreCase("PRTSEQ2NO")) {
			tReturn = "9068";
		}
		// 回收清算单号
		else if (cNoType.equalsIgnoreCase("TAKEBACKNO")) {
			tReturn = "9061";
		}
		// 银行代扣代付批次号
		else if (cNoType.equalsIgnoreCase("BATCHNO")) {
			tReturn = "9063";
		}
		// 扎帐表流水号
		else if (cNoType.equalsIgnoreCase("COMMISIONSN")) {
			tReturn = "9071";
		}
		// 中介协议书号码
		else if (cNoType.equalsIgnoreCase("PROTOCOLNO")) {
			serialLen = 15;
			tReturn = "907";
		}
		// 建议书编码
		else if (cNoType.equalsIgnoreCase("SUGCODE")) {
			tReturn = "9088";
		}
		// 建议书模版代码
		else if (cNoType.equalsIgnoreCase("SUGMODELCODE")) {
			tReturn = "9082";
		}
		// 建议书项目编码
		else if (cNoType.equalsIgnoreCase("SUGITEMCODE")) {
			tReturn = "9083";
		}
		// 建议书数据项目编码
		else if (cNoType.equalsIgnoreCase("SUGDATAITEMCODE")) {
			tReturn = "9085";
		}
		// 流水号
		else if (cNoType.equalsIgnoreCase("SERIALNO")) {
			tReturn = "9090";
		}
		// 财务需要流水号
		else if (cNoType.equalsIgnoreCase("FINNO")) {
			tReturn = "212401";
		}
		//维护用户流水号
		else if(cNoType.equalsIgnoreCase("EDITUSER")){
			tReturn="5700";
		}
//		年金子保单处理号,zhangjq 2007-11-27
		else if(cNoType.equalsIgnoreCase("GrpContNoSub")){
			tReturn="8800";
		}
		//年金子保单撤销处理号,zhangjq 2007-11-27
		else if(cNoType.equalsIgnoreCase("GrpContNoSubBk")){
			tReturn="8700";
		}
		// 其他
		else {
			tReturn = "";
			// return null;
		}

		// 中意团体年金机构内部编码,传入的cNoLimit必须为上级机构的内部编码
		if (cNoType.equalsIgnoreCase("ORGCOMCODE")
				|| cNoType.equalsIgnoreCase("SENDCOMCODE")) {
			tReturn = cNoLimit;
		}

		Connection conn = DBConnPool.getConnection();
		if (conn == null) {
			System.out.println("CreateMaxNo : fail to get db connection");
			return null;
		}

		// int tMaxNo = 0;
		BigInteger tMaxNo = new BigInteger("0");

		try {
			// 开始事务
			// 查询结果有3个： -- added by Fanym
			// 全部采用直接执行SQL语句，只要有其他事务锁定了本行，立即返回NULL
			// 如果没有锁定，则本事务锁定，查询得到结果则UPDATE，没有则INSERT
			conn.setAutoCommit(false);

			String strSQL = "select MaxNo from LDMaxNo where notype='"
					+ cNoType + "' and nolimit='" + cNoLimit + "' for update";
			ExeSQL exeSQL = new ExeSQL(conn);
			String result = null;
			result = exeSQL.getOneValue(strSQL);

			// 测试返回null时
			if (exeSQL.mErrors.needDealError()) {
				System.out.println("查询LDMaxNo出错，请稍后!");
				conn.rollback();
				conn.close();
				return null;
			}

			if ((result == null) || result.equals("")) {
				strSQL = "insert into ldmaxno(notype, nolimit, maxno) values('"
						+ cNoType + "', '" + cNoLimit + "', 1)";
				exeSQL = new ExeSQL(conn);
				if (!exeSQL.execUpdateSQL(strSQL)) {
					System.out.println("CreateMaxNo 插入失败，请重试!");
					conn.rollback();
					conn.close();

					return null;
				} else {
					tMaxNo = new BigInteger("1");
				}
			} else {
				strSQL = "update ldmaxno set maxno = maxno + 1 where notype = '"
						+ cNoType + "' and nolimit = '" + cNoLimit + "'";
				exeSQL = new ExeSQL(conn);
				if (!exeSQL.execUpdateSQL(strSQL)) {
					System.out.println("CreateMaxNo 更新失败，请重试!");
					conn.rollback();
					conn.close();

					return null;
				} else {
					// tMaxNo = Integer.parseInt(result) + 1;
					tMaxNo = new BigInteger(result).add(new BigInteger("1"));

				}
			}
			conn.commit();
			conn.close();
		} catch (Exception Ex) {
			try {
				conn.rollback();
				conn.close();

				return null;
			} catch (Exception e1) {
				e1.printStackTrace();

				return null;
			}
		}

		// String tStr = String.valueOf(tMaxNo);
		String tStr = tMaxNo.toString();
		if (!cNoType.equalsIgnoreCase("ORGCOMCODE")
				&& !cNoType.equalsIgnoreCase("SENDCOMCODE")) {
			if (cNoType.equalsIgnoreCase("GRPCONTNO")) { // 8＋2位branch
				// code+6位流水号＋2位续保号码
				tStr = PubFun.LCh(tStr, "0", serialLen - tReturn.length() - 2);
				System.out.println("tStr = " + tStr);
				tReturn = tReturn + tStr + "00";
				System.out.println("tReturn 1 = " + tReturn);
			} else if (cNoType.equalsIgnoreCase("GPROPOSALCONTNO")) { // 4位branch
				// code+5位流水号
				tStr = PubFun.LCh(tStr, "0", serialLen - tReturn.length());
				tReturn = tReturn + tStr;
				System.out.println("tReturn 2 = " + tReturn);
			} else if (cNoType.equalsIgnoreCase("RGTNO")) { // 四位年＋2位branch
				// code＋6位流水号 //
				tStr = PubFun.LCh(tStr, "0", serialLen - tReturn.length());
				System.out.println("tStr= " + tStr);
				tReturn = tReturn + tStr;
				System.out.println("tReturn 3 = " + tReturn);
			} else if(cNoType.equalsIgnoreCase("ActuGetNo")){
				tStr = PubFun.LCh(tStr, "0", serialLen - tReturn.length() - 6) ;
				tReturn = tReturn + tStr;
			}
//liuwd 20070927
			else if(cNoType.equalsIgnoreCase("EDITUSER")){
				tStr = PubFun.LCh(tStr, "0", serialLen - tReturn.length() - 6) ;
				tReturn = tReturn + tStr;
			}
//			年金子保单处理号,zhangjq 2007-11-27
			else if(cNoType.equalsIgnoreCase("GrpContNoSub")){
				tStr = PubFun.LCh(tStr, "0", 4) ;
				tReturn = tReturn + tStr;
			}
			//年金子保单撤销处理号,zhangjq 2007-11-27
			else if(cNoType.equalsIgnoreCase("GrpContNoSubBk")){
				tStr = PubFun.LCh(tStr, "0", 4) ;
				tReturn = tReturn + tStr;
			}
			else { // 还要留两位的88 
				tStr = PubFun.LCh(tStr, "0", serialLen - tReturn.length() - 2);
				tReturn = tReturn + tStr + "88";
				System.out.println("tReturn4 =  " + tReturn);
			}
		} else {
			tStr = PubFun.LCh(tStr, "0", serialLen);
			tReturn = tReturn + tStr;
		}

		return tReturn;
	}

	/**
	 * 功能：产生指定长度的流水号，一个号码类型一个流水
	 * 
	 * @param cNoType
	 *            String 流水号的类型
	 * @param cNoLength
	 *            int 流水号的长度
	 * @return String 返回产生的流水号码
	 */
	public String CreateMaxNo(String cNoType, int cNoLength) {
		if ((cNoType == null) || (cNoType.trim().length() <= 0)
				|| (cNoLength <= 0)) {
			System.out.println("NoType长度错误或NoLength错误");
			return null;
		}

		cNoType = cNoType.toUpperCase();
		String tReturn = "";
		String cNoLimit = "SN";
		// 对上面那种cNoLimit为SN的数据做一个校验，否则会导致数据干扰
		if (cNoType.equalsIgnoreCase("COMMISIONSN")
				|| cNoType.equalsIgnoreCase("GRPNO")
				|| cNoType.equalsIgnoreCase("CUSTOMERNO")
				|| cNoType.equalsIgnoreCase("SUGDATAITEMCODE")
				|| cNoType.equalsIgnoreCase("SUGITEMCODE")
				|| cNoType.equalsIgnoreCase("SUGMODELCODE")
				|| cNoType.equalsIgnoreCase("SUGCODE")) {
			System.out.println("该类型流水号，请采用CreateMaxNo('" + cNoType
					+ "','SN')方式生成");
			return null;
		}

		Connection conn = DBConnPool.getConnection();
		if (conn == null) {
			System.out.println("CreateMaxNo : fail to get db connection");
			return null;
		}

		// int tMaxNo = 0;
		BigInteger tMaxNo = new BigInteger("0");
		tReturn = cNoLimit;

		try {
			// 开始事务
			conn.setAutoCommit(false);

			String strSQL = "select MaxNo from LDMaxNo where notype='"
					+ cNoType + "' and nolimit='" + cNoLimit + "' for update";

			ExeSQL exeSQL = new ExeSQL(conn);
			String result = null;
			result = exeSQL.getOneValue(strSQL);

			if (exeSQL.mErrors.needDealError()) {
				System.out.println("CreateMaxNo 查询失败，请稍后!");
				conn.rollback();
				conn.close();
				return null;
			}

			if ((result == null) || (result.equals(""))) {
				strSQL = "insert into ldmaxno(notype, nolimit, maxno) values('"
						+ cNoType + "', '" + cNoLimit + "', 1)";
				exeSQL = new ExeSQL(conn);
				if (!exeSQL.execUpdateSQL(strSQL)) {
					System.out.println("CreateMaxNo 插入失败，请重试!");
					conn.rollback();
					conn.close();
					return null;
				} else {
					// tMaxNo = 1;
					tMaxNo = new BigInteger("1");
				}
			} else {
				strSQL = "update ldmaxno set maxno = maxno + 1 where notype = '"
						+ cNoType + "' and nolimit = '" + cNoLimit + "'";
				exeSQL = new ExeSQL(conn);
				if (!exeSQL.execUpdateSQL(strSQL)) {
					System.out.println("CreateMaxNo 更新失败，请重试!");
					conn.rollback();
					conn.close();
					return null;
				} else {
					// tMaxNo = Integer.parseInt(result) + 1;
					tMaxNo = new BigInteger(result).add(new BigInteger("1"));
				}
			}

			conn.commit();
			conn.close();
		} catch (Exception Ex) {
			try {
				conn.rollback();
				conn.close();
				return null;
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
		}

		// String tStr = String.valueOf(tMaxNo);
		String tStr = tMaxNo.toString();
		tStr = PubFun.LCh(tStr, "0", cNoLength);
		tReturn = tStr;

		return tReturn;
	}

	public static void main(String[] args) {
		/*
		 * System.out.println(PubFun1.CreateMaxNo("ORGCOMCODE","86"));
		 * System.out.println(PubFun1.CreateMaxNo("GRPCONTNO", "86010101"));
		 * System.out.println(PubFun1.CreateMaxNo("GPROPOSALCONTNO","86010101"));
		 * System.out.println(PubFun1.CreateMaxNo("RGTNO", "86010101"));
		 * System.out.println(PubFun1.CreateMaxNo("EDORGRPNO", "86"));
		 * System.out.println(PubFun1.CreateMaxNo("GCONTNO","86"));
		 * System.out.println(PubFun1.CreateMaxNo("GRPNO", "86"));
		 * System.out.println(PubFun1.CreateMaxNo("PROGRPCONTNO","86010101"));
		 * String a = "8611"; System.out.println(a.substring(0, 4));
		 * System.out.println(PubFun1.CreateMaxNo("CUSTOMERNO", "86"));
		 * 
		 * 
		 * VData tVData = new VData(); TransferData tTransferData =new
		 * TransferData(); tTransferData.setNameAndValue("RGTNO",
		 * "200630000003");
		 * tTransferData.setNameAndValue("GRPCONTNO","83012345700" );
		 * tVData.addElement(tTransferData);
		 * 
		 * System.out.println("++++++++GRTCONTNO=
		 * "+PubFun1.CreateMaxNo("GRPCONTNO", "86",tVData));
		 * System.out.println("+++++++++++CASENO=
		 * "+PubFun1.CreateMaxNo("CASENO", "86",tVData));
		 */

	}
}

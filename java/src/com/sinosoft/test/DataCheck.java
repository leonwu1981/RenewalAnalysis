package com.sinosoft.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;

public class DataCheck implements Runnable {
	private FileWriter fw;

	int count = 1, number;

	public DataCheck(int num) {
		number = num;
		System.out.println("创建线程 " + number);

	}

	public DataCheck() {

	}

	public void run() {
		if (count == 15) {
			return;
		}
		String indexsql = initThread(number);
		datacheckAfterUpdate(indexsql);
		count++;
		}

	public synchronized String initThread(int index) {
		// 执行sql之后的校验标准
		// a) 首先校验新表中的数据是否平
		String indexsql = "";
		switch (index) {
		case 1:
			indexsql = " and organcomcode>='0114' and organcomcode<='1211'";
		//	indexsql = " and organcomcode>='2106' and organcomcode<='2106'";
			break;
		case 2:
			indexsql = " and organcomcode>='1212' and organcomcode<='1214' ";
			break;
		case 3:
			indexsql = " and organcomcode>='1215' and organcomcode<='1223' ";
			break;
		case 4:
			indexsql = " and organcomcode>='12240000' and organcomcode<='1601' ";
			break;
		case 5:
			indexsql = " and organcomcode>='21011443' and organcomcode<='21021419' ";
			break;
		case 6:
			indexsql = " and organcomcode>='21031703' and organcomcode<='21041718' ";
			break;
		case 7:
			indexsql = " and organcomcode>='2106' and organcomcode<='21081707' ";
			break;
		case 8:
			indexsql = " and organcomcode>='21101425' and organcomcode<='2204' ";
			break;
		case 9:
			indexsql = " and organcomcode>='2205' and organcomcode<='2207' ";
			break;
		case 10:
			indexsql = " and organcomcode>='22081410' and organcomcode<='2310' ";
			break;
		case 11:
			indexsql = " and organcomcode>='2401' and organcomcode<='2609' ";
			break;
		case 12:
			indexsql = " and organcomcode>='3001' and organcomcode<='3001' ";
			break;
		case 13:
			indexsql = " and organcomcode>='30021410' and organcomcode<='30021410' ";
			break;
		case 14:
			indexsql = " and organcomcode>='3003' and organcomcode<='4012' ";
			break;
		case 15:
			indexsql = " and organcomcode>='4013' and organcomcode<='5008' ";
			break;
		}
		return indexsql;
	}

	public void datacheckAfterUpdate(String indexsql) {
		String organSql = "select organcomcode, organinnercode, childflag "
				+ "        from lcsendorgan "
				+ "       where grpcontno = '99029288'"
				+ "         and grouplevel = '1'"
				+ "         and organcomcode <> '0000' and organcomcode<>'21031703' " + indexsql + "     order by organcomcode";
		System.out.println(organSql);
		ExeSQL exeSQL = new ExeSQL();
		SSRS ssrs = exeSQL.execSQL(organSql);
		System.out.println("机构数==" + ssrs.getMaxRow());
		int total = ssrs.getMaxRow();
		// List yearMonthList = new ArrayList();
		// yearMonthList.add("200805");
		// yearMonthList.add("200806");
		// 定义要处理的月份

		String[] yearmonnths = new String[] {"200803" };
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 1; i <= ssrs.getMaxRow(); i++) {
			List errors = new ArrayList();
			String organcomcode = ssrs.GetText(i, 1);
			String organinnercode = ssrs.GetText(i, 2);

			String sql = "select organcomcode,organinnercode,childflag from lcsendorgan where organinnercode like '"
					+ organinnercode + "%'";

			ExeSQL exeSQL2 = new ExeSQL();

			SSRS ssrs2 = exeSQL2.execSQL(sql);

			System.out.println("校验到一级机构  " + organcomcode + "   第" + i
					+ "家机构 还有" + (total - i) + "家机构 ");
			for (int m = 1; m <= ssrs2.getMaxRow(); m++) {
				String sendcomcode = ssrs2.GetText(m, 1);
				String sendinnercode = ssrs2.GetText(m, 2);
//				System.out.println("机构  " + m + " " + sendcomcode + " "
//						+ sendinnercode + "  " + ssrs2.getMaxRow());

				String childflag = ssrs2.GetText(m, 3);
				String sendsql = "";
				if (childflag.equals("1")) {
					sendsql = " and sendinnercode like '" + sendinnercode
							+ "%' ";
				}
				// 末级机构
				else if (childflag.equals("0")) {
					sendsql = " and sendcomcode = '" + sendcomcode + "' ";
				}
				for (int j = 0; j < yearmonnths.length; j++) {
					String calmonth = yearmonnths[j];
					// 首先和原来的汇总表校验
					String compersoninfo = "select  nvl(sum(addnum),0),nvl(sum(addmoney),0),"
							+ "nvl(sum(reducenum),0),nvl(sum(reducemoney),0),nvl(sum(supplymoney),0),nvl(sum(deduckmoney),0),"
							+ "nvl(sum(totalmoney),0),nvl(sum(actmoney),0),nvl(sum(lastgetnum),0),nvl(sum(lastgetmoney),0),nvl(sum(getnum),0),nvl(sum(getmoney),0) from lccompersoninfo "
							+ "where calmonth = '" + calmonth + "' " + sendsql;
					String compersoninfobak = "select nvl(sum(addnum),0),nvl(sum(addmoney),0),"
							+ "nvl(sum(reducenum),0),nvl(sum(reducemoney),0),nvl(sum(supplymoney),0),nvl(sum(deduckmoney),0),"
							+ "nvl(sum(totalmoney),0),nvl(sum(actmoney),0),nvl(sum(lastgetnum),0),nvl(sum(lastgetmoney),0),nvl(sum(getnum),0),nvl(sum(getmoney),0) from lccompersoninfo_bak "
							+ "where calmonth = '"
							+ calmonth
							+ "' and sendcomcode='" + sendcomcode + "'";
					SSRS ssrs3 = new SSRS();
					ssrs3 = exeSQL2.execSQL(compersoninfo);
					SSRS ssrs4 = new SSRS();
					ssrs4 = exeSQL2.execSQL(compersoninfobak);
					if (ssrs3.getMaxRow() != 0 && ssrs4.getMaxRow() != 0) {
						if (Double.parseDouble(ssrs3.GetText(1, 1)) != Double
								.parseDouble(ssrs4.GetText(1, 1))
								|| Double.parseDouble(ssrs3.GetText(1, 2)) != Double
										.parseDouble(ssrs4.GetText(1, 2))
								|| Double.parseDouble(ssrs3.GetText(1, 3)) != Double
										.parseDouble(ssrs4.GetText(1, 3))
								|| Double.parseDouble(ssrs3.GetText(1, 4)) != Double
										.parseDouble(ssrs4.GetText(1, 4))
								|| Double.parseDouble(ssrs3.GetText(1, 5)) != Double
										.parseDouble(ssrs4.GetText(1, 5))
								|| Double.parseDouble(ssrs3.GetText(1, 6)) != Double
										.parseDouble(ssrs4.GetText(1, 6))
								|| Double.parseDouble(ssrs3.GetText(1, 7)) != Double
										.parseDouble(ssrs4.GetText(1, 7))
								|| Double.parseDouble(ssrs3.GetText(1, 8)) != Double
										.parseDouble(ssrs4.GetText(1, 8))
								|| Double.parseDouble(ssrs3.GetText(1, 9)) != Double
										.parseDouble(ssrs4.GetText(1, 9))	
								|| Double.parseDouble(ssrs3.GetText(1, 10)) != Double
										.parseDouble(ssrs4.GetText(1, 10))
								|| Double.parseDouble(ssrs3.GetText(1, 11)) != Double
										.parseDouble(ssrs4.GetText(1, 11))
								|| Double.parseDouble(ssrs3.GetText(1, 12)) != Double
										.parseDouble(ssrs4.GetText(1, 12))
						) {
							if (Double.parseDouble(ssrs3.GetText(1, 7)) != Double
									.parseDouble(ssrs3.GetText(1, 8))) {
//								System.out
//										.println("这种情况比较特殊原汇总表的totalmoney和actmoney不相等");

							}
//							System.out.println("出现错误 机构 " + sendcomcode + " "
//									+ calmonth + "的数据不正确 " + sendinnercode);
//							System.out.println(compersoninfo);
//							System.out.println(compersoninfobak);
							errors.add(compersoninfo);
							errors.add(compersoninfobak);

						} else {
//							System.out.println("机构 " + sendcomcode
//									+ " 和原来汇总表检验结束 " + calmonth);

						}
					}
					// end 首先和原来的汇总表校验

					// 和原来的汇总表校验 精确到原来汇总表的每条有效的记录
					if (childflag.equals("0")) {
						String everyRecordSql = "select addnum,addmoney,reducenum,reducemoney,supplymoney,deduckmoney,totalmoney,actmoney,"
								+ "classinfo,contflag,proposalstate,retiretype,"
								+ "sendcomcode,lastgetnum,lastgetmoney,getnum,getmoney "
								+ "from lccompersoninfo where calmonth='"
								+ calmonth
								+ "' and sendcomcode ='"
								+ sendcomcode
								+ "' and (addnum+reducenum>0 or "
								+ "addmoney+reducemoney+supplymoney+deduckmoney>0 "
								+ "or totalmoney<>0 or actmoney<>0 )";
						SSRS everyRecordSqlRS = exeSQL2.execSQL(everyRecordSql);
						for (int k = 1; k <= everyRecordSqlRS.getMaxRow(); k++) {
							String contflagt = "";
							if (everyRecordSqlRS.GetText(k, 10).equals("1")) {
								contflagt = " and contflag in ('0','1')";
							} else {
								contflagt = "and contflag ='"
										+ everyRecordSqlRS.GetText(k, 10)
										+ "' ";
							}
							String sumLccomperbaksql = "select nvl(sum(addnum),0),nvl(sum(addmoney),0),"
									+ "nvl(sum(reducenum),0),nvl(sum(reducemoney),0),nvl(sum(supplymoney),0),"
									+ "nvl(sum(deduckmoney),0),nvl(sum(totalmoney),0),nvl(sum(actmoney),0),nvl(sum(lastgetnum),0),nvl(sum(lastgetmoney),0),nvl(sum(getnum),0),nvl(sum(getmoney),0)  from lccompersoninfo_bak where sendcomcode='"
									+ sendcomcode
									+ "' and calmonth='"
									+ calmonth
									+ "' and classinfo='"
									+ everyRecordSqlRS.GetText(k, 9)
									+ "' and proposalstate='"
									+ everyRecordSqlRS.GetText(k, 11)
									+ "' and retiretype='"
									+ everyRecordSqlRS.GetText(k, 12)
									+ "' "
									+ contflagt;
							SSRS ssrs5 = new SSRS();
							ssrs5 = exeSQL2.execSQL(sumLccomperbaksql);
							if (ssrs5.getMaxRow() != 0) {

								if (Double.parseDouble(everyRecordSqlRS
										.GetText(k, 1)) != Double
										.parseDouble(ssrs5.GetText(1, 1))
										|| Double.parseDouble(everyRecordSqlRS
												.GetText(k, 2)) != Double
												.parseDouble(ssrs5
														.GetText(1, 2))
										|| Double.parseDouble(everyRecordSqlRS
												.GetText(k, 3)) != Double
												.parseDouble(ssrs5
														.GetText(1, 3))
										|| Double.parseDouble(everyRecordSqlRS
												.GetText(k, 4)) != Double
												.parseDouble(ssrs5
														.GetText(1, 4))
										|| Double.parseDouble(everyRecordSqlRS
												.GetText(k, 5)) != Double
												.parseDouble(ssrs5
														.GetText(1, 5))
										|| Double.parseDouble(everyRecordSqlRS
												.GetText(k, 6)) != Double
												.parseDouble(ssrs5
														.GetText(1, 6))
										|| Double.parseDouble(everyRecordSqlRS
												.GetText(k, 7)) != Double
												.parseDouble(ssrs5
														.GetText(1, 7))
										|| Double.parseDouble(everyRecordSqlRS
												.GetText(k, 8)) != Double
												.parseDouble(ssrs5
														.GetText(1, 8))
								        || Double.parseDouble(everyRecordSqlRS
												.GetText(k, 14)) != Double
												.parseDouble(ssrs5
														.GetText(1, 9))
									    || Double.parseDouble(everyRecordSqlRS
												.GetText(k, 15)) != Double
												.parseDouble(ssrs5
														.GetText(1, 10))
										|| Double.parseDouble(everyRecordSqlRS
												.GetText(k, 16)) != Double
												.parseDouble(ssrs5
														.GetText(1, 11))
										|| Double.parseDouble(everyRecordSqlRS
												.GetText(k, 17)) != Double
												.parseDouble(ssrs5
														.GetText(1, 12))
								) {
									errors
											.add("出错了 机构" + sendcomcode + " "
													+ calmonth
													+ " 和原汇总表的数据不符 下面是查询sql");
//									System.out.println("出错了 机构" + sendcomcode
//											+ " " + calmonth
//											+ " 和原汇总表的数据不符 下面是查询sql");
									errors.add(everyRecordSql);
									errors.add(sumLccomperbaksql);
//									System.out.println(everyRecordSql);
//									System.out.println(sumLccomperbaksql);

								}

							}
						}
					}

				}
			}
			if (errors.size() == 0) {
				System.out.println("机构 " + organcomcode + "  没有错误");
			} else {
				System.out.println("机构 " + organcomcode + " 发现错误");
				for (int j = 0; j < errors.size(); j++) {
					System.out.println(errors.get(j));
				}
			}
		}

	}

	public void writeLog(String log) {
		try {
			System.out.println(log);
			fw.write(log);
			fw.write("\r\n");
		} catch (IOException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
	}

	public void unseal(String fileLogPath) {
		try {
			fw = new FileWriter(fileLogPath);
			fw.write("*****\r\n");
		} catch (IOException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
	}

	public void colse() {
		try {
			fw.write("*****");
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		DataCheck dataUpdate = new DataCheck();
		// dataUpdate.datacheckAfterUpdate();

		File path = new File(DataUpdate.class.getResource("/").getFile());
		String folderLogPath = path.getParentFile().getParentFile()
				.getParentFile().toString()
				+ "/logs";
		File folderLog = new File(folderLogPath);
		if (!folderLog.exists()) {
			folderLog.mkdirs();
		}
		String fileLogPath = folderLogPath + "/" + "datacheckt"
				+ PubFun.getCurrentDate() + ".log";
		File fileLog = new File(fileLogPath); // 判断文件是否存在，没有则创建
		if (!fileLog.exists()) {
			try {
				fileLog.createNewFile();
			} catch (IOException e) { // TODO 自动生成 catch 块
				// e.printStackTrace();
			}
		}

		dataUpdate.unseal(fileLogPath); // 保全表LPEdorItem基本校验
		for (int i = 0; i < 15; i++) {
			new Thread(new DataCheck(i + 1)).start();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dataUpdate.colse();

	}

}

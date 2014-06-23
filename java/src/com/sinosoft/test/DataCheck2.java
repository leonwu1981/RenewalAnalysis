package com.sinosoft.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;

public class DataCheck2 implements Runnable {
	private FileWriter fw;

	int count = 16, number;

	public DataCheck2(int num) {
		number = num;
		System.out.println("创建线程 " + number);

	}

	public DataCheck2() {

	}

	public void run() {
//		if (count == 16) {
//			return;
//		}
		String indexsql = initThread(number);
		// datacheckAfterUpdate(indexsql);
		checkDataUpcomChildcom(indexsql);
		count++;
	}

	public synchronized String initThread(int index) {
		// 执行sql之后的校验标准
		// a) 首先校验新表中的数据是否平
		String indexsql = "";
		switch (index) {
		case 1:
			 indexsql = " and organcomcode>='0114' and organcomcode<='1220'";
			//indexsql = " and organcomcode>='21011443' and organcomcode<='21011443'";
			break;
		case 2:
			indexsql = " and organcomcode>='1221' and organcomcode<='1601' ";
			break;
		case 3:
			indexsql = " and organcomcode>='21011443' and organcomcode<='21011443' ";
			break;
		case 4:
			indexsql = " and organcomcode>='21021419' and organcomcode<='21031703' ";
			break;
		case 5:
			indexsql = " and organcomcode>='21041718' and organcomcode<='2107' ";
			break;
		case 6:
			indexsql = " and organcomcode>='21081707' and organcomcode<='21101425' ";
			break;
		case 7:
			indexsql = " and organcomcode>='21111703' and organcomcode<='2112' ";
			break;
		case 8:
			indexsql = " and organcomcode>='2201' and organcomcode<='2204' ";
			break;
		case 9:
			indexsql = " and organcomcode>='2205' and organcomcode<='2304' ";
			break;
		case 10:
			indexsql = " and organcomcode>='2310' and organcomcode<='2501' ";
			break;
		case 11:
			indexsql = " and organcomcode>='25021701' and organcomcode<='2609' ";
			break;
		case 12:
			indexsql = " and organcomcode>='3001' and organcomcode<='3003' ";
			break;
		case 13:
			indexsql = " and organcomcode>='3005' and organcomcode<='40031704' ";
			break;
		case 14:
			indexsql = " and organcomcode>='4004' and organcomcode<='4014' ";
			break;
		case 15:
			indexsql = " and organcomcode>='4020' and organcomcode<='4022' ";
			break;
		case 16:
			indexsql = " and organcomcode>='4023' and organcomcode<='5008' ";
			break;
		}
		return indexsql;
	}

	public void checkDataUpcomChildcom(String indexsql) {

		String organSql = "select organcomcode, organinnercode, childflag ,grouplevel"
				+ "        from lcsendorgan "
				+ "       where grpcontno = '99029288'"
				+ "         and grouplevel = '1'"
				+ "         and organcomcode <> '0000' and childflag='1' "
				+ indexsql + "       order by organcomcode";
		System.out.println(organSql);
		ExeSQL exeSQL = new ExeSQL();
		SSRS ssrs = exeSQL.execSQL(organSql);
		System.out.println("机构数==" + ssrs.getMaxRow());
		int total = ssrs.getMaxRow();
		// List yearMonthList = new ArrayList();
		// yearMonthList.add("200805");
		// yearMonthList.add("200806");
		// 定义要处理的月份

		String[] yearmonnths = new String[] {"200806"};
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

			String sql = "select organcomcode,organinnercode,childflag,grouplevel from lcsendorgan where organinnercode like '"
					+ organinnercode + "%' ";

			ExeSQL exeSQL2 = new ExeSQL();

			SSRS ssrs2 = exeSQL2.execSQL(sql);
			String grouplevel = ssrs2.GetText(1, 4);
			System.out.println("校验到一级机构  " + organcomcode + "   第" + i
					+ "家机构 还有" + (total - i) + "家机构 ");

			for (int m = 1; m <= ssrs2.getMaxRow(); m++) {
				String sendcomcode = ssrs2.GetText(m, 1);
				String sendinnercode = ssrs2.GetText(m, 2);
//				System.out.println("机构  " + m + " " + sendcomcode + " "
//						+ sendinnercode + "  " + ssrs2.getMaxRow());
				// 当前的非末级机构下存在的非末级机构
				String ss = "select organcomcode,organinnercode,childflag,grouplevel from lcsendorgan where upcomcode='"
						+ sendcomcode + "' ";
			//	System.out.println(ss);
				SSRS ssrs5 = exeSQL2.execSQL(ss);
				String sendsql = "";
				if (ssrs5.getMaxRow() != 0) {
					StringBuffer buffer = new StringBuffer();
					buffer.append("(");

					for (int j = 1; j <= ssrs5.getMaxRow(); j++) {
						String comcode = ssrs5.GetText(j, 1);
						String innercode = ssrs5.GetText(j, 2);
						// 效率上的考虑，采取单个机构查询然后求和
						//
						if (j == ssrs5.getMaxRow()) {
							buffer.append("'" + comcode + "'");
						} else {
							buffer.append("'" + comcode + "',");
						}
					}
					buffer.append(")");
			//		System.out.println(buffer.toString());
					sendsql = "and sendcomcode in " + buffer.toString();
					for (int j = 0; j < yearmonnths.length; j++) {
						String calmonth = yearmonnths[j];
						String compersoninfo = "select nvl(sum(addnum),0),nvl(sum(addmoney),0),"
								+ "nvl(sum(reducenum),0),nvl(sum(reducemoney),0),nvl(sum(supplymoney),0),nvl(sum(deduckmoney),0),"
								+ "nvl(sum(totalmoney),0),nvl(sum(actmoney),0) from lccompersoninfo_bak "
								+ "where calmonth = '" + calmonth
								+ "' and sendcomcode='" + sendcomcode + "'";
				//		System.out.println(compersoninfo);
						String compersoninfobak = "select nvl(sum(addnum),0),nvl(sum(addmoney),0),"
								+ "nvl(sum(reducenum),0),nvl(sum(reducemoney),0),nvl(sum(supplymoney),0),nvl(sum(deduckmoney),0),"
								+ "nvl(sum(totalmoney),0),nvl(sum(actmoney),0) from lccompersoninfo_bak "
								+ "where calmonth = '"
								+ calmonth
								+ "'"
								+ sendsql;
			//			System.out.println(compersoninfobak);
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
//									|| Double.parseDouble(ssrs3.GetText(1, 8)) != Double
//											.parseDouble(ssrs4.GetText(1, 8))) {
																			) {
//								System.out.println("出现错误 机构 " + sendcomcode
//										+ " " + calmonth + "的数据不正确 "
//										+ sendinnercode);
//								System.out.println(compersoninfo);
//								System.out.println(compersoninfobak);
								errors.add(compersoninfo);
								errors.add(compersoninfobak);

							} else {
//								System.out.println("机构 " + sendcomcode
//										+ " 和原来汇总表检验结束 " + calmonth);
							}
						}
						// end 首先和原来的汇总表校验

					}
				} else {
					// break;
					// sendsql = "and sendinnercode like '" + sendinnercode
					// + "%' and sendcomcode <>'" + sendcomcode + "'";
				}
				// String childflag = ssrs2.GetText(m, 3);

				// String grouplevel = "";
				// grouplevel = ssrs2.GetText(m, 4);
				// if (childflag.equals("1")) {
				// sendsql = " and sendinnercode like '" + sendinnercode + "%'
				// ";

				// }
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

	public void test() {
		ExeSQL exeSQL2 = new ExeSQL();
		// String ss = "select organcomcode,organinnercode,childflag,grouplevel
		// from lcsendorgan where organinnercode like '8608400001%' and
		// childflag='1' and upcomcode='2101'";
		String ss = "select organcomcode,organinnercode,childflag,grouplevel from lcsendorgan where  childflag='1' and upcomcode='2101'";
		System.out.println(ss);
		SSRS ssrs5 = exeSQL2.execSQL(ss);
		if (ssrs5.getMaxRow() != 0) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("(");
			for (int j = 1; j <= ssrs5.getMaxRow(); j++) {
				String comcode = ssrs5.GetText(j, 1);
				String innercode = ssrs5.GetText(j, 2);
				if (j == ssrs5.getMaxRow()) {
					buffer.append("'" + comcode + "'");
				} else {
					buffer.append("'" + comcode + "',");
				}
			}
			buffer.append(")");
			System.out.println(buffer.toString());
		}
	}

	public static void main(String[] args) {
	//	DataCheck2 dataUpdate = new DataCheck2();
		// dataUpdate.test();
		File path = new File(DataUpdate.class.getResource("/").getFile());
		String folderLogPath = path.getParentFile().getParentFile()
				.getParentFile().toString()
				+ "/logs";
		File folderLog = new File(folderLogPath);
		if (!folderLog.exists()) {
			folderLog.mkdirs();
		}
		String fileLogPath = folderLogPath + "/" + "datacheckup"
				+ PubFun.getCurrentDate() + ".log";
		File fileLog = new File(fileLogPath); // 判断文件是否存在，没有则创建
		if (!fileLog.exists()) {
			try {
				fileLog.createNewFile();
			} catch (IOException e) { // TODO 自动生成 catch 块
				// e.printStackTrace();
			}
		}

	//	dataUpdate.unseal(fileLogPath);
		for (int i = 0; i < 16; i++) {
			new Thread(new DataCheck2(i + 1)).start();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	//	dataUpdate.colse();

	}

}
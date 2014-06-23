/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sinosoft.lis.db.LDSysVarDB;
import com.sinosoft.lis.db.LMRiskPayDB;
import com.sinosoft.lis.vschema.LMRiskPaySet;
import com.sinosoft.utility.*;

/**
 * <p>
 * Title: Web业务系统
 * </p>
 * <p>
 * Description:业务系统的公共业务处理函数 该类包含所有业务处理中的公共函数，和以前系统中的funpub.4gl
 * 文件相对应。在这个类中，所有的函数都采用Static的类型，所有需要的数据都是 通过参数传入的，在本类中不采用通过属性传递数据的方法。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: Sinosoft
 * </p>
 * 
 * @author YT
 * @version 1.0
 */
public class PubFun {
	private final static int MAX_YEAR = 9999;

	private final static int MIN_YEAR = 1800;

	private final static int MAX_MONTH = 12;

	private final static int MIN_MONTH = 1;

	private final static int MAX_DAY = 31;

	private final static int MIN_DAY = 1;

	// private final static int MAX_HOUR = 23;
	// private final static int MAX_MINUTE = 59;
	// private final static int MAX_SEC = 59;
	private final static String DATE_LIST = "0123456789-";

	public PubFun() {
	}

	/**
	 * 计算日期的函数 author: HST 参照日期指当按照年月进行日期的计算的时候，参考的日期，如下例，结果返回2002-03-31
	 * <p>
	 * <b>Example: </b>
	 * <p>
	 * <p>
	 * FDate tD=new FDate();
	 * <p>
	 * <p>
	 * Date baseDate =new Date();
	 * <p>
	 * <p>
	 * baseDate=tD.getDate("2000-02-29");
	 * <p>
	 * <p>
	 * Date comDate =new Date();
	 * <p>
	 * <p>
	 * comDate=tD.getDate("1999-12-31");
	 * <p>
	 * <p>
	 * int inteval=1;
	 * <p>
	 * <p>
	 * String tUnit="M";
	 * <p>
	 * <p>
	 * Date tDate =new Date();
	 * <p>
	 * <p>
	 * tDate=PubFun.calDate(baseDate,inteval,tUnit,comDate);
	 * <p>
	 * <p>
	 * System.out.println(tDate.toString());
	 * <p>
	 * 
	 * @param baseDate
	 *            起始日期
	 * @param interval
	 *            时间间隔
	 * @param unit
	 *            时间间隔单位
	 * @param compareDate
	 *            参照日期
	 * @return Date类型变量
	 */
	public static Date calDate(Date baseDate, int interval, String unit,
			Date compareDate) {
		Date returnDate = null;

		GregorianCalendar mCalendar = new GregorianCalendar();
		mCalendar.setTime(baseDate);
		if (unit.equals("Y")) {
			mCalendar.add(Calendar.YEAR, interval);
		}
		if (unit.equals("M")) {
			mCalendar.add(Calendar.MONTH, interval);
		}
		if (unit.equals("D")) {
			mCalendar.add(Calendar.DATE, interval);
		}
		if (compareDate != null) {
			GregorianCalendar cCalendar = new GregorianCalendar();
			cCalendar.setTime(compareDate);

			int mYears = mCalendar.get(Calendar.YEAR);
			int mMonths = mCalendar.get(Calendar.MONTH);
			int cMonths = cCalendar.get(Calendar.MONTH);
			int cDays = cCalendar.get(Calendar.DATE);

			if (unit.equals("Y")) {
				cCalendar.set(mYears, cMonths, cDays);
				if (cCalendar.before(mCalendar)) {
					mCalendar.set(mYears + 1, cMonths, cDays);
					returnDate = mCalendar.getTime();
				} else {
					returnDate = cCalendar.getTime();
				}
			}
			if (unit.equals("M")) {
				cCalendar.set(mYears, mMonths, cDays);
				if (cCalendar.before(mCalendar)) {
					mCalendar.set(mYears, mMonths + 1, cDays);
					returnDate = mCalendar.getTime();
				} else {
					returnDate = cCalendar.getTime();
				}
			}
			if (unit.equals("D")) {
				returnDate = mCalendar.getTime();
			}
		} else {
			returnDate = mCalendar.getTime();
		}

		return returnDate;
	}

	/**
	 * 重载计算日期，参数见楼上，add by Minim
	 * 
	 * @param baseDate
	 *            String
	 * @param interval
	 *            int
	 * @param unit
	 *            String
	 * @param compareDate
	 *            String
	 * @return String
	 */
	public static String calDate(String baseDate, int interval, String unit,
			String compareDate) {
		try {
			FDate tFDate = new FDate();
			Date bDate = tFDate.getDate(baseDate);
			Date cDate = tFDate.getDate(compareDate);
			return tFDate.getString(calDate(bDate, interval, unit, cDate));
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 方法使用和作用同(Date baseDate, int interval, String unit, Date
	 * compareDate),中意特殊要求 计算终止日期是为2006-12-31 calDate()为2007-1-1 add by 张阔
	 */
	public static Date calDateZY(Date baseDate, int interval, String unit,
			Date compareDate) {
		Date returnDate = null;

		GregorianCalendar mCalendar = new GregorianCalendar();
		mCalendar.setTime(baseDate);
		if (unit.equals("Y")) {
			mCalendar.add(Calendar.YEAR, interval);
		}
		if (unit.equals("M")) {
			mCalendar.add(Calendar.MONTH, interval);
		}
		if (unit.equals("D")) {
			mCalendar.add(Calendar.DATE, interval);
		}
		mCalendar.add(Calendar.DATE, -1);
		if (compareDate != null) {
			GregorianCalendar cCalendar = new GregorianCalendar();
			cCalendar.setTime(compareDate);

			int mYears = mCalendar.get(Calendar.YEAR);
			int mMonths = mCalendar.get(Calendar.MONTH);
			int cMonths = cCalendar.get(Calendar.MONTH);
			int cDays = cCalendar.get(Calendar.DATE);

			if (unit.equals("Y")) {
				cCalendar.set(mYears, cMonths, cDays);
				if (cCalendar.before(mCalendar)) {
					mCalendar.set(mYears + 1, cMonths, cDays);
					returnDate = mCalendar.getTime();
				} else {
					returnDate = cCalendar.getTime();
				}
			}
			if (unit.equals("M")) {
				cCalendar.set(mYears, mMonths, cDays);
				if (cCalendar.before(mCalendar)) {
					mCalendar.set(mYears, mMonths + 1, cDays);
					returnDate = mCalendar.getTime();
				} else {
					returnDate = cCalendar.getTime();
				}
			}
			if (unit.equals("D")) {
				returnDate = mCalendar.getTime();
			}
		} else {
			returnDate = mCalendar.getTime();
		}

		return returnDate;
	}

	/**
	 * 重载计算日期，参数见楼上，add by 张阔
	 * 
	 * @param baseDate
	 *            String
	 * @param interval
	 *            int
	 * @param unit
	 *            String
	 * @param compareDate
	 *            String
	 * @return String
	 */
	public static String calDateZY(String baseDate, int interval, String unit,
			String compareDate) {
		try {
			FDate tFDate = new FDate();
			Date bDate = tFDate.getDate(baseDate);
			Date cDate = tFDate.getDate(compareDate);
			return tFDate.getString(calDateZY(bDate, interval, unit, cDate));
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 通过起始日期和终止日期计算以时间间隔单位为计量标准的时间间隔 author: HST
	 * <p>
	 * <b>Example: </b>
	 * <p>
	 * <p>
	 * 参照calInterval(String cstartDate, String cendDate, String
	 * unit)，前两个变量改为日期型即可
	 * <p>
	 * 
	 * @param startDate
	 *            起始日期，Date变量
	 * @param endDate
	 *            终止日期，Date变量
	 * @param unit
	 *            时间间隔单位，可用值("Y"--年 "M"--月 "D"--日)
	 * @return 时间间隔,整形变量int
	 */
	public static int calInterval(Date startDate, Date endDate, String unit) {
		int interval = 0;

		GregorianCalendar sCalendar = new GregorianCalendar();
		sCalendar.setTime(startDate);
		int sYears = sCalendar.get(Calendar.YEAR);
		int sMonths = sCalendar.get(Calendar.MONTH);
		int sDays = sCalendar.get(Calendar.DAY_OF_MONTH);
		int sDaysOfYear = sCalendar.get(Calendar.DAY_OF_YEAR);

		GregorianCalendar eCalendar = new GregorianCalendar();
		eCalendar.setTime(endDate);
		int eYears = eCalendar.get(Calendar.YEAR);
		int eMonths = eCalendar.get(Calendar.MONTH);
		int eDays = eCalendar.get(Calendar.DAY_OF_MONTH);
		int eDaysOfYear = eCalendar.get(Calendar.DAY_OF_YEAR);

		if (unit.equals("Y")) {
			interval = eYears - sYears;
			if (eMonths < sMonths) {
				interval--;
			} else {
				if (eMonths == sMonths && eDays < sDays) {
					interval--;
					if (eMonths == 1) {
						// 如果同是2月，校验润年问题
						if ((sYears % 4) == 0 && (eYears % 4) != 0) {
							// 如果起始年是润年，终止年不是润年
							if (eDays == 28) {
								// 如果终止年不是润年，且2月的最后一天28日，那么补一
								interval++;
							}
						}
					}
				}
			}
		}
		if (unit.equals("M")) {
			interval = eYears - sYears;
			interval *= 12;
			interval += eMonths - sMonths;
			if (eDays < sDays) {
				interval--;
				// eDays如果是月末，则认为是满一个月
				int maxDate = eCalendar.getActualMaximum(Calendar.DATE);
				if (eDays == maxDate) {
					interval++;
				}
			}
		}
		if (unit.equals("D")) {
			interval = eYears - sYears;
			interval *= 365;
			interval += eDaysOfYear - sDaysOfYear;

			// 处理润年
			int n = 0;
			eYears--;
			if (eYears > sYears) {
				int i = sYears % 4;
				if (i == 0) {
					sYears++;
					n++;
				}
				int j = (eYears) % 4;
				if (j == 0) {
					eYears--;
					n++;
				}
				n += (eYears - sYears) / 4;
			}
			if (eYears == sYears) {
				int i = sYears % 4;
				if (i == 0) {
					n++;
				}
			}
			interval += n;
		}
		return interval;
	}

	/**
	 * 通过起始日期和终止日期计算以时间间隔单位为计量标准的时间间隔，舍弃法 author: HST
	 * 起始日期，(String,格式："YYYY-MM-DD")
	 * 
	 * @param cstartDate
	 *            String 终止日期，(String,格式："YYYY-MM-DD")
	 * @param cendDate
	 *            String 时间间隔单位，可用值("Y"--年 "M"--月 "D"--日)
	 * @param unit
	 *            String 时间间隔,整形变量int
	 * @return int
	 */
	public static int calInterval(String cstartDate, String cendDate,
			String unit) {
		FDate fDate = new FDate();
		Date startDate = fDate.getDate(cstartDate);
		Date endDate = fDate.getDate(cendDate);
		if (fDate.mErrors.needDealError()) {
			return 0;
		}

		int interval = 0;

		GregorianCalendar sCalendar = new GregorianCalendar();
		sCalendar.setTime(startDate);
		int sYears = sCalendar.get(Calendar.YEAR);
		int sMonths = sCalendar.get(Calendar.MONTH);
		int sDays = sCalendar.get(Calendar.DAY_OF_MONTH);
		int sDaysOfYear = sCalendar.get(Calendar.DAY_OF_YEAR);

		GregorianCalendar eCalendar = new GregorianCalendar();
		eCalendar.setTime(endDate);
		int eYears = eCalendar.get(Calendar.YEAR);
		int eMonths = eCalendar.get(Calendar.MONTH);
		int eDays = eCalendar.get(Calendar.DAY_OF_MONTH);
		int eDaysOfYear = eCalendar.get(Calendar.DAY_OF_YEAR);

		if (StrTool.cTrim(unit).equals("Y")) {
			interval = eYears - sYears;

			if (eMonths < sMonths) {
				interval--;
			} else {
				if (eMonths == sMonths && eDays < sDays) {
					interval--;
					if (eMonths == 1) {
						// 如果同是2月，校验润年问题
						if ((sYears % 4) == 0 && (eYears % 4) != 0) {
							// 如果起始年是润年，终止年不是润年
							if (eDays == 28) {
								// 如果终止年不是润年，且2月的最后一天28日，那么补一
								interval++;
							}
						}
					}
				}
			}
		}
		if (StrTool.cTrim(unit).equals("M")) {
			interval = eYears - sYears;
			interval *= 12;
			interval += eMonths - sMonths;

			if (eDays < sDays) {
				interval--;
				// eDays如果是月末，则认为是满一个月
				int maxDate = eCalendar.getActualMaximum(Calendar.DATE);
				if (eDays == maxDate) {
					interval++;
				}
			}
		}
		if (StrTool.cTrim(unit).equals("D")) {
			interval = eYears - sYears;
			interval *= 365;
			interval += eDaysOfYear - sDaysOfYear;

			// 处理润年
			int n = 0;
			eYears--;
			if (eYears > sYears) {
				int i = sYears % 4;
				if (i == 0) {
					sYears++;
					n++;
				}
				int j = (eYears) % 4;
				if (j == 0) {
					eYears--;
					n++;
				}
				n += (eYears - sYears) / 4;
			}
			if (eYears == sYears) {
				int i = sYears % 4;
				if (i == 0) {
					n++;
				}
			}
			interval += n;
		}
		return interval;
	}

	/**
	 * 通过起始日期和终止日期计算以时间间隔单位为计量标准的时间间隔，约进法 author: YangZhao，Minim
	 * 起始日期，(String,格式："YYYY-MM-DD")
	 * 
	 * @param cstartDate
	 *            String 终止日期，(String,格式："YYYY-MM-DD")
	 * @param cendDate
	 *            String 时间间隔单位，可用值("Y"--年 "M"--月 "D"--日)
	 * @param unit
	 *            String 时间间隔,整形变量int
	 * @return int
	 */
	public static int calInterval2(String cstartDate, String cendDate,
			String unit) {
		FDate fDate = new FDate();
		Date startDate = fDate.getDate(cstartDate);
		Date endDate = fDate.getDate(cendDate);
		if (fDate.mErrors.needDealError()) {
			return 0;
		}

		int interval = 0;

		GregorianCalendar sCalendar = new GregorianCalendar();
		sCalendar.setTime(startDate);
		int sYears = sCalendar.get(Calendar.YEAR);
		int sMonths = sCalendar.get(Calendar.MONTH);
		int sDays = sCalendar.get(Calendar.DAY_OF_MONTH);
		int sDaysOfYear = sCalendar.get(Calendar.DAY_OF_YEAR);

		GregorianCalendar eCalendar = new GregorianCalendar();
		eCalendar.setTime(endDate);
		int eYears = eCalendar.get(Calendar.YEAR);
		int eMonths = eCalendar.get(Calendar.MONTH);
		int eDays = eCalendar.get(Calendar.DAY_OF_MONTH);
		int eDaysOfYear = eCalendar.get(Calendar.DAY_OF_YEAR);

		if (StrTool.cTrim(unit).equals("Y")) {
			interval = eYears - sYears;

			if (eMonths > sMonths) {
				interval++;
			} else {
				if (eMonths == sMonths && eDays > sDays) {
					interval++;
					if (eMonths == 1) {
						// 如果同是2月，校验润年问题
						if ((sYears % 4) == 0 && (eYears % 4) != 0) {
							// 如果起始年是润年，终止年不是润年
							if (eDays == 28) {
								// 如果终止年不是润年，且2月的最后一天28日，那么减一
								interval--;
							}
						}
					}
				}
			}
		}
		if (StrTool.cTrim(unit).equals("M")) {
			interval = eYears - sYears;
			interval *= 12;
			interval += eMonths - sMonths;

			if (eDays > sDays) {
				interval++;
				// 如果起始日期为月末，则该减掉一个月
				int maxDate = sCalendar.getActualMaximum(Calendar.DATE);
				if (sDays == maxDate) {
					interval--;
				}
			}
		}
		if (StrTool.cTrim(unit).equals("D")) {
			interval = eYears - sYears;
			interval *= 365;
			interval += eDaysOfYear - sDaysOfYear;

			// 处理润年
			int n = 0;
			eYears--;
			if (eYears > sYears) {
				int i = sYears % 4;
				if (i == 0) {
					sYears++;
					n++;
				}
				int j = (eYears) % 4;
				if (j == 0) {
					eYears--;
					n++;
				}
				n += (eYears - sYears) / 4;
			}
			if (eYears == sYears) {
				int i = sYears % 4;
				if (i == 0) {
					n++;
				}
			}
			interval += n;
		}
		return interval;
	}

	/**
	 * 通过传入的日期可以得到所在月的第一天和最后一天的日期 author: LH 日期，(String,格式："YYYY-MM-DD")
	 * 
	 * @param tDate
	 *            String 本月开始和结束日期，返回String[2]
	 * @return String[]
	 */
	public static String[] calFLDate(String tDate) {
		String MonDate[] = new String[2];
		FDate fDate = new FDate();
		Date CurDate = fDate.getDate(tDate);
		GregorianCalendar mCalendar = new GregorianCalendar();
		mCalendar.setTime(CurDate);
		int Years = mCalendar.get(Calendar.YEAR);
		int Months = mCalendar.get(Calendar.MONTH);
		int FirstDay = mCalendar.getActualMinimum(Calendar.DAY_OF_MONTH);
		int LastDay = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		mCalendar.set(Years, Months, FirstDay);
		MonDate[0] = fDate.getString(mCalendar.getTime());
		mCalendar.set(Years, Months, LastDay);
		MonDate[1] = fDate.getString(mCalendar.getTime());
		return MonDate;
	}

	/**
	 * 通过传入的日期可以得到宽限期止期(包括延长宽限期) 起始日期，(String,格式："YYYY-MM-DD")
	 * 
	 * @param startDate
	 *            String
	 * @param strRiskCode
	 *            String
	 * @return String
	 */
	public static String calLapseDate(String startDate, String strRiskCode) {
		String returnDate = "";
		// Date tLapseDate = null;
		FDate tFDate = new FDate();
		int nDates;
		int nExtendLapseDates;

		// 检查输入数据完整性
		if ((startDate == null) || startDate.trim().equals("")) {
			System.out.println("没有起始日期,计算宽限止期失败!");
			return returnDate;
		}

		// 获取险种交费失效描述
		LMRiskPayDB tLMRiskPayDB = new LMRiskPayDB();
		tLMRiskPayDB.setRiskCode(strRiskCode);
		LMRiskPaySet tLMRiskPaySet = tLMRiskPayDB.query();

		if (tLMRiskPaySet.size() > 0) {
			if ((tLMRiskPaySet.get(1).getGracePeriodUnit() == null)
					|| tLMRiskPaySet.get(1).getGracePeriodUnit().equals("")) {
				// 设置宽限期为默认值
				System.out.println("缺少险种交费失效描述!按默认值计算");
				nDates = 60;
				returnDate = calDate(startDate, nDates, "D", null);
			} else {
				// 取得指定宽限期
				nDates = tLMRiskPaySet.get(1).getGracePeriod();
				returnDate = calDate(startDate, nDates, tLMRiskPaySet.get(1)
						.getGracePeriodUnit(), null);
				// jdk1.4自带的方法，根据－拆分字符串到数组
				// String[] tDate = returnDate.split("-");
				// 按月进位，舍弃日精度
				if (tLMRiskPaySet.get(1).getGraceDateCalMode().equals("1")) {
					// 对日期的操作，最好使用Calendar方法
					GregorianCalendar tCalendar = new GregorianCalendar();
					tCalendar.setTime(tFDate.getDate(returnDate));
					// 月份进位，舍弃日精度
					tCalendar.set(tCalendar.get(Calendar.YEAR), tCalendar
							.get(Calendar.MONTH) + 1, 1);
					returnDate = tFDate.getString(tCalendar.getTime());
				}

				// 按年进位，只舍弃了日精度，不舍弃月精度
				if (tLMRiskPaySet.get(1).getGraceDateCalMode().equals("2")) {
					// 对日期的操作，最好使用Calendar方法
					GregorianCalendar tCalendar = new GregorianCalendar();
					tCalendar.setTime(tFDate.getDate(returnDate));
					// 年份进位，舍弃日精度，不舍弃月精度
					tCalendar.set(tCalendar.get(Calendar.YEAR) + 1, tCalendar
							.get(Calendar.MONTH), 1);
					returnDate = tFDate.getString(tCalendar.getTime());
				}
			}
		} else {
			// 设置宽限期为默认值
			System.out.println("没有险种交费失效描述!按默认值计算");
			nDates = 60;
			returnDate = calDate(startDate, nDates, "D", null);
		}

		// 取得宽限期延长期
		LDSysVarDB tLDSysVarDB = new LDSysVarDB();
		tLDSysVarDB.setSysVar("ExtendLapseDates");
		if (!tLDSysVarDB.getInfo()) {
			nExtendLapseDates = 0;
		} else {
			nExtendLapseDates = Integer.parseInt(tLDSysVarDB.getSchema()
					.getSysVarValue());
			returnDate = calDate(returnDate, nExtendLapseDates, "D", null);
		}

		return returnDate;
	}

	/**
	 * 得到默认的JDBCUrl
	 * 
	 * @return JDBCUrl
	 */
	public static JdbcUrl getDefaultUrl() {
		JdbcUrl tUrl = new JdbcUrl();
		return tUrl;
	}

	/**
	 * 将字符串补数,将sourString的<br>
	 * 后面</br>用cChar补足cLen长度的字符串,如果字符串超长，则不做处理
	 * <p>
	 * <b>Example: </b>
	 * <p>
	 * <p>
	 * RCh("Minim", "0", 10) returns "Minim00000"
	 * <p>
	 * 
	 * @param sourString
	 *            源字符串
	 * @param cChar
	 *            补数用的字符
	 * @param cLen
	 *            字符串的目标长度
	 * @return 字符串
	 */
	public static String RCh(String sourString, String cChar, int cLen) {
		int tLen = sourString.length();
		int i, iMax;
		StringBuffer tReturn = new StringBuffer();
		if (tLen >= cLen) {
			return sourString;
		}
		iMax = cLen - tLen;
		tReturn.append(sourString.trim());
		for (i = 0; i < iMax; i++) {
			tReturn.append(cChar);
		}
		return tReturn.toString();
	}

	/**
	 * 将字符串补数,将sourString的<br>
	 * 前面</br>用cChar补足cLen长度的字符串,如果字符串超长，则不做处理
	 * <p>
	 * <b>Example: </b>
	 * <p>
	 * <p>
	 * LCh("Minim", "0", 10) returns "00000Minim"
	 * <p>
	 * 
	 * @param sourString
	 *            源字符串
	 * @param cChar
	 *            补数用的字符
	 * @param cLen
	 *            字符串的目标长度
	 * @return 字符串
	 */
	public static String LCh(String sourString, String cChar, int cLen) {
		int tLen = sourString.length();
		int i, iMax;
		StringBuffer tReturn = new StringBuffer();
		if (tLen >= cLen) {
			return sourString;
		}
		iMax = cLen - tLen;
		for (i = 0; i < iMax; i++) {
			tReturn.append(cChar);
		}
		tReturn.append(sourString.trim());
		return tReturn.toString();
	}

	/**
	 * 比较获取两天中较后的一天
	 * 
	 * @param date1
	 *            String
	 * @param date2
	 *            String
	 * @return String
	 */
	public static String getLaterDate(String date1, String date2) {
		try {
			date1 = StrTool.cTrim(date1);
			date2 = StrTool.cTrim(date2);
			if (date1.equals("")) {
				return date2;
			}
			if (date2.equals("")) {
				return date1;
			}
			FDate fd = new FDate();
			Date d1 = fd.getDate(date1);
			Date d2 = fd.getDate(date2);
			if (d1.after(d2)) {
				return date1;
			}
			return date2;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}

	/**
	 * 比较获取两天中较早的一天
	 * 
	 * @param date1
	 *            String
	 * @param date2
	 *            String
	 * @return String
	 */
	public static String getBeforeDate(String date1, String date2) {
		try {
			date1 = StrTool.cTrim(date1);
			date2 = StrTool.cTrim(date2);
			if (date1.equals("")) {
				return date2;
			}
			if (date2.equals("")) {
				return date1;
			}
			FDate fd = new FDate();
			Date d1 = fd.getDate(date1);
			Date d2 = fd.getDate(date2);
			if (d1.before(d2)) {
				return date1;
			}
			return date2;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}

	/**
	 * 得到当前系统日期 author: YT
	 * 
	 * @return 当前日期的格式字符串,日期格式为"yyyy-MM-dd"
	 */
	public static String getCurrentDate() 
	{
		GregorianCalendar tGCalendar = new GregorianCalendar();
    	StringBuffer tStringBuffer = new StringBuffer(10);
        int sYears = tGCalendar.get(Calendar.YEAR);
        tStringBuffer.append(sYears);
        tStringBuffer.append('-');
        int sMonths = tGCalendar.get(Calendar.MONTH)+1;
        if(sMonths < 10){
        	tStringBuffer.append('0');
        }
        tStringBuffer.append(sMonths);
        tStringBuffer.append('-');
        int sDays = tGCalendar.get(Calendar.DAY_OF_MONTH);
        if(sDays < 10){
        	tStringBuffer.append('0');
        }
        tStringBuffer.append(sDays);
        String tString = tStringBuffer.toString();
    	return tString;
	
	}

	/**
	 * 得到当前系统时间 author: YT
	 * 
	 * @return 当前时间的格式字符串，时间格式为"HH:mm:ss"
	 */
	public static String getCurrentTime() 
	{
		GregorianCalendar tGCalendar = new GregorianCalendar();
   	    StringBuffer tStringBuffer = new StringBuffer(8);
        int sHOUR = tGCalendar.get(Calendar.HOUR_OF_DAY);
        if(sHOUR < 10){
       	 tStringBuffer.append('0');
        }
        tStringBuffer.append(sHOUR);
        tStringBuffer.append(':');
        int sMINUTE = tGCalendar.get(Calendar.MINUTE);
        if(sMINUTE < 10){
       	 tStringBuffer.append('0');
        }
        tStringBuffer.append(sMINUTE);
        tStringBuffer.append(':');
        int sSECOND = tGCalendar.get(Calendar.SECOND);
        if(sSECOND < 10){
       	 tStringBuffer.append('0');
        }
        tStringBuffer.append(sSECOND);
        String tString = tStringBuffer.toString();
        return tString;

	}

	/**
	 * 得到流水号前导 author: YT
	 * 
	 * @param comCode
	 *            机构代码
	 * @return 流水号的前导字符串
	 */
	public static String getNoLimit(String comCode) {
		comCode = comCode.trim();
		int tLen = comCode.length();
		if (tLen > 6) {
			comCode = comCode.substring(0, 6);
		}
		if (tLen < 6) {
			comCode = RCh(comCode, "0", 6);
		}
		StringBuffer tString = new StringBuffer();
		tString.append(comCode);
		tString.append(getCurrentDate().substring(0, 4));

		return tString.toString();
	}

	/**
	 * picc获取管理机构，截取管理代码的第3-6位（二级机构+三级机构） 再加上日期编码的两位年两位月日日 052203
	 * 
	 * @param comCode
	 *            String
	 * @return String
	 */
	public static String getPiccNoLimit(String comCode) {
		comCode = comCode.trim();
		// System.out.println("comCode :" + comCode);
		int tLen = comCode.length();
		if (tLen == 8) {
			comCode = comCode.substring(2, 6);
		}
		if (tLen == 4) {
			// comCode = comCode.substring(2, 4) + "00";
			comCode = comCode.substring(2, 4);
		}
		// System.out.println("SubComCode :" + comCode);
		StringBuffer tString = new StringBuffer();
		// tString = comCode + getCurrentDate().substring(2, 4) +
		// getCurrentDate().substring(5
		// , 7) + getCurrentDate().substring(8, 10);
		tString.append(comCode);
		if (tLen == 4) {
			tString.append("00");
		}
		tString.append(getCurrentDate().substring(2, 4));
		tString.append(getCurrentDate().substring(5, 7));
		tString.append(getCurrentDate().substring(8, 10));
		// System.out.println("PubFun getPiccNoLimit : " + tString);
		return tString.toString();
	}

	/**
	 * 该函数得到c_Str中的第c_i个以c_Split分割的字符串
	 * 
	 * @param c_Str
	 *            目标字符串
	 * @param c_i
	 *            位置
	 * @param c_Split
	 *            分割符
	 * @return 如果发生异常，则返回空
	 */
	public static String getStr(String c_Str, int c_i, String c_Split) {
		String t_Str1 = "";
		String t_Str2 = "";
		String t_strOld = "";
		int i = 0;
		int i_Start = 0;
		// int j_End = 0;
		t_Str1 = c_Str;
		t_Str2 = c_Split;
		i = 0;
		try {
			while (i < c_i) {
				i_Start = t_Str1.indexOf(t_Str2, 0);
				if (i_Start >= 0) {
					i += 1;
					t_strOld = t_Str1;
					t_Str1 = t_Str1.substring(i_Start + t_Str2.length(), t_Str1
							.length());
				} else {
					if (i != c_i - 1) {
						t_Str1 = "";
					}
					break;
				}
			}

			if (i_Start >= 0) {
				t_Str1 = t_strOld.substring(0, i_Start);
			}
		} catch (Exception ex) {
			t_Str1 = "";
		}
		return t_Str1;
	}

	/**
	 * 把数字金额转换为中文大写金额 author: HST
	 * 
	 * @param money
	 *            数字金额(double)
	 * @return 中文大写金额(String)
	 */
	public static String getChnMoney(double money) {
		String ChnMoney = "";
		String s0 = "";

		// 在原来版本的程序中，getChnMoney(585.30)得到的数据是585.29。

		if (money == 0.0) {
			ChnMoney = "零元整";
			return ChnMoney;
		}

		if (money < 0) {
			s0 = "负";
			money *= (-1);
		}

		String sMoney = new DecimalFormat("0").format(money * 100);

		int nLen = sMoney.length();
		String sInteger;
		String sDot;
		if (nLen < 2) {
			// add by JL at 2004-9-14
			sInteger = "";
			if (nLen == 1) {
				sDot = "0" + sMoney.substring(nLen - 1, nLen);
			} else {
				sDot = "0";
			}
		} else {
			sInteger = sMoney.substring(0, nLen - 2);
			sDot = sMoney.substring(nLen - 2, nLen);
		}

		String sFormatStr = PubFun.formatStr(sInteger);

		String s1 = PubFun.getChnM(sFormatStr.substring(0, 4), "亿");

		String s2 = PubFun.getChnM(sFormatStr.substring(4, 8), "万");

		String s3 = PubFun.getChnM(sFormatStr.substring(8, 12), "元");

		String s4 = PubFun.getDotM(sDot);

		if (s1.length() > 0 && s1.substring(0, 1).equals("0")) {
			s1 = s1.substring(1, s1.length());
		}
		if (s1.length() > 0
				&& s1.substring(s1.length() - 1, s1.length()).equals("0")
				&& s2.length() > 0 && s2.substring(0, 1).equals("0")) {
			s1 = s1.substring(0, s1.length() - 1);
		}
		if (s2.length() > 0
				&& s2.substring(s2.length() - 1, s2.length()).equals("0")
				&& s3.length() > 0 && s3.substring(0, 1).equals("0")) {
			s2 = s2.substring(0, s2.length() - 1);
		}
		if (s4.equals("00")) {
			s4 = "";
			if (s3.length() > 0
					&& s3.substring(s3.length() - 1, s3.length()).equals("0")) {
				s3 = s3.substring(0, s3.length() - 1);
			}
		}
		if (s3.length() > 0
				&& s3.substring(s3.length() - 1, s3.length()).equals("0")
				&& s4.length() > 0 && s4.substring(0, 1).equals("0")) {
			s3 = s3.substring(0, s3.length() - 1);
		}
		if (s4.length() > 0
				&& s4.substring(s4.length() - 1, s4.length()).equals("0")) {
			s4 = s4.substring(0, s4.length() - 1);
		}
		if ((!s1.equals("") || !s2.equals("")) && s3.equals("0")) {
			s3 = "";
			s4 = "0" + s4;
		}
		//add by winnie PIR20113235
		if ((!s1.equals("") || !s2.equals("")) && s3.equals("")&& s4.equals("")) {
			s2+="元";
		}
		ChnMoney = s0 + s1 + s2 + s3 + s4;
		if (ChnMoney.substring(0, 1).equals("0")) {
			ChnMoney = ChnMoney.substring(1, ChnMoney.length());
		}
		if (ChnMoney.substring(0, 1).equals("元")) {
			ChnMoney = ChnMoney.substring(1, ChnMoney.length());
		}
		for (int i = 0; i < ChnMoney.length(); i++) {
			if (ChnMoney.substring(i, i + 1).equals("0")) {
				ChnMoney = ChnMoney.substring(0, i) + "零"
						+ ChnMoney.substring(i + 1, ChnMoney.length());
			}
		}

		if (sDot.substring(1, 2).equals("0")) {
			ChnMoney += "整";
		}

		return ChnMoney;
	}

	/**
	 * 得到money的角分信息
	 * 
	 * @param sIn
	 *            String
	 * @return String
	 */
	private static String getDotM(String sIn) {
		String sMoney = "";
		if (!sIn.substring(0, 1).equals("0")) {
			sMoney += getNum(sIn.substring(0, 1)) + "角";
		} else {
			sMoney += "0";
		}
		if (!sIn.substring(1, 2).equals("0")) {
			sMoney += getNum(sIn.substring(1, 2)) + "分";
		} else {
			sMoney += "0";
		}

		return sMoney;
	}

	/**
	 * 添加仟、佰、拾等单位信息
	 * 
	 * @param strUnit
	 *            String
	 * @param digit
	 *            String
	 * @return String
	 */
	private static String getChnM(String strUnit, String digit) {
		String sMoney = "";
		boolean flag = false;

		if (strUnit.equals("0000")) {
			sMoney += "0";
			return sMoney;
		}
		if (!strUnit.substring(0, 1).equals("0")) {
			sMoney += getNum(strUnit.substring(0, 1)) + "仟";
		} else {
			sMoney += "0";
			flag = true;
		}
		if (!strUnit.substring(1, 2).equals("0")) {
			sMoney += getNum(strUnit.substring(1, 2)) + "佰";
			flag = false;
		} else {
			if (!flag) {
				sMoney += "0";
				flag = true;
			}
		}
		if (!strUnit.substring(2, 3).equals("0")) {
			sMoney += getNum(strUnit.substring(2, 3)) + "拾";
			flag = false;
		} else {
			if (!flag) {
				sMoney += "0";
				flag = true;
			}
		}
		if (!strUnit.substring(3, 4).equals("0")) {
			sMoney += getNum(strUnit.substring(3, 4));
		} else {
			if (!flag) {
				sMoney += "0";
				flag = true;
			}
		}

		if (sMoney.substring(sMoney.length() - 1, sMoney.length()).equals("0")) {
			sMoney = sMoney.substring(0, sMoney.length() - 1) + digit.trim()
					+ "0";
		} else {
			sMoney += digit.trim();
		}
		return sMoney;
	}

	/**
	 * 格式化字符
	 * 
	 * @param sIn
	 *            String
	 * @return String
	 */
	private static String formatStr(String sIn) {
		int n = sIn.length();
		// String sOut = sIn;
		StringBuffer sOut = new StringBuffer();

		for (int k = 1; k <= 12 - n; k++) {
			// sOut = "0" + sOut;
			sOut.append("0");
		}
		sOut.append(sIn);
		return sOut.toString();
	}

	/**
	 * 获取阿拉伯数字和中文数字的对应关系
	 * 
	 * @param value
	 *            String
	 * @return String
	 */
	private static String getNum(String value) {
		String sNum = "";
		Integer I = new Integer(value);
		int iValue = I.intValue();
		switch (iValue) {
		case 0:
			sNum = "零";
			break;
		case 1:
			sNum = "壹";
			break;
		case 2:
			sNum = "贰";
			break;
		case 3:
			sNum = "叁";
			break;
		case 4:
			sNum = "肆";
			break;
		case 5:
			sNum = "伍";
			break;
		case 6:
			sNum = "陆";
			break;
		case 7:
			sNum = "柒";
			break;
		case 8:
			sNum = "捌";
			break;
		case 9:
			sNum = "玖";
			break;
		}
		return sNum;
	}

	/**
	 * 如果一个字符串数字中小数点后全为零，则去掉小数点及零
	 * 
	 * @param Value
	 *            String
	 * @return String
	 */
	public static String getInt(String Value) {

		if (Value == null) {
			return null;
		}
		// 查询时对于没有数据的数字型数据Value传入的时null字符串，因此特殊处理一下
		// 朱向峰 2005-07-26 修改
		if (Value.equals("null")) {
			return "";
		}
		String result = "";
		boolean mflag = true;
		int m = 0;
		m = Value.lastIndexOf(".");
		if (m == -1) {
			result = Value;
		} else {
			for (int i = m + 1; i <= Value.length() - 1; i++) {
				if (Value.charAt(i) != '0') {
					result = Value;
					mflag = false;
					break;
				}
			}
			if (mflag) {
				result = Value.substring(0, m);
			}
		}
		return result;
	}

	/**
	 * 得到近似值
	 * 
	 * @param aValue
	 *            double
	 * @return double
	 */
	public static double getApproximation(double aValue) {
		if (java.lang.Math.abs(aValue) <= 0.01) {
			aValue = 0;
		}
		return aValue;
	}

	/**
	 * 根据输入标记为间隔符号，拆分字符串
	 * 
	 * @param strMain
	 *            String
	 * @param strDelimiters
	 *            String 失败返回NULL
	 * @return String[]
	 */
	public static String[] split(String strMain, String strDelimiters) {
		int i;
		int intIndex = 0; // 记录分隔符位置，以取出子串
		Vector vResult = new Vector(); // 存储子串的数组
		String strSub = ""; // 存放子串的中间变量

		strMain = strMain.trim();

		// 若主字符串比分隔符串还要短的话,则返回空字符串
		if (strMain.length() <= strDelimiters.length()) {
			System.out.println("分隔符串长度大于等于主字符串长度，不能进行拆分！");
			return null;
		}

		// 取出第一个分隔符在主串中的位置
		intIndex = strMain.indexOf(strDelimiters);

		// 在主串中找不到分隔符
		if (intIndex == -1) {
			String[] arrResult = { strMain };
			return arrResult;
		}

		// 分割主串到数组中
		while (intIndex != -1) {
			strSub = strMain.substring(0, intIndex);
			if (intIndex != 0) {
				vResult.add(strSub);
			} else {
				// break;
				vResult.add("");
			}

			strMain = strMain.substring(intIndex + strDelimiters.length())
					.trim();
			intIndex = strMain.indexOf(strDelimiters);
		}

		// 如果最末不是分隔符，取最后的字符串
		// if (!strMain.equals("") && strMain != null)
		if (!strMain.equals("")) {
			vResult.add(strMain);
		}

		String[] arrResult = new String[vResult.size()];
		for (i = 0; i < vResult.size(); i++) {
			arrResult[i] = (String) vResult.get(i);
		}

		return arrResult;
	}

	/**
	 * 设置数字精度 需要格式化的数据
	 * 
	 * @param value
	 *            float 精度描述（0.00表示精确到小数点后两位）
	 * @param precision
	 *            String
	 * @return double
	 */
	public static double setPrecision(float value, String precision) {
		return Float.parseFloat(new DecimalFormat(precision).format(value));
	}

	/**
	 * 设置数字精度 需要格式化的数据
	 * 
	 * @param value
	 *            double 精度描述（0.00表示精确到小数点后两位）
	 * @param precision
	 *            String
	 * @return double 此方法存在问题，对于10.445的结果是10.44，不能完全满足我们的需求，需要用Arith.round
	 */
	public static double setPrecision(double value, String precision) {
		return Double.parseDouble(new DecimalFormat(precision).format(value));
	}

	/**
	 * 设置数字精度 需要格式化的数据
	 * 
	 * @param value
	 *            double 精度描述（0.00表示精确到小数点后两位）
	 * @param precision
	 *            String
	 * @return String 此方法存在问题，对于10.445的结果是10.44，不能完全满足我们的需求，需要用Arith.round
	 */
	public static String chgPrecision(double value, String precision) {
		String str2 = new DecimalFormat(precision).format(Double.valueOf(String
				.valueOf(value)));
		return str2;
	}

	/**
	 * 把schemaset对象拷贝一份返回
	 * 
	 * @param srcSet
	 *            SchemaSet
	 * @return SchemaSet
	 */
	public static SchemaSet copySchemaSet(SchemaSet srcSet) {
		Reflections reflect = new Reflections();
		try {
			if (srcSet != null && srcSet.size() > 0) {
				if (srcSet.getObj(1) == null) {
					return null;
				}
				Class cls = srcSet.getClass();
				Schema schema = (Schema) srcSet.getObj(1).getClass()
						.newInstance();
				SchemaSet obj = (SchemaSet) cls.newInstance();
				obj.add(schema);
				reflect.transFields(obj, srcSet);
				return obj;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 互换LP和LC表数据
	 * 
	 * @param source
	 *            Schema
	 * @param object
	 *            Schema
	 * @return boolean
	 */
	public static boolean exchangeSchema(Schema source, Schema object) {
		try {
			// 把LP表的数据传递到LC表
			Reflections tReflections = new Reflections();
			tReflections.transFields(object, source);

			// 获取一个数据库连接DB
			Method m = object.getClass().getMethod("getDB", null);
			Schema schemaDB = (Schema) m.invoke(object, null);
			// 因为LP表与LC表只有EdorNo和EdorType两个关键字的差别，所以可以唯一获取LC表对应记录
			m = schemaDB.getClass().getMethod("getInfo", null);
			m.invoke(schemaDB, null);
			m = schemaDB.getClass().getMethod("getSchema", null);
			object = (Schema) m.invoke(schemaDB, null);

			// 把LC表数据备份到临时表
			m = object.getClass().getMethod("getSchema", null);
			Schema tSchema = (Schema) m.invoke(object, null);

			// 互换LP和LC表数据
			tReflections.transFields(object, source);
			tReflections.transFields(source, tSchema);

			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * 生成更新的sql列表
	 * 
	 * @param tables
	 *            String[]
	 * @param condition
	 *            String
	 * @param wherepart
	 *            String
	 * @return Vector
	 */
	public static Vector formUpdateSql(String[] tables, String condition,
			String wherepart) {
		Vector sqlVec = new Vector();
		StringBuffer tSBql = null;
		for (int i = 0; i < tables.length; i++) {
			tSBql = new StringBuffer(128);
			tSBql.append("update ");
			tSBql.append(tables[i]);
			tSBql.append(" set ");
			tSBql.append(condition);
			tSBql.append(" where ");
			tSBql.append(wherepart);
			sqlVec.add(tSBql.toString());
		}
		return sqlVec;
	}

	/**
	 * 将账号前的0去掉
	 * 
	 * @param sIn
	 *            String
	 * @return String
	 */
	public static String DeleteZero(String sIn) {
		int n = sIn.length();
		String sOut = sIn;
		// int k = 0;
		while (sOut.substring(0, 1).equals("0") && n > 1) {
			sOut = sOut.substring(1, n);
			n = sOut.length();
		}
		if (sOut.equals("0")) {
			return "";
		} else {
			return sOut;
		}
	}

	/**
	 * 转换JavaScript解析不了的特殊字符
	 * 
	 * @param s
	 *            String
	 * @return String
	 */
	public static String changForJavaScript(String s) {
		char[] arr = s.toCharArray();
		StringBuffer tSBql = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == '"' || arr[i] == '\'' || arr[i] == '\n') {
				tSBql.append("\\");
			}
			tSBql.append(arr[i]);
		}
		return tSBql.toString();
	}

	/**
	 * 转换JavaScript解析不了的特殊字符
	 * 
	 * @param s
	 *            String
	 * @return String
	 */
	public static String changForHTML(String s) {
		char[] arr = s.toCharArray();
		StringBuffer tSBql = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == '"' || arr[i] == '\'') {
				tSBql.append("\\");
			}
			if (arr[i] == '\n') {
				tSBql.append("<br>");
				continue;
			}
			tSBql.append(arr[i]);
		}
		return tSBql.toString();
	}

	/**
	 * 获取class的名称
	 * 
	 * @param o
	 *            Object
	 * @return String
	 */
	public static String getClassFileName(Object o) {
		String fileName = o.getClass().getName();
		fileName = fileName.substring(fileName.lastIndexOf(".") + 1);
		return fileName;
	}

	public static void out(Object o, String s) {
		System.out.println(PubFun.getClassFileName(o) + " : " + s);
	}

	/**
	 * 计算保单年度
	 * 
	 * @param cstartDate
	 *            String
	 * @param cendDate
	 *            String
	 * @return int
	 */
	public static int calPolYear(String cstartDate, String cendDate) {
		FDate fDate = new FDate();
		Date startDate = fDate.getDate(cstartDate);
		Date endDate = fDate.getDate(cendDate);
		if (fDate.mErrors.needDealError()) {
			return 0;
		}

		int interval = 0;

		GregorianCalendar sCalendar = new GregorianCalendar();
		sCalendar.setTime(startDate);
		int sYears = sCalendar.get(Calendar.YEAR);
		// int sMonths = sCalendar.get(Calendar.MONTH);
		// int sDays = sCalendar.get(Calendar.DAY_OF_MONTH);
		int sDaysOfYear = sCalendar.get(Calendar.DAY_OF_YEAR);

		GregorianCalendar eCalendar = new GregorianCalendar();
		eCalendar.setTime(endDate);
		int eYears = eCalendar.get(Calendar.YEAR);
		// int eMonths = eCalendar.get(Calendar.MONTH);
		// int eDays = eCalendar.get(Calendar.DAY_OF_MONTH);
		int eDaysOfYear = eCalendar.get(Calendar.DAY_OF_YEAR);

		interval = eYears - sYears;
		interval *= 365;
		interval += eDaysOfYear - sDaysOfYear;

		// 处理润年
		int n = 0;
		eYears--;
		if (eYears > sYears) {
			int i = sYears % 4;
			if (i == 0) {
				sYears++;
				n++;
			}
			int j = (eYears) % 4;
			if (j == 0) {
				eYears--;
				n++;
			}
			n += (eYears - sYears) / 4;
		}
		if (eYears == sYears) {
			int i = sYears % 4;
			if (i == 0) {
				n++;
			}
		}
		interval += n;

		int x = 365;
		int PolYear = 1;
		while (x < interval) {
			x += 365;
			PolYear += 1;
		}

		return PolYear;
	}

	/**
	 * 通过身份证号号获取生日日期
	 * 
	 * @param IdNo
	 *            String
	 * @return String
	 */
	public static String getBirthdayFromId(String IdNo) {
		String tIdNo = StrTool.cTrim(IdNo);
		String birthday = "";
		StringBuffer tReturn = new StringBuffer();
		if (tIdNo.length() != 15 && tIdNo.length() != 18) {
			return "";
		}
		if (tIdNo.length() == 18) {
			birthday = tIdNo.substring(6, 14);
			tReturn.append(birthday.substring(0, 4));
			tReturn.append("-");
			tReturn.append(birthday.substring(4, 6));
			tReturn.append("-");
			tReturn.append(birthday.substring(6));
		}
		if (tIdNo.length() == 15) {
			birthday = tIdNo.substring(6, 12);
			tReturn.append("19");
			tReturn.append(birthday.substring(0, 2));
			tReturn.append("-");
			tReturn.append(birthday.substring(2, 4));
			tReturn.append("-");
			tReturn.append(birthday.substring(4));
		}
		return tReturn.toString();
	}

	/**
	 * 通过身份证号获取性别
	 * 
	 * @param IdNo
	 *            String
	 * @return String
	 */
	public static String getSexFromId(String IdNo) {
		String tIdNo = StrTool.cTrim(IdNo);
		if (tIdNo.length() != 15 && tIdNo.length() != 18) {
			return "";
		}
		String sex = "";
		if (tIdNo.length() == 15) {
			sex = tIdNo.substring(14, 15);
		} else {
			sex = tIdNo.substring(16, 17);
		}
		try {
			int iSex = Integer.parseInt(sex);
			iSex %= 2;
			if (iSex == 0) {
				return "1";
			}
			if (iSex == 1) {
				return "0";
			}
		} catch (Exception ex) {
			return "";
		}

		return "";

	}

	/**
	 * 获取系统明天信息
	 * 
	 * @return String
	 */
	public static String getTomorrow() {
		// 返回日期的格式化样式
		String tFormatDate = "yyyy-MM-dd";
		// 创建格式化对象
		SimpleDateFormat tSimpleDateFormat = new SimpleDateFormat(tFormatDate);
		// 获取当前日期
		Calendar tCalendar = Calendar.getInstance();
		// 取出“日月”数
		int tDay = tCalendar.get(Calendar.DAY_OF_MONTH);
		// 将“日”加一，即得到明天
		++tDay;
		// 将“日”数设置回去
		tCalendar.set(Calendar.DAY_OF_MONTH, tDay);
		return tSimpleDateFormat.format(tCalendar.getTime());
	}

	/**
	 * 获取传入日期后的度量纬日期
	 * 
	 * @param cDate
	 *            String 传入的日期
	 * @param cInterval
	 *            int 日期的间隔
	 * @param cUnit
	 *            String 日期计算的纬度
	 * @return String
	 */
	public static String getLastDate(String cDate, int cInterval, String cUnit) {
		// 返回日期的格式化样式
		String tFormatDate = "yyyy-MM-dd";
		// 创建格式化对象
		SimpleDateFormat tSimpleDateFormat = new SimpleDateFormat(tFormatDate);
		// 转换日期字符串为日期类型
		FDate tFDate = new FDate();
		Date CurDate = tFDate.getDate(cDate);

		GregorianCalendar tCalendar = new GregorianCalendar();
		// 将传入的日期置入
		tCalendar.setTime(CurDate);

		if (cUnit.equals("D")) {
			// 取出日信息，需要同时取日月
			int tDay = tCalendar.get(Calendar.DAY_OF_MONTH);
			// 将日加上间隔，置回
			tCalendar.set(Calendar.DAY_OF_MONTH, tDay + cInterval);
			// 返回
			return tSimpleDateFormat.format(tCalendar.getTime());
		} else if (cUnit.equals("M")) {
			// 获取月份信息
			int tMonth = tCalendar.get(Calendar.MONTH);
			// 将月份加上间隔，置回
			tCalendar.set(Calendar.MONTH, tMonth + cInterval);
			// 返回
			return tSimpleDateFormat.format(tCalendar.getTime());
		} else if (cUnit.equals("Y")) {
			// 获取年信息
			int tYear = tCalendar.get(Calendar.YEAR);
			// 将年加上间隔，置回
			tCalendar.set(Calendar.YEAR, tYear + cInterval);
			// 返回
			return tSimpleDateFormat.format(tCalendar.getTime());
		} else {
			return "";
		}
	}

	/**
	 * 获取传入日期的上月信息，返回上月的第一天和最后一天
	 * 
	 * @param cDate
	 *            String
	 * @return String[]
	 */
	public static String[] getPrevMonth(String cDate) {
		// 返回日期的格式化样式
		String tFormatDate = "yyyy-MM-dd";
		// 创建格式化对象
		SimpleDateFormat tSimpleDateFormat = new SimpleDateFormat(tFormatDate);
		FDate tFDate = new FDate();
		Date CurDate = tFDate.getDate(cDate);
		GregorianCalendar tCalendar = new GregorianCalendar();
		// 将传入的日期置入
		tCalendar.setTime(CurDate);
		// 获取月份信息
		int tMonth = tCalendar.get(Calendar.MONTH);
		// 将月份减一，并置回
		tCalendar.set(Calendar.MONTH, tMonth - 1);
		return PubFun.calFLDate(tSimpleDateFormat.format(tCalendar.getTime()));
	}

	/**
	 * 获取系统昨天信息
	 * 
	 * @return String
	 */
	public static String getYesterday() {
		// 返回日期的格式化样式
		String tFormatDate = "yyyy-MM-dd";
		// 创建格式化对象
		SimpleDateFormat tSimpleDateFormat = new SimpleDateFormat(tFormatDate);
		// 获取当前日期
		Calendar tCalendar = Calendar.getInstance();
		// 取出“日月”数
		int tDay = tCalendar.get(Calendar.DAY_OF_MONTH);
		// 将“日”加一，即得到明天
		--tDay;
		// 将“日”数设置回去
		tCalendar.set(Calendar.DAY_OF_MONTH, tDay);
		return tSimpleDateFormat.format(tCalendar.getTime());
	}

	
    /**
     * 获取传入日期所在自然月的天数 add by zhangtao 2007-04-28
     * @param sDate 传入日期
     * @return
     */
    public static int monthLength(String sDate)
    {
        FDate fDate = new FDate();
        Date startDate = fDate.getDate(sDate);
        if (fDate.mErrors.needDealError())
        {
            return 0;
        }

        GregorianCalendar sCalendar = new GregorianCalendar();
        sCalendar.setTime(startDate);
        int sYears = sCalendar.get(Calendar.YEAR);
        int sMonths = sCalendar.get(Calendar.MONTH);

        return monthLength(sYears, sMonths+1);
    }
    
    /**
     * 获取传入年份、月份的自然月天数 add by zhangtao 2007-04-28
     * @param year 年份
     * @param month 月份 1-12
     * @return
     */
    public static int monthLength(int year, int month)
    {
        int LEAP_MONTH_LENGTH[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int MONTH_LENGTH[] = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        return isLeapYear(year) ? MONTH_LENGTH[month-1] : LEAP_MONTH_LENGTH[month-1];
    }
    
    
    /**
     * 判断是否为闰年
     * XinYQ added on 2006-09-25
     */
    public static boolean isLeapYear(int nYear)
    {
        boolean ResultLeap = false;
        ResultLeap = (nYear % 400 == 0) | (nYear % 100 != 0) & (nYear % 4 == 0);
        return ResultLeap;
      }
 
    /**
     * 判断是否为闰年
     * frost added on 2007-10-08
     */
    public static boolean isLeapYear(String sDate)
    { 	
    	FDate fDate = new FDate();
        Date startDate = fDate.getDate(sDate);
        if (fDate.mErrors.needDealError())
        {
            return false;
        }

        GregorianCalendar sCalendar = new GregorianCalendar();
        sCalendar.setTime(startDate);
        int sYears = sCalendar.get(Calendar.YEAR);
        return isLeapYear(sYears);
    }
    
	/**
	 * <p>
	 * 校验是否存在非法字符
	 * </p>
	 * 
	 * @param str
	 *            String
	 * @return boolean
	 */
	public static boolean validateNumber(String str) {
		String tmp = null;
		for (int i = 0; i < str.length(); i++) {
			tmp = str.substring(i, i + 1);
			if (DATE_LIST.indexOf(tmp) == -1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <p>
	 * 计算输入日期格式校验, 并且这个日期不能晚于今天.
	 * </p>
	 * 
	 * @param strDate
	 *            String
	 * @return boolean
	 */
	public static boolean validateDate(String strDate) {
		int yyyy = 0000;
		int mm = 00;
		int dd = 00;
		// 校验是否有非法字符
		if (!validateNumber(strDate)) {
			return false;
		}

		if (strDate.indexOf("-") >= 0) {
			StringTokenizer token = new StringTokenizer(strDate, "-");
			int i = 0;
			while (token.hasMoreElements()) {
				if (i == 0) {
					yyyy = Integer.parseInt(token.nextToken());
				}
				if (i == 1) {
					mm = Integer.parseInt(token.nextToken());
				}
				if (i == 2) {
					dd = Integer.parseInt(token.nextToken());
				}
				i++;
			}
		} else {
			if (strDate.length() != 8) {
				return false;
			}
			yyyy = Integer.parseInt(strDate.substring(0, 4));
			mm = Integer.parseInt(strDate.substring(4, 6));
			dd = Integer.parseInt(strDate.substring(6, 8));
		}
		if (yyyy > MAX_YEAR || yyyy < MIN_YEAR) {
			return false;
		}
		if (mm > MAX_MONTH || mm < MIN_MONTH) {
			return false;
		}
		if (dd > MAX_DAY || dd < MIN_DAY) {
			return false;
		}
		if ((mm == 4 || mm == 6 || mm == 9 || mm == 11) && (dd == 31)) {
			return false;
		}
		if (mm == 2) {
			// 校验闰年的情况下__日期格式
			boolean leap = (yyyy % 4 == 0 && (yyyy % 100 != 0 || yyyy % 400 == 0));
			if (dd > 29 || (dd == 29 && !leap)) {
				return false;
			}
		}
		// 日期不能超过机器日期
		FDate myFDate = new FDate();
		Date validateDate = myFDate.getDate(strDate);
		Date now = new Date();
		if (calInterval(validateDate, now, "D") < 0) {
			return false;
		}

		return true;
	}
	/**
	 * <p>
	 * 计算输入日期格式校验, 并且这个日期可以晚于今天.
	 * </p>
	 * 
	 * @param strDate
	 *            String
	 * @return boolean
	 */
	public static boolean validateDate1(String strDate) {
		int yyyy = 0000;
		int mm = 00;
		int dd = 00;
		// 校验是否有非法字符
		if (!validateNumber(strDate)) {
			return false;
		}

		if (strDate.indexOf("-") >= 0) {
			StringTokenizer token = new StringTokenizer(strDate, "-");
			int i = 0;
			while (token.hasMoreElements()) {
				if (i == 0) {
					yyyy = Integer.parseInt(token.nextToken());
				}
				if (i == 1) {
					mm = Integer.parseInt(token.nextToken());
				}
				if (i == 2) {
					dd = Integer.parseInt(token.nextToken());
				}
				i++;
			}
		} else {
			if (strDate.length() != 8) {
				return false;
			}
			yyyy = Integer.parseInt(strDate.substring(0, 4));
			mm = Integer.parseInt(strDate.substring(4, 6));
			dd = Integer.parseInt(strDate.substring(6, 8));
		}
		if (yyyy > MAX_YEAR || yyyy < MIN_YEAR) {
			return false;
		}
		if (mm > MAX_MONTH || mm < MIN_MONTH) {
			return false;
		}
		if (dd > MAX_DAY || dd < MIN_DAY) {
			return false;
		}
		if ((mm == 4 || mm == 6 || mm == 9 || mm == 11) && (dd == 31)) {
			return false;
		}
		if (mm == 2) {
			// 校验闰年的情况下__日期格式
			boolean leap = (yyyy % 4 == 0 && (yyyy % 100 != 0 || yyyy % 400 == 0));
			if (dd > 29 || (dd == 29 && !leap)) {
				return false;
			}
		}

		return true;
	}
	/**
	 * <p>
	 * 由15位身份证计算18位.
	 * </p>
	 * 
	 * @param id
	 *            String,证件号码
	 * @param type
	 *            String,类型0：15->18类型1：18->15
	 * @return boolean
	 */
	public static final String getNewId(String id, String type) {
		String newid = "";
		if (type.equals("0")) {
			final int[] W = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4,
					2, 1 };
			// 加权因子
			final String[] A = { "1", "0", "X", "9", "8", "7", "6", "5", "4",
					"3", "2" };
			// 校验码
			int i, j, s = 0;
			newid = id;
			newid = newid.substring(0, 6) + "19"
					+ newid.substring(6, id.length());
			for (i = 0; i < newid.length(); i++) {

				j = Integer.parseInt(newid.substring(i, i + 1)) * W[i];
				s += j;
			}
			s %= 11;
			newid += A[s];
		}
		if (type.equals("1")) {
			newid = id.substring(0, 6) + id.substring(8, 17);
		}

		return newid;

	}

	/**
	 * <p>
	 * 比较两个身份证
	 * </p>
	 * 
	 * @param id1
	 *            String,证件号码1
	 * @param id2
	 *            String,证件号码2
	 * @return boolean
	 */
	public static boolean CompareId(String id1, String id2) {
		if (id1.length() == id2.length()) {
			if (id1.equals(id2)) {
				return true;
			}
		} else if (id1.length() == 15 && id2.length() == 18) {
			if (PubFun.getNewId(id1, "0").equals(id2)) {
				return true;
			}
		} else if (id1.length() == 18 && id2.length() == 15) {
			if (PubFun.getNewId(id1, "1").equals(id2)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 格式化浮点型数据
	 * 
	 * @param dValue
	 *            double
	 * @return String
	 */
	public static String format(double dValue) {
		// 格式化的四舍五入不准确
		// 准确的四舍五入
		double tValue = Arith.round(dValue, 2);
		return new DecimalFormat("0.00").format(tValue);
	}
	/**
	 * 格式化浮点型数据
	 * 
	 * @param dValue
	 *            double
	 * @return String
	 */
	public static String format(double dValue, int scale) {
		// 格式化的四舍五入不准确
		// 准确的四舍五入
		double tValue = Arith.round(dValue, scale);
		String tFormat = "0";
		if (scale > 0) {
			tFormat = "0.";
			for (int i = 0; i < scale; i++)
				tFormat += "0";
		}
		return new DecimalFormat(tFormat).format(tValue);
	}
	/**
	 * 格式化日期格式 例如：传入2005-12-12，返回2005年12月12日
	 * 
	 * @param cDate
	 *            String
	 * @return String
	 */
	public static String FormatDate(String cDate) {
		if (cDate == null || "".equals(cDate)) {
			return "";
		}

		cDate = cDate.replaceFirst("-", "年");
		cDate = cDate.replaceFirst("-", "月");

		StringBuffer tSBql = new StringBuffer();
		tSBql.append(cDate);
		tSBql.append("日");

		return tSBql.toString();
	}

	/**
	 * 将xxxx-xx-xx转换为xxxx年xx月xx日 遇到0x月，或者0x日时，去掉前面的零 FormatDateEx("2005-09-09")
	 * 返回2005年9月9日
	 * 
	 * @param cDate
	 *            String
	 * @return String
	 */
	public static String formatDateEx(String cDate) {
		if (cDate == null || "".equals(cDate)) {
			return "";
		}

		// 年月日
		String year = cDate.substring(0, cDate.indexOf("-"));
		String month = cDate.substring(cDate.indexOf("-") + 1, cDate
				.lastIndexOf("-"));
		String day = cDate.substring(cDate.lastIndexOf("-") + 1);

		// 月前面去零
		if (month.startsWith("0")) {
			month = month.substring(1);
		}

		// 日前面去零
		if (day.startsWith("0")) {
			day = day.substring(1);
		}

		cDate = year + "年" + month + "月" + day + "日";
		return cDate;
	}

	/**
	 * 重载计算日期，参数见楼上，add by Minim
	 * 
	 * @param baseDate
	 *            String
	 * @param interval
	 *            int
	 * @param unit
	 *            String
	 * @param compareDate
	 *            String
	 * @return String
	 */
	public static String calOFDate(String baseDate, int interval, String unit,
			String compareDate) {
		try {
			FDate tFDate = new FDate();
			Date bDate = tFDate.getDate(baseDate);
			Date cDate = tFDate.getDate(compareDate);
			return tFDate.getString(calOFDate(bDate, interval, unit, cDate));
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 计算日期的函数 author: HST 参照日期指当按照年月进行日期的计算的时候，参考的日期，如下例，结果返回2002-03-31
	 * <p>
	 * <b>Example: </b>
	 * <p>
	 * <p>
	 * FDate tD=new FDate();
	 * <p>
	 * <p>
	 * Date baseDate =new Date();
	 * <p>
	 * <p>
	 * baseDate=tD.getDate("2000-02-29");
	 * <p>
	 * <p>
	 * Date comDate =new Date();
	 * <p>
	 * <p>
	 * comDate=tD.getDate("1999-12-31");
	 * <p>
	 * <p>
	 * int inteval=1;
	 * <p>
	 * <p>
	 * String tUnit="M";
	 * <p>
	 * <p>
	 * Date tDate =new Date();
	 * <p>
	 * <p>
	 * tDate=PubFun.calDate(baseDate,inteval,tUnit,comDate);
	 * <p>
	 * <p>
	 * System.out.println(tDate.toString());
	 * <p>
	 * 
	 * @param baseDate
	 *            起始日期
	 * @param interval
	 *            时间间隔
	 * @param unit
	 *            时间间隔单位
	 * @param compareDate
	 *            参照日期
	 * @return Date类型变量
	 */
	public static Date calOFDate(Date baseDate, int interval, String unit,
			Date compareDate) {
		Date returnDate = null;

		GregorianCalendar mCalendar = new GregorianCalendar();
		// 设置起始日期格式
		mCalendar.setTime(baseDate);
		if (unit.equals("Y")) {
			mCalendar.add(Calendar.YEAR, interval);
		}
		if (unit.equals("M")) {
			// 执行月份增减
			mCalendar.add(Calendar.MONTH, interval);
		}
		if (unit.equals("D")) {
			mCalendar.add(Calendar.DATE, interval);
		}

		if (compareDate != null) {
			GregorianCalendar cCalendar = new GregorianCalendar();
			// 设置坐标日期
			cCalendar.setTime(compareDate);

			int mYears = mCalendar.get(Calendar.YEAR);
			int mMonths = mCalendar.get(Calendar.MONTH);
			int cMonths = cCalendar.get(Calendar.MONTH);
			int cDays = cCalendar.get(Calendar.DATE);

			if (unit.equals("Y")) {
				cCalendar.set(mYears, cMonths, cDays);
				if (mMonths < cCalendar.get(Calendar.MONTH)) {
					cCalendar.set(mYears, mMonths + 1, 0);
					// cCalendar.set(mYears, mMonths,
					// mCalendar.get(Calendar.DAY_OF_MONTH));
				}
				if (cCalendar.before(mCalendar)) {
					mCalendar.set(mYears + 1, cMonths, cDays);
					returnDate = mCalendar.getTime();
				} else {
					returnDate = cCalendar.getTime();
				}
			}
			if (unit.equals("M")) {
				cCalendar.set(mYears, mMonths, cDays);

				if (mMonths < cCalendar.get(Calendar.MONTH)) {
					// 如果这个时候发生了问题，则需要重新处理月末的日期
					// Calendar tCalendar = Calendar.getInstance();
					// tCalendar.set(Calendar.YEAR, mYears);
					// tCalendar.set(Calendar.MONTH, mMonths + 1);
					// tCalendar.set(Calendar.DAY_OF_MONTH, 0);
					// 取当前月的最后一天日期
					cCalendar.set(mYears, mMonths + 1, 0);
					// cCalendar.set(mYears, mMonths,
					// tCalendar.get(Calendar.DAY_OF_MONTH));
				}
				if (cCalendar.before(mCalendar)) {
					mCalendar.set(mYears, mMonths + 1, cDays);
					returnDate = mCalendar.getTime();
				} else {
					returnDate = cCalendar.getTime();
				}
			}
			if (unit.equals("D")) {
				returnDate = mCalendar.getTime();
			}
		} else {
			returnDate = mCalendar.getTime();
		}

		return returnDate;
	}

	/**
	 * 获取日期＆时间的字符串
	 * 
	 * @param cDate
	 *            String
	 * @param cTime
	 *            String
	 * @return String
	 */
	public static String GetDateTime(String cDate, String cTime) {
		String tDate[] = cDate.split("-");
		String tTime[] = cTime.split(":");
		StringBuffer tSBql = new StringBuffer();
		tSBql.append(tDate[0]);
		tSBql.append(tDate[1]);
		tSBql.append(tDate[2]);
		tSBql.append(tTime[0]);
		tSBql.append(tTime[1]);
		tSBql.append(tTime[2]);
		return tSBql.toString();
	}
	/**
	 * 用户页面权限判断
	 * 
	 * @param cGlobalInput
	 *            GlobalInput
	 * @param RunScript
	 *            String
	 * @return boolean
	 */
	public static boolean canIDo(GlobalInput cGlobalInput, String RunScript,
			String pagesign) {
		String Operator = cGlobalInput.Operator;
		// String ComCode = cGlobalInput.ComCode;
		// String ManageCom = cGlobalInput.ManageCom;
		// 通过用户编码查询用户页面权限集合,NodeSign = 2为用户页面权限菜单标志
		StringBuffer sqlStr = new StringBuffer(128); 
		sqlStr.append("select count(1) from LDMenu ");
		sqlStr.append("where RunScript like '%");
		sqlStr.append(RunScript);
		sqlStr.append("%' ");
		// "where NodeSign = '2' and RunScript = '" + RunScript + "' ";
		if (pagesign.equals("page"))
			sqlStr.append("and parentnodecode in ( select distinct NodeCode from LDMenuGrpToMenu ");
		if (pagesign.equals("menu"))
			sqlStr.append("and NodeCode in ( select distinct NodeCode from LDMenuGrpToMenu ");
		sqlStr.append("where MenuGrpCode in ( select distinct MenuGrpCode from LDMenuGrp ");
		sqlStr.append("where MenuGrpCode in (select distinct MenuGrpCode from LDUserToMenuGrp where UserCode = '");
		sqlStr.append(Operator);
		sqlStr.append("') ) ) ");
		ExeSQL tExeSQL = new ExeSQL();
		SSRS tSSRS = tExeSQL.execSQL(sqlStr.toString());
		if (tSSRS != null) {
			String tt[] = tSSRS.getRowData(1);
			if (tt[0].equals("0")) {
				return false;
			}
		}
		// System.out.println("Yes can do");
		return true;
	}

	
    /************************
     * 判断传入的字符串是否为数字
     * 如果是数字，则返回true;否则返回false
     * 只要能Double.parseDouble(str)的都支持，比如:0000.89=0.89
     ************************/
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile(
                "([\\+\\-])?([0-9])+(.[0-9])?([0-9])*");
        Matcher isNum = pattern.matcher(str);

        if (!isNum.matches()) {
            return false;
        }

        return true;
    }
    /************************
     * 判断传入的字符串是否为正数，且小数位最多为２位
     * 如果是正数，则返回true;否则返回false
     * 只要能Double.parseDouble(str)的都支持，
     ************************/
    public static boolean isPositiveNumeric(String str) {
        Pattern pattern = Pattern.compile("^[0-9]+(.[0-9]{0,2})?$");
        Matcher isNum = pattern.matcher(str);

        if (!isNum.matches()) {
            return false;
        }

        return true;
    }
    /************************
     * 判断传入的字符串是否为数字或字母
     * 如果是数字，则返回true;否则返回false
     * 只要能Double.parseDouble(str)的都支持，比如:0000.89=0.89
     ************************/
    public static boolean isNumLetter(String str) {
        Pattern pattern = Pattern.compile(
                "^[A-Za-z0-9]+$");
        Matcher isNum = pattern.matcher(str);

        if (!isNum.matches()) {
            return false;
        }

        return true;
    }/************************
     * 判断传入的字符串不能存在英文，为银行信息校验
     * 如果是数字，则返回true;否则返回false
     * 只要能Double.parseDouble(str)的都支持，比如:0000.89=0.89
     ************************/
    public static boolean isCharacter(String str) {
        Pattern pattern = Pattern.compile(
                "[^A-Za-z]*[A-Za-z]+[^A-Za-z]*");
        Matcher isNum = pattern.matcher(str);

        if (isNum.matches()) {
            return false;
        }

        return true;
    }
	/**
     * 把数字用科学计数法显示,仅限小数 
     * 小数位数设定为20位
	 * @param aa
	 *            double
	 * @return String
	 * 
	 * modify by ka
	 */
    
    public static String FormatNumble(double aa)
    {
    	NumberFormat   formatter   =   NumberFormat.getNumberInstance();   
		formatter.setMaximumFractionDigits(20);     //设置最大小数位
		String   s   =   formatter.format(aa); 
		return s;
    }
	/**
	 * 获取传入日期所属的标准交费期间，返回EdorValiDate所在交费期间的起期和止期,如果是非整期保单,止期比EndDate大; CValiDate<=EdorValiDate<EndDate
	 * @param CValiDate String 团体保单生效日期
	 * @param EndDate String 团体保单终止日期
	 * @param EdorValiDate String 个人生效日期   
	 * @param PayIntv int 团体保单交费间隔 0,1,3,6         
	 * @return String[]
	 */
	public static String[] getIntervalDate(String CValiDate,String EndDate,String EdorValiDate,int PayIntv) {
		String MonDate[] = new String[2];
		FDate tD = new FDate();
		//PayIntv = 0 趸交
		if(tD.getDate(EndDate).compareTo(tD.getDate(CValiDate))>0 && tD.getDate(EdorValiDate).compareTo(tD.getDate(CValiDate))>=0 && tD.getDate(EndDate).compareTo(tD.getDate(EdorValiDate))>0 && PayIntv==0)
		{
	          Date tCValiDate = tD.getDate(CValiDate); 
	          MonDate[0] = tD.getString(tCValiDate);
	          Date tEndDate = tD.getDate(EndDate); 
	          MonDate[1] = tD.getString(tEndDate);
		}
		//PayIntv > 0 (1,3,6)期交
		else if(tD.getDate(EndDate).compareTo(tD.getDate(CValiDate))>0 && tD.getDate(EdorValiDate).compareTo(tD.getDate(CValiDate))>=0 && tD.getDate(EndDate).compareTo(tD.getDate(EdorValiDate))>0 && PayIntv>0)
		{
			  int monthUnit = PubFun.calInterval2(CValiDate, EndDate, "M");//算出两个日期之间月份间隔
	          int num = monthUnit/PayIntv;
	          if(Arith.div(monthUnit, PayIntv)>num)
	          {
	        	  num+=1; 
	          }
	          Date newBaseDate = tD.getDate(CValiDate); 
	          MonDate[0] = tD.getString(newBaseDate);
	          for(int i=1;i<=num;i++)
	          {
	             Date PayToDate = PubFun.calDate(newBaseDate, PayIntv*i, "M", null);	
	             if(PayToDate.compareTo(tD.getDate(EdorValiDate))>0)
	             {
	            	 MonDate[1]=tD.getString(PayToDate);
	            	 break;
	             }
	             else
	             {
	            	 MonDate[0] =tD.getString(PayToDate);
	             }
	          }
		}
		return MonDate;
	}
	/**
	 * 获取传入日期所属的标准交费期间之后至paytodate之前还有几期标准交费期间
	 * @param CValiDate String 团体保单生效日期
	 * @param EndDate String 团体保单终止日期
	 * @param EdorValiDate String 个人生效日期   
	 * @param PayIntv int 团体保单交费间隔 0,1,3,6   
	 * @param PayToDate String 团体保单交至日期       
	 * @return String[]
	 */
	public static int getPayIntvNum(String CValiDate,String EndDate,String EdorValiDate,int PayIntv,String PayToDate) {
		int result = 0;
		if(PayIntv==0)  //趸交
		{
			result = 0;
		}
		else if(PayIntv>0) //1,3,6期交
		{
			String MonDate[] = new String[2];
			MonDate = PubFun.getIntervalDate(CValiDate,EndDate,EdorValiDate,PayIntv);
			FDate tD = new FDate();
			if(MonDate[1]!=null && !MonDate[1].equals("") && tD.getDate(PayToDate).compareTo(tD.getDate(EndDate))<=0 && tD.getDate(PayToDate).compareTo(tD.getDate(MonDate[1]))>=0 )
			{
				int monthUnit = PubFun.calInterval2(MonDate[1], PayToDate, "M");//算出两个日期之间月份间隔
		        int num = monthUnit/PayIntv;
		        if(Arith.div(monthUnit, PayIntv)>num)
		        {
		        	  num+=1; 
		        }
		        result = num;
			}
		}
		return result;
	}
    
	/**
	 * 主函数，测试用
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String[] args) {

//		int PayIntv =0;
//		String CValiDate = "2010-1-10";
//		String EndDate = "2010-12-25";
//		String EdorValiDate ="2010-1-10";
//		String MonDate[] = new String[2];
//		MonDate = PubFun.getIntervalDate(CValiDate,EndDate,EdorValiDate,PayIntv);
//		System.out.println("==="+MonDate[0]+"  "+MonDate[1]);
		
//		int PayIntv =3;
//		String CValiDate = "2010-1-10";
//		String EndDate = "2010-12-25";
//		String EdorValiDate ="2010-6-28";
//		String PayToDate ="2010-12-25";
//		int num = PubFun.getPayIntvNum(CValiDate,EndDate,EdorValiDate,PayIntv,PayToDate);
//		System.out.println(num);
		
	}
}

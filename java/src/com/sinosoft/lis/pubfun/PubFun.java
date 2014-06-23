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
 * Title: Webҵ��ϵͳ
 * </p>
 * <p>
 * Description:ҵ��ϵͳ�Ĺ���ҵ������ �����������ҵ�����еĹ�������������ǰϵͳ�е�funpub.4gl
 * �ļ����Ӧ����������У����еĺ���������Static�����ͣ�������Ҫ�����ݶ��� ͨ����������ģ��ڱ����в�����ͨ�����Դ������ݵķ�����
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
	 * �������ڵĺ��� author: HST ��������ָ���������½������ڵļ����ʱ�򣬲ο������ڣ����������������2002-03-31
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
	 *            ��ʼ����
	 * @param interval
	 *            ʱ����
	 * @param unit
	 *            ʱ������λ
	 * @param compareDate
	 *            ��������
	 * @return Date���ͱ���
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
	 * ���ؼ������ڣ�������¥�ϣ�add by Minim
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
	 * ����ʹ�ú�����ͬ(Date baseDate, int interval, String unit, Date
	 * compareDate),��������Ҫ�� ������ֹ������Ϊ2006-12-31 calDate()Ϊ2007-1-1 add by ����
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
	 * ���ؼ������ڣ�������¥�ϣ�add by ����
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
	 * ͨ����ʼ���ں���ֹ���ڼ�����ʱ������λΪ������׼��ʱ���� author: HST
	 * <p>
	 * <b>Example: </b>
	 * <p>
	 * <p>
	 * ����calInterval(String cstartDate, String cendDate, String
	 * unit)��ǰ����������Ϊ�����ͼ���
	 * <p>
	 * 
	 * @param startDate
	 *            ��ʼ���ڣ�Date����
	 * @param endDate
	 *            ��ֹ���ڣ�Date����
	 * @param unit
	 *            ʱ������λ������ֵ("Y"--�� "M"--�� "D"--��)
	 * @return ʱ����,���α���int
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
						// ���ͬ��2�£�У����������
						if ((sYears % 4) == 0 && (eYears % 4) != 0) {
							// �����ʼ�������꣬��ֹ�겻������
							if (eDays == 28) {
								// �����ֹ�겻�����꣬��2�µ����һ��28�գ���ô��һ
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
				// eDays�������ĩ������Ϊ����һ����
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

			// ��������
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
	 * ͨ����ʼ���ں���ֹ���ڼ�����ʱ������λΪ������׼��ʱ������������ author: HST
	 * ��ʼ���ڣ�(String,��ʽ��"YYYY-MM-DD")
	 * 
	 * @param cstartDate
	 *            String ��ֹ���ڣ�(String,��ʽ��"YYYY-MM-DD")
	 * @param cendDate
	 *            String ʱ������λ������ֵ("Y"--�� "M"--�� "D"--��)
	 * @param unit
	 *            String ʱ����,���α���int
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
						// ���ͬ��2�£�У����������
						if ((sYears % 4) == 0 && (eYears % 4) != 0) {
							// �����ʼ�������꣬��ֹ�겻������
							if (eDays == 28) {
								// �����ֹ�겻�����꣬��2�µ����һ��28�գ���ô��һ
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
				// eDays�������ĩ������Ϊ����һ����
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

			// ��������
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
	 * ͨ����ʼ���ں���ֹ���ڼ�����ʱ������λΪ������׼��ʱ������Լ���� author: YangZhao��Minim
	 * ��ʼ���ڣ�(String,��ʽ��"YYYY-MM-DD")
	 * 
	 * @param cstartDate
	 *            String ��ֹ���ڣ�(String,��ʽ��"YYYY-MM-DD")
	 * @param cendDate
	 *            String ʱ������λ������ֵ("Y"--�� "M"--�� "D"--��)
	 * @param unit
	 *            String ʱ����,���α���int
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
						// ���ͬ��2�£�У����������
						if ((sYears % 4) == 0 && (eYears % 4) != 0) {
							// �����ʼ�������꣬��ֹ�겻������
							if (eDays == 28) {
								// �����ֹ�겻�����꣬��2�µ����һ��28�գ���ô��һ
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
				// �����ʼ����Ϊ��ĩ����ü���һ����
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

			// ��������
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
	 * ͨ����������ڿ��Եõ������µĵ�һ������һ������� author: LH ���ڣ�(String,��ʽ��"YYYY-MM-DD")
	 * 
	 * @param tDate
	 *            String ���¿�ʼ�ͽ������ڣ�����String[2]
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
	 * ͨ����������ڿ��Եõ�������ֹ��(�����ӳ�������) ��ʼ���ڣ�(String,��ʽ��"YYYY-MM-DD")
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

		// �����������������
		if ((startDate == null) || startDate.trim().equals("")) {
			System.out.println("û����ʼ����,�������ֹ��ʧ��!");
			return returnDate;
		}

		// ��ȡ���ֽ���ʧЧ����
		LMRiskPayDB tLMRiskPayDB = new LMRiskPayDB();
		tLMRiskPayDB.setRiskCode(strRiskCode);
		LMRiskPaySet tLMRiskPaySet = tLMRiskPayDB.query();

		if (tLMRiskPaySet.size() > 0) {
			if ((tLMRiskPaySet.get(1).getGracePeriodUnit() == null)
					|| tLMRiskPaySet.get(1).getGracePeriodUnit().equals("")) {
				// ���ÿ�����ΪĬ��ֵ
				System.out.println("ȱ�����ֽ���ʧЧ����!��Ĭ��ֵ����");
				nDates = 60;
				returnDate = calDate(startDate, nDates, "D", null);
			} else {
				// ȡ��ָ��������
				nDates = tLMRiskPaySet.get(1).getGracePeriod();
				returnDate = calDate(startDate, nDates, tLMRiskPaySet.get(1)
						.getGracePeriodUnit(), null);
				// jdk1.4�Դ��ķ��������ݣ�����ַ���������
				// String[] tDate = returnDate.split("-");
				// ���½�λ�������վ���
				if (tLMRiskPaySet.get(1).getGraceDateCalMode().equals("1")) {
					// �����ڵĲ��������ʹ��Calendar����
					GregorianCalendar tCalendar = new GregorianCalendar();
					tCalendar.setTime(tFDate.getDate(returnDate));
					// �·ݽ�λ�������վ���
					tCalendar.set(tCalendar.get(Calendar.YEAR), tCalendar
							.get(Calendar.MONTH) + 1, 1);
					returnDate = tFDate.getString(tCalendar.getTime());
				}

				// �����λ��ֻ�������վ��ȣ��������¾���
				if (tLMRiskPaySet.get(1).getGraceDateCalMode().equals("2")) {
					// �����ڵĲ��������ʹ��Calendar����
					GregorianCalendar tCalendar = new GregorianCalendar();
					tCalendar.setTime(tFDate.getDate(returnDate));
					// ��ݽ�λ�������վ��ȣ��������¾���
					tCalendar.set(tCalendar.get(Calendar.YEAR) + 1, tCalendar
							.get(Calendar.MONTH), 1);
					returnDate = tFDate.getString(tCalendar.getTime());
				}
			}
		} else {
			// ���ÿ�����ΪĬ��ֵ
			System.out.println("û�����ֽ���ʧЧ����!��Ĭ��ֵ����");
			nDates = 60;
			returnDate = calDate(startDate, nDates, "D", null);
		}

		// ȡ�ÿ������ӳ���
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
	 * �õ�Ĭ�ϵ�JDBCUrl
	 * 
	 * @return JDBCUrl
	 */
	public static JdbcUrl getDefaultUrl() {
		JdbcUrl tUrl = new JdbcUrl();
		return tUrl;
	}

	/**
	 * ���ַ�������,��sourString��<br>
	 * ����</br>��cChar����cLen���ȵ��ַ���,����ַ�����������������
	 * <p>
	 * <b>Example: </b>
	 * <p>
	 * <p>
	 * RCh("Minim", "0", 10) returns "Minim00000"
	 * <p>
	 * 
	 * @param sourString
	 *            Դ�ַ���
	 * @param cChar
	 *            �����õ��ַ�
	 * @param cLen
	 *            �ַ�����Ŀ�곤��
	 * @return �ַ���
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
	 * ���ַ�������,��sourString��<br>
	 * ǰ��</br>��cChar����cLen���ȵ��ַ���,����ַ�����������������
	 * <p>
	 * <b>Example: </b>
	 * <p>
	 * <p>
	 * LCh("Minim", "0", 10) returns "00000Minim"
	 * <p>
	 * 
	 * @param sourString
	 *            Դ�ַ���
	 * @param cChar
	 *            �����õ��ַ�
	 * @param cLen
	 *            �ַ�����Ŀ�곤��
	 * @return �ַ���
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
	 * �Ƚϻ�ȡ�����нϺ��һ��
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
	 * �Ƚϻ�ȡ�����н����һ��
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
	 * �õ���ǰϵͳ���� author: YT
	 * 
	 * @return ��ǰ���ڵĸ�ʽ�ַ���,���ڸ�ʽΪ"yyyy-MM-dd"
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
	 * �õ���ǰϵͳʱ�� author: YT
	 * 
	 * @return ��ǰʱ��ĸ�ʽ�ַ�����ʱ���ʽΪ"HH:mm:ss"
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
	 * �õ���ˮ��ǰ�� author: YT
	 * 
	 * @param comCode
	 *            ��������
	 * @return ��ˮ�ŵ�ǰ���ַ���
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
	 * picc��ȡ�����������ȡ�������ĵ�3-6λ����������+���������� �ټ������ڱ������λ����λ������ 052203
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
	 * �ú����õ�c_Str�еĵ�c_i����c_Split�ָ���ַ���
	 * 
	 * @param c_Str
	 *            Ŀ���ַ���
	 * @param c_i
	 *            λ��
	 * @param c_Split
	 *            �ָ��
	 * @return ��������쳣���򷵻ؿ�
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
	 * �����ֽ��ת��Ϊ���Ĵ�д��� author: HST
	 * 
	 * @param money
	 *            ���ֽ��(double)
	 * @return ���Ĵ�д���(String)
	 */
	public static String getChnMoney(double money) {
		String ChnMoney = "";
		String s0 = "";

		// ��ԭ���汾�ĳ����У�getChnMoney(585.30)�õ���������585.29��

		if (money == 0.0) {
			ChnMoney = "��Ԫ��";
			return ChnMoney;
		}

		if (money < 0) {
			s0 = "��";
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

		String s1 = PubFun.getChnM(sFormatStr.substring(0, 4), "��");

		String s2 = PubFun.getChnM(sFormatStr.substring(4, 8), "��");

		String s3 = PubFun.getChnM(sFormatStr.substring(8, 12), "Ԫ");

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
			s2+="Ԫ";
		}
		ChnMoney = s0 + s1 + s2 + s3 + s4;
		if (ChnMoney.substring(0, 1).equals("0")) {
			ChnMoney = ChnMoney.substring(1, ChnMoney.length());
		}
		if (ChnMoney.substring(0, 1).equals("Ԫ")) {
			ChnMoney = ChnMoney.substring(1, ChnMoney.length());
		}
		for (int i = 0; i < ChnMoney.length(); i++) {
			if (ChnMoney.substring(i, i + 1).equals("0")) {
				ChnMoney = ChnMoney.substring(0, i) + "��"
						+ ChnMoney.substring(i + 1, ChnMoney.length());
			}
		}

		if (sDot.substring(1, 2).equals("0")) {
			ChnMoney += "��";
		}

		return ChnMoney;
	}

	/**
	 * �õ�money�ĽǷ���Ϣ
	 * 
	 * @param sIn
	 *            String
	 * @return String
	 */
	private static String getDotM(String sIn) {
		String sMoney = "";
		if (!sIn.substring(0, 1).equals("0")) {
			sMoney += getNum(sIn.substring(0, 1)) + "��";
		} else {
			sMoney += "0";
		}
		if (!sIn.substring(1, 2).equals("0")) {
			sMoney += getNum(sIn.substring(1, 2)) + "��";
		} else {
			sMoney += "0";
		}

		return sMoney;
	}

	/**
	 * ���Ǫ���ۡ�ʰ�ȵ�λ��Ϣ
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
			sMoney += getNum(strUnit.substring(0, 1)) + "Ǫ";
		} else {
			sMoney += "0";
			flag = true;
		}
		if (!strUnit.substring(1, 2).equals("0")) {
			sMoney += getNum(strUnit.substring(1, 2)) + "��";
			flag = false;
		} else {
			if (!flag) {
				sMoney += "0";
				flag = true;
			}
		}
		if (!strUnit.substring(2, 3).equals("0")) {
			sMoney += getNum(strUnit.substring(2, 3)) + "ʰ";
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
	 * ��ʽ���ַ�
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
	 * ��ȡ���������ֺ��������ֵĶ�Ӧ��ϵ
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
			sNum = "��";
			break;
		case 1:
			sNum = "Ҽ";
			break;
		case 2:
			sNum = "��";
			break;
		case 3:
			sNum = "��";
			break;
		case 4:
			sNum = "��";
			break;
		case 5:
			sNum = "��";
			break;
		case 6:
			sNum = "½";
			break;
		case 7:
			sNum = "��";
			break;
		case 8:
			sNum = "��";
			break;
		case 9:
			sNum = "��";
			break;
		}
		return sNum;
	}

	/**
	 * ���һ���ַ���������С�����ȫΪ�㣬��ȥ��С���㼰��
	 * 
	 * @param Value
	 *            String
	 * @return String
	 */
	public static String getInt(String Value) {

		if (Value == null) {
			return null;
		}
		// ��ѯʱ����û�����ݵ�����������Value�����ʱnull�ַ�����������⴦��һ��
		// ����� 2005-07-26 �޸�
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
	 * �õ�����ֵ
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
	 * ����������Ϊ������ţ�����ַ���
	 * 
	 * @param strMain
	 *            String
	 * @param strDelimiters
	 *            String ʧ�ܷ���NULL
	 * @return String[]
	 */
	public static String[] split(String strMain, String strDelimiters) {
		int i;
		int intIndex = 0; // ��¼�ָ���λ�ã���ȡ���Ӵ�
		Vector vResult = new Vector(); // �洢�Ӵ�������
		String strSub = ""; // ����Ӵ����м����

		strMain = strMain.trim();

		// �����ַ����ȷָ�������Ҫ�̵Ļ�,�򷵻ؿ��ַ���
		if (strMain.length() <= strDelimiters.length()) {
			System.out.println("�ָ��������ȴ��ڵ������ַ������ȣ����ܽ��в�֣�");
			return null;
		}

		// ȡ����һ���ָ����������е�λ��
		intIndex = strMain.indexOf(strDelimiters);

		// ���������Ҳ����ָ���
		if (intIndex == -1) {
			String[] arrResult = { strMain };
			return arrResult;
		}

		// �ָ�������������
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

		// �����ĩ���Ƿָ�����ȡ�����ַ���
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
	 * �������־��� ��Ҫ��ʽ��������
	 * 
	 * @param value
	 *            float ����������0.00��ʾ��ȷ��С�������λ��
	 * @param precision
	 *            String
	 * @return double
	 */
	public static double setPrecision(float value, String precision) {
		return Float.parseFloat(new DecimalFormat(precision).format(value));
	}

	/**
	 * �������־��� ��Ҫ��ʽ��������
	 * 
	 * @param value
	 *            double ����������0.00��ʾ��ȷ��С�������λ��
	 * @param precision
	 *            String
	 * @return double �˷����������⣬����10.445�Ľ����10.44��������ȫ�������ǵ�������Ҫ��Arith.round
	 */
	public static double setPrecision(double value, String precision) {
		return Double.parseDouble(new DecimalFormat(precision).format(value));
	}

	/**
	 * �������־��� ��Ҫ��ʽ��������
	 * 
	 * @param value
	 *            double ����������0.00��ʾ��ȷ��С�������λ��
	 * @param precision
	 *            String
	 * @return String �˷����������⣬����10.445�Ľ����10.44��������ȫ�������ǵ�������Ҫ��Arith.round
	 */
	public static String chgPrecision(double value, String precision) {
		String str2 = new DecimalFormat(precision).format(Double.valueOf(String
				.valueOf(value)));
		return str2;
	}

	/**
	 * ��schemaset���󿽱�һ�ݷ���
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
	 * ����LP��LC������
	 * 
	 * @param source
	 *            Schema
	 * @param object
	 *            Schema
	 * @return boolean
	 */
	public static boolean exchangeSchema(Schema source, Schema object) {
		try {
			// ��LP������ݴ��ݵ�LC��
			Reflections tReflections = new Reflections();
			tReflections.transFields(object, source);

			// ��ȡһ�����ݿ�����DB
			Method m = object.getClass().getMethod("getDB", null);
			Schema schemaDB = (Schema) m.invoke(object, null);
			// ��ΪLP����LC��ֻ��EdorNo��EdorType�����ؼ��ֵĲ�����Կ���Ψһ��ȡLC���Ӧ��¼
			m = schemaDB.getClass().getMethod("getInfo", null);
			m.invoke(schemaDB, null);
			m = schemaDB.getClass().getMethod("getSchema", null);
			object = (Schema) m.invoke(schemaDB, null);

			// ��LC�����ݱ��ݵ���ʱ��
			m = object.getClass().getMethod("getSchema", null);
			Schema tSchema = (Schema) m.invoke(object, null);

			// ����LP��LC������
			tReflections.transFields(object, source);
			tReflections.transFields(source, tSchema);

			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * ���ɸ��µ�sql�б�
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
	 * ���˺�ǰ��0ȥ��
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
	 * ת��JavaScript�������˵������ַ�
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
	 * ת��JavaScript�������˵������ַ�
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
	 * ��ȡclass������
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
	 * ���㱣�����
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

		// ��������
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
	 * ͨ�����֤�źŻ�ȡ��������
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
	 * ͨ�����֤�Ż�ȡ�Ա�
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
	 * ��ȡϵͳ������Ϣ
	 * 
	 * @return String
	 */
	public static String getTomorrow() {
		// �������ڵĸ�ʽ����ʽ
		String tFormatDate = "yyyy-MM-dd";
		// ������ʽ������
		SimpleDateFormat tSimpleDateFormat = new SimpleDateFormat(tFormatDate);
		// ��ȡ��ǰ����
		Calendar tCalendar = Calendar.getInstance();
		// ȡ�������¡���
		int tDay = tCalendar.get(Calendar.DAY_OF_MONTH);
		// �����ա���һ�����õ�����
		++tDay;
		// �����ա������û�ȥ
		tCalendar.set(Calendar.DAY_OF_MONTH, tDay);
		return tSimpleDateFormat.format(tCalendar.getTime());
	}

	/**
	 * ��ȡ�������ں�Ķ���γ����
	 * 
	 * @param cDate
	 *            String ���������
	 * @param cInterval
	 *            int ���ڵļ��
	 * @param cUnit
	 *            String ���ڼ����γ��
	 * @return String
	 */
	public static String getLastDate(String cDate, int cInterval, String cUnit) {
		// �������ڵĸ�ʽ����ʽ
		String tFormatDate = "yyyy-MM-dd";
		// ������ʽ������
		SimpleDateFormat tSimpleDateFormat = new SimpleDateFormat(tFormatDate);
		// ת�������ַ���Ϊ��������
		FDate tFDate = new FDate();
		Date CurDate = tFDate.getDate(cDate);

		GregorianCalendar tCalendar = new GregorianCalendar();
		// ���������������
		tCalendar.setTime(CurDate);

		if (cUnit.equals("D")) {
			// ȡ������Ϣ����Ҫͬʱȡ����
			int tDay = tCalendar.get(Calendar.DAY_OF_MONTH);
			// ���ռ��ϼ�����û�
			tCalendar.set(Calendar.DAY_OF_MONTH, tDay + cInterval);
			// ����
			return tSimpleDateFormat.format(tCalendar.getTime());
		} else if (cUnit.equals("M")) {
			// ��ȡ�·���Ϣ
			int tMonth = tCalendar.get(Calendar.MONTH);
			// ���·ݼ��ϼ�����û�
			tCalendar.set(Calendar.MONTH, tMonth + cInterval);
			// ����
			return tSimpleDateFormat.format(tCalendar.getTime());
		} else if (cUnit.equals("Y")) {
			// ��ȡ����Ϣ
			int tYear = tCalendar.get(Calendar.YEAR);
			// ������ϼ�����û�
			tCalendar.set(Calendar.YEAR, tYear + cInterval);
			// ����
			return tSimpleDateFormat.format(tCalendar.getTime());
		} else {
			return "";
		}
	}

	/**
	 * ��ȡ�������ڵ�������Ϣ���������µĵ�һ������һ��
	 * 
	 * @param cDate
	 *            String
	 * @return String[]
	 */
	public static String[] getPrevMonth(String cDate) {
		// �������ڵĸ�ʽ����ʽ
		String tFormatDate = "yyyy-MM-dd";
		// ������ʽ������
		SimpleDateFormat tSimpleDateFormat = new SimpleDateFormat(tFormatDate);
		FDate tFDate = new FDate();
		Date CurDate = tFDate.getDate(cDate);
		GregorianCalendar tCalendar = new GregorianCalendar();
		// ���������������
		tCalendar.setTime(CurDate);
		// ��ȡ�·���Ϣ
		int tMonth = tCalendar.get(Calendar.MONTH);
		// ���·ݼ�һ�����û�
		tCalendar.set(Calendar.MONTH, tMonth - 1);
		return PubFun.calFLDate(tSimpleDateFormat.format(tCalendar.getTime()));
	}

	/**
	 * ��ȡϵͳ������Ϣ
	 * 
	 * @return String
	 */
	public static String getYesterday() {
		// �������ڵĸ�ʽ����ʽ
		String tFormatDate = "yyyy-MM-dd";
		// ������ʽ������
		SimpleDateFormat tSimpleDateFormat = new SimpleDateFormat(tFormatDate);
		// ��ȡ��ǰ����
		Calendar tCalendar = Calendar.getInstance();
		// ȡ�������¡���
		int tDay = tCalendar.get(Calendar.DAY_OF_MONTH);
		// �����ա���һ�����õ�����
		--tDay;
		// �����ա������û�ȥ
		tCalendar.set(Calendar.DAY_OF_MONTH, tDay);
		return tSimpleDateFormat.format(tCalendar.getTime());
	}

	
    /**
     * ��ȡ��������������Ȼ�µ����� add by zhangtao 2007-04-28
     * @param sDate ��������
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
     * ��ȡ������ݡ��·ݵ���Ȼ������ add by zhangtao 2007-04-28
     * @param year ���
     * @param month �·� 1-12
     * @return
     */
    public static int monthLength(int year, int month)
    {
        int LEAP_MONTH_LENGTH[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int MONTH_LENGTH[] = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        return isLeapYear(year) ? MONTH_LENGTH[month-1] : LEAP_MONTH_LENGTH[month-1];
    }
    
    
    /**
     * �ж��Ƿ�Ϊ����
     * XinYQ added on 2006-09-25
     */
    public static boolean isLeapYear(int nYear)
    {
        boolean ResultLeap = false;
        ResultLeap = (nYear % 400 == 0) | (nYear % 100 != 0) & (nYear % 4 == 0);
        return ResultLeap;
      }
 
    /**
     * �ж��Ƿ�Ϊ����
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
	 * У���Ƿ���ڷǷ��ַ�
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
	 * �����������ڸ�ʽУ��, ����������ڲ������ڽ���.
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
		// У���Ƿ��зǷ��ַ�
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
			// У������������__���ڸ�ʽ
			boolean leap = (yyyy % 4 == 0 && (yyyy % 100 != 0 || yyyy % 400 == 0));
			if (dd > 29 || (dd == 29 && !leap)) {
				return false;
			}
		}
		// ���ڲ��ܳ�����������
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
	 * �����������ڸ�ʽУ��, ����������ڿ������ڽ���.
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
		// У���Ƿ��зǷ��ַ�
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
			// У������������__���ڸ�ʽ
			boolean leap = (yyyy % 4 == 0 && (yyyy % 100 != 0 || yyyy % 400 == 0));
			if (dd > 29 || (dd == 29 && !leap)) {
				return false;
			}
		}

		return true;
	}
	/**
	 * <p>
	 * ��15λ���֤����18λ.
	 * </p>
	 * 
	 * @param id
	 *            String,֤������
	 * @param type
	 *            String,����0��15->18����1��18->15
	 * @return boolean
	 */
	public static final String getNewId(String id, String type) {
		String newid = "";
		if (type.equals("0")) {
			final int[] W = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4,
					2, 1 };
			// ��Ȩ����
			final String[] A = { "1", "0", "X", "9", "8", "7", "6", "5", "4",
					"3", "2" };
			// У����
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
	 * �Ƚ��������֤
	 * </p>
	 * 
	 * @param id1
	 *            String,֤������1
	 * @param id2
	 *            String,֤������2
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
	 * ��ʽ������������
	 * 
	 * @param dValue
	 *            double
	 * @return String
	 */
	public static String format(double dValue) {
		// ��ʽ�����������벻׼ȷ
		// ׼ȷ����������
		double tValue = Arith.round(dValue, 2);
		return new DecimalFormat("0.00").format(tValue);
	}
	/**
	 * ��ʽ������������
	 * 
	 * @param dValue
	 *            double
	 * @return String
	 */
	public static String format(double dValue, int scale) {
		// ��ʽ�����������벻׼ȷ
		// ׼ȷ����������
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
	 * ��ʽ�����ڸ�ʽ ���磺����2005-12-12������2005��12��12��
	 * 
	 * @param cDate
	 *            String
	 * @return String
	 */
	public static String FormatDate(String cDate) {
		if (cDate == null || "".equals(cDate)) {
			return "";
		}

		cDate = cDate.replaceFirst("-", "��");
		cDate = cDate.replaceFirst("-", "��");

		StringBuffer tSBql = new StringBuffer();
		tSBql.append(cDate);
		tSBql.append("��");

		return tSBql.toString();
	}

	/**
	 * ��xxxx-xx-xxת��Ϊxxxx��xx��xx�� ����0x�£�����0x��ʱ��ȥ��ǰ����� FormatDateEx("2005-09-09")
	 * ����2005��9��9��
	 * 
	 * @param cDate
	 *            String
	 * @return String
	 */
	public static String formatDateEx(String cDate) {
		if (cDate == null || "".equals(cDate)) {
			return "";
		}

		// ������
		String year = cDate.substring(0, cDate.indexOf("-"));
		String month = cDate.substring(cDate.indexOf("-") + 1, cDate
				.lastIndexOf("-"));
		String day = cDate.substring(cDate.lastIndexOf("-") + 1);

		// ��ǰ��ȥ��
		if (month.startsWith("0")) {
			month = month.substring(1);
		}

		// ��ǰ��ȥ��
		if (day.startsWith("0")) {
			day = day.substring(1);
		}

		cDate = year + "��" + month + "��" + day + "��";
		return cDate;
	}

	/**
	 * ���ؼ������ڣ�������¥�ϣ�add by Minim
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
	 * �������ڵĺ��� author: HST ��������ָ���������½������ڵļ����ʱ�򣬲ο������ڣ����������������2002-03-31
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
	 *            ��ʼ����
	 * @param interval
	 *            ʱ����
	 * @param unit
	 *            ʱ������λ
	 * @param compareDate
	 *            ��������
	 * @return Date���ͱ���
	 */
	public static Date calOFDate(Date baseDate, int interval, String unit,
			Date compareDate) {
		Date returnDate = null;

		GregorianCalendar mCalendar = new GregorianCalendar();
		// ������ʼ���ڸ�ʽ
		mCalendar.setTime(baseDate);
		if (unit.equals("Y")) {
			mCalendar.add(Calendar.YEAR, interval);
		}
		if (unit.equals("M")) {
			// ִ���·�����
			mCalendar.add(Calendar.MONTH, interval);
		}
		if (unit.equals("D")) {
			mCalendar.add(Calendar.DATE, interval);
		}

		if (compareDate != null) {
			GregorianCalendar cCalendar = new GregorianCalendar();
			// ������������
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
					// ������ʱ���������⣬����Ҫ���´�����ĩ������
					// Calendar tCalendar = Calendar.getInstance();
					// tCalendar.set(Calendar.YEAR, mYears);
					// tCalendar.set(Calendar.MONTH, mMonths + 1);
					// tCalendar.set(Calendar.DAY_OF_MONTH, 0);
					// ȡ��ǰ�µ����һ������
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
	 * ��ȡ���ڣ�ʱ����ַ���
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
	 * �û�ҳ��Ȩ���ж�
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
		// ͨ���û������ѯ�û�ҳ��Ȩ�޼���,NodeSign = 2Ϊ�û�ҳ��Ȩ�޲˵���־
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
     * �жϴ�����ַ����Ƿ�Ϊ����
     * ��������֣��򷵻�true;���򷵻�false
     * ֻҪ��Double.parseDouble(str)�Ķ�֧�֣�����:0000.89=0.89
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
     * �жϴ�����ַ����Ƿ�Ϊ��������С��λ���Ϊ��λ
     * ������������򷵻�true;���򷵻�false
     * ֻҪ��Double.parseDouble(str)�Ķ�֧�֣�
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
     * �жϴ�����ַ����Ƿ�Ϊ���ֻ���ĸ
     * ��������֣��򷵻�true;���򷵻�false
     * ֻҪ��Double.parseDouble(str)�Ķ�֧�֣�����:0000.89=0.89
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
     * �жϴ�����ַ������ܴ���Ӣ�ģ�Ϊ������ϢУ��
     * ��������֣��򷵻�true;���򷵻�false
     * ֻҪ��Double.parseDouble(str)�Ķ�֧�֣�����:0000.89=0.89
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
     * �������ÿ�ѧ��������ʾ,����С�� 
     * С��λ���趨Ϊ20λ
	 * @param aa
	 *            double
	 * @return String
	 * 
	 * modify by ka
	 */
    
    public static String FormatNumble(double aa)
    {
    	NumberFormat   formatter   =   NumberFormat.getNumberInstance();   
		formatter.setMaximumFractionDigits(20);     //�������С��λ
		String   s   =   formatter.format(aa); 
		return s;
    }
	/**
	 * ��ȡ�������������ı�׼�����ڼ䣬����EdorValiDate���ڽ����ڼ�����ں�ֹ��,����Ƿ����ڱ���,ֹ�ڱ�EndDate��; CValiDate<=EdorValiDate<EndDate
	 * @param CValiDate String ���屣����Ч����
	 * @param EndDate String ���屣����ֹ����
	 * @param EdorValiDate String ������Ч����   
	 * @param PayIntv int ���屣�����Ѽ�� 0,1,3,6         
	 * @return String[]
	 */
	public static String[] getIntervalDate(String CValiDate,String EndDate,String EdorValiDate,int PayIntv) {
		String MonDate[] = new String[2];
		FDate tD = new FDate();
		//PayIntv = 0 ����
		if(tD.getDate(EndDate).compareTo(tD.getDate(CValiDate))>0 && tD.getDate(EdorValiDate).compareTo(tD.getDate(CValiDate))>=0 && tD.getDate(EndDate).compareTo(tD.getDate(EdorValiDate))>0 && PayIntv==0)
		{
	          Date tCValiDate = tD.getDate(CValiDate); 
	          MonDate[0] = tD.getString(tCValiDate);
	          Date tEndDate = tD.getDate(EndDate); 
	          MonDate[1] = tD.getString(tEndDate);
		}
		//PayIntv > 0 (1,3,6)�ڽ�
		else if(tD.getDate(EndDate).compareTo(tD.getDate(CValiDate))>0 && tD.getDate(EdorValiDate).compareTo(tD.getDate(CValiDate))>=0 && tD.getDate(EndDate).compareTo(tD.getDate(EdorValiDate))>0 && PayIntv>0)
		{
			  int monthUnit = PubFun.calInterval2(CValiDate, EndDate, "M");//�����������֮���·ݼ��
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
	 * ��ȡ�������������ı�׼�����ڼ�֮����paytodate֮ǰ���м��ڱ�׼�����ڼ�
	 * @param CValiDate String ���屣����Ч����
	 * @param EndDate String ���屣����ֹ����
	 * @param EdorValiDate String ������Ч����   
	 * @param PayIntv int ���屣�����Ѽ�� 0,1,3,6   
	 * @param PayToDate String ���屣����������       
	 * @return String[]
	 */
	public static int getPayIntvNum(String CValiDate,String EndDate,String EdorValiDate,int PayIntv,String PayToDate) {
		int result = 0;
		if(PayIntv==0)  //����
		{
			result = 0;
		}
		else if(PayIntv>0) //1,3,6�ڽ�
		{
			String MonDate[] = new String[2];
			MonDate = PubFun.getIntervalDate(CValiDate,EndDate,EdorValiDate,PayIntv);
			FDate tD = new FDate();
			if(MonDate[1]!=null && !MonDate[1].equals("") && tD.getDate(PayToDate).compareTo(tD.getDate(EndDate))<=0 && tD.getDate(PayToDate).compareTo(tD.getDate(MonDate[1]))>=0 )
			{
				int monthUnit = PubFun.calInterval2(MonDate[1], PayToDate, "M");//�����������֮���·ݼ��
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
	 * ��������������
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

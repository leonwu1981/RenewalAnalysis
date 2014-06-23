/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vdb.LCInsureAccDBSet;
import com.sinosoft.lis.vdb.LCInsureAccTraceDBSet;
import com.sinosoft.lis.vschema.LCInsureAccClassSet;
import com.sinosoft.lis.vschema.LCInsureAccSet;
import com.sinosoft.lis.vschema.LCInsureAccTraceSet;
import com.sinosoft.utility.*;
import com.sinosoft.lis.schema.LCInsureAccClassSchema;
import com.sinosoft.lis.schema.LDBankRateSchema;
import com.sinosoft.lis.vschema.LDBankRateSet;
import com.sinosoft.lis.tb.CachedRiskInfo;
import com.sinosoft.lis.transfer.Grp2InsuredBL;

/**
 * <p>
 * Title: Life Information System
 * </p>
 * <p>
 * Description:帐户处理类
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 *
 * @author unascribed
 * @version 1.0
 */
public class AccountManage {

	public CErrors mErrors = new CErrors(); // 错误信息

	LCInsureAccSet mLCInsureAccSet = new LCInsureAccSet();

	LCInsureAccTraceSet mLCInsureAccTraceSet = new LCInsureAccTraceSet();

	LCInsureAccClassSet mLCInsureAccClassSet = new LCInsureAccClassSet();

	// 存放利息的缓存
	private static Map rateCacheMap;// = new HashMap();

	public AccountManage() {
            if (rateCacheMap == null)
                rateCacheMap = new HashMap();
        }

        public static void deleteRateCash(){
            if (rateCacheMap != null)
                rateCacheMap.clear();
        }


	/**
	 * 此方法根据参数aOriginMoney的原始金额计算利息
	 *
	 * @param aOriginMoney
	 *            double 原始金额
	 * @param TableName
	 *            String
	 * @param aRate
	 *            double 提供固定利率值,如果按利率表，该值初始设为0.0
	 * @param aComputerFlag
	 *            int 计算标志位，区分单利，复利，固定利率及按利率表等
	 * @param aRateType
	 *            String 原始利率类型 比如按年利率，按月等
	 * @param aIntervalType
	 *            String 目标利率类型 同上
	 * @param aStartDate
	 *            String 节息计时的开始日期
	 * @param aEndDate
	 *            String 节息计时的结束日期
	 * @return double
	 */
	public double getInterest(double aOriginMoney, String TableName,
			double aRate, int aComputerFlag, String aRateType,
			String aIntervalType, String aStartDate, String aEndDate) {
		int tInterval = 0;
		double calInterest = 0.0;
		double tCalRate = 0.0;
		switch (aComputerFlag) {
		case 0:

			// 不计息
			break;
		case 1:

			// 按照固定利率单利生息
			tCalRate = TransAccRate(aRate, aRateType, "S", aIntervalType); // 按给定参数更改利率
			tInterval = PubFun.calInterval(aStartDate, aEndDate, aIntervalType); // 计算时间间隔
			calInterest = aOriginMoney * getIntvRate(tInterval, tCalRate, "S"); // 计算利息
			break;
		case 2:

			// 按照固定利率复利生息
			tCalRate = TransAccRate(aRate, aRateType, "C", aIntervalType);
			tInterval = PubFun.calInterval(aStartDate, aEndDate, aIntervalType); // 计算时间间隔
			calInterest = aOriginMoney * getIntvRate(tInterval, tCalRate, "C"); // 计算利息
			break;
		case 3:

			// 按照利率表单利生息(利率表是用interest000001还是LDBankRate???)
			String[] ResultRate = getMultiAccRate(TableName, aStartDate,
					aEndDate, aRateType, "S", aIntervalType); // 根据提供表获取分段利率
			for (int m = 0; m < ResultRate.length; m++) {
				// 杨红添加此段代码，添加开始
				if (ResultRate[m] == null) {
					ResultRate[m] = "0";
				}
				// 添加结束
				tCalRate = Double.parseDouble(ResultRate[m]);
				double tSubInterest = aOriginMoney * tCalRate;
				calInterest += tSubInterest;
			}
			break;
		case 4:
			String[] ResultRate2 = getMultiAccRate(TableName, aStartDate,
					aEndDate, aRateType, "C", aIntervalType); // 根据提供表获取分段利率
			for (int m = 0; m < ResultRate2.length; m++) {
				// 杨红添加此段代码，添加开始
				if (ResultRate2[m] == null) {
					ResultRate2[m] = "0";
				}
				// 添加结束
				tCalRate = Double.parseDouble(ResultRate2[m]);
				double tSubInterest = aOriginMoney * tCalRate;
				calInterest += tSubInterest;
			}
			break;

		}
		return calInterest;
	}

	/**
	 * Yanghong于20050614添加该方法
	 *
	 * @param aOriginMoney
	 *            double
	 * @param aInsuAccNo
	 *            String 帐户类型
	 * @param aRateType
	 *            String
	 * @param aIntervalType
	 *            String
	 * @param aStartDate
	 *            String
	 * @param aEndDate
	 *            String
	 * @return double
	 */
	public double getInterest(double aOriginMoney, String aInsuAccNo,
			String aRateType, String aIntervalType, String aStartDate,
			String aEndDate) {
		double calInterest = 0.0;
		int tInterval = 0;
		double tAccRate, tCalAccRate;
		// 这里计算方式，利率表等根据LMRiskInsuAcc获取
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		tLMRiskInsuAccDB.setInsuAccNo(aInsuAccNo);
		// 如果在表中未找到相关信息，利息置0.0
		if (!tLMRiskInsuAccDB.getInfo()) {
			return calInterest;
		}
		switch (Integer.parseInt(tLMRiskInsuAccDB.getAccComputeFlag())) {
		case 0:

			// 不计息
			calInterest = 0.0;
			break;
		case 1:

			// 按照固定利率单利生息
			tAccRate = tLMRiskInsuAccDB.getAccRate(); // 从表中获取固定利率
			tCalAccRate = TransAccRate(tAccRate, aRateType, "S", aIntervalType); // 按给定参数更改利率
			tInterval = PubFun.calInterval(aStartDate, aEndDate, aIntervalType); // 计算时间间隔
			calInterest = aOriginMoney
					* getIntvRate(tInterval, tCalAccRate, "S"); // 计算利息
			break;
		case 2:

			// 按照固定利率复利生息
			tAccRate = tLMRiskInsuAccDB.getAccRate(); // 从表中获取固定利率
			tCalAccRate = TransAccRate(tAccRate, aRateType, "C", aIntervalType); // 按给定参数更改利率
			tInterval = PubFun.calInterval(aStartDate, aEndDate, aIntervalType); // 计算时间间隔
			calInterest = aOriginMoney
					* getIntvRate(tInterval, tCalAccRate, "C"); // 计算利息
			break;
		case 3:
			String[] ResultRate = getMultiAccRate(aInsuAccNo, tLMRiskInsuAccDB
					.getSchema(), aStartDate, aEndDate, aRateType, "S",
					aIntervalType); // 根据提供表获取分段利率
			for (int m = 0; m < ResultRate.length; m++) {
				// 杨红添加此段代码，添加开始
				if (ResultRate[m] == null) {
					ResultRate[m] = "0";
				}
				// 添加结束
				tCalAccRate = Double.parseDouble(ResultRate[m]);
				double tSubInterest = aOriginMoney * tCalAccRate;
				calInterest += tSubInterest;
			}
			break;
		case 4:
			String[] ResultRate2 = getMultiAccRate(aInsuAccNo, tLMRiskInsuAccDB
					.getSchema(), aStartDate, aEndDate, aRateType, "C",
					aIntervalType); // 根据提供表获取分段利率
			for (int m = 0; m < ResultRate2.length; m++) {
				// 杨红添加此段代码，添加开始
				if (ResultRate2[m] == null) {
					ResultRate2[m] = "0";
				}
				// 添加结束
				tCalAccRate = Double.parseDouble(ResultRate2[m]);
				double tSubInterest = aOriginMoney * tCalAccRate;
				calInterest += tSubInterest;
			}
			break;

		}
		return calInterest;
	}

	/**
	 * 按照新的帐户结息的逻辑进行处理 计算帐户分类表的利息，适用于理赔、保全等有追溯情况的结息方式
	 *
	 * @param aLCInsureAccClassDB
	 *            LCInsureAccClassDB
	 * @param aBalaDate
	 *            String 结息日期
	 * @param aRateType
	 *            String 原始利率类型，比如年利率，月利率等
	 * @param aIntvType
	 *            String 目标利率类型，比如年利率，月利率等
	 * @return double
	 */
	public double getAccClassInterest(LCInsureAccClassDB aLCInsureAccClassDB,
			String aBalaDate, String aRateType, String aIntvType) {
		ExeSQL tExeSQL = new ExeSQL();
		double aAccClassInterest = 0.0;
		int tInterval = 0;
		double tAccClassRate, tCalAccClassRate;
		double tInsuAccClassBala = aLCInsureAccClassDB.getInsuAccBala();
		// 得到描述表中帐户利率
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		String aInsuAccNo = aLCInsureAccClassDB.getInsuAccNo();
		String tBalaDate = aLCInsureAccClassDB.getBalaDate(); // 注意与参数aBalaDate区分,此为帐户分类表上次结息日期
		// 判断真正的结息日
		//判断指定结息日aBalaDate 前的真正的结息日
		boolean isInterest = false;
		LCInsureAccTraceDB aLCInsureAccTraceDB = new LCInsureAccTraceDB();
		String tsql = "select nvl(c.aa,d.aa) from (select max(a.paydate) aa from LCInsureAccTrace a where a.polno = '"
				+ aLCInsureAccClassDB.getPolNo()
				+ "' and a.InsuAccNo = '"
				+ aLCInsureAccClassDB.getInsuAccNo()
				+ "' and a.payplancode = '"
				+ aLCInsureAccClassDB.getPayPlanCode()
				+ "' and a.moneytype = 'LX' and a.paydate <='"
				+ aBalaDate
				+ "') c ,(select min(b.paydate) aa from LCInsureAccTrace b where b.polno = '"
				+ aLCInsureAccClassDB.getPolNo()
				+ "' and b.InsuAccNo = '"
				+ aLCInsureAccClassDB.getInsuAccNo()
				+ "' and b.payplancode = '"
				+ aLCInsureAccClassDB.getPayPlanCode() + "') d";
		System.out.println(tsql);
		tBalaDate = StrTool.cTrim(tExeSQL.getOneValue(tsql));
		if ("".equals(tBalaDate)) {
			System.out.println("无轨迹！");
			return 0;
		}
		aLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
		aLCInsureAccTraceDB.setInsuAccNo(aLCInsureAccClassDB.getInsuAccNo());
		aLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB.getPayPlanCode());
		aLCInsureAccTraceDB.setPayDate(tBalaDate);
		aLCInsureAccTraceDB.setMoneyType("LX");
		if (aLCInsureAccTraceDB.query().size() >= 1)
			isInterest = true;

		tLMRiskInsuAccDB.setInsuAccNo(aInsuAccNo);
		LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
		LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet();
		double tInsureAccTraceMoneySum = 0.0;
		String[] ResultRate = null;
		if (!tLMRiskInsuAccDB.getInfo()) {
			return aAccClassInterest;
		}
		switch (Integer.parseInt(tLMRiskInsuAccDB.getAccComputeFlag())) {
		case 0:

			// 不计息
			break;
		case 1:
			tAccClassRate = tLMRiskInsuAccDB.getAccRate(); // 获取固定利率

			tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "S",
					aIntvType); // 转换后的利率值

			// 筛选出paydate>帐户分类表的结息日期BalaDate

			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB
					.getPayPlanCode());
			tLCInsureAccTraceDB.setOtherNo(aLCInsureAccClassDB.getOtherNo());
			tLCInsureAccTraceDB.setAccAscription(aLCInsureAccClassDB
					.getAccAscription());

			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = (LCInsureAccTraceDB) tLCInsureAccTraceSet
						.get(i);
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				int tIntv = PubFun.calInterval(tBalaDate, payDate, aIntvType);
				if (tIntv > 0) {
					// 处理轨迹表中paydate>帐户分类表的结息日期BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// 然后对轨迹表的这笔钱tempMoney作结息
					tInterval = PubFun.calInterval(payDate, aBalaDate,
							aIntvType);
					aAccClassInterest += tempMoney
							* getIntvRate(tInterval, tCalAccClassRate, "S");
				}
			}
			tInsuAccClassBala -= tInsureAccTraceMoneySum; // 将帐户分类表的另一部分余额结息
			tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			aAccClassInterest += tInsuAccClassBala
					* getIntvRate(tInterval, tCalAccClassRate, "S");
			break;
		case 2:
			tAccClassRate = tLMRiskInsuAccDB.getAccRate(); // 获取固定利率

			tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "C",
					aIntvType); // 转换后的利率值

			// 筛选出paydate>帐户分类表的结息日期BalaDate
			// LCInsureAccTraceDB tLCInsureAccTraceDB = new
			// LCInsureAccTraceDB();
			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB
					.getPayPlanCode());
			tLCInsureAccTraceDB.setOtherNo(aLCInsureAccClassDB.getOtherNo());
			tLCInsureAccTraceDB.setAccAscription(aLCInsureAccClassDB
					.getAccAscription());

			// LCInsureAccTraceSet tLCInsureAccTraceSet=new
			// LCInsureAccTraceSet();
			// double tInsureAccTraceMoneySum=0.0;
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = (LCInsureAccTraceDB) tLCInsureAccTraceSet
						.get(i);
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				int tIntv = PubFun.calInterval(tBalaDate, payDate, aIntvType);
				if (tIntv > 0) {
					// 处理轨迹表中paydate>帐户分类表的结息日期BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// 然后对轨迹表的这笔钱tempMoney作结息
					tInterval = PubFun.calInterval(payDate, aBalaDate,
							aIntvType);
					aAccClassInterest += tempMoney
							* getIntvRate(tInterval, tCalAccClassRate, "C");
				}
			}
			tInsuAccClassBala -= tInsureAccTraceMoneySum; // 将帐户分类表的另一部分余额结息
			tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			aAccClassInterest += tInsuAccClassBala
					* getIntvRate(tInterval, tCalAccClassRate, "C");
			break;
		case 3:

			// tAccClassRate = tLMRiskInsuAccDB.getAccRate();//获取固定利率

			// tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "C",
			// aIntvType);//转换后的利率值
			// 筛选出paydate>帐户分类表的结息日期BalaDate
			// LCInsureAccTraceDB tLCInsureAccTraceDB = new
			// LCInsureAccTraceDB();
			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB
					.getPayPlanCode());
			tLCInsureAccTraceDB.setOtherNo(aLCInsureAccClassDB.getOtherNo());
			tLCInsureAccTraceDB.setAccAscription(aLCInsureAccClassDB
					.getAccAscription());

			// LCInsureAccTraceSet tLCInsureAccTraceSet=new
			// LCInsureAccTraceSet();
			// double tInsureAccTraceMoneySum=0.0;
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = (LCInsureAccTraceDB) tLCInsureAccTraceSet
						.get(i);
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				int tIntv = PubFun.calInterval(tBalaDate, payDate, aIntvType);
				if (tIntv > 0) {
					// 处理轨迹表中paydate>帐户分类表的结息日期BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// 然后对轨迹表的这笔钱tempMoney作结息
					// tInterval = PubFun.calInterval(payDate, aBalaDate,
					// aIntvType);
					String[] ResultRate2 = getMultiAccRate(aInsuAccNo,
							tLMRiskInsuAccDB.getSchema(), payDate, aBalaDate,
							aRateType, "S", aIntvType);
					for (int m = 0; m < ResultRate2.length; m++) {
						// 杨红添加此段代码，添加开始
						if (ResultRate2[m] == null) {
							ResultRate2[m] = "0";
						}
						tCalAccClassRate = Double.parseDouble(ResultRate2[m]);
						double tSubInterest = tempMoney * tCalAccClassRate;
						aAccClassInterest += tSubInterest;
					}

					// aAccClassInterest+=tempMoney*getIntvRate(tInterval,
					// tCalAccClassRate,
					// "C");
				}
			}
			tInsuAccClassBala -= tInsureAccTraceMoneySum; // 将帐户分类表的另一部分余额结息

			// tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			ResultRate = getMultiAccRate(aInsuAccNo, tLMRiskInsuAccDB
					.getSchema(), tBalaDate, aBalaDate, aRateType, "S",
					aIntvType);
			for (int m = 0; m < ResultRate.length; m++) {
				// 杨红添加此段代码，添加开始
				if (ResultRate[m] == null) {
					ResultRate[m] = "0";
				}
				tCalAccClassRate = Double.parseDouble(ResultRate[m]);
				aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
			}

			// aAccClassInterest+=tInsuAccClassBala*getIntvRate(tInterval,tCalAccClassRate,"C");
			break;
		case 4:// 银保万能险
			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB
					.getPayPlanCode());
			tLCInsureAccTraceDB.setOtherNo(aLCInsureAccClassDB.getOtherNo());
			tLCInsureAccTraceDB.setAccAscription(aLCInsureAccClassDB
					.getAccAscription());

			// LCInsureAccTraceSet tLCInsureAccTraceSet=new
			// LCInsureAccTraceSet();
			// double tInsureAccTraceMoneySum=0.0;
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = tLCInsureAccTraceSet
						.get(i).getDB();
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				int tIntv = PubFun.calInterval(payDate, aBalaDate, aIntvType);
				// 判定交费日期和结息日的间隔，如果间隔为正，需要对其进行利息计算
				// 同时需要判定交费日期大于上次结息日期或帐户生成日期
				if (tIntv > 0 && payDate.compareTo(tBalaDate) > 0) {

					// 处理轨迹表中paydate>帐户分类表的结息日期BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// 然后对轨迹表的这笔钱tempMoney作结息
					// tInterval = PubFun.calInterval(payDate, aBalaDate,
					// aIntvType);
					// String[] ResultRate2 = getMultiAccRate(aInsuAccNo
					// , tLMRiskInsuAccDB.getSchema(), payDate, aBalaDate,
					// aRateType, "C"
					// , aIntvType);
					String[] ResultRate2 = getMultiAccRate(aInsuAccNo,
							tLMRiskInsuAccDB.getSchema(), payDate, aBalaDate,
							aRateType, "C", aIntvType);
					double tmpIntrest = 1;
					for (int m = 0; m < ResultRate2.length; m++) {
						// 杨红添加此段代码，添加开始
						if (ResultRate2[m] == null) {
							ResultRate2[m] = "0";
						}
						tCalAccClassRate = Double.parseDouble(ResultRate2[m]);
						// tmpMoneyAddIntrest+=tempMoney * (tCalAccClassRate+1);
						tmpIntrest = tmpIntrest * (tCalAccClassRate + 1);
						// double tSubInterest = tempMoney * tCalAccClassRate;
						// aAccClassInterest += tSubInterest;
					}
					aAccClassInterest += tempMoney * (tmpIntrest - 1);
					System.out.println("每笔部分领取及追加本金的利息" + aAccClassInterest);
					// aAccClassInterest+=tempMoney*getIntvRate(tInterval,
					// tCalAccClassRate,
					// "C");
				}
			}
			System.out.println("部分领取及追加本金的利息" + aAccClassInterest);

			// 修改程序为正面推算后，这步操作就没有必要执行了
			// tInsuAccClassBala += tInsureAccTraceMoneySum; //将帐户分类表的另一部分余额结息

			// tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			ResultRate = getMultiAccRate(aInsuAccNo, tLMRiskInsuAccDB
					.getSchema(), tBalaDate, aBalaDate, aRateType, "C",
					aIntvType);
			double tmpInterst = 1;
			for (int m = 0; m < ResultRate.length; m++) {
				// 杨红添加此段代码，添加开始
				if (ResultRate[m] == null) {
					ResultRate[m] = "0";
				}
				tCalAccClassRate = Double.parseDouble(ResultRate[m]);
				// System.out.println(tCalAccClassRate + "第二次的日利率");
				tmpInterst = tmpInterst * (1 + tCalAccClassRate);
				// aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
			}

			// System.out.println(tInsuAccClassBala * (tmpInterst - 1)) ;
			aAccClassInterest += tInsuAccClassBala * (tmpInterst - 1);
			System.out.println("理赔或退保时的利息" + aAccClassInterest);

			// aAccClassInterest+=tInsuAccClassBala*getIntvRate(tInterval,tCalAccClassRate,"C");
			break;
		case 5:// 团险
			// 长城帐户结息方法（非银保万能险）－－－Alex
			// 根据帐户轨迹，满足一年的按年复利计算，剩余不足一年的按年单利计算，单利为年复利×不足一年的天数/365
			// 长城这里acc和accClass是一一对应的，所以polno+InsuAccNo就够了。
			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			// tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB.getPayPlanCode());
			// tLCInsureAccTraceDB.setOtherNo(aLCInsureAccClassDB.getOtherNo());
			// tLCInsureAccTraceDB.setAccAscription(aLCInsureAccClassDB.getAccAscription());

			// LCInsureAccTraceSet tLCInsureAccTraceSet=new
			// LCInsureAccTraceSet();
			// double tInsureAccTraceMoneySum=0.0;
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = tLCInsureAccTraceSet
						.get(i).getDB();
				// paydate 为起息日！
				String payDate = PubFun.getLastDate(tmpLCInsuAccTraceDB
						.getPayDate(), -1, "D");
				// 账户转移的特殊处理。
				if ("AO".equals(tmpLCInsuAccTraceDB.getMoneyType())
						|| "AI".equals(tmpLCInsuAccTraceDB.getMoneyType()))
					payDate = tmpLCInsuAccTraceDB.getPayDate();
				// System.out.println("payDate:-->"+payDate);

				// tBalaDate之前的所有轨迹，从tBalaDate开始计算利息，tBalaDate（含）之后的按payDate计算利息
				// 但是现在精算给的标准是paydate就是计（起）息日，所以用>、<还是用>=、<=有待再评估。lb_:)
				// 精算标准：如果payDate == aBalaDate（期望结息日），算一天利息。
				if (isInterest && payDate.compareTo(tBalaDate) < 0) {
					payDate = tBalaDate;
				}
				double tempMoney = tmpLCInsuAccTraceDB.getMoney();
				int tIntv = PubFun.calInterval(payDate, aBalaDate, aIntvType);
				// 先计算是否有超过一年的轨迹，aIntvType="Y",如果有需要先用年复利进行计算
				if (payDate.compareTo(PubFun.getLastDate(tBalaDate, -1, "Y")) > 0) {
					// StringBuffer tSBql = new StringBuffer(128);
					// tSBql.append("select to_date('");
					// tSBql.append(payDate);
					// tSBql.append("', 'YYYY-MM-DD') - to_date('");
					// tSBql.append(tBalaDate);
					// tSBql.append("', 'YYYY-MM-DD') from dual");
					// SSRS aSSRS = new ExeSQL().execSQL(tSBql.toString());
					// String tSameDate = "";
					// if(aSSRS!=null&&aSSRS.getMaxRow()>0)
					// tSameDate= aSSRS.GetText(1, 1);
					// 只有判定交费日期大于上次结息日期或帐户生成日期的才先算年复利
					if (tIntv > 0) {
						// 处理轨迹表中paydate>帐户分类表的结息日期BalaDate

						tInsureAccTraceMoneySum += tempMoney;
						// 然后对轨迹表的这笔钱tempMoney作结息
						// tInterval = PubFun.calInterval(payDate, aBalaDate,
						// aIntvType);
						// String[] ResultRate2 = getMultiAccRate(aInsuAccNo
						// , tLMRiskInsuAccDB.getSchema(), payDate, aBalaDate,
						// aRateType, "C"
						// , aIntvType);
						String[] ResultRate2 = getMultiAccRate(aInsuAccNo,
								tLMRiskInsuAccDB.getSchema(), payDate, PubFun
										.getLastDate(payDate, tIntv, "Y"),
								aRateType, "C", aIntvType);
						double tmpIntrest = 1;
						for (int m = 0; m < ResultRate2.length; m++) {
							// 杨红添加此段代码，添加开始
							if (ResultRate2[m] == null) {
								ResultRate2[m] = "0";
							}
							tCalAccClassRate = Double
									.parseDouble(ResultRate2[m]);
							// tmpMoneyAddIntrest+=tempMoney *
							// (tCalAccClassRate+1);
							tmpIntrest = tmpIntrest * (tCalAccClassRate + 1);
							// double tSubInterest = tempMoney *
							// tCalAccClassRate;
							// aAccClassInterest += tSubInterest;
						}
						aAccClassInterest += tempMoney * (tmpIntrest - 1);

						// 计算完年复利后剩余不足一年的用日单利计算
						String[] ResultRate3 = getMultiAccRate(aInsuAccNo,
								tLMRiskInsuAccDB.getSchema(), PubFun
										.getLastDate(payDate, tIntv, "Y"),
								aBalaDate, aRateType, "S", "D");
						double tmpIntrest3 = 1;
						for (int m = 0; m < ResultRate3.length; m++) {
							// 杨红添加此段代码，添加开始
							if (ResultRate3[m] == null) {
								ResultRate3[m] = "0";
							}
							tCalAccClassRate = Double
									.parseDouble(ResultRate3[m]);
							// tmpMoneyAddIntrest+=tempMoney *
							// (tCalAccClassRate+1);
							tmpIntrest3 = tmpIntrest3 * (tCalAccClassRate + 1);
							// double tSubInterest = tempMoney *
							// tCalAccClassRate;
							// aAccClassInterest += tSubInterest;
						}
						aAccClassInterest += tempMoney * tmpIntrest
								* (tmpIntrest3 - 1); // 是在算完复利的基础上算单利，复利为单利本金，所以用tempMoney
						// *tmpIntrest做基数
						// 单利计算结束

						System.out
								.println("每笔部分领取及追加本金的利息" + aAccClassInterest);
						// aAccClassInterest+=tempMoney*getIntvRate(tInterval,
						// tCalAccClassRate,
						// "C");
					} else {
						int tIntvDay = PubFun.calInterval(payDate, aBalaDate,
								"D");
						if (tIntvDay > 0) {
							// 完全不足一年的轨迹直接计算单利
							String[] ResultRate3 = getMultiAccRate(aInsuAccNo,
									tLMRiskInsuAccDB.getSchema(), payDate,
									aBalaDate, aRateType, "S", "D");
							double tmpIntrest3 = 1;
							for (int m = 0; m < ResultRate3.length; m++) {
								// 杨红添加此段代码，添加开始
								if (ResultRate3[m] == null) {
									ResultRate3[m] = "0";
								}
								tCalAccClassRate = Double
										.parseDouble(ResultRate3[m]);
								// tmpMoneyAddIntrest+=tempMoney *
								// (tCalAccClassRate+1);
								tmpIntrest3 = tmpIntrest3
										* (tCalAccClassRate + 1);
								// double tSubInterest = tempMoney *
								// tCalAccClassRate;
								// aAccClassInterest += tSubInterest;
							}
							aAccClassInterest += tempMoney * (tmpIntrest3 - 1);

						}
						// 算一天利息，但是如果tBalaDate == payDate，表示当天已经结过一次息了，不再进行结息
						else if (tIntvDay == 0 && !isInterest) {
							String tTableName = tLMRiskInsuAccDB
									.getAccRateTable();
							// 取出有效复利年利率
							StringBuffer tSBql = new StringBuffer(128);
							tSBql = new StringBuffer(128);
							tSBql.append("select Rate,EndDate from ");
							tSBql.append(tTableName);
							tSBql.append(" where InsuAccNo='");
							tSBql.append(aInsuAccNo);
							tSBql.append("' and StartDate<='");
							tSBql.append(payDate);
							tSBql.append("' and EndDate>='");
							tSBql.append(payDate);
							tSBql
									.append("' and RateType = 'C' and RateIntv = 'Y'");
							tSBql.append(" order by EndDate desc");
							SSRS tSSRS = new ExeSQL().execSQL(tSBql.toString());
							if (tSSRS != null && tSSRS.getMaxRow() == 1) {
								tCalAccClassRate = Double.parseDouble(tSSRS
										.GetText(1, 1));
								aAccClassInterest += tempMoney
										* tCalAccClassRate / 365;
							}
						}// 单利计算结束
					}
				}

			}
			System.out.println("部分领取及追加本金的利息" + aAccClassInterest);

			// 修改程序为正面推算后，这步操作就没有必要执行了
			// tInsuAccClassBala += tInsureAccTraceMoneySum; //将帐户分类表的另一部分余额结息

			// tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			// ResultRate = getMultiAccRate(aInsuAccNo,
			// tLMRiskInsuAccDB.getSchema(), tBalaDate
			// , aBalaDate, aRateType, "S", "D");
			// tmpInterst = 1;
			// for (int m = 0; m < ResultRate.length; m++)
			// {
			// //杨红添加此段代码，添加开始
			// if (ResultRate[m] == null)
			// {
			// ResultRate[m] = "0";
			// }
			// tCalAccClassRate = Double.parseDouble(ResultRate[m]);
			// System.out.println(tCalAccClassRate + "第二次的日利率");
			// tmpInterst = tmpInterst * (1 + tCalAccClassRate);
			// // aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
			// }
			//
			// System.out.println(tInsuAccClassBala * (tmpInterst - 1)) ;
			// aAccClassInterest += tInsuAccClassBala * (tmpInterst - 1);
			// System.out.println("理赔或退保时的利息" + aAccClassInterest);

			// aAccClassInterest+=tInsuAccClassBala*getIntvRate(tInterval,tCalAccClassRate,"C");
			break;
		case 6:
			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB
					.getPayPlanCode());
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			// if(aLCInsureAccClassDB.getPayPlanCode().equals("620103"))
			// {
			// System.out.println("Here");
			// }
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = tLCInsureAccTraceSet
						.get(i).getDB();
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				// tBalaDate之前的所有轨迹，从tBalaDate开始计算利息，tBalaDate（含）之后的按payDate计算利息
				if (isInterest && payDate.compareTo(tBalaDate) < 0) {
					payDate = tBalaDate;
				}
				double tempMoney = tmpLCInsuAccTraceDB.getMoney();
				int tIntv = PubFun.calInterval(payDate, aBalaDate, "D");
				// 日复利
				String rateDate = payDate;
				double tmpIntrest = 1;
				for (int j = 1; j <= tIntv; j++) {
					rateDate = PubFun.getLastDate(rateDate, 1, "D");
					double rate = queryRateFromInterest000001(tLMRiskInsuAccDB
							.getAccRateTable(), aInsuAccNo, aRateType,
							aIntvType, rateDate);
					tmpIntrest = Arith.mul(tmpIntrest, Arith.add(rate, 1));
				}
				aAccClassInterest = Arith.add(Arith.mul(tempMoney, Arith.sub(
						tmpIntrest, 1)), aAccClassInterest);

			}

			break;
		case 7:// 中意万能
			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB
					.getPayPlanCode());
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = tLCInsureAccTraceSet
						.get(i).getDB();
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				if(payDate!=null&&!payDate.equals(""))
				{
					if (isInterest && payDate.compareTo(tBalaDate) <= 0) {
						payDate = PubFun.calDate(tBalaDate, 1, "D", null) ;
					}
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					int tIntv = PubFun.calInterval(payDate, aBalaDate, "D");
					if (tIntv >= 0) {
						double tmpIntrest = 1;
						String tPayDateBak = payDate;
						for (int j = 0; j <= tIntv; j++) {
							tPayDateBak = PubFun.getLastDate(tPayDateBak, 1, "D");
							double tRate = this.queryRateFromInterest000001(
									tLMRiskInsuAccDB.getAccRateTable(),
									tmpLCInsuAccTraceDB.getInsuAccNo(), aRateType,
									aIntvType, tPayDateBak);
							String tOriginRateType = "1";
							if (aIntvType.equals("M"))
								tOriginRateType = "2";
							if (aIntvType.equals("D"))
								tOriginRateType = "3";
							// 转换为日利率
							tCalAccClassRate = TransAccRate(tRate, tOriginRateType,
									aRateType, "D");
							tmpIntrest = Arith.mul(tmpIntrest, Arith.add(
									tCalAccClassRate, 1));
						}

						aAccClassInterest = Arith.add(Arith.mul(tempMoney, Arith
								.sub(tmpIntrest, 1)), aAccClassInterest);

						System.out.println("每笔部分领取及追加本金的利息" + aAccClassInterest);

					}
				}
			}
			System.out.println("部分领取及追加本金的利息" + aAccClassInterest);

			break;

		case 8:// 中意万能:先把年利率转换成月复利，再转换成日单利, 算头又算尾
//			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
//			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
//			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB
//					.getPayPlanCode());
			//只计算指定计息日aBalaDate前的轨迹
			tsql = "select * from LCInsureAccTrace where polno = '"
					+ aLCInsureAccClassDB.getPolNo() + "' and InsuAccNo = '"
					+ aInsuAccNo + "' and PayPlanCode  = '"
					+ aLCInsureAccClassDB.getPayPlanCode()
					+ "' and paydate <='" + aBalaDate + "'";
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.executeQuery(tsql);
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = tLCInsureAccTraceSet
						.get(i).getDB();
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				if(payDate!=null&&!payDate.equals(""))
				{
					if (isInterest && payDate.compareTo(tBalaDate) <= 0) {
						payDate = PubFun.calDate(tBalaDate, 1, "D", null) ;
					}
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					int tIntv = PubFun.calInterval(payDate, aBalaDate, "D");
					if (tIntv >= 0) {
						double tmpIntrest = 1;
						String tPayDateBak = payDate;
						for (int j = 0; j <= tIntv; j++) {
							int monthLength = PubFun.monthLength(tPayDateBak);
							int yearLength = PubFun.isLeapYear(tPayDateBak) ? 366 : 365;

							double tRate = this.queryRateFromInterest000001(
									tLMRiskInsuAccDB.getAccRateTable(),
									tmpLCInsuAccTraceDB.getInsuAccNo(), aRateType,
									aIntvType, tPayDateBak);
							String tOriginRateType = "1";
							if (aIntvType.equals("M"))
								tOriginRateType = "2";
							if (aIntvType.equals("D"))
								tOriginRateType = "3";

							//转换为日单利
							tCalAccClassRate = TransAccRate(tRate, tOriginRateType,
									"S", "D", monthLength, yearLength);

							// 转换为单利日利率
							/*tCalAccClassRate = TransAccRate(tCalAccClassRate, "2",
									"S", "D", monthLength, yearLength);*/

							tmpIntrest = Arith.add(tmpIntrest, tCalAccClassRate);

							tPayDateBak = PubFun.getLastDate(tPayDateBak, 1, "D");
						}

						aAccClassInterest = Arith.add(Arith.mul(tempMoney, Arith
								.sub(tmpIntrest, 1)), aAccClassInterest);

						System.out.println("每笔部分领取及追加本金的利息" + aAccClassInterest);

					}
				}
			}
			System.out.println("部分领取及追加本金的利息" + aAccClassInterest);

			break;

		}
		return Arith.round(aAccClassInterest, 2);
	}


	/**
	 * 按照新的帐户结息的逻辑进行处理 计算帐户分类表的利息，适用于理赔、保全等有追溯情况的结息方式
	 *
	 * @param aLCInsureAccTraceDB
	 *            LCInsureAccTraceDB
	 * @param aBalaDate
	 *            String 结息日期
	 * @param aRateType
	 *            String 原始利率类型，比如年利率，月利率等
	 * @param aIntvType
	 *            String 目标利率类型，比如年利率，月利率等
	 * @param aBqFlag
	 *            String 保全标记，1保全，其他
	 * @return double
	 */
	public double getAccTraceInterest(LCInsureAccTraceDB aLCInsureAccTraceDB,
			String aBalaDate, String aRateType, String aIntvType, String aBqFlag) {

		ExeSQL tExeSQL = new ExeSQL();
		double aAccClassInterest = 0.0; // 最后计算得到的利息
		String tBalaDate; // 上次计息日期

		// 得到描述表中帐户利率
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		String aInsuAccNo = aLCInsureAccTraceDB.getInsuAccNo();

		// 判断指定结息日aBalaDate 前的真正的结息日
		boolean isInterest = false;
		String tsql = "select to_char(max(a.paydate),'yyyy-mm-dd')  from LCInsureAccTrace a where a.polno = '"
				+ aLCInsureAccTraceDB.getPolNo()
				+ "' and a.InsuAccNo = '"
				+ aLCInsureAccTraceDB.getInsuAccNo()
				+ "' and a.payplancode = '"
				+ aLCInsureAccTraceDB.getPayPlanCode()
				+ "' and a.moneytype in ('LX','BX') and a.paydate <='"  //查上次结息日时，也包括按保证利率结的BX利息记录
				+ aBalaDate
				+ "'";
		if (StrTool.cTrim(aLCInsureAccTraceDB.getMainSerialNo()).equals("")){
			tsql += " and MainSerialNo is null";
		}else{
			tsql += " and MainSerialNo = '"+ aLCInsureAccTraceDB.getMainSerialNo() + "'";
		}
		if (StrTool.cTrim(aLCInsureAccTraceDB.getPromiseStartDate()).equals(""))
			tsql += " and PromiseStartDate is null";
		else
			tsql += " and PromiseStartDate  = '"
					+ aLCInsureAccTraceDB.getPromiseStartDate() + "'";
		tBalaDate = StrTool.cTrim(tExeSQL.getOneValue(tsql));
		if ("".equals(tBalaDate)) {
			String tSQL = "select to_char(min(b.paydate),'yyyy-mm-dd') from LCInsureAccTrace b where b.polno = '"
					+ aLCInsureAccTraceDB.getPolNo()
					+ "' and b.InsuAccNo = '"
					+ aLCInsureAccTraceDB.getInsuAccNo()
					+ "' and b.payplancode = '"
					+ aLCInsureAccTraceDB.getPayPlanCode() + "'";
			if (StrTool.cTrim(aLCInsureAccTraceDB.getMainSerialNo()).equals("")){
				tSQL += " and MainSerialNo is null";
			}else{
				tSQL += " and MainSerialNo = '"+ aLCInsureAccTraceDB.getMainSerialNo() + "'";
			}
			if (StrTool.cTrim(aLCInsureAccTraceDB.getPromiseStartDate())
					.equals(""))
				tSQL += " and PromiseStartDate is null";
			else
				tSQL += " and PromiseStartDate  = '"
						+ aLCInsureAccTraceDB.getPromiseStartDate() + "'";

			tBalaDate = StrTool.cTrim(tExeSQL.getOneValue(tSQL));
			if ("".equals(tBalaDate)) {
				System.out.println("没有账户轨迹表记录！");
				return 0;
			}
		} else {
			isInterest = true;
		}

		tLMRiskInsuAccDB.setInsuAccNo(aInsuAccNo);
		LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
		LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet();
		if (!tLMRiskInsuAccDB.getInfo()) {
			return aAccClassInterest;
		}
		switch (Integer.parseInt(tLMRiskInsuAccDB.getAccComputeFlag())) {
		case 0:
			// 不计息
			break;

		case 8:// 中意万能:日单利, 算头又算尾
			// 分段处理，先查询上次结息日与当前结息日之间的记录
			tsql = "select * from LCInsureAccTrace where polno = '"
					+ aLCInsureAccTraceDB.getPolNo() + "' and InsuAccNo = '"
					+ aInsuAccNo + "' and PayPlanCode  = '"
					+ aLCInsureAccTraceDB.getPayPlanCode()
					+ "' and money <>0 and paydate <='" + aBalaDate + "'";
			if (StrTool.cTrim(aLCInsureAccTraceDB.getMainSerialNo()).equals("")){
				tsql += " and MainSerialNo is null";
			}else{
				tsql += " and MainSerialNo = '"+ aLCInsureAccTraceDB.getMainSerialNo() + "'";
			}
			if (aLCInsureAccTraceDB.getPromiseStartDate() == null
					|| aLCInsureAccTraceDB.getPromiseStartDate().equals("")) {
				tsql += " and PromiseStartDate is null";
			} else {
				tsql += " and PromiseStartDate = '"
						+ aLCInsureAccTraceDB.getPromiseStartDate() + "'";
			}
			if (isInterest) {
				tsql += " and paydate >'" + tBalaDate + "'";
			}
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.executeQuery(tsql);
			if (isInterest) {
				LCInsureAccTraceSchema tLCInsureAccTraceSchema = new LCInsureAccTraceSchema();
				tsql = "select nvl(sum(money),0) from LCInsureAccTrace where polno = '"
						+ aLCInsureAccTraceDB.getPolNo()
						+ "' and InsuAccNo = '"
						+ aInsuAccNo
						+ "' and PayPlanCode  = '"
						+ aLCInsureAccTraceDB.getPayPlanCode()
						+ "' and money <>0 and paydate <='"
						+ aBalaDate
						+ "' and paydate <='" + tBalaDate + "'";
				if (StrTool.cTrim(aLCInsureAccTraceDB.getMainSerialNo()).equals("")){
					tsql += " and MainSerialNo is null";
				}else{
					tsql += " and MainSerialNo = '"+ aLCInsureAccTraceDB.getMainSerialNo() + "'";
				}
				if (aLCInsureAccTraceDB.getPromiseStartDate() == null
						|| aLCInsureAccTraceDB.getPromiseStartDate().equals("")) {
					tsql += " and PromiseStartDate is null";
				} else {
					tsql += " and PromiseStartDate = '"
							+ aLCInsureAccTraceDB.getPromiseStartDate() + "'";
				}
				String tMoney = tExeSQL.getOneValue(tsql);
				tLCInsureAccTraceSchema.setSchema(aLCInsureAccTraceDB
						.getSchema());
				tLCInsureAccTraceSchema.setPayDate(tBalaDate);
				tLCInsureAccTraceSchema.setMoney(tMoney);
				tLCInsureAccTraceSet.add(tLCInsureAccTraceSchema);
			}
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = tLCInsureAccTraceSet
						.get(i).getDB();
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				if (payDate != null && !payDate.equals("")) {
					if (isInterest && payDate.compareTo(tBalaDate) <= 0) {
						payDate = PubFun.calDate(tBalaDate, 1, "D", null);
					}
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					int tIntv = PubFun.calInterval(payDate, aBalaDate, "D");
					if (tIntv >= 0) {
						double tmpIntrest = 1;
						String tPayDateBak = payDate;
						for (int j = 0; j <= tIntv; j++) {
							int monthLength = PubFun.monthLength(tPayDateBak);
							int yearLength = PubFun.isLeapYear(tPayDateBak) ? 366
									: 365;
							double tRate = 0.0;
							if (aBqFlag != null && aBqFlag.equals("1")) {
								tRate = tmpLCInsuAccTraceDB.getPromiseRate();
								if (tRate == 0.0) {
									tRate = this.queryRateFromLDPromiseRate(
											"LDPromiseRate",
											tmpLCInsuAccTraceDB.getRiskCode(),
											aRateType, aIntvType, tPayDateBak);
								}
							} else {
								tRate = this.queryRateFromInterest000001(
										tLMRiskInsuAccDB.getAccRateTable(),
										tmpLCInsuAccTraceDB.getInsuAccNo(),
										aRateType, aIntvType, tPayDateBak);
								if (tRate <= tmpLCInsuAccTraceDB
										.getPromiseRate()) {
									tRate = tmpLCInsuAccTraceDB
											.getPromiseRate();
								}
							}

							String tOriginRateType = "1";
							if (aIntvType.equals("M"))
								tOriginRateType = "2";
							if (aIntvType.equals("D"))
								tOriginRateType = "3";

							// 转换为日单利
							double tCalAccClassRate = TransAccRate(tRate,
									tOriginRateType, "S", "D", monthLength,
									yearLength);

							tmpIntrest = Arith
									.add(tmpIntrest, tCalAccClassRate);

							tPayDateBak = PubFun.getLastDate(tPayDateBak, 1,
									"D");
						}

						aAccClassInterest = Arith.add(Arith.mul(tempMoney,
								Arith.sub(tmpIntrest, 1)), aAccClassInterest);

						System.out
								.println("每笔部分领取及追加本金的利息" + aAccClassInterest);

					}
				}
			}
			System.out.println("部分领取及追加本金的利息" + aAccClassInterest);

			break;

		}
		return Arith.round(aAccClassInterest, 2);
	}



	/**
	 * 按照新的帐户结息的逻辑进行处理 计算帐户分类表的利息，适用于理赔、保全等有追溯情况的结息方式
	 *
	 * @param aLCInsureAccClassDB
	 *            LCInsureAccClassDB
	 * @param aBalaDate
	 *            String 结息日期
	 * @param aRateType
	 *            String 原始利率类型，比如年利率，月利率等
	 * @param aIntvType
	 *            String 目标利率类型，比如年利率，月利率等
	 * @return double
	 */
	public double getAccClassInterest(LCInsureAccClassDB aLCInsureAccClassDB,
			String aBalaDate, String aRateType, String aIntvType , String aBqFlag) {
		ExeSQL tExeSQL = new ExeSQL();
		double aAccClassInterest = 0.0;
		int tInterval = 0;
		double tAccClassRate, tCalAccClassRate;
		double tInsuAccClassBala = aLCInsureAccClassDB.getInsuAccBala();
		// 得到描述表中帐户利率
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		String aInsuAccNo = aLCInsureAccClassDB.getInsuAccNo();
		String tBalaDate = aLCInsureAccClassDB.getBalaDate(); // 注意与参数aBalaDate区分,此为帐户分类表上次结息日期
		// 判断真正的结息日
		//判断指定结息日aBalaDate 前的真正的结息日
		boolean isInterest = false;
		LCInsureAccTraceDB aLCInsureAccTraceDB = new LCInsureAccTraceDB();
		String tsql = "select nvl(c.aa,d.aa) from (select max(a.paydate) aa from LCInsureAccTrace a where a.polno = '"
				+ aLCInsureAccClassDB.getPolNo()
				+ "' and a.InsuAccNo = '"
				+ aLCInsureAccClassDB.getInsuAccNo()
				+ "' and a.payplancode = '"
				+ aLCInsureAccClassDB.getPayPlanCode()
				+ "' and a.moneytype = 'LX' and a.paydate <='"
				+ aBalaDate
				+ "') c ,(select min(b.paydate) aa from LCInsureAccTrace b where b.polno = '"
				+ aLCInsureAccClassDB.getPolNo()
				+ "' and b.InsuAccNo = '"
				+ aLCInsureAccClassDB.getInsuAccNo()
				+ "' and b.payplancode = '"
				+ aLCInsureAccClassDB.getPayPlanCode() + "') d";
		System.out.println(tsql);
		tBalaDate = StrTool.cTrim(tExeSQL.getOneValue(tsql));
		if ("".equals(tBalaDate)) {
			System.out.println("无轨迹！");
			return 0;
		}
		aLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
		aLCInsureAccTraceDB.setInsuAccNo(aLCInsureAccClassDB.getInsuAccNo());
		aLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB.getPayPlanCode());
		aLCInsureAccTraceDB.setPayDate(tBalaDate);
		aLCInsureAccTraceDB.setMoneyType("LX");
		if (aLCInsureAccTraceDB.query().size() >= 1)
			isInterest = true;

		tLMRiskInsuAccDB.setInsuAccNo(aInsuAccNo);
		LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
		LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet();
		double tInsureAccTraceMoneySum = 0.0;
		String[] ResultRate = null;
		if (!tLMRiskInsuAccDB.getInfo()) {
			return aAccClassInterest;
		}
		switch (Integer.parseInt(tLMRiskInsuAccDB.getAccComputeFlag())) {
		case 0:

			// 不计息
			break;
		case 1:
			tAccClassRate = tLMRiskInsuAccDB.getAccRate(); // 获取固定利率

			tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "S",
					aIntvType); // 转换后的利率值

			// 筛选出paydate>帐户分类表的结息日期BalaDate

			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB
					.getPayPlanCode());
			tLCInsureAccTraceDB.setOtherNo(aLCInsureAccClassDB.getOtherNo());
			tLCInsureAccTraceDB.setAccAscription(aLCInsureAccClassDB
					.getAccAscription());

			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = (LCInsureAccTraceDB) tLCInsureAccTraceSet
						.get(i);
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				int tIntv = PubFun.calInterval(tBalaDate, payDate, aIntvType);
				if (tIntv > 0) {
					// 处理轨迹表中paydate>帐户分类表的结息日期BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// 然后对轨迹表的这笔钱tempMoney作结息
					tInterval = PubFun.calInterval(payDate, aBalaDate,
							aIntvType);
					aAccClassInterest += tempMoney
							* getIntvRate(tInterval, tCalAccClassRate, "S");
				}
			}
			tInsuAccClassBala -= tInsureAccTraceMoneySum; // 将帐户分类表的另一部分余额结息
			tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			aAccClassInterest += tInsuAccClassBala
					* getIntvRate(tInterval, tCalAccClassRate, "S");
			break;
		case 2:
			tAccClassRate = tLMRiskInsuAccDB.getAccRate(); // 获取固定利率

			tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "C",
					aIntvType); // 转换后的利率值

			// 筛选出paydate>帐户分类表的结息日期BalaDate
			// LCInsureAccTraceDB tLCInsureAccTraceDB = new
			// LCInsureAccTraceDB();
			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB
					.getPayPlanCode());
			tLCInsureAccTraceDB.setOtherNo(aLCInsureAccClassDB.getOtherNo());
			tLCInsureAccTraceDB.setAccAscription(aLCInsureAccClassDB
					.getAccAscription());

			// LCInsureAccTraceSet tLCInsureAccTraceSet=new
			// LCInsureAccTraceSet();
			// double tInsureAccTraceMoneySum=0.0;
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = (LCInsureAccTraceDB) tLCInsureAccTraceSet
						.get(i);
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				int tIntv = PubFun.calInterval(tBalaDate, payDate, aIntvType);
				if (tIntv > 0) {
					// 处理轨迹表中paydate>帐户分类表的结息日期BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// 然后对轨迹表的这笔钱tempMoney作结息
					tInterval = PubFun.calInterval(payDate, aBalaDate,
							aIntvType);
					aAccClassInterest += tempMoney
							* getIntvRate(tInterval, tCalAccClassRate, "C");
				}
			}
			tInsuAccClassBala -= tInsureAccTraceMoneySum; // 将帐户分类表的另一部分余额结息
			tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			aAccClassInterest += tInsuAccClassBala
					* getIntvRate(tInterval, tCalAccClassRate, "C");
			break;
		case 3:

			// tAccClassRate = tLMRiskInsuAccDB.getAccRate();//获取固定利率

			// tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "C",
			// aIntvType);//转换后的利率值
			// 筛选出paydate>帐户分类表的结息日期BalaDate
			// LCInsureAccTraceDB tLCInsureAccTraceDB = new
			// LCInsureAccTraceDB();
			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB
					.getPayPlanCode());
			tLCInsureAccTraceDB.setOtherNo(aLCInsureAccClassDB.getOtherNo());
			tLCInsureAccTraceDB.setAccAscription(aLCInsureAccClassDB
					.getAccAscription());

			// LCInsureAccTraceSet tLCInsureAccTraceSet=new
			// LCInsureAccTraceSet();
			// double tInsureAccTraceMoneySum=0.0;
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = (LCInsureAccTraceDB) tLCInsureAccTraceSet
						.get(i);
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				int tIntv = PubFun.calInterval(tBalaDate, payDate, aIntvType);
				if (tIntv > 0) {
					// 处理轨迹表中paydate>帐户分类表的结息日期BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// 然后对轨迹表的这笔钱tempMoney作结息
					// tInterval = PubFun.calInterval(payDate, aBalaDate,
					// aIntvType);
					String[] ResultRate2 = getMultiAccRate(aInsuAccNo,
							tLMRiskInsuAccDB.getSchema(), payDate, aBalaDate,
							aRateType, "S", aIntvType);
					for (int m = 0; m < ResultRate2.length; m++) {
						// 杨红添加此段代码，添加开始
						if (ResultRate2[m] == null) {
							ResultRate2[m] = "0";
						}
						tCalAccClassRate = Double.parseDouble(ResultRate2[m]);
						double tSubInterest = tempMoney * tCalAccClassRate;
						aAccClassInterest += tSubInterest;
					}

					// aAccClassInterest+=tempMoney*getIntvRate(tInterval,
					// tCalAccClassRate,
					// "C");
				}
			}
			tInsuAccClassBala -= tInsureAccTraceMoneySum; // 将帐户分类表的另一部分余额结息

			// tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			ResultRate = getMultiAccRate(aInsuAccNo, tLMRiskInsuAccDB
					.getSchema(), tBalaDate, aBalaDate, aRateType, "S",
					aIntvType);
			for (int m = 0; m < ResultRate.length; m++) {
				// 杨红添加此段代码，添加开始
				if (ResultRate[m] == null) {
					ResultRate[m] = "0";
				}
				tCalAccClassRate = Double.parseDouble(ResultRate[m]);
				aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
			}

			// aAccClassInterest+=tInsuAccClassBala*getIntvRate(tInterval,tCalAccClassRate,"C");
			break;
		case 4:// 银保万能险
			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB
					.getPayPlanCode());
			tLCInsureAccTraceDB.setOtherNo(aLCInsureAccClassDB.getOtherNo());
			tLCInsureAccTraceDB.setAccAscription(aLCInsureAccClassDB
					.getAccAscription());

			// LCInsureAccTraceSet tLCInsureAccTraceSet=new
			// LCInsureAccTraceSet();
			// double tInsureAccTraceMoneySum=0.0;
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = tLCInsureAccTraceSet
						.get(i).getDB();
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				int tIntv = PubFun.calInterval(payDate, aBalaDate, aIntvType);
				// 判定交费日期和结息日的间隔，如果间隔为正，需要对其进行利息计算
				// 同时需要判定交费日期大于上次结息日期或帐户生成日期
				if (tIntv > 0 && payDate.compareTo(tBalaDate) > 0) {

					// 处理轨迹表中paydate>帐户分类表的结息日期BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// 然后对轨迹表的这笔钱tempMoney作结息
					// tInterval = PubFun.calInterval(payDate, aBalaDate,
					// aIntvType);
					// String[] ResultRate2 = getMultiAccRate(aInsuAccNo
					// , tLMRiskInsuAccDB.getSchema(), payDate, aBalaDate,
					// aRateType, "C"
					// , aIntvType);
					String[] ResultRate2 = getMultiAccRate(aInsuAccNo,
							tLMRiskInsuAccDB.getSchema(), payDate, aBalaDate,
							aRateType, "C", aIntvType);
					double tmpIntrest = 1;
					for (int m = 0; m < ResultRate2.length; m++) {
						// 杨红添加此段代码，添加开始
						if (ResultRate2[m] == null) {
							ResultRate2[m] = "0";
						}
						tCalAccClassRate = Double.parseDouble(ResultRate2[m]);
						// tmpMoneyAddIntrest+=tempMoney * (tCalAccClassRate+1);
						tmpIntrest = tmpIntrest * (tCalAccClassRate + 1);
						// double tSubInterest = tempMoney * tCalAccClassRate;
						// aAccClassInterest += tSubInterest;
					}
					aAccClassInterest += tempMoney * (tmpIntrest - 1);
					System.out.println("每笔部分领取及追加本金的利息" + aAccClassInterest);
					// aAccClassInterest+=tempMoney*getIntvRate(tInterval,
					// tCalAccClassRate,
					// "C");
				}
			}
			System.out.println("部分领取及追加本金的利息" + aAccClassInterest);

			// 修改程序为正面推算后，这步操作就没有必要执行了
			// tInsuAccClassBala += tInsureAccTraceMoneySum; //将帐户分类表的另一部分余额结息

			// tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			ResultRate = getMultiAccRate(aInsuAccNo, tLMRiskInsuAccDB
					.getSchema(), tBalaDate, aBalaDate, aRateType, "C",
					aIntvType);
			double tmpInterst = 1;
			for (int m = 0; m < ResultRate.length; m++) {
				// 杨红添加此段代码，添加开始
				if (ResultRate[m] == null) {
					ResultRate[m] = "0";
				}
				tCalAccClassRate = Double.parseDouble(ResultRate[m]);
				// System.out.println(tCalAccClassRate + "第二次的日利率");
				tmpInterst = tmpInterst * (1 + tCalAccClassRate);
				// aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
			}

			// System.out.println(tInsuAccClassBala * (tmpInterst - 1)) ;
			aAccClassInterest += tInsuAccClassBala * (tmpInterst - 1);
			System.out.println("理赔或退保时的利息" + aAccClassInterest);

			// aAccClassInterest+=tInsuAccClassBala*getIntvRate(tInterval,tCalAccClassRate,"C");
			break;
		case 5:// 团险
			// 长城帐户结息方法（非银保万能险）－－－Alex
			// 根据帐户轨迹，满足一年的按年复利计算，剩余不足一年的按年单利计算，单利为年复利×不足一年的天数/365
			// 长城这里acc和accClass是一一对应的，所以polno+InsuAccNo就够了。
			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			// tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB.getPayPlanCode());
			// tLCInsureAccTraceDB.setOtherNo(aLCInsureAccClassDB.getOtherNo());
			// tLCInsureAccTraceDB.setAccAscription(aLCInsureAccClassDB.getAccAscription());

			// LCInsureAccTraceSet tLCInsureAccTraceSet=new
			// LCInsureAccTraceSet();
			// double tInsureAccTraceMoneySum=0.0;
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = tLCInsureAccTraceSet
						.get(i).getDB();
				// paydate 为起息日！
				String payDate = PubFun.getLastDate(tmpLCInsuAccTraceDB
						.getPayDate(), -1, "D");
				// 账户转移的特殊处理。
				if ("AO".equals(tmpLCInsuAccTraceDB.getMoneyType())
						|| "AI".equals(tmpLCInsuAccTraceDB.getMoneyType()))
					payDate = tmpLCInsuAccTraceDB.getPayDate();
				// System.out.println("payDate:-->"+payDate);

				// tBalaDate之前的所有轨迹，从tBalaDate开始计算利息，tBalaDate（含）之后的按payDate计算利息
				// 但是现在精算给的标准是paydate就是计（起）息日，所以用>、<还是用>=、<=有待再评估。lb_:)
				// 精算标准：如果payDate == aBalaDate（期望结息日），算一天利息。
				if (isInterest && payDate.compareTo(tBalaDate) < 0) {
					payDate = tBalaDate;
				}
				double tempMoney = tmpLCInsuAccTraceDB.getMoney();
				int tIntv = PubFun.calInterval(payDate, aBalaDate, aIntvType);
				// 先计算是否有超过一年的轨迹，aIntvType="Y",如果有需要先用年复利进行计算
				if (payDate.compareTo(PubFun.getLastDate(tBalaDate, -1, "Y")) > 0) {
					// StringBuffer tSBql = new StringBuffer(128);
					// tSBql.append("select to_date('");
					// tSBql.append(payDate);
					// tSBql.append("', 'YYYY-MM-DD') - to_date('");
					// tSBql.append(tBalaDate);
					// tSBql.append("', 'YYYY-MM-DD') from dual");
					// SSRS aSSRS = new ExeSQL().execSQL(tSBql.toString());
					// String tSameDate = "";
					// if(aSSRS!=null&&aSSRS.getMaxRow()>0)
					// tSameDate= aSSRS.GetText(1, 1);
					// 只有判定交费日期大于上次结息日期或帐户生成日期的才先算年复利
					if (tIntv > 0) {
						// 处理轨迹表中paydate>帐户分类表的结息日期BalaDate

						tInsureAccTraceMoneySum += tempMoney;
						// 然后对轨迹表的这笔钱tempMoney作结息
						// tInterval = PubFun.calInterval(payDate, aBalaDate,
						// aIntvType);
						// String[] ResultRate2 = getMultiAccRate(aInsuAccNo
						// , tLMRiskInsuAccDB.getSchema(), payDate, aBalaDate,
						// aRateType, "C"
						// , aIntvType);
						String[] ResultRate2 = getMultiAccRate(aInsuAccNo,
								tLMRiskInsuAccDB.getSchema(), payDate, PubFun
										.getLastDate(payDate, tIntv, "Y"),
								aRateType, "C", aIntvType);
						double tmpIntrest = 1;
						for (int m = 0; m < ResultRate2.length; m++) {
							// 杨红添加此段代码，添加开始
							if (ResultRate2[m] == null) {
								ResultRate2[m] = "0";
							}
							tCalAccClassRate = Double
									.parseDouble(ResultRate2[m]);
							// tmpMoneyAddIntrest+=tempMoney *
							// (tCalAccClassRate+1);
							tmpIntrest = tmpIntrest * (tCalAccClassRate + 1);
							// double tSubInterest = tempMoney *
							// tCalAccClassRate;
							// aAccClassInterest += tSubInterest;
						}
						aAccClassInterest += tempMoney * (tmpIntrest - 1);

						// 计算完年复利后剩余不足一年的用日单利计算
						String[] ResultRate3 = getMultiAccRate(aInsuAccNo,
								tLMRiskInsuAccDB.getSchema(), PubFun
										.getLastDate(payDate, tIntv, "Y"),
								aBalaDate, aRateType, "S", "D");
						double tmpIntrest3 = 1;
						for (int m = 0; m < ResultRate3.length; m++) {
							// 杨红添加此段代码，添加开始
							if (ResultRate3[m] == null) {
								ResultRate3[m] = "0";
							}
							tCalAccClassRate = Double
									.parseDouble(ResultRate3[m]);
							// tmpMoneyAddIntrest+=tempMoney *
							// (tCalAccClassRate+1);
							tmpIntrest3 = tmpIntrest3 * (tCalAccClassRate + 1);
							// double tSubInterest = tempMoney *
							// tCalAccClassRate;
							// aAccClassInterest += tSubInterest;
						}
						aAccClassInterest += tempMoney * tmpIntrest
								* (tmpIntrest3 - 1); // 是在算完复利的基础上算单利，复利为单利本金，所以用tempMoney
						// *tmpIntrest做基数
						// 单利计算结束

						System.out
								.println("每笔部分领取及追加本金的利息" + aAccClassInterest);
						// aAccClassInterest+=tempMoney*getIntvRate(tInterval,
						// tCalAccClassRate,
						// "C");
					} else {
						int tIntvDay = PubFun.calInterval(payDate, aBalaDate,
								"D");
						if (tIntvDay > 0) {
							// 完全不足一年的轨迹直接计算单利
							String[] ResultRate3 = getMultiAccRate(aInsuAccNo,
									tLMRiskInsuAccDB.getSchema(), payDate,
									aBalaDate, aRateType, "S", "D");
							double tmpIntrest3 = 1;
							for (int m = 0; m < ResultRate3.length; m++) {
								// 杨红添加此段代码，添加开始
								if (ResultRate3[m] == null) {
									ResultRate3[m] = "0";
								}
								tCalAccClassRate = Double
										.parseDouble(ResultRate3[m]);
								// tmpMoneyAddIntrest+=tempMoney *
								// (tCalAccClassRate+1);
								tmpIntrest3 = tmpIntrest3
										* (tCalAccClassRate + 1);
								// double tSubInterest = tempMoney *
								// tCalAccClassRate;
								// aAccClassInterest += tSubInterest;
							}
							aAccClassInterest += tempMoney * (tmpIntrest3 - 1);

						}
						// 算一天利息，但是如果tBalaDate == payDate，表示当天已经结过一次息了，不再进行结息
						else if (tIntvDay == 0 && !isInterest) {
							String tTableName = tLMRiskInsuAccDB
									.getAccRateTable();
							// 取出有效复利年利率
							StringBuffer tSBql = new StringBuffer(128);
							tSBql = new StringBuffer(128);
							tSBql.append("select Rate,EndDate from ");
							tSBql.append(tTableName);
							tSBql.append(" where InsuAccNo='");
							tSBql.append(aInsuAccNo);
							tSBql.append("' and StartDate<='");
							tSBql.append(payDate);
							tSBql.append("' and EndDate>='");
							tSBql.append(payDate);
							tSBql
									.append("' and RateType = 'C' and RateIntv = 'Y'");
							tSBql.append(" order by EndDate desc");
							SSRS tSSRS = new ExeSQL().execSQL(tSBql.toString());
							if (tSSRS != null && tSSRS.getMaxRow() == 1) {
								tCalAccClassRate = Double.parseDouble(tSSRS
										.GetText(1, 1));
								aAccClassInterest += tempMoney
										* tCalAccClassRate / 365;
							}
						}// 单利计算结束
					}
				}

			}
			System.out.println("部分领取及追加本金的利息" + aAccClassInterest);

			// 修改程序为正面推算后，这步操作就没有必要执行了
			// tInsuAccClassBala += tInsureAccTraceMoneySum; //将帐户分类表的另一部分余额结息

			// tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			// ResultRate = getMultiAccRate(aInsuAccNo,
			// tLMRiskInsuAccDB.getSchema(), tBalaDate
			// , aBalaDate, aRateType, "S", "D");
			// tmpInterst = 1;
			// for (int m = 0; m < ResultRate.length; m++)
			// {
			// //杨红添加此段代码，添加开始
			// if (ResultRate[m] == null)
			// {
			// ResultRate[m] = "0";
			// }
			// tCalAccClassRate = Double.parseDouble(ResultRate[m]);
			// System.out.println(tCalAccClassRate + "第二次的日利率");
			// tmpInterst = tmpInterst * (1 + tCalAccClassRate);
			// // aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
			// }
			//
			// System.out.println(tInsuAccClassBala * (tmpInterst - 1)) ;
			// aAccClassInterest += tInsuAccClassBala * (tmpInterst - 1);
			// System.out.println("理赔或退保时的利息" + aAccClassInterest);

			// aAccClassInterest+=tInsuAccClassBala*getIntvRate(tInterval,tCalAccClassRate,"C");
			break;
		case 6:
			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB
					.getPayPlanCode());
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			// if(aLCInsureAccClassDB.getPayPlanCode().equals("620103"))
			// {
			// System.out.println("Here");
			// }
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = tLCInsureAccTraceSet
						.get(i).getDB();
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				// tBalaDate之前的所有轨迹，从tBalaDate开始计算利息，tBalaDate（含）之后的按payDate计算利息
				if (isInterest && payDate.compareTo(tBalaDate) < 0) {
					payDate = tBalaDate;
				}
				double tempMoney = tmpLCInsuAccTraceDB.getMoney();
				int tIntv = PubFun.calInterval(payDate, aBalaDate, "D");
				// 日复利
				String rateDate = payDate;
				double tmpIntrest = 1;
				for (int j = 1; j <= tIntv; j++) {
					rateDate = PubFun.getLastDate(rateDate, 1, "D");
					double rate = queryRateFromInterest000001(tLMRiskInsuAccDB
							.getAccRateTable(), aInsuAccNo, aRateType,
							aIntvType, rateDate);
					tmpIntrest = Arith.mul(tmpIntrest, Arith.add(rate, 1));
				}
				aAccClassInterest = Arith.add(Arith.mul(tempMoney, Arith.sub(
						tmpIntrest, 1)), aAccClassInterest);

			}

			break;
		case 7:// 中意万能
			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB
					.getPayPlanCode());
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = tLCInsureAccTraceSet
						.get(i).getDB();
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				if(payDate!=null&&!payDate.equals(""))
				{
					if (isInterest && payDate.compareTo(tBalaDate) <= 0) {
						payDate = PubFun.calDate(tBalaDate, 1, "D", null) ;
					}
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					int tIntv = PubFun.calInterval(payDate, aBalaDate, "D");
					if (tIntv >= 0) {
						double tmpIntrest = 1;
						String tPayDateBak = payDate;
						for (int j = 0; j <= tIntv; j++) {
							tPayDateBak = PubFun.getLastDate(tPayDateBak, 1, "D");
							double tRate = this.queryRateFromInterest000001(
									tLMRiskInsuAccDB.getAccRateTable(),
									tmpLCInsuAccTraceDB.getInsuAccNo(), aRateType,
									aIntvType, tPayDateBak);
							String tOriginRateType = "1";
							if (aIntvType.equals("M"))
								tOriginRateType = "2";
							if (aIntvType.equals("D"))
								tOriginRateType = "3";
							// 转换为日利率
							tCalAccClassRate = TransAccRate(tRate, tOriginRateType,
									aRateType, "D");
							tmpIntrest = Arith.mul(tmpIntrest, Arith.add(
									tCalAccClassRate, 1));
						}

						aAccClassInterest = Arith.add(Arith.mul(tempMoney, Arith
								.sub(tmpIntrest, 1)), aAccClassInterest);

						System.out.println("每笔部分领取及追加本金的利息" + aAccClassInterest);

					}
				}
			}
			System.out.println("部分领取及追加本金的利息" + aAccClassInterest);

			break;

		case 8:// 中意万能:先把年利率转换成月复利，再转换成日单利, 算头又算尾
//			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
//			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
//			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB
//					.getPayPlanCode());
			//只计算指定计息日aBalaDate前的轨迹
			tsql = "select * from LCInsureAccTrace where polno = '"
					+ aLCInsureAccClassDB.getPolNo() + "' and InsuAccNo = '"
					+ aInsuAccNo + "' and PayPlanCode  = '"
					+ aLCInsureAccClassDB.getPayPlanCode()
					+ "' and paydate <='" + aBalaDate + "'";
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.executeQuery(tsql);
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = tLCInsureAccTraceSet
						.get(i).getDB();
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				if(payDate!=null&&!payDate.equals(""))
				{
					if (isInterest && payDate.compareTo(tBalaDate) <= 0) {
						payDate = PubFun.calDate(tBalaDate, 1, "D", null) ;
					}
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					int tIntv = PubFun.calInterval(payDate, aBalaDate, "D");
					if (tIntv >= 0) {
						double tmpIntrest = 1;
						String tPayDateBak = payDate;
						for (int j = 0; j <= tIntv; j++) {
							int monthLength = PubFun.monthLength(tPayDateBak);
							int yearLength = PubFun.isLeapYear(tPayDateBak) ? 366 : 365;
							double tRate = 0.0;
							if(aBqFlag != null && aBqFlag.equals("1")){
								tRate = this.queryRateFromLDPromiseRate(
										"LDPromiseRate",
										tmpLCInsuAccTraceDB.getRiskCode(), aRateType,
										aIntvType, tPayDateBak);
							}else{
								tRate = this.queryRateFromInterest000001(
										tLMRiskInsuAccDB.getAccRateTable(),
										tmpLCInsuAccTraceDB.getInsuAccNo(), aRateType,
										aIntvType, tPayDateBak);
							}

							String tOriginRateType = "1";
							if (aIntvType.equals("M"))
								tOriginRateType = "2";
							if (aIntvType.equals("D"))
								tOriginRateType = "3";

							//转换为日单利
							tCalAccClassRate = TransAccRate(tRate, tOriginRateType,
									"S", "D", monthLength, yearLength);

							// 转换为单利日利率
							/*tCalAccClassRate = TransAccRate(tCalAccClassRate, "2",
									"S", "D", monthLength, yearLength);*/

							tmpIntrest = Arith.add(tmpIntrest, tCalAccClassRate);

							tPayDateBak = PubFun.getLastDate(tPayDateBak, 1, "D");
						}

						aAccClassInterest = Arith.add(Arith.mul(tempMoney, Arith
								.sub(tmpIntrest, 1)), aAccClassInterest);

						System.out.println("每笔部分领取及追加本金的利息" + aAccClassInterest);

					}
				}
			}
			System.out.println("部分领取及追加本金的利息" + aAccClassInterest);

			break;

		}
		return Arith.round(aAccClassInterest, 2);
	}



	public double queryRateFromInterest000001(String aTableName,
			String insuAccNo, String rateType, String intvType, String rateDate) {
		synchronized (AccountManage.class) {
//			String key = aTableName + "#" + insuAccNo + "#" + rateType + "#"
//					+ intvType;
//			Vector specRateList = (Vector) rateCacheMap.get(key);
//			if (specRateList == null || specRateList.size() == 0) {
				StringBuffer tSBql = new StringBuffer(128);
				tSBql.append("select startdate,enddate,rate from " + aTableName
						+ " where insuaccno = '");
				tSBql.append(insuAccNo);
				tSBql.append("' and RateType = '" + rateType
						+ "' and RateIntv = '" + intvType + "'");
				SSRS resultSSRS = new ExeSQL().execSQL(tSBql.toString());
				if (resultSSRS != null && resultSSRS.getMaxRow() != 0) {
//					specRateList = new Vector();
					for (int i = 1; i <= resultSSRS.MaxRow; i++) {
						String resultArray[] = new String[3];
						resultArray[0] = resultSSRS.GetText(i, 1);
						resultArray[1] = resultSSRS.GetText(i, 2);
						resultArray[2] = resultSSRS.GetText(i, 3);
//						specRateList.add(resultArray);
                                                if (resultArray[0].compareTo(rateDate) <= 0
                                                        && resultArray[1].compareTo(rateDate) >= 0) {
                                                return Double.parseDouble(resultArray[2]);
                                                }
					}
				}
//				rateCacheMap.put(key, specRateList);
//			}
//			if (specRateList != null) {
//				for (int i = 0; i < specRateList.size(); i++) {
//					String resultArray[] = (String[]) specRateList.get(i);
//					String startDate = resultArray[0];
//					String endDate = resultArray[1];
//					String rateStr = resultArray[2];
//					if (startDate.compareTo(rateDate) <= 0
//							&& endDate.compareTo(rateDate) >= 0) {
//						return Double.parseDouble(rateStr);
//					}
//				}
			return 0;
		}
	}


	public double queryRateFromLDPromiseRate(String aTableName,
			String riskCode, String rateType, String intvType, String rateDate) {
		synchronized (AccountManage.class) {
//			String key = aTableName + "#" + riskCode + "#" + rateType + "#"
//					+ intvType;
//			Vector specRateList = (Vector) rateCacheMap.get(key);
//			if (specRateList == null || specRateList.size() == 0) {
				StringBuffer tSBql = new StringBuffer(128);
				tSBql.append("select startdate,enddate,rate from " + aTableName
						+ " where riskCode = '");
				tSBql.append(riskCode);
				tSBql.append("' and RateType = '" + rateType
						+ "' and RateIntv = '" + intvType + "'");
				SSRS resultSSRS = new ExeSQL().execSQL(tSBql.toString());
				if (resultSSRS != null && resultSSRS.getMaxRow() != 0) {
//					specRateList = new Vector();
					for (int i = 1; i <= resultSSRS.MaxRow; i++) {
						String resultArray[] = new String[3];
						resultArray[0] = resultSSRS.GetText(i, 1);
						resultArray[1] = resultSSRS.GetText(i, 2);
						resultArray[2] = resultSSRS.GetText(i, 3);

//						specRateList.add(resultArray);
                                                if (resultArray[0].compareTo(rateDate) <= 0
                                                       && resultArray[1].compareTo(rateDate) >= 0) {
                                                       return Double.parseDouble(resultArray[2]);
                                                   }
					}
				}
//				rateCacheMap.put(key, specRateList);
//			}
//			if (specRateList != null) {
//				for (int i = 0; i < specRateList.size(); i++) {
//					String resultArray[] = (String[]) specRateList.get(i);
//					String startDate = resultArray[0];
//					String endDate = resultArray[1];
//					String rateStr = resultArray[2];
//					if (startDate.compareTo(rateDate) <= 0
//							&& endDate.compareTo(rateDate) >= 0) {
//						return Double.parseDouble(rateStr);
//					}
//				}
//			}
                                return 0;
		}
	}



	/**
	 * 按照新的帐户结息的逻辑进行处理 计算帐户分类表的利息，帐户结息时使用 与上面的方法唯一的区别是，一个是正向推算利息，一个是反向推算利息
	 * 还有更重要的区别，该方法是满一年以上的trace才结息，而且结出来的也是整年部分的利息，即不满整年部分的利息不在 返回值中，现在修改case
	 * 5逻辑，与getAccClassInterest方法统一。
	 *
	 * @param aLCInsureAccClassDB
	 *            LCInsureAccClassDB
	 * @param aBalaDate
	 *            String 结息日期
	 * @param aRateType
	 *            String 原始利率类型，比如年利率，月利率等
	 * @param aIntvType
	 *            String 目标利率类型，比如年利率，月利率等
	 * @return double
	 */
	public double getAccClassInterestBalance(
			LCInsureAccClassDB aLCInsureAccClassDB, String aBalaDate,
			String aRateType, String aIntvType) {
		double aAccClassInterest = 0.0;
		int tInterval = 0;
		double tAccClassRate, tCalAccClassRate;
		double tInsuAccClassBala = aLCInsureAccClassDB.getInsuAccBala();
		// 得到描述表中帐户利率
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		String aInsuAccNo = aLCInsureAccClassDB.getInsuAccNo();
		String tBalaDate = aLCInsureAccClassDB.getBalaDate(); // 注意与参数aBalaDate区分,此为帐户分类表上次结息日期
		// 判断真正的结息日
		boolean isInterest = false;
		LCInsureAccTraceDB aLCInsureAccTraceDB = new LCInsureAccTraceDB();
		aLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
		aLCInsureAccTraceDB.setInsuAccNo(aLCInsureAccClassDB.getInsuAccNo());
		aLCInsureAccTraceDB.setPayDate(tBalaDate);
		aLCInsureAccTraceDB.setMoneyType("LX");
		if (aLCInsureAccTraceDB.query().size() >= 1)
			isInterest = true;

		tLMRiskInsuAccDB.setInsuAccNo(aInsuAccNo);
		LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
		LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet();
		double tInsureAccTraceMoneySum = 0.0;
		String[] ResultRate = null;
		if (!tLMRiskInsuAccDB.getInfo()) {
			return aAccClassInterest;
		}
		switch (Integer.parseInt(tLMRiskInsuAccDB.getAccComputeFlag())) {
		case 0:

			// 不计息
			break;
		case 1:
			tAccClassRate = tLMRiskInsuAccDB.getAccRate(); // 获取固定利率

			tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "S",
					aIntvType); // 转换后的利率值

			// 筛选出paydate>帐户分类表的结息日期BalaDate

			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB
					.getPayPlanCode());
			tLCInsureAccTraceDB.setOtherNo(aLCInsureAccClassDB.getOtherNo());
			tLCInsureAccTraceDB.setAccAscription(aLCInsureAccClassDB
					.getAccAscription());

			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = (LCInsureAccTraceDB) tLCInsureAccTraceSet
						.get(i);
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				int tIntv = PubFun.calInterval(tBalaDate, payDate, aIntvType);
				if (tIntv > 0) {
					// 处理轨迹表中paydate>帐户分类表的结息日期BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// 然后对轨迹表的这笔钱tempMoney作结息
					tInterval = PubFun.calInterval(payDate, aBalaDate,
							aIntvType);
					aAccClassInterest += tempMoney
							* getIntvRate(tInterval, tCalAccClassRate, "S");
				}
			}
			tInsuAccClassBala -= tInsureAccTraceMoneySum; // 将帐户分类表的另一部分余额结息
			tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			aAccClassInterest += tInsuAccClassBala
					* getIntvRate(tInterval, tCalAccClassRate, "S");
			break;
		case 2:
			tAccClassRate = tLMRiskInsuAccDB.getAccRate(); // 获取固定利率

			tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "C",
					aIntvType); // 转换后的利率值

			// 筛选出paydate>帐户分类表的结息日期BalaDate
			// LCInsureAccTraceDB tLCInsureAccTraceDB = new
			// LCInsureAccTraceDB();
			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB
					.getPayPlanCode());
			tLCInsureAccTraceDB.setOtherNo(aLCInsureAccClassDB.getOtherNo());
			tLCInsureAccTraceDB.setAccAscription(aLCInsureAccClassDB
					.getAccAscription());

			// LCInsureAccTraceSet tLCInsureAccTraceSet=new
			// LCInsureAccTraceSet();
			// double tInsureAccTraceMoneySum=0.0;
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = (LCInsureAccTraceDB) tLCInsureAccTraceSet
						.get(i);
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				int tIntv = PubFun.calInterval(tBalaDate, payDate, aIntvType);
				if (tIntv > 0) {
					// 处理轨迹表中paydate>帐户分类表的结息日期BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// 然后对轨迹表的这笔钱tempMoney作结息
					tInterval = PubFun.calInterval(payDate, aBalaDate,
							aIntvType);
					aAccClassInterest += tempMoney
							* getIntvRate(tInterval, tCalAccClassRate, "C");
				}
			}
			tInsuAccClassBala -= tInsureAccTraceMoneySum; // 将帐户分类表的另一部分余额结息
			tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			aAccClassInterest += tInsuAccClassBala
					* getIntvRate(tInterval, tCalAccClassRate, "C");
			break;
		case 3:

			// tAccClassRate = tLMRiskInsuAccDB.getAccRate();//获取固定利率

			// tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "C",
			// aIntvType);//转换后的利率值
			// 筛选出paydate>帐户分类表的结息日期BalaDate
			// LCInsureAccTraceDB tLCInsureAccTraceDB = new
			// LCInsureAccTraceDB();
			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB
					.getPayPlanCode());
			tLCInsureAccTraceDB.setOtherNo(aLCInsureAccClassDB.getOtherNo());
			tLCInsureAccTraceDB.setAccAscription(aLCInsureAccClassDB
					.getAccAscription());

			// LCInsureAccTraceSet tLCInsureAccTraceSet=new
			// LCInsureAccTraceSet();
			// double tInsureAccTraceMoneySum=0.0;
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = (LCInsureAccTraceDB) tLCInsureAccTraceSet
						.get(i);
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				int tIntv = PubFun.calInterval(tBalaDate, payDate, aIntvType);
				if (tIntv > 0) {
					// 处理轨迹表中paydate>帐户分类表的结息日期BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// 然后对轨迹表的这笔钱tempMoney作结息
					// tInterval = PubFun.calInterval(payDate, aBalaDate,
					// aIntvType);
					String[] ResultRate2 = getMultiAccRate(aInsuAccNo,
							tLMRiskInsuAccDB.getSchema(), payDate, aBalaDate,
							aRateType, "S", aIntvType);
					for (int m = 0; m < ResultRate2.length; m++) {
						// 杨红添加此段代码，添加开始
						if (ResultRate2[m] == null) {
							ResultRate2[m] = "0";
						}
						tCalAccClassRate = Double.parseDouble(ResultRate2[m]);
						double tSubInterest = tempMoney * tCalAccClassRate;
						aAccClassInterest += tSubInterest;
					}

					// aAccClassInterest+=tempMoney*getIntvRate(tInterval,
					// tCalAccClassRate,
					// "C");
				}
			}
			tInsuAccClassBala -= tInsureAccTraceMoneySum; // 将帐户分类表的另一部分余额结息

			// tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			ResultRate = getMultiAccRate(aInsuAccNo, tLMRiskInsuAccDB
					.getSchema(), tBalaDate, aBalaDate, aRateType, "S",
					aIntvType);
			for (int m = 0; m < ResultRate.length; m++) {
				// 杨红添加此段代码，添加开始
				if (ResultRate[m] == null) {
					ResultRate[m] = "0";
				}
				tCalAccClassRate = Double.parseDouble(ResultRate[m]);
				aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
			}

			// aAccClassInterest+=tInsuAccClassBala*getIntvRate(tInterval,tCalAccClassRate,"C");
			break;
		case 4:

			// 根据利率表复利计息
			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB
					.getPayPlanCode());
			tLCInsureAccTraceDB.setOtherNo(aLCInsureAccClassDB.getOtherNo());
			tLCInsureAccTraceDB.setAccAscription(aLCInsureAccClassDB
					.getAccAscription());

			// LCInsureAccTraceSet tLCInsureAccTraceSet=new
			// LCInsureAccTraceSet();
			// double tInsureAccTraceMoneySum=0.0;
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = tLCInsureAccTraceSet
						.get(i).getDB();
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				int tIntv = PubFun.calInterval(tBalaDate, payDate, aIntvType);
				if (tIntv > 0) {

					// 处理轨迹表中paydate>帐户分类表的结息日期BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// 然后对轨迹表的这笔钱tempMoney作结息
					// tInterval = PubFun.calInterval(payDate, aBalaDate,
					// aIntvType);

					String[] ResultRate2 = getMultiAccRate(aInsuAccNo,
							tLMRiskInsuAccDB.getSchema(), payDate, aBalaDate,
							aRateType, "C", aIntvType);
					double tmpIntrest = 1;
					for (int m = 0; m < ResultRate2.length; m++) {
						// 杨红添加此段代码，添加开始
						if (ResultRate2[m] == null) {
							ResultRate2[m] = "0";
						}
						tCalAccClassRate = Double.parseDouble(ResultRate2[m]);
						// tmpMoneyAddIntrest+=tempMoney * (tCalAccClassRate+1);
						tmpIntrest = tmpIntrest * (tCalAccClassRate + 1);
						// double tSubInterest = tempMoney * tCalAccClassRate;
						// aAccClassInterest += tSubInterest;
					}
					aAccClassInterest += tempMoney * (tmpIntrest - 1);
					// aAccClassInterest+=tempMoney*getIntvRate(tInterval,
					// tCalAccClassRate,
					// "C");
				}
			}
			tInsuAccClassBala -= tInsureAccTraceMoneySum; // 将帐户分类表的另一部分余额结息

			// tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			ResultRate = getMultiAccRate(aInsuAccNo, tLMRiskInsuAccDB
					.getSchema(), tBalaDate, aBalaDate, aRateType, "C",
					aIntvType);
			double tmpInterst = 1;
			for (int m = 0; m < ResultRate.length; m++) {
				// 杨红添加此段代码，添加开始
				if (ResultRate[m] == null) {
					ResultRate[m] = "0";
				}
				tCalAccClassRate = Double.parseDouble(ResultRate[m]);
				// System.out.println(tCalAccClassRate + "第二次的日利率");
				tmpInterst = tmpInterst * (1 + tCalAccClassRate);
				// aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
			}
			aAccClassInterest += tInsuAccClassBala * (tmpInterst - 1);

			// aAccClassInterest+=tInsuAccClassBala*getIntvRate(tInterval,tCalAccClassRate,"C");
			break;
		case 5:
			// 长城帐户结息方法（非银保万能险）－－－Alex
			// 根据帐户轨迹，满足一年的按年复利计算，剩余不足一年不记利息
			// 长城这里acc和accClass是一一对应的，所以polno+InsuAccNo就够了。lb:_)

			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			// tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB.getPayPlanCode());
			// tLCInsureAccTraceDB.setOtherNo(aLCInsureAccClassDB.getOtherNo());
			// tLCInsureAccTraceDB.setAccAscription(aLCInsureAccClassDB.getAccAscription());

			// LCInsureAccTraceSet tLCInsureAccTraceSet=new
			// LCInsureAccTraceSet();
			// double tInsureAccTraceMoneySum=0.0;
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = tLCInsureAccTraceSet
						.get(i).getDB();

				String payDate = PubFun.getLastDate(tmpLCInsuAccTraceDB
						.getPayDate(), -1, "D");
				// 账户转移的特殊处理。
				if ("AO".equals(tmpLCInsuAccTraceDB.getMoneyType())
						|| "AI".equals(tmpLCInsuAccTraceDB.getMoneyType()))
					payDate = tmpLCInsuAccTraceDB.getPayDate();
				// tBalaDate之前的所有轨迹，从tBalaDate开始计算利息，tBalaDate（含）之后的按payDate计算利息
				// 但是现在精算给的标准是paydate就是计（起）息日，所以用>、<还是用>=、<=有待再评估。lb_:)
				// 精算标准：如果payDate == aBalaDate（期望结息日），算一天利息。
				if (isInterest && payDate.compareTo(tBalaDate) < 0) {
					payDate = tBalaDate;
				}
				double tempMoney = tmpLCInsuAccTraceDB.getMoney();
				int tIntv = PubFun.calInterval(payDate, aBalaDate, aIntvType);
				// 先计算是否有超过一年的轨迹，aIntvType="Y",如果有需要先用年复利进行计算
				if (payDate.compareTo(PubFun.getLastDate(tBalaDate, -1, "Y")) > 0) {

					// tSBql.append("select to_date('");
					// tSBql.append(payDate);
					// tSBql.append("', 'YYYY-MM-DD') - to_date('");
					// tSBql.append(tBalaDate);
					// tSBql.append("', 'YYYY-MM-DD') from dual");
					// SSRS aSSRS = new ExeSQL().execSQL(tSBql.toString());
					// String tSameDate = "";
					// if(aSSRS!=null&&aSSRS.getMaxRow()>0)
					// tSameDate= aSSRS.GetText(1, 1);
					/**
					 * //与getAccClassInterest case 5统一。 //
					 * //只有判定交费日期大于上上次结息日期或帐户生成日期的才先算年复利 // if (tIntv > 0 ) // { //
					 * //处理轨迹表中paydate>帐户分类表的结息日期BalaDate // //
					 * tInsureAccTraceMoneySum += tempMoney; //
					 * //然后对轨迹表的这笔钱tempMoney作结息 // // tInterval =
					 * PubFun.calInterval(payDate, aBalaDate, aIntvType); // //
					 * String[] ResultRate2 = getMultiAccRate(aInsuAccNo // // ,
					 * tLMRiskInsuAccDB.getSchema(), payDate, aBalaDate,
					 * aRateType, "C" // // , aIntvType); // String[]
					 * ResultRate2 = getMultiAccRate(aInsuAccNo // ,
					 * tLMRiskInsuAccDB.getSchema(), payDate, //
					 * PubFun.getLastDate(payDate, tIntv, "Y"), // aRateType,
					 * "C" // , aIntvType); // double tmpIntrest = 1; // for
					 * (int m = 0; m < ResultRate2.length; m++) // { //
					 * //杨红添加此段代码，添加开始 // if (ResultRate2[m] == null) // { //
					 * ResultRate2[m] = "0"; // } // tCalAccClassRate =
					 * Double.parseDouble( // ResultRate2[m]); // //
					 * tmpMoneyAddIntrest+=tempMoney * (tCalAccClassRate+1); //
					 * tmpIntrest = tmpIntrest * (tCalAccClassRate + 1); // //
					 * double tSubInterest = tempMoney * tCalAccClassRate; // //
					 * aAccClassInterest += tSubInterest; // } //
					 * aAccClassInterest += tempMoney * (tmpIntrest - 1); // //
					 * System.out.println("每笔部分领取及追加本金的利息" + //
					 * aAccClassInterest); // //
					 * aAccClassInterest+=tempMoney*getIntvRate(tInterval,
					 * tCalAccClassRate, // // "C"); // }
					 */

					// 只有判定交费日期大于上次结息日期或帐户生成日期的才先算年复利
					if (tIntv > 0) {
						// 处理轨迹表中paydate>帐户分类表的结息日期BalaDate

						tInsureAccTraceMoneySum += tempMoney;
						// 然后对轨迹表的这笔钱tempMoney作结息
						// tInterval = PubFun.calInterval(payDate, aBalaDate,
						// aIntvType);
						// String[] ResultRate2 = getMultiAccRate(aInsuAccNo
						// , tLMRiskInsuAccDB.getSchema(), payDate, aBalaDate,
						// aRateType, "C"
						// , aIntvType);
						String[] ResultRate2 = getMultiAccRate(aInsuAccNo,
								tLMRiskInsuAccDB.getSchema(), payDate, PubFun
										.getLastDate(payDate, tIntv, "Y"),
								aRateType, "C", aIntvType);
						double tmpIntrest = 1;
						for (int m = 0; m < ResultRate2.length; m++) {
							// 杨红添加此段代码，添加开始
							if (ResultRate2[m] == null) {
								ResultRate2[m] = "0";
							}
							tCalAccClassRate = Double
									.parseDouble(ResultRate2[m]);
							// tmpMoneyAddIntrest+=tempMoney *
							// (tCalAccClassRate+1);
							tmpIntrest = tmpIntrest * (tCalAccClassRate + 1);
							// double tSubInterest = tempMoney *
							// tCalAccClassRate;
							// aAccClassInterest += tSubInterest;
						}
						aAccClassInterest += tempMoney * (tmpIntrest - 1);

						// 计算完年复利后剩余不足一年的用日单利计算
						String[] ResultRate3 = getMultiAccRate(aInsuAccNo,
								tLMRiskInsuAccDB.getSchema(), PubFun
										.getLastDate(payDate, tIntv, "Y"),
								aBalaDate, aRateType, "S", "D");
						double tmpIntrest3 = 1;
						for (int m = 0; m < ResultRate3.length; m++) {
							// 杨红添加此段代码，添加开始
							if (ResultRate3[m] == null) {
								ResultRate3[m] = "0";
							}
							tCalAccClassRate = Double
									.parseDouble(ResultRate3[m]);
							// tmpMoneyAddIntrest+=tempMoney *
							// (tCalAccClassRate+1);
							tmpIntrest3 = tmpIntrest3 * (tCalAccClassRate + 1);
							// double tSubInterest = tempMoney *
							// tCalAccClassRate;
							// aAccClassInterest += tSubInterest;
						}
						aAccClassInterest += tempMoney * tmpIntrest
								* (tmpIntrest3 - 1); // 是在算完复利的基础上算单利，复利为单利本金，所以用tempMoney
						// *tmpIntrest做基数
						// 单利计算结束

						System.out
								.println("每笔部分领取及追加本金的利息" + aAccClassInterest);
						// aAccClassInterest+=tempMoney*getIntvRate(tInterval,
						// tCalAccClassRate,
						// "C");
					} else {
						int tIntvDay = PubFun.calInterval(payDate, aBalaDate,
								"D");
						if (tIntvDay > 0) {
							// 完全不足一年的轨迹直接计算单利
							String[] ResultRate3 = getMultiAccRate(aInsuAccNo,
									tLMRiskInsuAccDB.getSchema(), payDate,
									aBalaDate, aRateType, "S", "D");
							double tmpIntrest3 = 1;
							for (int m = 0; m < ResultRate3.length; m++) {
								// 杨红添加此段代码，添加开始
								if (ResultRate3[m] == null) {
									ResultRate3[m] = "0";
								}
								tCalAccClassRate = Double
										.parseDouble(ResultRate3[m]);
								// tmpMoneyAddIntrest+=tempMoney *
								// (tCalAccClassRate+1);
								tmpIntrest3 = tmpIntrest3
										* (tCalAccClassRate + 1);
								// double tSubInterest = tempMoney *
								// tCalAccClassRate;
								// aAccClassInterest += tSubInterest;
							}
							aAccClassInterest += tempMoney * (tmpIntrest3 - 1);
							// 单利计算结束
						}
						// 算一天利息，但是如果tBalaDate == payDate，表示当天已经结过一次息了，不再进行结息
						else if (tIntvDay == 0 && !isInterest) {
							String tTableName = tLMRiskInsuAccDB
									.getAccRateTable();
							// 取出有效复利年利率
							StringBuffer tSBql = new StringBuffer(128);
							tSBql.append("select Rate,EndDate from ");
							tSBql.append(tTableName);
							tSBql.append(" where InsuAccNo='");
							tSBql.append(aInsuAccNo);
							tSBql.append("' and StartDate<='");
							tSBql.append(payDate);
							tSBql.append("' and EndDate>='");
							tSBql.append(payDate);
							tSBql
									.append("' and RateType = 'C' and RateIntv = 'Y'");
							tSBql.append(" order by EndDate desc");
							SSRS tSSRS = new ExeSQL().execSQL(tSBql.toString());
							if (tSSRS != null && tSSRS.getMaxRow() == 1) {
								tCalAccClassRate = Double.parseDouble(tSSRS
										.GetText(1, 1));
								aAccClassInterest += tempMoney
										* tCalAccClassRate / 365;
							}
						}// 单利计算结束
					}

				}

			}
			System.out.println("部分领取及追加本金的利息" + aAccClassInterest);

			// 修改程序为正面推算后，这步操作就没有必要执行了
			// tInsuAccClassBala += tInsureAccTraceMoneySum; //将帐户分类表的另一部分余额结息

			// tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			ResultRate = getMultiAccRate(aInsuAccNo, tLMRiskInsuAccDB
					.getSchema(), tBalaDate, aBalaDate, aRateType, "C", "Y");
			tmpInterst = 1;
			for (int m = 0; m < ResultRate.length; m++) {
				// 杨红添加此段代码，添加开始
				if (ResultRate[m] == null) {
					ResultRate[m] = "0";
				}
				tCalAccClassRate = Double.parseDouble(ResultRate[m]);
				// System.out.println(tCalAccClassRate + "第二次的日利率");
				tmpInterst = tmpInterst * (1 + tCalAccClassRate);
				// aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
			}

			// System.out.println(tInsuAccClassBala * (tmpInterst - 1)) ;
			aAccClassInterest += tInsuAccClassBala * (tmpInterst - 1);
			System.out.println("理赔或退保时的利息" + aAccClassInterest);

			// aAccClassInterest+=tInsuAccClassBala*getIntvRate(tInterval,tCalAccClassRate,"C");
			break;
		case 7:// 个险
			// 长城帐户结息方法（非银保万能险）－－－Alex
			// 根据帐户轨迹，满足一年的按年复利计算，剩余不足一年的按年单利计算，单利为年复利×不足一年的天数/365
			// 长城这里acc和accClass是一一对应的，所以polno+InsuAccNo就够了。
			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			// tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB.getPayPlanCode());
			// tLCInsureAccTraceDB.setOtherNo(aLCInsureAccClassDB.getOtherNo());
			// tLCInsureAccTraceDB.setAccAscription(aLCInsureAccClassDB.getAccAscription());

			// LCInsureAccTraceSet tLCInsureAccTraceSet=new
			// LCInsureAccTraceSet();
			// double tInsureAccTraceMoneySum=0.0;
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = tLCInsureAccTraceSet
						.get(i).getDB();
				// paydate 为起息日！
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				// System.out.println("payDate:-->"+payDate);

				// tBalaDate之前的所有轨迹，从tBalaDate开始计算利息，tBalaDate（含）之后的按payDate计算利息
				// 但是现在精算给的标准是paydate就是计（起）息日，所以用>、<还是用>=、<=有待再评估。lb_:)
				// 精算标准：如果payDate == aBalaDate（期望结息日），算一天利息。
				if (isInterest && payDate.compareTo(tBalaDate) < 0) {
					payDate = tBalaDate;
				}
				double tempMoney = tmpLCInsuAccTraceDB.getMoney();
				int tIntv = PubFun.calInterval(payDate, aBalaDate, aIntvType);
				// 先计算是否有超过一年的轨迹，aIntvType="Y",如果有需要先用年复利进行计算
				if (payDate.compareTo(PubFun.getLastDate(tBalaDate, -1, "Y")) > 0) {
					// StringBuffer tSBql = new StringBuffer(128);
					// tSBql.append("select to_date('");
					// tSBql.append(payDate);
					// tSBql.append("', 'YYYY-MM-DD') - to_date('");
					// tSBql.append(tBalaDate);
					// tSBql.append("', 'YYYY-MM-DD') from dual");
					// SSRS aSSRS = new ExeSQL().execSQL(tSBql.toString());
					// String tSameDate = "";
					// if(aSSRS!=null&&aSSRS.getMaxRow()>0)
					// tSameDate= aSSRS.GetText(1, 1);
					// 只有判定交费日期大于上次结息日期或帐户生成日期的才先算年复利
					if (tIntv > 0) {
						// 处理轨迹表中paydate>帐户分类表的结息日期BalaDate

						tInsureAccTraceMoneySum += tempMoney;
						// 然后对轨迹表的这笔钱tempMoney作结息
						// tInterval = PubFun.calInterval(payDate, aBalaDate,
						// aIntvType);
						// String[] ResultRate2 = getMultiAccRate(aInsuAccNo
						// , tLMRiskInsuAccDB.getSchema(), payDate, aBalaDate,
						// aRateType, "C"
						// , aIntvType);
						String[] ResultRate2 = getMultiAccRate(aInsuAccNo,
								tLMRiskInsuAccDB.getSchema(), payDate, PubFun
										.getLastDate(payDate, tIntv, "Y"),
								aRateType, "C", aIntvType);
						double tmpIntrest = 1;
						for (int m = 0; m < ResultRate2.length; m++) {
							// 杨红添加此段代码，添加开始
							if (ResultRate2[m] == null) {
								ResultRate2[m] = "0";
							}
							tCalAccClassRate = Double
									.parseDouble(ResultRate2[m]);
							// tmpMoneyAddIntrest+=tempMoney *
							// (tCalAccClassRate+1);
							tmpIntrest = tmpIntrest * (tCalAccClassRate + 1);
							// double tSubInterest = tempMoney *
							// tCalAccClassRate;
							// aAccClassInterest += tSubInterest;
						}
						aAccClassInterest += tempMoney * (tmpIntrest - 1);

						// 计算完年复利后剩余不足一年的用日单利计算
						String[] ResultRate3 = getMultiAccRate(aInsuAccNo,
								tLMRiskInsuAccDB.getSchema(), PubFun
										.getLastDate(payDate, tIntv, "Y"),
								aBalaDate, aRateType, "S", "D");
						double tmpIntrest3 = 1;
						for (int m = 0; m < ResultRate3.length; m++) {
							// 杨红添加此段代码，添加开始
							if (ResultRate3[m] == null) {
								ResultRate3[m] = "0";
							}
							tCalAccClassRate = Double
									.parseDouble(ResultRate3[m]);
							// tmpMoneyAddIntrest+=tempMoney *
							// (tCalAccClassRate+1);
							tmpIntrest3 = tmpIntrest3 * (tCalAccClassRate + 1);
							// double tSubInterest = tempMoney *
							// tCalAccClassRate;
							// aAccClassInterest += tSubInterest;
						}
						aAccClassInterest += tempMoney * tmpIntrest
								* (tmpIntrest3 - 1); // 是在算完复利的基础上算单利，复利为单利本金，所以用tempMoney
						// *tmpIntrest做基数
						// 单利计算结束

						System.out
								.println("每笔部分领取及追加本金的利息" + aAccClassInterest);
						// aAccClassInterest+=tempMoney*getIntvRate(tInterval,
						// tCalAccClassRate,
						// "C");
					} else {
						int tIntvDay = PubFun.calInterval(payDate, aBalaDate,
								"D");
						if (tIntvDay > 0) {
							// 完全不足一年的轨迹直接计算单利
							String[] ResultRate3 = getMultiAccRate(aInsuAccNo,
									tLMRiskInsuAccDB.getSchema(), payDate,
									aBalaDate, aRateType, "S", "D");
							double tmpIntrest3 = 1;
							for (int m = 0; m < ResultRate3.length; m++) {
								// 杨红添加此段代码，添加开始
								if (ResultRate3[m] == null) {
									ResultRate3[m] = "0";
								}
								tCalAccClassRate = Double
										.parseDouble(ResultRate3[m]);
								// tmpMoneyAddIntrest+=tempMoney *
								// (tCalAccClassRate+1);
								tmpIntrest3 = tmpIntrest3
										* (tCalAccClassRate + 1);
								// double tSubInterest = tempMoney *
								// tCalAccClassRate;
								// aAccClassInterest += tSubInterest;
							}
							aAccClassInterest += tempMoney * (tmpIntrest3 - 1);

						}
						// 算一天利息，但是如果tBalaDate == payDate，表示当天已经结过一次息了，不再进行结息
						// 个险不需要了
						// else if (tIntvDay == 0 && !isInterest) {
						// String tTableName = tLMRiskInsuAccDB
						// .getAccRateTable();
						// // 取出有效复利年利率
						// StringBuffer tSBql = new StringBuffer(128);
						// tSBql = new StringBuffer(128);
						// tSBql.append("select Rate,EndDate from ");
						// tSBql.append(tTableName);
						// tSBql.append(" where InsuAccNo='");
						// tSBql.append(aInsuAccNo);
						// tSBql.append("' and StartDate<='");
						// tSBql.append(payDate);
						// tSBql.append("' and EndDate>='");
						// tSBql.append(payDate);
						// tSBql
						// .append("' and RateType = 'C' and RateIntv = 'Y'");
						// tSBql.append(" order by EndDate desc");
						// SSRS tSSRS = new ExeSQL().execSQL(tSBql.toString());
						// if (tSSRS != null && tSSRS.getMaxRow() == 1) {
						// tCalAccClassRate = Double.parseDouble(tSSRS
						// .GetText(1, 1));
						// aAccClassInterest += tempMoney
						// * tCalAccClassRate / 365;
						// }
						// }// 单利计算结束
					}
				}

			}
			System.out.println("部分领取及追加本金的利息" + aAccClassInterest);

			// 修改程序为正面推算后，这步操作就没有必要执行了
			// tInsuAccClassBala += tInsureAccTraceMoneySum; //将帐户分类表的另一部分余额结息

			// tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			// ResultRate = getMultiAccRate(aInsuAccNo,
			// tLMRiskInsuAccDB.getSchema(), tBalaDate
			// , aBalaDate, aRateType, "S", "D");
			// tmpInterst = 1;
			// for (int m = 0; m < ResultRate.length; m++)
			// {
			// //杨红添加此段代码，添加开始
			// if (ResultRate[m] == null)
			// {
			// ResultRate[m] = "0";
			// }
			// tCalAccClassRate = Double.parseDouble(ResultRate[m]);
			// System.out.println(tCalAccClassRate + "第二次的日利率");
			// tmpInterst = tmpInterst * (1 + tCalAccClassRate);
			// // aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
			// }
			//
			// System.out.println(tInsuAccClassBala * (tmpInterst - 1)) ;
			// aAccClassInterest += tInsuAccClassBala * (tmpInterst - 1);
			// System.out.println("理赔或退保时的利息" + aAccClassInterest);

			// aAccClassInterest+=tInsuAccClassBala*getIntvRate(tInterval,tCalAccClassRate,"C");
			break;

		}
		return aAccClassInterest;
	}

	/**
	 * 对按交费计划编码进行分类的账户计算其账户余额
	 *
	 * @param aPayPlanCode
	 *            String 主键之一
	 * @param aInsuAccNo
	 *            String 主键之一
	 * @param aPolNo
	 *            String 主键之一
	 * @param aOtherNo
	 *            String 主键之一
	 * @param aAccAscription
	 *            String 主键之一
	 * @param aBalanceDate
	 *            String
	 * @param aRateType
	 *            String
	 * @param aIntervalType
	 *            String
	 * @return double
	 */
	public double getAccClassBalance(String aPayPlanCode, String aInsuAccNo,
			String aPolNo, String aOtherNo, String aAccAscription,
			String aBalanceDate, String aRateType, String aIntervalType) {

		double aAccBalance = 0.0;
		double tAccRate, tCalAccRate;
		int tInterval = 0;
		// String tSql = "";
		// String iSql = "";
		double tAccInterest = 0.0;

		mLCInsureAccClassSet.clear();
		mLCInsureAccTraceSet.clear();

		// 得到描述表中帐户利率
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		tLMRiskInsuAccDB.setInsuAccNo(aInsuAccNo);
		if (!tLMRiskInsuAccDB.getInfo()) {
			return aAccBalance;
		}

		switch (Integer.parseInt(tLMRiskInsuAccDB.getAccComputeFlag())) {
		case 0:

			// 不计息
			aAccBalance = 0.0;
			break;
		case 1:

			// 按照固定利率单利生息
			tAccRate = tLMRiskInsuAccDB.getAccRate();
			tCalAccRate = TransAccRate(tAccRate, aRateType, "S", aIntervalType);

			// tSql = "select * from lcInsureAcc where PolNo='"+aPolNo+"' and
			// InsuAccNo='"+aInsuAccNo+"'";
			LCInsureAccClassDB tLCInsureAccClassDB = new LCInsureAccClassDB();
			tLCInsureAccClassDB.setPayPlanCode(aPayPlanCode);
			tLCInsureAccClassDB.setPolNo(aPolNo);
			tLCInsureAccClassDB.setInsuAccNo(aInsuAccNo);
			tLCInsureAccClassDB.setOtherNo(aOtherNo);
			tLCInsureAccClassDB.setAccAscription(aAccAscription);

			// tLCInsureAccDB.setOtherNo(aOtherNo);
			mLCInsureAccClassSet = tLCInsureAccClassDB.query();

			// 这里记录肯定只有一条，因为主键仅对应一条记录
			for (int i = 1; i <= mLCInsureAccClassSet.size(); i++) {
				LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
				tLCInsureAccTraceDB.setPolNo(aPolNo);
				tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
				tLCInsureAccTraceDB.setPayPlanCode(aPayPlanCode);
				tLCInsureAccTraceDB.setOtherNo(aOtherNo);
				tLCInsureAccTraceDB.setAccAscription(aAccAscription);
				// 根据该方法提供的5个参数，得到LCInsureAccTrace列表
				// tLCInsureAccTraceDB.setOtherNo(mLCInsureAccSet.get(i).getOtherNo());
				mLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
				for (int j = 1; j <= mLCInsureAccTraceSet.size(); j++) {
					// 剔除掉交费类型为LX的轨迹记录
					if (!mLCInsureAccTraceSet.get(j).getMoneyType()
							.equals("LX")) {
						// 得到时间间隔
						tInterval = PubFun.calInterval(mLCInsureAccTraceSet
								.get(j).getPayDate(), aBalanceDate,
								aIntervalType);
						double tInterest = mLCInsureAccTraceSet.get(j)
								.getMoney()
								+ mLCInsureAccTraceSet.get(j).getMoney()
								* getIntvRate(tInterval, tCalAccRate, "S");
						// mLCInsureAccTraceSet.get(j).setMoney(tInterest);
						tAccInterest += tInterest;
					}
				}
				mLCInsureAccClassSet.get(i).setInsuAccBala(tAccInterest);
				mLCInsureAccClassSet.get(i).setBalaDate(aBalanceDate);
				mLCInsureAccClassSet.get(i)
						.setBalaTime(PubFun.getCurrentTime());
				// 上述3个信息需要在DB中保存
			}
			break;
		case 2:

			// 按照固定利率复利生息
			tAccRate = tLMRiskInsuAccDB.getAccRate();
			tCalAccRate = TransAccRate(tAccRate, aRateType, "C", aIntervalType);

			// tSql = "select * from lcInsureAcc where PolNo='"+aPolNo+"' and
			// InsuAccNo='"+aInsuAccNo+"'";
			LCInsureAccClassDB tLCInsureAccClassDB2 = new LCInsureAccClassDB();
			tLCInsureAccClassDB2.setPolNo(aPolNo);
			tLCInsureAccClassDB2.setInsuAccNo(aInsuAccNo);
			tLCInsureAccClassDB2.setPayPlanCode(aPayPlanCode);
			tLCInsureAccClassDB2.setOtherNo(aOtherNo);
			tLCInsureAccClassDB2.setAccAscription(aAccAscription);

			// tLCInsureAccDB2.setOtherNo(aOtherNo);
			mLCInsureAccClassSet = tLCInsureAccClassDB2.query(); // size为1
			for (int i = 1; i <= mLCInsureAccClassSet.size(); i++) {
				LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
				tLCInsureAccTraceDB.setPolNo(aPolNo);
				tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
				tLCInsureAccTraceDB.setPayPlanCode(aPayPlanCode);
				tLCInsureAccTraceDB.setOtherNo(aOtherNo);
				tLCInsureAccTraceDB.setAccAscription(aAccAscription);
				// tLCInsureAccTraceDB.setOtherNo(mLCInsureAccSet.get(i).getOtherNo());
				mLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
				for (int j = 1; j <= mLCInsureAccTraceSet.size(); j++) {
					if (!mLCInsureAccTraceSet.get(j).getMoneyType()
							.equals("LX")) {
						// 得到时间间隔
						tInterval = PubFun.calInterval(mLCInsureAccTraceSet
								.get(j).getPayDate(), aBalanceDate,
								aIntervalType);
						double tInterest = mLCInsureAccTraceSet.get(j)
								.getMoney()
								+ mLCInsureAccTraceSet.get(j).getMoney()
								* getIntvRate(tInterval, tCalAccRate, "C");
						mLCInsureAccTraceSet.get(j).setMoney(tInterest);
						tAccInterest += tInterest;
					}
				}
				mLCInsureAccClassSet.get(i).setInsuAccBala(tAccInterest);
				mLCInsureAccClassSet.get(i).setBalaDate(aBalanceDate);
				mLCInsureAccClassSet.get(i)
						.setBalaTime(PubFun.getCurrentTime());
			}

			break;
		case 3:

			// 按照利率表单利生息

			// tAccRate = this.getAccRate(tLMRiskInsuAccDB.getSchema());
			// tCalAccRate = TransAccRate(tAccRate,aRateType,"S",aIntervalType);
			// tSql = "select * from lcInsureAcc where PolNo='"+aPolNo+"' and
			// InsuAccNo='"+aInsuAccNo+"'";
			LCInsureAccClassDB tLCInsureAccClassDB3 = new LCInsureAccClassDB();
			tLCInsureAccClassDB3.setPolNo(aPolNo);
			tLCInsureAccClassDB3.setInsuAccNo(aInsuAccNo);
			tLCInsureAccClassDB3.setPayPlanCode(aPayPlanCode);
			tLCInsureAccClassDB3.setOtherNo(aOtherNo);
			tLCInsureAccClassDB3.setAccAscription(aAccAscription);

			// 说明：以上5个作为AccClass的主键

			// tLCInsureAccDB3.setOtherNo(aOtherNo);
			mLCInsureAccClassSet = tLCInsureAccClassDB3.query();
			for (int i = 1; i <= mLCInsureAccClassSet.size(); i++) {
				LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
				tLCInsureAccTraceDB.setPolNo(aPolNo);
				tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
				tLCInsureAccTraceDB.setPayPlanCode(aPayPlanCode);
				tLCInsureAccTraceDB.setOtherNo(aOtherNo);
				tLCInsureAccTraceDB.setAccAscription(aAccAscription);

				// tLCInsureAccTraceDB.setOtherNo(mLCInsureAccSet.get(i).getOtherNo());
				mLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
				for (int j = 1; j <= mLCInsureAccTraceSet.size(); j++) {
					if (!mLCInsureAccTraceSet.get(j).getMoneyType()
							.equals("LX")) {
						double tInterest = 0;
						// 得到分段的单利计算
						String[] ResultRate = getMultiAccRate(aInsuAccNo,
								tLMRiskInsuAccDB.getSchema(),
								mLCInsureAccTraceSet.get(j).getPayDate(),
								aBalanceDate, aRateType, "S", aIntervalType);
						for (int m = 0; m < ResultRate.length; m++) {
							// 杨红添加此段代码，添加开始
							if (ResultRate[m] == null) {
								ResultRate[m] = "0";
							}
							// 添加结束
							tCalAccRate = Double.parseDouble(ResultRate[m]);
							double tSubInterest = mLCInsureAccTraceSet.get(j)
									.getMoney()
									* tCalAccRate;
							tInterest += tSubInterest;

						}
						double tBalance = mLCInsureAccTraceSet.get(j)
								.getMoney()
								+ tInterest;
						mLCInsureAccTraceSet.get(j).setMoney(tBalance);
						tAccInterest += tBalance;
					}
				}
				mLCInsureAccClassSet.get(i).setInsuAccBala(tAccInterest);
				mLCInsureAccClassSet.get(i).setBalaDate(aBalanceDate);
				mLCInsureAccClassSet.get(i)
						.setBalaTime(PubFun.getCurrentTime());
			}

			break;
		case 4:

			// 按照利率表复利生息

			// 得到描述表中的利率
			tAccRate = getAccRate(tLMRiskInsuAccDB, aBalanceDate);
			tCalAccRate = TransAccRate(tAccRate, aRateType, "C", aIntervalType);

			// tSql = "select * from lcInsureAcc where PolNo='"+aPolNo+"' and
			// InsuAccNo='"+aInsuAccNo+"'";
			LCInsureAccClassDB tLCInsureAccClassDB4 = new LCInsureAccClassDB();
			tLCInsureAccClassDB4.setPolNo(aPolNo);
			tLCInsureAccClassDB4.setInsuAccNo(aInsuAccNo);
			tLCInsureAccClassDB4.setPayPlanCode(aPayPlanCode);

			// tLCInsureAccClassDB4.setInsuAccNo(aInsuAccNo);
			tLCInsureAccClassDB4.setOtherNo(aOtherNo);

			// tLCInsureAccTraceDB4.setPayPlanCode(aPayPlanCode);
			tLCInsureAccClassDB4.setAccAscription(aAccAscription);

			// tLCInsureAccDB4.setOtherNo(aOtherNo);
			mLCInsureAccClassSet = tLCInsureAccClassDB4.query();
			for (int i = 1; i <= mLCInsureAccClassSet.size(); i++) {
				LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
				tLCInsureAccTraceDB.setPolNo(aPolNo);
				tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
				tLCInsureAccTraceDB.setPayPlanCode(aPayPlanCode);
				tLCInsureAccTraceDB.setOtherNo(aOtherNo);
				tLCInsureAccTraceDB.setAccAscription(aAccAscription);

				// tLCInsureAccTraceDB.setOtherNo(mLCInsureAccSet.get(i).getOtherNo());
				mLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
				for (int j = 1; j <= mLCInsureAccTraceSet.size(); j++) {
					if (!mLCInsureAccTraceSet.get(j).getMoneyType()
							.equals("LX")) {
						double tInterest = 0;
						// 得到分段的单利计算
						if (mLCInsureAccTraceSet.get(j).getPayDate() == null) {
							mLCInsureAccTraceSet.get(j).setPayDate(
									mLCInsureAccTraceSet.get(j).getMakeDate());
						}
						String[] ResultRate = getMultiAccRate(aInsuAccNo,
								tLMRiskInsuAccDB.getSchema(),
								mLCInsureAccTraceSet.get(j).getPayDate(),
								aBalanceDate, aRateType, "C", aIntervalType);
						for (int m = 0; m < ResultRate.length; m++) {
							// System.out.println("yhTest begin");
							String test_yh = ResultRate[m];
							// System.out.println(test_yh);
							if (ResultRate[m] == null) {
								ResultRate[m] = "0";
							}
							tCalAccRate = Double.parseDouble(ResultRate[m]);
							// System.out.println("Yhtest end!");
							double tSubInterest = mLCInsureAccTraceSet.get(j)
									.getMoney()
									* tCalAccRate;
							tInterest += tSubInterest;
						}
						double tBalance = mLCInsureAccTraceSet.get(j)
								.getMoney()
								+ tInterest;
						mLCInsureAccTraceSet.get(j).setMoney(tBalance);
						tAccInterest += tBalance;
					}
				}
				mLCInsureAccClassSet.get(i).setInsuAccBala(tAccInterest);
				mLCInsureAccClassSet.get(i).setBalaDate(aBalanceDate);
				mLCInsureAccClassSet.get(i)
						.setBalaTime(PubFun.getCurrentTime());
			}
			break;
		default:
			aAccBalance = 0.0;
			break;
		}
		aAccBalance = tAccInterest;
		return aAccBalance;

	}

	/**
	 * 计算一个保单下的所有账户结息后的账户余额
	 *
	 * @param aPolNo
	 *            String
	 * @param aBalanceDate
	 *            String
	 * @param aRateType
	 *            String
	 * @param aIntervalType
	 *            String
	 * @return double
	 */
	public double getAccBalance3(String aPolNo, String aBalanceDate,
			String aRateType, String aIntervalType) {
		double aAccBalance = 0.0;
		mLCInsureAccSet.clear();
		mLCInsureAccClassSet.clear();
		LCInsureAccDB tLCInsureAccDB = new LCInsureAccDB();
		tLCInsureAccDB.setPolNo(aPolNo);
		mLCInsureAccSet = tLCInsureAccDB.query();
		for (int i = 1; i <= mLCInsureAccSet.size(); i++) {
			String aInsuAccNo = mLCInsureAccSet.get(i).getInsuAccNo();
			aAccBalance += getAccBalance2(aInsuAccNo, aPolNo, aBalanceDate,
					aRateType, aIntervalType);
		}

		return aAccBalance;
	}

	/**
	 * 计算出一保单下某险种账户的现金余额
	 *
	 * @param aInsuAccNo
	 *            String
	 * @param aPolNo
	 *            String
	 * @param aBalanceDate
	 *            String
	 * @param aRateType
	 *            String
	 * @param aIntervalType
	 *            String
	 * @return double
	 */
	public double getAccBalance2(String aInsuAccNo, String aPolNo,
			String aBalanceDate, String aRateType, String aIntervalType) {
		double aAccBalance = 0.0;
		mLCInsureAccSet.clear();
		mLCInsureAccClassSet.clear();
		LCInsureAccDB tLCInsureAccDB = new LCInsureAccDB();
		tLCInsureAccDB.setPolNo(aPolNo);
		tLCInsureAccDB.setInsuAccNo(aInsuAccNo);
		mLCInsureAccSet = tLCInsureAccDB.query();
		for (int i = 1; i <= mLCInsureAccSet.size(); i++) {
			// String aPayPlanCode=mLCInsureAccSet.get(i).getPay
			// String tInsuAccNo=mLCInsureAccSet.get(i).getInsuAccNo();
			// String tPolNo=mLCInsureAccSet.get(i).getPolNo();
			LCInsureAccClassDB tLCInsureAccClassDB = new LCInsureAccClassDB();
			tLCInsureAccClassDB.setPolNo(aPolNo);
			tLCInsureAccClassDB.setInsuAccNo(aInsuAccNo);
			LCInsureAccClassSet mLCInsureAccClassSet2 = new LCInsureAccClassSet();
			mLCInsureAccClassSet2 = tLCInsureAccClassDB.query();
			for (int j = 1; j <= mLCInsureAccClassSet2.size(); j++) {
				String aPayPlanCode = mLCInsureAccClassSet2.get(j)
						.getPayPlanCode();
				String aOtherNo = mLCInsureAccClassSet2.get(j).getOtherNo();
				String aAccAscription = mLCInsureAccClassSet2.get(j)
						.getAccAscription();
				aAccBalance += getAccClassBalance(aPayPlanCode, aInsuAccNo,
						aPolNo, aOtherNo, aAccAscription, aBalanceDate,
						aRateType, aIntervalType);

			}
			mLCInsureAccSet.get(i).setInsuAccBala(aAccBalance);
			mLCInsureAccSet.get(i).setBalaDate(aBalanceDate);
			mLCInsureAccSet.get(i).setBalaTime(PubFun.getCurrentTime());

		}

		return aAccBalance;
	}

	/**
	 * 按照保单得到帐户子帐户的余额
	 *
	 * @param aOtherNo
	 *            String
	 * @param aInsuAccNo
	 *            String
	 * @param aPolNo
	 *            String
	 * @param aBalanceDate
	 *            String 结息日期
	 * @param aRateType
	 *            String 取得描述帐户利率的时间单位
	 * @param aIntervalType
	 *            String 结息的时间单位
	 * @return double
	 */
	public double getAccBalance(String aOtherNo, String aInsuAccNo,
			String aPolNo, String aBalanceDate, String aRateType,
			String aIntervalType) {
		double aAccBalance = 0.0;
		double tAccRate, tCalAccRate;
		int tInterval = 0;
		String tSql = "";
		String iSql = "";
		double tAccInterest = 0.0;

		mLCInsureAccSet.clear();
		mLCInsureAccTraceSet.clear();

		// 得到描述表中帐户利率
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		tLMRiskInsuAccDB.setInsuAccNo(aInsuAccNo);
		if (!tLMRiskInsuAccDB.getInfo()) {
			return aAccBalance;
		}

		switch (Integer.parseInt(tLMRiskInsuAccDB.getAccComputeFlag())) {
		// 不计息
		case 0:
			aAccBalance = 0.0;
			break;
		// 按照固定利率单利生息
		case 1:
			tAccRate = tLMRiskInsuAccDB.getAccRate();
			tCalAccRate = TransAccRate(tAccRate, aRateType, "S", aIntervalType);

			// tSql = "select * from lcInsureAcc where PolNo='"+aPolNo+"' and
			// InsuAccNo='"+aInsuAccNo+"'";
			LCInsureAccDB tLCInsureAccDB = new LCInsureAccDB();
			tLCInsureAccDB.setPolNo(aPolNo);
			tLCInsureAccDB.setInsuAccNo(aInsuAccNo);

			// tLCInsureAccDB.setOtherNo(aOtherNo);
			mLCInsureAccSet = tLCInsureAccDB.query();
			for (int i = 1; i <= mLCInsureAccSet.size(); i++) {
				LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
				tLCInsureAccTraceDB.setPolNo(aPolNo);
				tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
				// tLCInsureAccTraceDB.setOtherNo(mLCInsureAccSet.get(i).getOtherNo());
				mLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
				for (int j = 1; j <= mLCInsureAccTraceSet.size(); j++) {
					// 得到时间间隔
					tInterval = PubFun.calInterval(mLCInsureAccSet.get(i)
							.getBalaDate(), aBalanceDate, aIntervalType);
					double tInterest = mLCInsureAccTraceSet.get(j).getMoney()
							+ mLCInsureAccTraceSet.get(j).getMoney()
							* getIntvRate(tInterval, tCalAccRate, "S");
					mLCInsureAccTraceSet.get(j).setMoney(tInterest);
					tAccInterest += tInterest;
				}
				mLCInsureAccSet.get(i).setInsuAccBala(tAccInterest);
				mLCInsureAccSet.get(i).setBalaDate(aBalanceDate);
				mLCInsureAccSet.get(i).setBalaTime(PubFun.getCurrentTime());
			}
			break;
		// 按照固定利率复利生息
		case 2:
			tAccRate = tLMRiskInsuAccDB.getAccRate();
			tCalAccRate = TransAccRate(tAccRate, aRateType, "C", aIntervalType);

			// tSql = "select * from lcInsureAcc where PolNo='"+aPolNo+"' and
			// InsuAccNo='"+aInsuAccNo+"'";
			LCInsureAccDB tLCInsureAccDB2 = new LCInsureAccDB();
			tLCInsureAccDB2.setPolNo(aPolNo);
			tLCInsureAccDB2.setInsuAccNo(aInsuAccNo);

			// tLCInsureAccDB2.setOtherNo(aOtherNo);
			mLCInsureAccSet = tLCInsureAccDB2.query();
			for (int i = 1; i <= mLCInsureAccSet.size(); i++) {
				LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
				tLCInsureAccTraceDB.setPolNo(aPolNo);
				tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
				// tLCInsureAccTraceDB.setOtherNo(mLCInsureAccSet.get(i).getOtherNo());
				mLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
				for (int j = 1; j <= mLCInsureAccTraceSet.size(); j++) {
					// 得到时间间隔
					tInterval = PubFun.calInterval(mLCInsureAccSet.get(i)
							.getBalaDate(), aBalanceDate, aIntervalType);
					double tInterest = mLCInsureAccTraceSet.get(j).getMoney()
							+ mLCInsureAccTraceSet.get(j).getMoney()
							* getIntvRate(tInterval, tCalAccRate, "C");
					mLCInsureAccTraceSet.get(j).setMoney(tInterest);
					tAccInterest += tInterest;
				}
				mLCInsureAccSet.get(i).setInsuAccBala(tAccInterest);
				mLCInsureAccSet.get(i).setBalaDate(aBalanceDate);
				mLCInsureAccSet.get(i).setBalaTime(PubFun.getCurrentTime());
			}

			break;
		// 按照利率表单利生息
		case 3:

			// tAccRate = this.getAccRate(tLMRiskInsuAccDB.getSchema());
			// tCalAccRate = TransAccRate(tAccRate,aRateType,"S",aIntervalType);
			// tSql = "select * from lcInsureAcc where PolNo='"+aPolNo+"' and
			// InsuAccNo='"+aInsuAccNo+"'";
			LCInsureAccDB tLCInsureAccDB3 = new LCInsureAccDB();
			tLCInsureAccDB3.setPolNo(aPolNo);
			tLCInsureAccDB3.setInsuAccNo(aInsuAccNo);

			// tLCInsureAccDB3.setOtherNo(aOtherNo);
			mLCInsureAccSet = tLCInsureAccDB3.query();
			for (int i = 1; i <= mLCInsureAccSet.size(); i++) {
				LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
				tLCInsureAccTraceDB.setPolNo(aPolNo);
				tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
				// tLCInsureAccTraceDB.setOtherNo(mLCInsureAccSet.get(i).getOtherNo());
				mLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
				for (int j = 1; j <= mLCInsureAccTraceSet.size(); j++) {
					double tInterest = 0;
					// 得到分段的单利计算
					String[] ResultRate = getMultiAccRate(aInsuAccNo,
							tLMRiskInsuAccDB.getSchema(), mLCInsureAccSet
									.get(i).getBalaDate(), aBalanceDate,
							aRateType, "S", aIntervalType);
					for (int m = 0; m < ResultRate.length; m++) {

						tCalAccRate = Double.parseDouble(ResultRate[m]);
						double tSubInterest = mLCInsureAccTraceSet.get(j)
								.getMoney()
								* tCalAccRate;
						tInterest += tSubInterest;

					}
					double tBalance = mLCInsureAccTraceSet.get(j).getMoney()
							+ tInterest;
					mLCInsureAccTraceSet.get(j).setMoney(tBalance);
					tAccInterest += tBalance;
				}
				mLCInsureAccSet.get(i).setInsuAccBala(tAccInterest);
				mLCInsureAccSet.get(i).setBalaDate(aBalanceDate);
				mLCInsureAccSet.get(i).setBalaTime(PubFun.getCurrentTime());
			}

			break;
		// 按照利率表复利生息
		case 4:
			tAccRate = AccountManage.getAccRate(tLMRiskInsuAccDB.getSchema());
			tCalAccRate = TransAccRate(tAccRate, aRateType, "C", aIntervalType);

			// tSql = "select * from lcInsureAcc where PolNo='"+aPolNo+"' and
			// InsuAccNo='"+aInsuAccNo+"'";
			LCInsureAccDB tLCInsureAccDB4 = new LCInsureAccDB();
			tLCInsureAccDB4.setPolNo(aPolNo);
			tLCInsureAccDB4.setInsuAccNo(aInsuAccNo);

			// tLCInsureAccDB4.setOtherNo(aOtherNo);
			mLCInsureAccSet = tLCInsureAccDB4.query();
			for (int i = 1; i <= mLCInsureAccSet.size(); i++) {
				LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
				tLCInsureAccTraceDB.setPolNo(aPolNo);
				tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
				// tLCInsureAccTraceDB.setOtherNo(mLCInsureAccSet.get(i).getOtherNo());
				mLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
				for (int j = 1; j <= mLCInsureAccTraceSet.size(); j++) {
					double tInterest = 0;
					// 得到分段的单利计算
					String[] ResultRate = getMultiAccRate(aInsuAccNo,
							tLMRiskInsuAccDB.getSchema(), mLCInsureAccSet
									.get(i).getBalaDate(), aBalanceDate,
							aRateType, "C", aIntervalType);
					for (int m = 0; m < ResultRate.length; m++) {
						tCalAccRate = Double.parseDouble(ResultRate[m]);
						double tSubInterest = mLCInsureAccTraceSet.get(j)
								.getMoney()
								* tCalAccRate;
						tInterest += tSubInterest;
					}
					double tBalance = mLCInsureAccTraceSet.get(j).getMoney()
							+ tInterest;
					mLCInsureAccTraceSet.get(j).setMoney(tBalance);
					tAccInterest += tBalance;
				}
				mLCInsureAccSet.get(i).setInsuAccBala(tAccInterest);
				mLCInsureAccSet.get(i).setBalaDate(aBalanceDate);
				mLCInsureAccSet.get(i).setBalaTime(PubFun.getCurrentTime());
			}
			break;
		default:
			aAccBalance = 0.0;
			break;
		}
		aAccBalance = tAccInterest;
		return aAccBalance;
	}

	/**
	 * 按照保单得到帐户子帐户的余额
	 *
	 * @param aLCInsureAccSchema
	 *            LCInsureAccSchema
	 * @param aBalanceDate
	 *            String 结息日期
	 * @param aRateType
	 *            String 取得描述帐户利率的时间单位。
	 * @param aIntervalType
	 *            String 结息的时间单位。
	 * @return double
	 */
	public double getAccBalance(LCInsureAccSchema aLCInsureAccSchema,
			String aBalanceDate, String aRateType, String aIntervalType) {
		double aAccBalance = 0.0;
		double tAccRate, tCalAccRate;
		int tInterval = 0;
		String tSql = "";
		String iSql = "";
		double tAccInterest = 0.0;

		mLCInsureAccSet.clear();
		mLCInsureAccTraceSet.clear();

		// 得到描述表中帐户利率
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		tLMRiskInsuAccDB.setInsuAccNo(aLCInsureAccSchema.getInsuAccNo());
		if (!tLMRiskInsuAccDB.getInfo()) {
			return aAccBalance;
		}

		switch (Integer.parseInt(tLMRiskInsuAccDB.getAccComputeFlag())) {
		// 不计息
		case 0:
			aAccBalance = 0.0;
			break;
		// 按照固定利率单利生息
		case 1:
			tAccRate = tLMRiskInsuAccDB.getAccRate();
			tCalAccRate = TransAccRate(tAccRate, aRateType, "S", aIntervalType);

			LCInsureAccDB tLCInsureAccDB = new LCInsureAccDB();
			tLCInsureAccDB.setPolNo(aLCInsureAccSchema.getPolNo());
			tLCInsureAccDB.setInsuAccNo(aLCInsureAccSchema.getInsuAccNo());

			// tLCInsureAccDB.setOtherNo(aLCInsureAccSchema.getOtherNo());
			mLCInsureAccSet = tLCInsureAccDB.query();
			for (int i = 1; i <= mLCInsureAccSet.size(); i++) {
				LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
				tLCInsureAccTraceDB.setPolNo(aLCInsureAccSchema.getPolNo());
				tLCInsureAccTraceDB.setInsuAccNo(aLCInsureAccSchema
						.getInsuAccNo());
				// tLCInsureAccTraceDB.setOtherNo(mLCInsureAccSet.get(i).getOtherNo());
				mLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
				for (int j = 1; j <= mLCInsureAccTraceSet.size(); j++) {
					// 得到时间间隔
					tInterval = PubFun.calInterval(mLCInsureAccSet.get(i)
							.getBalaDate(), aLCInsureAccSchema.getBalaDate(),
							aIntervalType);
					double tInterest = mLCInsureAccTraceSet.get(j).getMoney()
							+ mLCInsureAccTraceSet.get(j).getMoney()
							* getIntvRate(tInterval, tCalAccRate, "S");
					mLCInsureAccTraceSet.get(j).setMoney(tInterest);
					tAccInterest += tInterest;
				}
				mLCInsureAccSet.get(i).setInsuAccBala(tAccInterest);
				mLCInsureAccSet.get(i).setBalaDate(aBalanceDate);
				mLCInsureAccSet.get(i).setBalaTime(PubFun.getCurrentTime());
			}
			break;
		// 按照固定利率复利生息
		case 2:
			tAccRate = tLMRiskInsuAccDB.getAccRate();
			tCalAccRate = TransAccRate(tAccRate, aRateType, "C", aIntervalType);

			// tSql = "select * from lcInsureAcc where PolNo='"+aPolNo+"' and
			// InsuAccNo='"+aInsuAccNo+"'";
			LCInsureAccDB tLCInsureAccDB2 = new LCInsureAccDB();
			tLCInsureAccDB2.setPolNo(aLCInsureAccSchema.getPolNo());
			tLCInsureAccDB2.setInsuAccNo(aLCInsureAccSchema.getInsuAccNo());

			// tLCInsureAccDB2.setOtherNo(aLCInsureAccSchema.getOtherNo());
			mLCInsureAccSet = tLCInsureAccDB2.query();
			for (int i = 1; i <= mLCInsureAccSet.size(); i++) {
				LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
				tLCInsureAccTraceDB.setPolNo(aLCInsureAccSchema.getPolNo());
				tLCInsureAccTraceDB.setInsuAccNo(aLCInsureAccSchema
						.getInsuAccNo());
				// tLCInsureAccTraceDB.setOtherNo(mLCInsureAccSet.get(i).getOtherNo());
				mLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
				for (int j = 1; j <= mLCInsureAccTraceSet.size(); j++) {
					// 得到时间间隔
					tInterval = PubFun.calInterval(mLCInsureAccSet.get(i)
							.getBalaDate(), aBalanceDate, aIntervalType);
					double tInterest = mLCInsureAccTraceSet.get(j).getMoney()
							+ mLCInsureAccTraceSet.get(j).getMoney()
							* getIntvRate(tInterval, tCalAccRate, "C");
					mLCInsureAccTraceSet.get(j).setMoney(tInterest);
					tAccInterest += tInterest;
				}
				mLCInsureAccSet.get(i).setInsuAccBala(tAccInterest);
				mLCInsureAccSet.get(i).setBalaDate(aBalanceDate);
				mLCInsureAccSet.get(i).setBalaTime(PubFun.getCurrentTime());
			}

			break;
		// 按照利率表单利生息
		case 3:
			tAccRate = AccountManage.getAccRate(tLMRiskInsuAccDB.getSchema());
			tCalAccRate = TransAccRate(tAccRate, aRateType, "S", aIntervalType);

			// tSql = "select * from lcInsureAcc where PolNo='"+aPolNo+"' and
			// InsuAccNo='"+aInsuAccNo+"'";
			LCInsureAccDB tLCInsureAccDB3 = new LCInsureAccDB();
			tLCInsureAccDB3.setPolNo(aLCInsureAccSchema.getPolNo());
			tLCInsureAccDB3.setInsuAccNo(aLCInsureAccSchema.getInsuAccNo());

			// tLCInsureAccDB3.setOtherNo(aLCInsureAccSchema.getOtherNo());
			mLCInsureAccSet = tLCInsureAccDB3.query();
			for (int i = 1; i <= mLCInsureAccSet.size(); i++) {
				LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
				tLCInsureAccTraceDB.setPolNo(aLCInsureAccSchema.getPolNo());
				tLCInsureAccTraceDB.setInsuAccNo(aLCInsureAccSchema
						.getInsuAccNo());
				// tLCInsureAccTraceDB.setOtherNo(mLCInsureAccSet.get(i).getOtherNo());
				mLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
				for (int j = 1; j <= mLCInsureAccTraceSet.size(); j++) {
					double tInterest = 0;
					// 得到分段的单利计算
					String[] ResultRate = getMultiAccRate(aLCInsureAccSchema
							.getInsuAccNo(), tLMRiskInsuAccDB.getSchema(),
							mLCInsureAccSet.get(i).getBalaDate(), aBalanceDate,
							aRateType, "S", aIntervalType);
					for (int m = 0; m < ResultRate.length; m++) {
						tCalAccRate = Double.parseDouble(ResultRate[m]);
						double tSubInterest = mLCInsureAccTraceSet.get(j)
								.getMoney()
								* tCalAccRate;
						tInterest += tSubInterest;
					}
					double tBalance = mLCInsureAccTraceSet.get(j).getMoney()
							+ tInterest;
					mLCInsureAccTraceSet.get(j).setMoney(tBalance);
					tAccInterest += tBalance;
				}
				mLCInsureAccSet.get(i).setInsuAccBala(tAccInterest);
				mLCInsureAccSet.get(i).setBalaDate(aBalanceDate);
				mLCInsureAccSet.get(i).setBalaTime(PubFun.getCurrentTime());
			}
			break;
		// 按照利率表复利生息
		case 4:
			tAccRate = AccountManage.getAccRate(tLMRiskInsuAccDB.getSchema());
			tCalAccRate = TransAccRate(tAccRate, aRateType, "C", aIntervalType);

			// tSql = "select * from lcInsureAcc where PolNo='"+aPolNo+"' and
			// InsuAccNo='"+aInsuAccNo+"'";
			LCInsureAccDB tLCInsureAccDB4 = new LCInsureAccDB();
			tLCInsureAccDB4.setPolNo(aLCInsureAccSchema.getPolNo());
			tLCInsureAccDB4.setInsuAccNo(aLCInsureAccSchema.getInsuAccNo());

			// tLCInsureAccDB4.setOtherNo(aLCInsureAccSchema.getOtherNo());
			mLCInsureAccSet = tLCInsureAccDB4.query();
			for (int i = 1; i <= mLCInsureAccSet.size(); i++) {
				LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
				tLCInsureAccTraceDB.setPolNo(aLCInsureAccSchema.getPolNo());
				tLCInsureAccTraceDB.setInsuAccNo(aLCInsureAccSchema
						.getInsuAccNo());
				// tLCInsureAccTraceDB.setOtherNo(mLCInsureAccSet.get(i).getOtherNo());
				mLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
				for (int j = 1; j <= mLCInsureAccTraceSet.size(); j++) {
					double tInterest = 0;
					// 得到分段的单利计算
					String[] ResultRate = getMultiAccRate(aLCInsureAccSchema
							.getInsuAccNo(), tLMRiskInsuAccDB.getSchema(),
							mLCInsureAccSet.get(i).getBalaDate(), aBalanceDate,
							aRateType, "C", aIntervalType);
					for (int m = 0; m < ResultRate.length; m++) {
						if (!(ResultRate[m] == null || ResultRate[m].equals(""))) {
							tCalAccRate = Double.parseDouble(ResultRate[m]);
							System.out.println("当m的值是" + m + "时，值是"
									+ tCalAccRate);
							double tSubInterest = mLCInsureAccTraceSet.get(j)
									.getMoney()
									* tCalAccRate;
							System.out.println("当m的值是" + m + "时，利息是"
									+ tSubInterest);
							tInterest += tSubInterest;
						}
					}
					double tBalance = mLCInsureAccTraceSet.get(j).getMoney()
							+ tInterest;
					mLCInsureAccTraceSet.get(j).setMoney(tBalance);
					tAccInterest += tBalance;
				}
				mLCInsureAccSet.get(i).setInsuAccBala(tAccInterest);
				mLCInsureAccSet.get(i).setBalaDate(aBalanceDate);
				mLCInsureAccSet.get(i).setBalaTime(PubFun.getCurrentTime());
			}
			break;
		default:
			aAccBalance = 0.0;
			break;
		}
		aAccBalance = tAccInterest;
		return aAccBalance;
	}

	/**
	 * 累计生息界面使用 按照保单得到某个帐户的余额
	 *
	 * @param aInsuAccNo
	 *            String
	 * @param aPolNo
	 *            String
	 * @param aBalanceDate
	 *            String 结息日期
	 * @param aRateType
	 *            String 取得描述帐户利率的时间单位。
	 * @param aIntervalType
	 *            String 结息的时间单位。
	 * @return double
	 */
	public double getAccBalance(String aInsuAccNo, String aPolNo,
			String aBalanceDate, String aRateType, String aIntervalType) {
		double aAccBalance = 0.0;
		double tAccRate, tCalAccRate;
		int tInterval = 0;
		// String tSql = "";
		// String iSql = "";
		double tAccInterest = 0.0;
		mLCInsureAccSet.clear();
		mLCInsureAccTraceSet.clear();

		// 得到描述表中帐户利率
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		tLMRiskInsuAccDB.setInsuAccNo(aInsuAccNo);
		if (!tLMRiskInsuAccDB.getInfo()) {
			return aAccBalance;
		}

		switch (Integer.parseInt(tLMRiskInsuAccDB.getAccComputeFlag())) {
		case 0:

			// 不计息
			aAccBalance = 0.0;
			break;
		case 1:

			// 按照固定利率单利生息
			tAccRate = tLMRiskInsuAccDB.getAccRate();
			tCalAccRate = TransAccRate(tAccRate, aRateType, "S", aIntervalType);

			// tSql = "select * from lcInsureAcc where PolNo='"+aPolNo+"' and
			// InsuAccNo='"+aInsuAccNo+"'";
			LCInsureAccDB tLCInsureAccDB = new LCInsureAccDB();
			tLCInsureAccDB.setPolNo(aPolNo);
			tLCInsureAccDB.setInsuAccNo(aInsuAccNo);
			mLCInsureAccSet = tLCInsureAccDB.query();
			for (int i = 1; i <= mLCInsureAccSet.size(); i++) {
				LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
				tLCInsureAccTraceDB.setPolNo(aPolNo);
				tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
				// tLCInsureAccTraceDB.setOtherNo(mLCInsureAccSet.get(i).getOtherNo());
				mLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
				for (int j = 1; j <= mLCInsureAccTraceSet.size(); j++) {
					// 得到时间间隔
					tInterval = PubFun.calInterval(mLCInsureAccSet.get(i)
							.getBalaDate(), aBalanceDate, aIntervalType);
					double tInterest = mLCInsureAccTraceSet.get(j).getMoney()
							+ mLCInsureAccTraceSet.get(j).getMoney()
							* getIntvRate(tInterval, tCalAccRate, "S");
					mLCInsureAccTraceSet.get(j).setMoney(tInterest);
					tAccInterest += tInterest;
				}
				mLCInsureAccSet.get(i).setInsuAccBala(tAccInterest);
				mLCInsureAccSet.get(i).setBalaDate(aBalanceDate);
				mLCInsureAccSet.get(i).setBalaTime(PubFun.getCurrentTime());
			}
			break;
		case 2:

			// 按照固定利率复利生息
			tAccRate = tLMRiskInsuAccDB.getAccRate();
			tCalAccRate = TransAccRate(tAccRate, aRateType, "C", aIntervalType);

			// tSql = "select * from lcInsureAcc where PolNo='"+aPolNo+"' and
			// InsuAccNo='"+aInsuAccNo+"'";
			LCInsureAccDB tLCInsureAccDB2 = new LCInsureAccDB();
			tLCInsureAccDB2.setPolNo(aPolNo);
			tLCInsureAccDB2.setInsuAccNo(aInsuAccNo);
			mLCInsureAccSet = tLCInsureAccDB2.query();
			for (int i = 1; i <= mLCInsureAccSet.size(); i++) {
				LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
				tLCInsureAccTraceDB.setPolNo(aPolNo);
				tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
				// tLCInsureAccTraceDB.setOtherNo(mLCInsureAccSet.get(i).getOtherNo());
				mLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
				for (int j = 1; j <= mLCInsureAccTraceSet.size(); j++) {
					// 得到时间间隔
					tInterval = PubFun.calInterval(mLCInsureAccSet.get(i)
							.getBalaDate(), aBalanceDate, aIntervalType);
					double tInterest = mLCInsureAccTraceSet.get(j).getMoney()
							+ mLCInsureAccTraceSet.get(j).getMoney()
							* getIntvRate(tInterval, tCalAccRate, "C");
					mLCInsureAccTraceSet.get(j).setMoney(tInterest);
					tAccInterest += tInterest;
				}
				mLCInsureAccSet.get(i).setInsuAccBala(tAccInterest);
				mLCInsureAccSet.get(i).setBalaDate(aBalanceDate);
				mLCInsureAccSet.get(i).setBalaTime(PubFun.getCurrentTime());
			}

			break;
		case 3:

			// 按照利率表单利生息
			tAccRate = AccountManage.getAccRate(tLMRiskInsuAccDB.getSchema());
			tCalAccRate = TransAccRate(tAccRate, aRateType, "S", aIntervalType);

			// tSql = "select * from lcInsureAcc where PolNo='"+aPolNo+"' and
			// InsuAccNo='"+aInsuAccNo+"'";
			LCInsureAccDB tLCInsureAccDB3 = new LCInsureAccDB();
			tLCInsureAccDB3.setPolNo(aPolNo);
			tLCInsureAccDB3.setInsuAccNo(aInsuAccNo);
			mLCInsureAccSet = tLCInsureAccDB3.query();
			for (int i = 1; i <= mLCInsureAccSet.size(); i++) {
				LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
				tLCInsureAccTraceDB.setPolNo(aPolNo);
				tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
				// tLCInsureAccTraceDB.setOtherNo(mLCInsureAccSet.get(i).getOtherNo());
				mLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
				for (int j = 1; j <= mLCInsureAccTraceSet.size(); j++) {
					double tInterest = 0;
					// 得到分段的单利计算
					String[] ResultRate = getMultiAccRate(aInsuAccNo,
							tLMRiskInsuAccDB.getSchema(), mLCInsureAccSet
									.get(i).getBalaDate(), aBalanceDate,
							aRateType, "S", aIntervalType);
					for (int m = 0; m < ResultRate.length; m++) {
						tCalAccRate = Double.parseDouble(ResultRate[m]);
						double tSubInterest = mLCInsureAccTraceSet.get(j)
								.getMoney()
								* tCalAccRate;
						tInterest += tSubInterest;
					}
					double tBalance = mLCInsureAccTraceSet.get(j).getMoney()
							+ tInterest;
					mLCInsureAccTraceSet.get(j).setMoney(tBalance);
					tAccInterest += tBalance;
				}
				mLCInsureAccSet.get(i).setInsuAccBala(tAccInterest);
				mLCInsureAccSet.get(i).setBalaDate(aBalanceDate);
				mLCInsureAccSet.get(i).setBalaTime(PubFun.getCurrentTime());
			}

			break;
		case 4:

			// 按照利率表复利生息

			// 2005-08-20 朱向峰注释下面的程序，启用新的方法计算利息
			// tAccRate = AccountManage.getAccRate(tLMRiskInsuAccDB,
			// aBalanceDate);
			// tCalAccRate = TransAccRate(tAccRate, aRateType, "C",
			// aIntervalType);
			//
			// LCInsureAccDB tLCInsureAccDB4 = new LCInsureAccDB();
			// tLCInsureAccDB4.setPolNo(aPolNo);
			// tLCInsureAccDB4.setInsuAccNo(aInsuAccNo);
			// mLCInsureAccSet = tLCInsureAccDB4.query();
			// for (int i = 1; i <= mLCInsureAccSet.size(); i++)
			// {
			// LCInsureAccTraceDB tLCInsureAccTraceDB = new
			// LCInsureAccTraceDB();
			// tLCInsureAccTraceDB.setPolNo(aPolNo);
			// tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			// mLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			// for (int j = 1; j <= mLCInsureAccTraceSet.size(); j++)
			// {
			// double tInterest = 0;
			// //得到分段的单利计算
			// String[] ResultRate = getMultiAccRate(aInsuAccNo
			// , tLMRiskInsuAccDB.getSchema(),
			// mLCInsureAccSet.get(i).getBalaDate()
			// , aBalanceDate, aRateType, "C", aIntervalType); //modify by sxy
			// 2004-05-19
			// for (int m = 0; m < ResultRate.length; m++)
			// {
			// if (ResultRate[m] != null && !ResultRate[m].trim().equals(""))
			// {
			// tCalAccRate = Double.parseDouble(ResultRate[m]);
			// double tSubInterest = mLCInsureAccTraceSet.get(j).getMoney()
			// * tCalAccRate;
			// tInterest += tSubInterest;
			// }
			// }
			// double tBalance = mLCInsureAccTraceSet.get(j).getMoney() +
			// tInterest;
			// mLCInsureAccTraceSet.get(j).setMoney(tBalance);
			// tAccInterest += tBalance;
			// }
			// mLCInsureAccSet.get(i).setInsuAccBala(tAccInterest);
			// mLCInsureAccSet.get(i).setBalaDate(aBalanceDate);
			// mLCInsureAccSet.get(i).setBalaTime(PubFun.getCurrentTime());
			// }
			// break;
			break;

		default:
			aAccBalance = 0.0;
			break;
		}
		aAccBalance = tAccInterest;
		return aAccBalance;
	}

	/**
	 * 按照保单得到所有帐户的余额
	 *
	 * @param aPolNo
	 *            String
	 * @param aBalanceDate
	 *            String 结息日期
	 * @param aRateType
	 *            String 取得描述帐户利率的时间单位。
	 * @param aIntervalType
	 *            String 结息的时间单位。
	 * @return double
	 */
	public double getAccBalance(String aPolNo, String aBalanceDate,
			String aRateType, String aIntervalType) {
		double aAccBalance = 0.0;
		double tAccRate, tCalAccRate;
		String aInsuAccNo = "";
		int tInterval = 0;
		String tSql = "";
		// String iSql = "";
		double tAccInterest = 0.0;
		mLCInsureAccSet.clear();
		mLCInsureAccTraceSet.clear();

		tSql = "select * from LCInsureAcc where PolNo='" + aPolNo
				+ "' order by InsuAccNo";
		LCInsureAccDB tLCInsureAccDB = new LCInsureAccDB();
		mLCInsureAccSet = tLCInsureAccDB.executeQuery(tSql);

		for (int i = 1; i <= mLCInsureAccSet.size(); i++) {
			// 得到描述表中帐户利率
			LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
			if (i == 1
					|| !mLCInsureAccSet.get(i).getInsuAccNo()
							.equals(aInsuAccNo)) {
				LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
				tLMRiskInsuAccDB.setInsuAccNo(mLCInsureAccSet.get(i)
						.getInsuAccNo());
				if (!tLMRiskInsuAccDB.getInfo()) {
					return aAccBalance;
				}
				tLMRiskInsuAccSchema = tLMRiskInsuAccDB.getSchema();
				aInsuAccNo = mLCInsureAccSet.get(i).getInsuAccNo();
			}

			switch (Integer.parseInt(tLMRiskInsuAccSchema.getAccComputeFlag())) {
			// 不计息
			case 0:
				aAccBalance = 0.0;
				break;
			// 按照固定利率单利生息
			case 1:
				tAccRate = tLMRiskInsuAccSchema.getAccRate();
				tCalAccRate = TransAccRate(tAccRate, aRateType, "S",
						aIntervalType);

				LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
				LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet();
				tLCInsureAccTraceDB.setPolNo(aPolNo);
				tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);

				// tLCInsureAccTraceDB.setOtherNo(mLCInsureAccSet.get(i).getOtherNo());
				tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
				for (int j = 1; j <= tLCInsureAccTraceSet.size(); j++) {
					// 得到时间间隔
					tInterval = PubFun.calInterval(mLCInsureAccSet.get(i)
							.getBalaDate(), aBalanceDate, aIntervalType);
					double tInterest = tLCInsureAccTraceSet.get(j).getMoney()
							+ tLCInsureAccTraceSet.get(j).getMoney()
							* getIntvRate(tInterval, tCalAccRate, "S");
					tLCInsureAccTraceSet.get(j).setMoney(tInterest);
					mLCInsureAccTraceSet.add(tLCInsureAccTraceSet.get(j));
					tAccInterest += tInterest;
				}
				mLCInsureAccSet.get(i).setInsuAccBala(tAccInterest);
				mLCInsureAccSet.get(i).setBalaDate(aBalanceDate);
				mLCInsureAccSet.get(i).setBalaTime(PubFun.getCurrentTime());
				break;
			// 按照固定利率复利生息
			case 2:
				tAccRate = tLMRiskInsuAccSchema.getAccRate();
				tCalAccRate = TransAccRate(tAccRate, aRateType, "C",
						aIntervalType);

				LCInsureAccTraceDB tLCInsureAccTraceDB2 = new LCInsureAccTraceDB();
				LCInsureAccTraceSet tLCInsureAccTraceSet2 = new LCInsureAccTraceSet();
				tLCInsureAccTraceDB2.setPolNo(aPolNo);
				tLCInsureAccTraceDB2.setInsuAccNo(aInsuAccNo);

				// tLCInsureAccTraceDB2.setOtherNo(mLCInsureAccSet.get(i).getOtherNo());
				tLCInsureAccTraceSet2 = tLCInsureAccTraceDB2.query();
				for (int j = 1; j <= tLCInsureAccTraceSet2.size(); j++) {
					// 得到时间间隔
					tInterval = PubFun.calInterval(mLCInsureAccSet.get(i)
							.getBalaDate(), aBalanceDate, aIntervalType);
					double tInterest = tLCInsureAccTraceSet2.get(j).getMoney()
							+ tLCInsureAccTraceSet2.get(j).getMoney()
							* getIntvRate(tInterval, tCalAccRate, "C");
					tLCInsureAccTraceSet2.get(j).setMoney(tInterest);
					mLCInsureAccTraceSet.add(tLCInsureAccTraceSet2.get(j));
					tAccInterest += tInterest;
				}
				mLCInsureAccSet.get(i).setInsuAccBala(tAccInterest);
				mLCInsureAccSet.get(i).setBalaDate(aBalanceDate);
				mLCInsureAccSet.get(i).setBalaTime(PubFun.getCurrentTime());

				break;
			// 按照利率表单利生息
			case 3:
				tAccRate = AccountManage.getAccRate(tLMRiskInsuAccSchema
						.getSchema());
				tCalAccRate = TransAccRate(tAccRate, aRateType, "S",
						aIntervalType);

				LCInsureAccTraceDB tLCInsureAccTraceDB3 = new LCInsureAccTraceDB();
				LCInsureAccTraceSet tLCInsureAccTraceSet3 = new LCInsureAccTraceSet();
				tLCInsureAccTraceDB3.setPolNo(aPolNo);
				tLCInsureAccTraceDB3.setInsuAccNo(aInsuAccNo);

				// tLCInsureAccTraceDB3.setOtherNo(mLCInsureAccSet.get(i).getOtherNo());
				tLCInsureAccTraceSet3 = tLCInsureAccTraceDB3.query();
				for (int j = 1; j <= tLCInsureAccTraceSet3.size(); j++) {
					double tInterest = 0;
					// 得到分段的单利计算
					String[] ResultRate = getMultiAccRate(aInsuAccNo,
							tLMRiskInsuAccSchema.getSchema(), mLCInsureAccSet
									.get(i).getBalaDate(), aBalanceDate,
							aRateType, "S", aIntervalType);
					for (int m = 0; m < ResultRate.length; m++) {
						tCalAccRate = Double.parseDouble(ResultRate[m]);
						double tSubInterest = tLCInsureAccTraceSet3.get(j)
								.getMoney()
								* tCalAccRate;
						tInterest += tSubInterest;
					}
					double tBalance = tLCInsureAccTraceSet3.get(j).getMoney()
							+ tInterest;
					mLCInsureAccTraceSet.get(j).setMoney(tBalance);
					tAccInterest += tBalance;
				}
				mLCInsureAccSet.get(i).setInsuAccBala(tAccInterest);
				mLCInsureAccSet.get(i).setBalaDate(aBalanceDate);
				mLCInsureAccSet.get(i).setBalaTime(PubFun.getCurrentTime());

				break;
			// 按照利率表复利生息
			case 4:
				tAccRate = AccountManage.getAccRate(tLMRiskInsuAccSchema
						.getSchema());
				tCalAccRate = TransAccRate(tAccRate, aRateType, "C",
						aIntervalType);

				LCInsureAccTraceDB tLCInsureAccTraceDB4 = new LCInsureAccTraceDB();
				LCInsureAccTraceSet tLCInsureAccTraceSet4 = new LCInsureAccTraceSet();

				tLCInsureAccTraceDB4.setPolNo(aPolNo);
				tLCInsureAccTraceDB4.setInsuAccNo(aInsuAccNo);

				// tLCInsureAccTraceDB4.setOtherNo(mLCInsureAccSet.get(i).getOtherNo());
				tLCInsureAccTraceSet4 = tLCInsureAccTraceDB4.query();
				for (int j = 1; j <= tLCInsureAccTraceSet4.size(); j++) {
					double tInterest = 0;
					// 得到分段的复利计算
					String[] ResultRate = getMultiAccRate(aInsuAccNo,
							tLMRiskInsuAccSchema.getSchema(), mLCInsureAccSet
									.get(i).getBalaDate(), aBalanceDate,
							aRateType, "C", aIntervalType);
					for (int m = 0; m < ResultRate.length; m++) {
						tCalAccRate = Double.parseDouble(ResultRate[m]);
						double tSubInterest = tLCInsureAccTraceSet4.get(j)
								.getMoney()
								* tCalAccRate;
						tInterest += tSubInterest;
					}
					double tBalance = tLCInsureAccTraceSet4.get(j).getMoney()
							+ tInterest;
					mLCInsureAccTraceSet.get(j).setMoney(tBalance);
					tAccInterest += tBalance;
				}
				mLCInsureAccSet.get(i).setInsuAccBala(tAccInterest);
				mLCInsureAccSet.get(i).setBalaDate(aBalanceDate);
				mLCInsureAccSet.get(i).setBalaTime(PubFun.getCurrentTime());
				break;
			default:
				aAccBalance = 0.0;
				break;
			}
			aAccBalance += tAccInterest;
		}

		return aAccBalance;
	}

	/**
	 * 已知帐户余额得到帐户的利息（原描述帐户利率默认为年）
	 *
	 * @param aInsuAccNo
	 *            String
	 * @param aOriginAccBalance
	 *            double
	 * @param aStartDate
	 *            String
	 * @param aEndDate
	 *            String
	 * @param aIntervalType
	 *            String
	 * @return double
	 */
	public double getAccInterest(String aInsuAccNo, double aOriginAccBalance,
			String aStartDate, String aEndDate, String aIntervalType) {
		double aAccInterest = 0.0;
		double tAccRate, tCalAccRate;
		int tInterval = 0;
		String aRateType = "Y";
		// 得到时间间隔
		tInterval = PubFun.calInterval(aStartDate, aEndDate, aIntervalType);
		// 得到描述表中帐户利率
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		tLMRiskInsuAccDB.setInsuAccNo(aInsuAccNo);
		if (!tLMRiskInsuAccDB.getInfo()) {
			return aAccInterest;
		}

		switch (Integer.parseInt(tLMRiskInsuAccDB.getAccComputeFlag())) {
		case 0:

			// 不计息
			aAccInterest = 0.0;
			break;
		case 1:

			// 按照固定利率单利生息
			tAccRate = tLMRiskInsuAccDB.getAccRate();
			tCalAccRate = TransAccRate(tAccRate, aRateType, "S", aIntervalType);
			aAccInterest = aOriginAccBalance * tCalAccRate * tInterval;
			break;
		case 2:

			// 按照固定利率复利生息
			tAccRate = tLMRiskInsuAccDB.getAccRate();
			tCalAccRate = TransAccRate(tAccRate, aRateType, "C", aIntervalType);
			aAccInterest = aOriginAccBalance * tCalAccRate * tInterval;
			break;
		case 3:

			// 按照利率表单利生息
			tAccRate = AccountManage.getAccRate(tLMRiskInsuAccDB.getSchema());
			tCalAccRate = TransAccRate(tAccRate, aRateType, "S", aIntervalType);
			aAccInterest = aOriginAccBalance * tCalAccRate * tInterval;
			break;
		case 4:

			// 按照利率表复利生息
			tAccRate = AccountManage.getAccRate(tLMRiskInsuAccDB, aEndDate);
			tCalAccRate = TransAccRate(tAccRate, aRateType, "C", aIntervalType);

			// System.out.println(tCalAccRate + "第一次的日利率");
			aAccInterest = aOriginAccBalance * tCalAccRate * tInterval;
			break;
		default:
			aAccInterest = 0.0;
			break;
		}
		return aAccInterest;
	}

	/**
	 * 已知帐户余额得到帐户的利息（按照描述帐户利率）
	 *
	 * @param aInsuAccNo
	 *            String
	 * @param aRateType
	 *            String
	 * @param aOriginAccBalance
	 *            double
	 * @param aStartDate
	 *            String
	 * @param aEndDate
	 *            String
	 * @param aIntervalType
	 *            String
	 * @return double
	 */
	public double getAccInterest(String aInsuAccNo, String aRateType,
			double aOriginAccBalance, String aStartDate, String aEndDate,
			String aIntervalType) {
		double aAccInterest = 0.0;
		double tAccRate, tCalAccRate;
		int tInterval = 0;

		// 得到时间间隔
		tInterval = PubFun.calInterval(aStartDate, aEndDate, aIntervalType);
		// 得到描述表中帐户利率
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		tLMRiskInsuAccDB.setInsuAccNo(aInsuAccNo);
		if (!tLMRiskInsuAccDB.getInfo()) {
			return aAccInterest;
		}

		switch (Integer.parseInt(tLMRiskInsuAccDB.getAccComputeFlag())) {
		case 0:

			// 不计息
			aAccInterest = 0.0;
			break;
		case 1:

			// 按照固定利率单利生息
			tAccRate = tLMRiskInsuAccDB.getAccRate();
			tCalAccRate = TransAccRate(tAccRate, aRateType, "S", aIntervalType);
			aAccInterest = aOriginAccBalance * tCalAccRate * tInterval;
			break;
		case 2:

			// 按照固定利率复利生息
			tAccRate = tLMRiskInsuAccDB.getAccRate();
			tCalAccRate = TransAccRate(tAccRate, aRateType, "C", aIntervalType);
			aAccInterest = aOriginAccBalance * tCalAccRate * tInterval;
			break;
		case 3:

			// 按照利率表单利生息
			tAccRate = AccountManage.getAccRate(tLMRiskInsuAccDB.getSchema(),
					aRateType);
			tCalAccRate = TransAccRate(tAccRate, aRateType, "S", aIntervalType);
			aAccInterest = aOriginAccBalance * tCalAccRate * tInterval;
			break;
		case 4:

			// 按照利率表复利生息
			tAccRate = AccountManage.getAccRate(tLMRiskInsuAccDB.getSchema(),
					aRateType);
			tCalAccRate = TransAccRate(tAccRate, aRateType, "C", aIntervalType);
			aAccInterest = aOriginAccBalance * tCalAccRate * tInterval;
			break;
		default:
			aAccInterest = 0.0;
			break;
		}
		return aAccInterest;
	}

	/**
	 * 得到描述表中的利率
	 *
	 * @param aLMRiskInsuAccSchema
	 *            LMRiskInsuAccSchema
	 * @return double
	 */
	private static double getAccRate(LMRiskInsuAccSchema aLMRiskInsuAccSchema) {
		double aAccRate = 0.0;
		LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
		Calculator tCalculator = new Calculator();
		tCalculator.addBasicFactor("InsuAccNo", tLMRiskInsuAccSchema
				.getInsuAccNo());
		// 默认为年利率
		tCalculator.addBasicFactor("RateType", "Y");
		// 待填加其它条件
		// tCalculator.addBasicFactor("","");
		tCalculator.setCalCode(tLMRiskInsuAccSchema.getAccCancelCode());
		String tStr = "";
		tStr = tCalculator.calculate();
		System.out.println("---str" + tStr);
		if (tStr != null && !tStr.trim().equals("")) {
			aAccRate = Double.parseDouble(tStr);
		}

		return aAccRate;
	}

	/**
	 * 按传入类型得到描述表中的利率
	 *
	 * @param aLMRiskInsuAccSchema
	 *            LMRiskInsuAccSchema
	 * @param aRateType
	 *            String
	 * @return double
	 */
	private static double getAccRate(LMRiskInsuAccSchema aLMRiskInsuAccSchema,
			String aRateType) {
		double aAccRate = 0.0;
		LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
		Calculator tCalculator = new Calculator();
		tCalculator.addBasicFactor("InsuAccNo", tLMRiskInsuAccSchema
				.getInsuAccNo());
		tCalculator.addBasicFactor("RateType", aRateType);
		// 待填加其它条件
		// tCalculator.addBasicFactor("","");
		tCalculator.setCalCode(tLMRiskInsuAccSchema.getAccCancelCode());
		String tStr = "";
		tStr = tCalculator.calculate();
		System.out.println("---str" + tStr);
		if (tStr != null && !tStr.trim().equals("")) {
			aAccRate = Double.parseDouble(tStr);
		}

		return aAccRate;
	}

	/**
	 * 得到描述表中的利率（正确）
	 *
	 * @param cLMRiskInsuAccDB
	 *            LMRiskInsuAccDB 险种保险帐户
	 * @param cEndDate
	 *            String 利率查询结束日期
	 * @return double
	 */
	private static double getAccRate(LMRiskInsuAccDB cLMRiskInsuAccDB,
			String cEndDate) {
		// 设置利率默认值
		double aAccRate = 0.0;

		ExeSQL tExeSQL = new ExeSQL();
		// 根据帐户类型、结算日期，查询利率
		StringBuffer tSBql = new StringBuffer(128);
		tSBql.append("select Rate from Interest000001 where StartDate <= '");
		tSBql.append(cEndDate);
		// tSBql.append("' and EndDate >= '");
		// tSBql.append(cEndDate);
		tSBql.append("' and InsuAccNo = '");
		tSBql.append(cLMRiskInsuAccDB.getInsuAccNo());
		tSBql.append("' order by EndDate desc");
		SSRS tSSRS = tExeSQL.execSQL(tSBql.toString());
		if (tSSRS.getMaxRow() > 0) {
			aAccRate = Double.parseDouble(tSSRS.GetText(1, 1));
		}
		return aAccRate;
	}

	/**
	 * 利率转换函数
	 *
	 * @param OriginRate
	 *            double 原始利率
	 * @param OriginRateType
	 *            String 原始利率类型：年利率（"Y")，月利率("M")，日利率("D")
	 * @param TransType
	 *            String 复利转换("C")compound，单利转换("S")simple
	 * @param DestRateType
	 *            String 年利率，月利率,日利率
	 * @return double 例子：TransAccRate(0.48,"Y","C","D") 将0.48的年复利，转换为日复利
	 */
	public static double TransAccRate(double OriginRate, String OriginRateType,
			String TransType, String DestRateType) {
		double DestRate = 0;
		double aPower;

		// Add by Minim for RF of BQ
		// if (TransType.equals("1"))
		// {
		// TransType = "S";
		// }
		// if (TransType.equals("2"))
		// {
		// TransType = "C";
		// }
		// 判定OriginRateType，决定原始利率类型（年、月、日）
		if (OriginRateType.equals("1")) {
			OriginRateType = "Y";
		}
		if (OriginRateType.equals("2")) {
			OriginRateType = "M";
		}
		if (OriginRateType.equals("3")) {
			OriginRateType = "D";
		}
		// End add by Minim

		// 复利处理
		if (TransType.equals("C")) {
			// 年复利转换
			if (OriginRateType.equals("Y")) {
				// translate to year
				if (DestRateType.equals("Y")) {
					DestRate = OriginRate;
				}
				// translate to month
				else if (DestRateType.equals("M")) {
					aPower = 1.0 / 12.0;
					DestRate = java.lang.Math.pow(1 + OriginRate, aPower) - 1;
				}
				// translate to day
				else if (DestRateType.equals("D")) {
					aPower = 1.0 / Double.parseDouble(SysConst.DAYSOFYEAR);
					DestRate = java.lang.Math.pow(1 + OriginRate, aPower) - 1;
				} else {
					System.out.println("-----CY no this DestRateType----");
				}
			}
			// 月复利转换
			else if (OriginRateType.equals("M")) {
				// translate to month
				if (DestRateType.equals("M")) {
					DestRate = OriginRate;
				}
				// translate to year
				else if (DestRateType.equals("Y")) {
					aPower = 12;
					DestRate = java.lang.Math.pow(1 + OriginRate, aPower) - 1;
				}
				// translate to day
				else if (DestRateType.equals("D")) {
					aPower = 1.0 / 30.0;
					DestRate = java.lang.Math.pow(1 + OriginRate, aPower) - 1;
				} else {
					System.out.println("-----CM no this DestRateType----");
				}
			}
			// 日复利转换
			else if (OriginRateType.equals("D")) {
				// translate to day
				if (DestRateType.equals("D")) {
					DestRate = OriginRate;
				}
				// translate to month
				else if (DestRateType.equals("M")) {
					aPower = 30;
					DestRate = java.lang.Math.pow(1 + OriginRate, aPower) - 1;
				}
				// translate to year
				else if (DestRateType.equals("Y")) {
					aPower = 365;
					DestRate = java.lang.Math.pow(1 + OriginRate, aPower) - 1;
				} else {
					System.out.println("-----CD no this DestRateType----");
				}
			} else {
				System.out.println("------C no this OriginRateType------");
			}
		}
		// 单利处理
		else if (TransType.equals("S")) {
			// 年单利转换
			if (OriginRateType.equals("Y")) {
				// translate to year
				if (DestRateType.equals("Y")) {
					DestRate = OriginRate;
				}
				// translate to month
				else if (DestRateType.equals("M")) {
					DestRate = OriginRate / 12;
				}
				// translate to day
				else if (DestRateType.equals("D")) {
					DestRate = OriginRate / 365;
				} else {
					System.out.println("-----SY no this DestRateType----");
				}
			}
			// 月单利转换
			else if (OriginRateType.equals("M")) {
				// translate to month
				if (DestRateType.equals("M")) {
					DestRate = OriginRate;
				}
				// translate to year
				else if (DestRateType.equals("Y")) {
					DestRate = OriginRate * 12;
				}
				// translate to day
				else if (DestRateType.equals("D")) {
					DestRate = OriginRate / 30;
				} else {
					System.out.println("-----SM no this DestRateType----");
				}
			}
			// 日单利转换
			else if (OriginRateType.equals("D")) {
				// translate to day
				if (DestRateType.equals("D")) {
					DestRate = OriginRate;
				}
				// translate to month
				else if (DestRateType.equals("M")) {
					DestRate = OriginRate * 30;
				}
				// translate to year
				else if (DestRateType.equals("Y")) {
					DestRate = OriginRate * 365;
				} else {
					System.out.println("-----SD no this DestRateType----");
				}
			} else {
				System.out.println("------S no this OriginRateType------");
			}
		} else {
			System.out.println("-------have not this TransType------");
		}
		// 返回转换后的实际利率
		return DestRate;
	}

	/**
	 * 利率转换函数 add by frost 2007-10-09 不能按每月30天，每年365天计算，而应该按照实际的天数转换
	 *
	 * @param OriginRate double 原始利率
	 * @param OriginRateType String 原始利率类型：年利率（"1")，月利率("2")，日利率("3")
	 * @param TransType String 复利转换("C")compound，单利转换("S")simple
	 * @param DestRateType String 转换后的利率类型 年利率（"Y")，月利率("M")，日利率("D")
	 * @param MonthLength int 交易日期所在的月份天数
	 * @param YearLength int 交易日期所在的年数天数
	 * @return double 例子：TransAccRate(0.48,"1","C","D",30,365) 将0.48的年复利，转换为日复利
	 */
	public static double TransAccRate(double OriginRate, String OriginRateType,
			String TransType, String DestRateType, int MonthLength, int YearLength) {
		double DestRate = 0;
		double aPower;

		// 判定OriginRateType，决定原始利率类型（年、月、日）
		if (OriginRateType.equals("1")) {
			OriginRateType = "Y";
		}
		if (OriginRateType.equals("2")) {
			OriginRateType = "M";
		}
		if (OriginRateType.equals("3")) {
			OriginRateType = "D";
		}
		// End add by Minim

		// 复利处理
		if (TransType.equals("C")) {
			// 年复利转换
			if (OriginRateType.equals("Y")) {
				// translate to year
				if (DestRateType.equals("Y")) {
					DestRate = OriginRate;
				}
				// translate to month
				else if (DestRateType.equals("M")) {
					aPower = 1.0 / 12.0;
					DestRate = java.lang.Math.pow(1 + OriginRate, aPower) - 1;
				}
				// translate to day
				else if (DestRateType.equals("D")) {
					aPower = 1.0 / ((double)(YearLength));
					DestRate = java.lang.Math.pow(1 + OriginRate, aPower) - 1;
				} else {
					System.out.println("-----CY no this DestRateType----");
				}
			}
			// 月复利转换
			else if (OriginRateType.equals("M")) {
				// translate to month
				if (DestRateType.equals("M")) {
					DestRate = OriginRate;
				}
				// translate to year
				else if (DestRateType.equals("Y")) {
					aPower = 12;
					DestRate = java.lang.Math.pow(1 + OriginRate, aPower) - 1;
				}
				// translate to day
				else if (DestRateType.equals("D")) {
					aPower = 1.0 / ((double)(MonthLength));
					DestRate = java.lang.Math.pow(1 + OriginRate, aPower) - 1;
				} else {
					System.out.println("-----CM no this DestRateType----");
				}
			}
			// 日复利转换
			else if (OriginRateType.equals("D")) {
				// translate to day
				if (DestRateType.equals("D")) {
					DestRate = OriginRate;
				}
				// translate to month
				else if (DestRateType.equals("M")) {
					aPower = (double)(MonthLength);
					DestRate = java.lang.Math.pow(1 + OriginRate, aPower) - 1;
				}
				// translate to year
				else if (DestRateType.equals("Y")) {
					aPower = (double)(YearLength);
					DestRate = java.lang.Math.pow(1 + OriginRate, aPower) - 1;
				} else {
					System.out.println("-----CD no this DestRateType----");
				}
			} else {
				System.out.println("------C no this OriginRateType------");
			}
		}
		// 单利处理
		else if (TransType.equals("S")) {
			// 年单利转换
			if (OriginRateType.equals("Y")) {
				// translate to year
				if (DestRateType.equals("Y")) {
					DestRate = OriginRate;
				}
				// translate to month
				else if (DestRateType.equals("M")) {
					DestRate = OriginRate / 12;
				}
				// translate to day
				else if (DestRateType.equals("D")) {
					DestRate = OriginRate / ((double)(YearLength));
				} else {
					System.out.println("-----SY no this DestRateType----");
				}
			}
			// 月单利转换
			else if (OriginRateType.equals("M")) {
				// translate to month
				if (DestRateType.equals("M")) {
					DestRate = OriginRate;
				}
				// translate to year
				else if (DestRateType.equals("Y")) {
					DestRate = OriginRate * 12;
				}
				// translate to day
				else if (DestRateType.equals("D")) {
					DestRate = OriginRate / ((double)(MonthLength));
				} else {
					System.out.println("-----SM no this DestRateType----");
				}
			}
			// 日单利转换
			else if (OriginRateType.equals("D")) {
				// translate to day
				if (DestRateType.equals("D")) {
					DestRate = OriginRate;
				}
				// translate to month
				else if (DestRateType.equals("M")) {
					DestRate = OriginRate * ((double)(MonthLength));
				}
				// translate to year
				else if (DestRateType.equals("Y")) {
					DestRate = OriginRate * ((double)(YearLength));
				} else {
					System.out.println("-----SD no this DestRateType----");
				}
			} else {
				System.out.println("------S no this OriginRateType------");
			}
		} else {
			System.out.println("-------have not this TransType------");
		}
		// 返回转换后的实际利率
		return DestRate;
	}


	/**
	 * 转换给定时间点的现金价值
	 *
	 * @param OriginValue
	 *            double 原点现金
	 * @param OriginDate
	 *            String 原点时间
	 * @param GivenDate
	 *            String 贴现时间点
	 * @return double 例子：TransCashValue(1000,"2006-12-31","2005-12-31")
	 *         将2006-12-31的1000元按活期利率贴现为2005-12-31的价值
	 *         假如2006年活期利率为i则2005-12-31的价值v = 1000 / ( 1 + i * ( "2006-12-31" -
	 *         "2005-12-31" ) / 365 )
	 */
	public static double TransCashValue(double OriginValue, String OriginDate,
			String GivenDate) {
		if (PubFun.calInterval(GivenDate, OriginDate, "D") > 0) {
			double tmpIntrest = 1;
			// 单利，just单利
			StringBuffer tSql = new StringBuffer();
			tSql
					.append("select r.* from (select Rate rate, to_char(EndDate, 'YYYY-MM-DD') enddate from LDBankRate where RateType = 'S' and RateIntv = 'Y' and StartDate <= '");
			tSql.append(GivenDate);
			tSql.append("' and EndDate >= '");
			tSql.append(GivenDate);
			tSql
					.append("' union select Rate, to_char(EndDate, 'YYYY-MM-DD') from LDBankRate where RateType = 'S' and RateIntv = 'Y' and StartDate >= '");
			tSql.append(GivenDate);
			tSql.append("' and EndDate <= '");
			tSql.append(OriginDate);
			tSql
					.append("' union select Rate, to_char(EndDate, 'YYYY-MM-DD') from LDBankRate where RateType = 'S' and RateIntv = 'Y' and StartDate <= '");
			tSql.append(OriginDate);
			tSql.append("' and EndDate >= '");
			tSql.append(OriginDate);
			tSql.append("') r order by r.enddate asc");
			SSRS tSSRS = new ExeSQL().execSQL(tSql.toString());
			if (tSSRS != null && tSSRS.getMaxRow() >= 1) {
				String CDate = GivenDate;
				for (int i = 1; i <= tSSRS.getMaxRow(); i++) {
					while (PubFun.calInterval(CDate, tSSRS.GetText(i, 2), "D") > 0
							&& PubFun.calInterval(CDate, OriginDate, "D") > 0) {
						if (PubFun.calInterval(CDate, tSSRS.GetText(i, 2), "D") >= 365) {
							tmpIntrest += Double.parseDouble(tSSRS
									.GetText(i, 1));
							CDate = PubFun.getLastDate(CDate, 365, "D");
						} else {
							tmpIntrest += Double.parseDouble(tSSRS
									.GetText(i, 1))
									* PubFun.calInterval(CDate, tSSRS.GetText(
											i, 2), "D") / 365;
							CDate = tSSRS.GetText(i, 2);
						}
					}
				}
			}
			OriginValue = OriginValue / tmpIntrest;
		} else if (PubFun.calInterval(GivenDate, OriginDate, "D") < 0) {

			double tmpIntrest = 1;
			// 单利，just单利
			StringBuffer tSql = new StringBuffer();
			tSql
					.append("select r.* from (select Rate rate, to_char(EndDate, 'YYYY-MM-DD') enddate from LDBankRate where RateType = 'S' and RateIntv = 'Y' and StartDate <= '");
			tSql.append(GivenDate);
			tSql.append("' and EndDate >= '");
			tSql.append(GivenDate);
			tSql
					.append("' union select Rate, to_char(EndDate, 'YYYY-MM-DD') from LDBankRate where RateType = 'S' and RateIntv = 'Y' and StartDate >= '");
			tSql.append(OriginDate);
			tSql.append("' and EndDate <= '");
			tSql.append(GivenDate);
			tSql
					.append("' union select Rate, to_char(EndDate, 'YYYY-MM-DD') from LDBankRate where RateType = 'S' and RateIntv = 'Y' and StartDate <= '");
			tSql.append(OriginDate);
			tSql.append("' and EndDate >= '");
			tSql.append(OriginDate);
			tSql.append("') r order by r.enddate asc");
			SSRS tSSRS = new ExeSQL().execSQL(tSql.toString());
			if (tSSRS != null && tSSRS.getMaxRow() >= 1) {
				String CDate = OriginDate;
				for (int i = 1; i <= tSSRS.getMaxRow(); i++) {
					while (PubFun.calInterval(CDate, tSSRS.GetText(i, 2), "D") > 0
							&& PubFun.calInterval(CDate, GivenDate, "D") > 0) {
						if (PubFun.calInterval(CDate, tSSRS.GetText(i, 2), "D") >= 365) {
							tmpIntrest += Double.parseDouble(tSSRS
									.GetText(i, 1));
							CDate = PubFun.getLastDate(CDate, 365, "D");
						} else {
							tmpIntrest += Double.parseDouble(tSSRS
									.GetText(i, 1))
									* PubFun.calInterval(CDate, tSSRS.GetText(
											i, 2), "D") / 365;
							CDate = tSSRS.GetText(i, 2);
						}
					}
				}
			}
			OriginValue = OriginValue * tmpIntrest;

		}
		return Arith.round(OriginValue, 2);
	}

	/**
	 * 得到结息后的帐户记录
	 *
	 * @return LCInsureAccSet
	 */
	public LCInsureAccSet getInsureAcc() {
		return mLCInsureAccSet;
	}

	/**
	 * 得到分段结息计算参数。(未测试)
	 *
	 * @param aLCInsureAccSchema
	 *            LCInsureAccSchema
	 * @param aLMRiskInsuAccSchema
	 *            LMRiskInsuAccSchema
	 * @param aBalanceDate
	 *            String
	 * @param aTransType
	 *            String
	 * @param aIntervalType
	 *            String
	 * @return String[]
	 */
	private String[] getMultiAccRate1(LCInsureAccSchema aLCInsureAccSchema,
			LMRiskInsuAccSchema aLMRiskInsuAccSchema, String aBalanceDate,
			String aTransType, String aIntervalType) {
		String tSql = "";
		String ResultArray[] = new String[100];
		Calculator tCalculator = new Calculator();
		String TableName = aLMRiskInsuAccSchema.getAccRateTable();
		if (aLMRiskInsuAccSchema.getAccCancelCode() != null
				&& !aLMRiskInsuAccSchema.getAccCancelCode().trim().equals("")) {
			try {
				tSql = PubFun1.getSQL(aLMRiskInsuAccSchema.getAccCancelCode(),
						tCalculator);
			} catch (Exception e) {
				e.printStackTrace();
				// @@错误处理
				CError tError = new CError();
				tError.moduleName = "AccountManage";
				tError.functionName = "getMultiAccInterest";
				tError.errorMessage = e.toString();
				this.mErrors.addOneError(tError);
			}
		} else {
			TableName = aLMRiskInsuAccSchema.getAccRateTable();
			tSql = "select * from '" + TableName + "' where InsuAccNo='"
					+ aLCInsureAccSchema.getInsuAccNo() + "' and EndDate>='"
					+ aLCInsureAccSchema.getBalaDate() + "' and EndDate<='"
					+ aBalanceDate + "' order by EndDate";
		}

		Statement stmt = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		double aValue = 0.0;
		double aResult = 0.0;
		String aDate = "";
		String tStartDate = "";

		DBOper db = new DBOper("");
		Connection con = db.getConnection();

		System.out.println(tSql.trim());
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			// 得到其中所有的时间段信息
			rs = stmt.executeQuery(StrTool.GBKToUnicode(tSql));
			int i = 0;
			while (rs.next()) {
				i++;
				if (i == 1) {
					tStartDate = aLCInsureAccSchema.getBalaDate();
				}
				aDate = rs.getString("EndDate");
				aValue = rs.getDouble("Rate");
				String aRateIntv = rs.getString("RateIntv");
				double tCalAccRate = TransAccRate(aValue, aRateIntv,
						aTransType, aIntervalType);
				int tInterval = PubFun.calInterval(tStartDate, aDate,
						aIntervalType);
				if (tInterval > 0) {
					aResult = getIntvRate(tInterval, tCalAccRate, aTransType);
					ResultArray[i - 1] = String.valueOf(aResult);
				}
				tStartDate = aDate;
			}
			rs.close();

			// 得到结息日在利率临界点后
			if (ResultArray.length > 0) {
				int tLength = ResultArray.length - 1;
				tSql = "select * from '" + TableName + "' where InsuAccNo='"
						+ aLCInsureAccSchema.getInsuAccNo()
						+ "' and StartDate<'" + aBalanceDate
						+ "' and EndDate>'" + aBalanceDate
						+ "' order by EndDate";
				rs1 = stmt.executeQuery(StrTool.GBKToUnicode(tSql));
				i = 0;
				while (rs1.next()) {
					i++;
					aDate = rs1.getString("StartDate");
					aValue = rs1.getDouble("Rate");
					String aRateIntv = rs.getString("RateIntv");
					double tCalAccRate = TransAccRate(aValue, aRateIntv,
							aTransType, aIntervalType);
					int tInterval = PubFun.calInterval(aDate, aBalanceDate,
							aIntervalType);
					aResult = getIntvRate(tInterval, tCalAccRate, aTransType);
					ResultArray[tLength++] = String.valueOf(aResult);
				}
				rs1.close();
			} else {
				tSql = "select * from '" + TableName + "' where InsuAccNo='"
						+ aLCInsureAccSchema.getInsuAccNo()
						+ "' and StartDate<'"
						+ aLCInsureAccSchema.getBalaDate() + "' and EndDate>'"
						+ aBalanceDate + "' order by EndDate";
				rs2 = stmt.executeQuery(StrTool.GBKToUnicode(tSql));
				i = 0;
				while (rs2.next()) {
					i++;
					aDate = rs2.getString("StartDate");
					aValue = rs2.getDouble("Rate");
					String aRateIntv = rs.getString("RateIntv");
					double tCalAccRate = TransAccRate(aValue, aRateIntv,
							aTransType, aIntervalType);
					int tInterval = PubFun.calInterval(aDate, rs2
							.getString("EndDate"), aIntervalType);
					aResult = getIntvRate(tInterval, tCalAccRate, aTransType);
					ResultArray[i - 1] = String.valueOf(aResult);
				}
				rs1.close();
			}
		} catch (Exception e) {
			// @@错误处理
			CError tError = new CError();
			tError.moduleName = "AccountManage";
			tError.functionName = "getMultiAccInterest";
			tError.errorMessage = e.toString();
			this.mErrors.addOneError(tError);

		}
		return ResultArray;
	}

	/**
	 * 得到分段结息的参数（推荐）
	 *
	 * @param aLCInsureAccSchema
	 *            LCInsureAccSchema
	 * @param aLMRiskInsuAccSchema
	 *            LMRiskInsuAccSchema
	 * @param aBalanceDate
	 *            String
	 * @param aRateType
	 *            String
	 * @param aTransType
	 *            String
	 * @param aIntervalType
	 *            String
	 * @return String[]
	 */
	private String[] getMultiAccRate(LCInsureAccSchema aLCInsureAccSchema,
			LMRiskInsuAccSchema aLMRiskInsuAccSchema, String aBalanceDate,
			String aRateType, String aTransType, String aIntervalType) {
		String tSql = "";
		String[] ResultArray = new String[100];
		Calculator tCalculator = new Calculator();
		String TableName = aLMRiskInsuAccSchema.getAccRateTable();
		if (aLMRiskInsuAccSchema.getAccCancelCode() != null
				&& !aLMRiskInsuAccSchema.getAccCancelCode().trim().equals("")) {
			try {
				tSql = PubFun1.getSQL(aLMRiskInsuAccSchema.getAccCancelCode(),
						tCalculator);
			} catch (Exception e) {
				e.printStackTrace();
				// @@错误处理
				CError tError = new CError();
				tError.moduleName = "AccountManage";
				tError.functionName = "getMultiAccInterest";
				tError.errorMessage = e.toString();
				this.mErrors.addOneError(tError);
			}
		} else {
			TableName = aLMRiskInsuAccSchema.getAccRateTable();
			tSql = "select * from '" + TableName + "' where InsuAccNo='"
					+ aLCInsureAccSchema.getInsuAccNo() + "' and StartDate<='"
					+ aBalanceDate + "' and EndDate>='"
					+ aLCInsureAccSchema.getBalaDate() + "' order by EndDate";
		}

		Statement stmt = null;
		ResultSet rs = null;
		double aValue = 0.0;
		double aResult = 0.0;
		String aDate = "";
		String tStartDate = "";

		DBOper db = new DBOper("");
		Connection con = db.getConnection();

		System.out.println(tSql.trim());
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(StrTool.GBKToUnicode(tSql));
			int i = 0;
			while (rs.next()) {
				i++;
				aDate = rs.getString("EndDate");
				aValue = rs.getDouble("Rate");
				String aRateIntv = rs.getString("RateIntv");
				double tCalAccRate = TransAccRate(aValue, aRateIntv,
						aTransType, aIntervalType);
				if (i == 1) {
					tStartDate = aLCInsureAccSchema.getBalaDate();
				}
				// 结息点超过某个利率临界点
				if (PubFun.calInterval(aDate, aBalanceDate, aIntervalType) > 0) {
					int tInterval = PubFun.calInterval(tStartDate, aDate,
							aIntervalType);
					if (tInterval > 0) {
						aResult = getIntvRate(tInterval, tCalAccRate,
								aTransType);
						ResultArray[i - 1] = String.valueOf(aResult);
					}
					tStartDate = aDate;
				} else {
					int tInterval = PubFun.calInterval(tStartDate,
							aBalanceDate, aIntervalType);
					if (tInterval > 0) {
						aResult = getIntvRate(tInterval, tCalAccRate,
								aTransType);
						ResultArray[i - 1] = String.valueOf(aResult);
					}
					break;
				}
			}
			rs.close();
		} catch (Exception e) {
			// @@错误处理
			CError tError = new CError();
			tError.moduleName = "AccountManage";
			tError.functionName = "getMultiAccInterest";
			tError.errorMessage = e.toString();
			this.mErrors.addOneError(tError);

		}
		return ResultArray;
	}

	/**
	 * 杨红于20050613添加
	 *
	 * @param TableName
	 *            String 要查询的利率表，比如ldbankrate,interest000001等
	 * @param aOriginBalaDate
	 *            String 上次节息日期或源日期
	 * @param aBalaDate
	 *            String 目标节息日期
	 * @param aRateType
	 *            String 源利率类型，比如按年利率，月利率，日利率等
	 * @param aTransType
	 *            String 转换类型，复利或单利
	 * @param aIntervalType
	 *            String 目标利率类型，比如按年利率，月利率，日利率等
	 * @return String[]
	 */
	private String[] getMultiAccRate(String TableName, String aOriginBalaDate,
			String aBalaDate, String aRateType, String aTransType,
			String aIntervalType) {
		String tSql = "";
		String[] ResultArray = new String[100];
		if (TableName == null || TableName.equals("null")
				|| TableName.trim().equals("")
				|| TableName.toUpperCase().equals("LDBANKRATE")) {
			tSql = "select * from LDBankRate where StartDate<='" + aBalaDate
					+ "' and EndDate>='" + aOriginBalaDate
					+ "' order by EndDate";

		}
		// 添加结束
		// tSql = "select * from " + TableName + " where InsuAccNo='" +
		// aInsuAccNo + "' and StartDate<='" + aBalanceDate +
		// "' and EndDate>='" + aOriginBalaDate + "' order by EndDate";
		Statement stmt = null;
		ResultSet rs = null;
		double aValue = 0.0;
		double aResult = 0.0;
		String aDate = "";
		String tStartDate = "";

		DBOper db = new DBOper("");
		Connection con = db.getConnection();

		System.out.println(tSql.trim());
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(StrTool.GBKToUnicode(tSql));
			int i = 0;
			while (rs.next()) {
				i++;
				aDate = rs.getString("EndDate");
				aValue = rs.getDouble("Rate");
				String aRateIntv = rs.getString("RateIntv");
				double tCalAccRate = TransAccRate(aValue, aRateType,
						aTransType, aIntervalType);
				if (i == 1) {
					tStartDate = aOriginBalaDate;
				}
				// 结息点超过某个利率临界点
				if (PubFun.calInterval(aDate, aBalaDate, aIntervalType) > 0) {
					int tInterval = PubFun.calInterval(tStartDate, aDate,
							aIntervalType);
					if (tInterval > 0) {
						aResult = getIntvRate(tInterval, tCalAccRate,
								aTransType);
						ResultArray[i - 1] = String.valueOf(aResult);
					}
					tStartDate = aDate;
				} else {
					int tInterval = PubFun.calInterval(tStartDate, aBalaDate,
							aIntervalType);
					if (tInterval > 0) {
						aResult = getIntvRate(tInterval, tCalAccRate,
								aTransType);
						ResultArray[i - 1] = String.valueOf(aResult);
					}
					break;
				}
			}
			rs.close();
		} catch (Exception e) {
			// @@错误处理
			CError tError = new CError();
			tError.moduleName = "AccountManage";
			tError.functionName = "getMultiAccInterest";
			tError.errorMessage = e.toString();
			this.mErrors.addOneError(tError);
		}
		try {
			if (!con.isClosed()) {
				con.close();
			}
		} catch (Exception e) {
			CError tError = new CError();
			tError.moduleName = "AccountManage";
			tError.functionName = "getMultiAccInterest";
			tError.errorMessage = e.toString();
			this.mErrors.addOneError(tError);
		}

		return ResultArray;
	}

	/**
	 * 得到分段结息的参数（推荐）
	 *
	 * @param aInsuAccNo
	 *            String
	 * @param aLMRiskInsuAccSchema
	 *            LMRiskInsuAccSchema
	 * @param aOriginBalaDate
	 *            String
	 * @param aBalanceDate
	 *            String
	 * @param aRateType
	 *            String 原描述利率的类型
	 * @param aTransType
	 *            String 单利复利
	 * @param aIntervalType
	 *            String 转换成利率类型
	 * @return String[]
	 */
	private String[] getMultiAccRate(String aInsuAccNo,
			LMRiskInsuAccSchema aLMRiskInsuAccSchema, String aOriginBalaDate,
			String aBalanceDate, String aRateType, String aTransType,
			String aIntervalType) {
		String tSql = "";
		// String[] tResultArray = new String[100];
		String[] tResultArray = new String[1];
		Calculator tCalculator = new Calculator();
		String TableName = aLMRiskInsuAccSchema.getAccRateTable();
		// 获取利息的方式不同，一种是通过calmode的描述获取，一种是直接查询利息表
		if (aLMRiskInsuAccSchema.getAccCancelCode() != null
				&& !aLMRiskInsuAccSchema.getAccCancelCode().trim().equals("")) {
			try {
				tSql = PubFun1.getSQL(aLMRiskInsuAccSchema.getAccCancelCode(),
						tCalculator);
			} catch (Exception e) {
				e.printStackTrace();
				// @@错误处理
				CError tError = new CError();
				tError.moduleName = "AccountManage";
				tError.functionName = "getMultiAccInterest";
				tError.errorMessage = e.toString();
				this.mErrors.addOneError(tError);
			}
		} else {
			TableName = aLMRiskInsuAccSchema.getAccRateTable();
			// 杨红添加开始
			// if (TableName == null || TableName.equals("null")
			// || TableName.toUpperCase().equals("LDBANKRATE"))
			// {
			// tSql = "select * from LDBankRate where StartDate<='" +
			// aBalanceDate
			// + "' and EndDate>='" + aOriginBalaDate + "' order by EndDate";
			//
			// }
			// 添加结束

			// tSql = "select * from " + TableName + " where InsuAccNo='" +
			// aInsuAccNo
			// + "' and StartDate<='" + aBalanceDate + "' and EndDate>='" +
			// aOriginBalaDate
			// + "' order by EndDate";

			StringBuffer tSBql = new StringBuffer(128);
			tSBql.append("select Rate,EndDate from ");
			tSBql.append(TableName);
			tSBql.append(" where InsuAccNo='");
			tSBql.append(aInsuAccNo);
			tSBql.append("' and StartDate<='");
			tSBql.append(aBalanceDate);
			tSBql.append("' order by EndDate desc");
			tSql = tSBql.toString();
		}

		// Statement stmt = null;
		// ResultSet rs = null;
		// double tValue = 0.0;
		double tResult = 0.0;
		double tRate = 0.0;
		// String tDate = "";
		String tEndDate = "";
		String tStartDate = "";

		ExeSQL tExeSQL = new ExeSQL();
		SSRS tSSRS = tExeSQL.execSQL(tSql);
		// 判定是否查询到有效利率
		if (tSSRS.getMaxRow() > 0) {
			tRate = Double.parseDouble(tSSRS.GetText(1, 1));
			tEndDate = tSSRS.GetText(1, 2);

			// 转换后得到实际利率
			double tCalAccRate = TransAccRate(tRate, aRateType, aTransType,
					aIntervalType);

			tStartDate = aOriginBalaDate;

			// 结息点超过某个利率临界点
			// if (PubFun.calInterval(tEndDate, aBalanceDate, aIntervalType) >
			// 0)
			// {
			int tInterval = PubFun.calInterval(tStartDate, aBalanceDate,
					aIntervalType);
			if (tInterval > 0) {
				tResult = getIntvRate(tInterval, tCalAccRate, aTransType);
				tResultArray[0] = String.valueOf(tResult);
			}
			tStartDate = tEndDate;
			// }
			// else
			// {
			// int tInterval = PubFun.calInterval(tStartDate, aBalanceDate,
			// aIntervalType);
			// if (tInterval > 0)
			// {
			// tResult = getIntvRate(tInterval, tCalAccRate, aTransType);
			// tResultArray[0] = String.valueOf(tResult);
			// }
			// }
		}

		// DBOper db = new DBOper("");
		// Connection con = db.getConnection();
		// // System.out.println(tSql.trim());
		// try
		// {
		// stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE
		// , ResultSet.CONCUR_READ_ONLY);
		// rs = stmt.executeQuery(StrTool.GBKToUnicode(tSql));
		// int i = 0;
		// while (rs.next())
		// {
		// i++;
		// tDate = rs.getString("EndDate");
		// tValue = rs.getDouble("Rate");
		// // String aRateIntv = rs.getString("RateIntv");
		// //转换后得到实际利率
		// double tCalAccRate = TransAccRate(tValue, aRateType, aTransType,
		// aIntervalType);
		// // tResultArray[i - 1] = String.valueOf(tCalAccRate);
		// if (i == 1)
		// {
		// tStartDate = aOriginBalaDate;
		// }
		// //结息点超过某个利率临界点
		// if (PubFun.calInterval(tDate, aBalanceDate, aIntervalType) > 0)
		// {
		// int tInterval = PubFun.calInterval(tStartDate, tDate, aIntervalType);
		// if (tInterval > 0)
		// {
		// tResult = getIntvRate(tInterval, tCalAccRate, aTransType);
		// tResultArray[i - 1] = String.valueOf(tResult);
		// }
		// tStartDate = tDate;
		// }
		// else
		// {
		// int tInterval = PubFun.calInterval(tStartDate, aBalanceDate,
		// aIntervalType);
		// if (tInterval > 0)
		// {
		// tResult = getIntvRate(tInterval, tCalAccRate, aTransType);
		// tResultArray[i - 1] = String.valueOf(tResult);
		// }
		// break;
		// }
		// }
		// rs.close();
		// }
		// catch (Exception e)
		// {
		// // @@错误处理
		// CError tError = new CError();
		// tError.moduleName = "AccountManage";
		// tError.functionName = "getMultiAccInterest";
		// tError.errorMessage = e.toString();
		// this.mErrors.addOneError(tError);
		// }
		// try
		// {
		// if (!con.isClosed())
		// {
		// con.close();
		// }
		// }
		// catch (Exception e)
		// {
		// CError tError = new CError();
		// tError.moduleName = "AccountManage";
		// tError.functionName = "getMultiAccInterest";
		// tError.errorMessage = e.toString();
		// this.mErrors.addOneError(tError);
		// }

		return tResultArray;
	}

	/**
	 * 得到帐户描述分段结息的参数（推荐）
	 *
	 * @param aInsuAccNo
	 *            String
	 * @param aLMRiskInsuAccSchema
	 *            LMRiskInsuAccSchema
	 * @param aBalance
	 *            double
	 * @param aOriginBalaDate
	 *            String
	 * @param aBalanceDate
	 *            String
	 * @param aTransType
	 *            String 单利复利
	 * @param aIntervalType
	 *            String 转换成利率类型
	 * @return double
	 */
	public double getMultiAccInterest(String aInsuAccNo,
			LMRiskInsuAccSchema aLMRiskInsuAccSchema, double aBalance,
			String aOriginBalaDate, String aBalanceDate, String aTransType,
			String aIntervalType) {
		String tSql = "";
		double tInterest = 0.0;
		String[] ResultArray = new String[100];
		Calculator tCalculator = new Calculator();
		String TableName = aLMRiskInsuAccSchema.getAccRateTable();
		if (aLMRiskInsuAccSchema.getAccCancelCode() != null
				&& !aLMRiskInsuAccSchema.getAccCancelCode().trim().equals("")) {
			try {
				tSql = PubFun1.getSQL(aLMRiskInsuAccSchema.getAccCancelCode(),
						tCalculator);
			} catch (Exception e) {
				e.printStackTrace();
				// @@错误处理
				CError tError = new CError();
				tError.moduleName = "AccountManage";
				tError.functionName = "getMultiAccInterest";
				tError.errorMessage = e.toString();
				this.mErrors.addOneError(tError);
			}
			ExeSQL tExeSQL = new ExeSQL();
			String tStr = tExeSQL.getOneValue(tSql);
			if (tStr == null || tStr.trim().equals("")) {
				tStr = "0";
			}
			tInterest = Double.parseDouble(tStr);
		} else {
			TableName = aLMRiskInsuAccSchema.getAccRateTable();
			tSql = "select * from " + TableName + " where InsuAccNo='"
					+ aInsuAccNo + "' and StartDate<='" + aBalanceDate
					+ "' and EndDate>='" + aOriginBalaDate
					+ "' order by EndDate";

			Statement stmt = null;
			ResultSet rs = null;
			double aValue = 0.0;
			double aResult = 0.0;
			String aDate = "";
			String tStartDate = "";

			DBOper db = new DBOper("");
			Connection con = db.getConnection();

			System.out.println(tSql.trim());
			try {
				stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				rs = stmt.executeQuery(StrTool.GBKToUnicode(tSql));
				int i = 0;
				while (rs.next()) {
					i++;
					aDate = rs.getString("EndDate");
					aValue = rs.getDouble("Rate");
					String aRateIntv = rs.getString("RateIntv");
					double tCalAccRate = TransAccRate(aValue, aRateIntv,
							aTransType, aIntervalType);

					if (i == 1) {
						tStartDate = aOriginBalaDate;
					}
					// 结息点超过某个利率临界点
					if (PubFun.calInterval(aDate, aBalanceDate, aIntervalType) > 0) {
						int tInterval = PubFun.calInterval(tStartDate, aDate,
								aIntervalType);
						if (tInterval > 0) {
							aResult = getIntvRate(tInterval, tCalAccRate,
									aTransType);
							tInterest += aBalance * aResult;
						}
						tStartDate = aDate;
					} else {
						int tInterval = PubFun.calInterval(tStartDate,
								aBalanceDate, aIntervalType);
						if (tInterval > 0) {
							aResult = getIntvRate(tInterval, tCalAccRate,
									aTransType);
							tInterest += aBalance * aResult;
						}
						break;
					}
				}
				rs.close();
				stmt.close();
				con.close();
			} catch (Exception e) {
				// @@错误处理
				CError tError = new CError();
				tError.moduleName = "AccountManage";
				tError.functionName = "getMultiAccInterest";
				tError.errorMessage = e.toString();
				this.mErrors.addOneError(tError);
				try {
					rs.close();
					stmt.close();
					con.close();
				} catch (Exception ex) {
				}
			}
		}
		return tInterest;
	}

	/**
	 * 得到借款描述分段结息的参数（推荐）
	 *
	 * @param aLOLoanSchema
	 *            LOLoanSchema
	 * @param aBalance
	 *            double
	 * @param aOriginBalaDate
	 *            String
	 * @param aBalanceDate
	 *            String
	 * @param aTransType
	 *            String 单利复利
	 * @param aIntervalType
	 *            String 转换成利率类型
	 * @return double
	 */
	private double getMultiAccInterest(LOLoanSchema aLOLoanSchema,
			double aBalance, String aOriginBalaDate, String aBalanceDate,
			String aTransType, String aIntervalType) {
		String tSql = "";
		double tInterest = 0.0;
		String[] ResultArray = new String[100];
		Calculator tCalculator = new Calculator();
		if (aLOLoanSchema.getRateCalType() != null
				&& aLOLoanSchema.getRateCalType().trim().equals("2")) {
			try {
				tSql = PubFun1.getSQL(aLOLoanSchema.getRateCalCode(),
						tCalculator);
			} catch (Exception e) {
				e.printStackTrace();
				// @@错误处理
				CError tError = new CError();
				tError.moduleName = "AccountManage";
				tError.functionName = "getMultiAccInterest";
				tError.errorMessage = e.toString();
				this.mErrors.addOneError(tError);
			}
			ExeSQL tExeSQL = new ExeSQL();
			String tStr = tExeSQL.getOneValue(tSql);
			if (tStr == null || tStr.trim().equals("")) {
				tStr = "0";
			}
			tInterest = Double.parseDouble(tStr);
		} else {
			String TableName = aLOLoanSchema.getRateCalCode();
			tSql = "select * from '" + TableName + "' where  and StartDate<='"
					+ aBalanceDate + "' and EndDate>='" + aOriginBalaDate
					+ "' order by EndDate";

			Statement stmt = null;
			ResultSet rs = null;
			double aValue = 0.0;
			double aResult = 0.0;
			String aDate = "";
			String tStartDate = "";

			DBOper db = new DBOper("");
			Connection con = db.getConnection();

			System.out.println(tSql.trim());
			try {
				stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				rs = stmt.executeQuery(StrTool.GBKToUnicode(tSql));
				int i = 0;
				while (rs.next()) {
					i++;
					aDate = rs.getString("EndDate");
					aValue = rs.getDouble("Rate");
					String aRateIntv = rs.getString("RateIntv");
					double tCalAccRate = TransAccRate(aValue, aRateIntv,
							aTransType, aIntervalType);

					if (i == 1) {
						tStartDate = aOriginBalaDate;
					}
					// 结息点超过某个利率临界点
					if (PubFun.calInterval(aDate, aBalanceDate, aIntervalType) > 0) {
						int tInterval = PubFun.calInterval(tStartDate, aDate,
								aIntervalType);
						if (tInterval > 0) {
							aResult = getIntvRate(tInterval, tCalAccRate,
									aTransType);
							tInterest += aBalance * aResult;
						}
						tStartDate = aDate;
					} else {
						int tInterval = PubFun.calInterval(tStartDate,
								aBalanceDate, aIntervalType);
						if (tInterval > 0) {
							aResult = getIntvRate(tInterval, tCalAccRate,
									aTransType);
							tInterest += aBalance * aResult;
						}
						break;
					}
				}
				rs.close();
				stmt.close();
				con.close();
			} catch (Exception e) {
				// @@错误处理
				CError tError = new CError();
				tError.moduleName = "AccountManage";
				tError.functionName = "getMultiAccInterest";
				tError.errorMessage = e.toString();
				this.mErrors.addOneError(tError);
				try {
					rs.close();
					stmt.close();
					con.close();
				} catch (Exception ex) {
				}
			}
		}
		return tInterest;
	}

	/**
	 * 按照利息类型,计算起始日期到截止日期之间的本金利息
	 *
	 * @param aBalance
	 *            计算本金
	 * @param startDate
	 *            起始日期
	 * @param endDate
	 *            截止日期
	 * @param interestType
	 *            利息类型 (具体的参见 --> ldintegrationrate.ratekind )
	 * @return 返回计算后的利息
	 */
	public double commonGetComplexInterest(double aBalance, String startDate,
			String endDate, String _ratekind) {

		double oldMoney = aBalance;
		double tInterest = 0.0;
		// 间隔年数
		int intvYears = PubFun.calInterval(startDate, endDate, "Y");
		// 按照年度累计利息
		// 如果借款期限小于 1年,按照单利计算，如果借款区间大于1年，满1年按照复利计算，剩余天数按照单利计算
		// 同时考虑在某个计息区间内存在多个率的变动，这时按照不同利率分段计算
		if (intvYears < 1) {
			tInterest = commonGetOneYearInterest(startDate, aBalance, endDate,
					"S", "D", _ratekind);
			aBalance += tInterest;
		} else if (intvYears > 0) {
			for (int j = 0; j < intvYears; j++) {
				String startDate_i = PubFun.getLastDate(startDate, j, "Y");
				String endDate_i = PubFun.getLastDate(startDate, j + 1, "Y");
				// endDate_i = PubFun.getLastDate(endDate_i,-1,"D");
				// 累计生息,即滚存
				tInterest = commonGetOneYearInterest(startDate_i, aBalance,
						endDate_i, "S", "D", _ratekind);
				aBalance += tInterest;
			}
			// 除去整年后的剩余天数
			int leaveDays = PubFun.calInterval(PubFun.getLastDate(startDate,
					intvYears, "Y"), endDate, "D");
			if (leaveDays > 0) {
				tInterest = commonGetOneYearInterest(PubFun.getLastDate(
						startDate, intvYears, "Y"), aBalance, endDate, "S",
						"D", _ratekind);
				aBalance += tInterest;
			}
		}

		// 最终的利息
		tInterest = aBalance - oldMoney;
		return tInterest;
	}

	/**
	 * 长城人寿非账户型利息计算方法 该方法按整年分段，分段后交给getOneYearInterest处理
	 *
	 * @param startDate
	 *            String 计息开始日期
	 * @param aBalance
	 *            double 本金
	 * @param endDate
	 *            String 止息日期
	 * @return double
	 * @author lanjun 2005/11/17
	 */
	public double getMultiLoanInterest(String startDate, double aBalance,
			String endDate)

	{
		double oldMoney = aBalance;
		double tInterest = 0.0;
		// 间隔年数
		int intvYears = PubFun.calInterval(startDate, endDate, "Y");
		// 按照年度累计利息
		// 如果借款期限小于 1年,按照单利计算，如果借款区间大于1年，满1年按照复利计算，剩余天数按照单利计算
		// 同时考虑在某个计息区间内存在多个率的变动，这时按照不同利率分段计算
		if (intvYears < 1) {
			tInterest = getOneYearInterest(startDate, aBalance, endDate, "S",
					"D");
			aBalance += tInterest;
		} else if (intvYears > 0) {
			for (int j = 0; j < intvYears; j++) {
				String startDate_i = PubFun.getLastDate(startDate, j, "Y");
				String endDate_i = PubFun.getLastDate(startDate, j + 1, "Y");
				// endDate_i = PubFun.getLastDate(endDate_i,-1,"D");
				// 累计生息,即滚存
				tInterest = getOneYearInterest(startDate_i, aBalance,
						endDate_i, "S", "D");
				aBalance += tInterest;
			}
			// 除去整年后的剩余天数
			int leaveDays = PubFun.calInterval(PubFun.getLastDate(startDate,
					intvYears, "Y"), endDate, "D");
			if (leaveDays > 0) {
				tInterest = getOneYearInterest(PubFun.getLastDate(startDate,
						intvYears, "Y"), aBalance, endDate, "S", "D");
				aBalance += tInterest;
			}
		}

		// 最终的利息
		tInterest = aBalance - oldMoney;
		return tInterest;
	}

	/**
	 * 根据不同的 (利率类型/起始日期/截止日期/本金/计算类型/计算间隔) 来计算相关的利息
	 *
	 * @param startDate
	 *            起始日期
	 * @param aBalance
	 *            本金
	 * @param endDate
	 *            截止日期
	 * @param aComputeType
	 *            计算类型
	 * @param aIntervalType
	 *            计算间隔
	 * @param ratekind
	 *            利率类型
	 * @return 返回一年的利息
	 */
	public double commonGetOneYearInterest(String startDate, double aBalance,
			String endDate, String aComputeType, String aIntervalType,
			String ratekind) {
		String tSql = "";
		double tInterest = 0.0;

		String TableName = "ldintegrationrate";
		tSql = "select enddate,rate,rateintv,rateType from " + TableName
				+ " where ratekind='" + ratekind + "'  and StartDate<='"
				+ endDate + "' and EndDate>='" + startDate
				+ "' order by EndDate";

		ExeSQL tExeSQL = new ExeSQL();
		SSRS tSSRS = tExeSQL.execSQL(tSql);
		if (tSSRS != null && tSSRS.getMaxRow() > 0) {
			int rows = tSSRS.getMaxRow();
			String rateStartDate = "";
			String rateEndDate = "";
			double aValue = 0.0;
			double aResult = 0.0;
			for (int i = 1; i <= rows; i++) {
				rateEndDate = tSSRS.GetText(i, 1);
				aValue = Double.parseDouble(tSSRS.GetText(i, 2));
				String aRateIntv = tSSRS.GetText(i, 3);
				String rateType = tSSRS.GetText(i, 4);

				double tCalAccRate = TransAccRate(aValue, aRateIntv,
						aComputeType, aIntervalType);

				if (i == 1) {
					rateStartDate = startDate;
				}
				// 如果某个结息点在本期结息区域内，首先计算前一段利息
				if (PubFun.calInterval(rateEndDate, endDate, aIntervalType) > 0) {
					int tInterval = PubFun.calInterval(rateStartDate,
							rateEndDate, aIntervalType);
					if (tInterval > 0) {
						// 将复利转换为单利计算
						tCalAccRate = TransAccRate(aValue, aRateIntv,
								aComputeType, aIntervalType);
						// 计算单利利息
						aResult = getIntvRate(tInterval, tCalAccRate,
								aComputeType);
						tInterest += aBalance * aResult;
					}
					rateStartDate = rateEndDate;
				} else {
					// 对于1年的单利和复利相等
					int tInterval = PubFun.calInterval(rateStartDate, endDate,
							aIntervalType);
					if (tInterval > 0) {
						// 如果间隔大于1年的天数，我们认为时间为1年
						// 在闰年时1年的复利不等于单利
						if (tInterval >= 365) {
							aResult = getIntvRate(1, aValue, "C");
						} else {
							aResult = getIntvRate(tInterval, tCalAccRate,
									aComputeType);
						}
						tInterest += aBalance * aResult;
					}
					break;
				}

			}
		}
		return tInterest;
	}

	/**
	 * 长城人寿非账户型利息计算方法 分段计息，分段不超过1年 该方法采用年复利计算方法，即整数年部分按复利计算，不够一年的按单利计算
	 *
	 * @param startDate
	 *            String 计息开始日期
	 * @param aBalance
	 *            double 本金
	 * @param endDate
	 *            String 止息日期
	 * @param aComputeType
	 *            String 单利复利
	 * @param aIntervalType
	 *            String 间隔类型，比如按年利率，月利率，日利率等
	 * @return double
	 * @author lanjun 2005/11/17
	 */
	public double getOneYearInterest(String startDate, double aBalance,
			String endDate, String aComputeType, String aIntervalType)

	{
		String tSql = "";
		double tInterest = 0.0;

		String TableName = "ldintegrationrate";
		tSql = "select enddate,rate,rateintv,rateType from " + TableName
				+ " where ratekind='loan'  and StartDate<='" + endDate
				+ "' and EndDate>='" + startDate + "' order by EndDate";
		ExeSQL tExeSQL = new ExeSQL();
		SSRS tSSRS = tExeSQL.execSQL(tSql);
		if (tSSRS != null && tSSRS.getMaxRow() > 0) {
			int rows = tSSRS.getMaxRow();
			String rateStartDate = "";
			String rateEndDate = "";
			double aValue = 0.0;
			double aResult = 0.0;
			for (int i = 1; i <= rows; i++) {
				rateEndDate = tSSRS.GetText(i, 1);
				aValue = Double.parseDouble(tSSRS.GetText(i, 2));
				String aRateIntv = tSSRS.GetText(i, 3);
				String rateType = tSSRS.GetText(i, 4);

				double tCalAccRate = TransAccRate(aValue, aRateIntv,
						aComputeType, aIntervalType);

				if (i == 1) {
					rateStartDate = startDate;
				}
				// 如果某个结息点在本期结息区域内，首先计算前一段利息
				if (PubFun.calInterval(rateEndDate, endDate, aIntervalType) > 0) {
					int tInterval = PubFun.calInterval(rateStartDate,
							rateEndDate, aIntervalType);
					if (tInterval > 0) {
						// 将复利转换为单利计算
						tCalAccRate = TransAccRate(aValue, aRateIntv,
								aComputeType, aIntervalType);
						// 计算单利利息
						aResult = getIntvRate(tInterval, tCalAccRate,
								aComputeType);
						tInterest += aBalance * aResult;
					}
					rateStartDate = rateEndDate;
				} else {
					// 对于1年的单利和复利相等
					int tInterval = PubFun.calInterval(rateStartDate, endDate,
							aIntervalType);
					if (tInterval > 0) {
						// 如果间隔大于1年的天数，我们认为时间为1年
						// 在闰年时1年的复利不等于单利
						if (tInterval >= 365) {
							aResult = getIntvRate(1, aValue, "C");
						} else {
							aResult = getIntvRate(tInterval, tCalAccRate,
									aComputeType);
						}
						tInterest += aBalance * aResult;
					}
					break;
				}

			}
		}
		return tInterest;
	}

	/**
	 * 得到按照银行利率结息分段结息的参数（推荐）
	 *
	 * @param aRateType
	 *            String
	 * @param aBalance
	 *            double 原余额
	 * @param aOriginBalaDate
	 *            String 利息起期
	 * @param aBalanceDate
	 *            String 利息止期
	 * @param aTransType
	 *            String 单利复利
	 * @param aIntervalType
	 *            String 转换成利率类型
	 * @return double
	 */
	public static double getMultiAccInterest(String aRateType, double aBalance,
			String aOriginBalaDate, String aBalanceDate, String aTransType,
			String aIntervalType) {
		String tSql = "";
		FDate tFDate = new FDate();
		String[] ResultArray = new String[100];
		// tSql = "select to_char(StartDate,'"+"yyyy-mm-dd"+"') as
		// StartDate,to_char(EndDate,'"+"yyyy-mm-dd"+"') as
		// EndDate,RateType,RateIntv,Rate from LDBankRate where
		// RateType='"+aRateType+"' and StartDate<='"+aBalanceDate+"' and
		// EndDate>='"+aOriginBalaDate+"' order by EndDate";
		tSql = "select * from LDBankRate where RateType='" + aRateType
				+ "' and StartDate<='" + aBalanceDate + "' and EndDate>='"
				+ aOriginBalaDate + "' order by EndDate";

		Statement stmt = null;
		ResultSet rs = null;
		double aValue = 0.0;
		double aResult = 0.0;
		String aDate = "";
		String tStartDate = "";

		double tInterest = 0.0;

		DBOper db = new DBOper("");
		Connection con = db.getConnection();

		System.out.println(tSql.trim());
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(StrTool.GBKToUnicode(tSql));
			int i = 0;
			while (rs.next()) {
				i++;
				aDate = tFDate.getString(rs.getDate("EndDate"));
				aValue = rs.getDouble("Rate");
				String aRateIntv = rs.getString("RateIntv");
				double tCalAccRate = TransAccRate(aValue, aRateIntv,
						aTransType, aIntervalType);
				if (i == 1) {
					tStartDate = aOriginBalaDate;
				}
				// 结息点超过某个利率临界点
				if (PubFun.calInterval(aDate, aBalanceDate, aIntervalType) > 0) {
					int tInterval = PubFun.calInterval(tStartDate, aDate,
							aIntervalType);
					if (tInterval > 0) {
						aResult = getIntvRate(tInterval, tCalAccRate,
								aTransType);
						tInterest += aBalance * aResult;
					}
					tStartDate = aDate;
				} else {
					int tInterval = PubFun.calInterval(tStartDate,
							aBalanceDate, aIntervalType);
					if (tInterval > 0) {
						aResult = getIntvRate(tInterval, tCalAccRate,
								aTransType);
						tInterest += aBalance * aResult;
					}
					break;
				}
			}
			rs.close();
			stmt.close();
			con.close();
		} catch (Exception e) {
			// @@错误处理
			CError tError = new CError();
			tError.moduleName = "AccountManage";
			tError.functionName = "getMultiAccInterest";
			tError.errorMessage = e.toString();
			try {
				rs.close();
				stmt.close();
				con.close();
			} catch (Exception ex) {
			}
		}

		return tInterest;
	}

	/**
	 * 帐户余额更新
	 *
	 * @return boolean
	 */
	public boolean updAccBalance() {

		Connection conn = null;
		conn = DBConnPool.getConnection();

		if (conn == null) {
			// @@错误处理
			CError tError = new CError();
			tError.moduleName = "AccountManage";
			tError.functionName = "saveData";
			tError.errorMessage = "数据库连接失败!";
			this.mErrors.addOneError(tError);
			return false;
		}

		try {
			conn.setAutoCommit(false);
			if (mLCInsureAccTraceSet.size() > 0) {
				LCInsureAccTraceDBSet tLCInsureAccTraceDBSet = new LCInsureAccTraceDBSet(
						conn);
				tLCInsureAccTraceDBSet.set(mLCInsureAccTraceSet);
				if (!tLCInsureAccTraceDBSet.update()) {
					// @@错误处理
					CError tError = new CError();
					tError.moduleName = "AccountManage";
					tError.functionName = "saveData";
					tError.errorMessage = "子帐户更新失败!";
					this.mErrors.addOneError(tError);
					conn.rollback();
					conn.close();
					return false;
				}
			}

			if (mLCInsureAccSet.size() > 0) {
				LCInsureAccDBSet tLCInsureAccDBSet = new LCInsureAccDBSet(conn);
				tLCInsureAccDBSet.set(mLCInsureAccSet);
				if (!tLCInsureAccDBSet.update()) {
					// @@错误处理
					CError tError = new CError();
					tError.moduleName = "AccountManage";
					tError.functionName = "saveData";
					tError.errorMessage = "帐户余额更新失败!";
					this.mErrors.addOneError(tError);
					conn.rollback();
					conn.close();
					return false;
				}
			}
			conn.commit();
			conn.close();
		} catch (Exception ex) {
			// @@错误处理
			CError tError = new CError();
			tError.moduleName = "AccountManage";
			tError.functionName = "updAccBalance";
			tError.errorMessage = ex.toString();
			this.mErrors.addOneError(tError);
			try {
				conn.rollback();
				conn.close();
			} catch (Exception e) {
			}
			return false;
		}
		return true;
	}

	/**
	 * 得到结息后的帐户轨迹
	 *
	 * @return LCInsureAccTraceSet
	 */
	public LCInsureAccTraceSet getInsureAccTrace() {
		return mLCInsureAccTraceSet;
	}

	/**
	 * 得到固定利率利息
	 *
	 * @param OriginBalance
	 *            double 原始金额
	 * @param aStartDate
	 *            String 起始日期
	 * @param aEndDate
	 *            String 终止日期
	 * @param OriginRate
	 *            double 原始利率
	 * @param OriginRateType
	 *            String 原始利率类型：年利率（"Y")，月利率("M")，日利率("D")
	 * @param TransType
	 *            String 复利转换("C")compound，单利转换("S")simple
	 * @param DestRateType
	 *            String 年利率，月利率,日利率
	 * @return double
	 */
	public double getInterest(double OriginBalance, String aStartDate,
			String aEndDate, double OriginRate, String OriginRateType,
			String TransType, String DestRateType) {
		double aInterest = 0.0;
		double tCalRate = 0.0;
		tCalRate = TransAccRate(OriginRate, OriginRateType, TransType,
				DestRateType);
		int tInterval = PubFun.calInterval(aStartDate, aEndDate, DestRateType);
		aInterest = OriginBalance * getIntvRate(tInterval, tCalRate, TransType);
		return aInterest;
	}

	/**
	 * 取得利息
	 *
	 * @param aCalType
	 *            String
	 * @param aVData
	 *            VData
	 * @param aBalance
	 *            double
	 * @param aStartDate
	 *            String
	 * @param aEndDate
	 *            String
	 * @return double
	 */
	public double getMultiInterest(String aCalType, VData aVData,
			double aBalance, String aStartDate, String aEndDate) {
		double aInterest = 0.0;
		if (aCalType.equals("0")) {
		} else if (aCalType.equals("1")) {
			LOLoanSchema tLOLoanSchema = new LOLoanSchema();
			tLOLoanSchema = (LOLoanSchema) aVData.getObjectByObjectName(
					"LOLoanSchema", 0);
			if (tLOLoanSchema.getSpecifyRate().equals("1")) {
				aInterest = getInterest(aBalance, aStartDate, aEndDate,
						tLOLoanSchema.getInterestRate(), tLOLoanSchema
								.getInterestMode(), tLOLoanSchema
								.getInterestType(), "D");
			} else if (tLOLoanSchema.getSpecifyRate().equals("2")) {
				aInterest = getMultiAccInterest(tLOLoanSchema, aBalance,
						aStartDate, aEndDate, tLOLoanSchema.getInterestType(),
						"D");
			} else {
				System.out.println("-----no this type----");
			}
		} else {

		}
		return aInterest;
	}

	/**
	 * 得到计算时间间隔的比率
	 *
	 * @param aInterval
	 *            int
	 * @param aRate
	 *            double
	 * @param aTransType
	 *            String
	 * @return double
	 */
	private static double getIntvRate(int aInterval, double aRate,
			String aTransType) {
		double aIntvRate = 0.0;
		// Add by Minim for RF of BQ
		if (aTransType.equals("1")) {
			aTransType = "S";
		}
		if (aTransType.equals("2")) {
			aTransType = "C";
		}
		// End add by Minim;

		if (aTransType.equals("S")) {
			aIntvRate = aRate * aInterval;
		} else if (aTransType.equals("C")) {
			aIntvRate = java.lang.Math.pow(1 + aRate, aInterval) - 1;
		} else {

		}
		return aIntvRate;
	}

	// ---------------------------------------2007-04-10 增加泰康的结息方法
	// 用于万能险的结息-------------------------------//
	/**
	 * 按照新的帐户结息的逻辑进行处理 计算帐户分类表的利息
	 *
	 * @param aBalaDate
	 *            本次结息日期
	 * @param aRateType
	 *            原始利率类型，比如年利率，月利率等
	 * @param aIntvType
	 *            目标利率类型，比如年利率，月利率等
	 * @param Period
	 *            利率期间
	 * @param tPeriodFlag
	 *            利率类型（活期or定期）
	 * @param Depst
	 *            贷存款标志 创建人: 创建日期：2006-02-16
	 * @return double 返回的是帐户分类表和相应的记价履历表的利息和以及总余额 修改人： 修改日期：2006-02-24
	 *         修改内容：传输参数修改
	 */
	public TransferData getAccClassInterestNew(
			LCInsureAccClassSchema aLCInsureAccClassSchema, String aBalaDate,
			String aRateType, String aIntvType, int Period, String tType,
			String Depst) {

		System.out.println("=====This is getAccClassInterestNew!=====\n");

		CachedRiskInfo mCRI = CachedRiskInfo.getInstance();
		// 记录帐户分类表的利息值
		double aAccClassInterest = 0.0;

		// 记录帐户分类表的本息和
		double aAccClassSumPay = 0.0;

		// 记录返回值利息和本息
		TransferData aAccClassRet = new TransferData();

		// 检验数据有效性
		if (!verifyNotNull("当前结息日期", aBalaDate)) {
			returnNull(aAccClassRet);
			return aAccClassRet;
		}
		if (!verifyNotNull("原始利率类型", aRateType)) {
			returnNull(aAccClassRet);
			return aAccClassRet;
		}
		if (!verifyNotNull("目标利率类型", aIntvType)) {
			returnNull(aAccClassRet);
			return aAccClassRet;
		}

		// 记录结息间隔
		int tInterval = 0;

		// 记录查询LCInsureAccClassTrace表返回有效记录的个数
		int tCount = 0;

		// 记录原始利率值
		double tAccClassRate;

		// 记录目标利率值
		double tCalAccClassRate;

		// 记录保险帐户现金余额
		double tInsuAccClassBala = aLCInsureAccClassSchema.getInsuAccBala(); // 记录保险帐户现金余额

		// 得到险种保险帐户描述表(lmRiskInsuAcc)中帐户利率
		// LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();

		// 记录保险帐户号码
		String aInsuAccNo = aLCInsureAccClassSchema.getInsuAccNo();
		if (!verifyNotNull("保险帐户号码", aInsuAccNo)) {
			returnNull(aAccClassRet);
			return aAccClassRet;
		}

		// 记录帐户分类表上次结息日期
		String tBalaDate = aLCInsureAccClassSchema.getBalaDate();

		// 记录帐户分类表上次结息时间
		String tBalaTime = aLCInsureAccClassSchema.getBalaTime();

		// 上次结息日期为空或者不存在则取入机日期
		if (tBalaDate == null || tBalaDate.equals("")) {
			tBalaDate = aLCInsureAccClassSchema.getMakeDate();
		}

		// 上次结息时间如果为空或者不存在则取入机时间
		if (tBalaTime == null || tBalaTime.equals("")) {
			tBalaTime = aLCInsureAccClassSchema.getMakeTime();
		}
		// tLMRiskInsuAccDB.setInsuAccNo(aInsuAccNo);
		LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
		LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet();

		LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
		tLMRiskInsuAccSchema = mCRI.findRiskInsuAccByInsuAccNo(aInsuAccNo);
		double tInsureAccTraceMoneySum = 0.0;
		String[] ResultRate = null;
		if (tLMRiskInsuAccSchema.getInsuAccNo() == null
				|| tLMRiskInsuAccSchema.getInsuAccNo().equals("")) {
			aAccClassRet
					.setNameAndValue("tAccClassInterest", aAccClassInterest);
			aAccClassRet.setNameAndValue("tAccClassSumPay", aAccClassSumPay);

			return aAccClassRet;
		}
		ExeSQL tExeSql = new ExeSQL();
		SSRS tSSRS = new SSRS();
		String tSql;
		// -----testing start----20060216-----------------------
		System.out.println("************开始结息****************");
		System.out.println("保险帐户现金余额: " + tInsuAccClassBala);
		System.out.println("本次结息日期: " + aBalaDate);
		System.out.println("上次结息日期: " + tBalaDate);
		System.out.println("上次结息时间: " + tBalaTime);
		// ----------testing end--------------------------------

		switch (Integer.parseInt(tLMRiskInsuAccSchema.getAccComputeFlag())) {

		// 不计息
		case 0:
			break;

		// 定期利率单利算法
		case 1:

			// 获取帐户固定利率AccRate
			tAccClassRate = tLMRiskInsuAccSchema.getAccRate();

			// 将原始固定利率转换为目标固定利率
			tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "S",
					aIntvType);

			String tAccKind = tLMRiskInsuAccSchema.getAccKind();
			// 先进先出险种特殊处理
			String tOtherNo = aLCInsureAccClassSchema.getOtherNo();
			if (!tAccKind.equals("") && tAccKind.equals("3")) {
				if (tOtherNo.equals("")) {
					CError.buildErr(this, "保险帐户分类表数据不完整！");
					returnNull(aAccClassRet);
					return aAccClassRet;
				}
				// tLCInsureAccTraceDB.setPayNo(tOtherNo);
			}
			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassSchema.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassSchema
					.getPayPlanCode());
			tLCInsureAccTraceDB.setAccAscription(aLCInsureAccClassSchema
					.getAccAscription());
			try {
				tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
			} catch (Exception ex) {
				CError.buildErr(this, ex.toString());
				returnNull(aAccClassRet);
				return aAccClassRet;
			}
			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = (LCInsureAccTraceDB) tLCInsureAccTraceSet
						.get(i);

				// 如果LCInsureAccTrace表中的moneyType字段的值为PX或者SX,则不作结息;
				String tMoneyType = tmpLCInsuAccTraceDB.getMoneyType();
				if (tMoneyType.equals("PX") || tMoneyType.equals("SX")) {
					continue;
				}

				// 记录缴费日期
				String payDate = tmpLCInsuAccTraceDB.getPayDate();

				// 交费生效时间
				String tMakeTime = tmpLCInsuAccTraceDB.getMakeTime();

				// 现在比较lcinsureaccclass表的baladate和lcinsuretrace表的paydate的值的大小
				int tIntv = PubFun.calInterval(tBalaDate, payDate, aIntvType);
				if (tIntv >= 0) {

					// 处理轨迹表中paydate >= 帐户分类表的结息日期BalaDate
					// 即处理自上次结息以来的交费总额的利息
					if (tIntv == 0 && tBalaTime.compareTo(tMakeTime) >= 0) {
						continue;
					}

					// 暂存一笔缴费
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;

					// 然后对轨迹表的这笔钱tempMoney作结息
					if (payDate.compareTo(aBalaDate) > 0) {
						tInterval = PubFun.calInterval(aBalaDate, payDate,
								aIntvType);
						tInterval = 0 - tInterval;
					} else {
						tInterval = PubFun.calInterval(payDate, aBalaDate,
								aIntvType);
					}

					// jixf chg 20060722不区分时间间隔，可以往回反结息
					aAccClassInterest += tempMoney
							* getIntvRate(tInterval, tCalAccClassRate, "S");
					aAccClassInterest = Arith.round(aAccClassInterest, 2);
				}
			}

			// 将帐户分类表的另一部分余额结息
			tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			if (tInterval > 0)
				aAccClassInterest += tInsuAccClassBala
						* getIntvRate(tInterval, tCalAccClassRate, "S");
			aAccClassInterest = Arith.round(aAccClassInterest, 2);
			break;

		// 定期利率复利算法
		case 2:

			// 获取固定利率
			tAccClassRate = tLMRiskInsuAccSchema.getAccRate();

			// 转换后的利率值
			tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "C",
					aIntvType);

			tAccKind = tLMRiskInsuAccSchema.getAccKind();

			// 分红险特殊处理
			tOtherNo = aLCInsureAccClassSchema.getOtherNo();
			if (!tAccKind.equals("") && tAccKind.equals("3")) {
				if (tOtherNo.equals("")) {
					CError.buildErr(this, "保险帐户分类表数据不完整！");
					returnNull(aAccClassRet);
					return aAccClassRet;
				}
				// tLCInsureAccTraceDB.setPayNo(tOtherNo);
			}
			tSql = "select * from LCInsureAccTrace where polno='"
					+ aLCInsureAccClassSchema.getPolNo() + "' and InsuAccNo='"
					+ aInsuAccNo + "' and PayPlanCode='"
					+ aLCInsureAccClassSchema.getPayPlanCode()
					+ "' and PayNo='" + tOtherNo + "' and (PayDate>'"
					+ tBalaDate + "' or (PayDate='" + tBalaDate
					+ "' and MakeTime>'" + tBalaTime + "')) and PayDate<='"
					+ aBalaDate + "'";

			tLCInsureAccTraceSet = tLCInsureAccTraceDB.executeQuery(tSql);

			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = new LCInsureAccTraceDB();
				tmpLCInsuAccTraceDB.setSchema(tLCInsureAccTraceSet.get(i));

				// 如果LCInsureAccTrace表中的moneyType字段的值为PX或者SX,则不作结息;
				String tMoneyType = tmpLCInsuAccTraceDB.getMoneyType();
				if (tMoneyType.equals("PX") || tMoneyType.equals("SX")) {
					continue;
				}

				// 交费日期
				String payDate = tmpLCInsuAccTraceDB.getPayDate();

				// 交费生效时间
				String tMakeTime = tmpLCInsuAccTraceDB.getMakeTime();

				double tempMoney = tmpLCInsuAccTraceDB.getMoney();
				tInsureAccTraceMoneySum += tempMoney;

				// 然后对轨迹表的这笔钱tempMoney作结息
				// jixf add 20050802 对于PayDate>aBalaDate的情况，tInterval有问题，反之没有问题
				System.out.println("------" + payDate);
				System.out.println("------" + aBalaDate);
				System.out.println("-%%%%%%%%%--===="
						+ payDate.compareTo(aBalaDate));
				if (payDate.compareTo(aBalaDate) > 0) {
					System.out.println("33333");
					tInterval = PubFun.calInterval(aBalaDate, payDate,
							aIntvType);
					tInterval = 0 - tInterval;
				} else {

					tInterval = PubFun.calInterval(payDate, aBalaDate,
							aIntvType);
				}
				// jixf chg 20060722不区分时间间隔，可以往回反结息
				aAccClassInterest += tempMoney
						* getIntvRate(tInterval, tCalAccClassRate, "C");
				System.out.println("=======" + tInterval);
				System.out.println("=======" + tInsuAccClassBala);
				System.out.println("======="
						+ getIntvRate(tInterval, tCalAccClassRate, "C"));
				aAccClassInterest = Arith.round(aAccClassInterest, 2);

				// }
			}

			// 将帐户分类表的另一部分余额结息
			tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			if (tInterval > 0)
				aAccClassInterest += tInsuAccClassBala
						* getIntvRate(tInterval, tCalAccClassRate, "C");
			System.out.println("=======" + tInterval);
			System.out.println("=======" + tInsuAccClassBala);
			System.out.println("======="
					+ getIntvRate(tInterval, tCalAccClassRate, "C"));
			aAccClassInterest = Arith.round(aAccClassInterest, 2);
			break;

		// 活期利率单利算法
		case 3:

			tAccKind = tLMRiskInsuAccSchema.getAccKind();
			// 分红险特殊处理
			tOtherNo = aLCInsureAccClassSchema.getOtherNo();
			if (!tAccKind.equals("") && tAccKind.equals("3")) {
				if (tOtherNo.equals("")) {
					CError.buildErr(this, "保险帐户分类表数据不完整！");
					returnNull(aAccClassRet);
					return aAccClassRet;
				}
				// tLCInsureAccTraceDB.setPayNo(tOtherNo);
			}
			tSql = "select * from LCInsureAccTrace where polno='"
					+ aLCInsureAccClassSchema.getPolNo() + "' and InsuAccNo='"
					+ aInsuAccNo + "' and PayPlanCode='"
					+ aLCInsureAccClassSchema.getPayPlanCode()
					+ "' and AccAscription='"
					+ aLCInsureAccClassSchema.getAccAscription()
					+ "' and (PayDate>'" + tBalaDate + "' or (PayDate='"
					+ tBalaDate + "' and MakeTime>'" + tBalaTime
					+ "')) and PayDate<='" + aBalaDate + "'";
			System.out.println("------" + tSql);

			tLCInsureAccTraceSet = tLCInsureAccTraceDB.executeQuery(tSql);

			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = new LCInsureAccTraceDB();
				tmpLCInsuAccTraceDB.setSchema(tLCInsureAccTraceSet.get(i));

				// 如果LCInsureAccTrace表中的moneyType字段的值为PX或者SX,则不作结息;
				String tMoneyType = tmpLCInsuAccTraceDB.getMoneyType();
				if (tMoneyType.equals("PX") || tMoneyType.equals("SX")) {
					continue;
				}

				// 记录交费日期
				String payDate = tmpLCInsuAccTraceDB.getPayDate();

				// 记录交费生效时间
				String tMakeTime = tmpLCInsuAccTraceDB.getMakeTime();

				// 现在比较lcinsureaccclass表的baladate和lcinsuretrace表的paydate的值的大小
				int tIntv = PubFun.calInterval(tBalaDate, payDate, aIntvType);
				if (tIntv >= 0) {

					// 处理轨迹表中paydate>=帐户分类表的结息日期BalaDate
					if (tIntv == 0 && tBalaTime.compareTo(tMakeTime) >= 0) {
						continue;
					}

					// 临时存放LCInsuAccTraceDB表中的帐户缴费
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;

					// 对轨迹表的这笔钱tempMoney作结息
					String[] ResultRate2 = getMultiAccRateNew(aInsuAccNo,
							tLMRiskInsuAccSchema, payDate, aBalaDate,
							aRateType, "S", aIntvType, Period, tType, Depst);

					// 记录ResultRate2[]的有效数组长度
					tCount = Integer.parseInt(ResultRate2[0]);
					for (int m = 1; m < tCount + 1; m++) {
						if (ResultRate2[m] == null) {
							ResultRate2[m] = "0";
						}
						tCalAccClassRate = Double.parseDouble(ResultRate2[m]);
						double tSubInterest = tempMoney * tCalAccClassRate;
						tSubInterest = Arith.round(tSubInterest, 2);
						aAccClassInterest += tSubInterest;

					}
				}
			}

			// 将帐户分类表的另一部分余额结息
			ResultRate = getMultiAccRateNew(aInsuAccNo, tLMRiskInsuAccSchema,
					tBalaDate, aBalaDate, aRateType, "S", aIntvType, Period,
					tType, Depst);
			tCount = Integer.parseInt(ResultRate[0]);
			for (int m = 1; m < tCount + 1; m++) {
				if (ResultRate[m] == null) {
					ResultRate[m] = "0";
				}
				tCalAccClassRate = Double.parseDouble(ResultRate[m]);
				aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
				aAccClassInterest = Arith.round(aAccClassInterest, 2);
			}
			break;

		// 活期利率复利算法
		case 4:
			System.out.println("=====复利结息开始=====\n");

			tAccKind = tLMRiskInsuAccSchema.getAccKind();

			// 分红险特殊处理
			tOtherNo = aLCInsureAccClassSchema.getOtherNo();
			if (!tAccKind.equals("") && tAccKind.equals("3")) {
				if (tOtherNo.equals("")) {
					CError.buildErr(this, "保险帐户分类表数据不完整！");
					returnNull(aAccClassRet);
					return aAccClassRet;
				}
				// tLCInsureAccTraceDB.setPayNo(tOtherNo);
			}
			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassSchema.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);

			// 缴费来源(企业缴还是个人缴)
			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassSchema
					.getPayPlanCode());
			tLCInsureAccTraceDB.setAccAscription(aLCInsureAccClassSchema
					.getAccAscription());
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();

			// ===20060703===testing start======================
			System.out
					.println("一共查询到trace表的记录数：" + tLCInsureAccTraceSet.size());
			// ===20060703===testing end=========================

			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = new LCInsureAccTraceDB();
				tmpLCInsuAccTraceDB.setSchema(tLCInsureAccTraceSet.get(i));

				// 如果LCInsureAccTrace表中的moneyType字段的值为PX或者SX,则不作结息;
				String tMoneyType = tmpLCInsuAccTraceDB.getMoneyType();
				if (tMoneyType.equals("PX") || tMoneyType.equals("SX")) {
					continue;
				}
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				if (payDate == null || payDate.equals("")) {
					payDate = tmpLCInsuAccTraceDB.getMakeDate();
					if (payDate == null || payDate.equals("")) {
						CError.buildErr(this, "缴费记录的缴费日期为空！");
						returnNull(aAccClassRet);
						return aAccClassRet;
					}
				}

				// 交费生效时间
				String tMakeTime = tmpLCInsuAccTraceDB.getMakeTime();

				// ==20060619===testing start===============
				System.out.println("第" + i + "条记录！");
				System.out.println("缴费日期：" + payDate);
				System.out.println("缴费时间：" + tMakeTime);
				System.out.println("缴费金额：" + tmpLCInsuAccTraceDB.getMoney());
				// ==20060619==testind end===================

				// 现在比较lcinsureaccclass表的baladate和lcinsuretrace表的paydate的值的大小
				int tIntv = PubFun.calInterval(tBalaDate, payDate, aIntvType);
				if (tIntv >= 0) {

					// 处理轨迹表中paydate>=帐户分类表的结息日期BalaDate
					if (tIntv == 0 && tBalaTime.compareTo(tMakeTime) >= 0) {
						continue;
					}

					// 临时存放帐户一笔缴费金额
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;

					// 然后对轨迹表的这笔钱tempMoney作结息
					String[] ResultRate2 = getMultiAccRateNew(aInsuAccNo,
							tLMRiskInsuAccSchema, payDate, aBalaDate,
							aRateType, "C", aIntvType, Period, tType, Depst);
					tCount = Integer.parseInt(ResultRate2[0]);

					// ====20060619==testing start=============
					System.out.println("查到" + tCount + "条利率值");
					// ===20060619==testing end================
					for (int m = 1; m < tCount + 1; m++) {
						if (ResultRate2[m] == null) {
							ResultRate2[m] = "0";
						}
						tCalAccClassRate = Double.parseDouble(ResultRate2[m]);

						// ===20060619==testing start===============
						System.out.println("Rate = " + tCalAccClassRate);
						// ===2060619===testing end=================
						double tSubInterest = tempMoney * tCalAccClassRate;
						tSubInterest = Arith.round(tSubInterest, 2);
						aAccClassInterest += tSubInterest;
					}
				}
			}

			// 将帐户分类表的另一部分余额结息
			System.out.println("=====将帐户分类表的另一部分余额结息=====\n");
			ResultRate = getMultiAccRateNew(aInsuAccNo, tLMRiskInsuAccSchema,
					tBalaDate, aBalaDate, aRateType, "C", aIntvType, Period,
					tType, Depst);
			tCount = Integer.parseInt(ResultRate[0]);
			for (int m = 1; m < tCount + 1; m++) {
				if (ResultRate[m] == null) {
					ResultRate[m] = "0";
				}
				tCalAccClassRate = Double.parseDouble(ResultRate[m]);
				aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
				aAccClassInterest = Arith.round(aAccClassInterest, 2);
				// ===20060703===testing start========================
				System.out.println("计算后的利率：" + tCalAccClassRate);
				// ===20060703==testing end===========================

			}
			break;
		}
		aAccClassSumPay = tInsuAccClassBala + tInsureAccTraceMoneySum
				+ aAccClassInterest;
		// 准备返回的数据包
		aAccClassRet.setNameAndValue("aAccClassInterest", aAccClassInterest);
		aAccClassRet.setNameAndValue("aAccClassSumPay", aAccClassSumPay);

		return aAccClassRet;
	}

	/**
	 * 不能为空校验
	 */
	private boolean verifyNotNull(String tVName, String tStrValue) {
		if (tStrValue == null || tStrValue.equals("")) {
			// @@错误处理
			CError tError = new CError();
			tError.moduleName = "AccountManage";
			tError.functionName = "getAccClassInterestNew";
			tError.errorMessage = "进行结息计算时，" + tVName + "没有传入！";
			this.mErrors.addOneError(tError);
		}

		return true;
	}

	/**
	 * 结息不成功，返回零 被getAccClassInterestNew在传入参数无效时调用
	 */
	private void returnNull(TransferData aAccClassRet) {
		aAccClassRet.setNameAndValue("tAccClassInterest", 0.0);
		aAccClassRet.setNameAndValue("tAccClassSumPay", 0.0);
	}

	/**
	 * * 得到分段结息的参数（推荐）
	 *
	 * @param aInsuAccNo
	 *            保单帐户号
	 * @param aLMRiskInsuAccSchema
	 *            险种保险帐户记录
	 * @param aOriginBalaDate
	 *            结息起始日期
	 * @param aBalanceDate
	 *            结息结束日期
	 * @param aRateType
	 *            原描述利率的类型
	 * @param aTransType
	 *            单利/复利
	 * @param aIntervalType
	 *            转换成利率类型
	 * @param aType
	 *            定期/活期
	 * @return
	 */
	private String[] getMultiAccRateNew(String aInsuAccNo,
			LMRiskInsuAccSchema aLMRiskInsuAccSchema, String aOriginBalaDate,
			String aBalanceDate, String aRateType, String aTransType,
			String aIntervalType, int aPeriod, String aType, String aDepst) {
		System.out
				.println("=====This is AccoutManage->getMultiAccRateNew=====\n");
		String tSql = "";
		String[] ResultArray = new String[100];
		Calculator tCalculator = new Calculator();
		String TableName = aLMRiskInsuAccSchema.getAccRateTable();
		if (aLMRiskInsuAccSchema.getAccCancelCode() != null
				&& !aLMRiskInsuAccSchema.getAccCancelCode().equals("")) {
			try {
				tSql = PubFun1.getSQL(aLMRiskInsuAccSchema.getAccCancelCode(),
						tCalculator);
			} catch (Exception e) {
				e.printStackTrace();

				// @@错误处理
				CError tError = new CError();
				tError.moduleName = "AccountManage";
				tError.functionName = "getMultiAccInterest";
				tError.errorMessage = e.toString();
				this.mErrors.addOneError(tError);
			}
		} else {
			TableName = aLMRiskInsuAccSchema.getAccRateTable();
			if (TableName == null || TableName == "null"
					|| TableName.toUpperCase().equals("LDBANKRATE")) {
				tSql = "select * from LDBankRate where "
						+ " DeclareDate <= to_date('" + aBalanceDate
						+ "', 'yyyy-mm-dd') and EndDate >= to_date('"
						+ aOriginBalaDate + "', 'yyyy-mm-dd') and Period = "
						+ aPeriod + "";
				if (aRateType != null) {
					tSql = tSql + " and PeriodFlag = '" + aRateType + "'";
				}
				if (aType != null) {
					tSql = tSql + " and Type = '" + aType + "'";
				}
				if (aDepst != null) {
					tSql = tSql + " and Depst_Loan = '" + aDepst + "'";
				}
				tSql = tSql + " order by EndDate";

				// 取银行获取利率
				ResultArray = getBankRate(tSql, aRateType, aTransType,
						aIntervalType, aOriginBalaDate, aBalanceDate);
			} else {
				tSql = "select Rate, EndDate from " + TableName
						+ " where InsuAccNo = '" + aInsuAccNo
						+ "' and StartDate <= to_date('" + aBalanceDate
						+ "', 'yyyy-mm-dd') and EndDate >= to_date('"
						+ aOriginBalaDate + "', 'yyyy-mm-dd')";
				if (aRateType != null) {
					tSql = tSql + " and RateIntv = '" + aRateType + "'";
				}
				tSql = tSql + " order by EndDate";

				// 取定期利率
				ResultArray = getTablRate(tSql, aRateType, TableName,
						aTransType, aIntervalType, aOriginBalaDate,
						aBalanceDate);
			}
		}

		return ResultArray;
	}

	/**
	 * * 得到帐户险种结息时的利率
	 *
	 * @param tSql
	 *            查询利率的SQL语句
	 * @param aOriginBalaDate
	 *            结息起始日期
	 * @param aBalanceDate
	 *            结息结束日期
	 * @param aRateType
	 *            原描述利率的类型（年/月/日）
	 * @param aTransType
	 *            单利/复利
	 * @param aIntervalType
	 *            转换成利率类型
	 * @return
	 */
	private String[] getBankRate(String tSql, String aRateType,
			String aTransType, String aIntervalType, String aOriginBalaDate,
			String aBalanceDate) {

		String aDate = "";
		double aValue = 0.0;
		double aResult = 0.0;
		String tStartDate = "";
		String[] ResultArray = new String[100];
		System.out.println("EXETSql: " + tSql);
		LDBankRateDB tLDBankRateDB = new LDBankRateDB();
		LDBankRateSet tLDBankRateSet = new LDBankRateSet();
		tLDBankRateSet = tLDBankRateDB.executeQuery(tSql);
		for (int i = 1; i <= tLDBankRateSet.size(); i++) {
			LDBankRateSchema tLDBankRateSchema = new LDBankRateSchema();
			tLDBankRateSchema = tLDBankRateSet.get(i);
			aDate = tLDBankRateSchema.getEndDate();
			aValue = tLDBankRateSchema.getRate();
			// String aRateIntv = rs.getString("RateIntv");

			// ===20060703===testing start=================
			System.out.println("直接从表中查出的利率为" + aValue);
			// ===20060703===testing end====================

			// 原始利率转换为目标利率
			double tCalAccRate = TransAccRate(aValue, aRateType, aTransType,
					aIntervalType);
			if (i == 1) {
				tStartDate = aOriginBalaDate;
			}

			// 结息点超过某个利率临界点
			if (PubFun.calInterval(aDate, aBalanceDate, aIntervalType) > 0) {
				int tInterval = PubFun.calInterval(tStartDate, aDate,
						aIntervalType);
				if (tInterval > 0) {

					// 计算一定时间间隔下的利率
					aResult = getIntvRate(tInterval, tCalAccRate, aTransType);
					ResultArray[i] = String.valueOf(aResult);

					// ===20060703===testing start===============
					System.out.println("折算后的利率为：" + ResultArray[i]);
					// ===20060703===testing end=================

				}
				tStartDate = aDate;
			} else {
				int tInterval = PubFun.calInterval(tStartDate, aBalanceDate,
						aIntervalType);
				if (tInterval > 0) {

					// 计算一定时间间隔下的利率
					aResult = getIntvRate(tInterval, tCalAccRate, aTransType);
					ResultArray[i] = String.valueOf(aResult);

					// ===20060703===testing start===============
					System.out.println("折算后的利率为：" + ResultArray[i]);
					// ===20060703===testing end=================

				}
				break;
			}
		}
		ResultArray[0] = String.valueOf(tLDBankRateSet.size());

		return ResultArray;
	}

	/**
	 *
	 */
	private String[] getTablRate(String tSql, String aRateType,
			String tTabName, String aTransType, String aIntervalType,
			String aOriginBalaDate, String aBalanceDate) {

		System.out.println("=====This is AccoutManage->getTableRate=====\n");
		String aDate = "";
		double aValue = 0.0;
		double aResult = 0.0;
		String tStartDate = "";
		SSRS tSSRS = new SSRS();
		String[] ResultArray = new String[100];
		ExeSQL tExeSQL = new ExeSQL();
		tSSRS = tExeSQL.execSQL(tSql);
		if (tSSRS == null || tSSRS.MaxRow <= 0) {
			System.out.println(tTabName + "表查询利率为空！");
			return null;
		}
		String[][] tResultArray = tSSRS.getAllData();
		int MaxRow = tSSRS.getMaxRow();
		for (int i = 1; i <= MaxRow; i++) {

			aValue = Double.parseDouble(tSSRS.GetText(i, 1));
			aDate = String.valueOf(tSSRS.GetText(i, 2));
			double tCalAccRate = TransAccRate(aValue, aRateType, aTransType,
					aIntervalType);
			if (i == 1) {
				tStartDate = aOriginBalaDate;
			}

			// 结息点超过某个利率临界点
			if (PubFun.calInterval(aDate, aBalanceDate, aIntervalType) > 0) {
				int tInterval = PubFun.calInterval(tStartDate, aDate,
						aIntervalType);
				if (tInterval > 0) {
					aResult = getIntvRate(tInterval, tCalAccRate, aTransType);
					ResultArray[i] = String.valueOf(aResult);
				}
				tStartDate = aDate;
			} else {
				int tInterval = PubFun.calInterval(tStartDate, aBalanceDate,
						aIntervalType);
				if (tInterval > 0) {
					aResult = getIntvRate(tInterval, tCalAccRate, aTransType);
					ResultArray[i] = String.valueOf(aResult);
				}
				break;
			}
		}
		ResultArray[0] = String.valueOf(MaxRow);

		return ResultArray;
	}

	// ---------------------------------------2007-04-10 增加泰康的结息方法
	// 用于万能险的结息-end------------------------------//

	public static void main(String args[]) {
		AccountManage aAccountManage = new AccountManage();
		// System.out.println(aAccountManage.getAccInterest("000006", 28500,
		// "2005-08-01"
		// , "2005-08-15", "D"));

//		LCInsureAccClassDB tLCInsureAccClassDB = new LCInsureAccClassDB();
//		tLCInsureAccClassDB.setPolNo("9021000000149988");
//		// tLCInsureAccClassDB.setPolNo("9021000000102988");
//		tLCInsureAccClassDB.setInsuAccNo("000006");
//		tLCInsureAccClassDB.setPayPlanCode("806101");
//		tLCInsureAccClassDB.setOtherNo("9021000000149988");
//		// tLCInsureAccClassDB.setOtherNo("9021000000102988");
//		tLCInsureAccClassDB.setAccAscription("1");
//
//		tLCInsureAccClassDB.setBalaDate("2003-10-31");
//		tLCInsureAccClassDB.setInsuAccBala(95446.7);
//
//		System.out.println(aAccountManage.getAccClassInterest(
//				tLCInsureAccClassDB, "2003-11-26", "D", "D"));

//                System.out.println(aAccountManage.queryRateFromInterest000001(
//				"interest000001", "000001", "C", "Y","2008-01-02"));

		// AccountManage aAccountManage = new AccountManage();
		// LCInsureAccSchema aLCInsureAccSchema = new LCInsureAccSchema();
		// aLCInsureAccSchema.setPolNo("86110020040210000722");
		// aLCInsureAccSchema.setInsuAccNo("000006");
		// aLCInsureAccSchema.setRiskCode("211701");
		// aLCInsureAccSchema.setAccType("003");
		// aLCInsureAccSchema.setOtherNo("86110020040210000722");
		// aLCInsureAccSchema.setOtherType("1");
		// aLCInsureAccSchema.setContNo("00000000000000000000");
		// aLCInsureAccSchema.setGrpPolNo("86110020040220000042");
		// aLCInsureAccSchema.setInsuredNo("0000001881");
		// aLCInsureAccSchema.setAppntName("北京捷通投资咨询管理有限公司");
		// aLCInsureAccSchema.setSumPay("93000");
		// aLCInsureAccSchema.setInsuAccBala("111800");
		// aLCInsureAccSchema.setUnitCount("0");
		// aLCInsureAccSchema.setInsuAccGetMoney("0");
		// aLCInsureAccSchema.setSumPaym("0");
		// aLCInsureAccSchema.setFrozenMoney("0");
		// aLCInsureAccSchema.setBalaDate("2004-9-1");
		// aLCInsureAccSchema.setAccComputeFlag("4");
		// aLCInsureAccSchema.setManageCom("86110000");
		// aLCInsureAccSchema.setOperator("001");
		// aLCInsureAccSchema.setMakeDate("2004-10-10");
		// aLCInsureAccSchema.setModifyTime("2004-10-10");
		// double a = aAccountManage.getAccBalance(aLCInsureAccSchema,
		// "2004-10-11", "Y", "D");
		// (String aInsuAccNo,double aOriginAccBalance,String aStartDate,String
		// aEndDate,String aIntervalType)

		// double interest =
		// aAccountManage.getInterest("000006",118000,"2004-9-1","2004-10-11","1");
		// double a = 0.72;
		// double b = 0;
		// double d ;
		// double c ;
		// b = 1.0/365.0;
		// d = 1/365;
		// LMLoanSchema tLMLoanSchema = new LMLoanSchema();
		// LMLoanDB tLMLoanDB = new LMLoanDB();
		// tLMLoanDB.setRiskCode("111301");
		// tLMLoanDB.getInfo();
		// tLMLoanSchema.setSchema(tLMLoanDB.getSchema());
		// LOLoanSchema tLOLoanSchema = new LOLoanSchema();
		// LOLoanDB tLOLoanDB = new LOLoanDB();
		// tLOLoanDB.setEdorNo("86110020030420000338");
		// tLOLoanDB.setPolNo("86110020030210000009");
		// tLOLoanDB.getInfo();
		// tLOLoanSchema.setSchema(tLOLoanDB.getSchema());
		// VData tv = new VData();
		// tv.add(tLOLoanSchema);
		//
		//
		// AccountManage tAccountManage = new AccountManage();
		// double
		// tt=tAccountManage.getMultiInterest("1",tv,1000,"2000-01-01","2003-09-01");
		// // b = tAccountManage.TransAccRate(a,"Y","C","D");
		// // d =
		// tAccountManage.getAccBalance("86110020020210000032","2003-05-01","Y","D");
		// d =
		// tAccountManage.getInterest(1000.0,"2002-10-10","2003-01-01",0.0072,"Y","S","D");
		// LOLoanSchema tL = new LOLoanSchema();
		// tL.set
		// System.out.println("---d:"+d);
		
	}
}

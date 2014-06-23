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
 * Description:�ʻ�������
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

	public CErrors mErrors = new CErrors(); // ������Ϣ

	LCInsureAccSet mLCInsureAccSet = new LCInsureAccSet();

	LCInsureAccTraceSet mLCInsureAccTraceSet = new LCInsureAccTraceSet();

	LCInsureAccClassSet mLCInsureAccClassSet = new LCInsureAccClassSet();

	// �����Ϣ�Ļ���
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
	 * �˷������ݲ���aOriginMoney��ԭʼ��������Ϣ
	 *
	 * @param aOriginMoney
	 *            double ԭʼ���
	 * @param TableName
	 *            String
	 * @param aRate
	 *            double �ṩ�̶�����ֵ,��������ʱ���ֵ��ʼ��Ϊ0.0
	 * @param aComputerFlag
	 *            int �����־λ�����ֵ������������̶����ʼ������ʱ��
	 * @param aRateType
	 *            String ԭʼ�������� ���簴�����ʣ����µ�
	 * @param aIntervalType
	 *            String Ŀ���������� ͬ��
	 * @param aStartDate
	 *            String ��Ϣ��ʱ�Ŀ�ʼ����
	 * @param aEndDate
	 *            String ��Ϣ��ʱ�Ľ�������
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

			// ����Ϣ
			break;
		case 1:

			// ���չ̶����ʵ�����Ϣ
			tCalRate = TransAccRate(aRate, aRateType, "S", aIntervalType); // ������������������
			tInterval = PubFun.calInterval(aStartDate, aEndDate, aIntervalType); // ����ʱ����
			calInterest = aOriginMoney * getIntvRate(tInterval, tCalRate, "S"); // ������Ϣ
			break;
		case 2:

			// ���չ̶����ʸ�����Ϣ
			tCalRate = TransAccRate(aRate, aRateType, "C", aIntervalType);
			tInterval = PubFun.calInterval(aStartDate, aEndDate, aIntervalType); // ����ʱ����
			calInterest = aOriginMoney * getIntvRate(tInterval, tCalRate, "C"); // ������Ϣ
			break;
		case 3:

			// �������ʱ�����Ϣ(���ʱ�����interest000001����LDBankRate???)
			String[] ResultRate = getMultiAccRate(TableName, aStartDate,
					aEndDate, aRateType, "S", aIntervalType); // �����ṩ���ȡ�ֶ�����
			for (int m = 0; m < ResultRate.length; m++) {
				// �����Ӵ˶δ��룬��ӿ�ʼ
				if (ResultRate[m] == null) {
					ResultRate[m] = "0";
				}
				// ��ӽ���
				tCalRate = Double.parseDouble(ResultRate[m]);
				double tSubInterest = aOriginMoney * tCalRate;
				calInterest += tSubInterest;
			}
			break;
		case 4:
			String[] ResultRate2 = getMultiAccRate(TableName, aStartDate,
					aEndDate, aRateType, "C", aIntervalType); // �����ṩ���ȡ�ֶ�����
			for (int m = 0; m < ResultRate2.length; m++) {
				// �����Ӵ˶δ��룬��ӿ�ʼ
				if (ResultRate2[m] == null) {
					ResultRate2[m] = "0";
				}
				// ��ӽ���
				tCalRate = Double.parseDouble(ResultRate2[m]);
				double tSubInterest = aOriginMoney * tCalRate;
				calInterest += tSubInterest;
			}
			break;

		}
		return calInterest;
	}

	/**
	 * Yanghong��20050614��Ӹ÷���
	 *
	 * @param aOriginMoney
	 *            double
	 * @param aInsuAccNo
	 *            String �ʻ�����
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
		// ������㷽ʽ�����ʱ�ȸ���LMRiskInsuAcc��ȡ
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		tLMRiskInsuAccDB.setInsuAccNo(aInsuAccNo);
		// ����ڱ���δ�ҵ������Ϣ����Ϣ��0.0
		if (!tLMRiskInsuAccDB.getInfo()) {
			return calInterest;
		}
		switch (Integer.parseInt(tLMRiskInsuAccDB.getAccComputeFlag())) {
		case 0:

			// ����Ϣ
			calInterest = 0.0;
			break;
		case 1:

			// ���չ̶����ʵ�����Ϣ
			tAccRate = tLMRiskInsuAccDB.getAccRate(); // �ӱ��л�ȡ�̶�����
			tCalAccRate = TransAccRate(tAccRate, aRateType, "S", aIntervalType); // ������������������
			tInterval = PubFun.calInterval(aStartDate, aEndDate, aIntervalType); // ����ʱ����
			calInterest = aOriginMoney
					* getIntvRate(tInterval, tCalAccRate, "S"); // ������Ϣ
			break;
		case 2:

			// ���չ̶����ʸ�����Ϣ
			tAccRate = tLMRiskInsuAccDB.getAccRate(); // �ӱ��л�ȡ�̶�����
			tCalAccRate = TransAccRate(tAccRate, aRateType, "C", aIntervalType); // ������������������
			tInterval = PubFun.calInterval(aStartDate, aEndDate, aIntervalType); // ����ʱ����
			calInterest = aOriginMoney
					* getIntvRate(tInterval, tCalAccRate, "C"); // ������Ϣ
			break;
		case 3:
			String[] ResultRate = getMultiAccRate(aInsuAccNo, tLMRiskInsuAccDB
					.getSchema(), aStartDate, aEndDate, aRateType, "S",
					aIntervalType); // �����ṩ���ȡ�ֶ�����
			for (int m = 0; m < ResultRate.length; m++) {
				// �����Ӵ˶δ��룬��ӿ�ʼ
				if (ResultRate[m] == null) {
					ResultRate[m] = "0";
				}
				// ��ӽ���
				tCalAccRate = Double.parseDouble(ResultRate[m]);
				double tSubInterest = aOriginMoney * tCalAccRate;
				calInterest += tSubInterest;
			}
			break;
		case 4:
			String[] ResultRate2 = getMultiAccRate(aInsuAccNo, tLMRiskInsuAccDB
					.getSchema(), aStartDate, aEndDate, aRateType, "C",
					aIntervalType); // �����ṩ���ȡ�ֶ�����
			for (int m = 0; m < ResultRate2.length; m++) {
				// �����Ӵ˶δ��룬��ӿ�ʼ
				if (ResultRate2[m] == null) {
					ResultRate2[m] = "0";
				}
				// ��ӽ���
				tCalAccRate = Double.parseDouble(ResultRate2[m]);
				double tSubInterest = aOriginMoney * tCalAccRate;
				calInterest += tSubInterest;
			}
			break;

		}
		return calInterest;
	}

	/**
	 * �����µ��ʻ���Ϣ���߼����д��� �����ʻ���������Ϣ�����������⡢��ȫ����׷������Ľ�Ϣ��ʽ
	 *
	 * @param aLCInsureAccClassDB
	 *            LCInsureAccClassDB
	 * @param aBalaDate
	 *            String ��Ϣ����
	 * @param aRateType
	 *            String ԭʼ�������ͣ����������ʣ������ʵ�
	 * @param aIntvType
	 *            String Ŀ���������ͣ����������ʣ������ʵ�
	 * @return double
	 */
	public double getAccClassInterest(LCInsureAccClassDB aLCInsureAccClassDB,
			String aBalaDate, String aRateType, String aIntvType) {
		ExeSQL tExeSQL = new ExeSQL();
		double aAccClassInterest = 0.0;
		int tInterval = 0;
		double tAccClassRate, tCalAccClassRate;
		double tInsuAccClassBala = aLCInsureAccClassDB.getInsuAccBala();
		// �õ����������ʻ�����
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		String aInsuAccNo = aLCInsureAccClassDB.getInsuAccNo();
		String tBalaDate = aLCInsureAccClassDB.getBalaDate(); // ע�������aBalaDate����,��Ϊ�ʻ�������ϴν�Ϣ����
		// �ж������Ľ�Ϣ��
		//�ж�ָ����Ϣ��aBalaDate ǰ�������Ľ�Ϣ��
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
			System.out.println("�޹켣��");
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

			// ����Ϣ
			break;
		case 1:
			tAccClassRate = tLMRiskInsuAccDB.getAccRate(); // ��ȡ�̶�����

			tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "S",
					aIntvType); // ת���������ֵ

			// ɸѡ��paydate>�ʻ������Ľ�Ϣ����BalaDate

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
					// ����켣����paydate>�ʻ������Ľ�Ϣ����BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// Ȼ��Թ켣������ǮtempMoney����Ϣ
					tInterval = PubFun.calInterval(payDate, aBalaDate,
							aIntvType);
					aAccClassInterest += tempMoney
							* getIntvRate(tInterval, tCalAccClassRate, "S");
				}
			}
			tInsuAccClassBala -= tInsureAccTraceMoneySum; // ���ʻ���������һ��������Ϣ
			tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			aAccClassInterest += tInsuAccClassBala
					* getIntvRate(tInterval, tCalAccClassRate, "S");
			break;
		case 2:
			tAccClassRate = tLMRiskInsuAccDB.getAccRate(); // ��ȡ�̶�����

			tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "C",
					aIntvType); // ת���������ֵ

			// ɸѡ��paydate>�ʻ������Ľ�Ϣ����BalaDate
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
					// ����켣����paydate>�ʻ������Ľ�Ϣ����BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// Ȼ��Թ켣������ǮtempMoney����Ϣ
					tInterval = PubFun.calInterval(payDate, aBalaDate,
							aIntvType);
					aAccClassInterest += tempMoney
							* getIntvRate(tInterval, tCalAccClassRate, "C");
				}
			}
			tInsuAccClassBala -= tInsureAccTraceMoneySum; // ���ʻ���������һ��������Ϣ
			tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			aAccClassInterest += tInsuAccClassBala
					* getIntvRate(tInterval, tCalAccClassRate, "C");
			break;
		case 3:

			// tAccClassRate = tLMRiskInsuAccDB.getAccRate();//��ȡ�̶�����

			// tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "C",
			// aIntvType);//ת���������ֵ
			// ɸѡ��paydate>�ʻ������Ľ�Ϣ����BalaDate
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
					// ����켣����paydate>�ʻ������Ľ�Ϣ����BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// Ȼ��Թ켣������ǮtempMoney����Ϣ
					// tInterval = PubFun.calInterval(payDate, aBalaDate,
					// aIntvType);
					String[] ResultRate2 = getMultiAccRate(aInsuAccNo,
							tLMRiskInsuAccDB.getSchema(), payDate, aBalaDate,
							aRateType, "S", aIntvType);
					for (int m = 0; m < ResultRate2.length; m++) {
						// �����Ӵ˶δ��룬��ӿ�ʼ
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
			tInsuAccClassBala -= tInsureAccTraceMoneySum; // ���ʻ���������һ��������Ϣ

			// tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			ResultRate = getMultiAccRate(aInsuAccNo, tLMRiskInsuAccDB
					.getSchema(), tBalaDate, aBalaDate, aRateType, "S",
					aIntvType);
			for (int m = 0; m < ResultRate.length; m++) {
				// �����Ӵ˶δ��룬��ӿ�ʼ
				if (ResultRate[m] == null) {
					ResultRate[m] = "0";
				}
				tCalAccClassRate = Double.parseDouble(ResultRate[m]);
				aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
			}

			// aAccClassInterest+=tInsuAccClassBala*getIntvRate(tInterval,tCalAccClassRate,"C");
			break;
		case 4:// ����������
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
				// �ж��������ںͽ�Ϣ�յļ����������Ϊ������Ҫ���������Ϣ����
				// ͬʱ��Ҫ�ж��������ڴ����ϴν�Ϣ���ڻ��ʻ���������
				if (tIntv > 0 && payDate.compareTo(tBalaDate) > 0) {

					// ����켣����paydate>�ʻ������Ľ�Ϣ����BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// Ȼ��Թ켣������ǮtempMoney����Ϣ
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
						// �����Ӵ˶δ��룬��ӿ�ʼ
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
					System.out.println("ÿ�ʲ�����ȡ��׷�ӱ������Ϣ" + aAccClassInterest);
					// aAccClassInterest+=tempMoney*getIntvRate(tInterval,
					// tCalAccClassRate,
					// "C");
				}
			}
			System.out.println("������ȡ��׷�ӱ������Ϣ" + aAccClassInterest);

			// �޸ĳ���Ϊ����������ⲽ������û�б�Ҫִ����
			// tInsuAccClassBala += tInsureAccTraceMoneySum; //���ʻ���������һ��������Ϣ

			// tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			ResultRate = getMultiAccRate(aInsuAccNo, tLMRiskInsuAccDB
					.getSchema(), tBalaDate, aBalaDate, aRateType, "C",
					aIntvType);
			double tmpInterst = 1;
			for (int m = 0; m < ResultRate.length; m++) {
				// �����Ӵ˶δ��룬��ӿ�ʼ
				if (ResultRate[m] == null) {
					ResultRate[m] = "0";
				}
				tCalAccClassRate = Double.parseDouble(ResultRate[m]);
				// System.out.println(tCalAccClassRate + "�ڶ��ε�������");
				tmpInterst = tmpInterst * (1 + tCalAccClassRate);
				// aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
			}

			// System.out.println(tInsuAccClassBala * (tmpInterst - 1)) ;
			aAccClassInterest += tInsuAccClassBala * (tmpInterst - 1);
			System.out.println("������˱�ʱ����Ϣ" + aAccClassInterest);

			// aAccClassInterest+=tInsuAccClassBala*getIntvRate(tInterval,tCalAccClassRate,"C");
			break;
		case 5:// ����
			// �����ʻ���Ϣ�����������������գ�������Alex
			// �����ʻ��켣������һ��İ��긴�����㣬ʣ�಻��һ��İ��굥�����㣬����Ϊ�긴��������һ�������/365
			// ��������acc��accClass��һһ��Ӧ�ģ�����polno+InsuAccNo�͹��ˡ�
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
				// paydate Ϊ��Ϣ�գ�
				String payDate = PubFun.getLastDate(tmpLCInsuAccTraceDB
						.getPayDate(), -1, "D");
				// �˻�ת�Ƶ����⴦��
				if ("AO".equals(tmpLCInsuAccTraceDB.getMoneyType())
						|| "AI".equals(tmpLCInsuAccTraceDB.getMoneyType()))
					payDate = tmpLCInsuAccTraceDB.getPayDate();
				// System.out.println("payDate:-->"+payDate);

				// tBalaDate֮ǰ�����й켣����tBalaDate��ʼ������Ϣ��tBalaDate������֮��İ�payDate������Ϣ
				// �������ھ�����ı�׼��paydate���Ǽƣ���Ϣ�գ�������>��<������>=��<=�д���������lb_:)
				// �����׼�����payDate == aBalaDate��������Ϣ�գ�����һ����Ϣ��
				if (isInterest && payDate.compareTo(tBalaDate) < 0) {
					payDate = tBalaDate;
				}
				double tempMoney = tmpLCInsuAccTraceDB.getMoney();
				int tIntv = PubFun.calInterval(payDate, aBalaDate, aIntvType);
				// �ȼ����Ƿ��г���һ��Ĺ켣��aIntvType="Y",�������Ҫ�����긴�����м���
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
					// ֻ���ж��������ڴ����ϴν�Ϣ���ڻ��ʻ��������ڵĲ������긴��
					if (tIntv > 0) {
						// ����켣����paydate>�ʻ������Ľ�Ϣ����BalaDate

						tInsureAccTraceMoneySum += tempMoney;
						// Ȼ��Թ켣������ǮtempMoney����Ϣ
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
							// �����Ӵ˶δ��룬��ӿ�ʼ
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

						// �������긴����ʣ�಻��һ������յ�������
						String[] ResultRate3 = getMultiAccRate(aInsuAccNo,
								tLMRiskInsuAccDB.getSchema(), PubFun
										.getLastDate(payDate, tIntv, "Y"),
								aBalaDate, aRateType, "S", "D");
						double tmpIntrest3 = 1;
						for (int m = 0; m < ResultRate3.length; m++) {
							// �����Ӵ˶δ��룬��ӿ�ʼ
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
								* (tmpIntrest3 - 1); // �������긴���Ļ������㵥��������Ϊ��������������tempMoney
						// *tmpIntrest������
						// �����������

						System.out
								.println("ÿ�ʲ�����ȡ��׷�ӱ������Ϣ" + aAccClassInterest);
						// aAccClassInterest+=tempMoney*getIntvRate(tInterval,
						// tCalAccClassRate,
						// "C");
					} else {
						int tIntvDay = PubFun.calInterval(payDate, aBalaDate,
								"D");
						if (tIntvDay > 0) {
							// ��ȫ����һ��Ĺ켣ֱ�Ӽ��㵥��
							String[] ResultRate3 = getMultiAccRate(aInsuAccNo,
									tLMRiskInsuAccDB.getSchema(), payDate,
									aBalaDate, aRateType, "S", "D");
							double tmpIntrest3 = 1;
							for (int m = 0; m < ResultRate3.length; m++) {
								// �����Ӵ˶δ��룬��ӿ�ʼ
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
						// ��һ����Ϣ���������tBalaDate == payDate����ʾ�����Ѿ����һ��Ϣ�ˣ����ٽ��н�Ϣ
						else if (tIntvDay == 0 && !isInterest) {
							String tTableName = tLMRiskInsuAccDB
									.getAccRateTable();
							// ȡ����Ч����������
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
						}// �����������
					}
				}

			}
			System.out.println("������ȡ��׷�ӱ������Ϣ" + aAccClassInterest);

			// �޸ĳ���Ϊ����������ⲽ������û�б�Ҫִ����
			// tInsuAccClassBala += tInsureAccTraceMoneySum; //���ʻ���������һ��������Ϣ

			// tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			// ResultRate = getMultiAccRate(aInsuAccNo,
			// tLMRiskInsuAccDB.getSchema(), tBalaDate
			// , aBalaDate, aRateType, "S", "D");
			// tmpInterst = 1;
			// for (int m = 0; m < ResultRate.length; m++)
			// {
			// //�����Ӵ˶δ��룬��ӿ�ʼ
			// if (ResultRate[m] == null)
			// {
			// ResultRate[m] = "0";
			// }
			// tCalAccClassRate = Double.parseDouble(ResultRate[m]);
			// System.out.println(tCalAccClassRate + "�ڶ��ε�������");
			// tmpInterst = tmpInterst * (1 + tCalAccClassRate);
			// // aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
			// }
			//
			// System.out.println(tInsuAccClassBala * (tmpInterst - 1)) ;
			// aAccClassInterest += tInsuAccClassBala * (tmpInterst - 1);
			// System.out.println("������˱�ʱ����Ϣ" + aAccClassInterest);

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
				// tBalaDate֮ǰ�����й켣����tBalaDate��ʼ������Ϣ��tBalaDate������֮��İ�payDate������Ϣ
				if (isInterest && payDate.compareTo(tBalaDate) < 0) {
					payDate = tBalaDate;
				}
				double tempMoney = tmpLCInsuAccTraceDB.getMoney();
				int tIntv = PubFun.calInterval(payDate, aBalaDate, "D");
				// �ո���
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
		case 7:// ��������
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
							// ת��Ϊ������
							tCalAccClassRate = TransAccRate(tRate, tOriginRateType,
									aRateType, "D");
							tmpIntrest = Arith.mul(tmpIntrest, Arith.add(
									tCalAccClassRate, 1));
						}

						aAccClassInterest = Arith.add(Arith.mul(tempMoney, Arith
								.sub(tmpIntrest, 1)), aAccClassInterest);

						System.out.println("ÿ�ʲ�����ȡ��׷�ӱ������Ϣ" + aAccClassInterest);

					}
				}
			}
			System.out.println("������ȡ��׷�ӱ������Ϣ" + aAccClassInterest);

			break;

		case 8:// ��������:�Ȱ�������ת�����¸�������ת�����յ���, ��ͷ����β
//			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
//			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
//			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB
//					.getPayPlanCode());
			//ֻ����ָ����Ϣ��aBalaDateǰ�Ĺ켣
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

							//ת��Ϊ�յ���
							tCalAccClassRate = TransAccRate(tRate, tOriginRateType,
									"S", "D", monthLength, yearLength);

							// ת��Ϊ����������
							/*tCalAccClassRate = TransAccRate(tCalAccClassRate, "2",
									"S", "D", monthLength, yearLength);*/

							tmpIntrest = Arith.add(tmpIntrest, tCalAccClassRate);

							tPayDateBak = PubFun.getLastDate(tPayDateBak, 1, "D");
						}

						aAccClassInterest = Arith.add(Arith.mul(tempMoney, Arith
								.sub(tmpIntrest, 1)), aAccClassInterest);

						System.out.println("ÿ�ʲ�����ȡ��׷�ӱ������Ϣ" + aAccClassInterest);

					}
				}
			}
			System.out.println("������ȡ��׷�ӱ������Ϣ" + aAccClassInterest);

			break;

		}
		return Arith.round(aAccClassInterest, 2);
	}


	/**
	 * �����µ��ʻ���Ϣ���߼����д��� �����ʻ���������Ϣ�����������⡢��ȫ����׷������Ľ�Ϣ��ʽ
	 *
	 * @param aLCInsureAccTraceDB
	 *            LCInsureAccTraceDB
	 * @param aBalaDate
	 *            String ��Ϣ����
	 * @param aRateType
	 *            String ԭʼ�������ͣ����������ʣ������ʵ�
	 * @param aIntvType
	 *            String Ŀ���������ͣ����������ʣ������ʵ�
	 * @param aBqFlag
	 *            String ��ȫ��ǣ�1��ȫ������
	 * @return double
	 */
	public double getAccTraceInterest(LCInsureAccTraceDB aLCInsureAccTraceDB,
			String aBalaDate, String aRateType, String aIntvType, String aBqFlag) {

		ExeSQL tExeSQL = new ExeSQL();
		double aAccClassInterest = 0.0; // ������õ�����Ϣ
		String tBalaDate; // �ϴμ�Ϣ����

		// �õ����������ʻ�����
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		String aInsuAccNo = aLCInsureAccTraceDB.getInsuAccNo();

		// �ж�ָ����Ϣ��aBalaDate ǰ�������Ľ�Ϣ��
		boolean isInterest = false;
		String tsql = "select to_char(max(a.paydate),'yyyy-mm-dd')  from LCInsureAccTrace a where a.polno = '"
				+ aLCInsureAccTraceDB.getPolNo()
				+ "' and a.InsuAccNo = '"
				+ aLCInsureAccTraceDB.getInsuAccNo()
				+ "' and a.payplancode = '"
				+ aLCInsureAccTraceDB.getPayPlanCode()
				+ "' and a.moneytype in ('LX','BX') and a.paydate <='"  //���ϴν�Ϣ��ʱ��Ҳ��������֤���ʽ��BX��Ϣ��¼
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
				System.out.println("û���˻��켣���¼��");
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
			// ����Ϣ
			break;

		case 8:// ��������:�յ���, ��ͷ����β
			// �ֶδ����Ȳ�ѯ�ϴν�Ϣ���뵱ǰ��Ϣ��֮��ļ�¼
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

							// ת��Ϊ�յ���
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
								.println("ÿ�ʲ�����ȡ��׷�ӱ������Ϣ" + aAccClassInterest);

					}
				}
			}
			System.out.println("������ȡ��׷�ӱ������Ϣ" + aAccClassInterest);

			break;

		}
		return Arith.round(aAccClassInterest, 2);
	}



	/**
	 * �����µ��ʻ���Ϣ���߼����д��� �����ʻ���������Ϣ�����������⡢��ȫ����׷������Ľ�Ϣ��ʽ
	 *
	 * @param aLCInsureAccClassDB
	 *            LCInsureAccClassDB
	 * @param aBalaDate
	 *            String ��Ϣ����
	 * @param aRateType
	 *            String ԭʼ�������ͣ����������ʣ������ʵ�
	 * @param aIntvType
	 *            String Ŀ���������ͣ����������ʣ������ʵ�
	 * @return double
	 */
	public double getAccClassInterest(LCInsureAccClassDB aLCInsureAccClassDB,
			String aBalaDate, String aRateType, String aIntvType , String aBqFlag) {
		ExeSQL tExeSQL = new ExeSQL();
		double aAccClassInterest = 0.0;
		int tInterval = 0;
		double tAccClassRate, tCalAccClassRate;
		double tInsuAccClassBala = aLCInsureAccClassDB.getInsuAccBala();
		// �õ����������ʻ�����
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		String aInsuAccNo = aLCInsureAccClassDB.getInsuAccNo();
		String tBalaDate = aLCInsureAccClassDB.getBalaDate(); // ע�������aBalaDate����,��Ϊ�ʻ�������ϴν�Ϣ����
		// �ж������Ľ�Ϣ��
		//�ж�ָ����Ϣ��aBalaDate ǰ�������Ľ�Ϣ��
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
			System.out.println("�޹켣��");
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

			// ����Ϣ
			break;
		case 1:
			tAccClassRate = tLMRiskInsuAccDB.getAccRate(); // ��ȡ�̶�����

			tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "S",
					aIntvType); // ת���������ֵ

			// ɸѡ��paydate>�ʻ������Ľ�Ϣ����BalaDate

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
					// ����켣����paydate>�ʻ������Ľ�Ϣ����BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// Ȼ��Թ켣������ǮtempMoney����Ϣ
					tInterval = PubFun.calInterval(payDate, aBalaDate,
							aIntvType);
					aAccClassInterest += tempMoney
							* getIntvRate(tInterval, tCalAccClassRate, "S");
				}
			}
			tInsuAccClassBala -= tInsureAccTraceMoneySum; // ���ʻ���������һ��������Ϣ
			tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			aAccClassInterest += tInsuAccClassBala
					* getIntvRate(tInterval, tCalAccClassRate, "S");
			break;
		case 2:
			tAccClassRate = tLMRiskInsuAccDB.getAccRate(); // ��ȡ�̶�����

			tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "C",
					aIntvType); // ת���������ֵ

			// ɸѡ��paydate>�ʻ������Ľ�Ϣ����BalaDate
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
					// ����켣����paydate>�ʻ������Ľ�Ϣ����BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// Ȼ��Թ켣������ǮtempMoney����Ϣ
					tInterval = PubFun.calInterval(payDate, aBalaDate,
							aIntvType);
					aAccClassInterest += tempMoney
							* getIntvRate(tInterval, tCalAccClassRate, "C");
				}
			}
			tInsuAccClassBala -= tInsureAccTraceMoneySum; // ���ʻ���������һ��������Ϣ
			tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			aAccClassInterest += tInsuAccClassBala
					* getIntvRate(tInterval, tCalAccClassRate, "C");
			break;
		case 3:

			// tAccClassRate = tLMRiskInsuAccDB.getAccRate();//��ȡ�̶�����

			// tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "C",
			// aIntvType);//ת���������ֵ
			// ɸѡ��paydate>�ʻ������Ľ�Ϣ����BalaDate
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
					// ����켣����paydate>�ʻ������Ľ�Ϣ����BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// Ȼ��Թ켣������ǮtempMoney����Ϣ
					// tInterval = PubFun.calInterval(payDate, aBalaDate,
					// aIntvType);
					String[] ResultRate2 = getMultiAccRate(aInsuAccNo,
							tLMRiskInsuAccDB.getSchema(), payDate, aBalaDate,
							aRateType, "S", aIntvType);
					for (int m = 0; m < ResultRate2.length; m++) {
						// �����Ӵ˶δ��룬��ӿ�ʼ
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
			tInsuAccClassBala -= tInsureAccTraceMoneySum; // ���ʻ���������һ��������Ϣ

			// tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			ResultRate = getMultiAccRate(aInsuAccNo, tLMRiskInsuAccDB
					.getSchema(), tBalaDate, aBalaDate, aRateType, "S",
					aIntvType);
			for (int m = 0; m < ResultRate.length; m++) {
				// �����Ӵ˶δ��룬��ӿ�ʼ
				if (ResultRate[m] == null) {
					ResultRate[m] = "0";
				}
				tCalAccClassRate = Double.parseDouble(ResultRate[m]);
				aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
			}

			// aAccClassInterest+=tInsuAccClassBala*getIntvRate(tInterval,tCalAccClassRate,"C");
			break;
		case 4:// ����������
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
				// �ж��������ںͽ�Ϣ�յļ����������Ϊ������Ҫ���������Ϣ����
				// ͬʱ��Ҫ�ж��������ڴ����ϴν�Ϣ���ڻ��ʻ���������
				if (tIntv > 0 && payDate.compareTo(tBalaDate) > 0) {

					// ����켣����paydate>�ʻ������Ľ�Ϣ����BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// Ȼ��Թ켣������ǮtempMoney����Ϣ
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
						// �����Ӵ˶δ��룬��ӿ�ʼ
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
					System.out.println("ÿ�ʲ�����ȡ��׷�ӱ������Ϣ" + aAccClassInterest);
					// aAccClassInterest+=tempMoney*getIntvRate(tInterval,
					// tCalAccClassRate,
					// "C");
				}
			}
			System.out.println("������ȡ��׷�ӱ������Ϣ" + aAccClassInterest);

			// �޸ĳ���Ϊ����������ⲽ������û�б�Ҫִ����
			// tInsuAccClassBala += tInsureAccTraceMoneySum; //���ʻ���������һ��������Ϣ

			// tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			ResultRate = getMultiAccRate(aInsuAccNo, tLMRiskInsuAccDB
					.getSchema(), tBalaDate, aBalaDate, aRateType, "C",
					aIntvType);
			double tmpInterst = 1;
			for (int m = 0; m < ResultRate.length; m++) {
				// �����Ӵ˶δ��룬��ӿ�ʼ
				if (ResultRate[m] == null) {
					ResultRate[m] = "0";
				}
				tCalAccClassRate = Double.parseDouble(ResultRate[m]);
				// System.out.println(tCalAccClassRate + "�ڶ��ε�������");
				tmpInterst = tmpInterst * (1 + tCalAccClassRate);
				// aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
			}

			// System.out.println(tInsuAccClassBala * (tmpInterst - 1)) ;
			aAccClassInterest += tInsuAccClassBala * (tmpInterst - 1);
			System.out.println("������˱�ʱ����Ϣ" + aAccClassInterest);

			// aAccClassInterest+=tInsuAccClassBala*getIntvRate(tInterval,tCalAccClassRate,"C");
			break;
		case 5:// ����
			// �����ʻ���Ϣ�����������������գ�������Alex
			// �����ʻ��켣������һ��İ��긴�����㣬ʣ�಻��һ��İ��굥�����㣬����Ϊ�긴��������һ�������/365
			// ��������acc��accClass��һһ��Ӧ�ģ�����polno+InsuAccNo�͹��ˡ�
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
				// paydate Ϊ��Ϣ�գ�
				String payDate = PubFun.getLastDate(tmpLCInsuAccTraceDB
						.getPayDate(), -1, "D");
				// �˻�ת�Ƶ����⴦��
				if ("AO".equals(tmpLCInsuAccTraceDB.getMoneyType())
						|| "AI".equals(tmpLCInsuAccTraceDB.getMoneyType()))
					payDate = tmpLCInsuAccTraceDB.getPayDate();
				// System.out.println("payDate:-->"+payDate);

				// tBalaDate֮ǰ�����й켣����tBalaDate��ʼ������Ϣ��tBalaDate������֮��İ�payDate������Ϣ
				// �������ھ�����ı�׼��paydate���Ǽƣ���Ϣ�գ�������>��<������>=��<=�д���������lb_:)
				// �����׼�����payDate == aBalaDate��������Ϣ�գ�����һ����Ϣ��
				if (isInterest && payDate.compareTo(tBalaDate) < 0) {
					payDate = tBalaDate;
				}
				double tempMoney = tmpLCInsuAccTraceDB.getMoney();
				int tIntv = PubFun.calInterval(payDate, aBalaDate, aIntvType);
				// �ȼ����Ƿ��г���һ��Ĺ켣��aIntvType="Y",�������Ҫ�����긴�����м���
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
					// ֻ���ж��������ڴ����ϴν�Ϣ���ڻ��ʻ��������ڵĲ������긴��
					if (tIntv > 0) {
						// ����켣����paydate>�ʻ������Ľ�Ϣ����BalaDate

						tInsureAccTraceMoneySum += tempMoney;
						// Ȼ��Թ켣������ǮtempMoney����Ϣ
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
							// �����Ӵ˶δ��룬��ӿ�ʼ
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

						// �������긴����ʣ�಻��һ������յ�������
						String[] ResultRate3 = getMultiAccRate(aInsuAccNo,
								tLMRiskInsuAccDB.getSchema(), PubFun
										.getLastDate(payDate, tIntv, "Y"),
								aBalaDate, aRateType, "S", "D");
						double tmpIntrest3 = 1;
						for (int m = 0; m < ResultRate3.length; m++) {
							// �����Ӵ˶δ��룬��ӿ�ʼ
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
								* (tmpIntrest3 - 1); // �������긴���Ļ������㵥��������Ϊ��������������tempMoney
						// *tmpIntrest������
						// �����������

						System.out
								.println("ÿ�ʲ�����ȡ��׷�ӱ������Ϣ" + aAccClassInterest);
						// aAccClassInterest+=tempMoney*getIntvRate(tInterval,
						// tCalAccClassRate,
						// "C");
					} else {
						int tIntvDay = PubFun.calInterval(payDate, aBalaDate,
								"D");
						if (tIntvDay > 0) {
							// ��ȫ����һ��Ĺ켣ֱ�Ӽ��㵥��
							String[] ResultRate3 = getMultiAccRate(aInsuAccNo,
									tLMRiskInsuAccDB.getSchema(), payDate,
									aBalaDate, aRateType, "S", "D");
							double tmpIntrest3 = 1;
							for (int m = 0; m < ResultRate3.length; m++) {
								// �����Ӵ˶δ��룬��ӿ�ʼ
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
						// ��һ����Ϣ���������tBalaDate == payDate����ʾ�����Ѿ����һ��Ϣ�ˣ����ٽ��н�Ϣ
						else if (tIntvDay == 0 && !isInterest) {
							String tTableName = tLMRiskInsuAccDB
									.getAccRateTable();
							// ȡ����Ч����������
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
						}// �����������
					}
				}

			}
			System.out.println("������ȡ��׷�ӱ������Ϣ" + aAccClassInterest);

			// �޸ĳ���Ϊ����������ⲽ������û�б�Ҫִ����
			// tInsuAccClassBala += tInsureAccTraceMoneySum; //���ʻ���������һ��������Ϣ

			// tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			// ResultRate = getMultiAccRate(aInsuAccNo,
			// tLMRiskInsuAccDB.getSchema(), tBalaDate
			// , aBalaDate, aRateType, "S", "D");
			// tmpInterst = 1;
			// for (int m = 0; m < ResultRate.length; m++)
			// {
			// //�����Ӵ˶δ��룬��ӿ�ʼ
			// if (ResultRate[m] == null)
			// {
			// ResultRate[m] = "0";
			// }
			// tCalAccClassRate = Double.parseDouble(ResultRate[m]);
			// System.out.println(tCalAccClassRate + "�ڶ��ε�������");
			// tmpInterst = tmpInterst * (1 + tCalAccClassRate);
			// // aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
			// }
			//
			// System.out.println(tInsuAccClassBala * (tmpInterst - 1)) ;
			// aAccClassInterest += tInsuAccClassBala * (tmpInterst - 1);
			// System.out.println("������˱�ʱ����Ϣ" + aAccClassInterest);

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
				// tBalaDate֮ǰ�����й켣����tBalaDate��ʼ������Ϣ��tBalaDate������֮��İ�payDate������Ϣ
				if (isInterest && payDate.compareTo(tBalaDate) < 0) {
					payDate = tBalaDate;
				}
				double tempMoney = tmpLCInsuAccTraceDB.getMoney();
				int tIntv = PubFun.calInterval(payDate, aBalaDate, "D");
				// �ո���
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
		case 7:// ��������
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
							// ת��Ϊ������
							tCalAccClassRate = TransAccRate(tRate, tOriginRateType,
									aRateType, "D");
							tmpIntrest = Arith.mul(tmpIntrest, Arith.add(
									tCalAccClassRate, 1));
						}

						aAccClassInterest = Arith.add(Arith.mul(tempMoney, Arith
								.sub(tmpIntrest, 1)), aAccClassInterest);

						System.out.println("ÿ�ʲ�����ȡ��׷�ӱ������Ϣ" + aAccClassInterest);

					}
				}
			}
			System.out.println("������ȡ��׷�ӱ������Ϣ" + aAccClassInterest);

			break;

		case 8:// ��������:�Ȱ�������ת�����¸�������ת�����յ���, ��ͷ����β
//			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassDB.getPolNo());
//			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
//			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassDB
//					.getPayPlanCode());
			//ֻ����ָ����Ϣ��aBalaDateǰ�Ĺ켣
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

							//ת��Ϊ�յ���
							tCalAccClassRate = TransAccRate(tRate, tOriginRateType,
									"S", "D", monthLength, yearLength);

							// ת��Ϊ����������
							/*tCalAccClassRate = TransAccRate(tCalAccClassRate, "2",
									"S", "D", monthLength, yearLength);*/

							tmpIntrest = Arith.add(tmpIntrest, tCalAccClassRate);

							tPayDateBak = PubFun.getLastDate(tPayDateBak, 1, "D");
						}

						aAccClassInterest = Arith.add(Arith.mul(tempMoney, Arith
								.sub(tmpIntrest, 1)), aAccClassInterest);

						System.out.println("ÿ�ʲ�����ȡ��׷�ӱ������Ϣ" + aAccClassInterest);

					}
				}
			}
			System.out.println("������ȡ��׷�ӱ������Ϣ" + aAccClassInterest);

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
	 * �����µ��ʻ���Ϣ���߼����д��� �����ʻ���������Ϣ���ʻ���Ϣʱʹ�� ������ķ���Ψһ�������ǣ�һ��������������Ϣ��һ���Ƿ���������Ϣ
	 * ���и���Ҫ�����𣬸÷�������һ�����ϵ�trace�Ž�Ϣ�����ҽ������Ҳ�����겿�ֵ���Ϣ�����������겿�ֵ���Ϣ���� ����ֵ�У������޸�case
	 * 5�߼�����getAccClassInterest����ͳһ��
	 *
	 * @param aLCInsureAccClassDB
	 *            LCInsureAccClassDB
	 * @param aBalaDate
	 *            String ��Ϣ����
	 * @param aRateType
	 *            String ԭʼ�������ͣ����������ʣ������ʵ�
	 * @param aIntvType
	 *            String Ŀ���������ͣ����������ʣ������ʵ�
	 * @return double
	 */
	public double getAccClassInterestBalance(
			LCInsureAccClassDB aLCInsureAccClassDB, String aBalaDate,
			String aRateType, String aIntvType) {
		double aAccClassInterest = 0.0;
		int tInterval = 0;
		double tAccClassRate, tCalAccClassRate;
		double tInsuAccClassBala = aLCInsureAccClassDB.getInsuAccBala();
		// �õ����������ʻ�����
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		String aInsuAccNo = aLCInsureAccClassDB.getInsuAccNo();
		String tBalaDate = aLCInsureAccClassDB.getBalaDate(); // ע�������aBalaDate����,��Ϊ�ʻ�������ϴν�Ϣ����
		// �ж������Ľ�Ϣ��
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

			// ����Ϣ
			break;
		case 1:
			tAccClassRate = tLMRiskInsuAccDB.getAccRate(); // ��ȡ�̶�����

			tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "S",
					aIntvType); // ת���������ֵ

			// ɸѡ��paydate>�ʻ������Ľ�Ϣ����BalaDate

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
					// ����켣����paydate>�ʻ������Ľ�Ϣ����BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// Ȼ��Թ켣������ǮtempMoney����Ϣ
					tInterval = PubFun.calInterval(payDate, aBalaDate,
							aIntvType);
					aAccClassInterest += tempMoney
							* getIntvRate(tInterval, tCalAccClassRate, "S");
				}
			}
			tInsuAccClassBala -= tInsureAccTraceMoneySum; // ���ʻ���������һ��������Ϣ
			tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			aAccClassInterest += tInsuAccClassBala
					* getIntvRate(tInterval, tCalAccClassRate, "S");
			break;
		case 2:
			tAccClassRate = tLMRiskInsuAccDB.getAccRate(); // ��ȡ�̶�����

			tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "C",
					aIntvType); // ת���������ֵ

			// ɸѡ��paydate>�ʻ������Ľ�Ϣ����BalaDate
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
					// ����켣����paydate>�ʻ������Ľ�Ϣ����BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// Ȼ��Թ켣������ǮtempMoney����Ϣ
					tInterval = PubFun.calInterval(payDate, aBalaDate,
							aIntvType);
					aAccClassInterest += tempMoney
							* getIntvRate(tInterval, tCalAccClassRate, "C");
				}
			}
			tInsuAccClassBala -= tInsureAccTraceMoneySum; // ���ʻ���������һ��������Ϣ
			tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			aAccClassInterest += tInsuAccClassBala
					* getIntvRate(tInterval, tCalAccClassRate, "C");
			break;
		case 3:

			// tAccClassRate = tLMRiskInsuAccDB.getAccRate();//��ȡ�̶�����

			// tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "C",
			// aIntvType);//ת���������ֵ
			// ɸѡ��paydate>�ʻ������Ľ�Ϣ����BalaDate
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
					// ����켣����paydate>�ʻ������Ľ�Ϣ����BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// Ȼ��Թ켣������ǮtempMoney����Ϣ
					// tInterval = PubFun.calInterval(payDate, aBalaDate,
					// aIntvType);
					String[] ResultRate2 = getMultiAccRate(aInsuAccNo,
							tLMRiskInsuAccDB.getSchema(), payDate, aBalaDate,
							aRateType, "S", aIntvType);
					for (int m = 0; m < ResultRate2.length; m++) {
						// �����Ӵ˶δ��룬��ӿ�ʼ
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
			tInsuAccClassBala -= tInsureAccTraceMoneySum; // ���ʻ���������һ��������Ϣ

			// tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			ResultRate = getMultiAccRate(aInsuAccNo, tLMRiskInsuAccDB
					.getSchema(), tBalaDate, aBalaDate, aRateType, "S",
					aIntvType);
			for (int m = 0; m < ResultRate.length; m++) {
				// �����Ӵ˶δ��룬��ӿ�ʼ
				if (ResultRate[m] == null) {
					ResultRate[m] = "0";
				}
				tCalAccClassRate = Double.parseDouble(ResultRate[m]);
				aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
			}

			// aAccClassInterest+=tInsuAccClassBala*getIntvRate(tInterval,tCalAccClassRate,"C");
			break;
		case 4:

			// �������ʱ�����Ϣ
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

					// ����켣����paydate>�ʻ������Ľ�Ϣ����BalaDate
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;
					// Ȼ��Թ켣������ǮtempMoney����Ϣ
					// tInterval = PubFun.calInterval(payDate, aBalaDate,
					// aIntvType);

					String[] ResultRate2 = getMultiAccRate(aInsuAccNo,
							tLMRiskInsuAccDB.getSchema(), payDate, aBalaDate,
							aRateType, "C", aIntvType);
					double tmpIntrest = 1;
					for (int m = 0; m < ResultRate2.length; m++) {
						// �����Ӵ˶δ��룬��ӿ�ʼ
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
			tInsuAccClassBala -= tInsureAccTraceMoneySum; // ���ʻ���������һ��������Ϣ

			// tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			ResultRate = getMultiAccRate(aInsuAccNo, tLMRiskInsuAccDB
					.getSchema(), tBalaDate, aBalaDate, aRateType, "C",
					aIntvType);
			double tmpInterst = 1;
			for (int m = 0; m < ResultRate.length; m++) {
				// �����Ӵ˶δ��룬��ӿ�ʼ
				if (ResultRate[m] == null) {
					ResultRate[m] = "0";
				}
				tCalAccClassRate = Double.parseDouble(ResultRate[m]);
				// System.out.println(tCalAccClassRate + "�ڶ��ε�������");
				tmpInterst = tmpInterst * (1 + tCalAccClassRate);
				// aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
			}
			aAccClassInterest += tInsuAccClassBala * (tmpInterst - 1);

			// aAccClassInterest+=tInsuAccClassBala*getIntvRate(tInterval,tCalAccClassRate,"C");
			break;
		case 5:
			// �����ʻ���Ϣ�����������������գ�������Alex
			// �����ʻ��켣������һ��İ��긴�����㣬ʣ�಻��һ�겻����Ϣ
			// ��������acc��accClass��һһ��Ӧ�ģ�����polno+InsuAccNo�͹��ˡ�lb:_)

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
				// �˻�ת�Ƶ����⴦��
				if ("AO".equals(tmpLCInsuAccTraceDB.getMoneyType())
						|| "AI".equals(tmpLCInsuAccTraceDB.getMoneyType()))
					payDate = tmpLCInsuAccTraceDB.getPayDate();
				// tBalaDate֮ǰ�����й켣����tBalaDate��ʼ������Ϣ��tBalaDate������֮��İ�payDate������Ϣ
				// �������ھ�����ı�׼��paydate���Ǽƣ���Ϣ�գ�������>��<������>=��<=�д���������lb_:)
				// �����׼�����payDate == aBalaDate��������Ϣ�գ�����һ����Ϣ��
				if (isInterest && payDate.compareTo(tBalaDate) < 0) {
					payDate = tBalaDate;
				}
				double tempMoney = tmpLCInsuAccTraceDB.getMoney();
				int tIntv = PubFun.calInterval(payDate, aBalaDate, aIntvType);
				// �ȼ����Ƿ��г���һ��Ĺ켣��aIntvType="Y",�������Ҫ�����긴�����м���
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
					 * //��getAccClassInterest case 5ͳһ�� //
					 * //ֻ���ж��������ڴ������ϴν�Ϣ���ڻ��ʻ��������ڵĲ������긴�� // if (tIntv > 0 ) // { //
					 * //����켣����paydate>�ʻ������Ľ�Ϣ����BalaDate // //
					 * tInsureAccTraceMoneySum += tempMoney; //
					 * //Ȼ��Թ켣������ǮtempMoney����Ϣ // // tInterval =
					 * PubFun.calInterval(payDate, aBalaDate, aIntvType); // //
					 * String[] ResultRate2 = getMultiAccRate(aInsuAccNo // // ,
					 * tLMRiskInsuAccDB.getSchema(), payDate, aBalaDate,
					 * aRateType, "C" // // , aIntvType); // String[]
					 * ResultRate2 = getMultiAccRate(aInsuAccNo // ,
					 * tLMRiskInsuAccDB.getSchema(), payDate, //
					 * PubFun.getLastDate(payDate, tIntv, "Y"), // aRateType,
					 * "C" // , aIntvType); // double tmpIntrest = 1; // for
					 * (int m = 0; m < ResultRate2.length; m++) // { //
					 * //�����Ӵ˶δ��룬��ӿ�ʼ // if (ResultRate2[m] == null) // { //
					 * ResultRate2[m] = "0"; // } // tCalAccClassRate =
					 * Double.parseDouble( // ResultRate2[m]); // //
					 * tmpMoneyAddIntrest+=tempMoney * (tCalAccClassRate+1); //
					 * tmpIntrest = tmpIntrest * (tCalAccClassRate + 1); // //
					 * double tSubInterest = tempMoney * tCalAccClassRate; // //
					 * aAccClassInterest += tSubInterest; // } //
					 * aAccClassInterest += tempMoney * (tmpIntrest - 1); // //
					 * System.out.println("ÿ�ʲ�����ȡ��׷�ӱ������Ϣ" + //
					 * aAccClassInterest); // //
					 * aAccClassInterest+=tempMoney*getIntvRate(tInterval,
					 * tCalAccClassRate, // // "C"); // }
					 */

					// ֻ���ж��������ڴ����ϴν�Ϣ���ڻ��ʻ��������ڵĲ������긴��
					if (tIntv > 0) {
						// ����켣����paydate>�ʻ������Ľ�Ϣ����BalaDate

						tInsureAccTraceMoneySum += tempMoney;
						// Ȼ��Թ켣������ǮtempMoney����Ϣ
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
							// �����Ӵ˶δ��룬��ӿ�ʼ
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

						// �������긴����ʣ�಻��һ������յ�������
						String[] ResultRate3 = getMultiAccRate(aInsuAccNo,
								tLMRiskInsuAccDB.getSchema(), PubFun
										.getLastDate(payDate, tIntv, "Y"),
								aBalaDate, aRateType, "S", "D");
						double tmpIntrest3 = 1;
						for (int m = 0; m < ResultRate3.length; m++) {
							// �����Ӵ˶δ��룬��ӿ�ʼ
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
								* (tmpIntrest3 - 1); // �������긴���Ļ������㵥��������Ϊ��������������tempMoney
						// *tmpIntrest������
						// �����������

						System.out
								.println("ÿ�ʲ�����ȡ��׷�ӱ������Ϣ" + aAccClassInterest);
						// aAccClassInterest+=tempMoney*getIntvRate(tInterval,
						// tCalAccClassRate,
						// "C");
					} else {
						int tIntvDay = PubFun.calInterval(payDate, aBalaDate,
								"D");
						if (tIntvDay > 0) {
							// ��ȫ����һ��Ĺ켣ֱ�Ӽ��㵥��
							String[] ResultRate3 = getMultiAccRate(aInsuAccNo,
									tLMRiskInsuAccDB.getSchema(), payDate,
									aBalaDate, aRateType, "S", "D");
							double tmpIntrest3 = 1;
							for (int m = 0; m < ResultRate3.length; m++) {
								// �����Ӵ˶δ��룬��ӿ�ʼ
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
							// �����������
						}
						// ��һ����Ϣ���������tBalaDate == payDate����ʾ�����Ѿ����һ��Ϣ�ˣ����ٽ��н�Ϣ
						else if (tIntvDay == 0 && !isInterest) {
							String tTableName = tLMRiskInsuAccDB
									.getAccRateTable();
							// ȡ����Ч����������
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
						}// �����������
					}

				}

			}
			System.out.println("������ȡ��׷�ӱ������Ϣ" + aAccClassInterest);

			// �޸ĳ���Ϊ����������ⲽ������û�б�Ҫִ����
			// tInsuAccClassBala += tInsureAccTraceMoneySum; //���ʻ���������һ��������Ϣ

			// tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			ResultRate = getMultiAccRate(aInsuAccNo, tLMRiskInsuAccDB
					.getSchema(), tBalaDate, aBalaDate, aRateType, "C", "Y");
			tmpInterst = 1;
			for (int m = 0; m < ResultRate.length; m++) {
				// �����Ӵ˶δ��룬��ӿ�ʼ
				if (ResultRate[m] == null) {
					ResultRate[m] = "0";
				}
				tCalAccClassRate = Double.parseDouble(ResultRate[m]);
				// System.out.println(tCalAccClassRate + "�ڶ��ε�������");
				tmpInterst = tmpInterst * (1 + tCalAccClassRate);
				// aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
			}

			// System.out.println(tInsuAccClassBala * (tmpInterst - 1)) ;
			aAccClassInterest += tInsuAccClassBala * (tmpInterst - 1);
			System.out.println("������˱�ʱ����Ϣ" + aAccClassInterest);

			// aAccClassInterest+=tInsuAccClassBala*getIntvRate(tInterval,tCalAccClassRate,"C");
			break;
		case 7:// ����
			// �����ʻ���Ϣ�����������������գ�������Alex
			// �����ʻ��켣������һ��İ��긴�����㣬ʣ�಻��һ��İ��굥�����㣬����Ϊ�긴��������һ�������/365
			// ��������acc��accClass��һһ��Ӧ�ģ�����polno+InsuAccNo�͹��ˡ�
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
				// paydate Ϊ��Ϣ�գ�
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				// System.out.println("payDate:-->"+payDate);

				// tBalaDate֮ǰ�����й켣����tBalaDate��ʼ������Ϣ��tBalaDate������֮��İ�payDate������Ϣ
				// �������ھ�����ı�׼��paydate���Ǽƣ���Ϣ�գ�������>��<������>=��<=�д���������lb_:)
				// �����׼�����payDate == aBalaDate��������Ϣ�գ�����һ����Ϣ��
				if (isInterest && payDate.compareTo(tBalaDate) < 0) {
					payDate = tBalaDate;
				}
				double tempMoney = tmpLCInsuAccTraceDB.getMoney();
				int tIntv = PubFun.calInterval(payDate, aBalaDate, aIntvType);
				// �ȼ����Ƿ��г���һ��Ĺ켣��aIntvType="Y",�������Ҫ�����긴�����м���
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
					// ֻ���ж��������ڴ����ϴν�Ϣ���ڻ��ʻ��������ڵĲ������긴��
					if (tIntv > 0) {
						// ����켣����paydate>�ʻ������Ľ�Ϣ����BalaDate

						tInsureAccTraceMoneySum += tempMoney;
						// Ȼ��Թ켣������ǮtempMoney����Ϣ
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
							// �����Ӵ˶δ��룬��ӿ�ʼ
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

						// �������긴����ʣ�಻��һ������յ�������
						String[] ResultRate3 = getMultiAccRate(aInsuAccNo,
								tLMRiskInsuAccDB.getSchema(), PubFun
										.getLastDate(payDate, tIntv, "Y"),
								aBalaDate, aRateType, "S", "D");
						double tmpIntrest3 = 1;
						for (int m = 0; m < ResultRate3.length; m++) {
							// �����Ӵ˶δ��룬��ӿ�ʼ
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
								* (tmpIntrest3 - 1); // �������긴���Ļ������㵥��������Ϊ��������������tempMoney
						// *tmpIntrest������
						// �����������

						System.out
								.println("ÿ�ʲ�����ȡ��׷�ӱ������Ϣ" + aAccClassInterest);
						// aAccClassInterest+=tempMoney*getIntvRate(tInterval,
						// tCalAccClassRate,
						// "C");
					} else {
						int tIntvDay = PubFun.calInterval(payDate, aBalaDate,
								"D");
						if (tIntvDay > 0) {
							// ��ȫ����һ��Ĺ켣ֱ�Ӽ��㵥��
							String[] ResultRate3 = getMultiAccRate(aInsuAccNo,
									tLMRiskInsuAccDB.getSchema(), payDate,
									aBalaDate, aRateType, "S", "D");
							double tmpIntrest3 = 1;
							for (int m = 0; m < ResultRate3.length; m++) {
								// �����Ӵ˶δ��룬��ӿ�ʼ
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
						// ��һ����Ϣ���������tBalaDate == payDate����ʾ�����Ѿ����һ��Ϣ�ˣ����ٽ��н�Ϣ
						// ���ղ���Ҫ��
						// else if (tIntvDay == 0 && !isInterest) {
						// String tTableName = tLMRiskInsuAccDB
						// .getAccRateTable();
						// // ȡ����Ч����������
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
						// }// �����������
					}
				}

			}
			System.out.println("������ȡ��׷�ӱ������Ϣ" + aAccClassInterest);

			// �޸ĳ���Ϊ����������ⲽ������û�б�Ҫִ����
			// tInsuAccClassBala += tInsureAccTraceMoneySum; //���ʻ���������һ��������Ϣ

			// tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			// ResultRate = getMultiAccRate(aInsuAccNo,
			// tLMRiskInsuAccDB.getSchema(), tBalaDate
			// , aBalaDate, aRateType, "S", "D");
			// tmpInterst = 1;
			// for (int m = 0; m < ResultRate.length; m++)
			// {
			// //�����Ӵ˶δ��룬��ӿ�ʼ
			// if (ResultRate[m] == null)
			// {
			// ResultRate[m] = "0";
			// }
			// tCalAccClassRate = Double.parseDouble(ResultRate[m]);
			// System.out.println(tCalAccClassRate + "�ڶ��ε�������");
			// tmpInterst = tmpInterst * (1 + tCalAccClassRate);
			// // aAccClassInterest += tInsuAccClassBala * tCalAccClassRate;
			// }
			//
			// System.out.println(tInsuAccClassBala * (tmpInterst - 1)) ;
			// aAccClassInterest += tInsuAccClassBala * (tmpInterst - 1);
			// System.out.println("������˱�ʱ����Ϣ" + aAccClassInterest);

			// aAccClassInterest+=tInsuAccClassBala*getIntvRate(tInterval,tCalAccClassRate,"C");
			break;

		}
		return aAccClassInterest;
	}

	/**
	 * �԰����Ѽƻ�������з�����˻��������˻����
	 *
	 * @param aPayPlanCode
	 *            String ����֮һ
	 * @param aInsuAccNo
	 *            String ����֮һ
	 * @param aPolNo
	 *            String ����֮һ
	 * @param aOtherNo
	 *            String ����֮һ
	 * @param aAccAscription
	 *            String ����֮һ
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

		// �õ����������ʻ�����
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		tLMRiskInsuAccDB.setInsuAccNo(aInsuAccNo);
		if (!tLMRiskInsuAccDB.getInfo()) {
			return aAccBalance;
		}

		switch (Integer.parseInt(tLMRiskInsuAccDB.getAccComputeFlag())) {
		case 0:

			// ����Ϣ
			aAccBalance = 0.0;
			break;
		case 1:

			// ���չ̶����ʵ�����Ϣ
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

			// �����¼�϶�ֻ��һ������Ϊ��������Ӧһ����¼
			for (int i = 1; i <= mLCInsureAccClassSet.size(); i++) {
				LCInsureAccTraceDB tLCInsureAccTraceDB = new LCInsureAccTraceDB();
				tLCInsureAccTraceDB.setPolNo(aPolNo);
				tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);
				tLCInsureAccTraceDB.setPayPlanCode(aPayPlanCode);
				tLCInsureAccTraceDB.setOtherNo(aOtherNo);
				tLCInsureAccTraceDB.setAccAscription(aAccAscription);
				// ���ݸ÷����ṩ��5���������õ�LCInsureAccTrace�б�
				// tLCInsureAccTraceDB.setOtherNo(mLCInsureAccSet.get(i).getOtherNo());
				mLCInsureAccTraceSet = tLCInsureAccTraceDB.query();
				for (int j = 1; j <= mLCInsureAccTraceSet.size(); j++) {
					// �޳�����������ΪLX�Ĺ켣��¼
					if (!mLCInsureAccTraceSet.get(j).getMoneyType()
							.equals("LX")) {
						// �õ�ʱ����
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
				// ����3����Ϣ��Ҫ��DB�б���
			}
			break;
		case 2:

			// ���չ̶����ʸ�����Ϣ
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
			mLCInsureAccClassSet = tLCInsureAccClassDB2.query(); // sizeΪ1
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
						// �õ�ʱ����
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

			// �������ʱ�����Ϣ

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

			// ˵��������5����ΪAccClass������

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
						// �õ��ֶεĵ�������
						String[] ResultRate = getMultiAccRate(aInsuAccNo,
								tLMRiskInsuAccDB.getSchema(),
								mLCInsureAccTraceSet.get(j).getPayDate(),
								aBalanceDate, aRateType, "S", aIntervalType);
						for (int m = 0; m < ResultRate.length; m++) {
							// �����Ӵ˶δ��룬��ӿ�ʼ
							if (ResultRate[m] == null) {
								ResultRate[m] = "0";
							}
							// ��ӽ���
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

			// �������ʱ�����Ϣ

			// �õ��������е�����
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
						// �õ��ֶεĵ�������
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
	 * ����һ�������µ������˻���Ϣ����˻����
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
	 * �����һ������ĳ�����˻����ֽ����
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
	 * ���ձ����õ��ʻ����ʻ������
	 *
	 * @param aOtherNo
	 *            String
	 * @param aInsuAccNo
	 *            String
	 * @param aPolNo
	 *            String
	 * @param aBalanceDate
	 *            String ��Ϣ����
	 * @param aRateType
	 *            String ȡ�������ʻ����ʵ�ʱ�䵥λ
	 * @param aIntervalType
	 *            String ��Ϣ��ʱ�䵥λ
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

		// �õ����������ʻ�����
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		tLMRiskInsuAccDB.setInsuAccNo(aInsuAccNo);
		if (!tLMRiskInsuAccDB.getInfo()) {
			return aAccBalance;
		}

		switch (Integer.parseInt(tLMRiskInsuAccDB.getAccComputeFlag())) {
		// ����Ϣ
		case 0:
			aAccBalance = 0.0;
			break;
		// ���չ̶����ʵ�����Ϣ
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
					// �õ�ʱ����
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
		// ���չ̶����ʸ�����Ϣ
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
					// �õ�ʱ����
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
		// �������ʱ�����Ϣ
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
					// �õ��ֶεĵ�������
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
		// �������ʱ�����Ϣ
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
					// �õ��ֶεĵ�������
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
	 * ���ձ����õ��ʻ����ʻ������
	 *
	 * @param aLCInsureAccSchema
	 *            LCInsureAccSchema
	 * @param aBalanceDate
	 *            String ��Ϣ����
	 * @param aRateType
	 *            String ȡ�������ʻ����ʵ�ʱ�䵥λ��
	 * @param aIntervalType
	 *            String ��Ϣ��ʱ�䵥λ��
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

		// �õ����������ʻ�����
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		tLMRiskInsuAccDB.setInsuAccNo(aLCInsureAccSchema.getInsuAccNo());
		if (!tLMRiskInsuAccDB.getInfo()) {
			return aAccBalance;
		}

		switch (Integer.parseInt(tLMRiskInsuAccDB.getAccComputeFlag())) {
		// ����Ϣ
		case 0:
			aAccBalance = 0.0;
			break;
		// ���չ̶����ʵ�����Ϣ
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
					// �õ�ʱ����
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
		// ���չ̶����ʸ�����Ϣ
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
					// �õ�ʱ����
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
		// �������ʱ�����Ϣ
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
					// �õ��ֶεĵ�������
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
		// �������ʱ�����Ϣ
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
					// �õ��ֶεĵ�������
					String[] ResultRate = getMultiAccRate(aLCInsureAccSchema
							.getInsuAccNo(), tLMRiskInsuAccDB.getSchema(),
							mLCInsureAccSet.get(i).getBalaDate(), aBalanceDate,
							aRateType, "C", aIntervalType);
					for (int m = 0; m < ResultRate.length; m++) {
						if (!(ResultRate[m] == null || ResultRate[m].equals(""))) {
							tCalAccRate = Double.parseDouble(ResultRate[m]);
							System.out.println("��m��ֵ��" + m + "ʱ��ֵ��"
									+ tCalAccRate);
							double tSubInterest = mLCInsureAccTraceSet.get(j)
									.getMoney()
									* tCalAccRate;
							System.out.println("��m��ֵ��" + m + "ʱ����Ϣ��"
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
	 * �ۼ���Ϣ����ʹ�� ���ձ����õ�ĳ���ʻ������
	 *
	 * @param aInsuAccNo
	 *            String
	 * @param aPolNo
	 *            String
	 * @param aBalanceDate
	 *            String ��Ϣ����
	 * @param aRateType
	 *            String ȡ�������ʻ����ʵ�ʱ�䵥λ��
	 * @param aIntervalType
	 *            String ��Ϣ��ʱ�䵥λ��
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

		// �õ����������ʻ�����
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		tLMRiskInsuAccDB.setInsuAccNo(aInsuAccNo);
		if (!tLMRiskInsuAccDB.getInfo()) {
			return aAccBalance;
		}

		switch (Integer.parseInt(tLMRiskInsuAccDB.getAccComputeFlag())) {
		case 0:

			// ����Ϣ
			aAccBalance = 0.0;
			break;
		case 1:

			// ���չ̶����ʵ�����Ϣ
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
					// �õ�ʱ����
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

			// ���չ̶����ʸ�����Ϣ
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
					// �õ�ʱ����
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

			// �������ʱ�����Ϣ
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
					// �õ��ֶεĵ�������
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

			// �������ʱ�����Ϣ

			// 2005-08-20 �����ע������ĳ��������µķ���������Ϣ
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
			// //�õ��ֶεĵ�������
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
	 * ���ձ����õ������ʻ������
	 *
	 * @param aPolNo
	 *            String
	 * @param aBalanceDate
	 *            String ��Ϣ����
	 * @param aRateType
	 *            String ȡ�������ʻ����ʵ�ʱ�䵥λ��
	 * @param aIntervalType
	 *            String ��Ϣ��ʱ�䵥λ��
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
			// �õ����������ʻ�����
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
			// ����Ϣ
			case 0:
				aAccBalance = 0.0;
				break;
			// ���չ̶����ʵ�����Ϣ
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
					// �õ�ʱ����
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
			// ���չ̶����ʸ�����Ϣ
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
					// �õ�ʱ����
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
			// �������ʱ�����Ϣ
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
					// �õ��ֶεĵ�������
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
			// �������ʱ�����Ϣ
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
					// �õ��ֶεĸ�������
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
	 * ��֪�ʻ����õ��ʻ�����Ϣ��ԭ�����ʻ�����Ĭ��Ϊ�꣩
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
		// �õ�ʱ����
		tInterval = PubFun.calInterval(aStartDate, aEndDate, aIntervalType);
		// �õ����������ʻ�����
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		tLMRiskInsuAccDB.setInsuAccNo(aInsuAccNo);
		if (!tLMRiskInsuAccDB.getInfo()) {
			return aAccInterest;
		}

		switch (Integer.parseInt(tLMRiskInsuAccDB.getAccComputeFlag())) {
		case 0:

			// ����Ϣ
			aAccInterest = 0.0;
			break;
		case 1:

			// ���չ̶����ʵ�����Ϣ
			tAccRate = tLMRiskInsuAccDB.getAccRate();
			tCalAccRate = TransAccRate(tAccRate, aRateType, "S", aIntervalType);
			aAccInterest = aOriginAccBalance * tCalAccRate * tInterval;
			break;
		case 2:

			// ���չ̶����ʸ�����Ϣ
			tAccRate = tLMRiskInsuAccDB.getAccRate();
			tCalAccRate = TransAccRate(tAccRate, aRateType, "C", aIntervalType);
			aAccInterest = aOriginAccBalance * tCalAccRate * tInterval;
			break;
		case 3:

			// �������ʱ�����Ϣ
			tAccRate = AccountManage.getAccRate(tLMRiskInsuAccDB.getSchema());
			tCalAccRate = TransAccRate(tAccRate, aRateType, "S", aIntervalType);
			aAccInterest = aOriginAccBalance * tCalAccRate * tInterval;
			break;
		case 4:

			// �������ʱ�����Ϣ
			tAccRate = AccountManage.getAccRate(tLMRiskInsuAccDB, aEndDate);
			tCalAccRate = TransAccRate(tAccRate, aRateType, "C", aIntervalType);

			// System.out.println(tCalAccRate + "��һ�ε�������");
			aAccInterest = aOriginAccBalance * tCalAccRate * tInterval;
			break;
		default:
			aAccInterest = 0.0;
			break;
		}
		return aAccInterest;
	}

	/**
	 * ��֪�ʻ����õ��ʻ�����Ϣ�����������ʻ����ʣ�
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

		// �õ�ʱ����
		tInterval = PubFun.calInterval(aStartDate, aEndDate, aIntervalType);
		// �õ����������ʻ�����
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		tLMRiskInsuAccDB.setInsuAccNo(aInsuAccNo);
		if (!tLMRiskInsuAccDB.getInfo()) {
			return aAccInterest;
		}

		switch (Integer.parseInt(tLMRiskInsuAccDB.getAccComputeFlag())) {
		case 0:

			// ����Ϣ
			aAccInterest = 0.0;
			break;
		case 1:

			// ���չ̶����ʵ�����Ϣ
			tAccRate = tLMRiskInsuAccDB.getAccRate();
			tCalAccRate = TransAccRate(tAccRate, aRateType, "S", aIntervalType);
			aAccInterest = aOriginAccBalance * tCalAccRate * tInterval;
			break;
		case 2:

			// ���չ̶����ʸ�����Ϣ
			tAccRate = tLMRiskInsuAccDB.getAccRate();
			tCalAccRate = TransAccRate(tAccRate, aRateType, "C", aIntervalType);
			aAccInterest = aOriginAccBalance * tCalAccRate * tInterval;
			break;
		case 3:

			// �������ʱ�����Ϣ
			tAccRate = AccountManage.getAccRate(tLMRiskInsuAccDB.getSchema(),
					aRateType);
			tCalAccRate = TransAccRate(tAccRate, aRateType, "S", aIntervalType);
			aAccInterest = aOriginAccBalance * tCalAccRate * tInterval;
			break;
		case 4:

			// �������ʱ�����Ϣ
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
	 * �õ��������е�����
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
		// Ĭ��Ϊ������
		tCalculator.addBasicFactor("RateType", "Y");
		// �������������
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
	 * ���������͵õ��������е�����
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
		// �������������
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
	 * �õ��������е����ʣ���ȷ��
	 *
	 * @param cLMRiskInsuAccDB
	 *            LMRiskInsuAccDB ���ֱ����ʻ�
	 * @param cEndDate
	 *            String ���ʲ�ѯ��������
	 * @return double
	 */
	private static double getAccRate(LMRiskInsuAccDB cLMRiskInsuAccDB,
			String cEndDate) {
		// ��������Ĭ��ֵ
		double aAccRate = 0.0;

		ExeSQL tExeSQL = new ExeSQL();
		// �����ʻ����͡��������ڣ���ѯ����
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
	 * ����ת������
	 *
	 * @param OriginRate
	 *            double ԭʼ����
	 * @param OriginRateType
	 *            String ԭʼ�������ͣ������ʣ�"Y")��������("M")��������("D")
	 * @param TransType
	 *            String ����ת��("C")compound������ת��("S")simple
	 * @param DestRateType
	 *            String �����ʣ�������,������
	 * @return double ���ӣ�TransAccRate(0.48,"Y","C","D") ��0.48���긴����ת��Ϊ�ո���
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
		// �ж�OriginRateType������ԭʼ�������ͣ��ꡢ�¡��գ�
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

		// ��������
		if (TransType.equals("C")) {
			// �긴��ת��
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
			// �¸���ת��
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
			// �ո���ת��
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
		// ��������
		else if (TransType.equals("S")) {
			// �굥��ת��
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
			// �µ���ת��
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
			// �յ���ת��
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
		// ����ת�����ʵ������
		return DestRate;
	}

	/**
	 * ����ת������ add by frost 2007-10-09 ���ܰ�ÿ��30�죬ÿ��365����㣬��Ӧ�ð���ʵ�ʵ�����ת��
	 *
	 * @param OriginRate double ԭʼ����
	 * @param OriginRateType String ԭʼ�������ͣ������ʣ�"1")��������("2")��������("3")
	 * @param TransType String ����ת��("C")compound������ת��("S")simple
	 * @param DestRateType String ת������������� �����ʣ�"Y")��������("M")��������("D")
	 * @param MonthLength int �����������ڵ��·�����
	 * @param YearLength int �����������ڵ���������
	 * @return double ���ӣ�TransAccRate(0.48,"1","C","D",30,365) ��0.48���긴����ת��Ϊ�ո���
	 */
	public static double TransAccRate(double OriginRate, String OriginRateType,
			String TransType, String DestRateType, int MonthLength, int YearLength) {
		double DestRate = 0;
		double aPower;

		// �ж�OriginRateType������ԭʼ�������ͣ��ꡢ�¡��գ�
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

		// ��������
		if (TransType.equals("C")) {
			// �긴��ת��
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
			// �¸���ת��
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
			// �ո���ת��
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
		// ��������
		else if (TransType.equals("S")) {
			// �굥��ת��
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
			// �µ���ת��
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
			// �յ���ת��
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
		// ����ת�����ʵ������
		return DestRate;
	}


	/**
	 * ת������ʱ�����ֽ��ֵ
	 *
	 * @param OriginValue
	 *            double ԭ���ֽ�
	 * @param OriginDate
	 *            String ԭ��ʱ��
	 * @param GivenDate
	 *            String ����ʱ���
	 * @return double ���ӣ�TransCashValue(1000,"2006-12-31","2005-12-31")
	 *         ��2006-12-31��1000Ԫ��������������Ϊ2005-12-31�ļ�ֵ
	 *         ����2006���������Ϊi��2005-12-31�ļ�ֵv = 1000 / ( 1 + i * ( "2006-12-31" -
	 *         "2005-12-31" ) / 365 )
	 */
	public static double TransCashValue(double OriginValue, String OriginDate,
			String GivenDate) {
		if (PubFun.calInterval(GivenDate, OriginDate, "D") > 0) {
			double tmpIntrest = 1;
			// ������just����
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
			// ������just����
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
	 * �õ���Ϣ����ʻ���¼
	 *
	 * @return LCInsureAccSet
	 */
	public LCInsureAccSet getInsureAcc() {
		return mLCInsureAccSet;
	}

	/**
	 * �õ��ֶν�Ϣ���������(δ����)
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
				// @@������
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
			// �õ��������е�ʱ�����Ϣ
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

			// �õ���Ϣ���������ٽ���
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
			// @@������
			CError tError = new CError();
			tError.moduleName = "AccountManage";
			tError.functionName = "getMultiAccInterest";
			tError.errorMessage = e.toString();
			this.mErrors.addOneError(tError);

		}
		return ResultArray;
	}

	/**
	 * �õ��ֶν�Ϣ�Ĳ������Ƽ���
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
				// @@������
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
				// ��Ϣ�㳬��ĳ�������ٽ��
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
			// @@������
			CError tError = new CError();
			tError.moduleName = "AccountManage";
			tError.functionName = "getMultiAccInterest";
			tError.errorMessage = e.toString();
			this.mErrors.addOneError(tError);

		}
		return ResultArray;
	}

	/**
	 * �����20050613���
	 *
	 * @param TableName
	 *            String Ҫ��ѯ�����ʱ�����ldbankrate,interest000001��
	 * @param aOriginBalaDate
	 *            String �ϴν�Ϣ���ڻ�Դ����
	 * @param aBalaDate
	 *            String Ŀ���Ϣ����
	 * @param aRateType
	 *            String Դ�������ͣ����簴�����ʣ������ʣ������ʵ�
	 * @param aTransType
	 *            String ת�����ͣ���������
	 * @param aIntervalType
	 *            String Ŀ���������ͣ����簴�����ʣ������ʣ������ʵ�
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
		// ��ӽ���
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
				// ��Ϣ�㳬��ĳ�������ٽ��
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
			// @@������
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
	 * �õ��ֶν�Ϣ�Ĳ������Ƽ���
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
	 *            String ԭ�������ʵ�����
	 * @param aTransType
	 *            String ��������
	 * @param aIntervalType
	 *            String ת������������
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
		// ��ȡ��Ϣ�ķ�ʽ��ͬ��һ����ͨ��calmode��������ȡ��һ����ֱ�Ӳ�ѯ��Ϣ��
		if (aLMRiskInsuAccSchema.getAccCancelCode() != null
				&& !aLMRiskInsuAccSchema.getAccCancelCode().trim().equals("")) {
			try {
				tSql = PubFun1.getSQL(aLMRiskInsuAccSchema.getAccCancelCode(),
						tCalculator);
			} catch (Exception e) {
				e.printStackTrace();
				// @@������
				CError tError = new CError();
				tError.moduleName = "AccountManage";
				tError.functionName = "getMultiAccInterest";
				tError.errorMessage = e.toString();
				this.mErrors.addOneError(tError);
			}
		} else {
			TableName = aLMRiskInsuAccSchema.getAccRateTable();
			// �����ӿ�ʼ
			// if (TableName == null || TableName.equals("null")
			// || TableName.toUpperCase().equals("LDBANKRATE"))
			// {
			// tSql = "select * from LDBankRate where StartDate<='" +
			// aBalanceDate
			// + "' and EndDate>='" + aOriginBalaDate + "' order by EndDate";
			//
			// }
			// ��ӽ���

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
		// �ж��Ƿ��ѯ����Ч����
		if (tSSRS.getMaxRow() > 0) {
			tRate = Double.parseDouble(tSSRS.GetText(1, 1));
			tEndDate = tSSRS.GetText(1, 2);

			// ת����õ�ʵ������
			double tCalAccRate = TransAccRate(tRate, aRateType, aTransType,
					aIntervalType);

			tStartDate = aOriginBalaDate;

			// ��Ϣ�㳬��ĳ�������ٽ��
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
		// //ת����õ�ʵ������
		// double tCalAccRate = TransAccRate(tValue, aRateType, aTransType,
		// aIntervalType);
		// // tResultArray[i - 1] = String.valueOf(tCalAccRate);
		// if (i == 1)
		// {
		// tStartDate = aOriginBalaDate;
		// }
		// //��Ϣ�㳬��ĳ�������ٽ��
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
		// // @@������
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
	 * �õ��ʻ������ֶν�Ϣ�Ĳ������Ƽ���
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
	 *            String ��������
	 * @param aIntervalType
	 *            String ת������������
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
				// @@������
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
					// ��Ϣ�㳬��ĳ�������ٽ��
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
				// @@������
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
	 * �õ���������ֶν�Ϣ�Ĳ������Ƽ���
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
	 *            String ��������
	 * @param aIntervalType
	 *            String ת������������
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
				// @@������
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
					// ��Ϣ�㳬��ĳ�������ٽ��
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
				// @@������
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
	 * ������Ϣ����,������ʼ���ڵ���ֹ����֮��ı�����Ϣ
	 *
	 * @param aBalance
	 *            ���㱾��
	 * @param startDate
	 *            ��ʼ����
	 * @param endDate
	 *            ��ֹ����
	 * @param interestType
	 *            ��Ϣ���� (����Ĳμ� --> ldintegrationrate.ratekind )
	 * @return ���ؼ�������Ϣ
	 */
	public double commonGetComplexInterest(double aBalance, String startDate,
			String endDate, String _ratekind) {

		double oldMoney = aBalance;
		double tInterest = 0.0;
		// �������
		int intvYears = PubFun.calInterval(startDate, endDate, "Y");
		// ��������ۼ���Ϣ
		// ����������С�� 1��,���յ������㣬�������������1�꣬��1�갴�ո������㣬ʣ���������յ�������
		// ͬʱ������ĳ����Ϣ�����ڴ��ڶ���ʵı䶯����ʱ���ղ�ͬ���ʷֶμ���
		if (intvYears < 1) {
			tInterest = commonGetOneYearInterest(startDate, aBalance, endDate,
					"S", "D", _ratekind);
			aBalance += tInterest;
		} else if (intvYears > 0) {
			for (int j = 0; j < intvYears; j++) {
				String startDate_i = PubFun.getLastDate(startDate, j, "Y");
				String endDate_i = PubFun.getLastDate(startDate, j + 1, "Y");
				// endDate_i = PubFun.getLastDate(endDate_i,-1,"D");
				// �ۼ���Ϣ,������
				tInterest = commonGetOneYearInterest(startDate_i, aBalance,
						endDate_i, "S", "D", _ratekind);
				aBalance += tInterest;
			}
			// ��ȥ������ʣ������
			int leaveDays = PubFun.calInterval(PubFun.getLastDate(startDate,
					intvYears, "Y"), endDate, "D");
			if (leaveDays > 0) {
				tInterest = commonGetOneYearInterest(PubFun.getLastDate(
						startDate, intvYears, "Y"), aBalance, endDate, "S",
						"D", _ratekind);
				aBalance += tInterest;
			}
		}

		// ���յ���Ϣ
		tInterest = aBalance - oldMoney;
		return tInterest;
	}

	/**
	 * �������ٷ��˻�����Ϣ���㷽�� �÷���������ֶΣ��ֶκ󽻸�getOneYearInterest����
	 *
	 * @param startDate
	 *            String ��Ϣ��ʼ����
	 * @param aBalance
	 *            double ����
	 * @param endDate
	 *            String ֹϢ����
	 * @return double
	 * @author lanjun 2005/11/17
	 */
	public double getMultiLoanInterest(String startDate, double aBalance,
			String endDate)

	{
		double oldMoney = aBalance;
		double tInterest = 0.0;
		// �������
		int intvYears = PubFun.calInterval(startDate, endDate, "Y");
		// ��������ۼ���Ϣ
		// ����������С�� 1��,���յ������㣬�������������1�꣬��1�갴�ո������㣬ʣ���������յ�������
		// ͬʱ������ĳ����Ϣ�����ڴ��ڶ���ʵı䶯����ʱ���ղ�ͬ���ʷֶμ���
		if (intvYears < 1) {
			tInterest = getOneYearInterest(startDate, aBalance, endDate, "S",
					"D");
			aBalance += tInterest;
		} else if (intvYears > 0) {
			for (int j = 0; j < intvYears; j++) {
				String startDate_i = PubFun.getLastDate(startDate, j, "Y");
				String endDate_i = PubFun.getLastDate(startDate, j + 1, "Y");
				// endDate_i = PubFun.getLastDate(endDate_i,-1,"D");
				// �ۼ���Ϣ,������
				tInterest = getOneYearInterest(startDate_i, aBalance,
						endDate_i, "S", "D");
				aBalance += tInterest;
			}
			// ��ȥ������ʣ������
			int leaveDays = PubFun.calInterval(PubFun.getLastDate(startDate,
					intvYears, "Y"), endDate, "D");
			if (leaveDays > 0) {
				tInterest = getOneYearInterest(PubFun.getLastDate(startDate,
						intvYears, "Y"), aBalance, endDate, "S", "D");
				aBalance += tInterest;
			}
		}

		// ���յ���Ϣ
		tInterest = aBalance - oldMoney;
		return tInterest;
	}

	/**
	 * ���ݲ�ͬ�� (��������/��ʼ����/��ֹ����/����/��������/������) ��������ص���Ϣ
	 *
	 * @param startDate
	 *            ��ʼ����
	 * @param aBalance
	 *            ����
	 * @param endDate
	 *            ��ֹ����
	 * @param aComputeType
	 *            ��������
	 * @param aIntervalType
	 *            ������
	 * @param ratekind
	 *            ��������
	 * @return ����һ�����Ϣ
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
				// ���ĳ����Ϣ���ڱ��ڽ�Ϣ�����ڣ����ȼ���ǰһ����Ϣ
				if (PubFun.calInterval(rateEndDate, endDate, aIntervalType) > 0) {
					int tInterval = PubFun.calInterval(rateStartDate,
							rateEndDate, aIntervalType);
					if (tInterval > 0) {
						// ������ת��Ϊ��������
						tCalAccRate = TransAccRate(aValue, aRateIntv,
								aComputeType, aIntervalType);
						// ���㵥����Ϣ
						aResult = getIntvRate(tInterval, tCalAccRate,
								aComputeType);
						tInterest += aBalance * aResult;
					}
					rateStartDate = rateEndDate;
				} else {
					// ����1��ĵ����͸������
					int tInterval = PubFun.calInterval(rateStartDate, endDate,
							aIntervalType);
					if (tInterval > 0) {
						// ����������1���������������Ϊʱ��Ϊ1��
						// ������ʱ1��ĸ��������ڵ���
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
	 * �������ٷ��˻�����Ϣ���㷽�� �ֶμ�Ϣ���ֶβ�����1�� �÷��������긴�����㷽�����������겿�ְ��������㣬����һ��İ���������
	 *
	 * @param startDate
	 *            String ��Ϣ��ʼ����
	 * @param aBalance
	 *            double ����
	 * @param endDate
	 *            String ֹϢ����
	 * @param aComputeType
	 *            String ��������
	 * @param aIntervalType
	 *            String ������ͣ����簴�����ʣ������ʣ������ʵ�
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
				// ���ĳ����Ϣ���ڱ��ڽ�Ϣ�����ڣ����ȼ���ǰһ����Ϣ
				if (PubFun.calInterval(rateEndDate, endDate, aIntervalType) > 0) {
					int tInterval = PubFun.calInterval(rateStartDate,
							rateEndDate, aIntervalType);
					if (tInterval > 0) {
						// ������ת��Ϊ��������
						tCalAccRate = TransAccRate(aValue, aRateIntv,
								aComputeType, aIntervalType);
						// ���㵥����Ϣ
						aResult = getIntvRate(tInterval, tCalAccRate,
								aComputeType);
						tInterest += aBalance * aResult;
					}
					rateStartDate = rateEndDate;
				} else {
					// ����1��ĵ����͸������
					int tInterval = PubFun.calInterval(rateStartDate, endDate,
							aIntervalType);
					if (tInterval > 0) {
						// ����������1���������������Ϊʱ��Ϊ1��
						// ������ʱ1��ĸ��������ڵ���
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
	 * �õ������������ʽ�Ϣ�ֶν�Ϣ�Ĳ������Ƽ���
	 *
	 * @param aRateType
	 *            String
	 * @param aBalance
	 *            double ԭ���
	 * @param aOriginBalaDate
	 *            String ��Ϣ����
	 * @param aBalanceDate
	 *            String ��Ϣֹ��
	 * @param aTransType
	 *            String ��������
	 * @param aIntervalType
	 *            String ת������������
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
				// ��Ϣ�㳬��ĳ�������ٽ��
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
			// @@������
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
	 * �ʻ�������
	 *
	 * @return boolean
	 */
	public boolean updAccBalance() {

		Connection conn = null;
		conn = DBConnPool.getConnection();

		if (conn == null) {
			// @@������
			CError tError = new CError();
			tError.moduleName = "AccountManage";
			tError.functionName = "saveData";
			tError.errorMessage = "���ݿ�����ʧ��!";
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
					// @@������
					CError tError = new CError();
					tError.moduleName = "AccountManage";
					tError.functionName = "saveData";
					tError.errorMessage = "���ʻ�����ʧ��!";
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
					// @@������
					CError tError = new CError();
					tError.moduleName = "AccountManage";
					tError.functionName = "saveData";
					tError.errorMessage = "�ʻ�������ʧ��!";
					this.mErrors.addOneError(tError);
					conn.rollback();
					conn.close();
					return false;
				}
			}
			conn.commit();
			conn.close();
		} catch (Exception ex) {
			// @@������
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
	 * �õ���Ϣ����ʻ��켣
	 *
	 * @return LCInsureAccTraceSet
	 */
	public LCInsureAccTraceSet getInsureAccTrace() {
		return mLCInsureAccTraceSet;
	}

	/**
	 * �õ��̶�������Ϣ
	 *
	 * @param OriginBalance
	 *            double ԭʼ���
	 * @param aStartDate
	 *            String ��ʼ����
	 * @param aEndDate
	 *            String ��ֹ����
	 * @param OriginRate
	 *            double ԭʼ����
	 * @param OriginRateType
	 *            String ԭʼ�������ͣ������ʣ�"Y")��������("M")��������("D")
	 * @param TransType
	 *            String ����ת��("C")compound������ת��("S")simple
	 * @param DestRateType
	 *            String �����ʣ�������,������
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
	 * ȡ����Ϣ
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
	 * �õ�����ʱ�����ı���
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

	// ---------------------------------------2007-04-10 ����̩���Ľ�Ϣ����
	// ���������յĽ�Ϣ-------------------------------//
	/**
	 * �����µ��ʻ���Ϣ���߼����д��� �����ʻ���������Ϣ
	 *
	 * @param aBalaDate
	 *            ���ν�Ϣ����
	 * @param aRateType
	 *            ԭʼ�������ͣ����������ʣ������ʵ�
	 * @param aIntvType
	 *            Ŀ���������ͣ����������ʣ������ʵ�
	 * @param Period
	 *            �����ڼ�
	 * @param tPeriodFlag
	 *            �������ͣ�����or���ڣ�
	 * @param Depst
	 *            ������־ ������: �������ڣ�2006-02-16
	 * @return double ���ص����ʻ���������Ӧ�ļǼ����������Ϣ���Լ������ �޸��ˣ� �޸����ڣ�2006-02-24
	 *         �޸����ݣ���������޸�
	 */
	public TransferData getAccClassInterestNew(
			LCInsureAccClassSchema aLCInsureAccClassSchema, String aBalaDate,
			String aRateType, String aIntvType, int Period, String tType,
			String Depst) {

		System.out.println("=====This is getAccClassInterestNew!=====\n");

		CachedRiskInfo mCRI = CachedRiskInfo.getInstance();
		// ��¼�ʻ���������Ϣֵ
		double aAccClassInterest = 0.0;

		// ��¼�ʻ������ı�Ϣ��
		double aAccClassSumPay = 0.0;

		// ��¼����ֵ��Ϣ�ͱ�Ϣ
		TransferData aAccClassRet = new TransferData();

		// ����������Ч��
		if (!verifyNotNull("��ǰ��Ϣ����", aBalaDate)) {
			returnNull(aAccClassRet);
			return aAccClassRet;
		}
		if (!verifyNotNull("ԭʼ��������", aRateType)) {
			returnNull(aAccClassRet);
			return aAccClassRet;
		}
		if (!verifyNotNull("Ŀ����������", aIntvType)) {
			returnNull(aAccClassRet);
			return aAccClassRet;
		}

		// ��¼��Ϣ���
		int tInterval = 0;

		// ��¼��ѯLCInsureAccClassTrace������Ч��¼�ĸ���
		int tCount = 0;

		// ��¼ԭʼ����ֵ
		double tAccClassRate;

		// ��¼Ŀ������ֵ
		double tCalAccClassRate;

		// ��¼�����ʻ��ֽ����
		double tInsuAccClassBala = aLCInsureAccClassSchema.getInsuAccBala(); // ��¼�����ʻ��ֽ����

		// �õ����ֱ����ʻ�������(lmRiskInsuAcc)���ʻ�����
		// LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();

		// ��¼�����ʻ�����
		String aInsuAccNo = aLCInsureAccClassSchema.getInsuAccNo();
		if (!verifyNotNull("�����ʻ�����", aInsuAccNo)) {
			returnNull(aAccClassRet);
			return aAccClassRet;
		}

		// ��¼�ʻ�������ϴν�Ϣ����
		String tBalaDate = aLCInsureAccClassSchema.getBalaDate();

		// ��¼�ʻ�������ϴν�Ϣʱ��
		String tBalaTime = aLCInsureAccClassSchema.getBalaTime();

		// �ϴν�Ϣ����Ϊ�ջ��߲�������ȡ�������
		if (tBalaDate == null || tBalaDate.equals("")) {
			tBalaDate = aLCInsureAccClassSchema.getMakeDate();
		}

		// �ϴν�Ϣʱ�����Ϊ�ջ��߲�������ȡ���ʱ��
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
		System.out.println("************��ʼ��Ϣ****************");
		System.out.println("�����ʻ��ֽ����: " + tInsuAccClassBala);
		System.out.println("���ν�Ϣ����: " + aBalaDate);
		System.out.println("�ϴν�Ϣ����: " + tBalaDate);
		System.out.println("�ϴν�Ϣʱ��: " + tBalaTime);
		// ----------testing end--------------------------------

		switch (Integer.parseInt(tLMRiskInsuAccSchema.getAccComputeFlag())) {

		// ����Ϣ
		case 0:
			break;

		// �������ʵ����㷨
		case 1:

			// ��ȡ�ʻ��̶�����AccRate
			tAccClassRate = tLMRiskInsuAccSchema.getAccRate();

			// ��ԭʼ�̶�����ת��ΪĿ��̶�����
			tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "S",
					aIntvType);

			String tAccKind = tLMRiskInsuAccSchema.getAccKind();
			// �Ƚ��ȳ��������⴦��
			String tOtherNo = aLCInsureAccClassSchema.getOtherNo();
			if (!tAccKind.equals("") && tAccKind.equals("3")) {
				if (tOtherNo.equals("")) {
					CError.buildErr(this, "�����ʻ���������ݲ�������");
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

				// ���LCInsureAccTrace���е�moneyType�ֶε�ֵΪPX����SX,������Ϣ;
				String tMoneyType = tmpLCInsuAccTraceDB.getMoneyType();
				if (tMoneyType.equals("PX") || tMoneyType.equals("SX")) {
					continue;
				}

				// ��¼�ɷ�����
				String payDate = tmpLCInsuAccTraceDB.getPayDate();

				// ������Чʱ��
				String tMakeTime = tmpLCInsuAccTraceDB.getMakeTime();

				// ���ڱȽ�lcinsureaccclass���baladate��lcinsuretrace���paydate��ֵ�Ĵ�С
				int tIntv = PubFun.calInterval(tBalaDate, payDate, aIntvType);
				if (tIntv >= 0) {

					// ����켣����paydate >= �ʻ������Ľ�Ϣ����BalaDate
					// ���������ϴν�Ϣ�����Ľ����ܶ����Ϣ
					if (tIntv == 0 && tBalaTime.compareTo(tMakeTime) >= 0) {
						continue;
					}

					// �ݴ�һ�ʽɷ�
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;

					// Ȼ��Թ켣������ǮtempMoney����Ϣ
					if (payDate.compareTo(aBalaDate) > 0) {
						tInterval = PubFun.calInterval(aBalaDate, payDate,
								aIntvType);
						tInterval = 0 - tInterval;
					} else {
						tInterval = PubFun.calInterval(payDate, aBalaDate,
								aIntvType);
					}

					// jixf chg 20060722������ʱ�������������ط���Ϣ
					aAccClassInterest += tempMoney
							* getIntvRate(tInterval, tCalAccClassRate, "S");
					aAccClassInterest = Arith.round(aAccClassInterest, 2);
				}
			}

			// ���ʻ���������һ��������Ϣ
			tInterval = PubFun.calInterval(tBalaDate, aBalaDate, aIntvType);
			if (tInterval > 0)
				aAccClassInterest += tInsuAccClassBala
						* getIntvRate(tInterval, tCalAccClassRate, "S");
			aAccClassInterest = Arith.round(aAccClassInterest, 2);
			break;

		// �������ʸ����㷨
		case 2:

			// ��ȡ�̶�����
			tAccClassRate = tLMRiskInsuAccSchema.getAccRate();

			// ת���������ֵ
			tCalAccClassRate = TransAccRate(tAccClassRate, aRateType, "C",
					aIntvType);

			tAccKind = tLMRiskInsuAccSchema.getAccKind();

			// �ֺ������⴦��
			tOtherNo = aLCInsureAccClassSchema.getOtherNo();
			if (!tAccKind.equals("") && tAccKind.equals("3")) {
				if (tOtherNo.equals("")) {
					CError.buildErr(this, "�����ʻ���������ݲ�������");
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

				// ���LCInsureAccTrace���е�moneyType�ֶε�ֵΪPX����SX,������Ϣ;
				String tMoneyType = tmpLCInsuAccTraceDB.getMoneyType();
				if (tMoneyType.equals("PX") || tMoneyType.equals("SX")) {
					continue;
				}

				// ��������
				String payDate = tmpLCInsuAccTraceDB.getPayDate();

				// ������Чʱ��
				String tMakeTime = tmpLCInsuAccTraceDB.getMakeTime();

				double tempMoney = tmpLCInsuAccTraceDB.getMoney();
				tInsureAccTraceMoneySum += tempMoney;

				// Ȼ��Թ켣������ǮtempMoney����Ϣ
				// jixf add 20050802 ����PayDate>aBalaDate�������tInterval�����⣬��֮û������
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
				// jixf chg 20060722������ʱ�������������ط���Ϣ
				aAccClassInterest += tempMoney
						* getIntvRate(tInterval, tCalAccClassRate, "C");
				System.out.println("=======" + tInterval);
				System.out.println("=======" + tInsuAccClassBala);
				System.out.println("======="
						+ getIntvRate(tInterval, tCalAccClassRate, "C"));
				aAccClassInterest = Arith.round(aAccClassInterest, 2);

				// }
			}

			// ���ʻ���������һ��������Ϣ
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

		// �������ʵ����㷨
		case 3:

			tAccKind = tLMRiskInsuAccSchema.getAccKind();
			// �ֺ������⴦��
			tOtherNo = aLCInsureAccClassSchema.getOtherNo();
			if (!tAccKind.equals("") && tAccKind.equals("3")) {
				if (tOtherNo.equals("")) {
					CError.buildErr(this, "�����ʻ���������ݲ�������");
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

				// ���LCInsureAccTrace���е�moneyType�ֶε�ֵΪPX����SX,������Ϣ;
				String tMoneyType = tmpLCInsuAccTraceDB.getMoneyType();
				if (tMoneyType.equals("PX") || tMoneyType.equals("SX")) {
					continue;
				}

				// ��¼��������
				String payDate = tmpLCInsuAccTraceDB.getPayDate();

				// ��¼������Чʱ��
				String tMakeTime = tmpLCInsuAccTraceDB.getMakeTime();

				// ���ڱȽ�lcinsureaccclass���baladate��lcinsuretrace���paydate��ֵ�Ĵ�С
				int tIntv = PubFun.calInterval(tBalaDate, payDate, aIntvType);
				if (tIntv >= 0) {

					// ����켣����paydate>=�ʻ������Ľ�Ϣ����BalaDate
					if (tIntv == 0 && tBalaTime.compareTo(tMakeTime) >= 0) {
						continue;
					}

					// ��ʱ���LCInsuAccTraceDB���е��ʻ��ɷ�
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;

					// �Թ켣������ǮtempMoney����Ϣ
					String[] ResultRate2 = getMultiAccRateNew(aInsuAccNo,
							tLMRiskInsuAccSchema, payDate, aBalaDate,
							aRateType, "S", aIntvType, Period, tType, Depst);

					// ��¼ResultRate2[]����Ч���鳤��
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

			// ���ʻ���������һ��������Ϣ
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

		// �������ʸ����㷨
		case 4:
			System.out.println("=====������Ϣ��ʼ=====\n");

			tAccKind = tLMRiskInsuAccSchema.getAccKind();

			// �ֺ������⴦��
			tOtherNo = aLCInsureAccClassSchema.getOtherNo();
			if (!tAccKind.equals("") && tAccKind.equals("3")) {
				if (tOtherNo.equals("")) {
					CError.buildErr(this, "�����ʻ���������ݲ�������");
					returnNull(aAccClassRet);
					return aAccClassRet;
				}
				// tLCInsureAccTraceDB.setPayNo(tOtherNo);
			}
			tLCInsureAccTraceDB.setPolNo(aLCInsureAccClassSchema.getPolNo());
			tLCInsureAccTraceDB.setInsuAccNo(aInsuAccNo);

			// �ɷ���Դ(��ҵ�ɻ��Ǹ��˽�)
			tLCInsureAccTraceDB.setPayPlanCode(aLCInsureAccClassSchema
					.getPayPlanCode());
			tLCInsureAccTraceDB.setAccAscription(aLCInsureAccClassSchema
					.getAccAscription());
			tLCInsureAccTraceSet = tLCInsureAccTraceDB.query();

			// ===20060703===testing start======================
			System.out
					.println("һ����ѯ��trace��ļ�¼����" + tLCInsureAccTraceSet.size());
			// ===20060703===testing end=========================

			for (int i = 1; i <= tLCInsureAccTraceSet.size(); i++) {
				LCInsureAccTraceDB tmpLCInsuAccTraceDB = new LCInsureAccTraceDB();
				tmpLCInsuAccTraceDB.setSchema(tLCInsureAccTraceSet.get(i));

				// ���LCInsureAccTrace���е�moneyType�ֶε�ֵΪPX����SX,������Ϣ;
				String tMoneyType = tmpLCInsuAccTraceDB.getMoneyType();
				if (tMoneyType.equals("PX") || tMoneyType.equals("SX")) {
					continue;
				}
				String payDate = tmpLCInsuAccTraceDB.getPayDate();
				if (payDate == null || payDate.equals("")) {
					payDate = tmpLCInsuAccTraceDB.getMakeDate();
					if (payDate == null || payDate.equals("")) {
						CError.buildErr(this, "�ɷѼ�¼�Ľɷ�����Ϊ�գ�");
						returnNull(aAccClassRet);
						return aAccClassRet;
					}
				}

				// ������Чʱ��
				String tMakeTime = tmpLCInsuAccTraceDB.getMakeTime();

				// ==20060619===testing start===============
				System.out.println("��" + i + "����¼��");
				System.out.println("�ɷ����ڣ�" + payDate);
				System.out.println("�ɷ�ʱ�䣺" + tMakeTime);
				System.out.println("�ɷѽ�" + tmpLCInsuAccTraceDB.getMoney());
				// ==20060619==testind end===================

				// ���ڱȽ�lcinsureaccclass���baladate��lcinsuretrace���paydate��ֵ�Ĵ�С
				int tIntv = PubFun.calInterval(tBalaDate, payDate, aIntvType);
				if (tIntv >= 0) {

					// ����켣����paydate>=�ʻ������Ľ�Ϣ����BalaDate
					if (tIntv == 0 && tBalaTime.compareTo(tMakeTime) >= 0) {
						continue;
					}

					// ��ʱ����ʻ�һ�ʽɷѽ��
					double tempMoney = tmpLCInsuAccTraceDB.getMoney();
					tInsureAccTraceMoneySum += tempMoney;

					// Ȼ��Թ켣������ǮtempMoney����Ϣ
					String[] ResultRate2 = getMultiAccRateNew(aInsuAccNo,
							tLMRiskInsuAccSchema, payDate, aBalaDate,
							aRateType, "C", aIntvType, Period, tType, Depst);
					tCount = Integer.parseInt(ResultRate2[0]);

					// ====20060619==testing start=============
					System.out.println("�鵽" + tCount + "������ֵ");
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

			// ���ʻ���������һ��������Ϣ
			System.out.println("=====���ʻ���������һ��������Ϣ=====\n");
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
				System.out.println("���������ʣ�" + tCalAccClassRate);
				// ===20060703==testing end===========================

			}
			break;
		}
		aAccClassSumPay = tInsuAccClassBala + tInsureAccTraceMoneySum
				+ aAccClassInterest;
		// ׼�����ص����ݰ�
		aAccClassRet.setNameAndValue("aAccClassInterest", aAccClassInterest);
		aAccClassRet.setNameAndValue("aAccClassSumPay", aAccClassSumPay);

		return aAccClassRet;
	}

	/**
	 * ����Ϊ��У��
	 */
	private boolean verifyNotNull(String tVName, String tStrValue) {
		if (tStrValue == null || tStrValue.equals("")) {
			// @@������
			CError tError = new CError();
			tError.moduleName = "AccountManage";
			tError.functionName = "getAccClassInterestNew";
			tError.errorMessage = "���н�Ϣ����ʱ��" + tVName + "û�д��룡";
			this.mErrors.addOneError(tError);
		}

		return true;
	}

	/**
	 * ��Ϣ���ɹ��������� ��getAccClassInterestNew�ڴ��������Чʱ����
	 */
	private void returnNull(TransferData aAccClassRet) {
		aAccClassRet.setNameAndValue("tAccClassInterest", 0.0);
		aAccClassRet.setNameAndValue("tAccClassSumPay", 0.0);
	}

	/**
	 * * �õ��ֶν�Ϣ�Ĳ������Ƽ���
	 *
	 * @param aInsuAccNo
	 *            �����ʻ���
	 * @param aLMRiskInsuAccSchema
	 *            ���ֱ����ʻ���¼
	 * @param aOriginBalaDate
	 *            ��Ϣ��ʼ����
	 * @param aBalanceDate
	 *            ��Ϣ��������
	 * @param aRateType
	 *            ԭ�������ʵ�����
	 * @param aTransType
	 *            ����/����
	 * @param aIntervalType
	 *            ת������������
	 * @param aType
	 *            ����/����
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

				// @@������
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

				// ȡ���л�ȡ����
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

				// ȡ��������
				ResultArray = getTablRate(tSql, aRateType, TableName,
						aTransType, aIntervalType, aOriginBalaDate,
						aBalanceDate);
			}
		}

		return ResultArray;
	}

	/**
	 * * �õ��ʻ����ֽ�Ϣʱ������
	 *
	 * @param tSql
	 *            ��ѯ���ʵ�SQL���
	 * @param aOriginBalaDate
	 *            ��Ϣ��ʼ����
	 * @param aBalanceDate
	 *            ��Ϣ��������
	 * @param aRateType
	 *            ԭ�������ʵ����ͣ���/��/�գ�
	 * @param aTransType
	 *            ����/����
	 * @param aIntervalType
	 *            ת������������
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
			System.out.println("ֱ�Ӵӱ��в��������Ϊ" + aValue);
			// ===20060703===testing end====================

			// ԭʼ����ת��ΪĿ������
			double tCalAccRate = TransAccRate(aValue, aRateType, aTransType,
					aIntervalType);
			if (i == 1) {
				tStartDate = aOriginBalaDate;
			}

			// ��Ϣ�㳬��ĳ�������ٽ��
			if (PubFun.calInterval(aDate, aBalanceDate, aIntervalType) > 0) {
				int tInterval = PubFun.calInterval(tStartDate, aDate,
						aIntervalType);
				if (tInterval > 0) {

					// ����һ��ʱ�����µ�����
					aResult = getIntvRate(tInterval, tCalAccRate, aTransType);
					ResultArray[i] = String.valueOf(aResult);

					// ===20060703===testing start===============
					System.out.println("����������Ϊ��" + ResultArray[i]);
					// ===20060703===testing end=================

				}
				tStartDate = aDate;
			} else {
				int tInterval = PubFun.calInterval(tStartDate, aBalanceDate,
						aIntervalType);
				if (tInterval > 0) {

					// ����һ��ʱ�����µ�����
					aResult = getIntvRate(tInterval, tCalAccRate, aTransType);
					ResultArray[i] = String.valueOf(aResult);

					// ===20060703===testing start===============
					System.out.println("����������Ϊ��" + ResultArray[i]);
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
			System.out.println(tTabName + "���ѯ����Ϊ�գ�");
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

			// ��Ϣ�㳬��ĳ�������ٽ��
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

	// ---------------------------------------2007-04-10 ����̩���Ľ�Ϣ����
	// ���������յĽ�Ϣ-end------------------------------//

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
		// aLCInsureAccSchema.setAppntName("������ͨͶ����ѯ�������޹�˾");
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

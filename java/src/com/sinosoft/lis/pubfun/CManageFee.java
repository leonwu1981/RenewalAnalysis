/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.util.EmptyStackException;

import com.sinosoft.lis.db.LCGrpFeeDB;
import com.sinosoft.lis.db.LCGrpFeeParamDB;
import com.sinosoft.lis.db.LCGrpIvstPlanDB;
import com.sinosoft.lis.db.LCPerInvestPlanDB;
import com.sinosoft.lis.db.LCPolDB;
import com.sinosoft.lis.db.LCPremDB;
import com.sinosoft.lis.db.LDPromiseRateDB;
import com.sinosoft.lis.db.LMDutyDB;
import com.sinosoft.lis.db.LMDutyPayDB;
import com.sinosoft.lis.db.LMRiskAccPayDB;
import com.sinosoft.lis.db.LMRiskAppDB;
import com.sinosoft.lis.db.LMRiskFeeDB;
import com.sinosoft.lis.db.LMRiskInsuAccDB;
import com.sinosoft.lis.db.LCInsuredDB;
import com.sinosoft.lis.schema.LCGrpFeeParamSchema;
import com.sinosoft.lis.schema.LCGrpFeeSchema;
import com.sinosoft.lis.schema.LCInsureAccClassFeeSchema;
import com.sinosoft.lis.schema.LCInsureAccClassSchema;
import com.sinosoft.lis.schema.LCInsureAccFeeSchema;
import com.sinosoft.lis.schema.LCInsureAccFeeTraceSchema;
import com.sinosoft.lis.schema.LCInsureAccSchema;
import com.sinosoft.lis.schema.LCInsureAccTraceSchema;
import com.sinosoft.lis.schema.LCPolSchema;
import com.sinosoft.lis.schema.LCPremSchema;
import com.sinosoft.lis.schema.LCPremToAccSchema;
import com.sinosoft.lis.schema.LDPromiseRateSchema;
import com.sinosoft.lis.schema.LMRiskAccPaySchema;
import com.sinosoft.lis.schema.LMRiskFeeSchema;
import com.sinosoft.lis.vschema.LCDutySet;
import com.sinosoft.lis.vschema.LCGrpFeeParamSet;
import com.sinosoft.lis.vschema.LCGrpFeeSet;
import com.sinosoft.lis.vschema.LCInsureAccClassFeeSet;
import com.sinosoft.lis.vschema.LCInsureAccClassSet;
import com.sinosoft.lis.vschema.LCInsureAccFeeSet;
import com.sinosoft.lis.vschema.LCInsureAccFeeTraceSet;
import com.sinosoft.lis.vschema.LCInsureAccSet;
import com.sinosoft.lis.vschema.LCInsureAccTraceSet;
import com.sinosoft.lis.vschema.LCInsuredSet;
import com.sinosoft.lis.vschema.LCPremSet;
import com.sinosoft.lis.vschema.LCPremToAccSet;
import com.sinosoft.lis.vschema.LDPromiseRateSet;
import com.sinosoft.lis.vschema.LMDutyPaySet;
import com.sinosoft.lis.vschema.LMRiskAccPaySet;
import com.sinosoft.lis.vschema.LMRiskFeeSet;
import com.sinosoft.utility.*;


/**
 * <p>Title: Lis </p>
 * <p>Description: Life Insurance System</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Sinosoft Co. Ltd.</p>
 * @author WUJS
 * @version 6.0
 */
public class CManageFee
{
    /**��Ž��*/
    public VData mVResult = new VData();


    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();


    /** �����洫�����ݵ����� */
    private VData mInputData;

    /** �������sql��� */
    private MMap upDate=new MMap();
    
    /**���������*/
    private LCGrpFeeSet mLCGrpFeeSet;


    /**�������*/
    private LCPremSet mLCPremSet;


    /**����*/
    private LCPolSchema mLCPolSchema;
    
    /**���� */
    private LCDutySet mLCDutySet;
    
    public String mFeeType = "1";//���������


    /**�����˻�����ѷ����*/
    private LCInsureAccFeeTraceSet mLCInsureAccFeeTraceSet;
    private LCInsureAccClassFeeSet mLCInsureAccClassFeeSet;
    private LCInsureAccFeeSet mLCInsureAccFeeSet;
    private LCInsureAccSet mLCInsureAccSet;
    private LCPremToAccSet mLCPremToAccSet;
    private LCInsureAccClassSet mInsureAccClassSet;
    private LCInsureAccTraceSet mLCInsureAccTraceSet;


    /**��ȫ������ʶ*/
    private String mBQFlag = "";


    //add by frost �������Ҫ����,���ڼ�������
    private Calculator mCalculator = new Calculator();

    public CManageFee()
    {
    }

    public CManageFee(String tBQFlag)
    {
        mBQFlag = tBQFlag;
    }


    /**
     * ��ʼ��
     * @param cInputData VData
     */
    private void Initialize(VData cInputData)
    {
        mInputData = cInputData;
//        mLCGrpFeeSet = (LCGrpFeeSet) mInputData.getObjectByObjectName(
//                "LCGrpFeeSet", 0);
        mLCPolSchema = (LCPolSchema) mInputData.getObjectByObjectName(
                "LCPolSchema", 0);
        mLCDutySet = (LCDutySet)mInputData.getObjectByObjectName("LCDutySet", 0);
        mLCInsureAccClassFeeSet = (LCInsureAccClassFeeSet) mInputData.
                                  getObjectByObjectName(
                "LCInsureAccClassFeeSet", 0);
        mLCInsureAccFeeTraceSet = (LCInsureAccFeeTraceSet) mInputData.
        getObjectByObjectName(
                "LCInsureAccFeeTraceSet", 0);
        mLCInsureAccFeeSet = (LCInsureAccFeeSet) mInputData.
                             getObjectByObjectName(
                "LCInsureAccFeeSet", 0);
        mLCPremSet = (LCPremSet) mInputData.getObjectByObjectName("LCPremSet",
                0);
        //�õ����ɵı����ʻ���
        mLCInsureAccSet = (LCInsureAccSet) (mInputData.getObjectByObjectName(
                "LCInsureAccSet", 0));

        //�õ����ɵĽɷ��ʻ�������
        mLCPremToAccSet = (LCPremToAccSet) (mInputData.getObjectByObjectName(
                "LCPremToAccSet", 0));

        mInsureAccClassSet = (LCInsureAccClassSet) mInputData.
                             getObjectByObjectName(
                "LCInsureAccClassSet", 0);

        //�õ���ȡ�ʻ�������--Ŀǰ����
//        mLCGetToAccSet = (LCGetToAccSet) (mInputData
//                                          .getObjectByObjectName(
//                "LCGetToAccSet",
//                0));
        mLCInsureAccTraceSet = (LCInsureAccTraceSet) mInputData.
                               getObjectByObjectName(
                "LCInsureAccTraceSet", 0);
        //  if ( mLCInsureAccTraceSet==null ) mLCInsureAccTraceSet = new LCInsureAccTraceSet();
        if (mLCPolSchema == null)
        {
            CError.buildErr(this, "���뱣����Ϣ����Ϊ��");
            // return false;
        }
        else
        {
            //��ȫ׷�ӱ��շ�ʱ�������ѱ���
            if (mBQFlag != null && !mBQFlag.equals(""))
            {
                mLCGrpFeeSet = (LCGrpFeeSet) mInputData.
                               getObjectByObjectName("LCGrpFeeSet", 0);
            }
            else
            {
                LCGrpFeeDB tLCGrpFeeDB = new LCGrpFeeDB();
                tLCGrpFeeDB.setGrpPolNo(mLCPolSchema.getGrpPolNo());
                tLCGrpFeeDB.setRiskCode(mLCPolSchema.getRiskCode());
                mLCGrpFeeSet = tLCGrpFeeDB.query();
            }
        }

    }

    public VData getResult()
    {
//        mInputData.add(mLCInsureAccTraceSet);
//        return mInputData;
        return mInputData;
    }


    /**
     * ����У��
     * @return boolean
     */
    private boolean checkData()
    {

        if (mInputData == null)
        {
            System.out.println("���ȵ��ó�ʼ������:Initialize");
            CError.buildErr(this, "���ȵ��ó�ʼ������:Initialize");
            return false;
        }
        if (mLCPolSchema == null
            || mLCInsureAccClassFeeSet == null)
        {
            CError.buildErr(this, "��ʼ����Ϣ����ȷ!");
            return false;
        }

        if (mLCPremSet == null)
        {
            CError.buildErr(this, "���봫�뱣����Ϣ");
            return false;
        }

        if (mLCGrpFeeSet == null)
        {
            CError.buildErr(this, "��ʼ���������ֹ����������Ϣ");
            return false;
        }

        return true;

    }


    /**
     * �����ݿ��в�ѯ������Ӧ��Ҫ�����˻������б�����
     * @param tLCPolSchema LCPolSchema
     * @return LCPremSet
     */
    private LCPremSet queryLCPremSet(LCPolSchema tLCPolSchema)
    {
        LCPremSet tLCPremSet = new LCPremSet();
        LCPremDB tLCPremDB = new LCPremDB();
        tLCPremDB.setPolNo(tLCPolSchema.getPolNo());
        tLCPremDB.setNeedAcc("1");
        tLCPremSet = tLCPremDB.query();
        if (tLCPremDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLCPremDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLCPrem";
            tError.errorMessage = "��������ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLCPremSet.clear();

            return null;
        }
        if (tLCPremSet.size() == 0)
        {
            return null;
        }

        return tLCPremSet;
    }


    /**
     * ��ѯƥ��ı�����
     * @param payPlanCode String
     * @return LCPremSchema
     */
    private LCPremSchema getLCPremSchema(String payPlanCode)
    {
        if (mLCPremSet == null)
        {
            CError.buildErr(this, "û�б�������Ϣ!");
            return null;
        }
        for (int i = 1; i <= mLCPremSet.size(); i++)
        {
            if (mLCPremSet.get(i).getPayPlanCode().equals(payPlanCode))
            {
                return mLCPremSet.get(i);
            }
        }
        return null;
    }


    /**
     * ��ѯƥ��ı����˻�����ѷ����
     * @param InsureAccNo String
     * @param payPlanCode String
     * @return LCInsureAccClassFeeSchema
     */
    private LCInsureAccClassFeeSchema getLCInsureAccClassFee(String InsureAccNo,
            String payPlanCode)
    {
        LCInsureAccClassFeeSchema tLCInsureAccClassFeeSchema = null;
        for (int i = 1; i <= mLCInsureAccClassFeeSet.size(); i++)
        {
            tLCInsureAccClassFeeSchema = mLCInsureAccClassFeeSet.get(i);
            if (tLCInsureAccClassFeeSchema.getInsuAccNo().equals(InsureAccNo)
                &&
                tLCInsureAccClassFeeSchema.getPayPlanCode().equals(payPlanCode))
            {
                break;
            }
        }
        return tLCInsureAccClassFeeSchema;
    }


    /**
     * ��ѯƥ��ı����˻�����ѷ����
     * @param InsureAccNo String
     * @return LCInsureAccFeeSchema
     */
    private LCInsureAccFeeSchema getLCInsureAccFee(String InsureAccNo)
    {
        LCInsureAccFeeSchema tLCInsureAccFeeSchema = null;
        for (int i = 1; i <= mLCInsureAccClassFeeSet.size(); i++)
        {
            if (mLCInsureAccFeeSet.get(i).getInsuAccNo().equals(InsureAccNo))
            {
                tLCInsureAccFeeSchema = mLCInsureAccFeeSet.get(i);
                break;
            }
        }
        return tLCInsureAccFeeSchema;
    }


    private boolean createFeeTrace(
            LCInsureAccClassFeeSchema pLCInsureAccClassFeeSchema,
            double dMoney,
            String sMoneyType,
            String sFeeCode)
    {
        Reflections ref = new Reflections();
        //�����ʻ��켣��¼
        LCInsureAccFeeTraceSchema tLCInsureAccFeeTraceSchema = new LCInsureAccFeeTraceSchema();
        ref.transFields(tLCInsureAccFeeTraceSchema, pLCInsureAccClassFeeSchema);
        String tLimit = PubFun.getNoLimit(pLCInsureAccClassFeeSchema.getManageCom());
        String serNo = PubFun1.CreateMaxNo("SERIALNO", tLimit);

        tLCInsureAccFeeTraceSchema.setSerialNo(serNo);

        tLCInsureAccFeeTraceSchema.setMoneyType(sMoneyType);
        tLCInsureAccFeeTraceSchema.setFee(dMoney);
        tLCInsureAccFeeTraceSchema.setPayDate(pLCInsureAccClassFeeSchema.getBalaDate());
        tLCInsureAccFeeTraceSchema.setFeeCode(sFeeCode);

        mLCInsureAccFeeTraceSet.add(tLCInsureAccFeeTraceSchema);
        return true;
    }
    
    
    /**
     * ��ѯ�ŵ�����Ѽ������
     * @param InsureAccNo String
     * @param payPlanCode String
     * @param feebase double
     * @return LCGrpFeeParamSchema
     */
    private LCGrpFeeParamSchema queryLCGrpFeeParamSchema(String InsureAccNo,
            String payPlanCode
            , double feebase)
    {
        //String sql = "select * from LCGrpFeeParam where ";
        StringBuffer sb = new StringBuffer();
        sb.append(" select * from LCGrpFeeParam where ");
        sb.append("GrpPolNo='").append(mLCPolSchema.getGrpPolNo()).append("'");
        sb.append(" and InsuAccNo='").append(InsureAccNo).append("'");
        sb.append(" and PayPlanCode='").append(payPlanCode).append("'");
        sb.append(" and FeeMin<").append(feebase);
        sb.append(" and feemax>").append(feebase);
        LCGrpFeeParamDB tLCGrpFeeParamDB = new LCGrpFeeParamDB();
        LCGrpFeeParamSet tLCGrpFeeParamSet = tLCGrpFeeParamDB.executeQuery(sb.
                toString());
        if (tLCGrpFeeParamDB.mErrors.needDealError())
        {
            this.mErrors.copyAllErrors(tLCGrpFeeParamDB.mErrors);
            return null;
        }
        if (tLCGrpFeeParamSet == null || tLCGrpFeeParamSet.size() != 1)
        {
            CError.buildErr(this, "����������ȡֵ��Ψһ!");
            return null;
        }

        return tLCGrpFeeParamSet.get(1);

    }


    /**
     * ���������ڿ۷���
     * @param manaFee double
     * @param prem double
     * @return double
     */
    private static double calInnerRate(double manaFee, double prem)
    {
		if (prem == 0)
			return 1;
		else
			return manaFee / prem;
	}

    /**
	 * �������Ѱ��̶�����һ����ȡ ����3��
	 * 
	 * @param manaFee
	 *            double
	 * @param prem
	 *            double
	 * @return double
	 */
    private static double calZYRate(double prem, double rate)
    {
        return (prem * rate) / (1-rate);//�����㷨
    }

    /**
     * ����������ɷ���
     * @param manaFee double
     * @param prem double
     * @return double
     */
    private static double calOutRate(double manaFee, double prem)
    {
        return manaFee / (prem - manaFee);
    }


    /**
     * �������� �� �ڿ�
     * @param prem double
     * @param rate double
     * @return double
     */
    private static double calInnerManaFee(double prem, double rate)
    {
        return prem * rate;
    }


    /**
     * �������� -���
     * @param prem double
     * @param rate double
     * @return double
     */
    private static double calOutManaFee(double prem, double rate)
    {
        return (prem * rate) / (1 + rate);
    }


    /**
     * �̶�ֵ�ͱ�����ϣ�ȡ��Сֵ
     * @param basePrem double
     * @param fixValue double
     * @param rate double
     * @return double
     */
    private static double calManaFeeMinRate(double basePrem, double fixValue,
                                            double rate)
    {
        double manaFee = 0;
        manaFee = CManageFee.calInnerManaFee(basePrem, rate);
        if (fixValue < manaFee)
        {
            return fixValue;
        }
        return manaFee;
    }


    /**
     * �̶�ֵ�ͱ�����ϣ�ȡ�ϴ�ֵ
     * @param basePrem double
     * @param fixValue double
     * @param rate double
     * @return double
     */
    private static double calManaFeeMaxRate(double basePrem, double fixValue,
                                            double rate)
    {
        double manaFee = 0;
        manaFee = CManageFee.calInnerManaFee(basePrem, rate);
        if (manaFee < fixValue)
        {
            return fixValue;
        }
        return manaFee;
    }


    /**
     * �÷�����ȱ�ݣ���������ǩ��������Ϊ����������еĵõ����Ǳ����ţ����ǿ��е�������
     * ��δǩ�������ݣ�ֻ��Ͷ�����š�
     * �����˻��ʽ�ע��(����3 ��Ա�����,ע��û�и���ע���ʽ��ڲ�����ü�����ĺ���)
     * �����ڣ��������ʻ��ṹ�󣬴�ʱ������δ�ύ�����ݿ⣬����Ҫִ���ʻ����ʽ�ע�롣
     * ����ʹ���� createInsureAcc()�����󣬵õ�VData���ݣ������޸�VData���ʻ��Ľ��
     * @param tVData VData ʹ���� createInsureAcc()�����󣬵õ���VData����
     * @param AccCreatePos String �μ� ���ֱ����ʻ��ɷ� LMRiskAccPay
     * @param OtherNo String �μ� �����ʻ��� LCInsureAcc
     * @param OtherNoType String ��������
     * @param MoneyType String �μ� �����ʻ���Ǽ������� LCInsureAccTrace
     * @return boolean
     */
    public boolean calPremManaFee(VData tVData, String AccCreatePos,
                                  String OtherNo
                                  , String OtherNoType, String MoneyType)
    {
        if ((tVData == null)
            || (AccCreatePos == null)
            || (OtherNo == null)
            || (OtherNoType == null)
            || (MoneyType == null))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "addPrem";
            tError.errorMessage = "�������ݲ���Ϊ��";
            this.mErrors.addOneError(tError);

            return false;
        }
        this.Initialize(tVData);
        if (!checkData())
        {
            return false;
        }

        LCInsureAccClassSchema tClassSchema = null;

        for (int n = 1; n <= mLCPremSet.size(); n++)
        {
            LCPremSchema tLCPremSchema = mLCPremSet.get(n);

            //�ж��Ƿ��ʻ����
            if (!"1".equals(tLCPremSchema.getNeedAcc()))
            {
                continue;
            }

            boolean addFlag=false;
            double addFee = 0;
            LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet();
            for (int m = 1; m <= mLCPremToAccSet.size(); m++)
            {
				LCPremToAccSchema tLCPremToAccSchema = mLCPremToAccSet.get(m);

				// �����ǰ������͵�ǰ�Ľɷ��ʻ�������ı����ţ����α��룬���Ѽƻ�������ͬ
				if (!tLCPremSchema.getPolNo().equals(
						tLCPremToAccSchema.getPolNo())
						|| !tLCPremSchema.getDutyCode().equals(
								tLCPremToAccSchema.getDutyCode())
						|| !tLCPremSchema.getPayPlanCode().equals(
								tLCPremToAccSchema.getPayPlanCode())) {
					continue;
				}
				tLCPremToAccSet.add(tLCPremToAccSchema.getSchema());
			}
            double rePrem = 0;
            for (int m = 1; m <= tLCPremToAccSet.size(); m++)
            {
                LCPremToAccSchema tLCPremToAccSchema = tLCPremToAccSet.get(m);

//                // �����ǰ������͵�ǰ�Ľɷ��ʻ�������ı����ţ����α��룬���Ѽƻ�������ͬ
//                if (!tLCPremSchema.getPolNo().equals(tLCPremToAccSchema.
//                        getPolNo())
//                    ||
//                    !tLCPremSchema.getDutyCode().equals(tLCPremToAccSchema.
//                        getDutyCode())
//                    ||
//                    !tLCPremSchema.getPayPlanCode().equals(tLCPremToAccSchema.
//                        getPayPlanCode()))
//                {
//                    continue;
//                }

                //ֻ��ע���������0��ʱ���ע���˻����,������
                if (tLCPremToAccSchema.getRate() <= 0)
                {
                    continue;
                }
                double inputMoney = 0;
                double manaFee = 0;
                //tLCPremToAccSchema.getRate()��ǰ����1
                double baseMoney = Arith.round(tLCPremSchema.getPrem() *
                                   tLCPremToAccSchema.getRate(),2);
                
                
                //Ͷ����Ʒ
                LMRiskAppDB tLMRiskAppDB = new LMRiskAppDB();
                tLMRiskAppDB.setRiskCode(mLCPolSchema.getRiskCode());
                if(!tLMRiskAppDB.getInfo()){
                	CError.buildErr(this, "����LMRiskAppʧ�ܣ����ֱ��룺"+mLCPolSchema.getRiskCode());
                    return false;
                }
                /*��Ͷ�ʼƻ��Ĵ���*/
                double rate=-1;//����-1���ȷ���Ƿ�¼����Ͷ�ʱ���.
                
                if(tLMRiskAppDB.getRiskType3() != null && tLMRiskAppDB.getRiskType3().equals("3")){
                	try {
						rate = getInvestRate(mLCPolSchema, tLCPremToAccSchema
								.getInsuAccNo(), tLCPremToAccSchema
								.getPayPlanCode());
					} catch (Exception ex) {
						return false;
					}
					if (rePrem > baseMoney) {
						baseMoney = 0;
					} else {
						if (m == tLCPremToAccSet.size()) {// ���һ���ü���
							baseMoney = Arith.sub(baseMoney, rePrem);
							if (baseMoney < 0)
								baseMoney = 0;
						} else {
							double tempBaseMoney = baseMoney;
							double tempresult = Arith.round(Arith.mul(
									baseMoney, rate), 2);
							rePrem = Arith.add(rePrem, tempresult);
							if (rePrem > tempBaseMoney) {
								baseMoney = Arith.sub(tempBaseMoney, Arith.sub(
										rePrem, tempresult));
							}else
								baseMoney = tempresult;
						}
					}
                
                }
                
                //ֱ�Ӽ��㣬����Ҫ�����
                if (tLCPremToAccSchema.getCalFlag() == null
                    || "0".equals(tLCPremToAccSchema.getCalFlag()))
                {
                    inputMoney = baseMoney;

                }
                else
                {
                    if ("1".equals(tLCPremToAccSchema.getCalFlag()))
                    {
                        manaFee = computeManaFee(baseMoney, tLCPremToAccSchema);
                    }
                    if (manaFee < 0)
                    {
                        CError.buildErr(this, "����Ѽ���ʧ��!");
                        return false;
                    }

                }
                //PubFun.setPrecision(manaFee, "0.00");
                //������λ����
                manaFee = Arith.round(manaFee, 2);
                boolean hasFeeRate=false;//�жϽɷ��Ƿ��й����
                for(int b=1;b<=mLCGrpFeeSet.size();b++){
                	if(mLCGrpFeeSet.get(b).getPayPlanCode().equals(tLCPremToAccSchema.getPayPlanCode())){
                		hasFeeRate=true;
                	}
                }
                if (!hasFeeRate) {
                	inputMoney = baseMoney;
               	}else{
	                for (int t = 1; t <= mLCInsureAccClassFeeSet.size(); t++){
	                    LCInsureAccClassFeeSchema tClsFeeSchema =mLCInsureAccClassFeeSet.get(t);
	                    //������صĹ�����˻������
	                    if (tClsFeeSchema.getPolNo().equals(tLCPremToAccSchema.getPolNo())
	                        &&tClsFeeSchema.getInsuAccNo().equals(tLCPremToAccSchema.getInsuAccNo())
	                        &&tClsFeeSchema.getPayPlanCode().equals(tLCPremToAccSchema.getPayPlanCode())){
	                        for (int u = 1; u <= mLCGrpFeeSet.size(); u++){
	                            LCGrpFeeSchema tFeeSchema = mLCGrpFeeSet.get(u);
	                            //������صĹ������������
	                            if (tFeeSchema.getGrpPolNo().equals(tClsFeeSchema.getGrpPolNo())
	                                &&tFeeSchema.getPayPlanCode().equals(tClsFeeSchema.getPayPlanCode())
	                                &&tFeeSchema.getInsuAccNo().equals(tClsFeeSchema.getInsuAccNo())){
	                                String calMode = tFeeSchema.getFeeCalMode();
	                            	LMDutyPayDB tLMDutyPayDB=new LMDutyPayDB();
	                            	tLMDutyPayDB.setPayPlanCode(tClsFeeSchema.getPayPlanCode());
	                            	LMDutyPaySet tLMDutyPaySet=tLMDutyPayDB.query();
	                            	if(calMode.equals("09")&&tLMDutyPaySet.size()==1
	                            			&&tLMDutyPaySet.get(1).getAccPayClass().equals("3")){
	                            		ExeSQL tExeSQL = new ExeSQL();
	                            		boolean flag = false;
	                            		
	                            		String riskStr = " select count(1) from lmriskapp where RiskType3 in('3','4') and riskcode='"+tClsFeeSchema.getRiskCode()+"' ";
	                            		String riskNum = tExeSQL.getOneValue(riskStr);
	                            		if(riskNum.equals("1"))  //��Ͷ�������ܲ�Ʒ
	                            		{
	                            			if(tLMDutyPayDB.getPayPlanCode().endsWith("03"))
	                                		{
	                                			flag = true;
	                                		}
	                            		}
	                            		else
	                            		{
	                            			if(tClsFeeSchema.getAccType().equals("001"))
	                                		{
	                                			flag = true;
	                                		}
	                            		}
	                            		
	                            		if(flag)
	                            		{
	                            		if(rate==-1){//��ֹû�ж��˻��ķ���Ϊ�յ����,��������û��ƽ��ƽ��.
	                            			rate=1.0;
	                            		}
	                                //if(calMode.equals("09")&&tClsFeeSchema.getAccType().equals("001")){
	                            		//chenwm071117 ����ܵĹ����addFeeΪ0,���Ǹñ��ѵĵ�һ���˻�,�˻����������������������Ѻ�
	                            		//��ӵ��ܵĹ������,����ܵĹ����addFee��Ϊ0,���Ǹñ��ѵĵڶ����˻�,�˻���Ĺ����Ӧ����û��
	                            		//�˹������Ĺ���Ѽ�ȥ�Ѿ������ܵĹ���ѵĵ�һ���˻��Ĺ����,Ȼ�����ۼӵ��ܵĹ������.
	                            		//ע��:����ʺ��������������һ����������1������2���˻������,�����Ŀ��ܾ���Ҫ�ĳ���.
	                            		if(addFee==0.0){
	                                		manaFee=manaFee*rate;//�����Ͷ��,��Ͷ�ʱ����������ѵ��˻�
	                                		manaFee=Arith.round(manaFee, 2);
	                                		addFee=manaFee;
	                            		}else{
	                                		manaFee=Arith.round(manaFee, 2);
	                            			manaFee=manaFee-addFee;
	                                		addFee=addFee+manaFee;//���һ�������ж���˻�,����ƽ��ƽ�� ����Ҫ�ۼ�.
	                                		
	                            		}
	                            		inputMoney = baseMoney;//�˻������Ǯ����
	                                	addFlag=true;
	                                	//��������һ�¹����,��Ϊ�������ѵ�ʱ��˳����ϵ�manafeeû�г���Ͷ�ʱ���.
	                                    tClsFeeSchema.setFee(Arith.round(manaFee, 2));
	                            		}
	                            		else  //������һ��else������ͬ
	                            		{
	                            			inputMoney = Arith.sub(baseMoney , manaFee);  
	                            		}
	                            	}else{
	                                    inputMoney = Arith.sub(baseMoney , manaFee);                                	
	                                }
	                            }
	                    	}
	                    }
	                }    
                }
                if (inputMoney < 0)
                {
                    CError.buildErr(this, "����Ѽ������,���ܴ��ڱ���");
                    return false;
                }
               // PubFun.setPrecision(inputMoney, "0.00");
                Arith.round(inputMoney, 2);
                //���¹�����˻���
                updateLCInsureAccFee(tLCPremToAccSchema.getInsuAccNo(), manaFee);
                
                //���ɹ���ѹ켣�� ���ø��¹�����˻������������Ѿ��ڼ�������ʱ������
                createLCInsureAccFeeTrace(tLCPremToAccSchema.getInsuAccNo(),
                                          manaFee,
                                          tLCPremToAccSchema.getPayPlanCode());

                //�����˻������ 
                updateLCInsuerAccClass(OtherNo, OtherNoType, MoneyType,
                                       tLCPremSchema, tClassSchema
                                       , inputMoney, tLCPremToAccSchema);
                //�����˻��� 
                updateInsureAcc(OtherNo, OtherNoType, MoneyType, inputMoney,
                                tLCPremSchema
                                , tLCPremToAccSchema);

            }
            if(addFlag){
            	tLCPremSchema.setPrem(Arith.add(tLCPremSchema.getPrem() ,addFee));//����Ҫ������۵Ĺ����
            	//��ȫ�ı���ԭʼ����
            	if ("".equals(StrTool.cTrim(this.mBQFlag))) {
					tLCPremSchema.setStandPrem(Arith.add(tLCPremSchema
							.getStandPrem(), addFee));
					mLCPolSchema.setStandPrem(Arith.add(mLCPolSchema
							.getStandPrem(), addFee));
				}
            	tLCPremSchema.setSumPrem(Arith.add(tLCPremSchema.getSumPrem()  ,addFee));
            	mLCPolSchema.setPrem(Arith.add(mLCPolSchema.getPrem() ,addFee));
            	mLCPolSchema.setSumPrem(Arith.add(mLCPolSchema.getSumPrem() ,addFee));
            	for (int b = 1; b <= mLCDutySet.size(); b++) {
					if (mLCDutySet.get(b).getDutyCode().equals(
							tLCPremSchema.getDutyCode())) {
						mLCDutySet.get(b).setPrem(
								Arith.add(mLCDutySet.get(b).getPrem(), addFee));
						if ("".equals(StrTool.cTrim(this.mBQFlag)))
							mLCDutySet.get(b).setStandPrem(
									Arith.add(mLCDutySet.get(b).getStandPrem(),
											addFee));
						mLCDutySet.get(b).setSumPrem(
								Arith.add(mLCDutySet.get(b).getSumPrem(),
										addFee));
					}
				}
            	
//            	ExeSQL tExeSQL = new ExeSQL();
/*                	String mSQL1= new String();
            	mSQL1="UPDATE lccont SET prem=prem+"+manaFee   //+ ",sumprem=sumprem+"+manaFee
            		+" WHERE grpcontno='"+mLCPolSchema.getGrpContNo()
            		+"' and insuredno='"+mLCPolSchema.getInsuredNo()+"'";
            	upDate.put(mSQL1, "UPDATE");
           	if(!tExeSQL.execUpdateSQL(mSQL)){
                    CError.buildErr(this, "����һ������һ����۹����ʱ,����lccont��ı��ѳ���.");
                    return false;
            	}
            	String mSQL2= new String();
            	mSQL2="UPDATE lcduty SET prem=prem+"+manaFee+",sumprem=sumprem+"+manaFee
        			+",standprem=standprem+"+manaFee+" WHERE contno in(select contno from lccont"
        			+" where grpcontno='"+mLCPolSchema.getGrpContNo()+"' and insuredno='"+mLCPolSchema.getInsuredNo()
        			+"') and dutycode='"+tLCPremSchema.getDutyCode()+"'";
				upDate.put(mSQL2, "UPDATE");
            	if(!tExeSQL.execUpdateSQL(mSQL2)){
                    CError.buildErr(this, "����һ������һ����۹����ʱ,����lcduty��ı��ѳ���.");
                    return false;
            	}
*/	            	String mSQL3= new String();
				mSQL3="UPDATE lcgrpcont SET prem=prem+"+addFee
        			+" WHERE grpcontno='"+mLCPolSchema.getGrpContNo()+"'";
				upDate.put(mSQL3, "UPDATE");
/*					if(!tExeSQL.execUpdateSQL(mSQL3)){
                    CError.buildErr(this, "����һ������һ����۹����ʱ,����lcgrpcont��ı��ѳ���.");
                    return false;
            	}
*/                	String mSQL4= new String();
				mSQL4="UPDATE lcgrppol SET prem=prem+"+addFee
        			+" WHERE grppolno='"+mLCPolSchema.getGrpPolNo()+"' and riskcode='"
        			+mLCPolSchema.getRiskCode()+"'";
				upDate.put(mSQL4, "UPDATE");
/*					if(!tExeSQL.execUpdateSQL(mSQL4)){
                    CError.buildErr(this, "����һ������һ����۹����ʱ,����lcgrppol��ı��ѳ���.");
                    return false;
            	}
*/              }
        }
        //20070604 ���ܺ�Ͷ�����⴦��  ÿ�¹���Ѻ��ʲ�������ɱ�ȫ����

        return true;
    }

    
    /**
     * updateLCInsureAccClassFee
     *
     * @param tInsuAccNo String
     * @param manaFee double
     */
    private void createLCInsureAccFeeTrace(String tInsuAccNo, double manaFee,
                                           String tPayPlanCode) {
        //����Ƿ��˻�
        LCInsureAccClassFeeSchema tLCInsureAccClassFeeSchema =
            getLCInsureAccClassFee
            (tInsuAccNo, tPayPlanCode);
        if (tLCInsureAccClassFeeSchema == null) {
            System.out.println("û�дӲ��ҵ���Ӧ�Ĺ�����˻�");
            //return null;
        }else{	        
	        String tFeeCode = "";
	        LMRiskFeeSet tLMRiskFeeSet = new LMRiskFeeSet();
	        LMRiskFeeDB tLMRiskFeeDB = new LMRiskFeeDB();
	        tLMRiskFeeDB.setInsuAccNo(tInsuAccNo);
	        tLMRiskFeeDB.setPayPlanCode(tPayPlanCode);
	        tLMRiskFeeDB.setFeeTakePlace("01");
	        tLMRiskFeeSet = tLMRiskFeeDB.query();
	        if(tLMRiskFeeSet != null && tLMRiskFeeSet.size() > 0){
	        	tFeeCode = tLMRiskFeeSet.get(1).getFeeCode();
	        }
	        //���������ʻ�����ѹ켣�Ǳ�
	        createFeeTrace(tLCInsureAccClassFeeSchema, manaFee, "GL", tFeeCode);
        }

    }
    
    /**
     *����Ͷ�ʱ���
     *
     */
    private double getInvestRate(LCPolSchema tLCPolSchema, String InsuAccNo,
			String PayPlanCode) throws Exception
    {
		double tRate = 1.00;
		ExeSQL tExeSQL = new ExeSQL();
		LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
		tLMRiskInsuAccDB.setInsuAccNo(InsuAccNo);
		tLMRiskInsuAccDB.getInfo();
		if(!"2".equals(tLMRiskInsuAccDB.getAccKind()))//��Ͷ�ʱ����˻����ò��
			return tRate;
		LCPerInvestPlanDB tLCPerInvestPlanDB = new LCPerInvestPlanDB();
		tLCPerInvestPlanDB.setPolNo(mLCPolSchema.getProposalNo());
		if (this.mBQFlag != null && !"".equals(this.mBQFlag))// ��ȫ
			tLCPerInvestPlanDB.setPolNo(mLCPolSchema.getPolNo());
		tLCPerInvestPlanDB.setPayPlanCode(PayPlanCode);
		tLCPerInvestPlanDB.setInsuAccNo(InsuAccNo);
		if (tLCPerInvestPlanDB.getInfo())// ����Ͷ�ʼƻ�
		{
			if (tLCPerInvestPlanDB.getInputMode().equals("1")) { // ���ձ���
				tRate = tLCPerInvestPlanDB.getInvestRate();
			} else {// ���ս��
				tRate = Arith.div(tLCPerInvestPlanDB.getInvestMoney()
						, Double
								.parseDouble(tExeSQL
										.getOneValue("select nvl(sum(InvestMoney),0) from LCPerInvestPlan where polno='"
												+ tLCPerInvestPlanDB.getPolNo()
												+ "' and payplancode='"
												+ PayPlanCode + "'")));
			}
		} else { // ����û��Ͷ�ʼƻ� ���Ҹ���ʹ�õ�����Ͷ�ʼƻ�
			LCPolDB tLCPolDB = new LCPolDB();
			tLCPolDB.setPolNo(mLCPolSchema.getProposalNo());
			if (this.mBQFlag != null && !"".equals(this.mBQFlag))
				tLCPolDB.setPolNo(mLCPolSchema.getPolNo());
			if (tLCPolDB.getInfo()) {
				LCGrpIvstPlanDB tLCGrpIvstPlanDB = new LCGrpIvstPlanDB();
				tLCGrpIvstPlanDB.setGrpPolNo(mLCPolSchema.getGrpPolNo());
				tLCGrpIvstPlanDB.setPayPlanCode(PayPlanCode);
				tLCGrpIvstPlanDB.setInsuAccNo(InsuAccNo);
				tLCGrpIvstPlanDB
						.setInvestRuleCode(tLCPolDB.getInvestRuleCode());
				if (tLCGrpIvstPlanDB.getInfo()) {
					if (tLCGrpIvstPlanDB.getInputMode().equals("1")) { // ���ձ���
						tRate = tLCGrpIvstPlanDB.getInvestRate();
					} else { // ���ս��
						tRate = Arith.div(tLCGrpIvstPlanDB.getInvestMoney()
								, Double
										.parseDouble(tExeSQL
												.getOneValue("select nvl(sum(InvestMoney),0) from LCGrpIvstPlan where grppolno='"
														+ tLCGrpIvstPlanDB
																.getGrpPolNo()
														+ "' and payplancode='"
														+ tLCGrpIvstPlanDB
																.getPayPlanCode()
														+ "' and InvestRuleCode='"
														+ tLCGrpIvstPlanDB
																.getInvestRuleCode()
														+ "'")));
					}
				} else {
					CError.buildErr(this, "���ұ������ˣ��ͻ��ţ�"
							+ tLCPolSchema.getInsuredNo() + "����Ͷ�ʱ���ʧ�ܣ�");
					throw new Exception("���ұ������ˣ��ͻ��ţ�"
							+ tLCPolSchema.getInsuredNo() + "����Ͷ�ʱ���ʧ�ܣ�");
				}
			} else {
				CError.buildErr(this, "���ұ������ˣ��ͻ��ţ�"
						+ tLCPolSchema.getInsuredNo() + "����Ͷ�ʱ���ʧ�ܣ�");
				throw new Exception("���ұ������ˣ��ͻ��ţ�" + tLCPolSchema.getInsuredNo()
						+ "����Ͷ�ʱ���ʧ�ܣ�");
			}
		}
		return tRate;
	}
    
    
    /**
	 * ��������
	 * 
	 * @param baseMoney
	 *            double
	 * @param tLCPremToAccSchema
	 *            LCPremToAccSchema
	 * @return double
	 */
    private double computeManaFee(double baseMoney,
                                  LCPremToAccSchema tLCPremToAccSchema)
    {
        double manaFee = 0;
        double manaFeeRate = 0;
        if (mLCGrpFeeSet.size() == 0) //���������û���������ѣ��������������
//        if (mLCGrpFeeSet == null)
        {
            //add by frost 2005-7-21
            LCPremSchema mLCPremSchema = new LCPremSchema();
            mLCPremSchema = getLCPremSchema(tLCPremToAccSchema.getPayPlanCode());

            for (int t = 1; t <= mLCInsureAccClassFeeSet.size(); t++)
            {
                LCInsureAccClassFeeSchema tClsFeeSchema =
                        mLCInsureAccClassFeeSet.get(t);
                //������صĹ�����˻������
                if (tClsFeeSchema.getPolNo().equals(tLCPremToAccSchema.getPolNo())
                    &&
                    tClsFeeSchema.getInsuAccNo().equals(tLCPremToAccSchema.
                        getInsuAccNo())
                    &&
                    tClsFeeSchema.getPayPlanCode().equals(tLCPremToAccSchema.
                        getPayPlanCode()))
                {

                    LMRiskFeeDB tLMRiskFeeDB = new LMRiskFeeDB();
                    tLMRiskFeeDB.setInsuAccNo(tLCPremToAccSchema.getInsuAccNo());
                    tLMRiskFeeDB.setPayPlanCode(tLCPremToAccSchema.
                                                getPayPlanCode());
                    LMRiskFeeSet mLMRiskFeeSet = new LMRiskFeeSet();
                    mLMRiskFeeSet = tLMRiskFeeDB.query();
                    LMRiskFeeSchema mLMRiskFeeSchema = new LMRiskFeeSchema();
                    for (int u = 1; u <= mLMRiskFeeSet.size(); u++)
                    {
                        mLMRiskFeeSchema = mLMRiskFeeSet.get(u);
                        //������صĹ��������
                        String FeeCalModeType = mLMRiskFeeSchema.
                                                getFeeCalModeType();
                        String calMode = mLMRiskFeeSchema.getFeeCalMode();
                        double mValue = 0;
                        //���ֵ���SQL
                        if (FeeCalModeType.equals("1"))
                        {
                            String FeeCalCode = mLMRiskFeeSchema.getFeeCalCode();
                            if (FeeCalCode == null || FeeCalCode.equals(""))
                            {
                                return 0;
                            }
                            else
                            {
                                mCalculator.addBasicFactor("Prem"
                                        , String.valueOf(mLCPremSchema.getPrem()));
                                mCalculator.setCalCode(FeeCalCode);
                                String strsql = mCalculator.calculate();
                                if (strsql.trim().equals(""))
                                {
                                    mValue = 0;
                                }
                                else
                                {
                                    mValue = Double.parseDouble(strsql);
                                }
                            }
                        }
                        if (calMode == null || "01".equals(calMode))
                        {
                            //�ڿ۹̶�ֵ
                            if (FeeCalModeType.equals("1"))
                            {
                                manaFee = mValue;
                            }
                            else
                            {
                                manaFee = mLMRiskFeeSchema.getFeeValue();
                            }
                            manaFeeRate = CManageFee.calInnerRate(manaFee,
                                    baseMoney);
                        }
                        else
                        if (calMode.equals("02"))
                        {
                            //�ڿ۱���
                            if (FeeCalModeType.equals("1"))
                            {
                                manaFeeRate = mValue;
                            }
                            else
                            {
                                manaFeeRate = mLMRiskFeeSchema.getFeeValue();
                            }
                            manaFee = CManageFee.calInnerManaFee(baseMoney,
                                    manaFeeRate);
                        }
                        else
                        if (calMode.equals("03"))
                        {
                            //���-�̶�ֵ
                            if (FeeCalModeType.equals("1"))
                            {
                                manaFee = mValue;
                            }
                            else
                            {
                                manaFee = mLMRiskFeeSchema.getFeeValue();
                            }
                            manaFeeRate = CManageFee.calOutRate(manaFee,
                                    baseMoney);
                        }
                        else
                        if (calMode.equals("04"))
                        {
                            //���-����ֵ
                            if (FeeCalModeType.equals("1"))
                            {
                                manaFeeRate = mValue;
                            }
                            else
                            {
                                manaFeeRate = mLMRiskFeeSchema.getFeeValue();
                            }
                            manaFee = CManageFee.calOutManaFee(baseMoney,
                                    manaFeeRate);
                        }
                        else
                        if (calMode.equals("05"))
                        {
                            //�̶�ֵ�����������ȡ��Сֵ
                            if (FeeCalModeType.equals("1"))
                            {
                                manaFeeRate = mValue;
                            }
                            else
                            {
                                manaFeeRate = mLMRiskFeeSchema.getFeeValue();
                            }
                            manaFee = CManageFee.calManaFeeMinRate(baseMoney
                                    , mLMRiskFeeSchema.getCompareValue(),
                                    manaFeeRate);
                        }
                        else
                        if (calMode.equals("06"))
                        {
                            //�̶�ֵ�����������ȡ�ϴ�ֵ
                            if (FeeCalModeType.equals("1"))
                            {
                                manaFeeRate = mValue;
                            }
                            else
                            {
                                manaFeeRate = mLMRiskFeeSchema.getFeeValue();
                            }
                            manaFee = CManageFee.calManaFeeMaxRate(baseMoney
                                    , mLMRiskFeeSchema.getCompareValue(),
                                    manaFeeRate);
                        }
                        /* else //��֪��ôʵ��
                         if (calMode.equals("07")) { //�ֵ�����
                           LCGrpFeeParamSchema
                               tLCGrpFeeParamSchema
                               = this.
                               queryLCGrpFeeParamSchema(
                               tFeeSchema.getInsuAccNo()
                               ,
                               tFeeSchema.
                               getPayPlanCode()
                               , baseMoney);
                           manaFeeRate =
                               tLCGrpFeeParamSchema.
                               getFeeRate();
                           manaFee = this.calInnerManaFee(
                               baseMoney,
                               manaFeeRate);
                         }*/
                        else if (calMode.equals("08"))
                        {
                            //�ۼƷֵ�����
                            //������ȷ����δ���
                            manaFeeRate = 0;
                            manaFee = 0;
                        }else if (calMode.equals("09")){
                        	this.mFeeType = "2";
                            //chenwm20071106 ���� 09-���̶�����һ����ȡ
                        	manaFeeRate = mValue;
                            manaFee = CManageFee.calZYRate(baseMoney,
                                    manaFeeRate);
                            if(!tLCPremToAccSchema.getPayPlanCode().endsWith("03"))
                            	manaFee = 0;
                        }
                        break;
                    }
                    tClsFeeSchema.setFee(Arith.round(manaFee, 2));
                    tClsFeeSchema.setFeeRate(Arith.round(manaFeeRate, 6) );
                    break;
                }

            }
        } //����
        else
        {
            //����ӽ�����¼�Ĺ���ѣ���ֻ���ŵ�
            for (int t = 1; t <= mLCInsureAccClassFeeSet.size(); t++)
            {
                LCInsureAccClassFeeSchema tClsFeeSchema =
                        mLCInsureAccClassFeeSet.get(t);
                //������صĹ�����˻������
                if (tClsFeeSchema.getPolNo().equals(tLCPremToAccSchema.getPolNo())
                    &&
                    tClsFeeSchema.getInsuAccNo().equals(tLCPremToAccSchema.
                        getInsuAccNo())
                    &&
                    tClsFeeSchema.getPayPlanCode().equals(tLCPremToAccSchema.
                        getPayPlanCode()))
                {
                    for (int u = 1; u <= mLCGrpFeeSet.size(); u++)
                    {
                        LCGrpFeeSchema tFeeSchema = mLCGrpFeeSet.get(u);

                        //������صĹ������������
                        if (tFeeSchema.getGrpPolNo().equals(tClsFeeSchema.
                                getGrpPolNo())
                            &&
                            tFeeSchema.getPayPlanCode().equals(tClsFeeSchema.
                                getPayPlanCode())
                            &&
                            tFeeSchema.getInsuAccNo().equals(tClsFeeSchema.
                                getInsuAccNo()))
                        {
                            String calMode = tFeeSchema.getFeeCalMode();

                            if (calMode == null || "01".equals(calMode))
                            {
                                //�ڿ۹̶�ֵ
                                manaFee = tFeeSchema.getFeeValue();
                                //����Ϊ0�����������Ĳ���Ҫ����
//                                if(baseMoney==0){
//                                    // @@������
//                                    CError tError = new CError();
//                                    tError.moduleName = "CManageFee";
//                                    tError.functionName = "computeManaFee";
//                                    tError.errorMessage = "���뱣�Ѳ���Ϊ��!";
//                                    this.mErrors.addOneError(tError);
//
//                                    return -1;
//                                	
//                                }
                                manaFeeRate = CManageFee.calInnerRate(manaFee,
                                        baseMoney);
                            }
                            else
                            if (calMode.equals("02"))
                            {
                                //�ڿ۱���
                                manaFeeRate = tFeeSchema.getFeeValue();
                                manaFee = CManageFee.calInnerManaFee(baseMoney,
                                        manaFeeRate);
                            }
                            else
                            if (calMode.equals("03"))
                            {
                                //���-�̶�ֵ
                                manaFee = tFeeSchema.getFeeValue();
                                manaFeeRate = CManageFee.calOutRate(manaFee,
                                        baseMoney);
                            }
                            else
                            if (calMode.equals("04"))
                            {
                                //���-����ֵ
                                manaFeeRate = tFeeSchema.getFeeValue();
                                manaFee = CManageFee.calOutManaFee(baseMoney,
                                        manaFeeRate);
                            }
                            else
                            if (calMode.equals("05"))
                            {
                                //�̶�ֵ�����������ȡ��Сֵ
                                manaFeeRate = tFeeSchema.getFeeValue();
                                manaFee = CManageFee.calManaFeeMinRate(
                                        baseMoney
                                        , tFeeSchema.getCompareValue(),
                                        manaFeeRate);
                            }
                            else
                            if (calMode.equals("06"))
                            {
                                //�̶�ֵ�����������ȡ�ϴ�ֵ
                                manaFeeRate = tFeeSchema.getFeeValue();
                                manaFee = CManageFee.calManaFeeMaxRate(
                                        baseMoney
                                        , tFeeSchema.getCompareValue(),
                                        manaFeeRate);
                            }
                            else
                            if (calMode.equals("07"))
                            {
                                //�ֵ�����
                                LCGrpFeeParamSchema tLCGrpFeeParamSchema = this.
                                        queryLCGrpFeeParamSchema(tFeeSchema.
                                        getInsuAccNo()
                                        , tFeeSchema.getPayPlanCode(),
                                        baseMoney);
                                manaFeeRate = tLCGrpFeeParamSchema.getFeeRate();
                                manaFee = CManageFee.calInnerManaFee(baseMoney,
                                        manaFeeRate);
                            }
                            else
                            if (calMode.equals("08"))
                            {
                                //�ۼƷֵ�����
                                //������ȷ����δ���
                                manaFeeRate = 0;
                                manaFee = 0;
                            }else if (calMode.equals("09")){
                                //chenwm20071106 ���� 09-���̶�����һ����ȡ
                            	//String tOrganComCode="0000";
                            	String tOrganInnerCode="86";
                            	this.mFeeType = "2";
                            	LMDutyPayDB tLMDutyPayDB=new LMDutyPayDB();
                            	tLMDutyPayDB.setPayPlanCode(tClsFeeSchema.getPayPlanCode());
                            	tLMDutyPayDB.getInfo();
                            	if("3".equals(tLMDutyPayDB.getAccPayClass())){
                            		//ֻ�й����˻�ʱ�ſ�,�����˻�����;�жϹ����ʻ��ĸ������ֱ���ò�ͬ���߼�������һ��ֻ��һ�������ʻ�(����ж�������ʻ����轫����һ��payplancode��03��β)�����ڿ��ж�������ʻ�
                            		ExeSQL tExeSQL = new ExeSQL();
                            		boolean flag = false;
                            		
                            		String riskStr = " select count(1) from lmriskapp where RiskType3 in('3','4') and riskcode='"+tClsFeeSchema.getRiskCode()+"' ";
                            		String riskNum = tExeSQL.getOneValue(riskStr);
                            		if(riskNum.equals("1"))  //��Ͷ�������ܲ�Ʒ
                            		{
                            			if(tLMDutyPayDB.getPayPlanCode().endsWith("03"))
                                		{
                                			flag = true;
                                		}
                            		}
                            		else
                            		{
                            			if(tClsFeeSchema.getAccType().equals("001"))
                                		{
                                			flag = true;
                                		}
                            		}
                            		
                            		if(flag)
                            		{	
                            		LCInsuredDB tLCInsuredDB=new LCInsuredDB();
                            		tLCInsuredDB.setGrpContNo(tClsFeeSchema.getGrpContNo());
                            		//tLCInsuredDB.setContNo(tClsFeeSchema.getContNo());
                            		tLCInsuredDB.setInsuredNo(tClsFeeSchema.getInsuredNo());
                            		LCInsuredSet tLCInsuredSet=tLCInsuredDB.query();
                            		if(tLCInsuredSet.size()!=1){
                                        CError.buildErr(this,"��ѯ�����ʻ�ʧ��!"); 
                                        return -1;
                            		}else{
                            			//tOrganComCode=tLCInsuredSet.get(1).getOrganComCode();
                            			tOrganInnerCode=tLCInsuredSet.get(1).getOrganInnerCode();
                            		}
//Amended By Fang for Change1(��۹������Ҫ���ڶ�Ӧ�����˻�)
//                            		//�๫���ʻ���ʱ��ֻȡ�и����ֵĲ����ڲ�������С���Ǹ������ʻ�
//                            		String tSQL="SELECT MIN(organinnercode) FROM lcinsured WHERE exists ("
//                            			+"SELECT 1 FROM lcpol WHERE grpcontno='"+tClsFeeSchema.getGrpContNo()
//                            			+"' AND riskcode='"+tClsFeeSchema.getRiskCode()+"' AND poltypeflag='2' and contno =lcinsured.contno ) and grpcontno = '"+
//                            			tClsFeeSchema.getGrpContNo()+"'";
//                            		String Min_organinnercode;
//                            		String tEdorNo = null;
//                            		String tEdorType = null;
//                            		if (!"".equals(StrTool.cTrim(this.mBQFlag))) {
//										// ׷�ӱ���
//										TransferData tTransferData = (TransferData) this.mInputData
//												.getObjectByObjectName(
//														"TransferData", 0);
//										tEdorNo = (String) tTransferData
//												.getValueByName("EdorNo");
//										tEdorType = (String) tTransferData
//												.getValueByName("EdorType");
//										tSQL = "SELECT MIN(organinnercode) FROM lcinsured WHERE exists ("
//												+ "SELECT 1 FROM lpprem WHERE edorno='"
//												+ tEdorNo
//												+ "' AND edortype='"
//												+ tEdorType
//												+ "' AND contno = lcinsured.contno) and exists (select 1 from lcpol where contno =lcinsured.contno and poltypeflag='2' and riskcode = '"
//												+ tClsFeeSchema.getRiskCode()
//												+ "' ) and grpcontno = '"
//												+ tClsFeeSchema.getGrpContNo()
//												+ "'";
//									}
//                            		Min_organinnercode =tExeSQL.getOneValue(tSQL);
//                            		if(Min_organinnercode!=null
//                            				&&Min_organinnercode.equals(tOrganInnerCode)){
//                            			//TODO:���ӱ�ȫ�߼�
//	                            		tSQL="SELECT SUM(prem) FROM lcpol WHERE grpcontno='"
//	                            			+tClsFeeSchema.getGrpContNo()+"' and riskcode='"
//	                            			+tFeeSchema.getRiskCode()+"'";
//	                            		if (!"".equals(StrTool.cTrim(this.mBQFlag))) {
//											tSQL = "SELECT SUM(standprem) FROM lpprem WHERE edorno='"
//													+ tEdorNo
//													+ "' and edortype='"
//													+ tEdorType
//													+ "' and exists (select 1 from lcpol where polno = lpprem.polno and riskcode = '"
//													+ tClsFeeSchema
//															.getRiskCode()
//													+ "')";
//										}
//	                            		baseMoney=Double.parseDouble(tExeSQL.getOneValue(tSQL));
//		                            	manaFeeRate = tFeeSchema.getFeeValue();
//		                                manaFee = CManageFee.calZYRate(baseMoney,manaFeeRate);
//                            		}else{
//                            			manaFee=0;
//                            		}
                            		String tSQL = "";
                            		tSQL = "select nvl(sum(a.prem),0) from lcpol a,lcinsured b where a.contno=b.contno and a.grpcontno='"
                            			+tClsFeeSchema.getGrpContNo()+"' and a.riskcode='"+tFeeSchema.getRiskCode()
                            			+"' and b.organinnercode='"+tOrganInnerCode+"'";
                            		String tEdorNo = null;
                            		String tEdorType = null;
                            		if (!"".equals(StrTool.cTrim(this.mBQFlag))) {
                            			// ׷�ӱ���
										TransferData tTransferData = (TransferData) this.mInputData.getObjectByObjectName("TransferData", 0);
										tEdorNo = (String) tTransferData.getValueByName("EdorNo");
										tEdorType = (String) tTransferData.getValueByName("EdorType");
										tSQL = "select nvl(sum(standprem),0) from lpprem WHERE edorno='"
												+ tEdorNo
												+ "' and edortype='"
												+ tEdorType
												+ "' and exists (select 1 from lcpol a,lcinsured b where a.contno=b.contno and polno=lpprem.polno and riskcode='"
												+ tClsFeeSchema.getRiskCode()
												+ "' and b.organinnercode='"
												+ tOrganInnerCode + "')";
									}
                            		baseMoney=Double.parseDouble(tExeSQL.getOneValue(tSQL));
	                            	manaFeeRate = tFeeSchema.getFeeValue();
	                                manaFee = CManageFee.calZYRate(baseMoney,manaFeeRate);
//Ended for Change1
                            		}
                            	}
                            }
                            break;
                        }
                    }
                    tClsFeeSchema.setFee(Arith.round(manaFee,
                            2));
                    tClsFeeSchema.setFeeRate(Arith.round(
                            manaFeeRate,
                            6));//chenwm20071102 ���ֺ����ݿ�һ��
                    break;

                }
            }
        }
        return manaFee;
    }

    private void updateInsureAcc(String OtherNo, String OtherNoType,
                                 String MoneyType
                                 , double inputMoney,
                                 LCPremSchema tLCPremSchema,
                                 LCPremToAccSchema tLCPremToAccSchema)
    {
        for (int j = 1; j <= mLCInsureAccSet.size(); j++)
        {
            //�����ǰ�ɷ��ʻ�������ı����ţ��˻��ź͵�ǰ���˻���ı����ţ��˻�����ͬ�����ʽ�Ϊ0�����ʽ�ע��
            LCInsureAccSchema tLCInsureAccSchema = mLCInsureAccSet.get(j);
            if (tLCPremToAccSchema.getPolNo().equals(tLCInsureAccSchema.
                    getPolNo())
                &&
                tLCPremToAccSchema.getInsuAccNo().equals(tLCInsureAccSchema.
                    getInsuAccNo()))
            {
                //�޸ı����ʻ����
                tLCInsureAccSchema.setInsuAccBala(PubFun.setPrecision(
                        tLCInsureAccSchema.
                        getInsuAccBala() + inputMoney, "0.00"));
                tLCInsureAccSchema.setSumPay(PubFun.setPrecision(
                        tLCInsureAccSchema.getSumPay()
                        + inputMoney, "0.00"));
                tLCInsureAccSchema.setLastAccBala(PubFun.setPrecision(
                        tLCInsureAccSchema.
                        getLastAccBala() + inputMoney, "0.00"));

                //��ѯ���ֱ����ʻ��ɷ�
//                LMRiskAccPaySchema
//                        tLMRiskAccPaySchema =
//                        queryLMRiskAccPay3(
//                        mLCPolSchema.getRiskCode(),
//                        tLCPremToAccSchema);
//                if (tLMRiskAccPaySchema == null)
//                {
//                    // return null;
//                    System.out.println("��ѯ���������");
//                    return;
//                }
//                if (tLMRiskAccPaySchema.
//                    getPayNeedToAcc()
//                    .equals("1"))
//                {
//                LCInsureAccTraceSchema tmpLCInsureAccTraceSchema = createAccTrace(OtherNo
//                        , OtherNoType, MoneyType, inputMoney, tLCPremSchema, tLCInsureAccSchema);
//                mLCInsureAccTraceSet.add(tmpLCInsureAccTraceSchema);

//                }

                // break;
            }
        }

    }


    /**
     * �����ʻ������
     * @param OtherNo String
     * @param OtherNoType String
     * @param MoneyType String
     * @param tLCPremSchema LCPremSchema
     * @param tLCInsureAccClassSchema LCInsureAccClassSchema
     * @param inputMoney double
     * @param tLCPremToAccSchema LCPremToAccSchema
     */
    private void updateLCInsuerAccClass(String OtherNo, String OtherNoType,
                                        String MoneyType
                                        , LCPremSchema tLCPremSchema,
                                        LCInsureAccClassSchema
                                        tLCInsureAccClassSchema
                                        , double inputMoney,
                                        LCPremToAccSchema tLCPremToAccSchema)
    {
        //�ۼ��˻�����ss
        for (int tt = 1; tt <= mInsureAccClassSet.size(); tt++)
        {
            tLCInsureAccClassSchema = mInsureAccClassSet.get(tt);
            if (tLCInsureAccClassSchema.getInsuAccNo().equals(
                    tLCPremToAccSchema.getInsuAccNo())
                &&
                tLCInsureAccClassSchema.getPayPlanCode().equals(
                    tLCPremToAccSchema.
                    getPayPlanCode())
                &&
                tLCInsureAccClassSchema.getPolNo().equals(tLCPremToAccSchema.
                    getPolNo()))
            {
                //�����ʻ��ۼƱ��ѡ���ʼ�ʻ����ʻ����
                tLCInsureAccClassSchema.setSumPay(PubFun.setPrecision(
                        tLCInsureAccClassSchema.
                        getSumPay() + inputMoney, "0.00"));
                tLCInsureAccClassSchema.setInsuAccBala(PubFun.setPrecision(
                        tLCInsureAccClassSchema.
                        getInsuAccBala() + inputMoney, "0.00"));
                tLCInsureAccClassSchema.setLastAccBala(PubFun.setPrecision(
                        tLCInsureAccClassSchema.
                        getLastAccBala() + inputMoney, "0.00"));
                // break;
                //�����ʻ���������ɶ�Ӧ���ʻ��켣������
                LCInsureAccTraceSchema tmpLCInsureAccTraceSchema =
                        createAccTrace(OtherNo, OtherNoType
                                       , MoneyType, inputMoney, tLCPremSchema,
                                       tLCInsureAccClassSchema);
                mLCInsureAccTraceSet.add(tmpLCInsureAccTraceSchema);

            }
            //�����ʻ���������ɶ�Ӧ���ʻ��켣������
            //���������ж��ڣ�����Alex 20050920
//            LCInsureAccTraceSchema tmpLCInsureAccTraceSchema = createAccTrace(OtherNo, OtherNoType
//                    , MoneyType, inputMoney, tLCPremSchema, tLCInsureAccClassSchema);
//            mLCInsureAccTraceSet.add(tmpLCInsureAccTraceSchema);

        }
    }


    /**
     * ���¹�����˻���
     * @param InsuAccNo String
     * @param fee double
     */
    private void updateLCInsureAccFee(String InsuAccNo, double fee)
    {
        //����Ƿ��˻�
        LCInsureAccFeeSchema tLCInsureAccFeeSchema = getLCInsureAccFee(
                InsuAccNo);
        if (tLCInsureAccFeeSchema == null)
        {
            System.out.println("û�дӲ��ҵ���Ӧ�Ĺ�����˻�");
            //return null;
        }
        else
        {
            tLCInsureAccFeeSchema.setFee(PubFun.setPrecision(
                    tLCInsureAccFeeSchema.
                    getFee() + fee
                    , "0.00"));
        }
    }


    /**
     * �����˻��켣��
     * @param OtherNo String
     * @param OtherNoType String
     * @param MoneyType String
     * @param inputMoney double
     * @param tLCPremSchema LCPremSchema
     * @param tLCInsureAccClassSchema LCInsureAccClassSchema
     * @return LCInsureAccTraceSchema
     */
    private static LCInsureAccTraceSchema createAccTrace(String OtherNo,
            String OtherNoType
            , String MoneyType, double inputMoney, LCPremSchema tLCPremSchema
            , LCInsureAccClassSchema tLCInsureAccClassSchema)
    {

        //��䱣���ʻ���Ǽ�������
        String tLimit = PubFun.getNoLimit(tLCPremSchema.getManageCom());
        String serNo = PubFun1.CreateMaxNo("SERIALNO", tLimit);
        LCInsureAccTraceSchema tLCInsureAccTraceSchema = new
                LCInsureAccTraceSchema();
        tLCInsureAccTraceSchema.setSerialNo(serNo);
//        tLCInsureAccTraceSchema.setInsuredNo(tLCInsureAccClassSchema
//                                             .getInsuredNo());
        tLCInsureAccTraceSchema.setPolNo(tLCInsureAccClassSchema.getPolNo());
        tLCInsureAccTraceSchema.setMoneyType(MoneyType);
        tLCInsureAccTraceSchema.setRiskCode(tLCInsureAccClassSchema.getRiskCode());
        tLCInsureAccTraceSchema.setOtherNo(OtherNo);
        tLCInsureAccTraceSchema.setOtherType(OtherNoType);
        tLCInsureAccTraceSchema.setMoney(inputMoney);
        tLCInsureAccTraceSchema.setContNo(tLCInsureAccClassSchema.getContNo());
        tLCInsureAccTraceSchema.setGrpPolNo(tLCInsureAccClassSchema.getGrpPolNo());
        tLCInsureAccTraceSchema.setInsuAccNo(tLCInsureAccClassSchema.
                                             getInsuAccNo());

        tLCInsureAccTraceSchema.setPolNo(tLCInsureAccClassSchema.getPolNo());
        tLCInsureAccTraceSchema.setGrpContNo(tLCInsureAccClassSchema.
                                             getGrpContNo());
        tLCInsureAccTraceSchema.setState(tLCInsureAccClassSchema.getState());
        tLCInsureAccTraceSchema.setManageCom(tLCInsureAccClassSchema.
                                             getManageCom());
        tLCInsureAccTraceSchema.setOperator(tLCInsureAccClassSchema.getOperator());

        tLCInsureAccTraceSchema.setPolNo(tLCInsureAccClassSchema.getPolNo());
        tLCInsureAccTraceSchema.setGrpContNo(tLCInsureAccClassSchema.
                                             getGrpContNo());
        String CurrentDate = PubFun.getCurrentDate();
        String CurrentTime = PubFun.getCurrentTime();
        tLCInsureAccTraceSchema.setPayPlanCode(tLCPremSchema.getPayPlanCode());
        tLCInsureAccTraceSchema.setState("0");
        //Ĭ��δ���������ù켣���еĹ�������Ϊ�ʻ�����������
        tLCInsureAccTraceSchema.setAccAscription(tLCInsureAccClassSchema.
                                                 getAccAscription());
        //���ý�������
        tLCInsureAccTraceSchema.setPayDate(tLCInsureAccClassSchema.getBalaDate()); //����Ϊ���������� Alex 20051124
        tLCInsureAccTraceSchema.setMainPayDate(tLCInsureAccClassSchema.getBalaDate()); // ASR20095140-���ȫ��������ȡ
        tLCInsureAccTraceSchema.setMainSerialNo(serNo);  // ����ı���MainSerialNoΪ�Լ���serNo,mainpaydateΪ�Լ���paydate
        
        tLCInsureAccTraceSchema.setMakeDate(CurrentDate);
        tLCInsureAccTraceSchema.setMakeTime(CurrentTime);
        tLCInsureAccTraceSchema.setModifyDate(CurrentDate);
        tLCInsureAccTraceSchema.setModifyTime(CurrentTime);
        tLCInsureAccTraceSchema.setOperator(tLCPremSchema.getOperator());
        
        //�洢��֤����
        if(tLCInsureAccTraceSchema.getPayDate() == null || !tLCInsureAccTraceSchema.getPayDate().equals("3000-01-01")){
	        LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
	        tLMRiskInsuAccDB.setInsuAccNo(tLCInsureAccTraceSchema.getInsuAccNo());
	        if (tLMRiskInsuAccDB.getInfo()) {
				if (tLMRiskInsuAccDB.getAccType1() == null
						|| (!tLMRiskInsuAccDB.getAccType1().equals("001") && !tLCInsureAccTraceSchema
								.getPayPlanCode().endsWith("02"))) {
					LDPromiseRateSchema tLDPromiseRateSchema = queryLDPromiseRate(
							tLCInsureAccTraceSchema.getRiskCode(),
							tLCInsureAccTraceSchema.getPayDate());
					if (tLDPromiseRateSchema != null) {
						tLCInsureAccTraceSchema
								.setPromiseStartDate(tLDPromiseRateSchema
										.getStartDate());
						tLCInsureAccTraceSchema
								.setPromiseRate(tLDPromiseRateSchema.getRate());
					}
				}
			}
        }


        return tLCInsureAccTraceSchema;
    }
    
    
    /**
     * ��ѯ��֤����
     * @param riskcode
     * @param dealDate
     * @return
     */
    public static LDPromiseRateSchema queryLDPromiseRate(String riskcode, String dealDate){
    	LDPromiseRateSchema tLDPromiseRateSchema = null;
    	LDPromiseRateDB tLDPromiseRateDB = new LDPromiseRateDB();
    	LDPromiseRateSet tLDPromiseRateSet = new LDPromiseRateSet();
    	String tsql = "select * from LDPromiseRate where riskcode='"+riskcode+"' and startdate <= '"+dealDate+"' and enddate >= '"+dealDate+"'";
    	tLDPromiseRateSet = tLDPromiseRateDB.executeQuery(tsql);
    	if(tLDPromiseRateSet != null && tLDPromiseRateSet.size() > 0){
    		tLDPromiseRateSchema = tLDPromiseRateSet.get(1);
    	}
    	return tLDPromiseRateSchema;
    }

    /**
     * ��ѯ���ֱ����ʻ��ɷѱ�3
     * @param riskCode String
     * @param pLCPremToAccSchema LCPremToAccSchema
     * @return LMRiskAccPaySchema
     */
    public LMRiskAccPaySchema queryLMRiskAccPay3(String riskCode
                                                 ,
                                                 LCPremToAccSchema
                                                 pLCPremToAccSchema)
    {
        if (riskCode == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay3";
            tError.errorMessage = "�������ݲ���Ϊ��!";
            this.mErrors.addOneError(tError);

            return null;
        }

        String payPlanCode = pLCPremToAccSchema.getPayPlanCode();
        String InsuAccNo = pLCPremToAccSchema.getInsuAccNo();

        //��ѯ���ֱ����ʻ��ɷѱ�
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from LMRiskAccPay where RiskCode='");
        tSBql.append(riskCode);
        tSBql.append("' and payPlanCode='");
        tSBql.append(payPlanCode);
        tSBql.append("' and InsuAccNo='");
        tSBql.append(InsuAccNo);
        tSBql.append("'");

        LMRiskAccPaySchema tLMRiskAccPaySchema = new LMRiskAccPaySchema();
        LMRiskAccPaySet tLMRiskAccPaySet = new LMRiskAccPaySet();
        LMRiskAccPayDB tLMRiskAccPayDB = tLMRiskAccPaySchema.getDB();
        tLMRiskAccPaySet = tLMRiskAccPayDB.executeQuery(tSBql.toString());
        if (tLMRiskAccPayDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskAccPayDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay2";
            tError.errorMessage = "���ֱ����ʻ��ɷѱ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLMRiskAccPaySet.clear();

            return null;
        }
        if (tLMRiskAccPaySet.size() == 0)
        {
            // @@������
            this.mErrors.copyAllErrors(tLMRiskAccPayDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay2";
            tError.errorMessage = "���ֱ����ʻ��ɷѱ�û�в�ѯ���������!";
            this.mErrors.addOneError(tError);
            tLMRiskAccPaySet.clear();

            return null;
        }

        return tLMRiskAccPaySet.get(1);
    }
    
    public VData getUpDate(){
    	VData tVData = new VData();
    	tVData.add(upDate);
    	return tVData;
    }
    
    //add by lilei
    
    public double calManaFee(double prem,LCPremToAccSchema tLCPremToAccSchema,LCGrpFeeSet tLCGrpFeeSet)
    {
    	double ManaFeeMoney = 0;
    	
        if (tLCGrpFeeSet.size() == 0) //���������û���������ѣ��������������
        {

        	LMRiskFeeDB tLMRiskFeeDB = new LMRiskFeeDB();
        	tLMRiskFeeDB.setInsuAccNo(tLCPremToAccSchema.getInsuAccNo());
        	tLMRiskFeeDB.setPayPlanCode(tLCPremToAccSchema.getPayPlanCode());
                    
            LMRiskFeeSet mLMRiskFeeSet = new LMRiskFeeSet();
        	mLMRiskFeeSet = tLMRiskFeeDB.query();
        	LMRiskFeeSchema mLMRiskFeeSchema = new LMRiskFeeSchema();
                    
        	for (int u = 1; u <= mLMRiskFeeSet.size(); u++)
            {
                mLMRiskFeeSchema = mLMRiskFeeSet.get(u);
                //������صĹ��������
                String FeeCalModeType = mLMRiskFeeSchema.getFeeCalModeType();
                
                String calMode = mLMRiskFeeSchema.getFeeCalMode();
                double mValue = 0;
                //���ֵ���SQL
                if (FeeCalModeType.equals("1"))
                {
                    String FeeCalCode = mLMRiskFeeSchema.getFeeCalCode();
                    if (FeeCalCode == null || FeeCalCode.equals(""))
                    {
                        return 0;
                    }
                    else
                    {
                        mCalculator.addBasicFactor("Prem", String.valueOf(prem));
                        
                        mCalculator.setCalCode(FeeCalCode);
                        String strsql = mCalculator.calculate();
                        if (strsql.trim().equals(""))
                        {
                            mValue = 0;
                        }
                        else
                        {
                            mValue = Double.parseDouble(strsql);
                        }
                    }
                }
                if (calMode == null || "01".equals(calMode))
                {
                    //�ڿ۹̶�ֵ
                    if (FeeCalModeType.equals("1"))
                    {
                    	ManaFeeMoney = ManaFeeMoney + mValue;
                    }
                    else
                    {
                    	ManaFeeMoney = ManaFeeMoney + mLMRiskFeeSchema.getFeeValue();
                    }
                }
                else
                if (calMode.equals("02"))
                {
                    //�ڿ۱���
                    if (FeeCalModeType.equals("1"))
                    {
                    	ManaFeeMoney = ManaFeeMoney + mValue;
                    }
                    else
                    {
                    	double tanaFeeRate = mLMRiskFeeSchema.getFeeValue();
                    	
                    	ManaFeeMoney = ManaFeeMoney + CManageFee.calInnerManaFee(prem,tanaFeeRate);
                    }
                }
            }
        }
        else
        {
            for (int u = 1; u <= mLCGrpFeeSet.size(); u++)
            {
                LCGrpFeeSchema tFeeSchema = mLCGrpFeeSet.get(u);

                String calMode = tFeeSchema.getFeeCalMode();

                if (calMode == null || "01".equals(calMode))
                {
                    //�ڿ۹̶�ֵ
                	ManaFeeMoney = ManaFeeMoney + tFeeSchema.getFeeValue();
                }
                else
                if (calMode.equals("02"))
                {
                    //�ڿ۱���
                	double tanaFeeRate = tFeeSchema.getFeeValue();
                	ManaFeeMoney = ManaFeeMoney + CManageFee.calInnerManaFee(prem,tanaFeeRate);
                }                            
            }
        }
        
    	return ManaFeeMoney;
    }
}

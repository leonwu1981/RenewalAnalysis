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
    /**存放结果*/
    public VData mVResult = new VData();


    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();


    /** 往后面传输数据的容器 */
    private VData mInputData;

    /** 保存更新sql语句 */
    private MMap upDate=new MMap();
    
    /**管理费描述*/
    private LCGrpFeeSet mLCGrpFeeSet;


    /**保费项表*/
    private LCPremSet mLCPremSet;


    /**险种*/
    private LCPolSchema mLCPolSchema;
    
    /**责任 */
    private LCDutySet mLCDutySet;
    
    public String mFeeType = "1";//管理费类型


    /**保险账户管理费分类表*/
    private LCInsureAccFeeTraceSet mLCInsureAccFeeTraceSet;
    private LCInsureAccClassFeeSet mLCInsureAccClassFeeSet;
    private LCInsureAccFeeSet mLCInsureAccFeeSet;
    private LCInsureAccSet mLCInsureAccSet;
    private LCPremToAccSet mLCPremToAccSet;
    private LCInsureAccClassSet mInsureAccClassSet;
    private LCInsureAccTraceSet mLCInsureAccTraceSet;


    /**保全操作标识*/
    private String mBQFlag = "";


    //add by frost 计算基础要素类,用于计算管理费
    private Calculator mCalculator = new Calculator();

    public CManageFee()
    {
    }

    public CManageFee(String tBQFlag)
    {
        mBQFlag = tBQFlag;
    }


    /**
     * 初始化
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
        //得到生成的保险帐户表
        mLCInsureAccSet = (LCInsureAccSet) (mInputData.getObjectByObjectName(
                "LCInsureAccSet", 0));

        //得到生成的缴费帐户关联表
        mLCPremToAccSet = (LCPremToAccSet) (mInputData.getObjectByObjectName(
                "LCPremToAccSet", 0));

        mInsureAccClassSet = (LCInsureAccClassSet) mInputData.
                             getObjectByObjectName(
                "LCInsureAccClassSet", 0);

        //得到领取帐户关联表--目前不用
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
            CError.buildErr(this, "传入保单信息不能为空");
            // return false;
        }
        else
        {
            //保全追加保险费时传入管理费比例
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
     * 数据校验
     * @return boolean
     */
    private boolean checkData()
    {

        if (mInputData == null)
        {
            System.out.println("请先调用初始化函数:Initialize");
            CError.buildErr(this, "请先调用初始化函数:Initialize");
            return false;
        }
        if (mLCPolSchema == null
            || mLCInsureAccClassFeeSet == null)
        {
            CError.buildErr(this, "初始化信息不正确!");
            return false;
        }

        if (mLCPremSet == null)
        {
            CError.buildErr(this, "必须传入保费信息");
            return false;
        }

        if (mLCGrpFeeSet == null)
        {
            CError.buildErr(this, "初始化集体险种管理费描述信息");
            return false;
        }

        return true;

    }


    /**
     * 从数据苦中查询保单对应需要处理账户的所有保费项
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
            // @@错误处理
            this.mErrors.copyAllErrors(tLCPremDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLCPrem";
            tError.errorMessage = "保费项表查询失败!";
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
     * 查询匹配的保费项
     * @param payPlanCode String
     * @return LCPremSchema
     */
    private LCPremSchema getLCPremSchema(String payPlanCode)
    {
        if (mLCPremSet == null)
        {
            CError.buildErr(this, "没有保费项信息!");
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
     * 查询匹配的保险账户管理费分类表
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
     * 查询匹配的保险账户管理费分类表
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
        //创建帐户轨迹记录
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
     * 查询团单管理费计算参数
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
            CError.buildErr(this, "参数表描述取值不唯一!");
            return null;
        }

        return tLCGrpFeeParamSet.get(1);

    }


    /**
     * 计算管理费内扣费率
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
	 * 计算管理费按固定比例一笔收取 中意3期
	 * 
	 * @param manaFee
	 *            double
	 * @param prem
	 *            double
	 * @return double
	 */
    private static double calZYRate(double prem, double rate)
    {
        return (prem * rate) / (1-rate);//中意算法
    }

    /**
     * 计算管理费外缴费率
     * @param manaFee double
     * @param prem double
     * @return double
     */
    private static double calOutRate(double manaFee, double prem)
    {
        return manaFee / (prem - manaFee);
    }


    /**
     * 计算管理费 － 内扣
     * @param prem double
     * @param rate double
     * @return double
     */
    private static double calInnerManaFee(double prem, double rate)
    {
        return prem * rate;
    }


    /**
     * 计算管理费 -外缴
     * @param prem double
     * @param rate double
     * @return double
     */
    private static double calOutManaFee(double prem, double rate)
    {
        return (prem * rate) / (1 + rate);
    }


    /**
     * 固定值和比例结合，取较小值
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
     * 固定值和比例结合，取较大值
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
     * 该方法有缺陷，不能用于签单程序，因为传入的数据中的得到的是保单号，可是库中的数据是
     * 尚未签单的数据，只有投保单号。
     * 保险账户资金注入(类型3 针对保费项,注意没有给出注入资金，内部会调用计算金额的函数)
     * 适用于：在生成帐户结构后，此时数据尚未提交到数据库，又需要执行帐户的资金注入。
     * 即在使用了 createInsureAcc()方法后，得到VData数据，接着修改VData中帐户的金额
     * @param tVData VData 使用了 createInsureAcc()方法后，得到的VData数据
     * @param AccCreatePos String 参见 险种保险帐户缴费 LMRiskAccPay
     * @param OtherNo String 参见 保险帐户表 LCInsureAcc
     * @param OtherNoType String 号码类型
     * @param MoneyType String 参见 保险帐户表记价履历表 LCInsureAccTrace
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
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "addPrem";
            tError.errorMessage = "传入数据不能为空";
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

            //判断是否帐户相关
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

				// 如果当前保费项和当前的缴费帐户关联表的保单号，责任编码，交费计划编码相同
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

//                // 如果当前保费项和当前的缴费帐户关联表的保单号，责任编码，交费计划编码相同
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

                //只有注入比例大于0的时候才注入账户金额,否则不做
                if (tLCPremToAccSchema.getRate() <= 0)
                {
                    continue;
                }
                double inputMoney = 0;
                double manaFee = 0;
                //tLCPremToAccSchema.getRate()当前都是1
                double baseMoney = Arith.round(tLCPremSchema.getPrem() *
                                   tLCPremToAccSchema.getRate(),2);
                
                
                //投连产品
                LMRiskAppDB tLMRiskAppDB = new LMRiskAppDB();
                tLMRiskAppDB.setRiskCode(mLCPolSchema.getRiskCode());
                if(!tLMRiskAppDB.getInfo()){
                	CError.buildErr(this, "查找LMRiskApp失败，险种编码："+mLCPolSchema.getRiskCode());
                    return false;
                }
                /*对投资计划的处理*/
                double rate=-1;//根据-1标记确定是否录入有投资比例.
                
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
						if (m == tLCPremToAccSet.size()) {// 最后一个用减法
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
                
                //直接计算，不需要管理费
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
                        CError.buildErr(this, "管理费计算失败!");
                        return false;
                    }

                }
                //PubFun.setPrecision(manaFee, "0.00");
                //保留两位精度
                manaFee = Arith.round(manaFee, 2);
                boolean hasFeeRate=false;//判断缴费是否有管理费
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
	                    //查找相关的管理费账户分类表
	                    if (tClsFeeSchema.getPolNo().equals(tLCPremToAccSchema.getPolNo())
	                        &&tClsFeeSchema.getInsuAccNo().equals(tLCPremToAccSchema.getInsuAccNo())
	                        &&tClsFeeSchema.getPayPlanCode().equals(tLCPremToAccSchema.getPayPlanCode())){
	                        for (int u = 1; u <= mLCGrpFeeSet.size(); u++){
	                            LCGrpFeeSchema tFeeSchema = mLCGrpFeeSet.get(u);
	                            //查找相关的管理费描述描述
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
	                            		if(riskNum.equals("1"))  //是投连，万能产品
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
	                            		if(rate==-1){//防止没有多账户的费率为空的情况,即万能型没有平衡平稳.
	                            			rate=1.0;
	                            		}
	                                //if(calMode.equals("09")&&tClsFeeSchema.getAccType().equals("001")){
	                            		//chenwm071117 如果总的管理费addFee为0,则是该保费的第一个账户,账户按比例四舍五入算出管理费后
	                            		//添加到总的管理费里,如果总的管理费addFee不为0,则是该保费的第二个账户,账户算的管理费应该用没有
	                            		//乘过比例的管理费减去已经存在总的管理费的第一个账户的管理费,然后在累加到总的管理费里.
	                            		//注意:这仅适合于现在中意这边一个保费下有1个或者2个账户的情况,其它的可能就需要改程序.
	                            		if(addFee==0.0){
	                                		manaFee=manaFee*rate;//如果是投连,按投资比例保存管理费到账户
	                                		manaFee=Arith.round(manaFee, 2);
	                                		addFee=manaFee;
	                            		}else{
	                                		manaFee=Arith.round(manaFee, 2);
	                            			manaFee=manaFee-addFee;
	                                		addFee=addFee+manaFee;//如果一个保费有多个账户,例如平衡平稳 这需要累加.
	                                		
	                            		}
	                            		inputMoney = baseMoney;//账户里面的钱不变
	                                	addFlag=true;
	                                	//重新设置一下管理费,因为在算管理费的时候顺便存上的manafee没有乘以投资比例.
	                                    tClsFeeSchema.setFee(Arith.round(manaFee, 2));
	                            		}
	                            		else  //和以下一行else内容相同
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
                    CError.buildErr(this, "管理费计算错误,不能大于保费");
                    return false;
                }
               // PubFun.setPrecision(inputMoney, "0.00");
                Arith.round(inputMoney, 2);
                //更新管理费账户表
                updateLCInsureAccFee(tLCPremToAccSchema.getInsuAccNo(), manaFee);
                
                //生成管理费轨迹表 不用更新管理费账户分类表，这个表已经在计算管理费时更新了
                createLCInsureAccFeeTrace(tLCPremToAccSchema.getInsuAccNo(),
                                          manaFee,
                                          tLCPremToAccSchema.getPayPlanCode());

                //更新账户分类表 
                updateLCInsuerAccClass(OtherNo, OtherNoType, MoneyType,
                                       tLCPremSchema, tClassSchema
                                       , inputMoney, tLCPremToAccSchema);
                //更新账户表 
                updateInsureAcc(OtherNo, OtherNoType, MoneyType, inputMoney,
                                tLCPremSchema
                                , tLCPremToAccSchema);

            }
            if(addFlag){
            	tLCPremSchema.setPrem(Arith.add(tLCPremSchema.getPrem() ,addFee));//保费要加上外扣的管理费
            	//保全的保留原始保费
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
                    CError.buildErr(this, "按照一定比例一笔外扣管理费时,更新lccont表的保费出错.");
                    return false;
            	}
            	String mSQL2= new String();
            	mSQL2="UPDATE lcduty SET prem=prem+"+manaFee+",sumprem=sumprem+"+manaFee
        			+",standprem=standprem+"+manaFee+" WHERE contno in(select contno from lccont"
        			+" where grpcontno='"+mLCPolSchema.getGrpContNo()+"' and insuredno='"+mLCPolSchema.getInsuredNo()
        			+"') and dutycode='"+tLCPremSchema.getDutyCode()+"'";
				upDate.put(mSQL2, "UPDATE");
            	if(!tExeSQL.execUpdateSQL(mSQL2)){
                    CError.buildErr(this, "按照一定比例一笔外扣管理费时,更新lcduty表的保费出错.");
                    return false;
            	}
*/	            	String mSQL3= new String();
				mSQL3="UPDATE lcgrpcont SET prem=prem+"+addFee
        			+" WHERE grpcontno='"+mLCPolSchema.getGrpContNo()+"'";
				upDate.put(mSQL3, "UPDATE");
/*					if(!tExeSQL.execUpdateSQL(mSQL3)){
                    CError.buildErr(this, "按照一定比例一笔外扣管理费时,更新lcgrpcont表的保费出错.");
                    return false;
            	}
*/                	String mSQL4= new String();
				mSQL4="UPDATE lcgrppol SET prem=prem+"+addFee
        			+" WHERE grppolno='"+mLCPolSchema.getGrpPolNo()+"' and riskcode='"
        			+mLCPolSchema.getRiskCode()+"'";
				upDate.put(mSQL4, "UPDATE");
/*					if(!tExeSQL.execUpdateSQL(mSQL4)){
                    CError.buildErr(this, "按照一定比例一笔外扣管理费时,更新lcgrppol表的保费出错.");
                    return false;
            	}
*/              }
        }
        //20070604 万能和投连特殊处理  每月管理费和资产管理费由保全处理

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
        //管理非费账户
        LCInsureAccClassFeeSchema tLCInsureAccClassFeeSchema =
            getLCInsureAccClassFee
            (tInsuAccNo, tPayPlanCode);
        if (tLCInsureAccClassFeeSchema == null) {
            System.out.println("没有从查找到对应的管理费账户");
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
	        //创建保险帐户管理费轨迹记表
	        createFeeTrace(tLCInsureAccClassFeeSchema, manaFee, "GL", tFeeCode);
        }

    }
    
    /**
     *计算投资比例
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
		if(!"2".equals(tLMRiskInsuAccDB.getAccKind()))//非投资比例账户不用拆分
			return tRate;
		LCPerInvestPlanDB tLCPerInvestPlanDB = new LCPerInvestPlanDB();
		tLCPerInvestPlanDB.setPolNo(mLCPolSchema.getProposalNo());
		if (this.mBQFlag != null && !"".equals(this.mBQFlag))// 保全
			tLCPerInvestPlanDB.setPolNo(mLCPolSchema.getPolNo());
		tLCPerInvestPlanDB.setPayPlanCode(PayPlanCode);
		tLCPerInvestPlanDB.setInsuAccNo(InsuAccNo);
		if (tLCPerInvestPlanDB.getInfo())// 个人投资计划
		{
			if (tLCPerInvestPlanDB.getInputMode().equals("1")) { // 按照比例
				tRate = tLCPerInvestPlanDB.getInvestRate();
			} else {// 按照金额
				tRate = Arith.div(tLCPerInvestPlanDB.getInvestMoney()
						, Double
								.parseDouble(tExeSQL
										.getOneValue("select nvl(sum(InvestMoney),0) from LCPerInvestPlan where polno='"
												+ tLCPerInvestPlanDB.getPolNo()
												+ "' and payplancode='"
												+ PayPlanCode + "'")));
			}
		} else { // 个人没有投资计划 查找个人使用的团体投资计划
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
					if (tLCGrpIvstPlanDB.getInputMode().equals("1")) { // 按照比例
						tRate = tLCGrpIvstPlanDB.getInvestRate();
					} else { // 按照金额
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
					CError.buildErr(this, "查找被保险人（客户号："
							+ tLCPolSchema.getInsuredNo() + "）的投资比例失败！");
					throw new Exception("查找被保险人（客户号："
							+ tLCPolSchema.getInsuredNo() + "）的投资比例失败！");
				}
			} else {
				CError.buildErr(this, "查找被保险人（客户号："
						+ tLCPolSchema.getInsuredNo() + "）的投资比例失败！");
				throw new Exception("查找被保险人（客户号：" + tLCPolSchema.getInsuredNo()
						+ "）的投资比例失败！");
			}
		}
		return tRate;
	}
    
    
    /**
	 * 计算管理费
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
        if (mLCGrpFeeSet.size() == 0) //如果界面上没有输入管理费，则根据描述计算
//        if (mLCGrpFeeSet == null)
        {
            //add by frost 2005-7-21
            LCPremSchema mLCPremSchema = new LCPremSchema();
            mLCPremSchema = getLCPremSchema(tLCPremToAccSchema.getPayPlanCode());

            for (int t = 1; t <= mLCInsureAccClassFeeSet.size(); t++)
            {
                LCInsureAccClassFeeSchema tClsFeeSchema =
                        mLCInsureAccClassFeeSet.get(t);
                //查找相关的管理费账户分类表
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
                        //查找相关的管理费描述
                        String FeeCalModeType = mLMRiskFeeSchema.
                                                getFeeCalModeType();
                        String calMode = mLMRiskFeeSchema.getFeeCalMode();
                        double mValue = 0;
                        //如果值需从SQL
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
                            //内扣固定值
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
                            //内扣比例
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
                            //外缴-固定值
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
                            //外缴-比例值
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
                            //固定值，比例计算后取较小值
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
                            //固定值，比例计算后取较大值
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
                        /* else //不知怎么实现
                         if (calMode.equals("07")) { //分档计算
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
                            //累计分党计算
                            //需求不明确，尚未完成
                            manaFeeRate = 0;
                            manaFee = 0;
                        }else if (calMode.equals("09")){
                        	this.mFeeType = "2";
                            //chenwm20071106 中意 09-按固定比例一笔收取
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
        } //结束
        else
        {
            //计算从界面上录的管理费，现只有团单
            for (int t = 1; t <= mLCInsureAccClassFeeSet.size(); t++)
            {
                LCInsureAccClassFeeSchema tClsFeeSchema =
                        mLCInsureAccClassFeeSet.get(t);
                //查找相关的管理费账户分类表
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

                        //查找相关的管理费描述描述
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
                                //内扣固定值
                                manaFee = tFeeSchema.getFeeValue();
                                //保费为0的情况是允许的不需要报错
//                                if(baseMoney==0){
//                                    // @@错误处理
//                                    CError tError = new CError();
//                                    tError.moduleName = "CManageFee";
//                                    tError.functionName = "computeManaFee";
//                                    tError.errorMessage = "传入保费不能为空!";
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
                                //内扣比例
                                manaFeeRate = tFeeSchema.getFeeValue();
                                manaFee = CManageFee.calInnerManaFee(baseMoney,
                                        manaFeeRate);
                            }
                            else
                            if (calMode.equals("03"))
                            {
                                //外缴-固定值
                                manaFee = tFeeSchema.getFeeValue();
                                manaFeeRate = CManageFee.calOutRate(manaFee,
                                        baseMoney);
                            }
                            else
                            if (calMode.equals("04"))
                            {
                                //外缴-比例值
                                manaFeeRate = tFeeSchema.getFeeValue();
                                manaFee = CManageFee.calOutManaFee(baseMoney,
                                        manaFeeRate);
                            }
                            else
                            if (calMode.equals("05"))
                            {
                                //固定值，比例计算后取较小值
                                manaFeeRate = tFeeSchema.getFeeValue();
                                manaFee = CManageFee.calManaFeeMinRate(
                                        baseMoney
                                        , tFeeSchema.getCompareValue(),
                                        manaFeeRate);
                            }
                            else
                            if (calMode.equals("06"))
                            {
                                //固定值，比例计算后取较大值
                                manaFeeRate = tFeeSchema.getFeeValue();
                                manaFee = CManageFee.calManaFeeMaxRate(
                                        baseMoney
                                        , tFeeSchema.getCompareValue(),
                                        manaFeeRate);
                            }
                            else
                            if (calMode.equals("07"))
                            {
                                //分档计算
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
                                //累计分党计算
                                //需求不明确，尚未完成
                                manaFeeRate = 0;
                                manaFee = 0;
                            }else if (calMode.equals("09")){
                                //chenwm20071106 中意 09-按固定比例一笔收取
                            	//String tOrganComCode="0000";
                            	String tOrganInnerCode="86";
                            	this.mFeeType = "2";
                            	LMDutyPayDB tLMDutyPayDB=new LMDutyPayDB();
                            	tLMDutyPayDB.setPayPlanCode(tClsFeeSchema.getPayPlanCode());
                            	tLMDutyPayDB.getInfo();
                            	if("3".equals(tLMDutyPayDB.getAccPayClass())){
                            		//只有公共账户时才扣,个人账户不扣;判断公共帐户的个数，分别调用不同的逻辑。二期一般只有一个公共帐户(如果有多个公共帐户，需将其中一个payplancode以03结尾)，三期可有多个公共帐户
                            		ExeSQL tExeSQL = new ExeSQL();
                            		boolean flag = false;
                            		
                            		String riskStr = " select count(1) from lmriskapp where RiskType3 in('3','4') and riskcode='"+tClsFeeSchema.getRiskCode()+"' ";
                            		String riskNum = tExeSQL.getOneValue(riskStr);
                            		if(riskNum.equals("1"))  //是投连，万能产品
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
                                        CError.buildErr(this,"查询公共帐户失败!"); 
                                        return -1;
                            		}else{
                            			//tOrganComCode=tLCInsuredSet.get(1).getOrganComCode();
                            			tOrganInnerCode=tLCInsuredSet.get(1).getOrganInnerCode();
                            		}
//Amended By Fang for Change1(外扣管理费需要存在对应公共账户)
//                            		//多公共帐户的时候只取有该险种的并且内部编码最小的那个公共帐户
//                            		String tSQL="SELECT MIN(organinnercode) FROM lcinsured WHERE exists ("
//                            			+"SELECT 1 FROM lcpol WHERE grpcontno='"+tClsFeeSchema.getGrpContNo()
//                            			+"' AND riskcode='"+tClsFeeSchema.getRiskCode()+"' AND poltypeflag='2' and contno =lcinsured.contno ) and grpcontno = '"+
//                            			tClsFeeSchema.getGrpContNo()+"'";
//                            		String Min_organinnercode;
//                            		String tEdorNo = null;
//                            		String tEdorType = null;
//                            		if (!"".equals(StrTool.cTrim(this.mBQFlag))) {
//										// 追加保费
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
//                            			//TODO:增加保全逻辑
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
                            			// 追加保费
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
                            6));//chenwm20071102 保持和数据库一样
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
            //如果当前缴费帐户关联表的保单号，账户号和当前的账户表的保单号，账户号相同并且资金不为0，将资金注入
            LCInsureAccSchema tLCInsureAccSchema = mLCInsureAccSet.get(j);
            if (tLCPremToAccSchema.getPolNo().equals(tLCInsureAccSchema.
                    getPolNo())
                &&
                tLCPremToAccSchema.getInsuAccNo().equals(tLCInsureAccSchema.
                    getInsuAccNo()))
            {
                //修改保险帐户金额
                tLCInsureAccSchema.setInsuAccBala(PubFun.setPrecision(
                        tLCInsureAccSchema.
                        getInsuAccBala() + inputMoney, "0.00"));
                tLCInsureAccSchema.setSumPay(PubFun.setPrecision(
                        tLCInsureAccSchema.getSumPay()
                        + inputMoney, "0.00"));
                tLCInsureAccSchema.setLastAccBala(PubFun.setPrecision(
                        tLCInsureAccSchema.
                        getLastAccBala() + inputMoney, "0.00"));

                //查询险种保险帐户缴费
//                LMRiskAccPaySchema
//                        tLMRiskAccPaySchema =
//                        queryLMRiskAccPay3(
//                        mLCPolSchema.getRiskCode(),
//                        tLCPremToAccSchema);
//                if (tLMRiskAccPaySchema == null)
//                {
//                    // return null;
//                    System.out.println("查询描述表错误");
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
     * 更新帐户分类表
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
        //累计账户分类ss
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
                //更新帐户累计保费、初始帐户金额＆帐户余额
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
                //根据帐户分类表生成对应的帐户轨迹表数据
                LCInsureAccTraceSchema tmpLCInsureAccTraceSchema =
                        createAccTrace(OtherNo, OtherNoType
                                       , MoneyType, inputMoney, tLCPremSchema,
                                       tLCInsureAccClassSchema);
                mLCInsureAccTraceSet.add(tmpLCInsureAccTraceSchema);

            }
            //根据帐户分类表生成对应的帐户轨迹表数据
            //移入条件判断内－－－Alex 20050920
//            LCInsureAccTraceSchema tmpLCInsureAccTraceSchema = createAccTrace(OtherNo, OtherNoType
//                    , MoneyType, inputMoney, tLCPremSchema, tLCInsureAccClassSchema);
//            mLCInsureAccTraceSet.add(tmpLCInsureAccTraceSchema);

        }
    }


    /**
     * 更新管理非账户表
     * @param InsuAccNo String
     * @param fee double
     */
    private void updateLCInsureAccFee(String InsuAccNo, double fee)
    {
        //管理非费账户
        LCInsureAccFeeSchema tLCInsureAccFeeSchema = getLCInsureAccFee(
                InsuAccNo);
        if (tLCInsureAccFeeSchema == null)
        {
            System.out.println("没有从查找到对应的管理费账户");
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
     * 创建账户轨迹表
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

        //填充保险帐户表记价履历表
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
        //默认未归属，设置轨迹表中的归属属性为帐户分类表的属性
        tLCInsureAccTraceSchema.setAccAscription(tLCInsureAccClassSchema.
                                                 getAccAscription());
        //设置交费日期
        tLCInsureAccTraceSchema.setPayDate(tLCInsureAccClassSchema.getBalaDate()); //设置为财务到帐日期 Alex 20051124
        tLCInsureAccTraceSchema.setMainPayDate(tLCInsureAccClassSchema.getBalaDate()); // ASR20095140-年金保全的三年领取
        tLCInsureAccTraceSchema.setMainSerialNo(serNo);  // 新入的保费MainSerialNo为自己的serNo,mainpaydate为自己的paydate
        
        tLCInsureAccTraceSchema.setMakeDate(CurrentDate);
        tLCInsureAccTraceSchema.setMakeTime(CurrentTime);
        tLCInsureAccTraceSchema.setModifyDate(CurrentDate);
        tLCInsureAccTraceSchema.setModifyTime(CurrentTime);
        tLCInsureAccTraceSchema.setOperator(tLCPremSchema.getOperator());
        
        //存储保证利率
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
     * 查询保证利率
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
     * 查询险种保险帐户缴费表3
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
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay3";
            tError.errorMessage = "传入数据不能为空!";
            this.mErrors.addOneError(tError);

            return null;
        }

        String payPlanCode = pLCPremToAccSchema.getPayPlanCode();
        String InsuAccNo = pLCPremToAccSchema.getInsuAccNo();

        //查询险种保险帐户缴费表
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
            // @@错误处理
            this.mErrors.copyAllErrors(tLMRiskAccPayDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay2";
            tError.errorMessage = "险种保险帐户缴费表查询失败!";
            this.mErrors.addOneError(tError);
            tLMRiskAccPaySet.clear();

            return null;
        }
        if (tLMRiskAccPaySet.size() == 0)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMRiskAccPayDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay2";
            tError.errorMessage = "险种保险帐户缴费表没有查询到相关数据!";
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
    	
        if (tLCGrpFeeSet.size() == 0) //如果界面上没有输入管理费，则根据描述计算
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
                //查找相关的管理费描述
                String FeeCalModeType = mLMRiskFeeSchema.getFeeCalModeType();
                
                String calMode = mLMRiskFeeSchema.getFeeCalMode();
                double mValue = 0;
                //如果值需从SQL
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
                    //内扣固定值
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
                    //内扣比例
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
                    //内扣固定值
                	ManaFeeMoney = ManaFeeMoney + tFeeSchema.getFeeValue();
                }
                else
                if (calMode.equals("02"))
                {
                    //内扣比例
                	double tanaFeeRate = tFeeSchema.getFeeValue();
                	ManaFeeMoney = ManaFeeMoney + CManageFee.calInnerManaFee(prem,tanaFeeRate);
                }                            
            }
        }
        
    	return ManaFeeMoney;
    }
}

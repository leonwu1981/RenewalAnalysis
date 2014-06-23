/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.sql.Connection;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vdb.LCInsureAccTraceDBSet;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import java.util.Date;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author hzm
 * @version 1.0
 */
public class DealAccount
{
    /**
     * 成员变量
     */
    public CErrors mErrors = new CErrors(); //错误类
    private String CurrentDate = PubFun.getCurrentDate(); //系统当前时间
    private String CurrentTime = PubFun.getCurrentTime();
    private String tLimit = ""; //流水号
    private String serNo = "";
    private String mEnterAccDate = CurrentDate; //资金到帐日期

    private GlobalInput mGlobalInput = null;
    public DealAccount()
    {
    }

    public DealAccount(GlobalInput inGlobalInput)
    {
        this.mGlobalInput = inGlobalInput;
    }


    /**
     * 生成保险帐户(生成结构:构建保险账户表,构建保费项表和客户账户表的关联表,构建给付项表和客户账户表的关联表)
     * @param parmData (Type:TransferData include: PolNo，AccCreatePos，OtherNo，OtherNoType，Rate)
     * @return VData (include: LCInsureAccSet，LCPremToAccSet，LCGetToAccSet)
     */
    public VData createInsureAcc(TransferData parmData)
    {
        //1-检验
        if (!checkTransferData(parmData))
        {
            return null;
        }

        //2-得到数据后用
        String tPolNo = (String) parmData.getValueByName("PolNo");
        String tAccCreatePos = (String) parmData.getValueByName("AccCreatePos");
        String tOtherNo = (String) parmData.getValueByName("OtherNo");
        String tOtherNoType = (String) parmData.getValueByName("OtherNoType");
        Double tRate;
        if (parmData.getValueByName("Rate") == null)
        {
            tRate = null;
        }
        else if (parmData.getValueByName("Rate").getClass().getName().equals(
                "java.lang.String"))
        {
            String strRate = (String) parmData.getValueByName("Rate");
            tRate = Double.valueOf(strRate);
        }
        else
        {
            tRate = (Double) parmData.getValueByName("Rate");
        }
        System.out.println("费率:" + tRate);

        //3-构建保险账户表
        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet();
        tLCInsureAccSet = getLCInsureAcc(tPolNo, tAccCreatePos, tOtherNo,
                                         tOtherNoType);
        if (tLCInsureAccSet == null)
        {
            return null;
        }

        //4-构建保费项表和客户账户表的关联表
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet();
        tLCPremToAccSet = getPremToAcc(tPolNo, tAccCreatePos, tRate);

        //if(tLCPremToAccSet==null) return null;
        //5-构建给付项表和客户账户表的关联表
        LCGetToAccSet tLCGetToAccSet = new LCGetToAccSet();
        tLCGetToAccSet = getGetToAcc(tPolNo, tAccCreatePos, tRate);

        //if(tLCGetToAccSet==null) return null;
        //6-返回数据
        VData tVData = new VData();
        tVData.add(tLCInsureAccSet);
        tVData.add(tLCPremToAccSet); //可能是null
        tVData.add(tLCGetToAccSet); //可能是null

        return tVData;
    }


    /**
     * 对个人保单生成保险帐户表(类型 1：空帐户,不需要添加履历表纪录)
     * @param PolNo  保单号
     * @param AccCreatePos 生成位置 :1-投保单录入时产生 2－缴费时产生 3－领取时产生
     * @param OtherNo 保单号或交费号
     * @param OtherNoType  保单号或交费号
     * @return LCInsureAccSet
     */
    public LCInsureAccSet getLCInsureAcc(String PolNo, String AccCreatePos,
                                         String OtherNo, String OtherNoType)
    {
        if ((PolNo == null) || (AccCreatePos == null) || (OtherNo == null)
            || (OtherNoType == null))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "getLCInsureAcc";
            tError.errorMessage = "传入数据不能为空!";
            this.mErrors.addOneError(tError);

            return null;
        }

        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet(); //保险帐户表

        //1-查询保单表
        LCPolSchema tLCPolSchema = new LCPolSchema();
        tLCPolSchema = queryLCPol(PolNo);
        if (tLCPolSchema == null)
        {
            return null;
        }

        //2-根据投保单表中的险种字段查询LMRisk表
        LMRiskSchema tLMRiskSchema = new LMRiskSchema();
        tLMRiskSchema = queryLMRisk(tLCPolSchema.getRiskCode());
        if (tLMRiskSchema == null)
        {
            return null;
        }

        //3-判断是否与帐户相关
        if (tLMRiskSchema.getInsuAccFlag().equals("Y")
            || tLMRiskSchema.getInsuAccFlag().equals("y"))
        {
            //根据险种查询LMRiskToAcc表(险种账户关联表)
            LMRiskToAccSet tLMRiskToAccSet = new LMRiskToAccSet();
            tLMRiskToAccSet = queryLMRiskToAcc(tLCPolSchema.getRiskCode());
            if (tLMRiskToAccSet == null)
            {
                return null;
            }

            LMRiskToAccSchema tLMRiskToAccSchema = new LMRiskToAccSchema(); //险种账户关联表
            LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema(); //险种保险帐户
            LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema(); //保险帐户表

            for (int i = 1; i <= tLMRiskToAccSet.size(); i++)
            {
                //根据保险账户号码查询LMRiskInsuAcc表(险种保险帐户)
                tLMRiskToAccSchema = tLMRiskToAccSet.get(i);
                tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
                tLMRiskInsuAccSchema = queryLMRiskInsuAcc(tLMRiskToAccSchema
                        .getInsuAccNo());
                if (tLMRiskInsuAccSchema == null)
                {
                    //return con;
                    continue;
                }

//              LMRiskInsuAccSet tLMRiskInsuAccSet = queryLMRiskInsuAccSet(tLMRiskToAccSchema.getInsuAccNo());
//              if (tLMRiskInsuAccSet == null)
//              {
//                  CError.buildErr(this,"账户描述查询失败");
//                  return null;
//              }
//                  for ( int u=1;u<=tLMRiskInsuAccSet.size();u++)
//                  {
//                      tLMRiskInsuAccSchema = tLMRiskInsuAccSet.get(u);
                //如果帐户类型是集体帐户,退出
                if (tLMRiskInsuAccSchema.getAccType().equals("001"))
                {
                    //如果保单类型是-2 --（团单）公共帐户(例如：众悦年金的集体帐户，从界面录入个人时选择保单类型为2)
                    //此时这个代表集体的个人除了生成个人的账户外，多生成集体的账户
                    if ((tLCPolSchema.getPolTypeFlag() != null)
                        && tLCPolSchema.getPolTypeFlag().equals("2"))
                    {
                        System.out.println("需要生成集体帐户");
                    }
                    else
                    {
                        continue;
                    }
                }

                //生成保险账户表
                //如果账户生成位置找到匹配的保险账户
                if (tLMRiskInsuAccSchema.getAccCreatePos().equals(AccCreatePos))
                {
                    tLCInsureAccSchema = new LCInsureAccSchema();
                    tLCInsureAccSchema.setPolNo(PolNo);
                    tLCInsureAccSchema.setInsuAccNo(tLMRiskInsuAccSchema.
                            getInsuAccNo());
                    tLCInsureAccSchema.setRiskCode(tLMRiskToAccSchema.
                            getRiskCode());
                    tLCInsureAccSchema.setAccType(tLMRiskInsuAccSchema.
                                                  getAccType());
                    //  tLCInsureAccSchema.setOtherNo(OtherNo);
                    //  tLCInsureAccSchema.setOtherType(OtherNoType);
                    tLCInsureAccSchema.setContNo(tLCPolSchema.getContNo());
                    tLCInsureAccSchema.setGrpPolNo(tLCPolSchema.getGrpPolNo());
                    tLCInsureAccSchema.setInsuredNo(tLCPolSchema.getInsuredNo());
//                    tLCInsureAccSchema.setAppntName(tLCPolSchema.
//                            getAppntName());
                    tLCInsureAccSchema.setSumPay(0);
                    tLCInsureAccSchema.setInsuAccBala(0);
                    tLCInsureAccSchema.setUnitCount(0);
                    tLCInsureAccSchema.setInsuAccGetMoney(0);
                    tLCInsureAccSchema.setSumPaym(0);
                    tLCInsureAccSchema.setFrozenMoney(0);
                    tLCInsureAccSchema.setAccComputeFlag(tLMRiskInsuAccSchema.
                            getAccComputeFlag());
                    tLCInsureAccSchema.setManageCom(tLCPolSchema.getManageCom());
                    tLCInsureAccSchema.setOperator(tLCPolSchema.getOperator());
                    tLCInsureAccSchema.setBalaDate(tLCPolSchema.getCValiDate());
                    tLCInsureAccSchema.setMakeDate(CurrentDate);
                    tLCInsureAccSchema.setMakeTime(CurrentTime);
                    tLCInsureAccSchema.setModifyDate(CurrentDate);
                    tLCInsureAccSchema.setModifyTime(CurrentTime);

                    //新增的
                    tLCInsureAccSchema.setGrpContNo(tLCPolSchema.getGrpContNo());
                    tLCInsureAccSchema.setPrtNo(tLCPolSchema.getPrtNo());
                    tLCInsureAccSchema.setAppntNo(tLCPolSchema.getAppntNo());

                    tLCInsureAccSet.add(tLCInsureAccSchema);
                }
                //                 }
            }

            return tLCInsureAccSet;
        }

        return null;
    }


    /**
     * 对集体保单生成保险帐户表(类型 1：空帐户,不需要添加履历表纪录)
     * @param GrpPolNo String 集体保单号
     * @param AccCreatePos String 生成位置 :1-投保单录入时产生 2－缴费时产生 3－领取时产生
     * @param OtherNo String 集体保单号或交费号
     * @param OtherNoType String 集体保单号或交费号
     * @return LCInsureAccSet
     */
    public LCInsureAccSet getLCInsureAccForGrp(String GrpPolNo,
                                               String AccCreatePos,
                                               String OtherNo
                                               , String OtherNoType)
    {
        if ((GrpPolNo == null) || (AccCreatePos == null) || (OtherNo == null)
            || (OtherNoType == null))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "getLCInsureAcc";
            tError.errorMessage = "传入数据不能为空!";
            this.mErrors.addOneError(tError);

            return null;
        }

        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet(); //保险帐户表

        //1-查询保单表
        LCGrpPolDB tLCGrpPolDB = new LCGrpPolDB();
        tLCGrpPolDB.setGrpPolNo(GrpPolNo);
        if (!tLCGrpPolDB.getInfo())
        {
            return null;
        }

        LCGrpPolSchema tLCGrppolSchema = tLCGrpPolDB.getSchema();

        //2-根据投保单表中的险种字段查询LMRisk表
        LMRiskSchema tLMRiskSchema = new LMRiskSchema();
        tLMRiskSchema = queryLMRisk(tLCGrppolSchema.getRiskCode());
        if (tLMRiskSchema == null)
        {
            return null;
        }

        //3-判断是否与帐户相关
        if (tLMRiskSchema.getInsuAccFlag().equals("Y")
            || tLMRiskSchema.getInsuAccFlag().equals("y"))
        {
            //根据险种查询LMRiskToAcc表(险种账户关联表)
            LMRiskToAccSet tLMRiskToAccSet = new LMRiskToAccSet();
            tLMRiskToAccSet = queryLMRiskToAcc(tLCGrppolSchema.getRiskCode());
            if (tLMRiskToAccSet == null)
            {
                return null;
            }

            LMRiskToAccSchema tLMRiskToAccSchema = new LMRiskToAccSchema(); //险种账户关联表
            LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema(); //险种保险帐户
            LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema(); //保险帐户表

            for (int i = 1; i <= tLMRiskToAccSet.size(); i++)
            {
                //根据保险账户号码查询LMRiskInsuAcc表(险种保险帐户)
                tLMRiskToAccSchema = tLMRiskToAccSet.get(i);
                tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
                tLMRiskInsuAccSchema = queryLMRiskInsuAcc(tLMRiskToAccSchema.
                        getInsuAccNo());
                if (tLMRiskInsuAccSchema == null)
                {
                    return null;
                }

                //如果帐户类型不是集体帐户,退出
                if (!tLMRiskInsuAccSchema.getAccType().equals("001"))
                {
                    continue;
                }

                //生成保险账户表
                //如果账户生成位置找到匹配的保险账户
                if (tLMRiskInsuAccSchema.getAccCreatePos().equals(AccCreatePos))
                {
                    tLCInsureAccSchema = new LCInsureAccSchema();
                    tLCInsureAccSchema.setPolNo(tLCGrppolSchema.getGrpPolNo());
                    tLCInsureAccSchema.setInsuAccNo(tLMRiskInsuAccSchema.
                            getInsuAccNo());
                    tLCInsureAccSchema.setRiskCode(tLMRiskToAccSchema.
                            getRiskCode());
                    tLCInsureAccSchema.setAccType(tLMRiskInsuAccSchema.
                                                  getAccType());
                    /*Lis5.3 upgrade get
                     tLCInsureAccSchema.setContNo(tLCGrppolSchema.getContNo());
                     */
                    tLCInsureAccSchema.setGrpPolNo(tLCGrppolSchema.getGrpPolNo());
                    tLCInsureAccSchema.setInsuredNo("0"); //因为是用集体的信息，没有被保人的客户号，所以填0
                    tLCInsureAccSchema.setSumPay(0);
                    tLCInsureAccSchema.setInsuAccBala(0);
                    tLCInsureAccSchema.setUnitCount(0);
                    tLCInsureAccSchema.setInsuAccGetMoney(0);
                    tLCInsureAccSchema.setSumPaym(0);
                    tLCInsureAccSchema.setFrozenMoney(0);
                    tLCInsureAccSchema.setAccComputeFlag(tLMRiskInsuAccSchema.
                            getAccComputeFlag());
                    tLCInsureAccSchema.setManageCom(tLCGrppolSchema.
                            getManageCom());
                    tLCInsureAccSchema.setOperator(tLCGrppolSchema.getOperator());
                    tLCInsureAccSchema.setBalaDate(tLCGrppolSchema.getCValiDate());
                    tLCInsureAccSchema.setMakeDate(CurrentDate);
                    tLCInsureAccSchema.setMakeTime(CurrentTime);
                    tLCInsureAccSchema.setModifyDate(CurrentDate);
                    tLCInsureAccSchema.setModifyTime(CurrentTime);
                    tLCInsureAccSet.add(tLCInsureAccSchema);
                }
            }

            return tLCInsureAccSet;
        }

        return null;
    }


    /**
     * 生成保险帐户表(类型 2:将生成帐户和注入资金合而为一,需要添加履历表纪录,注意：已经给出要注入的资金 )
     * @param PolNo  保单号
     * @param AccCreatePos 生成位置 :1-投保单录入时产生 2－缴费时产生 3－领取时产生
     * @param OtherNo 保单号或交费号
     * @param OtherNoType 保单号或交费号
     * @param ManageCom 登陆机构
     * @param AccType 账号类型: 001-集体公共账户 002-个人缴费账户 003-个人储蓄账户 004-个人红利账户
     * @param MoneyType 金额类型:BF－保费 GL－管理费 HL－红利 LX－累积生息的利息
     * @param Money 存入帐户的金额
     * @return VData(LCInsureAccSet,LCInsureAccTraceSet)
     */
    public VData getLCInsureAcc(String PolNo, String AccCreatePos,
                                String OtherNo
                                , String OtherNoType, String ManageCom,
                                String AccType, String MoneyType, double Money)
    {
        if ((PolNo == null) || (AccCreatePos == null) || (OtherNo == null)
            || (OtherNoType == null) || (ManageCom == null)
            || (AccType == null) || (MoneyType == null))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "getLCInsureAcc";
            tError.errorMessage = "传入数据不能为空!";
            this.mErrors.addOneError(tError);

            return null;
        }

        //1-查询保单表
        LCPolSchema tLCPolSchema = new LCPolSchema();
        tLCPolSchema = queryLCPol(PolNo);
        if (tLCPolSchema == null)
        {
            return null;
        }

        //2-根据投保单表中的险种字段查询LMRisk表
        LMRiskSchema tLMRiskSchema = new LMRiskSchema();
        tLMRiskSchema = queryLMRisk(tLCPolSchema.getRiskCode());
        if (tLMRiskSchema == null)
        {
            return null;
        }

        //3-判断是否与帐户相关
        VData tVData = new VData();
        if (tLMRiskSchema.getInsuAccFlag().equals("Y")
            || tLMRiskSchema.getInsuAccFlag().equals("y"))
        {
            //根据险种查询LMRiskToAcc表(险种账户关联表)
            LMRiskToAccSet tLMRiskToAccSet = new LMRiskToAccSet();
            tLMRiskToAccSet = queryLMRiskToAcc(tLCPolSchema.getRiskCode());
            if (tLMRiskToAccSet == null)
            {
                return null;
            }

            LMRiskToAccSchema tLMRiskToAccSchema = new LMRiskToAccSchema(); //险种账户关联表
            LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema(); //险种保险帐户
            LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema(); //保险帐户表
            LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet(); //保险帐户表
            LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet(); //保险帐户表记价履历表
            LCInsureAccTraceSchema tLCInsureAccTraceSchema = new
                    LCInsureAccTraceSchema();
            for (int i = 1; i <= tLMRiskToAccSet.size(); i++)
            {
                //根据保险账户号码查询LMRiskInsuAcc表(险种保险帐户)
                tLMRiskToAccSchema = tLMRiskToAccSet.get(i);
                tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
                tLMRiskInsuAccSchema = queryLMRiskInsuAcc(tLMRiskToAccSchema.
                        getInsuAccNo()
                        , AccType);
                if (tLMRiskInsuAccSchema == null)
                {
                    return null;
                }

                //生成保险账户表
                //如果账户生成位置找到匹配的保险账户
                if (tLMRiskInsuAccSchema.getAccCreatePos().equals(AccCreatePos))
                {
                    tLCInsureAccSchema = new LCInsureAccSchema();
                    tLCInsureAccSchema.setPolNo(PolNo);
                    tLCInsureAccSchema.setInsuAccNo(tLMRiskInsuAccSchema.
                            getInsuAccNo());
                    tLCInsureAccSchema.setRiskCode(tLMRiskToAccSchema.
                            getRiskCode());
                    tLCInsureAccSchema.setAccType(tLMRiskInsuAccSchema.
                                                  getAccType());
                    //  tLCInsureAccSchema.setOtherNo(OtherNo);
                    //  tLCInsureAccSchema.setOtherType(OtherNoType);
                    tLCInsureAccSchema.setContNo(tLCPolSchema.getContNo());
                    tLCInsureAccSchema.setGrpPolNo(tLCPolSchema.getGrpPolNo());
                    tLCInsureAccSchema.setInsuredNo(tLCPolSchema.getInsuredNo());
//                    tLCInsureAccSchema.setAppntName(tLCPolSchema.getAppntName());
                    tLCInsureAccSchema.setSumPay(Money);
                    tLCInsureAccSchema.setInsuAccBala(Money);
                    tLCInsureAccSchema.setUnitCount(0);
                    tLCInsureAccSchema.setInsuAccGetMoney(0);
                    tLCInsureAccSchema.setFrozenMoney(0);
                    tLCInsureAccSchema.setAccComputeFlag(tLMRiskInsuAccSchema.
                            getAccComputeFlag());
                    tLCInsureAccSchema.setManageCom(ManageCom);
                    tLCInsureAccSchema.setOperator(tLCPolSchema.getOperator());
//                    tLCInsureAccSchema.setBalaDate(tLCPolSchema.getCValiDate());
                    tLCInsureAccSchema.setBalaDate(CurrentDate);
                    tLCInsureAccSchema.setMakeDate(CurrentDate);
                    tLCInsureAccSchema.setMakeTime(CurrentTime);
                    tLCInsureAccSchema.setModifyDate(CurrentDate);
                    tLCInsureAccSchema.setModifyTime(CurrentTime);
                    tLCInsureAccSet.add(tLCInsureAccSchema);

                    //填充保险帐户表记价履历表
                    tLimit = PubFun.getNoLimit(ManageCom);
                    serNo = PubFun1.CreateMaxNo("SERIALNO", tLimit);
                    tLCInsureAccTraceSchema = new LCInsureAccTraceSchema();
                    tLCInsureAccTraceSchema.setSerialNo(serNo);
//                    tLCInsureAccTraceSchema.setInsuredNo(tLCInsureAccSchema
//                            .getInsuredNo());
                    tLCInsureAccTraceSchema.setPolNo(tLCInsureAccSchema.
                            getPolNo());
                    tLCInsureAccTraceSchema.setMoneyType(MoneyType);
                    tLCInsureAccTraceSchema.setRiskCode(tLCInsureAccSchema.
                            getRiskCode());
                    tLCInsureAccTraceSchema.setOtherNo(OtherNo);
                    tLCInsureAccTraceSchema.setOtherType(OtherNoType);
                    tLCInsureAccTraceSchema.setMoney(Money);
                    tLCInsureAccTraceSchema.setContNo(tLCInsureAccSchema.
                            getContNo());
                    tLCInsureAccTraceSchema.setGrpPolNo(tLCInsureAccSchema.
                            getGrpPolNo());
                    tLCInsureAccTraceSchema.setInsuAccNo(tLCInsureAccSchema.
                            getInsuAccNo());
                    /*Lis5.3 upgrade set
                     tLCInsureAccTraceSchema.setAppntName(tLCInsureAccSchema
                                                         .getAppntName());
                     */
                    tLCInsureAccTraceSchema.setState(tLCInsureAccSchema.
                            getState());
                    tLCInsureAccTraceSchema.setManageCom(tLCInsureAccSchema.
                            getManageCom());
                    tLCInsureAccTraceSchema.setOperator(tLCInsureAccSchema.
                            getOperator());
                    tLCInsureAccTraceSchema.setMakeDate(CurrentDate);
                    tLCInsureAccTraceSchema.setMakeTime(CurrentTime);
                    tLCInsureAccTraceSchema.setModifyDate(CurrentDate);
                    tLCInsureAccTraceSchema.setModifyTime(CurrentTime);
                    tLCInsureAccTraceSchema.setPayDate(CurrentDate);

                    //添加容器
                    tLCInsureAccTraceSet.add(tLCInsureAccTraceSchema);
                }
            }
            if ((tLCInsureAccSet.size() == 0)
                || (tLCInsureAccTraceSet.size() == 0))
            {
                // @@错误处理
                //        CError tError =new CError();
                //        tError.moduleName="DealAccount";
                //        tError.functionName="addPrem";
                //        tError.errorMessage="条件不符合，没有生成纪录";
                //        this.mErrors .addOneError(tError) ;
                return null;
            }
            tVData.add(tLCInsureAccSet);
            tVData.add(tLCInsureAccTraceSet);

            return tVData;
        }
        else
        {
            //      CError tError = new CError();
            //      tError.moduleName = "DealAccount";
            //      tError.functionName = "getLCInsureAcc";
            //      tError.errorMessage = "险种定义纪录中保险帐户标记为N!";
            //      this.mErrors.addOneError(tError);
            return null;
        }
    }


    /**
     * 生成保费项表和客户帐户表的关联表
     * @param PolNo 保单号
     * @param AccCreatePos 生成位置
     * @param Rate 费率
     * @return LCPremToAccSet 保费项关联表
     */
    public LCPremToAccSet getPremToAcc(String PolNo, String AccCreatePos,
                                       Double Rate)
    {
        if ((PolNo == null) || (AccCreatePos == null))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "getPremToAcc";
            tError.errorMessage = "错误原因:传入参数不能为空";
            this.mErrors.addOneError(tError);

            return null;
        }

        String tPolNo = PolNo;
        String tAccCreatePos = AccCreatePos;
        Double tRate = Rate;
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet();

        //1-取出保费项表
        LCPremSet tLCPremSet = new LCPremSet();
        tLCPremSet = queryLCPrem(tPolNo);
        if (tLCPremSet == null)
        {
            return null;
        }

        //2-根据保费项表取出对应的责任缴费描述表
        VData tVData = new VData();
        tVData = getFromLMDutyPay(tLCPremSet);
        if (tVData == null)
        {
            return null;
        }

        //3-生成保费项表和客户账户表的关联表
        tLCPremToAccSet = createPremToAcc(tVData, tPolNo, tAccCreatePos, tRate);

        return tLCPremToAccSet;
    }


    /**
     * 保险账户资金注入(类型1 针对保费项,注意没有给出注入资金，内部会调用计算金额的函数)
     * @param pLCPremSchema 保费项
     * @param AccCreatePos  参见 险种保险帐户缴费 LMRiskAccPay
     * @param OtherNo  参见 保险帐户表 LCInsureAcc
     * @param OtherNoType  号码类型
     * @param MoneyType  参见 保险帐户表记价履历表 LCInsureAccTrace
     * @param Rate 费率
     * @return VData(tLCInsureAccSet:update or insert ,tLCInsureAccTraceSet: insert)
     */
    public VData addPrem(LCPremSchema pLCPremSchema, String AccCreatePos,
                         String OtherNo
                         , String OtherNoType, String MoneyType, Double Rate)
    {
        if ((pLCPremSchema == null) || (AccCreatePos == null)
            || (OtherNo == null) || (OtherNoType == null)
            || (MoneyType == null))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "addPrem";
            tError.errorMessage = "传入数据不能为空";
            this.mErrors.addOneError(tError);

            return null;
        }

        VData tVData = new VData();
        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet(); //保险帐户表
        LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema();
        LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet(); //保险帐户表记价履历表
        LCInsureAccTraceSchema tLCInsureAccTraceSchema = new
                LCInsureAccTraceSchema();
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet(); //保费项表和客户帐户表的关联表
        LCPremToAccSchema tLCPremToAccSchema = new LCPremToAccSchema();
        LMRiskAccPaySchema tLMRiskAccPaySchema = new LMRiskAccPaySchema();
        String newFlag = "";
//        boolean addPrem = false;
        double inputMoney = 0;

        //判断是否帐户相关
        if (pLCPremSchema.getNeedAcc().equals("1"))
        {
            tLCPremToAccSet = queryLCPremToAccSet(pLCPremSchema);
            if (tLCPremToAccSet == null)
            {
                return null;
            }

            TransferData tFData = new TransferData();
            LCInsureAccSet mLCInsureAccSet = new LCInsureAccSet();

            //判断生成位置是否匹配
            if (AccCreatePos.equals(tLCPremToAccSet.get(1).getNewFlag()))
            {
                //如果匹配：生成帐户(即对于每次交费都产生新账号的情况，参看LCInsureAcc-保险帐户表)
                tFData = new TransferData();
                tFData.setNameAndValue("PolNo", pLCPremSchema.getPolNo());
                tFData.setNameAndValue("OtherNo", OtherNo); //对于每次交费都产生新账号的情况，该字段存放交费号。主键
                tFData.setNameAndValue("OtherNoType", OtherNoType);
                tFData.setNameAndValue("Rate", Rate);
                tLCInsureAccSet = new LCInsureAccSet();
                mLCInsureAccSet = getLCInsureAcc(pLCPremSchema.getPolNo(),
                                                 AccCreatePos, OtherNo
                                                 , OtherNoType);
                if (mLCInsureAccSet == null)
                {
                    return null;
                }
                newFlag = "INSERT";
            }

            for (int i = 1; i <= tLCPremToAccSet.size(); i++)
            {
                tLCPremToAccSchema = new LCPremToAccSchema();
                tLCPremToAccSchema = tLCPremToAccSet.get(i);

                //计算实际应该注入的资金
                inputMoney = calInputMoney(tLCPremToAccSchema,
                                           pLCPremSchema.getPrem());
                if (inputMoney == -1)
                {
                    // @@错误处理
                    CError tError = new CError();
                    tError.moduleName = "DealAccount";
                    tError.functionName = "addPrem";
                    tError.errorMessage = "计算实际应该注入的资金出错";
                    this.mErrors.addOneError(tError);

                    return null;
                }
                if (newFlag.equals("INSERT"))
                {
                    //如果是新生成帐户
                    //根据保单号和保险账户号和其它号码查询mLCInsureAccSet集合中唯一一条数据
                    tLCInsureAccSchema = new LCInsureAccSchema();
                    tLCInsureAccSchema = queryLCInsureAccSet(pLCPremSchema.
                            getPolNo()
                            , tLCPremToAccSchema.getInsuAccNo(), OtherNo,
                            mLCInsureAccSet);
                    if (tLCInsureAccSchema == null)
                    {
                        return null;
                    }
                }
                else
                {
                    //根据保单号和保险账户号和其它号码查询LCInsureAcc表的唯一一条数据
                    tLCInsureAccSchema = new LCInsureAccSchema();
                    tLCInsureAccSchema = queryLCInsureAcc(pLCPremSchema.
                            getPolNo()
                            , tLCPremToAccSchema.getInsuAccNo(), OtherNo);
                    if (tLCInsureAccSchema == null)
                    {
                        return null;
                    }
                }

                //修改保险帐户金额
                tLCInsureAccSchema.setInsuAccBala(tLCInsureAccSchema.
                                                  getInsuAccBala() + inputMoney);
                tLCInsureAccSchema.setSumPay(tLCInsureAccSchema.getSumPay() +
                                             inputMoney);
                tLCInsureAccSchema.setModifyDate(CurrentDate);
                tLCInsureAccSchema.setModifyTime(CurrentTime);

                //tLCInsureAccSchema.setInsuAccGetMoney(tLCInsureAccSchema.getInsuAccGetMoney()+inputMoney);
                tLMRiskAccPaySchema = queryLMRiskAccPay2(tLCPremToAccSchema); //查询险种保险帐户缴费
                if (tLMRiskAccPaySchema == null)
                {
                    return null;
                }
                if (tLMRiskAccPaySchema.getPayNeedToAcc().equals("1") &&
                    (inputMoney != 0))
                {
                    //填充保险帐户表记价履历表
                    tLimit = PubFun.getNoLimit(pLCPremSchema.getManageCom());
                    serNo = PubFun1.CreateMaxNo("SERIALNO", tLimit);
                    tLCInsureAccTraceSchema = new LCInsureAccTraceSchema();
                    tLCInsureAccTraceSchema.setSerialNo(serNo);
//                    tLCInsureAccTraceSchema.setInsuredNo(tLCInsureAccSchema
//                            .getInsuredNo());
                    tLCInsureAccTraceSchema.setPolNo(tLCInsureAccSchema.
                            getPolNo());
                    tLCInsureAccTraceSchema.setMoneyType(MoneyType);
                    tLCInsureAccTraceSchema.setRiskCode(tLCInsureAccSchema.
                            getRiskCode());
                    tLCInsureAccTraceSchema.setOtherNo(OtherNo);
                    tLCInsureAccTraceSchema.setOtherType(OtherNoType);
                    tLCInsureAccTraceSchema.setMoney(inputMoney);
                    tLCInsureAccTraceSchema.setContNo(tLCInsureAccSchema.
                            getContNo());
                    tLCInsureAccTraceSchema.setGrpPolNo(tLCInsureAccSchema.
                            getGrpPolNo());
                    tLCInsureAccTraceSchema.setInsuAccNo(tLCInsureAccSchema.
                            getInsuAccNo());
                    tLCInsureAccTraceSchema.setState(tLCInsureAccSchema.
                            getState());
                    tLCInsureAccTraceSchema.setManageCom(tLCInsureAccSchema.
                            getManageCom());
                    tLCInsureAccTraceSchema.setOperator(tLCInsureAccSchema.
                            getOperator());
                    tLCInsureAccTraceSchema.setMakeDate(CurrentDate);
                    tLCInsureAccTraceSchema.setMakeTime(CurrentTime);
                    tLCInsureAccTraceSchema.setModifyDate(CurrentDate);
                    tLCInsureAccTraceSchema.setModifyTime(CurrentTime);
                    tLCInsureAccTraceSchema.setPayDate(CurrentDate);
                    tLCInsureAccTraceSet.add(tLCInsureAccTraceSchema);
                }

                //添加容器
                tLCInsureAccSet.add(tLCInsureAccSchema);
            }
        }
        if (tLCInsureAccSet.size() == 0)
        {
            // @@错误处理
            //      CError tError =new CError();
            //      tError.moduleName="DealAccount";
            //      tError.functionName="addPrem";
            //      tError.errorMessage="条件不符合，没有生成纪录";
            //      this.mErrors .addOneError(tError) ;
            return null;
        }
        tVData.add(tLCInsureAccSet);
        tVData.add(tLCInsureAccTraceSet);

        return tVData;

        //最后在操作VData时，（数据tLCInsureAccSet可能是update or insert）
        //因此操作数据库时先执行删除操作，再执行插入操作
    }


    /**
     * 该方法有缺陷，不能用于签单程序，因为传入的数据中的得到的是保单号，可是库中的数据是
     * 尚未签单的数据，只有投保单号。
     * 保险账户资金注入(类型3 针对保费项,注意没有给出注入资金，内部会调用计算金额的函数)
     * 适用于：在生成帐户结构后，此时数据尚未提交到数据库，又需要执行帐户的资金注入。
     * 即在使用了 createInsureAcc()方法后，得到VData数据，接着修改VData中帐户的金额
     * @param inVData VData 使用了 createInsureAcc()方法后，得到的VData数据
     * @param pLCPremSet LCPremSet 保费项集合
     * @param AccCreatePos String 参见 险种保险帐户缴费 LMRiskAccPay
     * @param OtherNo String 参见 保险帐户表 LCInsureAcc
     * @param OtherNoType String 号码类型
     * @param MoneyType String 参见 保险帐户表记价履历表 LCInsureAccTrace
     * @param RiskCode String 险种信息
     * @param Rate String 费率
     * @return VData
     */
    public VData addPremInner(VData inVData, LCPremSet pLCPremSet,
                              String AccCreatePos
                              , String OtherNo, String OtherNoType,
                              String MoneyType, String RiskCode, String Rate)
    {
        if ((inVData == null) || (pLCPremSet == null) || (AccCreatePos == null)
            || (OtherNo == null) || (OtherNoType == null)
            || (MoneyType == null) || (RiskCode == null))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "addPrem";
            tError.errorMessage = "传入数据不能为空";
            this.mErrors.addOneError(tError);

            return null;
        }

//        VData tVData = new VData();

        //得到生成的保险帐户表
        LCInsureAccSet tLCInsureAccSet = (LCInsureAccSet) (inVData.
                getObjectByObjectName(
                "LCInsureAccSet", 0));

        //得到生成的缴费帐户关联表
        LCPremToAccSet tLCPremToAccSet = (LCPremToAccSet) (inVData.
                getObjectByObjectName(
                "LCPremToAccSet", 0));

        LCInsureAccClassSet tInsureAccClassSet = (LCInsureAccClassSet) inVData.
                                                 getObjectByObjectName(
                "LCInsureAccClassSet", 0);

        //得到领取帐户关联表--目前不用
        LCGetToAccSet tLCGetToAccSet = (LCGetToAccSet) (inVData.
                getObjectByObjectName(
                "LCGetToAccSet", 0));

        if (tLCInsureAccSet == null)
        {
            tLCInsureAccSet = new LCInsureAccSet();
        }
        if (tLCPremToAccSet == null)
        {
            tLCPremToAccSet = new LCPremToAccSet();
        }
        if (tLCGetToAccSet == null)
        {
            tLCGetToAccSet = new LCGetToAccSet();
        }
        if (tInsureAccClassSet == null)
        {
            tInsureAccClassSet = new LCInsureAccClassSet();
        }

        LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet(); //保险帐户表记价履历表
        LCInsureAccTraceSchema tLCInsureAccTraceSchema = new
                LCInsureAccTraceSchema();
        LCInsureAccClassSchema tClassSchema = null;
        double inputMoney = 0;
        for (int n = 1; n <= pLCPremSet.size(); n++)
        {
            LCPremSchema tLCPremSchema = pLCPremSet.get(n);

            //判断是否帐户相关
            if (tLCPremSchema.getNeedAcc().equals("1"))
            {
                for (int m = 1; m <= tLCPremToAccSet.size(); m++)
                {
                    LCPremToAccSchema tLCPremToAccSchema = tLCPremToAccSet.get(
                            m);

                    //如果当前保费项和当前的缴费帐户关联表的保单号，责任编码，交费计划编码相同
                    if (tLCPremSchema.getPolNo().equals(tLCPremToAccSchema.
                            getPolNo())
                        &&
                        tLCPremSchema.getDutyCode().equals(tLCPremToAccSchema.
                            getDutyCode())
                        &&
                        tLCPremSchema.getPayPlanCode().equals(
                            tLCPremToAccSchema.getPayPlanCode()))
                    {

//               if ( tLCPremToAccSchema.getCalFlag()==null
//                   || "0".equals( tLCPremToAccSchema.getCalFlag() ))
//              {
                        //计算需要注入的资金
                        inputMoney = calInputMoney(tLCPremToAccSchema,
                                tLCPremSchema.getPrem());

                        if (inputMoney == -1)
                        {
                            // @@错误处理
                            CError tError = new CError();
                            tError.moduleName = "DealAccount";
                            tError.functionName = "addPrem";
                            tError.errorMessage = "计算实际应该注入的资金出错";
                            this.mErrors.addOneError(tError);

                            return null;
                        }
//              }else
//              {
//                  //计算管理非
//                  VData feeData = new VData();
//              CManageFee cManageFee = new CManageFee();
//              LCPremSet tLCPremSet = new LCPremSet();
//
//              feeData.add(tLCPremSet);
//              cManageFee.Initialize( feeData );
//              cManageFee.computeManaFee();
//              if ( cManageFee.mErrors.needDealError())
//              {
//                  this.mErrors.copyAllErrors( cManageFee.mErrors);
//                  return false;
//              }
//              VData tResult = cManageFee.getResult();
//              LCInsureAccFeeSet tmpLCInsureAccFeeSet =(LCInsureAccFeeSet) tResult.getObjectByObjectName("LCInsureAccFeeSet",0);
//              LCInsureAccClassFeeSet tmpLCInsureAccClassFeeSet=(LCInsureAccClassFeeSet)tResult.getObjectByObjectName("LCInsureAccClassFeeSet",0);
//              manFeeMap.put(tmpLCInsureAccFeeSet,this.INSERT );
//              manFeeMap.put(tmpLCInsureAccClassFeeSet, this.INSERT);
//
//
//              }

                        //累计账户分类ss
                        for (int t = 1; t <= tInsureAccClassSet.size(); t++)
                        {
                            tClassSchema = tInsureAccClassSet.get(t);
                            if (tClassSchema.getInsuAccNo().equals(
                                    tLCPremToAccSchema.getInsuAccNo())
                                &&
                                tClassSchema.getPayPlanCode().equals(
                                    tLCPremToAccSchema.
                                    getPayPlanCode())
                                &&
                                tClassSchema.getPolNo().equals(
                                    tLCPremToAccSchema.getPolNo()))
                            {

                                tClassSchema.setSumPay(tClassSchema.getSumPay() +
                                        inputMoney);
                                tClassSchema.setInsuAccBala(tClassSchema.
                                        getInsuAccBala()
                                        + inputMoney);
                                break;
                            }

                        }
                        for (int j = 1; j <= tLCInsureAccSet.size(); j++)
                        {
                            //如果当前缴费帐户关联表的保单号，账户号和当前的账户表的保单号，账户号相同并且资金不为0，将资金注入
                            LCInsureAccSchema tLCInsureAccSchema =
                                    tLCInsureAccSet.get(j);
                            if (tLCPremToAccSchema.getPolNo().equals(
                                    tLCInsureAccSchema.getPolNo())
                                &&
                                tLCPremToAccSchema.getInsuAccNo().equals(
                                    tLCInsureAccSchema.
                                    getInsuAccNo())
                                && (inputMoney != 0))
                            {
                                //修改保险帐户金额
                                tLCInsureAccSchema.setInsuAccBala(
                                        tLCInsureAccSchema.getInsuAccBala()
                                        + inputMoney);
                                tLCInsureAccSchema.setSumPay(tLCInsureAccSchema.
                                        getSumPay()
                                        + inputMoney);

                                //tLCInsureAccSchema.setInsuAccGetMoney(tLCInsureAccSchema.getInsuAccGetMoney()+inputMoney);
                                tLCInsureAccSet.set(j, tLCInsureAccSchema);

                                //查询险种保险帐户缴费
                                LMRiskAccPaySchema tLMRiskAccPaySchema =
                                        queryLMRiskAccPay3(
                                        RiskCode, tLCPremToAccSchema);
                                if (tLMRiskAccPaySchema == null)
                                {
                                    return null;
                                }
                                if (tLMRiskAccPaySchema.getPayNeedToAcc().
                                    equals("1"))
                                {
                                    //填充保险帐户表记价履历表
                                    tLimit = PubFun.getNoLimit(tLCPremSchema.
                                            getManageCom());
                                    serNo = PubFun1.CreateMaxNo("SERIALNO",
                                            tLimit);
                                    tLCInsureAccTraceSchema = new
                                            LCInsureAccTraceSchema();
                                    tLCInsureAccTraceSchema.setSerialNo(serNo);
//                                    tLCInsureAccTraceSchema.setInsuredNo(
//                                            tLCInsureAccSchema
//                                            .getInsuredNo());
                                    tLCInsureAccTraceSchema.setPolNo(
                                            tLCInsureAccSchema.getPolNo());
                                    tLCInsureAccTraceSchema.setMoneyType(
                                            MoneyType);
                                    tLCInsureAccTraceSchema.setRiskCode(
                                            tLCInsureAccSchema.
                                            getRiskCode());
                                    tLCInsureAccTraceSchema.setOtherNo(OtherNo);
                                    tLCInsureAccTraceSchema.setOtherType(
                                            OtherNoType);
                                    tLCInsureAccTraceSchema.setMoney(inputMoney);
                                    tLCInsureAccTraceSchema.setContNo(
                                            tLCInsureAccSchema.getContNo());
                                    tLCInsureAccTraceSchema.setGrpPolNo(
                                            tLCInsureAccSchema.
                                            getGrpPolNo());
                                    tLCInsureAccTraceSchema.setInsuAccNo(
                                            tLCInsureAccSchema.
                                            getInsuAccNo());
                                    tLCInsureAccTraceSchema.setPolNo(
                                            tLCInsureAccSchema.getPolNo());
                                    tLCInsureAccTraceSchema.setGrpContNo(
                                            tLCInsureAccSchema.
                                            getGrpContNo());
                                    tLCInsureAccTraceSchema.setState(
                                            tLCInsureAccSchema.getState());
                                    tLCInsureAccTraceSchema.setManageCom(
                                            tLCInsureAccSchema.
                                            getManageCom());
                                    tLCInsureAccTraceSchema.setOperator(
                                            tLCInsureAccSchema.
                                            getOperator());
                                    tLCInsureAccTraceSchema.setMakeDate(
                                            CurrentDate);
                                    tLCInsureAccTraceSchema.setMakeTime(
                                            CurrentTime);
                                    tLCInsureAccTraceSchema.setModifyDate(
                                            CurrentDate);
                                    tLCInsureAccTraceSchema.setModifyTime(
                                            CurrentTime);
                                    tLCInsureAccTraceSchema.setPayDate(
                                            CurrentDate);
                                    tLCInsureAccTraceSchema.setPolNo(
                                            tLCInsureAccSchema.getPolNo());
                                    tLCInsureAccTraceSchema.setGrpContNo(
                                            tLCInsureAccSchema.
                                            getGrpContNo());

                                    tLCInsureAccTraceSet.add(
                                            tLCInsureAccTraceSchema);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }

        inVData.clear();
        inVData.add(tLCInsureAccSet);
        inVData.add(tLCPremToAccSet);
        inVData.add(tLCGetToAccSet);

        //添加帐户注入资金轨迹
        inVData.add(tLCInsureAccTraceSet);

        //操作数据库时执行插入操作
        return inVData; //(LCInsureAccSet,LCPremToAccSet,LCGetToAccSet,LCInsureAccTraceSet)
    }


    /**
     * 保险账户资金注入(类型2 通用)
     * @param PolNo    保单号
     * @param InsuAccNo    帐户号
     * @param OtherNo  存放交费号或保单号
     * @param OtherNoType 存放交费号或保单号
     * @param MoneyType 金额类型:BF－保费 GL－管理费 HL－红利 LX－累积生息的利息
     * @param ManageCom 管理机构
     * @param money     注入资金
     * @return VData(tLCInsureAccSet:update ,tLCInsureAccTraceSet: insert)
     */
    public VData addPrem(String PolNo, String InsuAccNo, String OtherNo,
                         String OtherNoType
                         , String MoneyType, String ManageCom, double money)
    {
        if ((PolNo == null) || (InsuAccNo == null) || (OtherNo == null)
            || (OtherNoType == null) || (MoneyType == null)
            || (ManageCom == null))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "addPrem";
            tError.errorMessage = "传入数据不能为空";
            this.mErrors.addOneError(tError);

            return null;
        }

        VData tVData = new VData();
        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet(); //保险帐户表
        LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema();
        LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet(); //保险帐户表记价履历表
        LCInsureAccTraceSchema tLCInsureAccTraceSchema = new
                LCInsureAccTraceSchema();

        //根据保单号和保险账户号和其它号码查询LCInsureAcc表的唯一一条数据
        tLCInsureAccSchema = new LCInsureAccSchema();
        tLCInsureAccSchema = queryLCInsureAcc(PolNo, InsuAccNo, OtherNo);
        if (tLCInsureAccSchema == null)
        {
            return null;
        }

        //填充保险帐户表记价履历表
        tLimit = PubFun.getNoLimit(ManageCom);
        tLCInsureAccTraceSchema = new LCInsureAccTraceSchema();
        if (money != 0)
        { //如果注入资金=0，不添加轨迹
            serNo = PubFun1.CreateMaxNo("SERIALNO", tLimit);
            tLCInsureAccTraceSchema.setSerialNo(serNo);
//            tLCInsureAccTraceSchema.setInsuredNo(tLCInsureAccSchema
//                                                 .getInsuredNo());
            tLCInsureAccTraceSchema.setPolNo(tLCInsureAccSchema.getPolNo());
            tLCInsureAccTraceSchema.setMoneyType(MoneyType);
            tLCInsureAccTraceSchema.setRiskCode(tLCInsureAccSchema.getRiskCode());
            tLCInsureAccTraceSchema.setOtherNo(OtherNo);
            tLCInsureAccTraceSchema.setOtherType(OtherNoType);
            tLCInsureAccTraceSchema.setMoney(money);
            tLCInsureAccTraceSchema.setContNo(tLCInsureAccSchema.getContNo());
            tLCInsureAccTraceSchema.setGrpPolNo(tLCInsureAccSchema.getGrpPolNo());
            tLCInsureAccTraceSchema.setInsuAccNo(tLCInsureAccSchema.
                                                 getInsuAccNo());
            tLCInsureAccTraceSchema.setState(tLCInsureAccSchema.getState());
            tLCInsureAccTraceSchema.setManageCom(tLCInsureAccSchema.
                                                 getManageCom());
            tLCInsureAccTraceSchema.setOperator(tLCInsureAccSchema.getOperator());
            tLCInsureAccTraceSchema.setMakeDate(CurrentDate);
            tLCInsureAccTraceSchema.setMakeTime(CurrentTime);
            tLCInsureAccTraceSchema.setModifyDate(CurrentDate);
            tLCInsureAccTraceSchema.setModifyTime(CurrentTime);
            tLCInsureAccTraceSchema.setPayDate(CurrentDate);
        }

        //修改保险帐户金额
        tLCInsureAccSchema.setInsuAccBala(tLCInsureAccSchema.getInsuAccBala() +
                                          money);
        tLCInsureAccSchema.setSumPay(tLCInsureAccSchema.getSumPay() + money);

        //tLCInsureAccSchema.setInsuAccGetMoney(tLCInsureAccSchema.getInsuAccGetMoney()+money);
        //添加容器
        tLCInsureAccSet.add(tLCInsureAccSchema);
        tLCInsureAccTraceSet.add(tLCInsureAccTraceSchema);
        if (tLCInsureAccSet.size() == 0)
        {
            //      // @@错误处理
            //      CError tError =new CError();
            //      tError.moduleName="DealAccount";
            //      tError.functionName="addPrem";
            //      tError.errorMessage="条件不符合，没有生成纪录";
            //      this.mErrors .addOneError(tError) ;
            return null;
        }
        tVData.add(tLCInsureAccSet);
        tVData.add(tLCInsureAccTraceSet);

        return tVData;

        //操作数据库时，只需要更新tLCInsureAccSet，插入tLCInsureAccTraceSet
    }


    //--------下面是四个主要的函数中调用的相关附属函数---------------

    /**
     * 检验传入数据是否完整
     * @param parmData 传入数据
     * @return boolean
     */
    public boolean checkTransferData(TransferData parmData)
    {
        if (parmData == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "checkTransferData";
            tError.errorMessage = "传入数据不能为空";
            this.mErrors.addOneError(tError);

            return false;
        }
        try
        {
            String tPolNo = (String) parmData.getValueByName("PolNo");
            String tAccCreatePos = (String) parmData.getValueByName(
                    "AccCreatePos");
            String tOtherNo = (String) parmData.getValueByName("OtherNo");
            String tOtherNoType = (String) parmData.getValueByName(
                    "OtherNoType");

            //Double tRate=(Double)parmData.getValueByName("Rate"); //不校验费率，可以为空
            String FieldName = "";
            boolean errFlag = false;
            if (tPolNo == null)
            {
                FieldName = "PolNo";
                errFlag = true;
            }
            else if (tAccCreatePos == null)
            {
                FieldName = "AccCreatePos";
                errFlag = true;
            }
            else if (tOtherNo == null)
            {
                FieldName = "OtherNo";
                errFlag = true;
            }
            else if (tOtherNoType == null)
            {
                FieldName = "OtherNoType";
                errFlag = true;
            }
            if (errFlag)
            {
                // @@错误处理
                CError tError = new CError();
                tError.moduleName = "DealAccount";
                tError.functionName = "checkTransferData";
                tError.errorMessage = "没有接受到字段名为'" + FieldName + "'的数据";
                this.mErrors.addOneError(tError);

                return false;
            }
        }
        catch (Exception ex)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "checkTransferData";
            tError.errorMessage = "错误原因:传入的数据类型不匹配";
            this.mErrors.addOneError(tError);

            return false;
        }

        return true;
    }


    /**
     * 根据保单号查询保费项表
     * @param cPolNo 保单号
     * @return LCPremSet or null
     */
    public LCPremSet queryLCPrem(String cPolNo)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LCPrem where PolNo='");
        tSBql.append(cPolNo);
        tSBql.append("' and needacc='1'");

        LCPremSchema tLCPremSchema = new LCPremSchema();
        LCPremSet tLCPremSet = new LCPremSet();
        LCPremDB tLCPremDB = tLCPremSchema.getDB();
        tLCPremSet = tLCPremDB.executeQuery(tSBql.toString());

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
     * 通过传入的保费项纪录查询得到责任交费纪录集合
     * @param pLCPremSet 传入的保费项纪录
     * @return VData
     */
    public VData getFromLMDutyPay(LCPremSet pLCPremSet)
    {
        if (pLCPremSet == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "getFromLMDutyPay";
            tError.errorMessage = "传入参数不能为空!";
            this.mErrors.addOneError(tError);

            return null;
        }

        //1 循环判断交费计划编码的前6位全0；2 判断交费计划编码的前6位是否有重复值；
        LCPremSchema tLCPremSchema = new LCPremSchema();
        LCPremSet tLCPremSet = new LCPremSet();
        LMDutyPaySchema tLMDutyPaySchema = new LMDutyPaySchema();
        LMDutyPaySet tLMDutyPaySet = new LMDutyPaySet();
        String[] payPlanCode = new String[pLCPremSet.size()];
        int i = 0;
        String strCode = "";
        for (int n = 1; n <= pLCPremSet.size(); n++)
        {
            tLCPremSchema = pLCPremSet.get(n);
            //由于中意项目组把数据库中的char类型全部改成carchar2类型
            //涉及到了DB中对StrTool.space函数调用
            //所以把StrTool.space的功能改变了，以至于这里不能再调用这个函数
            //而改成调用String本身的函数－－huanglei
            //strCode = StrTool.space(tLCPremSchema.getPayPlanCode(), 6);
            strCode = tLCPremSchema.getPayPlanCode().substring(0,6);
            if (i == 0)
            {
                if (!strCode.equals("000000"))
                {
                    payPlanCode[i] = strCode;
                    i++;
                    tLMDutyPaySchema = new LMDutyPaySchema();
                    tLMDutyPaySchema = queryLMDutyPay(strCode);
                    if (tLMDutyPaySchema == null)
                    {
                        // @@错误处理
                        CError tError = new CError();
                        tError.moduleName = "DealAccount";
                        tError.functionName = "getFromLMDutyPay";
                        tError.errorMessage = "没有找到缴费编码=" + strCode + "的责任交费纪录";
                        this.mErrors.addOneError(tError);

                        return null;
                    }
                    tLMDutyPaySet.add(tLMDutyPaySchema);
                    tLCPremSet.add(tLCPremSchema);
                }
            }
            else
            {
                boolean saveFlag = true;
                if (!strCode.equals("000000"))
                {
                    for (int m = 0; m < i; m++)
                    {
                        if (strCode.equals(payPlanCode[m]))
                        {
                            saveFlag = false;

                            break;
                        }
                    }
                    if (saveFlag)
                    {
                        payPlanCode[i] = strCode;
                        i++;
                        tLMDutyPaySchema = new LMDutyPaySchema();
                        tLMDutyPaySchema = queryLMDutyPay(strCode);
                        if (tLMDutyPaySchema == null)
                        {
                            // @@错误处理
                            CError tError = new CError();
                            tError.moduleName = "DealAccount";
                            tError.functionName = "getFromLMDutyPay";
                            tError.errorMessage = "没有找到缴费编码=" + strCode
                                                  + "的责任交费纪录";
                            this.mErrors.addOneError(tError);

                            return null;
                        }
                        tLMDutyPaySet.add(tLMDutyPaySchema);
                        tLCPremSet.add(tLCPremSchema);
                    }
                }
            }
        }
        if ((tLMDutyPaySet.size() == 0) || (tLCPremSet.size() == 0))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "getFromLMDutyPay";
            tError.errorMessage = "没有找到责任交费纪录";
            this.mErrors.addOneError(tError);

            return null;
        }

        VData tVData = new VData();
        tVData.add(tLMDutyPaySet);
        tVData.add(tLCPremSet);

        return tVData;
    }


    /**
     * 根据责任交费编码查询责任交费表
     * @param payPlanCode 从保费项表查询出的交费编码（提取前6位）
     * @return LMDutyPaySchema or null
     */
    private LMDutyPaySchema queryLMDutyPay(String payPlanCode)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LMDutyPay where payPlanCode='");
        tSBql.append(payPlanCode);
        tSBql.append("'");

        LMDutyPaySchema tLMDutyPaySchema = new LMDutyPaySchema();
        LMDutyPaySet tLMDutyPaySet = new LMDutyPaySet();
        LMDutyPayDB tLMDutyPayDB = tLMDutyPaySchema.getDB();
        tLMDutyPaySet = tLMDutyPayDB.executeQuery(tSBql.toString());

        if (tLMDutyPayDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMDutyPayDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMDutyPay";
            tError.errorMessage = "责任交费表查询失败!";
            this.mErrors.addOneError(tError);
            tLMDutyPaySet.clear();

            return null;
        }
        if (tLMDutyPaySet.size() == 0)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMDutyPayDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMDutyPay";
            tError.errorMessage = "责任交费表没有查询到相关数据!";
            this.mErrors.addOneError(tError);
            tLMDutyPaySet.clear();

            return null;
        }

        return tLMDutyPaySet.get(1);
    }


    /**
     * 生成保费项表和客户账户表的关联表
     * @param tVData        包含责任交费和保费项集合
     * @param PolNo 保单号
     * @param AccCreatePos  生成帐户的流程位置标记（承保，交费等）
     * @param Rate          提取比率
     * @return LCPremToAccSet
     */
    public LCPremToAccSet createPremToAcc(VData tVData, String PolNo,
                                          String AccCreatePos, Double Rate)
    {
        if ((tVData == null) || (PolNo == null) || (AccCreatePos == null))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "createPremToAcc";
            tError.errorMessage = "传入数据不能为空!";
            this.mErrors.addOneError(tError);

            return null;
        }

        LCPremSet tLCPremSet = new LCPremSet();
        LMDutyPaySet tLMDutyPaySet = new LMDutyPaySet();
        tLCPremSet = (LCPremSet) tVData.getObjectByObjectName("LCPremSet", 0);
        tLMDutyPaySet = (LMDutyPaySet) tVData.getObjectByObjectName(
                "LMDutyPaySet", 0);

        LCPremSchema tLCPremSchema = new LCPremSchema(); //保费项表
        LMDutyPaySchema tLMDutyPaySchema = new LMDutyPaySchema(); //责任交费表
        LMRiskAccPaySchema tLMRiskAccPaySchema = new LMRiskAccPaySchema(); //
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet(); //保费项表和客户帐户表的关联表
        LCPremToAccSchema tLCPremToAccSchema = new LCPremToAccSchema(); //保费项表和客户帐户表的关联表

        double tRate = 0;
        for (int i = 1; i <= tLMDutyPaySet.size(); i++)
        {
            tLMDutyPaySchema = tLMDutyPaySet.get(i);
            tLCPremSchema = tLCPremSet.get(i);

            //判断是否和帐户关联
            if (tLMDutyPaySchema.getNeedAcc().equals("1"))
            {
                //查询险种保险帐户缴费表
                tLMRiskAccPaySchema = new LMRiskAccPaySchema();
                tLMRiskAccPaySchema = queryLMRiskAccPay(tLMDutyPaySchema,
                        tLCPremSchema, PolNo);
                if (tLMRiskAccPaySchema == null)
                {
                    return null;
                }

                //判断生成位置标记是否匹配
                if (AccCreatePos.equals(tLMRiskAccPaySchema.getAccCreatePos()))
                {
                    //判断费率是否需要录入
                    if (tLMRiskAccPaySchema.getNeedInput().equals("1"))
                    {
                        //如果需要录入:判断传入的费率是否为空
                        if (Rate == null)
                        {
                            // @@错误处理
                            CError tError = new CError();
                            tError.moduleName = "DealAccount";
                            tError.functionName = "createPremToAcc";
                            tError.errorMessage = "费率需要从界面录入，不能为空!";
                            this.mErrors.addOneError(tError);

                            return null;
                        }
                        tRate = Rate.doubleValue();
                    }
                    else
                    { //取默认值
                        tRate = tLMRiskAccPaySchema.getDefaultRate();
                    }

                    tLCPremToAccSchema = new LCPremToAccSchema();
                    tLCPremToAccSchema.setPolNo(PolNo);
                    tLCPremToAccSchema.setDutyCode(tLCPremSchema.getDutyCode());
                    tLCPremToAccSchema.setPayPlanCode(tLCPremSchema.
                            getPayPlanCode());
                    tLCPremToAccSchema.setInsuAccNo(tLMRiskAccPaySchema.
                            getInsuAccNo());
                    tLCPremToAccSchema.setRate(tRate);
                    tLCPremToAccSchema.setNewFlag(tLMRiskAccPaySchema.
                                                  getAccCreatePos());
                    tLCPremToAccSchema.setCalCodeMoney(tLMRiskAccPaySchema.
                            getCalCodeMoney());
                    tLCPremToAccSchema.setCalCodeUnit(tLMRiskAccPaySchema.
                            getCalCodeUnit());
                    tLCPremToAccSchema.setCalFlag(tLMRiskAccPaySchema.
                                                  getCalFlag());
                    tLCPremToAccSchema.setOperator(tLCPremSchema.getOperator());
                    tLCPremToAccSchema.setModifyDate(CurrentDate);
                    tLCPremToAccSchema.setModifyTime(CurrentTime);
                    tLCPremToAccSet.add(tLCPremToAccSchema);
                }
            }
        }

        if (tLCPremToAccSet.size() == 0)
        {
            // @@错误处理
            return null;
        }

        return tLCPremToAccSet;
    }


    /**
     * 查询险种保险帐户缴费表
     * @param cLMDutyPaySchema LMDutyPaySchema
     * @param cLCPremSchema LCPremSchema
     * @param cPolNo String
     * @return LMRiskAccPaySchema
     */
    public LMRiskAccPaySchema queryLMRiskAccPay(LMDutyPaySchema
                                                cLMDutyPaySchema
                                                , LCPremSchema cLCPremSchema,
                                                String cPolNo)
    {
        if ((cLMDutyPaySchema == null) || (cLCPremSchema == null)
            || (cPolNo == null))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay";
            tError.errorMessage = "传入数据不能为空!";
            this.mErrors.addOneError(tError);

            return null;
        }

        //查询保单表
        LCPolSchema tLCPolSchema = new LCPolSchema();
        tLCPolSchema = queryLCPol(cPolNo);
        if (tLCPolSchema == null)
        {
            //取默认值
            return null;
        }

        String riskCode = tLCPolSchema.getRiskCode();
        String payPlanCode = cLMDutyPaySchema.getPayPlanCode();

        //查询险种保险帐户缴费表
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from LMRiskAccPay where RiskCode='");
        tSBql.append(riskCode);
        tSBql.append("' and payPlanCode='");
        tSBql.append(payPlanCode);
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
            tError.functionName = "queryLMRiskAccPay";
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
            tError.functionName = "queryLMRiskAccPay";
            tError.errorMessage = "险种保险帐户缴费表没有查询到相关数据!";
            this.mErrors.addOneError(tError);
            tLMRiskAccPaySet.clear();

            return null;
        }

        return tLMRiskAccPaySet.get(1);
    }


    /**
     * 查询险种保险帐户缴费表2
     * @param cLCPremToAccSchema LCPremToAccSchema
     * @return LMRiskAccPaySchema
     */
    public LMRiskAccPaySchema queryLMRiskAccPay2(LCPremToAccSchema
                                                 cLCPremToAccSchema)
    {
        if (cLCPremToAccSchema == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay2";
            tError.errorMessage = "传入数据不能为空!";
            this.mErrors.addOneError(tError);

            return null;
        }

        //查询保单表
        LCPolSchema tLCPolSchema = new LCPolSchema();
        tLCPolSchema = queryLCPol(cLCPremToAccSchema.getPolNo());
        if (tLCPolSchema == null)
        {
            return null;
        }

        String riskCode = tLCPolSchema.getRiskCode();
        String payPlanCode = cLCPremToAccSchema.getPayPlanCode();
        String InsuAccNo = cLCPremToAccSchema.getInsuAccNo();

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


    /**
     * 查询保单表
     * @param PolNo String
     * @return LCPolSchema
     */
    public LCPolSchema queryLCPol(String PolNo)
    {
//        System.out.println("帐户内查询保单表");
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setPolNo(PolNo);
        if (tLCPolDB.getInfo())
        {
            return tLCPolDB.getSchema();
        }
        else
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCPolDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLCPol";
            tError.errorMessage = "保单表查询失败!";
            this.mErrors.addOneError(tError);

            return null;
        }
    }


    /**
     * 查询保险帐户表(传入3个主键，返回唯一纪录)
     * @param PolNo String
     * @param InsuAccNo String
     * @param OtherNo String
     * @return LCInsureAccSchema
     */
    public LCInsureAccSchema queryLCInsureAcc(String PolNo, String InsuAccNo,
                                              String OtherNo)
    {
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from LCInsureAcc where PolNo='");
        tSBql.append(PolNo);
        tSBql.append("' and InsuAccNo='");
        tSBql.append(InsuAccNo);
        tSBql.append("' and OtherNo='");
        tSBql.append(OtherNo);
        tSBql.append("' ");

        LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema();
        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet();
        LCInsureAccDB tLCInsureAccDB = tLCInsureAccSchema.getDB();
        tLCInsureAccSet = tLCInsureAccDB.executeQuery(tSBql.toString());

        if (tLCInsureAccDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCInsureAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLCInsureAcc";
            tError.errorMessage = "保险帐户表查询失败!";
            this.mErrors.addOneError(tError);
            tLCInsureAccSet.clear();

            return null;
        }
        if (tLCInsureAccSet.size() == 0)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCInsureAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLCInsureAcc";
            tError.errorMessage = "保险帐户表没有查询到相关数据!";
            this.mErrors.addOneError(tError);
            tLCInsureAccSet.clear();

            return null;
        }

        return tLCInsureAccSet.get(1);
    }


    /**
     * 查询保险帐户表(传入一个主键，返回纪录集合)
     * @param PolNo String
     * @return LCInsureAccSet
     */
    public LCInsureAccSet queryLCInsureAcc(String PolNo)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LCInsureAcc where PolNo='");
        tSBql.append(PolNo);
        tSBql.append("' ");

        LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema();
        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet();
        LCInsureAccDB tLCInsureAccDB = tLCInsureAccSchema.getDB();
        tLCInsureAccSet = tLCInsureAccDB.executeQuery(tSBql.toString());

        if (tLCInsureAccDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCInsureAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLCInsureAcc";
            tError.errorMessage = "保险帐户表查询失败!";
            this.mErrors.addOneError(tError);
            tLCInsureAccSet.clear();

            return null;
        }
        if (tLCInsureAccSet.size() == 0)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCInsureAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLCInsureAcc";
            tError.errorMessage = "保险帐户表没有查询到相关数据!";
            this.mErrors.addOneError(tError);
            tLCInsureAccSet.clear();

            return null;
        }

        return tLCInsureAccSet;
    }


    /**
     * 险种定义表查询
     * @param RiskCode String
     * @return LMRiskSchema
     */
    public LMRiskSchema queryLMRisk(String RiskCode)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LMRisk where RiskCode='");
        tSBql.append(RiskCode);
        tSBql.append("'");

        LMRiskSchema tLMRiskSchema = new LMRiskSchema();
        LMRiskSet tLMRiskSet = new LMRiskSet();
        LMRiskDB tLMRiskDB = tLMRiskSchema.getDB();
        tLMRiskSet = tLMRiskDB.executeQuery(tSBql.toString());

        if (tLMRiskDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMRiskDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRisk";
            tError.errorMessage = "险种定义表查询失败!";
            this.mErrors.addOneError(tError);
            tLMRiskSet.clear();

            return null;
        }
        if (tLMRiskSet.size() == 0)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMRiskDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRisk";
            tError.errorMessage = "险种定义表没有查询到相关数据!";
            this.mErrors.addOneError(tError);
            tLMRiskSet.clear();

            return null;
        }

        return tLMRiskSet.get(1);
    }


    /**
     * 查询险种账户关联表
     * @param RiskCode String
     * @return LMRiskToAccSet
     */
    public LMRiskToAccSet queryLMRiskToAcc(String RiskCode)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LMRiskToAcc where RiskCode='");
        tSBql.append(RiskCode);
        tSBql.append("'");

        LMRiskToAccSchema tLMRiskToAccSchema = new LMRiskToAccSchema();
        LMRiskToAccSet tLMRiskToAccSet = new LMRiskToAccSet();
        LMRiskToAccDB tLMRiskToAccDB = tLMRiskToAccSchema.getDB();
        tLMRiskToAccSet = tLMRiskToAccDB.executeQuery(tSBql.toString());

        if (tLMRiskToAccDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMRiskToAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskToAcc";
            tError.errorMessage = "险种账户关联表查询失败!";
            this.mErrors.addOneError(tError);
            tLMRiskToAccSet.clear();

            return null;
        }
        if (tLMRiskToAccSet.size() == 0)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMRiskToAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskToAcc";
            tError.errorMessage = "险种账户关联表没有查询到相关数据!";
            this.mErrors.addOneError(tError);
            tLMRiskToAccSet.clear();

            return null;
        }

        return tLMRiskToAccSet;
    }


    /**
     * 查询险种保险帐户(类型1)
     * @param InsuAccNo String
     * @return LMRiskInsuAccSchema
     */
    public LMRiskInsuAccSchema queryLMRiskInsuAcc(String InsuAccNo)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LMRiskInsuAcc where InsuAccNo='");
        tSBql.append(InsuAccNo);
        tSBql.append("'");

        LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
        LMRiskInsuAccSet tLMRiskInsuAccSet = new LMRiskInsuAccSet();
        LMRiskInsuAccDB tLMRiskInsuAccDB = tLMRiskInsuAccSchema.getDB();
        tLMRiskInsuAccSet = tLMRiskInsuAccDB.executeQuery(tSBql.toString());

        if (tLMRiskInsuAccDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMRiskInsuAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskInsuAcc";
            tError.errorMessage = "险种保险帐户表查询失败!";
            this.mErrors.addOneError(tError);
            tLMRiskInsuAccSet.clear();

            return null;
        }
        if (tLMRiskInsuAccSet.size() == 0)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMRiskInsuAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskInsuAcc";
            tError.errorMessage = "险种保险帐户表没有查询到相关数据!";
            this.mErrors.addOneError(tError);
            tLMRiskInsuAccSet.clear();

            return null;
        }

        return tLMRiskInsuAccSet.get(1);
    }


    /**
     * 查询险种保险帐户(类型2)
     * @param InsuAccNo String 帐号
     * @param AccType String 帐户类型
     * @return LMRiskInsuAccSchema
     */
    public LMRiskInsuAccSchema queryLMRiskInsuAcc(String InsuAccNo,
                                                  String AccType)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LMRiskInsuAcc where InsuAccNo='");
        tSBql.append(InsuAccNo);
        tSBql.append("' and AccType='");
        tSBql.append(AccType);
        tSBql.append("'");

        LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
        LMRiskInsuAccSet tLMRiskInsuAccSet = new LMRiskInsuAccSet();
        LMRiskInsuAccDB tLMRiskInsuAccDB = tLMRiskInsuAccSchema.getDB();
        tLMRiskInsuAccSet = tLMRiskInsuAccDB.executeQuery(tSBql.toString());

        if (tLMRiskInsuAccDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMRiskInsuAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskInsuAcc";
            tError.errorMessage = "险种保险帐户表查询失败!";
            this.mErrors.addOneError(tError);
            tLMRiskInsuAccSet.clear();

            return null;
        }
        if (tLMRiskInsuAccSet.size() == 0)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMRiskInsuAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskInsuAcc";
            tError.errorMessage = "险种保险帐户表没有查询到相关数据!";
            this.mErrors.addOneError(tError);
            tLMRiskInsuAccSet.clear();

            return null;
        }

        return tLMRiskInsuAccSet.get(1);
    }


    /**
     * 生成给付项表和客户帐户表的关联表
     * @param PolNo String
     * @param AccCreatePos String
     * @param Rate Double
     * @return LCGetToAccSet
     */
    public LCGetToAccSet getGetToAcc(String PolNo, String AccCreatePos,
                                     Double Rate)
    {
        //1-取出领取项表
        LCGetSet tLCGetSet = new LCGetSet();
        tLCGetSet = queryLCGet(PolNo);
        if (tLCGetSet == null)
        {
            return null;
        }

        //2-根据领取项表取出对应的责任给付描述表
        //LMDutyGetSet tLMDutyGetSet = new LMDutyGetSet();
        VData tVData = new VData();
        tVData = createLMDutyGet(tLCGetSet);
        if (tVData == null)
        {
            return null;
        }

        //3-生成给付项表和客户账户表的关联表
        LCGetToAccSet tLCGetToAccSet = new LCGetToAccSet();
        tLCGetToAccSet = createGetToAcc(tVData, PolNo, AccCreatePos, Rate);

        return tLCGetToAccSet;
    }


    /**
     * 取出领取项表
     * @param PolNo String
     * @return LCGetSet
     */
    public LCGetSet queryLCGet(String PolNo)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LCGet where PolNo='");
        tSBql.append(PolNo);
        tSBql.append("' and needacc='1'");

        LCGetSchema tLCGetSchema = new LCGetSchema();
        LCGetSet tLCGetSet = new LCGetSet();
        LCGetDB tLCGetDB = tLCGetSchema.getDB();
        tLCGetSet = tLCGetDB.executeQuery(tSBql.toString());

        if (tLCGetDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCGetDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLCGet";
            tError.errorMessage = "领取项表查询失败!";
            this.mErrors.addOneError(tError);
            tLCGetSet.clear();

            return null;
        }
        if (tLCGetSet.size() == 0)
        {
            return null;
        }

        return tLCGetSet;
    }


    /**
     * 根据领取项表取出对应的责任给付描述表
     * @param pLCGetSet LCGetSet
     * @return VData
     */
    public VData createLMDutyGet(LCGetSet pLCGetSet)
    {
        if (pLCGetSet == null)
        {
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMDutyGet";
            tError.errorMessage = "不能传入空数据!";
            this.mErrors.addOneError(tError);
        }

        LMDutyGetSchema tLMDutyGetSchema = new LMDutyGetSchema(); //责任给付
        LMDutyGetSet tLMDutyGetSet = new LMDutyGetSet();
        LCGetSchema tLCGetSchema = new LCGetSchema(); //领取项表
        LCGetSet tLCGetSet = new LCGetSet();
        for (int i = 1; i <= pLCGetSet.size(); i++)
        {
            tLCGetSchema = new LCGetSchema();
            tLCGetSchema = pLCGetSet.get(i);
            tLMDutyGetSchema = new LMDutyGetSchema();

            //查询责任给付表
            tLMDutyGetSchema = new LMDutyGetSchema();
            tLMDutyGetSchema = queryLMDutyGet(tLCGetSchema.getGetDutyCode());
            if (tLMDutyGetSchema == null)
            {
                continue;
            }
            tLMDutyGetSet.add(tLMDutyGetSchema);
            tLCGetSet.add(tLCGetSchema);
        }
        if (tLMDutyGetSet.size() == 0)
        {
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMDutyGet";
            tError.errorMessage = "没有查到责任给付纪录!";
            this.mErrors.addOneError(tError);

            return null;
        }

        VData tVData = new VData();
        tVData.add(tLMDutyGetSet);
        tVData.add(tLCGetSet);

        return tVData;
    }


    /**
     * 查询责任给付表
     * @param GetDutyCode String
     * @return LMDutyGetSchema
     */
    public LMDutyGetSchema queryLMDutyGet(String GetDutyCode)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LMDutyGet where GetDutyCode='");
        tSBql.append(GetDutyCode);
        tSBql.append("'");

        LMDutyGetSchema tLMDutyGetSchema = new LMDutyGetSchema();
        LMDutyGetSet tLMDutyGetSet = new LMDutyGetSet();
        LMDutyGetDB tLMDutyGetDB = tLMDutyGetSchema.getDB();
        tLMDutyGetSet = tLMDutyGetDB.executeQuery(tSBql.toString());

        if (tLMDutyGetDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMDutyGetDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMDutyGet";
            tError.errorMessage = "责任给付表查询失败!";
            this.mErrors.addOneError(tError);
            tLMDutyGetSet.clear();

            return null;
        }
        if (tLMDutyGetSet.size() == 0)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMDutyGet";
            tError.errorMessage = "责任给付表没有查询到相关数据!";
            this.mErrors.addOneError(tError);

            return null;
        }

        return tLMDutyGetSet.get(1);
    }


    /**
     * 生成给付项表和客户账户表的关联表
     * @param pVData VData
     * @param PolNo String
     * @param AccCreatePos String
     * @param Rate Double
     * @return LCGetToAccSet
     */
    public LCGetToAccSet createGetToAcc(VData pVData, String PolNo,
                                        String AccCreatePos
                                        , Double Rate)
    {
        if ((pVData == null) || (AccCreatePos == null) || (PolNo == null))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "createGetToAcc";
            tError.errorMessage = "传入数据不能为空!";
            this.mErrors.addOneError(tError);

            return null;
        }

        LMDutyGetSet tLMDutyGetSet = (LMDutyGetSet) pVData.
                                     getObjectByObjectName("LMDutyGetSet", 0);
        LCGetSet tLCGetSet = (LCGetSet) pVData.getObjectByObjectName("LCGetSet",
                0);
        LCPolSchema tLCPolSchema = new LCPolSchema();
        tLCPolSchema = queryLCPol(PolNo);
        if (tLCPolSchema == null)
        {
            return null;
        }

        LMDutyGetSchema tLMDutyGetSchema = new LMDutyGetSchema(); //责任给付
        LMRiskAccGetSet tLMRiskAccGetSet = new LMRiskAccGetSet(); //责任给付
        LMRiskAccGetSchema tLMRiskAccGetSchema = new LMRiskAccGetSchema(); //险种保险帐户给付
        LCGetToAccSchema tLCGetToAccSchema = new LCGetToAccSchema(); //给付项表和客户账户表的关联表
        LCGetToAccSet tLCGetToAccSet = new LCGetToAccSet(); //给付项表和客户账户表的关联表

        for (int i = 1; i <= tLMDutyGetSet.size(); i++)
        {
            tLMDutyGetSchema = new LMDutyGetSchema();
            tLMDutyGetSchema = tLMDutyGetSet.get(i);

            //判断是否和帐户相关
            if (tLMDutyGetSchema.getNeedAcc().equals("1"))
            {
                //查询险种保险帐户给付表
                tLMRiskAccGetSet = new LMRiskAccGetSet();
                tLMRiskAccGetSet = queryLMRiskAccGet(tLCPolSchema.getRiskCode()
                        , tLMDutyGetSchema.getGetDutyCode());
                if (tLMRiskAccGetSet == null)
                {
                    continue;
                }
                for (int n = 1; n <= tLMRiskAccGetSet.size(); n++)
                {
                    tLMRiskAccGetSchema = new LMRiskAccGetSchema();
                    tLMRiskAccGetSchema = tLMRiskAccGetSet.get(n);
                    if (tLMRiskAccGetSchema.getDealDirection().equals("0")
                        &&
                        tLMRiskAccGetSchema.getAccCreatePos().equals(
                            AccCreatePos))
                    {
                        tLCGetToAccSchema = new LCGetToAccSchema();

                        //判断是否需要录入
                        if (tLMRiskAccGetSchema.getNeedInput().equals("1"))
                        {
                            if (Rate == null)
                            {
                                // @@错误处理
                                CError tError = new CError();
                                tError.moduleName = "DealAccount";
                                tError.functionName = "createGetToAcc";
                                tError.errorMessage = "费率需要从界面录入，不能为空!";
                                this.mErrors.addOneError(tError);

                                return null;
                            }
                            tLCGetToAccSchema.setDefaultRate(Rate.doubleValue());
                        }
                        else
                        {
                            tLCGetToAccSchema.setDefaultRate(
                                    tLMRiskAccGetSchema.getDefaultRate());
                        }

                        tLCGetToAccSchema.setNeedInput(tLMRiskAccGetSchema.
                                getNeedInput());
                        tLCGetToAccSchema.setPolNo(PolNo);
                        tLCGetToAccSchema.setDutyCode(tLCGetSet.get(i).
                                getDutyCode());
                        tLCGetToAccSchema.setGetDutyCode(tLMRiskAccGetSchema.
                                getGetDutyCode());
                        tLCGetToAccSchema.setInsuAccNo(tLMRiskAccGetSchema.
                                getInsuAccNo());
                        tLCGetToAccSchema.setCalCodeMoney(tLMRiskAccGetSchema.
                                getCalCodeMoney());
                        tLCGetToAccSchema.setDealDirection(tLMRiskAccGetSchema.
                                getDealDirection());
                        tLCGetToAccSchema.setCalFlag(tLMRiskAccGetSchema.
                                getCalFlag());
                        tLCGetToAccSchema.setModifyDate(CurrentDate);
                        tLCGetToAccSchema.setModifyTime(CurrentTime);
                        tLCGetToAccSet.add(tLCGetToAccSchema);
                    }
                }
            }
        }

        if (tLCGetToAccSet.size() == 0)
        {
            // @@错误处理
            //      CError tError = new CError();
            //      tError.moduleName = "DealAccount";
            //      tError.functionName = "createGetToAcc";
            //      tError.errorMessage = "没有符合条件的给付项表和客户账户表的关联表纪录!";
            //      this.mErrors.addOneError(tError);
            return null;
        }

        return tLCGetToAccSet;
    }


    /**
     * 查询险种保险帐户给付表
     * @param RiskCode String
     * @param GetDutyCode String
     * @return LMRiskAccGetSet
     */
    public LMRiskAccGetSet queryLMRiskAccGet(String RiskCode,
                                             String GetDutyCode)
    {
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from LMRiskAccGet where GetDutyCode='");
        tSBql.append(GetDutyCode);
        tSBql.append("' and RiskCode='");
        tSBql.append(RiskCode);
        tSBql.append("'");

        LMRiskAccGetSchema tLMRiskAccGetSchema = new LMRiskAccGetSchema();
        LMRiskAccGetSet tLMRiskAccGetSet = new LMRiskAccGetSet();
        LMRiskAccGetDB tLMRiskAccGetDB = tLMRiskAccGetSchema.getDB();
        tLMRiskAccGetSet = tLMRiskAccGetDB.executeQuery(tSBql.toString());

        if (tLMRiskAccGetDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMRiskAccGetDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccGet";
            tError.errorMessage = "险种保险帐户给付表查询失败!";
            this.mErrors.addOneError(tError);
            tLMRiskAccGetSet.clear();

            return null;
        }
        if (tLMRiskAccGetSet.size() == 0)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccGet";
            tError.errorMessage = "险种保险帐户给付表没有查询到相关数据!";
            this.mErrors.addOneError(tError);

            return null;
        }

        return tLMRiskAccGetSet;
    }


    /**
     * 查询保费项表和客户帐户表的关联表
     * @param pLCPremSchema LCPremSchema
     * @return LCPremToAccSet
     */
    public LCPremToAccSet queryLCPremToAccSet(LCPremSchema pLCPremSchema)
    {
        if (pLCPremSchema == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLCPremToAccSet";
            tError.errorMessage = "传入数据不能为空!";
            this.mErrors.addOneError(tError);

            return null;
        }

        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from LCPremToAcc where PolNo='");
        tSBql.append(pLCPremSchema.getPolNo());
        tSBql.append("' and  DutyCode='");
        tSBql.append(pLCPremSchema.getDutyCode());
        tSBql.append("' and PayPlanCode='");
        tSBql.append(pLCPremSchema.getPayPlanCode());
        tSBql.append("' ");

        LCPremToAccSchema tLCPremToAccSchema = new LCPremToAccSchema();
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet();
        LCPremToAccDB tLCPremToAccDB = tLCPremToAccSchema.getDB();
        tLCPremToAccSet = tLCPremToAccDB.executeQuery(tSBql.toString());

        if (tLCPremToAccDB.mErrors.needDealError())
        {
            return null;
        }
        if (tLCPremToAccSet.size() == 0)
        {
            return null;
        }

        return tLCPremToAccSet;
    }


    /**
     * 从传入的保险帐户集合中查询符合条件的纪录
     * @param PolNo String
     * @param InsuAccNo String
     * @param OtherNo String
     * @param pLCInsureAccSet LCInsureAccSet
     * @return LCInsureAccSchema
     */
    public LCInsureAccSchema queryLCInsureAccSet(String PolNo, String InsuAccNo,
                                                 String OtherNo
                                                 ,
                                                 LCInsureAccSet pLCInsureAccSet)
    {
        if ((PolNo == null) || (InsuAccNo == null) || (OtherNo == null)
            || (pLCInsureAccSet == null))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLCInsureAccSet";
            tError.errorMessage = "传入数据不能为空!";
            this.mErrors.addOneError(tError);

            return null;
        }

        LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema();
        for (int i = 1; i <= pLCInsureAccSet.size(); i++)
        {
            tLCInsureAccSchema = new LCInsureAccSchema();
            tLCInsureAccSchema = pLCInsureAccSet.get(i);
            if (tLCInsureAccSchema.getPolNo().equals(PolNo)
                && tLCInsureAccSchema.getInsuAccNo().equals(InsuAccNo)
                // && tLCInsureAccSchema.getOtherNo().equals(OtherNo)
                )
            {
                return tLCInsureAccSchema;
            }
        }

        // @@错误处理
        CError tError = new CError();
        tError.moduleName = "DealAccount";
        tError.functionName = "queryLCInsureAccSet";
        tError.errorMessage = "没有从要生成的保险账户中找到匹配的数据!";
        this.mErrors.addOneError(tError);

        return null;
    }


    /**
     * 计算实际应该注入的资金(类似佣金计算,不过数据库内的计算编码尚未描述)
     * @param tLCPremToAccSchema 传入保费项表和客户帐户表的关联表纪录
     * @param Prem 缴纳保费
     * @return 实际应该注入的资金
     */
    public double calInputMoney(LCPremToAccSchema tLCPremToAccSchema,
                                double Prem)
    {
        // @@错误处理
        if (tLCPremToAccSchema == null)
        {
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "calInputMoneyRate";
            tError.errorMessage = "传入数据不能为空!";
            this.mErrors.addOneError(tError);

            return -1;
        }

//        String[] F = new String[5];
//        int m = 0;
        double defaultRate = 0;
        double inputMoney = 0;
        String calMoney = "";
        defaultRate = tLCPremToAccSchema.getRate(); //缺省比例

        Calculator tCalculator = new Calculator(); //计算类

        if (tLCPremToAccSchema.getCalFlag() == null)
        { //如果该标记为空
            inputMoney = Prem * 1 * defaultRate;

            return inputMoney;
        }

        //账户转入计算标志:0 －－ 完全转入账户
        // 1 －－ 按现金计算转入账户
        // 2 －－ 按股份计算转入账户
        // 3 －－ 先算现金，然后按股份计算。(未做)
        if (tLCPremToAccSchema.getCalFlag().equals("0"))
        {
            inputMoney = Prem * 1 * defaultRate;

            return inputMoney;
        }
        if (tLCPremToAccSchema.getCalFlag().equals("1"))
        {
            if (tLCPremToAccSchema.getCalCodeMoney() == null)
            {
                // @@错误处理
                CError tError = new CError();
                tError.moduleName = "DealAccount";
                tError.functionName = "calInputMoneyRate";
                tError.errorMessage = "未找到转入账户时的算法编码(现金)!";
                this.mErrors.addOneError(tError);

                return -1;
            }
            tCalculator.setCalCode(tLCPremToAccSchema.getCalCodeMoney()); //添加计算编码

            //添加计算必要条件：保费
            LCPolDB tLCPolDB = new LCPolDB();

            //注意：此时保单可能是还没有签单，所以要根据具体情况传入号码（投保单号或保单号）
            tLCPolDB.setPolNo(tLCPremToAccSchema.getPolNo());
            if (!tLCPolDB.getInfo())
            {
                // @@错误处理
                CError tError = new CError();
                tError.moduleName = "DealAccount";
                tError.functionName = "calInputMoneyRate";
                tError.errorMessage = "未找到账户对应的保单!";
                this.mErrors.addOneError(tError);

                return -1;
            }
            tCalculator.addBasicFactor("ManageFeeRate",
                                       String.valueOf(tLCPolDB.getManageFeeRate())); //管理费比例-参见众悦年金分红-计算编码601304
            tCalculator.addBasicFactor("Prem", String.valueOf(Prem));

            //计算要素可后续添加
            calMoney = tCalculator.calculate();
            if (calMoney == null)
            {
                CError tError = new CError();
                tError.moduleName = "DealAccount";
                tError.functionName = "calInputMoneyRate";
                tError.errorMessage = "计算注入帐户资金失败!";
                this.mErrors.addOneError(tError);

                return -1;
            }
            inputMoney = Double.parseDouble(calMoney);

            return inputMoney;
        }
        if (tLCPremToAccSchema.getCalFlag().equals("2"))
        {
            if (tLCPremToAccSchema.getCalCodeMoney() == null)
            {
                // @@错误处理
                CError tError = new CError();
                tError.moduleName = "DealAccount";
                tError.functionName = "calInputMoneyRate";
                tError.errorMessage = "未找到转入账户时的算法编码(股份)";
                this.mErrors.addOneError(tError);

                return -1;
            }
            tCalculator.setCalCode(tLCPremToAccSchema.getCalCodeUnit()); //添加计算编码

            //添加计算必要条件：保费
            tCalculator.addBasicFactor("Prem", String.valueOf(Prem));
            calMoney = tCalculator.calculate();
            if (calMoney == null)
            {
                CError tError = new CError();
                tError.moduleName = "DealAccount";
                tError.functionName = "calInputMoneyRate";
                tError.errorMessage = "计算注入帐户资金失败!";
                this.mErrors.addOneError(tError);

                return -1;
            }
            inputMoney = Double.parseDouble(calMoney);

            return inputMoney;
        }

        return 0;
    }


    /**
     * 修改保险帐户表记价履历表纪录的交费日期
     * @param PayDate String
     * @param pVData VData
     * @return VData
     */
    public VData updateLCInsureAccTraceDate(String PayDate, VData pVData)
    {
        // @@错误处理
        if ((PayDate == null) || (pVData == null))
        {
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "updateLCInsureAccTraceDate";
            tError.errorMessage = "传入数据不能为空!";
            this.mErrors.addOneError(tError);

            return null;
        }

        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet();
        LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet();

        tLCInsureAccSet = (LCInsureAccSet) pVData.getObjectByObjectName(
                "LCInsureAccSet", 0);
        tLCInsureAccTraceSet = (LCInsureAccTraceSet) pVData.
                               getObjectByObjectName(
                "LCInsureAccTraceSet", 0);

        if (tLCInsureAccTraceSet == null)
        {
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "updateLCInsureAccTraceDate";
            tError.errorMessage = "VData中没有找到需要的数据!";
            this.mErrors.addOneError(tError);

            return null;
        }

        VData tVData = new VData();
        LCInsureAccTraceSet newLCInsureAccTraceSet = new LCInsureAccTraceSet();
        LCInsureAccTraceSchema tLCInsureAccTraceSchema = new
                LCInsureAccTraceSchema();
        for (int n = 1; n <= tLCInsureAccTraceSet.size(); n++)
        {
            tLCInsureAccTraceSchema = new LCInsureAccTraceSchema();
            tLCInsureAccTraceSchema = tLCInsureAccTraceSet.get(n);
            tLCInsureAccTraceSchema.setPayDate(PayDate);
            newLCInsureAccTraceSet.add(tLCInsureAccTraceSchema);
        }
        tVData.add(newLCInsureAccTraceSet);
        tVData.add(tLCInsureAccSet);

        return tVData;
    }


    /**
     * 为已经存在的集体下个人账户添加交费轨迹(譬如，承保签单时应该注入资金的没有注入)
     * @param GrpPolNo String
     * @param InsuAccNo String
     * @param Money double
     * @return boolean
     */
    public boolean addPremTraceForAcc(String GrpPolNo, String InsuAccNo,
                                      double Money)
    {
        LCGrpPolDB tLCGrpPolDB = new LCGrpPolDB();
        tLCGrpPolDB.setGrpPolNo(GrpPolNo);
        if (!tLCGrpPolDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "addPremTraceForAcc";
            tError.errorMessage = "没有找到集体保单!";
            this.mErrors.addOneError(tError);

            return false;
        }

        String ManageCom = tLCGrpPolDB.getManageCom();
        VData tVData = new VData();
        LCInsureAccTraceSet saveLCInsureAccTraceSet = new LCInsureAccTraceSet();
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setGrpPolNo(GrpPolNo);

        LCPolSet tLCPolSet = new LCPolSet();
        tLCPolSet = tLCPolDB.query();

        LCPolSchema tLCPolSchema = new LCPolSchema();
        LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet();
        for (int i = 1; i <= tLCPolSet.size(); i++)
        {
            tLCPolSchema = tLCPolSet.get(i);
            tVData = addPrem(tLCPolSchema.getPolNo(), InsuAccNo,
                             tLCPolSchema.getPolNo(), "1", "BF"
                             , ManageCom, Money);
            tLCInsureAccTraceSet = (LCInsureAccTraceSet) tVData.
                                   getObjectByObjectName(
                    "LCInsureAccTraceSet", 0);
            if (tLCInsureAccTraceSet != null)
            {
                saveLCInsureAccTraceSet.add(tLCInsureAccTraceSet);
            }
        }

        Connection conn = DBConnPool.getConnection();

        try
        {
            conn.setAutoCommit(false);

            LCInsureAccTraceDBSet tLCInsureAccTraceDBSet = new
                    LCInsureAccTraceDBSet(conn);
            tLCInsureAccTraceDBSet.add(saveLCInsureAccTraceSet);

            //数据提交
            if (!tLCInsureAccTraceDBSet.insert())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tLCInsureAccTraceDBSet.mErrors);

                CError tError = new CError();
                tError.moduleName = "tLPAppntIndDB";
                tError.functionName = "insertData";
                tError.errorMessage = "数据提交失败!";
                this.mErrors.addOneError(tError);

                conn.rollback();
                conn.close();

                return false;
            }

            conn.commit();
        }
        catch (Exception ex)
        {
            try
            {
                conn.rollback();
                conn.close();
            }
            catch (Exception e)
            {
            }
        }

        return true;
    }


    //---------------为批量处理准备，例如：集体批量签单处理帐户------------------

    /**
     * 对个人保单生成保险帐户表(类型 1：空帐户,不需要添加履历表纪录)
     * @param PolNo String 保单号
     * @param AccCreatePos String 生成位置 :1-投保单录入时产生 2－缴费时产生 3－领取时产生
     * @param OtherNo String 保单号或交费号
     * @param OtherNoType String 保单号或交费号
     * @param inLCPolSchema LCPolSchema
     * @param inLMRiskSchema LMRiskSchema
     * @return LCInsureAccSet
     */
    public LCInsureAccSet getLCInsureAccForBat(String PolNo,
                                               String AccCreatePos,
                                               String OtherNo
                                               , String OtherNoType,
                                               LCPolSchema inLCPolSchema,
                                               LMRiskSchema inLMRiskSchema)
    {
        if ((PolNo == null) || (AccCreatePos == null) || (OtherNo == null)
            || (OtherNoType == null) || (inLCPolSchema == null)
            || (inLMRiskSchema == null))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "getLCInsureAccForBat";
            tError.errorMessage = "传入数据不能为空!";
            this.mErrors.addOneError(tError);

            return null;
        }

        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet(); //保险帐户表

        if (inLMRiskSchema.getInsuAccFlag().equals("Y")
            || inLMRiskSchema.getInsuAccFlag().equals("y"))
        {
            //根据险种查询LMRiskToAcc表(险种账户关联表)
            LMRiskToAccSet tLMRiskToAccSet = new LMRiskToAccSet();
            tLMRiskToAccSet = queryLMRiskToAcc(inLCPolSchema.getRiskCode());
            if (tLMRiskToAccSet == null)
            {
                return null;
            }

            LMRiskToAccSchema tLMRiskToAccSchema = new LMRiskToAccSchema(); //险种账户关联表
            LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema(); //险种保险帐户
            LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema(); //保险帐户表

            for (int i = 1; i <= tLMRiskToAccSet.size(); i++)
            {
                //根据保险账户号码查询LMRiskInsuAcc表(险种保险帐户)
                tLMRiskToAccSchema = tLMRiskToAccSet.get(i);
                tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
                tLMRiskInsuAccSchema = queryLMRiskInsuAcc(tLMRiskToAccSchema.
                        getInsuAccNo());
                if (tLMRiskInsuAccSchema == null)
                {
                    return null;
                }

                //判断帐户类型，决定是否生成相关记录 Alex 20050920
                if (tLMRiskInsuAccSchema.getAccType().equals("001") ||
                    tLMRiskInsuAccSchema.getAccType().equals("005"))
                {
                    //001公共帐户，005公共红利帐户
                    //如果保单类型是-2 --（团单）公共帐户
                    //此时才生成对应的公共帐户记录
                    if ((inLCPolSchema.getPolTypeFlag() != null)
                        && inLCPolSchema.getPolTypeFlag().equals("2"))
                    {
                        System.out.println("开始生成集体帐户");
                    }
                    else
                    {
                        continue;
                    }
                }
                else
                {
                    //添加判断，使生成公共帐户同时不生成个人帐户类型的记录
                    //如果保单类型是-2 --（团单）公共帐户
                    //不生成个人类型的帐户, 但是投资账户还是要生成的
                    if ((inLCPolSchema.getPolTypeFlag() != null)
                        && inLCPolSchema.getPolTypeFlag().equals("2") && (!"2".equals(tLMRiskInsuAccSchema.getAccKind())))
                    {
                        continue;
                    }
                    else
                    {
                        System.out.println("开始生成个人帐户");
                    }

                }

                //生成保险账户表
                //如果账户生成位置找到匹配的保险账户
                if (tLMRiskInsuAccSchema.getAccCreatePos().equals(AccCreatePos))
                {
                    tLCInsureAccSchema = new LCInsureAccSchema();
                    tLCInsureAccSchema.setPolNo(inLCPolSchema.getPolNo());
                    tLCInsureAccSchema.setInsuAccNo(tLMRiskInsuAccSchema.
                            getInsuAccNo());
                    tLCInsureAccSchema.setRiskCode(tLMRiskToAccSchema.
                            getRiskCode());
                    tLCInsureAccSchema.setAccType(tLMRiskInsuAccSchema.
                                                  getAccType());
                    tLCInsureAccSchema.setGrpContNo(inLCPolSchema.getGrpContNo());
                    tLCInsureAccSchema.setPrtNo(inLCPolSchema.getPrtNo());
                    tLCInsureAccSchema.setContNo(inLCPolSchema.getContNo());
                    tLCInsureAccSchema.setGrpPolNo(inLCPolSchema.getGrpPolNo());
                    tLCInsureAccSchema.setInsuredNo(inLCPolSchema.getInsuredNo());
                    tLCInsureAccSchema.setAppntNo(inLCPolSchema.getAppntNo());
                    tLCInsureAccSchema.setSumPay(0);
                    tLCInsureAccSchema.setInsuAccBala(0);
                    tLCInsureAccSchema.setUnitCount(0);
                    tLCInsureAccSchema.setInsuAccGetMoney(0);
                    tLCInsureAccSchema.setFrozenMoney(0);
                    tLCInsureAccSchema.setLastAccBala(0);
                    tLCInsureAccSchema.setLastUnitCount(0);
                    tLCInsureAccSchema.setLastUnitPrice(0);
                    tLCInsureAccSchema.setUnitPrice(0);
                    tLCInsureAccSchema.setAccComputeFlag(tLMRiskInsuAccSchema.
                            getAccComputeFlag());
                    tLCInsureAccSchema.setAccType(tLMRiskInsuAccSchema.
                                                  getAccType());
                    tLCInsureAccSchema.setManageCom(inLCPolSchema.getManageCom());
                    tLCInsureAccSchema.setOperator(inLCPolSchema.getOperator());
                    tLCInsureAccSchema.setBalaDate(mEnterAccDate); //默认的结息日，原为签单日，现改为财务到帐日期 Alex 20051124
                    tLCInsureAccSchema.setMakeDate(CurrentDate);
                    tLCInsureAccSchema.setMakeTime(CurrentTime);
                    tLCInsureAccSchema.setModifyDate(CurrentDate);
                    tLCInsureAccSchema.setModifyTime(CurrentTime);
                    tLCInsureAccSchema.setState("0");
                    tLCInsureAccSchema.setInvestType(tLMRiskInsuAccSchema.
                            getInvestType());
                    tLCInsureAccSchema.setFundCompanyCode(tLMRiskInsuAccSchema.
                            getFundCompanyCode());
                    tLCInsureAccSchema.setOwner(tLMRiskInsuAccSchema.getOwner());
                    tLCInsureAccSet.add(tLCInsureAccSchema);
                }
            }

            return tLCInsureAccSet;
        }

        return null;
    }


    /**
     * 生成保险帐户(生成结构:构建保险账户表,构建保费项表和客户账户表的关联表,构建给付项表和客户账户表的关联表)
     * @param parmData TransferData
     * @param inLCPolSchema LCPolSchema
     * @param inLMRiskSchema LMRiskSchema
     * @return VData
     */
    public VData createInsureAccForBat(TransferData parmData,
                                       LCPolSchema inLCPolSchema
                                       , LMRiskSchema inLMRiskSchema)
    {
        //1-检验
        if (!checkTransferData(parmData))
        {
            return null;
        }

        if ((inLCPolSchema == null) || (inLMRiskSchema == null))
        {
            return null;
        }

        //2-得到数据后用
        String tPolNo = (String) parmData.getValueByName("PolNo");
        String tAccCreatePos = (String) parmData.getValueByName("AccCreatePos");
        String tOtherNo = (String) parmData.getValueByName("OtherNo");
        String tOtherNoType = (String) parmData.getValueByName("OtherNoType");
        mEnterAccDate = (String) parmData.getValueByName("FinanceDate");
        if (mEnterAccDate == null || mEnterAccDate.equals(""))
        {
            mEnterAccDate = CurrentDate; //如未传入财务确认日，则签单日为资金注入帐户日 Alex 20051124
        }
        else
        {
            //否则以财务确认日为资金注入帐户日，不做任何修改
//            FDate tFDate = new FDate();
//            Date tEndDate = PubFun.calDate(tFDate.getDate(mEnterAccDate), 1,
//                                       "D", null);
//            mEnterAccDate=tFDate.getString(tEndDate);
        }
        if ("1".equals(inLCPolSchema.getContType()))
        {
            //个险以保单签单日期为资金注入帐户日，长城默认签单日与财务签单日相同
//            mEnterAccDate = inLCPolSchema.getCValiDate();
            mEnterAccDate = CurrentDate;
        }

        Double tRate;
        if (parmData.getValueByName("Rate") == null)
        {
            tRate = null;
        }
        else if (parmData.getValueByName("Rate").getClass().getName().equals(
                "java.lang.String"))
        {
            String strRate = (String) parmData.getValueByName("Rate");
            tRate = Double.valueOf(strRate);
        }
        else
        {
            tRate = (Double) parmData.getValueByName("Rate");
        }
        System.out.println("费率:" + tRate);

        //3-构建保险账户表
        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet();
        tLCInsureAccSet = getLCInsureAccForBat(tPolNo, tAccCreatePos, tOtherNo,
                                               tOtherNoType
                                               , inLCPolSchema, inLMRiskSchema);
        if (tLCInsureAccSet == null)
        {
            return null;
        }

        //4-构建保费项表和客户账户表的关联表
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet();
        tLCPremToAccSet = getPremToAccForBat(tPolNo, tAccCreatePos, tRate,
                                             inLCPolSchema);

        //4.5 构建账户分类表
        LCInsureAccClassSet tLCInsureAccClassSet = null;
        if (tLCPremToAccSet != null)
        {
            tLCInsureAccClassSet = getLCInsureAccClassForBat(inLCPolSchema,
                    tLCPremToAccSet);
        }

        //if(tLCPremToAccSet==null) return null;
        //5-构建给付项表和客户账户表的关联表
        LCGetToAccSet tLCGetToAccSet = new LCGetToAccSet();
        tLCGetToAccSet = getGetToAccForBat(tPolNo, tAccCreatePos, tRate,
                                           inLCPolSchema);

        //if(tLCGetToAccSet==null) return null;
        //6-返回数据
        VData tVData = new VData();
        tVData.add(tLCInsureAccSet);
        tVData.add(tLCPremToAccSet); //可能是null
        tVData.add(tLCInsureAccClassSet);
        tVData.add(tLCGetToAccSet); //可能是null

        return tVData;
    }


    /**
     * 创建管理费结构
     * @param tLCPolSchema LCPolSchema
     * @param tLCPremToAccSet LCPremToAccSet
     * @param tLCInsureAccSet LCInsureAccSet
     * @return VData
     */
    public VData getManageFeeStru(LCPolSchema tLCPolSchema,
                                  LCPremToAccSet tLCPremToAccSet
                                  , LCInsureAccSet tLCInsureAccSet)
    {

        VData tData = new VData();
        //保险帐户管理费表
        LCInsureAccFeeSet tLCInsureAccFeeSet = new LCInsureAccFeeSet();
        //保险账户管理费分类表
        LCInsureAccClassFeeSet tLCInsureAccClassFeeSet = new
                LCInsureAccClassFeeSet();
        //险种帐户描述表
        LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
        LCPremToAccSchema tLCPremToAccSchema = null;
        if (tLCPremToAccSet != null)
        {
            for (int t = 1; t <= tLCPremToAccSet.size(); t++)
            {
                tLCPremToAccSchema = tLCPremToAccSet.get(t);

                LCInsureAccSchema tLCInsureAccSchema = null;
                for (int i = 1; i <= tLCInsureAccSet.size(); i++)
                {
                    if (tLCInsureAccSet.get(i).getInsuAccNo().equals(
                            tLCPremToAccSchema.
                            getInsuAccNo()))
                    {
                        tLCInsureAccSchema = tLCInsureAccSet.get(i);
                        break;
                    }
                }
                if (tLCInsureAccSchema == null)
                {
                    // System.out.println("没有找对对应的账户");
                    continue;
                    // return null;
                }
                //查询险种帐户描述表，判定归属属性
                tLMRiskInsuAccDB.setInsuAccNo(tLCInsureAccSchema.getInsuAccNo());
                if (!tLMRiskInsuAccDB.getInfo())
                {
                    CError.buildErr(this, "查询险种账户描述表出错");
                    return null;
                }

                //创建管理费分类表
                LCInsureAccClassFeeSchema tLCInsureAccClassFeeSchema = new
                        LCInsureAccClassFeeSchema();
                tLCInsureAccClassFeeSchema.setGrpPolNo(tLCPolSchema.getGrpPolNo());
                tLCInsureAccClassFeeSchema.setPolNo(tLCPolSchema.getPolNo());
                tLCInsureAccClassFeeSchema.setInsuAccNo(tLCPremToAccSchema.
                        getInsuAccNo());
                tLCInsureAccClassFeeSchema.setPayPlanCode(tLCPremToAccSchema.
                        getPayPlanCode());
                //  tLCInsureAccClassFeeSchema.setAcc
                tLCInsureAccClassFeeSchema.setAccType(tLCInsureAccSchema.
                        getAccType());
                tLCInsureAccClassFeeSchema.setContNo(tLCPolSchema.getContNo());
                tLCInsureAccClassFeeSchema.setManageCom(tLCPolSchema.
                        getManageCom());
                tLCInsureAccClassFeeSchema.setGrpContNo(tLCPolSchema.
                        getGrpContNo());
                tLCInsureAccClassFeeSchema.setOtherType("1"); //个人保单号
                tLCInsureAccClassFeeSchema.setOtherNo(tLCPolSchema.getPolNo());
                tLCInsureAccClassFeeSchema.setRiskCode(tLCPolSchema.getRiskCode());
                tLCInsureAccClassFeeSchema.setInsuredNo(tLCPolSchema.
                        getInsuredNo());
                tLCInsureAccClassFeeSchema.setAppntNo(tLCPolSchema.getAppntNo());
                tLCInsureAccClassFeeSchema.setAccComputeFlag(tLCInsureAccSchema.
                        getAccComputeFlag());
                //            tLCInsureAccClassFeeSchema.setAccFoundDate(null);
//                tLCInsureAccClassFeeSchema.setBalaDate(tLCPolSchema.getCValiDate());
                tLCInsureAccClassFeeSchema.setBalaDate(tLCInsureAccSchema.
                        getBalaDate()); //置为对应帐户表的资金到帐日
                //            tLCInsureAccClassFeeSchema.setBalaTime();
                tLCInsureAccClassFeeSchema.setFee(0);
                tLCInsureAccClassFeeSchema.setFeeRate(0);
                tLCInsureAccClassFeeSchema.setFeeUnit(0);
                tLCInsureAccClassFeeSchema.setMakeDate(PubFun.getCurrentDate());
                tLCInsureAccClassFeeSchema.setOperator(tLCPolSchema.getOperator());
                tLCInsureAccClassFeeSchema.setMakeDate(CurrentDate);
                tLCInsureAccClassFeeSchema.setMakeTime(CurrentTime);
                tLCInsureAccClassFeeSchema.setModifyDate(CurrentDate);
                tLCInsureAccClassFeeSchema.setModifyTime(CurrentTime);
                //暂时假设未归属
                tLCInsureAccClassFeeSchema.setAccAscription("0");
                if (tLMRiskInsuAccDB.getOwner() != null &&
                    !"".equals(tLMRiskInsuAccDB.getOwner()))
                {
                    //重新设置归属属性
                    tLCInsureAccClassFeeSchema.setAccAscription(
                            tLMRiskInsuAccDB.getOwner());
                }
                tLCInsureAccClassFeeSet.add(tLCInsureAccClassFeeSchema);

                //管理费表
                //查找看是否已经存在
                boolean has = false;

                for (int j = 1; j <= tLCInsureAccFeeSet.size(); j++)
                {
                    if (tLCInsureAccFeeSet.get(j).getInsuAccNo().equals(
                            tLCPremToAccSchema.getInsuAccNo()))
                    {
                        has = true;
                        break;
                    }

                }

                if (has)
                {
                    continue;
                }
                //还没有，则创建
                LCInsureAccFeeSchema tLCInsureAccFeeSchema = new
                        LCInsureAccFeeSchema();
                tLCInsureAccFeeSchema.setAppntNo(tLCPolSchema.getAppntNo());
                //          tLCInsureAccFeeSchema.setAppntName(tLCPolSchema.getAppntName());
                tLCInsureAccFeeSchema.setInsuAccNo(tLCPremToAccSchema.
                        getInsuAccNo());
                tLCInsureAccFeeSchema.setManageCom(tLCPolSchema.getManageCom());
                tLCInsureAccFeeSchema.setPolNo(tLCPolSchema.getPolNo());
                tLCInsureAccFeeSchema.setRiskCode(tLCPolSchema.getRiskCode());
                tLCInsureAccFeeSchema.setInsuredNo(tLCPolSchema.getInsuredNo());
                tLCInsureAccFeeSchema.setContNo(tLCPolSchema.getContNo());
                tLCInsureAccFeeSchema.setGrpContNo(tLCPolSchema.getGrpContNo());
                tLCInsureAccFeeSchema.setGrpPolNo(tLCPolSchema.getGrpPolNo());
                // tLCInsureAccFeeSchema.setMoney(0);
                tLCInsureAccFeeSchema.setPrtNo(tLCPolSchema.getPrtNo());
                // tLCInsureAccFeeSchema.setUnitCount(0);
                tLCInsureAccFeeSchema.setAccType(tLCInsureAccSchema.getAccType());
                tLCInsureAccFeeSchema.setAccComputeFlag(tLCInsureAccSchema.
                        getAccComputeFlag());
                tLCInsureAccFeeSchema.setFundCompanyCode(tLCInsureAccSchema.
                        getFundCompanyCode());
                tLCInsureAccFeeSchema.setOwner(tLCInsureAccSchema.getOwner());
                tLCInsureAccFeeSchema.setInvestType(tLCInsureAccSchema.
                        getInvestType());
                tLCInsureAccFeeSchema.setMakeDate(CurrentDate);
                tLCInsureAccFeeSchema.setMakeTime(CurrentTime);
                tLCInsureAccFeeSchema.setModifyDate(CurrentDate);
                tLCInsureAccFeeSchema.setModifyTime(CurrentTime);
                tLCInsureAccFeeSchema.setOperator(tLCPolSchema.getOperator());
                tLCInsureAccFeeSet.add(tLCInsureAccFeeSchema);                
            }
        }
     
        tData.add(tLCInsureAccClassFeeSet);
        tData.add(tLCInsureAccFeeSet);
        return tData;
    }


    /**
	 * 创建保险账户分类表
	 * 
	 * @param tPolSchema
	 *            LCPolSchema
	 * @param tLCPremToAccSet
	 *            LCPremToAccSet
	 * @return LCInsureAccClassSet
	 */
    private LCInsureAccClassSet getLCInsureAccClassForBat(LCPolSchema
            tPolSchema
            , LCPremToAccSet tLCPremToAccSet)
    {
        LCInsureAccClassSet tSet = new LCInsureAccClassSet();
        LCPremToAccSchema tPremAccSchema = null;
        String nowDate = PubFun.getCurrentDate();
        String nowTime = PubFun.getCurrentTime();
        LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
        String tAccNo = "";
        for (int i = 1; i <= tLCPremToAccSet.size(); i++)
        {
            LCInsureAccClassSchema tSchema = new LCInsureAccClassSchema();
            tPremAccSchema = tLCPremToAccSet.get(i);
            //不校验了
            //由于要判断帐户类型，把校验加上 Alex 20050902
            //if (!tPremAccSchema.getInsuAccNo().equals(tAccNo)) //?
            //{
            tAccNo = tPremAccSchema.getInsuAccNo();
            tLMRiskInsuAccDB.setInsuAccNo(tAccNo);
            if (!tLMRiskInsuAccDB.getInfo())
            {
                CError.buildErr(this, "查询险种账户描述表出错");
                return null;
            }
            //如果帐户类型是集体帐户或集体红利帐户,退出  add by Alex 20050920
            //判断帐户类型，决定是否生成相关记录
            if (tLMRiskInsuAccDB.getAccType().equals("001") ||
                tLMRiskInsuAccDB.getAccType().equals("005"))
            {
                //001公共帐户，005公共红利帐户
                //如果保单类型是-2 --（团单）公共帐户
                //此时才生成对应的公共帐户记录
                if ((tPolSchema.getPolTypeFlag() != null)
                    && tPolSchema.getPolTypeFlag().equals("2"))
                {
                    System.out.println("开始生成集体帐户");
                }
                else
                {
                    continue;
                }
            }
            else
            {
                //添加判断，使生成公共帐户同时不生成个人帐户类型的记录
                //如果保单类型是-2 --（团单）公共帐户
                //不生成个人类型的帐户, 但是投资账户还是要生成的 
                if ((tPolSchema.getPolTypeFlag() != null)
                    && tPolSchema.getPolTypeFlag().equals("2") && (!"2".equals(tLMRiskInsuAccDB.getAccKind())))
                {
                    continue;
                }
                else
                {
                    System.out.println("开始生成个人帐户");
                }

            }

            //add by Alex 20050920

            //}

            tSchema.setAccType(tLMRiskInsuAccDB.getAccType());
            tSchema.setState("0");
            tSchema.setAccComputeFlag(tLMRiskInsuAccDB.getAccComputeFlag());

            tSchema.setPolNo(tPremAccSchema.getPolNo());
            tSchema.setInsuAccNo(tPremAccSchema.getInsuAccNo());
            tSchema.setPayPlanCode(tPremAccSchema.getPayPlanCode());
            tSchema.setContNo(tPolSchema.getContNo());
            //       tSchema.setAppntName(tPolSchema.getAppntName());
            tSchema.setInsuredNo(tPolSchema.getInsuredNo());
            tSchema.setGrpContNo(tPolSchema.getGrpContNo());
            tSchema.setGrpPolNo(tPolSchema.getGrpPolNo());
            tSchema.setFrozenMoney(0);
            //  tSchema.setBalaDate(nowDate);
            //   tSchema.setBalaTime(nowTime);
            tSchema.setInsuAccBala(0);
            tSchema.setInsuAccGetMoney(0);
            tSchema.setBalaDate(mEnterAccDate);
            tSchema.setMakeDate(nowDate);
            tSchema.setMakeTime(nowTime);
            tSchema.setManageCom(tPolSchema.getManageCom());
            tSchema.setModifyDate(nowDate);
            tSchema.setModifyTime(nowTime);
            tSchema.setOperator(this.mGlobalInput.Operator);
            tSchema.setOtherNo(tPolSchema.getPolNo());
            tSchema.setOtherType("1");
            tSchema.setState("0");
            tSchema.setSumPay(0);
            tSchema.setSumPaym(0);
//       tSchema.setState();
            //新加字段
            tSchema.setGrpContNo(tPolSchema.getGrpContNo());
            tSchema.setAppntNo(tPolSchema.getAppntNo());
            tSchema.setUnitCount(0);
            tSchema.setRiskCode(tPolSchema.getRiskCode());
            //暂时假设未归属-- wujs
            tSchema.setAccAscription("0");
            //根据描述表LMRiskInsuAcc中雇员雇主的表述来确定帐户的归属属性
            //描述时个人帐户个人交费部分和个人红利帐户个人交费部分为已归属－－－Alex 20050819
            if (tLMRiskInsuAccDB.getOwner() != null &&
                !"".equals(tLMRiskInsuAccDB.getOwner()))
            {
                tSchema.setAccAscription(tLMRiskInsuAccDB.getOwner());
            }
            tSet.add(tSchema);

        }
        return tSet;
    }


    /**
     * 生成保费项表和客户帐户表的关联表
     * @param PolNo 保单号
     * @param AccCreatePos 生成位置
     * @param Rate 费率
     * @param inLCPolSchema 传入保单数据
     * @return LCPremToAccSet 保费项关联表
     */
    public LCPremToAccSet getPremToAccForBat(String PolNo, String AccCreatePos,
                                             Double Rate,
                                             LCPolSchema inLCPolSchema)
    {
        if ((PolNo == null) || (AccCreatePos == null))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "getPremToAcc";
            tError.errorMessage = "错误原因:传入参数不能为空";
            this.mErrors.addOneError(tError);

            return null;
        }

        String tPolNo = PolNo;
        String tAccCreatePos = AccCreatePos;
        Double tRate = Rate;
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet();

        //1-取出保费项表
        LCPremSet tLCPremSet = new LCPremSet();
        tLCPremSet = queryLCPrem(tPolNo);
        if (tLCPremSet == null)
        {
            return null;
        }

        //2-根据保费项表取出对应的责任缴费描述表
        VData tVData = new VData();
        tVData = getFromLMDutyPay(tLCPremSet);
        if (tVData == null)
        {
            return null;
        }

        //3-生成保费项表和客户账户表的关联表
        tLCPremToAccSet = createPremToAccForBat(tVData, tPolNo, tAccCreatePos,
                                                tRate, inLCPolSchema);

        return tLCPremToAccSet;
    }


    /**
     * 生成保费项表和客户账户表的关联表
     * @param tVData VData 包含责任交费和保费项集合
     * @param PolNo String 保单号
     * @param AccCreatePos String 生成帐户的流程位置标记（承保，交费等）
     * @param Rate Double 提取比率
     * @param inLCPolSchema LCPolSchema
     * @return LCPremToAccSet
     */
    public LCPremToAccSet createPremToAccForBat(VData tVData, String PolNo,
                                                String AccCreatePos,
                                                Double Rate,
                                                LCPolSchema inLCPolSchema)
    {
        if ((tVData == null) || (StrTool.cTrim(PolNo).equals("")) || (AccCreatePos == null))
        {
            // @@错误处理
            CError.buildErr(this,"传入数据不能为空!");
            return null;
        }
        //TODO:
        //判断是否公共账户
        String tPolTypeFlag = StrTool.cTrim(inLCPolSchema.getPolTypeFlag());
        if("".equals(tPolTypeFlag)){
        	LCPolDB tLCPolDB = new LCPolDB();
        	tLCPolDB.setPolNo(PolNo);
        	tLCPolDB.getInfo();
        	tPolTypeFlag = StrTool.cTrim(tLCPolDB.getPolTypeFlag()); 
        	if("".equals(tPolTypeFlag)){
        	      // @@错误处理
                CError.buildErr(this,"传入数据有误!");
                return null;
        	}
        }
        
        LCPremSet tLCPremSet = (LCPremSet) tVData.getObjectByObjectName(
                "LCPremSet", 0);
        LMDutyPaySet tLMDutyPaySet = (LMDutyPaySet) tVData.
                                     getObjectByObjectName("LMDutyPaySet",
                0);

        LCPremSchema tLCPremSchema = new LCPremSchema(); //保费项表
        LMDutyPaySchema tLMDutyPaySchema = new LMDutyPaySchema(); //责任交费表
        LMRiskAccPaySchema tLMRiskAccPaySchema = new LMRiskAccPaySchema(); //
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet(); //保费项表和客户帐户表的关联表
        LCPremToAccSchema tLCPremToAccSchema = new LCPremToAccSchema(); //保费项表和客户帐户表的关联表
        LMRiskAccPaySet tLMRiskAccPaySet = null;
        double tRate = 0;
        for (int i = 1; i <= tLMDutyPaySet.size(); i++)
        {
            tLMDutyPaySchema = tLMDutyPaySet.get(i);
            tLCPremSchema = tLCPremSet.get(i);

            //判断是否和帐户关联
            if (tLMDutyPaySchema.getNeedAcc().equals("1"))
            {
                //查询险种保险帐户缴费表
                // tLMRiskAccPaySchema = new LMRiskAccPaySchema();
                tLMRiskAccPaySet = queryLMRiskAccPayForBat(tLMDutyPaySchema.
                        getPayPlanCode(),
                        inLCPolSchema.getRiskCode());
                if (tLMRiskAccPaySet == null || tLMRiskAccPaySet.size() == 0
                    )
                {
                    System.out.println("查询险种账户缴费表失败");
                    CError.buildErr(this, "查询险种账户缴费表失败");
                    return null;
                }
                for (int u = 1; u <= tLMRiskAccPaySet.size(); u++)
                {
                    tLMRiskAccPaySchema = tLMRiskAccPaySet.get(u);
                   
                    
                    //判断生成位置标记是否匹配
                    if (AccCreatePos.equals(tLMRiskAccPaySchema.getAccCreatePos()))
                    {
                        //判断费率是否需要录入
                        if (tLMRiskAccPaySchema.getNeedInput().equals("1"))
                        {
                            //如果需要录入:判断传入的费率是否为空
                            if (Rate == null)
                            {
                                // @@错误处理
                                CError.buildErr(this,"费率需要从界面录入，不能为空!") ;
                                return null;
                            }
                            tRate = Rate.doubleValue();
                        }
                        else
                        { //取默认值
                            tRate = tLMRiskAccPaySchema.getDefaultRate();
                        }
                        LMRiskInsuAccDB tLMRiskInsuAccDB = new LMRiskInsuAccDB();
                        tLMRiskInsuAccDB.setInsuAccNo(tLMRiskAccPaySchema.getInsuAccNo());
                        tLMRiskInsuAccDB.getInfo();
                        if ("2".equals(tPolTypeFlag))// 公共账户
						{
							if (!tLMRiskInsuAccDB.getAccType().equals("001")
									&& !tLMRiskInsuAccDB.equals("005"))
								continue;
						} else {// 个人账户
							if (tLMRiskInsuAccDB.getAccType().equals("001")
									|| tLMRiskInsuAccDB.equals("005"))
								continue;
						}
                        tLCPremToAccSchema = new LCPremToAccSchema();
//                      tLCPremToAccSchema.setPolNo(PolNo);
                        tLCPremToAccSchema.setPolNo(inLCPolSchema.getPolNo());
                        tLCPremToAccSchema.setDutyCode(tLCPremSchema.
                                getDutyCode());
                        tLCPremToAccSchema.setPayPlanCode(tLCPremSchema
                                .getPayPlanCode());
                        tLCPremToAccSchema.setInsuAccNo(tLMRiskAccPaySchema
                                .getInsuAccNo());
                        tLCPremToAccSchema.setRate(tRate);
                        tLCPremToAccSchema.setNewFlag(tLMRiskAccPaySchema
                                .getAccCreatePos());
                        tLCPremToAccSchema.setCalCodeMoney(tLMRiskAccPaySchema
                                .getCalCodeMoney());
                        tLCPremToAccSchema.setCalCodeUnit(tLMRiskAccPaySchema
                                .getCalCodeUnit());
                        tLCPremToAccSchema.setCalFlag(tLMRiskAccPaySchema
                                .getCalFlag());
                        tLCPremToAccSchema.setOperator(this.mGlobalInput.
                                Operator);
                        tLCPremToAccSchema.setMakeDate(CurrentDate);
                        tLCPremToAccSchema.setMakeTime(CurrentTime);
                        tLCPremToAccSchema.setModifyDate(CurrentDate);
                        tLCPremToAccSchema.setModifyTime(CurrentTime);

                        tLCPremToAccSet.add(tLCPremToAccSchema);
                    }
                }
            }
        }

        if (tLCPremToAccSet.size() == 0)
        {
            // @@错误处理
            return null;
        }

        return tLCPremToAccSet;
    }


    /**
     * 查询险种保险帐户缴费表
     * @param pLMDutyPaySchema LMDutyPaySchema
     * @param pLCPremSchema LCPremSchema
     * @param PolNo String
     * @param inLCPolSchema LCPolSchema
     * @return LMRiskAccPaySchema
     */
    public LMRiskAccPaySchema queryLMRiskAccPayForBat(LMDutyPaySchema
            pLMDutyPaySchema,
            LCPremSchema pLCPremSchema,
            String PolNo,
            LCPolSchema inLCPolSchema)
    {
        if ((pLMDutyPaySchema == null) || (pLCPremSchema == null)
            || (PolNo == null))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay";
            tError.errorMessage = "传入数据不能为空!";
            this.mErrors.addOneError(tError);

            return null;
        }

        //查询保单表
        LCPolSchema tLCPolSchema = inLCPolSchema;
        if (tLCPolSchema == null)
        { //取默认值

            return null;
        }

        String riskCode = tLCPolSchema.getRiskCode();
        String payPlanCode = pLMDutyPaySchema.getPayPlanCode();

        //查询险种保险帐户缴费表
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from LMRiskAccPay where RiskCode='");
        tSBql.append(riskCode);
        tSBql.append("' and payPlanCode='");
        tSBql.append(payPlanCode);
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
            tError.functionName = "queryLMRiskAccPay";
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
            tError.functionName = "queryLMRiskAccPay";
            tError.errorMessage = "险种保险帐户缴费表没有查询到相关数据!";
            this.mErrors.addOneError(tError);
            tLMRiskAccPaySet.clear();

            return null;
        }

        return tLMRiskAccPaySet.get(1);
    }


    /**
     * 生成给付项表和客户帐户表的关联表
     * @param PolNo String
     * @param AccCreatePos String
     * @param Rate Double
     * @param inLCPolSchema LCPolSchema
     * @return LCGetToAccSet
     */
    public LCGetToAccSet getGetToAccForBat(String PolNo, String AccCreatePos,
                                           Double Rate
                                           , LCPolSchema inLCPolSchema)
    {
        //1-取出领取项表
        LCGetSet tLCGetSet = new LCGetSet();
        tLCGetSet = queryLCGet(PolNo);
        if (tLCGetSet == null)
        {
            return null;
        }

        //2-根据领取项表取出对应的责任给付描述表
        //LMDutyGetSet tLMDutyGetSet = new LMDutyGetSet();
        VData tVData = new VData();
        tVData = createLMDutyGet(tLCGetSet);
        if (tVData == null)
        {
            return null;
        }

        //3-生成给付项表和客户账户表的关联表
        LCGetToAccSet tLCGetToAccSet = new LCGetToAccSet();
        tLCGetToAccSet = createGetToAccForBat(tVData, PolNo, AccCreatePos, Rate,
                                              inLCPolSchema);

        return tLCGetToAccSet;
    }


    /**
     * 生成给付项表和客户账户表的关联表
     * @param pVData VData
     * @param PolNo String
     * @param AccCreatePos String
     * @param Rate Double
     * @param inLCPolSchema LCPolSchema
     * @return LCGetToAccSet
     */
    public LCGetToAccSet createGetToAccForBat(VData pVData, String PolNo,
                                              String AccCreatePos
                                              , Double Rate,
                                              LCPolSchema inLCPolSchema)
    {
        if ((pVData == null) || (AccCreatePos == null) || (PolNo == null))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "createGetToAcc";
            tError.errorMessage = "传入数据不能为空!";
            this.mErrors.addOneError(tError);

            return null;
        }

        LMDutyGetSet tLMDutyGetSet = (LMDutyGetSet) pVData.
                                     getObjectByObjectName("LMDutyGetSet", 0);
        LCGetSet tLCGetSet = (LCGetSet) pVData.getObjectByObjectName("LCGetSet",
                0);
        LCPolSchema tLCPolSchema = inLCPolSchema;
        if (tLCPolSchema == null)
        {
            return null;
        }

        LMDutyGetSchema tLMDutyGetSchema = new LMDutyGetSchema(); //责任给付
        LMRiskAccGetSet tLMRiskAccGetSet = new LMRiskAccGetSet(); //责任给付
        LMRiskAccGetSchema tLMRiskAccGetSchema = new LMRiskAccGetSchema(); //险种保险帐户给付
        LCGetToAccSchema tLCGetToAccSchema = new LCGetToAccSchema(); //给付项表和客户账户表的关联表
        LCGetToAccSet tLCGetToAccSet = new LCGetToAccSet(); //给付项表和客户账户表的关联表

        for (int i = 1; i <= tLMDutyGetSet.size(); i++)
        {
            tLMDutyGetSchema = new LMDutyGetSchema();
            tLMDutyGetSchema = tLMDutyGetSet.get(i);

            //判断是否和帐户相关
            if (tLMDutyGetSchema.getNeedAcc().equals("1"))
            {
                //查询险种保险帐户给付表
                tLMRiskAccGetSet = new LMRiskAccGetSet();
                tLMRiskAccGetSet = queryLMRiskAccGet(tLCPolSchema.getRiskCode()
                        , tLMDutyGetSchema.getGetDutyCode());
                if (tLMRiskAccGetSet == null)
                {
                    continue;
                }
                for (int n = 1; n <= tLMRiskAccGetSet.size(); n++)
                {
                    tLMRiskAccGetSchema = new LMRiskAccGetSchema();
                    tLMRiskAccGetSchema = tLMRiskAccGetSet.get(n);
                    if (tLMRiskAccGetSchema.getDealDirection().equals("0")
                        &&
                        tLMRiskAccGetSchema.getAccCreatePos().equals(
                            AccCreatePos))
                    {
                        tLCGetToAccSchema = new LCGetToAccSchema();

                        //判断是否需要录入
                        if (tLMRiskAccGetSchema.getNeedInput().equals("1"))
                        {
                            if (Rate == null)
                            {
                                // @@错误处理
                                CError tError = new CError();
                                tError.moduleName = "DealAccount";
                                tError.functionName = "createGetToAcc";
                                tError.errorMessage = "费率需要从界面录入，不能为空!";
                                this.mErrors.addOneError(tError);

                                return null;
                            }
                            tLCGetToAccSchema.setDefaultRate(Rate.doubleValue());
                        }
                        else
                        {
                            tLCGetToAccSchema.setDefaultRate(
                                    tLMRiskAccGetSchema
                                    .getDefaultRate());
                        }

                        tLCGetToAccSchema.setNeedInput(tLMRiskAccGetSchema
                                .getNeedInput());
                        //  tLCGetToAccSchema.setPolNo(PolNo);
                        tLCGetToAccSchema.setPolNo(inLCPolSchema.getPolNo());
                        tLCGetToAccSchema.setDutyCode(tLCGetSet.get(i)
                                .getDutyCode());
                        tLCGetToAccSchema.setGetDutyCode(tLMRiskAccGetSchema
                                .getGetDutyCode());
                        tLCGetToAccSchema.setInsuAccNo(tLMRiskAccGetSchema
                                .getInsuAccNo());
                        tLCGetToAccSchema.setCalCodeMoney(tLMRiskAccGetSchema
                                .getCalCodeMoney());
                        tLCGetToAccSchema.setDealDirection(tLMRiskAccGetSchema
                                .getDealDirection());
                        tLCGetToAccSchema.setCalFlag(tLMRiskAccGetSchema
                                .getCalFlag());
                        tLCGetToAccSchema.setOperator(this.mGlobalInput.
                                Operator);
                        tLCGetToAccSchema.setMakeDate(CurrentDate);
                        tLCGetToAccSchema.setMakeTime(CurrentTime);
                        tLCGetToAccSchema.setModifyDate(CurrentDate);
                        tLCGetToAccSchema.setModifyTime(CurrentTime);
                        tLCGetToAccSet.add(tLCGetToAccSchema);
                    }
                }
            }
        }

        if (tLCGetToAccSet.size() == 0)
        {
            // @@错误处理
            return null;
        }

        return tLCGetToAccSet;
    }


    /**
     *** 以下的方法都是健康险系统增加的方法
     * * AUTHOR: GUOXIANG
     * * DATE: 2004-08-24
     * *************************************************************************************
     *
     * 生成保险帐户，生成结构SET对象:
     * 构建保险账户表, 调用getLCInsureAccForHealth(....)
     * 构建保费项表和客户账户表的关联表,调用getPremToAccForBat(....)
     * 构建给付项表和客户账户表的关联表,调用getGetToAccForBat(....)
     * @param parmData TransferData
     * @param inLCPolSchema LCPolSchema
     * @param inLMRiskSchema LMRiskSchema
     * @return VData
     */
    public VData createInsureAccForHealth(TransferData parmData,
                                          LCPolSchema inLCPolSchema,
                                          LMRiskSchema inLMRiskSchema)
    {
        //1-检验
        if (!checkTransferData(parmData))
        {
            return null;
        }

        if ((inLCPolSchema == null) || (inLMRiskSchema == null))
        {
            return null;
        }

        //2-得到数据后用
        String tPolNo = (String) parmData.getValueByName("PolNo");
        String tAccCreatePos = (String) parmData.getValueByName("AccCreatePos");
        String tOtherNo = (String) parmData.getValueByName("OtherNo");
        String tOtherNoType = (String) parmData.getValueByName("OtherNoType");
        Double tRate;
        if (parmData.getValueByName("Rate") == null)
        {
            tRate = null;
        }
        else if (parmData.getValueByName("Rate").getClass().getName().equals(
                "java.lang.String"))
        {
            String strRate = (String) parmData.getValueByName("Rate");
            tRate = Double.valueOf(strRate);
        }
        else
        {
            tRate = (Double) parmData.getValueByName("Rate");
        }
        System.out.println("费率:" + tRate);

        //3-构建保险账户表
        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet();
        tLCInsureAccSet = getLCInsureAccForHealth(tPolNo, tAccCreatePos,
                                                  tOtherNo, tOtherNoType,
                                                  inLCPolSchema, inLMRiskSchema);
        if (tLCInsureAccSet == null)
        {
            return null;
        }

        //4-构建保费项表和客户账户表的关联表
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet();
        tLCPremToAccSet = getPremToAccForBat(tPolNo, tAccCreatePos, tRate,
                                             inLCPolSchema);

        //if(tLCPremToAccSet==null) return null;
        //5-构建给付项表和客户账户表的关联表
        LCGetToAccSet tLCGetToAccSet = new LCGetToAccSet();
        tLCGetToAccSet = getGetToAccForBat(tPolNo, tAccCreatePos, tRate,
                                           inLCPolSchema);

        //if(tLCGetToAccSet==null) return null;
        //6-返回数据
        VData tVData = new VData();
        tVData.add(tLCInsureAccSet);
        tVData.add(tLCPremToAccSet); //可能是null
        tVData.add(tLCGetToAccSet); //可能是null

        return tVData;
    }


    /**
     * 健康险部分对个人保单生成保险帐户表的扩充    (类型 1：空帐户,不需要添加履历表纪录)
     * 原来处理方式是：把该险种的所有账户全部遍历，然后将数据插入到保险账户表
     * 现有处理方式是：并不是把所有账户都要插入到保险账户表，根据保费项表的缴费计划编码
     * 和给付项表的给付责任编码查对应的账户，然后将数据插入到保险账户表
     * 因此现在的账户处理过程为：
     * 1-校验
     * 2-判断是否与帐户相关
     * 3-根据投保单查询lcprem表(保费项表)和lcget表（给付项表）（数据库中是投保单号）
     * 5-根据投保单表中的险种字段查询LMRisk表
     * 6-判断是否与帐户相关
     * 7-判断是否与帐户相关
     * @param PolNo String 保单号
     * @param AccCreatePos String 生成位置 :1-签单时产生（承保） 2－缴费时产生 3－领取时产生
     * @param OtherNo String 保单号或交费号
     * @param OtherNoType String 保单号或交费号
     * @param inLCPolSchema LCPolSchema
     * @param inLMRiskSchema LMRiskSchema
     * @return LCInsureAccSet
     */
    public LCInsureAccSet getLCInsureAccForHealth(String PolNo,
                                                  String AccCreatePos,
                                                  String OtherNo
                                                  , String OtherNoType,
                                                  LCPolSchema inLCPolSchema,
                                                  LMRiskSchema inLMRiskSchema)
    {
        //1-校验
        if ((PolNo == null) || (AccCreatePos == null) || (OtherNo == null)
            || (OtherNoType == null) || (inLCPolSchema == null)
            || (inLMRiskSchema == null))
        {
            // @@错误处理
            buildError("getLCInsureAccForHealth", "传入数据不能为空!");

            return null;
        }

        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet(); //保险帐户表

        //2-判断是否与帐户相关
        if (inLMRiskSchema.getInsuAccFlag().equals("Y")
            || inLMRiskSchema.getInsuAccFlag().equals("y"))
        {
            //3根据投保单查询lcprem表(保费项表)和lcget表（给付项表）
            LCPremSet tLCPremSet = new LCPremSet();
            tLCPremSet = queryLCPrem(PolNo);
            if (tLCPremSet == null)
            {
                tLCPremSet = new LCPremSet();

            }

            LCGetSet tLCGetSet = new LCGetSet();
            tLCGetSet = queryLCGet(PolNo);
            if (tLCGetSet == null)
            {
                tLCGetSet = new LCGetSet();

            }

            LCPremSchema tLCPremSchema = new LCPremSchema(); //保费项表
            LCGetSchema tLCGetSchema = new LCGetSchema(); //给付项表

            LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema();
            LMRiskInsuAccSet tLMRiskInsuAccSet = new LMRiskInsuAccSet();
            LMRiskAccPaySet tLMRiskAccPaySet = null; //险种缴费表
            LMRiskAccGetSet tLMRiskAccGetSet = null; //险种给付表
            for (int i = 1; i <= tLCPremSet.size(); i++)
            //保费项集合---险种交费账户集合---险种账户集合
            {
                tLCPremSchema = tLCPremSet.get(i);

                //判断是否和帐户相关
                if (tLCPremSchema.getNeedAcc().equals("1"))
                {
                    LMRiskAccPaySchema tLMRiskAccPaySchema = new
                            LMRiskAccPaySchema();
                    tLMRiskAccPaySchema = queryLMRiskAccPay(tLCPremSchema,
                            inLMRiskSchema
                            .getRiskCode());
                    if (tLMRiskAccPaySchema == null)
                    {
                        continue;
                    }

                    LMRiskInsuAccSchema tLMRiskInsuAccSchema = new
                            LMRiskInsuAccSchema();

                    tLMRiskInsuAccSchema = queryLMRiskInsuAcc(
                            tLMRiskAccPaySchema);

                    if (tLMRiskInsuAccSchema == null)
                    {
                        continue;
                    }

                    tLMRiskInsuAccSet.add(tLMRiskInsuAccSchema);
                }
            }

            for (int i = 1; i <= tLCGetSet.size(); i++)
            //给付项集合---险种给付账户集合---险种账户集合（不包含缴费项的得到账户集合）
            {
                tLCGetSchema = tLCGetSet.get(i);

                //判断是否和帐户相关
                if (tLCGetSchema.getNeedAcc().equals("1"))
                {
                    LMRiskAccGetSchema tLMRiskAccGetSchema = new
                            LMRiskAccGetSchema();

                    LMRiskInsuAccSchema tLMRiskInsuAccSchema = new
                            LMRiskInsuAccSchema();

                    tLMRiskAccGetSchema = queryLMRiskAccGet(tLCGetSchema,
                            inLMRiskSchema
                            .getRiskCode());

                    if (tLMRiskAccGetSchema == null)
                    {
                        continue;
                    }

                    boolean continueFlag = false;
                    for (int j = 1; j <= tLMRiskInsuAccSet.size(); j++)
                    {
                        LMRiskInsuAccSchema pLMRiskInsuAccSchema =
                                tLMRiskInsuAccSet
                                .get(j);

                        if (tLMRiskAccGetSchema.getInsuAccNo().equals(
                                pLMRiskInsuAccSchema
                                .getInsuAccNo()))
                        {
                            continueFlag = true;

                            break;
                        }
                    }
                    if (continueFlag)
                    {
                        continue;
                    }

                    tLMRiskInsuAccSchema = queryLMRiskInsuAcc(
                            tLMRiskAccGetSchema);

                    if (tLMRiskInsuAccSchema == null)
                    {
                        continue;
                    }
                    tLMRiskInsuAccSet.add(tLMRiskInsuAccSchema);
                }
            }

            //循环并集
            for (int k = 1; k <= tLMRiskInsuAccSet.size(); k++)
            {
                //有险种账户表---生成保险账户表
                LMRiskInsuAccSchema tLMRiskInsuAccSchema = tLMRiskInsuAccSet
                        .get(k); //保险帐户表

                //如果账户生成位置找到匹配的保险账户
                if (tLMRiskInsuAccSchema.getAccCreatePos().equals(AccCreatePos))
                {
                    tLCInsureAccSchema = new LCInsureAccSchema();
                    tLCInsureAccSchema.setPolNo(PolNo);
                    tLCInsureAccSchema.setInsuAccNo(tLMRiskInsuAccSchema
                            .getInsuAccNo());
                    tLCInsureAccSchema.setRiskCode(inLMRiskSchema.getRiskCode());
                    tLCInsureAccSchema.setAccType(tLMRiskInsuAccSchema
                                                  .getAccType());
                    // tLCInsureAccSchema.setOtherNo(OtherNo);
                    // tLCInsureAccSchema.setOtherType(OtherNoType);
                    tLCInsureAccSchema.setContNo(inLCPolSchema.getContNo());
                    tLCInsureAccSchema.setGrpPolNo(inLCPolSchema.getGrpPolNo());
                    tLCInsureAccSchema.setGrpContNo(inLCPolSchema.getGrpContNo());
                    tLCInsureAccSchema.setInsuredNo(inLCPolSchema.getInsuredNo());
                    //                 tLCInsureAccSchema.setAppntName(inLCPolSchema.getAppntName());
                    tLCInsureAccSchema.setSumPay(0);
                    tLCInsureAccSchema.setInsuAccBala(0);
                    tLCInsureAccSchema.setUnitCount(0);
                    tLCInsureAccSchema.setInsuAccGetMoney(0);
                    tLCInsureAccSchema.setSumPaym(0);
                    tLCInsureAccSchema.setFrozenMoney(0);
                    tLCInsureAccSchema.setAccComputeFlag(tLMRiskInsuAccSchema
                            .getAccComputeFlag());
                    tLCInsureAccSchema.setManageCom(inLCPolSchema.getManageCom());
                    tLCInsureAccSchema.setOperator(inLCPolSchema.getOperator());
                    tLCInsureAccSchema.setBalaDate(inLCPolSchema.getCValiDate());
                    tLCInsureAccSchema.setMakeDate(CurrentDate);
                    tLCInsureAccSchema.setMakeTime(CurrentTime);
                    tLCInsureAccSchema.setModifyDate(CurrentDate);
                    tLCInsureAccSchema.setModifyTime(CurrentTime);
                    tLCInsureAccSet.add(tLCInsureAccSchema);
                }
            }

            return tLCInsureAccSet;
        }

        return null;
    }


    /**
     * 健康险系统注入资金取得管理费比例从保费项表中取
     * 修改calInputMoney() 为 calInputMoneyHealth()
     * 保险账户资金注入(类型3 针对保费项,注意没有给出注入资金，内部会调用计算金额的函数)
     * 适用于：在生成帐户结构后，此时数据尚未提交到数据库，又需要执行帐户的资金注入。
     * 即在使用了 createInsureAccHealth()方法后，得到VData数据，接着修改VData中帐户的金额
     * @param inVData VData 使用了 createInsureAcc()方法后，得到的VData数据
     * @param pLCPremSet LCPremSet 保费项集合
     * @param AccCreatePos String 参见 险种保险帐户缴费 LMRiskAccPay
     * @param OtherNo String 参见 保险帐户表 LCInsureAcc
     * @param OtherNoType String 号码类型
     * @param MoneyType String 参见 保险帐户表记价履历表 LCInsureAccTrace
     * @param RiskCode String 险种编码
     * @param Rate String 费率
     * @return VData
     */
    public VData addPremInnerHealth(VData inVData, LCPremSet pLCPremSet,
                                    String AccCreatePos
                                    , String OtherNo, String OtherNoType,
                                    String MoneyType, String RiskCode,
                                    String Rate)
    {
        if ((inVData == null) || (pLCPremSet == null) || (AccCreatePos == null)
            || (OtherNo == null) || (OtherNoType == null)
            || (MoneyType == null) || (RiskCode == null))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "addPrem";
            tError.errorMessage = "传入数据不能为空";
            this.mErrors.addOneError(tError);

            return null;
        }

        VData tVData = new VData();

        //得到生成的保险帐户表
        LCInsureAccSet tLCInsureAccSet = (LCInsureAccSet) (inVData
                .getObjectByObjectName("LCInsureAccSet",
                                       0));

        //得到生成的缴费帐户关联表
        LCPremToAccSet tLCPremToAccSet = (LCPremToAccSet) (inVData
                .getObjectByObjectName("LCPremToAccSet",
                                       0));

        //得到领取帐户关联表--目前不用
        LCGetToAccSet tLCGetToAccSet = (LCGetToAccSet) (inVData
                .getObjectByObjectName("LCGetToAccSet",
                                       0));

        if (tLCInsureAccSet == null)
        {
            tLCInsureAccSet = new LCInsureAccSet();
        }
        if (tLCPremToAccSet == null)
        {
            tLCPremToAccSet = new LCPremToAccSet();
        }
        if (tLCGetToAccSet == null)
        {
            tLCGetToAccSet = new LCGetToAccSet();
        }

        LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet(); //保险帐户表记价履历表
        LCInsureAccTraceSchema tLCInsureAccTraceSchema = new
                LCInsureAccTraceSchema();

        double inputMoney = 0;
        for (int n = 1; n <= pLCPremSet.size(); n++)
        {
            LCPremSchema tLCPremSchema = pLCPremSet.get(n);

            //判断是否帐户相关
            if (tLCPremSchema.getNeedAcc().equals("1"))
            {
                for (int m = 1; m <= tLCPremToAccSet.size(); m++)
                {
                    LCPremToAccSchema tLCPremToAccSchema = tLCPremToAccSet.get(
                            m);

                    //如果当前保费项和当前的缴费帐户关联表的保单号，责任编码，交费计划编码相同
                    if (tLCPremSchema.getPolNo().equals(tLCPremToAccSchema
                            .getPolNo())
                        &&
                        tLCPremSchema.getDutyCode().equals(tLCPremToAccSchema
                            .getDutyCode())
                        &&
                        tLCPremSchema.getPayPlanCode().equals(
                            tLCPremToAccSchema
                            .getPayPlanCode()))
                    {
                        //计算需要注入的资金
                        inputMoney = calInputMoneyHealth(tLCPremToAccSchema,
                                tLCPremSchema);
                        if (inputMoney == -1)
                        {
                            // @@错误处理
                            CError tError = new CError();
                            tError.moduleName = "DealAccount";
                            tError.functionName = "addPrem";
                            tError.errorMessage = "计算实际应该注入的资金出错";
                            this.mErrors.addOneError(tError);

                            return null;
                        }
                        for (int j = 1; j <= tLCInsureAccSet.size(); j++)
                        {
                            //如果当前缴费帐户关联表的保单号，账户号和当前的账户表的保单号，账户号相同并且资金不为0，将资金注入
                            LCInsureAccSchema tLCInsureAccSchema =
                                    tLCInsureAccSet
                                    .get(j);
                            if (tLCPremToAccSchema.getPolNo().equals(
                                    tLCInsureAccSchema
                                    .getPolNo())
                                &&
                                tLCPremToAccSchema.getInsuAccNo().equals(
                                    tLCInsureAccSchema
                                    .getInsuAccNo())
                                && (inputMoney != 0))
                            {
                                //修改保险帐户金额
                                tLCInsureAccSchema.setInsuAccBala(
                                        tLCInsureAccSchema
                                        .getInsuAccBala()
                                        + inputMoney);
                                tLCInsureAccSchema.setSumPay(tLCInsureAccSchema
                                        .getSumPay()
                                        + inputMoney);

                                //tLCInsureAccSchema.setInsuAccGetMoney(tLCInsureAccSchema.getInsuAccGetMoney()+inputMoney);
                                tLCInsureAccSet.set(j, tLCInsureAccSchema);

                                //查询险种保险帐户缴费
                                LMRiskAccPaySchema tLMRiskAccPaySchema =
                                        queryLMRiskAccPay3(
                                        RiskCode,
                                        tLCPremToAccSchema);
                                if (tLMRiskAccPaySchema == null)
                                {
                                    return null;
                                }
                                if (tLMRiskAccPaySchema.getPayNeedToAcc()
                                    .equals("1"))
                                {
                                    //填充保险帐户表记价履历表
                                    tLimit = PubFun.getNoLimit(tLCPremSchema
                                            .getManageCom());
                                    serNo = PubFun1.CreateMaxNo("SERIALNO",
                                            tLimit);
                                    tLCInsureAccTraceSchema = new
                                            LCInsureAccTraceSchema();
                                    tLCInsureAccTraceSchema.setSerialNo(serNo);
//                                    tLCInsureAccTraceSchema.setInsuredNo(
//                                            tLCInsureAccSchema
//                                            .getInsuredNo());
                                    tLCInsureAccTraceSchema.setPolNo(
                                            tLCInsureAccSchema
                                            .getPolNo());
                                    tLCInsureAccTraceSchema.setMoneyType(
                                            MoneyType);
                                    tLCInsureAccTraceSchema.setRiskCode(
                                            tLCInsureAccSchema
                                            .getRiskCode());
                                    tLCInsureAccTraceSchema.setOtherNo(OtherNo);
                                    tLCInsureAccTraceSchema.setOtherType(
                                            OtherNoType);
                                    tLCInsureAccTraceSchema.setMoney(inputMoney);
                                    tLCInsureAccTraceSchema.setContNo(
                                            tLCInsureAccSchema
                                            .getContNo());
                                    tLCInsureAccTraceSchema.setGrpPolNo(
                                            tLCInsureAccSchema
                                            .getGrpPolNo());
                                    tLCInsureAccTraceSchema.setInsuAccNo(
                                            tLCInsureAccSchema
                                            .getInsuAccNo());
                                    /*Lis5.3 upgrade set
                                     tLCInsureAccTraceSchema.setAppntName(tLCInsureAccSchema
                                     .getAppntName());
                                     */
                                    tLCInsureAccTraceSchema.setState(
                                            tLCInsureAccSchema
                                            .getState());
                                    tLCInsureAccTraceSchema.setManageCom(
                                            tLCInsureAccSchema
                                            .getManageCom());
                                    tLCInsureAccTraceSchema.setOperator(
                                            tLCInsureAccSchema
                                            .getOperator());
                                    tLCInsureAccTraceSchema.setMakeDate(
                                            CurrentDate);
                                    tLCInsureAccTraceSchema.setMakeTime(
                                            CurrentTime);
                                    tLCInsureAccTraceSchema.setModifyDate(
                                            CurrentDate);
                                    tLCInsureAccTraceSchema.setModifyTime(
                                            CurrentTime);
                                    tLCInsureAccTraceSchema.setPayDate(
                                            CurrentDate);
                                    tLCInsureAccTraceSet.add(
                                            tLCInsureAccTraceSchema);
                                }

                                break;
                            }
                        }
                    }
                }
            }
        }

        inVData.clear();
        inVData.add(tLCInsureAccSet);
        inVData.add(tLCPremToAccSet);
        inVData.add(tLCGetToAccSet);

        //添加帐户注入资金轨迹
        inVData.add(tLCInsureAccTraceSet);

        //操作数据库时执行插入操作
        return inVData; //(LCInsureAccSet,LCPremToAccSet,LCGetToAccSet,LCInsureAccTraceSet)
    }


    /**
     * 保险账户资金注入(类型1 针对保费项,注意没有给出注入资金，内部会调用计算金额的函数)
     * @param pLCPremSchema 保费项
     * @param AccCreatePos  参见 险种保险帐户缴费 LMRiskAccPay
     * @param OtherNo  参见 保险帐户表 LCInsureAcc
     * @param OtherNoType  号码类型
     * @param MoneyType  参见 保险帐户表记价履历表 LCInsureAccTrace
     * @param Rate 费率
     * @return VData(tLCInsureAccSet:update or insert ,tLCInsureAccTraceSet: insert)
     * @author guoxiang
     * @data 2004-9-2 10:14
     */
    public VData addPremHealth(LCPremSchema pLCPremSchema, String AccCreatePos,
                               String OtherNo, String OtherNoType,
                               String MoneyType,
                               Double Rate)
    {
        if ((pLCPremSchema == null) || (AccCreatePos == null)
            || (OtherNo == null) || (OtherNoType == null)
            || (MoneyType == null))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "addPrem";
            tError.errorMessage = "传入数据不能为空";
            this.mErrors.addOneError(tError);

            return null;
        }

        VData tVData = new VData();
        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet(); //保险帐户表
        LCInsureAccSchema tLCInsureAccSchema = new LCInsureAccSchema();
        LCInsureAccTraceSet tLCInsureAccTraceSet = new LCInsureAccTraceSet(); //保险帐户表记价履历表
        LCInsureAccTraceSchema tLCInsureAccTraceSchema = new
                LCInsureAccTraceSchema();
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet(); //保费项表和客户帐户表的关联表
        LCPremToAccSchema tLCPremToAccSchema = new LCPremToAccSchema();
        LMRiskAccPaySchema tLMRiskAccPaySchema = new LMRiskAccPaySchema();
        String newFlag = "";
        boolean addPrem = false;
        double inputMoney = 0;

        //判断是否帐户相关
        if (pLCPremSchema.getNeedAcc().equals("1"))
        {
            tLCPremToAccSet = queryLCPremToAccSet(pLCPremSchema);
            if (tLCPremToAccSet == null)
            {
                return null;
            }

            TransferData tFData = new TransferData();
            LCInsureAccSet mLCInsureAccSet = new LCInsureAccSet();

            //判断生成位置是否匹配
            if (AccCreatePos.equals(tLCPremToAccSet.get(1).getNewFlag()))
            {
                //如果匹配：生成帐户(即对于每次交费都产生新账号的情况，参看LCInsureAcc-保险帐户表)
                tFData = new TransferData();
                tFData.setNameAndValue("PolNo", pLCPremSchema.getPolNo());
                tFData.setNameAndValue("OtherNo", OtherNo); //对于每次交费都产生新账号的情况，该字段存放交费号。主键
                tFData.setNameAndValue("OtherNoType", OtherNoType);
                tFData.setNameAndValue("Rate", Rate);
                tLCInsureAccSet = new LCInsureAccSet();
                mLCInsureAccSet = getLCInsureAcc(pLCPremSchema.getPolNo(),
                                                 AccCreatePos, OtherNo,
                                                 OtherNoType);
                if (mLCInsureAccSet == null)
                {
                    return null;
                }
                newFlag = "INSERT";
            }

            for (int i = 1; i <= tLCPremToAccSet.size(); i++)
            {
                tLCPremToAccSchema = new LCPremToAccSchema();
                tLCPremToAccSchema = tLCPremToAccSet.get(i);

                //计算实际应该注入的资金
                inputMoney = calInputMoneyHealth(tLCPremToAccSchema,
                                                 pLCPremSchema);
                if (inputMoney == -1)
                {
                    // @@错误处理
                    CError tError = new CError();
                    tError.moduleName = "DealAccount";
                    tError.functionName = "addPrem";
                    tError.errorMessage = "计算实际应该注入的资金出错";
                    this.mErrors.addOneError(tError);

                    return null;
                }
                if (newFlag.equals("INSERT"))
                { //如果是新生成帐户
                    //根据保单号和保险账户号和其它号码查询mLCInsureAccSet集合中唯一一条数据
                    tLCInsureAccSchema = new LCInsureAccSchema();
                    tLCInsureAccSchema = queryLCInsureAccSet(pLCPremSchema
                            .getPolNo(),
                            tLCPremToAccSchema
                            .getInsuAccNo(),
                            OtherNo,
                            mLCInsureAccSet);
                    if (tLCInsureAccSchema == null)
                    {
                        return null;
                    }
                }
                else
                {
                    //根据保单号和保险账户号和其它号码查询LCInsureAcc表的唯一一条数据
                    tLCInsureAccSchema = new LCInsureAccSchema();
                    tLCInsureAccSchema = queryLCInsureAcc(pLCPremSchema
                            .getPolNo(),
                            tLCPremToAccSchema
                            .getInsuAccNo(),
                            OtherNo);
                    if (tLCInsureAccSchema == null)
                    {
                        return null;
                    }
                }

                //修改保险帐户金额
                tLCInsureAccSchema.setInsuAccBala(tLCInsureAccSchema
                                                  .getInsuAccBala()
                                                  + inputMoney);
                tLCInsureAccSchema.setSumPay(tLCInsureAccSchema.getSumPay()
                                             + inputMoney);
                tLCInsureAccSchema.setModifyDate(CurrentDate);
                tLCInsureAccSchema.setModifyTime(CurrentTime);

                //tLCInsureAccSchema.setInsuAccGetMoney(tLCInsureAccSchema.getInsuAccGetMoney()+inputMoney);
                tLMRiskAccPaySchema = queryLMRiskAccPay2(tLCPremToAccSchema); //查询险种保险帐户缴费
                if (tLMRiskAccPaySchema == null)
                {
                    return null;
                }
                if (tLMRiskAccPaySchema.getPayNeedToAcc().equals("1")
                    && (inputMoney != 0))
                {
                    //填充保险帐户表记价履历表
                    tLimit = PubFun.getNoLimit(pLCPremSchema.getManageCom());
                    serNo = PubFun1.CreateMaxNo("SERIALNO", tLimit);
                    tLCInsureAccTraceSchema = new LCInsureAccTraceSchema();
                    tLCInsureAccTraceSchema.setSerialNo(serNo);
//                    tLCInsureAccTraceSchema.setInsuredNo(tLCInsureAccSchema
//                            .getInsuredNo());
                    tLCInsureAccTraceSchema.setPolNo(tLCInsureAccSchema.
                            getPolNo());
                    tLCInsureAccTraceSchema.setMoneyType(MoneyType);
                    tLCInsureAccTraceSchema.setRiskCode(tLCInsureAccSchema.
                            getRiskCode());
                    tLCInsureAccTraceSchema.setOtherNo(OtherNo);
                    tLCInsureAccTraceSchema.setOtherType(OtherNoType);
                    tLCInsureAccTraceSchema.setMoney(inputMoney);
                    tLCInsureAccTraceSchema.setContNo(tLCInsureAccSchema.
                            getContNo());
                    tLCInsureAccTraceSchema.setGrpPolNo(tLCInsureAccSchema.
                            getGrpPolNo());
                    tLCInsureAccTraceSchema.setInsuAccNo(tLCInsureAccSchema.
                            getInsuAccNo());
                    /*Lis5.3 upgrade set
                     tLCInsureAccTraceSchema.setAppntName(tLCInsureAccSchema
                                                         .getAppntName());
                     */
                    tLCInsureAccTraceSchema.setState(tLCInsureAccSchema.
                            getState());
                    tLCInsureAccTraceSchema.setManageCom(tLCInsureAccSchema.
                            getManageCom());
                    tLCInsureAccTraceSchema.setOperator(tLCInsureAccSchema.
                            getOperator());
                    tLCInsureAccTraceSchema.setMakeDate(CurrentDate);
                    tLCInsureAccTraceSchema.setMakeTime(CurrentTime);
                    tLCInsureAccTraceSchema.setModifyDate(CurrentDate);
                    tLCInsureAccTraceSchema.setModifyTime(CurrentTime);
                    tLCInsureAccTraceSchema.setPayDate(CurrentDate);
                    tLCInsureAccTraceSet.add(tLCInsureAccTraceSchema);
                }

                //添加容器
                tLCInsureAccSet.add(tLCInsureAccSchema);
            }
        }
        if (tLCInsureAccSet.size() == 0)
        {
            // @@错误处理
            //      CError tError =new CError();
            //      tError.moduleName="DealAccount";
            //      tError.functionName="addPrem";
            //      tError.errorMessage="条件不符合，没有生成纪录";
            //      this.mErrors .addOneError(tError) ;
            return null;
        }
        tVData.add(tLCInsureAccSet);
        tVData.add(tLCInsureAccTraceSet);

        return tVData;

        //最后在操作VData时，（数据tLCInsureAccSet可能是update or insert）
        //因此操作数据库时先执行删除操作，再执行插入操作
    }


    /**
     * 健康险系统的管理费比例取自保费项表
     * 修改：tCalculator.addBasicFactor("ManageFeeRate",
     *       String.valueOf(tLCPolDB.getManageFeeRate())); //管理费比例-参见众悦年金分红-计算编码601304
     * 为：
     * 计算实际应该注入的资金(类似佣金计算,不过数据库内的计算编码尚未描述)
     * @param tLCPremToAccSchema LCPremToAccSchema 传入保费项表和客户帐户表的关联表纪录
     * @param tLCPremSchema LCPremSchema
     * @return double 实际应该注入的资金
     */
    public double calInputMoneyHealth(LCPremToAccSchema tLCPremToAccSchema
                                      , LCPremSchema tLCPremSchema)
    {
        // @@错误处理
        if (tLCPremToAccSchema == null)
        {
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "calInputMoneyRate";
            tError.errorMessage = "传入数据不能为空!";
            this.mErrors.addOneError(tError);

            return -1;
        }

        double Prem = tLCPremSchema.getPrem();
        /*Lis5.3 upgrade get
                 double ManageFeeRate = tLCPremSchema.getManageFeeRate();
         */
        double ManageFeeRate = 0;
        String[] F = new String[5];
        int m = 0;
        double defaultRate = 0;
        double inputMoney = 0;
        String calMoney = "";
        defaultRate = tLCPremToAccSchema.getRate(); //缺省比例

        Calculator tCalculator = new Calculator(); //计算类

        if (tLCPremToAccSchema.getCalFlag() == null)
        { //如果该标记为空
            inputMoney = Prem * 1 * defaultRate;

            return inputMoney;
        }

        //账户转入计算标志:0 －－ 完全转入账户
        // 1 －－ 按现金计算转入账户
        // 2 －－ 按股份计算转入账户
        // 3 －－ 先算现金，然后按股份计算。(未做)
        if (tLCPremToAccSchema.getCalFlag().equals("0"))
        {
            inputMoney = Prem * 1 * defaultRate;

            return inputMoney;
        }
        if (tLCPremToAccSchema.getCalFlag().equals("1"))
        {
            if (tLCPremToAccSchema.getCalCodeMoney() == null)
            {
                // @@错误处理
                CError tError = new CError();
                tError.moduleName = "DealAccount";
                tError.functionName = "calInputMoneyRate";
                tError.errorMessage = "未找到转入账户时的算法编码(现金)!";
                this.mErrors.addOneError(tError);

                return -1;
            }
            tCalculator.setCalCode(tLCPremToAccSchema.getCalCodeMoney()); //添加计算编码

            tCalculator.addBasicFactor("ManageFeeRate",
                                       String.valueOf(ManageFeeRate));

            //管理费比例-参见众悦年金分红-计算编码601304
            tCalculator.addBasicFactor("Prem", String.valueOf(Prem));

            //计算要素可后续添加
            calMoney = tCalculator.calculate();
            if (calMoney == null)
            {
                CError tError = new CError();
                tError.moduleName = "DealAccount";
                tError.functionName = "calInputMoneyRate";
                tError.errorMessage = "计算注入帐户资金失败!";
                this.mErrors.addOneError(tError);

                return -1;
            }
            inputMoney = Double.parseDouble(calMoney);

            return inputMoney;
        }
        if (tLCPremToAccSchema.getCalFlag().equals("2"))
        {
            if (tLCPremToAccSchema.getCalCodeMoney() == null)
            {
                // @@错误处理
                CError tError = new CError();
                tError.moduleName = "DealAccount";
                tError.functionName = "calInputMoneyRate";
                tError.errorMessage = "未找到转入账户时的算法编码(股份)";
                this.mErrors.addOneError(tError);

                return -1;
            }
            tCalculator.setCalCode(tLCPremToAccSchema.getCalCodeUnit()); //添加计算编码

            //添加计算必要条件：保费
            tCalculator.addBasicFactor("Prem", String.valueOf(Prem));
            calMoney = tCalculator.calculate();
            if (calMoney == null)
            {
                CError tError = new CError();
                tError.moduleName = "DealAccount";
                tError.functionName = "calInputMoneyRate";
                tError.errorMessage = "计算注入帐户资金失败!";
                this.mErrors.addOneError(tError);

                return -1;
            }
            inputMoney = Double.parseDouble(calMoney);

            return inputMoney;
        }

        return 0;
    }


    /**
     * 根据缴费项纪录和险种编码
     * 查询险种保险帐户缴费表
     * 因为只要保证一条缴费纪录
     * @param pLCPremSchema LCPremSchema
     * @param riskcode String
     * @return LMRiskAccPaySchema
     */
    public LMRiskAccPaySchema queryLMRiskAccPay(LCPremSchema pLCPremSchema,
                                                String riskcode)
    {
        if ((pLCPremSchema == null) || (riskcode == null))
        {
            // @@错误处理
            buildError("queryLMRiskAccPay", "传入数据不能为空！");

            return null;
        }

        String payPlanCode = pLCPremSchema.getPayPlanCode();

        //查询险种保险帐户缴费表
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from LMRiskAccPay where RiskCode='");
        tSBql.append(riskcode);
        tSBql.append("' and payPlanCode='");
        tSBql.append(payPlanCode);
        tSBql.append("'");

        LMRiskAccPaySet tLMRiskAccPaySet = new LMRiskAccPaySet();
        LMRiskAccPaySchema tLMRiskAccPaySchema = new LMRiskAccPaySchema();
        LMRiskAccPayDB tLMRiskAccPayDB = tLMRiskAccPaySchema.getDB();
        tLMRiskAccPaySet = tLMRiskAccPayDB.executeQuery(tSBql.toString());

        if (tLMRiskAccPayDB.mErrors.needDealError())
        {
            // @@错误处理
            buildError("queryLMRiskAccPay", "险种保险帐户缴费表查询失败！");

            return null;
        }
        if (tLMRiskAccPaySet.size() == 0)
        {
            // @@错误处理
            buildError("queryLMRiskAccPay", "险种保险帐户缴费表没有查询到相关数据！");
            return null;
        }

        return tLMRiskAccPaySet.get(1);
    }


    /**
     * 查询险种保险帐户给付表
     * @param pLCGetSchema LCGetSchema
     * @param riskcode String
     * @return LMRiskAccGetSchema
     */
    public LMRiskAccGetSchema queryLMRiskAccGet(LCGetSchema pLCGetSchema,
                                                String riskcode)
    {
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from LMRiskAccGet where GetDutyCode='");
        tSBql.append(pLCGetSchema.getGetDutyCode());
        tSBql.append("' and RiskCode='");
        tSBql.append(riskcode);
        tSBql.append("'");

        LMRiskAccGetSchema tLMRiskAccGetSchema = new LMRiskAccGetSchema();
        LMRiskAccGetSet tLMRiskAccGetSet = new LMRiskAccGetSet();
        LMRiskAccGetDB tLMRiskAccGetDB = tLMRiskAccGetSchema.getDB();
        tLMRiskAccGetSet = tLMRiskAccGetDB.executeQuery(tSBql.toString());

        if (tLMRiskAccGetDB.mErrors.needDealError())
        {
            // @@错误处理
            buildError("queryLMRiskAccGet", "险种保险帐户给付表查询失败！");

            return null;
        }
        if (tLMRiskAccGetSet.size() == 0)
        {
            // @@错误处理
            buildError("queryLMRiskAccGet", "险种保险帐户给付表没有查询到相关数据！");
            return null;
        }

        return tLMRiskAccGetSet.get(1);
    }


    /**
     * 查询险种保险帐户(交费)
     * @param tLMRiskAccPaySchema LMRiskAccPaySchema
     * @return LMRiskInsuAccSchema
     */
    public LMRiskInsuAccSchema queryLMRiskInsuAcc(LMRiskAccPaySchema
                                                  tLMRiskAccPaySchema)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LMRiskInsuAcc where InsuAccNo='");
        tSBql.append(tLMRiskAccPaySchema.getInsuAccNo());
        tSBql.append("'");

        LMRiskInsuAccSet tLMRiskInsuAccSet = new LMRiskInsuAccSet();
        LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
        LMRiskInsuAccDB tLMRiskInsuAccDB = tLMRiskInsuAccSchema.getDB();
        tLMRiskInsuAccSet = tLMRiskInsuAccDB.executeQuery(tSBql.toString());

        if (tLMRiskInsuAccDB.mErrors.needDealError())
        {
            // @@错误处理
            buildError("queryLMRiskInsuAcc", "险种保险帐户表查询失败！");

            return null;
        }
        if (tLMRiskInsuAccSet.size() == 0)
        {
            // @@错误处理
            buildError("queryLMRiskInsuAcc", "险种保险帐户表没有查询到相关数据！");

            return null;
        }

        return tLMRiskInsuAccSet.get(1);
    }


    /**
     * 查询险种保险帐户(给付)
     * @param tLMRiskAccGetSchema LMRiskAccGetSchema
     * @return LMRiskInsuAccSchema
     */
    public LMRiskInsuAccSchema queryLMRiskInsuAcc(LMRiskAccGetSchema
                                                  tLMRiskAccGetSchema)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LMRiskInsuAcc where InsuAccNo='");
        tSBql.append(tLMRiskAccGetSchema.getInsuAccNo());
        tSBql.append("'");

        LMRiskInsuAccSet tLMRiskInsuAccSet = new LMRiskInsuAccSet();
        LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
        LMRiskInsuAccDB tLMRiskInsuAccDB = tLMRiskInsuAccSchema.getDB();
        tLMRiskInsuAccSet = tLMRiskInsuAccDB.executeQuery(tSBql.toString());

        if (tLMRiskInsuAccDB.mErrors.needDealError())
        {
            // @@错误处理
            buildError("queryLMRiskInsuAcc", "险种保险帐户表查询失败！");

            return null;
        }
        if (tLMRiskInsuAccSet.size() == 0)
        {
            // @@错误处理
            buildError("queryLMRiskInsuAcc", "险种保险帐户表没有查询到相关数据！");

            return null;
        }

        return tLMRiskInsuAccSet.get(1);
    }

    private void buildError(String szFunc, String szErrMsg)
    {
        CError cError = new CError();
        cError.moduleName = "DealAccount";
        cError.functionName = szFunc;
        cError.errorMessage = szErrMsg;
        this.mErrors.addOneError(cError);
    }


    /**
     * 查询险种保险帐户缴费表
     * @param payPlanCode String
     * @param riskCode String
     * @return LMRiskAccPaySet
     */
    private LMRiskAccPaySet queryLMRiskAccPayForBat(String payPlanCode,
            String riskCode)
    {
        //查询险种保险帐户缴费表
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from LMRiskAccPay where RiskCode='");
        tSBql.append(riskCode);
        tSBql.append("' and payPlanCode='");
        tSBql.append(payPlanCode);
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
            tError.functionName = "queryLMRiskAccPay";
            tError.errorMessage = "险种保险帐户缴费表查询失败!";
            this.mErrors.addOneError(tError);
            tLMRiskAccPaySet.clear();

            return null;
        }
        if (tLMRiskAccPaySet == null || tLMRiskAccPaySet.size() == 0)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMRiskAccPayDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskAccPay";
            tError.errorMessage = "险种保险帐户缴费表没有查询到相关数据!";
            this.mErrors.addOneError(tError);
            tLMRiskAccPaySet.clear();

            return null;
        }

        return tLMRiskAccPaySet;
    }


    /**
     * 查询险种保险帐户(类型1)
     * @param InsuAccNo String
     * @return LMRiskInsuAccSet
     */
    private LMRiskInsuAccSet queryLMRiskInsuAccSet(String InsuAccNo)
    {
        StringBuffer tSBql = new StringBuffer(64);
        tSBql.append("select * from LMRiskInsuAcc where InsuAccNo='");
        tSBql.append(InsuAccNo);
        tSBql.append("'");

        LMRiskInsuAccSchema tLMRiskInsuAccSchema = new LMRiskInsuAccSchema();
        LMRiskInsuAccSet tLMRiskInsuAccSet = new LMRiskInsuAccSet();
        LMRiskInsuAccDB tLMRiskInsuAccDB = tLMRiskInsuAccSchema.getDB();
        tLMRiskInsuAccSet = tLMRiskInsuAccDB.executeQuery(tSBql.toString());

        if (tLMRiskInsuAccDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMRiskInsuAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskInsuAcc";
            tError.errorMessage = "险种保险帐户表查询失败!";
            this.mErrors.addOneError(tError);
            tLMRiskInsuAccSet.clear();

            return null;
        }
        if (tLMRiskInsuAccSet.size() == 0)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMRiskInsuAccDB.mErrors);

            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "queryLMRiskInsuAcc";
            tError.errorMessage = "险种保险帐户表没有查询到相关数据!";
            this.mErrors.addOneError(tError);
            tLMRiskInsuAccSet.clear();

            return null;
        }

        return tLMRiskInsuAccSet;
    }


    /**
     * 测试函数
     * @param args String[]
     */
    public static void main(String[] args)
    {
        DealAccount dealAccount1 = new DealAccount();
        String PolNo = "86110020020210000217";
        String AccCreatePos = "2"; //－－缴费时产生
        String OtherNo = "86110020020210000217";
        String OtherNoType = "1"; //－－ 个人保单号
        Double Rate = new Double(0.5);
        TransferData tTransferData = new TransferData();
        tTransferData.setNameAndValue("PolNo", PolNo);
        tTransferData.setNameAndValue("AccCreatePos", AccCreatePos);
        tTransferData.setNameAndValue("OtherNo", OtherNo);
        tTransferData.setNameAndValue("OtherNoType", OtherNoType);
        tTransferData.setNameAndValue("Rate", Rate);

        VData tVData = new VData();

        dealAccount1.addPremTraceForAcc("86110020030220000068", "000003", 0.0);

        //测试生成帐户
        //tVData=dealAccount1.createInsureAcc(tTransferData);
        //测试生成保险帐户表
        LCInsureAccSet tLCInsureAccSet = new LCInsureAccSet();
        tLCInsureAccSet = dealAccount1.getLCInsureAcc(PolNo, AccCreatePos,
                OtherNo, OtherNoType);

        //测试生成保费项表和客户帐户表的关联表
        LCPremToAccSet tLCPremToAccSet = new LCPremToAccSet();
        tLCPremToAccSet = dealAccount1.getPremToAcc(PolNo, AccCreatePos, Rate);

        //生成给付项表和客户帐户表的关联表
        LCGetToAccSet tLCGetToAccSet = new LCGetToAccSet();
        tLCGetToAccSet = dealAccount1.getGetToAcc(PolNo, AccCreatePos, Rate);

        //测试保险账户资金注入
        LCPremSchema tLCPremSchema = new LCPremSchema();
        LCPremSet tLCPremSet = new LCPremSet();
        tLCPremSet = dealAccount1.queryLCPrem(PolNo);
        tLCPremSchema = tLCPremSet.get(1);
        tVData.clear();
        tVData = dealAccount1.addPrem(tLCPremSchema, AccCreatePos, OtherNo,
                                      OtherNoType, "BF", Rate);
    }
}

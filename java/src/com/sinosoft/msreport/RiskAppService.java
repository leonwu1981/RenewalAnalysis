/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import com.sinosoft.lis.db.LFRiskAppSubDB;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.pubfun.ReportPubFun;
import com.sinosoft.lis.schema.LFDesbModeSchema;
import com.sinosoft.lis.schema.LFRiskAppSchema;
import com.sinosoft.lis.schema.LFRiskAppSubSchema;
import com.sinosoft.lis.vschema.LFRiskAppSubSet;
import com.sinosoft.utility.*;

/**
 * <p>Title: 民生核心业务系统</p>
 * <p>Description:实现接口 保费收入，承保人次，件次，保险金额</p>
 * <p>Copyright: SINOSOFT Copyright (c) 2004</p>
 * <p>Company: 中科软科技</p>
 * @author guoxiang
 * @version 1.0
 */
public class RiskAppService implements CalService
{
    /** 传入数据的容器 */
//    private VData mInputData = new VData();

    /** 传出数据的容器 */
    private VData mResult = new VData();

    /** 数据操作字符串 */
//    private String mOperate;

    /** 错误处理类 */
    public CErrors mErrors = new CErrors();

    /** 业务处理相关变量 */
    private TransferData mTransferData = new TransferData();
    private LFDesbModeSchema mLFDesbModeSchema = new LFDesbModeSchema();

    public RiskAppService()
    {
    }

    public boolean submitData(VData cInputData, String cOperate)
    {
//        mOperate = cOperate;

        // 得到外部传入的数据，将数据备份到本类中
        if (!getInputData(cInputData))
        {
            return false;
        }

        mResult.clear();

        // 准备所有 Insert 的数据
        if (!DealData())
        {
            return false;
        }

        return true;
    }

    public VData getResult()
    {
        return mResult;
    }

    public CErrors getErrors()
    {
        return mErrors;
    }

    /**
     * 从输入数据中得到所有对象
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @param cInputData VData
     * @return boolean
     */
    private boolean getInputData(VData cInputData)
    {
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData",
                0);
        mLFDesbModeSchema.setSchema((LFDesbModeSchema) cInputData
                                    .getObjectByObjectName("LFDesbModeSchema",
                0));

        if (mLFDesbModeSchema == null)
        {
            buildError("getInputData", "没有得到足够的信息！");

            return false;
        }
        if (mTransferData == null)
        {
            buildError("getInputData", "没有得到足够的信息！");

            return false;
        }
        if (!mLFDesbModeSchema.getDealType().equals("C"))
        {
            buildError("getInputData", "查询描述表错误--该描述处于");

            return false;
        }

        return true;
    }

    private boolean DealData()
    {
        String XQ_sql =
                "select distinct riskcode,managecom,payIntv,FirstPayFlag,ReportDate"
                + " from lfriskappsub where firstpayflag='2'";
        String SQQJ_sql =
                "select distinct riskcode,managecom,payIntv,FirstPayFlag,ReportDate "
                + " from lfriskappsub  where (firstpayflag='1' and  payintv>0) or (firstpayflag='Z' and payintv=3333)";
        String SQDJ_sql =
                " select distinct riskcode,managecom,payIntv,FirstPayFlag,ReportDate "
                + "from lfriskappsub  where (firstpayflag='1' and  payintv<=0)";
        SaveData(XQ_sql);
        SaveData(SQQJ_sql);
        SaveData(SQDJ_sql);

        return true;
    }

    private boolean SaveData(String sql)
    {
        ExeSQL exesql = new ExeSQL();
        SSRS ssrs = exesql.execSQL(sql);

        for (int i = 1; i <= ssrs.MaxRow; i++)
        {
            LFRiskAppSchema tLFRiskAppSchema = new LFRiskAppSchema();
            String SelSql = "select * from lfriskappsub where 1=1 "
                            + ReportPubFun.getWherePart("riskcode",
                    ssrs.GetText(i, 1))
                            + ReportPubFun.getWherePart("managecom",
                    ssrs.GetText(i, 2))
                            + ReportPubFun.getWherePart("PayIntv",
                    ssrs.GetText(i, 3))
                            + ReportPubFun.getWherePart("FirstPayFlag",
                    ssrs.GetText(i, 4))
                            + ReportPubFun.getWherePart("ReportDate",
                    ssrs.GetText(i, 5));

            LFRiskAppSubDB tLFRiskAppSubDB = new LFRiskAppSubDB();

            LFRiskAppSubSet tLFRiskAppSubSet = tLFRiskAppSubDB.executeQuery(
                    SelSql);
            System.out.println("SelSql:" + SelSql);
            tLFRiskAppSchema.setRiskCode(ssrs.GetText(i, 1));
            tLFRiskAppSchema.setManageCom(ssrs.GetText(i, 2));
            tLFRiskAppSchema.setPayIntv(ssrs.GetText(i, 3));
            tLFRiskAppSchema.setSaleChnl("z");
            tLFRiskAppSchema.setPersonPolFlag("z");
            tLFRiskAppSchema.setFirstPayFlag(ssrs.GetText(i, 4));
            tLFRiskAppSchema.setReportDate(ssrs.GetText(i, 5));
            tLFRiskAppSchema.setMakeDate(PubFun.getCurrentDate());
            tLFRiskAppSchema.setMakeTime(PubFun.getCurrentTime());
            for (int j = 1; j <= tLFRiskAppSubSet.size(); j++)
            {
                LFRiskAppSubSchema tLFRiskAppSubSchema = tLFRiskAppSubSet.get(j);
                if (tLFRiskAppSubSchema.getMeasurementType().equals("11")
                    && (tLFRiskAppSubSchema != null)) //11本年累计保费每日新增
                {
                    tLFRiskAppSchema.setPrem(tLFRiskAppSubSchema
                                             .getCurYearValue());
                }
                if (tLFRiskAppSubSchema.getMeasurementType().equals("12")
                    && (tLFRiskAppSubSchema != null)) //12期末有效保费每日新增
                {
                    tLFRiskAppSchema.setPremSum(tLFRiskAppSubSchema
                                                .getCurYearValue());
                }

                if (tLFRiskAppSubSchema.getMeasurementType().equals("21")
                    && (tLFRiskAppSubSchema != null))
                {
                    tLFRiskAppSchema.setAmnt(tLFRiskAppSubSchema
                                             .getCurYearValue());
                }
                if (tLFRiskAppSubSchema.getMeasurementType().equals("22")
                    && (tLFRiskAppSubSchema != null))
                {
                    tLFRiskAppSchema.setAmntSum(tLFRiskAppSubSchema
                                                .getCurYearValue());
                }
                if (tLFRiskAppSubSchema.getMeasurementType().equals("31")
                    && (tLFRiskAppSubSchema != null)) //11本年累计保费每日新增
                {
                    tLFRiskAppSchema.setInsuredCount(tLFRiskAppSubSchema
                            .getCurYearValue());
                }
                if (tLFRiskAppSubSchema.getMeasurementType().equals("32")
                    && (tLFRiskAppSubSchema != null)) //12期末有效保费每日新增
                {
                    tLFRiskAppSchema.setInsuredCountSum(tLFRiskAppSubSchema
                            .getCurYearValue());
                }

                if (tLFRiskAppSubSchema.getMeasurementType().equals("41")
                    && (tLFRiskAppSubSchema != null))
                {
                    tLFRiskAppSchema.setPolCount(tLFRiskAppSubSchema
                                                 .getCurYearValue());
                }
                if (tLFRiskAppSubSchema.getMeasurementType().equals("42")
                    && (tLFRiskAppSubSchema != null))
                {
                    tLFRiskAppSchema.setPolCountSum(tLFRiskAppSubSchema
                            .getCurYearValue());
                }
            }

            if ((tLFRiskAppSchema.getPrem() == 0)
                && (tLFRiskAppSchema.getPrem() == 0)
                && (tLFRiskAppSchema.getPremSum() == 0)
                && (tLFRiskAppSchema.getAmnt() == 0)
                && (tLFRiskAppSchema.getPrem() == 0)
                && (tLFRiskAppSchema.getAmntSum() == 0)
                && (tLFRiskAppSchema.getInsuredCount() == 0)
                && (tLFRiskAppSchema.getInsuredCountSum() == 0)
                && (tLFRiskAppSchema.getPolCount() == 0)
                && (tLFRiskAppSchema.getPolCountSum() == 0))
            {
                continue;
            }
            System.out.println(tLFRiskAppSchema.getPrem());
            System.out.println(tLFRiskAppSchema.getPremSum());
            System.out.println(tLFRiskAppSchema.getAmnt());
            System.out.println(tLFRiskAppSchema.getAmntSum());
            System.out.println(tLFRiskAppSchema.getInsuredCount());
            System.out.println(tLFRiskAppSchema.getInsuredCountSum());
            System.out.println(tLFRiskAppSchema.getPolCount());
            System.out.println(tLFRiskAppSchema.getPolCountSum());

            MMap mMap = new MMap();
            mMap.put(tLFRiskAppSchema, "insert");
            mResult.add(mMap);

        }

        return true;
    }

    /**
     * 构建错误信息
     * add by kevin, 2002-10-14
     * @param szFunc String
     * @param szErrMsg String
     */
    private void buildError(String szFunc, String szErrMsg)
    {
        CError cError = new CError();
        cError.moduleName = "RiskAppService";
        cError.functionName = szFunc;
        cError.errorMessage = szErrMsg;
        this.mErrors.addOneError(cError);
    }
}

/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;

/**
 * <p>Title: 民生核心业务系统</p>
 * <p>Description: </p>
 * <p>Copyright: SINOSOFT Copyright (c) 2004</p>
 * <p>Company: 中科软科技</p>
 * @author guoxiang
 * @version 1.0
 */
public class ReportEngineUI
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 往后面传输数据的容器 */
    private VData mInputData = new VData();

    /** 往界面传输数据的容器 */
    private VData mResult = new VData();

    /** 数据操作字符串 */
    private String mOperate;

    public ReportEngineUI()
    {
    }

    public boolean submitData(VData cInputData, String cOperate)
    {
        //将操作数据拷贝到本类中

        this.mOperate = cOperate;
        ReportEngineBL tReportEngineBL = new ReportEngineBL();
        System.out.println("---ReportEngineUI BEGIN---");
        if (!tReportEngineBL.submitData(cInputData, mOperate))
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tReportEngineBL.mErrors);
            buildError("submitData", "数据查询失败");
            mResult.clear();

            return false;
        }
        else
        {
            mResult = tReportEngineBL.getResult();
            this.mErrors.copyAllErrors(tReportEngineBL.mErrors);
        }

        return true;
    }

    public VData getResult()
    {
        return mResult;
    }

    /*
     * add by kevin, 2002-10-14
     */
    private void buildError(String szFunc, String szErrMsg)
    {
        CError cError = new CError();
        cError.moduleName = "ReportEngineUI";
        cError.functionName = szFunc;
        cError.errorMessage = szErrMsg;
        this.mErrors.addOneError(cError);
    }

    // @Main
    public static void main(String[] args)
    {
        VData tVData = new VData();
        GlobalInput mGlobalInput = new GlobalInput();
        TransferData mTransferData = new TransferData();

        /** 全局变量 */
        mGlobalInput.Operator = "Admin";
        mGlobalInput.ComCode = "asd";
        mGlobalInput.ManageCom = "sdd";

        /** 传递变量 */

//        mTransferData.setNameAndValue("RepType", "1");
//        mTransferData.setNameAndValue("StatYear", "2004");
//        mTransferData.setNameAndValue("StatMon", "04");
        //一级汇总
//        mTransferData.setNameAndValue("ReportDate", "2004-06-10");
//        mTransferData.setNameAndValue("sDate", "2003-03-01");
//        mTransferData.setNameAndValue("eDate", "2003-12-10");
//
//        mTransferData.setNameAndValue("makedate", "2004-03-10");
//        mTransferData.setNameAndValue("maketime", "11:11:11");


        //XML 汇总

        mTransferData.setNameAndValue("RepType", "4"); //统计类型
        mTransferData.setNameAndValue("StatYear", "2004"); //统计年
        mTransferData.setNameAndValue("StatMon", "07"); //统计月
        mTransferData.setNameAndValue("sYearDate", "2004-01-01"); //统计年初
        mTransferData.setNameAndValue("sDate", "2004-01-01"); //统计开始时间
        mTransferData.setNameAndValue("eDate", "2004-07-25"); //统计结束时间

        tVData.add(mGlobalInput);
        tVData.add("1");
        tVData.add(mTransferData);

        try
        {
            ReportEngineUI tReportEngineUI = new ReportEngineUI();

            if (!tReportEngineUI.submitData(tVData, "" + "||" + "2043" + "|| AND ItemCode ='2043' AND Dealtype='S' AND ItemType In ('&','X1') order by ItemNum"))
            {
                if (tReportEngineUI.mErrors.needDealError())
                {
                    System.out.println(tReportEngineUI.mErrors.getFirstError());
                }
                else
                {
                    System.out.println("保存失败，但是没有详细的原因");
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}

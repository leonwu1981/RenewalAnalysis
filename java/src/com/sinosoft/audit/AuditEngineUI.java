/**
 * Copyright (c) 2006 sinosoft Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.audit;

import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.tb.*;
import com.sinosoft.lis.vschema.*;

import com.sinosoft.utility.*;

/**
 * <p>
 * ClassName: AuditEngineUI
 * </p>
 * <p>
 * Company: Sinosoft Co. Ltd.
 * </p>
 * @author Alex
 * @version 1.0
 */
public class AuditEngineUI
{
    public CErrors mErrors = new CErrors();

    private VData mInputData = new VData();

    private VData mResult = new VData();

    private String mOperate;

	/**
	 * AuditEngineUI
	 */
    public AuditEngineUI()
    {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

	/**
	 * submitData
	 * 通用接口：接收传入数据，并对数据进行处理
	 * @param VData cInputData
	 * @param String cOperate
	 */
    public boolean submitData(VData cInputData, String cOperate)
    {
        this.mOperate = cOperate;

        AuditEngineBL tAuditEngineBL = new AuditEngineBL();
        System.out.println("---AuditEngineUI BEGIN---");

        if (!tAuditEngineBL.submitData(cInputData, mOperate))
        {
            this.mErrors.copyAllErrors(tAuditEngineBL.mErrors);
            buildError("submitData", "数据查询失败");
            mResult.clear();

            return false;
        }
        else
        {
            mResult = tAuditEngineBL.getResult();
            this.mErrors.copyAllErrors(tAuditEngineBL.mErrors);
        }

        return true;
    }

	/**
	 * getResult
	 * 数据返回
	 */
    public VData getResult()
    {
        return mResult;
    }

	/**
	 * buildError
	 * 构建错误
	 * @param String szFunc
	 * @param String szErrMsg
	 */
    private void buildError(String szFunc, String szErrMsg)
    {
        CError cError = new CError();
        cError.moduleName = "ReportEngineUI";
        cError.functionName = szFunc;
        cError.errorMessage = szErrMsg;
        this.mErrors.addOneError(cError);
    }

	/**
	 * main
	 * 应用测试
	 * @param String[] args
	 */
    public static void main(String[] args)
    {
        VData tVData = new VData();
        GlobalInput mGlobalInput = new GlobalInput();
        TransferData mTransferData = new TransferData();

        mGlobalInput.Operator = "audit";
        mGlobalInput.ComCode = "86";
        mGlobalInput.ManageCom = "86";

        //        mTransferData.setNameAndValue("StatMon", "04");
        mTransferData.setNameAndValue("NeedItemKey", "0"); 
        mTransferData.setNameAndValue("ManageCom", "86"); 
        mTransferData.setNameAndValue("StartDate", "2005-05-01"); 
        mTransferData.setNameAndValue("EndDate", "2005-07-30"); 

        tVData.add(mGlobalInput);
        tVData.add("1");
        tVData.add(mTransferData);

        try
        {
            AuditEngineUI tReportEngineUI = new AuditEngineUI();

            if (!tReportEngineUI.submitData(tVData,
                                                "" + "||" + "" +
                                                "||   AND ItemType In ('&','A1','A2','A3') order by ItemNum"))
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

	/**
	 * jbInit
	 * @throws Exception {
	 */
    private void jbInit() throws Exception {
    }
}

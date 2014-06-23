/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;


import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;

/**
 * <p>Title: Web业务系统承保个人单状态查询部分</p>
 * <p>Description:接口功能类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author WHN
 * @version 1.0
 */
public class CircReportApplyUI
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往后面传输数据的容器 */
    private VData mInputData = new VData();
    /** 往界面传输数据的容器 */
    private VData mResult = new VData();
    /** 数据操作字符串 */
    private String mOperate;

    public CircReportApplyUI()
    {}

// @Main
    public static void main(String[] args)
    {
        GlobalInput tG = new GlobalInput();
        tG.Operator = "001";
        tG.ManageCom = "86";
        CircReportApplyUI tCircReportApplyUI = new CircReportApplyUI();
        TransferData tTransferData = new TransferData();

        tTransferData.setNameAndValue("StatYear", "2004");
        tTransferData.setNameAndValue("StatMonth", "01");

        VData tVData = new VData();
        tVData.add(tTransferData);
        tVData.add(tG);
        CircReportApplyUI ui = new CircReportApplyUI();
        if (ui.submitData(tVData, "") == true)
        {
            System.out.println("---ok---");
        }
        else
        {
            System.out.println("---NO---" + ui.mErrors.getError(0).errorMessage);
        }

        CErrors tError = ui.mErrors;
        int n = tError.getErrorCount();

    }

    /**
       传输数据的公共方法
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //将操作数据拷贝到本类中
        this.mOperate = cOperate;

        CircReportApplyBL tCircReportApplyBL = new CircReportApplyBL();

        System.out.println("---CircReportApplyBL BEGIN---");
        if (tCircReportApplyBL.submitData(cInputData, mOperate) == false)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tCircReportApplyBL.mErrors);
//      CError tError = new CError();
//      tError.moduleName = "PolStatusChkUI";
//      tError.functionName = "submitData";
//      //tError.errorMessage = "数据查询失败!";
//      this.mErrors .addOneError(tError) ;
            mResult.clear();
            return false;
        }
        else
        {
            //this.mErrors.copyAllErrors(tCircReportApplyBL.mErrors);
        }

        return true;
    }

    public VData getResult()
    {
        return mResult;
    }
}

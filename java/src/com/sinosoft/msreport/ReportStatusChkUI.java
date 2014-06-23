/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

/**
 * <p>Title: Web业务系统报表状态查询部分</p>
 * <p>Description:接口功能类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author WHN
 * @version 1.0
 */
public class ReportStatusChkUI
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往后面传输数据的容器 */
//    private VData mInputData = new VData();
    /** 往界面传输数据的容器 */
    private VData mResult = new VData();
    /** 数据操作字符串 */
    private String mOperate;

    public ReportStatusChkUI()
    {}

// @Main
    public static void main(String[] args)
    {
//        GlobalInput tG = new GlobalInput();
//        tG.Operator = "001";
//        tG.ManageCom = "86";
//        TransferData tTransferData = new TransferData();
//        tTransferData.setNameAndValue("StatYear", "2004");
//        tTransferData.setNameAndValue("StatMon", "06");
//        VData tVData = new VData();
//        tVData.add(tTransferData);
//        tVData.add(tG);
//        ReportStatusChkUI ui = new ReportStatusChkUI();
//        if (ui.submitData(tVData, "dd") == true)
//        {
//            System.out.println("---ok---");
//        }
//        else
//        {
//            System.out.println("---NO---");
//        }
//        VData tVData2 = new VData();
//        tVData2 = ui.getResult();
//        int i = tVData2.size();
    }

    /**
       传输数据的公共方法
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //将操作数据拷贝到本类中
        this.mOperate = cOperate;

        ReportStatusChkBL tReportStatusChkBL = new ReportStatusChkBL();

        System.out.println("---ReportStatusChkUI BEGIN---");
        if (tReportStatusChkBL.submitData(cInputData, mOperate) == false)
        {
            mResult.clear();
            return false;
        }
        else
        {
            mResult = tReportStatusChkBL.getResult();
            this.mErrors.copyAllErrors(tReportStatusChkBL.mErrors);
        }

        return true;
    }

    public VData getResult()
    {
        return mResult;
    }
}

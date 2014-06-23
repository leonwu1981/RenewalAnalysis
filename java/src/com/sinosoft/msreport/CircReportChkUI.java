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
public class CircReportChkUI
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往后面传输数据的容器 */
    private VData mInputData = new VData();
    /** 往界面传输数据的容器 */
    private VData mResult = new VData();
    /** 数据操作字符串 */
    private String mOperate;

    public CircReportChkUI()
    {}

// @Main
    public static void main(String[] args)
    {
        GlobalInput tG = new GlobalInput();
        tG.Operator = "001";
        tG.ManageCom = "86";
        tG.ComCode = "86";
        TransferData tTransferData = new TransferData();
        tTransferData.setNameAndValue("StatYear", "2004");
        tTransferData.setNameAndValue("StatMon", "6");
        tTransferData.setNameAndValue("RepType", "4");
        tTransferData.setNameAndValue("ComCodeISC", "-1");

        VData tVData = new VData();
        tVData.add(tTransferData);
        tVData.add(tG);
        CircReportChkUI ui = new CircReportChkUI();
        if (ui.submitData(tVData, "") == true)
        {
            System.out.println("---ok---");
        }
        else
        {
            System.out.println("---NO---");
        }

        CErrors tError = ui.mErrors;
        int n = tError.getErrorCount();

        String Content = "TEST!";
        if (n > 0)
        {
            Content = Content.trim() + "有未通过自动核保保单原因是:";
            for (int i = 0; i < n; i++)
            {
                //tError = tErrors.getError(i);
                Content = Content.trim() + i + ". " +
                          tError.getError(i).errorMessage.trim() + ".";
            }
        }

    }

    /**
       传输数据的公共方法
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //将操作数据拷贝到本类中
        this.mOperate = cOperate;

        CircReportChkBL tCircAutoBatchChkBL = new CircReportChkBL();

        System.out.println("---CircAutoChkUI BEGIN---");
        if (tCircAutoBatchChkBL.submitData(cInputData, mOperate) == false)
        {
            mResult.clear();
            return false;
        }
        else
        {
            mResult = tCircAutoBatchChkBL.getResult();
            this.mErrors.copyAllErrors(tCircAutoBatchChkBL.mErrors);
        }

        return true;
    }

    public VData getResult()
    {
        return mResult;
    }
}

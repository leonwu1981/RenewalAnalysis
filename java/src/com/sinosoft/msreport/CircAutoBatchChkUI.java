/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;


import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

/**
 * <p>Title: Web业务系统承保个人单状态查询部分</p>
 * <p>Description:接口功能类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author WHN
 * @version 1.0
 */
public class CircAutoBatchChkUI
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往后面传输数据的容器 */
//    private VData mInputData = new VData();
    /** 往界面传输数据的容器 */
    private VData mResult = new VData();
    /** 数据操作字符串 */
    private String mOperate;

    public CircAutoBatchChkUI()
    {}

    /**
     * 传输数据的公共方法
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //将操作数据拷贝到本类中
        this.mOperate = cOperate;

        CircAutoBatchChkBL tCircAutoBatchChkBL = new CircAutoBatchChkBL();

        System.out.println("---CircAutoChkUI BEGIN---");
        if (tCircAutoBatchChkBL.submitData(cInputData, mOperate))
        {
            mResult = tCircAutoBatchChkBL.getResult();
            this.mErrors.copyAllErrors(tCircAutoBatchChkBL.mErrors);
        }
        else
        {
            mResult.clear();
            return false;
        }

        return true;
    }

    public VData getResult()
    {
        return mResult;
    }

    /**
     * 测试函数
     * @param args String[]
     */
    public static void main(String[] args)
    {
//        GlobalInput tG = new GlobalInput();
//        tG.Operator = "001";
//        tG.ManageCom = "86";
//        tG.ComCode = "86";
//        String tSQL =
//                "select * from LFXMLColl where itemcode='5' order by itemcode ";
//        String tCountSQL = "select count(*) from LFXMLColl where itemcode='5' ";
//        VData tVData = new VData();
//        tVData.add(tCountSQL);
//        tVData.add(tSQL);
//        tVData.add(tG);
//        CircAutoBatchChkUI ui = new CircAutoBatchChkUI();
//        if (ui.submitData(tVData, ""))
//        {
//            System.out.println("---ok---");
//        }
//        else
//        {
//            System.out.println("---NO---");
//        }
//
//        CErrors tError = ui.mErrors;
//        int n = tError.getErrorCount();
//
//        String Content = "TEST!";
//        if (n > 0)
//        {
//            Content = Content.trim() + "有未通过自动核保保单原因是:";
//            for (int i = 0; i < n; i++)
//            {
//                //tError = tErrors.getError(i);
//                Content = Content.trim() + i + ". " +
//                          tError.getError(i).errorMessage.trim() + ".";
//            }
//        }
    }
}

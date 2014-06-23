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
public class CircAutoChkUI
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往后面传输数据的容器 */
//    private VData mInputData = new VData();
    /** 往界面传输数据的容器 */
    private VData mResult = new VData();
    /** 数据操作字符串 */
    private String mOperate;

    public CircAutoChkUI()
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

        CircAutoChkBL tCircAutoChkBL = new CircAutoChkBL();

        System.out.println("---CircAutoChkUI BEGIN---");
        if (tCircAutoChkBL.submitData(cInputData, mOperate))
        {
            mResult = tCircAutoChkBL.getResult();
            this.mErrors.copyAllErrors(tCircAutoChkBL.mErrors);
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
//        LFXMLCollSchema mLFXMLCollSchema = new LFXMLCollSchema();
//        mLFXMLCollSchema.setItemCode("5");
//        mLFXMLCollSchema.setComCodeISC("100002");
//        mLFXMLCollSchema.setRepType("1");
//        mLFXMLCollSchema.setStatYear("2004");
//        mLFXMLCollSchema.setStatMon("6");
//
//        VData tVData = new VData();
//        tVData.add(mLFXMLCollSchema);
//        tVData.add(tG);
//        CircAutoChkUI ui = new CircAutoChkUI();
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
    }
}

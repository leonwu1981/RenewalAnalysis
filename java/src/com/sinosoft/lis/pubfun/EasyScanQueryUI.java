/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

/**
 * <p>Title: 扫描件处理类</p>
 * <p>Description: UI功能类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author Minim
 * @version 1.0
 * @date 2002-11-06
 */

public class EasyScanQueryUI
{
    /** 传出数据的容器 */
    private VData mResult = new VData();
    /** 错误处理类 */
    public CErrors mErrors = new CErrors();

    public EasyScanQueryUI()
    {
    }

    /**
     * 数据提交的公共方法，提交成功后将返回结果保存入内部VData对象中
     * @param cInputData 传入的数据,VData对象
     * @param cOperate 数据操作字符串，主要包括"QUERY||MAIN"和"QUERY||DETAIL"
     * @return 布尔值（true--提交成功, false--提交失败）
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
//        System.out.println("---EasyScanQuery BL BEGIN---");
        EasyScanQueryBL tEasyScanQueryBL = new EasyScanQueryBL();
        if (tEasyScanQueryBL.submitData(cInputData, cOperate))
        {
            mResult = tEasyScanQueryBL.getResult();
        }
        else
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tEasyScanQueryBL.mErrors);
            mResult.clear();
            return false;
        }
//        System.out.println("---EasyScanQuery BL END---");
        return true;
    }

    /**
     * 数据输出方法，供外界获取数据处理结果
     * @return 包含有数据查询结果字符串的VData对象
     */
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
        EasyScanQueryUI tEasyScanQueryUI = new EasyScanQueryUI();
        VData tVData = new VData();
        VData tVData1 = new VData();
//      //docid test
//      tVData.add("532");
//      tEasyScanQueryUI.submitData(tVData, "QUERY||0");
        tVData.add("80000000000002");
        tVData.add("11");
        tEasyScanQueryUI.submitData(tVData, "QUERY||1");
//      tVData.add("532");
//      tEasyScanQueryUI.submitData(tVData, "QUERY||2");
    }
}

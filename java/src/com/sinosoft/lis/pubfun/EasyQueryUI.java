/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

//import com.sinosoft.lis.schema.LAAgentSchema;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;


/**
 * <p> Title: Web业务系统 </p>
 * <p> Description: UI功能类 </p>
 * <p>Copyright: Copyright (c) 2002 </p>
 * <p>Company: Sinosoft </p>
 * @author HST
 * @version 1.0
 * @date 2002-09-03
 */
public class EasyQueryUI
{
    /** 传入数据的容器 */
//    private VData mInputData = new VData();


    /** 传出数据的容器 */
    private VData mResult = new VData();


    /** 数据操作字符串 */
    private String mOperate;


    /** 错误处理类 */
    public CErrors mErrors = new CErrors();


    // @Constructor
    public EasyQueryUI()
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
        // 数据操作字符串拷贝到本类中
        this.mOperate = cOperate;

        EasyQueryBL tEasyQueryBL = new EasyQueryBL();

        //System.out.println("\n---EasyQuery BL BEGIN---");
        if (tEasyQueryBL.submitData(cInputData, mOperate))
        {
            mResult = tEasyQueryBL.getResult();
        }
        else
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tEasyQueryBL.mErrors);
            mResult.clear();
            return false;
        }
        //System.out.println("---EasyQuery BL END---\n");

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
//        String sql = "select ComCode,Name,Address,Phone,SatrapName from LDCom order by ComCode";
//        Integer start = new Integer(1);
//        String tSql = "0";
//        VData tVData = new VData();
//        VData tVData1 = new VData();
//        tVData.add(sql);
//        tVData.add(start);
//        tVData.add(tSql);
//        EasyQueryUI t = new EasyQueryUI();
//        t.submitData(tVData, "QUERY||MAIN");
//        tVData1 = t.getResult();
//        String result = "";
//        result = (String) tVData1.getObject(0);
//        System.out.println("testResult:" + result);
    }
}

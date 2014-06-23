/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;

/**
 * <p>Title: Web业务系统</p>
 * <p>Description: 锁表类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author Minim
 * @version 1.0
 */
public class LockTableUI
{
    /** 传入数据的容器 */
    private VData mInputData = new VData();
    /** 传出数据的容器 */
    private VData mResult = new VData();
    /** 数据操作字符串 */
    private String mOperate;
    /** 错误处理类 */
    public CErrors mErrors = new CErrors();

    public LockTableUI()
    {
    }

    /**
     * 数据提交的公共方法，提交成功后将返回结果保存入内部VData对象中
     * @param cInputData 传入的数据,VData对象
     * @param cOperate 数据操作字符串，主要包括"INSERT"
     * @return 布尔值（true--提交成功, false--提交失败）
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        // 数据操作字符串拷贝到本类中
        this.mInputData = (VData) cInputData.clone();
        this.mOperate = cOperate;

        System.out.println("---LockTable BL BEGIN---");
        LockTableBL tLockTableBL = new LockTableBL();

        if (tLockTableBL.submitData(cInputData, cOperate) == false)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLockTableBL.mErrors);
            mResult.clear();
            mResult.add(mErrors.getFirstError());
            return false;
        }
        else
        {
            mResult = tLockTableBL.getResult();
        }
        System.out.println("---LockTable BL END---");

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
     * 主函数，测试用
     * @param args String[]
     */
    public static void main(String[] args)
    {
        LockTableUI LockTableUI1 = new LockTableUI();
        GlobalInput tGlobalInput = new GlobalInput();
        tGlobalInput.Operator = "001";
        LDSysTraceSchema tLDSysTraceSchema = new LDSysTraceSchema();
        tLDSysTraceSchema.setPolNo("98765423");
        tLDSysTraceSchema.setCreatePos("承保录单");
        tLDSysTraceSchema.setPolState("1002");
        LDSysTraceSet inLDSysTraceSet = new LDSysTraceSet();
        inLDSysTraceSet.add(tLDSysTraceSchema);
        VData tVData = new VData();
        tVData.add(tGlobalInput);
        tVData.add(inLDSysTraceSet);
        if (!LockTableUI1.submitData(tVData, "INSERT"))
        {
            VData rVData = LockTableUI1.getResult();
            System.out.println("Submit Failed! " + (String) rVData.get(0));
        }
        else
        {
            System.out.println("Submit Succed!");
        }
    }
}

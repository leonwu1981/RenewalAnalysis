/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author lh
 * @version 1.0
 */

public class ColDataUI
{
    /** 往后面传输数据的容器 */
    private VData mInputData;
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    private VData mResult = new VData();

    public ColDataUI()
    {
    }

    /**
     传输数据的公共方法
     */
    public boolean submitData(VData cInputData)
    {
        //首先将数据在本类中做一个备份
        mInputData = (VData) cInputData.clone();
        //得到外部传入的数据,将数据备份到本类中
        if (!getInputData(cInputData))
        {
            return false;
        }
        //进行业务处理
        if (!dealData())
        {
            return false;
        }
        //准备往后台的数据
        if (!prepareOutputData())
        {
            return false;
        }
        ColDataBL tColDataBL = new ColDataBL();
        System.out.println("Start ColData UI Submit...");
        tColDataBL.submitData(mInputData);
        System.out.println("End ColData UI Submit...");
        //如果有需要处理的错误，则返回
        if (tColDataBL.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tColDataBL.mErrors);
            CError tError = new CError();
            tError.moduleName = "ColDataUI";
            tError.functionName = "submitData";
            tError.errorMessage = "数据提交失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mInputData = null;
        return true;
    }

    /**
     * 准备往后层输出所需要的数据
     * 输出：如果准备数据时发生错误则返回false,否则返回true
     */
    private boolean prepareOutputData()
    {
        try
        {
        }
        catch (Exception ex)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "LDColDataUI";
            tError.functionName = "prepareData";
            tError.errorMessage = "在准备往后层处理所需要的数据时出错。";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * 根据前面的输入数据，进行UI逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     */
    private boolean dealData()
    {
        boolean tReturn = false;
        //此处增加一些校验代码
        tReturn = true;
        return tReturn;
    }

    /**
     * 从输入数据中得到所有对象
     *输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean getInputData(VData cInputData)
    {
        return true;
    }

    public VData getResult()
    {
        return this.mResult;
    }

    public static void main(String[] args)
    {
        ColDataUI ColDataUI1 = new ColDataUI();
    }
}

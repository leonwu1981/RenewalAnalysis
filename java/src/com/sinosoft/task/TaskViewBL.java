package com.sinosoft.task;

import com.sinosoft.utility.*;
import com.sinosoft.lis.bl.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;

/**
 * <p>Title: 工单管理系统</p>
 * <p>Description: 工单查看BL层业务逻辑处理类 </p>
 * <p>Copyright: Copyright (c) 2005 </p>
 * <p>Company: Sinosoft </p>
 * @author QiuYang
 * @version 1.0
 * @date 2005-01-19
 */

public class TaskViewBL
{
    /** 错误处理类 */
    public CErrors mErrors = new CErrors();

    /** 输入数据的容器 */
    private VData mInputData = new VData();

    /** 输出数据的容器 */
    private VData mResult = new VData();

    /** 数据操作字符串 */
    private String mOperate;

    private LGWorkSchema mLGWorkSchema = new LGWorkSchema();

    private MMap map = new MMap();

    /** 统一更新日期 */
    private String mCurrentDate = PubFun.getCurrentDate();

    /** 统一更新时间 */
    private String mCurrentTime = PubFun.getCurrentTime();

    public TaskViewBL()
    {
    }

    /**
     * 数据提交的公共方法
     * @param: cInputData 传入的数据
     * @param: cOperate   数据操作字符串
     * @return: boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        // 将传入的数据拷贝到本类中
        mInputData = (VData) cInputData.clone();
        this.mOperate = cOperate;
        System.out.println("now in TaskViewBL submit");
        // 将外部传入的数据分解到本类的属性中，准备处理
        if (this.getInputData() == false)
        {
            return false;
        }
        System.out.println("---getInputData---");

        // 根据业务逻辑对数据进行处理
        if (this.dealData() == false)
        {
            return false;
        }
        System.out.println("---dealDate---");

        // 装配处理好的数据，准备给后台进行保存
        this.prepareOutputData();
        System.out.println("---prepareOutputData---");

        PubSubmit tPubSubmit = new PubSubmit();
        System.out.println("Start tPRnewManualDunBLS Submit...");

        if (!tPubSubmit.submitData(mInputData, mOperate))
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tPubSubmit.mErrors);

            CError tError = new CError();
            tError.moduleName = "TaskViewBL";
            tError.functionName = "submitData";
            tError.errorMessage = "数据提交失败!";

            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * 将外部传入的数据分解到本类的属性中
     * @param: 无
     * @return: boolean
     */
    private boolean getInputData()
    {
        mLGWorkSchema.setSchema((LGWorkSchema) mInputData.
                getObjectByObjectName("LGWorkSchema", 0));
        return true;
    }

    /**
     * 校验传入的数据
     * @param: 无
     * @return: boolean
     */
    private boolean checkData()
    {
        return true;
    }

    /**
     * 根据业务逻辑对数据进行处理
     * @param: 无
     * @return: boolean
     */
    private boolean dealData()
    {
        //得到工单号和受理号,工单号和受理号相同

        return true;
    }

    /**
     * 根据业务逻辑对数据进行处理
     * @param: 无
     * @return: void
     */
    private void prepareOutputData()
    {
        mInputData.clear();
        mInputData.add(map);
        mResult.clear();
        mResult.add(mLGWorkSchema);
    }

    /**
     * 得到处理后的结果集
     * @return 结果集
     */
    public VData getResult()
    {
        return mResult;
    }
}

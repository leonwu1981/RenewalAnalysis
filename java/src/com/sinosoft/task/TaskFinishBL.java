package com.sinosoft.task;

import com.sinosoft.utility.*;
import com.sinosoft.lis.bl.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;

/**
 * <p>Title: 工单管理系统</p>
 * <p>Description: 工单转交BL层业务逻辑处理类 </p>
 * <p>Copyright: Copyright (c) 2005 </p>
 * <p>Company: Sinosoft </p>
 * @author QiuYang
 * @version 1.0
 * @date 2005-01-20
 */

public class TaskFinishBL
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

    private LGWorkRemarkSchema mLGWorkRemarkSchema = new LGWorkRemarkSchema();

    private MMap map = new MMap();

    /** 全局参数 */
    private GlobalInput mGlobalInput = new GlobalInput();

    /** 统一更新日期 */
    private String mCurrentDate = PubFun.getCurrentDate();

    /** 统一更新时间 */
    private String mCurrentTime = PubFun.getCurrentTime();

    public TaskFinishBL()
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
        System.out.println("now in TaskDeliverBL submit");
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
            tError.moduleName = "TaskDeliverBL";
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
        mGlobalInput.setSchema((GlobalInput) mInputData.
                getObjectByObjectName("GlobalInput", 0));
        mLGWorkSchema.setSchema((LGWorkSchema) mInputData.
                getObjectByObjectName("LGWorkSchema", 0));
        mLGWorkRemarkSchema.setSchema((LGWorkRemarkSchema) mInputData.
                getObjectByObjectName("LGWorkRemarkSchema", 0));
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
        String sql;
        SSRS tSSRS = new SSRS();
        ExeSQL tExeSQL = new ExeSQL();
        String tWorkNo;
        String tNodeNo;
        String tRemarkNo;

        //得到作业当前结点
        tWorkNo = mLGWorkSchema.getWorkNo();
        sql = "Select NodeNo " +
              "From   LGWork " +
              "Where  WorkNo = '" + tWorkNo + "'";
        tSSRS = tExeSQL.execSQL(sql);
        tNodeNo = tSSRS.GetText(1, 1);

        //得到批注编号
        sql = "Select Case When max(to_number(RemarkNo)) Is Null " +
              "       Then 0 Else max(to_number(RemarkNo))+1 End " +
              "From   LGWorkTrace " +
              "Where  WorkNo = '" + tWorkNo + "'" +
              "And    NodeNo = '" + tNodeNo + "'";
        tSSRS = tExeSQL.execSQL(sql);
        tRemarkNo = tSSRS.GetText(1, 1);

        //设置数据
        mLGWorkSchema.setStatusNo("5");   //设为结案状态
        mLGWorkSchema.setOperator(mGlobalInput.Operator);
        mLGWorkSchema.setModifyDate(mCurrentDate);
        mLGWorkSchema.setModifyTime(mCurrentTime);
        map.put(mLGWorkSchema, "UPDATE");  //修改

        mLGWorkRemarkSchema.setWorkNo(tWorkNo);
        mLGWorkRemarkSchema.setNodeNo(tNodeNo);
        mLGWorkRemarkSchema.setRemarkNo(tRemarkNo);
        mLGWorkRemarkSchema.setRemarkTypeNo("1");  //批注类型为结案
        mLGWorkRemarkSchema.setRemarkDate(mCurrentDate);
        mLGWorkRemarkSchema.setRemarkTime(mCurrentTime);
        mLGWorkRemarkSchema.setOperator(mGlobalInput.Operator);
        mLGWorkRemarkSchema.setMakeDate(mCurrentDate);
        mLGWorkRemarkSchema.setMakeTime(mCurrentTime);
        mLGWorkRemarkSchema.setModifyDate(mCurrentDate);
        mLGWorkRemarkSchema.setModifyTime(mCurrentTime);
        map.put(mLGWorkRemarkSchema, "INSERT");  //插入

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

package com.sinosoft.task;

import com.sinosoft.utility.*;
import com.sinosoft.lis.bl.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;

/**
 * <p>Title: 工单管理系统</p>
 * <p>Description: 工单收回BL层业务逻辑处理类 </p>
 * <p>Copyright: Copyright (c) 2005 </p>
 * <p>Company: Sinosoft </p>
 * @author QiuYang
 * @version 1.0
 * @date 2005-01-20
 */

public class TaskBackBL
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

    private LGWorkTraceSchema mLGWorkTraceSchema = new LGWorkTraceSchema();

    private MMap map = new MMap();

    /** 全局参数 */
    private GlobalInput mGlobalInput = new GlobalInput();

    /** 统一更新日期 */
    private String mCurrentDate = PubFun.getCurrentDate();

    /** 统一更新时间 */
    private String mCurrentTime = PubFun.getCurrentTime();

    public TaskBackBL()
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
        System.out.println("now in TaskBackBL submit");
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
        System.out.println("Start TaskBackBL Submit...");

        if (!tPubSubmit.submitData(mInputData, mOperate))
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tPubSubmit.mErrors);

            CError tError = new CError();
            tError.moduleName = "TaskBackBL";
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
        String tWorkBoxNo;

        //得到新的作业结点
        tWorkNo = mLGWorkSchema.getWorkNo();
        sql = "Select Case When max(to_number(NodeNo)) Is Null " +
              "       Then 0 Else max(to_number(NodeNo))+1 End " +
              "From   LGWorkTrace " +
              "Where  WorkNo = '" + tWorkNo + "'";
        tSSRS = tExeSQL.execSQL(sql);
        tNodeNo = tSSRS.GetText(1, 1);

        //得到用户信箱号
        sql = "select WorkBoxNo from LGWorkBox " +
              "where  OwnerTypeNo = '2' " + //个人信箱
              "and    OwnerNo = '" + mGlobalInput.Operator + "'";
        tSSRS = tExeSQL.execSQL(sql);
        tWorkBoxNo = tSSRS.GetText(1, 1);

        //设置数据
        sql = "Update LGWork set " +
              "NodeNo = '" + tNodeNo + "', " +    //指向新结点
              "Operator = '" + mGlobalInput.Operator + "', " +
              "ModifyDate = '" + mCurrentDate + "', " +
              "ModifyTime = '" + mCurrentTime + "' " +
              "Where  WorkNo = '" + tWorkNo + "' ";
        map.put(sql, "UPDATE");  //修改

        mLGWorkTraceSchema.setWorkNo(tWorkNo);
        mLGWorkTraceSchema.setNodeNo(tNodeNo);
        mLGWorkTraceSchema.setWorkBoxNo(tWorkBoxNo); //信箱编号
        mLGWorkTraceSchema.setInMethodNo("3");       //收回
        mLGWorkTraceSchema.setInDate(mCurrentDate);
        mLGWorkTraceSchema.setInTime(mCurrentTime);
        mLGWorkTraceSchema.setSendComNo(mGlobalInput.ManageCom);
        mLGWorkTraceSchema.setSendPersonNo(mGlobalInput.Operator);
        mLGWorkTraceSchema.setOperator(mGlobalInput.Operator);
        mLGWorkTraceSchema.setMakeDate(mCurrentDate);
        mLGWorkTraceSchema.setMakeTime(mCurrentTime);
        mLGWorkTraceSchema.setModifyDate(mCurrentDate);
        mLGWorkTraceSchema.setModifyTime(mCurrentTime);
        map.put(mLGWorkTraceSchema, "INSERT");  //插入

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

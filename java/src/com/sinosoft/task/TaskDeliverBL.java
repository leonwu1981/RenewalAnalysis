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

public class TaskDeliverBL
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

    private LGWorkRemarkSchema mLGWorkRemarkSchema = new LGWorkRemarkSchema();

    private MMap map = new MMap();

    private String[] mWorkNo = null;

    private String mDeliverType;

    private String mCopyFlag;

    /** 全局参数 */
    private GlobalInput mGlobalInput = new GlobalInput();

    /** 统一更新日期 */
    private String mCurrentDate = PubFun.getCurrentDate();

    /** 统一更新时间 */
    private String mCurrentTime = PubFun.getCurrentTime();

    public TaskDeliverBL()
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
        mWorkNo = (String[]) mInputData.getObject(1);
        mCopyFlag = (String) mInputData.getObject(2);
        mDeliverType = (String) mInputData.getObject(3);
        mLGWorkSchema.setSchema((LGWorkSchema) mInputData.
                getObjectByObjectName("LGWorkSchema", 0));
        mLGWorkTraceSchema.setSchema((LGWorkTraceSchema) mInputData.
                getObjectByObjectName("LGWorkTraceSchema", 0));
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
        String tRemarkTypeNo;
        String tTypeNo;
        String tWorkBoxNo;
        String tRemarkContent;
        tTypeNo = mLGWorkSchema.getTypeNo();
        tWorkBoxNo = mLGWorkTraceSchema.getWorkBoxNo();
        tRemarkContent = mLGWorkRemarkSchema.getRemarkContent();

        for (int i = 0; i < mWorkNo.length; i++)
        {
            tWorkNo = mWorkNo[i];
            LGWorkSchema tLGWorkSchema = new LGWorkSchema();
            LGWorkTraceSchema tLGWorkTraceSchema = new LGWorkTraceSchema();
            LGWorkRemarkSchema tLGWorkRemarkSchema = new LGWorkRemarkSchema();
            if ((mCopyFlag != null) && (mCopyFlag.equals("Y"))) //复制
            {
                sql = "Select * From LGWork " +
                      "Where WorkNo = '" + tWorkNo + "' ";
                LGWorkDB tLGWorkDB = new LGWorkDB();
                tLGWorkDB.setWorkNo(tWorkNo);
                tLGWorkDB.getInfo();
                tLGWorkSchema.setSchema(tLGWorkDB.getSchema());

                //产生作业号
                String tDate = mCurrentDate;
                tDate = tDate.substring(0, 4) + tDate.substring(5, 7) + tDate.substring(8, 10);
                tWorkNo = PubFun1.CreateMaxNo("TASK" + tDate, 6);

                //送审批
                if ((mDeliverType != null) && (mDeliverType.equals("1")))
                {
                    tLGWorkSchema.setStatusNo("4");
                    tRemarkTypeNo = "3";
                }
                else
                {
                    tRemarkTypeNo = "0";
                }
                tLGWorkSchema.setWorkNo(tWorkNo);
                tLGWorkSchema.setNodeNo("0");
                tLGWorkSchema.setTypeNo(tTypeNo);
                tLGWorkSchema.setMakeDate(mCurrentDate);
                tLGWorkSchema.setMakeTime(mCurrentTime);
                tLGWorkSchema.setModifyDate(mCurrentDate);
                tLGWorkSchema.setModifyTime(mCurrentTime);
                tLGWorkSchema.setOperator(mGlobalInput.Operator);
                map.put(tLGWorkSchema, "INSERT"); //插入

                tLGWorkTraceSchema.setWorkNo(tWorkNo);
                tLGWorkTraceSchema.setNodeNo("0"); //起始结点
                tLGWorkTraceSchema.setWorkBoxNo(tWorkBoxNo); //信箱编号
                tLGWorkTraceSchema.setInMethodNo("0"); //新单录入
                tLGWorkTraceSchema.setInDate(mCurrentDate);
                tLGWorkTraceSchema.setInTime(mCurrentTime);
                tLGWorkTraceSchema.setSendComNo("");
                tLGWorkTraceSchema.setSendPersonNo("");
                tLGWorkTraceSchema.setMakeDate(mCurrentDate);
                tLGWorkTraceSchema.setMakeTime(mCurrentTime);
                tLGWorkTraceSchema.setModifyDate(mCurrentDate);
                tLGWorkTraceSchema.setModifyTime(mCurrentTime);
                tLGWorkTraceSchema.setOperator(mGlobalInput.Operator);
                map.put(tLGWorkTraceSchema, "INSERT"); //插入

                tLGWorkRemarkSchema.setWorkNo(tWorkNo);
                tLGWorkRemarkSchema.setNodeNo("0");
                tLGWorkRemarkSchema.setRemarkNo("0");
                tLGWorkRemarkSchema.setRemarkTypeNo(tRemarkTypeNo); //批注类型为转交
                tLGWorkRemarkSchema.setRemarkContent(tRemarkContent);
                tLGWorkRemarkSchema.setRemarkDate(mCurrentDate);
                tLGWorkRemarkSchema.setRemarkTime(mCurrentTime);
                tLGWorkRemarkSchema.setOperator(mGlobalInput.Operator);
                tLGWorkRemarkSchema.setMakeDate(mCurrentDate);
                tLGWorkRemarkSchema.setMakeTime(mCurrentTime);
                tLGWorkRemarkSchema.setModifyDate(mCurrentDate);
                tLGWorkRemarkSchema.setModifyTime(mCurrentTime);
                map.put(tLGWorkRemarkSchema, "INSERT"); //插入
            }
            else //不复制
            {
                //得到新的作业结点
                sql = "Select Case When max(to_number(NodeNo)) Is Null " +
                      "       Then 0 Else max(to_number(NodeNo))+1 End " +
                      "From   LGWorkTrace " +
                      "Where  WorkNo = '" + tWorkNo + "' ";
                tSSRS = tExeSQL.execSQL(sql);
                tNodeNo = tSSRS.GetText(1, 1);

                //得到批注编号
                sql = "Select Case When max(to_number(RemarkNo)) Is Null " +
                      "       Then 0 Else max(to_number(RemarkNo))+1 End " +
                      "From   LGWorkRemark " +
                      "Where  WorkNo = '" + tWorkNo + "' " +
                      "And    NodeNo = '" + tNodeNo + "' ";
                tSSRS = tExeSQL.execSQL(sql);
                tRemarkNo = tSSRS.GetText(1, 1);

                //设置数据
                if ((mDeliverType != null) && (mDeliverType.equals("1")))
                {
                    sql = "Update LGWork set " +
                          "NodeNo = '" + tNodeNo + "', " +
                          "StatusNo = '4', " + //设为审核状态
                          "Operator = '" + mGlobalInput.Operator + "', " +
                          "ModifyDate = '" + mCurrentDate + "', " +
                          "ModifyTime = '" + mCurrentTime + "' " +
                          "Where  WorkNo = '" + tWorkNo + "' ";
                    tRemarkTypeNo = "3"; //审核
                }
                else
                {
                    sql = "Update LGWork set " +
                          "NodeNo = '" + tNodeNo + "', " +
                          "Operator = '" + mGlobalInput.Operator + "', " +
                          "ModifyDate = '" + mCurrentDate + "', " +
                          "ModifyTime = '" + mCurrentTime + "' " +
                          "Where  WorkNo = '" + tWorkNo + "' ";
                    tRemarkTypeNo = "0"; //转交
                }
                map.put(sql, "UPDATE"); //修改

                tLGWorkTraceSchema.setWorkNo(tWorkNo);
                tLGWorkTraceSchema.setNodeNo(tNodeNo);
                tLGWorkTraceSchema.setWorkBoxNo(tWorkBoxNo);
                tLGWorkTraceSchema.setInMethodNo("2"); //接收
                tLGWorkTraceSchema.setInDate(mCurrentDate);
                tLGWorkTraceSchema.setInTime(mCurrentTime);
                tLGWorkTraceSchema.setSendComNo(mGlobalInput.ManageCom);
                tLGWorkTraceSchema.setSendPersonNo(mGlobalInput.Operator);
                tLGWorkTraceSchema.setOperator(mGlobalInput.Operator);
                tLGWorkTraceSchema.setMakeDate(mCurrentDate);
                tLGWorkTraceSchema.setMakeTime(mCurrentTime);
                tLGWorkTraceSchema.setModifyDate(mCurrentDate);
                tLGWorkTraceSchema.setModifyTime(mCurrentTime);
                map.put(tLGWorkTraceSchema, "INSERT"); //插入

                tLGWorkRemarkSchema.setWorkNo(tWorkNo);
                tLGWorkRemarkSchema.setNodeNo(tNodeNo);
                tLGWorkRemarkSchema.setRemarkNo(tRemarkNo);
                tLGWorkRemarkSchema.setRemarkTypeNo(tRemarkTypeNo); //批注类型为转交
                tLGWorkRemarkSchema.setRemarkDate(mCurrentDate);
                tLGWorkRemarkSchema.setRemarkTime(mCurrentTime);
                tLGWorkRemarkSchema.setOperator(mGlobalInput.Operator);
                tLGWorkRemarkSchema.setMakeDate(mCurrentDate);
                tLGWorkRemarkSchema.setMakeTime(mCurrentTime);
                tLGWorkRemarkSchema.setModifyDate(mCurrentDate);
                tLGWorkRemarkSchema.setModifyTime(mCurrentTime);
                map.put(tLGWorkRemarkSchema, "INSERT"); //插入
            }
        }
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

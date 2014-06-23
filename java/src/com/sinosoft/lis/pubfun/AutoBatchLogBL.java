/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import com.sinosoft.lis.db.LCBatchLogDB;
import com.sinosoft.lis.schema.LCBatchLogSchema;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

/**
 * <p>Title: 批处理日志处理</p>
 * <p>Description: 记录批处理的执行情况</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: sinosoft</p>
 * @author zhuxf
 * @version 1.0
 */
public class AutoBatchLogBL
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往后面传输数据的容器 */
    private VData mInputData;
    private MMap mMap = new MMap();
    /** 业务处理相关变量 */
    private GlobalInput mGlobalInput = new GlobalInput();
    private LCBatchLogSchema mLCBatchLogSchema = new LCBatchLogSchema();
    private String mOperate;
    private String mSeiralNo;

    public AutoBatchLogBL()
    {
    }

    /**
     * 通用接口函数
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //全局的操作变量
        mOperate = cOperate;

        //得到外部传入的数据,将数据备份到本类中
        if (!getInputData(cInputData))
        {
            return false;
        }

        if (!dealData())
        {
            return false;
        }

        //准备往后台的数据
        if (!prepareOutputData())
        {
            return false;
        }

        PubSubmit tSubmit = new PubSubmit();
        if (tSubmit.submitData(mInputData, ""))
        {
            return true;
        }
        else
        {
            // @@错误处理
            mErrors.copyAllErrors(tSubmit.mErrors);
            CError tError = new CError();
            tError.moduleName = "AutoBatchLogBL";
            tError.functionName = "submitData";
            tError.errorMessage = "数据提交失败！";
            mErrors.addOneError(tError);
            return false;
        }
    }

    /**
     * 获取传入数据函数
     * @param cInputData VData
     * @return boolean
     */
    private boolean getInputData(VData cInputData)
    {
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
        mLCBatchLogSchema.setSchema((LCBatchLogSchema) cInputData.getObjectByObjectName(
                "LCBatchLogSchema", 0));
        return true;
    }

    /**
     * 数据准备函数
     * @return boolean
     */
    public boolean dealData()
    {
        //判定日志执行的类型，是开始操作还是结束操作。
        if (mOperate.equals("BEGIN"))
        {
            try
            {
                mSeiralNo = PubFun1.CreateMaxNo("BATCHLOG", 20);
                mLCBatchLogSchema.setSeiralNo(mSeiralNo);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                System.out.println("生成最大流水号的时候出错...");
                // @@错误处理
                CError tError = new CError();
                tError.moduleName = "AutoBatchLogBL";
                tError.functionName = "dealData";
                tError.errorMessage = "在准备数据时出错。";
                mErrors.addOneError(tError);
                return false;
            }
            mLCBatchLogSchema.setRunDate(PubFun.getCurrentDate());
            mLCBatchLogSchema.setRunTime(PubFun.getCurrentTime());
            mLCBatchLogSchema.setOperator(mGlobalInput.Operator);
            mLCBatchLogSchema.setManageCom(mGlobalInput.ManageCom);
        }
        else
        {
            LCBatchLogDB tLCBatchLogDB = new LCBatchLogDB();
            tLCBatchLogDB.setSeiralNo(mLCBatchLogSchema.getSeiralNo());
            try
            {
                tLCBatchLogDB.getInfo();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                System.out.println("查询日志的时候出错...");
                // @@错误处理
                CError tError = new CError();
                tError.moduleName = "AutoBatchLogBL";
                tError.functionName = "dealData";
                tError.errorMessage = "在准备数据时出错。";
                mErrors.addOneError(tError);
                return false;
            }
            mLCBatchLogSchema.setRunDate(tLCBatchLogDB.getRunDate());
            mLCBatchLogSchema.setRunTime(tLCBatchLogDB.getRunTime());
            mLCBatchLogSchema.setEndDate(PubFun.getCurrentDate());
            mLCBatchLogSchema.setEndTime(PubFun.getCurrentTime());
            mLCBatchLogSchema.setOperator(mGlobalInput.Operator);
            mLCBatchLogSchema.setManageCom(mGlobalInput.ManageCom);
        }
        return true;
    }

    /**
     * 准备传出数据函数
     * @return boolean
     */
    private boolean prepareOutputData()
    {
        try
        {
            //判定日志执行的类型，是开始操作还是结束操作。
            if (mOperate.equals("BEGIN"))
            {
                mMap.put(mLCBatchLogSchema, "INSERT");
            }
            else
            {
                mMap.put(mLCBatchLogSchema, "UPDATE");
            }
            mInputData = new VData();
            mInputData.add(mMap);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "AutoBatchLogBL";
            tError.functionName = "prepareOutputData";
            tError.errorMessage = "在准备往后层处理所需要的数据时出错。";
            mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * 返回序列号信息
     * @return String
     */
    public String getReturnSeiralNo()
    {
        return mSeiralNo;
    }

    public static void main(String[] args)
    {
        GlobalInput tG = new GlobalInput();
        tG.ManageCom = "86010101";
        tG.Operator = "001";

        LCBatchLogSchema tLCBatchLogSchema = new LCBatchLogSchema();
//        tLCBatchLogSchema.setAwakeFlag("N");
//        tLCBatchLogSchema.setAwakeNote("通知成功");
//        tLCBatchLogSchema.setGetNoticeNo("1021010000114788");

        AutoBatchLogBL tAutoBatchLogBL = new AutoBatchLogBL();
        // 准备传输数据 VData
        VData tVData = new VData();
        tVData.add(tLCBatchLogSchema);
        tVData.add(tG);
        tAutoBatchLogBL.submitData(tVData, "BEGIN");
    }
}

/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.circ;


import java.util.Vector;

import com.sinosoft.lis.db.LFDesbModeDB;
import com.sinosoft.lis.db.LFItemRelaDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubCalculator;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.pubfun.ReportPubFun;
import com.sinosoft.lis.schema.LFDesbModeSchema;
import com.sinosoft.lis.vschema.LFDesbModeSet;
import com.sinosoft.msreport.CalService;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: 民生核心业务系统</p>
 * <p>Description: </p>
 * <p>Copyright: SINOSOFT Copyright (c) 2004</p>
 * <p>Company: 中科软科技</p>
 * @author guoxiang
 * @version 1.0
 */
public class XmlReportImportAfterInitService implements AfterInitService
{
    /** 传入数据的容器 */
//    private VData mInputData = new VData();

    /** 传出数据的容器 */
    private VData mResult = new VData();

    /** 数据操作字符串 */
//    private String mOperate;

    /** 错误处理类 */
    public CErrors mErrors = new CErrors();

    /** 业务处理相关变量 */
    private TransferData mTransferData = new TransferData();
    private MMap mmap = new MMap();
//    private String tOperate;

    /** 全局数据 */
    private GlobalInput mGlobalInput = new GlobalInput();

    public XmlReportImportAfterInitService()
    {
    }

    /**
     * 传输数据的公共方法
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
//        mOperate = cOperate;

        //  得到外部传入的数据，将数据备份到本类中
        if (!getInputData(cInputData))
        {
            return false;
        }

        //  处理提数逻辑
        if (!dealData())
        {
            return false;
        }

        //准备公共提交数据
        if (mmap != null && mmap.keySet().size() > 0)
        {
            mResult.add(mmap);
        }

        //  提交到BLS进行插入INSERT 处理
        //        ReportEngineBLS tReportEngineBLS = new ReportEngineBLS();
        //        if (!tReportEngineBLS.submitData(mResult, tOperate))
        //        {
        //            // @@错误处理
        //            this.mErrors.copyAllErrors(tReportEngineBLS.mErrors);
        //            buildError("submitData", "数据插入中间表失败！");
        //            mResult.clear();
        //
        //            return false;
        //        }
        //        else
        //        {
        //            mResult = tReportEngineBLS.getResult();
        //            this.mErrors.copyAllErrors(tReportEngineBLS.mErrors);
        //        }
        return true;
    }


    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     * @return boolean
     */
    private boolean dealData()
    {
        String tWhereSQLName = "WhereSQL";
        String tWhereSQLValue = mTransferData.getValueByName((Object)
                tWhereSQLName).toString();

        String[] KeyWord = PubFun.split(tWhereSQLValue, "||");

        // 查找计算处理的记录
        String strSQL = "SELECT * FROM LFDesbMode where 1=1"
                        + ReportPubFun.getWherePart("ItemCode", KeyWord[0])
                        + ReportPubFun.getWherePart("ItemNum", KeyWord[1])
                        + " " + KeyWord[2];
        System.out.println("通过前台的条件查询描述表:" + strSQL);

        LFDesbModeSet tLFDesbModeSet = new LFDesbModeDB().executeQuery(strSQL);
        if (tLFDesbModeSet.size() == 0)
        {
            buildError("dealData", "查询描述表失败！");

            return false;
        }
        mResult.clear();
        for (int i = 1; i <= tLFDesbModeSet.size(); i++)
        {
            LFDesbModeSchema mLFDesbModeSchema = tLFDesbModeSet.get(i);
            try
            {
                if (!CheckTransitionCondition(mLFDesbModeSchema, mTransferData))
                {
                    return false;
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();

                return false;
            }
        }

        return true;
    }

    /**
     * 校验转移条件是否满足
     * @param tLFDesbModeSchema LFDesbModeSchema
     * @param tTransferData TransferData
     * @return boolean
     * @throws Exception
     */
    private boolean CheckTransitionCondition(LFDesbModeSchema tLFDesbModeSchema,
                                             TransferData tTransferData) throws
            Exception
    {
        if (tLFDesbModeSchema == null)
        {
            // @@错误处理
            buildError("CheckTransitionCondition", "传入的信息为空");

            return false;
        }

        if (tLFDesbModeSchema.getDealType().equals("S"))
        {
            //S-应用SQL语句进行处理
            String insertSQL = "";
            insertSQL = getInsertSQL(tLFDesbModeSchema, tTransferData);

            System.out.println("特殊sql输出" + insertSQL);
            if (!insertSQL.trim().equals(""))
            {
                mmap.put(insertSQL, "insert");
            }
            System.out.println("map:" + mmap);
            System.out.println("size:" + mmap.keySet().size());

            return true;
        }
        else if (tLFDesbModeSchema.getDealType().equals("C"))
        {
            //C -- 应用Class类进行处理
            try
            {
                Class tClass = Class.forName(tLFDesbModeSchema
                                             .getInterfaceClassName());
                CalService tCalService = (CalService) tClass.newInstance();

                // 准备数据
                String strOperate = "";
                VData tInputData = new VData();
                tInputData.add(tTransferData);
                tInputData.add(tLFDesbModeSchema);
                if (!tCalService.submitData(tInputData, strOperate))
                {
                    return false;
                }
                mResult = tCalService.getResult();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();

                return false;
            }
        }

        return true;
    }

    private String getInsertSQL(LFDesbModeSchema tLFDesbModeSchema,
                                TransferData tTransferData)
    {
        PubCalculator tPubCalculator = new PubCalculator();

        //准备计算要素
        Vector tVector = tTransferData.getValueNames();
        String tNeedItemKeyName = "NeedItemKey";
        String tNeedItemKeyValue = tTransferData.getValueByName((Object)
                tNeedItemKeyName).toString();
        System.out.println("tNeedItemKeyName:" + tNeedItemKeyName);
        if (tNeedItemKeyValue.equals("1")) //1-扩充要素
        {
            LFItemRelaDB tLFItemRelaDB = new LFItemRelaDB();
            tLFItemRelaDB.setItemCode(tLFDesbModeSchema.getItemCode());
            if (!tLFItemRelaDB.getInfo())
            {
                buildError("getInsertSQL", "查询内外科目编码对应表失败！");
                System.out.println("getinfo fail:itemcode=" +
                                   tLFItemRelaDB.getItemCode());
                return "0";
            }

            System.out.println("UpItemCode:" + tLFItemRelaDB.getUpItemCode());
            System.out.println("Layer:" + tLFItemRelaDB.getLayer());
            System.out.println("Remark:" + tLFItemRelaDB.getRemark());

            tPubCalculator.addBasicFactor("UpItemCode",
                                          tLFItemRelaDB.getUpItemCode());
            tPubCalculator.addBasicFactor("Layer",
                                          String.valueOf(tLFItemRelaDB.getLayer()));
            tPubCalculator.addBasicFactor("Remark", tLFItemRelaDB.getRemark());
        }

        //0－普通要素
        for (int i = 0; i < tVector.size(); i++)
        {
            String tName = (String) tVector.get(i);
            String tValue = tTransferData.getValueByName((Object) tName).
                            toString();
            tPubCalculator.addBasicFactor(tName, tValue);
        }

        //准备计算SQL
        if ((tLFDesbModeSchema.getCalSQL1() == null)
            || (tLFDesbModeSchema.getCalSQL1().length() == 0))
        {
            tLFDesbModeSchema.setCalSQL1("");
        }
        if ((tLFDesbModeSchema.getCalSQL2() == null)
            || (tLFDesbModeSchema.getCalSQL2().length() == 0))
        {
            tLFDesbModeSchema.setCalSQL2("");
        }
        if ((tLFDesbModeSchema.getCalSQL3() == null)
            || (tLFDesbModeSchema.getCalSQL3().length() == 0))
        {
            tLFDesbModeSchema.setCalSQL3("");
        }

        String Calsql = tLFDesbModeSchema.getCalSQL()
                        + tLFDesbModeSchema.getCalSQL1()
                        + tLFDesbModeSchema.getCalSQL2()
                        + tLFDesbModeSchema.getCalSQL3();
        tPubCalculator.setCalSql(Calsql);

        String strSQL = tPubCalculator.calculateEx();
        System.out.println("从描述表取得的SQL : " + strSQL);

        String insertSQL = "Insert Into ";
        String insertTableName = tLFDesbModeSchema.getDestTableName();
        String stsql = insertSQL + " " + insertTableName + " " + strSQL;
        System.out.println("得到的insert SQL 语句: " + stsql);

        return stsql;
    }

    /**
     * 从输入数据中得到所有对象
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @param cInputData VData
     * @return boolean
     */
    private boolean getInputData(VData cInputData)
    {
        //全局变量
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput",
                0));

        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData",
                0);

        if ((mGlobalInput == null) || (mTransferData == null))
        {
            buildError("getInputData", "没有得到足够的信息！");

            return false;
        }

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

    public TransferData getReturnTransferData()
    {
        return mTransferData;
    }

    public CErrors getErrors()
    {
        return mErrors;
    }

    /*
     * add by kevin, 2002-10-14
     */
    private void buildError(String szFunc, String szErrMsg)
    {
        CError cError = new CError();
        cError.moduleName = "ReportEngineBL";
        cError.functionName = szFunc;
        cError.errorMessage = szErrMsg;
        this.mErrors.addOneError(cError);
    }

    public static void main(String[] args)
    {
    }
}

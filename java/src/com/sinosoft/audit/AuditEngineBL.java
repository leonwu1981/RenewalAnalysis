/**
 * Copyright (c) 2006 sinosoft Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.audit;

import com.sinosoft.lis.bl.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.tb.*;
import com.sinosoft.lis.vbl.*;
import com.sinosoft.lis.vdb.*;
import com.sinosoft.lis.vschema.*;

import com.sinosoft.utility.*;

import java.util.*;

/**
 * <p>
 * ClassName: AuditEngineBL
 * </p>
 * <p>
 * Company: Sinosoft Co. Ltd.
 * </p>
 * @author Alex
 * @version 1.0
 */
public class AuditEngineBL
{
    private VData mInputData = new VData();

    private VData mResult = new VData();

    private String mOperate;

    public CErrors mErrors = new CErrors();

    private TransferData mTransferData = new TransferData();
    private String tOperate;
    private String mNeedItemKey;

    private GlobalInput mGlobalInput = new GlobalInput();

	/**
	 * AuditEngineBL
	 */
    public AuditEngineBL()
    {
    }

	/**
	 * submitData
	 * 通用接口：接收传入数据，并对数据进行处理
	 * @param VData cInputData
	 * @param String cOperate
	 */
    public boolean submitData(VData cInputData, String cOperate)
    {
        mOperate = cOperate;

        if (!getInputData(cInputData))
        {
            return false;
        }

        if (!dealData())
        {
            return false;
        }

        AuditEngineBLS tAuditEngineBLS = new AuditEngineBLS();

        if (!tAuditEngineBLS.submitData(mResult, tOperate))
        {
            this.mErrors.copyAllErrors(tAuditEngineBLS.mErrors);
            buildError("submitData", "数据提数失败！");
            mResult.clear();

            return false;
        }
        else
        {
            mResult = tAuditEngineBLS.getResult();
            this.mErrors.copyAllErrors(tAuditEngineBLS.mErrors);
        }

        return true;
    }

	/**
	 * dealData
	 * 数据准备
	 */
    private boolean dealData()
    {
        String[] KeyWord = PubFun.split(mOperate, "||");

        String strSQL = "SELECT * FROM LFDesbMode where 1=1" +
                        ReportPubFun.getWherePart("ItemCode", KeyWord[0]) +
                        ReportPubFun.getWherePart("ItemNum", KeyWord[1]) + " " +
                        KeyWord[2];
        System.out.println("通过前台的条件查询描述表:" + strSQL);

        LFDesbModeSet tLFDesbModeSet = new LFDesbModeDB().executeQuery(strSQL);
        System.out.println("完成查询sql描述");

        if ((tLFDesbModeSet == null) || (tLFDesbModeSet.size() == 0))
        {
            //buildError("dealData", "查询描述表失败！");
            return true;
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
	 * CheckTransitionCondition
	 * @param LFDesbModeSchema tLFDesbModeSchema
	 */
    private boolean CheckTransitionCondition(LFDesbModeSchema tLFDesbModeSchema,
                                             TransferData tTransferData)
                                      throws Exception
    {
        if (tLFDesbModeSchema == null)
        {
            buildError("CheckTransitionCondition", "传入的信息为空");

            return false;
        }

        if (tLFDesbModeSchema.getDealType().equals("S"))
        {
            String insertSQL = "";
            insertSQL = getInsertSQL(tLFDesbModeSchema, tTransferData);
            //huangkai--PIR20081433--2008-10-09--保监会稽核数据生成时，保费费用表稽核数据导入时见269行，会传回空字符串，在执行更新SQL创建statement时会报错
            if(insertSQL==null||"".equals(insertSQL)){
            	return true;
            }
            MMap map = new MMap();
            map.put(insertSQL, "EXESQL");
            mResult.add(map);

            return true;
        }
        else if (tLFDesbModeSchema.getDealType().equals("C"))
        {
            try
            {
                Class tClass = Class.forName(tLFDesbModeSchema.getInterfaceClassName());
                CalService tCalService = (CalService) tClass.newInstance();

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

	/**
	 * getInsertSQL
	 * @param LFDesbModeSchema tLFDesbModeSchema
	 */
    private String getInsertSQL(LFDesbModeSchema tLFDesbModeSchema,
                                TransferData tTransferData)
    {
        PubCalculator tPubCalculator = new PubCalculator();

        Vector tVector = (Vector) tTransferData.getValueNames();
        String tNeedItemKeyName = "NeedItemKey";
        String tNeedItemKeyValue = (String) tTransferData.getValueByName((Object) tNeedItemKeyName)
                                                         .toString();

        if (tNeedItemKeyName.equals("1")) //1-扩充要素
        {
            LFItemRelaDB tLFItemRelaDB = new LFItemRelaDB();
            tLFItemRelaDB.setItemCode(tLFDesbModeSchema.getItemCode());

            if (!tLFItemRelaDB.getInfo())
            {
                buildError("getInsertSQL", "查询内外科目编码对应表失败！");

                return "0";
            }

            tPubCalculator.addBasicFactor("UpItemCode",
                                          tLFItemRelaDB.getUpItemCode());
            tPubCalculator.addBasicFactor("Layer",
                                          String.valueOf(tLFItemRelaDB.getLayer()));
            tPubCalculator.addBasicFactor("Remark", tLFItemRelaDB.getRemark());
        }

        for (int i = 0; i < tVector.size(); i++)
        {
            String tName = (String) tVector.get(i);
            String tValue = (String) tTransferData.getValueByName((Object) tName)
                                                  .toString();
            tPubCalculator.addBasicFactor(tName, tValue);
        }

        System.out.println("准备计算sql！");

        if ((tLFDesbModeSchema.getCalSQL1() == null) ||
                (tLFDesbModeSchema.getCalSQL1().length() == 0))
        {
            tLFDesbModeSchema.setCalSQL1("");
        }

        if ((tLFDesbModeSchema.getCalSQL2() == null) ||
                (tLFDesbModeSchema.getCalSQL2().length() == 0))
        {
            tLFDesbModeSchema.setCalSQL2("");
        }

        if ((tLFDesbModeSchema.getCalSQL3() == null) ||
                (tLFDesbModeSchema.getCalSQL3().length() == 0))
        {
            tLFDesbModeSchema.setCalSQL3("");
        }

        String Calsql = tLFDesbModeSchema.getCalSQL() +
                        tLFDesbModeSchema.getCalSQL1() +
                        tLFDesbModeSchema.getCalSQL2() +
                        tLFDesbModeSchema.getCalSQL3();
        tPubCalculator.setCalSql(Calsql);
        

        String strSQL = tPubCalculator.calculateEx();
        System.out.println("从描述表取得的SQL : " + strSQL);

        String stsql = "";
        //保费表目前是根据function跑的不需要insert 语句
        if("au_prem_info".equals(tLFDesbModeSchema.getDestTableName().trim())  //Amended By Fang 20080315
        		||"au_prem_temp".equals(tLFDesbModeSchema.getDestTableName().trim())
        		||"au_bill_info".equals(tLFDesbModeSchema.getDestTableName().trim())){
        	
        	ExeSQL tExeSQL = new ExeSQL();
        	SSRS tSSRS = tExeSQL.execSQL(strSQL);
        	if(tSSRS.getMaxRow() > 0){
        		System.out.println(tLFDesbModeSchema.getDestTableName().trim()+"表插入行数："+tSSRS.GetText(1, 1));
        	}
        }else{
        
	        String insertSQL = "Insert Into ";
	        String insertTableName = tLFDesbModeSchema.getDestTableName();
	        stsql = insertSQL + " " + insertTableName + " " + strSQL;
	        System.out.println("得到的insert SQL 语句: " + stsql);

        }
        return stsql;
    }

	/**
	 * getInputData
	 * 获取传入数据
	 * @param VData cInputData
	 */
    private boolean getInputData(VData cInputData)
    {
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName("GlobalInput",
                                                                              0));

        mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData",
                                                                        0);

        mNeedItemKey = (String) cInputData.getObjectByObjectName("String", 0);

        if ((mGlobalInput == null) || (mTransferData == null))
        {
            buildError("getInputData", "没有得到足够的信息！");

            return false;
        }

        return true;
    }

	/**
	 * getResult
	 * 数据返回
	 */
    public VData getResult()
    {
        return mResult;
    }

	/**
	 * buildError
	 * 构建错误
	 * @param String szFunc
	 * @param String szErrMsg
	 */
    private void buildError(String szFunc, String szErrMsg)
    {
        CError cError = new CError();
        cError.moduleName = "ReportEngineBL";
        cError.functionName = szFunc;
        cError.errorMessage = szErrMsg;
        this.mErrors.addOneError(cError);
    }

	/**
	 * main
	 * 应用测试
	 * @param String[] args
	 */
    public static void main(String[] args)
    {
    }
}

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
	 * ͨ�ýӿڣ����մ������ݣ��������ݽ��д���
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
            buildError("submitData", "��������ʧ�ܣ�");
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
	 * ����׼��
	 */
    private boolean dealData()
    {
        String[] KeyWord = PubFun.split(mOperate, "||");

        String strSQL = "SELECT * FROM LFDesbMode where 1=1" +
                        ReportPubFun.getWherePart("ItemCode", KeyWord[0]) +
                        ReportPubFun.getWherePart("ItemNum", KeyWord[1]) + " " +
                        KeyWord[2];
        System.out.println("ͨ��ǰ̨��������ѯ������:" + strSQL);

        LFDesbModeSet tLFDesbModeSet = new LFDesbModeDB().executeQuery(strSQL);
        System.out.println("��ɲ�ѯsql����");

        if ((tLFDesbModeSet == null) || (tLFDesbModeSet.size() == 0))
        {
            //buildError("dealData", "��ѯ������ʧ�ܣ�");
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
            buildError("CheckTransitionCondition", "�������ϢΪ��");

            return false;
        }

        if (tLFDesbModeSchema.getDealType().equals("S"))
        {
            String insertSQL = "";
            insertSQL = getInsertSQL(tLFDesbModeSchema, tTransferData);
            //huangkai--PIR20081433--2008-10-09--����������������ʱ�����ѷ��ñ�������ݵ���ʱ��269�У��ᴫ�ؿ��ַ�������ִ�и���SQL����statementʱ�ᱨ��
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

        if (tNeedItemKeyName.equals("1")) //1-����Ҫ��
        {
            LFItemRelaDB tLFItemRelaDB = new LFItemRelaDB();
            tLFItemRelaDB.setItemCode(tLFDesbModeSchema.getItemCode());

            if (!tLFItemRelaDB.getInfo())
            {
                buildError("getInsertSQL", "��ѯ�����Ŀ�����Ӧ��ʧ�ܣ�");

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

        System.out.println("׼������sql��");

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
        System.out.println("��������ȡ�õ�SQL : " + strSQL);

        String stsql = "";
        //���ѱ�Ŀǰ�Ǹ���function�ܵĲ���Ҫinsert ���
        if("au_prem_info".equals(tLFDesbModeSchema.getDestTableName().trim())  //Amended By Fang 20080315
        		||"au_prem_temp".equals(tLFDesbModeSchema.getDestTableName().trim())
        		||"au_bill_info".equals(tLFDesbModeSchema.getDestTableName().trim())){
        	
        	ExeSQL tExeSQL = new ExeSQL();
        	SSRS tSSRS = tExeSQL.execSQL(strSQL);
        	if(tSSRS.getMaxRow() > 0){
        		System.out.println(tLFDesbModeSchema.getDestTableName().trim()+"�����������"+tSSRS.GetText(1, 1));
        	}
        }else{
        
	        String insertSQL = "Insert Into ";
	        String insertTableName = tLFDesbModeSchema.getDestTableName();
	        stsql = insertSQL + " " + insertTableName + " " + strSQL;
	        System.out.println("�õ���insert SQL ���: " + stsql);

        }
        return stsql;
    }

	/**
	 * getInputData
	 * ��ȡ��������
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
            buildError("getInputData", "û�еõ��㹻����Ϣ��");

            return false;
        }

        return true;
    }

	/**
	 * getResult
	 * ���ݷ���
	 */
    public VData getResult()
    {
        return mResult;
    }

	/**
	 * buildError
	 * ��������
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
	 * Ӧ�ò���
	 * @param String[] args
	 */
    public static void main(String[] args)
    {
    }
}

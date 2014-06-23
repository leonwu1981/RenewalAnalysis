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
 * <p>Title: ��������ҵ��ϵͳ</p>
 * <p>Description: </p>
 * <p>Copyright: SINOSOFT Copyright (c) 2004</p>
 * <p>Company: �п���Ƽ�</p>
 * @author guoxiang
 * @version 1.0
 */
public class XmlReportImportAfterInitService implements AfterInitService
{
    /** �������ݵ����� */
//    private VData mInputData = new VData();

    /** �������ݵ����� */
    private VData mResult = new VData();

    /** ���ݲ����ַ��� */
//    private String mOperate;

    /** �������� */
    public CErrors mErrors = new CErrors();

    /** ҵ������ر��� */
    private TransferData mTransferData = new TransferData();
    private MMap mmap = new MMap();
//    private String tOperate;

    /** ȫ������ */
    private GlobalInput mGlobalInput = new GlobalInput();

    public XmlReportImportAfterInitService()
    {
    }

    /**
     * �������ݵĹ�������
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
//        mOperate = cOperate;

        //  �õ��ⲿ��������ݣ������ݱ��ݵ�������
        if (!getInputData(cInputData))
        {
            return false;
        }

        //  ���������߼�
        if (!dealData())
        {
            return false;
        }

        //׼�������ύ����
        if (mmap != null && mmap.keySet().size() > 0)
        {
            mResult.add(mmap);
        }

        //  �ύ��BLS���в���INSERT ����
        //        ReportEngineBLS tReportEngineBLS = new ReportEngineBLS();
        //        if (!tReportEngineBLS.submitData(mResult, tOperate))
        //        {
        //            // @@������
        //            this.mErrors.copyAllErrors(tReportEngineBLS.mErrors);
        //            buildError("submitData", "���ݲ����м��ʧ�ܣ�");
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
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean dealData()
    {
        String tWhereSQLName = "WhereSQL";
        String tWhereSQLValue = mTransferData.getValueByName((Object)
                tWhereSQLName).toString();

        String[] KeyWord = PubFun.split(tWhereSQLValue, "||");

        // ���Ҽ��㴦��ļ�¼
        String strSQL = "SELECT * FROM LFDesbMode where 1=1"
                        + ReportPubFun.getWherePart("ItemCode", KeyWord[0])
                        + ReportPubFun.getWherePart("ItemNum", KeyWord[1])
                        + " " + KeyWord[2];
        System.out.println("ͨ��ǰ̨��������ѯ������:" + strSQL);

        LFDesbModeSet tLFDesbModeSet = new LFDesbModeDB().executeQuery(strSQL);
        if (tLFDesbModeSet.size() == 0)
        {
            buildError("dealData", "��ѯ������ʧ�ܣ�");

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
     * У��ת�������Ƿ�����
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
            // @@������
            buildError("CheckTransitionCondition", "�������ϢΪ��");

            return false;
        }

        if (tLFDesbModeSchema.getDealType().equals("S"))
        {
            //S-Ӧ��SQL�����д���
            String insertSQL = "";
            insertSQL = getInsertSQL(tLFDesbModeSchema, tTransferData);

            System.out.println("����sql���" + insertSQL);
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
            //C -- Ӧ��Class����д���
            try
            {
                Class tClass = Class.forName(tLFDesbModeSchema
                                             .getInterfaceClassName());
                CalService tCalService = (CalService) tClass.newInstance();

                // ׼������
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

        //׼������Ҫ��
        Vector tVector = tTransferData.getValueNames();
        String tNeedItemKeyName = "NeedItemKey";
        String tNeedItemKeyValue = tTransferData.getValueByName((Object)
                tNeedItemKeyName).toString();
        System.out.println("tNeedItemKeyName:" + tNeedItemKeyName);
        if (tNeedItemKeyValue.equals("1")) //1-����Ҫ��
        {
            LFItemRelaDB tLFItemRelaDB = new LFItemRelaDB();
            tLFItemRelaDB.setItemCode(tLFDesbModeSchema.getItemCode());
            if (!tLFItemRelaDB.getInfo())
            {
                buildError("getInsertSQL", "��ѯ�����Ŀ�����Ӧ��ʧ�ܣ�");
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

        //0����ͨҪ��
        for (int i = 0; i < tVector.size(); i++)
        {
            String tName = (String) tVector.get(i);
            String tValue = tTransferData.getValueByName((Object) tName).
                            toString();
            tPubCalculator.addBasicFactor(tName, tValue);
        }

        //׼������SQL
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
        System.out.println("��������ȡ�õ�SQL : " + strSQL);

        String insertSQL = "Insert Into ";
        String insertTableName = tLFDesbModeSchema.getDestTableName();
        String stsql = insertSQL + " " + insertTableName + " " + strSQL;
        System.out.println("�õ���insert SQL ���: " + stsql);

        return stsql;
    }

    /**
     * �����������еõ����ж���
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @param cInputData VData
     * @return boolean
     */
    private boolean getInputData(VData cInputData)
    {
        //ȫ�ֱ���
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput",
                0));

        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData",
                0);

        if ((mGlobalInput == null) || (mTransferData == null))
        {
            buildError("getInputData", "û�еõ��㹻����Ϣ��");

            return false;
        }

        return true;
    }

    /**
     * �������������������ȡ���ݴ�����
     * @return ���������ݲ�ѯ����ַ�����VData����
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

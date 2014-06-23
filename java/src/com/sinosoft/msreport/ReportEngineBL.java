/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

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
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;

/**
 * <p>Title: </p>
 * <p>Description:����һ��������������ӿ��� </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author guoxiang
 * @version 1.0
 */
public class ReportEngineBL
{
    /** �������ݵ����� */
    private VData mInputData = new VData();

    /** �������ݵ����� */
    private VData mResult = new VData();

    /** ���ݲ����ַ��� */
    private String mOperate;

    /** �������� */
    public CErrors mErrors = new CErrors();

    /** ҵ������ر��� */
    private TransferData mTransferData = new TransferData();
    private String tOperate;
    private String mNeedItemKey;

    /** ȫ������ */
    private GlobalInput mGlobalInput = new GlobalInput();

    public ReportEngineBL()
    {
    }

    /**
     * �������ݵĹ�������
     * @param: cInputData ���������
     *         cOperate ���ݲ���
     * @return:
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        mOperate = cOperate;

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

        //  �ύ��BLS���в���INSERT ����
        ReportEngineBLS tReportEngineBLS = new ReportEngineBLS();
        if (!tReportEngineBLS.submitData(mResult, tOperate))
        {
            // @@������
            this.mErrors.copyAllErrors(tReportEngineBLS.mErrors);
            buildError("submitData", "���ݲ����м��ʧ�ܣ�");
            mResult.clear();

            return false;
        }
        else
        {
            mResult = tReportEngineBLS.getResult();
            this.mErrors.copyAllErrors(tReportEngineBLS.mErrors);
        }

        return true;
    }

    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {
        String[] KeyWord = PubFun.split(mOperate, "||");

        // ���Ҽ��㴦��ļ�¼
        String strSQL = "SELECT * FROM LFDesbMode where 1=1"
                        + ReportPubFun.getWherePart("ItemCode", KeyWord[0])
                        + ReportPubFun.getWherePart("ItemNum", KeyWord[1])
                        + " " + KeyWord[2];
        System.out.println("ͨ��ǰ̨��������ѯ������:" + strSQL);

        LFDesbModeSet tLFDesbModeSet = new LFDesbModeDB().executeQuery(strSQL);
        if (tLFDesbModeSet == null || tLFDesbModeSet.size() == 0)
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
     * У��ת�������Ƿ�����
     * @param: tLWProcessInstanceSchema ����ĵ�ǰ����ʵ�� ����
     * @param: tInputData ����ĸ�������
     * @return boolean:
     *
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

            MMap map = new MMap();
            map.put(insertSQL, "EXESQL");
            mResult.add(map);

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
        if (tLFDesbModeSchema.getItemCode().equals("500"))
        {
            System.out.println("hahahah");

        }
        PubCalculator tPubCalculator = new PubCalculator();

        //׼������Ҫ��
        Vector tVector = (Vector) tTransferData.getValueNames();

        if (mNeedItemKey.equals("1")) //1-����Ҫ��
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
        //0����ͨҪ��
        for (int i = 0; i < tVector.size(); i++)
        {
            String tName = (String) tVector.get(i);
            String tValue = (String) tTransferData.getValueByName((Object)
                    tName)
                            .toString();
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

        mNeedItemKey = (String) cInputData.getObjectByObjectName("String", 0);
        if ((mGlobalInput == null) || (mTransferData == null)
            || (mNeedItemKey == ""))
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

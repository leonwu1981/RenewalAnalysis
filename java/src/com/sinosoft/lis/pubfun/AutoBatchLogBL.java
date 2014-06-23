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
 * <p>Title: ��������־����</p>
 * <p>Description: ��¼�������ִ�����</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: sinosoft</p>
 * @author zhuxf
 * @version 1.0
 */
public class AutoBatchLogBL
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** �����洫�����ݵ����� */
    private VData mInputData;
    private MMap mMap = new MMap();
    /** ҵ������ر��� */
    private GlobalInput mGlobalInput = new GlobalInput();
    private LCBatchLogSchema mLCBatchLogSchema = new LCBatchLogSchema();
    private String mOperate;
    private String mSeiralNo;

    public AutoBatchLogBL()
    {
    }

    /**
     * ͨ�ýӿں���
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //ȫ�ֵĲ�������
        mOperate = cOperate;

        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData(cInputData))
        {
            return false;
        }

        if (!dealData())
        {
            return false;
        }

        //׼������̨������
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
            // @@������
            mErrors.copyAllErrors(tSubmit.mErrors);
            CError tError = new CError();
            tError.moduleName = "AutoBatchLogBL";
            tError.functionName = "submitData";
            tError.errorMessage = "�����ύʧ�ܣ�";
            mErrors.addOneError(tError);
            return false;
        }
    }

    /**
     * ��ȡ�������ݺ���
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
     * ����׼������
     * @return boolean
     */
    public boolean dealData()
    {
        //�ж���־ִ�е����ͣ��ǿ�ʼ�������ǽ���������
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
                System.out.println("���������ˮ�ŵ�ʱ�����...");
                // @@������
                CError tError = new CError();
                tError.moduleName = "AutoBatchLogBL";
                tError.functionName = "dealData";
                tError.errorMessage = "��׼������ʱ����";
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
                System.out.println("��ѯ��־��ʱ�����...");
                // @@������
                CError tError = new CError();
                tError.moduleName = "AutoBatchLogBL";
                tError.functionName = "dealData";
                tError.errorMessage = "��׼������ʱ����";
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
     * ׼���������ݺ���
     * @return boolean
     */
    private boolean prepareOutputData()
    {
        try
        {
            //�ж���־ִ�е����ͣ��ǿ�ʼ�������ǽ���������
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
            // @@������
            CError tError = new CError();
            tError.moduleName = "AutoBatchLogBL";
            tError.functionName = "prepareOutputData";
            tError.errorMessage = "��׼������㴦������Ҫ������ʱ����";
            mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * �������к���Ϣ
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
//        tLCBatchLogSchema.setAwakeNote("֪ͨ�ɹ�");
//        tLCBatchLogSchema.setGetNoticeNo("1021010000114788");

        AutoBatchLogBL tAutoBatchLogBL = new AutoBatchLogBL();
        // ׼���������� VData
        VData tVData = new VData();
        tVData.add(tLCBatchLogSchema);
        tVData.add(tG);
        tAutoBatchLogBL.submitData(tVData, "BEGIN");
    }
}

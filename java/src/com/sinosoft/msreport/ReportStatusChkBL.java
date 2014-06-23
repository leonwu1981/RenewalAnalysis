/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;


import com.sinosoft.lis.db.LMCalModeDB;
import com.sinosoft.lis.pubfun.CalBase;
import com.sinosoft.lis.pubfun.Calculator;
import com.sinosoft.lis.pubfun.FDate;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LMCalModeSchema;
import com.sinosoft.lis.vschema.LMCalModeSet;
import com.sinosoft.lis.vschema.LMUWSet;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;

/**
 * <p>Title: Webҵ��ϵͳ����״̬��ѯ����</p>
 * <p>Description: �߼�������</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author WHN
 * @version 1.0
 */
public class ReportStatusChkBL
{

    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** �����洫�����ݵ����� */
    private VData mInputData;
    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();
    //private VData mIputData = new VData();
    private TransferData mTransferData = new TransferData();
    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;
    private String mOperate;
    /** ҵ�����ݲ����ַ��� */
    private String mStatYear;
    private String mStatMon;
    private String mCalCode; //�������
    private String mUser;
    private FDate fDate = new FDate();
    private float mValue;
    /** ҵ������ر��� */
    /**���㹫ʽ��**/
    private LMUWSet mLMUWSet = new LMUWSet();
    private LMUWSet m2LMUWSet = new LMUWSet();
    private LMUWSet mmLMUWSet = new LMUWSet();

    private LMCalModeSet mmLMCalModeSet = new LMCalModeSet();
    private LMCalModeSet mLMCalModeSet = new LMCalModeSet();

    private CalBase mCalBase = new CalBase();

    public ReportStatusChkBL()
    {}

    /**
     * �������ݵĹ�������
     * @param: cInputData ���������
     *         cOperate ���ݲ���
     * @return:
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        int flag = 0; //�ж��ǲ����������ݶ����ɹ�
        int j = 0; //�����������ݸ���

        //���������ݿ�����������
        mInputData = (VData) cInputData.clone();
        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData(cInputData, cOperate))
        {
            return false;
        }

        System.out.println("---ReportStatusChkBL getInputData---");
        // ���ݲ���ҵ����
        if (!dealData())
        {
            return false;
        }

        System.out.println("---ReportStatusChkBL dealData---");
        //׼�����ص�����
        prepareOutputData();

        return true;
    }

    /**
     * ���ݲ�����ҵ����
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {
        //׼���㷨
        if (CheckKinds() == false)
        {
            return false;
        }

        //ȡ������Ϣ
        int n = mmLMCalModeSet.size();
        if (n == 0)
        {
        }
        else
        {
            int j = 0;
            mLMCalModeSet.clear();
            for (int i = 1; i <= n; i++)
            {
                //ȡ�������
                LMCalModeSchema tLMCalModeSchema = new LMCalModeSchema();
                tLMCalModeSchema = mmLMCalModeSet.get(i);
                mCalCode = tLMCalModeSchema.getCalCode();
                if (CheckPol() == 0)
                {
                }
                else
                {
                    j++;
                    mLMCalModeSet.add(tLMCalModeSchema);
                }
            }
        }

        return true;
    }


    /**
     * �����������еõ����ж���
     *��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean getInputData(VData cInputData, String cOperate)
    {
        //�����������еõ����ж���
        //���ȫ�ֹ�������
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReportStatusChkBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��ò���Ա����
        mOperater = mGlobalInput.Operator;
        if (mOperater == null || mOperater.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReportStatusChkBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�½��������
        mManageCom = mGlobalInput.ManageCom;
        if (mManageCom == null || mManageCom.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReportStatusChkBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mOperate = cOperate;

        //���ҵ������
        if (mTransferData == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReportStatusChkBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mStatYear = (String) mTransferData.getValueByName("StatYear");
        if (mStatYear == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReportStatusChkBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������StatYearʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mStatMon = (String) mTransferData.getValueByName("StatMon");
        if (mStatMon == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReportStatusChkBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������StatMonʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * �˱�������ϢУ��,׼���˱��㷨
     * �����������������򷵻�false,���򷵻�true
     */
    private boolean CheckKinds()
    {
        String tsql = "";
        mmLMCalModeSet.clear();
        LMCalModeSchema tLMCalModeSchema = new LMCalModeSchema();
        //��ѯ�㷨����
        tsql = "select * from LMCalMode where riskcode = 'Report' and type ='S'  order by calcode";

        LMCalModeDB tLMCalModeDB = new LMCalModeDB();
        mmLMCalModeSet = tLMCalModeDB.executeQuery(tsql);
        if (tLMCalModeDB.mErrors.needDealError() == true)
        {
            // @@������
            this.mErrors.copyAllErrors(tLMCalModeDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "ReportStatusChkBL";
            tError.functionName = "CheckKinds";
            tError.errorMessage = "״̬������Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            mLMUWSet.clear();
            return false;
        }
        return true;
    }


    /**
     * ���˵��˱�
     * �����������������򷵻�false,���򷵻�true
     */
    private float CheckPol()
    {
        // ����
        Calculator mCalculator = new Calculator();
        mCalculator.setCalCode(mCalCode);
        //���ӻ���Ҫ��
        mCalculator.addBasicFactor("StatYear", mStatYear);
        mCalculator.addBasicFactor("StatMonth", mStatMon);
        String tStr = "";
        tStr = mCalculator.calculate();
        if (tStr.trim().equals(""))
        {
            mValue = 0;
        }
        else
        {
            mValue = Float.parseFloat(tStr);
        }

        System.out.println(mValue);
        return mValue;
    }


    /**
     * ׼����Ҫ���������
     */
    private void prepareOutputData()
    {
        mResult.clear();
        mResult.add(mLMCalModeSet);
    }

    public VData getResult()
    {
        return mResult;
    }

}

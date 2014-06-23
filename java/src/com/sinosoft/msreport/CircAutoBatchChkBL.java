/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.pubfun.PubFun1;
import com.sinosoft.lis.schema.LFCKErrorSchema;
import com.sinosoft.lis.schema.LFXMLCollSchema;
import com.sinosoft.lis.vschema.LFCKErrorSet;
import com.sinosoft.lis.vschema.LMCalModeSet;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;
import com.sinosoft.utility.VData;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class CircAutoBatchChkBL
{

    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** �����洫�����ݵ����� */
    private VData mInputData;
    /** �����洫�����ݵ����� */

    /** ȫ������ */
    private GlobalInput mGlobalInput = new GlobalInput();
//    private Reflections mReflections = new Reflections();

    /** ���ݲ����ַ��� */
    private String mOperate;
    private String mOperater;
    private String mManageCom;
//    private String mPolNo;
//    private String mPrtNo; //ӡˢ��
    private String mSQLCountString; //ӡˢ��
    private String mSQLString; //ӡˢ��
    private final int mCount = 100; //ÿ��ѭ������ļ�¼��


//����ʱ���
    private String CurrentDate = PubFun.getCurrentDate();
    private String CurrentTime = PubFun.getCurrentTime();
    private LFCKErrorSet tLFCKErrorSet = new LFCKErrorSet();
    private LFXMLCollSchema mLFXMLCollSchema = new LFXMLCollSchema();
//    private LCRnewStateLogSet mLCRnewStateLogSet = new LCRnewStateLogSet();
//    private CalBase mCalBase = new CalBase();
    private VData mResult = new VData();

    public CircAutoBatchChkBL()
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
//        int flag = 0; //�ж��ǲ����������ݶ����ɹ�
//        int j = 0; //�����������ݸ���

        //���������ݿ�����������
        mInputData = (VData) cInputData.clone();

        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData(cInputData))
        {
            return false;
        }
        System.out.println("---CircAutoBatchChkBL getInputData---");

        //����ҵ������У��
        if (!checkData())
        {
            return false;
        }

        //����ҵ����
        if (!dealData())
        {
            return false;
        }
        System.out.println("---CircAutoBatchChkBL dealData---");
        return true;
    }

    /**
     * �����������еõ����ж���
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @param cInputData VData
     * @return boolean
     */
    private boolean getInputData(VData cInputData)
    {

        //�����������еõ����ж���
        //���ȫ�ֹ�������
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mSQLCountString = (String) cInputData.get(0);
        mSQLString = (String) cInputData.get(1);

        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewAutoDunBL";
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
            tError.moduleName = "PRnewAutoDunBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operaterʧ��!";
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
            tError.moduleName = "PRnewAutoDunBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //���ҵ������
        if (mSQLString == null || mSQLString.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewAutoDunBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //���ҵ������
        if (mSQLCountString == null || mSQLCountString.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewAutoDunBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * У��ҵ������
     * @return boolean
     */
    private static boolean checkData()
    {
        return true;
    }

    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean dealData()
    {

        String SQL_Count = mSQLCountString;
        ExeSQL tExeSQL = new ExeSQL();
        SSRS tSSRS = tExeSQL.execSQL(SQL_Count);
        String strCount = tSSRS.GetText(1, 1);
        int SumCount = Integer.parseInt(strCount);
        int CurrentCounter = 1;
        String SQL_ItemCode = mSQLString;

//������������Ŀ��¼��������ѭ��
        while (CurrentCounter <= SumCount)
        {
            tExeSQL = new ExeSQL();
            tSSRS = tExeSQL.execSQL(SQL_ItemCode, CurrentCounter, mCount);
            if (tSSRS != null)
            {
                for (int i = 1; i <= tSSRS.getMaxRow(); i++)
                {
                    //׼����Ŀ����
                    VData tVData = new VData();
                    tVData = prepareCircAutoChkData(tSSRS, i);
                    //�Զ�У���Ŀ����
                    CircAutoChkBL tCircAutoChkBL = new CircAutoChkBL();
                    if (tCircAutoChkBL.submitData(tVData, "INSERT"))
                    {
                        //�Զ�У���Ŀ�����¼
                        LMCalModeSet mLMCalModeSet = new LMCalModeSet();
                        VData tVDate = new VData();
                        tVDate = tCircAutoChkBL.getResult();
                        if (tVDate != null)
                        {
                            mLMCalModeSet = (LMCalModeSet) tVDate.
                                            getObjectByObjectName(
                                    "LMCalModeSet", 0);
                            if (mLMCalModeSet != null &&
                                mLMCalModeSet.size() > 0)
                            {
                                PrepareErrLog(mLMCalModeSet, mLFXMLCollSchema);
                                //�����ύ
                                CircAutoBatchChkBLS tCircAutoBatchChkBLS = new
                                        CircAutoBatchChkBLS();
                                if (!tCircAutoBatchChkBLS.submitData(mResult,
                                        mOperate))
                                {
                                    // @@������
                                    this.mErrors.copyAllErrors(
                                            tCircAutoBatchChkBLS.mErrors);
                                    return false;
                                }
                            }
                        }
                    }
                }
                CurrentCounter += mCount; //����������
            }
        }

        return true;

    }

    /**
     * ��ӡ��Ϣ��
     * @return boolean
     */
//    private boolean prepareData()
//    {
//        for (int i = 1; i <= mLCRnewStateLogSet.size(); i++)
//        {
//            mLCRnewStateLogSet.get(i).setState("4");
//            mLCRnewStateLogSet.get(i).setModifyDate(CurrentDate);
//        }
//
//        return true;
//    }

    /**
     *
     * @param tSSRS SSRS
     * @param i int
     * @return VData
     */
    private VData prepareCircAutoChkData(SSRS tSSRS, int i)
    {
        VData tVData = new VData();

        //����������
        mLFXMLCollSchema = new LFXMLCollSchema();
        mLFXMLCollSchema.setComCodeISC(tSSRS.GetText(i, 1));
        mLFXMLCollSchema.setItemCode(tSSRS.GetText(i, 2));
        mLFXMLCollSchema.setRepType(tSSRS.GetText(i, 3));
        mLFXMLCollSchema.setStatMon(tSSRS.GetText(i, 5));
        mLFXMLCollSchema.setStatYear(tSSRS.GetText(i, 4));
        mLFXMLCollSchema.setStatValue(tSSRS.GetText(i, 6));
        tVData.add(mLFXMLCollSchema);
        tVData.add(mGlobalInput);
        return tVData;

    }

    /**
     * �����¼
     * @param mLMCalModeSet LMCalModeSet
     * @param tLFXMLCollSchema LFXMLCollSchema
     * @return boolean
     */
    private boolean PrepareErrLog(LMCalModeSet mLMCalModeSet,
                                  LFXMLCollSchema tLFXMLCollSchema)
    {
        tLFCKErrorSet = new LFCKErrorSet();
        mResult = new VData();
        for (int i = 1; i <= mLMCalModeSet.size(); i++)
        {
            String strNoLimit = PubFun.getNoLimit(mManageCom);
            String tCIRCERRORSEQ = PubFun1.CreateMaxNo("CIRCERRORSEQ",
                    strNoLimit);
            LFCKErrorSchema tLFCKErrorSchema = new LFCKErrorSchema();
            tLFCKErrorSchema.setSerialNo(tCIRCERRORSEQ);
            tLFCKErrorSchema.setComcodeisc(tLFXMLCollSchema.getComCodeISC());
            tLFCKErrorSchema.setItemCode(tLFXMLCollSchema.getItemCode());
            tLFCKErrorSchema.setCKRuleCode(mLMCalModeSet.get(i).getCalCode());
            tLFCKErrorSchema.setCKError(mLMCalModeSet.get(i).getRemark());
            tLFCKErrorSchema.setMakeDate(CurrentDate);
            tLFCKErrorSchema.setMakeTime(CurrentTime);
            tLFCKErrorSet.add(tLFCKErrorSchema);
        }

        MMap tMMap = new MMap();
        tMMap.put(tLFCKErrorSet, "INSERT");
        mResult.add(tMMap);
        return true;
    }

    public VData getResult()
    {
        return mResult;
    }


}

/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import com.sinosoft.lis.db.LMCalModeDB;
import com.sinosoft.lis.pubfun.Calculator;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LFXMLCollSchema;
import com.sinosoft.lis.schema.LMCalModeSchema;
import com.sinosoft.lis.vschema.LMCalModeSet;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

/**
 * <p>Title: Webҵ��ϵͳCIRC��ѯ����</p>
 * <p>Description: �߼�������</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author WHN
 * @version 1.0
 */
public class CircAutoChkBL
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** �����洫�����ݵ����� */
    private VData mInputData;
    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
    /** ���ݲ����ַ��� */
    private String mOperate;
//    private String mpassflag; //ͨ�����
//    private int merrcount; //��������
    private String mCalCode; //�������
//    private String mUser;
//    private FDate fDate = new FDate();
    private float mValue;

    LFXMLCollSchema mLFXMLCollSchema = new LFXMLCollSchema();
    /**���㹫ʽ��**/
//    private LMUWSchema mLMUWSchema = new LMUWSchema();
//    private LMUWSet mLMUWSet = new LMUWSet();

    private LMCalModeSet mmLMCalModeSet = new LMCalModeSet();
    private LMCalModeSet mLMCalModeSet = new LMCalModeSet();

//    private CalBase mCalBase = new CalBase();

    public CircAutoChkBL()
    {}

    /**
     * �������ݵĹ�������
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        int flag = 0; //�ж��ǲ����������ݶ����ɹ�
        int j = 0; //�����������ݸ���

        //���������ݿ�����������
        mInputData = (VData) cInputData.clone();
        //�õ��ⲿ���������,�����ݱ��ݵ�������

        if (!getInputData(cInputData))
        {
            return false;
        }
        System.out.println("---CircAutoChkBL getInputData---");

        // ���ݲ���ҵ����
        if (!dealData())
        {
            return false;
        }

        System.out.println("---CircAutoChkBL dealData---");
        //׼�����ص�����
        prepareOutputData();

        return true;
    }

    /**
     * ���ݲ�����ҵ����
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean dealData()
    {
        //׼��У���㷨����
        if (!CheckKinds())
        {
            return false;
        }

        //ȡ������Ϣ
        int n = mmLMCalModeSet.size();
        if (n > 0)
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
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @param cInputData VData
     * @return boolean
     */
    private boolean getInputData(VData cInputData)
    {
        GlobalInput tGlobalInput = new GlobalInput();
        tGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mOperate = tGlobalInput.Operator;

        mLFXMLCollSchema = (LFXMLCollSchema) cInputData.getObjectByObjectName(
                "LFXMLCollSchema", 0);

        return true;
    }

    /**
     * �˱�������ϢУ��,׼���˱��㷨
     * �����������������򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean CheckKinds()
    {
        String tsql = "";
        mmLMCalModeSet = new LMCalModeSet();
//        LMCalModeSchema tLMCalModeSchema = new LMCalModeSchema();
        //��ѯ�㷨����
        tsql = "select * from LMCalMode where riskcode = '" +
               mLFXMLCollSchema.getItemCode() +
               "' and type ='R'  order by calcode";
        System.out.println(tsql);
        LMCalModeDB tLMCalModeDB = new LMCalModeDB();
        mmLMCalModeSet = tLMCalModeDB.executeQuery(tsql);
        if (tLMCalModeDB == null)
        {
            this.mErrors.copyAllErrors(tLMCalModeDB.mErrors);
            return false;
        }

        return true;
    }


    /**
     * ���˵��˱�
     * �����������������򷵻�false,���򷵻�true
     * @return float
     */
    private float CheckPol()
    {
        // ����
        Calculator mCalculator = new Calculator();
        mCalculator.setCalCode(mCalCode);
        //���ӻ���Ҫ��
        mCalculator.addBasicFactor("ComCodeISC", mLFXMLCollSchema.getComCodeISC());
        mCalculator.addBasicFactor("ItemCode", mLFXMLCollSchema.getItemCode());
        mCalculator.addBasicFactor("RepType", mLFXMLCollSchema.getRepType());
        mCalculator.addBasicFactor("StatYear",
                                   Integer.toString(mLFXMLCollSchema.
                getStatYear()));
        mCalculator.addBasicFactor("StatMon",
                                   Integer.toString(mLFXMLCollSchema.getStatMon()));
        mCalculator.addBasicFactor("StatValue",
                                   Double.toString(mLFXMLCollSchema.
                getStatValue()));

        String tStr = "";
        tStr = mCalculator.calculate();
        if (tStr == null || tStr.trim().equals(""))
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

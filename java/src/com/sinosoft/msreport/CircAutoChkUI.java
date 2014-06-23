/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

/**
 * <p>Title: Webҵ��ϵͳ�б����˵�״̬��ѯ����</p>
 * <p>Description:�ӿڹ�����</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author WHN
 * @version 1.0
 */
public class CircAutoChkUI
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** �����洫�����ݵ����� */
//    private VData mInputData = new VData();
    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
    /** ���ݲ����ַ��� */
    private String mOperate;

    public CircAutoChkUI()
    {}

    /**
     * �������ݵĹ�������
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //���������ݿ�����������
        this.mOperate = cOperate;

        CircAutoChkBL tCircAutoChkBL = new CircAutoChkBL();

        System.out.println("---CircAutoChkUI BEGIN---");
        if (tCircAutoChkBL.submitData(cInputData, mOperate))
        {
            mResult = tCircAutoChkBL.getResult();
            this.mErrors.copyAllErrors(tCircAutoChkBL.mErrors);
        }
        else
        {
            mResult.clear();
            return false;
        }

        return true;
    }

    public VData getResult()
    {
        return mResult;
    }

    /**
     * ���Ժ���
     * @param args String[]
     */
    public static void main(String[] args)
    {
//        GlobalInput tG = new GlobalInput();
//        tG.Operator = "001";
//        LFXMLCollSchema mLFXMLCollSchema = new LFXMLCollSchema();
//        mLFXMLCollSchema.setItemCode("5");
//        mLFXMLCollSchema.setComCodeISC("100002");
//        mLFXMLCollSchema.setRepType("1");
//        mLFXMLCollSchema.setStatYear("2004");
//        mLFXMLCollSchema.setStatMon("6");
//
//        VData tVData = new VData();
//        tVData.add(mLFXMLCollSchema);
//        tVData.add(tG);
//        CircAutoChkUI ui = new CircAutoChkUI();
//        if (ui.submitData(tVData, ""))
//        {
//            System.out.println("---ok---");
//        }
//        else
//        {
//            System.out.println("---NO---");
//        }
//
//        CErrors tError = ui.mErrors;
//        int n = tError.getErrorCount();
    }
}

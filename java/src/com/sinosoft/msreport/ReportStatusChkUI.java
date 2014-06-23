/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

/**
 * <p>Title: Webҵ��ϵͳ����״̬��ѯ����</p>
 * <p>Description:�ӿڹ�����</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author WHN
 * @version 1.0
 */
public class ReportStatusChkUI
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** �����洫�����ݵ����� */
//    private VData mInputData = new VData();
    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
    /** ���ݲ����ַ��� */
    private String mOperate;

    public ReportStatusChkUI()
    {}

// @Main
    public static void main(String[] args)
    {
//        GlobalInput tG = new GlobalInput();
//        tG.Operator = "001";
//        tG.ManageCom = "86";
//        TransferData tTransferData = new TransferData();
//        tTransferData.setNameAndValue("StatYear", "2004");
//        tTransferData.setNameAndValue("StatMon", "06");
//        VData tVData = new VData();
//        tVData.add(tTransferData);
//        tVData.add(tG);
//        ReportStatusChkUI ui = new ReportStatusChkUI();
//        if (ui.submitData(tVData, "dd") == true)
//        {
//            System.out.println("---ok---");
//        }
//        else
//        {
//            System.out.println("---NO---");
//        }
//        VData tVData2 = new VData();
//        tVData2 = ui.getResult();
//        int i = tVData2.size();
    }

    /**
       �������ݵĹ�������
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //���������ݿ�����������
        this.mOperate = cOperate;

        ReportStatusChkBL tReportStatusChkBL = new ReportStatusChkBL();

        System.out.println("---ReportStatusChkUI BEGIN---");
        if (tReportStatusChkBL.submitData(cInputData, mOperate) == false)
        {
            mResult.clear();
            return false;
        }
        else
        {
            mResult = tReportStatusChkBL.getResult();
            this.mErrors.copyAllErrors(tReportStatusChkBL.mErrors);
        }

        return true;
    }

    public VData getResult()
    {
        return mResult;
    }
}

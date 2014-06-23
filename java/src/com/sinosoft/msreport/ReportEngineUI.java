/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;

/**
 * <p>Title: ��������ҵ��ϵͳ</p>
 * <p>Description: </p>
 * <p>Copyright: SINOSOFT Copyright (c) 2004</p>
 * <p>Company: �п���Ƽ�</p>
 * @author guoxiang
 * @version 1.0
 */
public class ReportEngineUI
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** �����洫�����ݵ����� */
    private VData mInputData = new VData();

    /** �����洫�����ݵ����� */
    private VData mResult = new VData();

    /** ���ݲ����ַ��� */
    private String mOperate;

    public ReportEngineUI()
    {
    }

    public boolean submitData(VData cInputData, String cOperate)
    {
        //���������ݿ�����������

        this.mOperate = cOperate;
        ReportEngineBL tReportEngineBL = new ReportEngineBL();
        System.out.println("---ReportEngineUI BEGIN---");
        if (!tReportEngineBL.submitData(cInputData, mOperate))
        {
            // @@������
            this.mErrors.copyAllErrors(tReportEngineBL.mErrors);
            buildError("submitData", "���ݲ�ѯʧ��");
            mResult.clear();

            return false;
        }
        else
        {
            mResult = tReportEngineBL.getResult();
            this.mErrors.copyAllErrors(tReportEngineBL.mErrors);
        }

        return true;
    }

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
        cError.moduleName = "ReportEngineUI";
        cError.functionName = szFunc;
        cError.errorMessage = szErrMsg;
        this.mErrors.addOneError(cError);
    }

    // @Main
    public static void main(String[] args)
    {
        VData tVData = new VData();
        GlobalInput mGlobalInput = new GlobalInput();
        TransferData mTransferData = new TransferData();

        /** ȫ�ֱ��� */
        mGlobalInput.Operator = "Admin";
        mGlobalInput.ComCode = "asd";
        mGlobalInput.ManageCom = "sdd";

        /** ���ݱ��� */

//        mTransferData.setNameAndValue("RepType", "1");
//        mTransferData.setNameAndValue("StatYear", "2004");
//        mTransferData.setNameAndValue("StatMon", "04");
        //һ������
//        mTransferData.setNameAndValue("ReportDate", "2004-06-10");
//        mTransferData.setNameAndValue("sDate", "2003-03-01");
//        mTransferData.setNameAndValue("eDate", "2003-12-10");
//
//        mTransferData.setNameAndValue("makedate", "2004-03-10");
//        mTransferData.setNameAndValue("maketime", "11:11:11");


        //XML ����

        mTransferData.setNameAndValue("RepType", "4"); //ͳ������
        mTransferData.setNameAndValue("StatYear", "2004"); //ͳ����
        mTransferData.setNameAndValue("StatMon", "07"); //ͳ����
        mTransferData.setNameAndValue("sYearDate", "2004-01-01"); //ͳ�����
        mTransferData.setNameAndValue("sDate", "2004-01-01"); //ͳ�ƿ�ʼʱ��
        mTransferData.setNameAndValue("eDate", "2004-07-25"); //ͳ�ƽ���ʱ��

        tVData.add(mGlobalInput);
        tVData.add("1");
        tVData.add(mTransferData);

        try
        {
            ReportEngineUI tReportEngineUI = new ReportEngineUI();

            if (!tReportEngineUI.submitData(tVData, "" + "||" + "2043" + "|| AND ItemCode ='2043' AND Dealtype='S' AND ItemType In ('&','X1') order by ItemNum"))
            {
                if (tReportEngineUI.mErrors.needDealError())
                {
                    System.out.println(tReportEngineUI.mErrors.getFirstError());
                }
                else
                {
                    System.out.println("����ʧ�ܣ�����û����ϸ��ԭ��");
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}

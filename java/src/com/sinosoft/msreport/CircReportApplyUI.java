/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;


import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;

/**
 * <p>Title: Webҵ��ϵͳ�б����˵�״̬��ѯ����</p>
 * <p>Description:�ӿڹ�����</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author WHN
 * @version 1.0
 */
public class CircReportApplyUI
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** �����洫�����ݵ����� */
    private VData mInputData = new VData();
    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
    /** ���ݲ����ַ��� */
    private String mOperate;

    public CircReportApplyUI()
    {}

// @Main
    public static void main(String[] args)
    {
        GlobalInput tG = new GlobalInput();
        tG.Operator = "001";
        tG.ManageCom = "86";
        CircReportApplyUI tCircReportApplyUI = new CircReportApplyUI();
        TransferData tTransferData = new TransferData();

        tTransferData.setNameAndValue("StatYear", "2004");
        tTransferData.setNameAndValue("StatMonth", "01");

        VData tVData = new VData();
        tVData.add(tTransferData);
        tVData.add(tG);
        CircReportApplyUI ui = new CircReportApplyUI();
        if (ui.submitData(tVData, "") == true)
        {
            System.out.println("---ok---");
        }
        else
        {
            System.out.println("---NO---" + ui.mErrors.getError(0).errorMessage);
        }

        CErrors tError = ui.mErrors;
        int n = tError.getErrorCount();

    }

    /**
       �������ݵĹ�������
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //���������ݿ�����������
        this.mOperate = cOperate;

        CircReportApplyBL tCircReportApplyBL = new CircReportApplyBL();

        System.out.println("---CircReportApplyBL BEGIN---");
        if (tCircReportApplyBL.submitData(cInputData, mOperate) == false)
        {
            // @@������
            this.mErrors.copyAllErrors(tCircReportApplyBL.mErrors);
//      CError tError = new CError();
//      tError.moduleName = "PolStatusChkUI";
//      tError.functionName = "submitData";
//      //tError.errorMessage = "���ݲ�ѯʧ��!";
//      this.mErrors .addOneError(tError) ;
            mResult.clear();
            return false;
        }
        else
        {
            //this.mErrors.copyAllErrors(tCircReportApplyBL.mErrors);
        }

        return true;
    }

    public VData getResult()
    {
        return mResult;
    }
}

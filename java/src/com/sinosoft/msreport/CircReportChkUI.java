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
public class CircReportChkUI
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** �����洫�����ݵ����� */
    private VData mInputData = new VData();
    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
    /** ���ݲ����ַ��� */
    private String mOperate;

    public CircReportChkUI()
    {}

// @Main
    public static void main(String[] args)
    {
        GlobalInput tG = new GlobalInput();
        tG.Operator = "001";
        tG.ManageCom = "86";
        tG.ComCode = "86";
        TransferData tTransferData = new TransferData();
        tTransferData.setNameAndValue("StatYear", "2004");
        tTransferData.setNameAndValue("StatMon", "6");
        tTransferData.setNameAndValue("RepType", "4");
        tTransferData.setNameAndValue("ComCodeISC", "-1");

        VData tVData = new VData();
        tVData.add(tTransferData);
        tVData.add(tG);
        CircReportChkUI ui = new CircReportChkUI();
        if (ui.submitData(tVData, "") == true)
        {
            System.out.println("---ok---");
        }
        else
        {
            System.out.println("---NO---");
        }

        CErrors tError = ui.mErrors;
        int n = tError.getErrorCount();

        String Content = "TEST!";
        if (n > 0)
        {
            Content = Content.trim() + "��δͨ���Զ��˱�����ԭ����:";
            for (int i = 0; i < n; i++)
            {
                //tError = tErrors.getError(i);
                Content = Content.trim() + i + ". " +
                          tError.getError(i).errorMessage.trim() + ".";
            }
        }

    }

    /**
       �������ݵĹ�������
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //���������ݿ�����������
        this.mOperate = cOperate;

        CircReportChkBL tCircAutoBatchChkBL = new CircReportChkBL();

        System.out.println("---CircAutoChkUI BEGIN---");
        if (tCircAutoBatchChkBL.submitData(cInputData, mOperate) == false)
        {
            mResult.clear();
            return false;
        }
        else
        {
            mResult = tCircAutoBatchChkBL.getResult();
            this.mErrors.copyAllErrors(tCircAutoBatchChkBL.mErrors);
        }

        return true;
    }

    public VData getResult()
    {
        return mResult;
    }
}

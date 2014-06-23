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
public class CircAutoBatchChkUI
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** �����洫�����ݵ����� */
//    private VData mInputData = new VData();
    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
    /** ���ݲ����ַ��� */
    private String mOperate;

    public CircAutoBatchChkUI()
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

        CircAutoBatchChkBL tCircAutoBatchChkBL = new CircAutoBatchChkBL();

        System.out.println("---CircAutoChkUI BEGIN---");
        if (tCircAutoBatchChkBL.submitData(cInputData, mOperate))
        {
            mResult = tCircAutoBatchChkBL.getResult();
            this.mErrors.copyAllErrors(tCircAutoBatchChkBL.mErrors);
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
//        tG.ManageCom = "86";
//        tG.ComCode = "86";
//        String tSQL =
//                "select * from LFXMLColl where itemcode='5' order by itemcode ";
//        String tCountSQL = "select count(*) from LFXMLColl where itemcode='5' ";
//        VData tVData = new VData();
//        tVData.add(tCountSQL);
//        tVData.add(tSQL);
//        tVData.add(tG);
//        CircAutoBatchChkUI ui = new CircAutoBatchChkUI();
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
//
//        String Content = "TEST!";
//        if (n > 0)
//        {
//            Content = Content.trim() + "��δͨ���Զ��˱�����ԭ����:";
//            for (int i = 0; i < n; i++)
//            {
//                //tError = tErrors.getError(i);
//                Content = Content.trim() + i + ". " +
//                          tError.getError(i).errorMessage.trim() + ".";
//            }
//        }
    }
}

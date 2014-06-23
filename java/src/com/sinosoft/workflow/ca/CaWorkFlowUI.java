package com.sinosoft.workflow.ca;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.utility.*;



/**
 * <p>Title:���⹤���� </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author pd
 * @version 1.0
 */

public class CaWorkFlowUI
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** �����洫�����ݵ����� */
    private VData mInputData = new VData();

    /** �����洫�����ݵ����� */
    private VData mResult = new VData();

    /** ���ݲ����ַ��� */
    private String mOperate;

    public CaWorkFlowUI()
    {}

    public static void main(String[] args)
    {
        VData tVData = new VData();
        GlobalInput mGlobalInput = new GlobalInput();
        TransferData mTransferData = new TransferData();

        /** ȫ�ֱ��� */
        mGlobalInput.Operator = "001";
        mGlobalInput.ComCode = "86";
        mGlobalInput.ManageCom = "86";
        //������ʼ�ڵ�
//        mTransferData.setNameAndValue("MissionID", "00000000000000006736");
//        mTransferData.setNameAndValue("SubMissionID", "1");
//        mTransferData.setNameAndValue("CaseNo", "500000000000071");
//        mTransferData.setNameAndValue("RptorName", "�ϵ�28");
//        mTransferData.setNameAndValue("CAFlag", "1");
        mTransferData.setNameAndValue("CaseNo", "500000000000073");
        mTransferData.setNameAndValue("ManageCom","86");
        mTransferData.setNameAndValue("RptorName","�ϵ�29");
        //mTransferData.setNameAndValue("CAFlag","2");
        mTransferData.setNameAndValue("InputDate","2005-10-09");
        mTransferData.setNameAndValue("Operator","001");
        mTransferData.setNameAndValue("RgtState","����״̬");
       // mTransferData.setNameAndValue("MissionID","00000000000000006737");
      //  mTransferData.setNameAndValue("SubMissionID","1");
        mTransferData.setNameAndValue("SurveyNo","86000000000055");
        /**�ܱ���*/
        tVData.add(mGlobalInput);
        tVData.add(mTransferData);

        CaWorkFlowUI tGrpTbWorkFlowUI = new CaWorkFlowUI();
        try
        {
            if (tGrpTbWorkFlowUI.submitData(tVData, "7899999999"))
            {
                VData tResult = new VData();

                //tResult = tActivityOperator.getResult() ;
            }
            else
            {
                System.out.println(tGrpTbWorkFlowUI.mErrors.getError(0).
                                   errorMessage);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }


    /**
       �������ݵĹ�������
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //���������ݿ�����������
        this.mOperate = cOperate;

        CaWorkFlowBL tCaWorkFlowBL = new CaWorkFlowBL();

        System.out.println("---CaWorkFlowBL UI BEGIN---");
        if (tCaWorkFlowBL.submitData(cInputData, mOperate) == false)
        {
            // @@������
            this.mErrors.copyAllErrors(tCaWorkFlowBL.mErrors);
            mResult.clear();
            return false;
        }
        return true;
    }
}

package com.sinosoft.workflow.ca;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.utility.*;



/**
 * <p>Title:理赔工作流 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author pd
 * @version 1.0
 */

public class CaWorkFlowUI
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 往后面传输数据的容器 */
    private VData mInputData = new VData();

    /** 往界面传输数据的容器 */
    private VData mResult = new VData();

    /** 数据操作字符串 */
    private String mOperate;

    public CaWorkFlowUI()
    {}

    public static void main(String[] args)
    {
        VData tVData = new VData();
        GlobalInput mGlobalInput = new GlobalInput();
        TransferData mTransferData = new TransferData();

        /** 全局变量 */
        mGlobalInput.Operator = "001";
        mGlobalInput.ComCode = "86";
        mGlobalInput.ManageCom = "86";
        //创建起始节点
//        mTransferData.setNameAndValue("MissionID", "00000000000000006736");
//        mTransferData.setNameAndValue("SubMissionID", "1");
//        mTransferData.setNameAndValue("CaseNo", "500000000000071");
//        mTransferData.setNameAndValue("RptorName", "上帝28");
//        mTransferData.setNameAndValue("CAFlag", "1");
        mTransferData.setNameAndValue("CaseNo", "500000000000073");
        mTransferData.setNameAndValue("ManageCom","86");
        mTransferData.setNameAndValue("RptorName","上帝29");
        //mTransferData.setNameAndValue("CAFlag","2");
        mTransferData.setNameAndValue("InputDate","2005-10-09");
        mTransferData.setNameAndValue("Operator","001");
        mTransferData.setNameAndValue("RgtState","立案状态");
       // mTransferData.setNameAndValue("MissionID","00000000000000006737");
      //  mTransferData.setNameAndValue("SubMissionID","1");
        mTransferData.setNameAndValue("SurveyNo","86000000000055");
        /**总变量*/
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
       传输数据的公共方法
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //将操作数据拷贝到本类中
        this.mOperate = cOperate;

        CaWorkFlowBL tCaWorkFlowBL = new CaWorkFlowBL();

        System.out.println("---CaWorkFlowBL UI BEGIN---");
        if (tCaWorkFlowBL.submitData(cInputData, mOperate) == false)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tCaWorkFlowBL.mErrors);
            mResult.clear();
            return false;
        }
        return true;
    }
}

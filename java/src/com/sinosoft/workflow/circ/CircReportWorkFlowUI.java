/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.circ;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class CircReportWorkFlowUI
{

    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往后面传输数据的容器 */
//    private VData mInputData = new VData();
    /** 往界面传输数据的容器 */
    private VData mResult = new VData();
    /** 数据操作字符串 */
    private String mOperate;

    public CircReportWorkFlowUI()
    {}

    /**
     * 传输数据的公共方法
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //将操作数据拷贝到本类中
        this.mOperate = cOperate;

        CircReportWorkFlowBL tCircReportWorkFlowBL = new CircReportWorkFlowBL();

        System.out.println("---CircReportWorkFlowBL UI BEGIN---");
        if (tCircReportWorkFlowBL.submitData(cInputData, mOperate))
        {
            return true;
        }
        else
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tCircReportWorkFlowBL.mErrors);
            mResult.clear();
            return false;
        }
    }

    /**
     * 测试函数
     * @param args String[]
     */
    public static void main(String[] args)
    {
        VData tVData = new VData();
        GlobalInput mGlobalInput = new GlobalInput();
        TransferData mTransferData = new TransferData();

        /** 全局变量 */
        mGlobalInput.Operator = "001";
        mGlobalInput.ComCode = "86";
        mGlobalInput.ManageCom = "86";
        /** 传递变量 */
//	mTransferData.setNameAndValue("InsuredNo","0000010748");
//	//发体检通知书测试
//	//加费录入测试
//	//生调通知书测试
//发送核保通知书
        // 准备传输工作流数据 VData
//	mTransferData.setNameAndValue("PolNo","86110020030210001017");
//	mTransferData.setNameAndValue("EdorNo","86110020030410000195");
//	mTransferData.setNameAndValue("MissionID","00000000000000000017");
//        mTransferData.setNameAndValue("LPUWMasterMainSchema",mLPUWMasterMainSchema);
//

//	 // 准备传输数据 VData
//	   String tOperate = new String();
//	   TransferData tTransferData = new TransferData();
//	   tTransferData.setNameAndValue("CertifyNo","86000020030810002085");
//	   tTransferData.setNameAndValue("CertifyCode","7773") ;
//	   tTransferData.setNameAndValue("PolNo","86110020030210004500") ;
//	   tTransferData.setNameAndValue("EdorNo","86110020030410000213") ;
//	   tTransferData.setNameAndValue("MissionID","00000000000000000028") ;
//	   tTransferData.setNameAndValue("SubMissionID","1") ;
//	   tTransferData.setNameAndValue("LZSysCertifySchema",tLZSysCertifySchema);

//    //精算数据导入
//	mTransferData.setNameAndValue("FileName","Data000000JS2004062.xls");
//	mTransferData.setNameAndValue("ConfigFileName","ExcelImportLFJSConfig.xml");
//    mTransferData.setNameAndValue("ItemType","04") ;
//	mTransferData.setNameAndValue("StatYear","2004") ;
//	mTransferData.setNameAndValue("StatMon","01");
//	mTransferData.setNameAndValue("MissionID","00000000000000000116");
//	mTransferData.setNameAndValue("SubMissionID","1");00000000000000000117
//    //精算数据导入确认
//	mTransferData.setNameAndValue("FileName","Data000000JS2004062.xls");
//	mTransferData.setNameAndValue("ConfigFileName","ExcelImportLFJSConfig.xml");
//    mTransferData.setNameAndValue("ItemType","07") ;
//	mTransferData.setNameAndValue("StatYear","2004") ;
//	mTransferData.setNameAndValue("StatMon","01");
//	mTransferData.setNameAndValue("MissionID","00000000000000000117");
//	mTransferData.setNameAndValue("SubMissionID","1");
//    //业务数据导入确认
//	mTransferData.setNameAndValue("FileName","Data000000JS2004062.xls");
//	mTransferData.setNameAndValue("ConfigFileName","ExcelImportLFJSConfig.xml");
//	mTransferData.setNameAndValue("ItemType","01") ;
//	mTransferData.setNameAndValue("StatYear","2004") ;
//	mTransferData.setNameAndValue("StatMon","07");
//	mTransferData.setNameAndValue("MissionID","00000000000000000117");
//	mTransferData.setNameAndValue("SubMissionID","1");

//	mTransferData.setNameAndValue("FileName","Data000052JS2005032.xls");
//	mTransferData.setNameAndValue("ConfigFileName","ExcelImportLFJSConfig.xml");
//        mTransferData.setNameAndValue("StatYear", "2005");
//        mTransferData.setNameAndValue("RepType", "2");
//        mTransferData.setNameAndValue("StatMon", "03");

//        tRepType: 2
//        tStatYear: 2005
//        tStatMonth: 03
//        tYearDate: 2005-01-01
//        tSubMissionID: 1
//        tMissionID: 00000000000000000335
//        tWhereSQL:  ||  ||  AND ItemType In ('X1','X2','X3','X4','X5','X6','X7')  order by ItemNum
//        tReportDate: 2005-03-01
//        makedate: 2005-03-05
//        sDate: 2005-02-26
//        tEndTime: 2005-03-25

        mTransferData.setNameAndValue("RepType", "2");
        mTransferData.setNameAndValue("StatYear", "2005");
        mTransferData.setNameAndValue("StatMon", "03");
        mTransferData.setNameAndValue("sYearDate", "2005-01-01");
        mTransferData.setNameAndValue("SubMissionID", "1");
        mTransferData.setNameAndValue("MissionID", "00000000000000000335");

        //准备公共传输信息
//        mTransferData.setNameAndValue("WhereSQL", " ||  ||AND ItemType In ('X1','X2','X3','X4','X5','X6','X7')  AND ItemCode in(select itemcode from LFItemRela where OutPutFlag='1' and IsMon='1') order by ItemNum");
        mTransferData.setNameAndValue("WhereSQL", " ||  ||  AND ItemType In ('X1','X2','X3','X4','X5','X6','X7')  order by ItemNum");
        mTransferData.setNameAndValue("NeedItemKey", "1");
        mTransferData.setNameAndValue("ReportDate", "2005-03-01");
        mTransferData.setNameAndValue("makedate", "2005-03-05");
        mTransferData.setNameAndValue("maketime", "10:10:10");
        mTransferData.setNameAndValue("sDate", "2005-02-26");
        mTransferData.setNameAndValue("eDate", "2005-03-25");

//
//    //准备公共传输信息
//	mTransferData.setNameAndValue("PolNo","86110020030210004500");
//	mTransferData.setNameAndValue("EdorNo","86110020030410000213") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000028");
//	mTransferData.setNameAndValue("PrtSeq","86000020030810002084");
//	mTransferData.setNameAndValue("SubMissionID","3");
//	mTransferData.setNameAndValue("LPRReportSchema",tLPRReportSchema);

        // 准备传输工作流数据 VData
//	mTransferData.setNameAndValue("StatYear","2004") ;
//	mTransferData.setNameAndValue("StatMonth","01");
//    mTransferData.setNameAndValue("MissionID","00000000000000000110");
//    mTransferData.setNameAndValue("SubMissionID","1");

//	VData tVData3 = new VData();
//    mTransferData.setNameAndValue("PolNo","86110020030210035008");
//    mTransferData.setNameAndValue("EdorNo","86110020040410000039") ;
//    mTransferData.setNameAndValue("MissionID","00000000000000000023");
//    mTransferData.setNameAndValue("SubMissionID","1");


        /**总变量*/
        tVData.add(mGlobalInput);
        tVData.add(mTransferData);

        CircReportWorkFlowUI tCircReportWorkFlowUI = new CircReportWorkFlowUI();
        try
        {
            if (tCircReportWorkFlowUI.submitData(tVData, "0000000222"))
            {
//                VData tResult = new VData();

                //tResult = tActivityOperator.getResult() ;
            }
            else
            {
                System.out.println(tCircReportWorkFlowUI.mErrors.getError(0).
                                   errorMessage);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}

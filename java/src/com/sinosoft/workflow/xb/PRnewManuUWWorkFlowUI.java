package com.sinosoft.workflow.xb;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LCUWMasterSchema;
import com.sinosoft.lis.schema.LZSysCertifySchema;
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

public class PRnewManuUWWorkFlowUI
{

    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往后面传输数据的容器 */
    private VData mInputData = new VData();
    /** 往界面传输数据的容器 */
    private VData mResult = new VData();
    /** 数据操作字符串 */
    private String mOperate;

    public PRnewManuUWWorkFlowUI()
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

        //执行起始节点
//	VData tVData3 = new VData();
//	mTransferData.setNameAndValue("PolNo","86110020040110000301");
//	mTransferData.setNameAndValue("PrtNo","200403290001") ;
//	mTransferData.setNameAndValue("ProposalNo","86110020040110000301");
//	mTransferData.setNameAndValue("MissionID","00000000000000000070");
//	mTransferData.setNameAndValue("SubMissionID","1");
        /** 传递变量 */
//	mTransferData.setNameAndValue("EdorNo","86110020030420000274") ;
//	mTransferData.setNameAndValue("PolNo","86110020030210001677");
//	mTransferData.setNameAndValue("RiskCode","111301");
//	mTransferData.setNameAndValue("RiskName","11130险种");
//	mTransferData.setNameAndValue("InsuredNo","0000000139");
//	mTransferData.setNameAndValue("InsuredName","王士宝");
//	mTransferData.setNameAndValue("AppntNo","0000000139");
//	mTransferData.setNameAndValue("AppntName","王士宝");

//	mTransferData.setNameAndValue("InsuredNo","0000010748");

        //发体检通知书测试
//	LCPENoticeSchema tLCPENoticeSchema = new LCPENoticeSchema();
//	tLCPENoticeSchema.setProposalNo("86110020040110000301");
//	tLCPENoticeSchema.setPEAddress("11003001");
//	tLCPENoticeSchema.setPEDate("2003-07-01");
//	tLCPENoticeSchema.setPEBeforeCond("N");
//	tLCPENoticeSchema.setRemark("ReMark");
//	tLCPENoticeSchema.setInsuredNo("0000030835");
//	mTransferData.setNameAndValue("LCPENoticeSchema",tLCPENoticeSchema);

//	LCPENoticeItemSet tLCPENoticeItemSet = new LCPENoticeItemSet();
//	LCPENoticeItemSchema tLCPENoticeItemSchema = new LCPENoticeItemSchema();
//	tLCPENoticeItemSchema.setProposalNo("86110020040110000301");
//	tLCPENoticeItemSchema.setInsuredNo("0000030835") ;
//	tLCPENoticeItemSchema.setPEItemCode( "001");
//	tLCPENoticeItemSchema.setPEItemName( "普检腹部B超");
//	tLCPENoticeItemSchema.setFreePE( "N");
//
//	LCPENoticeItemSchema tLCPENoticeItemSchema2 = new LCPENoticeItemSchema();
//	tLCPENoticeItemSchema2.setProposalNo("86110020040110000301");
//	tLCPENoticeItemSchema2.setInsuredNo("0000030835") ;
//	tLCPENoticeItemSchema2.setPEItemCode( "002");
//	tLCPENoticeItemSchema2.setPEItemName( "普检腹部B超");
//	tLCPENoticeItemSchema2.setFreePE( "N");

//	tLCPENoticeItemSet.add( tLCPENoticeItemSchema );
//	tLCPENoticeItemSet.add( tLCPENoticeItemSchema2 );
//	mTransferData.setNameAndValue("LCPENoticeItemSet",tLCPENoticeItemSet);
//	mTransferData.setNameAndValue("PolNo","86110020040110000301");
//    mTransferData.setNameAndValue("MissionID","00000000000000000070");
//	mTransferData.setNameAndValue("SubMissionID","1");
//    mTransferData.setNameAndValue("InsuredNo","0000030835");
        //特约录入测试
//	LCSpecSchema tLCSpecSchema = new LCSpecSchema();
//	tLCSpecSchema.setPolNo("86110020040110000301");
//	tLCSpecSchema.setPolType("1");
//	tLCSpecSchema.setSpecContent("特约内容");
//	tLCSpecSchema.setSpecType("1");
//	tLCSpecSchema.setSpecCode("1");
//	mTransferData.setNameAndValue("Remark","tRemark");
//	mTransferData.setNameAndValue("PolNo","86110020040110000301");
//	mTransferData.setNameAndValue("PrtNo","200403290001") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000070");
//	mTransferData.setNameAndValue("SpecReason","my特约原因");
//	mTransferData.setNameAndValue("LCSpecSchema",tLCSpecSchema);
//	mTransferData.setNameAndValue("SubMissionID","1");
//	//加费录入测试
//	LCPremSet tLCPremSet = new LCPremSet();
//	LCPremSchema tLCPremSchema = new LCPremSchema();
//    tLCPremSchema.setPolNo("86110020040110000301");
//	tLCPremSchema.setDutyCode("205001");
//	//tLPPremSchema.setPayPlanCode("00000001");//加费
//	tLCPremSchema.setPayStartDate("2004-03-29");
//	tLCPremSchema.setPayPlanType( "1");
//	tLCPremSchema.setPayEndDate( "2049-03-29");
//	tLCPremSchema.setPrem( 50);
//	tLCPremSet.add( tLCPremSchema );
//
//	LCPremSchema tLCPremSchema2 = new LCPremSchema();
//	tLCPremSchema2.setPolNo("86110020040110000301");
//	tLCPremSchema2.setDutyCode("205001");
//	//tLPPremSchema2.setPayPlanCode("00000002");//加费
//	tLCPremSchema2.setPayStartDate("2004-03-29");
//	tLCPremSchema2.setPayPlanType( "2");
//	tLCPremSchema2.setPayEndDate( "2049-03-29");
//	tLCPremSchema2.setPrem( 50);
//    tLCPremSet.add( tLCPremSchema2 );

//	mTransferData.setNameAndValue("PolNo","86110020040110000301");
//	mTransferData.setNameAndValue("PolNo2","86110020040110000301");
//	mTransferData.setNameAndValue("PrtNo","200403290001") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000070");
//	mTransferData.setNameAndValue("SubMissionID","1");
//	mTransferData.setNameAndValue("AddReason","加费原因");
//	mTransferData.setNameAndValue("LCPremSet",tLCPremSet);

//	//生调通知书测试
//	LCRReportSchema tLCRReportSchema = new LCRReportSchema();
//	tLCRReportSchema.setPolNo("86110020040110000301");
//    tLCRReportSchema.setContente("我的生调内容：哈哈");
//
//	mTransferData.setNameAndValue("PolNo","86110020040110000301");
//	mTransferData.setNameAndValue("PrtNo","200403290001") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000070");
//	mTransferData.setNameAndValue("SubMissionID","1");
//	mTransferData.setNameAndValue("LCRReportSchema",tLCRReportSchema);


//    //发送核保通知书
//	mTransferData.setNameAndValue("PolNo","86110020040110000301");
//	mTransferData.setNameAndValue("PrtNo","200403290001") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000070");
//	mTransferData.setNameAndValue("SubMissionID","1");
//
//
        //准备传输工作流数据 VData
//	 LPUWMasterMainSchema mLPUWMasterMainSchema = new LPUWMasterMainSchema();
//	 mLPUWMasterMainSchema.setPolNo("86110020030210009299");
//	 mLPUWMasterMainSchema.setEdorNo("86110020030410000126");
//	 mLPUWMasterMainSchema.setUWIdea("正常承保!");
//	 mLPUWMasterMainSchema.setPassFlag("9");
//	 mLPUWMasterMainSchema.setAppGrade("");
//
        // 准备传输工作流数据 VData
//	mTransferData.setNameAndValue("PolNo","86110020030210001017");
//	mTransferData.setNameAndValue("EdorNo","86110020030410000195");
//	mTransferData.setNameAndValue("MissionID","00000000000000000017");
        //mTransferData.setNameAndValue("LPUWMasterMainSchema",mLPUWMasterMainSchema);
//

//	//打印体检通知书
//	mTransferData.setNameAndValue("PrtSeq","86000020040810000080");
//	mTransferData.setNameAndValue("Code","43") ;
//	mTransferData.setNameAndValue("PolNo","86110020040110000301") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000070") ;
//    mTransferData.setNameAndValue("SubMissionID","1") ;

//	//打印核保通知书
//	mTransferData.setNameAndValue("PrtSeq","86000020040810000101");
//	mTransferData.setNameAndValue("Code","45") ;
//	mTransferData.setNameAndValue("PolNo","86110020040110000301") ;
//	mTransferData.setNameAndValue("PrtNo","200403290001") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000070") ;
//    mTransferData.setNameAndValue("SubMissionID","1") ;

        //	//打印生调通知书
//	mTransferData.setNameAndValue("PrtSeq","86000020040810000098");
//	mTransferData.setNameAndValue("Code","44") ;
//	mTransferData.setNameAndValue("PolNo","86110020040110000301") ;
//	mTransferData.setNameAndValue("PrtNo","200403290001") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000070") ;
//	mTransferData.setNameAndValue("SubMissionID","1") ;


        //回收体检通知书
//	  LZSysCertifySchema tLZSysCertifySchema = new LZSysCertifySchema();
//	   tLZSysCertifySchema.setCertifyCode( "6663" );
//	   tLZSysCertifySchema.setCertifyNo( "86000020040810000080" );
//	   tLZSysCertifySchema.setTakeBackOperator( "001" );
//	   tLZSysCertifySchema.setTakeBackDate( "2004-03-31" );
//	   tLZSysCertifySchema.setTakeBackMakeDate( "2004-03-31" );
//	   tLZSysCertifySchema.setSendOutCom( "A86" );
//	   tLZSysCertifySchema.setReceiveCom( "D8611000001" );

//	 // 准备传输数据 VData
//	   String tOperate = new String();
//	   TransferData tTransferData = new TransferData();
//	   mTransferData.setNameAndValue("CertifyNo","86000020040810000080");
//	   mTransferData.setNameAndValue("CertifyCode","6663") ;
//	   mTransferData.setNameAndValue("PolNo","86110020040110000301") ;
//	   mTransferData.setNameAndValue("MissionID","00000000000000000070") ;
//	   mTransferData.setNameAndValue("SubMissionID","1") ;
//	   mTransferData.setNameAndValue("LZSysCertifySchema",tLZSysCertifySchema);



        //回收核保通知书
        LZSysCertifySchema tLZSysCertifySchema = new LZSysCertifySchema();
        tLZSysCertifySchema.setCertifyCode("6665");
        tLZSysCertifySchema.setCertifyNo("86000020040810000103");
        tLZSysCertifySchema.setTakeBackOperator("001");
        tLZSysCertifySchema.setTakeBackDate("2004-03-31");
        tLZSysCertifySchema.setTakeBackMakeDate("2004-03-31");
        tLZSysCertifySchema.setSendOutCom("A86");
        tLZSysCertifySchema.setReceiveCom("D8611000001");
        // 准备传输数据 VData
        String tOperate = new String();
        TransferData tTransferData = new TransferData();
        mTransferData.setNameAndValue("CertifyNo", "86000020040810000103");
        mTransferData.setNameAndValue("CertifyCode", "6665");
        mTransferData.setNameAndValue("PolNo", "86110020040110000301");
        mTransferData.setNameAndValue("MissionID", "00000000000000000070");
        mTransferData.setNameAndValue("SubMissionID", "1");
        mTransferData.setNameAndValue("LZSysCertifySchema", tLZSysCertifySchema);

//补体检通知书
//    LOPRTManagerSchema tLOPRTManagerSchema = new LOPRTManagerSchema();
//    tLOPRTManagerSchema.setPrtSeq("86000020040810000080") ;
//	mTransferData.setNameAndValue("Code","43") ;
//	mTransferData.setNameAndValue("PolNo","86110020040110000301") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000070") ;
//	mTransferData.setNameAndValue("SubMissionID","1") ;
//    mTransferData.setNameAndValue("LOPRTManagerSchema",tLOPRTManagerSchema) ;
        //补打生调通知书
//    LOPRTManagerSchema tLOPRTManagerSchema = new LOPRTManagerSchema();
//    tLOPRTManagerSchema.setPrtSeq("86000020030810002081") ;
//	mTransferData.setNameAndValue("Code","44") ;
//	mTransferData.setNameAndValue("PolNo","86110020030210004500") ;
//	mTransferData.setNameAndValue("EdorNo","86110020030410000213") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000028") ;
//	mTransferData.setNameAndValue("SubMissionID","1") ;
//    mTransferData.setNameAndValue("LOPRTManagerSchema",tLOPRTManagerSchema) ;


//补核保通知书
//    LOPRTManagerSchema tLOPRTManagerSchema = new LOPRTManagerSchema();
//    tLOPRTManagerSchema.setPrtSeq("86000020040810000101") ;
//	mTransferData.setNameAndValue("Code","43") ;
//	mTransferData.setNameAndValue("PolNo","86110020040110000301") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000070") ;
//	mTransferData.setNameAndValue("SubMissionID","1") ;
//    mTransferData.setNameAndValue("LOPRTManagerSchema",tLOPRTManagerSchema) ;

//    //回收生调通知书
//	LCRReportSchema tLCRReportSchema = new LCRReportSchema();
//    tLCRReportSchema.setPolNo("86110020040110000301");
//    tLCRReportSchema.setSerialNo("0");
//    tLCRReportSchema.setReplyContente("回复内容:dfdfdfd");
//
//    //准备公共传输信息
//	mTransferData.setNameAndValue("PolNo","86110020040110000301");
//	mTransferData.setNameAndValue("PrtNo","200403290001") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000070");
//	mTransferData.setNameAndValue("PrtSeq","86000020040810000098");
//	mTransferData.setNameAndValue("SubMissionID","1");
//	mTransferData.setNameAndValue("LCRReportSchema",tLCRReportSchema);

//	//下核保结论
        LCUWMasterSchema tLCUWMasterSchema = new LCUWMasterSchema();
        tLCUWMasterSchema.setProposalNo("86110020040110000301");
        tLCUWMasterSchema.setUWIdea("延期承保");
        tLCUWMasterSchema.setPostponeDay("2天");
        tLCUWMasterSchema.setPassFlag("2");

        // 准备传输工作流数据 VData
        mTransferData.setNameAndValue("PolNo", "86110020040110000301");
        mTransferData.setNameAndValue("PrtNo", "200403290001");
        mTransferData.setNameAndValue("AppUser", null);
        mTransferData.setNameAndValue("MissionID", "00000000000000000070");
        mTransferData.setNameAndValue("SubMissionID", "2");
        mTransferData.setNameAndValue("LCUWMasterSchema", tLCUWMasterSchema);

//	VData tVData3 = new VData();
//    mTransferData.setNameAndValue("PolNo","86110020030210035008");
//    mTransferData.setNameAndValue("EdorNo","86110020040410000039") ;
//    mTransferData.setNameAndValue("MissionID","00000000000000000023");
//    mTransferData.setNameAndValue("SubMissionID","1");


        /**总变量*/
        tVData.add(mGlobalInput);
        tVData.add(mTransferData);

        PRnewManuUWWorkFlowUI tPRnewManuUWWorkFlowUI = new PRnewManuUWWorkFlowUI();
        try
        {
            if (tPRnewManuUWWorkFlowUI.submitData(tVData, "0000000110"))
            {
                VData tResult = new VData();
                //tResult = tActivityOperator.getResult() ;
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

        PRnewManuUWWorkFlowBL tPRnewManuUWWorkFlowBL = new PRnewManuUWWorkFlowBL();

        System.out.println("---PRnewManuUWWorkFlowUI UI BEGIN---");
        if (tPRnewManuUWWorkFlowBL.submitData(cInputData, mOperate) == false)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tPRnewManuUWWorkFlowBL.mErrors);
//	  CError tError = new CError();
//	  tError.moduleName = "PRnewManuUWWorkFlowUI";
//	  tError.functionName = "submitData";
//	  tError.errorMessage = "续保人工核保工作流任务执行处理失败!";
//	  this.mErrors .addOneError(tError) ;
            mResult.clear();
            return false;
        }
        return true;
    }
}
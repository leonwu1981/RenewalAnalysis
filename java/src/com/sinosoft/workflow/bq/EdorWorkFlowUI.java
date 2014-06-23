package com.sinosoft.workflow.bq;

import java.sql.*;
import com.sinosoft.utility.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.tb.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.workflow.bq.*;
import com.sinosoft.workflowengine.*;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.vdb.*;
import com.sinosoft.lis.pubfun.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * ReWrite ZhangRong
 * @version 1.0
 */

public class EdorWorkFlowUI
{

  /** 错误处理类，每个需要错误处理的类中都放置该类 */
  public  CErrors mErrors = new CErrors();
  /** 往后面传输数据的容器 */
  private VData mInputData = new VData();
  /** 往界面传输数据的容器 */
  private VData mResult = new VData();
  /** 数据操作字符串 */
  private String mOperate;

  public EdorWorkFlowUI() {}
  public static void main(String[] args) {
	VData tVData = new VData();
	GlobalInput mGlobalInput = new GlobalInput();
	TransferData mTransferData = new TransferData();

	/** 全局变量 */
	mGlobalInput.Operator ="001";
	mGlobalInput.ComCode  ="86";
	mGlobalInput.ManageCom="86";
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

//	//发体检通知书测试
	LPPENoticeSchema tLPPENoticeSchema = new LPPENoticeSchema();
	tLPPENoticeSchema.setEdorAcceptNo("86000000000228");
	tLPPENoticeSchema.setPEAddress("11003001");
	tLPPENoticeSchema.setPEDate("2003-07-01");
	tLPPENoticeSchema.setPEBeforeCond("N");
	tLPPENoticeSchema.setRemark("ReMark");
	tLPPENoticeSchema.setCustomerNo("0000318860");
	mTransferData.setNameAndValue("LPPENoticeSchema",tLPPENoticeSchema);

	LPPENoticeItemSet tLPPENoticeItemSet = new LPPENoticeItemSet();
	LPPENoticeItemSchema tLPPENoticeItemSchema = new LPPENoticeItemSchema();
	tLPPENoticeItemSchema.setEdorAcceptNo("86000000000228");
	tLPPENoticeItemSchema.setPEItemCode( "001");
	tLPPENoticeItemSchema.setPEItemName( "普检");
	tLPPENoticeItemSchema.setFreePE( "N");

	LPPENoticeItemSchema tLPPENoticeItemSchema2 = new LPPENoticeItemSchema();
	tLPPENoticeItemSchema2.setEdorNo("86000000000228");
	tLPPENoticeItemSchema2.setPEItemCode( "002");
	tLPPENoticeItemSchema2.setPEItemName( "尿常规");
	tLPPENoticeItemSchema2.setFreePE( "N");

	tLPPENoticeItemSet.add( tLPPENoticeItemSchema );
	tLPPENoticeItemSet.add( tLPPENoticeItemSchema2 );
	mTransferData.setNameAndValue("LPPENoticeItemSet",tLPPENoticeItemSet);
    mTransferData.setNameAndValue("EdorAcceptNo","86000000000228") ;
    mTransferData.setNameAndValue("MissionID","00000000000000004874");
	mTransferData.setNameAndValue("SubMissionID","1");
    mTransferData.setNameAndValue("InsuredNo","0000318860");
    //特约录入测试
//	LPSpecSchema tLPSpecSchema = new LPSpecSchema();
//	tLPSpecSchema.setPolNo("86110020030210009299");
//	tLPSpecSchema.setEndorsementNo("86110020030410000126");
//	tLPSpecSchema.setPolType("1");
//	tLPSpecSchema.setSpecContent("特约内容");
//	tLPSpecSchema.setSpecType("1");
//	tLPSpecSchema.setSpecCode("1");
//
//	mTransferData.setNameAndValue("PolNo","86110020030210009299");
//	mTransferData.setNameAndValue("EdorNo","86110020030410000126") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000014");
//	mTransferData.setNameAndValue("SpecReason","特约原因");
//	mTransferData.setNameAndValue("LPSpecSchema",tLPSpecSchema);

//	//加费录入测试
//	LPPremSet tLPPremSet = new LPPremSet();
//	LPPremSchema tLPPremSchema = new LPPremSchema();
//    tLPPremSchema.setPolNo("86110020030210034999");
//    tLPPremSchema.setEdorNo("86110020040410000054");
//	tLPPremSchema.setEdorType("AC");//保全加费
//	tLPPremSchema.setDutyCode("223001");
//	//tLPPremSchema.setPayPlanCode("00000001");//加费
//	tLPPremSchema.setPayStartDate("2003-03-18");
//	tLPPremSchema.setPayPlanType( "1");
//	tLPPremSchema.setPayEndDate( "2008-03-18");
//	tLPPremSchema.setPrem( 50);
//	tLPPremSet.add( tLPPremSchema );
//
//	LPPremSchema tLPPremSchema2 = new LPPremSchema();
//	tLPPremSchema2.setPolNo("86110020030210034999");
//	tLPPremSchema2.setEdorNo("86110020040410000054");
//	tLPPremSchema2.setEdorType("AC");//保全加费
//	tLPPremSchema2.setDutyCode("223001");
//	//tLPPremSchema2.setPayPlanCode("00000002");//加费
//	tLPPremSchema2.setPayStartDate("2003-03-18");
//	tLPPremSchema2.setPayPlanType( "2");
//	tLPPremSchema2.setPayEndDate( "2008-03-18");
//	tLPPremSchema2.setPrem( 50);
//    tLPPremSet.add( tLPPremSchema2 );
//
//	mTransferData.setNameAndValue("PolNo","86110020030210034999");
//	mTransferData.setNameAndValue("PolNo2","86110020030210034999");
//	mTransferData.setNameAndValue("EdorNo","86110020040410000054") ;
//	mTransferData.setNameAndValue("EdorType","AC") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000037");
//	mTransferData.setNameAndValue("SubMissionID","1");
//	mTransferData.setNameAndValue("AddReason","加费原因");
//	mTransferData.setNameAndValue("LPPremSet",tLPPremSet);

//	//生调通知书测试
//	LPRReportSchema tLPRReportSchema = new LPRReportSchema();
//	tLPRReportSchema.setPolNo("86110020030210001677");
//    tLPRReportSchema.setContente("生调内容");
//
//	mTransferData.setNameAndValue("PolNo","86110020030210001677");
//	mTransferData.setNameAndValue("EdorNo","86110020030410000129") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000015");
//	mTransferData.setNameAndValue("LPRReportSchema",tLPRReportSchema);


//    //发送核保通知书
//	mTransferData.setNameAndValue("PolNo","86110020030210009537");
//	mTransferData.setNameAndValue("EdorNo","86110020030410000211") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000027");
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
//	//打印核保通知书
//	mTransferData.setNameAndValue("PrtSeq","86000020030810002053");
//	mTransferData.setNameAndValue("Code","23") ;
//	mTransferData.setNameAndValue("PolNo","86110020030210001092") ;
//	mTransferData.setNameAndValue("EdorNo","86110020030410000200") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000023") ;
//    mTransferData.setNameAndValue("SubMissionID","1") ;

	//	//打印生调通知书
//	mTransferData.setNameAndValue("PrtSeq","86000020030810002081");
//	mTransferData.setNameAndValue("Code","24") ;
//	mTransferData.setNameAndValue("PolNo","86110020030210004500") ;
//	mTransferData.setNameAndValue("EdorNo","86110020030410000213") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000028") ;
//	mTransferData.setNameAndValue("SubMissionID","1") ;


    //回收体检通知书
//	  LZSysCertifySchema tLZSysCertifySchema = new LZSysCertifySchema();
//	   tLZSysCertifySchema.setCertifyCode( "7773" );
//	   tLZSysCertifySchema.setCertifyNo( "86000020030810002085" );
//	   tLZSysCertifySchema.setTakeBackOperator( "001" );
//	   tLZSysCertifySchema.setTakeBackDate( "2003-12-17" );
//	   tLZSysCertifySchema.setTakeBackMakeDate( "2003-12-17" );
//	   tLZSysCertifySchema.setSendOutCom( "A86" );
//	   tLZSysCertifySchema.setReceiveCom( "D8611000091" );
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
  //补体检通知书
//    LOPRTManagerSchema tLOPRTManagerSchema = new LOPRTManagerSchema();
//    tLOPRTManagerSchema.setPrtSeq("86000020030810002060") ;
//	mTransferData.setNameAndValue("Code","23") ;
//	mTransferData.setNameAndValue("PolNo","86110020030210018153") ;
//	mTransferData.setNameAndValue("EdorNo","86110020030410000202") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000025") ;
//	mTransferData.setNameAndValue("SubMissionID","2") ;
//    mTransferData.setNameAndValue("LOPRTManagerSchema",tLOPRTManagerSchema) ;
       //补打生调通知书
//    LOPRTManagerSchema tLOPRTManagerSchema = new LOPRTManagerSchema();
//    tLOPRTManagerSchema.setPrtSeq("86000020030810002081") ;
//	mTransferData.setNameAndValue("Code","24") ;
//	mTransferData.setNameAndValue("PolNo","86110020030210004500") ;
//	mTransferData.setNameAndValue("EdorNo","86110020030410000213") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000028") ;
//	mTransferData.setNameAndValue("SubMissionID","1") ;
//    mTransferData.setNameAndValue("LOPRTManagerSchema",tLOPRTManagerSchema) ;

//    //回收生调通知书
//	LPRReportSchema tLPRReportSchema = new LPRReportSchema();
//    tLPRReportSchema.setPolNo("86110020030210004500");
//    tLPRReportSchema.setSerialNo("0");
//    tLPRReportSchema.setReplyContente("回复内容:dfdfdfd");
//
//    //准备公共传输信息
//	mTransferData.setNameAndValue("PolNo","86110020030210004500");
//	mTransferData.setNameAndValue("EdorNo","86110020030410000213") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000028");
//	mTransferData.setNameAndValue("PrtSeq","86000020030810002084");
//	mTransferData.setNameAndValue("SubMissionID","3");
//	mTransferData.setNameAndValue("LPRReportSchema",tLPRReportSchema);

//	//下核保结论
//	LPUWMasterMainSchema tLPUWMasterMainSchema = new LPUWMasterMainSchema();
//    tLPUWMasterMainSchema.setPolNo("86110020030210001115");
//    tLPUWMasterMainSchema.setEdorNo("86110020040410000073");
//    tLPUWMasterMainSchema.setUWIdea("延期承保");
//	tLPUWMasterMainSchema.setPostponeDay("2") ;
//    tLPUWMasterMainSchema.setPassFlag("2");
//
//  // 准备传输工作流数据 VData
//    mTransferData.setNameAndValue("EdorAcceptNo","86000000000192");
//    mTransferData.setNameAndValue("MissionID","00000000000040004072");
//    mTransferData.setNameAndValue("SubMissionID","1");
//    mTransferData.setNameAndValue("LPUWMasterMainSchema",tLPUWMasterMainSchema);

//	VData tVData3 = new VData();
//    mTransferData.setNameAndValue("PolNo","86110020030210035008");
//    mTransferData.setNameAndValue("EdorNo","86110020040410000039") ;
//    mTransferData.setNameAndValue("MissionID","00000000000000000023");
//    mTransferData.setNameAndValue("SubMissionID","1");


	/**总变量*/
	tVData.add(mGlobalInput) ;
	tVData.add(mTransferData) ;



	EdorWorkFlowUI tEdorWorkFlowUI = new EdorWorkFlowUI();
	try{
	  if(tEdorWorkFlowUI.submitData(tVData,"0000000019"))
	   {
		   VData  tResult = new VData();
	       //tResult = tActivityOperator.getResult() ;
	   }
	}
   catch (Exception ex) {
		ex.printStackTrace();
		 }

  }

  /**
  传输数据的公共方法
  */
  public boolean submitData(VData cInputData,String cOperate)
  {
	//将操作数据拷贝到本类中
	this.mOperate = cOperate;

	EdorWorkFlowBL tEdorWorkFlowBL = new EdorWorkFlowBL();

	System.out.println("---EdorWorkFlowUI UI BEGIN---");
	if (!tEdorWorkFlowBL.submitData(cInputData,mOperate))
	{
		  // @@错误处理
	  this.mErrors.copyAllErrors(tEdorWorkFlowBL.mErrors);
//	  CError tError = new CError();
//	  tError.moduleName = "EdorWorkFlowUI";
//	  tError.functionName = "submitData";
//	  tError.errorMessage = "保全人工核保工作流任务执行处理失败!";
//	  this.mErrors .addOneError(tError) ;
	  mResult.clear();
	  return false;
	}
        else
        {

        }
	return true;
  }





  /**
   * 传输数据的公共方法
   * @return VData
   */
  public VData getResult() {
    return mResult;
  }

}

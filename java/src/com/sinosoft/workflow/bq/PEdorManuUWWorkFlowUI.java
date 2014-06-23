/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.bq;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LPUWMasterMainSchema;
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

public class PEdorManuUWWorkFlowUI
{

    /** �������࣬ÿ����Ҫ�����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** �����洫�����ݵ����� */
//    private VData mInputData = new VData();
    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
    /** ���ݲ����ַ��� */
    private String mOperate;

    public PEdorManuUWWorkFlowUI()
    {}

    /**
       �������ݵĹ�������
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //���������ݿ�����������
        this.mOperate = cOperate;

        PEdorManuUWWorkFlowBL tPEdorManuUWWorkFlowBL = new PEdorManuUWWorkFlowBL();

        System.out.println("---PEdorManuUWWorkFlowUI UI BEGIN---");
        if (tPEdorManuUWWorkFlowBL.submitData(cInputData, mOperate))
        {
            return true;
        }
        else
        {
            // @@������
            this.mErrors.copyAllErrors(tPEdorManuUWWorkFlowBL.mErrors);
//	  CError tError = new CError();
//	  tError.moduleName = "PEdorManuUWWorkFlowUI";
//	  tError.functionName = "submitData";
//	  tError.errorMessage = "��ȫ�˹��˱�����������ִ�д���ʧ��!";
//	  this.mErrors .addOneError(tError) ;
            mResult.clear();
            return false;
        }
    }

    public static void main(String[] args)
    {
        VData tVData = new VData();
        GlobalInput mGlobalInput = new GlobalInput();
        TransferData mTransferData = new TransferData();

        /** ȫ�ֱ��� */
        mGlobalInput.Operator = "S001";
        mGlobalInput.ComCode = "86";
        mGlobalInput.ManageCom = "86";
        /** ���ݱ��� */
//	mTransferData.setNameAndValue("EdorNo","86110020030420000274") ;
//	mTransferData.setNameAndValue("PolNo","86110020030210001677");
//	mTransferData.setNameAndValue("RiskCode","111301");
//	mTransferData.setNameAndValue("RiskName","11130����");
//	mTransferData.setNameAndValue("InsuredNo","0000000139");
//	mTransferData.setNameAndValue("InsuredName","��ʿ��");
//	mTransferData.setNameAndValue("AppntNo","0000000139");
//	mTransferData.setNameAndValue("AppntName","��ʿ��");

//	mTransferData.setNameAndValue("InsuredNo","0000010748");

//	//�����֪ͨ�����
//	LPPENoticeSchema tLPPENoticeSchema = new LPPENoticeSchema();
//	tLPPENoticeSchema.setEdorNo("86110020040410000009");
//	tLPPENoticeSchema.setPolNo("86110020030210001687");
//	tLPPENoticeSchema.setPEAddress("11003001");
//	tLPPENoticeSchema.setPEDate("2003-07-01");
//	tLPPENoticeSchema.setPEBeforeCond("N");
//	tLPPENoticeSchema.setRemark("ReMark");
//	tLPPENoticeSchema.setInsuredNo("0000002456");
//	mTransferData.setNameAndValue("LPPENoticeSchema",tLPPENoticeSchema);
//
//	LPPENoticeItemSet tLPPENoticeItemSet = new LPPENoticeItemSet();
//	LPPENoticeItemSchema tLPPENoticeItemSchema = new LPPENoticeItemSchema();
//	tLPPENoticeItemSchema.setEdorNo("86110020040410000009");
//	tLPPENoticeItemSchema.setPolNo("86110020030210001687");
//	tLPPENoticeItemSchema.setInsuredNo("0000002456") ;
//	tLPPENoticeItemSchema.setPEItemCode( "001");
//	tLPPENoticeItemSchema.setPEItemName( "�ռ츹��B��");
//	tLPPENoticeItemSchema.setFreePE( "N");

//	LPPENoticeItemSchema tLPPENoticeItemSchema2 = new LPPENoticeItemSchema();
//	tLPPENoticeItemSchema2.setEdorNo("86110020040410000009");
//	tLPPENoticeItemSchema2.setPolNo("86110020030210001687");
//	tLPPENoticeItemSchema2.setInsuredNo("0000002456") ;
//	tLPPENoticeItemSchema2.setPEItemCode( "002");
//	tLPPENoticeItemSchema2.setPEItemName( "�ռ츹��B��");
//	tLPPENoticeItemSchema2.setFreePE( "N");

//	tLPPENoticeItemSet.add( tLPPENoticeItemSchema );
//	tLPPENoticeItemSet.add( tLPPENoticeItemSchema2 );
//	mTransferData.setNameAndValue("LPPENoticeItemSet",tLPPENoticeItemSet);
//	mTransferData.setNameAndValue("PolNo","86110020030210001687");
//    mTransferData.setNameAndValue("EdorNo","86110020040410000009") ;
//    mTransferData.setNameAndValue("MissionID","00000000000000000009");
//	mTransferData.setNameAndValue("SubMissionID","1");
//    mTransferData.setNameAndValue("InsuredNo","0000002456");
        //��Լ¼�����
//	LPSpecSchema tLPSpecSchema = new LPSpecSchema();
//	tLPSpecSchema.setPolNo("86110020030210009299");
//	tLPSpecSchema.setEndorsementNo("86110020030410000126");
//	tLPSpecSchema.setPolType("1");
//	tLPSpecSchema.setSpecContent("��Լ����");
//	tLPSpecSchema.setSpecType("1");
//	tLPSpecSchema.setSpecCode("1");
//
//	mTransferData.setNameAndValue("PolNo","86110020030210009299");
//	mTransferData.setNameAndValue("EdorNo","86110020030410000126") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000014");
//	mTransferData.setNameAndValue("SpecReason","��Լԭ��");
//	mTransferData.setNameAndValue("LPSpecSchema",tLPSpecSchema);

//	//�ӷ�¼�����
//	LPPremSet tLPPremSet = new LPPremSet();
//	LPPremSchema tLPPremSchema = new LPPremSchema();
//    tLPPremSchema.setPolNo("86110020030210034999");
//    tLPPremSchema.setEdorNo("86110020040410000054");
//	tLPPremSchema.setEdorType("AC");//��ȫ�ӷ�
//	tLPPremSchema.setDutyCode("223001");
//	//tLPPremSchema.setPayPlanCode("00000001");//�ӷ�
//	tLPPremSchema.setPayStartDate("2003-03-18");
//	tLPPremSchema.setPayPlanType( "1");
//	tLPPremSchema.setPayEndDate( "2008-03-18");
//	tLPPremSchema.setPrem( 50);
//	tLPPremSet.add( tLPPremSchema );
//
//	LPPremSchema tLPPremSchema2 = new LPPremSchema();
//	tLPPremSchema2.setPolNo("86110020030210034999");
//	tLPPremSchema2.setEdorNo("86110020040410000054");
//	tLPPremSchema2.setEdorType("AC");//��ȫ�ӷ�
//	tLPPremSchema2.setDutyCode("223001");
//	//tLPPremSchema2.setPayPlanCode("00000002");//�ӷ�
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
//	mTransferData.setNameAndValue("AddReason","�ӷ�ԭ��");
//	mTransferData.setNameAndValue("LPPremSet",tLPPremSet);

//	//����֪ͨ�����
//	LPRReportSchema tLPRReportSchema = new LPRReportSchema();
//	tLPRReportSchema.setPolNo("86110020030210001677");
//    tLPRReportSchema.setContente("��������");
//
//	mTransferData.setNameAndValue("PolNo","86110020030210001677");
//	mTransferData.setNameAndValue("EdorNo","86110020030410000129") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000015");
//	mTransferData.setNameAndValue("LPRReportSchema",tLPRReportSchema);


//    //���ͺ˱�֪ͨ��
//	mTransferData.setNameAndValue("PolNo","86110020030210009537");
//	mTransferData.setNameAndValue("EdorNo","86110020030410000211") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000027");
//	mTransferData.setNameAndValue("SubMissionID","1");
//
//
        //׼�����乤�������� VData
//	 LPUWMasterMainSchema mLPUWMasterMainSchema = new LPUWMasterMainSchema();
//	 mLPUWMasterMainSchema.setPolNo("86110020030210009299");
//	 mLPUWMasterMainSchema.setEdorNo("86110020030410000126");
//	 mLPUWMasterMainSchema.setUWIdea("�����б�!");
//	 mLPUWMasterMainSchema.setPassFlag("9");
//	 mLPUWMasterMainSchema.setAppGrade("");
//
        // ׼�����乤�������� VData
//	mTransferData.setNameAndValue("PolNo","86110020030210001017");
//	mTransferData.setNameAndValue("EdorNo","86110020030410000195");
//	mTransferData.setNameAndValue("MissionID","00000000000000000017");
        //mTransferData.setNameAndValue("LPUWMasterMainSchema",mLPUWMasterMainSchema);
//
//	//��ӡ�˱�֪ͨ��
//	mTransferData.setNameAndValue("PrtSeq","86000020030810002053");
//	mTransferData.setNameAndValue("Code","23") ;
//	mTransferData.setNameAndValue("PolNo","86110020030210001092") ;
//	mTransferData.setNameAndValue("EdorNo","86110020030410000200") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000023") ;
//    mTransferData.setNameAndValue("SubMissionID","1") ;

        //	//��ӡ����֪ͨ��
//	mTransferData.setNameAndValue("PrtSeq","86000020030810002081");
//	mTransferData.setNameAndValue("Code","24") ;
//	mTransferData.setNameAndValue("PolNo","86110020030210004500") ;
//	mTransferData.setNameAndValue("EdorNo","86110020030410000213") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000028") ;
//	mTransferData.setNameAndValue("SubMissionID","1") ;


        //�������֪ͨ��
//	  LZSysCertifySchema tLZSysCertifySchema = new LZSysCertifySchema();
//	   tLZSysCertifySchema.setCertifyCode( "7773" );
//	   tLZSysCertifySchema.setCertifyNo( "86000020030810002085" );
//	   tLZSysCertifySchema.setTakeBackOperator( "001" );
//	   tLZSysCertifySchema.setTakeBackDate( "2003-12-17" );
//	   tLZSysCertifySchema.setTakeBackMakeDate( "2003-12-17" );
//	   tLZSysCertifySchema.setSendOutCom( "A86" );
//	   tLZSysCertifySchema.setReceiveCom( "D8611000091" );
//
//	 // ׼���������� VData
//	   String tOperate = new String();
//	   TransferData tTransferData = new TransferData();
//	   tTransferData.setNameAndValue("CertifyNo","86000020030810002085");
//	   tTransferData.setNameAndValue("CertifyCode","7773") ;
//	   tTransferData.setNameAndValue("PolNo","86110020030210004500") ;
//	   tTransferData.setNameAndValue("EdorNo","86110020030410000213") ;
//	   tTransferData.setNameAndValue("MissionID","00000000000000000028") ;
//	   tTransferData.setNameAndValue("SubMissionID","1") ;
//	   tTransferData.setNameAndValue("LZSysCertifySchema",tLZSysCertifySchema);
        //�����֪ͨ��
//    LOPRTManagerSchema tLOPRTManagerSchema = new LOPRTManagerSchema();
//    tLOPRTManagerSchema.setPrtSeq("86000020030810002060") ;
//	mTransferData.setNameAndValue("Code","23") ;
//	mTransferData.setNameAndValue("PolNo","86110020030210018153") ;
//	mTransferData.setNameAndValue("EdorNo","86110020030410000202") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000025") ;
//	mTransferData.setNameAndValue("SubMissionID","2") ;
//    mTransferData.setNameAndValue("LOPRTManagerSchema",tLOPRTManagerSchema) ;
        //��������֪ͨ��
//    LOPRTManagerSchema tLOPRTManagerSchema = new LOPRTManagerSchema();
//    tLOPRTManagerSchema.setPrtSeq("86000020030810002081") ;
//	mTransferData.setNameAndValue("Code","24") ;
//	mTransferData.setNameAndValue("PolNo","86110020030210004500") ;
//	mTransferData.setNameAndValue("EdorNo","86110020030410000213") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000028") ;
//	mTransferData.setNameAndValue("SubMissionID","1") ;
//    mTransferData.setNameAndValue("LOPRTManagerSchema",tLOPRTManagerSchema) ;

//    //��������֪ͨ��
//	LPRReportSchema tLPRReportSchema = new LPRReportSchema();
//    tLPRReportSchema.setPolNo("86110020030210004500");
//    tLPRReportSchema.setSerialNo("0");
//    tLPRReportSchema.setReplyContente("�ظ�����:dfdfdfd");
//
//    //׼������������Ϣ
//	mTransferData.setNameAndValue("PolNo","86110020030210004500");
//	mTransferData.setNameAndValue("EdorNo","86110020030410000213") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000028");
//	mTransferData.setNameAndValue("PrtSeq","86000020030810002084");
//	mTransferData.setNameAndValue("SubMissionID","3");
//	mTransferData.setNameAndValue("LPRReportSchema",tLPRReportSchema);

//	//�º˱�����
        LPUWMasterMainSchema tLPUWMasterMainSchema = new LPUWMasterMainSchema();
//    tLPUWMasterMainSchema.setPolNo("86110020030210001115");
        tLPUWMasterMainSchema.setEdorNo("86110020040410000073");
        tLPUWMasterMainSchema.setUWIdea("���ڳб�");
        tLPUWMasterMainSchema.setPostponeDay("2");
        tLPUWMasterMainSchema.setPassFlag("2");
//
//  // ׼�����乤�������� VData
        mTransferData.setNameAndValue("PolNo", "86110020030210001115");
        mTransferData.setNameAndValue("EdorNo", "86110020040410000073");
        mTransferData.setNameAndValue("AppUser", null);
        mTransferData.setNameAndValue("MissionID", "00000000000000000055");
        mTransferData.setNameAndValue("SubMissionID", "1");
        mTransferData.setNameAndValue("LPUWMasterMainSchema", tLPUWMasterMainSchema);

//	VData tVData3 = new VData();
//    mTransferData.setNameAndValue("PolNo","86110020030210035008");
//    mTransferData.setNameAndValue("EdorNo","86110020040410000039") ;
//    mTransferData.setNameAndValue("MissionID","00000000000000000023");
//    mTransferData.setNameAndValue("SubMissionID","1");


        /**�ܱ���*/
        tVData.add(mGlobalInput);
        tVData.add(mTransferData);

        PEdorManuUWWorkFlowUI tPEdorManuUWWorkFlowUI = new PEdorManuUWWorkFlowUI();
        try
        {
            if (tPEdorManuUWWorkFlowUI.submitData(tVData, "0000000010"))
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
}
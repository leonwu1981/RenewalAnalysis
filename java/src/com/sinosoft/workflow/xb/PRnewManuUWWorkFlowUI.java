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

    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** �����洫�����ݵ����� */
    private VData mInputData = new VData();
    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
    /** ���ݲ����ַ��� */
    private String mOperate;

    public PRnewManuUWWorkFlowUI()
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

        //ִ����ʼ�ڵ�
//	VData tVData3 = new VData();
//	mTransferData.setNameAndValue("PolNo","86110020040110000301");
//	mTransferData.setNameAndValue("PrtNo","200403290001") ;
//	mTransferData.setNameAndValue("ProposalNo","86110020040110000301");
//	mTransferData.setNameAndValue("MissionID","00000000000000000070");
//	mTransferData.setNameAndValue("SubMissionID","1");
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

        //�����֪ͨ�����
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
//	tLCPENoticeItemSchema.setPEItemName( "�ռ츹��B��");
//	tLCPENoticeItemSchema.setFreePE( "N");
//
//	LCPENoticeItemSchema tLCPENoticeItemSchema2 = new LCPENoticeItemSchema();
//	tLCPENoticeItemSchema2.setProposalNo("86110020040110000301");
//	tLCPENoticeItemSchema2.setInsuredNo("0000030835") ;
//	tLCPENoticeItemSchema2.setPEItemCode( "002");
//	tLCPENoticeItemSchema2.setPEItemName( "�ռ츹��B��");
//	tLCPENoticeItemSchema2.setFreePE( "N");

//	tLCPENoticeItemSet.add( tLCPENoticeItemSchema );
//	tLCPENoticeItemSet.add( tLCPENoticeItemSchema2 );
//	mTransferData.setNameAndValue("LCPENoticeItemSet",tLCPENoticeItemSet);
//	mTransferData.setNameAndValue("PolNo","86110020040110000301");
//    mTransferData.setNameAndValue("MissionID","00000000000000000070");
//	mTransferData.setNameAndValue("SubMissionID","1");
//    mTransferData.setNameAndValue("InsuredNo","0000030835");
        //��Լ¼�����
//	LCSpecSchema tLCSpecSchema = new LCSpecSchema();
//	tLCSpecSchema.setPolNo("86110020040110000301");
//	tLCSpecSchema.setPolType("1");
//	tLCSpecSchema.setSpecContent("��Լ����");
//	tLCSpecSchema.setSpecType("1");
//	tLCSpecSchema.setSpecCode("1");
//	mTransferData.setNameAndValue("Remark","tRemark");
//	mTransferData.setNameAndValue("PolNo","86110020040110000301");
//	mTransferData.setNameAndValue("PrtNo","200403290001") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000070");
//	mTransferData.setNameAndValue("SpecReason","my��Լԭ��");
//	mTransferData.setNameAndValue("LCSpecSchema",tLCSpecSchema);
//	mTransferData.setNameAndValue("SubMissionID","1");
//	//�ӷ�¼�����
//	LCPremSet tLCPremSet = new LCPremSet();
//	LCPremSchema tLCPremSchema = new LCPremSchema();
//    tLCPremSchema.setPolNo("86110020040110000301");
//	tLCPremSchema.setDutyCode("205001");
//	//tLPPremSchema.setPayPlanCode("00000001");//�ӷ�
//	tLCPremSchema.setPayStartDate("2004-03-29");
//	tLCPremSchema.setPayPlanType( "1");
//	tLCPremSchema.setPayEndDate( "2049-03-29");
//	tLCPremSchema.setPrem( 50);
//	tLCPremSet.add( tLCPremSchema );
//
//	LCPremSchema tLCPremSchema2 = new LCPremSchema();
//	tLCPremSchema2.setPolNo("86110020040110000301");
//	tLCPremSchema2.setDutyCode("205001");
//	//tLPPremSchema2.setPayPlanCode("00000002");//�ӷ�
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
//	mTransferData.setNameAndValue("AddReason","�ӷ�ԭ��");
//	mTransferData.setNameAndValue("LCPremSet",tLCPremSet);

//	//����֪ͨ�����
//	LCRReportSchema tLCRReportSchema = new LCRReportSchema();
//	tLCRReportSchema.setPolNo("86110020040110000301");
//    tLCRReportSchema.setContente("�ҵ��������ݣ�����");
//
//	mTransferData.setNameAndValue("PolNo","86110020040110000301");
//	mTransferData.setNameAndValue("PrtNo","200403290001") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000070");
//	mTransferData.setNameAndValue("SubMissionID","1");
//	mTransferData.setNameAndValue("LCRReportSchema",tLCRReportSchema);


//    //���ͺ˱�֪ͨ��
//	mTransferData.setNameAndValue("PolNo","86110020040110000301");
//	mTransferData.setNameAndValue("PrtNo","200403290001") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000070");
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

//	//��ӡ���֪ͨ��
//	mTransferData.setNameAndValue("PrtSeq","86000020040810000080");
//	mTransferData.setNameAndValue("Code","43") ;
//	mTransferData.setNameAndValue("PolNo","86110020040110000301") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000070") ;
//    mTransferData.setNameAndValue("SubMissionID","1") ;

//	//��ӡ�˱�֪ͨ��
//	mTransferData.setNameAndValue("PrtSeq","86000020040810000101");
//	mTransferData.setNameAndValue("Code","45") ;
//	mTransferData.setNameAndValue("PolNo","86110020040110000301") ;
//	mTransferData.setNameAndValue("PrtNo","200403290001") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000070") ;
//    mTransferData.setNameAndValue("SubMissionID","1") ;

        //	//��ӡ����֪ͨ��
//	mTransferData.setNameAndValue("PrtSeq","86000020040810000098");
//	mTransferData.setNameAndValue("Code","44") ;
//	mTransferData.setNameAndValue("PolNo","86110020040110000301") ;
//	mTransferData.setNameAndValue("PrtNo","200403290001") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000070") ;
//	mTransferData.setNameAndValue("SubMissionID","1") ;


        //�������֪ͨ��
//	  LZSysCertifySchema tLZSysCertifySchema = new LZSysCertifySchema();
//	   tLZSysCertifySchema.setCertifyCode( "6663" );
//	   tLZSysCertifySchema.setCertifyNo( "86000020040810000080" );
//	   tLZSysCertifySchema.setTakeBackOperator( "001" );
//	   tLZSysCertifySchema.setTakeBackDate( "2004-03-31" );
//	   tLZSysCertifySchema.setTakeBackMakeDate( "2004-03-31" );
//	   tLZSysCertifySchema.setSendOutCom( "A86" );
//	   tLZSysCertifySchema.setReceiveCom( "D8611000001" );

//	 // ׼���������� VData
//	   String tOperate = new String();
//	   TransferData tTransferData = new TransferData();
//	   mTransferData.setNameAndValue("CertifyNo","86000020040810000080");
//	   mTransferData.setNameAndValue("CertifyCode","6663") ;
//	   mTransferData.setNameAndValue("PolNo","86110020040110000301") ;
//	   mTransferData.setNameAndValue("MissionID","00000000000000000070") ;
//	   mTransferData.setNameAndValue("SubMissionID","1") ;
//	   mTransferData.setNameAndValue("LZSysCertifySchema",tLZSysCertifySchema);



        //���պ˱�֪ͨ��
        LZSysCertifySchema tLZSysCertifySchema = new LZSysCertifySchema();
        tLZSysCertifySchema.setCertifyCode("6665");
        tLZSysCertifySchema.setCertifyNo("86000020040810000103");
        tLZSysCertifySchema.setTakeBackOperator("001");
        tLZSysCertifySchema.setTakeBackDate("2004-03-31");
        tLZSysCertifySchema.setTakeBackMakeDate("2004-03-31");
        tLZSysCertifySchema.setSendOutCom("A86");
        tLZSysCertifySchema.setReceiveCom("D8611000001");
        // ׼���������� VData
        String tOperate = new String();
        TransferData tTransferData = new TransferData();
        mTransferData.setNameAndValue("CertifyNo", "86000020040810000103");
        mTransferData.setNameAndValue("CertifyCode", "6665");
        mTransferData.setNameAndValue("PolNo", "86110020040110000301");
        mTransferData.setNameAndValue("MissionID", "00000000000000000070");
        mTransferData.setNameAndValue("SubMissionID", "1");
        mTransferData.setNameAndValue("LZSysCertifySchema", tLZSysCertifySchema);

//�����֪ͨ��
//    LOPRTManagerSchema tLOPRTManagerSchema = new LOPRTManagerSchema();
//    tLOPRTManagerSchema.setPrtSeq("86000020040810000080") ;
//	mTransferData.setNameAndValue("Code","43") ;
//	mTransferData.setNameAndValue("PolNo","86110020040110000301") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000070") ;
//	mTransferData.setNameAndValue("SubMissionID","1") ;
//    mTransferData.setNameAndValue("LOPRTManagerSchema",tLOPRTManagerSchema) ;
        //��������֪ͨ��
//    LOPRTManagerSchema tLOPRTManagerSchema = new LOPRTManagerSchema();
//    tLOPRTManagerSchema.setPrtSeq("86000020030810002081") ;
//	mTransferData.setNameAndValue("Code","44") ;
//	mTransferData.setNameAndValue("PolNo","86110020030210004500") ;
//	mTransferData.setNameAndValue("EdorNo","86110020030410000213") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000028") ;
//	mTransferData.setNameAndValue("SubMissionID","1") ;
//    mTransferData.setNameAndValue("LOPRTManagerSchema",tLOPRTManagerSchema) ;


//���˱�֪ͨ��
//    LOPRTManagerSchema tLOPRTManagerSchema = new LOPRTManagerSchema();
//    tLOPRTManagerSchema.setPrtSeq("86000020040810000101") ;
//	mTransferData.setNameAndValue("Code","43") ;
//	mTransferData.setNameAndValue("PolNo","86110020040110000301") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000070") ;
//	mTransferData.setNameAndValue("SubMissionID","1") ;
//    mTransferData.setNameAndValue("LOPRTManagerSchema",tLOPRTManagerSchema) ;

//    //��������֪ͨ��
//	LCRReportSchema tLCRReportSchema = new LCRReportSchema();
//    tLCRReportSchema.setPolNo("86110020040110000301");
//    tLCRReportSchema.setSerialNo("0");
//    tLCRReportSchema.setReplyContente("�ظ�����:dfdfdfd");
//
//    //׼������������Ϣ
//	mTransferData.setNameAndValue("PolNo","86110020040110000301");
//	mTransferData.setNameAndValue("PrtNo","200403290001") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000070");
//	mTransferData.setNameAndValue("PrtSeq","86000020040810000098");
//	mTransferData.setNameAndValue("SubMissionID","1");
//	mTransferData.setNameAndValue("LCRReportSchema",tLCRReportSchema);

//	//�º˱�����
        LCUWMasterSchema tLCUWMasterSchema = new LCUWMasterSchema();
        tLCUWMasterSchema.setProposalNo("86110020040110000301");
        tLCUWMasterSchema.setUWIdea("���ڳб�");
        tLCUWMasterSchema.setPostponeDay("2��");
        tLCUWMasterSchema.setPassFlag("2");

        // ׼�����乤�������� VData
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


        /**�ܱ���*/
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
       �������ݵĹ�������
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //���������ݿ�����������
        this.mOperate = cOperate;

        PRnewManuUWWorkFlowBL tPRnewManuUWWorkFlowBL = new PRnewManuUWWorkFlowBL();

        System.out.println("---PRnewManuUWWorkFlowUI UI BEGIN---");
        if (tPRnewManuUWWorkFlowBL.submitData(cInputData, mOperate) == false)
        {
            // @@������
            this.mErrors.copyAllErrors(tPRnewManuUWWorkFlowBL.mErrors);
//	  CError tError = new CError();
//	  tError.moduleName = "PRnewManuUWWorkFlowUI";
//	  tError.functionName = "submitData";
//	  tError.errorMessage = "�����˹��˱�����������ִ�д���ʧ��!";
//	  this.mErrors .addOneError(tError) ;
            mResult.clear();
            return false;
        }
        return true;
    }
}
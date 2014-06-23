/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LCContSchema;
import com.sinosoft.lis.schema.LCPENoticeItemSchema;
import com.sinosoft.lis.schema.LCPENoticeSchema;
import com.sinosoft.lis.vschema.LCPENoticeItemSet;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;

/**
 * <p>Title:����Լ������ </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class TbWorkFlowUI
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** �����洫�����ݵ����� */
//    private VData mInputData = new VData();

    /** �����洫�����ݵ����� */
    private VData mResult = new VData();

    /** ���ݲ����ַ��� */
    private String mOperate;

    public TbWorkFlowUI()
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
        TbWorkFlowBL tTbWorkFlowBL = new TbWorkFlowBL();
//        System.out.println("---TbWorkFlowBL UI BEGIN---");
        if (tTbWorkFlowBL.submitData(cInputData, mOperate))
        {
            mResult = tTbWorkFlowBL.getResult();
            return true;
        }
        else
        {
            // @@������
            this.mErrors.copyAllErrors(tTbWorkFlowBL.mErrors);
            mResult.clear();
            return false;
        }
    }

    /**
     * �������ݵĹ�������
     * @return VData
     */
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
        VData tVData = new VData();
        GlobalInput mGlobalInput = new GlobalInput();
        TransferData mTransferData = new TransferData();

        /** ȫ�ֱ��� */
        mGlobalInput.Operator = "001";
        mGlobalInput.ComCode = "86";
        mGlobalInput.ManageCom = "86";
        //��������

        //������ʼ�ڵ�
//
//        mTransferData.setNameAndValue("PrtNo", "1980000321");
//        mTransferData.setNameAndValue("Operator", "001");
//        mTransferData.setNameAndValue("ManageCom", "86110000");
//        mTransferData.setNameAndValue("InputDate", "2004-12-10");
//        mTransferData.setNameAndValue("SubType", "TB01");

        //�Զ���ʼ��
//        mTransferData.setNameAndValue("ContNo","130110000013723");
//        mTransferData.setNameAndValue("PrtNo","19800000004");
//        mTransferData.setNameAndValue("ManageCom", "86110000");
//        mTransferData.setNameAndValue("MissionID","00000000000000005134");
//        mTransferData.setNameAndValue("SubMissionID","1");
//        mTransferData.setNameAndValue("PrtSeq","810000000000595");
//        //�������ӡȷ��
//        mTransferData.setNameAndValue("ContNo","130110000008969");
//        mTransferData.setNameAndValue("PrtNo","444444444");
//        mTransferData.setNameAndValue("MissionID","00000000000000000559");
//        mTransferData.setNameAndValue("SubMissionID","6");;


//        mTransferData.setNameAndValue("AppntNo", "0000314850");
//        mTransferData.setNameAndValue("AppntName", "zhangxing");
//        mTransferData.setNameAndValue("AgentCode", "8611000433");

//        mTransferData.setNameAndValue("MissionID", "00000000000000004978");
//        mTransferData.setNameAndValue("SubMissionID", "1");
//        mTransferData.setNameAndValue("UWFlag", "9");
//        mTransferData.setNameAndValue("UWIdea2", "9");

        //ִ����ʼ�ڵ�
//    mTransferData.setNameAndValue("ContNo", "00000000000000000006");
//    mTransferData.setNameAndValue("PrtNo", "00002004111207");
//    mTransferData.setNameAndValue("Operator", "001");
//    mTransferData.setNameAndValue("ApproveCode", "001");
//    mTransferData.setNameAndValue("MakeDate", "2004-11-22");
//    mTransferData.setNameAndValue("AppntNo", "0000031226");
//    mTransferData.setNameAndValue("AppntName", "����");
//    mTransferData.setNameAndValue("InsuredNo", "0000031226");
//    mTransferData.setNameAndValue("InsuredName", "����");
//    mTransferData.setNameAndValue("MissionID", "00000000000000000166");
//    mTransferData.setNameAndValue("SubMissionID", "1");
///** ���ݱ��� */
        //�Զ��˱�
//    LCContSchema tLCContSchema = new LCContSchema();
//    tLCContSchema.setContNo( "99999020040990000036" );
//    mTransferData.setNameAndValue("LCContSchema",tLCContSchema);
//    mTransferData.setNameAndValue("MissionID", "00000000000000000200");
//    mTransferData.setNameAndValue("SubMissionID", "1");

//        //�µ�����
//        mTransferData.setNameAndValue("ContNo", "130110000013815");
//        mTransferData.setNameAndValue("MissionID", "00000000000000005199");
//        mTransferData.setNameAndValue("SubMissionID", "1");

        //�����޸�
//    mTransferData.setNameAndValue("ContNo", "00000000000000000006");
//    mTransferData.setNameAndValue("MissionID", "00000000000000000166");
//    mTransferData.setNameAndValue("SubMissionID", "1");
//    �˱�����
//    mTransferData.setNameAndValue("ContNo", "130110000000139");
//    mTransferData.setNameAndValue("PrtNo", "21210000000009");
//    mTransferData.setNameAndValue("AgentCode", "8611000433");
//    mTransferData.setNameAndValue("MakeDate", "2004-11-22");
//    mTransferData.setNameAndValue("AppntNo", "0000031226");
//    mTransferData.setNameAndValue("AppntName", "����");
//    mTransferData.setNameAndValue("InsuredNo", "0000031226");
//    mTransferData.setNameAndValue("InsuredName", "����");
//    mTransferData.setNameAndValue("UWFlag", "a");
//    mTransferData.setNameAndValue("MissionID", "00000000000000000202");
//    mTransferData.setNameAndValue("SubMissionID", "4");

        /** ���ݱ��� */
//	mTransferData.setNameAndValue("InsuredNo","0000010748");
//	//�����֪ͨ�����
//	LCPENoticeSchema tLCPENoticeSchema = new LCPENoticeSchema();
//	tLCPENoticeSchema.setEdorNo("86110020040410000009");
//	tLCPENoticeSchema.setPolNo("86110020030210001687");
//	tLCPENoticeSchema.setPEAddress("11003001");
//	tLCPENoticeSchema.setPEDate("2003-07-01");
//	tLCPENoticeSchema.setPEBeforeCond("N");
//	tLCPENoticeSchema.setRemark("ReMark");
//	tLCPENoticeSchema.setInsuredNo("0000002456");
//	mTransferData.setNameAndValue("LCPENoticeSchema",tLCPENoticeSchema);
//
//	LCPENoticeItemSet tLCPENoticeItemSet = new LCPENoticeItemSet();
//	LCPENoticeItemSchema tLCPENoticeItemSchema = new LCPENoticeItemSchema();
//	tLCPENoticeItemSchema.setEdorNo("86110020040410000009");
//	tLCPENoticeItemSchema.setPolNo("86110020030210001687");
//	tLCPENoticeItemSchema.setInsuredNo("0000002456") ;
//	tLCPENoticeItemSchema.setPEItemCode( "001");
//	tLCPENoticeItemSchema.setPEItemName( "�ռ츹��B��");
//	tLCPENoticeItemSchema.setFreePE( "N");

//	LCPENoticeItemSchema tLCPENoticeItemSchema2 = new LCPENoticeItemSchema();
//	tLCPENoticeItemSchema2.setEdorNo("86110020040410000009");
//	tLCPENoticeItemSchema2.setPolNo("86110020030210001687");
//	tLCPENoticeItemSchema2.setInsuredNo("0000002456") ;
//	tLCPENoticeItemSchema2.setPEItemCode( "002");
//	tLCPENoticeItemSchema2.setPEItemName( "�ռ츹��B��");
//	tLCPENoticeItemSchema2.setFreePE( "N");

//	tLCPENoticeItemSet.add( tLCPENoticeItemSchema );
//	tLCPENoticeItemSet.add( tLCPENoticeItemSchema2 );
//	mTransferData.setNameAndValue("LCPENoticeItemSet",tLCPENoticeItemSet);
//	mTransferData.setNameAndValue("PolNo","86110020030210001687");
//    mTransferData.setNameAndValue("EdorNo","86110020040410000009") ;
//    mTransferData.setNameAndValue("MissionID","00000000000000000009");
//	mTransferData.setNameAndValue("SubMissionID","1");
//    mTransferData.setNameAndValue("InsuredNo","0000002456");

        //�����֪ͨ�����
        LCContSchema tLCContSchema = new LCContSchema();
        LCPENoticeSchema tLCPENoticeSchema = new LCPENoticeSchema();
        tLCContSchema.setContNo("130110000022786");
        tLCContSchema.setInsuredNo("0000543690");

        tLCPENoticeSchema.setContNo("130110000022786");
        tLCPENoticeSchema.setPEAddress("11003001");
        tLCPENoticeSchema.setPEDate("2003-07-01");
        tLCPENoticeSchema.setPEBeforeCond("N");
        tLCPENoticeSchema.setRemark("ReMark");
        tLCPENoticeSchema.setCustomerNo("0000031226");

        LCPENoticeItemSet tLCPENoticeItemSet = new LCPENoticeItemSet();
        LCPENoticeItemSchema tLCPENoticeItemSchema = new LCPENoticeItemSchema();
        tLCPENoticeItemSchema.setContNo("130110000022786");
        tLCPENoticeItemSchema.setPEItemCode("001");
        tLCPENoticeItemSchema.setPEItemName("�ռ츹��B��");
        tLCPENoticeItemSchema.setFreePE("N");
        tLCPENoticeItemSet.add(tLCPENoticeItemSchema);

//        LCPENoticeItemSchema tLCPENoticeItemSchema2 = new LCPENoticeItemSchema();
//        tLCPENoticeItemSchema2.setContNo("130110000022799");
//        tLCPENoticeItemSchema2.setPEItemCode("002");
//        tLCPENoticeItemSchema2.setPEItemName("�ռ츹��B��");
//        tLCPENoticeItemSchema2.setFreePE("N");
//        tLCPENoticeItemSet.add(tLCPENoticeItemSchema2);

        mTransferData.setNameAndValue("LCPENoticeItemSet", tLCPENoticeItemSet);
        mTransferData.setNameAndValue("LCPENoticeSchema", tLCPENoticeSchema);
        mTransferData.setNameAndValue("MissionID", "00000000000000007495");
        mTransferData.setNameAndValue("SubMissionID", "1");
        mTransferData.setNameAndValue("CustomerNo", "0000543690");
        mTransferData.setNameAndValue("ContNo", "130110000022786");

        //��ӡ���֪ͨ��
//    	mTransferData.setNameAndValue("PrtSeq","810000000000634");
//    	mTransferData.setNameAndValue("Code","85") ;
//    	mTransferData.setNameAndValue("ContNo","130110000008969") ;
//    	mTransferData.setNameAndValue("MissionID","00000000000000000559") ;
//        mTransferData.setNameAndValue("SubMissionID","1") ;
//        //�������֪ͨ��
//	  LZSysCertifySchema tLZSysCertifySchema = new LZSysCertifySchema();
//	   tLZSysCertifySchema.setCertifyCode( "7777" );
//	   tLZSysCertifySchema.setCertifyNo( "810000000000634" );
//	   tLZSysCertifySchema.setTakeBackOperator( "001" );
//	   tLZSysCertifySchema.setTakeBackDate( "2004-03-31" );
//	   tLZSysCertifySchema.setTakeBackMakeDate( "2004-03-31" );
//	   tLZSysCertifySchema.setSendOutCom( "A86" );
//	   tLZSysCertifySchema.setReceiveCom( "D86110484" );
//	   String tOperate = new String();
//	   TransferData tTransferData = new TransferData();
//	   mTransferData.setNameAndValue("CertifyNo","810000000000634");
//	   mTransferData.setNameAndValue("CertifyCode","7777") ;
//	   mTransferData.setNameAndValue("ContNo","130110000008969") ;
//	   mTransferData.setNameAndValue("MissionID","00000000000000000559") ;
//	   mTransferData.setNameAndValue("SubMissionID","1") ;
//	   mTransferData.setNameAndValue("LZSysCertifySchema",tLZSysCertifySchema);
//	//����֪ͨ�����
//	LCRReportSchema tLCRReportSchema = new LCRReportSchema();
//	tLCRReportSchema.setContNo("00000000000000000006");
//        tLCRReportSchema.setContente("�ҵ��������ݣ�����");
//	mTransferData.setNameAndValue("ContNo","130110000013699");
//	mTransferData.setNameAndValue("PrtNo","31231231231") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000005113");
//	mTransferData.setNameAndValue("CustomerNo","0000492380");
//       mTransferData.setNameAndValue("SubMissionID","1");
//	mTransferData.setNameAndValue("LCRReportSchema",tLCRReportSchema);
//��ӡ����֪ͨ��
//    mTransferData.setNameAndValue("PrtSeq", "810000000000654");
//    mTransferData.setNameAndValue("Code", "85");
//    mTransferData.setNameAndValue("ContNo", "130110000013771");
//    mTransferData.setNameAndValue("PrtNo", "000324005");
//    mTransferData.setNameAndValue("MissionID", "00000000000000005155");
//    mTransferData.setNameAndValue("SubMissionID", "2");
        //��������֪ͨ��
//    LOPRTManagerSchema tLOPRTManagerSchema = new LOPRTManagerSchema();
//    tLOPRTManagerSchema.setPrtSeq("86000020040810000157") ;
//	mTransferData.setNameAndValue("Code","04") ;
//	mTransferData.setNameAndValue("ContNo","00000000000000000006") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000028") ;
//	mTransferData.setNameAndValue("SubMissionID","2") ;
//    mTransferData.setNameAndValue("LOPRTManagerSchema",tLOPRTManagerSchema) ;
//    //��������֪ͨ��
//    mTransferData.setNameAndValue("ContNo", "00000000000000000006");
//    mTransferData.setNameAndValue("MissionID", "00000000000000000028");
//    mTransferData.setNameAndValue("SubMissionID", "1");
//    mTransferData.setNameAndValue("PrtNo", "00002004111207");
//    mTransferData.setNameAndValue("PrtSeq", "86000020040810000163");
//    LCRReportSchema tLCRReportSchema = new LCRReportSchema();
//    tLCRReportSchema.setContNo("00000000000000000006");
//    tLCRReportSchema.setPrtSeq("86000020040810000163");
//    tLCRReportSchema.setReplyContente("�ظ�����:dfdfdfd");
//    mTransferData.setNameAndValue("LCRReportSchema", tLCRReportSchema);
//	//�ӷ�¼�����
//	//����֪ͨ�����
        //���ͺ˱�֪ͨ��
//	mTransferData.setNameAndValue("ContNo","130110000013977");
//	mTransferData.setNameAndValue("PrtNo","uyiy98076") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000005348");
//	mTransferData.setNameAndValue("SubMissionID","1");
//    //�º˱�����
//    LCContSchema tLCContSchema = new LCContSchema();
//
//    LCCUWMasterSet tLCCUWMasterSet = new LCCUWMasterSet();
//    LCCUWMasterSchema tLCCUWMasterSchema = new LCCUWMasterSchema();
//
//    tLCContSchema.setContNo("130110000013940");
//    tLCContSchema.setUWFlag("2");
//    tLCContSchema.setRemark("ok");
//    tLCCUWMasterSchema.setContNo("130110000013940");
//tLCCUWMasterSchema.setPostponeDay(tvalidate);
//    tLCCUWMasterSchema.setUWIdea("1234");
//    tLCCUWMasterSchema.setSugPassFlag("2");
//    tLCCUWMasterSchema.setSugUWIdea("1234");
//
//    mTransferData.setNameAndValue("ContNo", "130110000013940");
//    mTransferData.setNameAndValue("PrtNo", "test09009");
//    mTransferData.setNameAndValue("UWFlag", "2");
//    mTransferData.setNameAndValue("UWIdea", "1234");
//    mTransferData.setNameAndValue("MissionID", "00000000000000005298");
//    mTransferData.setNameAndValue("SubMissionID", "1");

        //�������������
//        mTransferData.setNameAndValue("ContNo","130110000000175");
//        mTransferData.setNameAndValue("MissionID","00000000000000000220");
//        mTransferData.setNameAndValue("SubMissionID","1");

//	//��ӡ�˱�֪ͨ��
//	mTransferData.setNameAndValue("PrtSeq","810000000000611");
//	mTransferData.setNameAndValue("Code","04");
//	mTransferData.setNameAndValue("ContNo","130110000013726") ;
//	mTransferData.setNameAndValue("PrtNo","19800000012") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000005138") ;
//        mTransferData.setNameAndValue("SubMissionID","3") ;
        //���˱�֪ͨ��
//    LOPRTManagerSchema tLOPRTManagerSchema = new LOPRTManagerSchema();
//    tLOPRTManagerSchema.setPrtSeq("810000000000035") ;
//	mTransferData.setNameAndValue("Code","03") ;
//	mTransferData.setNameAndValue("ContNo","00000000000000000006") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000001") ;
//	mTransferData.setNameAndValue("SubMissionID","1") ;
//    mTransferData.setNameAndValue("LOPRTManagerSchema",tLOPRTManagerSchema) ;

        // ׼�����乤�������� VData
//	mTransferData.setNameAndValue("PolNo","86110020030210001017");
//	mTransferData.setNameAndValue("EdorNo","86110020030410000195");
//	mTransferData.setNameAndValue("MissionID","00000000000000000017");
        //mTransferData.setNameAndValue("LCUWMasterMainSchema",mLCUWMasterMainSchema);
//
        //���պ˱�֪ͨ��
//        LZSysCertifySchema tLZSysCertifySchema = new LZSysCertifySchema();
//         tLZSysCertifySchema.setCertifyCode( "9999" );
//         tLZSysCertifySchema.setCertifyNo( "86000020040810000190" );
//         tLZSysCertifySchema.setTakeBackOperator( "001" );
//         tLZSysCertifySchema.setTakeBackDate( "2004-12-01" );
//         tLZSysCertifySchema.setTakeBackMakeDate( "2004-12-01" );
//         tLZSysCertifySchema.setSendOutCom( "A86" );
//         tLZSysCertifySchema.setReceiveCom( "D8611000439 " );

//         String tOperate = new String();
//         TransferData tTransferData = new TransferData();
//         mTransferData.setNameAndValue("CertifyNo","86000020040810000190");
//         mTransferData.setNameAndValue("CertifyCode","9999") ;
//         mTransferData.setNameAndValue("ContNo","130110000000139") ;
//         mTransferData.setNameAndValue("MissionID","00000000000000000202") ;
//         mTransferData.setNameAndValue("SubMissionID","4") ;
//         mTransferData.setNameAndValue("LZSysCertifySchema",tLZSysCertifySchema);
//
//	//��ӡҵ��Ա֪ͨ��
//	mTransferData.setNameAndValue("PrtSeq","810000000000010");
//	mTransferData.setNameAndValue("Code","14") ;
//	mTransferData.setNameAndValue("ContNo","00000000000000000006") ;
//	mTransferData.setNameAndValue("PrtNo","00002004111207") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000001") ;
//        mTransferData.setNameAndValue("SubMissionID","1") ;
        //��ҵ��Ա֪ͨ��
//    LOPRTManagerSchema tLOPRTManagerSchema = new LOPRTManagerSchema();
//    tLOPRTManagerSchema.setPrtSeq("810000000000010") ;
//	mTransferData.setNameAndValue("Code","14") ;
//	mTransferData.setNameAndValue("ContNo","00000000000000000006") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000001") ;
//	mTransferData.setNameAndValue("SubMissionID","1") ;
//    mTransferData.setNameAndValue("LOPRTManagerSchema",tLOPRTManagerSchema) ;

        //����ҵ��Ա֪ͨ��
//        LZSysCertifySchema tLZSysCertifySchema = new LZSysCertifySchema();
//         tLZSysCertifySchema.setCertifyCode( "1113" );
//         tLZSysCertifySchema.setCertifyNo("810000000000611" );
//         tLZSysCertifySchema.setTakeBackOperator( "001" );
//         tLZSysCertifySchema.setTakeBackDate( "2004-03-31" );
//         tLZSysCertifySchema.setTakeBackMakeDate( "2004-03-31" );
//         tLZSysCertifySchema.setSendOutCom( "A86" );
//         tLZSysCertifySchema.setReceiveCom( "D8611000434 " );
//
//         String tOperate = new String();
//         TransferData tTransferData = new TransferData();
//         mTransferData.setNameAndValue("CertifyNo","810000000000611");
//         mTransferData.setNameAndValue("CertifyCode","1113") ;
//         mTransferData.setNameAndValue("ContNo","130110000013726") ;
//         mTransferData.setNameAndValue("MissionID","00000000000000005138") ;;
//         mTransferData.setNameAndValue("SubMissionID","3") ;
//         mTransferData.setNameAndValue("LZSysCertifySchema",tLZSysCertifySchema);
//
//         LCRReportSchema tLCRReportSchema = new LCRReportSchema();
//         tLCRReportSchema.setPrtSeq("810000000000611");
//         mTransferData.setNameAndValue("LCRReportSchema",tLCRReportSchema);

//   �����޸�ȷ��
//    mTransferData.setNameAndValue("ContNo", "86110020040990000060");
//    mTransferData.setNameAndValue("PrtNo", "21210000000004");
//    mTransferData.setNameAndValue("MissionID", "00000000000000000193");
//    mTransferData.setNameAndValue("SubMissionID", "2");
//    //�������ݵ���
//	mTransferData.setNameAndValue("FileName","Data000000JS2004062.xls");
//	mTransferData.setNameAndValue("ConfigFileName","ExcelImportLFJSConfig.xml");
//    mTransferData.setNameAndValue("ItemType","04") ;
//	mTransferData.setNameAndValue("StatYear","2004") ;
//	mTransferData.setNameAndValue("StatMon","01");
//	mTransferData.setNameAndValue("MissionID","00000000000000000116");
//	mTransferData.setNameAndValue("SubMissionID","1");00000000000000000117
//    //�������ݵ���ȷ��
//	mTransferData.setNameAndValue("FileName","Data000000JS2004062.xls");
//	mTransferData.setNameAndValue("ConfigFileName","ExcelImportLFJSConfig.xml");
//    mTransferData.setNameAndValue("ItemType","07") ;
//	mTransferData.setNameAndValue("StatYear","2004") ;
//	mTransferData.setNameAndValue("StatMon","01");
//	mTransferData.setNameAndValue("MissionID","00000000000000000117");
//	mTransferData.setNameAndValue("SubMissionID","1");
//    //ҵ�����ݵ���ȷ��
//	mTransferData.setNameAndValue("FileName","Data000000JS2004062.xls");
//	mTransferData.setNameAndValue("ConfigFileName","ExcelImportLFJSConfig.xml");
//	mTransferData.setNameAndValue("ItemType","01") ;
//	mTransferData.setNameAndValue("StatYear","2004") ;
//	mTransferData.setNameAndValue("StatMon","07");
//	mTransferData.setNameAndValue("MissionID","00000000000000000117");
//	mTransferData.setNameAndValue("SubMissionID","1");
//
//    LCContSchema tLCContSchema = new LCContSchema();
//    tLCContSchema.setContNo("00000000000000000005");
//    mTransferData.setNameAndValue("LCContSchema", tLCContSchema);
//    mTransferData.setNameAndValue("MissionID", "00000000000000000001");
//    mTransferData.setNameAndValue("SubMissionID", "1");
//
        //׼������������Ϣ
//	mTransferData.setNameAndValue("WhereSQL", " ||  ||AND ItemType In ('X1','X2','X3','X4','X5','X6','X7')  AND ItemCode in(select itemcode from LFItemRela where OutPutFlag='1' and IsMon='1') order by ItemNum");
//	mTransferData.setNameAndValue("NeedItemKey", "1");
//	mTransferData.setNameAndValue("ReportDate", "2003-10-01");
//	mTransferData.setNameAndValue("makedate","2004-07-12");
//	mTransferData.setNameAndValue("maketime","10:10:10");
//	mTransferData.setNameAndValue("sDate", "2003-10-01");
//    mTransferData.setNameAndValue("eDate", "2003-10-31");

//
//    //׼������������Ϣ
//	mTransferData.setNameAndValue("PolNo","86110020030210004500");
//	mTransferData.setNameAndValue("EdorNo","86110020030410000213") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000028");
//	mTransferData.setNameAndValue("PrtSeq","86000020030810002084");
//	mTransferData.setNameAndValue("SubMissionID","3");
//	mTransferData.setNameAndValue("LCRReportSchema",tLCRReportSchema);

        // ׼�����乤�������� VData
//	mTransferData.setNameAndValue("StatYear","2004") ;
//	mTransferData.setNameAndValue("StatMonth","01");


//    mTransferData.setNameAndValue("MissionID","00000000000000000110");
//    mTransferData.setNameAndValue("SubMissionID","1");

//	VData tVData3 = new VData();
//    mTransferData.setNameAndValue("PolNo","86110020030210035008");
//    mTransferData.setNameAndValue("EdorNo","86110020040410000039") ;
//    mTransferData.setNameAndValue("MissionID","00000000000000000023");
//    mTransferData.setNameAndValue("SubMissionID","1");

        /** ǩ������ */
//   String[] testContNo = {"86110020040990000042"};
//        LCContSet tLCContSet = new LCContSet ();
//        for ( int i=0;i< testContNo.length;i++){
//          LCContSchema tLCContSchema = new LCContSchema();
//          tLCContSchema.setContNo(testContNo[i]);
//          tLCContSet.add( tLCContSchema );
//        }
//      mTransferData.setNameAndValue("MissionID","00000000000000000174");
//     mTransferData.setNameAndValue("SubMissionID","1");
//	//�º˱�����
//    LCUWMasterSchema tLCUWMasterSchema = new LCUWMasterSchema();
//    tLCUWMasterSchema.setProposalNo("86110020040110000987");
//    tLCUWMasterSchema.setUWIdea("���ڳб�");
//    tLCUWMasterSchema.setPostponeDay("2��");
//    tLCUWMasterSchema.setPassFlag("2");

//    LCUWMasterSchema tLCUWMasterSchema = new LCUWMasterSchema();
//    //�µ�¼�����
//    mTransferData.setNameAndValue("ContNo","130110000013973");
//    mTransferData.setNameAndValue("PrtNo","yhyu87679");
//    mTransferData.setNameAndValue("AppntNo","0000493130");
//     mTransferData.setNameAndValue("AppntName","�ŷ�");
//     mTransferData.setNameAndValue("AgentCode","86110519");
//     mTransferData.setNameAndValue("ManageCom", "86110000");
//     mTransferData.setNameAndValue("Operator","001");
//     mTransferData.setNameAndValue("MakeDate","2005-04-07");
//     mTransferData.setNameAndValue("MissionID","00000000000000005338");
//     mTransferData.setNameAndValue("SubMissionID","1");


        //�µ�����
//        mTransferData.setNameAndValue("ContNo", "130110000013967");
//        mTransferData.setNameAndValue("PrtNo", "tyuj09089");
//        mTransferData.setNameAndValue("AppntNo", "0000493080");
//        mTransferData.setNameAndValue("AppntName","dsfasdf");
//        mTransferData.setNameAndValue("AgentCode","86110519");
//        mTransferData.setNameAndValue("ManageCom", "86110000");
//        mTransferData.setNameAndValue("Operator","001");
//        mTransferData.setNameAndValue("MakeDate","05-03-30");
//        mTransferData.setNameAndValue("MissionID","00000000000000005332");
//        mTransferData.setNameAndValue("SubMissionID","1");

        //��������
//    LCContSet tLCContSet = new LCContSet();
//    LCCUWMasterSet tLCCUWMasterSet = new LCCUWMasterSet();
//    mTransferData.setNameAndValue("ContNo", "130110000013825");
//    mTransferData.setNameAndValue("PrtNo", "20050330145");
//    mTransferData.setNameAndValue("UWFlag", "a");
//    mTransferData.setNameAndValue("UWIdea", "ok");
//    mTransferData.setNameAndValue("MissionID", "00000000000000005265");
//    mTransferData.setNameAndValue("SubMissionID", "1");
//    tVData.clear();
//    tVData.add(tLCContSet);
//    tVData.add(tLCCUWMasterSet);

        /**�ܱ���*/
        tVData.add(mGlobalInput);
        tVData.add(mTransferData);
        TbWorkFlowUI tTbWorkFlowUI = new TbWorkFlowUI();
        try
        {
            if (tTbWorkFlowUI.submitData(tVData, "0000001110"))
            {
                VData tResult = new VData();

                //tResult = tActivityOperator.getResult() ;
            }
            else
            {
                System.out.println(tTbWorkFlowUI.mErrors.getError(0).
                        errorMessage);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}

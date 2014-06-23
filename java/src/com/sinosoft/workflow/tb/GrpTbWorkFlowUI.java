/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.pubfun.GlobalInput;
//import com.sinosoft.lis.schema.LCGrpContSchema;
//import com.sinosoft.lis.vschema.LCGrpContSet;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;

/**
 * <p>Title: ���幤���� </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: SinoSoft </p>
 * @author HYQ
 * @version 1.0
 */

public class GrpTbWorkFlowUI
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** �����洫�����ݵ����� */
    private VData mResult = new VData();

    /** ���ݲ����ַ��� */
    private String mOperate;

    public GrpTbWorkFlowUI()
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

        GrpTbWorkFlowBL tGrpTbWorkFlowBL = new GrpTbWorkFlowBL();

        System.out.println("---GrpTbWorkFlowBL UI BEGIN---");
        if (tGrpTbWorkFlowBL.submitData(cInputData, mOperate))
        {
            return true;
        }
        else
        {
            // @@������
            this.mErrors.copyAllErrors(tGrpTbWorkFlowBL.mErrors);
            mResult.clear();
            return false;
        }
//        return true;
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
        //������ʼ�ڵ�
//    mTransferData.setNameAndValue("ContNo", "00000000000000000123");
//    mTransferData.setNameAndValue("PrtNo", "00002004111456");
//    mTransferData.setNameAndValue("Operator", "001");
//    mTransferData.setNameAndValue("ManageCom", "86110000");
//    mTransferData.setNameAndValue("MakeDate", "2004-11-22");
//    mTransferData.setNameAndValue("AppntNo", "000003345");
//    mTransferData.setNameAndValue("AppntName", "����");
//    mTransferData.setNameAndValue("AgentCode", "8611000433");
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

//        //�µ�����GrpContNo
//        mTransferData.setNameAndValue("GrpContNo", "140110000000737");
//        mTransferData.setNameAndValue("ContNo", "140110000000737");
//        mTransferData.setNameAndValue("MissionID", "00000000000000005382");
//        mTransferData.setNameAndValue("SubMissionID", "1");

        //�����޸�
//    mTransferData.setNameAndValue("ContNo", "00000000000000000006");
//    mTransferData.setNameAndValue("MissionID", "00000000000000000166");
//    mTransferData.setNameAndValue("SubMissionID", "1");
//    �˱�����
    mTransferData.setNameAndValue("GrpContNo", "000000006014");
    mTransferData.setNameAndValue("PrtNo", "2005063006305201");
    mTransferData.setNameAndValue("AgentCode", "8601900001");
    mTransferData.setNameAndValue("MakeDate", "2005-07-02");
    mTransferData.setNameAndValue("AppntNo", "000000000005");
    mTransferData.setNameAndValue("AppntName", "����");
    mTransferData.setNameAndValue("InsuredNo", "0000031226");
    mTransferData.setNameAndValue("InsuredName", "����");
    mTransferData.setNameAndValue("UWFlag", "a");
    mTransferData.setNameAndValue("MissionID", "00000000000000000076");
    mTransferData.setNameAndValue("SubMissionID", "1");

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
//        LCContSchema tLCContSchema = new LCContSchema();
// 	LCPENoticeSchema tLCPENoticeSchema = new LCPENoticeSchema();
//        tLCContSchema.setContNo("00000000000000000005");
//        tLCContSchema.setInsuredNo("0000031226");

        //	LCPENoticeSchema tLCPENoticeSchema = new LCPENoticeSchema();
//	tLCPENoticeSchema.setContNo("00000000000000000005");
//	tLCPENoticeSchema.setPEAddress("11003001");
//	tLCPENoticeSchema.setPEDate("2003-07-01");
//	tLCPENoticeSchema.setPEBeforeCond("N");
//	tLCPENoticeSchema.setRemark("ReMark");
//	tLCPENoticeSchema.setCustomerNo("0000031226");
//
//	LCPENoticeItemSet tLCPENoticeItemSet = new LCPENoticeItemSet();
//	LCPENoticeItemSchema tLCPENoticeItemSchema = new LCPENoticeItemSchema();
//	tLCPENoticeItemSchema.setContNo("00000000000000000005");
//	tLCPENoticeItemSchema.setPEItemCode( "001");
//	tLCPENoticeItemSchema.setPEItemName( "�ռ츹��B��");
//	tLCPENoticeItemSchema.setFreePE( "N");

//	LCPENoticeItemSchema tLCPENoticeItemSchema2 = new LCPENoticeItemSchema();
//	tLCPENoticeItemSchema2.setContNo("00000000000000000005");
//	tLCPENoticeItemSchema2.setPEItemCode( "002");
//	tLCPENoticeItemSchema2.setPEItemName( "�ռ츹��B��");
//	tLCPENoticeItemSchema2.setFreePE( "N");

//	tLCPENoticeItemSet.add( tLCPENoticeItemSchema );
//	tLCPENoticeItemSet.add( tLCPENoticeItemSchema2 );
//
//
//        mTransferData.setNameAndValue("LCPENoticeItemSet",tLCPENoticeItemSet);
//        mTransferData.setNameAndValue("LCPENoticeSchema",tLCPENoticeSchema);
//        mTransferData.setNameAndValue("MissionID","00000000000000000001");
//	mTransferData.setNameAndValue("SubMissionID","1");
//        mTransferData.setNameAndValue("SubMissionID","1");
//        mTransferData.setNameAndValue("CustomerNo","0000031226");
//        mTransferData.setNameAndValue("ContNo","00000000000000000005");
        //��ӡ���֪ͨ��
//    	mTransferData.setNameAndValue("PrtSeq","86000020040810000147");
//    	mTransferData.setNameAndValue("Code","03") ;
//    	mTransferData.setNameAndValue("ContNo","00000000000000000005") ;
//    	mTransferData.setNameAndValue("MissionID","00000000000000000001") ;
//        mTransferData.setNameAndValue("SubMissionID","1") ;
        //�������֪ͨ��
//	  LZSysCertifySchema tLZSysCertifySchema = new LZSysCertifySchema();
//	   tLZSysCertifySchema.setCertifyCode( "8888" );
//	   tLZSysCertifySchema.setCertifyNo( "86000020040810000147" );
//	   tLZSysCertifySchema.setTakeBackOperator( "001" );
//	   tLZSysCertifySchema.setTakeBackDate( "2004-03-31" );
//	   tLZSysCertifySchema.setTakeBackMakeDate( "2004-03-31" );
//	   tLZSysCertifySchema.setSendOutCom( "A86" );
//	   tLZSysCertifySchema.setReceiveCom( "D8611000433" );
//	   String tOperate = new String();
//	   TransferData tTransferData = new TransferData();
//	   mTransferData.setNameAndValue("CertifyNo","86000020040810000147");
//	   mTransferData.setNameAndValue("CertifyCode","8888") ;
//	   mTransferData.setNameAndValue("ContNo","00000000000000000005") ;
//	   mTransferData.setNameAndValue("MissionID","00000000000000000001") ;
//	   mTransferData.setNameAndValue("SubMissionID","1") ;
//	   mTransferData.setNameAndValue("LZSysCertifySchema",tLZSysCertifySchema);
//	//����֪ͨ�����
//	LCRReportSchema tLCRReportSchema = new LCRReportSchema();
//	tLCRReportSchema.setContNo("00000000000000000006");
//        tLCRReportSchema.setContente("�ҵ��������ݣ�����");
//	mTransferData.setNameAndValue("ContNo","00000000000000000006");
//	mTransferData.setNameAndValue("PrtNo","00002004111207") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000001");
//	mTransferData.setNameAndValue("CustomerNo","0000031226");
//       mTransferData.setNameAndValue("SubMissionID","1");
//	mTransferData.setNameAndValue("LCRReportSchema",tLCRReportSchema);
//��ӡ����֪ͨ��
//    mTransferData.setNameAndValue("PrtSeq", "86000020040810000157");
//    mTransferData.setNameAndValue("Code", "04");
//    mTransferData.setNameAndValue("ContNo", "00000000000000000006");
//    mTransferData.setNameAndValue("PrtNo", "00002004111207");
//    mTransferData.setNameAndValue("MissionID", "00000000000000000001");
//    mTransferData.setNameAndValue("SubMissionID", "1");
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
//    //���ͺ˱�֪ͨ��
//	mTransferData.setNameAndValue("ContNo","130110000000174");
//	mTransferData.setNameAndValue("PrtNo","00002004120211") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000219");
//	mTransferData.setNameAndValue("SubMissionID","1");
        //�º˱�����
//	mTransferData.setNameAndValue("ContNo","130110000000188");
//	mTransferData.setNameAndValue("PrtNo","21210000000021") ;
//        mTransferData.setNameAndValue("UWFlag","1") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000227");
//	mTransferData.setNameAndValue("SubMissionID","1");

        //�������������
//        mTransferData.setNameAndValue("ContNo","130110000000175");
//        mTransferData.setNameAndValue("MissionID","00000000000000000220");
//        mTransferData.setNameAndValue("SubMissionID","1");

//	//��ӡ�˱�֪ͨ��
//	mTransferData.setNameAndValue("PrtSeq","810000000000035");
//	mTransferData.setNameAndValue("Code","05") ;
//	mTransferData.setNameAndValue("ContNo","00000000000000000006") ;
//	mTransferData.setNameAndValue("PrtNo","00002004111207") ;
//	mTransferData.setNameAndValue("MissionID","00000000000000000001") ;
//        mTransferData.setNameAndValue("SubMissionID","1") ;
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

//    //����ҵ��Ա֪ͨ��
//        LZSysCertifySchema tLZSysCertifySchema = new LZSysCertifySchema();
//         tLZSysCertifySchema.setCertifyCode( "9996" );
//         tLZSysCertifySchema.setCertifyNo( "810000000000010" );
//         tLZSysCertifySchema.setTakeBackOperator( "001" );
//         tLZSysCertifySchema.setTakeBackDate( "2004-03-31" );
//         tLZSysCertifySchema.setTakeBackMakeDate( "2004-03-31" );
//         tLZSysCertifySchema.setSendOutCom( "A86" );
//         tLZSysCertifySchema.setReceiveCom( "D8611000434 " );

//         String tOperate = new String();
//         TransferData tTransferData = new TransferData();
//         mTransferData.setNameAndValue("CertifyNo","810000000000010");
//         mTransferData.setNameAndValue("CertifyCode","9996") ;
//         mTransferData.setNameAndValue("ContNo","00000000000000000006") ;
//         mTransferData.setNameAndValue("MissionID","00000000000000000001") ;
//         mTransferData.setNameAndValue("SubMissionID","1") ;
//         mTransferData.setNameAndValue("LZSysCertifySchema",tLZSysCertifySchema);
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
//    LCGrpContSchema tLCGrpContSchema  = new LCGrpContSchema();
//    LCGrpContSet tLCGrpContSet = new LCGrpContSet();
//    tLCGrpContSchema.setGrpContNo("140110000000684");
//    tVData.add(tLCGrpContSchema);
//    mTransferData.setNameAndValue("MissionID","00000000000000005217");
//    mTransferData.setNameAndValue("SubMissionID","1");
//	//�º˱�����
//    LCUWMasterSchema tLCUWMasterSchema = new LCUWMasterSchema();
//    tLCUWMasterSchema.setProposalNo("86110020040110000987");
//    tLCUWMasterSchema.setUWIdea("���ڳб�");
//    tLCUWMasterSchema.setPostponeDay("2��");
//    tLCUWMasterSchema.setPassFlag("2");
        //�˹��˱�����
//    LCGrpContSchema tLCGrpContSchema = new LCGrpContSchema();
//    LCGrpContSet tLCGrpContSet = new LCGrpContSet();
//
//    tLCGrpContSchema.setGrpContNo("140110000000721");
//    tLCGrpContSchema.setUWFlag("9");
//    tLCGrpContSchema.setRemark("ok");
//    tLCGrpContSet.add(tLCGrpContSchema);
//
//    mTransferData.setNameAndValue("MissionID", "00000000000000005373");
//    mTransferData.setNameAndValue("SubMissionID", "3");

        // ׼�����乤�������� VData
//    mTransferData.setNameAndValue("ContNo","130110000000289");
//    mTransferData.setNameAndValue("PrtNo","21210000000072");
//    mTransferData.setNameAndValue("AppUser","001");
//    mTransferData.setNameAndValue("UWFlag","1");
//    mTransferData.setNameAndValue("UWIdea","asdf");
//    mTransferData.setNameAndValue("MissionID","00000000000000000255");
//    mTransferData.setNameAndValue("SubMissionID","1");
//    mTransferData.setNameAndValue("LCUWMasterSchema",tLCUWMasterSchema);
//    ׼���ŵ�����Լ��ʼ���
//        mTransferData.setNameAndValue("ProposalGrpContNo", "86110000000063");
//        mTransferData.setNameAndValue("PrtNo", "31310000000085");
//        mTransferData.setNameAndValue("SaleChnl", "01");
//        mTransferData.setNameAndValue("AgentCode","86110001");
//        mTransferData.setNameAndValue("AgentGroup","000000000001");
//        mTransferData.setNameAndValue("ManageCom", "86110000");
//        mTransferData.setNameAndValue("GrpName", "IBM����");
//        mTransferData.setNameAndValue("CValiDate", "2004-12-17");
//        mTransferData.setNameAndValue("MissionID","null");
//        mTransferData.setNameAndValue("SubMissionID","null");
        //�ŵ�¼����Ͻڵ�
//    mTransferData.setNameAndValue("GrpContNo","140110000000687");
//mTransferData.setNameAndValue("PrtNo", "150001234");
//mTransferData.setNameAndValue("SaleChnl","01");
//mTransferData.setNameAndValue("AgentCode","86110487");
//mTransferData.setNameAndValue("AgentGroup","000000000473");
//mTransferData.setNameAndValue("ManageCom","86110000");
//mTransferData.setNameAndValue("GrpName", "һ��");
//mTransferData.setNameAndValue("CValiDate","2005-04-04");
//mTransferData.setNameAndValue("Operator","001");
//mTransferData.setNameAndValue("MakeDate","2005-04-04");
//mTransferData.setNameAndValue("MissionID","00000000000000005323");
//mTransferData.setNameAndValue("SubMissionID","1");

        //׼���ŵ��µ����˽ڵ�
//    mTransferData.setNameAndValue("GrpContNo","120110000000172");
//    mTransferData.setNameAndValue("PrtNo","12345678924333");
//    mTransferData.setNameAndValue("SaleChnl","01");
//    mTransferData.setNameAndValue("ManageCom","86110000");
//    mTransferData.setNameAndValue("GrpName","ee");
//    mTransferData.setNameAndValue("CValiDate","2004-12-08");
//    mTransferData.setNameAndValue("MissionID","00000000000000000291");
//    mTransferData.setNameAndValue("SubMissionID","1");
        //׼���ŵ��Ժ˽ڵ�
//        LCGrpContSet tLCGrpContSet = new LCGrpContSet();
//        LCGrpContSchema tLCGrpContSchema = new LCGrpContSchema();
//        tLCGrpContSchema.setGrpContNo("120110000000271");
//        mTransferData.setNameAndValue("MissionID", "00000000000000000381");
//        mTransferData.setNameAndValue("SubMissionID", "1");
//        tLCGrpContSet.add(tLCGrpContSchema);
        //׼���ŵ��˹��˱��ڵ�
//    LCGrpContSchema tLCGrpContSchema = new LCGrpContSchema();
//    LCGrpContSet tLCGrpContSet =new LCGrpContSet();
//
//    tLCGrpContSchema.setGrpContNo("120110000000167");
//    tLCGrpContSchema.setUWFlag("4");
//    tLCGrpContSchema.setRemark("dsafdfa");
//    tLCGrpContSet.add(tLCGrpContSchema);
//    mTransferData.setNameAndValue("MissionID", "00000000000000000287");
//    mTransferData.setNameAndValue("SubMissionID", "1");
        //׼���ŵ��˹��˱��ڵ�
//    LCGrpContSchema tLCGrpContSchema = new LCGrpContSchema();
//    LCGrpContSet tLCGrpContSet =new LCGrpContSet();
//
//    tLCGrpContSchema.setGrpContNo("140110000000199");
//    tLCGrpContSchema.setUWFlag("9");
//    tLCGrpContSchema.setRemark("dsafdfa");
//    tLCGrpContSet.add(tLCGrpContSchema);
//    mTransferData.setNameAndValue("MissionID", "00000000000000000626");
//    mTransferData.setNameAndValue("SubMissionID", "2");
        //׼���ŵ��˱������ڵ�
//    mTransferData.setNameAndValue("GrpContNo","140110000000201");
//    mTransferData.setNameAndValue("MissionID", "00000000000000005323");
//    mTransferData.setNameAndValue("SubMissionID", "1");

        //׼���ŵ�Sign�˱��ڵ�
//    LCGrpContSchema tLCGrpContSchema = new LCGrpContSchema();
//
//    tLCGrpContSchema.setGrpContNo("140110000000026");
//
//    mTransferData.setNameAndValue("MissionID", "00000000000000000029");
//    mTransferData.setNameAndValue("SubMissionID", "1");

        /**�ܱ���*/
        tVData.add(mGlobalInput);
//    tVData.add(tLCGrpContSet);
//        tVData.add(tLCGrpContSchema);
        tVData.add(mTransferData);

        GrpTbWorkFlowUI tGrpTbWorkFlowUI = new GrpTbWorkFlowUI();
        try
        {
            if (tGrpTbWorkFlowUI.submitData(tVData, "0000002005"))
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
}

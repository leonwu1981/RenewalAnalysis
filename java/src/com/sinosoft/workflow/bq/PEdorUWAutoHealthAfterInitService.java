package com.sinosoft.workflow.bq;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author lh
 * @version 1.0
 */

public class PEdorUWAutoHealthAfterInitService implements AfterInitService
{

    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** �����洫�����ݵ����� */
    private VData mInputData;
    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();
    //private VData mIputData = new VData();
    private TransferData mTransferData = new TransferData();
    private TransferData mReturnTransferData = new TransferData();
    /**���������� */
    ActivityOperator mActivityOperator = new ActivityOperator();
    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;
    private String mOperate;
    /** ҵ�����ݲ����ַ��� */
    private String mEdorNo;
    private String mContNo;
    private String mInsuredNo;
    private String mMissionID;
    private String mPrtSeq;

    /**ִ�б�ȫ�����������֪ͨ��������0000000001*/
    /**������*/
    private LCContSchema mLCContSchema = new LCContSchema();
    /** ��ȫ�˱����� */
    private LPUWMasterMainSchema mLPUWMasterMainSchema = new
            LPUWMasterMainSchema();
    /** ����������� */
    private LPPENoticeSet mLPPENoticeSet = new LPPENoticeSet();
    private LPPENoticeSchema mLPPENoticeSchema = new LPPENoticeSchema();
    private LPPENoticeSet mOldLPPENoticeSet = new LPPENoticeSet();
    private LPPENoticeItemSet mOldLPPENoticeItemSet = new LPPENoticeItemSet();
    /** ���������Ŀ�� */
    private LPPENoticeItemSet mLPPENoticeItemSet = new LPPENoticeItemSet();
    /** ��ӡ����� */
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();

    public PEdorUWAutoHealthAfterInitService()
    {
    }

    /**
     * �������ݵĹ�������
     * @param: cInputData ���������
     *         cOperate ���ݲ���
     * @return:
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        System.out.println("Start PEdorUWAutoHealthAfterInitService Submit...");
        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData(cInputData, cOperate))
        {
            return false;
        }

        //У���Ƿ���δ��ӡ�����֪ͨ��
        if (!checkData())
        {
            return false;
        }

        //����ҵ����
        if (!dealData())
        {
            return false;
        }

        //Ϊ��������һ�ڵ������ֶ�׼������
        if (!prepareTransferData())
        {
            return false;
        }

        //׼������̨������
        if (!prepareOutputData())
        {
            return false;
        }

        //mResult.clear();
        return true;
    }


    /**
     * У��ҵ������
     * @return
     */
    private boolean checkData()
    {
        //У�鱣����Ϣ
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mContNo);
        if (!tLCContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "������ͬ" + mContNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);

        //У���Ƿ���δ��ӡ�����֪ͨ��
        LPPENoticeDB tLPPENoticeDB = new LPPENoticeDB();
        tLPPENoticeDB.setEdorNo(mEdorNo);
        tLPPENoticeDB.setContNo(mContNo);
        tLPPENoticeDB.setCustomerNo(mInsuredNo);
        LPPENoticeSet tLPPENoticeSet = new LPPENoticeSet();
        tLPPENoticeSet = tLPPENoticeDB.query();

        if (tLPPENoticeSet.size() > 0)
        {

            if (tLPPENoticeSet.get(1).getPrintFlag().equals("0"))
            {
                CError tError = new CError();
                tError.moduleName = "PEdorUWAutoHealthAfterInitService";
                tError.functionName = "checkData";
                tError.errorMessage = "���֪ͨ�Ѿ�¼��,��δ��ӡ������¼�����������!";
                this.mErrors.addOneError(tError);
                return false;
            }
        }

        //У�鱣ȫ�����˱�����
        //У�鱣����Ϣ
        LPUWMasterMainDB tLPUWMasterMainDB = new LPUWMasterMainDB();
        tLPUWMasterMainDB.setEdorNo(mEdorNo);
        tLPUWMasterMainDB.setContNo(mContNo);
        if (!tLPUWMasterMainDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorUWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "������ͬ" + mContNo + "��ȫ�����˱�������Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLPUWMasterMainSchema.setSchema(tLPUWMasterMainDB);

        return true;
    }

    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {
        String strNoLimit = PubFun.getNoLimit(mGlobalInput.ComCode);
        mPrtSeq = PubFun1.CreateMaxNo("PRTSEQNO", strNoLimit);

        // �����Ϣ
        if (!prepareHealth())
        {
            return false;
        }

        //��ӡ����
        if (!preparePrint())
        {
            return false;
        }

        return true;

    }


    /**
     * �����������еõ����ж���
     *��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean getInputData(VData cInputData, String cOperate)
    {
        //�����������еõ����ж���
        //���ȫ�ֹ�������
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��ò���Ա����
        mOperater = mGlobalInput.Operator;
        if (mOperater == null || mOperater.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�½��������
        mManageCom = mGlobalInput.ManageCom;
        if (mManageCom == null || mManageCom.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mOperate = cOperate;

        //���ҵ������
        if (mTransferData == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mEdorNo = (String) mTransferData.getValueByName("EdorNo");
        if (mEdorNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������EdorNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mContNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������PolNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mInsuredNo = (String) mTransferData.getValueByName("InsuredNo");
        if (mInsuredNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������InsuredNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //���ҵ�����֪ͨ����
        mLPPENoticeSchema = (LPPENoticeSchema) mTransferData.getValueByName(
                "LPPENoticeSchema");
        if (mLPPENoticeSchema == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨������ҵ�����֪ͨ����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //���ҵ�����֪ͨ��Ӧ�������Ŀ
        mLPPENoticeItemSet = (LPPENoticeItemSet) mTransferData.getValueByName(
                "LPPENoticeItemSet");
        if (mLPPENoticeItemSet == null || mLPPENoticeItemSet.size() == 0)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨������ҵ�����֪ͨ��Ӧ�������Ŀ����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ�������������ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ׼�����������Ϣ
     * �����������������򷵻�false,���򷵻�true
     */
    private boolean prepareHealth()
    {

        //ȡ����������
        LAAgentDB tLAAgentDB = new LAAgentDB();
        tLAAgentDB.setAgentCode(mLCContSchema.getAgentCode());
        if (!tLAAgentDB.getInfo())
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "prepareHealth";
            tError.errorMessage = "ȡ����������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

//	//ɾ���ɵ������Ϣ
//	LPPENoticeDB tLPPENoticeDB = new LPPENoticeDB();
//	tLPPENoticeDB.setEdorNo(mLPPENoticeSchema.getEdorNo()) ;
//	tLPPENoticeDB.setContNo(mLPPENoticeSchema.getPolNo()) ;
//	tLPPENoticeDB.setInsuredNo(mLPPENoticeSchema.getInsuredNo()) ;
//    mOldLPPENoticeSet = tLPPENoticeDB.query() ;
//	if(mOldLPPENoticeSet == null)
//	{
//	  // @@������
//	  CError tError = new CError();
//	  tError.moduleName = "PEdorUWAutoHealthAfterInitService";
//	  tError.functionName = "prepareHealth";
//	  tError.errorMessage = "��ѯ�ɵ������Ϣ����!";
//	  this.mErrors .addOneError(tError) ;
//	  return false;
//	}
//
//	//׼����������Ŀ��Ϣ
//	for (int i = 1;i <= mLPPENoticeItemSet.size();i++)
//	{
//	  //mLPPENoticeItemSet.get(i).setEdorNo( mEdorNo );
//	  //mLPPENoticeItemSet.get(i).setPolNo( mContNo );
//	  //mLPPENoticeItemSet.get(i).setPEItemCode(); //�˱��������
//	  //mLPPENoticeItemSet.get(i).setPEItemName(); //�˱�������Ϣ
//	  //mLPPENoticeItemSet.get(i).setInsuredNo(mInsuredNo);
//	  mLPPENoticeItemSet.get(i).setModifyDate(PubFun.getCurrentDate()); //��ǰֵ
//	  mLPPENoticeItemSet.get(i).setModifyTime(PubFun.getCurrentTime());
//	  //mLPPENoticeItemSet.get(i).setFreePE() ;
//	}
//
//
//	//ɾ���ɵ������Ϣ
//
//	String tSQL = "select * from LPPENoticeItem where edorno = '"+mEdorNo +"'"
//	              +" and ContNo = '" + mContNo +"'"
//	              +" and insuredno = '" + mInsuredNo + "'";
//	LPPENoticeItemDB tLPPENoticeItemDB = new LPPENoticeItemDB();
//	mOldLPPENoticeItemSet = tLPPENoticeItemDB.executeQuery(tSQL);
//	if(mOldLPPENoticeItemSet == null)
//	{
//	  // @@������
//	  CError tError = new CError();
//	  tError.moduleName = "PEdorUWAutoHealthAfterInitService";
//	  tError.functionName = "prepareHealth";
//	  tError.errorMessage = "��ѯ�ɵ������Ŀ��Ϣ����!";
//	  this.mErrors .addOneError(tError) ;
//	  return false;
//	}

        //ȡ���������
        LDPersonDB tLDPersonDB = new LDPersonDB();
        tLDPersonDB.setCustomerNo(mInsuredNo);
        if (!tLDPersonDB.getInfo())
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "UWAutoHealthAfterInitService";
            tError.functionName = "prepareHealth";
            tError.errorMessage = "ȡ�����ͻ�����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLPPENoticeSchema.setGrpContNo(mLCContSchema.getGrpContNo());
        mLPPENoticeSchema.setProposalContNo(mLCContSchema.getProposalContNo());
        mLPPENoticeSchema.setName(tLDPersonDB.getName());
        mLPPENoticeSchema.setPrtSeq(mPrtSeq);
        //mLCPENoticeSchema.setPEDate(mLCPENoticeSchema.getPEDate());
        //mLCPENoticeSchema.setPEAddress(mLCPENoticeSchema.getPEAddress());
        mLPPENoticeSchema.setPrintFlag("0");
        mLPPENoticeSchema.setAppName(mLCContSchema.getAppntName());
        mLPPENoticeSchema.setAgentCode(mLCContSchema.getAgentCode());
        mLPPENoticeSchema.setAgentName(tLAAgentDB.getName());
        mLPPENoticeSchema.setManageCom(mLCContSchema.getManageCom());
        //mLCPENoticeSchema.setPEBeforeCond(mLCPENoticeSchema.getPEBeforeCond());
        mLPPENoticeSchema.setOperator(mOperater); //����Ա
        mLPPENoticeSchema.setMakeDate(PubFun.getCurrentDate());
        mLPPENoticeSchema.setMakeTime(PubFun.getCurrentTime());
        mLPPENoticeSchema.setModifyDate(PubFun.getCurrentDate());
        mLPPENoticeSchema.setModifyTime(PubFun.getCurrentTime());
        //mLCPENoticeSchema.setRemark(mLCPENoticeSchema.getRemark());

        //׼����������Ŀ��Ϣ
        for (int i = 1; i <= mLPPENoticeItemSet.size(); i++)
        {
            mLPPENoticeItemSet.get(i).setGrpContNo(mLCContSchema.getGrpContNo());
            mLPPENoticeItemSet.get(i).setProposalContNo(mLCContSchema.
                    getProposalContNo());
            mLPPENoticeItemSet.get(i).setPrtSeq(mPrtSeq);
            //mLCPENoticeItemSet.get(i).setContNo( mContNo );
            //mLCPENoticeItemSet.get(i).setPEItemCode(); //�˱��������
            //mLCPENoticeItemSet.get(i).setPEItemName(); //�˱�������Ϣ
            //mLCPENoticeItemSet.get(i).setCustomerNo(mCustomerNo);
            mLPPENoticeItemSet.get(i).setModifyDate(PubFun.getCurrentDate()); //��ǰֵ
            mLPPENoticeItemSet.get(i).setModifyTime(PubFun.getCurrentTime());
            //mLCPENoticeItemSet.get(i).setFreePE() ;
        }
        //׼���˱�������Ϣ
        mLPUWMasterMainSchema.setOperator(mOperater);
        mLPUWMasterMainSchema.setManageCom(mManageCom);
        mLPUWMasterMainSchema.setModifyDate(PubFun.getCurrentDate());
        mLPUWMasterMainSchema.setModifyTime(PubFun.getCurrentTime());
        mLPUWMasterMainSchema.setHealthFlag("1");

        return true;
    }


    /**
     * ��ӡ��Ϣ��
     * @return
     */
    private boolean preparePrint()
    {
        // ����δ��ӡ״̬��֪ͨ���ڴ�ӡ������ֻ����һ��
        // ������ͬһ���������ͣ�ͬһ���������룬ͬһ��������������
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

        tLOPRTManagerDB.setCode(PrintManagerBL.CODE_PEdorPE); //���
        tLOPRTManagerDB.setOtherNo(mContNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_CONT); //������
        tLOPRTManagerDB.setStandbyFlag1(mInsuredNo);
        tLOPRTManagerDB.setStandbyFlag2(mEdorNo);
        tLOPRTManagerDB.setStateFlag("0");

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "��ѯ��ӡ�������Ϣ����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLOPRTManagerSet.size() != 0)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "�ڴ�ӡ����������һ������δ��ӡ״̬�����֪ͨ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //׼����ӡ���������
//	 String strNoLimit = PubFun.getNoLimit( mGlobalInput.ComCode );
//	 String tPrtSeq = PubFun1.CreateMaxNo("PRTSEQNO", strNoLimit);
        mLOPRTManagerSchema.setPrtSeq(mPrtSeq);
        mLOPRTManagerSchema.setOtherNo(mContNo);
        mLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_CONT); //������
        mLOPRTManagerSchema.setCode(PrintManagerBL.CODE_PEdorPE); //���
        mLOPRTManagerSchema.setManageCom(mLCContSchema.getManageCom());
        mLOPRTManagerSchema.setAgentCode(mLCContSchema.getAgentCode());
        mLOPRTManagerSchema.setReqCom(mGlobalInput.ComCode);
        mLOPRTManagerSchema.setReqOperator(mGlobalInput.Operator);
        //mLOPRTManagerSchema.setExeCom();
        //mLOPRTManagerSchema.setExeOperator();
        mLOPRTManagerSchema.setPrtType(PrintManagerBL.PT_FRONT); //ǰ̨��ӡ
        mLOPRTManagerSchema.setStateFlag("0");
        mLOPRTManagerSchema.setPatchFlag("0");
        mLOPRTManagerSchema.setMakeDate(PubFun.getCurrentDate());
        mLOPRTManagerSchema.setMakeTime(PubFun.getCurrentTime());
        //mLOPRTManagerSchema.setDoneDate() ;
        //mLOPRTManagerSchema.setDoneTime();
        mLOPRTManagerSchema.setStandbyFlag1(mInsuredNo); //�������˱���
        mLOPRTManagerSchema.setStandbyFlag2(mEdorNo); //��ȫ������
        mLOPRTManagerSchema.setStandbyFlag3(mMissionID);
        mLOPRTManagerSchema.setOldPrtSeq(mPrtSeq);

        return true;
    }

    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return
     */
    private boolean prepareTransferData()
    {
        LAAgentDB tLAAgentDB = new LAAgentDB();
        LAAgentSet tLAAgentSet = new LAAgentSet();
        tLAAgentDB.setAgentCode(mLCContSchema.getAgentCode());
        tLAAgentSet = tLAAgentDB.query();
        if (tLAAgentSet == null || tLAAgentSet.size() != 1)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "�����˱�LAAgent��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLAAgentSet.get(1).getAgentGroup() == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "�����˱�LAAgent�еĴ���������ݶ�ʧ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        LABranchGroupDB tLABranchGroupDB = new LABranchGroupDB();
        LABranchGroupSet tLABranchGroupSet = new LABranchGroupSet();
        tLABranchGroupDB.setAgentGroup(tLAAgentSet.get(1).getAgentGroup());
        tLABranchGroupSet = tLABranchGroupDB.query();
        if (tLABranchGroupSet == null || tLABranchGroupSet.size() != 1)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "������չҵ������LABranchGroup��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLABranchGroupSet.get(1).getBranchAttr() == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "������չҵ������LABranchGroup��չҵ������Ϣ��ʧ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mTransferData.setNameAndValue("PrtSeq", mLOPRTManagerSchema.getPrtSeq());
        mTransferData.setNameAndValue("OldPrtSeq",
                mLOPRTManagerSchema.getPrtSeq());
        mTransferData.setNameAndValue("AgentCode", mLCContSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup",
                tLAAgentSet.get(1).getAgentGroup());
        mTransferData.setNameAndValue("BranchAttr",
                tLABranchGroupSet.get(1).getBranchAttr());
        mTransferData.setNameAndValue("ManageCom", mLCContSchema.getManageCom());
        return true;
    }

    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();

        //ɾ�������֪ͨ����
        if (mOldLPPENoticeSet != null && mOldLPPENoticeSet.size() > 0)
        {
            map.put(mOldLPPENoticeSet, "DELETE");
        }

        //������֪ͨ����
        map.put(mLPPENoticeSchema, "INSERT");

        //ɾ���������Ŀ֪ͨ����
        if (mOldLPPENoticeItemSet != null && mOldLPPENoticeItemSet.size() > 0)
        {
            map.put(mOldLPPENoticeItemSet, "DELETE");
        }

        //��������Ŀ����
        map.put(mLPPENoticeItemSet, "INSERT");

        //������֪ͨ���ӡ���������
        map.put(mLOPRTManagerSchema, "INSERT");

        //��ӱ�ȫ�����˱�����֪ͨ���ӡ���������
        map.put(mLPUWMasterMainSchema, "UPDATE");

        mResult.add(map);
        return true;
    }

    public VData getResult()
    {
        return mResult;
    }

    public TransferData getReturnTransferData()
    {
        return mTransferData;
    }

    public CErrors getErrors()
    {
        return mErrors;
    }

    public static void main(String[] args)
    {
        //SysUWNoticeBL sysUWNoticeBL1 = new SysUWNoticeBL();
    }
}

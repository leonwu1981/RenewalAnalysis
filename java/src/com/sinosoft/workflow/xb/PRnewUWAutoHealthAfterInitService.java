/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.xb;

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

public class PRnewUWAutoHealthAfterInitService implements AfterInitService
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
//    private TransferData mReturnTransferData = new TransferData();
    /**���������� */
    ActivityOperator mActivityOperator = new ActivityOperator();
    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;
    private String mOperate;
    /** ҵ�����ݲ����ַ��� */
    private String mPolNo;
    private String mInsuredNo;
    private String mMissionID;

    /**ִ�б�ȫ�����������֪ͨ��������0000000001*/
    /**������*/
    private LCPolSchema mLCPolSchema = new LCPolSchema();
    /** ��ȫ�˱����� */
    private LCUWMasterSchema mLCUWMasterSchema = new LCUWMasterSchema();
    /** ����������� */
//    private LCPENoticeSet mLCPENoticeSet = new LCPENoticeSet();
    private LCPENoticeSchema mLCPENoticeSchema = new LCPENoticeSchema();
    private LCPENoticeSet mOldLCPENoticeSet = new LCPENoticeSet();
    private LCPENoticeItemSet mOldLCPENoticeItemSet = new LCPENoticeItemSet();
    /** ���������Ŀ�� */
    private LCPENoticeItemSet mLCPENoticeItemSet = new LCPENoticeItemSet();
    /** ��ӡ����� */
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();

    public PRnewUWAutoHealthAfterInitService()
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

        System.out.println("After PRnewUWAutoHealthAfterInitService Submit...");

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
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setPolNo(mPolNo);
        if (!tLCPolDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mPolNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCPolSchema.setSchema(tLCPolDB);

        //У���Ƿ���δ��ӡ�����֪ͨ��
        LCPENoticeDB tLCPENoticeDB = new LCPENoticeDB();
//	tLCPENoticeDB.setProposalNo(mPolNo);
//	tLCPENoticeDB.setInsuredNo(mInsuredNo);

        if (tLCPENoticeDB.getInfo())
        {
            if (tLCPENoticeDB.getPrintFlag().equals("0"))
            {
                CError tError = new CError();
                tError.moduleName = "PRnewUWAutoHealthAfterInitService";
                tError.functionName = "checkData";
                tError.errorMessage = "���֪ͨ�Ѿ�¼��,��δ��ӡ������¼�����������!";
                this.mErrors.addOneError(tError);
                return false;
            }
        }

        //У�鱣ȫ�����˱�����
        //У�鱣����Ϣ
        LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
        tLCUWMasterDB.setProposalNo(mPolNo);
        if (!tLCUWMasterDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PRnewUWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mPolNo + "�������˱�������Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCUWMasterSchema.setSchema(tLCUWMasterDB);

        return true;
    }

    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {
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
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
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
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
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
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
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
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mPolNo = (String) mTransferData.getValueByName("PolNo");
        if (mPolNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
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
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������InsuredNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //���ҵ�����֪ͨ����
        mLCPENoticeSchema = (LCPENoticeSchema) mTransferData.getValueByName("LCPENoticeSchema");
        if (mLCPENoticeSchema == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨������ҵ�����֪ͨ����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //���ҵ�����֪ͨ��Ӧ�������Ŀ
        mLCPENoticeItemSet = (LCPENoticeItemSet) mTransferData.getValueByName("LCPENoticeItemSet");
        if (mLCPENoticeItemSet == null || mLCPENoticeItemSet.size() == 0)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
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
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
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

        //ȡ��������
        LMRiskSchema tLMRiskSchema = new LMRiskSchema();
        LMRiskDB tLMRiskDB = new LMRiskDB();
        tLMRiskDB.setRiskCode(mLCPolSchema.getRiskCode());
        tLMRiskDB.setRiskVer(mLCPolSchema.getRiskVersion());
        if (!tLMRiskDB.getInfo())
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
            tError.functionName = "prepareHealth";
            tError.errorMessage = "ȡ��������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ȡ����������
        LAAgentDB tLAAgentDB = new LAAgentDB();
        tLAAgentDB.setAgentCode(mLCPolSchema.getAgentCode());
        if (!tLAAgentDB.getInfo())
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
            tError.functionName = "prepareHealth";
            tError.errorMessage = "ȡ����������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ȡ����������
        LCInsuredDB tLCInsuredDB = new LCInsuredDB();
        /*Lis5.3 upgrade set
          tLCInsuredDB.setPolNo(mPolNo);
          tLCInsuredDB.setCustomerNo(mInsuredNo);
         */
        if (!tLCInsuredDB.getInfo())
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
            tError.functionName = "prepareHealth";
            tError.errorMessage = "ȡ����������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //׼�����֪ͨ����
        //mLCPENoticeSchema.setProposalNo( mPolNo);
        //mLCPENoticeSchema.setInsuredNo(mInsuredNo);
        /*Lis5.3 upgrade get
          mLCPENoticeSchema.setInsuredName(tLCInsuredDB.getName());
         */
        //mLCPENoticeSchema.setPEDate(mLCPENoticeSchema.getPEDate());
        //mLCPENoticeSchema.setPEAddress(mLCPENoticeSchema.getPEAddress());
        mLCPENoticeSchema.setPrintFlag("0");
        mLCPENoticeSchema.setAppName(mLCPolSchema.getAppntName());
        mLCPENoticeSchema.setAgentCode(mLCPolSchema.getAgentCode());
        mLCPENoticeSchema.setAgentName(tLAAgentDB.getName());
        mLCPENoticeSchema.setManageCom(mLCPolSchema.getManageCom());
        //mLCPENoticeSchema.setPEBeforeCond(mLCPENoticeSchema.getPEBeforeCond());
        mLCPENoticeSchema.setOperator(mOperater); //����Ա
        mLCPENoticeSchema.setMakeDate(PubFun.getCurrentDate());
        mLCPENoticeSchema.setMakeTime(PubFun.getCurrentTime());
        mLCPENoticeSchema.setModifyDate(PubFun.getCurrentDate());
        mLCPENoticeSchema.setModifyTime(PubFun.getCurrentTime());
        //mLCPENoticeSchema.setRemark(mLCPENoticeSchema.getRemark());


        //ɾ���ɵ������Ϣ
        LCPENoticeDB tLCPENoticeDB = new LCPENoticeDB();
//	tLCPENoticeDB.setProposalNo(mLCPENoticeSchema.getProposalNo()) ;
//	tLCPENoticeDB.setInsuredNo(mLCPENoticeSchema.getInsuredNo()) ;
        mOldLCPENoticeSet = tLCPENoticeDB.query();
        if (mOldLCPENoticeSet == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
            tError.functionName = "prepareHealth";
            tError.errorMessage = "��ѯ�ɵ������Ϣ����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //׼����������Ŀ��Ϣ
        for (int i = 1; i <= mLCPENoticeItemSet.size(); i++)
        {
            //mLCPENoticeItemSet.get(i).setEdorNo( mEdorNo );
            //mLCPENoticeItemSet.get(i).setPolNo( mPolNo );
            //mLCPENoticeItemSet.get(i).setPEItemCode(); //�˱��������
            //mLCPENoticeItemSet.get(i).setPEItemName(); //�˱�������Ϣ
            //mLCPENoticeItemSet.get(i).setInsuredNo(mInsuredNo);
            mLCPENoticeItemSet.get(i).setModifyDate(PubFun.getCurrentDate()); //��ǰֵ
            mLCPENoticeItemSet.get(i).setModifyTime(PubFun.getCurrentTime());
            //mLCPENoticeItemSet.get(i).setFreePE() ;
        }

        //ɾ���ɵ������Ϣ

        String tSQL = "select * from LCPENoticeItem where "
                + " and polno = '" + mPolNo + "'"
                + " and insuredno = '" + mInsuredNo + "'";
        LCPENoticeItemDB tLCPENoticeItemDB = new LCPENoticeItemDB();
        mOldLCPENoticeItemSet = tLCPENoticeItemDB.executeQuery(tSQL);
        if (mOldLCPENoticeItemSet == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
            tError.functionName = "prepareHealth";
            tError.errorMessage = "��ѯ�ɵ������Ŀ��Ϣ����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        ////׼���˱�������Ϣ

        mLCUWMasterSchema.setOperator(mOperater);
        mLCUWMasterSchema.setManageCom(mManageCom);
        mLCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
        mLCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());

        mLCUWMasterSchema.setHealthFlag("1");

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

        tLOPRTManagerDB.setCode(PrintManagerBL.CODE_PRnewPE); //���
        tLOPRTManagerDB.setOtherNo(mPolNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_INDPOL); //������
        tLOPRTManagerDB.setStandbyFlag1(mInsuredNo);
        tLOPRTManagerDB.setStateFlag("0");

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "��ѯ��ӡ�������Ϣ����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLOPRTManagerSet.size() != 0)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "�ڴ�ӡ����������һ������δ��ӡ״̬�����֪ͨ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //׼����ӡ���������
        String strNoLimit = PubFun.getNoLimit(mGlobalInput.ComCode);
        String tPrtSeq = PubFun1.CreateMaxNo("PRTSEQNO", strNoLimit);
        mLOPRTManagerSchema.setPrtSeq(tPrtSeq);
        mLOPRTManagerSchema.setOtherNo(mPolNo);
        mLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_INDPOL); //������
        mLOPRTManagerSchema.setCode(PrintManagerBL.CODE_PRnewPE); //���
        mLOPRTManagerSchema.setManageCom(mLCPolSchema.getManageCom());
        mLOPRTManagerSchema.setAgentCode(mLCPolSchema.getAgentCode());
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
        mLOPRTManagerSchema.setStandbyFlag2(mLCPolSchema.getPrtNo()); //����������
        mLOPRTManagerSchema.setStandbyFlag3(mMissionID);
        mLOPRTManagerSchema.setOldPrtSeq(tPrtSeq);

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
        tLAAgentDB.setAgentCode(mLCPolSchema.getAgentCode());
        tLAAgentSet = tLAAgentDB.query();
        if (tLAAgentSet == null || tLAAgentSet.size() != 1)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "�����˱�LAAgent��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLAAgentSet.get(1).getAgentGroup() == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
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
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "������չҵ������LABranchGroup��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLABranchGroupSet.get(1).getBranchAttr() == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "������չҵ������LABranchGroup��չҵ������Ϣ��ʧ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mTransferData.setNameAndValue("PrtSeq", mLOPRTManagerSchema.getPrtSeq());
        mTransferData.setNameAndValue("AgentCode", mLCPolSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup", tLAAgentSet.get(1).getAgentGroup());
        mTransferData.setNameAndValue("BranchAttr", tLABranchGroupSet.get(1).getBranchAttr());
        mTransferData.setNameAndValue("ManageCom", mLCPolSchema.getManageCom());
        return true;
    }

    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();

        //ɾ�������֪ͨ����
        if (mOldLCPENoticeSet != null && mOldLCPENoticeSet.size() > 0)
        {
            map.put(mOldLCPENoticeSet, "DELETE");
        }

        //������֪ͨ����
        map.put(mLCPENoticeSchema, "INSERT");

        //ɾ���������Ŀ֪ͨ����
        if (mOldLCPENoticeItemSet != null && mOldLCPENoticeItemSet.size() > 0)
        {
            map.put(mOldLCPENoticeItemSet, "DELETE");
        }

        //��������Ŀ����
        map.put(mLCPENoticeItemSet, "INSERT");

        //������֪ͨ���ӡ���������
        map.put(mLOPRTManagerSchema, "INSERT");

        //������������˱�����֪ͨ���ӡ���������
        map.put(mLCUWMasterSchema, "UPDATE");

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

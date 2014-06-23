/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title:�������ڵ�����:��ӡ�˱�֪ͨ�� </p>
 * <p>Description:�����˹��˱����ͺ˱�֪ͨ������� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PrintSendNoticeAfterInitService implements AfterInitService
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** �����洫�����ݵ����� */
    private VData mInputData;

    /** �����洫�����ݵ����� */
    private VData mResult = new VData();

    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();

    private TransferData mTransferData = new TransferData();

    /** �˱����� */
    private LCCUWMasterSchema mLCCUWMasterSchema = new LCCUWMasterSchema();
    private LCUWMasterSet mLCUWMasterSet = new LCUWMasterSet();

    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;

    /** ҵ�����ݲ����ַ��� */
    private String mContNo;
    private String mMissionID;
    private String mPrtSeq;
    private String mCode;
    private boolean mPatchFlag;
    private boolean mAutoSysCertSendOutFlag = true;

    /**������*/
    private LCContSchema mLCContSchema = new LCContSchema();

    /** ��ӡ����� */
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();
    private LCPolSet mLCPolSet = new LCPolSet();

    /** ��֤���ű�*/
    private LZSysCertifySchema mLZSysCertifySchema = new LZSysCertifySchema();

    public PrintSendNoticeAfterInitService()
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

        System.out.println("Start  Submit...");

        //mResult.clear();
        return true;
    }

    /**
     * ׼������ǰ̨ͳһ�洢����
     * �����������������򷵻�false,���򷵻�true
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();

        //��Ӻ˱�֪ͨ���ӡ���������
        map.put(mLOPRTManagerSchema, "UPDATE");

        map.put(mLCUWMasterSet, "UPDATE");
        //������������˱���������
        if (!mPatchFlag)
        {
            map.put(mLCCUWMasterSchema, "UPDATE");
        }

        //��������˱�֪ͨ���Զ����ű�����
        if (mAutoSysCertSendOutFlag)
        {
            map.put(mLZSysCertifySchema, "INSERT");
        }

        mResult.add(map);
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
            tError.moduleName = "PrintSendNoticeAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mContNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);

        //У�����������˱�����
        //У�鱣����Ϣ
        LCCUWMasterDB tLCCUWMasterDB = new LCCUWMasterDB();
        tLCCUWMasterDB.setContNo(mContNo);
        if (!tLCCUWMasterDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PrintSendNoticeAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mContNo + "���������˱�������Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCCUWMasterSchema.setSchema(tLCCUWMasterDB);

        // ����δ��ӡ״̬�ĺ˱�֪ͨ���ڴ�ӡ������ֻ����һ��
        // ������ͬһ���������ͣ�ͬһ���������룬ͬһ��������������
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

        tLOPRTManagerDB.setCode(mCode); //�˱�֪ͨ��
        tLOPRTManagerDB.setPrtSeq(mPrtSeq);
        tLOPRTManagerDB.setOtherNo(mContNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_CONT); //������
        tLOPRTManagerDB.setStateFlag("0");

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PrintSendNoticeAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "��ѯ��ӡ�������Ϣ����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLOPRTManagerSet.size() != 1)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PrintSendNoticeAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "�ڴ�ӡ������û�д���δ��ӡ״̬�ĺ˱�֪ͨ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLOPRTManagerSchema = tLOPRTManagerSet.get(1);
        if (mLOPRTManagerSchema.getPatchFlag() == null)
        {
            mPatchFlag = false;
        }
        else if (mLOPRTManagerSchema.getPatchFlag().equals("0"))
        {
            mPatchFlag = false;
        }
        else if (mLOPRTManagerSchema.getPatchFlag().equals("1"))
        {
            mPatchFlag = true;
        }
//   // ���Ҵ�ӡ����
//   LDCodeDB tLDCodeDB = new LDCodeDB();
//   tLDCodeDB.setCode( mCode);//��ӡ�˱�֪ͨ��
//   tLDCodeDB.setCodeType("print_service");
//   tLDCodeDB.setOtherSign("0") ;
//   LDCodeSet tLDCodeSet = new LDCodeSet();
//   tLDCodeSet = tLDCodeDB.query() ;
//   if( tLDCodeSet.size() !=1 )
//   {
//	 // @@������
//	 CError tError = new CError();
//	 tError.moduleName = "PrintSendNoticeAfterInitService";
//	 tError.functionName = "preparePrint";
//	 tError.errorMessage = "��LDCode�д�ӡ�����¼��ʧ!";
//	 this.mErrors.addOneError(tError) ;
//	 return false;
//   }

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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PrintSendNoticeAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PrintSendNoticeAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWSendNoticeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //���ҵ������
        if (mTransferData == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PrintSendNoticeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mContNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PrintSendNoticeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������ContNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ�������������ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PrintSendNoticeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //���ҵ��������
        mPrtSeq = (String) mTransferData.getValueByName("PrtSeq");
        if (mPrtSeq == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PrintSendNoticeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //���ҵ��������
        mCode = (String) mTransferData.getValueByName("Code");
        if (mCode == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PrintSendNoticeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {
        // �˱�������Ϣ
        if (!preparePrintSendNotice())
        {
            return false;
        }
        //׼�����ֺ˱���
        if (!preparePolUW())
        {
            return false;
        }
        //��ӡ����
        if (!preparePrint())
        {
            return false;
        }

        //����ϵͳ��֤��ӡ����
        if (!prepareAutoSysCertSendOut())
        {
            return false;
        }

        return true;
    }

    /**
     * ׼�����ֺ˱���Ϣ
     * �����������������򷵻�false,���򷵻�true
     */
    private boolean preparePolUW()
    {
        //��������־��Ϊ2��ʾ�Ѿ���ӡ���˱�֪ͨ��
        if (mLCCUWMasterSchema.getSpecFlag().equals("1") ||
                mLCCUWMasterSchema.getChangePolFlag().equals("1") ||
                mLCCUWMasterSchema.getPrintFlag().equals("1"))
        {
            mLCCUWMasterSchema.setPrintFlag("2");
            if (mLCCUWMasterSchema.getSpecFlag().equals("1"))
            {
                mLCCUWMasterSchema.setSpecFlag("2");
            }
            if (mLCCUWMasterSchema.getChangePolFlag().equals("1"))
            {
                mLCCUWMasterSchema.setChangePolFlag("2");
            }
        }
        //׼�����ֺ�ͬ������
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setContNo(mContNo);
        mLCPolSet = tLCPolDB.query();

        mLCUWMasterSet.clear();
        LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
        LCPolSchema tLCPolSchema = new LCPolSchema();
        LCUWMasterSchema tLCUWMasterSchema = new LCUWMasterSchema();

        for (int i = 1; i <= mLCPolSet.size(); i++)
        {
            tLCUWMasterSchema = new LCUWMasterSchema();
            tLCUWMasterDB = new LCUWMasterDB();
            tLCPolSchema = mLCPolSet.get(i);
            tLCUWMasterDB.setProposalNo(tLCPolSchema.getProposalNo());
            if (!tLCUWMasterDB.getInfo())
            {
                // @@������
                this.mErrors.copyAllErrors(tLCUWMasterDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "UWSendNoticeAfterInitService";
                tError.functionName = "prepareAllUW";
                tError.errorMessage = "LCUWMaster��ȡ��ʧ��!";
                this.mErrors.addOneError(tError);
                return false;
            }
            tLCUWMasterSchema.setSchema(tLCUWMasterDB);
            tLCUWMasterSchema.setAddPremFlag("2");
            tLCUWMasterSchema.setSpecFlag("2");
            tLCUWMasterSchema.setChangePolFlag("2");
            tLCUWMasterSchema.setPrintFlag("2");
            mLCUWMasterSet.add(tLCUWMasterSchema);
        }
        return true;
    }

    /**
     * ׼���˱�������Ϣ
     * �����������������򷵻�false,���򷵻�true
     */
    private boolean preparePrintSendNotice()
    {

        ////׼���˱�������Ϣ

        if (mLOPRTManagerSchema.getPatchFlag() == null)
        {
            mPatchFlag = false;
        }
        else if (mLOPRTManagerSchema.getPatchFlag().equals("0"))
        {
            mPatchFlag = false;
        }
        else if (mLOPRTManagerSchema.getPatchFlag().equals("1"))
        {
            mPatchFlag = true;
        }
        if (!mPatchFlag)
        {
            //���ǲ������������״̬
            mLCCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
            mLCCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());
            mLCCUWMasterSchema.setPrintFlag("2"); //���ͺ˱�֪ͨ��
        }
        return true;
    }

    /**
     * ׼����ӡ��Ϣ��
     * @return
     */
    private boolean preparePrint()
    {
        //׼����ӡ���������
        mLOPRTManagerSchema.setStateFlag("1");
        mLOPRTManagerSchema.setDoneDate(PubFun.getCurrentDate());
        mLOPRTManagerSchema.setDoneTime(PubFun.getCurrentTime());
        mLOPRTManagerSchema.setExeCom(mManageCom);
        mLOPRTManagerSchema.setExeOperator(mOperater);
        return true;
    }

    /**
     * ׼���˱�������Ϣ
     * �����������������򷵻�false,���򷵻�true
     */
    private boolean prepareAutoSysCertSendOut()
    {
        //�жϴ�ӡ���ĵ����Ƿ���Ҫ�Զ�����
        LDCodeDB tLDCodeDB = new LDCodeDB();
        tLDCodeDB.setCodeType("syscertifycode");
        tLDCodeDB.setCode(mCode);
        if (!tLDCodeDB.getInfo())
        {
            mAutoSysCertSendOutFlag = false;
            return true;
        }

        LMCertifySubDB tLMCertifySubDB = new LMCertifySubDB();
        tLMCertifySubDB.setCertifyCode(tLDCodeDB.getCodeName());
        if (!tLMCertifySubDB.getInfo())
        {
            // @@������
            this.mErrors.copyAllErrors(tLMCertifySubDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "AutoSysCertSendOutBL";
            tError.functionName = "JudgeAutoSend";
            tError.errorMessage = "��ѯ��Ϣʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        String tflag = tLMCertifySubDB.getAutoSend();
        if (StrTool.cTrim(tflag).equals("") || StrTool.cTrim(tflag).equals("N"))
        {
            mAutoSysCertSendOutFlag = false;
            return true;
        }

        ////׼���Զ����ű���Ϣ
        if (mAutoSysCertSendOutFlag)
        {
            //��Ҫ�Զ����ţ����������״̬
            LZSysCertifySchema tLZSysCertifyschema = new LZSysCertifySchema();
            tLZSysCertifyschema.setCertifyCode(tLDCodeDB.getCodeName());
            tLZSysCertifyschema.setCertifyNo(mPrtSeq);
            tLZSysCertifyschema.setSendOutCom("A" + mGlobalInput.ManageCom);
            tLZSysCertifyschema.setReceiveCom("D" +
                    mLOPRTManagerSchema.getAgentCode());
            System.out.println("D" + mLOPRTManagerSchema.getAgentCode());
            tLZSysCertifyschema.setHandler("SYS");
            tLZSysCertifyschema.setStateFlag("0");
            tLZSysCertifyschema.setHandleDate(PubFun.getCurrentDate());
            tLZSysCertifyschema.setTakeBackNo(PubFun1.CreateMaxNo("TAKEBACKNO",
                    PubFun.getNoLimit(mManageCom.substring(1))));
            tLZSysCertifyschema.setSendNo(PubFun1.CreateMaxNo("TAKEBACKNO",
                    PubFun.getNoLimit(mManageCom.substring(1))));
            tLZSysCertifyschema.setOperator(mOperater);
            tLZSysCertifyschema.setMakeDate(PubFun.getCurrentDate());
            tLZSysCertifyschema.setMakeTime(PubFun.getCurrentTime());
            tLZSysCertifyschema.setModifyDate(PubFun.getCurrentDate());
            tLZSysCertifyschema.setModifyTime(PubFun.getCurrentTime());
            mLZSysCertifySchema = tLZSysCertifyschema;
        }
        return true;
    }

    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return
     */
    private boolean prepareTransferData()
    {

        mTransferData.setNameAndValue("CertifyCode", mLZSysCertifySchema.getCertifyCode());
        mTransferData.setNameAndValue("ValidDate", mLZSysCertifySchema.getValidDate());
        mTransferData.setNameAndValue("SendOutCom", mLZSysCertifySchema.getSendOutCom());
        mTransferData.setNameAndValue("ReceiveCom", mLZSysCertifySchema.getReceiveCom());
        mTransferData.setNameAndValue("Handler", mLZSysCertifySchema.getHandler());
        mTransferData.setNameAndValue("HandleDate", mLZSysCertifySchema.getHandleDate());
        mTransferData.setNameAndValue("Operator", mLZSysCertifySchema.getOperator());
        mTransferData.setNameAndValue("MakeDate", mLZSysCertifySchema.getMakeDate());
        mTransferData.setNameAndValue("SendNo", mLZSysCertifySchema.getSendNo());
        mTransferData.setNameAndValue("TakeBackNo", mLZSysCertifySchema.getTakeBackNo());

        //Ϊ����˱�֪ͨ��ڵ�׼����������
        LAAgentDB tLAAgentDB = new LAAgentDB();
        LAAgentSet tLAAgentSet = new LAAgentSet();
        tLAAgentDB.setAgentCode(mLCContSchema.getAgentCode());
        tLAAgentSet = tLAAgentDB.query();
        if (tLAAgentSet == null || tLAAgentSet.size() != 1)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "UWAutoHealthAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "�����˱�LAAgent��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLAAgentSet.get(1).getAgentGroup() == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "UWAutoHealthAfterInitService";
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
            tError.moduleName = "UWAutoHealthAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "������չҵ������LABranchGroup��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLABranchGroupSet.get(1).getBranchAttr() == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "UWAutoHealthAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "������չҵ������LABranchGroup��չҵ������Ϣ��ʧ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (mPatchFlag)
        {
            mTransferData.setNameAndValue("OldPrtSeq", mLOPRTManagerSchema.getOldPrtSeq());
        }
        else
        {
            mTransferData.setNameAndValue("OldPrtSeq", mLOPRTManagerSchema.getPrtSeq());
        }
        mTransferData.setNameAndValue("AgentCode", mLCContSchema.getAgentCode());
        mTransferData.setNameAndValue("Code", PrintManagerBL.CODE_UW);
        mTransferData.setNameAndValue("DoneDate", mLOPRTManagerSchema.getDoneDate());
        mTransferData.setNameAndValue("ManageCom", mLOPRTManagerSchema.getManageCom());
        mTransferData.setNameAndValue("ExeOperator", mLOPRTManagerSchema.getExeOperator());
        return true;
    }

    /**
     * ���ش����Ľ��
     * @return VData
     */
    public VData getResult()
    {
        return mResult;
    }

    /**
     * ���ع������е�Lwfieldmap��������ֵ
     * @return TransferData
     */
    public TransferData getReturnTransferData()
    {
        return mTransferData;
    }

    /**
     * ���ش������
     * @return CErrors
     */
    public CErrors getErrors()
    {
        return mErrors;
    }
}

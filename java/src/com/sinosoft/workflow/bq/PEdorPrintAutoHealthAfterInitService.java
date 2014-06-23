package com.sinosoft.workflow.bq;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: </p>
 * <p>Description:�������ڵ�����:��ȫ�˹��˱����ͺ˱�֪ͨ������� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PEdorPrintAutoHealthAfterInitService implements AfterInitService
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
    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;
    private String mOperate;
    /** ҵ�����ݲ����ַ��� */
    private String mEdorNo;
    private String mContNo;
    private String mMissionID;
    private String mPrtSeq;
    private String mCode;
    private boolean mPatchFlag;
    private boolean mAutoSysCertSendOutFlag = true;
    private Reflections mReflections = new Reflections();

    /**ִ�б�ȫ��������Լ�������0000000003*/
    /**������*/
    private LCPolSchema mLCPolSchema = new LCPolSchema();
    private LCContSchema mLCContSchema = new LCContSchema();
    /** ��ȫ�˱����� */
    private LPUWMasterMainSchema mLPUWMasterMainSchema = new
            LPUWMasterMainSchema();
    /** ��ӡ����� */
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();
    /** ��֤���ű�*/
    private LZSysCertifySchema mLZSysCertifySchema = new LZSysCertifySchema();
    /** �������*/
    private LPPENoticeSchema mLPPENoticeSchema = new LPPENoticeSchema();
    public PEdorPrintAutoHealthAfterInitService()
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

        //��ӱ�ȫ�����˱���������
        if (mPatchFlag == false)
        {
            map.put(mLPUWMasterMainSchema, "UPDATE");
            map.put(mLPPENoticeSchema, "UPDATE");
        }

        //��ӱ�ȫ���֪ͨ���Զ����ű�����
        if (mAutoSysCertSendOutFlag == true)
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
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "������ͬ" + mContNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);

        //У�鱣ȫ�����˱�����
        //У�鱣����Ϣ
        LPUWMasterMainDB tLPUWMasterMainDB = new LPUWMasterMainDB();
        tLPUWMasterMainDB.setEdorNo(mEdorNo);
        tLPUWMasterMainDB.setContNo(mContNo);
        if (!tLPUWMasterMainDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mContNo + "��ȫ�����˱�������Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLPUWMasterMainSchema.setSchema(tLPUWMasterMainDB);

        // ����δ��ӡ״̬�ĺ˱�֪ͨ���ڴ�ӡ������ֻ����һ��
        // ������ͬһ���������ͣ�ͬһ���������룬ͬһ��������������
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

        tLOPRTManagerDB.setCode(mCode); //�˱�֪ͨ��
        tLOPRTManagerDB.setPrtSeq(mPrtSeq);
        tLOPRTManagerDB.setOtherNo(mContNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_CONT); //������
        tLOPRTManagerDB.setStandbyFlag2(mEdorNo);
        tLOPRTManagerDB.setStateFlag("0");

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "��ѯ��ӡ�������Ϣ����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLOPRTManagerSet.size() != 1)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
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
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
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
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
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
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
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
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
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
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
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
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������PolNoʧ��!";
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
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
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
        if (preparePrintAutoHealth() == false)
        {
            return false;
        }

        //��ӡ����
        if (preparePrint() == false)
        {
            return false;
        }

        //����ϵͳ��֤��ӡ����
        if (prepareAutoSysCertSendOut() == false)
        {
            return false;
        }

        return true;

    }

    /**
     * ׼���˱�������Ϣ
     * �����������������򷵻�false,���򷵻�true
     */
    private boolean preparePrintAutoHealth()
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

        if (mPatchFlag == false) //���ǲ������������״̬
        {
            mLPUWMasterMainSchema.setModifyDate(PubFun.getCurrentDate());
            mLPUWMasterMainSchema.setModifyTime(PubFun.getCurrentTime());
            mLPUWMasterMainSchema.setHealthFlag("2"); //�Ѵ�ӡ���֪ͨ���ʶ

            //�������֪ͨ��
            LPPENoticeDB tLPPENoticeDB = new LPPENoticeDB();
            tLPPENoticeDB.setEdorNo(mEdorNo);
            tLPPENoticeDB.setContNo(mContNo);
            tLPPENoticeDB.setPrintFlag("0");
            tLPPENoticeDB.setCustomerNo(mLOPRTManagerSchema.getStandbyFlag1()); //�����ֶ�1��ſͻ���
            LPPENoticeSet tLPPENoticeSet = new LPPENoticeSet();
            tLPPENoticeSet = tLPPENoticeDB.query();
            if (tLPPENoticeSet.size() < 1)
            {
                buildError("preparePrintAutoHealth", "��ȡ�����֪ͨ������ʱ��������");
                return false;
            }
            mLPPENoticeSchema = tLPPENoticeSet.get(1);
            mLPPENoticeSchema.setPrintFlag("1");
            mLPPENoticeSchema.setModifyDate(PubFun.getCurrentDate());
            mLPPENoticeSchema.setModifyTime(PubFun.getCurrentTime());
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
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "prepareAutoSysCertSendOut";
            tError.errorMessage = "��ѯ��֤����������Ϣ��ʧ��!";
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
        if (mAutoSysCertSendOutFlag == true) //��Ҫ�Զ����ţ����������״̬
        {
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
        //Ϊ�������л������֪ͨ��ڵ�׼����������
        mTransferData.setNameAndValue("CertifyCode",
                mLZSysCertifySchema.getCertifyCode());
        mTransferData.setNameAndValue("ValidDate",
                mLZSysCertifySchema.getValidDate());
        mTransferData.setNameAndValue("SendOutCom",
                mLZSysCertifySchema.getSendOutCom());
        mTransferData.setNameAndValue("ReceiveCom",
                mLZSysCertifySchema.getReceiveCom());
        mTransferData.setNameAndValue("Handler", mLZSysCertifySchema.getHandler());
        mTransferData.setNameAndValue("HandleDate",
                mLZSysCertifySchema.getHandleDate());
        mTransferData.setNameAndValue("Operator",
                mLZSysCertifySchema.getOperator());
        mTransferData.setNameAndValue("MakeDate",
                mLZSysCertifySchema.getMakeDate());
        mTransferData.setNameAndValue("SendNo", mLZSysCertifySchema.getSendNo());
        mTransferData.setNameAndValue("TakeBackNo",
                mLZSysCertifySchema.getTakeBackNo());
        //Ϊ�������֪ͨ��ڵ�׼����������
        LAAgentDB tLAAgentDB = new LAAgentDB();
        LAAgentSet tLAAgentSet = new LAAgentSet();
        tLAAgentDB.setAgentCode(mLCPolSchema.getAgentCode());
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
        if (mPatchFlag == true)
        {
            mTransferData.setNameAndValue("OldPrtSeq",
                    mLOPRTManagerSchema.getOldPrtSeq());
        }
        else
        {
            mTransferData.setNameAndValue("OldPrtSeq",
                    mLOPRTManagerSchema.getPrtSeq());
        }
        mTransferData.setNameAndValue("AgentCode", mLCPolSchema.getAgentCode());
        mTransferData.setNameAndValue("Code", PrintManagerBL.CODE_PEdorPE);
        mTransferData.setNameAndValue("DoneDate",
                mLOPRTManagerSchema.getDoneDate());
        mTransferData.setNameAndValue("ManageCom",
                mLOPRTManagerSchema.getManageCom());
        mTransferData.setNameAndValue("ExeOperator",
                mLOPRTManagerSchema.getExeOperator());

        return true;
    }

    private void buildError(String szFunc, String szErrMsg)
    {
        CError cError = new CError();

        cError.moduleName = "PEdorBodyCheckPrintBL";
        cError.functionName = szFunc;
        cError.errorMessage = szErrMsg;
        this.mErrors.addOneError(cError);
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
}

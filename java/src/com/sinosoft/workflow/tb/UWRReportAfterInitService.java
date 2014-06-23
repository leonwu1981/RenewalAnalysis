/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import java.util.Date;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;
import com.sinosoft.workflowengine.AfterInitService;


/**
 * <p>Title:�������ڵ�����:����Լ������֪ͨ�� </p>
 * <p>Description: ������֪ͨ�鹤����AfterInit������ </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: SinoSoft </p>
 * @author HYQ
 * @version 1.0
 */

public class UWRReportAfterInitService implements AfterInitService
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** �����洫�����ݵ����� */
    private VData mResult = new VData();

    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();

    /**���������� */
    ActivityOperator mActivityOperator = new ActivityOperator();

    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;

    /** ҵ�����ݲ����ַ��� */
    private String mContNo;
    private String mCustomerNo;
    private String mInsuredNo;
    private String mName;
    private String mMissionID;
    String mPrtSeq = "";

    /**������*/
    private LCContSchema mLCContSchema = new LCContSchema();

//    /** �����˱����� */
//    private LCCUWMasterSchema mLCCUWMasterSchema = new LCCUWMasterSchema();

    /** �������� */
    private LCRReportSchema mLCRReportSchema = new LCRReportSchema();
    private LCRReportItemSet mLCRReportItemSet = new LCRReportItemSet();
    private LCRReportItemSet mNewLCRReportItemSet = new LCRReportItemSet();
    /** ��ӡ����� */
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();

    /**�Զ���־**/
//    private boolean mAutoSysCertSendOutFlag = true;

    public UWRReportAfterInitService()
    {
    }


    /**
     * �������ݵĹ�������
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
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

        System.out.println("After UWRReportAfterInitService Submit...");

        return true;
    }

    /**
     * ׼������ǰ̨ͳһ�洢����
     * �����������������򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();

        //�����������
        if (mLCRReportSchema != null)
        {
            map.put(mLCRReportSchema, "INSERT");
        }

        //������֪ͨ���ӡ���������
        map.put(mLOPRTManagerSchema, "INSERT");

        //������������˱�����֪ͨ���ӡ���������
//        map.put(mLCCUWMasterSchema, "UPDATE");
        map.put(mNewLCRReportItemSet, "INSERT");
        mResult.add(map);
        return true;
    }

    /**
     * У��ҵ������
     * @return boolean
     */
    private boolean checkData()
    {
        //У�鱣����Ϣ
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mContNo);
        if (!tLCContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mContNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);

        //У�鱣�������˱�����Ϣ
        mInsuredNo = mLCContSchema.getInsuredNo();
        if (mInsuredNo == null)
        {
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mContNo + "�ı����˱�����Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

//        //У�����������˱�����
//        //У�鱣����Ϣ
//        LCCUWMasterDB tLCCUWMasterDB = new LCCUWMasterDB();
//        tLCCUWMasterDB.setContNo(mContNo);
//        if (!tLCCUWMasterDB.getInfo())
//        {
//            CError tError = new CError();
//            tError.moduleName = "UWRReportAfterInitService";
//            tError.functionName = "checkData";
//            tError.errorMessage = "����" + mContNo + "���������˱�������Ϣ��ѯʧ��!";
//            this.mErrors.addOneError(tError);
//            return false;
//        }
//
//        mLCCUWMasterSchema.setSchema(tLCCUWMasterDB);

        // ����δ��ӡ״̬�ĺ˱�֪ͨ���ڴ�ӡ������ֻ����һ��
        // ������ͬһ���������ͣ�ͬһ���������룬ͬһ��������������
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

        tLOPRTManagerDB.setCode(PrintManagerBL.CODE_MEET); //����֪ͨ��
        tLOPRTManagerDB.setOtherNo(mContNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_CONT); //������
        tLOPRTManagerDB.setStateFlag("0"); //֪ͨ��δ��ӡ

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "��ѯ��ӡ�������Ϣ����!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //ȡ����������
        LDPersonDB tLDPersonDB = new LDPersonDB();
        tLDPersonDB.setCustomerNo(mCustomerNo);
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
        mName = tLDPersonDB.getName();

        return true;
    }

    /**
     * �����������еõ����ж���
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    private boolean getInputData(VData cInputData, String cOperate)
    {
        //�����������еõ����ж���
        //���ȫ�ֹ�������
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);

        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
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
            tError.moduleName = "UWRReportAfterInitService";
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
            tError.moduleName = "UWRReportAfterInitService";
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
            tError.moduleName = "UWRReportAfterInitService";
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
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������ContNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mCustomerNo = (String) mTransferData.getValueByName("CustomerNo");
        if (mCustomerNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������CustomerNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //���ҵ������֪ͨ����
        mLCRReportSchema = (LCRReportSchema) mTransferData.getValueByName(
                "LCRReportSchema");
        if (mLCRReportSchema == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨������ҵ����������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCRReportItemSet = (LCRReportItemSet) mTransferData.getValueByName("LCRReportItemSet");
        if (mLCRReportItemSet == null || mLCRReportItemSet.size() <= 0)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨������ҵ��������Ŀ��������ʧ��!";
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
            tError.moduleName = "UWRReportAfterInitService";
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
     * @return boolean
     */
    private boolean dealData()
    {
        VData tVData = new VData();
        String strNoLimit = PubFun.getNoLimit(mGlobalInput.ComCode);
        //mPrtSeq = PubFun1.CreateMaxNo("PRTSEQNO", strNoLimit);
        mPrtSeq = PubFun1.CreateMaxNo("QIDIAO", strNoLimit, tVData);
        // �˱�������Ϣ
        if (!prepareReport())
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
     * ׼������������Ϣ
     * �����������������򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean prepareReport()
    {
        LCRReportItemSchema tLCRReportItemSchema;

        mLCRReportSchema.setPrtSeq(mPrtSeq);
        mLCRReportSchema.setContNo(mLCContSchema.getContNo());
        mLCRReportSchema.setGrpContNo(mLCContSchema.getGrpContNo());
        mLCRReportSchema.setProposalContNo(mLCContSchema.getProposalContNo());
        mLCRReportSchema.setAppntNo(mLCContSchema.getAppntNo());
        mLCRReportSchema.setAppntName(mLCContSchema.getAppntName());
        mLCRReportSchema.setCustomerNo(mCustomerNo);
        mLCRReportSchema.setName(mName);
        mLCRReportSchema.setManageCom(mManageCom);
        mLCRReportSchema.setReplyContente("");
        mLCRReportSchema.setContente(mLCRReportSchema.getContente());
        mLCRReportSchema.setReplyFlag("0");
        mLCRReportSchema.setOperator(mOperater);
        mLCRReportSchema.setMakeDate(PubFun.getCurrentDate());
        mLCRReportSchema.setMakeTime(PubFun.getCurrentTime());
        mLCRReportSchema.setReplyOperator("");
        mLCRReportSchema.setReplyDate("");
        mLCRReportSchema.setReplyTime("");
        mLCRReportSchema.setModifyDate(PubFun.getCurrentDate());
        mLCRReportSchema.setModifyTime(PubFun.getCurrentTime());

        ////׼���˱�������Ϣ

//        mLCCUWMasterSchema.setOperator(mOperater);
//        mLCCUWMasterSchema.setManageCom(mManageCom);
//        mLCCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
//        mLCCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());
//        mLCCUWMasterSchema.setReportFlag("1");          //������֪ͨ�鲢�ȴ��ظ�

        for (int i = 1; i <= mLCRReportItemSet.size(); i++)
        {
            tLCRReportItemSchema = new LCRReportItemSchema();

            tLCRReportItemSchema.setContNo(mLCContSchema.getContNo());
            tLCRReportItemSchema.setGrpContNo(mLCContSchema.getGrpContNo());
            tLCRReportItemSchema.setProposalContNo(mLCContSchema.
                    getProposalContNo());
            tLCRReportItemSchema.setPrtSeq(mPrtSeq);
            tLCRReportItemSchema.setRReportItemCode(mLCRReportItemSet.get(i).
                    getRReportItemCode());
            tLCRReportItemSchema.setRReportItemName(mLCRReportItemSet.get(i).
                    getRReportItemName());
            tLCRReportItemSchema.setModifyDate(PubFun.getCurrentDate());
            tLCRReportItemSchema.setModifyTime(PubFun.getCurrentTime());

            mNewLCRReportItemSet.add(tLCRReportItemSchema);
        }
        return true;
    }


    /**
     * ��ӡ��Ϣ��
     * @return boolean
     */
    private boolean preparePrint()
    {

        LDSysVarDB tLDSysVarDB = new LDSysVarDB();
        tLDSysVarDB.setSysVar("URGEInterval");

        if (!tLDSysVarDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "UWSendPrintBL";
            tError.functionName = "prepareURGE";
            tError.errorMessage = "û�������߷����!";
            this.mErrors.addOneError(tError);
            return false;
        }
        FDate tFDate = new FDate();
        int tInterval = Integer.parseInt(tLDSysVarDB.getSysVarValue());
        System.out.println(tInterval);

        Date tDate = PubFun.calDate(tFDate.getDate(PubFun.getCurrentDate()),
                tInterval, "D", null);
        System.out.println(tDate); //ȡԤ�ƴ߰�����
        //׼����ӡ���������
        mLOPRTManagerSchema.setPrtSeq(mPrtSeq);
        mLOPRTManagerSchema.setOtherNo(mContNo);
        mLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_CONT); //������
        mLOPRTManagerSchema.setCode(PrintManagerBL.CODE_MEET); //����
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
        mLOPRTManagerSchema.setStandbyFlag1(mCustomerNo); //����˱���
        mLOPRTManagerSchema.setStandbyFlag2(mLCContSchema.getPrtNo()); //�������˱���
        mLOPRTManagerSchema.setStandbyFlag3(mMissionID);
        mLOPRTManagerSchema.setOldPrtSeq(mPrtSeq);
        mLOPRTManagerSchema.setForMakeDate(tDate);
        return true;
    }

    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return boolean
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
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "�����˱�LAAgent��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLAAgentSet.get(1).getAgentGroup() == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
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
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "������չҵ������LABranchGroup��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLABranchGroupSet.get(1).getBranchAttr() == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
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
        mTransferData.setNameAndValue("CustomerNo", mCustomerNo);

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

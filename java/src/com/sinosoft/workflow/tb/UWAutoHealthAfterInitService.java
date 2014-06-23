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
 * <p>Title: �������ڵ�����:����Լ�����֪ͨ�� </p>
 * <p>Description: �����������֪ͨ��AfterInit������</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class UWAutoHealthAfterInitService implements AfterInitService
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
    private String mMissionID;

    /**������*/
    private LCContSchema mLCContSchema = new LCContSchema();
    private String mPrtSeq;

    /** ��ȫ�˱����� */
    private LCCUWMasterSchema mLCCUWMasterSchema = new LCCUWMasterSchema();

    /** ����������� */
    private LCPENoticeSchema mLCPENoticeSchema = new LCPENoticeSchema();

    /** ���������Ŀ�� */
    private LCPENoticeItemSet mLCPENoticeItemSet = new LCPENoticeItemSet();

    /** ��ӡ����� */
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();

    public UWAutoHealthAfterInitService()
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

//        System.out.println("Start SysUWNoticeBL Submit...");

        //mResult.clear();
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
            tError.moduleName = "UWAutoHealthAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mContNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);

        //У���Ƿ���δ��ӡ�����֪ͨ��
        LCPENoticeDB tLCPENoticeDB = new LCPENoticeDB();
        tLCPENoticeDB.setContNo(mContNo);
        tLCPENoticeDB.setCustomerNo(mCustomerNo);

        if (tLCPENoticeDB.getInfo())
        {
            if (tLCPENoticeDB.getPrintFlag().equals("0"))
            {
                CError tError = new CError();
                tError.moduleName = "UWAutoHealthAfterInitService";
                tError.functionName = "checkData";
                tError.errorMessage = "���֪ͨ�Ѿ�¼��,��δ��ӡ������¼�����������!";
                this.mErrors.addOneError(tError);
                return false;
            }
        }

//        //У�鱣ȫ�����˱�����
//        //У�鱣����Ϣ
//        LCCUWMasterDB tLCCUWMasterDB = new LCCUWMasterDB();
//        tLCCUWMasterDB.setContNo(mContNo);
//        if (!tLCCUWMasterDB.getInfo())
//        {
//            CError tError = new CError();
//            tError.moduleName = "UWRReportAfterInitService";
//            tError.functionName = "checkData";
//            tError.errorMessage = "����" + mContNo + "��ȫ�����˱�������Ϣ��ѯʧ��!";
//            this.mErrors.addOneError(tError);
//            return false;
//        }
//
//        mLCCUWMasterSchema.setSchema(tLCCUWMasterDB);

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

        //ԭ�л�ȡ��ˮ�ķ��������ڸ����ú����ٵ�������޸�
//        mPrtSeq = PubFun1.CreateMaxNo("PRTSEQNO", strNoLimit);
        mPrtSeq = PubFun1.CreateMaxNo("TIJIAN", strNoLimit, tVData);

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
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    private boolean getInputData(VData cInputData, String cOperate)
    {
        //�����������еõ����ж���
        //���ȫ�ֹ�������
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);

        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWAutoHealthAfterInitService";
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
            tError.moduleName = "UWAutoHealthAfterInitService";
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
            tError.moduleName = "UWAutoHealthAfterInitService";
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
            tError.moduleName = "UWAutoHealthAfterInitService";
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
            tError.moduleName = "UWAutoHealthAfterInitService";
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
            tError.moduleName = "UWAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������CustomerNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //���ҵ�����֪ͨ����
        mLCPENoticeSchema = (LCPENoticeSchema) mTransferData.getValueByName("LCPENoticeSchema");
        if (mLCPENoticeSchema == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWAutoHealthAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWAutoHealthAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWAutoHealthAfterInitService";
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
     * @return boolean
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
            tError.moduleName = "UWAutoHealthAfterInitService";
            tError.functionName = "prepareHealth";
            tError.errorMessage = "ȡ����������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ȡ���������
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

        mLCPENoticeSchema.setGrpContNo(mLCContSchema.getGrpContNo());
        mLCPENoticeSchema.setContNo(mLCContSchema.getContNo());
        mLCPENoticeSchema.setProposalContNo(mLCContSchema.getProposalContNo());
        mLCPENoticeSchema.setCustomerNo(mCustomerNo);
        mLCPENoticeSchema.setName(tLDPersonDB.getName());
        mLCPENoticeSchema.setPrtSeq(mPrtSeq);
        //mLCPENoticeSchema.setPEDate(mLCPENoticeSchema.getPEDate());
        //mLCPENoticeSchema.setPEAddress(mLCPENoticeSchema.getPEAddress());
        mLCPENoticeSchema.setPrintFlag("0");
        mLCPENoticeSchema.setAppName(mLCContSchema.getAppntName());
        mLCPENoticeSchema.setAgentCode(mLCContSchema.getAgentCode());
        mLCPENoticeSchema.setAgentName(tLAAgentDB.getName());
        mLCPENoticeSchema.setManageCom(mLCContSchema.getManageCom());
        //mLCPENoticeSchema.setPEBeforeCond(mLCPENoticeSchema.getPEBeforeCond());
        mLCPENoticeSchema.setOperator(mOperater); //����Ա
        mLCPENoticeSchema.setMakeDate(PubFun.getCurrentDate());
        mLCPENoticeSchema.setMakeTime(PubFun.getCurrentTime());
        mLCPENoticeSchema.setModifyDate(PubFun.getCurrentDate());
        mLCPENoticeSchema.setModifyTime(PubFun.getCurrentTime());
        //mLCPENoticeSchema.setRemark(mLCPENoticeSchema.getRemark());

        //׼����������Ŀ��Ϣ
        for (int i = 1; i <= mLCPENoticeItemSet.size(); i++)
        {

            mLCPENoticeItemSet.get(i).setGrpContNo(mLCContSchema.getGrpContNo());
            mLCPENoticeItemSet.get(i).setContNo(mLCContSchema.getContNo());
            mLCPENoticeItemSet.get(i).setProposalContNo(mLCContSchema.getProposalContNo());
            mLCPENoticeItemSet.get(i).setPrtSeq(mPrtSeq);
            //mLCPENoticeItemSet.get(i).setContNo( mContNo );
            //mLCPENoticeItemSet.get(i).setPEItemCode(); //�˱��������
            //mLCPENoticeItemSet.get(i).setPEItemName(); //�˱�������Ϣ
            //mLCPENoticeItemSet.get(i).setCustomerNo(mCustomerNo);
            mLCPENoticeItemSet.get(i).setModifyDate(PubFun.getCurrentDate()); //��ǰֵ
            mLCPENoticeItemSet.get(i).setModifyTime(PubFun.getCurrentTime());
            //mLCPENoticeItemSet.get(i).setFreePE() ;
        }
        //׼���˱�������Ϣ
        mLCCUWMasterSchema.setOperator(mOperater);
        mLCCUWMasterSchema.setManageCom(mManageCom);
        mLCCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
        mLCCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());

        return true;
    }

    /**
     * ��ӡ��Ϣ��
     * @return boolean
     */
    private boolean preparePrint()
    {
        // ����δ��ӡ״̬��֪ͨ���ڴ�ӡ������ֻ����һ��
        // ������ͬһ���������ͣ�ͬһ���������룬ͬһ��������������
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

        tLOPRTManagerDB.setCode(PrintManagerBL.CODE_PE); //���
        tLOPRTManagerDB.setOtherNo(mContNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_CONT); //������
        tLOPRTManagerDB.setStandbyFlag1(mCustomerNo);
        tLOPRTManagerDB.setStateFlag("0");

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "UWAutoHealthAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "��ѯ��ӡ�������Ϣ����!";
            this.mErrors.addOneError(tError);
            return false;
        }

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
//        System.out.println(tInterval);
        //ȡԤ�ƴ߰�����
        Date tDate = PubFun.calDate(tFDate.getDate(PubFun.getCurrentDate()), tInterval, "D", null);
//        System.out.println(tDate);

        //׼����ӡ���������
        mLOPRTManagerSchema.setPrtSeq(mPrtSeq);
        mLOPRTManagerSchema.setOtherNo(mContNo);
        mLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_CONT); //������
        mLOPRTManagerSchema.setCode(PrintManagerBL.CODE_PE); //���
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
        mLOPRTManagerSchema.setStandbyFlag1(mCustomerNo); //�������˱���
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

        mTransferData.setNameAndValue("PrtSeq", mLOPRTManagerSchema.getPrtSeq());
        mTransferData.setNameAndValue("OldPrtSeq", mLOPRTManagerSchema.getPrtSeq());
        mTransferData.setNameAndValue("AgentCode", mLCContSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup", tLAAgentSet.get(1).getAgentGroup());
        mTransferData.setNameAndValue("BranchAttr", tLABranchGroupSet.get(1).getBranchAttr());
        mTransferData.setNameAndValue("ManageCom", mLCContSchema.getManageCom());
        return true;
    }

    /**
     * ������׼����̨�ύ����
     * @return boolean
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();

        //������֪ͨ����
        map.put(mLCPENoticeSchema, "INSERT");

        //��������Ŀ����
        map.put(mLCPENoticeItemSet, "INSERT");

        //������֪ͨ���ӡ���������
        map.put(mLOPRTManagerSchema, "INSERT");

        //��ӱ�ȫ�����˱�����֪ͨ���ӡ���������
//        map.put(mLCCUWMasterSchema, "UPDATE");

        mResult.add(map);
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

    public static void main(String[] args)
    {
        //SysUWNoticeBL sysUWNoticeBL1 = new SysUWNoticeBL();
    }
}

/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.xb;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.LAAgentSet;
import com.sinosoft.lis.vschema.LABranchGroupSet;
import com.sinosoft.lis.vschema.LOPRTManagerSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: </p>
 * <p>Description:�������ڵ�����:�����˹��˱�����¼������� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PRnewUWRReportAfterInitService implements AfterInitService
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
//    private Reflections mReflections = new Reflections();

    /**ִ����������������֪ͨ��������0000000004*/
    /**������*/
    private LCPolSchema mLCPolSchema = new LCPolSchema();
    /** �����˱����� */
    private LCUWMasterSchema mLCUWMasterSchema = new LCUWMasterSchema();
    /** �������� */
    private LCRReportSchema mLCRReportSchema = new LCRReportSchema();
    /** ��ӡ����� */
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();

    public PRnewUWRReportAfterInitService()
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

        System.out.println("After PRnewUWRReportAfterInitService Submit...");

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

        //�����������
        if (mLCRReportSchema != null)
        {
            map.put(mLCRReportSchema, "INSERT");
        }

        //������֪ͨ���ӡ���������
        map.put(mLOPRTManagerSchema, "INSERT");

        //������������˱�����֪ͨ���ӡ���������
        map.put(mLCUWMasterSchema, "UPDATE");

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
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setPolNo(mPolNo);
        if (!tLCPolDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorUWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mPolNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCPolSchema.setSchema(tLCPolDB);

        //У�鱣�������˱�����Ϣ
        mInsuredNo = mLCPolSchema.getInsuredNo();
        if (mInsuredNo == null)
        {
            CError tError = new CError();
            tError.moduleName = "PEdorUWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mPolNo + "�ı����˱�����Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //У�����������˱�����
        //У�鱣����Ϣ
        LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
        tLCUWMasterDB.setProposalNo(mPolNo);
        if (!tLCUWMasterDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorUWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mPolNo + "���������˱�������Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCUWMasterSchema.setSchema(tLCUWMasterDB);

//   if (mLPUWMasterMainSchema != null && mLPUWMasterMainSchema.getReportFlag().equals("1"))
//	 {
//	   CError tError = new CError();
//	   tError.moduleName = "PEdorUWRReportAfterInitService";
//	   tError.functionName = "checkData";
//	   tError.errorMessage = "�Ѿ�������֪ͨ��,��δ��ӡ,�����ٴν�������¼��!";
//	   this.mErrors .addOneError(tError) ;
//	   return false;
//	 }


        // ����δ��ӡ״̬�ĺ˱�֪ͨ���ڴ�ӡ������ֻ����һ��
        // ������ͬһ���������ͣ�ͬһ���������룬ͬһ��������������
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

        tLOPRTManagerDB.setCode(PrintManagerBL.CODE_PRnewMEET); //����֪ͨ��
        tLOPRTManagerDB.setOtherNo(mPolNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_INDPOL); //������
        tLOPRTManagerDB.setStateFlag("0");

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWRReportAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "��ѯ��ӡ�������Ϣ����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLOPRTManagerSet.size() != 0)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWRReportAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "�ڴ�ӡ����������һ������δ��ӡ״̬������֪ͨ��!";
            this.mErrors.addOneError(tError);
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
            tError.moduleName = "PRnewUWRReportAfterInitService";
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
            tError.moduleName = "PRnewUWRReportAfterInitService";
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
            tError.moduleName = "PRnewUWRReportAfterInitService";
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
            tError.moduleName = "PRnewUWRReportAfterInitService";
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
            tError.moduleName = "PRnewUWRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������PolNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //���ҵ������֪ͨ����
        mLCRReportSchema = (LCRReportSchema) mTransferData.getValueByName("LCRReportSchema");
        if (mLCRReportSchema == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewUWRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨������ҵ����������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (mLCRReportSchema.getContente() == null
                || mLCRReportSchema.getContente().trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewUWRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨������ҵ��������������ʧ��!";
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
            tError.moduleName = "PRnewUWRReportAfterInitService";
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
     */
    private boolean prepareReport()
    {

        //׼������
        //������ˮ��
        String tsql = "select max(serialno) from lcrreport where polno = '" + mPolNo + "'";

        ExeSQL tExeSQL = new ExeSQL();
        String tSerialno = tExeSQL.getOneValue(tsql);

        if (tSerialno == null)
        {
            tSerialno = "0";
        }
        else
        {
            Integer ttno = Integer.valueOf(tSerialno.trim());
            int tno = ttno.intValue();
            tno = +1;
            tSerialno = String.valueOf(tno);
        }

//	 mLCRReportSchema.setSerialNo(tSerialno);
        mLCRReportSchema.setContNo(mLCPolSchema.getContNo());
//	 mLCRReportSchema.setGrpPolNo(mLCPolSchema.getGrpPolNo());
        mLCRReportSchema.setAppntNo(mLCPolSchema.getAppntNo());
        mLCRReportSchema.setAppntName(mLCPolSchema.getAppntName());
//	 mLCRReportSchema.setInsuredNo(mLCPolSchema.getInsuredNo());
//	 mLCRReportSchema.setInsuredName(mLCPolSchema.getInsuredName());
        mLCRReportSchema.setManageCom(mManageCom);
        mLCRReportSchema.setReplyContente("");
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

        mLCUWMasterSchema.setOperator(mOperater);
        mLCUWMasterSchema.setManageCom(mManageCom);
        mLCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
        mLCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());

        mLCUWMasterSchema.setReportFlag("1");

        return true;
    }

    /**
     * ��ӡ��Ϣ��
     * @return
     */
    private boolean preparePrint()
    {

        //׼����ӡ���������
        String strNoLimit = PubFun.getNoLimit(mGlobalInput.ComCode);
        String tPrtSeq = PubFun1.CreateMaxNo("PRTSEQNO", strNoLimit);
        mLOPRTManagerSchema.setPrtSeq(tPrtSeq);
        mLOPRTManagerSchema.setOtherNo(mPolNo);
        mLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_INDPOL); //������
        mLOPRTManagerSchema.setCode(PrintManagerBL.CODE_PRnewMEET); //����
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
        mLOPRTManagerSchema.setStandbyFlag2(mLCPolSchema.getPrtNo()); //�������˱���
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
            tError.moduleName = "PRnewUWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "�����˱�LAAgent��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLAAgentSet.get(1).getAgentGroup() == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWRReportAfterInitService";
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
            tError.moduleName = "PRnewUWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "������չҵ������LABranchGroup��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLABranchGroupSet.get(1).getBranchAttr() == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWRReportAfterInitService";
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

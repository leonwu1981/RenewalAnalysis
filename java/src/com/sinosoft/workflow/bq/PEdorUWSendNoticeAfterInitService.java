/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.bq;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.LCContSchema;
import com.sinosoft.lis.schema.LOPRTManagerSchema;
import com.sinosoft.lis.schema.LPUWMasterMainSchema;
import com.sinosoft.lis.vschema.LAAgentSet;
import com.sinosoft.lis.vschema.LABranchGroupSet;
import com.sinosoft.lis.vschema.LOPRTManagerSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: </p>
 * <p>Description:�������ڵ�����:��ȫ�˹��˱����ͺ˱�֪ͨ������� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PEdorUWSendNoticeAfterInitService implements AfterInitService
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
    /** ������*/
    private String mEdorNo;
    /** ������*/
    private String mContNo;
    private String mInsuredNo;
//    private String mSpecReason;
    private String mMissionID;

    /**������*/
    private LCContSchema mLCContSchema = new LCContSchema();
    /** ��ȫ�˱����� */
    private LPUWMasterMainSchema mLPUWMasterMainSchema = new LPUWMasterMainSchema();
    /** ��ӡ����� */
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();

    public PEdorUWSendNoticeAfterInitService()
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

        //������֪ͨ���ӡ���������
        map.put(mLOPRTManagerSchema, "INSERT");

        //��ӱ�ȫ�����˱�����֪ͨ���ӡ���������
        map.put(mLPUWMasterMainSchema, "UPDATE");

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
            tError.moduleName = "PEdorUWSendNoticeAfterInitService";
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
            tError.moduleName = "PEdorUWSendNoticeAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mContNo + "�ı����˱�����Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //У�鱣ȫ�����˱�����
        //У�鱣����Ϣ
        LPUWMasterMainDB tLPUWMasterMainDB = new LPUWMasterMainDB();
        tLPUWMasterMainDB.setEdorNo(mEdorNo) ;
        tLPUWMasterMainDB.setContNo(mContNo) ;
        if (!tLPUWMasterMainDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorUWSendNoticeAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mContNo + "��ȫ�����˱�������Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLPUWMasterMainSchema.setSchema(tLPUWMasterMainDB);

//   if (mLPUWMasterMainSchema != null && !mLPUWMasterMainSchema.getSpecFlag().equals("1"))
//	 {
//	   CError tError = new CError();
//	   tError.moduleName = "PEdorUWSendNoticeAfterInitService";
//	   tError.functionName = "checkData";
//	   tError.errorMessage = "�޺˱�֪ͨ������,���˱�֪ͨ��ʧ��!";
//	   this.mErrors .addOneError(tError) ;
//	   return false;
//	 }
//
//   if (mLPUWMasterMainSchema != null && mLPUWMasterMainSchema.getPrintFlag().equals("1"))
//	 {
//	   CError tError = new CError();
//	   tError.moduleName = "PEdorUWSendNoticeAfterInitService";
//	   tError.functionName = "checkData";
//	   tError.errorMessage = "�Ѿ����˱�֪ͨ��,��δ��ӡ,�����ٴη��˱�֪ͨ��!";
//	   this.mErrors .addOneError(tError) ;
//	   return false;
//	 }


        // ����δ��ӡ״̬�ĺ˱�֪ͨ���ڴ�ӡ������ֻ����һ��
        // ������ͬһ���������ͣ�ͬһ���������룬ͬһ��������������
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

        tLOPRTManagerDB.setCode(PrintManagerBL.CODE_PEdorUW); //�˱�֪ͨ��
        tLOPRTManagerDB.setOtherNo(mContNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_CONT); //������
        tLOPRTManagerDB.setStandbyFlag2(mEdorNo);
        tLOPRTManagerDB.setStateFlag("0");

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorUWSendNoticeAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "��ѯ��ӡ�������Ϣ����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLOPRTManagerSet.size() != 0)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorUWSendNoticeAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "�ڴ�ӡ����������һ������δ��ӡ״̬�ĺ˱�֪ͨ��!";
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWSendNoticeAfterInitService";
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
            tError.moduleName = "PEdorUWSendNoticeAfterInitService";
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
            tError.moduleName = "PEdorUWSendNoticeAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWSendNoticeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mEdorNo = (String) mTransferData.getValueByName("EdorNo");
        if (mEdorNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWSendNoticeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������EdorNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mContNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWSendNoticeAfterInitService";
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
            tError.moduleName = "PEdorUWSendNoticeAfterInitService";
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
        if (!prepareSendNotice())
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
     * ׼���˱�֪ͨ��������Ϣ
     * �����������������򷵻�false,���򷵻�true
     */
    private boolean prepareSendNotice()
    {

        ////׼���˱�������Ϣ

        mLPUWMasterMainSchema.setOperator(mOperater);
        mLPUWMasterMainSchema.setManageCom(mManageCom);
        mLPUWMasterMainSchema.setModifyDate(PubFun.getCurrentDate());
        mLPUWMasterMainSchema.setModifyTime(PubFun.getCurrentTime());

        mLPUWMasterMainSchema.setPrintFlag("1"); //���ͺ˱�֪ͨ��

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
        mLOPRTManagerSchema.setOtherNo(mContNo);
        mLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_CONT); //������
        mLOPRTManagerSchema.setCode(PrintManagerBL.CODE_PEdorUW); //�˱�֪ͨ��
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
        mLOPRTManagerSchema.setStandbyFlag3(mMissionID); //��ȫ������
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
        tLAAgentDB.setAgentCode(mLCContSchema.getAgentCode());
        tLAAgentSet = tLAAgentDB.query();
        if (tLAAgentSet == null || tLAAgentSet.size() != 1)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorUWSendNoticeAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "�����˱�LAAgent��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLAAgentSet.get(1).getAgentGroup() == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorUWSendNoticeAfterInitService";
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
            tError.moduleName = "PEdorUWSendNoticeAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "������չҵ������LABranchGroup��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLABranchGroupSet.get(1).getBranchAttr() == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorUWSendNoticeAfterInitService";
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

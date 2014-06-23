/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.xb;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.LCRnewStateLogSet;
import com.sinosoft.lis.vschema.LDUserSet;
import com.sinosoft.lis.vschema.LOPRTManagerSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PRnewUWConfirmAfterInitService implements AfterInitService
{

    /** ȫ������ */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** �����洫�����ݵ����� */
    private VData mInputData;
//    private VData pInputData;
    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
    //**���˼�����ڱ�־*/
//    private String mContType;
    //** ���ݲ����ַ��� */
    private String mManageCom;
    private String mOperate;
//    private String mLJSPayOperateFlag;
    private String mOperator;
    private String mMissionID;
    /** ҵ�����ݲ����ַ��� */
    private String mPrtNo;
    private String mPolNo;
    private String mInsuredNo;
    private String mUWState;
    private String mUWIdea;
    private String mPostponeDay;
    private String mAppGrade; //�ϱ��˱�ʦ����
    private String mAppUser; //�ϱ��˱�ʦ����
    private String mAgentGroup;
    private String mAgentCode;
//    private Reflections mReflections = new Reflections();
    private MMap mMMap = new MMap();
    /**ִ�б�ȫ�˱��������˱�ȷ�ϻ������0000000010*/
    /**������*/
    private LCPolSchema mLCPolSchema = new LCPolSchema();
    /**�������ĺ˱������ݱ�  */
    private LCUWSubSchema mLCUWSubSchema = new LCUWSubSchema();
//    private LCUWSubSet mLCUWSubSet = new LCUWSubSet();
    /**���������˱����� */
    private LCUWMasterSchema mLCUWMasterSchema = new LCUWMasterSchema();
    private LCRnewStateLogSchema mLCRnewStateLogSchema = new LCRnewStateLogSchema();

//    private String mFlag;

    //private  LJSPaySet mLJSPaySet  = new LJSPaySet();
    /** ��ӡ����� */
    private LOPRTManagerSet mLOPRTManagerSet = new LOPRTManagerSet();
    private String CurrentDate = PubFun.getCurrentDate();
//    private String CurrentTime = PubFun.getCurrentTime();

    public PRnewUWConfirmAfterInitService()
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

        //���������ݿ�����������
        mInputData = (VData) cInputData.clone();

        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData(cInputData, cOperate))
        {
            return false;
        }

        //����У�������checkdata)
        if (!checkData())
        {
            return false;
        }

        //����ҵ����
        if (!dealData())
        {
            return false;
        }

        //����׼��������preparedata())
        if (!prepareOutputData())
        {
            return false;
        }

//	if (mOperate.equals("UPDATE||MANUUWENDORSE"))
//	{
//	  PEdorManuUWBLS tPEdorManuUWBLS = new PEdorManuUWBLS();
//	  if (!tPEdorManuUWBLS.submitData(mInputData,mOperate))
//		return false;
//	  if (mContType.equals("I"))
//	  {
//		System.out.println("------mConttype"+mContType);
//		this.checkFinaProduce();
//		if (this.getUWFlag().equals("9"))
//		{
//		  EdorFinaProduce tEdorFinaProduce = new EdorFinaProduce(mLPEdorMainSchema.getEdorNo());
//		  tEdorFinaProduce.setLimit(PubFun.getNoLimit(mLPEdorMainSchema.getManageCom()));
//		  tEdorFinaProduce.setOperator(mGlobalInput.Operator);
//		  if (!tEdorFinaProduce.submitData())
//			return false;
//		  //���ɴ�ӡ����.
//		  VData tVData = new VData();
//		  tVData.add(mGlobalInput);
//		  tVData.add(mLPEdorMainSchema);
//		  PrtEndorsementBL tPrtEndorsementBL = new PrtEndorsementBL(mLPEdorMainSchema.getEdorNo(),mLPEdorMainSchema.getPolNo());
//		  if (!tPrtEndorsementBL.submitData(tVData,""))
//			return false;
//		}
//	  }
//	}
//	System.out.println("---updateDataPEdorManuUWBL---");

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
        if (mMMap != null && mMMap.keySet().size() > 0)
        {
            map = mMMap;
        }

        //��������˱�������Ϣ
        map.put(mLCUWMasterSchema, "UPDATE");

        //���Ͷ��������Ϣ
        map.put(mLCPolSchema, "UPDATE");

        //�����������Ŀ�˱������ݱ���Ϣ
        if (mLCUWSubSchema != null)
        {
            map.put(mLCUWSubSchema, "INSERT");
        }

        //�����������Ŀ�˱������ݱ���Ϣ
        if (mLCRnewStateLogSchema != null && !mLCRnewStateLogSchema.getProposalNo().equals(""))
        {
            map.put(mLCRnewStateLogSchema, "UPDATE");
        }

        //�������������Ϣ
        if (mLOPRTManagerSet != null && mLOPRTManagerSet.size() > 0)
        {
            map.put(mLOPRTManagerSet, "INSERT");
        }

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
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��ò���Ա����
        mOperator = mGlobalInput.Operator;
        if (mOperator == null || mOperator.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWConfirmAfterInitService";
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
            tError.moduleName = "PEdorUWConfirmAfterInitService";
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
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
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

        mPolNo = (String) mTransferData.getValueByName("PolNo");
        if (mPolNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������PolNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //���ҵ��ȫ�˱�ȷ������
        //mContType = (String)cInputData.get(0);
        LCUWMasterSchema tLCUWMasterSchema = new LCUWMasterSchema();
        tLCUWMasterSchema = (LCUWMasterSchema) mTransferData.getValueByName("LCUWMasterSchema");
        if (tLCUWMasterSchema == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨������ҵ�������˱���������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLCUWMasterSchema.getPassFlag() == null
                || tLCUWMasterSchema.getPassFlag().trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨������ҵ�������˱������еĺ˱���������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mUWState = tLCUWMasterSchema.getPassFlag();
        mUWIdea = tLCUWMasterSchema.getUWIdea();
        mPostponeDay = tLCUWMasterSchema.getPostponeDay();
        mAppUser = (String) mTransferData.getValueByName("AppUser");
        if (mUWState.trim().equals("6") && (mAppUser == null || mAppUser.trim().equals("")))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ���������ϱ��˱�ʦ����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (mUWState.trim().equals("2") && (mPostponeDay == null || mPostponeDay.trim().equals("")))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ�������е�������������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;

    }


    /**
     * У�鴫������ݵĺϷ���
     * �����������������򷵻�false,���򷵻�true
     */
    private boolean checkData()
    {

        //У�鱣����Ϣ
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setPolNo(mPolNo);
        if (!tLCPolDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mPolNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCPolSchema.setSchema(tLCPolDB);

        mAgentCode = mLCPolSchema.getAgentCode();
        mAgentGroup = mLCPolSchema.getAgentGroup();
        mPrtNo = mLCPolSchema.getPrtNo();
        //У�鱣�������˱�����Ϣ
        mInsuredNo = mLCPolSchema.getInsuredNo();
        if (mInsuredNo == null)
        {
            CError tError = new CError();
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mPolNo + "�ı����˱�����Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //У�鱣ȫ�����˱�����
        //У�鱣����Ϣ
        LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
        tLCUWMasterDB.setProposalNo(mPolNo);
        if (!tLCUWMasterDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorUWConfirmAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mPolNo + "��ȫ�����˱�������Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCUWMasterSchema.setSchema(tLCUWMasterDB);

        if (mUWState.trim().equals("6"))
        {
            //��ȫ�˱�����Ϊ�ϱ��˱�,����ϱ��˱�ʦ����
            LDUserDB tLDUserDB = new LDUserDB();
            LDUserSet tLDUserSet = new LDUserSet();
            LDUserSchema tLDUserSchema = new LDUserSchema();
            tLDUserDB.setUserCode(mAppUser);
            tLDUserSet = tLDUserDB.query();
            if (tLDUserSet == null || tLDUserSet.size() != 1)
            {
                CError tError = new CError();
                tError.moduleName = "PEdorUWConfirmAfterInitService";
                tError.functionName = "prepareData";
                tError.errorMessage = "�ϱ��˱�ʦ" + mAppGrade + "����Ϣ��ѯʧ��!";
                this.mErrors.addOneError(tError);
                return false;
            }
            tLDUserSchema = tLDUserSet.get(1);
            mAppGrade = tLDUserSchema.getEdorPopedom();
        }

        LCRnewStateLogDB tLCRnewStateLogDB = new LCRnewStateLogDB();
        tLCRnewStateLogDB.setProposalNo(mPolNo);
        LCRnewStateLogSet tLCRnewStateLogSet = tLCRnewStateLogDB.query();
        if (tLCRnewStateLogSet == null || tLCRnewStateLogSet.size() != 1)
        {
            CError tError = new CError();
            tError.moduleName = "PRnewManualDunBL";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mPrtNo + "����״̬����Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCRnewStateLogSchema = tLCRnewStateLogSet.get(1);

        return true;
    }


    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {
        // �˱�������Ϣ
        if (!prepareData())
        {
            return false;
        }
        // �˱�����Ϊ���ͣ�ʱҪ��ӡ����Ӧ��֪ͨ����Ϣ
        if (!preparePrint())
        {
            return false;
        }
        return true;

    }


    /**
     * ׼����Ҫ���������
     */
    private boolean prepareData()
    {

        //׼�������˱�������Ϣ
        if (mUWIdea != null && !mUWIdea.trim().equals(""))
        {
            mLCUWMasterSchema.setUWIdea(mUWIdea);
        }
        if (mUWState.trim().equals("6"))
        {
            mLCUWMasterSchema.setAppGrade(mAppGrade);
        }
        if (mUWState.trim().equals("2"))
        {
            mLCUWMasterSchema.setPostponeDay(mPostponeDay);
        }
        mLCUWMasterSchema.setUWNo(mLCUWMasterSchema.getUWNo() + 1);
        mLCUWMasterSchema.setPassFlag(mUWState);
        mLCUWMasterSchema.setState(mUWState);
        mLCUWMasterSchema.setOperator(mOperator);
        mLCUWMasterSchema.setManageCom(mManageCom);
        mLCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
        mLCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());

        //׼�������˱�����켣��Ϣ
        mLCUWSubSchema = new LCUWSubSchema();
        //mReflections.transFields(mLCUWSubSchema,mLCUWMasterSchema );
        mLCUWSubSchema.setUWNo(mLCUWMasterSchema.getUWNo()); //�ڼ��κ˱�
        mLCUWSubSchema.setProposalNo(mLCUWMasterSchema.getProposalNo());
        mLCUWSubSchema.setPolNo(mLCUWMasterSchema.getPolNo());
//	 mLCUWSubSchema.setUWFlag(mLCUWMasterSchema.getState()); //�˱����
        mLCUWSubSchema.setUWGrade(mLCUWMasterSchema.getUWGrade()); //�˱�����
        mLCUWSubSchema.setAppGrade(mLCUWMasterSchema.getAppGrade()); //���뼶��
        mLCUWSubSchema.setAutoUWFlag(mLCUWMasterSchema.getAutoUWFlag());
        mLCUWSubSchema.setState(mLCUWMasterSchema.getState());
        mLCUWSubSchema.setUWIdea(mLCUWMasterSchema.getUWIdea());
        mLCUWSubSchema.setOperator(mLCUWMasterSchema.getOperator()); //����Ա
        mLCUWSubSchema.setManageCom(mLCUWMasterSchema.getManageCom());
        if (mLCUWMasterSchema.getSpecReason() != null)
        {
            mLCUWSubSchema.setSpecReason(mLCUWMasterSchema.getSpecReason()); //��Լԭ��
        }
        if (mLCUWMasterSchema.getAddPremReason() != null)
        {
            mLCUWSubSchema.setAddPremReason(mLCUWMasterSchema.getAddPremReason());
        }
        if (mLCUWMasterSchema.getChangePolReason() != null)
        {
            mLCUWSubSchema.setChangePolReason(mLCUWMasterSchema.getChangePolReason());
        }
        if (mLCUWMasterSchema.getUpReportContent() != null)
        {
            mLCUWSubSchema.setUpReportContent(mLCUWMasterSchema.getUpReportContent());
        }
        mLCUWSubSchema.setMakeDate(PubFun.getCurrentDate());
        mLCUWSubSchema.setMakeTime(PubFun.getCurrentTime());
        mLCUWSubSchema.setModifyDate(PubFun.getCurrentDate());
        mLCUWSubSchema.setModifyTime(PubFun.getCurrentTime());

        //׼��������Ϣ
        if (mUWState != null && !mUWState.trim().equals(""))
        {
            mLCPolSchema.setUWFlag(mUWState);
            mLCPolSchema.setUWCode(mOperator);
            mLCPolSchema.setUWDate(PubFun.getCurrentDate());
        }

        //׼������״̬��Ϣ
        if (mUWState.trim().equals("9") || mUWState.trim().equals("4"))
        {
            mLCRnewStateLogSchema.setModifyDate(CurrentDate);
            mLCRnewStateLogSchema.setState("3");
        }

        return true;

    }

    /**
     * ��ӡ��Ϣ��
     * @return
     */
    private boolean preparePrint()
    {

        //�ܱ�֪ͨ��
        if (mUWState.trim().equals("1"))
        {
            //֪ͨ���
            String tLimit = PubFun.getNoLimit(mManageCom);
            String mGetNoticeNo = PubFun1.CreateMaxNo("PRTSEQNO", tLimit); //��������֪ͨ���
            LOPRTManagerSchema tLOPRTManagerSchema;
            tLOPRTManagerSchema = new LOPRTManagerSchema();
            tLOPRTManagerSchema.setPrtSeq(mGetNoticeNo);
            tLOPRTManagerSchema.setReqCom(mGlobalInput.ComCode);
            tLOPRTManagerSchema.setReqOperator(mGlobalInput.Operator);
            tLOPRTManagerSchema.setMakeDate(PubFun.getCurrentDate());
            tLOPRTManagerSchema.setMakeTime(PubFun.getCurrentTime());
            tLOPRTManagerSchema.setPrtSeq(mGetNoticeNo);
            tLOPRTManagerSchema.setManageCom(mLCPolSchema.getManageCom());
            tLOPRTManagerSchema.setPrtType(PrintManagerBL.PT_FRONT);
            tLOPRTManagerSchema.setAgentCode(mLCPolSchema.getAgentCode());
            tLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_INDPOL);

            tLOPRTManagerSchema.setCode(PrintManagerBL.CODE_PRnewDECLINE);
            tLOPRTManagerSchema.setOtherNo(mPolNo);
            tLOPRTManagerSchema.setStandbyFlag1(mLCPolSchema.getPrtNo());
            tLOPRTManagerSchema.setStandbyFlag3(mMissionID);
            tLOPRTManagerSchema.setStandbyFlag2(mLCPolSchema.getPrtNo());
            tLOPRTManagerSchema.setStateFlag("0");
            mLOPRTManagerSet.add(tLOPRTManagerSchema);
        }

        //����֪ͨ��
        if (mUWState.trim().equals("2"))
        {
            String tLimit = PubFun.getNoLimit(mManageCom);
            String mGetNoticeNo = PubFun1.CreateMaxNo("PRTSEQNO", tLimit); //��������֪ͨ���
            LOPRTManagerSchema tLOPRTManagerSchema;
            tLOPRTManagerSchema = new LOPRTManagerSchema();
            tLOPRTManagerSchema.setPrtSeq(mGetNoticeNo);
            tLOPRTManagerSchema.setReqCom(mGlobalInput.ComCode);
            tLOPRTManagerSchema.setReqOperator(mGlobalInput.Operator);
            tLOPRTManagerSchema.setMakeDate(PubFun.getCurrentDate());
            tLOPRTManagerSchema.setMakeTime(PubFun.getCurrentTime());
            tLOPRTManagerSchema.setManageCom(mLCPolSchema.getManageCom());
            tLOPRTManagerSchema.setPrtType(PrintManagerBL.PT_FRONT);
            tLOPRTManagerSchema.setAgentCode(mLCPolSchema.getAgentCode());
            tLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_INDPOL);

            tLOPRTManagerSchema.setCode(PrintManagerBL.CODE_PRnewDEFER);
            tLOPRTManagerSchema.setOtherNo(mPolNo);
            tLOPRTManagerSchema.setStandbyFlag1(mLCPolSchema.getPrtNo());
            tLOPRTManagerSchema.setStandbyFlag3(mMissionID);
            tLOPRTManagerSchema.setStandbyFlag2(mLCPolSchema.getPrtNo());
            tLOPRTManagerSchema.setStateFlag("0");
            mLOPRTManagerSet.add(tLOPRTManagerSchema);
        }

        return true;
    }

//    private void checkFinaProduce()
//    {
//        String sql = "select * from LPEdorMain where edorno='" + mLPEdorMainSchema.getEdorNo()
//                + "' and uwstate in ('0','5','6')";
//        LPEdorMainDB tLPEdorMainDB = new LPEdorMainDB();
//        LPEdorMainSet tLPEdorMainSet = new LPEdorMainSet();
//        tLPEdorMainSet = tLPEdorMainDB.executeQuery(sql);
//
//        if (tLPEdorMainSet.size() > 0)
//        {
//            this.setUWFlag("5");
//        }
//        else
//        {
//            this.setUWFlag("9");
//        }
//    }

    public static void main(String[] args)
    {
//        VData tInputData = new VData();
//        GlobalInput tGlobalInput = new GlobalInput();
//        LPEdorMainSchema tLPEdorMainSchema = new LPEdorMainSchema();
//        LPUWMasterSchema tLPUWMasterSchema = new LPUWMasterSchema();
//        tGlobalInput.ManageCom = "001";
//        tGlobalInput.Operator = "Admin";
//        tLPEdorMainSchema.setEdorNo("00000120020420000067");
//        tLPUWMasterSchema.setUWIdea("tjj temp test");
//        tLPUWMasterSchema.setPassFlag("9");
//        tInputData.addElement(tLPEdorMainSchema);
//        tInputData.addElement(tLPUWMasterSchema);
//        tInputData.addElement(tGlobalInput);
    }

}

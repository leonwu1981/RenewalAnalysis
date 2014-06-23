package com.sinosoft.workflow.bq;

import com.sinosoft.lis.db.*;
import java.util.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;
import com.sinosoft.workflowengine.AfterInitService;
import com.sinosoft.lis.vdb.LPContDBSet;

/**
 * <p>Title: �������ڵ�����:��ȫ�����֪ͨ�� </p>
 * <p>Description: �����������֪ͨ��AfterInit������</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: SinoSoft</p>
 * @author Lanjun
 * @version 1.0
 */

public class PEdorAppUWAutoHealthAfterInitService
    implements AfterInitService {
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
  private LPContSchema mLPContSchema = new LPContSchema();
  private String mPrtSeq;

  /** ��ȫ�˱����� */
  private LPCUWMasterSchema mLPCUWMasterSchema = new LPCUWMasterSchema();

  /** ����������� */
  private LPAppPENoticeSchema mLPAppPENoticeSchema = new LPAppPENoticeSchema();

  /** ���������Ŀ�� */
  private LPAppPENoticeItemSet mLPAppPENoticeItemSet = new LPAppPENoticeItemSet();

  /** ��ӡ����� */
  private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();

  public PEdorAppUWAutoHealthAfterInitService() {
  }

  /**
   * �������ݵĹ�������
   * @param: cInputData ���������
   *         cOperate ���ݲ���
   * @return:
   */
  public boolean submitData(VData cInputData, String cOperate) {
    //�õ��ⲿ���������,�����ݱ��ݵ�������
    if (!getInputData(cInputData, cOperate))
      return false;

    //У���Ƿ���δ��ӡ�����֪ͨ��
    if (!checkData())
      return false;

    //����ҵ����
    if (!dealData())
      return false;

    //Ϊ��������һ�ڵ������ֶ�׼������
    if (!prepareTransferData())
      return false;

    //׼������̨������
    if (!prepareOutputData())
      return false;

    System.out.println("Start SysUWNoticeBL Submit...");

    //mResult.clear();
    return true;
  }

  /**
   * У��ҵ������
   * @return
   */
  private boolean checkData() {
    //У�鱣����Ϣ
        LPContDB tLPContDB = new LPContDB();
        tLPContDB.setContNo(mContNo);

        LPContSet tLPContSet = new LPContSet();
        tLPContSet = tLPContDB.query();
        if (tLPContSet.size() < 1 || tLPContSet == null)
        {
            CError tError = new CError();
            tError.moduleName = "UWAutoHealthAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mContNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLPContSchema.setSchema(tLPContSet.get(1));

       //У���Ƿ���δ��ӡ�����֪ͨ��
        LPAppPENoticeDB tLPAppPENoticeDB = new LPAppPENoticeDB();
        tLPAppPENoticeDB.setContNo(mContNo);
        tLPAppPENoticeDB.setCustomerNo(mCustomerNo);

        if (tLPAppPENoticeDB.getInfo())
        {
            if (tLPAppPENoticeDB.getPrintFlag().equals("0"))
            {
                CError tError = new CError();
                tError.moduleName = "UWAutoHealthAfterInitService";
                tError.functionName = "checkData";
                tError.errorMessage = "���֪ͨ�Ѿ�¼��,��δ��ӡ������¼�����������!";
                this.mErrors.addOneError(tError);
                return false;
            }
        }

        //У�鱣ȫ�����˱�����
        //У�鱣����Ϣ
        LPCUWMasterDB tLPCUWMasterDB = new LPCUWMasterDB();
        tLPCUWMasterDB.setContNo(mContNo);
        String sql = "select * from LPCUWMaster where ContNo='" + mContNo
            + "' and edorno in (select edorno from lpedormain where edoracceptno = '"
            +(String) mTransferData.getValueByName("EdorAcceptNo") + "')";
        LPCUWMasterSet tLPCUWMasterSet = new LPCUWMasterSet();
        tLPCUWMasterSet = tLPCUWMasterDB.executeQuery(sql);
        if (tLPCUWMasterSet.size()<1 || tLPCUWMasterSet == null)
        {
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mContNo + "��ȫ�����˱�������Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLPCUWMasterSchema.setSchema(tLPCUWMasterSet.get(1));

    return true;
  }

  /**
   * ����ǰ����������ݣ�����BL�߼�����
   * ����ڴ�������г����򷵻�false,���򷵻�true
   */
  private boolean dealData() {

    String strNoLimit = PubFun.getNoLimit(mGlobalInput.ComCode);

    mPrtSeq = PubFun1.CreateMaxNo("PRTSEQNO", strNoLimit);

    // �����Ϣ
    if (prepareHealth() == false)
      return false;

    //��ӡ����
    if (preparePrint() == false)
      return false;

    return true;
  }

  /**
   * �����������еõ����ж���
   *��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
   */
  private boolean getInputData(VData cInputData, String cOperate) {
    //�����������еõ����ж���
    //���ȫ�ֹ�������
    mGlobalInput.setSchema( (GlobalInput) cInputData.getObjectByObjectName(
        "GlobalInput", 0));
    mTransferData = (TransferData) cInputData.getObjectByObjectName(
        "TransferData", 0);

    if (mGlobalInput == null) {
      // @@������
      //this.mErrors.copyAllErrors( tLPContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "UWAutoHealthAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "ǰ̨����ȫ�ֹ�������ʧ��!";
      this.mErrors.addOneError(tError);
      return false;
    }

    //��ò���Ա����
    mOperater = mGlobalInput.Operator;
    if (mOperater == null || mOperater.trim().equals("")) {
      // @@������
      //this.mErrors.copyAllErrors( tLPContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "UWAutoHealthAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
      this.mErrors.addOneError(tError);
      return false;
    }

    //��õ�½��������
    mManageCom = mGlobalInput.ManageCom;
    if (mManageCom == null || mManageCom.trim().equals("")) {
      // @@������
      //this.mErrors.copyAllErrors( tLPContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "UWAutoHealthAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
      this.mErrors.addOneError(tError);
      return false;
    }

    //���ҵ������
    if (mTransferData == null) {
      // @@������
      //this.mErrors.copyAllErrors( tLPContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "UWAutoHealthAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
      this.mErrors.addOneError(tError);
      return false;
    }

        mContNo = (String) mTransferData.getValueByName("ContNo");
        System.out.println("mContNo="+mContNo);
        if (mContNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLPContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������ContNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

    mCustomerNo = (String) mTransferData.getValueByName("CustomerNo");
    if (mCustomerNo == null) {
      // @@������
      //this.mErrors.copyAllErrors( tLPContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "UWAutoHealthAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "ǰ̨����ҵ��������CustomerNoʧ��!";
      this.mErrors.addOneError(tError);
      return false;
    }

    //���ҵ�����֪ͨ����
    mLPAppPENoticeSchema = (LPAppPENoticeSchema) mTransferData.getValueByName(
        "LPAppPENoticeSchema");
    if (mLPAppPENoticeSchema == null) {
      // @@������
      //this.mErrors.copyAllErrors( tLPContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "UWAutoHealthAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "ǰ̨������ҵ�����֪ͨ����ʧ��!";
      this.mErrors.addOneError(tError);
      return false;
    }
    //���ҵ�����֪ͨ��Ӧ�������Ŀ
    mLPAppPENoticeItemSet = (LPAppPENoticeItemSet) mTransferData.getValueByName(
        "LPAppPENoticeItemSet");
    if (mLPAppPENoticeItemSet == null || mLPAppPENoticeItemSet.size() == 0) {
      // @@������
      //this.mErrors.copyAllErrors( tLPContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "UWAutoHealthAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "ǰ̨������ҵ�����֪ͨ��Ӧ�������Ŀ����ʧ��!";
      this.mErrors.addOneError(tError);
      return false;
    }

    //��õ�ǰ�������������ID
    mMissionID = (String) mTransferData.getValueByName("MissionID");
    if (mMissionID == null) {
      // @@������
      //this.mErrors.copyAllErrors( tLPContDB.mErrors );
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
   */
  private boolean prepareHealth() {

   //ȡ����������
        LAAgentDB tLAAgentDB = new LAAgentDB();
        tLAAgentDB.setAgentCode(mLPContSchema.getAgentCode());
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
    if (!tLDPersonDB.getInfo()) {
      // @@������
      CError tError = new CError();
      tError.moduleName = "UWAutoHealthAfterInitService";
      tError.functionName = "prepareHealth";
      tError.errorMessage = "ȡ�����ͻ�����ʧ��!";
      this.mErrors.addOneError(tError);
      return false;
    }

    mLPAppPENoticeSchema.setGrpContNo(mLPContSchema.getGrpContNo());
    mLPAppPENoticeSchema.setContNo(mLPContSchema.getContNo());
   // mLPAppPENoticeSchema.setProposaLPontNo(mLPContSchema.getProposaLPontNo());
    mLPAppPENoticeSchema.setCustomerNo(mCustomerNo);
    mLPAppPENoticeSchema.setName(tLDPersonDB.getName());
    mLPAppPENoticeSchema.setPrtSeq(mPrtSeq);
    System.out.println("SEQNO=" + mPrtSeq);
    mLPAppPENoticeSchema.setPEDate(mLPAppPENoticeSchema.getPEDate());
    mLPAppPENoticeSchema.setPEAddress(mLPAppPENoticeSchema.getPEAddress());
    mLPAppPENoticeSchema.setPrintFlag("0");
    mLPAppPENoticeSchema.setAppName(mLPContSchema.getAppntName());
    mLPAppPENoticeSchema.setAgentCode(mLPContSchema.getAgentCode());
    mLPAppPENoticeSchema.setAgentName(tLAAgentDB.getName());
    mLPAppPENoticeSchema.setManageCom(mLPContSchema.getManageCom());
    mLPAppPENoticeSchema.setPEBeforeCond(mLPAppPENoticeSchema.getPEBeforeCond());
    mLPAppPENoticeSchema.setOperator(mOperater); //����Ա
    mLPAppPENoticeSchema.setMakeDate(PubFun.getCurrentDate());
    mLPAppPENoticeSchema.setMakeTime(PubFun.getCurrentTime());
    mLPAppPENoticeSchema.setModifyDate(PubFun.getCurrentDate());
    mLPAppPENoticeSchema.setModifyTime(PubFun.getCurrentTime());
    mLPAppPENoticeSchema.setRemark(mLPAppPENoticeSchema.getRemark());

    //׼����������Ŀ��Ϣ
    for (int i = 1; i <= mLPAppPENoticeItemSet.size(); i++) {

      mLPAppPENoticeItemSet.get(i).setGrpContNo(mLPContSchema.getGrpContNo());
      mLPAppPENoticeItemSet.get(i).setContNo(mLPContSchema.getContNo());
      mLPAppPENoticeItemSet.get(i).setEdorAcceptNo( (String) mTransferData.
          getValueByName("EdorAcceptNo"));
      System.out.println( (String) mTransferData.getValueByName("EdorAcceptNo"));
      //  mLPAppPENoticeItemSet.get(i).setProposaLPontNo(mLPContSchema.getProposaLPontNo());
      mLPAppPENoticeItemSet.get(i).setPrtSeq(mPrtSeq);
      mLPAppPENoticeItemSet.get(i).setContNo( mContNo );
      //mLPAppPENoticeItemSet.get(i).setPEItemCode(); //�˱��������
      //mLPAppPENoticeItemSet.get(i).setPEItemName(); //�˱�������Ϣ
      //mLPAppPENoticeItemSet.get(i).setCustomerNo(mCustomerNo);
      mLPAppPENoticeItemSet.get(i).setModifyDate(PubFun.getCurrentDate()); //��ǰֵ
      mLPAppPENoticeItemSet.get(i).setModifyTime(PubFun.getCurrentTime());
      //mLPAppPENoticeItemSet.get(i).setFreePE() ;
    }
    //׼���˱�������Ϣ
    mLPCUWMasterSchema.setOperator(mOperater);
    mLPCUWMasterSchema.setManageCom(mManageCom);
    mLPCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
    mLPCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());

    return true;
  }

  /**
   * ��ӡ��Ϣ��
   * @return
   */
  private boolean preparePrint() {
    // ����δ��ӡ״̬��֪ͨ���ڴ�ӡ������ֻ����һ��
    // ������ͬһ���������ͣ�ͬһ���������룬ͬһ��������������
    LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

    tLOPRTManagerDB.setCode(PrintManagerBL.CODE_PE); //���
    //tLOPRTManagerDB.setOtherNo(mContNo);
    tLOPRTManagerDB.setOtherNo( (String) mTransferData.getValueByName(
        "EdorAcceptNo"));
    tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_CONT); //������
    tLOPRTManagerDB.setStandbyFlag1(mCustomerNo);
    tLOPRTManagerDB.setStateFlag("0");

    LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
    if (tLOPRTManagerSet == null) {
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

    if (tLDSysVarDB.getInfo() == false) {
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
    mLOPRTManagerSchema.setOtherNo( (String) mTransferData.getValueByName(
        "EdorAcceptNo"));
    mLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_EDORACCEPT); //������
    mLOPRTManagerSchema.setCode(PrintManagerBL.CODE_PE); //���
    mLOPRTManagerSchema.setManageCom(mLPContSchema.getManageCom());
    mLOPRTManagerSchema.setAgentCode(mLPContSchema.getAgentCode());
    mLOPRTManagerSchema.setReqCom(mGlobalInput.ComCode);
    mLOPRTManagerSchema.setReqOperator(mGlobalInput.Operator);
    //mLOPRTManagerSchema.setExeCom();
    //mLOPRTManagerSchema.setExeOperator();
    mLOPRTManagerSchema.setPrtType(PrintManagerBL.PT_FRONT); //ǰ̨��ӡ
    mLOPRTManagerSchema.setStateFlag("0");
    mLOPRTManagerSchema.setPatchFlag("0");
    mLOPRTManagerSchema.setMakeDate(PubFun.getCurrentDate());
    mLOPRTManagerSchema.setMakeTime(PubFun.getCurrentTime());
    // mLOPRTManagerSchema.setDoneDate() ;
    // mLOPRTManagerSchema.setDoneTime();
    mLOPRTManagerSchema.setStandbyFlag1(mCustomerNo); //�������˱���
    mLOPRTManagerSchema.setStandbyFlag3(mMissionID);
    mLOPRTManagerSchema.setOldPrtSeq(mPrtSeq);
    mLOPRTManagerSchema.setForMakeDate(tDate);

    return true;
  }

  /**
   * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
   * @return
   */
  private boolean prepareTransferData() {
        LAAgentDB tLAAgentDB = new LAAgentDB();
        LAAgentSet tLAAgentSet = new LAAgentSet();
        tLAAgentDB.setAgentCode(mLPContSchema.getAgentCode());
        System.out.println("mLPContSchema.getAgentCode()"+mLPContSchema.getAgentCode());
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


    mTransferData.setNameAndValue("EdorAcceotNo",mLOPRTManagerSchema.getOtherNo());
    mTransferData.setNameAndValue("PrtSeq", mLOPRTManagerSchema.getPrtSeq());
    mTransferData.setNameAndValue("OldPrtSeq", mLOPRTManagerSchema.getPrtSeq());
    mTransferData.setNameAndValue("AgentCode", mLPContSchema.getAgentCode());
    mTransferData.setNameAndValue("AgentGroup",
                                      tLAAgentSet.get(1).getAgentGroup());
    mTransferData.setNameAndValue("BranchAttr",
                                      tLABranchGroupSet.get(1).getBranchAttr());
    mTransferData.setNameAndValue("ManageCom", mLPContSchema.getManageCom());
    mTransferData.setNameAndValue("ContNo",mLPContSchema.getContNo());
    System.out.println("class��Ϣ��edorAcceptNo="+mTransferData.getValueByName("EdorAcceotNo"));

    return true;
  }

  /**
   * ������׼����̨�ύ����
   * @return boolean
   */
  private boolean prepareOutputData() {
    mResult.clear();
    MMap map = new MMap();

    //������֪ͨ����
    map.put(mLPAppPENoticeSchema, "INSERT");

    //��������Ŀ����
    map.put(mLPAppPENoticeItemSet, "INSERT");

    //������֪ͨ���ӡ���������
    map.put(mLOPRTManagerSchema, "INSERT");

    //��ӱ�ȫ�����˱�����֪ͨ���ӡ���������
     map.put(mLPCUWMasterSchema, "UPDATE");

    mResult.add(map);
    return true;
  }

  /**
   * ���ش����Ľ��
   * @return VData
   */
  public VData getResult() {
    return mResult;
  }

  /**
   * ���ع������е�Lwfieldmap��������ֵ
   * @return TransferData
   */
  public TransferData getReturnTransferData() {
    return mTransferData;
  }

  /**
   * ���ش������
   * @return CErrors
   */
  public CErrors getErrors() {
    return mErrors;
  }

  public static void main(String[] args) {
    //SysUWNoticeBL sysUWNoticeBL1 = new SysUWNoticeBL();
  }
}

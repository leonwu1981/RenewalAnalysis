package com.sinosoft.workflow.ask;

import com.sinosoft.workflowengine.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.utility.*;
import com.sinosoft.lis.cbcheck.*;
import com.sinosoft.lis.pubfun.*;

/**
 * <p>Title: ѯ�۸���֪ͨ�� </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class AskSendTrackAfterInitService implements AfterInitService{

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
    private LCGrpContSet mLCGrpContSet = new LCGrpContSet();
    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();
    private MMap mMap = new MMap();

    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;
    private String mOperate;
    private String mMissionID;
    private String mSubMissionID;

    public AskSendTrackAfterInitService()
    {
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

      System.out.println("Start  dealData...");

      //����ҵ����
      if (!dealData())
        return false;

      System.out.println("dealData successful!");

      //Ϊ��������һ�ڵ������ֶ�׼������
      if (!prepareTransferData())
        return false;

      //׼������̨������
      if (!prepareOutputData())
        return false;

      System.out.println("Start  Submit...");

      return true;
    }

    /**
     * ׼������ǰ̨ͳһ�洢����
     * �����������������򷵻�false,���򷵻�true
     */
    private boolean prepareOutputData() {
      mResult.clear();
      MMap map = new MMap();

      map.add(mMap);

      mResult.add(map);
      return true;
    }

    /**
     * У��ҵ������
     * @return
     */
    private boolean checkData() {
        LCGrpContDB tLCGrpContDB = new LCGrpContDB();
        tLCGrpContDB.setGrpContNo(mLCGrpContSet.get(1).getGrpContNo());
        if(!tLCGrpContDB.getInfo())
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "AskSendTrackAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "��ѯ�������ֱ�ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCGrpContSchema = tLCGrpContDB.getSchema();

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
      mLCGrpContSet = (LCGrpContSet) cInputData.getObjectByObjectName(
          "LCGrpContSet", 0);
      mInputData = cInputData;

      if (mGlobalInput == null) {
        // @@������
        //this.mErrors.copyAllErrors( tLCContDB.mErrors );
        CError tError = new CError();
        tError.moduleName = "AskSendTrackAfterInitService";
        tError.functionName = "getInputData";
        tError.errorMessage = "ǰ̨����ȫ�ֹ�������ʧ��!";
        this.mErrors.addOneError(tError);
        return false;
      }

      //��ò���Ա����
      mOperater = mGlobalInput.Operator;
      if (mOperater == null || mOperater.trim().equals("")) {
        // @@������
        //this.mErrors.copyAllErrors( tLCContDB.mErrors );
        CError tError = new CError();
        tError.moduleName = "AskSendTrackAfterInitService";
        tError.functionName = "getInputData";
        tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
        this.mErrors.addOneError(tError);
        return false;
      }

      mOperate = cOperate;
      //��õ�ǰ�������������ID
      mMissionID = (String) mTransferData.getValueByName("MissionID");
      if (mMissionID == null) {
        // @@������
        //this.mErrors.copyAllErrors( tLCContDB.mErrors );
        CError tError = new CError();
        tError.moduleName = "AskSendTrackAfterInitService";
        tError.functionName = "getInputData";
        tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
        this.mErrors.addOneError(tError);
        return false;
      }

      if(mLCGrpContSet==null)
      {
          // @@������
          //this.mErrors.copyAllErrors( tLCContDB.mErrors );
          CError tError = new CError();
          tError.moduleName = "AskSendTrackAfterInitService";
          tError.functionName = "getInputData";
          tError.errorMessage = "ǰ̨����ҵ��������mLCGrpContSchemaʧ��!";
          this.mErrors.addOneError(tError);
          return false;
      }
      return true;
    }

    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean dealData() {
        AskTrackChkBL tAskTrackChkBL = new AskTrackChkBL();
        boolean tResult = tAskTrackChkBL.submitData(mInputData, "");
        if (tResult)
        {
            mMap = (MMap) tAskTrackChkBL.getResult().getObjectByObjectName("MMap",0);
            mLOPRTManagerSchema = (LOPRTManagerSchema) tAskTrackChkBL.getResult().
                                  getObjectByObjectName("LOPRTManagerSchema", 0);
        }
        else
        {
            this.mErrors.copyAllErrors(tAskTrackChkBL.mErrors);
        }

        return true;
    }

    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return
     */
    private boolean prepareTransferData() {

       mTransferData.setNameAndValue("GrpContNo",mLCGrpContSchema.getGrpContNo());
       mTransferData.setNameAndValue("PrtNo",mLCGrpContSchema.getPrtNo());
       mTransferData.setNameAndValue("AgentCode",mLCGrpContSchema.getAgentCode());
       mTransferData.setNameAndValue("PrtNo",mLCGrpContSchema.getPrtNo());
       mTransferData.setNameAndValue("ManageCom",mLCGrpContSchema.getManageCom());
       mTransferData.setNameAndValue("GrpNo",mLCGrpContSchema.getAppntNo());
       mTransferData.setNameAndValue("GrpName",mLCGrpContSchema.getGrpName());
       mTransferData.setNameAndValue("PrtSeq",mLOPRTManagerSchema.getPrtSeq());

      return true;
    }

    public VData getResult() {
      return mResult;
    }

    public TransferData getReturnTransferData() {
      return mTransferData;
    }

    public CErrors getErrors() {
      return mErrors;
    }

}

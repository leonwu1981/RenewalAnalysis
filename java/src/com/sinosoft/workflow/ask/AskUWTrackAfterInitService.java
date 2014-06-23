package com.sinosoft.workflow.ask;

import com.sinosoft.workflowengine.*;
import java.lang.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.utility.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.cbcheck.*;
import com.sinosoft.utility.VData;
/**
 * <p>Title: ������������ģ�� </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author heyq
 * @version 1.0
 */

public class AskUWTrackAfterInitService implements AfterInitService{

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
  private LCGCUWMasterSchema mLCGCUWMasterSchema = new LCGCUWMasterSchema();
  private MMap mMap = new MMap();

  /** ���ݲ����ַ��� */
  private String mOperater;
  private String mManageCom;
  private String mOperate;
  private String mMissionID;
  private String mSubMissionID;
  private String mGrpContNo;
  private String mUWFlag;

  public AskUWTrackAfterInitService() {
  }

  /**
   * �������ݵĹ�������
   * @param: cInputData ���������
   *         cOperate ���ݲ���
   * @return:
   */
  public boolean submitData(VData cInputData, String cOperate) {

    mInputData = (VData) cInputData.clone();
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
    mInputData = cInputData;
    if (mGlobalInput == null) {
      // @@������
      //this.mErrors.copyAllErrors( tLCContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "AskUWConfirmAfterInitService";
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
      tError.moduleName = "AskUWConfirmAfterInitService";
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
      tError.moduleName = "AskUWConfirmAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
      this.mErrors.addOneError(tError);
      return false;
    }

    //��õ������ͬ��
    mGrpContNo = (String) mTransferData.getValueByName("GrpContNo");
    if (mGrpContNo == null) {
      // @@������
      //this.mErrors.copyAllErrors( tLCContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "AskUWConfirmAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "ǰ̨����ҵ��������GrpContNoʧ��!";
      this.mErrors.addOneError(tError);
      return false;
    }

    mLCGCUWMasterSchema = (LCGCUWMasterSchema)mTransferData.getValueByName("LCGCUWMasterSchema");
    if (mLCGCUWMasterSchema == null) {
      // @@������
      //this.mErrors.copyAllErrors( tLCContDB.mErrors );
      CError tError = new CError();
      tError.moduleName = "AskUWConfirmAfterInitService";
      tError.functionName = "getInputData";
      tError.errorMessage = "ǰ̨����ҵ������LCGCUWMasterSchemaʧ��!";
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


      AskUWTrackConfirmBL tAskUWConfirmBL = new AskUWTrackConfirmBL();
      boolean tResult = tAskUWConfirmBL.submitData(mInputData,"");
      if(tResult)
      {
          mMap = (MMap)tAskUWConfirmBL.getResult().getObjectByObjectName("MMap",0);
      }
      else
      {
          this.mErrors.copyAllErrors(tAskUWConfirmBL.getErrors());
      }

    return true;
  }

  /**
   * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
   * @return
   */
  private boolean prepareTransferData() {

    mTransferData.setNameAndValue("UWFlag",mUWFlag) ;

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

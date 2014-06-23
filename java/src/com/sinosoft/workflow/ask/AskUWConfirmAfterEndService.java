package com.sinosoft.workflow.ask;

import com.sinosoft.workflowengine.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.utility.*;
import com.sinosoft.lis.pubfun.*;

/**
 * <p>Title: ѯ�ۺ˱�����</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class AskUWConfirmAfterEndService implements AfterEndService
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
  private LWMissionSet mLWMissionSet = new LWMissionSet();

  /** ���ݲ����ַ��� */
  private String mOperater;
  private String mManageCom;
  private String mOperate;
  private String mMissionID;
  private String mSubMissionID;

  public AskUWConfirmAfterEndService()
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

    map.put(mLWMissionSet,"DELETE");

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
      tError.moduleName = "AskUWConfirmAfterEndService";
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
      tError.moduleName = "AskUWConfirmAfterEndService";
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
      tError.moduleName = "AskUWConfirmAfterEndService";
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
  private boolean dealData() {

    String tSql = "select * from lwmission where 1=1"
                  +" and missionid = '"+mMissionID+"'"
                  +" and activityid in ('0000006004','0000006007','0000006008','0000006009','0000006010','0000006011','0000006012','0000006013','0000006014','0000006020')"
                  ;
    LWMissionDB tLWMissionDB = new LWMissionDB();
    mLWMissionSet = tLWMissionDB.executeQuery(tSql);

    return true;

  }

  /**
   * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
   * @return
   */
  private boolean prepareTransferData() {

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

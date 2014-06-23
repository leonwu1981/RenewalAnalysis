/*
 * <p>ClassName: OLGWorkTypeBL </p>
 * <p>Description: OLGWorkTypeBL���ļ� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: testcompany </p>
 * @Database:
 * @CreateDate��2005-02-26 15:30:19
 */
package com.sinosoft.task;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.vdb.*;
import com.sinosoft.lis.sys.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.bl.*;
import com.sinosoft.lis.vbl.*;
import com.sinosoft.utility.*;
import com.sinosoft.lis.pubfun.*;

public class OLGWorkTypeBL {
  /** �������࣬ÿ����Ҫ����������ж����ø��� */
  public CErrors mErrors = new CErrors();
  private VData mResult = new VData();

  /** �����洫�����ݵ����� */
  private VData mInputData = new VData();

  private MMap map = new MMap();
  /** ͳһ�������� */
  private String mCurrentDate = PubFun.getCurrentDate();
  /** ͳһ����ʱ�� */
  private String mCurrentTime = PubFun.getCurrentTime();

  /** ȫ������ */
  private GlobalInput mGlobalInput = new GlobalInput();

  /** ���ݲ����ַ��� */
  private String mOperate;

  /** ҵ������ر��� */
  private LGWorkTypeSchema mLGWorkTypeSchema = new LGWorkTypeSchema();

//private LGWorkTypeSet mLGWorkTypeSet=new LGWorkTypeSet();
  public OLGWorkTypeBL() {
  }

  public static void main(String[] args) {
  }

  /**
   * �������ݵĹ�������
   * @param: cInputData ���������
   *         cOperate ���ݲ���
   * @return:
   */
  public boolean submitData(VData cInputData, String cOperate) {
    //���������ݿ�����������
    this.mOperate = cOperate;
    //�õ��ⲿ���������,�����ݱ��ݵ�������
    if (!getInputData(cInputData))
      return false;
    //����ҵ����
    if (!dealData()) {
      // @@������
      CError tError = new CError();
      tError.moduleName = "OLGWorkTypeBL";
      tError.functionName = "submitData";
      tError.errorMessage = "���ݴ���ʧ��OLGWorkTypeBL-->dealData!";
      this.mErrors.addOneError(tError);
      return false;
    }
    //׼������̨������
    if (!prepareOutputData())
      return false;
    if (this.mOperate.equals("QUERY||MAIN")) {
      this.submitquery();
    }
    else {
      PubSubmit tPubSubmit = new PubSubmit();
      if (!tPubSubmit.submitData(mInputData, mOperate)) {
        // @@������
        this.mErrors.copyAllErrors(tPubSubmit.mErrors);
        CError tError = new CError();
        tError.moduleName = "OLGWorkTypeBL";
        tError.functionName = "submitData";
        tError.errorMessage = "�����ύʧ��!";

        this.mErrors.addOneError(tError);
        return false;
      }
    }
    mInputData = null;
    return true;
  }

  /**
   * ����ǰ����������ݣ�����BL�߼�����
   * ����ڴ�������г����򷵻�false,���򷵻�true
   */
  private boolean dealData() {
    if (mOperate.equals("INSERT||MAIN")) {
      mLGWorkTypeSchema.setIsDeal("0"); //delete
      mLGWorkTypeSchema.setDealPath("0"); //delete
      mLGWorkTypeSchema.setMakeDate(mCurrentDate);
      mLGWorkTypeSchema.setMakeTime(mCurrentTime);
      mLGWorkTypeSchema.setModifyDate(mCurrentDate);
      mLGWorkTypeSchema.setModifyTime(mCurrentTime);
      mLGWorkTypeSchema.setOperator(mGlobalInput.Operator);
      map.put(mLGWorkTypeSchema, "INSERT"); //����
    }
    if (mOperate.equals("UPDATE||MAIN")) {
      System.out.println("update");
      String sql = "Update LGWorkType set " +
          "WorkTypeName = '" + mLGWorkTypeSchema.getWorkTypeName() + "', " +
          "SuperTypeNo = '" + mLGWorkTypeSchema.getSuperTypeNo() + "', " +
          "Operator = '" + mGlobalInput.Operator + "', " +
          "ModifyDate = '" + mCurrentDate + "', " +
          "ModifyTime = '" + mCurrentTime + "' " +
          "Where  WorkTypeNo = '" + mLGWorkTypeSchema.getWorkTypeNo() + "' ";
      map.put(sql, "UPDATE"); //�޸�
    }
    if (this.mOperate.equals("DELETE||MAIN")) {
      map.put(mLGWorkTypeSchema, "DELETE"); //ɾ��
    }

    return true;
  }

  /**
   * ����ǰ����������ݣ�����BL�߼�����
   * ����ڴ�������г����򷵻�false,���򷵻�true
   */
  private boolean updateData() {
    return true;
  }

  /**
   * ����ǰ����������ݣ�����BL�߼�����
   * ����ڴ�������г����򷵻�false,���򷵻�true
   */
  private boolean deleteData() {
    return true;
  }

  /**
   * �����������еõ����ж���
   *��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
   */
  private boolean getInputData(VData cInputData) {
    this.mLGWorkTypeSchema.setSchema( (LGWorkTypeSchema) cInputData.
                                     getObjectByObjectName("LGWorkTypeSchema",
        0));
    this.mGlobalInput.setSchema((GlobalInput)cInputData.getObjectByObjectName("GlobalInput",0));
    return true;
  }

  /**
   * ׼��������������Ҫ������
   * ��������׼������ʱ���������򷵻�false,���򷵻�true
   */
  private boolean submitquery() {
    this.mResult.clear();
    LGWorkTypeDB tLGWorkTypeDB = new LGWorkTypeDB();
    tLGWorkTypeDB.setSchema(this.mLGWorkTypeSchema);
    //�������Ҫ����Ĵ����򷵻�
    if (tLGWorkTypeDB.mErrors.needDealError()) {
      // @@������
      this.mErrors.copyAllErrors(tLGWorkTypeDB.mErrors);
      CError tError = new CError();
      tError.moduleName = "LGWorkTypeBL";
      tError.functionName = "submitData";
      tError.errorMessage = "�����ύʧ��!";
      this.mErrors.addOneError(tError);
      return false;
    }
    mInputData = null;
    return true;
  }

  private boolean prepareOutputData() {
    try {
      this.mInputData.clear();
      this.mInputData.add(this.mLGWorkTypeSchema);
      mInputData.add(this.map);
      mResult.clear();
      mResult.add(this.mLGWorkTypeSchema);
    }
    catch (Exception ex) {
      // @@������
      CError tError = new CError();
      tError.moduleName = "LGWorkTypeBL";
      tError.functionName = "prepareData";
      tError.errorMessage = "��׼������㴦������Ҫ������ʱ����";
      this.mErrors.addOneError(tError);
      return false;
    }
    return true;
  }

  public VData getResult() {
    return this.mResult;
  }
}

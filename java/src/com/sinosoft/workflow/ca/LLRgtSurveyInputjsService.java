/**
 * Copyright (c) 2005 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.ca;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.BeforeInitService;
import com.sinosoft.lis.llcase.RgtSurveyUI;
import com.sinosoft.lis.llcase.RgtSurveyBL;
import com.sinosoft.lis.llcase.LLCaseReturnUI;
import com.sinosoft.lis.schema.LLCaseSchema;
import com.sinosoft.lis.schema.LLSurveySchema;


/**
 * <p>Title: �������ڵ�����:���ⰸ�����˹����������� </p>
 * <p>Description: ���ⰸ�����˹�����AfterInit������</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: SinoSoft</p>
 * @author liruofei
 * @version 1.0
 */
public class LLRgtSurveyInputjsService implements BeforeInitService
{
    /*�������࣬ÿ����Ҫ����������ж����ø���*/
    public CErrors mErrors  = new CErrors();

    /*�����洫�����ݵ�����*/
    private VData mResult = new VData();

    /*�������������д������ݵ�����*/
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();

    /*���ݲ����ַ���*/
    private String mOperater;
    private String mManageCom;

    /*ҵ�����ݲ����ַ���*/
    private String mMissionID;
    private String tSubMissionID;

    public String SurveyNo = "";

    /**/
    public LLRgtSurveyInputjsService()
    {}
    /**
     * �������ݵĹ�������
     * @param cInputData VData ���������
     * @param cOperate String ���ݲ���
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //�õ��ⲿ��������ݣ������ݱ��ݵ�������
        if (!getInputData(cInputData, cOperate))

        {
            return false;
        }

        //����ҵ����
        if (!dealData(cInputData))
        {
            return false;
        }

        //׼������̨������
        if (!prepareOutputData())
        {
            return false;
        }

        return true;
    }


    /**
     * ׼������ǰ̨ͳһ�洢����
     * �����������������򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean prepareOutputData()
    {
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
       //��ȡȫ�ֹ�������
       mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
       mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);
       System.out.println("SurveyNo==="+mTransferData.getValueByName("SurveyNo"));
    if(mTransferData==null){
        CError.buildErr(mErrors, "ǰ̨����ȫ�ֹ�������ʧ��!");
           return false;
    }
       if (mGlobalInput == null)
       {
           //������
           CError.buildErr(mErrors, "ǰ̨����ȫ�ֹ�������ʧ��!");
           return false;
       }

       //��ò���Ա����
       mOperater = mGlobalInput.Operator;
       if (mOperater == null || mOperater.trim().equals(""))
      {

           //������
           CError.buildErr(mErrors, "ǰ̨����ȫ�ֹ�������Operaterʧ��!");
           return false;
      }

      //��õ�½��������
      mManageCom = mGlobalInput.ManageCom;
      if (mManageCom == null || mManageCom.trim().equals(""))
      {
           //������
           CError.buildErr(mErrors, "ǰ̨����ȫ�ֹ�������ManageComʧ��!");
           return false;
      }

      //��ȡ��ǰ��������ID
      mMissionID = (String) mTransferData.getValueByName("MissionID");
      if (mMissionID == null)
      {
          //������
          CError.buildErr(mErrors, "ǰ̨����ҵ��������MissionIDʧ��!");
          return false;
      }

      return true;
  }

  /**
    * ����ǰ����������ݣ�����BL�߼�����
    * ����ڴ�������г����򷵻�false,���򷵻�true
    * @return boolean
    */
   private boolean dealData(VData cInputData)
   {
       VData tVData = new VData();
       RgtSurveyUI tRgtSurveyUI   = new RgtSurveyUI();
       LLCaseReturnUI tLLCaseReturnUI   = new LLCaseReturnUI();//C
       String transact = (String)mTransferData.getValueByName("transact");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        System.out.println("transact ======== "+transact);
        String SurveyReturn = "";
        SurveyReturn = (String)mTransferData.getValueByName("SurveyReturn");
        System.out.println("SurveyReturn ======== "+SurveyReturn);
        if(SurveyReturn!=null && SurveyReturn.equals("1"))
        {
            if (tRgtSurveyUI.submitData(cInputData, transact)) {
                tVData = tRgtSurveyUI.getResult();
                LLSurveySchema mLLSurveySchema = new LLSurveySchema();
                mLLSurveySchema.setSchema((LLSurveySchema) tVData.
                                          getObjectByObjectName(
                        "LLSurveySchema", 0));
                System.out.println("SurveyNo2===" + mLLSurveySchema.getSurveyNo());
                SurveyNo = mLLSurveySchema.getSurveyNo();
                mResult.clear();
                mResult.add(tVData);
            } else {
                this.mErrors.copyAllErrors(tRgtSurveyUI.mErrors);
                return false;
            }


        }else {
            String CAFlag = (String) mTransferData.getValueByName("CAFlag");
            System.out.println("CAFlag ======== " + CAFlag);
            if (transact != null && !transact.equals("")) {
                if (tLLCaseReturnUI.submitData(cInputData, transact)) {

                    tVData = tLLCaseReturnUI.getResult();
                    mResult.add(tVData);
                } else {
                    this.mErrors.copyAllErrors(tLLCaseReturnUI.mErrors);
                    return false;
                }
            }


        }
       return true;
   }

   /**
       * ���ش����Ľ��
       * @return VData
     */

  /*����
  RgtSurveyBL tRgtSurveyBL = new RgtSurveyBL();
  VData mVData = tRgtSurveyBL.getResult();
  LLSurveySchema mLLSurveySchema = new LLSurveySchema();
  mLLSurveySchema.setSchema(( LLSurveySchema )mVData.getObjectByObjectName( "LLSurveySchema", 0 ));
  System.out.println("SurveyNo2==="+mLLSurveySchema.getSurveyNo());
   /*���Խ���*/
  public VData getResult()
  {
      return this.mResult;

  }

public String getSurveyNo()
 {
    return this.SurveyNo;
 }
  /**
     * ���ع������е�Lwfieldmap��������ֵ
     * @return TransferData
     */

  public TransferData getReturnTransferData()
  {
      return this.mTransferData;
  }

  /**
     * ���ش������
     * @return CErrors
     */
  public CErrors getErrors()
  {
      return this.mErrors;
  }
}

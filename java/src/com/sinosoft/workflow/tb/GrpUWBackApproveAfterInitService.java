/**
 * Copyright (c) 2005 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.*;

/**
 * <p>Title: �������ŵ����ظ��˷����� </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author chenhq
 * @version 1.0
 */

public class GrpUWBackApproveAfterInitService implements AfterInitService
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** �����洫�����ݵ����� */
    private VData mResult = new VData();

    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();

    private TransferData mTransferData = new TransferData();
    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();

    /** ���ݲ����ַ��� */
    private String mOperator;

    /** ҵ�����ݲ����ַ��� */
    private String mGrpContNo;
    private String mMissionID;

    /** �޸ĸ����޸ĵı� */
    private String mPolSql;
    private String mContSql;
    private String mGrpPolSql;
    private String mGrpContSql;
    private String mGCUWMasterSql;
    private String mGUWMasterSql;
    private String mGCUWErrorSql;
    private String mGUWErrorSql;
    private String mGCUWSubSql;
    private String mGUWSubSql;
    private String mCUWMasterSql;
    private String mUWMasterSql;
    private String mCUWErrorSql;
    private String mUWErrorSql;
    private String mCUWSubSql;
    private String mUWSubSql;
    private String mDelWorkFlowSql;

    private LCGCUWErrorTraceSet mLCGCUWErrorTraceSet = new LCGCUWErrorTraceSet();
    private LCGUWErrorTraceSet mLCGUWErrorTraceSet = new LCGUWErrorTraceSet();
    private LCCUWErrorTraceSet mLCCUWErrorTraceSet = new LCCUWErrorTraceSet();
    private LCUWErrorTraceSet mLCUWErrorTraceSet = new LCUWErrorTraceSet();
    
    public GrpUWBackApproveAfterInitService()
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

        //У������
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

        map.put(mPolSql, "UPDATE");
        map.put(mContSql, "UPDATE");
        map.put(mGrpPolSql, "UPDATE");
        map.put(mGrpContSql, "UPDATE");
        map.put(mGCUWMasterSql, "DELETE");
        map.put(mGUWMasterSql, "DELETE");
        map.put(mGCUWErrorSql, "DELETE");
        map.put(mGUWErrorSql, "DELETE");
        map.put(mGCUWSubSql, "DELETE");
        map.put(mGUWSubSql, "DELETE");
        map.put(mCUWMasterSql, "DELETE");
        map.put(mUWMasterSql, "DELETE");
        map.put(mCUWErrorSql, "DELETE");
        map.put(mUWErrorSql, "DELETE");
        map.put(mCUWSubSql, "DELETE");
        map.put(mUWSubSql, "DELETE");
        map.put(mDelWorkFlowSql, "DELETE");
        map.put(mLCGCUWErrorTraceSet, "DELETE&INSERT");
        map.put(mLCGUWErrorTraceSet, "DELETE&INSERT");
        map.put(mLCCUWErrorTraceSet, "DELETE&INSERT");
        map.put(mLCUWErrorTraceSet, "DELETE&INSERT");

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
        LCGrpContDB tLCGrpContDB = new LCGrpContDB();
        tLCGrpContDB.setGrpContNo(mGrpContNo);
        if (!tLCGrpContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mGrpContNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCGrpContSchema.setSchema(tLCGrpContDB);

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
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);

        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ�������������ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ���������GrpContNo
        mGrpContNo = (String) mTransferData.getValueByName("GrpContNo");
        if (mGrpContNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������GrpContNoʧ��!";
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
      String tCurrentDate = PubFun.getCurrentDate();
      String tCurrentTime = PubFun.getCurrentTime();

//�޸����ֱ����� mPolSql
      StringBuffer tSBql = new StringBuffer(128);
      tSBql.append("update LCPol set ApproveCode = '',");
      tSBql.append("ApproveDate = '',");
      tSBql.append("ApproveTime = '',");
      tSBql.append("ApproveFlag = '0',");
      tSBql.append("uwcode = '',");
      tSBql.append("uwflag = '0',");
      tSBql.append("uwdate = '',");
      tSBql.append("uwtime = '',");
      tSBql.append("ModifyDate = '");
      tSBql.append(tCurrentDate);
      tSBql.append("',ModifyTime = '");
      tSBql.append(tCurrentTime);
      tSBql.append("' where GrpContNo = '");
      tSBql.append(mGrpContNo);
      tSBql.append("'");
      mPolSql = tSBql.toString();

//�޸ĺ�ͬ�� mContSql
      tSBql = new StringBuffer(128);
      tSBql.append("update LCCont set ApproveCode = '',");
      tSBql.append("ApproveDate = '',");
      tSBql.append("ApproveTime = '',");
      tSBql.append("ApproveFlag = '0',");
      tSBql.append("uwoperator = '',");
      tSBql.append("uwflag = '0',");
      tSBql.append("uwdate = '',");
      tSBql.append("uwtime = '',");
      tSBql.append("ModifyDate = '");
      tSBql.append(tCurrentDate);
      tSBql.append("',ModifyTime = '");
      tSBql.append(tCurrentTime);
      tSBql.append("' where GrpContNo = '");
      tSBql.append(mGrpContNo);
      tSBql.append("'");
      mContSql = tSBql.toString();

//�޸ļ������ֱ� mGrpPolSql
      tSBql = new StringBuffer(128);
      tSBql.append("update LCGrpPol set ApproveCode = '',");
      tSBql.append("ApproveDate = '',");
      tSBql.append("ApproveTime = '',");
      tSBql.append("ApproveFlag = '0',");
      tSBql.append("uwoperator = '',");
      tSBql.append("uwflag = '0',");
      tSBql.append("uwdate = '',");
      tSBql.append("uwtime = '',");
      tSBql.append("ModifyDate = '");
      tSBql.append(tCurrentDate);
      tSBql.append("',ModifyTime = '");
      tSBql.append(tCurrentTime);
      tSBql.append("' where GrpContNo = '");
      tSBql.append(mGrpContNo);
      tSBql.append("'");
      mGrpPolSql = tSBql.toString();

//�޸ļ����ͬ�� mGrpContSql
      tSBql = new StringBuffer(128);
      tSBql.append("update LCGrpCont set ApproveCode = '',");
      tSBql.append("ApproveDate = '',");
      tSBql.append("ApproveTime = '',");
      tSBql.append("ApproveFlag = '0',");
      tSBql.append("uwoperator = '',");
      tSBql.append("uwflag = '0',");
      tSBql.append("uwdate = '',");
      tSBql.append("uwtime = '',");
      tSBql.append("ModifyDate = '");
      tSBql.append(tCurrentDate);
      tSBql.append("',ModifyTime = '");
      tSBql.append(tCurrentTime);
      tSBql.append("' where GrpContNo = '");
      tSBql.append(mGrpContNo);
      tSBql.append("'");
      mGrpContSql = tSBql.toString();

//����˱��������� mGCUWMasterSql
      tSBql = new StringBuffer(128);
      tSBql.append("delete LCGCUWMaster where GrpContNo='");
      tSBql.append(mGrpContNo);
      tSBql.append("'");
      mGCUWMasterSql = tSBql.toString();

//����˱��������� mGUWMasterSql
      tSBql = new StringBuffer(128);
      tSBql.append("delete LCGUWMaster where GrpContNo='");
      tSBql.append(mGrpContNo);
      tSBql.append("'");
      mGUWMasterSql = tSBql.toString();

//����˱������ mGCUWErrorSql
      tSBql = new StringBuffer(128);
      tSBql.append("delete LCGCUWError where GrpContNo='");
      tSBql.append(mGrpContNo);
      tSBql.append("'");
      mGCUWErrorSql = tSBql.toString();

//����˱������ mGUWErrorSql
      tSBql = new StringBuffer(128);
      tSBql.append("delete LCGUWError where GrpContNo='");
      tSBql.append(mGrpContNo);
      tSBql.append("'");
      mGUWErrorSql = tSBql.toString();

//����˱��켣�� mGCUWSubSql
      tSBql = new StringBuffer(128);
      tSBql.append("delete LCGCUWSub where GrpContNo='");
      tSBql.append(mGrpContNo);
      tSBql.append("'");
      mGCUWSubSql = tSBql.toString();

//����˱��켣�� mGUWSubSql
      tSBql = new StringBuffer(128);
      tSBql.append("delete LCGUWSub where GrpContNo='");
      tSBql.append(mGrpContNo);
      tSBql.append("'");
      mGUWSubSql = tSBql.toString();

//�˱��������� mCUWMasterSql
            tSBql = new StringBuffer(128);
            tSBql.append("delete LCCUWMaster where GrpContNo='");
            tSBql.append(mGrpContNo);
            tSBql.append("'");
            mCUWMasterSql = tSBql.toString();

//���ֺ˱��������� mUWMasterSql
            tSBql = new StringBuffer(128);
            tSBql.append("delete LCUWMaster where GrpContNo='");
            tSBql.append(mGrpContNo);
            tSBql.append("'");
            mUWMasterSql = tSBql.toString();

//�˱������ mCUWErrorSql
            tSBql = new StringBuffer(128);
            tSBql.append("delete LCCUWError where GrpContNo='");
            tSBql.append(mGrpContNo);
            tSBql.append("'");
            mCUWErrorSql = tSBql.toString();

//���ֺ˱������ mUWErrorSql
            tSBql = new StringBuffer(128);
            tSBql.append("delete LCUWError where GrpContNo='");
            tSBql.append(mGrpContNo);
            tSBql.append("'");
            mUWErrorSql = tSBql.toString();

//�˱��켣�� mCUWSubSql
            tSBql = new StringBuffer(128);
            tSBql.append("delete LCCUWSub where GrpContNo='");
            tSBql.append(mGrpContNo);
            tSBql.append("'");
            mCUWSubSql = tSBql.toString();

//���ֺ˱��켣�� mUWSubSql
            tSBql = new StringBuffer(128);
            tSBql.append("delete LCUWSub where GrpContNo='");
            tSBql.append(mGrpContNo);
            tSBql.append("'");
            mUWSubSql = tSBql.toString();

//ɾ���˹��˱��Ľڵ�      mDelWorkFlowSql
      tSBql = new StringBuffer(128);
      tSBql.append("delete lwmission where activityid='0000002004' and missionid='");
      tSBql.append(mMissionID);
      tSBql.append("'");
      mDelWorkFlowSql = tSBql.toString();
      
      //ASR20105425-���ѳ���ϵͳ�޸ģ��˱����֣�  �洢֮ǰ�¹�ͨ���˱�����(uwpassflagΪ4��9��B)����Ϣ add by frost
      String tSql1 = "select * from lcgcuwerror where grpcontno='"+ mGrpContNo +"' and uwpassflag in ('4','9','B')";
      String tSql2 = "select * from lcguwerror where grpcontno='"+ mGrpContNo +"' and uwpassflag in ('4','9','B')";
      String tSql3 = "select * from Lccuwerror where grpcontno='"+ mGrpContNo +"' and uwpassflag in ('4','9','B')";
      String tSql4 = "select * from Lcuwerror where grpcontno='"+ mGrpContNo +"' and uwpassflag in ('4','9','B')";
      
      String tMaxUwnoSql =  "select nvl(max(uwno),0)+1 from uwerrortrace_view where grpcontno='"+ mGrpContNo +"'";
      ExeSQL tExeSQL = new ExeSQL();
      String tMaxUwno = tExeSQL.getOneValue(tMaxUwnoSql);
      
      LCGCUWErrorDB tLCGCUWErrorDB = new LCGCUWErrorDB();
      LCGUWErrorDB tLCGUWErroreDB = new LCGUWErrorDB();
      LCCUWErrorDB tLCCUWErrorDB = new LCCUWErrorDB();
      LCUWErrorDB tLCUWErrorDB = new LCUWErrorDB();

      LCGCUWErrorSet tLCGCUWErrorSet = tLCGCUWErrorDB.executeQuery(tSql1);
      LCGUWErrorSet tLCGUWErroreSet = tLCGUWErroreDB.executeQuery(tSql2);
      LCCUWErrorSet tLCCUWErrorSet = tLCCUWErrorDB.executeQuery(tSql3);
      LCUWErrorSet tLCUWErrorSet = tLCUWErrorDB.executeQuery(tSql4);
      
      Reflections tReflections = new Reflections();
      for (int i = 1; i <= tLCGCUWErrorSet.size(); i++)
      {
    	  LCGCUWErrorTraceSchema aLCGCUWErrorTraceSchema = new LCGCUWErrorTraceSchema();
          LCGCUWErrorSchema aLCGCUWErrorSchema = tLCGCUWErrorSet.get(i);          
          tReflections.transFields(aLCGCUWErrorTraceSchema, aLCGCUWErrorSchema);
          aLCGCUWErrorTraceSchema.setUWNo(tMaxUwno);
          mLCGCUWErrorTraceSet.add(aLCGCUWErrorTraceSchema);
      }
      
      for (int i = 1; i <= tLCGUWErroreSet.size(); i++)
      {
    	  LCGUWErrorTraceSchema aLCGUWErrorTraceSchema = new LCGUWErrorTraceSchema();
    	  LCGUWErrorSchema aLCGUWErrorSchema = tLCGUWErroreSet.get(i);          
          tReflections.transFields(aLCGUWErrorTraceSchema, aLCGUWErrorSchema);
          aLCGUWErrorTraceSchema.setUWNo(tMaxUwno);
          mLCGUWErrorTraceSet.add(aLCGUWErrorTraceSchema);
      }
      
      for (int i = 1; i <= tLCCUWErrorSet.size(); i++)
      {
    	  LCCUWErrorTraceSchema aLCCUWErrorTraceSchema = new LCCUWErrorTraceSchema();
          LCCUWErrorSchema aLCCUWErrorSchema = tLCCUWErrorSet.get(i);          
          tReflections.transFields(aLCCUWErrorTraceSchema, aLCCUWErrorSchema);
          aLCCUWErrorTraceSchema.setUWNo(tMaxUwno);
          mLCCUWErrorTraceSet.add(aLCCUWErrorTraceSchema);
      }
      
      for (int i = 1; i <= tLCUWErrorSet.size(); i++)
      {
    	  LCUWErrorTraceSchema aLCUWErrorTraceSchema = new LCUWErrorTraceSchema();
          LCUWErrorSchema aLCUWErrorSchema = tLCUWErrorSet.get(i);          
          tReflections.transFields(aLCUWErrorTraceSchema, aLCUWErrorSchema);
          aLCUWErrorTraceSchema.setUWNo(tMaxUwno);
          mLCUWErrorTraceSet.add(aLCUWErrorTraceSchema);
      }

      return true;
    }

    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return
     */
    private boolean prepareTransferData()
    {
        mTransferData.setNameAndValue("ProposalGrpContNo", mGrpContNo);
        mTransferData.setNameAndValue("PrtNo", mLCGrpContSchema.getPrtNo());
        mTransferData.setNameAndValue("SaleChnl", mLCGrpContSchema.getSaleChnl());
        mTransferData.setNameAndValue("ManageCom",mLCGrpContSchema.getManageCom());
        mTransferData.setNameAndValue("AgentCode", mLCGrpContSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup", mLCGrpContSchema.getAgentGroup());
        mTransferData.setNameAndValue("GrpName", mLCGrpContSchema.getGrpName());
        mTransferData.setNameAndValue("CValiDate",mLCGrpContSchema.getCValiDate());

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
}

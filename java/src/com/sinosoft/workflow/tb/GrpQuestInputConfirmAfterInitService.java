/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LCGrpContDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.schema.LCGrpContSchema;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;


/**
 * <p>Title: ������������ģ�� </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author heyq
 * @version 1.0
 */

public class GrpQuestInputConfirmAfterInitService implements AfterInitService
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


    /** ���ݲ����ַ��� */
    private String mOperater;
//    private String mManageCom;
    private String mOperate;
    private String mMissionID;
    private String mSubMissionID;
    private String mGrpContNo;
    private String mLCGrpPolSql = "";
    private String mLCContSql = "";
    private String mLCPolSql = "";
    private String mLCGCUWMasterSql = "";
    private String mLCGUWMasterSql = "";
    private String mLCCUWMasterSql = "";
    private String mLCUWMasterSql = "";
    private String mLCGCUWSubSql = "";
    private String mLCGUWSubSql = "";
    private String mLCCUWSubSql = "";
    private String mLCUWSubSql = "";
    private String mLCGCUWErrorSql = "";
    private String mLCGUWErrorSql = "";
    private String mLCCUWErrorSql = "";
    private String mLCUWErrorSql = "";


    /** ҵ������� */
    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();

    public GrpQuestInputConfirmAfterInitService()
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

        System.out.println("Start  dealData...");

        //����ҵ����
        if (!dealData())
        {
            return false;
        }

        System.out.println("dealData successful!");

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

        map.put(mLCGrpContSchema, "UPDATE");
        map.put(mLCGrpPolSql, "UPDATE");
        map.put(mLCContSql, "UPDATE");
        map.put(mLCPolSql, "UPDATE");
        map.put(mLCGCUWMasterSql, "DELETE");
        map.put(mLCGUWMasterSql, "DELETE");
        map.put(mLCCUWMasterSql, "DELETE");
        map.put(mLCUWMasterSql, "DELETE");
        map.put(mLCGCUWSubSql, "DELETE");
        map.put(mLCGUWSubSql, "DELETE");
        map.put(mLCCUWSubSql, "DELETE");
        map.put(mLCUWSubSql, "DELETE");
        map.put(mLCGCUWErrorSql, "DELETE");
        map.put(mLCGUWErrorSql, "DELETE");
        map.put(mLCCUWErrorSql, "DELETE");
        map.put(mLCUWErrorSql, "DELETE");

        mResult.add(map);
        return true;
    }


    /**
     * У��ҵ������
     * @return
     */
    private boolean checkData()
    {
        LCGrpContDB tLCGrpContDB = new LCGrpContDB();
        tLCGrpContDB.setGrpContNo(mGrpContNo);
        if (!tLCGrpContDB.getInfo())
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpQuestInputConfirmAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "��ѯ���屣��ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCGrpContSchema = tLCGrpContDB.getSchema();

        ExeSQL tExeSQL = new ExeSQL();
        String tSql = "select count(1) from lcgrpissuepol where ProposalGrpContNo = '" + mGrpContNo
                + "' and BackObjType = '1' and  ReplyMan is null ";
        String rs = tExeSQL.getOneValue(tSql);
        if (Integer.parseInt(rs) <= 0)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpQuestInputConfirmAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "δ¼�������!";
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
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpQuestInputConfirmAfterInitService";
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
            tError.moduleName = "GrpQuestInputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mOperate = cOperate;
        //��õ�ǰ�������������ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpQuestInputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ�������������ID
        mSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
        if (mSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpQuestInputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ���������GrpContNo
        mGrpContNo = (String) mTransferData.getValueByName("GrpContNo");
        if (mGrpContNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpQuestInputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������GrpContNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
      /**
      * ����ǰ����������ݣ�����BL�߼�����
      * ����ڴ�������г����򷵻�false,���򷵻�true
      */
     private boolean dealData()
     {
         //��״̬��Ϊδ�˱�״̬

         //׼��lcgrpcont������
         mLCGrpContSchema.setUWFlag("");
         mLCGrpContSchema.setUWDate("");
         mLCGrpContSchema.setUWOperator("");
         mLCGrpContSchema.setUWTime("");

         //׼��lcgrppol������
         mLCGrpPolSql =
                 "update lcgrppol set uwflag='',UWOperator='',UWDate=null,UWTime=null where GrpContNo = '"
                 + mGrpContNo + "'";

         //׼��lccont������
         mLCContSql =
                 "update lccont set uwflag='',UWOperator='',UWDate=null,UWTime=null where GrpContNo = '"
                 + mGrpContNo + "'";

         //׼��lcpol������
         mLCPolSql =
                 "update lcpol set uwflag='',uwcode='',UWDate=null,UWTime=null where GrpContNo = '"
                 + mGrpContNo + "'";

         //׼��LCGCUWMaster����
         mLCGCUWMasterSql = "delete from LCGCUWMaster where ProposalGrpContNo = '" + mGrpContNo
                 + "'";

         //׼��LCGUWMaster����
         mLCGUWMasterSql = "delete from LCGUWMaster where GrpContNo = '" + mGrpContNo + "'";

         //׼��lccuwmaster����
         mLCCUWMasterSql = "delete from lccuwmaster where GrpContNo = '" + mGrpContNo + "'";

         //׼��lcuwmaster����
         mLCUWMasterSql = "delete from lcuwmaster where GrpContNo = '" + mGrpContNo + "'";

         //׼��LCGCUWSub����
         mLCGCUWSubSql = "delete from LCGCUWSub where ProposalGrpContNo = '" + mGrpContNo + "'";

         //׼��LCGUWSub����
         mLCGUWSubSql = "delete from LCGUWSub where GrpContNo = '" + mGrpContNo + "'";

         //׼��lccuwSub����
         mLCCUWSubSql = "delete from lccuwSub where GrpContNo = '" + mGrpContNo + "'";

         //׼��lcuwSub����
         mLCUWSubSql = "delete from lcuwSub where GrpContNo = '" + mGrpContNo + "'";

         //׼��LCGCUWError����
         mLCGCUWErrorSql = "delete from LCGCUWError where ProposalGrpContNo = '" + mGrpContNo + "'";

         //׼��LCGUWError����
         mLCGUWErrorSql = "delete from LCGUWError where GrpContNo = '" + mGrpContNo + "'";

         //׼��lccuwError����
         mLCCUWErrorSql = "delete from lccuwError where GrpContNo = '" + mGrpContNo + "'";

         //׼��lcuwError����
         mLCUWErrorSql = "delete from lcuwError where GrpContNo = '" + mGrpContNo + "'";

         return true;
     }


    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return
     */
    private boolean prepareTransferData()
    {
        mTransferData.setNameAndValue("GrpContNo", mGrpContNo);
        mTransferData.setNameAndValue("PrtNo", mLCGrpContSchema.getPrtNo());
        mTransferData.setNameAndValue("SaleChnl", mLCGrpContSchema.getSaleChnl());
        mTransferData.setNameAndValue("ManageCom",
                mLCGrpContSchema.getManageCom());
        mTransferData.setNameAndValue("AgentCode",
                mLCGrpContSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup",
                mLCGrpContSchema.getAgentGroup());
        mTransferData.setNameAndValue("GrpNo", mLCGrpContSchema.getAppntNo());
        mTransferData.setNameAndValue("GrpName", mLCGrpContSchema.getGrpName());
        mTransferData.setNameAndValue("CValiDate",
                mLCGrpContSchema.getCValiDate());

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

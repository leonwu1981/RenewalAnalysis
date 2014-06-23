package com.sinosoft.workflow.ask;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;


/**
 * <p>Title: ����ѯ��¼����� </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author heyq
 * @version 1.0
 */

public class AskInputConfirmAfterInitService implements AfterInitService
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
    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();
    private LCContPlanSet mLCContPlanSet = new LCContPlanSet();
    private LCContPlanRiskSet mLCContPlanRiskSet = new LCContPlanRiskSet();
    private LCContPlanSet mNewLCContPlanSet = new LCContPlanSet();
    private LCContPlanRiskSet mNewLCContPlanRiskSet = new LCContPlanRiskSet();
    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;
    private String mOperate;
    private String mMissionID;
    private String mSubMissionID;
    private String mGrpContNo;
    private String mContSql;

    public AskInputConfirmAfterInitService()
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
    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();

        map.put(mLCGrpContSchema,"UPDATE");
        map.put(mContSql,"UPDATE");
        map.put(mLCContPlanSet,"INSERT");
        map.put(mLCContPlanRiskSet,"INSERT");
        map.put(mNewLCContPlanSet,"INSERT");
        map.put(mNewLCContPlanRiskSet,"INSERT");

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
            tError.moduleName = "GrpInputConfirmAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "��ѯ�����ͬ��Ϣʧ�ܣ���ȷ���Ƿ�¼����ȷ!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCGrpContSchema = tLCGrpContDB.getSchema();

        //У��������Ƿ��Ѿ�¼�뱻����
//        for (int i = 1; i <= tLCGrpPolSet.size(); i++)
//        {
//            if (tLCGrpPolSet.get(i).getPeoples2() != 0)
//            {
//                CError tError = new CError();
//                tError.moduleName = "GrpFirstWorkFlowCheck";
//                tError.functionName = "checkData";
//                tError.errorMessage = "����" + tLCGrpPolSet.get(i).getRiskCode() +
//                                      "��δ¼�뱻���ˣ���ɾ�������ֻ�¼�뱻�����ˣ�";
//                this.mErrors.addOneError(tError);
//                return false;
//            }
//        }
//
//        ExeSQL tExeSQL = new ExeSQL();
//        String tSql = "select distinct 1 from lccont where 1=1 "
//                      + " and grpcontno = '" + mGrpContNo + "'"
//                      ;
//        String rs = tExeSQL.getOneValue(tSql);
//        if (rs == null || rs.length() != 0)
//        {
//            CError tError = new CError();
//            tError.moduleName = "GrpFirstWorkFlowCheck";
//            tError.functionName = "checkData";
//            tError.errorMessage = "����������δ¼����˺�ͬ��Ϣ��";
//            this.mErrors.addOneError(tError);
//            return false;
//        }
//
//        tSql = "select distinct 1 from lcpol where 1=1 "
//               + " and grpcontno = '" + mGrpContNo + "'"
//               ;
//        rs = tExeSQL.getOneValue(tSql);
//        if (rs == null || rs.length() != 0)
//        {
//            CError tError = new CError();
//            tError.moduleName = "GrpFirstWorkFlowCheck";
//            tError.functionName = "checkData";
//            tError.errorMessage = "����������δ¼�����������Ϣ��";
//            this.mErrors.addOneError(tError);
//            return false;
//        }

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
            tError.moduleName = "GrpInputConfirmAfterInitService";
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
            tError.moduleName = "GrpInputConfirmAfterInitService";
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
            tError.moduleName = "GrpInputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ�������������ID
        mGrpContNo = (String) mTransferData.getValueByName("GrpContNo");
        if (mGrpContNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpInputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������mContNoʧ��!";
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

         //Ϊ�����ͬ�����¼���ˡ�¼��ʱ��
         mLCGrpContSchema.setInputOperator(mOperater);
         mLCGrpContSchema.setInputDate(PubFun.getCurrentDate());
         mLCGrpContSchema.setInputTime(PubFun.getCurrentTime());

         //Ϊ��ͬ�����¼���ˡ�¼��ʱ��
         mContSql = "update lccont set InputOperator ='" + mOperater + "',"
                    + "InputDate = '" + PubFun.getCurrentDate() + "',"
                    + "InputTime = '" + PubFun.getCurrentTime() + "'"
                    + " where grpcontno = '" + mGrpContNo + "'"
                    ;

         //Ϊ��Ʒ����׼������
         if(!prepareContPlan())
             return false;

         return true;

     }


    /**
     * prepareContPlan
     * Ϊ��Ʒ����׼������
     * ѯ��¼����Ϻ���Ҫ���ڱ��ռƻ����в���PlanType=4����ͬ�ļ�¼
     * @return boolean
     */
    private boolean prepareContPlan()
    {
        LCContPlanSchema tLCContPlanSchema;
        LCContPlanDB tLCContPlanDB = new LCContPlanDB();
        tLCContPlanDB.setGrpContNo(mGrpContNo);

        mLCContPlanSet = tLCContPlanDB.query();
        mNewLCContPlanSet = tLCContPlanDB.query();
        if (mLCContPlanSet == null || mLCContPlanSet.size() <= 0)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpInputConfirmAfterInitService";
            tError.functionName = "prepareContPlan";
            tError.errorMessage = "δ¼�뱣�ռƻ����ܱ���!";
            this.mErrors.addOneError(tError);
            return false;
        }

        for (int i = 1; i <= mLCContPlanSet.size(); i++)
        {
            mLCContPlanSet.get(i).setPlanType("4"); //�˱�����
            mNewLCContPlanSet.get(i).setPlanType("5"); //Ϊ��Ʒ������׼��
        }

        LCContPlanRiskDB tLCContPlanRiskDB = new LCContPlanRiskDB();
        tLCContPlanRiskDB.setGrpContNo(mGrpContNo);
        mLCContPlanRiskSet = tLCContPlanRiskDB.query();
        mNewLCContPlanRiskSet = tLCContPlanRiskDB.query();
        if (mLCContPlanRiskSet == null || mLCContPlanRiskSet.size() <= 0)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpInputConfirmAfterInitService";
            tError.functionName = "prepareContPlan";
            tError.errorMessage = "δ¼�뱣�ռƻ����ܱ���!";
            this.mErrors.addOneError(tError);
            return false;
        }

        for(int i=1;i<=mLCContPlanRiskSet.size();i++)
        {
            mLCContPlanRiskSet.get(i).setPlanType("4");
            mNewLCContPlanRiskSet.get(i).setPlanType("5");
        }

        return true;
    }


    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return
     */
    private boolean prepareTransferData()
    {
        mTransferData.setNameAndValue("GrpContNo",mLCGrpContSchema.getGrpContNo());
        mTransferData.setNameAndValue("PrtNo",mLCGrpContSchema.getPrtNo());
        mTransferData.setNameAndValue("SaleChnl",mLCGrpContSchema.getSaleChnl());
        mTransferData.setNameAndValue("ManageCom",mLCGrpContSchema.getManageCom());
        mTransferData.setNameAndValue("AgentCode",mLCGrpContSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup",mLCGrpContSchema.getAgentGroup());
        mTransferData.setNameAndValue("GrpName",mLCGrpContSchema.getGrpName());
        mTransferData.setNameAndValue("GrpNo",mLCGrpContSchema.getAppntNo());
        mTransferData.setNameAndValue("CValiDate",mLCGrpContSchema.getCValiDate());

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

package com.sinosoft.workflow.ca;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;
import com.sinosoft.lis.pubfun.PubFun1;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.llcase.ClientRegisterBackBL;
import com.sinosoft.lis.schema.LLCaseSchema;
import com.sinosoft.lis.schema.LLSurveySchema;
import com.sinosoft.workflow.ca.LLRgtSurveyInputjsService;


/**
 * <p>Title: ���⹤���� </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author pd
 * @version 1.0
 */
public class CaWorkFlowBL {
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();


    /** �����洫�����ݵ����� */
    private VData mInputData;

    VData tVData = new VData();


    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
    private VData tResult = new VData();

    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();


    //private VData mIputData = new VData();
    private TransferData mTransferData = new TransferData();


    /**���������� */
    ActivityOperator mActivityOperator = new ActivityOperator();

    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;
    private String mOperate;
    private String mSaveFlag;// ���mSaveFlagΪnull����ֵ����""����"0",��ʾ���ε�����Ҫ������,����,������,ֻ���ؽ��

    public CaWorkFlowBL() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    
    
    public VData getmResult()
    {
    	return mResult;
    }
    /**
     * �������ݵĹ�������
     * @param: cInputData ���������
     *         cOperate ���ݲ���
     * @return:
     */
    public boolean submitData(VData cInputData, String cOperate) {
        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData(cInputData, cOperate)) {
            return false;
        }
        System.out.println("---CaWorkFlowBL getInputData---");
        // ���ݲ���ҵ����
        if (!dealData()) {
            return false;
        }
        System.out.println("---CaWorkFlowBL dealData---");

        //׼������̨������
        if (!prepareOutputData()) {
            return false;
        }
        
        if(mSaveFlag==null || mSaveFlag.trim().equals("") || mSaveFlag.trim().equals("0"))
        {
        //�����ύ
	        CaWorkFlowBLS tCaWorkFlowBLS = new CaWorkFlowBLS();
	        System.out.println("Start CaWorkFlowBL Submit...");
	
	        if (!tCaWorkFlowBLS.submitData(mResult, mOperate)) {
	            // @@������
	            this.mErrors.copyAllErrors(tCaWorkFlowBLS.mErrors);
	
	            CError tError = new CError();
	            tError.moduleName = "CaWorkFlowBL";
	            tError.functionName = "submitData";
	            tError.errorMessage = "�����ύʧ��!";
	            this.mErrors.addOneError(tError);
	
	            return false;
	        }
        }
        System.out.println("---CaWorkFlowBLS commitData End ---");

        return true;
    }

    
    
    /**
     * �����������еõ����ж���
     *��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean getInputData(VData cInputData, String cOperate) {
        //�����������еõ����ж���
        //���ȫ�ֹ�������
        mGlobalInput = ((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput",
                0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData",
                0);
        mInputData = cInputData;
        if (mGlobalInput == null) {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ʧ��!";
            this.mErrors.addOneError(tError);

            return false;
        }

        //��ò���Ա����
        mOperater = mGlobalInput.Operator;
        if ((mOperater == null) || mOperater.trim().equals("")) {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
            this.mErrors.addOneError(tError);

            return false;
        }

        //��õ�½��������
        mManageCom = mGlobalInput.ManageCom;
        if ((mManageCom == null) || mManageCom.trim().equals("")) {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
            this.mErrors.addOneError(tError);

            return false;
        }

        mOperate = cOperate;
        if ((mOperate == null) || mOperate.trim().equals("")) {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operate����ڵ����ʧ��!";
            this.mErrors.addOneError(tError);

            return false;
        }
        mSaveFlag=(String)mTransferData.getValueByName("SaveFlag");
        return true;
    }


    /**
     * ���ݲ�����ҵ����
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean dealData() {

        if (mOperate.trim().equals("7699999999")) { //ִ�й��������˹��˱��������
            if (!Execute7699999999()) {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("7899999999")) {
            if (!Execute7899999999()) {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("7799999999")) {
            if (!Execute7799999999()) {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("7599999999")) {
            if (!Execute7599999999()) {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("7499999999")) {
            if (!Execute7499999999()) {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("7999999999")) {
            if (!Execute7999999999()) {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("8099999999")) {
            if (!Execute8099999999()) {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("8199999999")) {
            if (!Execute8199999999()) {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("8299999999")) {
            if (!Execute8299999999()) {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("6999999999")) {
            if (!Execute6999999999()) {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("7299999999")) {
            if (!Execute7299999999()) {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("9099999999")) {
            if (!Execute9099999999()) {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else if (mOperate.trim().equals("9199999999")) {
            if (!Execute9199999999()) {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        } else {
            if (!Execute()) {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        return true;
    }


    /**
     * ִ�гб����������˹��˱��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute() {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();

        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        System.out.println("CAFlag===" + mTransferData.getValueByName("CAFlag"));
        System.out.println("CaseNo===" + mTransferData.getValueByName("CaseNo"));
        System.out.println("RptorName===" +
                           mTransferData.getValueByName("RptorName"));
        System.out.println("MissionID===" +
                           mTransferData.getValueByName("MissionID"));
        System.out.println("SubMissionID===" +
                           mTransferData.getValueByName("SubMissionID"));
        System.out.println("ManageCom===" +
                           mTransferData.getValueByName("ManageCom"));
        System.out.println("Operator===" +
                           mTransferData.getValueByName("Operator"));
        System.out.println("RgtState===" +
                           mTransferData.getValueByName("RgtState"));
        System.out.println("InputDate===" +
                           mTransferData.getValueByName("InputDate"));

        System.out.println(
                "-------------------------------------------------------------");
        if (tMissionID == null) {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute0000000000";
            tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);

            return false;
        }

        if (tSubMissionID == null) {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute0000000000";
            tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);

            return false;
        }

        try {
            //����Service
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  mOperate, mInputData)) {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);

                return false;
            }
            //���ִ�����Ᵽ�������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData != null) {
                for (int i = 0; i < tVData.size(); i++) {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    mResult.add(tempVData);
                }
            }
        } catch (Exception ex) {
            // @@������

            this.mErrors.copyAllErrors(mActivityOperator.mErrors);

            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "����������ִ��������ExecuteMission�������!";
            this.mErrors.addOneError(tError);

            return false;
        }
        try {
            //����ִ�������Ᵽ�������������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    mOperate, mInputData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if ((tempVData != null) && (tempVData.size() > 0)) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            } else {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                System.out.println("Mission Test");
                return false;
            }
        } catch (Exception ex) {
            // @@������

            this.mErrors.copyAllErrors(mActivityOperator.mErrors);

            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "����������ִ��������CreateNextMission�������!";
            this.mErrors.addOneError(tError);

            return false;
        }
        try {
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                mOperate, mInputData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if ((tempVData != null) && (tempVData.size() > 0)) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            } else {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);

                return false;
            }
        } catch (Exception ex) {
            // @@������

            this.mErrors.copyAllErrors(mActivityOperator.mErrors);

            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "����������ִ��������DeleteMission�������!";
            this.mErrors.addOneError(tError);

            return false;
        }

        //*/
        return true;
    }

    /**
     * ִ�����⹤�������Ᵽ�������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * ������������ڵ�
     */
    private boolean Execute7499999999() { //*
        mResult.clear();

        ActivityOperator tActivityOperator = new ActivityOperator();

        try {
            if (!preparedRptNo()) {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);

                CError tError = new CError();
                tError.moduleName = "CaWorkFlowBL";
                tError.functionName = "preparedCaseNO";
                tError.errorMessage = "�����������κų���!";
                this.mErrors.addOneError(tError);
            }
            //����ִ�������⹤��������������������ڵ�
            if (tActivityOperator.CreateStartMission("0000000005", "0000005012",
                    tVData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        } catch (Exception ex) {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute7499999999";
            tError.errorMessage = "����������ִ�����⹤���������������!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/

        return true;

    }

    /**
     * ִ�����⹤�������Ᵽ�������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * �����ж� 1 �����ʵ�¼��  2 ���ɼ�¼
     */
    private boolean Execute7599999999() { //*
        mResult.clear();
        ActivityOperator tActivityOperator = new ActivityOperator();
        try {
            //����ִ�������⹤��������������������ڵ�
            String CAFlag = (String) mTransferData.getValueByName("CAFlag");
            if (CAFlag.equals("1")) {
                if (tActivityOperator.CreateStartMission("0000000005",
                        "0000005004", mInputData)) {
                    VData tempVData = new VData();
                    tempVData = tActivityOperator.getResult();
                    if (tempVData != null && tempVData.size() > 0) {
                        mResult.add(tempVData);
                        tempVData = null;
                    }
                }
            } else if (CAFlag.equals("2")) {
                if (tActivityOperator.CreateStartMission("0000000005",
                        "0000005005", mInputData)) {
                    VData tempVData = new VData();
                    tempVData = tActivityOperator.getResult();
                    if (tempVData != null && tempVData.size() > 0) {
                        mResult.add(tempVData);
                        tempVData = null;
                    }
                }
            }
        } catch (Exception ex) {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute7599999999";
            tError.errorMessage = "����������ִ�����⹤���������������!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/

        return true;

    }

    /**
     * ִ�����⹤�������Ᵽ�������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * ���ɱ����ڵ�
     */
    private boolean Execute7699999999() { //*
        mResult.clear();

        ActivityOperator tActivityOperator = new ActivityOperator();

        try {
            if (!preparedCaseNO()) {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);

                CError tError = new CError();
                tError.moduleName = "CaWorkFlowBL";
                tError.functionName = "preparedCaseNO";
                tError.errorMessage = "���ɰ����ų���!";
                this.mErrors.addOneError(tError);
            }
            //����ִ�������⹤��������������������ڵ�

            if (tActivityOperator.CreateStartMission("0000000005", "0000005001",
                    tVData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        } catch (Exception ex) {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute7699999999";
            tError.errorMessage = "����������ִ�����⹤���������������!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/

        return true;

    }

    /**
     * ִ�����⹤�������鱣�������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * ���ɵ���ڵ�
     */
    private boolean Execute7899999999() { //*
        mResult.clear();

        ActivityOperator tActivityOperator = new ActivityOperator();
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        String tActivityID = (String) mTransferData.getValueByName("ActivityID");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("CAFlag===" + mTransferData.getValueByName("CAFlag"));
        System.out.println("ActivityID===" + mTransferData.getValueByName("ActivityID"));
        System.out.println("CaseNo===" + mTransferData.getValueByName("CaseNo"));
        System.out.println("RptorName===" +
                           mTransferData.getValueByName("RptorName"));
        System.out.println("MissionID===" +
                           mTransferData.getValueByName("MissionID"));
        System.out.println("SubMissionID===" +
                           mTransferData.getValueByName("SubMissionID"));
        System.out.println("ManageCom===" +
                           mTransferData.getValueByName("ManageCom"));
        System.out.println("Operator===" +
                           mTransferData.getValueByName("Operator"));
        System.out.println("RgtState===" +
                           mTransferData.getValueByName("RgtState"));
        System.out.println("InputDate===" +
                           mTransferData.getValueByName("InputDate"));
        System.out.println("SurveyNo===" +
                           mTransferData.getValueByName("SurveyNo"));
        System.out.println("SurveyReturn===" +
                           mTransferData.getValueByName("SurveyReturn"));
        try {
            //����Service
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  tActivityID, mInputData)) {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);

                return false;
            }
            //���ִ�����Ᵽ�������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData != null) {
                for (int i = 0; i < tVData.size(); i++) {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    mResult.add(tempVData);
                }
            }
        } catch (Exception ex) {
            // @@������

            this.mErrors.copyAllErrors(mActivityOperator.mErrors);

            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "����������ִ��������ExecuteMission�������!";
            this.mErrors.addOneError(tError);

            return false;
        }
        VData aVData = new VData();
        //LLRgtSurveyInputjsService tLLRgtSurveyInputjsService = new LLRgtSurveyInputjsService();
        //tVData = LLRgtSurveyInputjsService.getResult();
        //String SurveyNo = LLRgtSurveyInputjsService.getSurveyNo;
        LLSurveySchema mLLSurveySchema = new LLSurveySchema();
        // tVData.getObjectByObjectName("VDate",1).get
        aVData = (VData) tVData.get(0);
        aVData = (VData) aVData.get(0);
        mLLSurveySchema = (LLSurveySchema) aVData.getObjectByObjectName(
                "LLSurveySchema", 0);
        String SurveyNo = mLLSurveySchema.getSurveyNo();
        //mLLSurveySchema.setSchema(( LLSurveySchema )tVData.getObjectByObjectName( "LLSurveySchema", 0 ));
        System.out.println("SurveyNo3===" + SurveyNo);

        //VData bVData = new VData();
        //LLSurveySchema mLLSurveySchema = new LLSurveySchema();
        //mLLSurveySchema = (LLSurveySchema) aVData.getObjectByObjectName(
        //        "LLSurveySchema", 0);
        //mTransferData = (TransferData)SurveyNo.subSequence(0, 15);
        mTransferData.setNameAndValue("SurveyNo",SurveyNo);
        System.out.println("SurveyNo4===" + SurveyNo);
        mInputData.add(mTransferData);

        try {
            //����ִ�������⹤��������������������ڵ�

            if (tActivityOperator.CreateStartMission("0000000005", "0000005010",
                    mInputData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        } catch (Exception ex) {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute7899999999";
            tError.errorMessage = "����������ִ�����⹤���������������!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/
        return true;
    }

//ɾ�����������Ľڵ�
    private boolean Execute7799999999() { //*
        mResult.clear();

        ActivityOperator tActivityOperator = new ActivityOperator();
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");

        try {
            //����Service
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000005011", mInputData)) {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);

                return false;
            }
            //���ִ�����Ᵽ�������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData != null) {
                for (int i = 0; i < tVData.size(); i++) {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    mResult.add(tempVData);
                }
            }
        } catch (Exception ex) {
            // @@������

            this.mErrors.copyAllErrors(mActivityOperator.mErrors);

            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "����������ִ��������ExecuteMission�������!";
            this.mErrors.addOneError(tError);

            return false;
        }

        try {

            //ɾ��ִ������鹤��������������������ڵ�
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000005011", mInputData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        } catch (Exception ex) {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute7799999999";
            tError.errorMessage = "����������ִ�����⹤���������������!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/
        return true;
    }

    //���˸���ȷ�Ϻ��ɾ��
    private boolean Execute7999999999() { //*
        mResult.clear();

        ActivityOperator tActivityOperator = new ActivityOperator();
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        try {

            //ɾ��ִ������鹤��������������������ڵ�
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000005009", mInputData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        } catch (Exception ex) {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute7999999999";
            tError.errorMessage = "����������ִ�����⹤���������������!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/
        return true;
    }

    //��������ȷ�Ϻ��ɾ��
    private boolean Execute8099999999() { //*
        mResult.clear();

        ActivityOperator tActivityOperator = new ActivityOperator();
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        try {

            //ɾ��ִ������鹤��������������������ڵ�
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000005012", mInputData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        } catch (Exception ex) {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute8099999999";
            tError.errorMessage = "����������ִ�����⹤���������������!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/
        return true;
    }

    //������������ڵ�
    private boolean Execute8199999999() { //*
        mResult.clear();

        ActivityOperator tActivityOperator = new ActivityOperator();

        try {

            //����ִ�������⹤��������������������ڵ�

            if (tActivityOperator.CreateStartMission("0000000005", "0000005013",
                    mInputData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        } catch (Exception ex) {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute8199999999";
            tError.errorMessage = "����������ִ�����⹤���������������!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/
        return true;
    }

    //�������ȷ�Ϻ��ɾ��
    private boolean Execute8299999999() { //*
        mResult.clear();

        ActivityOperator tActivityOperator = new ActivityOperator();
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        try {

            //ɾ��ִ������鹤��������������������ڵ�
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000005013", mInputData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        } catch (Exception ex) {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute8299999999";
            tError.errorMessage = "����������ִ�����⹤���������������!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/
        return true;
    }

    private boolean Execute6999999999() {
        tResult.clear();
        VData tVData = new VData();
        //�����������У��
        ClientRegisterBackBL tClientRegisterBackBL = new ClientRegisterBackBL();
        String strOperate = (String) mTransferData.getValueByName("strOperate");
        if (tClientRegisterBackBL.submitData(mInputData, strOperate)) {
            tVData = tClientRegisterBackBL.getResult();
            tResult.add(tVData);
        } else {
            this.mErrors.copyAllErrors(tClientRegisterBackBL.mErrors);
            return false;
        }
        ActivityOperator tActivityOperator = new ActivityOperator();
        LLCaseSchema mLLCaseSchema = new LLCaseSchema();
        mLLCaseSchema.setSchema((LLCaseSchema) tVData.getObjectByObjectName(
                "LLCaseSchema", 0));
        System.out.println("CaseNo ==== " + mLLCaseSchema.getCaseNo());
        mTransferData.setNameAndValue("CaseNo", mLLCaseSchema.getCaseNo());
        tResult.add(mLLCaseSchema);
        mInputData.add(mTransferData);
        //��ѯ�Ƿ���Ҫ�ʵ�¼�������
        String sql = "select count(*) from LLAffix where caseno = '" +
                     mLLCaseSchema.getCaseNo() +
                     "' and affixcode in('1304','1305','1314','1316','1317')";
        ExeSQL exesql = new ExeSQL();
        SSRS ssrs = exesql.execSQL(sql);
        String CAFlag = "";
        if (ssrs.getMaxRow() > 0) {
            CAFlag = "1";
        } else {
            CAFlag = "2";
        }
        try {
            if (CAFlag.equals("1")) {
                if (tActivityOperator.CreateStartMission("0000000005",
                        "0000005004", mInputData)) {
                    VData tempVData = new VData();
                    tempVData = tActivityOperator.getResult();
                    if (tempVData != null && tempVData.size() > 0) {
                        mResult.add(tempVData);
                        tempVData = null;
                    }
                }
            } else if (CAFlag.equals("2")) {
                if (tActivityOperator.CreateStartMission("0000000005",
                        "0000005005", mInputData)) {
                    VData tempVData = new VData();
                    tempVData = tActivityOperator.getResult();
                    if (tempVData != null && tempVData.size() > 0) {
                        mResult.add(tempVData);
                        tempVData = null;
                    }
                }
            }
        } catch (Exception ex) {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "GrpTbWorkFlowBL";
            tError.functionName = "Execute6999999999";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

   //����ֱ�����������������ڵ�
    private boolean Execute7299999999() {
         mResult.clear();
         ActivityOperator tActivityOperator = new ActivityOperator();
         tVData.add(mGlobalInput);
         tVData.add(mTransferData);

         try {
         //����ִ�������⹤��������������������ڵ�
            if (tActivityOperator.CreateStartMission("0000000005",
                            "0000005002",tVData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
         }
         catch (Exception ex)
         {
             // @@������
             this.mErrors.copyAllErrors(mActivityOperator.mErrors);
             CError tError = new CError();
             tError.moduleName = "CaWorkFlowBL";
             tError.functionName = "Execute7699999999";
             tError.errorMessage = "����������ִ�����⹤���������������!";
             this.mErrors.addOneError(tError);
             return false;
          }
          return true;
    }

    //���������������������������ڵ�
    private boolean Execute9099999999() {
        mResult.clear();
        ActivityOperator tActivityOperator = new ActivityOperator();
        tVData.add(mGlobalInput);
        tVData.add(mTransferData);

        try {
         //����ִ�������⹤��������������������ڵ�
            if (tActivityOperator.CreateStartMission("0000000005",
                            "0000005101",tVData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        }
        catch (Exception ex)
        {
             // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute9099999999";
            tError.errorMessage = "����������ִ���������⹤���������������!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }    
    
    //��������᰸��ɾ���ڵ�
    private boolean Execute9199999999() { 
        mResult.clear();

        ActivityOperator tActivityOperator = new ActivityOperator();
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");

        try {
            //����Service
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000005105", mInputData)) 
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            //���ִ�����Ᵽ�������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData != null) {
                for (int i = 0; i < tVData.size(); i++) {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    mResult.add(tempVData);
                }
            }
        } catch (Exception ex) {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "����������ִ��������ExecuteMission�������!";
            this.mErrors.addOneError(tError);
            return false;
        }
        try {

            //ɾ��ִ������鹤��������������������ڵ�
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000005105", mInputData)) {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0) {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        } catch (Exception ex) {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "CaWorkFlowBL";
            tError.functionName = "Execute9199999999";
            tError.errorMessage = "����������ִ�����⹤���������������!";
            this.mErrors.addOneError(tError);
            return false;
        } 
        return true;
    }   

    /**
     * ׼����Ҫ���������
     */
    private boolean prepareOutputData() {
        //mInputData.add( mGlobalInput );
        return true;
    }

    //����case����
    private boolean preparedCaseNO() {
        String tManageCom = mGlobalInput.ManageCom;
        String tRgtNo = PubFun1.CreateMaxNo("RgtNo", tManageCom);
 
        String tLimit = PubFun.getNoLimit(mGlobalInput.ManageCom);
        TransferData tempTransferData = new TransferData();
        VData tempVDate = new VData();
        tempTransferData.setNameAndValue("RGTNO", tRgtNo);//�ܰ�������
        tempVDate.add(tempTransferData);
        String tCaseNo = PubFun1.CreateMaxNo("CASENO", tLimit, tempVDate);//�����ⰸ��
        
        System.out.println("������" + tCaseNo);
        tVData.add(mGlobalInput);
        mTransferData.setNameAndValue("CaseNo", tCaseNo);
        tVData.add(mTransferData);
        return true;
    }

    //����RptNo����
    private boolean preparedRptNo() {
        String tLimit = PubFun.getNoLimit(this.mGlobalInput.ManageCom);
        System.out.println("������������� : " + tLimit);
        
        //String RGTNO = PubFun1.CreateMaxNo("RGTNO", tLimit);
        //������
        String RGTNO = PubFun1.CreateMaxNo("RGTNO", this.mGlobalInput.ManageCom);
        System.out.println("�������κ� :" + RGTNO);
        tVData.add(mGlobalInput);
        mTransferData.setNameAndValue("RptNo", RGTNO);
        tVData.add(mTransferData);
        return true;
    }

    public String getCaseNo() {
        String tCaseNo = (String) mTransferData.getValueByName("CaseNo");
        return tCaseNo;
    }

    public String getRptNo() {
        String tRptNo = (String) mTransferData.getValueByName("RptNo");
        return tRptNo;
    }

    public VData getResult() {
        return tResult;
    }

    private void jbInit() throws Exception {
    }
}

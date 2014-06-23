package com.sinosoft.workflow.bq;

import java.lang.*;
import java.util.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.vdb.*;
import com.sinosoft.utility.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.workflow.bq.*;
import com.sinosoft.workflowengine.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * ReWrite ZhangRong,FanX
 * @version 1.0
 */

public class EdorWorkFlowBL
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** �����洫�����ݵ����� */
    private VData mInputData;

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
    private String mOperate;


    /** ����������� */
    private LPPENoticeSet mLPPENoticeSet = new LPPENoticeSet();
    private LPPENoticeSet mAllLPPENoticeSet = new LPPENoticeSet();
    private LPPENoticeSchema mLPPENoticeSchema = new LPPENoticeSchema();

    /** ���������Ŀ�� */
    private LPPENoticeItemSet mLPPENoticeItemSet = new LPPENoticeItemSet();
    private LPPENoticeItemSet mmLPPENoticeItemSet = new LPPENoticeItemSet();
    private LPPENoticeItemSet mAllLPPENoticeItemSet = new LPPENoticeItemSet();

    public EdorWorkFlowBL()
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

        // ���ݲ���ҵ����
        if (!dealData())
        {
            return false;
        }

        //׼������̨������
        if (!prepareOutputData())
        {
            return false;
        }

        //�����ύ
        PubSubmit tps = new PubSubmit();

//        EdorWorkFlowBLS tEdorWorkFlowBLS = new EdorWorkFlowBLS();
//        System.out.println("Start EdorWorkFlowBLS Submit...");

        EdorWorkFlowBLS tEdorWorkFlowBLS = new EdorWorkFlowBLS();

        if (!tEdorWorkFlowBLS.submitData(mResult, mOperate))
        {
            // @@������
            this.mErrors.copyAllErrors(tEdorWorkFlowBLS.mErrors);

            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "submitData";
            tError.errorMessage = "�����ύʧ��!";
            this.mErrors.addOneError(tError);

            return false;
        }

//        if (! tps.submitData(mResult,mOperate))
//        {
//            // @@������
//            this.mErrors.copyAllErrors(tps.mErrors);
//            CError tError = new CError();
//            tError.moduleName = "EdorWorkFlowBLS";
//            tError.functionName = "submitData";
//            tError.errorMessage = "�����ύʧ��!";
//            this.mErrors.addOneError(tError);
//            return false;
//        }

        System.out.println("---EdorWorkFlowBLS commitData End ---");
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
        mGlobalInput = ((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
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
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�½��������
        mManageCom = mGlobalInput.ManageCom;
        if (mManageCom == null || mManageCom.trim().equals(""))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mOperate = cOperate;
        if (mOperate == null || mOperate.trim().equals(""))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operate����ڵ����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }


    /**
     * ���ݲ�����ҵ����
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {
        //������ʼ����ڵ�(��ȫ���뼶)
        if (mOperate.trim().equals("9999999998"))
        {
            if (!Execute9999999998())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }

        //��ȫ��������ӡ�˱�֪ͨ����
        if (mOperate.trim().equals("9999999999"))
        {
            if (!Execute9999999999())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }
        //ִ�б�ȫ���������˹��˱��������
        else if (mOperate.trim().equals("0000000000"))
        {
            if (!Execute0000000000())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }

        //ִ�б�ȫ�����������֪ͨ��������
        else if (mOperate.trim().equals("0000000001"))
        {
            if (!Execute0000000001())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }
        //ִ�б�ȫ�������ӷѻ������
        else if (mOperate.trim().equals("0000000002"))
        {
            if (!Execute0000000002())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }

            return true;

        }
        //ִ�б�ȫ��������Լ�������
        else if (mOperate.trim().equals("0000000003"))
        {
            if (!Execute0000000003())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }
        //ִ�б�ȫ������������֪ͨ��������
        else if (mOperate.trim().equals("0000000004"))
        {
            if (!Execute0000000004())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }
        //ִ�б�ȫ���������˱�֪ͨ��������
        else if (mOperate.trim().equals("0000000005"))
        {
            if (!Execute0000000005())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }
        //ִ�б�ȫ��������ӡ���֪ͨ��������
        else if (mOperate.trim().equals("0000000006"))
        {
            if (!Execute0000000006())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }

        //��ȫ��������ӡ�˱�֪ͨ����
        else if (mOperate.trim().equals("0000000007"))
        {
            if (!Execute0000000007())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }

        //��ȫ��������ӡ����֪ͨ����
        else if (mOperate.trim().equals("0000000008"))
        {
            if (!Execute0000000008())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }

        //ִ�б�ȫ�������˱�ȷ�ϻ������
//        else if (mOperate.trim().equals("0000000010"))
//        {
//            if (!Execute0000000010())
//            {
//                // @@������
//                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//                return false;
//            }
//            return true;
//
//        }

        //ִ�б�ȫ�������������֪ͨ��������
        else if (mOperate.trim().equals("0000000011"))
        {
            if (!Execute0000000011())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }

        //��ȫ��������ӡ�˱�֪ͨ����
        else if (mOperate.trim().equals("0000000012"))
        {
            if (!Execute0000000012())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;
        }
        //��ȫ���������մ�ӡ����֪ͨ����
        else if (mOperate.trim().equals("0000000013"))
        {
            if (!Execute0000000013())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;
        }
        //ִ�б�ȫ�������������֪ͨ��������
        else if (mOperate.trim().equals("0000000014"))
        {
            if (!Execute0000000014())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }

        //��ȫ����������˱�֪ͨ����
        else if (mOperate.trim().equals("0000000015"))
        {
            if (!Execute0000000015())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;

        }

        //��ȫ��������������֪ͨ����
        else if (mOperate.trim().equals("0000000016"))
        {
            if (!Execute0000000016())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;
        }
        //��ȫ��������������ʼ���
        else if (mOperate.trim().equals("0000000017"))
        {
            if (!Execute0000000017())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;
        }
        else if (mOperate.trim().equals("0000000019"))
        {
            if (!Execute0000000019())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                System.out.println(
                        "class��Ϣ: EdorWorkFloeBL -->ִ��Execute0000000019()����");
                return false;
            }
            return true;

        }

        else
        {
            if (!Execute())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            return true;
        }
    }


    /**
     * ִ�б�ȫ���������˹��˱��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000000()
    { //*
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "Execute0000000000";
            tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "Execute0000000000";
            tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000000", mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }

            //���ִ�б�ȫ���������˹��˱��������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    for (int j = 0; j < tempVData.size(); j++)
                    {
                        mResult.add(tempVData.get(i)); //ȡ��Mapֵ
                    }
                }
            }

            //����ִ���걣ȫ���������˹��˱��������������ڵ�

            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000000", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
        }
        catch (Exception ex)
        {
            // @@������
            System.out.println(ex.getStackTrace());
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "����������ִ�б�ȫ���������˹��˱�����������!";
            this.mErrors.addOneError(tError);
            return false;

        } //*/

        return true;

    }


    /**
     * ִ�б�ȫ�����������֪ͨ��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000001()
    {
        mResult.clear();
        VData tVData = new VData();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ�з������֪ͨ����������(������������ִ������Ϊͬһ����ʱ,����ִ����������ģʽ����)
        try
        {
            System.out.println("ActivityOperator name:" +
                               mActivityOperator.getClass());
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000001", mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }

            //���ִ�з������֪ͨ������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�˹��˱������֪ͨ��¼���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    mResult.add(tempVData); //ȡ��Mapֵ
                }
            }

            //����ִ���귢�����֪ͨ����������һ��ӡ���֪ͨ������ڵ�
            ActivityOperator tActivityOperator = new ActivityOperator();
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000001", mInputData))
            {
                VData tmpVData = new VData();
                tmpVData = tActivityOperator.getResult();
                if (tmpVData != null && tmpVData.size() > 0)
                {
                    mResult.add(tmpVData);
                    tmpVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�˹��˱������֪ͨ��¼�������,������һ��������ڵ����!";
                this.mErrors.addOneError(tError);
                return false;

            }

            //ɾ���������֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000001", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�˹��˱������֪ͨ��¼�������,ɾ��������ڵ����!";
                this.mErrors.addOneError(tError);
                return false;

            }
        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ִ�б�ȫ�������ӷ�¼��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000002()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����tSubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //ִ���˹��˱����ӷ�¼����
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000002", mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }

            //���ִ���˹��˱�����Լ¼������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    mResult.add(tempVData); //ȡ��Mapֵ
                }
            }

            //����ִ���˹��˱����ӷ�¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000002", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ִ�б�ȫ�������˹��˱�����Լ¼��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000003()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ���˹��˱�����Լ���
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000003", mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }

            //���ִ���˹��˱�����Լ¼������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�������˹��˱�����Լ¼���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    mResult.add(tempVData); //ȡ��Mapֵ
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000003", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ִ�б�ȫ��������������֪ͨ��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000004()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");

        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ���˹��˱�����Լ���
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000004", mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "EdorWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ���˹��˱���������֪ͨ���������!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����Լ¼������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�������˹��˱���������֪ͨ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000004", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                // tError.moduleName = "EdorWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ�б�ȫ�˹��˱���������֪ͨ�������,������һ��������ڵ����!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

            //ɾ��������֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000004", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "EdorWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ�б�ȫ�˹��˱���������֪ͨ�������,ɾ��������ڵ����!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ִ�б�ȫ���������˱�֪ͨ��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000005()
    {

        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");

        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //ִ���˹��˱������˱�֪ͨ����
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000005", mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "EdorWorkFlowBL";
                // tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ���˹��˱������˱�֪ͨ���������!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����Լ¼������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�������˹��˱������˱�֪ͨ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000005", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "EdorWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ�б�ȫ�˹��˱���������֪ͨ�������,������һ��������ڵ����!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000005", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "EdorWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ�б�ȫ�˹��˱������˱�֪ͨ�������,ɾ��������ڵ����!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ִ�б�ȫ��������ӡ���֪ͨ��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000006()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");

        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ���˹��˱�����ӡ���֪ͨ��
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000006", mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "EdorWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ���˹��˱�����ӡ���֪ͨ���������!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����Լ¼������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�������˹��˱�����ӡ���֪ͨ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000006", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "EdorWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ�б�ȫ�˹��˱�����ӡ���֪ͨ�������,������һ��������ڵ����!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000006", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "EdorWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ�б�ȫ�˹��˱�����ӡ���֪ͨ�������,ɾ��������ڵ����!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ִ�б�ȫ��������ӡ�˱�֪ͨ��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000007()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");

        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //ִ���˹��˱�����Լ���
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000007", mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "EdorWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "����������ִ��ִ�д�ӡ�˱�֪ͨ���������!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����ӡ�˱�֪ͨ������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�д�ӡ�˱�֪ͨ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000007", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "EdorWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "����������ִ�б�ȫ�˹��˱���������֪ͨ�������,������һ��������ڵ����!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000007", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "EdorWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "����������ִ�б�ȫ�˹��˱������˱�֪ͨ�������,ɾ��������ڵ����!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ִ�б�ȫ��������ӡ���֪ͨ��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000008()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");

        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ�д�ӡ���֪ͨ����
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000008", mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "EdorWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "����������ִ��ִ�д�ӡ���֪ͨ���������!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����ӡ���֪ͨ������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�д�ӡ���֪ͨ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000008", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "EdorWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "����������ִ�б�ȫ�˹��˱���������֪ͨ�������,������һ��������ڵ����!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000008", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "EdorWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "����������ִ�б�ȫ�˹��˱������˱�֪ͨ�������,ɾ��������ڵ����!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ִ�б�ȫ�������˱�ȷ�ϻ������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000010()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");

        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ���˹��˱�����Լ���
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000010", mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "EdorWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "����������ִ���˹���ȫ�������˱�ȷ���������!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����Լ¼������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�������˹���ȫ�������˱�ȷ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000010", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "EdorWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "���������汣ȫ�������˹���ȫ�������˱�ȷ�����������һ�������ڵ����!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000010", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//	   CError tError = new CError();
//	   tError.moduleName = "EdorWorkFlowBL";
//	   tError.functionName = "dealData";
//	   tError.errorMessage = "����������ִ�б�ȫ�˹��˱������˱�֪ͨ�������,ɾ��������ڵ����!";
//	   this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ִ�б�ȫ���������֪ͨ����ջ������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000011()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");

        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //ִ���˹��˱�����Լ���
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000011", mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			 CError tError = new CError();
//			 tError.moduleName = "EdorWorkFlowBL";
//			 tError.functionName = "dealData";
//			 tError.errorMessage = "����������ִ���˹���ȫ���������֪ͨ������������!";
//			 this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����Լ¼������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�������˹���ȫ���������֪ͨ������������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000011", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			  CError tError = new CError();
//			  tError.moduleName = "EdorWorkFlowBL";
//			  tError.functionName = "dealData";
//			  tError.errorMessage = "���������汣ȫ�������˹���ȫ���������֪ͨ��������������һ�������ڵ����!";
//			  this.mErrors .addOneError(tError) ;
                return false;
            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000011", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			  CError tError = new CError();
//			  tError.moduleName = "EdorWorkFlowBL";
//			  tError.functionName = "dealData";
//			  tError.errorMessage = "����������ִ�б�ȫ�˹��˱������˱�֪ͨ�������,ɾ��������ڵ����!";
//			  this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ִ�б�ȫ�������˱�֪ͨ����ջ������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000012()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");

        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //ִ���˹��˱�����Լ���
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000012", mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			  CError tError = new CError();
//			  tError.moduleName = "EdorWorkFlowBL";
//			  tError.functionName = "dealData";
//			  tError.errorMessage = "����������ִ���˹���ȫ�������˱�֪ͨ������������!";
//			  this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����Լ¼������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�������˹���ȫ�������˱�֪ͨ������������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000012", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			  CError tError = new CError();
//			  tError.moduleName = "EdorWorkFlowBL";
//			  tError.functionName = "dealData";
//			  tError.errorMessage = "���������汣ȫ�������˹���ȫ�������˱�֪ͨ��������������һ�������ڵ����!";
//			  this.mErrors .addOneError(tError) ;
                return false;
            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000012", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			  CError tError = new CError();
//			  tError.moduleName = "EdorWorkFlowBL";
//			  tError.functionName = "dealData";
//			  tError.errorMessage = "����������ִ�б�ȫ�˹��˱������˱�֪ͨ�������,ɾ��������ڵ����!";
//			  this.mErrors .addOneError(tError) ;
                return false;

            }
        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ִ�б�ȫ�������ظ����֪ͨ��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000013()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");

        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ�лظ����֪ͨ����
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000013", mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "EdorWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "����������ִ��ִ�лظ����֪ͨ���������!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱����ظ����֪ͨ������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�д�ӡ���֪ͨ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000013", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "EdorWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "����������ִ�б�ȫ�˹��˱����ظ�����֪ͨ�������,������һ��������ڵ����!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000013", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "EdorWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "����������ִ�б�ȫ�˹��˱����ظ�����֪ͨ�������,ɾ��������ڵ����!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ִ�б�ȫ�������������֪ͨ��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000014()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");

        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ���˹��˱�����ӡ���֪ͨ��
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000014", mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			 CError tError = new CError();
//			 tError.moduleName = "EdorWorkFlowBL";
//			 tError.functionName = "dealData";
//			 tError.errorMessage = "����������ִ���˹��˱�����ӡ���֪ͨ���������!";
//			 this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����Լ¼������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�������˹��˱�����ӡ���֪ͨ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000014", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			CError tError = new CError();
//			tError.moduleName = "EdorWorkFlowBL";
//			tError.functionName = "dealData";
//			tError.errorMessage = "����������ִ�б�ȫ�˹��˱�����ӡ���֪ͨ�������,������һ��������ڵ����!";
//			this.mErrors .addOneError(tError) ;
                return false;

            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000014", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			CError tError = new CError();
//			tError.moduleName = "EdorWorkFlowBL";
//			tError.functionName = "dealData";
//			tError.errorMessage = "����������ִ�б�ȫ�˹��˱�����ӡ���֪ͨ�������,ɾ��������ڵ����!";
//			this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ִ�б�ȫ����������˱�֪ͨ��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000015()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");

        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //ִ���˹��˱�����Լ���
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000015", mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "EdorWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "����������ִ��ִ�в���˱�֪ͨ���������!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����ӡ�˱�֪ͨ������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�в���˱�֪ͨ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000015", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "EdorWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "����������ִ�б�ȫ�˹��˱�������˱�֪ͨ�������,������һ��������ڵ����!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000015", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "EdorWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "����������ִ�б�ȫ�˹��˱������˱�֪ͨ�������,ɾ��������ڵ����!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ִ�б�ȫ�������������֪ͨ��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000016()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");

        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ�д�ӡ���֪ͨ����
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000016", mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "EdorWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "����������ִ��ִ�в������֪ͨ���������!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����ӡ���֪ͨ������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "EdorWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�в������֪ͨ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000016", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;

            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                "0000000016", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    private boolean Execute0000000017()
    { //*
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "Execute0000000017";
            tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "Execute0000000017";
            tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000017", mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }

            //���ִ�б�ȫ���������˹��˱��������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    for (int j = 0; j < tempVData.size(); j++)
                    {
                        mResult.add(tempVData.get(i)); //ȡ��Mapֵ
                    }
                }
            }

            //����ִ���걣ȫ���������˹��˱��������������ڵ�

            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000017", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "����������ִ�б�ȫ���������˹��˱�����������!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/

        return true;

    }


    /**
     * ��ȫ���������뼶���֪ͨ¼��
     * @return boolean
     */
    private boolean Execute0000000019()
    { //*
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "Execute0000000019";
            tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "Execute0000000019";
            tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        System.out.println("class��Ϣ��EdorWorkFlowBL --->MissionID=" + tMissionID);
        System.out.println("class��Ϣ��EdorWorkFlowBL --->SubMissionID=" +
                           tSubMissionID);
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000000019", mInputData))
            {
                System.out.println("class��Ϣ��EdorWorkFlowBL-->Result = false");
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            else
            {
                System.out.println("class��Ϣ��EdorWorkFlowBL-->Result = true");
            }

            //���ִ�б�ȫ���������֪ͨ¼��������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    System.out.println("tempVData.size()=" + tempVData.size());
                    for (int j = 0; j < tempVData.size(); j++)
                    {
                        mResult.add(tempVData.get(j)); //ȡ��Mapֵ
                        // tempVData.get(i).toString()
                        System.out.println(tempVData.get(j).toString());
                    }
                }
            }

            //����ִ���걣ȫ���������֪ͨ¼��������������ڵ�

            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000000019", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                System.out.println("dfdfdfdfd");
                if (tempVData != null && tempVData.size() > 0)
                {
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
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "����������ִ�б�ȫ���������֪ͨ¼�뱣����������!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/

        return true;

    }


    /**
     * ������ʼ����ڵ�(��ȫ���뼶)
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute9999999998()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        try
        {
            //����ִ�б�ȫ������ʼ����ڵ�ͺ˱����۽ڵ�
            if (tActivityOperator.CreateStartMission("0000000000", "0000000017",
                    mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                mResult.add(tempVData);
                tempVData = null;
            }
            else
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "Execute9999999998";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ������ʼ����ڵ�
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute9999999999()
    {
        mResult.clear();
        ActivityOperator tActivityOperator = new ActivityOperator();
        try
        {
            //����ִ���귢�����֪ͨ����������һ��ӡ���֪ͨ������ڵ�
            if (tActivityOperator.CreateStartMission("0000000000", "0000000000",
                    mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                mResult.add(tempVData);
                tempVData = null;
            }
            else
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "Execute9999999999";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    private boolean Execute()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();

        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "Execute0000000000";
            tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);

            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "Execute0000000000";
            tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);

            return false;
        }

        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  mOperate, mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);

                return false;
            }
            //���ִ�гб����������˹��˱��������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    mResult.add(tempVData);
                }
            }

            //����ִ����б����������˹��˱��������������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    mOperate, mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if ((tempVData != null) && (tempVData.size() > 0))
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);

                return false;
            }

            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                mOperate, mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if ((tempVData != null) && (tempVData.size() > 0))
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);

                return false;
            }
        }
        catch (Exception ex)
        {
            // @@������

            this.mErrors.copyAllErrors(mActivityOperator.mErrors);

            CError tError = new CError();
            tError.moduleName = "EdorWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "����������ִ������Լ����������!";
            this.mErrors.addOneError(tError);

            return false;
        }
        return true;
    }

    public VData getResult()
    {
      return this.mResult;
    }



    /**
     * ׼����Ҫ���������
     */
    private boolean prepareOutputData()
    {
        //mInputData.add( mGlobalInput );
        return true;
    }


}

/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.circ;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;
import com.sinosoft.workflowengine.ActivityOperator;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class CircReportWorkFlowBL
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

    /**���������� */
    ActivityOperator mActivityOperator = new ActivityOperator();

    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;
    private String mOperate;

    public CircReportWorkFlowBL()
    {
    }

    /**
     * �������ݵĹ�������
     * @param cInputData VData ���������
     * @param cOperate String ���ݲ���
     * @return boolean
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

        System.out.println("---CircReportWorkFlowBL dealData---");

        //׼������̨������
        if (!prepareOutputData())
        {
            return false;
        }

        System.out.println("---CircReportWorkFlowBL prepareOutputData---");

        //�����ύ
        CircReportWorkFlowBLS tCircReportWorkFlowBLS = new
                CircReportWorkFlowBLS();
        System.out.println("Start CircReportWorkFlowBL Submit...");

        if (!tCircReportWorkFlowBLS.submitData(mResult, mOperate))
        {
            // @@������
            this.mErrors.copyAllErrors(tCircReportWorkFlowBLS.mErrors);

            CError tError = new CError();
            tError.moduleName = "CircReportWorkFlowBL";
            tError.functionName = "submitData";
            tError.errorMessage = "�����ύʧ��!";
            this.mErrors.addOneError(tError);

            return false;
        }

        System.out.println("---CircReportWorkFlowBLS commitData End ---");

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
        //���ȫ�ֹ�������
        mGlobalInput = ((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput",
                0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData",
                0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CircReportWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ʧ��!";
            this.mErrors.addOneError(tError);

            return false;
        }

        //��ò���Ա����
        mOperater = mGlobalInput.Operator;
        if ((mOperater == null) || mOperater.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CircReportWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
            this.mErrors.addOneError(tError);

            return false;
        }

        //��õ�½��������
        mManageCom = mGlobalInput.ManageCom;
        if ((mManageCom == null) || mManageCom.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CircReportWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
            this.mErrors.addOneError(tError);

            return false;
        }

        mOperate = cOperate;
        if ((mOperate == null) || mOperate.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CircReportWorkFlowBL";
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
     * @return boolean
     */
    private boolean dealData()
    {
        //ִ�б�ȫ���������˱�֪ͨ��������
        if (!Execute())
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            return false;
        }

        return true;
    }

    /**
     * ִ�б�ȫ���������˹��˱��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @return boolean
     */
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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

            //���ִ�б�ȫ���������˹��˱��������Ľ��
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

            //����ִ���걣ȫ���������˹��˱��������������ڵ�
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "����������ִ�б���ᱨ�����������!";
            this.mErrors.addOneError(tError);

            return false;
        }
        return true;
    }

    /**
     * ׼����Ҫ���������
     * @return boolean
     */
    private static boolean prepareOutputData()
    {
        return true;
    }
}

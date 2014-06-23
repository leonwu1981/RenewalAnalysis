/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LOPRTManagerDB;
import com.sinosoft.lis.f1print.MeetF1PBLS;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.LOPRTManagerSchema;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;


/**
 * <p>Title: ������������:����Լ��ӡ����֪ͨ�� </p>
 * <p>Description:��ӡ����֪ͨ�鹤����AfterInit������ </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class MeetF1PAfterInitService implements AfterInitService
{
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();

    private String mOperate = "";
    private String CurrentDate = PubFun.getCurrentDate();

    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** �����洫�����ݵ����� */
    private VData mResult = new VData();

    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();

    private TransferData mTransferData = new TransferData();

    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;

    /** ҵ�����ݲ����ַ��� */
    private String mMissionID;

    public MeetF1PAfterInitService()
    {
    }


    /**
     * �������ݵĹ�������
     * @param cInputData
     * @param cOperate
     * @return
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        mOperate = cOperate;
        try
        {
            // �õ��ⲿ��������ݣ������ݱ��ݵ�������
            if (!getInputData(cInputData, mOperate))
            {
                return false;
            }

            if (!saveData(cInputData))
            {
                return false;
            }

            return true;

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            buildError("submitData", ex.toString());
            return false;
        }
    }

    /**
     * �����ӡ״̬����
     * @param mInputData VData
     * @return boolean
     */
    private boolean saveData(VData mInputData)
    {

        //����ӡˢ�Ų�ѯ��ӡ�����еļ�¼
        //mLOPRTManagerSchema
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();
        tLOPRTManagerDB.setSchema(mLOPRTManagerSchema);
        if (!tLOPRTManagerDB.getInfo())
        {
            mErrors.copyAllErrors(tLOPRTManagerDB.mErrors);
            buildError("outputXML", "��ȡ�ô�ӡ����������ʱ��������");
            return false;
        }
        mLOPRTManagerSchema = tLOPRTManagerDB.getSchema();
        mLOPRTManagerSchema.setStateFlag("1");
        mLOPRTManagerSchema.setDoneDate(CurrentDate);
        mLOPRTManagerSchema.setExeOperator(mGlobalInput.Operator);
        tLOPRTManagerDB.setSchema((LOPRTManagerSchema) mInputData.
                getObjectByObjectName("LOPRTManagerSchema", 0));

        mResult.add(mLOPRTManagerSchema);
        mResult.add(tLOPRTManagerDB);
        MeetF1PBLS tMeetF1PBLS = new MeetF1PBLS();
        tMeetF1PBLS.submitData(mResult, mOperate);
        if (tMeetF1PBLS.mErrors.needDealError())
        {
            mErrors.copyAllErrors(tMeetF1PBLS.mErrors);
            buildError("saveData", "�ύ���ݿ����");
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

        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintSendNoticeAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintSendNoticeAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWSendNoticeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mOperate = cOperate;

        //��õ�ǰ�������������ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintSendNoticeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //ȫ�ֱ���
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mLOPRTManagerSchema.setSchema((LOPRTManagerSchema) cInputData.
                getObjectByObjectName(
                "LOPRTManagerSchema", 0));

        if (mGlobalInput == null)
        {
            buildError("getInputData", "û�еõ��㹻����Ϣ��");
            return false;
        }

        return true;
    }

    private void buildError(String szFunc, String szErrMsg)
    {
        CError cError = new CError();

        cError.moduleName = "RefuseAppF1PBL";
        cError.functionName = szFunc;
        cError.errorMessage = szErrMsg;
        this.mErrors.addOneError(cError);
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

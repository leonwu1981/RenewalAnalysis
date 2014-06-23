/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.xb;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.LCRReportSet;
import com.sinosoft.lis.vschema.LOPRTManagerSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: </p>
 * <p>Description:�������ڵ�����:��ȫ�˹��˱��˱�֪ͨ����շ����� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PRnewTakeBackRReportAfterInitService implements AfterInitService
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
    private String mManageCom;
    private String mOperate;
    /** ҵ�����ݲ����ַ��� */
    private String mPolNo;
    private String mMissionID;
    private String mPrtSeq;
    private String mSerialNo;
    private boolean mPatchFlag;
    private String mReplyContente;
    private String mOldPrtSeq; //����õ�֤�ǲ���֤,��ͬʱ����ʧԭ��֤Ҳ����.��֮�������ԭ��֤,�����Ѳ�����,��ͬʱҲҪ�Ѳ����ĵ�֤���յ�
//   private Reflections mReflections = new Reflections();

    /**ִ�б�ȫ��������Լ�������0000000013*/
    /**������*/
    private LCPolSchema mLCPolSchema = new LCPolSchema();
    /** ��ȫ�˱����� */
    private LCUWMasterSchema mLCUWMasterSchema = new LCUWMasterSchema();
    /** ��ӡ����� */
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();
    private LOPRTManagerSet mLOPRTManagerSet = new LOPRTManagerSet(); //����õ�֤�ǲ���֤,��ͬʱ����ʧԭ��֤Ҳ����.��֮�������ԭ��֤,�����Ѳ�����,��ͬʱҲҪ�Ѳ����ĵ�֤���յ�
    /** �������� */
    private LCRReportSchema mLCRReportSchema = new LCRReportSchema();

    public PRnewTakeBackRReportAfterInitService()
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

        //����ҵ����
        if (!dealData())
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
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();

        //��Ӻ˱�֪ͨ���ӡ���������
        if (mLOPRTManagerSet != null && mLOPRTManagerSet.size() > 0)
        {
            map.put(mLOPRTManagerSet, "UPDATE");
        }

        //��ӱ�ȫ�����˱���������
        if (mLCUWMasterSchema != null)
        {
            map.put(mLCUWMasterSchema, "UPDATE");
        }

        //��ӱ�ȫ����������
        if (mLCRReportSchema != null)
        {
            map.put(mLCRReportSchema, "UPDATE");
        }

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
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setPolNo(mPolNo);
        if (!tLCPolDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mPolNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCPolSchema.setSchema(tLCPolDB);

        //У�鱣ȫ�����˱�����
        //У�鱣����Ϣ
        LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
        tLCUWMasterDB.setProposalNo(mPolNo);
        if (!tLCUWMasterDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mPolNo + "��ȫ�����˱�������Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCUWMasterSchema.setSchema(tLCUWMasterDB);

        // ����δ��ӡ״̬�ĺ˱�֪ͨ���ڴ�ӡ������ֻ����һ��
        // ������ͬһ���������ͣ�ͬһ���������룬ͬһ��������������
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

        tLOPRTManagerDB.setCode(PrintManagerBL.CODE_PRnewMEET); //
        tLOPRTManagerDB.setPrtSeq(mPrtSeq);
        tLOPRTManagerDB.setOtherNo(mPolNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_INDPOL); //������
        tLOPRTManagerDB.setStateFlag("1");

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "��ѯ��ӡ�������Ϣ����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLOPRTManagerSet.size() != 1)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "�ڴ�ӡ������û�д����Ѵ�ӡ������״̬������֪ͨ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLOPRTManagerSchema = tLOPRTManagerSet.get(1);
        if (mLOPRTManagerSchema.getPatchFlag() == null)
        {
            mPatchFlag = false;
        }
        else if (mLOPRTManagerSchema.getPatchFlag().equals("0"))
        {
            mPatchFlag = false;
        }
        else if (mLOPRTManagerSchema.getPatchFlag().equals("1"))
        {
            mPatchFlag = true;
        }

        //����õ�֤�ǲ���֤,��ͬʱ����ʧԭ��֤Ҳ����.��֮�������ԭ��֤,�����Ѳ�����,��ͬʱҲҪ�Ѳ����ĵ�֤���յ�
        if (mPatchFlag)
        {
            LOPRTManagerDB tempLOPRTManagerDB = new LOPRTManagerDB();
            mOldPrtSeq = mLOPRTManagerSchema.getOldPrtSeq();
            String tStr = "select * from LOPRTManager where (PrtSeq = '" + mOldPrtSeq + "'"
                    + "or OldPrtSeq = '" + mOldPrtSeq + "')";
            LOPRTManagerSet tempLOPRTManagerSet = tempLOPRTManagerDB.executeQuery(tStr);
            if (tempLOPRTManagerSet.size() == 1)
            {
                // @@������
                CError tError = new CError();
                tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
                tError.functionName = "preparePrint";
                tError.errorMessage = "��ѯ�ڴ�ӡ������û�иò�������֪ͨ���ԭ֪ͨ���¼��Ϣ����!";
                this.mErrors.addOneError(tError);
                return false;
            }

            for (int i = 1; i <= tempLOPRTManagerSet.size(); i++)
            {
                mLOPRTManagerSet.add(tempLOPRTManagerSet.get(i));
            }
        }
        else
        {
            LOPRTManagerDB tempLOPRTManagerDB = new LOPRTManagerDB();
            mOldPrtSeq = mLOPRTManagerSchema.getPrtSeq();
            if (mOldPrtSeq != null && !mOldPrtSeq.equals(""))
            {
                tempLOPRTManagerDB.setOldPrtSeq(mOldPrtSeq);
                LOPRTManagerSet tempLOPRTManagerSet = tempLOPRTManagerDB.query();
                if (tempLOPRTManagerSet != null && tempLOPRTManagerSet.size() > 0)
                {
                    for (int i = 1; i <= tempLOPRTManagerSet.size(); i++)
                    {
                        mLOPRTManagerSet.add(tempLOPRTManagerSet.get(i));
                    }
                }
            }
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
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
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
            tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
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
            tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mOperate = cOperate;

        //���ҵ������
        if (mTransferData == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mPolNo = (String) mTransferData.getValueByName("PolNo");
        if (mPolNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������PolNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ�������������ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //���ҵ��������
        mPrtSeq = (String) mTransferData.getValueByName("PrtSeq");
        if (mPrtSeq == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //���ҵ��������
        mLCRReportSchema = (LCRReportSchema) mTransferData.getValueByName("LCRReportSchema");
        if (mLCRReportSchema == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������LPRReportSchemaʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mReplyContente = mLCRReportSchema.getReplyContente();
        mSerialNo = mLCRReportSchema.getPrtSeq();

        if (mSerialNo == null || mSerialNo.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewPrintTakeBackRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������LPRReportSchema�е�SerialNoʧ��!";
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
        // �˱�������Ϣ
        if (!prepareRReport())
        {
            return false;
        }

        // ��ӡ����
        if (!preparePrint())
        {
            return false;
        }

        return true;

    }

    /**
     * ׼���˱�������Ϣ
     * �����������������򷵻�false,���򷵻�true
     */
    private boolean prepareRReport()
    {

        ////׼���˱�������Ϣ
        mLCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
        mLCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());
        mLCUWMasterSchema.setReportFlag("3"); //��������֪ͨ��

        //׼����������Ϣ
        LCRReportDB tLCRReportDB = new LCRReportDB();
        LCRReportSet tLCRReportSet = new LCRReportSet();
//	  tLCRReportDB.setContNo2(mPolNo) ;
        tLCRReportDB.setPrtSeq(mSerialNo);
        tLCRReportSet = tLCRReportDB.query();
        if (tLCRReportSet == null || tLCRReportSet.size() != 1)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewPrintTakeBackRReportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "��ѯ����������LPRReportSchemaʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCRReportSchema = tLCRReportSet.get(1);
        mLCRReportSchema.setReplyFlag("1");
        if (mReplyContente != null)
        {
            mLCRReportSchema.setReplyContente(mReplyContente);
        }
        mLCRReportSchema.setReplyOperator(mOperater);
        mLCRReportSchema.setReplyDate(PubFun.getCurrentDate());
        mLCRReportSchema.setReplyTime(PubFun.getCurrentTime());
        mLCRReportSchema.setModifyDate(PubFun.getCurrentDate());
        mLCRReportSchema.setModifyTime(PubFun.getCurrentTime());
        return true;
    }

    /**
     * ׼����ӡ��Ϣ��
     * @return
     */
    private boolean preparePrint()
    {
        //׼����ӡ���������
        for (int i = 1; i <= mLOPRTManagerSet.size(); i++)
        {
            mLOPRTManagerSet.get(i).setStateFlag("2");
        }
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

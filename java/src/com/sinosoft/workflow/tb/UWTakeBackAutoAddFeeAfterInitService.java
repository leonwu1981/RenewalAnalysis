/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: �������ڵ�����:����Լ�˹��˱�����֪ͨ����շ����� </p>
 * <p>Description: ���ղ���֪ͨ��AfterInit������������ </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class UWTakeBackAutoAddFeeAfterInitService implements AfterInitService
{

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
    private String mContNo;
    private String mMissionID;
    private String mCertifyNo;
    private String mCertifyCode;
    private boolean mPatchFlag;
    private String mTakeBackOperator;
    private String mTakeBackMakeDate;
    private String mOldPrtSeq; //����õ�֤�ǲ���֤,��ͬʱ����ʧԭ��֤Ҳ����.��֮�������ԭ��֤,�����Ѳ�����,��ͬʱҲҪ�Ѳ����ĵ�֤���յ�

    /**ִ��������������Լ�������0000000011*/
    /**��ͬ������*/
    private LCContSchema mLCContSchema = new LCContSchema();

//    /** �����˱����� */
//    private LCCUWMasterSchema mLCCUWMasterSchema = new LCCUWMasterSchema();

    /** ��ӡ����� */
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();
    private LOPRTManagerSet mLOPRTManagerSet = new LOPRTManagerSet(); //����õ�֤�ǲ���֤,��ͬʱ����ʧԭ��֤Ҳ����.��֮�������ԭ��֤,�����Ѳ�����,��ͬʱҲҪ�Ѳ����ĵ�֤���յ�

    /** ��֤���ű�*/
    private LZSysCertifySet mLZSysCertifySet = new LZSysCertifySet();
    public UWTakeBackAutoAddFeeAfterInitService()
    {
    }

    /**
     * �������ݵĹ�������
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
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
     * @return boolean
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

//        //������������˱���������
//        if (mLCCUWMasterSchema != null)
//        {
//            map.put(mLCCUWMasterSchema, "UPDATE");
//        }

        //����������֪ͨ���Զ����ű�����
        if (mLZSysCertifySet != null && mLZSysCertifySet.size() > 0)
        {
            map.put(mLZSysCertifySet, "UPDATE");
        }

        mResult.add(map);
        return true;
    }

    /**
     * У��ҵ������
     * @return boolean
     */
    private boolean checkData()
    {
        //У�鱣����Ϣ
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mContNo);
        if (!tLCContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mContNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);

        // ����δ��ӡ״̬�ĺ˱�֪ͨ���ڴ�ӡ������ֻ����һ��
        // ������ͬһ���������ͣ�ͬһ���������룬ͬһ��������������
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

        tLOPRTManagerDB.setCode(PrintManagerBL.CODE_URGE_PB); //
        tLOPRTManagerDB.setPrtSeq(mCertifyNo);
        tLOPRTManagerDB.setOtherNo(mContNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_CONT); //��ͬ��
        tLOPRTManagerDB.setStateFlag("1");

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewPrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "��ѯ��ӡ�������Ϣ����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLOPRTManagerSet.size() != 1)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "UWPrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "�ڴ�ӡ������û�д����Ѵ�ӡ������״̬�����֪ͨ��!";
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
            StringBuffer tSBql = new StringBuffer(128);
            tSBql.append("select * from LOPRTManager where (PrtSeq = '");
            tSBql.append(mOldPrtSeq);
            tSBql.append("' or OldPrtSeq = '");
            tSBql.append(mOldPrtSeq);
            tSBql.append("')");
            LOPRTManagerSet tempLOPRTManagerSet = tempLOPRTManagerDB.executeQuery(tSBql.toString());
            if (tempLOPRTManagerSet.size() == 1)
            {
                // @@������
                CError tError = new CError();
                tError.moduleName = "UWPrintTakeBackAutoHealthAfterInitService";
                tError.functionName = "preparePrint";
                tError.errorMessage = "��ѯ�ڴ�ӡ������û�иò������֪ͨ���ԭ֪ͨ���¼��Ϣ����!";
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
                if (tempLOPRTManagerSet != null &&
                        tempLOPRTManagerSet.size() > 0)
                {
                    for (int i = 1; i <= tempLOPRTManagerSet.size(); i++)
                    {
                        mLOPRTManagerSet.add(tempLOPRTManagerSet.get(i));
                    }
                }
            }
        }

        //��ѯϵͳ��֤���ն��б�
        for (int i = 1; i <= mLOPRTManagerSet.size(); i++)
        {
            if (mLOPRTManagerSet.get(i).getStateFlag() != null &&
                    mLOPRTManagerSet.get(i).getStateFlag().trim().equals("1"))
            {
                LZSysCertifyDB tLZSysCertifyDB = new LZSysCertifyDB();
                LZSysCertifySet tLZSysCertifySet = new LZSysCertifySet();
                tLZSysCertifyDB.setCertifyCode(PrintManagerBL.CERT_CODE_ADDFEE); //���֪ͨ���ʶ
                tLZSysCertifyDB.setCertifyNo(mLOPRTManagerSet.get(i).getPrtSeq());
                tLZSysCertifySet = tLZSysCertifyDB.query();
                if (tLZSysCertifySet == null || tLZSysCertifySet.size() != 1)
                {
                    // @@������
                    CError tError = new CError();
                    tError.moduleName =
                            "UWTakeBackAutoAddFeeAfterInitService";
                    tError.functionName = "preparePrint";
                    tError.errorMessage = "����֪ͨ��ʱ,LZSysCertifySchema����Ϣ��ѯʧ��!";
                    this.mErrors.addOneError(tError);
                    return false;
                }
                mLZSysCertifySet.add(tLZSysCertifySet.get(1));
            }
        }
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
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);

        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterInitService";
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
            tError.moduleName = "UWTakeBackAutoAddFeeAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //���ҵ������
        if (mTransferData == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mContNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������ContNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ�������������ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //���ҵ��������
        mCertifyNo = (String) mTransferData.getValueByName("CertifyNo");
        if (mCertifyNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //���ҵ��������
        mCertifyCode = (String) mTransferData.getValueByName("CertifyCode");
        if (mCertifyCode == null || mCertifyCode.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //���ҵ��������
        LZSysCertifySchema tLZSysCertifySchema = new LZSysCertifySchema();
        tLZSysCertifySchema = (LZSysCertifySchema) mTransferData.getValueByName(
                "LZSysCertifySchema");
        if (tLZSysCertifySchema == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������LZSysCertifySchemaʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mTakeBackOperator = tLZSysCertifySchema.getTakeBackOperator();
        if (mTakeBackOperator == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������TakeBackOperatorʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mTakeBackMakeDate = tLZSysCertifySchema.getTakeBackMakeDate();
        if (mTakeBackMakeDate == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWTakeBackAutoAddFeeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������mTakeBackMakeDateʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean dealData()
    {
        //��ӡ����
        if (!preparePrint())
        {
            return false;
        }

        //����ϵͳ��֤��ӡ����
        if (!prepareAutoSysCertSendOut())
        {
            return false;
        }

        return true;

    }

    /**
     * ׼����ӡ��Ϣ��
     * @return boolean
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

    /**
     * ׼���˱�������Ϣ
     * �����������������򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean prepareAutoSysCertSendOut()
    {
        //׼����֤���չ��������
        for (int i = 1; i <= mLZSysCertifySet.size(); i++)
        {
            mLZSysCertifySet.get(i).setTakeBackMakeDate(PubFun.getCurrentDate());
            mLZSysCertifySet.get(i).setTakeBackMakeTime(PubFun.getCurrentTime());
            mLZSysCertifySet.get(i).setModifyDate(PubFun.getCurrentDate());
            mLZSysCertifySet.get(i).setModifyTime(PubFun.getCurrentTime());
            mLZSysCertifySet.get(i).setTakeBackOperator(mTakeBackOperator);
            mLZSysCertifySet.get(i).setStateFlag("1");
        }
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

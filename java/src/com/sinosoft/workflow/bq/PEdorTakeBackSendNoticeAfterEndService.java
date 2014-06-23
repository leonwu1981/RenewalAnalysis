package com.sinosoft.workflow.bq;

import com.sinosoft.lis.db.LWMissionDB;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.LBMissionSet;
import com.sinosoft.lis.vschema.LWMissionSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterEndService;

/**
 * <p>Title: </p>
 * <p>Description:�������ڵ�����:��ȫ�˹��˱����֪ͨ����շ����� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PEdorTakeBackSendNoticeAfterEndService implements AfterEndService
{

    /** �������࣬ÿ����Ҫ�����������ж����ø��� */
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
    private String mString;
    /** ҵ�����ݲ����ַ��� */
    private String mEdorNo;
    private String mContNo;
    private String mMissionID;
    private String mSubMissionID;
    private Reflections mReflections = new Reflections();

    /**ִ�б�ȫ��������Լ�������0000000011*/
    /**������*/
    private LCContSchema mLCContSchema = new LCContSchema();
    /** ��ȫ�˱����� */
    private LPUWMasterMainSchema mLPUWMasterMainSchema = new LPUWMasterMainSchema();
    /** ����������ڵ��*/
    private LWMissionSchema mLWMissionSchema = new LWMissionSchema();
    private LWMissionSchema mInitLWMissionSchema = new LWMissionSchema(); //��ȫ�˹��˱���������ʼ�ڵ�
    private LWMissionSet mLWMissionSet = new LWMissionSet();
    /** ����������ڵ㱸�ݱ�*/
    private LBMissionSet mLBMissionSet = new LBMissionSet();
    public PEdorTakeBackSendNoticeAfterEndService()
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

        //������ع�����ͬ��ִ����ϵ�����ڵ������
        if (mLWMissionSet != null && mLWMissionSet.size() > 0)
        {
            map.put(mLWMissionSet, "DELETE");
        }

        //������ع�����ͬ��ִ����ϵ�����ڵ㱸�ݱ�����
        if (mLBMissionSet != null && mLBMissionSet.size() > 0)
        {
            map.put(mLBMissionSet, "INSERT");
        }

        //���ӱ�ȫ��������ʼ����ڵ������
        if (mInitLWMissionSchema != null)
        {
            map.put(mInitLWMissionSchema, "UPDATE");
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
        //��ѯ��������ǰ����켣��
        LWMissionDB tLWMissionDB = new LWMissionDB();
        LWMissionSet tLWMissionSet = new LWMissionSet();
        tLWMissionDB.setMissionID(mMissionID);
        tLWMissionDB.setActivityID("0000000012");
        tLWMissionDB.setSubMissionID(mSubMissionID);
        tLWMissionSet = tLWMissionDB.query();
        if (tLWMissionSet == null || tLWMissionSet.size() != 1)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackSendNoticeAfterEndService";
            tError.functionName = "checkData";
            tError.errorMessage = "��ѯ�������켣��LWMissionʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLWMissionSchema = tLWMissionSet.get(1);
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackSendNoticeAfterEndService";
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
            tError.moduleName = "PEdorPrintTakeBackSendNoticeAfterEndService";
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
            tError.moduleName = "PEdorPrintTakeBackSendNoticeAfterEndService";
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackSendNoticeAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mEdorNo = (String) mTransferData.getValueByName("EdorNo");
        if (mEdorNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackSendNoticeAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������EdorNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mContNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackSendNoticeAfterEndService";
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
            tError.moduleName = "PEdorPrintTakeBackSendNoticeAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ���������������ID
        mSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
        if (mSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackSendNoticeAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ��������г������򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {

        //��ȫ�˱���������ʼ�ڵ�״̬�ı�
        if (prepareMission() == false)
        {
            return false;
        }

        return true;

    }


    /**
     * ׼����ӡ��Ϣ��
     * @return
     */
    private boolean prepareMission()
    {

        //��ѯ��ͬһ���֪ͨ��(ԭ��ӡ��ˮ����ͬ��)�����յ�����ڵ�,������
        boolean flag = false;
        String tStr = "Select * from LWMission where MissionID = '" + mMissionID + "'"
                + "and ActivityID = '0000000012'"
                + "and SubMissionID <> '" + mSubMissionID + "'"
                + "and MissionProp14 = '" + mLWMissionSchema.getMissionProp14() + "'"
                + " union "
                + "Select * from LWMission where MissionID = '" + mMissionID + "'"
                + " and ActivityID = '0000000015'"
                + " and MissionProp3 = '" + mLWMissionSchema.getMissionProp14() + "'"
                + " union "
                + "Select * from LWMission where MissionID = '" + mMissionID + "'"
                + " and ActivityID = '0000000007'"
                + " and MissionProp8 = '" + mLWMissionSchema.getMissionProp14() + "'";

        LWMissionDB tLWMissionDB = new LWMissionDB();
        mLWMissionSet = tLWMissionDB.executeQuery(tStr);
        if (mLWMissionSet == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterEndService";
            tError.functionName = "prepareMission";
            tError.errorMessage = "��ȫ��������ʼ����ڵ��ѯ����!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (mLWMissionSet.size() < 0)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterEndService";
            tError.functionName = "prepareMission";
            tError.errorMessage = "��ȫ��������ʼ����ڵ�LWMission��ѯ����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        for (int i = 1; i <= mLWMissionSet.size(); i++)
        {
            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
            LBMissionSchema tLBMissionSchema = new LBMissionSchema();

            tLWMissionSchema = mLWMissionSet.get(i);
            String tSerielNo = PubFun1.CreateMaxNo("MissionSerielNo", 10);
            mReflections.transFields(tLBMissionSchema, tLWMissionSchema);
            tLBMissionSchema.setSerialNo(tSerielNo);
            tLBMissionSchema.setActivityStatus("3"); //�ڵ�����ִ�����
            tLBMissionSchema.setLastOperator(mOperater);
            tLBMissionSchema.setMakeDate(PubFun.getCurrentDate());
            tLBMissionSchema.setMakeTime(PubFun.getCurrentTime());
            mLBMissionSet.add(tLBMissionSchema);
        }

        //�жθú˱�֪ͨ����պ�,�Ƿ�ñ�ȫ�����Ѵ����˹��˱��ѻظ�״̬.
        tStr = "Select count(*) from LWMission where MissionID = '" + mMissionID + "'"
                + " and ActivityID in ('0000000011','0000000012','0000000013','0000000006','0000000007','0000000008','0000000014','0000000015','0000000016')";
        String tReSult = new String();
        ExeSQL tExeSQL = new ExeSQL();
        tReSult = tExeSQL.getOneValue(tStr);
        if (tExeSQL.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tExeSQL.mErrors);
            CError tError = new CError();
            tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterEndService";
            tError.functionName = "prepareMission";
            tError.errorMessage = "ִ��SQL��䣺" + tStr + "ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (tReSult == null || tReSult.equals(""))
        {
            return false;
        }

        int tCount = 0;
        tCount = Integer.parseInt(tReSult); //�Ѱ����˱��νڵ㼰���ͬ���ڵ�
        if (tCount > (mLWMissionSet.size() + 1))
        { //���ں˱�δ�ظ�״̬,�����޸ı�ȫ�˹��˱�����ʼ�ڵ�״
            mInitLWMissionSchema = null;
        }
        else
        {
            //���ں˱��ѻظ�״̬,�޸ı�ȫ�˹��˱�����ʼ�ڵ�״̬Ϊ�ѻظ�
            LWMissionSet tLWMissionSet = new LWMissionSet();
            tStr = "Select * from LWMission where MissionID = '" + mMissionID + "'"
                    + "and ActivityID = '0000000000'";

            tLWMissionSet = tLWMissionDB.executeQuery(tStr);
            if (tLWMissionSet == null || tLWMissionSet.size() != 1)
            {
                // @@������
                this.mErrors.copyAllErrors(tExeSQL.mErrors);
                CError tError = new CError();
                tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterEndService";
                tError.functionName = "prepareMission";
                tError.errorMessage = "��ѯ��������ȫ�˹��˱�����ʼ����ڵ�ʧ��!";
                this.mErrors.addOneError(tError);
                return false;
            }
            mInitLWMissionSchema = tLWMissionSet.get(1);
            mInitLWMissionSchema.setActivityStatus("3");

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
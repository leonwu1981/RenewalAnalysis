/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LCContDB;
import com.sinosoft.lis.db.LCInsuredDB;
import com.sinosoft.lis.db.LCPolDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.LCContSchema;
import com.sinosoft.lis.vschema.LCInsuredSet;
import com.sinosoft.lis.vschema.LDSpotTrackSet;
import com.sinosoft.lis.vschema.LCPolSet;
import com.sinosoft.utility.*;
import com.sinosoft.lis.tb.CheckDrawBL;
import com.sinosoft.workflowengine.AfterInitService;


/**
 * <p>Title: ������������:��������Լɨ��¼��ȷ��</p>
 * <p>Description: ɨ��¼��ȷ�Ϲ�����AfterInit������</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author: qiuyang
 * @version 1.0
 */

public class InputConfirmAfterInitService implements AfterInitService
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
//    private String mApproveFlag;
    private String mMissionID;


    /**������*/
    private LCContSchema mLCContSchema = new LCContSchema();
    private LCInsuredSet mLCInsuredSet = new LCInsuredSet();
    private LCPolSet mLCPolSet = new LCPolSet();
    private LDSpotTrackSet mLDSpotTrackSet = new LDSpotTrackSet();
    public InputConfirmAfterInitService()
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

        //У��ҵ������
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
        if (!prepareOutputData(cInputData))
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
    private boolean prepareOutputData(VData cInputData)
    {
        mResult.clear();
        MMap map = new MMap();
        CheckDrawBL tCheckDrawBL=new CheckDrawBL();
        tCheckDrawBL.submitData(cInputData,"");
        VData tVData=new VData();
        tVData=tCheckDrawBL.getResult();
        mLDSpotTrackSet = (LDSpotTrackSet) tVData.getObjectByObjectName(
                "LDSpotTrackSet", 0);
        map.put(mLDSpotTrackSet, "INSERT");
        map.put(mLCContSchema, "UPDATE");
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
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mContNo);
        if (!tLCContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "InputConfirmAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mContNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);
        mLCContSchema.setInputOperator(mGlobalInput.Operator);
        mLCContSchema.setInputDate(PubFun.getCurrentDate());
        mLCContSchema.setInputTime(PubFun.getCurrentTime());

        //У���ͬ�����Ƿ��б�����
        LCInsuredDB tLCInsuredDB = new LCInsuredDB();
        tLCInsuredDB.setContNo(mContNo);
        mLCInsuredSet = tLCInsuredDB.query();
        if (mLCInsuredSet == null || mLCInsuredSet.size() == 0)
        {
            CError tError = new CError();
            tError.moduleName = "InputConfirmAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "������" + mLCContSchema.getContNo() + "��û�б�������Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //У���ͬ�����Ƿ������ֵ�
        for (int i = 0; i < mLCInsuredSet.size(); i++)
        {
            LCPolDB tLCPolDB = new LCPolDB();
            tLCPolDB.setContNo(mContNo);
            tLCPolDB.setInsuredNo(mLCInsuredSet.get(i + 1).getInsuredNo());
            mLCPolSet = tLCPolDB.query();
            if (mLCPolSet == null || mLCPolSet.size() == 0)
            {
                CError tError = new CError();
                tError.moduleName = "InputConfirmAfterInitService";
                tError.functionName = "checkData";
                tError.errorMessage = "�ͻ�" + mLCInsuredSet.get(i + 1).getInsuredNo() + "��������Ϣ!";
                this.mErrors.addOneError(tError);
                return false;
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
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);
        //mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "InputConfirmAfterInitService";
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
            tError.moduleName = "InputConfirmAfterInitService";
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
            tError.moduleName = "InputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
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
            tError.moduleName = "InputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ���������mCont
        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mContNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "InputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������ContNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     *
     */
    private static boolean dealData()
    {

        return true;
    }

    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return
     */
    private boolean prepareTransferData()
    {
        mTransferData.setNameAndValue("ContNo", mLCContSchema.getContNo());
        mTransferData.setNameAndValue("PrtNo", mLCContSchema.getPrtNo());
        mTransferData.setNameAndValue("AppntNo", mLCContSchema.getAppntNo());
        mTransferData.setNameAndValue("AppntName", mLCContSchema.getAppntName());
        mTransferData.setNameAndValue("Operator", mLCContSchema.getOperator());
        mTransferData.setNameAndValue("MakeDate", mLCContSchema.getMakeDate());
        mTransferData.setNameAndValue("AgentCode", mLCContSchema.getAgentCode());
        mTransferData.setNameAndValue("ManageCom", mLCContSchema.getManageCom());

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

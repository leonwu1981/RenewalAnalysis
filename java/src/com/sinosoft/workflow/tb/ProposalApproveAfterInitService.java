/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LCContDB;
import com.sinosoft.lis.db.LCIssuePolDB;
import com.sinosoft.lis.db.LCPolDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.LCContSchema;
import com.sinosoft.lis.tb.CustomerdRelaInfoUI;
import com.sinosoft.lis.tb.GetLCFamilyRelaInfo;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title:������������:����Լ�µ����� </p>
 * <p>Description: �µ����˹�����AfterInit������ </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft </p>
 * @author HYQ
 * @version 1.0
 */

public class ProposalApproveAfterInitService implements AfterInitService
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
    private String mOperate;

    /** ҵ�����ݲ����ַ��� */
    private String mContNo;
    private String mApproveFlag;
    private String mApproveDate;
    private String mApproveTime;
    private String mMissionID;
    private boolean checkfamilyr = true;

    /**������*/
    private LCContSchema mLCContSchema = new LCContSchema();
    public LCFamilyRelaInfoSet tLCFamilyRelaInfoSet = new LCFamilyRelaInfoSet();
    public LCFamilyInfoSet tLCFamilyInfoSet = new LCFamilyInfoSet();
    private LCPolSet mLCPolSet = new LCPolSet();
    public ProposalApproveAfterInitService()
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
        if (checkfamilyr)
        {
            VData frdata = new VData();
            TransferData frTransferData = new TransferData();
            frTransferData.setNameAndValue("ContNo", mContNo);
            frdata.add(frTransferData);
            GetLCFamilyRelaInfo frGetLCFamilyRelaInfo = new GetLCFamilyRelaInfo();
            if (!frGetLCFamilyRelaInfo.GetFamilyRelaInfo(frdata, "INSERT"))
            {
                CError tError = new CError();
                tError.moduleName = "UWRReportAfterInitService";
                tError.functionName = "checkData";
                tError.errorMessage = "����" + mContNo + "���ɼ�ͥ��ϵʱʧ��";
                this.mErrors.addOneError(tError);
                return false;
            }
            VData fresultdata = new VData();
            fresultdata = frGetLCFamilyRelaInfo.getResult();
            System.out.println("asdasdasd");
            tLCFamilyRelaInfoSet = (LCFamilyRelaInfoSet) fresultdata.
                    getObjectByObjectName("LCFamilyRelaInfoSet", 0);
            tLCFamilyInfoSet = (LCFamilyInfoSet) fresultdata.getObjectByObjectName(
                    "LCFamilyInfoSet", 0);
            if (tLCFamilyRelaInfoSet.size() > 0)
            {
                map.put(tLCFamilyRelaInfoSet, "INSERT");
            }
            if (tLCFamilyInfoSet.size() > 0)
            {
                map.put(tLCFamilyInfoSet, "INSERT");
            }
        }
        map.put(mLCContSchema, "UPDATE");
        map.put(mLCPolSet, "UPDATE");
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
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mContNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);
        //���˵����账���ͥ��ϵ
        if (mLCContSchema.getFamilyType().equals("0"))
        {
            checkfamilyr = false;
        }
        else
        {
            checkfamilyr = true;
        }

        //У���ͬ�����Ƿ������ֵ�
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setContNo(mContNo);
        mLCPolSet = tLCPolDB.query();
        if (mLCPolSet == null || mLCPolSet.size() <= 0)
        {
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "���ֱ���" + mContNo + "��Ϣ��ѯʧ��!";
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

        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ProposalApproveAfterInitService";
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
            tError.moduleName = "ProposalApproveAfterInitService";
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
            tError.moduleName = "ProposalApproveAfterInitService";
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
            tError.moduleName = "ProposalApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (mLCContSchema.getAppFlag() != null &&
                !mLCContSchema.getAppFlag().trim().equals("0"))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "ProposalApproveBL";
            tError.functionName = "checkData";
            tError.errorMessage = "�˵�����Ͷ���������ܽ��и��˲���!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (mLCContSchema.getUWFlag() != null &&
                !mLCContSchema.getUWFlag().trim().equals("0"))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "ProposalApproveBL";
            tError.functionName = "checkData";
            tError.errorMessage = "��Ͷ�����Ѿ���ʼ�˱������ܽ��и��˲���!";
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
        if (checkfamilyr)
        {
            VData urdata = new VData();
            TransferData urTransferData = new TransferData();
            urTransferData.setNameAndValue("ContNo", mContNo);
            urdata.add(urTransferData);
            CustomerdRelaInfoUI urCustomerdRelaInfoUI = new CustomerdRelaInfoUI();
            if (!urCustomerdRelaInfoUI.submitData(urdata, "INSERT"))
            {
                // @@������
                CError tError = new CError();
                tError.moduleName = "CustomerdRelaInfoBL";
                tError.functionName = "submitData";
                tError.errorMessage = "���ɿͻ���ϵʱ����";
                this.mErrors.addOneError(tError);
                return false;
            }
        }

        mApproveDate = PubFun.getCurrentDate();
        mApproveTime = PubFun.getCurrentTime();
        LCIssuePolDB tLCIssuePolDB = new LCIssuePolDB();
        LCIssuePolSet tLCIssuePolSet = new LCIssuePolSet();
        String tSql = "Select * from lcissuepol where ContNo = '" + mContNo
                + "' and backobjtype = '1' and REPLYMAN is null ";
        tLCIssuePolSet = tLCIssuePolDB.executeQuery(tSql);
        if (tLCIssuePolSet == null)
        {
            CError tError = new CError();
            tError.moduleName = "ProposalApproveAfterInitService";
            tError.functionName = "dealData";
            tError.errorMessage = "��ѯ�����ʧ��!";
            this.mErrors.addOneError(tError);
        }
        System.out.println("size==" + tLCIssuePolSet.size());
        if (tLCIssuePolSet.size() > 0)
        {
            System.out.println("ApproveFlag = 1");
            mApproveFlag = "1"; //���ѷ��Ĳ���Ա�����,���븴���޸�
        }
        else
        {
            System.out.println("ApproveFlag = 9");
            mApproveFlag = "9";
        }

        //׼�������ĸ��˱�־
        mLCContSchema.setApproveFlag(mApproveFlag);
        mLCContSchema.setApproveDate(mApproveDate);
        mLCContSchema.setApproveTime(mApproveTime);
        mLCContSchema.setApproveCode(mOperate);
        mLCContSchema.setModifyDate(mApproveDate);
        mLCContSchema.setModifyTime(mApproveTime);

        //׼�����ֱ����ĸ��˱�־
        for (int i = 1; i <= mLCPolSet.size(); i++)
        {
            mLCPolSet.get(i).setApproveFlag(mApproveFlag);
            mLCPolSet.get(i).setApproveDate(mApproveDate);
            mLCPolSet.get(i).setApproveTime(mApproveTime);
            mLCPolSet.get(i).setApproveCode(mOperate);
            mLCPolSet.get(i).setModifyDate(mApproveDate);
            mLCPolSet.get(i).setModifyTime(mApproveTime);
        }
        return true;
    }

    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return
     */
    private boolean prepareTransferData()
    {
        System.out.println("-----ApproveFlag==" + mApproveFlag);
        mTransferData.setNameAndValue("ApproveFlag", mApproveFlag);

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

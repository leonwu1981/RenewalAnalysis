/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;


/**
 * <p>Title: ������������:����Լ�˱�����</p>
 * <p>Description: �˱�����</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class ReManuUWAfterInitService implements AfterInitService
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();


    /** �����洫�����ݵ����� */
    private VData mResult = new VData();


    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();

    private TransferData mTransferData = new TransferData();

    private LCCUWMasterSet mLCCUWMasterSet = new LCCUWMasterSet();
    private LCCUWSubSet mLCCUWSubSet = new LCCUWSubSet();
    private LCUWMasterSet mLCUWMasterSet = new LCUWMasterSet();
    private LCUWSubSet mLCUWSubSet = new LCUWSubSet();


    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;


    /** ҵ�����ݲ����ַ��� */
    private String mContNo;
    private String mUWFlag;
    private String mBackUWGrade;
    private String mBackAppGrade;
    private String mOperator;
    private String mUWPopedom; //�˱�����
    private String mAppGrade; //���뼶��
    private String mMissionID;


    /**������*/
    private LCContSchema mLCContSchema = new LCContSchema();
    private LCPolSet mLCPolSet = new LCPolSet();
    public ReManuUWAfterInitService()
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
     * @return boolean
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();

        map.put(mLCContSchema, "UPDATE");
        map.put(mLCPolSet, "UPDATE");
        map.put(mLCCUWMasterSet, "UPDATE");
        map.put(mLCCUWSubSet, "INSERT");
        map.put(mLCUWMasterSet, "UPDATE");
        map.put(mLCUWSubSet, "INSERT");
        mResult.add(map);
        return true;
    }


    /**
     * У��ҵ������
     * @return boolean
     */
    private boolean checkData()
    {
        //У��˱�Ա����
        LDUserDB tLDUserDB = new LDUserDB();
        tLDUserDB.setUserCode(mOperater);
        System.out.println("mOperate" + mOperater);
        if (!tLDUserDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "ReManuUWAfterInitService";
            tError.functionName = "checkUWGrade";
            tError.errorMessage = "�޴˲���Ա��Ϣ�����ܺ˱�!������Ա��" + mOperater + "��";
            this.mErrors.addOneError(tError);
            return false;
        }

        LDUWUserDB tLDUWUserDB = new LDUWUserDB();
       tLDUWUserDB.setUserCode(mOperater);
       if (!tLDUWUserDB.getInfo())
       {
           CError tError = new CError();
           tError.moduleName = "UWManuNormChkBL";
           tError.functionName = "checkUWGrade";
           tError.errorMessage = "�޴˺˱�ʦ��Ϣ�����ܺ˱�����!������Ա��" + mOperater + "��";
           this.mErrors.addOneError(tError);
           return false;
       }
       String tUWPopedom = tLDUWUserDB.getUWPopedom();
       if (tUWPopedom == null || tUWPopedom.trim().equals("")) {
         CError tError = new CError();
         tError.moduleName = "ReManuUWAfterInitService";
         tError.functionName = "CheckDate";
         tError.errorMessage = "�ú˱�ʦû�ж���Ȩ��!";
         this.mErrors.addOneError(tError);
         return false;
       }

        //У�鱣����Ϣ
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mContNo);
        if (!tLCContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "ReManuUWAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mContNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);

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
            tError.moduleName = "ReManuUWAfterInitService";
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
            tError.moduleName = "ReManuUWAfterInitService";
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
            tError.moduleName = "ReManuUWAfterInitService";
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
            tError.moduleName = "ReManuUWAfterInitService";
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
            tError.moduleName = "ReManuUWAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mUWFlag = (String) mTransferData.getValueByName("UWFlag");
        if (mUWFlag == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReManuUWAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������UWFlagʧ��!";
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

        mUWFlag = "z"; //�˱�������־
        //׼�������ĸ��˱�־
        mLCContSchema.setUWFlag(mUWFlag);

//��Ϊ�ں˱�ʱ�����ڸ����վܱ����ڵ����LCCont�ı��ѽ����˴��¼��㣬�ʶ�����ȥ�ĵ���ҲҪ����һ�α���    chenhq
        String tsql = "select sum(prem) from lcpol where contno='"+ mContNo +"'";
        ExeSQL tExeSQL = new ExeSQL();
        String UWprem = tExeSQL.getOneValue(tsql);
        mLCContSchema.setPrem(Double.parseDouble(UWprem));

        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setContNo(mLCContSchema.getContNo());
        mLCPolSet = tLCPolDB.query();
        //׼�����ֱ����ĸ��˱�־
        for (int i = 1; i <= mLCPolSet.size(); i++)
        {
            mLCPolSet.get(i).setUWFlag(mUWFlag);
        }

        //׼����ͬ���˱�����
        if (!prepareContUW())
        {
            return false;
        }

        //׼�����ָ��˱�����
        if (!prepareAllUW())
        {
            return false;
        }

        return true;
    }


    /**
     * ׼�������պ˱���Ϣ
     * �����������������򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean prepareAllUW()
    {
        mLCUWMasterSet.clear();
        mLCUWSubSet.clear();

        for (int i = 1; i <= mLCPolSet.size(); i++)
        {
            LCPolSchema tLCPolSchema = new LCPolSchema();

            tLCPolSchema = mLCPolSet.get(i);

            LCUWMasterSchema tLCUWMasterSchema = new LCUWMasterSchema();
            LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
            tLCUWMasterDB.setPolNo(tLCPolSchema.getPolNo());
            LCUWMasterSet tLCUWMasterSet = new LCUWMasterSet();
            tLCUWMasterSet = tLCUWMasterDB.query();
            if (tLCUWMasterDB.mErrors.needDealError())
            {
                // @@������
                this.mErrors.copyAllErrors(tLCUWMasterDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "UWManuNormChkBL";
                tError.functionName = "prepareAllUW";
                tError.errorMessage = "LCUWMaster��ȡ��ʧ��!";
                this.mErrors.addOneError(tError);
                return false;
            }

            int n = tLCUWMasterSet.size();
            System.out.println("��Ͷ�����ĺ˱�����ǰ��¼����:  " + n);
            if (n == 1)
            {
                tLCUWMasterSchema = tLCUWMasterSet.get(1);

                //Ϊ�˱��������˱���˱�����ͺ˱���
                mBackUWGrade = tLCUWMasterSchema.getUWGrade();
                mBackAppGrade = tLCUWMasterSchema.getAppGrade();
                mOperator = tLCUWMasterSchema.getOperator();

                //tLCUWMasterSchema.setUWNo(tLCUWMasterSchema.getUWNo()+1);�˱������е�UWNo��ʾ��Ͷ�������������˹��˱�(�ȼ��ھ��������Զ��˱�����),�������˹��˱�����(�����˱�֪ͨ��,�ϱ���)�¹�����.���Խ���ע��.sxy-2003-09-19
                tLCUWMasterSchema.setPassFlag(mUWFlag); //ͨ����־
                tLCUWMasterSchema.setState(mUWFlag);
                tLCUWMasterSchema.setAutoUWFlag("2"); // 1 �Զ��˱� 2 �˹��˱�
                tLCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
                tLCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());

                //�ָ��˱�����ͺ˱�Ա
                tLCUWMasterSchema.setUWGrade(mBackUWGrade);
                tLCUWMasterSchema.setAppGrade(mBackAppGrade);
                tLCUWMasterSchema.setOperator(mOperator);
                //����
                LDSysTraceSchema tLDSysTraceSchema = new LDSysTraceSchema();
                tLDSysTraceSchema.setPolNo(mContNo);
                tLDSysTraceSchema.setCreatePos("�˹��˱�");
                tLDSysTraceSchema.setPolState("1001");
                LDSysTraceSet inLDSysTraceSet = new LDSysTraceSet();
                inLDSysTraceSet.add(tLDSysTraceSchema);

                VData tVData = new VData();
                tVData.add(mGlobalInput);
                tVData.add(inLDSysTraceSet);

                LockTableBL LockTableBL1 = new LockTableBL();
                if (!LockTableBL1.submitData(tVData, "DELETE"))
                {
                    System.out.println("����ʧ�ܣ�");
                }

            }
            else
            {
                // @@������
                this.mErrors.copyAllErrors(tLCUWMasterDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "UWManuNormChkBL";
                tError.functionName = "prepareAllUW";
                tError.errorMessage = "LCUWMaster��ȡ���ݲ�Ψһ!";
                this.mErrors.addOneError(tError);
                return false;
            }

            mLCUWMasterSet.add(tLCUWMasterSchema);

            // �˱��켣��
            LCUWSubSchema tLCUWSubSchema = new LCUWSubSchema();
            LCUWSubDB tLCUWSubDB = new LCUWSubDB();
            tLCUWSubDB.setContNo(tLCPolSchema.getContNo());
            tLCUWSubDB.setPolNo(tLCPolSchema.getPolNo());
            LCUWSubSet tLCUWSubSet = new LCUWSubSet();
            tLCUWSubSet = tLCUWSubDB.query();
            if (tLCUWSubDB.mErrors.needDealError())
            {
                // @@������
                this.mErrors.copyAllErrors(tLCUWSubDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "UWManuNormChkBL";
                tError.functionName = "prepareAllUW";
                tError.errorMessage = "LCUWSub��ȡ��ʧ��!";
                this.mErrors.addOneError(tError);
                return false;
            }

            int m = tLCUWSubSet.size();
            System.out.println("subcount=" + m);
            if (m > 0)
            {
                m++; //�˱�����
                tLCUWSubSchema = new LCUWSubSchema();
                tLCUWSubSchema.setUWNo(m); //�ڼ��κ˱�
                tLCUWSubSchema.setProposalNo(tLCUWMasterSchema.getProposalNo());
                tLCUWSubSchema.setContNo(tLCUWMasterSchema.getContNo());
                tLCUWSubSchema.setGrpContNo(tLCUWMasterSchema.getGrpContNo());
                tLCUWSubSchema.setProposalContNo(tLCUWMasterSchema.
                        getProposalContNo());
                tLCUWSubSchema.setPolNo(tLCUWMasterSchema.getPolNo());
                tLCUWSubSchema.setOperator(tLCUWMasterSchema.getOperator());
                tLCUWSubSchema.setProposalNo(tLCUWMasterSchema.getProposalNo());

                tLCUWSubSchema.setPassFlag(mUWFlag); //�˱����
                tLCUWSubSchema.setUWGrade(mUWPopedom); //�˱�����
                tLCUWSubSchema.setAppGrade(mAppGrade); //���뼶��
                tLCUWSubSchema.setAutoUWFlag("2");
                tLCUWSubSchema.setState(mUWFlag);
                tLCUWSubSchema.setOperator(mOperater); //����Ա

                tLCUWSubSchema.setManageCom(tLCUWMasterSchema.getManageCom());
                tLCUWSubSchema.setMakeDate(PubFun.getCurrentDate());
                tLCUWSubSchema.setMakeTime(PubFun.getCurrentTime());
                tLCUWSubSchema.setModifyDate(PubFun.getCurrentDate());
                tLCUWSubSchema.setModifyTime(PubFun.getCurrentTime());

            }
            else
            {
                // @@������
                this.mErrors.copyAllErrors(tLCUWSubDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "UWManuNormChkBL";
                tError.functionName = "prepareAllUW";
                tError.errorMessage = "LCUWSub��ȡ��ʧ��!";
                this.mErrors.addOneError(tError);
                return false;
            }
            mLCUWSubSet.add(tLCUWSubSchema);
        }
        return true;
    }


    /**
     * ׼�������պ˱���Ϣ
     * @return boolean
     */
    private boolean prepareContUW()
    {
        mLCCUWMasterSet.clear();
        mLCCUWSubSet.clear();

        LCCUWMasterSchema tLCCUWMasterSchema = new LCCUWMasterSchema();
        LCCUWMasterDB tLCCUWMasterDB = new LCCUWMasterDB();
        tLCCUWMasterDB.setContNo(mLCContSchema.getContNo());
        LCCUWMasterSet tLCCUWMasterSet = new LCCUWMasterSet();
        tLCCUWMasterSet = tLCCUWMasterDB.query();
        if (tLCCUWMasterDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLCCUWMasterDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWManuNormChkBL";
            tError.functionName = "prepareAllUW";
            tError.errorMessage = "LCCUWMaster��ȡ��ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        int n = tLCCUWMasterSet.size();
        System.out.println("��Ͷ�����ĺ˱�����ǰ��¼����:  " + n);
        if (n == 1)
        {
            tLCCUWMasterSchema = tLCCUWMasterSet.get(1);

            //Ϊ�˱��������˱���˱�����ͺ˱���
            mBackUWGrade = tLCCUWMasterSchema.getUWGrade();
            mBackAppGrade = tLCCUWMasterSchema.getAppGrade();
            mOperator = tLCCUWMasterSchema.getOperator();

            //tLCCUWMasterSchema.setUWNo(tLCCUWMasterSchema.getUWNo()+1);�˱������е�UWNo��ʾ��Ͷ�������������˹��˱�(�ȼ��ھ��������Զ��˱�����),�������˹��˱�����(�����˱�֪ͨ��,�ϱ���)�¹�����.���Խ���ע��.sxy-2003-09-19
            tLCCUWMasterSchema.setPassFlag(mUWFlag); //ͨ����־
            tLCCUWMasterSchema.setState(mUWFlag);
            tLCCUWMasterSchema.setAutoUWFlag("2"); // 1 �Զ��˱� 2 �˹��˱�
            tLCCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
            tLCCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());

            //�ָ��˱�����ͺ˱�Ա
            tLCCUWMasterSchema.setUWGrade(mBackUWGrade);
            tLCCUWMasterSchema.setAppGrade(mBackAppGrade);
            tLCCUWMasterSchema.setOperator(mOperator);
            //����
            LDSysTraceSchema tLDSysTraceSchema = new LDSysTraceSchema();
            tLDSysTraceSchema.setPolNo(mContNo);
            tLDSysTraceSchema.setCreatePos("�˹��˱�");
            tLDSysTraceSchema.setPolState("1001");
            LDSysTraceSet inLDSysTraceSet = new LDSysTraceSet();
            inLDSysTraceSet.add(tLDSysTraceSchema);

            VData tVData = new VData();
            tVData.add(mGlobalInput);
            tVData.add(inLDSysTraceSet);

            LockTableBL LockTableBL1 = new LockTableBL();
            if (!LockTableBL1.submitData(tVData, "DELETE"))
            {
                System.out.println("����ʧ�ܣ�");
            }

        }
        else
        {
            // @@������
            this.mErrors.copyAllErrors(tLCCUWMasterDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWManuNormChkBL";
            tError.functionName = "prepareAllUW";
            tError.errorMessage = "LCCUWMaster��ȡ���ݲ�Ψһ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCCUWMasterSet.add(tLCCUWMasterSchema);

        // �˱��켣��
        LCCUWSubSchema tLCCUWSubSchema = new LCCUWSubSchema();
        LCCUWSubDB tLCCUWSubDB = new LCCUWSubDB();
        tLCCUWSubDB.setContNo(mLCContSchema.getContNo());
        LCCUWSubSet tLCCUWSubSet = new LCCUWSubSet();
        tLCCUWSubSet = tLCCUWSubDB.query();
        if (tLCCUWSubDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLCCUWSubDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWManuNormChkBL";
            tError.functionName = "prepareAllUW";
            tError.errorMessage = "LCCUWSub��ȡ��ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        int m = tLCCUWSubSet.size();
        System.out.println("subcount=" + m);
        if (m > 0)
        {
            m++; //�˱�����
            tLCCUWSubSchema = new LCCUWSubSchema();
            tLCCUWSubSchema.setUWNo(m); //�ڼ��κ˱�
            tLCCUWSubSchema.setContNo(tLCCUWMasterSchema.getContNo());
            tLCCUWSubSchema.setGrpContNo(tLCCUWMasterSchema.getGrpContNo());
            tLCCUWSubSchema.setProposalContNo(tLCCUWMasterSchema.getProposalContNo());
            tLCCUWSubSchema.setOperator(mOperater);

            tLCCUWSubSchema.setPassFlag(mUWFlag); //�˱����
            tLCCUWSubSchema.setUWGrade(mUWPopedom); //�˱�����
            tLCCUWSubSchema.setAppGrade(mAppGrade); //���뼶��
            tLCCUWSubSchema.setAutoUWFlag("2");
            tLCCUWSubSchema.setState(mUWFlag);
            tLCCUWSubSchema.setOperator(mOperater); //����Ա

            tLCCUWSubSchema.setManageCom(tLCCUWMasterSchema.getManageCom());
            tLCCUWSubSchema.setMakeDate(PubFun.getCurrentDate());
            tLCCUWSubSchema.setMakeTime(PubFun.getCurrentTime());
            tLCCUWSubSchema.setModifyDate(PubFun.getCurrentDate());
            tLCCUWSubSchema.setModifyTime(PubFun.getCurrentTime());
        }
        else
        {
            // @@������
            this.mErrors.copyAllErrors(tLCCUWSubDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWManuNormChkBL";
            tError.functionName = "prepareAllUW";
            tError.errorMessage = "LCCUWSub��ȡ��ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCCUWSubSet.add(tLCCUWSubSchema);

        return true;
    }


    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return boolean
     */
    private boolean prepareTransferData()
    {
        LAAgentDB tLAAgentDB = new LAAgentDB();
        LAAgentSet tLAAgentSet = new LAAgentSet();
        tLAAgentDB.setAgentCode(mLCContSchema.getAgentCode());
        tLAAgentSet = tLAAgentDB.query();
        if (tLAAgentSet == null || tLAAgentSet.size() != 1)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "ReManuUWAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "�����˱�LAAgent��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLAAgentSet.get(1).getAgentGroup() == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "ReManuUWAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "�����˱�LAAgent�еĴ���������ݶ�ʧ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        LABranchGroupDB tLABranchGroupDB = new LABranchGroupDB();
        LABranchGroupSet tLABranchGroupSet = new LABranchGroupSet();
        tLABranchGroupDB.setAgentGroup(tLAAgentSet.get(1).getAgentGroup());
        tLABranchGroupSet = tLABranchGroupDB.query();
        if (tLABranchGroupSet == null || tLABranchGroupSet.size() != 1)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "ReManuUWAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "������չҵ������LABranchGroup��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLABranchGroupSet.get(1).getBranchAttr() == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "ReManuUWAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "������չҵ������LABranchGroup��չҵ������Ϣ��ʧ!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mTransferData.setNameAndValue("AgentCode", mLCContSchema.getAgentCode());
//        System.out.println("agentcode==" + mLCContSchema.getAgentCode());
//        mTransferData.setNameAndValue("ManageCom", mManageCom);
        mTransferData.setNameAndValue("ManageCom", mLCContSchema.getManageCom());
//        System.out.println("manageCom=" + mManageCom);
        mTransferData.setNameAndValue("PrtNo", mLCContSchema.getPrtNo());
//        System.out.println("prtNo==" + mLCContSchema.getPrtNo());
        mTransferData.setNameAndValue("ContNo", mLCContSchema.getContNo());
//        System.out.println("ContNo==" + mLCContSchema.getContNo());
        mTransferData.setNameAndValue("AgentGroup", tLAAgentSet.get(1).getAgentGroup());
        mTransferData.setNameAndValue("AgentName", tLAAgentSet.get(1).getName());
        mTransferData.setNameAndValue("AppntCode", mLCContSchema.getAppntNo());
//        System.out.println("AppntName = " + mLCContSchema.getAppntName());
        mTransferData.setNameAndValue("AppntName", mLCContSchema.getAppntName());
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

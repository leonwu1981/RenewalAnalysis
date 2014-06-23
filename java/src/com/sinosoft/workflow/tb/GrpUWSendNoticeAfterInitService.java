/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import java.util.Date;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;


/**
 * <p>Title: �������ڵ�����:����Լ�ŵ����˱�֪ͨ��</p>
 * <p>Description: ���˱�֪ͨ�鹤����AfterInit������</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author Yuanaq
 * @version 1.0--��ʱֻ����˱��ӷѵ���Ϣ
 */

public class GrpUWSendNoticeAfterInitService implements AfterInitService
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();


    /** �����洫�����ݵ����� */
    private VData mResult = new VData();


    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();
   /**�����ͬ��*/
   LCGrpContSchema mLCGrpContSchema=new LCGrpContSchema();

    /** �˱����� */
    private LCCUWMasterSet mLCCUWMasterSet = new LCCUWMasterSet();
    private LCUWMasterSet mLCUWMasterSet = new LCUWMasterSet();
    private LCUWMasterSet GmLCUWMasterSet = new LCUWMasterSet();

    /** �˱��ӱ� */
    private LCUWSubSet mLCUWSubSet = new LCUWSubSet();
    private LCUWSubSet GmLCUWSubSet= new LCUWSubSet();
    private LCCUWSubSet mLCCUWSubSet = new LCCUWSubSet();
    private LCCUWSubSet GmLCCUWSubSet = new LCCUWSubSet();

    /** ��ӡ����� */
    private LOPRTManagerSet mLOPRTManagerSet = new LOPRTManagerSet();
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();
    private LCIssuePolSet mLCIssuePolSet = new LCIssuePolSet();


    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;


    /** ҵ�����ݲ����ַ��� */
    private String mGrpContNo;
    private String mMissionID;
    private String mPrtSeq;
    private String mPrtSeqUW = "0";
    private String mPrtSeqOper = "0";


    /**������*/
     private LCContSet mLCContSet = new LCContSet();
    private LCPolSet GmLCPolSet = new LCPolSet();
    private String mUWFlag = ""; //�˱���־


    /**������Ťת��־*/
    private String mUWSendFlag = "";
    private String mSendOperFlag = "";
    private String mQuesOrgFlag = "";
    private String mApproveModifyFlag = "";

    public GrpUWSendNoticeAfterInitService()
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

        //���Ƿ��ܷ��˱�֪ͨ�����У��
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

        if (!prepareTransferData())
        {
            return false;
        }
        return true;
    }


    /**
     * checkData
     *
     * @return boolean
     */
    private boolean checkData()
    {
        int flag = 0;

        LCGCUWMasterDB tLCGCUWMasterDB = new LCGCUWMasterDB();
        LCGCUWMasterSchema tLCGCUWMasterSchema = new LCGCUWMasterSchema();
        tLCGCUWMasterDB.setProposalGrpContNo(mGrpContNo);
        if (!tLCGCUWMasterDB.getInfo())
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpUWSendNoticeAfterEndService";
            tError.functionName = "checkData";
            tError.errorMessage = "��ѯ�˱�����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;

        }
        tLCGCUWMasterSchema = tLCGCUWMasterDB.getSchema();
/*********��ʱֻ����ӷ�
        //û�гб��ƻ���������ܷ��˱�֪ͨ��
        if (tLCGCUWMasterSchema.getChangePolFlag() != null &&
                tLCGCUWMasterSchema.getChangePolFlag().length() > 0 &&
                tLCGCUWMasterSchema.getChangePolFlag().equals("1"))
        {
            flag = 1;
        }

        //û����Լ�����ܷ��˱�֪ͨ��
        LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
        tLCUWMasterDB.setContNo(mContNo);
        LCUWMasterSet tLCUWMasterSet = tLCUWMasterDB.query();
        if (tLCUWMasterSet == null || tLCUWMasterSet.size() <= 0)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpUWSendNoticeAfterEndService";
            tError.functionName = "checkData";
            tError.errorMessage = "��ѯ���ֵ��˱�����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        for (int i = 1; i <= tLCUWMasterSet.size(); i++)
        {
            if (tLCUWMasterSet.get(i).getSpecFlag() != null &&
                    tLCUWMasterSet.get(i).getSpecFlag().length() > 0 &&
                    tLCUWMasterSet.get(i).getSpecFlag().equals("1"))
            {
                flag = 1;
            }
        }
*/
        //û�мӷѣ����ܷ��˱�֪ͨ��
        if (tLCGCUWMasterSchema.getAddPremFlag() != null &&
                tLCGCUWMasterSchema.getAddPremFlag().length() > 0 &&
                tLCGCUWMasterSchema.getAddPremFlag().equals("1"))
        {
            flag = 1;
        }

        if (flag == 0)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpUWSendNoticeAfterEndService";
            tError.functionName = "checkData";
            //tError.errorMessage = "û��¼���������û�гб��ƻ������û����Լ��û�мӷѣ����ܷ��˱�֪ͨ��!";
            tError.errorMessage = "û�мӷѣ����ܷ��˱�֪ͨ��!";
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
            tError.moduleName = "GrpUWSendNoticeAfterEndService";
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
            tError.moduleName = "GrpUWSendNoticeAfterEndService";
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
            tError.moduleName = "GrpUWSendNoticeAfterEndService";
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
            tError.moduleName = "GrpUWSendNoticeAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mGrpContNo = (String) mTransferData.getValueByName("GrpContNo");
        if (mGrpContNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpUWSendNoticeAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������GrpContNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        LCGrpContDB tLCGrpContDB=new LCGrpContDB();
        tLCGrpContDB.setGrpContNo(mGrpContNo);
        if(!tLCGrpContDB.getInfo())
        {
          CError tError = new CError();
          tError.moduleName = "GrpUWSendNoticeAfterEndService";
          tError.functionName = "getInputData";
          tError.errorMessage = "û�в鵽��ؼ����ͬ!";
          this.mErrors.addOneError(tError);
          return false;
        }
        else
        {
          mLCGrpContSchema = tLCGrpContDB.getSchema();
        }
        //���ҵ��˱�֪ͨ����

        //��õ�ǰ�������������ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpUWSendNoticeAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
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
      StringBuffer tSBql = null;
      tSBql = new StringBuffer(128);
      tSBql.append(" select * from lccont where grpcontno='");
      tSBql.append(mGrpContNo);
      tSBql.append("' and contno in ");
      tSBql.append(" (select contno from LCCUWMaster where ");
      tSBql.append(" AddPremFlag='1' )");
       LCContDB tLCContDB=new LCContDB();
       LCContSet tLCContSet=new LCContSet();
       tLCContSet=tLCContDB.executeQuery(tSBql.toString());
       if(tLCContSet.size()<=0)
       {
        CError tError = new CError();
        tError.moduleName = "GrpUWSendNoticeAfterEndService";
        tError.functionName = "dealData";
        tError.errorMessage = "��ѯ������ͬʧ��!";
        this.mErrors.addOneError(tError);
        return false;

       }
       else
       {
         for (int i = 1; i <= tLCContSet.size(); i++) {
           String mContNo = tLCContSet.get(i).getContNo();
           //׼��������Ϣ
           if (!prepareCont(mContNo)) {
             return false;
           }
           //׼����ͬ�˱�����Ϣ
           if (!prepareContUW(mContNo)) {
             return false;
           }
           //׼�����ֺ˱�����Ϣ
           if (!preparePolUW(mContNo)) {
             return false;
           }
           /*
           //ѡ��������ת�
           if (!chooseActivity(mContNo)) {
             return false;
           }
           */
           //if (mUWSendFlag.equals("1") || mSendOperFlag.equals("1")) {
             //׼����ӡ�������Ϣ
             if (!preparePrt(mContNo)) {
               return false;
             }
             /*
             if (!prepareIssue(mContNo)) {
               return false;
             }
             */
         }
       }
        return true;
    }


    /**
     * prepareIssue
     *  ׼��lcissuepol�����ݣ�����prtseq
     * @return boolean
     */
    private boolean prepareIssue(String mContNo)
    {
        LCIssuePolSet tLCIssuePolSet = new LCIssuePolSet();
        LCIssuePolDB tLCIssuePolDB = new LCIssuePolDB();
        LCIssuePolSchema tLCIssuePolSchema = new LCIssuePolSchema();
        tLCIssuePolDB.setContNo(mContNo);
        tLCIssuePolSet = tLCIssuePolDB.query();

        for (int i = 1; i <= tLCIssuePolSet.size(); i++)
        {
            tLCIssuePolSchema = new LCIssuePolSchema();
            tLCIssuePolSchema = tLCIssuePolSet.get(i);
            if (tLCIssuePolSchema.getPrtSeq().equals("") || tLCIssuePolSchema.getPrtSeq() == null)
            {
                if (tLCIssuePolSchema.getBackObjType().equals("2"))
                {
                    tLCIssuePolSchema.setPrtSeq(mPrtSeqOper);
                }
                else
                {
                    tLCIssuePolSchema.setPrtSeq(mPrtSeqUW);
                }
            }
            mLCIssuePolSet.add(tLCIssuePolSchema);
        }

        return true;
    }


    /**
     * preparePrt
     *  ׼����ͬ������,����UWFlag=8,��ʾ���ɺ˱�֪ͨ��
     * @return boolean
     */
    private boolean prepareCont(String mContNo)
    {
       LCContSchema mLCContSchema = new LCContSchema();
        //׼����ͬ������
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mContNo);
        if (!tLCContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "GrpUWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "��ͬ" + mContNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);
        mUWFlag = "8"; //�˱�������־
        //׼�������ĸ��˱�־
        mLCContSchema.setUWFlag(mUWFlag);
        mLCContSet.add(mLCContSchema);
        //׼�����ֺ�ͬ������
        LCPolSet mLCPolSet = new LCPolSet();
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setContNo(mLCContSchema.getContNo());
        mLCPolSet = tLCPolDB.query();

        //׼�����ֱ����ĸ��˱�־
        for (int i = 1; i < mLCPolSet.size(); i++)
        {
            mLCPolSet.get(i).setUWFlag(mUWFlag);
        }
        GmLCPolSet.add(mLCPolSet);
        return true;
    }


    /**
     * preparePrt
     *  ׼���˱���������
     * @return boolean
     */
    private boolean prepareContUW(String mContNo)
    {
        mLCCUWSubSet.clear();
        LCCUWMasterSchema mLCCUWMasterSchema = new LCCUWMasterSchema();
        LCCUWMasterDB tLCCUWMasterDB = new LCCUWMasterDB();
        tLCCUWMasterDB.setContNo(mContNo);
        if (!tLCCUWMasterDB.getInfo())
        {
            // @@������
            this.mErrors.copyAllErrors(tLCCUWMasterDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWSendNoticeAfterInitService";
            tError.functionName = "prepareContUW";
            tError.errorMessage = "LCCUWMaster��ȡ��ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCCUWMasterSchema.setSchema(tLCCUWMasterDB);
        mLCCUWMasterSchema.setPassFlag(mUWFlag);
        mLCCUWMasterSet.add(mLCCUWMasterSchema);
        //ÿ�ν��к˱���ز���ʱ����˱��켣���һ������
        LCCUWSubSchema tLCCUWSubSchema = new LCCUWSubSchema();
        LCCUWSubDB tLCCUWSubDB = new LCCUWSubDB();
        tLCCUWSubDB.setContNo(mContNo);
        LCCUWSubSet tLCCUWSubSet = new LCCUWSubSet();
        tLCCUWSubSet = tLCCUWSubDB.query();

        if (tLCCUWSubDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLCCUWSubDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWSendNoticeAfterInitService";
            tError.functionName = "prepareContUW";
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
            tLCCUWSubSchema.setContNo(mContNo);
            tLCCUWSubSchema.setGrpContNo(mLCCUWMasterSchema.getGrpContNo());
            tLCCUWSubSchema.setProposalContNo(mLCCUWMasterSchema.
                    getProposalContNo());
            tLCCUWSubSchema.setPassFlag(mUWFlag); //�˱����
            tLCCUWSubSchema.setPrintFlag("1");
            tLCCUWSubSchema.setAutoUWFlag(mLCCUWMasterSchema.getAutoUWFlag());
            tLCCUWSubSchema.setState(mLCCUWMasterSchema.getState());
            tLCCUWSubSchema.setOperator(mLCCUWMasterSchema.getOperator()); //����Ա
            tLCCUWSubSchema.setManageCom(mLCCUWMasterSchema.getManageCom());
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
            tError.moduleName = "UWSendNoticeAfterInitService";
            tError.functionName = "prepareAllUW";
            tError.errorMessage = "LCCUWSub��ȡ��ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        GmLCCUWSubSet.add(tLCCUWSubSchema);
        return true;
    }


    /**
     * ׼�����ֺ˱�����Ϣ
     * �����������������򷵻�false,���򷵻�true
     */
    private boolean preparePolUW(String mContNo)
    {
        mLCUWMasterSet.clear();
        mLCUWSubSet.clear();
        LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
        LCPolSchema tLCPolSchema = new LCPolSchema();
        LCUWMasterSchema tLCUWMasterSchema = new LCUWMasterSchema();
        LCUWSubSchema tLCUWSubSchema = new LCUWSubSchema();
        LCUWSubDB tLCUWSubDB = new LCUWSubDB();
        LCUWSubSet tLCUWSubSet = new LCUWSubSet();
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setContNo(mContNo);
        LCPolSet mLCPolSet = new LCPolSet();
        mLCPolSet = tLCPolDB.query();
        for (int i = 1; i <= mLCPolSet.size(); i++)
        {
            tLCUWMasterSchema = new LCUWMasterSchema();
            tLCUWMasterDB = new LCUWMasterDB();
            tLCPolSchema = mLCPolSet.get(i);
            tLCUWMasterDB.setProposalNo(tLCPolSchema.getProposalNo());

            if (!tLCUWMasterDB.getInfo())
            {
                // @@������
                this.mErrors.copyAllErrors(tLCUWMasterDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "GrpUWSendNoticeAfterInitService";
                tError.functionName = "prepareAllUW";
                tError.errorMessage = "LCUWMaster��ȡ��ʧ��!";
                this.mErrors.addOneError(tError);
                return false;
            }
            tLCUWMasterSchema.setSchema(tLCUWMasterDB);

            //tLCUWMasterSchema.setUWNo(tLCUWMasterSchema.getUWNo()+1);�˱������е�UWNo��ʾ��Ͷ�������������˹��˱�(�ȼ��ھ��������Զ��˱�����),�������˹��˱�����(�����˱�֪ͨ��,�ϱ���)�¹�����.���Խ���ע��.sxy-2003-09-19
            tLCUWMasterSchema.setPassFlag(mUWFlag); //ͨ����־
            tLCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
            tLCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());

            mLCUWMasterSet.add(tLCUWMasterSchema);

            // �˱��켣��
            tLCUWSubSchema = new LCUWSubSchema();
            tLCUWSubDB = new LCUWSubDB();
            tLCUWSubSet = new LCUWSubSet();
            tLCUWSubDB.setContNo(tLCPolSchema.getContNo());
            tLCUWSubDB.setPolNo(tLCPolSchema.getPolNo());
            tLCUWSubSet = tLCUWSubDB.query();
            if (tLCUWSubDB.mErrors.needDealError())
            {
                // @@������
                this.mErrors.copyAllErrors(tLCUWSubDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "GrpUWSendNoticeAfterInitService";
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
                tLCUWSubSchema.setGrpContNo(tLCUWMasterSchema.getGrpContNo());
                tLCUWSubSchema.setProposalNo(tLCUWMasterSchema.getProposalNo());
                tLCUWSubSchema.setContNo(tLCUWMasterSchema.getContNo());
                tLCUWSubSchema.setProposalContNo(tLCUWMasterSchema.
                        getProposalContNo());
                tLCUWSubSchema.setPolNo(tLCUWMasterSchema.getPolNo());
                tLCUWSubSchema.setPassFlag(mUWFlag); //�˱����
                tLCUWSubSchema.setUWGrade(tLCUWMasterSchema.getUWGrade()); //�˱�����
                tLCUWSubSchema.setAppGrade(tLCUWMasterSchema.getAppGrade()); //���뼶��
                tLCUWSubSchema.setAutoUWFlag(tLCUWMasterSchema.getAutoUWFlag());
                tLCUWSubSchema.setPrintFlag("1");
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
                tError.moduleName = "GrpUWSendNoticeAfterInitService";
                tError.functionName = "prepareAllUW";
                tError.errorMessage = "LCUWSub��ȡ��ʧ��!";
                this.mErrors.addOneError(tError);
                return false;
            }
            mLCUWSubSet.add(tLCUWSubSchema);
        }
        GmLCUWMasterSet.add(mLCUWMasterSet);
        GmLCUWSubSet.add(mLCUWSubSet);
        return true;
    }


    /**
     * preparePrt
     *  ׼����ӡ��
     * @return boolean
    private boolean chooseActivity(String mContNo)
    {
        String tsql = "";
        LCIssuePolDB tLCIssuePolDB = new LCIssuePolDB();
        LCIssuePolSet tLCIssuePolSet = new LCIssuePolSet();

        mUWSendFlag = "0"; //��ӡ�˱�֪ͨ���־
        mSendOperFlag = "0"; //��ӡҵ��Ա֪ͨ���־
        mQuesOrgFlag = "0"; //�����������־
        mApproveModifyFlag = "0"; //�����޸ı�־

        for (int i = 1; i <= mLCUWMasterSet.size(); i++)
        {
            if (mLCUWMasterSet.get(i).getAddPremFlag() != null &&
                    mLCUWMasterSet.get(i).getAddPremFlag().equals("1"))
            {
                mUWSendFlag = "1";
            }
            if (mLCUWMasterSet.get(i).getSpecFlag() != null &&
                    mLCUWMasterSet.get(i).getSpecFlag().equals("1"))
            {
                mUWSendFlag = "1";
            }
        }
        if (mLCCUWMasterSchema.getChangePolFlag() != null &&
                mLCCUWMasterSchema.getChangePolFlag().equals("1"))
        {
            mUWSendFlag = "1";
        }

        tsql = "select * from lcissuepol where Contno = '" + mContNo
                + "' and backobjtype = '3' and replyresult is null and needprint = 'Y'";
        System.out.println("tSql==" + tsql);
        tLCIssuePolSet = tLCIssuePolDB.executeQuery(tsql);
        if (tLCIssuePolSet.size() > 0)
        {
            mUWSendFlag = "1";
        }
        tsql = "select * from lcissuepol where Contno = '" + mContNo
                + "' and backobjtype = '2' and prtseq is null";
        tLCIssuePolSet.clear();
        System.out.println("tSql==" + tsql);
        tLCIssuePolSet = tLCIssuePolDB.executeQuery(tsql);
        if (tLCIssuePolSet.size() > 0)
        {
            mSendOperFlag = "1";
        }
        tsql = "select * from lcissuepol where Contno = '" + mContNo
                + "' and backobjtype = '4' and replyresult is null";
        tLCIssuePolSet.clear();
        System.out.println("tSql==" + tsql);
        tLCIssuePolSet = tLCIssuePolDB.executeQuery(tsql);
        if (tLCIssuePolSet.size() > 0)
        {
            mQuesOrgFlag = "1";
        }
        tsql = "select * from lcissuepol where Contno = '" + mContNo
                + "' and backobjtype = '1' ";

        System.out.println("tSql==" + tsql);
        tLCIssuePolSet.clear();
        tLCIssuePolSet = tLCIssuePolDB.executeQuery(tsql);
        if (tLCIssuePolSet.size() > 0 && mSendOperFlag.equals("0") &&
                mUWSendFlag.equals("0") && mQuesOrgFlag.equals("0"))
        {
            mApproveModifyFlag = "1";
        }

        return true;
    }
     */

    /**
     * preparePrt
     *  ׼����ӡ��
     * @return boolean
     */
    private boolean preparePrt(String mContNo)
    {
        // ����δ��ӡ״̬��֪ͨ���ڴ�ӡ������ֻ����һ��
        // ������ͬһ���������ͣ�ͬһ���������룬ͬһ��������������
        //���ɸ���֪ͨ���

        LDSysVarDB tLDSysVarDB = new LDSysVarDB();
        tLDSysVarDB.setSysVar("URGEInterval");

        if (!tLDSysVarDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "UWSendPrintBL";
            tError.functionName = "prepareURGE";
            tError.errorMessage = "û�������߷����!";
            this.mErrors.addOneError(tError);
            return false;
        }
        FDate tFDate = new FDate();
        int tInterval = Integer.parseInt(tLDSysVarDB.getSysVarValue());
        Date tDate = PubFun.calDate(tFDate.getDate(PubFun.getCurrentDate()),
                tInterval, "D", null);
       // if (mUWSendFlag.equals("1"))
        //{
            String tLimit = PubFun.getNoLimit(mManageCom);
            mPrtSeqUW = PubFun1.CreateMaxNo("PRTSEQNO", tLimit);
            System.out.println("---tLimit---" + tLimit);

            //׼����ӡ���������
            mLOPRTManagerSchema.setPrtSeq(mPrtSeqUW);
            mLOPRTManagerSchema.setOtherNo(mContNo);
            mLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_CONT); //������
            mLOPRTManagerSchema.setCode(PrintManagerBL.CODE_GRP_UW); //�˱�
            mLOPRTManagerSchema.setManageCom(mLCGrpContSchema.getManageCom());
            mLOPRTManagerSchema.setAgentCode(mLCGrpContSchema.getAgentCode());
            mLOPRTManagerSchema.setReqCom(mGlobalInput.ComCode);
            mLOPRTManagerSchema.setReqOperator(mGlobalInput.Operator);
            //mLOPRTManagerSchema.setExeCom();
            //mLOPRTManagerSchema.setExeOperator();
            mLOPRTManagerSchema.setPrtType(PrintManagerBL.PT_FRONT); //ǰ̨��ӡ
            mLOPRTManagerSchema.setStateFlag("0");
            mLOPRTManagerSchema.setPatchFlag("0");
            mLOPRTManagerSchema.setMakeDate(PubFun.getCurrentDate());
            mLOPRTManagerSchema.setMakeTime(PubFun.getCurrentTime());
            //mLOPRTManagerSchema.setDoneDate() ;
            //mLOPRTManagerSchema.setDoneTime();
            mLOPRTManagerSchema.setStandbyFlag1(mLCGrpContSchema.getAppntNo()); //Ͷ���˱���
            mLOPRTManagerSchema.setStandbyFlag3(mMissionID);
            mLOPRTManagerSchema.setOldPrtSeq(mPrtSeq);
            mLOPRTManagerSchema.setForMakeDate(tDate);

            mLOPRTManagerSet.add(mLOPRTManagerSchema);
        //}
        if (mSendOperFlag.equals("1"))
        {
            tLimit = PubFun.getNoLimit(mManageCom);
            mPrtSeqOper = PubFun1.CreateMaxNo("PRTSEQNO", tLimit);
            //׼����ӡ���������
            mLOPRTManagerSchema.setPrtSeq(mPrtSeqOper);
            mLOPRTManagerSchema.setOtherNo(mContNo);
            mLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_CONT); //������
            mLOPRTManagerSchema.setCode(PrintManagerBL.CODE_AGEN_QUEST); //ҵ��Ա֪ͨ��
            mLOPRTManagerSchema.setManageCom(mLCGrpContSchema.getManageCom());
            mLOPRTManagerSchema.setAgentCode(mLCGrpContSchema.getAgentCode());
            mLOPRTManagerSchema.setReqCom(mGlobalInput.ComCode);
            mLOPRTManagerSchema.setReqOperator(mGlobalInput.Operator);
            //mLOPRTManagerSchema.setExeCom();
            //mLOPRTManagerSchema.setExeOperator();
            mLOPRTManagerSchema.setPrtType(PrintManagerBL.PT_FRONT); //ǰ̨��ӡ
            mLOPRTManagerSchema.setStateFlag("0");
            mLOPRTManagerSchema.setPatchFlag("0");
            mLOPRTManagerSchema.setMakeDate(PubFun.getCurrentDate());
            mLOPRTManagerSchema.setMakeTime(PubFun.getCurrentTime());
            //mLOPRTManagerSchema.setDoneDate() ;
            //mLOPRTManagerSchema.setDoneTime();
            mLOPRTManagerSchema.setStandbyFlag1(mLCGrpContSchema.getAppntNo()); //Ͷ���˱���
            mLOPRTManagerSchema.setStandbyFlag3(mMissionID);
            mLOPRTManagerSchema.setOldPrtSeq(mPrtSeq);

            mLOPRTManagerSet.add(mLOPRTManagerSchema);
        }

        return true;
    }


    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return
     */
    private boolean prepareTransferData()
    {
        mTransferData.setNameAndValue("SendOperFlag", mSendOperFlag);
        mTransferData.setNameAndValue("UWSendFlag", mUWSendFlag); //��ӡ�˱�֪ͨ���־
        mTransferData.setNameAndValue("QuesOrgFlag", mQuesOrgFlag);
        mTransferData.setNameAndValue("ApproveModifyFlag", mApproveModifyFlag);

        LAAgentDB tLAAgentDB = new LAAgentDB();
        LAAgentSet tLAAgentSet = new LAAgentSet();
        tLAAgentDB.setAgentCode(mLCGrpContSchema.getAgentCode());
        tLAAgentSet = tLAAgentDB.query();
        if (tLAAgentSet == null || tLAAgentSet.size() != 1)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "�����˱�LAAgent��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLAAgentSet.get(1).getAgentGroup() == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
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
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "������չҵ������LABranchGroup��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLABranchGroupSet.get(1).getBranchAttr() == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "������չҵ������LABranchGroup��չҵ������Ϣ��ʧ!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mTransferData.setNameAndValue("AgentCode", mLCGrpContSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup",
                tLAAgentSet.get(1).getAgentGroup());
        mTransferData.setNameAndValue("BranchAttr",
                tLABranchGroupSet.get(1).getBranchAttr());
        mTransferData.setNameAndValue("ManageCom", mLCGrpContSchema.getManageCom());
        mTransferData.setNameAndValue("PrtNo", mLCGrpContSchema.getPrtNo());
        mTransferData.setNameAndValue("PrtSeqUW", mPrtSeqUW);
        mTransferData.setNameAndValue("PrtSeqOper", mPrtSeqOper);
        mTransferData.setNameAndValue("AppntName", mLCGrpContSchema.getGrpName());

        return true;
    }


    /**
     * �����������ύ����
     * @return boolean
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();

        map.put(mLCContSet, "UPDATE");
        map.put(GmLCPolSet, "UPDATE");
        map.put(mLCCUWMasterSet, "UPDATE");
        //map.put(mLCIssuePolSet, "UPDATE");
        map.put(GmLCCUWSubSet, "INSERT");
        map.put(GmLCUWMasterSet, "UPDATE");
        map.put(GmLCUWSubSet, "INSERT");
        map.put(mLOPRTManagerSet, "INSERT");

        mResult.add(map);
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
    public static void main(String[] args)
  {
      VData cInputData = new VData();
      String cOperate = "";
      GlobalInput tGlobalInput = new GlobalInput();
      tGlobalInput.Operator = "001";
      tGlobalInput.ManageCom = "861100";
      tGlobalInput.ComCode = "001";
      cInputData.add(tGlobalInput);
      TransferData tTransferData = new TransferData();
      tTransferData.setNameAndValue("GrpContNo","140110000000951");
      tTransferData.setNameAndValue("PrtNo","20050601006") ;
      tTransferData.setNameAndValue("MissionID","00000000000000007289");
      tTransferData.setNameAndValue("SubMissionID","1");

      cInputData.add(tTransferData);
      GrpUWSendNoticeAfterInitService lGrpUWSendNoticeAfterInitService = new GrpUWSendNoticeAfterInitService();
      boolean actualReturn = lGrpUWSendNoticeAfterInitService.submitData(cInputData, cOperate);
  }

}

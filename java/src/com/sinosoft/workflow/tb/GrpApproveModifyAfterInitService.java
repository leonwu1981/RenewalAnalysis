/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LCGrpContDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.LCGrpContSchema;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title:�����������ࣺ��������Լ�����޸� </p>
 * <p>Description: ���帴���޸Ĺ�����AfterInit������ </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft </p>
 * @author HYQ
 * @version 1.0
 */

public class GrpApproveModifyAfterInitService implements AfterInitService
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** �����洫�����ݵ����� */
    private VData mResult = new VData();

    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();

    private TransferData mTransferData = new TransferData();
    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();

    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;
    private String mMissionID;
    private String mGrpContNo;
    private String mApproveFlag;
    private StringBuffer mGrpContSql = new StringBuffer(128);
    private StringBuffer mContSql = new StringBuffer(128);
    private StringBuffer mPolSql = new StringBuffer(128);
    //  private String tLCGrpPolSql;
    //  private String tLCGrpContSql;

    public GrpApproveModifyAfterInitService()
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

        System.out.println("Start  dealData...");

        //����ҵ����
        if (!dealData())
        {
            return false;
        }

        System.out.println("dealData successful!");

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

        map.put(mGrpContSql.toString(), "UPDATE");
        map.put(mContSql.toString(), "UPDATE");
        map.put(mPolSql.toString(), "UPDATE");

        // ����ͳ�Ƽ����ͬ���������ֵ��ı������������ܱ��ѱ���
//        map.put(tLCGrpPolSql,"UPDATE");
//        map.put(tLCGrpContSql,"UPDATE");

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
        LCGrpContDB tLCGrpContDB = new LCGrpContDB();
        tLCGrpContDB.setGrpContNo(mGrpContNo);
        if (!tLCGrpContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "UWRReportModifyAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "���屣��" + mGrpContNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCGrpContSchema.setSchema(tLCGrpContDB);
//ȷ������Ͷ�������и���Ͷ����
        ExeSQL tExeSQL = new ExeSQL();
        String sql = "select count(*) from LCCont "
                + "where GrpContNo = '" +
                mLCGrpContSchema.getProposalGrpContNo() + "'";

        String tStr = "";
        double tCount = -1;
        tStr = tExeSQL.getOneValue(sql);
        if (tStr.trim().equals(""))
        {
            tCount = 0;
        }
        else
        {
            tCount = Double.parseDouble(tStr);
        }

        if (tCount <= 0.0)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveBL";
            tError.functionName = "dealData";
            tError.errorMessage = "����Ͷ������û�и���Ͷ���������ܽ��и��˲���!";
            this.mErrors.addOneError(tError);
            return false;
        }

//ȷ������Ͷ�������и����޸�ȷ�ϲ���ʱ,�ѽ����в���Ա��������ѻظ�
        sql = "select count(1) from LCGrpIssuePol where ProposalGrpContNo = '"
                + mLCGrpContSchema.getProposalGrpContNo()
                + "' and backobjtype='1' and replyman is null";
        tCount = -1;
        tStr = tExeSQL.getOneValue(sql);
        if (tStr.trim().equals(""))
        {
            tCount = 0;
        }
        else
        {
            tCount = Double.parseDouble(tStr);
        }

        if (tCount >= 1.0)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveBL";
            tError.functionName = "dealData";
            tError.errorMessage = "����Ͷ��������δ�ظ��Ĳ���Ա������������ܽ��и����޸�ȷ�ϲ���!";
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
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpUWModifyAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpUWModifyAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ProposalApproveAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpUWModifyAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ���������GrpContNo
        mGrpContNo = (String) mTransferData.getValueByName("ProposalGrpContNo");
        if (mGrpContNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpUWModifyAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������GrpContNoʧ��!";
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
        mApproveFlag = "0";
        //�޸����ֱ�����
        mPolSql.append("update LCPol set ApproveFlag = '");
        mPolSql.append(mApproveFlag);
        mPolSql.append("',ModifyDate = '");
        mPolSql.append(PubFun.getCurrentDate());
        mPolSql.append("',ModifyTime = '");
        mPolSql.append(PubFun.getCurrentTime());
        mPolSql.append("' where GrpContNo in ( select GrpContNo from lcgrpcont where prtno = '");
        mPolSql.append(mLCGrpContSchema.getPrtNo());
        mPolSql.append(
                "' and appflag = '0' and approveflag = '9') and appflag = '0' and approveflag = '9'");
//        mPolSql = "update LCPol set ApproveFlag = '" + mApproveFlag + "',ModifyDate = '"
//                + PubFun.getCurrentDate() + "',ModifyTime = '" + PubFun.getCurrentTime()
//                + "' where GrpContNo in ( select GrpContNo from lcgrpcont where prtno = '"
//                + mLCGrpContSchema.getPrtNo()
//                +
//                "' and appflag = '0' and approveflag = '9') and appflag = '0' and approveflag = '9'";

        //�޸ĺ�ͬ��
        mContSql.append("update LCCont set ApproveFlag = '");
        mContSql.append(mApproveFlag);
        mContSql.append("',ModifyDate = '");
        mContSql.append(PubFun.getCurrentDate());
        mContSql.append("',ModifyTime = '");
        mContSql.append(PubFun.getCurrentTime());
        mContSql.append("' where GrpContNo in ( select GrpContNo from lcgrpcont where prtno = '");
        mContSql.append(mLCGrpContSchema.getPrtNo());
        mContSql.append(
                "' and appflag = '0' and approveflag = '9') and appflag = '0' and approveflag = '9'");
//        mContSql = "update LCCont set ApproveFlag = '" + mApproveFlag + "',ModifyDate = '"
//                + PubFun.getCurrentDate() + "',ModifyTime = '" + PubFun.getCurrentTime()
//                + "' where GrpContNo in ( select GrpContNo from lcgrpcont where prtno = '"
//                + mLCGrpContSchema.getPrtNo()
//                +
//                "' and appflag = '0' and approveflag = '9') and appflag = '0' and approveflag = '9'";

        //�޸ļ����ͬ��
        mGrpContSql.append("update LCGrpCont set ApproveFlag = '");
        mGrpContSql.append(mApproveFlag);
        mGrpContSql.append("',ModifyDate = '");
        mGrpContSql.append(PubFun.getCurrentDate());
        mGrpContSql.append("',ModifyTime = '");
        mGrpContSql.append(PubFun.getCurrentTime());
        mGrpContSql.append("' where PrtNo = '");
        mGrpContSql.append(mLCGrpContSchema.getPrtNo());
        mGrpContSql.append("' and appflag = '0' and approveflag = '9'");
//        mGrpContSql = "update LCGrpCont set ApproveFlag = '" + mApproveFlag + "',ModifyDate = '"
//                + PubFun.getCurrentDate() + "',ModifyTime = '" + PubFun.getCurrentTime()
//                + "' where PrtNo = '" + mLCGrpContSchema.getPrtNo()
//                + "' and appflag = '0' and approveflag = '9'";

        /**
         * ����ͳ�Ƽ����ͬ���������ֵ��ı������������ܱ��ѱ���
         */
        //�������ֵ� LCGrpPol
        /*                tLCGrpPolSql = "Update LCGrpPol"
                            + " set "
                            + " Peoples2 =( select SUM(b.InsuredPeoples) from LCGrpPol a , LCPol b where a.riskcode=b.riskcode and a.GrpContNo = '" + mGrpContNo + "'),"
                            + " Prem =( select SUM(b.Prem) from LCGrpPol a , LCPol b where a.riskcode=b.riskcode and a.GrpContNo = '" + mGrpContNo + "'),"
                            + " Amnt =( select SUM(b.Amnt) from LCGrpPol a , LCPol b where a.riskcode=b.riskcode and a.GrpContNo = '" + mGrpContNo + "')";

                        //�����ͬ  LCGrpCont
                        tLCGrpContSql = "Update LCGrpCont"
                            + " set "
                            + " Peoples2 =( select SUM(b.Peoples) from LCGrpCont a , LCCont b where a.GrpContNo = '" + mGrpContNo + "'),"
                            + " Prem =( select SUM(b.Prem) from LCGrpCont a , LCCont b where a.GrpContNo = '" + mGrpContNo + "'),"
                            + " Amnt =( select SUM(b.Amnt) from LCGrpCont a , LCCont b where a.GrpContNo = '" + mGrpContNo + "')";
         */
        return true;
    }

    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return
     */
    private boolean prepareTransferData()
    {
        mTransferData.setNameAndValue("GrpContNo", mGrpContNo);
        mTransferData.setNameAndValue("PrtNo", mLCGrpContSchema.getPrtNo());
        mTransferData.setNameAndValue("AgentCode",
                mLCGrpContSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup",
                mLCGrpContSchema.getAgentGroup());
        mTransferData.setNameAndValue("SaleChnl", mLCGrpContSchema.getSaleChnl());
        mTransferData.setNameAndValue("ManageCom",
                mLCGrpContSchema.getManageCom());
        mTransferData.setNameAndValue("GrpNo", mLCGrpContSchema.getAppntNo());
        mTransferData.setNameAndValue("GrpName", mLCGrpContSchema.getGrpName());
        mTransferData.setNameAndValue("CValiDate",
                mLCGrpContSchema.getCValiDate());

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

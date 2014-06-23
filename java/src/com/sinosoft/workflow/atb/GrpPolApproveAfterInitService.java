/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.atb;

import com.sinosoft.lis.db.LCGrpContDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.LCGrpContSchema;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;
import com.sinosoft.lis.db.LCGrpPolDB;
import com.sinosoft.lis.vschema.LCGrpPolSet;

/**
 * <p>Title: �������ŵ����˷����� </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p> 
 * @version 1.0
 */

public class GrpPolApproveAfterInitService implements AfterInitService
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
    private String mOperator;

    /** ҵ�����ݲ����ַ��� */
    private String mGrpContNo;
    private String mApproveFlag = "9";//ֱ��ͨ��
    private String mMissionID;
    private String mPolSql;
    private String mContSql;
    private String mGrpContSql;
    private String mGrpContSql2;
    private String mGrpPolSql;

    public GrpPolApproveAfterInitService()
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

        //mResult.clear();
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

        //�޸����ֱ�����
//        mPolSql = "update LCPol set ApproveCode = '" + mLCGrpContSchema.getApproveCode()
//                + "', ApproveDate = '" + mLCGrpContSchema.getApproveDate() + "',ApproveTime = '"
//                + mLCGrpContSchema.getApproveTime() + "',ApproveFlag = '" + mApproveFlag
//                + "',ModifyDate = '" + mLCGrpContSchema.getModifyDate() + "',ModifyTime = '"
//                + mLCGrpContSchema.getModifyTime()
//                + "' where GrpContNo in ( select GrpContNo from lcgrpcont where prtno = '"
//                + mLCGrpContSchema.getPrtNo()
//                +
//                "' and appflag = '0' and approveflag <> '9') and appflag = '0' and approveflag <> '9'";
        int tFlag = 9;//ֱ�Ӹ��ˡ��˱�ͨ��
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("update LCPol set ApproveCode = '");
        tSBql.append(mGlobalInput.Operator);
        tSBql.append("', ApproveDate = '");
        tSBql.append(mLCGrpContSchema.getApproveDate());
        tSBql.append("',ApproveTime = '");
        tSBql.append(mLCGrpContSchema.getApproveTime());
        tSBql.append("',ApproveFlag = '");
        tSBql.append(tFlag);
        tSBql.append("',ModifyDate = '");
        tSBql.append(mLCGrpContSchema.getModifyDate());
        tSBql.append("', ModifyTime = '");
        tSBql.append(mLCGrpContSchema.getModifyTime());
        tSBql.append("', UWCode = '");
        tSBql.append(mGlobalInput.Operator);
        tSBql.append("',UWFlag = '");
        tSBql.append(tFlag);
        tSBql.append("',UWDate = '");
        tSBql.append(mLCGrpContSchema.getApproveDate());
        tSBql.append("',UWTime = '");
        tSBql.append(mLCGrpContSchema.getApproveTime());
        tSBql.append("' where GrpContNo in ( select GrpContNo from lcgrpcont where prtno = '");
        tSBql.append(mLCGrpContSchema.getPrtNo());
        tSBql.append(
                "' and appflag = '0' and approveflag <> '9') and appflag = '0' and approveflag <> '9'");
        mPolSql = tSBql.toString();

        //�޸ĺ�ͬ��
//        mContSql = "update LCCont set ApproveCode = '" + mLCGrpContSchema.getApproveCode()
//                + "',ApproveDate = '" + mLCGrpContSchema.getApproveDate() + "',ApproveTime = '"
//                + mLCGrpContSchema.getApproveTime() + "',ApproveFlag = '" + mApproveFlag
//                + "',ModifyDate = '" + mLCGrpContSchema.getModifyDate() + "',ModifyTime = '"
//                + mLCGrpContSchema.getModifyTime()
//                + "' where GrpContNo in ( select GrpContNo from lcgrpcont where prtno = '"
//                + mLCGrpContSchema.getPrtNo()
//                +
//                "' and appflag = '0' and approveflag <> '9') and appflag = '0' and approveflag <> '9'";
        tSBql = new StringBuffer(128);
        tSBql.append("update LCCont set ApproveCode = '");
        tSBql.append(mGlobalInput.Operator);
        tSBql.append("',ApproveDate = '");
        tSBql.append(mLCGrpContSchema.getApproveDate());
        tSBql.append("',ApproveTime = '");
        tSBql.append(mLCGrpContSchema.getApproveTime());
        tSBql.append("',ApproveFlag = '");
        tSBql.append(tFlag);
        tSBql.append("',ModifyDate = '");
        tSBql.append(mLCGrpContSchema.getModifyDate());
        tSBql.append("',ModifyTime = '");
        tSBql.append(mLCGrpContSchema.getModifyTime());
        tSBql.append("', UWOperator = '");
        tSBql.append(mGlobalInput.Operator);
        tSBql.append("',UWFlag = '");
        tSBql.append(tFlag);
        tSBql.append("',UWDate = '");
        tSBql.append(mLCGrpContSchema.getApproveDate());
        tSBql.append("',UWTime = '");
        tSBql.append(mLCGrpContSchema.getApproveTime());
        tSBql.append("' where GrpContNo in ( select GrpContNo from lcgrpcont where prtno = '");
        tSBql.append(mLCGrpContSchema.getPrtNo());
        tSBql.append(
                "' and appflag = '0' and approveflag <> '9') and appflag = '0' and approveflag <> '9'");
        mContSql = tSBql.toString();

        //�޸ļ����ͬ��
//        mGrpContSql = "update LCGrpCont set ApproveCode = '" + mLCGrpContSchema.getApproveCode()
//                + "',ApproveDate = '" + mLCGrpContSchema.getApproveDate() + "',ApproveTime = '"
//                + mLCGrpContSchema.getApproveTime() + "',ApproveFlag = '" + mApproveFlag
//                + "',ModifyDate = '" + mLCGrpContSchema.getModifyDate() + "',ModifyTime = '"
//                + mLCGrpContSchema.getModifyTime() + "' where PrtNo = '"
//                + mLCGrpContSchema.getPrtNo() + "' and appflag = '0' and approveflag <> '9'";
        tSBql = new StringBuffer(128);
        tSBql.append("update LCGrpCont set ApproveCode = '");
        tSBql.append(mGlobalInput.Operator);
        tSBql.append("',ApproveDate = '");
        tSBql.append(mLCGrpContSchema.getApproveDate());
        tSBql.append("',ApproveTime = '");
        tSBql.append(mLCGrpContSchema.getApproveTime());
        tSBql.append("',ApproveFlag = '");
        tSBql.append(tFlag);
        tSBql.append("',ModifyDate = '");
        tSBql.append(mLCGrpContSchema.getModifyDate());
        tSBql.append("',ModifyTime = '");
        tSBql.append(mLCGrpContSchema.getModifyTime());
        tSBql.append("',Payintv = '");
        tSBql.append(mLCGrpContSchema.getPayIntv());
        tSBql.append("', UWOperator = '");
        tSBql.append(mGlobalInput.Operator);
        tSBql.append("',UWFlag = '");
        tSBql.append(tFlag);
        tSBql.append("',UWDate = '");
        tSBql.append(mLCGrpContSchema.getApproveDate());
        tSBql.append("',UWTime = '");
        tSBql.append(mLCGrpContSchema.getApproveTime());
        tSBql.append("' where PrtNo = '");
        tSBql.append(mLCGrpContSchema.getPrtNo());
        tSBql.append("' and appflag = '0' and approveflag <> '9'");
        mGrpContSql = tSBql.toString();

//        mGrpPolSql = "update LCGrpPol set ApproveCode = '" + mLCGrpContSchema.getApproveCode()
//                + "',ApproveDate = '" + mLCGrpContSchema.getApproveDate() + "',ApproveTime = '"
//                + mLCGrpContSchema.getApproveTime() + "',ApproveFlag = '" + mApproveFlag
//                + "',ModifyDate = '" + mLCGrpContSchema.getModifyDate() + "',ModifyTime = '"
//                + mLCGrpContSchema.getModifyTime() + "' where PrtNo = '"
//                + mLCGrpContSchema.getPrtNo() + "' and appflag = '0' and approveflag <> '9'";
        tSBql = new StringBuffer(128);
        tSBql.append("update LCGrpPol set ApproveCode = '");
        tSBql.append(mGlobalInput.Operator);
        tSBql.append("',ApproveDate = '");
        tSBql.append(mLCGrpContSchema.getApproveDate());
        tSBql.append("',ApproveTime = '");
        tSBql.append(mLCGrpContSchema.getApproveTime());
        tSBql.append("',ApproveFlag = '");
        tSBql.append(tFlag);
        tSBql.append("',ModifyDate = '");
        tSBql.append(mLCGrpContSchema.getModifyDate());
        tSBql.append("',ModifyTime = '");
        tSBql.append(mLCGrpContSchema.getModifyTime());
        tSBql.append("', UWOperator = '");
        tSBql.append(mGlobalInput.Operator);
        tSBql.append("',UWFlag = '");
        tSBql.append(tFlag);
        tSBql.append("',UWDate = '");
        tSBql.append(mLCGrpContSchema.getApproveDate());
        tSBql.append("',UWTime = '");
        tSBql.append(mLCGrpContSchema.getApproveTime());
        tSBql.append("' where PrtNo = '");
        tSBql.append(mLCGrpContSchema.getPrtNo());
        tSBql.append("' and appflag = '0' and approveflag <> '9'");
        mGrpPolSql = tSBql.toString();

        /**
         * �ŵ������޸ĺ󣬱����˸��ڸ��˴����룬�µ�¼�����ʱLCGrpCont���peoples�ֶα��ÿգ�������Ϻ��轫���ֶθ���
         * @return boolean
         */
             tSBql = new StringBuffer(128);
             tSBql.append(
                     "update lcgrpcont set prem = (select sum(prem) from lccont where grpcontno = '");
             tSBql.append(mGrpContNo);
             tSBql.append("'),amnt=(select sum(amnt) from lccont where grpcontno = '");
             tSBql.append(mGrpContNo);
             tSBql.append("'),Peoples = (select sum(Peoples) from lccont where grpcontno = '");
             tSBql.append(mGrpContNo);
             tSBql.append("') where grpcontno = '");
             tSBql.append(mGrpContNo);
             tSBql.append("'");
             mGrpContSql2 = tSBql.toString();

        map.put(mPolSql, "UPDATE");
        map.put(mContSql, "UPDATE");
        map.put(mGrpContSql, "UPDATE");
        map.put(mGrpPolSql, "UPDATE");
        map.put(mGrpContSql2, "UPDATE");
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
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mGrpContNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        LCGrpPolDB tLCGrpPolDB = new LCGrpPolDB();
        tLCGrpPolDB.setGrpContNo(mGrpContNo);
        LCGrpPolSet tLCGrpPolSet = tLCGrpPolDB.query();
        if (tLCGrpPolSet.size() == 0)
        {
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "δ¼�뼯�屣����Ϣ��";
            this.mErrors.addOneError(tError);
            return false;
        }
        //У��������Ƿ��Ѿ�¼�뱻����
        for (int i = 1; i <= tLCGrpPolSet.size(); i++)
        {
            if (tLCGrpPolSet.get(i).getPeoples2() == 0)
            {
                CError tError = new CError();
                tError.moduleName = "GrpPolApproveAfterInitService";
                tError.functionName = "checkData";
                tError.errorMessage = "����" + tLCGrpPolSet.get(i).getRiskCode() +
                        "��δ¼�뱻���ˣ���ɾ�������ֻ�¼�뱻�����ˣ�";
                this.mErrors.addOneError(tError);
                return false;
            }
            ExeSQL tExeSQL = new ExeSQL();
            String tSql = "select distinct 1 from lcpol where grpcontno = '" + mGrpContNo
                    + "' and riskcode = '" + tLCGrpPolSet.get(i).getRiskCode() + "'";
            String rs = tExeSQL.getOneValue(tSql);
            if (rs == null || rs.length() == 0)
            {
                CError tError = new CError();
                tError.moduleName = "GrpFirstWorkFlowCheck";
                tError.functionName = "checkData";
                tError.errorMessage = "����������δ¼�����������Ϣ��";
                this.mErrors.addOneError(tError);
                return false;
            }
        }
  
        mLCGrpContSchema.setSchema(tLCGrpContDB);
        if (!mLCGrpContSchema.getAppFlag().trim().equals("0"))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "�˼��嵥����Ͷ���������ܽ��и��˲���!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (mLCGrpContSchema.getApproveFlag().equals("9"))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "�˼���Ͷ���������Ѿ�ͨ���������ٽ��и���ͨ������!";
            this.mErrors.addOneError(tError);
            return false;
        }

        ExeSQL tExeSQL = new ExeSQL();
        String sql = "select count(1) from LCCont where GrpContNo = '"
                + mLCGrpContSchema.getGrpContNo() + "'";

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
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "dealData";
            tError.errorMessage = "����Ͷ������û�и���Ͷ���������ܽ��и��˲���!";
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
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��ò���Ա����
        mOperator = mGlobalInput.Operator;
        if (mOperator == null || mOperator.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
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
            tError.moduleName = "GrpPolApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ���������GrpContNo
        mGrpContNo = (String) mTransferData.getValueByName("GrpContNo");
        if (mGrpContNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpPolApproveAfterInitService";
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

        //�ж���������Ͷ�������Ƿ��в���Ա�������,�����ø����屣��״̬Ϊ�������޸�
        ExeSQL tExeSQL = new ExeSQL();
        String sql = "select count(1) from LCGrpIssuePol where GrpContNo = '"
                + mLCGrpContSchema.getGrpContNo() + "' and replyman is null and backobjtype = '1'";

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
         System.out.print(" mApproveFlag: " + mApproveFlag);

        String tCurrentDate = PubFun.getCurrentDate();
        String tCurrentTime = PubFun.getCurrentTime();
        // �޸ļ���Ͷ���������˱���͸�������
        mLCGrpContSchema.setApproveCode(mOperator);
        mLCGrpContSchema.setApproveDate(tCurrentDate);
        mLCGrpContSchema.setApproveTime(tCurrentTime);
        mLCGrpContSchema.setApproveFlag(mApproveFlag);
        mLCGrpContSchema.setModifyDate(tCurrentDate);
        mLCGrpContSchema.setModifyTime(tCurrentTime);
        mLCGrpContSchema.setSpecFlag("0");

        //added by yeshu,2005-12-22,begin
        //���������ͬ���payintv�����ŵ������в����ڽ������ͬ�Ľ��ѷ�ʽΪ-1��
        //�������������������ͬ���ѷ�ʽΪ0
        //������ȫΪ�ڽ�����ȡ���Ѽ��ʱ����̵ķ�ʽ
        String temPayintv="12";
        StringBuffer SQLPayintv =  new StringBuffer(128);
        SQLPayintv.append("select payintv from lcgrppol where grpcontno='");
        SQLPayintv.append(mGrpContNo);
        SQLPayintv.append("'");
        System.out.println(SQLPayintv.toString());
        ExeSQL temExeSql = new ExeSQL();
        SSRS tmpSSRS = temExeSql.execSQL(SQLPayintv.toString());
        System.out.println("tmpSSRS.MAXROW:"+tmpSSRS.MaxRow);
        for(int i=1;i<=tmpSSRS.MaxRow;i++)
        {
          System.out.println("tmpSSRS.GetText("+i+",1)="+tmpSSRS.GetText(i,1));
          if(tmpSSRS.GetText(i,1).equals("-1"))
          {
            temPayintv="-1";
            break;
          }
          else if (tmpSSRS.GetText(i,1).equals("0"))
          {
            temPayintv="0";
          }
          else if( Integer.parseInt(tmpSSRS.GetText(i,1)) < Integer.parseInt(temPayintv) &&
                  !temPayintv.equals("0"))
          {
            temPayintv=tmpSSRS.GetText(i,1);
          }
        }
        mLCGrpContSchema.setPayIntv(temPayintv);
        //added by yeshu,2005-12-22,end
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
        mTransferData.setNameAndValue("GrpContNo", mGrpContNo);
        mTransferData.setNameAndValue("PrtNo", mLCGrpContSchema.getPrtNo());
        mTransferData.setNameAndValue("SaleChnl", mLCGrpContSchema.getSaleChnl());
        mTransferData.setNameAndValue("ManageCom",
                mLCGrpContSchema.getManageCom());
        mTransferData.setNameAndValue("AgentCode", mLCGrpContSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup", mLCGrpContSchema.getAgentGroup());
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

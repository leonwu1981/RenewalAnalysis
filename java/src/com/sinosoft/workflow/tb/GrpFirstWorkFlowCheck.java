/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.LCGrpContSchema;
import com.sinosoft.lis.vschema.LCContSet;
import com.sinosoft.lis.vschema.LCGrpPolSet;
import com.sinosoft.lis.vschema.LCPolSet;
import com.sinosoft.utility.*;


/**
 * <p>Title: ��������Լ��������ʼ���У���� </p>
 * <p>Description: У���Ƿ�����¼���������������¼¼���ˡ�¼�����ʱ��</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class GrpFirstWorkFlowCheck
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();


    /** �����洫�����ݵ����� */
    private VData mResult = new VData();


    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();

    /** ҵ������ */
    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();
//    private LCContSchema mLCContSchema = new LCContSchema();
//    private LCGrpPolSchema mLCGrpPolSchema = new LCGrpPolSchema();
//    private LCPolSchema mLCPolSchema = new LCPolSchema();
//    private ES_DOC_MAINSchema mES_DOC_MAINSchema = new ES_DOC_MAINSchema();

    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;


    /** ҵ�������ַ��� */
    private String mGrpContNo;
    private String mContSql;
//    private String mES_DOC_MAINSql = "";
    private String mGrpContSql;
    private String[] mGrpPolSql;
    public GrpFirstWorkFlowCheck()
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

        //׼������̨������
        if (!prepareOutputData())
        {
            return false;
        }

        System.out.println("Start  Submit...");

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
            tError.moduleName = "GrpFirstWorkFlowCheck";
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
            tError.moduleName = "GrpFirstWorkFlowCheck";
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
            tError.moduleName = "GrpFirstWorkFlowCheck";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
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
            tError.moduleName = "GrpFirstWorkFlowCheck";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������GrpContNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }


    /**
     * У��ҵ������:У��ʱ��ﵽ¼���������
     * @return
     */
    private boolean checkData()
    {
        LCGrpContDB tLCGrpContDB = new LCGrpContDB();
        LCGrpPolDB tLCGrpPolDB = new LCGrpPolDB();
        LCContDB tLCContDB = new LCContDB();
        LCPolDB tLCPolDB = new LCPolDB();

        tLCGrpContDB.setGrpContNo(mGrpContNo);
        if (!tLCGrpContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "GrpFirstWorkFlowCheck";
            tError.functionName = "checkData";
            tError.errorMessage = "�����ͬ����" + mGrpContNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCGrpContSchema.setSchema(tLCGrpContDB);

        tLCGrpPolDB.setGrpContNo(mGrpContNo);
        LCGrpPolSet tLCGrpPolSet = tLCGrpPolDB.query();
        if (tLCGrpPolSet.size() == 0)
        {
            CError tError = new CError();
            tError.moduleName = "GrpFirstWorkFlowCheck";
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
                tError.moduleName = "GrpFirstWorkFlowCheck";
                tError.functionName = "checkData";
                tError.errorMessage = "����" + tLCGrpPolSet.get(i).getRiskCode()
                        + "��δ¼�뱻���ˣ���ɾ�������ֻ�¼�뱻�����ˣ�";
                this.mErrors.addOneError(tError);
                return false;
            }
        }

        tLCContDB.setGrpContNo(mGrpContNo);
        LCContSet tLCContSet = tLCContDB.query();
        if (tLCContSet.size() == 0)
        {
            CError tError = new CError();
            tError.moduleName = "GrpFirstWorkFlowCheck";
            tError.functionName = "checkData";
            tError.errorMessage = "�ŵ��¸�����ͬ��Ϣ��ѯʧ�ܣ�����ԭ������ǣ�δ¼�������ͬ��Ϣ��";
            this.mErrors.addOneError(tError);
            return false;
        }
        for (int i = 1; i <= tLCContSet.size(); i++)
        {
            tLCPolDB.setContNo(tLCContSet.get(i).getContNo());
            LCPolSet tLCPolSet = tLCPolDB.query();
            if (tLCPolSet.size() == 0)
            {
                CError tError = new CError();
                tError.moduleName = "GrpFirstWorkFlowCheck";
                tError.functionName = "checkData";
                tError.errorMessage = "�ŵ��¸���������Ϣ��ѯʧ�ܣ�����ԭ������ǣ�δ¼�����" + tLCContSet.get(i).getContNo()
                        + "������Ϣ��";
                this.mErrors.addOneError(tError);
                return false;
            }
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

        map.put(mLCGrpContSchema, "UPDATE");
        map.put(mContSql, "UPDATE");
//        map.put(mES_DOC_MAINSql,"UPDATE");
        map.put(mGrpContSql, "UPDATE");
        for (int i = 0; i < mGrpPolSql.length; i++)
        {
            map.put(mGrpPolSql[i], "UPDATE");
        }

        mResult.add(map);
        return true;
    }


    /**
     * sumData
     * ���б��ѡ�����Ļ���
     * @return boolean
     */
    private boolean sumData()
    {
        LCGrpPolDB tLCGrpPolDB = new LCGrpPolDB();
        LCGrpPolSet tLCGrpPolSet = new LCGrpPolSet();
        tLCGrpPolDB.setGrpContNo(mGrpContNo);
        tLCGrpPolSet = tLCGrpPolDB.query();
        mGrpPolSql = new String[tLCGrpPolSet.size()];

        StringBuffer tSBql = null;
        String tGrpPolNo = null;
        for (int i = 1; i <= tLCGrpPolSet.size(); i++)
        {
            tGrpPolNo = tLCGrpPolSet.get(i).getGrpPolNo();
//            mGrpPolSql[i - 1] =
//                    "update lcgrppol set prem = (select sum(prem) from lcpol where grppolno = '"
//                    + tGrpPolNo + "'),amnt = (select sum(amnt) from lcpol where grppolno = '"
//                    + tGrpPolNo
//                    + "'),peoples2 = (select sum(InsuredPeoples) from lcpol where grppolno = '"
//                    + tGrpPolNo + "') where grppolno = '" + tGrpPolNo + "'";
            tSBql = new StringBuffer(128);
            tSBql.append(
                    "update lcgrppol set prem = (select sum(prem) from lcpol where grppolno = '");
            tSBql.append(tGrpPolNo);
            tSBql.append("'),amnt = (select sum(amnt) from lcpol where grppolno = '");
            tSBql.append(tGrpPolNo);
            tSBql.append("'),peoples2 = (select sum(InsuredPeoples) from lcpol where grppolno = '");
            tSBql.append(tGrpPolNo);
            tSBql.append("') where grppolno = '");
            tSBql.append(tGrpPolNo);
            tSBql.append("'");
            mGrpPolSql[i - 1] = tSBql.toString();
        }

//        mGrpContSql =
//                "update lcgrpcont set prem = (select sum(prem) from lccont where grpcontno = '"
//                + mGrpContNo + "'),amnt=(select sum(amnt) from lccont where grpcontno = '"
//                + mGrpContNo + "'),Peoples = (select sum(Peoples) from lccont where grpcontno = '"
//                + mGrpContNo + "') where grpcontno = '" + mGrpContNo + "'";
        tSBql = new StringBuffer(128);
        tSBql.append(
                "update lcgrpcont set prem = (select sum(prem) from lccont where grpcontno = '");
        tSBql.append(mGrpContNo);
        tSBql.append("'),amnt=(select sum(amnt) from lccont where grpcontno = '");
        tSBql.append(mGrpContNo);
       // tSBql.append("'),Peoples = (select sum(Peoples) from lccont where grpcontno = '");
       // tSBql.append(mGrpContNo);
      //  tSBql.append("') where grpcontno = '");
        tSBql.append("') where grpcontno='");
        tSBql.append(mGrpContNo);
        tSBql.append("'");
        mGrpContSql = tSBql.toString();

        return true;
    }

    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {
        //Ϊ�����ͬ�����¼���ˡ�¼��ʱ��
        mLCGrpContSchema.setInputOperator(mOperater);
        mLCGrpContSchema.setInputDate(PubFun.getCurrentDate());
        mLCGrpContSchema.setInputTime(PubFun.getCurrentTime());

        //Ϊ��ͬ�����¼���ˡ�¼��ʱ��
//        mContSql = "update lccont set InputOperator ='" + mOperater + "',InputDate = '"
//                + PubFun.getCurrentDate() + "',InputTime = '" + PubFun.getCurrentTime()
//                + "' where grpcontno = '" + mGrpContNo + "'";
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("update lccont set InputOperator ='");
        tSBql.append(mOperater);
        tSBql.append("',InputDate = '");
        tSBql.append(PubFun.getCurrentDate());
        tSBql.append("',InputTime = '");
        tSBql.append(PubFun.getCurrentTime());
        tSBql.append("' where grpcontno = '");
        tSBql.append(mGrpContNo);
        tSBql.append("'");
        mContSql = tSBql.toString();

        //����ɨ���״̬
//        mES_DOC_MAINSql = "update ES_DOC_MAIN set InputState = '1',"
//                          +"InputStartDate ='" + PubFun.getCurrentDate() + "',"
//                          +"InputStartTime = '" + PubFun.getCurrentTime() + "',"
//                          +"scanoperator = '" +mOperater+"'"
//                          +" where doccode = '" + mLCGrpContSchema.getPrtNo() + "'"
//                          ;
        //���¶Ը��˺�ͬ���������֣������ͬ��������ͳ�ƺϼ��������ϼƱ��ѣ��ϼƱ���
        if (!sumData())
        {
            return false;
        }

        return true;
    }

    /**
     *  ���ؽ����
     *  @return mResult
     */
    public VData getResult()
    {
        return mResult;
    }

    /**
     * ���ش��������
     * @return CErrors
     */
    public CErrors getErrors()
    {
        return mErrors;
    }
}

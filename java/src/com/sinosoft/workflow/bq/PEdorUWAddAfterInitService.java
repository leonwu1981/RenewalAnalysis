/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.bq;

import java.text.DecimalFormat;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
*  @author lanjun
 * @version 1.0
 */

public class PEdorUWAddAfterInitService implements AfterInitService
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
    /**���������� */
    ActivityOperator mActivityOperator = new ActivityOperator();
    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;
    private String mOperate;
    /** ҵ�����ݲ����ַ��� */
    private String mEdorNo;
    private String mEdorType;
     private String mContNo;
    private String mPolNo;
    private String mPolNo2;
//    private String mInsuredNo;
    private String mAddReason;
    private Reflections mReflections = new Reflections();

    /**ִ�б�ȫ�������ӷѻ������0000000002*/
    /**������*/
    private LCPolSchema mLCPolSchema = new LCPolSchema();
    private LPPolSchema mLPPolSchema = new LPPolSchema();

    /** ��ȫ�˱����� */
    private LPUWMasterSchema mLPUWMasterSchema = new LPUWMasterSchema();
    /** ������� */
    private LPDutySet mLPDutySet = new LPDutySet();
    private LCDutySet mLCDutySet = new LCDutySet();

    private LPDutySet mNewLPDutySet = new LPDutySet();
      private LCDutySet mNewLCDutySet = new LCDutySet();
    /** ���ѱ� */
    private LPPremSet mLPPremSet = new LPPremSet();
    private LPPremSet mOldLPPremSet = new LPPremSet();

    private LCPremSet mLCPremSet = new LCPremSet();
    private LCPremSet mOldLCPremSet = new LCPremSet();

//    private LPPremSet mNewLPPremSet = new LPPremSet();
    /**��ȫ���Ĳ��˷ѱ�*/
    private LJSGetEndorseSet mOldLJSGetEndorseSet = new LJSGetEndorseSet();
    private LJSGetEndorseSet mNewLJSGetEndorseSet = new LJSGetEndorseSet();
    /**��ȫ���ı�*/
    private LPEdorMainSchema mLPEdorMainSchema = new LPEdorMainSchema();


    public PEdorUWAddAfterInitService()
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

        //У���Ƿ���δ��ӡ�ļӷ�֪ͨ��
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

        System.out.println("Start SysUWNoticeBL Submit...");

        //mResult.clear();
        return true;
    }


    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {
        // �˱���Լ��Ϣ
        if (prepareAdd())
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * У��ҵ������
     * @return
     */
    private boolean checkData()
    {
        //У�鱣����Ϣ
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setPolNo(mPolNo2);
        if (!tLCPolDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mPolNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCPolSchema.setSchema(tLCPolDB);

        //У�鱣����Ϣ
        LPPolDB tLPPolDB = new LPPolDB();
        tLPPolDB.setPolNo(mPolNo2);
        tLPPolDB.setEdorNo(mEdorNo);
        tLPPolDB.setEdorType(mEdorType);
        if (!tLPPolDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mPolNo2 + "��P����Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            //return false;
        }
        mLPPolSchema.setSchema(tLPPolDB);

        //У�鱣ȫ�����˱�����
        //У�鱣����Ϣ
        LPUWMasterDB tLPUWMasterDB = new LPUWMasterDB();
        tLPUWMasterDB.setPolNo(mPolNo2);
        tLPUWMasterDB.setEdorNo(mEdorNo);
        tLPUWMasterDB.setEdorType(mEdorType);

        if (!tLPUWMasterDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mPolNo + "��ȫ�����˱�������Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLPUWMasterSchema.setSchema(tLPUWMasterDB);

        // ����δ��ӡ״̬�ĺ˱�֪ͨ���ڴ�ӡ������ֻ����һ��
        // ������ͬһ���������ͣ�ͬһ���������룬ͬһ��������������
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

        tLOPRTManagerDB.setCode(PrintManagerBL.CODE_PEdorUW); //�˱�֪ͨ��
        tLOPRTManagerDB.setOtherNo(mPolNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_INDPOL); //������
        tLOPRTManagerDB.setStandbyFlag2(mEdorNo);
        tLOPRTManagerDB.setStateFlag("0");

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "��ѯ��ӡ�������Ϣ����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLOPRTManagerSet.size() != 0)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "�ڴ�ӡ����������һ������δ��ӡ״̬�ĺ˱�֪ͨ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (mLPPremSet != null && mLPPremSet.size() > 0)
        {
            LPEdorMainDB tLPEdorMainDB = new LPEdorMainDB();
            tLPEdorMainDB.setEdorNo(mEdorNo);
//	  tLPEdorMainDB.setPolNo(mPolNo) ;
//	  tLPEdorMainDB.setEdorType(mLPPremSet.get(1).getEdorType());
            LPEdorMainSet mLPEdorMainSet = new LPEdorMainSet();
            mLPEdorMainSet = tLPEdorMainDB.query();
            if (mLPEdorMainSet == null || mLPEdorMainSet.size() != 1)
            {
                // @@������
                CError tError = new CError();
                tError.moduleName = "PEdorUWAddAfterInitService";
                tError.functionName = "preparePrint";
                tError.errorMessage = "��ѯ��ȫ����������Ϣ����!";
                this.mErrors.addOneError(tError);
                return false;
            }
            mLPEdorMainSchema = mLPEdorMainSet.get(1);
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
            tError.moduleName = "PEdorUWAddAfterInitService";
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
            tError.moduleName = "PEdorUWAddAfterInitService";
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
            tError.moduleName = "PEdorUWAddAfterInitService";
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
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mEdorNo = (String) mTransferData.getValueByName("EdorNo");
        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mEdorNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������EdorNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mEdorType = (String) mTransferData.getValueByName("EdorType");
        if (mEdorType == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������mEdorTypeʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mPolNo = (String) mTransferData.getValueByName("PolNo");
        if (mPolNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������PolNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mPolNo2 = (String) mTransferData.getValueByName("PolNo2");
        if (mPolNo2 == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������PolNo2ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mAddReason = (String) mTransferData.getValueByName("AddReason");
        //���ҵ��ӷ�֪ͨ����
        mLPPremSet = (LPPremSet) mTransferData.getValueByName("LPPremSet");


        return true;
    }


    /**
     * ׼����Լ������Ϣ
     * �����������������򷵻�false,���򷵻�true
     */
    private boolean prepareAdd()
    {

        double tTotalMoney = 0;
        double tSumStandPrem = 0;
        String tCurrentDate = PubFun.getCurrentDate();
        String tCurrentTime = PubFun.getCurrentTime();
        //�γɼӷ���Ϣ
        if (mLPPremSet.size() > 0)
        {
            //ȡ������Ϣ
            LPDutyDB tLPDutyDB = new LPDutyDB();
            tLPDutyDB.setPolNo(mLPPolSchema.getPolNo());
            tLPDutyDB.setEdorNo(mEdorNo);
            tLPDutyDB.setEdorType(mEdorType);
            mLPDutySet = tLPDutyDB.query();

            if(mLPDutySet.size()<1)
            {
              LCDutyDB tLCDutyDB = new LCDutyDB();
              tLCDutyDB.setPolNo(mLCPolSchema.getPolNo());
              //tLCDutyDB.setEdorNo(mEdorNo);
              //tLCDutyDB.setEdorType(mEdorType);
              mLCDutySet = tLCDutyDB.query();

            }

            //�����ȥ���α�ȫ�ӷ���Ŀ,�б�ʱ�Ļ���������󣬸ñ����ڸñ�ȫ��Ŀ�µļӷ���Ŀ����
            //�Ա���㱾�α�ȫ�ӷѵı�����ʼ����ֵ.
            //�ڱ�ȫȷ�Ϻ󣬱������Ѽ�¼������c����
            String tsql = "select count(*) from LPPrem where  polno = '"
                    + mLCPolSchema.getPolNo().trim() + "'  and edortype = '" + mEdorType
                    + "'  and edorno = '" + mEdorNo + "' and state in ( '1','3')";
            String tReSult = new String();
            ExeSQL tExeSQL = new ExeSQL();
            tReSult = tExeSQL.getOneValue(tsql);
            if (tReSult == null || tReSult.equals(""))
            {
                return false;
            }

            int tCount = 0;
            tCount = Integer.parseInt(tReSult); //�Ѱ����˱��νڵ㼰���ͬ���ڵ�

            //����������
            if (mLPDutySet.size() > 0)
            {
                for (int m = 1; m <= mLPDutySet.size(); m++)
                {
                    int maxno = 0;
                    LPDutySchema tLPDutySchema = new LPDutySchema();
                    tLPDutySchema = mLPDutySet.get(m);

                    //��ȥ�����ε�ԭ���α�ȫ�ӷѽ��
                    String sql =
                            "select * from LPPrem where payplancode  like '000000%' and polno = '"
                            + mLCPolSchema.getPolNo().trim() + "' and dutycode = '"
                            + tLPDutySchema.getDutyCode().trim() + "' and edortype = '"
                            + tLPDutySchema.getEdorType() + "' and  state = '2'";
                    LPPremDB tLPPremDB = new LPPremDB();
                    LPPremSet tLPPremSet = new LPPremSet();

                    tLPPremSet = tLPPremDB.executeQuery(sql);

                    if (tLPPremSet.size() > 0)
                    {
                        for (int j = 1; j <= tLPPremSet.size(); j++)
                        {
                            LPPremSchema tLPPremSchema = new LPPremSchema();
                            tLPPremSchema = tLPPremSet.get(j);

                            tLPDutySchema.setPrem(tLPDutySchema.getPrem() - tLPPremSchema.getPrem());
                            mLPPolSchema.setPrem(mLPPolSchema.getPrem() - tLPPremSchema.getPrem());
                        }
                    }

                    //ΪͶ����������α���ϱ��εļӷ�.ͬʱ�γɼӷ���Ϣ
                    for (int i = 1; i <= mLPPremSet.size(); i++)
                    {
                        double tPrem;

                        if (mLPPremSet.get(i).getDutyCode().equals(tLPDutySchema.getDutyCode()))
                        {
                            maxno += 1;
                            //�γɼӷѱ���
                            String PayPlanCode = "";
                            PayPlanCode = String.valueOf(maxno + tCount);
                            for (int j = PayPlanCode.length(); j < 8; j++)
                            {
                                PayPlanCode = "0" + PayPlanCode;
                            }

                            //�����ܱ���
                            tPrem = mLPPolSchema.getPrem() + mLPPremSet.get(i).getPrem();
                            tSumStandPrem += mLPPremSet.get(i).getPrem();
                            mLPPremSet.get(i).setContNo(mContNo);//����ע������������Ϣ��ǰ̨������Ϣ
                            //mLPPremSet.get(i).setDutyCode();
                            mLPPremSet.get(i).setPayPlanCode(PayPlanCode);
			     mLPPremSet.get(i).setGrpContNo("00000000000000000000");
                            //mLPPremSet.get(i).setPayPlanType();
                            //mLPPremSet.get(i).setPayTimes();
                            mLPPremSet.get(i).setPayIntv(tLPDutySchema.getPayIntv());
//			  mLPPremSet.get(i).setMult(tLPDutySchema.getMult());

                            //�����δ���ڱ��Ѽӷ�
                            String valiDate = mLPPolSchema.getCValiDate();
                            String sqlMainPolPayToDate = "select paytoDate from lcpol where polno='"
                                +mLPPolSchema.getMainPolNo()+"'";

                            String payToDate  =  tExeSQL.getOneValue(sqlMainPolPayToDate);

                            System.out.println("valiDate"+valiDate +"payToDate"+payToDate);
                            int leftDays = PubFun.calInterval(valiDate,payToDate,"D");
                            int payIntvl  = mLPPolSchema.getPayIntv();
                            int payDays ; //�ڼ�����

                            switch(payIntvl)
                            {
                               //�½�
                              case 1:
                                payDays = 30;
                                break;
                                //���Ƚ�
                              case 3:
                                payDays = 90;
                                break;
                                //���꽻
                              case 6:
                                payDays = 180;
                                break;
                                //�꽻
                              case 12:
                                payDays = 365;
                                break;
                                //����
                              case 0:
                                payDays = 365;
                                break;
                              default:
                                payDays = 365;
                            }

                            double leftPrem = mLPPremSet.get(i).getPrem() * leftDays / payDays;
                            System.out.println(leftPrem+"  "+leftDays
                                               );

                            mLPPremSet.get(i).setStandPrem(leftPrem);//�����Ӹ�����ʱ��δ���ڱ���
                            mLPPremSet.get(i).setPrem(leftPrem);
                            mLPPremSet.get(i).setSumPrem("0");
                            //mLPPremSet.get(i).setRate();
                            //mLPPremSet.get(i).setPayStartDate();
                            //mLPPremSet.get(i).setPayEndDate();
                            mLPPremSet.get(i).setPaytoDate(mLPPremSet.get(i).getPayStartDate());
                            mLPPremSet.get(i).setState("2"); //0:�б�ʱ�ı����1:�б�ʱ�ļӷ��2�����α�ȫ��Ŀ��ȫ�ӷ��3��ǰ���β�ͨ�����µı�ȫ�ӷѣ�
                            mLPPremSet.get(i).setUrgePayFlag("Y"); //�ӷ���һ��Ҫ�߽���������ȥȡ�������������Ĵ߽���־��
                            mLPPremSet.get(i).setManageCom(mLCPolSchema.getManageCom());
                            mLPPremSet.get(i).setAppntNo(mLCPolSchema.getAppntNo());
                            mLPPremSet.get(i).setAppntType("1"); //����Ͷ��
                            mLPPremSet.get(i).setModifyDate(PubFun.getCurrentDate());
                            mLPPremSet.get(i).setModifyTime(PubFun.getCurrentTime());
                            mLPPremSet.get(i).setSuppRiskScore(mLPPremSet.get(i).getSuppRiskScore());
                             mLPPremSet.get(i).setOperator(mGlobalInput.Operator);
                            mLPPremSet.get(i).setMakeDate(PubFun.getCurrentDate());
                            mLPPremSet.get(i).setMakeTime(PubFun.getCurrentTime());

                            //���±�������
                            tLPDutySchema.setPrem(tLPDutySchema.getPrem()
                                    + mLPPremSet.get(i).getPrem());
                            //���±�������
                            mLPPolSchema.setPrem(tPrem);
                        }
                    }
                    mNewLPDutySet.add(tLPDutySchema);

                }
            }

        }

        //׼��ɾ����һ�θ���Ŀ�ļӷѵ�����
        String tSQL = "select * from lpprem where polno = '" + mPolNo2 + "'"
                + " and EdorNo = '" + mEdorNo + "'"
                + " and EdorType = '" + mEdorType + "'"
                + " and substr(payplancode,1,6) = '000000'"
                + " and state = '2'"; //0:�б�ʱ�ı����1:�б�ʱ�ļӷ��2�����α�ȫ��Ŀ��ȫ�ӷ��3��ǰ���β�ͨ�����µı�ȫ�ӷѣ�
        LPPremDB tLPPremDB = new LPPremDB();
        mOldLPPremSet = tLPPremDB.executeQuery(tSQL);
        if (mOldLPPremSet == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "prepareAdd";
            tError.errorMessage = "��ѯ��ȫ�ӷ���Ϣʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //׼��ɾ����һ�θ�NS��Ŀ�ı�ȫ���Ĳ��˷ѱ��б�ȫ�˹��˱��ӷѵ�����
        tSQL = "select * from ljsgetendorse where getnoticeno = '" + mEdorNo + "'"
                + " and endorsementno = '" + mEdorNo + "'"
                + " and feeoperationtype = '" + mLPPremSet.get(1).getEdorType() + "'"
                + " and substr(payplancode,1,6) = '000000'"
                + " and payplancode <> '00000000'"
                + " and polno = '" + mPolNo2 + "'";
        LJSGetEndorseDB tLJSGetEndorseDB = new LJSGetEndorseDB();
        mOldLJSGetEndorseSet = tLJSGetEndorseDB.executeQuery(tSQL);
        if (mOldLJSGetEndorseSet == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "prepareAdd";
            tError.errorMessage = "��ѯ��ȫ�ӷ���Ϣʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //׼����Ӹ���Ŀ�ı�ȫ���Ĳ��˷ѱ��б�ȫ�˹��˱��ӷѵ�����
        if (tSumStandPrem > 0 && mLPPremSet != null && mLPPremSet.size() > 0)
        {

            for (int i = 1; i <= mLPPremSet.size(); i++)
            {
                LCDutyDB tLCDutyDB = new LCDutyDB();
                LCDutySchema tLCDutySchema = new LCDutySchema();
                tLCDutyDB.setDutyCode(mLPPremSet.get(i).getDutyCode());
                tLCDutyDB.setPolNo(mLPPremSet.get(i).getPolNo());
                LCDutySet tLCDutySet = tLCDutyDB.query();
                if (tLCDutySet == null)
                {
                    // @@������
                    CError tError = new CError();
                    tError.moduleName = "PEdorUWAddAfterInitService";
                    tError.functionName = "prepareAdd";
                    tError.errorMessage = "��ѯ���α���Ϣʧ��!";
                    this.mErrors.addOneError(tError);
                    return false;
                }
                tLCDutySchema = tLCDutySet.get(1);
                String tDutyPaytoDate = tLCDutySchema.getPaytoDate();
                String tPremPaytoDate = mLPPremSet.get(i).getPaytoDate();

                //��ȫ�˱����һ���μӷѵĽ�������С�ڸ����εĽ�������ʱҪ���㵱ǰҪ�ӷѵĽ�����Ϣ������ȫ���Ĳ��˷ѱ����ύһ�ʽ�����
                if (!mEdorType.equals("NS") && tDutyPaytoDate != null && tPremPaytoDate != null
                        && tDutyPaytoDate.compareTo(tPremPaytoDate) > 0)
                {

                    //������Ϣ������
                    //�������½��Ѽ�����㽻������
                    int premNum = 0;
                    if (mLCPolSchema.getPayIntv() == 0)
                    {
                        premNum = 1;
                    }
                    else
                    {
                        premNum = PubFun.calInterval(mLCPolSchema.getCValiDate()
                                , mLCPolSchema.getPaytoDate(), "M") / mLCPolSchema.getPayIntv();
                    }

                    double intervalMoney = mLPPremSet.get(i).getPrem(); //���Ѳ��
                    double bfMoney = 0; //���ѱ���
                    double interestMoney = 0; //��Ϣ

 /**     ע�� lanjun 2005/11/24 ����ע�͵���Ϣ�ļ��㷽��             */

//                    //��ȡ��������
//                    LMLoanDB tLMLoanDB = new LMLoanDB();
//                    tLMLoanDB.setRiskCode(mLCPolSchema.getRiskCode());
//                    if (!tLMLoanDB.getInfo())
//                    {
//                        //?
//                    }
//                    AccountManage tAccountManage = new AccountManage();
//
//                    //������Ϣ
//                    double interest = 0;
//                    for (int j = 0; j < premNum; j++)
//                    {
//                        if (j == 0)
//                        {
//                            interest = tAccountManage.getInterest(intervalMoney
//                                    , mLCPolSchema.getCValiDate(), PubFun.getCurrentDate()
//                                    , tLMLoanDB.getInterestRate(), tLMLoanDB.getInterestMode()
//                                    , tLMLoanDB.getInterestType(), "D");
//                        }
//                        else
//                        {
//                            interest = tAccountManage.getInterest(intervalMoney
//                                    , PubFun.calDate(mLCPolSchema.getCValiDate()
//                                    , j * mLCPolSchema.getPayIntv(), "M", mLCPolSchema.getCValiDate())
//                                    , PubFun.getCurrentDate(), tLMLoanDB.getInterestRate()
//                                    , tLMLoanDB.getInterestMode(), tLMLoanDB.getInterestType(), "D");
//                        }
//                        bfMoney += intervalMoney; //���ѱ���
//                        if (interest > 0)
//                        {
//                            interestMoney += interest; //��Ϣ
//                        }
//                        bfMoney = Double.parseDouble(new DecimalFormat("0.00").format(bfMoney));
//                        interestMoney = Double.parseDouble(new DecimalFormat("0.00").format(
//                                interestMoney));
//                    }

                    tTotalMoney = bfMoney + interestMoney;
                    //�������Ĳ��˷ѱ��еĲ��Ѽ�¼
                    LJSGetEndorseSchema tLJSGetEndorseSchema = new LJSGetEndorseSchema();
                    LPPremSchema tLPPremSchema = new LPPremSchema();
                    tLPPremSchema = mLPPremSet.get(i);
                    tLJSGetEndorseSchema.setGetNoticeNo(mEdorNo); //����֪ͨ�����
                    tLJSGetEndorseSchema.setEndorsementNo(mEdorNo);
                    tLJSGetEndorseSchema.setFeeOperationType(tLPPremSchema.getEdorType());

                    mReflections.transFields(tLJSGetEndorseSchema, mLCPolSchema);

                    tLJSGetEndorseSchema.setGetDate(mLPEdorMainSchema.getEdorValiDate());
                    tLJSGetEndorseSchema.setGetMoney(bfMoney); //�����
                    tLJSGetEndorseSchema.setFeeFinaType("BF"); //	�ӷ�
                    tLJSGetEndorseSchema.setPayPlanCode(tLPPremSchema.getPayPlanCode()); //������
                    tLJSGetEndorseSchema.setDutyCode(tLPPremSchema.getDutyCode()); //�����ã���һ��Ҫ��תljagetendorseʱ�ǿ�
                    tLJSGetEndorseSchema.setOtherNo(mEdorNo); //������
                    tLJSGetEndorseSchema.setOtherNoType("3"); //��ȫ����
                    tLJSGetEndorseSchema.setGetFlag("0");

                    tLJSGetEndorseSchema.setOperator(mGlobalInput.Operator);
                    tLJSGetEndorseSchema.setMakeDate(tCurrentDate);
                    tLJSGetEndorseSchema.setMakeTime(tCurrentTime);
                    tLJSGetEndorseSchema.setModifyDate(tCurrentDate);
                    tLJSGetEndorseSchema.setModifyTime(tCurrentTime);

                    //���ɱ�ȫ���Ĳ��˷ѱ���Ϣ��¼
//                    LJSGetEndorseSchema tLJSGetEndorseSchemaLX = new LJSGetEndorseSchema();
//                    tLJSGetEndorseSchemaLX.setGetNoticeNo(mEdorNo); //����֪ͨ�����
//                    tLJSGetEndorseSchemaLX.setEndorsementNo(mEdorNo);
//                    tLJSGetEndorseSchemaLX.setFeeOperationType(tLPPremSchema.getEdorType());
//
//                    mReflections.transFields(tLJSGetEndorseSchemaLX, mLCPolSchema);
//
//                    tLJSGetEndorseSchemaLX.setGetDate(mLPEdorMainSchema.getEdorValiDate());
//                    tLJSGetEndorseSchemaLX.setGetMoney(interestMoney);
//                    tLJSGetEndorseSchemaLX.setFeeFinaType("LX"); //	��Ϣ
//                    tLJSGetEndorseSchemaLX.setPayPlanCode(tLPPremSchema.getPayPlanCode()); //������
//                    tLJSGetEndorseSchemaLX.setDutyCode(tLPPremSchema.getDutyCode()); //�����ã���һ��Ҫ��תljagetendorseʱ�ǿ�
//                    tLJSGetEndorseSchemaLX.setOtherNo(mEdorNo); //������
//                    tLJSGetEndorseSchemaLX.setOtherNoType("3"); //��ȫ����
//                    tLJSGetEndorseSchemaLX.setGetFlag("0");
//
//                    tLJSGetEndorseSchemaLX.setOperator(mLCPolSchema.getManageCom());
//                    tLJSGetEndorseSchemaLX.setMakeDate(tCurrentDate);
//                    tLJSGetEndorseSchemaLX.setMakeTime(tCurrentTime);
//                    tLJSGetEndorseSchemaLX.setModifyDate(tCurrentDate);
//                    tLJSGetEndorseSchemaLX.setModifyTime(tCurrentTime);

                    mNewLJSGetEndorseSet.add(tLJSGetEndorseSchema);
                    //mNewLJSGetEndorseSet.add(tLJSGetEndorseSchemaLX);
                }

                String tPayStartDate = mLPPremSet.get(i).getPayStartDate();
                //��ȫNS��Ŀ�˱����һ���μӷѵĽ�������С�ڸ����εĽ�������ʱҪ���㵱ǰҪ�ӷѵĽ�����Ϣ������ȫ���Ĳ��˷ѱ����ύһ�ʽ�����
                if (mEdorType.equals("NS") && tPayStartDate != null)
                {
                    double intervalMoney = mLPPremSet.get(i).getPrem(); //���Ѳ��
                    double bfMoney = 0; //���ѱ���
                    double interestMoney = 0; //��Ϣ

//          if(tCurrentDate.compareTo(tPayStartDate)>0)
//		  {
                    //������Ϣ������
                    //��ȡ��������
//		  LMLoanDB tLMLoanDB = new LMLoanDB();
//		  tLMLoanDB.setRiskCode(mLCPolSchema.getRiskCode());
//		  if (!tLMLoanDB.getInfo())
//		  {
//			//?
//		  }
//		  AccountManage tAccountManage = new AccountManage();

                    //������Ϣ
                    //double interest = 0;
                    //interest = tAccountManage.getInterest(intervalMoney,tPayStartDate,PubFun.getCurrentDate(), tLMLoanDB.getInterestRate(), tLMLoanDB.getInterestMode(), tLMLoanDB.getInterestType(), "D");
                    //bfMoney = bfMoney + intervalMoney; //���ѱ���
                    //if (interest > 0) interestMoney = interestMoney + interest; //��Ϣ
                    //bfMoney = Double.parseDouble(new DecimalFormat("0.00").format(bfMoney));
                    //interestMoney = Double.parseDouble(new DecimalFormat("0.00").format(interestMoney));
//		  }

                    tTotalMoney = bfMoney + intervalMoney;
                    //�������Ĳ��˷ѱ��еĲ��Ѽ�¼
                    LJSGetEndorseSchema tLJSGetEndorseSchema = new LJSGetEndorseSchema();
                    LPPremSchema tLPPremSchema = new LPPremSchema();
                    tLPPremSchema = mLPPremSet.get(i);
                    tLJSGetEndorseSchema.setGetNoticeNo(mEdorNo); //����֪ͨ�����
                    tLJSGetEndorseSchema.setEndorsementNo(mEdorNo);
                    tLJSGetEndorseSchema.setFeeOperationType(tLPPremSchema.getEdorType());

                    mReflections.transFields(tLJSGetEndorseSchema, mLCPolSchema);

                    tLJSGetEndorseSchema.setGetDate(mLPEdorMainSchema.getEdorValiDate());
                    tLJSGetEndorseSchema.setGetMoney(intervalMoney); //�����
                    tLJSGetEndorseSchema.setFeeFinaType("BF"); //	�ӷ�
                    tLJSGetEndorseSchema.setPayPlanCode(tLPPremSchema.getPayPlanCode()); //������
                    tLJSGetEndorseSchema.setDutyCode(tLPPremSchema.getDutyCode()); //�����ã���һ��Ҫ��תljagetendorseʱ�ǿ�
                    tLJSGetEndorseSchema.setOtherNo(mEdorNo); //������
                    tLJSGetEndorseSchema.setOtherNoType("3"); //��ȫ����
                    tLJSGetEndorseSchema.setGetFlag("0");

                    tLJSGetEndorseSchema.setOperator(mGlobalInput.Operator);
                    tLJSGetEndorseSchema.setMakeDate(tCurrentDate);
                    tLJSGetEndorseSchema.setMakeTime(tCurrentTime);
                    tLJSGetEndorseSchema.setModifyDate(tCurrentDate);
                    tLJSGetEndorseSchema.setModifyTime(tCurrentTime);

                    //���ɱ�ȫ���Ĳ��˷ѱ���Ϣ��¼
//                    LJSGetEndorseSchema tLJSGetEndorseSchemaLX = new LJSGetEndorseSchema();
//                    tLJSGetEndorseSchemaLX.setGetNoticeNo(mEdorNo); //����֪ͨ�����
//                    tLJSGetEndorseSchemaLX.setEndorsementNo(mEdorNo);
//                    tLJSGetEndorseSchemaLX.setFeeOperationType(tLPPremSchema.getEdorType());
//
//                    mReflections.transFields(tLJSGetEndorseSchemaLX, mLCPolSchema);
//
//                    tLJSGetEndorseSchemaLX.setGetDate(mLPEdorMainSchema.getEdorValiDate());
//                    tLJSGetEndorseSchemaLX.setGetMoney(interestMoney);
//                    tLJSGetEndorseSchemaLX.setFeeFinaType("LX"); //	��Ϣ
//                    tLJSGetEndorseSchemaLX.setPayPlanCode(tLPPremSchema.getPayPlanCode()); //������
//                    tLJSGetEndorseSchemaLX.setDutyCode(tLPPremSchema.getDutyCode()); //�����ã���һ��Ҫ��תljagetendorseʱ�ǿ�
//                    tLJSGetEndorseSchemaLX.setOtherNo(mEdorNo); //������
//                    tLJSGetEndorseSchemaLX.setOtherNoType("3"); //��ȫ����
//                    tLJSGetEndorseSchemaLX.setGetFlag("0");
//
//                    tLJSGetEndorseSchemaLX.setOperator(mLCPolSchema.getManageCom());
//                    tLJSGetEndorseSchemaLX.setMakeDate(tCurrentDate);
//                    tLJSGetEndorseSchemaLX.setMakeTime(tCurrentTime);
//                    tLJSGetEndorseSchemaLX.setModifyDate(tCurrentDate);
                    //tLJSGetEndorseSchemaLX.setModifyTime(tCurrentTime);

                    mNewLJSGetEndorseSet.add(tLJSGetEndorseSchema);
//                    mNewLJSGetEndorseSet.add(tLJSGetEndorseSchemaLX);
                }
            }
        }

        //׼����ȫ�˱�������Ϣ
        LPUWMasterDB tLPUWMasterDB = new LPUWMasterDB();
	tLPUWMasterDB.setPolNo(mPolNo2);
        tLPUWMasterDB.setEdorNo(mEdorNo);
        tLPUWMasterDB.setEdorType(mEdorType);

        System.out.println(mPolNo2+"  "+ mEdorNo+"  "+mEdorType);
        if (!tLPUWMasterDB.getInfo())
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "prepareAdd";
            tError.errorMessage = "�ޱ�ȫ�����˱�������Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLPUWMasterSchema.setSchema(tLPUWMasterDB);
        if (mAddReason != null && !mAddReason.trim().equals(""))
        {
            mLPUWMasterSchema.setAddPremReason(mAddReason);
        }
        else
        {
            mLPUWMasterSchema.setAddPremReason("");
        }

        if (mLPPremSet != null && mLPPremSet.size() > 0)
        {
            mLPUWMasterSchema.setChangePolFlag("1"); //�мӷѱ�ʶ
        }
        else
        {
            mLPUWMasterSchema.setChangePolFlag("0"); //�޼ӷѱ�ʶ
        }

        mLPUWMasterSchema.setOperator(mOperater);
        mLPUWMasterSchema.setManageCom(mManageCom);
        mLPUWMasterSchema.setModifyDate(tCurrentDate);
        mLPUWMasterSchema.setModifyTime(tCurrentTime);

        //׼����ȫ����������Ϣ(���˱�ȷ��ʱ���޸�)
        //double tGetMoney = tTotalMoney + mLPEdorMainSchema.getGetMoney();
        //mLPEdorMainSchema.setGetMoney(tGetMoney);
        // mLPEdorMainSchema.setOperator(mOperater) ;
        // mLPEdorMainSchema.setManageCom(mManageCom);
        // mLPEdorMainSchema.setModifyDate(tCurrentDate) ;
        // mLPEdorMainSchema.setModifyTime(tCurrentTime);

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


        //ɾ����һ���ӷ�����
        if (mOldLPPremSet != null && mOldLPPremSet.size() > 0) {
          map.put(mOldLPPremSet, "DELETE");
        }

        //��ӱ��α�ȫ�ӷ�����
        if (mLPPremSet != null && mLPPremSet.size() > 0) {
          map.put(mLPPremSet, "INSERT");
        }

        //�޸ı��α�ȫ�ӷѺ���µı�����������
        if (mNewLPDutySet != null && mNewLPDutySet.size() > 0) {
          map.put(mNewLPDutySet, "UPDATE");
        }

        //�޸ı��α�ȫ�ӷѺ���µı�������
        if (mLPPolSchema != null) {
          map.put(mLPPolSchema, "UPDATE");
        }

        //׼��ɾ����һ�θ�NS��Ŀ�ı�ȫ���Ĳ��˷ѱ��б�ȫ�˹��˱��ӷѵ�����
        //׼��ɾ����һ�θ�NS��Ŀ�ı�ȫ���Ĳ��˷ѱ��б�ȫ�˹��˱��ӷѵ�����
        if (mOldLJSGetEndorseSet != null && mOldLJSGetEndorseSet.size() > 0)
        {
            map.put(mOldLJSGetEndorseSet, "DELETE");
        }

        //׼�������θ�NS��Ŀ�ı�ȫ���Ĳ��˷ѱ��б�ȫ�˹��˱��ӷѵ�����
        if (mNewLJSGetEndorseSet != null && mNewLJSGetEndorseSet.size() > 0)
        {
            map.put(mNewLJSGetEndorseSet, "INSERT");

        }

        //��ӱ�ȫ�����˱�����
        map.put(mLPUWMasterSchema, "UPDATE");

        mResult.add(map);
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

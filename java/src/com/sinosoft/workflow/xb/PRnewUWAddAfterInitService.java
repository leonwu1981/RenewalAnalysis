package com.sinosoft.workflow.xb;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
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
 * @version 1.0
 */

public class PRnewUWAddAfterInitService implements AfterInitService
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
    private String mPolNo;
    private String mPolNo2;
    private String mInsuredNo;
    private String mAddReason;
    private Reflections mReflections = new Reflections();

    /**ִ�б�ȫ�������ӷѻ������0000000002*/
    /**������*/
    private LCPolSchema mLCPolSchema = new LCPolSchema();
    /** ��ȫ�˱����� */
    private LCUWMasterSchema mLCUWMasterSchema = new LCUWMasterSchema();
    /** ������� */
    private LCDutySet mLCDutySet = new LCDutySet();
    private LCDutySet mNewLCDutySet = new LCDutySet();
    /** ���ѱ� */
    private LCPremSet mLCPremSet = new LCPremSet();
    private LCPremSet mOldLCPremSet = new LCPremSet();
    private LCPremSet mNewLCPremSet = new LCPremSet();
    /**��ȫ���Ĳ��˷ѱ�*/
    private LJSGetEndorseSet mOldLJSGetEndorseSet = new LJSGetEndorseSet();
    private LJSGetEndorseSet mNewLJSGetEndorseSet = new LJSGetEndorseSet();


    public PRnewUWAddAfterInitService()
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
        if (prepareAdd() == false)
        {
            return false;
        }

        return true;

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
            tError.moduleName = "PRnewUWAddAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mPolNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCPolSchema.setSchema(tLCPolDB);

        //У�鱣ȫ�����˱�����
        //У�鱣����Ϣ
        LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
        tLCUWMasterDB.setProposalNo(mPolNo);
        if (!tLCUWMasterDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PRnewUWAddAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mPolNo + "��ȫ�����˱�������Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCUWMasterSchema.setSchema(tLCUWMasterDB);

        // ����δ��ӡ״̬�ĺ˱�֪ͨ���ڴ�ӡ������ֻ����һ��
        // ������ͬһ���������ͣ�ͬһ���������룬ͬһ��������������
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

        tLOPRTManagerDB.setCode(PrintManagerBL.CODE_PRnewUW); //�˱�֪ͨ��
        tLOPRTManagerDB.setOtherNo(mPolNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_INDPOL); //������
        //tLOPRTManagerDB.setStandbyFlag2(mLCPolSchema.getPrtNo() );
        tLOPRTManagerDB.setStateFlag("0");

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWAddAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "��ѯ��ӡ�������Ϣ����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLOPRTManagerSet.size() != 0)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWAddAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "�ڴ�ӡ����������һ������δ��ӡ״̬�ĺ˱�֪ͨ��!";
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
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewUWAddAfterInitService";
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
            tError.moduleName = "PRnewUWAddAfterInitService";
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
            tError.moduleName = "PRnewUWAddAfterInitService";
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
            tError.moduleName = "PRnewUWAddAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mPolNo = (String) mTransferData.getValueByName("PolNo");
        if (mPolNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewUWAddAfterInitService";
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
            tError.moduleName = "PRnewUWAddAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������PolNo2ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mAddReason = (String) mTransferData.getValueByName("AddReason");
//	if ( mAddReason == null  )
//	{
//	  // @@������
//	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
//	  CError tError = new CError();
//	  tError.moduleName = "PRnewUWAddAfterInitService";
//	  tError.functionName = "getInputData";
//	  tError.errorMessage = "ǰ̨����ҵ��������AddReasonʧ��!";
//	  this.mErrors .addOneError(tError) ;
//	  return false;
//	}//ע�����ǵ�:�ӷѺ��ֳ���ʱ,ͨ���˱�ʦ����������ӷ�ԭ��.



        //���ҵ�����֪ͨ����
        mLCPremSet = (LCPremSet) mTransferData.getValueByName("LCPremSet");
//	if ( mLPPremSet == null  )
//	{
//	  // @@������
//	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
//	  CError tError = new CError();
//	  tError.moduleName = "PRnewUWAddAfterInitService";
//	  tError.functionName = "getInputData";
//	  tError.errorMessage = "ǰ̨������ҵ��ӷ�����ʧ��!";
//	  this.mErrors .addOneError(tError) ;
//	  return false;
//	}

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

        //ȡ��������
        LMRiskSchema tLMRiskSchema = new LMRiskSchema();
        LMRiskDB tLMRiskDB = new LMRiskDB();
        tLMRiskDB.setRiskCode(mLCPolSchema.getRiskCode());
        //tLMRiskDB.setRiskVer(mLCPolSchema.getRiskVersion());
        if (!tLMRiskDB.getInfo())
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWAddAfterInitService";
            tError.functionName = "prepareHealth";
            tError.errorMessage = "ȡ��������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ȡ����������
        LAAgentDB tLAAgentDB = new LAAgentDB();
        tLAAgentDB.setAgentCode(mLCPolSchema.getAgentCode());
        if (!tLAAgentDB.getInfo())
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWAddAfterInitService";
            tError.functionName = "prepareHealth";
            tError.errorMessage = "ȡ����������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //�γɼӷ���Ϣ
        if (mLCPremSet.size() > 0)
        {
            //ȡ������Ϣ
            LCDutyDB tLCDutyDB = new LCDutyDB();
            tLCDutyDB.setPolNo(mLCPolSchema.getPolNo());
            mLCDutySet = tLCDutyDB.query();

            //�����ȥ���������ӷ���Ŀ,�б�ʱ�Ļ���������󣬸ñ����ڸ������ļӷ���Ŀ�����Ա���㱾�������ӷѵı�����ʼ����ֵ.
            String tsql = "select count(*) from LCPrem where  polno = '"
                    + mLCPolSchema.getPolNo().trim() + "'  and state in ('1','3')";
            String tReSult = new String();
            ExeSQL tExeSQL = new ExeSQL();
            tReSult = tExeSQL.getOneValue(tsql);
            if (tExeSQL.mErrors.needDealError())
            {
                // @@������
                this.mErrors.copyAllErrors(tExeSQL.mErrors);
                CError tError = new CError();
                tError.moduleName = "PRnewUWAddAfterInitService";
                tError.functionName = "prepareAdd";
                tError.errorMessage = "ִ��SQL��䣺" + tsql + "ʧ��!";
                this.mErrors.addOneError(tError);
                return false;
            }
            if (tReSult == null || tReSult.equals(""))
            {
                return false;
            }

            int tCount = 0;
            tCount = Integer.parseInt(tReSult); //�Ѱ����˱��νڵ㼰���ͬ���ڵ�

            //����������
            if (mLCDutySet.size() > 0)
            {
                for (int m = 1; m <= mLCDutySet.size(); m++)
                {
                    int maxno = 0;
                    LCDutySchema tLCDutySchema = new LCDutySchema();
                    tLCDutySchema = mLCDutySet.get(m);

                    //��ȥ�����ε�ԭ���������ӷѽ��
                    String sql =
                            "select * from LCPrem where payplancode  like '000000%' and polno = '"
                            + mLCPolSchema.getPolNo().trim() + "' and dutycode = '"
                            + tLCDutySchema.getDutyCode().trim() + "'and state = '1'";
                    LCPremDB tLCPremDB = new LCPremDB();
                    LCPremSet tLCPremSet = new LCPremSet();

                    tLCPremSet = tLCPremDB.executeQuery(sql);

                    if (tLCPremSet.size() > 0)
                    {
                        for (int j = 1; j <= tLCPremSet.size(); j++)
                        {
                            LCPremSchema tLCPremSchema = new LCPremSchema();
                            tLCPremSchema = tLCPremSet.get(j);

                            tLCDutySchema.setPrem(tLCDutySchema.getPrem() - tLCPremSchema.getPrem());
                            mLCPolSchema.setPrem(mLCPolSchema.getPrem() - tLCPremSchema.getPrem());
                        }
                    }

                    //ΪͶ����������α���ϱ��εļӷ�.ͬʱ�γɼӷ���Ϣ
                    for (int i = 1; i <= mLCPremSet.size(); i++)
                    {
                        double tPrem;

                        if (mLCPremSet.get(i).getDutyCode().equals(tLCDutySchema.getDutyCode()))
                        {
                            maxno = maxno + 1;
                            //�γɼӷѱ���
                            String PayPlanCode = "";
                            PayPlanCode = String.valueOf(maxno + tCount);
                            for (int j = PayPlanCode.length(); j < 8; j++)
                            {
                                PayPlanCode = "0" + PayPlanCode;
                            }

                            //�����ܱ���
                            tPrem = mLCPolSchema.getPrem() + mLCPremSet.get(i).getPrem();
                            tSumStandPrem = tSumStandPrem + mLCPremSet.get(i).getPrem();
                            //mLCPremSet.get(i).setPolNo(mLCPolSchema.getPolNo());//����ע������������Ϣ��ǰ̨������Ϣ
                            //mLCPremSet.get(i).setDutyCode();
                            mLCPremSet.get(i).setPayPlanCode(PayPlanCode);
                            /*Lis5.3 upgrade set
                                  mLCPremSet.get(i).setGrpPolNo("00000000000000000000");
                             mLCPremSet.get(i).setMult(tLCDutySchema.getMult());
                             */
                            //mLCPremSet.get(i).setPayPlanType();
                            //mLCPremSet.get(i).setPayTimes();
                            mLCPremSet.get(i).setPayIntv(tLCDutySchema.getPayIntv());

                            mLCPremSet.get(i).setStandPrem(mLCPremSet.get(i).getPrem());
                            //mLCPremSet.get(i).setPrem();
                            mLCPremSet.get(i).setSumPrem("0");
                            //mLCPremSet.get(i).setRate();
                            //mLCPremSet.get(i).setPayStartDate();
                            //mLCPremSet.get(i).setPayEndDate();
                            mLCPremSet.get(i).setPaytoDate(mLCPremSet.get(i).getPayStartDate());
                            mLCPremSet.get(i).setState("1"); //0:�б�ʱ�ı����1:�б�ʱ�ļӷ��2������������Ŀ�����ӷ��3��ǰ���β�ͨ�����µ������ӷѣ�
                            mLCPremSet.get(i).setUrgePayFlag("Y"); //�ӷ���һ��Ҫ�߽���������ȥȡ�������������Ĵ߽���־��
                            mLCPremSet.get(i).setManageCom(mLCPolSchema.getManageCom());
                            mLCPremSet.get(i).setAppntNo(mLCPolSchema.getAppntNo());
                            mLCPremSet.get(i).setAppntType("1"); //����Ͷ��
                            mLCPremSet.get(i).setModifyDate(PubFun.getCurrentDate());
                            mLCPremSet.get(i).setModifyTime(PubFun.getCurrentTime());

                            //���±�������
                            tLCDutySchema.setPrem(tLCDutySchema.getPrem()
                                    + mLCPremSet.get(i).getPrem());
                            //���±�������
                            mLCPolSchema.setPrem(tPrem);
                        }
                    }
                    mNewLCDutySet.add(tLCDutySchema);

                }
            }

        }

        //׼��ɾ����һ�θ���Ŀ�ļӷѵ�����
        String tSQL = "select * from lcprem where polno = '" + mPolNo2 + "'"
                + " and substr(payplancode,1,6) = '000000'"
                + " and state = '1'"; //0:�б�ʱ�ı����1:�б�ʱ�ļӷ��2������������Ŀ�����ӷ��3��ǰ���β�ͨ�����µ������ӷѣ�
        LCPremDB tLCPremDB = new LCPremDB();
        mOldLCPremSet = tLCPremDB.executeQuery(tSQL);
        if (mOldLCPremSet == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWAddAfterInitService";
            tError.functionName = "prepareAdd";
            tError.errorMessage = "��ѯ�����ӷ���Ϣʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //׼��ɾ����һ�θ�NS��Ŀ���������Ĳ��˷ѱ��������˹��˱��ӷѵ�����
//	tSQL = "select * from ljsgetendorse where getnoticeno = '" + mEdorNo + "'"
//	  +" and endorsementno = '" + mEdorNo +"'"
//	  +" and feeoperationtype = '" + mLPPremSet.get(1).getEdorType() + "'"
//	  +" and substr(payplancode,1,6) = '000000'"
//	  +" and payplancode <> '00000000'"
//	  +" and polno = '" + mPolNo2 + "'";
//	LJSGetEndorseDB tLJSGetEndorseDB = new LJSGetEndorseDB();
//	mOldLJSGetEndorseSet = tLJSGetEndorseDB.executeQuery(tSQL) ;
//	if(mOldLJSGetEndorseSet == null)
//	{
//	  // @@������
//	  CError tError = new CError();
//	  tError.moduleName = "PRnewUWAddAfterInitService";
//	  tError.functionName = "prepareAdd";
//	  tError.errorMessage = "��ѯ�����ӷ���Ϣʧ��!";
//	  this.mErrors .addOneError(tError) ;
//	  return false;
//	}

        //׼����Ӹ���Ŀ���������Ĳ��˷ѱ��������˹��˱��ӷѵ�����
        if (tSumStandPrem > 0 && mLCPremSet != null && mLCPremSet.size() > 0)
        {

            for (int i = 1; i <= mLCPremSet.size(); i++)
            {
                LCDutyDB tLCDutyDB = new LCDutyDB();
                LCDutySchema tLCDutySchema = new LCDutySchema();
                tLCDutyDB.setDutyCode(mLCPremSet.get(i).getDutyCode());
                tLCDutyDB.setPolNo(mLCPremSet.get(i).getPolNo());
                LCDutySet tLCDutySet = tLCDutyDB.query();
                if (tLCDutySet == null)
                {
                    // @@������
                    CError tError = new CError();
                    tError.moduleName = "PRnewUWAddAfterInitService";
                    tError.functionName = "prepareAdd";
                    tError.errorMessage = "��ѯ���α���Ϣʧ��!";
                    this.mErrors.addOneError(tError);
                    return false;
                }
                tLCDutySchema = tLCDutySet.get(1);
                String tDutyPaytoDate = tLCDutySchema.getPaytoDate();
                String tPremPaytoDate = mLCPremSet.get(i).getPaytoDate();

//		//�����˱����һ���μӷѵĽ�������С�ڸ����εĽ�������ʱҪ���㵱ǰҪ�ӷѵĽ�����Ϣ�����������Ĳ��˷ѱ����ύһ�ʽ�����
//		if(tDutyPaytoDate != null && tPremPaytoDate != null && tDutyPaytoDate.compareTo(tPremPaytoDate)>0 )
//		{
//
//		  //������Ϣ������
//		  //�������½��Ѽ�����㽻������
//		  int premNum = 0;
//		  if (mLCPolSchema.getPayIntv() == 0)
//		  {
//			premNum = 1;
//		  }
//		  else
//		  {
//			premNum = PubFun.calInterval(mLCPolSchema.getCValiDate(), mLCPolSchema.getPaytoDate(), "M") / mLCPolSchema.getPayIntv();
//		  }
//
//		  double intervalMoney = mLCPremSet.get(i).getPrem(); //���Ѳ��
//		  double bfMoney = 0; //���ѱ���
//		  double interestMoney = 0; //��Ϣ
//
//		  //��ȡ��������
//		  LMLoanDB tLMLoanDB = new LMLoanDB();
//		  tLMLoanDB.setRiskCode(mLCPolSchema.getRiskCode());
//		  if (!tLMLoanDB.getInfo())
//		  {
//			//?
//		  }
//		  AccountManage tAccountManage = new AccountManage();
//
//		  //������Ϣ
//		  double interest = 0;
//		  for (int j=0; j<premNum; j++)
//		  {
//			if (j == 0)
//			{
//			interest = tAccountManage.getInterest(intervalMoney, mLCPolSchema.getCValiDate(), PubFun.getCurrentDate(), tLMLoanDB.getInterestRate(), tLMLoanDB.getInterestMode(), tLMLoanDB.getInterestType(), "D");
//		  }
//		  else
//		  {
//			interest = tAccountManage.getInterest(intervalMoney, PubFun.calDate(mLCPolSchema.getCValiDate(), j*mLCPolSchema.getPayIntv(), "M", mLCPolSchema.getCValiDate()), PubFun.getCurrentDate(), tLMLoanDB.getInterestRate(), tLMLoanDB.getInterestMode(), tLMLoanDB.getInterestType(), "D");
//		  }
//		  bfMoney = bfMoney + intervalMoney; //���ѱ���
//		  if (interest > 0) interestMoney = interestMoney + interest; //��Ϣ
//		  bfMoney = Double.parseDouble(new DecimalFormat("0.00").format(bfMoney));
//		  interestMoney = Double.parseDouble(new DecimalFormat("0.00").format(interestMoney));
//		  }
//
//		  tTotalMoney = bfMoney + interestMoney;
//		  //�������Ĳ��˷ѱ��еĲ��Ѽ�¼
//		  LJSGetEndorseSchema tLJSGetEndorseSchema = new LJSGetEndorseSchema();
//		  LCPremSchema tLCPremSchema = new LCPremSchema();
//		  tLCPremSchema = mLCPremSet.get(i) ;
//		  tLJSGetEndorseSchema.setGetNoticeNo(mEdorNo);  //����֪ͨ�����
//		  tLJSGetEndorseSchema.setEndorsementNo(mEdorNo);
//		  tLJSGetEndorseSchema.setFeeOperationType(tLPPremSchema.getEdorType());
//
//		  mReflections.transFields(tLJSGetEndorseSchema, mLCPolSchema);
//
//		  tLJSGetEndorseSchema.setGetDate(mLPRnewMainSchema.getEdorValiDate());
//		  tLJSGetEndorseSchema.setGetMoney(bfMoney);//�����
//		  tLJSGetEndorseSchema.setFeeFinaType("BF"); //	�ӷ�
//		  tLJSGetEndorseSchema.setPayPlanCode(tLPPremSchema.getPayPlanCode());  //������
//		  tLJSGetEndorseSchema.setDutyCode(tLPPremSchema.getDutyCode() );     //�����ã���һ��Ҫ��תljagetendorseʱ�ǿ�
//		  tLJSGetEndorseSchema.setOtherNo(mEdorNo);      //������
//		  tLJSGetEndorseSchema.setOtherNoType("3");  //��������
//		  tLJSGetEndorseSchema.setGetFlag("0");
//
//		  tLJSGetEndorseSchema.setOperator(mGlobalInput.Operator);
//		  tLJSGetEndorseSchema.setMakeDate(tCurrentDate);
//		  tLJSGetEndorseSchema.setMakeTime(tCurrentTime);
//		  tLJSGetEndorseSchema.setModifyDate(tCurrentDate);
//		  tLJSGetEndorseSchema.setModifyTime(tCurrentTime);
//
//		  //�����������Ĳ��˷ѱ���Ϣ��¼
//		  LJSGetEndorseSchema tLJSGetEndorseSchemaLX = new LJSGetEndorseSchema();
//		  tLJSGetEndorseSchemaLX.setGetNoticeNo(mEdorNo);  //����֪ͨ�����
//		  tLJSGetEndorseSchemaLX.setEndorsementNo(mEdorNo);
//		  tLJSGetEndorseSchemaLX.setFeeOperationType(tLPPremSchema.getEdorType());
//
//		  mReflections.transFields(tLJSGetEndorseSchemaLX, mLCPolSchema);
//
//		  tLJSGetEndorseSchemaLX.setGetDate(mLPRnewMainSchema.getEdorValiDate());
//		  tLJSGetEndorseSchemaLX.setGetMoney(interestMoney);
//		  tLJSGetEndorseSchemaLX.setFeeFinaType("LX"); //	��Ϣ
//		  tLJSGetEndorseSchemaLX.setPayPlanCode(tLPPremSchema.getPayPlanCode());  //������
//		  tLJSGetEndorseSchemaLX.setDutyCode(tLPPremSchema.getDutyCode() );     //�����ã���һ��Ҫ��תljagetendorseʱ�ǿ�
//		  tLJSGetEndorseSchemaLX.setOtherNo(mEdorNo);      //������
//		  tLJSGetEndorseSchemaLX.setOtherNoType("3");  //��������
//		  tLJSGetEndorseSchemaLX.setGetFlag("0");
//
//		  tLJSGetEndorseSchemaLX.setOperator(mLCPolSchema.getManageCom() );
//		  tLJSGetEndorseSchemaLX.setMakeDate(tCurrentDate);
//		  tLJSGetEndorseSchemaLX.setMakeTime(tCurrentTime);
//		  tLJSGetEndorseSchemaLX.setModifyDate(tCurrentDate);
//		  tLJSGetEndorseSchemaLX.setModifyTime(tCurrentTime);
//
//		  mNewLJSGetEndorseSet.add(tLJSGetEndorseSchema) ;
//		  mNewLJSGetEndorseSet.add(tLJSGetEndorseSchemaLX) ;
//		}
            }
        }

        //׼�������˱�������Ϣ
        LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
        tLCUWMasterDB.setProposalNo(mPolNo);
        if (tLCUWMasterDB.getInfo() == false)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWAddAfterInitService";
            tError.functionName = "prepareAdd";
            tError.errorMessage = "�����������˱�������Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCUWMasterSchema.setSchema(tLCUWMasterDB);
        if (mAddReason != null && !mAddReason.trim().equals(""))
        {
            mLCUWMasterSchema.setAddPremReason(mAddReason);
        }
        else
        {
            mLCUWMasterSchema.setAddPremReason("");
        }

        if (mLCPremSet != null && mLCPremSet.size() > 0)
        {
            mLCUWMasterSchema.setSpecFlag("1"); //�мӷѱ�ʶ
        }
        else
        {
            mLCUWMasterSchema.setSpecFlag("0"); //�޼ӷѱ�ʶ
        }

        mLCUWMasterSchema.setOperator(mOperater);
        mLCUWMasterSchema.setManageCom(mManageCom);
        mLCUWMasterSchema.setModifyDate(tCurrentDate);
        mLCUWMasterSchema.setModifyTime(tCurrentTime);

        //׼����������������Ϣ(���˱�ȷ��ʱ���޸�)
        //double tGetMoney = tTotalMoney + mLPRnewMainSchema.getGetMoney();
        //mLPRnewMainSchema.setGetMoney(tGetMoney);
        // mLPRnewMainSchema.setOperator(mOperater) ;
        // mLPRnewMainSchema.setManageCom(mManageCom);
        // mLPRnewMainSchema.setModifyDate(tCurrentDate) ;
        // mLPRnewMainSchema.setModifyTime(tCurrentTime);

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
        if (mOldLCPremSet != null && mOldLCPremSet.size() > 0)
        {
            map.put(mOldLCPremSet, "DELETE");
        }

        //��ӱ��������ӷ�����
        if (mLCPremSet != null && mLCPremSet.size() > 0)
        {
            map.put(mLCPremSet, "INSERT");
        }

        //�޸ı��������ӷѺ���µı�����������
        if (mNewLCDutySet != null && mNewLCDutySet.size() > 0)
        {
            map.put(mNewLCDutySet, "UPDATE");
        }

        //�޸ı��������ӷѺ���µı�������
        if (mLCPolSchema != null)
        {
            map.put(mLCPolSchema, "UPDATE");
        }

//	//׼��ɾ����һ�θ�NS��Ŀ���������Ĳ��˷ѱ��������˹��˱��ӷѵ�����
//	if(mOldLJSGetEndorseSet != null && mOldLJSGetEndorseSet.size()>0)
//	{
//	  map.put(mOldLJSGetEndorseSet, "DELETE");
//	}
//
//	//׼�������θ�NS��Ŀ���������Ĳ��˷ѱ��������˹��˱��ӷѵ�����
//	if(mNewLJSGetEndorseSet != null && mNewLJSGetEndorseSet.size()>0)
//	{
//	  map.put(mNewLJSGetEndorseSet, "INSERT");
//
//	}

        //������������˱�����
        map.put(mLCUWMasterSchema, "UPDATE");

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

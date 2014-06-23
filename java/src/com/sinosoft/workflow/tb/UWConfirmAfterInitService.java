/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.cbcheck.SplitFamilyBL;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.finfee.TempFeeWithdrawBL;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: ����Լ�˱�ȷ�� </p>
 * <p>Description: ������������:ִ������Լ�˱�ȷ��</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */
public class UWConfirmAfterInitService implements AfterInitService
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    private VData mReturnFeeData;


    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
    private MMap mSplitMap = new MMap();

    /** ���ݲ����ַ��� */
    private String mManageCom;
    private String mCalCode; //�������

    /** ҵ������ر��� */
    private LCPolSet mLCPolSet = new LCPolSet();
    private LCPolSchema mLCPolSchema = new LCPolSchema();
    private String mOldPolNo = "";
    private String mUWFlag = ""; //�˱���־
    private String mUWIdea = ""; //�˱����
    private String mStopFlag = "";
    private String mUWPopedom = ""; //����Ա�˱�����
    private String mAppGrade = ""; //�ϱ�����
//    private String mPolType = ""; //��������
    private String mNewContNo = "";

    //modify by Minim
    private String mBackUWGrade = "";
    private String mBackAppGrade = "";
    private String mOperator = "";
    private String mOperatorUWGrade = "";
    private Reflections mReflections = new Reflections();
    private MMap mmap = new MMap();
    private String mPrtNo = "";

    private LJSPaySet outLJSPaySet = new LJSPaySet();
    private LJTempFeeSet outLJTempFeeSet = new LJTempFeeSet();
    private LBTempFeeSet outLBTempFeeSet = new LBTempFeeSet();
    private LJTempFeeClassSet outLJTempFeeClassSet = new LJTempFeeClassSet();
    private LBTempFeeClassSet outLBTempFeeClassSet = new LBTempFeeClassSet();


    /** �˱����� */
    private LCCUWMasterSet mLCCUWMasterSet = new LCCUWMasterSet();
    private LCUWMasterSet mLCUWMasterSet = new LCUWMasterSet();


    /** �˱��ӱ� */
    private LCUWSubSet mLCUWSubSet = new LCUWSubSet();
    private LCCUWSubSet mLCCUWSubSet = new LCCUWSubSet();


    /** ��ӡ����� */
    private LOPRTManagerSet mLOPRTManagerSet = new LOPRTManagerSet();

    private LMUWSet mLMUWSet = new LMUWSet();
    private CalBase mCalBase = new CalBase();
    private GlobalInput mGlobalInput = new GlobalInput();
    private String mGetNoticeNo = "";
    private TransferData mTransferData = new TransferData();


    /** ���ݲ����ַ��� */
    private String mOperater;


    /** ҵ�����ݲ����ַ��� */
    private String mContNo;
    private String mMissionID;
    private String Poluwflagsql="";


    /**������*/
    private LCContSchema mLCContSchema = new LCContSchema();

    public UWConfirmAfterInitService()
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

        //У��
        if (!checkData())
        {
            return false;
        }

        //���ɸ���֪ͨ���
        String tLimit = PubFun.getNoLimit(mManageCom);
        mGetNoticeNo = PubFun1.CreateMaxNo("GETNOTICENO", tLimit); //��������֪ͨ���
//        System.out.println("---tLimit---" + tLimit);

        //�ܱ������ڻ򳷵�ʱ��У��������;����
        if (mUWFlag.equals("a") || mUWFlag.equals("1") || mUWFlag.equals("8"))
        {
            //��ѯӦ���ܱ�����
//            String strSql =
//                    "select * from ljspay where trim(otherno) in (select trim(contno) from lccont where prtno='" +
//                    mPrtNo + "' union select trim(proposalcontno) from lccont where prtno='" +
//                    mPrtNo + "' union select trim(prtno) from lccont where prtno='" +
//                    mPrtNo + "' )";
            StringBuffer tSBql = new StringBuffer(128);
            tSBql.append(
                    "select * from ljspay where trim(otherno) in (select trim(contno) from lccont where prtno='");
            tSBql.append(mPrtNo);
            tSBql.append("' union select trim(proposalcontno) from lccont where prtno='");
            tSBql.append(mPrtNo);
            tSBql.append("' union select trim(prtno) from lccont where prtno='");
            tSBql.append(mPrtNo);
            tSBql.append("' )");
//            System.out.println("strSql=" + strSql);
            outLJSPaySet = (new LJSPayDB()).executeQuery(tSBql.toString());

            for (int i = 0; i < outLJSPaySet.size(); i++)
            {
                if (outLJSPaySet.get(i + 1).getBankOnTheWayFlag().equals("1"))
                {
                    System.out.println("��������;���ݣ�������ܱ������ڻ򳷵�!");
                    // @@������
                    CError tError = new CError();
                    tError.moduleName = "UWManuNormChkBL";
                    tError.functionName = "submitData";
                    tError.errorMessage = "��������;���ݣ�������ܱ������ڻ򳷵�!";
                    this.mErrors.addOneError(tError);
                    return false;
                }
            }

            //��ѯ�ݽ��ѱ�����
//            strSql =
//                    "select * from ljtempfee where EnterAccDate is not null and trim(otherno) in (select trim(contno) from lccont where prtno='" +
//                    mPrtNo + "' union select trim(proposalcontno) from lccont where prtno='" +
//                    mPrtNo + "' union select trim(prtno) from lccont where prtno='" + mPrtNo
//                    + "' )";
            tSBql = new StringBuffer(128);
            tSBql.append("select * from ljtempfee where EnterAccDate is not null and trim(otherno) in (select trim(contno) from lccont where prtno='");
            tSBql.append(mPrtNo);
            tSBql.append("' union select trim(proposalcontno) from lccont where prtno='");
            tSBql.append(mPrtNo);
            tSBql.append("' union select trim(prtno) from lccont where prtno='");
            tSBql.append(mPrtNo);
            tSBql.append("' )");
//            System.out.println("strSql=" + strSql);
            outLJTempFeeSet = (new LJTempFeeDB()).executeQuery(tSBql.toString());

            if (outLJTempFeeSet.size() > 0)
            {
                for (int i = 0; i < outLJTempFeeSet.size(); i++)
                {
                    LBTempFeeSchema tLBTempFeeSchema = new LBTempFeeSchema();
                    mReflections.transFields(tLBTempFeeSchema, outLJTempFeeSet.get(i + 1));
                    tLBTempFeeSchema.setBackUpSerialNo(PubFun1.CreateMaxNo("LBTempFee", 20));
                    outLBTempFeeSet.add(tLBTempFeeSchema);

                    LJTempFeeClassDB tLJTempFeeClassDB = new LJTempFeeClassDB();
                    tLJTempFeeClassDB.setTempFeeNo(outLJTempFeeSet.get(i + 1).getTempFeeNo());
                    outLJTempFeeClassSet.add(tLJTempFeeClassDB.query());
                }

                for (int i = 0; i < outLJTempFeeClassSet.size(); i++)
                {
                    LBTempFeeClassSchema tLBTempFeeClassSchema = new LBTempFeeClassSchema();
                    mReflections.transFields(tLBTempFeeClassSchema, outLJTempFeeClassSet.get(i + 1));
                    tLBTempFeeClassSchema.setBackUpSerialNo(PubFun1.CreateMaxNo("LBTFClass", 20));
                    outLBTempFeeClassSet.add(tLBTempFeeClassSchema);
                }
            }
        }

        if (mUWFlag.equals("1") || mUWFlag.equals("4") || mUWFlag.equals("8") || mUWFlag.equals("9"))
        {
            //�α�׼��У��˱�Ա����
            if (!checkStandGrade())
            {
                return false;
            }
        }

        //�ܱ�������ҪУ��˱�Ա����
        if (mUWFlag.equals("1") || mUWFlag.equals("8"))
        {
            if (!checkUserGrade())
            {
                return false;
            }
        }

        //����ҵ����
        if (!dealData())
        {
            return false;
        }

        //�����˷Ѵ��� �Լ��޸ı������uwflag
//        System.out.println("Start Return Tempfee");
        if (mUWFlag.equals("a") || mUWFlag.equals("1") || mUWFlag.equals("8"))
        {
            if (!returnFee())
            {
                return false;
            }
        //�޸ı������uwflag
            Poluwflagsql = "update lcpol set uwflag='"+ mUWFlag +"' where contno='"+ mContNo +"'";
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

//        System.out.println("Start  Submit...");

        //mResult.clear();
        return true;
    }


    /**
     * ���ݲ�������ҵ����
     * �������������򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean returnFee()
    {
//        System.out.println("============In ReturnFee");

        String payMode = ""; //���ѷ�ʽ
        String BankCode = ""; //���б���
        String BankAccNo = ""; //�����˺�
        String AccName = ""; //����

        //׼��TransferData����
//        String strSql = "";
        //���Ը�Ͷ�����Ƿ����ݽ��Ѵ���
//        strSql = "select * from ljtempfee where trim(otherno) in (select '" + mPrtNo
//                + "' from dual ) and EnterAccDate is not null and confdate is  null";
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from ljtempfee where trim(otherno) in (select '");
        tSBql.append(mPrtNo);
        tSBql.append("' from dual ) and EnterAccDate is not null and confdate is  null");

        LJTempFeeDB sLJTempFeeDB = new LJTempFeeDB();
        LJTempFeeSet sLJTempFeeSet = new LJTempFeeSet();
        sLJTempFeeSet = sLJTempFeeDB.executeQuery(tSBql.toString());
//        System.out.println("�ݽ�������:  " + sLJTempFeeSet.size());
        if (sLJTempFeeSet.size() == 0)
        {
            System.out.println("Out ReturnFee");
            return true;
        }

        //���֪ͨ��Ų�Ϊ�գ��ҳ��˷ѷ�ʽ�����ȼ�����Ϊ֧Ʊ�����У��ֽ�
        GetPayType tGetPayType = new GetPayType();
        if (tGetPayType.getPayTypeForLCPol(mPrtNo))
        {
            payMode = tGetPayType.getPayMode(); //���ѷ�ʽ
            BankCode = tGetPayType.getBankCode(); //���б���
            BankAccNo = tGetPayType.getBankAccNo(); //�����˺�
            AccName = tGetPayType.getAccName(); //����
        }
        else
        {
            // @@������
            this.mErrors.copyAllErrors(tGetPayType.mErrors);
            System.out.println("��ѯ������Ϣ  :" + tGetPayType.mErrors);
            return false;
        }

        TransferData sTansferData = new TransferData();
        sTansferData.setNameAndValue("PayMode", payMode);
        sTansferData.setNameAndValue("NotBLS", "1");
        if (payMode.equals("1"))
        {
            sTansferData.setNameAndValue("BankFlag", "0");
        }
        else
        {
            sTansferData.setNameAndValue("BankCode", BankCode);
            sTansferData.setNameAndValue("AccNo", BankAccNo);
            sTansferData.setNameAndValue("AccName", AccName);
            sTansferData.setNameAndValue("BankFlag", "1");
        }
        sTansferData.setNameAndValue("GetNoticeNo", mGetNoticeNo);

        LJTempFeeSet tLJTempFeeSet = new LJTempFeeSet();
        LJAGetTempFeeSet tLJAGetTempFeeSet = new LJAGetTempFeeSet();

        for (int index = 1; index <= sLJTempFeeSet.size(); index++)
        {
            System.out.println("HaveDate In Second1");
            LJTempFeeSchema tLJTempFeeSchema = new LJTempFeeSchema();
            tLJTempFeeSchema.setTempFeeNo(sLJTempFeeSet.get(index).getTempFeeNo());
            tLJTempFeeSchema.setTempFeeType(sLJTempFeeSet.get(index).getTempFeeType());
            tLJTempFeeSchema.setRiskCode(sLJTempFeeSet.get(index).getRiskCode());
            tLJTempFeeSet.add(tLJTempFeeSchema);

            LJAGetTempFeeSchema tLJAGetTempFeeSchema = new LJAGetTempFeeSchema();
            //tLJAGetTempFeeSchema.setGetReasonCode(mAllLCUWMasterSet.get(1).getUWIdea());
            tLJAGetTempFeeSchema.setGetReasonCode("99");

            tLJAGetTempFeeSet.add(tLJAGetTempFeeSchema);

            //��ӡ�˷�֪ͨ��
            // setLOPRTManager(tLJTempFeeSchema);

//            System.out.println("HaveDate In Second2");
//            try
//            {
//                System.out.println("TempFeeNo:  " + sLJTempFeeSet.get(index).getTempFeeNo());
//                System.out.println("TempFeeType:  " + sLJTempFeeSet.get(index).getTempFeeType());
//                System.out.println("RiskCode:  " + sLJTempFeeSet.get(index).getRiskCode());
//            }
//            catch (Exception e)
//            {
//                System.out.println("����������!");
//            }
        }

        // ׼���������� VData
        VData tVData = new VData();
        tVData.add(tLJTempFeeSet);
        tVData.add(tLJAGetTempFeeSet);
        tVData.add(sTansferData);
        tVData.add(mGlobalInput);

        // ���ݴ���
//        System.out.println("--------��ʼ��������---------");
        TempFeeWithdrawBL tTempFeeWithdrawBL = new TempFeeWithdrawBL();
        tTempFeeWithdrawBL.submitData(tVData, "INSERT");
//        if (tTempFeeWithdrawBL.submitData(tVData, "INSERT"))
//        {
//            System.out.println("---ok---");
//        }
//        else
//        {
//            System.out.println("---NO---");
//        }

        if (tTempFeeWithdrawBL.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tTempFeeWithdrawBL.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWManuNormChkBL";
            tError.functionName = "submitData";
            tError.errorMessage = "�˱��ɹ�,���˷�ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mReturnFeeData = tTempFeeWithdrawBL.getResult();

        mmap = (MMap) mReturnFeeData.getObjectByObjectName("MMap", 0);

//        System.out.println("Out ReturnFee");
        return true;
    }


//    /**
//     * ���ô�ӡ���ݱ�
//     * @param tLJTempFeeSchema LJTempFeeSchema
//     * @return boolean
//     */
//    private boolean setLOPRTManager(LJTempFeeSchema tLJTempFeeSchema)
//    {
//        System.out.println("----------setLOPRTManager----------");
//        //3-׼����ӡ����,����ӡˢ��ˮ��
//        LOPRTManagerSet outLOPRTManagerSet = new LOPRTManagerSet();
//        String tLimit = PubFun.getNoLimit(mManageCom);
//        String tNo = PubFun1.CreateMaxNo("GETNO", tLimit);
//        String prtSeqNo = "";
//        String mCurrentDate = PubFun.getCurrentDate();
//        String mCurrentTime = PubFun.getCurrentTime();
//
//        LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();
//        prtSeqNo = PubFun1.CreateMaxNo("PRTSEQNO", tLimit);
//        mLOPRTManagerSchema.setPrtSeq(prtSeqNo);
//        mLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_GET);
//        mLOPRTManagerSchema.setOtherNo(tNo); //ʵ������
//        System.out.println("otherno:" + tNo);
//        mLOPRTManagerSchema.setMakeDate(mCurrentDate);
//        mLOPRTManagerSchema.setMakeTime(mCurrentTime);
//        mLOPRTManagerSchema.setManageCom(tLJTempFeeSchema.getManageCom());
//        mLOPRTManagerSchema.setAgentCode(tLJTempFeeSchema.getAgentCode());
//        mLOPRTManagerSchema.setCode(PrintManagerBL.CODE_REFUND);
//        mLOPRTManagerSchema.setReqCom(mManageCom);
//        mLOPRTManagerSchema.setReqOperator(mOperator);
//        mLOPRTManagerSchema.setPrtType("0");
//        mLOPRTManagerSchema.setStateFlag("0");
//        outLOPRTManagerSet.add(mLOPRTManagerSchema);
//        return true;
//    }


    /**
     * �α�׼��У��˱�Ա����
     * �����������������򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean checkStandGrade()
    {
        CheckKinds("1");
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setContNo(mLCContSchema.getContNo());
        mLCPolSet = tLCPolDB.query();
        //׼�����ֱ����ĸ��˱�־
        for (int j = 1; j < mLCPolSet.size(); j++)
        {
            mOldPolNo = mLCPolSet.get(j).getPolNo();

            LCUWMasterSchema tLCUWMasterSchema = new LCUWMasterSchema();
            LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();

            tLCUWMasterDB.setProposalNo(mOldPolNo);

            if (tLCUWMasterDB.getInfo())
            {
                tLCUWMasterSchema = tLCUWMasterDB.getSchema();
            }
            else
            {
                // @@������
                this.mErrors.copyAllErrors(tLCUWMasterDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "UWManuNormChkBL";
                tError.functionName = "checkStandGrade";
                tError.errorMessage = "LCUWMaster��ȡ��ʧ��!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //����Լ���ӷѣ����ռƻ����Ϊ�α�׼��
            if (!tLCUWMasterSchema.getSpecFlag().equals("0") ||
                    !tLCUWMasterSchema.getChangePolFlag().equals("0"))
            {
                if (mLMUWSet.size() > 0)
                {
                    for (int i = 1; i <= mLMUWSet.size(); i++)
                    {
                        LMUWSchema tLMUWSchema = new LMUWSchema();
                        tLMUWSchema = mLMUWSet.get(i);

                        mCalCode = tLMUWSchema.getCalCode(); //�α�׼����㹫ʽ����
                        String tempuwgrade = CheckPol();
                        if (tempuwgrade != null)
                        {
                            if (mUWPopedom.compareTo(tempuwgrade) < 0)
                            {
                                CError tError = new CError();
                                tError.moduleName = "UWManuNormChkBL";
                                tError.functionName = "prepareAllPol";
                                tError.errorMessage =
                                        "�޴˴α�׼��Ͷ�����˱�Ȩ�ޣ���Ҫ�ϱ��ϼ��˱�ʦ!";
                                this.mErrors.addOneError(tError);
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }


    /**
     * �˱�������ϢУ��,׼���˱��㷨
     * �����������������򷵻�false,���򷵻�true
     * @param tFlag String
     * @return boolean
     */
    private boolean CheckKinds(String tFlag)
    {
        mLMUWSet.clear();
        LMUWSchema tLMUWSchema = new LMUWSchema();
        //��ѯ�㷨����
        if (tFlag.equals("1"))
        {
            tLMUWSchema.setUWType("13"); //�Ǳ�׼��
        }

        if (tFlag.equals("8"))
        {
            tLMUWSchema.setUWType("14"); //�ܱ�����
        }

        tLMUWSchema.setRiskCode("000000");
        tLMUWSchema.setRelaPolType("I");

        LMUWDB tLMUWDB = new LMUWDB();
        tLMUWDB.setSchema(tLMUWSchema);

        mLMUWSet = tLMUWDB.query();
        if (tLMUWDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLMUWDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWManuNormChkBL";
            tError.functionName = "CheckKinds";
            tError.errorMessage = "�˱�����У���㷨��ȡʧ��!";
            this.mErrors.addOneError(tError);
            //mLMUWDBSet.clear();
            return false;
        }
        return true;
    }


    /**
     * �ܱ�������У��˱�Ա����
     * �����������������򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean checkUserGrade()
    {
        CheckKinds("2");

        if (mLMUWSet.size() > 0)
        {
            for (int i = 1; i <= mLMUWSet.size(); i++)
            {
                LMUWSchema tLMUWSchema = new LMUWSchema();
                tLMUWSchema = mLMUWSet.get(i);

                mCalCode = tLMUWSchema.getCalCode(); //���ھܱ����㹫ʽ����
                String tempuwgrade = CheckPol();
                if (tempuwgrade != null)
                {
                    if (mUWPopedom.compareTo(tempuwgrade) < 0)
                    {
                        CError tError = new CError();
                        tError.moduleName = "UWManuNormChkBL";
                        tError.functionName = "prepareAllPol";
                        tError.errorMessage = "�޴˵��ܱ�������Ȩ�ޣ����ϱ��ϼ��˱�ʦ!";
                        this.mErrors.addOneError(tError);
                        return false;
                    }
                }
            }
        }

        return true;
    }


    /**
     * ���˵��˱�
     * @return String
     */
    private String CheckPol()
    {
        //׼������
        CheckPolInit(mLCPolSchema);

        String tUWGrade = "";

        // ����
        Calculator mCalculator = new Calculator();
        mCalculator.setCalCode(mCalCode);
        //���ӻ���Ҫ��
        mCalculator.addBasicFactor("Get", mCalBase.getGet());
        mCalculator.addBasicFactor("Mult", mCalBase.getMult());
        mCalculator.addBasicFactor("Prem", mCalBase.getPrem());
        //mCalculator.addBasicFactor("PayIntv", mCalBase.getPayIntv() );
        //mCalculator.addBasicFactor("GetIntv", mCalBase.getGetIntv() );
        mCalculator.addBasicFactor("AppAge", mCalBase.getAppAge());
        mCalculator.addBasicFactor("Sex", mCalBase.getSex());
        mCalculator.addBasicFactor("Job", mCalBase.getJob());
        mCalculator.addBasicFactor("PayEndYear", mCalBase.getPayEndYear());
        mCalculator.addBasicFactor("GetStartDate", "");
        //mCalculator.addBasicFactor("GetYear", mCalBase.getGetYear() );
        mCalculator.addBasicFactor("Years", mCalBase.getYears());
        mCalculator.addBasicFactor("Grp", "");
        mCalculator.addBasicFactor("GetFlag", "");
        mCalculator.addBasicFactor("ValiDate", "");
        mCalculator.addBasicFactor("Count", mCalBase.getCount());
        mCalculator.addBasicFactor("FirstPayDate", "");
        //mCalculator.addBasicFactor("AddRate", mCalBase.getAddRate() );
        //mCalculator.addBasicFactor("GDuty", mCalBase.getGDuty() );
        mCalculator.addBasicFactor("PolNo", mCalBase.getPolNo());
        mCalculator.addBasicFactor("InsuredNo", mLCPolSchema.getInsuredNo());

        String tStr = "";
        tStr = mCalculator.calculate();
        if (tStr.trim().equals(""))
        {
            tUWGrade = "";
        }
        else
        {
            tUWGrade = tStr.trim();
        }

        System.out.println("AmntGrade:" + tUWGrade);

        return tUWGrade;
    }


    /**
     * ���˵��˱�����׼��
     * @param tLCPolSchema LCPolSchema
     */
    private void CheckPolInit(LCPolSchema tLCPolSchema)
    {
        mCalBase = new CalBase();
        mCalBase.setPrem(tLCPolSchema.getPrem());
        mCalBase.setGet(tLCPolSchema.getAmnt());
        mCalBase.setMult(tLCPolSchema.getMult());
        //mCalBase.setYears( tLCPolSchema.getYears() );
        mCalBase.setAppAge(tLCPolSchema.getInsuredAppAge());
        mCalBase.setSex(tLCPolSchema.getInsuredSex());
        mCalBase.setJob(tLCPolSchema.getOccupationType());
        mCalBase.setCount(tLCPolSchema.getInsuredPeoples());
        mCalBase.setPolNo(tLCPolSchema.getPolNo());
    }


    /**
     * ׼������ǰ̨ͳһ�洢����
     * �����������������򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
//        mResult.add(mSplitMap);

        MMap map = new MMap();

        map.put(mLCContSchema, "UPDATE");
//        map.put(mLCPolSet, "UPDATE");
        map.put(mLCCUWMasterSet, "UPDATE");
        map.put(mLCCUWSubSet, "INSERT");
//        map.put(mLCUWMasterSet, "UPDATE");
//        map.put(mLCUWSubSet, "INSERT");
        map.put(outLJSPaySet, "DELETE");
        //20041207������
//    map.put(outLJTempFeeSet, "DELETE");
        map.put(outLBTempFeeSet, "INSERT");
//    map.put(outLJTempFeeClassSet, "DELETE");
        map.put(outLBTempFeeClassSet, "INSERT");
        map.put(mLOPRTManagerSet, "INSERT");
        if(Poluwflagsql!=null && !"".equals(Poluwflagsql))
        {
          map.put(Poluwflagsql,"UPDATE");
        }
        map.add(mmap);
        map.add(mSplitMap);

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
//        System.out.println("mOperate" + mOperater);
        if (!tLDUserDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "UWManuNormChkBL";
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
            tError.errorMessage = "�޴˺˱�ʦ��Ϣ�����ܺ˱�!������Ա��" + mOperater + "��";
            this.mErrors.addOneError(tError);
            return false;
        }
        mOperatorUWGrade = tLDUWUserDB.getUWPopedom();
        String tUWPopedom = tLDUWUserDB.getUWPopedom();
        mUWPopedom = tUWPopedom;
        mAppGrade = mUWPopedom;

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

        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setContNo(mLCContSchema.getContNo());
        mLCPolSet = tLCPolDB.query();

        //У���Ƿ������ֽ���
        if (!mUWFlag.equals("1") && !mUWFlag.equals("8") && !mUWFlag.equals("a"))
        {
//            System.out.println("dddddf" + mLCPolSet.size());
            for (int i = 1; i <= mLCPolSet.size(); i++)
            {
                if (mLCPolSet.get(i).getUWFlag().equals("5") ||
                        mLCPolSet.get(i).getUWFlag().equals("z"))
                {
                    CError tError = new CError();
                    tError.moduleName = "UWRReportAfterInitService";
                    tError.functionName = "checkData";
                    tError.errorMessage = "���ֱ���" + mLCPolSet.get(i).getPolNo() + "δ�º˱�����";
                    this.mErrors.addOneError(tError);
                    return false;
                }
            }
        }

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
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);

        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWConfirmAfterInitService";
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
            tError.moduleName = "UWConfirmAfterInitService";
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
            tError.moduleName = "UWConfirmAfterInitService";
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
            tError.moduleName = "UWConfirmAfterInitService";
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
            tError.moduleName = "UWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������ContNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ���������mPrtNo
        mPrtNo = (String) mTransferData.getValueByName("PrtNo");
        if (mPrtNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������PrtNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mUWFlag = (String) mTransferData.getValueByName("UWFlag");
        if (mUWFlag == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������UWFlagʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mUWIdea = (String) mTransferData.getValueByName("UWIdea");
        if (mUWIdea == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������UWIdeaʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * stopInsured
     * ��ͣ������
     * @return boolean
     */
    private boolean stopInsured()
    {
        SplitFamilyBL tSplitFamilyBL = new SplitFamilyBL();
        TransferData tTransferData = new TransferData();
        tTransferData.setNameAndValue("ContNo", mContNo);

        VData tVData = new VData();
        tVData.add(mGlobalInput);
        tVData.add(tTransferData);

        tSplitFamilyBL.submitData(tVData, "");

        if (tSplitFamilyBL.mErrors.needDealError())
        {
            this.mErrors.copyAllErrors(tSplitFamilyBL.mErrors);
            return false;
        }
        else
        {
            mNewContNo = (String) tSplitFamilyBL.getResult().getObject(0);
            System.out.println("NewContNo==" + mNewContNo);
            mSplitMap = (MMap) tSplitFamilyBL.getResult().getObjectByObjectName("MMap", 0);
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
        int tflag = 0;
        //׼����ͬ������
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mContNo);
        if (!tLCContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "��ͬ" + mContNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);

        //׼�������ĸ��˱�־
        mLCContSchema.setUWFlag(mUWFlag);
        mLCContSchema.setUWDate(PubFun.getCurrentDate());
//�����վܱ����ڲ�����ʱ�򣬶�LCCont�ı��ѽ��д��¼���    chenhq
        String tsql = "select nvl(sum(prem),0) from lcpol where uwflag not in ('1','8','a') and contno='"+ mContNo +"'";
        ExeSQL tExeSQL = new ExeSQL();
        String UWprem = tExeSQL.getOneValue(tsql);
        mLCContSchema.setPrem(Double.parseDouble(UWprem));

        /*  ���ֺ˱����ۺͺ�ͬ�˱����۷ֿ���
                 LCPolDB tLCPolDB = new LCPolDB();
                 tLCPolDB.setContNo(mLCContSchema.getContNo());
                 mLCPolSet = tLCPolDB.query();
                 //׼�����ֱ����ĸ��˱�־
                 for (int i = 1; i <= mLCPolSet.size(); i++)
                 {
            mLCPolSet.get(i).setUWFlag(mUWFlag);
            mLCPolSet.get(i).setUWDate(PubFun.getCurrentDate());
                 }
         */
        //׼����ͬ���˱�����
        if (!prepareContUW())
        {
            return false;
        }

        LCInsuredDB tLCInsuredDB = new LCInsuredDB();
        tLCInsuredDB.setContNo(mContNo);
        LCInsuredSet tLCInsuredSet = tLCInsuredDB.query();
        if (tLCInsuredSet == null || tLCInsuredSet.size() <= 0)
        {
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "dealData";
            tError.errorMessage = "��������Ϣʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        for (int i = 1; i <= tLCInsuredSet.size(); i++)
        {
            if (tLCInsuredSet.get(i).getInsuredStat() != null
                    && tLCInsuredSet.get(i).getInsuredStat().length() > 0
                    && tLCInsuredSet.get(i).getInsuredStat().equals("1"))
            {
                tflag = 1;
            }
        }
        if (tflag == 1)
        {
            mStopFlag = "1";
            if (!stopInsured())
            {
                return false;
            }
        }

        return true;
    }

    /**
     * ׼�������պ˱���Ϣ
     * �����������������򷵻�false,���򷵻�true
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
//        System.out.println("��Ͷ�����ĺ˱�����ǰ��¼����:  " + n);
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
            tLCCUWMasterSchema.setUWIdea(mUWIdea);
            tLCCUWMasterSchema.setAutoUWFlag("2"); // 1 �Զ��˱� 2 �˹��˱�
            tLCCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
            tLCCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());

            //�˱�����
            if (mUWFlag.equals("z"))
            {
                tLCCUWMasterSchema.setAutoUWFlag("1");
                tLCCUWMasterSchema.setState("5");
                tLCCUWMasterSchema.setPassFlag("5");
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
//        System.out.println("subcount=" + m);
        if (m > 0)
        {
            m++; //�˱�����
            tLCCUWSubSchema = new LCCUWSubSchema();
            tLCCUWSubSchema.setUWNo(m); //�ڼ��κ˱�
            tLCCUWSubSchema.setContNo(tLCCUWMasterSchema.getContNo());
            tLCCUWSubSchema.setGrpContNo(tLCCUWMasterSchema.getGrpContNo());
            tLCCUWSubSchema.setProposalContNo(tLCCUWMasterSchema.getProposalContNo());
            tLCCUWSubSchema.setOperator(mOperater);
            if (mUWFlag != null && mUWFlag.equals("z"))
            { //�˱�����
                tLCCUWSubSchema.setPassFlag(mUWFlag); //�˱����
                tLCCUWSubSchema.setUWGrade(mUWPopedom); //�˱�����
                tLCCUWSubSchema.setAppGrade(mAppGrade); //���뼶��
                tLCCUWSubSchema.setAutoUWFlag("2");
                tLCCUWSubSchema.setState(mUWFlag);
                tLCCUWSubSchema.setOperator(mOperater); //����Ա
            }

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
        mTransferData.setNameAndValue("AgentCode", mLCContSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup", tLAAgentSet.get(1).getAgentGroup());
//        mTransferData.setNameAndValue("ManageCom", mManageCom);
        //Ӧ��ʹ�ú�ͬ�ϵĹ������
        mTransferData.setNameAndValue("ManageCom", mLCContSchema.getManageCom());
//        System.out.println("manageCom=" + mManageCom);
        mTransferData.setNameAndValue("PrtNo", mLCContSchema.getPrtNo());
        mTransferData.setNameAndValue("ContNo", mLCContSchema.getContNo());
        mTransferData.setNameAndValue("AppntCode", mLCContSchema.getAppntNo());
        mTransferData.setNameAndValue("AppntName", mLCContSchema.getAppntName());
        mTransferData.setNameAndValue("UWDate", PubFun.getCurrentDate());
        mTransferData.setNameAndValue("StopFlag", mStopFlag);
        mTransferData.setNameAndValue("NewContNo", mNewContNo);

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

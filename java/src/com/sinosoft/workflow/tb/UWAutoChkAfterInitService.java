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
 * <p>Title:�������ڵ�����:����Լ�Զ��˱� </p>
 * <p>Description: �Զ��˱���������̨AfterInit������ </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: SinoSoft </p>
 * @author HYQ
 * @version 1.0
 */

public class UWAutoChkAfterInitService implements AfterInitService
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** �����洫�����ݵ����� */
    MMap mMap = new MMap();
    private VData mResult = new VData();

    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();

    /** ���ݲ����ַ��� */
    private String mOperator;
    private String mManageCom;

    /** ҵ�����ݲ����ַ��� */
    private String mMissionID;

    /**������*/
    private LCContSchema mLCContSchema = new LCContSchema();
    private String mPolPassFlag = "0"; //����ͨ�����
    private String mContPassFlag = "0"; //��ͬͨ�����
    private String mUWGrade = "";
    private String mCalCode; //�������
    private double mValue;

    private LCContSet mAllLCContSet = new LCContSet();
    private LCPolSet mAllLCPolSet = new LCPolSet();
    private String mContNo = "";
    private String mPContNo = "";
    private String mOldPolNo = "";

    /** ��ͬ�˱�����*/
    private LCCUWMasterSet mLCCUWMasterSet = new LCCUWMasterSet();
    private LCCUWMasterSet mAllLCCUWMasterSet = new LCCUWMasterSet();

    /** ��ͬ�˱��ӱ�*/
    private LCCUWSubSet mLCCUWSubSet = new LCCUWSubSet();
    private LCCUWSubSet mAllLCCUWSubSet = new LCCUWSubSet();

    /** ��ͬ�˱�������Ϣ��*/
    private LCCUWErrorSet mLCCUWErrorSet = new LCCUWErrorSet();
    private LCCUWErrorSet mAllLCCUWErrorSet = new LCCUWErrorSet();

    /** �����ֺ˱����� */
    private LCUWMasterSet mLCUWMasterSet = new LCUWMasterSet();
    private LCUWMasterSet mAllLCUWMasterSet = new LCUWMasterSet();

    /** �����ֺ˱��ӱ� */
    private LCUWSubSet mLCUWSubSet = new LCUWSubSet();
    private LCUWSubSet mAllLCUWSubSet = new LCUWSubSet();

    /** �˱�������Ϣ�� */
    private LCUWErrorSet mLCUWErrorSet = new LCUWErrorSet();
    private LCUWErrorSet mAllLCErrSet = new LCUWErrorSet();

    private CalBase mCalBase = new CalBase();

    public UWAutoChkAfterInitService()
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
//        System.out.println("---UWAutoChkBL getInputData---");

        if (!checkData())
        {
            return false;
        }

        if (!dealData(mLCContSchema))
        {
            return false;
        }
//        System.out.println("---UWAutoChkBL dealData END---");

        //Ϊ��������һ�ڵ������ֶ�׼������
        if (!prepareTransferData())
        {
            return false;
        }

        //׼������̨������
        if (prepareOutputData(mLCContSchema))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "UWAutoChkBL";
            tError.functionName = "submitData";
            tError.errorMessage = "�ύ������׼��ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
//        System.out.println("Start  Submit...");

        mResult.clear();
        mResult.add(mMap);

        return true;
    }


    /**
     * ���ݲ�����ҵ����
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @param tLCContSchema LCContSchema
     * @return boolean
     */
    private boolean dealData(LCContSchema tLCContSchema)
    {
//        LCPolSet tLCPolSet = new LCPolSet();
        LCPolSchema tLCPolSchema = null;
        LMUWSet tLMUWSetUnpass = new LMUWSet(); //δͨ���ĺ˱�����
        LMUWSet tLMUWSetAll = null; //���к˱�����
        LMUWSet tLMUWSetSpecial = null; //��Ҫ���ռ�������˱�����
        LMUWSchema tLMUWSchema = null;

        //��ñ�����
        mContNo = tLCContSchema.getContNo();
        //���Ͷ������
        mPContNo = tLCContSchema.getProposalContNo();

        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setContNo(mContNo);
        //��ȡ���˺�ͬ�µ�������Ϣ
        LCPolSet tLCPolSet = tLCPolDB.query();

        int nPolIndex = 0;
        int nPolCount = tLCPolSet.size();
        for (nPolIndex = 1; nPolIndex <= nPolCount; nPolIndex++)
        {
            tLCPolSchema = tLCPolSet.get(nPolIndex);
            mOldPolNo = tLCPolSchema.getPolNo(); //��ñ������ֺ�

            //׼���㷨����ȡĳ���ֵ����к˱�����ļ���
            tLMUWSetUnpass.clear();
            if (tLMUWSetAll != null)
            {
                tLMUWSetAll.clear();
            }
            if (tLMUWSetSpecial != null)
            {
                tLMUWSetSpecial.clear();
            }
            //��ȡ���ֺ˱�������Ϣ��ָ�����ֵĺ˱���Ϣ
            tLMUWSetAll = CheckKinds(tLCPolSchema);
            if (tLMUWSetAll == null)
            {
                return false;
            }
            //��ȡ���ֺ˱�������Ϣ��Ĭ�ϵ����ֺ˱���Ϣ
            tLMUWSetSpecial = CheckKinds2(tLCPolSchema);
            if (tLMUWSetSpecial == null)
            {
                return false;
            }

            //׼�����ݣ���������Ϣ�л�ȡ���������Ϣ
            CheckPolInit(tLCPolSchema);

            //���˵��˱�
            mPolPassFlag = "0"; //�˱�ͨ����־����ʼΪδ�˱�

            int n = tLMUWSetAll.size(); //�˱���������
            if (n == 0)
            {
                mPolPassFlag = "9"; //�޺˱��������ñ�־Ϊͨ��
            }
            else
            {
                //ĿǰĿǰ���е����־���һЩ�����ĺ˱�����,���Աض��߸÷�֦
                int j = 0;
                for (int i = 1; i <= n; i++)
                {
                    //ȡ�������
//                    tLMUWSchema = new LMUWSchema();
//                    tLMUWSchema = tLMUWSetAll.get(i);
                    tLMUWSchema = tLMUWSetAll.get(i);
                    mCalCode = tLMUWSchema.getCalCode();
                    //�˱�����
                    if (CheckPol(tLCPolSchema.getInsuredNo(), tLCPolSchema.getRiskCode()) == 0)
                    {
                    }
                    else
                    {
                        j++;
                        tLMUWSetUnpass.add(tLMUWSchema);
                        mPolPassFlag = "5"; //���˹��˱�
                        mContPassFlag = "5";

                        //ȡ�˱�����
                        String tuwgrade = tLMUWSchema.getUWGrade();
                        //�������Ĭ�ϵĺ˱����𲻴��ڣ����˳�
                        if (tuwgrade == null)
                        {
                            // @@������
                            CError tError = new CError();
                            tError.moduleName = "UWAutoChkBL";
                            tError.functionName = "dealData";
                            tError.errorMessage = "ȡ�Ժ˹������Ϊ��" + tLMUWSchema.getCalCode()
                                    + " �ĺ˱��������";
                            this.mErrors.addOneError(tError);
                            return false;
                        }

                        if (j == 1)
                        {
                            mUWGrade = tuwgrade;
                        }
                        else
                        {
                            if (mUWGrade.compareTo(tuwgrade) < 0)
                            {
                                mUWGrade = tuwgrade;
                            }
                        }
                    }
                }

                //��Ҫ�˹��˱�ʱ��У��˱����غ˱�Ա�˱�����
                if (tLMUWSetUnpass.size() > 0 && tLMUWSetSpecial.size() > 0)
                {
                    for (int k = 1; k <= tLMUWSetSpecial.size(); k++)
                    {
                        LMUWSchema t2LMUWSchema = new LMUWSchema();
                        t2LMUWSchema = tLMUWSetSpecial.get(k);
                        mCalCode = t2LMUWSchema.getCalCode();

                        String tempuwgrade = checkRiskAmnt(tLCPolSchema);
                        if (tempuwgrade != null)
                        {
                            //����Ҫ�˹��˱�ʱ�򵱼�tLMUWSetUnpass.size()>0ʱ,mUWGradeӦ�ò�Ϊnull,�������Զ��˱������к˱������ֶ�ȱ��������
                            if (mUWGrade == null || mUWGrade.compareTo(tempuwgrade) < 0)
                            {
                                mUWGrade = tempuwgrade;
                            }
                        }
                    }
                }
                else
                {
                    //�����е��Զ��˱����ɹ�����������Ͷ����ƥ��ʱ�˱������Ϊ��
                    //��һ��Ҫ���к˱�����������޺˱�������쳣����
                    //���Ը������޺˱������Ͷ����һ�����Ĭ�ϼ���
                    if (mUWGrade == null || mUWGrade.equals(""))
                    {
                        StringBuffer tSBql = new StringBuffer(32);
                        tSBql.append("select min(UWGrade) from LMUW");
                        ExeSQL tExeSQL = new ExeSQL();
                        SSRS tSSRS = tExeSQL.execSQL(tSBql.toString());
                        if (tSSRS.getMaxRow() == 0)
                        {
                            //������
                            CError tError = new CError();
                            tError.moduleName = "UWAutoChkBL";
                            tError.functionName = "dealData";
                            tError.errorMessage = "���ֺ˱������������ݡ�";
                            this.mErrors.addOneError(tError);
                            return false;
                        }
                        else
                        {
                            mUWGrade = tSSRS.GetText(1, 1);
                        }
                    }
                }

                if (mPolPassFlag.equals("0"))
                {
                    mPolPassFlag = "9";
                }
//                System.out.println("ƥ����:" + tLMUWSetAll.size() + "�������:" + tLMUWSetSpecial.size()
//                        + "����:" + mUWGrade);
            }
            if (!dealOnePol(tLCPolSchema, tLMUWSetUnpass))
            {
                return false;
            }
        }

        /* ��ͬ�˱� */
        LMUWSet tLMUWSetContUnpass = new LMUWSet(); //δͨ���ĺ�ͬ�˱�����
        LMUWSet tLMUWSetContAll = CheckKinds3(); //���к�ͬ�˱�����

        //׼�����ݣ���������Ϣ�л�ȡ���������Ϣ
        CheckContInit(tLCContSchema);

        //���˺�ͬ�˱�
        int tCount = tLMUWSetContAll.size(); //�˱���������
        if (tCount == 0)
        {
            //�޺˱��������ñ�־Ϊͨ��
            mContPassFlag = "9";
        }
        else
        {
            //ĿǰĿǰ���е����־���һЩ�����ĺ˱�����,���Աض��߸÷�֦
            int j = 0;
            for (int index = 1; index <= tCount; index++)
            {
                //ȡ�������
                tLMUWSchema = new LMUWSchema();
                tLMUWSchema = tLMUWSetContAll.get(index);
                mCalCode = tLMUWSchema.getCalCode();
                if (CheckPol(tLCContSchema.getInsuredNo(), "000000") == 0)
                {
                }
                else
                {
                    j++;
                    tLMUWSetContUnpass.add(tLMUWSchema);
                    mContPassFlag = "5"; //�˱���ͨ�������˹��˱�

                    //ȡ�˱�����
                    String tuwgrade = tLMUWSchema.getUWGrade();
                    if (tuwgrade == null)
                    {
                        // @@������
                        CError tError = new CError();
                        tError.moduleName = "UWAutoChkBL";
                        tError.functionName = "dealData";
                        tError.errorMessage = "��ͬ�˱�ʱȡ�Ժ˹������Ϊ��" + tLMUWSchema.getCalCode()
                                + " �ĺ˱��������";
                        this.mErrors.addOneError(tError);
                        return false;
                    }

                    if (j == 1 && (mUWGrade == null || mUWGrade.equals("")))
                    {
                        mUWGrade = tuwgrade;
                    }
                    else
                    {
                        if (mUWGrade.compareTo(tuwgrade) < 0)
                        {
                            mUWGrade = tuwgrade;
                        }
                    }
                }
            }

            if (mUWGrade == null || mUWGrade.equals(""))
            {
                StringBuffer tSBql = new StringBuffer(32);
                tSBql.append("select min(UWGrade) from LMUW");
                ExeSQL tExeSQL = new ExeSQL();
                SSRS tSSRS = tExeSQL.execSQL(tSBql.toString());
                if (tSSRS.getMaxRow() == 0)
                {
                    //������
                    CError tError = new CError();
                    tError.moduleName = "UWAutoChkBL";
                    tError.functionName = "dealData";
                    tError.errorMessage = "���ֺ˱������������ݡ�";
                    this.mErrors.addOneError(tError);
                    return false;
                }
                else
                {
                    mUWGrade = tSSRS.GetText(1, 1);
                }
            }

            if (mContPassFlag.equals("0"))
            {
                mContPassFlag = "9";
            }
//            System.out.println("��ͬ�˱�ƥ����:" + tLMUWSetContAll.size() + "��ͬ�˱�δͨ����:"
//                    + tLMUWSetContUnpass.size() + "����:" + mUWGrade);
        }
        dealOneCont(tLCContSchema, tLMUWSetContUnpass);
        return true;
    }


    /**
     * ���ݱ���У��˱�����
     * @param tLCPolSchema LCPolSchema
     * @return String
     */
    private String checkRiskAmnt(LCPolSchema tLCPolSchema)
    {
        String tUWGrade = "";
        // ����
        Calculator mCalculator = new Calculator();
        mCalculator.setCalCode(mCalCode);

        //���ӻ���Ҫ��
        mCalculator.addBasicFactor("Get", mCalBase.getGet());
        mCalculator.addBasicFactor("Mult", mCalBase.getMult());
        mCalculator.addBasicFactor("Prem", mCalBase.getPrem());
        mCalculator.addBasicFactor("AppAge", mCalBase.getAppAge());
        mCalculator.addBasicFactor("Sex", mCalBase.getSex());
        mCalculator.addBasicFactor("Job", mCalBase.getJob());
        mCalculator.addBasicFactor("PayEndYear", mCalBase.getPayEndYear());
        mCalculator.addBasicFactor("GetStartDate", "");
        mCalculator.addBasicFactor("Years", mCalBase.getYears());
        mCalculator.addBasicFactor("Grp", "");
        mCalculator.addBasicFactor("GetFlag", "");
        mCalculator.addBasicFactor("ValiDate", "");
        mCalculator.addBasicFactor("Count", mCalBase.getCount());
        mCalculator.addBasicFactor("FirstPayDate", "");
        mCalculator.addBasicFactor("PolNo", mCalBase.getPolNo());
        mCalculator.addBasicFactor("InsuredNo", tLCPolSchema.getInsuredNo());
        mCalculator.addBasicFactor("RiskCode", tLCPolSchema.getRiskCode());

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
//        System.out.println("AmntGrade:" + tUWGrade);
        return tUWGrade;
    }


    /**
     * ����һ�ű�����ҵ����
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @param tLCPolSchema LCPolSchema
     * @param tLMUWSetUnpass LMUWSet
     * @return boolean
     */
    private boolean dealOnePol(LCPolSchema tLCPolSchema, LMUWSet tLMUWSetUnpass)
    {
        // ����
        if (!preparePol(tLCPolSchema))
        {
            return false;
        }
        // �˱���Ϣ
        if (!preparePolUW(tLCPolSchema, tLMUWSetUnpass))
        {
            return false;
        }

        LCPolSchema tLCPolSchemaDup = new LCPolSchema();
        tLCPolSchemaDup.setSchema(tLCPolSchema);
        mAllLCPolSet.add(tLCPolSchemaDup);

        LCUWMasterSet tLCUWMasterSet = new LCUWMasterSet();
        tLCUWMasterSet.set(mLCUWMasterSet);
        mAllLCUWMasterSet.add(tLCUWMasterSet);

        LCUWSubSet tLCUWSubSet = new LCUWSubSet();
        tLCUWSubSet.set(mLCUWSubSet);
        mAllLCUWSubSet.add(tLCUWSubSet);

        LCUWErrorSet tLCUWErrorSet = new LCUWErrorSet();
        tLCUWErrorSet.set(mLCUWErrorSet);
        mAllLCErrSet.add(tLCUWErrorSet);

        return true;
    }


    /**
     * ����һ�ű�����ҵ����
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @param tLCContSchema LCContSchema
     * @param tLMUWSetContUnpass LMUWSet
     * @return boolean
     */
    private boolean dealOneCont(LCContSchema tLCContSchema, LMUWSet tLMUWSetContUnpass)
    {
        prepareContUW(tLCContSchema, tLMUWSetContUnpass);

        LCContSchema tLCContSchemaDup = new LCContSchema();
        tLCContSchemaDup.setSchema(tLCContSchema);
        mAllLCContSet.add(tLCContSchemaDup);

        LCCUWMasterSet tLCCUWMasterSet = new LCCUWMasterSet();
        tLCCUWMasterSet.set(mLCCUWMasterSet);
        mAllLCCUWMasterSet.add(tLCCUWMasterSet);

        LCCUWSubSet tLCCUWSubSet = new LCCUWSubSet();
        tLCCUWSubSet.set(mLCCUWSubSet);
        mAllLCCUWSubSet.add(tLCCUWSubSet);

        LCCUWErrorSet tLCCUWErrorSet = new LCCUWErrorSet();
        tLCCUWErrorSet.set(mLCCUWErrorSet);
        mAllLCCUWErrorSet.add(tLCCUWErrorSet);

        return true;
    }


    /**
     * У��Ͷ�����Ƿ񸴺�
     * �����������������򷵻�false,���򷵻�true
     * @param tLCContSchema LCContSchema
     * @return boolean
     */
    private boolean checkApprove(LCContSchema tLCContSchema)
    {
        if (tLCContSchema.getApproveFlag() == null || !tLCContSchema.getApproveFlag().equals("9"))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "UWAutoChkBL";
            tError.functionName = "checkApprove";
            tError.errorMessage = "Ͷ������δ���и��˲��������ܺ˱�!��Ͷ�����ţ�"
                    + tLCContSchema.getProposalContNo().trim() + "��";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }


    /**
     * У��˱�Ա����
     * �����������������򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean checkUWGrade()
    {
        LDUserDB tLDUserDB = new LDUserDB();
        tLDUserDB.setUserCode(mOperator);

        if (!tLDUserDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "UWAutoChkBL";
            tError.functionName = "checkUWGrade";
            tError.errorMessage = "�޴˲���Ա��Ϣ�����ܺ˱�!������Ա��" + mOperator + "��";
            this.mErrors.addOneError(tError);
            return false;
        }

        String tUWPopedom = tLDUserDB.getUWPopedom();
        if (tUWPopedom == null || tUWPopedom.equals(""))
        {
            CError tError = new CError();
            tError.moduleName = "UWAutoChkBL";
            tError.functionName = "checkUWGrade";
            tError.errorMessage = "����Ա�޺˱�Ȩ�ޣ����ܺ˱�!������Ա��" + mOperator + "��";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * �˱�������ϢУ��,׼���˱��㷨
     * @param tLCPolSchema LCPolSchema
     * @return LMUWSet
     */
    private LMUWSet CheckKinds(LCPolSchema tLCPolSchema)
    {
//        String tsql = "";
//        tsql = "select * from lmuw where (riskcode = '000000' and relapoltype = 'I' and uwtype = '11') or (riskcode = '" +
//                tLCPolSchema.getRiskCode().trim() +
//                "' and relapoltype = 'I' and uwtype = '1')  order by calcode";
        //��ѯ�㷨����
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from lmuw where (riskcode = '000000' and relapoltype = 'I' and uwtype = '11') or (riskcode = '");
        tSBql.append(tLCPolSchema.getRiskCode().trim());
        tSBql.append("' and relapoltype = 'I' and uwtype = '1')  order by calcode");

        LMUWDB tLMUWDB = new LMUWDB();
        LMUWSet tLMUWSet = tLMUWDB.executeQuery(tSBql.toString());
        if (tLMUWDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLMUWDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWAutoChkBL";
            tError.functionName = "CheckKinds";
            tError.errorMessage = tLCPolSchema.getRiskCode().trim() +
                    "���ֺ˱���Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLMUWSet.clear();
            return null;
        }
        return tLMUWSet;
    }


    /**
     * �˱�������ϢУ��,׼���˱��㷨
     * @param tLCPolSchema LCPolSchema
     * @return LMUWSet
     */
    private LMUWSet CheckKinds2(LCPolSchema tLCPolSchema)
    {
        String tsql = "";
//        LMUWSchema tLMUWSchema = new LMUWSchema();
        //��ѯ�㷨����
        tsql =
                "select * from lmuw where riskcode = '000000' and relapoltype = 'I' and uwtype = '12'";

        LMUWDB tLMUWDB = new LMUWDB();
        LMUWSet tLMUWSet = tLMUWDB.executeQuery(tsql);
        if (tLMUWDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLMUWDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWAutoChkBL";
            tError.functionName = "CheckKinds2";
            tError.errorMessage = tLCPolSchema.getRiskCode().trim() +
                    "������Ϣ�˱���ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLMUWSet.clear();
            return null;
        }
        return tLMUWSet;
    }


    /**
     * �˱�������ϢУ��,׼���˱��㷨
     * @return LMUWSet
     */
    private LMUWSet CheckKinds3()
    {
        String tsql = "";
//        LMUWSchema tLMUWSchema = new LMUWSchema();
        //��ѯ�㷨����
        tsql =
                "select * from lmuw where riskcode = '000000' and relapoltype = 'I' and uwtype = '19'";

        LMUWDB tLMUWDB = new LMUWDB();
        LMUWSet tLMUWSet = tLMUWDB.executeQuery(tsql);
        if (tLMUWDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLMUWDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWAutoChkBL";
            tError.functionName = "CheckKinds3";
            tError.errorMessage = "��ͬ���ֺ˱���Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            tLMUWSet.clear();
            return null;
        }
        return tLMUWSet;
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
        mCalBase.setAppAge(tLCPolSchema.getInsuredAppAge());
        mCalBase.setSex(tLCPolSchema.getInsuredSex());
        mCalBase.setJob(tLCPolSchema.getOccupationType());
        mCalBase.setCount(tLCPolSchema.getInsuredPeoples());
        mCalBase.setPolNo(tLCPolSchema.getPolNo());
        mCalBase.setContNo(mContNo);
    }


    /**
     * ���˵��˱�����׼��
     * @param tLCContSchema LCContSchema
     */
    private void CheckContInit(LCContSchema tLCContSchema)
    {
        mCalBase = new CalBase();
        mCalBase.setPrem(tLCContSchema.getPrem());
        mCalBase.setGet(tLCContSchema.getAmnt());
        mCalBase.setMult(tLCContSchema.getMult());
//            mCalBase.setAppAge( tLCContSchema.getInsuredAppAge() );
        mCalBase.setSex(tLCContSchema.getInsuredSex());
//            mCalBase.setJob( tLCContSchema.getOccupationType() );
//            mCalBase.setCount( tLCContSchema.getInsuredPeoples() );
        mCalBase.setContNo(mContNo);
    }


    /**
     * ���˵��˱�
     * @param tInsuredNo String
     * @param tRiskCode String
     * @return double
     */
    private double CheckPol(String tInsuredNo, String tRiskCode) //LCPolSchema tLCPolSchema)
    {
        // ����
        Calculator mCalculator = new Calculator();
        mCalculator.setCalCode(mCalCode);
        //���ӻ���Ҫ��
        mCalculator.addBasicFactor("Get", mCalBase.getGet());
        mCalculator.addBasicFactor("Mult", mCalBase.getMult());
        mCalculator.addBasicFactor("Prem", mCalBase.getPrem());
        mCalculator.addBasicFactor("AppAge", mCalBase.getAppAge());
        mCalculator.addBasicFactor("Sex", mCalBase.getSex());
        mCalculator.addBasicFactor("Job", mCalBase.getJob());
        mCalculator.addBasicFactor("PayEndYear", mCalBase.getPayEndYear());
        mCalculator.addBasicFactor("GetStartDate", "");
        mCalculator.addBasicFactor("Years", mCalBase.getYears());
        mCalculator.addBasicFactor("Grp", "");
        mCalculator.addBasicFactor("GetFlag", "");
        mCalculator.addBasicFactor("ValiDate", "");
        mCalculator.addBasicFactor("Count", mCalBase.getCount());
        mCalculator.addBasicFactor("FirstPayDate", "");
        mCalculator.addBasicFactor("ContNo", mCalBase.getContNo());
        mCalculator.addBasicFactor("PolNo", mCalBase.getPolNo());
        mCalculator.addBasicFactor("InsuredNo", tInsuredNo); //tLCPolSchema.getInsuredNo());;
        mCalculator.addBasicFactor("RiskCode", tRiskCode); //tLCPolSchema.getRiskCode());;
        String tStr = "";
        tStr = mCalculator.calculate();
        if (tStr == null || tStr.trim().equals(""))
        {
            mValue = 0;
        }
        else
        {
            mValue = Double.parseDouble(tStr);
        }
//        System.out.println(mValue);
        return mValue;
    }


    /**
     * ׼��������Ϣ
     * �����������������򷵻�false,���򷵻�true
     * @param tLCPolSchema LCPolSchema
     * @return boolean
     */
    private boolean preparePol(LCPolSchema tLCPolSchema)
    {
//        System.out.println("���ֺ˱���־" + mPolPassFlag);
        tLCPolSchema.setUWFlag(mPolPassFlag);
        tLCPolSchema.setUWCode(mOperator);
        tLCPolSchema.setUWDate(PubFun.getCurrentDate());
        tLCPolSchema.setModifyDate(PubFun.getCurrentDate());
        tLCPolSchema.setModifyTime(PubFun.getCurrentTime());
        return true;
    }


    /**
     * ׼����ͬ�˱���Ϣ
     * �����������������򷵻�false,���򷵻�true
     * @param tLCContSchema LCContSchema
     * @param tLMUWSetContUnpass LMUWSet
     * @return boolean
     */
    private boolean prepareContUW(LCContSchema tLCContSchema, LMUWSet tLMUWSetContUnpass)
    {
        tLCContSchema.setUWFlag(mContPassFlag);
        tLCContSchema.setUWOperator(mOperator);
        tLCContSchema.setUWDate(PubFun.getCurrentDate());
        tLCContSchema.setUWTime(PubFun.getCurrentTime());
        tLCContSchema.setModifyDate(PubFun.getCurrentDate());
        tLCContSchema.setModifyTime(PubFun.getCurrentTime());

        //��ͬ�˱�����
        boolean firstUW = true;
        LCCUWMasterSchema tLCCUWMasterSchema = new LCCUWMasterSchema();
        LCCUWMasterDB tLCCUWMasterDB = new LCCUWMasterDB();
        tLCCUWMasterDB.setContNo(mContNo);
        LCCUWMasterSet tLCCUWMasterSet = new LCCUWMasterSet();
        tLCCUWMasterSet = tLCCUWMasterDB.query();
        if (tLCCUWMasterDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLCCUWMasterDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWAtuoChkAfterInitService";
            tError.functionName = "prepareContUW";
            tError.errorMessage = mContNo + "��ͬ�˱��ܱ�ȡ��ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLCCUWMasterSet.size() == 0)
        {
            tLCCUWMasterSchema.setContNo(mContNo);
            tLCCUWMasterSchema.setGrpContNo(tLCContSchema.getGrpContNo());
            tLCCUWMasterSchema.setProposalContNo(tLCContSchema.getProposalContNo());
            tLCCUWMasterSchema.setUWNo(1);
            tLCCUWMasterSchema.setInsuredNo(tLCContSchema.getInsuredNo());
            tLCCUWMasterSchema.setInsuredName(tLCContSchema.getInsuredName());
            tLCCUWMasterSchema.setAppntNo(tLCContSchema.getAppntNo());
            tLCCUWMasterSchema.setAppntName(tLCContSchema.getAppntName());
            tLCCUWMasterSchema.setAgentCode(tLCContSchema.getAgentCode());
            tLCCUWMasterSchema.setAgentGroup(tLCContSchema.getAgentGroup());
            tLCCUWMasterSchema.setUWGrade(mUWGrade); //�˱�����
            tLCCUWMasterSchema.setAppGrade(mUWGrade); //�걨����
            tLCCUWMasterSchema.setPostponeDay("");
            tLCCUWMasterSchema.setPostponeDate("");
            tLCCUWMasterSchema.setAutoUWFlag("1"); // 1 �Զ��˱� 2 �˹��˱�
            tLCCUWMasterSchema.setState(mContPassFlag);
            tLCCUWMasterSchema.setPassFlag(mContPassFlag);
            tLCCUWMasterSchema.setHealthFlag("0");
            tLCCUWMasterSchema.setSpecFlag("0");
            tLCCUWMasterSchema.setQuesFlag("0");
            tLCCUWMasterSchema.setReportFlag("0");
            tLCCUWMasterSchema.setChangePolFlag("0");
            tLCCUWMasterSchema.setPrintFlag("0");
            tLCCUWMasterSchema.setPrintFlag2("0");
            tLCCUWMasterSchema.setManageCom(tLCContSchema.getManageCom());
            tLCCUWMasterSchema.setUWIdea("");
            tLCCUWMasterSchema.setUpReportContent("");
            tLCCUWMasterSchema.setOperator(mOperator); //����Ա
            tLCCUWMasterSchema.setMakeDate(PubFun.getCurrentDate());
            tLCCUWMasterSchema.setMakeTime(PubFun.getCurrentTime());
            tLCCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
            tLCCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());
        }
        else
        {
            firstUW = false;
            tLCCUWMasterSchema = tLCCUWMasterSet.get(1);
            tLCCUWMasterSchema.setUWNo(tLCCUWMasterSchema.getUWNo() + 1);
            tLCCUWMasterSchema.setState(mContPassFlag);
            tLCCUWMasterSchema.setPassFlag(mContPassFlag);
            tLCCUWMasterSchema.setAutoUWFlag("1"); // 1 �Զ��˱� 2 �˹��˱�
            tLCCUWMasterSchema.setUWGrade(mUWGrade); //�˱�����
            tLCCUWMasterSchema.setAppGrade(mUWGrade); //�걨����
            tLCCUWMasterSchema.setOperator(mOperator); //����Ա
            tLCCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
            tLCCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());
        }
        mLCCUWMasterSet.clear();
        mLCCUWMasterSet.add(tLCCUWMasterSchema);

        // ��ͬ�˱��켣��
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
            tError.moduleName = "UWAtuoChkAfterInitService";
            tError.functionName = "prepareContUW";
            tError.errorMessage = mContNo + "��ͬ�˱��켣���ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        int nUWNo = tLCCUWSubSet.size();
        if (nUWNo > 0)
        {
            tLCCUWSubSchema.setUWNo(++nUWNo); //�ڼ��κ˱�
        }
        else
        {
            tLCCUWSubSchema.setUWNo(1); //��1�κ˱�
        }

        tLCCUWSubSchema.setContNo(tLCCUWMasterSchema.getContNo());
        tLCCUWSubSchema.setGrpContNo(tLCCUWMasterSchema.getGrpContNo());
        tLCCUWSubSchema.setProposalContNo(tLCCUWMasterSchema.getProposalContNo());
        tLCCUWSubSchema.setInsuredNo(tLCCUWMasterSchema.getInsuredNo());
        tLCCUWSubSchema.setInsuredName(tLCCUWMasterSchema.getInsuredName());
        tLCCUWSubSchema.setAppntNo(tLCCUWMasterSchema.getAppntNo());
        tLCCUWSubSchema.setAppntName(tLCCUWMasterSchema.getAppntName());
        tLCCUWSubSchema.setAgentCode(tLCCUWMasterSchema.getAgentCode());
        tLCCUWSubSchema.setAgentGroup(tLCCUWMasterSchema.getAgentGroup());
        tLCCUWSubSchema.setUWGrade(tLCCUWMasterSchema.getUWGrade()); //�˱�����
        tLCCUWSubSchema.setAppGrade(tLCCUWMasterSchema.getAppGrade()); //���뼶��
        tLCCUWSubSchema.setAutoUWFlag(tLCCUWMasterSchema.getAutoUWFlag());
        tLCCUWSubSchema.setState(tLCCUWMasterSchema.getState());
        tLCCUWSubSchema.setPassFlag(tLCCUWMasterSchema.getState());
        tLCCUWSubSchema.setPostponeDay(tLCCUWMasterSchema.getPostponeDay());
        tLCCUWSubSchema.setPostponeDate(tLCCUWMasterSchema.getPostponeDate());
        tLCCUWSubSchema.setUpReportContent(tLCCUWMasterSchema.getUpReportContent());
        tLCCUWSubSchema.setHealthFlag(tLCCUWMasterSchema.getHealthFlag());
        tLCCUWSubSchema.setSpecFlag(tLCCUWMasterSchema.getSpecFlag());
        tLCCUWSubSchema.setSpecReason(tLCCUWMasterSchema.getSpecReason());
        tLCCUWSubSchema.setQuesFlag(tLCCUWMasterSchema.getQuesFlag());
        tLCCUWSubSchema.setReportFlag(tLCCUWMasterSchema.getReportFlag());
        tLCCUWSubSchema.setChangePolFlag(tLCCUWMasterSchema.getChangePolFlag());
        tLCCUWSubSchema.setChangePolReason(tLCCUWMasterSchema.getChangePolReason());
        tLCCUWSubSchema.setAddPremReason(tLCCUWMasterSchema.getAddPremReason());
        tLCCUWSubSchema.setPrintFlag(tLCCUWMasterSchema.getPrintFlag());
        tLCCUWSubSchema.setPrintFlag2(tLCCUWMasterSchema.getPrintFlag2());
        tLCCUWSubSchema.setUWIdea(tLCCUWMasterSchema.getUWIdea());
        tLCCUWSubSchema.setOperator(tLCCUWMasterSchema.getOperator()); //����Ա
        tLCCUWSubSchema.setManageCom(tLCCUWMasterSchema.getManageCom());
        tLCCUWSubSchema.setMakeDate(PubFun.getCurrentDate());
        tLCCUWSubSchema.setMakeTime(PubFun.getCurrentTime());
        tLCCUWSubSchema.setModifyDate(PubFun.getCurrentDate());
        tLCCUWSubSchema.setModifyTime(PubFun.getCurrentTime());

        mLCCUWSubSet.clear();
        mLCCUWSubSet.add(tLCCUWSubSchema);

        // �˱�������Ϣ��
        LCCUWErrorSchema tLCCUWErrorSchema = new LCCUWErrorSchema();
        LCCUWErrorDB tLCCUWErrorDB = new LCCUWErrorDB();
        tLCCUWErrorDB.setContNo(mContNo);
        LCCUWErrorSet tLCCUWErrorSet = new LCCUWErrorSet();
        tLCCUWErrorSet = tLCCUWErrorDB.query();
        if (tLCCUWErrorDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLCCUWErrorDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWAtuoChkAfterInitService";
            tError.functionName = "prepareContUW";
            tError.errorMessage = mContNo + "��ͬ������Ϣ���ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        tLCCUWErrorSchema.setSerialNo("0");
        if (nUWNo > 0)
        {
            tLCCUWErrorSchema.setUWNo(nUWNo);
        }
        else
        {
            tLCCUWErrorSchema.setUWNo(1);
        }
        tLCCUWErrorSchema.setContNo(mContNo);
        tLCCUWErrorSchema.setGrpContNo(tLCCUWSubSchema.getGrpContNo());
        tLCCUWErrorSchema.setProposalContNo(tLCCUWSubSchema.getProposalContNo());
        tLCCUWErrorSchema.setInsuredNo(tLCCUWSubSchema.getInsuredNo());
        tLCCUWErrorSchema.setInsuredName(tLCCUWSubSchema.getInsuredName());
        tLCCUWErrorSchema.setAppntNo(tLCCUWSubSchema.getAppntNo());
        tLCCUWErrorSchema.setAppntName(tLCCUWSubSchema.getAppntName());
        tLCCUWErrorSchema.setManageCom(tLCCUWSubSchema.getManageCom());
        tLCCUWErrorSchema.setUWRuleCode(""); //�˱��������
        tLCCUWErrorSchema.setUWError(""); //�˱�������Ϣ
        tLCCUWErrorSchema.setCurrValue(""); //��ǰֵ
        tLCCUWErrorSchema.setModifyDate(PubFun.getCurrentDate());
        tLCCUWErrorSchema.setModifyTime(PubFun.getCurrentTime());
        tLCCUWErrorSchema.setUWPassFlag(mPolPassFlag);

        //ȡ�˱�������Ϣ
        mLCCUWErrorSet.clear();
        int merrcount = tLMUWSetContUnpass.size();
        if (merrcount > 0)
        {
            for (int i = 1; i <= merrcount; i++)
            {
                //ȡ������Ϣ
                LMUWSchema tLMUWSchema = new LMUWSchema();
                tLMUWSchema = tLMUWSetContUnpass.get(i);
                //������ˮ��
                String tserialno = "" + i;

                tLCCUWErrorSchema.setSerialNo(tserialno);
                tLCCUWErrorSchema.setUWRuleCode(tLMUWSchema.getUWCode()); //�˱��������
                tLCCUWErrorSchema.setUWError(tLMUWSchema.getRemark().trim()); //�˱�������Ϣ�����˱������������������
                tLCCUWErrorSchema.setUWGrade(tLMUWSchema.getUWGrade());
                tLCCUWErrorSchema.setCurrValue(""); //��ǰֵ

                LCCUWErrorSchema ttLCCUWErrorSchema = new LCCUWErrorSchema();
                ttLCCUWErrorSchema.setSchema(tLCCUWErrorSchema);
                mLCCUWErrorSet.add(ttLCCUWErrorSchema);
            }
        }

        return true;
    }


    /**
     * ׼�����ֺ˱���Ϣ
     * �����������������򷵻�false,���򷵻�true
     * @param tLCPolSchema LCPolSchema
     * @param tLMUWSetUnpass LMUWSet
     * @return boolean
     */
    private boolean preparePolUW(LCPolSchema tLCPolSchema, LMUWSet tLMUWSetUnpass)
    {
        int tuwno = 0;
        LCUWMasterSchema tLCUWMasterSchema = new LCUWMasterSchema();
        LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
        tLCUWMasterDB.setPolNo(mOldPolNo);
        LCUWMasterSet tLCUWMasterSet = new LCUWMasterSet();
        tLCUWMasterSet = tLCUWMasterDB.query();
        if (tLCUWMasterDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLCUWMasterDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWAtuoChkBL";
            tError.functionName = "prepareUW";
            tError.errorMessage = mOldPolNo + "���˺˱��ܱ�ȡ��ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        int n = tLCUWMasterSet.size();
        if (n == 0)
        {
            tLCUWMasterSchema.setContNo(mContNo);
            tLCUWMasterSchema.setGrpContNo(tLCPolSchema.getGrpContNo());
            tLCUWMasterSchema.setPolNo(mOldPolNo);
            tLCUWMasterSchema.setProposalContNo(mPContNo);
            tLCUWMasterSchema.setProposalNo(tLCPolSchema.getProposalNo());
            tLCUWMasterSchema.setUWNo(1);
            tLCUWMasterSchema.setInsuredNo(tLCPolSchema.getInsuredNo());
            tLCUWMasterSchema.setInsuredName(tLCPolSchema.getInsuredName());
            tLCUWMasterSchema.setAppntNo(tLCPolSchema.getAppntNo());
            tLCUWMasterSchema.setAppntName(tLCPolSchema.getAppntName());
            tLCUWMasterSchema.setAgentCode(tLCPolSchema.getAgentCode());
            tLCUWMasterSchema.setAgentGroup(tLCPolSchema.getAgentGroup());
            tLCUWMasterSchema.setUWGrade(mUWGrade); //�˱�����
            tLCUWMasterSchema.setAppGrade(mUWGrade); //�걨����
            tLCUWMasterSchema.setPostponeDay("");
            tLCUWMasterSchema.setPostponeDate("");
            tLCUWMasterSchema.setAutoUWFlag("1"); // 1 �Զ��˱� 2 �˹��˱�
            tLCUWMasterSchema.setState(mPolPassFlag);
            tLCUWMasterSchema.setPassFlag(mPolPassFlag);
            tLCUWMasterSchema.setHealthFlag("0");
            tLCUWMasterSchema.setSpecFlag("0");
            tLCUWMasterSchema.setQuesFlag("0");
            tLCUWMasterSchema.setReportFlag("0");
            tLCUWMasterSchema.setChangePolFlag("0");
            tLCUWMasterSchema.setPrintFlag("0");
            tLCUWMasterSchema.setManageCom(tLCPolSchema.getManageCom());
            tLCUWMasterSchema.setUWIdea("");
            tLCUWMasterSchema.setUpReportContent("");
            tLCUWMasterSchema.setOperator(mOperator); //����Ա
            tLCUWMasterSchema.setMakeDate(PubFun.getCurrentDate());
            tLCUWMasterSchema.setMakeTime(PubFun.getCurrentTime());
            tLCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
            tLCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());
        }
        else if (n == 1)
        {
            tLCUWMasterSchema = tLCUWMasterSet.get(1);

            tuwno = tLCUWMasterSchema.getUWNo();
            tuwno += 1;

            tLCUWMasterSchema.setUWNo(tuwno);
            tLCUWMasterSchema.setProposalContNo(mPContNo);
            tLCUWMasterSchema.setState(mPolPassFlag);
            tLCUWMasterSchema.setPassFlag(mPolPassFlag);
            tLCUWMasterSchema.setAutoUWFlag("1"); // 1 �Զ��˱� 2 �˹��˱�
            tLCUWMasterSchema.setUWGrade(mUWGrade); //�˱�����
            tLCUWMasterSchema.setAppGrade(mUWGrade); //�걨����
            tLCUWMasterSchema.setOperator(mOperator); //����Ա
            tLCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
            tLCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());
        }
        else
        {
            // @@������
            this.mErrors.copyAllErrors(tLCUWMasterDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWAtuoChkBL";
            tError.functionName = "prepareUW";
            tError.errorMessage = mOldPolNo + "���˺˱��ܱ�ȡ���ݲ�Ψһ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCUWMasterSet.clear();
        mLCUWMasterSet.add(tLCUWMasterSchema);

        // �˱��켣��
        LCUWSubSchema tLCUWSubSchema = new LCUWSubSchema();
        LCUWSubDB tLCUWSubDB = new LCUWSubDB();
        tLCUWSubDB.setPolNo(mOldPolNo);
        LCUWSubSet tLCUWSubSet = new LCUWSubSet();
        tLCUWSubSet = tLCUWSubDB.query();
        if (tLCUWSubDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLCUWSubDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWAtuoChkBL";
            tError.functionName = "prepareUW";
            tError.errorMessage = mOldPolNo + "���˺˱��켣���ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        int m = tLCUWSubSet.size();
        if (m > 0)
        {
            tLCUWSubSchema.setUWNo(++m); //�ڼ��κ˱�
        }
        else
        {
            tLCUWSubSchema.setUWNo(1); //��1�κ˱�
        }

        tLCUWSubSchema.setContNo(mContNo);
        tLCUWSubSchema.setPolNo(mOldPolNo);
        tLCUWSubSchema.setGrpContNo(tLCUWMasterSchema.getGrpContNo());
        tLCUWSubSchema.setProposalContNo(tLCUWMasterSchema.getProposalContNo());
        tLCUWSubSchema.setProposalNo(tLCUWMasterSchema.getProposalNo());
        tLCUWSubSchema.setInsuredNo(tLCUWMasterSchema.getInsuredNo());
        tLCUWSubSchema.setInsuredName(tLCUWMasterSchema.getInsuredName());
        tLCUWSubSchema.setAppntNo(tLCUWMasterSchema.getAppntNo());
        tLCUWSubSchema.setAppntName(tLCUWMasterSchema.getAppntName());
        tLCUWSubSchema.setAgentCode(tLCUWMasterSchema.getAgentCode());
        tLCUWSubSchema.setAgentGroup(tLCUWMasterSchema.getAgentGroup());
        tLCUWSubSchema.setUWGrade(tLCUWMasterSchema.getUWGrade()); //�˱�����
        tLCUWSubSchema.setAppGrade(tLCUWMasterSchema.getAppGrade()); //���뼶��
        tLCUWSubSchema.setAutoUWFlag(tLCUWMasterSchema.getAutoUWFlag());
        tLCUWSubSchema.setState(tLCUWMasterSchema.getState());
        tLCUWSubSchema.setPassFlag(tLCUWMasterSchema.getState());
        tLCUWSubSchema.setPostponeDay(tLCUWMasterSchema.getPostponeDay());
        tLCUWSubSchema.setPostponeDate(tLCUWMasterSchema.getPostponeDate());
        tLCUWSubSchema.setUpReportContent(tLCUWMasterSchema.getUpReportContent());
        tLCUWSubSchema.setHealthFlag(tLCUWMasterSchema.getHealthFlag());
        tLCUWSubSchema.setSpecFlag(tLCUWMasterSchema.getSpecFlag());
        tLCUWSubSchema.setSpecReason(tLCUWMasterSchema.getSpecReason());
        tLCUWSubSchema.setQuesFlag(tLCUWMasterSchema.getQuesFlag());
        tLCUWSubSchema.setReportFlag(tLCUWMasterSchema.getReportFlag());
        tLCUWSubSchema.setChangePolFlag(tLCUWMasterSchema.getChangePolFlag());
        tLCUWSubSchema.setChangePolReason(tLCUWMasterSchema.getChangePolReason());
        tLCUWSubSchema.setAddPremReason(tLCUWMasterSchema.getAddPremReason());
        tLCUWSubSchema.setPrintFlag(tLCUWMasterSchema.getPrintFlag());
        tLCUWSubSchema.setPrintFlag2(tLCUWMasterSchema.getPrintFlag2());
        tLCUWSubSchema.setUWIdea(tLCUWMasterSchema.getUWIdea());
        tLCUWSubSchema.setOperator(tLCUWMasterSchema.getOperator()); //����Ա
        tLCUWSubSchema.setManageCom(tLCUWMasterSchema.getManageCom());
        tLCUWSubSchema.setMakeDate(PubFun.getCurrentDate());
        tLCUWSubSchema.setMakeTime(PubFun.getCurrentTime());
        tLCUWSubSchema.setModifyDate(PubFun.getCurrentDate());
        tLCUWSubSchema.setModifyTime(PubFun.getCurrentTime());

        mLCUWSubSet.clear();
        mLCUWSubSet.add(tLCUWSubSchema);

        // �˱�������Ϣ��
        LCUWErrorSchema tLCUWErrorSchema = new LCUWErrorSchema();
        LCUWErrorDB tLCUWErrorDB = new LCUWErrorDB();
        tLCUWErrorDB.setPolNo(mOldPolNo);
        LCUWErrorSet tLCUWErrorSet = new LCUWErrorSet();
        tLCUWErrorSet = tLCUWErrorDB.query();
        if (tLCUWErrorDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLCUWErrorDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWAtuoChkBL";
            tError.functionName = "prepareUW";
            tError.errorMessage = mOldPolNo + "���˴�����Ϣ���ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        tLCUWErrorSchema.setSerialNo("0");
        if (m > 0)
        {
            tLCUWErrorSchema.setUWNo(m);
        }
        else
        {
            tLCUWErrorSchema.setUWNo(1);
        }
        tLCUWErrorSchema.setContNo(mContNo);
        tLCUWErrorSchema.setGrpContNo(tLCUWMasterSchema.getGrpContNo());
        tLCUWErrorSchema.setProposalContNo(mPContNo);
        tLCUWErrorSchema.setPolNo(mOldPolNo);
        tLCUWErrorSchema.setProposalNo(tLCPolSchema.getProposalNo());
        tLCUWErrorSchema.setInsuredNo(tLCPolSchema.getInsuredNo());
        tLCUWErrorSchema.setInsuredName(tLCPolSchema.getInsuredName());
        tLCUWErrorSchema.setAppntNo(tLCPolSchema.getAppntNo());
        tLCUWErrorSchema.setAppntName(tLCPolSchema.getAppntName());
        tLCUWErrorSchema.setManageCom(tLCPolSchema.getManageCom());
        tLCUWErrorSchema.setUWRuleCode(""); //�˱��������
        tLCUWErrorSchema.setUWError(""); //�˱�������Ϣ
        tLCUWErrorSchema.setCurrValue(""); //��ǰֵ
        tLCUWErrorSchema.setModifyDate(PubFun.getCurrentDate());
        tLCUWErrorSchema.setModifyTime(PubFun.getCurrentTime());
        tLCUWErrorSchema.setUWPassFlag(mPolPassFlag);

        //ȡ�˱�������Ϣ
        mLCUWErrorSet.clear();
        int merrcount = tLMUWSetUnpass.size();
        if (merrcount > 0)
        {
            for (int i = 1; i <= merrcount; i++)
            {
                //ȡ������Ϣ
                LMUWSchema tLMUWSchema = new LMUWSchema();
                tLMUWSchema = tLMUWSetUnpass.get(i);
                //������ˮ��
                String tserialno = "" + i;

                tLCUWErrorSchema.setSerialNo(tserialno);
                tLCUWErrorSchema.setUWRuleCode(tLMUWSchema.getUWCode()); //�˱��������
                tLCUWErrorSchema.setUWError(tLMUWSchema.getRemark().trim()); //�˱�������Ϣ�����˱������������������
                tLCUWErrorSchema.setUWGrade(tLMUWSchema.getUWGrade());
                tLCUWErrorSchema.setCurrValue(""); //��ǰֵ

                LCUWErrorSchema ttLCUWErrorSchema = new LCUWErrorSchema();
                ttLCUWErrorSchema.setSchema(tLCUWErrorSchema);
                mLCUWErrorSet.add(ttLCUWErrorSchema);
            }
        }
        return true;
    }


    /**
     * ׼������ǰ̨ͳһ�洢����
     * �����������������򷵻�false,���򷵻�true
     * @param tLCContSchema LCContSchema
     * @return boolean
     */
    private boolean prepareOutputData(LCContSchema tLCContSchema)
    {

        mMap.put(tLCContSchema, "UPDATE");
//          if(mLCCUWMasterSet.size() ==1)
        mMap.put(mLCCUWMasterSet.get(1), "DELETE&INSERT");
        mMap.put(mLCCUWSubSet, "INSERT");
        mMap.put(mLCCUWErrorSet, "INSERT");

        mMap.put(mAllLCPolSet, "UPDATE");
        int n = mAllLCUWMasterSet.size();
        for (int i = 1; i <= n; i++)
        {
            LCUWMasterSchema tLCUWMasterSchema = mAllLCUWMasterSet.get(i);
            mMap.put(tLCUWMasterSchema, "DELETE&INSERT");
        }
        mMap.put(mAllLCUWSubSet, "INSERT");
        mMap.put(mAllLCErrSet, "INSERT");

        return false;
    }


    /**
     * У��ҵ������
     * @return boolean
     */
    private boolean checkData()
    {
        //У��˱�����
        if (!checkUWGrade())
        {
            return false;
        }

        //У���Ƿ񸴺�
        if (!checkApprove(mLCContSchema))
        {
            return false;
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
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintSendNoticeAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintSendNoticeAfterInitService";
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
            tError.moduleName = "PEdorUWSendNoticeAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintSendNoticeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCContSchema = (LCContSchema) mTransferData.getValueByName("LCContSchema"); //�����������л�ȡ��ͬ��¼������
        if (mLCContSchema == null)
        {
            CError tError = new CError();
            tError.moduleName = "UWAutoChkBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "��ͬ����Ϣδ��ѯ��!";
            this.mErrors.addOneError(tError);
            System.out.println("NO LCCONTSCHEMA");
            return false;
        }
//        System.out.println(mLCContSchema.getContNo());
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mLCContSchema.getContNo());
        if (tLCContDB.getInfo()) //��֤LCCont�����Ƿ���ڸú�ͬ���¼
        {
            mLCContSchema.setSchema(tLCContDB.getSchema());
        }
        else
        {
            CError tError = new CError();
            tError.moduleName = "UWAutoChkBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "��ͬ��Ϊ" + mLCContSchema.getContNo() + "δ��ѯ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return boolean
     */
    private boolean prepareTransferData()
    {
        mTransferData.setNameAndValue("ContNo", mLCContSchema.getContNo());
        mTransferData.setNameAndValue("PrtNo", mLCContSchema.getPrtNo());
        mTransferData.setNameAndValue("AppntNo", mLCContSchema.getAppntNo());
        mTransferData.setNameAndValue("AppntName", mLCContSchema.getAppntName());
        mTransferData.setNameAndValue("ProposalContNo", mLCContSchema.getProposalContNo());
        mTransferData.setNameAndValue("AgentCode", mLCContSchema.getAgentCode());
        LAAgentDB tLAAgentDB = new LAAgentDB();
//        LAAgentSet tLAAgentSet = new LAAgentSet();
        tLAAgentDB.setAgentCode(mLCContSchema.getAgentCode());
        LAAgentSet tLAAgentSet = tLAAgentDB.query();
        if (tLAAgentSet == null || tLAAgentSet.size() != 1)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "UWAutoChkAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "�����˱�LAAgent��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mTransferData.setNameAndValue("AgentName", tLAAgentSet.get(1).getName());
        mTransferData.setNameAndValue("ManageCom", mLCContSchema.getManageCom());
        mTransferData.setNameAndValue("UWFlag", mPolPassFlag);
        mTransferData.setNameAndValue("UWDate", PubFun.getCurrentDate());
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

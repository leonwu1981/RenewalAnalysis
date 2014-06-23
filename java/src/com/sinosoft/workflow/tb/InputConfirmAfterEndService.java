/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LCContDB;
import com.sinosoft.lis.db.LMCheckFieldDB;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.LCIssuePolSet;
import com.sinosoft.lis.vschema.LDSpotTrackSet;
import com.sinosoft.lis.vschema.LCPolSet;
import com.sinosoft.lis.vschema.LMCheckFieldSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterEndService;


/**
 * <p>Title: ������������:��������Լ¼����� </p>
 * <p>Description: ����¼����Ϻ��У�� </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class InputConfirmAfterEndService implements AfterEndService
{

    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();


    /** �����洫�����ݵ����� */
    private VData mResult = new VData();


    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();
    /** �����洫�����ݵ����� */
    private VData mInputData;

    /** ҵ������ */
    private LCContSchema mLCContSchema = new LCContSchema();
    private LCPolSet mLCPolSet = new LCPolSet();
//    private LMCheckFieldSet mLMCheckFieldSet = new LMCheckFieldSet();
    private LCIssuePolSet mLCIssuePolSet = new LCIssuePolSet();
    private LDSpotTrackSet mLDSpotTrackSet = new LDSpotTrackSet();
    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;
    private String mMissionID;


    /** ҵ�������ֶ� */
    private String mContNo;

    public InputConfirmAfterEndService()
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
         mInputData = (VData) cInputData.clone();
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
        //map.put(mLCIssuePolSet, "INSERT");
        mResult.add(map);


        return true;
    }


    /**
     * У��ҵ������
     * @return
     */
    private boolean checkData()
    {
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mContNo);
        if (!tLCContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "InputConfirmAfterEndService";
            tError.functionName = "checkData";
            tError.errorMessage = "��ͬ������Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema = tLCContDB.getSchema();

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
            tError.moduleName = "InputConfirmAfterEndService";
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
            tError.moduleName = "InputConfirmAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��ù����������
        mManageCom = mGlobalInput.ManageCom;
        if (mManageCom == null || mManageCom.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "InputConfirmAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������mManageComʧ��!";
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
            tError.moduleName = "InputConfirmAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ�������������ContNo
        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mContNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "InputConfirmAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ContNoʧ��!";
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
        String tCalCode = "";
        String tCheckResult = "";
        LMCheckFieldSet tLMCheckFieldSet = null;
        //���к�ͬ����У��
        tLMCheckFieldSet = getCheckFieldSetCont();
        if (tLMCheckFieldSet == null || tLMCheckFieldSet.size() <= 0)
        {
        }
        else
        {
            for (int i = 1; i <= tLMCheckFieldSet.size(); i++)
            {
                tCalCode = tLMCheckFieldSet.get(i).getCalCode(); //��ü����������Ӧ��calcode
                tCheckResult = CheckCont(tCalCode); //��ͬУ��
                if (!tCheckResult.equals("") || tCheckResult.length() > 0)
                {
                    //׼����Ҫ�ύ�����������
                    if (!prepareContIssue(tLMCheckFieldSet.get(i)))
                    {
                        CError tError = new CError();
                        tError.moduleName = "InputConfirmAfterEndService";
                        tError.functionName = "getInputData";
                        tError.errorMessage = "׼�����������ʧ��!";
                        this.mErrors.addOneError(tError);
                        return false;
                    }
                }
            }
        }

        //���ּ���У��
        for (int i = 1; i <= mLCPolSet.size(); i++)
        {
            if (tLMCheckFieldSet != null)
            {
                tLMCheckFieldSet.clear();
            }
            //ȡ�ú�ͬ�µ�У�����
            tLMCheckFieldSet = getCheckFieldSetPol(mLCPolSet.get(i));
            int n = tLMCheckFieldSet.size();
            if (n == 0)
            {}
            else
            {
                for (int j = 1; j <= n; j++)
                {
                    tCalCode = tLMCheckFieldSet.get(j).getCalCode();
                    tCheckResult = CheckPol(mLCPolSet.get(i), tCalCode);
                    if (!tCheckResult.equals("") || tCheckResult.length() > 0)
                    {
                        //׼����Ҫ�ύ�����������
                        if (!preparePolIssue(mLCPolSet.get(i), tLMCheckFieldSet.get(i)))
                        {
                            CError tError = new CError();
                            tError.moduleName = "InputConfirmAfterEndService";
                            tError.functionName = "getInputData";
                            tError.errorMessage = "׼�����������ʧ��!";
                            this.mErrors.addOneError(tError);
                            return false;
                        }
                    }

                }
            }
        }
        return true;
    }


    /**
     * preparePolIssue
     *
     * @param lCPolSchema LCPolSchema
     * @param lMCheckFieldSchema LMCheckFieldSchema
     * @return boolean
     */
    private boolean preparePolIssue(LCPolSchema tlCPolSchema,
            LMCheckFieldSchema tlMCheckFieldSchema)
    {
        LCIssuePolSchema tLCIssuePolSchema = new LCIssuePolSchema();

        tLCIssuePolSchema.setGrpContNo(tlCPolSchema.getGrpContNo());
        tLCIssuePolSchema.setContNo(tlCPolSchema.getContNo());
        tLCIssuePolSchema.setProposalContNo(tlCPolSchema.getContNo());
//        tLCIssuePolSchema.setPrtSeq("");
        tLCIssuePolSchema.setSerialNo(PubFun1.CreateMaxNo("QustSerlNo", 20));
        tLCIssuePolSchema.setFieldName(tlMCheckFieldSchema.getFieldName());
        tLCIssuePolSchema.setLocation(tlMCheckFieldSchema.getLocation());
        tLCIssuePolSchema.setIssueType("9999999999"); //��ʱ��Ϊʮ����9��
        tLCIssuePolSchema.setOperatePos("0");
        tLCIssuePolSchema.setBackObjType("5");
//        tLCIssuePolSchema.setBackObj("");
        tLCIssuePolSchema.setIsueManageCom(mManageCom);
        tLCIssuePolSchema.setIssueCont(tlMCheckFieldSchema.getMsg());
        tLCIssuePolSchema.setPrintCount(0);
//        tLCIssuePolSchema.setNeedPrint("");
//        tLCIssuePolSchema.setReplyMan("");
//        tLCIssuePolSchema.setReplyResult("");
        tLCIssuePolSchema.setState("0");
        tLCIssuePolSchema.setOperator(tlCPolSchema.getOperator());
        tLCIssuePolSchema.setManageCom(tlCPolSchema.getManageCom());
        tLCIssuePolSchema.setMakeDate(PubFun.getCurrentDate());
        tLCIssuePolSchema.setMakeTime(PubFun.getCurrentTime());
        tLCIssuePolSchema.setModifyDate(PubFun.getCurrentDate());
        tLCIssuePolSchema.setModifyTime(PubFun.getCurrentTime());

        mLCIssuePolSet.add(tLCIssuePolSchema);
        return true;
    }


    /**
     * prepareIssue
     *
     * @param lMCheckFieldSchema LMCheckFieldSchema
     * @return boolean
     */
    private boolean prepareContIssue(LMCheckFieldSchema tlMCheckFieldSchema)
    {
        LCIssuePolSchema tLCIssuePolSchema = new LCIssuePolSchema();

        tLCIssuePolSchema.setGrpContNo(mLCContSchema.getContNo());
        tLCIssuePolSchema.setContNo(mLCContSchema.getContNo());
        tLCIssuePolSchema.setProposalContNo(mLCContSchema.getContNo());
//        tLCIssuePolSchema.setPrtSeq("");
        tLCIssuePolSchema.setSerialNo(PubFun1.CreateMaxNo("QustSerlNo", 20));
        tLCIssuePolSchema.setFieldName(tlMCheckFieldSchema.getFieldName());
        tLCIssuePolSchema.setLocation(tlMCheckFieldSchema.getLocation());
        tLCIssuePolSchema.setIssueType("9999999999"); //��ʱ��Ϊʮ����9��
        tLCIssuePolSchema.setOperatePos("0");
        tLCIssuePolSchema.setBackObjType("5");
//        tLCIssuePolSchema.setBackObj("");
        tLCIssuePolSchema.setIsueManageCom(mManageCom);
        tLCIssuePolSchema.setIssueCont(tlMCheckFieldSchema.getMsg());
        tLCIssuePolSchema.setPrintCount(0);
//        tLCIssuePolSchema.setNeedPrint("");
//        tLCIssuePolSchema.setReplyMan("");
//        tLCIssuePolSchema.setReplyResult("");
        tLCIssuePolSchema.setState("0");
        tLCIssuePolSchema.setOperator(mOperater);
        tLCIssuePolSchema.setManageCom(mManageCom);
        tLCIssuePolSchema.setMakeDate(PubFun.getCurrentDate());
        tLCIssuePolSchema.setMakeTime(PubFun.getCurrentTime());
        tLCIssuePolSchema.setModifyDate(PubFun.getCurrentDate());
        tLCIssuePolSchema.setModifyTime(PubFun.getCurrentTime());

        mLCIssuePolSet.add(tLCIssuePolSchema);

        return true;
    }


    /**
     * CheckCont
     *
     * @param tCalCode String
     * @return String
     */
    private String CheckCont(String tCalCode)
    {
        String tCheckResult = "";
        Calculator mCalculator = new Calculator();
        mCalculator.setCalCode(tCalCode);

        mCalculator.addBasicFactor("ContNo", mLCContSchema.getContNo());

        String tStr = "";
        tStr = mCalculator.calculate();
        if (tStr.trim().equals(""))
        {
            tCheckResult = "";
        }
        else
        {
            tCheckResult = tStr.trim();
        }

        return tCheckResult;
    }


    /**
     * CheckPol
     *
     * @param lCPolSchema LCPolSchema
     * @param tCalCode String
     * @return String
     */
    private static String CheckPol(LCPolSchema tlCPolSchema, String tCalCode)
    {
        String tCheckResult = "";
        Calculator mCalculator = new Calculator();
        mCalculator.setCalCode(tCalCode);

        mCalculator.addBasicFactor("PolNo", tlCPolSchema.getPolNo());

        String tStr = "";
        tStr = mCalculator.calculate();
        if (tStr.trim().equals(""))
        {
            tCheckResult = "";
        }
        else
        {
            tCheckResult = tStr.trim();
        }

        return tCheckResult;
    }


    /**
     * getCheckFieldSet
     *
     * @return LMCheckFieldSet
     */
    private LMCheckFieldSet getCheckFieldSetCont()
    {
        LMCheckFieldSet tLMCheckFieldSet = new LMCheckFieldSet();
        LMCheckFieldDB tLMCheckFieldDB = new LMCheckFieldDB();
        //������ͬ�Զ����˹���
        String tSql =
                "select * from lmcheckfield where riskcode = '000000' and pagelocation = 'TBINPUT#TBTYPEIC' ";
        System.out.println(tSql);
        tLMCheckFieldSet = tLMCheckFieldDB.executeQuery(tSql);
        if (tLMCheckFieldSet == null)
        {
            CError tError = new CError();
            tError.moduleName = "InputConfirmAfterEndService";
            tError.functionName = "getCheckFieldSet";
            tError.errorMessage = "��ѯ�㷨������ʧ��!";
            this.mErrors.addOneError(tError);
            return null;
        }
        return tLMCheckFieldSet;
    }


    /**
     * getCheckFieldSet
     *
     * @return LMCheckFieldSet
     */
    private LMCheckFieldSet getCheckFieldSetPol(LCPolSchema tLCPolSchema)
    {
        LMCheckFieldSet tLMCheckFieldSet = new LMCheckFieldSet();
        LMCheckFieldDB tLMCheckFieldDB = new LMCheckFieldDB();
        //������ͬ�Զ����˹���
        String tSql =
                "select * from lmcheckfield where 1=1 and (riskcode = '000000' or riskcode = '"
                + tLCPolSchema.getRiskCode() + "' and pagelocation = 'TBINPUT#TBTYPEI' ";

        tLMCheckFieldSet = tLMCheckFieldDB.executeQuery(tSql);
        if (tLMCheckFieldSet == null || tLMCheckFieldSet.size() <= 0)
        {
            CError tError = new CError();
            tError.moduleName = "InputConfirmAfterEndService";
            tError.functionName = "getCheckFieldSet";
            tError.errorMessage = "��ѯ�㷨������ʧ��!";
            this.mErrors.addOneError(tError);
            return null;
        }
        return tLMCheckFieldSet;
    }

    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����,׼������
     * @return
     */
    private static boolean prepareTransferData()
    {
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

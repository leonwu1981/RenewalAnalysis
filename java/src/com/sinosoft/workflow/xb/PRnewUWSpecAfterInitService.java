/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.xb;

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
 * <p>Description:�������ڵ�����:�����˹��˱���Լ¼������� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PRnewUWSpecAfterInitService implements AfterInitService
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
    private String mPrtNo;
//    private String mInsuredNo;
    private String mSpecReason;
    private String mRemark;
    private Reflections mReflections = new Reflections();

    /**ִ��������������Լ�������0000000003*/
    /**������*/
    private LCPolSchema mLCPolSchema = new LCPolSchema();
    /** �����˱����� */
    private LCUWMasterSchema mLCUWMasterSchema = new LCUWMasterSchema();
    /** ��Լ�� */
    private LCSpecSchema mLCSpecSchema = new LCSpecSchema();
    private LCSpecSet mLCSpecSet = new LCSpecSet();
    private LBSpecSet mLBSpecSet = new LBSpecSet();
    /** ������ע�� */
    private LCRemarkSchema mLCRemarkSchema = new LCRemarkSchema();
//    private LBRemarkSchema mLBRemarkSchema = new LBRemarkSchema();
    private LBRemarkSet mLBRemarkSet = new LBRemarkSet();
    private LCRemarkSet mLCRemarkSet = new LCRemarkSet();

    public PRnewUWSpecAfterInitService()
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

        return true;
    }


    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {
        // �˱���Լ��Ϣ
        if (!prepareSpec())
        {
            return false;
        }
        // �˱���ע��Ϣ
        if (!prepareRemark())
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
        tLCPolDB.setPolNo(mPolNo);
        if (!tLCPolDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PRnewSpecAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mPolNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCPolSchema.setSchema(tLCPolDB);
        mPrtNo = mLCPolSchema.getPrtNo();

        //У�����������˱�����
        //У�鱣����Ϣ
        LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
        tLCUWMasterDB.setProposalNo(mPolNo);
        if (!tLCUWMasterDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PRnewSpecAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mPolNo + "���������˱�������Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCUWMasterSchema.setSchema(tLCUWMasterDB);

        // ����δ��ӡ״̬�ĺ˱�֪ͨ���ڴ�ӡ������ֻ����һ��
        // ������ͬһ���������ͣ�ͬһ���������룬ͬһ��������������
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

        tLOPRTManagerDB.setCode(PrintManagerBL.CODE_UW); //�˱�֪ͨ��
        tLOPRTManagerDB.setOtherNo(mPolNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_INDPOL); //������
        tLOPRTManagerDB.setStandbyFlag2(mLCPolSchema.getPrtNo());
        tLOPRTManagerDB.setStateFlag("0");

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "��ѯ��ӡ�������Ϣ����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLOPRTManagerSet.size() != 0)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWAutoHealthAfterInitService";
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
            tError.moduleName = "PRnewUWSpecAfterInitService";
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
            tError.moduleName = "PRnewUWSpecAfterInitService";
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
            tError.moduleName = "PRnewUWSpecAfterInitService";
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
            tError.moduleName = "PRnewUWSpecAfterInitService";
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
            tError.moduleName = "PRnewUWSpecAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������PolNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mSpecReason = (String) mTransferData.getValueByName("SpecReason");
//	if ( mSpecReason == null  )
//	{
//	  // @@������
//	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
//	  CError tError = new CError();
//	  tError.moduleName = "PRnewUWSpecAfterInitService";
//	  tError.functionName = "getInputData";
//	  tError.errorMessage = "ǰ̨����ҵ��������SpecReasonʧ��!";
//	  this.mErrors .addOneError(tError) ;
//	  return false;
//	}  //ע�����ǵ�:ֻ¼����Լ����,����¼����Լԭ��ʱ����



        //���ҵ����Լ֪ͨ������
        mLCSpecSchema = (LCSpecSchema) mTransferData.getValueByName("LCSpecSchema");
        if (mLCSpecSchema == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewUWSpecAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨������ҵ����Լ����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mRemark = (String) mTransferData.getValueByName("Remark");
//	if ( mRemark == null  )
//	{
//	  // @@������
//	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
//	  CError tError = new CError();
//	  tError.moduleName = "PRnewUWSpecAfterInitService";
//	  tError.functionName = "getInputData";
//	  tError.errorMessage = "ǰ̨����ҵ��������mRemarkʧ��!";
//	  this.mErrors .addOneError(tError) ;
//	  return false;
//	}  //ע�����ǵ�:ֻ¼����Լ����,����¼��������עʱ����


        return true;
    }


    /**
     * ׼����Լ������Ϣ
     * �����������������򷵻�false,���򷵻�true
     */
    private boolean prepareSpec()
    {

        //ȡ��������
        LMRiskSchema tLMRiskSchema = new LMRiskSchema();
        LMRiskDB tLMRiskDB = new LMRiskDB();
        tLMRiskDB.setRiskCode(mLCPolSchema.getRiskCode());
        //tLMRiskDB.setRiskVer(mLCPolSchema.getRiskVersion());
        if (!tLMRiskDB.getInfo())
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWSpecAfterInitService";
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
            tError.moduleName = "PRnewUWSpecAfterInitService";
            tError.functionName = "prepareHealth";
            tError.errorMessage = "ȡ����������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //׼����Լ������
        if (mLCSpecSchema != null && !mLCSpecSchema.getSpecContent().trim().equals(""))
        {
//		mLCSpecSchema.setSpecNo(PubFun1.CreateMaxNo("SpecNo",PubFun.getNoLimit(mGlobalInput.ComCode)));
            //mLCSpecSchema.setPolNo(mPolNo);
            //mLCSpecSchema.setPolType("1");
            //mLCSpecSchema.setEndorsementNo("");
            //mLCSpecSchema.setSpecType();
            //mLCSpecSchema.setSpecCode();
            //mLCSpecSchema.setSpecContent();//ǰ̨��׼��
            mLCSpecSchema.setPrtFlag("1");
            mLCSpecSchema.setBackupType("");
            //mLCSpecSchema.setState("0") ;
            mLCSpecSchema.setOperator(mOperater);
            mLCSpecSchema.setMakeDate(PubFun.getCurrentDate());
            mLCSpecSchema.setMakeTime(PubFun.getCurrentTime());
            mLCSpecSchema.setModifyDate(PubFun.getCurrentDate());
            mLCSpecSchema.setModifyTime(PubFun.getCurrentTime());
        }
        else
        {
            mLCSpecSchema = null;
        }

        //׼��������һ�ε���Լ��Ϣ
        LCSpecDB tLCSpecDB = new LCSpecDB();

        tLCSpecDB.setPolNo(mPolNo);
        mLCSpecSet = tLCSpecDB.query();
        System.out.println("LCSpecSet.size()" + mLCSpecSet.size());
        for (int i = 1; i <= mLCSpecSet.size(); i++)
        {
            LBSpecSchema tLBSpecSchema = new LBSpecSchema();
            mReflections.transFields(tLBSpecSchema, mLCSpecSet.get(i));
            tLBSpecSchema.setMakeDate(PubFun.getCurrentDate());
            tLBSpecSchema.setMakeTime(PubFun.getCurrentTime());
            mLBSpecSet.add(tLBSpecSchema);
        }

        //׼�������˱�������Ϣ
        LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
        tLCUWMasterDB.setProposalNo(mPolNo);
        if (!tLCUWMasterDB.getInfo())
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PRnewUWSpecAfterInitService";
            tError.functionName = "prepareSpec";
            tError.errorMessage = "�����������˱�������Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCUWMasterSchema.setSchema(tLCUWMasterDB);
        String tSpecReason = (String) mTransferData.getValueByName("SpecReason");
        if (tSpecReason != null && !tSpecReason.trim().equals(""))
        {
            mLCUWMasterSchema.setSpecReason(tSpecReason);
        }
        else
        {
            mLCUWMasterSchema.setSpecReason("");
        }

        if (mLCSpecSchema != null)
        {
            mLCUWMasterSchema.setSpecFlag("1"); //��Լ��ʶ
        }
        else
        {
            mLCUWMasterSchema.setSpecFlag("0"); //��Լ��ʶ
        }

        mLCUWMasterSchema.setOperator(mOperater);
        mLCUWMasterSchema.setManageCom(mManageCom);
        mLCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
        mLCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());

        return true;
    }


    /**
     * ׼����Լ������Ϣ
     * �����������������򷵻�false,���򷵻�true
     */
    private boolean prepareRemark()
    {

        //׼��������ע������
        if (mRemark != null && !mRemark.trim().equals(""))
        {
            String tSerialNo = PubFun1.CreateMaxNo("RemarkNo", 20);
            mLCRemarkSchema.setSerialNo(tSerialNo);
            mLCRemarkSchema.setPolNo(mPolNo);
            mLCRemarkSchema.setPrtNo(mPrtNo);
            mLCRemarkSchema.setOperatePos("1");
            mLCRemarkSchema.setRemarkCont(mRemark);
            mLCRemarkSchema.setManageCom(mManageCom);
            mLCRemarkSchema.setOperator(mOperater);
            mLCRemarkSchema.setMakeDate(PubFun.getCurrentDate());
            mLCRemarkSchema.setMakeTime(PubFun.getCurrentTime());
            mLCRemarkSchema.setModifyDate(PubFun.getCurrentDate());
            mLCRemarkSchema.setModifyTime(PubFun.getCurrentTime());
        }
        else
        {
            mLCRemarkSchema = null;
        }

        //׼��������һ�ε�������ע��Ϣ
        LCRemarkDB tLCRemarkDB = new LCRemarkDB();
        tLCRemarkDB.setPolNo(mPolNo);
        tLCRemarkDB.setPrtNo(mPrtNo);
        tLCRemarkDB.setOperatePos("1");
        tLCRemarkDB.setPolNo(mPolNo);
        mLCRemarkSet = tLCRemarkDB.query();
        System.out.println("tLCRemarkSet.size()" + mLCRemarkSet.size());
        for (int i = 1; i <= mLCRemarkSet.size(); i++)
        {
            LBRemarkSchema tLBRemarkSchema = new LBRemarkSchema();
            mReflections.transFields(tLBRemarkSchema, mLCRemarkSet.get(i));
            tLBRemarkSchema.setEdorNo("99999999999999999999");
            tLBRemarkSchema.setMakeDate(PubFun.getCurrentDate());
            tLBRemarkSchema.setMakeTime(PubFun.getCurrentTime());
            mLBRemarkSet.add(tLBRemarkSchema);
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

        //���ɾ����һ����Լ����
        if (mLCSpecSet != null && mLCSpecSet.size() > 0)
        {
            map.put(mLCSpecSet, "DELETE");
        }
        //��ӱ���������Լ����
        if (mLCSpecSchema != null)
        {
            map.put(mLCSpecSchema, "INSERT");
        }

        //���ɾ����һ��������ע����
        if (mLCRemarkSet != null && mLCRemarkSet.size() > 0)
        {
            map.put(mLCRemarkSet, "DELETE");
        }
        //���������Լ��������
        if (mLBSpecSet != null && mLBSpecSet.size() > 0)
        {
            map.put(mLBSpecSet, "INSERT");
        }

        //��ӱ���������ע����
        if (mLCRemarkSchema != null)
        {
            map.put(mLCRemarkSchema, "INSERT");
        }

        //���������ע��������
        if (mLBRemarkSet != null && mLBRemarkSet.size() > 0)
        {
            map.put(mLBRemarkSet, "INSERT");
        }

        //������������˱�����֪ͨ���ӡ���������
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

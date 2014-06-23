/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.bq;

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
 * <p>Description:�������ڵ�����:��ȫ�˹��˱���Լ¼������� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PEdorUWSpecAfterInitService implements AfterInitService
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
    private String mPolNo;
    private String mPrtNo;
    private String mContNo;
    private String mSpecReason;
    private String mEdorType;
    private String mRemark;
    private Reflections mReflections = new Reflections();

    /**ִ�б�ȫ��������Լ�������0000000003*/
    /**������*/
    private LCPolSchema mLCPolSchema = new LCPolSchema();
    /** ��ȫ�˱����� */
    private LPUWMasterSchema mLPUWMasterSchema = new LPUWMasterSchema();
    /** ��Լ�� */
    private LPSpecSchema mLPSpecSchema = new LPSpecSchema();
    private LPSpecSet mLPSpecSet = new LPSpecSet();
    private LBSpecSet mLBSpecSet = new LBSpecSet();
    /** ��ȫ��ע�� */
    private LPRemarkSchema mLPRemarkSchema = new LPRemarkSchema();
//    private LBRemarkSchema mLBRemarkSchema = new LBRemarkSchema();
    private LBRemarkSet mLBRemarkSet = new LBRemarkSet();
    private LPRemarkSet mLPRemarkSet = new LPRemarkSet();

    public PEdorUWSpecAfterInitService()
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
            tError.moduleName = "PEdorSpecAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mPolNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCPolSchema.setSchema(tLCPolDB);
        mPrtNo = mLCPolSchema.getPrtNo();

        //У�鱣ȫ�����˱�����
        //У�鱣����Ϣ
        LPUWMasterDB tLPUWMasterDB = new LPUWMasterDB();
        tLPUWMasterDB.setEdorNo(mEdorNo);
	tLPUWMasterDB.setContNo(mContNo) ;
        tLPUWMasterDB.setEdorType(mEdorType);
        tLPUWMasterDB.setPolNo(mPolNo);
        if (!tLPUWMasterDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorSpecAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "����" + mPolNo + "��ȫ�����˱�������Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLPUWMasterSchema.setSchema(tLPUWMasterDB);

        // ����δ��ӡ״̬�ĺ˱�֪ͨ���ڴ�ӡ������ֻ����һ��
        // ������ͬһ���������ͣ�ͬһ���������룬ͬһ��������������
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

        tLOPRTManagerDB.setCode(PrintManagerBL.CODE_UW); //�˱�֪ͨ��
        tLOPRTManagerDB.setOtherNo(mPolNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_INDPOL); //������
        tLOPRTManagerDB.setStandbyFlag2(mEdorNo);
        tLOPRTManagerDB.setStateFlag("0");

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "��ѯ��ӡ�������Ϣ����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLOPRTManagerSet.size() != 0)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorUWAutoHealthAfterInitService";
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
            tError.moduleName = "PEdorUWSpecAfterInitService";
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
            tError.moduleName = "PEdorUWSpecAfterInitService";
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
            tError.moduleName = "PEdorUWSpecAfterInitService";
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
            tError.moduleName = "PEdorUWSpecAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mEdorNo = (String) mTransferData.getValueByName("EdorNo");
        if (mEdorNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWSpecAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������EdorNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mPolNo = (String) mTransferData.getValueByName("PolNo");

        if (mPolNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWSpecAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������PolNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        String sqlContNo = "select contno from lcpol where polno ='"+mPolNo+"'";
        ExeSQL tExeSQL = new ExeSQL();
        mContNo = tExeSQL.getOneValue(sqlContNo);

        if (mContNo == null || "".equals(mContNo))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWSpecAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������ContNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }


        mSpecReason = (String) mTransferData.getValueByName("SpecReason");
//	if ( mSpecReason == null  )
//	{
//	  // @@������
//	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
//	  CError tError = new CError();
//	  tError.moduleName = "PEdorUWSpecAfterInitService";
//	  tError.functionName = "getInputData";
//	  tError.errorMessage = "ǰ̨����ҵ��������SpecReasonʧ��!";
//	  this.mErrors .addOneError(tError) ;
//	  return false;
//	}  //ע�����ǵ�:ֻ¼����Լ����,����¼����Լԭ��ʱ����



        //���ҵ����Լ֪ͨ������
        mLPSpecSchema = (LPSpecSchema) mTransferData.getValueByName("LPSpecSchema");

        if (mLPSpecSchema == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWSpecAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨������ҵ����Լ����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mEdorType = mLPSpecSchema.getEdorType();
        mRemark = (String) mTransferData.getValueByName("Remark");

//	if ( mRemark == null  )
//	{
//	  // @@������
//	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
//	  CError tError = new CError();
//	  tError.moduleName = "PEdorUWSpecAfterInitService";
//	  tError.functionName = "getInputData";
//	  tError.errorMessage = "ǰ̨����ҵ��������mRemarkʧ��!";
//	  this.mErrors .addOneError(tError) ;
//	  return false;
//	}  //ע�����ǵ�:ֻ¼����Լ����,����¼�뱣ȫ��עʱ����


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
            tError.moduleName = "PEdorUWSpecAfterInitService";
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
            tError.moduleName = "PEdorUWSpecAfterInitService";
            tError.functionName = "prepareHealth";
            tError.errorMessage = "ȡ����������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //׼����Լ������
        if (mLPSpecSchema != null && !"".equals(mLPSpecSchema.getSpecContent()))
        {
//		mLPSpecSchema.setSpecNo(PubFun1.CreateMaxNo("SpecNo",PubFun.getNoLimit(mGlobalInput.ComCode)));
            mLPSpecSchema.setPolNo(mPolNo);
            mLPSpecSchema.setProposalNo(mContNo);
            String maxnoSQL = "select max(serialno)+1 from lpspec where edorno='"
                +mEdorNo+"' and edortype='"+mLPSpecSchema.getEdorType()+"'";
            ExeSQL tExeSQL = new ExeSQL();
            String maxNo = tExeSQL.getOneValue(maxnoSQL);
            if(maxNo == null || "".equals(maxNo))
            {
              maxNo = "1";
            }
            mLPSpecSchema.setSerialNo(maxNo);
            mLPSpecSchema.setContNo(mContNo);
            mLPSpecSchema.setEdorNo(mEdorNo);
            mLPSpecSchema.setEndorsementNo(mEdorNo);
            //mLPSpecSchema.setSpecType();
            //mLPSpecSchema.setSpecCode();
            //mLPSpecSchema.setSpecContent();//ǰ̨��׼��
            mLPSpecSchema.setPrtFlag("1");
            mLPSpecSchema.setBackupType("");
//		mLPSpecSchema.setState("0") ;
            mLPSpecSchema.setOperator(mOperater);
            mLPSpecSchema.setMakeDate(PubFun.getCurrentDate());
            mLPSpecSchema.setMakeTime(PubFun.getCurrentTime());
            mLPSpecSchema.setModifyDate(PubFun.getCurrentDate());
            mLPSpecSchema.setModifyTime(PubFun.getCurrentTime());
        }
        else
        {
            mLPSpecSchema = null;
        }

        //׼����ȫ�˱�������Ϣ
        LPUWMasterDB tLPUWMasterDB = new LPUWMasterDB();
	  tLPUWMasterDB.setContNo(mContNo);
        tLPUWMasterDB.setEdorNo(mEdorNo);
        tLPUWMasterDB.setPolNo(mPolNo);
        tLPUWMasterDB.setEdorType(mLPSpecSchema.getEdorType());
        if (!tLPUWMasterDB.getInfo())
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "PEdorUWSpecAfterInitService";
            tError.functionName = "prepareSpec";
            tError.errorMessage = "�ޱ�ȫ�����˱�������Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLPUWMasterSchema.setSchema(tLPUWMasterDB);
        String tSpecReason = (String) mTransferData.getValueByName("SpecReason");
        if (tSpecReason != null && !tSpecReason.trim().equals(""))
        {
            mLPUWMasterSchema.setSpecReason(tSpecReason);
        }
        else
        {
            mLPUWMasterSchema.setSpecReason("");
        }

        if (mLPSpecSchema != null)
        {
            mLPUWMasterSchema.setSpecFlag("1"); //��Լ��ʶ
        }
        else
        {
            mLPUWMasterSchema.setSpecFlag("0"); //��Լ��ʶ
        }

        mLPUWMasterSchema.setOperator(mOperater);
        mLPUWMasterSchema.setManageCom(mManageCom);
        mLPUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
        mLPUWMasterSchema.setModifyTime(PubFun.getCurrentTime());

        return true;
    }


    /**
     * ׼����Լ������Ϣ
     * �����������������򷵻�false,���򷵻�true
     */
    private boolean prepareRemark()
    {

        //׼����ȫ��ע������
        if (mRemark != null && !mRemark.trim().equals(""))
        {
            String tSerialNo = PubFun1.CreateMaxNo("RemarkNo", 20);
            mLPRemarkSchema.setSerialNo(tSerialNo);
            mLPRemarkSchema.setEdorNo(mEdorNo);
            mLPRemarkSchema.setPolNo(mPolNo);
            mLPRemarkSchema.setPrtNo(mPrtNo);
            mLPRemarkSchema.setOperatePos("3");
            mLPRemarkSchema.setRemarkCont(mRemark);
            mLPRemarkSchema.setManageCom(mManageCom);
            mLPRemarkSchema.setOperator(mOperater);
            mLPRemarkSchema.setMakeDate(PubFun.getCurrentDate());
            mLPRemarkSchema.setMakeTime(PubFun.getCurrentTime());
            mLPRemarkSchema.setModifyDate(PubFun.getCurrentDate());
            mLPRemarkSchema.setModifyTime(PubFun.getCurrentTime());
        }
        else
        {
            mLPRemarkSchema = null;
        }

        //׼��������һ�εı�ȫ��ע��Ϣ
        LPRemarkDB tLPRemarkDB = new LPRemarkDB();
        tLPRemarkDB.setEdorNo(mEdorNo);
        tLPRemarkDB.setPolNo(mPolNo);
        tLPRemarkDB.setPrtNo(mPrtNo);
        tLPRemarkDB.setOperatePos("3");
        tLPRemarkDB.setPolNo(mPolNo);
        mLPRemarkSet = tLPRemarkDB.query();
        System.out.println("tLPRemarkSet.size()" + mLPRemarkSet.size());
        for (int i = 1; i <= mLPRemarkSet.size(); i++)
        {
            LBRemarkSchema tLBRemarkSchema = new LBRemarkSchema();
            mReflections.transFields(tLBRemarkSchema, mLPRemarkSet.get(i));
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
//        if (mLPSpecSet != null && mLPSpecSet.size() > 0)
//        {
//            map.put(mLPSpecSet, "DELETE");
//        }
        //��ӱ��α�ȫ��Լ����
        if (mLPSpecSchema != null)
        {
            map.put(mLPSpecSchema, "INSERT");
        }

//        //���ɾ����һ����ȫ��ע����
//        if (mLPRemarkSet != null && mLPRemarkSet.size() > 0)
//        {
//            map.put(mLPRemarkSet, "DELETE");
//        }
//        //��ӱ�ȫ��Լ��������
//        if (mLBSpecSet != null && mLBSpecSet.size() > 0)
//        {
//            map.put(mLBSpecSet, "INSERT");
//        }
//
//        //��ӱ��α�ȫ��ע����
//        if (mLPRemarkSchema != null)
//        {
//            map.put(mLPRemarkSchema, "INSERT");
//        }
//
//        //��ӱ�ȫ��ע��������
//        if (mLBRemarkSet != null && mLBRemarkSet.size() > 0)
//        {
//            map.put(mLBRemarkSet, "INSERT");
//        }

        //��ӱ�ȫ�����˱�����֪ͨ���ӡ���������
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

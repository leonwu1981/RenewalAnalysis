/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LAAgentDB;
import com.sinosoft.lis.db.LCContDB;
import com.sinosoft.lis.db.LCStopInsuredDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.schema.LCContSchema;
import com.sinosoft.lis.vschema.LAAgentSet;
import com.sinosoft.lis.vschema.LCContSet;
import com.sinosoft.lis.vschema.LCStopInsuredSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;


/**
 * <p>Title: ��������ͣ�ָ� </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @modify by Yuanaq
 * @version 1.1
 */

public class UWStopRecoverAfterInitService implements AfterInitService
{

    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();


    /** �����洫�����ݵ����� */
    private VData mInputData;


    /** �����洫�����ݵ����� */
    private VData mResult = new VData();


    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();
    private LCStopInsuredSet tLCStopInsuredSet = new LCStopInsuredSet();
    private LCContSchema mLCContSchema = new LCContSchema();

    /** ���ݲ����ַ��� */
    private String mOperater;
//    private String mManageCom;
//    private String mOperate;
    private String mMissionID;
    private String mSubMissionID;

    /** ҵ������ַ��� */
    private String mPrtNo;
    private String mContNo;

    public UWStopRecoverAfterInitService()
    {}


    /**
     * submitData
     *
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

        //У���Ƿ���δ��ӡ�����֪ͨ��
        if (!checkData())
        {
            return false;
        }

//        System.out.println("Start  dealData...");

        //����ҵ����
        if (!dealData())
        {
            return false;
        }

//        System.out.println("dealData successful!");

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

        return true;
    }


    /**
     * ׼������ǰ̨ͳһ�洢����
     * �����������������򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();
        if (tLCStopInsuredSet.size() > 0)
        {
            StringBuffer tSBql = new StringBuffer();
            map.put(tLCStopInsuredSet, "DELETE");
            for (int i = 1; i <= tLCStopInsuredSet.size(); i++)
            {
                tSBql = new StringBuffer(128);
                tSBql.append("Update LCInsured set InsuredStat='0' where Insuredno='");
                tSBql.append(tLCStopInsuredSet.get(i).getInsuredNo());
                tSBql.append("'");
//                map.put("Update LCInsured set InsuredStat='0' where Insuredno='" +
//                        tLCStopInsuredSet.get(i).getInsuredNo() + "'",
//                        "UPDATE");
                map.put(tSBql.toString(), "UPDATE");
            }
        }
        mResult.add(map);
        return true;
    }


    /**
     * У��ҵ������
     * @return boolean
     */
    private boolean checkData()
    {
        LCStopInsuredDB tLCStopInsuredDB = new LCStopInsuredDB();

        tLCStopInsuredDB.setPrtNo(mPrtNo);
        tLCStopInsuredSet = tLCStopInsuredDB.query();

        if (tLCStopInsuredSet == null || tLCStopInsuredSet.size() <= 0)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWStopRecoverAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "δ�鵽��������ͣ��Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //����У�飬������ֻ����ǩ��������ͣ�ָ����Ժ���ܻ�ſ�
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setPrtNo(mPrtNo);
        LCContSet tLCContSet = new LCContSet();
        tLCContSet = tLCContDB.query();
        if (tLCContSet == null || tLCContSet.size() <= 0)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWStopRecoverAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "��ͬ��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        int tflag = 0;
        for (int i = 1; i <= tLCContSet.size(); i++)
        {
            if (tLCContSet.get(i).getAppFlag().equals("1"))
            {
                tflag = 1;
            }
        }
        if (tflag == 0)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWStopRecoverAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "��ʱ��֧��ǩ��ǰ����ͣ�ָ�!";
            this.mErrors.addOneError(tError);
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
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWStopRecoverAfterInitService";
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
            tError.moduleName = "UWStopRecoverAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWStopRecoverAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ�������������ID
        mSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
        if (mSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWStopRecoverAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ�������������ID
        mPrtNo = (String) mTransferData.getValueByName("PrtNo");
        if (mPrtNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWStopRecoverAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������PrtNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ�������������ID
        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mContNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWStopRecoverAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������ContNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
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
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mContNo);
        if (!tLCContDB.getInfo())
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWStopRecoverAfterInitService";
            tError.functionName = "dealData";
            tError.errorMessage = "��ѯ��ͬ��Ϣʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema = tLCContDB.getSchema();

        return true;
    }


    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return boolean
     */
    private boolean prepareTransferData()
    {

        mTransferData.setNameAndValue("PrtNo", mPrtNo);
        mTransferData.setNameAndValue("ContNo", mContNo);
        mTransferData.setNameAndValue("ProposalContNo", mLCContSchema.getProposalContNo());
        mTransferData.setNameAndValue("AgentCode", mLCContSchema.getAgentCode());

        mTransferData.setNameAndValue("AppntNo", mLCContSchema.getAppntNo());
        mTransferData.setNameAndValue("AppntName", mLCContSchema.getAppntName());
        mTransferData.setNameAndValue("ManageCom", mLCContSchema.getManageCom());

        LAAgentDB tLAAgentDB = new LAAgentDB();
        LAAgentSet tLAAgentSet = new LAAgentSet();
        tLAAgentDB.setAgentCode(mLCContSchema.getAgentCode());
        tLAAgentSet = tLAAgentDB.query();
        if (tLAAgentSet == null || tLAAgentSet.size() != 1)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "ProposalApproveAfterEndService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "�����˱�LAAgent��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mTransferData.setNameAndValue("AgentName", tLAAgentSet.get(1).getName());

        return true;
    }


    /**
     * getResult
     *
     * @return VData
     */
    public VData getResult()
    {
        return mResult;
    }


    /**
     * getReturnTransferData
     *
     * @return TransferData
     */

    public TransferData getReturnTransferData()
    {
        return mTransferData;
    }


    /**
     * getErrors
     *
     * @return CErrors
     */
    public CErrors getErrors()
    {
        return mErrors;
    }

}

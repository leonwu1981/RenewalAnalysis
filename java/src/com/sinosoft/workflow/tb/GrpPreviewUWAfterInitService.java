/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LCGrpContDB;
import com.sinosoft.lis.db.LCGrpPolDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.LCGrpPolSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;
import com.sinosoft.lis.vschema.LCGrpContSet;


/**
 * <p>Title: ��������Լ�µ����� </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Liuliang
 * @version 1.0
 */

public class GrpPreviewUWAfterInitService  implements AfterInitService
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
    private LCProtocolRelaContSchema mLCProtocolRelaContSchema   = new LCProtocolRelaContSchema();
    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();


    /** ���ݲ����ַ��� */
    private String mOperater;
//    private String mManageCom;
    private String mOperate;
    private String mMissionID;
//    private String mSubMissionID;
    //Э��������
    private String mProtocolRelaFlag;
    //��������
    private String mSpecialFlag;
    private String mProtocolPrtNo;
    private String mPrtNo;

    public GrpPreviewUWAfterInitService()
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
        map.put(mLCGrpContSchema, "UPDATE");
        //�����ͬ������Э�飬�򽫹�����Ϣд�뵽Э�����ͬ����������
        if(mProtocolRelaFlag.equals("Y"))
        {
          map.put(mLCProtocolRelaContSchema, "INSERT");
        }
        mResult.add(map);
        return true;
    }


    /**
     * У��ҵ������
     * @return
     */
    private boolean checkData()
    {


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
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpInputConfirmAfterInitService";
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
            tError.moduleName = "GrpInputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mOperate = cOperate;
        //��õ�ǰ�������������ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpInputConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

//        //��õ�ǰ�������������ID
//        mGrpContNo = (String) mTransferData.getValueByName("GrpContNo");
//        if (mGrpContNo == null)
//        {
//            // @@������
//            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
//            CError tError = new CError();
//            tError.moduleName = "GrpInputConfirmAfterInitService";
//            tError.functionName = "getInputData";
//            tError.errorMessage = "ǰ̨����ҵ��������mContNoʧ��!";
//            this.mErrors.addOneError(tError);
//            return false;
//        }

        return true;
    }

    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {

        mPrtNo = (String) mTransferData.getValueByName("PrtNo");
        mProtocolRelaFlag = (String) mTransferData.getValueByName("ProtocolRelaFlag");
        mSpecialFlag = (String) mTransferData.getValueByName("SpecialFlag");
        mProtocolPrtNo = (String) mTransferData.getValueByName("ProtocolPrtNo");


        LCGrpContDB tLCGrpContDB = new LCGrpContDB();
        String strSQL = "SELECT * FROM LCGrpCont WHERE PrtNo = '" +
            mPrtNo + "'";
        LCGrpContSet tempLCGrpContSet = tLCGrpContDB.executeQuery(strSQL);
        System.out.println("$$$$$$$$" + strSQL);
        if (tempLCGrpContSet.size() == 0) {
          mErrors.copyAllErrors(tLCGrpContDB.mErrors);
          CError tError = new CError();
          tError.moduleName = "GrpPreviewUWAfterInitService";
          tError.functionName = "dealdata";
          tError.errorMessage = "��ѯLCGrpCont����ȡ����ʧ��";
          this.mErrors.addOneError(tError);

          return false;
        }

        tLCGrpContDB.setSchema(tempLCGrpContSet.get(1));
        mLCGrpContSchema = tLCGrpContDB.getSchema();

      //Ϊ�����ͬ�����¼���ˡ�¼��ʱ��
        //mLCGrpContSchema.setInputOperator(mOperater);
        mLCGrpContSchema.setModifyDate(PubFun.getCurrentDate());
        mLCGrpContSchema.setModifyTime(PubFun.getCurrentTime());

        mLCGrpContSchema.setSpecialFlag(mSpecialFlag);
        mLCGrpContSchema.setProtocolRelaFlag(mProtocolRelaFlag);



        //�����ͬ������Э�飬�򽫹�����Ϣд�뵽Э�����ͬ����������
        if(mProtocolRelaFlag.equals("Y"))
        {
          mLCProtocolRelaContSchema.setProtocolNo(mProtocolPrtNo);
          mLCProtocolRelaContSchema.setPrtNo(mPrtNo);
          mLCProtocolRelaContSchema.setTypeCode("2");

          mLCProtocolRelaContSchema.setOperator(mOperater);
          mLCProtocolRelaContSchema.setMakeDate(PubFun.getCurrentDate());
          mLCProtocolRelaContSchema.setMakeTime(PubFun.getCurrentTime());
          mLCProtocolRelaContSchema.setModifyDate(PubFun.getCurrentDate());
          mLCProtocolRelaContSchema.setModifyTime(PubFun.getCurrentTime());
        }
        return true;

    }



    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return
     */
    private boolean prepareTransferData()
    {
//        mTransferData.setNameAndValue("ProposalGrpContNo", mLCGrpContSchema.getProposalGrpContNo());
//        mTransferData.setNameAndValue("PrtNo", mLCGrpContSchema.getPrtNo());
//        mTransferData.setNameAndValue("SaleChnl", mLCGrpContSchema.getSaleChnl());
//        mTransferData.setNameAndValue("ManageCom", mLCGrpContSchema.getManageCom());
//        mTransferData.setNameAndValue("AgentCode", mLCGrpContSchema.getAgentCode());
//        mTransferData.setNameAndValue("AgentGroup", mLCGrpContSchema.getAgentGroup());
//        mTransferData.setNameAndValue("GrpName", mLCGrpContSchema.getGrpName());
//        mTransferData.setNameAndValue("CValiDate", mLCGrpContSchema.getCValiDate());

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

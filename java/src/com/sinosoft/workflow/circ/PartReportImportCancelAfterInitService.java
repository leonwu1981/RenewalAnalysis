/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.circ;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: </p>
 * <p>Description:�������ڵ�����:��ȫ�˹��˱����ͺ˱�֪ͨ������� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PartReportImportCancelAfterInitService implements AfterInitService
{

    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** �����洫�����ݵ����� */
//    private VData mInputData;
    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();
    //private VData mIputData = new VData();
    private TransferData mTransferData = new TransferData();
    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;
//    private String mOperate;
    /** ҵ�����ݲ����ַ��� */
    private String mStatYear;
    private String mStatMon;
    private String mMissionID;
//    private String mItemType;
//    private String mDeleteSQL;
//    private Reflections mReflections = new Reflections();

    /**ִ�б�ȫ��������Լ�������0000000003*/
    public PartReportImportCancelAfterInitService()
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

        //mResult.clear();
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

        //��Ӻ˱�֪ͨ���ӡ���������
        //map.put(mLOPRTManagerSchema, "UPDATE");


        mResult.add(map);
        return true;
    }

    /**
     * У��ҵ������
     * @return boolean
     */
    private static boolean checkData()
    {

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
//        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
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
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
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
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

//        mOperate = cOperate;

        //���ҵ������
        if (mTransferData == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mStatYear = (String) mTransferData.getValueByName("StatYear");
        if (mStatYear == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������StatYearʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mStatMon = (String) mTransferData.getValueByName("StatMon");
        if (mStatMon == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������StatMonʧ��!";
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
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
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
        String tString = "delete from LFXMLColl where StatYear=" + mStatYear +
                         " and StatMon=" + mStatMon;
//        mDeleteSQL = tString;
        return true;
    }


    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return boolean
     */
    private static boolean prepareTransferData()
    {
        //Ϊ�������л������֪ͨ��ڵ�׼����������
//	  mTransferData.setNameAndValue("CertifyCode",mLZSysCertifySchema.getCertifyCode());
//	  mTransferData.setNameAndValue("ValidDate",mLZSysCertifySchema.getValidDate()) ;

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

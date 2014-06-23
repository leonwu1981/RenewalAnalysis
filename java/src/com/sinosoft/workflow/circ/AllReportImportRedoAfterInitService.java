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
 * <p>Description:�������ڵ�����:����ᱨ�����������������³�ʼ�������� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class AllReportImportRedoAfterInitService implements AfterInitService
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
    private String mDeleteXMLSQL;
    private String mDeleteRiskGetSQL;
    private String mDeleteRiskAppSubSQL;
    private String mDeleteRiskAppSQL;
    private String mDeleteLFFinXMLSQL;
//    private String mDeleteLFActuaryXMLSQL;
    private String mDeleteLFFBXMLSQL;
    private String mDeleteLFActuaryXmlSQL;
    private String mDeleteLFRLXmlSQL;
    private String mDeleteLFTZXmlSQL;
    private String mDeleteChargeSQL;
    private String mDeleteWageSQL;
    private String mDeleteTaxSQL;
    private String mDeleteProductSQL;

//    private Reflections mReflections = new Reflections();

    /**ִ�б�ȫ��������Լ�������0000000003*/
    public AllReportImportRedoAfterInitService()
    {
    }

    /**
     * �������ݵĹ�������
     * @param cInputData VData ���������
     * @param cOperate String ���ݲ���
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

        map.put(mDeleteXMLSQL, "DELETE");
        map.put(mDeleteRiskGetSQL, "DELETE");
        map.put(mDeleteRiskAppSubSQL, "DELETE");
        map.put(mDeleteRiskAppSQL, "DELETE");
        map.put(mDeleteProductSQL, "DELETE");

        map.put(mDeleteLFFinXMLSQL, "DELETE");
        map.put(mDeleteLFActuaryXmlSQL, "DELETE");
        map.put(mDeleteLFFBXMLSQL, "DELETE");
        map.put(mDeleteLFRLXmlSQL, "DELETE");
        map.put(mDeleteLFTZXmlSQL, "DELETE");
        map.put(mDeleteChargeSQL, "DELETE");
        map.put(mDeleteWageSQL, "DELETE");
        map.put(mDeleteTaxSQL, "DELETE");

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
            tError.moduleName = "AllReportImportRedoAfterInitService";
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
            tError.moduleName = "AllReportImportRedoAfterInitService";
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
            tError.moduleName = "AllReportImportRedoAfterInitService";
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
            tError.moduleName = "AllReportImportRedoAfterInitService";
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
            tError.moduleName = "AllReportImportRedoAfterInitService";
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
            tError.moduleName = "AllReportImportRedoAfterInitService";
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
            tError.moduleName = "AllReportImportRedoAfterInitService";
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
        //ɾ��xml���ܱ�
        mDeleteXMLSQL = "delete from LFXMLColl where StatYear=" + mStatYear +
                        " and StatMon=" + mStatMon;
        //ɾ��ҵ���м������
        mDeleteRiskGetSQL = "delete from LFRiskGet where year(reportdate)=" +
                            mStatYear + " and month(reportdate)=" + mStatMon;
        mDeleteRiskAppSubSQL =
                "delete from  LFRiskAppSub where year(reportdate)=" + mStatYear +
                " and month(reportdate)=" + mStatMon;

        mDeleteRiskAppSQL = "delete from LFRiskApp where year(reportdate)=" +
                            mStatYear + " and month(reportdate)=" + mStatMon;

        mDeleteProductSQL = "delete from lfproductxml where year(reportdate)=" +
                            mStatYear + " and month(reportdate)=" + mStatMon;

        //ɾ�������м������
        mDeleteLFFinXMLSQL = "delete from LFFinXml where year(reportdate)=" +
                             mStatYear + " and month(reportdate)=" + mStatMon;
        //ɾ��ҵ���м������
        //mDeleteOtherSQL2="delete from LFXMLColl where StatYear="+mStatYear+" and StatMon="+mStatMon;
        //ɾ�������м������
//        mDeleteLFActuaryXmlSQL =
//                "delete from LFActuaryXml where trim(StatYear)=" + mStatYear +
//                " and trim(StatMon)=" + mStatMon;
        mDeleteLFActuaryXmlSQL = "delete from LFActuaryXml where StatYear=" +
                                 mStatYear + " and StatMon=" + mStatMon;
        //ɾ�������м������
//        mDeleteLFRLXmlSQL = "delete from LFRLXml where trim(StatYear)=" +
//                            mStatYear + " and trim(StatMon)=" + mStatMon;
        mDeleteLFRLXmlSQL = "delete from LFRLXml where StatYear=" + mStatYear +
                            " and StatMon=" + mStatMon;
        //ɾ��Ͷ���м������
//        mDeleteLFTZXmlSQL = "delete from LFTZXml where trim(StatYear)=" +
//                            mStatYear.trim() + " and trim(StatMon)=" +
//                            mStatMon.trim();
        mDeleteLFTZXmlSQL = "delete from LFTZXml where StatYear=" +
                            mStatYear.trim() +
                            " and StatMon=" + mStatMon.trim();
        //ɾ�������м������
        mDeleteChargeSQL = "delete from LFCharge where year(reportdate)=" +
                           mStatYear + " and month(reportdate)=" + mStatMon;

        mDeleteWageSQL = "delete from LFWage where year(reportdate)=" +
                         mStatYear + " and month(reportdate)=" + mStatMon;

        mDeleteTaxSQL = "delete from LFTax where year(reportdate)=" + mStatYear +
                        " and month(reportdate)=" + mStatMon;
        //ɾ���ֱ��м������
        mDeleteLFFBXMLSQL = "delete from  LFReinsureXml where StatYear=" + mStatYear +
                " and StatMon=" + mStatMon;

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

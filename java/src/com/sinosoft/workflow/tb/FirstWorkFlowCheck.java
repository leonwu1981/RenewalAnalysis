/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LCContDB;
import com.sinosoft.lis.db.LCPolDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.LCContSchema;
import com.sinosoft.lis.vschema.LCPolSet;
import com.sinosoft.utility.*;


/**
 * <p>Title: ��������Լ��������ʼ���У���� </p>
 * <p>Description: У���Ƿ�����¼���������������¼¼���ˡ�¼�����ʱ��</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class FirstWorkFlowCheck
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();


    /** �����洫�����ݵ����� */
    private VData mResult = new VData();


    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();

    /** ҵ������ */
//    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();
    private LCContSchema mLCContSchema = new LCContSchema();
//    private LCGrpPolSchema mLCGrpPolSchema = new LCGrpPolSchema();
//    private LCPolSchema mLCPolSchema = new LCPolSchema();


    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;


    /** ҵ�������ַ��� */
    private String mContNo;
//    private String mContSql;

    public FirstWorkFlowCheck()
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

        System.out.println("Start  dealData...");

        //����ҵ����
        if (!dealData())
        {
            return false;
        }

        System.out.println("dealData successful!");

        //׼������̨������
        if (!prepareOutputData())
        {
            return false;
        }

        System.out.println("Start  Submit...");

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
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpFirstWorkFlowCheck";
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
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpFirstWorkFlowCheck";
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
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpFirstWorkFlowCheck";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ���������GrpContNo
        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mContNo == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpFirstWorkFlowCheck";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������GrpContNoʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }


    /**
     * У��ҵ������:У��ʱ��ﵽ¼���������
     * @return boolean
     */
    private boolean checkData()
    {
        LCContDB tLCContDB = new LCContDB();
        LCPolDB tLCPolDB = new LCPolDB();

        tLCContDB.setContNo(mContNo);
        if (!tLCContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "FirstWorkFlowCheck";
            tError.functionName = "checkData";
            tError.errorMessage = "��ͬ����" + mContNo + "��Ϣ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);

        tLCPolDB.setContNo(mContNo);
        LCPolSet tLCPolSet = tLCPolDB.query();
        if (tLCPolSet.size() == 0 || tLCPolSet == null)
        {
            CError tError = new CError();
            tError.moduleName = "FirstWorkFlowCheck";
            tError.functionName = "checkData";
            tError.errorMessage = "����������Ϣ��ѯʧ�ܣ�����ԭ������ǣ�δ¼�����������Ϣ��";
            this.mErrors.addOneError(tError);
            return false;
        }

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

        map.put(mLCContSchema, "UPDATE");

        mResult.add(map);
        return true;
    }


    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean dealData()
    {
        //Ϊ��ͬ�����¼���ˡ�¼��ʱ��
        mLCContSchema.setInputOperator(mOperater);
        mLCContSchema.setInputDate(PubFun.getCurrentDate());
        mLCContSchema.setInputTime(PubFun.getCurrentTime());
        return true;
    }

    /**
     *  ���ؽ����
     *  @return mResult
     */
    public VData getResult()
    {
        return mResult;
    }

    /**
     * ���ش��������
     * @return CErrors
     */
    public CErrors getErrors()
    {
        return mErrors;
    }
}

/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.cbcheck.UWTakeBackTransferBL;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: �������ڵ�����:����Լ�˹��˱����֪ͨ����շ����� </p>
 * <p>Description: �������֪ͨ��AfterInit������������ </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class UWTakeBackAutoTransferAfterInitService implements AfterInitService
{

    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** �����洫�����ݵ����� */
    private VData mResult = new VData();

    /** �����洫�����ݵ����� */
    private VData mInputData = new VData();


    /** �������������д������ݵ����� */
//    private GlobalInput mGlobalInput = new GlobalInput();

    private TransferData mTransferData = new TransferData();

    public UWTakeBackAutoTransferAfterInitService()
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

        //����ҵ����֤
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
     * ׼������ǰ̨ͳһ�洢����
     * �����������������򷵻�false,���򷵻�true
     * @return boolean
     */
    private static boolean prepareOutputData()
    {
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
        mInputData = (VData) cInputData.clone();
        return true;
    }


    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean dealData()
    {
        UWTakeBackTransferBL tUWTakeBackTransferBL = new UWTakeBackTransferBL();
        if (!tUWTakeBackTransferBL.submitData(mInputData, ""))
        {
            CError tError = new CError();
            tError.moduleName = "UWPrintTakeBackAutoIssueAfterInitService";
            tError.functionName = "dealdata";
            tError.errorMessage = "������������������";
            this.mErrors.addOneError(tError);
            return false;
        }
        mResult = tUWTakeBackTransferBL.getResult();
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

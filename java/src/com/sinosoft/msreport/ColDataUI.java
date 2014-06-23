/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author lh
 * @version 1.0
 */

public class ColDataUI
{
    /** �����洫�����ݵ����� */
    private VData mInputData;
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    private VData mResult = new VData();

    public ColDataUI()
    {
    }

    /**
     �������ݵĹ�������
     */
    public boolean submitData(VData cInputData)
    {
        //���Ƚ������ڱ�������һ������
        mInputData = (VData) cInputData.clone();
        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData(cInputData))
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
        ColDataBL tColDataBL = new ColDataBL();
        System.out.println("Start ColData UI Submit...");
        tColDataBL.submitData(mInputData);
        System.out.println("End ColData UI Submit...");
        //�������Ҫ����Ĵ����򷵻�
        if (tColDataBL.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tColDataBL.mErrors);
            CError tError = new CError();
            tError.moduleName = "ColDataUI";
            tError.functionName = "submitData";
            tError.errorMessage = "�����ύʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mInputData = null;
        return true;
    }

    /**
     * ׼��������������Ҫ������
     * ��������׼������ʱ���������򷵻�false,���򷵻�true
     */
    private boolean prepareOutputData()
    {
        try
        {
        }
        catch (Exception ex)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "LDColDataUI";
            tError.functionName = "prepareData";
            tError.errorMessage = "��׼������㴦������Ҫ������ʱ����";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * ����ǰ����������ݣ�����UI�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {
        boolean tReturn = false;
        //�˴�����һЩУ�����
        tReturn = true;
        return tReturn;
    }

    /**
     * �����������еõ����ж���
     *��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean getInputData(VData cInputData)
    {
        return true;
    }

    public VData getResult()
    {
        return this.mResult;
    }

    public static void main(String[] args)
    {
        ColDataUI ColDataUI1 = new ColDataUI();
    }
}

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

public class MakeXMLUI
{

    /** �����洫�����ݵ����� */
    private VData mInputData;
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    private VData mResult = new VData();

    public MakeXMLUI()
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
        MakeXMLBL tMakeXMLBL = new MakeXMLBL();
        System.out.println("Start MakeXML UI Submit...");
        tMakeXMLBL.submitData(mInputData);
        System.out.println("End MakeXML UI Submit...");
        //�������Ҫ����Ĵ����򷵻�
        if (tMakeXMLBL.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tMakeXMLBL.mErrors);
            CError tError = new CError();
            tError.moduleName = "MakeXMLUI";
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
            tError.moduleName = "LDMakeXMLUI";
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
        MakeXMLUI makeXMLUI1 = new MakeXMLUI();
    }
}

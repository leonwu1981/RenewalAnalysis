/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

/**
 * <p>Title: ɨ���������</p>
 * <p>Description: UI������</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author Minim
 * @version 1.0
 * @date 2002-11-06
 */

public class EasyScanQueryUI
{
    /** �������ݵ����� */
    private VData mResult = new VData();
    /** �������� */
    public CErrors mErrors = new CErrors();

    public EasyScanQueryUI()
    {
    }

    /**
     * �����ύ�Ĺ����������ύ�ɹ��󽫷��ؽ���������ڲ�VData������
     * @param cInputData ���������,VData����
     * @param cOperate ���ݲ����ַ�������Ҫ����"QUERY||MAIN"��"QUERY||DETAIL"
     * @return ����ֵ��true--�ύ�ɹ�, false--�ύʧ�ܣ�
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
//        System.out.println("---EasyScanQuery BL BEGIN---");
        EasyScanQueryBL tEasyScanQueryBL = new EasyScanQueryBL();
        if (tEasyScanQueryBL.submitData(cInputData, cOperate))
        {
            mResult = tEasyScanQueryBL.getResult();
        }
        else
        {
            // @@������
            this.mErrors.copyAllErrors(tEasyScanQueryBL.mErrors);
            mResult.clear();
            return false;
        }
//        System.out.println("---EasyScanQuery BL END---");
        return true;
    }

    /**
     * �������������������ȡ���ݴ�����
     * @return ���������ݲ�ѯ����ַ�����VData����
     */
    public VData getResult()
    {
        return mResult;
    }

    /**
     * ���Ժ���
     * @param args String[]
     */
    public static void main(String[] args)
    {
        EasyScanQueryUI tEasyScanQueryUI = new EasyScanQueryUI();
        VData tVData = new VData();
        VData tVData1 = new VData();
//      //docid test
//      tVData.add("532");
//      tEasyScanQueryUI.submitData(tVData, "QUERY||0");
        tVData.add("80000000000002");
        tVData.add("11");
        tEasyScanQueryUI.submitData(tVData, "QUERY||1");
//      tVData.add("532");
//      tEasyScanQueryUI.submitData(tVData, "QUERY||2");
    }
}

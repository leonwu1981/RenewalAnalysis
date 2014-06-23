/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

/**
 * <p>Title: Webҵ��ϵͳ</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author Minim
 * @version 1.0
 */

public class CodeQueryUI
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** �����洫�����ݵ����� */
    private VData mInputData = new VData();

    /** ���ݲ����ַ��� */
    private String mOperate;

    public CodeQueryUI()
    {
    }

    /**
     * �������ݵĹ�������
     * <p><b>Example: </b><p>
     * <p>CodeQueryUI tCodeQueryUI=new CodeQueryUI();<p>
     * <p>VData tData=new VData();<p>
     * <p>LDCodeSchema tLDCodeSchema =new LDCodeSchema();<p>
     * <p>tLDCodeSchema.setCodeType("sex");<p>
     * <p>tData.add(tLDCodeSchema);<p>
     * <p>tCodeQueryUI.submitData(tData,"QUERY||MAIN");<p>
     * @param cInputData ��Ϊ����������VData����
     * @param cOperate ��������־
     * @return �����̨���ݴ�������ɹ�������������ڲ�VData�����У�����true������ʧ���򷵻�false
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //���������ݿ�����������
        this.mOperate = cOperate;
        CodeQueryBL tCodeQueryBL = new CodeQueryBL();
        if (tCodeQueryBL.submitData(cInputData, mOperate))
        {
            mInputData = tCodeQueryBL.getResult();
            return true;
        }
        else
        {
            // @@������
            this.mErrors.copyAllErrors(tCodeQueryBL.mErrors);
            CError tError = new CError();
            tError.moduleName = "CodeQueryUI";
            tError.functionName = "submitData";
            tError.errorMessage = "���ݲ�ѯʧ��!";
            this.mErrors.addOneError(tError);
            mInputData.clear();
            return false;
        }
    }


    /**
     * �������������������ȡ���ݴ�����
     * @return ���������ݲ�ѯ����ַ�����VData����
     */
    public VData getResult()
    {
        return mInputData;
    }

    public static void main(String[] args)
    {
//        GlobalInput tGlobalInput = new GlobalInput();
//        tGlobalInput.ManageCom = "001";
//        CodeQueryUI tCodeQueryUI = new CodeQueryUI();
//        VData tData = new VData();
//        LDCodeSchema tLDCodeSchema = new LDCodeSchema();
//        tLDCodeSchema.setCodeType("agentcode");
//        tData.add(tLDCodeSchema);
//        tData.add(tGlobalInput);
//        TransferData tTransferData = new TransferData();
//        tData.add(tTransferData);
//        tCodeQueryUI.submitData(tData, "QUERY||MAIN");
//        tData = tCodeQueryUI.getResult();
//        String tStr = "";
//        tStr = (String) tData.getObject(0);
//        tStr = StrTool.unicodeToGBK(tStr);
//        System.out.println("result:" + tStr);
    }
}

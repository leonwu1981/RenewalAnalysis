package com.sinosoft.lis.claimanalysis.renewal;


import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

/**
 * <p>Title: Webҵ��ϵͳ</p>
 * <p>Description: ��������_�ļ�����</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author frost
 * @version 1.0
 */
public class RenewalAnalysisUI
{
    /** �������࣬ÿ����Ҫ�����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** �����洫�����ݵ����� */
    private VData mInputData = new VData();
    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
    /** ���ݲ����ַ��� */
    private String mOperate;


    public RenewalAnalysisUI()
    {}

    /**
       �������ݵĹ�������
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //���������ݿ�����������
        this.mOperate = cOperate;
        mInputData = (VData) cInputData.clone();
        //LLClaimFileDownBL tLLClaimFileDownBL = new LLClaimFileDownBL();
        RenewalAnalysisBL tRenewalAnalysisBL = new RenewalAnalysisBL();
        if (tRenewalAnalysisBL.submitData(mInputData, mOperate) == false)
        {
            // @@������
            this.mErrors.copyAllErrors(tRenewalAnalysisBL.mErrors);
            CError tError = new CError();
            tError.moduleName = "LLRenewalAnalUI";
            tError.functionName = "submitData";
            tError.errorMessage = "���ݲ���ʧ��!";
            this.mErrors.addOneError(tError);
            mResult.clear();
            return false;
        }
        else
        {
//        	mResult = tRenewalAnalysisBL.getResult();
        }
        return true;
    }

    public VData getResult()
    {
        return mResult;
    }

     public static void main(String[] args)
     {
    	 
     }
     
}

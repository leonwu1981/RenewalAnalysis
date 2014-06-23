package com.sinosoft.lis.claimanalysis.renewal;


import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

/**
 * <p>Title: Web业务系统</p>
 * <p>Description: 理赔数据_文件生成</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author frost
 * @version 1.0
 */
public class ClaimPredictUI
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往后面传输数据的容器 */
    private VData mInputData = new VData();
    /** 往界面传输数据的容器 */
    private VData mResult = new VData();
    /** 数据操作字符串 */
    private String mOperate;


    public ClaimPredictUI()
    {}

    /**
       传输数据的公共方法
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //将操作数据拷贝到本类中
        this.mOperate = cOperate;
        mInputData = (VData) cInputData.clone();
        //LLClaimFileDownBL tLLClaimFileDownBL = new LLClaimFileDownBL();
        ClaimPredictBL tClaimPredictBL = new ClaimPredictBL();
        if (tClaimPredictBL.submitData(mInputData, mOperate) == false)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tClaimPredictBL.mErrors);
            CError tError = new CError();
            tError.moduleName = "ClaimPredictBL";
            tError.functionName = "submitData";
            tError.errorMessage = "数据操作失败!";
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


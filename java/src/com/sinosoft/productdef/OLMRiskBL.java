package com.sinosoft.productdef;

import com.sinosoft.lis.db.LMRiskDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LMRiskSchema;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

public class OLMRiskBL
{
    public CErrors mErrors = new CErrors();
    private VData mResult = new VData();
    /** 往后面传输数据的容器 */
    private VData mInputData = new VData();
    /** 全局数据 */
    private GlobalInput mGlobalInput = new GlobalInput();
    /** 数据操作字符串 */
    private String mOperate;
    /** 业务处理相关变量 */
    private LMRiskSchema mLMRiskSchema = new LMRiskSchema();

    public OLMRiskBL()
    {
    }

    public boolean submitData(VData cInputData, String cOperate)
    {
        //将操作数据拷贝到本类中
        this.mOperate = cOperate;
        System.out.println("this.mOperate " + this.mOperate);
        //得到外部传入的数据,将数据备份到本类中
        if (!getInputData(cInputData))
        {
            return false;
        }
        //进行业务处理
        if (!dealData())
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "OLMRiskBL";
            tError.functionName = "submitData";
            tError.errorMessage = "数据处理失败OLMRiskBL-->dealData!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //准备往后台的数据
        if (!prepareOutputData())
        {
            return false;
        }
        if (this.mOperate.equals("QUERY||MAIN"))
        {
            this.submitquery();
        }
        else
        {
            System.out.println("Start OLMRiskBL Submit...");
            OLMRiskBLS tOLMRiskBLS = new OLMRiskBLS();
            tOLMRiskBLS.submitData(mInputData, mOperate);
            System.out.println("End OLMRiskBL Submit...");
            //如果有需要处理的错误，则返回
            if (tOLMRiskBLS.mErrors.needDealError())
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tOLMRiskBLS.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLMRiskBL";
                tError.functionName = "submitDat";
                tError.errorMessage = "数据提交失败!";
                this.mErrors.addOneError(tError);
                return false;
            }
        }
        mInputData = null;
        return true;
    }

    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     */
    private boolean dealData()
    {
        return true;
    }

    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     */
    private boolean updateData()
    {
        return true;
    }

    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     */
    private boolean deleteData()
    {
        return true;
    }

    /**
     * 从输入数据中得到所有对象
     *输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean getInputData(VData cInputData)
    {
        this.mLMRiskSchema.setSchema((LMRiskSchema) cInputData.
                                     getObjectByObjectName("LMRiskSchema", 0));
        return true;
    }

    /**
     * 准备往后层输出所需要的数据
     * 输出：如果准备数据时发生错误则返回false,否则返回true
     */
    private boolean submitquery()
    {
        this.mResult.clear();
        LMRiskDB tLMRiskDB = new LMRiskDB();
        tLMRiskDB.setSchema(this.mLMRiskSchema);
        //如果有需要处理的错误，则返回
        if (tLMRiskDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMRiskDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "LDRiskBL";
            tError.functionName = "submitData";
            tError.errorMessage = "数据提交失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mInputData = null;
        return true;
    }

    private boolean prepareOutputData()
    {
        try
        {
            this.mInputData.clear();
            this.mInputData.add(this.mLMRiskSchema);
            mResult.clear();
            mResult.add(this.mLMRiskSchema);
        }
        catch (Exception ex)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "LMRiskBL";
            tError.functionName = "prepareData";
            tError.errorMessage = "在准备往后层处理所需要的数据时出错。";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    public VData getResult()
    {
        return this.mResult;
    }
}

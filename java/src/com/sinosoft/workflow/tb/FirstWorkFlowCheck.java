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
 * <p>Title: 个单新契约工作流起始结点校验类 </p>
 * <p>Description: 校验是否满足录入完成条件，并记录录入人、录入完成时间</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class FirstWorkFlowCheck
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();


    /** 往界面传输数据的容器 */
    private VData mResult = new VData();


    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();

    /** 业务处理类 */
//    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();
    private LCContSchema mLCContSchema = new LCContSchema();
//    private LCGrpPolSchema mLCGrpPolSchema = new LCGrpPolSchema();
//    private LCPolSchema mLCPolSchema = new LCPolSchema();


    /** 数据操作字符串 */
    private String mOperater;
    private String mManageCom;


    /** 业务数据字符串 */
    private String mContNo;
//    private String mContSql;

    public FirstWorkFlowCheck()
    {
    }


    /**
     * 传输数据的公共方法
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //得到外部传入的数据,将数据备份到本类中
        if (!getInputData(cInputData, cOperate))
        {
            return false;
        }

        //校验是否有未打印的体检通知书
        if (!checkData())
        {
            return false;
        }

        System.out.println("Start  dealData...");

        //进行业务处理
        if (!dealData())
        {
            return false;
        }

        System.out.println("dealData successful!");

        //准备往后台的数据
        if (!prepareOutputData())
        {
            return false;
        }

        System.out.println("Start  Submit...");

        return true;
    }


    /**
     * 从输入数据中得到所有对象
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    private boolean getInputData(VData cInputData, String cOperate)
    {
        //从输入数据中得到所有对象
        //获得全局公共数据
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);

        if (mGlobalInput == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpFirstWorkFlowCheck";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得操作员编码
        mOperater = mGlobalInput.Operator;
        if (mOperater == null || mOperater.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpFirstWorkFlowCheck";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据Operate失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得登陆机构编码
        mManageCom = mGlobalInput.ManageCom;
        if (mManageCom == null || mManageCom.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpFirstWorkFlowCheck";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的GrpContNo
        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mContNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpFirstWorkFlowCheck";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中GrpContNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }


    /**
     * 校验业务数据:校验时否达到录入完毕条件
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
            tError.errorMessage = "合同保单" + mContNo + "信息查询失败!";
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
            tError.errorMessage = "个单险种信息查询失败，错误原因可能是：未录入个单险种信息！";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 准备返回前台统一存储数据
     * 输出：如果发生错误则返回false,否则返回true
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
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     * @return boolean
     */
    private boolean dealData()
    {
        //为合同表存入录入人、录入时间
        mLCContSchema.setInputOperator(mOperater);
        mLCContSchema.setInputDate(PubFun.getCurrentDate());
        mLCContSchema.setInputTime(PubFun.getCurrentTime());
        return true;
    }

    /**
     *  返回结果集
     *  @return mResult
     */
    public VData getResult()
    {
        return mResult;
    }

    /**
     * 返回错误处理对象
     * @return CErrors
     */
    public CErrors getErrors()
    {
        return mErrors;
    }
}

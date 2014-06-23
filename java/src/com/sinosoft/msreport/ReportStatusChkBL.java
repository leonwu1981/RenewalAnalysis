/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;


import com.sinosoft.lis.db.LMCalModeDB;
import com.sinosoft.lis.pubfun.CalBase;
import com.sinosoft.lis.pubfun.Calculator;
import com.sinosoft.lis.pubfun.FDate;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LMCalModeSchema;
import com.sinosoft.lis.vschema.LMCalModeSet;
import com.sinosoft.lis.vschema.LMUWSet;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;

/**
 * <p>Title: Web业务系统报表状态查询部分</p>
 * <p>Description: 逻辑处理类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author WHN
 * @version 1.0
 */
public class ReportStatusChkBL
{

    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往后面传输数据的容器 */
    private VData mInputData;
    /** 往界面传输数据的容器 */
    private VData mResult = new VData();
    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();
    //private VData mIputData = new VData();
    private TransferData mTransferData = new TransferData();
    /** 数据操作字符串 */
    private String mOperater;
    private String mManageCom;
    private String mOperate;
    /** 业务数据操作字符串 */
    private String mStatYear;
    private String mStatMon;
    private String mCalCode; //计算编码
    private String mUser;
    private FDate fDate = new FDate();
    private float mValue;
    /** 业务处理相关变量 */
    /**计算公式表**/
    private LMUWSet mLMUWSet = new LMUWSet();
    private LMUWSet m2LMUWSet = new LMUWSet();
    private LMUWSet mmLMUWSet = new LMUWSet();

    private LMCalModeSet mmLMCalModeSet = new LMCalModeSet();
    private LMCalModeSet mLMCalModeSet = new LMCalModeSet();

    private CalBase mCalBase = new CalBase();

    public ReportStatusChkBL()
    {}

    /**
     * 传输数据的公共方法
     * @param: cInputData 输入的数据
     *         cOperate 数据操作
     * @return:
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        int flag = 0; //判断是不是所有数据都不成功
        int j = 0; //符合条件数据个数

        //将操作数据拷贝到本类中
        mInputData = (VData) cInputData.clone();
        //得到外部传入的数据,将数据备份到本类中
        if (!getInputData(cInputData, cOperate))
        {
            return false;
        }

        System.out.println("---ReportStatusChkBL getInputData---");
        // 数据操作业务处理
        if (!dealData())
        {
            return false;
        }

        System.out.println("---ReportStatusChkBL dealData---");
        //准备返回的数据
        prepareOutputData();

        return true;
    }

    /**
     * 数据操作类业务处理
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean dealData()
    {
        //准备算法
        if (CheckKinds() == false)
        {
            return false;
        }

        //取保单信息
        int n = mmLMCalModeSet.size();
        if (n == 0)
        {
        }
        else
        {
            int j = 0;
            mLMCalModeSet.clear();
            for (int i = 1; i <= n; i++)
            {
                //取计算编码
                LMCalModeSchema tLMCalModeSchema = new LMCalModeSchema();
                tLMCalModeSchema = mmLMCalModeSet.get(i);
                mCalCode = tLMCalModeSchema.getCalCode();
                if (CheckPol() == 0)
                {
                }
                else
                {
                    j++;
                    mLMCalModeSet.add(tLMCalModeSchema);
                }
            }
        }

        return true;
    }


    /**
     * 从输入数据中得到所有对象
     *输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean getInputData(VData cInputData, String cOperate)
    {
        //从输入数据中得到所有对象
        //获得全局公共数据
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReportStatusChkBL";
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
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReportStatusChkBL";
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
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReportStatusChkBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mOperate = cOperate;

        //获得业务数据
        if (mTransferData == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReportStatusChkBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mStatYear = (String) mTransferData.getValueByName("StatYear");
        if (mStatYear == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReportStatusChkBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中StatYear失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mStatMon = (String) mTransferData.getValueByName("StatMon");
        if (mStatMon == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReportStatusChkBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中StatMon失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * 核保险种信息校验,准备核保算法
     * 输出：如果发生错误则返回false,否则返回true
     */
    private boolean CheckKinds()
    {
        String tsql = "";
        mmLMCalModeSet.clear();
        LMCalModeSchema tLMCalModeSchema = new LMCalModeSchema();
        //查询算法编码
        tsql = "select * from LMCalMode where riskcode = 'Report' and type ='S'  order by calcode";

        LMCalModeDB tLMCalModeDB = new LMCalModeDB();
        mmLMCalModeSet = tLMCalModeDB.executeQuery(tsql);
        if (tLMCalModeDB.mErrors.needDealError() == true)
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMCalModeDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "ReportStatusChkBL";
            tError.functionName = "CheckKinds";
            tError.errorMessage = "状态描述信息查询失败!";
            this.mErrors.addOneError(tError);
            mLMUWSet.clear();
            return false;
        }
        return true;
    }


    /**
     * 个人单核保
     * 输出：如果发生错误则返回false,否则返回true
     */
    private float CheckPol()
    {
        // 计算
        Calculator mCalculator = new Calculator();
        mCalculator.setCalCode(mCalCode);
        //增加基本要素
        mCalculator.addBasicFactor("StatYear", mStatYear);
        mCalculator.addBasicFactor("StatMonth", mStatMon);
        String tStr = "";
        tStr = mCalculator.calculate();
        if (tStr.trim().equals(""))
        {
            mValue = 0;
        }
        else
        {
            mValue = Float.parseFloat(tStr);
        }

        System.out.println(mValue);
        return mValue;
    }


    /**
     * 准备需要保存的数据
     */
    private void prepareOutputData()
    {
        mResult.clear();
        mResult.add(mLMCalModeSet);
    }

    public VData getResult()
    {
        return mResult;
    }

}

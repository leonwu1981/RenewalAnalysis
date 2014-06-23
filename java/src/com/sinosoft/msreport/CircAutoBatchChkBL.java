/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.pubfun.PubFun1;
import com.sinosoft.lis.schema.LFCKErrorSchema;
import com.sinosoft.lis.schema.LFXMLCollSchema;
import com.sinosoft.lis.vschema.LFCKErrorSet;
import com.sinosoft.lis.vschema.LMCalModeSet;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;
import com.sinosoft.utility.VData;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class CircAutoBatchChkBL
{

    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往后面传输数据的容器 */
    private VData mInputData;
    /** 往界面传输数据的容器 */

    /** 全局数据 */
    private GlobalInput mGlobalInput = new GlobalInput();
//    private Reflections mReflections = new Reflections();

    /** 数据操作字符串 */
    private String mOperate;
    private String mOperater;
    private String mManageCom;
//    private String mPolNo;
//    private String mPrtNo; //印刷号
    private String mSQLCountString; //印刷号
    private String mSQLString; //印刷号
    private final int mCount = 100; //每次循环处理的纪录数


//操作时间戳
    private String CurrentDate = PubFun.getCurrentDate();
    private String CurrentTime = PubFun.getCurrentTime();
    private LFCKErrorSet tLFCKErrorSet = new LFCKErrorSet();
    private LFXMLCollSchema mLFXMLCollSchema = new LFXMLCollSchema();
//    private LCRnewStateLogSet mLCRnewStateLogSet = new LCRnewStateLogSet();
//    private CalBase mCalBase = new CalBase();
    private VData mResult = new VData();

    public CircAutoBatchChkBL()
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
//        int flag = 0; //判断是不是所有数据都不成功
//        int j = 0; //符合条件数据个数

        //将操作数据拷贝到本类中
        mInputData = (VData) cInputData.clone();

        //得到外部传入的数据,将数据备份到本类中
        if (!getInputData(cInputData))
        {
            return false;
        }
        System.out.println("---CircAutoBatchChkBL getInputData---");

        //进行业务数据校验
        if (!checkData())
        {
            return false;
        }

        //进行业务处理
        if (!dealData())
        {
            return false;
        }
        System.out.println("---CircAutoBatchChkBL dealData---");
        return true;
    }

    /**
     * 从输入数据中得到所有对象
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @param cInputData VData
     * @return boolean
     */
    private boolean getInputData(VData cInputData)
    {

        //从输入数据中得到所有对象
        //获得全局公共数据
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mSQLCountString = (String) cInputData.get(0);
        mSQLString = (String) cInputData.get(1);

        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewAutoDunBL";
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
            tError.moduleName = "PRnewAutoDunBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据Operater失败!";
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
            tError.moduleName = "PRnewAutoDunBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得业务数据
        if (mSQLString == null || mSQLString.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewAutoDunBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得业务数据
        if (mSQLCountString == null || mSQLCountString.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewAutoDunBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 校验业务数据
     * @return boolean
     */
    private static boolean checkData()
    {
        return true;
    }

    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     * @return boolean
     */
    private boolean dealData()
    {

        String SQL_Count = mSQLCountString;
        ExeSQL tExeSQL = new ExeSQL();
        SSRS tSSRS = tExeSQL.execSQL(SQL_Count);
        String strCount = tSSRS.GetText(1, 1);
        int SumCount = Integer.parseInt(strCount);
        int CurrentCounter = 1;
        String SQL_ItemCode = mSQLString;

//如果基数大与科目纪录数，跳出循环
        while (CurrentCounter <= SumCount)
        {
            tExeSQL = new ExeSQL();
            tSSRS = tExeSQL.execSQL(SQL_ItemCode, CurrentCounter, mCount);
            if (tSSRS != null)
            {
                for (int i = 1; i <= tSSRS.getMaxRow(); i++)
                {
                    //准备科目数据
                    VData tVData = new VData();
                    tVData = prepareCircAutoChkData(tSSRS, i);
                    //自动校验科目数据
                    CircAutoChkBL tCircAutoChkBL = new CircAutoChkBL();
                    if (tCircAutoChkBL.submitData(tVData, "INSERT"))
                    {
                        //自动校验科目错误纪录
                        LMCalModeSet mLMCalModeSet = new LMCalModeSet();
                        VData tVDate = new VData();
                        tVDate = tCircAutoChkBL.getResult();
                        if (tVDate != null)
                        {
                            mLMCalModeSet = (LMCalModeSet) tVDate.
                                            getObjectByObjectName(
                                    "LMCalModeSet", 0);
                            if (mLMCalModeSet != null &&
                                mLMCalModeSet.size() > 0)
                            {
                                PrepareErrLog(mLMCalModeSet, mLFXMLCollSchema);
                                //数据提交
                                CircAutoBatchChkBLS tCircAutoBatchChkBLS = new
                                        CircAutoBatchChkBLS();
                                if (!tCircAutoBatchChkBLS.submitData(mResult,
                                        mOperate))
                                {
                                    // @@错误处理
                                    this.mErrors.copyAllErrors(
                                            tCircAutoBatchChkBLS.mErrors);
                                    return false;
                                }
                            }
                        }
                    }
                }
                CurrentCounter += mCount; //计数器增加
            }
        }

        return true;

    }

    /**
     * 打印信息表
     * @return boolean
     */
//    private boolean prepareData()
//    {
//        for (int i = 1; i <= mLCRnewStateLogSet.size(); i++)
//        {
//            mLCRnewStateLogSet.get(i).setState("4");
//            mLCRnewStateLogSet.get(i).setModifyDate(CurrentDate);
//        }
//
//        return true;
//    }

    /**
     *
     * @param tSSRS SSRS
     * @param i int
     * @return VData
     */
    private VData prepareCircAutoChkData(SSRS tSSRS, int i)
    {
        VData tVData = new VData();

        //保单表数据
        mLFXMLCollSchema = new LFXMLCollSchema();
        mLFXMLCollSchema.setComCodeISC(tSSRS.GetText(i, 1));
        mLFXMLCollSchema.setItemCode(tSSRS.GetText(i, 2));
        mLFXMLCollSchema.setRepType(tSSRS.GetText(i, 3));
        mLFXMLCollSchema.setStatMon(tSSRS.GetText(i, 5));
        mLFXMLCollSchema.setStatYear(tSSRS.GetText(i, 4));
        mLFXMLCollSchema.setStatValue(tSSRS.GetText(i, 6));
        tVData.add(mLFXMLCollSchema);
        tVData.add(mGlobalInput);
        return tVData;

    }

    /**
     * 错误纪录
     * @param mLMCalModeSet LMCalModeSet
     * @param tLFXMLCollSchema LFXMLCollSchema
     * @return boolean
     */
    private boolean PrepareErrLog(LMCalModeSet mLMCalModeSet,
                                  LFXMLCollSchema tLFXMLCollSchema)
    {
        tLFCKErrorSet = new LFCKErrorSet();
        mResult = new VData();
        for (int i = 1; i <= mLMCalModeSet.size(); i++)
        {
            String strNoLimit = PubFun.getNoLimit(mManageCom);
            String tCIRCERRORSEQ = PubFun1.CreateMaxNo("CIRCERRORSEQ",
                    strNoLimit);
            LFCKErrorSchema tLFCKErrorSchema = new LFCKErrorSchema();
            tLFCKErrorSchema.setSerialNo(tCIRCERRORSEQ);
            tLFCKErrorSchema.setComcodeisc(tLFXMLCollSchema.getComCodeISC());
            tLFCKErrorSchema.setItemCode(tLFXMLCollSchema.getItemCode());
            tLFCKErrorSchema.setCKRuleCode(mLMCalModeSet.get(i).getCalCode());
            tLFCKErrorSchema.setCKError(mLMCalModeSet.get(i).getRemark());
            tLFCKErrorSchema.setMakeDate(CurrentDate);
            tLFCKErrorSchema.setMakeTime(CurrentTime);
            tLFCKErrorSet.add(tLFCKErrorSchema);
        }

        MMap tMMap = new MMap();
        tMMap.put(tLFCKErrorSet, "INSERT");
        mResult.add(tMMap);
        return true;
    }

    public VData getResult()
    {
        return mResult;
    }


}

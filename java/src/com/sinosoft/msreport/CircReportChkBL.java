/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import java.text.DecimalFormat;

import com.sinosoft.lis.pubfun.CalBase;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.pubfun.PubFun1;
import com.sinosoft.lis.schema.LFCKErrorSchema;
import com.sinosoft.lis.schema.LFXMLCollSchema;
import com.sinosoft.lis.vschema.LCRnewStateLogSet;
import com.sinosoft.lis.vschema.LFCKErrorSet;
import com.sinosoft.utility.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class CircReportChkBL
{

    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往后面传输数据的容器 */
    private VData mInputData;
    /** 往界面传输数据的容器 */


    /** 全局数据 */
    private GlobalInput mGlobalInput = new GlobalInput();
    private Reflections mReflections = new Reflections();
    private TransferData mTransferData = new TransferData();

    /** 数据操作字符串 */
    private String mOperate;
    private String mOperater;
    private String mManageCom;
    private String mPolNo;
    private String mPrtNo;
    private String mStatYear;
    private String mStatMon;
    private String mRepType;
    private String mComCodeISC;
    private String mEqualSign = "=";

    private int mCount = 1000; //每次循环处理的纪录数
    private String FORMATMODOL = "0.00"; //保费保额计算出来后的精确位数
    private DecimalFormat mDecimalFormat = new DecimalFormat(FORMATMODOL); //数字转换对象


//操作时间戳
    private String CurrentDate = PubFun.getCurrentDate();
    private String CurrentTime = PubFun.getCurrentTime();
    private LFCKErrorSet tLFCKErrorSet = new LFCKErrorSet();
    private LFXMLCollSchema mLFXMLCollSchema = new LFXMLCollSchema();
    private LCRnewStateLogSet mLCRnewStateLogSet = new LCRnewStateLogSet();
    private CalBase mCalBase = new CalBase();
    private VData mResult = new VData();

    public CircReportChkBL()
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
        int flag = 0; //判断是不是所有数据都不成功
        int j = 0; //符合条件数据个数

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
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);
        mStatYear = (String) mTransferData.getValueByName("StatYear");
        mStatMon = (String) mTransferData.getValueByName("StatMon");
        mRepType = (String) mTransferData.getValueByName("RepType");
        mComCodeISC = (String) mTransferData.getValueByName("ComCodeISC");

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
        if (mStatYear == null || mStatYear.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewAutoDunBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据StatYear失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得业务数据
        if (mStatMon == null || mStatMon.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewAutoDunBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据StatMon失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //获得业务数据
        if (mComCodeISC == null || mComCodeISC.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewAutoDunBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据ComCodeISC失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
//获得业务数据
        if (mRepType == null || mRepType.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewAutoDunBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据RepType失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 校验业务数据
     * @return boolean
     */
    private boolean checkData()
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
        ExeSQL tExeSQL = null;
        SSRS tComCodISCSet = null;
        SSRS tComCodISCSetSize = null;

//针对所有机构进行遍历校验
        if (mComCodeISC.trim().equals("-1"))
        {
            tExeSQL = new ExeSQL();
            String tSQL =
                    "select comcodeisc from lfcomisc where outputflag='1'";
            tComCodISCSet = tExeSQL.execSQL(tSQL);

            String tSQLCount =
                    "select count(comcodeisc) from lfcomisc where outputflag='1'";
            tComCodISCSetSize = tExeSQL.execSQL(tSQLCount);

        }

//遍历所有的校验描述
        String SQL_Count =
                "select count(*) from lfcheckfield where isneedchk='1' ";
        tExeSQL = new ExeSQL();
        SSRS tSSRS = tExeSQL.execSQL(SQL_Count);
        String strCount = tSSRS.GetText(1, 1);
        int SumCount = Integer.parseInt(strCount);
        int CurrentCounter = 1;

        String SQL_ItemCode =
                "select * from lfcheckfield where isneedchk='1' order by serialno";

        if (tComCodISCSet != null && tComCodISCSet.getMaxRow() > 0)
        {
            String tCountComCode = tComCodISCSetSize.GetText(1, 1);
            int SumComCodeCount = Integer.parseInt(tCountComCode);
            for (int j = 1; j <= SumComCodeCount; j++)
            {
                CurrentCounter = 1;
                mComCodeISC = tComCodISCSet.GetText(j, 1);
//如果基数大与科目纪录数，跳出循环
                while (CurrentCounter <= SumCount)
                {
                    tExeSQL = new ExeSQL();
                    tSSRS = tExeSQL.execSQL(SQL_ItemCode, CurrentCounter,
                                            mCount);
                    if (tSSRS != null)
                    {
                        for (int i = 1; i <= tSSRS.getMaxRow(); i++)
                        {
                            //准备科目数据
                            if (tSSRS.GetText(i, 1).trim().equals("1040"))
                            {
                                System.out.println(tSSRS.GetText(i, 1));
                            }
                            VData tVData = new VData();
                            String[] tResult = null;
                            String[] tLeftAdd = null;
                            String[] tLeftMun = null;
                            String[] tRightAdd = null;
                            String[] tRightMun = null;
                            tRightAdd = getRightFomula(tSSRS, i, "+");
                            tRightMun = getRightFomula(tSSRS, i, "-");
                            tLeftAdd = getLeftFomula(tSSRS, i, "+");
                            tLeftMun = getLeftFomula(tSSRS, i, "-");

                            double tRightAddValue = getItemValue(tRightAdd);
                            double tRightMunValue = getItemValue(tRightMun);

                            double tLeftAddValue = getItemValue(tLeftAdd);
                            double tLeftMunValue = getItemValue(tLeftMun);

                            String tRightValue = mDecimalFormat.format(
                                    tRightAddValue - tRightMunValue); //转换计算后的保费(规定的精度)
                            String tLeftValue = mDecimalFormat.format(
                                    tLeftAddValue - tLeftMunValue); //转换计算后的保费(规定的精度)

                            if (!tRightValue.trim().equals(tLeftValue.trim()))
                            {
                                PrepareErrLog(tLeftAdd[0], tSSRS, i, tLeftValue,
                                              tRightValue); //.GetText(i,1),tSSRS.GetText(i,4));
                                //数据提交
                                CircReportChkBLS tCircAutoBatchChkBLS = new
                                        CircReportChkBLS();
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
                        CurrentCounter = CurrentCounter + mCount; //计数器增加
                    }
                }
            }
        }
        else
        {
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
                        String[] tResult = null;
                        String[] tLeftAdd = null;
                        String[] tLeftMun = null;
                        String[] tRightAdd = null;
                        String[] tRightMun = null;
                        tRightAdd = getRightFomula(tSSRS, i, "+");
                        tRightMun = getRightFomula(tSSRS, i, "-");
                        tLeftAdd = getLeftFomula(tSSRS, i, "+");
                        tLeftMun = getLeftFomula(tSSRS, i, "-");

                        double tRightAddValue = getItemValue(tRightAdd);
                        double tRightMunValue = getItemValue(tRightMun);

                        double tLeftAddValue = getItemValue(tLeftAdd);
                        double tLeftMunValue = getItemValue(tLeftMun);
                        String tRightValue = mDecimalFormat.format(
                                tRightAddValue - tRightMunValue); //转换计算后的保费(规定的精度)
                        String tLeftValue = mDecimalFormat.format(tLeftAddValue -
                                tLeftMunValue); //转换计算后的保费(规定的精度)

                        if (!tRightValue.trim().equals(tLeftValue.trim()))
                        {
                            PrepareErrLog(tLeftAdd[0], tSSRS, i, tLeftValue,
                                          tRightValue); //.GetText(i,1),tSSRS.GetText(i,4));
                            //数据提交
                            CircReportChkBLS tCircAutoBatchChkBLS = new
                                    CircReportChkBLS();
                            if (!tCircAutoBatchChkBLS.submitData(mResult,
                                    mOperate))
                            {
                                // @@错误处理
                                this.mErrors.copyAllErrors(tCircAutoBatchChkBLS.
                                        mErrors);
                                return false;
                            }

                        }

                    }
                    CurrentCounter = CurrentCounter + mCount; //计数器增加
                }
            }
        }

        return true;

    }

    /**
     * 打印信息表
     * @return boolean
     */
    private boolean prepareData()
    {
        for (int i = 1; i <= mLCRnewStateLogSet.size(); i++)
        {
            mLCRnewStateLogSet.get(i).setState("4");
            mLCRnewStateLogSet.get(i).setModifyDate(CurrentDate);
        }

        return true;
    }

    private VData prepareCircAutoChkData(SSRS tSSRS, int i, SSRS tComCodeSSRS,
                                         int j)
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

    private String[] getRightFomula(SSRS tSSRS, int i, String tCalSign)
    {
        String[] tResult = null;

        String tCalFomula = tSSRS.GetText(i, 2);
        int tEqualLocation = tCalFomula.indexOf("=");
        String tRightCalFomula = tCalFomula.substring(tEqualLocation + 1);

        int mMod = (tRightCalFomula.length() + 1) / 9;
        tResult = new String[mMod];

        if (tCalSign.equals("+"))
        {

            for (int j = 0; j < mMod; j++)
            {
                if (j == 0)
                {
                    tResult[0] = tRightCalFomula.substring(0, 8);
                }
                else
                {
                    String t = tRightCalFomula.substring(j * 9 - 1, j * 9);
                    if (tRightCalFomula.substring(j * 9 - 1, j * 9).equals("+"))
                    {
                        tResult[j] = tRightCalFomula.substring(j * 9, j * 9 + 8);
                    }
                }
            }
            String tRight = tCalFomula.substring(1, tEqualLocation);
        }

        if (tCalSign.equals("-"))
        {

            for (int j = 0; j < mMod; j++)
            {
                if (j == 0)
                {
                    ;
                }
                else
                {
                    if (tRightCalFomula.substring(j * 9 - 1, j * 9).equals("-"))
                    {
                        tResult[j] = tRightCalFomula.substring(j * 9, j * 9 + 8);
                    }
                }
            }
        }

        return tResult;

    }

    private String[] getLeftFomula(SSRS tSSRS, int i, String tCalSign)
    {
        String[] tResult = null;

        String tCalFomula = tSSRS.GetText(i, 2);
        int tEqualLocation = tCalFomula.indexOf("=");
        String tLeftCalFomula = tCalFomula.substring(0, tEqualLocation);
        int mMod = (tLeftCalFomula.length() + 1) / 9;
        tResult = new String[mMod];

        if (tCalSign.equals("+"))
        {

            for (int j = 0; j < mMod; j++)
            {
                if (j == 0)
                {
                    tResult[0] = tLeftCalFomula.substring(0, 8);
                }
                else
                {
                    if (tLeftCalFomula.substring(j * 9 - 1, j * 9).equals("+"))
                    {
                        tResult[j] = tLeftCalFomula.substring(j * 9, j * 9 + 8);
                    }
                }
            }
        }

        if (tCalSign.equals("-"))
        {
            for (int j = 0; j < mMod; j++)
            {
                if (j == 0)
                {
                    ;
                }
                else
                {
                    if (tLeftCalFomula.substring(j * 9 - 1, j * 9).equals("-"))
                    {
                        tResult[j] = tLeftCalFomula.substring(j * 9, j * 9 + 8);
                    }
                }
            }
        }

        return tResult;

    }

    private double getItemValue(String[] tRightAdd)
    {
        double SumCount = 0;

        ExeSQL tExeSQL = null;
        SSRS tComCodISCSet = null;
        String tInString = "";
        int n = 0;
        for (int i = 0; i < tRightAdd.length; i++)
        {
            if (tRightAdd[i] == null || tRightAdd[i].equals(""))
            {
            }
            else
            {
                n++;
                if (n == 1)
                {
                    tInString = "'" + tRightAdd[i] + "'";
                }
                else
                {
                    tInString = tInString + ",'" + tRightAdd[i] + "'";
                }
            }
        }
        if (tInString == "")
        {
            return SumCount;
        }
        String tSumStatValue = "select sum(a.statValue) from lfxmlcoll a,lfitemrela b where a.itemcode=b.itemcode and a.statyear=" +
                               mStatYear + " and a.statmon=" + mStatMon +
                               " and a.reptype='" + mRepType +
                               "' and a.comcodeisc ='" + mComCodeISC +
                               "' and b.outitemcode in(" + tInString + ")";
        tExeSQL = new ExeSQL();
        SSRS tSSRS = tExeSQL.execSQL(tSumStatValue);
        if (tSSRS == null)
        {
            SumCount = 0;
        }
        else
        {
            String strCount = tSSRS.GetText(1, 1);
            System.out.println("计算Sql：" + tSumStatValue);

            if (strCount == null || strCount.trim().equals("") ||
                strCount.equals("null"))
            {
                strCount = "0";
            }
            else
            {
                System.out.println("=======================" + strCount);
            }

            SumCount = Double.parseDouble(strCount);
            System.out.println("计算Sql结果值：" + SumCount);

        }
        return SumCount;

    }

    /**
     * 错误纪录
     * @param tItemCode String
     * @param tSSRS SSRS
     * @param i int
     * @param tLeftValue String
     * @param tRightValue String
     * @return boolean
     */
    private boolean PrepareErrLog(String tItemCode, SSRS tSSRS, int i,
                                  String tLeftValue, String tRightValue)
    {
        String tFormulaSerialNo = tSSRS.GetText(i, 1);
        String tFormulaRemark = "等式左边为：" + tLeftValue + "   等式右边为：" +
                                tRightValue;
        tLFCKErrorSet = new LFCKErrorSet();
        mResult = new VData();
        String strNoLimit = PubFun.getNoLimit(mManageCom);
        String tCIRCERRORSEQ = PubFun1.CreateMaxNo("CIRCERRORSEQ", strNoLimit);
        LFCKErrorSchema tLFCKErrorSchema = new LFCKErrorSchema();
        tLFCKErrorSchema.setSerialNo(tCIRCERRORSEQ);
        tLFCKErrorSchema.setComcodeisc(mComCodeISC);
        tLFCKErrorSchema.setItemCode(tItemCode);
        tLFCKErrorSchema.setCKRuleCode(tFormulaSerialNo);
        tLFCKErrorSchema.setCKError(tFormulaRemark);
        tLFCKErrorSchema.setMakeDate(CurrentDate);
        tLFCKErrorSchema.setMakeTime(CurrentTime);
        tLFCKErrorSet.add(tLFCKErrorSchema);

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

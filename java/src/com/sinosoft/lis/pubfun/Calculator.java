/**
 * Copyright (c) 2002 sinosoft Co. Ltd. All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.sql.Connection;
import java.sql.SQLException;

import com.sinosoft.lis.schema.LMCalFactorSchema;
import com.sinosoft.lis.schema.LMCalModeSchema;
import com.sinosoft.lis.tb.CachedRiskInfo;
import com.sinosoft.lis.vschema.LMCalFactorSet;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.StrTool;

/*
 * <p>Title: 保费计算类 </p> <p>Description: 通过传入的保单信息和责任信息构建出保费信息和领取信息 </p> <p>Copyright: Copyright (c) 2002</p> <p>Company:
 * sinosoft</p> @author HST
 * 
 * @version 1.0 @date 2002-07-01
 */
public class Calculator {
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 计算需要用到的保单号码 */
    public String PolNo;

    /**
     * 各种要素存放的琏表 1--基本要素、和常量要素相同，但是优先级最低 2--扩展要素，根据SQL语句从新计算 3--常量要素（只取默认值）
     */
    private LMCalFactorSet mCalFactors1 = new LMCalFactorSet(); // 存放基本要素

    public LMCalFactorSet mCalFactors = new LMCalFactorSet();

    private boolean mFlag = false;

    private Connection conn = null;

    public Calculator() {
    }

    public Calculator(Connection conn) {
        mFlag = true;
        this.conn = conn;
    }

    // @Field
    // 计算编码
    private String CalCode = "";

    // 算法对应SQL语句所在表结构
    private LMCalModeSchema mLMCalMode = new LMCalModeSchema();

    private CachedRiskInfo mCRI = CachedRiskInfo.getInstance();

    /**
     * 增加基本要素
     * 
     * @param cFactorCode
     *            要素的编码
     * @param cFactorValue
     *            要素的数据值
     */
    public void addBasicFactor(String cFactorCode, String cFactorValue) {
        LMCalFactorSchema tS = new LMCalFactorSchema();
        tS.setFactorCode(cFactorCode);
        tS.setFactorDefault(cFactorValue);
        tS.setFactorType("1");
        mCalFactors1.add(tS);
    }

    // @Method
    public void setCalCode(String tCalCode) {
        CalCode = tCalCode;
    }

    /**
     * 公式计算函数
     * 
     * @return: String 计算的结果，只能是单值的数据（数字型的转换成字符型）
     * @author: YT
     */
    public String calculate() {
        if (!mFlag) {
            conn = DBConnPool.getConnection();
        }
        try {
            // System.out.println("start calculate++++++++++++++");
            if (!checkCalculate()) {
                return "0";
            }
            // 取得数据库中计算要素
            LMCalFactorSet tLMCalFactorSet = mCRI.findCalFactorByCalCodeClone(CalCode);
            // System.out.println("calCode :" + CalCode);

            if (tLMCalFactorSet == null) {
                return "0";
            }

            mCalFactors.add(tLMCalFactorSet);
            // 增加基本要素
            mCalFactors.add(mCalFactors1);
            // 解释计算要素
            if (!interpretFactors()) {
                return "0";
            }

            // 读取SQL语句
            if (!getSQL()) {
                return "0";
            }

            // 解释SQL语句中的变量
            if (!interpretFactorInSQL()) {
                return "0";
            }
            // 执行SQL语句
            // System.out.println("start execute SQL.....");
            return executeSQL();
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        } finally {
            if (!mFlag) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 执行SQL语句
     * 
     * @return String
     */
    private String executeSQL() {
    	long t0 = System.currentTimeMillis();
        String tReturn = "0";
        ExeSQL tExeSQL = new ExeSQL(conn);
        tReturn = tExeSQL.getOneValue(mLMCalMode.getCalSQL());
        System.out.println();
        String tResult="";
        if(tReturn.length()<10){
        	tResult="@"+tReturn;
        }
        t0 = System.currentTimeMillis()-t0;
        System.out.println("/* 执行的语句是：("+mLMCalMode.getCalCode()+tResult+")；执行时间："+t0+"毫秒 */"+mLMCalMode.getCalSQL());
        if(t0>500)
        	System.out.println("/* 警告：算法执行sql超时。("+mLMCalMode.getCalCode()+tResult+")；执行时间："+t0+"毫秒 */"+mLMCalMode.getCalSQL());
        if (tExeSQL.mErrors.needDealError()) {
            // @@错误处理
            this.mErrors.copyAllErrors(tExeSQL.mErrors);
            CError tError = new CError();
            tError.moduleName = "Calculator";
            tError.functionName = "executeSQL";
//            tError.errorMessage = "执行SQL语句：" + mLMCalMode.getCalCode() + "失败!";
            tError.errorMessage = "执行SQL语句失败!";
            this.mErrors.addOneError(tError);
            return "0";
        }
        return tReturn;
    }

    /**
     * 解释SQL语句中的变量
     * 
     * @return boolean
     */
    private boolean interpretFactorInSQL() {
        String tStr = null;
        // tStr1 = "";
        StringBuffer tSBql = null;
        String tSql = mLMCalMode.getCalSQL();
        try {
            while (true) {
                tStr = PubFun.getStr(tSql, 2, "?");
                if (tStr.equals("")) {
                    break;
                }
                tSBql = new StringBuffer();
                tSBql.append("?");
                tSBql.append(tStr);
                tSBql.append("?");
                // tStr1 = "?" + tStr.trim() + "?";
                // 替换变量
                // tSql = StrTool.replaceEx(tSql, tStr1, getValueByName(tStr));
                tSql = StrTool.replaceEx(tSql, tSBql.toString(), getValueByName(tStr));
            }
        } catch (Exception ex) {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "Calculator";
            tError.functionName = "interpretFactorInSQL";
            tError.errorMessage = "解释" + tSql + "的变量:" + tStr + "时出错。";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLMCalMode.setCalSQL(tSql);
        return true;
    }

    /**
     * 计算子要素的值
     * 
     * @param cF
     *            LMCalFactorSchema
     * @return String
     */
    private String calSubFactors(LMCalFactorSchema cF) {
        int i, iMax;
        String tReturn = "";
        LMCalFactorSchema tC = new LMCalFactorSchema();
        Calculator tNewC = null;
        if(!this.mFlag)
        	tNewC = new Calculator();
        else
        	tNewC = new Calculator(this.conn);
        iMax = mCalFactors.size();
        for (i = 1; i <= iMax; i++) {
            tC = mCalFactors.get(i);
            // 如果是基本要素或常量要素，则传入下一个要素中
            if (tC.getFactorType().toUpperCase().equals("1") || tC.getFactorType().toUpperCase().equals("3")) {
                tNewC.mCalFactors.add(tC);
            }
        }
        tNewC.setCalCode(cF.getFactorCalCode());
        // System.out.println("----SubFactor---calcode = " + cF.getFactorCalCode());
        tReturn = String.valueOf(tNewC.calculate());
        // System.out.println("----SubFactor = " + tReturn);
        // 如果有错误，则将错误拷贝到上一要素,并且返回"0"
        if (tNewC.mErrors.needDealError()) {
            this.mErrors.copyAllErrors(tNewC.mErrors);
            tReturn = "0";
        }
        return tReturn;
    }

    /**
     * 读取SQL语句
     * 
     * @return boolean
     */
    private boolean getSQL() {
        CachedRiskInfo cri = CachedRiskInfo.getInstance();
        LMCalModeSchema tLMCalModeSchema = cri.findCalModeByCalCode(CalCode);

        if (tLMCalModeSchema == null) {
            CError tError = new CError();
            tError.moduleName = "Calculator";
            tError.functionName = "getSql";
            tError.errorMessage = "得到" + CalCode + "的SQL语句时出错。";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLMCalMode.setSchema(tLMCalModeSchema);

        return true;
    }

    /**
     * 解释要素连表中的非变量要素
     * 
     * @return boolean
     */
    private boolean interpretFactors() {
        int i, iMax;
        LMCalFactorSchema tC = new LMCalFactorSchema();
        iMax = mCalFactors.size();
        for (i = 1; i <= iMax; i++) {
            tC = mCalFactors.get(i);
            // 如果是扩展要素，则解释该扩展要素
            if (tC.getFactorType().toUpperCase().equals("2")) {
                tC.setFactorDefault(calSubFactors(tC));
                // 如果在计算子要素的时候发生错误，则返回false
                if (this.mErrors.needDealError()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 校验计算的输入是否足够
     * 
     * @return boolean 如果不正确返回false
     */
    private boolean checkCalculate() {
        if (CalCode == null || CalCode.equals("")) {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "Calculator";
            tError.functionName = "checkCalculate";
            tError.errorMessage = "计算时必须有计算编码。";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * 根据变量名得到变量的值
     * 
     * @param cVarName
     *            String
     * @return String 如果不正确返回"",否则返回变量值
     */
    private String getValueByName(String cVarName) {
        cVarName = cVarName.toLowerCase();
        int i, iMax;
        String tReturn = "";
        LMCalFactorSchema tC = new LMCalFactorSchema();
        iMax = mCalFactors.size();
        for (i = 1; i <= iMax; i++) {
            tC = mCalFactors.get(i);
            if (tC.getFactorCode().toLowerCase().equals(cVarName)) {
                tReturn = tC.getFactorDefault();
                break;
            }
        }
        return StrTool.cTrim(tReturn);
    }

    /**
     * Kevin 2003-08-20 得到解析过的SQL语句，而不是SQL语句执行后的值。
     * 
     * @return String
     */
    public String getCalSQL() {
        if (!mFlag) {
            conn = DBConnPool.getConnection();
        }
        try {
            if (!checkCalculate()) {
                return "0";
            }

            LMCalFactorSet tLMCalFactorSet = mCRI.findCalFactorByCalCodeClone(CalCode);

            if (tLMCalFactorSet == null) {
                return "0";
            }

            mCalFactors.add(tLMCalFactorSet);
            // 增加基本要素
            mCalFactors.add(mCalFactors1);
            // 解释计算要素
            if (!interpretFactors()) {
                return "0";
            }

            // 读取SQL语句
            if (!getSQL()) {
                return "0";
            }

            // 解释SQL语句中的变量
            if (!interpretFactorInSQL()) {
                return "0";
            }

            // 返回解析过的SQL语句
            return mLMCalMode.getCalSQL();
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        } finally {
            if (!mFlag) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 测试函数
     * 
     * @param args
     *            String[]
     */
    public static void main(String[] args) {
        // Calculator tC=new Calculator();
        // LMCalFactorSchema tLMCalFactorSchema=new LMCalFactorSchema();
        // tC.addBasicFactor("PolNo","00000120021100000000");
        // tC.setCalCode("001001") ;
        // System.out.println(tC.calculate());
        // if(tC.mErrors.needDealError())
        // System.out.println(tC.mErrors.getFirstError());
    }
}

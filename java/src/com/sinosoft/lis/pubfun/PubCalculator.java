/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import com.sinosoft.lis.schema.LMCalFactorSchema;
import com.sinosoft.lis.vschema.LMCalFactorSet;
import com.sinosoft.utility.*;

/*
 * <p>Title: 公共计算类 </p>
 * <p>Description: 通过传入的保单信息和责任信息构建出保费信息和领取信息 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft</p>
 * @author HST
 * @version 1.0
 * @date 2002-07-01
 */
public class PubCalculator
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();


    /** 计算需要用到的保单号码 */
    //public String  PolNo;

    /** 各种要素存放的琏表
     *  1--基本要素、和常量要素相同，但是优先级最低
     *  2--扩展要素，根据SQL语句从新计算
     *  3--常量要素（只取默认值）
     */
    //private LMCalFactorSet mCalFactors1=new LMCalFactorSet();//存放基本要素

    public LMCalFactorSet mCalFactors = new LMCalFactorSet();

    // @Field
    //计算编码
    private String mSourCalSql = "";
    private String mDestCalSql = "";

    //算法对应SQL语句所在表结构

    /**
     * 增加基本要素
     * @param cFactorCode 要素的编码
     * @param cFactorValue  要素的数据值
     */
    public void addBasicFactor(String cFactorCode, String cFactorValue)
    {
        LMCalFactorSchema tS = new LMCalFactorSchema();
        tS.setFactorCode(cFactorCode);
        tS.setFactorDefault(cFactorValue);
        tS.setFactorType("1"); //临时计算要素
        mCalFactors.add(tS);
    }


    // @Method
    public void setCalSql(String tCalSql)
    {
        mSourCalSql = tCalSql;
    }


    /**
     * 公式计算函数
     * @return: String 计算的结果，只能是单值的数据（数字型的转换成字符型）
     * @author: YT
     **/
    public String calculate()
    {
        if (!checkCalculate())
            return "0";

        //解释SQL语句中的变量
        if (!interpretFactorInSQL())
            return "0";
        //执行SQL语句
        System.out.println("PubCalculate execute SQL.....");
        return executeSQL();
    }

    public String calculateEx()
    {
        if (!checkCalculate())
            return "0";
        //解释SQL语句中的变量
        if (!interpretFactorInSQL())
            return "0";
        //执行SQL语句
        System.out.println("PubCalculate execute SQL.....");
        return mDestCalSql;
    }


    /**
     * 执行SQL语句
     * @return String
     */
    private String executeSQL()
    {
        String tReturn = "0";
        ExeSQL tExeSQL = new ExeSQL();
        tReturn = tExeSQL.getOneValue(mDestCalSql);
        if (tExeSQL.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tExeSQL.mErrors);
            CError tError = new CError();
            tError.moduleName = "Calculator";
            tError.functionName = "executeSQL";
            tError.errorMessage = "执行SQL语句：" + mDestCalSql + "失败!";
            this.mErrors.addOneError(tError);
            return "0";
        }
        return tReturn;
    }


    /**
     * 解释SQL语句中的变量
     * @return boolean
     */
    private boolean interpretFactorInSQL()
    {
        String tSql, tStr = "", tStr1 = "";
        tSql = mSourCalSql;
        try
        {
            while (true)
            {
                tStr = PubFun.getStr(tSql, 2, "?");
                if (tStr.equals(""))
                    break;
                tStr1 = "?" + tStr.trim() + "?";
                //替换变量
                tSql = StrTool.replaceEx(tSql, tStr1, getValueByName(tStr));
            }
        }
        catch (Exception ex)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "Calculator";
            tError.functionName = "interpretFactorInSQL";
            tError.errorMessage = "解释" + tSql + "的变量:" + tStr + "时出错。";
            this.mErrors.addOneError(tError);
            return false;
        }
        mDestCalSql = tSql;
        return true;
    }


    /**
     * 校验计算的输入是否足够
     * @return boolean 如果不正确返回false
     */
    private boolean checkCalculate()
    {
        if (mSourCalSql == null || mSourCalSql.equals(""))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "Calculator";
            tError.functionName = "checkCalculate";
            tError.errorMessage = "计算时必须有计算SQL语句。";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }


    /**
     * 根据变量名得到变量的值
     * 如果不正确返回"",否则返回变量值
     * @param cVarName String
     * @return String
     */
    private String getValueByName(String cVarName)
    {
        cVarName = cVarName.toLowerCase();
        int i, iMax;
        String tReturn = "";
        LMCalFactorSchema tC = new LMCalFactorSchema();
        iMax = mCalFactors.size();
        for (i = 1; i <= iMax; i++)
        {
            tC = mCalFactors.get(i);
            if (tC.getFactorCode().toLowerCase().equals(cVarName))
            {
                tReturn = tC.getFactorDefault();
                break;
            }
        }
        return tReturn;
    }


    /**
     * Kevin 2003-08-20
     * 得到解析过的SQL语句，而不是SQL语句执行后的值。
     * @return String
     */
    public String getCalSQL()
    {

        if (!checkCalculate())
            return "0";

        //解释SQL语句中的变量
        if (!interpretFactorInSQL())
            return "0";

        // 返回解析过的SQL语句
        return mSourCalSql;
    }

    public static void main(String[] args)
    {
//        Calculator tC = new Calculator();
//        LMCalFactorSchema tLMCalFactorSchema = new LMCalFactorSchema();
//        tC.addBasicFactor("PolNo", "00000120021100000000");
//        tC.setCalCode("001001");
//        System.out.println(tC.calculate());
//        if (tC.mErrors.needDealError())
//            System.out.println(tC.mErrors.getFirstError());
    }
}

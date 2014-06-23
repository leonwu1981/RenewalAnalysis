/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;

/*
 * <p>Title: 保费计算类 </p>
 * <p>Description: 通过传入的保单信息和责任信息构建出保费信息和领取信息 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft</p>
 * @author HST
 * @version 1.0
 * @date 2002-07-01
 */
public class FDate implements Cloneable
{
    // @Field
    /** 错误处理类 */
    public CErrors mErrors = new CErrors(); // 错误信息

    private final String pattern = "yyyy-MM-dd";
    private final String pattern1 = "yyyyMMdd";
    private SimpleDateFormat df;
    private SimpleDateFormat df1;
    // @Constructor
    public FDate()
    {
        df = new SimpleDateFormat(pattern);
        df1 = new SimpleDateFormat(pattern1);
    }

    /**
     * 克隆FDate对象
     * 2005－04－15 朱向峰添加
     * @return Object
     * @throws CloneNotSupportedException
     */
    public Object clone()
            throws CloneNotSupportedException
    {
        FDate cloned = (FDate)super.clone();
        // clone the mutable fields of this class
        cloned.mErrors = (CErrors)mErrors.clone();
        return cloned;
    }

    // @Method
    /**
     * 输入符合格式要求的日期字符串，返回日期类型变量
     * <p><b>Example: </b><p>
     * <p>getDate("2002-10-8") returns "Tue Oct 08 00:00:00 CST 2002"<p>
     * @param dateString 日期字符串
     * @return 日期类型变量
     */
    public Date getDate(String dateString)
    {
        Date tDate = null;
        try
        {
            if (dateString.indexOf("-") != -1)
            {
                tDate = df.parse(dateString);
            }
            else
            {
                tDate = df1.parse(dateString);
            }
        }
        catch (Exception e)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "FDate";
            tError.functionName = "getDate";
            tError.errorMessage = e.toString();
            this.mErrors.addOneError(tError);
        }

        return tDate;
    }

    /**
     * 输入日期类型变量，返回日期字符串
     * <p><b>Example: </b><p>
     * <p>getString("Tue Oct 08 00:00:00 CST 2002") returns "2002-10-8"<p>
     * @param mDate 日期类型变量
     * @return 日期字符串
     */
    public String getString(Date mDate)
    {
        String tString = null;
        if (mDate != null)
        {
            tString = df.format(mDate);
        }
        return tString;
    }

    /**
     * MAIN方法，测试用
     * @param args String[]
     */
    public static void main(String[] args)
    {
//        FDate tFDate = new FDate();
//        System.out.println(tFDate.getDate("2002-10-8"));
    }
}

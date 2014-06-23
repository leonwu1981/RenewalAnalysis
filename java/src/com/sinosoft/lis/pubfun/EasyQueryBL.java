/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.VData;

/**
 * <p>Title: Web业务系统 </p>
 * <p>Description: BL层业务逻辑处理类 </p>
 * <p>Copyright: Copyright (c) 2002 </p>
 * <p>Company: Sinosoft </p>
 * @author HST
 * @version 1.0
 * @date 2002-09-03
 */
public class EasyQueryBL
{
    /** 传入数据的容器 */
    private VData mInputData = new VData();

    /** 传出数据的容器 */
    private VData mResult = new VData();

    /** 数据操作字符串 */
    private String mOperate;

    /** 错误处理类 */
    public CErrors mErrors = new CErrors();

    /** 业务处理相关变量 */
    private String mSQL = "";
    private int mStartIndex;
    private String mEncodedResult = "";
    private String mLargeFlag = "";
//    private int mTotalCount;


    // @Constructor
    public EasyQueryBL()
    {
    }


    // @Method

    /**
     * 传输数据的公共方法, 本处理没有后续的BLS层，故该方法无用
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        // 将传入的数据拷贝到本类中
        mInputData = (VData) cInputData.clone();
        this.mOperate = cOperate;

        // 将外部传入的数据分解到本类的属性中，准备处理
        if (!this.getInputData())
        {
            return false;
        }

        //System.out.println("---End getInputData---");
        // 查询数据,查询的分支可以根据业务要求放到不同的调用级别中
        if (mOperate.equals("QUERY||MAIN"))
        {
            if (this.queryData())
            {
                return true;
            }
            else
            {
                return false;
            }
            //System.out.println("---End queryData---");
        }
        return true;
    }


    /**
     * 将外部传入的数据分解到本类的属性中
     * @return boolean
     */
    private boolean getInputData()
    {
        mSQL = (String) mInputData.get(0);
        if ((mSQL == null) || mSQL.trim().equals(""))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "EasyQueryBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "没有传入SQL语句!";
            this.mErrors.addOneError(tError);
            return false;
        }
        Integer tStart = (Integer) mInputData.get(1);
        mStartIndex = tStart.intValue();
        mLargeFlag = (String) mInputData.get(2);
//        if (mLargeFlag.equals("朱总理"))
//        {
//            System.out.println("等价");
//        }
//        else
//        {
//            System.out.println("不等价");
//        }
//        mLargeFlag = "0";
        if (mLargeFlag == null)
        {
            mLargeFlag = "0";
        }

        return true;
    }


    /**
     * 主要信息查询
     * @return: boolean
     */
    private boolean queryData()
    {
        ExeSQL tExeSQL = new ExeSQL();
        //添加大数据量标志，对于大数据量，采用特殊方式查询，默认情况下都是小数据量查询
        if (mLargeFlag.equals("0"))//100
        {
            mEncodedResult = tExeSQL.getEncodedResult(mSQL, mStartIndex);
        }
        else if (mLargeFlag.equals("2"))//300
        {
            mEncodedResult = tExeSQL.getEncodedResultAll(mSQL, mStartIndex);
        }
        else//all

        {
            mEncodedResult = tExeSQL.getEncodedResultLarge(mSQL, mStartIndex);
        }
        if (tExeSQL.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tExeSQL.mErrors);
            mEncodedResult = "";
            return false;
        }
        else
        {
            mResult.add(mEncodedResult);
        }
//        System.out.println(mEncodedResult);
        return true;
    }

    /**
     * 数据输出方法，供外界获取数据处理结果
     * @return 包含有数据查询结果字符串的VData对象
     */
    public VData getResult()
    {
        return mResult;
    }

    /**
     * 调试函数
     * @param args String[]
     */
    public static void main(String[] args)
    {
//        System.out.println(Character.digit('A', 0));
    }
}

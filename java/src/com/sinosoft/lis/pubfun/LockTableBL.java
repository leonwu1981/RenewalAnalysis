/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import com.sinosoft.lis.schema.LDSysTraceSchema;
import com.sinosoft.lis.vschema.LDSysTraceSet;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;

/**
 * <p>Title: Web业务系统</p>
 * <p>Description: 锁表类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author Minim
 * @version 1.0
 */
public class LockTableBL
{
    /** 传入数据的容器 */
    private VData mInputData = new VData();
    /** 传出数据的容器 */
    private VData mResult = new VData();
    /** 数据操作字符串 */
    private String mOperate;
    /** 错误处理类 */
    public CErrors mErrors = new CErrors();
    /** 全局基础数据 */
    private GlobalInput mGlobalInput = new GlobalInput();
    /** 额外传递的参数 */
    //private TransferData mTransferData = null;

    /** 传入的业务数据 */
    private LDSysTraceSet inLDSysTraceSet = new LDSysTraceSet();

    /** 传出的业务数据 */
    private LDSysTraceSet outLDSysTraceSet = new LDSysTraceSet();

    public LockTableBL()
    {
    }

    /**
     * 数据提交的公共方法，提交成功后将返回结果保存入内部VData对象中
     * @param cInputData 传入的数据,VData对象
     * @param cOperate 数据操作字符串，主要包括"INSERT"
     * @return 布尔值（true--提交成功, false--提交失败）
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //将操作数据拷贝到本类中
        this.mInputData = (VData) cInputData.clone();
        this.mOperate = cOperate;

        //得到外部传入的数据,将数据备份到本类中
        if (!getInputData())
        {
            return false;
        }
        System.out.println("---End getInputData---");

        //进行业务处理
        if (!dealData())
        {
            return false;
        }
        System.out.println("---End dealData---");

        //需要传到后台处理
        if (mOperate.equals("INSERT") || mOperate.equals("DELETE"))
        {
            //准备往后台的数据
            if (!prepareOutputData())
            {
                return false;
            }
            System.out.println("---End prepareOutputData---");

            System.out.println("Start LockTable BLS Submit...");
            LockTableBLS tLockTableBLS = new LockTableBLS();
            if (tLockTableBLS.submitData(mInputData, cOperate) == false)
            {
                //@@错误处理
                this.mErrors.copyAllErrors(tLockTableBLS.mErrors);
                mResult.clear();
                mResult.add(mErrors.getFirstError());
                return false;
            }
            else
            {
                mResult = tLockTableBLS.getResult();
            }
            System.out.println("End LockTable BLS Submit...");
        }
        //不需要传到后台处理
        else if (mOperate.equals(""))
        {
        }

        return true;
    }

    /**
     * 将外部传入的数据分解到本类的属性中
     * @return boolean
     */
    private boolean getInputData()
    {
        try
        {
            mGlobalInput = (GlobalInput) mInputData.getObjectByObjectName(
                    "GlobalInput", 0);

            //mTransferData = (TransferData)mInputData.getObjectByObjectName("TransferData", 0);

            inLDSysTraceSet = (LDSysTraceSet) mInputData.getObjectByObjectName(
                    "LDSysTraceSet", 0);
        }
        catch (Exception e)
        {
            // @@错误处理
            e.printStackTrace();
            CError tError = new CError();
            tError.moduleName = "LockTableBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "接收数据失败!!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * 校验数据
     * @param tLDSysTraceSchema LDSysTraceSchema
     * @return boolean
     */
    private boolean checkData(LDSysTraceSchema tLDSysTraceSchema)
    {
        if (tLDSysTraceSchema.getPolNo().equals("")
            || tLDSysTraceSchema.getPolState() == 0
            || tLDSysTraceSchema.getCreatePos().equals(""))
        {
            return false;
        }

        return true;
    }

    /**
     * 根据前面的输入数据，进行逻辑处理
     * @return 如果在处理过程中出错，则返回false,否则返回true
     */
    private boolean dealData()
    {
        try
        {
            if (mOperate.equals("INSERT"))
            {
                for (int i = 0; i < inLDSysTraceSet.size(); i++)
                {
                    LDSysTraceSchema tLDSysTraceSchema = inLDSysTraceSet.get(i +
                            1);

                    if (!checkData(tLDSysTraceSchema))
                    {
                        throw new Exception("校验数据失败！需要传入待锁定的号码和锁定状态！");
                    }

                    tLDSysTraceSchema.setOperator(mGlobalInput.Operator);
                    tLDSysTraceSchema.setMakeDate(PubFun.getCurrentDate());
                    tLDSysTraceSchema.setMakeTime(PubFun.getCurrentTime());
                    tLDSysTraceSchema.setManageCom(mGlobalInput.ManageCom);
                    tLDSysTraceSchema.setOperator2(mGlobalInput.Operator);
                    tLDSysTraceSchema.setManageCom2(mGlobalInput.ManageCom);
                    tLDSysTraceSchema.setModifyDate(PubFun.getCurrentDate());
                    tLDSysTraceSchema.setModifyTime(PubFun.getCurrentTime());
                }

                outLDSysTraceSet = inLDSysTraceSet;
            }
            else if (mOperate.equals("QUERY"))
            {
                for (int i = 0; i < inLDSysTraceSet.size(); i++)
                {
                    LDSysTraceSchema tLDSysTraceSchema = inLDSysTraceSet.get(i +
                            1);

                    if (!checkData(tLDSysTraceSchema))
                    {
                        throw new Exception("校验数据失败！需要传入待锁定的号码和锁定状态！");
                    }

                    LDSysTraceSet tLDSysTraceSet = tLDSysTraceSchema.getDB().
                            query();
                    if (tLDSysTraceSet.size() > 0)
                    {
                        mResult.add("false");
                    }
                    else
                    {
                        mResult.add("true");
                    }
                }
            }
            else if (mOperate.equals("DELETE"))
            {
                for (int i = 0; i < inLDSysTraceSet.size(); i++)
                {
                    LDSysTraceSchema tLDSysTraceSchema = inLDSysTraceSet.get(i +
                            1);

                    if (!checkData(tLDSysTraceSchema))
                    {
                        throw new Exception("校验数据失败！需要传入待锁定的号码和锁定状态！");
                    }

                    outLDSysTraceSet.add(tLDSysTraceSchema.getDB().query());
                }
            }
        }
        catch (Exception e)
        {
            // @@错误处理
            e.printStackTrace();
            CError tError = new CError();
            tError.moduleName = "LockTableBL";
            tError.functionName = "dealData";
            tError.errorMessage = "数据处理错误! " + e.getMessage();
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * 准备往后层输出所需要的数据
     * @return 如果准备数据时发生错误则返回false,否则返回true
     */
    private boolean prepareOutputData()
    {
        try
        {
            mInputData.clear();

            mInputData.add(outLDSysTraceSet);
        }
        catch (Exception ex)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "LockTableBL";
            tError.functionName = "prepareOutputData";
            tError.errorMessage = "在准备往后层处理所需要的数据时出错! ";
            this.mErrors.addOneError(tError);
            return false;
        }

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
     * 主函数，测试用
     * @param args String[]
     */
    public static void main(String[] args)
    {
//        LockTableBL LockTableBL1 = new LockTableBL();
    }
}

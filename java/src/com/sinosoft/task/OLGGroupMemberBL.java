/*
 * <p>ClassName: OLGGroupMemberBL </p>
 * <p>Description: OLGGroupMemberBL类文件 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: testcompany </p>
 * @Database:
 * @CreateDate：2005-02-23 10:20:45
 */
package com.sinosoft.task;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.vdb.*;
import com.sinosoft.lis.sys.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.bl.*;
import com.sinosoft.lis.vbl.*;
import com.sinosoft.utility.*;
import com.sinosoft.lis.pubfun.*;

public class OLGGroupMemberBL
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    private VData mResult = new VData();

    /** 往后面传输数据的容器 */
    private VData mInputData = new VData();

    /** 全局数据 */
    private GlobalInput mGlobalInput = new GlobalInput();
    private MMap map = new MMap();

    /** 统一更新日期 */
    private String mCurrentDate = PubFun.getCurrentDate();

    /** 统一更新时间 */
    private String mCurrentTime = PubFun.getCurrentTime();

    /** 数据操作字符串 */
    private String mOperate;

    /** 业务处理相关变量 */
    private LGGroupMemberSchema mLGGroupMemberSchema = new LGGroupMemberSchema();

//private LGGroupMemberSet mLGGroupMemberSet=new LGGroupMemberSet();
    public OLGGroupMemberBL()
    {
    }

    public static void main(String[] args)
    {
    }


    /**
     * 传输数据的公共方法
     * @param: cInputData 输入的数据
     *         cOperate 数据操作
     * @return:
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //将操作数据拷贝到本类中
        this.mOperate = cOperate;

        //得到外部传入的数据,将数据备份到本类中
        if (!getInputData(cInputData))
            return false;

        //进行业务处理
        if (!dealData())
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "OLGGroupMemberBL";
            tError.functionName = "submitData";
            tError.errorMessage = "数据处理失败OLGGroupMemberBL-->dealData!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //准备往后台的数据
        if (!prepareOutputData())
            return false;

        if (this.mOperate.equals("QUERY||MAIN"))
        {
            this.submitquery();
        }
        else
        {
            PubSubmit tPubSubmit = new PubSubmit();
            if (!tPubSubmit.submitData(mInputData, mOperate))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tPubSubmit.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLGGroupMemberBL";
                tError.functionName = "submitData";
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
        if (mOperate.equals("INSERT||MAIN"))
        {
            map.put(mLGGroupMemberSchema, "INSERT"); //插入
        }
        if (mOperate.equals("UPDATE||MAIN"))
        {
            String sql = "Update LGGroupMember set " +
                         "MemberNo = '" + mLGGroupMemberSchema.getMemberNo() +
                         "' " +
              /*          "', " +
                         "Operator = '" + mGlobalInput.Operator + "', " +
                         "ModifyDate = '" + mCurrentDate + "', " +
                         "ModifyTime = '" + mCurrentTime + "' " +
               */
                         "Where  GroupNo = '" + mLGGroupMemberSchema.getGroupNo() +
                         "' ";
            map.put(sql, "UPDATE"); //修改
        }
        if (this.mOperate.equals("DELETE||MAIN"))
        {
            map.put(mLGGroupMemberSchema, "DELETE"); //删除
        }

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
        this.mLGGroupMemberSchema.setSchema((LGGroupMemberSchema) cInputData.
                                            getObjectByObjectName(
                "LGGroupMemberSchema", 0));
        this.mGlobalInput.setSchema((GlobalInput) cInputData.
                                    getObjectByObjectName("GlobalInput", 0));
        return true;
    }


    /**
     * 准备往后层输出所需要的数据
     * 输出：如果准备数据时发生错误则返回false,否则返回true
     */
    private boolean submitquery()
    {
        this.mResult.clear();
        LGGroupMemberDB tLGGroupMemberDB = new LGGroupMemberDB();
        tLGGroupMemberDB.setSchema(this.mLGGroupMemberSchema);
        //如果有需要处理的错误，则返回
        if (tLGGroupMemberDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLGGroupMemberDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "LGGroupMemberBL";
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
            this.mInputData.add(this.mLGGroupMemberSchema);
            mInputData.add(this.map);
            mResult.clear();
            mResult.add(this.mLGGroupMemberSchema);
        }
        catch (Exception ex)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "LGGroupMemberBL";
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

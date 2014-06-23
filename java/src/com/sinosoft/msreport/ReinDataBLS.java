/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import java.sql.Connection;

import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.ExeSQL;

/**
 * <p>Title: lis</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: sinosoft</p>
 * @author lh
 * @version 1.0
 */

public class ReinDataBLS
{

    //错误处理类，每个需要错误处理的类中都放置该类
    public CErrors mErrors = new CErrors();

    public ReinDataBLS()
    {
    }

    //传输数据的公共方法
    public boolean submitData()
    {
        System.out.println("Start ReinData BLS Submit...");

        boolean tReturn = false;
//对数据库进行操作，把相关数据插入到数据库中
        tReturn = save();
        if (tReturn)
        {
            System.out.println("Save sucessful");
        }
        else
        {
            System.out.println("Save failed");
        }

        System.out.println("End ReinData BLS Submit...");

        return tReturn;
    }

    /**
     * 根据前面的输入数据，进行BLS逻辑处理，保存数据
     * 如果在处理过程中出错，则返回false,否则返回true
     */
    private boolean save()
    {
        boolean tReturn = true;
        System.out.println("Start Save...");

//获得连接池的一个连接
        Connection conn = DBConnPool.getConnection();
        if (conn == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "ReinDataBLS";
            tError.functionName = "save";
            tError.errorMessage = "数据库连接失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            //设置XML汇总表的外部机构上级编码字段
//    String bSQL = "update LFXMLColl a set ParentComCodeISC=(select ParentComCodeISC from LFComISC b where a.ComCodeISC=b.ComCodeISC)";
//    ExeSQL tExeSQL = new ExeSQL(conn);
//    if(!tExeSQL.execUpdateSQL(bSQL))
//    {
//      CError tError =new CError();
//      tError.moduleName = "ReinDataBLS";
//      tError.functionName = "save";
//      tError.errorMessage = "插入机构编码对应表出错！";
//      this.mErrors.addOneError(tError);
//      return false;
//    }

            //设置XML汇总表的内部科目上级编码字段
            String tSQL1 = "update LFXMLColl a set UpItemCode=(select UpItemCode from LFItemRela b where a.ItemCode=b.ItemCode)";
            ExeSQL tExeSQL1 = new ExeSQL(conn);
            if (!tExeSQL1.execUpdateSQL(tSQL1))
            {
                CError tError = new CError();
                tError.moduleName = "ReinDataBLS";
                tError.functionName = "save";
                tError.errorMessage = "插入内部科目上级编码出错！";
                this.mErrors.addOneError(tError);
                return false;
            }

            //设置XML汇总表的层级字段
            String tSQL2 = "update LFXMLColl a set Layer=(select Layer from LFItemRela b where a.ItemCode=b.ItemCode)";
            ExeSQL tExeSQL2 = new ExeSQL(conn);
            if (!tExeSQL2.execUpdateSQL(tSQL2))
            {
                CError tError = new CError();
                tError.moduleName = "ReinDataBLS";
                tError.functionName = "save";
                tError.errorMessage = "插入层级出错！";
                this.mErrors.addOneError(tError);
                return false;
            }

            //设置XML汇总表的备注字段
            String tSQL3 = "update LFXMLColl a set Remark=(select Remark from LFItemRela b where a.ItemCode=b.ItemCode)";
            ExeSQL tExeSQL3 = new ExeSQL(conn);
            if (!tExeSQL3.execUpdateSQL(tSQL3))
            {
                CError tError = new CError();
                tError.moduleName = "ReinDataBLS";
                tError.functionName = "save";
                tError.errorMessage = "插入备注出错！";
                this.mErrors.addOneError(tError);
                return false;
            }

            conn.commit();
            conn.close();
        }
        catch (Exception ex)
        {
            System.out.println("Exception in BLS");
            ex.printStackTrace();
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "ReinDataBLS";
            tError.functionName = "save";
            tError.errorMessage = ex.toString();
            this.mErrors.addOneError(tError);
            try
            {
                conn.rollback();
                conn.close();
            }
            catch (Exception e)
            {}
            tReturn = false;
        }

        return tReturn;
    }

    public static void main(String[] args)
    {
        ReinDataBLS reinDataBLS1 = new ReinDataBLS();
    }
}

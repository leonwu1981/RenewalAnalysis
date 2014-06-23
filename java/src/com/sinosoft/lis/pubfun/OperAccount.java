/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.sql.Connection;

import com.sinosoft.lis.db.LCGrpPolDB;
import com.sinosoft.lis.db.LCPolDB;
import com.sinosoft.lis.db.LCPremDB;
import com.sinosoft.lis.schema.LCPolSchema;
import com.sinosoft.lis.vdb.LCGetToAccDBSet;
import com.sinosoft.lis.vdb.LCInsureAccDBSet;
import com.sinosoft.lis.vdb.LCInsureAccTraceDBSet;
import com.sinosoft.lis.vdb.LCPremToAccDBSet;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class OperAccount
{
    public CErrors mErrors = new CErrors(); //错误类
    private String CurrentDate = PubFun.getCurrentDate(); //系统当前时间
    private String CurrentTime = PubFun.getCurrentTime();

    public OperAccount()
    {
    }

    public static void main(String[] args)
    {
//        OperAccount operAccount1 = new OperAccount();
//        String GrpPolNo = "86110020030220000031";
//        operAccount1.OperLCPolOfGrp(GrpPolNo);
    }

    /**
     * 适用于集体已经签单，但是帐户未产生的情况
     * 在集体签单时产生帐户(帐户表，关联表，轨迹表)
     * @param GrpPolNo String
     * @return boolean
     */
    public boolean OperLCPolOfGrp(String GrpPolNo)
    {
        LCGrpPolDB tLCGrpPolDB = new LCGrpPolDB();
        tLCGrpPolDB.setGrpPolNo(GrpPolNo);
        if (tLCGrpPolDB.getInfo() == false)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "OperLCPolOfGrp";
            tError.errorMessage = "传入集体保单号不能为空!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (!tLCGrpPolDB.getAppFlag().equals("1"))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "DealAccount";
            tError.functionName = "getLCInsureAcc";
            tError.errorMessage = "该集体单尚未签单!";
            this.mErrors.addOneError(tError);
            return false;
        }

        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setGrpPolNo(tLCGrpPolDB.getGrpPolNo());
        tLCPolDB.setAppFlag("1");
        LCPolSet tLCPolSet = tLCPolSet = tLCPolDB.query();
        LCPolSchema tLCPolSchema = new LCPolSchema();
        DealAccount tDealAccount = new DealAccount();
        for (int i = 1; i <= tLCPolSet.size(); i++)
        {
            tLCPolSchema = tLCPolSet.get(i);
            TransferData tTransferData = new TransferData();
            tTransferData.setNameAndValue("PolNo", tLCPolSchema.getPolNo());
            tTransferData.setNameAndValue("AccCreatePos", "1");
            tTransferData.setNameAndValue("OtherNo", tLCPolSchema.getPolNo());
            tTransferData.setNameAndValue("OtherNoType", "1");
            VData tVData = tDealAccount.createInsureAcc(tTransferData);
            if (tDealAccount.mErrors.needDealError())
            {
                mErrors.copyAllErrors(tDealAccount.mErrors);
                return false;
            }
            else
            {
                if (tVData != null)
                {
                    LCPremDB tLCPremDB = new LCPremDB();
                    tLCPremDB.setPolNo(tLCPolSchema.getPolNo());
                    LCPremSet tLCPremSet = tLCPremDB.query();
                    tVData = tDealAccount.addPremInner(tVData, tLCPremSet, "1",
                            tLCPolSchema.getPolNo(), "1", "BF",
                            tLCPolSchema.getRiskCode(), null);
                    if (tDealAccount.mErrors.needDealError())
                    {
                        mErrors.copyAllErrors(tDealAccount.mErrors);
                        return false;
                    }
                    LCInsureAccSet tLCInsureAccSet = (LCInsureAccSet) (tVData.
                            getObjectByObjectName("LCInsureAccSet", 0));
                    LCPremToAccSet tLCPremToAccSet = (LCPremToAccSet) (tVData.
                            getObjectByObjectName("LCPremToAccSet", 0));
                    LCGetToAccSet tLCGetToAccSet = (LCGetToAccSet) (tVData.
                            getObjectByObjectName("LCGetToAccSet", 0));
                    LCInsureAccTraceSet tLCInsureAccTraceSet = (
                            LCInsureAccTraceSet) (tVData.getObjectByObjectName(
                                    "LCInsureAccTraceSet", 0));

                    Connection conn = DBConnPool.getConnection();

                    if (conn == null)
                    {
                        // @@错误处理
                        CError tError = new CError();
                        tError.moduleName = "OperAccount";
                        tError.functionName = "OperLCPolOfGrp";
                        tError.errorMessage = "数据库连接失败!";
                        this.mErrors.addOneError(tError);
                        return false;
                    }

                    try
                    {
                        conn.setAutoCommit(false);
                        //
                        if (tLCInsureAccSet != null)
                        {
                            LCInsureAccDBSet tLCInsureAccDBSet = new
                                    LCInsureAccDBSet(conn);
                            tLCInsureAccDBSet.set(tLCInsureAccSet);
                            if (tLCInsureAccDBSet.insert() == false)
                            {
                                this.mErrors.copyAllErrors(tLCInsureAccDBSet.
                                        mErrors);
                                conn.rollback();
                                conn.close();
                                return false;
                            }
                        }
                        if (tLCPremToAccSet != null)
                        {
                            LCPremToAccDBSet tLCPremToAccDBSet = new
                                    LCPremToAccDBSet(conn);
                            tLCPremToAccDBSet.set(tLCPremToAccSet);
                            if (tLCPremToAccDBSet.insert() == false)
                            {
                                this.mErrors.copyAllErrors(tLCPremToAccDBSet.
                                        mErrors);
                                conn.rollback();
                                conn.close();
                                return false;
                            }
                        }
                        if (tLCGetToAccSet != null)
                        {
                            LCGetToAccDBSet tLCGetToAccDBSet = new
                                    LCGetToAccDBSet(conn);
                            tLCGetToAccDBSet.set(tLCGetToAccSet);
                            if (tLCGetToAccDBSet.insert() == false)
                            {
                                this.mErrors.copyAllErrors(tLCGetToAccDBSet.
                                        mErrors);
                                conn.rollback();
                                conn.close();
                                return false;
                            }
                        }
                        if (tLCInsureAccTraceSet != null)
                        {
                            LCInsureAccTraceDBSet tLCInsureAccTraceDBSet = new
                                    LCInsureAccTraceDBSet(conn);
                            tLCInsureAccTraceDBSet.set(tLCInsureAccTraceSet);
                            if (tLCInsureAccTraceDBSet.insert() == false)
                            {
                                // @@错误处理
                                this.mErrors.copyAllErrors(
                                        tLCInsureAccTraceDBSet.mErrors);
                                conn.rollback();
                                conn.close();
                                return false;
                            }
                        }

                        conn.commit();
                        conn.close();

                    }
                    catch (Exception ex)
                    {
                        try
                        {
                            conn.rollback();
                        }
                        catch (Exception e)
                        {}
                        return false;
                    }

                }
            }

        }
        return true;
    }
}

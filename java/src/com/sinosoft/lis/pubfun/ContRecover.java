/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.sql.Connection;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vdb.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;


/**
 * <p>Title: Web业务系统</p>
 * <p>Description: 保全确认逻辑处理类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author Tjj
 * @version 1.0
 */
public class ContRecover
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();


    /** 往后面传输数据的容器 */
    private VData mInputData;


    /** 往界面传输数据的容器 */
    private VData mResult = new VData();


    /** 数据操作字符串 */
    private String mOperate;


    /**保单号*/
    private String mContNo;


    /**保单类型*/
    private String mContType;


    /**相关操作的代码 */
    private String mOtherNo;


    /** 全局数据 */
    private GlobalInput mGlobalInput = new GlobalInput();


    /**
     * constructor
     * @param aContNo
     */
    public ContRecover(String aContNo)
    {
        mContNo = aContNo;
    }


    /**
     *
     * @param aContNo
     * @param aOtherNo
     */
    public ContRecover(String aContNo, String aOtherNo)
    {
        mContNo = aContNo;
        mOtherNo = aOtherNo;
        mContType = "I";
    }


    /**
     *
     * @param aContNo
     * @param aOtherNo
     * @param aContType
     */
    public ContRecover(String aContNo, String aOtherNo, String aContType)
    {
        mContNo = aContNo;
        mOtherNo = aOtherNo;
        mContType = aContType;
    }


    /**
     * 传输数据的公共方法
     * @param: cInputData 输入的数据
     *         cOperate 数据操作
     * @return:
     */
    public void setOperate(String cOperate)
    {
        this.mOperate = cOperate;
    }

    public String getOperate()
    {
        return this.mOperate;
    }

    public boolean submitData()
    {
        if (!dealData())
            return false;
        System.out.println("---dealData---");
        return true;
    }


    /**
     * 返回
     * @return VData
     */
    public VData getResult()
    {
        return mResult;
    }


    /**
     * 数据准备
     * @return boolean
     */
    public boolean dealData()
    {
        if (mContType.equals("G"))
        {
            LBPolDB tLBPolDB = new LBPolDB();
            LBPolSet tLBPolSet = new LBPolSet();

            tLBPolDB.setGrpPolNo(mContNo);
            tLBPolSet = tLBPolDB.query();
            for (int i = 1; i <= tLBPolSet.size(); i++)
            {
                VData tInputData = new VData();
                tInputData = preparePolData(tLBPolSet.get(i).getPolNo(),
                                            mOtherNo);
                if (!saveData(tInputData))
                    return false;
            }
            if (tLBPolSet.size() > 0)
            {
                VData tInputData = new VData();
                tInputData = prepareGrpData(mContNo, mOtherNo);
                if (!saveGrpData(tInputData))
                    return false;
            }
        }
        else
        {
            VData tInputData = new VData();
            tInputData = preparePolData(mContNo, mOtherNo);
            if (!saveData(tInputData))
                return false;
        }
        return true;
    }


    /**
     *
     * @param aGrpPolNo
     * @param aOtherNo
     * @return VData
     */
    private VData prepareGrpData(String aGrpPolNo, String aOtherNo)
    {
        VData aInputData = new VData();
        aInputData.clear();

        LCGrpPolSchema tLCGrpPolSchema = new LCGrpPolSchema();
        LBGrpPolSchema tLBGrpPolSchema = new LBGrpPolSchema();
        LBGrpPolSet tLBGrpPolSet = new LBGrpPolSet();
        LCGrpPolSet tLCGrpPolSet = new LCGrpPolSet();
        Reflections tReflections = new Reflections();

        //准备数据
        //保单信息备份
        tLBGrpPolSet.clear();
        tLCGrpPolSet.clear();
        LBGrpPolDB tLBGrpPolDB = new LBGrpPolDB();
        tLBGrpPolDB.setGrpPolNo(aGrpPolNo);
        tLBGrpPolDB.setEdorNo(aOtherNo);
        tLBGrpPolSet = tLBGrpPolDB.query();
        for (int i = 1; i <= tLBGrpPolSet.size(); i++)
        {
            tLCGrpPolSchema = new LCGrpPolSchema();
            tLBGrpPolSchema = tLBGrpPolSet.get(i);
            tReflections.transFields(tLCGrpPolSchema, tLBGrpPolSchema);
            tLCGrpPolSet.add(tLCGrpPolSchema);
        }

        aInputData.clear();
        aInputData.addElement(tLCGrpPolSet);
        return aInputData;
    }


    /**
     * 准备需要保存的数据 (备份保单信息)
     * @param aPolNo
     * @param aEdorNo
     * @return VData
     */
    private VData preparePolData(String aPolNo, String aEdorNo)
    {
        //定义变量
        int m, count;
        VData tVData = new VData();
        Reflections tReflections = new Reflections();

//        LCContSchema tLCContSchema = new LCContSchema();
//        LBContSchema tLBContSchema = new LBContSchema();
//        LCGrpPolSchema tLCGrpPolSchema = new LCGrpPolSchema();
//        LBGrpPolSchema tLBGPolSchema = new LBGrpPolSchema();
//        LBGrpPolSet tLBGrpPolSet = new LBGrpPolSet();
//        LCGrpPolSet tLCGrpPolSet = new LCGrpPolSet();

        LCPolSchema tLCPolSchema = new LCPolSchema();
        LBPolSchema tLBPolSchema = new LBPolSchema();
        LBPolSet tLBPolSet = new LBPolSet();
        LCPolSet tLCPolSet = new LCPolSet();

        LCDutySchema tLCDutySchema = new LCDutySchema();
        LBDutySchema tLBDutySchema = new LBDutySchema();
        LBDutySet tLBDutySet = new LBDutySet();
        LCDutySet tLCDutySet = new LCDutySet();

        LCPremSchema tLCPremSchema = new LCPremSchema();
        LBPremSchema tLBPremSchema = new LBPremSchema();
        LBPremSet tLBPremSet = new LBPremSet();
        LCPremSet tLCPremSet = new LCPremSet();

        LCGetSchema tLCGetSchema = new LCGetSchema();
        LBGetSchema tLBGetSchema = new LBGetSchema();
        LBGetSet tLBGetSet = new LBGetSet();
        LCGetSet tLCGetSet = new LCGetSet();

        LCCustomerImpartSchema tLCCustomerImpartSchema = new
                LCCustomerImpartSchema();
        LBCustomerImpartSchema tLBCustomerImpartSchema = new
                LBCustomerImpartSchema();
        LBCustomerImpartSet tLBCustomerImpartSet = new LBCustomerImpartSet();
        LCCustomerImpartSet tLCCustomerImpartSet = new LCCustomerImpartSet();

//        LCAppntGrpSchema tLCAppntGrpSchema = new LCAppntGrpSchema();
//        LBAppntGrpSchema tLBAppntGrpSchema = new LBAppntGrpSchema();
//        LBAppntGrpSet tLBAppntGrpSet = new LBAppntGrpSet();
//        LCAppntGrpSet tLCAppntGrpSet = new LCAppntGrpSet();

        LCAppntIndSchema tLCAppntIndSchema = new LCAppntIndSchema();
        LBAppntIndSchema tLBAppntIndSchema = new LBAppntIndSchema();
        LBAppntIndSet tLBAppntIndSet = new LBAppntIndSet();
        LCAppntIndSet tLCAppntIndSet = new LCAppntIndSet();

        LCInsuredSchema tLCInsuredSchema = new LCInsuredSchema();
        LBInsuredSchema tLBInsuredSchema = new LBInsuredSchema();
        LBInsuredSet tLBInsuredSet = new LBInsuredSet();
        LCInsuredSet tLCInsuredSet = new LCInsuredSet();

        LCBnfSchema tLCBnfSchema = new LCBnfSchema();
        LBBnfSchema tLBBnfSchema = new LBBnfSchema();
        LBBnfSet tLBBnfSet = new LBBnfSet();
        LCBnfSet tLCBnfSet = new LCBnfSet();

        m = 0;
        count = 0;
        tVData.clear();
        //准备数据
        //保单信息备份
        tLBPolSet.clear();
        tLCPolSet.clear();
        LBPolDB tLBPolDB = new LBPolDB();
        tLBPolDB.setPolNo(aPolNo);
        tLBPolDB.setEdorNo(aEdorNo);
        tLBPolSet = tLBPolDB.query();
        for (int i = 1; i <= tLBPolSet.size(); i++)
        {
            tLCPolSchema = new LCPolSchema();
            tLBPolSchema = tLBPolSet.get(i);
            tReflections.transFields(tLCPolSchema, tLBPolSchema);
            tLCPolSet.add(tLCPolSchema);
        }
        if (tLBPolSet.size() > 0)
            tVData.addElement(tLCPolSet);
            //保单责任表备份
        tLBDutySet.clear();
        tLCDutySet.clear();
        LBDutyDB tLBDutyDB = new LBDutyDB();
        tLBDutyDB.setPolNo(aPolNo);
        tLBDutyDB.setEdorNo(aEdorNo);
        tLBDutySet = tLBDutyDB.query();
        for (int i = 1; i <= tLBDutySet.size(); i++)
        {
            tLCDutySchema = new LCDutySchema();
            tLBDutySchema = tLBDutySet.get(i);
            tReflections.transFields(tLCDutySchema, tLBDutySchema);

            tLCDutySet.add(tLCDutySchema);
        }
        if (tLBDutySet.size() > 0)
            tVData.addElement(tLCDutySet);
            //保单交费表备份
        tLBPremSet.clear();
        tLCPremSet.clear();
        LBPremDB tLBPremDB = new LBPremDB();
        tLBPremDB.setPolNo(aPolNo);
        tLBPremDB.setEdorNo(aEdorNo);
        tLBPremSet = tLBPremDB.query();
        for (int i = 1; i <= tLBPremSet.size(); i++)
        {
            tLCPremSchema = new LCPremSchema();
            tLBPremSchema = tLBPremSet.get(i);
            tReflections.transFields(tLCPremSchema, tLBPremSchema);

            tLCPremSet.add(tLCPremSchema);
        }
        if (tLBPremSet.size() > 0)
            tVData.addElement(tLCPremSet);
            //保单给付责任表备份
        tLBGetSet.clear();
        tLCGetSet.clear();
        LBGetDB tLBGetDB = new LBGetDB();
        tLBGetDB.setPolNo(aPolNo);
        tLBGetDB.setEdorNo(aEdorNo);
        tLBGetSet = tLBGetDB.query();

        for (int i = 1; i <= tLBGetSet.size(); i++)
        {
            tLBGetSchema = tLBGetSet.get(i);
            tLCGetSchema = new LCGetSchema();
            tReflections.transFields(tLCGetSchema, tLBGetSchema);

            tLCGetSet.add(tLCGetSchema);
        }
        System.out.println("tLCGetSet.size" + tLCGetSet.size());
        if (tLBGetSet.size() > 0)
            tVData.addElement(tLCGetSet);
            //保单个人健康告知备份
        tLBCustomerImpartSet.clear();
        tLCCustomerImpartSet.clear();
        LBCustomerImpartDB tLBCustomerImpartDB = new LBCustomerImpartDB();
        /*Lis5.3 upgrade set
             tLBCustomerImpartDB.setPolNo(aPolNo);
         */
        tLBCustomerImpartDB.setEdorNo(aEdorNo);
        tLBCustomerImpartSet = tLBCustomerImpartDB.query();
        for (int i = 1; i <= tLBCustomerImpartSet.size(); i++)
        {
            tLBCustomerImpartSchema = tLBCustomerImpartSet.get(i);
            tLCCustomerImpartSchema = new LCCustomerImpartSchema();
            tReflections.transFields(tLCCustomerImpartSchema,
                                     tLBCustomerImpartSchema);

            tLCCustomerImpartSet.add(tLCCustomerImpartSchema);
        }
        if (tLBCustomerImpartSet.size() > 0)
            tVData.addElement(tLCCustomerImpartSet);
            //保单被保人信息备份
        tLBInsuredSet.clear();
        tLCInsuredSet.clear();
        LBInsuredDB tLBInsuredDB = new LBInsuredDB();
        /*Lis5.3 upgrade set
             tLBInsuredDB.setPolNo(aPolNo);
         */
        tLBInsuredDB.setEdorNo(aEdorNo);
        tLBInsuredSet = tLBInsuredDB.query();
        for (int i = 1; i <= tLBInsuredSet.size(); i++)
        {
            tLBInsuredSchema = tLBInsuredSet.get(i);
            tLCInsuredSchema = new LCInsuredSchema();
            tReflections.transFields(tLCInsuredSchema, tLBInsuredSchema);

            tLCInsuredSet.add(tLCInsuredSchema);
        }
        if (tLBInsuredSet.size() > 0)
            tVData.addElement(tLCInsuredSet);
            //保单投保人信息
        tLBAppntIndSet.clear();
        tLCAppntIndSet.clear();
        LBAppntIndDB tLBAppntIndDB = new LBAppntIndDB();
        tLBAppntIndDB.setPolNo(aPolNo);
        tLBAppntIndDB.setEdorNo(aEdorNo);
        tLBAppntIndSet = tLBAppntIndDB.query();
        for (int i = 1; i <= tLBAppntIndSet.size(); i++)
        {
            tLBAppntIndSchema = tLBAppntIndSet.get(i);
            tLCAppntIndSchema = new LCAppntIndSchema();
            tReflections.transFields(tLCAppntIndSchema, tLBAppntIndSchema);

            tLCAppntIndSet.add(tLCAppntIndSchema);
        }
        if (tLBAppntIndSet.size() > 0)
            tVData.addElement(tLCAppntIndSet);
            //保单受益人信息
        tLBBnfSet.clear();
        tLCBnfSet.clear();
        LBBnfDB tLBBnfDB = new LBBnfDB();
        tLBBnfDB.setPolNo(aPolNo);
        tLBBnfDB.setEdorNo(aEdorNo);
        tLBBnfSet = tLBBnfDB.query();
        for (int i = 1; i <= tLBBnfSet.size(); i++)
        {
            tLBBnfSchema = tLBBnfSet.get(i);
            tLCBnfSchema = new LCBnfSchema();
            tReflections.transFields(tLCBnfSchema, tLBBnfSchema);

            tLCBnfSet.add(tLCBnfSchema);
        }
        if (tLBBnfSet.size() > 0)
            tVData.addElement(tLCBnfSet);

        return tVData;
    }


    /**
     * 保存个人保单信息
     * @param aInputData
     * @return boolean
     */
    private boolean saveData(VData aInputData)
    {

        LCPolSet tLCPolSet = new LCPolSet();
        LCDutySet tLCDutySet = new LCDutySet();
        LCPremSet tLCPremSet = new LCPremSet();
        LCGetSet tLCGetSet = new LCGetSet();
        LCCustomerImpartSet tLCCustomerImpartSet = new LCCustomerImpartSet();
        LCAppntIndSet tLCAppntIndSet = new LCAppntIndSet();
        LCInsuredSet tLCInsuredSet = new LCInsuredSet();
        LCBnfSet tLCBnfSet = new LCBnfSet();

//        LBPolDB tLBPolDB;
//        LBDutyDB tLBDutyDB;
//        LBPremDB tLBPremDB;
//        LBGetDB tLBGetDB;
//        LBCustomerImpartDB tLBCustomerImpartDB;
//        LBAppntIndDB tLBAppntIndDB;
//        LBInsuredDB tLBInsuredDB;
//        LBBnfDB tLBBnfDB;

        LCPolDBSet tLCPolDBSet;
        LCDutyDBSet tLCDutyDBSet;
        LCPremDBSet tLCPremDBSet;
        LCGetDBSet tLCGetDBSet;
        LCCustomerImpartDBSet tLCCustomerImpartDBSet;
        LCAppntIndDBSet tLCAppntIndDBSet;
        LCInsuredDBSet tLCInsuredDBSet;
        LCBnfDBSet tLCBnfDBSet;

        tLCPolSet = (LCPolSet) aInputData.getObjectByObjectName("LCPolSet", 0);
        tLCDutySet = (LCDutySet) aInputData.getObjectByObjectName("LCDutySet",
                0);
        tLCPremSet = (LCPremSet) aInputData.getObjectByObjectName("LCPremSet",
                0);
        tLCGetSet = (LCGetSet) aInputData.getObjectByObjectName("LCGetSet", 0);
        tLCCustomerImpartSet = (LCCustomerImpartSet) aInputData.
                               getObjectByObjectName("LCCustomerImpartSet", 0);
        tLCAppntIndSet = (LCAppntIndSet) aInputData.getObjectByObjectName(
                "LCAppntIndSet", 0);
        tLCInsuredSet = (LCInsuredSet) aInputData.getObjectByObjectName(
                "LCInsuredSet", 0);
        tLCBnfSet = (LCBnfSet) aInputData.getObjectByObjectName("LCBnfSet", 0);

        Connection conn = null;
        conn = DBConnPool.getConnection();

        if (conn == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "ContRecover";
            tError.functionName = "saveData";
            tError.errorMessage = "数据库连接失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            conn.setAutoCommit(false);
            //保单信息备份
            if (tLCPolSet != null && tLCPolSet.size() > 0)
            {
                tLCPolDBSet = new LCPolDBSet(conn);
                tLCPolDBSet.set(tLCPolSet);

                if (!tLCPolDBSet.insert())
                {
                    // @@错误处理
                    CError tError = new CError();
                    tError.moduleName = "ContRecover";
                    tError.functionName = "saveData";
                    tError.errorMessage = "保单信息备份失败!";
                    this.mErrors.addOneError(tError);
                    System.out.println(tError);
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            //责任信息备份
            if (tLCDutySet != null && tLCDutySet.size() > 0)
            {
                tLCDutyDBSet = new LCDutyDBSet(conn);
                tLCDutyDBSet.set(tLCDutySet);

                if (!tLCDutyDBSet.insert())
                {
                    // @@错误处理
                    CError tError = new CError();
                    tError.moduleName = "ContRecover";
                    tError.functionName = "saveData";
                    tError.errorMessage = "责任信息备份失败!";
                    this.mErrors.addOneError(tError);
                    System.out.println(tError);
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            //交费信息备份
            if (tLCPremSet != null && tLCPremSet.size() > 0)
            {

                tLCPremDBSet = new LCPremDBSet(conn);
                tLCPremDBSet.set(tLCPremSet);

                if (!tLCPremDBSet.insert())
                {
                    // @@错误处理
                    CError tError = new CError();
                    tError.moduleName = "ContRecover";
                    tError.functionName = "saveData";
                    tError.errorMessage = "保费项信息备份失败!";
                    this.mErrors.addOneError(tError);
                    System.out.println(tError);
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            //给付信息备份
            if (tLCGetSet != null && tLCGetSet.size() > 0)
            {

                tLCGetDBSet = new LCGetDBSet(conn);
                System.out.println("------" + tLCGetSet.size());
                tLCGetDBSet.set(tLCGetSet);
                System.out.println("------" + tLCGetDBSet.size());
                if (!tLCGetDBSet.insert())
                {
                    // @@错误处理
                    CError tError = new CError();
                    tError.moduleName = "ContRecover";
                    tError.functionName = "saveData";
                    tError.errorMessage = "给付项信息备份失败!";
                    this.mErrors.addOneError(tError);
                    System.out.println(tError);
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            //健康告知信息备份
            if (tLCCustomerImpartSet != null && tLCCustomerImpartSet.size() > 0)
            {

                tLCCustomerImpartDBSet = new LCCustomerImpartDBSet(conn);
                tLCCustomerImpartDBSet.set(tLCCustomerImpartSet);

                if (!tLCCustomerImpartDBSet.insert())
                {
                    // @@错误处理
                    CError tError = new CError();
                    tError.moduleName = "ContRecover";
                    tError.functionName = "saveData";
                    tError.errorMessage = "健康告知信息备份失败!";
                    this.mErrors.addOneError(tError);
                    System.out.println(tError);
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            //投保人信息备份
            if (tLCAppntIndSet != null && tLCAppntIndSet.size() > 0)
            {

                tLCAppntIndDBSet = new LCAppntIndDBSet(conn);
                tLCAppntIndDBSet.set(tLCAppntIndSet);

                if (!tLCAppntIndDBSet.insert())
                {
                    // @@错误处理
                    CError tError = new CError();
                    tError.moduleName = "ContRecover";
                    tError.functionName = "saveData";
                    tError.errorMessage = "投保人信息备份失败!";
                    this.mErrors.addOneError(tError);
                    System.out.println(tError);
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            //被保人信息备份
            if (tLCInsuredSet != null && tLCInsuredSet.size() > 0)
            {

                tLCInsuredDBSet = new LCInsuredDBSet(conn);
                tLCInsuredDBSet.set(tLCInsuredSet);

                if (!tLCInsuredDBSet.insert())
                {
                    // @@错误处理
                    CError tError = new CError();
                    tError.moduleName = "ContRecover";
                    tError.functionName = "saveData";
                    tError.errorMessage = "被保人信息备份失败!";
                    this.mErrors.addOneError(tError);
                    System.out.println(tError);
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            //受益人信息备份
            if (tLCBnfSet != null && tLCBnfSet.size() > 0)
            {

                tLCBnfDBSet = new LCBnfDBSet(conn);
                tLCBnfDBSet.set(tLCBnfSet);

                if (!tLCBnfDBSet.insert())
                {
                    // @@错误处理
                    CError tError = new CError();
                    tError.moduleName = "ContRecover";
                    tError.functionName = "saveData";
                    tError.errorMessage = "受益人信息备份失败!";
                    this.mErrors.addOneError(tError);
                    System.out.println(tError);
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
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "ContRecover";
            tError.functionName = "saveData";
            tError.errorMessage = ex.toString();
            this.mErrors.addOneError(tError);
            try
            {
                conn.rollback();
                conn.close();
            }
            catch (Exception e)
            {}
            return false;
        }
        return true;
    }


    /**
     * 集体单备份
     * @param aInputData
     * @return boolean
     */
    private boolean saveGrpData(VData aInputData)
    {
        LCGrpPolSet tLCGrpPolSet = new LCGrpPolSet();

        tLCGrpPolSet = (LCGrpPolSet) aInputData.getObjectByObjectName(
                "LCGrpPolSet", 0);

        Connection conn = null;
        conn = DBConnPool.getConnection();

        if (conn == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PGrpEdorConfirmWTBLS";
            tError.functionName = "saveData";
            tError.errorMessage = "数据库连接失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            conn.setAutoCommit(false);
            //保单信息备份
            if (tLCGrpPolSet != null && tLCGrpPolSet.size() > 0)
            {

                LCGrpPolDBSet tLCGrpPolDBSet = new LCGrpPolDBSet(conn);
                tLCGrpPolDBSet.set(tLCGrpPolSet);

                if (!tLCGrpPolDBSet.insert())
                {
                    // @@错误处理
                    CError tError = new CError();
                    tError.moduleName = "PGrpEdorConfirmWTBLS";
                    tError.functionName = "saveData";
                    tError.errorMessage = "集体保单备份失败!";
                    this.mErrors.addOneError(tError);
                    System.out.println(tError);
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return true;
    }


    public static void main(String[] args)
    {
        String aContNo, aOtherNo;

        aContNo = "00000020020110000015";
        aOtherNo = "0010000010";
        ContRecover aContRecover = new ContRecover(aContNo, aOtherNo);
        aContRecover.submitData();
    }

}

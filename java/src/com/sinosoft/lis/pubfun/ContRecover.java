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
 * <p>Title: Webҵ��ϵͳ</p>
 * <p>Description: ��ȫȷ���߼�������</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author Tjj
 * @version 1.0
 */
public class ContRecover
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();


    /** �����洫�����ݵ����� */
    private VData mInputData;


    /** �����洫�����ݵ����� */
    private VData mResult = new VData();


    /** ���ݲ����ַ��� */
    private String mOperate;


    /**������*/
    private String mContNo;


    /**��������*/
    private String mContType;


    /**��ز����Ĵ��� */
    private String mOtherNo;


    /** ȫ������ */
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
     * �������ݵĹ�������
     * @param: cInputData ���������
     *         cOperate ���ݲ���
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
     * ����
     * @return VData
     */
    public VData getResult()
    {
        return mResult;
    }


    /**
     * ����׼��
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

        //׼������
        //������Ϣ����
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
     * ׼����Ҫ��������� (���ݱ�����Ϣ)
     * @param aPolNo
     * @param aEdorNo
     * @return VData
     */
    private VData preparePolData(String aPolNo, String aEdorNo)
    {
        //�������
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
        //׼������
        //������Ϣ����
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
            //�������α���
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
            //�������ѱ���
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
            //�����������α���
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
            //�������˽�����֪����
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
            //������������Ϣ����
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
            //����Ͷ������Ϣ
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
            //������������Ϣ
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
     * ������˱�����Ϣ
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
            // @@������
            CError tError = new CError();
            tError.moduleName = "ContRecover";
            tError.functionName = "saveData";
            tError.errorMessage = "���ݿ�����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            conn.setAutoCommit(false);
            //������Ϣ����
            if (tLCPolSet != null && tLCPolSet.size() > 0)
            {
                tLCPolDBSet = new LCPolDBSet(conn);
                tLCPolDBSet.set(tLCPolSet);

                if (!tLCPolDBSet.insert())
                {
                    // @@������
                    CError tError = new CError();
                    tError.moduleName = "ContRecover";
                    tError.functionName = "saveData";
                    tError.errorMessage = "������Ϣ����ʧ��!";
                    this.mErrors.addOneError(tError);
                    System.out.println(tError);
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            //������Ϣ����
            if (tLCDutySet != null && tLCDutySet.size() > 0)
            {
                tLCDutyDBSet = new LCDutyDBSet(conn);
                tLCDutyDBSet.set(tLCDutySet);

                if (!tLCDutyDBSet.insert())
                {
                    // @@������
                    CError tError = new CError();
                    tError.moduleName = "ContRecover";
                    tError.functionName = "saveData";
                    tError.errorMessage = "������Ϣ����ʧ��!";
                    this.mErrors.addOneError(tError);
                    System.out.println(tError);
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            //������Ϣ����
            if (tLCPremSet != null && tLCPremSet.size() > 0)
            {

                tLCPremDBSet = new LCPremDBSet(conn);
                tLCPremDBSet.set(tLCPremSet);

                if (!tLCPremDBSet.insert())
                {
                    // @@������
                    CError tError = new CError();
                    tError.moduleName = "ContRecover";
                    tError.functionName = "saveData";
                    tError.errorMessage = "��������Ϣ����ʧ��!";
                    this.mErrors.addOneError(tError);
                    System.out.println(tError);
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            //������Ϣ����
            if (tLCGetSet != null && tLCGetSet.size() > 0)
            {

                tLCGetDBSet = new LCGetDBSet(conn);
                System.out.println("------" + tLCGetSet.size());
                tLCGetDBSet.set(tLCGetSet);
                System.out.println("------" + tLCGetDBSet.size());
                if (!tLCGetDBSet.insert())
                {
                    // @@������
                    CError tError = new CError();
                    tError.moduleName = "ContRecover";
                    tError.functionName = "saveData";
                    tError.errorMessage = "��������Ϣ����ʧ��!";
                    this.mErrors.addOneError(tError);
                    System.out.println(tError);
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            //������֪��Ϣ����
            if (tLCCustomerImpartSet != null && tLCCustomerImpartSet.size() > 0)
            {

                tLCCustomerImpartDBSet = new LCCustomerImpartDBSet(conn);
                tLCCustomerImpartDBSet.set(tLCCustomerImpartSet);

                if (!tLCCustomerImpartDBSet.insert())
                {
                    // @@������
                    CError tError = new CError();
                    tError.moduleName = "ContRecover";
                    tError.functionName = "saveData";
                    tError.errorMessage = "������֪��Ϣ����ʧ��!";
                    this.mErrors.addOneError(tError);
                    System.out.println(tError);
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            //Ͷ������Ϣ����
            if (tLCAppntIndSet != null && tLCAppntIndSet.size() > 0)
            {

                tLCAppntIndDBSet = new LCAppntIndDBSet(conn);
                tLCAppntIndDBSet.set(tLCAppntIndSet);

                if (!tLCAppntIndDBSet.insert())
                {
                    // @@������
                    CError tError = new CError();
                    tError.moduleName = "ContRecover";
                    tError.functionName = "saveData";
                    tError.errorMessage = "Ͷ������Ϣ����ʧ��!";
                    this.mErrors.addOneError(tError);
                    System.out.println(tError);
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            //��������Ϣ����
            if (tLCInsuredSet != null && tLCInsuredSet.size() > 0)
            {

                tLCInsuredDBSet = new LCInsuredDBSet(conn);
                tLCInsuredDBSet.set(tLCInsuredSet);

                if (!tLCInsuredDBSet.insert())
                {
                    // @@������
                    CError tError = new CError();
                    tError.moduleName = "ContRecover";
                    tError.functionName = "saveData";
                    tError.errorMessage = "��������Ϣ����ʧ��!";
                    this.mErrors.addOneError(tError);
                    System.out.println(tError);
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            //��������Ϣ����
            if (tLCBnfSet != null && tLCBnfSet.size() > 0)
            {

                tLCBnfDBSet = new LCBnfDBSet(conn);
                tLCBnfDBSet.set(tLCBnfSet);

                if (!tLCBnfDBSet.insert())
                {
                    // @@������
                    CError tError = new CError();
                    tError.moduleName = "ContRecover";
                    tError.functionName = "saveData";
                    tError.errorMessage = "��������Ϣ����ʧ��!";
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
            // @@������
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
     * ���嵥����
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
            // @@������
            CError tError = new CError();
            tError.moduleName = "PGrpEdorConfirmWTBLS";
            tError.functionName = "saveData";
            tError.errorMessage = "���ݿ�����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            conn.setAutoCommit(false);
            //������Ϣ����
            if (tLCGrpPolSet != null && tLCGrpPolSet.size() > 0)
            {

                LCGrpPolDBSet tLCGrpPolDBSet = new LCGrpPolDBSet(conn);
                tLCGrpPolDBSet.set(tLCGrpPolSet);

                if (!tLCGrpPolDBSet.insert())
                {
                    // @@������
                    CError tError = new CError();
                    tError.moduleName = "PGrpEdorConfirmWTBLS";
                    tError.functionName = "saveData";
                    tError.errorMessage = "���屣������ʧ��!";
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

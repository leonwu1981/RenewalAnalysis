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
 * <p>Title: Webҵ��ϵͳ</p>
 * <p>Description: ������</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author Minim
 * @version 1.0
 */
public class LockTableBL
{
    /** �������ݵ����� */
    private VData mInputData = new VData();
    /** �������ݵ����� */
    private VData mResult = new VData();
    /** ���ݲ����ַ��� */
    private String mOperate;
    /** �������� */
    public CErrors mErrors = new CErrors();
    /** ȫ�ֻ������� */
    private GlobalInput mGlobalInput = new GlobalInput();
    /** ���⴫�ݵĲ��� */
    //private TransferData mTransferData = null;

    /** �����ҵ������ */
    private LDSysTraceSet inLDSysTraceSet = new LDSysTraceSet();

    /** ������ҵ������ */
    private LDSysTraceSet outLDSysTraceSet = new LDSysTraceSet();

    public LockTableBL()
    {
    }

    /**
     * �����ύ�Ĺ����������ύ�ɹ��󽫷��ؽ���������ڲ�VData������
     * @param cInputData ���������,VData����
     * @param cOperate ���ݲ����ַ�������Ҫ����"INSERT"
     * @return ����ֵ��true--�ύ�ɹ�, false--�ύʧ�ܣ�
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //���������ݿ�����������
        this.mInputData = (VData) cInputData.clone();
        this.mOperate = cOperate;

        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData())
        {
            return false;
        }
        System.out.println("---End getInputData---");

        //����ҵ����
        if (!dealData())
        {
            return false;
        }
        System.out.println("---End dealData---");

        //��Ҫ������̨����
        if (mOperate.equals("INSERT") || mOperate.equals("DELETE"))
        {
            //׼������̨������
            if (!prepareOutputData())
            {
                return false;
            }
            System.out.println("---End prepareOutputData---");

            System.out.println("Start LockTable BLS Submit...");
            LockTableBLS tLockTableBLS = new LockTableBLS();
            if (tLockTableBLS.submitData(mInputData, cOperate) == false)
            {
                //@@������
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
        //����Ҫ������̨����
        else if (mOperate.equals(""))
        {
        }

        return true;
    }

    /**
     * ���ⲿ��������ݷֽ⵽�����������
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
            // @@������
            e.printStackTrace();
            CError tError = new CError();
            tError.moduleName = "LockTableBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "��������ʧ��!!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * У������
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
     * ����ǰ����������ݣ������߼�����
     * @return ����ڴ�������г����򷵻�false,���򷵻�true
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
                        throw new Exception("У������ʧ�ܣ���Ҫ����������ĺ��������״̬��");
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
                        throw new Exception("У������ʧ�ܣ���Ҫ����������ĺ��������״̬��");
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
                        throw new Exception("У������ʧ�ܣ���Ҫ����������ĺ��������״̬��");
                    }

                    outLDSysTraceSet.add(tLDSysTraceSchema.getDB().query());
                }
            }
        }
        catch (Exception e)
        {
            // @@������
            e.printStackTrace();
            CError tError = new CError();
            tError.moduleName = "LockTableBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���ݴ������! " + e.getMessage();
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * ׼��������������Ҫ������
     * @return ���׼������ʱ���������򷵻�false,���򷵻�true
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
            // @@������
            CError tError = new CError();
            tError.moduleName = "LockTableBL";
            tError.functionName = "prepareOutputData";
            tError.errorMessage = "��׼������㴦������Ҫ������ʱ����! ";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * �������������������ȡ���ݴ�����
     * @return ���������ݲ�ѯ����ַ�����VData����
     */
    public VData getResult()
    {
        return mResult;
    }

    /**
     * ��������������
     * @param args String[]
     */
    public static void main(String[] args)
    {
//        LockTableBL LockTableBL1 = new LockTableBL();
    }
}

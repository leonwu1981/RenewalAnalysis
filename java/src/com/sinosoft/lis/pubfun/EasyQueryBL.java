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
 * <p>Title: Webҵ��ϵͳ </p>
 * <p>Description: BL��ҵ���߼������� </p>
 * <p>Copyright: Copyright (c) 2002 </p>
 * <p>Company: Sinosoft </p>
 * @author HST
 * @version 1.0
 * @date 2002-09-03
 */
public class EasyQueryBL
{
    /** �������ݵ����� */
    private VData mInputData = new VData();

    /** �������ݵ����� */
    private VData mResult = new VData();

    /** ���ݲ����ַ��� */
    private String mOperate;

    /** �������� */
    public CErrors mErrors = new CErrors();

    /** ҵ������ر��� */
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
     * �������ݵĹ�������, ������û�к�����BLS�㣬�ʸ÷�������
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        // ����������ݿ�����������
        mInputData = (VData) cInputData.clone();
        this.mOperate = cOperate;

        // ���ⲿ��������ݷֽ⵽����������У�׼������
        if (!this.getInputData())
        {
            return false;
        }

        //System.out.println("---End getInputData---");
        // ��ѯ����,��ѯ�ķ�֧���Ը���ҵ��Ҫ��ŵ���ͬ�ĵ��ü�����
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
     * ���ⲿ��������ݷֽ⵽�����������
     * @return boolean
     */
    private boolean getInputData()
    {
        mSQL = (String) mInputData.get(0);
        if ((mSQL == null) || mSQL.trim().equals(""))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "EasyQueryBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "û�д���SQL���!";
            this.mErrors.addOneError(tError);
            return false;
        }
        Integer tStart = (Integer) mInputData.get(1);
        mStartIndex = tStart.intValue();
        mLargeFlag = (String) mInputData.get(2);
//        if (mLargeFlag.equals("������"))
//        {
//            System.out.println("�ȼ�");
//        }
//        else
//        {
//            System.out.println("���ȼ�");
//        }
//        mLargeFlag = "0";
        if (mLargeFlag == null)
        {
            mLargeFlag = "0";
        }

        return true;
    }


    /**
     * ��Ҫ��Ϣ��ѯ
     * @return: boolean
     */
    private boolean queryData()
    {
        ExeSQL tExeSQL = new ExeSQL();
        //��Ӵ���������־�����ڴ����������������ⷽʽ��ѯ��Ĭ������¶���С��������ѯ
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
            // @@������
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
     * �������������������ȡ���ݴ�����
     * @return ���������ݲ�ѯ����ַ�����VData����
     */
    public VData getResult()
    {
        return mResult;
    }

    /**
     * ���Ժ���
     * @param args String[]
     */
    public static void main(String[] args)
    {
//        System.out.println(Character.digit('A', 0));
    }
}

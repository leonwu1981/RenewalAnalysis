package com.sinosoft.task;

import com.sinosoft.utility.*;
import com.sinosoft.lis.bl.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;

/**
 * <p>Title: ��������ϵͳ</p>
 * <p>Description: �����ջ�BL��ҵ���߼������� </p>
 * <p>Copyright: Copyright (c) 2005 </p>
 * <p>Company: Sinosoft </p>
 * @author QiuYang
 * @version 1.0
 * @date 2005-01-20
 */

public class TaskBackBL
{
    /** �������� */
    public CErrors mErrors = new CErrors();

    /** �������ݵ����� */
    private VData mInputData = new VData();

    /** ������ݵ����� */
    private VData mResult = new VData();

    /** ���ݲ����ַ��� */
    private String mOperate;

    private LGWorkSchema mLGWorkSchema = new LGWorkSchema();

    private LGWorkTraceSchema mLGWorkTraceSchema = new LGWorkTraceSchema();

    private MMap map = new MMap();

    /** ȫ�ֲ��� */
    private GlobalInput mGlobalInput = new GlobalInput();

    /** ͳһ�������� */
    private String mCurrentDate = PubFun.getCurrentDate();

    /** ͳһ����ʱ�� */
    private String mCurrentTime = PubFun.getCurrentTime();

    public TaskBackBL()
    {
    }

    /**
     * �����ύ�Ĺ�������
     * @param: cInputData ���������
     * @param: cOperate   ���ݲ����ַ���
     * @return: boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        // ����������ݿ�����������
        mInputData = (VData) cInputData.clone();
        this.mOperate = cOperate;
        System.out.println("now in TaskBackBL submit");
        // ���ⲿ��������ݷֽ⵽����������У�׼������
        if (this.getInputData() == false)
        {
            return false;
        }
        System.out.println("---getInputData---");

        // ����ҵ���߼������ݽ��д���
        if (this.dealData() == false)
        {
            return false;
        }
        System.out.println("---dealDate---");

        // װ�䴦��õ����ݣ�׼������̨���б���
        this.prepareOutputData();
        System.out.println("---prepareOutputData---");

        PubSubmit tPubSubmit = new PubSubmit();
        System.out.println("Start TaskBackBL Submit...");

        if (!tPubSubmit.submitData(mInputData, mOperate))
        {
            // @@������
            this.mErrors.copyAllErrors(tPubSubmit.mErrors);

            CError tError = new CError();
            tError.moduleName = "TaskBackBL";
            tError.functionName = "submitData";
            tError.errorMessage = "�����ύʧ��!";

            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * ���ⲿ��������ݷֽ⵽�����������
     * @param: ��
     * @return: boolean
     */
    private boolean getInputData()
    {
        mGlobalInput.setSchema((GlobalInput) mInputData.
                getObjectByObjectName("GlobalInput", 0));
        mLGWorkSchema.setSchema((LGWorkSchema) mInputData.
                getObjectByObjectName("LGWorkSchema", 0));
        return true;
    }

    /**
     * У�鴫�������
     * @param: ��
     * @return: boolean
     */
    private boolean checkData()
    {
        return true;
    }

    /**
     * ����ҵ���߼������ݽ��д���
     * @param: ��
     * @return: boolean
     */
    private boolean dealData()
    {
        String sql;
        SSRS tSSRS = new SSRS();
        ExeSQL tExeSQL = new ExeSQL();
        String tWorkNo;
        String tNodeNo;
        String tWorkBoxNo;

        //�õ��µ���ҵ���
        tWorkNo = mLGWorkSchema.getWorkNo();
        sql = "Select Case When max(to_number(NodeNo)) Is Null " +
              "       Then 0 Else max(to_number(NodeNo))+1 End " +
              "From   LGWorkTrace " +
              "Where  WorkNo = '" + tWorkNo + "'";
        tSSRS = tExeSQL.execSQL(sql);
        tNodeNo = tSSRS.GetText(1, 1);

        //�õ��û������
        sql = "select WorkBoxNo from LGWorkBox " +
              "where  OwnerTypeNo = '2' " + //��������
              "and    OwnerNo = '" + mGlobalInput.Operator + "'";
        tSSRS = tExeSQL.execSQL(sql);
        tWorkBoxNo = tSSRS.GetText(1, 1);

        //��������
        sql = "Update LGWork set " +
              "NodeNo = '" + tNodeNo + "', " +    //ָ���½��
              "Operator = '" + mGlobalInput.Operator + "', " +
              "ModifyDate = '" + mCurrentDate + "', " +
              "ModifyTime = '" + mCurrentTime + "' " +
              "Where  WorkNo = '" + tWorkNo + "' ";
        map.put(sql, "UPDATE");  //�޸�

        mLGWorkTraceSchema.setWorkNo(tWorkNo);
        mLGWorkTraceSchema.setNodeNo(tNodeNo);
        mLGWorkTraceSchema.setWorkBoxNo(tWorkBoxNo); //������
        mLGWorkTraceSchema.setInMethodNo("3");       //�ջ�
        mLGWorkTraceSchema.setInDate(mCurrentDate);
        mLGWorkTraceSchema.setInTime(mCurrentTime);
        mLGWorkTraceSchema.setSendComNo(mGlobalInput.ManageCom);
        mLGWorkTraceSchema.setSendPersonNo(mGlobalInput.Operator);
        mLGWorkTraceSchema.setOperator(mGlobalInput.Operator);
        mLGWorkTraceSchema.setMakeDate(mCurrentDate);
        mLGWorkTraceSchema.setMakeTime(mCurrentTime);
        mLGWorkTraceSchema.setModifyDate(mCurrentDate);
        mLGWorkTraceSchema.setModifyTime(mCurrentTime);
        map.put(mLGWorkTraceSchema, "INSERT");  //����

        return true;
    }

    /**
     * ����ҵ���߼������ݽ��д���
     * @param: ��
     * @return: void
     */
    private void prepareOutputData()
    {
        mInputData.clear();
        mInputData.add(map);
        mResult.clear();
        mResult.add(mLGWorkSchema);
    }

    /**
     * �õ������Ľ����
     * @return �����
     */
    public VData getResult()
    {
        return mResult;
    }
}

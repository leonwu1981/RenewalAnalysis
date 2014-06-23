package com.sinosoft.task;

import com.sinosoft.utility.*;
import com.sinosoft.lis.bl.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;

/**
 * <p>Title: ��������ϵͳ</p>
 * <p>Description: ȡ��BL��ҵ���߼������� </p>
 * <p>Copyright: Copyright (c) 2005 </p>
 * <p>Company: Sinosoft </p>
 * @author QiuYang
 * @version 1.0
 * @dat e 2005-01-20
 */

public class TaskFatchBL
{
    /** �������� */
    public CErrors mErrors = new CErrors();

    /** �������ݵ����� */
    private VData mInputData = new VData();

    /** ������ݵ����� */
    private VData mResult = new VData();

    /** ���ݲ����ַ��� */
    private String mOperate;

    private LGWorkTraceSchema mLGWorkTraceSchema = new LGWorkTraceSchema();

    /** ȡ���� */
    private int mNum;

    private MMap map = new MMap();

    /** ȫ�ֲ��� */
    private GlobalInput mGlobalInput = new GlobalInput();

    /** ͳһ�������� */
    private String mCurrentDate = PubFun.getCurrentDate();

    /** ͳһ����ʱ�� */
    private String mCurrentTime = PubFun.getCurrentTime();

    public TaskFatchBL()
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
        System.out.println("now in TaskFatchBL submit");
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
        System.out.println("Start TaskFatchBL Submit...");

        if (!tPubSubmit.submitData(mInputData, mOperate))
        {
            // @@������
            this.mErrors.copyAllErrors(tPubSubmit.mErrors);

            CError tError = new CError();
            tError.moduleName = "TaskFatchBL";
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
        mNum = Integer.parseInt((String) mInputData.
                getObjectByObjectName("String", 0));
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
        SSRS iSSRS = new SSRS();
        ExeSQL tExeSQL = new ExeSQL();
        String tWorkNo;
        String tNodeNo;
        String tWorkBoxNo;

        sql = "Select WorkBoxNo " +
              "From   LGWorkBox " +
              "Where  OwnerTypeNo = '2' " + //��������
              "And    OwnerNo = '" + mGlobalInput.Operator + "' ";
        tSSRS = tExeSQL.execSQL(sql);
        tWorkBoxNo = tSSRS.GetText(1, 1);

        sql = "Select w.WorkNo " +
              "From   LGWork w, LGWorkTrace t " +
              "Where  w.WorkNo = t.WorkNo " +
              "And    w.NodeNo = t.NodeNo " +
              "And    t.WorkBoxNo = " +
              "       (Select WorkBoxNo from LGWorkBox " +
              "        Where  OwnerTypeNo = '1' " +  //С������
              "        And    OwnerNo = " +
              "               (select GroupNo from LGGroupMember " +
              "                where  MemberNo = '" + mGlobalInput.Operator + "'" +
              "                ) " +
              "        ) " +
              "Order By w.WorkNo desc ";
        tSSRS = tExeSQL.execSQL(sql);

        for (int i = 0; i < mNum; i++)
        {
            tWorkNo = tSSRS.GetText(i + 1, 1);

            //�õ��µ���ҵ���
            sql = "Select Case When max(to_number(NodeNo)) Is Null " +
                  "       Then 0 Else max(to_number(NodeNo))+1 End " +
                  "From   LGWorkTrace " +
                  "Where  WorkNo = '" + tWorkNo + "'";
            iSSRS = tExeSQL.execSQL(sql);
            tNodeNo = iSSRS.GetText(1, 1);

            LGWorkTraceSchema tLGWorkTraceSchema = new LGWorkTraceSchema();
            tLGWorkTraceSchema.setWorkNo(tWorkNo);
            tLGWorkTraceSchema.setNodeNo(tNodeNo);
            tLGWorkTraceSchema.setWorkBoxNo(tWorkBoxNo);
            tLGWorkTraceSchema.setInMethodNo("1"); //ȡ��
            tLGWorkTraceSchema.setInDate(mCurrentDate);
            tLGWorkTraceSchema.setInTime(mCurrentTime);
            tLGWorkTraceSchema.setSendComNo(mGlobalInput.ManageCom);
            tLGWorkTraceSchema.setSendPersonNo(mGlobalInput.Operator);
            tLGWorkTraceSchema.setOperator(mGlobalInput.Operator);
            tLGWorkTraceSchema.setMakeDate(mCurrentDate);
            tLGWorkTraceSchema.setMakeTime(mCurrentTime);
            tLGWorkTraceSchema.setModifyDate(mCurrentDate);
            tLGWorkTraceSchema.setModifyTime(mCurrentTime);
            map.put(tLGWorkTraceSchema, "INSERT"); //����

            //��������
            sql = "Update LGWork set " +
                  "NodeNo = '" + tNodeNo + "', " +
                  "Operator = '" + mGlobalInput.Operator + "', " +
                  "ModifyDate = '" + mCurrentDate + "', " +
                  "ModifyTime = '" + mCurrentTime + "' " +
                  "Where  WorkNo = '" + tWorkNo + "' ";
            map.put(sql, "UPDATE"); //�޸�
        }

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

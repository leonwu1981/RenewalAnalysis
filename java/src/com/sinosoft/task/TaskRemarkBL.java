package com.sinosoft.task;

import com.sinosoft.utility.*;
import com.sinosoft.lis.bl.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;

/**
 * <p>Title: ��������ϵͳ</p>
 * <p>Description: ����ת��BL��ҵ���߼������� </p>
 * <p>Copyright: Copyright (c) 2005 </p>
 * <p>Company: Sinosoft </p>
 * @author QiuYang
 * @version 1.0
 * @date 2005-01-20
 */

public class TaskRemarkBL
{
    /** �������� */
    public CErrors mErrors = new CErrors();

    /** �������ݵ����� */
    private VData mInputData = new VData();

    /** ������ݵ����� */
    private VData mResult = new VData();

    /** ���ݲ����ַ��� */
    private String mOperate;

    private String[] mWorkNo;

    private LGWorkRemarkSchema mLGWorkRemarkSchema = new LGWorkRemarkSchema();

    private MMap map = new MMap();

    /** ȫ�ֲ��� */
    private GlobalInput mGlobalInput = new GlobalInput();

    /** ͳһ�������� */
    private String mCurrentDate = PubFun.getCurrentDate();

    /** ͳһ����ʱ�� */
    private String mCurrentTime = PubFun.getCurrentTime();

    public TaskRemarkBL()
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
        System.out.println("now in TaskDeliverBL submit");
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
        System.out.println("Start tPRnewManualDunBLS Submit...");

        if (!tPubSubmit.submitData(mInputData, mOperate))
        {
            // @@������
            this.mErrors.copyAllErrors(tPubSubmit.mErrors);

            CError tError = new CError();
            tError.moduleName = "TaskDeliverBL";
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
        mWorkNo = (String[]) mInputData.getObject(1);
        mLGWorkRemarkSchema.setSchema((LGWorkRemarkSchema) mInputData.
                getObjectByObjectName("LGWorkRemarkSchema", 0));
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
        String tRemarkNo;

        //�õ���ǰ��ҵ���
        for (int i = 0; i < mWorkNo.length; i++)
        {
            tWorkNo = mWorkNo[i];
            sql = "Select NodeNo " +
                  "From   LGWork " +
                  "Where  WorkNo = '" + tWorkNo + "' ";
            tSSRS = tExeSQL.execSQL(sql);
            tNodeNo = tSSRS.GetText(1, 1);

            //�õ���ע���
            sql = "Select Case When max(to_number(RemarkNo)) Is Null " +
                  "       Then 0 Else max(to_number(RemarkNo))+1 End " +
                  "From   LGWorkRemark " +
                  "Where  WorkNo = '" + tWorkNo + "'" +
                  "And    NodeNo = '" + tNodeNo + "'";
            tSSRS = tExeSQL.execSQL(sql);
            tRemarkNo = tSSRS.GetText(1, 1);

            LGWorkRemarkSchema tLGWorkRemarkSchema = new LGWorkRemarkSchema();
            tLGWorkRemarkSchema.setWorkNo(tWorkNo);
            tLGWorkRemarkSchema.setNodeNo(tNodeNo);
            tLGWorkRemarkSchema.setRemarkNo(tRemarkNo);
            tLGWorkRemarkSchema.setRemarkTypeNo("2"); //��ע����Ϊ��ע
            tLGWorkRemarkSchema.setRemarkContent(mLGWorkRemarkSchema.getRemarkContent());
            tLGWorkRemarkSchema.setRemarkDate(mCurrentDate);
            tLGWorkRemarkSchema.setRemarkTime(mCurrentTime);
            tLGWorkRemarkSchema.setOperator(mGlobalInput.Operator);
            tLGWorkRemarkSchema.setMakeDate(mCurrentDate);
            tLGWorkRemarkSchema.setMakeTime(mCurrentTime);
            tLGWorkRemarkSchema.setModifyDate(mCurrentDate);
            tLGWorkRemarkSchema.setModifyTime(mCurrentTime);
            map.put(tLGWorkRemarkSchema, "INSERT"); //����
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

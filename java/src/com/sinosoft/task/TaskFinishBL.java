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

public class TaskFinishBL
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

    private LGWorkRemarkSchema mLGWorkRemarkSchema = new LGWorkRemarkSchema();

    private MMap map = new MMap();

    /** ȫ�ֲ��� */
    private GlobalInput mGlobalInput = new GlobalInput();

    /** ͳһ�������� */
    private String mCurrentDate = PubFun.getCurrentDate();

    /** ͳһ����ʱ�� */
    private String mCurrentTime = PubFun.getCurrentTime();

    public TaskFinishBL()
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
        mLGWorkSchema.setSchema((LGWorkSchema) mInputData.
                getObjectByObjectName("LGWorkSchema", 0));
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

        //�õ���ҵ��ǰ���
        tWorkNo = mLGWorkSchema.getWorkNo();
        sql = "Select NodeNo " +
              "From   LGWork " +
              "Where  WorkNo = '" + tWorkNo + "'";
        tSSRS = tExeSQL.execSQL(sql);
        tNodeNo = tSSRS.GetText(1, 1);

        //�õ���ע���
        sql = "Select Case When max(to_number(RemarkNo)) Is Null " +
              "       Then 0 Else max(to_number(RemarkNo))+1 End " +
              "From   LGWorkTrace " +
              "Where  WorkNo = '" + tWorkNo + "'" +
              "And    NodeNo = '" + tNodeNo + "'";
        tSSRS = tExeSQL.execSQL(sql);
        tRemarkNo = tSSRS.GetText(1, 1);

        //��������
        mLGWorkSchema.setStatusNo("5");   //��Ϊ�᰸״̬
        mLGWorkSchema.setOperator(mGlobalInput.Operator);
        mLGWorkSchema.setModifyDate(mCurrentDate);
        mLGWorkSchema.setModifyTime(mCurrentTime);
        map.put(mLGWorkSchema, "UPDATE");  //�޸�

        mLGWorkRemarkSchema.setWorkNo(tWorkNo);
        mLGWorkRemarkSchema.setNodeNo(tNodeNo);
        mLGWorkRemarkSchema.setRemarkNo(tRemarkNo);
        mLGWorkRemarkSchema.setRemarkTypeNo("1");  //��ע����Ϊ�᰸
        mLGWorkRemarkSchema.setRemarkDate(mCurrentDate);
        mLGWorkRemarkSchema.setRemarkTime(mCurrentTime);
        mLGWorkRemarkSchema.setOperator(mGlobalInput.Operator);
        mLGWorkRemarkSchema.setMakeDate(mCurrentDate);
        mLGWorkRemarkSchema.setMakeTime(mCurrentTime);
        mLGWorkRemarkSchema.setModifyDate(mCurrentDate);
        mLGWorkRemarkSchema.setModifyTime(mCurrentTime);
        map.put(mLGWorkRemarkSchema, "INSERT");  //����

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

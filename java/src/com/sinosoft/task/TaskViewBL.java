package com.sinosoft.task;

import com.sinosoft.utility.*;
import com.sinosoft.lis.bl.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;

/**
 * <p>Title: ��������ϵͳ</p>
 * <p>Description: �����鿴BL��ҵ���߼������� </p>
 * <p>Copyright: Copyright (c) 2005 </p>
 * <p>Company: Sinosoft </p>
 * @author QiuYang
 * @version 1.0
 * @date 2005-01-19
 */

public class TaskViewBL
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

    private MMap map = new MMap();

    /** ͳһ�������� */
    private String mCurrentDate = PubFun.getCurrentDate();

    /** ͳһ����ʱ�� */
    private String mCurrentTime = PubFun.getCurrentTime();

    public TaskViewBL()
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
        System.out.println("now in TaskViewBL submit");
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
            tError.moduleName = "TaskViewBL";
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
        //�õ������ź������,�����ź��������ͬ

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

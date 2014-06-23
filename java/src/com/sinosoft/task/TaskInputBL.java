package com.sinosoft.task;

import com.sinosoft.utility.*;
import com.sinosoft.lis.bl.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.bq.*;

/**
 * <p>Title: ��������ϵͳ</p>
 * <p>Description: ����¼��BL��ҵ���߼������� </p>
 * <p>Copyright: Copyright (c) 2005 </p>
 * <p>Company: Sinosoft </p>
 * @author QiuYang
 * @version 1.0
 * @date 2005-01-17
 */

public class TaskInputBL
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

    private LPEdorAppSchema mLPEdorAppSchema = new LPEdorAppSchema();

    private MMap map = new MMap();

    /** ȫ�ֲ��� */
    private GlobalInput mGlobalInput = new GlobalInput();

    /** ͳһ�������� */
    private String mCurrentDate = PubFun.getCurrentDate();

    /** ͳһ����ʱ�� */
    private String mCurrentTime = PubFun.getCurrentTime();

    public TaskInputBL()
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
        System.out.println("now in TaskInputBL submit");
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
        System.out.println("Start TaskInputBL Submit...");

        if (!tPubSubmit.submitData(mInputData, mOperate))
        {
            // @@������
            this.mErrors.copyAllErrors(tPubSubmit.mErrors);

            CError tError = new CError();
            tError.moduleName = "TaskInputBL";
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
        String tWorkBoxNo;
        String tAcceptorNo = mGlobalInput.Operator;
        String tAcceptCom;

        //�õ��������
        sql = "select GroupNo from LGGroupMember " +
              "where  MemberNo = '" + tAcceptorNo + "' ";
        tSSRS = tExeSQL.execSQL(sql);
        tAcceptCom = tSSRS.GetText(1, 1);

        //������ҵ�ź������,��ҵ�ź��������ͬ,��Ϊ���ڼ�����ˮ��
        String tDate = mCurrentDate;
        tDate = tDate.substring(0, 4) + tDate.substring(5, 7) + tDate.substring(8, 10);
        System.out.println(tDate);
        tWorkNo = tDate + PubFun1.CreateMaxNo("TASK" + tDate, 6);

        //�õ��û������
        sql = "select WorkBoxNo from LGWorkBox " +
              "where  OwnerTypeNo = '2' " + //��������
              "and    OwnerNo = '" + mGlobalInput.Operator + "'";
        tSSRS = tExeSQL.execSQL(sql);
        tWorkBoxNo = tSSRS.GetText(1, 1);

        //��������
        mLGWorkSchema.setWorkNo(tWorkNo);
        mLGWorkSchema.setAcceptNo(tWorkNo);
        mLGWorkSchema.setNodeNo("0"); //��ǰ����ʼ���
        mLGWorkSchema.setStatusNo("2"); //����Ϊδ����״̬
        mLGWorkSchema.setAcceptorNo(tAcceptorNo);
        mLGWorkSchema.setAcceptCom(tAcceptCom);
        mLGWorkSchema.setMakeDate(mCurrentDate);
        mLGWorkSchema.setMakeTime(mCurrentTime);
        mLGWorkSchema.setModifyDate(mCurrentDate);
        mLGWorkSchema.setModifyTime(mCurrentTime);
        mLGWorkSchema.setOperator(mGlobalInput.Operator);

        mLGWorkTraceSchema.setWorkNo(tWorkNo);
        mLGWorkTraceSchema.setNodeNo("0"); //��ʼ���
        mLGWorkTraceSchema.setWorkBoxNo(tWorkBoxNo); //������
        mLGWorkTraceSchema.setInMethodNo("0"); //�µ�¼��
        mLGWorkTraceSchema.setInDate(mCurrentDate);
        mLGWorkTraceSchema.setInTime(mCurrentTime);
        mLGWorkTraceSchema.setSendComNo("");
        mLGWorkTraceSchema.setSendPersonNo("");
        mLGWorkTraceSchema.setMakeDate(mCurrentDate);
        mLGWorkTraceSchema.setMakeTime(mCurrentTime);
        mLGWorkTraceSchema.setModifyDate(mCurrentDate);
        mLGWorkTraceSchema.setModifyTime(mCurrentTime);
        mLGWorkTraceSchema.setOperator(mGlobalInput.Operator);

        //¼��ҵ����Ϣ
        String tTypeNo = mLGWorkSchema.getTypeNo();
        String tTopTypeNo = tTypeNo.substring(0, 2);
        if (tTopTypeNo.equals("01")) //��ѯ
        {
        }
        else if (tTopTypeNo.equals("02")) //Ͷ��
        {
        }
        else if (tTopTypeNo.equals("03")) //��ȫ
        {
            String strLimit = PubFun.getNoLimit(mGlobalInput.ManageCom);
            String strEdorAcceptNo = PubFun1.CreateMaxNo("EdorAcceptNo",
                    strLimit);
            if (StrTool.compareString(strEdorAcceptNo, ""))
            {
                CError.buildErr(this, "���ɱ�ȫ����Ŵ���");
                return false;
            }
            else
            {
                mLPEdorAppSchema.setEdorAcceptNo(tWorkNo);
            }
            mLPEdorAppSchema.setOtherNo(mLGWorkSchema.getCustomerNo());      //�������
            mLPEdorAppSchema.setOtherNoType(mLGWorkSchema.getApplyTypeNo()); //�����������
            mLPEdorAppSchema.setEdorAppName(mLGWorkSchema.getApplyName());   //����������
            mLPEdorAppSchema.setAppType(mLGWorkSchema.getAcceptWayNo());     //���뷽ʽ
            mLPEdorAppSchema.setEdorAppDate(mLGWorkSchema.getAcceptDate());  //��������
            mLPEdorAppSchema.setEdorState("1");
            mLPEdorAppSchema.setOperator(mGlobalInput.Operator);
            mLPEdorAppSchema.setManageCom(mGlobalInput.ManageCom);
            mLPEdorAppSchema.setMakeDate(mCurrentDate);
            mLPEdorAppSchema.setMakeTime(mCurrentTime);
            mLPEdorAppSchema.setModifyDate(mCurrentDate);
            mLPEdorAppSchema.setModifyTime(mCurrentTime);
            map.put(mLPEdorAppSchema, "INSERT"); //����

            mLGWorkSchema.setDetailWorkNo(tWorkNo);
        }
        else if (tTopTypeNo.equals("04")) //����
        {
        }

        map.put(mLGWorkSchema, "INSERT"); //����
        map.put(mLGWorkTraceSchema, "INSERT"); //����
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
        mResult.add(mLPEdorAppSchema);
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

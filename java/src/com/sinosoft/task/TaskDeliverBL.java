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

public class TaskDeliverBL
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

    private LGWorkRemarkSchema mLGWorkRemarkSchema = new LGWorkRemarkSchema();

    private MMap map = new MMap();

    private String[] mWorkNo = null;

    private String mDeliverType;

    private String mCopyFlag;

    /** ȫ�ֲ��� */
    private GlobalInput mGlobalInput = new GlobalInput();

    /** ͳһ�������� */
    private String mCurrentDate = PubFun.getCurrentDate();

    /** ͳһ����ʱ�� */
    private String mCurrentTime = PubFun.getCurrentTime();

    public TaskDeliverBL()
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
        mCopyFlag = (String) mInputData.getObject(2);
        mDeliverType = (String) mInputData.getObject(3);
        mLGWorkSchema.setSchema((LGWorkSchema) mInputData.
                getObjectByObjectName("LGWorkSchema", 0));
        mLGWorkTraceSchema.setSchema((LGWorkTraceSchema) mInputData.
                getObjectByObjectName("LGWorkTraceSchema", 0));
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
        String tRemarkTypeNo;
        String tTypeNo;
        String tWorkBoxNo;
        String tRemarkContent;
        tTypeNo = mLGWorkSchema.getTypeNo();
        tWorkBoxNo = mLGWorkTraceSchema.getWorkBoxNo();
        tRemarkContent = mLGWorkRemarkSchema.getRemarkContent();

        for (int i = 0; i < mWorkNo.length; i++)
        {
            tWorkNo = mWorkNo[i];
            LGWorkSchema tLGWorkSchema = new LGWorkSchema();
            LGWorkTraceSchema tLGWorkTraceSchema = new LGWorkTraceSchema();
            LGWorkRemarkSchema tLGWorkRemarkSchema = new LGWorkRemarkSchema();
            if ((mCopyFlag != null) && (mCopyFlag.equals("Y"))) //����
            {
                sql = "Select * From LGWork " +
                      "Where WorkNo = '" + tWorkNo + "' ";
                LGWorkDB tLGWorkDB = new LGWorkDB();
                tLGWorkDB.setWorkNo(tWorkNo);
                tLGWorkDB.getInfo();
                tLGWorkSchema.setSchema(tLGWorkDB.getSchema());

                //������ҵ��
                String tDate = mCurrentDate;
                tDate = tDate.substring(0, 4) + tDate.substring(5, 7) + tDate.substring(8, 10);
                tWorkNo = PubFun1.CreateMaxNo("TASK" + tDate, 6);

                //������
                if ((mDeliverType != null) && (mDeliverType.equals("1")))
                {
                    tLGWorkSchema.setStatusNo("4");
                    tRemarkTypeNo = "3";
                }
                else
                {
                    tRemarkTypeNo = "0";
                }
                tLGWorkSchema.setWorkNo(tWorkNo);
                tLGWorkSchema.setNodeNo("0");
                tLGWorkSchema.setTypeNo(tTypeNo);
                tLGWorkSchema.setMakeDate(mCurrentDate);
                tLGWorkSchema.setMakeTime(mCurrentTime);
                tLGWorkSchema.setModifyDate(mCurrentDate);
                tLGWorkSchema.setModifyTime(mCurrentTime);
                tLGWorkSchema.setOperator(mGlobalInput.Operator);
                map.put(tLGWorkSchema, "INSERT"); //����

                tLGWorkTraceSchema.setWorkNo(tWorkNo);
                tLGWorkTraceSchema.setNodeNo("0"); //��ʼ���
                tLGWorkTraceSchema.setWorkBoxNo(tWorkBoxNo); //������
                tLGWorkTraceSchema.setInMethodNo("0"); //�µ�¼��
                tLGWorkTraceSchema.setInDate(mCurrentDate);
                tLGWorkTraceSchema.setInTime(mCurrentTime);
                tLGWorkTraceSchema.setSendComNo("");
                tLGWorkTraceSchema.setSendPersonNo("");
                tLGWorkTraceSchema.setMakeDate(mCurrentDate);
                tLGWorkTraceSchema.setMakeTime(mCurrentTime);
                tLGWorkTraceSchema.setModifyDate(mCurrentDate);
                tLGWorkTraceSchema.setModifyTime(mCurrentTime);
                tLGWorkTraceSchema.setOperator(mGlobalInput.Operator);
                map.put(tLGWorkTraceSchema, "INSERT"); //����

                tLGWorkRemarkSchema.setWorkNo(tWorkNo);
                tLGWorkRemarkSchema.setNodeNo("0");
                tLGWorkRemarkSchema.setRemarkNo("0");
                tLGWorkRemarkSchema.setRemarkTypeNo(tRemarkTypeNo); //��ע����Ϊת��
                tLGWorkRemarkSchema.setRemarkContent(tRemarkContent);
                tLGWorkRemarkSchema.setRemarkDate(mCurrentDate);
                tLGWorkRemarkSchema.setRemarkTime(mCurrentTime);
                tLGWorkRemarkSchema.setOperator(mGlobalInput.Operator);
                tLGWorkRemarkSchema.setMakeDate(mCurrentDate);
                tLGWorkRemarkSchema.setMakeTime(mCurrentTime);
                tLGWorkRemarkSchema.setModifyDate(mCurrentDate);
                tLGWorkRemarkSchema.setModifyTime(mCurrentTime);
                map.put(tLGWorkRemarkSchema, "INSERT"); //����
            }
            else //������
            {
                //�õ��µ���ҵ���
                sql = "Select Case When max(to_number(NodeNo)) Is Null " +
                      "       Then 0 Else max(to_number(NodeNo))+1 End " +
                      "From   LGWorkTrace " +
                      "Where  WorkNo = '" + tWorkNo + "' ";
                tSSRS = tExeSQL.execSQL(sql);
                tNodeNo = tSSRS.GetText(1, 1);

                //�õ���ע���
                sql = "Select Case When max(to_number(RemarkNo)) Is Null " +
                      "       Then 0 Else max(to_number(RemarkNo))+1 End " +
                      "From   LGWorkRemark " +
                      "Where  WorkNo = '" + tWorkNo + "' " +
                      "And    NodeNo = '" + tNodeNo + "' ";
                tSSRS = tExeSQL.execSQL(sql);
                tRemarkNo = tSSRS.GetText(1, 1);

                //��������
                if ((mDeliverType != null) && (mDeliverType.equals("1")))
                {
                    sql = "Update LGWork set " +
                          "NodeNo = '" + tNodeNo + "', " +
                          "StatusNo = '4', " + //��Ϊ���״̬
                          "Operator = '" + mGlobalInput.Operator + "', " +
                          "ModifyDate = '" + mCurrentDate + "', " +
                          "ModifyTime = '" + mCurrentTime + "' " +
                          "Where  WorkNo = '" + tWorkNo + "' ";
                    tRemarkTypeNo = "3"; //���
                }
                else
                {
                    sql = "Update LGWork set " +
                          "NodeNo = '" + tNodeNo + "', " +
                          "Operator = '" + mGlobalInput.Operator + "', " +
                          "ModifyDate = '" + mCurrentDate + "', " +
                          "ModifyTime = '" + mCurrentTime + "' " +
                          "Where  WorkNo = '" + tWorkNo + "' ";
                    tRemarkTypeNo = "0"; //ת��
                }
                map.put(sql, "UPDATE"); //�޸�

                tLGWorkTraceSchema.setWorkNo(tWorkNo);
                tLGWorkTraceSchema.setNodeNo(tNodeNo);
                tLGWorkTraceSchema.setWorkBoxNo(tWorkBoxNo);
                tLGWorkTraceSchema.setInMethodNo("2"); //����
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

                tLGWorkRemarkSchema.setWorkNo(tWorkNo);
                tLGWorkRemarkSchema.setNodeNo(tNodeNo);
                tLGWorkRemarkSchema.setRemarkNo(tRemarkNo);
                tLGWorkRemarkSchema.setRemarkTypeNo(tRemarkTypeNo); //��ע����Ϊת��
                tLGWorkRemarkSchema.setRemarkDate(mCurrentDate);
                tLGWorkRemarkSchema.setRemarkTime(mCurrentTime);
                tLGWorkRemarkSchema.setOperator(mGlobalInput.Operator);
                tLGWorkRemarkSchema.setMakeDate(mCurrentDate);
                tLGWorkRemarkSchema.setMakeTime(mCurrentTime);
                tLGWorkRemarkSchema.setModifyDate(mCurrentDate);
                tLGWorkRemarkSchema.setModifyTime(mCurrentTime);
                map.put(tLGWorkRemarkSchema, "INSERT"); //����
            }
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

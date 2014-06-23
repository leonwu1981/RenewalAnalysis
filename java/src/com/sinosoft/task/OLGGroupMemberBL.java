/*
 * <p>ClassName: OLGGroupMemberBL </p>
 * <p>Description: OLGGroupMemberBL���ļ� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: testcompany </p>
 * @Database:
 * @CreateDate��2005-02-23 10:20:45
 */
package com.sinosoft.task;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.vdb.*;
import com.sinosoft.lis.sys.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.bl.*;
import com.sinosoft.lis.vbl.*;
import com.sinosoft.utility.*;
import com.sinosoft.lis.pubfun.*;

public class OLGGroupMemberBL
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    private VData mResult = new VData();

    /** �����洫�����ݵ����� */
    private VData mInputData = new VData();

    /** ȫ������ */
    private GlobalInput mGlobalInput = new GlobalInput();
    private MMap map = new MMap();

    /** ͳһ�������� */
    private String mCurrentDate = PubFun.getCurrentDate();

    /** ͳһ����ʱ�� */
    private String mCurrentTime = PubFun.getCurrentTime();

    /** ���ݲ����ַ��� */
    private String mOperate;

    /** ҵ������ر��� */
    private LGGroupMemberSchema mLGGroupMemberSchema = new LGGroupMemberSchema();

//private LGGroupMemberSet mLGGroupMemberSet=new LGGroupMemberSet();
    public OLGGroupMemberBL()
    {
    }

    public static void main(String[] args)
    {
    }


    /**
     * �������ݵĹ�������
     * @param: cInputData ���������
     *         cOperate ���ݲ���
     * @return:
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //���������ݿ�����������
        this.mOperate = cOperate;

        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData(cInputData))
            return false;

        //����ҵ����
        if (!dealData())
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "OLGGroupMemberBL";
            tError.functionName = "submitData";
            tError.errorMessage = "���ݴ���ʧ��OLGGroupMemberBL-->dealData!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //׼������̨������
        if (!prepareOutputData())
            return false;

        if (this.mOperate.equals("QUERY||MAIN"))
        {
            this.submitquery();
        }
        else
        {
            PubSubmit tPubSubmit = new PubSubmit();
            if (!tPubSubmit.submitData(mInputData, mOperate))
            {
                // @@������
                this.mErrors.copyAllErrors(tPubSubmit.mErrors);
                CError tError = new CError();
                tError.moduleName = "OLGGroupMemberBL";
                tError.functionName = "submitData";
                tError.errorMessage = "�����ύʧ��!";

                this.mErrors.addOneError(tError);
                return false;
            }
        }
        mInputData = null;
        return true;
    }


    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {
        if (mOperate.equals("INSERT||MAIN"))
        {
            map.put(mLGGroupMemberSchema, "INSERT"); //����
        }
        if (mOperate.equals("UPDATE||MAIN"))
        {
            String sql = "Update LGGroupMember set " +
                         "MemberNo = '" + mLGGroupMemberSchema.getMemberNo() +
                         "' " +
              /*          "', " +
                         "Operator = '" + mGlobalInput.Operator + "', " +
                         "ModifyDate = '" + mCurrentDate + "', " +
                         "ModifyTime = '" + mCurrentTime + "' " +
               */
                         "Where  GroupNo = '" + mLGGroupMemberSchema.getGroupNo() +
                         "' ";
            map.put(sql, "UPDATE"); //�޸�
        }
        if (this.mOperate.equals("DELETE||MAIN"))
        {
            map.put(mLGGroupMemberSchema, "DELETE"); //ɾ��
        }

        return true;
    }


    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean updateData()
    {
        return true;
    }


    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean deleteData()
    {
        return true;
    }


    /**
     * �����������еõ����ж���
     *��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean getInputData(VData cInputData)
    {
        this.mLGGroupMemberSchema.setSchema((LGGroupMemberSchema) cInputData.
                                            getObjectByObjectName(
                "LGGroupMemberSchema", 0));
        this.mGlobalInput.setSchema((GlobalInput) cInputData.
                                    getObjectByObjectName("GlobalInput", 0));
        return true;
    }


    /**
     * ׼��������������Ҫ������
     * ��������׼������ʱ���������򷵻�false,���򷵻�true
     */
    private boolean submitquery()
    {
        this.mResult.clear();
        LGGroupMemberDB tLGGroupMemberDB = new LGGroupMemberDB();
        tLGGroupMemberDB.setSchema(this.mLGGroupMemberSchema);
        //�������Ҫ����Ĵ����򷵻�
        if (tLGGroupMemberDB.mErrors.needDealError())
        {
            // @@������
            this.mErrors.copyAllErrors(tLGGroupMemberDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "LGGroupMemberBL";
            tError.functionName = "submitData";
            tError.errorMessage = "�����ύʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mInputData = null;
        return true;
    }

    private boolean prepareOutputData()
    {
        try
        {
            this.mInputData.clear();
            this.mInputData.add(this.mLGGroupMemberSchema);
            mInputData.add(this.map);
            mResult.clear();
            mResult.add(this.mLGGroupMemberSchema);
        }
        catch (Exception ex)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "LGGroupMemberBL";
            tError.functionName = "prepareData";
            tError.errorMessage = "��׼������㴦������Ҫ������ʱ����";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    public VData getResult()
    {
        return this.mResult;
    }
}

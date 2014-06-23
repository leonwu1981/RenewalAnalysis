/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import com.sinosoft.lis.db.LBMissionDB;
import com.sinosoft.lis.db.LWMissionDB;
import com.sinosoft.lis.pubfun.CalBase;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.pubfun.PubSubmit;
import com.sinosoft.lis.schema.LWMissionSchema;
import com.sinosoft.lis.vschema.LBMissionSet;
import com.sinosoft.lis.vschema.LWMissionSet;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.Reflections;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;
import com.sinosoft.workflowengine.ActivityOperator;


/**
 * <p>Title: Webҵ��ϵͳ�б����˵��Զ��˱�����</p>
 * <p>Description: �߼�������</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author WHN
 * @version 1.0
 */
public class CircReportApplyBL
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** �����洫�����ݵ����� */
    private VData mInputData;
    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
    /** ���ݲ����ַ��� */
    private String mOperate;
    private String mManageCom;
    private String mOperater;
    private String mStatYear; //ͨ�����
    private String mStatMonth = "";

    /**�����˹��˱�����������*/
    private VData tWorkFlowVData = new VData();
    private TransferData tWorkFlowTransferData = new TransferData();
    private LWMissionSet mLWMissionSet = new LWMissionSet();
    private LWMissionSchema mLWMissionSchema = new LWMissionSchema();
    /** ȫ������ */
    private GlobalInput mGlobalInput = new GlobalInput();
    private Reflections mReflections = new Reflections();
    private String CurrentDate = PubFun.getCurrentDate();
    private String CurrentTime = PubFun.getCurrentTime();
    private TransferData mTransferData = new TransferData();

    private CalBase mCalBase = new CalBase();

    public CircReportApplyBL()
    {}

    /**
     * �������ݵĹ�������
     * @param: cInputData ���������
     *         cOperate ���ݲ���
     * @return:
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        int flag = 0; //�ж��ǲ����������ݶ����ɹ�
        int j = 0; //�����������ݸ���

        //���������ݿ�����������
        mInputData = (VData) cInputData.clone();

        System.out.println("---1---");
        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData(cInputData))
        {
            return false;
        }

        if (!CheckData())
        {
            return false;
        }

        if (!dealData())
        {
            return false;
        }
        //׼������̨������
        if (!prepareOutputData())
        {
            return false;
        }

        //�����ύ
        PubSubmit tPubSubmit = new PubSubmit();
        System.out.println("Start CircReportApplyBL Submit...");
        if (!tPubSubmit.submitData(mInputData, mOperate))
        {
            // @@������
            this.mErrors.copyAllErrors(tPubSubmit.mErrors);
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "submitData";
            tError.errorMessage = "�����ύʧ��!";
            //this.mErrors .addOneError(tError) ;
            return false;
        }
        System.out.println("---CircReportApplyBL commitData---");
        return true;
    }

    /**
     * ���ݲ�����ҵ����
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {
        if (!prepareWorkFlowData())
        {
            return false;
        }
        return true;
    }

    /**
     * ���ݲ�����ҵ����
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean CheckData()
    {
        LWMissionDB tLWMissionDB = new LWMissionDB();
        LWMissionSet tLWMissionSet = new LWMissionSet();
        String tStr = "Select * from LWMission where ProcessID = '0000000002'"
                      + "and MissionProp2 = '" + mStatYear + "'"
                      + "and MissionProp3 = '" + mStatMonth + "'";
        tLWMissionSet = tLWMissionDB.executeQuery(tStr);
        if (tLWMissionSet == null)
        {
            // @@������
            this.mErrors.copyAllErrors(tLWMissionSet.mErrors);
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "prepareMission";
            tError.errorMessage = "��ѯ����ᱨ����������ڵ�ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (tLWMissionSet.size() > 0)
        {
            // @@������
            this.mErrors.copyAllErrors(tLWMissionSet.mErrors);
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "prepareMission";
            tError.errorMessage = "������" + mStatYear + "��" + mStatMonth +
                                  "�µı���ᱨ�����ڴ���״̬�������ٴ�����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        LBMissionDB tLBMissionDB = new LBMissionDB();
        LBMissionSet tLBMissionSet = new LBMissionSet();
        tStr = "Select * from LBMission where ProcessID = '0000000002'"
               + "and MissionProp2 = '" + mStatYear + "'"
               + "and MissionProp3 = '" + mStatMonth + "'";
        tLBMissionSet = tLBMissionDB.executeQuery(tStr);
        if (tLBMissionSet == null)
        {
            // @@������
            this.mErrors.copyAllErrors(tLWMissionSet.mErrors);
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "prepareMission";
            tError.errorMessage = "��ѯ����ᱨ��������������ڵ�ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (tLWMissionSet.size() == 0 && tLBMissionSet.size() > 0)
        {
            // @@������
            this.mErrors.copyAllErrors(tLWMissionSet.mErrors);
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "prepareMission";
            tError.errorMessage = "������" + mStatYear + "��" + mStatMonth +
                                  "�µı���ᱨ���Ѵ������״̬�������ٴ�����!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }


    private boolean getInputData(VData cInputData)
    {
        //�����������еõ����ж���
        //���ȫ�ֹ�������
        mGlobalInput = ((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��ò���Ա����
        mOperater = mGlobalInput.Operator;
        if (mOperater == null || mOperater.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�½��������
        mManageCom = mGlobalInput.ManageCom;
        if (mManageCom == null || mManageCom.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //���ҵ������
        if (mTransferData == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mStatYear = (String) mTransferData.getValueByName("StatYear");
        if (mStatYear == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������StatYearʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mStatMonth = (String) mTransferData.getValueByName("StatMonth");
        if (mStatMonth == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������StatMonthʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * Ϊ�Զ��˱���,������˹��˱��ı�ȫ׼��ת�빤����������
     */
    private boolean prepareWorkFlowData()
    {

        // ׼�����乤�������� VData

        tWorkFlowTransferData = new TransferData();
        tWorkFlowTransferData.setNameAndValue("StatYear", mStatYear);
        tWorkFlowTransferData.setNameAndValue("StatMon", mStatMonth);

        tWorkFlowVData = new VData();
        tWorkFlowVData.add(mGlobalInput);
        tWorkFlowVData.add(tWorkFlowTransferData);
        System.out.println("ok-prepareWorkFlowData");

        ActivityOperator tActivityOperator = new ActivityOperator();
        //ִ�з������֪ͨ����������(������������ִ������Ϊͬһ����ʱ,����ִ����������ģʽ����)
        try
        {
            //����ִ���귢�����֪ͨ����������һ��ӡ���֪ͨ������ڵ�
            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
            tLWMissionSchema = tActivityOperator.CreateStartMission(
                    "0000000002", tWorkFlowVData);
            if (tLWMissionSchema != null)
            {
                mLWMissionSet.add(tLWMissionSchema);
            }
            else
            {
                return false;
            }
        }
        catch (Exception ex)
        {
            // @@������
            //this.mErrors.copyAllErrors( mActivityOperator.mErrors );
            CError tError = new CError();
            tError.moduleName = "CircReportApplyBL";
            tError.functionName = "CreateStartMission";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;

    }


    /**
     * ׼����Ҫ���������
     */
    private boolean prepareOutputData()
    {
        mInputData = new VData();
        MMap tMMap = new MMap();
        if (mLWMissionSet != null && mLWMissionSet.size() > 0)
        {
            tMMap.put(mLWMissionSet, "INSERT");
            mInputData.add(tMMap);

            return true;

        }

        return false;

    }

}

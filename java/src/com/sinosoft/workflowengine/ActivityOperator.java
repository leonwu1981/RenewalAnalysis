/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflowengine;

import java.util.Vector;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;


/**
 * <p>Title: </p>
 * <p>Description:��������������ӿ��� </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author sxy
 * @version 1.0
 */
public class ActivityOperator
{

    /** �������ݵ����� */
//    private VData mInputData = new VData();

    /** �������ݵ����� */
    private VData mResult = new VData();

    /** ���ݲ����ַ��� */
//    private String mOperate;

    /** �������� */
    public CErrors mErrors = new CErrors();


    /** ҵ������ر��� */
    /** ȫ������ */
//    private GlobalInput mGlobalInput = new GlobalInput();
    private Reflections mReflections = new Reflections();


    //public  LWProcessSchema mLWProcessSchema=new LWProcessSchema();
    //public  LWLockSchema mLWLockSchema=new LWLockSchema();

    private ActivityOperator mActivityOperator[];

    public ActivityOperator()
    {
    }

    public boolean ActivityFinished()
    {
        //�˴�����Finished�����ع������ڲ��߼��Ͳ���
        //1������BeforeEnd������������������ж�,�������Ϊ
        //1����ǰ������켣�Ľṹ
        //2����ǰ���������Ϣ
        //3����ع��������������ݣ���Щ���������������ģ�����ͨ�������������Ч�ʣ�
        if (BeforeEnd())
        {
            if (AfterEnd())
            {
                TransferAllActivity();
            }
        }
        else
        {
            return false;
        }
        return true;
    }

    public boolean AfterInit()
    {
        return true;
    }


    public boolean TransferAllActivity()
    {
        boolean tAuto = true;
        //�˴�Ӧ����һ��ѭ��
        for (int i = 0; i < 10; i++)
        {
            mActivityOperator[0].AfterInit();
            //�������Ҫ�Զ�ִ�еĻ����ִ������Ĵ��롣
            if (tAuto)
            {
                //mActivityOperator[0].ExecBL() ;
                mActivityOperator[0].ActivityFinished();
            }
        }
        return true;
    }


    /**
     *��A���������ʱ�򣬵���������߼�����ͬһ�������У�//��ʱȡ��
     * @return boolean
     */
    public boolean AfterEnd()
    {
//             1������A���ͷ���������A.Finished��ʼ��
//             ������еĻ���
//             2�����Ʋ���B��B2������
//             3��������B������B.AfterInit��,B2������B2.AfterInit��
        return true;
    }


    /**
     *�������ǰ��Ҫִ�еĴ���
     * @return boolean
     */
    public boolean BeforeEnd()
    {
        //��ʼ�����з��������ĺ�����Ľṹ���˴�Ӧ����һ��ѭ��
        mActivityOperator[0] = new ActivityOperator();

        return true;
    }


    /**
     * ����һ�������������������
     * �ڸ÷�������Ҫ���2�����飺
     *  1������һ���������µ��������
     *  2���Ը��´�����������ϡ�����A.Finished��ʼ����(��ʱȡ��)
     * ִ����ɺ󣬲���һ��VData�������ΪBLS�ı�����׼����--YT
     * @param tProcessID String
     * @param tInputData VData
     * @return LWMissionSchema
     */
    public LWMissionSchema CreateStartMission(String tProcessID,
            VData tInputData)
    {
        //��ȡǰ̨����
        GlobalInput tGlobalInput = new GlobalInput();
        TransferData tTransferData = new TransferData();
        tGlobalInput = (GlobalInput) tInputData.getObjectByObjectName(
                "GlobalInput", 0);
        tTransferData = (TransferData) tInputData.getObjectByObjectName(
                "TransferData", 0);

        LWMissionSchema tLWMissionSchema = new LWMissionSchema();

        //У��������Ч��
        if (tProcessID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage = "����һ����������Ĭ�����������,��û�д��빤����������Ϣ!";
            this.mErrors.addOneError(tError);
            return null;
        }

        //��ѯ���������̱�
        LWProcessDB tLWProcessDB = new LWProcessDB();
        LWProcessSet tLWProcessSet = new LWProcessSet();
        tLWProcessDB.setProcessID(tProcessID);
        tLWProcessSet = tLWProcessDB.query();
        if (tLWProcessSet.size() != 1)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage = "����һ����������Ĭ�����������,������Ĺ�����������Ϣ����!";
            this.mErrors.addOneError(tError);
            return null;
        }

        //��ѯĬ�Ϲ�����������ת��(����������ʵ����)
        LWProcessInstanceDB tLWProcessInstanceDB = new LWProcessInstanceDB();
        LWProcessInstanceSet tLWProcessInstanceSet = new LWProcessInstanceSet();
        tLWProcessInstanceDB.setProcessID(tLWProcessSet.get(1).getProcessID());
        tLWProcessInstanceDB.setStartType("0"); //0ΪĬ������ʶ
        tLWProcessInstanceSet = tLWProcessInstanceDB.query();
        if (tLWProcessInstanceSet.size() == 0)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage = "����һ����������Ĭ�����������,��Ĭ�Ϲ�������㲻Ψһ,��ѯ��������ת�����!";
            this.mErrors.addOneError(tError);
            return null;
        }

        //��ѯĬ�Ϲ��������ڵ��(���������)
        LWActivityDB tLWActivityDB = new LWActivityDB();
        LWActivitySet tLWActivitySet = new LWActivitySet();
        tLWActivityDB.setActivityID(tLWProcessInstanceSet.get(1).
                getTransitionStart());
        tLWActivitySet = tLWActivityDB.query();
        if (tLWActivitySet.size() != 1)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage = "����һ����������Ĭ�����������,��ѯ�������ڵ�����!";
            this.mErrors.addOneError(tError);
            return null;
        }

        //У�鴫�����
        LWFieldMapDB tLWFieldMapDB = new LWFieldMapDB();
        LWFieldMapSet tLWFieldMapSet = new LWFieldMapSet();
        tLWFieldMapDB.setActivityID(tLWActivitySet.get(1).getActivityID());
        tLWFieldMapSet = tLWFieldMapDB.query();
        if (tLWFieldMapSet.size() != 0 && tTransferData == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage =
                    "����һ����������Ĭ�����������,���빤�����������������ֶθ������ڸù�������ľ����ֶ�ӳ����м�¼����!";
            this.mErrors.addOneError(tError);
            return null;
        }

        //�����������������,׼������
        String tMissionID = PubFun1.CreateMaxNo("MissionID", 20);
        tLWMissionSchema.setMissionID(tMissionID);
        tLWMissionSchema.setSubMissionID("1");
        tLWMissionSchema.setProcessID(tLWProcessSet.get(1).getProcessID());
        tLWMissionSchema.setActivityID(tLWActivitySet.get(1).getActivityID());
        tLWMissionSchema.setActivityStatus("1"); //0 -- ��������У����״̬�ʺ���һ��������һϵ�ж�����������ɺ�����ύ��ҵ�������屣�����룬���ڵ�����Ҫһ����ʱ�䣬�����ڵ�������л���ָ�״̬����1 -- ���������ϴ�����2 -- �����У�3 -- ������ɣ�4 -- ��ͣ
        //׼�������ֶ�
        for (int i = 1; i <= tLWFieldMapSet.size(); i++)
        {
            if (tLWFieldMapSet.get(i).getSourFieldName() == null ||
                    tLWFieldMapSet.get(i).getSourFieldName().equals(""))
            {
                // @@������
                //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                CError tError = new CError();
                tError.moduleName = "ActivityOperator";
                tError.functionName = "CreateNewMission";
                tError.errorMessage =
                        "����һ����������Ĭ�����������,��������ľ����ֶ�ӳ����м�¼SourFieldName�ֶ���������!";
                this.mErrors.addOneError(tError);
                return null;
            }
            if (tLWFieldMapSet.get(i).getDestFieldName() == null ||
                    tLWFieldMapSet.get(i).getDestFieldName().equals(""))
            {
                // @@������
                //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                CError tError = new CError();
                tError.moduleName = "ActivityOperator";
                tError.functionName = "CreateNewMission";
                tError.errorMessage =
                        "����һ����������ָ�����������,��������ľ����ֶ�ӳ����м�¼SourFieldName�ֶ���������!";
                this.mErrors.addOneError(tError);
                return null;
            }
            if (tTransferData.getValueByName(tLWFieldMapSet.get(i).
                    getSourFieldName()) == null)
            {
                // @@������
                //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                CError tError = new CError();
                tError.moduleName = "ActivityOperator";
                tError.functionName = "CreateNewMission";
                tError.errorMessage = "����һ����������Ĭ�����������,���빤�����������������ֶ���Ϣ����!";
                this.mErrors.addOneError(tError);
                return null;
            }

            String tMissionProp = (String) tTransferData.getValueByName(
                    tLWFieldMapSet.get(i).getSourFieldName());
            String tDestFieldName = tLWFieldMapSet.get(i).getDestFieldName();
            tLWMissionSchema.setV(tDestFieldName, tMissionProp);

        }
        //tLWMissionSchema.setDefaultOperator();
        tLWMissionSchema.setLastOperator(tGlobalInput.Operator);
        tLWMissionSchema.setCreateOperator(tGlobalInput.Operator);
        tLWMissionSchema.setMakeDate(PubFun.getCurrentDate());
        tLWMissionSchema.setMakeTime(PubFun.getCurrentTime());
        tLWMissionSchema.setModifyDate(PubFun.getCurrentDate());
        tLWMissionSchema.setModifyTime(PubFun.getCurrentTime());

        //NewLWMissionSchema.setSchema(tLWMissionSchema);
        return tLWMissionSchema;
    }


    /**
     * ����һ����������ָ���������
     * �ڸ÷�������Ҫ���2�����飺
     *  1������һ���������µ��������
     *  2���Ը��´�����������ϡ�����A.Finished��ʼ����(��ʱȡ��)
     * ִ����ɺ󣬲���һ��VData�������ΪBLS�ı�����׼����--YT
     * @param tProcessID String
     * @param tActivityID String
     * @param tInputData VData
     * @return boolean
     */
    public boolean CreateStartMission(String tProcessID, String tActivityID,
            VData tInputData)
    {
        //��ȡǰ̨����
        GlobalInput tGlobalInput = new GlobalInput();
        TransferData tTransferData = new TransferData();
        tGlobalInput = (GlobalInput) tInputData.getObjectByObjectName(
                "GlobalInput", 0);
        tTransferData = (TransferData) tInputData.getObjectByObjectName(
                "TransferData", 0);

        LWMissionSchema tLWMissionSchema = new LWMissionSchema();

        //У��������Ч��
        if (tProcessID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage = "����һ����������ָ�����������,��û�д��빤����������Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (tActivityID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage = "����һ����������ָ�����������,��û�д��빤������ڵ���Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��ѯ���������̱�
        LWProcessDB tLWProcessDB = new LWProcessDB();
        LWProcessSet tLWProcessSet = new LWProcessSet();
        tLWProcessDB.setProcessID(tProcessID);
        tLWProcessSet = tLWProcessDB.query();
        if (tLWProcessSet.size() != 1)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage = "����һ����������ָ�����������,������Ĺ�����������Ϣ����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��ѯָ��������������ת��(����������ʵ����)
        LWProcessInstanceDB tLWProcessInstanceDB = new LWProcessInstanceDB();
        LWProcessInstanceSet tLWProcessInstanceSet = new LWProcessInstanceSet();

        StringBuffer tSBql = new StringBuffer(256);
        tSBql.append("select * from LWProcessInstance where ProcessID ='");
        tSBql.append(tLWProcessSet.get(1).getProcessID());
        tSBql.append("' and TransitionStart = '");
        tSBql.append(tActivityID);
        tSBql.append("' and StartType <>'2'");

        tLWProcessInstanceSet = tLWProcessInstanceDB.executeQuery(tSBql.toString());

        if (tLWProcessInstanceSet == null || tLWProcessInstanceSet.size() == 0)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage = "����һ����������ָ�����������,��ѯ��������ת�����!";
            this.mErrors.addOneError(tError);
            return false;

        }

        //��ѯĬ�Ϲ��������ڵ��(���������)
        LWActivityDB tLWActivityDB = new LWActivityDB();
        LWActivitySet tLWActivitySet = new LWActivitySet();
        tLWActivityDB.setActivityID(tActivityID);
        tLWActivitySet = tLWActivityDB.query();
        if (tLWActivitySet == null || tLWActivitySet.size() != 1)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLWActivitySet.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage = "����һ����������ָ�����������,��ѯ�������ڵ�����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //У�鴫�����
        LWFieldMapDB tLWFieldMapDB = new LWFieldMapDB();
        LWFieldMapSet tLWFieldMapSet = new LWFieldMapSet();
        tLWFieldMapDB.setActivityID(tLWActivitySet.get(1).getActivityID());
        tLWFieldMapSet = tLWFieldMapDB.query();
        if (tLWFieldMapSet.size() == 0 && tTransferData == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage =
                    "����һ����������ָ�����������,���빤�����������������ֶθ������ڸù�������ľ����ֶ�ӳ����м�¼����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //�����������������,׼������
        String tMissionID = PubFun1.CreateMaxNo("MissionID", 20);
        tLWMissionSchema.setMissionID(tMissionID);
        tLWMissionSchema.setSubMissionID("1");
        tLWMissionSchema.setProcessID(tLWProcessSet.get(1).getProcessID());
        tLWMissionSchema.setActivityID(tLWActivitySet.get(1).getActivityID());
        tLWMissionSchema.setActivityStatus("1"); //0 -- ��������У����״̬�ʺ���һ��������һϵ�ж�����������ɺ�����ύ��ҵ�������屣�����룬���ڵ�����Ҫһ����ʱ�䣬�����ڵ�������л���ָ�״̬����1 -- ���������ϴ�����2 -- �����У�3 -- ������ɣ�4 -- ��ͣ
        //׼�������ֶ�
        for (int i = 1; i <= tLWFieldMapSet.size(); i++)
        {
            if (tLWFieldMapSet.get(i).getSourFieldName() == null ||
                    tLWFieldMapSet.get(i).getSourFieldName().equals(""))
            {
                // @@������
                //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                CError tError = new CError();
                tError.moduleName = "ActivityOperator";
                tError.functionName = "CreateNewMission";
                tError.errorMessage =
                        "����һ����������ָ�����������,��������ľ����ֶ�ӳ����м�¼SourFieldName�ֶ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            if (tLWFieldMapSet.get(i).getDestFieldName() == null ||
                    tLWFieldMapSet.get(i).getDestFieldName().equals(""))
            {
                // @@������
                //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                CError tError = new CError();
                tError.moduleName = "ActivityOperator";
                tError.functionName = "CreateNewMission";
                tError.errorMessage =
                        "����һ����������ָ�����������,��������ľ����ֶ�ӳ����м�¼SourFieldName�ֶ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }
            if (tTransferData.getValueByName(tLWFieldMapSet.get(i).
                    getSourFieldName()) == null)
            {
                // @@������
                //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                CError tError = new CError();
                tError.moduleName = "ActivityOperator";
                tError.functionName = "CreateNewMission";
                tError.errorMessage = "����һ����������ָ�����������,���빤�����������������ֶ���Ϣ����!";
                this.mErrors.addOneError(tError);
                return false;
            }

            String tMissionProp = (String) tTransferData.getValueByName(
                    tLWFieldMapSet.get(i).getSourFieldName());
            String tDestFieldName = tLWFieldMapSet.get(i).getDestFieldName();

            tLWMissionSchema.setV(tDestFieldName, tMissionProp);
        }
        tLWMissionSchema.setDefaultOperator(tGlobalInput.Operator);
        tLWMissionSchema.setLastOperator(tGlobalInput.Operator);
        tLWMissionSchema.setCreateOperator(tGlobalInput.Operator);
        tLWMissionSchema.setMakeDate(PubFun.getCurrentDate());
        tLWMissionSchema.setMakeTime(PubFun.getCurrentTime());
        tLWMissionSchema.setModifyDate(PubFun.getCurrentDate());
        tLWMissionSchema.setModifyTime(PubFun.getCurrentTime());

        //NewLWMissionSchema.setSchema(tLWMissionSchema);
        mResult.clear();
        if (tLWMissionSchema != null)
        {
            MMap map = new MMap();
            map.put(tLWMissionSchema, "INSERT");
            mResult.add(map);
        }
        return true;
    }


    /**
     * ����һ����������ָ���������
     * �ڸ÷�������Ҫ���2�����飺
     *  1������һ���������µ��������
     *  2���Ը��´�����������ϡ�����A.Finished��ʼ����(��ʱȡ��)
     * ִ����ɺ󣬲���һ��VData�������ΪBLS�ı�����׼����--YT
     * @param tProcessID String
     * @param tActivityID String
     * @param tInputData VData
     * @return LWMissionSchema
     */
    public LWMissionSchema CreateOneMission(String tProcessID,
            String tActivityID,
            VData tInputData)
    {
        //��ȡǰ̨����
        GlobalInput tGlobalInput = new GlobalInput();
        TransferData tTransferData = new TransferData();
        tGlobalInput = (GlobalInput) tInputData.getObjectByObjectName(
                "GlobalInput", 0);
        tTransferData = (TransferData) tInputData.getObjectByObjectName(
                "TransferData", 0);

        LWMissionSchema tLWMissionSchema = new LWMissionSchema();

        //У��������Ч��
        if (tProcessID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage = "����һ����������ָ�����������,��û�д��빤����������Ϣ!";
            this.mErrors.addOneError(tError);
            return null;
        }
        if (tActivityID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage = "����һ����������ָ�����������,��û�д��빤������ڵ���Ϣ!";
            this.mErrors.addOneError(tError);
            return null;
        }

        //��ѯ���������̱�
        LWProcessDB tLWProcessDB = new LWProcessDB();
        LWProcessSet tLWProcessSet = new LWProcessSet();
        tLWProcessDB.setProcessID(tProcessID);
        tLWProcessSet = tLWProcessDB.query();
        if (tLWProcessSet.size() != 1)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage = "����һ����������ָ�����������,������Ĺ�����������Ϣ����!";
            this.mErrors.addOneError(tError);
            return null;
        }

        //��ѯָ��������������ת��(����������ʵ����)
        LWProcessInstanceDB tLWProcessInstanceDB = new LWProcessInstanceDB();
        LWProcessInstanceSet tLWProcessInstanceSet = new LWProcessInstanceSet();

        StringBuffer tSBql = new StringBuffer(256);
        tSBql.append("select * from LWProcessInstance where ProcessID ='");
        tSBql.append(tLWProcessSet.get(1).getProcessID());
        tSBql.append("' and TransitionStart = '");
        tSBql.append(tActivityID);
        tSBql.append("' and StartType <>'2'");
        tLWProcessInstanceSet = tLWProcessInstanceDB.executeQuery(tSBql.toString());

        if (tLWProcessInstanceSet == null || tLWProcessInstanceSet.size() == 0)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage = "����һ����������ָ�����������,��ѯ��������ת�����!";
            this.mErrors.addOneError(tError);
            return null;

        }

        //��ѯĬ�Ϲ��������ڵ��(���������)
        LWActivityDB tLWActivityDB = new LWActivityDB();
        LWActivitySet tLWActivitySet = new LWActivitySet();
        tLWActivityDB.setActivityID(tActivityID);
        tLWActivitySet = tLWActivityDB.query();
        if (tLWActivitySet == null || tLWActivitySet.size() != 1)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLWActivitySet.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage = "����һ����������ָ�����������,��ѯ�������ڵ�����!";
            this.mErrors.addOneError(tError);
            return null;
        }

        //У�鴫�����
        LWFieldMapDB tLWFieldMapDB = new LWFieldMapDB();
        LWFieldMapSet tLWFieldMapSet = new LWFieldMapSet();
        tLWFieldMapDB.setActivityID(tLWActivitySet.get(1).getActivityID());
        tLWFieldMapSet = tLWFieldMapDB.query();
        if (tLWFieldMapSet.size() != 0 && tTransferData == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage =
                    "����һ����������ָ�����������,���빤�����������������ֶθ������ڸù�������ľ����ֶ�ӳ����м�¼����!";
            this.mErrors.addOneError(tError);
            return null;
        }

        //�����������������,׼������
        String tMissionID = PubFun1.CreateMaxNo("MissionID", 20);
        tLWMissionSchema.setMissionID(tMissionID);
        tLWMissionSchema.setSubMissionID("1");
        tLWMissionSchema.setProcessID(tLWProcessSet.get(1).getProcessID());
        tLWMissionSchema.setActivityID(tLWActivitySet.get(1).getActivityID());
        tLWMissionSchema.setActivityStatus("1"); //0 -- ��������У����״̬�ʺ���һ��������һϵ�ж�����������ɺ�����ύ��ҵ�������屣�����룬���ڵ�����Ҫһ����ʱ�䣬�����ڵ�������л���ָ�״̬����1 -- ���������ϴ�����2 -- �����У�3 -- ������ɣ�4 -- ��ͣ
        //׼�������ֶ�
        for (int i = 1; i <= tLWFieldMapSet.size(); i++)
        {
            if (tLWFieldMapSet.get(i).getSourFieldName() == null ||
                    tLWFieldMapSet.get(i).getSourFieldName().equals(""))
            {
                // @@������
                //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                CError tError = new CError();
                tError.moduleName = "ActivityOperator";
                tError.functionName = "CreateNewMission";
                tError.errorMessage =
                        "����һ����������ָ�����������,��������ľ����ֶ�ӳ����м�¼SourFieldName�ֶ���������!";
                this.mErrors.addOneError(tError);
                return null;
            }

            if (tLWFieldMapSet.get(i).getDestFieldName() == null ||
                    tLWFieldMapSet.get(i).getDestFieldName().equals(""))
            {
                // @@������
                //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                CError tError = new CError();
                tError.moduleName = "ActivityOperator";
                tError.functionName = "CreateNewMission";
                tError.errorMessage =
                        "����һ����������ָ�����������,��������ľ����ֶ�ӳ����м�¼SourFieldName�ֶ���������!";
                this.mErrors.addOneError(tError);
                return null;
            }
            if (tTransferData.getValueByName(tLWFieldMapSet.get(i).
                    getSourFieldName()) == null)
            {
                // @@������
                //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                CError tError = new CError();
                tError.moduleName = "ActivityOperator";
                tError.functionName = "CreateNewMission";
                tError.errorMessage = "����һ����������ָ�����������,���빤�����������������ֶ���Ϣ����!";
                this.mErrors.addOneError(tError);
                return null;
            }

            String tMissionProp = (String) tTransferData.getValueByName(
                    tLWFieldMapSet.get(i).getSourFieldName());
            String tDestFieldName = tLWFieldMapSet.get(i).getDestFieldName();

            tLWMissionSchema.setV(tDestFieldName, tMissionProp);
        }
        tLWMissionSchema.setDefaultOperator(tGlobalInput.Operator);
        tLWMissionSchema.setLastOperator(tGlobalInput.Operator);
        tLWMissionSchema.setCreateOperator(tGlobalInput.Operator);
        tLWMissionSchema.setMakeDate(PubFun.getCurrentDate());
        tLWMissionSchema.setMakeTime(PubFun.getCurrentTime());
        tLWMissionSchema.setModifyDate(PubFun.getCurrentDate());
        tLWMissionSchema.setModifyTime(PubFun.getCurrentTime());

//        System.out.println("ok!:");
        return tLWMissionSchema;
    }


    /**
     * ����һ����Ե�ǰ�ڵ��ָ������һ�ڵ�����
     * @param tMissionID String ����ĵ�ǰ ����ID ����
     * @param tSourActivityID String
     * @param tDestActivityID String
     * @param tInputData VData ����ĸ�������
     * @param tStr String
     * @return boolean
     * @throws Exception
     */
    public boolean CreateNextMission(String tMissionID, String tSourActivityID,
            String tDestActivityID, VData tInputData,
            String tStr)
            throws Exception
    {
        //��ȡǰ̨����
        GlobalInput tGlobalInput = new GlobalInput();
        TransferData tTransferData = new TransferData();
        tGlobalInput = (GlobalInput) tInputData.getObjectByObjectName(
                "GlobalInput", 0);
        tTransferData = (TransferData) tInputData.getObjectByObjectName(
                "TransferData", 0);

        LWMissionSchema tLWMissionSchema = new LWMissionSchema();

        //У��������Ч��
        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNextMission";
            tError.errorMessage = "����һ������������һ����ڵ�,��û�д��빤������ǰ����ID��Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (tDestActivityID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNextMission";
            tError.errorMessage = "����һ������������һ����ڵ�,��û�д���ָ�����ID��Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��ѯ����������켣��
        LWMissionDB tLWMissionDB = new LWMissionDB();
        LWMissionSet tLWMissionSet = new LWMissionSet();
        tLWMissionDB.setMissionID(tMissionID);
        tLWMissionDB.setActivityID(tSourActivityID);
        tLWMissionSet = tLWMissionDB.query();
        if (tLWMissionSet.size() != 1)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNextMission";
            tError.errorMessage = "����һ������������һ������ڵ�,��ѯ����������켣��LWMission����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��ѯָ����ڵ��(���������)
        LWActivityDB tLWActivityDB = new LWActivityDB();
        LWActivitySet tLWActivitySet = new LWActivitySet();
        tLWActivityDB.setActivityID(tDestActivityID);
        tLWActivitySet = tLWActivityDB.query();
        if (tLWActivitySet.size() != 1)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNextMission";
            tError.errorMessage = "����һ������������һ������ڵ�,��ѯ��������ڵ�����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��ѯ����������ʵ����
        LWProcessInstanceDB tLWProcessInstanceDB = new LWProcessInstanceDB();
        LWProcessInstanceSet tLWProcessInstanceSet = new LWProcessInstanceSet();
        tLWProcessInstanceDB.setProcessID(tLWMissionSet.get(1).getProcessID());
        tLWProcessInstanceDB.setTransitionStart(tSourActivityID);
        tLWProcessInstanceDB.setTransitionEnd(tDestActivityID);
        tLWProcessInstanceSet = tLWProcessInstanceDB.query();
        if (tLWProcessInstanceSet.size() != 1)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNextMission";
            tError.errorMessage = "����һ������������һ������ڵ�,��ѯ��������ת�����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //У��ָ����ת���Ƿ�����ת������
        if (!CheckTransitionCondition(tLWProcessInstanceSet.get(1), tInputData))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNextMission";
            tError.errorMessage = "����һ������������һ������ڵ�,ָ����ת�Ʋ�����ת������!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //У�鴫�����
        LWFieldMapDB tLWFieldMapDB = new LWFieldMapDB();
        LWFieldMapSet tLWFieldMapSet = new LWFieldMapSet();
        tLWFieldMapDB.setActivityID(tLWActivitySet.get(1).getActivityID());
        tLWFieldMapSet = tLWFieldMapDB.query();
        if (tLWFieldMapSet.size() != 0 && tTransferData == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNextMission";
            tError.errorMessage =
                    "����һ������������һ�ڵ�����,���빤�����������������ֶθ������ڸù�������ľ����ֶ�ӳ����м�¼����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //�����������������,׼������
        //String tMissionID = PubFun1.CreateMaxNo( "MissionID", 20 );
        tLWMissionSchema.setMissionID(tMissionID);
        tLWMissionSchema.setSubMissionID("0");
        tLWMissionSchema.setProcessID(tLWMissionSet.get(1).getProcessID());
        tLWMissionSchema.setActivityID(tLWActivitySet.get(1).getActivityID());
        tLWMissionSchema.setActivityStatus("1"); //0 -- ��������У����״̬�ʺ���һ��������һϵ�ж�����������ɺ�����ύ��ҵ�������屣�����룬���ڵ�����Ҫһ����ʱ�䣬�����ڵ�������л���ָ�״̬����1 -- ���������ϴ�����2 -- �����У�3 -- ������ɣ�4 -- ��ͣ
        //׼�������ֶ�
        for (int i = 1; i <= tLWFieldMapSet.size(); i++)
        {
            if (tLWFieldMapSet.get(i).getSourFieldName() == null ||
                    tLWFieldMapSet.get(i).getSourFieldName().equals(""))
            {
                // @@������
                //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                CError tError = new CError();
                tError.moduleName = "ActivityOperator";
                tError.functionName = "CreateNewMission";
                tError.errorMessage =
                        "����һ������������һ������ڵ�,��������ľ����ֶ�ӳ����м�¼SourFieldName�ֶ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }
            if (tLWFieldMapSet.get(i).getDestFieldName() == null ||
                    tLWFieldMapSet.get(i).getDestFieldName().trim().equals(""))
            {
                // @@������
                //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                CError tError = new CError();
                tError.moduleName = "ActivityOperator";
                tError.functionName = "CreateNewMission";
                tError.errorMessage =
                        "����һ������������һ������ڵ�,��������ľ����ֶ�ӳ����м�¼DestFieldName�ֶ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }
            if (tTransferData.getValueByName(tLWFieldMapSet.get(i).
                    getSourFieldName()) == null)
            {
                // @@������
                //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                CError tError = new CError();
                tError.moduleName = "ActivityOperator";
                tError.functionName = "CreateNewMission";
                tError.errorMessage = "����һ������������һ������ڵ�,���빤�����������������ֶ���Ϣ����!";
                this.mErrors.addOneError(tError);
                return false;
            }

            String tMissionProp = (String) tTransferData.getValueByName(
                    tLWFieldMapSet.get(i).getSourFieldName());
            String tDestFieldName = tLWFieldMapSet.get(i).getDestFieldName();
            tLWMissionSchema.setV(tDestFieldName, tMissionProp);

        }
        tLWMissionSchema.setDefaultOperator(tGlobalInput.Operator);
        tLWMissionSchema.setLastOperator(tGlobalInput.Operator);
        tLWMissionSchema.setCreateOperator(tGlobalInput.Operator);
        tLWMissionSchema.setMakeDate(PubFun.getCurrentDate());
        tLWMissionSchema.setMakeTime(PubFun.getCurrentTime());
        tLWMissionSchema.setModifyDate(PubFun.getCurrentDate());
        tLWMissionSchema.setModifyTime(PubFun.getCurrentTime());

        mResult.clear();
        mResult.add(tLWMissionSchema);
        return true;
    }


    /**
     * ����һ����Ե�ǰ�ڵ����������ת����������һ�ڵ�����
     * @param tMissionID String
     * @param tSubMissionID String
     * @param tSourActivityID String
     * @param tInputData VData
     * @return boolean
     * @throws Exception
     */
    public boolean CreateNextMission(String tMissionID, String tSubMissionID
            , String tSourActivityID, VData tInputData)
            throws Exception
    {
        //��ȡǰ̨����
        GlobalInput tGlobalInput = new GlobalInput();
        TransferData tTransferData = new TransferData();
        tGlobalInput = (GlobalInput) tInputData.getObjectByObjectName("GlobalInput", 0);
        tTransferData = (TransferData) tInputData.getObjectByObjectName("TransferData", 0);

        LWMissionSet tNewLWMissionResultSet = new LWMissionSet(); //�²���������ڵ�
        //LWMissionSet tOldLWMissionResultSet = new LWMissionSet();//�ɵ�����ڵ�
        //У��������Ч��
        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNextMission";
            tError.errorMessage = "����һ������������һ����ڵ�,��û�д��빤������ǰ����ID��Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSourActivityID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNextMission";
            tError.errorMessage = "����һ������������һ����ڵ�,��û�д��빤������ǰ�ID��Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��ѯ����������켣��
        LWMissionDB tLWMissionDB = new LWMissionDB();
        LWMissionSet tLWMissionSet = new LWMissionSet();
        tLWMissionDB.setMissionID(tMissionID);
        tLWMissionDB.setActivityID(tSourActivityID);
        tLWMissionDB.setSubMissionID(tSubMissionID);
        tLWMissionSet = tLWMissionDB.query();

        if (tLWMissionSet == null || tLWMissionSet.size() != 1)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNextMission";
            tError.errorMessage = "����һ������������һ������ڵ�,��ѯ����������켣��LWMission����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��ѯ����������ʵ����
        LWProcessInstanceDB tLWProcessInstanceDB = new LWProcessInstanceDB();
        LWProcessInstanceSet tLWProcessInstanceSet = new LWProcessInstanceSet();
        tLWProcessInstanceDB.setProcessID(tLWMissionSet.get(1).getProcessID());
        tLWProcessInstanceDB.setTransitionStart(tLWMissionSet.get(1).getActivityID());
        tLWProcessInstanceSet = tLWProcessInstanceDB.query();
        if (tLWProcessInstanceSet == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNextMission";
            tError.errorMessage = "����һ������������һ������ڵ�,��ѯ��������ת�����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLWProcessInstanceSet.size() < 1)
        {
            return true;
        }

        //׼��������һ�ڵ����������
        for (int j = 1; j <= tLWProcessInstanceSet.size(); j++)
        {
            //��ѯָ����ڵ��(���������)
            String tActivityID = tLWProcessInstanceSet.get(j).getTransitionEnd();
            if (tActivityID == null || tActivityID.trim().equals(""))
            {
                // @@������
                //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                CError tError = new CError();
                tError.moduleName = "ActivityOperator";
                tError.functionName = "CreateNextMission";
                tError.errorMessage =
                        "����һ������������һ������ڵ�,��ѯ������ת�ƽڵ��ActivityID���ݳ���!";
                this.mErrors.addOneError(tError);
                return false;
            }

            LWActivityDB tLWActivityDB = new LWActivityDB();
            LWActivitySet tLWActivitySet = new LWActivitySet();
            tLWActivityDB.setActivityID(tActivityID);
            tLWActivitySet = tLWActivityDB.query();
            if (tLWActivitySet == null || tLWActivitySet.size() != 1)
            {
                // @@������
                //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                CError tError = new CError();
                tError.moduleName = "ActivityOperator";
                tError.functionName = "CreateNextMission";
                tError.errorMessage = "����һ������������һ������ڵ�,��ѯ��������ڵ�����!";
                this.mErrors.addOneError(tError);
                return false;
            }
//            System.out.println("-------------------in");
            //У��ָ����ת���Ƿ�����ת������
            if (CheckTransitionCondition(tLWProcessInstanceSet.get(j), tInputData))
            {
                //У��W���Ѵ��ڸ�����ڵ�����
                int tLWSize = 0;
                int tLBSize = 0;
                LWMissionDB tempLWMissionDB = new LWMissionDB();
                LWMissionSet tempLWMissionSet = new LWMissionSet();
                tempLWMissionDB.setMissionID(tMissionID);
                tempLWMissionDB.setActivityID(tActivityID);
                tempLWMissionSet = tempLWMissionDB.query();
                if (tempLWMissionSet != null)
                {
                    tLWSize = tempLWMissionSet.size();
                }
                else
                {
                    // @@������
                    //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                    CError tError = new CError();
                    tError.moduleName = "ActivityOperator";
                    tError.functionName = "CreateNextMission";
                    tError.errorMessage = "����һ������������һ�ڵ�����,��ѯ����켣��ʧ��!";
                    this.mErrors.addOneError(tError);
                    return false;

                }
                ////У��B���Ѵ��ڸ�����ڵ�����
                LBMissionDB tempLBMissionDB = new LBMissionDB();
                LBMissionSet tempLBMissionSet = new LBMissionSet();
                tempLBMissionDB.setMissionID(tMissionID);
                tempLBMissionDB.setActivityID(tActivityID);
                tempLBMissionSet = tempLBMissionDB.query();
                if (tempLBMissionSet != null)
                {
                    tLBSize = tempLBMissionSet.size();
                }
                else
                {
                    // @@������
                    //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                    CError tError = new CError();
                    tError.moduleName = "ActivityOperator";
                    tError.functionName = "CreateNextMission";
                    tError.errorMessage = "����һ������������һ�ڵ�����,��ѯ����켣���ݱ�ʧ��!";
                    this.mErrors.addOneError(tError);
                    return false;
                }

                //������ڵ����״β���
                LWMissionSchema tLWMissionSchema = new LWMissionSchema();
                //У�鴫�����
                LWFieldMapDB tLWFieldMapDB = new LWFieldMapDB();
                LWFieldMapSet tLWFieldMapSet = new LWFieldMapSet();
                tLWFieldMapDB.setActivityID(tLWActivitySet.get(1).getActivityID());
                tLWFieldMapSet = tLWFieldMapDB.query();
                if (tLWFieldMapSet == null || (tLWFieldMapSet.size() != 0 && tTransferData == null))
                {
                    // @@������
                    //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                    CError tError = new CError();
                    tError.moduleName = "ActivityOperator";
                    tError.functionName = "CreateNextMission";
                    tError.errorMessage =
                            "����һ������������һ�ڵ�����,���빤�����������������ֶθ������ڸù�������ľ����ֶ�ӳ����м�¼����!";
                    this.mErrors.addOneError(tError);
                    return false;
                }

                //�����������������,׼������
                tLWMissionSchema.setMissionID(tMissionID);
                tLWMissionSchema.setSubMissionID(String.valueOf(tLWSize + tLBSize + 1));
                tLWMissionSchema.setProcessID(tLWMissionSet.get(1).getProcessID());
                tLWMissionSchema.setActivityID(tActivityID);
                tLWMissionSchema.setActivityStatus("1"); //0 -- ��������У����״̬�ʺ���һ��������һϵ�ж�����������ɺ�����ύ��ҵ�������屣�����룬���ڵ�����Ҫһ����ʱ�䣬�����ڵ�������л���ָ�״̬����1 -- ���������ϴ�����2 -- �����У�3 -- ������ɣ�4 -- ��ͣ
                //׼�������ֶ�
                for (int i = 1; i <= tLWFieldMapSet.size(); i++)
                {
                    if (tLWFieldMapSet.get(i).getSourFieldName() == null
                            || tLWFieldMapSet.get(i).getSourFieldName().equals(""))
                    {
                        // @@������
                        //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                        CError tError = new CError();
                        tError.moduleName = "ActivityOperator";
                        tError.functionName = "CreateNewMission";
                        tError.errorMessage =
                                "����һ������������һ������ڵ�,��������ľ����ֶ�ӳ����м�¼SourFieldName�ֶ���������!";
                        this.mErrors.addOneError(tError);
                        return false;
                    }
                    if (tLWFieldMapSet.get(i).getDestFieldName() == null
                            || tLWFieldMapSet.get(i).getDestFieldName().trim().equals(""))
                    {
                        // @@������
                        //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                        CError tError = new CError();
                        tError.moduleName = "ActivityOperator";
                        tError.functionName = "CreateNewMission";
                        tError.errorMessage =
                                "����һ������������һ������ڵ�,��������ľ����ֶ�ӳ����м�¼DestFieldName�ֶ���������!";
                        this.mErrors.addOneError(tError);
                        return false;
                    }

//		   if( tTransferData.getValueByName(tLWFieldMapSet.get(i).getSourFieldName()) == null )
//		  {
//			// @@������
//			//this.mErrors.copyAllErrors( tLCPolDB.mErrors );
//			CError tError = new CError();
//			tError.moduleName = "ActivityOperator";
//			tError.functionName = "CreateNewMission";
//			tError.errorMessage = "����һ������������һ������ڵ�,���빤�����������������ֶ���Ϣ����!";
//			this.mErrors.addOneError(tError) ;
//			return false;
//		  }

                    String tMissionProp = (String) tTransferData.getValueByName(tLWFieldMapSet.get(
                            i).getSourFieldName());
                    String tDestFieldName = tLWFieldMapSet.get(i).getDestFieldName();
                    tLWMissionSchema.setV(tDestFieldName, tMissionProp);
                    System.out.println("���������ֶ�"+i+tDestFieldName+":"+tMissionProp);
                }
               //�µ�������ɺ���뾭��������ܵ�����˹������� 20070516
                if("0000002004".equals(tActivityID))
                {
                	tLWMissionSchema.setDefaultOperator("");
                }
                else
                {        
                	tLWMissionSchema.setDefaultOperator(tGlobalInput.Operator);
                }
                tLWMissionSchema.setLastOperator(tGlobalInput.Operator);
                tLWMissionSchema.setCreateOperator(tGlobalInput.Operator);
                tLWMissionSchema.setMakeDate(PubFun.getCurrentDate());
                tLWMissionSchema.setMakeTime(PubFun.getCurrentTime());
                tLWMissionSchema.setModifyDate(PubFun.getCurrentDate());
                tLWMissionSchema.setModifyTime(PubFun.getCurrentTime());

                tNewLWMissionResultSet.add(tLWMissionSchema);
                tLWMissionSchema = null;
            }
        }

        mResult.clear();
        MMap map = new MMap();

        if (tNewLWMissionResultSet != null && tNewLWMissionResultSet.size() > 0)
        {
            map.put(tNewLWMissionResultSet, "INSERT");
        }
        if (map != null && map.keySet().size() > 0)
        {
            mResult.add(map);
        }

        return true;
    }


    /**
     * У��ת�������Ƿ�����
     * @param tLWProcessInstanceSchema LWProcessInstanceSchema ����ĵ�ǰ ����������ʵ�� ����
     * @param tInputData VData ����ĸ�������
     * @return boolean
     * @throws Exception
     */
    private boolean CheckTransitionCondition(LWProcessInstanceSchema tLWProcessInstanceSchema
            , VData tInputData)
            throws Exception
    {
        //��ȡ��������
//        GlobalInput tGlobalInput = new GlobalInput();
        TransferData tTransferData = new TransferData();
//        tGlobalInput = (GlobalInput) tInputData.getObjectByObjectName(
//                "GlobalInput", 0);
        tTransferData = (TransferData) tInputData.getObjectByObjectName("TransferData", 0);
        //Connection conn = DBConnPool.getConnection();
//        System.out.println("---------123");
        if (tLWProcessInstanceSchema == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CheckTransitionCondition";
            tError.errorMessage = "tLWProcessInstanceSchemaΪ��";
            this.mErrors.addOneError(tError);
            return false;
        }
//        System.out.println("---------3333123" + tLWProcessInstanceSchema.getTransitionCondT());

        if (tLWProcessInstanceSchema.getTransitionCondT() == null ||
                tLWProcessInstanceSchema.getTransitionCondT().trim().equals(""))
        {

            //��----��ʾû����Ҫִ�еĶ�����
            return true;
        }
        else if (tLWProcessInstanceSchema.getTransitionCondT().equals("0"))
        {
            //0 -- Ĭ�ϣ���ʾ����ΪWhere�Ӿ䡣
//            System.out.println("---------aaaaa");
            PubCalculator tPubCalculator = new PubCalculator();
            //׼������Ҫ��
            Vector tVector = tTransferData.getValueNames();
//            for (int j = 0; j < tVector.size(); j++)
//            {
//                String ttName = (String) tVector.get(j);
//                System.out.println("ttName=" + ttName);
//            }

//            System.out.println("i===" + tVector.size());
            for (int i = 0; i < tVector.size(); i++)
            {
                String tName = (String) tVector.get(i);
                String tValue = tTransferData.getValueByName((Object) tName).toString();
//                System.out.println("tName=" + tName);
//                System.out.println("tValue=" + tValue);
                tPubCalculator.addBasicFactor(tName, tValue);
            }
//            System.out.println("---------bbbbbb=" + tLWProcessInstanceSchema.getTransitionCond());
            //׼������SQL
            tPubCalculator.setCalSql(tLWProcessInstanceSchema.getTransitionCond());
            String strSQL = tPubCalculator.calculate();
//            System.out.println("SQL : " + strSQL);
            if (strSQL == null || strSQL.trim().equals(""))
            {
                return true;
            }
            else
            {
                //�ֽ��������,����ִ��
                //Statement stmt = conn.createStatement();
                try
                {
                    String strTemp = "";
                    char cTemp = ' ';
                    for (int nIndex = 0; nIndex < strSQL.length(); nIndex++)
                    {
                        cTemp = strSQL.charAt(nIndex);
                        if (cTemp == ';' && !strTemp.equals(""))
                        {
//                            System.out.println(strTemp);
                            String tReSult = new String();
                            ExeSQL tExeSQL = new ExeSQL();
                            tReSult = tExeSQL.getOneValue(strTemp); //ת����������ʱһ�����з���ֵ,ת������SQLֻ��Select���
                            if (tExeSQL.mErrors.needDealError())
                            {
                                // @@������
                                this.mErrors.copyAllErrors(tExeSQL.mErrors);
                                CError tError = new CError();
                                tError.moduleName = "ActivityOperator";
                                tError.functionName =
                                        "CheckTransitionCondition";
                                tError.errorMessage = "ִ��SQL��䣺" + strTemp +
                                        "ʧ��!";
                                this.mErrors.addOneError(tError);
                                return false;
                            }
                            if (tReSult == null || tReSult.equals("")) //������ת������
                            {
                                return false;
                            }
                            strTemp = "";
                        }
                        else
                        {
                            strTemp += String.valueOf(cTemp);
                        }
                    } // end of for
//                    System.out.println("strTemp=" + strTemp);
                    //stmt.close();
                }
                catch (Exception ex)
                {
                    //stmt.close();
                    ex.printStackTrace();
                    return false;
                }
            }
            return true;
        }
        else if (tLWProcessInstanceSchema.getTransitionCondT().equals("1"))
        {
            //1 -- ��ʾת������Ϊһ��������ࡣ
            try
            {
                Class tClass = Class.forName(tLWProcessInstanceSchema.
                        getTransitionCond());
                TransCondService tTransCondService = (TransCondService) tClass.
                        newInstance();

                // ׼������
                String strOperate = "TransCondService";
                tInputData.add(tLWProcessInstanceSchema);
                if (!tTransCondService.submitData(tInputData, strOperate))
                {
                    return false;
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                return false;
            }

        }
//   if(!conn.isClosed())
//   {
//	 conn.close();
//   }
        return true;
    }


    /**
     * ִ�нڵ�����
     * @param tMissionID String
     * @param tSubMissionID String
     * @param tActivityID String
     * @param tInputData VData
     * @return boolean
     * @throws Exception
     */
    public boolean ExecuteMission(String tMissionID, String tSubMissionID, String tActivityID
            , VData tInputData)
            throws Exception
    {
        mResult.clear();
        //У��������Ч��
        if (tMissionID == null || tMissionID.equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ�нڵ�����,��û�д��뵱ǰ����ID��Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null || tSubMissionID.equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ�нڵ�����,��û�д��뵱ǰ������ID��Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tActivityID == null || tActivityID.equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ�нڵ�����,��û�д��뵱ǰ����ID��Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��ѯ����������켣��
        LWMissionDB tLWMissionDB = new LWMissionDB();
        LWMissionSet tLWMissionSet = new LWMissionSet();
        tLWMissionDB.setMissionID(tMissionID);
        tLWMissionDB.setSubMissionID(tSubMissionID);
        tLWMissionDB.setActivityID(tActivityID);
        tLWMissionSet = tLWMissionDB.query();

        if (tLWMissionSet == null || tLWMissionSet.size() == 0)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "������ýڵ������ִ������. ";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLWMissionSet.size() > 1)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ�нڵ�����,��ѯ����������켣��LWMission����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��ѯָ����ڵ��(���������)
        LWActivityDB tLWActivityDB = new LWActivityDB();
        LWActivitySet tLWActivitySet = new LWActivitySet();
        tLWActivityDB.setActivityID(tLWMissionSet.get(1).getActivityID());
        tLWActivitySet = tLWActivityDB.query();
        if (tLWActivitySet.size() != 1)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ�нڵ�����,��ѯ��������ڵ�����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        LWActivitySchema tLWActivitySchema = new LWActivitySchema();
        tLWActivitySchema.setSchema(tLWActivitySet.get(1));

        //ִ��BeforeInit����
        if (!ExecuteBeforeInitDuty(tLWActivitySchema, tInputData))
        {

            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ�нڵ�����,ִ��BeforeInit�������!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ��AfterInit����
        if (!ExecuteAfterInitDuty(tLWActivitySchema, tInputData))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ�нڵ�����,ִ��AfterInit�������!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ��BeforeEnd����
        if (!ExecuteBeforeEndDuty(tLWActivitySchema, tInputData))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ�нڵ�����,ִ��BeforeEnd�������!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ��AfterEnd����
        if (!ExecuteAfterEndDuty(tLWActivitySchema, tInputData))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ�нڵ�����,ִ��AfterEnd�������!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ִ������ڵ�����
     * @param tLWMissionSchema LWMissionSchema
     * @param tInputData VData
     * @return boolean
     * @throws Exception
     */
    public boolean ExecuteDummyMission(LWMissionSchema tLWMissionSchema, VData tInputData)
            throws Exception
    {
        mResult.clear();
        //У��������Ч��
        //��ѯ����������켣��
        LWMissionDB tLWMissionDB = new LWMissionDB();
        LWMissionSet tLWMissionSet = new LWMissionSet();
        tLWMissionDB.setSchema(tLWMissionSchema);
        tLWMissionSet = tLWMissionDB.query();
        if (tLWMissionSet.size() > 0)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ������ڵ�����,��ѯ����������켣��LWMission����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��ѯָ����ڵ��(���������)
        LWActivityDB tLWActivityDB = new LWActivityDB();
        LWActivitySet tLWActivitySet = new LWActivitySet();
        tLWActivityDB.setActivityID(tLWMissionSchema.getActivityID());
        tLWActivitySet = tLWActivityDB.query();
        if (tLWActivitySet.size() != 1)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ������ڵ�����,��ѯ��������ڵ�����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        LWActivitySchema tLWActivitySchema = new LWActivitySchema();
        tLWActivitySchema.setSchema(tLWActivitySet.get(1));

        //ִ��BeforeInit����
        if (!ExecuteBeforeInitDuty(tLWActivitySchema, tInputData))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ������ڵ�����,ִ��BeforeInit�������!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ��AfterInit����
        if (!ExecuteAfterInitDuty(tLWActivitySchema, tInputData))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ������ڵ�����,ִ��AfterInit�������!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ��BeforeEnd����
        if (!ExecuteBeforeEndDuty(tLWActivitySchema, tInputData))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ������ڵ�����,ִ��BeforeEnd�������!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ��AfterEnd����
        if (!ExecuteAfterEndDuty(tLWActivitySchema, tInputData))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ������ڵ�����,ִ��AfterEnd�������!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ɾ���ڵ�����
     * @param tMissionID String
     * @param tSubMissionID String
     * @param tActivityID String
     * @param tInputData VData
     * @return boolean
     */
    public boolean DeleteMission(String tMissionID, String tSubMissionID, String tActivityID
            , VData tInputData) //throws Exception
    {

        //У��������Ч��
        GlobalInput tGlobalInput = new GlobalInput();
        if (tMissionID == null || tMissionID.equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ�нڵ�����,��û�д��뵱ǰ������ID��Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null || tSubMissionID.equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ�нڵ�����,��û�д��뵱ǰ��������ID��Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //У��������Ч��
        if (tActivityID == null || tActivityID.equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ�нڵ�����,��û�д��뵱ǰ����ID��Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��ѯ����������켣��
        LWMissionDB tLWMissionDB = new LWMissionDB();
        LWMissionSet tLWMissionSet = new LWMissionSet();
        tLWMissionDB.setMissionID(tMissionID);
        tLWMissionDB.setActivityID(tActivityID);
        tLWMissionDB.setSubMissionID(tSubMissionID);
        tLWMissionSet = tLWMissionDB.query();
        if (tLWMissionSet == null || tLWMissionSet.size() != 1)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ�нڵ�����,��ѯ����������켣��LWMission����!";
            this.mErrors.addOneError(tError);
            return false;
        }

        LWMissionSchema tLWMissionSchema = new LWMissionSchema();
        tLWMissionSchema = tLWMissionSet.get(1);

        //׼������ڵ㱸�ݱ�����
        LBMissionSchema mLBMissionSchema = new LBMissionSchema();
//        LWMissionSchema mLWMissionSchema = new LWMissionSchema();
        String tSerielNo = PubFun1.CreateMaxNo("MissionSerielNo", 10);

        mReflections.transFields(mLBMissionSchema, tLWMissionSchema);
        mLBMissionSchema.setSerialNo(tSerielNo);
        mLBMissionSchema.setActivityStatus("3"); //�ڵ�����ִ�����
        mLBMissionSchema.setLastOperator(tGlobalInput.Operator);
        mLBMissionSchema.setMakeDate(PubFun.getCurrentDate());
        mLBMissionSchema.setMakeTime(PubFun.getCurrentTime());

        if (tLWMissionSchema != null && mLBMissionSchema != null)
        {
            mResult.clear();
            MMap map = new MMap();
            map.put(tLWMissionSchema, "DELETE");
            map.put(mLBMissionSchema, "INSERT");
            mResult.add(map);
            return true;
        }
        else
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ�нڵ�����,��ѯ����������켣��LWMission����!";
            this.mErrors.addOneError(tError);
            return false;
        }
    }

    /**
     * ת��ڵ�����
     * @param tMissionID String
     * @param tInputData VData
     * @return LBMissionSchema
     * @throws Exception
     */
    public LBMissionSchema TranSaveMission(String tMissionID, VData tInputData)
            throws Exception
    {
        //mResult.clear();
        //У��������Ч��
        if (tMissionID == null || tMissionID.equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ�нڵ�����,��û�д��뵱ǰ����ID��Ϣ!";
            this.mErrors.addOneError(tError);
            return null;
        }

        //��ѯ����������켣��
        LWMissionDB tLWMissionDB = new LWMissionDB();
        LWMissionSet tLWMissionSet = new LWMissionSet();
        tLWMissionDB.setMissionID(tMissionID);
        tLWMissionSet = tLWMissionDB.query();
        if (tLWMissionSet.size() != 1)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ�нڵ�����,��ѯ����������켣��LWMission����!";
            this.mErrors.addOneError(tError);
            return null;
        }

        LWMissionSchema tLWMissionSchema = new LWMissionSchema();
        tLWMissionSchema.setSchema(tLWMissionSet.get(1));
        LBMissionSchema tLBMissionSchema = new LBMissionSchema();
        Reflections tReflections = new Reflections();
        tReflections.transFields(tLBMissionSchema, tLWMissionSchema);

        String tSerialNo = PubFun1.CreateMaxNo("MissionBID", 10);
        tLBMissionSchema.setSerialNo(tSerialNo);
        return tLBMissionSchema;
    }


    /**
     * ת������ڵ�����
     * @param tLWMissionSchema LWMissionSchema
     * @param tInputData VData
     * @return LBMissionSchema
     * @throws Exception
     */
    public LBMissionSchema TranSaveDummyMission(LWMissionSchema tLWMissionSchema, VData tInputData)
            throws Exception
    {
        //У��������Ч��
        //��ѯ��������������켣��
        LWMissionDB tLWMissionDB = new LWMissionDB();
        LWMissionSet tLWMissionSet = new LWMissionSet();
        tLWMissionDB.setMissionID(tLWMissionSchema.getMissionID());
        tLWMissionSet = tLWMissionDB.query();
        if (tLWMissionSet.size() > 0)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "TranSaveDummyMission";
            tError.errorMessage = "ת������ڵ�����,��ѯ����������켣��LWMission����!";
            this.mErrors.addOneError(tError);
            return null;
        }

        LBMissionSchema tLBMissionSchema = new LBMissionSchema();
        Reflections tReflections = new Reflections();
        tReflections.transFields(tLBMissionSchema, tLWMissionSchema);

        String tSerialNo = PubFun1.CreateMaxNo("MissionBID", 10);
        tLBMissionSchema.setSerialNo(tSerialNo);
        return tLBMissionSchema;
    }


    /**
     * ���ִ�нڵ�����ǰ�ĳ�ʼ����
     * @param tLWActivitySchema LWActivitySchema
     * @param tInputData VData
     * @return boolean
     * @throws Exception
     */
    private boolean ExecuteBeforeInitDuty(LWActivitySchema tLWActivitySchema, VData tInputData)
            throws Exception
    {
        //��ȡ��������
        GlobalInput tGlobalInput = new GlobalInput();
        TransferData tTransferData = new TransferData();
        tGlobalInput = (GlobalInput) tInputData.getObjectByObjectName("GlobalInput", 0);
        tTransferData = (TransferData) tInputData.getObjectByObjectName("TransferData", 0);

        //У��������Ч��
        if (tLWActivitySchema == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ�нڵ�����,��û�д��뵱ǰ����ID��Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ��BeforeInit����
        if (tLWActivitySchema.getBeforeInitType() == null ||
                tLWActivitySchema.getBeforeInitType().trim().equals(""))
        {
            //��----��ʾû����Ҫִ�еĶ�����
            return true;
        }
        else if (tLWActivitySchema.getBeforeInitType().equals("0"))
        {
            //0 -- Ĭ�ϣ���ʾ����ΪWhere�Ӿ䡣
            PubCalculator tPubCalculator = new PubCalculator();
            //׼������Ҫ��
            Vector tVector = tTransferData.getValueNames();
            for (int i = 0; i < tVector.size(); i++)
            {
                String tName = (String) tVector.get(i);
                String tValue = tTransferData.getValueByName((Object) tName).toString();
                tPubCalculator.addBasicFactor(tName, tValue);
            }
            //׼������SQL
            tPubCalculator.setCalSql(tLWActivitySchema.getBeforeInit());
            String strSQL = tPubCalculator.calculate();
//            System.out.println("SQL : " + strSQL);
            if (strSQL == null || strSQL.trim().equals(""))
            {
                return true;
            }
            else
            {
                //�ֽ��������,����ִ��
                try
                {
                    String strTemp = "";
                    char cTemp = ' ';
                    for (int nIndex = 0; nIndex < strSQL.length(); nIndex++)
                    {
                        cTemp = strSQL.charAt(nIndex);
                        if (cTemp == ';' && !strTemp.equals(""))
                        {
//                            System.out.println(strTemp);
//                            String tReSult = new String();
                            ExeSQL tExeSQL = new ExeSQL();
                            tExeSQL.execUpdateSQL(strTemp); //���ִ�нڵ�����ǰ�ĳ�ʼ����ֻ��һЩInsert||Delete||Update���,�����޷������ݼ���
                            if (tExeSQL.mErrors.needDealError())
                            {
                                // @@������
                                //this.mErrors.copyAllErrors(tExeSQL.mErrors);
                                CError tError = new CError();
                                tError.moduleName = "ActivityOperator";
                                tError.functionName = "ExecuteBeforeInitDuty";
                                tError.errorMessage = "ִ��SQL��䣺" + strTemp +
                                        "ʧ��!";
                                this.mErrors.addOneError(tError);
                                return false;
                            }
                            strTemp = "";
                        }
                        else
                        {
                            strTemp += String.valueOf(cTemp);
                        }
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    return false;
                }
            }
            return true;
        }
        else if (tLWActivitySchema.getBeforeInitType().equals("1"))
        {
            //1 -- ��ʾת������Ϊһ��������ࡣ
            try
            {
                Class tClass = Class.forName(tLWActivitySchema.getBeforeInit());
                BeforeInitService tBeforeInitService = (BeforeInitService)
                        tClass.newInstance();

                // ׼������
                String strOperate = tLWActivitySchema.getActivityID();
                tInputData.add(tLWActivitySchema);
                if (!tBeforeInitService.submitData(tInputData, strOperate))
                {
                    // @@������
                    this.mErrors.copyAllErrors(tBeforeInitService.getErrors());
                    CError tError = new CError();
                    tError.moduleName = "ActivityOperator";
                    tError.functionName = "ExecuteBeforeInitDuty";

                    tError.errorMessage = "ִ��ִ�нڵ�����BeforeInitʧ��!";
                    this.mErrors.addOneError(tError);
                    return false;
                }
                //������ύ����
                VData tVData = new VData();
                tVData = tBeforeInitService.getResult();
                if (tVData != null && tVData.size() > 0)
                {
                    mResult.add(tVData);
                }

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                return false;
            }
        }

        return true;
    }


    /**
     * ���ִ�нڵ�����ĺ�������
     * @param tLWActivitySchema LWActivitySchema
     * @param tInputData VData
     * @return boolean
     * @throws Exception
     */
    private boolean ExecuteAfterInitDuty(LWActivitySchema tLWActivitySchema, VData tInputData)
            throws Exception
    {
        //��ȡ��������
        GlobalInput tGlobalInput = new GlobalInput();
        TransferData tTransferData = new TransferData();
        tGlobalInput = (GlobalInput) tInputData.getObjectByObjectName("GlobalInput", 0);
        tTransferData = (TransferData) tInputData.getObjectByObjectName("TransferData", 0);

        //У��������Ч��
        if (tLWActivitySchema == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ�нڵ�����,��û�д��뵱ǰ����ID��Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ��AfterInit����
        if (tLWActivitySchema.getAfterInitType() == null ||
                tLWActivitySchema.getAfterInitType().trim().equals(""))
        {
            //��----��ʾû����Ҫִ�еĶ�����
            return true;
        }
        else if (tLWActivitySchema.getAfterInitType().equals("0"))
        {
            //0 -- Ĭ�ϣ���ʾ����ΪWhere�Ӿ䡣
            PubCalculator tPubCalculator = new PubCalculator();
            //׼������Ҫ��
            Vector tVector = tTransferData.getValueNames();
            for (int i = 0; i < tVector.size(); i++)
            {
                String tName = (String) tVector.get(i);
                String tValue = tTransferData.getValueByName((Object) tName).toString();
                tPubCalculator.addBasicFactor(tName, tValue);
            }
            //׼������SQL
            tPubCalculator.setCalSql(tLWActivitySchema.getAfterInit());
            String strSQL = tPubCalculator.calculate();
//            System.out.println("SQL : " + strSQL);
            if (strSQL == null || strSQL.trim().equals(""))
            {
                return true;
            }
            else
            {
                //�ֽ��������,����ִ��
                //Statement stmt = conn.createStatement();
                try
                {
                    String strTemp = "";
                    char cTemp = ' ';
                    for (int nIndex = 0; nIndex < strSQL.length(); nIndex++)
                    {
                        cTemp = strSQL.charAt(nIndex);
                        if (cTemp == ';' && !strTemp.equals(""))
                        {
//                            System.out.println(strTemp);
//                            String tReSult = new String();
                            ExeSQL tExeSQL = new ExeSQL();
                            tExeSQL.execUpdateSQL(strTemp); //���ִ�нڵ�����ĺ�������ֻ��һЩInsert||Delete||Update���,�����޷������ݼ���
                            if (tExeSQL.mErrors.needDealError())
                            {
                                // @@������
                                this.mErrors.copyAllErrors(tExeSQL.mErrors);
                                CError tError = new CError();
                                tError.moduleName = "ActivityOperator";
                                tError.functionName = "ExecuteAfterInitDuty";
                                tError.errorMessage = "ִ��SQL��䣺" + strTemp +
                                        "ʧ��!";
                                this.mErrors.addOneError(tError);
                                return false;
                            }
                            strTemp = "";
                        }
                        else
                        {
                            strTemp += String.valueOf(cTemp);
                        }
                    }
                    //stmt.close();
                }
                catch (Exception ex)
                {
                    //stmt.close();
                    ex.printStackTrace();
                    return false;
                }
            }
            return true;
        }
        else if (tLWActivitySchema.getAfterInitType().equals("1"))
        {
            //1 -- ��ʾת������Ϊһ��������ࡣ
            try
            {
                Class tClass = Class.forName(tLWActivitySchema.getAfterInit());
                AfterInitService tAfterInitService = (AfterInitService) tClass.
                        newInstance();

                // ׼������
                String strOperate = tLWActivitySchema.getActivityID();
                tInputData.add(tLWActivitySchema);
                if (!tAfterInitService.submitData(tInputData, strOperate))
                {
                    // @@������
                    this.mErrors.copyAllErrors(tAfterInitService.getErrors());
//			CError tError =new CError();
//			tError.moduleName="ActivityOperator";
//			tError.functionName="ExecuteAfterInitDuty";
//			tError.errorMessage="ִ��ִ�нڵ�����AfterInitʧ��!";
//			this.mErrors .addOneError(tError) ;
                    return false;
                }

                //������ύ����
                VData tVData = new VData();
                tVData = tAfterInitService.getResult();
                if (tVData != null && tVData.size() > 0)
                {
                    mResult.add(tVData);
                }

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                return false;
            }
        }

        return true;
    }


    /**
     * ���ִ�нڵ�����ĺ����������ƺ�����
     * @param tLWActivitySchema LWActivitySchema
     * @param tInputData VData
     * @return boolean
     * @throws Exception
     */
    private boolean ExecuteBeforeEndDuty(LWActivitySchema tLWActivitySchema, VData tInputData)
            throws Exception
    {
        //��ȡ��������
        GlobalInput tGlobalInput = new GlobalInput();
        TransferData tTransferData = new TransferData();
        tGlobalInput = (GlobalInput) tInputData.getObjectByObjectName("GlobalInput", 0);
        tTransferData = (TransferData) tInputData.getObjectByObjectName("TransferData", 0);
        //У��������Ч��
        if (tLWActivitySchema == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteBeforeEndDuty";
            tError.errorMessage = "ִ�нڵ�����,��û�д��뵱ǰ������Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ��BeforeEnd����
        if (tLWActivitySchema.getBeforeEndType() == null ||
                tLWActivitySchema.getBeforeEndType().trim().equals(""))
        {
            //��----��ʾû����Ҫִ�еĶ�����
            return true;
        }
        else if (tLWActivitySchema.getBeforeEndType().equals("0"))
        {
            //0 -- Ĭ�ϣ���ʾ����ΪWhere�Ӿ䡣
            PubCalculator tPubCalculator = new PubCalculator();
            //׼������Ҫ��
            Vector tVector = tTransferData.getValueNames();
            for (int i = 0; i < tVector.size(); i++)
            {
                String tName = (String) tVector.get(i);
                String tValue = tTransferData.getValueByName((Object) tName).toString();
                tPubCalculator.addBasicFactor(tName, tValue);
            }
            //׼������SQL
            tPubCalculator.setCalSql(tLWActivitySchema.getBeforeEnd());
            String strSQL = tPubCalculator.calculate();
//            System.out.println("SQL : " + strSQL);
            if (strSQL == null || strSQL.trim().equals(""))
            {
                return true;
            }
            else
            {
                //�ֽ��������,����ִ��
                try
                {
                    String strTemp = "";
                    char cTemp = ' ';
                    for (int nIndex = 0; nIndex < strSQL.length(); nIndex++)
                    {
                        cTemp = strSQL.charAt(nIndex);
                        if (cTemp == ';' && !strTemp.equals(""))
                        {
//                            System.out.println(strTemp);
//                            String tReSult = new String();
                            ExeSQL tExeSQL = new ExeSQL();
                            tExeSQL.execUpdateSQL(strTemp); //���ִ�нڵ�����ĺ����������ƺ�����ֻ��һЩInsert||Delete||Update���,�����޷������ݼ���
                            if (tExeSQL.mErrors.needDealError())
                            {
                                // @@������
                                //this.mErrors.copyAllErrors(tExeSQL.mErrors);
                                CError tError = new CError();
                                tError.moduleName = "ActivityOperator";
                                tError.functionName = "ExecuteBeforeEndDuty";
                                tError.errorMessage = "ִ��SQL��䣺" + strTemp +
                                        "ʧ��!";
                                this.mErrors.addOneError(tError);
                                return false;
                            }
                            strTemp = "";
                        }
                        else
                        {
                            strTemp += String.valueOf(cTemp);
                        }
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    return false;
                }
            }
            return true;
        }
        else if (tLWActivitySchema.getBeforeEndType().equals("1"))
        {
            //1 -- ��ʾת������Ϊһ��������ࡣ
            try
            {
                Class tClass = Class.forName(tLWActivitySchema.getBeforeEnd());
                BeforeEndService tBeforeEndService = (BeforeEndService) tClass.
                        newInstance();

                // ׼������
                String strOperate = tLWActivitySchema.getActivityID();
                tInputData.add(tLWActivitySchema);
                if (!tBeforeEndService.submitData(tInputData, strOperate))
                {
                    // @@������
                    this.mErrors.copyAllErrors(tBeforeEndService.getErrors());
                    CError tError = new CError();
                    tError.moduleName = "ActivityOperator";
                    tError.functionName = "ExecuteBeforeEndDuty";
                    tError.errorMessage = "ִ��ִ�нڵ�����BeforeEndʧ��!";
                    this.mErrors.addOneError(tError);
                    return false;
                }
                //������ύ����
                VData tVData = new VData();
                tVData = tBeforeEndService.getResult();
                if (tVData != null && tVData.size() > 0)
                {
                    mResult.add(tVData);
                }

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                return false;
            }
        }
        return true;
    }


    /**
     * ���ִ�нڵ���������������
     * @param tLWActivitySchema LWActivitySchema
     * @param tInputData VData
     * @return boolean
     * @throws Exception
     */
    private boolean ExecuteAfterEndDuty(LWActivitySchema tLWActivitySchema, VData tInputData)
            throws Exception
    {
        //��ȡ��������
        GlobalInput tGlobalInput = new GlobalInput();
        TransferData tTransferData = new TransferData();
        tGlobalInput = (GlobalInput) tInputData.getObjectByObjectName("GlobalInput", 0);
        tTransferData = (TransferData) tInputData.getObjectByObjectName("TransferData", 0);
        //У��������Ч��
        if (tLWActivitySchema == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "ExecuteMission";
            tError.errorMessage = "ִ�нڵ�����,��û�д��뵱ǰ����ID��Ϣ!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ��AfterEnd����
        if (tLWActivitySchema.getAfterEndType() == null ||
                tLWActivitySchema.getAfterEndType().trim().equals(""))
        {
            //��----��ʾû����Ҫִ�еĶ�����
            return true;
        }
        else if (tLWActivitySchema.getAfterEndType().equals("0"))
        {
            //0 -- Ĭ�ϣ���ʾ����ΪWhere�Ӿ䡣
            PubCalculator tPubCalculator = new PubCalculator();
            //׼������Ҫ��
            Vector tVector = tTransferData.getValueNames();
            for (int i = 0; i < tVector.size(); i++)
            {
                String tName = (String) tVector.get(i);
                String tValue = tTransferData.getValueByName((Object) tName).toString();
                tPubCalculator.addBasicFactor(tName, tValue);
            }
            //׼������SQL
            tPubCalculator.setCalSql(tLWActivitySchema.getAfterInit());
            String strSQL = tPubCalculator.calculate();
//            System.out.println("SQL : " + strSQL);
            if (strSQL == null || strSQL.trim().equals(""))
            {
                return true;
            }
            else
            {
                //�ֽ��������,����ִ��
                //Statement stmt = conn.createStatement();
                try
                {
                    String strTemp = "";
                    char cTemp = ' ';
                    for (int nIndex = 0; nIndex < strSQL.length(); nIndex++)
                    {
                        cTemp = strSQL.charAt(nIndex);
                        if (cTemp == ';' && !strTemp.equals(""))
                        {
//                            System.out.println(strTemp);
//                            String tReSult = new String();
                            ExeSQL tExeSQL = new ExeSQL();
                            tExeSQL.execUpdateSQL(strTemp); //���ִ�нڵ�����ĺ�������ֻ��һЩInsert||Delete||Update���,�����޷������ݼ���
                            if (tExeSQL.mErrors.needDealError())
                            {
                                // @@������
                                this.mErrors.copyAllErrors(tExeSQL.mErrors);
                                CError tError = new CError();
                                tError.moduleName = "ActivityOperator";
                                tError.functionName = "ExecuteAfterInitDuty";
                                tError.errorMessage = "ִ��SQL��䣺" + strTemp +
                                        "ʧ��!";
                                this.mErrors.addOneError(tError);
                                return false;
                            }
                            strTemp = "";
                        }
                        else
                        {
                            strTemp += String.valueOf(cTemp);
                        }
                    }
                    //stmt.close();
                }
                catch (Exception ex)
                {
                    //stmt.close();
                    ex.printStackTrace();
                    return false;
                }
            }
            return true;
        }
        else if (tLWActivitySchema.getAfterEndType().equals("1"))
        {
            //1 -- ��ʾת������Ϊһ��������ࡣ
            try
            {
                Class tClass = Class.forName(tLWActivitySchema.getAfterEnd());
                AfterEndService tAfterEndService = (AfterEndService) tClass.
                        newInstance();

                // ׼������
                String strOperate = tLWActivitySchema.getActivityID();
                tInputData.add(tLWActivitySchema);
                if (!tAfterEndService.submitData(tInputData, strOperate))
                {
                    // @@������
                    this.mErrors.copyAllErrors(tAfterEndService.getErrors());
                    CError tError = new CError();
                    tError.moduleName = "ActivityOperator";
                    tError.functionName = "ExecuteAfterEndDuty";
                    tError.errorMessage = "ִ��ִ�нڵ�����AfterEndʧ��!";
                    this.mErrors.addOneError(tError);
                    return false;
                }
                //������ύ����
                VData tVData = new VData();
                tVData = tAfterEndService.getResult();
                if (tVData != null && tVData.size() > 0)
                {
                    mResult.add(tVData);
                }

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                return false;
            }
        }

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

    /*
     * ����һ�����������е�����ڵ�
     * �ڸ÷�������Ҫ���2�����飺
     *  1������һ���������µ��������
     *  2���Ը��´�����������ϡ�����A.Finished��ʼ����(��ʱȡ��)
     * ִ����ɺ󣬲���һ��VData�������ΪBLS�ı�����׼����--YT
     * @param tProcessID String
     * @param tActivityID String
     * @param tInputData VData
     * @return LWMissionSchema
     * @add by tuqiang
     */
    public LWMissionSchema CreateOnlyOneMission(String tProcessID, String tActivityID
            , VData tInputData)
    {
        //��ȡǰ̨����
        GlobalInput tGlobalInput = new GlobalInput();
        TransferData tTransferData = new TransferData();
        tGlobalInput = (GlobalInput) tInputData.getObjectByObjectName("GlobalInput", 0);
        tTransferData = (TransferData) tInputData.getObjectByObjectName("TransferData", 0);
        String tmissionid = (String) tTransferData.getValueByName("MissionID");
        LWMissionSchema tLWMissionSchema = new LWMissionSchema();

        LWMissionDB mLWMissionDB = new LWMissionDB();
        LWMissionSet mLWMissionSet = new LWMissionSet();

        mLWMissionDB.setMissionID(tmissionid);
        mLWMissionSet = mLWMissionDB.query();
        if (mLWMissionSet.size() == 0)
        {
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage = "�������ڵ��ѯʧ��!";
            this.mErrors.addOneError(tError);
            return null;
        }
        mLWMissionDB.setActivityID(tActivityID);
        mLWMissionSet = mLWMissionDB.query();
        if (mLWMissionSet.size() != 0)
        {
            return null;
        }
        //У��������Ч��
        if (tProcessID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage = "����һ����������ָ�����������,��û�д��빤����������Ϣ!";
            this.mErrors.addOneError(tError);
            return null;
        }
        if (tActivityID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage = "����һ����������ָ�����������,��û�д��빤������ڵ���Ϣ!";
            this.mErrors.addOneError(tError);
            return null;
        }
        //��ѯ���������̱�
        LWProcessDB tLWProcessDB = new LWProcessDB();
        LWProcessSet tLWProcessSet = new LWProcessSet();
        tLWProcessDB.setProcessID(tProcessID);
        tLWProcessSet = tLWProcessDB.query();
        if (tLWProcessSet.size() != 1)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage = "����һ����������ָ�����������,������Ĺ�����������Ϣ����!";
            this.mErrors.addOneError(tError);
            return null;
        }

        //��ѯĬ�Ϲ��������ڵ��(���������)
        LWActivityDB tLWActivityDB = new LWActivityDB();
        LWActivitySet tLWActivitySet = new LWActivitySet();
        tLWActivityDB.setActivityID(tActivityID);
        tLWActivitySet = tLWActivityDB.query();
        if (tLWActivitySet == null || tLWActivitySet.size() != 1)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLWActivitySet.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage = "����һ����������ָ�����������,��ѯ�������ڵ�����!";
            this.mErrors.addOneError(tError);
            return null;
        }

        //У�鴫�����
        LWFieldMapDB tLWFieldMapDB = new LWFieldMapDB();
        LWFieldMapSet tLWFieldMapSet = new LWFieldMapSet();
        tLWFieldMapDB.setActivityID(tLWActivitySet.get(1).getActivityID());
        tLWFieldMapSet = tLWFieldMapDB.query();
        if (tLWFieldMapSet.size() != 0 && tTransferData == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ActivityOperator";
            tError.functionName = "CreateNewMission";
            tError.errorMessage =
                    "����һ����������ָ�����������,���빤�����������������ֶθ������ڸù�������ľ����ֶ�ӳ����м�¼����!";
            this.mErrors.addOneError(tError);
            return null;
        }

        //�����������������,׼������
        tLWMissionSchema.setMissionID(tmissionid);
        tLWMissionSchema.setSubMissionID("1");
        tLWMissionSchema.setProcessID(tLWProcessSet.get(1).getProcessID());
        tLWMissionSchema.setActivityID(tLWActivitySet.get(1).getActivityID());
        tLWMissionSchema.setActivityStatus("1"); //0 -- ��������У����״̬�ʺ���һ��������һϵ�ж�����������ɺ�����ύ��ҵ�������屣�����룬���ڵ�����Ҫһ����ʱ�䣬�����ڵ�������л���ָ�״̬����1 -- ���������ϴ�����2 -- �����У�3 -- ������ɣ�4 -- ��ͣ
        //׼�������ֶ�
        for (int i = 1; i <= tLWFieldMapSet.size(); i++)
        {
            if (tLWFieldMapSet.get(i).getSourFieldName() == null ||
                    tLWFieldMapSet.get(i).getSourFieldName().equals(""))
            {
                // @@������
                //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                CError tError = new CError();
                tError.moduleName = "ActivityOperator";
                tError.functionName = "CreateNewMission";
                tError.errorMessage =
                        "����һ����������ָ�����������,��������ľ����ֶ�ӳ����м�¼SourFieldName�ֶ���������!";
                this.mErrors.addOneError(tError);
                return null;
            }

            if (tLWFieldMapSet.get(i).getDestFieldName() == null ||
                    tLWFieldMapSet.get(i).getDestFieldName().equals(""))
            {
                // @@������
                //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                CError tError = new CError();
                tError.moduleName = "ActivityOperator";
                tError.functionName = "CreateNewMission";
                tError.errorMessage =
                        "����һ����������ָ�����������,��������ľ����ֶ�ӳ����м�¼SourFieldName�ֶ���������!";
                this.mErrors.addOneError(tError);
                return null;
            }
            if (tTransferData.getValueByName(tLWFieldMapSet.get(i).
                    getSourFieldName()) == null)
            {
                // @@������
                //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                CError tError = new CError();
                tError.moduleName = "ActivityOperator";
                tError.functionName = "CreateNewMission";
                tError.errorMessage = "����һ����������ָ�����������,���빤�����������������ֶ���Ϣ����!";
                this.mErrors.addOneError(tError);
                return null;
            }

            String tMissionProp = (String) tTransferData.getValueByName(tLWFieldMapSet.get(i).
                    getSourFieldName());
            String tDestFieldName = tLWFieldMapSet.get(i).getDestFieldName();

            tLWMissionSchema.setV(tDestFieldName, tMissionProp);

        }
        tLWMissionSchema.setDefaultOperator(tGlobalInput.Operator);
        tLWMissionSchema.setLastOperator(tGlobalInput.Operator);
        tLWMissionSchema.setCreateOperator(tGlobalInput.Operator);
        tLWMissionSchema.setMakeDate(PubFun.getCurrentDate());
        tLWMissionSchema.setMakeTime(PubFun.getCurrentTime());
        tLWMissionSchema.setModifyDate(PubFun.getCurrentDate());
        tLWMissionSchema.setModifyTime(PubFun.getCurrentTime());

//        System.out.println("ok!:");
        return tLWMissionSchema;
    }

    /**
     * ���Ժ���
     * @param args String[]
     */
    public static void main(String[] args)
    {

        VData tVData = new VData();
        GlobalInput mGlobalInput = new GlobalInput();
        TransferData mTransferData = new TransferData();

        /** ȫ�ֱ��� */
        mGlobalInput.Operator = "Admin";
        mGlobalInput.ComCode = "asd";
        mGlobalInput.ManageCom = "sdd";
        /** ���ݱ��� */
        mTransferData.setNameAndValue("MissionID", "00000000000000000001");
        mTransferData.setNameAndValue("Default2", "1");
        /**�ܱ���*/
        tVData.add(mGlobalInput);
        tVData.add(mTransferData);

        ActivityOperator tActivityOperator = new ActivityOperator();
        //��������1:ok
        //LWMissionSchema tLWMissionSchema = new LWMissionSchema();
        //tLWMissionSchema = tActivityOperator.CreateStartMission("0000000000","0000000000",tVData);
        //��������2:ok
        //LWMissionSchema tLWMissionSchema = new LWMissionSchema();
        //tLWMissionSchema = tActivityOperator.CreateStartMission("0000000000",tVData);

        ////��������3:ת������ΪSQL ok
//	LWMissionSchema tLWMissionSchema = new LWMissionSchema();
//	try{
//	if( tActivityOperator.CreateNextMission("00000000000000000001","","0000000000","0000000001",tVData))
//	{
//	  tLWMissionSchema = (LWMissionSchema)tActivityOperator.getResult().get(0) ;
//	}
//	}
//   catch (Exception ex) {
//		ex.printStackTrace();
//		 }

        ////��������4:ת������ΪCLASS ok
        LWMissionSet tLWMissionSet = new LWMissionSet();
        try
        {
            if (tActivityOperator.CreateNextMission("00000000000000000001", "",
                    "0000000000", tVData))
            {
                tLWMissionSet = (LWMissionSet) tActivityOperator.getResult().
                        get(0);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        //��������5:ִ�й����ڵ�����
//	LWMissionSchema tLWMissionSchema = new LWMissionSchema();
//	try{
//	  if(tActivityOperator.ExecuteMission("0000000000",tVData))
//	   {
//		   VData  tResult = new VData();
//	       tResult = tActivityOperator.getResult() ;
//	   }
//	}
//   catch (Exception ex) {
//		ex.printStackTrace();
//		 }

        int i = 1;
        //tActivityOperator.ActivityFinished() ;
    }
}

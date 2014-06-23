/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LWMissionDB;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.vschema.LDSpotTrackSet;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;
import com.sinosoft.lis.sms.*;
/**
 * <p>Title: ����Լ������ </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */
public class TbWorkFlowBL
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();

    /** �����洫�����ݵ����� */
    private VData mInputData;

    /** �����洫�����ݵ����� */
    private VData mResult = new VData();

    /** ��ǰ̨�������ݵ����� */
    private VData tResult = new VData();

    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();

    //private VData mIputData = new VData();
    private TransferData mTransferData = new TransferData();

    /**���������� */
    ActivityOperator mActivityOperator = new ActivityOperator();

    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;
    private String mOperate;

    /**�Ƿ��ύ��־**/
    private String flag;
    private boolean mFlag = true;

    public TbWorkFlowBL()
    {
    }

    /**
     * �������ݵĹ�������
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {

        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData(cInputData, cOperate))
        {
            return false;
        }
//        System.out.println("---TbWorkFlowBL getInputData---");
        // ���ݲ���ҵ����
        if (!dealData())
        {
            return false;
        }
//        System.out.println("---TbWorkFlowBL dealData---");

        //׼������̨������
        if (!prepareOutputData())
        {
            return false;
        }

//        System.out.println("---TbWorkFlowBL prepareOutputData---");

        if (mFlag)
        {
            //�������Ӧ�ı�־λ�����ύ
            //�����ύ
            TbWorkFlowBLS tTbWorkFlowBLS = new TbWorkFlowBLS();
//            System.out.println("Start TbWorkFlowBL Submit...");
            boolean a=true;
            if (!tTbWorkFlowBLS.submitData(mResult, mOperate))
            {
                // @@������
                this.mErrors.copyAllErrors(tTbWorkFlowBLS.mErrors);

                CError tError = new CError();
                tError.moduleName = "TbWorkFlowBL";
                tError.functionName = "submitData";
                tError.errorMessage = "�����ύʧ��!";
                this.mErrors.addOneError(tError);

                return false;
            }
            else
            {
                if(mOperate.equals("0000001150"))
                {
                  LCContSet toneLCContSet=new LCContSet();
                  LCContSet ttwoLCContSet=new LCContSet();
                  toneLCContSet = (LCContSet) cInputData.getObjectByObjectName("LCContSet", 0);
                  LCContDB tLCContDB=new LCContDB();
                  tLCContDB.setProposalContNo(toneLCContSet.get(1).getContNo());
                  ttwoLCContSet=tLCContDB.query();
                  LXCalculator lxcalculator = new LXCalculator();
                  VData tVData = new VData();
                  TransferData tTransferData = new TransferData();
                  tTransferData.setNameAndValue("ItemCode", "TBGX0001");
                  tTransferData.setNameAndValue("SendDate", PubFun.getCurrentDate());
                  tTransferData.setNameAndValue("ContNo", ttwoLCContSet.get(1).getContNo());
                  tVData.add(mGlobalInput);
                  tVData.add(tTransferData);
                  lxcalculator.submitData(tVData, "send");
                }
                /*
                if(mOperate.equals("0000001104"))
                {
                  LCContSet toneLCContSet=new LCContSet();
                  LCContSet ttwoLCContSet=new LCContSet();
                  toneLCContSet = (LCContSet) cInputData.getObjectByObjectName("LCContSet", 0);
                  LCContDB tLCContDB=new LCContDB();
                  tLCContDB.setProposalContNo(toneLCContSet.get(1).getContNo());
                  ttwoLCContSet=tLCContDB.query();
                  LXCalculator lxcalculator = new LXCalculator();
                  VData tVData = new VData();
                  TransferData tTransferData = new TransferData();
                  tTransferData.setNameAndValue("ItemCode", "LIS00020005");
                  tTransferData.setNameAndValue("SendDate", PubFun.getCurrentDate()+1);
                  tTransferData.setNameAndValue("ContNo", ttwoLCContSet.get(1).getContNo());
                  tVData.add(mGlobalInput);
                  tVData.add(tTransferData);
                  lxcalculator.submitData(tVData, "send");
                }
                */
            }
        }
        /*
                 if (!CheckDraw(mTransferData)) {
          TbWorkFlowUI tTbWorkFlowUI = new TbWorkFlowUI();
          if (!tTbWorkFlowUI.submitData(cInputData, "0000001001")) {
            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "submitData";
            tError.errorMessage = "���зǳ��ֱ�Ӹ���ʧ��!";
            this.mErrors.addOneError(tError);

          }

                 }
         */
        return true;
    }

    /**
     * �����������еõ����ж���
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    private boolean getInputData(VData cInputData, String cOperate)
    {
        //�����������еõ����ж���
        //���ȫ�ֹ�������
        mGlobalInput = ((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ʧ��!";
            this.mErrors.addOneError(tError);

            return false;
        }

        //��ò���Ա����
        mOperater = mGlobalInput.Operator;
        if ((mOperater == null) || mOperater.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
            this.mErrors.addOneError(tError);

            return false;
        }

        //��õ�½��������
        mManageCom = mGlobalInput.ManageCom;
        if ((mManageCom == null) || mManageCom.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
            this.mErrors.addOneError(tError);

            return false;
        }

        mOperate = cOperate;
        if ((mOperate == null) || mOperate.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operate����ڵ����ʧ��!";
            this.mErrors.addOneError(tError);

            return false;
        }
        flag = (String) mTransferData.getValueByName("flag");
        if (flag != null)
        {
            if (flag.equals("N"))
            {
                mFlag = false;
            }
        }

        return true;
    }

    /**
     * ���ݲ�����ҵ����
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean dealData()
    {
        //�б���������ӡ�˱�֪ͨ����
        if (mOperate.trim().equals("7999999999"))
        {
            if (!Execute7999999999())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        else if (mOperate.trim().equals("0000001100"))
        {
            //ִ�й��������˹��˱��������
            if (!Execute0000001100())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        else if (mOperate.trim().equals("7599999999"))
        {
            //ִ�й��������˹��˱��������
            if (!Execute7599999999())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }

        else if (mOperate.trim().equals("7899999999"))
        {
            if (!Execute7899999999())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        else if (mOperate.trim().equals("7799999999"))
        {
            if (!Execute7799999999())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        else if (mOperate.trim().equals("7099999999"))
        {
            //ִ�г�������ڵ�
            if (!Execute7099999999())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        else if (mOperate.trim().equals("9999991061"))
        {
            if (!Execute9999991061())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        /*
        else if (mOperate.trim().equals("0000001123") || mOperate.trim().equals("0000001124")|| mOperate.trim().equals("0000001125"))
        {
            if (!Execute000000112345(mOperate))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        */
        else
        {
            //ִ�гб����������˱�֪ͨ��������
            if (!Execute())
            {
                // @@������
                //this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }

        return true;
    }

    /**
     * ִ�гб����������˹��˱��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean Execute()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();

        //��õ�ǰ�������������ID������û��������ݵ��µ����⣬�ҹ�����
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "Execute0000000000";
            tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);

            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "Execute0000000000";
            tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);

            return false;
        }

        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, mOperate, mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
            //���ִ�гб����������˹��˱��������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    mResult.add(tempVData);
                }
            }

            //����ִ����б����������˹��˱��������������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, mOperate, mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if ((tempVData != null) && (tempVData.size() > 0))
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }

            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, mOperate, mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if ((tempVData != null) && (tempVData.size() > 0))
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "����������ִ������Լ����������!";
            this.mErrors.addOneError(tError);

            return false;
        }
        return true;
    }

    /**
     * ִ�гб����������˹��˱��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean Execute0000001100()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "Execute0000000100";
            tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "Execute0000001100";
            tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000001100"
                    , mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "TbWorkFlowBL";
                //tError.functionName = "Execute0000000000";
                //tError.errorMessage = "����������ִ�гб����������˹��˱�����������!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ�гб����������˹��˱��������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    for (int j = 0; j < tempVData.size(); j++)
                    {
                        mResult.add(tempVData.get(i)); //ȡ��Mapֵ
                    }
                }
            }

            //����ִ����б����������˹��˱��������������ڵ�

            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000001100"
                    , mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "����������ִ�гб����������˹��˱�����������!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * ������ʼ����ڵ�
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean Execute7999999999()
    {
        mResult.clear();
        VData tVData = new VData();
        //�ŵ�¼�����У��
        FirstWorkFlowCheck tFirstWorkFlowCheck = new
                FirstWorkFlowCheck();

        if (tFirstWorkFlowCheck.submitData(mInputData, ""))
        {
            tVData = tFirstWorkFlowCheck.getResult();
            mResult.add(tVData);
        }
        else
        {
            this.mErrors.copyAllErrors(tFirstWorkFlowCheck.mErrors);
            return false;
        }

        ActivityOperator tActivityOperator = new ActivityOperator();
        //ִ�з������֪ͨ����������(������������ִ������Ϊͬһ����ʱ,����ִ����������ģʽ����)
        try
        {
//            System.out.println("ActivityOperator name:" +                    mActivityOperator.getClass());

            //����ִ���귢�����֪ͨ����������һ��ӡ���֪ͨ������ڵ�
//            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
            if (tActivityOperator.CreateStartMission("0000000003", "0000001001",
                    mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                mResult.add(tempVData);
                tempVData = null;
            }
            else
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "TbWorkFlowBL";
                //tError.functionName = "Execute9999999999";
                //tError.errorMessage = "���������湤�������쳣!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }
        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "Execute7999999999";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * ������ʼ����ڵ�
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean Execute7599999999()
    {
        mResult.clear();
//        VData tVData = new VData();
//        System.out.println("Execute 7599999999");
        ActivityOperator tActivityOperator = new ActivityOperator();
        //ִ�з������֪ͨ����������(������������ִ������Ϊͬһ����ʱ,����ִ����������ģʽ����)
        try
        {
//            System.out.println("ActivityOperator name:" +                    mActivityOperator.getClass());

            //����ִ���귢�����֪ͨ����������һ��ӡ���֪ͨ������ڵ�
//            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
            if (tActivityOperator.CreateStartMission("0000000003", "0000001061", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                mResult.add(tempVData);
                tempVData = null;
            }
            else
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "TbWorkFlowBL";
                //tError.functionName = "Execute9999999999";
                //tError.errorMessage = "���������湤�������쳣!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }
        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "Execute7899999999";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;

    }

    /**
     * ������ʼ����ڵ�
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean Execute7899999999()
    {
        mResult.clear();
//        VData tVData = new VData();
//        System.out.println("Excute 78999999999");
        ActivityOperator tActivityOperator = new ActivityOperator();
        //ִ�з������֪ͨ����������(������������ִ������Ϊͬһ����ʱ,����ִ����������ģʽ����)
        try
        {
//            System.out.println("ActivityOperator name:" +                    mActivityOperator.getClass());

            //����ִ���귢�����֪ͨ����������һ��ӡ���֪ͨ������ڵ�
//            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
//            tLWMissionSchema = tActivityOperator.CreateOneMission("0000000003",                    "0000001099", mInputData);
            tActivityOperator.CreateOneMission("0000000003", "0000001099", mInputData);
//            System.out.println("prtno ==" + tLWMissionSchema.getMissionProp1());

//            if (tActivityOperator.CreateStartMission("0000000003", "0000001099",
//                    mInputData))
//            {
//                VData tempVData = new VData();
//                tempVData = tActivityOperator.getResult();
//                mResult.add(tempVData);
//                tempVData = null;
//            }
//            else
//            {
//                // @@������
//                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//                //CError tError = new CError();
//                //tError.moduleName = "TbWorkFlowBL";
//                //tError.functionName = "Execute9999999999";
//                //tError.errorMessage = "���������湤�������쳣!";
//                //this.mErrors .addOneError(tError) ;
//                return false;
//            }
        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "Execute7899999999";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * ������ʼ����ڵ�
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean Execute7799999999()
    {
        mResult.clear();
//        VData tVData = new VData();
//        System.out.println("Excute 77999999999");
        ActivityOperator tActivityOperator = new ActivityOperator();
//        LWMissionSchema tLWMissionSchema = new LWMissionSchema();
        //ִ�з������֪ͨ����������(������������ִ������Ϊͬһ����ʱ,����ִ����������ģʽ����)
        try
        {
//            System.out.println("ActivityOperator name:" +                    mActivityOperator.getClass());
            if (CheckFirstTrial())
            {
                return true;
            }
            if (tActivityOperator.CreateStartMission("0000000003", "0000001098", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                mResult.add(tempVData);
                tempVData = null;
            }
            else
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "TbWorkFlowBL";
                //tError.functionName = "Execute9999999999";
                //tError.errorMessage = "���������湤�������쳣!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }
        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "Execute7899999999";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * ����������ʼ����ڵ�
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean Execute7099999999()
    {
        mResult.clear();
//        VData tVData = new VData();
//        System.out.println("Excute Execute7099999999");
        ActivityOperator tActivityOperator = new ActivityOperator();
        //ִ�з������֪ͨ����������(������������ִ������Ϊͬһ����ʱ,����ִ����������ģʽ����)
        try
        {
//            System.out.println("ActivityOperator name:" +                    mActivityOperator.getClass());

//            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
            if (tActivityOperator.CreateStartMission("0000000003", "0000001061", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                mResult.add(tempVData);
                tempVData = null;
            }
            else
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "TbWorkFlowBL";
                //tError.functionName = "Execute9999999999";
                //tError.errorMessage = "���������湤�������쳣!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }
        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "TbWorkFlowBL";
            tError.functionName = "Execute7899999999";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * �Զ���ʼ�����,�����ڵ�
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean Execute9999991061()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "AskWorkFlowBL";
            tError.functionName = "Execute0000006004";
            tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "AskWorkFlowBL";
            tError.functionName = "Execute0000006004";
            tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        LWMissionSchema t1LWMissionSchema = new LWMissionSchema();
        LWMissionSchema t2LWMissionSchema = new LWMissionSchema();

        t1LWMissionSchema = tActivityOperator.CreateOnlyOneMission("0000000003",
                "0000001101", mInputData);
        t2LWMissionSchema = tActivityOperator.CreateOnlyOneMission("0000000003",
                "0000001104", mInputData);

        MMap map = new MMap();
        if (t1LWMissionSchema != null)
        {
            map.put(t1LWMissionSchema, "INSERT");
        }
        if (t1LWMissionSchema != null)
        {
            map.put(t2LWMissionSchema, "INSERT");
        }
        tVData.add(map);
        PubSubmit tPubSubmit = new PubSubmit();
        if (!tPubSubmit.submitData(tVData, ""))
        {
            CError tError = new CError();
            tError.moduleName = "UWSendTraceBL";
            tError.functionName = "submitData";
            tError.errorMessage = "���ݿ��ύʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * �Զ���������������ڵ�
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @param mOperate String
     * @return boolean
     */
    private boolean Execute000000112345(String mOperate)
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "TBWorkFlowBL";
            tError.functionName = "Execute0000001123";
            tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "TBWorkFlowBL";
            tError.functionName = "Execute0000001123";
            tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        tActivityOperator.DeleteMission(tMissionID, tSubMissionID, mOperate, mInputData);
        MMap map = new MMap();
        map = ((MMap) tActivityOperator.getResult().getObjectByObjectName("MMap", 0));
        tVData.add(map);
        PubSubmit tPubSubmit = new PubSubmit();
        if (!tPubSubmit.submitData(tVData, ""))
        {
            CError tError = new CError();
            tError.moduleName = "TBWorkFlowBL";
            tError.functionName = "submitData";
            tError.errorMessage = "���ݿ��ύʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * ׼����Ҫ���������
     * @return boolean
     */
    private boolean prepareOutputData()
    {
        try
        {
            MMap tmap = new MMap();
            for (int i = 0; i < mResult.size(); i++)
            {
                VData tData = new VData();
                tData = (VData) mResult.get(i);
                MMap map = (MMap) tData.getObjectByObjectName("MMap", 0);
                tmap.add(map);
            }
            tResult.add(tmap);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * �ж��Ƿ��й�����
     * @return boolean
     */
    private boolean CheckFirstTrial()
    {

        VData tVData = new VData();
        LWMissionSet tLWMissionSet = new LWMissionSet();
        LWMissionSchema tLWMissionSchema = new LWMissionSchema();
        LWMissionDB tLWMissionDB = new LWMissionDB();
        tLWMissionDB.setActivityID("0000001062");
        tLWMissionDB.setProcessID("0000000003");
        tLWMissionDB.setMissionProp1((String) mTransferData.getValueByName("PrtNo"));
        tLWMissionSet = tLWMissionDB.query();
        if (tLWMissionSet.size() == 0)
        {
            return false;
        }
        MMap map = new MMap();

        tLWMissionSchema = tLWMissionSet.get(1);
        map.put("delete from lwmission where missionid='" +
                tLWMissionSchema.getMissionID() + "' and activityid = '0000001062'",
                "DELETE"); //ɾ����ǰ�Ľڵ�
        tLWMissionSchema.setActivityID("0000001098");
        if (mTransferData.getValueByName("SubType") != null)
        {
            tLWMissionSchema.setMissionProp5((String) mTransferData.getValueByName("SubType"));
        }
        tLWMissionSchema.setLastOperator(mGlobalInput.Operator);
        tLWMissionSchema.setModifyDate(PubFun.getCurrentDate());
        tLWMissionSchema.setModifyTime(PubFun.getCurrentTime());
        tLWMissionSchema.setMissionProp2(PubFun.getCurrentDate());
        map.put(tLWMissionSchema, "INSERT"); //�����µĽڵ�
        tVData.add(map);
        mResult.add(tVData);
        return true;
    }

    public VData getResult()
    {
        return tResult;
    }

    /**
     * �ж��Ƿ���Ҫ���
     * @param tTransferData TransferData
     * @return boolean
     */
    private boolean CheckDraw(TransferData tTransferData)
    {
        if (mOperate.equals("0000001098") || mOperate.equals("0000001099"))
        {
            String mContNo = (String) tTransferData.getValueByName("ContNo");
            LDSpotTrackSet tLDSpotTrackSet = new LDSpotTrackSet();
            LDSpotTrackDB tLDSpotTrackDB = new LDSpotTrackDB();
            tLDSpotTrackDB.setOtherNo(mContNo);
            tLDSpotTrackDB.setOtherType("spotbargain");
            tLDSpotTrackSet = tLDSpotTrackDB.query();
            if (tLDSpotTrackSet.size() >= 1)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * ���Ժ���
     * @param args String[]
     */
    public static void main(String[] args)
    {
        VData cInputData = new VData();
        String cOperate = "";
        boolean expectedReturn = true;
        GlobalInput tGlobalInput = new GlobalInput();
        tGlobalInput.Operator = "001";
        tGlobalInput.ManageCom = "861100";
        tGlobalInput.ComCode = "001";
        cInputData.add(tGlobalInput);
        LCContSet tLCContSet = new LCContSet();
        LCContSchema tLCContSchema = new LCContSchema();
        tLCContSchema.setContNo( "9015000000747788");
        tLCContSchema.setPrtNo( "1001010512300188");
        //���³�ʼ��Set����
        tLCContSet = new LCContSet();
        tLCContSet.add( tLCContSchema );

        TransferData tTransferData = new TransferData();
        tTransferData.setNameAndValue("MissionID", "00000000000000003276");
        tTransferData.setNameAndValue("SubMissionID", "1");
        cInputData.add(tLCContSet);
        cInputData.add(tTransferData);
        TbWorkFlowBL ttTbWorkFlowBL = new TbWorkFlowBL();
        boolean actualReturn = ttTbWorkFlowBL.submitData(cInputData, "0000001150");
    }
}

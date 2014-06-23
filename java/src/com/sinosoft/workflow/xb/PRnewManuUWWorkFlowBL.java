package com.sinosoft.workflow.xb;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LWMissionSchema;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PRnewManuUWWorkFlowBL
{
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** �����洫�����ݵ����� */
    private VData mInputData;
    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
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

    /**ִ�б�ȫ�����������֪ͨ��������0000000001
         /** ����������� */
//     private LPPENoticeSet mLPPENoticeSet = new LPPENoticeSet();
//    private LPPENoticeSet mAllLPPENoticeSet = new LPPENoticeSet();
//    private LPPENoticeSchema mLPPENoticeSchema = new LPPENoticeSchema();
     /** ���������Ŀ�� */
//    private LPPENoticeItemSet mLPPENoticeItemSet = new LPPENoticeItemSet();
//    private LPPENoticeItemSet mmLPPENoticeItemSet = new LPPENoticeItemSet();
//    private LPPENoticeItemSet mAllLPPENoticeItemSet = new LPPENoticeItemSet();

     public PRnewManuUWWorkFlowBL()
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
        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData(cInputData, cOperate))
        {
            return false;
        }

        // ���ݲ���ҵ����
        if (!dealData())
        {
            return false;
        }

        System.out.println("---PRnewManuUWWorkFlowBL dealData---");

        //׼������̨������
        if (!prepareOutputData())
        {
            return false;
        }

        System.out.println("---PRnewManuUWWorkFlowBL prepareOutputData---");

        //�����ύ
        PRnewManuUWWorkFlowBLS tPRnewManuUWWorkFlowBLS = new PRnewManuUWWorkFlowBLS();
        System.out.println("Start PRnewManuUWWorkFlowBLS Submit...");

        if (!tPRnewManuUWWorkFlowBLS.submitData(mResult, mOperate))
        {
            // @@������
            this.mErrors.copyAllErrors(tPRnewManuUWWorkFlowBLS.mErrors);
            CError tError = new CError();
            tError.moduleName = "PRnewManuUWWorkFlowBLS";
            tError.functionName = "submitData";
            tError.errorMessage = "�����ύʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        System.out.println("---PRnewManuUWWorkFlowBLS commitData End ---");
        return true;
    }

    /**
     * �����������еõ����ж���
     *��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
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
            tError.moduleName = "PRnewManuUWWorkFlowBL";
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
            tError.moduleName = "PRnewManuUWWorkFlowBL";
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
            tError.moduleName = "PRnewManuUWWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mOperate = cOperate;
        if (mOperate == null || mOperate.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PRnewManuUWWorkFlowBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operate����ڵ����ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * ���ݲ�����ҵ����
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {
        //��ȫ��������ӡ�˱�֪ͨ����
        if (mOperate.trim().equals("8999999999"))
        {
            if (!Execute8999999999())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "������ȫ���������˹��˱������ʼ����ʧ��!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;
        }

        //ִ�б�ȫ���������˹��˱��������
        if (mOperate.trim().equals("0000000100"))
        {
            if (!Execute0000000100())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "ִ�б�ȫ���������˹��˱��������ʧ��!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }

        //ִ�б�ȫ�����������֪ͨ��������
        if (mOperate.trim().equals("0000000101"))
        {
            if (!Execute0000000101())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "ִ�б�ȫ�����������֪ͨ��������ʧ��!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }
        //ִ�б�ȫ�������ӷѻ������
        if (mOperate.trim().equals("0000000102"))
        {
            if (!Execute0000000102())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "ִ�б�ȫ�����������֪ͨ��������ʧ��!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }

            return true;

        }
        //ִ�б�ȫ��������Լ�������
        if (mOperate.trim().equals("0000000103"))
        {
            if (!Execute0000000103())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "ִ�б�ȫ�����������֪ͨ��������ʧ��!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }
        //ִ�б�ȫ������������֪ͨ��������
        if (mOperate.trim().equals("0000000104"))
        {
            if (!Execute0000000104())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "ִ�б�ȫ�����������֪ͨ��������ʧ��!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }
        //ִ�б�ȫ���������˱�֪ͨ��������
        if (mOperate.trim().equals("0000000105"))
        {
            if (!Execute0000000105())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "ִ�б�ȫ�����������֪ͨ��������ʧ��!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }
        //ִ�б�ȫ��������ӡ���֪ͨ��������
        if (mOperate.trim().equals("0000000106"))
        {
            if (!Execute0000000106())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "ִ�б�ȫ�����������֪ͨ��������ʧ��!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }

        //��ȫ��������ӡ�˱�֪ͨ����
        if (mOperate.trim().equals("0000000107"))
        {
            if (!Execute0000000107())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "ִ�б�ȫ��������ӡ�˱�֪ͨ��������ʧ��!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }

        //��ȫ��������ӡ����֪ͨ����
        if (mOperate.trim().equals("0000000108"))
        {
            if (!Execute0000000108())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "ִ�б�ȫ�����������֪ͨ��������ʧ��!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }

        //ִ�б�ȫ�������˱�ȷ�ϻ������
        if (mOperate.trim().equals("0000000110"))
        {
            if (!Execute0000000110())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "ִ�б�ȫ�����������֪ͨ��������ʧ��!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }

        //ִ�б�ȫ�������������֪ͨ��������
        if (mOperate.trim().equals("0000000111"))
        {
            if (!Execute0000000111())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "ִ�б�ȫ�����������֪ͨ��������ʧ��!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }

        //��ȫ��������ӡ�˱�֪ͨ����
        if (mOperate.trim().equals("0000000112"))
        {
            if (!Execute0000000112())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "PEdorManuUWWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "ִ�б�ȫ��������ӡ�˱�֪ͨ��������ʧ��!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;
        }
        //��ȫ���������մ�ӡ����֪ͨ����
        if (mOperate.trim().equals("0000000113"))
        {
            if (!Execute0000000113())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//	   CError tError = new CError();
//	   tError.moduleName = "PEdorManuUWWorkFlowBL";
//	   tError.functionName = "dealData";
//	   tError.errorMessage = "ִ�б�ȫ��������������֪ͨ��������ʧ��!";
//	   this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;
        }
        //ִ�б�ȫ�������������֪ͨ��������
        if (mOperate.trim().equals("0000000114"))
        {
            if (!Execute0000000114())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "ִ�б�ȫ�������������֪ͨ��������ʧ��!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }

        //��ȫ����������˱�֪ͨ����
        if (mOperate.trim().equals("0000000115"))
        {
            if (!Execute0000000115())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "ִ�б�ȫ����������˱�֪ͨ��������ʧ��!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;

        }

        //��ȫ��������������֪ͨ����
        if (mOperate.trim().equals("0000000116"))
        {
            if (!Execute0000000116())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "ִ�б�ȫ��������������֪ͨ��������ʧ��!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }
            return true;
        }

        return true;

    }

    /**
     * ִ�б�ȫ���������˹��˱��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000100()
    { //*
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "Execute0000000100";
            tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000100"
                    , mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "Execute0000000000";
                //tError.errorMessage = "����������ִ�б�ȫ���������˹��˱�����������!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ�б�ȫ���������˹��˱��������Ľ��
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

            //����ִ���걣ȫ���������˹��˱��������������ڵ�

            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000100"
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

            //���ת�����������
//		LBMissionSchema tLBMissionSchema = new LBMissionSchema ();
//		tLBMissionSchema = mActivityOperator.TranSaveDummyMission(tLWMissionSchema,mInputData);
//		if(tLBMissionSchema == null)
//		{
//		  // @@������
//		 this.mErrors.copyAllErrors( mActivityOperator.mErrors );
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
//		 tError.functionName = "Execute0000000100";
//		 tError.errorMessage = "����������ִ��ִ���걣ȫ���������˹��˱��������,���ת���������ݳ���!";
//		 this.mErrors .addOneError(tError) ;
//		 return false;
//		}
//		else
//		{
//		  MMap map = new MMap();
//		  map.put(tLBMissionSchema, "INSERT");
//		  mResult.add(map) ;
//		  tLBMissionSchema = null ;
//		}

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "����������ִ�б�ȫ���������˹��˱�����������!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/

        return true;

    }


    /**
     * ִ�б�ȫ�����������֪ͨ��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000101()
    {
        mResult.clear();
        VData tVData = new VData();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ�з������֪ͨ����������(������������ִ������Ϊͬһ����ʱ,����ִ����������ģʽ����)
        try
        {
            System.out.println("ActivityOperator name:" + mActivityOperator.getClass());
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000101"
                    , mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ�б�ȫ�˹��˱������֪ͨ��¼���������!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ�з������֪ͨ������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "PEdorManuUWWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�˹��˱������֪ͨ��¼���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    mResult.add(tempVData); //ȡ��Mapֵ
                }
            }

            //����ִ���귢�����֪ͨ����������һ��ӡ���֪ͨ������ڵ�
            ActivityOperator tActivityOperator = new ActivityOperator();
            //mInputData.add() ;
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000101"
                    , mInputData))
            {
                VData tmpVData = new VData();
                tmpVData = tActivityOperator.getResult();
                if (tmpVData != null && tmpVData.size() > 0)
                {
                    mResult.add(tmpVData);
                    tmpVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "PEdorManuUWWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�˹��˱������֪ͨ��¼�������,������һ��������ڵ����!";
                this.mErrors.addOneError(tError);
                return false;

            }

            //ɾ���������֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000101", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "PEdorManuUWWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�˹��˱������֪ͨ��¼�������,ɾ��������ڵ����!";
                this.mErrors.addOneError(tError);
                return false;

            }
        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * ִ�б�ȫ�������ӷ�¼��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000102()
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����tSubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //ִ���˹��˱����ӷ�¼����
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000102"
                    , mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ���˹��˱����ӷ�¼���������!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����Լ¼������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ�б�ȫ�������˹��˱����ӷ�¼���������!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱����ӷ�¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000102"
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
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ�б�ȫ�˹��˱����ӷ�¼�������,������һ��������ڵ����!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

//		//ɾ�����żӷ�¼����������ڵ�
//		tActivityOperator = new ActivityOperator();
//		if(tActivityOperator.DeleteMission(tMissionID,tSubMissionID,"0000000002",mInputData) )
//		{
//		  VData tempVData = new VData();
//		  tempVData = tActivityOperator.getResult();
//		  if(tempVData != null && tempVData.size() >0)
//		  {
//			mResult.add(tempVData) ;
//			tempVData = null ;
//		  }
//		}
//		else
//		{
//		  this.mErrors.copyAllErrors( mActivityOperator.mErrors );
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "����������ִ�б�ȫ�˹��˱����ӷ�¼�������,ɾ��������ڵ����!";
//		 this.mErrors .addOneError(tError) ;
//		 return false;
//
//		}//δ���˱�֪ͨ����ǰ�Կ����޸ļӷ���Ϣ

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * ִ�б�ȫ�������˹��˱�����Լ¼��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000103()
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ���˹��˱�����Լ���
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000103"
                    , mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ���˹��˱�����Լ¼���������!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����Լ¼������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "PEdorManuUWWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�������˹��˱�����Լ¼���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000103"
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
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ�б�ȫ�˹��˱�����Լ¼�������,������һ��������ڵ����!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

//		//ɾ��������Լ¼����������ڵ�
//		tActivityOperator = new ActivityOperator();
//		if(tActivityOperator.DeleteMission(tMissionID,tSubMissionID,"0000000003",mInputData) )
//		{
//		  VData tempVData = new VData();
//		  tempVData = tActivityOperator.getResult();
//		  if(tempVData != null && tempVData.size() >0)
//		  {
//			mResult.add(tempVData) ;
//			tempVData = null ;
//		  }
//		}
//		else
//		{
//		  this.mErrors.copyAllErrors( mActivityOperator.mErrors );
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "����������ִ�б�ȫ�˹��˱�����Լ¼�������,ɾ��������ڵ����!";
//		 this.mErrors .addOneError(tError) ;
//		 return false;
//
//		}//δ���˱�֪ͨ����ǰ�Կ����޸���Լ��Ϣ


        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * ִ�б�ȫ��������������֪ͨ��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000104()
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ���˹��˱�����Լ���
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000104"
                    , mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ���˹��˱���������֪ͨ���������!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }
            System.out.println("Exception:1");
            //���ִ���˹��˱�����Լ¼������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "PEdorManuUWWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�������˹��˱���������֪ͨ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }
            System.out.println("Exception:2");
            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000104"
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
            else
            {
                this.mErrors.copyAllErrors(tActivityOperator.mErrors);
                //CError tError = new CError();
                // tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ�б�ȫ�˹��˱���������֪ͨ�������,������һ��������ڵ����!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }
            System.out.println("Exception:3");
            //ɾ��������֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000104", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(tActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ�б�ȫ�˹��˱���������֪ͨ�������,ɾ��������ڵ����!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
//		 // @@������
//		 if(tActivityOperator.mErrors != null )
            System.out.println("Exception:" + ex.toString());
//		 if(mActivityOperator.mErrors != null )
//         System.out.println("mActivityOperator.mErrors:"+mActivityOperator.mErrors.getErrorCount()) ;

//		 this.mErrors.copyAllErrors( tActivityOperator.mErrors );
//		 this.mErrors.copyAllErrors( mActivityOperator.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = ex.toString();
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * ִ�б�ȫ���������˱�֪ͨ��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000105()
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //ִ���˹��˱������˱�֪ͨ����
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000105"
                    , mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                // tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ���˹��˱������˱�֪ͨ���������!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����Լ¼������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "PEdorManuUWWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�������˹��˱������˱�֪ͨ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000105"
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
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ�б�ȫ�˹��˱���������֪ͨ�������,������һ��������ڵ����!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000105", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ�б�ȫ�˹��˱������˱�֪ͨ�������,ɾ��������ڵ����!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * ִ�б�ȫ��������ӡ���֪ͨ��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000106()
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ���˹��˱�����ӡ���֪ͨ��
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000106"
                    , mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ���˹��˱�����ӡ���֪ͨ���������!";
                //this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����Լ¼������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "PEdorManuUWWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�������˹��˱�����ӡ���֪ͨ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000106"
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
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ�б�ȫ�˹��˱�����ӡ���֪ͨ�������,������һ��������ڵ����!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000106", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
                //tError.functionName = "dealData";
                //tError.errorMessage = "����������ִ�б�ȫ�˹��˱�����ӡ���֪ͨ�������,ɾ��������ڵ����!";
                //this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * ִ�б�ȫ��������ӡ�˱�֪ͨ��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000107()
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //ִ���˹��˱�����Լ���
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000107"
                    , mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "����������ִ��ִ�д�ӡ�˱�֪ͨ���������!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����ӡ�˱�֪ͨ������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "PEdorManuUWWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�д�ӡ�˱�֪ͨ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000107"
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
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "PEdorManuUWWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "����������ִ�б�ȫ�˹��˱���������֪ͨ�������,������һ��������ڵ����!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000107", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "PEdorManuUWWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "����������ִ�б�ȫ�˹��˱������˱�֪ͨ�������,ɾ��������ڵ����!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * ִ�б�ȫ��������ӡ���֪ͨ��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000108()
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ�д�ӡ���֪ͨ����
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000108"
                    , mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "����������ִ��ִ�д�ӡ���֪ͨ���������!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����ӡ���֪ͨ������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "PEdorManuUWWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�д�ӡ���֪ͨ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000108"
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
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "PEdorManuUWWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "����������ִ�б�ȫ�˹��˱���������֪ͨ�������,������һ��������ڵ����!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000108", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "PEdorManuUWWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "����������ִ�б�ȫ�˹��˱������˱�֪ͨ�������,ɾ��������ڵ����!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ִ�б�ȫ�������˱�ȷ�ϻ������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000110()
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ���˹��˱�����Լ���
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000110"
                    , mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "����������ִ���˹���ȫ�������˱�ȷ���������!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����Լ¼������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "PEdorManuUWWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�������˹���ȫ�������˱�ȷ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000110"
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
            else
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "���������汣ȫ�������˹���ȫ�������˱�ȷ�����������һ�������ڵ����!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000110", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//	   CError tError = new CError();
//	   tError.moduleName = "PEdorManuUWWorkFlowBL";
//	   tError.functionName = "dealData";
//	   tError.errorMessage = "����������ִ�б�ȫ�˹��˱������˱�֪ͨ�������,ɾ��������ڵ����!";
//	   this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ִ�б�ȫ���������֪ͨ����ջ������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000111()
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //ִ���˹��˱�����Լ���
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000111"
                    , mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			 CError tError = new CError();
//			 tError.moduleName = "PEdorManuUWWorkFlowBL";
//			 tError.functionName = "dealData";
//			 tError.errorMessage = "����������ִ���˹���ȫ���������֪ͨ������������!";
//			 this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����Լ¼������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "PEdorManuUWWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�������˹���ȫ���������֪ͨ������������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000111"
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
            else
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			  CError tError = new CError();
//			  tError.moduleName = "PEdorManuUWWorkFlowBL";
//			  tError.functionName = "dealData";
//			  tError.errorMessage = "���������汣ȫ�������˹���ȫ���������֪ͨ��������������һ�������ڵ����!";
//			  this.mErrors .addOneError(tError) ;
                return false;
            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000111", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			  CError tError = new CError();
//			  tError.moduleName = "PEdorManuUWWorkFlowBL";
//			  tError.functionName = "dealData";
//			  tError.errorMessage = "����������ִ�б�ȫ�˹��˱������˱�֪ͨ�������,ɾ��������ڵ����!";
//			  this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ִ�б�ȫ�������˱�֪ͨ����ջ������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000112()
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //ִ���˹��˱�����Լ���
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000112"
                    , mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			  CError tError = new CError();
//			  tError.moduleName = "PEdorManuUWWorkFlowBL";
//			  tError.functionName = "dealData";
//			  tError.errorMessage = "����������ִ���˹���ȫ�������˱�֪ͨ������������!";
//			  this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����Լ¼������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "PEdorManuUWWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�������˹���ȫ�������˱�֪ͨ������������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000112"
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
            else
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			  CError tError = new CError();
//			  tError.moduleName = "PEdorManuUWWorkFlowBL";
//			  tError.functionName = "dealData";
//			  tError.errorMessage = "���������汣ȫ�������˹���ȫ�������˱�֪ͨ��������������һ�������ڵ����!";
//			  this.mErrors .addOneError(tError) ;
                return false;
            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000112", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			  CError tError = new CError();
//			  tError.moduleName = "PEdorManuUWWorkFlowBL";
//			  tError.functionName = "dealData";
//			  tError.errorMessage = "����������ִ�б�ȫ�˹��˱������˱�֪ͨ�������,ɾ��������ڵ����!";
//			  this.mErrors .addOneError(tError) ;
                return false;

            }
        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * ִ�б�ȫ�������ظ����֪ͨ��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000113()
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ�лظ����֪ͨ����
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000113"
                    , mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "����������ִ��ִ�лظ����֪ͨ���������!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱����ظ����֪ͨ������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "PEdorManuUWWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�д�ӡ���֪ͨ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000113"
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
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "PEdorManuUWWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "����������ִ�б�ȫ�˹��˱����ظ�����֪ͨ�������,������һ��������ڵ����!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000113", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "PEdorManuUWWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "����������ִ�б�ȫ�˹��˱����ظ�����֪ͨ�������,ɾ��������ڵ����!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ִ�б�ȫ�������������֪ͨ��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000114()
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ���˹��˱�����ӡ���֪ͨ��
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000114"
                    , mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			 CError tError = new CError();
//			 tError.moduleName = "PEdorManuUWWorkFlowBL";
//			 tError.functionName = "dealData";
//			 tError.errorMessage = "����������ִ���˹��˱�����ӡ���֪ͨ���������!";
//			 this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����Լ¼������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "PEdorManuUWWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�б�ȫ�������˹��˱�����ӡ���֪ͨ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000114"
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
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			CError tError = new CError();
//			tError.moduleName = "PEdorManuUWWorkFlowBL";
//			tError.functionName = "dealData";
//			tError.errorMessage = "����������ִ�б�ȫ�˹��˱�����ӡ���֪ͨ�������,������һ��������ڵ����!";
//			this.mErrors .addOneError(tError) ;
                return false;

            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000114", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//			CError tError = new CError();
//			tError.moduleName = "PEdorManuUWWorkFlowBL";
//			tError.functionName = "dealData";
//			tError.errorMessage = "����������ִ�б�ȫ�˹��˱�����ӡ���֪ͨ�������,ɾ��������ڵ����!";
//			this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ִ�б�ȫ����������˱�֪ͨ��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000115()
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //ִ���˹��˱�����Լ���
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000115"
                    , mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "����������ִ��ִ�в���˱�֪ͨ���������!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����ӡ�˱�֪ͨ������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "PEdorManuUWWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�в���˱�֪ͨ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000115"
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
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "PEdorManuUWWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "����������ִ�б�ȫ�˹��˱�������˱�֪ͨ�������,������һ��������ڵ����!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000115", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "PEdorManuUWWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "����������ִ�б�ȫ�˹��˱������˱�֪ͨ�������,ɾ��������ڵ����!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * ִ�б�ȫ�������������֪ͨ��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute0000000116()
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tSubMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��������ǰ̨����TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //ִ�д�ӡ���֪ͨ����
        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID, "0000000116"
                    , mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		 CError tError = new CError();
//		 tError.moduleName = "PEdorManuUWWorkFlowBL";
//		 tError.functionName = "dealData";
//		 tError.errorMessage = "����������ִ��ִ�в������֪ͨ���������!";
//		 this.mErrors .addOneError(tError) ;
                return false;
            }

            //���ִ���˹��˱�����ӡ���֪ͨ������Ľ��
            tVData = mActivityOperator.getResult();
            if (tVData == null)
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                CError tError = new CError();
                tError.moduleName = "PEdorManuUWWorkFlowBL";
                tError.functionName = "dealData";
                tError.errorMessage = "����������ִ�в������֪ͨ���������!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //mResult.add(tVData);
            if (tVData != null)
            {
                for (int i = 0; i < tVData.size(); i++)
                {
                    VData tempVData = new VData();
                    tempVData = (VData) tVData.get(i);
                    //for(int j=0;j< tempVData.size() ;j++)
                    //{
                    mResult.add(tempVData); //ȡ��Mapֵ
                    //}
                }
            }

            //����ִ���˹��˱�����Լ¼�������ȴ����ź˱�֪ͨ������ڵ�
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID, "0000000116"
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
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "PEdorManuUWWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "����������ִ�б�ȫ�˹��˱���������֪ͨ�������,������һ��������ڵ����!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

            //ɾ�����˱�֪ͨ������ڵ�
            tActivityOperator = new ActivityOperator();
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID, "0000000116", mInputData))
            {
                VData tempVData = new VData();
                tempVData = tActivityOperator.getResult();
                if (tempVData != null && tempVData.size() > 0)
                {
                    mResult.add(tempVData);
                    tempVData = null;
                }
            }
            else
            {
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "PEdorManuUWWorkFlowBL";
//		tError.functionName = "dealData";
//		tError.errorMessage = "����������ִ�б�ȫ�˹��˱������˱�֪ͨ�������,ɾ��������ڵ����!";
//		this.mErrors .addOneError(tError) ;
                return false;

            }

        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ������ʼ����ڵ�
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute8999999999()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //ִ�з������֪ͨ����������(������������ִ������Ϊͬһ����ʱ,����ִ����������ģʽ����)
        try
        {
            System.out.println("ActivityOperator name:" + mActivityOperator.getClass());

            //����ִ���귢�����֪ͨ����������һ��ӡ���֪ͨ������ڵ�
            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
            if (tActivityOperator.CreateStartMission("0000000001", "0000000100", mInputData))
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
                //tError.moduleName = "PEdorManuUWWorkFlowBL";
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
            tError.moduleName = "PEdorManuUWWorkFlowBL";
            tError.functionName = "Execute9999999999";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * ׼����Ҫ���������
     */
    private static boolean prepareOutputData()
    {
        //mInputData.add( mGlobalInput );
        return true;
    }


}

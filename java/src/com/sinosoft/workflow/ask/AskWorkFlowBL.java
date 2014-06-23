package com.sinosoft.workflow.ask;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LWMissionSchema;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;


/**
 * <p>Title: ����Լ������ </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */
public class AskWorkFlowBL
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

    public AskWorkFlowBL()
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
        System.out.println("---AskWorkFlowBL getInputData---");
        // ���ݲ���ҵ����
        if (!dealData())
        {
            return false;
        }
        System.out.println("---AskWorkFlowBL dealData---");

        //׼������̨������
        if (!prepareOutputData())
        {
            return false;
        }

        System.out.println("---AskWorkFlowBL prepareOutputData---");

        //�����ύ
        AskWorkFlowBLS tAskWorkFlowBLS = new AskWorkFlowBLS();
        System.out.println("Start AskWorkFlowBL Submit...");

        if (!tAskWorkFlowBLS.submitData(mResult, mOperate))
        {
            // @@������
            this.mErrors.copyAllErrors(tAskWorkFlowBLS.mErrors);

            CError tError = new CError();
            tError.moduleName = "AskWorkFlowBL";
            tError.functionName = "submitData";
            tError.errorMessage = "�����ύʧ��!";
            this.mErrors.addOneError(tError);

            return false;
        }

        System.out.println("---AskWorkFlowBLS commitData End ---");

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
        mGlobalInput = ((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput",
                0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData",
                0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "AskWorkFlowBL";
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
            tError.moduleName = "AskWorkFlowBL";
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
            tError.moduleName = "AskWorkFlowBL";
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
            tError.moduleName = "AskWorkFlowBL";
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
        else if (mOperate.trim().equals("0000006004")) //ִ�й��������˹��˱��������
        {
            if (!Execute0000006004())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }
        else if(mOperate.trim().equals("0000006000")) //������ɨ��ĵ�һ���ڵ�
        {
          if(!Execute0000006000())
          {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            return false;

          }
        }else if(mOperate.trim().equals("0000006020"))
        {
          if(!Execute0000006020())
          {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            return false;
          }

        }else if(mOperate.trim().equals("0000006006"))
        {
          if(!Execute0000006006())
          {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            return false;
          }
        }
        else //ִ�гб����������˱�֪ͨ��������
        {
            if (!Execute())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;
            }
        }

        return true;
    }
    //����ѯ�۹������ĵ�һ���ڵ�
    private boolean Execute0000006000() {
      mResult.clear();
      VData tVData = new VData();
      ActivityOperator tActivityOperator = new ActivityOperator();
      try {
        System.out.println("ActivityOperator name:" +
                           mActivityOperator.getClass());
        //������һ���ڵ�
        if (tActivityOperator.CreateStartMission("0000000006", "0000006002",
                                                 mInputData)) {
          mGlobalInput = ((GlobalInput) mInputData.getObjectByObjectName(
                  "GlobalInput",
                  0));
          VData tempVData = new VData();
          tempVData = tActivityOperator.getResult();
          mResult.add(tempVData);
          tempVData = null;
        }
        else {
          // @@������
          this.mErrors.copyAllErrors(mActivityOperator.mErrors);
          //CError tError = new CError();
          //tError.moduleName = "AskWorkFlowBL";
          //tError.functionName = "Execute0000006002";
          //tError.errorMessage = "���������湤�������쳣!";
          //this.mErrors .addOneError(tError) ;
          return false;
        }
      }
      catch (Exception ex) {
        // @@������
        this.mErrors.copyAllErrors(mActivityOperator.mErrors);
        CError tError = new CError();
        tError.moduleName = "AskWorkFlowBL";
        tError.functionName = "Execute0000006002";
        tError.errorMessage = "���������湤�������쳣!";
        this.mErrors.addOneError(tError);
        return false;
      }
      return true;
    }

    /**
     * Execute0000001100
     *
     * @return boolean
     */
    private boolean Execute0000006004()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
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

        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000006004", mInputData))
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

            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000006004", mInputData))
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
            tError.moduleName = "AskWorkFlowBL";
            tError.functionName = "Execute0000006004";
            tError.errorMessage = "����������ִ�гб����������˹��˱�����������!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/

        return true;
    }

  private boolean Execute0000006006()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
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

        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  "0000006006", mInputData))
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

            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    "0000006006", mInputData))
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
            tError.moduleName = "AskWorkFlowBL";
            tError.functionName = "Execute0000006004";
            tError.errorMessage = "����������ִ�гб����������˹��˱�����������!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/

        return true;
    }

    /**
     * ִ�гб����������˹��˱��������
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute()
    {
        mResult.clear();
        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();

        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "AskWorkFlowBL";
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
            tError.moduleName = "AskWorkFlowBL";
            tError.functionName = "Execute0000000000";
            tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);

            return false;
        }

        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                                                  mOperate, mInputData))
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
            if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                    mOperate, mInputData))
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
            if (tActivityOperator.DeleteMission(tMissionID, tSubMissionID,
                                                mOperate, mInputData))
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
            tError.moduleName = "AskWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "����������ִ������Լ����������!";
            this.mErrors.addOneError(tError);

            return false;
        }

        //*/
        return true;
    }

   private boolean Execute0000006020()
   {
     mResult.clear();
     VData tVData = new VData();
     ActivityOperator tActivityOperator = new ActivityOperator();

     //��õ�ǰ�������������ID
     String tMissionID = (String) mTransferData.getValueByName("MissionID");
     String tSubMissionID = (String) mTransferData.getValueByName(
             "SubMissionID");
     if (tMissionID == null)
     {
         // @@������
         //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
         CError tError = new CError();
         tError.moduleName = "AskWorkFlowBL";
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
         tError.moduleName = "AskWorkFlowBL";
         tError.functionName = "Execute0000000000";
         tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����SubMissionIDʧ��!";
         this.mErrors.addOneError(tError);

         return false;
     }
     try
     {

         //����ִ����б����������˹��˱��������������ڵ�
         if (tActivityOperator.CreateNextMission(tMissionID, tSubMissionID,
                 mOperate, mInputData))
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
         ex.printStackTrace();
         this.mErrors.copyAllErrors(mActivityOperator.mErrors);

         CError tError = new CError();
         tError.moduleName = "AskWorkFlowBL";
         tError.functionName = "dealData";
         tError.errorMessage = "����������ִ������Լ����������!";
         this.mErrors.addOneError(tError);

         return false;
     }

     //*/
     return true;
   }
    /**
     * ������ʼ����ڵ�
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean Execute7999999999()
    {
        mResult.clear();
        VData tVData = new VData();
        //�ŵ�¼�����У��
        AskFirstActivityCheck tAskFirstActivityCheck = new
                                                 AskFirstActivityCheck();

        if (tAskFirstActivityCheck.submitData(mInputData, ""))
        {
            tVData = tAskFirstActivityCheck.getResult();
//            mResult.add(tVData);
        }
        else
        {
            this.mErrors.copyAllErrors(tAskFirstActivityCheck.mErrors);
            return false;
        }

        ActivityOperator tActivityOperator = new ActivityOperator();
        //ִ�з������֪ͨ����������(������������ִ������Ϊͬһ����ʱ,����ִ����������ģʽ����)
        try
        {
            System.out.println("ActivityOperator name:" +
                               mActivityOperator.getClass());

            //����ִ���귢�����֪ͨ����������һ��ӡ���֪ͨ������ڵ�
            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
            if (tActivityOperator.CreateStartMission("0000000006", "0000006004",
                                                     mInputData))                 //����¼����ϵĵ�һ���ڵ�
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

                return false;
            }
        }
        catch (Exception ex)
        {
            // @@������
            this.mErrors.copyAllErrors(mActivityOperator.mErrors);
            CError tError = new CError();
            tError.moduleName = "AskWorkFlowBL";
            tError.functionName = "Execute7999999999";
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
        //mInputData.add( mGlobalInput );
        return true;
    }
}

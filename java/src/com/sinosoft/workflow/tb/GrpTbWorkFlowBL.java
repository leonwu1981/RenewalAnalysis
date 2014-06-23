/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;
import com.sinosoft.lis.db.LWMissionDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LWMissionSchema;
import com.sinosoft.lis.vschema.LWMissionSet;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */
public class GrpTbWorkFlowBL
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
    String tPrtNo;//ӡˢ��
    String lockKey = "";//��ֵΪ�����������ӡˢ��
    String lockuser = "";//�����û�
    String lockinfo ="";//����Ϣ
    ExeSQL mExeSQL = new ExeSQL();
    public GrpTbWorkFlowBL()
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
        if(!submitAddLock())//���ü�������
        {
        	 return false;
        }
        // ���ݲ���ҵ����
        if (!dealData())
        {
        	if(!submitUnLock())//���ý�������
        	{
        		 return false;
        	}
            return false;
        }

        System.out.println("---GrpTbWorkFlowBL dealData---");

        //׼������̨������
        if (!prepareOutputData())
        {
        	if(!submitUnLock())//���ý�������
        	{
        		 return false;
        	}
            return false;
        }

        System.out.println("---GrpTbWorkFlowBL prepareOutputData---");

        //�����ύ
        GrpTbWorkFlowBLS tGrpTbWorkFlowBLS = new GrpTbWorkFlowBLS();
        System.out.println("Start GrpTbWorkFlowBL Submit...");

        if (tGrpTbWorkFlowBLS.submitData(mResult, mOperate))
        {
        	if(!submitUnLock())//���ý�������
        	{
        		 return false;
        	}
            System.out.println("---GrpTbWorkFlowBLS commitData End ---");
            return true;
        }
        else
        {
        	if(!submitUnLock())//���ý�������
        	{
        		 return false;
        	}
            // @@������
            this.mErrors.copyAllErrors(tGrpTbWorkFlowBLS.mErrors);

            CError tError = new CError();
            tError.moduleName = "GrpTbWorkFlowBL";
            tError.functionName = "submitData";
            tError.errorMessage = "�����ύʧ��!";
            this.mErrors.addOneError(tError);
            System.out.println("---GrpTbWorkFlowBLS commitData End ---");
            return false;
        }
    }

    //guoly-2009-03-24-TASK000024--��Լ������¼����ϡ����ˡ��˹��˱����˱�������/���в���������������
    public boolean submitAddLock()//�����ύ���ݼ���
    {
        lockuser = mGlobalInput.Operator;
        lockinfo ="";
        //Ϊ��ֹ�͵��ô�������������г�ͻ��������ֻ�жϴ���������-��Լ������¼����ϡ����ˡ��˹��˱����˱����������м���
        //����ʵ��Լ��¼����ϡ��µ����ˡ��˹��˱����˱�������������ô˹����������࣬�̽��������ڴ���
        if(mOperate.equals("0000002005")||mOperate.equals("0000002098")//0000002005	�ŵ��˱����� 0000002098	�ŵ�����Լ��ɨ���¼��
          //0000002001 ,�ŵ��µ�����  0000002002 ,�ŵ������޸�  0000002003 �ŵ��Զ��˱�//0000001001 ����������Լ����ȷ��
          ||mOperate.equals("0000002001")||mOperate.equals("0000002002")||mOperate.equals("0000002003")
          ||mOperate.equals("0000002008")//�ŵ������µ�����
          ||mOperate.equals("0000002004"))//0000002004 �ŵ��˹��˱�
          
        {
		     if(mOperate.equals("0000002098"))//�ŵ�����Լ��ɨ���¼�룬״̬����(������ڵ��ж�)
		     {
		         tPrtNo=(String) mTransferData.getValueByName("PrtNo");//��ɨ��¼���ֱ�ӽ��մ����ӡˢ��
		         lockinfo="�û���"+lockuser+"�����ڽ�����Լ��¼����ϡ�������";
		     }
		     if(mOperate.equals("0000002005"))//�ŵ��˱�������״̬����(������ڵ��ж�)
		     {
		    	 String sqlPrtNo="select n.missionprop2 from lwmission n where n.activityid='0000002005'  "
 		                        +" and n.missionid='"+(String) mTransferData.getValueByName("MissionID")+"'"
 		                        +" and n.submissionid='"+(String) mTransferData.getValueByName("SubMissionID")+"'"
 		                        ;
                 tPrtNo=mExeSQL.getOneValue(sqlPrtNo);//�˱�������Ҫ���ݴ����MissionID&SubMissionID����ȡ��ӡˢ��
		         lockinfo="�û���"+lockuser+"�����ڽ�����Լ���˱�������������";
		     }
		     //0000002001 ,�ŵ��µ�����  0000002002 ,�ŵ������޸�  0000002003 �ŵ��Զ��˱�
		     if(mOperate.equals("0000002001")||mOperate.equals("0000002002")||mOperate.equals("0000002003"))
		     {
		    	 tPrtNo=(String) mTransferData.getValueByName("PrtNo");//�µ����ˡ���ֱ�ӽ��մ����ӡˢ��
			     lockinfo="�û���"+lockuser+"�����ڽ�����Լ���µ����ˡ�������";
		     }
		     //0000002004 �ŵ��˹��˱�
		     if(mOperate.equals("0000002004"))
		     {
		    	 String sqlPrtNo="select n.missionprop2 from lwmission n where n.activityid='0000002004' and "
                                +" n.missionid='"+(String) mTransferData.getValueByName("MissionID")+"'"
                                +" and n.submissionid='"+(String) mTransferData.getValueByName("SubMissionID")+"'"
                                ;
		    	 tPrtNo=mExeSQL.getOneValue(sqlPrtNo);//�˹��˱����ӹ��������л�ȡӡˢ��
			     lockinfo="�û���"+lockuser+"�����ڽ�����Լ���˹��˱���������";
		     }
		     //0000002008 �ŵ������µ�����
		     if(mOperate.equals("0000002008"))
		     {
		    	 tPrtNo=(String) mTransferData.getValueByName("PrtNo");//�����µ����˿�ֱ�ӽ��մ����ӡˢ��
			     lockinfo="�û���"+lockuser+"�����ڽ�����Լ���ŵ������µ����ˡ�������";
		     }
		     if(tPrtNo==null||tPrtNo.equals(""))
		     {
		    	 CError.buildErr(this, "��ȡӡˢ��ʧ�ܣ����ʵ����������ٽ���ҵ�������");
		         return false;
		     }
		     lockKey = mManageCom +tPrtNo;//��ֵΪ�����������ӡˢ��
		     if(!LockUtil.lock(SysConst.QY_LOCK_TYPE, lockKey, lockuser ,lockinfo))
		     {
		        	String info = " select information from ldlock where locktype='QY' and lockvalue='"+lockKey+"'";
			        String tinfo = mExeSQL.getOneValue(info);
		        	CError.buildErr(this, "���������"+mManageCom+"��"+" ӡˢ��: "+tPrtNo +" ������������ֵΪ�� "+lockKey+"�����ܽ��в��������� ������ϢΪ:"+tinfo+"");
		        	return false;
		     }
        }
    	return true;
    }
    //guoly-2009-03-24-TASK000024--��Լ������¼����ϡ����ˡ��˹��˱����˱�������/���н���
    public boolean submitUnLock()//����������ݽ���
    {
        //Ϊ��ֹ�͵��ô�������������г�ͻ��������ֻ�жϴ���������-��Լ������¼����ϡ��˱����������м���
        if(mOperate.equals("0000002005")||mOperate.equals("0000002098")//0000002005	�ŵ��˱����� 0000002098	�ŵ�����Լ��ɨ���¼��
    	   //0000002001 ,�ŵ��µ�����  0000002002 ,�ŵ������޸�  0000002003 �ŵ��Զ��˱�//0000001001 ����������Լ����ȷ��
    	   ||mOperate.equals("0000002001")||mOperate.equals("0000002002")||mOperate.equals("0000002003")
    	   ||mOperate.equals("0000002008")//�ŵ������µ�����
    	   ||mOperate.equals("0000002004"))//0000002004 �ŵ��˹��˱�
        {
            //���������Լ��¼����ϡ��µ����ˡ��˹��˱����˱�����������
	        if(!LockUtil.unlock(SysConst.QY_LOCK_TYPE, lockKey, this)){
	        	CError.buildErr(this, "�������"+mManageCom+"��,"+" ӡˢ��: "+tPrtNo +" ����ʧ�ܣ�");
				return false;
	        }
        }
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
            tError.moduleName = "GrpTbWorkFlowBL";
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
            tError.moduleName = "GrpTbWorkFlowBL";
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
            tError.moduleName = "GrpTbWorkFlowBL";
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
            tError.moduleName = "GrpTbWorkFlowBL";
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
     * @return boolean
     */
    private boolean dealData()
    {
        //�б���������ӡ�˱�֪ͨ����
        if (mOperate.trim().equals("6999999999"))
        {
            if (!Execute6999999999())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
//		  CError tError = new CError();
//		  tError.moduleName = "GrpTbWorkFlowBL";
//		  tError.functionName = "dealData";
//		  tError.errorMessage = "�����б����������˹��˱������ʼ����ʧ��!";
//		  this.mErrors .addOneError(tError) ;
                return false;
            }
        }
        else if (mOperate.trim().equals("7699999999"))
        {
            if (!Execute7699999999())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;

            }
            return true;
        }
        else if (mOperate.trim().equals("7899999999"))
        {
            if (!Execute7899999999())
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                return false;

            }
            return true;
        }

        else
        {
            if (!Execute())
            {
                // @@������
//                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
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

        //��õ�ǰ�������������ID
        String tMissionID = (String) mTransferData.getValueByName("MissionID");
        String tSubMissionID = (String) mTransferData.getValueByName(
                "SubMissionID");
        if (tMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpTbWorkFlowBL";
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
            tError.moduleName = "GrpTbWorkFlowBL";
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
            tError.moduleName = "GrpTbWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "����������ִ������Լ����������!";
            this.mErrors.addOneError(tError);

            return false;
        }

        //*/
        return true;
    }

    private boolean Execute7699999999()
    {
        mResult.clear();
//        VData tVData = new VData();
        ActivityOperator tActivityOperator = new ActivityOperator();
        //ִ�з������֪ͨ����������(������������ִ������Ϊͬһ����ʱ,����ִ����������ģʽ����)
        try
        {
            System.out.println("ActivityOperator name:" + mActivityOperator.getClass());
            if (CheckGrpFirstTrial())
            {
                return true;
            }
            //����ִ���귢�����֪ͨ����������һ��ӡ���֪ͨ������ڵ�
//            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
            if (tActivityOperator.CreateStartMission("0000000004", "0000002098",
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
            tError.functionName = "Execute7899999999";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    private boolean Execute7899999999()
      {
          mResult.clear();
//        VData tVData = new VData();
          ActivityOperator tActivityOperator = new ActivityOperator();
          //ִ�з������֪ͨ����������(������������ִ������Ϊͬһ����ʱ,����ִ����������ģʽ����)
          try
          {
              System.out.println("ActivityOperator name:" + mActivityOperator.getClass());

              //����ִ���귢�����֪ͨ����������һ��ӡ���֪ͨ������ڵ�
//            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
              if (tActivityOperator.CreateStartMission("0000000004", "0000002095",
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
              tError.functionName = "Execute7899999999";
              tError.errorMessage = "���������湤�������쳣!";
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
    { //*
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
            tError.moduleName = "GrpTbWorkFlowBL";
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
            tError.moduleName = "GrpTbWorkFlowBL";
            tError.functionName = "Execute0000001100";
            tError.errorMessage = "ǰ̨��������TransferData�еı�Ҫ����SubMissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        try
        {
            if (!mActivityOperator.ExecuteMission(tMissionID, tSubMissionID,
                    "0000001100", mInputData))
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "GrpTbWorkFlowBL";
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
                    "0000001100", mInputData))
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
            tError.moduleName = "GrpTbWorkFlowBL";
            tError.functionName = "dealData";
            tError.errorMessage = "����������ִ�гб����������˹��˱�����������!";
            this.mErrors.addOneError(tError);
            return false;
        } //*/
        return true;
    }

    /**
     * ������ʼ����ڵ�
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean Execute6999999999()
    {
        mResult.clear();
        VData tVData = new VData();

        //�ŵ�¼�����У��
        GrpFirstWorkFlowCheck tGrpFirstWorkFlowCheck = new GrpFirstWorkFlowCheck();

        if (tGrpFirstWorkFlowCheck.submitData(mInputData, ""))
        {
            tVData = tGrpFirstWorkFlowCheck.getResult();
            mResult.add(tVData);
        }
        else
        {
            this.mErrors.copyAllErrors(tGrpFirstWorkFlowCheck.mErrors);
            return false;
        }
        //ִ�з������֪ͨ����������(������������ִ������Ϊͬһ����ʱ,����ִ����������ģʽ����)
        try
        {
            System.out.println("ActivityOperator name:" + mActivityOperator.getClass());

            //����ִ���귢�����֪ͨ����������һ��ӡ���֪ͨ������ڵ�
//            LWMissionSchema tLWMissionSchema = new LWMissionSchema();
            if (mActivityOperator.CreateStartMission("0000000004", "0000002001",
                    mInputData))
            {
                VData tempVData = new VData();
                tempVData = mActivityOperator.getResult();
                mResult.add(tempVData);
                tempVData = null;
            }
            else
            {
                // @@������
                this.mErrors.copyAllErrors(mActivityOperator.mErrors);
                //CError tError = new CError();
                //tError.moduleName = "GrpTbWorkFlowBL";
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
            tError.moduleName = "GrpTbWorkFlowBL";
            tError.functionName = "Execute7999999999";
            tError.errorMessage = "���������湤�������쳣!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * ׼����Ҫ���������
     * @return boolean
     */
    private static boolean prepareOutputData()
    {
        //mInputData.add( mGlobalInput );
        return true;
    }

    private boolean CheckGrpFirstTrial()
{

    VData tVData = new VData();
    LWMissionSet tLWMissionSet = new LWMissionSet();
    LWMissionSchema tLWMissionSchema = new LWMissionSchema();
    LWMissionDB tLWMissionDB = new LWMissionDB();
    tLWMissionDB.setActivityID("0000002096");
    tLWMissionDB.setProcessID("0000000004");
    tLWMissionDB.setMissionProp1((String) mTransferData.getValueByName("PrtNo"));
    tLWMissionSet = tLWMissionDB.query();
    if (tLWMissionSet.size() == 0)
    {
        return false;
    }
    MMap map = new MMap();

    tLWMissionSchema = tLWMissionSet.get(1);
    map.put("delete from lwmission where missionid='" +
            tLWMissionSchema.getMissionID() + "' and activityid = '0000002096'",
            "DELETE"); //ɾ����ǰ�Ľڵ�
    tLWMissionSchema.setActivityID("0000002098");
    if (mTransferData.getValueByName("SubType") != null)
    {
        tLWMissionSchema.setMissionProp5((String) mTransferData.getValueByName("SubType"));
    }
    tLWMissionSchema.setLastOperator(mGlobalInput.Operator);
    //chenwm080125 ��¼��ɨ��¼��ʱ���������,��ǰ���õ��ǳ���¼���������.
    tLWMissionSchema.setMissionProp4(mGlobalInput.Operator);
    tLWMissionSchema.setModifyDate(PubFun.getCurrentDate());
    tLWMissionSchema.setModifyTime(PubFun.getCurrentTime());
    tLWMissionSchema.setMissionProp2(PubFun.getCurrentDate());
    map.put(tLWMissionSchema, "INSERT"); //�����µĽڵ�
    tVData.add(map);
    mResult.add(tVData);
    return true;
}

}


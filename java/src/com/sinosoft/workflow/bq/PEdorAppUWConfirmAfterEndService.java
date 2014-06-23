package com.sinosoft.workflow.bq;

import com.sinosoft.workflowengine.*;
import java.lang.*;
import java.util.*;
import com.sinosoft.lis.tb.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.vdb.*;
import com.sinosoft.lis.bl.*;
import com.sinosoft.lis.vbl.*;
import com.sinosoft.utility.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.cbcheck.*;
import com.sinosoft.lis.f1print.*;
import com.sinosoft.lis.bq.PEdorAppCancelBL;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PEdorAppUWConfirmAfterEndService implements AfterEndService{

  /** �������࣬ÿ����Ҫ����������ж����ø��� */
	public  CErrors mErrors = new CErrors();
	/** �����洫�����ݵ����� */
	private VData mInputData ;
	/** �����洫�����ݵ����� */
	private VData mResult = new VData();
	/** �������������д������ݵ����� */
	private GlobalInput mGlobalInput = new GlobalInput();
	//private VData mIputData = new VData();
	private TransferData mTransferData = new TransferData();
	/** ���ݲ����ַ��� */
	private String mOperater;
	private String mManageCom;
	private String mOperate;
	private String mString ;
	/** ҵ�����ݲ����ַ��� */
	private String mEdorNo;
	private String mPolNo;
	private String mMissionID ;
	private String mSubMissionID ;
	private String mInsuredNo;
	private String mUWState;
	private String mUWIdea;
	private String mAppGrade;//�ϱ��˱�ʦ����
	private String mAppUser;//�ϱ��˱�ʦ����
	private Reflections mReflections = new Reflections();
        private MMap map = new MMap();

   /**ִ�б�ȫ��������Լ�������0000000001*/
   /**������*/
   private LCPolSchema mLCPolSchema = new LCPolSchema();
   /** ����������ڵ��*/
   private LWMissionSchema mInitLWMissionSchema = new LWMissionSchema();//��ȫ�˹��˱���������ʼ�ڵ�
   /** ����������ڵ㱸�ݱ�*/
   private LWMissionSet mLWMissionSet = new LWMissionSet();
   private LBMissionSet mLBMissionSet = new LBMissionSet();

  public PEdorAppUWConfirmAfterEndService() {
  }

  /**
   * �������ݵĹ�������
   * @param: cInputData ���������
   *         cOperate ���ݲ���
   * @return:
   */
  public boolean submitData(VData cInputData,String cOperate)
  {
	  //�õ��ⲿ���������,�����ݱ��ݵ�������
	 if (!getInputData(cInputData,cOperate))
	   return false;


	 //����ҵ����
	 if (!dealData())
		 return false;

	 //׼������̨������
	 if (!prepareOutputData())
	   return false;

	return true;
  }


  /**
   * �����������еõ����ж���
   *��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
   */
  private boolean getInputData(VData cInputData,String cOperate)
  {
	//�����������еõ����ж���
	//���ȫ�ֹ�������
	mGlobalInput.setSchema((GlobalInput)cInputData.getObjectByObjectName("GlobalInput",0));
	mTransferData = (TransferData)cInputData.getObjectByObjectName("TransferData",0);
	mInputData = cInputData ;
	if ( mGlobalInput == null  )
	{
	  // @@������
	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
	  CError tError = new CError();
	  tError.moduleName = "PEdorUWConfirmAfterEndService";
	  tError.functionName = "getInputData";
	  tError.errorMessage = "ǰ̨����ȫ�ֹ�������ʧ��!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}

	//��ò���Ա����
	mOperater = mGlobalInput.Operator;
	if ( mOperater == null || mOperater.trim().equals("") )
	{
	  // @@������
	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
	  CError tError = new CError();
	  tError.moduleName = "PEdorUWConfirmAfterEndService";
	  tError.functionName = "getInputData";
	  tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}

	//��õ�½��������
	mManageCom = mGlobalInput.ManageCom;
	if ( mManageCom == null || mManageCom.trim().equals("") )
	{
	  // @@������
	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
	  CError tError = new CError();
	  tError.moduleName = "PEdorUWConfirmAfterEndService";
	  tError.functionName = "getInputData";
	  tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}

	mOperate = cOperate;

	//���ҵ������
	if ( mTransferData == null  )
	{
	  // @@������
	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
	  CError tError = new CError();
	  tError.moduleName = "PEdorUWConfirmAfterEndService";
	  tError.functionName = "getInputData";
	  tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}

	mEdorNo = (String)mTransferData.getValueByName("EdorNo");
	if ( mEdorNo == null  )
	{
	  // @@������
	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
	  CError tError = new CError();
	  tError.moduleName = "PEdorUWConfirmAfterEndService";
	  tError.functionName = "getInputData";
	  tError.errorMessage = "ǰ̨����ҵ��������EdorNoʧ��!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}

	mPolNo = (String)mTransferData.getValueByName("PolNo");
	if ( mPolNo == null  )
	{
	  // @@������
	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
	  CError tError = new CError();
	  tError.moduleName = "PEdorUWConfirmAfterEndService";
	  tError.functionName = "getInputData";
	  tError.errorMessage = "ǰ̨����ҵ��������PolNoʧ��!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}



	//���ҵ�����֪ͨ����

	//��õ�ǰ�������������ID
	  mMissionID = (String)mTransferData.getValueByName("MissionID");
	   if ( mMissionID == null  )
	  {
		// @@������
		//this.mErrors.copyAllErrors( tLCPolDB.mErrors );
		CError tError = new CError();
		tError.moduleName = "PEdorUWConfirmAfterEndService";
		tError.functionName = "getInputData";
		tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
		this.mErrors .addOneError(tError) ;
		return false;
	 }

	 //��õ�ǰ�������������ID
	  mSubMissionID = (String)mTransferData.getValueByName("SubMissionID");
	   if ( mMissionID == null  )
	  {
		// @@������
		//this.mErrors.copyAllErrors( tLCPolDB.mErrors );
		CError tError = new CError();
		tError.moduleName = "PEdorUWConfirmAfterEndService";
		tError.functionName = "getInputData";
		tError.errorMessage = "ǰ̨����ҵ��������SubMissionIDʧ��!";
		this.mErrors .addOneError(tError) ;
		return false;
	 }

	 //���ҵ��ȫ�˱�ȷ������
	//mContType = (String)cInputData.get(0);
	LPUWMasterMainSchema tLPUWMasterMainSchema = new LPUWMasterMainSchema();
	tLPUWMasterMainSchema = (LPUWMasterMainSchema)mTransferData.getValueByName("LPUWMasterMainSchema");
	if ( tLPUWMasterMainSchema == null   )
	{
	  // @@������
	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
	  CError tError = new CError();
	  tError.moduleName = "PEdorUWConfirmAfterEndService";
	  tError.functionName = "getInputData";
	  tError.errorMessage = "ǰ̨������ҵ�������˱���������ʧ��!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}

	if(tLPUWMasterMainSchema.getPassFlag()==null ||tLPUWMasterMainSchema.getPassFlag().trim().equals(""))
	{
	  // @@������
	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
	  CError tError = new CError();
	  tError.moduleName = "PEdorUWConfirmAfterEndService";
	  tError.functionName = "getInputData";
	  tError.errorMessage = "ǰ̨������ҵ�������˱������еĺ˱���������ʧ��!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}

	mUWState = tLPUWMasterMainSchema.getPassFlag();
	mUWIdea = tLPUWMasterMainSchema.getUWIdea();

	mAppUser = (String)mTransferData.getValueByName("AppUser");
	if( mUWState.trim().equals("6") && (mAppUser == null || mAppUser.trim().equals("")))
	{
	  // @@������
	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
	  CError tError = new CError();
	  tError.moduleName = "PEdorUWConfirmAfterEndService";
	  tError.functionName = "getInputData";
	  tError.errorMessage = "ǰ̨����ҵ���������ϱ��˱�ʦ����ʧ��!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}


	return true;
  }


  /**
   * ����ǰ����������ݣ�����BL�߼�����
   * ����ڴ�������г����򷵻�false,���򷵻�true
   */
  private boolean dealData()
  {
	//��ȫ�˱���������ʼ�ڵ�״̬�ı�
	if (prepareMission() == false)
		return false;

	 return true;

  }

  /**
	* ׼����ӡ��Ϣ��
	* @return
	*/
   private boolean prepareMission()
   {

	 //�ϱ��˱��޸ı�ȫ�˹��˱�����ʼ�ڵ�Ĭ�ϲ���ԱΪ�ϱ��˱�ʦ
	 if(mUWState.equals("6"))
	 {
	   LWMissionDB tLWMissionDB = new LWMissionDB();
	   LWMissionSet tLWMissionSet = new LWMissionSet();
	   String tStr = "Select * from LWMission where MissionID = '"+mMissionID+"'"
				+ "and ActivityID = '0000000000'";
	   tLWMissionSet = tLWMissionDB.executeQuery(tStr);
	   if(tLWMissionSet == null || tLWMissionSet.size() !=1)
	   {
		 // @@������
		 this.mErrors.copyAllErrors(tLWMissionSet.mErrors);
		 CError tError =new CError();
		 tError.moduleName="PEdorUWConfirmAfterEndService";
		 tError.functionName="prepareMission";
		 tError.errorMessage="��ѯ��������ȫ�˹��˱�����ʼ����ڵ�ʧ��!";
		 this.mErrors .addOneError(tError) ;
		 return false;
	   }
	   mInitLWMissionSchema = tLWMissionSet.get(1) ;
	   mInitLWMissionSchema.setDefaultOperator(mAppUser);


	 }

     //�±�ȫ�˱����ս���
        if (mUWState.equals("1") || mUWState.equals("9") || mUWState.equals("8")||mUWState.equals("4")||mUWState.equals("E")||mUWState.equals("L")||mUWState.equals("a"))
	 {
	   mInitLWMissionSchema = null;

	   LWMissionDB tLWMissionDB = new LWMissionDB();
	   String tStr = "Select * from LWMission where MissionID = '"+mMissionID+"' and ActivityID <> '0000000010'";

	   mLWMissionSet = tLWMissionDB.executeQuery(tStr);
	   if(mLWMissionSet == null )
	   {
		 // @@������
		 this.mErrors.copyAllErrors(mLWMissionSet.mErrors);
		 CError tError =new CError();
		 tError.moduleName="PEdorUWConfirmAfterEndService";
		 tError.functionName="prepareMission";
		 tError.errorMessage="��ѯ��������ȫ�˹��˱�������ڵ�ʧ��!";
		 this.mErrors .addOneError(tError) ;
		 return false;
	   }

	   for(int i = 1 ;i<=mLWMissionSet.size();i++)
	   {
		 LWMissionSchema tLWMissionSchema = new LWMissionSchema();
		 LBMissionSchema tLBMissionSchema = new LBMissionSchema();

		 tLWMissionSchema = mLWMissionSet.get(i);
		 String tSerielNo = PubFun1.CreateMaxNo("MissionSerielNo", 10);
		 mReflections.transFields(tLBMissionSchema,tLWMissionSchema);
		 tLBMissionSchema.setSerialNo(tSerielNo);
		 tLBMissionSchema.setActivityStatus("4");//�ڵ�����ִ��ǿ���Ƴ�
		 tLBMissionSchema.setLastOperator(mOperater);
		 tLBMissionSchema.setMakeDate(PubFun.getCurrentDate());
		 tLBMissionSchema.setMakeTime(PubFun.getCurrentTime());
		 mLBMissionSet.add(tLBMissionSchema) ;
	   }

           //����ͻ���ͬ�⣬ֱ�ӳ����������롣
           //add by Lanjun 2005/12/18
           if(mUWState.equals("a")||mUWState.equals("1"))
           {
             LPEdorAppSchema tLPEdorAppSchema  = new LPEdorAppSchema();

             String sqlEdorAcceptNo =
                 "select edoracceptno from lpedormain where edorno='"
                 + mEdorNo + "'";
             ExeSQL tExeSQL = new ExeSQL();
             String edorAcceptNo = tExeSQL.getOneValue(sqlEdorAcceptNo);

             tLPEdorAppSchema.setEdorAcceptNo(edorAcceptNo);

             String delReason = "�˱���ͨ��";
             String reasonCode = "6";
             TransferData tTransferData = new TransferData();
             tTransferData.setNameAndValue("DelReason", delReason);
             tTransferData.setNameAndValue("ReasonCode", reasonCode);

             // ׼���������� VData
             VData tVData = new VData();
             tVData.addElement(mGlobalInput);
             tVData.addElement(tLPEdorAppSchema);
             tVData.addElement(tTransferData);

             //���ñ�ȫ����ɾ���࣬�ͱ��ι���������ִ��ͬһ�������ύ��
             PEdorAppCancelBL tPEdorAppCancelBL = new PEdorAppCancelBL();
             if (!tPEdorAppCancelBL.submitData(tVData, "EDORAPP")) {
               this.mErrors.copyAllErrors(tPEdorAppCancelBL.mErrors);
               return false;
             }
             else {
               map.add(tPEdorAppCancelBL.getMap());
             }
           }



	 }
	 return true;
   }


  private boolean prepareOutputData()
 {
	 mResult.clear();
	 //��ӱ�ȫ��������ʼ����ڵ������
	 if( mUWState.equals("6") && mInitLWMissionSchema != null  )
	 {
	   map.put(mInitLWMissionSchema, "UPDATE");
	 }

	 //�����ع�����ͬ��ǿ��ִ����ϵ�����ڵ������
	 if(mLWMissionSet != null && mLWMissionSet.size()>0)
	 {
	   map.put(mLWMissionSet, "DELETE");
	 }

	 //�����ع�����ͬ��ǿ��ִ����ϵ�����ڵ㱸�ݱ�����
	 if(mLBMissionSet != null && mLBMissionSet.size()>0)
	 {
	   map.put(mLBMissionSet, "INSERT");
	 }


	mResult.add(map);
   return true;
  }
  public VData getResult()
  {
	return mResult;
  }

  public TransferData getReturnTransferData()
  {
	return mTransferData;
  }
  public CErrors getErrors() {
	return mErrors;
  }
}

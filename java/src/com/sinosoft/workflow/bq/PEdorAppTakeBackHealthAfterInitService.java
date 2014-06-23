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

/**
 * <p>Title: </p>
 * <p>Description:�������ڵ�����:��ȫ�˹��˱����֪ͨ����շ����� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PEdorAppTakeBackHealthAfterInitService implements AfterInitService{

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
   /** ҵ�����ݲ����ַ��� */
   private String mEdorAcceptNo;
   private String mPolNo;
   private String mMissionID ;
   private String mCertifyNo;
   private String mCertifyCode ;
   private boolean mPatchFlag ;
   private String mTakeBackOperator ;
   private String mTakeBackMakeDate ;
   private String mOldPrtSeq ;//����õ�֤�ǲ���֤,��ͬʱ����ʧԭ��֤Ҳ����.��֮�������ԭ��֤,�����Ѳ�����,��ͬʱҲҪ�Ѳ����ĵ�֤���յ�
   private boolean mAutoSysCertSendOutFlag = true;
   private Reflections mReflections = new Reflections();

   /**ִ�б�ȫ��������Լ�������0000000011*/
   /**������*/
   private LCPolSchema mLCPolSchema = new LCPolSchema();
   /** ��ȫ�˱����� */
   private LPUWMasterMainSchema mLPUWMasterMainSchema = new LPUWMasterMainSchema();
   /** ��ӡ����� */
   private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();
   private LOPRTManagerSet mLOPRTManagerSet = new LOPRTManagerSet();//����õ�֤�ǲ���֤,��ͬʱ����ʧԭ��֤Ҳ����.��֮�������ԭ��֤,�����Ѳ�����,��ͬʱҲҪ�Ѳ����ĵ�֤���յ�
   /** �������*/
   private LPAppPENoticeSchema mLPAppPENoticeSchema = new LPAppPENoticeSchema();
   /** ��֤���ű�*/
   private LZSysCertifySchema mLZSysCertifySchema = new LZSysCertifySchema();
   private LZSysCertifySet mLZSysCertifySet = new LZSysCertifySet();
   public PEdorAppTakeBackHealthAfterInitService() {
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

	//У���Ƿ���δ��ӡ�����֪ͨ��
	if(!checkData())
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
   * ׼������ǰ̨ͳһ�洢����
   * �����������������򷵻�false,���򷵻�true
	 */
  private boolean prepareOutputData()
 {
	mResult.clear();
	MMap map = new MMap();

	//��Ӻ˱�֪ͨ���ӡ���������
	if(mLOPRTManagerSet != null && mLOPRTManagerSet.size() >0)
	 {
	  map.put(mLOPRTManagerSet, "UPDATE");
	 }

   //��ӱ�ȫ�����˱���������
	if(mLPUWMasterMainSchema != null)
	{
	  map.put(mLPUWMasterMainSchema, "UPDATE");
	}

	//�����������
	if(mLPAppPENoticeSchema != null)
	{
	  map.put( mLPAppPENoticeSchema , "UPDATE");
	}

	//��ӱ�ȫ���֪ͨ���Զ����ű�����
	if(mLZSysCertifySet != null && mLZSysCertifySet.size() >0)
	{
	  map.put(mLZSysCertifySet, "UPDATE");
	}

   mResult.add(map) ;
   return true;
 }

 /**
  * У��ҵ������
  * @return
  */
 private boolean checkData()
 {
   //У�鱣����Ϣ
//   LCPolDB tLCPolDB = new LCPolDB();
//   tLCPolDB.setPolNo(mPolNo) ;
//   if(!tLCPolDB.getInfo())
//   {
//	 CError tError = new CError();
//	 tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
//	 tError.functionName = "checkData";
//	 tError.errorMessage = "����"+mPolNo+"��Ϣ��ѯʧ��!";
//	 this.mErrors .addOneError(tError) ;
//	 return false;
//   }
//   mLCPolSchema.setSchema(tLCPolDB) ;

   //У�鱣ȫ�����˱�����
   //У�鱣����Ϣ
   LPUWMasterMainDB tLPUWMasterMainDB = new LPUWMasterMainDB();
   String sql = "select * from lpEdorMain where EdorAcceptNo='"+mEdorAcceptNo+"'";
   LPEdorMainSchema tLPEdorMainSchema = new LPEdorMainSchema();
   LPEdorMainDB tLPEdorMainDB = new LPEdorMainDB();
   LPEdorMainSet tLPEdorMainSet =  tLPEdorMainDB.executeQuery(sql);
   tLPEdorMainSchema = tLPEdorMainSet.get(1);
   tLPUWMasterMainDB.setEdorNo(tLPEdorMainSchema.getEdorNo()) ;
   tLPUWMasterMainDB.setContNo(tLPEdorMainSchema.getContNo()) ;
  // tLPUWMasterMainDB.setPolNo(mPolNo) ;
   if(!tLPUWMasterMainDB.getInfo())
   {
	 CError tError = new CError();
	 tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
	 tError.functionName = "checkData";
	 tError.errorMessage = "����"+mPolNo+"��ȫ�����˱�������Ϣ��ѯʧ��!";
	 this.mErrors .addOneError(tError) ;
	 return false;
   }

   mLPUWMasterMainSchema.setSchema(tLPUWMasterMainDB) ;



   // ����δ��ӡ״̬�ĺ˱�֪ͨ���ڴ�ӡ������ֻ����һ��
   // ������ͬһ���������ͣ�ͬһ���������룬ͬһ��������������
   LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

   tLOPRTManagerDB.setCode("03");//
   tLOPRTManagerDB.setPrtSeq(mCertifyNo) ;
   tLOPRTManagerDB.setOtherNo( mEdorAcceptNo );
   tLOPRTManagerDB.setOtherNoType( PrintManagerBL.ONT_EDORACCEPT );//������
   //tLOPRTManagerDB.setStandbyFlag2(mEdorAcceptNo);
   tLOPRTManagerDB.setStateFlag("1");

   LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
   if( tLOPRTManagerSet == null )
   {
	 // @@������
	 CError tError = new CError();
	 tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
	 tError.functionName = "preparePrint";
	 tError.errorMessage = "��ѯ��ӡ�������Ϣ����!";
	 this.mErrors.addOneError(tError) ;
	 return false;
   }


   if( tLOPRTManagerSet.size() !=1 )
   {
	 // @@������
	 CError tError = new CError();
	 tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
	 tError.functionName = "preparePrint";
	 tError.errorMessage = "�ڴ�ӡ������û�д����Ѵ�ӡ������״̬�����֪ͨ��!";
	 this.mErrors.addOneError(tError) ;
	 return false;
   }

   mLOPRTManagerSchema = tLOPRTManagerSet.get(1);
   if(mLOPRTManagerSchema.getPatchFlag()==null)
		  mPatchFlag=false;
   else if(mLOPRTManagerSchema.getPatchFlag().equals("0"))
		 mPatchFlag=false;
   else if(mLOPRTManagerSchema.getPatchFlag().equals("1"))
		 mPatchFlag=true;


   //����õ�֤�ǲ���֤,��ͬʱ����ʧԭ��֤Ҳ����.��֮�������ԭ��֤,�����Ѳ�����,��ͬʱҲҪ�Ѳ����ĵ�֤���յ�
   if(mPatchFlag == true)
   {
       LOPRTManagerDB tempLOPRTManagerDB = new LOPRTManagerDB();
	   mOldPrtSeq = mLOPRTManagerSchema.getOldPrtSeq() ;
	   String tStr = "select * from LOPRTManager where (PrtSeq = '" + mOldPrtSeq + "'"
					+ "or OldPrtSeq = '" + mOldPrtSeq +"')" ;
	  LOPRTManagerSet tempLOPRTManagerSet = tempLOPRTManagerDB.executeQuery(tStr) ;;
	  if( tempLOPRTManagerSet.size() == 1 )
	  {
		// @@������
		CError tError = new CError();
		tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
		tError.functionName = "preparePrint";
		tError.errorMessage = "��ѯ�ڴ�ӡ������û�иò������֪ͨ���ԭ֪ͨ���¼��Ϣ����!";
		this.mErrors.addOneError(tError) ;
		return false;
	  }

	   for(int i=1;i<=tempLOPRTManagerSet.size() ; i ++)
	   {
	     mLOPRTManagerSet.add(tempLOPRTManagerSet.get(i) ) ;
	   }
   }
   else
	{
	   LOPRTManagerDB tempLOPRTManagerDB = new LOPRTManagerDB();
	   mOldPrtSeq = mLOPRTManagerSchema.getPrtSeq() ;
	   if(mOldPrtSeq!= null && !mOldPrtSeq.equals(""))
	   {
		 tempLOPRTManagerDB.setOldPrtSeq(mOldPrtSeq) ;
		 LOPRTManagerSet tempLOPRTManagerSet = tempLOPRTManagerDB.query();
		 if( tempLOPRTManagerSet != null && tempLOPRTManagerSet.size() >0 )
		 {
		   for(int i=1;i<=tempLOPRTManagerSet.size() ; i++)
		   {
			 mLOPRTManagerSet.add(tempLOPRTManagerSet.get(i)) ;
		   }
		 }
	   }
   }

    //��ѯϵͳ��֤���ն��б�
   for(int i= 1 ;i<= mLOPRTManagerSet.size() ; i++)
   {
	 if(mLOPRTManagerSet.get(i).getStateFlag() != null && mLOPRTManagerSet.get(i).getStateFlag().trim().equals("1") )
	{
	 LZSysCertifyDB  tLZSysCertifyDB = new LZSysCertifyDB();
	 LZSysCertifySet  tLZSysCertifySet = new LZSysCertifySet();
	 tLZSysCertifyDB.setCertifyCode((String)mTransferData.getValueByName("CertifyCode")) ;//���֪ͨ���ʶ
	 tLZSysCertifyDB.setCertifyNo(mLOPRTManagerSet.get(i).getPrtSeq()) ;
	 tLZSysCertifySet = tLZSysCertifyDB.query() ;
	 if( tLZSysCertifySet == null || tLZSysCertifySet.size() !=1 )
	 {
	   // @@������
	   CError tError = new CError();
	   tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
	   tError.functionName = "preparePrint";
	   tError.errorMessage = "�������֪ͨ��ʱ,LZSysCertifySchema����Ϣ��ѯʧ��!";
	   this.mErrors.addOneError(tError) ;
	   return false;
	 }
	 mLZSysCertifySet.add(tLZSysCertifySet.get(1));
   }
   }

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
	   tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
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
	   tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
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
	   tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
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
	   tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
	   tError.functionName = "getInputData";
	   tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
	   this.mErrors .addOneError(tError) ;
	   return false;
	 }

	 mEdorAcceptNo = (String)mTransferData.getValueByName("EdorAcceptNo");
	if ( mEdorAcceptNo == null  )
	{
	  // @@������
	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
	  CError tError = new CError();
	  tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
	  tError.functionName = "getInputData";
	  tError.errorMessage = "ǰ̨����ҵ��������EdorAcceptNoʧ��!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}

//2005-04-05 lanjun ע��
//	mPolNo = (String)mTransferData.getValueByName("PolNo");
//	if ( mPolNo == null  )
//	{
//	  // @@������
//	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
//	  CError tError = new CError();
//	  tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
//	  tError.functionName = "getInputData";
//	  tError.errorMessage = "ǰ̨����ҵ��������PolNoʧ��!";
//	  this.mErrors .addOneError(tError) ;
//	  return false;
//	}


	 //��õ�ǰ�������������ID
	   mMissionID = (String)mTransferData.getValueByName("MissionID");
	   if ( mMissionID == null  )
	  {
		// @@������
		//this.mErrors.copyAllErrors( tLCPolDB.mErrors );
		CError tError = new CError();
		tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
		tError.functionName = "getInputData";
		tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
		this.mErrors .addOneError(tError) ;
		return false;
	 }

	 //���ҵ��������
	 mCertifyNo = (String)mTransferData.getValueByName("CertifyNo");
	 if ( mCertifyNo == null  )
	 {
		// @@������
		//this.mErrors.copyAllErrors( tLCPolDB.mErrors );
		CError tError = new CError();
		tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
		tError.functionName = "getInputData";
		tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
		this.mErrors .addOneError(tError) ;
		return false;
	 }

	 //���ҵ��������
	 mCertifyCode = (String)mTransferData.getValueByName("CertifyCode");
	 if ( mCertifyCode == null || mCertifyCode.trim().equals("") )
	 {
		// @@������
		//this.mErrors.copyAllErrors( tLCPolDB.mErrors );
		CError tError = new CError();
		tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
		tError.functionName = "getInputData";
		tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
		this.mErrors .addOneError(tError) ;
		return false;
	 }

     //���ҵ��������
	 LZSysCertifySchema tLZSysCertifySchema = new LZSysCertifySchema();
	 tLZSysCertifySchema = (LZSysCertifySchema)mTransferData.getValueByName("LZSysCertifySchema");
	 if ( tLZSysCertifySchema == null  )
	 {
		// @@������
		//this.mErrors.copyAllErrors( tLCPolDB.mErrors );
		CError tError = new CError();
		tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
		tError.functionName = "getInputData";
		tError.errorMessage = "ǰ̨����ҵ��������LZSysCertifySchemaʧ��!";
		this.mErrors .addOneError(tError) ;
		return false;
	 }

	  mTakeBackOperator = tLZSysCertifySchema.getTakeBackOperator() ;
	  if ( mTakeBackOperator == null  )
	  {
		// @@������
		//this.mErrors.copyAllErrors( tLCPolDB.mErrors );
		CError tError = new CError();
		tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
		tError.functionName = "getInputData";
		tError.errorMessage = "ǰ̨����ҵ��������TakeBackOperatorʧ��!";
		this.mErrors .addOneError(tError) ;
		return false;
	  }

      mTakeBackMakeDate = tLZSysCertifySchema.getTakeBackMakeDate() ;
	  if ( mTakeBackMakeDate == null  )
	  {
		// @@������
		//this.mErrors.copyAllErrors( tLCPolDB.mErrors );
		CError tError = new CError();
		tError.moduleName = "PEdorPrintTakeBackAutoHealthAfterInitService";
		tError.functionName = "getInputData";
		tError.errorMessage = "ǰ̨����ҵ��������mTakeBackMakeDateʧ��!";
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
	 // �˱�������Ϣ
	 if (prepareAutoHealth() == false)
		   return false;

	 //��ӡ����
	 if (preparePrint() == false)
		 return false;

	//����ϵͳ��֤��ӡ����
	if (prepareAutoSysCertSendOut() == false)
	  return false;


	  return true;

   }

   /**
   * ׼���˱�������Ϣ
   * �����������������򷵻�false,���򷵻�true
   */
  private boolean prepareAutoHealth()
  {

	////׼���˱�������Ϣ
	  mLPUWMasterMainSchema.setModifyDate(PubFun.getCurrentDate()) ;
	  mLPUWMasterMainSchema.setModifyTime(PubFun.getCurrentTime());
	  mLPUWMasterMainSchema.setHealthFlag("3") ;//���ͺ˱�֪ͨ��

	//�������֪ͨ��
	LPAppPENoticeDB tLPAppPENoticeDB = new LPAppPENoticeDB();
	tLPAppPENoticeDB.setEdorAcceptNo(mEdorAcceptNo) ;
        tLPAppPENoticeDB.setPrtSeq(mLOPRTManagerSchema.getPrtSeq());
	//tLPAppPENoticeDB.setPolNo(mPolNo);
	tLPAppPENoticeDB.setPrintFlag("1");
	//tLPAppPENoticeDB.setInsuredNo(mLOPRTManagerSchema.getStandbyFlag1());//�����ֶ�1��ſͻ���
	if( !tLPAppPENoticeDB.getInfo() ) {
	  mErrors.copyAllErrors(tLPAppPENoticeDB.mErrors);
	  return false;
	}
	mLPAppPENoticeSchema=tLPAppPENoticeDB.getSchema();
	mLPAppPENoticeSchema.setPrintFlag("2");
	mLPAppPENoticeSchema.setModifyDate(PubFun.getCurrentDate());
	mLPAppPENoticeSchema.setModifyTime(PubFun.getCurrentTime());

	return true;
  }

   /**
	* ׼����ӡ��Ϣ��
	* @return
	*/
   private boolean preparePrint()
   {
	 //׼����ӡ���������
	 for(int i=1 ;i<=mLOPRTManagerSet.size() ;i++)
	 {
	   mLOPRTManagerSet.get(i).setStateFlag("2");
	 }
	 return true;
   }

   /**
	* ׼���˱�������Ϣ
	* �����������������򷵻�false,���򷵻�true
	*/
   private boolean prepareAutoSysCertSendOut()
   {
	 //׼����֤���չ��������
	 for(int i=1 ;i<=mLZSysCertifySet.size() ;i++)
	 {
	   mLZSysCertifySet.get(i).setTakeBackMakeDate(PubFun.getCurrentDate());
	   mLZSysCertifySet.get(i).setTakeBackMakeTime(PubFun.getCurrentTime());
	   mLZSysCertifySet.get(i).setModifyDate(PubFun.getCurrentDate());
	   mLZSysCertifySet.get(i).setModifyTime(PubFun.getCurrentTime());
	   mLZSysCertifySet.get(i).setTakeBackOperator(mTakeBackOperator);
	   mLZSysCertifySet.get(i).setStateFlag("1") ;
	 }
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
   public CErrors getErrors()
   {
	 return mErrors;
   }
}

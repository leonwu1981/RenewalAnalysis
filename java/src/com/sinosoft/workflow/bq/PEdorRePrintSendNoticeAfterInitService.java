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
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author lh
 * @version 1.0
 */

public class PEdorRePrintSendNoticeAfterInitService implements AfterInitService {

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
	private TransferData mReturnTransferData = new TransferData();
	/**���������� */
	ActivityOperator mActivityOperator = new ActivityOperator();
	/** ���ݲ����ַ��� */
	private String mOperater;
	private String mManageCom;
	private String mOperate;
	/** ҵ�����ݲ����ַ��� */
	private String mEdorNo;
	private String mPolNo;
	private String mInsuredNo;
	private String mMissionID ;

	/**ִ�б�ȫ�����������֪ͨ��������0000000001*/
	/**������*/
	private LCPolSchema mLCPolSchema = new LCPolSchema();
	/** ��ȫ�˱����� */
	private LPUWMasterSchema mLPUWMasterSchema = new LPUWMasterSchema();
	/** ��ӡ����� */
	private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();

	public PEdorRePrintSendNoticeAfterInitService() {
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

	//Ϊ��������һ�ڵ������ֶ�׼������
	if(!prepareTransferData())
		return false;

	//׼������̨������
	if (!prepareOutputData())
	  return false;

	  System.out.println("Start SysUWNoticeBL Submit...");

	//mResult.clear();
	return true;
  }


  /**
   * У��ҵ������
   * @return
   */
  private boolean checkData()
  {
	//У�鱣����Ϣ
	LCPolDB tLCPolDB = new LCPolDB();
	tLCPolDB.setPolNo(mPolNo) ;
	if(!tLCPolDB.getInfo())
	{
	  CError tError = new CError();
	  tError.moduleName = "PEdorUWAutoHealthAfterInitService";
	  tError.functionName = "checkData";
	  tError.errorMessage = "����"+mPolNo+"��Ϣ��ѯʧ��!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}
	mLCPolSchema.setSchema(tLCPolDB) ;

	//У���Ƿ���Ҫ����ĺ˱�֪ͨ��
	LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();
	LOPRTManagerSet tLOPRTManagerSet = new LOPRTManagerSet();
	tLOPRTManagerDB.setPrtSeq(mLOPRTManagerSchema.getPrtSeq()) ;
	tLOPRTManagerSet = tLOPRTManagerDB.query() ;
	if(tLOPRTManagerSet == null )
	{
	  CError tError = new CError();
	  tError.moduleName = "PEdorRePrintAutoHealthAfterInitService";
	  tError.functionName = "checkData";
	  tError.errorMessage = "�˱�֪ͨ��"+mLOPRTManagerSchema.getPrtSeq()+"��Ϣ��ѯʧ��!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}

	if(tLOPRTManagerSet.size() != 1)
	{
	  CError tError = new CError();
	  tError.moduleName = "PEdorRePrintAutoHealthAfterInitService";
	  tError.functionName = "checkData";
	  tError.errorMessage = "�˱�֪ͨ��"+mLOPRTManagerSchema.getPrtSeq()+"��Ϣ��ѯʧ��!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}

	 mLOPRTManagerSchema = tLOPRTManagerSet.get(1);
	 if(!mLOPRTManagerSchema.getCode().equals("25"))
	 {
	  CError tError = new CError();
	  tError.moduleName = "PEdorRePrintAutoHealthAfterInitService";
	  tError.functionName = "checkData";
	  tError.errorMessage = "�˱�֪ͨ��"+mLOPRTManagerSchema.getPrtSeq()+"����ʶ��Ϣ��ѯʧ��!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	 }

	 if(mLOPRTManagerSchema.getStateFlag() == null || !mLOPRTManagerSchema.getStateFlag().equals("1"))
	 {
	  CError tError = new CError();
	  tError.moduleName = "PEdorRePrintAutoHealthAfterInitService";
	  tError.functionName = "checkData";
	  tError.errorMessage = "�˱�֪ͨ��"+mLOPRTManagerSchema.getPrtSeq()+"��ʶδ�����Ѵ�ӡ,��δ����״̬!";
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

	//��ӡ����
	if (preparePrint() == false)
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
	  tError.moduleName = "PEdorUWAutoHealthAfterInitService";
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
	  tError.moduleName = "PEdorUWAutoHealthAfterInitService";
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
	  tError.moduleName = "PEdorUWAutoHealthAfterInitService";
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
	  tError.moduleName = "PEdorUWAutoHealthAfterInitService";
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
	  tError.moduleName = "PEdorUWAutoHealthAfterInitService";
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
	  tError.moduleName = "PEdorUWAutoHealthAfterInitService";
	  tError.functionName = "getInputData";
	  tError.errorMessage = "ǰ̨����ҵ��������PolNoʧ��!";
	  this.mErrors .addOneError(tError) ;
	  return false;
	}


	//��õ�ǰ�������������ID
	   mLOPRTManagerSchema = (LOPRTManagerSchema)mTransferData.getValueByName("LOPRTManagerSchema");
	   if ( mLOPRTManagerSchema == null  )
	   {
		 // @@������
		 //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
		 CError tError = new CError();
		 tError.moduleName = "PEdorUWAutoHealthAfterInitService";
		 tError.functionName = "getInputData";
		 tError.errorMessage = "ǰ̨����ҵ��������LOPRTManagerSchemaʧ��!";
		 this.mErrors .addOneError(tError) ;
		 return false;
	   }

	   if(mLOPRTManagerSchema.getPrtSeq() == null || mLOPRTManagerSchema.getPrtSeq().trim().equals(""))
	   {
		 // @@������
		 //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
		 CError tError = new CError();
		 tError.moduleName = "PEdorUWAutoHealthAfterInitService";
		 tError.functionName = "getInputData";
		 tError.errorMessage = "ǰ̨����ҵ��������LOPRTManagerSchema�еĴ�ӡ��ˮ��ʧ��!";
		 this.mErrors .addOneError(tError) ;
		 return false;
	   }

	return true;
  }


   /**
	* ��ӡ��Ϣ��
	* @return
	*/
   private boolean preparePrint()
   {
	 if(mLOPRTManagerSchema.getPatchFlag() != null && mLOPRTManagerSchema.getPatchFlag().equals("1"))
	 {
	   mLOPRTManagerSchema.setOldPrtSeq(mLOPRTManagerSchema.getOldPrtSeq());
	 }
	 else
	 {
	   mLOPRTManagerSchema.setOldPrtSeq(mLOPRTManagerSchema.getPrtSeq());
	 }
	 mLOPRTManagerSchema.setPatchFlag("1");
	 mLOPRTManagerSchema.setStateFlag("0");

	 String strNoLimit = PubFun.getNoLimit( mGlobalInput.ComCode );
	 mLOPRTManagerSchema.setPrtSeq(PubFun1.CreateMaxNo("PRTSEQNO", strNoLimit));

	 mLOPRTManagerSchema.setMakeDate(PubFun.getCurrentDate());
	 mLOPRTManagerSchema.setMakeTime(PubFun.getCurrentTime());

	 return true;
   }

   /**
	* Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
	 * @return
	 */
	private boolean prepareTransferData()
	{
	  LAAgentDB tLAAgentDB = new LAAgentDB();
	  LAAgentSet tLAAgentSet = new LAAgentSet();
	  tLAAgentDB.setAgentCode(mLCPolSchema.getAgentCode());
	  tLAAgentSet = tLAAgentDB.query() ;
	  if(tLAAgentSet == null || tLAAgentSet.size() != 1)
	  {
		// @@������
		CError tError = new CError();
		tError.moduleName = "PEdorUWAutoHealthAfterInitService";
		tError.functionName = "prepareTransferData";
		tError.errorMessage = "�����˱�LAAgent��ѯʧ��!";
		this.mErrors.addOneError(tError) ;
		return false;
	  }

	  if(tLAAgentSet.get(1).getAgentGroup()==null)
	  {
		// @@������
		CError tError = new CError();
		tError.moduleName = "PEdorUWAutoHealthAfterInitService";
		tError.functionName = "prepareTransferData";
		tError.errorMessage = "�����˱�LAAgent�еĴ���������ݶ�ʧ!";
		this.mErrors.addOneError(tError) ;
		return false;
	  }

	  LABranchGroupDB  tLABranchGroupDB = new LABranchGroupDB();
	  LABranchGroupSet tLABranchGroupSet = new LABranchGroupSet();
	  tLABranchGroupDB.setAgentGroup(tLAAgentSet.get(1).getAgentGroup());
	  tLABranchGroupSet = tLABranchGroupDB.query();
	  if(tLABranchGroupSet == null || tLABranchGroupSet.size() != 1)
	  {
		// @@������
		CError tError = new CError();
		tError.moduleName = "PEdorUWAutoHealthAfterInitService";
		tError.functionName = "prepareTransferData";
		tError.errorMessage = "������չҵ������LABranchGroup��ѯʧ��!";
		this.mErrors.addOneError(tError) ;
		return false;
	  }

	  if(tLABranchGroupSet.get(1).getBranchAttr()==null)
	  {
	   // @@������
	   CError tError = new CError();
	   tError.moduleName = "PEdorUWAutoHealthAfterInitService";
	   tError.functionName = "prepareTransferData";
	   tError.errorMessage = "������չҵ������LABranchGroup��չҵ������Ϣ��ʧ!";
	   this.mErrors.addOneError(tError) ;
	   return false;
	  }

	  mTransferData.setNameAndValue("PrtSeq",mLOPRTManagerSchema.getPrtSeq());
	  mTransferData.setNameAndValue("OldPrtSeq",mLOPRTManagerSchema.getOldPrtSeq());
	  mTransferData.setNameAndValue("AgentCode",mLCPolSchema.getAgentCode()) ;
	  mTransferData.setNameAndValue("AgentGroup",tLAAgentSet.get(1).getAgentGroup()) ;
	  mTransferData.setNameAndValue("BranchAttr",tLABranchGroupSet.get(1).getBranchAttr()) ;
	  mTransferData.setNameAndValue("ManageCom",mLCPolSchema.getManageCom());
	  return true;
	}

  private boolean prepareOutputData()
  {
	mResult.clear();
	MMap map = new MMap();

	//������֪ͨ���ӡ���������
	map.put(mLOPRTManagerSchema, "INSERT");

	mResult.add(map) ;
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
  public static void main(String[] args) {
	//SysUWNoticeBL sysUWNoticeBL1 = new SysUWNoticeBL();
  }
}
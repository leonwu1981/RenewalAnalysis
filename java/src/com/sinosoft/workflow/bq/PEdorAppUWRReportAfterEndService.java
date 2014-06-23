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
 * <p>Title: �������ڵ�����:��ȫ���뷢���֪ͨ��</p>
 * <p>Description: �����������֪ͨ��AfterEnd������ </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Lanjun
 * @version 1.0
 */

public class PEdorAppUWRReportAfterEndService implements AfterEndService{

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
        private String mEdorAcceptNo;
        private String mContNo;
        private String mInsuredNo;
        private String mMissionID ;
        private String mSubMissionID ;
        private Reflections mReflections = new Reflections();

   /**ִ�б�ȫ��������Լ�������0000000019*/
   /**������*/
   private LCPolSchema mLCPolSchema = new LCPolSchema();
   /** ����������ڵ��*/
   private LWMissionSchema mInitLWMissionSchema = new LWMissionSchema();//��ȫ�˹��˱���������ʼ�ڵ�
   /** ����������ڵ㱸�ݱ�*/
   private LBMissionSet mLBMissionSet = new LBMissionSet();

  public PEdorAppUWRReportAfterEndService() {
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
          tError.moduleName = "PEdorUWAutoHealthAfterEndService";
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
          tError.moduleName = "PEdorUWAutoHealthAfterEndService";
          tError.functionName = "getInputData";
          tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
          this.mErrors .addOneError(tError) ;
          return false;
        }

        //��õ�½��������
        mManageCom = mGlobalInput.ManageCom;
        System.out.println("class ��Ϣ:ManageCom"+mManageCom);
        if ( mManageCom == null || mManageCom.trim().equals("") )
        {
          // @@������
          //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
          CError tError = new CError();
          tError.moduleName = "PEdorUWAutoHealthAfterEndService";
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
          tError.moduleName = "PEdorUWAutoHealthAfterEndService";
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
          tError.moduleName = "PEdorUWAutoHealthAfterEndService";
          tError.functionName = "getInputData";
          tError.errorMessage = "ǰ̨����ҵ��������EdorAcceptNoʧ��!";
          this.mErrors .addOneError(tError) ;
          return false;
        }
//
//	mContNo = (String)mTransferData.getValueByName("ContNo");
//	if ( mContNo == null  )
//	{
//	  // @@������
//	  //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
//	  CError tError = new CError();
//	  tError.moduleName = "PEdorUWAutoHealthAfterEndService";
//	  tError.functionName = "getInputData";
//	  tError.errorMessage = "ǰ̨����ҵ��������PolNoʧ��!";
//	  this.mErrors .addOneError(tError) ;
//	  return false;
//	}
//
//
//
//	//���ҵ�����֪ͨ����
//
        //��õ�ǰ�������������ID
          mMissionID = (String)mTransferData.getValueByName("MissionID");
         System.out.println("class��Ϣ��PEdorAppUWRReportAfterEndService --->mMissionID="+mMissionID);
           if ( mMissionID == null  )
          {
                // @@������
                //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                CError tError = new CError();
                tError.moduleName = "PEdorUWAutoHealthAfterEndService";
                tError.functionName = "getInputData";
                tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
                this.mErrors .addOneError(tError) ;
                return false;
         }

         //��õ�ǰ�������������ID
          mSubMissionID = (String)mTransferData.getValueByName("SubMissionID");
         System.out.println("class��Ϣ��PEdorAppUWRReportAfterEndService --->mSubMissionID="+mSubMissionID);
           if ( mMissionID == null  )
          {
                // @@������
                //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
                CError tError = new CError();
                tError.moduleName = "PEdorUWAutoHealthAfterEndService";
                tError.functionName = "getInputData";
                tError.errorMessage = "ǰ̨����ҵ��������SubMissionIDʧ��!";
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

         //�޸ı�ȫ�˹��˱�����ʼ�ڵ�״̬Ϊδ�ظ�
         LWMissionDB tLWMissionDB = new LWMissionDB();
         LWMissionSet tLWMissionSet = new LWMissionSet();
         String tStr = "Select * from LWMission where MissionID = '"+mMissionID+"'"
                          + "  and ActivityID = '0000000022'";
        System.out.println(tStr);

         tLWMissionSet = tLWMissionDB.executeQuery(tStr);
         if(tLWMissionSet == null || tLWMissionSet.size() !=1)
         {
           // @@������
           this.mErrors.copyAllErrors(tLWMissionSet.mErrors);
           CError tError =new CError();
           tError.moduleName="PEdorUWAutoHealthAfterEndService";
           tError.functionName="prepareMission";
           tError.errorMessage="��ѯ��������ȫ�˹��˱�����ʼ����ڵ�ʧ��!";
           this.mErrors .addOneError(tError) ;
           return false;
         }
         mInitLWMissionSchema = tLWMissionSet.get(1) ;
         mInitLWMissionSchema.setActivityStatus("2");
         mInitLWMissionSchema.setDefaultOperator(mOperater);

         return true;
   }


  private boolean prepareOutputData()
 {
         mResult.clear();
         MMap map = new MMap();

         //��ӱ�ȫ��������ʼ����ڵ������
         if(mInitLWMissionSchema != null  )
         {
           map.put(mInitLWMissionSchema, "UPDATE");
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

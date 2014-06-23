/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.xb;

import com.sinosoft.lis.db.LCIssuePolDB;
import com.sinosoft.lis.db.LCUWMasterDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LCIssuePolSchema;
import com.sinosoft.lis.schema.LCUWMasterSchema;
import com.sinosoft.lis.schema.LOPRTManagerSchema;
import com.sinosoft.lis.vschema.LCIssuePolSet;
import com.sinosoft.lis.vschema.LCUWMasterSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.BeforeInitService;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author lh
 * @version 1.0
 */
public class PRnewUWAutoHealthBeforeInitService implements BeforeInitService
{

    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    private VData mResult = new VData();
    /** �����洫�����ݵ����� */
    private VData mInputData = new VData();
    /** ȫ������ */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();
    /** ���ݲ����ַ��� */
//    private String mOperate;
    /** ҵ������ر��� */
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();
    private LCUWMasterSet mLCUWMasterSet = new LCUWMasterSet();
    private LCIssuePolSet mLCIssuePolSet = new LCIssuePolSet();

    public PRnewUWAutoHealthBeforeInitService()
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
//	//���������ݿ�����������
//	this.mOperate =cOperate;
//	//�õ��ⲿ���������,�����ݱ��ݵ�������
//	if (!getInputData(cInputData))
//	  return false;
//	//����ҵ����
//	if (mOperate.equals("TakeBack")){
//	  if (!dealData()){
//		// @@������
//		CError tError = new CError();
//		tError.moduleName = "SysUWNoticeBL";
//		tError.functionName = "submitData";
//		tError.errorMessage = "���ݴ���ʧ��SysUWNoticeBL-->dealData!";
//		this.mErrors.addOneError(tError) ;
//		return false;
//	  }
//	}
//
//	//׼������̨������
//	if (!prepareOutputData())
//	  return false;
//
//	  System.out.println("Start SysUWNoticeBL Submit...");
//	  SysUWNoticeBLS tSysUWNoticeBLS=new SysUWNoticeBLS();
//	  tSysUWNoticeBLS.submitData(mInputData,mOperate);
//	  System.out.println("End SysUWNoticeBL Submit...");
//	  //�������Ҫ����Ĵ����򷵻�
//	  if (tSysUWNoticeBLS.mErrors.needDealError())
//	  {
//		// @@������
//		this.mErrors.copyAllErrors(tSysUWNoticeBLS.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "SysUWNoticeBL";
//		tError.functionName = "submitDat";
//		tError.errorMessage ="�����ύʧ��!";
//		this.mErrors.addOneError(tError) ;
//		return false;
//	  }
//
        mResult.clear();
        return true;
    }

    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {
        String tPolNo = mLOPRTManagerSchema.getOtherNo();
        String tsql = "Select * from lcuwmaster where proposalno in (select proposalno from lcpol where mainpolno = (select mainpolno from lcpol where proposalno = '"
                + tPolNo + "')) and PrintFlag = '2'";
        System.out.println(tsql);
        LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
        LCUWMasterSet tLCUWMasterSet = new LCUWMasterSet();
        tLCUWMasterSet = tLCUWMasterDB.executeQuery(tsql);
        if (tLCUWMasterSet.size() > 0)
        {
            for (int i = 1; i <= tLCUWMasterSet.size(); i++)
            {
                LCUWMasterSchema tLCUWMasterSchema = new LCUWMasterSchema();

                tLCUWMasterSchema = tLCUWMasterSet.get(i);

                //�˱�֪ͨ��ǻظ�
                if (tLCUWMasterSchema.getPrintFlag().equals("1")
                        && !tLCUWMasterSchema.getSpecFlag().equals("1")
                        && !tLCUWMasterSchema.getChangePolFlag().equals("1"))
                {
                    tLCUWMasterSchema.setPrintFlag("3");
                }

                LCIssuePolSet tLCIssuePolSet = new LCIssuePolSet();
                LCIssuePolDB tLCIssuePolDB = new LCIssuePolDB();
//                tLCIssuePolDB.setProposalNo(tLCUWMasterSchema.getProposalNo());
                tLCIssuePolDB.setBackObjType("3");
                tLCIssuePolDB.setNeedPrint("Y");
                tLCIssuePolSet.set(tLCIssuePolDB.query());
                //�������Ҫ����Ĵ����򷵻�
                if (tLCIssuePolDB.mErrors.needDealError())
                {
                    // @@������
                    mErrors.copyAllErrors(tLCIssuePolDB.mErrors);
                    CError tError = new CError();
                    tError.moduleName = "SysUWNoticeBL";
                    tError.functionName = "DealData";
                    tError.errorMessage = "���ݲ�ѯʧ��!";
                    mErrors.addOneError(tError);
                    return false;
                }
                LCIssuePolSchema tLCIssuePolSchema;
                for (int j = 1; j <= tLCIssuePolSet.size(); j++)
                {
                    tLCIssuePolSchema = tLCIssuePolSet.get(j);
                    tLCIssuePolSchema.setReplyMan("SYS002");
                    tLCIssuePolSchema.setReplyResult("�Զ��ظ�");
                    tLCIssuePolSchema.setNeedPrint("P");
                    mLCIssuePolSet.add(tLCIssuePolSchema);
                }

                ExeSQL tExeSQL = new ExeSQL();
                String asql = "select count(1) from LCIssuePol where ProposalNo='"
                        + tLCUWMasterSchema.getProposalNo()
                        +
                        "' and ((backobjtype = '2' and needprint = 'Y') or (backobjtype = '4')) and ReplyResult is null"; //�����ǲ���Ա��������Է��ظ�ҵ��Ա��Ҫ��ӡ�Ļ��з��ظ������������У�飬�ж��ǲ����������û�лظ�
                String tNumber = tExeSQL.getOneValue(asql);
                if (tNumber.equals("0"))
                {
                    tLCUWMasterSchema.setQuesFlag("2");
                }
//          if(tLCUWMasterSchema.getQuesFlag().equals("1"))

                mLCUWMasterSet.add(tLCUWMasterSchema);
            }
        }
//    else
//    {
//      tsql = "Select * from lcuwmaster where proposalno in (select proposalno from lcpol where mainpolno = (select mainpolno from lcpol where proposalno = '"+tPolNo+"'))";
//      System.out.println(tsql);
//      LCUWMasterDB ttLCUWMasterDB = new LCUWMasterDB();
//      LCUWMasterSet ttLCUWMasterSet = new LCUWMasterSet();
//
//      ttLCUWMasterSet = ttLCUWMasterDB.executeQuery(tsql);
//
//      for(int i = 1;i <= ttLCUWMasterSet.size();i++)
//      {
//        LCUWMasterSchema tLCUWMasterSchema = new LCUWMasterSchema();
//
//        tLCUWMasterSchema = ttLCUWMasterSet.get(i);
//
//        //�˱�֪ͨ��ǻظ�
//        if((tLCUWMasterSchema.getPrintFlag().equals("1")&&!tLCUWMasterSchema.getSpecFlag().equals("1")&&!tLCUWMasterSchema.getChangePolFlag().equals("1"))||tLCUWMasterSchema.getPrintFlag().equals("3"))
//          tLCUWMasterSchema.setPrintFlag("2");
//
//        if(tLCUWMasterSchema.getQuesFlag().equals("1"))
//          tLCUWMasterSchema.setQuesFlag("2");
//
//        mLCUWMasterSet.add(tLCUWMasterSchema);
//      }
//    }
        return true;
    }


    /**
     * �����������еõ����ж���
     *��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean getInputData(VData cInputData)
    {
        this.mLOPRTManagerSchema.setSchema((LOPRTManagerSchema) cInputData.getObjectByObjectName(
                "LOPRTManagerSchema", 0));
        this.mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
        return true;
    }

    private boolean prepareOutputData()
    {
        try
        {
            this.mInputData.clear();
            this.mInputData.add(this.mLCUWMasterSet);
            this.mInputData.add(this.mLCIssuePolSet);

            this.mResult.clear();
            this.mResult.add(this.mLCUWMasterSet);
            this.mResult.add(this.mLCIssuePolSet);
        }
        catch (Exception ex)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "SysUWNoticeBL";
            tError.functionName = "prepareData";
            tError.errorMessage = "��׼������㴦������Ҫ������ʱ����";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    public VData getResult()
    {
        return this.mResult;
    }

    public TransferData getReturnTransferData()
    {
        return mTransferData;
    }

    public CErrors getErrors()
    {
        return mErrors;
    }

    public static void main(String[] args)
    {
        //SysUWNoticeBL sysUWNoticeBL1 = new SysUWNoticeBL();
    }
}

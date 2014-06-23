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

    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    private VData mResult = new VData();
    /** 往后面传输数据的容器 */
    private VData mInputData = new VData();
    /** 全局数据 */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();
    /** 数据操作字符串 */
//    private String mOperate;
    /** 业务处理相关变量 */
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();
    private LCUWMasterSet mLCUWMasterSet = new LCUWMasterSet();
    private LCIssuePolSet mLCIssuePolSet = new LCIssuePolSet();

    public PRnewUWAutoHealthBeforeInitService()
    {
    }

    /**
     * 传输数据的公共方法
     * @param: cInputData 输入的数据
     *         cOperate 数据操作
     * @return:
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
//	//将操作数据拷贝到本类中
//	this.mOperate =cOperate;
//	//得到外部传入的数据,将数据备份到本类中
//	if (!getInputData(cInputData))
//	  return false;
//	//进行业务处理
//	if (mOperate.equals("TakeBack")){
//	  if (!dealData()){
//		// @@错误处理
//		CError tError = new CError();
//		tError.moduleName = "SysUWNoticeBL";
//		tError.functionName = "submitData";
//		tError.errorMessage = "数据处理失败SysUWNoticeBL-->dealData!";
//		this.mErrors.addOneError(tError) ;
//		return false;
//	  }
//	}
//
//	//准备往后台的数据
//	if (!prepareOutputData())
//	  return false;
//
//	  System.out.println("Start SysUWNoticeBL Submit...");
//	  SysUWNoticeBLS tSysUWNoticeBLS=new SysUWNoticeBLS();
//	  tSysUWNoticeBLS.submitData(mInputData,mOperate);
//	  System.out.println("End SysUWNoticeBL Submit...");
//	  //如果有需要处理的错误，则返回
//	  if (tSysUWNoticeBLS.mErrors.needDealError())
//	  {
//		// @@错误处理
//		this.mErrors.copyAllErrors(tSysUWNoticeBLS.mErrors);
//		CError tError = new CError();
//		tError.moduleName = "SysUWNoticeBL";
//		tError.functionName = "submitDat";
//		tError.errorMessage ="数据提交失败!";
//		this.mErrors.addOneError(tError) ;
//		return false;
//	  }
//
        mResult.clear();
        return true;
    }

    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
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

                //核保通知标记回复
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
                //如果有需要处理的错误，则返回
                if (tLCIssuePolDB.mErrors.needDealError())
                {
                    // @@错误处理
                    mErrors.copyAllErrors(tLCIssuePolDB.mErrors);
                    CError tError = new CError();
                    tError.moduleName = "SysUWNoticeBL";
                    tError.functionName = "DealData";
                    tError.errorMessage = "数据查询失败!";
                    mErrors.addOneError(tError);
                    return false;
                }
                LCIssuePolSchema tLCIssuePolSchema;
                for (int j = 1; j <= tLCIssuePolSet.size(); j++)
                {
                    tLCIssuePolSchema = tLCIssuePolSet.get(j);
                    tLCIssuePolSchema.setReplyMan("SYS002");
                    tLCIssuePolSchema.setReplyResult("自动回复");
                    tLCIssuePolSchema.setNeedPrint("P");
                    mLCIssuePolSet.add(tLCIssuePolSchema);
                }

                ExeSQL tExeSQL = new ExeSQL();
                String asql = "select count(1) from LCIssuePol where ProposalNo='"
                        + tLCUWMasterSchema.getProposalNo()
                        +
                        "' and ((backobjtype = '2' and needprint = 'Y') or (backobjtype = '4')) and ReplyResult is null"; //不考虑操作员问题件，对返回给业务员需要打印的还有返回给机构的问题件校验，判断是不是有问题件没有回复
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
//        //核保通知标记回复
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
     * 从输入数据中得到所有对象
     *输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
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
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "SysUWNoticeBL";
            tError.functionName = "prepareData";
            tError.errorMessage = "在准备往后层处理所需要的数据时出错。";
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

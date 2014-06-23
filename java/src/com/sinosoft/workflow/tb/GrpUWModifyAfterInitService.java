/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LCBDutyDB;
import com.sinosoft.lis.db.LCBGetDB;
import com.sinosoft.lis.db.LCBPremDB;
import com.sinosoft.lis.db.LCContDB;
import com.sinosoft.lis.db.LCDutyDB;
import com.sinosoft.lis.db.LCGILTraceDB;
import com.sinosoft.lis.db.LCGetDB;
import com.sinosoft.lis.db.LCGrpContDB;
import com.sinosoft.lis.db.LCPremDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.schema.LCGILTraceSchema;
import com.sinosoft.lis.schema.LCGrpContSchema;
import com.sinosoft.lis.vschema.LCBDutySet;
import com.sinosoft.lis.vschema.LCBGetSet;
import com.sinosoft.lis.vschema.LCBPremSet;
import com.sinosoft.lis.vschema.LCContSet;
import com.sinosoft.lis.vschema.LCDutySet;
import com.sinosoft.lis.vschema.LCGILTraceSet;
import com.sinosoft.lis.vschema.LCGetSet;
import com.sinosoft.lis.vschema.LCPremSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: 工作流服务类:团体新契约核保订正 </p>
 * <p>Description:团体核保订正工作流AfterInit服务类 </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class GrpUWModifyAfterInitService implements AfterInitService
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 往界面传输数据的容器 */
    private VData mResult = new VData();

    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();

    private TransferData mTransferData = new TransferData();
    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();
    private Reflections mReflections = new Reflections();

    /** 业务数据类 */
    private LCContSet mLCContSet = new LCContSet();
//    private LCPolSet mLCPolSet = new LCPolSet();

    /** 数据操作字符串 */
    private String mOperater;
    private String mManageCom;
    private String mMissionID;
    private String mGrpContNo;
    private String mPrtNo;
    
    private MMap map = new MMap();

    public GrpUWModifyAfterInitService()
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
        //得到外部传入的数据,将数据备份到本类中
        if (!getInputData(cInputData, cOperate))
        {
            return false;
        }

        //校验是否有未打印的体检通知书
        if (!checkData())
        {
            return false;
        }

        System.out.println("Start  dealData...");

        //进行业务处理
        if (!dealData())
        {
            return false;
        }

        System.out.println("dealData successful!");

        //为工作流下一节点属性字段准备数据
        if (!prepareTransferData())
        {
            return false;
        }

        //准备往后台的数据
        if (!prepareOutputData())
        {
            return false;
        }

        System.out.println("Start  Submit...");

        return true;
    }


    /**
     * 准备返回前台统一存储数据
     * 输出：如果发生错误则返回false,否则返回true
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        mResult.add(map);
        return true;
    }


    /**
     * 校验业务数据
     * @return
     */
    private boolean checkData()
    {
        //校验保单信息
        LCGrpContDB tLCGrpContDB = new LCGrpContDB();
        tLCGrpContDB.setGrpContNo(mGrpContNo);
        if (!tLCGrpContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "UWRReportModifyAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "集体保单" + mGrpContNo + "信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCGrpContSchema.setSchema(tLCGrpContDB);
        mPrtNo = mLCGrpContSchema.getPrtNo();

        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setPrtNo(mPrtNo);
        mLCContSet = tLCContDB.query();
        if (mLCContSet == null || mLCContSet.size() <= 0)
        {
            CError tError = new CError();
            tError.moduleName = "UWRReportModifyAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "个人合同保单信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * 从输入数据中得到所有对象
     *输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean getInputData(VData cInputData, String cOperate)
    {
        //从输入数据中得到所有对象
        //获得全局公共数据
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);

        if (mGlobalInput == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpUWModifyAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得操作员编码
        mOperater = mGlobalInput.Operator;
        if (mOperater == null || mOperater.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpUWModifyAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据Operate失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得登陆机构编码
        mManageCom = mGlobalInput.ManageCom;
        if (mManageCom == null || mManageCom.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ProposalApproveAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的任务ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpUWModifyAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的GrpContNo
        mGrpContNo = (String) mTransferData.getValueByName("GrpContNo");
        if (mGrpContNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpUWModifyAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中GrpContNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     */
    private boolean dealData()
    {
//        LCPolDB tLCPolDB = null;
//        LCUWSubDB tLCUWSubDB =null;
//        for(int i =1;i<mLCContSet.size();i++)
//        {
//            //查询合同核保主表，如果有uwflag等于5的纪录，则说明自核不通过，没有则说明自核通过
//            //核保订正后使其恢复自核后的状态
//            String tSql = "select distinct 1 from LCCUWSub where 1=1 "
//                          + " and ContNo = '"+mLCContSet.get(i).getContNo() + "'"
//                          + " and uwflag = '5'"
//                          ;
//            ExeSQL lccuw = new ExeSQL();
//            String rs = lccuw.getOneValue(tSql);
//            if(rs.equals("1"))
//            {
//                //此分支表示自核未通过的保单
//                mLCContSet.get(i).setUWFlag("5");
//                tLCPolDB = new LCPolDB();
//                tLCPolDB.setContNo(mLCContSet.get(i).getContNo());
//                mLCPolSet = tLCPolDB.query();
//                if(mLCPolSet==null||mLCPolSet.size()<=0)
//                {
//                    // @@错误处理
//                    //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
//                    CError tError = new CError();
//                    tError.moduleName = "GrpUWModifyAfterInitService";
//                    tError.functionName = "dealData";
//                    tError.errorMessage = "查询险种保单表失败!";
//                    this.mErrors.addOneError(tError);
//                    return false;
//                }
//                for(int j=1;j<=mLCPolSet.size();j++)
//                {
//                    String tPolSql = "select distinct 1 from LCUWSub where 1=1 "
//                          + " and PolNo = '"+mLCPolSet.get(j).getPolNo() + "'"
//                          + " and uwflag = '5'"
//                          ;
//                  ExeSQL lcuw = new ExeSQL();
//                  String rsc = lcuw.getOneValue(tSql);
//                  if (rsc.equals("1"))
//                    {
//                        mLCPolSet.get(j).setUWFlag("5");
//                    }
//                }
//            }
//        }
    	String mGrpContSql = "update lcgrpcont set uwflag= 'z' where grpcontno = '" + mGrpContNo + "'";

    	String mGrpPolSql = " update lcgrppol set uwflag = 'z' where grpcontno = '" + mGrpContNo + "'";

    	String mContSql = "update lccont set uwflag = 'z' where contno in (select distinct contno from LCCUWSub where grpcontno = '"
                + mGrpContNo + "' and passflag = '5')";

    	String mPolSql = "update lcpol set uwflag = 'z' where polno in (select distinct polno from lcuwsub where grpcontno = '"
                + mGrpContNo + "' and passflag = '5')";

    	String mCCUWMasterSql = "update lccuwmaster set passflag = 'z',uwno=uwno+1 where contno in (select distinct contno from LCCUWSub where grpcontno = '"
                + mGrpContNo + "' and passflag = '5')";

    	String mCCUWSubSql = "insert into lccuwsub select * from lccuwmaster where contno in (select distinct contno from LCCUWSub where grpcontno = '"
                + mGrpContNo + "' and passflag = '5') ";

    	String mCUWMasterSql = "update lcuwmaster set passflag = 'z',uwno=uwno+1 where polno in (select distinct polno from lcuwsub where grpcontno = '"
                + mGrpContNo + "' and passflag = '5')";

    	String mCUWSubSql = "insert into lcuwsub select * from lcuwmaster where polno in (select distinct polno from lcuwsub where grpcontno = '"
                + mGrpContNo + "' and passflag = '5') and passflag = 'z'";

    	String mGCUWMasterSql = "update lcgcuwmaster set passflag = 'z',uwno=uwno+1 where grpcontno = (select grpcontno from lcgrpcont where prtno = '"
                + mPrtNo + "')";

    	String mGCUWSubSql = "insert into lcgcuwsub select * from lcgcuwmaster where grpcontno = (select grpcontno from lcgrpcont where prtno = '"
                + mPrtNo + "')";

    	String mGUWMasterSql = "update lcguwmaster set passflag = 'z',uwno=uwno+1 where grpcontno = (select grpcontno from lcgrpcont where prtno = '"
                + mPrtNo + "')";

    	String mGUWSubSql = "insert into lcguwsub select * from lcguwmaster where grpcontno = (select grpcontno from lcgrpcont where prtno = '"
                + mPrtNo + "')";
    	
    	//还原人工核保阶段处理的GIL信息
    	String gilLCPrem = " update lcprem a set "
    		+ " a.sumprem=(select sumprem from lcbprem where serialno='Q'  and polno=a.polno and dutycode=a.dutycode and payplancode=a.payplancode), "
    		+ " a.prem=(select prem from lcbprem where serialno='Q'  and polno=a.polno and dutycode=a.dutycode and payplancode=a.payplancode), "
    		+ " a.standprem=(select standprem from lcbprem where serialno='Q'  and polno=a.polno and dutycode=a.dutycode and payplancode=a.payplancode) "
    		+ " where grpcontno='"+mGrpContNo+"' "
    		+ " and exists(select 1 from lcbprem where serialno='Q'  and polno=a.polno and dutycode=a.dutycode and payplancode=a.payplancode) ";
    	String gilLCGet = "  update lcget a set "
    		+ " a.standmoney=(select standmoney from lcbget where serialno='Q'  and polno=a.polno and dutycode=a.dutycode and getdutycode=a.getdutycode), "
    		+ " a.actuget=(select actuget from lcbget where serialno='Q'  and polno=a.polno and dutycode=a.dutycode and getdutycode=a.getdutycode) "
    		+ " where grpcontno='"+mGrpContNo+"' "
    		+ " and exists(select 1 from lcbget where serialno='Q'  and polno=a.polno and dutycode=a.dutycode and getdutycode=a.getdutycode) ";
    	String gilLCDuty = " update lcduty a set "
    		+ " a.sumprem=(select sumprem from lcbduty where serialno='Q'  and polno=a.polno and dutycode=a.dutycode), "
    		+ " a.prem=(select prem from lcbduty where serialno='Q'  and polno=a.polno and dutycode=a.dutycode), "
    		+ " a.standprem=(select standprem from lcbduty where serialno='Q'  and polno=a.polno and dutycode=a.dutycode), "
    		+ " a.amnt=(select amnt from lcbduty where serialno='Q'  and polno=a.polno and dutycode=a.dutycode), "
    		+ " a.subgil=(select subgil from lcbduty where serialno='Q'  and polno=a.polno and dutycode=a.dutycode), "
    		+ " a.riskamnt=(select riskamnt from lcbduty where serialno='Q'  and polno=a.polno and dutycode=a.dutycode) "
    		+ " where exists(select 1 from lcpol where grpcontno='"+mGrpContNo+"' and polno=a.polno) "
    		+ " and exists(select 1 from lcbduty where serialno='Q'  and polno=a.polno and dutycode=a.dutycode) ";
    	String gilLCPol = " update lcpol a set " 
    		+ " a.sumprem=(select nvl(sum(sumprem),0) from lcduty where polno=a.polno), "
			+ " a.prem=(select nvl(sum(prem),0) from lcduty where polno=a.polno), "
			+ " a.standprem=(select nvl(sum(standprem),0) from lcduty where polno=a.polno), "
			+ " a.amnt=(select nvl(max(amnt),0) from lcduty where polno=a.polno), "
			+ " a.riskamnt=(select nvl(max(riskamnt),0) from lcduty where polno=a.polno), "
			+ " a.subgil =(select nvl(max(subgil),0) from lcduty where polno=a.polno) "
			+ " where a.grpcontno='"+mGrpContNo+"' "
			+ " and exists(select 1 from lcbduty where serialno='Q'  and polno=a.polno) ";
    	String gilLCCont =" update lccont a set "
    		+ " a.amnt=(select nvl(sum(amnt),0) from lcpol where contno=a.contno), "
    		+ " a.prem=(select nvl(sum(prem),0) from lcpol where contno=a.contno), "
    		+ " a.sumprem=(select nvl(sum(sumprem),0) from lcpol where contno=a.contno) "
    		+ " where a.grpcontno='"+mGrpContNo+"' "
    		+ " and exists(select 1 from lcbduty where serialno='Q'  and contno = a.contno) ";
    	String gilLLCGrpPol = " update lcgrppol a set "
    		+ " a.amnt=(select nvl(sum(amnt),0) from lcpol where grppolno=a.grppolno), "
    		+ " a.prem=(select nvl(sum(prem),0) from lcpol where grppolno=a.grppolno), "
    		+ " a.sumprem=(select nvl(sum(sumprem),0) from lcpol where grppolno=a.grppolno) "
    		+ " where a.grpcontno='"+mGrpContNo+"' ";
    	String gilLCGrpCont =" update lcgrpcont a set "
    		+ " a.amnt=(select nvl(sum(amnt),0) from lcpol where grpcontno=a.grpcontno), "
    		+ " a.prem=(select nvl(sum(prem),0) from lcpol where grpcontno=a.grpcontno), "
    		+ " a.sumprem=(select nvl(sum(sumprem),0) from lcpol where grpcontno=a.grpcontno) "
    		+ " where a.grpcontno='"+mGrpContNo+"' ";

    	
    	 map = new MMap();
         map.put(mGrpContSql, "UPDATE");
         map.put(mGrpPolSql, "UPDATE");
         map.put(mContSql, "UPDATE");
         map.put(mPolSql, "UPDATE");
         map.put(mCCUWMasterSql, "UPDATE");
         map.put(mCUWMasterSql, "UPDATE");
         map.put(mGCUWMasterSql, "UPDATE");
         map.put(mGUWMasterSql, "UPDATE");
         map.put(mCCUWSubSql, "INSERT");
         map.put(mCUWSubSql, "INSERT");
         map.put(mGCUWSubSql, "INSERT");
         map.put(mGUWSubSql, "INSERT");
         
         map.put(gilLCPrem, "UPDATE");
         map.put(gilLCGet, "UPDATE");
         map.put(gilLCDuty, "UPDATE");
         map.put(gilLCPol, "UPDATE");
         map.put(gilLCCont, "UPDATE");
         map.put(gilLLCGrpPol, "UPDATE");
         map.put(gilLCGrpCont, "UPDATE");

        return true;
    }

    /**
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
     * @return
     */
    private boolean prepareTransferData()
    {
        mTransferData.setNameAndValue("GrpContNo", mGrpContNo);
        mTransferData.setNameAndValue("PrtNo", mLCGrpContSchema.getPrtNo());
        mTransferData.setNameAndValue("AgentCode",
                mLCGrpContSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup",
                mLCGrpContSchema.getAgentGroup());
        mTransferData.setNameAndValue("SaleChnl", mLCGrpContSchema.getSaleChnl());
        mTransferData.setNameAndValue("ManageCom",
                mLCGrpContSchema.getManageCom());
        mTransferData.setNameAndValue("GrpNo", mLCGrpContSchema.getAppntNo());
        mTransferData.setNameAndValue("GrpName", mLCGrpContSchema.getGrpName());
        mTransferData.setNameAndValue("CValiDate",
                mLCGrpContSchema.getCValiDate());

        return true;
    }

    /**
     * 返回处理后的结果
     * @return VData
     */
    public VData getResult()
    {
        return mResult;
    }

    /**
     * 返回工作流中的Lwfieldmap所描述的值
     * @return TransferData
     */
    public TransferData getReturnTransferData()
    {
        return mTransferData;
    }

    /**
     * 返回错误对象
     * @return CErrors
     */
    public CErrors getErrors()
    {
        return mErrors;
    }
}

/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.circ;

import java.util.LinkedList;

import com.sinosoft.lis.db.LFComISCDB;
import com.sinosoft.lis.db.LFDesbModeDB;
import com.sinosoft.lis.db.LFItemRelaDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.schema.LFComISCSchema;
import com.sinosoft.lis.schema.LFDesbModeSchema;
import com.sinosoft.lis.schema.LFItemRelaSchema;
import com.sinosoft.lis.vschema.LFComISCSet;
import com.sinosoft.lis.vschema.LFDesbModeSet;
import com.sinosoft.lis.vschema.LFItemRelaSet;
import com.sinosoft.msreport.ColDataBLS;
import com.sinosoft.msreport.ReportEngineUI;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: </p>
 * <p>Description:工作流节点任务:保全人工核保发送核保通知书服务类 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class XmlReportCalAfterInitService implements AfterInitService
{

    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往后面传输数据的容器 */
//    private VData mInputData;
    /** 往界面传输数据的容器 */
    private VData mResult = new VData();
    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();
    //private VData mIputData = new VData();
    private TransferData mTransferData = new TransferData();
    /** 数据操作字符串 */
    private String mOperater;
    private String mManageCom;
//    private String mOperate;
    /** 业务数据操作字符串 */
    private String mStatYear;
    private String mStatMon;
    private String mRepType;
    private String mMissionID;
//    private String mItemType;
    private LinkedList mLinkedList = new LinkedList();

//    private Reflections mReflections = new Reflections();

    /**执行保全工作流特约活动表任务0000000003*/
    public XmlReportCalAfterInitService()
    {
    }

    /**
     * 传输数据的公共方法
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
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

        //进行业务处理
        if (!dealData())
        {
            return false;
        }

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

        //mResult.clear();
        return true;
    }

    /**
     * 准备返回前台统一存储数据
     * 输出：如果发生错误则返回false,否则返回true
     * @return boolean
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();

        //添加核保通知书打印管理表数据
//	  map.put(mDeleteXMLSQL, "DELETE");
//        map.put(mDeleteMidSQL, "DELETE");
        mResult.add(map);
        return true;
    }

    /**
     * 校验业务数据
     * @return boolean
     */
    private boolean checkData()
    {

        return true;
    }

    /**
     * 从输入数据中得到所有对象
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    private boolean getInputData(VData cInputData, String cOperate)
    {
        //从输入数据中得到所有对象
        //获得全局公共数据
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);
//        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

//        mOperate = cOperate;

        //获得业务数据
        if (mTransferData == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mStatYear = (String) mTransferData.getValueByName("StatYear");
        if (mStatYear == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中StatYear失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mStatMon = (String) mTransferData.getValueByName("StatMon");
        if (mStatMon == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中StatMon失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mRepType = (String) mTransferData.getValueByName("RepType");
        if (mRepType == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中RepType失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //获得当前工作任务的任务ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     * @return boolean
     */
    private boolean dealData()
    {

//按照科目进行汇总处理(合计科目除外)
        LFItemRelaDB tLFItemRelaDB = new LFItemRelaDB();
//    String tSQL = "select * from LFItemRela where IsLeaf='0' order by ItemLevel desc";
        String tSQL = "select a.* from LFItemRela a where a.IsLeaf='0' and isCalFlag='0' order by a.ItemLevel desc";
//String tSQL = "select a.* from LFItemRela a where a.IsLeaf='0' and ItemCode='1851' and exists(select UpItemCode from LFXMLColl where UpItemCode=a.ItemCode and RepType='"+this.RepType+"' and StatYear="+this.StatYear+" and StatMon="+this.StatMon+" ) order by a.ItemLevel desc";
        System.out.println(tSQL);
        LFItemRelaSet tLFItemRelaSet = tLFItemRelaDB.executeQuery(tSQL);
        if (tLFItemRelaDB.mErrors.needDealError())
        {
            CError tError = new CError();
            tError.moduleName = "ColDataBL";
            tError.functionName = "dealData";
            tError.errorMessage = "查询内外科目编码对应表出错！";
            this.mErrors.addOneError(tError);
            return false;
        }
        int tSize = tLFItemRelaSet.size();
        for (int i = 1; i <= tSize; i++)
        {
            LFItemRelaSchema tLFItemRelaSchema = tLFItemRelaSet.get(i);
            if (tLFItemRelaSchema.getRemark() == null ||
                tLFItemRelaSchema.getRemark().trim().equals("null"))
            {
                tLFItemRelaSchema.setRemark("");
            }
//      System.out.println(tLFItemRelaSchema.getRemark());
            String aSQL = "insert into LFXMLColl select ComCodeISC,'" +
                          tLFItemRelaSchema.getItemCode() + "','" +
                          this.mRepType + "'," + this.mStatYear + "," +
                          this.mStatMon + ",'" +
                          tLFItemRelaSchema.getUpItemCode() +
                          "',MAX(ParentComCodeISC)," +
                          tLFItemRelaSchema.getLayer() + ",sum(StatValue),'" +
                          tLFItemRelaSchema.getRemark() +
                          "' from LFXMLColl where RepType='" + this.mRepType +
                          "' and StatYear=" + this.mStatYear + " and StatMon=" +
                          this.mStatMon + " and Layer=1 and UpItemCode='" +
                          tLFItemRelaSchema.getItemCode() +
                          "' group by ComCodeISC";
            System.out.println(aSQL);
            mLinkedList.add(aSQL);
            ColDataBLS tColDataBLS = new ColDataBLS();
//调用后台BLS程序对数据库进行操作
            if (!tColDataBLS.submitData(mLinkedList))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tColDataBLS.mErrors);
                CError tError = new CError();
                tError.moduleName = "ColDataBL";
                tError.functionName = "dealData";
                tError.errorMessage = "数据提交失败!";
                this.mErrors.addOneError(tError);
                return false;
            }
            mLinkedList.clear();
        }

//计算科目的汇总处理(即合计科目)
        LFDesbModeDB tLFDesbModeDB = new LFDesbModeDB();
        String tempSQL = "select * from LFDesbMode  where ItemType in('C1','C2','C3','C4','C5','C6','C7') and DealType='S' order by ItemNum ";
        System.out.println(tempSQL);
        LFDesbModeSet tLFDesbModeSet = tLFDesbModeDB.executeQuery(tempSQL);
        if (tLFDesbModeSet.mErrors.needDealError())
        {
            CError tError = new CError();
            tError.moduleName = "ColDataBL";
            tError.functionName = "dealData";
            tError.errorMessage = "查询LFDesbMode(合计科目)出错！";
            this.mErrors.addOneError(tError);
            return false;
        }
        int tempSize = tLFDesbModeSet.size();
        System.out.println("size:" + tempSize);

        for (int j = 1; j <= tempSize; j++)
        {
            LFDesbModeSchema tLFDesbModeSchema = new LFDesbModeSchema();
            tLFDesbModeSchema = tLFDesbModeSet.get(j);
            System.out.println("itemcode:" + tLFDesbModeSchema.getItemCode());
            VData tVData = new VData();
            GlobalInput mGlobalInput = new GlobalInput();
            TransferData mTransferData = new TransferData();

            /** 传递变量 */
            //计算汇总
            //mTransferData.setNameAndValue("ReportDate", "2004-06-10");
            //mTransferData.setNameAndValue("MakeDate", "2003-03-01");

            mTransferData.setNameAndValue("reptype", mRepType);
            mTransferData.setNameAndValue("StatYear", mStatYear);
            mTransferData.setNameAndValue("StatMon", mStatMon);

            tVData.add(mGlobalInput);
            tVData.add("1");
            tVData.add(mTransferData);

            try
            {
                ReportEngineUI tReportEngineUI = new ReportEngineUI();
                System.out.println("sql:" + tLFDesbModeSchema.getItemCode() +
                                   "||" + "" +
                                   "|| AND Dealtype='S' AND ItemType in('C1','C2','C3','C4','C5','C6','C7')");

                if (!tReportEngineUI.submitData(tVData,
                                                tLFDesbModeSchema.getItemCode() +
                                                "||" + "" +
                                                "|| AND Dealtype='S' AND ItemType in('C1','C2','C3','C4','C5','C6','C7')"))
                {
                    if (tReportEngineUI.mErrors.needDealError())
                    {
                        System.out.println(tReportEngineUI.mErrors.
                                           getFirstError());
                    }
                    else
                    {
                        System.out.println("保存失败，但是没有详细的原因");
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

//按照机构进行汇总处理
        LFComISCDB tLFComISCDB = new LFComISCDB();
//    String cSQL = "select * from LFComISC where IsLeaf='0' order by ComLevel desc";
        String cSQL =
                "select a.* from LFComISC a where a.IsLeaf='0' order by a.ComLevel desc";
        System.out.println(cSQL);
        LFComISCSet tLFComISCSet = tLFComISCDB.executeQuery(cSQL);
        if (tLFComISCDB.mErrors.needDealError())
        {
//      this.mErrors.copyAllErrors(tLFComISCDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "ColDataBL";
            tError.functionName = "dealData";
            tError.errorMessage = "查询保监会规定机构信息表出错！";
            this.mErrors.addOneError(tError);
            return false;
        }
        int aSize = tLFComISCSet.size();
        for (int i = 1; i <= aSize; i++)
        {
            LFComISCSchema tLFComISCSchema = tLFComISCSet.get(i);
            //增加别名，否则程序有可能因为数据问题报错，2004-6-20 yt
            String dSQL = "insert into LFXMLColl select c.c1,c.itemcode,c.c3,c.c4,c.c5,c.c6,c.c7,c.c8,c.c9,b.Remark from (select '" +
                          tLFComISCSchema.getComCodeISC() +
                          "' c1,a.ItemCode itemcode,'" + this.mRepType +
                          "' c3," + this.mStatYear + " c4," + this.mStatMon +
                          " c5,max(a.UpItemCode) c6,'" +
                          tLFComISCSchema.getParentComCodeISC() +
                          "' c7,max(a.Layer) c8,sum(a.StatValue) c9 from LFXMLColl a where a.RepType='" +
                          this.mRepType + "' and a.StatYear=" + this.mStatYear +
                          " and a.StatMon=" + this.mStatMon +
                          " and a.ParentComCodeISC='" +
                          tLFComISCSchema.getComCodeISC() + "' group by a.ItemCode) c,LFItemRela b where c.ItemCode=b.ItemCode and b.General='1'";
            System.out.println(dSQL);
            mLinkedList.add(dSQL);
            //调用后台BLS程序对数据库进行操作
            ColDataBLS tColDataBLS = new ColDataBLS();
            if (!tColDataBLS.submitData(mLinkedList))
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tColDataBLS.mErrors);
                CError tError = new CError();
                tError.moduleName = "ColDataBL";
                tError.functionName = "dealData";
                tError.errorMessage = "数据提交失败!";
                this.mErrors.addOneError(tError);
                return false;
            }
            mLinkedList.clear();
        }
        //计算科目的汇总处理(即第二类合计科目自身不需要总分汇总，但构成科目中有需要作总分汇总，所以在构成科目均已计算结束后再作计算如科目：itemcode=2544)
        tLFDesbModeDB = new LFDesbModeDB();
        tempSQL = "select * from LFDesbMode  where ItemType in('D1','D2','D3','D4','D5','D6','D7') and DealType='S' order by ItemNum ";
        System.out.println(tempSQL);
        tLFDesbModeSet = tLFDesbModeDB.executeQuery(tempSQL);
        if (tLFDesbModeSet.mErrors.needDealError())
        {
            CError tError = new CError();
            tError.moduleName = "ColDataBL";
            tError.functionName = "dealData";
            tError.errorMessage = "查询LFDesbMode(第二类合计科目)出错！";
            this.mErrors.addOneError(tError);
            return false;
        }
        tempSize = tLFDesbModeSet.size();
        System.out.println("size:" + tempSize);

        for (int j = 1; j <= tempSize; j++)
        {
            LFDesbModeSchema tLFDesbModeSchema = new LFDesbModeSchema();
            tLFDesbModeSchema = tLFDesbModeSet.get(j);
            System.out.println("itemcode:" + tLFDesbModeSchema.getItemCode());
            VData tVData = new VData();
            GlobalInput mGlobalInput = new GlobalInput();
            TransferData mTransferData = new TransferData();

            /** 传递变量 */
            //计算汇总
            //mTransferData.setNameAndValue("ReportDate", "2004-06-10");
            //mTransferData.setNameAndValue("MakeDate", "2003-03-01");

            mTransferData.setNameAndValue("reptype", mRepType);
            mTransferData.setNameAndValue("StatYear", mStatYear);
            mTransferData.setNameAndValue("StatMon", mStatMon);

            tVData.add(mGlobalInput);
            tVData.add("1");
            tVData.add(mTransferData);

            try
            {
                ReportEngineUI tReportEngineUI = new ReportEngineUI();
                System.out.println("sql:" + tLFDesbModeSchema.getItemCode() +
                                   "||" + "" +
                                   "|| AND Dealtype='S' AND ItemType in('D1','D2','D3','D4','D5','D6','D7')");

                if (!tReportEngineUI.submitData(tVData,
                                                tLFDesbModeSchema.getItemCode() +
                                                "||" + "" +
                                                "|| AND Dealtype='S' AND ItemType in('D1','D2','D3','D4','D5','D6','D7')"))
                {
                    if (tReportEngineUI.mErrors.needDealError())
                    {
                        System.out.println(tReportEngineUI.mErrors.
                                           getFirstError());
                    }
                    else
                    {
                        System.out.println("保存失败，但是没有详细的原因");
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        //补应该报但无业务数据的科目
        String tAndSql = "";
        if (this.mRepType == null || this.mRepType.length() == 0)
        {
            CError tError = new CError();
            tError.moduleName = "ColDataBL";
            tError.functionName = "dealData";
            tError.errorMessage = "报表类型不能为空!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (this.mRepType.equals("1"))
        {
            tAndSql = " and IsQuick='1' ";
        }
        else if (this.mRepType.equals("2"))
        {
            tAndSql = " and IsMon='1' ";
        }
        else if (this.mRepType.equals("3"))
        {
            tAndSql = " and IsQut='1' ";
        }
        else if (this.mRepType.equals("4"))
        {
            tAndSql = " and IsHalYer='1' ";
        }
        else if (this.mRepType.equals("5"))
        {
            tAndSql = " and IsYear='1' ";
        }
        else
        {
            CError tError = new CError();
            tError.moduleName = "ColDataBL";
            tError.functionName = "dealData";
            tError.errorMessage = "报表类型错误";
            this.mErrors.addOneError(tError);
            return false;
        }
        String tSql = "insert into LFXMLColl ";
        tSql = tSql + "select  b.comcodeisc,a.itemcode,'" + this.mRepType +
               "'," + this.mStatYear + "," + this.mStatMon +
               ",a.upitemcode,b.ParentComCodeISC,a.layer,0,a.remark";
        tSql = tSql + " from lfitemrela a,lfcomisc b where 1=1 and b.OutputFlag ='1' and a.outputflag='1' ";
        tSql = tSql + tAndSql;
        tSql = tSql + " and not exists (select 'X' from lfxmlcoll where comcodeisc=b.comcodeisc and itemcode = a.itemcode and statyear=" +
               this.mStatYear + " and statmon=" + this.mStatMon +
               " and reptype='" + this.mRepType + "') ";
        tSql = tSql + " and b.ComLevel<=a.ComFlag ";
        System.out.println(tSql);

        mLinkedList.add(tSql);
        //调用后台BLS程序对数据库进行操作
        ColDataBLS tColDataBLS = new ColDataBLS();
        if (!tColDataBLS.submitData(mLinkedList))
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tColDataBLS.mErrors);
            CError tError = new CError();
            tError.moduleName = "ColDataBL";
            tError.functionName = "dealData";
            tError.errorMessage = "数据提交失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLinkedList.clear();
        return true;
    }


    /**
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
     * @return boolean
     */
    private boolean prepareTransferData()
    {
        //为工作流中回收体检通知书节点准备属性数据
//	  mTransferData.setNameAndValue("CertifyCode",mLZSysCertifySchema.getCertifyCode());
//	  mTransferData.setNameAndValue("ValidDate",mLZSysCertifySchema.getValidDate()) ;

        return true;
    }


    public VData getResult()
    {
        mResult = new VData(); //不保证事务一致性
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

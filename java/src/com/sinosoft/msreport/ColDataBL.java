/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import java.util.LinkedList;

import com.sinosoft.lis.db.LFComISCDB;
import com.sinosoft.lis.db.LFDesbModeDB;
import com.sinosoft.lis.db.LFItemRelaDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.schema.LFComISCSchema;
import com.sinosoft.lis.schema.LFDesbModeSchema;
import com.sinosoft.lis.schema.LFItemRelaSchema;
import com.sinosoft.lis.vschema.LFComISCSet;
import com.sinosoft.lis.vschema.LFDesbModeSet;
import com.sinosoft.lis.vschema.LFItemRelaSet;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;

/**
 * <p>Title: lis</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: sinosoft</p>
 * @author lh
 * @version 1.0
 */

public class ColDataBL
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往后面传输数据的容器 */
    private VData mInputData;
    private LinkedList mLinkedList = new LinkedList();
    /** 统计年 */
    private int StatYear;
    /** 统计月 */
    private int StatMon;
    /** 报表类型 */
    private String RepType;

    public ColDataBL()
    {
    }

    /**
     * 传输数据的公共方法
     * @param: cInputData 输入的数据
     *         cOperate 数据操作
     * @return:
     */
    public boolean submitData(VData cInputData)
    {
        //得到外部传入的数据,将数据备份到本类中
        if (!getInputData(cInputData))
        {
            return false;
        }
        //进行业务处理
        if (!dealData())
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "ColDataBL";
            tError.functionName = "submitData";
            tError.errorMessage = "数据处理失败";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * 从输入数据中得到所有对象
     *输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     */
    private boolean getInputData(VData cInputData)
    {
        StatYear = Integer.parseInt(((String) cInputData.get(0)));
        StatMon = Integer.parseInt(((String) cInputData.get(1)));
//    StatYear =((Integer)cInputData.get(0)).intValue();
//    StatMon =((Integer)cInputData.get(1)).intValue();
        RepType = (String) cInputData.get(2);
        return true;
    }

    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     */
    private boolean dealData()
    {
        //补充XML汇总表所缺字段
//    ReinDataBLS tReinDataBLS = new ReinDataBLS();
//调用后台BLS程序对数据库进行操作
//    if(!tReinDataBLS.submitData())
//    {
//      // @@错误处理
//      this.mErrors.copyAllErrors(tReinDataBLS.mErrors);
//      CError tError = new CError();
//      tError.moduleName = "ColDataBL";
//      tError.functionName = "dealData";
//      tError.errorMessage ="数据提交失败!";
//      this.mErrors .addOneError(tError) ;
//      return false;
//    }

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
            String aSQL = "insert into LFXMLColl select ComCodeISC," +
                          tLFItemRelaSchema.getItemCode() + "," + this.RepType +
                          "," + this.StatYear + "," + this.StatMon + "," +
                          tLFItemRelaSchema.getUpItemCode() +
                          ",MAX(ParentComCodeISC)," +
                          tLFItemRelaSchema.getLayer() + ",sum(StatValue),'" +
                          tLFItemRelaSchema.getRemark() +
                          "' from LFXMLColl where RepType='" + this.RepType +
                          "' and StatYear=" + this.StatYear + " and StatMon=" +
                          this.StatMon + " and Layer='1' and UpItemCode='" +
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

            mTransferData.setNameAndValue("reptype", RepType);
            mTransferData.setNameAndValue("StatYear", StatYear);
            mTransferData.setNameAndValue("StatMon", StatMon);

            tVData.add(mGlobalInput);
            tVData.add("1");
            tVData.add(mTransferData);

            try
            {
                ReportEngineUI tReportEngineUI = new ReportEngineUI();
                System.out.println("sql:" + tLFDesbModeSchema.getItemCode() +
                                   "||" + "" +
                                   "|| AND Dealtype='S' AND ItemType in('C1','C2','C3','C4','C5','C6','C7') ");

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
                          "' c1,a.ItemCode itemcode,'" + this.RepType + "' c3," +
                          this.StatYear + " c4," + this.StatMon +
                          " c5,max(a.UpItemCode) c6,'" +
                          tLFComISCSchema.getParentComCodeISC() +
                          "' c7,max(a.Layer) c8,sum(a.StatValue) c9 from LFXMLColl a where a.RepType='" +
                          this.RepType + "' and a.StatYear=" + this.StatYear +
                          " and a.StatMon=" + this.StatMon +
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

            mTransferData.setNameAndValue("reptype", RepType);
            mTransferData.setNameAndValue("StatYear", StatYear);
            mTransferData.setNameAndValue("StatMon", StatMon);

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
        if (this.RepType == null || this.RepType.length() == 0)
        {
            CError tError = new CError();
            tError.moduleName = "ColDataBL";
            tError.functionName = "dealData";
            tError.errorMessage = "报表类型不能为空!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (this.RepType.equals("1"))
        {
            tAndSql = " and IsQuick='1' ";
        }
        else if (this.RepType.equals("2"))
        {
            tAndSql = " and IsMon='1' ";
        }
        else if (this.RepType.equals("3"))
        {
            tAndSql = " and IsQut='1' ";
        }
        else if (this.RepType.equals("4"))
        {
            tAndSql = " and IsHalYer='1' ";
        }
        else if (this.RepType.equals("5"))
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
        tSql = tSql + "select  b.comcodeisc,a.itemcode,'" + this.RepType + "'," +
               this.StatYear + "," + this.StatMon +
               ",a.upitemcode,b.ParentComCodeISC,a.layer,0,a.remark";
        tSql = tSql + " from lfitemrela a,lfcomisc b where 1=1 and b.OutputFlag ='1' and a.outputflag='1' ";
        tSql = tSql + tAndSql;
        tSql = tSql + " and not exists (select 'X' from lfxmlcoll where comcodeisc=b.comcodeisc and itemcode = a.itemcode and statyear=" +
               this.StatYear + " and statmon=" + this.StatMon +
               " and reptype='" + this.RepType + "') ";
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

    public static void main(String[] args)
    {
        VData tVData = new VData();
        String StatYear = "2004";
        String StatMon = "7";
        String RepType = "2";
        tVData.add(StatYear);
        tVData.add(StatMon);
        tVData.add(RepType);
        ColDataBL ColDataBL1 = new ColDataBL();
        ColDataBL1.submitData(tVData);
    }
}

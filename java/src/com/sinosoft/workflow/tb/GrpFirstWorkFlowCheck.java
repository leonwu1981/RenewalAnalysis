/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.LCGrpContSchema;
import com.sinosoft.lis.vschema.LCContSet;
import com.sinosoft.lis.vschema.LCGrpPolSet;
import com.sinosoft.lis.vschema.LCPolSet;
import com.sinosoft.utility.*;


/**
 * <p>Title: 团体新契约工作流起始结点校验类 </p>
 * <p>Description: 校验是否满足录入完成条件，并记录录入人、录入完成时间</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class GrpFirstWorkFlowCheck
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();


    /** 往界面传输数据的容器 */
    private VData mResult = new VData();


    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();

    /** 业务处理类 */
    private LCGrpContSchema mLCGrpContSchema = new LCGrpContSchema();
//    private LCContSchema mLCContSchema = new LCContSchema();
//    private LCGrpPolSchema mLCGrpPolSchema = new LCGrpPolSchema();
//    private LCPolSchema mLCPolSchema = new LCPolSchema();
//    private ES_DOC_MAINSchema mES_DOC_MAINSchema = new ES_DOC_MAINSchema();

    /** 数据操作字符串 */
    private String mOperater;
    private String mManageCom;


    /** 业务数据字符串 */
    private String mGrpContNo;
    private String mContSql;
//    private String mES_DOC_MAINSql = "";
    private String mGrpContSql;
    private String[] mGrpPolSql;
    public GrpFirstWorkFlowCheck()
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

        //准备往后台的数据
        if (!prepareOutputData())
        {
            return false;
        }

        System.out.println("Start  Submit...");

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
            tError.moduleName = "GrpFirstWorkFlowCheck";
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
            tError.moduleName = "GrpFirstWorkFlowCheck";
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
            tError.moduleName = "GrpFirstWorkFlowCheck";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的GrpContNo
        mGrpContNo = (String) mTransferData.getValueByName("ProposalGrpContNo");
        if (mGrpContNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCGrpContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "GrpFirstWorkFlowCheck";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中GrpContNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }


    /**
     * 校验业务数据:校验时否达到录入完毕条件
     * @return
     */
    private boolean checkData()
    {
        LCGrpContDB tLCGrpContDB = new LCGrpContDB();
        LCGrpPolDB tLCGrpPolDB = new LCGrpPolDB();
        LCContDB tLCContDB = new LCContDB();
        LCPolDB tLCPolDB = new LCPolDB();

        tLCGrpContDB.setGrpContNo(mGrpContNo);
        if (!tLCGrpContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "GrpFirstWorkFlowCheck";
            tError.functionName = "checkData";
            tError.errorMessage = "集体合同保单" + mGrpContNo + "信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCGrpContSchema.setSchema(tLCGrpContDB);

        tLCGrpPolDB.setGrpContNo(mGrpContNo);
        LCGrpPolSet tLCGrpPolSet = tLCGrpPolDB.query();
        if (tLCGrpPolSet.size() == 0)
        {
            CError tError = new CError();
            tError.moduleName = "GrpFirstWorkFlowCheck";
            tError.functionName = "checkData";
            tError.errorMessage = "未录入集体保单信息！";
            this.mErrors.addOneError(tError);
            return false;
        }
        //校验此险种是否已经录入被保人
        for (int i = 1; i <= tLCGrpPolSet.size(); i++)
        {
            if (tLCGrpPolSet.get(i).getPeoples2() == 0)
            {
                CError tError = new CError();
                tError.moduleName = "GrpFirstWorkFlowCheck";
                tError.functionName = "checkData";
                tError.errorMessage = "险种" + tLCGrpPolSet.get(i).getRiskCode()
                        + "下未录入被保人，请删除此险种或录入被保险人！";
                this.mErrors.addOneError(tError);
                return false;
            }
        }

        tLCContDB.setGrpContNo(mGrpContNo);
        LCContSet tLCContSet = tLCContDB.query();
        if (tLCContSet.size() == 0)
        {
            CError tError = new CError();
            tError.moduleName = "GrpFirstWorkFlowCheck";
            tError.functionName = "checkData";
            tError.errorMessage = "团单下个单合同信息查询失败，错误原因可能是：未录入个单合同信息！";
            this.mErrors.addOneError(tError);
            return false;
        }
        for (int i = 1; i <= tLCContSet.size(); i++)
        {
            tLCPolDB.setContNo(tLCContSet.get(i).getContNo());
            LCPolSet tLCPolSet = tLCPolDB.query();
            if (tLCPolSet.size() == 0)
            {
                CError tError = new CError();
                tError.moduleName = "GrpFirstWorkFlowCheck";
                tError.functionName = "checkData";
                tError.errorMessage = "团单下个单险种信息查询失败，错误原因可能是：未录入个单" + tLCContSet.get(i).getContNo()
                        + "险种信息！";
                this.mErrors.addOneError(tError);
                return false;
            }
        }

        return true;
    }


    /**
     * 准备返回前台统一存储数据
     * 输出：如果发生错误则返回false,否则返回true
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();

        map.put(mLCGrpContSchema, "UPDATE");
        map.put(mContSql, "UPDATE");
//        map.put(mES_DOC_MAINSql,"UPDATE");
        map.put(mGrpContSql, "UPDATE");
        for (int i = 0; i < mGrpPolSql.length; i++)
        {
            map.put(mGrpPolSql[i], "UPDATE");
        }

        mResult.add(map);
        return true;
    }


    /**
     * sumData
     * 进行保费、保额的汇总
     * @return boolean
     */
    private boolean sumData()
    {
        LCGrpPolDB tLCGrpPolDB = new LCGrpPolDB();
        LCGrpPolSet tLCGrpPolSet = new LCGrpPolSet();
        tLCGrpPolDB.setGrpContNo(mGrpContNo);
        tLCGrpPolSet = tLCGrpPolDB.query();
        mGrpPolSql = new String[tLCGrpPolSet.size()];

        StringBuffer tSBql = null;
        String tGrpPolNo = null;
        for (int i = 1; i <= tLCGrpPolSet.size(); i++)
        {
            tGrpPolNo = tLCGrpPolSet.get(i).getGrpPolNo();
//            mGrpPolSql[i - 1] =
//                    "update lcgrppol set prem = (select sum(prem) from lcpol where grppolno = '"
//                    + tGrpPolNo + "'),amnt = (select sum(amnt) from lcpol where grppolno = '"
//                    + tGrpPolNo
//                    + "'),peoples2 = (select sum(InsuredPeoples) from lcpol where grppolno = '"
//                    + tGrpPolNo + "') where grppolno = '" + tGrpPolNo + "'";
            tSBql = new StringBuffer(128);
            tSBql.append(
                    "update lcgrppol set prem = (select sum(prem) from lcpol where grppolno = '");
            tSBql.append(tGrpPolNo);
            tSBql.append("'),amnt = (select sum(amnt) from lcpol where grppolno = '");
            tSBql.append(tGrpPolNo);
            tSBql.append("'),peoples2 = (select sum(InsuredPeoples) from lcpol where grppolno = '");
            tSBql.append(tGrpPolNo);
            tSBql.append("') where grppolno = '");
            tSBql.append(tGrpPolNo);
            tSBql.append("'");
            mGrpPolSql[i - 1] = tSBql.toString();
        }

//        mGrpContSql =
//                "update lcgrpcont set prem = (select sum(prem) from lccont where grpcontno = '"
//                + mGrpContNo + "'),amnt=(select sum(amnt) from lccont where grpcontno = '"
//                + mGrpContNo + "'),Peoples = (select sum(Peoples) from lccont where grpcontno = '"
//                + mGrpContNo + "') where grpcontno = '" + mGrpContNo + "'";
        tSBql = new StringBuffer(128);
        tSBql.append(
                "update lcgrpcont set prem = (select sum(prem) from lccont where grpcontno = '");
        tSBql.append(mGrpContNo);
        tSBql.append("'),amnt=(select sum(amnt) from lccont where grpcontno = '");
        tSBql.append(mGrpContNo);
       // tSBql.append("'),Peoples = (select sum(Peoples) from lccont where grpcontno = '");
       // tSBql.append(mGrpContNo);
      //  tSBql.append("') where grpcontno = '");
        tSBql.append("') where grpcontno='");
        tSBql.append(mGrpContNo);
        tSBql.append("'");
        mGrpContSql = tSBql.toString();

        return true;
    }

    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     */
    private boolean dealData()
    {
        //为团体合同表存入录入人、录入时间
        mLCGrpContSchema.setInputOperator(mOperater);
        mLCGrpContSchema.setInputDate(PubFun.getCurrentDate());
        mLCGrpContSchema.setInputTime(PubFun.getCurrentTime());

        //为合同表存入录入人、录入时间
//        mContSql = "update lccont set InputOperator ='" + mOperater + "',InputDate = '"
//                + PubFun.getCurrentDate() + "',InputTime = '" + PubFun.getCurrentTime()
//                + "' where grpcontno = '" + mGrpContNo + "'";
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("update lccont set InputOperator ='");
        tSBql.append(mOperater);
        tSBql.append("',InputDate = '");
        tSBql.append(PubFun.getCurrentDate());
        tSBql.append("',InputTime = '");
        tSBql.append(PubFun.getCurrentTime());
        tSBql.append("' where grpcontno = '");
        tSBql.append(mGrpContNo);
        tSBql.append("'");
        mContSql = tSBql.toString();

        //更新扫描件状态
//        mES_DOC_MAINSql = "update ES_DOC_MAIN set InputState = '1',"
//                          +"InputStartDate ='" + PubFun.getCurrentDate() + "',"
//                          +"InputStartTime = '" + PubFun.getCurrentTime() + "',"
//                          +"scanoperator = '" +mOperater+"'"
//                          +" where doccode = '" + mLCGrpContSchema.getPrtNo() + "'"
//                          ;
        //重新对个人合同，集体险种，集体合同进行重新统计合计人数，合计保费，合计保额
        if (!sumData())
        {
            return false;
        }

        return true;
    }

    /**
     *  返回结果集
     *  @return mResult
     */
    public VData getResult()
    {
        return mResult;
    }

    /**
     * 返回错误处理对象
     * @return CErrors
     */
    public CErrors getErrors()
    {
        return mErrors;
    }
}

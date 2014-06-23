/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.bq;

import java.text.DecimalFormat;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.ActivityOperator;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
*  @author lanjun
 * @version 1.0
 */

public class PEdorUWAddAfterInitService implements AfterInitService
{

    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往后面传输数据的容器 */
    private VData mInputData;
    /** 往界面传输数据的容器 */
    private VData mResult = new VData();
    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();
    //private VData mIputData = new VData();
    private TransferData mTransferData = new TransferData();
    /**工作流引擎 */
    ActivityOperator mActivityOperator = new ActivityOperator();
    /** 数据操作字符串 */
    private String mOperater;
    private String mManageCom;
    private String mOperate;
    /** 业务数据操作字符串 */
    private String mEdorNo;
    private String mEdorType;
     private String mContNo;
    private String mPolNo;
    private String mPolNo2;
//    private String mInsuredNo;
    private String mAddReason;
    private Reflections mReflections = new Reflections();

    /**执行保全工作流加费活动表任务0000000002*/
    /**保单表*/
    private LCPolSchema mLCPolSchema = new LCPolSchema();
    private LPPolSchema mLPPolSchema = new LPPolSchema();

    /** 保全核保主表 */
    private LPUWMasterSchema mLPUWMasterSchema = new LPUWMasterSchema();
    /** 责任项表 */
    private LPDutySet mLPDutySet = new LPDutySet();
    private LCDutySet mLCDutySet = new LCDutySet();

    private LPDutySet mNewLPDutySet = new LPDutySet();
      private LCDutySet mNewLCDutySet = new LCDutySet();
    /** 保费表 */
    private LPPremSet mLPPremSet = new LPPremSet();
    private LPPremSet mOldLPPremSet = new LPPremSet();

    private LCPremSet mLCPremSet = new LCPremSet();
    private LCPremSet mOldLCPremSet = new LCPremSet();

//    private LPPremSet mNewLPPremSet = new LPPremSet();
    /**保全批改补退费表*/
    private LJSGetEndorseSet mOldLJSGetEndorseSet = new LJSGetEndorseSet();
    private LJSGetEndorseSet mNewLJSGetEndorseSet = new LJSGetEndorseSet();
    /**保全批改表*/
    private LPEdorMainSchema mLPEdorMainSchema = new LPEdorMainSchema();


    public PEdorUWAddAfterInitService()
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

        //校验是否有未打印的加费通知书
        if (!checkData())
        {
            return false;
        }

        //进行业务处理
        if (!dealData())
        {
            return false;
        }

        //准备往后台的数据
        if (!prepareOutputData())
        {
            return false;
        }

        System.out.println("Start SysUWNoticeBL Submit...");

        //mResult.clear();
        return true;
    }


    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     */
    private boolean dealData()
    {
        // 核保特约信息
        if (prepareAdd())
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * 校验业务数据
     * @return
     */
    private boolean checkData()
    {
        //校验保单信息
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setPolNo(mPolNo2);
        if (!tLCPolDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mPolNo + "信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCPolSchema.setSchema(tLCPolDB);

        //校验保单信息
        LPPolDB tLPPolDB = new LPPolDB();
        tLPPolDB.setPolNo(mPolNo2);
        tLPPolDB.setEdorNo(mEdorNo);
        tLPPolDB.setEdorType(mEdorType);
        if (!tLPPolDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mPolNo2 + "的P表信息查询失败!";
            this.mErrors.addOneError(tError);
            //return false;
        }
        mLPPolSchema.setSchema(tLPPolDB);

        //校验保全批单核保主表
        //校验保单信息
        LPUWMasterDB tLPUWMasterDB = new LPUWMasterDB();
        tLPUWMasterDB.setPolNo(mPolNo2);
        tLPUWMasterDB.setEdorNo(mEdorNo);
        tLPUWMasterDB.setEdorType(mEdorType);

        if (!tLPUWMasterDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mPolNo + "保全批单核保主表信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLPUWMasterSchema.setSchema(tLPUWMasterDB);

        // 处于未打印状态的核保通知书在打印队列中只能有一个
        // 条件：同一个单据类型，同一个其它号码，同一个其它号码类型
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

        tLOPRTManagerDB.setCode(PrintManagerBL.CODE_PEdorUW); //核保通知书
        tLOPRTManagerDB.setOtherNo(mPolNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_INDPOL); //保单号
        tLOPRTManagerDB.setStandbyFlag2(mEdorNo);
        tLOPRTManagerDB.setStateFlag("0");

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "查询打印管理表信息出错!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLOPRTManagerSet.size() != 0)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "在打印队列中已有一个处于未打印状态的核保通知书!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (mLPPremSet != null && mLPPremSet.size() > 0)
        {
            LPEdorMainDB tLPEdorMainDB = new LPEdorMainDB();
            tLPEdorMainDB.setEdorNo(mEdorNo);
//	  tLPEdorMainDB.setPolNo(mPolNo) ;
//	  tLPEdorMainDB.setEdorType(mLPPremSet.get(1).getEdorType());
            LPEdorMainSet mLPEdorMainSet = new LPEdorMainSet();
            mLPEdorMainSet = tLPEdorMainDB.query();
            if (mLPEdorMainSet == null || mLPEdorMainSet.size() != 1)
            {
                // @@错误处理
                CError tError = new CError();
                tError.moduleName = "PEdorUWAddAfterInitService";
                tError.functionName = "preparePrint";
                tError.errorMessage = "查询保全批改主表信息出错!";
                this.mErrors.addOneError(tError);
                return false;
            }
            mLPEdorMainSchema = mLPEdorMainSet.get(1);
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
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);
        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
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
            tError.moduleName = "PEdorUWAddAfterInitService";
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
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mOperate = cOperate;

        //获得业务数据
        if (mTransferData == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mEdorNo = (String) mTransferData.getValueByName("EdorNo");
        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mEdorNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中EdorNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mEdorType = (String) mTransferData.getValueByName("EdorType");
        if (mEdorType == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中mEdorType失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mPolNo = (String) mTransferData.getValueByName("PolNo");
        if (mPolNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中PolNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mPolNo2 = (String) mTransferData.getValueByName("PolNo2");
        if (mPolNo2 == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中PolNo2失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mAddReason = (String) mTransferData.getValueByName("AddReason");
        //获得业务加费通知数据
        mLPPremSet = (LPPremSet) mTransferData.getValueByName("LPPremSet");


        return true;
    }


    /**
     * 准备特约资料信息
     * 输出：如果发生错误则返回false,否则返回true
     */
    private boolean prepareAdd()
    {

        double tTotalMoney = 0;
        double tSumStandPrem = 0;
        String tCurrentDate = PubFun.getCurrentDate();
        String tCurrentTime = PubFun.getCurrentTime();
        //形成加费信息
        if (mLPPremSet.size() > 0)
        {
            //取责任信息
            LPDutyDB tLPDutyDB = new LPDutyDB();
            tLPDutyDB.setPolNo(mLPPolSchema.getPolNo());
            tLPDutyDB.setEdorNo(mEdorNo);
            tLPDutyDB.setEdorType(mEdorType);
            mLPDutySet = tLPDutyDB.query();

            if(mLPDutySet.size()<1)
            {
              LCDutyDB tLCDutyDB = new LCDutyDB();
              tLCDutyDB.setPolNo(mLCPolSchema.getPolNo());
              //tLCDutyDB.setEdorNo(mEdorNo);
              //tLCDutyDB.setEdorType(mEdorType);
              mLCDutySet = tLCDutyDB.query();

            }

            //计算除去本次保全加费项目,承保时的基本保费项后，该保单在该保全项目下的加费项目数。
            //以便计算本次保全加费的编码起始编码值.
            //在保全确认后，本条交费记录将存入c表中
            String tsql = "select count(*) from LPPrem where  polno = '"
                    + mLCPolSchema.getPolNo().trim() + "'  and edortype = '" + mEdorType
                    + "'  and edorno = '" + mEdorNo + "' and state in ( '1','3')";
            String tReSult = new String();
            ExeSQL tExeSQL = new ExeSQL();
            tReSult = tExeSQL.getOneValue(tsql);
            if (tReSult == null || tReSult.equals(""))
            {
                return false;
            }

            int tCount = 0;
            tCount = Integer.parseInt(tReSult); //已包括了本次节点及相关同步节点

            //更新责任项
            if (mLPDutySet.size() > 0)
            {
                for (int m = 1; m <= mLPDutySet.size(); m++)
                {
                    int maxno = 0;
                    LPDutySchema tLPDutySchema = new LPDutySchema();
                    tLPDutySchema = mLPDutySet.get(m);

                    //减去该责任的原本次保全加费金额
                    String sql =
                            "select * from LPPrem where payplancode  like '000000%' and polno = '"
                            + mLCPolSchema.getPolNo().trim() + "' and dutycode = '"
                            + tLPDutySchema.getDutyCode().trim() + "' and edortype = '"
                            + tLPDutySchema.getEdorType() + "' and  state = '2'";
                    LPPremDB tLPPremDB = new LPPremDB();
                    LPPremSet tLPPremSet = new LPPremSet();

                    tLPPremSet = tLPPremDB.executeQuery(sql);

                    if (tLPPremSet.size() > 0)
                    {
                        for (int j = 1; j <= tLPPremSet.size(); j++)
                        {
                            LPPremSchema tLPPremSchema = new LPPremSchema();
                            tLPPremSchema = tLPPremSet.get(j);

                            tLPDutySchema.setPrem(tLPDutySchema.getPrem() - tLPPremSchema.getPrem());
                            mLPPolSchema.setPrem(mLPPolSchema.getPrem() - tLPPremSchema.getPrem());
                        }
                    }

                    //为投保单表和责任表加上本次的加费.同时形成加费信息
                    for (int i = 1; i <= mLPPremSet.size(); i++)
                    {
                        double tPrem;

                        if (mLPPremSet.get(i).getDutyCode().equals(tLPDutySchema.getDutyCode()))
                        {
                            maxno += 1;
                            //形成加费编码
                            String PayPlanCode = "";
                            PayPlanCode = String.valueOf(maxno + tCount);
                            for (int j = PayPlanCode.length(); j < 8; j++)
                            {
                                PayPlanCode = "0" + PayPlanCode;
                            }

                            //保单总保费
                            tPrem = mLPPolSchema.getPrem() + mLPPremSet.get(i).getPrem();
                            tSumStandPrem += mLPPremSet.get(i).getPrem();
                            mLPPremSet.get(i).setContNo(mContNo);//以下注销处表明其信息是前台传入信息
                            //mLPPremSet.get(i).setDutyCode();
                            mLPPremSet.get(i).setPayPlanCode(PayPlanCode);
			     mLPPremSet.get(i).setGrpContNo("00000000000000000000");
                            //mLPPremSet.get(i).setPayPlanType();
                            //mLPPremSet.get(i).setPayTimes();
                            mLPPremSet.get(i).setPayIntv(tLPDutySchema.getPayIntv());
//			  mLPPremSet.get(i).setMult(tLPDutySchema.getMult());

                            //计算出未满期保费加费
                            String valiDate = mLPPolSchema.getCValiDate();
                            String sqlMainPolPayToDate = "select paytoDate from lcpol where polno='"
                                +mLPPolSchema.getMainPolNo()+"'";

                            String payToDate  =  tExeSQL.getOneValue(sqlMainPolPayToDate);

                            System.out.println("valiDate"+valiDate +"payToDate"+payToDate);
                            int leftDays = PubFun.calInterval(valiDate,payToDate,"D");
                            int payIntvl  = mLPPolSchema.getPayIntv();
                            int payDays ; //期间天数

                            switch(payIntvl)
                            {
                               //月交
                              case 1:
                                payDays = 30;
                                break;
                                //季度交
                              case 3:
                                payDays = 90;
                                break;
                                //半年交
                              case 6:
                                payDays = 180;
                                break;
                                //年交
                              case 12:
                                payDays = 365;
                                break;
                                //趸交
                              case 0:
                                payDays = 365;
                                break;
                              default:
                                payDays = 365;
                            }

                            double leftPrem = mLPPremSet.get(i).getPrem() * leftDays / payDays;
                            System.out.println(leftPrem+"  "+leftDays
                                               );

                            mLPPremSet.get(i).setStandPrem(leftPrem);//在增加附加险时，未满期保费
                            mLPPremSet.get(i).setPrem(leftPrem);
                            mLPPremSet.get(i).setSumPrem("0");
                            //mLPPremSet.get(i).setRate();
                            //mLPPremSet.get(i).setPayStartDate();
                            //mLPPremSet.get(i).setPayEndDate();
                            mLPPremSet.get(i).setPaytoDate(mLPPremSet.get(i).getPayStartDate());
                            mLPPremSet.get(i).setState("2"); //0:承保时的保费项。1:承保时的加费项；2：本次保全项目保全加费项　3：前几次不通批单下的保全加费：
                            mLPPremSet.get(i).setUrgePayFlag("Y"); //加费相一定要催交，而不是去取该险种所描述的催交标志。
                            mLPPremSet.get(i).setManageCom(mLCPolSchema.getManageCom());
                            mLPPremSet.get(i).setAppntNo(mLCPolSchema.getAppntNo());
                            mLPPremSet.get(i).setAppntType("1"); //个人投保
                            mLPPremSet.get(i).setModifyDate(PubFun.getCurrentDate());
                            mLPPremSet.get(i).setModifyTime(PubFun.getCurrentTime());
                            mLPPremSet.get(i).setSuppRiskScore(mLPPremSet.get(i).getSuppRiskScore());
                             mLPPremSet.get(i).setOperator(mGlobalInput.Operator);
                            mLPPremSet.get(i).setMakeDate(PubFun.getCurrentDate());
                            mLPPremSet.get(i).setMakeTime(PubFun.getCurrentTime());

                            //更新保险责任
                            tLPDutySchema.setPrem(tLPDutySchema.getPrem()
                                    + mLPPremSet.get(i).getPrem());
                            //更新保单数据
                            mLPPolSchema.setPrem(tPrem);
                        }
                    }
                    mNewLPDutySet.add(tLPDutySchema);

                }
            }

        }

        //准备删除上一次该项目的加费的数据
        String tSQL = "select * from lpprem where polno = '" + mPolNo2 + "'"
                + " and EdorNo = '" + mEdorNo + "'"
                + " and EdorType = '" + mEdorType + "'"
                + " and substr(payplancode,1,6) = '000000'"
                + " and state = '2'"; //0:承保时的保费项。1:承保时的加费项；2：本次保全项目保全加费项　3：前几次不通批单下的保全加费：
        LPPremDB tLPPremDB = new LPPremDB();
        mOldLPPremSet = tLPPremDB.executeQuery(tSQL);
        if (mOldLPPremSet == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "prepareAdd";
            tError.errorMessage = "查询保全加费信息失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //准备删除上一次该NS项目的保全批改补退费表中保全人工核保加费的数据
        tSQL = "select * from ljsgetendorse where getnoticeno = '" + mEdorNo + "'"
                + " and endorsementno = '" + mEdorNo + "'"
                + " and feeoperationtype = '" + mLPPremSet.get(1).getEdorType() + "'"
                + " and substr(payplancode,1,6) = '000000'"
                + " and payplancode <> '00000000'"
                + " and polno = '" + mPolNo2 + "'";
        LJSGetEndorseDB tLJSGetEndorseDB = new LJSGetEndorseDB();
        mOldLJSGetEndorseSet = tLJSGetEndorseDB.executeQuery(tSQL);
        if (mOldLJSGetEndorseSet == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "prepareAdd";
            tError.errorMessage = "查询保全加费信息失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //准备添加该项目的保全批改补退费表中保全人工核保加费的数据
        if (tSumStandPrem > 0 && mLPPremSet != null && mLPPremSet.size() > 0)
        {

            for (int i = 1; i <= mLPPremSet.size(); i++)
            {
                LCDutyDB tLCDutyDB = new LCDutyDB();
                LCDutySchema tLCDutySchema = new LCDutySchema();
                tLCDutyDB.setDutyCode(mLPPremSet.get(i).getDutyCode());
                tLCDutyDB.setPolNo(mLPPremSet.get(i).getPolNo());
                LCDutySet tLCDutySet = tLCDutyDB.query();
                if (tLCDutySet == null)
                {
                    // @@错误处理
                    CError tError = new CError();
                    tError.moduleName = "PEdorUWAddAfterInitService";
                    tError.functionName = "prepareAdd";
                    tError.errorMessage = "查询责任表信息失败!";
                    this.mErrors.addOneError(tError);
                    return false;
                }
                tLCDutySchema = tLCDutySet.get(1);
                String tDutyPaytoDate = tLCDutySchema.getPaytoDate();
                String tPremPaytoDate = mLPPremSet.get(i).getPaytoDate();

                //保全核保针对一责任加费的交至日期小于该责任的交费日期时要计算当前要加费的金额及其利息并往保全批改补退费表中提交一笔交费项
                if (!mEdorType.equals("NS") && tDutyPaytoDate != null && tPremPaytoDate != null
                        && tDutyPaytoDate.compareTo(tPremPaytoDate) > 0)
                {

                    //生成利息交费项
                    //按照最新交费间隔计算交费期数
                    int premNum = 0;
                    if (mLCPolSchema.getPayIntv() == 0)
                    {
                        premNum = 1;
                    }
                    else
                    {
                        premNum = PubFun.calInterval(mLCPolSchema.getCValiDate()
                                , mLCPolSchema.getPaytoDate(), "M") / mLCPolSchema.getPayIntv();
                    }

                    double intervalMoney = mLPPremSet.get(i).getPrem(); //保费差额
                    double bfMoney = 0; //补费本金
                    double interestMoney = 0; //利息

 /**     注释 lanjun 2005/11/24 首先注释掉利息的计算方法             */

//                    //获取利率描述
//                    LMLoanDB tLMLoanDB = new LMLoanDB();
//                    tLMLoanDB.setRiskCode(mLCPolSchema.getRiskCode());
//                    if (!tLMLoanDB.getInfo())
//                    {
//                        //?
//                    }
//                    AccountManage tAccountManage = new AccountManage();
//
//                    //计算利息
//                    double interest = 0;
//                    for (int j = 0; j < premNum; j++)
//                    {
//                        if (j == 0)
//                        {
//                            interest = tAccountManage.getInterest(intervalMoney
//                                    , mLCPolSchema.getCValiDate(), PubFun.getCurrentDate()
//                                    , tLMLoanDB.getInterestRate(), tLMLoanDB.getInterestMode()
//                                    , tLMLoanDB.getInterestType(), "D");
//                        }
//                        else
//                        {
//                            interest = tAccountManage.getInterest(intervalMoney
//                                    , PubFun.calDate(mLCPolSchema.getCValiDate()
//                                    , j * mLCPolSchema.getPayIntv(), "M", mLCPolSchema.getCValiDate())
//                                    , PubFun.getCurrentDate(), tLMLoanDB.getInterestRate()
//                                    , tLMLoanDB.getInterestMode(), tLMLoanDB.getInterestType(), "D");
//                        }
//                        bfMoney += intervalMoney; //补费本金
//                        if (interest > 0)
//                        {
//                            interestMoney += interest; //利息
//                        }
//                        bfMoney = Double.parseDouble(new DecimalFormat("0.00").format(bfMoney));
//                        interestMoney = Double.parseDouble(new DecimalFormat("0.00").format(
//                                interestMoney));
//                    }

                    tTotalMoney = bfMoney + interestMoney;
                    //生成批改补退费表中的补费记录
                    LJSGetEndorseSchema tLJSGetEndorseSchema = new LJSGetEndorseSchema();
                    LPPremSchema tLPPremSchema = new LPPremSchema();
                    tLPPremSchema = mLPPremSet.get(i);
                    tLJSGetEndorseSchema.setGetNoticeNo(mEdorNo); //给付通知书号码
                    tLJSGetEndorseSchema.setEndorsementNo(mEdorNo);
                    tLJSGetEndorseSchema.setFeeOperationType(tLPPremSchema.getEdorType());

                    mReflections.transFields(tLJSGetEndorseSchema, mLCPolSchema);

                    tLJSGetEndorseSchema.setGetDate(mLPEdorMainSchema.getEdorValiDate());
                    tLJSGetEndorseSchema.setGetMoney(bfMoney); //本金额
                    tLJSGetEndorseSchema.setFeeFinaType("BF"); //	加费
                    tLJSGetEndorseSchema.setPayPlanCode(tLPPremSchema.getPayPlanCode()); //无作用
                    tLJSGetEndorseSchema.setDutyCode(tLPPremSchema.getDutyCode()); //无作用，但一定要，转ljagetendorse时非空
                    tLJSGetEndorseSchema.setOtherNo(mEdorNo); //无作用
                    tLJSGetEndorseSchema.setOtherNoType("3"); //保全给付
                    tLJSGetEndorseSchema.setGetFlag("0");

                    tLJSGetEndorseSchema.setOperator(mGlobalInput.Operator);
                    tLJSGetEndorseSchema.setMakeDate(tCurrentDate);
                    tLJSGetEndorseSchema.setMakeTime(tCurrentTime);
                    tLJSGetEndorseSchema.setModifyDate(tCurrentDate);
                    tLJSGetEndorseSchema.setModifyTime(tCurrentTime);

                    //生成保全批改补退费表利息记录
//                    LJSGetEndorseSchema tLJSGetEndorseSchemaLX = new LJSGetEndorseSchema();
//                    tLJSGetEndorseSchemaLX.setGetNoticeNo(mEdorNo); //给付通知书号码
//                    tLJSGetEndorseSchemaLX.setEndorsementNo(mEdorNo);
//                    tLJSGetEndorseSchemaLX.setFeeOperationType(tLPPremSchema.getEdorType());
//
//                    mReflections.transFields(tLJSGetEndorseSchemaLX, mLCPolSchema);
//
//                    tLJSGetEndorseSchemaLX.setGetDate(mLPEdorMainSchema.getEdorValiDate());
//                    tLJSGetEndorseSchemaLX.setGetMoney(interestMoney);
//                    tLJSGetEndorseSchemaLX.setFeeFinaType("LX"); //	利息
//                    tLJSGetEndorseSchemaLX.setPayPlanCode(tLPPremSchema.getPayPlanCode()); //无作用
//                    tLJSGetEndorseSchemaLX.setDutyCode(tLPPremSchema.getDutyCode()); //无作用，但一定要，转ljagetendorse时非空
//                    tLJSGetEndorseSchemaLX.setOtherNo(mEdorNo); //无作用
//                    tLJSGetEndorseSchemaLX.setOtherNoType("3"); //保全给付
//                    tLJSGetEndorseSchemaLX.setGetFlag("0");
//
//                    tLJSGetEndorseSchemaLX.setOperator(mLCPolSchema.getManageCom());
//                    tLJSGetEndorseSchemaLX.setMakeDate(tCurrentDate);
//                    tLJSGetEndorseSchemaLX.setMakeTime(tCurrentTime);
//                    tLJSGetEndorseSchemaLX.setModifyDate(tCurrentDate);
//                    tLJSGetEndorseSchemaLX.setModifyTime(tCurrentTime);

                    mNewLJSGetEndorseSet.add(tLJSGetEndorseSchema);
                    //mNewLJSGetEndorseSet.add(tLJSGetEndorseSchemaLX);
                }

                String tPayStartDate = mLPPremSet.get(i).getPayStartDate();
                //保全NS项目核保针对一责任加费的交至日期小于该责任的交费日期时要计算当前要加费的金额及其利息并往保全批改补退费表中提交一笔交费项
                if (mEdorType.equals("NS") && tPayStartDate != null)
                {
                    double intervalMoney = mLPPremSet.get(i).getPrem(); //保费差额
                    double bfMoney = 0; //补费本金
                    double interestMoney = 0; //利息

//          if(tCurrentDate.compareTo(tPayStartDate)>0)
//		  {
                    //生成利息交费项
                    //获取利率描述
//		  LMLoanDB tLMLoanDB = new LMLoanDB();
//		  tLMLoanDB.setRiskCode(mLCPolSchema.getRiskCode());
//		  if (!tLMLoanDB.getInfo())
//		  {
//			//?
//		  }
//		  AccountManage tAccountManage = new AccountManage();

                    //计算利息
                    //double interest = 0;
                    //interest = tAccountManage.getInterest(intervalMoney,tPayStartDate,PubFun.getCurrentDate(), tLMLoanDB.getInterestRate(), tLMLoanDB.getInterestMode(), tLMLoanDB.getInterestType(), "D");
                    //bfMoney = bfMoney + intervalMoney; //补费本金
                    //if (interest > 0) interestMoney = interestMoney + interest; //利息
                    //bfMoney = Double.parseDouble(new DecimalFormat("0.00").format(bfMoney));
                    //interestMoney = Double.parseDouble(new DecimalFormat("0.00").format(interestMoney));
//		  }

                    tTotalMoney = bfMoney + intervalMoney;
                    //生成批改补退费表中的补费记录
                    LJSGetEndorseSchema tLJSGetEndorseSchema = new LJSGetEndorseSchema();
                    LPPremSchema tLPPremSchema = new LPPremSchema();
                    tLPPremSchema = mLPPremSet.get(i);
                    tLJSGetEndorseSchema.setGetNoticeNo(mEdorNo); //给付通知书号码
                    tLJSGetEndorseSchema.setEndorsementNo(mEdorNo);
                    tLJSGetEndorseSchema.setFeeOperationType(tLPPremSchema.getEdorType());

                    mReflections.transFields(tLJSGetEndorseSchema, mLCPolSchema);

                    tLJSGetEndorseSchema.setGetDate(mLPEdorMainSchema.getEdorValiDate());
                    tLJSGetEndorseSchema.setGetMoney(intervalMoney); //本金额
                    tLJSGetEndorseSchema.setFeeFinaType("BF"); //	加费
                    tLJSGetEndorseSchema.setPayPlanCode(tLPPremSchema.getPayPlanCode()); //无作用
                    tLJSGetEndorseSchema.setDutyCode(tLPPremSchema.getDutyCode()); //无作用，但一定要，转ljagetendorse时非空
                    tLJSGetEndorseSchema.setOtherNo(mEdorNo); //无作用
                    tLJSGetEndorseSchema.setOtherNoType("3"); //保全给付
                    tLJSGetEndorseSchema.setGetFlag("0");

                    tLJSGetEndorseSchema.setOperator(mGlobalInput.Operator);
                    tLJSGetEndorseSchema.setMakeDate(tCurrentDate);
                    tLJSGetEndorseSchema.setMakeTime(tCurrentTime);
                    tLJSGetEndorseSchema.setModifyDate(tCurrentDate);
                    tLJSGetEndorseSchema.setModifyTime(tCurrentTime);

                    //生成保全批改补退费表利息记录
//                    LJSGetEndorseSchema tLJSGetEndorseSchemaLX = new LJSGetEndorseSchema();
//                    tLJSGetEndorseSchemaLX.setGetNoticeNo(mEdorNo); //给付通知书号码
//                    tLJSGetEndorseSchemaLX.setEndorsementNo(mEdorNo);
//                    tLJSGetEndorseSchemaLX.setFeeOperationType(tLPPremSchema.getEdorType());
//
//                    mReflections.transFields(tLJSGetEndorseSchemaLX, mLCPolSchema);
//
//                    tLJSGetEndorseSchemaLX.setGetDate(mLPEdorMainSchema.getEdorValiDate());
//                    tLJSGetEndorseSchemaLX.setGetMoney(interestMoney);
//                    tLJSGetEndorseSchemaLX.setFeeFinaType("LX"); //	利息
//                    tLJSGetEndorseSchemaLX.setPayPlanCode(tLPPremSchema.getPayPlanCode()); //无作用
//                    tLJSGetEndorseSchemaLX.setDutyCode(tLPPremSchema.getDutyCode()); //无作用，但一定要，转ljagetendorse时非空
//                    tLJSGetEndorseSchemaLX.setOtherNo(mEdorNo); //无作用
//                    tLJSGetEndorseSchemaLX.setOtherNoType("3"); //保全给付
//                    tLJSGetEndorseSchemaLX.setGetFlag("0");
//
//                    tLJSGetEndorseSchemaLX.setOperator(mLCPolSchema.getManageCom());
//                    tLJSGetEndorseSchemaLX.setMakeDate(tCurrentDate);
//                    tLJSGetEndorseSchemaLX.setMakeTime(tCurrentTime);
//                    tLJSGetEndorseSchemaLX.setModifyDate(tCurrentDate);
                    //tLJSGetEndorseSchemaLX.setModifyTime(tCurrentTime);

                    mNewLJSGetEndorseSet.add(tLJSGetEndorseSchema);
//                    mNewLJSGetEndorseSet.add(tLJSGetEndorseSchemaLX);
                }
            }
        }

        //准备保全核保主表信息
        LPUWMasterDB tLPUWMasterDB = new LPUWMasterDB();
	tLPUWMasterDB.setPolNo(mPolNo2);
        tLPUWMasterDB.setEdorNo(mEdorNo);
        tLPUWMasterDB.setEdorType(mEdorType);

        System.out.println(mPolNo2+"  "+ mEdorNo+"  "+mEdorType);
        if (!tLPUWMasterDB.getInfo())
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PEdorUWAddAfterInitService";
            tError.functionName = "prepareAdd";
            tError.errorMessage = "无保全批单核保主表信息!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLPUWMasterSchema.setSchema(tLPUWMasterDB);
        if (mAddReason != null && !mAddReason.trim().equals(""))
        {
            mLPUWMasterSchema.setAddPremReason(mAddReason);
        }
        else
        {
            mLPUWMasterSchema.setAddPremReason("");
        }

        if (mLPPremSet != null && mLPPremSet.size() > 0)
        {
            mLPUWMasterSchema.setChangePolFlag("1"); //有加费标识
        }
        else
        {
            mLPUWMasterSchema.setChangePolFlag("0"); //无加费标识
        }

        mLPUWMasterSchema.setOperator(mOperater);
        mLPUWMasterSchema.setManageCom(mManageCom);
        mLPUWMasterSchema.setModifyDate(tCurrentDate);
        mLPUWMasterSchema.setModifyTime(tCurrentTime);

        //准备保全批改主表信息(待核保确认时再修改)
        //double tGetMoney = tTotalMoney + mLPEdorMainSchema.getGetMoney();
        //mLPEdorMainSchema.setGetMoney(tGetMoney);
        // mLPEdorMainSchema.setOperator(mOperater) ;
        // mLPEdorMainSchema.setManageCom(mManageCom);
        // mLPEdorMainSchema.setModifyDate(tCurrentDate) ;
        // mLPEdorMainSchema.setModifyTime(tCurrentTime);

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


        //删除上一个加费数据
        if (mOldLPPremSet != null && mOldLPPremSet.size() > 0) {
          map.put(mOldLPPremSet, "DELETE");
        }

        //添加本次保全加费数据
        if (mLPPremSet != null && mLPPremSet.size() > 0) {
          map.put(mLPPremSet, "INSERT");
        }

        //修改本次保全加费后更新的保单责任数据
        if (mNewLPDutySet != null && mNewLPDutySet.size() > 0) {
          map.put(mNewLPDutySet, "UPDATE");
        }

        //修改本次保全加费后更新的保单数据
        if (mLPPolSchema != null) {
          map.put(mLPPolSchema, "UPDATE");
        }

        //准备删除上一次该NS项目的保全批改补退费表中保全人工核保加费的数据
        //准备删除上一次该NS项目的保全批改补退费表中保全人工核保加费的数据
        if (mOldLJSGetEndorseSet != null && mOldLJSGetEndorseSet.size() > 0)
        {
            map.put(mOldLJSGetEndorseSet, "DELETE");
        }

        //准备添加这次该NS项目的保全批改补退费表中保全人工核保加费的数据
        if (mNewLJSGetEndorseSet != null && mNewLJSGetEndorseSet.size() > 0)
        {
            map.put(mNewLJSGetEndorseSet, "INSERT");

        }

        //添加保全批单核保主表
        map.put(mLPUWMasterSchema, "UPDATE");

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

    public CErrors getErrors()
    {
        return mErrors;
    }
}

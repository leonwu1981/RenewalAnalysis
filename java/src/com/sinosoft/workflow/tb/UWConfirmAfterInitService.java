/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.cbcheck.SplitFamilyBL;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.finfee.TempFeeWithdrawBL;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: 新契约核保确认 </p>
 * <p>Description: 工作流服务类:执行新契约核保确认</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */
public class UWConfirmAfterInitService implements AfterInitService
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    private VData mReturnFeeData;


    /** 往界面传输数据的容器 */
    private VData mResult = new VData();
    private MMap mSplitMap = new MMap();

    /** 数据操作字符串 */
    private String mManageCom;
    private String mCalCode; //计算编码

    /** 业务处理相关变量 */
    private LCPolSet mLCPolSet = new LCPolSet();
    private LCPolSchema mLCPolSchema = new LCPolSchema();
    private String mOldPolNo = "";
    private String mUWFlag = ""; //核保标志
    private String mUWIdea = ""; //核保意见
    private String mStopFlag = "";
    private String mUWPopedom = ""; //操作员核保级别
    private String mAppGrade = ""; //上报级别
//    private String mPolType = ""; //保单类型
    private String mNewContNo = "";

    //modify by Minim
    private String mBackUWGrade = "";
    private String mBackAppGrade = "";
    private String mOperator = "";
    private String mOperatorUWGrade = "";
    private Reflections mReflections = new Reflections();
    private MMap mmap = new MMap();
    private String mPrtNo = "";

    private LJSPaySet outLJSPaySet = new LJSPaySet();
    private LJTempFeeSet outLJTempFeeSet = new LJTempFeeSet();
    private LBTempFeeSet outLBTempFeeSet = new LBTempFeeSet();
    private LJTempFeeClassSet outLJTempFeeClassSet = new LJTempFeeClassSet();
    private LBTempFeeClassSet outLBTempFeeClassSet = new LBTempFeeClassSet();


    /** 核保主表 */
    private LCCUWMasterSet mLCCUWMasterSet = new LCCUWMasterSet();
    private LCUWMasterSet mLCUWMasterSet = new LCUWMasterSet();


    /** 核保子表 */
    private LCUWSubSet mLCUWSubSet = new LCUWSubSet();
    private LCCUWSubSet mLCCUWSubSet = new LCCUWSubSet();


    /** 打印管理表 */
    private LOPRTManagerSet mLOPRTManagerSet = new LOPRTManagerSet();

    private LMUWSet mLMUWSet = new LMUWSet();
    private CalBase mCalBase = new CalBase();
    private GlobalInput mGlobalInput = new GlobalInput();
    private String mGetNoticeNo = "";
    private TransferData mTransferData = new TransferData();


    /** 数据操作字符串 */
    private String mOperater;


    /** 业务数据操作字符串 */
    private String mContNo;
    private String mMissionID;
    private String Poluwflagsql="";


    /**保单表*/
    private LCContSchema mLCContSchema = new LCContSchema();

    public UWConfirmAfterInitService()
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

        //校验
        if (!checkData())
        {
            return false;
        }

        //生成给付通知书号
        String tLimit = PubFun.getNoLimit(mManageCom);
        mGetNoticeNo = PubFun1.CreateMaxNo("GETNOTICENO", tLimit); //产生即付通知书号
//        System.out.println("---tLimit---" + tLimit);

        //拒保、延期或撤单时，校验银行在途数据
        if (mUWFlag.equals("a") || mUWFlag.equals("1") || mUWFlag.equals("8"))
        {
            //查询应收总表数据
//            String strSql =
//                    "select * from ljspay where trim(otherno) in (select trim(contno) from lccont where prtno='" +
//                    mPrtNo + "' union select trim(proposalcontno) from lccont where prtno='" +
//                    mPrtNo + "' union select trim(prtno) from lccont where prtno='" +
//                    mPrtNo + "' )";
            StringBuffer tSBql = new StringBuffer(128);
            tSBql.append(
                    "select * from ljspay where trim(otherno) in (select trim(contno) from lccont where prtno='");
            tSBql.append(mPrtNo);
            tSBql.append("' union select trim(proposalcontno) from lccont where prtno='");
            tSBql.append(mPrtNo);
            tSBql.append("' union select trim(prtno) from lccont where prtno='");
            tSBql.append(mPrtNo);
            tSBql.append("' )");
//            System.out.println("strSql=" + strSql);
            outLJSPaySet = (new LJSPayDB()).executeQuery(tSBql.toString());

            for (int i = 0; i < outLJSPaySet.size(); i++)
            {
                if (outLJSPaySet.get(i + 1).getBankOnTheWayFlag().equals("1"))
                {
                    System.out.println("有银行在途数据，不允许拒保、延期或撤单!");
                    // @@错误处理
                    CError tError = new CError();
                    tError.moduleName = "UWManuNormChkBL";
                    tError.functionName = "submitData";
                    tError.errorMessage = "有银行在途数据，不允许拒保、延期或撤单!";
                    this.mErrors.addOneError(tError);
                    return false;
                }
            }

            //查询暂交费表数据
//            strSql =
//                    "select * from ljtempfee where EnterAccDate is not null and trim(otherno) in (select trim(contno) from lccont where prtno='" +
//                    mPrtNo + "' union select trim(proposalcontno) from lccont where prtno='" +
//                    mPrtNo + "' union select trim(prtno) from lccont where prtno='" + mPrtNo
//                    + "' )";
            tSBql = new StringBuffer(128);
            tSBql.append("select * from ljtempfee where EnterAccDate is not null and trim(otherno) in (select trim(contno) from lccont where prtno='");
            tSBql.append(mPrtNo);
            tSBql.append("' union select trim(proposalcontno) from lccont where prtno='");
            tSBql.append(mPrtNo);
            tSBql.append("' union select trim(prtno) from lccont where prtno='");
            tSBql.append(mPrtNo);
            tSBql.append("' )");
//            System.out.println("strSql=" + strSql);
            outLJTempFeeSet = (new LJTempFeeDB()).executeQuery(tSBql.toString());

            if (outLJTempFeeSet.size() > 0)
            {
                for (int i = 0; i < outLJTempFeeSet.size(); i++)
                {
                    LBTempFeeSchema tLBTempFeeSchema = new LBTempFeeSchema();
                    mReflections.transFields(tLBTempFeeSchema, outLJTempFeeSet.get(i + 1));
                    tLBTempFeeSchema.setBackUpSerialNo(PubFun1.CreateMaxNo("LBTempFee", 20));
                    outLBTempFeeSet.add(tLBTempFeeSchema);

                    LJTempFeeClassDB tLJTempFeeClassDB = new LJTempFeeClassDB();
                    tLJTempFeeClassDB.setTempFeeNo(outLJTempFeeSet.get(i + 1).getTempFeeNo());
                    outLJTempFeeClassSet.add(tLJTempFeeClassDB.query());
                }

                for (int i = 0; i < outLJTempFeeClassSet.size(); i++)
                {
                    LBTempFeeClassSchema tLBTempFeeClassSchema = new LBTempFeeClassSchema();
                    mReflections.transFields(tLBTempFeeClassSchema, outLJTempFeeClassSet.get(i + 1));
                    tLBTempFeeClassSchema.setBackUpSerialNo(PubFun1.CreateMaxNo("LBTFClass", 20));
                    outLBTempFeeClassSet.add(tLBTempFeeClassSchema);
                }
            }
        }

        if (mUWFlag.equals("1") || mUWFlag.equals("4") || mUWFlag.equals("8") || mUWFlag.equals("9"))
        {
            //次标准体校验核保员级别
            if (!checkStandGrade())
            {
                return false;
            }
        }

        //拒保或延期要校验核保员级别
        if (mUWFlag.equals("1") || mUWFlag.equals("8"))
        {
            if (!checkUserGrade())
            {
                return false;
            }
        }

        //进行业务处理
        if (!dealData())
        {
            return false;
        }

        //撤单退费处理 以及修改保单表的uwflag
//        System.out.println("Start Return Tempfee");
        if (mUWFlag.equals("a") || mUWFlag.equals("1") || mUWFlag.equals("8"))
        {
            if (!returnFee())
            {
                return false;
            }
        //修改保单表的uwflag
            Poluwflagsql = "update lcpol set uwflag='"+ mUWFlag +"' where contno='"+ mContNo +"'";
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

//        System.out.println("Start  Submit...");

        //mResult.clear();
        return true;
    }


    /**
     * 数据操作撤单业务处理
     * 输出：如果出错，则返回false,否则返回true
     * @return boolean
     */
    private boolean returnFee()
    {
//        System.out.println("============In ReturnFee");

        String payMode = ""; //交费方式
        String BankCode = ""; //银行编码
        String BankAccNo = ""; //银行账号
        String AccName = ""; //户名

        //准备TransferData数据
//        String strSql = "";
        //测试该投保单是否有暂交费待退
//        strSql = "select * from ljtempfee where trim(otherno) in (select '" + mPrtNo
//                + "' from dual ) and EnterAccDate is not null and confdate is  null";
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from ljtempfee where trim(otherno) in (select '");
        tSBql.append(mPrtNo);
        tSBql.append("' from dual ) and EnterAccDate is not null and confdate is  null");

        LJTempFeeDB sLJTempFeeDB = new LJTempFeeDB();
        LJTempFeeSet sLJTempFeeSet = new LJTempFeeSet();
        sLJTempFeeSet = sLJTempFeeDB.executeQuery(tSBql.toString());
//        System.out.println("暂交费数量:  " + sLJTempFeeSet.size());
        if (sLJTempFeeSet.size() == 0)
        {
            System.out.println("Out ReturnFee");
            return true;
        }

        //如果通知书号不为空，找出退费方式（优先级依次为支票，银行，现金）
        GetPayType tGetPayType = new GetPayType();
        if (tGetPayType.getPayTypeForLCPol(mPrtNo))
        {
            payMode = tGetPayType.getPayMode(); //交费方式
            BankCode = tGetPayType.getBankCode(); //银行编码
            BankAccNo = tGetPayType.getBankAccNo(); //银行账号
            AccName = tGetPayType.getAccName(); //户名
        }
        else
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tGetPayType.mErrors);
            System.out.println("查询出错信息  :" + tGetPayType.mErrors);
            return false;
        }

        TransferData sTansferData = new TransferData();
        sTansferData.setNameAndValue("PayMode", payMode);
        sTansferData.setNameAndValue("NotBLS", "1");
        if (payMode.equals("1"))
        {
            sTansferData.setNameAndValue("BankFlag", "0");
        }
        else
        {
            sTansferData.setNameAndValue("BankCode", BankCode);
            sTansferData.setNameAndValue("AccNo", BankAccNo);
            sTansferData.setNameAndValue("AccName", AccName);
            sTansferData.setNameAndValue("BankFlag", "1");
        }
        sTansferData.setNameAndValue("GetNoticeNo", mGetNoticeNo);

        LJTempFeeSet tLJTempFeeSet = new LJTempFeeSet();
        LJAGetTempFeeSet tLJAGetTempFeeSet = new LJAGetTempFeeSet();

        for (int index = 1; index <= sLJTempFeeSet.size(); index++)
        {
            System.out.println("HaveDate In Second1");
            LJTempFeeSchema tLJTempFeeSchema = new LJTempFeeSchema();
            tLJTempFeeSchema.setTempFeeNo(sLJTempFeeSet.get(index).getTempFeeNo());
            tLJTempFeeSchema.setTempFeeType(sLJTempFeeSet.get(index).getTempFeeType());
            tLJTempFeeSchema.setRiskCode(sLJTempFeeSet.get(index).getRiskCode());
            tLJTempFeeSet.add(tLJTempFeeSchema);

            LJAGetTempFeeSchema tLJAGetTempFeeSchema = new LJAGetTempFeeSchema();
            //tLJAGetTempFeeSchema.setGetReasonCode(mAllLCUWMasterSet.get(1).getUWIdea());
            tLJAGetTempFeeSchema.setGetReasonCode("99");

            tLJAGetTempFeeSet.add(tLJAGetTempFeeSchema);

            //打印退费通知书
            // setLOPRTManager(tLJTempFeeSchema);

//            System.out.println("HaveDate In Second2");
//            try
//            {
//                System.out.println("TempFeeNo:  " + sLJTempFeeSet.get(index).getTempFeeNo());
//                System.out.println("TempFeeType:  " + sLJTempFeeSet.get(index).getTempFeeType());
//                System.out.println("RiskCode:  " + sLJTempFeeSet.get(index).getRiskCode());
//            }
//            catch (Exception e)
//            {
//                System.out.println("无银行数据!");
//            }
        }

        // 准备传输数据 VData
        VData tVData = new VData();
        tVData.add(tLJTempFeeSet);
        tVData.add(tLJAGetTempFeeSet);
        tVData.add(sTansferData);
        tVData.add(mGlobalInput);

        // 数据传输
//        System.out.println("--------开始传输数据---------");
        TempFeeWithdrawBL tTempFeeWithdrawBL = new TempFeeWithdrawBL();
        tTempFeeWithdrawBL.submitData(tVData, "INSERT");
//        if (tTempFeeWithdrawBL.submitData(tVData, "INSERT"))
//        {
//            System.out.println("---ok---");
//        }
//        else
//        {
//            System.out.println("---NO---");
//        }

        if (tTempFeeWithdrawBL.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tTempFeeWithdrawBL.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWManuNormChkBL";
            tError.functionName = "submitData";
            tError.errorMessage = "核保成功,但退费失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mReturnFeeData = tTempFeeWithdrawBL.getResult();

        mmap = (MMap) mReturnFeeData.getObjectByObjectName("MMap", 0);

//        System.out.println("Out ReturnFee");
        return true;
    }


//    /**
//     * 设置打印数据表
//     * @param tLJTempFeeSchema LJTempFeeSchema
//     * @return boolean
//     */
//    private boolean setLOPRTManager(LJTempFeeSchema tLJTempFeeSchema)
//    {
//        System.out.println("----------setLOPRTManager----------");
//        //3-准备打印数据,生成印刷流水号
//        LOPRTManagerSet outLOPRTManagerSet = new LOPRTManagerSet();
//        String tLimit = PubFun.getNoLimit(mManageCom);
//        String tNo = PubFun1.CreateMaxNo("GETNO", tLimit);
//        String prtSeqNo = "";
//        String mCurrentDate = PubFun.getCurrentDate();
//        String mCurrentTime = PubFun.getCurrentTime();
//
//        LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();
//        prtSeqNo = PubFun1.CreateMaxNo("PRTSEQNO", tLimit);
//        mLOPRTManagerSchema.setPrtSeq(prtSeqNo);
//        mLOPRTManagerSchema.setOtherNoType(PrintManagerBL.ONT_GET);
//        mLOPRTManagerSchema.setOtherNo(tNo); //实付号码
//        System.out.println("otherno:" + tNo);
//        mLOPRTManagerSchema.setMakeDate(mCurrentDate);
//        mLOPRTManagerSchema.setMakeTime(mCurrentTime);
//        mLOPRTManagerSchema.setManageCom(tLJTempFeeSchema.getManageCom());
//        mLOPRTManagerSchema.setAgentCode(tLJTempFeeSchema.getAgentCode());
//        mLOPRTManagerSchema.setCode(PrintManagerBL.CODE_REFUND);
//        mLOPRTManagerSchema.setReqCom(mManageCom);
//        mLOPRTManagerSchema.setReqOperator(mOperator);
//        mLOPRTManagerSchema.setPrtType("0");
//        mLOPRTManagerSchema.setStateFlag("0");
//        outLOPRTManagerSet.add(mLOPRTManagerSchema);
//        return true;
//    }


    /**
     * 次标准体校验核保员级别
     * 输出：如果发生错误则返回false,否则返回true
     * @return boolean
     */
    private boolean checkStandGrade()
    {
        CheckKinds("1");
        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setContNo(mLCContSchema.getContNo());
        mLCPolSet = tLCPolDB.query();
        //准备险种保单的复核标志
        for (int j = 1; j < mLCPolSet.size(); j++)
        {
            mOldPolNo = mLCPolSet.get(j).getPolNo();

            LCUWMasterSchema tLCUWMasterSchema = new LCUWMasterSchema();
            LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();

            tLCUWMasterDB.setProposalNo(mOldPolNo);

            if (tLCUWMasterDB.getInfo())
            {
                tLCUWMasterSchema = tLCUWMasterDB.getSchema();
            }
            else
            {
                // @@错误处理
                this.mErrors.copyAllErrors(tLCUWMasterDB.mErrors);
                CError tError = new CError();
                tError.moduleName = "UWManuNormChkBL";
                tError.functionName = "checkStandGrade";
                tError.errorMessage = "LCUWMaster表取数失败!";
                this.mErrors.addOneError(tError);
                return false;
            }

            //有特约，加费，保险计划变更为次标准体
            if (!tLCUWMasterSchema.getSpecFlag().equals("0") ||
                    !tLCUWMasterSchema.getChangePolFlag().equals("0"))
            {
                if (mLMUWSet.size() > 0)
                {
                    for (int i = 1; i <= mLMUWSet.size(); i++)
                    {
                        LMUWSchema tLMUWSchema = new LMUWSchema();
                        tLMUWSchema = mLMUWSet.get(i);

                        mCalCode = tLMUWSchema.getCalCode(); //次标准体计算公式代码
                        String tempuwgrade = CheckPol();
                        if (tempuwgrade != null)
                        {
                            if (mUWPopedom.compareTo(tempuwgrade) < 0)
                            {
                                CError tError = new CError();
                                tError.moduleName = "UWManuNormChkBL";
                                tError.functionName = "prepareAllPol";
                                tError.errorMessage =
                                        "无此次标准体投保件核保权限，需要上报上级核保师!";
                                this.mErrors.addOneError(tError);
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }


    /**
     * 核保险种信息校验,准备核保算法
     * 输出：如果发生错误则返回false,否则返回true
     * @param tFlag String
     * @return boolean
     */
    private boolean CheckKinds(String tFlag)
    {
        mLMUWSet.clear();
        LMUWSchema tLMUWSchema = new LMUWSchema();
        //查询算法编码
        if (tFlag.equals("1"))
        {
            tLMUWSchema.setUWType("13"); //非标准体
        }

        if (tFlag.equals("8"))
        {
            tLMUWSchema.setUWType("14"); //拒保延期
        }

        tLMUWSchema.setRiskCode("000000");
        tLMUWSchema.setRelaPolType("I");

        LMUWDB tLMUWDB = new LMUWDB();
        tLMUWDB.setSchema(tLMUWSchema);

        mLMUWSet = tLMUWDB.query();
        if (tLMUWDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMUWDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWManuNormChkBL";
            tError.functionName = "CheckKinds";
            tError.errorMessage = "核保级别校验算法读取失败!";
            this.mErrors.addOneError(tError);
            //mLMUWDBSet.clear();
            return false;
        }
        return true;
    }


    /**
     * 拒保，撤单校验核保员级别
     * 输出：如果发生错误则返回false,否则返回true
     * @return boolean
     */
    private boolean checkUserGrade()
    {
        CheckKinds("2");

        if (mLMUWSet.size() > 0)
        {
            for (int i = 1; i <= mLMUWSet.size(); i++)
            {
                LMUWSchema tLMUWSchema = new LMUWSchema();
                tLMUWSchema = mLMUWSet.get(i);

                mCalCode = tLMUWSchema.getCalCode(); //延期拒保计算公式代码
                String tempuwgrade = CheckPol();
                if (tempuwgrade != null)
                {
                    if (mUWPopedom.compareTo(tempuwgrade) < 0)
                    {
                        CError tError = new CError();
                        tError.moduleName = "UWManuNormChkBL";
                        tError.functionName = "prepareAllPol";
                        tError.errorMessage = "无此单拒保，延期权限，需上报上级核保师!";
                        this.mErrors.addOneError(tError);
                        return false;
                    }
                }
            }
        }

        return true;
    }


    /**
     * 个人单核保
     * @return String
     */
    private String CheckPol()
    {
        //准备数据
        CheckPolInit(mLCPolSchema);

        String tUWGrade = "";

        // 计算
        Calculator mCalculator = new Calculator();
        mCalculator.setCalCode(mCalCode);
        //增加基本要素
        mCalculator.addBasicFactor("Get", mCalBase.getGet());
        mCalculator.addBasicFactor("Mult", mCalBase.getMult());
        mCalculator.addBasicFactor("Prem", mCalBase.getPrem());
        //mCalculator.addBasicFactor("PayIntv", mCalBase.getPayIntv() );
        //mCalculator.addBasicFactor("GetIntv", mCalBase.getGetIntv() );
        mCalculator.addBasicFactor("AppAge", mCalBase.getAppAge());
        mCalculator.addBasicFactor("Sex", mCalBase.getSex());
        mCalculator.addBasicFactor("Job", mCalBase.getJob());
        mCalculator.addBasicFactor("PayEndYear", mCalBase.getPayEndYear());
        mCalculator.addBasicFactor("GetStartDate", "");
        //mCalculator.addBasicFactor("GetYear", mCalBase.getGetYear() );
        mCalculator.addBasicFactor("Years", mCalBase.getYears());
        mCalculator.addBasicFactor("Grp", "");
        mCalculator.addBasicFactor("GetFlag", "");
        mCalculator.addBasicFactor("ValiDate", "");
        mCalculator.addBasicFactor("Count", mCalBase.getCount());
        mCalculator.addBasicFactor("FirstPayDate", "");
        //mCalculator.addBasicFactor("AddRate", mCalBase.getAddRate() );
        //mCalculator.addBasicFactor("GDuty", mCalBase.getGDuty() );
        mCalculator.addBasicFactor("PolNo", mCalBase.getPolNo());
        mCalculator.addBasicFactor("InsuredNo", mLCPolSchema.getInsuredNo());

        String tStr = "";
        tStr = mCalculator.calculate();
        if (tStr.trim().equals(""))
        {
            tUWGrade = "";
        }
        else
        {
            tUWGrade = tStr.trim();
        }

        System.out.println("AmntGrade:" + tUWGrade);

        return tUWGrade;
    }


    /**
     * 个人单核保数据准备
     * @param tLCPolSchema LCPolSchema
     */
    private void CheckPolInit(LCPolSchema tLCPolSchema)
    {
        mCalBase = new CalBase();
        mCalBase.setPrem(tLCPolSchema.getPrem());
        mCalBase.setGet(tLCPolSchema.getAmnt());
        mCalBase.setMult(tLCPolSchema.getMult());
        //mCalBase.setYears( tLCPolSchema.getYears() );
        mCalBase.setAppAge(tLCPolSchema.getInsuredAppAge());
        mCalBase.setSex(tLCPolSchema.getInsuredSex());
        mCalBase.setJob(tLCPolSchema.getOccupationType());
        mCalBase.setCount(tLCPolSchema.getInsuredPeoples());
        mCalBase.setPolNo(tLCPolSchema.getPolNo());
    }


    /**
     * 准备返回前台统一存储数据
     * 输出：如果发生错误则返回false,否则返回true
     * @return boolean
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
//        mResult.add(mSplitMap);

        MMap map = new MMap();

        map.put(mLCContSchema, "UPDATE");
//        map.put(mLCPolSet, "UPDATE");
        map.put(mLCCUWMasterSet, "UPDATE");
        map.put(mLCCUWSubSet, "INSERT");
//        map.put(mLCUWMasterSet, "UPDATE");
//        map.put(mLCUWSubSet, "INSERT");
        map.put(outLJSPaySet, "DELETE");
        //20041207待测试
//    map.put(outLJTempFeeSet, "DELETE");
        map.put(outLBTempFeeSet, "INSERT");
//    map.put(outLJTempFeeClassSet, "DELETE");
        map.put(outLBTempFeeClassSet, "INSERT");
        map.put(mLOPRTManagerSet, "INSERT");
        if(Poluwflagsql!=null && !"".equals(Poluwflagsql))
        {
          map.put(Poluwflagsql,"UPDATE");
        }
        map.add(mmap);
        map.add(mSplitMap);

        mResult.add(map);
        return true;
    }


    /**
     * 校验业务数据
     * @return boolean
     */
    private boolean checkData()
    {
        //校验核保员级别
        LDUserDB tLDUserDB = new LDUserDB();
        tLDUserDB.setUserCode(mOperater);
//        System.out.println("mOperate" + mOperater);
        if (!tLDUserDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "UWManuNormChkBL";
            tError.functionName = "checkUWGrade";
            tError.errorMessage = "无此操作员信息，不能核保!（操作员：" + mOperater + "）";
            this.mErrors.addOneError(tError);
            return false;
        }

        LDUWUserDB tLDUWUserDB = new LDUWUserDB();
        tLDUWUserDB.setUserCode(mOperater);
        if (!tLDUWUserDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "UWManuNormChkBL";
            tError.functionName = "checkUWGrade";
            tError.errorMessage = "无此核保师信息，不能核保!（操作员：" + mOperater + "）";
            this.mErrors.addOneError(tError);
            return false;
        }
        mOperatorUWGrade = tLDUWUserDB.getUWPopedom();
        String tUWPopedom = tLDUWUserDB.getUWPopedom();
        mUWPopedom = tUWPopedom;
        mAppGrade = mUWPopedom;

        //校验保单信息
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mContNo);
        if (!tLCContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mContNo + "信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);

        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setContNo(mLCContSchema.getContNo());
        mLCPolSet = tLCPolDB.query();

        //校验是否下险种结论
        if (!mUWFlag.equals("1") && !mUWFlag.equals("8") && !mUWFlag.equals("a"))
        {
//            System.out.println("dddddf" + mLCPolSet.size());
            for (int i = 1; i <= mLCPolSet.size(); i++)
            {
                if (mLCPolSet.get(i).getUWFlag().equals("5") ||
                        mLCPolSet.get(i).getUWFlag().equals("z"))
                {
                    CError tError = new CError();
                    tError.moduleName = "UWRReportAfterInitService";
                    tError.functionName = "checkData";
                    tError.errorMessage = "险种保单" + mLCPolSet.get(i).getPolNo() + "未下核保结论";
                    this.mErrors.addOneError(tError);
                    return false;
                }
            }
        }

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
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName("GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName("TransferData", 0);

        if (mGlobalInput == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWConfirmAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWConfirmAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWConfirmAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的mCont
        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mContNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中ContNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的mPrtNo
        mPrtNo = (String) mTransferData.getValueByName("PrtNo");
        if (mPrtNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中PrtNo失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mUWFlag = (String) mTransferData.getValueByName("UWFlag");
        if (mUWFlag == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中UWFlag失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mUWIdea = (String) mTransferData.getValueByName("UWIdea");
        if (mUWIdea == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "UWConfirmAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中UWIdea失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

    /**
     * stopInsured
     * 暂停被保人
     * @return boolean
     */
    private boolean stopInsured()
    {
        SplitFamilyBL tSplitFamilyBL = new SplitFamilyBL();
        TransferData tTransferData = new TransferData();
        tTransferData.setNameAndValue("ContNo", mContNo);

        VData tVData = new VData();
        tVData.add(mGlobalInput);
        tVData.add(tTransferData);

        tSplitFamilyBL.submitData(tVData, "");

        if (tSplitFamilyBL.mErrors.needDealError())
        {
            this.mErrors.copyAllErrors(tSplitFamilyBL.mErrors);
            return false;
        }
        else
        {
            mNewContNo = (String) tSplitFamilyBL.getResult().getObject(0);
            System.out.println("NewContNo==" + mNewContNo);
            mSplitMap = (MMap) tSplitFamilyBL.getResult().getObjectByObjectName("MMap", 0);
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
        int tflag = 0;
        //准备合同表数据
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mContNo);
        if (!tLCContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "合同" + mContNo + "信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);

        //准备保单的复核标志
        mLCContSchema.setUWFlag(mUWFlag);
        mLCContSchema.setUWDate(PubFun.getCurrentDate());
//附加险拒保延期操作的时候，对LCCont的保费进行从新计算    chenhq
        String tsql = "select nvl(sum(prem),0) from lcpol where uwflag not in ('1','8','a') and contno='"+ mContNo +"'";
        ExeSQL tExeSQL = new ExeSQL();
        String UWprem = tExeSQL.getOneValue(tsql);
        mLCContSchema.setPrem(Double.parseDouble(UWprem));

        /*  险种核保结论和合同核保结论分开下
                 LCPolDB tLCPolDB = new LCPolDB();
                 tLCPolDB.setContNo(mLCContSchema.getContNo());
                 mLCPolSet = tLCPolDB.query();
                 //准备险种保单的复核标志
                 for (int i = 1; i <= mLCPolSet.size(); i++)
                 {
            mLCPolSet.get(i).setUWFlag(mUWFlag);
            mLCPolSet.get(i).setUWDate(PubFun.getCurrentDate());
                 }
         */
        //准备合同复核表数据
        if (!prepareContUW())
        {
            return false;
        }

        LCInsuredDB tLCInsuredDB = new LCInsuredDB();
        tLCInsuredDB.setContNo(mContNo);
        LCInsuredSet tLCInsuredSet = tLCInsuredDB.query();
        if (tLCInsuredSet == null || tLCInsuredSet.size() <= 0)
        {
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "dealData";
            tError.errorMessage = "被保人信息失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        for (int i = 1; i <= tLCInsuredSet.size(); i++)
        {
            if (tLCInsuredSet.get(i).getInsuredStat() != null
                    && tLCInsuredSet.get(i).getInsuredStat().length() > 0
                    && tLCInsuredSet.get(i).getInsuredStat().equals("1"))
            {
                tflag = 1;
            }
        }
        if (tflag == 1)
        {
            mStopFlag = "1";
            if (!stopInsured())
            {
                return false;
            }
        }

        return true;
    }

    /**
     * 准备主附险核保信息
     * 输出：如果发生错误则返回false,否则返回true
     * @return boolean
     */
    private boolean prepareContUW()
    {
        mLCCUWMasterSet.clear();
        mLCCUWSubSet.clear();

        LCCUWMasterSchema tLCCUWMasterSchema = new LCCUWMasterSchema();
        LCCUWMasterDB tLCCUWMasterDB = new LCCUWMasterDB();
        tLCCUWMasterDB.setContNo(mLCContSchema.getContNo());
        LCCUWMasterSet tLCCUWMasterSet = new LCCUWMasterSet();
        tLCCUWMasterSet = tLCCUWMasterDB.query();
        if (tLCCUWMasterDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCCUWMasterDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWManuNormChkBL";
            tError.functionName = "prepareAllUW";
            tError.errorMessage = "LCCUWMaster表取数失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        int n = tLCCUWMasterSet.size();
//        System.out.println("该投保单的核保主表当前记录条数:  " + n);
        if (n == 1)
        {
            tLCCUWMasterSchema = tLCCUWMasterSet.get(1);

            //为核保订正回退保存核保级别和核保人
            mBackUWGrade = tLCCUWMasterSchema.getUWGrade();
            mBackAppGrade = tLCCUWMasterSchema.getAppGrade();
            mOperator = tLCCUWMasterSchema.getOperator();

            //tLCCUWMasterSchema.setUWNo(tLCCUWMasterSchema.getUWNo()+1);核保主表中的UWNo表示该投保单经过几次人工核保(等价于经过几次自动核保次数),而不是人工核保结论(包括核保通知书,上报等)下过几次.所以将其注释.sxy-2003-09-19
            tLCCUWMasterSchema.setPassFlag(mUWFlag); //通过标志
            tLCCUWMasterSchema.setState(mUWFlag);
            tLCCUWMasterSchema.setUWIdea(mUWIdea);
            tLCCUWMasterSchema.setAutoUWFlag("2"); // 1 自动核保 2 人工核保
            tLCCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
            tLCCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());

            //核保订正
            if (mUWFlag.equals("z"))
            {
                tLCCUWMasterSchema.setAutoUWFlag("1");
                tLCCUWMasterSchema.setState("5");
                tLCCUWMasterSchema.setPassFlag("5");
                //恢复核保级别和核保员
                tLCCUWMasterSchema.setUWGrade(mBackUWGrade);
                tLCCUWMasterSchema.setAppGrade(mBackAppGrade);
                tLCCUWMasterSchema.setOperator(mOperator);
                //解锁
                LDSysTraceSchema tLDSysTraceSchema = new LDSysTraceSchema();
                tLDSysTraceSchema.setPolNo(mContNo);
                tLDSysTraceSchema.setCreatePos("人工核保");
                tLDSysTraceSchema.setPolState("1001");
                LDSysTraceSet inLDSysTraceSet = new LDSysTraceSet();
                inLDSysTraceSet.add(tLDSysTraceSchema);

                VData tVData = new VData();
                tVData.add(mGlobalInput);
                tVData.add(inLDSysTraceSet);

                LockTableBL LockTableBL1 = new LockTableBL();
                if (!LockTableBL1.submitData(tVData, "DELETE"))
                {
                    System.out.println("解锁失败！");
                }
            }
        }
        else
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCCUWMasterDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWManuNormChkBL";
            tError.functionName = "prepareAllUW";
            tError.errorMessage = "LCCUWMaster表取数据不唯一!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCCUWMasterSet.add(tLCCUWMasterSchema);

        // 核保轨迹表
        LCCUWSubSchema tLCCUWSubSchema = new LCCUWSubSchema();
        LCCUWSubDB tLCCUWSubDB = new LCCUWSubDB();
        tLCCUWSubDB.setContNo(mLCContSchema.getContNo());
        LCCUWSubSet tLCCUWSubSet = new LCCUWSubSet();
        tLCCUWSubSet = tLCCUWSubDB.query();
        if (tLCCUWSubDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCCUWSubDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWManuNormChkBL";
            tError.functionName = "prepareAllUW";
            tError.errorMessage = "LCCUWSub表取数失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        int m = tLCCUWSubSet.size();
//        System.out.println("subcount=" + m);
        if (m > 0)
        {
            m++; //核保次数
            tLCCUWSubSchema = new LCCUWSubSchema();
            tLCCUWSubSchema.setUWNo(m); //第几次核保
            tLCCUWSubSchema.setContNo(tLCCUWMasterSchema.getContNo());
            tLCCUWSubSchema.setGrpContNo(tLCCUWMasterSchema.getGrpContNo());
            tLCCUWSubSchema.setProposalContNo(tLCCUWMasterSchema.getProposalContNo());
            tLCCUWSubSchema.setOperator(mOperater);
            if (mUWFlag != null && mUWFlag.equals("z"))
            { //核保订正
                tLCCUWSubSchema.setPassFlag(mUWFlag); //核保意见
                tLCCUWSubSchema.setUWGrade(mUWPopedom); //核保级别
                tLCCUWSubSchema.setAppGrade(mAppGrade); //申请级别
                tLCCUWSubSchema.setAutoUWFlag("2");
                tLCCUWSubSchema.setState(mUWFlag);
                tLCCUWSubSchema.setOperator(mOperater); //操作员
            }

            tLCCUWSubSchema.setManageCom(tLCCUWMasterSchema.getManageCom());
            tLCCUWSubSchema.setMakeDate(PubFun.getCurrentDate());
            tLCCUWSubSchema.setMakeTime(PubFun.getCurrentTime());
            tLCCUWSubSchema.setModifyDate(PubFun.getCurrentDate());
            tLCCUWSubSchema.setModifyTime(PubFun.getCurrentTime());

        }
        else
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCCUWSubDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWManuNormChkBL";
            tError.functionName = "prepareAllUW";
            tError.errorMessage = "LCCUWSub表取数失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCCUWSubSet.add(tLCCUWSubSchema);

        return true;
    }


    /**
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
     * @return boolean
     */
    private boolean prepareTransferData()
    {
        LAAgentDB tLAAgentDB = new LAAgentDB();
        LAAgentSet tLAAgentSet = new LAAgentSet();
        tLAAgentDB.setAgentCode(mLCContSchema.getAgentCode());
        tLAAgentSet = tLAAgentDB.query();
        if (tLAAgentSet == null || tLAAgentSet.size() != 1)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人表LAAgent查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLAAgentSet.get(1).getAgentGroup() == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人表LAAgent中的代理机构数据丢失!";
            this.mErrors.addOneError(tError);
            return false;
        }

        LABranchGroupDB tLABranchGroupDB = new LABranchGroupDB();
        LABranchGroupSet tLABranchGroupSet = new LABranchGroupSet();
        tLABranchGroupDB.setAgentGroup(tLAAgentSet.get(1).getAgentGroup());
        tLABranchGroupSet = tLABranchGroupDB.query();
        if (tLABranchGroupSet == null || tLABranchGroupSet.size() != 1)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人展业机构表LABranchGroup查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLABranchGroupSet.get(1).getBranchAttr() == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "UWRReportAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人展业机构表LABranchGroup中展业机构信息丢失!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mTransferData.setNameAndValue("AgentCode", mLCContSchema.getAgentCode());
        mTransferData.setNameAndValue("AgentGroup", tLAAgentSet.get(1).getAgentGroup());
//        mTransferData.setNameAndValue("ManageCom", mManageCom);
        //应当使用合同上的管理机构
        mTransferData.setNameAndValue("ManageCom", mLCContSchema.getManageCom());
//        System.out.println("manageCom=" + mManageCom);
        mTransferData.setNameAndValue("PrtNo", mLCContSchema.getPrtNo());
        mTransferData.setNameAndValue("ContNo", mLCContSchema.getContNo());
        mTransferData.setNameAndValue("AppntCode", mLCContSchema.getAppntNo());
        mTransferData.setNameAndValue("AppntName", mLCContSchema.getAppntName());
        mTransferData.setNameAndValue("UWDate", PubFun.getCurrentDate());
        mTransferData.setNameAndValue("StopFlag", mStopFlag);
        mTransferData.setNameAndValue("NewContNo", mNewContNo);

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

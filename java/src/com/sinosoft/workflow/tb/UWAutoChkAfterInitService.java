/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title:工作流节点任务:新契约自动核保 </p>
 * <p>Description: 自动核保工作流后台AfterInit服务类 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: SinoSoft </p>
 * @author HYQ
 * @version 1.0
 */

public class UWAutoChkAfterInitService implements AfterInitService
{
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 往界面传输数据的容器 */
    MMap mMap = new MMap();
    private VData mResult = new VData();

    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();

    /** 数据操作字符串 */
    private String mOperator;
    private String mManageCom;

    /** 业务数据操作字符串 */
    private String mMissionID;

    /**保单表*/
    private LCContSchema mLCContSchema = new LCContSchema();
    private String mPolPassFlag = "0"; //险种通过标记
    private String mContPassFlag = "0"; //合同通过标记
    private String mUWGrade = "";
    private String mCalCode; //计算编码
    private double mValue;

    private LCContSet mAllLCContSet = new LCContSet();
    private LCPolSet mAllLCPolSet = new LCPolSet();
    private String mContNo = "";
    private String mPContNo = "";
    private String mOldPolNo = "";

    /** 合同核保主表*/
    private LCCUWMasterSet mLCCUWMasterSet = new LCCUWMasterSet();
    private LCCUWMasterSet mAllLCCUWMasterSet = new LCCUWMasterSet();

    /** 合同核保子表*/
    private LCCUWSubSet mLCCUWSubSet = new LCCUWSubSet();
    private LCCUWSubSet mAllLCCUWSubSet = new LCCUWSubSet();

    /** 合同核保错误信息表*/
    private LCCUWErrorSet mLCCUWErrorSet = new LCCUWErrorSet();
    private LCCUWErrorSet mAllLCCUWErrorSet = new LCCUWErrorSet();

    /** 各险种核保主表 */
    private LCUWMasterSet mLCUWMasterSet = new LCUWMasterSet();
    private LCUWMasterSet mAllLCUWMasterSet = new LCUWMasterSet();

    /** 各险种核保子表 */
    private LCUWSubSet mLCUWSubSet = new LCUWSubSet();
    private LCUWSubSet mAllLCUWSubSet = new LCUWSubSet();

    /** 核保错误信息表 */
    private LCUWErrorSet mLCUWErrorSet = new LCUWErrorSet();
    private LCUWErrorSet mAllLCErrSet = new LCUWErrorSet();

    private CalBase mCalBase = new CalBase();

    public UWAutoChkAfterInitService()
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
//        System.out.println("---UWAutoChkBL getInputData---");

        if (!checkData())
        {
            return false;
        }

        if (!dealData(mLCContSchema))
        {
            return false;
        }
//        System.out.println("---UWAutoChkBL dealData END---");

        //为工作流下一节点属性字段准备数据
        if (!prepareTransferData())
        {
            return false;
        }

        //准备给后台的数据
        if (prepareOutputData(mLCContSchema))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "UWAutoChkBL";
            tError.functionName = "submitData";
            tError.errorMessage = "提交的数据准备失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
//        System.out.println("Start  Submit...");

        mResult.clear();
        mResult.add(mMap);

        return true;
    }


    /**
     * 数据操作类业务处理
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @param tLCContSchema LCContSchema
     * @return boolean
     */
    private boolean dealData(LCContSchema tLCContSchema)
    {
//        LCPolSet tLCPolSet = new LCPolSet();
        LCPolSchema tLCPolSchema = null;
        LMUWSet tLMUWSetUnpass = new LMUWSet(); //未通过的核保规则
        LMUWSet tLMUWSetAll = null; //所有核保规则
        LMUWSet tLMUWSetSpecial = null; //需要风险检测的特殊核保规则
        LMUWSchema tLMUWSchema = null;

        //获得保单号
        mContNo = tLCContSchema.getContNo();
        //获得投保单号
        mPContNo = tLCContSchema.getProposalContNo();

        LCPolDB tLCPolDB = new LCPolDB();
        tLCPolDB.setContNo(mContNo);
        //获取个人合同下的险种信息
        LCPolSet tLCPolSet = tLCPolDB.query();

        int nPolIndex = 0;
        int nPolCount = tLCPolSet.size();
        for (nPolIndex = 1; nPolIndex <= nPolCount; nPolIndex++)
        {
            tLCPolSchema = tLCPolSet.get(nPolIndex);
            mOldPolNo = tLCPolSchema.getPolNo(); //获得保单险种号

            //准备算法，获取某险种的所有核保规则的集合
            tLMUWSetUnpass.clear();
            if (tLMUWSetAll != null)
            {
                tLMUWSetAll.clear();
            }
            if (tLMUWSetSpecial != null)
            {
                tLMUWSetSpecial.clear();
            }
            //获取险种核保定义信息，指定险种的核保信息
            tLMUWSetAll = CheckKinds(tLCPolSchema);
            if (tLMUWSetAll == null)
            {
                return false;
            }
            //获取险种核保定义信息，默认的险种核保信息
            tLMUWSetSpecial = CheckKinds2(tLCPolSchema);
            if (tLMUWSetSpecial == null)
            {
                return false;
            }

            //准备数据，从险种信息中获取各项计算信息
            CheckPolInit(tLCPolSchema);

            //个人单核保
            mPolPassFlag = "0"; //核保通过标志，初始为未核保

            int n = tLMUWSetAll.size(); //核保规则数量
            if (n == 0)
            {
                mPolPassFlag = "9"; //无核保规则则置标志为通过
            }
            else
            {
                //目前目前所有的险种均有一些公共的核保规则,所以必定走该分枝
                int j = 0;
                for (int i = 1; i <= n; i++)
                {
                    //取计算编码
//                    tLMUWSchema = new LMUWSchema();
//                    tLMUWSchema = tLMUWSetAll.get(i);
                    tLMUWSchema = tLMUWSetAll.get(i);
                    mCalCode = tLMUWSchema.getCalCode();
                    //核保计算
                    if (CheckPol(tLCPolSchema.getInsuredNo(), tLCPolSchema.getRiskCode()) == 0)
                    {
                    }
                    else
                    {
                        j++;
                        tLMUWSetUnpass.add(tLMUWSchema);
                        mPolPassFlag = "5"; //待人工核保
                        mContPassFlag = "5";

                        //取核保级别
                        String tuwgrade = tLMUWSchema.getUWGrade();
                        //如果险种默认的核保级别不存在，则退出
                        if (tuwgrade == null)
                        {
                            // @@错误处理
                            CError tError = new CError();
                            tError.moduleName = "UWAutoChkBL";
                            tError.functionName = "dealData";
                            tError.errorMessage = "取自核规则编码为：" + tLMUWSchema.getCalCode()
                                    + " 的核保级别错误";
                            this.mErrors.addOneError(tError);
                            return false;
                        }

                        if (j == 1)
                        {
                            mUWGrade = tuwgrade;
                        }
                        else
                        {
                            if (mUWGrade.compareTo(tuwgrade) < 0)
                            {
                                mUWGrade = tuwgrade;
                            }
                        }
                    }
                }

                //需要人工核保时候，校验核保返回核保员核保级别
                if (tLMUWSetUnpass.size() > 0 && tLMUWSetSpecial.size() > 0)
                {
                    for (int k = 1; k <= tLMUWSetSpecial.size(); k++)
                    {
                        LMUWSchema t2LMUWSchema = new LMUWSchema();
                        t2LMUWSchema = tLMUWSetSpecial.get(k);
                        mCalCode = t2LMUWSchema.getCalCode();

                        String tempuwgrade = checkRiskAmnt(tLCPolSchema);
                        if (tempuwgrade != null)
                        {
                            //当需要人工核保时候当即tLMUWSetUnpass.size()>0时,mUWGrade应该不为null,否则是自动核保规则中核保级别字段缺少了数据
                            if (mUWGrade == null || mUWGrade.compareTo(tempuwgrade) < 0)
                            {
                                mUWGrade = tempuwgrade;
                            }
                        }
                    }
                }
                else
                {
                    //当所有的自动核保不成功规则均不与该投保单匹配时核保级别会为空
                    //但一旦要进行核保订正会出现无核保级别的异常保错
                    //所以给所有无核保级别的投保单一个最低默认级别
                    if (mUWGrade == null || mUWGrade.equals(""))
                    {
                        StringBuffer tSBql = new StringBuffer(32);
                        tSBql.append("select min(UWGrade) from LMUW");
                        ExeSQL tExeSQL = new ExeSQL();
                        SSRS tSSRS = tExeSQL.execSQL(tSBql.toString());
                        if (tSSRS.getMaxRow() == 0)
                        {
                            //错误处理
                            CError tError = new CError();
                            tError.moduleName = "UWAutoChkBL";
                            tError.functionName = "dealData";
                            tError.errorMessage = "险种核保定无描述数据。";
                            this.mErrors.addOneError(tError);
                            return false;
                        }
                        else
                        {
                            mUWGrade = tSSRS.GetText(1, 1);
                        }
                    }
                }

                if (mPolPassFlag.equals("0"))
                {
                    mPolPassFlag = "9";
                }
//                System.out.println("匹配数:" + tLMUWSetAll.size() + "级别计算:" + tLMUWSetSpecial.size()
//                        + "级别:" + mUWGrade);
            }
            if (!dealOnePol(tLCPolSchema, tLMUWSetUnpass))
            {
                return false;
            }
        }

        /* 合同核保 */
        LMUWSet tLMUWSetContUnpass = new LMUWSet(); //未通过的合同核保规则
        LMUWSet tLMUWSetContAll = CheckKinds3(); //所有合同核保规则

        //准备数据，从险种信息中获取各项计算信息
        CheckContInit(tLCContSchema);

        //个人合同核保
        int tCount = tLMUWSetContAll.size(); //核保规则数量
        if (tCount == 0)
        {
            //无核保规则则置标志为通过
            mContPassFlag = "9";
        }
        else
        {
            //目前目前所有的险种均有一些公共的核保规则,所以必定走该分枝
            int j = 0;
            for (int index = 1; index <= tCount; index++)
            {
                //取计算编码
                tLMUWSchema = new LMUWSchema();
                tLMUWSchema = tLMUWSetContAll.get(index);
                mCalCode = tLMUWSchema.getCalCode();
                if (CheckPol(tLCContSchema.getInsuredNo(), "000000") == 0)
                {
                }
                else
                {
                    j++;
                    tLMUWSetContUnpass.add(tLMUWSchema);
                    mContPassFlag = "5"; //核保不通过，待人工核保

                    //取核保级别
                    String tuwgrade = tLMUWSchema.getUWGrade();
                    if (tuwgrade == null)
                    {
                        // @@错误处理
                        CError tError = new CError();
                        tError.moduleName = "UWAutoChkBL";
                        tError.functionName = "dealData";
                        tError.errorMessage = "合同核保时取自核规则编码为：" + tLMUWSchema.getCalCode()
                                + " 的核保级别错误";
                        this.mErrors.addOneError(tError);
                        return false;
                    }

                    if (j == 1 && (mUWGrade == null || mUWGrade.equals("")))
                    {
                        mUWGrade = tuwgrade;
                    }
                    else
                    {
                        if (mUWGrade.compareTo(tuwgrade) < 0)
                        {
                            mUWGrade = tuwgrade;
                        }
                    }
                }
            }

            if (mUWGrade == null || mUWGrade.equals(""))
            {
                StringBuffer tSBql = new StringBuffer(32);
                tSBql.append("select min(UWGrade) from LMUW");
                ExeSQL tExeSQL = new ExeSQL();
                SSRS tSSRS = tExeSQL.execSQL(tSBql.toString());
                if (tSSRS.getMaxRow() == 0)
                {
                    //错误处理
                    CError tError = new CError();
                    tError.moduleName = "UWAutoChkBL";
                    tError.functionName = "dealData";
                    tError.errorMessage = "险种核保定无描述数据。";
                    this.mErrors.addOneError(tError);
                    return false;
                }
                else
                {
                    mUWGrade = tSSRS.GetText(1, 1);
                }
            }

            if (mContPassFlag.equals("0"))
            {
                mContPassFlag = "9";
            }
//            System.out.println("合同核保匹配数:" + tLMUWSetContAll.size() + "合同核保未通过数:"
//                    + tLMUWSetContUnpass.size() + "级别:" + mUWGrade);
        }
        dealOneCont(tLCContSchema, tLMUWSetContUnpass);
        return true;
    }


    /**
     * 根据保额校验核保级别
     * @param tLCPolSchema LCPolSchema
     * @return String
     */
    private String checkRiskAmnt(LCPolSchema tLCPolSchema)
    {
        String tUWGrade = "";
        // 计算
        Calculator mCalculator = new Calculator();
        mCalculator.setCalCode(mCalCode);

        //增加基本要素
        mCalculator.addBasicFactor("Get", mCalBase.getGet());
        mCalculator.addBasicFactor("Mult", mCalBase.getMult());
        mCalculator.addBasicFactor("Prem", mCalBase.getPrem());
        mCalculator.addBasicFactor("AppAge", mCalBase.getAppAge());
        mCalculator.addBasicFactor("Sex", mCalBase.getSex());
        mCalculator.addBasicFactor("Job", mCalBase.getJob());
        mCalculator.addBasicFactor("PayEndYear", mCalBase.getPayEndYear());
        mCalculator.addBasicFactor("GetStartDate", "");
        mCalculator.addBasicFactor("Years", mCalBase.getYears());
        mCalculator.addBasicFactor("Grp", "");
        mCalculator.addBasicFactor("GetFlag", "");
        mCalculator.addBasicFactor("ValiDate", "");
        mCalculator.addBasicFactor("Count", mCalBase.getCount());
        mCalculator.addBasicFactor("FirstPayDate", "");
        mCalculator.addBasicFactor("PolNo", mCalBase.getPolNo());
        mCalculator.addBasicFactor("InsuredNo", tLCPolSchema.getInsuredNo());
        mCalculator.addBasicFactor("RiskCode", tLCPolSchema.getRiskCode());

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
//        System.out.println("AmntGrade:" + tUWGrade);
        return tUWGrade;
    }


    /**
     * 操作一张保单的业务处理
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @param tLCPolSchema LCPolSchema
     * @param tLMUWSetUnpass LMUWSet
     * @return boolean
     */
    private boolean dealOnePol(LCPolSchema tLCPolSchema, LMUWSet tLMUWSetUnpass)
    {
        // 保单
        if (!preparePol(tLCPolSchema))
        {
            return false;
        }
        // 核保信息
        if (!preparePolUW(tLCPolSchema, tLMUWSetUnpass))
        {
            return false;
        }

        LCPolSchema tLCPolSchemaDup = new LCPolSchema();
        tLCPolSchemaDup.setSchema(tLCPolSchema);
        mAllLCPolSet.add(tLCPolSchemaDup);

        LCUWMasterSet tLCUWMasterSet = new LCUWMasterSet();
        tLCUWMasterSet.set(mLCUWMasterSet);
        mAllLCUWMasterSet.add(tLCUWMasterSet);

        LCUWSubSet tLCUWSubSet = new LCUWSubSet();
        tLCUWSubSet.set(mLCUWSubSet);
        mAllLCUWSubSet.add(tLCUWSubSet);

        LCUWErrorSet tLCUWErrorSet = new LCUWErrorSet();
        tLCUWErrorSet.set(mLCUWErrorSet);
        mAllLCErrSet.add(tLCUWErrorSet);

        return true;
    }


    /**
     * 操作一张保单的业务处理
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @param tLCContSchema LCContSchema
     * @param tLMUWSetContUnpass LMUWSet
     * @return boolean
     */
    private boolean dealOneCont(LCContSchema tLCContSchema, LMUWSet tLMUWSetContUnpass)
    {
        prepareContUW(tLCContSchema, tLMUWSetContUnpass);

        LCContSchema tLCContSchemaDup = new LCContSchema();
        tLCContSchemaDup.setSchema(tLCContSchema);
        mAllLCContSet.add(tLCContSchemaDup);

        LCCUWMasterSet tLCCUWMasterSet = new LCCUWMasterSet();
        tLCCUWMasterSet.set(mLCCUWMasterSet);
        mAllLCCUWMasterSet.add(tLCCUWMasterSet);

        LCCUWSubSet tLCCUWSubSet = new LCCUWSubSet();
        tLCCUWSubSet.set(mLCCUWSubSet);
        mAllLCCUWSubSet.add(tLCCUWSubSet);

        LCCUWErrorSet tLCCUWErrorSet = new LCCUWErrorSet();
        tLCCUWErrorSet.set(mLCCUWErrorSet);
        mAllLCCUWErrorSet.add(tLCCUWErrorSet);

        return true;
    }


    /**
     * 校验投保单是否复核
     * 输出：如果发生错误则返回false,否则返回true
     * @param tLCContSchema LCContSchema
     * @return boolean
     */
    private boolean checkApprove(LCContSchema tLCContSchema)
    {
        if (tLCContSchema.getApproveFlag() == null || !tLCContSchema.getApproveFlag().equals("9"))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "UWAutoChkBL";
            tError.functionName = "checkApprove";
            tError.errorMessage = "投保单尚未进行复核操作，不能核保!（投保单号："
                    + tLCContSchema.getProposalContNo().trim() + "）";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }


    /**
     * 校验核保员级别
     * 输出：如果发生错误则返回false,否则返回true
     * @return boolean
     */
    private boolean checkUWGrade()
    {
        LDUserDB tLDUserDB = new LDUserDB();
        tLDUserDB.setUserCode(mOperator);

        if (!tLDUserDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "UWAutoChkBL";
            tError.functionName = "checkUWGrade";
            tError.errorMessage = "无此操作员信息，不能核保!（操作员：" + mOperator + "）";
            this.mErrors.addOneError(tError);
            return false;
        }

        String tUWPopedom = tLDUserDB.getUWPopedom();
        if (tUWPopedom == null || tUWPopedom.equals(""))
        {
            CError tError = new CError();
            tError.moduleName = "UWAutoChkBL";
            tError.functionName = "checkUWGrade";
            tError.errorMessage = "操作员无核保权限，不能核保!（操作员：" + mOperator + "）";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 核保险种信息校验,准备核保算法
     * @param tLCPolSchema LCPolSchema
     * @return LMUWSet
     */
    private LMUWSet CheckKinds(LCPolSchema tLCPolSchema)
    {
//        String tsql = "";
//        tsql = "select * from lmuw where (riskcode = '000000' and relapoltype = 'I' and uwtype = '11') or (riskcode = '" +
//                tLCPolSchema.getRiskCode().trim() +
//                "' and relapoltype = 'I' and uwtype = '1')  order by calcode";
        //查询算法编码
        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("select * from lmuw where (riskcode = '000000' and relapoltype = 'I' and uwtype = '11') or (riskcode = '");
        tSBql.append(tLCPolSchema.getRiskCode().trim());
        tSBql.append("' and relapoltype = 'I' and uwtype = '1')  order by calcode");

        LMUWDB tLMUWDB = new LMUWDB();
        LMUWSet tLMUWSet = tLMUWDB.executeQuery(tSBql.toString());
        if (tLMUWDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMUWDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWAutoChkBL";
            tError.functionName = "CheckKinds";
            tError.errorMessage = tLCPolSchema.getRiskCode().trim() +
                    "险种核保信息查询失败!";
            this.mErrors.addOneError(tError);
            tLMUWSet.clear();
            return null;
        }
        return tLMUWSet;
    }


    /**
     * 核保险种信息校验,准备核保算法
     * @param tLCPolSchema LCPolSchema
     * @return LMUWSet
     */
    private LMUWSet CheckKinds2(LCPolSchema tLCPolSchema)
    {
        String tsql = "";
//        LMUWSchema tLMUWSchema = new LMUWSchema();
        //查询算法编码
        tsql =
                "select * from lmuw where riskcode = '000000' and relapoltype = 'I' and uwtype = '12'";

        LMUWDB tLMUWDB = new LMUWDB();
        LMUWSet tLMUWSet = tLMUWDB.executeQuery(tsql);
        if (tLMUWDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMUWDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWAutoChkBL";
            tError.functionName = "CheckKinds2";
            tError.errorMessage = tLCPolSchema.getRiskCode().trim() +
                    "险种信息核保查询失败!";
            this.mErrors.addOneError(tError);
            tLMUWSet.clear();
            return null;
        }
        return tLMUWSet;
    }


    /**
     * 核保险种信息校验,准备核保算法
     * @return LMUWSet
     */
    private LMUWSet CheckKinds3()
    {
        String tsql = "";
//        LMUWSchema tLMUWSchema = new LMUWSchema();
        //查询算法编码
        tsql =
                "select * from lmuw where riskcode = '000000' and relapoltype = 'I' and uwtype = '19'";

        LMUWDB tLMUWDB = new LMUWDB();
        LMUWSet tLMUWSet = tLMUWDB.executeQuery(tsql);
        if (tLMUWDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLMUWDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWAutoChkBL";
            tError.functionName = "CheckKinds3";
            tError.errorMessage = "合同险种核保信息查询失败!";
            this.mErrors.addOneError(tError);
            tLMUWSet.clear();
            return null;
        }
        return tLMUWSet;
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
        mCalBase.setAppAge(tLCPolSchema.getInsuredAppAge());
        mCalBase.setSex(tLCPolSchema.getInsuredSex());
        mCalBase.setJob(tLCPolSchema.getOccupationType());
        mCalBase.setCount(tLCPolSchema.getInsuredPeoples());
        mCalBase.setPolNo(tLCPolSchema.getPolNo());
        mCalBase.setContNo(mContNo);
    }


    /**
     * 个人单核保数据准备
     * @param tLCContSchema LCContSchema
     */
    private void CheckContInit(LCContSchema tLCContSchema)
    {
        mCalBase = new CalBase();
        mCalBase.setPrem(tLCContSchema.getPrem());
        mCalBase.setGet(tLCContSchema.getAmnt());
        mCalBase.setMult(tLCContSchema.getMult());
//            mCalBase.setAppAge( tLCContSchema.getInsuredAppAge() );
        mCalBase.setSex(tLCContSchema.getInsuredSex());
//            mCalBase.setJob( tLCContSchema.getOccupationType() );
//            mCalBase.setCount( tLCContSchema.getInsuredPeoples() );
        mCalBase.setContNo(mContNo);
    }


    /**
     * 个人单核保
     * @param tInsuredNo String
     * @param tRiskCode String
     * @return double
     */
    private double CheckPol(String tInsuredNo, String tRiskCode) //LCPolSchema tLCPolSchema)
    {
        // 计算
        Calculator mCalculator = new Calculator();
        mCalculator.setCalCode(mCalCode);
        //增加基本要素
        mCalculator.addBasicFactor("Get", mCalBase.getGet());
        mCalculator.addBasicFactor("Mult", mCalBase.getMult());
        mCalculator.addBasicFactor("Prem", mCalBase.getPrem());
        mCalculator.addBasicFactor("AppAge", mCalBase.getAppAge());
        mCalculator.addBasicFactor("Sex", mCalBase.getSex());
        mCalculator.addBasicFactor("Job", mCalBase.getJob());
        mCalculator.addBasicFactor("PayEndYear", mCalBase.getPayEndYear());
        mCalculator.addBasicFactor("GetStartDate", "");
        mCalculator.addBasicFactor("Years", mCalBase.getYears());
        mCalculator.addBasicFactor("Grp", "");
        mCalculator.addBasicFactor("GetFlag", "");
        mCalculator.addBasicFactor("ValiDate", "");
        mCalculator.addBasicFactor("Count", mCalBase.getCount());
        mCalculator.addBasicFactor("FirstPayDate", "");
        mCalculator.addBasicFactor("ContNo", mCalBase.getContNo());
        mCalculator.addBasicFactor("PolNo", mCalBase.getPolNo());
        mCalculator.addBasicFactor("InsuredNo", tInsuredNo); //tLCPolSchema.getInsuredNo());;
        mCalculator.addBasicFactor("RiskCode", tRiskCode); //tLCPolSchema.getRiskCode());;
        String tStr = "";
        tStr = mCalculator.calculate();
        if (tStr == null || tStr.trim().equals(""))
        {
            mValue = 0;
        }
        else
        {
            mValue = Double.parseDouble(tStr);
        }
//        System.out.println(mValue);
        return mValue;
    }


    /**
     * 准备保单信息
     * 输出：如果发生错误则返回false,否则返回true
     * @param tLCPolSchema LCPolSchema
     * @return boolean
     */
    private boolean preparePol(LCPolSchema tLCPolSchema)
    {
//        System.out.println("险种核保标志" + mPolPassFlag);
        tLCPolSchema.setUWFlag(mPolPassFlag);
        tLCPolSchema.setUWCode(mOperator);
        tLCPolSchema.setUWDate(PubFun.getCurrentDate());
        tLCPolSchema.setModifyDate(PubFun.getCurrentDate());
        tLCPolSchema.setModifyTime(PubFun.getCurrentTime());
        return true;
    }


    /**
     * 准备合同核保信息
     * 输出：如果发生错误则返回false,否则返回true
     * @param tLCContSchema LCContSchema
     * @param tLMUWSetContUnpass LMUWSet
     * @return boolean
     */
    private boolean prepareContUW(LCContSchema tLCContSchema, LMUWSet tLMUWSetContUnpass)
    {
        tLCContSchema.setUWFlag(mContPassFlag);
        tLCContSchema.setUWOperator(mOperator);
        tLCContSchema.setUWDate(PubFun.getCurrentDate());
        tLCContSchema.setUWTime(PubFun.getCurrentTime());
        tLCContSchema.setModifyDate(PubFun.getCurrentDate());
        tLCContSchema.setModifyTime(PubFun.getCurrentTime());

        //合同核保主表
        boolean firstUW = true;
        LCCUWMasterSchema tLCCUWMasterSchema = new LCCUWMasterSchema();
        LCCUWMasterDB tLCCUWMasterDB = new LCCUWMasterDB();
        tLCCUWMasterDB.setContNo(mContNo);
        LCCUWMasterSet tLCCUWMasterSet = new LCCUWMasterSet();
        tLCCUWMasterSet = tLCCUWMasterDB.query();
        if (tLCCUWMasterDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCCUWMasterDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWAtuoChkAfterInitService";
            tError.functionName = "prepareContUW";
            tError.errorMessage = mContNo + "合同核保总表取数失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLCCUWMasterSet.size() == 0)
        {
            tLCCUWMasterSchema.setContNo(mContNo);
            tLCCUWMasterSchema.setGrpContNo(tLCContSchema.getGrpContNo());
            tLCCUWMasterSchema.setProposalContNo(tLCContSchema.getProposalContNo());
            tLCCUWMasterSchema.setUWNo(1);
            tLCCUWMasterSchema.setInsuredNo(tLCContSchema.getInsuredNo());
            tLCCUWMasterSchema.setInsuredName(tLCContSchema.getInsuredName());
            tLCCUWMasterSchema.setAppntNo(tLCContSchema.getAppntNo());
            tLCCUWMasterSchema.setAppntName(tLCContSchema.getAppntName());
            tLCCUWMasterSchema.setAgentCode(tLCContSchema.getAgentCode());
            tLCCUWMasterSchema.setAgentGroup(tLCContSchema.getAgentGroup());
            tLCCUWMasterSchema.setUWGrade(mUWGrade); //核保级别
            tLCCUWMasterSchema.setAppGrade(mUWGrade); //申报级别
            tLCCUWMasterSchema.setPostponeDay("");
            tLCCUWMasterSchema.setPostponeDate("");
            tLCCUWMasterSchema.setAutoUWFlag("1"); // 1 自动核保 2 人工核保
            tLCCUWMasterSchema.setState(mContPassFlag);
            tLCCUWMasterSchema.setPassFlag(mContPassFlag);
            tLCCUWMasterSchema.setHealthFlag("0");
            tLCCUWMasterSchema.setSpecFlag("0");
            tLCCUWMasterSchema.setQuesFlag("0");
            tLCCUWMasterSchema.setReportFlag("0");
            tLCCUWMasterSchema.setChangePolFlag("0");
            tLCCUWMasterSchema.setPrintFlag("0");
            tLCCUWMasterSchema.setPrintFlag2("0");
            tLCCUWMasterSchema.setManageCom(tLCContSchema.getManageCom());
            tLCCUWMasterSchema.setUWIdea("");
            tLCCUWMasterSchema.setUpReportContent("");
            tLCCUWMasterSchema.setOperator(mOperator); //操作员
            tLCCUWMasterSchema.setMakeDate(PubFun.getCurrentDate());
            tLCCUWMasterSchema.setMakeTime(PubFun.getCurrentTime());
            tLCCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
            tLCCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());
        }
        else
        {
            firstUW = false;
            tLCCUWMasterSchema = tLCCUWMasterSet.get(1);
            tLCCUWMasterSchema.setUWNo(tLCCUWMasterSchema.getUWNo() + 1);
            tLCCUWMasterSchema.setState(mContPassFlag);
            tLCCUWMasterSchema.setPassFlag(mContPassFlag);
            tLCCUWMasterSchema.setAutoUWFlag("1"); // 1 自动核保 2 人工核保
            tLCCUWMasterSchema.setUWGrade(mUWGrade); //核保级别
            tLCCUWMasterSchema.setAppGrade(mUWGrade); //申报级别
            tLCCUWMasterSchema.setOperator(mOperator); //操作员
            tLCCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
            tLCCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());
        }
        mLCCUWMasterSet.clear();
        mLCCUWMasterSet.add(tLCCUWMasterSchema);

        // 合同核保轨迹表
        LCCUWSubSchema tLCCUWSubSchema = new LCCUWSubSchema();
        LCCUWSubDB tLCCUWSubDB = new LCCUWSubDB();
        tLCCUWSubDB.setContNo(mContNo);
        LCCUWSubSet tLCCUWSubSet = new LCCUWSubSet();
        tLCCUWSubSet = tLCCUWSubDB.query();
        if (tLCCUWSubDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCCUWSubDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWAtuoChkAfterInitService";
            tError.functionName = "prepareContUW";
            tError.errorMessage = mContNo + "合同核保轨迹表查失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        int nUWNo = tLCCUWSubSet.size();
        if (nUWNo > 0)
        {
            tLCCUWSubSchema.setUWNo(++nUWNo); //第几次核保
        }
        else
        {
            tLCCUWSubSchema.setUWNo(1); //第1次核保
        }

        tLCCUWSubSchema.setContNo(tLCCUWMasterSchema.getContNo());
        tLCCUWSubSchema.setGrpContNo(tLCCUWMasterSchema.getGrpContNo());
        tLCCUWSubSchema.setProposalContNo(tLCCUWMasterSchema.getProposalContNo());
        tLCCUWSubSchema.setInsuredNo(tLCCUWMasterSchema.getInsuredNo());
        tLCCUWSubSchema.setInsuredName(tLCCUWMasterSchema.getInsuredName());
        tLCCUWSubSchema.setAppntNo(tLCCUWMasterSchema.getAppntNo());
        tLCCUWSubSchema.setAppntName(tLCCUWMasterSchema.getAppntName());
        tLCCUWSubSchema.setAgentCode(tLCCUWMasterSchema.getAgentCode());
        tLCCUWSubSchema.setAgentGroup(tLCCUWMasterSchema.getAgentGroup());
        tLCCUWSubSchema.setUWGrade(tLCCUWMasterSchema.getUWGrade()); //核保级别
        tLCCUWSubSchema.setAppGrade(tLCCUWMasterSchema.getAppGrade()); //申请级别
        tLCCUWSubSchema.setAutoUWFlag(tLCCUWMasterSchema.getAutoUWFlag());
        tLCCUWSubSchema.setState(tLCCUWMasterSchema.getState());
        tLCCUWSubSchema.setPassFlag(tLCCUWMasterSchema.getState());
        tLCCUWSubSchema.setPostponeDay(tLCCUWMasterSchema.getPostponeDay());
        tLCCUWSubSchema.setPostponeDate(tLCCUWMasterSchema.getPostponeDate());
        tLCCUWSubSchema.setUpReportContent(tLCCUWMasterSchema.getUpReportContent());
        tLCCUWSubSchema.setHealthFlag(tLCCUWMasterSchema.getHealthFlag());
        tLCCUWSubSchema.setSpecFlag(tLCCUWMasterSchema.getSpecFlag());
        tLCCUWSubSchema.setSpecReason(tLCCUWMasterSchema.getSpecReason());
        tLCCUWSubSchema.setQuesFlag(tLCCUWMasterSchema.getQuesFlag());
        tLCCUWSubSchema.setReportFlag(tLCCUWMasterSchema.getReportFlag());
        tLCCUWSubSchema.setChangePolFlag(tLCCUWMasterSchema.getChangePolFlag());
        tLCCUWSubSchema.setChangePolReason(tLCCUWMasterSchema.getChangePolReason());
        tLCCUWSubSchema.setAddPremReason(tLCCUWMasterSchema.getAddPremReason());
        tLCCUWSubSchema.setPrintFlag(tLCCUWMasterSchema.getPrintFlag());
        tLCCUWSubSchema.setPrintFlag2(tLCCUWMasterSchema.getPrintFlag2());
        tLCCUWSubSchema.setUWIdea(tLCCUWMasterSchema.getUWIdea());
        tLCCUWSubSchema.setOperator(tLCCUWMasterSchema.getOperator()); //操作员
        tLCCUWSubSchema.setManageCom(tLCCUWMasterSchema.getManageCom());
        tLCCUWSubSchema.setMakeDate(PubFun.getCurrentDate());
        tLCCUWSubSchema.setMakeTime(PubFun.getCurrentTime());
        tLCCUWSubSchema.setModifyDate(PubFun.getCurrentDate());
        tLCCUWSubSchema.setModifyTime(PubFun.getCurrentTime());

        mLCCUWSubSet.clear();
        mLCCUWSubSet.add(tLCCUWSubSchema);

        // 核保错误信息表
        LCCUWErrorSchema tLCCUWErrorSchema = new LCCUWErrorSchema();
        LCCUWErrorDB tLCCUWErrorDB = new LCCUWErrorDB();
        tLCCUWErrorDB.setContNo(mContNo);
        LCCUWErrorSet tLCCUWErrorSet = new LCCUWErrorSet();
        tLCCUWErrorSet = tLCCUWErrorDB.query();
        if (tLCCUWErrorDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCCUWErrorDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWAtuoChkAfterInitService";
            tError.functionName = "prepareContUW";
            tError.errorMessage = mContNo + "合同错误信息表查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        tLCCUWErrorSchema.setSerialNo("0");
        if (nUWNo > 0)
        {
            tLCCUWErrorSchema.setUWNo(nUWNo);
        }
        else
        {
            tLCCUWErrorSchema.setUWNo(1);
        }
        tLCCUWErrorSchema.setContNo(mContNo);
        tLCCUWErrorSchema.setGrpContNo(tLCCUWSubSchema.getGrpContNo());
        tLCCUWErrorSchema.setProposalContNo(tLCCUWSubSchema.getProposalContNo());
        tLCCUWErrorSchema.setInsuredNo(tLCCUWSubSchema.getInsuredNo());
        tLCCUWErrorSchema.setInsuredName(tLCCUWSubSchema.getInsuredName());
        tLCCUWErrorSchema.setAppntNo(tLCCUWSubSchema.getAppntNo());
        tLCCUWErrorSchema.setAppntName(tLCCUWSubSchema.getAppntName());
        tLCCUWErrorSchema.setManageCom(tLCCUWSubSchema.getManageCom());
        tLCCUWErrorSchema.setUWRuleCode(""); //核保规则编码
        tLCCUWErrorSchema.setUWError(""); //核保出错信息
        tLCCUWErrorSchema.setCurrValue(""); //当前值
        tLCCUWErrorSchema.setModifyDate(PubFun.getCurrentDate());
        tLCCUWErrorSchema.setModifyTime(PubFun.getCurrentTime());
        tLCCUWErrorSchema.setUWPassFlag(mPolPassFlag);

        //取核保错误信息
        mLCCUWErrorSet.clear();
        int merrcount = tLMUWSetContUnpass.size();
        if (merrcount > 0)
        {
            for (int i = 1; i <= merrcount; i++)
            {
                //取出错信息
                LMUWSchema tLMUWSchema = new LMUWSchema();
                tLMUWSchema = tLMUWSetContUnpass.get(i);
                //生成流水号
                String tserialno = "" + i;

                tLCCUWErrorSchema.setSerialNo(tserialno);
                tLCCUWErrorSchema.setUWRuleCode(tLMUWSchema.getUWCode()); //核保规则编码
                tLCCUWErrorSchema.setUWError(tLMUWSchema.getRemark().trim()); //核保出错信息，即核保规则的文字描述内容
                tLCCUWErrorSchema.setUWGrade(tLMUWSchema.getUWGrade());
                tLCCUWErrorSchema.setCurrValue(""); //当前值

                LCCUWErrorSchema ttLCCUWErrorSchema = new LCCUWErrorSchema();
                ttLCCUWErrorSchema.setSchema(tLCCUWErrorSchema);
                mLCCUWErrorSet.add(ttLCCUWErrorSchema);
            }
        }

        return true;
    }


    /**
     * 准备险种核保信息
     * 输出：如果发生错误则返回false,否则返回true
     * @param tLCPolSchema LCPolSchema
     * @param tLMUWSetUnpass LMUWSet
     * @return boolean
     */
    private boolean preparePolUW(LCPolSchema tLCPolSchema, LMUWSet tLMUWSetUnpass)
    {
        int tuwno = 0;
        LCUWMasterSchema tLCUWMasterSchema = new LCUWMasterSchema();
        LCUWMasterDB tLCUWMasterDB = new LCUWMasterDB();
        tLCUWMasterDB.setPolNo(mOldPolNo);
        LCUWMasterSet tLCUWMasterSet = new LCUWMasterSet();
        tLCUWMasterSet = tLCUWMasterDB.query();
        if (tLCUWMasterDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCUWMasterDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWAtuoChkBL";
            tError.functionName = "prepareUW";
            tError.errorMessage = mOldPolNo + "个人核保总表取数失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        int n = tLCUWMasterSet.size();
        if (n == 0)
        {
            tLCUWMasterSchema.setContNo(mContNo);
            tLCUWMasterSchema.setGrpContNo(tLCPolSchema.getGrpContNo());
            tLCUWMasterSchema.setPolNo(mOldPolNo);
            tLCUWMasterSchema.setProposalContNo(mPContNo);
            tLCUWMasterSchema.setProposalNo(tLCPolSchema.getProposalNo());
            tLCUWMasterSchema.setUWNo(1);
            tLCUWMasterSchema.setInsuredNo(tLCPolSchema.getInsuredNo());
            tLCUWMasterSchema.setInsuredName(tLCPolSchema.getInsuredName());
            tLCUWMasterSchema.setAppntNo(tLCPolSchema.getAppntNo());
            tLCUWMasterSchema.setAppntName(tLCPolSchema.getAppntName());
            tLCUWMasterSchema.setAgentCode(tLCPolSchema.getAgentCode());
            tLCUWMasterSchema.setAgentGroup(tLCPolSchema.getAgentGroup());
            tLCUWMasterSchema.setUWGrade(mUWGrade); //核保级别
            tLCUWMasterSchema.setAppGrade(mUWGrade); //申报级别
            tLCUWMasterSchema.setPostponeDay("");
            tLCUWMasterSchema.setPostponeDate("");
            tLCUWMasterSchema.setAutoUWFlag("1"); // 1 自动核保 2 人工核保
            tLCUWMasterSchema.setState(mPolPassFlag);
            tLCUWMasterSchema.setPassFlag(mPolPassFlag);
            tLCUWMasterSchema.setHealthFlag("0");
            tLCUWMasterSchema.setSpecFlag("0");
            tLCUWMasterSchema.setQuesFlag("0");
            tLCUWMasterSchema.setReportFlag("0");
            tLCUWMasterSchema.setChangePolFlag("0");
            tLCUWMasterSchema.setPrintFlag("0");
            tLCUWMasterSchema.setManageCom(tLCPolSchema.getManageCom());
            tLCUWMasterSchema.setUWIdea("");
            tLCUWMasterSchema.setUpReportContent("");
            tLCUWMasterSchema.setOperator(mOperator); //操作员
            tLCUWMasterSchema.setMakeDate(PubFun.getCurrentDate());
            tLCUWMasterSchema.setMakeTime(PubFun.getCurrentTime());
            tLCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
            tLCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());
        }
        else if (n == 1)
        {
            tLCUWMasterSchema = tLCUWMasterSet.get(1);

            tuwno = tLCUWMasterSchema.getUWNo();
            tuwno += 1;

            tLCUWMasterSchema.setUWNo(tuwno);
            tLCUWMasterSchema.setProposalContNo(mPContNo);
            tLCUWMasterSchema.setState(mPolPassFlag);
            tLCUWMasterSchema.setPassFlag(mPolPassFlag);
            tLCUWMasterSchema.setAutoUWFlag("1"); // 1 自动核保 2 人工核保
            tLCUWMasterSchema.setUWGrade(mUWGrade); //核保级别
            tLCUWMasterSchema.setAppGrade(mUWGrade); //申报级别
            tLCUWMasterSchema.setOperator(mOperator); //操作员
            tLCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
            tLCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());
        }
        else
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCUWMasterDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWAtuoChkBL";
            tError.functionName = "prepareUW";
            tError.errorMessage = mOldPolNo + "个人核保总表取数据不唯一!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCUWMasterSet.clear();
        mLCUWMasterSet.add(tLCUWMasterSchema);

        // 核保轨迹表
        LCUWSubSchema tLCUWSubSchema = new LCUWSubSchema();
        LCUWSubDB tLCUWSubDB = new LCUWSubDB();
        tLCUWSubDB.setPolNo(mOldPolNo);
        LCUWSubSet tLCUWSubSet = new LCUWSubSet();
        tLCUWSubSet = tLCUWSubDB.query();
        if (tLCUWSubDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCUWSubDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWAtuoChkBL";
            tError.functionName = "prepareUW";
            tError.errorMessage = mOldPolNo + "个人核保轨迹表查失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        int m = tLCUWSubSet.size();
        if (m > 0)
        {
            tLCUWSubSchema.setUWNo(++m); //第几次核保
        }
        else
        {
            tLCUWSubSchema.setUWNo(1); //第1次核保
        }

        tLCUWSubSchema.setContNo(mContNo);
        tLCUWSubSchema.setPolNo(mOldPolNo);
        tLCUWSubSchema.setGrpContNo(tLCUWMasterSchema.getGrpContNo());
        tLCUWSubSchema.setProposalContNo(tLCUWMasterSchema.getProposalContNo());
        tLCUWSubSchema.setProposalNo(tLCUWMasterSchema.getProposalNo());
        tLCUWSubSchema.setInsuredNo(tLCUWMasterSchema.getInsuredNo());
        tLCUWSubSchema.setInsuredName(tLCUWMasterSchema.getInsuredName());
        tLCUWSubSchema.setAppntNo(tLCUWMasterSchema.getAppntNo());
        tLCUWSubSchema.setAppntName(tLCUWMasterSchema.getAppntName());
        tLCUWSubSchema.setAgentCode(tLCUWMasterSchema.getAgentCode());
        tLCUWSubSchema.setAgentGroup(tLCUWMasterSchema.getAgentGroup());
        tLCUWSubSchema.setUWGrade(tLCUWMasterSchema.getUWGrade()); //核保级别
        tLCUWSubSchema.setAppGrade(tLCUWMasterSchema.getAppGrade()); //申请级别
        tLCUWSubSchema.setAutoUWFlag(tLCUWMasterSchema.getAutoUWFlag());
        tLCUWSubSchema.setState(tLCUWMasterSchema.getState());
        tLCUWSubSchema.setPassFlag(tLCUWMasterSchema.getState());
        tLCUWSubSchema.setPostponeDay(tLCUWMasterSchema.getPostponeDay());
        tLCUWSubSchema.setPostponeDate(tLCUWMasterSchema.getPostponeDate());
        tLCUWSubSchema.setUpReportContent(tLCUWMasterSchema.getUpReportContent());
        tLCUWSubSchema.setHealthFlag(tLCUWMasterSchema.getHealthFlag());
        tLCUWSubSchema.setSpecFlag(tLCUWMasterSchema.getSpecFlag());
        tLCUWSubSchema.setSpecReason(tLCUWMasterSchema.getSpecReason());
        tLCUWSubSchema.setQuesFlag(tLCUWMasterSchema.getQuesFlag());
        tLCUWSubSchema.setReportFlag(tLCUWMasterSchema.getReportFlag());
        tLCUWSubSchema.setChangePolFlag(tLCUWMasterSchema.getChangePolFlag());
        tLCUWSubSchema.setChangePolReason(tLCUWMasterSchema.getChangePolReason());
        tLCUWSubSchema.setAddPremReason(tLCUWMasterSchema.getAddPremReason());
        tLCUWSubSchema.setPrintFlag(tLCUWMasterSchema.getPrintFlag());
        tLCUWSubSchema.setPrintFlag2(tLCUWMasterSchema.getPrintFlag2());
        tLCUWSubSchema.setUWIdea(tLCUWMasterSchema.getUWIdea());
        tLCUWSubSchema.setOperator(tLCUWMasterSchema.getOperator()); //操作员
        tLCUWSubSchema.setManageCom(tLCUWMasterSchema.getManageCom());
        tLCUWSubSchema.setMakeDate(PubFun.getCurrentDate());
        tLCUWSubSchema.setMakeTime(PubFun.getCurrentTime());
        tLCUWSubSchema.setModifyDate(PubFun.getCurrentDate());
        tLCUWSubSchema.setModifyTime(PubFun.getCurrentTime());

        mLCUWSubSet.clear();
        mLCUWSubSet.add(tLCUWSubSchema);

        // 核保错误信息表
        LCUWErrorSchema tLCUWErrorSchema = new LCUWErrorSchema();
        LCUWErrorDB tLCUWErrorDB = new LCUWErrorDB();
        tLCUWErrorDB.setPolNo(mOldPolNo);
        LCUWErrorSet tLCUWErrorSet = new LCUWErrorSet();
        tLCUWErrorSet = tLCUWErrorDB.query();
        if (tLCUWErrorDB.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tLCUWErrorDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "UWAtuoChkBL";
            tError.functionName = "prepareUW";
            tError.errorMessage = mOldPolNo + "个人错误信息表查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        tLCUWErrorSchema.setSerialNo("0");
        if (m > 0)
        {
            tLCUWErrorSchema.setUWNo(m);
        }
        else
        {
            tLCUWErrorSchema.setUWNo(1);
        }
        tLCUWErrorSchema.setContNo(mContNo);
        tLCUWErrorSchema.setGrpContNo(tLCUWMasterSchema.getGrpContNo());
        tLCUWErrorSchema.setProposalContNo(mPContNo);
        tLCUWErrorSchema.setPolNo(mOldPolNo);
        tLCUWErrorSchema.setProposalNo(tLCPolSchema.getProposalNo());
        tLCUWErrorSchema.setInsuredNo(tLCPolSchema.getInsuredNo());
        tLCUWErrorSchema.setInsuredName(tLCPolSchema.getInsuredName());
        tLCUWErrorSchema.setAppntNo(tLCPolSchema.getAppntNo());
        tLCUWErrorSchema.setAppntName(tLCPolSchema.getAppntName());
        tLCUWErrorSchema.setManageCom(tLCPolSchema.getManageCom());
        tLCUWErrorSchema.setUWRuleCode(""); //核保规则编码
        tLCUWErrorSchema.setUWError(""); //核保出错信息
        tLCUWErrorSchema.setCurrValue(""); //当前值
        tLCUWErrorSchema.setModifyDate(PubFun.getCurrentDate());
        tLCUWErrorSchema.setModifyTime(PubFun.getCurrentTime());
        tLCUWErrorSchema.setUWPassFlag(mPolPassFlag);

        //取核保错误信息
        mLCUWErrorSet.clear();
        int merrcount = tLMUWSetUnpass.size();
        if (merrcount > 0)
        {
            for (int i = 1; i <= merrcount; i++)
            {
                //取出错信息
                LMUWSchema tLMUWSchema = new LMUWSchema();
                tLMUWSchema = tLMUWSetUnpass.get(i);
                //生成流水号
                String tserialno = "" + i;

                tLCUWErrorSchema.setSerialNo(tserialno);
                tLCUWErrorSchema.setUWRuleCode(tLMUWSchema.getUWCode()); //核保规则编码
                tLCUWErrorSchema.setUWError(tLMUWSchema.getRemark().trim()); //核保出错信息，即核保规则的文字描述内容
                tLCUWErrorSchema.setUWGrade(tLMUWSchema.getUWGrade());
                tLCUWErrorSchema.setCurrValue(""); //当前值

                LCUWErrorSchema ttLCUWErrorSchema = new LCUWErrorSchema();
                ttLCUWErrorSchema.setSchema(tLCUWErrorSchema);
                mLCUWErrorSet.add(ttLCUWErrorSchema);
            }
        }
        return true;
    }


    /**
     * 准备返回前台统一存储数据
     * 输出：如果发生错误则返回false,否则返回true
     * @param tLCContSchema LCContSchema
     * @return boolean
     */
    private boolean prepareOutputData(LCContSchema tLCContSchema)
    {

        mMap.put(tLCContSchema, "UPDATE");
//          if(mLCCUWMasterSet.size() ==1)
        mMap.put(mLCCUWMasterSet.get(1), "DELETE&INSERT");
        mMap.put(mLCCUWSubSet, "INSERT");
        mMap.put(mLCCUWErrorSet, "INSERT");

        mMap.put(mAllLCPolSet, "UPDATE");
        int n = mAllLCUWMasterSet.size();
        for (int i = 1; i <= n; i++)
        {
            LCUWMasterSchema tLCUWMasterSchema = mAllLCUWMasterSet.get(i);
            mMap.put(tLCUWMasterSchema, "DELETE&INSERT");
        }
        mMap.put(mAllLCUWSubSet, "INSERT");
        mMap.put(mAllLCErrSet, "INSERT");

        return false;
    }


    /**
     * 校验业务数据
     * @return boolean
     */
    private boolean checkData()
    {
        //校验核保级别
        if (!checkUWGrade())
        {
            return false;
        }

        //校验是否复核
        if (!checkApprove(mLCContSchema))
        {
            return false;
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
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintSendNoticeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得操作员编码
        mOperator = mGlobalInput.Operator;
        if (mOperator == null || mOperator.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintSendNoticeAfterInitService";
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
            tError.moduleName = "PEdorUWSendNoticeAfterInitService";
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
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintSendNoticeAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCContSchema = (LCContSchema) mTransferData.getValueByName("LCContSchema"); //从输入数据中获取合同记录的数据
        if (mLCContSchema == null)
        {
            CError tError = new CError();
            tError.moduleName = "UWAutoChkBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "合同号信息未查询到!";
            this.mErrors.addOneError(tError);
            System.out.println("NO LCCONTSCHEMA");
            return false;
        }
//        System.out.println(mLCContSchema.getContNo());
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mLCContSchema.getContNo());
        if (tLCContDB.getInfo()) //验证LCCont表中是否存在该合同项记录
        {
            mLCContSchema.setSchema(tLCContDB.getSchema());
        }
        else
        {
            CError tError = new CError();
            tError.moduleName = "UWAutoChkBL";
            tError.functionName = "getInputData";
            tError.errorMessage = "合同号为" + mLCContSchema.getContNo() + "未查询到!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }


    /**
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
     * @return boolean
     */
    private boolean prepareTransferData()
    {
        mTransferData.setNameAndValue("ContNo", mLCContSchema.getContNo());
        mTransferData.setNameAndValue("PrtNo", mLCContSchema.getPrtNo());
        mTransferData.setNameAndValue("AppntNo", mLCContSchema.getAppntNo());
        mTransferData.setNameAndValue("AppntName", mLCContSchema.getAppntName());
        mTransferData.setNameAndValue("ProposalContNo", mLCContSchema.getProposalContNo());
        mTransferData.setNameAndValue("AgentCode", mLCContSchema.getAgentCode());
        LAAgentDB tLAAgentDB = new LAAgentDB();
//        LAAgentSet tLAAgentSet = new LAAgentSet();
        tLAAgentDB.setAgentCode(mLCContSchema.getAgentCode());
        LAAgentSet tLAAgentSet = tLAAgentDB.query();
        if (tLAAgentSet == null || tLAAgentSet.size() != 1)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "UWAutoChkAfterInitService";
            tError.functionName = "prepareTransferData";
            tError.errorMessage = "代理人表LAAgent查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mTransferData.setNameAndValue("AgentName", tLAAgentSet.get(1).getName());
        mTransferData.setNameAndValue("ManageCom", mLCContSchema.getManageCom());
        mTransferData.setNameAndValue("UWFlag", mPolPassFlag);
        mTransferData.setNameAndValue("UWDate", PubFun.getCurrentDate());
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

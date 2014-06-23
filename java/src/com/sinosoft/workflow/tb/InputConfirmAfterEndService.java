/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.LCContDB;
import com.sinosoft.lis.db.LMCheckFieldDB;
import com.sinosoft.lis.pubfun.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.LCIssuePolSet;
import com.sinosoft.lis.vschema.LDSpotTrackSet;
import com.sinosoft.lis.vschema.LCPolSet;
import com.sinosoft.lis.vschema.LMCheckFieldSet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterEndService;


/**
 * <p>Title: 工作流服务类:个人新契约录入完毕 </p>
 * <p>Description: 进行录入完毕后的校验 </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class InputConfirmAfterEndService implements AfterEndService
{

    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();


    /** 往界面传输数据的容器 */
    private VData mResult = new VData();


    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();
    /** 往后面传输数据的容器 */
    private VData mInputData;

    /** 业务处理类 */
    private LCContSchema mLCContSchema = new LCContSchema();
    private LCPolSet mLCPolSet = new LCPolSet();
//    private LMCheckFieldSet mLMCheckFieldSet = new LMCheckFieldSet();
    private LCIssuePolSet mLCIssuePolSet = new LCIssuePolSet();
    private LDSpotTrackSet mLDSpotTrackSet = new LDSpotTrackSet();
    /** 数据操作字符串 */
    private String mOperater;
    private String mManageCom;
    private String mMissionID;


    /** 业务数据字段 */
    private String mContNo;

    public InputConfirmAfterEndService()
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
         mInputData = (VData) cInputData.clone();
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
        MMap map = new MMap();
        //map.put(mLCIssuePolSet, "INSERT");
        mResult.add(map);


        return true;
    }


    /**
     * 校验业务数据
     * @return
     */
    private boolean checkData()
    {
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mContNo);
        if (!tLCContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "InputConfirmAfterEndService";
            tError.functionName = "checkData";
            tError.errorMessage = "合同保单信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema = tLCContDB.getSchema();

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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "InputConfirmAfterEndService";
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
            tError.moduleName = "InputConfirmAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据Operate失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得管理机构编码
        mManageCom = mGlobalInput.ManageCom;
        if (mManageCom == null || mManageCom.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "InputConfirmAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据mManageCom失败!";
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
            tError.moduleName = "InputConfirmAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的任务ContNo
        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mContNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "InputConfirmAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据ContNo失败!";
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
        String tCalCode = "";
        String tCheckResult = "";
        LMCheckFieldSet tLMCheckFieldSet = null;
        //进行合同级的校验
        tLMCheckFieldSet = getCheckFieldSetCont();
        if (tLMCheckFieldSet == null || tLMCheckFieldSet.size() <= 0)
        {
        }
        else
        {
            for (int i = 1; i <= tLMCheckFieldSet.size(); i++)
            {
                tCalCode = tLMCheckFieldSet.get(i).getCalCode(); //获得计算规则所对应的calcode
                tCheckResult = CheckCont(tCalCode); //合同校验
                if (!tCheckResult.equals("") || tCheckResult.length() > 0)
                {
                    //准备将要提交的问题件数据
                    if (!prepareContIssue(tLMCheckFieldSet.get(i)))
                    {
                        CError tError = new CError();
                        tError.moduleName = "InputConfirmAfterEndService";
                        tError.functionName = "getInputData";
                        tError.errorMessage = "准备问题件数据失败!";
                        this.mErrors.addOneError(tError);
                        return false;
                    }
                }
            }
        }

        //险种级的校验
        for (int i = 1; i <= mLCPolSet.size(); i++)
        {
            if (tLMCheckFieldSet != null)
            {
                tLMCheckFieldSet.clear();
            }
            //取得合同下的校验规则
            tLMCheckFieldSet = getCheckFieldSetPol(mLCPolSet.get(i));
            int n = tLMCheckFieldSet.size();
            if (n == 0)
            {}
            else
            {
                for (int j = 1; j <= n; j++)
                {
                    tCalCode = tLMCheckFieldSet.get(j).getCalCode();
                    tCheckResult = CheckPol(mLCPolSet.get(i), tCalCode);
                    if (!tCheckResult.equals("") || tCheckResult.length() > 0)
                    {
                        //准备将要提交的问题件数据
                        if (!preparePolIssue(mLCPolSet.get(i), tLMCheckFieldSet.get(i)))
                        {
                            CError tError = new CError();
                            tError.moduleName = "InputConfirmAfterEndService";
                            tError.functionName = "getInputData";
                            tError.errorMessage = "准备问题件数据失败!";
                            this.mErrors.addOneError(tError);
                            return false;
                        }
                    }

                }
            }
        }
        return true;
    }


    /**
     * preparePolIssue
     *
     * @param lCPolSchema LCPolSchema
     * @param lMCheckFieldSchema LMCheckFieldSchema
     * @return boolean
     */
    private boolean preparePolIssue(LCPolSchema tlCPolSchema,
            LMCheckFieldSchema tlMCheckFieldSchema)
    {
        LCIssuePolSchema tLCIssuePolSchema = new LCIssuePolSchema();

        tLCIssuePolSchema.setGrpContNo(tlCPolSchema.getGrpContNo());
        tLCIssuePolSchema.setContNo(tlCPolSchema.getContNo());
        tLCIssuePolSchema.setProposalContNo(tlCPolSchema.getContNo());
//        tLCIssuePolSchema.setPrtSeq("");
        tLCIssuePolSchema.setSerialNo(PubFun1.CreateMaxNo("QustSerlNo", 20));
        tLCIssuePolSchema.setFieldName(tlMCheckFieldSchema.getFieldName());
        tLCIssuePolSchema.setLocation(tlMCheckFieldSchema.getLocation());
        tLCIssuePolSchema.setIssueType("9999999999"); //暂时存为十个“9”
        tLCIssuePolSchema.setOperatePos("0");
        tLCIssuePolSchema.setBackObjType("5");
//        tLCIssuePolSchema.setBackObj("");
        tLCIssuePolSchema.setIsueManageCom(mManageCom);
        tLCIssuePolSchema.setIssueCont(tlMCheckFieldSchema.getMsg());
        tLCIssuePolSchema.setPrintCount(0);
//        tLCIssuePolSchema.setNeedPrint("");
//        tLCIssuePolSchema.setReplyMan("");
//        tLCIssuePolSchema.setReplyResult("");
        tLCIssuePolSchema.setState("0");
        tLCIssuePolSchema.setOperator(tlCPolSchema.getOperator());
        tLCIssuePolSchema.setManageCom(tlCPolSchema.getManageCom());
        tLCIssuePolSchema.setMakeDate(PubFun.getCurrentDate());
        tLCIssuePolSchema.setMakeTime(PubFun.getCurrentTime());
        tLCIssuePolSchema.setModifyDate(PubFun.getCurrentDate());
        tLCIssuePolSchema.setModifyTime(PubFun.getCurrentTime());

        mLCIssuePolSet.add(tLCIssuePolSchema);
        return true;
    }


    /**
     * prepareIssue
     *
     * @param lMCheckFieldSchema LMCheckFieldSchema
     * @return boolean
     */
    private boolean prepareContIssue(LMCheckFieldSchema tlMCheckFieldSchema)
    {
        LCIssuePolSchema tLCIssuePolSchema = new LCIssuePolSchema();

        tLCIssuePolSchema.setGrpContNo(mLCContSchema.getContNo());
        tLCIssuePolSchema.setContNo(mLCContSchema.getContNo());
        tLCIssuePolSchema.setProposalContNo(mLCContSchema.getContNo());
//        tLCIssuePolSchema.setPrtSeq("");
        tLCIssuePolSchema.setSerialNo(PubFun1.CreateMaxNo("QustSerlNo", 20));
        tLCIssuePolSchema.setFieldName(tlMCheckFieldSchema.getFieldName());
        tLCIssuePolSchema.setLocation(tlMCheckFieldSchema.getLocation());
        tLCIssuePolSchema.setIssueType("9999999999"); //暂时存为十个“9”
        tLCIssuePolSchema.setOperatePos("0");
        tLCIssuePolSchema.setBackObjType("5");
//        tLCIssuePolSchema.setBackObj("");
        tLCIssuePolSchema.setIsueManageCom(mManageCom);
        tLCIssuePolSchema.setIssueCont(tlMCheckFieldSchema.getMsg());
        tLCIssuePolSchema.setPrintCount(0);
//        tLCIssuePolSchema.setNeedPrint("");
//        tLCIssuePolSchema.setReplyMan("");
//        tLCIssuePolSchema.setReplyResult("");
        tLCIssuePolSchema.setState("0");
        tLCIssuePolSchema.setOperator(mOperater);
        tLCIssuePolSchema.setManageCom(mManageCom);
        tLCIssuePolSchema.setMakeDate(PubFun.getCurrentDate());
        tLCIssuePolSchema.setMakeTime(PubFun.getCurrentTime());
        tLCIssuePolSchema.setModifyDate(PubFun.getCurrentDate());
        tLCIssuePolSchema.setModifyTime(PubFun.getCurrentTime());

        mLCIssuePolSet.add(tLCIssuePolSchema);

        return true;
    }


    /**
     * CheckCont
     *
     * @param tCalCode String
     * @return String
     */
    private String CheckCont(String tCalCode)
    {
        String tCheckResult = "";
        Calculator mCalculator = new Calculator();
        mCalculator.setCalCode(tCalCode);

        mCalculator.addBasicFactor("ContNo", mLCContSchema.getContNo());

        String tStr = "";
        tStr = mCalculator.calculate();
        if (tStr.trim().equals(""))
        {
            tCheckResult = "";
        }
        else
        {
            tCheckResult = tStr.trim();
        }

        return tCheckResult;
    }


    /**
     * CheckPol
     *
     * @param lCPolSchema LCPolSchema
     * @param tCalCode String
     * @return String
     */
    private static String CheckPol(LCPolSchema tlCPolSchema, String tCalCode)
    {
        String tCheckResult = "";
        Calculator mCalculator = new Calculator();
        mCalculator.setCalCode(tCalCode);

        mCalculator.addBasicFactor("PolNo", tlCPolSchema.getPolNo());

        String tStr = "";
        tStr = mCalculator.calculate();
        if (tStr.trim().equals(""))
        {
            tCheckResult = "";
        }
        else
        {
            tCheckResult = tStr.trim();
        }

        return tCheckResult;
    }


    /**
     * getCheckFieldSet
     *
     * @return LMCheckFieldSet
     */
    private LMCheckFieldSet getCheckFieldSetCont()
    {
        LMCheckFieldSet tLMCheckFieldSet = new LMCheckFieldSet();
        LMCheckFieldDB tLMCheckFieldDB = new LMCheckFieldDB();
        //个单合同自动复核规则
        String tSql =
                "select * from lmcheckfield where riskcode = '000000' and pagelocation = 'TBINPUT#TBTYPEIC' ";
        System.out.println(tSql);
        tLMCheckFieldSet = tLMCheckFieldDB.executeQuery(tSql);
        if (tLMCheckFieldSet == null)
        {
            CError tError = new CError();
            tError.moduleName = "InputConfirmAfterEndService";
            tError.functionName = "getCheckFieldSet";
            tError.errorMessage = "查询算法描述表失败!";
            this.mErrors.addOneError(tError);
            return null;
        }
        return tLMCheckFieldSet;
    }


    /**
     * getCheckFieldSet
     *
     * @return LMCheckFieldSet
     */
    private LMCheckFieldSet getCheckFieldSetPol(LCPolSchema tLCPolSchema)
    {
        LMCheckFieldSet tLMCheckFieldSet = new LMCheckFieldSet();
        LMCheckFieldDB tLMCheckFieldDB = new LMCheckFieldDB();
        //个单合同自动复核规则
        String tSql =
                "select * from lmcheckfield where 1=1 and (riskcode = '000000' or riskcode = '"
                + tLCPolSchema.getRiskCode() + "' and pagelocation = 'TBINPUT#TBTYPEI' ";

        tLMCheckFieldSet = tLMCheckFieldDB.executeQuery(tSql);
        if (tLMCheckFieldSet == null || tLMCheckFieldSet.size() <= 0)
        {
            CError tError = new CError();
            tError.moduleName = "InputConfirmAfterEndService";
            tError.functionName = "getCheckFieldSet";
            tError.errorMessage = "查询算法描述表失败!";
            this.mErrors.addOneError(tError);
            return null;
        }
        return tLMCheckFieldSet;
    }

    /**
     * 为公共传输数据集合中添加工作流下一节点属性字段数据,准备条件
     * @return
     */
    private static boolean prepareTransferData()
    {
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

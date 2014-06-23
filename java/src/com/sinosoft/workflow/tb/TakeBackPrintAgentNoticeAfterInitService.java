/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.f1print.PrintManagerBL;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.LOPRTManagerSet;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.vschema.LZSysCertifySet;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title:工作流节点任务: </p>
 * <p>Description:续保人工业务员业务员通知书回收服务类 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: SinoSoft </p>
 * @author HYQ
 * @version 1.0
 */

public class TakeBackPrintAgentNoticeAfterInitService implements
        AfterInitService
{

    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();

    /** 往界面传输数据的容器 */
    private VData mResult = new VData();

    /** 往工作流引擎中传输数据的容器 */
    private GlobalInput mGlobalInput = new GlobalInput();

    private TransferData mTransferData = new TransferData();


    /** 工作流任务节点表*/
    private LWMissionSchema mLWMissionSchema = new LWMissionSchema();
    private LWMissionSet mLWMissionSet = new LWMissionSet();
    /** 问题件表 */
    private LCIssuePolSet tLCIssuePolSet = new LCIssuePolSet();
    /** 数据操作字符串 */
    private String mOperater;
    private String mManageCom;

    /** 业务数据操作字符串 */
    private String mContNo;
    private String mMissionID;
    private String mSubMissionID;
    private String mCertifyNo;
    private String mCertifyCode;
    private boolean mPatchFlag;
    private String mTakeBackOperator;
    private String mTakeBackMakeDate;
    private String mOldPrtSeq; //如果该单证是补打单证,则同时将遗失原单证也回收.反之如果回收原单证,但其已补发过,则同时也要把补发的单证回收掉

    /**保单表*/
    private LCContSchema mLCContSchema = new LCContSchema();

    /** 续保业务员主表 */
    private LCCUWMasterSchema mLCCUWMasterSchema = new LCCUWMasterSchema();

    /** 打印管理表 */
    private LOPRTManagerSchema mLOPRTManagerSchema = new LOPRTManagerSchema();
    private LOPRTManagerSet mLOPRTManagerSet = new LOPRTManagerSet(); //如果该单证是补打单证,则同时将遗失原单证也回收.反之如果回收原单证,但其已补发过,则同时也要把补发的单证回收掉

    private LZSysCertifySet mLZSysCertifySet = new LZSysCertifySet();
    private String mQueModFlag = "0";

    public TakeBackPrintAgentNoticeAfterInitService()
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
        if (!prepareTransferData())
        {
            return false;
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

        //添加业务员通知书打印管理表数据
        if (mLOPRTManagerSet != null && mLOPRTManagerSet.size() > 0)
        {
            map.put(mLOPRTManagerSet, "UPDATE");
        }

        //添加续保批单业务员主表数据
        if (mLCCUWMasterSchema != null)
        {
            map.put(mLCCUWMasterSchema, "UPDATE");
        }
        //添加体检表数据
        if (tLCIssuePolSet != null && tLCIssuePolSet.size()>0)
        {
            map.put(tLCIssuePolSet, "UPDATE");
        }

        //添加续保体检通知书自动发放表数据
        if (mLZSysCertifySet != null && mLZSysCertifySet.size() > 0)
        {
            map.put(mLZSysCertifySet, "UPDATE");
        }

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
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mContNo);
        if (!tLCContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mContNo + "信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLCContSchema.setSchema(tLCContDB);

        //校验续保批单业务员主表
        //校验保单信息
        LCCUWMasterDB tLCCUWMasterDB = new LCCUWMasterDB();
        tLCCUWMasterDB.setContNo(mContNo);
        if (!tLCCUWMasterDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "PrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mContNo + "续保批单业务员主表信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCCUWMasterSchema.setSchema(tLCCUWMasterDB);

        // 处于未打印状态的业务员通知书在打印队列中只能有一个
        // 条件：同一个单据类型，同一个其它号码，同一个其它号码类型
        LOPRTManagerDB tLOPRTManagerDB = new LOPRTManagerDB();

        tLOPRTManagerDB.setCode(PrintManagerBL.CODE_AGEN_QUEST); //
        tLOPRTManagerDB.setPrtSeq(mCertifyNo);
        tLOPRTManagerDB.setOtherNo(mContNo);
        tLOPRTManagerDB.setOtherNoType(PrintManagerBL.ONT_CONT); //保单号
        tLOPRTManagerDB.setStateFlag("1");

        LOPRTManagerSet tLOPRTManagerSet = tLOPRTManagerDB.query();
        if (tLOPRTManagerSet == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "查询打印管理表信息出错!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (tLOPRTManagerSet.size() != 1)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "PrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "preparePrint";
            tError.errorMessage = "在打印队列中没有处于已打印待回收状态的业务员通知书!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLOPRTManagerSchema = tLOPRTManagerSet.get(1);
        if (mLOPRTManagerSchema.getPatchFlag() == null)
        {
            mPatchFlag = false;
        }
        else if (mLOPRTManagerSchema.getPatchFlag().equals("0"))
        {
            mPatchFlag = false;
        }
        else if (mLOPRTManagerSchema.getPatchFlag().equals("1"))
        {
            mPatchFlag = true;
        }

        //如果该单证是补打单证,则同时将遗失原单证也回收.反之如果回收原单证,但其已补发过,则同时也要把补发的单证回收掉
        if (mPatchFlag)
        {
            LOPRTManagerDB tempLOPRTManagerDB = new LOPRTManagerDB();
            mOldPrtSeq = mLOPRTManagerSchema.getOldPrtSeq();
            String tStr = "select * from LOPRTManager where (PrtSeq = '" + mOldPrtSeq
                    + "' or OldPrtSeq = '" + mOldPrtSeq + "')";
            LOPRTManagerSet tempLOPRTManagerSet = tempLOPRTManagerDB.executeQuery(tStr);
            if (tempLOPRTManagerSet.size() == 1)
            {
                // @@错误处理
                CError tError = new CError();
                tError.moduleName = "PrintTakeBackAutoHealthAfterInitService";
                tError.functionName = "preparePrint";
                tError.errorMessage = "查询在打印队列中没有该补打业务员通知书的原通知书记录信息出错!";
                this.mErrors.addOneError(tError);
                return false;
            }

            for (int i = 1; i <= tempLOPRTManagerSet.size(); i++)
            {
                mLOPRTManagerSet.add(tempLOPRTManagerSet.get(i));
            }
        }
        else
        {
            LOPRTManagerDB tempLOPRTManagerDB = new LOPRTManagerDB();
            mOldPrtSeq = mLOPRTManagerSchema.getPrtSeq();
            if (mOldPrtSeq != null && !mOldPrtSeq.equals(""))
            {
                tempLOPRTManagerDB.setOldPrtSeq(mOldPrtSeq);
                LOPRTManagerSet tempLOPRTManagerSet = tempLOPRTManagerDB.query();
                if (tempLOPRTManagerSet != null &&
                        tempLOPRTManagerSet.size() > 0)
                {
                    for (int i = 1; i <= tempLOPRTManagerSet.size(); i++)
                    {
                        mLOPRTManagerSet.add(tempLOPRTManagerSet.get(i));
                    }
                }
            }
        }

        //查询系统单证回收队列表
        for (int i = 1; i <= mLOPRTManagerSet.size(); i++)
        {
            if (mLOPRTManagerSet.get(i).getStateFlag() != null &&
                    mLOPRTManagerSet.get(i).getStateFlag().trim().equals("1"))
            {
                LZSysCertifyDB tLZSysCertifyDB = new LZSysCertifyDB();
                LZSysCertifySet tLZSysCertifySet = new LZSysCertifySet();
                tLZSysCertifyDB.setCertifyCode("106601"); //业务员通知书标识
                tLZSysCertifyDB.setCertifyNo(mLOPRTManagerSet.get(i).getPrtSeq());
                tLZSysCertifySet = tLZSysCertifyDB.query();
                if (tLZSysCertifySet == null || tLZSysCertifySet.size() != 1)
                {
                    // @@错误处理
                    CError tError = new CError();
                    tError.moduleName =
                            "PrintTakeBackAutoHealthAfterInitService";
                    tError.functionName = "preparePrint";
                    tError.errorMessage =
                            "回收业务员通知书时,LZSysCertifySchema表信息查询失败!";
                    this.mErrors.addOneError(tError);
                    return false;
                }
                mLZSysCertifySet.add(tLZSysCertifySet.get(1));
            }
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
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PrintTakeBackAutoHealthAfterInitService";
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
            tError.moduleName = "PrintTakeBackAutoHealthAfterInitService";
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
            tError.moduleName = "PrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得业务数据
        if (mTransferData == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mContNo = (String) mTransferData.getValueByName("ContNo");
        if (mContNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中ContNo失败!";
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
            tError.moduleName = "PrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的子任务ID
        mSubMissionID = (String) mTransferData.getValueByName("SubMissionID");
        if (mSubMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PrintTakeBackSendNoticeAfterEndService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中SubMissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得业务处理数据
        mCertifyNo = (String) mTransferData.getValueByName("CertifyNo");
        if (mCertifyNo == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得业务处理数据
        mCertifyCode = (String) mTransferData.getValueByName("CertifyCode");
        if (mCertifyCode == null || mCertifyCode.trim().equals(""))
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得业务处理数据
        LZSysCertifySchema tLZSysCertifySchema = new LZSysCertifySchema();
        tLZSysCertifySchema = (LZSysCertifySchema) mTransferData.getValueByName(
                "LZSysCertifySchema");
        if (tLZSysCertifySchema == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中LZSysCertifySchema失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mTakeBackOperator = tLZSysCertifySchema.getTakeBackOperator();
        if (mTakeBackOperator == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中TakeBackOperator失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mTakeBackMakeDate = tLZSysCertifySchema.getTakeBackMakeDate();
        if (mTakeBackMakeDate == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCContDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PrintTakeBackAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中TakeBackMakeDat失败!";
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
        // 业务员生调信息
        if (!prepareSendNotice())
        {
            return false;
        }
        // 问题件信息
        if (prepareAutoIssue() == false)
            return false;

        //打印队列
        if (!preparePrint())
        {
            return false;
        }

        //发放系统单证打印队列
        if (!prepareAutoSysCertSendOut())
        {
            return false;
        }
        //选择工作流activity
        if (!chooseActivity())
        {
            return false;
        }

        return true;
    }

    /**
     * 准备业务员资料信息
     * 输出：如果发生错误则返回false,否则返回true
     */
    private boolean prepareSendNotice()
    {
        ////准备业务员主表信息
        mLCCUWMasterSchema.setModifyDate(PubFun.getCurrentDate());
        mLCCUWMasterSchema.setModifyTime(PubFun.getCurrentTime());
        mLCCUWMasterSchema.setPrintFlag2("3"); //回收业务员通知书
        mLCCUWMasterSchema.setSpecFlag("2");

        return true;
    }
    //问题件信息处理
    private boolean prepareAutoIssue()
    {
      LCIssuePolDB tLCIssuePolDB = new LCIssuePolDB();
      tLCIssuePolDB.setPrtSeq(mLOPRTManagerSet.get(1).getPrtSeq());
      tLCIssuePolDB.setState("2");
      tLCIssuePolDB.setBackObjType("2");
      tLCIssuePolSet = tLCIssuePolDB.query();
      for(int i=1;i<=tLCIssuePolSet.size();i++)
      {
        tLCIssuePolSet.get(i).setState("3");
      }
      return true;
    }


    /**
     * 准备打印信息表
     * @return
     */
    private boolean preparePrint()
    {
        //准备打印管理表数据
        for (int i = 1; i <= mLOPRTManagerSet.size(); i++)
        {
            mLOPRTManagerSet.get(i).setStateFlag("2");
        }
        return true;
    }


    /**
     * 选择工作流流转
     * @return
     */
    private boolean chooseActivity()
    {
//        String tStr = "Select * from LWMission where MissionID = '" +
//                mMissionID + "' and ActivityID = '0000001112' and SubMissionID <> '"
//                + mSubMissionID + "' and MissionProp14 = '" +
//                mLWMissionSchema.getMissionProp14()
//                + "' union Select * from LWMission where MissionID = '" +
//                mMissionID + "' and ActivityID = '0000001115' and MissionProp14 = '" +
//                mLWMissionSchema.getMissionProp14()
//                + "' union Select * from LWMission where MissionID = '" +
//                mMissionID + "' and ActivityID = '0000001107' and MissionProp14 = '" +
//                mLWMissionSchema.getMissionProp14()
//                + "' union Select * from LWMission where MissionID = '" +
//                mMissionID + "' and ActivityID = '0000001017' and MissionProp14 = '" +
//                mLWMissionSchema.getMissionProp14()
//                + "' union Select * from LWMission where MissionID = '" +
//                mMissionID + "' and ActivityID = '0000001018' and MissionProp14 = '" +
//                mLWMissionSchema.getMissionProp14()
//                + "' union Select * from LWMission where MissionID = '" +
//                mMissionID + "' and ActivityID = '0000001019' and MissionProp14 = '" +
//                mLWMissionSchema.getMissionProp14()
//                + "' union Select * from LWMission where MissionID = '" +
//                mMissionID + "' and ActivityID = '0000001020' and MissionProp14 = '" +
//                mLWMissionSchema.getMissionProp14() + "'";

        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("Select * from LWMission where MissionID = '");
        tSBql.append(mMissionID);
        tSBql.append("' and ActivityID = '0000001112' and SubMissionID <> '");
        tSBql.append(mSubMissionID);
        tSBql.append("' and MissionProp14 = '");
        tSBql.append(mLWMissionSchema.getMissionProp14());
        tSBql.append("' union Select * from LWMission where MissionID = '");
        tSBql.append(mMissionID);
        tSBql.append("' and ActivityID = '0000001115' and MissionProp14 = '");
        tSBql.append(mLWMissionSchema.getMissionProp14());
        tSBql.append("' union Select * from LWMission where MissionID = '");
        tSBql.append(mMissionID);
        tSBql.append("' and ActivityID = '0000001107' and MissionProp14 = '");
        tSBql.append(mLWMissionSchema.getMissionProp14());
        tSBql.append("' union Select * from LWMission where MissionID = '");
        tSBql.append(mMissionID);
        tSBql.append("' and ActivityID = '0000001017' and MissionProp14 = '");
        tSBql.append(mLWMissionSchema.getMissionProp14());
        tSBql.append("' union Select * from LWMission where MissionID = '");
        tSBql.append(mMissionID);
        tSBql.append("' and ActivityID = '0000001018' and MissionProp14 = '");
        tSBql.append(mLWMissionSchema.getMissionProp14());
        tSBql.append("' union Select * from LWMission where MissionID = '");
        tSBql.append(mMissionID);
        tSBql.append("' and ActivityID = '0000001019' and MissionProp14 = '");
        tSBql.append(mLWMissionSchema.getMissionProp14());
        tSBql.append("' union Select * from LWMission where MissionID = '");
        tSBql.append(mMissionID);
        tSBql.append("' and ActivityID = '0000001020' and MissionProp14 = '");
        tSBql.append(mLWMissionSchema.getMissionProp14());
        tSBql.append("'");

        LWMissionDB tLWMissionDB = new LWMissionDB();
        mLWMissionSet = tLWMissionDB.executeQuery(tSBql.toString());
        int i = mLWMissionSet.size();
//        tStr = "select count(1) from lwmission where MissionID = '" + mMissionID
//                + "' and activityid in ('0000001017','0000001018','0000001019')";
        tSBql = new StringBuffer(128);
        tSBql.append("select count(1) from lwmission where MissionID = '");
        tSBql.append(mMissionID);
        tSBql.append("' and activityid in ('0000001017','0000001018','0000001019')");

        String tReSult = new String();
        ExeSQL tExeSQL = new ExeSQL();
        tReSult = tExeSQL.getOneValue(tSBql.toString());
        if (tExeSQL.mErrors.needDealError())
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tExeSQL.mErrors);
            CError tError = new CError();
            tError.moduleName = "PrintTakeBackRReportAfterEndService";
            tError.functionName = "prepareMission";
            tError.errorMessage = "执行SQL语句：" + tSBql.toString() + "失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (tReSult == null || tReSult.equals(""))
        {
            return false;
        }
        int tCount = 0;
        tCount = Integer.parseInt(tReSult); //已包括了本次节点及相关同步节点

        if (i > tCount - 1)
        {
            mQueModFlag = "0";
        }
        else
        {
            mQueModFlag = "1";
        }

        return true;
    }


    /**
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
     * @return
     */
    private boolean prepareTransferData()
    {
        mTransferData.setNameAndValue("QueModFlag", mQueModFlag);
        mTransferData.setNameAndValue("PrtNo", mLCContSchema.getPrtNo());
        mTransferData.setNameAndValue("AppntNo", mLCContSchema.getAppntNo());
        mTransferData.setNameAndValue("AppntName", mLCContSchema.getAppntName());
        return true;
    }

    /**
     * 准备业务员资料信息
     * 输出：如果发生错误则返回false,否则返回true
     */
    private boolean prepareAutoSysCertSendOut()
    {
        //准备单证回收管理表数据
        for (int i = 1; i <= mLZSysCertifySet.size(); i++)
        {
            mLZSysCertifySet.get(i).setTakeBackMakeDate(PubFun.getCurrentDate());
            mLZSysCertifySet.get(i).setTakeBackMakeTime(PubFun.getCurrentTime());
            mLZSysCertifySet.get(i).setModifyDate(PubFun.getCurrentDate());
            mLZSysCertifySet.get(i).setModifyTime(PubFun.getCurrentTime());
            mLZSysCertifySet.get(i).setTakeBackOperator(mTakeBackOperator);
            mLZSysCertifySet.get(i).setStateFlag("1");
        }
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

/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.tb;

import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.schema.LCCUWMasterSchema;
import com.sinosoft.lis.schema.LCContSchema;
import com.sinosoft.lis.schema.LWMissionSchema;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: 工作流服务类:新契约机构问题件回复</p>
 * <p>Description: 机构问题件回复AfterInit服务类</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SinoSoft</p>
 * @author HYQ
 * @version 1.0
 */

public class AgencyQueBackAfterInitService implements AfterInitService
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
    private LCIssuePolSet mLCIssuePolSet = new LCIssuePolSet();

    /** 数据操作字符串 */
    private String mOperater;
    private String mManageCom;

    /** 业务数据操作字符串 */
    private String mAgencyQueFlag = "0";
    private String mContNo;
    private String mMissionID;
    private String mSubMissionID;

    /**保单表*/
    private LCContSchema mLCContSchema = new LCContSchema();

    /** 续保业务员主表 */
    private LCCUWMasterSchema mLCCUWMasterSchema = new LCCUWMasterSchema();

    /** 打印管理表 */
    private LOPRTManagerSet mLOPRTManagerSet = new LOPRTManagerSet(); //如果该单证是补打单证,则同时将遗失原单证也回收.反之如果回收原单证,但其已补发过,则同时也要把补发的单证回收掉
    private LZSysCertifySet mLZSysCertifySet = new LZSysCertifySet();
    private String mQueModFlag = "0";

    public AgencyQueBackAfterInitService()
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
     * @return boolean
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
     * @return boolean
     */
    private boolean checkData()
    {
        //校验是否已回收所有机构问题件
        String tStr = "select * from lcissuepol where ContNo = '" + mContNo
                + "' and backobjtype = '4' and replyman is null";
//        System.out.println("tStr==" + tStr);
        LCIssuePolDB tLCIssuePolDB = new LCIssuePolDB();
        LCIssuePolSet tLCIssuePolSet = new LCIssuePolSet();
        tLCIssuePolSet = tLCIssuePolDB.executeQuery(tStr);
        if (tLCIssuePolSet.size() > 0)
        {
            CError tError = new CError();
            tError.moduleName = "AgencyQueBackAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "存在未回复的机构问题件,请回复!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //校验保单信息
        LCContDB tLCContDB = new LCContDB();
        tLCContDB.setContNo(mContNo);
        if (!tLCContDB.getInfo())
        {
            CError tError = new CError();
            tError.moduleName = "AgencyQueBackAfterInitService";
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
            tError.moduleName = "AgencyQueBackAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "保单" + mContNo + "续保批单业务员主表信息查询失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mLCCUWMasterSchema.setSchema(tLCCUWMasterDB);

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
            tError.moduleName = "AgencyQueBackAfterInitService";
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
            tError.moduleName = "AgencyQueBackAfterInitService";
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
            tError.moduleName = "AgencyQueBackAfterInitService";
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
            tError.moduleName = "AgencyQueBackAfterInitService";
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
            tError.moduleName = "AgencyQueBackAfterInitService";
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
            tError.moduleName = "AgencyQueBackAfterInitService";
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
        mLCIssuePolSet.set((LCIssuePolSet) cInputData.getObjectByObjectName("LCIssuePolSet", 0));

        return true;
    }

    /**
     * 数据操作类业务处理
     * 输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
     * @return boolean
     */
    private boolean dealData()
    {
        if (chooseActivity())
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * 选择工作流流转
     * @return boolean
     */
    private boolean chooseActivity()
    {
//        String tStr = "Select * from LWMission where MissionID = '" +
//                mMissionID + "'and ActivityID = '0000001112' and MissionProp14 = '" +
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
//                mMissionID + "' and ActivityID = '0000001020' and SubMissionID <> '" +
//                mSubMissionID + "' and MissionProp14 = '" +
//                mLWMissionSchema.getMissionProp14() + "'";
//        System.out.println(tStr);

        StringBuffer tSBql = new StringBuffer(128);
        tSBql.append("Select * from LWMission where MissionID = '");
        tSBql.append(mMissionID);
        tSBql.append("'and ActivityID = '0000001112' and MissionProp14 = '");
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
        tSBql.append("' and ActivityID = '0000001020' and SubMissionID <> '");
        tSBql.append(mSubMissionID);
        tSBql.append("' and MissionProp14 = '");
        tSBql.append(mLWMissionSchema.getMissionProp14());
        tSBql.append("'");
//        System.out.println(tSBql.toString());

        LWMissionDB tLWMissionDB = new LWMissionDB();
        mLWMissionSet = tLWMissionDB.executeQuery(tSBql.toString());

        if (mLWMissionSet.size() == 0)
        {
            mQueModFlag = "1";
        }
        else
        {
            mQueModFlag = "0";
        }

        return true;
    }

    /**
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
     * @return boolean
     */
    private boolean prepareTransferData()
    {
        mTransferData.setNameAndValue("QueModFlag", mQueModFlag);
        mTransferData.setNameAndValue("AgencyQueFlag", mAgencyQueFlag);
        mTransferData.setNameAndValue("PrtNo", mLCContSchema.getPrtNo());
        mTransferData.setNameAndValue("AppntNo", mLCContSchema.getAppntNo());
        mTransferData.setNameAndValue("AppntName", mLCContSchema.getAppntName());
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

    public static void main(String[] args)
    {
//        AgencyQueBackAfterInitService a = new AgencyQueBackAfterInitService();
//        a.chooseActivity();
    }
}

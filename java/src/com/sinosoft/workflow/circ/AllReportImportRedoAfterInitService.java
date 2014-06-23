/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.circ;

import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: </p>
 * <p>Description:工作流节点任务:保监会报表工作流报表提数重新初始化服务类 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class AllReportImportRedoAfterInitService implements AfterInitService
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
    private String mMissionID;
//    private String mItemType;
    private String mDeleteXMLSQL;
    private String mDeleteRiskGetSQL;
    private String mDeleteRiskAppSubSQL;
    private String mDeleteRiskAppSQL;
    private String mDeleteLFFinXMLSQL;
//    private String mDeleteLFActuaryXMLSQL;
    private String mDeleteLFFBXMLSQL;
    private String mDeleteLFActuaryXmlSQL;
    private String mDeleteLFRLXmlSQL;
    private String mDeleteLFTZXmlSQL;
    private String mDeleteChargeSQL;
    private String mDeleteWageSQL;
    private String mDeleteTaxSQL;
    private String mDeleteProductSQL;

//    private Reflections mReflections = new Reflections();

    /**执行保全工作流特约活动表任务0000000003*/
    public AllReportImportRedoAfterInitService()
    {
    }

    /**
     * 传输数据的公共方法
     * @param cInputData VData 输入的数据
     * @param cOperate String 数据操作
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

        map.put(mDeleteXMLSQL, "DELETE");
        map.put(mDeleteRiskGetSQL, "DELETE");
        map.put(mDeleteRiskAppSubSQL, "DELETE");
        map.put(mDeleteRiskAppSQL, "DELETE");
        map.put(mDeleteProductSQL, "DELETE");

        map.put(mDeleteLFFinXMLSQL, "DELETE");
        map.put(mDeleteLFActuaryXmlSQL, "DELETE");
        map.put(mDeleteLFFBXMLSQL, "DELETE");
        map.put(mDeleteLFRLXmlSQL, "DELETE");
        map.put(mDeleteLFTZXmlSQL, "DELETE");
        map.put(mDeleteChargeSQL, "DELETE");
        map.put(mDeleteWageSQL, "DELETE");
        map.put(mDeleteTaxSQL, "DELETE");

        mResult.add(map);
        return true;
    }

    /**
     * 校验业务数据
     * @return boolean
     */
    private static boolean checkData()
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
            tError.moduleName = "AllReportImportRedoAfterInitService";
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
            tError.moduleName = "AllReportImportRedoAfterInitService";
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
            tError.moduleName = "AllReportImportRedoAfterInitService";
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
            tError.moduleName = "AllReportImportRedoAfterInitService";
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
            tError.moduleName = "AllReportImportRedoAfterInitService";
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
            tError.moduleName = "AllReportImportRedoAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中StatMon失败!";
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
            tError.moduleName = "AllReportImportRedoAfterInitService";
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
        //删除xml汇总表
        mDeleteXMLSQL = "delete from LFXMLColl where StatYear=" + mStatYear +
                        " and StatMon=" + mStatMon;
        //删除业务中间表数据
        mDeleteRiskGetSQL = "delete from LFRiskGet where year(reportdate)=" +
                            mStatYear + " and month(reportdate)=" + mStatMon;
        mDeleteRiskAppSubSQL =
                "delete from  LFRiskAppSub where year(reportdate)=" + mStatYear +
                " and month(reportdate)=" + mStatMon;

        mDeleteRiskAppSQL = "delete from LFRiskApp where year(reportdate)=" +
                            mStatYear + " and month(reportdate)=" + mStatMon;

        mDeleteProductSQL = "delete from lfproductxml where year(reportdate)=" +
                            mStatYear + " and month(reportdate)=" + mStatMon;

        //删除财务中间表数据
        mDeleteLFFinXMLSQL = "delete from LFFinXml where year(reportdate)=" +
                             mStatYear + " and month(reportdate)=" + mStatMon;
        //删除业务中间表数据
        //mDeleteOtherSQL2="delete from LFXMLColl where StatYear="+mStatYear+" and StatMon="+mStatMon;
        //删除精算中间表数据
//        mDeleteLFActuaryXmlSQL =
//                "delete from LFActuaryXml where trim(StatYear)=" + mStatYear +
//                " and trim(StatMon)=" + mStatMon;
        mDeleteLFActuaryXmlSQL = "delete from LFActuaryXml where StatYear=" +
                                 mStatYear + " and StatMon=" + mStatMon;
        //删除人力中间表数据
//        mDeleteLFRLXmlSQL = "delete from LFRLXml where trim(StatYear)=" +
//                            mStatYear + " and trim(StatMon)=" + mStatMon;
        mDeleteLFRLXmlSQL = "delete from LFRLXml where StatYear=" + mStatYear +
                            " and StatMon=" + mStatMon;
        //删除投资中间表数据
//        mDeleteLFTZXmlSQL = "delete from LFTZXml where trim(StatYear)=" +
//                            mStatYear.trim() + " and trim(StatMon)=" +
//                            mStatMon.trim();
        mDeleteLFTZXmlSQL = "delete from LFTZXml where StatYear=" +
                            mStatYear.trim() +
                            " and StatMon=" + mStatMon.trim();
        //删除销售中间表数据
        mDeleteChargeSQL = "delete from LFCharge where year(reportdate)=" +
                           mStatYear + " and month(reportdate)=" + mStatMon;

        mDeleteWageSQL = "delete from LFWage where year(reportdate)=" +
                         mStatYear + " and month(reportdate)=" + mStatMon;

        mDeleteTaxSQL = "delete from LFTax where year(reportdate)=" + mStatYear +
                        " and month(reportdate)=" + mStatMon;
        //删除分保中间表数据
        mDeleteLFFBXMLSQL = "delete from  LFReinsureXml where StatYear=" + mStatYear +
                " and StatMon=" + mStatMon;

        return true;

    }


    /**
     * 为公共传输数据集合中添加工作流下一节点属性字段数据
     * @return boolean
     */
    private static boolean prepareTransferData()
    {
        //为工作流中回收体检通知书节点准备属性数据
//	  mTransferData.setNameAndValue("CertifyCode",mLZSysCertifySchema.getCertifyCode());
//	  mTransferData.setNameAndValue("ValidDate",mLZSysCertifySchema.getValidDate()) ;
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

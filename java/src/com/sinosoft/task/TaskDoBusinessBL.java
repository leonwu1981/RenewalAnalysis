package com.sinosoft.task;
import com.sinosoft.utility.*;
import com.sinosoft.lis.bl.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;


public class TaskDoBusinessBL
{
    /** 错误处理类 */
    public CErrors mErrors = new CErrors();

    /** 输入数据的容器 */
    private VData mInputData = new VData();

    /** 输出数据的容器 */
    private VData mResult = new VData();

    /** 数据操作字符串 */
    private String mOperate;

    private LGWorkTraceSchema mLGWorkTraceSchema = new LGWorkTraceSchema();

    private MMap map = new MMap();

    private String mWorkNo;

    private String mTypeNo;

    private String mUrl = "";

    /** 全局参数 */
    private GlobalInput mGlobalInput = new GlobalInput();

    /** 统一更新日期 */
    private String mCurrentDate = PubFun.getCurrentDate();

    /** 统一更新时间 */
    private String mCurrentTime = PubFun.getCurrentTime();

    public TaskDoBusinessBL()
    {
    }

    /**
     * 数据提交的公共方法
     * @param: cInputData 传入的数据
     * @param: cOperate   数据操作字符串
     * @return: boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        // 将传入的数据拷贝到本类中
        mInputData = (VData) cInputData.clone();
        this.mOperate = cOperate;
        System.out.println("now in TaskDeliverBL submit");
        // 将外部传入的数据分解到本类的属性中，准备处理
        if (this.getInputData() == false)
        {
            return false;
        }
        System.out.println("---getInputData---");

        // 根据业务逻辑对数据进行处理
        if (this.dealData() == false)
        {
            return false;
        }
        System.out.println("---dealDate---");

        // 装配处理好的数据，准备给后台进行保存
        this.prepareOutputData();
        System.out.println("---prepareOutputData---");

        PubSubmit tPubSubmit = new PubSubmit();
        System.out.println("Start tPRnewManualDunBLS Submit...");

        if (!tPubSubmit.submitData(mInputData, mOperate))
        {
            // @@错误处理
            this.mErrors.copyAllErrors(tPubSubmit.mErrors);

            CError tError = new CError();
            tError.moduleName = "TaskDeliverBL";
            tError.functionName = "submitData";
            tError.errorMessage = "数据提交失败!";

            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }


    /**
     * 将外部传入的数据分解到本类的属性中
     * @param: 无
     * @return: boolean
     */
    private boolean getInputData()
    {
        mGlobalInput.setSchema((GlobalInput) mInputData.
                               getObjectByObjectName("GlobalInput", 0));
        mWorkNo = (String) mInputData.
                               getObjectByObjectName("String", 0);
        return true;
    }

    /**
     * 校验传入的数据
     * @param: 无
     * @return: boolean
     */
    private boolean checkData()
    {
        return true;
    }

    /**
     * 根据业务逻辑对数据进行处理
     * @param: 无
     * @return: boolean
     */
    private boolean dealData()
    {
        String sql;
        SSRS tSSRS = new SSRS();
        ExeSQL tExeSQL = new ExeSQL();

        sql = "Select TypeNo, StatusNo, DetailWorkNo, CustomerNo, " +
              "       ApplyName, AcceptWayNo, AcceptDate " +
              "From   LGWork " +
              "Where  WorkNo= '" + mWorkNo + "' ";
        tSSRS = tExeSQL.execSQL(sql);
        String tTypeNo = tSSRS.GetText(1, 1);
        String tStatusNo = tSSRS.GetText(1, 2);
        String tDetailWorkNo = tSSRS.GetText(1, 3);
        String tCustomerNo = tSSRS.GetText(1, 4);
        String tApplyName = tSSRS.GetText(1,5);
        String tAcceptWayNo = tSSRS.GetText(1, 6);
        String tAcceptDate = tSSRS.GetText(1, 7);
        mTypeNo = tTypeNo.substring(0, 2);
        String tStateNo = "";

        if ((tStatusNo != null) && (mTypeNo != null))
        {
            if ((tStatusNo.equals("2")) || (tStatusNo.equals("3")))
            {
                if (mTypeNo.equals("01")) //咨询
                {
                }
                else if (mTypeNo.equals("02")) //投诉
                {

                }
                else if (mTypeNo.equals("03")) //保全
                {
                    String tEdorAcceptNo;
                    String tEdorState;
                    String tAutoUWFlag;
                    String tUwState;

                    tEdorAcceptNo = tDetailWorkNo;
                    sql = "Select e.EdorState, e.UwState, a.AutoUWFlag " +
                          "From   LPEdorApp e, LPAppUWMasterMain a " +
                          "Where  e.EdorAcceptNo = a.EdorAcceptNo " +
                          "And    e.EdorAcceptNo = '" + tEdorAcceptNo + "' " +
                          "Union " +
                          "Select EdorState, UwState, '' as AutoUWFlag " +
                          "From   LPEdorApp " +
                          "Where  EdorAcceptNo Not In " +
                          "(SELECT EdorAcceptNo FROM LPAppUWMasterMain) " +
                          "And    EdorAcceptNo = '" + tEdorAcceptNo + "' ";
                    tSSRS = tExeSQL.execSQL(sql);
                    tEdorState = tSSRS.GetText(1, 1);
                    tUwState = tSSRS.GetText(1, 2);
                    tAutoUWFlag = tSSRS.GetText(1, 3);
                    if ((tEdorState == null) || (tEdorState.equals("")))
                    {
                        tEdorState = "N";
                    }
                    if ((tUwState == null) || (tUwState.equals("")))
                    {
                        tUwState = "N";
                    }
                    if ((tAutoUWFlag == null) || (tAutoUWFlag.equals("")))
                    {
                        tAutoUWFlag = "N";
                    }
                    tStateNo = tEdorState + tUwState + tAutoUWFlag;
                    System.out.println(tStateNo);
                }
                else if (mTypeNo.equals("04")) //理赔
                {
                }

                if (!tStateNo.equals(""))
                {
                    sql = "Select UrlInfo " +
                          "From   LGBusinessUrl " +
                          "Where  TypeNo = '" + mTypeNo + "' " +
                          "and    StatusNo = '" + tStateNo + "' ";
                    tSSRS = tExeSQL.execSQL(sql);
                    String tUrlInfo = tSSRS.GetText(1, 1);
                    mUrl = tUrlInfo;
                    mUrl += "DetailWorkNo=" + tDetailWorkNo +
                            "&CustomerNo=" + tCustomerNo +
                            "&ApplyName=" + tApplyName +
                            "&AcceptWayNo=" + tAcceptWayNo +
                            "&AcceptDate=" + tAcceptDate;
                }
            }
        }
        return true;
    }


    /**
     * 根据业务逻辑对数据进行处理
     * @param: 无
     * @return: void
     */
    private void prepareOutputData()
    {
        mInputData.clear();
        mInputData.add(map);
        mResult.clear();
        mResult.add(mUrl);
    }

    /**
     * 得到处理后的结果集
     * @return 结果集
     */
    public VData getResult()
    {
        return mResult;
    }
}

package com.sinosoft.task;
import com.sinosoft.utility.*;
import com.sinosoft.lis.bl.*;
import com.sinosoft.lis.schema.*;
import com.sinosoft.lis.vschema.*;
import com.sinosoft.lis.db.*;
import com.sinosoft.lis.pubfun.*;


public class TaskDoBusinessBL
{
    /** �������� */
    public CErrors mErrors = new CErrors();

    /** �������ݵ����� */
    private VData mInputData = new VData();

    /** ������ݵ����� */
    private VData mResult = new VData();

    /** ���ݲ����ַ��� */
    private String mOperate;

    private LGWorkTraceSchema mLGWorkTraceSchema = new LGWorkTraceSchema();

    private MMap map = new MMap();

    private String mWorkNo;

    private String mTypeNo;

    private String mUrl = "";

    /** ȫ�ֲ��� */
    private GlobalInput mGlobalInput = new GlobalInput();

    /** ͳһ�������� */
    private String mCurrentDate = PubFun.getCurrentDate();

    /** ͳһ����ʱ�� */
    private String mCurrentTime = PubFun.getCurrentTime();

    public TaskDoBusinessBL()
    {
    }

    /**
     * �����ύ�Ĺ�������
     * @param: cInputData ���������
     * @param: cOperate   ���ݲ����ַ���
     * @return: boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        // ����������ݿ�����������
        mInputData = (VData) cInputData.clone();
        this.mOperate = cOperate;
        System.out.println("now in TaskDeliverBL submit");
        // ���ⲿ��������ݷֽ⵽����������У�׼������
        if (this.getInputData() == false)
        {
            return false;
        }
        System.out.println("---getInputData---");

        // ����ҵ���߼������ݽ��д���
        if (this.dealData() == false)
        {
            return false;
        }
        System.out.println("---dealDate---");

        // װ�䴦��õ����ݣ�׼������̨���б���
        this.prepareOutputData();
        System.out.println("---prepareOutputData---");

        PubSubmit tPubSubmit = new PubSubmit();
        System.out.println("Start tPRnewManualDunBLS Submit...");

        if (!tPubSubmit.submitData(mInputData, mOperate))
        {
            // @@������
            this.mErrors.copyAllErrors(tPubSubmit.mErrors);

            CError tError = new CError();
            tError.moduleName = "TaskDeliverBL";
            tError.functionName = "submitData";
            tError.errorMessage = "�����ύʧ��!";

            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }


    /**
     * ���ⲿ��������ݷֽ⵽�����������
     * @param: ��
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
     * У�鴫�������
     * @param: ��
     * @return: boolean
     */
    private boolean checkData()
    {
        return true;
    }

    /**
     * ����ҵ���߼������ݽ��д���
     * @param: ��
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
                if (mTypeNo.equals("01")) //��ѯ
                {
                }
                else if (mTypeNo.equals("02")) //Ͷ��
                {

                }
                else if (mTypeNo.equals("03")) //��ȫ
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
                else if (mTypeNo.equals("04")) //����
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
     * ����ҵ���߼������ݽ��д���
     * @param: ��
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
     * �õ������Ľ����
     * @return �����
     */
    public VData getResult()
    {
        return mResult;
    }
}

/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.circ;

import java.util.LinkedList;

import com.sinosoft.lis.db.LFComISCDB;
import com.sinosoft.lis.db.LFDesbModeDB;
import com.sinosoft.lis.db.LFItemRelaDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.schema.LFComISCSchema;
import com.sinosoft.lis.schema.LFDesbModeSchema;
import com.sinosoft.lis.schema.LFItemRelaSchema;
import com.sinosoft.lis.vschema.LFComISCSet;
import com.sinosoft.lis.vschema.LFDesbModeSet;
import com.sinosoft.lis.vschema.LFItemRelaSet;
import com.sinosoft.msreport.ColDataBLS;
import com.sinosoft.msreport.ReportEngineUI;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: </p>
 * <p>Description:�������ڵ�����:��ȫ�˹��˱����ͺ˱�֪ͨ������� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class XmlReportCalAfterInitService implements AfterInitService
{

    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** �����洫�����ݵ����� */
//    private VData mInputData;
    /** �����洫�����ݵ����� */
    private VData mResult = new VData();
    /** �������������д������ݵ����� */
    private GlobalInput mGlobalInput = new GlobalInput();
    //private VData mIputData = new VData();
    private TransferData mTransferData = new TransferData();
    /** ���ݲ����ַ��� */
    private String mOperater;
    private String mManageCom;
//    private String mOperate;
    /** ҵ�����ݲ����ַ��� */
    private String mStatYear;
    private String mStatMon;
    private String mRepType;
    private String mMissionID;
//    private String mItemType;
    private LinkedList mLinkedList = new LinkedList();

//    private Reflections mReflections = new Reflections();

    /**ִ�б�ȫ��������Լ�������0000000003*/
    public XmlReportCalAfterInitService()
    {
    }

    /**
     * �������ݵĹ�������
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    public boolean submitData(VData cInputData, String cOperate)
    {
        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData(cInputData, cOperate))
        {
            return false;
        }

        //У���Ƿ���δ��ӡ�����֪ͨ��
        if (!checkData())
        {
            return false;
        }

        //����ҵ����
        if (!dealData())
        {
            return false;
        }

        //Ϊ��������һ�ڵ������ֶ�׼������
        if (!prepareTransferData())
        {
            return false;
        }

        //׼������̨������
        if (!prepareOutputData())
        {
            return false;
        }

        System.out.println("Start  Submit...");

        //mResult.clear();
        return true;
    }

    /**
     * ׼������ǰ̨ͳһ�洢����
     * �����������������򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean prepareOutputData()
    {
        mResult.clear();
        MMap map = new MMap();

        //��Ӻ˱�֪ͨ���ӡ���������
//	  map.put(mDeleteXMLSQL, "DELETE");
//        map.put(mDeleteMidSQL, "DELETE");
        mResult.add(map);
        return true;
    }

    /**
     * У��ҵ������
     * @return boolean
     */
    private boolean checkData()
    {

        return true;
    }

    /**
     * �����������еõ����ж���
     * ��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     * @param cInputData VData
     * @param cOperate String
     * @return boolean
     */
    private boolean getInputData(VData cInputData, String cOperate)
    {
        //�����������еõ����ж���
        //���ȫ�ֹ�������
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);
//        mInputData = cInputData;
        if (mGlobalInput == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��ò���Ա����
        mOperater = mGlobalInput.Operator;
        if (mOperater == null || mOperater.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������Operateʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�½��������
        mManageCom = mGlobalInput.ManageCom;
        if (mManageCom == null || mManageCom.trim().equals(""))
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ȫ�ֹ�������ManageComʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

//        mOperate = cOperate;

        //���ҵ������
        if (mTransferData == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mStatYear = (String) mTransferData.getValueByName("StatYear");
        if (mStatYear == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������StatYearʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mStatMon = (String) mTransferData.getValueByName("StatMon");
        if (mStatMon == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������StatMonʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mRepType = (String) mTransferData.getValueByName("RepType");
        if (mRepType == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������RepTypeʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //��õ�ǰ�������������ID
        mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean dealData()
    {

//���տ�Ŀ���л��ܴ���(�ϼƿ�Ŀ����)
        LFItemRelaDB tLFItemRelaDB = new LFItemRelaDB();
//    String tSQL = "select * from LFItemRela where IsLeaf='0' order by ItemLevel desc";
        String tSQL = "select a.* from LFItemRela a where a.IsLeaf='0' and isCalFlag='0' order by a.ItemLevel desc";
//String tSQL = "select a.* from LFItemRela a where a.IsLeaf='0' and ItemCode='1851' and exists(select UpItemCode from LFXMLColl where UpItemCode=a.ItemCode and RepType='"+this.RepType+"' and StatYear="+this.StatYear+" and StatMon="+this.StatMon+" ) order by a.ItemLevel desc";
        System.out.println(tSQL);
        LFItemRelaSet tLFItemRelaSet = tLFItemRelaDB.executeQuery(tSQL);
        if (tLFItemRelaDB.mErrors.needDealError())
        {
            CError tError = new CError();
            tError.moduleName = "ColDataBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��ѯ�����Ŀ�����Ӧ�����";
            this.mErrors.addOneError(tError);
            return false;
        }
        int tSize = tLFItemRelaSet.size();
        for (int i = 1; i <= tSize; i++)
        {
            LFItemRelaSchema tLFItemRelaSchema = tLFItemRelaSet.get(i);
            if (tLFItemRelaSchema.getRemark() == null ||
                tLFItemRelaSchema.getRemark().trim().equals("null"))
            {
                tLFItemRelaSchema.setRemark("");
            }
//      System.out.println(tLFItemRelaSchema.getRemark());
            String aSQL = "insert into LFXMLColl select ComCodeISC,'" +
                          tLFItemRelaSchema.getItemCode() + "','" +
                          this.mRepType + "'," + this.mStatYear + "," +
                          this.mStatMon + ",'" +
                          tLFItemRelaSchema.getUpItemCode() +
                          "',MAX(ParentComCodeISC)," +
                          tLFItemRelaSchema.getLayer() + ",sum(StatValue),'" +
                          tLFItemRelaSchema.getRemark() +
                          "' from LFXMLColl where RepType='" + this.mRepType +
                          "' and StatYear=" + this.mStatYear + " and StatMon=" +
                          this.mStatMon + " and Layer=1 and UpItemCode='" +
                          tLFItemRelaSchema.getItemCode() +
                          "' group by ComCodeISC";
            System.out.println(aSQL);
            mLinkedList.add(aSQL);
            ColDataBLS tColDataBLS = new ColDataBLS();
//���ú�̨BLS��������ݿ���в���
            if (!tColDataBLS.submitData(mLinkedList))
            {
                // @@������
                this.mErrors.copyAllErrors(tColDataBLS.mErrors);
                CError tError = new CError();
                tError.moduleName = "ColDataBL";
                tError.functionName = "dealData";
                tError.errorMessage = "�����ύʧ��!";
                this.mErrors.addOneError(tError);
                return false;
            }
            mLinkedList.clear();
        }

//�����Ŀ�Ļ��ܴ���(���ϼƿ�Ŀ)
        LFDesbModeDB tLFDesbModeDB = new LFDesbModeDB();
        String tempSQL = "select * from LFDesbMode  where ItemType in('C1','C2','C3','C4','C5','C6','C7') and DealType='S' order by ItemNum ";
        System.out.println(tempSQL);
        LFDesbModeSet tLFDesbModeSet = tLFDesbModeDB.executeQuery(tempSQL);
        if (tLFDesbModeSet.mErrors.needDealError())
        {
            CError tError = new CError();
            tError.moduleName = "ColDataBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��ѯLFDesbMode(�ϼƿ�Ŀ)����";
            this.mErrors.addOneError(tError);
            return false;
        }
        int tempSize = tLFDesbModeSet.size();
        System.out.println("size:" + tempSize);

        for (int j = 1; j <= tempSize; j++)
        {
            LFDesbModeSchema tLFDesbModeSchema = new LFDesbModeSchema();
            tLFDesbModeSchema = tLFDesbModeSet.get(j);
            System.out.println("itemcode:" + tLFDesbModeSchema.getItemCode());
            VData tVData = new VData();
            GlobalInput mGlobalInput = new GlobalInput();
            TransferData mTransferData = new TransferData();

            /** ���ݱ��� */
            //�������
            //mTransferData.setNameAndValue("ReportDate", "2004-06-10");
            //mTransferData.setNameAndValue("MakeDate", "2003-03-01");

            mTransferData.setNameAndValue("reptype", mRepType);
            mTransferData.setNameAndValue("StatYear", mStatYear);
            mTransferData.setNameAndValue("StatMon", mStatMon);

            tVData.add(mGlobalInput);
            tVData.add("1");
            tVData.add(mTransferData);

            try
            {
                ReportEngineUI tReportEngineUI = new ReportEngineUI();
                System.out.println("sql:" + tLFDesbModeSchema.getItemCode() +
                                   "||" + "" +
                                   "|| AND Dealtype='S' AND ItemType in('C1','C2','C3','C4','C5','C6','C7')");

                if (!tReportEngineUI.submitData(tVData,
                                                tLFDesbModeSchema.getItemCode() +
                                                "||" + "" +
                                                "|| AND Dealtype='S' AND ItemType in('C1','C2','C3','C4','C5','C6','C7')"))
                {
                    if (tReportEngineUI.mErrors.needDealError())
                    {
                        System.out.println(tReportEngineUI.mErrors.
                                           getFirstError());
                    }
                    else
                    {
                        System.out.println("����ʧ�ܣ�����û����ϸ��ԭ��");
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

//���ջ������л��ܴ���
        LFComISCDB tLFComISCDB = new LFComISCDB();
//    String cSQL = "select * from LFComISC where IsLeaf='0' order by ComLevel desc";
        String cSQL =
                "select a.* from LFComISC a where a.IsLeaf='0' order by a.ComLevel desc";
        System.out.println(cSQL);
        LFComISCSet tLFComISCSet = tLFComISCDB.executeQuery(cSQL);
        if (tLFComISCDB.mErrors.needDealError())
        {
//      this.mErrors.copyAllErrors(tLFComISCDB.mErrors);
            CError tError = new CError();
            tError.moduleName = "ColDataBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��ѯ�����涨������Ϣ�����";
            this.mErrors.addOneError(tError);
            return false;
        }
        int aSize = tLFComISCSet.size();
        for (int i = 1; i <= aSize; i++)
        {
            LFComISCSchema tLFComISCSchema = tLFComISCSet.get(i);
            //���ӱ�������������п�����Ϊ�������ⱨ��2004-6-20 yt
            String dSQL = "insert into LFXMLColl select c.c1,c.itemcode,c.c3,c.c4,c.c5,c.c6,c.c7,c.c8,c.c9,b.Remark from (select '" +
                          tLFComISCSchema.getComCodeISC() +
                          "' c1,a.ItemCode itemcode,'" + this.mRepType +
                          "' c3," + this.mStatYear + " c4," + this.mStatMon +
                          " c5,max(a.UpItemCode) c6,'" +
                          tLFComISCSchema.getParentComCodeISC() +
                          "' c7,max(a.Layer) c8,sum(a.StatValue) c9 from LFXMLColl a where a.RepType='" +
                          this.mRepType + "' and a.StatYear=" + this.mStatYear +
                          " and a.StatMon=" + this.mStatMon +
                          " and a.ParentComCodeISC='" +
                          tLFComISCSchema.getComCodeISC() + "' group by a.ItemCode) c,LFItemRela b where c.ItemCode=b.ItemCode and b.General='1'";
            System.out.println(dSQL);
            mLinkedList.add(dSQL);
            //���ú�̨BLS��������ݿ���в���
            ColDataBLS tColDataBLS = new ColDataBLS();
            if (!tColDataBLS.submitData(mLinkedList))
            {
                // @@������
                this.mErrors.copyAllErrors(tColDataBLS.mErrors);
                CError tError = new CError();
                tError.moduleName = "ColDataBL";
                tError.functionName = "dealData";
                tError.errorMessage = "�����ύʧ��!";
                this.mErrors.addOneError(tError);
                return false;
            }
            mLinkedList.clear();
        }
        //�����Ŀ�Ļ��ܴ���(���ڶ���ϼƿ�Ŀ������Ҫ�ֻܷ��ܣ������ɿ�Ŀ������Ҫ���ֻܷ��ܣ������ڹ��ɿ�Ŀ���Ѽ�������������������Ŀ��itemcode=2544)
        tLFDesbModeDB = new LFDesbModeDB();
        tempSQL = "select * from LFDesbMode  where ItemType in('D1','D2','D3','D4','D5','D6','D7') and DealType='S' order by ItemNum ";
        System.out.println(tempSQL);
        tLFDesbModeSet = tLFDesbModeDB.executeQuery(tempSQL);
        if (tLFDesbModeSet.mErrors.needDealError())
        {
            CError tError = new CError();
            tError.moduleName = "ColDataBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��ѯLFDesbMode(�ڶ���ϼƿ�Ŀ)����";
            this.mErrors.addOneError(tError);
            return false;
        }
        tempSize = tLFDesbModeSet.size();
        System.out.println("size:" + tempSize);

        for (int j = 1; j <= tempSize; j++)
        {
            LFDesbModeSchema tLFDesbModeSchema = new LFDesbModeSchema();
            tLFDesbModeSchema = tLFDesbModeSet.get(j);
            System.out.println("itemcode:" + tLFDesbModeSchema.getItemCode());
            VData tVData = new VData();
            GlobalInput mGlobalInput = new GlobalInput();
            TransferData mTransferData = new TransferData();

            /** ���ݱ��� */
            //�������
            //mTransferData.setNameAndValue("ReportDate", "2004-06-10");
            //mTransferData.setNameAndValue("MakeDate", "2003-03-01");

            mTransferData.setNameAndValue("reptype", mRepType);
            mTransferData.setNameAndValue("StatYear", mStatYear);
            mTransferData.setNameAndValue("StatMon", mStatMon);

            tVData.add(mGlobalInput);
            tVData.add("1");
            tVData.add(mTransferData);

            try
            {
                ReportEngineUI tReportEngineUI = new ReportEngineUI();
                System.out.println("sql:" + tLFDesbModeSchema.getItemCode() +
                                   "||" + "" +
                                   "|| AND Dealtype='S' AND ItemType in('D1','D2','D3','D4','D5','D6','D7')");

                if (!tReportEngineUI.submitData(tVData,
                                                tLFDesbModeSchema.getItemCode() +
                                                "||" + "" +
                                                "|| AND Dealtype='S' AND ItemType in('D1','D2','D3','D4','D5','D6','D7')"))
                {
                    if (tReportEngineUI.mErrors.needDealError())
                    {
                        System.out.println(tReportEngineUI.mErrors.
                                           getFirstError());
                    }
                    else
                    {
                        System.out.println("����ʧ�ܣ�����û����ϸ��ԭ��");
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        //��Ӧ�ñ�����ҵ�����ݵĿ�Ŀ
        String tAndSql = "";
        if (this.mRepType == null || this.mRepType.length() == 0)
        {
            CError tError = new CError();
            tError.moduleName = "ColDataBL";
            tError.functionName = "dealData";
            tError.errorMessage = "�������Ͳ���Ϊ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (this.mRepType.equals("1"))
        {
            tAndSql = " and IsQuick='1' ";
        }
        else if (this.mRepType.equals("2"))
        {
            tAndSql = " and IsMon='1' ";
        }
        else if (this.mRepType.equals("3"))
        {
            tAndSql = " and IsQut='1' ";
        }
        else if (this.mRepType.equals("4"))
        {
            tAndSql = " and IsHalYer='1' ";
        }
        else if (this.mRepType.equals("5"))
        {
            tAndSql = " and IsYear='1' ";
        }
        else
        {
            CError tError = new CError();
            tError.moduleName = "ColDataBL";
            tError.functionName = "dealData";
            tError.errorMessage = "�������ʹ���";
            this.mErrors.addOneError(tError);
            return false;
        }
        String tSql = "insert into LFXMLColl ";
        tSql = tSql + "select  b.comcodeisc,a.itemcode,'" + this.mRepType +
               "'," + this.mStatYear + "," + this.mStatMon +
               ",a.upitemcode,b.ParentComCodeISC,a.layer,0,a.remark";
        tSql = tSql + " from lfitemrela a,lfcomisc b where 1=1 and b.OutputFlag ='1' and a.outputflag='1' ";
        tSql = tSql + tAndSql;
        tSql = tSql + " and not exists (select 'X' from lfxmlcoll where comcodeisc=b.comcodeisc and itemcode = a.itemcode and statyear=" +
               this.mStatYear + " and statmon=" + this.mStatMon +
               " and reptype='" + this.mRepType + "') ";
        tSql = tSql + " and b.ComLevel<=a.ComFlag ";
        System.out.println(tSql);

        mLinkedList.add(tSql);
        //���ú�̨BLS��������ݿ���в���
        ColDataBLS tColDataBLS = new ColDataBLS();
        if (!tColDataBLS.submitData(mLinkedList))
        {
            // @@������
            this.mErrors.copyAllErrors(tColDataBLS.mErrors);
            CError tError = new CError();
            tError.moduleName = "ColDataBL";
            tError.functionName = "dealData";
            tError.errorMessage = "�����ύʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mLinkedList.clear();
        return true;
    }


    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return boolean
     */
    private boolean prepareTransferData()
    {
        //Ϊ�������л������֪ͨ��ڵ�׼����������
//	  mTransferData.setNameAndValue("CertifyCode",mLZSysCertifySchema.getCertifyCode());
//	  mTransferData.setNameAndValue("ValidDate",mLZSysCertifySchema.getValidDate()) ;

        return true;
    }


    public VData getResult()
    {
        mResult = new VData(); //����֤����һ����
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

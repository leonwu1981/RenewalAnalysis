/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.circ;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import com.sinosoft.lis.db.LDSysVarDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.utility.*;
import com.sinosoft.workflowengine.AfterInitService;

/**
 * <p>Title: </p>
 * <p>Description:�������ڵ�����:��ȫ�˹��˱����ͺ˱�֪ͨ������� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ReportXMLCreateAfterInitServicer implements AfterInitService
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
//    private LinkedList mLinkedList = new LinkedList();

    private String FORMATMODOL = "0.00"; //���ѱ�����������ľ�ȷλ��
    private DecimalFormat mDecimalFormat = new DecimalFormat(FORMATMODOL); //����ת������

//    private Reflections mReflections = new Reflections();
    private SSRS mSSRS;

    private static final String mArea = "area";
    private static final String mItem = "item";
    private static final String mPK = "PK";
    private static final String mAreaid = "areaid";
    private static final String mKey = "key";
    private static final String mIntervaltype = "intervaltype";
    private static final String mValue = "value";
    private static final String mRemark = "remark";
    /**ִ�б�ȫ��������Լ�������0000000003*/
    public ReportXMLCreateAfterInitServicer()
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

        System.out.println("------get data over---:" + PubFun.getCurrentTime());
        //����XML�ļ�
        if (!makeFile())
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "MakeXMLBL";
            tError.functionName = "submitData";
            tError.errorMessage = "���ݴ���ʧ��MakeXMLBL-->makeFile!";
            this.mErrors.addOneError(tError);
            return false;
        }
        System.out.println("------xml over---:" + PubFun.getCurrentTime());

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
//	map.put(mDeleteXMLSQL, "DELETE");
//map.put(mDeleteMidSQL, "DELETE");
        mResult.add(map);
        return true;
    }

    /**
     * ����ǰ����������ݣ�����xml�ļ�
     * ���������XML�ļ������г����򷵻�false,���򷵻�true
     * @return boolean
     */
    private boolean makeFile()
    {
        try
        {
            String tStatYear = String.valueOf(mStatYear);
            String tStatMon = String.valueOf(mStatMon);
            if (tStatMon.length() == 1)
            {
                tStatMon = "0" + tStatMon;
            }
            //ȡ���ļ�����λ�õ�·��
            LDSysVarDB tLDSysVarDB = new LDSysVarDB();
            tLDSysVarDB.setSysVar("ReportXmlPath");
            if (!tLDSysVarDB.getInfo())
            {
                CError tError = new CError();
                tError.moduleName = "MakeXMLBL";
                tError.functionName = "makeFile";
                tError.errorMessage = "����·������";
                this.mErrors.addOneError(tError);
                return false;
            }
            //����XML�ļ�
            String strFileName = tLDSysVarDB.getSysVarValue() + "DATA" +
                                 SysConst.CorpCode + tStatYear + tStatMon +
                                 mRepType + ".xml";
            PrintWriter out = new PrintWriter(new FileOutputStream(strFileName));
            out.println("<?xml version=\"1.0\" encoding=\"gb2312\" ?>");
            out.flush();
            String rt = "DATA" + SysConst.CorpCode + tStatYear + tStatMon;
            out.println("<" + rt + ">");
            out.flush();
            String tComCodeISC = "";
            for (int i = 1; i <= mSSRS.getMaxRow(); i++)
            {

                String cComCode = mSSRS.GetText(i, 1);
                if (!cComCode.equals(tComCodeISC))
                {
                    if (i != 1)
                    {
                        out.println("  </" + mArea + ">");
                    }
                    tComCodeISC = cComCode;
                    out.println("  <" + mArea + ">");
                    out.println("    <" + mAreaid + ">" + SysConst.CorpCode +
                                tComCodeISC + "</" + mAreaid + ">");
                }
                out.println("    <" + mItem + ">");
                out.println("      <" + mPK + ">");
                out.println("        <" + mKey + ">" + mSSRS.GetText(i, 2) +
                            "</" + mKey + ">");
                out.println("        <" + mIntervaltype + ">" +
                            mSSRS.GetText(i, 3) + "</" + mIntervaltype + ">");
                out.println("      </" + mPK + ">");

                double t = Double.parseDouble(mSSRS.GetText(i, 4));

           System.out.println("tValue:"+t);
                String tStatValue = mDecimalFormat.format(t); //ת�������ı���(�涨�ľ���)

           System.out.println("t2Value:"+tStatValue);

                //out.println("      <"+mValue+">"+String.valueOf(new DecimalFormat("2").format(Double.parseDouble(mSSRS.GetText(i,4))))+"</"+mValue+">");
                out.println("      <" + mValue + ">" + tStatValue + "</" +
                            mValue + ">");

                out.println("      <" + mRemark + ">" + mSSRS.GetText(i, 5) +
                            "</" + mRemark + ">");
                out.println("    </" + mItem + ">");
                out.flush();
            }
            out.println("  </" + mArea + ">");
            out.println("</" + rt + ">");
            out.flush();
            out.close();

//      Element root = new Element(rt);
//      Document doc = new Document(root);
//      String tComCodeISC = "";
//      Element area = new Element(mArea);
//      Element item = new Element(mItem);
//      Element PK = new Element(mPK);
//      for(int i=1;i<=mSSRS.getMaxRow();i++)
//      {
//
//            String cComCode = mSSRS.GetText(i,1);
//            if (!cComCode.equals(tComCodeISC)){
//              tComCodeISC=cComCode;
//              area = new Element(mArea);
//              root.addContent(area);
//              Element areaid = new Element(mAreaid);
//              areaid.setText(tComCodeISC);
//              area.addContent(areaid);
//            }
//
//
//            item = new Element(mItem);
//            area.addContent(item);
//            PK = new Element(mPK);
//            item.addContent(PK);
//            Element key = new Element(mKey);
//            key.setText(mSSRS.GetText(i,2));
//            PK.addContent(key);
//
//
//            Element intervaltype = new Element(mIntervaltype);
//            intervaltype.setText(mSSRS.GetText(i,3));
//            PK.addContent(intervaltype);
//
//
//            Element value = new Element(mValue);
//            value.setText(mSSRS.GetText(i,4));
//            item.addContent(value);
//
//
//            Element remark = new Element(mRemark);
//            remark.setText(mSSRS.GetText(i,5));
//            item.addContent(remark);
//
//      }
//      XMLOutputter xo = new XMLOutputter(" ",true,"gb2312");
//      xo.output(doc,new FileOutputStream(strFileName));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            CError tError = new CError();
            tError.moduleName = "MakeXMLBL";
            tError.functionName = "makeFile";
            tError.errorMessage = ex.toString();
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }


    /**
     * У��ҵ������
     * @return boolean
     */
    private static boolean checkData()
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
        //ȡ������Ҫ�����ļ�������
        String tSQL = "select a.ComCodeISC,b.OutItemCode,a.RepType,a.StatValue,a.Remark from LFXMLColl a,LFItemRela b where a.ItemCode=b.ItemCode and a.RepType='" +
                      this.mRepType + "' and a.StatYear=" + this.mStatYear +
                      " and a.StatMon=" + this.mStatMon +
                      " order by a.ComCodeISC,a.ItemCode";
        ExeSQL tExeSQL = new ExeSQL();
        mSSRS = tExeSQL.execSQL(tSQL);
        if (tExeSQL.mErrors.needDealError())
        {
            CError tError = new CError();
            tError.moduleName = "MakeXMLBL";
            tError.functionName = "dealData";
            tError.errorMessage = "��ѯXML���ݻ��ܱ����";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }


    /**
     * Ϊ�����������ݼ�������ӹ�������һ�ڵ������ֶ�����
     * @return boolean
     */
    private static boolean prepareTransferData()
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

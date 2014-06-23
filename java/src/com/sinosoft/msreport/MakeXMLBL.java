/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import com.sinosoft.lis.db.LDSysVarDB;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.utility.*;

/**
 * <p>Title: lis</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: sinosoft</p>
 * @author lh
 * @version 1.0
 */

public class MakeXMLBL
{

    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** �����洫�����ݵ����� */
    private VData mInputData;
    /** ͳ���� */
    private int StatYear;
    /** ͳ���� */
    private int StatMon;
    /** �������� */
    private String RepType;
    private SSRS mSSRS;

    private static final String mArea = "area";
    private static final String mItem = "item";
    private static final String mPK = "PK";
    private static final String mAreaid = "areaid";
    private static final String mKey = "key";
    private static final String mIntervaltype = "intervaltype";
    private static final String mValue = "value";
    private static final String mRemark = "remark";

    public MakeXMLBL()
    {
    }

    /**
     * �������ݵĹ�������
     * @param: cInputData ���������
     *         cOperate ���ݲ���
     * @return:
     */
    public boolean submitData(VData cInputData)
    {
        System.out.println("------start---:" + PubFun.getCurrentTime());
        //�õ��ⲿ���������,�����ݱ��ݵ�������
        if (!getInputData(cInputData))
        {
            return false;
        }

        //����ҵ����
        if (!dealData())
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "MakeXMLBL";
            tError.functionName = "submitData";
            tError.errorMessage = "���ݴ���ʧ��MakeXMLBL-->dealData!";
            this.mErrors.addOneError(tError);
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
        return true;
    }

    /**
     * ����ǰ����������ݣ�����BL�߼�����
     * ����ڴ�������г����򷵻�false,���򷵻�true
     */
    private boolean dealData()
    {
        //ȡ������Ҫ�����ļ�������
        String tSQL = "select a.ComCodeISC,b.OutItemCode,a.RepType,a.StatValue,a.Remark from LFXMLColl a,LFItemRela b where a.ItemCode=b.ItemCode and a.RepType='" +
                      this.RepType + "' and a.StatYear=" + this.StatYear +
                      " and a.StatMon=" + this.StatMon +
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
     * ����ǰ����������ݣ�����xml�ļ�
     * ���������XML�ļ������г����򷵻�false,���򷵻�true
     */
    private boolean makeFile()
    {
        try
        {
            String tStatYear = String.valueOf(StatYear);
            String tStatMon = String.valueOf(StatMon);
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
                                 RepType + ".xml";
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
                    out.println("    <" + mAreaid + ">" + tComCodeISC + "</" +
                                mAreaid + ">");
                }
                out.println("    <" + mItem + ">");
                out.println("      <" + mPK + ">");
                out.println("        <" + mKey + ">" + mSSRS.GetText(i, 2) +
                            "</" + mKey + ">");
                out.println("        <" + mIntervaltype + ">" +
                            mSSRS.GetText(i, 3) + "</" + mIntervaltype + ">");
                out.println("      </" + mPK + ">");
                out.println("      <" + mValue + ">" +
                            String.valueOf(new
                                           DecimalFormat("2").format(Double.
                        parseDouble(mSSRS.GetText(i, 4)))) + "</" + mValue +
                            ">");
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
     * �����������еõ����ж���
     *��������û�еõ��㹻��ҵ�����ݶ����򷵻�false,���򷵻�true
     */
    private boolean getInputData(VData cInputData)
    {
        StatYear = Integer.parseInt(((String) cInputData.get(0)));
        StatMon = Integer.parseInt(((String) cInputData.get(1)));
//    StatYear =((Integer)cInputData.get(0)).intValue();
//    StatMon =((Integer)cInputData.get(1)).intValue();
        RepType = (String) cInputData.get(2);
        return true;
    }

    public static void main(String[] args)
    {
        VData tVData = new VData();
        String StatYear = "2004";
        String StatMon = "5";
        String RepType = "2";
        tVData.add(StatYear);
        tVData.add(StatMon);
        tVData.add(RepType);
        MakeXMLBL MakeXMLBL1 = new MakeXMLBL();
        MakeXMLBL1.submitData(tVData);
    }
}

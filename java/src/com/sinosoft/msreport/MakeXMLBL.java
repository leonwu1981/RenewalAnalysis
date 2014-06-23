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

    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往后面传输数据的容器 */
    private VData mInputData;
    /** 统计年 */
    private int StatYear;
    /** 统计月 */
    private int StatMon;
    /** 报表类型 */
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
     * 传输数据的公共方法
     * @param: cInputData 输入的数据
     *         cOperate 数据操作
     * @return:
     */
    public boolean submitData(VData cInputData)
    {
        System.out.println("------start---:" + PubFun.getCurrentTime());
        //得到外部传入的数据,将数据备份到本类中
        if (!getInputData(cInputData))
        {
            return false;
        }

        //进行业务处理
        if (!dealData())
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "MakeXMLBL";
            tError.functionName = "submitData";
            tError.errorMessage = "数据处理失败MakeXMLBL-->dealData!";
            this.mErrors.addOneError(tError);
            return false;
        }
        System.out.println("------get data over---:" + PubFun.getCurrentTime());
        //生成XML文件
        if (!makeFile())
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "MakeXMLBL";
            tError.functionName = "submitData";
            tError.errorMessage = "数据处理失败MakeXMLBL-->makeFile!";
            this.mErrors.addOneError(tError);
            return false;
        }
        System.out.println("------xml over---:" + PubFun.getCurrentTime());
        return true;
    }

    /**
     * 根据前面的输入数据，进行BL逻辑处理
     * 如果在处理过程中出错，则返回false,否则返回true
     */
    private boolean dealData()
    {
        //取得所有要生成文件的数据
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
            tError.errorMessage = "查询XML数据汇总表出错！";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * 根据前面的输入数据，生成xml文件
     * 如果在生成XML文件过程中出错，则返回false,否则返回true
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
            //取得文件生成位置的路径
            LDSysVarDB tLDSysVarDB = new LDSysVarDB();
            tLDSysVarDB.setSysVar("ReportXmlPath");
            if (!tLDSysVarDB.getInfo())
            {
                CError tError = new CError();
                tError.moduleName = "MakeXMLBL";
                tError.functionName = "makeFile";
                tError.errorMessage = "生成路径出错！";
                this.mErrors.addOneError(tError);
                return false;
            }
            //生成XML文件
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
     * 从输入数据中得到所有对象
     *输出：如果没有得到足够的业务数据对象，则返回false,否则返回true
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

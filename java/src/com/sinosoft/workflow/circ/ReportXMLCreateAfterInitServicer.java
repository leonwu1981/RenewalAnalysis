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
 * <p>Description:工作流节点任务:保全人工核保发送核保通知书服务类 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ReportXMLCreateAfterInitServicer implements AfterInitService
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
    private String mRepType;
    private String mMissionID;
//    private String mItemType;
//    private LinkedList mLinkedList = new LinkedList();

    private String FORMATMODOL = "0.00"; //保费保额计算出来后的精确位数
    private DecimalFormat mDecimalFormat = new DecimalFormat(FORMATMODOL); //数字转换对象

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
    /**执行保全工作流特约活动表任务0000000003*/
    public ReportXMLCreateAfterInitServicer()
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
//	map.put(mDeleteXMLSQL, "DELETE");
//map.put(mDeleteMidSQL, "DELETE");
        mResult.add(map);
        return true;
    }

    /**
     * 根据前面的输入数据，生成xml文件
     * 如果在生成XML文件过程中出错，则返回false,否则返回true
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
                String tStatValue = mDecimalFormat.format(t); //转换计算后的保费(规定的精度)

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
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
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
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
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
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
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
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
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
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
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
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中StatMon失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mRepType = (String) mTransferData.getValueByName("RepType");
        if (mRepType == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中RepType失败!";
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
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
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
        //取得所有要生成文件的数据
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
            tError.errorMessage = "查询XML数据汇总表出错！";
            this.mErrors.addOneError(tError);
            return false;
        }
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
        mResult = new VData(); //不保证事务一致性
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

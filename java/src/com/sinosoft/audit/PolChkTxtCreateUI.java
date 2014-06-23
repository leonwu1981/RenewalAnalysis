/**
 * Copyright (c) 2006 sinosoft Co. Ltd.
 * All right reserved.
 */

package com.sinosoft.audit;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import com.sinosoft.lis.db.LDSysVarDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.utility.*;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import java.io.File;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Transformer;

/**
 * <p>
 * ClassName: PolChkTxtCreateUI
 * </p>
 * <p>
 * Company: Sinosoft Co. Ltd.
 * </p>
 * @author not attributable
 * @version 1.0
 */
public class PolChkTxtCreateUI {


    public CErrors mErrors = new CErrors();
//    private VData mInputData;
    private VData mResult = new VData();
    private GlobalInput mGlobalInput = new GlobalInput();
    private TransferData mTransferData = new TransferData();
    private String mOperater;
    private String mManageCom;
    private String mStatDate;
    private String mEndDate;
    private String mOperate;

    private String mTableName;

    private String mFilePath;
    private String mXslFilePath;
    private String FORMATMODOL = "0.00"; 
    private DecimalFormat mDecimalFormat = new DecimalFormat(FORMATMODOL); 

    private SSRS mSSRS;
    private VData mFieldData;


    private String mfileName = "";
    private static final String mPre = "";
    private static final String mPolMain = mPre + "au_pol_main";
    private static final String mEndoFee = mPre + "au_endo_fee";
    private static final String mPremInfo = mPre + "au_prem_info";
    private static final String mClaimMain = mPre + "au_claim_main";
    private static final String mClaimSettled = mPre + "au_claim_settled";
    private static final String mPayDue = mPre + "au_pay_due";
    private static final String mAgentInfo = mPre + "au_agent_info";
    private static final String mAgtCode = mPre + "au_agt_code";
    private static final String mPlanInfo = mPre + "au_plan_info";

	/**
	 * PolChkTxtCreateUI
	 */
    public PolChkTxtCreateUI() {
    }

	/**
	 * submitData
	 * 通用接口：接收传入数据，并对数据进行处理
	 * @param VData cInputData
	 */
    public boolean submitData(VData cInputData) {
        System.out.println(" start  submit");
        if (!getInputData(cInputData)) {
            return false;
        }

        if (!checkData()) {
            return false;
        }

        if (!dealData()) {
            return false;
        }

        System.out.println("------deal data over---:" + PubFun.getCurrentTime());

        if (!prepareTransferData()) {
            return false;
        }

        if (!prepareOutputData()) {
            return false;
        }

        System.out.println("end  Submit");

        return true;
    }

	/**
	 * getInputData
	 * 获取传入数据
	 * @param VData cInputData
	 */
    private boolean getInputData(VData cInputData) {
        mGlobalInput.setSchema((GlobalInput) cInputData.getObjectByObjectName(
                "GlobalInput", 0));
        mTransferData = (TransferData) cInputData.getObjectByObjectName(
                "TransferData", 0);
        if (mGlobalInput == null) {
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mOperater = mGlobalInput.Operator;
        if (mOperater == null || mOperater.trim().equals("")) {
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据Operate失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mManageCom = mGlobalInput.ManageCom;
        if (mManageCom == null || mManageCom.trim().equals("")) {
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输全局公共数据ManageCom失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        if (mTransferData == null) {
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mStatDate = (String) mTransferData.getValueByName("StatDate");
        if (mStatDate == null) {
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中StatYear失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        mEndDate = (String) mTransferData.getValueByName("EndDate");
        if (mEndDate == null) {
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中StatMon失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        mOperate = (String) mTransferData.getValueByName("Operate");
        if (mOperate == null) {
            CError tError = new CError();
            tError.moduleName = "PEdorPrintAutoHealthAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中mOperate失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

	/**
	 * checkData
	 * 数据校验
	 */
    private static boolean checkData() {

        return true;
    }

	/**
	 * prepareOutputData
	 * 准备输出数据信息
	 */
    private boolean prepareOutputData() {
        mResult.clear();
        MMap map = new MMap();


        mResult.add(map);
        return true;
    }

	/**
	 * makeFile
	 */
    private boolean makeFile() {
        try {
            //String strFileName = mTableName.substring(3, mTableName.length());
            mfileName = mFilePath + mTableName + ".xml";
            if ("1".equals(mOperate)) {
                PrintWriter out = new PrintWriter(new FileOutputStream(
                        mfileName));
                out.println("<?xml version=\"1.0\" encoding=\"gb2312\" ?>");
                out.flush();
                out.println("<POLDATA>");
                out.flush();

                out.println("  <ROW>");
                out.flush();
                for (int j = 1; j <= mFieldData.size(); j++) {
                    out.print("    <" +
                              (String) mFieldData.getObject(j - 1) +
                              ">");
                    out.print((String) mFieldData.getObject(j - 1));
                    out.println("</" +
                                (String) mFieldData.getObject(j - 1) +
                                ">");
                }
                out.println("  </ROW>");
                out.println("");
                out.flush();

                for (int i = 1; i <= mSSRS.getMaxRow(); i++) {

                    out.println("  <ROW>");
                    out.flush();
                    for (int j = 1; j <= mSSRS.getMaxCol(); j++) {
                        out.print("    <" + (String) mFieldData.getObject(j - 1) +
                                  ">");
                        if (mSSRS.GetText(i, j) == null ||
                            "null".equals(mSSRS.GetText(i, j)) ||
                            "".equals(mSSRS.GetText(i, j))) {

                            out.print("");
                        } else {
                            out.print(mSSRS.GetText(i, j));
                        }
                        out.println("</" + (String) mFieldData.getObject(j - 1) +
                                    ">");
                    }
                    out.println("  </ROW>");
                    out.println("");
                    out.flush();
                }
                out.println("</POLDATA>");
                out.flush();
                out.close();
            }
        } catch (Exception ex) {
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
	 * dealData
	 * 数据准备
	 */
    private boolean dealData() {
        if (!getFilePath()) {
            return false;
        }

        if (!dealPolMain()) {
            return false;
        }

        if (!dealEndoFee()) {
            return false;
        }

        if (!dealPremInfo()) {
            return false;
        }

        if (!dealClaimMain()) {
            return false;
        }

        if (!dealClaimSettled()) {
            return false;
        }

        if (!dealPayDue()) {
            return false;
        }

        if (!dealAgentInfo()) {
            return false;
        }

        if (!dealAgtCode()) {
            return false;
        }

        if (!dealPlanInfo()) {
            return false;
        }

        return true;
    }


	/**
	 * dealPolMain
	 */
    private boolean dealPolMain() {
//        String tSQL = "select * from " + mPolMain + " a where a.Eff_date >='" +
//                      this.mStatDate
//                      + "' and a.Eff_date <='" + this.mEndDate + "'"
//                      + " order by a.PolNo";
        String tSQL = "select * from " + mPolMain + " a ";
        
        mTableName = mPolMain;

        if (!creatFile(tSQL)) {
            return false;
        }
        System.out.println(mTableName + "-----xml over---:" +
                           PubFun.getCurrentTime());

        return true;
    }

	/**
	 * dealEndoFee
	 */
    private boolean dealEndoFee() {

//        String tSQL = "select * from " + mEndoFee + " a where (a.Proc_date >='" +
//                      this.mStatDate
//                      + "' and a.Proc_date <='" + this.mEndDate + "')"
//                      + " or (a.gained_date >='"
//                      + this.mStatDate
//                      + "' and a.gained_date<='" + this.mEndDate + "' and a.Proc_date < '"
//                      + this.mStatDate
//                      + "')"
//                      + " order by a.PolNo";

        String tSQL = "select * from " + mEndoFee + " a";
        
        
        
        mTableName = mEndoFee;
        if (!creatFile(tSQL)) {
            return false;
        }
        System.out.println(mTableName + "-----xml over---:" +
                           PubFun.getCurrentTime());

        return true;
    }

	/**
	 * dealPremInfo
	 */
    private boolean dealPremInfo() {

//        String tSQL = "select * from " + mPremInfo +
//                      " a where a.Gained_date >='" + this.mStatDate
//                      + "' and a.Gained_date  <='" + this.mEndDate + "'"
//                      + " order by a.PolNo";
        String tSQL = "select * from " + mPremInfo;
        
        
        
        mTableName = mPremInfo;
        if (!creatFile(tSQL)) {
            return false;
        }
        System.out.println(mTableName + "-----xml over---:" +
                           PubFun.getCurrentTime());
        return true;
    }

	/**
	 * dealClaimMain
	 */
    private boolean dealClaimMain() {

//        String tSQL = "select distinct a.* from " + mClaimMain + " a ," +
//                      mClaimSettled + " b"
//                      +
//                      " where a.CaseNo =b.CaseNo and a.GP_Type=b.GP_Type and a.polNo=b.polNo"
//                      + " and ((a.Docu_date >='" + this.mStatDate
//                      + "' and  a.Docu_date <='" + this.mEndDate + "' ) or "
//                      + " (  b.Gained_date >='" + this.mStatDate
//                      + "' and  b.Gained_date <='" + this.mEndDate + "') or "
//                      + " (  a.Docu_date <'" + this.mStatDate
//                      + "' and  b.Gained_date >'" + this.mEndDate + "') )"
//                      ;

        String tSQL = "select  a.* from " + mClaimMain 
        ;
        
        
        
        mTableName = mClaimMain;
        if (!creatFile(tSQL)) {
            return false;
        }
        System.out.println(mTableName + "-----xml over---:" +
                           PubFun.getCurrentTime());

        return true;
    }

	/**
	 * dealClaimSettled
	 */
    private boolean dealClaimSettled() {

//        String tSQL = "select distinct b.* from " + mClaimMain + " a ," +
//                      mClaimSettled + " b"
//                      + " where a.CaseNo =b.CaseNo and a.GP_Type=b.GP_Type and a.polNo=b.polNo and ((a.Docu_date >='" +
//                      this.mStatDate
//                      + "' and  a.Docu_date <='" + this.mEndDate + "' ) or "
//                      + " (  b.Gained_date >='" + this.mStatDate
//                      + "' and  b.Gained_date <='" + this.mEndDate + "') or "
//                      + " (  a.Docu_date <'" + this.mStatDate
//                      + "' and  b.Gained_date >'" + this.mEndDate + "') )"
//                      ;

        String tSQL = "select  b.* from " +
        mClaimSettled + " b"
       
        ;

    	
    	
        mTableName = mClaimSettled;
        if (!creatFile(tSQL)) {
            return false;
        }
        System.out.println(mTableName + "-----xml over---:" +
                           PubFun.getCurrentTime());

        return true;
    }

	/**
	 * dealPayDue
	 */
    private boolean dealPayDue() {

        String tSQL = "select * from " + mPayDue;
        mTableName = mPayDue;
        if (!creatFile(tSQL)) {
            return false;
        }
        System.out.println(mTableName + "-----xml over---:" +
                           PubFun.getCurrentTime());

        return true;
    }

	/**
	 * dealAgentInfo
	 */
    private boolean dealAgentInfo() {

        String tSQL = "select * from " + mAgentInfo
                      + " where length( AGENTNO ) > 1 order by Agentno";
        mTableName = mAgentInfo;
        if (!creatFile(tSQL)) {
            return false;
        }
        System.out.println(mTableName + "-----xml over---:" +
                           PubFun.getCurrentTime());

        return true;
    }

	/**
	 * dealAgtCode
	 */
    private boolean dealAgtCode() {

        String tSQL = "select * from " + mAgtCode
                      + " where length( agt_code ) > 1 order by agt_code";
        mTableName = mAgtCode;
        if (!creatFile(tSQL)) {
            return false;
        }
        System.out.println(mTableName + "-----xml over---:" +
                           PubFun.getCurrentTime());

        return true;
    }

	/**
	 * dealPlanInfo
	 */
    private boolean dealPlanInfo() {

        String tSQL = "select * from " + mPlanInfo
                      + " order by plan_code";
        mTableName = mPlanInfo;
        if (!creatFile(tSQL)) {
            return false;
        }
        System.out.println(mTableName + "-----xml over---:" +
                           PubFun.getCurrentTime());

        return true;
    }

	/**
	 * creatFile
	 * @param String tSQL
	 */
    private boolean creatFile(String tSQL) {

        if ("1".equals(mOperate)) {
            ExeSQL tExeSQL = new ExeSQL();
            mSSRS = tExeSQL.execSQL(tSQL);
            if (tExeSQL.mErrors.needDealError()) {
                CError tError = new CError();
                tError.moduleName = "MakeXMLBL";
                tError.functionName = "creatFile";
                tError.errorMessage = "查询XML数据" + mTableName + "出错！";
                this.mErrors.addOneError(tError);
                return false;
            }

            PolChkExecSql tPolChkExecSql = new PolChkExecSql();
            mFieldData = tPolChkExecSql.execSQL(tSQL);
            if (tPolChkExecSql.mErrors.needDealError()) {
                CError tError = new CError();
                tError.moduleName = "MakeXMLBL";
                tError.functionName = "creatFile";
                tError.errorMessage = "查询XML字段名" + mTableName + "出错！";
                this.mErrors.addOneError(tError);
                return false;
            }
        }
        if (!makeFile()) {
            CError tError = new CError();
            tError.moduleName = "MakeXMLBL";
            tError.functionName = "submitData";
            tError.errorMessage = "数据处理失败MakeXMLBL-->makeFile!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if ("0".equals(mOperate)) {
            if (!xmlTransform()) {
                return false;
            }
        }
        return true;
    }


	/**
	 * getFilePath
	 */
    private boolean getFilePath() {

        LDSysVarDB tLDSysVarDB = new LDSysVarDB();
        tLDSysVarDB.setSysVar("ReportXmlPath");
        if (!tLDSysVarDB.getInfo()) {
            CError tError = new CError();
            tError.moduleName = "PolChkTxtCreateUI";
            tError.functionName = "getFilePath";
            tError.errorMessage = "生成路径出错！";
            this.mErrors.addOneError(tError);
            return false;
        }
        mFilePath = tLDSysVarDB.getSysVarValue();
        if (mFilePath.equals("")) {
            return false;
        }
        tLDSysVarDB.setSysVar("AuditXslPath");
        if (!tLDSysVarDB.getInfo()) {
            CError tError = new CError();
            tError.moduleName = "PolChkTxtCreateUI";
            tError.functionName = "getFilePath";
            tError.errorMessage = "生成路径出错！";
            this.mErrors.addOneError(tError);
            return false;
        }
        mXslFilePath = tLDSysVarDB.getSysVarValue();
        if (mXslFilePath.equals("")) {
            return false;
        }
        String tCurrenDate = PubFun.getCurrentDate();
        String tCurrenTime = PubFun.getCurrentTime();
        String arrCurrenDate[] = tCurrenDate.split("-");
        String arrCurrenTime[] = tCurrenTime.split(":");
        String tFilePathSub = arrCurrenDate[0] + arrCurrenDate[1] +
                              arrCurrenDate[2] + arrCurrenTime[0];
        String tFilePath = mFilePath + tFilePathSub;
        return true;
    }


	/**
	 * makeDirectory
	 * @param String fileName
	 */
    public static boolean makeDirectory(String fileName) {

        File file = new File(fileName);
        return file.mkdir();
    }

	/**
	 * prepareTransferData
	 */
    private static boolean prepareTransferData() {
        return true;
    }


	/**
	 * getResult
	 * 数据返回
	 */
    public VData getResult() {
        mResult = new VData(); 
        return mResult;
    }

	/**
	 * getReturnTransferData
	 */
    public TransferData getReturnTransferData() {
        return mTransferData;
    }

	/**
	 * getErrors
	 */
    public CErrors getErrors() {
        return mErrors;
    }

	/**
	 * xmlTransform
	 */
    public boolean xmlTransform() {
        String strFileName = "";
        try {
            System.out.println( "mfileName=" + mfileName + ":" );
            //    strfilePath = mfileName.substring(0, mfileName.lastIndexOf("/"));
            //System.out.println("txtPath:" + strfilePath);
            String xslPath = mXslFilePath + mTableName + ".xsl";
            System.out.println("xslPath:" + xslPath);

            File fSource = new File(mfileName);
            File fStyle = new File(xslPath);

            Source source = new StreamSource(fSource);
            Result result = new StreamResult(new FileOutputStream(mfileName.
                    substring(0, mfileName.lastIndexOf("/")) + "/" + mTableName.substring( 3, mTableName.length() ) + ".txt"));
            Source style = new StreamSource(fStyle);

            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer(style);

            transformer.transform(source, result);

            System.out.println("Transform Success!");
        } catch (Exception e) {
            e.printStackTrace();
            CError tError = new CError();
            tError.moduleName = "WriteToFileBLS";
            tError.functionName = "SimpleTransform";
            tError.errorMessage = "Xml处理失败!!";
            this.mErrors.addOneError(tError);
            return false;
        }

        return true;
    }

	/**
	 * main
	 * 应用测试
	 * @param String[] args
	 */
    public static void main(String[] args) {
        VData tVData = new VData();
        GlobalInput mGlobalInput = new GlobalInput();
        TransferData mTransferData = new TransferData();

        mGlobalInput.Operator = "circ";
        mGlobalInput.ComCode = "86";
        mGlobalInput.ManageCom = "86";
        mTransferData.setNameAndValue("StatDate", "2005-01-01");
        mTransferData.setNameAndValue("EndDate", "2005-10-20");
        mTransferData.setNameAndValue("Operate", "1");

        tVData.add(mGlobalInput);
        tVData.add(mTransferData);

        PolChkTxtCreateUI tPolChkTxtCreateUI = new PolChkTxtCreateUI();
        try {
            if (tPolChkTxtCreateUI.submitData(tVData)) {
//                VData tResult = new VData();
            } else {
                System.out.println(tPolChkTxtCreateUI.mErrors.getError(0).
                                   errorMessage);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}

/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.workflow.circ;

// Imported JAVA API for XML Parsing 1.0 classes
// Imported Serializer classes
//import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileWriter;

import com.sinosoft.lis.db.LDSysVarDB;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.msreport.CircAutoImportBL;
import com.sinosoft.msreport.CircParserXml;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;
import com.sinosoft.workflowengine.AfterInitService;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

/**
 * <p>Title: Web业务系统</p>
 * <p>Description: 投保业务逻辑处理类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author zy
 * @version 1.0
 */
public class ReportFBImportAfterInitService implements AfterInitService
{
//  @Fields
    /** 错误处理类，每个需要错误处理的类中都放置该类 */
    public CErrors mErrors = new CErrors();
    /** 往前面传输数据的容器 */
    private VData mResult = new VData();
    /** 往后面传输数据的容器 */
    private VData mInputData;
    /**内存文件暂存*/
    private org.jdom.Document myDocument;
//    private org.jdom.Element myElement;
    private GlobalInput mGlobalInput = new GlobalInput();

    private TransferData mTransferData = new TransferData();

//    private String mDutyFlag = "0";
//    private InputStream DataIn;

    private String mImportFileName;
    private String mConfigFileName;
    private String XmlFileName;

//    private String BatchNo;

//数据Xml解析节点描述
    private String mFilePath = "C:/Lis/";
//    private String ParseRootPath = "/DATASET/BATCHID";
//    private static String PATH_GRPPOLNO =
//            "/DATASET/TABLE/ROW/FIELD/GrpPolNo[position()=1]";
//    private String ParsePath = "/DATASET/TABLE/ROW";
//    private GrpPolImpInfo m_GrpPolImpInfo = new GrpPolImpInfo();
//    private String[] m_strDataFiles = null;
//    private String[] mHead = null;
//    private String[][] mData = null;
//    private String mTable = null;
//    private int nRow;
//    private int nCol;

    //     @Constructors
    public ReportFBImportAfterInitService()
    {
//    bulidDocument();
    }

    public ReportFBImportAfterInitService(String tFileName,
                                          String tConfigfileName)
    {
        mImportFileName = tFileName;
        mConfigFileName = tConfigfileName;
    }

    public boolean submitData(VData cInputData, String cOperate)
    {
        mInputData = (VData) cInputData.clone();
        if (!getInputData())
        {
            return false;
        }
        //取上传文件名
        if (!this.checkData())
        {
            return false;
        }
        //开始准备解析xls为xml
        try
        {
            if (!this.parseXMLandExcel())
            {
                return false;
            }
            System.out.println("ReportFBImportAfterInitService生成Xml成功:" +
                               PubFun.getCurrentTime());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "CircAutoImportBL";
            tError.functionName = "submitData";
            tError.errorMessage = "导入文件格式有误!" + ex.getMessage();
            this.mErrors.addOneError(tError);
            return false;
        }
        System.out.println("结束时间:" + PubFun.getCurrentTime());
        return true;
    }

    /**
     * 得到传入数据
     */
    private boolean getInputData()
    {
        mGlobalInput = (GlobalInput) mInputData.getObjectByObjectName(
                "GlobalInput", 0);
        mTransferData = (TransferData) mInputData.getObjectByObjectName(
                "TransferData", 0);
        if (mGlobalInput == null)
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "ReportFBImportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "无操作员信息，请重新登录!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //获得业务数据
        if (mTransferData == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReportFBImportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        String mStatYear = (String) mTransferData.getValueByName("StatYear");
        if (mStatYear == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReportFBImportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中StatYear失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        String mStatMon = (String) mTransferData.getValueByName("StatMon");
        if (mStatMon == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReportFBImportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中StatMon失败!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //获得当前工作任务的任务ID
        String mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@错误处理
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReportFBImportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "前台传输业务数据中MissionID失败!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * 校验传输数据
     * @return
     */
    private boolean checkData()
    {

        mImportFileName = (String) mTransferData.getValueByName("FileName");
        mConfigFileName = (String) mTransferData.getValueByName(
                "ConfigFileName");
        if (mImportFileName == null || mImportFileName.trim().equals(""))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "ReportFBImportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "前台传入导入Excel文件名信息丢失!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (mConfigFileName == null || mConfigFileName.trim().equals(""))
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "ParseGuideIn";
            tError.functionName = "checkData";
            tError.errorMessage = "前台传入的XML配置文件名信息丢失!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //确定导入的物理源EXCEL文件（如：ExcelImportLFFinace.xml）已到指定目的地存在

        if (!this.checkImportFile())
        {
            return false;
        }
        int i = 0;
        //确定配置文件（如：ExcelImportLFFinaceConfig.xml）存在
        if (!this.checkImportConfigFile())
        {
            return false;
        }

        return true;
    }

    /**
     * 得到生成文件路径
     * @return
     */
    private boolean getFilePath()
    {
        LDSysVarDB tLDSysVarDB = new LDSysVarDB();
        tLDSysVarDB.setSysVar("TranDataPath");
        if (!tLDSysVarDB.getInfo())
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "ParseXML";
            tError.functionName = "getFilePath";
            tError.errorMessage = "缺少文件导入路径!";
            this.mErrors.addOneError(tError);
            return false;
        }
        else
        {
            mFilePath = tLDSysVarDB.getSysVarValue();
        }

        return true;
    }

    /**
     * 检验文件是否存在
     * @return
     */
    private boolean checkXmlFile()
    {
//    XmlFileName = (String)mTransferData.getValueByName("FileName");
        File tFile = new File(XmlFileName);
        if (!tFile.exists())
        {
            LDSysVarDB tLDSysVarDB = new LDSysVarDB();
            tLDSysVarDB.setSysVar("TranDataPath");
            if (!tLDSysVarDB.getInfo())
            {
                // @@错误处理
                CError tError = new CError();
                tError.moduleName = "ParseXML";
                tError.functionName = "checkData";
                tError.errorMessage = "缺少文件导入路径!";
                this.mErrors.addOneError(tError);
                return false;
            }
            else
            {
                mFilePath = tLDSysVarDB.getSysVarValue();
            }

            File tFile1 = new File(mFilePath);
            if (!tFile1.exists())
            {
                tFile1.mkdirs();
            }
            XmlFileName = mFilePath + XmlFileName;
            File tFile2 = new File(XmlFileName);
            if (!tFile2.exists())
            {
                // @@错误处理
                CError tError = new CError();
                tError.moduleName = "ReportFBImportAfterInitService";
                tError.functionName = "checkData";
                tError.errorMessage = "请上传相应的数据文件到指定路径" + mFilePath + "!";
                this.mErrors.addOneError(tError);
                return false;
            }
        }
        return true;
    }

    /**
     * 检查导入配置文件是否存在
     * @return
     */
    private boolean checkImportConfigFile()
    {
        this.getFilePath();
        File tFile1 = new File(mFilePath);
        if (!tFile1.exists())
        {
            tFile1.mkdirs();
        }

        String tConfigFileName = mFilePath + mConfigFileName;
        File tFile2 = new File(tConfigFileName);
        if (!tFile2.exists())
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "ReportFBImportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "请上传配置文件到指定路径" + mFilePath + "!";
            this.mErrors.addOneError(tError);
            return false;
        }
        System.out.println("物理导入Excel文件的配置模板文件:" + mConfigFileName + "在" +
                           mFilePath + "下已存在！");
        return true;
    }

    /**
     * 初始化上传文件
     * @return
     */
    private boolean checkImportFile()
    {
        this.getFilePath();
        String tFileName = mFilePath + mImportFileName;
        File tFile = new File(tFileName);
        if (!tFile.exists())
        {
            // @@错误处理
            CError tError = new CError();
            tError.moduleName = "ReportFBImportAfterInitService";
            tError.functionName = "checkImportFile";
            tError.errorMessage = "未上传文件到指定路径" + mFilePath + "!";
            this.mErrors.addOneError(tError);
            return false;
        }
        System.out.println("物理导入Excel文件:" + mImportFileName + "在路径" + mFilePath +
                           "下已存在！");
        return true;
    }

    /**
     * 解析操作
     * @return
     */
    private boolean parseXMLandExcel() throws Exception
    {

        //更据配置模板，解析EXCEL文件，更据配置模板提取信息生成配置模板指定格式的XML文件
        CircParserXml tCircParserXml = new CircParserXml();
        //设置文件输入输出路径
        tCircParserXml.setFilePathName(mFilePath);
        //设置物理源EXCEL文件名
        String tFileName = getDefaultName(mImportFileName);
        if (!tCircParserXml.setDataExcelFileName(tFileName))
        {
            mErrors.copyAllErrors(tCircParserXml.mErrors);
            return false;
        }
        //设指物理源EXCEL文件的配置文件名
        String tFileName2 = getDefaultName(mConfigFileName);
        if (!tCircParserXml.setConfigFileName(tFileName2))
        {
            mErrors.copyAllErrors(tCircParserXml.mErrors);
            return false;
        }
        // 转换操作，将磁盘投保文件转换成指定格式的XML文件。
        if (!tCircParserXml.transform())
        {
            mErrors.copyAllErrors(tCircParserXml.mErrors);
            return false;
        }

        //得到生成的目标XML数据
        Element tElement = (Element) tCircParserXml.getXMLTarget();
        if (tElement == null)
        {
            mErrors.copyAllErrors(tCircParserXml.mErrors);
            return false;
        }
//	// 生成的目标XML数据到目标数据库表中
        if (!tCircParserXml.insertIntoPhyTable(tElement))
        {
            mErrors.copyAllErrors(tCircParserXml.mErrors);
            return false;
        }
        else
        {
            if (tCircParserXml.getResult() != null &&
                tCircParserXml.getResult().size() > 0)
            {
                //mResult=tCircParserXml.getResult();
                mResult = tCircParserXml.getResult();
                System.out.println("in ReportFBImportAfterInitService");
            }
        }

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

    /**
     *  输出xml文件，参数为路径，文件名
     * @param pathname
     * @param filename
     */
    private void outputDocumentToFile(String pathname, String filename)
    {
        //setup this like outputDocument
        try
        {
            XMLOutputter outputter = new XMLOutputter("  ", true, "GB2312");
            //output to a file
            String str = pathname + filename + ".xml";
            XmlFileName = str;
            FileWriter writer = new FileWriter(str);
            outputter.output(myDocument, writer);
            writer.close();
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 增加元素
     * @param aElt
     */
    private void addElement(org.jdom.Element aElt)
    {
        this.myDocument.getRootElement().addContent(aElt);
    }


    /**
     * 生成缺省文件名（）
     * @return
     */
    private String getDefaultName(String tFileName)
    {
        String tBatchID = "";
        File tFile = new File(tFileName);
        String name = tFile.getName();
        for (int i = name.length() - 1; i >= 0; i--)
        {
            if (name.substring(i - 1, i).equals("."))
            {
                tBatchID = name.substring(0, i - 1);
                //System.out.println("BatchID:"+tBatchID);
                return tBatchID;
            }
        }
        return tBatchID;
    }


    public String getExtendFileName(String aFileName)
    {
        File tFile = new File(aFileName);
        String aExtendFileName = "";
        String name = tFile.getName();
        for (int i = name.length() - 1; i >= 0; i--)
        {
            if (i < 1)
            {
                i = 1;
            }
            if (name.substring(i - 1, i).equals("."))
            {
                aExtendFileName = name.substring(i, name.length());
                System.out.println("ExtendFileName;" + aExtendFileName);
                return aExtendFileName;
            }
        }
        return aExtendFileName;
    }


    public static void main(String[] args)
    {
        try
        {
            CircAutoImportBL tPGI = new CircAutoImportBL();
//      tPGI.parseVts();
            VData tVData = new VData();
            TransferData tTransferData = new TransferData();
            tTransferData.setNameAndValue("FileName", "LMRiskDefine.xls");
            tTransferData.setNameAndValue("ConfigFileName",
                                          "ExcelImportLMRiskConfig.xml");
            GlobalInput tG = new GlobalInput();
            tG.Operator = "001";
            tG.ManageCom = "86110000";
            tVData.add(tTransferData);
            tVData.add(tG);

            tPGI.submitData(tVData, "");
            if (tPGI.mErrors.getErrorCount() > 0)
            {
                System.out.println(tPGI.mErrors.getError(0).errorMessage);
            }
            //tPGI.outputDocumentToFile("E:/temp/","ProcedureXml");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void buildError(String szFunc, String szErrMsg)
    {
        CError cError = new CError();

        cError.moduleName = "ParseGuideIn";
        cError.functionName = szFunc;
        cError.errorMessage = szErrMsg;
        this.mErrors.addOneError(cError);
    }
}

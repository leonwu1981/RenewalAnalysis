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
 * <p>Title: Webҵ��ϵͳ</p>
 * <p>Description: Ͷ��ҵ���߼�������</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author zy
 * @version 1.0
 */
public class ReportFBImportAfterInitService implements AfterInitService
{
//  @Fields
    /** �������࣬ÿ����Ҫ����������ж����ø��� */
    public CErrors mErrors = new CErrors();
    /** ��ǰ�洫�����ݵ����� */
    private VData mResult = new VData();
    /** �����洫�����ݵ����� */
    private VData mInputData;
    /**�ڴ��ļ��ݴ�*/
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

//����Xml�����ڵ�����
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
        //ȡ�ϴ��ļ���
        if (!this.checkData())
        {
            return false;
        }
        //��ʼ׼������xlsΪxml
        try
        {
            if (!this.parseXMLandExcel())
            {
                return false;
            }
            System.out.println("ReportFBImportAfterInitService����Xml�ɹ�:" +
                               PubFun.getCurrentTime());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            // @@������
            CError tError = new CError();
            tError.moduleName = "CircAutoImportBL";
            tError.functionName = "submitData";
            tError.errorMessage = "�����ļ���ʽ����!" + ex.getMessage();
            this.mErrors.addOneError(tError);
            return false;
        }
        System.out.println("����ʱ��:" + PubFun.getCurrentTime());
        return true;
    }

    /**
     * �õ���������
     */
    private boolean getInputData()
    {
        mGlobalInput = (GlobalInput) mInputData.getObjectByObjectName(
                "GlobalInput", 0);
        mTransferData = (TransferData) mInputData.getObjectByObjectName(
                "TransferData", 0);
        if (mGlobalInput == null)
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "ReportFBImportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "�޲���Ա��Ϣ�������µ�¼!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //���ҵ������
        if (mTransferData == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReportFBImportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ������ʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        String mStatYear = (String) mTransferData.getValueByName("StatYear");
        if (mStatYear == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReportFBImportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������StatYearʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        String mStatMon = (String) mTransferData.getValueByName("StatMon");
        if (mStatMon == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReportFBImportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������StatMonʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }

        //��õ�ǰ�������������ID
        String mMissionID = (String) mTransferData.getValueByName("MissionID");
        if (mMissionID == null)
        {
            // @@������
            //this.mErrors.copyAllErrors( tLCPolDB.mErrors );
            CError tError = new CError();
            tError.moduleName = "ReportFBImportAfterInitService";
            tError.functionName = "getInputData";
            tError.errorMessage = "ǰ̨����ҵ��������MissionIDʧ��!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    /**
     * У�鴫������
     * @return
     */
    private boolean checkData()
    {

        mImportFileName = (String) mTransferData.getValueByName("FileName");
        mConfigFileName = (String) mTransferData.getValueByName(
                "ConfigFileName");
        if (mImportFileName == null || mImportFileName.trim().equals(""))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "ReportFBImportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "ǰ̨���뵼��Excel�ļ�����Ϣ��ʧ!";
            this.mErrors.addOneError(tError);
            return false;
        }
        if (mConfigFileName == null || mConfigFileName.trim().equals(""))
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "ParseGuideIn";
            tError.functionName = "checkData";
            tError.errorMessage = "ǰ̨�����XML�����ļ�����Ϣ��ʧ!";
            this.mErrors.addOneError(tError);
            return false;
        }
        //ȷ�����������ԴEXCEL�ļ����磺ExcelImportLFFinace.xml���ѵ�ָ��Ŀ�ĵش���

        if (!this.checkImportFile())
        {
            return false;
        }
        int i = 0;
        //ȷ�������ļ����磺ExcelImportLFFinaceConfig.xml������
        if (!this.checkImportConfigFile())
        {
            return false;
        }

        return true;
    }

    /**
     * �õ������ļ�·��
     * @return
     */
    private boolean getFilePath()
    {
        LDSysVarDB tLDSysVarDB = new LDSysVarDB();
        tLDSysVarDB.setSysVar("TranDataPath");
        if (!tLDSysVarDB.getInfo())
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "ParseXML";
            tError.functionName = "getFilePath";
            tError.errorMessage = "ȱ���ļ�����·��!";
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
     * �����ļ��Ƿ����
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
                // @@������
                CError tError = new CError();
                tError.moduleName = "ParseXML";
                tError.functionName = "checkData";
                tError.errorMessage = "ȱ���ļ�����·��!";
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
                // @@������
                CError tError = new CError();
                tError.moduleName = "ReportFBImportAfterInitService";
                tError.functionName = "checkData";
                tError.errorMessage = "���ϴ���Ӧ�������ļ���ָ��·��" + mFilePath + "!";
                this.mErrors.addOneError(tError);
                return false;
            }
        }
        return true;
    }

    /**
     * ��鵼�������ļ��Ƿ����
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
            // @@������
            CError tError = new CError();
            tError.moduleName = "ReportFBImportAfterInitService";
            tError.functionName = "checkData";
            tError.errorMessage = "���ϴ������ļ���ָ��·��" + mFilePath + "!";
            this.mErrors.addOneError(tError);
            return false;
        }
        System.out.println("������Excel�ļ�������ģ���ļ�:" + mConfigFileName + "��" +
                           mFilePath + "���Ѵ��ڣ�");
        return true;
    }

    /**
     * ��ʼ���ϴ��ļ�
     * @return
     */
    private boolean checkImportFile()
    {
        this.getFilePath();
        String tFileName = mFilePath + mImportFileName;
        File tFile = new File(tFileName);
        if (!tFile.exists())
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "ReportFBImportAfterInitService";
            tError.functionName = "checkImportFile";
            tError.errorMessage = "δ�ϴ��ļ���ָ��·��" + mFilePath + "!";
            this.mErrors.addOneError(tError);
            return false;
        }
        System.out.println("������Excel�ļ�:" + mImportFileName + "��·��" + mFilePath +
                           "���Ѵ��ڣ�");
        return true;
    }

    /**
     * ��������
     * @return
     */
    private boolean parseXMLandExcel() throws Exception
    {

        //��������ģ�壬����EXCEL�ļ�����������ģ����ȡ��Ϣ��������ģ��ָ����ʽ��XML�ļ�
        CircParserXml tCircParserXml = new CircParserXml();
        //�����ļ��������·��
        tCircParserXml.setFilePathName(mFilePath);
        //��������ԴEXCEL�ļ���
        String tFileName = getDefaultName(mImportFileName);
        if (!tCircParserXml.setDataExcelFileName(tFileName))
        {
            mErrors.copyAllErrors(tCircParserXml.mErrors);
            return false;
        }
        //��ָ����ԴEXCEL�ļ��������ļ���
        String tFileName2 = getDefaultName(mConfigFileName);
        if (!tCircParserXml.setConfigFileName(tFileName2))
        {
            mErrors.copyAllErrors(tCircParserXml.mErrors);
            return false;
        }
        // ת��������������Ͷ���ļ�ת����ָ����ʽ��XML�ļ���
        if (!tCircParserXml.transform())
        {
            mErrors.copyAllErrors(tCircParserXml.mErrors);
            return false;
        }

        //�õ����ɵ�Ŀ��XML����
        Element tElement = (Element) tCircParserXml.getXMLTarget();
        if (tElement == null)
        {
            mErrors.copyAllErrors(tCircParserXml.mErrors);
            return false;
        }
//	// ���ɵ�Ŀ��XML���ݵ�Ŀ�����ݿ����
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
     *  ���xml�ļ�������Ϊ·�����ļ���
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
     * ����Ԫ��
     * @param aElt
     */
    private void addElement(org.jdom.Element aElt)
    {
        this.myDocument.getRootElement().addContent(aElt);
    }


    /**
     * ����ȱʡ�ļ�������
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

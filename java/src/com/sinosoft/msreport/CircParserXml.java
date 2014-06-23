/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.msreport;

//import org.xml.sax.SAXException;
//import org.xml.sax.InputSource;
// Imported JAVA API for XML Parsing 1.0 classes
// Imported Serializer classes
//import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

//import org.apache.xpath.XPathAPI;
//import org.jdom.*;
//import org.jdom.output.*;
//import java.net.*;
import com.f1j.ss.BookModelImpl;
import com.sinosoft.lis.pubfun.MMap;
import com.sinosoft.lis.pubfun.PubCalculator;
import com.sinosoft.utility.CError;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.TransferData;
import com.sinosoft.utility.VData;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.jdom.output.XMLOutputter;


/**
 * <p>Title: Webҵ��ϵͳ </p>
 * <p>Description: ��xls�ļ�������ָ����ʽ��XML�ļ� </p>
 * <p>Copyright: Copyright (c) 2002 </p>
 * <p>Company: Sinosoft </p>
 * @author zy
 * @version 1.0
 * @date 2003-12-04
 */
public class CircParserXml
{
    public CErrors mErrors = new CErrors();
    public VData mResults = new VData();

    // �����ļ��Ĺ���·��
    private String mFilePath = "";
    // ��������Excel�ļ��ľ���·��
    private String mDataExcelFileName = "";
    // �������Ͷ���ļ���·����
    private String m_strPathName = "";

    private String m_strBatchNo = "";
    // �����ļ����ļ���
    private String mConfigXmlFileName = "";

    // ����Sheet��ص���Ϣ��������У�����У���ǰ�������е�
    private TransferData mExcelSheetMaxRowSet = new TransferData();
    private TransferData mExcelSheetMaxColSet = new TransferData();


    private static int ROW_LIMIT = 1000; // һ�δ��������,���Ч��


    //����BookModelImpl����������
    //����ģ������
    private Element mXMLImplConfig = null;
    private Element mXMLMiddleRoot = null;
    private Element mXMLTargetRoot = null;

    //��������ԴEXCEL�ļ�����
    private BookModelImpl mBookModelImplImport = new BookModelImpl();
    private int mExcelSheetNum = 0;
    //Ŀ��XML����
    private Document mXMLImplExport = null;
    //�м�XML����
    private Document mXMLImplMiddle = null;
    private List mSheetList = null;
    private MMap map = new MMap();


    public CircParserXml()
    {

    }

    // ����Ҫ������ļ���
    public boolean setDataExcelFileName(String strFileName)
    {
        mDataExcelFileName = strFileName;
        //m_strPathName = file.getParent();

//	int nPos = strFileName.indexOf('.');
//
//	if( nPos == -1 ) {
//	  nPos = strFileName.length();
//	}
//
//	m_strBatchNo = strFileName.substring(0, nPos);//�õ��ļ���
//
//	nPos = m_strBatchNo.lastIndexOf('\\');
//
//	if( nPos != -1 ) {
//	  m_strBatchNo = m_strBatchNo.substring(nPos + 1);
//	}
//
//	nPos = m_strBatchNo.lastIndexOf('/');
//
//	if( nPos != -1 ) {
//	  m_strBatchNo = m_strBatchNo.substring(nPos + 1);
//	}

        return true;
    }

    public String getDataExcelFileName()
    {
        return mDataExcelFileName;
    }

    public VData getResult()
    {
        return mResults;
    }

    public Element getXMLTarget()
    {
        return mXMLTargetRoot;
    }


    // ���������ļ���
    public boolean setConfigFileName(String strConfigFileName)
    {

        mConfigXmlFileName = strConfigFileName;
        return true;
    }

    // ���������ļ���
    public void setFilePathName(String strConfigFileName)
    {
        File file = new File(strConfigFileName);
        if (!file.exists())
        {
            buildError("setFileName", "ָ����·��" + strConfigFileName + "�����ڣ�");
        }
        mFilePath = strConfigFileName;
    }

//
    public String getConfigFileName()
    {
        return mConfigXmlFileName;
    }


    //��������ģ�壬����EXCEL�ļ�����������ģ����ȡ��Ϣ��������ģ��ָ����ʽ��XML�ļ�
    public boolean transform()
    {
        String strFileName = "";
        int nCount = 0;
        try
        {
            //��ȡ����Excel������Ϣ
            if (!loadDataExcel())
            {
                return false;
            }

            //��ȡXML������Ϣ
            if (!loadConfigXML())
            {
                return false;
            }

            //У�������ļ���XML�����ļ���ƥ��
            if (!checkMatch())
            {
                return false;
            }

            //�����м�XML����Դ��Ϣ
            if (!createMiddleXML())
            {
                return false;
            }
            //����м�XML������Ϣ
            if (!outputDocumentToFile(mFilePath, "Middle" + mConfigXmlFileName,
                                      mXMLImplMiddle))
            {
                return false;
            }

            //����Ŀ��XML������Ϣ
            if (!createTargetXML())
            {
                return false;
            }
            //���Ŀ��XML������Ϣ
            if (!outputDocumentToFile(mFilePath, "Target" + mConfigXmlFileName,
                                      mXMLImplExport))
            {
                return false;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            buildError("transfrom", ex.getMessage());
            return false;
        }
        return true;
    }

    //��������ģ�壬����EXCEL�ļ�����������ģ����ȡ��Ϣ��������ģ��ָ����ʽ��XML�ļ�
    public boolean insertIntoPhyTable(Element tXMLTargetRoot)
    {
        String strFileName = "";
        int nCount = 0;
        try
        {
            //�ͷ���Դ
            Element mXMLImplConfig = null;
            //Ŀ��XML����
            Document mXMLImplExport = null;
            //�м�XML����
            Document mXMLImplMiddle = null;

            //��һPart׼���������ύ��ָ�������ݿ��е�����
            try
            {
                //����XML����ģ�������е�SheetԪ��
                List tSheetList = mXMLTargetRoot.getChildren();
                int tSheetListNum = tSheetList.size();
                for (int nSheetIndex = 0; nSheetIndex < tSheetListNum;
                                       nSheetIndex++)
                {
                    //XML����ģ��������SheetԪ�ر���
                    Element tSheetElement = ((Element) tSheetList.get(
                            nSheetIndex));

                    //XML����ģ�������и�SheetԪ�����ڲ�����Ԫ��
                    List tPartList = tSheetElement.getChildren();
                    //XML����ģ�������и�SheetԪ�����ڲ�PartԪ�ر����У��
                    int tPartListNum = tPartList.size();
                    //XML����ģ�������и�SheetԪ�����ڲ�PartԪ��У��
                    for (int nPartIndex = 0; nPartIndex < tPartListNum;
                                          nPartIndex++)
                    {
                        Element tPartElement = ((Element) tPartList.get(
                                nPartIndex));
                        //XML����ģ��������SheetԪ���ڲ�PartԪ���еĲ����洢����
                        String tPhysicalTableAttributeValue = tPartElement.
                                getAttributeValue("physicaltable");
                        String tNameAttributeValue = tPartElement.getName();

                        if (tPhysicalTableAttributeValue == null ||
                            tPhysicalTableAttributeValue.trim().equals(""))
                        {
                            continue;
                        }
                        else
                        {
                            prepareOutPutData(tPartElement,
                                              tPhysicalTableAttributeValue, map);
                        }
                    }
                }
                if (map.keySet().size() > 0)
                {
//		  VData tVData=new VData();
//		  tVData.add(map) ;
                    mResults.add(map);
                    System.out.println("in CircParserXml");
//		  PubSubmit tPubSubmit = new PubSubmit();
//		  if(!tPubSubmit.submitData(tVData,"UPDATE"))
//		  {
//			buildError("insertIntoPhyTable", tPubSubmit.mErrors.getErrContent());
//			return false;
//		  }
                }

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                buildError("insertIntoPhyTable", ex.getMessage());
                return false;
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            buildError("insertIntoPhyTable", ex.getMessage());
            return false;
        }
        return true;
    }


    /**
     * ����xml���������ɲ���ű�
     * @param tPartElement Element
     * @param tTableName String
     * @param tMap MMap
     */
    private void prepareOutPutData(Element tPartElement, String tTableName,
                                   MMap tMap)
    {
        //���ݱ�������ȡ�����ֶεĺ���
        try
        {
            Connection conn = DBConnPool.getConnection();
            ResultSet tResultSet = null;
            DatabaseMetaData tDatabaseMetaData = null;
            tDatabaseMetaData = conn.getMetaData();
            String tColumns = "";
            String tColumnType = "";
            //ͨ��DatabaseMetData������ȡ���е��ֶ�����]
            System.out.println(tTableName);
            tResultSet = tDatabaseMetaData.getColumns(null, null,
                    tTableName.toUpperCase(), null);
            while (tResultSet.next())
            {
                //��ȡ��ǰ�ֶε����ͣ��ж��Ƿ���char��varchar��varchar2��date��datetime
                tColumnType = tResultSet.getString(6).toLowerCase();
                if (tColumnType.indexOf("char") != -1 ||
                    tColumnType.indexOf("date") != -1)
                {
                    tColumns = tColumns + tResultSet.getString(4).toLowerCase() +
                               ",";
                }
            }
            //��������������ֶ���ɵĴ��ַ���
            tColumns = tColumns.substring(0, tColumns.length() - 1);
            conn.close();
            tResultSet.close();

            List tRowList = (List) ((tPartElement).getChildren());
            int tRowNum = tRowList.size();
//            int tTotalRowCount = 0;
            for (int nRowIndex = 0; nRowIndex < tRowNum; nRowIndex++)
            {
                Element tRowElement = (Element) tRowList.get(nRowIndex);
                //��ʼ������������
                String tInsertSQL = "insert into " + tTableName + " ";
                String tValuesSQL = " values(";
                String tInnerSQL = "(";

                List tRowAttributeList = (List) tRowElement.getAttributes();
                int tAttributeListNum = tRowAttributeList.size();
                //String st=tel.getText();
                for (int nAttributeIndex = 0;
                                           nAttributeIndex < tAttributeListNum;
                                           nAttributeIndex++)
                {
                    org.jdom.Attribute tAttribute = (org.jdom.Attribute)
                            tRowAttributeList.get(
                                    nAttributeIndex);
                    //�ֶ���
                    String tAttributeName = tAttribute.getName();
                    //�ֶ�ֵ
                    String tAttributeValue = tAttribute.getValue();
                    if (nAttributeIndex == 0)
                    {
                        tInnerSQL = tInnerSQL + tAttributeName;
                        //���������ֶ��ڴ��ַ����У�����Ҫ�������������'��Ϣ
                        if (tColumns.indexOf(tAttributeName) != -1)
                        {
                            tValuesSQL = tValuesSQL + "'" + tAttributeValue +
                                         "'";
                        }
                        else
                        {
                            //���Ҫ����ķ��ַ��������ݵ�ֵΪ�գ�����Ҫ���⴦��
                            if (tAttributeValue.trim().compareTo("") != 0 &&
                                tAttributeValue != null)
                            {
                                tValuesSQL = tValuesSQL + "" + tAttributeValue +
                                             "";
                            }
                            else
                            {
                                tValuesSQL = tValuesSQL + "null";
                            }
                        }
                    }
                    else if (nAttributeIndex == tAttributeListNum - 1)
                    {
                        tInnerSQL = tInnerSQL + " ," + tAttributeName + " )";
                        //���������ֶ��ڴ��ַ����У�����Ҫ�������������'��Ϣ
                        if (tColumns.indexOf(tAttributeName) != -1)
                        {
                            tValuesSQL = tValuesSQL + ",'" + tAttributeValue +
                                         "' )";
                        }
                        else
                        {
                            //���Ҫ����ķ��ַ��������ݵ�ֵΪ�գ�����Ҫ���⴦��
                            if (tAttributeValue.trim().compareTo("") != 0 &&
                                tAttributeValue != null)
                            {
                                tValuesSQL = tValuesSQL + "," + tAttributeValue +
                                             " )";
                            }
                            else
                            {
                                tValuesSQL = tValuesSQL + ",null)";
                            }
                        }
                    }
                    else
                    {
                        tInnerSQL = tInnerSQL + "," + tAttributeName + "";
                        //���������ֶ��ڴ��ַ����У�����Ҫ�������������'��Ϣ
                        if (tColumns.indexOf(tAttributeName) != -1)
                        {
                            tValuesSQL = tValuesSQL + ",'" + tAttributeValue +
                                         "'";
                        }
                        else
                        {
                            //���Ҫ����ķ��ַ��������ݵ�ֵΪ�գ�����Ҫ���⴦��
                            if (tAttributeValue.trim().compareTo("") != 0 &&
                                tAttributeValue != null)
                            {
                                tValuesSQL = tValuesSQL + "," + tAttributeValue +
                                             "";
                            }
                            else
                            {
                                tValuesSQL = tValuesSQL + ",null";
                            }
                        }
                    }
                }
                //��ϲ��������Ϣ
                String tAllSQL = tInsertSQL + tInnerSQL + tValuesSQL;
//                System.out.println(tAllSQL);
                tMap.put(tAllSQL, "INSERT");
                tAllSQL = null;
            }

        }
        catch (Exception ex)
        {
            buildError("prepareOutPutData", "��ѯ��ṹʧ�ܣ�");
        }

    }


    // ������˽�к���
    private void buildError(String szFunc, String szErrMsg)
    {
        CError cError = new CError();
        cError.moduleName = "GrpPolVTSParser";
        cError.functionName = szFunc;
        cError.errorMessage = szErrMsg;
        this.mErrors.addOneError(cError);
    }

    /**
     * ��������XML�ļ�����
     * @return boolean
     * @throws Exception
     */
    private boolean loadDataExcel() throws Exception
    {
        if (mDataExcelFileName.equals(""))
        {
            throw new Exception("���ȵ���setFileName��������Ҫ������ļ�����");
        }

        mBookModelImplImport.read(mFilePath + mDataExcelFileName + ".xls",
                                  new com.f1j.ss.ReadParams());
        mExcelSheetNum = mBookModelImplImport.getNumSheets();
        if (mBookModelImplImport.getNumSheets() < 1)
        {
            throw new Exception("������Excel�ļ���������ȱ��Sheet��");
        }
        for (int i = 0; i < mExcelSheetNum; i++)
        {
            String nMaxRow = String.valueOf(getMaxRow(i));
            String nMaxCol = String.valueOf(getMaxCol(i));

            mExcelSheetMaxRowSet.setNameAndValue("SHEET" + i, nMaxRow);
            mExcelSheetMaxColSet.setNameAndValue("SHEET" + i, nMaxCol);
        }
//	getMaxRow(0);
//	getMaxCol(0);
        return true;
    }

    /**
     * ��������XML�ļ�����
     * @return boolean
     */
    private boolean loadConfigXML()
    {
        try
        {
            FileInputStream tFileInputStream = new FileInputStream(mFilePath +
                    mConfigXmlFileName + ".xml");
            DOMBuilder builder = new DOMBuilder();
            mXMLImplConfig = builder.build(tFileInputStream).getRootElement();
            Element root = mXMLImplConfig;
        }
        catch (Exception e)
        {
            System.err.println("XML�������ݶ���ʧ��:" + e.getMessage());
        }
        return true;
    }

    //���xml�ļ�������Ϊ·�����ļ���
    public boolean outputDocumentToFile(String pathname, String filename,
                                        Document tDocument)
    {
        //setup this like outputDocument
        try
        {
            XMLOutputter outputter = new XMLOutputter("  ", true, "GB2312");
            //output to a file
            String str = pathname + filename + ".xml";
            FileWriter writer = new FileWriter(str);
            outputter.output(tDocument, writer);
            writer.close();
            System.err.println("XML�м�����������" + str);
            return true;
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * �����м�XML���ݼ��ϣ�ֻ����Ϊÿ������Sheet����ȡ������Row��Ϣ��
     * @param tFieldElement Element
     * @return String
     */
    private String getParamsFieldValue(Element tFieldElement)
    {
        String tTypeAttributeValue = tFieldElement.getAttributeValue("type");
        if (tTypeAttributeValue == null)
        {
            tTypeAttributeValue = tFieldElement.getAttributeValue("Type");
        }
        if (tTypeAttributeValue == null)
        {
            tTypeAttributeValue = tFieldElement.getAttributeValue("TYPE");
        }

        if (tTypeAttributeValue.trim().equals("0")) //����srcֵ���Զ���ֵ
        {
            String tFieldAttributeValue = tFieldElement.getAttributeValue("src");
            if (tFieldAttributeValue == null ||
                tFieldAttributeValue.trim().equals(""))
            {
                return "";
            }
            else
            {
                return tFieldAttributeValue;
            }
        }

        if (tTypeAttributeValue.trim().equals("1")) //����srcֵ��ֱ�ӵ���������ԴExcel��ȡ��
        {
            String tFieldAttributeValue = tFieldElement.getAttributeValue("src");
            if (tFieldAttributeValue == null ||
                tFieldAttributeValue.trim().equals(""))
            {

            }
        }
        if (tTypeAttributeValue.trim().equals("2")) //Sheet(0,0,3)
        {
            String tFieldAttributeValue = tFieldElement.getAttributeValue("src");
            if (tFieldAttributeValue == null ||
                tFieldAttributeValue.trim().equals(""))
            {
            }
            String tHeader = tFieldAttributeValue.trim().substring(1, 5);
            String tTail = tFieldAttributeValue.trim().substring(4);

        }

        return "";
    }

    /**
     * �����м�XML���ݼ��ϣ�ֻ����Ϊÿ������Sheet����ȡ������Row��Ϣ��
     * @param tRowFieldElement Element
     * @return String
     */
    private String getRowFieldValue(Element tRowFieldElement)
    {
        return "";
    }

    private String[] getParseExcelPositon(String tString)
    {
        try
        {
            String tHeader = tString.trim().substring(0, 5);
            String tTail = tString.trim().substring(5);
            tTail = tTail.substring(1, tTail.length() - 1);
            String[] tSubString = getSpliter(tTail, ",");
            return tSubString;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    private String[] getParseMiddlePosition(String tString)
    {
        try
        {
            String tHeader = tString.trim().substring(0, 3);
            String tTail = tString.trim().substring(3);
            tTail = tTail.substring(1, tTail.length() - 1);
            String[] tSubString = getSpliter(tTail, ",");
            return tSubString;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    private String[] getParseTargetPositon(String tSrcAttributeValueString)
    {
        try
        {
            int tSheetIndex = 0;
            String tstrRowIndex = new String();
            int tRowIndex = 0;
            String tColName = "";

            String tHeader = tSrcAttributeValueString.trim().substring(0, 3);
            String tStrRowIndex = tSrcAttributeValueString.trim().substring(3);
            //tstrRowIndex= tStrRowIndex;
            int i = tStrRowIndex.indexOf(".", 1);
            String tTail = tStrRowIndex.substring(1, i - 1);
            tColName = tStrRowIndex.trim().substring(i + 1, tStrRowIndex.length());
            tRowIndex = Integer.parseInt(tTail);
            String[] tSubString = new String[2];
            tSubString[0] = tTail;
            tSubString[1] = tColName;
            return tSubString;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    private String[] getParseParamsAttributeName(String tParamsAttributeValue)
    {
        try
        {
            String[] tResult = null;
            String tHeader = tParamsAttributeValue.trim().substring(0, 6);
            String tTail = tParamsAttributeValue.trim().substring(6);
            tTail = tTail.substring(1, tTail.length() - 1);
            String[] tSubString = getSpliter(tTail, ";");
            if (tSubString.length == 0)
            {
                return null;
            }
            tResult = new String[tSubString.length];
            for (int i = 0; i < tSubString.length; i++)
            {
                String[] tInnerSubString = getSpliter(tSubString[i], "|");
                String tParam = tInnerSubString[0];
                tResult[i] = tParam;
            }
            return tResult;

        }
        catch (Exception ex)
        {
            return null;
        }
    }

    private String[] getParseParamsAttributeType(String tParamsAttributeValue)
    {
        try
        {
            String[] tResult = null;
            String tHeader = tParamsAttributeValue.trim().substring(0, 6);
            String tTail = tParamsAttributeValue.trim().substring(6);
            tTail = tTail.substring(1, tTail.length() - 1);
            String[] tSubString = getSpliter(tTail, ";");
            if (tSubString.length == 0)
            {
                return null;
            }
            tResult = new String[tSubString.length];
            for (int i = 0; i < tSubString.length; i++)
            {
                String[] tInnerSubString = getSpliter(tSubString[i], "|");
                String tType = tInnerSubString[1];
                tResult[i] = tType;
            }
            return tResult;

        }
        catch (Exception ex)
        {
            return null;
        }
    }

    /**
     * �õ�����Excel����ָ����������Ϣ��
     * @param tSheetIndex int
     * @param tRowIndex int
     * @param tColIndex int
     * @return String
     * @throws Exception
     */
    private String getExcelValue(int tSheetIndex, int tRowIndex, int tColIndex) throws
            Exception
    {
        String tExcelValue = mBookModelImplImport.getText(tSheetIndex,
                tRowIndex, tColIndex);
        if (tExcelValue == null)
        {
            return "";
        }
        else
        {
            return tExcelValue;
        }
    }


    /**
     * �õ�����Excel����ָ����Sheet(nSheetIndex,x,y)��ʽ��������Ϣ��
     * @param tSrcAttributeValueString String
     * @return String
     */
    private String getExcelValue(String tSrcAttributeValueString)
    {
        try
        {
            int tSheetIndex = 0;
            int tRowIndex = 0;
            int tColIndex = 0;
            String tHeader = tSrcAttributeValueString.trim().substring(0, 5);
            String tTail = tSrcAttributeValueString.trim().substring(5);
            tTail = tTail.substring(1, tTail.length() - 1);
            String[] tSubString = getSpliter(tTail, ",");
            tSheetIndex = Integer.parseInt(tSubString[0]);
            tRowIndex = Integer.parseInt(tSubString[1]);
            tColIndex = Integer.parseInt(tSubString[2]);

            String tExcelValue = mBookModelImplImport.getText(tSheetIndex,
                    tRowIndex, tColIndex);
            if (tExcelValue == null)
            {
                return "";
            }
            else
            {
                return tExcelValue;
            }
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    /**
     * �õ�����Excel����ָ����Sheet(nSheetIndex,x,y)��ʽ��������Ϣ��
     * @param tSrcAttributeValueString String
     * @param tSheetIndex int
     * @param tRowIndex int
     * @return String
     */
    private String getExcelValue(String tSrcAttributeValueString,
                                 int tSheetIndex, int tRowIndex)
    {
        try
        {
            int tempSheetIndex = 0;
            int tempRowIndex = 0;
            int tColIndex = 0;
            String tHeader = tSrcAttributeValueString.trim().substring(0, 5);
            String tTail = tSrcAttributeValueString.trim().substring(5);
            tTail = tTail.substring(1, tTail.length() - 1);
            String[] tSubString = getSpliter(tTail, ",");
            tempSheetIndex = Integer.parseInt(tSubString[0]);
            tempRowIndex = Integer.parseInt(tSubString[1]);
            tColIndex = Integer.parseInt(tSubString[2]);

            //ָ��ȡ����������Դ�е�ǰ��ĳ��ֵ
            if (tempSheetIndex != -1)
            {
                tSheetIndex = tempSheetIndex;
            }
            if (tempRowIndex != -1)
            {
                tRowIndex = tempRowIndex;
            }

            String tExcelValue = mBookModelImplImport.getText(tSheetIndex,
                    tRowIndex, tColIndex);
            if (tExcelValue == null)
            {
                return "";
            }
            else
            {
                return tExcelValue;
            }
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    /**
     * У����������Excel����λ���Ƿ�����Sheet(nSheetIndex,x,y)��ʽ��Ҫ��
     * @param tSrcAttributeValueString String
     * @return boolean
     */
    private boolean checkExcelPositionDesc(String tSrcAttributeValueString)
    {

        try
        {
            int tSheetIndex = 0;
            int tRowIndex = 0;
            int tColIndex = 0;

            if (tSrcAttributeValueString.trim().length() < 12) //����:Sheet(0,3,0)
            {
                return false;
            }
            String tHeader = tSrcAttributeValueString.trim().substring(0, 5);
            if (!tHeader.toUpperCase().equals("SHEET"))
            {
                return false;
            }
            String tTail = tSrcAttributeValueString.trim().substring(5);
            tTail = tTail.substring(1, tTail.length() - 1);
            String[] tSubString = getSpliter(tTail, ",");
            if (tSubString == null || tSubString.length != 3)
            {
                return false;
            }
            tSheetIndex = Integer.parseInt(tSubString[0]);
            tRowIndex = Integer.parseInt(tSubString[1]);
            tColIndex = Integer.parseInt(tSubString[2]);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    /**
     * У����������Excel����λ���Ƿ�����Row(2).FirstSubjectNo��ʽ��Ҫ��
     * @param tTransferData TransferData
     * @param tSrcAttributeValueString String
     * @return boolean
     */
    private boolean checkTargetPositionDesc(TransferData tTransferData,
                                            String tSrcAttributeValueString)
    {
        try
        {
            int tSheetIndex = 0;
            String tStrRowIndex = new String();
            int tRowIndex = 0;
            String tColName = "";

            if (tSrcAttributeValueString.trim().length() < 8) //����Row(2).FirstSubjectNo
            {
                return false;
            }
            String tHeader = tSrcAttributeValueString.trim().substring(0, 3);
            if (!tHeader.toUpperCase().equals("ROW"))
            {
                return false;
            }
            tStrRowIndex = tSrcAttributeValueString.trim().substring(3);
            int i = tStrRowIndex.indexOf(".", 1);
            String ttRowIndex = tStrRowIndex.substring(1, i - 1);
            tColName = tStrRowIndex.trim().substring(i + 1, tStrRowIndex.length());
            if (!isParamExist(tTransferData, tColName))
            {
                return false;
            }
            tRowIndex = Integer.parseInt(ttRowIndex);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    /**
     * У�������м����λ���Ƿ�����ROW(tStartRowIndex,tEndRowIndex)��ȡֵ�ķ�ΧҪ��
     * @param tSrcAttributeValueString String
     * @param tTransferData TransferData
     * @param tempTransferData TransferData
     * @return boolean
     */
    private boolean checkTargetPosition(String tSrcAttributeValueString,
                                        TransferData tTransferData,
                                        TransferData tempTransferData)
    {
        try
        {
            int tSheetIndex = 0;
            String tstrRowIndex = new String();
            int tRowIndex = 0;
            String tColName = "";

            if (tSrcAttributeValueString.trim().length() < 8) //����Row(2).FirstSubjectNo
            {
                return false;
            }
            String tHeader = tSrcAttributeValueString.trim().substring(0, 3);
            String tStrRowIndex = tSrcAttributeValueString.trim().substring(3);
            int i = tStrRowIndex.indexOf(".", 1);
            String ttStrRowIndex = tStrRowIndex.substring(1, i - 1);
            tColName = tStrRowIndex.trim().substring(i + 1, tStrRowIndex.length());
            tRowIndex = Integer.parseInt(ttStrRowIndex);
            int a = Integer.parseInt((String) tTransferData.getValueByName(
                    "PARAM0"));
            int b = Integer.parseInt((String) tTransferData.getValueByName(
                    "PARAM1"));

            //У�������Ƿ�Խ��
            if (tRowIndex < 0 || tRowIndex > b - a)
            {
                return false;
            }
            //У���������Ƿ��Ѷ���
            String tString = (String) tempTransferData.getValueByName(tColName);
            if (tString == null)
            {
                return false;
            }
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    /**
     * У����������Excel����λ���Ƿ�����ROW(tStartRowIndex,tEndRowIndex)��ʽ��Ҫ��
     * @param tSrcAttributeValueString String
     * @return boolean
     */
    private boolean checkMiddlePositionDesc(String tSrcAttributeValueString)
    {
        try
        {
            int tColIndex = 0;

            if (tSrcAttributeValueString.trim().length() < 8) //����:Row(0,3)
            {
                return false;
            }
            String tHeader = tSrcAttributeValueString.trim().substring(0, 3);
            if (!tHeader.toUpperCase().equals("ROW"))
            {
                return false;
            }
            String tTail = tSrcAttributeValueString.trim().substring(3);
            tTail = tTail.substring(1, tTail.length() - 1);
            String[] tSubString = getSpliter(tTail, ",");
            if (tSubString == null || tSubString.length != 2)
            {
                return false;
            }
            int tRowEndIndex = Integer.parseInt(tSubString[0]);
            int tRowStartIndex = Integer.parseInt(tSubString[1]);

            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    /**
     * У�������м����λ���Ƿ�����ROW(tStartRowIndex,tEndRowIndex)��ȡֵ�ķ�ΧҪ��
     * @param tStartRowIndex int
     * @param tEndRowIndex int
     * @param tMaxRowIndex int
     * @return boolean
     */
    private boolean checkMiddlePosition(int tStartRowIndex, int tEndRowIndex,
                                        int tMaxRowIndex)
    {
        try
        {
            if (tStartRowIndex < 0 || tEndRowIndex > tMaxRowIndex)
            {
                return false;
            }
            if (tStartRowIndex > tMaxRowIndex && tMaxRowIndex != -1)
            {
                return false;
            }
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

//ȡ��ColConfigԪ���е�FieldԪ�ص�src����ֵ
    private String getMiddleSrcValue(TransferData tTransferData,
                                     TransferData tempTransferData,
                                     int tSheetIndex, int tPartIndex,
                                     int tRowIndex, String tSrcAttributeValue,
                                     String tTypeAttributeValue,
                                     String tParamsAttributeValue)
    {
        String tTrueSrcAttributeValue = "";
        if (tTypeAttributeValue.trim().equals("0")) //����srcֵ���Զ���ֵ
        {
            if (tSrcAttributeValue == null)
            {
                tTrueSrcAttributeValue = "";
            }
            else
            {
                tTrueSrcAttributeValue = tSrcAttributeValue;
            }
        }
        else if (tTypeAttributeValue.trim().equals("1")) //����srcֵ��ֱ�ӵ���������ԴExcel��ĳҳ(tSheetIndex)ĳ��(tRowIndex)ĳ��(tColIndex)ȡ��
        {
            tTrueSrcAttributeValue = getExcelValue(tSrcAttributeValue,
                    tSheetIndex, tRowIndex);
            if (tTrueSrcAttributeValue == null)
            {
                tTrueSrcAttributeValue = "";
            }
        }
        else if (tTypeAttributeValue.trim().equals("2")) //����srcֵ��ͨ������ȡ��
        {
            TransferData temppTransferData = new TransferData();
            //String[] tParamsValue=null;
            String[] tParamsType = null;
            String[] tParamsName = null;
            if (tParamsAttributeValue != null &&
                !tParamsAttributeValue.trim().equals(""))
            {
                tParamsName = getParseParamsAttributeName(tParamsAttributeValue);
                tParamsType = getParseParamsAttributeType(tParamsAttributeValue);
                if (tParamsName != null && tParamsType != null &&
                    tParamsName.length > 0 && tParamsType.length > 0)
                {
                    for (int i = 0; i < tParamsName.length; i++)
                    {
                        String tParamsValue = null;
                        if (tParamsType[i].equals("1"))
                        {
                            tParamsValue = getMiddleParamAttributeValue(
                                    tParamsName[i], tParamsType[i],
                                    tTransferData, tempTransferData,
                                    tSheetIndex, tRowIndex);
                            if (tParamsValue == null)
                            {
                                tParamsValue = "";
                            }
                            temppTransferData.setNameAndValue(tParamsName[i],
                                    tParamsValue);
                        }
                    }
                }
            }
            //0 -- Ĭ�ϣ���ʾ����ΪWhere�Ӿ䡣
            PubCalculator tPubCalculator = new PubCalculator();
            //׼������Ҫ��
            Vector tVector = (Vector) tTransferData.getValueNames();
            if (tVector != null)
            {
                for (int i = 0; i < tVector.size(); i++)
                {
                    String tName = (String) tVector.get(i);
                    String tValue = (String) tTransferData.getValueByName((
                            Object) tName).toString();
                    tPubCalculator.addBasicFactor(tName, tValue);
                }
            }

            tVector = (Vector) tempTransferData.getValueNames();
            if (tVector != null)
            {
                for (int i = 0; i < tVector.size(); i++)
                {
                    String tName = (String) tVector.get(i);
                    String tValue = (String) tempTransferData.getValueByName((
                            Object) tName).toString();
                    tPubCalculator.addBasicFactor(tName, tValue);
                }
            }
            tVector = (Vector) temppTransferData.getValueNames();
            if (tVector != null)
            {
                for (int i = 0; i < tVector.size(); i++)
                {
                    String tName = (String) tVector.get(i);
                    String tValue = (String) temppTransferData.getValueByName((
                            Object) tName).toString();
                    tPubCalculator.addBasicFactor(tName, tValue);
                }
            }
            //׼������SQL
            tPubCalculator.setCalSql(tSrcAttributeValue);
            String strSQL = tPubCalculator.calculate();
            System.out.println("����Ԫ�ص�Src����ֵSQL : " + strSQL);
            if (strSQL == null)
            {
                //buildError("getSrcValue","XML����ģ�������е�"+tSheetIndex+"Sheet�еĵ�"+tPartIndex+"Part�е�DataSourceSetԪ���е�ColConfigԪ�ص�Field��Src����ֵ"+tSrcAttributeValue+"����������Sql�����﷨��������ò���֮ǰδ���壩��");
                return "";
            }
            tTrueSrcAttributeValue = strSQL;
        }
        else if (tTypeAttributeValue.trim().equals("3")) //����srcֵ�Ǵ�ֱ�Ӳ�����ֱ��ȡ��
        {
            String tString = (String) tTransferData.getValueByName(
                    tSrcAttributeValue);
            if (tString == null)
            {
                tTrueSrcAttributeValue = "";
            }
            else
            {
                tTrueSrcAttributeValue = tString;
            }
        }
        else if (tTypeAttributeValue.trim().equals("4")) //����srcֵ�ǴӸ���������ֱ��ȡ�ã�������ȡ�õ��о�����Ϊ�����洢���������������У�
        {
            String tString = (String) tempTransferData.getValueByName(
                    tSrcAttributeValue);
            if (tString == null)
            {
                tTrueSrcAttributeValue = "";
            }
            else
            {
                tTrueSrcAttributeValue = tString;
            }
        }
        return tTrueSrcAttributeValue;
    }


//ȡ��Ŀ������DataDestinationSet�е�RowԪ���е�FieldԪ�ص�src����ֵ
    private String getTargetSrcValue(List tMiddleRowList,
                                     TransferData tTransferData,
                                     TransferData tempTransferData,
                                     int tSheetIndex, int tPartIndex,
                                     int tRowIndex, int tFieldIndex,
                                     String tSrcAttributeValue,
                                     String tTypeAttributeValue,
                                     String tParamsAttributeValue)
    {
        String tTrueSrcAttributeValue = "";
        if (tTypeAttributeValue.trim().equals("0")) //����srcֵ���Զ���ֵ
        {
            if (tSrcAttributeValue == null)
            {
                tTrueSrcAttributeValue = "";
            }
            else
            {
                tTrueSrcAttributeValue = tSrcAttributeValue;
            }
        }
        else if (tTypeAttributeValue.trim().equals("1")) //����srcֵ��ֱ�ӵ���������ԴExcel��ĳҳ(tSheetIndex)ĳ��(tRowIndex)ĳ��(tColIndex)ȡ��
        {
            tTrueSrcAttributeValue = getExcelValue(tSrcAttributeValue,
                    tSheetIndex, tRowIndex);
            if (tTrueSrcAttributeValue == null)
            {
                tTrueSrcAttributeValue = "";
            }
        }
        else if (tTypeAttributeValue.trim().equals("2")) //����srcֵ��ͨ������ȡ��
        {
            TransferData temppTransferData = new TransferData();
            //String[] tParamsValue=null;
            String[] tParamsType = null;
            String[] tParamsName = null;
            if (tParamsAttributeValue != null &&
                !tParamsAttributeValue.trim().equals(""))
            {
                tParamsName = getParseParamsAttributeName(tParamsAttributeValue);
                tParamsType = getParseParamsAttributeType(tParamsAttributeValue);
                if (tParamsName != null && tParamsType != null &&
                    tParamsName.length > 0 && tParamsType.length > 0)
                {
                    for (int i = 0; i < tParamsName.length; i++)
                    {
                        String tParamsValue = null;
                        if (tParamsType[i].equals("1") ||
                            tParamsType[i].equals("5"))
                        {
                            tParamsValue = getTargetParamAttributeValue(
                                    tMiddleRowList, tParamsName[i],
                                    tParamsType[i], tTransferData,
                                    tempTransferData, tSheetIndex, tRowIndex);
                            if (tParamsValue == null)
                            {
                                tParamsValue = "";
                            }
                            temppTransferData.setNameAndValue(tParamsName[i],
                                    tParamsValue);
                        }
                    }
                }
            }
            //0 -- Ĭ�ϣ���ʾ����ΪWhere�Ӿ䡣
            PubCalculator tPubCalculator = new PubCalculator();
            //׼������Ҫ��
            Vector tVector = (Vector) tTransferData.getValueNames();
            if (tVector != null)
            {
                for (int i = 0; i < tVector.size(); i++)
                {
                    String tName = (String) tVector.get(i);
                    String tValue = (String) tTransferData.getValueByName((
                            Object) tName).toString();
                    tPubCalculator.addBasicFactor(tName, tValue);
                }
            }
            tVector = (Vector) tempTransferData.getValueNames();
            if (tVector != null)
            {
                for (int i = 0; i < tVector.size(); i++)
                {
                    String tName = (String) tVector.get(i);
                    String tValue = (String) tempTransferData.getValueByName((
                            Object) tName).toString();
                    tPubCalculator.addBasicFactor(tName, tValue);
                }
            }
            tVector = (Vector) temppTransferData.getValueNames();
            if (tVector != null)
            {
                for (int i = 0; i < tVector.size(); i++)
                {
                    String tName = (String) tVector.get(i);
                    String tValue = (String) temppTransferData.getValueByName((
                            Object) tName).toString();
                    tPubCalculator.addBasicFactor(tName, tValue);
                }
            }
            //׼������SQL
            tPubCalculator.setCalSql(tSrcAttributeValue);
            String strSQL = tPubCalculator.calculate();
            System.out.println("����ColConfigԪ�ص�FieldԪ�ص�Src����ֵSQL : " + strSQL);
            if (strSQL == null)
            {
                //buildError("getTargetSrcValue","XML����ģ�������е�"+tSheetIndex+"Sheet�еĵ�"+tPartIndex+"Part�е�DataDestinationSetԪ���еĵ�"+tFieldIndex+"RowԪ���ڲ���"+tFieldIndex+"FieldԪ�ص�Src����ֵ"+tSrcAttributeValue+"����������Sql�����﷨��������ò���֮ǰδ���壩��");
                return "";
            }
            tTrueSrcAttributeValue = strSQL;
        }
        else if (tTypeAttributeValue.trim().equals("3")) //����srcֵ�Ǵ�ֱ�Ӳ�����ֱ��ȡ��
        {
            String tString = (String) tTransferData.getValueByName(
                    tSrcAttributeValue);
            if (tString == null)
            {
                tTrueSrcAttributeValue = "";
            }
            else
            {
                tTrueSrcAttributeValue = tString;
            }
        }
        else if (tTypeAttributeValue.trim().equals("4")) //����srcֵ�ǴӸ���������ֱ��ȡ�ã�������ȡ�õ��о�����Ϊ�����洢���������������У�
        {
            String tString = (String) tempTransferData.getValueByName(
                    tSrcAttributeValue);
            if (tString == null)
            {
                tTrueSrcAttributeValue = "";
            }
            else
            {
                tTrueSrcAttributeValue = tString;
            }
        }
        else if (tTypeAttributeValue.trim().equals("5")) //����srcֵ���м���������ֱ��ȡ�ã���Row(2).FirstSubjectNo��
        {

            String[] tSubString = getParseTargetPositon(tSrcAttributeValue);
            //String tString=(String)tempTransferData.getValueByName(tSrcAttributeValue);
            int tMiddleRowIndex = Integer.parseInt(tSubString[0]);
            String tString = ((Element) tMiddleRowList.get(tMiddleRowIndex + 1)).
                             getAttributeValue(tSubString[1]);
            if (tString == null)
            {
                tTrueSrcAttributeValue = "";
            }
            else
            {
                tTrueSrcAttributeValue = tString;
            }
        }
        return tTrueSrcAttributeValue;
    }

//ȡ�ò���ֵ
    private String getParamValue(TransferData tTransferData, String tParamName)
    {
        String tString = (String) tTransferData.getValueByName(tParamName);
        if (tString == null)
        {
            return null;
        }
        else
        {
            return tString;
        }
    }

//ȡ�ò���ֵ
    private boolean isParamExist(TransferData tTransferData, String tParamName)
    {
        String tString = (String) tTransferData.getValueByName(tParamName);
        if (tString == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    private String getExcelSrcValue(TransferData tTransferData,
                                    String tSrcAttributeValue,
                                    String tTypeAttributeValue,
                                    String tParamsAttributeValue,
                                    int tSheetIndex, int tPartIndex)
    {
        String tTrueSrcAttributeValue = "";
        if (tTypeAttributeValue.trim().equals("0")) //����srcֵ���Զ���ֵ
        {
            if (tSrcAttributeValue == null)
            {
                tTrueSrcAttributeValue = "";
            }
            else
            {
                tTrueSrcAttributeValue = tSrcAttributeValue;
            }
        }
        else if (tTypeAttributeValue.trim().equals("1")) //����srcֵ��ֱ�ӵ���������ԴExcel��ȡ��
        {
            tTrueSrcAttributeValue = getExcelValue(tSrcAttributeValue.trim());
            if (tSrcAttributeValue == null)
            {
                tTrueSrcAttributeValue = "";
            }
        }
        else if (tTypeAttributeValue.trim().equals("2")) //����srcֵ��ͨ������ȡ��
        {
            TransferData temppTransferData = new TransferData();
            //String[] tParamsValue=null;
            String[] tParamsType = null;
            String[] tParamsName = null;
            if (tParamsAttributeValue != null &&
                !tParamsAttributeValue.trim().equals(""))
            {
                tParamsName = getParseParamsAttributeName(tParamsAttributeValue);
                tParamsType = getParseParamsAttributeType(tParamsAttributeValue);
                if (tParamsName != null && tParamsType != null &&
                    tParamsName.length > 0 && tParamsType.length > 0)
                {
                    for (int i = 0; i < tParamsName.length; i++)
                    {
                        String tParamsValue = null;
                        if (tParamsType[i].equals("1"))
                        {
                            tParamsValue = getExcelParamAttributeValue(
                                    tParamsName[i], tParamsType[i],
                                    tTransferData, tSheetIndex);
                            if (tParamsValue == null)
                            {
                                tParamsValue = "";
                            }
                            temppTransferData.setNameAndValue(tParamsName[i],
                                    tParamsValue);
                        }
                    }
                }
            }
            //0 -- Ĭ�ϣ���ʾ����ΪWhere�Ӿ䡣
            PubCalculator tPubCalculator = new PubCalculator();
            //׼������Ҫ��
            Vector tVector = (Vector) tTransferData.getValueNames();
            if (tVector != null)
            {
                for (int i = 0; i < tVector.size(); i++)
                {
                    String tName = (String) tVector.get(i);
                    String tValue = (String) tTransferData.getValueByName((
                            Object) tName).toString();
                    tPubCalculator.addBasicFactor(tName, tValue);
                }
            }

            tVector = (Vector) temppTransferData.getValueNames();
            if (tVector != null)
            {
                for (int i = 0; i < tVector.size(); i++)
                {
                    String tName = (String) tVector.get(i);
                    String tValue = (String) temppTransferData.getValueByName((
                            Object) tName).toString();
                    tPubCalculator.addBasicFactor(tName, tValue);
                }
            }
            //׼������SQL
            tPubCalculator.setCalSql(tSrcAttributeValue);
            String strSQL = tPubCalculator.calculate();
            System.out.println("����PARAMS�е�Src����SQL : " + strSQL);
            if (strSQL == null)
            {
                //buildError("getExcelSrcValue","XML����ģ�������е�"+tSheetIndex+"Sheet�еĵ�"+tPartIndex+"Part�е�DataSourceSetԪ���е�ParamsԪ�ص�Field��Src����ֵ"+tSrcAttributeValue+"����������Sql�����﷨��������ò���֮ǰδ���壩��");
                return "";
            }
            tTrueSrcAttributeValue = strSQL;
        }

        return tTrueSrcAttributeValue;

    }

    /**
     * �����м�XML���ݼ��ϣ�ֻ����Ϊÿ������Sheet����ȡ������Row��Ϣ��
     * @return boolean
     */
    private boolean createMiddleXML()
    {
        mXMLMiddleRoot = new org.jdom.Element("XmlRoot");
        //Element tXMLImplMiddleRoot = mXMLMiddleRoot;
        try
        {
            //����XML����ģ�������е�SheetԪ��
            List tSheetList = mXMLImplConfig.getChildren();
            int tSheetListNum = tSheetList.size();
            if (tSheetListNum < 1)
            {
                buildError("checkMatch", "XML����ģ��������ȱ��SheetԪ�ص�����");
                return false;
            }

            for (int nSheetIndex = 0; nSheetIndex < tSheetListNum; nSheetIndex++)
            {
                //XML����ģ��������SheetԪ�ر���
                Element tSheetElement = ((Element) tSheetList.get(nSheetIndex));
                //����һ��Sheet�ڵ�
                Element tSheetRootElement = new Element("SHEET");
                tSheetRootElement.addAttribute("id", String.valueOf(nSheetIndex));

                String tDestIndex = tSheetElement.getAttributeValue("dest");
                if (tDestIndex == null || tDestIndex.trim().equals("")) //û����������Ϊ��sheet˳�����ͬ
                {
                    tDestIndex = String.valueOf(nSheetIndex);
                }
                int nDestIndex = Integer.parseInt(tDestIndex);

                //XML����ģ�������и�SheetԪ�����ڲ�����Ԫ��
                List tPartList = tSheetElement.getChildren();
                //XML����ģ�������и�SheetԪ�����ڲ�PartԪ�ر����У��
                int tPartListNum = tPartList.size();
                //XML����ģ�������и�SheetԪ�����ڲ�PartԪ��У��
                for (int nPartIndex = 0; nPartIndex < tPartListNum; nPartIndex++)
                {
                    //XML����ģ��������SheetԪ���ڲ�PartԪ���еĲ����洢����
                    TransferData nParamsValueSet = new TransferData();
                    Element tPartRootElement = new Element("PART");
                    tPartRootElement.addAttribute("id",
                                                  String.valueOf(nPartIndex));

                    int nMaxRow = 0;
                    //XML����ģ��������SheetԪ���ڲ�PartԪ�ر���У��
                    Element tPartElement = ((Element) tPartList.get(nPartIndex));
                    String tPartName = tPartElement.getName();
                    if (tPartName.trim().toUpperCase().equals("PART"))
                    {
                        //�õ�XML����ģ�������и�SheetԪ�����ڲ���PartԪ���еĵ�һ����Ԫ��DataSourceSetԪ���е�һ����ParamsԪ�ؼ����ڲ�Ԫ�ص�Field
                        Element tParamsElement = (Element) (((List) ((((Element) (((
                                List) tPartElement.getChildren()).get(0)))).
                                getChildren())).get(0));
                        String tParamsElementName = tParamsElement.getName();
                        List tParamsFieldList = (List) ((tParamsElement).
                                getChildren());
                        int tParamsFieldNum = tParamsFieldList.size();
                        //���ɲ�����Ϣ

                        //����ϵͳ������
                        nParamsValueSet.setNameAndValue("SYS_FILENAMEPARAM",
                                mDataExcelFileName);
                        //���ɲ����ڵ�
                        Element tSysParamRootElement = new Element("PARAM");
                        tSysParamRootElement.addAttribute("name",
                                "SYS_FILENAMEPARAM");
                        tSysParamRootElement.addAttribute("src",
                                mDataExcelFileName);
                        //���һϵͳ�������ڵ�
                        Element tParamsRootElement = new Element("PARAMS");
                        tParamsRootElement.addContent(tSysParamRootElement);
                        //��������������Ϣ
                        for (int nParamsFieldIndex = 0;
                                nParamsFieldIndex < tParamsFieldNum;
                                nParamsFieldIndex++)
                        {
                            //��ȡXML����ģ�������е�ÿһ��FieldԪ��
                            //��ø�Sheet���������
                            String tSheetMaxRow = "SHEET" +
                                                  String.valueOf(nDestIndex);
                            String tStrMaxRow = (String) mExcelSheetMaxRowSet.
                                                getValueByName(tSheetMaxRow);
                            nMaxRow = Integer.parseInt(tStrMaxRow);
                            int nEndRow = nMaxRow;
                            int nStartRow = 0;

                            Element tFieldElement = ((Element) tParamsFieldList.
                                    get(nParamsFieldIndex));
                            String tFieldName = tFieldElement.getName();
                            //��������в���
                            String tTypeAttributeValue = tFieldElement.
                                    getAttributeValue("type");
                            String tSrcAttributeValue = tFieldElement.
                                    getAttributeValue("src");
                            String tNameAttributeValue = tFieldElement.
                                    getAttributeValue("name");
                            String tParamsAttributeValue = tFieldElement.
                                    getAttributeValue("params");
                            String tRealSrcAttributeValue = getExcelSrcValue(
                                    nParamsValueSet, tSrcAttributeValue,
                                    tTypeAttributeValue, tParamsAttributeValue,
                                    nSheetIndex, nPartIndex);
                            if (tRealSrcAttributeValue == null)
                            {
                                return false;
                            }
                            if (tNameAttributeValue.equals("PARAM1") &&
                                tRealSrcAttributeValue.equals("-1")) //������PARAM1��SrcȡֵΪ-1�ǣ�����ֹ�к�Ϊ�������-1
                            {
                                tRealSrcAttributeValue = String.valueOf(nMaxRow -
                                        1);
                            }
                            nParamsValueSet.setNameAndValue(tNameAttributeValue,
                                    tRealSrcAttributeValue);

                            //���ɲ����ڵ�
                            Element tParamRootElement = new Element("PARAM");
                            tParamRootElement.addAttribute("name",
                                    tNameAttributeValue);
                            tParamRootElement.addAttribute("src",
                                    tRealSrcAttributeValue);
                            //���һ�����ڵ�
                            tParamsRootElement.addContent(tParamRootElement);
                        }

                        //��Ӳ������Ͻڵ�
                        tPartRootElement.addContent(tParamsRootElement);

                        //����XML����ģ�������и�SheetԪ�����ڲ���PartԪ���еĵ�һ����Ԫ��DataSourceSetԪ���еڶ�����ColConfigԪ���ڲ�Ԫ�ص�Field������������Excel�����ȡ�����γ��м������
                        int tStartRow = Integer.parseInt((String)
                                nParamsValueSet.getValueByName("PARAM0"));
                        int tEndRow = Integer.parseInt((String) nParamsValueSet.
                                getValueByName("PARAM1"));
                        Element tColConfigElement = (Element) (((List) ((((
                                Element) (((List) tPartElement.getChildren()).
                                          get(0)))).getChildren())).get(1));
                        String tColConfigName = tColConfigElement.getName();
                        List tColConfigFieldList = (List) (tColConfigElement.
                                getChildren());
                        int tColConfigFieldNum = tColConfigFieldList.size();

                        //��������Row��Ϣ
                        int tID = 0;
                        for (int nRowIndex = tStartRow; nRowIndex <= tEndRow;
                                             nRowIndex++)
                        {
                            //���ɸ��еĸ��ڵ㼰������
                            Element tOneRowRootElement = new Element("ROW");
                            //tOneRowRootElement.addAttribute("name","ROW"+String.valueOf(tID));
                            tID++;
                            //���ɸ��и��ڵ�ĺ��ӽڵ�
                            TransferData tempParamsValueSet = new TransferData(); //���еĸ�������
                            //����XML����ģ�������и�SheetԪ�����ڲ���PartԪ���еĵ�һ����Ԫ��DataSourceSetԪ���еڶ�����ColConfigԪ��Ԫ���ڲ�Ԫ�ص�Field
                            for (int nColConfigFieldIndex = 0;
                                    nColConfigFieldIndex < tColConfigFieldNum;
                                    nColConfigFieldIndex++)
                            {
                                //XML����ģ��������Row�е�FieldԪ�ر���У��
                                Element tFieldElement = ((Element)
                                        tColConfigFieldList.get(
                                                nColConfigFieldIndex));
                                String tFieldName = tFieldElement.getName();
                                String tTypeAttributeValue = tFieldElement.
                                        getAttributeValue("type");
                                String tNameAttributeValue = tFieldElement.
                                        getAttributeValue("name");
                                String tSrcAttributeValue = tFieldElement.
                                        getAttributeValue("src");
                                String tParamsAttributeValue = tFieldElement.
                                        getAttributeValue("params");

                                //���ɸ�Field�ڵ�ĸ��ڵ㼰����name
//			   Element tOneFieldRootElement = new Element("FIELD");
//			   tOneFieldRootElement.addAttribute("name",tNameAttributeValue);
                                if (tParamsAttributeValue == null)
                                {
                                    tParamsAttributeValue = "";
                                }
                                String tTrueSrcAttributeValue =
                                        getMiddleSrcValue(nParamsValueSet,
                                        tempParamsValueSet, nSheetIndex,
                                        nPartIndex, nRowIndex,
                                        tSrcAttributeValue, tTypeAttributeValue,
                                        tParamsAttributeValue);
                                if (tTrueSrcAttributeValue == null)
                                {
                                    return false;
                                }
//		       tOneFieldRootElement.addContent(tTrueSrcAttributeValue);
//              //���һ��Field�ڵ�ĵ����ڵ�Row��
//               tOneRowRootElement.addContent(tOneFieldRootElement);
                                //���һ��Field�ڵ�ĵ����ڵ�Rowĳ��������
                                tOneRowRootElement.addAttribute(
                                        tNameAttributeValue,
                                        tTrueSrcAttributeValue);

                                //��Ӹ����и��еļ������
                                if (tTrueSrcAttributeValue == null)
                                {
                                    tTrueSrcAttributeValue = "";
                                }
//               tempParamsValueSet.setNameAndValue("COL"+nColConfigFieldIndex,tTrueSrcAttributeValue) ;
                                tempParamsValueSet.setNameAndValue(
                                        tNameAttributeValue,
                                        tTrueSrcAttributeValue);

                            }
                            //���һ��Row�ڵ�ĵ����ڵ�Part��
                            tPartRootElement.addContent(tOneRowRootElement);
                        }
                    }
                    tSheetRootElement.addContent(tPartRootElement);
                    System.err.println("��" + nSheetIndex + "Sheet�еĵ�" +
                                       nPartIndex + "PartXML�м���������������");

                }
                mXMLMiddleRoot.addContent(tSheetRootElement);
                System.err.println("��" + nSheetIndex + "SheetXML�м���������������");
            }
            System.err.println("���е�Sheet��XML�м���������������");
            //myDocument = new org.jdom.Document(myElement);
            mXMLImplMiddle = new Document(mXMLMiddleRoot);
        }
        catch (Exception e)
        {
            System.err.println("XML�м���������ʧ��:" + e.getMessage());
            return false;
        }

        return true;

    }

    /**
     * ����Ŀ��XML���ݼ��ϣ�ֻ����Ϊÿ������Sheet����ȡ������Row��Ϣ��
     * @return boolean
     */
    private boolean createTargetXML()
    {
        //mXMLMiddleRoot��¼���м����ݽ��
        mXMLTargetRoot = new org.jdom.Element("XmlRoot");
        try
        {
            //����XML����ģ�������е�SheetԪ��
            List tSheetList = mXMLImplConfig.getChildren();
            List tMiddleSheetList = mXMLMiddleRoot.getChildren();
            int tSheetListNum = tSheetList.size();
            if (tSheetListNum < 1)
            {
                buildError("checkMatch", "XML����ģ��������ȱ��SheetԪ�ص�����");
                return false;
            }

            for (int nSheetIndex = 0; nSheetIndex < tSheetListNum; nSheetIndex++)
            {
                //XML����ģ��������SheetԪ�ر���
                Element tSheetElement = ((Element) tSheetList.get(nSheetIndex));
                Element tMiddleSheetElement = ((Element) tMiddleSheetList.get(
                        nSheetIndex));
                //����һ��Sheet�ڵ�
                Element tSheetRootElement = new Element("SHEET");
                tSheetRootElement.addAttribute("id", String.valueOf(nSheetIndex));

                String tDestIndex = tSheetElement.getAttributeValue("dest");
                if (tDestIndex == null || tDestIndex.trim().equals("")) //û����������Ϊ��sheet˳�����ͬ
                {
                    tDestIndex = String.valueOf(nSheetIndex);
                }
                int nDestIndex = Integer.parseInt(tDestIndex);

                //XML����ģ�������и�SheetԪ�����ڲ�����Ԫ��
                List tPartList = tSheetElement.getChildren();
                List tMiddlePartList = tMiddleSheetElement.getChildren();
                //XML����ģ�������и�SheetԪ�����ڲ�PartԪ�ر����У��
                int tPartListNum = tPartList.size();
                //XML����ģ�������и�SheetԪ�����ڲ�PartԪ��У��
                for (int nPartIndex = 0; nPartIndex < tPartListNum; nPartIndex++)
                {
                    int nMaxRow = 0;
                    nMaxRow = getMaxRow(nSheetIndex);
                    //XML����ģ��������SheetԪ���ڲ�PartԪ�ر���У��
                    Element tPartElement = ((Element) tPartList.get(nPartIndex));
                    Element tMiddlePartElement = ((Element) tMiddlePartList.get(
                            nPartIndex));
                    //XML����ģ��������SheetԪ���ڲ�PartԪ���еĲ����洢����
                    TransferData nParamsValueSet = new TransferData();
                    Element tPartRootElement = new Element("PART");
                    String tPhysicalTableAttributeValue = tPartElement.
                            getAttributeValue("physicaltable");
                    if (tPhysicalTableAttributeValue == null)
                    {
                        tPhysicalTableAttributeValue = "";
                    }

                    tPartRootElement.addAttribute("id",
                                                  String.valueOf(nPartIndex));
                    tPartRootElement.addAttribute("physicaltable",
                                                  String.
                                                  valueOf(
                            tPhysicalTableAttributeValue));

                    String tPartName = tPartElement.getName();
                    if (tPartName.trim().toUpperCase().equals("PART"))
                    {

                        //�õ�XML����ģ�������и�SheetԪ�����ڲ���PartԪ���еĵڶ�������Ԫ��DataDestinationSetԪ���м����ڲ�Ԫ�ص�ROW
                        Element tDataDestinationSetsElement = (Element) ((((
                                Element) (((List) tPartElement.getChildren()).
                                          get(1)))));
                        String tDataDestinationSetsElementElementName =
                                tDataDestinationSetsElement.getName();
                        //Ԥ��ȡ���м��������е�ֱ�Ӳ���Params��nParamsValueSet
                        Element tParamsElement = (Element) ((((Element) (((List)
                                tMiddlePartElement.getChildren()).get(0)))));
                        List tParamList = (List) ((tParamsElement).getChildren());
                        int tParamFieldNum = tParamList.size();
                        for (int nParamFieldIndex = 0;
                                nParamFieldIndex < tParamFieldNum;
                                nParamFieldIndex++)
                        {
                            Element tFieldElement = ((Element) tParamList.get(
                                    nParamFieldIndex));
                            String tFieldName = tFieldElement.getName();
                            String tNameAttributeValue = tFieldElement.
                                    getAttributeValue("name");
                            String tFieldContent = tFieldElement.
                                    getAttributeValue("src");
                            nParamsValueSet.setNameAndValue(tNameAttributeValue,
                                    tFieldContent);
                        }

                        //����XML����ģ��������DataDestinationSet�е�RowԪ�ص�������һ����������
                        List tRowList = (List) ((tDataDestinationSetsElement).
                                                getChildren());
                        List tMiddleRowList = (List) tMiddlePartElement.
                                              getChildren(); //�ӵڶ������ӽڵ㿪ʼ��Row�ڵ�
                        int tRowNum = tRowList.size();
                        int tTotalRowCount = 0;
                        for (int nRowIndex = 0; nRowIndex < tRowNum; nRowIndex++)
                        {
                            //XML����ģ��������DataDestinationSet�е�RowԪ�ص�У��ֻ��Ա���У�飬���������ڲ��ٽ���У��
                            //XML����ģ��������DataDestinationSet�е�RowԪ�ر���У��
                            Element tDataDestinationSetRowElement = ((Element)
                                    tRowList.get(nRowIndex));
                            String tDataDestinationSetRowName =
                                    tDataDestinationSetRowElement.getName();
                            String tTypeAttributeValue =
                                    tDataDestinationSetRowElement.
                                    getAttributeValue("type");
                            if (tTypeAttributeValue.trim().equals("1")) //Rowֱֵ�ӵ��м�����Դ��ȡ�õ�������ROW(startRowIndex,endRowIndex)
                            {
                                String tFieldAttributeValue =
                                        tDataDestinationSetRowElement.
                                        getAttributeValue("src");

                                String[] tSubString = getParseMiddlePosition(
                                        tFieldAttributeValue);
                                int tStartRowIndex = Integer.parseInt(
                                        tSubString[0]);
                                int tEndRowIndex = Integer.parseInt(tSubString[
                                        1]);
                                if (tEndRowIndex == -1)
                                {
                                    tEndRowIndex = tMiddleRowList.size() - 2;
                                }
                                for (; tStartRowIndex <= tEndRowIndex;
                                     tStartRowIndex++)
                                {
                                    Element tRowElement = (Element) (
                                            tMiddleRowList.get(tStartRowIndex +
                                            1));

                                    Element tRowRootElement = new Element("ROW");
                                    List tList = (List) tRowElement.
                                                 getAttributes();
                                    tRowRootElement.setAttributes(tList);
                                    //��Ŀ�������������һ��Row�ڵ�
                                    tPartRootElement.addContent(tRowRootElement);
                                    tTotalRowCount++;
                                }
                            }
                            else if (tTypeAttributeValue.trim().equals("2")) //Rowֵͨ�����㵽��������ԴExcel����м伶�����л��
                            {
                                List tDataDestinationSetRowFieldList =
                                        tDataDestinationSetRowElement.
                                        getChildren();
                                int tDataDestinationSetRowFieldListNum =
                                        tDataDestinationSetRowFieldList.size();
                                //for(int nRowFieldIndex=0;nRowFieldIndex<tDataDestinationSetRowFieldListNum;nRowFieldIndex++)
                                {
                                    Element tOneRowRootElement = new Element(
                                            "ROW");
                                    //tOneRowRootElement.addAttribute("name","ROW"+String.valueOf(tTotalRowCount));
                                    tTotalRowCount++;
                                    //���ɸ��и��ڵ������ֵ��
                                    TransferData tempParamsValueSet = new
                                            TransferData(); //���еĸ�������
                                    //����XML����ģ�������и�SheetԪ�����ڲ���PartԪ���еĵ�һ����Ԫ��DataSourceSetԪ���еڶ�����ColConfigԪ��Ԫ���ڲ�Ԫ�ص�Field
                                    for (int nFieldIndex = 0;
                                            nFieldIndex <
                                            tDataDestinationSetRowFieldListNum;
                                            nFieldIndex++)
                                    {
                                        //XML����ģ��������Row�е�FieldԪ�ر���У��
                                        Element tFieldElement = ((Element)
                                                tDataDestinationSetRowFieldList.
                                                get(nFieldIndex));
                                        String tFieldName = tFieldElement.
                                                getName();
                                        String tTypeFieldAttributeValue =
                                                tFieldElement.getAttributeValue(
                                                "type");
                                        String tNameFieldAttributeValue =
                                                tFieldElement.getAttributeValue(
                                                "name");
                                        String tSrcFieldAttributeValue =
                                                tFieldElement.getAttributeValue(
                                                "src");
                                        String tParamsFieldAttributeValue =
                                                tFieldElement.getAttributeValue(
                                                "params");
                                        if (tParamsFieldAttributeValue == null)
                                        {
                                            tParamsFieldAttributeValue = "";
                                        }
                                        //���ɸ�Field�ڵ�ĸ��ڵ㼰����name
                                        String tTrueSrcAttributeValue =
                                                getTargetSrcValue(
                                                tMiddleRowList, nParamsValueSet,
                                                tempParamsValueSet, nSheetIndex,
                                                nPartIndex, nRowIndex,
                                                nFieldIndex,
                                                tSrcFieldAttributeValue,
                                                tTypeFieldAttributeValue,
                                                tParamsFieldAttributeValue);
                                        if (tTrueSrcAttributeValue == null)
                                        {
                                            return false;
                                        }
                                        //���һ��Field�ڵ�ĵ����ڵ�Rowĳ��������
                                        tOneRowRootElement.addAttribute(
                                                tNameFieldAttributeValue,
                                                tTrueSrcAttributeValue);

                                        //��Ӹ����и��еļ������
                                        if (tTrueSrcAttributeValue == null)
                                        {
                                            tTrueSrcAttributeValue = "";
                                        }
//               tempParamsValueSet.setNameAndValue("COL"+nColConfigFieldIndex,tTrueSrcAttributeValue) ;
                                        tempParamsValueSet.setNameAndValue(
                                                tNameFieldAttributeValue,
                                                tTrueSrcAttributeValue);
                                    }
                                    //���һ��Row�ڵ�ĵ����ڵ�Part��
                                    tPartRootElement.addContent(
                                            tOneRowRootElement);
                                }
                            }
                            else
                            {
                                buildError("checkMatch",
                                           "XML����ģ�������е�" + nSheetIndex +
                                           "SheetԪ���еĵ�" + nPartIndex +
                                           "��PartԪ�ص�DataDestinationSetԪ���е�" +
                                           nRowIndex + "��RowԪ��Type����ֵ����������");
                                return false;
                            }
                        }
                        tSheetRootElement.addContent(tPartRootElement);
                        System.err.println("��" + nSheetIndex + "Sheet�еĵ�" +
                                           nPartIndex + "PartXMLĿ��XML��������������");
                    }

                }
                mXMLTargetRoot.addContent(tSheetRootElement);
                System.err.println("��" + nSheetIndex + "SheetXMLĿ��XML��������������");

            }
            System.err.println("���е�Sheet��Ŀ��XML��������������");
            mXMLImplExport = new Document(mXMLTargetRoot);

        }
        catch (Exception e)
        {
            System.err.println("Ŀ��XML��������ʧ��:" + e.getMessage());
            return false;
        }

        return true;

    }

    /**
     * У���ʽƥ��
     * @param tSheetIndex int
     * @param tRowIndex int
     * @param tColIndex int
     * @return boolean
     * @throws Exception
     */
    private boolean checkExcelPosition(int tSheetIndex, int tRowIndex,
                                       int tColIndex) throws Exception
    {
        if (tSheetIndex > mExcelSheetNum)
        {
            return false;
        }
        String tStrSheetIndex = "SHEET" + String.valueOf(tSheetIndex);
        String tStrMaxRow = (String) mExcelSheetMaxRowSet.getValueByName(
                tStrSheetIndex);
        String tStrMaxCol = (String) mExcelSheetMaxColSet.getValueByName(
                tStrSheetIndex);

        int nMaxRow = Integer.parseInt(tStrMaxRow);
        int nMaxCol = Integer.parseInt(tStrMaxCol);
        if (tRowIndex > nMaxRow)
        {
            return false;
        }
        if (tColIndex > nMaxCol)
        {
            return false;
        }
        return true;
    }


    /**
     * ��������Ԫ���в�����������SQL�����ã���������ʾ��Params����ʱ���������ĸ��������и�ʽƥ��У��
     * @param tParamsSrcAttributeValue String
     * @param tTransferData TransferData
     * @param tSheetIndex int
     * @return boolean
     */
    private boolean checkExcelParamsAttributeDesc(String
                                                  tParamsSrcAttributeValue,
                                                  TransferData tTransferData,
                                                  int tSheetIndex)
    {
        try
        {
            if (tParamsSrcAttributeValue.trim().length() < 8) //����:Params(ParamName|ParamType;ParamName|ParamType);����ΪParams()
            {
                return false;
            }
            String tHeader = tParamsSrcAttributeValue.trim().substring(0, 6);
            if (!tHeader.toUpperCase().equals("PARAMS"))
            {
                return false;
            }

            String tTail = tParamsSrcAttributeValue.trim().substring(6);
            tTail = tTail.substring(1, tTail.length() - 1);
            String[] tSubString = getSpliter(tTail, ";");

            if (tSubString != null && tSubString.length < 1)
            {
                return false;
            }
            for (int i = 0; i < tSubString.length; i++)
            {
                String[] tInnerSubString = getSpliter(tSubString[i], "|");
                if (tInnerSubString == null || tInnerSubString.length < 2)
                {
                    return false;
                }
                String tParam = tInnerSubString[0];
                int tType = Integer.parseInt(tInnerSubString[1]);
                if (tType == 1) //�������磺Sheet(0,1,2)
                {
                    if (tParam == null || tParam.trim().equals(""))
                    {
                        return false;
                    }
                    if (!checkExcelPositionDesc(tParam))
                    {
                        return false;
                    }
                    String[] tPositionIndex = getParseExcelPositon(tParam);
                    int nSheetIndex = Integer.parseInt(tPositionIndex[0]);
                    int nRowIndex = Integer.parseInt(tPositionIndex[1]);
                    int nColindex = Integer.parseInt(tPositionIndex[2]);
                    if (nRowIndex == -1 || nColindex == -1) //ֱ�Ӳ��������ж������ַ����ʱû���е�ǰ�е�ǰ�еĸ���
                    {
                        return false;
                    }
                    if (nSheetIndex == -1)
                    {
                        nSheetIndex = tSheetIndex;
                    }
                    if (!checkExcelPosition(nSheetIndex, nRowIndex, nColindex))
                    {
                        return false;
                    }
                }
                if (tType == 2 || tType == 0) //��֧��param����SQl����ֱ���Զ�ֵ
                {
                    return false;
                }
                if (tType == 3) //ֱ������ǰ���Ѷ����param
                {
                    if (tParam == null || tParam.trim().equals(""))
                    {
                        return false;
                    }
                    String tString = (String) tTransferData.getValueByName(
                            tParam);
                    if (tString == null)
                    {
                        return false;
                    }
                }
            }
            return true;

        }
        catch (Exception ex)
        {
            return false;
        }
    }

    /**
     * �ж���Ԫ���У���Src���Զ��屾������SQL�����ã��������˲������ֽ�������ʾ��Params����ʱ���������ĸ��������и�ʽƥ��У��
     * @param tTransferData TransferData
     * @param tempTransferData TransferData
     * @param tSheetIndex int
     * @param tRowIndex int
     * @param tParamsSrcAttributeValue String
     * @return boolean
     * @throws Exception
     */
    private boolean checkMiddleParamsAttributeDesc(TransferData tTransferData,
            TransferData tempTransferData, int tSheetIndex, int tRowIndex,
            String tParamsSrcAttributeValue) throws Exception
    {
        try
        {
            if (tParamsSrcAttributeValue.trim().length() < 8) //����:Params(ParamName|ParamType;ParamName|ParamType);����ΪParams()
            {
                return false;
            }
            String tHeader = tParamsSrcAttributeValue.trim().substring(0, 6);
            if (!tHeader.toUpperCase().equals("PARAMS"))
            {
                return false;
            }

            String tTail = tParamsSrcAttributeValue.trim().substring(6);
            tTail = tTail.substring(1, tTail.length() - 1);
            String[] tSubString = getSpliter(tTail, ";");

            if (tSubString != null && tSubString.length < 1)
            {
                return false;
            }
            for (int i = 0; i < tSubString.length; i++)
            {
                String[] tInnerSubString = getSpliter(tSubString[i], "|");
                if (tInnerSubString == null || tInnerSubString.length < 2)
                {
                    return false;
                }
                String tParam = tInnerSubString[0];
                int tType = Integer.parseInt(tInnerSubString[1]);
                if (tType == 1) //�������磺Sheet(0,1,2)
                {
                    if (tParam == null || tParam.trim().equals(""))
                    {
                        return false;
                    }
                    if (!checkExcelPositionDesc(tParam))
                    {
                        return false;
                    }
                    String[] tPositionIndex = getParseExcelPositon(tParam);
                    int nSheetIndex = Integer.parseInt(tPositionIndex[0]);
                    int nRowIndex = Integer.parseInt(tPositionIndex[1]);
                    int nColIndex = Integer.parseInt(tPositionIndex[2]);
                    if (nRowIndex == -1) //�ж����ж������ַ����ʱ�����е�ǰ�е�ǰҳ�ĸ���
                    {
                        nRowIndex = tRowIndex;
                    }
                    if (nSheetIndex == -1) //�ж����ж������ַ����ʱ�����е�ǰ�е�ǰҳ�ĸ���
                    {
                        nSheetIndex = tSheetIndex;
                    }
                    if (nColIndex == -1) //�ж����ж������ַ����ʱû�е�ǰ�еĸ���
                    {
                        return false;
                    }
                    if (!checkExcelPosition(nSheetIndex, nRowIndex, nColIndex))
                    {
                        return false;
                    }
                }
                if (tType == 2 || tType == 0) //��֧��param����SQl����ֱ���Զ�ֵ
                {
                    return false;
                }
                if (tType == 3) //ֱ������ǰ���Ѷ����param
                {
                    if (tParam == null || tParam.trim().equals(""))
                    {
                        return false;
                    }
                    String tString = (String) tTransferData.getValueByName(
                            tParam);
                    if (tString == null)
                    {
                        return false;
                    }
                }
                if (tType == 4) //ֱ������ǰ���Ѷ���ĵ�ǰ�и����в���
                {
                    if (tParam == null || tParam.trim().equals(""))
                    {
                        return false;
                    }
                    String tString = (String) tempTransferData.getValueByName(
                            tParam);
                    if (tString == null)
                    {
                        return false;
                    }
                }
            }
            return true;

        }
        catch (Exception ex)
        {
            return false;
        }
    }


    /**
     * �м����ݼ������ж���Ԫ���У���Src���Զ��屾������SQL�����ã��������˲������ֽ�������ʾ��Params����ʱ���������ĸ��������и�ʽƥ��У��
     * @param tParamName String
     * @param tParamType String
     * @param tTransferData TransferData
     * @param tempTransferData TransferData
     * @param tSheetIndex int
     * @param tRowIndex int
     * @return String
     */
    private String getMiddleParamAttributeValue(String tParamName,
                                                String tParamType,
                                                TransferData tTransferData,
                                                TransferData tempTransferData,
                                                int tSheetIndex, int tRowIndex)
    {
        try
        {
            String tParamValue = null;
            if (tParamType.equals("1")) //�������磺Sheet(0,1,2)
            {
                String[] tPositionIndex = getParseExcelPositon(tParamName);
                int nSheetIndex = Integer.parseInt(tPositionIndex[0]);
                int nRowIndex = Integer.parseInt(tPositionIndex[1]);
                int nColIndex = Integer.parseInt(tPositionIndex[2]);
                if (nRowIndex == -1) //�ж����ж������ַ����ʱ�����е�ǰ�е�ǰҳ�ĸ���
                {
                    nRowIndex = tRowIndex;
                }
                if (nSheetIndex == -1) //�ж����ж������ַ����ʱ�����е�ǰ�е�ǰҳ�ĸ���
                {
                    nSheetIndex = tSheetIndex;
                }
                tParamValue = getExcelValue(nSheetIndex, nRowIndex, nColIndex);
                if (tParamValue == null)
                {
                    tParamValue = "";
                }
            }
            if (tParamType.equals("2") || tParamType.equals("0")) //��֧��param����SQl����ֱ���Զ�ֵ
            {
                return null;
            }
            if (tParamType.equals("3")) //ֱ������ǰ���Ѷ����param
            {
                tParamValue = (String) tTransferData.getValueByName(tParamName);
                if (tParamValue == null)
                {
                    tParamValue = "";
                }
            }
            if (tParamType.equals("4")) //ֱ������ǰ���Ѷ���ĵ�ǰ�и����в���
            {
                tParamValue = (String) tempTransferData.getValueByName(
                        tParamName);
                if (tParamValue == null)
                {
                    tParamValue = "";
                }
            }
            return tParamValue;

        }
        catch (Exception ex)
        {
            return null;
        }
    }

    /**
     * ֱ�Ӳ�������Ԫ���У���Src���Զ��屾������SQL�����ã��������˲������ֽ�������ʾ��Params����ʱ���������ĸ��������и�ʽƥ��У��
     * @param tParamName String
     * @param tParamType String
     * @param tTransferData TransferData
     * @param tSheetIndex int
     * @return String
     */
    private String getExcelParamAttributeValue(String tParamName,
                                               String tParamType,
                                               TransferData tTransferData,
                                               int tSheetIndex)
    {
        try
        {
            String tParamValue = null;
            if (tParamType == "1") //�������磺Sheet(0,1,2)
            {
                String[] tPositionIndex = getParseExcelPositon(tParamName);
                int nSheetIndex = Integer.parseInt(tPositionIndex[0]);
                int nRowIndex = Integer.parseInt(tPositionIndex[1]);
                int nColIndex = Integer.parseInt(tPositionIndex[2]);
                if (nSheetIndex == -1) //�ж����ж������ַ����ʱ�����е�ǰ�еĸ���
                {
                    nSheetIndex = tSheetIndex;
                }
                tParamValue = getExcelValue(nSheetIndex, nRowIndex, nColIndex);
                if (tParamValue == null)
                {
                    tParamValue = "";
                }
            }
            if (tParamType == "2" || tParamType == "0") //��֧��param����SQl����ֱ���Զ�ֵ
            {
                return null;
            }
            if (tParamType == "3") //ֱ������ǰ���Ѷ����param
            {
                tParamValue = (String) tTransferData.getValueByName(tParamName);
                if (tParamValue == null)
                {
                    tParamValue = "";
                }
            }
            return tParamValue;

        }
        catch (Exception ex)
        {
            return null;
        }
    }

    /**
     * ֱ�Ӳ�������Ԫ���У���Src���Զ��屾������SQL�����ã��������˲������ֽ�������ʾ��Params����ʱ���������ĸ��������и�ʽƥ��У��
     * @param tMiddleRowList List
     * @param tParamName String
     * @param tParamType String
     * @param tTransferData TransferData
     * @param tempTransferData TransferData
     * @param tSheetIndex int
     * @param tRowIndex int
     * @return String
     */
    private String getTargetParamAttributeValue(List tMiddleRowList,
                                                String tParamName,
                                                String tParamType,
                                                TransferData tTransferData,
                                                TransferData tempTransferData,
                                                int tSheetIndex, int tRowIndex)
    {
        try
        {
            String tParamValue = null;
            if (tParamType.equals("1")) //�������磺Sheet(0,1,2)
            {
                String[] tPositionIndex = getParseExcelPositon(tParamName);
                int nSheetIndex = Integer.parseInt(tPositionIndex[0]);
                int nRowIndex = Integer.parseInt(tPositionIndex[1]);
                int nColIndex = Integer.parseInt(tPositionIndex[2]);
                if (nSheetIndex == -1) //�ж����ж������ַ����ʱ�����е�ǰ�е�ǰҳ�ĸ���
                {
                    nSheetIndex = tSheetIndex;
                }
                tParamValue = getExcelValue(nSheetIndex, nRowIndex, nColIndex);
                if (tParamValue == null)
                {
                    tParamValue = "";
                }
            }
            if (tParamType.equals("2") || tParamType.equals("0")) //��֧��param����SQl����ֱ���Զ�ֵ
            {
                return null;
            }
            if (tParamType.equals("3")) //ֱ������ǰ���Ѷ����param
            {
                tParamValue = (String) tTransferData.getValueByName(tParamName);
                if (tParamValue == null)
                {
                    tParamValue = "";
                }
            }
            if (tParamType.equals("4")) //ֱ������ǰ���Ѷ����param
            {
                tParamValue = (String) tempTransferData.getValueByName(
                        tParamName);
                if (tParamValue == null)
                {
                    tParamValue = "";
                }
            }
            if (tParamType.equals("5")) //�����м���������ǰ���Ѷ����ĳ�е�ĳ��
            {
                String[] tSubString = getParseMiddlePosition(tParamName);
                int tMiddleRowIndex = Integer.parseInt(tSubString[0]);
                String tString = ((Element) tMiddleRowList.get(tMiddleRowIndex +
                        1)).getAttributeValue(tSubString[1]);
                if (tString == null)
                {
                    tParamValue = "";
                }
            }
            return tParamValue;

        }
        catch (Exception ex)
        {
            return null;
        }
    }

    /**
     * Ŀ�������Զ���Ԫ���У���Src���Զ��屾������SQL�����ã��������˲������ֽ�������ʾ��Params����ʱ���������ĸ��������и�ʽƥ��У��
     * @param tTransferData TransferData
     * @param tempTransferData TransferData
     * @param tSheetIndex int
     * @param tRowIndex int
     * @param tParamsSrcAttributeValue String
     * @return boolean
     * @throws Exception
     */
    private boolean checkTargetParamsAttributeDesc(TransferData tTransferData,
            TransferData tempTransferData, int tSheetIndex, int tRowIndex,
            String tParamsSrcAttributeValue) throws Exception
    {
        try
        {
            if (tParamsSrcAttributeValue.trim().length() < 8) //����:Params(ParamName|ParamType;ParamName|ParamType);����ΪParams()
            {
                return false;
            }
            String tHeader = tParamsSrcAttributeValue.trim().substring(0, 6);
            if (!tHeader.toUpperCase().equals("PARAMS"))
            {
                return false;
            }

            String tTail = tParamsSrcAttributeValue.trim().substring(6);
            tTail = tTail.substring(1, tTail.length() - 1);
            String[] tSubString = getSpliter(tTail, ";");

            if (tSubString != null && tSubString.length < 1) //����ΪParams()
            {
                return false;
            }
            for (int i = 0; i < tSubString.length; i++)
            {
                String[] tInnerSubString = getSpliter(tSubString[i], "|");
                if (tInnerSubString == null || tInnerSubString.length < 2)
                {
                    return false;
                }
                String tParam = tInnerSubString[0];
                int tType = Integer.parseInt(tInnerSubString[1]);
                if (tType == 1) //�������磺Sheet(0,1,2)
                {
                    if (tParam == null || tParam.trim().equals(""))
                    {
                        return false;
                    }
                    if (!checkExcelPositionDesc(tParam))
                    {
                        return false;
                    }
                    String[] tPositionIndex = getParseExcelPositon(tParam);
                    int nSheetIndex = Integer.parseInt(tPositionIndex[0]);
                    int nRowIndex = Integer.parseInt(tPositionIndex[1]);
                    int nColIndex = Integer.parseInt(tPositionIndex[2]);
                    if (nSheetIndex == -1) //�ж����ж������ַ����ʱ�����е�ǰҳ�ĸ���
                    {
                        nSheetIndex = tSheetIndex;
                    }
                    if (nRowIndex == -1) //�ж����ж������ַ����ʱû�е�ǰ�еĸ���
                    {
                        return false;
                    }
                    if (nColIndex == -1) //�ж����ж������ַ����ʱû�е�ǰ�еĸ���
                    {
                        return false;
                    }
                    if (!checkExcelPosition(nSheetIndex, nRowIndex, nColIndex))
                    {
                        return false;
                    }
                }
                if (tType == 2 || tType == 0) //��֧��param����SQl����ֱ���Զ�ֵ
                {
                    return false;
                }
                if (tType == 3) //����ǰ���Ѷ����ֱ�Ӳ���param
                {
                    if (tParam == null || tParam.trim().equals(""))
                    {
                        return false;
                    }
                    String tString = (String) tTransferData.getValueByName(
                            tParam);
                    if (tString == null)
                    {
                        return false;
                    }
                }
                if (tType == 4) //ֱ������ǰ���Ѷ���ĵ�ǰ�и����в���
                {
                    if (tParam == null || tParam.trim().equals(""))
                    {
                        return false;
                    }
                    String tString = (String) tempTransferData.getValueByName(
                            tParam);
                    if (tString == null)
                    {
                        return false;
                    }
                }
                if (tType == 5) //�����м���������ǰ���Ѷ����ĳ�е�ĳ��
                {
                    if (tParam == null || tParam.trim().equals(""))
                    {
                        return false;
                    }
                    if (!checkTargetPositionDesc(tempTransferData, tParam))
                    {
                        return false;
                    }
                    if (!checkTargetPosition(tParam, tTransferData,
                                             tempTransferData))
                    {
                        return false;
                    }
                }
            }
            return true;

        }
        catch (Exception ex)
        {
            return false;
        }
    }


    /**
     * У���ʽƥ��
     * @param tString String
     * @param tSpliter String
     * @return String[]
     * @throws Exception
     */
    private String[] getSpliter(String tString, String tSpliter) throws
            Exception
    {
        String[] tSpliterString = null;
        StringTokenizer tStringTokenizer = new StringTokenizer(tString,
                tSpliter);
        if (tStringTokenizer.countTokens() == 0)
        {
            tSpliterString = new String[1];
            tSpliterString[0] = tString;
        }
        else
        {
            int i = 0;
            tSpliterString = new String[tStringTokenizer.countTokens()];
            while (tStringTokenizer.hasMoreTokens())
            {

                String tSubString = tStringTokenizer.nextToken();
                tSpliterString[i] = new String(tSubString);
                i++;
            }
        }
        return tSpliterString;
    }

    /**
     * У��DataSourceSet�ĸ�ʽƥ��
     * @param nSheetIndex int
     * @param nPartIndex int
     * @param nDestSheetIndex int
     * @param tDataSourceSetElement Element
     * @param nParamsValueSet TransferData
     * @return boolean
     * @throws Exception
     */
    private boolean checkDataSourceSetMatch(int nSheetIndex, int nPartIndex,
                                            int nDestSheetIndex,
                                            Element tDataSourceSetElement,
                                            TransferData nParamsValueSet) throws
            Exception
    {
//XML����ģ�������и�SheetԪ�����ڲ���PartԪ���е�DataSourceSetԪ�ر����У��
        String tDataSourceSetName = tDataSourceSetElement.getName();
        if (tDataSourceSetName == null ||
            !tDataSourceSetName.trim().toUpperCase().equals("DATASOURCESET"))
        {
            buildError("checkMatch",
                       "XML����ģ�������е�" + nSheetIndex + "��SheetԪ���еĵ�" + nPartIndex +
                       "��PartԪ�ص�DataSourceSetԪ������������");
            return false;
        }
        //XML����ģ�������и�SheetԪ�����ڲ���PartԪ���еĵ�һ����Ԫ�أ�DataSourceSet���ڲ�Ԫ�ص�У��
        List tDataSourceSetChildenList = tDataSourceSetElement.getChildren();
        //XML����ģ�������и�SheetԪ�����ڲ���PartԪ���е�DataSourceSetԪ�ر����У��
        int tDataSourceSetChildenNum = tDataSourceSetChildenList.size();
        if (tDataSourceSetChildenNum != 2)
        {
            buildError("checkMatch",
                       "XML����ģ�������е�" + nSheetIndex + "��SheetԪ���еĵ�" + nPartIndex +
                       "��PartԪ�ص�DataSourceSetԪ��ȱ�ٱر���Params��ROWԪ�ص�����");
            return false;
        }

        //XML����ģ�������и�SheetԪ�����ڲ���PartԪ���еĵ�һ����Ԫ��DataSourceSetԪ���е�һ����ParamsԪ�ص�У��
        Element tParamsElement = ((Element) tDataSourceSetChildenList.get(0));
        //XML����ģ�������и�SheetԪ�����ڲ���PartԪ���еĵ�һ����Ԫ��DataSourceSetԪ���е�һ����ParamsԪ�������У��
        String tParamsName = tParamsElement.getName();
        if (tParamsName == null ||
            !tParamsName.trim().toUpperCase().equals("PARAMS"))
        {
            buildError("checkMatch",
                       "XML����ģ�������е�" + nSheetIndex + "��SheetԪ���еĵ�" + nPartIndex +
                       "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params������������");
            return false;
        }
        //XML����ģ�������и�SheetԪ�����ڲ���PartԪ���еĵ�һ����Ԫ��DataSourceSetԪ���е�һ����ParamsԪ���ڲ�Ԫ�ص�FieldУ��
        List tParamsFieldList = tParamsElement.getChildren();
        int tParamsFieldNum = tParamsFieldList.size();
        if (tParamsFieldNum < 2)
        {
            buildError("checkMatch",
                       "XML����ģ�������е�" + nSheetIndex + "��SheetԪ���еĵ�" + nPartIndex +
                       "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�е�ȱ�ٱ�Ҫ������FeildԪ����������nameֵΪPARAM0��PARAM1");
            return false;
        }
        for (int nParamsFieldIndex = 0; nParamsFieldIndex < tParamsFieldNum;
                                     nParamsFieldIndex++)
        {
            //XML����ģ��������FieldԪ�ر���У��
            //��ø�Sheet���������
            String tSheetMaxRow = "SHEET" + String.valueOf(nDestSheetIndex);
            String tStrMaxRow = (String) mExcelSheetMaxRowSet.getValueByName(
                    tSheetMaxRow);
            if (tStrMaxRow == null || tStrMaxRow.trim().equals("0"))
            {
                buildError("checkMatch",
                           "XML����ģ�������е�" + nSheetIndex + "��SheetԪ���еĵ�" +
                           nPartIndex +
                           "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�е�ȱ�ٱ�Ҫ������FeildԪ����������nameֵΪPARAM0��PARAM1");
                return false;
            }
            int nMaxRow = Integer.parseInt(tStrMaxRow);
            int nStartRow = 0;
            int nEndRow = nMaxRow;

            Element tFieldElement = ((Element) tParamsFieldList.get(
                    nParamsFieldIndex));
            String tFieldName = tFieldElement.getName();
            if (!tFieldName.trim().toUpperCase().equals("FIELD"))
            {
                buildError("checkMatch",
                           "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                           nPartIndex +
                           "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�еĵ�" +
                           nParamsFieldIndex + "��FeildԪ��������������");
                return false;
            }
            //��һ��FieldԪ��(��ʼ��Ԫ��)У��
            if (nParamsFieldIndex == 0)
            {
                String tTypeAttributeValue = tFieldElement.getAttributeValue(
                        "type");
                if (tTypeAttributeValue == null ||
                    tTypeAttributeValue.trim().equals(""))
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                               nPartIndex +
                               "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�еĵ�һ��FeildԪ��ȱ��type����ֵ������");
                    return false;
                }
                if (!tTypeAttributeValue.trim().equals("0"))
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                               nPartIndex +
                               "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�еĵ�һ��FeildԪ��ȱ��type����ֵ����Ϊ0");
                    return false;
                }
                //���ָ������ʼ����
                String tStrStartRow = tFieldElement.getAttributeValue("src");
                String tStrNameAttributeValue = tFieldElement.getAttributeValue(
                        "name");

                if (tStrStartRow == null || tStrStartRow.trim().equals(""))
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                               nPartIndex +
                               "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�еĵ�һ��FeildԪ�أ�ָ����ʼ��Ԫ�أ�ȱ��src����ֵ����������");
                    return false;
                }
                nStartRow = Integer.parseInt(tStrStartRow);
                // �ж����������е������Sheetҳ���Ƿ�С�ڵ�������EXCEL�����ṩ��Sheetҳ��
                if (nStartRow < 0)
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                               nPartIndex +
                               "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�еĵ�һ��FeildԪ�أ�ָ����ʼ��Ԫ�أ���Scr����ֵ����������С��0");
                    return false;
                }
                if (nStartRow > nMaxRow)
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                               nPartIndex + "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�еĵ�һ��FeildԪ�أ�ָ����ʼ��Ԫ�أ���Scr����ֵ�������������ڸ�Sheet���������");
                    return false;
                }
                if (tStrNameAttributeValue == null ||
                    !tStrNameAttributeValue.trim().equals("PARAM0"))
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                               nPartIndex +
                               "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�еĵ�һ��FeildԪ�أ�ָ����ʼ��Ԫ�أ�name����ֵ��������ΪPARAM0����");
                    return false;
                }
                //��¼ǰ���Ѷ���Ĳ������Ա��������������õ�У��
                nParamsValueSet.setNameAndValue(tStrNameAttributeValue,
                                                tStrStartRow);
            }
            else if (nParamsFieldIndex == 1)
            {
                //�ڶ���FieldԪ�أ�����ֹ��Ԫ�أ�У��
                String tTypeAttributeValue = tFieldElement.getAttributeValue(
                        "type");
                String tStrNameAttributeValue = tFieldElement.getAttributeValue(
                        "name");
                if (tTypeAttributeValue == null ||
                    tTypeAttributeValue.trim().equals(""))
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                               nPartIndex +
                               "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�еĵڶ���FeildԪ��ȱ��type����ֵ������");
                    return false;
                }
                if (!tTypeAttributeValue.trim().equals("0"))
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                               nPartIndex +
                               "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�еĵڶ���FeildԪ��ȱ��type����ֵ����Ϊ0");
                    return false;
                }
                if (tTypeAttributeValue.trim().equals("0"))
                {
                    //���ָ������ֹ����
                    String tStrEndRow = tFieldElement.getAttributeValue("src");
                    if (tStrEndRow == null || tStrEndRow.trim().equals(""))
                    {
                        buildError("checkMatch",
                                   "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                                   nPartIndex +
                                   "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�еĵ�һ��FeildԪ�أ�ָ����ֹ��Ԫ�أ�ȱ��src����ֵ����������");
                        return false;
                    }
                    nEndRow = Integer.parseInt(tStrEndRow);
                    // �ж����������е������Sheetҳ���Ƿ�С�ڵ�������EXCEL�����ṩ��Sheetҳ��
                    if (nEndRow < 0 && nEndRow != -1)
                    {
                        buildError("checkMatch",
                                   "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                                   nPartIndex +
                                   "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�еĵڶ���FeildԪ�أ�ָ����ֹ��Ԫ�أ���Scr����ֵ����������С��0�Ҳ�Ϊ-1");
                        return false;
                    }
                    if (nEndRow > nMaxRow)
                    {
                        buildError("checkMatch",
                                   "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                                   nPartIndex + "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�еĵڶ���FeildԪ�أ�ָ����ֹ��Ԫ�أ���Scr����ֵ�������������ڸ�Sheet���������");
                        return false;
                    }
                    if (nStartRow > nEndRow && nEndRow != -1)
                    {
                        buildError("checkMatch",
                                   "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                                   nPartIndex +
                                   "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�е�ָ����ʼ��Ԫ�ص�Src����ֵ������ֹ��Ԫ�ص�Src����ֵ");
                        return false;
                    }
                    if (nEndRow == -1) //��ʾ��ֹ��Ϊ���һ��
                    {
                        tStrEndRow = String.valueOf(nMaxRow - 1);

                    }
                    if (tStrNameAttributeValue == null ||
                        !tStrNameAttributeValue.trim().equals("PARAM1"))
                    {
                        buildError("checkMatch",
                                   "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                                   nPartIndex +
                                   "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�еĵڶ���FeildԪ�أ�ָ����ֹ��Ԫ�أ�name����ֵ��������ΪPARAM1����");
                        return false;
                    }
                    //��¼ǰ���Ѷ���Ĳ������Ա��������������õ�У��
                    nParamsValueSet.setNameAndValue(tStrNameAttributeValue,
                            tStrEndRow);
                }

            }
            else
            {
                //����FieldԪ��У��
                String tTypeAttributeValue = tFieldElement.getAttributeValue(
                        "type");
                String tStrNameAttributeValue = tFieldElement.getAttributeValue(
                        "name");

                if (tTypeAttributeValue == null ||
                    tTypeAttributeValue.trim().equals(""))
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                               nPartIndex +
                               "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�еĵ�" +
                               nPartIndex + "��FeildԪ��ȱ��type����ֵ������");
                    return false;
                }

                if (tTypeAttributeValue.trim().equals("0")) //����srcֵ���Զ���ֵ
                {
                }
                else if (tTypeAttributeValue.trim().equals("1")) //����srcֵ��ֱ�ӵ���������ԴExcel��ȡ��
                {
                    String tFieldAttributeValue = tFieldElement.
                                                  getAttributeValue("src");
                    if (tFieldAttributeValue == null ||
                        tFieldAttributeValue.trim().equals(""))
                    {
                        buildError("checkMatch",
                                   "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                                   nPartIndex +
                                   "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�еĵ�" +
                                   nPartIndex + "��FeildԪ��ȱ��src����ֵ������");
                        return false;
                    }
                    if (!checkExcelPositionDesc(tFieldAttributeValue)) //����Sheet(0,1,0)
                    {
                        buildError("checkMatch",
                                   "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                                   nPartIndex +
                                   "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�еĵ�" +
                                   nParamsFieldIndex + "��FeildԪ��Src����ֵ��������ʽ����");
                        return false;
                    }
                    String[] tSubString = getParseExcelPositon(
                            tFieldAttributeValue);
                    if (!checkExcelPosition(Integer.parseInt(tSubString[0]),
                                            Integer.parseInt(tSubString[1]),
                                            Integer.parseInt(tSubString[2])))
                    {
                        buildError("checkMatch",
                                   "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                                   nPartIndex +
                                   "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�еĵ�" +
                                   nParamsFieldIndex +
                                   "��FeildԪ��Src����ֵ�����Ĳ���ȡֵ����������Excel��Χ");
                        return false;
                    }

                }
                else if (tTypeAttributeValue.trim().equals("2"))
                {
                    String tFieldAttributeValue = tFieldElement.
                                                  getAttributeValue("src");
                    String tParamsAttributeValue = tFieldElement.
                            getAttributeValue("params");
                    if (tFieldAttributeValue == null ||
                        tFieldAttributeValue.trim().equals(""))
                    {
                        buildError("checkMatch",
                                   "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                                   nPartIndex +
                                   "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�еĵ�" +
                                   nParamsFieldIndex + "��FeildԪ��ȱ��src����ֵ������");
                        return false;
                    }
                    if (tParamsAttributeValue == null ||
                        tParamsAttributeValue.trim().equals(""))
                    {
                    }
                    else //��type=2 ����sql����ʱ��������ʾ������������ʱ��������ӦУ��
                    {
                        if (!checkExcelParamsAttributeDesc(
                                tParamsAttributeValue, nParamsValueSet,
                                nSheetIndex))
                        {
                            buildError("checkMatch",
                                       "XML����ģ�������е�" + nSheetIndex +
                                       "SheetԪ���еĵ�" + nPartIndex +
                                       "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�еĵ�" +
                                       nParamsFieldIndex +
                                       "��FeildԪ��params����ֵ����������");
                            return false;
                        }
                    }
                }
                else
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                               nPartIndex +
                               "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�еĵ�" +
                               nParamsFieldIndex + "��FeildԪ��Type����ֵ����������");
                    return false;
                }
                if (tStrNameAttributeValue == null ||
                    tStrNameAttributeValue.trim().equals(""))
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                               nPartIndex +
                               "��PartԪ�ص�DataSourceSetԪ���е�һԪ��:Params�еĵڶ���FeildԪ�أ�ָ����ֹ��Ԫ�أ�name����ֵ����������Ϊ��");
                    return false;
                }
                //��¼ǰ���Ѷ���Ĳ������Ա��������������õ�У��
                nParamsValueSet.setNameAndValue(tStrNameAttributeValue,
                                                "DEFAUT");
            }
        }

        //XML����ģ�������и�SheetԪ�����ڲ���PartԪ���еĵ�һ����Ԫ��DataSourceSetԪ���еڶ�����ColConfigԪ��Ԫ��У��
        Element tColConfigElement = ((Element) tDataSourceSetChildenList.get(1));
        //XML����ģ�������и�SheetԪ�����ڲ���PartԪ���еĵ�һ����Ԫ��DataSourceSetԪ���е�һ����ColConfigԪ�������У��
        String tRowName = tColConfigElement.getName();
        if (tRowName == null ||
            !tRowName.trim().toUpperCase().equals("COLCONFIG"))
        {
            buildError("checkMatch",
                       "XML����ģ�������е�" + nSheetIndex + "��SheetԪ���еĵ�" + nPartIndex +
                       "��PartԪ�ص�DataSourceSetԪ���еڶ�Ԫ��:ColConfig������������");
            return false;
        }
        //XML����ģ�������и�SheetԪ�����ڲ���PartԪ���еĵ�һ����Ԫ��DataSourceSetԪ���еڶ�����RowԪ���ڲ�Ԫ�ص�FieldУ��
        List tColConfigFieldList = tColConfigElement.getChildren();
        int tColConfigFieldNum = tColConfigFieldList.size();
        if (tColConfigFieldNum < 1)
        {
            buildError("checkMatch",
                       "XML����ģ�������е�" + nSheetIndex + "��SheetԪ���еĵ�" + nPartIndex +
                       "��PartԪ�ص�DataSourceSetԪ���еڶ�Ԫ��:ColConfig�е�Ӧ������һ��FeildԪ��");
            return false;
        }

        //��¼����ǰ���Ѷ����в���
        TransferData nCurrentRowParamsValueSet = new TransferData();
        for (int nRowFieldIndex = 0; nRowFieldIndex < tColConfigFieldNum;
                                  nRowFieldIndex++)
        {
            //XML����ģ��������Row�е�FieldԪ�ر���У��
            Element tFieldElement = ((Element) tColConfigFieldList.get(
                    nRowFieldIndex));
            String tFieldName = tFieldElement.getName();
            if (!tFieldName.trim().toUpperCase().equals("FIELD"))
            {
                buildError("checkMatch",
                           "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                           nPartIndex +
                           "��PartԪ�ص�DataSourceSetԪ���еڶ�Ԫ��:ColConfig�еĵ�" +
                           nRowFieldIndex + "��FeildԪ��������������");
                return false;
            }
            String tTypeAttributeValue = tFieldElement.getAttributeValue("type");
            if (tTypeAttributeValue == null ||
                tTypeAttributeValue.trim().equals(""))
            {
                buildError("checkMatch",
                           "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                           nPartIndex +
                           "��PartԪ�ص�DataSourceSetԪ���еڶ�Ԫ��:ColConfig�еĵ�" +
                           nRowFieldIndex + "��FeildԪ��ȱ��type����ֵ������");
                return false;
            }
            String tNameAttributeValue = tFieldElement.getAttributeValue("name");
            if (tNameAttributeValue == null ||
                tNameAttributeValue.trim().equals(""))
            {
                buildError("checkMatch",
                           "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                           nPartIndex +
                           "��PartԪ�ص�DataSourceSetԪ���еڶ�Ԫ��:ColConfig�еĵ�" +
                           nRowFieldIndex + "��FeildԪ��ȱ��name����ֵ������");
                return false;
            }

            if (tTypeAttributeValue.trim().equals("0")) //����srcֵ���Զ���ֵ
            {}
            else if (tTypeAttributeValue.trim().equals("1")) //����srcֵ��ֱ�ӵ���������Դ��ȡ��
            {
                String tFieldAttributeValue = tFieldElement.getAttributeValue(
                        "src");
                if (tFieldAttributeValue == null ||
                    tFieldAttributeValue.trim().equals(""))
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                               nPartIndex +
                               "��PartԪ�ص�DataSourceSetԪ���еڶ�Ԫ��:ColConfig�еĵ�" +
                               nRowFieldIndex + "��FeildԪ��ȱ��src����ֵ������");
                    return false;
                }
                if (!checkExcelPositionDesc(tFieldAttributeValue))
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                               nPartIndex +
                               "��PartԪ�ص�DataSourceSetԪ���еڶ�Ԫ��:ColConfig�еĵ�" +
                               nRowFieldIndex +
                               "��FeildԪ��src����ֵ����������Ӧ��:COL0,COL1��");
                    return false;
                }
                String[] tSubString = getParseExcelPositon(tFieldAttributeValue);
                if (tSubString[0].trim().equals("-1"))
                {
                    tSubString[0] = String.valueOf(nSheetIndex);
                }
                if (!tSubString[1].trim().equals("-1"))
                {
                    if (!checkExcelPosition(Integer.parseInt(tSubString[0]),
                                            Integer.parseInt(tSubString[1]),
                                            Integer.parseInt(tSubString[2])))
                    {
                        buildError("checkMatch",
                                   "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                                   nPartIndex +
                                   "��PartԪ�ص�DataSourceSetԪ���еڶ�Ԫ��:ColConfig�еĵ�" +
                                   nRowFieldIndex +
                                   "��FeildԪ��src����ֵ������������ȡ����ַ����Excel��ȡ����Χ");
                        return false;
                    }
                }

            }
            else if (tTypeAttributeValue.trim().equals("2"))
            {
                String tFieldAttributeValue = tFieldElement.getAttributeValue(
                        "src");
                String tParamsAttributeValue = tFieldElement.getAttributeValue(
                        "params");
                if (tFieldAttributeValue == null ||
                    tFieldAttributeValue.trim().equals(""))
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                               nPartIndex +
                               "��PartԪ�ص�DataSourceSetԪ���еڶ�Ԫ��:ColConfig�еĵ�" +
                               nRowFieldIndex + "��FeildԪ��ȱ��src����ֵ������");
                    return false;
                }
                if (tParamsAttributeValue != null &&
                    !tParamsAttributeValue.trim().equals(""))
                {
                    if (!checkMiddleParamsAttributeDesc(nParamsValueSet,
                            nCurrentRowParamsValueSet, nSheetIndex,
                            nRowFieldIndex, tParamsAttributeValue))
                    {
                        buildError("checkMatch",
                                   "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                                   nPartIndex +
                                   "��PartԪ�ص�DataSourceSetԪ���еڶ�Ԫ��:ColConfig�еĵ�" +
                                   nRowFieldIndex + "��FeildԪ��params����ֵ����������");
                        return false;
                    }
                }
            }
            else if (tTypeAttributeValue.trim().equals("3")) //����srcֵ�ǴӲ�����ֱ��ȡ��
            {
                String tFieldAttributeValue = tFieldElement.getAttributeValue(
                        "src");
                if (tFieldAttributeValue == null ||
                    tFieldAttributeValue.trim().equals(""))
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                               nPartIndex +
                               "��PartԪ�ص�DataSourceSetԪ���еڶ�Ԫ��:ColConfig�еĵ�" +
                               nRowFieldIndex + "��FeildԪ��ȱ��src����ֵ������");
                    return false;
                }
                if (!isParamExist(nParamsValueSet, tFieldAttributeValue))
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                               nPartIndex +
                               "��PartԪ�ص�DataSourceSetԪ���еڶ�Ԫ��:ColConfig�еĵ�" +
                               nRowFieldIndex + "��FeildԪ��src�������õ�ֱ�Ӳ���δ����");
                    return false;
                }

            }
            else if (tTypeAttributeValue.trim().equals("4")) //����srcֵ�ǴӸ��и���������ֱ��ȡ��
            {
                String tFieldAttributeValue = tFieldElement.getAttributeValue(
                        "src");
                if (tFieldAttributeValue == null ||
                    tFieldAttributeValue.trim().equals(""))
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                               nPartIndex +
                               "��PartԪ�ص�DataSourceSetԪ���еڶ�Ԫ��:ColConfig�еĵ�" +
                               nRowFieldIndex + "��FeildԪ��ȱ��src����ֵ������");
                    return false;
                }
                if (!isParamExist(nCurrentRowParamsValueSet,
                                  tFieldAttributeValue))
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                               nPartIndex +
                               "��PartԪ�ص�DataSourceSetԪ���еڶ�Ԫ��:ColConfig�еĵ�" +
                               nRowFieldIndex + "��FeildԪ��src�������õĸ����в���δǰ������");
                    return false;
                }
            }
            else
            {
                buildError("checkMatch",
                           "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                           nPartIndex +
                           "��PartԪ�ص�DataSourceSetԪ���еڶ�Ԫ��:ColConfig�еĵ�" +
                           nRowFieldIndex + "��FeildԪ��type����ֵ����������");
                return false;
            }
            //��Ӹ��и��в���
            nCurrentRowParamsValueSet.setNameAndValue(tNameAttributeValue,
                    "DEFAUT");
            if (nRowFieldIndex == 0)
            {
                nParamsValueSet.setNameAndValue(tNameAttributeValue, "DEFAUT");
            }
        }

        return true;
    }

    /**
     * У��DataDestinationSet�ĸ�ʽƥ��
     * @param nSheetIndex int
     * @param nPartIndex int
     * @param nDestSheetIndex int
     * @param tDataDestinationSetElement Element
     * @param nParamsValueSet TransferData
     * @return boolean
     * @throws Exception
     */
    private boolean checkDataDestinationSetMatch(int nSheetIndex,
                                                 int nPartIndex,
                                                 int nDestSheetIndex,
                                                 Element
                                                 tDataDestinationSetElement,
                                                 TransferData nParamsValueSet) throws
            Exception
    {
        //XML����ģ�������и�SheetԪ�����ڲ���PartԪ���еڶ�������DataDestinationSetԪ�ص������У��
        String tDataDestinationSetName = tDataDestinationSetElement.getName();
        if (tDataDestinationSetName == null ||
            !tDataDestinationSetName.trim().
            toUpperCase().equals("DATADESTINATIONSET"))
        {
            buildError("checkMatch",
                       "XML����ģ�������е�" + nSheetIndex + "��SheetԪ���еĵ�" + nPartIndex +
                       "��PartԪ�ص�DataDestinationSetԪ������������");
            return false;
        }
        //XML����ģ�������и�SheetԪ�����ڲ���PartԪ���еĵڶ�����Ԫ�أ�DataDestinationSet���ڲ�Ԫ�ص�У��
        List tDataDestinationSetChildenList = tDataDestinationSetElement.
                                              getChildren();
        int tDataDestinationSetChildenNum = tDataDestinationSetChildenList.size();
        if (tDataDestinationSetChildenNum < 1)
        {
            buildError("checkMatch",
                       "XML����ģ�������е�" + nSheetIndex + "��SheetԪ���еĵ�" + nPartIndex +
                       "��PartԪ�ص�DataDestinationSetԪ��ȱ��ROWԪ�ص�����");
            return false;
        }
        for (int nRowIndex = 0; nRowIndex < tDataDestinationSetChildenNum;
                             nRowIndex++)
        {
            //XML����ģ��������DataDestinationSet�е�RowԪ�ص�У��ֻ��Ա���У�飬���������ڲ��ٽ���У��
            //XML����ģ��������DataDestinationSet�е�RowԪ�ر���У��
            Element tDataDestinationSetRowElement = ((Element)
                    tDataDestinationSetChildenList.get(nRowIndex));
            String tDataDestinationSetRowName = tDataDestinationSetRowElement.
                                                getName();
            if (!tDataDestinationSetRowName.trim().toUpperCase().equals("ROW"))
            {
                buildError("checkMatch",
                           "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                           nPartIndex + "��PartԪ�ص�DataDestinationSetԪ���е�" +
                           nRowIndex + "��RowԪ��������������");
                return false;
            }
            String tTypeAttributeValue = tDataDestinationSetRowElement.
                                         getAttributeValue("type");
            if (tTypeAttributeValue == null ||
                tTypeAttributeValue.trim().equals(""))
            {
                buildError("checkMatch",
                           "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                           nPartIndex + "��PartԪ�ص�DataDestinationSetԪ�ص�" +
                           nRowIndex + "��RowԪ��ȱ��type����ֵ������");
                return false;
            }

            if (tTypeAttributeValue.trim().equals("1")) //Rowֱֵ�ӵ���������ԴExcel����м伶������ȡ�õ�������ROW(startRowIndex,endRowIndex)
            {
                String tFieldAttributeValue = tDataDestinationSetRowElement.
                                              getAttributeValue("src");
                if (tFieldAttributeValue == null ||
                    tFieldAttributeValue.trim().equals(""))
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                               nPartIndex + "��PartԪ�ص�DataDestinationSetԪ���е�" +
                               nRowIndex + "��RowԪ��ȱ��src����ֵ������");
                    return false;
                }
                int tStarRowIndex = Integer.parseInt((String) nParamsValueSet.
                        getValueByName("PARAM0"));
                int tEndRowIndex = Integer.parseInt((String) nParamsValueSet.
                        getValueByName("PARAM1"));
                if (!checkMiddlePositionDesc(tFieldAttributeValue))
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                               nPartIndex + "��PartԪ�ص�DataDestinationSetԪ���е�" +
                               nRowIndex + "��RowԪ��Src����ֵ����������ʽҪ���磺Row(0,12)");
                    return false;
                }
                String[] tSubString = getParseMiddlePosition(
                        tFieldAttributeValue);

                if (!checkMiddlePosition(Integer.parseInt(tSubString[0]),
                                         Integer.parseInt(tSubString[1]),
                                         tEndRowIndex - tStarRowIndex))
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                               nPartIndex + "��PartԪ�ص�DataDestinationSetԪ���е�" +
                               nRowIndex + "��RowԪ��Src����ֵ�����Ĳ���ȡֵ�������м�Row������ϵķ�Χ");
                    return false;
                }
            }
            else if (tTypeAttributeValue.trim().equals("2")) //Rowֵͨ�����㵽��������ԴExcel����м伶�����л��
            {
                List tDataDestinationSetRowFieldList =
                        tDataDestinationSetRowElement.getChildren();
                int tDataDestinationSetRowFieldListNum =
                        tDataDestinationSetRowFieldList.size();
                if (tDataDestinationSetRowFieldListNum < 1)
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "��SheetԪ���еĵ�" +
                               nPartIndex +
                               "��PartԪ�ص�DataDestinationSetԪ����Ԫ��Row�е�Ӧ������һ��FeildԪ��");
                    return false;
                }
                TransferData nCurrentRowParamsValueSet = new TransferData();
                for (int nRowFieldIndex = 0;
                                          nRowFieldIndex <
                                          tDataDestinationSetRowFieldListNum;
                                          nRowFieldIndex++)
                {
                    //XML����ģ��������Row�е�FieldԪ�ر���У��
                    Element tFieldElement = ((Element)
                                             tDataDestinationSetRowFieldList.
                                             get(nRowFieldIndex));
                    String tFieldName = tFieldElement.getName();
                    if (!tFieldName.trim().toUpperCase().equals("FIELD"))
                    {
                        buildError("checkMatch",
                                   "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                                   nPartIndex +
                                   "��PartԪ�ص�DataDestinationSetԪ����Ԫ��:Row�еĵ�" +
                                   nRowFieldIndex + "��FeildԪ��������������");
                        return false;
                    }
                    String tRowTypeAttributeValue = tFieldElement.
                            getAttributeValue("type");
                    String tRowNameAttributeValue = tFieldElement.
                            getAttributeValue("name");
                    if (tRowNameAttributeValue == null ||
                        tRowNameAttributeValue.trim().equals(""))
                    {
                        buildError("checkMatch",
                                   "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                                   nPartIndex +
                                   "��PartԪ�ص�DataDestinationSetԪ����Ԫ��:Row�еĵ�" +
                                   nRowFieldIndex + "��FeildԪ��ȱ��name����ֵ������");
                        return false;
                    }

                    if (tRowTypeAttributeValue == null ||
                        tRowTypeAttributeValue.trim().equals(""))
                    {
                        buildError("checkMatch",
                                   "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                                   nPartIndex +
                                   "��PartԪ�ص�DataDestinationSetԪ����Ԫ��:Row�еĵ�" +
                                   nRowFieldIndex + "��FeildԪ��ȱ��type����ֵ������");
                        return false;
                    }

                    if (tRowTypeAttributeValue.trim().equals("0")) //����srcֵ���Զ���ֵ
                    {}
                    else if (tRowTypeAttributeValue.trim().equals("1")) //����srcֵ��ֱ�ӵ���������Դ��ȡ��
                    {
                        String tFieldAttributeValue = tFieldElement.
                                getAttributeValue("src");
                        if (tFieldAttributeValue == null ||
                            tFieldAttributeValue.trim().equals(""))
                        {
                            buildError("checkMatch",
                                       "XML����ģ�������е�" + nSheetIndex +
                                       "SheetԪ���еĵ�" + nPartIndex +
                                       "��PartԪ�ص�DataDestinationSetԪ����Ԫ���е�" +
                                       nRowIndex + "��Row�еĵ�" + nRowFieldIndex +
                                       "��FeildԪ��ȱ��src����ֵ������");
                            return false;
                        }
                        if (!checkExcelPositionDesc(tFieldAttributeValue))
                        {
                            buildError("checkMatch",
                                       "XML����ģ�������е�" + nSheetIndex +
                                       "SheetԪ���еĵ�" + nPartIndex +
                                       "��PartԪ�ص�DataDestinationSetԪ����Ԫ���е�" +
                                       nRowIndex + "��Row�еĵ�" + nRowFieldIndex +
                                       "��FeildԪ��src����ֵ����������Ӧ��:Sheet(0,1,3)");
                            return false;
                        }
                        String[] tSubString = getParseExcelPositon(
                                tFieldAttributeValue);
                        if (tSubString[0].trim().equals("-1"))
                        {
                            tSubString[0] = String.valueOf(nSheetIndex);
                        }
                        if (tSubString[1].trim().equals("-1"))
                        {
                            buildError("checkMatch",
                                       "XML����ģ�������е�" + nSheetIndex +
                                       "SheetԪ���еĵ�" + nPartIndex +
                                       "��PartԪ�ص�DataDestinationSetԪ����Ԫ���е�" +
                                       nRowIndex + "��Row�еĵ�" + nRowFieldIndex +
                                       "��FeildԪ��src����ֵ������������ȡ����ַ����Ŀ���е����Զ����У�����������������Ϊ��ǰ�м�-1��");
                            return false;

                        }
                        if (!checkExcelPosition(Integer.parseInt(tSubString[0]),
                                                Integer.parseInt(tSubString[1]),
                                                Integer.parseInt(tSubString[2])))
                        {
                            buildError("checkMatch",
                                       "XML����ģ�������е�" + nSheetIndex +
                                       "SheetԪ���еĵ�" + nPartIndex +
                                       "��PartԪ�ص�DataDestinationSetԪ����Ԫ���е�" +
                                       nRowIndex + "��Row�еĵ�" + nRowFieldIndex +
                                       "��FeildԪ��src����ֵ������������ȡ����ַ����Excel��ȡ����Χ");
                            return false;
                        }

                    }
                    else if (tRowTypeAttributeValue.trim().equals("2"))
                    {
                        String tFieldAttributeValue = tFieldElement.
                                getAttributeValue("src");
                        String tParamsAttributeValue = tFieldElement.
                                getAttributeValue("params");
                        if (tFieldAttributeValue == null ||
                            tFieldAttributeValue.trim().equals(""))
                        {
                            buildError("checkMatch",
                                       "XML����ģ�������е�" + nSheetIndex +
                                       "SheetԪ���еĵ�" + nPartIndex +
                                       "��PartԪ�ص�DataDestinationSetԪ����Ԫ���е�" +
                                       nRowIndex + "��Row�еĵ�" + nRowFieldIndex +
                                       "��FeildԪ��ȱ��src����ֵ������");
                            return false;
                        }
                        if (tParamsAttributeValue == null ||
                            tParamsAttributeValue.trim().equals(""))
                        {
                        }
                        else //��type=2 ����sql����ʱ��������ʾ������������ʱ��������ӦУ��
                        {
                            if (!checkTargetParamsAttributeDesc(nParamsValueSet,
                                    nCurrentRowParamsValueSet, nSheetIndex,
                                    nRowFieldIndex, tParamsAttributeValue))
                            {
                                buildError("checkMatch",
                                           "XML����ģ�������е�" + nSheetIndex +
                                           "SheetԪ���еĵ�" + nPartIndex +
                                           "��PartԪ�ص�DataDestinationSetԪ����Ԫ���е�" +
                                           nRowIndex + "��Row�еĵ�" +
                                           nRowFieldIndex +
                                           "��FeildԪ��params����ֵ����������");
                                return false;
                            }
                        }
                    }
                    else if (tRowTypeAttributeValue.trim().equals("3")) //����srcֵ�ǴӲ�����ֱ��ȡ��
                    {
                        String tFieldAttributeValue = tFieldElement.
                                getAttributeValue("src");
                        if (tFieldAttributeValue == null ||
                            tFieldAttributeValue.trim().equals(""))
                        {
                            buildError("checkMatch",
                                       "XML����ģ�������е�" + nSheetIndex +
                                       "SheetԪ���еĵ�" + nPartIndex +
                                       "��PartԪ�ص�DataDestinationSetԪ����Ԫ���е�" +
                                       nRowIndex + "��Row�еĵ�" + nRowFieldIndex +
                                       "��FeildԪ��ȱ��src����ֵ������");
                            return false;
                        }
                        if (!isParamExist(nParamsValueSet, tFieldAttributeValue))
                        {
                            buildError("checkMatch",
                                       "XML����ģ�������е�" + nSheetIndex +
                                       "SheetԪ���еĵ�" + nPartIndex +
                                       "��PartԪ�ص�DataDestinationSetԪ����Ԫ���е�" +
                                       nRowIndex + "��Row�еĵ�" + nRowFieldIndex +
                                       "��FeildԪ��src�������õ�ֱ�Ӳ���δ����");
                            return false;
                        }

                    }
                    else if (tRowTypeAttributeValue.trim().equals("4")) //����srcֵ�ǴӸ��и���������ֱ��ȡ��
                    {
                        String tFieldAttributeValue = tFieldElement.
                                getAttributeValue("src");
                        if (tFieldAttributeValue == null ||
                            tFieldAttributeValue.trim().equals(""))
                        {
                            buildError("checkMatch",
                                       "XML����ģ�������е�" + nSheetIndex +
                                       "SheetԪ���еĵ�" + nPartIndex +
                                       "��PartԪ�ص�DataDestinationSetԪ����Ԫ���е�" +
                                       nRowIndex + "��Row�еĵ�" + nRowFieldIndex +
                                       "��FeildԪ��ȱ��src����ֵ������");
                            return false;
                        }
                        if (!isParamExist(nCurrentRowParamsValueSet,
                                          tFieldAttributeValue))
                        {
                            buildError("checkMatch",
                                       "XML����ģ�������е�" + nSheetIndex +
                                       "SheetԪ���еĵ�" + nPartIndex +
                                       "��PartԪ�ص�DataDestinationSetԪ����Ԫ���е�" +
                                       nRowIndex + "��Row�еĵ�" + nRowFieldIndex +
                                       "��FeildԪ��src�������õĸ����в���δǰ������");
                            return false;
                        }
                    }
                    else if (tRowTypeAttributeValue.trim().equals("5")) //����srcֵ�Ǵ��м����ݼ���ĳ�е�ĳһ����ֵ���
                    {
                        String tFieldAttributeValue = tFieldElement.
                                getAttributeValue("src");
                        if (tFieldAttributeValue == null ||
                            tFieldAttributeValue.trim().equals(""))
                        {
                            buildError("checkMatch",
                                       "XML����ģ�������е�" + nSheetIndex +
                                       "SheetԪ���еĵ�" + nPartIndex +
                                       "��PartԪ�ص�DataDestinationSetԪ����Ԫ���е�" +
                                       nRowIndex + "��Row�еĵ�" + nRowFieldIndex +
                                       "��FeildԪ��ȱ��src����ֵ������");
                            return false;
                        }
                        if (!checkTargetPositionDesc(nCurrentRowParamsValueSet,
                                tFieldAttributeValue))
                        {
                            buildError("checkMatch",
                                       "XML����ģ�������е�" + nSheetIndex +
                                       "SheetԪ���еĵ�" + nPartIndex +
                                       "��PartԪ�ص�DataDestinationSetԪ����Ԫ���е�" +
                                       nRowIndex + "��Row�еĵ�" + nRowFieldIndex +
                                       "��FeildԪ��src����ֵ����������Ӧ��:Row(2).����������");
                            return false;
                        }
                        if (!checkTargetPosition(tFieldAttributeValue,
                                                 nParamsValueSet,
                                                 nCurrentRowParamsValueSet))
                        {
                            buildError("checkMatch",
                                       "XML����ģ�������е�" + nSheetIndex +
                                       "SheetԪ���еĵ�" + nPartIndex +
                                       "��PartԪ�ص�DataDestinationSetԪ����Ԫ���е�" +
                                       nRowIndex + "��Row�еĵ�" + nRowFieldIndex +
                                       "��FeildԪ��src����ֵ���������м��ȡ����ַ��ȡ����Χ");
                            return false;
                        }
                    }
                    nCurrentRowParamsValueSet.setNameAndValue(
                            tRowNameAttributeValue, "DEFAUT");
                }
            }
            else
            {
                buildError("checkMatch",
                           "XML����ģ�������е�" + nSheetIndex + "SheetԪ���еĵ�" +
                           nPartIndex + "��PartԪ�ص�DataDestinationSetԪ���е�" +
                           nRowIndex + "��RowԪ��Type����ֵ����������");
                return false;
            }
        }

        return true;
    }

    /**
     * У���ʽƥ��
     * @return boolean
     * @throws Exception
     */
    private boolean checkMatch() throws Exception
    {
        //��ȡExcel��������Դ��Sheet�ĸ���
        int tExcelSheetNum = mBookModelImplImport.getNumSheets();
        try
        {
            //У���Ԫ��DataConfigDesc
            String tRootName = mXMLImplConfig.getName();
            if (tRootName == null ||
                !tRootName.trim().toUpperCase().equals("DATACONFIGDESC"))
            {
                buildError("checkMatch", "XML����ģ��������ȱ�ٸ�Ԫ��DataConfigDesc������");
                return false;
            }

            //У��XML����ģ�������е�SheetԪ��
            List tSheetList = mXMLImplConfig.getChildren();
            int tSheetListNum = tSheetList.size();
            if (tSheetListNum < 1)
            {
                buildError("checkMatch", "XML����ģ��������ȱ��SheetԪ�ص�����");
                return false;
            }
            for (int nSheetIndex = 0; nSheetIndex < tSheetListNum; nSheetIndex++)
            {
                //XML����ģ��������SheetԪ�ر���У��
                Element tSheetElement = ((Element) tSheetList.get(nSheetIndex));
                String tSheetName = tSheetElement.getName();
                if (!tSheetName.trim().toUpperCase().equals("SHEET"))
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex + "SheetԪ��������������");
                    return false;
                }
                String tDestIndex = tSheetElement.getAttributeValue("dest");
                if (tDestIndex == null || tDestIndex.trim().equals("")) //û����������Ϊ��sheet˳�����ͬ
                {
                    tDestIndex = String.valueOf(nSheetIndex);
                }
                // �ж����������е������Sheetҳ���Ƿ�С�ڵ�������EXCEL�����ṩ��Sheetҳ��
                int nDestIndex = Integer.parseInt(tDestIndex);
                if (nDestIndex >= tExcelSheetNum)
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex +
                               "SheetԪ�ص�Dest���������Sheetҳ�Ŵ�������EXCEL�����ṩ��Sheetҳ��");
                    return false;
                }

                //XML����ģ�������и�SheetԪ�����ڲ�����Ԫ��У��
                List tPartList = tSheetElement.getChildren();
                //XML����ģ�������и�SheetԪ�����ڲ�PartԪ�ر����У��
                int tPartListNum = tPartList.size();
                if (tPartListNum < 1)
                {
                    buildError("checkMatch",
                               "XML����ģ�������е�" + nSheetIndex +
                               "Sheet��ȱ��PartԪ�ص�����");
                    return false;
                }
                //XML����ģ�������и�SheetԪ�����ڲ�PartԪ��У��
                for (int nPartIndex = 0; nPartIndex < tPartListNum; nPartIndex++)
                {
                    int nMaxRow = 0;
                    //XML����ģ��������SheetԪ���ڲ�PartԪ���еĲ����洢����
                    TransferData nParamsValueSet = new TransferData();
                    //XML����ģ��������SheetԪ���ڲ�PartԪ�ر���У��
                    Element tPartElement = ((Element) tPartList.get(nPartIndex));
                    String tPartName = tPartElement.getName();
                    if (tPartName.trim().toUpperCase().equals("PART"))
                    {
                        //XML����ģ�������и�SheetԪ�����ڲ���PartԪ�����ڲ�Ԫ��У��
                        List tPartChildenList = tPartElement.getChildren();
                        //XML����ģ�������и�SheetԪ�����ڲ���PartԪ���е�DataSourceSetԪ�ر����У��
                        int tPartChildenNum = tPartChildenList.size();
                        if (tPartChildenNum != 2)
                        {
                            buildError("checkMatch",
                                       "XML����ģ�������е�" + nSheetIndex +
                                       "��SheetԪ���еĵ�" + nPartIndex +
                                       "��PartԪ�ص�ȱ�ٱر���DataSourceSet��DataDestinationSetԪ������");
                            return false;
                        }

                        //XML����ģ�������и�SheetԪ�����ڲ���PartԪ���еĵ�һ����Ԫ�أ�DataSourceSet��У��
                        Element tDataSourceSetElement = ((Element)
                                tPartChildenList.get(0));

                        if (!checkDataSourceSetMatch(nSheetIndex, nPartIndex,
                                nDestIndex, tDataSourceSetElement,
                                nParamsValueSet))
                        {
                            return false;
                        }

                        //��ȡ�м���������
                        //XML����ģ�������и�SheetԪ�����ڲ���PartԪ���еڶ�������DataDestinationSetԪ�ص�У��
                        Element tDataDestinationSetElement = ((Element)
                                tPartChildenList.get(1));
                        if (!checkDataDestinationSetMatch(nSheetIndex,
                                nPartIndex, nDestIndex,
                                tDataDestinationSetElement, nParamsValueSet))
                        {
                            return false;
                        }
                    }
                    else
                    {
                        buildError("checkMatch",
                                   "XML����ģ�������е�" + nSheetIndex + "SheetԪ���е�" +
                                   nPartIndex + "PartԪ��������������");
                        return false;
                    }
                }
                System.out.println("XML����ģ����" + nSheetIndex + "Sheet����ɹ�����");
            }
            System.out.println("XML����ģ�����ɹ�����");
        }
        catch (Exception e)
        {
            System.err.println("XML�������ݶ���ʧ��:" + e.getMessage());
            return false;
        }
        return true;

        // ����һ����Ԫ�ص�ֵ��Ϊ��Ӧ��XMLԪ�ص�����
        // ����Sheet0�У�ÿ�еĵ�һ�ж�Ӧ��XMLԪ������ΪID��
//    DOMBuilder db = new DOMBuilder();
//    Document doc = db.build(new FileInputStream(m_strConfigFileName));
//    Element eleRoot = doc.getRootElement();
//    Element ele = null;
//    String strColName = "";

//    for(int nIndex = 0; nIndex < 1; nIndex ++) {
//
//      ele = eleRoot.getChild("Sheet" + String.valueOf(nIndex+1));
//      int nMaxCol = getMaxCol(nIndex);
//      String[] strArr = new String[nMaxCol];
//
//      for(int nCol = 0; nCol < nMaxCol; nCol ++) {
//        strColName = ele.getChildText("COL" + String.valueOf(nCol));
//        if( strColName == null || strColName.equals("") ) {
//          throw new Exception("�Ҳ�����Ӧ��������Ϣ��Sheet" + String.valueOf(nIndex+1)
//                              + ":COL" + String.valueOf(nCol));
//        }
//
//        strArr[nCol] = strColName;
//      }
//
//      setPropArray(nIndex, PROP_COL_NAME, strArr);
//    }
    }

    private int getMaxRow(int nSheetIndex) throws Exception
    {
        int nMaxRow = 0;
        int nNullCount = mBookModelImplImport.checkSheet(nSheetIndex).
                         getLastRow();
//	String str = "";
//	  for(nMaxRow = 0; nMaxRow < mBookModelImplImport.getMaxRow(); nMaxRow ++) {
//		str = mBookModelImplImport.getText(nSheetIndex, nMaxRow, 0);
//		if( str != null && str.trim().toUpperCase().equals("") ) {
//		  nNullCount++;
//		  if(nNullCount==10)//��������ʮ�е�һ�ж�Ϊ0����Ϊ�ѵ���Ч�����
//		     break;
//		}
//		else
//	     nNullCount=0;
//	  }

        return nNullCount + 1;
    }

    private int getMaxCol(int nSheetIndex) throws Exception
    {
        String str = "";
        int nMaxCol = 0;
        int nNullCount = mBookModelImplImport.checkSheet(nSheetIndex).
                         getLastCol();
//	for(nMaxCol = 0; nMaxCol < mBookModelImplImport.getLastCol(); nMaxCol ++) {
//	  str = mBookModelImplImport.getText(nSheetIndex, 0, nMaxCol);
//	  if( str == null || str.trim().equals("") ) {
//		nNullCount++;
//		if(nNullCount==10)//��������ʮ�е�һ�ж�Ϊ0����Ϊ�ѵ���Ч�����
//		  break;
//	  }
//	  else
//	   nNullCount=0;
//	}
        return nNullCount + 1;
    }

}

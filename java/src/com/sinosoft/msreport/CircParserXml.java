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
 * <p>Title: Web业务系统 </p>
 * <p>Description: 将xls文件解析成指定格式的XML文件 </p>
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

    // 保存文件的公共路径
    private String mFilePath = "";
    // 保存物理Excel文件的绝对路径
    private String mDataExcelFileName = "";
    // 保存磁盘投保文件的路径名
    private String m_strPathName = "";

    private String m_strBatchNo = "";
    // 配置文件的文件名
    private String mConfigXmlFileName = "";

    // 保存Sheet相关的信息，如最大行，最大列，当前处理到的行等
    private TransferData mExcelSheetMaxRowSet = new TransferData();
    private TransferData mExcelSheetMaxColSet = new TransferData();


    private static int ROW_LIMIT = 1000; // 一次处理的行数,提高效率


    //定义BookModelImpl公共处理类
    //配置模板数据
    private Element mXMLImplConfig = null;
    private Element mXMLMiddleRoot = null;
    private Element mXMLTargetRoot = null;

    //物理数据源EXCEL文件数据
    private BookModelImpl mBookModelImplImport = new BookModelImpl();
    private int mExcelSheetNum = 0;
    //目标XML数据
    private Document mXMLImplExport = null;
    //中间XML数据
    private Document mXMLImplMiddle = null;
    private List mSheetList = null;
    private MMap map = new MMap();


    public CircParserXml()
    {

    }

    // 设置要处理的文件名
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
//	m_strBatchNo = strFileName.substring(0, nPos);//得到文件名
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


    // 设置配置文件名
    public boolean setConfigFileName(String strConfigFileName)
    {

        mConfigXmlFileName = strConfigFileName;
        return true;
    }

    // 设置配置文件名
    public void setFilePathName(String strConfigFileName)
    {
        File file = new File(strConfigFileName);
        if (!file.exists())
        {
            buildError("setFileName", "指定的路径" + strConfigFileName + "不存在！");
        }
        mFilePath = strConfigFileName;
    }

//
    public String getConfigFileName()
    {
        return mConfigXmlFileName;
    }


    //更据配置模板，解析EXCEL文件，更据配置模板提取信息生成配置模板指定格式的XML文件
    public boolean transform()
    {
        String strFileName = "";
        int nCount = 0;
        try
        {
            //读取物理Excel数据信息
            if (!loadDataExcel())
            {
                return false;
            }

            //读取XML配置信息
            if (!loadConfigXML())
            {
                return false;
            }

            //校验物理文件与XML配置文件的匹配
            if (!checkMatch())
            {
                return false;
            }

            //生成中间XML数据源信息
            if (!createMiddleXML())
            {
                return false;
            }
            //输出中间XML数据信息
            if (!outputDocumentToFile(mFilePath, "Middle" + mConfigXmlFileName,
                                      mXMLImplMiddle))
            {
                return false;
            }

            //生成目标XML数据信息
            if (!createTargetXML())
            {
                return false;
            }
            //输出目标XML数据信息
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

    //更据配置模板，解析EXCEL文件，更据配置模板提取信息生成配置模板指定格式的XML文件
    public boolean insertIntoPhyTable(Element tXMLTargetRoot)
    {
        String strFileName = "";
        int nCount = 0;
        try
        {
            //释放资源
            Element mXMLImplConfig = null;
            //目标XML数据
            Document mXMLImplExport = null;
            //中间XML数据
            Document mXMLImplMiddle = null;

            //逐一Part准备将具体提交到指定的数据库中的数据
            try
            {
                //查找XML配置模板描述中的Sheet元素
                List tSheetList = mXMLTargetRoot.getChildren();
                int tSheetListNum = tSheetList.size();
                for (int nSheetIndex = 0; nSheetIndex < tSheetListNum;
                                       nSheetIndex++)
                {
                    //XML配置模板描述中Sheet元素本身
                    Element tSheetElement = ((Element) tSheetList.get(
                            nSheetIndex));

                    //XML配置模板描述中该Sheet元素中内部其他元素
                    List tPartList = tSheetElement.getChildren();
                    //XML配置模板描述中该Sheet元素中内部Part元素本身的校验
                    int tPartListNum = tPartList.size();
                    //XML配置模板描述中该Sheet元素中内部Part元素校验
                    for (int nPartIndex = 0; nPartIndex < tPartListNum;
                                          nPartIndex++)
                    {
                        Element tPartElement = ((Element) tPartList.get(
                                nPartIndex));
                        //XML配置模板描述中Sheet元素内部Part元素中的参数存储对象
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
     * 根据xml解析，生成插入脚本
     * @param tPartElement Element
     * @param tTableName String
     * @param tMap MMap
     */
    private void prepareOutPutData(Element tPartElement, String tTableName,
                                   MMap tMap)
    {
        //根据表名，获取表中字段的含义
        try
        {
            Connection conn = DBConnPool.getConnection();
            ResultSet tResultSet = null;
            DatabaseMetaData tDatabaseMetaData = null;
            tDatabaseMetaData = conn.getMetaData();
            String tColumns = "";
            String tColumnType = "";
            //通过DatabaseMetData方法获取表中的字段属性]
            System.out.println(tTableName);
            tResultSet = tDatabaseMetaData.getColumns(null, null,
                    tTableName.toUpperCase(), null);
            while (tResultSet.next())
            {
                //获取当前字段的类型，判定是否是char，varchar，varchar2，date，datetime
                tColumnType = tResultSet.getString(6).toLowerCase();
                if (tColumnType.indexOf("char") != -1 ||
                    tColumnType.indexOf("date") != -1)
                {
                    tColumns = tColumns + tResultSet.getString(4).toLowerCase() +
                               ",";
                }
            }
            //获得满足条件的字段组成的大字符串
            tColumns = tColumns.substring(0, tColumns.length() - 1);
            conn.close();
            tResultSet.close();

            List tRowList = (List) ((tPartElement).getChildren());
            int tRowNum = tRowList.size();
//            int tTotalRowCount = 0;
            for (int nRowIndex = 0; nRowIndex < tRowNum; nRowIndex++)
            {
                Element tRowElement = (Element) tRowList.get(nRowIndex);
                //初始化插入语句对象
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
                    //字段名
                    String tAttributeName = tAttribute.getName();
                    //字段值
                    String tAttributeValue = tAttribute.getValue();
                    if (nAttributeIndex == 0)
                    {
                        tInnerSQL = tInnerSQL + tAttributeName;
                        //如果插入的字段在大字符串中，则需要在数据两边添加'信息
                        if (tColumns.indexOf(tAttributeName) != -1)
                        {
                            tValuesSQL = tValuesSQL + "'" + tAttributeValue +
                                         "'";
                        }
                        else
                        {
                            //如果要插入的非字符类型数据的值为空，则需要特殊处理
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
                        //如果插入的字段在大字符串中，则需要在数据两边添加'信息
                        if (tColumns.indexOf(tAttributeName) != -1)
                        {
                            tValuesSQL = tValuesSQL + ",'" + tAttributeValue +
                                         "' )";
                        }
                        else
                        {
                            //如果要插入的非字符类型数据的值为空，则需要特殊处理
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
                        //如果插入的字段在大字符串中，则需要在数据两边添加'信息
                        if (tColumns.indexOf(tAttributeName) != -1)
                        {
                            tValuesSQL = tValuesSQL + ",'" + tAttributeValue +
                                         "'";
                        }
                        else
                        {
                            //如果要插入的非字符类型数据的值为空，则需要特殊处理
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
                //组合插入语句信息
                String tAllSQL = tInsertSQL + tInnerSQL + tValuesSQL;
//                System.out.println(tAllSQL);
                tMap.put(tAllSQL, "INSERT");
                tAllSQL = null;
            }

        }
        catch (Exception ex)
        {
            buildError("prepareOutPutData", "查询表结构失败！");
        }

    }


    // 下面是私有函数
    private void buildError(String szFunc, String szErrMsg)
    {
        CError cError = new CError();
        cError.moduleName = "GrpPolVTSParser";
        cError.functionName = szFunc;
        cError.errorMessage = szErrMsg;
        this.mErrors.addOneError(cError);
    }

    /**
     * 读入配置XML文件数据
     * @return boolean
     * @throws Exception
     */
    private boolean loadDataExcel() throws Exception
    {
        if (mDataExcelFileName.equals(""))
        {
            throw new Exception("请先调用setFileName函数设置要处理的文件名。");
        }

        mBookModelImplImport.read(mFilePath + mDataExcelFileName + ".xls",
                                  new com.f1j.ss.ReadParams());
        mExcelSheetNum = mBookModelImplImport.getNumSheets();
        if (mBookModelImplImport.getNumSheets() < 1)
        {
            throw new Exception("物理导入Excel文件不完整，缺少Sheet。");
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
     * 读入配置XML文件数据
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
            System.err.println("XML配置数据读入失败:" + e.getMessage());
        }
        return true;
    }

    //输出xml文件，参数为路径，文件名
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
            System.err.println("XML中间表已输出到：" + str);
            return true;
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 生成中间XML数据集合（只包含为每个物理Sheet所提取的所有Row信息）
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

        if (tTypeAttributeValue.trim().equals("0")) //属性src值是自定义值
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

        if (tTypeAttributeValue.trim().equals("1")) //属性src值是直接到物理数据源Excel表取得
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
     * 生成中间XML数据集合（只包含为每个物理Sheet所提取的所有Row信息）
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
     * 得到物理Excel表中指定的数据信息）
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
     * 得到物理Excel表中指定的Sheet(nSheetIndex,x,y)格式的数据信息）
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
     * 得到物理Excel表中指定的Sheet(nSheetIndex,x,y)格式的数据信息）
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

            //指定取的物理数据源中当前行某列值
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
     * 校验描述物理Excel表中位置是否满足Sheet(nSheetIndex,x,y)格式的要求
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

            if (tSrcAttributeValueString.trim().length() < 12) //型如:Sheet(0,3,0)
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
     * 校验描述物理Excel表中位置是否满足Row(2).FirstSubjectNo格式的要求
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

            if (tSrcAttributeValueString.trim().length() < 8) //型如Row(2).FirstSubjectNo
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
     * 校验描述中间表中位置是否满足ROW(tStartRowIndex,tEndRowIndex)的取值的范围要求
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

            if (tSrcAttributeValueString.trim().length() < 8) //型如Row(2).FirstSubjectNo
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

            //校验行数是否越界
            if (tRowIndex < 0 || tRowIndex > b - a)
            {
                return false;
            }
            //校验属性数是否已定义
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
     * 校验描述物理Excel表中位置是否满足ROW(tStartRowIndex,tEndRowIndex)格式的要求
     * @param tSrcAttributeValueString String
     * @return boolean
     */
    private boolean checkMiddlePositionDesc(String tSrcAttributeValueString)
    {
        try
        {
            int tColIndex = 0;

            if (tSrcAttributeValueString.trim().length() < 8) //型如:Row(0,3)
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
     * 校验描述中间表中位置是否满足ROW(tStartRowIndex,tEndRowIndex)的取值的范围要求
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

//取得ColConfig元素中的Field元素的src属性值
    private String getMiddleSrcValue(TransferData tTransferData,
                                     TransferData tempTransferData,
                                     int tSheetIndex, int tPartIndex,
                                     int tRowIndex, String tSrcAttributeValue,
                                     String tTypeAttributeValue,
                                     String tParamsAttributeValue)
    {
        String tTrueSrcAttributeValue = "";
        if (tTypeAttributeValue.trim().equals("0")) //属性src值是自定义值
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
        else if (tTypeAttributeValue.trim().equals("1")) //属性src值是直接到物理数据源Excel表某页(tSheetIndex)某行(tRowIndex)某列(tColIndex)取得
        {
            tTrueSrcAttributeValue = getExcelValue(tSrcAttributeValue,
                    tSheetIndex, tRowIndex);
            if (tTrueSrcAttributeValue == null)
            {
                tTrueSrcAttributeValue = "";
            }
        }
        else if (tTypeAttributeValue.trim().equals("2")) //属性src值是通过计算取得
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
            //0 -- 默认，表示条件为Where子句。
            PubCalculator tPubCalculator = new PubCalculator();
            //准备计算要素
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
            //准备计算SQL
            tPubCalculator.setCalSql(tSrcAttributeValue);
            String strSQL = tPubCalculator.calculate();
            System.out.println("计算元素的Src属性值SQL : " + strSQL);
            if (strSQL == null)
            {
                //buildError("getSrcValue","XML配置模板描述中第"+tSheetIndex+"Sheet中的第"+tPartIndex+"Part中的DataSourceSet元素中的ColConfig元素的Field的Src属性值"+tSrcAttributeValue+"的描述有误（Sql本身语法有误或引用参数之前未定义））");
                return "";
            }
            tTrueSrcAttributeValue = strSQL;
        }
        else if (tTypeAttributeValue.trim().equals("3")) //属性src值是从直接参数中直接取得
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
        else if (tTypeAttributeValue.trim().equals("4")) //属性src值是从辅助参数中直接取得（该行已取得的列均已作为参数存储到辅助参数对象中）
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


//取得目标数据DataDestinationSet中的Row元素中的Field元素的src属性值
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
        if (tTypeAttributeValue.trim().equals("0")) //属性src值是自定义值
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
        else if (tTypeAttributeValue.trim().equals("1")) //属性src值是直接到物理数据源Excel表某页(tSheetIndex)某行(tRowIndex)某列(tColIndex)取得
        {
            tTrueSrcAttributeValue = getExcelValue(tSrcAttributeValue,
                    tSheetIndex, tRowIndex);
            if (tTrueSrcAttributeValue == null)
            {
                tTrueSrcAttributeValue = "";
            }
        }
        else if (tTypeAttributeValue.trim().equals("2")) //属性src值是通过计算取得
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
            //0 -- 默认，表示条件为Where子句。
            PubCalculator tPubCalculator = new PubCalculator();
            //准备计算要素
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
            //准备计算SQL
            tPubCalculator.setCalSql(tSrcAttributeValue);
            String strSQL = tPubCalculator.calculate();
            System.out.println("计算ColConfig元素的Field元素的Src属性值SQL : " + strSQL);
            if (strSQL == null)
            {
                //buildError("getTargetSrcValue","XML配置模板描述中第"+tSheetIndex+"Sheet中的第"+tPartIndex+"Part中的DataDestinationSet元素中的第"+tFieldIndex+"Row元素内部第"+tFieldIndex+"Field元素的Src属性值"+tSrcAttributeValue+"的描述有误（Sql本身语法有误或引用参数之前未定义））");
                return "";
            }
            tTrueSrcAttributeValue = strSQL;
        }
        else if (tTypeAttributeValue.trim().equals("3")) //属性src值是从直接参数中直接取得
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
        else if (tTypeAttributeValue.trim().equals("4")) //属性src值是从辅助参数中直接取得（该行已取得的列均已作为参数存储到辅助参数对象中）
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
        else if (tTypeAttributeValue.trim().equals("5")) //属性src值是中间结果集合中直接取得（如Row(2).FirstSubjectNo）
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

//取得参数值
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

//取得参数值
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
        if (tTypeAttributeValue.trim().equals("0")) //属性src值是自定义值
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
        else if (tTypeAttributeValue.trim().equals("1")) //属性src值是直接到物理数据源Excel表取得
        {
            tTrueSrcAttributeValue = getExcelValue(tSrcAttributeValue.trim());
            if (tSrcAttributeValue == null)
            {
                tTrueSrcAttributeValue = "";
            }
        }
        else if (tTypeAttributeValue.trim().equals("2")) //属性src值是通过计算取得
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
            //0 -- 默认，表示条件为Where子句。
            PubCalculator tPubCalculator = new PubCalculator();
            //准备计算要素
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
            //准备计算SQL
            tPubCalculator.setCalSql(tSrcAttributeValue);
            String strSQL = tPubCalculator.calculate();
            System.out.println("计算PARAMS中的Src属性SQL : " + strSQL);
            if (strSQL == null)
            {
                //buildError("getExcelSrcValue","XML配置模板描述中第"+tSheetIndex+"Sheet中的第"+tPartIndex+"Part中的DataSourceSet元素中的Params元素的Field的Src属性值"+tSrcAttributeValue+"的描述有误（Sql本身语法有误或引用参数之前未定义））");
                return "";
            }
            tTrueSrcAttributeValue = strSQL;
        }

        return tTrueSrcAttributeValue;

    }

    /**
     * 生成中间XML数据集合（只包含为每个物理Sheet所提取的所有Row信息）
     * @return boolean
     */
    private boolean createMiddleXML()
    {
        mXMLMiddleRoot = new org.jdom.Element("XmlRoot");
        //Element tXMLImplMiddleRoot = mXMLMiddleRoot;
        try
        {
            //查找XML配置模板描述中的Sheet元素
            List tSheetList = mXMLImplConfig.getChildren();
            int tSheetListNum = tSheetList.size();
            if (tSheetListNum < 1)
            {
                buildError("checkMatch", "XML配置模板描述中缺少Sheet元素的描述");
                return false;
            }

            for (int nSheetIndex = 0; nSheetIndex < tSheetListNum; nSheetIndex++)
            {
                //XML配置模板描述中Sheet元素本身
                Element tSheetElement = ((Element) tSheetList.get(nSheetIndex));
                //生成一个Sheet节点
                Element tSheetRootElement = new Element("SHEET");
                tSheetRootElement.addAttribute("id", String.valueOf(nSheetIndex));

                String tDestIndex = tSheetElement.getAttributeValue("dest");
                if (tDestIndex == null || tDestIndex.trim().equals("")) //没有描述则认为和sheet顺序号相同
                {
                    tDestIndex = String.valueOf(nSheetIndex);
                }
                int nDestIndex = Integer.parseInt(tDestIndex);

                //XML配置模板描述中该Sheet元素中内部其他元素
                List tPartList = tSheetElement.getChildren();
                //XML配置模板描述中该Sheet元素中内部Part元素本身的校验
                int tPartListNum = tPartList.size();
                //XML配置模板描述中该Sheet元素中内部Part元素校验
                for (int nPartIndex = 0; nPartIndex < tPartListNum; nPartIndex++)
                {
                    //XML配置模板描述中Sheet元素内部Part元素中的参数存储对象
                    TransferData nParamsValueSet = new TransferData();
                    Element tPartRootElement = new Element("PART");
                    tPartRootElement.addAttribute("id",
                                                  String.valueOf(nPartIndex));

                    int nMaxRow = 0;
                    //XML配置模板描述中Sheet元素内部Part元素本身校验
                    Element tPartElement = ((Element) tPartList.get(nPartIndex));
                    String tPartName = tPartElement.getName();
                    if (tPartName.trim().toUpperCase().equals("PART"))
                    {
                        //得到XML配置模板描述中该Sheet元素中内部该Part元素中的第一孩子元素DataSourceSet元素中第一孩子Params元素及其内部元素的Field
                        Element tParamsElement = (Element) (((List) ((((Element) (((
                                List) tPartElement.getChildren()).get(0)))).
                                getChildren())).get(0));
                        String tParamsElementName = tParamsElement.getName();
                        List tParamsFieldList = (List) ((tParamsElement).
                                getChildren());
                        int tParamsFieldNum = tParamsFieldList.size();
                        //生成参数信息

                        //生成系统级参数
                        nParamsValueSet.setNameAndValue("SYS_FILENAMEPARAM",
                                mDataExcelFileName);
                        //生成参数节点
                        Element tSysParamRootElement = new Element("PARAM");
                        tSysParamRootElement.addAttribute("name",
                                "SYS_FILENAMEPARAM");
                        tSysParamRootElement.addAttribute("src",
                                mDataExcelFileName);
                        //添加一系统级参数节点
                        Element tParamsRootElement = new Element("PARAMS");
                        tParamsRootElement.addContent(tSysParamRootElement);
                        //生成其他参数信息
                        for (int nParamsFieldIndex = 0;
                                nParamsFieldIndex < tParamsFieldNum;
                                nParamsFieldIndex++)
                        {
                            //获取XML配置模板描述中的每一个Field元素
                            //获得该Sheet的最大行数
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
                            //计算出所有参数
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
                                tRealSrcAttributeValue.equals("-1")) //当参数PARAM1的Src取值为-1是，则终止行号为最大行数-1
                            {
                                tRealSrcAttributeValue = String.valueOf(nMaxRow -
                                        1);
                            }
                            nParamsValueSet.setNameAndValue(tNameAttributeValue,
                                    tRealSrcAttributeValue);

                            //生成参数节点
                            Element tParamRootElement = new Element("PARAM");
                            tParamRootElement.addAttribute("name",
                                    tNameAttributeValue);
                            tParamRootElement.addAttribute("src",
                                    tRealSrcAttributeValue);
                            //添加一参数节点
                            tParamsRootElement.addContent(tParamRootElement);
                        }

                        //添加参数集合节点
                        tPartRootElement.addContent(tParamsRootElement);

                        //按照XML配置模板描述中该Sheet元素中内部该Part元素中的第一孩子元素DataSourceSet元素中第二孩子ColConfig元素内部元素的Field的描述对物理Excel表进行取数，形成中间表数据
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

                        //逐行生成Row信息
                        int tID = 0;
                        for (int nRowIndex = tStartRow; nRowIndex <= tEndRow;
                                             nRowIndex++)
                        {
                            //生成该行的根节点及根属性
                            Element tOneRowRootElement = new Element("ROW");
                            //tOneRowRootElement.addAttribute("name","ROW"+String.valueOf(tID));
                            tID++;
                            //生成该行根节点的孩子节点
                            TransferData tempParamsValueSet = new TransferData(); //该行的辅助参数
                            //生成XML配置模板描述中该Sheet元素中内部该Part元素中的第一孩子元素DataSourceSet元素中第二孩子ColConfig元素元素内部元素的Field
                            for (int nColConfigFieldIndex = 0;
                                    nColConfigFieldIndex < tColConfigFieldNum;
                                    nColConfigFieldIndex++)
                            {
                                //XML配置模板描述中Row中的Field元素本身校验
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

                                //生成该Field节点的根节点及属性name
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
//              //添加一个Field节点的到父节点Row中
//               tOneRowRootElement.addContent(tOneFieldRootElement);
                                //添加一个Field节点的到父节点Row某以属性中
                                tOneRowRootElement.addAttribute(
                                        tNameAttributeValue,
                                        tTrueSrcAttributeValue);

                                //添加改行中该列的计算参数
                                if (tTrueSrcAttributeValue == null)
                                {
                                    tTrueSrcAttributeValue = "";
                                }
//               tempParamsValueSet.setNameAndValue("COL"+nColConfigFieldIndex,tTrueSrcAttributeValue) ;
                                tempParamsValueSet.setNameAndValue(
                                        tNameAttributeValue,
                                        tTrueSrcAttributeValue);

                            }
                            //添加一个Row节点的到父节点Part中
                            tPartRootElement.addContent(tOneRowRootElement);
                        }
                    }
                    tSheetRootElement.addContent(tPartRootElement);
                    System.err.println("第" + nSheetIndex + "Sheet中的第" +
                                       nPartIndex + "PartXML中间数据生成已生成");

                }
                mXMLMiddleRoot.addContent(tSheetRootElement);
                System.err.println("第" + nSheetIndex + "SheetXML中间数据生成已生成");
            }
            System.err.println("所有的Sheet的XML中间数据生成已生成");
            //myDocument = new org.jdom.Document(myElement);
            mXMLImplMiddle = new Document(mXMLMiddleRoot);
        }
        catch (Exception e)
        {
            System.err.println("XML中间数据生成失败:" + e.getMessage());
            return false;
        }

        return true;

    }

    /**
     * 生成目标XML数据集合（只包含为每个物理Sheet所提取的所有Row信息）
     * @return boolean
     */
    private boolean createTargetXML()
    {
        //mXMLMiddleRoot记录了中间数据结果
        mXMLTargetRoot = new org.jdom.Element("XmlRoot");
        try
        {
            //查找XML配置模板描述中的Sheet元素
            List tSheetList = mXMLImplConfig.getChildren();
            List tMiddleSheetList = mXMLMiddleRoot.getChildren();
            int tSheetListNum = tSheetList.size();
            if (tSheetListNum < 1)
            {
                buildError("checkMatch", "XML配置模板描述中缺少Sheet元素的描述");
                return false;
            }

            for (int nSheetIndex = 0; nSheetIndex < tSheetListNum; nSheetIndex++)
            {
                //XML配置模板描述中Sheet元素本身
                Element tSheetElement = ((Element) tSheetList.get(nSheetIndex));
                Element tMiddleSheetElement = ((Element) tMiddleSheetList.get(
                        nSheetIndex));
                //生成一个Sheet节点
                Element tSheetRootElement = new Element("SHEET");
                tSheetRootElement.addAttribute("id", String.valueOf(nSheetIndex));

                String tDestIndex = tSheetElement.getAttributeValue("dest");
                if (tDestIndex == null || tDestIndex.trim().equals("")) //没有描述则认为和sheet顺序号相同
                {
                    tDestIndex = String.valueOf(nSheetIndex);
                }
                int nDestIndex = Integer.parseInt(tDestIndex);

                //XML配置模板描述中该Sheet元素中内部其他元素
                List tPartList = tSheetElement.getChildren();
                List tMiddlePartList = tMiddleSheetElement.getChildren();
                //XML配置模板描述中该Sheet元素中内部Part元素本身的校验
                int tPartListNum = tPartList.size();
                //XML配置模板描述中该Sheet元素中内部Part元素校验
                for (int nPartIndex = 0; nPartIndex < tPartListNum; nPartIndex++)
                {
                    int nMaxRow = 0;
                    nMaxRow = getMaxRow(nSheetIndex);
                    //XML配置模板描述中Sheet元素内部Part元素本身校验
                    Element tPartElement = ((Element) tPartList.get(nPartIndex));
                    Element tMiddlePartElement = ((Element) tMiddlePartList.get(
                            nPartIndex));
                    //XML配置模板描述中Sheet元素内部Part元素中的参数存储对象
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

                        //得到XML配置模板描述中该Sheet元素中内部该Part元素中的第二个孩子元素DataDestinationSet元素中及其内部元素的ROW
                        Element tDataDestinationSetsElement = (Element) ((((
                                Element) (((List) tPartElement.getChildren()).
                                          get(1)))));
                        String tDataDestinationSetsElementElementName =
                                tDataDestinationSetsElement.getName();
                        //预先取得中间结果集合中的直接参数Params到nParamsValueSet
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

                        //更据XML配置模板描述中DataDestinationSet中的Row元素的描述逐一生成行数据
                        List tRowList = (List) ((tDataDestinationSetsElement).
                                                getChildren());
                        List tMiddleRowList = (List) tMiddlePartElement.
                                              getChildren(); //从第二个孩子节点开始是Row节点
                        int tRowNum = tRowList.size();
                        int tTotalRowCount = 0;
                        for (int nRowIndex = 0; nRowIndex < tRowNum; nRowIndex++)
                        {
                            //XML配置模板描述中DataDestinationSet中的Row元素的校验只针对本身校验，而不对其内部再进行校验
                            //XML配置模板描述中DataDestinationSet中的Row元素本身校验
                            Element tDataDestinationSetRowElement = ((Element)
                                    tRowList.get(nRowIndex));
                            String tDataDestinationSetRowName =
                                    tDataDestinationSetRowElement.getName();
                            String tTypeAttributeValue =
                                    tDataDestinationSetRowElement.
                                    getAttributeValue("type");
                            if (tTypeAttributeValue.trim().equals("1")) //Row值直接到中间数据源中取得的连续行ROW(startRowIndex,endRowIndex)
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
                                    //往目标结果集合中添加一个Row节点
                                    tPartRootElement.addContent(tRowRootElement);
                                    tTotalRowCount++;
                                }
                            }
                            else if (tTypeAttributeValue.trim().equals("2")) //Row值通过计算到物理数据源Excel表的中间级数据中获得
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
                                    //生成该行根节点的属性值点
                                    TransferData tempParamsValueSet = new
                                            TransferData(); //该行的辅助参数
                                    //生成XML配置模板描述中该Sheet元素中内部该Part元素中的第一孩子元素DataSourceSet元素中第二孩子ColConfig元素元素内部元素的Field
                                    for (int nFieldIndex = 0;
                                            nFieldIndex <
                                            tDataDestinationSetRowFieldListNum;
                                            nFieldIndex++)
                                    {
                                        //XML配置模板描述中Row中的Field元素本身校验
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
                                        //生成该Field节点的根节点及属性name
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
                                        //添加一个Field节点的到父节点Row某以属性中
                                        tOneRowRootElement.addAttribute(
                                                tNameFieldAttributeValue,
                                                tTrueSrcAttributeValue);

                                        //添加改行中该列的计算参数
                                        if (tTrueSrcAttributeValue == null)
                                        {
                                            tTrueSrcAttributeValue = "";
                                        }
//               tempParamsValueSet.setNameAndValue("COL"+nColConfigFieldIndex,tTrueSrcAttributeValue) ;
                                        tempParamsValueSet.setNameAndValue(
                                                tNameFieldAttributeValue,
                                                tTrueSrcAttributeValue);
                                    }
                                    //添加一个Row节点的到父节点Part中
                                    tPartRootElement.addContent(
                                            tOneRowRootElement);
                                }
                            }
                            else
                            {
                                buildError("checkMatch",
                                           "XML配置模板描述中第" + nSheetIndex +
                                           "Sheet元素中的第" + nPartIndex +
                                           "个Part元素的DataDestinationSet元素中第" +
                                           nRowIndex + "个Row元素Type属性值的描述有误");
                                return false;
                            }
                        }
                        tSheetRootElement.addContent(tPartRootElement);
                        System.err.println("第" + nSheetIndex + "Sheet中的第" +
                                           nPartIndex + "PartXML目标XML数据生成已生成");
                    }

                }
                mXMLTargetRoot.addContent(tSheetRootElement);
                System.err.println("第" + nSheetIndex + "SheetXML目标XML数据生成已生成");

            }
            System.err.println("所有的Sheet的目标XML数据生成已生成");
            mXMLImplExport = new Document(mXMLTargetRoot);

        }
        catch (Exception e)
        {
            System.err.println("目标XML数据生成失败:" + e.getMessage());
            return false;
        }

        return true;

    }

    /**
     * 校验格式匹配
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
     * 参数定义元素中参数本身是有SQL计算获得，其又有显示的Params描述时，对描述的个参数进行格式匹配校验
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
            if (tParamsSrcAttributeValue.trim().length() < 8) //型如:Params(ParamName|ParamType;ParamName|ParamType);可以为Params()
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
                if (tType == 1) //参数型如：Sheet(0,1,2)
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
                    if (nRowIndex == -1 || nColindex == -1) //直接参数定义中对物理地址引用时没有有当前行当前列的感念
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
                if (tType == 2 || tType == 0) //不支持param又是SQl或是直接自定值
                {
                    return false;
                }
                if (tType == 3) //直接引用前驱已定义的param
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
     * 列定义元素中，其Src属性定义本身是有SQL计算获得，其引用了参数，又进行了显示的Params声明时，对声明的个参数进行格式匹配校验
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
            if (tParamsSrcAttributeValue.trim().length() < 8) //型如:Params(ParamName|ParamType;ParamName|ParamType);可以为Params()
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
                if (tType == 1) //参数型如：Sheet(0,1,2)
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
                    if (nRowIndex == -1) //列定义中对物理地址引用时可以有当前行当前页的感念
                    {
                        nRowIndex = tRowIndex;
                    }
                    if (nSheetIndex == -1) //列定义中对物理地址引用时可以有当前行当前页的感念
                    {
                        nSheetIndex = tSheetIndex;
                    }
                    if (nColIndex == -1) //列定义中对物理地址引用时没有当前列的感念
                    {
                        return false;
                    }
                    if (!checkExcelPosition(nSheetIndex, nRowIndex, nColIndex))
                    {
                        return false;
                    }
                }
                if (tType == 2 || tType == 0) //不支持param又是SQl或是直接自定值
                {
                    return false;
                }
                if (tType == 3) //直接引用前驱已定义的param
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
                if (tType == 4) //直接引用前驱已定义的当前行辅助列参数
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
     * 中间数据集合中列定义元素中，其Src属性定义本身是有SQL计算获得，其引用了参数，又进行了显示的Params声明时，对声明的个参数进行格式匹配校验
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
            if (tParamType.equals("1")) //参数型如：Sheet(0,1,2)
            {
                String[] tPositionIndex = getParseExcelPositon(tParamName);
                int nSheetIndex = Integer.parseInt(tPositionIndex[0]);
                int nRowIndex = Integer.parseInt(tPositionIndex[1]);
                int nColIndex = Integer.parseInt(tPositionIndex[2]);
                if (nRowIndex == -1) //列定义中对物理地址引用时可以有当前行当前页的感念
                {
                    nRowIndex = tRowIndex;
                }
                if (nSheetIndex == -1) //列定义中对物理地址引用时可以有当前行当前页的感念
                {
                    nSheetIndex = tSheetIndex;
                }
                tParamValue = getExcelValue(nSheetIndex, nRowIndex, nColIndex);
                if (tParamValue == null)
                {
                    tParamValue = "";
                }
            }
            if (tParamType.equals("2") || tParamType.equals("0")) //不支持param又是SQl或是直接自定值
            {
                return null;
            }
            if (tParamType.equals("3")) //直接引用前驱已定义的param
            {
                tParamValue = (String) tTransferData.getValueByName(tParamName);
                if (tParamValue == null)
                {
                    tParamValue = "";
                }
            }
            if (tParamType.equals("4")) //直接引用前驱已定义的当前行辅助列参数
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
     * 直接参数定义元素中，其Src属性定义本身是有SQL计算获得，其引用了参数，又进行了显示的Params声明时，对声明的个参数进行格式匹配校验
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
            if (tParamType == "1") //参数型如：Sheet(0,1,2)
            {
                String[] tPositionIndex = getParseExcelPositon(tParamName);
                int nSheetIndex = Integer.parseInt(tPositionIndex[0]);
                int nRowIndex = Integer.parseInt(tPositionIndex[1]);
                int nColIndex = Integer.parseInt(tPositionIndex[2]);
                if (nSheetIndex == -1) //列定义中对物理地址引用时可以有当前行的感念
                {
                    nSheetIndex = tSheetIndex;
                }
                tParamValue = getExcelValue(nSheetIndex, nRowIndex, nColIndex);
                if (tParamValue == null)
                {
                    tParamValue = "";
                }
            }
            if (tParamType == "2" || tParamType == "0") //不支持param又是SQl或是直接自定值
            {
                return null;
            }
            if (tParamType == "3") //直接引用前驱已定义的param
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
     * 直接参数定义元素中，其Src属性定义本身是有SQL计算获得，其引用了参数，又进行了显示的Params声明时，对声明的个参数进行格式匹配校验
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
            if (tParamType.equals("1")) //参数型如：Sheet(0,1,2)
            {
                String[] tPositionIndex = getParseExcelPositon(tParamName);
                int nSheetIndex = Integer.parseInt(tPositionIndex[0]);
                int nRowIndex = Integer.parseInt(tPositionIndex[1]);
                int nColIndex = Integer.parseInt(tPositionIndex[2]);
                if (nSheetIndex == -1) //列定义中对物理地址引用时可以有当前行当前页的感念
                {
                    nSheetIndex = tSheetIndex;
                }
                tParamValue = getExcelValue(nSheetIndex, nRowIndex, nColIndex);
                if (tParamValue == null)
                {
                    tParamValue = "";
                }
            }
            if (tParamType.equals("2") || tParamType.equals("0")) //不支持param又是SQl或是直接自定值
            {
                return null;
            }
            if (tParamType.equals("3")) //直接引用前驱已定义的param
            {
                tParamValue = (String) tTransferData.getValueByName(tParamName);
                if (tParamValue == null)
                {
                    tParamValue = "";
                }
            }
            if (tParamType.equals("4")) //直接引用前驱已定义的param
            {
                tParamValue = (String) tempTransferData.getValueByName(
                        tParamName);
                if (tParamValue == null)
                {
                    tParamValue = "";
                }
            }
            if (tParamType.equals("5")) //引用中间结果集合中前驱已定义的某行的某列
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
     * 目标行列自定义元素中，其Src属性定义本身是有SQL计算获得，其引用了参数，又进行了显示的Params声明时，对声明的个参数进行格式匹配校验
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
            if (tParamsSrcAttributeValue.trim().length() < 8) //型如:Params(ParamName|ParamType;ParamName|ParamType);可以为Params()
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

            if (tSubString != null && tSubString.length < 1) //可以为Params()
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
                if (tType == 1) //参数型如：Sheet(0,1,2)
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
                    if (nSheetIndex == -1) //列定义中对物理地址引用时可以有当前页的感念
                    {
                        nSheetIndex = tSheetIndex;
                    }
                    if (nRowIndex == -1) //列定义中对物理地址引用时没有当前行的感念
                    {
                        return false;
                    }
                    if (nColIndex == -1) //列定义中对物理地址引用时没有当前列的感念
                    {
                        return false;
                    }
                    if (!checkExcelPosition(nSheetIndex, nRowIndex, nColIndex))
                    {
                        return false;
                    }
                }
                if (tType == 2 || tType == 0) //不支持param又是SQl或是直接自定值
                {
                    return false;
                }
                if (tType == 3) //引用前驱已定义的直接参数param
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
                if (tType == 4) //直接引用前驱已定义的当前行辅助列参数
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
                if (tType == 5) //引用中间结果集合中前驱已定义的某行的某列
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
     * 校验格式匹配
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
     * 校验DataSourceSet的格式匹配
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
//XML配置模板描述中该Sheet元素中内部该Part元素中的DataSourceSet元素本身的校验
        String tDataSourceSetName = tDataSourceSetElement.getName();
        if (tDataSourceSetName == null ||
            !tDataSourceSetName.trim().toUpperCase().equals("DATASOURCESET"))
        {
            buildError("checkMatch",
                       "XML配置模板描述中第" + nSheetIndex + "个Sheet元素中的第" + nPartIndex +
                       "个Part元素的DataSourceSet元素名描述有误");
            return false;
        }
        //XML配置模板描述中该Sheet元素中内部该Part元素中的第一孩子元素：DataSourceSet的内部元素的校验
        List tDataSourceSetChildenList = tDataSourceSetElement.getChildren();
        //XML配置模板描述中该Sheet元素中内部该Part元素中的DataSourceSet元素本身的校验
        int tDataSourceSetChildenNum = tDataSourceSetChildenList.size();
        if (tDataSourceSetChildenNum != 2)
        {
            buildError("checkMatch",
                       "XML配置模板描述中第" + nSheetIndex + "个Sheet元素中的第" + nPartIndex +
                       "个Part元素的DataSourceSet元素缺少必备的Params和ROW元素的描述");
            return false;
        }

        //XML配置模板描述中该Sheet元素中内部该Part元素中的第一孩子元素DataSourceSet元素中第一孩子Params元素的校验
        Element tParamsElement = ((Element) tDataSourceSetChildenList.get(0));
        //XML配置模板描述中该Sheet元素中内部该Part元素中的第一孩子元素DataSourceSet元素中第一孩子Params元素自身的校验
        String tParamsName = tParamsElement.getName();
        if (tParamsName == null ||
            !tParamsName.trim().toUpperCase().equals("PARAMS"))
        {
            buildError("checkMatch",
                       "XML配置模板描述中第" + nSheetIndex + "个Sheet元素中的第" + nPartIndex +
                       "个Part元素的DataSourceSet元素中第一元素:Params名称描述有误");
            return false;
        }
        //XML配置模板描述中该Sheet元素中内部该Part元素中的第一孩子元素DataSourceSet元素中第一孩子Params元素内部元素的Field校验
        List tParamsFieldList = tParamsElement.getChildren();
        int tParamsFieldNum = tParamsFieldList.size();
        if (tParamsFieldNum < 2)
        {
            buildError("checkMatch",
                       "XML配置模板描述中第" + nSheetIndex + "个Sheet元素中的第" + nPartIndex +
                       "个Part元素的DataSourceSet元素中第一元素:Params中的缺少必要的两个Feild元素其中属性name值为PARAM0和PARAM1");
            return false;
        }
        for (int nParamsFieldIndex = 0; nParamsFieldIndex < tParamsFieldNum;
                                     nParamsFieldIndex++)
        {
            //XML配置模板描述中Field元素本身校验
            //获得该Sheet的最大行数
            String tSheetMaxRow = "SHEET" + String.valueOf(nDestSheetIndex);
            String tStrMaxRow = (String) mExcelSheetMaxRowSet.getValueByName(
                    tSheetMaxRow);
            if (tStrMaxRow == null || tStrMaxRow.trim().equals("0"))
            {
                buildError("checkMatch",
                           "XML配置模板描述中第" + nSheetIndex + "个Sheet元素中的第" +
                           nPartIndex +
                           "个Part元素的DataSourceSet元素中第一元素:Params中的缺少必要的两个Feild元素其中属性name值为PARAM0和PARAM1");
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
                           "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                           nPartIndex +
                           "个Part元素的DataSourceSet元素中第一元素:Params中的第" +
                           nParamsFieldIndex + "个Feild元素名称描述有误");
                return false;
            }
            //第一个Field元素(起始行元素)校验
            if (nParamsFieldIndex == 0)
            {
                String tTypeAttributeValue = tFieldElement.getAttributeValue(
                        "type");
                if (tTypeAttributeValue == null ||
                    tTypeAttributeValue.trim().equals(""))
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                               nPartIndex +
                               "个Part元素的DataSourceSet元素中第一元素:Params中的第一个Feild元素缺少type属性值的描述");
                    return false;
                }
                if (!tTypeAttributeValue.trim().equals("0"))
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                               nPartIndex +
                               "个Part元素的DataSourceSet元素中第一元素:Params中的第一个Feild元素缺少type属性值必须为0");
                    return false;
                }
                //获得指定的起始行数
                String tStrStartRow = tFieldElement.getAttributeValue("src");
                String tStrNameAttributeValue = tFieldElement.getAttributeValue(
                        "name");

                if (tStrStartRow == null || tStrStartRow.trim().equals(""))
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                               nPartIndex +
                               "个Part元素的DataSourceSet元素中第一元素:Params中的第一个Feild元素（指定起始行元素）缺少src属性值的描述有误");
                    return false;
                }
                nStartRow = Integer.parseInt(tStrStartRow);
                // 判断配置描述中的请求的Sheet页号是否小于等于物理EXCEL表中提供的Sheet页数
                if (nStartRow < 0)
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                               nPartIndex +
                               "个Part元素的DataSourceSet元素中第一元素:Params中的第一个Feild元素（指定起始行元素）的Scr属性值的描述行数小于0");
                    return false;
                }
                if (nStartRow > nMaxRow)
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                               nPartIndex + "个Part元素的DataSourceSet元素中第一元素:Params中的第一个Feild元素（指定起始行元素）的Scr属性值的描述行数大于该Sheet的最大行数");
                    return false;
                }
                if (tStrNameAttributeValue == null ||
                    !tStrNameAttributeValue.trim().equals("PARAM0"))
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                               nPartIndex +
                               "个Part元素的DataSourceSet元素中第一元素:Params中的第一个Feild元素（指定起始行元素）name属性值的描述不为PARAM0有误");
                    return false;
                }
                //记录前驱已定义的参数，以便检验后续参数引用的校验
                nParamsValueSet.setNameAndValue(tStrNameAttributeValue,
                                                tStrStartRow);
            }
            else if (nParamsFieldIndex == 1)
            {
                //第二个Field元素（该终止行元素）校验
                String tTypeAttributeValue = tFieldElement.getAttributeValue(
                        "type");
                String tStrNameAttributeValue = tFieldElement.getAttributeValue(
                        "name");
                if (tTypeAttributeValue == null ||
                    tTypeAttributeValue.trim().equals(""))
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                               nPartIndex +
                               "个Part元素的DataSourceSet元素中第一元素:Params中的第二个Feild元素缺少type属性值的描述");
                    return false;
                }
                if (!tTypeAttributeValue.trim().equals("0"))
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                               nPartIndex +
                               "个Part元素的DataSourceSet元素中第一元素:Params中的第二个Feild元素缺少type属性值必须为0");
                    return false;
                }
                if (tTypeAttributeValue.trim().equals("0"))
                {
                    //获得指定的终止行数
                    String tStrEndRow = tFieldElement.getAttributeValue("src");
                    if (tStrEndRow == null || tStrEndRow.trim().equals(""))
                    {
                        buildError("checkMatch",
                                   "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                                   nPartIndex +
                                   "个Part元素的DataSourceSet元素中第一元素:Params中的第一个Feild元素（指定终止行元素）缺少src属性值的描述有误");
                        return false;
                    }
                    nEndRow = Integer.parseInt(tStrEndRow);
                    // 判断配置描述中的请求的Sheet页号是否小于等于物理EXCEL表中提供的Sheet页数
                    if (nEndRow < 0 && nEndRow != -1)
                    {
                        buildError("checkMatch",
                                   "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                                   nPartIndex +
                                   "个Part元素的DataSourceSet元素中第一元素:Params中的第二个Feild元素（指定终止行元素）的Scr属性值的描述行数小于0且不为-1");
                        return false;
                    }
                    if (nEndRow > nMaxRow)
                    {
                        buildError("checkMatch",
                                   "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                                   nPartIndex + "个Part元素的DataSourceSet元素中第一元素:Params中的第二个Feild元素（指定终止行元素）的Scr属性值的描述行数大于该Sheet的最大行数");
                        return false;
                    }
                    if (nStartRow > nEndRow && nEndRow != -1)
                    {
                        buildError("checkMatch",
                                   "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                                   nPartIndex +
                                   "个Part元素的DataSourceSet元素中第一元素:Params中的指定起始行元素的Src属性值大于终止行元素的Src属性值");
                        return false;
                    }
                    if (nEndRow == -1) //表示终止行为最后一行
                    {
                        tStrEndRow = String.valueOf(nMaxRow - 1);

                    }
                    if (tStrNameAttributeValue == null ||
                        !tStrNameAttributeValue.trim().equals("PARAM1"))
                    {
                        buildError("checkMatch",
                                   "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                                   nPartIndex +
                                   "个Part元素的DataSourceSet元素中第一元素:Params中的第二个Feild元素（指定终止行元素）name属性值的描述不为PARAM1有误");
                        return false;
                    }
                    //记录前驱已定义的参数，以便检验后续参数引用的校验
                    nParamsValueSet.setNameAndValue(tStrNameAttributeValue,
                            tStrEndRow);
                }

            }
            else
            {
                //其他Field元素校验
                String tTypeAttributeValue = tFieldElement.getAttributeValue(
                        "type");
                String tStrNameAttributeValue = tFieldElement.getAttributeValue(
                        "name");

                if (tTypeAttributeValue == null ||
                    tTypeAttributeValue.trim().equals(""))
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                               nPartIndex +
                               "个Part元素的DataSourceSet元素中第一元素:Params中的第" +
                               nPartIndex + "个Feild元素缺少type属性值的描述");
                    return false;
                }

                if (tTypeAttributeValue.trim().equals("0")) //属性src值是自定义值
                {
                }
                else if (tTypeAttributeValue.trim().equals("1")) //属性src值是直接到物理数据源Excel表取得
                {
                    String tFieldAttributeValue = tFieldElement.
                                                  getAttributeValue("src");
                    if (tFieldAttributeValue == null ||
                        tFieldAttributeValue.trim().equals(""))
                    {
                        buildError("checkMatch",
                                   "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                                   nPartIndex +
                                   "个Part元素的DataSourceSet元素中第一元素:Params中的第" +
                                   nPartIndex + "个Feild元素缺少src属性值的描述");
                        return false;
                    }
                    if (!checkExcelPositionDesc(tFieldAttributeValue)) //型如Sheet(0,1,0)
                    {
                        buildError("checkMatch",
                                   "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                                   nPartIndex +
                                   "个Part元素的DataSourceSet元素中第一元素:Params中的第" +
                                   nParamsFieldIndex + "个Feild元素Src属性值的描述格式有误");
                        return false;
                    }
                    String[] tSubString = getParseExcelPositon(
                            tFieldAttributeValue);
                    if (!checkExcelPosition(Integer.parseInt(tSubString[0]),
                                            Integer.parseInt(tSubString[1]),
                                            Integer.parseInt(tSubString[2])))
                    {
                        buildError("checkMatch",
                                   "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                                   nPartIndex +
                                   "个Part元素的DataSourceSet元素中第一元素:Params中的第" +
                                   nParamsFieldIndex +
                                   "个Feild元素Src属性值描述的参数取值不符和物理Excel范围");
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
                                   "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                                   nPartIndex +
                                   "个Part元素的DataSourceSet元素中第一元素:Params中的第" +
                                   nParamsFieldIndex + "个Feild元素缺少src属性值的描述");
                        return false;
                    }
                    if (tParamsAttributeValue == null ||
                        tParamsAttributeValue.trim().equals(""))
                    {
                    }
                    else //当type=2 即：sql运算时，如有显示参数定义引用时，进行相应校验
                    {
                        if (!checkExcelParamsAttributeDesc(
                                tParamsAttributeValue, nParamsValueSet,
                                nSheetIndex))
                        {
                            buildError("checkMatch",
                                       "XML配置模板描述中第" + nSheetIndex +
                                       "Sheet元素中的第" + nPartIndex +
                                       "个Part元素的DataSourceSet元素中第一元素:Params中的第" +
                                       nParamsFieldIndex +
                                       "个Feild元素params属性值的描述有误");
                            return false;
                        }
                    }
                }
                else
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                               nPartIndex +
                               "个Part元素的DataSourceSet元素中第一元素:Params中的第" +
                               nParamsFieldIndex + "个Feild元素Type属性值的描述有误");
                    return false;
                }
                if (tStrNameAttributeValue == null ||
                    tStrNameAttributeValue.trim().equals(""))
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                               nPartIndex +
                               "个Part元素的DataSourceSet元素中第一元素:Params中的第二个Feild元素（指定终止行元素）name属性值的描述不能为空");
                    return false;
                }
                //记录前驱已定义的参数，以便检验后续参数引用的校验
                nParamsValueSet.setNameAndValue(tStrNameAttributeValue,
                                                "DEFAUT");
            }
        }

        //XML配置模板描述中该Sheet元素中内部该Part元素中的第一孩子元素DataSourceSet元素中第二孩子ColConfig元素元素校验
        Element tColConfigElement = ((Element) tDataSourceSetChildenList.get(1));
        //XML配置模板描述中该Sheet元素中内部该Part元素中的第一孩子元素DataSourceSet元素中第一孩子ColConfig元素自身的校验
        String tRowName = tColConfigElement.getName();
        if (tRowName == null ||
            !tRowName.trim().toUpperCase().equals("COLCONFIG"))
        {
            buildError("checkMatch",
                       "XML配置模板描述中第" + nSheetIndex + "个Sheet元素中的第" + nPartIndex +
                       "个Part元素的DataSourceSet元素中第二元素:ColConfig名称描述有误");
            return false;
        }
        //XML配置模板描述中该Sheet元素中内部该Part元素中的第一孩子元素DataSourceSet元素中第二孩子Row元素内部元素的Field校验
        List tColConfigFieldList = tColConfigElement.getChildren();
        int tColConfigFieldNum = tColConfigFieldList.size();
        if (tColConfigFieldNum < 1)
        {
            buildError("checkMatch",
                       "XML配置模板描述中第" + nSheetIndex + "个Sheet元素中的第" + nPartIndex +
                       "个Part元素的DataSourceSet元素中第二元素:ColConfig中的应至少有一个Feild元素");
            return false;
        }

        //记录改行前驱已定义列参数
        TransferData nCurrentRowParamsValueSet = new TransferData();
        for (int nRowFieldIndex = 0; nRowFieldIndex < tColConfigFieldNum;
                                  nRowFieldIndex++)
        {
            //XML配置模板描述中Row中的Field元素本身校验
            Element tFieldElement = ((Element) tColConfigFieldList.get(
                    nRowFieldIndex));
            String tFieldName = tFieldElement.getName();
            if (!tFieldName.trim().toUpperCase().equals("FIELD"))
            {
                buildError("checkMatch",
                           "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                           nPartIndex +
                           "个Part元素的DataSourceSet元素中第二元素:ColConfig中的第" +
                           nRowFieldIndex + "个Feild元素名称描述有误");
                return false;
            }
            String tTypeAttributeValue = tFieldElement.getAttributeValue("type");
            if (tTypeAttributeValue == null ||
                tTypeAttributeValue.trim().equals(""))
            {
                buildError("checkMatch",
                           "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                           nPartIndex +
                           "个Part元素的DataSourceSet元素中第二元素:ColConfig中的第" +
                           nRowFieldIndex + "个Feild元素缺少type属性值的描述");
                return false;
            }
            String tNameAttributeValue = tFieldElement.getAttributeValue("name");
            if (tNameAttributeValue == null ||
                tNameAttributeValue.trim().equals(""))
            {
                buildError("checkMatch",
                           "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                           nPartIndex +
                           "个Part元素的DataSourceSet元素中第二元素:ColConfig中的第" +
                           nRowFieldIndex + "个Feild元素缺少name属性值的描述");
                return false;
            }

            if (tTypeAttributeValue.trim().equals("0")) //属性src值是自定义值
            {}
            else if (tTypeAttributeValue.trim().equals("1")) //属性src值是直接到物理数据源中取得
            {
                String tFieldAttributeValue = tFieldElement.getAttributeValue(
                        "src");
                if (tFieldAttributeValue == null ||
                    tFieldAttributeValue.trim().equals(""))
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                               nPartIndex +
                               "个Part元素的DataSourceSet元素中第二元素:ColConfig中的第" +
                               nRowFieldIndex + "个Feild元素缺少src属性值的描述");
                    return false;
                }
                if (!checkExcelPositionDesc(tFieldAttributeValue))
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                               nPartIndex +
                               "个Part元素的DataSourceSet元素中第二元素:ColConfig中的第" +
                               nRowFieldIndex +
                               "个Feild元素src属性值的描述有误（应如:COL0,COL1）");
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
                                   "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                                   nPartIndex +
                                   "个Part元素的DataSourceSet元素中第二元素:ColConfig中的第" +
                                   nRowFieldIndex +
                                   "个Feild元素src属性值的描述的物理取数地址超出Excel表取数范围");
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
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                               nPartIndex +
                               "个Part元素的DataSourceSet元素中第二元素:ColConfig中的第" +
                               nRowFieldIndex + "个Feild元素缺少src属性值的描述");
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
                                   "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                                   nPartIndex +
                                   "个Part元素的DataSourceSet元素中第二元素:ColConfig中的第" +
                                   nRowFieldIndex + "个Feild元素params属性值的描述有误");
                        return false;
                    }
                }
            }
            else if (tTypeAttributeValue.trim().equals("3")) //属性src值是从参数中直接取得
            {
                String tFieldAttributeValue = tFieldElement.getAttributeValue(
                        "src");
                if (tFieldAttributeValue == null ||
                    tFieldAttributeValue.trim().equals(""))
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                               nPartIndex +
                               "个Part元素的DataSourceSet元素中第二元素:ColConfig中的第" +
                               nRowFieldIndex + "个Feild元素缺少src属性值的描述");
                    return false;
                }
                if (!isParamExist(nParamsValueSet, tFieldAttributeValue))
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                               nPartIndex +
                               "个Part元素的DataSourceSet元素中第二元素:ColConfig中的第" +
                               nRowFieldIndex + "个Feild元素src属性引用的直接参数未定义");
                    return false;
                }

            }
            else if (tTypeAttributeValue.trim().equals("4")) //属性src值是从该行辅助参数中直接取得
            {
                String tFieldAttributeValue = tFieldElement.getAttributeValue(
                        "src");
                if (tFieldAttributeValue == null ||
                    tFieldAttributeValue.trim().equals(""))
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                               nPartIndex +
                               "个Part元素的DataSourceSet元素中第二元素:ColConfig中的第" +
                               nRowFieldIndex + "个Feild元素缺少src属性值的描述");
                    return false;
                }
                if (!isParamExist(nCurrentRowParamsValueSet,
                                  tFieldAttributeValue))
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                               nPartIndex +
                               "个Part元素的DataSourceSet元素中第二元素:ColConfig中的第" +
                               nRowFieldIndex + "个Feild元素src属性引用的辅助行参数未前驱定义");
                    return false;
                }
            }
            else
            {
                buildError("checkMatch",
                           "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                           nPartIndex +
                           "个Part元素的DataSourceSet元素中第二元素:ColConfig中的第" +
                           nRowFieldIndex + "个Feild元素type属性值的描述有误");
                return false;
            }
            //添加该行该列参数
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
     * 校验DataDestinationSet的格式匹配
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
        //XML配置模板描述中该Sheet元素中内部该Part元素中第二个孩子DataDestinationSet元素的自身的校验
        String tDataDestinationSetName = tDataDestinationSetElement.getName();
        if (tDataDestinationSetName == null ||
            !tDataDestinationSetName.trim().
            toUpperCase().equals("DATADESTINATIONSET"))
        {
            buildError("checkMatch",
                       "XML配置模板描述中第" + nSheetIndex + "个Sheet元素中的第" + nPartIndex +
                       "个Part元素的DataDestinationSet元素名描述有误");
            return false;
        }
        //XML配置模板描述中该Sheet元素中内部该Part元素中的第二孩子元素：DataDestinationSet的内部元素的校验
        List tDataDestinationSetChildenList = tDataDestinationSetElement.
                                              getChildren();
        int tDataDestinationSetChildenNum = tDataDestinationSetChildenList.size();
        if (tDataDestinationSetChildenNum < 1)
        {
            buildError("checkMatch",
                       "XML配置模板描述中第" + nSheetIndex + "个Sheet元素中的第" + nPartIndex +
                       "个Part元素的DataDestinationSet元素缺少ROW元素的描述");
            return false;
        }
        for (int nRowIndex = 0; nRowIndex < tDataDestinationSetChildenNum;
                             nRowIndex++)
        {
            //XML配置模板描述中DataDestinationSet中的Row元素的校验只针对本身校验，而不对其内部再进行校验
            //XML配置模板描述中DataDestinationSet中的Row元素本身校验
            Element tDataDestinationSetRowElement = ((Element)
                    tDataDestinationSetChildenList.get(nRowIndex));
            String tDataDestinationSetRowName = tDataDestinationSetRowElement.
                                                getName();
            if (!tDataDestinationSetRowName.trim().toUpperCase().equals("ROW"))
            {
                buildError("checkMatch",
                           "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                           nPartIndex + "个Part元素的DataDestinationSet元素中第" +
                           nRowIndex + "个Row元素名称描述有误");
                return false;
            }
            String tTypeAttributeValue = tDataDestinationSetRowElement.
                                         getAttributeValue("type");
            if (tTypeAttributeValue == null ||
                tTypeAttributeValue.trim().equals(""))
            {
                buildError("checkMatch",
                           "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                           nPartIndex + "个Part元素的DataDestinationSet元素第" +
                           nRowIndex + "个Row元素缺少type属性值的描述");
                return false;
            }

            if (tTypeAttributeValue.trim().equals("1")) //Row值直接到物理数据源Excel表的中间级数据中取得的连续行ROW(startRowIndex,endRowIndex)
            {
                String tFieldAttributeValue = tDataDestinationSetRowElement.
                                              getAttributeValue("src");
                if (tFieldAttributeValue == null ||
                    tFieldAttributeValue.trim().equals(""))
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                               nPartIndex + "个Part元素的DataDestinationSet元素中第" +
                               nRowIndex + "个Row元素缺少src属性值的描述");
                    return false;
                }
                int tStarRowIndex = Integer.parseInt((String) nParamsValueSet.
                        getValueByName("PARAM0"));
                int tEndRowIndex = Integer.parseInt((String) nParamsValueSet.
                        getValueByName("PARAM1"));
                if (!checkMiddlePositionDesc(tFieldAttributeValue))
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                               nPartIndex + "个Part元素的DataDestinationSet元素中第" +
                               nRowIndex + "个Row元素Src属性值描述不符格式要求如：Row(0,12)");
                    return false;
                }
                String[] tSubString = getParseMiddlePosition(
                        tFieldAttributeValue);

                if (!checkMiddlePosition(Integer.parseInt(tSubString[0]),
                                         Integer.parseInt(tSubString[1]),
                                         tEndRowIndex - tStarRowIndex))
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                               nPartIndex + "个Part元素的DataDestinationSet元素中第" +
                               nRowIndex + "个Row元素Src属性值描述的参数取值不符和中间Row结果集合的范围");
                    return false;
                }
            }
            else if (tTypeAttributeValue.trim().equals("2")) //Row值通过计算到物理数据源Excel表的中间级数据中获得
            {
                List tDataDestinationSetRowFieldList =
                        tDataDestinationSetRowElement.getChildren();
                int tDataDestinationSetRowFieldListNum =
                        tDataDestinationSetRowFieldList.size();
                if (tDataDestinationSetRowFieldListNum < 1)
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "个Sheet元素中的第" +
                               nPartIndex +
                               "个Part元素的DataDestinationSet元素中元素Row中的应至少有一个Feild元素");
                    return false;
                }
                TransferData nCurrentRowParamsValueSet = new TransferData();
                for (int nRowFieldIndex = 0;
                                          nRowFieldIndex <
                                          tDataDestinationSetRowFieldListNum;
                                          nRowFieldIndex++)
                {
                    //XML配置模板描述中Row中的Field元素本身校验
                    Element tFieldElement = ((Element)
                                             tDataDestinationSetRowFieldList.
                                             get(nRowFieldIndex));
                    String tFieldName = tFieldElement.getName();
                    if (!tFieldName.trim().toUpperCase().equals("FIELD"))
                    {
                        buildError("checkMatch",
                                   "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                                   nPartIndex +
                                   "个Part元素的DataDestinationSet元素中元素:Row中的第" +
                                   nRowFieldIndex + "个Feild元素名称描述有误");
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
                                   "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                                   nPartIndex +
                                   "个Part元素的DataDestinationSet元素中元素:Row中的第" +
                                   nRowFieldIndex + "个Feild元素缺少name属性值的描述");
                        return false;
                    }

                    if (tRowTypeAttributeValue == null ||
                        tRowTypeAttributeValue.trim().equals(""))
                    {
                        buildError("checkMatch",
                                   "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                                   nPartIndex +
                                   "个Part元素的DataDestinationSet元素中元素:Row中的第" +
                                   nRowFieldIndex + "个Feild元素缺少type属性值的描述");
                        return false;
                    }

                    if (tRowTypeAttributeValue.trim().equals("0")) //属性src值是自定义值
                    {}
                    else if (tRowTypeAttributeValue.trim().equals("1")) //属性src值是直接到物理数据源中取得
                    {
                        String tFieldAttributeValue = tFieldElement.
                                getAttributeValue("src");
                        if (tFieldAttributeValue == null ||
                            tFieldAttributeValue.trim().equals(""))
                        {
                            buildError("checkMatch",
                                       "XML配置模板描述中第" + nSheetIndex +
                                       "Sheet元素中的第" + nPartIndex +
                                       "个Part元素的DataDestinationSet元素中元素中第" +
                                       nRowIndex + "个Row中的第" + nRowFieldIndex +
                                       "个Feild元素缺少src属性值的描述");
                            return false;
                        }
                        if (!checkExcelPositionDesc(tFieldAttributeValue))
                        {
                            buildError("checkMatch",
                                       "XML配置模板描述中第" + nSheetIndex +
                                       "Sheet元素中的第" + nPartIndex +
                                       "个Part元素的DataDestinationSet元素中元素中第" +
                                       nRowIndex + "个Row中的第" + nRowFieldIndex +
                                       "个Feild元素src属性值的描述有误（应如:Sheet(0,1,3)");
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
                                       "XML配置模板描述中第" + nSheetIndex +
                                       "Sheet元素中的第" + nPartIndex +
                                       "个Part元素的DataDestinationSet元素中元素中第" +
                                       nRowIndex + "个Row中的第" + nRowFieldIndex +
                                       "个Feild元素src属性值的描述的物理取数地址有误（目标行的属性定义中，行数参数不能描述为当前行即-1）");
                            return false;

                        }
                        if (!checkExcelPosition(Integer.parseInt(tSubString[0]),
                                                Integer.parseInt(tSubString[1]),
                                                Integer.parseInt(tSubString[2])))
                        {
                            buildError("checkMatch",
                                       "XML配置模板描述中第" + nSheetIndex +
                                       "Sheet元素中的第" + nPartIndex +
                                       "个Part元素的DataDestinationSet元素中元素中第" +
                                       nRowIndex + "个Row中的第" + nRowFieldIndex +
                                       "个Feild元素src属性值的描述的物理取数地址超出Excel表取数范围");
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
                                       "XML配置模板描述中第" + nSheetIndex +
                                       "Sheet元素中的第" + nPartIndex +
                                       "个Part元素的DataDestinationSet元素中元素中第" +
                                       nRowIndex + "个Row中的第" + nRowFieldIndex +
                                       "个Feild元素缺少src属性值的描述");
                            return false;
                        }
                        if (tParamsAttributeValue == null ||
                            tParamsAttributeValue.trim().equals(""))
                        {
                        }
                        else //当type=2 即：sql运算时，如有显示参数定义引用时，进行相应校验
                        {
                            if (!checkTargetParamsAttributeDesc(nParamsValueSet,
                                    nCurrentRowParamsValueSet, nSheetIndex,
                                    nRowFieldIndex, tParamsAttributeValue))
                            {
                                buildError("checkMatch",
                                           "XML配置模板描述中第" + nSheetIndex +
                                           "Sheet元素中的第" + nPartIndex +
                                           "个Part元素的DataDestinationSet元素中元素中第" +
                                           nRowIndex + "个Row中的第" +
                                           nRowFieldIndex +
                                           "个Feild元素params属性值的描述有误");
                                return false;
                            }
                        }
                    }
                    else if (tRowTypeAttributeValue.trim().equals("3")) //属性src值是从参数中直接取得
                    {
                        String tFieldAttributeValue = tFieldElement.
                                getAttributeValue("src");
                        if (tFieldAttributeValue == null ||
                            tFieldAttributeValue.trim().equals(""))
                        {
                            buildError("checkMatch",
                                       "XML配置模板描述中第" + nSheetIndex +
                                       "Sheet元素中的第" + nPartIndex +
                                       "个Part元素的DataDestinationSet元素中元素中第" +
                                       nRowIndex + "个Row中的第" + nRowFieldIndex +
                                       "个Feild元素缺少src属性值的描述");
                            return false;
                        }
                        if (!isParamExist(nParamsValueSet, tFieldAttributeValue))
                        {
                            buildError("checkMatch",
                                       "XML配置模板描述中第" + nSheetIndex +
                                       "Sheet元素中的第" + nPartIndex +
                                       "个Part元素的DataDestinationSet元素中元素中第" +
                                       nRowIndex + "个Row中的第" + nRowFieldIndex +
                                       "个Feild元素src属性引用的直接参数未定义");
                            return false;
                        }

                    }
                    else if (tRowTypeAttributeValue.trim().equals("4")) //属性src值是从该行辅助参数中直接取得
                    {
                        String tFieldAttributeValue = tFieldElement.
                                getAttributeValue("src");
                        if (tFieldAttributeValue == null ||
                            tFieldAttributeValue.trim().equals(""))
                        {
                            buildError("checkMatch",
                                       "XML配置模板描述中第" + nSheetIndex +
                                       "Sheet元素中的第" + nPartIndex +
                                       "个Part元素的DataDestinationSet元素中元素中第" +
                                       nRowIndex + "个Row中的第" + nRowFieldIndex +
                                       "个Feild元素缺少src属性值的描述");
                            return false;
                        }
                        if (!isParamExist(nCurrentRowParamsValueSet,
                                          tFieldAttributeValue))
                        {
                            buildError("checkMatch",
                                       "XML配置模板描述中第" + nSheetIndex +
                                       "Sheet元素中的第" + nPartIndex +
                                       "个Part元素的DataDestinationSet元素中元素中第" +
                                       nRowIndex + "个Row中的第" + nRowFieldIndex +
                                       "个Feild元素src属性引用的辅助行参数未前驱定义");
                            return false;
                        }
                    }
                    else if (tRowTypeAttributeValue.trim().equals("5")) //属性src值是从中间数据集合某行的某一属性值获得
                    {
                        String tFieldAttributeValue = tFieldElement.
                                getAttributeValue("src");
                        if (tFieldAttributeValue == null ||
                            tFieldAttributeValue.trim().equals(""))
                        {
                            buildError("checkMatch",
                                       "XML配置模板描述中第" + nSheetIndex +
                                       "Sheet元素中的第" + nPartIndex +
                                       "个Part元素的DataDestinationSet元素中元素中第" +
                                       nRowIndex + "个Row中的第" + nRowFieldIndex +
                                       "个Feild元素缺少src属性值的描述");
                            return false;
                        }
                        if (!checkTargetPositionDesc(nCurrentRowParamsValueSet,
                                tFieldAttributeValue))
                        {
                            buildError("checkMatch",
                                       "XML配置模板描述中第" + nSheetIndex +
                                       "Sheet元素中的第" + nPartIndex +
                                       "个Part元素的DataDestinationSet元素中元素中第" +
                                       nRowIndex + "个Row中的第" + nRowFieldIndex +
                                       "个Feild元素src属性值的描述有误（应如:Row(2).属性列名）");
                            return false;
                        }
                        if (!checkTargetPosition(tFieldAttributeValue,
                                                 nParamsValueSet,
                                                 nCurrentRowParamsValueSet))
                        {
                            buildError("checkMatch",
                                       "XML配置模板描述中第" + nSheetIndex +
                                       "Sheet元素中的第" + nPartIndex +
                                       "个Part元素的DataDestinationSet元素中元素中第" +
                                       nRowIndex + "个Row中的第" + nRowFieldIndex +
                                       "个Feild元素src属性值的描述的中间表取数地址超取数范围");
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
                           "XML配置模板描述中第" + nSheetIndex + "Sheet元素中的第" +
                           nPartIndex + "个Part元素的DataDestinationSet元素中第" +
                           nRowIndex + "个Row元素Type属性值的描述有误");
                return false;
            }
        }

        return true;
    }

    /**
     * 校验格式匹配
     * @return boolean
     * @throws Exception
     */
    private boolean checkMatch() throws Exception
    {
        //获取Excel物理数据源中Sheet的个数
        int tExcelSheetNum = mBookModelImplImport.getNumSheets();
        try
        {
            //校验根元素DataConfigDesc
            String tRootName = mXMLImplConfig.getName();
            if (tRootName == null ||
                !tRootName.trim().toUpperCase().equals("DATACONFIGDESC"))
            {
                buildError("checkMatch", "XML配置模板描述中缺少根元素DataConfigDesc的描述");
                return false;
            }

            //校验XML配置模板描述中的Sheet元素
            List tSheetList = mXMLImplConfig.getChildren();
            int tSheetListNum = tSheetList.size();
            if (tSheetListNum < 1)
            {
                buildError("checkMatch", "XML配置模板描述中缺少Sheet元素的描述");
                return false;
            }
            for (int nSheetIndex = 0; nSheetIndex < tSheetListNum; nSheetIndex++)
            {
                //XML配置模板描述中Sheet元素本身校验
                Element tSheetElement = ((Element) tSheetList.get(nSheetIndex));
                String tSheetName = tSheetElement.getName();
                if (!tSheetName.trim().toUpperCase().equals("SHEET"))
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex + "Sheet元素名称描述有误");
                    return false;
                }
                String tDestIndex = tSheetElement.getAttributeValue("dest");
                if (tDestIndex == null || tDestIndex.trim().equals("")) //没有描述则认为和sheet顺序号相同
                {
                    tDestIndex = String.valueOf(nSheetIndex);
                }
                // 判断配置描述中的请求的Sheet页号是否小于等于物理EXCEL表中提供的Sheet页数
                int nDestIndex = Integer.parseInt(tDestIndex);
                if (nDestIndex >= tExcelSheetNum)
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex +
                               "Sheet元素的Dest属性请求的Sheet页号大于物理EXCEL表中提供的Sheet页数");
                    return false;
                }

                //XML配置模板描述中该Sheet元素中内部其他元素校验
                List tPartList = tSheetElement.getChildren();
                //XML配置模板描述中该Sheet元素中内部Part元素本身的校验
                int tPartListNum = tPartList.size();
                if (tPartListNum < 1)
                {
                    buildError("checkMatch",
                               "XML配置模板描述中第" + nSheetIndex +
                               "Sheet中缺少Part元素的描述");
                    return false;
                }
                //XML配置模板描述中该Sheet元素中内部Part元素校验
                for (int nPartIndex = 0; nPartIndex < tPartListNum; nPartIndex++)
                {
                    int nMaxRow = 0;
                    //XML配置模板描述中Sheet元素内部Part元素中的参数存储对象
                    TransferData nParamsValueSet = new TransferData();
                    //XML配置模板描述中Sheet元素内部Part元素本身校验
                    Element tPartElement = ((Element) tPartList.get(nPartIndex));
                    String tPartName = tPartElement.getName();
                    if (tPartName.trim().toUpperCase().equals("PART"))
                    {
                        //XML配置模板描述中该Sheet元素中内部该Part元素中内部元素校验
                        List tPartChildenList = tPartElement.getChildren();
                        //XML配置模板描述中该Sheet元素中内部该Part元素中的DataSourceSet元素本身的校验
                        int tPartChildenNum = tPartChildenList.size();
                        if (tPartChildenNum != 2)
                        {
                            buildError("checkMatch",
                                       "XML配置模板描述中第" + nSheetIndex +
                                       "个Sheet元素中的第" + nPartIndex +
                                       "个Part元素的缺少必备的DataSourceSet和DataDestinationSet元素描述");
                            return false;
                        }

                        //XML配置模板描述中该Sheet元素中内部该Part元素中的第一孩子元素：DataSourceSet的校验
                        Element tDataSourceSetElement = ((Element)
                                tPartChildenList.get(0));

                        if (!checkDataSourceSetMatch(nSheetIndex, nPartIndex,
                                nDestIndex, tDataSourceSetElement,
                                nParamsValueSet))
                        {
                            return false;
                        }

                        //获取中间数据行数
                        //XML配置模板描述中该Sheet元素中内部该Part元素中第二个孩子DataDestinationSet元素的校验
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
                                   "XML配置模板描述中第" + nSheetIndex + "Sheet元素中第" +
                                   nPartIndex + "Part元素描述名称有误");
                        return false;
                    }
                }
                System.out.println("XML配置模板检第" + nSheetIndex + "Sheet检验成功结束");
            }
            System.out.println("XML配置模板检验成功结束");
        }
        catch (Exception e)
        {
            System.err.println("XML配置数据读入失败:" + e.getMessage());
            return false;
        }
        return true;

        // 将第一行中元素的值设为对应的XML元素的名字
        // 如在Sheet0中，每行的第一列对应的XML元素名字为ID。
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
//          throw new Exception("找不到对应的配置信息，Sheet" + String.valueOf(nIndex+1)
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
//		  if(nNullCount==10)//当连续有十行第一行都为0则认为已到有效最大列
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
//		if(nNullCount==10)//当连续有十行第一列都为0则认为已到有效最大行
//		  break;
//	  }
//	  else
//	   nNullCount=0;
//	}
        return nNullCount + 1;
    }

}

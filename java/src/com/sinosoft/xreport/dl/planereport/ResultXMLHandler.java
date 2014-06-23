package com.sinosoft.xreport.dl.planereport;

import java.io.File;

import com.sinosoft.xreport.util.XMLPathTool;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ResultXMLHandler
{

    private String sFileName;
    private XMLPathTool aTool;

    public ResultXMLHandler()
    {}

    public ResultXMLHandler(String aFile)
    {
        setFileName(aFile);
    }

    /**
     * 设置xml文件名
     * @param aFile xml文件名
     */
    private void setFileName(String aFile)
    {
        sFileName = aFile;
        File oFile = new File(sFileName);
        aTool = new XMLPathTool(oFile);
    }

    /**
     * 从xml文件中取得报表使用单位编码
     * @return 报表使用单位编码
     */
    public String getBranch()
    {
        XObject xobj = aTool.parseX("/report_define/info/branch");
        String ret = xobj.toString();
        return ret;
    }

    /**
     * 从xml文件中取得报表编码
     * @return 报表编码
     */
    public String getCode()
    {
        XObject xobj = aTool.parseX("/report_define/info/code");
        String ret = xobj.toString();
        return ret;
    }

    /**
     * 从xml文件中取得报表名称
     * @return 报表名称
     */
    public String getName()
    {
        XObject xobj = aTool.parseX("/report_define/info/name");
        String ret = xobj.toString();
        return ret;
    }

    /**
     * 从xml文件中取得报表版别
     * @return 报表版别
     */
    public String getEdition()
    {
        XObject xobj = aTool.parseX("/report_define/info/edition");
        String ret = xobj.toString();
        return ret;
    }

    /**
     * 从xml文件中取得报表使用者编码
     * @return 报表使用者编码
     */
    public String getOperator()
    {
        XObject xobj = aTool.parseX("/report_define/info/operator");
        String ret = xobj.toString();
        return ret;
    }

    /**
     * 从xml文件中取得报表数据类型
     * @return 报表数据类型
     */
    public String getDataType()
    {
        String ret;
        Node node;
        try
        {
            ret = XMLPathTool.getAttrValue(null, "type", "/report_define/data");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 从xml文件中取得报表的格式文件
     * @return 报表的格式文件
     */
    public String getFormatFile()
    {
        XObject xobj = aTool.parseX("/report_define/format/file");
        String ret = xobj.toString();
        return ret;
    }

    /**
     * 从xml文件中取得报表全局查询条件类型
     * @param condition 全局查询条件节点
     * @return 报表全局查询条件类型
     */
    public String getGCType(Node condition)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(condition, "type");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 从xml文件中取得报表全局查询条件值
     * @param condition 全局查询条件节点
     * @return 报表全局查询条件值
     */
    public String getGCValue(Node condition)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(condition, "value");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 取得全局查询条件个数
     * @return 全局查询条件个数
     */
    public int getGCCount()
    {
        NodeList nl = aTool.parseN("/report_define/data/global/condition");
        return nl.getLength();
    }

    /**
     * 取得全局查询条件
     * @return 全局查询条件
     */
    public GlobalConditions getGC()
    {
        GlobalConditions aGCSet = new GlobalConditions();
        NodeList nl = aTool.parseN("/report_define/data/global/condition");
        Node condition;
        int iGCCount = getGCCount();
        GCondition aGC[] = new GCondition[iGCCount];
        aGCSet.setConditionCount(iGCCount);
        for (int i = 0; i < iGCCount; i++)
        {
            aGC[i] = new GCondition();
            condition = nl.item(i);
            aGC[i].setType(this.getGCType(condition));
            aGC[i].setValue(this.getGCValue(condition));
        }
        aGCSet.setConditions(aGC);
        return aGCSet;
    }

    /**
     * 取得显示列头的取数条件
     * @param column 显示列头节点
     * @return 显示列头的取数条件
     */
    public String getColCondition(Node column)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(column, "condition");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 取得显示列头的计算公式
     * @param column 显示列头节点
     * @return 显示列头的计算公式
     */
    public String getColFormula(Node column)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(column, "formula");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 取得显示列头的位置
     * @param column 显示列头节点
     * @return 显示列头的位置
     */
    public String getColLocation(Node column)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(column, "location");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 取得显示列头的位置
     * @param column 显示列头节点
     * @return 显示列头的位置
     */
    public String getColName(Node column)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(column, "name");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 取得显示列头的文本字符
     * @param column 显示列头节点
     * @return 显示列头的文本字符
     */
    public String getColText(Node column)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getValue(column);
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 取得全局查询条件个数
     * @return 全局查询条件个数
     */
    public int getColCount()
    {
        NodeList nl = aTool.parseN("/report_define/data/cols/col");
        return nl.getLength();
    }

    /**
     * 取得全局查询条件
     * @return 全局查询条件
     */
    public Cols getCols()
    {
        Cols aColSet = new Cols();
        NodeList nl = aTool.parseN("/report_define/data/cols/col");
        Node column;
        int iColCount = getColCount();
        Col aCol[] = new Col[iColCount];
        aColSet.setColCount(iColCount);
        for (int i = 0; i < iColCount; i++)
        {
            aCol[i] = new Col();
            column = nl.item(i);
            aCol[i].setCondition(this.getColCondition(column));
            aCol[i].setFormula(this.getColFormula(column));
            aCol[i].setLocation(this.getColLocation(column));
            aCol[i].setName(this.getColName(column));
            aCol[i].setText(this.getColText(column));
        }
        aColSet.setCol(aCol);
        return aColSet;
    }

    /**
     * 取得单元格的文本字符
     * @param cell 单元格
     * @return 单元格的文本字符
     */
    public String getCellText(Node cell)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getValue(cell);
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 取得单元格的位置
     * @param cell 单元格节点
     * @return 单元格的位置
     */
    public String getCellLocation(Node cell)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(cell, "location");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 取得单元格个数
     * @return 单元格个数
     */
    public int getCellCount()
    {
        NodeList nl = aTool.parseN("/report_define/data/cells/cell");
        return nl.getLength();
    }

    /**
     * 取得单元格
     * @return 单元格
     */
    public Cells getCells()
    {
        Cells aCellSet = new Cells();
        NodeList nl = aTool.parseN("/report_define/data/cells/cell");
        Node cell;
        int iCellCount = getCellCount();
        Cell aCell[] = new Cell[iCellCount];
        aCellSet.setCellCount(iCellCount);
        for (int i = 0; i < iCellCount; i++)
        {
            aCell[i] = new Cell();
            cell = nl.item(i);
            aCell[i].setLocation(this.getCellLocation(cell));
            aCell[i].setText(this.getCellText(cell));
        }
        aCellSet.setCell(aCell);
        return aCellSet;
    }

    /**
     * 取得结果值
     * @param data 结果节点
     * @return 结果值
     */
    public String getDataResultValue(Node data)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(data, "value");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 取得结果名称
     * @param data 结果节点
     * @return 结果名称
     */
    public String getDataResultName(Node data)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(data, "name");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 取得结果的行号
     * @param data 结果节点
     * @return 结果的行号
     */
    public int getDataResultRow(Node data)
    {
        String sRet;
        int iRet = 0;
        try
        {
            sRet = XMLPathTool.getAttrValue(data, "row");
            iRet = Integer.parseInt(sRet);
        }
        catch (Exception e)
        {
            iRet = -1;
        }
        return iRet;
    }

    /**
     * 取得结果的列号
     * @param data 结果节点
     * @return 结果的列号
     */
    public int getDataResultCol(Node data)
    {
        String sRet;
        int iRet = 0;
        try
        {
            sRet = XMLPathTool.getAttrValue(data, "col");
            iRet = Integer.parseInt(sRet);
        }
        catch (Exception e)
        {
            iRet = -1;
        }
        return iRet;
    }

    /**
     * 取得结果个数
     * @return 结果个数
     */
    public int getDataResultCount()
    {
        NodeList nl = aTool.parseN("/report_define/data/dataResults/data");
        return nl.getLength();
    }

    /**
     * 取得结果
     * @return 结果
     */
    public DataResults getDataResults()
    {
        DataResults aDataSet = new DataResults();
        NodeList nl = aTool.parseN("/report_define/data/dataResults/data");
        Node data;
        int iDataCount = getDataResultCount();
        Data aData[] = new Data[iDataCount];
        aDataSet.setResultCount(iDataCount);
        for (int i = 0; i < iDataCount; i++)
        {
            aData[i] = new Data();
            data = nl.item(i);
            aData[i].setCol(this.getDataResultCol(data));
            aData[i].setRow(this.getDataResultRow(data));
            aData[i].setValue(this.getDataResultValue(data));
        }
        aDataSet.setResultData(aData);
        return aDataSet;
    }

    /**
     * 取得清单式报表定义
     * @return 清单式报表定义
     */
    public ResultXML getResultXML()
    {
        ResultXML aResultXML = new ResultXML();
        aResultXML.setCols(this.getCols());
        aResultXML.setCells(this.getCells());
        aResultXML.setGCondition(this.getGC());
        aResultXML.setRptBranch(this.getBranch());
        aResultXML.setRptCode(this.getCode());
        aResultXML.setRptName(this.getName());
        aResultXML.setRptEdition(this.getEdition());
        aResultXML.setRptOperator(this.getOperator());
        aResultXML.setRptFile(this.getFormatFile());
        aResultXML.setRptType(this.getDataType());
        aResultXML.setDataResults(this.getDataResults());
        return aResultXML;
    }

    public boolean toFile()
    {
        Node root;
        try
        {
            root = aTool.getRootNode();
            XMLPathTool.toFile(root, sFileName);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 添加计算结果数据节点
     * @param row 行号
     * @param col 列号
     * @param value 计算结果值
     * @param name 计算结果名称
     * @return 真：成功；假：失败
     */
    public boolean appendData(int row, int col, String value, String name)
    {
        try
        {
            Node dataSet = aTool.getNode("/report_define/data/dataResults");
            Node aNewData = XMLPathTool.appendNode(dataSet, "data");
            aNewData = XMLPathTool.setAttrValue(aNewData, "row", String.valueOf(row));
            aNewData = XMLPathTool.setAttrValue(aNewData, "col", String.valueOf(col));
            aNewData = XMLPathTool.setAttrValue(aNewData, "value", value);
            aNewData = XMLPathTool.setAttrValue(aNewData, "name", name);
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 添加计算结果单元格节点
     * @param location 单元格位置
     * @param text 单元格内容
     * @return 真：成功；假：失败
     */
    public boolean appendCell(String location, String text)
    {
        try
        {
            Node dataSet = aTool.getNode("/report_define/data/cells");
            Node aNewData = XMLPathTool.appendNode(dataSet, "cell");
            aNewData = XMLPathTool.setAttrValue(aNewData, "location", location);
            aNewData = XMLPathTool.setValue(aNewData, text);
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 设置计算结果节点属性值
     * @param aData 计算结果节点
     * @param value 属性值
     * @param name 属性名称
     * @return 计算结果节点
     */
    public Node setDataNode(Node aData, String value, String name)
    {
        try
        {
            aData = XMLPathTool.setAttrValue(aData, name, value);
            return aData;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return aData;
        }
    }

    /**
     * 取得计算结果节点属性值
     * @param aData 计算结果节点
     * @param name 属性名称
     * @return 计算结果节点属性值
     */
    public String getDataAttr(Node aData, String name)
    {
        try
        {
            return XMLPathTool.getAttrValue(aData, name);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * 查找符合条件的计算结果节点
     * @param name 计算结果名称取值
     * @param value 计算结果值取值
     * @return 符合条件的计算结果节点链
     */
    public NodeList queryData(String name, String value)
    {
        String xpath = "/report_define/data/dataResults/data[@" + name + "='" +
                       value + "']";
        NodeList nl = aTool.parseN(xpath);
        return nl;
    }

    /**
     * 查找符合计算条件的计算结果节点
     * @param formula 计算条件
     * @return 符合条件的计算结果节点链
     */
    public NodeList calculateData(String formula)
    {
        String xpath = "/report_define/data/dataResults/data[" + formula + "]";
        NodeList nl = aTool.parseN(xpath);
        return nl;
    }

    public static void main(String args[])
    {
        ResultXMLHandler aTest = new ResultXMLHandler(
                "E:\\jbuilder5\\xreport\\aa_ddd_result.xml");
        ResultXML aResultXML = aTest.getResultXML();
        System.out.println(aResultXML.getRptBranch());
        System.out.println(aResultXML.getRptCode());
        System.out.println(aResultXML.getRptName());
        System.out.println(aResultXML.getRptEdition());
        System.out.println(aResultXML.getRptOperator());
        System.out.println(aResultXML.getRptType());
        System.out.println(aResultXML.getRptFile());

        System.out.println("Display Column Define:");
        Cols aColSet = aResultXML.getCols();
        Col aCol[] = aColSet.getCol();
        for (int i = 0; i < aColSet.getColCount(); i++)
        {
            System.out.print(aCol[i].getText() + "\t");
        }
        System.out.print("\n");
        DataResults aDataSet = aResultXML.getDataResults();
        Data aData[] = aDataSet.getResultData();
        int row, rowtmp = -1;
        for (int i = 0; i < aDataSet.getResultCount(); i++)
        {
            row = aData[i].getRow();
            if (rowtmp != row)
            {
                System.out.print("\n");
                rowtmp = row;
            }
            System.out.print(aData[i].getValue() + "\t");
        }
    }
}

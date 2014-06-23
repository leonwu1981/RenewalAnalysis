package com.sinosoft.xreport.dl.planereport;

import java.io.File;

import com.sinosoft.xreport.util.XMLPathTool;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DefineXMLHandler
{
    private String sFileName;
    private XMLPathTool aTool;

    public DefineXMLHandler()
    {}

    public DefineXMLHandler(String aFile)
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
            node = aTool.getRootNode();
            ret = XMLPathTool.getAttrValue(node, "type", "/report_define/data");
        }
        catch (Exception e)
        {
            ret = "";
        }
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
     * 取得数据列的位置
     * @param viewCol 数据列节点
     * @return 数据列的位置
     */
    public String getViewColLocation(Node viewCol)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(viewCol, "location");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 取得数据列的计算公式
     * @param viewCol 数据列节点
     * @return 数据列的计算公式
     */
    public String getViewColFormula(Node viewCol)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(viewCol, "formula");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 取得数据列的来源
     * @param viewCol 数据列节点
     * @return 数据列的来源
     */
    public String getViewColSrc(Node viewCol)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(viewCol, "src");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 取得数据列的显示属性
     * @param viewCol 数据列节点
     * @return 数据列的显示属性
     */
    public boolean getViewColView(Node viewCol)
    {
        String sRet;
        boolean bRet;
        try
        {
            sRet = XMLPathTool.getAttrValue(viewCol, "view");
            if (sRet.equals("1"))
            {
                bRet = true;
            }
            else
            {
                bRet = false;
            }
        }
        catch (Exception e)
        {
            bRet = true;
        }
        return bRet;
    }

    /**
     * 取得数据列的数据库列名
     * @param viewCol 数据列节点
     * @return 数据列的数据库列名
     */
    public String getViewColDbName(Node viewCol)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(viewCol, "dbname");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 取得数据列的名称
     * @param viewCol 数据列节点
     * @return 数据列的名称
     */
    public String getViewColId(Node viewCol)
    {
        try
        {
            String sRet = XMLPathTool.getAttrValue(viewCol, "col");
            return sRet;
        }
        catch (Exception ex)
        {
            return "";
        }
    }

    /**
     * 取得数据列个数
     * @return 数据列个数
     */
    public int getViewColCount()
    {
        NodeList nl = aTool.parseN(
                "/report_define/data/dataSrc/columnViews/viewCol");
        return nl.getLength();
    }

    /**
     * 取得数据列
     * @return 数据列
     */
    public ColumnViews getViewCols()
    {
        ColumnViews aViewColSet = new ColumnViews();
        NodeList nl = aTool.parseN(
                "/report_define/data/dataSrc/columnViews/viewCol");
        Node viewCol;
        int iCount = getViewColCount();
        ViewCol aViewCol[] = new ViewCol[iCount];
        aViewColSet.setViewColCount(iCount);
        for (int i = 0; i < iCount; i++)
        {
            aViewCol[i] = new ViewCol();
            viewCol = nl.item(i);
            aViewCol[i].setLocation(this.getViewColLocation(viewCol));
            aViewCol[i].setFormula(this.getViewColFormula(viewCol));
            aViewCol[i].setSource(this.getViewColSrc(viewCol));
            aViewCol[i].setView(this.getViewColView(viewCol));
            aViewCol[i].setDbName(this.getViewColDbName(viewCol));
            aViewCol[i].setCol(this.getViewColId(viewCol));
        }
        aViewColSet.setViewCol(aViewCol);
        return aViewColSet;
    }

    /**
     * 取得小计、合计列的名称
     * @param sumCol 小计、合计列节点
     * @return 小计、合计列的名称
     */
    public String getSumColId(Node sumCol)
    {
        try
        {
            String sRet = XMLPathTool.getAttrValue(sumCol, "col");
            return sRet;
        }
        catch (Exception ex)
        {
            return "";
        }
    }

    /**
     * 取得小计、合计列的数据库列名
     * @param sumCol 小计、合计列节点
     * @return 小计、合计列的数据库列名
     */
    public String getSumColDbName(Node sumCol)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(sumCol, "dbname");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 取得小计、合计列个数
     * @return 小计、合计列个数
     */
    public int getSumColCount()
    {
        NodeList nl = aTool.parseN(
                "/report_define/data/dataSrc/sumColumns/sumCol");
        return nl.getLength();
    }

    /**
     * 取得小计、合计列
     * @return 小计、合计列
     */
    public SumColumns getSumCols()
    {
        SumColumns aSumColSet = new SumColumns();
        NodeList nl = aTool.parseN(
                "/report_define/data/dataSrc/sumColumns/sumCol");
        Node sumCol;
        int iCount = getSumColCount();
        SumCol aSumCol[] = new SumCol[iCount];
        aSumColSet.setSumColCount(iCount);
        for (int i = 0; i < iCount; i++)
        {
            aSumCol[i] = new SumCol();
            sumCol = nl.item(i);
            aSumCol[i].setCol(this.getSumColId(sumCol));
            aSumCol[i].setDbName(this.getSumColDbName(sumCol));
        }
        aSumColSet.setSumCol(aSumCol);
        return aSumColSet;
    }

    /**
     * 取得小计、合计依据列的名称
     * @param sumDependCol 小计、合计依据列节点
     * @return 小计、合计依据列的名称
     */
    public String getSumDependColId(Node sumDependCol)
    {
        try
        {
            String sRet = XMLPathTool.getAttrValue(sumDependCol, "col");
            return sRet;
        }
        catch (Exception ex)
        {
            return "";
        }
    }

    /**
     * 取得小计、合计依据列的数据库列名
     * @param sumDependCol 小计、合计依据列节点
     * @return 小计、合计依据列的数据库列名
     */
    public String getSumDependColDbName(Node sumDependCol)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(sumDependCol, "dbname");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 取得小计、合计依据列个数
     * @return 小计、合计依据列个数
     */
    public int getSumDependColCount()
    {
        NodeList nl = aTool.parseN(
                "/report_define/data/dataSrc/sumDepend/dependCol");
        return nl.getLength();
    }

    /**
     * 取得小计、合计依据列
     * @return 小计、合计依据列
     */
    public SumDepend getSumDependCols()
    {
        SumDepend aSumDependColSet = new SumDepend();
        NodeList nl = aTool.parseN(
                "/report_define/data/dataSrc/sumDepend/dependCol");
        Node sumDependCol;
        int iCount = getSumDependColCount();
        DependCol aSumDependCol[] = new DependCol[iCount];
        aSumDependColSet.setSumDependColCount(iCount);
        for (int i = 0; i < iCount; i++)
        {
            aSumDependCol[i] = new DependCol();
            sumDependCol = nl.item(i);
            aSumDependCol[i].setCol(this.getSumDependColId(sumDependCol));
            aSumDependCol[i].setDbName(this.getSumDependColDbName(sumDependCol));
        }
        aSumDependColSet.setSumDependCol(aSumDependCol);
        return aSumDependColSet;
    }

    /**
     * 取得起始单元格的列号
     * @return 起始单元格的列号
     */
    public int getStartCellCol()
    {
        String sRet;
        int iRet = 0;
        try
        {
            Node root = aTool.getRootNode();
            sRet = XMLPathTool.getAttrValue(root, "col",
                                      "/report_define/data/dataSrc/startCell");
            iRet = Integer.parseInt(sRet);
        }
        catch (Exception e)
        {
            iRet = -1;
        }
        return iRet;
    }

    /**
     * 取得起始单元格的列号
     * @return 起始单元格的列号
     */
    public int getStartCellRow()
    {
        String sRet;
        int iRet = 0;
        try
        {
            Node root = aTool.getRootNode();
            sRet = XMLPathTool.getAttrValue(root, "row",
                                      "/report_define/data/dataSrc/startCell");
            iRet = Integer.parseInt(sRet);
        }
        catch (Exception e)
        {
            iRet = -1;
        }
        return iRet;
    }

    /**
     * 取得起始单元格的信息
     * @return 起始单元格的信息
     */
    public StartCell getStartCell()
    {
        StartCell ret = new StartCell();
        ret.setCol(this.getStartCellCol());
        ret.setRow(this.getStartCellRow());
        return ret;
    }

    /**
     * 从xml文件中取得报表的主题表
     * @return 报表主题表
     */
    public String getSubject()
    {
        XObject xobj = aTool.parseX("/report_define/data/dataSrc/subject");
        String ret = xobj.toString();
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
     * 从xml文件中取得报表主题表查询条件
     * @return 报表主题表查询条件
     */
    public String getStrWhere()
    {
        XObject xobj = aTool.parseX("/report_define/data/dataSrc/strWhere");
        String ret = xobj.toString();
        return ret;
    }

    /**
     * 从xml文件中取得附加数据源
     * @return 附加数据源
     */
    public int getAppendCount()
    {
        NodeList nlAppend = aTool.parseN("/report_define/data/dataSrc/append");
        return nlAppend.getLength();
    }

    /**
     * 从xml文件中取得附加数据源
     * @return 附加数据源
     */
    public Append[] getAppend()
    {
        NodeList nlAppend = aTool.parseN("/report_define/data/dataSrc/append");
        Append appendSet[] = new Append[nlAppend.getLength()];
        for (int j = 0; j < nlAppend.getLength(); j++)
        {
            appendSet[j] = new Append();
            Node nodeAppend = nlAppend.item(j);
            NodeList nl = aTool.parseN(nodeAppend, "appendCol");
            Node appendCol;
            int iCount = getAppendColCount(nodeAppend);
            AppendCol aAppendCol[] = new AppendCol[iCount];
            appendSet[j].setAppendColCount(iCount);
            for (int i = 0; i < iCount; i++)
            {
                aAppendCol[i] = new AppendCol();
                appendCol = nl.item(i);
                aAppendCol[i].setColName(this.getAppendColId(appendCol));
                aAppendCol[i].setDbName(this.getAppendColDbName(appendCol));
            }
            appendSet[j].setSource(this.getAppendSource(nodeAppend));
            appendSet[j].setCol(this.getAppendCol(nodeAppend));
            appendSet[j].setAppendCol(aAppendCol);
        }
        return appendSet;
    }

    /**
     * 从xml文件中取得附加数据源的查询SQL
     * @param append 附加数据源节点
     * @return 附加数据源的查询SQL
     */
    public String getAppendSource(Node append)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(append, "src");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 从xml文件中取得附加数据源的显示起始列
     * @param append 附加数据源节点
     * @return 附加数据源的显示起始列
     */
    public int getAppendCol(Node append)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(append, "col");
            return Integer.parseInt(ret);
        }
        catch (Exception e)
        {
            return -1;
        }
    }

    /**
     * 取得附加数据源依赖列的名称
     * @param appendCol 附加数据源依赖列节点
     * @return 附加数据源依赖列的名称
     */
    public String getAppendColId(Node appendCol)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(appendCol, "name");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 取得附加数据源依赖列对应的数据库名
     * @param appendCol 附加数据源依赖列节点
     * @return 附加数据源依赖列对应的数据库名
     */
    public String getAppendColDbName(Node appendCol)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(appendCol, "dbName");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 取得附加数据源依赖列个数
     * @param append 附加数据源节点
     * @return 附加数据源依赖列个数
     */
    public int getAppendColCount(Node append)
    {
        NodeList nl = aTool.parseN(append, "appendCol");
        return nl.getLength();
    }

    /**
     * 取得代码替换列的名称
     * @param replaceCol 代码替换列节点
     * @return 代码替换列的名称
     */
    public String getReplaceColId(Node replaceCol)
    {
        try
        {
            return XMLPathTool.getAttrValue(replaceCol, "col");
        }
        catch (Exception e)
        {
            return "";
        }
    }

    /**
     * 取得代码替换列的要替换的列名
     * @param replaceCol 代码替换列节点
     * @return 代码替换列的要替换的列名
     */
    public String getReplaceColData(Node replaceCol)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(replaceCol, "dataCol");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 取得代码替换列个数
     * @return 代码替换列个数
     */
    public int getReplaceColCount()
    {
        NodeList nl = aTool.parseN(
                "/report_define/data/dataSrc/columnReplace/replaceCol");
        return nl.getLength();
    }

    /**
     * 取得代码替换列的替换源查询SQL
     * @param replaceCol 代码替换列节点
     * @return 代码替换列的替换源查询SQL
     */
    public String getReplaceColSrc(Node replaceCol)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(replaceCol, "src");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 取得代码替换列的替换列名
     * @param replaceCol 代码替换列节点
     * @return 代码替换列的替换列名
     */
    public String getReplaceColDisplay(Node replaceCol)
    {
        String ret;
        try
        {
            ret = XMLPathTool.getAttrValue(replaceCol, "dispCol");
        }
        catch (Exception e)
        {
            ret = "";
        }
        return ret;
    }

    /**
     * 取得代码替换列
     * @return 代码替换列
     */
    public ColumnReplace getReplaceCols()
    {
        ColumnReplace aReplaceColSet = new ColumnReplace();
        NodeList nl = aTool.parseN(
                "/report_define/data/dataSrc/columnReplace/replaceCol");
        Node replaceCol;
        int iCount = getReplaceColCount();
        ReplaceCol aReplaceCol[] = new ReplaceCol[iCount];
        aReplaceColSet.setReplaceColCount(iCount);
        for (int i = 0; i < iCount; i++)
        {
            aReplaceCol[i] = new ReplaceCol();
            replaceCol = nl.item(i);
            aReplaceCol[i].setColumn(this.getReplaceColId(replaceCol));
            aReplaceCol[i].setDataColumn(this.getReplaceColData(replaceCol));
            aReplaceCol[i].setDisplayColumn(this.getReplaceColDisplay(
                    replaceCol));
            aReplaceCol[i].setSource(this.getReplaceColSrc(replaceCol));
        }
        aReplaceColSet.setReplaceCol(aReplaceCol);
        return aReplaceColSet;
    }

    /**
     * 取得数据源定义
     * @return 数据源定义
     */
    public DataSrc getDataSource()
    {
        DataSrc aDataSrc = new DataSrc();
        aDataSrc.setColReplaces(this.getReplaceCols());
        aDataSrc.setColSumDepends(this.getSumDependCols());
        aDataSrc.setColSums(this.getSumCols());
        aDataSrc.setColViews(this.getViewCols());
        aDataSrc.setStartCell(this.getStartCell());
        aDataSrc.setSubject(this.getSubject());
        aDataSrc.setStrWhere(this.getStrWhere());
        aDataSrc.setAppendCount(this.getAppendCount());
        aDataSrc.setAppend(this.getAppend());
        return aDataSrc;
    }

    /**
     * 取得清单式报表定义
     * @return 清单式报表定义
     */
    public DefineXML getDefineXML()
    {
        DefineXML aDefineXML = new DefineXML();
        aDefineXML.setCols(this.getCols());
        aDefineXML.setCells(this.getCells());
        aDefineXML.setDataSrc(this.getDataSource());
        aDefineXML.setGCondition(this.getGC());
        aDefineXML.setRptBranch(this.getBranch());
        aDefineXML.setRptCode(this.getCode());
        aDefineXML.setRptName(this.getName());
        aDefineXML.setRptEdition(this.getEdition());
        aDefineXML.setRptOperator(this.getOperator());
        aDefineXML.setRptFile(this.getFormatFile());
        aDefineXML.setRptType(this.getDataType());
        return aDefineXML;
    }

    public boolean toFile()
    {
        Document doc;
        try
        {
            doc = aTool.getDocument();
            XMLPathTool.toFile(doc, sFileName);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String args[])
    {
        DefineXMLHandler aTest = new DefineXMLHandler(
                "E:\\jbuilder5\\xreport\\aa_ddd_define.xml");
        DefineXML aDefineXML = aTest.getDefineXML();
        System.out.println(aDefineXML.getRptBranch());
        System.out.println(aDefineXML.getRptCode());
        System.out.println(aDefineXML.getRptName());
        System.out.println(aDefineXML.getRptEdition());
        System.out.println(aDefineXML.getRptOperator());
        System.out.println(aDefineXML.getRptType());
        System.out.println(aDefineXML.getRptFile());

        System.out.println("Global Condition Define:");
        GlobalConditions aGCSet = aDefineXML.getGCondition();
        GCondition aGC[] = aGCSet.getConditions();
        for (int i = 0; i < aGCSet.getConditionCount(); i++)
        {
            System.out.print(aGC[i].getType());
            System.out.print("=");
            System.out.println(aGC[i].getValue());
        }
        System.out.println("Display Column Define:");
        Cols aColSet = aDefineXML.getCols();
        Col aCol[] = aColSet.getCol();
        for (int i = 0; i < aColSet.getColCount(); i++)
        {
            System.out.print(aCol[i].getCondition());
            System.out.print(aCol[i].getFormula());
            System.out.print(aCol[i].getLocation());
            System.out.println(aCol[i].getText());
        }
        System.out.println("Cell Source Define:");
        Cells aCellSet = aDefineXML.getCells();
        Cell aCell[] = aCellSet.getCell();
        for (int i = 0; i < aCellSet.getCellCount(); i++)
        {
            System.out.print(aCell[i].getLocation());
            System.out.println(aCell[i].getText());
        }
        System.out.println("Data Source Define:");
        DataSrc aDataSrc = aDefineXML.getDataSrc();
        System.out.print(aDataSrc.getSubject());
        System.out.print(aDataSrc.getStrWhere());
        System.out.println("Data Columns:");
        ColumnViews aColViews = aDataSrc.getColViews();
        ViewCol aViewCol[] = aColViews.getViewCol();
        for (int i = 0; i < aColViews.getViewColCount(); i++)
        {
            System.out.print(aViewCol[i].getCol());
            System.out.print(aViewCol[i].getDbName());
            System.out.print(aViewCol[i].getFormula());
            System.out.print(aViewCol[i].getLocation());
            System.out.print(aViewCol[i].getSource());
            System.out.println(aViewCol[i].isView());
        }
        System.out.println("Replace Columns:");
        ColumnReplace aColReplace = aDataSrc.getColReplaces();
        ReplaceCol aReplaceCol[] = aColReplace.getReplaceCol();
        for (int i = 0; i < aColReplace.getReplaceColCount(); i++)
        {
            System.out.print(aReplaceCol[i].getColumn());
            System.out.print(aReplaceCol[i].getDataColumn());
            System.out.print(aReplaceCol[i].getDisplayColumn());
            System.out.println(aReplaceCol[i].getSource());
        }
        System.out.println("Sum Columns:");
        SumColumns aColSums = aDataSrc.getColSums();
        SumCol aSumCol[] = aColSums.getSumCol();
        for (int i = 0; i < aColSums.getSumColCount(); i++)
        {
            System.out.print(aSumCol[i].getCol());
            System.out.println(aSumCol[i].getDbName());
        }
        System.out.println("Sum Depend Columns:");
        SumDepend aSumDepends = aDataSrc.getColSumDepends();
        DependCol aDependCol[] = aSumDepends.getSumDependCol();
        for (int i = 0; i < aSumDepends.getSumDependColCount(); i++)
        {
            System.out.print(aDependCol[i].getCol());
            System.out.println(aDependCol[i].getDbName());
        }
        StartCell aStartCell = aDataSrc.getStartCell();
        System.out.println("StartCell:");
        System.out.print(aStartCell.getRow());
        System.out.println(aStartCell.getCol());
        System.out.println("Append Data Source:");
        Append aAppends[] = aDataSrc.getAppend();
        for (int j = 0; j < aAppends.length; j++)
        {
            System.out.print(aAppends[j].getCol());
            System.out.println(aAppends[j].getSource());
            AppendCol aAppendCol[] = aAppends[j].getAppendCol();
            for (int i = 0; i < aAppends[j].getAppendColCount(); i++)
            {
                System.out.println(aAppendCol[i].getColName());
                System.out.println(aAppendCol[i].getDbName());
            }
        }
    }
}

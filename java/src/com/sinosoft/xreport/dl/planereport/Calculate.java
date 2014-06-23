package com.sinosoft.xreport.dl.planereport;

import java.util.Calendar;
import java.util.Vector;

import bsh.Interpreter;
import com.sinosoft.xreport.dl.DBExecption;
import com.sinosoft.xreport.dl.DataSourceImpl;
import com.sinosoft.xreport.util.ExprEval;
import com.sinosoft.xreport.util.Str;
import com.sinosoft.xreport.util.XMLPathTool;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Calculate
{

    private DefineXMLHandler aDefineHandler;
    private DefineXML aDefineXML;
    private ResultXMLHandler aResultHandler;
    private ResultXML aResultXML;
    private String sDefineFile;
    private String sResultFile;
    Vector vSubject, vSum[], vReplace, vTotal, vAppend[];
    private int iStartCellRow, iStartCellCol;
    DataResults aDataSet = new DataResults();
    SumDepend aSumDepend;
    DependCol aDependCol[];
    SumColumns aSumCols;
    SumCol aSumCol[];
    ColumnViews aViewCols;
    ViewCol aViewCol[];
    ColumnReplace aReplaceCols;
    ReplaceCol aReplaceCol[];
    Cells aCellSet;
    Cell aCell[];
    Append aAppend[];
    AppendCol aAppendCol[];

    int dataIdx = 0;
    int startRow = 1;
    int changeCol = 0;
    int row = 0;

    public Calculate()
    {}

    public Calculate(String fName)
    {
        setDefineFile(fName);
    }

    /**
     * 设置定义文件的文件名
     * @param fName 定义文件的文件名
     */
    public void setDefineFile(String fName)
    {
        aDefineHandler = new DefineXMLHandler(fName);
        sDefineFile = fName;
        aDefineXML = aDefineHandler.getDefineXML();
        aResultXML = new ResultXML();
        aResultXML.setGCondition(aDefineXML.getGCondition());
        aResultXML.setCols(aDefineXML.getCols());
        aResultXML.setRptBranch(aDefineXML.getRptBranch());
        aResultXML.setRptCode(aDefineXML.getRptCode());
        aResultXML.setRptName(aDefineXML.getRptName());
        aResultXML.setRptEdition(aDefineXML.getRptEdition());
        aResultXML.setRptOperator(aDefineXML.getRptOperator());
        aResultXML.setRptType(aDefineXML.getRptType());
        aResultXML.setRptFile(aDefineXML.getRptFile());
        this.setResultFile();
        aResultXML.toFile(sResultFile);
        aResultHandler = new ResultXMLHandler(sResultFile);
    }

    /**
     * 根据定义文件名生成结果文件名
     */
    private void setResultFile()
    {
        int x = sDefineFile.toLowerCase().indexOf("define");
        if (x > 0)
        {
            sResultFile = sDefineFile.substring(0, x) + "result.xml";
        }
        else
        {
            sResultFile = sDefineFile + "_result.xml";
        }
    }

    /**
     * 设置结果文件名
     * @param param 结果文件名
     */
    public void setResultFile(String param)
    {
        sResultFile = param;
        aResultXML.setRptFile(param);
    }

    /**
     * 获得结果文件名
     * @return 结果文件名
     */
    public String getResultFile()
    {
        return sResultFile;
    }

    /**
     * 获得定义文件名
     * @return 定义文件名
     */
    public String getDefineFile()
    {
        return sDefineFile;
    }

    /**
     * 获得定义文件的处理进程
     * @return 定义文件的处理进程
     */
    public DefineXMLHandler getDefineHandler()
    {
        return aDefineHandler;
    }

    /**
     * 获得定义文件对象
     * @return 定义文件对象
     */
    public DefineXML getDefineXML()
    {
        return aDefineXML;
    }

    /**
     * 获得计算结果文件的处理进程
     * @return 计算结果文件的处理进程
     */
    public ResultXMLHandler getResultHandler()
    {
        return aResultHandler;
    }

    /**
     * 获得计算结果文件对象
     * @return 计算结果文件对象
     */
    public ResultXML getResultXML()
    {
        return aResultXML;
    }

    /**
     * 计算平面/清单式报表
     */
    public void execute()
    {
        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.get(Calendar.HOUR) + ":"
                           + calendar.get(Calendar.MINUTE) + ":"
                           + calendar.get(Calendar.SECOND));
        String subjectSQL = aDefineXML.getSubjectSQL();
        String sumSQL[] = aDefineXML.getSumSQL();
        String totalSQL = aDefineXML.getTotalSQL();
        vSubject = query(subjectSQL);
        System.out.println(sumSQL);
        if (sumSQL.length > 0)
        {
            vSum = new Vector[sumSQL.length];
            for (int i = 0; i < sumSQL.length; i++)
            {
                vSum[i] = query(sumSQL[i]);
            }
        }
        else
        {
            vSum = null;
        }
        vTotal = query(totalSQL);
        String appendSQL[] = aDefineXML.getAppendSQL();
        if (appendSQL.length > 0)
        {
            vAppend = new Vector[appendSQL.length];
        }
        else
        {
            vAppend = null;
        }
        for (int i = 0; i < appendSQL.length; i++)
        {
            vAppend[i] = query(appendSQL[i]);
        }
        //主表计算
        System.out.println("正在计算主题表数据，请稍候……");
        if (vSubject != null)
        {
            retrieveData();
        }
        //计算列
        System.out.println("正在计算主题表计算列数据，请稍候……");
        setSubjectCalcData();
        System.out.println("正在计算附加表数据，请稍候……");
        if (vAppend != null)
        {
            retrieveAppendData();
        }
        //计算单元格
        System.out.println("正在计算单元格数据，请稍候……");
        retrieveCellData();
        //代码替换
        System.out.println("正在进行代码数据转换，请稍候……");
        replaceDataAll();
        //输出到文件
        System.out.println("正在写文件，请稍候……");
        aResultHandler.toFile();
        System.out.println("平面/清单式报表计算完毕");
        calendar = Calendar.getInstance();
        System.out.println(calendar.get(Calendar.HOUR) + ":"
                           + calendar.get(Calendar.MINUTE) + ":"
                           + calendar.get(Calendar.SECOND));
    }

    /**
     * 执行SQL语句
     * @param sql SQL语句
     * @return 查询结果集
     */
    private Vector query(String sql)
    {
        try
        {
            DataSourceImpl aDSImpl = new DataSourceImpl();
            Vector vRet = null;
            if (sql.indexOf("$") >= 0)
            {
                sql = replaceParameter(sql);
            }
            System.out.println("SQL:" + sql);
            if (!sql.trim().equals(""))
            {
                try
                {
                    vRet = (Vector) aDSImpl.getDataSet(sql);
                    return vRet;
                }
                catch (DBExecption dbEx)
                {
                    dbEx.printStackTrace();
                    return null;
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    return null;
                }
//                finally
//                {
//                    return vRet;
//                }
            }
            else
            {
                System.err.println("SQL语句为空串，请检查!!!");
                return null;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 替换sql语句中的参数
     * @param sql 需要替换参数的SQL语句
     * @return 替换完参数的SQL语句
     */
    private String replaceParameter(String sql)
    {
        String gc = getGlobalCondition();
        if (sql.indexOf("?$global?") >= 0)
        {
            sql = Str.replace(sql, "?$global?", gc);
        }
        int idx4 = sql.indexOf("$");
        int idxL = sql.indexOf("?");
        int idxR = -1;
        if (idxL >= 0)
        {
            idxR = sql.indexOf("?", idxL + 1);
        }
        while (idx4 >= 0 && idxL >= 0 && idxR >= 0)
        {
            String paramName = sql.substring(idx4 + 1, idxR);
            String paramValue = GeneralParameter.getParameterValue(paramName);
            sql = Str.replace(sql, sql.substring(idxL, idxR - idxL), paramValue);
            idx4 = sql.indexOf("$");
            idxL = sql.indexOf("?");
            if (idxL >= 0)
            {
                idxR = sql.indexOf("?", idxL + 1);
            }
        }
        return sql;
    }

    /**
     * 获得通用查询条件
     * @return 通用查询条件
     */
    private String getGlobalCondition()
    {
        String gcSQL = "";
        GlobalConditions gcSet = aResultXML.getGCondition();
        GCondition gc[] = gcSet.getConditions();
        for (int i = 0; i < gc.length; i++)
        {
            String type = gc[i].getType();
            String value = gc[i].getValue();
            if (value.indexOf("$") == 0)
            {
                value = GeneralParameter.getParameterValue(value.substring(1));
            }
            if (!gcSQL.equalsIgnoreCase(""))
            {
                gcSQL = gcSQL + " and ";
            }
            gcSQL = gcSQL + type + "='" + value + "'";
        }
        return gcSQL;
    }

    /**
     * 计算所有附加数据源内容
     */
    private void retrieveAppendData()
    {
        DataSrc aDataSrc = aDefineXML.getDataSrc();
        aAppend = aDataSrc.getAppend();
        for (int i = 0; i < vAppend.length; i++)
        {
            aAppendCol = aAppend[i].getAppendCol();
            retrieveAppendData1(i);
        }
    }

    /**
     * 计算一个附加数据源内容
     * @param index 附加数据源索引位置
     */
    private void retrieveAppendData1(int index)
    {
        String value = "";
        String colName[] = new String[aAppendCol.length];
        String dbName[] = new String[aAppendCol.length];
        NodeList nl[] = new NodeList[aAppendCol.length];
        for (int i = 0; i < aAppendCol.length; i++)
        {
            colName[i] = aAppendCol[i].getColName();
            dbName[i] = aAppendCol[i].getDbName();
            nl[i] = aResultHandler.queryData("name", colName[i]);
        }
        //初始化附加数据源依赖列的值
        String dependValue[] = new String[aAppendCol.length];
        for (int i = 0; i < aAppendCol.length; i++)
        {
            dependValue[i] = "";
        }
        //初始化查询结果集位置指针
        startRow = 1;
        //初始化查询结果集位置指针
        row = iStartCellRow;
        for (int i = 0; i < nl[0].getLength(); i++)
        {
            if (isAppendDependValueChanged(nl, dependValue, i) != -1)
            {
                dependValue = getAppendDependValue(nl, i);
                setAppendData(dependValue, index);
            }
        }
    }

    /**
     * 判断附加数据源的数值和依赖值比较是否发生变化
     * @param nl NodeList[]
     * @param dependValue String[]
     * @param rowIdx int
     * @return int
     */
    private int isAppendDependValueChanged(NodeList nl[], String dependValue[],
                                           int rowIdx)
    {
        for (int i = 0; i < aAppendCol.length; i++)
        {
            if (rowIdx < nl[i].getLength())
            {
                Node data = nl[i].item(rowIdx);
                try
                {
                    String value = XMLPathTool.getAttrValue(data, "value");
                    if (!value.equalsIgnoreCase(dependValue[i]))
                    {
                        return i;
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    return -1;
                }
            }
            else
            {
                return -1;
            }
        }
        return -1;
    }

    /**
     * 获得附加数据源依赖列的数据
     * @param nl 依赖列节点集
     * @param rowIdx 行号
     * @return 附加数据源依赖列的数据
     */
    private String[] getAppendDependValue(NodeList nl[], int rowIdx)
    {
        String ret[] = new String[nl.length];

        for (int i = 0; i < nl.length; i++)
        {
            Node data = nl[i].item(rowIdx);
            try
            {
                ret[i] = XMLPathTool.getAttrValue(data, "value");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 判断附加依赖列数据是否改变
     * @param dependValue 当前附加依赖列的值
     * @param rowData 当前数据行
     * @param index 附加数据源索引位置
     * @return 如果有改变，则返回发生改变的列号，否则返回-1
     */
    private int isAppendChanged(String dependValue[], String rowData[],
                                int index)
    {
        for (int i = 0; i < aAppendCol.length; i++)
        {
            String dbName = aAppendCol[i].getDbName();
            int colIdx = findColIdxInResultSet(dbName, vAppend[index]);
            if (colIdx >= 0)
            {
                String data = rowData[colIdx];
                if (!data.equals(dependValue[i]))
                {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 获取符合依赖条件的附加数据源的数据
     * @param dependValue 依赖条件取值
     * @param index 附加数据源索引位置
     */
    private void setAppendData(String dependValue[], int index)
    {
        String rowData[];
        int changeCol;
        int appendCol = aAppend[0].getCol();

        for (int i = startRow; i < vAppend[index].size(); i++)
        {
            rowData = (String[]) vAppend[index].get(i);
            if (dependValue == null)
            {
                changeCol = -1;
            }
            else
            {
                changeCol = isAppendChanged(dependValue, rowData, index);
            }
            if (changeCol == -1)
            {
                String columnName[] = (String[]) vAppend[index].get(0);
                int columnCount = columnName.length;
                for (int j = 0; j < columnCount; j++)
                {
                    aResultHandler.appendData(row,
                                              appendCol + j + iStartCellCol,
                                              rowData[j], columnName[j]);
                    dataIdx++;
                }
                row++;
            }
            else
            {
                startRow = i;
                break;
            }
        }
    }

    /**
     * 计算单元格内容
     */
    private void retrieveCellData()
    {
        aCellSet = aDefineXML.getCells();
        aCell = aCellSet.getCell();
        String value = "";
        for (int i = 0; i < aCell.length; i++)
        {
            String formula = aCell[i].getText();
            String location = aCell[i].getLocation();
            if (formula.indexOf("(") >= 0)
            {
                ExprEval ee = new ExprEval();
                try
                {
                    value = String.valueOf(ee.calculate(formula));
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            else if (formula.indexOf("$") >= 0)
            {
                value = GeneralParameter.getParameterValue(formula.substring(
                        formula.indexOf("$") + 1));
            }
            else
            {
                value = formula;
            }
            aResultHandler.appendCell(location, value);
            dataIdx++;
        }
    }

    /**
     * 接收数据
     */
    private void retrieveData()
    {
        DataSrc aDataSrc = aDefineXML.getDataSrc();
        aViewCols = aDataSrc.getColViews();
        aViewCol = aViewCols.getViewCol();
        aSumDepend = aDataSrc.getColSumDepends();
        aDependCol = aSumDepend.getSumDependCol();
        aSumCols = aDataSrc.getColSums();
        aSumCol = aSumCols.getSumCol();
        String dependValue[] = new String[aDependCol.length];
        iStartCellRow = aDataSrc.getStartCell().getRow();
        iStartCellCol = aDataSrc.getStartCell().getCol();
        int col = 0;
        String rowData[];
        int sumCount = 0;
        if (vSum == null)
        {
            changeCol = -1;
            sumCount = 2;
        }
        else
        {
            changeCol = vSum.length - 1;
            sumCount = vSum[vSum.length - 1].size();
        }
        //初始化查询结果集位置指针
        startRow = 1;
        //初始化查询结果集位置指针
        row = iStartCellRow;
        for (int k = 1; k < sumCount; k++)
        {
            //获得小计依赖项的取值
            dependValue = getDependValue(k);
            //根据小计依赖项的取值在主体表查询结果集中找相应数据，生成结果文件
            setSubjectData(dependValue);
            //取得小计项查询结果数据
            rowData = getSumRowData(dependValue);
            //根据校计依赖项变化列在小计查询结果集中查找相应数据，生成结果文件
            if (rowData != null)
            {
                setSumData(rowData);
            }
        }
        //处理剩余的小计项
        if (vSum != null)
        {
            for (int i = vSum.length - 2; i >= 0; i--)
            {
                changeCol = i;
                rowData = getSumRowData(dependValue);
                //取得小计项查询结果数据
                rowData = getSumRowData(dependValue);
                //根据校计依赖项变化列在小计查询结果集中查找相应数据，生成结果文件
                if (rowData != null)
                {
                    setSumData(rowData);
                }
            }
            //处理合计项
            rowData = (String[]) vTotal.get(1);
            setTotalData(rowData);
        }
    }

    /**
     * 填写合计项数据
     * @param rowData 合计项一行数据
     */
    private void setTotalData(String rowData[])
    {
        aResultHandler.appendData(row, 0, "合计", "合计");
        for (int j = 0; j < aSumCol.length; j++)
        {
            String colName = aSumCol[j].getCol();
            String dbName = aSumCol[j].getDbName();
            int viewColIdx = findColIdxInViewCol(colName);
            String location = aViewCol[viewColIdx].getLocation();
            int colIdx = findColIdxInResultSet(dbName, vTotal);
            aResultHandler.appendData(row,
                                      Integer.parseInt(location) +
                                      iStartCellCol,
                                      rowData[colIdx], colName);
            dataIdx++;
        }
        dataIdx++;
        row++;
    }

    /**
     * 根据小计依赖值和变化列获取小计值
     * @param dependValue 小计依赖值
     * @return 小计值
     */
    private String[] getSumRowData(String dependValue[])
    {
        String rowData[] = null;
        int i, j = 1;
        if (vSum != null)
        {
            for (j = 1; j < vSum[changeCol].size(); j++)
            {
                rowData = (String[]) vSum[changeCol].get(j);
                for (i = 0; i <= changeCol; i++)
                {
                    String dbName = aDependCol[i].getDbName();
                    int colIdx = findColIdxInResultSet(dbName, vSum[changeCol]);
                    if (colIdx >= 0)
                    {
                        String data = rowData[colIdx];
                        if (!data.equals(dependValue[i]))
                        {
                            break;
                        }
                    }
                }
                if (i > changeCol)
                {
                    break;
                }
            }
            if (j <= vSum[changeCol].size())
            {
                return rowData;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * 填写小计项数据
     * @param rowData 小计项一行数据
     */
    private void setSumData(String rowData[])
    {
        if (changeCol >= 0)
        {
            aResultHandler.appendData(row, changeCol + iStartCellCol, "小计",
                                      "小计");
        }
        else
        {
            String colName = aDependCol[0].getCol();
            int viewColIdx = findColIdxInViewCol(colName);
            String location = aViewCol[viewColIdx].getLocation();
            aResultHandler.appendData(row,
                                      Integer.parseInt(location) +
                                      iStartCellCol,
                                      "小计", "小计");
        }
        dataIdx++;
        for (int j = 0; j < aSumCol.length; j++)
        {
            String colName = aSumCol[j].getCol();
            String dbName = aSumCol[j].getDbName();
            int viewColIdx = findColIdxInViewCol(colName);
            String location = aViewCol[viewColIdx].getLocation();
            int colIdx = findColIdxInResultSet(dbName, vSum[changeCol]);
            aResultHandler.appendData(row,
                                      Integer.parseInt(location) +
                                      iStartCellCol,
                                      rowData[colIdx], colName);
            dataIdx++;
        }
        row++;
    }

    /**
     * 获取符合求和依赖条件的主题数据
     * @param dependValue 求和依赖条件取值，当此值为null时，表示没有小计项计算
     */
    private void setSubjectData(String dependValue[])
    {
        String rowData[];
        int changeCol;

        for (int i = startRow; i < vSubject.size(); i++)
        {
            rowData = (String[]) vSubject.get(i);
            if (dependValue == null)
            {
                changeCol = -1;
            }
            else
            {
                changeCol = isSubjectChanged(dependValue, rowData);
            }
            if (changeCol == -1)
            {
                for (int j = 0; j < aViewCol.length; j++)
                {
                    String src = aViewCol[j].getSource();
                    if (src.equalsIgnoreCase("db"))
                    {
                        String dbName = aViewCol[j].getDbName();
                        String colName = aViewCol[j].getCol();
                        String location = aViewCol[j].getLocation();
                        int colIdx = findColIdxInResultSet(dbName, vSubject);
                        aResultHandler.appendData(row,
                                                  Integer.parseInt(location) +
                                                  iStartCellCol, rowData[colIdx],
                                                  colName);
                        dataIdx++;
                    }
                }
                row++;
            }
            else
            {
                startRow = i;
                break;
            }
        }
    }

    /**
     * 求主题数据表计算列
     */
    private void setSubjectCalcData()
    {
        String rowData[];
        int changeCol;
        Interpreter calcParser = new Interpreter();

        try
        {
            for (int j = 0; j < aViewCol.length; j++)
            {
                String src = aViewCol[j].getSource();
                if (!src.equalsIgnoreCase("db"))
                {
                    String formula = aViewCol[j].getFormula();
                    String colName = aViewCol[j].getCol();
                    String location = aViewCol[j].getLocation();
                    Vector calcCol = analyseFormula(formula);
                    NodeList nl[] = getFormulaDataSource(calcCol);
                    for (int i = 0; i < nl[0].getLength(); i++)
                    {
                        String calcFormula = formula;
                        Node n = nl[0].item(i);
                        row = Integer.parseInt(XMLPathTool.getAttrValue(n,
                                "row"));
                        for (int k = 0; k < calcCol.size(); k++)
                        {
                            Node data = nl[k].item(i);
                            String dataValue = XMLPathTool.getAttrValue(data,
                                    "value");
                            calcFormula = Str.replace(calcFormula,
                                    "[" + (String) calcCol.get(k) + "]",
                                    dataValue);
                        }
                        String dataResult = String.valueOf(calcParser.eval(
                                calcFormula));
                        aResultHandler.appendData(row,
                                                  Integer.parseInt(location) +
                                                  iStartCellCol, dataResult,
                                                  colName);
                        dataIdx++;
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * 获得计算相关列的数据
     * @param calcCol 计算相关列名
     * @return 计算相关列的数据
     */
    private NodeList[] getFormulaDataSource(Vector calcCol)
    {
        NodeList nl[] = new NodeList[calcCol.size()];
        for (int i = 0; i < calcCol.size(); i++)
        {
            nl[i] = aResultHandler.queryData("name", (String) calcCol.get(i));
        }
        return nl;
    }

    /**
     * 分析计算公式，得到相关列名
     * @param formula 计算公式
     * @return 相关列名
     */
    private Vector analyseFormula(String formula)
    {
        String analyseStr = formula;
        Vector calcCol = new Vector();
        String colName = "";
        int idxL = 0, idxR = 0;
        idxL = analyseStr.indexOf("[");
        idxR = analyseStr.indexOf("]");
        while (idxL >= 0 && idxR >= 0)
        {
            colName = analyseStr.substring(idxL + 1, idxR);
            analyseStr = analyseStr.substring(idxR + 1).trim();
            idxL = analyseStr.indexOf("[");
            idxR = analyseStr.indexOf("]");
            calcCol.add(colName);
        }
        return calcCol;
    }

    /**
     * 替换所有数据
     */
    private void replaceDataAll()
    {
        DataSrc aDataSrc = aDefineXML.getDataSrc();
        aReplaceCols = aDataSrc.getColReplaces();
        aReplaceCol = aReplaceCols.getReplaceCol();
        String dataCol, dispCol, repCol, repSrc;
        for (int i = 0; i < aReplaceCols.getReplaceColCount(); i++)
        {
            repCol = aReplaceCol[i].getColumn();
            dataCol = aReplaceCol[i].getDataColumn();
            dispCol = aReplaceCol[i].getDisplayColumn();
            repSrc = aReplaceCol[i].getSource();
            vReplace = query(repSrc);
            replaceDataOne(repCol, dataCol, dispCol);
        }
    }

    /**
     * 替换一列数据
     * @param repCol 要替换的列名
     * @param dataCol 要替换数据列名
     * @param dispCol 替换成对应列名
     */
    private void replaceDataOne(String repCol, String dataCol, String dispCol)
    {
        NodeList nl = aResultHandler.queryData("name", repCol);
        String tmp = "";
        String toData = "";
        for (int j = 0; j < nl.getLength(); j++)
        {
            Node data = nl.item(j);
            String fromData = aResultHandler.getDataAttr(data, "value");
            if (!fromData.equals(tmp))
            {
                tmp = fromData;
                toData = findReplaceValue(dataCol, dispCol, fromData);
            }
            data = aResultHandler.setDataNode(data, toData.trim(), "value");
        }
    }

    /**
     * 在代码转换结果集中查找对应的值
     * @param dataCol 数据列名
     * @param dispCol 显示列名
     * @param value 代码值
     * @return 对应的值
     */
    private String findReplaceValue(String dataCol, String dispCol,
                                    String value)
    {
        int dataIdx, dispIdx;
        dataIdx = findColIdxInResultSet(dataCol, vReplace);
        dispIdx = findColIdxInResultSet(dispCol, vReplace);
        String rowData[];
        String fromData, toData = "";
        for (int i = 1; i < vReplace.size(); i++)
        {
            rowData = (String[]) vReplace.get(i);
            fromData = rowData[dataIdx];
            if (fromData.equals(value))
            {
                toData = rowData[dispIdx];
                break;
            }
            else
            {
                toData = "";
            }
        }
        return toData;
    }

    /**
     * 获取当前行的求和依赖值
     * @param rowIdx 当前行号
     * @return 当前行的求和依赖值
     */
    private String[] getDependValue(int rowIdx)
    {
        if (vSum == null)
        {
            return null;
        }
        String dependValue[] = new String[aDependCol.length];
        for (int i = 0; i < aDependCol.length; i++)
        {
            String dbName = aDependCol[i].getDbName();
            int colIdx = findColIdxInResultSet(dbName, vSum[changeCol]);
            if (colIdx >= 0)
            {
                String rowData[] = (String[]) vSum[changeCol].get(rowIdx);
                dependValue[i] = rowData[colIdx];
            }
            else
            {
                dependValue[i] = "";
            }
        }
        return dependValue;
    }

    /**
     * 判断求和依赖列数据是否改变
     * @param dependValue 当前求和依赖列的值
     * @param rowData 当前数据行
     * @return 如果有改变，则返回发生改变的列号，否则返回-1
     */
    private int isSubjectChanged(String dependValue[], String rowData[])
    {
        for (int i = 0; i < aDependCol.length; i++)
        {
            String dbName = aDependCol[i].getDbName();
            int colIdx = findColIdxInResultSet(dbName, vSubject);
            if (colIdx >= 0)
            {
                String data = rowData[colIdx];
                if (!data.equals(dependValue[i]))
                {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 根据列名，在数据列中找列号
     * @param colName 列名
     * @return 如果找到返回列号，否则返回-1；
     */
    private int findColIdxInViewCol(String colName)
    {
        int j;
        for (j = 0; j < aViewCol.length; j++)
        {
            if (aViewCol[j].getCol().equalsIgnoreCase(colName))
            {
                break;
            }
        }
        if (j < aViewCol.length)
        {
            return j;
        }
        else
        {
            return -1;
        }
    }

    /**
     * 根据列号在数据列中找对应列的数据库列名
     * @param colIdx 列号
     * @return 列的数据库列名
     */
    private String getViewColDbName(int colIdx)
    {
        return aViewCol[colIdx].getDbName();
    }

    /**
     * 根据列名在数据列中找对应列的数据库列名
     * @param colName 列名
     * @return 列的数据库列名
     */
    private String getViewColDbName(String colName)
    {
        int colIdx = findColIdxInViewCol(colName);
        if (colIdx >= 0)
        {
            return getViewColDbName(colIdx);
        }
        else
        {
            return null;
        }
    }

    /**
     * 在指定结果集中找对应列号
     * @param colName 数据库列名
     * @param v 查询结果集
     * @return 如果找到，返回列号，否则返回-1
     */
    private int findColIdxInResultSet(String colName, Vector v)
    {
        int j;
        String rowStr[] = (String[]) v.get(0);
        for (j = 0; j < rowStr.length; j++)
        {
            if (rowStr[j].equalsIgnoreCase(colName))
            {
                break;
            }
        }
        if (j < rowStr.length)
        {
            return j;
        }
        else
        {
            return -1;
        }
    }

    public static void main(String args[])
    {
        GeneralParameter.setCenterCode("330700");
        GeneralParameter.setCenterName("绍兴市分公司");
        GeneralParameter.setBeginTime("2001-12-01");
        GeneralParameter.setEndTime("2001-12-31");
        Calculate calc = new Calculate(
                "E:\\jbuilder5\\xreport\\aa_ddd_define.xml");
        calc.execute();
        System.exit(0);

    }
}

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
     * ���ö����ļ����ļ���
     * @param fName �����ļ����ļ���
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
     * ���ݶ����ļ������ɽ���ļ���
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
     * ���ý���ļ���
     * @param param ����ļ���
     */
    public void setResultFile(String param)
    {
        sResultFile = param;
        aResultXML.setRptFile(param);
    }

    /**
     * ��ý���ļ���
     * @return ����ļ���
     */
    public String getResultFile()
    {
        return sResultFile;
    }

    /**
     * ��ö����ļ���
     * @return �����ļ���
     */
    public String getDefineFile()
    {
        return sDefineFile;
    }

    /**
     * ��ö����ļ��Ĵ������
     * @return �����ļ��Ĵ������
     */
    public DefineXMLHandler getDefineHandler()
    {
        return aDefineHandler;
    }

    /**
     * ��ö����ļ�����
     * @return �����ļ�����
     */
    public DefineXML getDefineXML()
    {
        return aDefineXML;
    }

    /**
     * ��ü������ļ��Ĵ������
     * @return �������ļ��Ĵ������
     */
    public ResultXMLHandler getResultHandler()
    {
        return aResultHandler;
    }

    /**
     * ��ü������ļ�����
     * @return �������ļ�����
     */
    public ResultXML getResultXML()
    {
        return aResultXML;
    }

    /**
     * ����ƽ��/�嵥ʽ����
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
        //�������
        System.out.println("���ڼ�����������ݣ����Ժ򡭡�");
        if (vSubject != null)
        {
            retrieveData();
        }
        //������
        System.out.println("���ڼ����������������ݣ����Ժ򡭡�");
        setSubjectCalcData();
        System.out.println("���ڼ��㸽�ӱ����ݣ����Ժ򡭡�");
        if (vAppend != null)
        {
            retrieveAppendData();
        }
        //���㵥Ԫ��
        System.out.println("���ڼ��㵥Ԫ�����ݣ����Ժ򡭡�");
        retrieveCellData();
        //�����滻
        System.out.println("���ڽ��д�������ת�������Ժ򡭡�");
        replaceDataAll();
        //������ļ�
        System.out.println("����д�ļ������Ժ򡭡�");
        aResultHandler.toFile();
        System.out.println("ƽ��/�嵥ʽ����������");
        calendar = Calendar.getInstance();
        System.out.println(calendar.get(Calendar.HOUR) + ":"
                           + calendar.get(Calendar.MINUTE) + ":"
                           + calendar.get(Calendar.SECOND));
    }

    /**
     * ִ��SQL���
     * @param sql SQL���
     * @return ��ѯ�����
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
                System.err.println("SQL���Ϊ�մ�������!!!");
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
     * �滻sql����еĲ���
     * @param sql ��Ҫ�滻������SQL���
     * @return �滻�������SQL���
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
     * ���ͨ�ò�ѯ����
     * @return ͨ�ò�ѯ����
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
     * �������и�������Դ����
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
     * ����һ����������Դ����
     * @param index ��������Դ����λ��
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
        //��ʼ����������Դ�����е�ֵ
        String dependValue[] = new String[aAppendCol.length];
        for (int i = 0; i < aAppendCol.length; i++)
        {
            dependValue[i] = "";
        }
        //��ʼ����ѯ�����λ��ָ��
        startRow = 1;
        //��ʼ����ѯ�����λ��ָ��
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
     * �жϸ�������Դ����ֵ������ֵ�Ƚ��Ƿ����仯
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
     * ��ø�������Դ�����е�����
     * @param nl �����нڵ㼯
     * @param rowIdx �к�
     * @return ��������Դ�����е�����
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
     * �жϸ��������������Ƿ�ı�
     * @param dependValue ��ǰ���������е�ֵ
     * @param rowData ��ǰ������
     * @param index ��������Դ����λ��
     * @return ����иı䣬�򷵻ط����ı���кţ����򷵻�-1
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
     * ��ȡ�������������ĸ�������Դ������
     * @param dependValue ��������ȡֵ
     * @param index ��������Դ����λ��
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
     * ���㵥Ԫ������
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
     * ��������
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
        //��ʼ����ѯ�����λ��ָ��
        startRow = 1;
        //��ʼ����ѯ�����λ��ָ��
        row = iStartCellRow;
        for (int k = 1; k < sumCount; k++)
        {
            //���С���������ȡֵ
            dependValue = getDependValue(k);
            //����С���������ȡֵ��������ѯ�����������Ӧ���ݣ����ɽ���ļ�
            setSubjectData(dependValue);
            //ȡ��С�����ѯ�������
            rowData = getSumRowData(dependValue);
            //����У��������仯����С�Ʋ�ѯ������в�����Ӧ���ݣ����ɽ���ļ�
            if (rowData != null)
            {
                setSumData(rowData);
            }
        }
        //����ʣ���С����
        if (vSum != null)
        {
            for (int i = vSum.length - 2; i >= 0; i--)
            {
                changeCol = i;
                rowData = getSumRowData(dependValue);
                //ȡ��С�����ѯ�������
                rowData = getSumRowData(dependValue);
                //����У��������仯����С�Ʋ�ѯ������в�����Ӧ���ݣ����ɽ���ļ�
                if (rowData != null)
                {
                    setSumData(rowData);
                }
            }
            //����ϼ���
            rowData = (String[]) vTotal.get(1);
            setTotalData(rowData);
        }
    }

    /**
     * ��д�ϼ�������
     * @param rowData �ϼ���һ������
     */
    private void setTotalData(String rowData[])
    {
        aResultHandler.appendData(row, 0, "�ϼ�", "�ϼ�");
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
     * ����С������ֵ�ͱ仯�л�ȡС��ֵ
     * @param dependValue С������ֵ
     * @return С��ֵ
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
     * ��дС��������
     * @param rowData С����һ������
     */
    private void setSumData(String rowData[])
    {
        if (changeCol >= 0)
        {
            aResultHandler.appendData(row, changeCol + iStartCellCol, "С��",
                                      "С��");
        }
        else
        {
            String colName = aDependCol[0].getCol();
            int viewColIdx = findColIdxInViewCol(colName);
            String location = aViewCol[viewColIdx].getLocation();
            aResultHandler.appendData(row,
                                      Integer.parseInt(location) +
                                      iStartCellCol,
                                      "С��", "С��");
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
     * ��ȡ�������������������������
     * @param dependValue �����������ȡֵ������ֵΪnullʱ����ʾû��С�������
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
     * ���������ݱ������
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
     * ��ü�������е�����
     * @param calcCol �����������
     * @return ��������е�����
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
     * �������㹫ʽ���õ��������
     * @param formula ���㹫ʽ
     * @return �������
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
     * �滻��������
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
     * �滻һ������
     * @param repCol Ҫ�滻������
     * @param dataCol Ҫ�滻��������
     * @param dispCol �滻�ɶ�Ӧ����
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
     * �ڴ���ת��������в��Ҷ�Ӧ��ֵ
     * @param dataCol ��������
     * @param dispCol ��ʾ����
     * @param value ����ֵ
     * @return ��Ӧ��ֵ
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
     * ��ȡ��ǰ�е��������ֵ
     * @param rowIdx ��ǰ�к�
     * @return ��ǰ�е��������ֵ
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
     * �ж���������������Ƿ�ı�
     * @param dependValue ��ǰ��������е�ֵ
     * @param rowData ��ǰ������
     * @return ����иı䣬�򷵻ط����ı���кţ����򷵻�-1
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
     * �����������������������к�
     * @param colName ����
     * @return ����ҵ������кţ����򷵻�-1��
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
     * �����к������������Ҷ�Ӧ�е����ݿ�����
     * @param colIdx �к�
     * @return �е����ݿ�����
     */
    private String getViewColDbName(int colIdx)
    {
        return aViewCol[colIdx].getDbName();
    }

    /**
     * �������������������Ҷ�Ӧ�е����ݿ�����
     * @param colName ����
     * @return �е����ݿ�����
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
     * ��ָ����������Ҷ�Ӧ�к�
     * @param colName ���ݿ�����
     * @param v ��ѯ�����
     * @return ����ҵ��������кţ����򷵻�-1
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
        GeneralParameter.setCenterName("�����зֹ�˾");
        GeneralParameter.setBeginTime("2001-12-01");
        GeneralParameter.setEndTime("2001-12-31");
        Calculate calc = new Calculate(
                "E:\\jbuilder5\\xreport\\aa_ddd_define.xml");
        calc.execute();
        System.exit(0);

    }
}

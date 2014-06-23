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
     * ����xml�ļ���
     * @param aFile xml�ļ���
     */
    private void setFileName(String aFile)
    {
        sFileName = aFile;
        File oFile = new File(sFileName);
        aTool = new XMLPathTool(oFile);
    }

    /**
     * ��xml�ļ���ȡ�ñ���ʹ�õ�λ����
     * @return ����ʹ�õ�λ����
     */
    public String getBranch()
    {
        XObject xobj = aTool.parseX("/report_define/info/branch");
        String ret = xobj.toString();
        return ret;
    }

    /**
     * ��xml�ļ���ȡ�ñ������
     * @return �������
     */
    public String getCode()
    {
        XObject xobj = aTool.parseX("/report_define/info/code");
        String ret = xobj.toString();
        return ret;
    }

    /**
     * ��xml�ļ���ȡ�ñ�������
     * @return ��������
     */
    public String getName()
    {
        XObject xobj = aTool.parseX("/report_define/info/name");
        String ret = xobj.toString();
        return ret;
    }

    /**
     * ��xml�ļ���ȡ�ñ�����
     * @return ������
     */
    public String getEdition()
    {
        XObject xobj = aTool.parseX("/report_define/info/edition");
        String ret = xobj.toString();
        return ret;
    }

    /**
     * ��xml�ļ���ȡ�ñ���ʹ���߱���
     * @return ����ʹ���߱���
     */
    public String getOperator()
    {
        XObject xobj = aTool.parseX("/report_define/info/operator");
        String ret = xobj.toString();
        return ret;
    }

    /**
     * ��xml�ļ���ȡ�ñ�����������
     * @return ������������
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
     * ��xml�ļ���ȡ�ñ���ȫ�ֲ�ѯ��������
     * @param condition ȫ�ֲ�ѯ�����ڵ�
     * @return ����ȫ�ֲ�ѯ��������
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
     * ��xml�ļ���ȡ�ñ���ȫ�ֲ�ѯ����ֵ
     * @param condition ȫ�ֲ�ѯ�����ڵ�
     * @return ����ȫ�ֲ�ѯ����ֵ
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
     * ȡ��ȫ�ֲ�ѯ��������
     * @return ȫ�ֲ�ѯ��������
     */
    public int getGCCount()
    {
        NodeList nl = aTool.parseN("/report_define/data/global/condition");
        return nl.getLength();
    }

    /**
     * ȡ��ȫ�ֲ�ѯ����
     * @return ȫ�ֲ�ѯ����
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
     * ȡ����ʾ��ͷ��ȡ������
     * @param column ��ʾ��ͷ�ڵ�
     * @return ��ʾ��ͷ��ȡ������
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
     * ȡ����ʾ��ͷ�ļ��㹫ʽ
     * @param column ��ʾ��ͷ�ڵ�
     * @return ��ʾ��ͷ�ļ��㹫ʽ
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
     * ȡ����ʾ��ͷ��λ��
     * @param column ��ʾ��ͷ�ڵ�
     * @return ��ʾ��ͷ��λ��
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
     * ȡ����ʾ��ͷ��λ��
     * @param column ��ʾ��ͷ�ڵ�
     * @return ��ʾ��ͷ��λ��
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
     * ȡ����ʾ��ͷ���ı��ַ�
     * @param column ��ʾ��ͷ�ڵ�
     * @return ��ʾ��ͷ���ı��ַ�
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
     * ȡ��ȫ�ֲ�ѯ��������
     * @return ȫ�ֲ�ѯ��������
     */
    public int getColCount()
    {
        NodeList nl = aTool.parseN("/report_define/data/cols/col");
        return nl.getLength();
    }

    /**
     * ȡ��ȫ�ֲ�ѯ����
     * @return ȫ�ֲ�ѯ����
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
     * ȡ�õ�Ԫ����ı��ַ�
     * @param cell ��Ԫ��
     * @return ��Ԫ����ı��ַ�
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
     * ȡ�õ�Ԫ���λ��
     * @param cell ��Ԫ��ڵ�
     * @return ��Ԫ���λ��
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
     * ȡ�õ�Ԫ�����
     * @return ��Ԫ�����
     */
    public int getCellCount()
    {
        NodeList nl = aTool.parseN("/report_define/data/cells/cell");
        return nl.getLength();
    }

    /**
     * ȡ�õ�Ԫ��
     * @return ��Ԫ��
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
     * ȡ�������е�λ��
     * @param viewCol �����нڵ�
     * @return �����е�λ��
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
     * ȡ�������еļ��㹫ʽ
     * @param viewCol �����нڵ�
     * @return �����еļ��㹫ʽ
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
     * ȡ�������е���Դ
     * @param viewCol �����нڵ�
     * @return �����е���Դ
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
     * ȡ�������е���ʾ����
     * @param viewCol �����нڵ�
     * @return �����е���ʾ����
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
     * ȡ�������е����ݿ�����
     * @param viewCol �����нڵ�
     * @return �����е����ݿ�����
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
     * ȡ�������е�����
     * @param viewCol �����нڵ�
     * @return �����е�����
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
     * ȡ�������и���
     * @return �����и���
     */
    public int getViewColCount()
    {
        NodeList nl = aTool.parseN(
                "/report_define/data/dataSrc/columnViews/viewCol");
        return nl.getLength();
    }

    /**
     * ȡ��������
     * @return ������
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
     * ȡ��С�ơ��ϼ��е�����
     * @param sumCol С�ơ��ϼ��нڵ�
     * @return С�ơ��ϼ��е�����
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
     * ȡ��С�ơ��ϼ��е����ݿ�����
     * @param sumCol С�ơ��ϼ��нڵ�
     * @return С�ơ��ϼ��е����ݿ�����
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
     * ȡ��С�ơ��ϼ��и���
     * @return С�ơ��ϼ��и���
     */
    public int getSumColCount()
    {
        NodeList nl = aTool.parseN(
                "/report_define/data/dataSrc/sumColumns/sumCol");
        return nl.getLength();
    }

    /**
     * ȡ��С�ơ��ϼ���
     * @return С�ơ��ϼ���
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
     * ȡ��С�ơ��ϼ������е�����
     * @param sumDependCol С�ơ��ϼ������нڵ�
     * @return С�ơ��ϼ������е�����
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
     * ȡ��С�ơ��ϼ������е����ݿ�����
     * @param sumDependCol С�ơ��ϼ������нڵ�
     * @return С�ơ��ϼ������е����ݿ�����
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
     * ȡ��С�ơ��ϼ������и���
     * @return С�ơ��ϼ������и���
     */
    public int getSumDependColCount()
    {
        NodeList nl = aTool.parseN(
                "/report_define/data/dataSrc/sumDepend/dependCol");
        return nl.getLength();
    }

    /**
     * ȡ��С�ơ��ϼ�������
     * @return С�ơ��ϼ�������
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
     * ȡ����ʼ��Ԫ����к�
     * @return ��ʼ��Ԫ����к�
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
     * ȡ����ʼ��Ԫ����к�
     * @return ��ʼ��Ԫ����к�
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
     * ȡ����ʼ��Ԫ�����Ϣ
     * @return ��ʼ��Ԫ�����Ϣ
     */
    public StartCell getStartCell()
    {
        StartCell ret = new StartCell();
        ret.setCol(this.getStartCellCol());
        ret.setRow(this.getStartCellRow());
        return ret;
    }

    /**
     * ��xml�ļ���ȡ�ñ���������
     * @return ���������
     */
    public String getSubject()
    {
        XObject xobj = aTool.parseX("/report_define/data/dataSrc/subject");
        String ret = xobj.toString();
        return ret;
    }

    /**
     * ��xml�ļ���ȡ�ñ���ĸ�ʽ�ļ�
     * @return ����ĸ�ʽ�ļ�
     */
    public String getFormatFile()
    {
        XObject xobj = aTool.parseX("/report_define/format/file");
        String ret = xobj.toString();
        return ret;
    }

    /**
     * ��xml�ļ���ȡ�ñ���������ѯ����
     * @return ����������ѯ����
     */
    public String getStrWhere()
    {
        XObject xobj = aTool.parseX("/report_define/data/dataSrc/strWhere");
        String ret = xobj.toString();
        return ret;
    }

    /**
     * ��xml�ļ���ȡ�ø�������Դ
     * @return ��������Դ
     */
    public int getAppendCount()
    {
        NodeList nlAppend = aTool.parseN("/report_define/data/dataSrc/append");
        return nlAppend.getLength();
    }

    /**
     * ��xml�ļ���ȡ�ø�������Դ
     * @return ��������Դ
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
     * ��xml�ļ���ȡ�ø�������Դ�Ĳ�ѯSQL
     * @param append ��������Դ�ڵ�
     * @return ��������Դ�Ĳ�ѯSQL
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
     * ��xml�ļ���ȡ�ø�������Դ����ʾ��ʼ��
     * @param append ��������Դ�ڵ�
     * @return ��������Դ����ʾ��ʼ��
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
     * ȡ�ø�������Դ�����е�����
     * @param appendCol ��������Դ�����нڵ�
     * @return ��������Դ�����е�����
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
     * ȡ�ø�������Դ�����ж�Ӧ�����ݿ���
     * @param appendCol ��������Դ�����нڵ�
     * @return ��������Դ�����ж�Ӧ�����ݿ���
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
     * ȡ�ø�������Դ�����и���
     * @param append ��������Դ�ڵ�
     * @return ��������Դ�����и���
     */
    public int getAppendColCount(Node append)
    {
        NodeList nl = aTool.parseN(append, "appendCol");
        return nl.getLength();
    }

    /**
     * ȡ�ô����滻�е�����
     * @param replaceCol �����滻�нڵ�
     * @return �����滻�е�����
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
     * ȡ�ô����滻�е�Ҫ�滻������
     * @param replaceCol �����滻�нڵ�
     * @return �����滻�е�Ҫ�滻������
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
     * ȡ�ô����滻�и���
     * @return �����滻�и���
     */
    public int getReplaceColCount()
    {
        NodeList nl = aTool.parseN(
                "/report_define/data/dataSrc/columnReplace/replaceCol");
        return nl.getLength();
    }

    /**
     * ȡ�ô����滻�е��滻Դ��ѯSQL
     * @param replaceCol �����滻�нڵ�
     * @return �����滻�е��滻Դ��ѯSQL
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
     * ȡ�ô����滻�е��滻����
     * @param replaceCol �����滻�нڵ�
     * @return �����滻�е��滻����
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
     * ȡ�ô����滻��
     * @return �����滻��
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
     * ȡ������Դ����
     * @return ����Դ����
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
     * ȡ���嵥ʽ������
     * @return �嵥ʽ������
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

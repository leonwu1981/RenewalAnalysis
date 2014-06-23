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
            ret = XMLPathTool.getAttrValue(null, "type", "/report_define/data");
        }
        catch (Exception e)
        {
            ret = "";
        }
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
     * ȡ�ý��ֵ
     * @param data ����ڵ�
     * @return ���ֵ
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
     * ȡ�ý������
     * @param data ����ڵ�
     * @return �������
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
     * ȡ�ý�����к�
     * @param data ����ڵ�
     * @return ������к�
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
     * ȡ�ý�����к�
     * @param data ����ڵ�
     * @return ������к�
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
     * ȡ�ý������
     * @return �������
     */
    public int getDataResultCount()
    {
        NodeList nl = aTool.parseN("/report_define/data/dataResults/data");
        return nl.getLength();
    }

    /**
     * ȡ�ý��
     * @return ���
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
     * ȡ���嵥ʽ������
     * @return �嵥ʽ������
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
     * ��Ӽ��������ݽڵ�
     * @param row �к�
     * @param col �к�
     * @param value ������ֵ
     * @param name ����������
     * @return �棺�ɹ����٣�ʧ��
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
     * ��Ӽ�������Ԫ��ڵ�
     * @param location ��Ԫ��λ��
     * @param text ��Ԫ������
     * @return �棺�ɹ����٣�ʧ��
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
     * ���ü������ڵ�����ֵ
     * @param aData �������ڵ�
     * @param value ����ֵ
     * @param name ��������
     * @return �������ڵ�
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
     * ȡ�ü������ڵ�����ֵ
     * @param aData �������ڵ�
     * @param name ��������
     * @return �������ڵ�����ֵ
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
     * ���ҷ��������ļ������ڵ�
     * @param name ����������ȡֵ
     * @param value ������ֵȡֵ
     * @return ���������ļ������ڵ���
     */
    public NodeList queryData(String name, String value)
    {
        String xpath = "/report_define/data/dataResults/data[@" + name + "='" +
                       value + "']";
        NodeList nl = aTool.parseN(xpath);
        return nl;
    }

    /**
     * ���ҷ��ϼ��������ļ������ڵ�
     * @param formula ��������
     * @return ���������ļ������ڵ���
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

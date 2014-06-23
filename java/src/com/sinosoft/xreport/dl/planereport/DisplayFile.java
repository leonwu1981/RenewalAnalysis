package com.sinosoft.xreport.dl.planereport;

import java.io.IOException;

import com.f1j.ss.CellFormat;
import com.f1j.ss.Constants;
import com.f1j.ss.RangeRef;
import com.f1j.util.F1Exception;

public class DisplayFile
{

    com.f1j.ss.Book book, bookNew;
    com.f1j.ss.BookModelImpl bookModel, bookModelNew;
    com.f1j.ss.CellFormat cellFmt;
    com.f1j.ss.ReadParams rp = new com.f1j.ss.ReadParams();
    String resultXMLFile = "";
    String displayFile = "";
    String templateFile = "";
    ResultXML aResultXML;

    public DisplayFile()
    {}

    public DisplayFile(String xmlFile)
    {
        setResultXMLFile(xmlFile);
    }

    public void setResultXMLFile(String xmlFile)
    {
        resultXMLFile = xmlFile;
        ResultXMLHandler aHandler = new ResultXMLHandler(resultXMLFile);
        aResultXML = aHandler.getResultXML();
        displayFile = resultXMLFile.substring(0, resultXMLFile.indexOf(".xml")) +
                      ".xls";
        templateFile = com.sinosoft.xreport.util.SysConfig.FILEPATH +
                       aResultXML.getRptFile();
        if (readTemplate())
        {
            if (fillData())
            {
                if (fillCell())
                {
                    toDisplayFile();
                }
            }
        }
    }

    /**
     * 读取报表定义模板文件
     * @return 真：成功；假：失败
     */
    private boolean readTemplate()
    {
        try
        {
            book = new com.f1j.ss.BookImpl();
            bookModel = new com.f1j.ss.BookModelImpl(book);
            //从指定的格式文件中读入数据
            com.f1j.ss.ReadResults result;
            result = bookModel.read(templateFile, rp);
            bookNew = new com.f1j.ss.BookImpl();
            bookModelNew = new com.f1j.ss.BookModelImpl(bookNew);
            result = bookModelNew.read(templateFile, rp);
            bookModelNew.setDefaultFont(bookModel.getDefaultFontName(),
                                        bookModel.getDefaultFontSize());
            return true;
        }
        catch (java.net.MalformedURLException urlEx)
        {
            urlEx.printStackTrace();
            return false;
        }
        catch (java.io.IOException ioEx)
        {
            ioEx.printStackTrace();
            return false;
        }
        catch (F1Exception f1Ex)
        {
            f1Ex.printStackTrace();
            return false;
        }
    }

    /**
     * 填充计算结果数据
     * @return 真：成功；假：失败
     */
    private boolean fillData()
    {
        DataResults aDataSet = aResultXML.getDataResults();
        Data aData[] = aDataSet.getResultData();
        int startRow = 0, startCol = 0, rowTemp = -1;
        String printedRow = "";
        try
        {
            for (int i = 0; i < aDataSet.getResultCount(); i++)
            {
                int row = aData[i].getRow();
                int col = aData[i].getCol();
                String value = aData[i].getValue();
                if (i == 0)
                {
                    startRow = row;
                    startCol = col;
                }
                CellFormat cellFmt = bookModel.getCellFormat(startRow, col,
                        startRow, col);
                if (row != rowTemp)
                {
                    rowTemp = row;
                    if (printedRow.indexOf(String.valueOf(row)) == -1)
                    {
                        printedRow = printedRow + String.valueOf(row) + ",";
                        if (row > startRow)
                        {
                            bookModelNew.insertRange(row, col, row, col,
                                    Constants.eShiftRows);
                        }
                    }
                }
                cellFmt.useAllFormats();
                bookModelNew.setCellFormat(cellFmt, row, col, row, col);
                if (isNumberCell(cellFmt))
                {
                    if (!isNumber(value))
                    {
                        bookModelNew.setText(row, col, " ");
                    }
                    else
                    {
                        bookModelNew.setNumber(row, col,
                                               Double.parseDouble(value));
                    }
                }
                else
                {
                    bookModelNew.setText(row, col, value);
                }
            }
            return true;
        }
        catch (F1Exception f1Ex)
        {
            f1Ex.printStackTrace();
            return false;
        }
    }

    public boolean isNumber(String sText)
    {
        try
        {
            Double.parseDouble(sText);
            return true;
        }
        catch (NumberFormatException nfex)
        {
            return false;
        }
    }

    private boolean isNumberCell(CellFormat cellFmt)
    {
        short valueType = cellFmt.getValueFormatType();
        switch (valueType)
        {
            case com.f1j.ss.CellFormat.eValueFormatTypeNumber:
            case com.f1j.ss.CellFormat.eValueFormatTypePercent:
            case com.f1j.ss.CellFormat.eValueFormatTypeScientific:
            case com.f1j.ss.CellFormat.eValueFormatTypeCurrency:
            case com.f1j.ss.CellFormat.eValueFormatTypeFraction:
                return true;
            default:
                return false;
        }
    }

    /**
     * 填充单元格计算结果
     * @return 真：成功；假：失败
     */
    private boolean fillCell()
    {
        Cells aCellSet = aResultXML.getCells();
        Cell aCell[] = aCellSet.getCell();
        try
        {
            for (int i = 0; i < aCellSet.getCellCount(); i++)
            {
                String location = aCell[i].getLocation();
                RangeRef range = bookModelNew.formulaToRangeRefLocal(location);
                int row = range.getRow1();
                int col = range.getCol1();
                String value = aCell[i].getText();
                bookModelNew.setText(row, col, value);
            }
            return true;
        }
        catch (F1Exception f1Ex)
        {
            f1Ex.printStackTrace();
            return false;
        }
    }

    /**
     * 保存到文件
     * @return 真：成功；假：失败
     */
    private boolean toDisplayFile()
    {
        try
        {
            bookModelNew.saveViewInfo();
            System.out.println(displayFile);
            bookModelNew.write(displayFile,
                               new com.f1j.ss.WriteParams(Constants.
                    eFileExcel97));
            return true;
        }
        catch (F1Exception f1Ex)
        {
            f1Ex.printStackTrace();
            return false;
        }
        catch (IOException ioEx)
        {
            ioEx.printStackTrace();
            return false;
        }
    }

    public static void main(String arg[])
    {
//        DisplayFile aDisplayFile = new DisplayFile(
//                "E:\\jbuilder5\\xreport\\aa_ddd_result.xml");
    }
}

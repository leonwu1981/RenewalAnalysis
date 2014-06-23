//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\ExcelWriter.java

package com.sinosoft.xreport.bl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Iterator;

import com.f1j.ss.BookModel;
import com.f1j.ss.BookModelImpl;
import com.f1j.ss.ReadParams;
import com.f1j.ss.WriteParams;
import com.sinosoft.xreport.util.StringUtility;


public class ExcelWriter extends ReportWriter
{

    public ExcelWriter()
    {

    }

    public void write(ReportData reportData) throws Exception
    {
        File dataFile = new File(reportData.getDataInfo().getDataFile());
        File xlsFile = new File(dataFile.getParentFile(),
                                dataFile.getName() + ".xls");
        if (!xlsFile.getParentFile().exists())
        {
            xlsFile.getParentFile().mkdirs();
        }

        write(reportData, new FileOutputStream(xlsFile));
    }

    public void write(ReportData reportData, OutputStream outputStream) throws
            Exception
    {
        Format format = reportData.getFormat();
        String formatFilePath = format.getFile();
        File sourceFile = new File(reportData.getDataInfo().getDefineFilePath());
        File formatFile = new File(sourceFile.getParentFile(), formatFilePath);

        BookModel bookModel = new BookModelImpl();
        bookModel.read(new FileInputStream(formatFile),
                       new ReadParams(BookModel.eFileExcel97));

        Iterator itBlockKey = reportData.getDataBlocks().keySet().iterator();
        while (itBlockKey.hasNext()) //逐块
        {
            DataBlock dataBlock = reportData.getDataBlock((String) itBlockKey.
                    next());
            Iterator itCellKey = dataBlock.getLocationMap().keySet().iterator();
            while (itCellKey.hasNext()) //逐格
            {
                String loc = (String) itCellKey.next();
                Cell cell = dataBlock.getCellByLocation(loc);

                if (cell == null)
                {
                    continue;
                }

                String cellValue = cell.getValue();
                if (cellValue.equals("0.00") && !disZero()) //不显示0
                {
                    continue;
                }

//         if (StringUtility.isNumber(cellValue))
//           bookModel.setNumber(0,StringUtility.Cell2Row(loc),
//                           StringUtility.Cell2Col(loc),Double.parseDouble(cellValue));
//         else
                bookModel.setText(0, StringUtility.Cell2Row(loc),
                                  StringUtility.Cell2Col(loc), cellValue);
            }
        }

        bookModel.write(new BufferedOutputStream(outputStream),
                        new WriteParams(BookModel.eFileExcel97));
        outputStream.flush();
        outputStream.close();

    }

    //已经初始化?
    private boolean inited;

    //显示0值?
    private boolean disZero;

    private boolean disZero() throws Exception
    {
        if (!inited)
        {
            //显示0?
            disZero = Environment.DISPLAYZERO_YES.equals(getEnvironment().
                    getEnv(Environment.DISPLAYZERO));
            inited = true;
        }

        return disZero;
    }

    public static void main(String[] args) throws Exception
    {
        File file = new File("d:/xreport_data/log/", "../日报1.xls");
        System.out.println(file.exists() + "|" + file.getAbsolutePath());
    }
}

//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\Writer.java

package com.sinosoft.xreport.bl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


public class ReportWriter
{
    //public Report theReport;

    //设置运行参数.
    protected Environment environment;

    public ReportWriter()
    {

    }

    public void write(ReportData reportData) throws Exception
    {
        File file = new File(reportData.getDataInfo().getDataFile());
        if (!file.getParentFile().exists())
        {
            file.getParentFile().mkdirs(); //建多级目录
        } //建立目录

        FileOutputStream fo = new FileOutputStream(file);

        write(reportData, fo);
    }

    public void write(ReportData reportData, OutputStream outputSteam) throws
            Exception
    {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                outputSteam));

        bw.write(reportData.toXMLString());
        bw.flush();
        bw.close();
    }

    public Environment getEnvironment()
    {
        return environment;
    }

    public void setEnvironment(Environment environment)
    {
        this.environment = environment;
    }
}

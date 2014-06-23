package com.sinosoft.xreport.dl;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */
import java.io.File;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.sinosoft.xreport.util.SysConfig;
import com.sinosoft.xreport.util.XTLogger;
import org.apache.log4j.Logger;

public class ShowReport
{

    String Define_name = null;
    String Start_date = null;
    String End_date = null;
    String Style = null;
    String Result = null;


    public ShowReport()
    {
    }

    public void showMyReport()
    {

    }


    public void setDefineName(String str)
    {
        this.Define_name = str;
    }

    public void setStartDate(String str)
    {
        this.Start_date = str;
    }

    public void setEndDate(String str)
    {
        this.End_date = str;
    }

    public void setStyle(String str)
    {
        this.Style = str;
    }

    public void setResult(String str)
    {
        this.Result = str;
    }

    public void setFile() throws Exception
    {
        Logger log = XTLogger.getLogger(ShowReport.class);
        String s = null;
        String tmp = this.Define_name;
        int i = tmp.indexOf(".xml");
        tmp = tmp.substring(0, i);
        int j = tmp.indexOf('_');
        String dir = tmp.substring(0, j);
        tmp = tmp.substring(j + 1);
        if (this.Define_name != null && this.Start_date != null &&
            this.End_date != null)
        {
            s = tmp + "_" + this.Start_date + "_" + this.End_date + ".xml";
        }
        else
        {
            log.error("信息不全，定义文件名，或者开始日期，或者结束日期不存在！");
        }
        ReadDataFile file = new ReadDataFile();
        file = file.doparse();
        file.setFile(SysConfig.FILEPATH + "data/" + dir + "/" + s);
        //file.setFile("D:\\xreport_data\\data\\000000\\86\\day1_20030401_20030220_20030220.xml");
        file.saveToFile(file.root);
    }

    public void setFile(String path) throws Exception
    {
        Logger log = XTLogger.getLogger(ShowReport.class);

        ReadDataFile file = new ReadDataFile();
        file = file.doparse();
        file.setFile(path);
        //file.setFile("D:\\xreport_data\\data\\000000\\86\\day1_20030401_20030220_20030220.xml");
        file.saveToFile(file.root);
    }


    public void transToHTML()
    {
        try
        {
            TransformerFactory trans = TransformerFactory.newInstance();
            StreamSource ss = new StreamSource(new File(this.Define_name));
            StreamSource style = new StreamSource(new File(this.Style));
            StreamResult sr = new StreamResult(new File(this.Result));

            Transformer tf = trans.newTransformer(style);
            tf.transform(ss, sr);
        }
        catch (Exception e)
        {
            XTLogger.getLogger(this.getClass()).error(e.getMessage(),
                    e.fillInStackTrace());
        }
    }


    public static void main(String[] args) throws Exception
    {
        ShowReport sr = new ShowReport();
        sr.setDefineName(
                "d:\\xreport_data\\define\\000000\\000000_day1_20030401.xml");
        sr.setStartDate("20030220");
        sr.setEndDate("20030220");
        sr.setStyle(SysConfig.FILEPATH + "style/minsheng.xslt");
        sr.setResult("c:\\testmy.html");
        sr.setFile();
        sr.transToHTML();
    }
}
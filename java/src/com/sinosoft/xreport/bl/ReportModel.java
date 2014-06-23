package com.sinosoft.xreport.bl;

import java.util.Vector;

import com.sinosoft.xreport.dl.Source;
import com.sinosoft.xreport.util.SysConfig;
import com.sinosoft.xreport.util.XWriter;

/**
 * <p>Title: XReport 1.0 (c)Sinosoft 2003</p>
 * <p>Description: 报表数据对象</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: sinosoft</p>
 * @author lixy
 * @version 1.0
 */

public abstract class ReportModel
{
    /**报表信息*/
    protected ReportMain report;
    /**报表数据源*/
    protected Vector source;
    /**报表计算参数*/
    protected BlockParams params;

    /**
     * 构造函数
     */
    public ReportModel()
    {
    }

    //////////////////////JavaBean Method////////////////////////
    public ReportMain getReport()
    {
        return report;
    }

    public void setReport(ReportMain report)
    {
        this.report = report;
    }

    public Vector getSource()
    {
        return source;
    }

    public void setSource(Vector source)
    {
        this.source = source;
    }

    public BlockParams getParams()
    {
        return params;
    }

    public void setParams(BlockParams params)
    {
        this.params = params;
    }

    ////////////////////End JavaBean/////////////////////////////

    ////////////////////User Method///////////////////////////////

    /**
     * 保存当前报表的定义(新建)
     * @return 报表定义字符串
     */
    public String newDefine()
    {
        String strFileName = "";
        String strFile = "";
        StringBuffer bufFile = new StringBuffer(strFile);
        /**拼制XML字符串*/
        bufFile.append("<?xml version=\"1.0\" encoding=\"gb2312\"?>\n");
        bufFile.append("<report_define>\n");
        bufFile.append("<info>\n");
        bufFile.append("<branch>" + report.getBranchId() + "</branch>");
        bufFile.append("<code>" + report.getReportId() + "</code>");
        bufFile.append("<name>" + report.getReportName() + "</name>");
        bufFile.append("<edition>" + report.getReportEdition() + "</edition>");
        bufFile.append("<type>" + report.getReportType() + "</type>");
        bufFile.append("<cycle>" + report.getReportCycle() + "</cycle>");
        bufFile.append("<feature>" + report.getReportAtt() + "</feature>");
        bufFile.append("<currency>" + report.getCurrency() + "</currency>");
        bufFile.append("</info>\n");
        bufFile.append("<sources>\n");
        for (int i = 0; i < source.size(); i++)
        {
            bufFile.append("<source>");
            bufFile.append(((Source) source.elementAt(i)).getDataSourceId() +
                           "");
            bufFile.append("</source>\n");
        }
        bufFile.append("</sources>\n");
        bufFile.append("</report_define>\n");
        /**拼制文件名*/
        strFileName = report.getBranchId() + SysConfig.REPORTJOINCHAR +
                      report.getReportId() + SysConfig.REPORTJOINCHAR +
                      report.getReportEdition() + ".xml";
        /**上传报表定义文件*/
        XWriter.writeDefine(bufFile.toString(), strFileName);
        return bufFile.toString();
    }

    public abstract String readDefine(ReportMain report);

    public abstract String saveDefine();

    ////////////////////End User//////////////////////////////////

}
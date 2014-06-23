package com.sinosoft.xreport.bl;

import java.util.Vector;

import com.sinosoft.xreport.dl.Source;
import com.sinosoft.xreport.util.SysConfig;
import com.sinosoft.xreport.util.XWriter;

/**
 * <p>Title: XReport 1.0 (c)Sinosoft 2003</p>
 * <p>Description: �������ݶ���</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: sinosoft</p>
 * @author lixy
 * @version 1.0
 */

public abstract class ReportModel
{
    /**������Ϣ*/
    protected ReportMain report;
    /**��������Դ*/
    protected Vector source;
    /**����������*/
    protected BlockParams params;

    /**
     * ���캯��
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
     * ���浱ǰ����Ķ���(�½�)
     * @return �������ַ���
     */
    public String newDefine()
    {
        String strFileName = "";
        String strFile = "";
        StringBuffer bufFile = new StringBuffer(strFile);
        /**ƴ��XML�ַ���*/
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
        /**ƴ���ļ���*/
        strFileName = report.getBranchId() + SysConfig.REPORTJOINCHAR +
                      report.getReportId() + SysConfig.REPORTJOINCHAR +
                      report.getReportEdition() + ".xml";
        /**�ϴ��������ļ�*/
        XWriter.writeDefine(bufFile.toString(), strFileName);
        return bufFile.toString();
    }

    public abstract String readDefine(ReportMain report);

    public abstract String saveDefine();

    ////////////////////End User//////////////////////////////////

}
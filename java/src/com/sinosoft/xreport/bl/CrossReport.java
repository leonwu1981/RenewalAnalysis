//Source file: D:\\xreport\\src\\com\\sinosoft\\xreport\\bl\\CrossReport.java

package com.sinosoft.xreport.bl;

import java.util.Collection;
import java.util.Map;

import com.sinosoft.xreport.dl.Source;
import com.sinosoft.xreport.util.SysConfig;
import com.sinosoft.xreport.util.XReader;
import com.sinosoft.xreport.util.XWriter;


public class CrossReport extends ReportModel
{
    /**����������*/
    private DefineBlock[] block;

    public CrossReport()
    {

    }

    public void setDefineBlock(DefineBlock[] block)
    {
        this.block = block;
    }

    /**
     * ���浱ǰ����Ķ���
     * @return �������ַ���
     */
    public String saveDefine()
    {
        StringBuffer bufFile = new StringBuffer();
        /**ƴ���ļ���*/
        String strFileName = report.getBranchId() + SysConfig.REPORTJOINCHAR +
                             report.getReportId() + SysConfig.REPORTJOINCHAR +
                             report.getReportEdition() + ".xml";
        try
        {
            bufFile.append("<?xml version=\"1.0\" encoding=\"gb2312\"?>\n");
            bufFile.append("<report_define>\n");
            /**�޸ı�����Ϣ*/
            bufFile.append(saveReportInfo());
            /**�޸�����Դ��Ϣ*/
            bufFile.append(saveReportSource());
            /**������������Ϣ*/
            bufFile.append(saveReportParams());
            /**�������ݿ�*/
            bufFile.append(saveReportBlock());
            bufFile.append("</report_define>\n");
            /**�ϴ��������ļ�*/
            XWriter.writeDefine(bufFile.toString(), strFileName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return bufFile.toString();
    }

    /**
     * ���汨�������Ϣ
     * @return ������Ϣ�ַ���
     */
    private String saveReportInfo()
    {
        StringBuffer bufInfo = new StringBuffer();
        bufInfo.append("<info>\n");
        bufInfo.append("<branch>" + report.getBranchId() + "</branch>\n");
        bufInfo.append("<code>" + report.getReportId() + "</code>\n");
        bufInfo.append("<name>" + report.getReportName() + "</name>\n");
        bufInfo.append("<edition>" + report.getReportEdition() + "</edition>\n");
        bufInfo.append("<type>" + report.getReportType() + "</type>\n");
        bufInfo.append("<cycle>" + report.getReportCycle() + "</cycle>\n");
        bufInfo.append("<feature>" + report.getReportAtt() + "</feature>\n");
        bufInfo.append("<currency>" + report.getCurrency() + "</currency>\n");
        bufInfo.append("</info>\n");
        return bufInfo.toString();
    }

    /**
     * ���汨������Դ
     * @return ����Դ��Ϣ�ַ���
     */
    private String saveReportSource()
    {
        StringBuffer bufSource = new StringBuffer();
        bufSource.append("<sources>\n");
        for (int i = 0; i < source.size(); i++)
        {
            bufSource.append("<source>");
            bufSource.append(((Source) source.elementAt(i)).getDataSourceId() +
                             "");
            bufSource.append("</source>\n");
        }
        bufSource.append("</sources>\n");
        return bufSource.toString();
    }

    private String saveReportParams()
    {
        return params.toXMLString();
    }

    private String saveReportBlock()
    {
        StringBuffer bufData = new StringBuffer();
        if (block == null)
        {
            return "";
        }
        for (int i = 0; i < block.length; i++)
        {
            bufData.append("<data type=\"cross\" name=\"block" + (i + 1) +
                           "\">\n");
            /**����ȫ�ֱ���*/
            Map global = block[i].getGlobal();
            bufData.append(saveReportGlobal(global));
            /**������ͷ*/
            Map row = block[i].getRows();
            bufData.append(saveReportRows(row));
            /**������ͷ*/
            Map col = block[i].getCols();
            bufData.append(saveReportCols(col));
            /**���浥Ԫ��*/
            Map cell = block[i].getCells();
            bufData.append(saveReportCells(cell));
            bufData.append("</data>\n");
        }
        return bufData.toString();
    }

    private String saveReportGlobal(Map globals)
    {
        BlockGlobal global = new BlockGlobal(globals);
        return global.toXMLString();
    }

    private String saveReportRows(Map rows)
    {
        StringBuffer bufRows = new StringBuffer();
        bufRows.append("<rows>\n");
        Collection values = rows.values();
        Object[] obj = values.toArray();
        for (int i = 0; i < obj.length; i++)
        {
            Map map = (Map) obj[i];
            RowHeader row = new RowHeader(map);
            bufRows.append(row.toXMLString());
        }
        bufRows.append("</rows>\n");
        return bufRows.toString();
    }

    private String saveReportCols(Map cols)
    {
        StringBuffer bufCols = new StringBuffer();
        bufCols.append("<cols>\n");
        Collection values = cols.values();
        Object[] obj = values.toArray();
        for (int i = 0; i < obj.length; i++)
        {
            Map map = (Map) obj[i];
            ColHeader col = new ColHeader(map);
            bufCols.append(col.toXMLString());
        }
        bufCols.append("</cols>\n");
        return bufCols.toString();
    }

    private String saveReportCells(Map cells)
    {
        StringBuffer bufCells = new StringBuffer();
        bufCells.append("<cells>\n");
        Collection values = cells.values();
        Object[] obj = values.toArray();
        for (int i = 0; i < obj.length; i++)
        {
            Map map = (Map) obj[i];
            DefineCell cell = new DefineCell(map);
            bufCells.append(cell.toXMLString());
        }
        bufCells.append("</cells>\n");
        return bufCells.toString();
    }

    /**
     * ��ȡ��������(����Դ)
     * @param tReportMain ������Ϣ
     * @return �������ַ���
     */
    public String readDefine(ReportMain tReportMain)
    {
        String file = tReportMain.getBranchId() + "_"
                      + tReportMain.getReportId() + "_"
                      + tReportMain.getReportEdition() + ".xml";
        try
        {
            DefineReader reader = new DefineReader(new StringBuffer(
                    XReader.readDefine(file)));
            /**��ȡ���ݿ�*/
            block = reader.getDefineBlocks();
            /**��ȡ����Դ*/
            source = reader.getSource();
            /**��ȡ������Ϣ*/
            report = reader.getReportMain();
            /**��ȡ������������Ϣ*/
            params = new BlockParams(reader.getParams());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }


}
package com.sinosoft.xreport.bl;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * �����Ͳ�ѯ���ȡ������.
 * @todo: ���ȡ����ʵ��
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */

public class ReportDataFactory
{

    private static ReportDataFactory rdf = new ReportDataFactory();

    private ReportDataBuilder rdBuilder = new ReportDataBuilder();

    private Map reportDataPool = new Hashtable();

    private ReportDataFactory()
    {
    }

    public static ReportDataFactory getInstance()
    {
        return rdf;
    }

    /**
     * ִ�б������ݲ�ѯ.
     * @param rql �����ѯ���.[��������].[����].[����].[����]
     * @return ��ѯ���
     * @throws Exception ��ѯ����
     */
    public String getText(String rql, Report currentReport) throws Exception
    {
        String result = null;

        try
        {
//      currentReport.getReportData().getDataInfo().bq();
        }
        catch (Exception ex)
        {

        }

        return result;
    }

    /**
     * ʹ��һ�����ݻ����,��ű�������
     * @param name ��������,������·����
     * @return �������ݶ���
     * @throws Exception ����
     */
    public ReportData getReportData(String name) throws Exception
    {
        //���������û��
        ReportData reportData = (ReportData) reportDataPool.get(name);
        if (reportData == null)
        {
            reportData = ReportDataBuilder.readReportData(name);
            reportDataPool.put(name, reportData);
        }

        return reportData;
    }

    /**
     * ʹ��һ�����ݻ����,��ű�������
     * @param name ����������Ϣ
     * @return �������ݶ���
     * @throws Exception ����
     */
    public ReportData getReportData(DataInfo dataInfo) throws Exception
    {
        String fileName = dataInfo.getDataFile();
        return getReportData(fileName);
    }


    public static void main(String[] args) throws Exception
    {
        ReportDataFactory reportDataFactory1 = ReportDataFactory.getInstance();

        ReportData r = reportDataFactory1.getReportData(
                "D:/xreport_data/data/330700/111111/js0207h_20020101_20030401_20030430.xml");
        System.out.println(r.getDataBlock("block1").getValue(
                "[̯�طֱ����].[����ס���ÿ�ƽ������]"));
    }
}


class ReportDataBuilder
{

    /**
     * ����һ������.
     * caution: �����dataInfoֻ��Ϊ��ȷ�������ļ�λ��.�������ݵ�dataInfo���ļ�����.
     * @param dataInfo ������Ϣ,�����趨branch,code,edition,startDate,endDate,calculateBranch
     * @return ��������
     * @throws Exception ��������
     */
    public static ReportData readReportData(DataInfo dataInfo) throws Exception
    {
//    DataReader dataReader=new DataReader(dataInfo.getDataFile());
        return readReportData(dataInfo.getDataFile());
    }

    /**
     * �������ļ�.
     * @param dataFile �����ļ�λ��
     * @return ��������
     * @throws Exception ��������
     */
    public static ReportData readReportData(String dataFile) throws Exception
    {
        ReportData reportData = new ReportData();
        FlyDataReader flyDataReader = new FlyDataReader(dataFile);

        //actual dataInfo
        reportData.setDataInfo(flyDataReader.getDataInfo());
        //block data
        DataBlock[] dbArr = flyDataReader.getDataBlocks();
        Map dataBlockMap = new HashMap();
        for (int i = 0; i < dbArr.length; i++)
        {
            dataBlockMap.put(dbArr[i].getName(), dbArr[i]);
        }
        reportData.setDataBlocks(dataBlockMap);

        //format
        Format format = flyDataReader.getFormat();

        reportData.setFormat(format);

        return reportData;
    }

    /**
     * create a new Report
     */
    public ReportData createReportData(String branch, String code,
                                       String edition, String startTime,
                                       String endTime) throws Exception
    {
        return new ReportData();
    }
}


class ReportDataReader
{
    public ReportData getReportData()
    {
        return null;
    }

}
package com.sinosoft.xreport.bl;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * 解析和查询表间取数的类.
 * @todo: 表间取数的实现
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
     * 执行报表数据查询.
     * @param rql 报表查询语句.[完整表名].[块名].[列名].[行名]
     * @return 查询结果
     * @throws Exception 查询错误
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
     * 使用一个数据缓存池,存放报表数据
     * @param name 报表名称,完整的路径名
     * @return 报表数据对象
     * @throws Exception 错误
     */
    public ReportData getReportData(String name) throws Exception
    {
        //缓存池中有没有
        ReportData reportData = (ReportData) reportDataPool.get(name);
        if (reportData == null)
        {
            reportData = ReportDataBuilder.readReportData(name);
            reportDataPool.put(name, reportData);
        }

        return reportData;
    }

    /**
     * 使用一个数据缓存池,存放报表数据
     * @param name 报表数据信息
     * @return 报表数据对象
     * @throws Exception 错误
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
                "[摊回分保赔款].[国寿住宿旅客平安保险]"));
    }
}


class ReportDataBuilder
{

    /**
     * 读出一个报表.
     * caution: 传入的dataInfo只是为了确定数据文件位置.报表数据的dataInfo从文件读出.
     * @param dataInfo 数据信息,必须设定branch,code,edition,startDate,endDate,calculateBranch
     * @return 数据内容
     * @throws Exception 解析错误
     */
    public static ReportData readReportData(DataInfo dataInfo) throws Exception
    {
//    DataReader dataReader=new DataReader(dataInfo.getDataFile());
        return readReportData(dataInfo.getDataFile());
    }

    /**
     * 读数据文件.
     * @param dataFile 数据文件位置
     * @return 数据内容
     * @throws Exception 解析错误
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
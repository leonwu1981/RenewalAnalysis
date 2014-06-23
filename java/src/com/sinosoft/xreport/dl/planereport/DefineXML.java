package com.sinosoft.xreport.dl.planereport;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DefineXML
{

    private String rptBranch;
    private String rptCode;
    private String rptName;
    private String rptEdition;
    private String rptOperator;
    private String rptType;
    private String rptFile;
    private DataSrc rptDataSrc;
    private GlobalConditions rptGCondion;
    private Cols cols;
    private Cells cells;

    public DefineXML()
    {}

    /**
     * 取得列头信息
     * @return 列头信息
     */
    public Cols getCols()
    {
        return cols;
    }

    /**
     * 取得数据源信息
     * @return 数据源信息
     */
    public DataSrc getDataSrc()
    {
        return rptDataSrc;
    }

    /**
     * 取得全局查询条件信息
     * @return 全局查询条件信息
     */
    public GlobalConditions getGCondition()
    {
        return rptGCondion;
    }

    /**
     * 设置数据源信息
     * @param param 数据源信息
     */
    public void setDataSrc(DataSrc param)
    {
        rptDataSrc = param;
    }

    /**
     * 设置全局查询条件信息
     * @param param 全局查询条件信息
     */
    public void setGCondition(GlobalConditions param)
    {
        rptGCondion = param;
    }

    /**
     * 设置列头信息
     * @param param 列头信息
     */
    public void setCols(Cols param)
    {
        cols = param;
    }

    /**
     * 取得单元格信息
     * @return 单元格信息
     */
    public Cells getCells()
    {
        return cells;
    }

    /**
     * 设置单元格信息
     * @param param 单元格信息
     */
    public void setCells(Cells param)
    {
        cells = param;
    }

    /**
     * 取得报表使用单位
     * @return 报表使用单位
     */
    public String getRptBranch()
    {
        return rptBranch;
    }

    /**
     * 取得报表编码
     * @return 报表编码
     */
    public String getRptCode()
    {
        return rptCode;
    }

    /**
     * 取得报表名称
     * @return 报表名称
     */
    public String getRptName()
    {
        return rptName;
    }

    /**
     * 取得报表版别
     * @return 报表版别
     */
    public String getRptEdition()
    {
        return rptEdition;
    }

    /**
     * 取得报表使用者
     * @return 报表使用者
     */
    public String getRptOperator()
    {
        return rptOperator;
    }

    /**
     * 取得报表类型
     * @return 报表类型
     */
    public String getRptType()
    {
        return rptType;
    }

    /**
     * 取得报表格式文件
     * @return 报表格式文件
     */
    public String getRptFile()
    {
        return rptFile;
    }

    /**
     * 取得报表全局查询条件
     * @return 报表全局查询条件
     */
    public GlobalConditions getRptGlobalCondition()
    {
        return rptGCondion;
    }

    /**
     * 设置报表使用单位
     * @param param 报表使用单位
     */
    public void setRptBranch(String param)
    {
        rptBranch = param;
    }

    /**
     * 设置报表编码
     * @param param 报表编码
     */
    public void setRptCode(String param)
    {
        rptCode = param;
    }

    /**
     * 设置报表名称
     * @param param 报表名称
     */
    public void setRptName(String param)
    {
        rptName = param;
    }

    /**
     * 设置报表版别
     * @param param 报表版别
     */
    public void setRptEdition(String param)
    {
        rptEdition = param;
    }

    /**
     * 设置报表使用者
     * @param param 报表使用者
     */
    public void setRptOperator(String param)
    {
        rptOperator = param;
    }

    /**
     * 设置报表类型
     * @param param 报表类型
     */
    public void setRptType(String param)
    {
        rptType = param;
    }

    /**
     * 设置报表格式文件
     * @param param 报表格式文件
     */
    public void setRptFile(String param)
    {
        rptFile = param;
    }

    /**
     * 设置报表全局查询条件
     * @param param 报表全局查询条件
     */
    public void setRptGlobalCondition(GlobalConditions param)
    {
        rptGCondion = param;
    }

    /**
     * 返回XML字符串
     * @return XML字符串
     */
    public String toXML()
    {
        String sXML = "<?xml version=\"1.0\" encoding=\"gb2312\"?>\r\n"
                      + "<<report_define>\r\n";
        sXML = sXML + "<info>\r\n"
               + "<branch>" + rptBranch + "</branch>\r\n"
               + "<code>" + rptCode + "</code>\r\n"
               + "<name>" + rptName + "</name>\r\n"
               + "<edition>" + rptEdition + "</edition>\r\n"
               + "<operator>" + rptOperator + "</operator>\r\n"
               + "</info>\r\n"
               + "<data type=\"" + rptType + "\">\r\n"
               + rptDataSrc.toXML()
               + rptGCondion.toXML()
               + cols.toXML()
               + cells.toXML()
               + "</data>\r\n<format>\r\n<file>" + rptFile +
               "</file>\r\n</format>\r\n"
               + "</report_define>\r\n";
        return sXML;
    }

    /**
     * 把当前结果内容写入文件
     * @param fName 文件名
     */
    public void toFile(String fName)
    {
        try
        {
            FileOutputStream aFileOS = new FileOutputStream(fName);
            String content = this.toXML();
            aFileOS.write(content.getBytes());
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
        catch (IOException ioEx)
        {
            ioEx.printStackTrace();
        }
    }

    /**
     * 获得主题表取数SQL
     * @return 主题表取数SQL
     */
    public String getSubjectSQL()
    {
        return rptDataSrc.getSubjectSQL();
    }

    /**
     * 获得附加数据源取数SQL
     * @return 附加数据源取数SQL
     */
    public String[] getAppendSQL()
    {
        return rptDataSrc.getAppendSQL();
    }

    /**
     * 获得主题表小计取数SQL
     * @return 主题表小计取数SQL
     */
    public String[] getSumSQL()
    {
        return rptDataSrc.getSumSQL();
    }

    /**
     * 获得主题表合计取数SQL
     * @return 主题表合计取数SQL
     */
    public String getTotalSQL()
    {
        return rptDataSrc.getTotalSQL();
    }
}
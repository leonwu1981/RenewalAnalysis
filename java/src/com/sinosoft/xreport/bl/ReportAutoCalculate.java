package com.sinosoft.xreport.bl;

//import  com.sinosoft.utility.*;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.xreport.util.SysConfig;
import com.sinosoft.xreport.util.XTLogger;

public class ReportAutoCalculate
{


    static final String DATASOURCE = "DataSource";
    static final String ENVIRONMENT = "Environment";
    static final String TABLERELATIONS = "TableRelations";
    boolean isEmpty(String in)
    {
        return null == in || "".equals(in);
    }

    public static void main(String[] args)
    {

        System.out.println("test report start");
        XTLogger xl = new XTLogger();
        org.apache.log4j.Logger log = XTLogger.getLogger(xl.getClass());

        log.debug(SysConfig.TRUEHOST + ":" + SysConfig.FILEPATH);

        try
        {

            ReportAutoCalculate rc = new ReportAutoCalculate();

            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("test report 3--------------");
            System.out.println("dataSource's");
            com.sinosoft.xreport.dl.DataSource dataSource = new com.sinosoft.
                    xreport.dl.BufferDataSourceImpl();

            System.out.println("test report 4---------------");
            Environment environment = new EnvironmentImpl();
            System.out.println("test report 5---------------");
            TableRelations tableRelations = new TableRelations(dataSource);

            String defineBranch = "86";
            String defineCode = "day3";
            String CodeName = "";
            String defineEdition = "20030401";
            String time = "2003-05-01";
            String timeend = "2003-05-01";

            String CurrentMonth = "";
            String firstDayDate = "";
            String lastDayDate = "";
            String CurrentDate = PubFun.getCurrentDate();
            time = CurrentDate;

            String calculateBranch = "86110000";
            environment.setEnv(Environment.CALCBRANCH, calculateBranch);
            if (!rc.isEmpty(time))
            {
                environment.setEnv(Environment.CALCDATE, time);
            }

            if (!rc.isEmpty(timeend))
            {
                environment.setEnv(Environment.CALCDATEEND, timeend);

            }
            Report r = new Report(defineBranch, defineCode, defineEdition);
            r.setRunEnv(environment);
            r.setDbDataSource(dataSource);
            r.setTableRelations(tableRelations);
            r.init();
            r.calculate();
            ReportWriter rw = new ReportWriter();
            ReportWriter rwExcel = new ExcelWriter();
            rw.setEnvironment(environment);
            rwExcel.setEnvironment(environment);
            r.write(rw);
            try
            {
                r.write(rwExcel);
            }
            catch (Exception ex)
            {
                log.error("写入Excel文件时出错.请检查格式文件是否存在.exmsg:" + ex.getMessage());
            }
            CalculateLog.save();

        }
        catch (Exception ex)
        {
            System.out.println("err|计算失败.查看日志文件.获取更多信息.errmsg:");
            System.exit(0);
        }
        System.out.print("OK");
        System.exit(0);

    }
}

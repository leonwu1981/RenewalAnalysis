//程序名称：ReportCalculate
//程序功能：在服务器端计算报表
//创建日期：2003-7-9
//创建人  ：GUOXIANG
//更新记录：  更新人    更新日期     更新原因/内容
package com.sinosoft.xreport.bl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

//import  com.sinosoft.utility.*;
import com.sinosoft.lis.pubfun.FDate;
import com.sinosoft.lis.pubfun.GlobalInput;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.xreport.util.SysConfig;
import com.sinosoft.xreport.util.XTLogger;

public class ReportCalculate
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

        javax.naming.NameNotFoundException ex1;
        long testtime = Calendar.getInstance().getTime().getTime();

        XTLogger xl = new XTLogger();
        org.apache.log4j.Logger log = XTLogger.getLogger(xl.getClass());

        log.debug(SysConfig.TRUEHOST + ":" + SysConfig.FILEPATH);

        try
        {

            ReportCalculate rc = new ReportCalculate();
            Class.forName("oracle.jdbc.driver.OracleDriver");

            com.sinosoft.xreport.dl.DataSource dataSource = new com.sinosoft.
                    xreport.dl.BufferDataSourceImpl();
            Environment environment = new EnvironmentImpl();
            TableRelations tableRelations = new TableRelations(dataSource);

            ReportMain reportMain = new ReportMain();
            Vector vRM = reportMain.query();
            Vector vecBranchId = new Vector();
            Vector vecReportName = new Vector();
            Vector vecReportId = new Vector();
            Vector vecReportEdition = new Vector();

            for (int i = 0; i < vRM.size(); i++)
            {
                String defineBranch = "86";
                String defineCode = "";
                String CodeName = "";
                String defineEdition = "20030401";
                String time = "";
                String timeend = "";

                String CurrentMonth = "";
                String firstDayDate = "";
                String lastDayDate = "";

                ReportMain report = (ReportMain) vRM.elementAt(i);
                defineCode = report.getReportId();
                CodeName = report.getReportName();

                //日报

                if ((defineCode.equals("day1")) ||
                    (defineCode.equals("day2")) ||
                    (defineCode.equals("agent")) ||
                    (defineCode.equals("dayForPerson")) ||
                    (defineCode.equals("dayForPersonItem")))
                {

                    String CurrentDate = PubFun.getCurrentDate();
                    time = CurrentDate;
                    GlobalInput tG = new GlobalInput();
                    String calculateBranch = tG.ManageCom;
                    calculateBranch = "86"; //默认值
                    environment.setEnv(Environment.CALCBRANCH, calculateBranch);

                    if (!rc.isEmpty(time))
                    {
                        environment.setEnv(Environment.CALCDATE, time);
                    }

                    if (!rc.isEmpty(timeend))
                    {
                        environment.setEnv(Environment.CALCDATEEND, timeend);

                    }
                    Report r = new Report(defineBranch, defineCode,
                                          defineEdition);
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
                        ex.printStackTrace();
                        log.error("写入Excel文件时出错00000.请检查格式文件是否存在.exmsg:" +
                                  ex.getMessage());
                    }
                    CalculateLog.save();

                }

                //月报

                if ((defineCode.equals("month1"))
                    || (defineCode.equals("month2"))
                    || (defineCode.equals("month301"))
                    || (defineCode.equals("month302"))
                    || (defineCode.equals("month303")))
                {
                    String CurrentDate = PubFun.getCurrentDate();
                    FDate fDate = new FDate();
                    Date CurDate = fDate.getDate(CurrentDate);
                    GregorianCalendar mCalendar = new GregorianCalendar();
                    mCalendar.setTime(CurDate);
                    int Months = mCalendar.get(Calendar.MONTH) + 1; //因为从0开始的
                    int Years = mCalendar.get(Calendar.YEAR);

                    if (Months < 10)
                    {
                        firstDayDate = Years + "-0" + Months + "-01";

                    }
                    else
                    {
                        firstDayDate = Years + "-" + Months + "-01";

                    }
                    time = firstDayDate;
                    String calculateBranch = "86";
                    environment.setEnv(Environment.CALCBRANCH, calculateBranch);

                    //计算时间.
                    if (!rc.isEmpty(time))
                    {
                        environment.setEnv(Environment.CALCDATE, time);
                    }

                    if (!rc.isEmpty(timeend))
                    {
                        environment.setEnv(Environment.CALCDATEEND, timeend);

                    }

                    Report r = new Report(defineBranch, defineCode,
                                          defineEdition);
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
                        log.error("写入Excel文件时出错.请检查格式文件是否存在.exmsg:" +
                                  ex.getMessage());
                    }
                    CalculateLog.save();
                    //log.println(r.getReportData().getDataInfo().getDefineFilePath());

                }

                if (defineCode.equals("month4"))

                {

                    String CurrentDate = PubFun.getCurrentDate();
                    FDate fDate = new FDate();
                    Date CurDate = fDate.getDate(CurrentDate);
                    GregorianCalendar mCalendar = new GregorianCalendar();
                    mCalendar.setTime(CurDate);
                    int Months = mCalendar.get(Calendar.MONTH) + 1; //因为从0开始的
                    int Years = mCalendar.get(Calendar.YEAR);

                    if (Months < 10)
                    {
                        firstDayDate = Years + "-0" + Months + "-01";

                    }
                    else
                    {
                        firstDayDate = Years + "-" + Months + "-01";

                    }
                    time = firstDayDate;
                    String calculateBranch = "86";
                    environment.setEnv(Environment.CALCBRANCH, calculateBranch);

                    //计算时间.
                    if (!rc.isEmpty(time))
                    {
                        environment.setEnv(Environment.CALCDATE, time);
                    }

                    if (!rc.isEmpty(timeend))
                    {
                        environment.setEnv(Environment.CALCDATEEND, timeend);

                    }

                    Report r = new Report(defineBranch, defineCode,
                                          defineEdition + "_gens");
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
                        log.error("写入Excel文件时出错.请检查格式文件是否存在.exmsg:" +
                                  ex.getMessage());
                    }
                    CalculateLog.save();
                    //log.println(r.getReportData().getDataInfo().getDefineFilePath());

                }

                //季报
                if (defineCode.equals("rzquarter"))

                {
                    String CurrentDate = PubFun.getCurrentDate();
                    FDate fDate = new FDate();
                    Date CurDate = fDate.getDate(CurrentDate);
                    GregorianCalendar mCalendar = new GregorianCalendar();
                    mCalendar.setTime(CurDate);
                    int Months = mCalendar.get(Calendar.MONTH) + 1; //因为从0开始的
                    int Years = mCalendar.get(Calendar.YEAR);
                    int Quarter = (Months - 1) / 3 + 1; //计算当前季度
                    int endmonth = Quarter * 3;
                    int firstmonth = endmonth - 2;
                    String firstday = "";
                    String lastday = "";
                    firstday = Years + "-" + firstmonth + "-01";
                    if ((Quarter == 1) || (Quarter == 4))
                    {
                        lastday = Years + "-" + firstmonth + "-31";
                    }
                    else
                    {
                        lastday = Years + "-" + firstmonth + "-30";
                    }
                    time = firstday;
                    timeend = lastday;
                    String calculateBranch = "86";
                    environment.setEnv(Environment.CALCBRANCH, calculateBranch);

                    //计算时间.
                    if (!rc.isEmpty(time))
                    {
                        environment.setEnv(Environment.CALCDATE, time);
                    }
                    if (!rc.isEmpty(timeend))
                    {
                        environment.setEnv(Environment.CALCDATEEND, timeend);
                    }
                    Report r = new Report(defineBranch, defineCode,
                                          defineEdition);
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
                        log.error("写入Excel文件时出错.请检查格式文件是否存在.exmsg:" +
                                  ex.getMessage());
                    }
                    CalculateLog.save();

                }

            }
            testtime = Calendar.getInstance().getTime().getTime() - testtime;
            log.info("..total time.." + testtime);
            log.info("ok|计算成功.");
            System.exit(0);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            log.error("写入Excel文件时出错11111.请检查格式文件是否存在.exmsg:" + ex.getMessage());

        }
        System.out.print("OK");
    }
}

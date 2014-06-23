//程序名称：ReportCalculate
//程序功能：在服务器端计算报表
//创建日期：2003-7-9
//创建人  ：GUOXIANG
//更新记录：  更新人    更新日期     更新原因/内容
//          guoxiang  2003-12-17   仅仅为日报，编码为day3
//          guoxiang  2003-7-23    为保监会报表5张报表和n家机构
package com.sinosoft.xreport.bl;

import com.sinosoft.lis.db.LDComDB;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.lis.schema.LDComSchema;
import com.sinosoft.lis.vschema.LDComSet;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.xreport.dl.BufferDataSourceImpl;
import com.sinosoft.xreport.dl.DataSource;
import com.sinosoft.xreport.util.SysConfig;
import com.sinosoft.xreport.util.XTLogger;

public class AutoReportMonth
{
    /**
     传输数据的公共方法
     */
    public boolean submitData()
    {
//   System.out.println("1111111111"+tSysdate);
        boolean tReturn = false;
        XTLogger xl = new XTLogger();
        org.apache.log4j.Logger log = XTLogger.getLogger(xl.getClass());
        log.debug(SysConfig.TRUEHOST + ":" + SysConfig.FILEPATH);
        String defineBranch = "86";
        String defineEdition = "20030401";
        String operatorID = "001";
        String CodeName = "";
        String defineCode = "";
        String calculateBranch = "";
        String time = "";
//    String timeend = "";

        String SQL = "select add_months(sysdate,-1) from dual";
        ExeSQL texe = new ExeSQL();
        time = texe.getOneValue(SQL).substring(0, 7) + "-01";
        System.out.println("-----------time" + time);

//    String sysdate = PubFun.getCurrentDate();

        //报表类型数组
        String[] DefineCode1 =
                {
//     "AgentNewMonth2",
//     "AgentPolMonth2",
//     "AgentPeopleMonth1",
//     "AgentPeopleMonth3"
//     ,


                "AgentPeopleMonth2",
                "AgentPeopleMonth4",
                "AgentNewMonth1",
                "AgentPolMonth1"
        };
        //佣金部分
        String[] DefineCode2 =
                {
                "wagemonth1",
                "wagemonth2"
//     ,
//     "wagemonth3"
        };

        String[] DefineCode =
                              {};
        //机构类型
        String sql =
//       "select * from ldcom where comcode='86' or comcode='8611' or comcode='861100'";

                "select * from ldcom where trim(comcode)!='8699' and length(trim(comcode))<>'8' order by comcode";
        LDComDB tLDComDB = new LDComDB();
        LDComSet tLDComSet = tLDComDB.executeQuery(sql);

        if (PubFun.getCurrentDate().substring(8).equals("04"))
        {
            System.out.println("111111111111111");
            DefineCode = DefineCode1;
            System.out.println("数组" + DefineCode.length);
        }
        else if (PubFun.getCurrentDate().substring(8).equals("31"))
        {
            DefineCode = DefineCode2;
        }

        for (int i = 1; i <= tLDComSet.size(); i++)
        {
            LDComSchema tLDComSchema = tLDComSet.get(i);
            for (int j = 0; j < DefineCode.length; j++)
            {
                try
                {
                    defineCode = DefineCode[j];

                    calculateBranch = tLDComSchema.getComCode().trim();

                    Class.forName("oracle.jdbc.driver.OracleDriver");

                    Environment environment = new EnvironmentImpl();
                    environment.setEnv(Environment.OPERATORID, operatorID);
                    environment.setEnv(Environment.CALCBRANCH, calculateBranch);
                    environment.setEnv(Environment.CALCDATE, time);

                    Report r = new Report(defineBranch, defineCode,
                                          defineEdition);
                    r.setRunEnv(environment);

                    System.out.println("4");

                    //改用BufferDataSource实现
                    DataSource dataSource = new BufferDataSourceImpl();
                    r.setDbDataSource(dataSource);

                    TableRelations tableRelations = new TableRelations(
                            dataSource);
                    r.setTableRelations(tableRelations);

                    System.out.println("5");
                    r.init();

                    System.out.println("c");

                    r.calculate(); //it's high cost!
                    System.out.println("d");

                    ReportWriter rw = new ReportWriter();
                    System.out.println("e");
                    ReportWriter rwExcel = new ExcelWriter(); //写到excel,当然可以写到xml,html...
                    System.out.println("f");
                    rw.setEnvironment(environment);
                    System.out.println("g");
                    rwExcel.setEnvironment(environment);
                    System.out.println("h");

                    r.write(rw); //写到xml

                    System.out.println("5");

                    try
                    {
                        r.write(rwExcel);
                        System.out.println("6");
                    }
                    catch (Exception ex)
                    {
                        log.error("写入Excel文件时出错,请检查格式文件是否存在.exmsg:" +
                                  ex.getMessage());
                        System.out.println("7");
                    }

                    // CalculateLog.save();取上期采用

                    System.out.println("ok|计算成功.");
                }
                catch (Exception ex)
                {
                    System.out.println("err|计算失败.查看日志文件.获取更多信息.errmsg:");
                    ex.printStackTrace();
                    System.exit(0);
                }
                System.out.println(calculateBranch + defineCode + "计算成功");
            }
        }
        System.out.println("计算over");

        return tReturn;
    }


    public static void main(String[] args)
    {
    }
}
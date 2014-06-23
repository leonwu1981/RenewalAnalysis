/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.report.f1report;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.sinosoft.utility.DBConn;
import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.SSRS;


/**
 * <p>Title:F1报表信息写数据库类 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Sinosoft 2004 </p>
 * <p>Company: Sinosoft Co.,Ltd. </p>
 * @author lwt
 * @version 1.0
 */
public class CReportLog
{
    public String m_ErrString = " ";
    public CReportLog()
    {
    }


    /**
     * 根据报表表示读取历史文件
     * @param cReportVarName 报表名
     * @return 字符串
     */
    public String GetHistoryFile(String cReportVarName)
    {

        String tSQL;
        String t_return = "";

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar now = Calendar.getInstance();
        String t_now = formatter.format(now.getTime());

        tSQL = "select SSReportLog.filename from SSReportLog  where SSReportLog.ReportVarName='" +
               cReportVarName + "' and SSReportLog.TimeOutDate >= '" + t_now +
               "'";

        SSRS tRs = new SSRS();
        ExeSQL tExeSQL = new ExeSQL();

        //System.out.println("tSQL: "+tSQL);
        tRs = tExeSQL.execSQL(tSQL);

        for (int rowcount = 1; rowcount <= tRs.MaxRow; rowcount++)
        {
            t_return = ChgValue(tRs.GetText(rowcount, 1));
        }
        return t_return;
    }


    /**
     * 写历史文件
     * @param cReportFileName 报表文件名
     * @param cReportVarName 报表变量名
     * @param cTimeOutDate 超时日期
     * @throws Exception
     */
    public void WriteHistoryFile(String cReportFileName, String cReportVarName,
                                 java.util.Date cTimeOutDate) throws Exception
    {

        String tSQL;
        DBConn con = null;
        java.sql.Statement st = null;

        tSQL = "delete from ssreportlog where reportvarname='" + cReportVarName +
               "'";

        // 建立数据库连接
        //boolean getcon = con.createConnection();
        con = DBConnPool.getConnection();
        if (con == null)
        {
            System.out.println("Connection建立失败!"); // @@错误处理
        }

        try
        {
            st = con.createStatement();

            System.out.println("tSQL: " + tSQL);
            st.execute(tSQL);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Calendar now = Calendar.getInstance();
            String t_now = formatter.format(now.getTime());
            String t_cTimeOutDate = formatter.format(cTimeOutDate);

            tSQL = "insert into ssreportlog  values('" + cReportVarName + "','" +
                   cReportFileName +
                   "', '" + t_now + "' , '" + t_cTimeOutDate + "' )";

            System.out.println("tSQL: " + tSQL);
            st.execute(tSQL);
            con.commit();
        }
        catch (SQLException ex)
        {
            con.rollback();
            System.out.println("数据库操作失败!" + ex);
            m_ErrString = "写历史文件,数据库操作失败!";
        }
        finally
        {
            st.close();
            con.close();
        }
    }


    /**
     * 将以字段返回的结果进行处理，主要是Null的处理
     * @param fd 传人的字符串
     * @return 字符串
     */
    public String ChgValue(String fd)
    {
        if (fd == null)
        {
            return "";
        }

        return fd.trim();
    }


    public static void main(String[] args)
    {
        CReportLog CReportLog1 = new CReportLog();
    }
}

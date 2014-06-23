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
 * <p>Title:F1������Ϣд���ݿ��� </p>
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
     * ���ݱ����ʾ��ȡ��ʷ�ļ�
     * @param cReportVarName ������
     * @return �ַ���
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
     * д��ʷ�ļ�
     * @param cReportFileName �����ļ���
     * @param cReportVarName ���������
     * @param cTimeOutDate ��ʱ����
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

        // �������ݿ�����
        //boolean getcon = con.createConnection();
        con = DBConnPool.getConnection();
        if (con == null)
        {
            System.out.println("Connection����ʧ��!"); // @@������
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
            System.out.println("���ݿ����ʧ��!" + ex);
            m_ErrString = "д��ʷ�ļ�,���ݿ����ʧ��!";
        }
        finally
        {
            st.close();
            con.close();
        }
    }


    /**
     * �����ֶη��صĽ�����д�����Ҫ��Null�Ĵ���
     * @param fd ���˵��ַ���
     * @return �ַ���
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

/**
 * Copyright (c) 2006 sinosoft Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.audit;

import com.sinosoft.utility.DBConnPool;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import com.sinosoft.utility.CError;
import java.sql.PreparedStatement;
import com.sinosoft.utility.StrTool;
import java.sql.Connection;
import java.sql.Types;
import com.sinosoft.lis.pubfun.FDate;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.utility.CErrors;
import com.sinosoft.utility.VData;



/**
 * <p>
 * ClassName: PolChkExecSql
 * </p>
 * <p>
 * Company: Sinosoft Co. Ltd.
 * </p>
 * @author not attributable</p>
 * @version 1.0</p>
 */
public class PolChkExecSql {

    private Connection con;
    private boolean mflag = false;
    private FDate fDate = new FDate();
    public CErrors mErrors = new CErrors(); 

	/**
	 * PolChkExecSql
	 */
    public PolChkExecSql() {
    }

	/**
	 * execSQL
	 * @param String sql
	 */
    public VData execSQL(String sql)
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        VData tSSRS = null;

        System.out.println("ExecSQL : " + sql.trim());
        if (mflag == false)
        {
            con = DBConnPool.getConnection();
        }

        try
        {
            pstmt = con.prepareStatement(StrTool.GBKToUnicode(sql),
                                         ResultSet.TYPE_FORWARD_ONLY,
                                         ResultSet.CONCUR_READ_ONLY);
            rs = pstmt.executeQuery();
            rsmd = rs.getMetaData();

            int n = rsmd.getColumnCount();
            tSSRS = new VData();


                for (int j = 1; j <= n; j++)
                {
                    String strField = rsmd.getColumnName(j);
                    tSSRS.addElement(strField);
                }

            rs.close();
            pstmt.close();

            if (mflag == false)
            {
                con.close();
            }
        }
        catch (Exception e)
        {
            System.out.println("####ExeSQL at exeSQL1: " + sql);
            e.printStackTrace();

            CError.buildErr(this, e.toString(), mErrors);

            tSSRS = null;

            try
            {
                if(rs!=null)
                {
                    rs.close();
                }
                if(pstmt!=null)
                {
                    pstmt.close();
                }

                if (mflag == false)
                {
                    con.close();
                }
            }
            catch (Exception ex)
            {
            }
        }
        return tSSRS;
    }


}

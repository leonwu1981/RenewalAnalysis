package com.sinosoft.lis.pubfun;

import com.sinosoft.utility.*;

public class GetTotalSum
{
    public GetTotalSum()
    {
    }

    public String getTotalPay(String serialno)
    {
        ExeSQL exeSql = new ExeSQL();
        SSRS testSSRS = new SSRS();

        String sql =
            "select Totalmoney, Totalnum from lybanklog where SerialNo = "
            + serialno;
        testSSRS = exeSql.execSQL(sql);
        return testSSRS.GetText(0, 0);
    }

    public String getTotalPic(String serialno)
    {
        ExeSQL exeSql = new ExeSQL();
        SSRS testSSRS = new SSRS();

        String sql =
            "select Totalmoney, Totalnum from lybanklog where SerialNo = "
            + serialno;
        testSSRS = exeSql.execSQL(sql);

        return testSSRS.GetText(0, 1);
    }
}

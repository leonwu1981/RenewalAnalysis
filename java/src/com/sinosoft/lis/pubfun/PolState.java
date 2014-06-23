/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import com.sinosoft.utility.*;


/**
 * <p>Title: Web业务系统</p>
 * <p>Description:业务系统的公共业务处理函数
 * 该类包含所有业务中有关保单状态的函数。 </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Sinosoft</p>
 * @author Fanym
 * @version 1.0
 */
public class PolState
{
    public CErrors mErrors = new CErrors();
//    private VData mResult = new VData();

    public PolState()
    {
    }

    public boolean SetPolEnd()
    {
        /*
              置保单为“终止”状态（状态已处于“终止”或“暂停”的除外）：
              1. 对于可以自动续保且选择自动续保的，在宽限期67天的次日终止
              2. 对于可以自动续保但没有选择自动续保的，在保险责任终止次日终止
              3. 对于不可以自动续保的，在保险责任终止次日终止
              4. 对于状态处于“无效”的，在“交至日期+2年”后终止
         */
        String sql = "UPDATE lcpol" +
                     " SET polstate = '0303' || SUBSTR (polstate, 0, 4)" +
                     " WHERE polno IN (" + " SELECT polno" + " FROM lcpol" +
                     " WHERE grppolno = '00000000000000000000'" +
                     "  AND SUBSTR (polstate, 0, 2) NOT IN ('03', '04')" +
                     "  AND (   (    EXISTS (SELECT riskcode FROM lmriskrnew" +
                     "                         WHERE riskcode = lcpol.riskcode)" +
                     "           AND SYSDATE > enddate + DECODE (rnewflag, '-1', 67, 0))" +
                     "        OR (    NOT EXISTS (SELECT riskcode FROM lmriskrnew" +
                     "                             WHERE riskcode = lcpol.riskcode)" +
                     "            AND SYSDATE > enddate)" +
                     "        OR (    polstate LIKE '02%'" +
                     "            AND SYSDATE > ADD_MONTHS (PayToDate, 24))" +
                     "       ))";
        System.out.println(sql);

        ExeSQL tExeSQL = new ExeSQL();

        if (!tExeSQL.execUpdateSQL(sql))
        {
            CError.buildErr(this, tExeSQL.mErrors.getFirstError(), mErrors);

            return false;
        }

        return true;
    }

    public static void main(String[] args)
    {
//        PolState tPolState = new PolState();
//        if (!tPolState.SetPolEnd())
//        {
//            System.out.println(tPolState.mErrors.getFirstError());
//        }
    }
}

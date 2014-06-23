/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import com.sinosoft.utility.*;


/**
 * <p>Title: Webҵ��ϵͳ</p>
 * <p>Description:ҵ��ϵͳ�Ĺ���ҵ������
 * �����������ҵ�����йر���״̬�ĺ����� </p>
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
              �ñ���Ϊ����ֹ��״̬��״̬�Ѵ��ڡ���ֹ������ͣ���ĳ��⣩��
              1. ���ڿ����Զ�������ѡ���Զ������ģ��ڿ�����67��Ĵ�����ֹ
              2. ���ڿ����Զ�������û��ѡ���Զ������ģ��ڱ���������ֹ������ֹ
              3. ���ڲ������Զ������ģ��ڱ���������ֹ������ֹ
              4. ����״̬���ڡ���Ч���ģ��ڡ���������+2�ꡱ����ֹ
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

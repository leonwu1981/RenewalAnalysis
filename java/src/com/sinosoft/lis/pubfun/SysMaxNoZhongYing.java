/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;


/**
 * <p>Title: Webҵ��ϵͳ</p>
 * <p>Description:ϵͳ��������������ٺ���ҵ��ϵͳ������ϵͳ���� </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Sinosoft</p>
 * @author Liuqiang
 * @version 1.0
 */

import java.sql.Connection;

import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.*;

public class SysMaxNoZhongYing implements com.sinosoft.lis.pubfun.SysMaxNo
{

    public SysMaxNoZhongYing()
    {
    }

    /**
     *<p>������ˮ�ŵĺ���<p>
     *<p>������򣺻�������  ������  У��λ   ����    ��ˮ��<p>
     *<p>          1-6     7-10   11     12-13   14-20<p>
     * @param cNoType Ϊ��Ҫ���ɺ��������
     * @param cNoLimit Ϊ��Ҫ���ɺ��������������Ҫô��SN��Ҫô�ǻ������룩
     * @param cVData Ϊ��Ҫ���ɺ����ҵ�������������
     * @return ���ɵķ�����������ˮ�ţ��������ʧ�ܣ����ؿ��ַ���""
     */
    public String CreateMaxNo(String cNoType, String cNoLimit, VData cVData)
    {
        //��ӢĿǰδ����
        return CreateMaxNo(cNoType, cNoLimit);
    }
    
    /**
     *<p>������ˮ�ŵĺ���<p>
     *<p>������򣺻�������  ������  У��λ   ����    ��ˮ��<p>
     *<p>          1-6     7-10   11     12-13   14-20<p>
     * @param cNoType Ϊ��Ҫ���ɺ��������
     * @param cNoLimit Ϊ��Ҫ���ɺ��������������Ҫô��SN��Ҫô�ǻ������룩
     * @return ���ɵķ�����������ˮ�ţ��������ʧ�ܣ����ؿ��ַ���""
     */
    public String CreateMaxNo(String cNoType, String cNoLimit)
    {
        //����Ĳ�������Ϊ�գ����Ϊ�գ���ֱ�ӷ���
        if ((cNoType == null) || (cNoType.trim().length() <= 0) ||
                (cNoLimit == null))
        {
            System.out.println("NoType���ȴ������NoLimitΪ��");

            return null;
        }

        //Ĭ����ˮ��λ��
        int serialLen = 10;
        String tReturn = null;
        cNoType = cNoType.toUpperCase();
        //System.out.println("-----------cNoType:"+cNoType+"  cNoLimit:"+cNoLimit);
      
        //cNoLimit�����SN���ͣ���cNoTypeֻ�������������е�һ��
        if (cNoLimit.trim().toUpperCase().equals("SN"))
        { //modify by yt 2002-11-04
            // 		if (cNoType.equals("GRPNO") || cNoType.equals("CUSTOMERNO") || cNoType.equals("SugDataItemCode") || cNoType.equals("SugItemCode") || cNoType.equals("SugModelCode") || cNoType.equals("SugCode"))
            if (cNoType.equals("COMMISIONSN") ||
                    cNoType.equals("GRPNO") || cNoType.equals("CUSTOMERNO") ||
                    cNoType.equals("SUGDATAITEMCODE") ||
                    cNoType.equals("SUGITEMCODE") ||
                    cNoType.equals("SUGMODELCODE") ||
                    cNoType.equals("SUGCODE"))
            {
                serialLen = 9;
            }
            else
            {
                System.out.println("�����NoLimit");

                return null;
            }
        }

        //���������������ڲ�����,�����cNoLimit����Ϊ�ϼ��������ڲ�����
        if (cNoType.equals("ORGCOMNO")||cNoType.equals("SENDCOMNO"))
        {
            serialLen = 4;
        }
        //������6λ��ˮ��
        if (cNoType.equals("AIRPOLNO"))
        { //modify by yt 2002-11-04
            serialLen = 6;
        }
        //����������ˮ�ų��ȴ���
        if (cNoType.equals("AGENTCODE") || cNoType.equals("MANAGECOM"))
        {
            serialLen = 4;
        }

        tReturn = cNoLimit.trim();
        //System.out.println("tReturn:"+tReturn);

        String tCom = ""; //��λ����
        if (tReturn.length() >= 4&&!cNoType.equals("ORGCOMNO")&&!cNoType.equals("SENDCOMNO"))
        {
            tCom = tReturn.substring(2, 4);
            tCom = "0" + tCom; //��һλ����λ
            tReturn = tReturn.substring(0, 4);
        }

        //���ɸ��ֺ���
        //Ͷ�������ֺ���
        if (cNoType.equals("PROPOSALNO"))
        {
            tReturn = "11" + tCom;
        }

        //����Ͷ�������ֺ���
        else if (cNoType.equals("GRPPROPOSALNO"))
        {
            tReturn = "12" + tCom;
        }

        //�ܵ�Ͷ��������
        else if (cNoType.equals("PROPOSALCONTNO"))
        {
            tReturn = "13" + tCom;
        }

        //����Ͷ��������ProposalGrpContNo,LDMaxNo����󳤶�Ϊ15��������ProGrpContNo����
        else if (cNoType.equals("PROGRPCONTNO"))
        {
            tReturn = "14" + tCom;
        }

        //�������ֺ���
        else if (cNoType.equals("POLNO"))
        {
            tReturn = "21" + tCom;
        }

        //���屣�����ֺ���
        else if (cNoType.equals("GRPPOLNO"))
        {
            tReturn = "22" + tCom;
        }

        //��ͬ����
        else if (cNoType.equals("CONTNO"))
        {
            tReturn = "23" + tCom;
        }

        //�����ͬ����
        else if (cNoType.equals("GRPCONTNO"))
        {
            tReturn = "24" + tCom;
        }

        //����֪ͨ�����
        else if (cNoType.equals("PAYNOTICENO"))
        {
            tReturn = "31" + tCom;
        }

        //�����վݺ���
        else if (cNoType.equals("PAYNO"))
        {
            tReturn = "32" + tCom;
        }

        //����֪ͨ�����
        else if (cNoType.equals("GETNOTICENO"))
        {
            tReturn = "36" + tCom;
        }

        //����֪ͨ�����
        else if (cNoType.equals("GETNO"))
        {
            tReturn = "37" + tCom;
        }

        //�����������
        else if (cNoType.equals("EDORAPPNO"))
        {
            tReturn = "41" + tCom;
        }

        //��������
        else if (cNoType.equals("EDORNO"))
        {
            tReturn = "42" + tCom;
        }

        //���������������
        else if (cNoType.equals("EDORGRPAPPNO"))
        {
            tReturn = "43" + tCom;
        }

        //������������
        else if (cNoType.equals("EDORGRPNO"))
        {
            tReturn = "44" + tCom;
        }

        //�������
        else if (cNoType.equals("RPTNO"))
        {
            tReturn = "50" + tCom;
        }

        //�������
        else if (cNoType.equals("RGTNO"))
        {
            tReturn = "51" + tCom;
        }

        //�ⰸ���
        else if (cNoType.equals("CLMNO"))
        {
            tReturn = "52" + tCom;
        }

        //�ܰ����
        else if (cNoType.equals("DECLINENO"))
        {
            tReturn = "53" + tCom;
        }

        //�����ְ����
        else if (cNoType.equals("SUBRPTNO"))
        {
            tReturn = "54" + tCom;
        }

        //�����ְ����
        else if (cNoType.equals("CASENO"))
        {
            tReturn = "55" + tCom;
        }

        //��ͬ��
        else if (cNoType.equals("PROTOCOLNO"))
        {
            tReturn = "71" + tCom;
        }

        //��֤ӡˢ����
        else if (cNoType.equals("PRTNO"))
        {
            tReturn = "80" + tCom;
        }

        //��ӡ������ˮ��
        else if (cNoType.equals("PRTSEQNO"))
        {
            tReturn = "81" + tCom;
        }

        //��ӡ������ˮ��
        else if (cNoType.equals("PRTSEQ2NO"))
        {
            tReturn = "82" + tCom;
        }

        //�������㵥��
        else if (cNoType.equals("TAKEBACKNO"))
        {
            tReturn = "61" + tCom;
        }

        //���д��۴������κ�
        else if (cNoType.equals("BATCHNO"))
        {
            tReturn = "62" + tCom;
        }

        //�ӿ�ƾ֤id��
        else if (cNoType.equals("VOUCHERIDNO"))
        {
            tReturn = "63" + tCom;
        }

        //Ӷ�����
        else if (cNoType.equals("WAGENO"))
        {
            tReturn = "90" + tCom;
        }

        //��ˮ��
        else if (cNoType.equals("SERIALNO"))
        {
            tReturn = "98" + tCom;
        }

        if (tReturn.length() == 10)
        {
            tReturn += "99";
        }

        //����
        Connection conn = DBConnPool.getConnection();

        if (conn == null)
        {
            System.out.println("CreateMaxNo : fail to get db connection");
            return tReturn;
        }

        int tMaxNo = 0;
        cNoLimit = tReturn;
        //System.out.println("cNoLimit:"+cNoLimit);

        try
        {
            //��ʼ����
            //��ѯ�����3���� -- added by Fanym
            //ȫ������ֱ��ִ��SQL��䣬ֻҪ���������������˱��У���������NULL
            //���û����������������������ѯ�õ������UPDATE��û����INSERT
            conn.setAutoCommit(false);

//            String strSQL = "select MaxNo from LDMaxNo where notype='" +
//                    cNoType + "' and nolimit='" + cNoLimit +
//                    "' for update";
//            //������ݿ�������ORACLE�Ļ�����Ҫ���nowait���ԣ��Է�ֹ���ȴ�
//            if (SysConst.DBTYPE.compareTo("ORACLE") == 0)
//            {
//                strSQL = strSQL + " nowait";
//            }
            StringBuffer tSBql = new StringBuffer(128);
            tSBql.append("select MaxNo from LDMaxNo where notype='");
            tSBql.append(cNoType);
            tSBql.append("' and nolimit='");
            tSBql.append(cNoLimit);
            tSBql.append("'");
            //������ݿ�������ORACLE�Ļ�����Ҫ���nowait���ԣ��Է�ֹ���ȴ�
            if (SysConst.DBTYPE.compareTo("ORACLE") == 0)
            {
                tSBql.append(" for update nowait");
            }
            else
            {
                tSBql.append(" WITH RS");
//                tSBql.append(" for update");
            }

            ExeSQL exeSQL = new ExeSQL(conn);
            String result = null;
            result = exeSQL.getOneValue(tSBql.toString());

            //���Է���bullʱ
            if (exeSQL.mErrors.needDealError())
            {
                System.out.println("��ѯLDMaxNo�������Ժ�!");
                conn.rollback();
                conn.close();

                return null;
            }

            if ((result == null) || result.equals(""))
            {
//                strSQL = "insert into ldmaxno(notype, nolimit, maxno) values('" +
//                        cNoType + "', '" + cNoLimit + "', 1)";
                tSBql = new StringBuffer(128);
                tSBql.append("insert into ldmaxno(notype, nolimit, maxno) values('");
                tSBql.append(cNoType);
                tSBql.append("', '");
                tSBql.append(cNoLimit);
                tSBql.append("', 1)");

                exeSQL = new ExeSQL(conn);
                if (!exeSQL.execUpdateSQL(tSBql.toString()))
                {
                    System.out.println("CreateMaxNo ����ʧ�ܣ�������!");
                    conn.rollback();
                    conn.close();

                    return null;
                }
                else
                {
                    tMaxNo = 1;
                }
            }
            else
            {
//                strSQL = "update ldmaxno set maxno = maxno + 1 where notype = '" + cNoType
//                        + "' and nolimit = '" + cNoLimit + "'";
                tSBql = new StringBuffer(128);
                tSBql.append("update ldmaxno set maxno = maxno + 1 where notype = '");
                tSBql.append(cNoType);
                tSBql.append("' and nolimit = '");
                tSBql.append(cNoLimit);
                tSBql.append("'");

                exeSQL = new ExeSQL(conn);
                if (!exeSQL.execUpdateSQL(tSBql.toString()))
                {
                    System.out.println("CreateMaxNo ����ʧ�ܣ�������!");
                    conn.rollback();
                    conn.close();

                    return null;
                }
                else
                {
                    tMaxNo = Integer.parseInt(result) + 1;
                }
            }

            conn.commit();
            conn.close();
        }
        catch (Exception Ex)
        {
            try
            {
                conn.rollback();
                conn.close();

                return null;
            }
            catch (Exception e1)
            {
                e1.printStackTrace();

                return null;
            }
        }

        String tStr = String.valueOf(tMaxNo);
        tStr = PubFun.LCh(tStr, "0", serialLen);

        if (tReturn.equals("SN"))
        {
            tReturn = tStr.trim() + "0";
        }
        else
        {
            tReturn = tReturn.trim() + tStr.trim();
        }
        return tReturn;
    }

    /**
     * ���ܣ�����ָ�����ȵ���ˮ�ţ�һ����������һ����ˮ
     * @param cNoType String ��ˮ�ŵ�����
     * @param cNoLength int ��ˮ�ŵĳ���
     * @return String ���ز�������ˮ����
     */
    public String CreateMaxNo(String cNoType, int cNoLength)
    {
        StringBuffer tSBql = null;
        if ((cNoType == null) || (cNoType.trim().length() <= 0) ||
                (cNoLength <= 0))
        {
            System.out.println("NoType���ȴ����NoLength����");
            return null;
        }

        cNoType = cNoType.toUpperCase();

        Connection conn = DBConnPool.getConnection();
        if (conn == null)
        {
            System.out.println("CreateMaxNo : fail to get db connection");
            return null;
        }

        String tReturn = "";
        String cNoLimit = "SN";
        //����������cNoLimitΪSN��������һ��У�飬����ᵼ�����ݸ���
        if (cNoType.equals("COMMISIONSN") ||
                cNoType.equals("GRPNO") || cNoType.equals("CUSTOMERNO") ||
                cNoType.equals("SUGDATAITEMCODE") ||
                cNoType.equals("SUGITEMCODE") ||
                cNoType.equals("SUGMODELCODE") ||
                cNoType.equals("SUGCODE"))
        {
            tSBql = new StringBuffer(128);
            tSBql.append("��������ˮ�ţ������CreateMaxNo('");
            tSBql.append(cNoType);
            tSBql.append("','SN')��ʽ����");
            System.out.println(tSBql.toString());
            return null;
        }
        int tMaxNo = 0;
        tReturn = cNoLimit;

        try
        {
            //��ʼ����
            conn.setAutoCommit(false);

//            String strSQL = "select MaxNo from LDMaxNo where notype='" +
//                    cNoType + "' and nolimit='" + cNoLimit +
//                    "' for update";
//            //������ݿ�������ORACLE�Ļ�����Ҫ���nowait���ԣ��Է�ֹ���ȴ�
//            if (SysConst.DBTYPE.compareTo("ORACLE") == 0)
//            {
//                strSQL = strSQL + " nowait";
//            }
            tSBql = new StringBuffer(128);
            tSBql.append("select MaxNo from LDMaxNo where notype='");
            tSBql.append(cNoType);
            tSBql.append("' and nolimit='");
            tSBql.append(cNoLimit);
            tSBql.append("'");
            //������ݿ�������ORACLE�Ļ�����Ҫ���nowait���ԣ��Է�ֹ���ȴ�
            if (SysConst.DBTYPE.compareTo("ORACLE") == 0)
            {
                tSBql.append(" for update nowait");
            }
            else
            {
                //��������Ǳ꼶����������
//                tSBql.append(" WITH RR USE AND KEEP UPDATE LOCKS");
                tSBql.append(" WITH RS");
            }

            ExeSQL exeSQL = new ExeSQL(conn);
            String result = null;
            result = exeSQL.getOneValue(tSBql.toString());

            if ((result == null) || exeSQL.mErrors.needDealError())
            {
                System.out.println("CreateMaxNo ��Դæ�����Ժ�!");
                conn.rollback();
                conn.close();

                return null;
            }

            if (result.equals(""))
            {
//                strSQL = "insert into ldmaxno(notype, nolimit, maxno) values('" +
//                        cNoType + "', '" + cNoLimit + "', 1)";
                tSBql = new StringBuffer(128);
                tSBql.append("insert into ldmaxno(notype, nolimit, maxno) values('");
                tSBql.append(cNoType);
                tSBql.append("', '");
                tSBql.append(cNoLimit);
                tSBql.append("', 1)");

                exeSQL = new ExeSQL(conn);
                if (!exeSQL.execUpdateSQL(tSBql.toString()))
                {
                    System.out.println("CreateMaxNo ����ʧ�ܣ�������!");
                    conn.rollback();
                    conn.close();

                    return null;
                }
                else
                {
                    tMaxNo = 1;
                }
            }
            else
            {
//                strSQL = "update ldmaxno set maxno = maxno + 1 where notype = '" + cNoType
//                        + "' and nolimit = '" + cNoLimit + "'";
                tSBql = new StringBuffer(128);
                tSBql.append("update ldmaxno set maxno = maxno + 1 where notype = '");
                tSBql.append(cNoType);
                tSBql.append("' and nolimit = '");
                tSBql.append(cNoLimit);
                tSBql.append("'");

                exeSQL = new ExeSQL(conn);
                if (!exeSQL.execUpdateSQL(tSBql.toString()))
                {
                    System.out.println("CreateMaxNo ����ʧ�ܣ�������!");
                    conn.rollback();
                    conn.close();

                    return null;
                }
                else
                {
                    tMaxNo = Integer.parseInt(result) + 1;
                }
            }

            conn.commit();
            conn.close();
        }
        catch (Exception Ex)
        {
            try
            {
                conn.rollback();
                conn.close();

                return null;
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
                return null;
            }
        }

        String tStr = String.valueOf(tMaxNo);
        tStr = PubFun.LCh(tStr, "0", cNoLength);
        tReturn = tStr.trim();

        return tReturn;
    }

    public static void main(String[] args)
    {
    	SysMaxNoZhongYing t = new SysMaxNoZhongYing();
        System.out.println(PubFun1.CreateMaxNo("ORGCOMNO", "86"));
    }
}

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
 * @author Fanym
 * @version 1.0
 */

import java.sql.Connection;
import java.math.BigInteger;
import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.*;

public class SysMaxNoYiHe implements com.sinosoft.lis.pubfun.SysMaxNo
{

    public SysMaxNoYiHe()
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
     * ����ú͵ĵ�֤���ͣ���Ҫһ��֤һ�汾��ˮ�Ĺ���LDMaxNo�е��ֶκ������£�
     * NoType:��֤����
     * NoLimit:��֤�汾
     */
    public String CreateMaxNo(String cNoType, String cNoLimit, VData cVData)
    {
        //��֤�������λ��
        int serialLen = 16;
        String tReturn = null;
        if (cNoType==null  || cNoType.equals(""))
        {
          return null;
        }
        //����ʧ��֪ͨ��
        else if(cNoType.equalsIgnoreCase("TRANSFER"))
        {
            tReturn = "1019";
        }
        //�ͻ������֪ͨ��
        else if(cNoType.equalsIgnoreCase("CUSISSUE"))
        {
            tReturn = "1022";
        }
        //�˱�֪ͨ��
        else if(cNoType.equalsIgnoreCase("HEBAOHAN"))
        {
            tReturn = "1023";
        }
        //���֪ͨ��
        else if(cNoType.equalsIgnoreCase("TIJIAN"))
        {
            tReturn = "1028";
        }
        //����֪ͨ��
        else if(cNoType.equalsIgnoreCase("QIDIAO"))
        {
            tReturn = "1045";
        }
        //����֪ͨ��
        else if(cNoType.equalsIgnoreCase("BUFEI"))
        {
            tReturn = "1048";
        }
        //ҵ��Ա�����
        else if(cNoType.equalsIgnoreCase("AGENTISSUE"))
        {
            tReturn = "1066";
        }
        //����������
        else if(cNoType.equalsIgnoreCase("SPECISSUE"))
        {
            tReturn = "1067";
        }

        else
        {
            return null;
        }

        Connection conn = DBConnPool.getConnection();
        if (conn == null)
        {
            System.out.println("CreateMaxNo : fail to get db connection");
            return null;
        }
        //int tMaxNo = 0;
        BigInteger tMaxNo = new BigInteger("0");
        try
        {
            //��ʼ����
            //��ѯ�����3���� -- added by Fanym
            //ȫ������ֱ��ִ��SQL��䣬ֻҪ���������������˱��У���������NULL
            //���û����������������������ѯ�õ������UPDATE��û����INSERT
            conn.setAutoCommit(false);
            //��ѯ��֤��������ȡ�õ�֤������Ч�����汾��
            String strSQL = "select max(certifycode) from lmcertifydes where state = '0' and Certifycode like '"
                          + tReturn
                          + "%'";
            ExeSQL exeSQL = new ExeSQL(conn);
            String tVersion = exeSQL.getOneValue(strSQL).substring(4, 6);

          //����汾�Ų����ڣ���֤��û�д˵�֤��������
            if (tVersion == null || tVersion.equals(""))
            {
                System.out.println("û�и�" + cNoType + "��֤����������");
                conn.rollback();
                conn.close();
                return null;
            }
            //�Ѱ汾��Ϊ��������������Ϊ2λ��������0
            cNoLimit = PubFun.LCh(tVersion, "0", 2);
            strSQL = "select MaxNo from LDMaxNo where notype='" +
                    cNoType + "' and nolimit='" + cNoLimit +
                    "' for update";
            exeSQL = new ExeSQL(conn);
            String result = null;
            result = exeSQL.getOneValue(strSQL);

            //���Է��ش���
            if (exeSQL.mErrors.needDealError())
            {
                System.out.println("��ѯLDMaxNo�������Ժ�!");
                conn.rollback();
                conn.close();
                return null;
            }

            if ((result == null) || result.equals(""))
            {
                strSQL = "insert into ldmaxno(notype, nolimit, maxno) values('" +
                         cNoType + "', '" + cNoLimit + "', 1)";
                exeSQL = new ExeSQL(conn);
                if (!exeSQL.execUpdateSQL(strSQL))
                {
                    System.out.println("CreateMaxNo ����ʧ�ܣ�������!");
                    conn.rollback();
                    conn.close();
                    return null;
                }
                else
                {
                    //tMaxNo = 1;
                    tMaxNo = new BigInteger("1");
                }
            }
            else
            {
                strSQL = "update ldmaxno set maxno = maxno + 1 where notype = '" + cNoType
                        + "' and nolimit = '" + cNoLimit + "'";
                exeSQL = new ExeSQL(conn);
                if (!exeSQL.execUpdateSQL(strSQL))
                {
                    System.out.println("CreateMaxNo ����ʧ�ܣ�������!");
                    conn.rollback();
                    conn.close();
                    return null;
                }
                else
                {
                    //tMaxNo = Integer.parseInt(result) + 1;
                    tMaxNo = new BigInteger(result).add(new BigInteger("1"));
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
        //��ȡ��ˮ�ţ���λ��-��֤���ͳ���-�汾λ��-�����λ88
        String tStr = PubFun.LCh(tMaxNo.toString(), "0", serialLen - tReturn.length() - cNoLimit.length() - 2);

        tReturn = tReturn + cNoLimit + tStr + "88";
        //���ص�֤��ˮ��
        return tReturn;
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
        if ((cNoType == null) || (cNoType.trim().length() <= 0) || (cNoLimit == null))
        {
            System.out.println("NoType���ȴ������NoLimitΪ��");
            return null;
        }

        int serialLen = 16; //���Ǻ����ܳ�����16λ
        String tReturn = null; //�����ַ���
        cNoType = cNoType.toUpperCase(); //��ˮ����
        //String tBit = "0"; //У��λ

        //cNoLimit�����SN���ͣ���cNoTypeֻ�������������е�һ��
        if (cNoLimit.trim().equalsIgnoreCase("SN"))
        {
            //modify by yt 2002-11-04
            if (cNoType.equals("CUSTOMERNO") || cNoType.equals("GRPNO") || cNoType.equals("VCUSTOMERNO"))
            {
                serialLen = 12; //����Ĭ�Ͽͻ�����12λ
            }
            else if (cNoType.equals("COMMISIONSN"))
            {
              serialLen = 20; //���ʱ���ˮ��Ĭ����20λ
            }
            else
            {
                System.out.println("�����NoLimit");
                return null;
            }
        }
        else
        {
            cNoLimit = "SYS";
        }

        //���˿ͻ���
        if (cNoType.equals("CUSTOMERNO"))
        {
           tReturn = "1";
        }
        //����ͻ���
        else if (cNoType.equals("GRPNO"))
        {
           tReturn = "9";
        }
        //����ͻ����������������ʻ�
        else if (cNoType.equals("VCUSTOMERNO"))
        {
           tReturn = "0";
        }
        //����Ͷ����
        else if (cNoType.equals("PROPOSALNO"))
        {
            tReturn = "9010";
        }
        //�����¸���Ͷ����
        else if (cNoType.equals("GPROPOSALNO"))
        {
            tReturn = "9011";
        }
        //����Ͷ����
        else if (cNoType.equals("GRPPROPOSALNO"))
        {
            tReturn = "9012";
        }
        //�ܵ�Ͷ�������룬ϵͳ����
        else if (cNoType.equals("PROPOSALCONTNO"))
        {
            tReturn = "9015";
        }
        //�����º�ͬͶ��������
        else if (cNoType.equals("GPROPOSALCONTNO"))
        {
            tReturn = "9016";
        }
        //����Ͷ��������ProposalGrpContNo,LDMaxNo����󳤶�Ϊ15��������ProGrpContNo����
        else if (cNoType.equals("PROGRPCONTNO"))
        {
            tReturn = "9018";
        }
        //�������ֺ���
        else if (cNoType.equals("POLNO"))
        {
            tReturn = "9020";
        }
        //�����±������ֺ���
        else if (cNoType.equals("GPOLNO"))
        {
            tReturn = "9021";
        }
        //���屣�����ֺ���
        else if (cNoType.equals("GRPPOLNO"))
        {
            tReturn = "9022";
        }
        //���˱�������
        else if (cNoType.equals("CONTNO"))
        {
            tReturn = "9025";
        }
        //�����¸��˱�������
        else if (cNoType.equals("GCONTNO"))
        {
            tReturn = "9026";
        }
        //���屣����
        else if (cNoType.equals("GRPCONTNO"))
        {
            tReturn = "9028";
        }
        //��ͨ�����ձ�����
        else if (cNoType.equals("AIRPOLNO"))
        {
            tReturn = "9029";
        }
        //�˱�֪ͨ���
//        else if (cNoType.equals("UWNOTICENO"))
//        {
//            tReturn = "102301";
//        }
        //����֪ͨ�����
        else if (cNoType.equals("PAYNOTICENO"))
        {
            tReturn = "102101";
        }
        //�����վݺ���
        else if (cNoType.equals("PAYNO"))
        {
            tReturn = "9032";
        }
        //����֪ͨ�����
        else if (cNoType.equals("GETNOTICENO"))
        {
            tReturn = "9036";
        }
        //ʵ������
        else if (cNoType.equals("GETNO"))
        {
            tReturn = "9038";
        }
        //�����������
        else if (cNoType.equals("EDORAPPNO"))
        {
            tReturn = "9041";
        }
        //��������
        else if (cNoType.equals("EDORNO"))
        {
            tReturn = "9046";
        }
        //���������������
        else if (cNoType.equals("EDORGRPAPPNO"))
        {
            tReturn = "9043";
        }
        //������������
        else if (cNoType.equals("EDORGRPNO"))
        {
            tReturn = "9045";
        }
        //��ȫ�������
        else if (cNoType.equals("EDORACCEPTNO"))
        {
            tReturn = "9048";
        }
        //�������
        else if (cNoType.equals("RPTNO"))
        {
            tReturn = "9050";
        }
        //�������
        else if (cNoType.equals("RGTNO"))
        {
            tReturn = "9051";
        }
        //�ⰸ���
        else if (cNoType.equals("CLMNO"))
        {
            tReturn = "9052";
        }
        //�ܰ����
        else if (cNoType.equals("DECLINENO"))
        {
            tReturn = "9053";
        }
        //�����ְ����
        else if (cNoType.equals("SUBRPTNO"))
        {
            tReturn = "9054";
        }
        //�����ְ����
        else if (cNoType.equals("CASENO"))
        {
            tReturn = "9055";
        }
        //�¼����
        else if (cNoType.equals("CASERELANO"))
        {
            tReturn = "9056";
        }
        //��ӡ������ˮ��
        else if (cNoType.equals("PRTSEQNO"))
        {
            tReturn = "9066";
        }
        //��ӡ������ˮ��
        else if (cNoType.equals("PRTSEQ2NO"))
        {
            tReturn = "9068";
        }
        //�������㵥��
        else if (cNoType.equals("TAKEBACKNO"))
        {
            tReturn = "9061";
        }
        //���д��۴������κ�
        else if (cNoType.equals("BATCHNO"))
        {
            tReturn = "9063";
        }
        //���ʱ���ˮ��
        else if (cNoType.equals("COMMISIONSN"))
        {
          tReturn = "9071";
        }
        //�н�Э�������
        else if (cNoType.equals("PROTOCOLNO"))
        {
          tReturn = "9078";
        }
        //���������
        else if (cNoType.equals("SUGCODE"))
        {
          tReturn = "9088";
        }
        //������ģ�����
        else if (cNoType.equals("SUGMODELCODE"))
        {
          tReturn = "9082";
        }
        //��������Ŀ����
        else if (cNoType.equals("SUGITEMCODE"))
        {
          tReturn = "9083";
        }
        //������������Ŀ����
        else if (cNoType.equals("SUGDATAITEMCODE"))
        {
          tReturn = "9085";
        }
        //��ˮ��
        else if (cNoType.equals("SERIALNO"))
        {
            tReturn = "9090";
        }
        //����
        else
        {
            tReturn = "";
            //return null;
        }

        Connection conn = DBConnPool.getConnection();
        if (conn == null)
        {
            System.out.println("CreateMaxNo : fail to get db connection");
            return null;
        }

        //int tMaxNo = 0;
        BigInteger tMaxNo = new BigInteger("0");

        try
        {
            //��ʼ����
            //��ѯ�����3���� -- added by Fanym
            //ȫ������ֱ��ִ��SQL��䣬ֻҪ���������������˱��У���������NULL
            //���û����������������������ѯ�õ������UPDATE��û����INSERT
            conn.setAutoCommit(false);

            String strSQL = "select MaxNo from LDMaxNo where notype='" +
                    cNoType + "' and nolimit='" + cNoLimit +
                    "' for update";
            ExeSQL exeSQL = new ExeSQL(conn);
            String result = null;
            result = exeSQL.getOneValue(strSQL);

            //���Է���nullʱ
            if (exeSQL.mErrors.needDealError())
            {
                System.out.println("��ѯLDMaxNo�������Ժ�!");
                conn.rollback();
                conn.close();
                return null;
            }

            if ((result == null) || result.equals(""))
            {
                strSQL = "insert into ldmaxno(notype, nolimit, maxno) values('" +
                        cNoType + "', '" + cNoLimit + "', 1)";
                exeSQL = new ExeSQL(conn);
                if (!exeSQL.execUpdateSQL(strSQL))
                {
                    System.out.println("CreateMaxNo ����ʧ�ܣ�������!");
                    conn.rollback();
                    conn.close();

                    return null;
                }
                else
                {
                    tMaxNo = new BigInteger("1");
                }
            }
            else
            {
                strSQL = "update ldmaxno set maxno = maxno + 1 where notype = '" + cNoType
                        + "' and nolimit = '" + cNoLimit + "'";
                exeSQL = new ExeSQL(conn);
                if (!exeSQL.execUpdateSQL(strSQL))
                {
                    System.out.println("CreateMaxNo ����ʧ�ܣ�������!");
                    conn.rollback();
                    conn.close();

                    return null;
                }
                else
                {
                    //tMaxNo = Integer.parseInt(result) + 1;
                    tMaxNo = new BigInteger(result).add(new BigInteger("1"));

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

        //String tStr = String.valueOf(tMaxNo);
        String tStr = tMaxNo.toString();
        tStr = PubFun.LCh(tStr, "0", serialLen - tReturn.length() - 2); //��Ҫ����λ��88
        tReturn = tReturn + tStr + "88";
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
        if ((cNoType == null) || (cNoType.trim().length() <= 0) ||
                (cNoLength <= 0))
        {
            System.out.println("NoType���ȴ����NoLength����");
            return null;
        }

        cNoType = cNoType.toUpperCase();
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
            System.out.println("��������ˮ�ţ������CreateMaxNo('"+cNoType+"','SN')��ʽ����");
            return null;
        }

        Connection conn = DBConnPool.getConnection();
        if (conn == null)
        {
            System.out.println("CreateMaxNo : fail to get db connection");
            return null;
        }

        //int tMaxNo = 0;
        BigInteger tMaxNo = new BigInteger("0");
        tReturn = cNoLimit;

        try
        {
            //��ʼ����
            conn.setAutoCommit(false);

            String strSQL = "select MaxNo from LDMaxNo where notype='" +
                    cNoType + "' and nolimit='" + cNoLimit +
                    "' for update";

            ExeSQL exeSQL = new ExeSQL(conn);
            String result = null;
            result = exeSQL.getOneValue(strSQL);

            if (exeSQL.mErrors.needDealError())
            {
                System.out.println("CreateMaxNo ��ѯʧ�ܣ����Ժ�!");
                conn.rollback();
                conn.close();
                return null;
            }

            if ((result == null) ||(result.equals("")))
            {
                strSQL = "insert into ldmaxno(notype, nolimit, maxno) values('" +
                        cNoType + "', '" + cNoLimit + "', 1)";
                exeSQL = new ExeSQL(conn);
                if (!exeSQL.execUpdateSQL(strSQL))
                {
                    System.out.println("CreateMaxNo ����ʧ�ܣ�������!");
                    conn.rollback();
                    conn.close();
                    return null;
                }
                else
                {
                    //tMaxNo = 1;
                    tMaxNo = new BigInteger("1");
                }
            }
            else
            {
                strSQL = "update ldmaxno set maxno = maxno + 1 where notype = '" + cNoType
                        + "' and nolimit = '" + cNoLimit + "'";
                exeSQL = new ExeSQL(conn);
                if (!exeSQL.execUpdateSQL(strSQL))
                {
                    System.out.println("CreateMaxNo ����ʧ�ܣ�������!");
                    conn.rollback();
                    conn.close();
                    return null;
                }
                else
                {
                    //tMaxNo = Integer.parseInt(result) + 1;
                    tMaxNo = new BigInteger(result).add(new BigInteger("1"));
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

        //String tStr = String.valueOf(tMaxNo);
        String tStr = tMaxNo.toString();
        tStr = PubFun.LCh(tStr, "0", cNoLength);
        tReturn = tStr;

        return tReturn;
    }

    public static void main(String[] args)
    {
        SysMaxNoYiHe tSysMaxNoYiHe = new SysMaxNoYiHe();
        VData tVData = new VData();
        System.out.println(tSysMaxNoYiHe.CreateMaxNo("HEBAOHAN","SN",tVData));
//        String a = "8611";
//        System.out.println(a.substring(0, 4));
    }
}

package com.sinosoft.lis.pubfun;

import java.util.*;
import java.sql.Connection;
import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.*;

public class SysMaxNoPicch implements com.sinosoft.lis.pubfun.SysMaxNo
{
    public SysMaxNoPicch()
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
        //PicchĿǰδ����
        return CreateMaxNo(cNoType, cNoLimit);
    }

    /**
     * ���ܣ�����ָ�����ȵ���ˮ�ţ�һ����������һ����ˮ
     * @param cNoType����ˮ�ŵ�����
     * @param cNoLength����ˮ�ŵĳ���
     * @return ���ز�������ˮ����
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

        //System.out.println("type:"+cNoType+"   length:"+cNoLength);
        Connection conn = DBConnPool.getConnection();

        if (conn == null)
        {
            System.out.println("CreateMaxNo : fail to get db connection");

            return null;
        }

        String tReturn = "";
        String cNoLimit = "SN";
        int tMaxNo = 0;
        tReturn = cNoLimit;

        try
        {
            //��ʼ����
            conn.setAutoCommit(false);

            String strSQL = "select MaxNo from LDMaxNo where notype='" +
                            cNoType + "' and nolimit='" + cNoLimit +
                            "' for update";
            //������ݿ�������ORACLE�Ļ�����Ҫ���nowait���ԣ��Է�ֹ���ȴ�
            if (SysConst.DBTYPE.equals("ORACLE"))
            {
                strSQL = strSQL + " nowait";
            }

            ExeSQL exeSQL = new ExeSQL(conn);
            String result = null;
            result = exeSQL.getOneValue(strSQL);

            if ((result == null) || exeSQL.mErrors.needDealError())
            {
                System.out.println("CreateMaxNo ��Դæ�����Ժ�!");
                conn.rollback();
                conn.close();

                return null;
            }

            if (result.equals(""))
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
                    tMaxNo = 1;
                }
            }
            else
            {
                strSQL = "update ldmaxno set maxno = maxno + 1" +
                         " where notype = '" + cNoType + "' and nolimit = '" +
                         cNoLimit + "'";
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

    /**
     *<p>������ˮ�ŵĺ���,�����ض��������ˮ��<p>
     * @param cNoType Ϊ��Ҫ���ɺ��������
     * @param cInput  Ϊ���ɺ�����Ҫ����Ĳ���
     * @return ���ɵķ�����������ˮ�ţ��������ʧ�ܣ����ؿ�null
     */
    public String CreateMaxNo(String cNoType, String cInput)
    {
        //����Ĳ�������Ϊ�գ����Ϊ�գ���ֱ�ӷ���
        if ((cNoType == null) || (cNoType.trim().length() <= 0))
        {
            System.out.println("NoType���ȴ���");
            return null;
        }

        String tReturn = null;
        cNoType = cNoType.toUpperCase();
        //�õ���ǰ����
        String tDate = PubFun.getCurrentDate();
        String tDateCode = tDate.substring(2, 4) + tDate.substring(5, 7) + tDate.substring(8, 10);

        //���յ���ͥ��
        if (cNoType.equals("FamilyNo"))
        {
            tReturn = "F" + CreateMaxNo(cNoType, 9);
        }

        //���տͻ���
        else if (cNoType.equals("CUSTOMERNO"))
        {
            tReturn = CreateMaxNo(cNoType, 9);
        }

        //����Ͷ������
        else if (cNoType.equals("PROPOSALCONTNO"))
        {
            tReturn = "13" + CreateMaxNo(cNoType, 9);
        }

        //����Ͷ������
        else if (cNoType.equals("PROGRPCONTNO"))
        {
            tReturn = "14" + CreateMaxNo(cNoType, 8);
        }

        //�������ֺ���
        else if (cNoType.equals("POLNO"))
        {
            tReturn = "21" +CreateMaxNo(cNoType, 9);
        }

        //���屣�����ֺ���
        else if (cNoType.equals("GRPPOLNO"))
        {
            tReturn = "22" + CreateMaxNo(cNoType, 8);
        }

        //����������Ϊ9λͶ���˿ͻ���+2λ������
        else if (cNoType.equals("CONTNO"))
        {
            if ((cInput == null) || (cInput.length() != 9))
            {
                System.out.println("������9λ�ͻ���!");
                return null;
            }
            tReturn = cInput + CreateMaxNo(cNoType, 2);
        }

        //��ͬ����
        else if (cNoType.equals("GRPPERSONCONTNO"))
        {
            tReturn = "23" + CreateMaxNo(cNoType, 8);
        }

        //����Ͷ����ӡˢ��
        else if (cNoType.equals("PRINTNO"))
        {
            tReturn = "16" + CreateMaxNo(cNoType, 9);
        }

        //���տͻ���
        else if (cNoType.equals("GRPNO"))
        {
            tReturn = CreateMaxNo(cNoType, 8);
        }

        //���ձ������˿ͻ���
        else if (cNoType.equals("GRPINSUREDNO"))
        {
            if ((cInput == null) || (cInput.length() != 9))
            {
                System.out.println("������9λ�ͻ���!");
                return null;
            }
            tReturn = cInput + CreateMaxNo(cNoType, 2);
        }

        //���ձ�����Ϊ8λ�ͻ���+2λ������
        else if (cNoType.equals("GRPCONTNO"))
        {
            if ((cInput == null) || (cInput.length() != 8))
            {
                System.out.println("������8λ�ͻ���!");
                return null;
            }
            tReturn = cInput + CreateMaxNo(cNoType, 2);
        }

        //����ѯ�ۺ���Ϊ1λӢ����ĸR+8λ�ͻ���+3λ����ѯ�۴���
        else if (cNoType.equals("GRPQUERYNO"))
        {
            if ((cInput == null) || (cInput.length() != 8))
            {
                System.out.println("������8λ�ͻ���!");
                return null;
            }
            tReturn = "R" + cInput + CreateMaxNo(cNoType, 3);
        }
        else if (cNoType.equals("PROPOSALNO"))
        {
            tReturn = "11" + CreateMaxNo(cNoType, 9);
        }

        //����Ͷ�������ֺ���
        else if (cNoType.equals("GRPPROPOSALNO"))
        {
            tReturn = "12" + CreateMaxNo(cNoType, 9);
        }

        //����ȷ����Ͷ����ӡˢ��
        else if (cNoType.equals("GRPCOMFIRMPRTNO"))
        {
            tReturn = "18" + CreateMaxNo(cNoType, 9);
        }

        //����ѯ����Ͷ����ӡˢ��
        else if (cNoType.equals("GRPQUERYPRTNO"))
        {
            tReturn = "19" + CreateMaxNo(cNoType, 9);
        }

        //��֤������Ϊ�����룫9λ�ͻ��ţ�2λ�����   --������?
        else if (cNoType.equals("BARCODENO"))
        {
            if (cInput == null)
            {
                System.out.println("������9λ�ͻ���!");
                return null;
            }
            Random random = new Random();
            tReturn = cInput + Math.abs(random.nextInt()) % 100;
        }

        //�����Ϊ����ŷ������+������������+������������+���ڱ���+������ˮ��
        //����֪ͨ��
        else if (cNoType.equals("NOTICENO"))
        {
//            if ((cInput == null) || (cInput.length() != 4))
//            {
//                System.out.println("����Ų������벻��ȷ!");
//                return null;
//            }
            cInput = getClaimLimit( cInput );
            tReturn = "T" + cInput + tDateCode +
                      CreateMaxNo(cNoType + tDateCode, 6);
        }
    //������ѯ��
        else if (cNoType.equals("COSULTNO"))
        {
//            if ((cInput == null) || (cInput.length() != 4))
//            {
//                System.out.println("����Ų������벻��ȷ!");
//                return null;
//            }
            cInput = getClaimLimit( cInput );
            tReturn = "Z" + cInput + tDateCode +
                      CreateMaxNo(cNoType + tDateCode, 6);
        }

        //����֪ͨ��ѯ��
        else if (cNoType.equals("CNNO"))
        {
//            if ((cInput == null) || (cInput.length() != 4))
//            {
//                System.out.println("����Ų������벻��ȷ!");
//                return null;
//            }
            cInput = getClaimLimit( cInput );
            tReturn = "H" + cInput + tDateCode +
                      CreateMaxNo(cNoType + tDateCode, 6);
        }

        //���������
        else if (cNoType.equals("CASENO"))
        {
//            if ((cInput == null) || (cInput.length() != 4))
//            {
//                System.out.println("����Ų������벻��ȷ!");
//                return null;
//            }
            cInput = getClaimLimit( cInput );
            tReturn = "C" + cInput + tDateCode +
                      CreateMaxNo(cNoType + tDateCode, 6);
        }

        //�������ߺ�
        else if (cNoType.equals("APPEALNO"))
        {
//            if ((cInput == null) || (cInput.length() != 4))
//            {
//                System.out.println("����Ų������벻��ȷ!");
//                return null;
//            }
            cInput = getClaimLimit( cInput );
            tReturn = "S" + cInput + tDateCode +
                      CreateMaxNo(cNoType + tDateCode, 6);
        }

        //����������
        else if (cNoType.equals("LLERRORNO"))
        {
//            if ((cInput == null) || (cInput.length() != 4))
//            {
//                System.out.println("����Ų������벻��ȷ!");
//                return null;
//            }
            cInput = getClaimLimit( cInput );
            tReturn = "R" + cInput + tDateCode +
                      CreateMaxNo(cNoType + tDateCode, 6);
        }

        //�������κ�
        else if (cNoType.equals("RGTNO"))
        {
//            if ((cInput == null) || (cInput.length() != 4))
//            {
//                System.out.println("����Ų������벻��ȷ!");
//                return null;
//            }
            cInput = getClaimLimit( cInput );
            tReturn = "P" + cInput + tDateCode +
                      CreateMaxNo(cNoType + tDateCode, 6);
        }

        //�����¼���
        else if (cNoType.equals("SUBRPTNO"))
        {
//            if ((cInput == null) || (cInput.length() != 4))
//            {
//                System.out.println("����Ų������벻��ȷ!");
//                return null;
//            }
            cInput = getClaimLimit( cInput );
            tReturn = "A" + cInput + tDateCode +
                      CreateMaxNo(cNoType + tDateCode, 6);
        }

        //����֪ͨ�����
        else if (cNoType.equals("PAYNOTICENO"))
        {
            tReturn = "31" + CreateMaxNo(cNoType, 9);
        }

        //�����վݺ���
        else if (cNoType.equals("PAYNO"))
        {
            tReturn = "32" + CreateMaxNo(cNoType, 9);
        }

        //����֪ͨ�����
        else if (cNoType.equals("GETNOTICENO"))
        {
            tReturn = "36" + CreateMaxNo(cNoType, 9);
        }

        //����֪ͨ�����
        else if (cNoType.equals("GETNO"))
        {
            tReturn = "37" + CreateMaxNo(cNoType, 9);
        }

        //�����������
        else if (cNoType.equals("EDORAPPNO"))
        {
            tReturn = "41" + CreateMaxNo(cNoType, 9);
        }

        //��������
        else if (cNoType.equals("EDORNO"))
        {
            tReturn = "42" + CreateMaxNo(cNoType, 9);
        }

        //���������������
        else if (cNoType.equals("EDORGRPAPPNO"))
        {
            tReturn = "43" + CreateMaxNo(cNoType, 9);
        }

        //������������
        else if (cNoType.equals("EDORGRPNO"))
        {
            tReturn = "44" + CreateMaxNo(cNoType, 9);
        }
        //��ͬ��
        else if (cNoType.equals("PROTOCOLNO"))
        {
            tReturn = "71" + CreateMaxNo(cNoType, 9);
        }

        //��֤ӡˢ����
        else if (cNoType.equals("PRTNO"))
        {
            tReturn = "80" + CreateMaxNo(cNoType, 9);
        }

        //��ӡ������ˮ��
        else if (cNoType.equals("PRTSEQNO"))
        {
            tReturn = "81" + CreateMaxNo(cNoType, 9);
        }

        //��ӡ������ˮ��
        else if (cNoType.equals("PRTSEQ2NO"))
        {
            tReturn = "82" + CreateMaxNo(cNoType, 9);
        }

        //�������㵥��
        else if (cNoType.equals("TAKEBACKNO"))
        {
            tReturn = "61" + CreateMaxNo(cNoType, 9);
        }

        //���д��۴������κ�
        else if (cNoType.equals("BATCHNO"))
        {
            tReturn = "62" + CreateMaxNo(cNoType, 9);
        }

        //�ӿ�ƾ֤id��
        else if (cNoType.equals("VOUCHERIDNO"))
        {
            tReturn = "63" + CreateMaxNo(cNoType, 9);
        }

        //Ӷ�����
        else if (cNoType.equals("WAGENO"))
        {
            tReturn = "90" + CreateMaxNo(cNoType, 9);
        }

        //��ˮ��
        else if (cNoType.equals("SERIALNO"))
        {
            tReturn = "98" + CreateMaxNo(cNoType, 9);
        }

        //Ĭ�ϵ�����Ӣ�Ĺ���
        else
        {
            SysMaxNoZhongYing zhongying = new SysMaxNoZhongYing();
            tReturn = zhongying.CreateMaxNo(cNoType, cInput);
        }

        return tReturn;
    }

   private String getClaimLimit(String MngComCode )
   {
        return MngComCode.substring(2,6);
   }

    public static void main(String[] args)
    {
        SysMaxNoPicch tSysMaxNoPicch = new SysMaxNoPicch();
//        System.out.println("HOMENO-" + tSysMaxNoPicch.CreateMaxNo("HOMENO", null));
//        System.out.println("CUSTOMERNO-" + tSysMaxNoPicch.CreateMaxNo("CUSTOMERNO", null));
//        System.out.println("PROPOSALCONTNO-" + tSysMaxNoPicch.CreateMaxNo("PROPOSALCONTNO", null));
//        System.out.println("CONTNO-" + tSysMaxNoPicch.CreateMaxNo("CONTNO", "111111111"));
//        System.out.println("PRINTNO-" + tSysMaxNoPicch.CreateMaxNo("PRINTNO", null));
//        System.out.println("GRPCUSTOMERNO-" + tSysMaxNoPicch.CreateMaxNo("GRPCUSTOMERNO", null));
//        System.out.println("GRPINSUREDNO-" + tSysMaxNoPicch.CreateMaxNo("GRPINSUREDNO", "123456789"));
//        System.out.println("GRPCONTNO-" + tSysMaxNoPicch.CreateMaxNo("GRPCONTNO", "12345678"));
//        System.out.println("GRPQUERYNO-" + tSysMaxNoPicch.CreateMaxNo("GRPQUERYNO", "12345678"));
//        System.out.println("GRPCOMFIRMPRTNO-" + tSysMaxNoPicch.CreateMaxNo("GRPCOMFIRMPRTNO", null));
//        System.out.println("GRPQUERYPRTNO-" + tSysMaxNoPicch.CreateMaxNo("GRPQUERYPRTNO", null));
//        System.out.println("BARCODENO-" + tSysMaxNoPicch.CreateMaxNo("BARCODENO", "123456789"));
//        System.out.println("RGTNO-" + tSysMaxNoPicch.CreateMaxNo("RGTNO", "0102"));
        System.out.println("CASERELANO" + tSysMaxNoPicch.CreateMaxNo("CASERELANO", "8600"));
    }

}


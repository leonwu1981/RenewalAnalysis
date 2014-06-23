/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;


/**
 * <p>Title: Webҵ��ϵͳ</p>
 * <p>Description:ϵͳ��������㿵�찲ҵ��ϵͳ������ϵͳ���� </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Sinosoft</p>
 * @author Liuqiang
 * @version 1.0
 */

import java.sql.Connection;

import com.sinosoft.lis.schema.LZCardPrintSchema;
import com.sinosoft.utility.DBConnPool;
import com.sinosoft.utility.ExeSQL;
import com.sinosoft.utility.VData;

public class SysMaxNoHengKang implements com.sinosoft.lis.pubfun.SysMaxNo
{

    public SysMaxNoHengKang()
    {
    }

    /**
     *<p>������ˮ�ŵĺ���<p>
     *<p>������򣺻�������  ������  У��λ   ����    ��ˮ��<p>
     *<p>          1-6     7-10   11     12-13   14-20<p>
     * @param cNoType Ϊ��Ҫ���ɺ��������
     * @param cNoLimit Ϊ��Ҫ���ɺ��������������Ҫô��SN��Ҫô�ǻ������룩
     * @return ���ɵķ�����������ˮ�ţ��������ʧ�ܣ����ؿ��ַ���""
     */
    public String CreateMaxNo(String cNoType, String cNoLimit, VData cVData)
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
                cNoType.equals("GRPNO") ||
                cNoType.equals("SUGDATAITEMCODE") ||
                cNoType.equals("SUGITEMCODE") ||
                cNoType.equals("SUGMODELCODE") ||
                cNoType.equals("SUGCODE"))
            {
                serialLen = 10;
            }
            else if (cNoType.equals("CUSTOMERNO")) //�ͻ�����20λ��ˮ��
            {
                serialLen = 20;
            }
            else if (cNoType.equals("GRPNO"))
            {
                serialLen = 20;
            }
            else
            {
                System.out.println("�����NoLimit");

                return null;
            }
        }

        if (cNoType.equals("AGENTCODE")) //Ա�����룬6λ��ˮ��
        {
            serialLen = 6;
        }

        tReturn = cNoLimit.trim();
        //System.out.println("tReturn:"+tReturn);

        String tCom = ""; //��λ����
        if (tReturn.length() >= 4)
        {
            //tCom = tReturn;
            tCom = tReturn.substring(0, 4);
            tCom = "0" + tCom; //��һλ����λ
            tReturn = tReturn.substring(0, 4);
        }

        //���ɸ��ֺ���
        //����Ͷ�������ֺ���
        if (cNoType.equals("PROPOSALNO"))
        {
            //tReturn = "11" + tCom;
            tReturn = tCom.substring(1, 5) + "A1";
        }

        //����Ͷ�������ֺ���
        else if (cNoType.equals("GRPPROPOSALNO"))
        {
            //tReturn = "12" + tCom;//��Ӣ�÷�
            tReturn = tCom.substring(1, 5) + "A2";
        }

        //�����ܵ�Ͷ��������
        else if (cNoType.equals("PROPOSALCONTNO"))
        {
            //tReturn = "13" + tCom;
            tReturn = tCom.substring(1, 5) + "A3";
        }

        //����Ͷ��������ProposalGrpContNo,LDMaxNo����󳤶�Ϊ15��������ProGrpContNo����
        else if (cNoType.equals("PROGRPCONTNO"))
        {
            tReturn = tCom.substring(1, 5) + "A0";
        }

        //���˱������ֺ���
        else if (cNoType.equals("POLNO"))
        {
            tReturn = tCom.substring(1, 5) + "P1";
        }

        //���屣�����ֺ���
        else if (cNoType.equals("GRPPOLNO"))
        {
            tReturn = tCom.substring(1, 5) + "P2";
        }

        //���˺�ͬ����
        else if (cNoType.equals("CONTNO"))
        {
            tReturn = tCom.substring(1, 5) + "P3";
        }

        //�����ͬ����
        else if (cNoType.equals("GRPCONTNO"))
        {

            tReturn = tCom.substring(1, 5) + "P0";
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

        //��֤ӡˢ����,�м۵�֤ӡˢ�������20λ����ɣ���������������4λ����֤�����5λ�����4λ��7λ��ˮ�š�
        else if (cNoType.equals("PRTNO"))
        {
            LZCardPrintSchema mLZCardPrintSchema = new LZCardPrintSchema();
            mLZCardPrintSchema.setSchema((LZCardPrintSchema) cVData.
                                         getObjectByObjectName(
                                                 "LZCardPrintSchema", 0));
            if (mLZCardPrintSchema.getSubCode() != null &&
                mLZCardPrintSchema.getSubCode() != "")
            {
                tReturn = tCom.substring(0, 4) +
                          mLZCardPrintSchema.getCertifyCode() +
                          tCom.substring(6, 4);
            }
            else
            {
                tReturn = tCom.substring(0, 4);
            }
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
            tReturn = tReturn + "99";
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

            String strSQL = "select MaxNo from LDMaxNo where notype='" +
                            cNoType + "' and nolimit='" + cNoLimit +
                            "' for update";

            ExeSQL exeSQL = new ExeSQL(conn);
            String result = null;
            result = exeSQL.getOneValue(strSQL);

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

        /*
               if (tReturn.length() >= 12)
               {
            tReturn = tReturn.substring(0, 10) + "0" +
                tReturn.substring(10, 12);
               }
         */

        String tStr = String.valueOf(tMaxNo);
        tStr = PubFun.LCh(tStr, "0", serialLen);
        if (tReturn.equals("SN"))
        {
            tReturn = tStr.trim();
            //tReturn = tStr.trim() + "0";
        }
        else
        {
            tReturn = tReturn.trim() + tStr.trim();
        }
        //System.out.println("------tReturn:"+tReturn);
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
                cNoType.equals("SUGDATAITEMCODE") ||
                cNoType.equals("SUGITEMCODE") ||
                cNoType.equals("SUGMODELCODE") ||
                cNoType.equals("SUGCODE"))
            {
                serialLen = 10;
            }
            else if (cNoType.equals("CUSTOMERNO")) //�ͻ�����20λ��ˮ��
            {
                serialLen = 20;
            }
            else if (cNoType.equals("GRPNO"))
            {
                serialLen = 20;
            }
            else
            {
                System.out.println("�����NoLimit");

                return null;
            }
        }

        if (cNoType.equals("AGENTCODE")) //Ա�����룬6λ��ˮ��
        {
            serialLen = 6;
        }

        tReturn = cNoLimit.trim();
        //System.out.println("tReturn:"+tReturn);

        String tCom = ""; //��λ����
        if (tReturn.length() >= 4)
        {
            //tCom = tReturn;
            tCom = tReturn.substring(0, 4);
            tCom = "0" + tCom; //��һλ����λ
            tReturn = tReturn.substring(0, 4);
        }

        //���ɸ��ֺ���
        //����Ͷ�������ֺ���
        if (cNoType.equals("PROPOSALNO"))
        {
            //tReturn = "11" + tCom;
            tReturn = tCom.substring(1, 5) + "A1";
        }

        //����Ͷ�������ֺ���
        else if (cNoType.equals("GRPPROPOSALNO"))
        {
            //tReturn = "12" + tCom;//��Ӣ�÷�
            tReturn = tCom.substring(1, 5) + "A2";
        }

        //�����ܵ�Ͷ��������
        else if (cNoType.equals("PROPOSALCONTNO"))
        {
            //tReturn = "13" + tCom;
            tReturn = tCom.substring(1, 5) + "A3";
        }

        //����Ͷ��������ProposalGrpContNo,LDMaxNo����󳤶�Ϊ15��������ProGrpContNo����
        else if (cNoType.equals("PROGRPCONTNO"))
        {
            tReturn = tCom.substring(1, 5) + "A0";
        }

        //���˱������ֺ���
        else if (cNoType.equals("POLNO"))
        {
            tReturn = tCom.substring(1, 5) + "P1";
        }

        //���屣�����ֺ���
        else if (cNoType.equals("GRPPOLNO"))
        {
            tReturn = tCom.substring(1, 5) + "P2";
        }

        //���˺�ͬ����
        else if (cNoType.equals("CONTNO"))
        {
            tReturn = tCom.substring(1, 5) + "P3";
        }

        //�����ͬ����
        else if (cNoType.equals("GRPCONTNO"))
        {

            tReturn = tCom.substring(1, 5) + "P0";
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
            tReturn = tReturn + "99";
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

            String strSQL = "select MaxNo from LDMaxNo where notype='" +
                            cNoType + "' and nolimit='" + cNoLimit +
                            "' for update";

            ExeSQL exeSQL = new ExeSQL(conn);
            String result = null;
            result = exeSQL.getOneValue(strSQL);

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

        /*
               if (tReturn.length() >= 12)
               {
            tReturn = tReturn.substring(0, 10) + "0" +
                tReturn.substring(10, 12);
               }
         */

        String tStr = String.valueOf(tMaxNo);
        tStr = PubFun.LCh(tStr, "0", serialLen);
        if (tReturn.equals("SN"))
        {
            tReturn = tStr.trim();
            //tReturn = tStr.trim() + "0";
        }
        else
        {
            tReturn = tReturn.trim() + tStr.trim();
        }
        //System.out.println("------tReturn:"+tReturn);
        return tReturn;
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
        //����������cNoLimitΪSN��������һ��У�飬����ᵼ�����ݸ���
        if (cNoType.equals("COMMISIONSN") ||
            cNoType.equals("GRPNO") || cNoType.equals("CUSTOMERNO") ||
            cNoType.equals("SUGDATAITEMCODE") ||
            cNoType.equals("SUGITEMCODE") ||
            cNoType.equals("SUGMODELCODE") ||
            cNoType.equals("SUGCODE"))
        {

            System.out.println("��������ˮ�ţ������CreateMaxNo('" + cNoType +
                               "','SN')��ʽ����");
            return null;
        }
        int tMaxNo = 0;
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

    public static void main(String[] args)
    {
//        SysMaxNoHengKang sysMaxNoHengKang1 = new SysMaxNoHengKang();
//        System.out.println(sysMaxNoHengKang1.CreateMaxNo("GRPNO", "SN"));
    }
}

/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * <p>Title: </p>
 * Blob���ݲ�����ת��
 * <p>Description: </p>
 * ����ͳһ��Blob���͵����ݲ���
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SINOSOFT</p>
 * @author ZHUXF
 * @version 1.0
 */
public class CBlob
{
    public CBlob()
    {
    }

    /**
     * BLOB��Ӳ���
     * @param pIns InputStream
     * @param pStr String[]
     * @param pConn Connection
     * @return boolean
     */
    public static boolean BlobInsert(InputStream pIns, String[] pStr, Connection pConn)
    {
        StringBuffer mSBql = new StringBuffer(256);
        //�ж�ϵͳʹ�õ����ݿ�����
        //System.out.println(pStr[0]);
        if (SysConst.DBTYPE.compareTo("DB2") == 0)
        {
            //�����db2���ݿ⣬����Ҫ�滻��oracle���к���empty_blob()
//            String tSql = pStr[0].substring(0, pStr[0].indexOf("empty_blob()"));
//            tSql = tSql + "?" +
//                   pStr[0].substring(pStr[0].indexOf("empty_blob()") + 12,pStr[0].length());
            mSBql.append(pStr[0].substring(0, pStr[0].indexOf("empty_blob()")));
            mSBql.append("?");
            mSBql.append(pStr[0].substring(pStr[0].indexOf("empty_blob()") + 12,pStr[0].length()));

            //����db2�µ�blob����
            CDB2Blob tCDB2Blob = new CDB2Blob();
            if (!tCDB2Blob.InsertBlankBlobRecord(pIns, mSBql.toString(), pConn))
            {
                //���ִ��ʧ�ܣ��򷵻�false
                return false;
            }
        }
        else
        {
            //����oralce�µ�blob����
            COracleBlob tCOracleBlob = new COracleBlob();
            if (!tCOracleBlob.InsertBlankBlobRecord(pStr[0], pConn))
            {
                //���ִ��ʧ�ܣ��򷵻�false
                return false;
            }
            if (!tCOracleBlob.UpdateBlob(pIns, pStr[1], pStr[2], pStr[3], pConn))
            {
                //���ִ��ʧ�ܣ��򷵻�false
                return false;
            }
        }
        //���쳣����true
        return true;
    }

    /**
     * BLOB���²���
     * @param pIns InputStream
     * @param pStr String[]
     * @param pConn Connection
     * @return boolean
     */
    public static boolean BlobUpdate(InputStream pIns, String[] pStr, Connection pConn)
    {
        if (SysConst.DBTYPE.compareTo("DB2") == 0)
        {
            //����db2�µ�blob����
            CDB2Blob tCDB2Blob = new CDB2Blob();
            if (!tCDB2Blob.UpdateBlob(pIns, pStr[1], pStr[2], pStr[3], pConn))
            {
                //���ִ��ʧ�ܣ��򷵻�false
                return false;
            }
        }
        else
        {
            //����oracle��blob����
            COracleBlob tCOracleBlob = new COracleBlob();
            if (!tCOracleBlob.UpdateBlob(pIns, pStr[1], pStr[2], pStr[3], pConn))
            {
                //���ִ��ʧ�ܣ��򷵻�false
                return false;
            }
        }
        return true;
    }

    /**
     * ��ȡָ��SQL����е�ĳ��Blob�ֶε�һ��OutStream�С�
     * @param cTabName String
     * @param cSelectField String
     * @param cWhereSQL String
     * @param cConn Connection
     * @return Blob
     */
    public static Blob SelectBlob(String cTabName, String cSelectField,
                           String cWhereSQL, Connection cConn)
    {
        StringBuffer mSBql = new StringBuffer(256);
        // TODO: implement
        Statement tStatement = null;
        ResultSet tResultSet = null;
        Blob tBlob = null;

        if (cConn == null)
        {
            //�������û�д��룬�򷵻�false
            System.out.println("CBlobû�д������ӣ�");
            return null;
        }
        try
        {
            tStatement = cConn.createStatement();
//            String tSql;
//            tSql = "SELECT " + cSelectField + " FROM " + cTabName
//                   + " WHERE 1=1 " + cWhereSQL;
            mSBql.append("SELECT ");
            mSBql.append(cSelectField);
            mSBql.append(" FROM ");
            mSBql.append(cTabName);
            mSBql.append(" WHERE 1=1 ");
            mSBql.append(cWhereSQL);

            tResultSet = tStatement.executeQuery(mSBql.toString());
            if (!tResultSet.next())
            {
                System.out.println("�Ҳ�����ӡ����,SQLΪ��" + mSBql.toString());
                tResultSet.close();
                tStatement.close();
                return null;
            }
            //��ȡBlob������Ϣ
            tBlob = tResultSet.getBlob(1);
            tResultSet.close();
            tStatement.close();
        }
        catch (Exception ex)
        {
            //������
            ex.printStackTrace();
            try
            {
                //�رո�������
                if (tResultSet != null)
                {
                    tResultSet.close();
                }
                if (tStatement != null)
                {
                    tStatement.close();
                }
            }
            catch (Exception ex1)
            {
            }
            return null;
        }
        return tBlob;
    }


    public static void BlobDelete()
    {
        if (SysConst.DBTYPE.compareTo("DB2") == 0)
        {

        }
        else
        {

        }
    }

    public static void main(String[] args)
    {
//        CBlob cblob = new CBlob();
    }
}

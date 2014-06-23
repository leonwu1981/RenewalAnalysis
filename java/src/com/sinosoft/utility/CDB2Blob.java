/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.jdom.Document;

/**
 * <p>Title: </p>
 * Blob�������ݲ���
 * <p>Description: </p>
 * �˷������DB2���ݿ�
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author �����
 * @version 1.0
 */
public class CDB2Blob
{
    //ȫ���ַ�������
    private StringBuffer mSBql = new StringBuffer(256);

    public CDB2Blob()
    {
    }

    /**
     * �޸�ĳ�����Blob�ֶ�
     * @param pInXmlDoc Document
     * @param pTabName String
     * @param pUpdateField String
     * @param pGrpPolNo String
     * @param pConn Connection
     * @return boolean
     */
    public boolean UpdateBlob(Document pInXmlDoc, String pTabName,
                              String pUpdateField, String pGrpPolNo,
                              Connection pConn)
    {
        PreparedStatement preparedStatement = null;

        Statement stmt = null;
        if (pConn == null)
        {
            //�������û�д��룬�򷵻�false
            System.out.println("CDB2Blobû�д������ӣ�");
            return false;
        }
        try
        {
            stmt = pConn.createStatement();
            String szSQL = "SELECT " + pUpdateField + " FROM " + pTabName
                           + " WHERE MainPolNo = '" + pGrpPolNo +
                           "' FOR UPDATE";
            System.out.println("UpdateBlob :" + szSQL);
            preparedStatement = pConn.prepareStatement(szSQL);
            preparedStatement.executeQuery();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.out.print(ex.toString());
            try
            {
                if (stmt != null)
                {
                    stmt.close();
                }
            }
            catch (Exception ex1)
            {
            }
            return false;
        }
        return true;
    }

    /**
     * �޸�ĳ�����Blob�ֶ�
     * @param pInStream InputStream
     * @param pTabName String
     * @param pUpdateField String
     * @param pWhereSQL String
     * @param pConn Connection
     * @return boolean
     */
    public boolean UpdateBlob(InputStream pInStream, String pTabName,
                              String pUpdateField, String pWhereSQL,
                              Connection pConn)
    {
        Statement stmt = null;
        ResultSet rs = null;
        if (pConn == null)
        {
            //�������û�д��룬�򷵻�false
            System.out.println("COracleBlobû�д������ӣ�");
            return false;
        }
        try
        {
            // �õ������������
            stmt = pConn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                                         ResultSet.CONCUR_UPDATABLE);
//            String szSQL = "SELECT " + pUpdateField + " FROM " + pTabName +
//                           " WHERE 1=1  " + pWhereSQL;
            mSBql.append("SELECT ");
            mSBql.append(pUpdateField);
            mSBql.append(" FROM ");
            mSBql.append(pTabName);
            mSBql.append(" WHERE 1=1 ");
            mSBql.append(pWhereSQL);

            rs = stmt.executeQuery(mSBql.toString());
            if (!rs.next())
            {
                System.out.println("COracleBlob�ò�ѯ����û�в�ѯ����¼��SQLΪ��" +
                                   mSBql.toString());
                rs.close();
                stmt.close();
                return false;
            }
            rs.close();

//            String spSQL = "UPDATE " + pTabName + " Set " + pUpdateField +
//                           " =? WHERE 1=1  " + pWhereSQL;
            mSBql = new StringBuffer(256);

            mSBql.append("UPDATE ");
            mSBql.append(pTabName);
            mSBql.append(" Set ");
            mSBql.append(pUpdateField);
            mSBql.append(" =? WHERE 1=1 ");
            mSBql.append(pWhereSQL);

            PreparedStatement ps = pConn.prepareStatement(mSBql.toString());
//            System.out.println(pInStream.available());
            ps.setBinaryStream(1, pInStream, pInStream.available());
            ps.execute();

            ps.close();
            stmt.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (stmt != null)
                {
                    stmt.close();
                }
            }
            catch (Exception ex1)
            {}
            return false;
        }
        return true;
    }

    /**
     * ɾ��Blob��¼
     * @param pDeleteSQL String
     * @param pConn Connection
     * @return boolean
     */
    public boolean DeleteBlobRecord(String pDeleteSQL, Connection pConn)
    {
        // TODO: implement
        Statement stmt = null;
        if (pConn == null)
        {
            //�������û�д��룬�򷵻�false
            System.out.println("CDB2Blobû�д������ӣ�");
            return false;
        }
        try
        {
            // �õ������������
            stmt = pConn.createStatement();
            String sDeleteSQL = pDeleteSQL.replaceFirst("DELETE",
                    "SELECT MAINPOLNO");
            ResultSet rs = stmt.executeQuery(sDeleteSQL);
            if (rs.next())
            {
                stmt.executeUpdate(pDeleteSQL);
            }
            stmt.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            try
            {
                if (stmt != null)
                {
                    stmt.close();
                }
            }
            catch (Exception ex1)
            {
            }
            return false;
        }
        return true;
    }

    /**
     * ����Blob��¼
     * @param pis InputStream
     * @param pInsertSQL String
     * @param pConn Connection
     * @return boolean
     */
    public boolean InsertBlankBlobRecord(InputStream pis, String pInsertSQL,
                                         Connection pConn)
    {
        // TODO: implement
        Statement stmt = null;
        if (pConn == null)
        {
            //�������û�д��룬�򷵻�false
            System.out.println("CDB2Blobû�д������ӣ�");
            return false;
        }
        try
        {
            System.out.println("DB2���ݿ�Blob�������sql��" + pInsertSQL);
            PreparedStatement ps = pConn.prepareStatement(pInsertSQL);
            ps.setBinaryStream(1, pis, pis.available());
            ps.execute();
            ps.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            try
            {
                stmt.close();
            }
            catch (Exception ex1)
            {
            }
            return false;
        }
        return true;
    }

    /**
     * ��ȡָ��SQL����е�ĳ��Blob�ֶε�һ��OutStream�С�
     * @param pOutStream OutputStream
     * @param pTabName String
     * @param pSelectField String
     * @param pWhereSQL String
     * @param pConn Connection
     * @return boolean
     */
    public boolean SelectBlob(OutputStream pOutStream, String pTabName,
                              String pSelectField, String pWhereSQL,
                              Connection pConn)
    {
        return false;
    }

    /**
     * ��ȡָ��SQL����е�ĳ��Blob�ֶε�һ��OutStream�С�
     * @param pTabName String
     * @param pSelectField String
     * @param pWhereSQL String
     * @param pConn Connection
     * @return Blob
     */
    public Blob SelectBlob(String pTabName, String pSelectField,
                           String pWhereSQL, Connection pConn)
    {
        // TODO: implement
        Statement stmt = null;
        ResultSet rs = null;
        Blob tOutData = null;

        if (pConn == null)
        {
            //�������û�д��룬�򷵻�false
            System.out.println("CDB2Blobû�д������ӣ�");
            return null;
        }
        try
        {
            stmt = pConn.createStatement();
//            String szSQL;
//            szSQL = "SELECT " + pSelectField + " FROM " + pTabName
//                    + " WHERE 1=1 " + pWhereSQL;

            mSBql.append("SELECT ");
            mSBql.append(pSelectField);
            mSBql.append(" FROM ");
            mSBql.append(pTabName);
            mSBql.append(" WHERE 1=1 ");
            mSBql.append(pWhereSQL);

            rs = stmt.executeQuery(mSBql.toString());
            if (!rs.next())
            {
                System.out.println("�Ҳ�����ӡ����,SQLΪ��" + mSBql.toString());
                rs.close();
                stmt.close();
                return null;
            }
            tOutData = rs.getBlob(1);
            rs.close();
            stmt.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (stmt != null)
                {
                    stmt.close();
                }
            }
            catch (Exception ex1)
            {
            }
            return null;
        }
        return tOutData;
    }
}

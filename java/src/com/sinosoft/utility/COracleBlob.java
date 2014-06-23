/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;

/*
 * <p>ClassName:  COracleBlob</p>
 * <p>Description: Oracle���ݿ�Blob�ֶβ������ļ� </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft </p>
 * @Database: YT
 * @CreateDate��2003-12-25
 */
public class COracleBlob
{
    //ȫ���ַ�������
    private StringBuffer mSBql = new StringBuffer(256);

    //ҵ������ر���
    public COracleBlob()
    {
    }

    /**
     * �޸�ĳ�����Blob�ֶ�
     * @param pInXmlDoc Document
     * @param pTabName String
     * @param pUpdateField String
     * @param pWhereSQL String
     * @param pConn Connection
     * @return boolean
     */
    public boolean UpdateBlob(Document pInXmlDoc, String pTabName,
                              String pUpdateField, String pWhereSQL,
                              Connection pConn)
    {
        // TODO: implement
        Statement stmt = null;
        ResultSet rs = null;
        if (pConn == null) //�������û�д��룬�򷵻�false
        {
            System.out.println("COracleBlobû�д������ӣ�");
            return false;
        }
        try
        {
            // �õ������������
            stmt = pConn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                                         ResultSet.CONCUR_UPDATABLE);
//            String szSQL = "SELECT " + pUpdateField + " FROM " + pTabName +
//                           " WHERE 1=1  " + pWhereSQL + " FOR UPDATE";
            mSBql.append("SELECT ");
            mSBql.append(pUpdateField);
            mSBql.append(" FROM ");
            mSBql.append(pTabName);
            mSBql.append(" WHERE 1=1 ");
            mSBql.append(pWhereSQL);
            mSBql.append(" FOR UPDATE");

            rs = stmt.executeQuery(mSBql.toString());
            if (!rs.next())
            {
                System.out.println("COracleBlob�ò�ѯ����û�в�ѯ����¼��SQLΪ��" +
                                   mSBql.toString());
                rs.close();
                stmt.close();
                return false;
            }

            Blob blob = rs.getBlob(pUpdateField);
            OutputStream os = ((oracle.sql.BLOB) blob).getBinaryOutputStream();

            XmlExport xmlexport = new XmlExport(); //�½�һ��XmlExport��ʵ��

            XMLOutputter outputter = new XMLOutputter("", false, "UTF-8");
//      XMLOutputter outputter = new XMLOutputter("", false, "GBK");

//      changeXml.displayDocument(pInXmlDoc.getRootElement());

            outputter.output(pInXmlDoc, os);

            os.flush();
            os.close();

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
            {}
            return false;
        }
        return true;
    }

    /**
     *  �޸�ĳ�����Blob�ֶ�
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
        // TODO: implement
        Statement stmt = null;
        ResultSet rs = null;
        if (pConn == null) //�������û�д��룬�򷵻�false
        {
            System.out.println("COracleBlobû�д������ӣ�");
            return false;
        }
        try
        {
            // �õ������������
            stmt = pConn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                                         ResultSet.CONCUR_UPDATABLE);
//            String szSQL = "SELECT " + pUpdateField + " FROM " + pTabName +
//                           " WHERE 1=1  " + pWhereSQL + " FOR UPDATE";
            mSBql.append("SELECT ");
            mSBql.append(pUpdateField);
            mSBql.append(" FROM ");
            mSBql.append(pTabName);
            mSBql.append(" WHERE 1=1 ");
            mSBql.append(pWhereSQL);
            mSBql.append(" FOR UPDATE");

            rs = stmt.executeQuery(mSBql.toString());
            if (!rs.next())
            {
                System.out.println("COracleBlob�ò�ѯ����û�в�ѯ����¼��SQLΪ��" +
                                   mSBql.toString());
                rs.close();
                stmt.close();
                return false;
            }

            java.sql.Blob blob = rs.getBlob(1);
            OutputStream os = ((oracle.sql.BLOB) blob).getBinaryOutputStream();
            InputStream ins = pInStream;
            int inData = 0;
            while ((inData = ins.read()) != -1)
            {
                os.write(inData);
            }

            os.flush();
            os.close();

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
            {}
            return false;
        }
        return true;
    }

    /**
     * ɾ����¼
     * @param pDeleteSQL String
     * @param pConn Connection
     * @return boolean
     */
    public boolean DeleteBlobRecord(String pDeleteSQL, Connection pConn)
    {
        // TODO: implement
        Statement stmt = null;
        if (pConn == null) //�������û�д��룬�򷵻�false
        {
            System.out.println("COracleBlobû�д������ӣ�");
            return false;
        }
        try
        {
            // �õ������������
            stmt = pConn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                                         ResultSet.CONCUR_UPDATABLE);
            String szSQL = pDeleteSQL;
            stmt.executeUpdate(szSQL);
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
            {}
            return false;
        }
        return true;
    }

    /**
     * ������¼
     * @param pInsertSQL String
     * @param pConn Connection
     * @return boolean
     */
    public boolean InsertBlankBlobRecord(String pInsertSQL, Connection pConn)
    {
        // TODO: implement
        Statement stmt = null;
        if (pConn == null) //�������û�д��룬�򷵻�false
        {
            System.out.println("COracleBlobû�д������ӣ�");
            return false;
        }
        try
        {
            // �õ������������
            stmt = pConn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                                         ResultSet.CONCUR_UPDATABLE);
            String szSQL = pInsertSQL;
            stmt.executeUpdate(szSQL);
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
            {}
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
        // TODO: implement
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
        if (pConn == null) //�������û�д��룬�򷵻�false
        {
            System.out.println("COracleBlobû�д������ӣ�");
            return null;
        }
        try
        {
            // �õ������������
            stmt = pConn.createStatement();
//            String szSQL;
//            szSQL = "SELECT " + pSelectField + " FROM " + pTabName +
//                    " WHERE 1=1 " + pWhereSQL;
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

            tOutData = rs.getBlob(pSelectField);
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
            {}
            return null;
        }
        return tOutData;
    }

    /**
     * ���Ժ���
     * @param args String[]
     */
    public static void main(String[] args)
    {
//        Connection conn = DBConnPool.getConnection();
//        COracleBlob blob = new COracleBlob();
//        Blob b = blob.SelectBlob("LCUWReport", "CONTENTE",
//                                 " and polno='86110020030110011412' ", conn);
//        XmlFun.displayBlob(b);

//    Blob b = blob.SelectBlob("LPEdorPrint", "edorinfo", " and edorno='86110020040430000007' ", conn);
//    String t="�����ո���Ϊ5��ְҵ�������ְҵ�ӷѣ���ͻ���ϵ��ͬ��ӷѡ���ʾ��ϯ�����Ǳ���ϵͣ�����С���ֹ�˾����չ���꿪�ź���ͬ�ⲻ���ӷѣ�ͨ�ڳб�";
//    ByteArrayInputStream is = new ByteArrayInputStream(t.getBytes());
//    String tSQL = "insert into lcuwreport values('86320020040210000245','000009','1','86',empty_blob(),'2004-1-17','17:00:00','2004-1-17','17:00:00')";
//    try {
//      conn.setAutoCommit(false) ;
//      blob.InsertBlankBlobRecord(tSQL,conn);
//      blob.UpdateBlob(is,"LCUWReport","CONTENTE"," and polno='86320020040210000245'",conn);
//      conn.commit();
//    }
//    catch (Exception ex) {
//    }
//    try {
//      InputStream ins = b.getBinaryStream();
//      DataInputStream in = new DataInputStream(ins);
//      String inData = "";
//      while( (inData = in.readLine()).length() != 0) {
//        System.out.println(inData);
//      }
//    }
//    catch (Exception ex) {
//    }
    }
}

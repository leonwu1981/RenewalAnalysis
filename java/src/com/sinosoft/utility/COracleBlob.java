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
 * <p>Description: Oracle数据库Blob字段操作类文件 </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft </p>
 * @Database: YT
 * @CreateDate：2003-12-25
 */
public class COracleBlob
{
    //全局字符串变量
    private StringBuffer mSBql = new StringBuffer(256);

    //业务处理相关变量
    public COracleBlob()
    {
    }

    /**
     * 修改某个表的Blob字段
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
        if (pConn == null) //如果连接没有寸入，则返回false
        {
            System.out.println("COracleBlob没有传入连接！");
            return false;
        }
        try
        {
            // 得到数据输出对象
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
                System.out.println("COracleBlob该查询条件没有查询到记录！SQL为：" +
                                   mSBql.toString());
                rs.close();
                stmt.close();
                return false;
            }

            Blob blob = rs.getBlob(pUpdateField);
            OutputStream os = ((oracle.sql.BLOB) blob).getBinaryOutputStream();

            XmlExport xmlexport = new XmlExport(); //新建一个XmlExport的实例

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
     *  修改某个表的Blob字段
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
        if (pConn == null) //如果连接没有寸入，则返回false
        {
            System.out.println("COracleBlob没有传入连接！");
            return false;
        }
        try
        {
            // 得到数据输出对象
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
                System.out.println("COracleBlob该查询条件没有查询到记录！SQL为：" +
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
     * 删除记录
     * @param pDeleteSQL String
     * @param pConn Connection
     * @return boolean
     */
    public boolean DeleteBlobRecord(String pDeleteSQL, Connection pConn)
    {
        // TODO: implement
        Statement stmt = null;
        if (pConn == null) //如果连接没有寸入，则返回false
        {
            System.out.println("COracleBlob没有传入连接！");
            return false;
        }
        try
        {
            // 得到数据输出对象
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
     * 新增记录
     * @param pInsertSQL String
     * @param pConn Connection
     * @return boolean
     */
    public boolean InsertBlankBlobRecord(String pInsertSQL, Connection pConn)
    {
        // TODO: implement
        Statement stmt = null;
        if (pConn == null) //如果连接没有寸入，则返回false
        {
            System.out.println("COracleBlob没有传入连接！");
            return false;
        }
        try
        {
            // 得到数据输出对象
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
     * 读取指定SQL语句中的某个Blob字段到一个OutStream中。
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
     * 读取指定SQL语句中的某个Blob字段到一个OutStream中。
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
        if (pConn == null) //如果连接没有寸入，则返回false
        {
            System.out.println("COracleBlob没有传入连接！");
            return null;
        }
        try
        {
            // 得到数据输出对象
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
                System.out.println("找不到打印数据,SQL为：" + mSBql.toString());
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
     * 测试函数
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
//    String t="被保险个人为5类职业，需进行职业加费，与客户联系不同意加费。请示首席，考虑保额较低，风险小，分公司正开展新年开门红活动，同意不做加费，通融承保";
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

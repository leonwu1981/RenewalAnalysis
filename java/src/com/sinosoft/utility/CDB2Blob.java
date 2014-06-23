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
 * Blob类型数据操作
 * <p>Description: </p>
 * 此方法针对DB2数据库
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author 朱向峰
 * @version 1.0
 */
public class CDB2Blob
{
    //全局字符串变量
    private StringBuffer mSBql = new StringBuffer(256);

    public CDB2Blob()
    {
    }

    /**
     * 修改某个表的Blob字段
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
            //如果连接没有传入，则返回false
            System.out.println("CDB2Blob没有传入连接！");
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
     * 修改某个表的Blob字段
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
            //如果连接没有传入，则返回false
            System.out.println("COracleBlob没有传入连接！");
            return false;
        }
        try
        {
            // 得到数据输出对象
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
                System.out.println("COracleBlob该查询条件没有查询到记录！SQL为：" +
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
     * 删除Blob记录
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
            //如果连接没有传入，则返回false
            System.out.println("CDB2Blob没有传入连接！");
            return false;
        }
        try
        {
            // 得到数据输出对象
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
     * 插入Blob记录
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
            //如果连接没有传入，则返回false
            System.out.println("CDB2Blob没有传入连接！");
            return false;
        }
        try
        {
            System.out.println("DB2数据库Blob插入操作sql：" + pInsertSQL);
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

        if (pConn == null)
        {
            //如果连接没有传入，则返回false
            System.out.println("CDB2Blob没有传入连接！");
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
                System.out.println("找不到打印数据,SQL为：" + mSBql.toString());
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

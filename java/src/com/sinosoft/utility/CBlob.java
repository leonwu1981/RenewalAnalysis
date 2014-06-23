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
 * Blob数据操作中转类
 * <p>Description: </p>
 * 用来统一化Blob类型的数据操作
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
     * BLOB添加操作
     * @param pIns InputStream
     * @param pStr String[]
     * @param pConn Connection
     * @return boolean
     */
    public static boolean BlobInsert(InputStream pIns, String[] pStr, Connection pConn)
    {
        StringBuffer mSBql = new StringBuffer(256);
        //判定系统使用的数据库类型
        //System.out.println(pStr[0]);
        if (SysConst.DBTYPE.compareTo("DB2") == 0)
        {
            //如果是db2数据库，则需要替换掉oracle特有函数empty_blob()
//            String tSql = pStr[0].substring(0, pStr[0].indexOf("empty_blob()"));
//            tSql = tSql + "?" +
//                   pStr[0].substring(pStr[0].indexOf("empty_blob()") + 12,pStr[0].length());
            mSBql.append(pStr[0].substring(0, pStr[0].indexOf("empty_blob()")));
            mSBql.append("?");
            mSBql.append(pStr[0].substring(pStr[0].indexOf("empty_blob()") + 12,pStr[0].length()));

            //调用db2下的blob操作
            CDB2Blob tCDB2Blob = new CDB2Blob();
            if (!tCDB2Blob.InsertBlankBlobRecord(pIns, mSBql.toString(), pConn))
            {
                //如果执行失败，则返回false
                return false;
            }
        }
        else
        {
            //调用oralce下的blob操作
            COracleBlob tCOracleBlob = new COracleBlob();
            if (!tCOracleBlob.InsertBlankBlobRecord(pStr[0], pConn))
            {
                //如果执行失败，则返回false
                return false;
            }
            if (!tCOracleBlob.UpdateBlob(pIns, pStr[1], pStr[2], pStr[3], pConn))
            {
                //如果执行失败，则返回false
                return false;
            }
        }
        //无异常返回true
        return true;
    }

    /**
     * BLOB更新操作
     * @param pIns InputStream
     * @param pStr String[]
     * @param pConn Connection
     * @return boolean
     */
    public static boolean BlobUpdate(InputStream pIns, String[] pStr, Connection pConn)
    {
        if (SysConst.DBTYPE.compareTo("DB2") == 0)
        {
            //调用db2下的blob操作
            CDB2Blob tCDB2Blob = new CDB2Blob();
            if (!tCDB2Blob.UpdateBlob(pIns, pStr[1], pStr[2], pStr[3], pConn))
            {
                //如果执行失败，则返回false
                return false;
            }
        }
        else
        {
            //调用oracle的blob操作
            COracleBlob tCOracleBlob = new COracleBlob();
            if (!tCOracleBlob.UpdateBlob(pIns, pStr[1], pStr[2], pStr[3], pConn))
            {
                //如果执行失败，则返回false
                return false;
            }
        }
        return true;
    }

    /**
     * 读取指定SQL语句中的某个Blob字段到一个OutStream中。
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
            //如果连接没有寸入，则返回false
            System.out.println("CBlob没有传入连接！");
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
                System.out.println("找不到打印数据,SQL为：" + mSBql.toString());
                tResultSet.close();
                tStatement.close();
                return null;
            }
            //获取Blob数据信息
            tBlob = tResultSet.getBlob(1);
            tResultSet.close();
            tStatement.close();
        }
        catch (Exception ex)
        {
            //出错处理
            ex.printStackTrace();
            try
            {
                //关闭各种连接
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

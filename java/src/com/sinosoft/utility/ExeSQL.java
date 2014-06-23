/**
 * Copyright (c) 2002 sinosoft Co. Ltd. All right reserved.
 */
package com.sinosoft.utility;

import java.sql.*;

import com.sinosoft.lis.pubfun.FDate;
import com.sinosoft.lis.pubfun.PubFun;
import com.sinosoft.utility.treetable.DataAccess;

/**
 * <p>
 * ClassName: ExeSQL
 * </p>
 * <p>
 * Description: DB层数据库操作类文件
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: sinosoft
 * </p>
 * 
 * @Database: LIS
 * @CreateDate：2002-07-11
 */
public class ExeSQL {
    private Connection con;

    /**
     * mflag = true: 传入Connection mflag = false: 不传入Connection
     */
    private boolean mflag = false;

    private FDate fDate = new FDate();

    public CErrors mErrors = new CErrors(); // 错误信息

    // @Constructor
    public ExeSQL(Connection tConnection) {
        con = tConnection;
        mflag = true;
    }

    public ExeSQL() {
    }

    /**
     * 获取唯一的返回值
     * 
     * @param sql
     *            String
     * @return String
     */
    public String getOneValue(String sql) {
    	long t0 = System.currentTimeMillis();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String mValue = "";
        // System.out.println("ExecSQL : " + sql);

        // add by yt，如果没有传入连接，则类创建
        if (!mflag) {
            con = DBConnPool.getConnection();
        }
        while (con==null) {
        	con = DBConnPool.getConnection();
        }

        try {
            pstmt = con.prepareStatement(StrTool.GBKToUnicode(sql), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            
            rs = pstmt.executeQuery();
            while (rs.next()) {
                // 其实并不是很合适，主要是因为有可能取得对象的数据类型有误
                mValue = rs.getString(1);
                break;
            }
            
            rs.close();
            pstmt.close();
            // 如果连接是类创建的，则关闭连接
            if (!mflag) {
                con.close();
            }
        } catch (SQLException e) {
            // @@错误处理
            System.out.println("### Error ExeSQL at getOneValue: " + sql);
            mErrors.clearErrors();
            CError.buildErr(this, e.toString());
            // 设置返回值
            mValue = "";
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // 由于描述的问题，导致执行的sql错误百出，因此pstmt的关闭需要特殊处理
                    try {
                        pstmt.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        pstmt.close();
                    }
                }
                if (!mflag) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            // 强制回收
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // 由于描述的问题，导致执行的sql错误百出，因此pstmt的关闭需要特殊处理
                    try {
                        pstmt.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        pstmt.close();
                    }
                }
                if (!mflag) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
//        long t1 = System.currentTimeMillis();
//        if( t1-t0 > 500 && t1-t0 <= 1000 ){
//        	System.out.println("警告：执行时间超过500毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
//        }else if(t1-t0 > 1000 && t1-t0 <=5000){
//        	System.out.println("严重警告：执行时间超过1000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
//        }else if(t1-t0 > 5000 && t1-t0 <=10000){
//        	System.out.println("严重警告：执行时间超过5000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
//        }else if(t1-t0 > 10000 && t1-t0 <=50000){
//        	System.out.println("严重警告：执行时间超过10000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
//        }else if(t1-t0 > 50000 && t1-t0 <=100000){
//        	System.out.println("严重警告：执行时间超过50000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
//        }else if(t1-t0 > 100000){
//        	System.out.println("严重警告：执行时间超过100000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
//        }
        return StrTool.cTrim(mValue);
    }

    /**
     * 获取SQL的查询结果记录数 备注：由于这个方法只会在内部调用，因此无需在此处关闭创建的连接。如果失败或者异常，返回的结果为0，会在调用的地方关闭连接。
     * 
     * @param sql
     *            String
     * @param pstmt
     *            PreparedStatement
     * @param rs
     *            ResultSet
     * @return int
     */
    private int getResultCount(String sql, PreparedStatement pstmt, ResultSet rs) {
    	long t0 = System.currentTimeMillis();
        int iCount = 0;
        // 此方法对不同数据库通用
        sql = "select count(1) from (" + sql + ") rsc";
        System.out.println("getResultCount : " + sql);

        try {
            pstmt = con.prepareStatement(StrTool.GBKToUnicode(sql), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = pstmt.executeQuery();
            // rs.next();
            // 这样可以保证，没有查询到数据的时候，也返回正常
            while (rs.next()) {
                iCount = rs.getInt(1);
                break;
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // @@错误处理
            System.out.println("### Error ExeSQL at getResultCount: " + sql);
            CError.buildErr(this, e.toString());
            iCount = 0;
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // 由于描述的问题，导致执行的sql错误百出，因此pstmt的关闭需要特殊处理
                    try {
                        pstmt.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        pstmt.close();
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                // 可能出现连接没有关闭
            } finally {
                // 强制回收
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (pstmt != null) {
                        // 由于描述的问题，导致执行的sql错误百出，因此pstmt的关闭需要特殊处理
                        try {
                            pstmt.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        } finally {
                            pstmt.close();
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        long t1 = System.currentTimeMillis();
        if( t1-t0 > 500 && t1-t0 <= 1000 ){
        	System.out.println("警告：执行时间超过500毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 1000 && t1-t0 <=5000){
        	System.out.println("严重警告：执行时间超过1000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 5000 && t1-t0 <=10000){
        	System.out.println("严重警告：执行时间超过5000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 10000 && t1-t0 <=50000){
        	System.out.println("严重警告：执行时间超过10000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 50000 && t1-t0 <=100000){
        	System.out.println("严重警告：执行时间超过50000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 100000){
        	System.out.println("严重警告：执行时间超过100000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }
        return iCount;
    }

    /**
     * 从指定位置查询全部数据
     * 
     * @param sql
     *            String
     * @param start
     *            int
     * @return String
     */
    public String getEncodedResult(String sql, int start) {
        long t0 = System.currentTimeMillis();
    	PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        StringBuffer mResult = new StringBuffer(256); // modified by liuqiang
        int rowCount = 0,getCount = 0;
        // System.out.println("ExecSQL : " + sql);

        // add by yt
        if (!mflag) {
            con = DBConnPool.getConnection();
        }

        try {
            pstmt = con.prepareStatement(StrTool.GBKToUnicode(sql), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = pstmt.executeQuery();
            rsmd = rs.getMetaData();
            // 查询字段的个数
            int n = rsmd.getColumnCount();
            // 查询记录的数量
            int m = start + (SysConst.MAXSCREENLINES * SysConst.MAXMEMORYPAGES);

            // 取得总记录数
            int k = 0;
            while (rs.next()) {
                k++;
                if ((k >= start) && (k < m)) {
                    // only get record we needed
                    for (int j = 1; j <= n; j++) {
                    	getCount ++;
                        if (j == 1) {
                            mResult.append(getDataValue(rsmd, rs, j));
                        } else {
                            mResult.append(SysConst.PACKAGESPILTER + getDataValue(rsmd, rs, j));
                        }
                    }
                    mResult.append(SysConst.RECORDSPLITER);
                }
            }
            
            rowCount = k;

            if (k >= start) {
                if (k > 1000) {
                    System.out.println("****** 建议采用大批量数据查询模式 ****** "+ sql);
                }
                // "0|"为查询成功标记，与CODEQUERY统一，MINIM修改
                mResult.insert(0, "0|" + String.valueOf(k) + SysConst.RECORDSPLITER);
                mResult.delete(mResult.length() - 1, mResult.length());
            } else {
                mResult.append("100|未查询到相关数据!");
            }
            rs.close();
            pstmt.close();
            if (!mflag) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // @@错误处理
            System.out.println("##### Error Sql in ExeSQL at getEncodedResult(String sql, int start): " + sql);
            mErrors.clearErrors();
            CError.buildErr(this, e.toString());
            mResult.setLength(0);
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // 由于描述的问题，导致执行的sql错误百出，因此pstmt的关闭需要特殊处理
                    try {
                        pstmt.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        pstmt.close();
                    }
                }
                if (!mflag) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            // 强制回收
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // 由于描述的问题，导致执行的sql错误百出，因此pstmt的关闭需要特殊处理
                    try {
                        pstmt.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        pstmt.close();
                    }
                }
                if (!mflag) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        long t1 = System.currentTimeMillis();
        if( t1-t0 > 500 && t1-t0 <= 1000 ){
        	System.out.println("警告：执行时间超过500毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 1000 && t1-t0 <=5000){
        	System.out.println("严重警告：执行时间超过1000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 5000 && t1-t0 <=10000){
        	System.out.println("严重警告：执行时间超过5000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 10000 && t1-t0 <=50000){
        	System.out.println("严重警告：执行时间超过10000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 50000){
        	System.out.println("严重警告：执行时间超过100000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }
        if(rowCount>200 && rowCount<=500){
        	System.out.println("SQL结果条数超过200，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }else if(rowCount>500 && rowCount<=1000){
        	System.out.println("SQL结果条数超过500，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }else if(rowCount>1000 && rowCount<=5000){
        	System.out.println("SQL结果条数超过1000，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }else if(rowCount>5000){
        	System.out.println("SQL结果条数超过5000，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }
        return mResult.toString();
    }

    /**
     * 从指定位置查询全部数据
     * 
     * @param sql
     *            String
     * @param start
     *            int
     * @return String
     */
    public String getEncodedResultAll(String sql, int start) {
        long t0 = System.currentTimeMillis();
    	PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        StringBuffer mResult = new StringBuffer(256); // modified by liuqiang
        int rowCount = 0,getCount = 0;
        // System.out.println("ExecSQL : " + sql);

        // add by yt
        if (!mflag) {
            con = DBConnPool.getConnection();
        }

        try {
            pstmt = con.prepareStatement(StrTool.GBKToUnicode(sql), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = pstmt.executeQuery();
            rsmd = rs.getMetaData();
            // 查询字段的个数
            int n = rsmd.getColumnCount();
            // 查询记录的数量
            int m = start + (SysConst.MAXSCREENLINES * SysConst.MAXMEMORYPAGES*1000000);//20100702此方法做为大数据量查询，查询结果为全部

            // 取得总记录数
            int k = 0;
            while (rs.next()) {
                k++;
                if ((k >= start) && (k < m)) {
                    // only get record we needed
                    for (int j = 1; j <= n; j++) {
                    	getCount ++;
                        if (j == 1) {
                            mResult.append(getDataValue(rsmd, rs, j));
                        } else {
                            mResult.append(SysConst.PACKAGESPILTER + getDataValue(rsmd, rs, j));
                        }
                    }
                    mResult.append(SysConst.RECORDSPLITER);
                }
            }
            
            rowCount = k;

            if (k >= start) {
                if (k > 1000) {
                    System.out.println("****** 建议采用大批量数据查询模式 ****** "+ sql);
                }
                // "0|"为查询成功标记，与CODEQUERY统一，MINIM修改
                mResult.insert(0, "0|" + String.valueOf(k) + SysConst.RECORDSPLITER);
                mResult.delete(mResult.length() - 1, mResult.length());
            } else {
                mResult.append("100|未查询到相关数据!");
            }
            rs.close();
            pstmt.close();
            if (!mflag) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // @@错误处理
            System.out.println("##### Error Sql in ExeSQL at getEncodedResult(String sql, int start): " + sql);
            mErrors.clearErrors();
            CError.buildErr(this, e.toString());
            mResult.setLength(0);
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // 由于描述的问题，导致执行的sql错误百出，因此pstmt的关闭需要特殊处理
                    try {
                        pstmt.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        pstmt.close();
                    }
                }
                if (!mflag) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            // 强制回收
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // 由于描述的问题，导致执行的sql错误百出，因此pstmt的关闭需要特殊处理
                    try {
                        pstmt.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        pstmt.close();
                    }
                }
                if (!mflag) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        long t1 = System.currentTimeMillis();
        if( t1-t0 > 500 && t1-t0 <= 1000 ){
        	System.out.println("警告：执行时间超过500毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 1000 && t1-t0 <=5000){
        	System.out.println("严重警告：执行时间超过1000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 5000 && t1-t0 <=10000){
        	System.out.println("严重警告：执行时间超过5000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 10000 && t1-t0 <=50000){
        	System.out.println("严重警告：执行时间超过10000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 50000){
        	System.out.println("严重警告：执行时间超过100000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }
        if(rowCount>200 && rowCount<=500){
        	System.out.println("SQL结果条数超过200，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }else if(rowCount>500 && rowCount<=1000){
        	System.out.println("SQL结果条数超过500，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }else if(rowCount>1000 && rowCount<=5000){
        	System.out.println("SQL结果条数超过1000，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }else if(rowCount>5000){
        	System.out.println("SQL结果条数超过5000，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }
        return mResult.toString();
    }

    
    /**
     * 从指定位置查询全部数据，此方法为大数据量查询
     * 
     * @param sql
     *            String
     * @param start
     *            int
     * @return String
     */
    public String getEncodedResultLarge(String sql, int start) {
    	long t0 = System.currentTimeMillis();
    	int rowCount = 0,getCount = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        StringBuffer mResult = new StringBuffer(256); // modified by liuqiang

        // add by Fanym
        if (start <= 0) {
            start = 1;
        }

        // add by yt
        if (!mflag) {
            con = DBConnPool.getConnection();
        }

        int m = start + (SysConst.MAXSCREENLINES * SysConst.MAXMEMORYPAGES);

        int iCount = 1000000000;// getResultCount(sql, pstmt, rs);
        if (m >= iCount) {// 最后一页
            iCount = getResultCount(sql, pstmt, rs);
            if (iCount % (SysConst.MAXSCREENLINES * SysConst.MAXMEMORYPAGES) == 0) {
                start = (iCount - SysConst.MAXSCREENLINES * SysConst.MAXMEMORYPAGES) + 1;
                m = start + (SysConst.MAXSCREENLINES * SysConst.MAXMEMORYPAGES);
            } else {
                start = (iCount - iCount % (SysConst.MAXSCREENLINES * SysConst.MAXMEMORYPAGES)) + 1;
                m = start + (SysConst.MAXSCREENLINES * SysConst.MAXMEMORYPAGES);
            }
        }
        // 如果记录数为0，表示没有查询的数据，这个时候，需要关闭连接
        if (iCount <= 0) {
            try {
                if (!mflag) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (!mflag) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            // 直接返回，查询结果为空
            return "100|未查询到相关数据!";
        }

        try {
            // 查询记录的数量

            // 根据数据库，查询指定范围数据集，采用此方法可以大幅度提高前台的分页查询效率
            StringBuffer tSBql = new StringBuffer();
            if (SysConst.DBTYPE.equals("ORACLE")) {
                tSBql.append("select * from (select rownum rnm,rs.* from (");
                tSBql.append(sql);
                tSBql.append(") rs where rownum < ");
                tSBql.append(m);
                tSBql.append(") rss where rnm >= ");
                tSBql.append(start);
            } else {
                tSBql.append("select * from (select rownumber() OVER () rnm ,rs.* from (");
                tSBql.append(sql);
                tSBql.append(") rs) rss WHERE rnm BETWEEN ");
                tSBql.append(start);
                tSBql.append(" and ");
                tSBql.append(m - 1);
            }

            System.out.println("getEncodedResultLarge : " + tSBql.toString());

            pstmt = con.prepareStatement(StrTool.GBKToUnicode(tSBql.toString()), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

            rs = pstmt.executeQuery();
            rsmd = rs.getMetaData();
            // 查询字段的个数
            int n = rsmd.getColumnCount();

            int k = 0; // 用来判定是否有数据
            while (rs.next()) {
                k++;
                // 直接从位置2开始就ok了，呵呵，怎么没想到呢！！！
                for (int j = 2; j <= n; j++) {
                    if (j == 2) {
                        mResult.append(getDataValue(rsmd, rs, j));
                    } else {
                        mResult.append(SysConst.PACKAGESPILTER + getDataValue(rsmd, rs, j));
                    }
                }
                mResult.append(SysConst.RECORDSPLITER);
            }
            rowCount = k;
            getCount = k;
            if (k < SysConst.MAXSCREENLINES * SysConst.MAXMEMORYPAGES) {// 下一页不全
                iCount = start + k - 1;
            }

            if (k > 0) {
                // "0|"为查询成功标记，与CODEQUERY统一，MINIM修改
                mResult.insert(0, "0|" + String.valueOf(iCount) + SysConst.RECORDSPLITER);
                mResult.delete(mResult.length() - 1, mResult.length());
            } else {
                mResult.append("100|未查询到相关数据!");
            }
            rs.close();
            pstmt.close();
            if (!mflag) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // @@错误处理
            System.out.println("##### Error Sql in ExeSQL at getEncodedResultLarge(String sql, int start): " + sql);
            mErrors.clearErrors();
            CError.buildErr(this, e.toString());
            mResult.setLength(0);
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // 由于描述的问题，导致执行的sql错误百出，因此pstmt的关闭需要特殊处理
                    try {
                        pstmt.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        pstmt.close();
                    }
                }
                if (!mflag) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            // 强制回收
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // 由于描述的问题，导致执行的sql错误百出，因此pstmt的关闭需要特殊处理
                    try {
                        pstmt.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        pstmt.close();
                    }
                }
                if (!mflag) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        long t1 = System.currentTimeMillis();
        if( t1-t0 > 500 && t1-t0 <= 1000 ){
        	System.out.println("警告：执行时间超过500毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 1000 && t1-t0 <=5000){
        	System.out.println("严重警告：执行时间超过1000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 5000 && t1-t0 <=10000){
        	System.out.println("严重警告：执行时间超过5000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 10000 && t1-t0 <=50000){
        	System.out.println("严重警告：执行时间超过10000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 50000 && t1-t0 <=100000){
        	System.out.println("严重警告：执行时间超过50000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 100000){
        	System.out.println("严重警告：执行时间超过100000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }
        if(rowCount>200 && rowCount<=500){
        	System.out.println("SQL结果条数超过200，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }else if(rowCount>500 && rowCount<=1000){
        	System.out.println("SQL结果条数超过500，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }else if(rowCount>1000 && rowCount<=5000){
        	System.out.println("SQL结果条数超过1000，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }else if(rowCount>5000){
        	System.out.println("SQL结果条数超过5000，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }
        return mResult.toString();
    }

    /**
     * 查询数据
     * 
     * @param sql
     *            String
     * @return String
     */
    public String getEncodedResult(String sql) {
    	long t0 = System.currentTimeMillis();
    	int rowCount = 0,getCount = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        StringBuffer mResult = new StringBuffer(256); // modified by liuqiang

        // System.out.println("ExecSQL : " + sql);
        // add by yt
        if (!mflag) {
            con = DBConnPool.getConnection();
        }
        try {
            pstmt = con.prepareStatement(StrTool.GBKToUnicode(sql), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            // 重新设置缓冲区，主要采用此中方式的查询数据量在几千左右
            pstmt.setFetchSize(500);
            rs = pstmt.executeQuery();
            rs.setFetchSize(500);
            rsmd = rs.getMetaData();

            int n = rsmd.getColumnCount();

            int k = 0;
            // 取得总记录数
            while (rs.next()) {
                k++;
                for (int j = 1; j <= n; j++) {
                    if (j == 1) {
                        mResult.append(getDataValue(rsmd, rs, j));
                    } else {
                        mResult.append(SysConst.PACKAGESPILTER);
                        mResult.append(getDataValue(rsmd, rs, j));
                    }
                }
                mResult.append(SysConst.RECORDSPLITER);
            }
            rowCount = k;
            getCount = k;
            if (k > 0) {
                // "0|"为查询成功标记，与CODEQUERY统一，MINIM修改
                mResult.insert(0, "0|" + String.valueOf(k) + SysConst.RECORDSPLITER);
                mResult.delete(mResult.length() - 1, mResult.length());
            } else {
                mResult.append("100|未查询到相关数据！");
            }
            rs.close();
            pstmt.close();
            if (!mflag) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // @@错误处理
            System.out.println("##### Error Sql in ExeSQL at getEncodedResult(String sql): " + sql);
            mErrors.clearErrors();
            CError.buildErr(this, e.toString());
            mResult.setLength(0);
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // 由于描述的问题，导致执行的sql错误百出，因此pstmt的关闭需要特殊处理
                    try {
                        pstmt.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        pstmt.close();
                    }
                }
                if (!mflag) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            // 强制回收
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // 由于描述的问题，导致执行的sql错误百出，因此pstmt的关闭需要特殊处理
                    try {
                        pstmt.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        pstmt.close();
                    }
                }
                if (!mflag) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        long t1 = System.currentTimeMillis();
        if( t1-t0 > 500 && t1-t0 <= 1000 ){
        	System.out.println("警告：执行时间超过500毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 1000 && t1-t0 <=5000){
        	System.out.println("严重警告：执行时间超过1000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 5000 && t1-t0 <=10000){
        	System.out.println("严重警告：执行时间超过5000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 10000 && t1-t0 <=50000){
        	System.out.println("严重警告：执行时间超过10000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 50000 && t1-t0 <=100000){
        	System.out.println("严重警告：执行时间超过50000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 100000){
        	System.out.println("严重警告：执行时间超过100000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }
        if(rowCount>200 && rowCount<=500){
        	System.out.println("SQL结果条数超过200，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }else if(rowCount>500 && rowCount<=1000){
        	System.out.println("SQL结果条数超过500，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }else if(rowCount>1000 && rowCount<=5000){
        	System.out.println("SQL结果条数超过1000，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }else if(rowCount>5000){
        	System.out.println("SQL结果条数超过5000，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }
        return mResult.toString();
    }

    /**
     * 从指定位置查询定量数据
     * 
     * @param sql
     *            String
     * @param start
     *            int
     * @param nCount
     *            int
     * @return String
     */
    public String getEncodedResult(String sql, int start, int nCount) {
    	long t0 = System.currentTimeMillis();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        StringBuffer mResult = new StringBuffer(256); // modified by liuqiang
    	int rowCount = 0,getCount = 0;
        // System.out.println("ExecSQL : " + sql);
        // add by Fanym
        if (start <= 0) {
            start = 1;
        }
        if (nCount <= 0) {
            nCount = 1;
        }
        // add by yt
        if (!mflag) {
            con = DBConnPool.getConnection();
        }
        // 取得总记录数 add by liuqiang
        // int iCount = getResultCount(sql,pstmt,rs);
        // if (iCount <= 0) return "";
        try {
            pstmt = con.prepareStatement(StrTool.GBKToUnicode(sql), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

            rs = pstmt.executeQuery();
            rsmd = rs.getMetaData();
            int n = rsmd.getColumnCount();
            int m = start + nCount;
            // 取得总记录数
            int k = 0;
            while (rs.next()) {
                k++;
                // 如果超过要取的记录数，直接退出
                if (k >= m) {
                    break;
                }
                if ((k >= start) && (k < m)) {
                	getCount ++;
                    // only get record we needed
                    for (int j = 1; j <= n; j++) {
                        if (j == 1) {
                            mResult.append(getDataValue(rsmd, rs, j));
                        } else {
                            mResult.append(SysConst.PACKAGESPILTER).append(getDataValue(rsmd, rs, j));
                        }
                    }
                    mResult.append(SysConst.RECORDSPLITER);
                }
            }
            if (k > 1000) {
                System.out.println("****** 建议采用大批量数据查询模式 ****** "+ sql);
            }
            rowCount = k;
            if (k >= start) {
                // "0|"为查询成功标记，与CODEQUERY统一，MINIM修改
                mResult.insert(0, "0|" + String.valueOf(k) + SysConst.RECORDSPLITER);
                mResult.delete(mResult.length() - 1, mResult.length());
            } else {
                mResult.append("100|未查询到相关数据！");
            }
            rs.close();
            pstmt.close();
            if (!mflag) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // @@错误处理
            System.out.println("##### Error Sql in ExeSQL at getEncodedResult(String sql, int start, int nCount): "
                    + sql);
            mErrors.clearErrors();
            CError.buildErr(this, e.toString());
            mResult.setLength(0);
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // 由于描述的问题，导致执行的sql错误百出，因此pstmt的关闭需要特殊处理
                    try {
                        pstmt.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        pstmt.close();
                    }
                }
                if (!mflag) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            // 强制回收
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // 由于描述的问题，导致执行的sql错误百出，因此pstmt的关闭需要特殊处理
                    try {
                        pstmt.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        pstmt.close();
                    }
                }
                if (!mflag) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        long t1 = System.currentTimeMillis();
        if( t1-t0 > 500 && t1-t0 <= 1000 ){
        	System.out.println("警告：执行时间超过500毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 1000 && t1-t0 <=5000){
        	System.out.println("严重警告：执行时间超过1000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 5000 && t1-t0 <=10000){
        	System.out.println("严重警告：执行时间超过5000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 10000 && t1-t0 <=50000){
        	System.out.println("严重警告：执行时间超过10000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 50000 && t1-t0 <=100000){
        	System.out.println("严重警告：执行时间超过50000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 100000){
        	System.out.println("严重警告：执行时间超过100000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }
        if(rowCount>200 && rowCount<=500){
        	System.out.println("SQL结果条数超过200，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }else if(rowCount>500 && rowCount<=1000){
        	System.out.println("SQL结果条数超过500，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }else if(rowCount>1000 && rowCount<=5000){
        	System.out.println("SQL结果条数超过1000，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }else if(rowCount>5000){
        	System.out.println("SQL结果条数超过5000，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }
        return mResult.toString();
    }

    /**
     * 把ResultSet中取出的数据转换为相应的数据值字符串 输出：如果成功执行，返回True，否则返回False，并且在Error中设置错误的详细信息
     * 
     * @param rsmd
     *            ResultSetMetaData
     * @param rs
     *            ResultSet
     * @param i
     *            int
     * @return String
     */
    public String getDataValue(ResultSetMetaData rsmd, ResultSet rs, int i) {
        String strValue = "";

        try {
            int dataType = rsmd.getColumnType(i);
            int dataScale = rsmd.getScale(i);
            int dataPrecision = rsmd.getPrecision(i);
            // 数据类型为字符
            if ((dataType == Types.CHAR) || (dataType == Types.VARCHAR)) {
                // 由于存入数据库的数据是GBK模式，因此没有必要做一次unicodeToGBK
                // strValue = StrTool.unicodeToGBK(rs.getString(i));
                strValue = rs.getString(i);
            }
            // 数据类型为日期、时间
            else if ((dataType == Types.TIMESTAMP) || (dataType == Types.DATE)) {
                strValue = fDate.getString(rs.getDate(i));
            }
            // 数据类型为浮点
            else if ((dataType == Types.DECIMAL) || (dataType == Types.FLOAT)) {
                // strValue = String.valueOf(rs.getFloat(i));
                // 采用下面的方法使得数据输出的时候不会产生科学计数法样式
                strValue = String.valueOf(rs.getBigDecimal(i));
                // 去零处理
                strValue = PubFun.getInt(strValue);
            }
            // 数据类型为整型
            else if ((dataType == Types.INTEGER) || (dataType == Types.SMALLINT)) {
                strValue = String.valueOf(rs.getInt(i));
                strValue = PubFun.getInt(strValue);
            }
            // 数据类型为浮点
            else if (dataType == Types.NUMERIC) {
                if (dataScale == 0) {
                    if (dataPrecision == 0) {
                        // strValue = String.valueOf(rs.getDouble(i));
                        // 采用下面的方法使得数据输出的时候不会产生科学计数法样式
                        strValue = String.valueOf(rs.getBigDecimal(i));
                    } else {
                        strValue = String.valueOf(rs.getLong(i));
                    }
                } else {
                    // strValue = String.valueOf(rs.getDouble(i));
                    // 采用下面的方法使得数据输出的时候不会产生科学计数法样式
                    strValue = String.valueOf(rs.getBigDecimal(i));
                }
                strValue = PubFun.getInt(strValue);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return StrTool.cTrim(strValue);
    }

    /**
     * 输入：cSQL，在ExeSQL类初始化的时候建立连接。 输出：如果成功执行，返回True，否则返回False，并且在Error中设置错误的详细信息
     * 
     * @param sql
     *            String
     * @return boolean
     */
    public boolean execUpdateSQL(String sql) {
    	long t0 = System.currentTimeMillis();
		PreparedStatement pstmt = null;
		// System.out.println("ExecSQL : " + sql);

		// add by yt
		if (!mflag)
		{
			con = DBConnPool.getConnection();
		}

		try
		{
			// 这里是否可以修改，还需要测试一下
			pstmt = con.prepareStatement(StrTool.GBKToUnicode(sql), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

			pstmt.executeUpdate();
			// int operCount = pstmt.executeUpdate();
			pstmt.close();

			if (!mflag)
			{
				con.commit();
				con.close();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			// @@错误处理
			System.out.println("##### Error Sql in ExeSQL at execUpdateSQL: " + sql);
			mErrors.clearErrors();
			CError.buildErr(this, e.toString());

			try
			{
				if (pstmt != null)
				{
					// 由于描述的问题，导致执行的sql错误百出，因此pstmt的关闭需要特殊处理
					try
					{
						pstmt.close();
					}
					catch (SQLException ex)
					{
						ex.printStackTrace();
					}
					finally
					{
						pstmt.close();
					}
				}
				if (!mflag)
				{
					con.rollback();
					con.close();
				}
			}
			catch (SQLException ex)
			{
				// 在这个地方，有可能会没有关闭连接
				ex.printStackTrace();
				return false;
			}
			return false;
		}
		finally
		{
			// 强制回收
			try
			{
				if (pstmt != null)
				{
					// 由于描述的问题，导致执行的sql错误百出，因此pstmt的关闭需要特殊处理
					try
					{
						pstmt.close();
					}
					catch (SQLException ex)
					{
						ex.printStackTrace();
					}
					finally
					{
						pstmt.close();
					}
				}
				if (!mflag)
				{
					// 是否需要在这里执行rollback？
					// con.rollback();
					con.close();
				}
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
		}
		long t1 = System.currentTimeMillis();
		if( t1-t0 > 500 && t1-t0 <= 1000 ){
        	System.out.println("警告：执行时间超过500毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 1000 && t1-t0 <=5000){
        	System.out.println("严重警告：执行时间超过1000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 5000 && t1-t0 <=10000){
        	System.out.println("严重警告：执行时间超过5000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 10000 && t1-t0 <=50000){
        	System.out.println("严重警告：执行时间超过10000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 50000 && t1-t0 <=100000){
        	System.out.println("严重警告：执行时间超过50000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 100000){
        	System.out.println("严重警告：执行时间超过100000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }
		return true;

    }

    /**
     * 功能：可以执行输入的任意查询SQL语句。 输入：任意一个查询语句的字符串csql 返回：一个SSRS类的实例，内为查询结果
     * 
     * @param sql
     *            String
     * @return SSRS
     */
    public SSRS execSQL(String sql) {
    	long t0 = System.currentTimeMillis();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        SSRS tSSRS = null;
        int rowCount = 0,getCount = 0;
        // System.out.println("ExecSQL : " + sql);
        // add by yt
        if (!mflag||con==null) {
            con = DBConnPool.getConnection();
        }

        try {
        	int index = 0;
    		while(con==null){
    			try {
	    			con = DBConnPool.getConnection();
	    			if(con==null){
							Thread.sleep(1000);
	    			}
    			} catch (Exception e) {
        			index++;
        			if(index==100){
        				System.out.println("getConnection fail 100 time");
        				e.printStackTrace();
        			}
				}
    		}
            pstmt = con.prepareStatement(StrTool.GBKToUnicode(sql), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

            rs = pstmt.executeQuery();
            rsmd = rs.getMetaData();

            int n = rsmd.getColumnCount();
            tSSRS = new SSRS(n);

            // 取得总记录数
            while (rs.next()) {
            	rowCount++;
            	getCount++;
                for (int j = 1; j <= n; j++) {
                    tSSRS.SetText(getDataValue(rsmd, rs, j));
                }
            }

            rs.close();
            pstmt.close();

            if (!mflag) {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // @@错误处理
            System.out.println("##### Error Sql in ExeSQL at execSQL(String sql): " + sql);
            mErrors.clearErrors();
            CError.buildErr(this, e.toString());

            tSSRS = null;
            // tSSRS.ErrorFlag = true;
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // 由于描述的问题，导致执行的sql错误百出，因此pstmt的关闭需要特殊处理
                    try {
                        pstmt.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        pstmt.close();
                    }
                }
                if (!mflag) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            // 强制回收
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // 由于描述的问题，导致执行的sql错误百出，因此pstmt的关闭需要特殊处理
                    try {
                        pstmt.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        pstmt.close();
                    }
                }
                if (!mflag) {
                	if(con!=null)
                		con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        long t1 = System.currentTimeMillis();
//        if( t1-t0 > 500 && t1-t0 <= 1000 ){
////        	System.out.println("警告：执行时间超过500毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
//        }else if(t1-t0 > 1000 && t1-t0 <=5000){
//        	System.out.println("严重警告：执行时间超过1000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
//        }else if(t1-t0 > 5000 && t1-t0 <=10000){
//        	System.out.println("严重警告：执行时间超过5000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
//        }else if(t1-t0 > 10000 && t1-t0 <=50000){
//        	System.out.println("严重警告：执行时间超过10000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
//        }else if(t1-t0 > 50000 && t1-t0 <=100000){
//        	System.out.println("严重警告：执行时间超过50000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
//        }else if(t1-t0 > 100000){
//        	System.out.println("严重警告：执行时间超过100000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
//        }
//        if(rowCount>200 && rowCount<=500){
//        	System.out.println("SQL结果条数超过200，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
//        }else if(rowCount>500 && rowCount<=10000){
//        	System.out.println("SQL结果条数超过500，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
//        }else if(rowCount>1000 && rowCount<=50000){
//        	System.out.println("SQL结果条数超过1000，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
//        }else if(rowCount>5000){
//        	System.out.println("SQL结果条数超过5000，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
//        }
        return tSSRS;
    }

    /**
     * 查询指定起始位置后的范围数据
     * 
     * @param sql
     *            String
     * @param start
     *            int
     * @param nCount
     *            int
     * @return SSRS
     */
    public SSRS execSQL(String sql, int start, int nCount) {
    	long t0 = System.currentTimeMillis();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        SSRS tSSRS = null;
        int rowCount = 0,getCount = 0;
        // System.out.println("ExecSQL : " + sql);
        // add by Fanym
        if (start <= 0) {
            start = 1;
        }

        if (nCount <= 0) {
            nCount = 1;
        }

        // add by yt
        if (!mflag) {
            con = DBConnPool.getConnection();
        }

        try {
            pstmt = con.prepareStatement(StrTool.GBKToUnicode(sql), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

            rs = pstmt.executeQuery();
            rsmd = rs.getMetaData();

            int n = rsmd.getColumnCount();
            tSSRS = new SSRS(n);

            int m = start + nCount;
            int k = 0;

            // 取得总记录数
            while (rs.next()) {
                k++;

                // 只取特定范围内的记录行
                if ((k >= start) && (k < m)) {
                	getCount ++;
                    for (int j = 1; j <= n; j++) {
                        tSSRS.SetText(getDataValue(rsmd, rs, j));
                    }
                }
            }
            if (k > 1000) {
                System.out.println("****** 建议采用大批量数据查询模式 ****** "+ sql);
            }
            rowCount = k;
            rs.close();
            pstmt.close();

            if (!mflag) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // @@错误处理
            System.out.println("##### Error Sql in ExeSQL at execSQL(String sql, int start, int nCount): " + sql);
            mErrors.clearErrors();
            CError.buildErr(this, e.toString());

            tSSRS = null;

            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // 由于描述的问题，导致执行的sql错误百出，因此pstmt的关闭需要特殊处理
                    try {
                        pstmt.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        pstmt.close();
                    }
                }
                if (!mflag) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            // 强制回收
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // 由于描述的问题，导致执行的sql错误百出，因此pstmt的关闭需要特殊处理
                    try {
                        pstmt.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        pstmt.close();
                    }
                }
                if (!mflag) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        long t1 = System.currentTimeMillis();
        if( t1-t0 > 500 && t1-t0 <= 1000 ){
        	System.out.println("警告：执行时间超过500毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 1000 && t1-t0 <=5000){
        	System.out.println("严重警告：执行时间超过1000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 5000 && t1-t0 <=10000){
        	System.out.println("严重警告：执行时间超过5000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 10000 && t1-t0 <=50000){
        	System.out.println("严重警告：执行时间超过10000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 50000 && t1-t0 <=100000){
        	System.out.println("严重警告：执行时间超过50000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }else if(t1-t0 > 100000){
        	System.out.println("严重警告：执行时间超过100000毫秒,执行时间为"+(t1-t0)+"毫秒  ------  "+sql);
        }
        if(rowCount>200 && rowCount<=500){
        	System.out.println("SQL结果条数超过200，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }else if(rowCount>500 && rowCount<=10000){
        	System.out.println("SQL结果条数超过500，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }else if(rowCount>1000 && rowCount<=50000){
        	System.out.println("SQL结果条数超过1000，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }else if(rowCount>5000){
        	System.out.println("SQL结果条数超过5000，结果条数为"+rowCount+"条,获取条数为"+getCount+"条！--- "+sql);
        }
        return tSSRS;
    }

    public static void main(String[] args) {
        int iCount = 177;
        int start = iCount % (SysConst.MAXSCREENLINES * SysConst.MAXMEMORYPAGES);
        System.out.println(start);
    }
}

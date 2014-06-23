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
 * Description: DB�����ݿ�������ļ�
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: sinosoft
 * </p>
 * 
 * @Database: LIS
 * @CreateDate��2002-07-11
 */
public class ExeSQL {
    private Connection con;

    /**
     * mflag = true: ����Connection mflag = false: ������Connection
     */
    private boolean mflag = false;

    private FDate fDate = new FDate();

    public CErrors mErrors = new CErrors(); // ������Ϣ

    // @Constructor
    public ExeSQL(Connection tConnection) {
        con = tConnection;
        mflag = true;
    }

    public ExeSQL() {
    }

    /**
     * ��ȡΨһ�ķ���ֵ
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

        // add by yt�����û�д������ӣ����ഴ��
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
                // ��ʵ�����Ǻܺ��ʣ���Ҫ����Ϊ�п���ȡ�ö����������������
                mValue = rs.getString(1);
                break;
            }
            
            rs.close();
            pstmt.close();
            // ����������ഴ���ģ���ر�����
            if (!mflag) {
                con.close();
            }
        } catch (SQLException e) {
            // @@������
            System.out.println("### Error ExeSQL at getOneValue: " + sql);
            mErrors.clearErrors();
            CError.buildErr(this, e.toString());
            // ���÷���ֵ
            mValue = "";
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // �������������⣬����ִ�е�sql����ٳ������pstmt�Ĺر���Ҫ���⴦��
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
            // ǿ�ƻ���
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // �������������⣬����ִ�е�sql����ٳ������pstmt�Ĺر���Ҫ���⴦��
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
//        	System.out.println("���棺ִ��ʱ�䳬��500����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
//        }else if(t1-t0 > 1000 && t1-t0 <=5000){
//        	System.out.println("���ؾ��棺ִ��ʱ�䳬��1000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
//        }else if(t1-t0 > 5000 && t1-t0 <=10000){
//        	System.out.println("���ؾ��棺ִ��ʱ�䳬��5000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
//        }else if(t1-t0 > 10000 && t1-t0 <=50000){
//        	System.out.println("���ؾ��棺ִ��ʱ�䳬��10000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
//        }else if(t1-t0 > 50000 && t1-t0 <=100000){
//        	System.out.println("���ؾ��棺ִ��ʱ�䳬��50000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
//        }else if(t1-t0 > 100000){
//        	System.out.println("���ؾ��棺ִ��ʱ�䳬��100000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
//        }
        return StrTool.cTrim(mValue);
    }

    /**
     * ��ȡSQL�Ĳ�ѯ�����¼�� ��ע�������������ֻ�����ڲ����ã���������ڴ˴��رմ��������ӡ����ʧ�ܻ����쳣�����صĽ��Ϊ0�����ڵ��õĵط��ر����ӡ�
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
        // �˷����Բ�ͬ���ݿ�ͨ��
        sql = "select count(1) from (" + sql + ") rsc";
        System.out.println("getResultCount : " + sql);

        try {
            pstmt = con.prepareStatement(StrTool.GBKToUnicode(sql), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = pstmt.executeQuery();
            // rs.next();
            // �������Ա�֤��û�в�ѯ�����ݵ�ʱ��Ҳ��������
            while (rs.next()) {
                iCount = rs.getInt(1);
                break;
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // @@������
            System.out.println("### Error ExeSQL at getResultCount: " + sql);
            CError.buildErr(this, e.toString());
            iCount = 0;
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // �������������⣬����ִ�е�sql����ٳ������pstmt�Ĺر���Ҫ���⴦��
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
                // ���ܳ�������û�йر�
            } finally {
                // ǿ�ƻ���
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (pstmt != null) {
                        // �������������⣬����ִ�е�sql����ٳ������pstmt�Ĺر���Ҫ���⴦��
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
        	System.out.println("���棺ִ��ʱ�䳬��500����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 1000 && t1-t0 <=5000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��1000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 5000 && t1-t0 <=10000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��5000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 10000 && t1-t0 <=50000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��10000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 50000 && t1-t0 <=100000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��50000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 100000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��100000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }
        return iCount;
    }

    /**
     * ��ָ��λ�ò�ѯȫ������
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
            // ��ѯ�ֶεĸ���
            int n = rsmd.getColumnCount();
            // ��ѯ��¼������
            int m = start + (SysConst.MAXSCREENLINES * SysConst.MAXMEMORYPAGES);

            // ȡ���ܼ�¼��
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
                    System.out.println("****** ������ô��������ݲ�ѯģʽ ****** "+ sql);
                }
                // "0|"Ϊ��ѯ�ɹ���ǣ���CODEQUERYͳһ��MINIM�޸�
                mResult.insert(0, "0|" + String.valueOf(k) + SysConst.RECORDSPLITER);
                mResult.delete(mResult.length() - 1, mResult.length());
            } else {
                mResult.append("100|δ��ѯ���������!");
            }
            rs.close();
            pstmt.close();
            if (!mflag) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // @@������
            System.out.println("##### Error Sql in ExeSQL at getEncodedResult(String sql, int start): " + sql);
            mErrors.clearErrors();
            CError.buildErr(this, e.toString());
            mResult.setLength(0);
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // �������������⣬����ִ�е�sql����ٳ������pstmt�Ĺر���Ҫ���⴦��
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
            // ǿ�ƻ���
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // �������������⣬����ִ�е�sql����ٳ������pstmt�Ĺر���Ҫ���⴦��
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
        	System.out.println("���棺ִ��ʱ�䳬��500����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 1000 && t1-t0 <=5000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��1000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 5000 && t1-t0 <=10000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��5000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 10000 && t1-t0 <=50000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��10000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 50000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��100000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }
        if(rowCount>200 && rowCount<=500){
        	System.out.println("SQL�����������200���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }else if(rowCount>500 && rowCount<=1000){
        	System.out.println("SQL�����������500���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }else if(rowCount>1000 && rowCount<=5000){
        	System.out.println("SQL�����������1000���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }else if(rowCount>5000){
        	System.out.println("SQL�����������5000���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }
        return mResult.toString();
    }

    /**
     * ��ָ��λ�ò�ѯȫ������
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
            // ��ѯ�ֶεĸ���
            int n = rsmd.getColumnCount();
            // ��ѯ��¼������
            int m = start + (SysConst.MAXSCREENLINES * SysConst.MAXMEMORYPAGES*1000000);//20100702�˷�����Ϊ����������ѯ����ѯ���Ϊȫ��

            // ȡ���ܼ�¼��
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
                    System.out.println("****** ������ô��������ݲ�ѯģʽ ****** "+ sql);
                }
                // "0|"Ϊ��ѯ�ɹ���ǣ���CODEQUERYͳһ��MINIM�޸�
                mResult.insert(0, "0|" + String.valueOf(k) + SysConst.RECORDSPLITER);
                mResult.delete(mResult.length() - 1, mResult.length());
            } else {
                mResult.append("100|δ��ѯ���������!");
            }
            rs.close();
            pstmt.close();
            if (!mflag) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // @@������
            System.out.println("##### Error Sql in ExeSQL at getEncodedResult(String sql, int start): " + sql);
            mErrors.clearErrors();
            CError.buildErr(this, e.toString());
            mResult.setLength(0);
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // �������������⣬����ִ�е�sql����ٳ������pstmt�Ĺر���Ҫ���⴦��
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
            // ǿ�ƻ���
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // �������������⣬����ִ�е�sql����ٳ������pstmt�Ĺر���Ҫ���⴦��
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
        	System.out.println("���棺ִ��ʱ�䳬��500����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 1000 && t1-t0 <=5000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��1000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 5000 && t1-t0 <=10000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��5000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 10000 && t1-t0 <=50000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��10000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 50000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��100000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }
        if(rowCount>200 && rowCount<=500){
        	System.out.println("SQL�����������200���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }else if(rowCount>500 && rowCount<=1000){
        	System.out.println("SQL�����������500���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }else if(rowCount>1000 && rowCount<=5000){
        	System.out.println("SQL�����������1000���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }else if(rowCount>5000){
        	System.out.println("SQL�����������5000���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }
        return mResult.toString();
    }

    
    /**
     * ��ָ��λ�ò�ѯȫ�����ݣ��˷���Ϊ����������ѯ
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
        if (m >= iCount) {// ���һҳ
            iCount = getResultCount(sql, pstmt, rs);
            if (iCount % (SysConst.MAXSCREENLINES * SysConst.MAXMEMORYPAGES) == 0) {
                start = (iCount - SysConst.MAXSCREENLINES * SysConst.MAXMEMORYPAGES) + 1;
                m = start + (SysConst.MAXSCREENLINES * SysConst.MAXMEMORYPAGES);
            } else {
                start = (iCount - iCount % (SysConst.MAXSCREENLINES * SysConst.MAXMEMORYPAGES)) + 1;
                m = start + (SysConst.MAXSCREENLINES * SysConst.MAXMEMORYPAGES);
            }
        }
        // �����¼��Ϊ0����ʾû�в�ѯ�����ݣ����ʱ����Ҫ�ر�����
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
            // ֱ�ӷ��أ���ѯ���Ϊ��
            return "100|δ��ѯ���������!";
        }

        try {
            // ��ѯ��¼������

            // �������ݿ⣬��ѯָ����Χ���ݼ������ô˷������Դ�������ǰ̨�ķ�ҳ��ѯЧ��
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
            // ��ѯ�ֶεĸ���
            int n = rsmd.getColumnCount();

            int k = 0; // �����ж��Ƿ�������
            while (rs.next()) {
                k++;
                // ֱ�Ӵ�λ��2��ʼ��ok�ˣ��Ǻǣ���ôû�뵽�أ�����
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
            if (k < SysConst.MAXSCREENLINES * SysConst.MAXMEMORYPAGES) {// ��һҳ��ȫ
                iCount = start + k - 1;
            }

            if (k > 0) {
                // "0|"Ϊ��ѯ�ɹ���ǣ���CODEQUERYͳһ��MINIM�޸�
                mResult.insert(0, "0|" + String.valueOf(iCount) + SysConst.RECORDSPLITER);
                mResult.delete(mResult.length() - 1, mResult.length());
            } else {
                mResult.append("100|δ��ѯ���������!");
            }
            rs.close();
            pstmt.close();
            if (!mflag) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // @@������
            System.out.println("##### Error Sql in ExeSQL at getEncodedResultLarge(String sql, int start): " + sql);
            mErrors.clearErrors();
            CError.buildErr(this, e.toString());
            mResult.setLength(0);
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // �������������⣬����ִ�е�sql����ٳ������pstmt�Ĺر���Ҫ���⴦��
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
            // ǿ�ƻ���
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // �������������⣬����ִ�е�sql����ٳ������pstmt�Ĺر���Ҫ���⴦��
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
        	System.out.println("���棺ִ��ʱ�䳬��500����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 1000 && t1-t0 <=5000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��1000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 5000 && t1-t0 <=10000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��5000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 10000 && t1-t0 <=50000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��10000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 50000 && t1-t0 <=100000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��50000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 100000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��100000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }
        if(rowCount>200 && rowCount<=500){
        	System.out.println("SQL�����������200���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }else if(rowCount>500 && rowCount<=1000){
        	System.out.println("SQL�����������500���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }else if(rowCount>1000 && rowCount<=5000){
        	System.out.println("SQL�����������1000���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }else if(rowCount>5000){
        	System.out.println("SQL�����������5000���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }
        return mResult.toString();
    }

    /**
     * ��ѯ����
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
            // �������û���������Ҫ���ô��з�ʽ�Ĳ�ѯ�������ڼ�ǧ����
            pstmt.setFetchSize(500);
            rs = pstmt.executeQuery();
            rs.setFetchSize(500);
            rsmd = rs.getMetaData();

            int n = rsmd.getColumnCount();

            int k = 0;
            // ȡ���ܼ�¼��
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
                // "0|"Ϊ��ѯ�ɹ���ǣ���CODEQUERYͳһ��MINIM�޸�
                mResult.insert(0, "0|" + String.valueOf(k) + SysConst.RECORDSPLITER);
                mResult.delete(mResult.length() - 1, mResult.length());
            } else {
                mResult.append("100|δ��ѯ��������ݣ�");
            }
            rs.close();
            pstmt.close();
            if (!mflag) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // @@������
            System.out.println("##### Error Sql in ExeSQL at getEncodedResult(String sql): " + sql);
            mErrors.clearErrors();
            CError.buildErr(this, e.toString());
            mResult.setLength(0);
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // �������������⣬����ִ�е�sql����ٳ������pstmt�Ĺر���Ҫ���⴦��
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
            // ǿ�ƻ���
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // �������������⣬����ִ�е�sql����ٳ������pstmt�Ĺر���Ҫ���⴦��
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
        	System.out.println("���棺ִ��ʱ�䳬��500����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 1000 && t1-t0 <=5000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��1000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 5000 && t1-t0 <=10000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��5000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 10000 && t1-t0 <=50000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��10000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 50000 && t1-t0 <=100000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��50000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 100000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��100000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }
        if(rowCount>200 && rowCount<=500){
        	System.out.println("SQL�����������200���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }else if(rowCount>500 && rowCount<=1000){
        	System.out.println("SQL�����������500���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }else if(rowCount>1000 && rowCount<=5000){
        	System.out.println("SQL�����������1000���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }else if(rowCount>5000){
        	System.out.println("SQL�����������5000���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }
        return mResult.toString();
    }

    /**
     * ��ָ��λ�ò�ѯ��������
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
        // ȡ���ܼ�¼�� add by liuqiang
        // int iCount = getResultCount(sql,pstmt,rs);
        // if (iCount <= 0) return "";
        try {
            pstmt = con.prepareStatement(StrTool.GBKToUnicode(sql), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

            rs = pstmt.executeQuery();
            rsmd = rs.getMetaData();
            int n = rsmd.getColumnCount();
            int m = start + nCount;
            // ȡ���ܼ�¼��
            int k = 0;
            while (rs.next()) {
                k++;
                // �������Ҫȡ�ļ�¼����ֱ���˳�
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
                System.out.println("****** ������ô��������ݲ�ѯģʽ ****** "+ sql);
            }
            rowCount = k;
            if (k >= start) {
                // "0|"Ϊ��ѯ�ɹ���ǣ���CODEQUERYͳһ��MINIM�޸�
                mResult.insert(0, "0|" + String.valueOf(k) + SysConst.RECORDSPLITER);
                mResult.delete(mResult.length() - 1, mResult.length());
            } else {
                mResult.append("100|δ��ѯ��������ݣ�");
            }
            rs.close();
            pstmt.close();
            if (!mflag) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // @@������
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
                    // �������������⣬����ִ�е�sql����ٳ������pstmt�Ĺر���Ҫ���⴦��
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
            // ǿ�ƻ���
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // �������������⣬����ִ�е�sql����ٳ������pstmt�Ĺر���Ҫ���⴦��
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
        	System.out.println("���棺ִ��ʱ�䳬��500����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 1000 && t1-t0 <=5000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��1000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 5000 && t1-t0 <=10000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��5000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 10000 && t1-t0 <=50000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��10000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 50000 && t1-t0 <=100000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��50000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 100000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��100000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }
        if(rowCount>200 && rowCount<=500){
        	System.out.println("SQL�����������200���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }else if(rowCount>500 && rowCount<=1000){
        	System.out.println("SQL�����������500���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }else if(rowCount>1000 && rowCount<=5000){
        	System.out.println("SQL�����������1000���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }else if(rowCount>5000){
        	System.out.println("SQL�����������5000���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }
        return mResult.toString();
    }

    /**
     * ��ResultSet��ȡ��������ת��Ϊ��Ӧ������ֵ�ַ��� ���������ɹ�ִ�У�����True�����򷵻�False��������Error�����ô������ϸ��Ϣ
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
            // ��������Ϊ�ַ�
            if ((dataType == Types.CHAR) || (dataType == Types.VARCHAR)) {
                // ���ڴ������ݿ��������GBKģʽ�����û�б�Ҫ��һ��unicodeToGBK
                // strValue = StrTool.unicodeToGBK(rs.getString(i));
                strValue = rs.getString(i);
            }
            // ��������Ϊ���ڡ�ʱ��
            else if ((dataType == Types.TIMESTAMP) || (dataType == Types.DATE)) {
                strValue = fDate.getString(rs.getDate(i));
            }
            // ��������Ϊ����
            else if ((dataType == Types.DECIMAL) || (dataType == Types.FLOAT)) {
                // strValue = String.valueOf(rs.getFloat(i));
                // ��������ķ���ʹ�����������ʱ�򲻻������ѧ��������ʽ
                strValue = String.valueOf(rs.getBigDecimal(i));
                // ȥ�㴦��
                strValue = PubFun.getInt(strValue);
            }
            // ��������Ϊ����
            else if ((dataType == Types.INTEGER) || (dataType == Types.SMALLINT)) {
                strValue = String.valueOf(rs.getInt(i));
                strValue = PubFun.getInt(strValue);
            }
            // ��������Ϊ����
            else if (dataType == Types.NUMERIC) {
                if (dataScale == 0) {
                    if (dataPrecision == 0) {
                        // strValue = String.valueOf(rs.getDouble(i));
                        // ��������ķ���ʹ�����������ʱ�򲻻������ѧ��������ʽ
                        strValue = String.valueOf(rs.getBigDecimal(i));
                    } else {
                        strValue = String.valueOf(rs.getLong(i));
                    }
                } else {
                    // strValue = String.valueOf(rs.getDouble(i));
                    // ��������ķ���ʹ�����������ʱ�򲻻������ѧ��������ʽ
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
     * ���룺cSQL����ExeSQL���ʼ����ʱ�������ӡ� ���������ɹ�ִ�У�����True�����򷵻�False��������Error�����ô������ϸ��Ϣ
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
			// �����Ƿ�����޸ģ�����Ҫ����һ��
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
			// @@������
			System.out.println("##### Error Sql in ExeSQL at execUpdateSQL: " + sql);
			mErrors.clearErrors();
			CError.buildErr(this, e.toString());

			try
			{
				if (pstmt != null)
				{
					// �������������⣬����ִ�е�sql����ٳ������pstmt�Ĺر���Ҫ���⴦��
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
				// ������ط����п��ܻ�û�йر�����
				ex.printStackTrace();
				return false;
			}
			return false;
		}
		finally
		{
			// ǿ�ƻ���
			try
			{
				if (pstmt != null)
				{
					// �������������⣬����ִ�е�sql����ٳ������pstmt�Ĺر���Ҫ���⴦��
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
					// �Ƿ���Ҫ������ִ��rollback��
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
        	System.out.println("���棺ִ��ʱ�䳬��500����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 1000 && t1-t0 <=5000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��1000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 5000 && t1-t0 <=10000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��5000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 10000 && t1-t0 <=50000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��10000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 50000 && t1-t0 <=100000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��50000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 100000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��100000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }
		return true;

    }

    /**
     * ���ܣ�����ִ������������ѯSQL��䡣 ���룺����һ����ѯ�����ַ���csql ���أ�һ��SSRS���ʵ������Ϊ��ѯ���
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

            // ȡ���ܼ�¼��
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
            // @@������
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
                    // �������������⣬����ִ�е�sql����ٳ������pstmt�Ĺر���Ҫ���⴦��
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
            // ǿ�ƻ���
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // �������������⣬����ִ�е�sql����ٳ������pstmt�Ĺر���Ҫ���⴦��
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
////        	System.out.println("���棺ִ��ʱ�䳬��500����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
//        }else if(t1-t0 > 1000 && t1-t0 <=5000){
//        	System.out.println("���ؾ��棺ִ��ʱ�䳬��1000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
//        }else if(t1-t0 > 5000 && t1-t0 <=10000){
//        	System.out.println("���ؾ��棺ִ��ʱ�䳬��5000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
//        }else if(t1-t0 > 10000 && t1-t0 <=50000){
//        	System.out.println("���ؾ��棺ִ��ʱ�䳬��10000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
//        }else if(t1-t0 > 50000 && t1-t0 <=100000){
//        	System.out.println("���ؾ��棺ִ��ʱ�䳬��50000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
//        }else if(t1-t0 > 100000){
//        	System.out.println("���ؾ��棺ִ��ʱ�䳬��100000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
//        }
//        if(rowCount>200 && rowCount<=500){
//        	System.out.println("SQL�����������200���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
//        }else if(rowCount>500 && rowCount<=10000){
//        	System.out.println("SQL�����������500���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
//        }else if(rowCount>1000 && rowCount<=50000){
//        	System.out.println("SQL�����������1000���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
//        }else if(rowCount>5000){
//        	System.out.println("SQL�����������5000���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
//        }
        return tSSRS;
    }

    /**
     * ��ѯָ����ʼλ�ú�ķ�Χ����
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

            // ȡ���ܼ�¼��
            while (rs.next()) {
                k++;

                // ֻȡ�ض���Χ�ڵļ�¼��
                if ((k >= start) && (k < m)) {
                	getCount ++;
                    for (int j = 1; j <= n; j++) {
                        tSSRS.SetText(getDataValue(rsmd, rs, j));
                    }
                }
            }
            if (k > 1000) {
                System.out.println("****** ������ô��������ݲ�ѯģʽ ****** "+ sql);
            }
            rowCount = k;
            rs.close();
            pstmt.close();

            if (!mflag) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // @@������
            System.out.println("##### Error Sql in ExeSQL at execSQL(String sql, int start, int nCount): " + sql);
            mErrors.clearErrors();
            CError.buildErr(this, e.toString());

            tSSRS = null;

            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // �������������⣬����ִ�е�sql����ٳ������pstmt�Ĺر���Ҫ���⴦��
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
            // ǿ�ƻ���
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    // �������������⣬����ִ�е�sql����ٳ������pstmt�Ĺر���Ҫ���⴦��
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
        	System.out.println("���棺ִ��ʱ�䳬��500����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 1000 && t1-t0 <=5000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��1000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 5000 && t1-t0 <=10000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��5000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 10000 && t1-t0 <=50000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��10000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 50000 && t1-t0 <=100000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��50000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }else if(t1-t0 > 100000){
        	System.out.println("���ؾ��棺ִ��ʱ�䳬��100000����,ִ��ʱ��Ϊ"+(t1-t0)+"����  ------  "+sql);
        }
        if(rowCount>200 && rowCount<=500){
        	System.out.println("SQL�����������200���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }else if(rowCount>500 && rowCount<=10000){
        	System.out.println("SQL�����������500���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }else if(rowCount>1000 && rowCount<=50000){
        	System.out.println("SQL�����������1000���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }else if(rowCount>5000){
        	System.out.println("SQL�����������5000���������Ϊ"+rowCount+"��,��ȡ����Ϊ"+getCount+"����--- "+sql);
        }
        return tSSRS;
    }

    public static void main(String[] args) {
        int iCount = 177;
        int start = iCount % (SysConst.MAXSCREENLINES * SysConst.MAXMEMORYPAGES);
        System.out.println(start);
    }
}

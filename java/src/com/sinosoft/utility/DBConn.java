/**
 * Copyright (c) 2002 sinosoft Co. Ltd. All right reserved.
 */
package com.sinosoft.utility;

import java.io.CharArrayWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/*
 * <p>ClassName: DBConn </p> <p>Description: �������ݿ����ļ� </p> <p>Copyright: Copyright (c) 2002</p> <p>Company:
 * sinosoft </p> @Database: LIS @CreateDate��2002-08-09
 */
public class DBConn implements java.sql.Connection {
    private JdbcUrl JUrl;

    private Connection con = null;

    private boolean bNotInUse;

    private boolean bIsPool = false;

    private java.util.Date m_lastestAccess = null;

    private CharArrayWriter m_buf = new CharArrayWriter();

    private PrintWriter m_pw = new PrintWriter(m_buf, true);

    /**
     * ��������
     * 
     * @return boolean
     */
    public boolean createConnection() {
        int dbType = 0;
        /**
         * WebLogic���ӳ����õ��� ����ķ��������ӱ��������ж�ȡ��ʱ��û�а�bIsPool����Ϊfalse ����ÿ��ִ�еľ���con.close()������û���𵽳ص����á�
         * ������������ӳ���������û�п��е����ӣ��ӱ�����ȥ���Ӿ�Υ���˹��� dbtype=10û������
         */
        if (JUrl.getDBType().equalsIgnoreCase("WEBLOGICPOOL")) {
            dbType = 10;
            bIsPool = true;
            if (getWeblogicPoolConnection()) {
                return true;
            } else {
                System.out.println("$$$$$$$$WebLogicPool Connect Failed$$$$$");
                // ����ȡ���ӳ�ʧ��ʱ���ӱ������Ӷ�ȡ����
                JdbcUrlBackUp tJdbcUrlBackUp = new JdbcUrlBackUp();
                JUrl.setDBName(tJdbcUrlBackUp.getDBName());
                JUrl.setDBType(tJdbcUrlBackUp.getDBType());
                JUrl.setIP1(tJdbcUrlBackUp.getIP1());
                JUrl.setIP2(tJdbcUrlBackUp.getIP2());
                JUrl.setPassWord(tJdbcUrlBackUp.getPassWord());
                JUrl.setPort1(tJdbcUrlBackUp.getPort1());
                JUrl.setPort2(tJdbcUrlBackUp.getPort2());
                JUrl.setServerName(tJdbcUrlBackUp.getServerName());
                JUrl.setUser(tJdbcUrlBackUp.getUserName());
            }
        }
        /**
         * apache���ӳ����õ���
         */
        else if (JUrl.getDBType().equalsIgnoreCase("COMMONSDBCP")) {
            bIsPool = true;
            if (getApachecommonDBCP()) {
                return true;
            } else {
                // ����ȡ���ӳ�ʧ��ʱ���ӱ������Ӷ�ȡ����
                JdbcUrlBackUp tJdbcUrlBackUp = new JdbcUrlBackUp();
                JUrl.setDBName(tJdbcUrlBackUp.getDBName());
                JUrl.setDBType(tJdbcUrlBackUp.getDBType());
                JUrl.setIP1(tJdbcUrlBackUp.getIP1());
                JUrl.setIP2(tJdbcUrlBackUp.getIP2());
                JUrl.setPassWord(tJdbcUrlBackUp.getPassWord());
                JUrl.setPort1(tJdbcUrlBackUp.getPort1());
                JUrl.setPort2(tJdbcUrlBackUp.getPort2());
                JUrl.setServerName(tJdbcUrlBackUp.getServerName());
                JUrl.setUser(tJdbcUrlBackUp.getUserName());
            }
        }
        /**
         * WebSphere���ӳ����õ���
         */
        else if (JUrl.getDBType().equalsIgnoreCase("WEBSPHERE")) {
            bIsPool = true;
            if (getWebSpherePoolConnection()) {
                return true;
            } else {
                // ����ȡ���ӳ�ʧ��ʱ���ӱ������Ӷ�ȡ����
                JdbcUrlBackUp tJdbcUrlBackUp = new JdbcUrlBackUp();
                JUrl.setDBName(tJdbcUrlBackUp.getDBName());
                JUrl.setDBType(tJdbcUrlBackUp.getDBType());
                JUrl.setIP1(tJdbcUrlBackUp.getIP1());
                JUrl.setIP2(tJdbcUrlBackUp.getIP2());
                JUrl.setPassWord(tJdbcUrlBackUp.getPassWord());
                JUrl.setPort1(tJdbcUrlBackUp.getPort1());
                JUrl.setPort2(tJdbcUrlBackUp.getPort2());
                JUrl.setServerName(tJdbcUrlBackUp.getServerName());
                JUrl.setUser(tJdbcUrlBackUp.getUserName());
            }
        }
        /**
         * ������涼û��ִ�гɹ���������Լ���д��jdbc����
         */
        try {
            if (con != null) {
                if (!con.isClosed()) {
                    try {
                        // Ϊ�˽������ʱ�������⣬�ڷ���֮ǰ��������һ��con
                        Statement stmt = con.createStatement();
                        stmt.execute("ALTER SESSION SET NLS_DATE_FORMAT='YYYY-MM-DD'");
                        stmt.execute("alter session set optimizer_mode = first_rows_1000");
                        stmt.close();
                        return true;
                    } catch (SQLException e) {
                    	if(e.getMessage().indexOf("ORA-02396")<0){
                    		e.printStackTrace();
                    	}
                        
//                        System.out.println("DBConn : recreate DBConn");
                        con.close();
                        con = null;
                    }
                }
                con = null;
            }

            // �ж����ݿ�����
            if (JUrl.getDBType().equalsIgnoreCase("ORACLE")) {
                dbType = 1;
            } else if (JUrl.getDBType().equalsIgnoreCase("INFORMIX")) {
                dbType = 2;
            } else if (JUrl.getDBType().equalsIgnoreCase("SQLSERVER")) {
                dbType = 3;
            } else if (JUrl.getDBType().equalsIgnoreCase("DB2")) {
                dbType = 4;
            } else if (JUrl.getDBType().equalsIgnoreCase("SYBASE")) {
                dbType = 5;
            }

            // �������ݿ����Ͷ�̬��������
            switch (dbType) {
            case 1:

                // ORACLE
                Class.forName("oracle.jdbc.driver.OracleDriver");
                break;
            case 2:

                // INFORMIX
                Class.forName("com.informix.jdbc.IfxDriver");
                break;
            case 3:

                // SQLSERVER
                Class.forName("com.inet.tds.TdsDriver");
                break;
            case 4:

                // DB2
                Class.forName("com.ibm.db2.jcc.DB2Driver");
                break;
            case 5:

                // SYBASE
                Class.forName("com.sybase.jdbc2.jdbc.SybDriver");
                break;
            default:
                System.out.println("Ŀǰ�ݲ�֧�ִ������͵����ݿ�!");
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        // �����������ݿ�
        try {
            switch (dbType) {

            case 1:

                // ORACLE
                // ���Ǻ���������������ʲô�����˵
                // ����һ���ǻ���ȡ���ļ�¼����һ��������Ĭ�ϵ������ύ��
                Properties props = new Properties();
                props.setProperty("user", JUrl.getUserName());
                props.setProperty("password", JUrl.getPassWord());

                // 50�������������Ѿ��ܺõ�˵������
                props.setProperty("defaultRowPrefetch", "50");
                props.setProperty("defaultExecuteBatch", "50");
                con = DriverManager.getConnection(JUrl.getJdbcUrl(), props);

                // con = DriverManager.getConnection(JUrl.getJdbcUrl(), JUrl.getUserName()
                // , JUrl.getPassWord());
                Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                stmt.execute("alter session set nls_date_format = 'YYYY-MM-DD'");
                stmt.close();
                break;

            case 2:

                // INFORMIX
                con = DriverManager.getConnection(JUrl.getJdbcUrl());
                break;

            case 3:

                // SQLSERVER
                con = DriverManager.getConnection(JUrl.getJdbcUrl(), JUrl.getUserName(), JUrl.getPassWord());
                break;
            case 4:

                // DB2
                con = DriverManager.getConnection(JUrl.getJdbcUrl(), JUrl.getUserName(), JUrl.getPassWord());
                break;
            case 5:

                // SYBASE
                con = DriverManager.getConnection(JUrl.getJdbcUrl(), JUrl.getUserName(), JUrl.getPassWord());
                break;
            }
        } catch (SQLException e) {
//            System.out.println("��������ʧ��...");
            return false;
        }
        return true;
    }

    /**
     * ����Weblogic���ӳ�
     * 
     * @return boolean
     */
    private boolean getWeblogicPoolConnection() {
        try {
            Driver myDriver = (Driver) (Class.forName("weblogic.jdbc.pool.Driver").newInstance());
            /* weblogic�����ӳ���д��close()���� */
            con = myDriver.connect(JUrl.getJdbcUrl(), null);
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.execute("alter session set nls_date_format = 'YYYY-MM-DD'");
            stmt.close();
            System.out.println("----------WebLogicPool Connected ---------");
        } catch (Exception ex) {
            System.out.println("$$$$$$$$WebLogicPool Connect Exception$$$$$");
            try {
                con.close();
            } catch (Exception e) {
            }
            con = null;
            return false;
        }

        return true;
    }

    /**
     * ��apache�ṩ�����ӳ���ȡ���ӣ�ʧ�ܷ���false
     * 
     * @return boolean
     * @date:
     */
    private boolean getApachecommonDBCP() {
        try {
            Context tContext = new InitialContext();
            tContext = (Context) tContext.lookup("java:comp/env");
            Object obj = tContext.lookup(JUrl.getDBName());
            DataSource tDataSource = (DataSource) obj;
            if (tDataSource != null) {
                con = tDataSource.getConnection();
                // ������ӵ���Oracle���ݿ⣬��Ҫ��΢����һ�����ڵĸ�ʽ��������ڷ�������������һ�£�������������ĳ���
                // �������һ���ֶ����ͣ��������Ƿ�ʹ����������
                if (con != null) {
                    // Statement stmt = con.createStatement(ResultSet.
                    // TYPE_SCROLL_SENSITIVE,
                    // ResultSet.CONCUR_UPDATABLE);
                    // stmt.execute(
                    // "alter session set nls_date_format = 'YYYY-MM-DD'");
                    // stmt.close();
                    return true;
                }
                return false;
            } else {
                System.out.println("a error occured when geting datasource");
                return false;
            }
        } catch (Throwable e) {
            System.out.println("failure when connect apache commons dbcp ");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ��WebSphere�ṩ�����ӳ���ȡ���ӣ�ʧ�ܷ���false
     * 
     * @return boolean
     */
    static DataSource singleDataSource;
    private boolean getWebSpherePoolConnection() {
//        JdbcUrl tJU = new JdbcUrl();
//        Context ctx;
//        try {
//            // Properties env = new Properties();
//            // env.put(Context.INITIAL_CONTEXT_FACTORY, "com.ibm.websphere.naming.WsnInitialContextFactory");
//            // env.put(Context.URL_PKG_PREFIXES, "com.ibm.ws.naming");
//            // env.put(Context.PROVIDER_URL, "iiop://127.0.0.1");
//            ctx = new InitialContext();
//            DataSource ds = (DataSource) ctx.lookup(tJU.getDBName());
//            con = ds.getConnection();
//            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
//            stmt.execute("alter session set nls_date_format = 'YYYY-MM-DD HH24:MI:SS'");
//            //stmt.execute("alter session set optimizer_mode = first_rows");
//            stmt.close();
//            // System.out.println("��ȡWebSphre���ӳ����ӳɹ�!");
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("����WebSphre���ӳ�ʧ��!" + e.getMessage());
//            return false;
//        }
//        return true;

    try
    {
         DataSource tDataSource ;
        if(singleDataSource!=null)
        {

          tDataSource = singleDataSource;
        }
        else
        {
          Hashtable env = new Hashtable();
          env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY,"com.ibm.websphere.naming.WsnInitialContextFactory");
          env.put(javax.naming.Context.PROVIDER_URL, "iiop://localhost:2809");
          Context tContext = new InitialContext(env);

          Object obj;

//          if(JUrl.getDBName().startsWith("jdbc/"))
//          {
//            obj = tContext.lookup(JUrl.getDBName());
//          }
//          else
//          {
//             obj = tContext.lookup("jdbc/"+JUrl.getDBName());
//          }

          obj = tContext.lookup("java:comp/env/" + JUrl.getDBName());
          tDataSource = (DataSource) obj;
          singleDataSource = tDataSource;
        }

//      Context tContext = new InitialContext();
        //�����web.xml�����������ö������������ķ���
//        DataSource tDataSource = (DataSource) tContext.lookup("java:comp/env/" + JUrl.getDBName());
        //����ķ���Ҳ���Է��ֵ�jndi����
//        DataSource tDataSource = (DataSource) tContext.lookup("jdbc/MET");
        //����������־��������´�����Ϣ��websphere���������
//        [03-9-2 17:19:11:916 CST] 6b0e97e8 ConnectionFac I J2CA0122I: �޷���λ��Դ���� jdbc/db2ds�����ʹ������ȱʡֵ��[Resource-ref settings]
//        res-auth:                 1 (APPLICATION)
//        res-isolation-level:      0 (TRANSACTION_NONE)
//        res-sharing-scope:        true (SHAREABLE)
//        res-resolution-control:   999 (undefined)
        if (tDataSource != null)
        {
            con = tDataSource.getConnection();
            if (con != null)
            {
              //  System.out.println("Connect succeed from websphere!");
              Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
              stmt.execute("alter session set nls_date_format = 'YYYY-MM-DD HH24:MI:SS'");
              stmt.execute("alter session set optimizer_mode = first_rows");
              stmt.close();
                return true;
            }
            else
            {
                System.out.println("new Connection error ...");
                return false;
            }
        }
        else
        {
            System.out.println("new DataSource error ...");
            return false;
        }
    }
    catch (Throwable e)
    {
        System.out.println("look for jndi name error ..."+JUrl.getDBName());
        e.printStackTrace();
        return false;
    }
  }

    /*
     * kevin 2002-10-04 friend function used by DBConnPool
     */
    protected DBConn() {
        JUrl = new JdbcUrl();

        bNotInUse = true;
    }

    protected boolean isInnerClose() {
        try {
            if (con == null) {
                return true;
            }

            return con.isClosed();
        } catch (SQLException ex) {
            return true;
        }
    }

    protected void setInUse() {
        /**
         * Record stack information when each connection is get We reassian System.err, so
         * Thread.currentThread().dumpStack() can dump stack info into our class FilterPrintStream.
         */
        new Throwable().printStackTrace(m_pw);

        bNotInUse = false;

        /**
         * record lastest access time
         */
        setLastestAccess();
    }

    /**
     * is this dbconn been used by someone
     * 
     * @return boolean
     */
    protected boolean isInUse() {
        return !bNotInUse;
    }

    /*
     * kevin 2002-10-04 Note: JDK 1.3 implements of java.sql.Connection
     */
    public void clearWarnings() throws SQLException {
        con.clearWarnings();
    }

    /**
     * ʵ����Connection�ӿڵ�close()������û�������ĶϿ����ӣ� ���ǽ����ӷŻ����ӳأ���bNotInUse���ó�δʹ�á�
     * 
     * @throws SQLException
     */
    public void close() throws SQLException {
        if (bIsPool) {
            // �����ͨ��weblogic���ӳصõ�����
            // �˴���close()�����ǽ����ӷŻ�weblogic���ӳء�
            // ������websphere��tomcat�Ƿ�Ҳʹ�ô˷���
            con.close();
        } else {
            // clear stack info of connection
            m_buf.reset();
            bNotInUse = true;
        }
    }

    public void commit() throws SQLException {
        con.commit();
    }

    public Statement createStatement() throws SQLException {
        return con.createStatement();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return con.createStatement(resultSetType, resultSetConcurrency);
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
        try {
            return con.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean getAutoCommit() throws SQLException {
        return con.getAutoCommit();
    }

    public String getCatalog() throws SQLException {
        return con.getCatalog();
    }

    public int getHoldability() {
        try {
            return con.getHoldability();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return con.getMetaData();
    }

    public int getTransactionIsolation() throws SQLException {
        return con.getTransactionIsolation();
    }

    public Map getTypeMap() throws SQLException {
        return con.getTypeMap();
    }

    public SQLWarning getWarnings() throws SQLException {
        return con.getWarnings();
    }

    public boolean isClosed() throws SQLException {
        if (bNotInUse) {
            return true;
        } else {
            return con.isClosed();
        }
    }

    public boolean isReadOnly() throws SQLException {
        return con.isReadOnly();
    }

    public String nativeSQL(String sql) throws SQLException {
        return con.nativeSQL(sql);
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        return con.prepareCall(sql);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return con.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
            int resultSetHoldability) {
        try {
            return con.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return con.prepareStatement(sql);
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) {
        try {
            return con.prepareStatement(sql, autoGeneratedKeys);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) {
        try {
            return con.prepareStatement(sql, columnIndexes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return con.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
            int resultSetHoldability) {
        try {
            return con.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) {
        try {
            return con.prepareStatement(sql, columnNames);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void releaseSavepoint(Savepoint savepoint) {
        try {
            con.releaseSavepoint(savepoint);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void rollback() throws SQLException {
        con.rollback();
    }

    public void rollback(Savepoint savepoint) {
        try {
            con.rollback(savepoint);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        //if (con.getAutoCommit()) {ע������ȻDBOper���һЩ������������
            con.setAutoCommit(autoCommit);
        //}
    }

    public void setCatalog(String catalog) throws SQLException {
        con.setCatalog(catalog);
    }

    public void setHoldability(int holdability) {
        try {
            con.setHoldability(holdability);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        con.setReadOnly(readOnly);
    }

    public Savepoint setSavepoint() {
        try {
            return con.setSavepoint();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Savepoint setSavepoint(String name) {
        try {
            return con.setSavepoint(name);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void setTransactionIsolation(int level) throws SQLException {
        con.setTransactionIsolation(level);
    }

    public void setTypeMap(Map map) throws SQLException {
        con.setTypeMap(map);
    }

    protected void dumpConnInfo(OutputStream os) throws Exception {
        // If this connection hasn't been closed, dump its' stack info
        if (!this.isClosed()) {
            os.write(m_buf.toString().getBytes());
        }
    }

    protected void setLastestAccess() {
        m_lastestAccess = new java.util.Date();
    }

    protected java.util.Date getLastestAccess() {
        return m_lastestAccess;
    }

    protected void innerClose() {
        System.out.println("DBConn.innerClose");
        if (isInUse()) {
            return;
        }

        m_lastestAccess = null;

        try {
            con.rollback();
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            con = null;
        }
    }
}

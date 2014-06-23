package com.sinosoft.xreport.dl;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author houzw
 * @version 1.0
 */

import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;

import com.sinosoft.xreport.util.XTLogger;
import org.apache.log4j.Logger;

public class DataSourceImp implements DataSourceInf, Runnable
{

    DBConnParam connParam; //���ݿ����ӵ���ϸ������Ϣ
    HashSet conns; //���ӳض��󣺴�����е����ݿ�����
    boolean bStop = false; //�Ƿ�ֹͣ����߳�
    long checkInterval = 1000L; //����̵߳ļ�ؼ��
    private Thread hMonitor = null; //����̵߳Ķ���
    private Driver dbDriver = null; //���ݿ��������򻺴棬����ע������

    /**
     * �������ӳ�������Ĳ���
     * @param connParam
     */
    DataSourceImp(DBConnParam connParam)
    {
        this.connParam = (DBConnParam) connParam.clone();
        conns = new HashSet(connParam.getMaxConnection());
        hMonitor = new Thread(this);
        hMonitor.start();
    }

    /**
     * ��ʼ�����Ӳ���,�������ݿ��������򲢳�ʼ����С������
     * @throws SQLException	��ʼ����С���������������쳣
     * @throws ClassNotFoundException	��������û�ҵ����쳣
     * @throws IllegalAccessException	�Ƿ���������
     * @throws InstantiationException	�Ƿ���������
     */
    synchronized void initConnection() throws SQLException,
    ClassNotFoundException, IllegalAccessException, InstantiationException
            {

            //connParam = param;

            dbDriver = (Driver) Class.forName(connParam.getDriver()).
                       newInstance();
            int min = Math.min(connParam.getMinConnection(),
                               connParam.getMaxConnection());
            if (min > 0)
    {

        Connection[] conns = new Connection[min];
        for (int i = 0; i < min; i++)
        {
            conns[i] = getConnection();
        }
        for (int i = 0; i < min; i++)
        {
            conns[i].close();
        }
        conns = null;
    }
    }
            /**
             * �رո����ӳ��е��������ݿ�����
             * @return int ���ر��ر����ӵĸ���
             * @throws SQLException	�ر�����ʱ���͵����ݿ��쳣
             */

            synchronized int close() throws SQLException
    {
        int cc = 0;
        SQLException excp = null;
        Iterator iter = conns.iterator();
        while (iter.hasNext())
        {
            try
            {
                ((_Connection) iter.next()).close();
                //TraceFactory.getTrace().trace("disconnected to " + connParam.getUrl());
                cc++;
            }
            catch (Exception e)
            {
                if (e instanceof SQLException)
                {
                    excp = (SQLException) e;
                }
            }
        }
        //ȡ�����ݿ���������ĵǼ�
        DriverManager.deregisterDriver(dbDriver);
        if (excp != null)
        {
            throw excp;
        }
        return cc;
    }

    /**
     * ֹͣ�����ӳ��и������Ӽ�ص��߳�
     */
    synchronized void stop()
    {
        bStop = true;
        //ֹͣ����߳�
        try
        {
            hMonitor.join(checkInterval, 100);
        }
        catch (Exception e)
        {
        } finally
        {
            if (hMonitor.isAlive())
            {
                hMonitor.interrupt();
                hMonitor = null;
            }
        }
    }

    /**
     * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
     */
    public synchronized Connection getConnection(String user, String password) throws
            SQLException
    {

        Logger logger = XTLogger.getLogger(DataSourceImp.class);
        Connection conn = null;
        //���ȴ����ӳ����ҳ����еĶ���
        conn = getFreeConnection(0);

        if (conn == null)
        {
            //�ж��Ƿ񳬹����������,����������������
            //��ȴ�һ��ʱ��鿴�Ƿ��п�������,�����׳��쳣�����û��޿�������
            if (getConnectionCount() >= connParam.getMaxConnection())
            {
                conn = getFreeConnection(connParam.getWaitTime());
            }
            else
            {
                //û�г��������������»�ȡһ�����ݿ������
                connParam.setUser(user);
                connParam.setPassword(password);
                logger.info("Get the URL is : " + connParam.getUrl());

                Connection conn2 = DriverManager.getConnection(connParam.getUrl(),
                        user, password);
                //TraceFactory.getTrace().trace("connected to " + connParam.getUrl());
                _Connection _conn = new _Connection(conn2);
                //�������ݿ����ӵĴ���
                conn = _conn.getConnection();
                conns.add(_conn);

                setNLSDateFormat(conn);

            }
        }

        return conn;

    }

    private void setNLSDateFormat(Connection conn) throws SQLException
    {

        Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                            ResultSet.CONCUR_UPDATABLE);
        st.execute(
                "alter session set nls_date_format = 'YYYY-MM-DD HH24:MI:SS'");
        st.close();
    }

    /**
     * �����ӳ���ȡһ�����е�����
     * @param nTimeout	����ò���ֵΪ0��û������ʱֻ�Ƿ���һ��null
     * ����Ļ��ȴ�nTimeout���뿴�Ƿ��п������ӣ����û���׳��쳣
     * @return Connection	���ؿ��õ����ݿ�����
     * @throws SQLException	���ݿ��쳣
     */
    Connection getFreeConnection(long nTimeout) throws SQLException
    {
        Connection conn = null;
        Iterator iter = conns.iterator();
        while (iter.hasNext())
        {
            _Connection _conn = (_Connection) iter.next();
            if (!_conn.isInUse())
            {
                conn = _conn.getConnection();
                _conn.setInUse(true);
                break;
            }
        }

        if (conn == null && nTimeout > 0)
        {
            //�ȴ�nTimeout�����Ա㿴�Ƿ��п�������
            int COUNT = 30;
            int i = 0;
            do
            {
                try
                {
                    Thread.sleep(nTimeout / COUNT);
                }
                catch (Exception e)
                {}
                conn = getFreeConnection(0);
                i++;
            }
            while (conn == null && i < COUNT);
            if (conn == null)
            {
                throw new SQLException("û�п��õ����ݿ�����");
            }
        }
        return conn;
    }

    /**
     * ���߳�����������ӳ��г�ʱ�Ķ���
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        long startTime = System.currentTimeMillis();
        int nHour = 0;
        int nMaxConn = 0;
        int usecount = 0;
        while (!bStop)
        {
            int i = 0;
            for (; !bStop && i < 100; i++)
            {
                try
                {
                    Thread.sleep(checkInterval / 100);
                }
                catch (Exception e)
                {}
            }
            if (i < 100)
            {
                break;
            }

            //��鳬ʱ����
            Iterator iter = conns.iterator();
            usecount = 0; //���������־
            while (!bStop && iter.hasNext())
            {
                _Connection conn = (_Connection) iter.next();
                long freeTime = System.currentTimeMillis() -
                                conn.getLastAccessTime();
                if (freeTime > connParam.getTimeoutValue() && conn.isInUse())
                {
                    try
                    {
                        conn.getConnection().close();
                    }
                    catch (Exception e)
                    {}
                }
                if (conn.isInUse())
                {
                    usecount++;
                }
            }
            //���һ��ʱ���������ʹ���е�������������رղ�ɾ�����õ����Ӷ���
            //��������Ϊ�˷�ֹ����ĳ��ʱ���ر���������Ӻ���Щ���ӾͲ��ٿ���
            //��ʱ����Щ���������˷Ѻܶ��ϵͳ��Դ�����������ʹ�õ����Ӷ���
            //�Ӷ�����ɾ����
            int hour = (int) ((System.currentTimeMillis() - startTime) /
                              3600000);
            if (hour == nHour)
            {
                nMaxConn = Math.max(nMaxConn, usecount);
                nMaxConn = Math.max(nMaxConn, connParam.getMinConnection());
            }
            else
            {
                int idx = 0;
                synchronized (conns)
                {
                    Iterator it = conns.iterator();
                    while (!bStop && it.hasNext())
                    {
                        _Connection conn = (_Connection) it.next();
                        idx++;
                        if (idx > nMaxConn)
                        {
                            try
                            {
                                conn.close();
                                //TraceFactory.getTrace().trace("disconnected to " + connParam.getUrl());
                            }
                            catch (Exception e)
                            {}
                            it.remove();
                        } //End of if(idx > nMaxConn)
                    } //End of while(it.hasNext())
                } //End of synchronized(conns)
                //��������ʱ������������������Ϊ0
                nMaxConn = 0;
                nHour = hour;
            }
        }
    }

    /**
     * ��ȡ���ӳ��е�������
     * @return int
     */
    protected int getConnectionCount()
    {
        return conns.size();
    }

    /**
     * @see javax.sql.DataSource#getConnection()
     */
    public Connection getConnection() throws SQLException
    {
        //System.out.println("i get user is : " + connParam.getUser());
        //System.out.println("i get pwd is : " + connParam.getPassword());
        return getConnection(connParam.getUser(), connParam.getPassword());
    }

    /**
     * @see javax.sql.DataSource#getLogWriter()
     */
    public PrintWriter getLogWriter() throws SQLException
    {
        return DriverManager.getLogWriter();
    }

    /**
     * @see javax.sql.DataSource#getLoginTimeout()
     */
    public int getLoginTimeout() throws SQLException
    {
        return DriverManager.getLoginTimeout();
    }

    /**
     * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
     */
    public void setLogWriter(PrintWriter arg0) throws SQLException
    {
        DriverManager.setLogWriter(arg0);
    }

    /**
     * @see javax.sql.DataSource#setLoginTimeout(int)
     */
    public void setLoginTimeout(int arg0) throws SQLException
    {
        DriverManager.setLoginTimeout(arg0);
    }

}


/**
 * �������ӵ��Է�װ��������close����
 * @author Liudong
 */
class _Connection implements InvocationHandler
{
    private final static String CLOSE_METHOD_NAME = "close";
    private Connection conn = null;
//���ݿ��æ״̬
    private boolean inUse = false;
//�û����һ�η��ʸ����ӷ�����ʱ��
    private long lastAccessTime = System.currentTimeMillis();

    _Connection(Connection conn)
    {
        this(conn, true);
    }

    _Connection(Connection conn, boolean inUse)
    {
        this.conn = conn;
        this.inUse = inUse;
    }

    /**
     * Returns the conn.
     * @return Connection
     */
    public Connection getConnection()
    {
        Connection conn2 = (Connection) Proxy.newProxyInstance(
                conn.getClass().getClassLoader(),
                conn.getClass().getInterfaces(), this);
        return conn2;
    }

    /**
     * �÷��������Ĺر������ݿ������
     * @throws SQLException
     */
    void close() throws SQLException
    {
        conn.close();
    }

    /**
     * Returns the inUse.
     * @return boolean
     */
    public boolean isInUse()
    {
        return inUse;
    }

    /**
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object)
     */
    public Object invoke(Object proxy, Method m, Object[] args) throws
            Throwable
    {
        Object obj = null;
        if (CLOSE_METHOD_NAME.equals(m.getName()))
        {
            setInUse(false);
        }
        else
        {
            //�������һ�η���ʱ��
            lastAccessTime = System.currentTimeMillis();
            try
            {
                obj = m.invoke(conn, args);
            }
            catch (InvocationTargetException e)
            {
                throw e.getTargetException();
            }
        }
        return obj;
    }

    /**
     * Returns the lastAccessTime.
     * @return long
     */
    public long getLastAccessTime()
    {
        return lastAccessTime;
    }

    /**
     * Sets the inUse.
     * @param inUse The inUse to set
     */
    public void setInUse(boolean inUse)
    {
        this.inUse = inUse;
    }

}
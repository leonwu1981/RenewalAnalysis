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

    DBConnParam connParam; //数据库连接的详细参数信息
    HashSet conns; //连接池对象：存放所有的数据库连接
    boolean bStop = false; //是否停止监控线程
    long checkInterval = 1000L; //监控线程的监控间隔
    private Thread hMonitor = null; //监控线程的对象
    private Driver dbDriver = null; //数据库驱动程序缓存，用于注销驱动

    /**
     * 构造连接池所必须的参数
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
     * 初始化连接参数,加载数据库驱动程序并初始化最小连接数
     * @throws SQLException	初始化最小连接数所产生的异常
     * @throws ClassNotFoundException	驱动程序没找到的异常
     * @throws IllegalAccessException	非法驱动程序
     * @throws InstantiationException	非法驱动程序
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
             * 关闭该连接池中的所有数据库连接
             * @return int 返回被关闭连接的个数
             * @throws SQLException	关闭连接时发送的数据库异常
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
        //取消数据库驱动程序的登记
        DriverManager.deregisterDriver(dbDriver);
        if (excp != null)
        {
            throw excp;
        }
        return cc;
    }

    /**
     * 停止对连接池中各个连接监控的线程
     */
    synchronized void stop()
    {
        bStop = true;
        //停止监控线程
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
        //首先从连接池中找出空闲的对象
        conn = getFreeConnection(0);

        if (conn == null)
        {
            //判断是否超过最大连接数,如果超过最大连接数
            //则等待一定时间查看是否有空闲连接,否则抛出异常告诉用户无可用连接
            if (getConnectionCount() >= connParam.getMaxConnection())
            {
                conn = getFreeConnection(connParam.getWaitTime());
            }
            else
            {
                //没有超过连接数，重新获取一个数据库的连接
                connParam.setUser(user);
                connParam.setPassword(password);
                logger.info("Get the URL is : " + connParam.getUrl());

                Connection conn2 = DriverManager.getConnection(connParam.getUrl(),
                        user, password);
                //TraceFactory.getTrace().trace("connected to " + connParam.getUrl());
                _Connection _conn = new _Connection(conn2);
                //返回数据库连接的代理
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
     * 从连接池中取一个空闲的连接
     * @param nTimeout	如果该参数值为0则没有连接时只是返回一个null
     * 否则的话等待nTimeout毫秒看是否还有空闲连接，如果没有抛出异常
     * @return Connection	返回可用的数据库连接
     * @throws SQLException	数据库异常
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
            //等待nTimeout毫秒以便看是否有空闲连接
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
                throw new SQLException("没有可用的数据库连接");
            }
        }
        return conn;
    }

    /**
     * 该线程用来检查连接池中超时的对象
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

            //检查超时连接
            Iterator iter = conns.iterator();
            usecount = 0; //清除计数标志
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
            //检查一个时间段内正在使用中的最大连接数，关闭并删除无用的连接对象
            //这样做是为了防止由于某段时间特别大量的连接后，这些连接就不再可用
            //的时候，这些连接往往浪费很多的系统资源，将最近最少使用的连接对象
            //从队列中删除掉
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
                //重新置新时间的最大连接数计数器为0
                nMaxConn = 0;
                nHour = hour;
            }
        }
    }

    /**
     * 获取连接池中的连接数
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
 * 数据连接的自封装，屏蔽了close方法
 * @author Liudong
 */
class _Connection implements InvocationHandler
{
    private final static String CLOSE_METHOD_NAME = "close";
    private Connection conn = null;
//数据库的忙状态
    private boolean inUse = false;
//用户最后一次访问该连接方法的时间
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
     * 该方法真正的关闭了数据库的连接
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
            //更新最近一次访问时间
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
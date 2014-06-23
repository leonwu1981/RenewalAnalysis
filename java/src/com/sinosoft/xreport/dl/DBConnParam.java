package com.sinosoft.xreport.dl;

/**
 * �����������Դ����XML���ṩ���ݿ����ӵĸ��ֲ���
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author houzw
 * @version 1.0
 */

import java.io.Serializable;


public class DBConnParam implements Serializable
{

    /*
      private String driver = "sun.jdbc.odbc.JdbcOdbcDriver";//���ݿ���������
      private String url = "jdbc:odbc:myTest";		//�������ӵ�URL
      private String user = "sa";				//���ݿ��û���
      private String password = "";			//���ݿ�����
      private int minConnection = 0;		//��ʼ��������
      private int maxConnection = 50;		//���������
      private long timeoutValue = 60000;	//���ӵ�������ʱ��
      private long waitTime = 80000;		//ȡ���ӵ�ʱ�����û�п����������ĵȴ�ʱ��
     */

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String driver; //���ݿ���������
    private String url; //�������ӵ�URL
    private String user; //���ݿ��û���
    private String password; //���ݿ�����
    private int minConnection = 0; //��ʼ��������
    private int maxConnection = 50; //���������
    private long timeoutValue = 60000; //���ӵ�������ʱ��
    private long waitTime = 80000; //ȡ���ӵ�ʱ�����û�п����������ĵȴ�ʱ��

    static DBConfigXMLParse dbxml = null;

    private boolean isParse()
    {
        if (dbxml == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    private DBConfigXMLParse doParse() throws Exception
    {
        if (!isParse())
        {
            dbxml = new DBConfigXMLParse();
            DBConfigXMLParse.doXMLParse();
        }
        return dbxml;
    }


    //public DBConnParam(){}
    public DBConnParam(String anything)
    {
        //do nothing
    }

    public DBConnParam() throws Exception
    {

//    DBConfigXMLParse db = new DBConfigXMLParse();
//    try
//    {
//      db.doXMLParse();
//    }
//    catch(Exception e)
//    {
//      System.out.println(e.toString());
//    }

        DBConfigXMLParse db = doParse();
        driver = db.getDriverClassName("ҵ��");
        url = db.getDBConnectString("ҵ��");
        user = db.getUserName("ҵ��");
        password = db.getUserPassword("ҵ��");
        minConnection = Integer.parseInt(db.getMinConnection("ҵ��"));
        maxConnection = Integer.parseInt(db.getMaxConnection("ҵ��"));
        timeoutValue = Long.parseLong(db.getTimeOut("ҵ��"));
        waitTime = Long.parseLong(db.getWaitTime("ҵ��"));

        //driver = "sun.jdbc.odbc.JdbcOdbcDriver";
        //url="jdbc:odbc:myTest";
        //user="sa";
        //password="";
    }


    public DBConnParam(String Driver, String Url, String User, String Password)
    {
        driver = Driver;
        url = Url;
        user = User;
        password = Password;
    }

    /**
     * Returns the driver.
     * @return String
     */
    public String getDriver()
    {
        return driver;
    }

    /**
     * Returns the password.
     * @return String
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Returns the url.
     * @return String
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Returns the user.
     * @return String
     */
    public String getUser()
    {
        return user;
    }

    /**
     * Sets the driver.
     * @param driver The driver to set
     */
    public void setDriver(String driver)
    {
        this.driver = driver;
    }

    /**
     * Sets the password.
     * @param password The password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Sets the url.
     * @param url The url to set
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * Sets the user.
     * @param user The user to set
     */
    public void setUser(String user)
    {
        this.user = user;
    }

    /**
     * Returns the maxConnection.
     * @return int
     */
    public int getMaxConnection()
    {
        return maxConnection;
    }

    /**
     * Returns the minConnection.
     * @return int
     */
    public int getMinConnection()
    {
        return minConnection;
    }

    /**
     * Returns the timeoutValue.
     * @return int
     */
    public long getTimeoutValue()
    {
        return timeoutValue;
    }

    /**
     * Sets the maxConnection.
     * @param maxConnection The maxConnection to set
     */
    public void setMaxConnection(int maxConnection)
    {
        this.maxConnection = maxConnection;
    }

    /**
     * Sets the minConnection.
     * @param minConnection The minConnection to set
     */
    public void setMinConnection(int minConnection)
    {
        this.minConnection = minConnection;
    }

    /**
     * Returns the waitTime.
     * @return long
     */
    public long getWaitTime()
    {
        return waitTime;
    }

    /**
     * Sets the timeoutValue.
     * @param timeoutValue The timeoutValue to set
     */
    public void setTimeoutValue(long timeoutValue)
    {
        this.timeoutValue = timeoutValue;
    }

    /**
     * Sets the waitTime.
     * @param waitTime The waitTime to set
     */
    public void setWaitTime(long waitTime)
    {
        this.waitTime = waitTime;
    }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        DBConnParam param = new DBConnParam(driver, url, user, password);
        param.setMaxConnection(maxConnection);
        param.setMinConnection(minConnection);
        param.setTimeoutValue(timeoutValue);
        param.setWaitTime(waitTime);
        return param;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof DBConnParam)
        {
            DBConnParam param = (DBConnParam) obj;
            return compare(driver, param.getDriver()) &&
                    compare(url, param.getUrl()) &&
                    compare(user, param.getUser()) &&
                    compare(password, param.getPassword());
        }
        return false;
    }

    public static boolean compare(Object obj1, Object obj2)
    {
        boolean bResult = false;
        if (obj1 != null)
        {
            bResult = obj1.equals(obj2);
        }
        else
        if (obj2 != null)
        {
            bResult = obj2.equals(obj1);
        }
        else
        {
            bResult = true;
        }
        return bResult;
    }
    /*
       public static void main(String args[])
       {
       DBConnParam d = new DBConnParam();
       }
     */
}

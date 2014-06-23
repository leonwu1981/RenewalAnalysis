package com.sinosoft.xreport.dl;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author yang , houzw
 * @version 1.0
 */
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.sinosoft.xreport.util.SmartContentHandler;
import com.sinosoft.xreport.util.Str;
import com.sinosoft.xreport.util.XReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class DBConfigXMLParse extends SmartContentHandler
{

    public static String FILENAME = "DBConfig.xml";
//    private TableDTDHandler dtdHandler;
//    private PrintErrorHandler errorHandler;
    private String driverClassName;
    private String driverName;
    private String serverHost;
    private String servicePort;
    private String databaseName;
    private String serverName;
    private String userName;
    private String userPassword;
    private String minConnection;
    private String maxConnection;
    private String timeOut;
    private String waitTime;
    private String configName;
    private static Hashtable dbconfig;

    public DBConfigXMLParse()
    {
    }

    protected String[] getAttributeNames(String methodName)
    {
        if (methodName.equals("DBConfigElement"))
        {
            return new String[0];
        }
        else if (methodName.equals("dbElement"))
        {
            return new String[]
                    {
                    "name"};
        }
        else if (methodName.equals("driverclassnameElement"))
        {
            return new String[0];
        }
        else if (methodName.equals("drivernameElement"))
        {
            return new String[0];
        }
        else if (methodName.equals("serverhostElement"))
        {
            return new String[0];
        }
        else if (methodName.equals("databasenameElement"))
        {
            return new String[0];
        }
        else if (methodName.equals("servernameElement"))
        {
            return new String[0];
        }
        else if (methodName.equals("usernameElement"))
        {
            return new String[0];
        }
        else if (methodName.equals("userpasswordElement"))
        {
            return new String[0];
        }
        else if (methodName.equals("minconnectionElement"))
        {
            return new String[0];
        }
        else if (methodName.equals("maxconnectionElement"))
        {
            return new String[0];
        }
        else if (methodName.equals("timeoutElement"))
        {
            return new String[0];
        }
        else if (methodName.equals("waittimeElement"))
        {
            return new String[0];
        }
        return null;
    }

    public void DBConfigElement()
    {}

    public void dbElement(String name)
    {
        configName = name;
    }

    public void driverclassnameElement()
    {}

    public void drivernameElement()
    {}

    public void serverhostElement()
    {}

    public void serviceportElement()
    {}

    public void databasenameElement()
    {}

    public void servernameElement()
    {}

    public void usernameElement()
    {}

    public void userpasswordElement()
    {}

    public void minconnectionElement()
    {}

    public void maxconnectionElement()
    {}

    public void timeoutElement()
    {}

    public void waittimeElement()
    {}


    public void DBConfigData(String data)
    {}

    public void dbData(String data)
    {}

    public void driverclassnameData(String data)
    {
        driverClassName = data;
    }

    public void drivernameData(String data)
    {
        driverName = data;
    }

    public void serverhostData(String data)
    {
        serverHost = data;
    }

    public void serviceportData(String data)
    {
        servicePort = data;
    }

    public void databasenameData(String data)
    {
        databaseName = data;
    }

    public void servernameData(String data)
    {
        serverName = data;
    }

    public void usernameData(String data)
    {
        userName = data;
    }

    public void userpasswordData(String data)
    {
        userPassword = data;
    }

    public void minconnectionData(String data)
    {
        minConnection = data;
    }

    public void maxconnectionData(String data)
    {
        maxConnection = data;
    }

    public void timeoutData(String data)
    {
        timeOut = data;
    }

    public void waittimeData(String data)
    {
        waitTime = data;
    }

    public void DBConfigElementEnd()
    {}

    public void dbElementEnd()
    {
        String conn = "";
        String[] value;

        if (driverClassName.indexOf("informix") >= 0)
        {
            if (driverName != null || !driverName.equalsIgnoreCase(""))
            {
                conn = conn + "jdbc:" + driverName;
            }
            if (serverHost != null || !serverHost.equalsIgnoreCase(""))
            {
                conn = conn + "://" + serverHost;
            }
            if (servicePort != null || !servicePort.equalsIgnoreCase(""))
            {
                conn = conn + ":" + servicePort;
            }
            if (databaseName != null || !databaseName.equalsIgnoreCase(""))
            {
                conn = conn + "/" + databaseName;
            }
            if (serverName != null || !serverName.equalsIgnoreCase(""))
            {
                conn = conn + ":informixserver=" + serverName;
            }
            if (userName != null || !userName.equalsIgnoreCase(""))
            {
                conn = conn + ";user=" + userName;
            }
            if (userPassword != null || !userPassword.equalsIgnoreCase(""))
            {
                conn = conn + ";password=" + userPassword;
            }
            //if(minConnection != null || ! minConnection.equalsIgnoreCase("")) conn = conn + ";"
            value = new String[]
                    {
                    driverClassName,
                    conn,
                    driverName,
                    serverHost,
                    servicePort,
                    databaseName,
                    serverName,
                    userName,
                    userPassword,
                    minConnection,
                    maxConnection,
                    timeOut,
                    waitTime
            };
            dbconfig.put(configName, value);
        }
        /******************************
         * 2002.10.28
         * yang changed for oracle driver
         *******************************/
        else if (driverClassName.indexOf("oracle") >= 0)
        {
            if (driverName != null || !driverName.equalsIgnoreCase(""))
            {
                conn = conn + "jdbc:" + driverName;
            }
            if (serverHost != null || !serverHost.equalsIgnoreCase(""))
            {
                conn = conn + ":@" + serverHost;
            }
            if (servicePort != null || !servicePort.equalsIgnoreCase(""))
            {
                conn = conn + ":" + servicePort;
            }
            if (databaseName != null || !databaseName.equalsIgnoreCase(""))
            {
                conn = conn + ":" + databaseName;
            }
            value = new String[]
                    {
                    driverClassName,
                    conn,
                    driverName,
                    serverHost,
                    servicePort,
                    databaseName,
                    serverName,
                    userName,
                    userPassword,
                    minConnection,
                    maxConnection,
                    timeOut,
                    waitTime
            };
            dbconfig.put(configName, value);
        }
        /******************************
         * 2002.10.28
         * yang changed for sqlserver driver
         *******************************/
        else if (driverClassName.indexOf("com.inet.tds") >= 0)
        {
            if (driverName != null || !driverName.equalsIgnoreCase(""))
            {
                conn = conn + "jdbc:" + driverName;
            }
            if (serverHost != null || !serverHost.equalsIgnoreCase(""))
            {
                conn = conn + ":" + serverHost;
            }
            if (servicePort != null || !servicePort.equalsIgnoreCase(""))
            {
                conn = conn + ":" + servicePort;
            }
            if (databaseName != null || !databaseName.equalsIgnoreCase(""))
            {
                conn = conn + "?database=" + databaseName;
            }
            if (serverName != null || !serverName.equalsIgnoreCase(""))
            {
                conn = conn + Str.replace(serverName, "@", "&");
            }
            value = new String[]
                    {
                    driverClassName,
                    conn,
                    driverName,
                    serverHost,
                    servicePort,
                    databaseName,
                    serverName,
                    userName,
                    userPassword,
                    minConnection,
                    maxConnection,
                    timeOut,
                    waitTime
            };
            dbconfig.put(configName, value);
        }
    }

    public void driverclassnameElementEnd()
    {}

    public void drivernameElementEnd()
    {}

    public void serverhostElementEnd()
    {}

    public void serviceportElementEnd()
    {}

    public void databasenameElementEnd()
    {}

    public void servernameElementEnd()
    {}

    public void usernameElementEnd()
    {}

    public void userpasswordElementEnd()
    {}

    public void minconnectionElementEnd()
    {}

    public void maxconnectionElementEnd()
    {}

    public void timeoutElementEnd()
    {}

    public void waittimeElementEnd()
    {}

    public static void doXMLParse() throws Exception
    {
        try
        {
            dbconfig = new Hashtable();
            //创建本类的一个实例
            DBConfigXMLParse dbcfgReader = new DBConfigXMLParse();

            /*********************
                  yang add 15:03 07/12/2002
             **********************/

            System.setProperty("javax.xml.parsers.SAXParserFactory",
                               "org.apache.xerces.jaxp.SAXParserFactoryImpl");
            /**********
             * end yang added
             **********/



            //创建SAX分析器的JAXP工厂
            SAXParserFactory saxFactory = SAXParserFactory.newInstance();
            saxFactory.setValidating(false);
            saxFactory.setNamespaceAware(false);
            //创建JAXP SAX分析器
            SAXParser saxParser = saxFactory.newSAXParser();
            //获得XML读者
            XMLReader xmlReader = saxParser.getXMLReader();
            //设置XML

            xmlReader.setContentHandler(dbcfgReader);
            //解析文件
//      xmlReader.parse(new InputSource(dbcfgReader.getClass().getResource(FILENAME).toString()));
            StringReader sr = new StringReader(XReader.readConf("DBConfig.xml"));
            xmlReader.parse(new InputSource(sr));
            //xmlReader.parse(new InputSource("d://xreport//src//com//sinosoft//xreport//dl//DBConfig.xml"));
        }
        catch (SAXException e)
        {
            e.printStackTrace();
            //throw new Exception(e.getMessage() + "\r\n");
        }
    }

    public String[] getDBConfig(String name)
    {
        String[] value;
        value = (String[]) dbconfig.get(name);
        return value;
    }

    public String getDriverClassName(String name)
    {
        String[] value;
        value = (String[]) dbconfig.get(name);
        return value[0];
    }

    public String getDBConnectString(String name)
    {
        String[] value;
        value = (String[]) dbconfig.get(name);
        return value[1];
    }

    /**
     * get driver name
     * added on 2003.03.11
     * @author houzw
     * @param name
     * @return
     */
    public String getDriverName(String name)
    {
        return ((String[]) dbconfig.get(name))[2];
    }

    /**
     * get ServerHost
     * added on 2003.03.11
     * @author houzw
     * @param name
     * @return
     */
    public String getServerHost(String name)
    {
        return ((String[]) dbconfig.get(name))[3];
    }

    /**
     * get ServicePort
     * added on 2003.03.11
     * @author houzw
     * @param name
     * @return
     */
    public String getServicePort(String name)
    {
        return ((String[]) dbconfig.get(name))[4];
    }

    /**
     * get DatabaseName
     * added on 2003.03.11
     * @author houzw
     * @param name
     * @return
     */
    public String getDatabaseName(String name)
    {
        return ((String[]) dbconfig.get(name))[5];
    }

    /**
     * get ServerName
     * added on 2003.03.11
     * @author houzw
     * @param name
     * @return
     */
    public String getServerName(String name)
    {
        return ((String[]) dbconfig.get(name))[6];
    }

    /**
     * get user's name
     * added on 2002.10.28
     * @author yang
     * @param name
     * @return
     */
    public String getUserName(String name)
    {
        return ((String[]) dbconfig.get(name))[7];
    }

    /**
     * get user's passwd
     * added on 2002.10.28
     * @author yang
     * @param name
     * @return
     */
    public String getUserPassword(String name)
    {
        return ((String[]) dbconfig.get(name))[8];
    }

    /**
     * get pool's min capicity
     * added on 2003.03.11
     * @author houzw
     * @param name
     * @return
     */
    public String getMinConnection(String name)
    {
        return ((String[]) dbconfig.get(name))[9];
    }

    /**
     * get pool's max capicity
     * added on 2003.03.11
     * @author houzw
     * @param name
     * @return
     */
    public String getMaxConnection(String name)
    {
        return ((String[]) dbconfig.get(name))[10];
    }

    /**
     * get pool's connect timeout
     * added on 2003.03.11
     * @author houzw
     * @param name
     * @return
     */
    public String getTimeOut(String name)
    {
        return ((String[]) dbconfig.get(name))[11];
    }

    /**
     * get pool's connect waittime
     * added on 2003.03.11
     * @author houzw
     * @param name
     * @return
     */
    public String getWaitTime(String name)
    {
        return ((String[]) dbconfig.get(name))[12];
    }

    public String[] getDBConfigNames()
    {
        String[] name = new String[dbconfig.size()];
        Enumeration e;

        e = dbconfig.keys();
        int i = 0;
        while (e.hasMoreElements())
        {
            name[i] = (String) e.nextElement();
            i++;
        }
        return name;
    }


    public static void main(String[] args) throws Exception
    {
//        DBConfigXMLParse test = new DBConfigXMLParse();
//        try
//        {
//            test.doXMLParse();
//        }
//        catch (Exception e)
//        {
//            System.out.println(e.toString());
//        }
//        System.out.println(test.getMaxConnection("业务"));
    }

}

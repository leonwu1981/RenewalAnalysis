/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.io.*;
import java.util.Iterator;

import org.dom4j.*;
import org.dom4j.io.*;

import com.sinosoft.lis.encrypt.LisIDEA;

/**
 * <p>ClassName: JdbcUrl </p>
 * <p>Description: 构建 Jdbc 的 url </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sinosoft </p>
 * @author: HST
 * @version: 1.0
 * @date: 2002-05-31
 */
public class JdbcUrl
{
    // @Field
    private String DBType;
    private String IP1;
    private String Port1;
    private String IP2;
    private String Port2;
    private String DBName;
    private String ServerName;
    private String UserName; 
    private String PassWord;


    // @Constructor
    public JdbcUrl()
    {
        String wasInstallPath = System.getProperty("was.install.root");
        if(wasInstallPath!=null&&wasInstallPath.length()>0){
            DBType = "WEBSPHERE";
            DBName = "MyPool"; 
        }else{
        	String currentClassPath = Thread.currentThread().getContextClassLoader().getResource("").getFile();
        	File file = new File(currentClassPath);
        	file = new File(file.getParent()+"/jdbcurlconfig.xml");
        	//System.out.println(file.exists()?"存在jdbcurlconfig.xml":"不存在jdbcurlconfig.xml");
        	if(file.exists()){
            	SAXReader reader = new SAXReader();
        		reader.setEncoding("GB2312");
        		try {
        			Document doc = reader.read(file);
        			Element root = doc.getRootElement();
        			Element foo;
        			Iterator i;
        			String lowerName,content;
        			for (i = root.elementIterator();i.hasNext();){
        				foo = (Element)i.next();
        				lowerName = foo.getName().toLowerCase();
        				content = foo.getTextTrim();
        				if(content.length()%16==0){
        					content = this.decryptString(content);
        				}
        				if(lowerName.equals("dbtype")){
        					DBType = content;
        				}else if(lowerName.equals("ip1")){
        					IP1 = content;
        				}else if(lowerName.equals("port1")){
        					Port1 = content;
        				}else if(lowerName.equals("ip2")){
        					IP2 = content;
        				}else if(lowerName.equals("port2")){
        					Port2 = content;
        				}else if(lowerName.equals("dbname")){
        					DBName = content;
        				}else if(lowerName.equals("username")){
        					UserName = content;
        				}else if(lowerName.equals("password")){
        					PassWord = content;
        				}
        			}
        			i = null;
        			foo = null;
        			root = null;
        			reader = null;
        			doc = null;
        			file = null;
        		} catch (DocumentException e) {
        			e.printStackTrace();
        		}
        	}else{
        		System.out.println("There isn't any DataBase Config xml file,please check it!");
        		return;
        	}
        }
    }


    // @Method
    public String getDBType()
    {
        return DBType;
    }

    public String getIP1()
    {
        return IP1;
    }

    public String getPort1()
    {
        return Port1;
    }

    public String getIP2()
    {
        return IP2;
    }

    public String getPort2()
    {
        return Port2;
    }
    
    public String getDBName()
    {
        return DBName;
    }

    public String getServerName()
    {
        return ServerName;
    }

    public String getUserName()
    {
        return UserName;
    }

    public String getPassWord()
    {
        return PassWord;
    }

    public void setDBType(String aDBType)
    {
        DBType = aDBType;
    }

    public void setIP1(String aIP)
    {
        IP1 = aIP;
    }

    public void setPort1(String aPort)
    {
        Port1 = aPort;
    }

    public void setIP2(String aIP)
    {
        IP2 = aIP;
    }

    public void setPort2(String aPort)
    {
        Port2 = aPort;
    }
    
    public void setDBName(String aDBName)
    {
        DBName = aDBName;
    }

    public void setServerName(String aServerName)
    {
        ServerName = aServerName;
    }

    public void setUser(String aUserName)
    {
        UserName = aUserName;
    }

    public void setPassWord(String aPassWord)
    {
        PassWord = aPassWord;
    }


    /**
     * 获取连接句柄
     * @return String
     */
    public String getJdbcUrl()
    {
        StringBuffer sUrl = new StringBuffer(256);
        //Oracle连接句柄
        if (DBType.trim().toUpperCase().equals("ORACLE"))
        {
        	if(IP2!=null&&!IP2.equals(""))
        	{
        		sUrl.append("jdbc:oracle:thin:@(DESCRIPTION =");
            	sUrl.append("(ADDRESS_LIST =");
            	sUrl.append("(ADDRESS = (PROTOCOL = TCP)(HOST = "+IP1+")(PORT = "+Port1+"))");
            	sUrl.append("(ADDRESS = (PROTOCOL = TCP)(HOST = "+IP2+")(PORT = "+Port2+"))");
            	sUrl.append("(LOAD_BALANCE = yes)");
            	sUrl.append("(FAILOVER = on)");
            	sUrl.append(")");
            	sUrl.append("(CONNECT_DATA =");
            	sUrl.append("(SERVICE_NAME = "+DBName+")");
            	sUrl.append("(FAILOVER_MODE = ");
            	sUrl.append("(TYPE = SELECT)");
            	sUrl.append("(METHOD = BASIC)");
            	sUrl.append("(RETRIES = 180)");
            	sUrl.append("(DELAY = 10)");
            	sUrl.append(")");
            	sUrl.append(")");
            	sUrl.append(")");
        	}
        	else
        	{
        		 sUrl.append("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST = (ADDRESS = (PROTOCOL = TCP)(HOST=");
                 sUrl.append(IP1);
                 sUrl.append(")(PORT =");
                 sUrl.append(Port1);
                 sUrl.append(")))(CONNECT_DATA = (SERVICE_NAME =");
                 sUrl.append(DBName);
                 sUrl.append(")))");
        	}
        	//sUrl.append("(LOAD_BALANCE = yes)(FAILOVER = on))(CONNECT_DATA =(SERVICE_NAME = LIS)(FAILOVER_MODE = (TYPE = SELECT)(METHOD = BASIC)(RETRIES = 20)(DELAY = 10))))");
//        	System.out.println("oracle:"+sUrl.toString());
        }
        //InforMix连接句柄
        if (DBType.trim().toUpperCase().equals("INFORMIX"))
        {
            sUrl.append("jdbc:informix-sqli://");
            sUrl.append(IP1);
            sUrl.append(":");
            sUrl.append(Port1);
            sUrl.append(DBName);
            sUrl.append(":");
            sUrl.append("informixserver=");
            sUrl.append(ServerName);
            sUrl.append(";");
            sUrl.append("user=");
            sUrl.append(UserName);
            sUrl.append(";");
            sUrl.append("password=");
            sUrl.append(PassWord);
            sUrl.append(";");
        }
        //SqlServer连接句柄
        if (DBType.trim().toUpperCase().equals("SQLSERVER"))
        {
            sUrl.append("jdbc:inetdae:");
            sUrl.append(IP1);
            sUrl.append(":");
            sUrl.append(Port1);
            sUrl.append("?sql7=true&database=");
            sUrl.append(DBName);
            sUrl.append("&charset=gbk");
        }
        //WebLogicPool连接句柄
        if (DBType.trim().toUpperCase().equals("WEBLOGICPOOL"))
        {
            sUrl.append("jdbc:weblogic:pool:");
            sUrl.append(DBName);
        }
        //DB2连接句柄
        if (DBType.trim().toUpperCase().equals("DB2"))
        {
            sUrl.append("jdbc:db2://");
            sUrl.append(IP1);
            sUrl.append(":");
            sUrl.append(Port1);
            sUrl.append("/");
            sUrl.append(DBName);
        }
        //SysBase连接句柄
        if (DBType.trim().toUpperCase().equals("SYBASE"))
        {
            sUrl.append("jdbc:sybase:Tds:");
            sUrl.append(IP1);
            sUrl.append(":");
            sUrl.append(Port1);
            sUrl.append("/");
            sUrl.append(DBName);
        }
        return sUrl.toString();
    }
	private String decryptString(String encryptText){
		LisIDEA lisieda = new LisIDEA();
    	String decrypt="";
    	int strlen=encryptText.length(),len=0;
    	strlen=encryptText.length();
    	if(strlen%16==0){
    		len = strlen / 16;
    	}else if(strlen%16>0){
    		len = strlen / 16 + 1;
    	}
    	for(int i=0;i<len;i++){
    		decrypt += lisieda.decryptString(encryptText.substring(i*16, (i+1)*16));
    	}
    	return decrypt;
	}
}

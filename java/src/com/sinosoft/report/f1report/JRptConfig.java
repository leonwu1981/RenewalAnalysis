package com.sinosoft.report.f1report;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class JRptConfig extends DefaultHandler
{
    private static String configPath = "";
    private static String templatePath = "";
    private static String generatedPath = "";
    private static String configFileName = "";
    private static String urlPrefix = "";
    private static String pathType = "false";
    private static String appPath = "";
    private String currentElement = "";
    private String currentValue = "";
    private static ServletContext servletCtx;

    public JRptConfig()
    {

    }

    public JRptConfig(String fileName)
    {
        if (fileName != null)
        {
            configFileName = fileName;
            setup(null);
        }
    }

    public static synchronized void setup(PageContext pCtx)
    {
        setup(pCtx.getServletContext(), (HttpServletRequest) pCtx.getRequest());
    }

    public static synchronized void setup(ServletContext sCtx,
                                          HttpServletRequest req)
    {
        if (configPath.equalsIgnoreCase(""))
        {
            try
            {
                {
                    appPath = req.getScheme() + "://" + req.getServerName() +
                              ":" + req.getServerPort() + req.getContextPath();
                    servletCtx = sCtx;
                }
                if (configFileName.equalsIgnoreCase(""))
                {
                    StringBuffer sb = new StringBuffer();
                    sb.append("WEB-INF/JRptConfig.xml");
                    configFileName = servletCtx.getRealPath(sb.toString());
                }
                SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                parserFactory.setValidating(false);
                parserFactory.setNamespaceAware(false);
                JRptConfig JRptConfigInstance = new JRptConfig();
                //System.out.println("JRptConfig:setup:configFilename:"+configFileName);
                SAXParser parser = parserFactory.newSAXParser();
                parser.parse(configFileName, JRptConfigInstance);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
    }

    public void characters(char[] ch, int start, int length) throws
            SAXException
    {
        this.currentValue = new String(ch, start, length);
    }

    public void endDocument() throws SAXException
    {
        if (pathType.equalsIgnoreCase("false"))
        {
            configPath = servletCtx.getRealPath(configPath);
            templatePath = servletCtx.getRealPath(templatePath);
            urlPrefix = new String(generatedPath);
            urlPrefix = appPath + "/" + urlPrefix;
            //System.out.println("生成路径2"+generatedPath);
            generatedPath = servletCtx.getRealPath(generatedPath);
            //System.out.println("生成路径3"+generatedPath);
        }
    }

    public void endElement(String url, String localName, String qName) throws
            SAXException
    {
        System.out.println("JRptConfig:endElement:" + this.currentElement + " " +
                           url + " " + localName);

        if (this.currentElement.equalsIgnoreCase("config-directory"))
        {
            configPath = this.currentValue;
        }
        else if (this.currentElement.equalsIgnoreCase("template-directory"))
        {
            templatePath = this.currentValue;
        }
        else if (this.currentElement.equalsIgnoreCase("generated-directory"))
        {
            if (generatedPath.equalsIgnoreCase("")) //解析器重复解析此元素多次，原因待查明
            {
                generatedPath = this.currentValue;
                //System.out.println("生成路径1" + generatedPath);
            }
        }
    }


    public void startDocument() throws SAXException
    {

    }

    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException
    {
        this.currentElement = qName;
        if (attributes != null && attributes.getValue(0) != null)
        {
            pathType = attributes.getValue(0);
        }
    }

    public static String getConfigPath()
    {
        return configPath;
    }

    public static String getTemplatePath()
    {
        return templatePath;
    }

    public static String getGeneratedPath()
    {
        return generatedPath;
    }

    public static String getUrlPrefix()
    {
        return urlPrefix;
    }

    public static String getAppPath()
    {
        return appPath;
    }

    public static void main(String[] args)
    {
//        JRptConfig config = new JRptConfig(
//                "F:\\LIS\\ui\\WEB-INF\\JRptConfig.xml");
    }
}

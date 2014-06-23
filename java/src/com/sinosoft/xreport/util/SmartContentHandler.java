package com.sinosoft.xreport.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public abstract class SmartContentHandler implements ContentHandler
{
    protected Locator documentLocator;
    protected Hashtable prefixMappings;
    protected Stack namespaceURIStack;
    protected Stack localNameStack;
    protected Stack qualifiedNameStack;

    protected abstract String[] getAttributeNames(String methodName);

    /**
     * 获得指定方法的参数类型
     * @param methodName 指定方法名
     * @return 参数类型数组，如果找到；否则返回空值(NULL)
     */
    protected Class[] getParameterTypes(String methodName)
    {
        //获得这个类的所有公用方法
        Method methods[] = getClass().getMethods();
        //遍历这个方法数组，返回第一个方法名匹配的参数值类型
        for (int i = 0; i < methods.length; i++)
        {
            if (methods[i].getName().equals(methodName))
            {
                return methods[i].getParameterTypes();
            }
        }
        return null;
    }

    protected Object invokeMethod(String name, Object param) throws
            SAXException
    {
        Class parameterTypes[] = null;
        Object parameterValues[] = null;
        if (param instanceof String)
        {
            parameterTypes = new Class[]
                             {
                             String.class};
            parameterValues = new Object[]
                              {
                              (String) param};
        }
        else if (param instanceof Attributes)
        {
            parameterTypes = getParameterTypes(name);
            String attributeNames[] = getAttributeNames(name);
            String stringValues[] = AttributesUtil.getValues((Attributes) param,
                    attributeNames);
            parameterValues = AttributesUtil.convertValues(stringValues,
                    parameterTypes);
        }
        else if (param == null)
        {
            parameterTypes = new Class[0];
            parameterValues = new Object[0];
        }
        if (parameterTypes == null || parameterValues == null)
        {
            return null;
        }
        try
        {
            Method method = getClass().getMethod(name, parameterTypes);
            return method.invoke(this, parameterValues);
        }
        catch (InvocationTargetException e)
        {
            Throwable t = e.getTargetException();
            if (t instanceof Exception)
            {
                throw new SAXException((Exception) t);
            }
            else
            {
                throw new SAXException(t.toString());
            }
        }
        catch (NoSuchMethodException e)
        {
            throw new SAXException(name + ":没有找到方法或此方法不是公有的");
        }
        catch (Exception e)
        {
            throw new SAXException(e);
        }
    }

    public void setDocumentLocator(Locator locator)
    {
        documentLocator = locator;
    }

    public void startDocument() throws SAXException
    {
        prefixMappings = new Hashtable();
        namespaceURIStack = new Stack();
        localNameStack = new Stack();
        qualifiedNameStack = new Stack();
    }

    public void endDocument() throws SAXException
    {
        prefixMappings = null;
        namespaceURIStack = null;
        localNameStack = null;
        qualifiedNameStack = null;
        System.gc();
    }

    public void startPrefixMapping(String prefix, String uri) throws
            SAXException
    {
        prefixMappings.put(prefix, uri);
    }

    public void endPrefixMapping(String prefix) throws SAXException
    {
        prefixMappings.remove(prefix);
    }

    public void startElement(String uri, String localName, String qName,
                             Attributes atts) throws SAXException
    {
        namespaceURIStack.push(uri);
        localNameStack.push(localName);
        qualifiedNameStack.push(qName);
//        invokeMethod(localName + "Element", atts);
        invokeMethod(qName + "Element", atts);
    }

    public void endElement(String uri, String localName, String qName) throws
            SAXException
    {
//        invokeMethod(localName + "ElementEnd", null);
        invokeMethod(qName + "ElementEnd", null);
        namespaceURIStack.pop();
        localNameStack.pop();
        qualifiedNameStack.pop();
    }

    public void characters(char ch[], int start, int length) throws
            SAXException
    {
//        String localName = (String) localNameStack.peek();
        String localName = (String) qualifiedNameStack.peek();
        String data = new String(ch, start, length);
        invokeMethod(localName + "Data", data);
    }

    public void ignorableWhitespace(char ch[], int start, int length) throws
            SAXException
    {
    }

    public void processingInstruction(String target, String data) throws
            SAXException
    {
        invokeMethod(target + "Proc", data);
    }

    public void skippedEntity(String name) throws SAXException
    {
        System.err.println("跳过实体：" + name);
    }
}
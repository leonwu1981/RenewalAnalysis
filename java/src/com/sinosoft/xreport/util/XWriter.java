package com.sinosoft.xreport.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author unascribed
 * @version 1.0
 */

public class XWriter
{

    public XWriter()
    {
    }

    /**
     * ��XML�ַ���д��XML�ļ�
     * @param xmlOut XML�ַ���
     * @param file �������ļ���
     */
    public static void writeDefine(String xmlOut, String file)
    {
        try
        {
            URL url = new URL(SysConfig.TRUEHOST + "up.jsp?type=define&file=" +
                              file);
            URLConnection uc = url.openConnection();
            uc.setDoOutput(true);
            OutputStream os = uc.getOutputStream();
            DataOutputStream output = new DataOutputStream(os);
            //�����xmlOut��GB2312��׼���ַ����������Զ����Ƶ�ģʽ���䣬��Ҫ����ת��
            xmlOut = CharacterCode.toConvertStr(xmlOut, "GB2312", "ISO8859_1");
            //д�ļ�
            output.writeBytes(xmlOut);
            uc.getInputStream();
            output.close();
            os.close();
        }
        catch (IOException ioEx)
        {
            ioEx.printStackTrace();
        }
    }

    /**
     * ��XML�ַ���д��XML�ļ�
     * @param xmlOut XML�ַ���
     * @param file �������ļ���
     */
    public static void writeData(String xmlOut, String file)
    {
        try
        {
            URL url = new URL(SysConfig.TRUEHOST + "up.jsp?type=data&file=" +
                              file);
            URLConnection uc = url.openConnection();
            uc.setDoOutput(true);
            OutputStream os = uc.getOutputStream();
            DataOutputStream output = new DataOutputStream(os);
            //�����xmlOut��GB2312��׼���ַ����������Զ����Ƶ�ģʽ���䣬��Ҫ����ת��
            xmlOut = CharacterCode.toConvertStr(xmlOut, "GB2312", "ISO8859_1");
            //д�ļ�
            output.writeBytes(xmlOut);
            uc.getInputStream();
            output.close();
            os.close();
        }
        catch (IOException ioEx)
        {
            ioEx.printStackTrace();
        }
    }

    /**
     * ��XML�ַ���д��XML�ļ�
     * @param xmlOut XML�ַ���
     * @param file �������ļ���
     */
    public static void writeConf(String xmlOut, String file)
    {
        try
        {
            URL url = new URL(SysConfig.TRUEHOST + "up.jsp?type=conf&file=" +
                              file);
            URLConnection uc = url.openConnection();
            uc.setDoOutput(true);
            OutputStream os = uc.getOutputStream();
            DataOutputStream output = new DataOutputStream(os);
            //�����xmlOut��GB2312��׼���ַ����������Զ����Ƶ�ģʽ���䣬��Ҫ����ת��
            xmlOut = CharacterCode.toConvertStr(xmlOut, "GB2312", "ISO8859_1");
            //д�ļ�
            output.writeBytes(xmlOut);
            output.flush();
            InputStream in = uc.getInputStream();

            output.close();
            os.close();
        }
        catch (IOException ioEx)
        {
            ioEx.printStackTrace();
        }
    }

    public static void writeStyle(String xmlOut, String file)
    {
        try
        {
            URL url = new URL(SysConfig.TRUEHOST + "up.jsp?type=style&file=" +
                              file);
            URLConnection uc = url.openConnection();
            uc.setDoOutput(true);
            OutputStream os = uc.getOutputStream();
            DataOutputStream output = new DataOutputStream(os);
            //�����xmlOut��GB2312��׼���ַ����������Զ����Ƶ�ģʽ���䣬��Ҫ����ת��
            xmlOut = CharacterCode.toConvertStr(xmlOut, "GB2312", "ISO8859_1");
            //д�ļ�
            output.writeBytes(xmlOut);
            output.flush();
            InputStream in = uc.getInputStream();

            output.close();
            os.close();
        }
        catch (IOException ioEx)
        {
            ioEx.printStackTrace();
        }
    }

    public static void deleteFile(String type, String file)
    {
        try
        {
            URL url = new URL(SysConfig.TRUEHOST + "delete.jsp?type=" + type +
                              "&file=" + file);
            URLConnection uc = url.openConnection();
            uc.setDoOutput(true);
            OutputStream os = uc.getOutputStream();
            InputStream in = uc.getInputStream();
            os.close();
        }
        catch (IOException ioEx)
        {
            ioEx.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        XWriter.deleteFile("conf", "1.xml");
    }

}
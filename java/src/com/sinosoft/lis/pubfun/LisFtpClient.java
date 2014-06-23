/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.lis.pubfun;

import java.io.DataOutputStream;
import com.sinosoft.lis.db.*;
import sun.net.TelnetOutputStream;
import sun.net.ftp.FtpClient;
import java.io.RandomAccessFile;

/**
 * <p>Title: LIS系统Ftp专用类</p>
 * <p>Description: 为保单打印、个人凭证打印、发票打印提供Ftp服务</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: sinosoft</p>
 * @author zhuxf
 * @version 1.0
 */
public class LisFtpClient
{
    private String mErrMessage = "";

    public LisFtpClient()
    {
    }

    /**
     * 上载文件到Ftp服务器
     * @param cFilePath String
     * @param cFileName String
     * @return boolean
     */
    public boolean UpLoadFile(String cFilePath, String cFileName)
    {
        String[] tString;

        LDSysVarDB tLDSysVarDB = new LDSysVarDB();
        tLDSysVarDB.setSysVar("FTPServer");
        if (!tLDSysVarDB.getInfo())
        {
            System.out.println("没有配置Ftp服务器信息...");
            mErrMessage = "没有配置Ftp服务器信息...";
            return false;
        }
        tString = tLDSysVarDB.getSysVarValue().split(",");

//        int port = 21;
//        String uid = "bitizxf";
//        String pwd = "biti78zxf";

        //连接ftp服务器
        FtpClient ftp = null;
        try
        {
            ftp = new FtpClient(tString[0], Integer.parseInt(tString[1]));
            ftp.login(tString[2], tString[3]);
            ftp.binary();
        }
        catch (Exception ex)
        {
            System.out.println("FtpServer连接失败..." + ex.toString());
            mErrMessage = "FtpServer连接失败..." + ex.toString();
            ex.printStackTrace();
            return false;
        }

        try
        {
            RandomAccessFile sendFile = new RandomAccessFile(cFilePath, "r");
            sendFile.seek(0);

            int ch;
            TelnetOutputStream outs = ftp.put(cFileName);
            DataOutputStream outputs = new DataOutputStream(outs);
            while (sendFile.getFilePointer() < sendFile.length())
            {
                ch = sendFile.read();
                outputs.write(ch);
            }

            outs.close();
            sendFile.close();

            ftp.closeServer();
        }
        catch (Exception ex)
        {
            System.out.println("文件上传失败..." + ex.toString());
            mErrMessage = "文件上传失败..." + ex.toString();
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public String GetErrMessage()
    {
        return mErrMessage;
    }

    public static void main(String[] args)
    {
//        LisFtpClient lisftpclient = new LisFtpClient();
    }
}

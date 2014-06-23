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
 * <p>Title: LISϵͳFtpר����</p>
 * <p>Description: Ϊ������ӡ������ƾ֤��ӡ����Ʊ��ӡ�ṩFtp����</p>
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
     * �����ļ���Ftp������
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
            System.out.println("û������Ftp��������Ϣ...");
            mErrMessage = "û������Ftp��������Ϣ...";
            return false;
        }
        tString = tLDSysVarDB.getSysVarValue().split(",");

//        int port = 21;
//        String uid = "bitizxf";
//        String pwd = "biti78zxf";

        //����ftp������
        FtpClient ftp = null;
        try
        {
            ftp = new FtpClient(tString[0], Integer.parseInt(tString[1]));
            ftp.login(tString[2], tString[3]);
            ftp.binary();
        }
        catch (Exception ex)
        {
            System.out.println("FtpServer����ʧ��..." + ex.toString());
            mErrMessage = "FtpServer����ʧ��..." + ex.toString();
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
            System.out.println("�ļ��ϴ�ʧ��..." + ex.toString());
            mErrMessage = "�ļ��ϴ�ʧ��..." + ex.toString();
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

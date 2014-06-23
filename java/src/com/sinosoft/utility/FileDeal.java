/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * <p>Title: lis</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: sinosoft</p>
 * @author lh
 * @version 1.0
 */
public class FileDeal
{

    public CErrors mErrors = new CErrors(); // ������Ϣ
    private String OriPathName;
    private String DesPathName;
    private File mFile;


//  private File DesFile;

    public FileDeal(String aStr)
    {
        OriPathName = aStr;
    }

    public boolean FileCopy(String aStr)
    {
        try
        {
            DesPathName = aStr;
            int nChar = -1;
            FileInputStream in = new FileInputStream(OriPathName);
            FileOutputStream out = new FileOutputStream(DesPathName);
            while ((nChar = in.read()) != -1)
            {
                out.write(nChar);
            }
            out.flush();
            in.close();
            out.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            // @@������
            CError tError = new CError();
            tError.moduleName = "FileDeal";
            tError.functionName = "FileCopy";
            tError.errorMessage = "�ļ����Ƴ���!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    public boolean FileMove(String aStr)
    {
        try
        {
            DesPathName = aStr;
            mFile = new File(OriPathName);
            File tFile = new File(DesPathName);
            if (!mFile.renameTo(tFile))
            {
                // @@������
                CError tError = new CError();
                tError.moduleName = "FileDeal";
                tError.functionName = "FileMove";
                tError.errorMessage = "�ļ��ƶ�����!";
                this.mErrors.addOneError(tError);
                return false;
            }
            if (!mFile.createNewFile())
            {
                // @@������
                CError tError = new CError();
                tError.moduleName = "FileDeal";
                tError.functionName = "FileMove";
                tError.errorMessage = "�ļ��ƶ�����!";
                this.mErrors.addOneError(tError);
                return false;
            }
            tFile = new File(OriPathName);
            if (!tFile.delete())
            {
                // @@������
                CError tError = new CError();
                tError.moduleName = "FileDeal";
                tError.functionName = "FileMove";
                tError.errorMessage = "�ļ��ƶ�����!";
                this.mErrors.addOneError(tError);
                return false;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            // @@������
            CError tError = new CError();
            tError.moduleName = "FileDeal";
            tError.functionName = "FileMove";
            tError.errorMessage = "�ļ��ƶ�����!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    public boolean FileDel()
    {
        File tFile = new File(OriPathName);
        if (!tFile.delete())
        {
            // @@������
            CError tError = new CError();
            tError.moduleName = "FileDeal";
            tError.functionName = "FileDel";
            tError.errorMessage = "�ļ�ɾ������!";
            this.mErrors.addOneError(tError);
            return false;
        }
        return true;
    }

    public static void main(String[] args)
    {
        FileDeal fileDeal1 = new FileDeal("E:\\test.xml");
//    fileDeal1.FileDel();
        fileDeal1.FileCopy("E:\\ui\\test.xml");
    }
}

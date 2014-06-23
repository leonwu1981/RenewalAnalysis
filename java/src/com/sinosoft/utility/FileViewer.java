/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

public class FileViewer
{
    File myDir;
    File[] contents;
    Vector vectorList;
    Iterator currentFileView;
    File currentFile;
    String path;

    public FileViewer()
    {
        path = new String("");
        vectorList = new Vector();
    }

    public FileViewer(String path)
    {
        this.path = path;
        vectorList = new Vector();
    }


    /**
     * ���������·��
     * @param path String
     */
    public void setPath(String path)
    {
        this.path = path;
    }


    /**
     * ���ص�ǰĿ¼·��
     * @return String
     */
    public String getDirectory()
    {
        return myDir.getPath();
    }


    /**
     * ˢ���б�
     */
    public void refreshList()
    {
        if (this.path.equals(""))
        {
            path = "c:\\";
        }
        myDir = new File(path);

        vectorList.clear();
        contents = myDir.listFiles();
        //����װ��·�����ļ�
        for (int i = 0; i < contents.length; i++)
        {
            vectorList.add(contents[i]);
        }

        currentFileView = vectorList.iterator();
    }


    /**
     * �ƶ���ǰ�ļ����ϵ�ָ��ָ����һ����Ŀ
     * @return �ɹ�����true,����false
     */
    public boolean nextFile()
    {
        while (currentFileView.hasNext())
        {
            currentFile = (File) currentFileView.next();
            return true;
        }
        return false;
    }


    /**
     * ���ص�ǰָ����ļ�������ļ�����
     * @return String
     */
    public String getFileName()
    {
        return currentFile.getName();
    }


    /**
     * ���ص�ǰָ����ļ�������ļ��ߴ�
     * @return String
     */
    public String getFileSize()
    {
        return new Long(currentFile.length()).toString();
    }


    /**
     * ���ص�ǰָ����ļ����������޸�����
     * @return String
     */
    public String getFileTimeStamp()
    {
        return new Date(currentFile.lastModified()).toString();
    }


    /**
     * ���ص�ǰָ����ļ������Ƿ���һ���ļ�Ŀ¼
     * @return boolean
     */
    public boolean getFileType()
    {
        return currentFile.isDirectory();
    }

    /**
     * ���Ժ���
     * @param args String[]
     */
    public static void main(String[] args)
    {
//        System.out.println("File List");
//        FileViewer f = new FileViewer();
//        f.setPath("d:\\");
//        f.refreshList();
//        while (f.nextFile())
//        {
//            System.out.print(f.getFileName());
//            if (!f.getFileType())
//            {
//                System.out.print("  " + f.getFileSize());
//            }
//            else
//            {
//                System.out.print("  ");
//            }
//            System.out.print(f.getFileTimeStamp() + "\n");
//        }
    }
}

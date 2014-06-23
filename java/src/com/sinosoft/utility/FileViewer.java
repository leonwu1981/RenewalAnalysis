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
     * 设置浏览的路径
     * @param path String
     */
    public void setPath(String path)
    {
        this.path = path;
    }


    /**
     * 返回当前目录路径
     * @return String
     */
    public String getDirectory()
    {
        return myDir.getPath();
    }


    /**
     * 刷新列表
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
        //重新装入路径下文件
        for (int i = 0; i < contents.length; i++)
        {
            vectorList.add(contents[i]);
        }

        currentFileView = vectorList.iterator();
    }


    /**
     * 移动当前文件集合的指针指到下一个条目
     * @return 成功返回true,否则false
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
     * 返回当前指向的文件对象的文件名称
     * @return String
     */
    public String getFileName()
    {
        return currentFile.getName();
    }


    /**
     * 返回当前指向的文件对象的文件尺寸
     * @return String
     */
    public String getFileSize()
    {
        return new Long(currentFile.length()).toString();
    }


    /**
     * 返回当前指向的文件对象的最后修改日期
     * @return String
     */
    public String getFileTimeStamp()
    {
        return new Date(currentFile.lastModified()).toString();
    }


    /**
     * 返回当前指向的文件对象是否是一个文件目录
     * @return boolean
     */
    public boolean getFileType()
    {
        return currentFile.isDirectory();
    }

    /**
     * 测试函数
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

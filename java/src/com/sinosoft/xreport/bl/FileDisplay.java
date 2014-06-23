package com.sinosoft.xreport.bl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Vector;

import com.sinosoft.xreport.util.SysConfig;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author lixy
 * @version 1.0
 */

public class FileDisplay
{
    /**数据文件根目录*/
    private File data = new File(SysConfig.FILEPATH + "data");

    /**定义单位目录列表*/
    private Vector vecDefine;
    /**计算单位目录列表*/
    private Vector vecCalculate;
    /**报表代码列表*/
    private Vector vecFile;
    /**报表版别列表*/
    private Vector vecEdition;

    public FileDisplay()
    {
    }

    public Vector getDefineDir()
    {
        if (vecDefine == null)
        {
            parseDir();
        }
        return vecDefine;
    }

    public Vector getCalculateDir()
    {
        if (vecCalculate == null)
        {
            parseDir();
        }
        return vecCalculate;
    }

    public Vector getFiles()
    {
        if (vecFile == null)
        {
            parseDir();
        }
        return vecFile;
    }

    public Vector getEdition()
    {
        if (vecEdition == null)
        {
            parseDir();
        }
        return vecEdition;
    }

    private void parseDir()
    {
        vecDefine = new Vector();
        vecCalculate = new Vector();
        vecFile = new Vector();
        vecEdition = new Vector();
        String[] defineBranch = data.list();
        for (int i = 0; i < defineBranch.length; i++)
        {
            if (!vecDefine.contains(defineBranch[i]))
            {
                vecDefine.addElement(defineBranch[i]);
            }
            File define = new File(SysConfig.FILEPATH + "data" +
                                   SysConfig.FILESEPARATOR + defineBranch[i]);
            String[] calculateBranch = define.list();
            for (int j = 0; j < calculateBranch.length; j++)
            {
                if (!vecCalculate.contains(calculateBranch[j]))
                {
                    vecCalculate.addElement(calculateBranch[j]);
                }
                File file = new File(SysConfig.FILEPATH + "data" +
                                     SysConfig.FILESEPARATOR + defineBranch[i] +
                                     SysConfig.FILESEPARATOR +
                                     calculateBranch[j]);
                String[] files = file.list(new DirFilter(".xls"));
                for (int k = 0; k < files.length; k++)
                {
                    String f = files[k];
                    int one = f.indexOf(SysConfig.REPORTJOINCHAR);
                    String code = f.substring(0, one);
                    if (!vecFile.contains(code))
                    {
                        vecFile.addElement(code);
                    }
                    int two = f.indexOf(SysConfig.REPORTJOINCHAR, one + 1);
                    String edition = f.substring(one + 1, two);
                    if (!vecEdition.contains(edition))
                    {
                        vecEdition.addElement(edition);
                    }
                }
            }
        }
    }

    public static void main(String[] args)
    {
        FileDisplay file = new FileDisplay();
        Vector vecEdition = file.getEdition();
        for (int i = 0; i < vecEdition.size(); i++)
        {
            System.out.println(vecEdition.elementAt(i));
        }
    }
}


class DirFilter implements FilenameFilter
{
    String filter = "";
    public DirFilter(String filter)
    {
        this.filter = filter;
    }

    public boolean accept(File dir, String name)
    {
        String f = new File(name).getName();
        return f.indexOf(filter) != -1;
    }
}

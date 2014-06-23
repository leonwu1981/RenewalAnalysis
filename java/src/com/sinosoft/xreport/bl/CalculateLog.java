package com.sinosoft.xreport.bl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import com.sinosoft.xreport.util.SysConfig;

/**
 * ���������־��¼,���Զ�д��־�����ļ�.
 * Ҫ����ձ�$sq()����,���������һ���ձ�,��Ϊ�޷��ж�����,�ڼ�������...
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author unascribed
 * @version 1.0
 */

public class CalculateLog
{

    private static Properties p;
    private static final String pFile = SysConfig.FILEPATH +
                                        "conf/calculate_log.properties";

    private CalculateLog()
    {
    }

    public static void main(String[] args)
    {
        CalculateLog calculateLog1 = new CalculateLog();
    }

    public static void setLast(String reportStr, String date)
    {
        init();
        p.put(reportStr, date);
    }

    public static String getLast(String reportStr)
    {
        init();
        return p.get(reportStr) != null ? p.get(reportStr).toString() : null;
    }

    public static void save() throws Exception
    {
//    p.store(new FileOutputStream(pFile),"����ÿ�ű�������һ��,�Ա�ʹ������$sq()����.Warning:�ֶ��������ѯPropertes�ļ���ʽ. --XTeam");
        p.store(new FileOutputStream(pFile), "This file record last calculate date of each report , so that the function $sq() is validate.Warning:Manual change must shutdown server,reference Properties file format. --XTeam  2003/05/14");
    }

    private static void init()
    {
        if (p == null)
        {
            p = new Properties();
            FileInputStream fis = null;

            try
            {
                fis = new FileInputStream(pFile);
                p.load(fis);

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            } finally
            {
                try
                {
                    fis.close();
                }
                catch (Exception e)
                {}
            }

        }

    }

}
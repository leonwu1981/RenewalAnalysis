package com.sinosoft.xreport.bl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Yang Yalin
 * @version 1.0
 */

public class EnvironmentImpl implements Environment
{
    Map content = new HashMap();


    public EnvironmentImpl()
    {
    }

    public static void main(String[] args) throws Exception
    {
        EnvironmentImpl environmentImpl1 = new EnvironmentImpl();
        System.out.println(environmentImpl1.getEnv("calculateDate"));
    }


    public String getEnv(String envName) throws Exception
    {
        Object o = content.get(envName);
        if (o != null)
        {
            return o.toString();
        }
        else
        {
            try
            {
                o = getClass().getMethod("get" +
                                         envName.substring(0, 1).toUpperCase() +
                                         envName.substring(1), null).invoke(this, null).
                    toString();
                setEnv(envName, o);
                return o.toString();
            }
            catch (Exception ex)
            {
                return null;
            }
        }
    }

    public void setEnv(String envName, Object value)
    {
        content.put(envName, value);
    }

    ///////////////////////////////////////////////////
    //free to add your env calculate

    /**
     * 得到结束时间.
     * @return
     */
    public String getEndDate()
    {
        return "2003-04-03";
    }

    /**
     * 得到系统当前日期.
     * 如果用户已经设置,使用用户设置,否则返回当天时间.
     * @return 系统计算时间
     */
    public String getCalculateDate()
    {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR)
                + "-"
                + (int) (c.get(Calendar.MONTH) + 1)
                + "-"
                + c.get(Calendar.DAY_OF_MONTH);
    }
}
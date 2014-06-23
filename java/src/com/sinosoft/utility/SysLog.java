/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * <p>Title: SysLog</p>
 * <p>Description: Use log4j to implements system log</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author Kevin
 * @version 1.0
 */

public class SysLog
{

    private static String DEFAULT_LOGGER = "com.sinosoft.utility";

    public static Logger logger = SysLog.getLogger(DEFAULT_LOGGER);

    public static final int OFF = 2147483647;
    public static final int FATAL = 50000;
    public static final int ERROR = 40000;
    public static final int WARN = 30000;
    public static final int INFO = 20000;
    public static final int DEBUG = 10000;
    public static final int ALL = -2147483648;

    public SysLog()
    {
    }

    /**
     * Kevin 2003-09-23
     * 将SysLog类中定义的日志级别映射到log4j的日志级别
     * @param nPriority int
     * @return Priority
     */
    private static Priority getPriority(int nPriority)
    {
        Priority priority = null;

        switch (nPriority)
        {
            case OFF:
                priority = Priority.toPriority(Priority.OFF_INT);
                break;
            case FATAL:
                priority = Priority.FATAL;
                break;
            case ERROR:
                priority = Priority.ERROR;
                break;
            case WARN:
                priority = Priority.WARN;
                break;
            case INFO:
                priority = Priority.INFO;
                break;
            case DEBUG:
                priority = Priority.DEBUG;
                break;
            case ALL:
                priority = Priority.toPriority(Priority.ALL_INT);
                break;
        }

        return priority;
    }

    /**
     * 得到记录器
     * @param className 类名
     * @return 记录器
     */
    public static Logger getLogger(String className)
    {
        return Logger.getLogger(className);
    }

    /**
     * 得到记录器
     * @param className 类
     * @return 记录器
     */
    public static Logger getLogger(Class className)
    {
        return Logger.getLogger(className);
    }

    public static void log(Object obj)
    {
        logger.info(obj);
    }

    public static void log(String strMessage)
    {
        logger.info(strMessage);
    }

    public static void log(int n)
    {
        logger.info(String.valueOf(n));
    }

    public static void log(float f)
    {
        logger.info(String.valueOf(f));
    }

    public static void log(double d)
    {
        logger.info(String.valueOf(d));
    }

    public static void log(int nLevel, Object obj)
    {
        logger.log(getPriority(nLevel), obj);
    }

    public static void log(int nLevel, String strMessage)
    {
        logger.log(getPriority(nLevel), strMessage);
    }

    public static void log(int nLevel, int n)
    {
        logger.log(getPriority(nLevel), String.valueOf(n));
    }

    public static void log(int nLevel, float f)
    {
        logger.log(getPriority(nLevel), String.valueOf(f));
    }

    public static void log(int nLevel, double d)
    {
        logger.log(getPriority(nLevel), String.valueOf(d));
    }

    public static void main(String[] args)
    {
        SysLog sysLog = new SysLog();
        Logger log = SysLog.getLogger(SysLog.class);
        log.info(".......");
        log.info("///////");

        log = SysLog.getLogger("com.sinosoft.utility");
        log.info("com.sinosoft.utility");
    }
}

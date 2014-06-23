package com.sinosoft.xreport.dl.planereport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * <p>Title: XReport</p>
 * <p>Description: Perfect report tool...</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sinosoft co. Ltd.</p>
 * @author wuxiao
 * @version 1.0
 */
public class GeneralParameter
{

    private static String centerCode;
    private static String centerName;
    private static String beginTime;
    private static String endTime;
    private static String reportCode;
    private static String reportEdition;
    private static String branchCode;
    private static String branchName;

    public GeneralParameter()
    {}

    /**
     * 取得核算单位编码
     * @return 核算单位编码
     */
    public static String getCenterCode()
    {
        return centerCode;
    }

    /**
     * 取得核算单位名称
     * @return 核算单位名称
     */
    public static String getCenterName()
    {
        return centerName;
    }

    /**
     * 取得报表计算开始时间
     * @return 报表计算开始时间
     */
    public static String getBeginTime()
    {
        return beginTime;
    }

    /**
     * 取得报表计算结束时间
     * @return 报表计算结束时间
     */
    public static String getEndTime()
    {
        return endTime;
    }

    /**
     * 取得报表编码
     * @return 报表编码
     */
    public static String getReportCode()
    {
        return reportCode;
    }

    /**
     * 取得报表版别
     * @return 报表版别
     */
    public static String getReportEdition()
    {
        return reportEdition;
    }

    /**
     * 取得基层单位编码
     * @return 基层单位编码
     */
    public static String getBranchCode()
    {
        return branchCode;
    }

    /**
     * 取得基层单位编码
     * @return 基层单位编码
     */
    public static String getBranchName()
    {
        return branchName;
    }

    /**
     * 取得通用参数对象
     * @return 通用参数对象
     */
    public static GeneralParameter getGeneralParameter()
    {
        GeneralParameter gp = new GeneralParameter();
        GeneralParameter.setCenterCode(centerCode);
        GeneralParameter.setCenterCode(centerName);
        GeneralParameter.setBeginTime(beginTime);
        GeneralParameter.setEndTime(endTime);
        GeneralParameter.setReportCode(reportCode);
        GeneralParameter.setReportEdition(reportEdition);
        GeneralParameter.setBranchCode(branchCode);
        GeneralParameter.setBranchName(branchName);
        return gp;
    }

    /**
     * 设置核算单位编码
     * @param param 核算单位编码
     */
    public static void setCenterCode(String param)
    {
        centerCode = param;
    }

    /**
     * 设置核算单位名称
     * @param param 核算单位名称
     */
    public static void setCenterName(String param)
    {
        centerName = param;
    }

    /**
     * 设置报表计算开始时间
     * @param param 报表计算开始时间
     */
    public static void setBeginTime(String param)
    {
        beginTime = param;
    }

    /**
     * 设置报表计算结束时间
     * @param param 报表计算结束时间
     */
    public static void setEndTime(String param)
    {
        endTime = param;
    }

    /**
     * 设置报表编码
     * @param param 报表编码
     */
    public static void setReportCode(String param)
    {
        reportCode = param;
    }

    /**
     * 设置报表版别
     * @param param 报表版别
     */
    public static void setReportEdition(String param)
    {
        reportEdition = param;
    }

    /**
     * 设置基层单位编码
     * @param param 基层单位编码
     */
    public static void setBranchCode(String param)
    {
        branchCode = param;
    }

    /**
     * 设置基层单位名称
     * @param param 基层单位名称
     */
    public static void setBranchName(String param)
    {
        branchName = param;
    }

    /**
     * 设置通用参数对象
     * @param gp 通用参数对象
     */
    public static void setGeneralParameter(GeneralParameter gp)
    {
        centerCode = GeneralParameter.getCenterCode();
        centerName = GeneralParameter.getCenterCode();
        beginTime = GeneralParameter.getBeginTime();
        endTime = GeneralParameter.getEndTime();
        reportCode = GeneralParameter.getReportCode();
        reportEdition = GeneralParameter.getReportEdition();
        branchCode = GeneralParameter.getBranchCode();
        branchName = GeneralParameter.getBranchName();
    }

    /**
     * 取通用变量的值
     * @param paramName 通用变量名
     * @return 通用变量的值
     */
    public static String getParameterValue(String paramName)
    {
        if (paramName.equalsIgnoreCase("code"))
        {
            return centerCode;
        }
        else if (paramName.equalsIgnoreCase("name"))
        {
            return centerName;
        }
        else if (paramName.equalsIgnoreCase("start"))
        {
            return beginTime;
        }
        else if (paramName.equalsIgnoreCase("end"))
        {
            return endTime;
        }
        else if (paramName.equalsIgnoreCase("reportcode"))
        {
            return reportCode;
        }
        else if (paramName.equalsIgnoreCase("edition"))
        {
            return reportEdition;
        }
        else if (paramName.equalsIgnoreCase("branchcode"))
        {
            return branchCode;
        }
        else if (paramName.equalsIgnoreCase("branchname"))
        {
            return branchName;
        }
        else if (paramName.equalsIgnoreCase("rep_js"))
        {
            try
            {
                SimpleDateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd");
                Date date = datefmt.parse(endTime);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                return String.valueOf(calendar.get(Calendar.YEAR)) + "JS";
            }
            catch (ParseException pex)
            {
                System.err.print(pex.getErrorOffset());
                System.err.println("非法日期：" + endTime);
                return "";
            }
        }
        else if (paramName.equalsIgnoreCase("rep_month"))
        {
            return endTime;
        }
        else if (paramName.equalsIgnoreCase("rep_year"))
        {
            try
            {
                SimpleDateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd");
                Date date = datefmt.parse(endTime);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                return String.valueOf(calendar.get(Calendar.YEAR));
            }
            catch (ParseException pex)
            {
                System.err.print("非法日期：" + beginTime);
                return "";
            }
        }
        else if (paramName.equalsIgnoreCase("ym"))
        {
            try
            {
                SimpleDateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd");
                Date date = datefmt.parse(endTime);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                return String.valueOf(calendar.get(Calendar.YEAR)) + "年" +
                        String.valueOf(calendar.get(Calendar.MONTH)) + "月";
            }
            catch (ParseException pex)
            {
                System.err.print("非法日期：" + endTime);
                return "";
            }
        }
        else
        {
            System.out.println("非法变量:" + paramName);
            return "";
        }
    }
}

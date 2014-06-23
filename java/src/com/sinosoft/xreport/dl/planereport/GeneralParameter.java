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
     * ȡ�ú��㵥λ����
     * @return ���㵥λ����
     */
    public static String getCenterCode()
    {
        return centerCode;
    }

    /**
     * ȡ�ú��㵥λ����
     * @return ���㵥λ����
     */
    public static String getCenterName()
    {
        return centerName;
    }

    /**
     * ȡ�ñ�����㿪ʼʱ��
     * @return ������㿪ʼʱ��
     */
    public static String getBeginTime()
    {
        return beginTime;
    }

    /**
     * ȡ�ñ���������ʱ��
     * @return ����������ʱ��
     */
    public static String getEndTime()
    {
        return endTime;
    }

    /**
     * ȡ�ñ������
     * @return �������
     */
    public static String getReportCode()
    {
        return reportCode;
    }

    /**
     * ȡ�ñ�����
     * @return ������
     */
    public static String getReportEdition()
    {
        return reportEdition;
    }

    /**
     * ȡ�û��㵥λ����
     * @return ���㵥λ����
     */
    public static String getBranchCode()
    {
        return branchCode;
    }

    /**
     * ȡ�û��㵥λ����
     * @return ���㵥λ����
     */
    public static String getBranchName()
    {
        return branchName;
    }

    /**
     * ȡ��ͨ�ò�������
     * @return ͨ�ò�������
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
     * ���ú��㵥λ����
     * @param param ���㵥λ����
     */
    public static void setCenterCode(String param)
    {
        centerCode = param;
    }

    /**
     * ���ú��㵥λ����
     * @param param ���㵥λ����
     */
    public static void setCenterName(String param)
    {
        centerName = param;
    }

    /**
     * ���ñ�����㿪ʼʱ��
     * @param param ������㿪ʼʱ��
     */
    public static void setBeginTime(String param)
    {
        beginTime = param;
    }

    /**
     * ���ñ���������ʱ��
     * @param param ����������ʱ��
     */
    public static void setEndTime(String param)
    {
        endTime = param;
    }

    /**
     * ���ñ������
     * @param param �������
     */
    public static void setReportCode(String param)
    {
        reportCode = param;
    }

    /**
     * ���ñ�����
     * @param param ������
     */
    public static void setReportEdition(String param)
    {
        reportEdition = param;
    }

    /**
     * ���û��㵥λ����
     * @param param ���㵥λ����
     */
    public static void setBranchCode(String param)
    {
        branchCode = param;
    }

    /**
     * ���û��㵥λ����
     * @param param ���㵥λ����
     */
    public static void setBranchName(String param)
    {
        branchName = param;
    }

    /**
     * ����ͨ�ò�������
     * @param gp ͨ�ò�������
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
     * ȡͨ�ñ�����ֵ
     * @param paramName ͨ�ñ�����
     * @return ͨ�ñ�����ֵ
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
                System.err.println("�Ƿ����ڣ�" + endTime);
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
                System.err.print("�Ƿ����ڣ�" + beginTime);
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
                return String.valueOf(calendar.get(Calendar.YEAR)) + "��" +
                        String.valueOf(calendar.get(Calendar.MONTH)) + "��";
            }
            catch (ParseException pex)
            {
                System.err.print("�Ƿ����ڣ�" + endTime);
                return "";
            }
        }
        else
        {
            System.out.println("�Ƿ�����:" + paramName);
            return "";
        }
    }
}

/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.text.DecimalFormat;

/**
 ****************************************************************
 *               Program NAME: �ַ�����������
 *                 programmer: Ouyangsheng (Modify)
 *                Create DATE: 2002.05.21
 *             Create address: Beijing
 *                Modify DATE:
 *             Modify address:
 *****************************************************************
 * ��������������ش�������,�����̬������ת�����ַ��������غ���
 *                  ���ַ��������Ĺ����ࡣ
 *
 *****************************************************************
 */
public class ChgData 
{
    /**
     * ��byteֵת�����ַ���������ֵΪ0ʱ����""�����򷵻���ֵ�ַ���
     * @param byteValue byte
     * @return String ת������ַ���
     */
    public static String chgData(byte byteValue)
    {
        String strReturn = null;
        if (byteValue == 0)
        {
            strReturn = "0";
        }
        else
        {
            strReturn = String.valueOf(byteValue);
        }
        return strReturn;
    }

    /**
     * ��shortֵת�����ַ���������ֵΪ0ʱ����""�����򷵻���ֵ�ַ���
     * @param shortValue short
     * @return String ת������ַ���
     */
    public static String chgData(short shortValue)
    {
        String strReturn = null;
        if (shortValue == 0)
        {
            strReturn = "0";
        }
        else
        {
            strReturn = String.valueOf(shortValue);
        }
        return strReturn;
    }

    /**
     * ��intֵת�����ַ���������ֵΪ0ʱ����""�����򷵻���ֵ�ַ���
     * @param intValue int
     * @return String ת������ַ���
     */
    public static String chgData(int intValue)
    {
        String strReturn = null;
        if (intValue == 0)
        {
            strReturn = "0";
        }
        else
        {
            strReturn = String.valueOf(intValue);
        }
        return strReturn;
    }

    /**
     * ��longֵת�����ַ���������ֵΪ0ʱ����""�����򷵻���ֵ�ַ���
     * @param longValue long
     * @return String ת������ַ���
     */
    public static String chgData(long longValue)
    {
        String strReturn = null;
        if (longValue == 0)
        {
            strReturn = "0";
        }
        else
        {
            strReturn = String.valueOf(longValue);
        }
        return strReturn;
    }

    /**
     * ��floatֵת�����ַ���������ֵΪ0ʱ����""�����򷵻���ֵ�ַ���
     * @param floatValue float
     * @return String ת������ַ���
     */
    public static String chgData(float floatValue)
    {
        String strReturn = null;

        if (floatValue == 0)
        {
            strReturn = "0";
        }
        else
        {
            //��ʽ���������ݣ���ֹ���ص�ʱ����ֿ�ѧ������
            DecimalFormat df = new DecimalFormat("#.###");
//            strReturn = String.valueOf(floatValue);
            strReturn = String.valueOf(df.format(floatValue));
        }
        return strReturn;
    }

    /**
     * ��doubleת�����ַ���������ֵΪ0ʱ����""�����򷵻���ֵ�ַ���
     * @param doubleValue double
     * @return String ת������ַ���
     */
    public static String chgData(double doubleValue)
    {
        String strReturn = null;
        if (doubleValue == 0)
        {
            strReturn = "0";
        }
        else
        {
            //��ʽ���������ݣ���ֹ���ص�ʱ����ֿ�ѧ������
            DecimalFormat df = new DecimalFormat("#.##################");
//            strReturn = String.valueOf(doubleValue);
            strReturn = String.valueOf(df.format(doubleValue));
        }
        return strReturn;
    }

    /**
     * ת����ֵ�ַ��������ַ���������ֵΪ""����Ϊ��ʱ,����ת��Ϊ�ַ���"0"
     * @param strValue String
     * @return String ת������ַ���
     */
    public static String chgNumericStr(String strValue)
    {
        if (strValue == null)
        {
            return "0";
        }
        else if (strValue.trim().equals("") || strValue.length() == 0)
        {
            return "0";
        }
        else
        {
            return strValue;
        }
    }

    /**
     * ת����������ֵ
     * @param strValue String
     * @return String ����������Ӧ����������
     */
    public static String getBooleanDescribe(String strValue)
    {
        String strReturn = strValue;
        if (strValue.equals("Y") || strValue.equals("y"))
        {
            strReturn = "��";
        }
        else if (strValue.equals("N") || strValue.equals("n"))
        {
            strReturn = "��";
        }
        return strReturn;
    }

    /**
     * ���Ժ���
     * @param args String[]
     */
    public static void main(String[] args)
    {
        double a = 12.4445;
        System.out.println(chgData(a));
    }
}

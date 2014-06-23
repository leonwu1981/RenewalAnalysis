/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.lang.reflect.Field;

import com.sinosoft.lis.pubfun.PubFun;

/**
 * <p>Title: Webҵ��ϵͳ</p>
 * <p>Description:��¼һ����������Ҫ����Ϣ </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author YT
 * @version 1.0
 * @date: 2002-06-18
 */
public class CError
{
    public static final String TYPE_NONEERR = "0"; //û�д���
    public static final String TYPE_FORBID = "1"; //��ֹ
    public static final String TYPE_ALLOW = "2"; //����
    public static final String TYPE_NEEDSELECT = "3"; //��Ҫ�ж�
    public static final String TYPE_UNKNOW = "4"; //���ݴ�ͳ
    public static final String SYSTEM = "10"; //����ϵͳ
    public static final String COMMUNICATION = "11"; //ͨѶ
    public static final String SAFETY = "12"; //��ȫ
    public static final String BL_RISK = "2101"; //Ӧ���߼�-������صĴ���
    public static final String BL_FINANCE = "2102"; //Ӧ���߼�-������ش���
    public static final String BL_TB = "2103"; //Ӧ���߼�-Ͷ����صĴ���
    public static final String BL_BQ = "2104"; //Ӧ���߼�-��ȫ��صĴ���
    public static final String BL_CASE = "2105"; //Ӧ���߼�-������صĴ���
    public static final String BL_LIVEGET = "2106"; //Ӧ���߼�-������ȡ��صĴ���
    public static final String BL_AGENT = "2107"; //Ӧ���߼�-ҵ��Ա��صĴ���
    public static final String BL_BANK = "2108"; //Ӧ���߼�-������صĴ���
    public static final String BL_UNKNOW = "2109"; //Ӧ���߼�-��������
    public static final String DB_OPERATE = "22"; //���ݿ����
    public static final String UNKNOW = "23"; //����
    public static final String TYPE_NONE = "0"; //�޴���
    public static final String WS_SERVER_CALL = "02010001"; //��������������˵��ô���
    public static final String WS_TRANS_SOAP = "02010002"; //��������soap�������

    /** �������ؼ������� */
    public String errorType = TYPE_FORBID;

    /** ������� */
    public String errorNo = UNKNOW;

    /** ģ������ */
    public String moduleName;

    /** �������� */
    public String functionName;

    /** �������� */
    public String errorMessage;

    public CError()
    {
    }

    /**
     * ��ô�����Ϣ
     * @param errString String
     */
    public CError(String errString)
    {
        errorMessage = errString;
        System.out.println(errorMessage);
    }

    /**
     * ���ô�����Ϣ����
     * @param errString String
     * @param cModuleName String
     * @param cFunctionName String
     */
    public CError(String errString, String cModuleName, String cFunctionName)
    {
        errorMessage = errString;
        moduleName = cModuleName;
        functionName = cFunctionName;
    }

    /**
     * ����������Ϣ���󣬲����ô�����õ�����Ķ���Ĵ�����ж�����
     * @param o ϣ�����ɸô���Ķ���
     * @param errMessage ������Ϣ
     */
    public static void buildErr(Object o, String errMessage)
    {
        buildErr(o, "", errMessage, TYPE_FORBID, UNKNOW);
    }

    /**
     * ����������Ϣ���󣬲����ô�����õ�����Ķ���Ĵ�����ж�����
     * @param o Object
     * @param errMessage String
     * @param errType String
     * @param errNo String
     */
    public static void buildErr(Object o, String errMessage, String errType,
            String errNo)
    {
        buildErr(o, "", errMessage.trim(), errType, errNo);
    }

    /**
     * ����������Ϣ���󣬲����ô�����õ�����Ķ���Ĵ�����ж�����
     * @param o ϣ�����ɸô���Ķ���
     * @param functionName ����������
     * @param errMessage ������Ϣ
     * @param errType �������ؼ�������
     * @param errNo �������
     */
    public static void buildErr(Object o, String functionName, String errMessage, String errType
            , String errNo)
    {
        try
        {
            CError tError = new CError();
            tError.moduleName = PubFun.getClassFileName(o);
            tError.functionName = functionName;
            tError.errorMessage = errMessage.trim();
            tError.errorType = errType;
            tError.errorNo = errNo;

            Class c = o.getClass();
            Field f = c.getField("mErrors");
            ((CErrors) f.get(o)).addOneError(tError);

            System.out.print("�ڣ�");
            System.out.print(tError.moduleName);
            System.out.print("�����׳����´���");
            System.out.println(errMessage);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * ����������Ϣ���󣬲����ô�����õ�����Ķ���Ĵ�����ж����У�ͬʱת����������Ķ����б�
     * @param o Object
     * @param errMessage String
     * @param e CErrors
     */
    public static void buildErr(Object o, String errMessage, CErrors e)
    {
        buildErr(o, "", errMessage.trim(), e, TYPE_FORBID, UNKNOW);
    }

    /**
     * ����������Ϣ���󣬲����ô�����õ�����Ķ���Ĵ�����ж����У�ͬʱת����������Ķ����б�
     * @param o Object
     * @param errMessage String
     * @param e CErrors
     * @param errType String
     * @param errNo String
     */
    public static void buildErr(Object o, String errMessage, CErrors e, String errType
            , String errNo)
    {
        buildErr(o, "", errMessage, e, errType, errNo);
    }

    /**
     * ����������Ϣ���󣬲����ô�����õ�����Ķ���Ĵ�����ж����У�ͬʱת����������Ķ����б�
     * @param o Object
     * @param functionName String
     * @param errMessage String
     * @param e CErrors
     * @param errType String
     * @param errNo String
     */
    public static void buildErr(Object o, String functionName, String errMessage, CErrors e
            , String errType, String errNo)
    {
        try
        {
            Class c = o.getClass();
            Field f = c.getField("mErrors");
            ((CErrors) f.get(o)).copyAllErrors(e);

            buildErr(o, functionName, errMessage, errType, errNo);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * ���Ժ���
     * @param arg String[]
     */
    public static void main(String[] arg)
    {
//        CError e = new CError();
    }
}

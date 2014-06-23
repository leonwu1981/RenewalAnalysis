/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

/**
 ****************************************************************
 *               Program NAME: ϵͳ������
 *                 programmer: Ouyangsheng
 *                Create DATE: 2002.04.17
 *             Create address: Beijing
 *                Modify DATE:
 *             Modify address:
 *****************************************************************
 *
 *                    ����ϵͳ�еĳ�����
 *
 *****************************************************************
 */
public class SysConst
{
    /* ϵͳ��Ϣ */
    public static final int FAILURE = -1;
    public static final int SUCCESS = 0;
    public static final int NOTFOUND = 100;

    /* ϵͳ���� */
    public static final String EMPTY = null;
    public static final boolean CHANGECHARSET = false; // Unicode to GBK

    /* ��Ϣ�ָ��� */
    public static final String PACKAGESPILTER = "|";
    public static final String RECORDSPLITER = "^";
    public static final String ENDOFPARAMETER = "^";
    public static final String EQUAL = "=";
    public static final String CONTAIN = "*";

    /* ��ѯ��ʾ���� */
    public static final int MAXSCREENLINES = 10; //ÿһҳ�����ʾ������
    public static final int MAXMEMORYPAGES = 10; //�ڴ��д洢������ҳ��

    /* ������Ϣ */
    public static final String ZERONO = "00000000000000000000"; //����û�к�����ֶε�Ĭ��ֵ
    public static final String POOLINFO = "poolname";
    public static final String PARAMETERINFO = "parameterbuf";
    public static final String POOLTYPE = "pooltype";
    public static final String MAXSIZE = "maxsize";
    public static final String MINSIZE = "minsize";

    public static final String USERLOGPATH = "userlogpath";
    public static final String SYSLOGPATH = "syslogpath";

    public static final String COMP = "comp";
    public static final String ENCRYPT = "encrypt";
    public static final String MACFLAG = "macflag";
    public static final String SIGNFLAG = "signflag";
    public static final String SRC = "src";
    public static final String SND = "snd";
    public static final String RCV = "rcv";
    public static final String PRIOR = "prior";

    /* ���Ѽ�� */
    public static final String PayIntvMonth = "�½�";
    public static final String PayIntvQuarter = "����";
    public static final String PayIntvHalfYear = "���꽻";
    public static final String PayIntvYear = "�꽻";

    /*����������ͬ��*/
    public static final int Number = 5000;

    /*����ϵͳ���չ�˾����*/
    public static final String CorpCode = "000095";

    /**
     * һ�������sys
     * ��PubFun��AccountManage�м�����Ϣʱ�õ�
     */
    public static final String DAYSOFYEAR = "365";

    /*ϵͳ����������ͣ�SysMaxNoʵ����ĺ�׺����������ʵ����ΪSysMaxNoMinSheng*/
//    public static final String MAXNOTYPE = "ZhongYing";
    public static final String MAXNOTYPE = "ZhongYi";

    /*���ݿ����ͣ�DB2��ORACLE��*/
//    public static final String DBTYPE = "DB2";
    public static final String DBTYPE = "ORACLE";
//    public static final String DBTYPE = "SYBASE";

    //���������ݲ�ѯʱ��ʹ�õĻ�������С
    public static final int FETCHCOUNT = 5000;

    //�������γ���ɸѡ������ɸѡ���ͻ����еĸ������κ͸������θ���
    public static String GETDUTYGET = "GetDutyGetImpl";

    //�Զ�����ƥ��
    public static String AUTOCHOOSEDUTY = "AutoClaimDutyMapImpl";

    //��ӡģ����·��
    public static String TEMPLATE = "yihetemplate";
    
    //zhangjinquan 2008-08-26 ������ȫ����������
    public static String BQ_LOCK_TYPE = "BQ";
    //huangkai 2008-09-08 �����������������
    public static String CW_LOCK_TYPE = "CW";
    //hanming 2009-02-18 �����������������
    public static String LP_LOCK_TYPE = "LP";
    //guoly 2009-03-23 ������Լ����������
    public static String QY_LOCK_TYPE = "QY";
    //fengyan 2010-12-22 ���ڽ��� 
    public static String DJ_LOCK_TYPE = "DJ";
    //hanming 2011-04-26 �ʻ����������� 
    public static String ZH_LOCK_TYPE = "ZH";
    //hanming 2011-06-24 �����˲��������� 
    public static String BL_LOCK_TYPE = "BL";
    //WuKai 2011-07-22 ֧������
    public static String ZF_LOCK_TYPE = "ZF";
    //By Fang 2011-10-24 ���ڴ��ռ���
    public static String CS_LOCK_TYPE = "CS";
    //zhangjinquan 2008-09-06 ������ȡui·����������
    public static String UI_Path = "uicontextpath";
}

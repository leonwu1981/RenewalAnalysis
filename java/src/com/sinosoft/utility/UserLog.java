/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/*****************************************************************
 *               Program NAME: д��־��
 *                 programmer: hubo
 *                Create DATE: 2002.04.17
 *             Create address: Beijing
 *                Modify DATE: 2002.09.25
 *             Modify address: Beijing
 *****************************************************************
 *                       ʹ��˵��
 *���Ŀ¼�б������SysConst.class��StrTool.class��ChgData.class
 *����־����ز���������Ҫ��Appconfig.properties�ļ���������ǰĿ¼"."�£�
 *����ͨ��getDefaultPath()�෽����ȡ��ǰĿ¼·��ȫ�ƣ�
 *�������ļ�Appconfig.properties������Ӧ����־��ϵͳ��־�Ĵ��·��
 *�����println()����д��Ӧ�ó�����־�ļ���printExceptionд��ϵͳ��־�ļ�
 *����ʹ�ýӿڸ�ΪaddUserLog(String strLog)��addSysLog(String strLog)����
 *


public
****************************************************************---------------------+*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************
*/
       class UserLog
{
   private static String strUserLogFileName = ConfigInfo.GetValuebyName(
           SysConst.USERLOGPATH);
   //Ӧ�ó�����־
   private static String strSysLogFileName = ConfigInfo.GetValuebyName(SysConst.
           SYSLOGPATH);
   //ϵͳ��־

   /****************************************
    *            ���췽��(default)
    ****************************************
    */
   public UserLog()
   {
       //��Ҫ������־�ļ����ĳ�ʼ����
       //��ʼ��Ӧ�ó�����־�ļ����ƣ�һ��ʹ��һ�������ڵ����ļ�
       String userLogExtendsion = strUserLogFileName.substring(
               strUserLogFileName.indexOf("."));
       String strDate = StrTool.getDate();
       String strDateYear = strDate.substring(0, strDate.indexOf("/"));
       strDate = strDate.substring(strDate.indexOf("/") + 1);
       String strDateMonth = strDate.substring(0, strDate.indexOf("/"));
       strDate = strDate.substring(strDate.indexOf("/") + 1);
       String strDateDay = strDate;
       //System.out.println(strDateYear + strDateMonth + strDateDay);

       //��ҳˢ��ʱ�������л��ж��������Ϊ�˷�ֹ�ļ������ظ���׷�����ڣ������ж�
       if ((strUserLogFileName.indexOf("(") == -1) &&
           (strUserLogFileName.indexOf(")") == -1))
       {
           strUserLogFileName = strUserLogFileName.substring(0,
                   strUserLogFileName.indexOf("."))
                                + "("
                                + strDateYear
                                + "_"
                                + strDateMonth
                                + "_"
                                + strDateDay
                                + ")"
                                + userLogExtendsion;
       }

       //��ʼ��ϵͳ��־�ļ����ƣ�һ��ʹ��һ�������ڵ����ļ�
       String sysLogExtendsion = strSysLogFileName.substring(strSysLogFileName.
               indexOf("."));
       strDate = StrTool.getDate();
       strDateYear = strDate.substring(0, strDate.indexOf("/"));
       strDate = strDate.substring(strDate.indexOf("/") + 1);
       strDateMonth = strDate.substring(0, strDate.indexOf("/"));
       strDate = strDate.substring(strDate.indexOf("/") + 1);
       strDateDay = strDate;
       //System.out.println(strDateYear + strDateMonth + strDateDay);

       //��ҳˢ��ʱ�������л��ж��������Ϊ�˷�ֹ�ļ������ظ���׷�����ڣ������ж�
       if ((strSysLogFileName.indexOf("(") == -1) &&
           (strSysLogFileName.indexOf(")") == -1))
       {
           strSysLogFileName = strSysLogFileName.substring(0,
                   strSysLogFileName.indexOf("."))
                               + "("
                               + strDateYear
                               + "_"
                               + strDateMonth
                               + "_"
                               + strDateDay
                               + ")"
                               + sysLogExtendsion;
       }

   }

   /****************************************
    *              ���췽��
    *   ������ALLLogPath :Ӧ�ó�����־��
    *                     ϵͳ��Ϣ��־ʹ
    *                     ����ͬ·��
    ****************************************
    */
   public UserLog(String AllLogPath)
   {
       strUserLogFileName = AllLogPath;
       strSysLogFileName = AllLogPath;
   }

   /****************************************
    *              ���췽��
    *   ������UserLogPath :Ӧ�ó�����־·��
    *         SysLogPath  :ϵͳ��Ϣ��־·��
    ****************************************
    */
   public UserLog(String UserLogPath, String SysLogPath)
   {
       strUserLogFileName = UserLogPath;
       strSysLogFileName = SysLogPath;
   }

   /****************************************
    *   ���ɴ�����Ϣ����ʱ�ļ�������html��
    *    ������ strFileName:�ļ�����,
    *              strValue:д����־������
    *  ����ֵ:  0���ɹ���-1:ʧ��
    ****************************************
    */
   public static int generateTempFile(String strFileName, String strValue)
   {
       String strFileValue = strValue;

       try
       {
           RandomAccessFile in = new RandomAccessFile(strFileName, "rw");
           in.write(strFileValue.getBytes());
           in.close();
       }
       catch (IOException exception)
       {
           System.out.print(exception.toString());
           return SysConst.FAILURE;
       }
       return SysConst.SUCCESS;
   }


   /****************************************
    *       д��Ӧ�ó�����־
    *   ������ strValue:д����־������
    * ����ֵ:  0���ɹ���-1:ʧ��
    ****************************************
    */
   private static int print(String strValue)
   {
       try
       {
           RandomAccessFile in = new RandomAccessFile(strUserLogFileName, "rw");
           in.seek(in.length());
           in.write(strValue.getBytes());
           in.write(13);
           in.write(10); //Minim add for �س����У�
           in.close();
       }
       catch (IOException exception)
       {
           System.out.print(exception.toString());
           return SysConst.FAILURE;
       }
       return SysConst.SUCCESS;
   }

   /****************************************
    *       д��Ӧ�ó�����־
    *   ������ intValue:д����־������
    * ����ֵ:  0���ɹ���-1:ʧ��
    ****************************************
    */
   public static int print(int intValue)
   {
       return print("" + intValue);
   }

   /****************************************
    *       д��Ӧ�ó�����־
    *   ������ doubleValue:д����־������
    * ����ֵ:  0���ɹ���-1:ʧ��
    ****************************************
    */
   public static int print(double doubleValue)
   {
       return print("" + doubleValue);
   }

   /****************************************
    *       д��Ӧ�ó�����־
    *   ������ object:д����־������
    * ����ֵ:  0���ɹ���-1:ʧ��
    ****************************************
    */
   public static int print(Object object)
   {
       return print("" + object.toString());
   }

   /****************************************
    *       ����д��Ӧ�ó�����־
    *   ������ strValue:д����־������
    * ����ֵ:  0���ɹ���-1:ʧ��
    ****************************************
    */
   public static int println(String strValue)
   {
       String finalstrValue = null; // д�봮

       finalstrValue = "<" + StrTool.getDate() + " " + StrTool.getTime() + ">" +
                       strValue + "\n";
       return print(finalstrValue);
   }

   /****************************************
    *       ����д��Ӧ�ó�����־
    *   ������ intValue:д����־������
    * ����ֵ:  0���ɹ���-1:ʧ��
    ****************************************
    */
   public static int println(int intValue)
   {
       String finalstrValue = null; // д�봮

       finalstrValue = "<" + StrTool.getDate() + " " + StrTool.getTime() + ">" +
                       intValue + "\n";
       return print(finalstrValue);
   }

   /****************************************
    *       ����д��Ӧ�ó�����־
    *   ������ doubleValue:д����־������
    * ����ֵ:  0���ɹ���-1:ʧ��
    ****************************************
    */
   public static int println(double doubleValue)
   {
       String finalstrValue = null; // д�봮

       finalstrValue = "<" + StrTool.getDate() + " " + StrTool.getTime() + ">" +
                       doubleValue + "\n";
       return print(finalstrValue);
   }

   /****************************************
    *       ����д��Ӧ�ó�����־
    *   ������ object:д����־������
    * ����ֵ:  0���ɹ���-1:ʧ��
    ****************************************
    */
   public static int println(Object object)
   {
       String finalstrValue = null; // д�봮

       finalstrValue = "<" + StrTool.getDate() + " " + StrTool.getTime() + ">" +
                       object.toString() + "\n";
       return print(finalstrValue);
   }

   /****************************************
    *       ����д��ϵͳ��־
    *   ������ strValue:д����־������
    * ����ֵ:  0���ɹ���-1:ʧ��
    ****************************************
    */
   private static int printException(String strValue)
   {
       String strLog = "<" + StrTool.getDate() + " " + StrTool.getTime() + ">" +
                       strValue + "\n";
       try
       {
           RandomAccessFile in = new RandomAccessFile(strSysLogFileName, "rw");
           in.seek(in.length());
           in.write(strLog.getBytes());
           in.write(13);
           in.write(10); //Minim add for �س����У�
           in.close();
       }
       catch (IOException exception)
       {
           System.out.print(exception.toString());
           return SysConst.FAILURE;
       }
       return SysConst.SUCCESS;
   }


   /****************************************
    *       ����д��ϵͳ��־
    *   ������ intValue:д����־������
    * ����ֵ:  0���ɹ���-1:ʧ��
    ****************************************
    */
   public static int printException(int intValue)
   {
       return printException("" + intValue);
   }

   /****************************************
    *       ����д��ϵͳ��־
    *   ������ doubleValue:д����־������
    * ����ֵ:  0���ɹ���-1:ʧ��
    ****************************************
    */
   public static int printException(double doubleValue)
   {
       return printException("" + doubleValue);
   }

   /****************************************
    *       ����д��ϵͳ��־
    *   ������ object:д����־������
    * ����ֵ:  0���ɹ���-1:ʧ��
    ****************************************
    */
   public static int printException(Object object)
   {
       return printException("" + object.toString());
   }

   /****************************************
    *       д��Ӧ����־�ӿ�
    *   ������ strLog:д����־������
    * ����ֵ:  0���ɹ���-1:ʧ��
    ****************************************
    */
   public static int addUserLog(String strLog)
   {
       return println(strLog);
   }

   /****************************************
    *       д��ϵͳ��־�ӿ�
    *   ������ strLog:д����־������
    * ����ֵ:  0���ɹ���-1:ʧ��
    ****************************************
    */
   public static int addSysLog(String strLog)
   {
       return printException(strLog);
   }

   public static String getDefaultPath()
   {
       File defaultPath = new File(".");
       return defaultPath.getAbsolutePath();
   }

   /**********************************************************************
    *                               �����õĶ�����
    **********************************************************************
    */
   private static int readTest()
   {
       try
       {
           RandomAccessFile in = new RandomAccessFile(strUserLogFileName, "rw");
           // in.seek(in.length());
           // in.write(strValue.getBytes());
           int tRead = 0;
           while (tRead != -1)
           {
               tRead = in.read();
               System.out.println(tRead);

           }
           in.close();
       }
       catch (IOException exception)
       {
           System.out.print(exception.toString());
           return SysConst.FAILURE;
       }
       return SysConst.SUCCESS;
   }


   /**********************************************************************
    *                               main
    **********************************************************************
    */
   public static void main(String args[])
   {
       //you can add test code at here;
       System.out.println("Start");
       UserLog testUserLog = new UserLog();
       //UserLog("D:/Minim_work/testUserLog.txt");
       //testUserLog.addUserLog("this a user log test!");
       //testUserLog.addSysLog("this a test!");
       //System.out.println( FileSystem.getFileSystem());
       System.out.println("File save successful");
       System.out.println(getDefaultPath());

   }
}
/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import com.sinosoft.lis.db.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class CodeJudge
{

    public CodeJudge()
    {
    }

    /**
     * �����жϺ����������жϺ�������
     * <p><b>Ӧ�����ո���ҵ������</b></p>
     * @param strCode ����
     * @return �����а��������ͣ�ʧ�ܷ��ء�00��
     */
    public static String getCodeType(String strCode)
    {
        if ((strCode == null) || (strCode.equals("")))
        {
            return "00";
        }
        else
        {
            try
            {
                //�ڡ���Ŀ�淶_Լ�����ġ��¾ɺ������.xls���й涨�������[1,2]λ��Ϊ�������ͱ�־
                return strCode.substring(0, 2);
            }
            catch (Exception e)
            {
                return "00";
            }
        }
    }

    /**
     * �����жϺ����������жϺ������ͣ��ж��Ƿ��Ǹ���������
     * @param strCode ����
     * @param strType ��������
     * @return Booleanֵ��true-������ͬ��false-��ͬ
     */
    public static boolean judgeCodeType(String strCode, String strType)
    {
        if ((strCode == null) || (strCode.equals("")) || (strType == null) ||
                (strType.equals("")))
        {
            return false;
        }
        else
        {
            try
            {
                return (getCodeType(strCode).compareTo(strType) == 0);
            }
            catch (Exception e)
            {
                return false;
            }
        }
    }

    /**
     * ���ݿ�ʼλ�ͳ��ȣ���ȡ�Ӵ������ڻ�ȡ��־λ
     * @param strCode ����
     * @param strStart ��ʼλ
     * @param strLength ����
     * @return �����а��������ͣ�ʧ�ܷ��ء�00��
     */
    public static String getCodeType(String strCode, String strStart, String strLength)
    {
        if ((strCode == null) || (strCode.equals("")))
        {
            return "00";
        }
        else
        {
            try
            {
                return strCode.substring(Integer.parseInt(strStart)
                        , Integer.parseInt(strStart) + Integer.parseInt(strLength));
            }
            catch (Exception e)
            {
                return "00";
            }
        }
    }

    /**
     * �ж��Ÿ�����Ϣ
     * @param cInputNo ����
     * @param cFlag ������
     * @return �����а��������ͣ�ʧ�ܷ��ء�00��
     */
    public static String getCodeType(String cInputNo, String cFlag)
    {
        if ((cInputNo == null) || (cInputNo.equals("")))
        {
            return "00";
        }
        else
        {
            try
            {
                LCContDB tLCContDB = new LCContDB();
                tLCContDB.setContNo(cInputNo);
                if (tLCContDB.getInfo())
                {
                    return "01";
                }
                else
                {
                    LCGrpContDB tLCGrpContDB = new LCGrpContDB();
                    tLCGrpContDB.setGrpContNo(cInputNo);
                    if (tLCGrpContDB.getInfo())
                    {
                        return "02";
                    }
                    else
                    {
                        return "00";
                    }
                }
            }
            catch (Exception e)
            {
                return "00";
            }
        }
    }

    /**
     * ���Ժ���
     * @param args String[]
     */
    public static void main(String[] args)
    {
//        CodeJudge codeJudge1 = new CodeJudge();
//        System.out.println(getCodeType("abcdefg"));
//        System.out.println(judgeCodeType("abcdef", "11"));
//        System.out.println(getCodeType("abcdefghijk11asdfasdf"));
//        System.out.println(judgeCodeType("abcdefghijk11asdfasdf", "11"));
//        System.out.println(getCodeType("ab12cdefghijk11asdfasdf", "2", "2"));
    }
}

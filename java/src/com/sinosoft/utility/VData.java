/**
 * Copyright (c) 2002 sinosoft  Co. Ltd.
 * All right reserved.
 */
package com.sinosoft.utility;

import java.util.Vector;


/**
 * <p>Title: Webҵ��ϵͳ</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft</p>
 * @author YT
 * @version 1.0
 * @date: 2002-06-18
 */
public class VData extends Vector
{
    //���洫������Ҫ������
    private static final long serialVersionUID = 1L;

    public VData()
    {}

    /**
     * �����ݽ��д�����ַ���
     * @return String
     */
    public String encode()
    {
        String strReturn = "";
        return strReturn;
    }

    /**
     * ����������ƣ���VData���ҳ���cStartPosΪ��ʼλ�õĵ�һ�����ƺ�cObjectName��ͬ�Ķ���
     * @param cObjectName String ��Ҫ���ҵĵ�һ��������
     * @param cStartPos int ���ҵĿ�ʼλ�ã��������λ�ã�
     * @return Object
     * ����ҵ��������ҵ��Ķ������û���ҵ�������null
     */
    public Object getObjectByObjectName(String cObjectName, int cStartPos)
    {
        int i = 0;
        int iMax = 0;
        String tStr1 = "";
        String tStr2 = "";
        Object tReturn = null;
        if (cStartPos < 0)
        {
            cStartPos = 0;
        }
        iMax = this.size();
        try
        {
            for (i = cStartPos; i < iMax; i++)
            {
                if (this.get(i) == null)
                {
                    continue;
                }
                tStr1 = this.get(i).getClass().getName().toUpperCase();
                tStr2 = cObjectName.toUpperCase();
                if (tStr1.equals(tStr2) || this.getLastWord(tStr1, ".").equals(tStr2))
                {
                    tReturn = this.get(i);
                    break;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.out.println("��ȡ�����������");
            tReturn = null;
        }
        return tReturn;
    }

    /**
     * ����������ƣ���VData���ҳ���cStartPosΪ��ʼλ�õĵ�һ�����ƺ�cObjectName��ͬ�Ķ���
     * @param cObjectName String ��Ҫ���ҵĵ�һ��������
     * @param cStartPos int ���ҵĿ�ʼλ�ã��������λ�ã�
     * @param cPos int ���ҵڼ����ö���
     * @return Object
     * ����ҵ��������ҵ��Ķ������û���ҵ�������null
     */
    public Object getObjectByObjectName(String cObjectName, int cStartPos, int cPos)
    {
        int i = 0;
        int j = 0;
        int iMax = 0;
        String tStr1 = "";
        String tStr2 = "";
        Object tReturn = null;
        if (cStartPos < 0)
        {
            cStartPos = 0;
        }
        iMax = this.size();
        try
        {
            for (i = cStartPos; i < iMax; i++)
            {
                if (this.get(i) == null)
                {
                    continue;
                }
                tStr1 = this.get(i).getClass().getName().toUpperCase();
                tStr2 = cObjectName.toUpperCase();
                if (tStr1.equals(tStr2) || this.getLastWord(tStr1, ".").equals(tStr2))
                {
                    j++;
                    if (j < cPos )
                    {
                        continue;
                    }
                    tReturn = this.get(i);
                    break;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.out.println("��ȡ�����������");
            tReturn = null;
        }
        return tReturn;
    }

    /**
     * �õ��ַ����е����һ����splitChar�ָ�ĵ���
     * @param cStr String
     * @param splitStr String
     * @return String
     */
    public String getLastWord(String cStr, String splitStr)
    {
        String tReturn;
        int tIndex = -1;
        int tIndexOld = -1;
        tReturn = cStr;
        try
        {
            while (true)
            {
                tIndex = tReturn.indexOf(splitStr, tIndex + 1);
                if (tIndex > 0)
                {
                    tIndexOld = tIndex;
                }
                else
                {
                    break;
                }
            }
            if (tIndexOld > 0)
            {
                tReturn = cStr.substring(tIndexOld + 1, cStr.length());
            }
            else
            {
                tReturn = cStr;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.out.println("��ȡ�����������");
            tReturn = "";
        }
        return tReturn;
    }

    /**
     * ���������������VData���ҳ���cIndexλ�õĶ���
     * @param cIndex int ��Ҫ�õ��Ķ���λ��
     * ���������ҵ��������ҵ��Ķ������û���ҵ�������null
     * @return Object
     */
    public Object getObject(int cIndex)
    {
        Object tReturn = null;
        if (cIndex < 0)
        {
            cIndex = 0;
        }
        try
        {
            tReturn = this.get(cIndex);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.out.println("��ȡ�����������");
            tReturn = null;
        }
        return tReturn;
    }
}

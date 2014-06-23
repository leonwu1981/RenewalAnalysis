package com.sinosoft.lis.pubfun;

import java.util.*;
import com.sinosoft.utility.*;

public class CommonCheck
{
    /** �������� */
    public CErrors mErrors = new CErrors();
    /**
     * У��Ԫ���Ƿ�ǿ�
     * @param name String   Ԫ���������ڴ�ӡ������Ϣ
     * @param ckobj Object  ��У���Ԫ��
     * @return boolean  �ǿ�--true  ��--false
     */
    public boolean checknoempty(String name, Object ckobj)
    {
        if (ckobj == null || ckobj.toString().equals(""))
        {
            mErrors.addOneError("��������: " + name + " �������");
            return false;
        }
        return true;
    }

    /**
     * У��Ԫ���Ƿ����ظ�
     * @param name String    Ԫ�����ƣ����ڴ�ӡ������Ϣ
     * @param ckobj Object   ��У��Ԫ��
     * @param source Vector  �뱻У��Ԫ�ضԱȵ�Ԫ�ؼ���
     * @return boolean  �ظ�--false  ���ظ�--true
     */
    public boolean checksingle(String name, Object ckobj, Vector source)
    {
        if (source == null)
        {
            return true;
        }
        for (int i = 0; i < source.size(); i++)
        {
            if (ckobj.equals(source.elementAt(i)))
            {
                mErrors.addOneError("��������: " + name + " �����ظ�ֵ");
                return false;
            }
        }
        return true;
    }

    /**
     * У��Ԫ���Ƿ�����֮ƥ���ֵ
     * @param name String   Ԫ�����ƣ����ڴ�ӡ������Ϣ
     * @param ckobj Object  ��У��Ԫ��
     * @param source Vector �뱻У��Ԫ�ضԱȵ�Ԫ�ؼ���
     * @return boolean    ��--true û��--false
     */
    public boolean checkmatch(String name, Object ckobj, Vector source)
    {
        if (source == null)
        {
            mErrors.addOneError("��������: " + name + " ������ƥ��ֵ");
            return false;
        }
        for (int i = 0; i < source.size(); i++)
        {
            if (ckobj.equals(source.elementAt(i)))
            {
                return true;
            }
        }
        mErrors.addOneError("��������: " + name + " ������ƥ��ֵ");
        return false;
    }

}
